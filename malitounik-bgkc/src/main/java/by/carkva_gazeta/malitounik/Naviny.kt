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
import android.view.*
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_naviny.*
import java.util.*

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
        viewWeb.settings.javaScriptEnabled = true
        viewWeb.webViewClient = MyWebViewClient()
        viewWeb.webChromeClient = object : WebChromeClient() {
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
        }
        var error = false
        when (naviny) {
            0 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    searchHistory.add("https://carkva-gazeta.by/")
                    viewWeb.loadUrl("https://carkva-gazeta.by/")
                } else error = true
                title_toolbar.text = "«Царква» — беларуская грэка-каталіцкая газета"
            }
            1 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    searchHistory.add("https://carkva-gazeta.by/index.php?his=")
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?his=")
                } else error = true
                title_toolbar.text = "Гісторыя Царквы"
            }
            2 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    searchHistory.add("https://carkva-gazeta.by/index.php?sva=")
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?sva=")
                } else error = true
                title_toolbar.text = "Сьвятло ўсходу"
            }
            3 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    searchHistory.add("https://carkva-gazeta.by/index.php?gra=")
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?gra=")
                } else error = true
                title_toolbar.text = "Царква і грамадзтва"
            }
            4 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    searchHistory.add("https://carkva-gazeta.by/index.php?it=")
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?it=")
                } else error = true
                title_toolbar.text = "Катэдральны пляц"
            }
            5 -> {
                if (MainActivity.isNetworkAvailable(this)) {
                    searchHistory.add("https://carkva-gazeta.by/index.php?ik=")
                    viewWeb.loadUrl("https://carkva-gazeta.by/index.php?ik=")
                } else error = true
                title_toolbar.text = "Відэа"
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var error = false
        if (id == R.id.action_chrome) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchHistory[searchHistory.size - 1]))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                intent.setPackage(null)
                startActivity(intent)
            }
        }
        if (id == R.id.num) {
            if (MainActivity.isNetworkAvailable(this)) {
                searchHistory.add("https://carkva-gazeta.by/index.php?num=")
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?num=")
            } else error = true
            title_toolbar.text = "Навіны"
        }
        if (id == R.id.sva) {
            if (MainActivity.isNetworkAvailable(this)) {
                searchHistory.add("https://carkva-gazeta.by/index.php?sva=")
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?sva=")
            } else error = true
            title_toolbar.text = "Сьвятло ўсходу"
        }
        if (id == R.id.his) {
            if (MainActivity.isNetworkAvailable(this)) {
                searchHistory.add("https://carkva-gazeta.by/index.php?his=")
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?his=")
            } else error = true
            title_toolbar.text = "Гісторыя Царквы"
        }
        if (id == R.id.gra) {
            if (MainActivity.isNetworkAvailable(this)) {
                searchHistory.add("https://carkva-gazeta.by/index.php?gra=")
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?gra=")
            } else error = true
            title_toolbar.text = "Царква і грамадзтва"
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
                searchHistory.add("https://carkva-gazeta.by/index.php?it=")
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?it=")
            } else error = true
            title_toolbar.text = "Катэдральны пляц"
        }
        if (id == R.id.ik) {
            if (MainActivity.isNetworkAvailable(this)) {
                searchHistory.add("https://carkva-gazeta.by/index.php?ik=")
                viewWeb.loadUrl("https://carkva-gazeta.by/index.php?ik=")
            } else error = true
            title_toolbar.text = "Відэа"
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
        }

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
            var error = false
            /*if (!url.contains("carkva-gazeta.by")) {
                error = if (MainActivity.isNetworkAvailable(this@Naviny)) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } else true
            }*/
            if (MainActivity.isNetworkAvailable(this@Naviny)) {
                searchHistory.add(url)
                view.loadUrl(url)
            } else error = true
            if (error) {
                error()
            }
            return true
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
                    viewWeb.loadUrl(searchHistory[searchHistory.size - 1])
                } else {
                    onBackPressed()
                }
            } else {
                onBackPressed()
            }
        }
        return true
    }
}