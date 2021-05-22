package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.admin.databinding.AdminPiarlinyBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class Piarliny : AppCompatActivity(), View.OnClickListener, DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminPiarlinyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val piarliny = ArrayList<String>()
    private var timerCount = 0
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var edit = -1

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startTimer() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (urlJob?.isActive == true && timerCount == 6) {
                    urlJob?.cancel()
                    stopTimer()
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.bad_internet), Toast.LENGTH_LONG)
                        binding.progressBar2.visibility = View.GONE
                    }
                }
                timerCount++
            }
        }
        timer = Timer()
        timer.schedule(timerTask, 0, 5000)
    }

    private fun stopTimer() {
        timer.cancel()
        timerTask = null
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onDialogEditClick(position: Int) {
        edit = position
        binding.ok.text = getString(by.carkva_gazeta.malitounik.R.string.save_sabytie)
        binding.addPiarliny.setText(piarliny[edit])
        binding.addPiarliny.setSelection(piarliny[edit].length)
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialogDelite = DialogDelite.getInstance(position, name)
        dialogDelite.show(supportFragmentManager, "DialogDelite")
    }

    override fun fileDelite(position: Int) {
        piarliny.removeAt(position)
        val gson = Gson()
        sendPostRequest(gson.toJson(piarliny))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminPiarlinyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.ok.setOnClickListener(this)
        binding.cansel.setOnClickListener(this)

        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            startTimer()
            withContext(Dispatchers.IO) {
                var responseCodeS: Int
                val mURL = URL("https://carkva-gazeta.by/chytanne/piarliny.json")
                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    responseCodeS = responseCode
                }
                if (responseCodeS == 200) {
                    val builder = mURL.readText()
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<String>>() {}.type
                    piarliny.addAll(gson.fromJson(builder, type))
                }
            }
            binding.listView.adapter = PiarlinyListAdaprer(this@Piarliny)
            binding.progressBar2.visibility = View.GONE
            stopTimer()
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            var text = piarliny[position]
            if (text.length > 30) {
                text = text.substring(0, 30)
                text = "$text..."
            }
            val dialog = DialogPiarlinyContextMenu.getInstance(position, text)
            dialog.show(supportFragmentManager, "DialogPiarlinyContextMenu")
            return@setOnItemLongClickListener true
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
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
            }
        }
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

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.cansel) {
            binding.addPiarliny.setText("")
        }
        if (id == R.id.ok) {
            val text = binding.addPiarliny.text.toString().trim()
            if (text != "") {
                if (edit != -1) piarliny[edit] = text
                else piarliny.add(0, text)
                val gson = Gson()
                sendPostRequest(gson.toJson(piarliny))
            }
        }
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
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode("5", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("piarliny", "UTF-8") + "=" + URLEncoder.encode(piarliny, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/android.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        responseCodeS = responseCode
                    }
                }
                if (responseCodeS == 200) {
                    binding.addPiarliny.setText("")
                    binding.ok.text = getString(by.carkva_gazeta.malitounik.R.string.add_piarliny)
                    edit = -1
                    MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                val adapter = binding.listView.adapter as PiarlinyListAdaprer
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private inner class PiarlinyListAdaprer(context: Activity) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, piarliny) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = MainActivity.fromHtml(piarliny[position])
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)
}