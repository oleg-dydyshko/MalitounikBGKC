package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_naviny.*
import java.io.*
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class Naviny : AppCompatActivity() {
    private val mHideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        relative.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private val mShowPart2Runnable = Runnable {
        supportActionBar?.show()
    }
    private lateinit var kq: SharedPreferences
    private val searchHistory = ArrayList<String>()
    private var mUrl: String = ""
    private val arrayList = ArrayList<ArrayList<String>>()
    private var fullscreenPage = false
    private var dzenNoch = false
    private var errorInternet = false
    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mUploadMessageArr: ValueCallback<Array<Uri>>? = null
    private var mLastClickTime = 0L
    private val uiAnimationDelay = 300L

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kq = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = kq.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        setContentView(R.layout.activity_naviny)
        if (dzenNoch) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
            }
            viewWeb.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
        }
        dynamic.adapter = NavinyAdapter(this)
        dynamic.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val re = arrayList[position]
            var htmlData = readerFile(File(filesDir.toString() + "/Site/" + re[0]))
            htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
            val intent = Intent(this, NavinyView::class.java)
            intent.putExtra("htmlData", htmlData)
            intent.putExtra("url", re[1])
            startActivity(intent)
        }
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            if (savedInstanceState.getBoolean("getModule")) {
                linear.visibility = View.VISIBLE
            }
        }
        val naviny = kq.getInt("naviny", 0)
        viewWeb.settings.javaScriptEnabled = true
        viewWeb.settings.domStorageEnabled = true
        viewWeb.webViewClient = MyWebViewClient()
        viewWeb.webChromeClient = object : WebChromeClient() {
            // For Android 4.1+
            /*fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String?, capture: String?) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = acceptType
                startActivityForResult(Intent.createChooser(i, "SELECT"), 100)
            }*/

            // For Android 5.0+
            @SuppressLint("NewApi")
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                if (mUploadMessageArr != null) {
                    mUploadMessageArr?.onReceiveValue(null)
                    mUploadMessageArr = null
                }
                mUploadMessageArr = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, 101)
                } catch (e: ActivityNotFoundException) {
                    mUploadMessageArr = null
                    return false
                }
                return true
            }
        }
        var error = false
        when (naviny) {
            0 -> {
                if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/")
                title_toolbar.text = "«Царква» — беларуская грэка-каталіцкая газета" //https://m.carkva-gazeta.by/
                val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_") //
                if (MainActivity.isNetworkAvailable(this)) {
                    writeFile("https://m.carkva-gazeta.by/")
                }
                when {
                    MainActivity.isNetworkAvailable(this) -> {
                        viewWeb.loadUrl("https://m.carkva-gazeta.by/")
                    }
                    file.exists() -> {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    }
                    else -> error = true
                }
            }
            1 -> {
                if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?his=")
                title_toolbar.text = "Гісторыя Царквы" //https://m.carkva-gazeta.by/index.php?his=
                val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?his=") //
                if (MainActivity.isNetworkAvailable(this)) {
                    writeFile("https://m.carkva-gazeta.by/index.php?his=")
                }
                when {
                    MainActivity.isNetworkAvailable(this) -> {
                        viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?his=")
                    }
                    file.exists() -> {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    }
                    else -> error = true
                }
            }
            2 -> {
                if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?sva=")
                title_toolbar.text = "Сьвятло ўсходу" //https://m.carkva-gazeta.by/index.php?sva=
                val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?sva=") //
                if (MainActivity.isNetworkAvailable(this)) {
                    writeFile("https://m.carkva-gazeta.by/index.php?sva=")
                }
                when {
                    MainActivity.isNetworkAvailable(this) -> {
                        viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?sva=")
                    }
                    file.exists() -> {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    }
                    else -> error = true
                }
            }
            3 -> {
                if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?gra=")
                title_toolbar.text = "Царква і грамадзтва" //https://m.carkva-gazeta.by/index.php?gra=
                val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?gra=")
                if (MainActivity.isNetworkAvailable(this)) {
                    writeFile("https://m.carkva-gazeta.by/index.php?gra=")
                }
                when {
                    MainActivity.isNetworkAvailable(this) -> {
                        viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?gra=")
                    }
                    file.exists() -> {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    }
                    else -> error = true
                }
            }
            4 -> {
                if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?it=")
                title_toolbar.text = "Катэдральны пляц" //https://m.carkva-gazeta.by/index.php?it=
                val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?it=")
                if (MainActivity.isNetworkAvailable(this)) {
                    writeFile("https://m.carkva-gazeta.by/index.php?it=")
                }
                when {
                    MainActivity.isNetworkAvailable(this) -> {
                        viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?it=")
                    }
                    file.exists() -> {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    }
                    else -> error = true
                }
            }
            5 -> {
                if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?ik=")
                title_toolbar.text = "Відэа" //https://m.carkva-gazeta.by/index.php?ik=
                val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?ik=")
                if (MainActivity.isNetworkAvailable(this)) {
                    writeFile("https://m.carkva-gazeta.by/index.php?ik=")
                }
                when {
                    MainActivity.isNetworkAvailable(this) -> {
                        viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?ik=")
                    }
                    file.exists() -> {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    }
                    else -> error = true
                }
            }
        }
        if (error) {
            error()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 150 && resultCode == Activity.RESULT_OK) {
            MainActivity.downloadDynamicModule(this)
        }
        if (requestCode == 100) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            mUploadMessage?.onReceiveValue(result)
            mUploadMessage = null
        } else if (requestCode == 101) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessageArr?.onReceiveValue(FileChooserParams.parseResult(resultCode, data))
            }
            mUploadMessageArr = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.site, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        if (linear.visibility == View.VISIBLE) {
            outState.putBoolean("getModule", true)
        } else {
            outState.putBoolean("getModule", false)
        }
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, uiAnimationDelay)
    }

    private fun show() {
        relative.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, uiAnimationDelay)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        if (fullscreenPage) hide()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    private fun writeFile(url: String) {
        if (Uri.parse(url).host?.contains("m.carkva-gazeta.by") == true) {
            Thread(Runnable {
                try {
                    val myUrl = URL(url)
                    var filename = url
                    filename = filename.replace("/", "_")
                    val inpstr = myUrl.openStream()
                    val file = File("$filesDir/Site/$filename")
                    val outputStream = FileOutputStream("$filesDir/Site/$filename")
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inpstr.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.close()
                    val htmlData = readerFile(file)
                    if (htmlData.contains("iframe")) {
                        val r1 = htmlData.indexOf("<iframe")
                        val r2 = htmlData.indexOf("</iframe>", r1 + 7)
                        var s2 = htmlData.substring(r1, r2 + 9)
                        if (!s2.contains("https://")) {
                            s2 = s2.replace("//", "https://")
                        }
                        val s1 = htmlData.substring(0, r1)
                        val s3 = htmlData.substring(r2 + 9)
                        val fileNew = File("$filesDir/Site/$filename")
                        val output: FileWriter
                        output = FileWriter(fileNew)
                        output.write(s1 + s2 + s3)
                        output.close()
                    }
                } catch (t: Throwable) {
                    DialogNoInternet().show(supportFragmentManager, "no_internet")
                }
            }).start()
        }
    }

    private fun readerFile(file: File): String {
        val inputStream = FileReader(file)
        val reader = BufferedReader(inputStream)
        return reader.readText()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (errorInternet) {
            menu.findItem(R.id.num).isVisible = false
            menu.findItem(R.id.sva).isVisible = false
            menu.findItem(R.id.his).isVisible = false
            menu.findItem(R.id.gra).isVisible = false
            menu.findItem(R.id.calendar).isVisible = false
            menu.findItem(R.id.biblia).isVisible = false
            menu.findItem(R.id.it).isVisible = false
            menu.findItem(R.id.ik).isVisible = false
            menu.findItem(R.id.bib).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var error = false
        if (id == R.id.num) {
            if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?num=")
            title_toolbar.text = "Навіны" //https://m.carkva-gazeta.by/
            val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?num=") //
            if (MainActivity.isNetworkAvailable(this)) {
                writeFile("https://m.carkva-gazeta.by/index.php?num=")
            }
            when {
                MainActivity.isNetworkAvailable(this) -> {
                    viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?num=")
                }
                file.exists() -> {
                    var htmlData = readerFile(file)
                    htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                    viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
                else -> error = true
            }
        }
        if (id == R.id.sva) {
            if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?sva=")
            title_toolbar.text = "Сьвятло ўсходу" //https://m.carkva-gazeta.by/index.php?sva=
            val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?sva=") //
            if (MainActivity.isNetworkAvailable(this)) {
                writeFile("https://m.carkva-gazeta.by/index.php?sva=")
            }
            when {
                MainActivity.isNetworkAvailable(this) -> {
                    viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?sva=")
                }
                file.exists() -> {
                    var htmlData = readerFile(file)
                    htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                    viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
                else -> error = true
            }
        }
        if (id == R.id.his) {
            if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?his=")
            title_toolbar.text = "Гісторыя Царквы" //https://m.carkva-gazeta.by/index.php?his=
            val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?his=") //
            if (MainActivity.isNetworkAvailable(this)) {
                writeFile("https://m.carkva-gazeta.by/index.php?his=")
            }
            when {
                MainActivity.isNetworkAvailable(this) -> {
                    viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?his=")
                }
                file.exists() -> {
                    var htmlData = readerFile(file)
                    htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                    viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
                else -> error = true
            }
        }
        if (id == R.id.gra) {
            if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?gra=")
            title_toolbar.text = "Царква і грамадзтва" //https://m.carkva-gazeta.by/index.php?gra=
            val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?gra=")
            if (MainActivity.isNetworkAvailable(this)) {
                writeFile("https://m.carkva-gazeta.by/index.php?gra=")
            }
            when {
                MainActivity.isNetworkAvailable(this) -> {
                    viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?gra=")
                }
                file.exists() -> {
                    var htmlData = readerFile(file)
                    htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                    viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
                else -> error = true
            }
        }
        if (id == R.id.calendar) {
            val prefEditors = kq.edit()
            prefEditors.putInt("id", R.id.label1)
            prefEditors.apply()
            val intent = Intent(this@Naviny, MainActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.biblia) {
            val prefEditors = kq.edit()
            prefEditors.putInt("id", R.id.label8)
            prefEditors.apply()
            val intent = Intent(this@Naviny, MainActivity::class.java)
            startActivity(intent)
        }
        if (id == R.id.it) {
            if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?it=")
            title_toolbar.text = "Катэдральны пляц" //https://m.carkva-gazeta.by/index.php?it=
            val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?it=")
            if (MainActivity.isNetworkAvailable(this)) {
                writeFile("https://m.carkva-gazeta.by/index.php?it=")
            }
            when {
                MainActivity.isNetworkAvailable(this) -> {
                    viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?it=")
                }
                file.exists() -> {
                    var htmlData = readerFile(file)
                    htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                    viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
                else -> error = true
            }
        }
        if (id == R.id.ik) {
            if (MainActivity.isNetworkAvailable(this)) searchHistory.add("https://m.carkva-gazeta.by/index.php?ik=")
            title_toolbar.text = "Відэа" //https://m.carkva-gazeta.by/index.php?ik=
            val file = File("$filesDir/Site/http:__m.carkva-gazeta.by_index.php?ik=")
            if (MainActivity.isNetworkAvailable(this)) {
                writeFile("https://m.carkva-gazeta.by/index.php?ik=")
            }
            when {
                MainActivity.isNetworkAvailable(this) -> {
                    viewWeb.loadUrl("https://m.carkva-gazeta.by/index.php?ik=")
                }
                file.exists() -> {
                    var htmlData = readerFile(file)
                    htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                    viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                }
                else -> error = true
            }
        }
        if (id == R.id.bib) {
            if (MainActivity.checkmoduleResources(this)) {
                if (MainActivity.checkmodulesBiblijateka(this)) {
                    val intent = Intent(this, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                    intent.putExtra("site", true)
                    startActivity(intent)
                } else {
                    MainActivity.downloadDynamicModule(this)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }
        }
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_fullscreen) {
            if (kq.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        if (error) {
            error()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            val title = view.title
            title_toolbar.text = title
            if (MainActivity.isNetworkAvailable(this@Naviny)) {
                writeFile(url)
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("https://malitounik.page.link/caliandar")) {
                val prefEditors = kq.edit()
                prefEditors.putInt("id", R.id.label1)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return false
            }
            if (url.contains("https://malitounik.page.link/biblija")) {
                val prefEditors = kq.edit()
                prefEditors.putInt("id", R.id.label8)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return false
            }
            if (url.contains("https://m.carkva-gazeta.by/index.php?bib=")) {
                if (MainActivity.checkmoduleResources(this@Naviny)) {
                    if (MainActivity.checkmodulesBiblijateka(this@Naviny)) {
                        val intent = Intent(this@Naviny, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                        intent.putExtra("site", true)
                        startActivity(intent)
                    } else {
                        MainActivity.downloadDynamicModule(this@Naviny)
                    }
                    return false
                }
            }
            var error = false
            try {
                if (!url.contains("m.carkva-gazeta.by")) {
                    error = if (MainActivity.isNetworkAvailable(this@Naviny)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view.context.startActivity(intent)
                        return true
                    } else true
                }
                mUrl = url
                if (MainActivity.isNetworkAvailable(this@Naviny)) searchHistory.add(url)
                var filename = url
                filename = filename.replace("/", "_")
                val file = File("$filesDir/Site/$filename")
                if (view.url == "https://m.carkva-gazeta.by/") {
                    when {
                        MainActivity.isNetworkAvailable(this@Naviny) -> {
                            view.loadUrl(url)
                        }
                        file.exists() -> {
                            var htmlData = readerFile(file)
                            htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                            viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                        }
                        else -> error = true
                    }
                } else {
                    if (kq.getInt("trafic", 0) == 0) {
                        when {
                            MainActivity.isNetworkAvailable(this@Naviny) -> {
                                view.loadUrl(url)
                            }
                            file.exists() -> {
                                var htmlData = readerFile(file)
                                htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                                viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                            }
                            else -> error = true
                        }
                    }
                    if (kq.getInt("trafic", 0) == 1) {
                        when {
                            file.exists() -> {
                                var htmlData = readerFile(file)
                                htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                                viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                            }
                            MainActivity.isNetworkAvailable(this@Naviny) -> {
                                view.loadUrl(url)
                            }
                            else -> error = true
                        }
                    }
                }
                if (error) {
                    error()
                }
            } catch (e: ActivityNotFoundException) {
                File("$filesDir/Site").walk().forEach {
                    if (it.isFile)
                        it.delete()
                }
                /*val dir1 = File("$filesDir/Site")
                val dirContents1 = dir1.list()
                for (aDirContents1 in dirContents1) {
                    File("$filesDir/Site/$aDirContents1").delete()
                }*/
            }
            return true
        }
    }

    private fun error() {
        errorInternet = true
        invalidateOptionsMenu()
        arrayList.clear()
        val files = ArrayList<File>()
        /*val dir = File("$filesDir/Site")
        for (i in 0 until dir.list().length) {
            files.add(File(filesDir.toString() + "/Site/" + dir.list()[i]))
        }*/
        File("$filesDir/Site").walk().forEach {
            if (it.isFile)
                files.add(it)
        }
        files.sortWith(Comparator { o1: File, o2: File ->
            o1.lastModified().compareTo(o2.lastModified())
        })
        files.reverse()
        //Collections.sort(files, Comparator<*> { o1: Any, o2: Any -> compare(o1.lastModified(), o2.lastModified()) })
        //Collections.reverse(files)
        for (file1 in files) {
            var res: String
            val htmlData = readerFile(file1)
            val seaN = htmlData.indexOf("<title>")
            val seaK = htmlData.indexOf("</title>")
            res = htmlData.substring(seaN + 7, seaK).trim()
            val arrayList1 = ArrayList<String>()
            arrayList1.add(file1.name)
            arrayList1.add(res)
            arrayList.add(arrayList1)
        }
        dynamic.visibility = View.VISIBLE
        viewWeb.visibility = View.GONE
        title_toolbar.setText(R.string.keshstar)
        val dialogNoInternet = DialogNoInternet()
        dialogNoInternet.show(supportFragmentManager, "no_internet")
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK && searchHistory.size > 0) {
                searchHistory.removeAt(searchHistory.size - 1)
                if (searchHistory.size > 0) {
                    val filenameUrl = searchHistory[searchHistory.size - 1]
                    val filename = filenameUrl.replace("/", "_")
                    val file = File("$filesDir/Site/$filename")
                    if (file.exists()) {
                        var htmlData = readerFile(file)
                        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"carkva.css\" /><script src=\"jquery-3.4.1.min.js\"></script>$htmlData"
                        viewWeb.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null)
                    } else {
                        viewWeb.loadUrl(mUrl)
                    }
                } else {
                    onBackPressed()
                }
            } else {
                onBackPressed()
            }
        }
        return true
    }

    internal inner class NavinyAdapter(context: Context) : ArrayAdapter<ArrayList<String>>(context, R.layout.simple_list_item_2, arrayList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                rootView = this@Naviny.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            val re = arrayList[position]
            val mnemonics = ArrayMap<String, String>()
            mnemonics["&amp;"] = "\u0026"
            mnemonics["&lt;"] = "\u003C"
            mnemonics["&gt;"] = "\u003E"
            mnemonics["&laquo;"] = "\u00AB"
            mnemonics["&raquo;"] = "\u00BB"
            mnemonics["&nbsp;"] = "\u0020"
            mnemonics["&mdash;"] = "\u0020-\u0020"
            var output = re[1]
            mnemonics.forEach {
                val matcher = Pattern.compile(it.key).matcher(output)
                output = matcher.replaceAll(mnemonics[it.key] ?: it.key)
            }
            /*for (key in mnemonics.keys) {
                val matcher = Pattern.compile(key).matcher(output)
                output = matcher.replaceAll(mnemonics[key])
            }*/
            if (dzenNoch) viewHolder.text?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text?.text = output
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}