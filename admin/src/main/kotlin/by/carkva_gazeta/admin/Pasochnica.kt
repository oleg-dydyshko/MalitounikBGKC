package by.carkva_gazeta.admin

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import by.carkva_gazeta.admin.databinding.AdminPasochnicaBinding
import by.carkva_gazeta.malitounik.InteractiveScrollView
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.apache.commons.text.StringEscapeUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Pasochnica : AppCompatActivity(), View.OnClickListener, DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogSaveAsFileExplorer.DialogSaveAsFileExplorerListener, DialogFileExists.DialogFileExistsListener, DialogPasochnicaMkDir.DialogPasochnicaMkDirListener, DialogAddPesny.DialogAddPesnyListiner, InteractiveScrollView.OnScrollChangedCallback, DialogDeliteHelp.DialogDeliteHelpListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaBinding
    private var resetTollbarJob: Job? = null
    private var fileName = "newFile.html"
    private var history = ArrayList<History>()
    private var positionY = 0
    private var firstTextPosition = ""
    private val textWatcher = object : TextWatcher {
        var editPosition = 0

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
        val laneLayout = binding.apisanne.layout
        laneLayout?.let { layout ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
        binding = AdminPasochnicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apisanne.addTextChangedListener(textWatcher)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.actionBr.setOnClickListener(this)
        binding.actionBack.setOnClickListener(this)
        binding.scrollView.setOnScrollChangedCallback(this)
        fileName = intent.extras?.getString("fileName", "newFile.html") ?: "newFile.html"
        val text = intent.extras?.getString("text", "") ?: ""
        if (savedInstanceState != null) {
            fileName = savedInstanceState.getString("fileName", "")
            history.clear()
            binding.apisanne.post {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    val index = binding.apisanne.text.toString().indexOf(textline)
                    val line = binding.apisanne.layout.getLineForOffset(index)
                    val y = binding.apisanne.layout.getLineTop(line)
                    binding.scrollView.scrollY = y
                } else {
                    binding.scrollView.smoothScrollBy(0, positionY)
                }
            }
        } else {
            if (fileName != "newFile.html") {
                getFilePostRequest(fileName)
            } else {
                if (fileName.contains(".htm")) {
                    binding.apisanne.setText(MainActivity.fromHtml(text))
                    binding.actionP.visibility = View.GONE
                    binding.actionBr.visibility = View.GONE
                } else {
                    binding.apisanne.setText(text)
                }
            }

        }
        if (text != "") {
            val gson = Gson()
            val resours = intent.extras?.getString("resours", "") ?: ""
            val title = intent.extras?.getString("title", "") ?: ""
            fileName = if (resours == "") {
                title
            } else {
                "($resours) $title.html"
            }
            if (intent.extras?.getBoolean("exits", false) == false) {
                sendPostRequest(fileName, gson.toJson(text), false)
            } else {
                getFilePostRequest(fileName)
            }
        }
        positionY = k.getInt("admin" + fileName + "position", 0)
        setTollbarTheme()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", fileName)
        outState.putString("textLine", firstTextPosition)
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
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onDialogSaveAsFile(dir: String, oldFileName: String, fileName: String) {
        getFileIssetPostRequest(dir, oldFileName, fileName)
    }

    override fun setFileName(oldFileName: String, fileName: String) {
        this.fileName = fileName
        saveResult(fileName)
    }

    override fun fileExists(dir: String, oldFileName: String, fileName: String) {
        sendSaveAsPostRequest("$dir/$fileName", oldFileName)
    }

    override fun setDir(oldDir: String) {
        val dialogSaveAsFileExplorer = supportFragmentManager.findFragmentByTag("dialogSaveAsFileExplorer") as? DialogSaveAsFileExplorer
        dialogSaveAsFileExplorer?.mkDir(oldDir)
    }

    override fun addPesny(title: String, pesny: String, fileName: String) {
        sendSaveAsAddNewPesnyPostRequest(title, pesny, fileName)
    }

    override fun onFileDelite(fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("unlink", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        inputStream
                    }
                }
                binding.progressBar2.visibility = View.GONE
                onBackPressed()
            }
        }
    }

    private fun sendSaveAsAddNewPesnyPostRequest(title: String, pesny: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("NewPesny", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode(pesny, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        responseCodeS = responseCode
                    }
                }
                if (responseCodeS == 200) {
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
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun getFileIssetPostRequest(dir: String, oldFileName: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var result = ""
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("isset", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("dir", "UTF-8") + "=" + URLEncoder.encode(dir, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        val sb = StringBuilder()
                        BufferedReader(InputStreamReader(inputStream)).use {
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                sb.append(inputLine)
                                inputLine = it.readLine()
                            }
                        }
                        val gson = Gson()
                        val type = object : TypeToken<String>() {}.type
                        result = gson.fromJson(sb.toString(), type)
                    }
                }
                if (result.contains("true")) {
                    val dialogFileExists = DialogFileExists.getInstance(dir, oldFileName, fileName)
                    dialogFileExists.show(supportFragmentManager, "dialogFileExists")
                } else {
                    sendSaveAsPostRequest("$dir/$fileName", oldFileName)
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun sendSaveAsPostRequest(dirToFile: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("saveas", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("dirToFile", "UTF-8") + "=" + URLEncoder.encode(dirToFile, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " ").replace(".txt", ".html", ignoreCase = true), "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        responseCodeS = responseCode
                    }
                }
                if (responseCodeS == 200) {
                    Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                        setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                        setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                        setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                        show()
                    }
                    val dialogDeliteHelp = DialogDeliteHelp.newInstance(fileName)
                    dialogDeliteHelp.show(supportFragmentManager, "dialogDeliteHelp")
                } else {
                    Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                        setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                        setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                        setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                        show()
                    }
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun getFilePostRequest(fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var result = ""
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("get", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        val sb = StringBuilder()
                        BufferedReader(InputStreamReader(inputStream)).use {
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                sb.append(inputLine)
                                inputLine = it.readLine()
                            }
                        }
                        val gson = Gson()
                        val type = object : TypeToken<String>() {}.type
                        result = gson.fromJson(sb.toString(), type)
                    }
                }

                if (fileName.contains(".htm")) {
                    binding.apisanne.setText(MainActivity.fromHtml(result, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    binding.actionP.visibility = View.GONE
                    binding.actionBr.visibility = View.GONE
                } else {
                    binding.apisanne.setText(result)
                }
                binding.apisanne.post {
                    binding.scrollView.smoothScrollBy(0, positionY)
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun findDirAsSave(fileName: String): Boolean {
        val fileList = ArrayList<DirList>()
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist0.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "akafist8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya9.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya12_9.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya13_8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya14_9.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya15_9.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_9.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_10.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya16_11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "bogashlugbovya17_8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "malitvy1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "malitvy2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec0.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ruzanec6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton1_budni.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton2_budni.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton3_budni.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton4_budni.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton5_budni.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton6_budni.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ton8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_bagarodzichnia_adpushchalnyia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_liccia_i_blaslavenne_xliabou.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia9.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia10.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia12.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia13.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia14.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia15.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia16.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia17.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia18.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia19.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia20.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia21.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia22.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia23.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_agulnaia24.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_mineia_sviatochnaia7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_na_kogny_dzen.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_niadzeli.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton1.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton3.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton4.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton5.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton6.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton7.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_ton8.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "viachernia_y_vialikim_poste.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", ".html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ju_8_11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "v_8_11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_8_11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ju_trojca_mineia_sviatochnaia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_trojca_mineia_sviatochnaia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "v_trojca_mineia_sviatochnaia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "ju_uzniasenne_mineia_sviatochnaia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_uzniasenne_mineia_sviatochnaia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "v_uzniasenne_mineia_sviatochnaia.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_1_10.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_21_11.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_2_2.html"))
        fileList.add(DirList("/admin/bogashlugbovya/", "l_ajcy_6_saborau.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_0.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_1.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_2.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_3.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_4.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_5.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_6.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_7.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_8.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_9.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_10.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_11.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_12.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_13.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_14.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_15.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_16.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_17.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_18.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_19.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_20.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_21.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_22.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_23.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_24.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_25.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_26.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_27.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_28.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_29.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_30.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_31.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_32.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_33.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_34.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_35.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_36.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_37.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_38.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_39.html"))
        fileList.add(DirList("/admin/prynagodnyia/", "prynagodnyia_40.html"))
        fileList.add(DirList("/admin/zmenyia_chastki_liturgii/", "zmenyia_chastki_miranosicay.html"))
        fileList.add(DirList("/admin/zmenyia_chastki_liturgii/", "zmenyia_chastki_pieramianiennie.html"))
        fileList.add(DirList("/admin/zmenyia_chastki_liturgii/", "zmenyia_chastki_samaranki.html"))
        fileList.add(DirList("/admin/zmenyia_chastki_liturgii/", "zmenyia_chastki_slepanarodz.html"))
        fileList.add(DirList("/admin/zmenyia_chastki_liturgii/", "zmenyia_chastki_tamash.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_anverpan.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_baranavichi.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_barysau.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_bielastok.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_brest.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_centr_dekan.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_gomel.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_grodno.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_ivachevichi.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_jodino.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_kaliningrad.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_kuryia.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_lida.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_londan.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_magilev.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_maladechna.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_marenagorka.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_mensk.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_navagrudak.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_orsha.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_pinsk.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_polachk.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_praga.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_rym.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_sanktpeterburg.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_slonim.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_usxod_dekan.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_vena.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_vilnia.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_vitebsk.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_warshava.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_zaslaue.html"))
        fileList.add(DirList("/admin/parafii_bgkc/", "dzie_zaxod_dekan.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_0.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_1.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_2.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_3.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_4.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_5.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_6.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_7.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_8.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_9.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_10.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_11.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_12.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_13.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_14.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bag_15.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_0.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_1.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_2.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_3.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_4.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_5.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_6.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_7.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_8.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_bel_9.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_0.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_1.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_2.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_3.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_4.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_5.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_6.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_7.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_8.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_9.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_10.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_11.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_12.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_13.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_14.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_15.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_16.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_17.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_18.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_19.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_20.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_kal_21.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_0.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_1.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_2.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_3.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_4.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_5.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_6.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_7.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_8.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_9.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_10.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_11.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_12.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_13.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_14.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_15.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_16.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_17.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_18.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_19.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_20.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_21.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_22.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_23.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_24.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_25.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_26.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_27.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_28.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_29.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_30.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_31.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_32.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_33.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_34.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_35.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_36.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_37.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_38.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_39.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_40.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_41.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_42.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_43.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_44.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_45.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_46.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_47.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_48.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_49.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_50.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_51.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_52.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_53.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_54.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_55.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_56.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_57.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_58.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_59.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_60.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_61.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_62.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_63.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_64.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_65.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_66.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_67.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_68.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_69.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_70.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_71.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_72.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_73.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_74.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_prasl_75.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_0.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_1.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_2.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_3.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_4.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_5.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_6.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_7.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_8.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_9.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_10.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_11.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_12.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_13.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_14.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_15.html"))
        fileList.add(DirList("/admin/pesny/", "pesny_taize_16.html"))
        var result = false
        val t1 = fileName.indexOf("(")
        if (t1 != -1 && t1 == 0) {
            val t2 = fileName.indexOf(")")
            val t3 = fileName.lastIndexOf(".")
            val newFileName = fileName.substring(1, t2) + fileName.substring(t3)
            fileList.forEach { dirlist ->
                if (dirlist.file == newFileName) {
                    result = true
                    sendSaveAsPostRequest(dirlist.dir + dirlist.file, fileName)
                    return@forEach
                }
            }
        }
        return result
    }

    private fun sendPostRequest(fileName: String, content: String, isSaveAs: Boolean = true) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("save", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                    reqParam += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        responseCodeS = responseCode
                    }
                }
                if (responseCodeS == 200) {
                    Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                        setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                        setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                        setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                        show()
                    }
                    if (isSaveAs) {
                        if (!findDirAsSave(fileName)) {
                            if (k.getBoolean("AdminDialogSaveAsHelp", true)) {
                                val dialodSaveAsHelp = DialogSaveAsHelp.newInstance(fileName)
                                dialodSaveAsHelp.show(supportFragmentManager, "dialodSaveAsHelp")
                            } else {
                                val dialogSaveAsFileExplorer = DialogSaveAsFileExplorer.getInstance(fileName)
                                dialogSaveAsFileExplorer.show(supportFragmentManager, "dialogSaveAsFileExplorer")
                            }
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
                binding.progressBar2.visibility = View.GONE
            }
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            if (fileName == "newFile.html") {
                val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance("newFile.html")
                dialogPasochnicaFileName.show(supportFragmentManager, "dialogPasochnicaFileName")
            } else {
                saveResult(fileName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveResult(fileName: String) {
        val text = binding.apisanne.text
        val gson = Gson()
        if (fileName.contains(".htm")) {
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
                sendPostRequest(fileName, gson.toJson(result))
            }
        } else {
            sendPostRequest(fileName, gson.toJson(text.toString()))
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_piasochnica, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private data class History(val spannable: Spannable, val editPosition: Int)

    private data class DirList(val dir: String, val file: String)
}