package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminPiarlinyBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class Piarliny : BaseActivity(), View.OnClickListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminPiarlinyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val piarliny = ArrayList<ArrayList<String>>()
    private var edit = -1

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onBack() {
        if (binding.addPiarliny.visibility == View.VISIBLE) {
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
        } else {
            super.onBack()
        }
    }

    private fun onDialogEditClick(position: Int) {
        edit = position
        binding.addPiarliny.setText(piarliny[edit][1])
        binding.addPiarliny.setSelection(piarliny[edit][1].length)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, position + 1)
        binding.listView.visibility = View.GONE
        binding.addPiarliny.visibility = View.VISIBLE
        binding.linearLayout2.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        piarliny.removeAt(position)
        val gson = Gson()
        val resultArray = ArrayList<ArrayList<String>>()
        for (i in 0 until piarliny.size) {
            val resultArray2 = ArrayList<String>()
            resultArray2.add(piarliny[i][0])
            resultArray2.add(piarliny[i][1])
            resultArray.add(resultArray2)
        }
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
        sendPostRequest(gson.toJson(resultArray, type))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminPiarlinyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)

        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            try {
                val localFile = File("$filesDir/cache/cache.txt")
                Malitounik.referens.child("/chytanne/piarliny.json").getFile(localFile).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val jsonFile = localFile.readText()
                        val gson = Gson()
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                        piarliny.addAll(gson.fromJson(jsonFile, type))
                        binding.listView.adapter = PiarlinyListAdaprer(this@Piarliny)
                    } else {
                        MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                    invalidateOptionsMenu()
                    binding.progressBar2.visibility = View.GONE
                    if (intent.extras != null) {
                        val position = intent.extras?.getInt("position", 0) ?: 0
                        onDialogEditClick(position)
                    }
                }.await()
            } catch (e: Throwable) {
                MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
            }
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            var text = piarliny[position][1]
            if (text.length > 50) {
                text = text.substring(0, 50)
                text = "$text..."
            }
            val dialogDelite = DialogDelite.getInstance(position, text, false)
            dialogDelite.show(supportFragmentManager, "DialogDelite")
            return@setOnItemLongClickListener true
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            onDialogEditClick(position)
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onPrepareMenu(menu: Menu) {
        val plus = menu.findItem(R.id.action_plus)
        val save = menu.findItem(R.id.action_save)
        if (piarliny.isNotEmpty()) {
            if (binding.addPiarliny.visibility == View.VISIBLE) {
                plus.isVisible = false
                save.isVisible = true
            } else {
                plus.isVisible = true
                save.isVisible = false
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piarliny, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_save) {
            val text = binding.addPiarliny.text.toString().trim()
            if (text != "") {
                var error = false
                if (edit != -1) {
                    piarliny[edit][0] = "0"
                    piarliny[edit][1] = text
                } else {
                    val sub = text.substring(0, 30)
                    for (i in piarliny.indices) {
                        val sub2 = piarliny[i][1].substring(0, 30)
                        if (sub2 == sub) {
                            error = true
                        }
                    }
                    if (!error) {
                        val list = ArrayList<String>()
                        list.add("0")
                        list.add(text)
                        piarliny.add(list)
                    }
                }
                if (!error) {
                    val gson = Gson()
                    val resultArray = ArrayList<ArrayList<String>>()
                    for (i in 0 until piarliny.size) {
                        val resultArray2 = ArrayList<String>()
                        resultArray2.add(piarliny[i][0])
                        resultArray2.add(piarliny[i][1])
                        resultArray.add(resultArray2)
                    }
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    sendPostRequest(gson.toJson(resultArray, type))
                } else {
                    MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.piarlina_error))
                }
            }
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_plus) {
            edit = -1
            binding.listView.visibility = View.GONE
            binding.addPiarliny.visibility = View.VISIBLE
            binding.linearLayout2.visibility = View.VISIBLE
            binding.addPiarliny.text?.clear()
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.add_piarliny)
            invalidateOptionsMenu()
            return true
        }
        return false
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_bold) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 29)
        }
        if (id == R.id.action_p) {
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<p>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 3)
        }
    }

    private fun sendPostRequest(piarliny: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = File("$filesDir/cache/cache.txt")
                    localFile.writer().use {
                        it.write(piarliny)
                    }
                    Malitounik.referens.child("/chytanne/piarliny.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.save))
                            binding.addPiarliny.text?.clear()
                            edit = -1
                        } else {
                            MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                val adapter = binding.listView.adapter as PiarlinyListAdaprer
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    private inner class PiarlinyListAdaprer(context: Activity) : ArrayAdapter<ArrayList<String>>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, piarliny) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            var text = MainActivity.fromHtml(piarliny[position][1]).toString()
            if (text.length > 50) {
                text = text.substring(0, 50)
                text = "$text..."
            }
            viewHolder.text.text = text
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)
}