package by.carkva_gazeta.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import by.carkva_gazeta.admin.databinding.AdminSviatyBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type

class Sviaty : BaseActivity(), View.OnClickListener, DialogImageFileLoad.DialogFileExplorerListener, DialogImageFileExplorer.DialogImageFileExplorerListener {
    private lateinit var binding: AdminSviatyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val sviaty = ArrayList<SviatyData>()
    private var edittext: AppCompatEditText? = null
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            val fileExplorer = DialogImageFileExplorer()
            fileExplorer.show(supportFragmentManager, "file_explorer")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun setImageFile(bitmap: Bitmap?, position: Int) {
    }

    override fun setImageFileCancel() {
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = AdminSviatyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sviaty.add(SviatyData(-1, 0, "Уваход у Ерусалім"))
        sviaty.add(SviatyData(-1, 1, "Уваскрасеньне"))
        sviaty.add(SviatyData(-1, 2, "Узьнясеньне"))
        sviaty.add(SviatyData(-1, 3, "Тройца"))
        sviaty.add(SviatyData(1, 1, "1 Студзеня"))
        sviaty.add(SviatyData(6, 1, "6 Студзеня"))
        sviaty.add(SviatyData(2, 2, "2 Лютага"))
        sviaty.add(SviatyData(25, 3, "25 Сакавіка"))
        sviaty.add(SviatyData(24, 6, "24 Чэрвеня"))
        sviaty.add(SviatyData(29, 6, "29 Чэрвеня"))
        sviaty.add(SviatyData(6, 8, "6 Жніўня"))
        sviaty.add(SviatyData(15, 8, "15 Жніўня"))
        sviaty.add(SviatyData(29, 8, "29 Жніўня"))
        sviaty.add(SviatyData(8, 9, "8 Верасьня"))
        sviaty.add(SviatyData(14, 9, "14 Верасьня"))
        sviaty.add(SviatyData(1, 10, "1 Кастрычніка"))
        sviaty.add(SviatyData(21, 11, "21 Лістапада"))
        sviaty.add(SviatyData(25, 12, "25 Сьнежня"))
        sviaty.add(SviatyData(-1, 4, "Айцоў першых 6-ці Ўсяленскіх сабораў"))
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.sviaty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) edittext = v as? AppCompatEditText
        }
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            val arrayList = ArrayList<ArrayList<String>>()
            try {
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("opisanieSviat", "json")
                }
                referens.child("/opisanie_sviat.json").getFile(localFile).addOnSuccessListener {
                    val builder = localFile.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                    arrayList.addAll(gson.fromJson(builder, type))
                }.await()
            } catch (e: Throwable) {
                MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
            }
            for (i in 0 until arrayList.size) {
                for (e in 0 until sviaty.size) {
                    if (arrayList[i][0].toInt() == sviaty[e].data && arrayList[i][1].toInt() == sviaty[e].mun) {
                        sviaty[e].opisanie = arrayList[i][2]
                        sviaty[e].utran = arrayList[i][3]
                        sviaty[e].liturgia = arrayList[i][4]
                        sviaty[e].viachernia = arrayList[i][5]
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
            binding.spinnerSviaty.adapter = SpinnerAdapter(this@Sviaty, sviaty)
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
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.sviaty)
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

    private fun fileUpload(bitmap: Bitmap) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("imageSave", "jpeg")
                }
                withContext(Dispatchers.IO) {
                    val out = FileOutputStream(localFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.flush()
                    out.close()
                }
                val fileName = File("/chytanne/icons/v_" + sviaty[binding.spinnerSviaty.selectedItemPosition].data.toString() + "_" + sviaty[binding.spinnerSviaty.selectedItemPosition].mun.toString() + ".jpg")
                val localFile2 = withContext(Dispatchers.IO) {
                    File.createTempFile("icons", "json")
                }
                referens.child("/chytanne/icons/" + fileName.name).putFile(Uri.fromFile(localFile)).await()
                val arrayListIcon = java.util.ArrayList<java.util.ArrayList<String>>()
                referens.child("/icons.json").getFile(localFile2).addOnSuccessListener {
                    val gson = Gson()
                    val json = localFile.readText()
                    val type: Type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    arrayListIcon.addAll(gson.fromJson(json, type))
                }.await()
                var chek = false
                arrayListIcon.forEach { result ->
                    if (fileName.name == result[0]) {
                        referens.child("/chytanne/icons/" + fileName.name).metadata.addOnSuccessListener {
                            result[1] = it.sizeBytes.toString()
                            result[2] = it.updatedTimeMillis.toString()
                            chek = true
                        }.await()
                    }
                }
                if (!chek) {
                    referens.child("/chytanne/icons/" + fileName.name).metadata.addOnSuccessListener {
                        val result = java.util.ArrayList<String>()
                        result.add(it.name ?: "")
                        result.add(it.sizeBytes.toString())
                        result.add(it.updatedTimeMillis.toString())
                        arrayListIcon.add(result)
                    }.await()
                }
                localFile2.writer().use {
                    val gson = Gson()
                    it.write(gson.toJson(arrayListIcon))
                }
                referens.child("/icons.json").putFile(Uri.fromFile(localFile2)).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.save))
                    } else {
                        MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onDialogFile(absolutePath: String, image: Int) {
        val bitmap = BitmapFactory.decodeFile(absolutePath)
        fileUpload(bitmap)
        val dialogImageFileExplorer = supportFragmentManager.findFragmentByTag("dialogImageFileExplorer") as? DialogImageFileExplorer
        dialogImageFileExplorer?.dialog?.cancel()
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
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(0, true)
                dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
            }
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
                        2 -> {
                            day = -1
                            mun = 2
                        }
                        3 -> {
                            day = -1
                            mun = 3
                        }
                        4 -> {
                            day = 1
                            mun = 1
                        }
                        5 -> {
                            day = 6
                            mun = 1
                        }
                        6 -> {
                            day = 2
                            mun = 2
                        }
                        7 -> {
                            day = 25
                            mun = 3
                        }
                        8 -> {
                            day = 24
                            mun = 6
                        }
                        9 -> {
                            day = 29
                            mun = 6
                        }
                        10 -> {
                            day = 6
                            mun = 8
                        }
                        11 -> {
                            day = 15
                            mun = 8
                        }
                        12 -> {
                            day = 29
                            mun = 8
                        }
                        13 -> {
                            day = 8
                            mun = 9
                        }
                        14 -> {
                            day = 14
                            mun = 9
                        }
                        15 -> {
                            day = 1
                            mun = 10
                        }
                        16 -> {
                            day = 21
                            mun = 11
                        }
                        17 -> {
                            day = 25
                            mun = 12
                        }
                    }
                    var opisanie: String
                    var utran = ""
                    var linur = ""
                    var viach = ""
                    var index = -5
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("opisanie_sviat", "json")
                    }
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    var array = ArrayList<ArrayList<String>>()
                    referens.child("/opisanie_sviat.json").getFile(localFile).addOnSuccessListener {
                        val builder = localFile.readText()
                        array = gson.fromJson(builder, type)
                        for (i in 0 until array.size) {
                            if (day == array[i][0].toInt() && mun == array[i][1].toInt()) {
                                opisanie = array[i][2]
                                utran = array[i][3]
                                linur = array[i][4]
                                viach = array[i][5]
                                index = i
                                break
                            }
                        }
                        opisanie = apisanne
                        if (index == -5) {
                            val temp = ArrayList<String>()
                            temp.add(day.toString())
                            temp.add(mun.toString())
                            temp.add(opisanie)
                            temp.add(utran)
                            temp.add(linur)
                            temp.add(viach)
                            array.add(temp)
                        } else {
                            array[index][2] = opisanie
                            array[index][3] = utran
                            array[index][4] = linur
                            array[index][5] = viach
                        }
                    }.await()
                    localFile.writer().use {
                        it.write(gson.toJson(array))
                    }
                    val logFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val sb = StringBuilder()
                    val url = "/opisanie_sviat.json"
                    referens.child("/admin/log.txt").getFile(logFile).await()
                    var ref = true
                    logFile.readLines().forEach {
                        sb.append("$it\n")
                        if (it.contains(url)) {
                            ref = false
                        }
                    }
                    if (ref) {
                        sb.append("$url\n")
                    }
                    logFile.writer().use {
                        it.write(sb.toString())
                    }
                    referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()

                    referens.child("/opisanie_sviat.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.save))
                        } else {
                            MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviaty, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    private class SpinnerAdapter(activity: Activity, private val data: ArrayList<SviatyData>) : ArrayAdapter<SviatyData>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
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
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text.text = data[position].title
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class SviatyData(val data: Int, val mun: Int, val title: String, var opisanie: String = "", var utran: String = "", var liturgia: String = "", var viachernia: String = "")
}