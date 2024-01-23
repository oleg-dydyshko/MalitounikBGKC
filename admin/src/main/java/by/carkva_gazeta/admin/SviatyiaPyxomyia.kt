package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminSviatyBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class SviatyiaPyxomyia : BaseActivity(), View.OnClickListener, DialogEditImage.DialogEditImageListener {
    private lateinit var binding: AdminSviatyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val sviaty = ArrayList<SviatyData>()
    private val arrayList = ArrayList<ArrayList<String>>()
    private var edittext: AppCompatEditText? = null

    override fun imageFileEdit(bitmap: Bitmap?, opisanie: String) {
        fileUpload(bitmap, opisanie)
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminSviatyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sviaty.add(SviatyData(-1, 0, "Праведнага Язэпа Абручніка, Давіда цара і Якуба, брата Гасподняга"))
        sviaty.add(SviatyData(-1, 1, "Вялеб. Касьяна Рымляніна"))
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.sviaty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) edittext = v as? AppCompatEditText
        }
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            getFileSviat()
        }
        setTollbarTheme()
    }

    private suspend fun getFileSviat(count: Int = 0) {
        var error = false
        binding.progressBar2.visibility = View.VISIBLE
        try {
            val localFile = File("$filesDir/cache/cache.txt")
            Malitounik.referens.child("/chytanne/sviatyja/opisanie13.json").getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) {
                    val builder = localFile.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    arrayList.addAll(gson.fromJson(builder, type))
                } else {
                    error = true
                }
            }.await()
        } catch (e: Throwable) {
            error = true
        }
        if (error && count < 3) {
            getFileSviat(count + 1)
            return
        }
        if (error) {
            MainActivity.toastView(this@SviatyiaPyxomyia, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
            binding.progressBar2.visibility = View.GONE
            return
        }
        for (i in 0 until arrayList.size) {
            for (e in 0 until sviaty.size) {
                if (arrayList[i][0].toInt() == sviaty[e].data && arrayList[i][1].toInt() == sviaty[e].mun) {
                    sviaty[e].opisanie = arrayList[i][2]
                    break
                }
            }
        }
        binding.spinnerSviaty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.sviaty.setText(sviaty[position].opisanie)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.spinnerSviaty.adapter = SpinnerAdapter(this@SviatyiaPyxomyia, sviaty)
        if (intent.extras != null) {
            for (i in 0 until sviaty.size) {
                if (intent.extras?.getInt("day") == sviaty[i].data && intent.extras?.getInt("mun") == sviaty[i].mun) {
                    binding.spinnerSviaty.setSelection(i)
                    break
                }
            }
        } else {
            binding.spinnerSviaty.setSelection(0)
        }
        binding.progressBar2.visibility = View.GONE
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.admin_sviatya_pyxomyia)
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

    private fun fileUpload(bitmap: Bitmap?, text: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = File("$filesDir/cache/cache.txt")
                val fileName = "s_" + sviaty[binding.spinnerSviaty.selectedItemPosition].data.toString() + "_" + sviaty[binding.spinnerSviaty.selectedItemPosition].mun.toString() + "_1.jpg"
                bitmap?.let {
                    withContext(Dispatchers.IO) {
                        val out = FileOutputStream(localFile)
                        it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                    }
                    Malitounik.referens.child("/chytanne/icons/$fileName").putFile(Uri.fromFile(localFile)).await()
                }
                val t1 = fileName.lastIndexOf(".")
                val fileNameT = fileName.substring(0, t1) + ".txt"
                if (text != "") {
                    localFile.writer().use {
                        it.write(text)
                    }
                    Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                        val file = File("$filesDir/iconsApisanne/$fileNameT")
                        localFile.copyTo(file, true)
                    }.await()
                } else {
                    try {
                        Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").delete().await()
                    } catch (_: Throwable) {
                    }
                }
                loadFilesMetaData()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private suspend fun loadFilesMetaData() {
        val sb = StringBuilder()
        val list = Malitounik.referens.child("/chytanne/icons").list(1000).await()
        list.items.forEach {
            val meta = it.metadata.await()
            sb.append(it.name).append("<-->").append(meta.sizeBytes).append("<-->").append(meta.updatedTimeMillis).append("\n")
        }
        val fileIcon = File("$filesDir/iconsMataData.txt")
        fileIcon.writer().use {
            it.write(sb.toString())
        }
        Malitounik.referens.child("/chytanne/iconsMataData.txt").putFile(Uri.fromFile(fileIcon)).await()
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        edittext?.let {
            if (id == R.id.action_bold) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<strong>")
                    append(text.substring(startSelect, endSelect))
                    append("</strong>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 17)
            }
            if (id == R.id.action_em) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<em>")
                    append(text.substring(startSelect, endSelect))
                    append("</em>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 9)
            }
            if (id == R.id.action_red) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<font color=\"#d00505\">")
                    append(text.substring(startSelect, endSelect))
                    append("</font>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 29)
            }
            if (id == R.id.action_p) {
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, endSelect))
                    append("<p>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 3)
            }
        }
    }

    override fun onBack() {
        if (binding.scrollpreView.visibility == View.VISIBLE) {
            binding.scrollpreView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        } else {
            super.onBack()
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val editItem = menu.findItem(R.id.action_preview)
        if (binding.scrollpreView.visibility == View.GONE) {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
        } else {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_upload_image) {
            val dialog = DialogEditImage.getInstance("$filesDir/icons/s_" + sviaty[binding.spinnerSviaty.selectedItemPosition].data.toString() + "_" + sviaty[binding.spinnerSviaty.selectedItemPosition].mun.toString() + "_1.jpg")
            dialog.show(supportFragmentManager, "DialogEditImage")
            return true
        }
        if (id == R.id.action_save) {
            sendPostRequest(binding.spinnerSviaty.selectedItemPosition, binding.sviaty.text.toString())
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.visibility == View.VISIBLE) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            } else {
                binding.preView.text = MainActivity.fromHtml(binding.sviaty.text.toString()).trim()
                binding.scrollpreView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.sviaty.windowToken, 0)
                invalidateOptionsMenu()
            }
            return true
        }
        return false
    }

    private fun sendPostRequest(position: Int, apisanne: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    var day = -1
                    var mun = 0
                    when (position) {
                        0 -> {
                            day = -1
                            mun = 0
                        }

                        1 -> {
                            day = -1
                            mun = 1
                        }
                    }
                    var index = -1
                    for (i in 0 until arrayList.size) {
                        if (day == arrayList[i][0].toInt() && mun == arrayList[i][1].toInt()) {
                            index = i
                            break
                        }
                    }
                    if (index == -1) {
                        val temp = ArrayList<String>()
                        temp.add(day.toString())
                        temp.add(mun.toString())
                        temp.add(apisanne)
                        arrayList.add(temp)
                    } else {
                        arrayList[index][2] = apisanne
                    }
                    if (arrayList.isNotEmpty()) {
                        val localFile = File("$filesDir/sviatyja/opisanie13.json")
                        val gson = Gson()
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                        localFile.writer().use {
                            it.write(gson.toJson(arrayList, type))
                        }
                        Malitounik.referens.child("/chytanne/sviatyja/opisanie13.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                            if (it.isSuccessful) {
                                MainActivity.toastView(this@SviatyiaPyxomyia, getString(by.carkva_gazeta.malitounik.R.string.save))
                            } else {
                                MainActivity.toastView(this@SviatyiaPyxomyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                            }
                        }.await()
                    } else {
                        MainActivity.toastView(this@SviatyiaPyxomyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@SviatyiaPyxomyia, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviaty, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    private class SpinnerAdapter(activity: Activity, private val data: ArrayList<SviatyData>) : ArrayAdapter<SviatyData>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = data[position].title
            textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = data[position].title
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class SviatyData(val data: Int, val mun: Int, val title: String, var opisanie: String = "", var utran: String = "", var liturgia: String = "", var viachernia: String = "")
}