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
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebChromeClient.FileChooserParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_naviny.*

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
    private var fullscreenPage = false
    private var dzenNoch = false
    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mUploadMessageArr: ValueCallback<Array<Uri>>? = null
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
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            if (savedInstanceState.getBoolean("getModule")) {
                linear.visibility = View.VISIBLE
            }
        }
        val naviny = kq.getInt("naviny", 0)
        val settings = viewWeb.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        viewWeb.webViewClient = MyWebViewClient()
        viewWeb.webChromeClient = MyWebChromeClient()
        var error = false
        when (naviny) {
            0 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/")
                } else error = true
            }
            1 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?num=")
                } else error = true
            }
            2 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?his=")
                } else error = true
            }
            3 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?sva=")
                } else error = true
            }
            4 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?gra=")
                } else error = true
            }
            5 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?it=")
                } else error = true
            }
            6 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?ik=")
                } else error = true
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

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_forward).isVisible = viewWeb.canGoForward()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var error = false
        if (id == R.id.action_forward) {
            viewWeb.goForward()
        }
        if (id == R.id.action_update) {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                viewWeb.reload()
            } else error = true
        }
        if (id == R.id.action_chrome) {
            val webBackForwardList = viewWeb.copyBackForwardList()
            val webHistoryItem = webBackForwardList.currentItem
            onChrome(webHistoryItem?.url ?: "https://carkva-gazeta.by")
        }
        if (id == R.id.num) {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?num=")
            } else error = true
        }
        if (id == R.id.sva) {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?sva=")
            } else error = true
        }
        if (id == R.id.his) {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?his=")
            } else error = true
        }
        if (id == R.id.gra) {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?gra=")
            } else error = true
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
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?it=")
            } else error = true
        }
        if (id == R.id.ik) {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?ik=")
            } else error = true
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

    private fun onChrome(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            intent.setPackage(null)
            startActivity(intent)
        }
    }

    private fun error() {
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
        title_toolbar.text = "«Царква» — беларуская грэка-каталіцкая газета"
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    override fun onBackPressed() {
        when {
            fullscreenPage -> {
                fullscreenPage = false
                show()
            }
            viewWeb.canGoBack() -> {
                viewWeb.goBack()
                invalidateOptionsMenu()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        // For Android 5.0+
        @SuppressLint("NewApi")
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
            mUploadMessageArr?.onReceiveValue(null)
            mUploadMessageArr = null
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

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            toolbarprogress.progress = newProgress
            if (newProgress == 100) {
                frameLayout2.visibility = View.INVISIBLE
                val title = view?.title ?: "«Царква» — беларуская грэка-каталіцкая газета"
                title_toolbar.text = title
                if (viewWeb.settings.cacheMode != WebSettings.LOAD_CACHE_ELSE_NETWORK)
                    viewWeb.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            } else {
                frameLayout2.visibility = View.VISIBLE
            }
        }
    }

    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("https://malitounik.page.link/caliandar")) {
                val prefEditors = kq.edit()
                prefEditors.putInt("id", R.id.label1)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/biblija")) {
                val prefEditors = kq.edit()
                prefEditors.putInt("id", R.id.label8)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://carkva-gazeta.by/index.php?bib=")) {
                if (MainActivity.checkmoduleResources(this@Naviny)) {
                    if (MainActivity.checkmodulesBiblijateka(this@Naviny)) {
                        val intent = Intent(this@Naviny, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                        intent.putExtra("site", true)
                        startActivity(intent)
                    } else {
                        MainActivity.downloadDynamicModule(this@Naviny)
                    }
                    return true
                }
            }
            if (MainActivity.isNetworkAvailable(this@Naviny)) {
                if (url.contains("translate.google.com")) {
                    onChrome(url)
                } else {
                    view.loadUrl(url)
                    invalidateOptionsMenu()
                }
            } else {
                error()
            }
            return true
        }
    }
}