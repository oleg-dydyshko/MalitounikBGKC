package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import kotlinx.android.synthetic.main.naviny.*

class Naviny : AppCompatActivity() {

    private lateinit var kq: SharedPreferences
    private var dzenNoch = false

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kq = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = kq.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (kq.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.naviny)
        swipeRefreshLayout.setOnRefreshListener {
            if (MainActivity.isNetworkAvailable(this)) {
                viewWeb.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                viewWeb.reload()
            } else error()
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        if (dzenNoch) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(viewWeb.settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
            }
            viewWeb.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
            toolbarprogress.setBackgroundResource(R.drawable.progress_bar_black)
        }
        val naviny = kq.getInt("naviny", 0)
        viewWeb.apply {
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                //setAppCacheEnabled(true)
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.naviny, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        viewWeb.onResume()
    }

    override fun onPause() {
        viewWeb.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        viewWeb?.destroy()
        super.onDestroy()
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
                val prefEditors = kq.edit()
                prefEditors.putInt("id", R.id.label2)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                intent.putExtra("site", true)
                startActivity(intent)
            } else {
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?bib=")
            }
        }
        if (error) {
            error()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onChrome(url: String) {
        viewWeb.stopLoading()
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
        }
    }

    override fun onBackPressed() {
        if (viewWeb.canGoBack()) {
            viewWeb.goBack()
            invalidateOptionsMenu()
        } else {
            super.onBackPressed()
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            toolbarprogress.setValue(newProgress)
            if (newProgress == 100) {
                toolbarprogress.visibility = View.INVISIBLE
                val title = view?.title ?: "«Царква» — беларуская грэка-каталіцкая газета"
                title_toolbar.text = title
                if (viewWeb.settings.cacheMode != WebSettings.LOAD_CACHE_ELSE_NETWORK)
                    viewWeb.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            } else {
                toolbarprogress.visibility = View.VISIBLE
            }
        }

        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            view?: return false
            val href = view.handler.obtainMessage()
            view.requestFocusNodeHref(href)
            val url = href.data.getString("url")?: ""
            onChrome(url)
            return true
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            return false
        }

        override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
            return false
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
                    val prefEditors = kq.edit()
                    prefEditors.putInt("id", R.id.label2)
                    prefEditors.apply()
                    val intent = Intent(this@Naviny, MainActivity::class.java)
                    intent.putExtra("site", true)
                    startActivity(intent)
                } else {
                    if (MainActivity.isNetworkAvailable(this@Naviny)) {
                        //view.loadUrl(url)
                        invalidateOptionsMenu()
                        return false
                    } else {
                        error()
                    }
                }
                return true
            }
            if (MainActivity.isNetworkAvailable(this@Naviny)) {
                if (url.contains("translate.google.com") || url.contains("carkva-gazeta.by/download.php")) {
                    onChrome(url)
                } else {
                    //view.loadUrl(url)
                    invalidateOptionsMenu()
                    return false
                }
            } else {
                error()
            }
            return true
        }
    }
}