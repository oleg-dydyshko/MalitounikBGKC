package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import by.carkva_gazeta.admin.databinding.AdminPasochnicaBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.InteractiveScrollView
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.apache.commons.text.StringEscapeUtils
import java.io.File


class Pasochnica : BaseActivity(), View.OnClickListener, DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogSaveAsFileExplorer.DialogSaveAsFileExplorerListener, DialogFileExists.DialogFileExistsListener, DialogPasochnicaMkDir.DialogPasochnicaMkDirListener, DialogAddPesny.DialogAddPesnyListiner, InteractiveScrollView.OnInteractiveScrollChangedCallback, DialogPasochnicaAHref.DialogPasochnicaAHrefListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaBinding
    private var resetTollbarJob: Job? = null
    private var fileName = "new_file.html"
    private var resours = ""
    private var history = ArrayList<History>()
    private var positionY = 0
    private var firstTextPosition = ""
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference
    private var isHTML = true
    private val textWatcher = object : TextWatcher {
        private var editPosition = 0

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable?) {
            addHistory(s, editPosition)
            if (history.size > 1) {
                binding.actionBack.visibility = View.VISIBLE
            } else {
                binding.actionBack.visibility = View.GONE
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun setUrl(url: String, titleUrl: String) {
        val startSelect = binding.apisanne.selectionStart
        val endSelect = binding.apisanne.selectionEnd
        if (fileName.contains(".htm")) {
            val text = SpannableStringBuilder(binding.apisanne.text)
            val subtext = text.getSpans(startSelect, endSelect, URLSpan::class.java)
            subtext.forEach {
                if (it.url.contains(url)) {
                    text.removeSpan(it)
                }
            }
            text.setSpan(URLSpan(url), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.append(text.substring(0, startSelect))
            text.append(titleUrl)
            text.append(text.substring(endSelect))
            binding.apisanne.text = text
        } else {
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<a href=\"$url\">")
                append(titleUrl)
                append("</a>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 29)
        }
        addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
    }

    private fun addHistory(s: Editable?, editPosition: Int) {
        s?.let {
            if (it.toString() != "") {
                if (history.size == 51) history.removeAt(0)
                history.add(History(it.toSpannable(), editPosition))
            }
            if (history.size > 1) {
                binding.actionBack.visibility = View.VISIBLE
            } else {
                binding.actionBack.visibility = View.GONE
            }
        }
    }

    override fun onScroll(t: Int, oldt: Int) {
        positionY = t
        binding.apisanne.layout?.let { layout ->
            val textForVertical = binding.apisanne.text.toString().substring(layout.getLineStart(layout.getLineForVertical(positionY)), layout.getLineEnd(layout.getLineForVertical(positionY))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        val prefEditor = k.edit()
        prefEditor.putInt("admin" + fileName + "position", positionY)
        prefEditor.apply()
    }

    private fun findResoursNameAndTitle(): String {
        var title: String
        val t3 = fileName.lastIndexOf(".")
        title = if (t3 != -1) {
            var findResours = false
            for (i in 0 until PasochnicaList.findDirAsSave.size) {
                if (fileName.substring(0, t3) == PasochnicaList.findDirAsSave[i]) {
                    findResours = true
                    break
                }
            }
            if (findResours) {
                resours = fileName.substring(0, t3)
                fileName.substring(0, t3)
            } else fileName
        } else fileName
        val t1 = fileName.indexOf("(")
        if (t1 != -1) {
            val t2 = fileName.indexOf(")")
            resours = fileName.substring(1, t2)
            title = if (t3 != -1) fileName.substring(t2 + 1, t3)
            else fileName.substring(t2 + 1)
        }
        return title.trim()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        binding = AdminPasochnicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apisanne.addTextChangedListener(textWatcher)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.actionA.setOnClickListener(this)
        binding.actionBr.setOnClickListener(this)
        binding.actionBack.setOnClickListener(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.actionKeyword.visibility = View.GONE
        } else {
            binding.apisanne.showSoftInputOnFocus = false
        }
        binding.actionKeyword.setOnClickListener(this)
        binding.scrollView.setOnScrollChangedCallback(this)
        fileName = intent.extras?.getString("fileName", "") ?: "new_file.html"
        resours = intent.extras?.getString("resours", "") ?: ""
        var title = intent.extras?.getString("title", "") ?: ""
        if (resours == "" && title == "") {
            title = findResoursNameAndTitle()
        }
        val text = intent.extras?.getString("text", "") ?: ""
        fileName = if (resours == "") {
            title
        } else {
            "($resours) $title"
        }
        if (savedInstanceState != null) {
            isHTML = savedInstanceState.getBoolean("isHTML", true)
            fileName = savedInstanceState.getString("fileName", "")
            resours = savedInstanceState.getString("resours", "")
            history.clear()
            binding.apisanne.post {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    binding.apisanne.layout?.let { layout ->
                        val index = binding.apisanne.text.toString().indexOf(textline)
                        val line = layout.getLineForOffset(index)
                        val y = layout.getLineTop(line)
                        binding.scrollView.smoothScrollBy(0, y)
                    }
                } else {
                    binding.scrollView.smoothScrollBy(0, positionY)
                }
            }
        } else {
            val newFile = intent.extras?.getBoolean("new_file", false) ?: false
            when {
                intent.extras?.getBoolean("backcopy", false) == true -> {
                    if (text.contains("<!DOCTYPE HTML>", ignoreCase = true)) {
                        binding.apisanne.setText(MainActivity.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                        binding.actionP.visibility = View.GONE
                        binding.actionBr.visibility = View.GONE
                    } else {
                        binding.apisanne.setText(text)
                        isHTML = false
                    }
                }
                !newFile -> {
                    getOrSendFilePostRequest(text, false)
                }
                else -> {
                    intent.removeExtra("newFile")
                }
            }
        }
        positionY = k.getInt("admin" + fileName + "position", 0)
        setTollbarTheme()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isHTML", isHTML)
        outState.putString("fileName", fileName)
        outState.putString("textLine", firstTextPosition)
        outState.putString("resours", resours)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.pasochnica)
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

    override fun onResume() {
        super.onResume()
        findDirAsSave()
        setTollbarTheme()
    }

    override fun onBack() {
        onSupportNavigateUp()
    }

    override fun onDialogSaveAsFile(dir: String, oldFileName: String, fileName: String) {
        getFileIssetPostRequest(dir, oldFileName, fileName)
    }

    override fun setFileName(oldFileName: String, fileName: String, isSite: Boolean) {
        this.fileName = fileName
        saveResult()
    }

    override fun fileExists(dir: String, oldFileName: String, fileName: String) {
        sendSaveAsPostRequest("$dir/$fileName", oldFileName)
    }

    override fun setDir() {
        val dialogSaveAsFileExplorer = supportFragmentManager.findFragmentByTag("dialogSaveAsFileExplorer") as? DialogSaveAsFileExplorer
        dialogSaveAsFileExplorer?.dismiss()
        Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
            setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
            setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
            setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            show()
        }
    }

    override fun addPesny(title: String, pesny: String, fileName: String) {
        sendSaveAsAddNewPesnyPostRequest(title, pesny, fileName)
    }

    private fun sendSaveAsAddNewPesnyPostRequest(title: String, pesny: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val localFile2 = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val string = StringBuilder()
                    val t1 = fileName.indexOf(".")
                    val nawFileName = if (t1 != -1) fileName.substring(0, t1)
                    else fileName
                    referens.child("/admin/pesny/pesny_menu.txt").getFile(localFile).addOnSuccessListener {
                        var onRun = false
                        localFile.readLines().forEach {
                            if (it.indexOf(pesny, ignoreCase = true) != -1) {
                                onRun = true
                                string.append("$it\n")
                            } else {
                                if (onRun) {
                                    string.append("$pesny$nawFileName<>$title\n")
                                    onRun = false
                                }
                                string.append("$it\n")
                            }
                        }
                    }.await()
                    referens.child("/admin/piasochnica/$fileName").getFile(localFile2).await()
                    referens.child("/admin/pesny/$pesny$fileName").putFile(Uri.fromFile(localFile2)).await()
                    referens.child("/admin/piasochnica/($pesny$nawFileName) $title").putFile(Uri.fromFile(localFile2)).await()
                    referens.child("/admin/piasochnica/$fileName").delete().await()
                    localFile.writer().use {
                        it.write(string.toString())
                    }
                    referens.child("/admin/pesny/pesny_menu.txt").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        } else {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        }
                    }.await()
                    PasochnicaList.getFindFileListAsSave()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun getFileIssetPostRequest(dir: String, oldFileName: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    referens.child("/$dir/" + fileName.replace("\n", " ")).downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val dialogFileExists = DialogFileExists.getInstance(dir, oldFileName, fileName)
                            dialogFileExists.show(supportFragmentManager, "dialogFileExists")
                        } else {
                            sendSaveAsPostRequest("$dir/$fileName", oldFileName)
                        }
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun sendSaveAsPostRequest(dirToFile: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    referens.child("/admin/piasochnica/" + fileName.replace("\n", " ")).getFile(localFile).await()
                    referens.child("/$dirToFile").putFile(Uri.fromFile(localFile)).await()
                    var oldFile = ""
                    if (fileName.indexOf("(") == -1) {
                        val t1 = dirToFile.lastIndexOf("/")
                        oldFile = "(" + dirToFile.substring(t1 + 1) + ") "
                        val t2 = oldFile.lastIndexOf(".")
                        if (t2 != -1) {
                            oldFile = " (" + oldFile.substring(0, t2) + ")"
                        }
                    }
                    referens.child("/admin/piasochnica/$oldFile" + fileName.replace("\n", " ")).putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        } else {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        }
                    }.await()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun getOrSendFilePostRequest(content: String, isSaveAs: Boolean = true) {
        if (isSaveAs) {
            val dir = getExternalFilesDir("PiasochnicaBackCopy")
            dir?.let {
                if (!dir.exists()) dir.mkdir()
            }
            val file = File(getExternalFilesDir("PiasochnicaBackCopy"), fileName)
            file.writer().use {
                it.write(content)
            }
        }
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var result = ""
                binding.progressBar2.visibility = View.VISIBLE
                val isSite = intent.extras?.getBoolean("isSite", false) ?: false
                if (isSite) {
                    intent.removeExtra("isSite")
                    try {
                        val localFile = withContext(Dispatchers.IO) {
                            File.createTempFile("piasochnica", "json")
                        }
                        referens.child("/admin/piasochnica/$fileName").getFile(localFile).await()
                        result = localFile.readText()
                    } catch (e: Throwable) {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                } else {
                    try {
                        val localFile = withContext(Dispatchers.IO) {
                            File.createTempFile("piasochnica", "json")
                        }
                        result = getTextOnSite(resours)
                        if (!isSaveAs) {
                            if (result == "") result = content
                        } else result = content
                        localFile.writer().use {
                            it.write(result)
                        }
                        referens.child("/admin/piasochnica/" + fileName.replace("\n", " ")).putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                            if (it.isSuccessful) {
                                PasochnicaList.getFindFileListAsSave()
                                if (isSaveAs) {
                                    if (!findDirAsSave()) {
                                        val dialogSaveAsFileExplorer = DialogSaveAsFileExplorer.getInstance(fileName)
                                        dialogSaveAsFileExplorer.show(supportFragmentManager, "dialogSaveAsFileExplorer")
                                        Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                                            setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                            setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                            setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                            show()
                                        }
                                    } else {
                                        sendSaveAsPostRequest(getDirAsSave(), fileName)
                                    }
                                }
                            } else {
                                Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                                    setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                    setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                    setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                    show()
                                }
                            }
                        }.await()
                    } catch (e: Throwable) {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
                if (isHTML) {
                    binding.apisanne.setText(MainActivity.fromHtml(result, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    binding.actionP.visibility = View.GONE
                    binding.actionBr.visibility = View.GONE
                } else {
                    binding.apisanne.setText(result)
                }
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            if (isHTML) {
                binding.apisanne.setText(MainActivity.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT))
                binding.actionP.visibility = View.GONE
                binding.actionBr.visibility = View.GONE
            } else {
                binding.apisanne.setText(content)
            }
        }
    }

    private fun getTextOnSite(fileName: String): String {
        var text = ""
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("piasochnica", "json")
                }
                val result = PasochnicaList.findDirAsSave
                val newFileName = "$fileName.html"
                for (i in 0 until result.size) {
                    val t1 = result[i].lastIndexOf("/")
                    if (result[i].substring(t1 + 1) == newFileName) {
                        referens.child("/" + result[i]).getFile(localFile).addOnSuccessListener {
                            text = localFile.readText()
                        }.await()
                        break
                    }
                }
            } catch (e: Throwable) {
                text = ""
                MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
            }
        }
        return text
    }

    private fun findDirAsSave(): Boolean {
        var result = false
        if (resours != "") {
            for (i in 0 until PasochnicaList.findDirAsSave.size) {
                if (PasochnicaList.findDirAsSave[i].contains(resours)) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    private fun getDirAsSave(): String {
        var result = ""
        if (resours != "") {
            for (i in 0 until PasochnicaList.findDirAsSave.size) {
                if (PasochnicaList.findDirAsSave[i].contains(resours)) {
                    result = PasochnicaList.findDirAsSave[i]
                    break
                }
            }
        }
        return result
    }

    private fun clearColor(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<font color=\"#d00505\">", position)
            val t2 = result.indexOf("</font>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 22, t2)
                val oldSubText = result.substring(t1, t2 + 7)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</font>", position)
            val t2 = result.indexOf("<font color=\"#d00505\">", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 7, t2)
                val oldSubText = result.substring(t1, t2 + 22)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearBold(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<strong>", position)
            val t2 = result.indexOf("</strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 8, t2)
                val oldSubText = result.substring(t1, t2 + 9)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</strong>", position)
            val t2 = result.indexOf("<strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 9, t2)
                val oldSubText = result.substring(t1, t2 + 8)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearEm(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<em>", position)
            val t2 = result.indexOf("</em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 4, t2)
                val oldSubText = result.substring(t1, t2 + 5)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</em>", position)
            val t2 = result.indexOf("<em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 5, t2)
                val oldSubText = result.substring(t1, t2 + 4)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearHtml(text: String): String {
        var result = text
        val t1 = result.indexOf("<p")
        if (t1 != -1) {
            val t2 = result.indexOf(">")
            val subString = result.substring(t1, t2 + 1)
            var stringres = result.replace(subString, "")
            stringres = stringres.replace("</p>", "<br>")
            stringres = stringres.replace("<span", "<font")
            stringres = stringres.replace("</span>", "</font>")
            stringres = stringres.replace("style=\"color:#D00505;\"", "color=\"#d00505\"")
            stringres = stringres.replace("<i>", "<em>")
            stringres = stringres.replace("</i>", "</em>")
            stringres = stringres.replace("<b>", "<strong>")
            stringres = stringres.replace("</b>", "</strong>")
            stringres = stringres.replace("<u>", "")
            stringres = stringres.replace("</u>", "")
            val t3 = stringres.lastIndexOf("<br>")
            result = stringres.substring(0, t3)
        }
        return result
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_preview) {
            isHTML = !isHTML
            convertView()
            return true
        }
        if (id == R.id.action_save) {
            if (fileName == "new_file.html") {
                val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance("new_file.html", false)
                dialogPasochnicaFileName.show(supportFragmentManager, "dialogPasochnicaFileName")
            } else {
                saveResult()
            }
            return true
        }
        return false
    }

    private fun convertView() {
        val text = binding.apisanne.text
        if (isHTML) {
            binding.apisanne.setText(MainActivity.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT))
            binding.actionP.visibility = View.GONE
            binding.actionBr.visibility = View.GONE
        } else {
            text?.let {
                var result = MainActivity.toHtml(it)
                result = StringEscapeUtils.unescapeHtml4(result)
                result = clearHtml(result)
                result = clearColor(result)
                result = clearEm(result)
                result = clearBold(result)
                result = clearEm(result)
                result = clearColor(result)
                result = clearBold(result)
                result = clearEm(result)
                binding.actionP.visibility = View.VISIBLE
                binding.actionBr.visibility = View.VISIBLE
                binding.apisanne.setText(result)
            }
        }
    }

    private fun saveResult() {
        val text = binding.apisanne.text
        if (isHTML) {
            text?.let {
                var result = MainActivity.toHtml(it)
                result = StringEscapeUtils.unescapeHtml4(result)
                result = clearHtml(result)
                result = clearColor(result)
                result = clearEm(result)
                result = clearBold(result)
                result = clearEm(result)
                result = clearColor(result)
                result = clearBold(result)
                result = clearEm(result)
                if (!result.contains("<!DOCTYPE HTML>")) result = "<!DOCTYPE HTML>$result"
                getOrSendFilePostRequest(result)
            }
        } else {
            getOrSendFilePostRequest(text.toString())
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_back) {
            binding.apisanne.removeTextChangedListener(textWatcher)
            if (history.size > 1) {
                binding.apisanne.setText(history[history.size - 2].spannable)
                binding.apisanne.setSelection(history[history.size - 2].editPosition)
                history.removeAt(history.size - 1)
            }
            if (history.size > 1) {
                binding.actionBack.visibility = View.VISIBLE
            } else {
                binding.actionBack.visibility = View.GONE
            }
            binding.apisanne.addTextChangedListener(textWatcher)
        }
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (fileName.contains(".htm")) {
                val text = binding.apisanne.text
                text?.let { editable ->
                    val subtext = editable.getSpans(startSelect, endSelect, StyleSpan(Typeface.BOLD)::class.java)
                    var check = false
                    subtext.forEach {
                        if (it.style == Typeface.BOLD) {
                            check = true
                            editable.removeSpan(it)
                        }
                    }
                    if (!check) editable.setSpan(StyleSpan(Typeface.BOLD), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                val text = binding.apisanne.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<strong>")
                    append(text.substring(startSelect, endSelect))
                    append("</strong>")
                    append(text.substring(endSelect))
                    toString()
                }
                binding.apisanne.setText(build)
                binding.apisanne.setSelection(endSelect + 17)
            }
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (fileName.contains(".htm")) {
                val text = binding.apisanne.text
                text?.let { editable ->
                    val subtext = editable.getSpans(startSelect, endSelect, StyleSpan(Typeface.ITALIC)::class.java)
                    var check = false
                    subtext.forEach {
                        if (it.style == Typeface.ITALIC) {
                            check = true
                            editable.removeSpan(it)
                        }
                    }
                    if (!check) editable.setSpan(StyleSpan(Typeface.ITALIC), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                val text = binding.apisanne.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<em>")
                    append(text.substring(startSelect, endSelect))
                    append("</em>")
                    append(text.substring(endSelect))
                    toString()
                }
                binding.apisanne.setText(build)
                binding.apisanne.setSelection(endSelect + 9)
            }
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (fileName.contains(".htm")) {
                val text = binding.apisanne.text
                text?.let { editable ->
                    val subtext = editable.getSpans(startSelect, endSelect, ForegroundColorSpan::class.java)
                    var check = false
                    subtext.forEach {
                        if (it.foregroundColor == ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)) {
                            check = true
                            editable.removeSpan(it)
                        }
                    }
                    if (!check) editable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                val text = binding.apisanne.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<font color=\"#d00505\">")
                    append(text.substring(startSelect, endSelect))
                    append("</font>")
                    append(text.substring(endSelect))
                    toString()
                }
                binding.apisanne.setText(build)
                binding.apisanne.setSelection(endSelect + 29)
            }
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_br) {
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<br>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 4)
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_p) {
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<p>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 3)
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_a) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (startSelect == endSelect) {
                MainActivity.toastView(this, "Памылка. Абярыце тэкст", Toast.LENGTH_LONG)
            } else {
                val text = binding.apisanne.text
                val urlSpan = text?.getSpans(startSelect, endSelect, URLSpan::class.java)
                var url = ""
                urlSpan?.forEach {
                    url = it.url
                }
                val dialogPasochnicaAHref = DialogPasochnicaAHref.getInstance(url, text?.substring(startSelect, endSelect) ?: "")
                dialogPasochnicaAHref.show(supportFragmentManager, "dialogPasochnicaAHref")
            }
        }
        if (id == R.id.action_keyword) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (binding.apisanne.showSoftInputOnFocus) {
                    imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                } else {
                    imm.showSoftInput(binding.apisanne, 0)
                }
                binding.apisanne.showSoftInputOnFocus = !binding.apisanne.showSoftInputOnFocus
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piasochnica, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    private data class History(val spannable: Spannable, val editPosition: Int)
}