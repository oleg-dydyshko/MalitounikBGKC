package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import by.carkva_gazeta.malitounik.databinding.NavinyBinding

class Naviny : BaseActivity() {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private lateinit var binding: NavinyBinding

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = getBaseDzenNoch()
        binding = NavinyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                binding.viewWeb.reload()
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        if (dzenNoch) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                @Suppress("DEPRECATION") WebSettingsCompat.setForceDark(binding.viewWeb.settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            binding.toolbarprogress.setBackgroundResource(R.drawable.progress_bar_black)
        }
        val naviny = intent.extras?.getInt("naviny", 0) ?: 0
        binding.viewWeb.apply {
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            }
        }
        binding.viewWeb.webViewClient = MyWebViewClient()
        binding.viewWeb.webChromeClient = MyWebChromeClient()
        var error = false
        when (naviny) {
            0 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/")
                } else error = true
            }
            1 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?num=")
                } else error = true
            }
            2 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?his=")
                } else error = true
            }
            3 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?sva=")
                } else error = true
            }
            4 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?gra=")
                } else error = true
            }
            5 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?it=")
                } else error = true
            }
            6 -> {
                if (MainActivity.isNetworkAvailable()) {
                    binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?ik=")
                } else error = true
            }
        }
        if (error) {
            val dialogNoInternet = DialogNoInternet()
            dialogNoInternet.show(supportFragmentManager, "no_internet")
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.naviny, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        binding.viewWeb.onResume()
    }

    override fun onPause() {
        binding.viewWeb.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.viewWeb.destroy()
        super.onDestroy()
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_forward).isVisible = binding.viewWeb.canGoForward()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_forward) {
            binding.viewWeb.goForward()
            return true
        }
        if (id == R.id.action_update) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                binding.viewWeb.clearCache(true)
                binding.viewWeb.reload()
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.action_chrome) {
            val webBackForwardList = binding.viewWeb.copyBackForwardList()
            val webHistoryItem = webBackForwardList.currentItem
            onChrome(webHistoryItem?.url ?: "https://carkva-gazeta.by")
            return true
        }
        if (id == R.id.num) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?num=")
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.sva) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?sva=")
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.his) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?his=")
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.gra) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?gra=")
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.calendar) {
            val prefEditors = k.edit()
            prefEditors.putInt("id", R.id.label1)
            prefEditors.apply()
            val intent = Intent(this@Naviny, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.biblia) {
            val prefEditors = k.edit()
            prefEditors.putInt("id", R.id.label8)
            prefEditors.apply()
            val intent = Intent(this@Naviny, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.it) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?it=")
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.ik) {
            if (MainActivity.isNetworkAvailable()) {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?ik=")
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }
        if (id == R.id.bib) {
            if (MainActivity.checkmoduleResources()) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label2)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                intent.putExtra("site", true)
                startActivity(intent)
            } else {
                binding.viewWeb.loadUrl("https://carkva-gazeta.by/index.php?bib=")
            }
            return true
        }
        return false
    }

    private fun onChrome(url: String) {
        binding.viewWeb.stopLoading()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            try {
                intent.setPackage(null)
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                MainActivity.toastView(this, getString(R.string.error_ch2))
            }
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            if (binding.titleToolbar.text?.contains("https://") != true) {
                binding.titleToolbar.setText(binding.viewWeb.url)
                binding.titleToolbar.isCursorVisible = true
            }
        }
        binding.titleToolbar.setOnEditorActionListener { l, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                binding.viewWeb.loadUrl(l.text.toString())
            }
            false
        }
        binding.titleToolbar.setText(getString(R.string.carkva_gazeta))
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.titleToolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    override fun onBackPressed() {
        if (binding.viewWeb.canGoBack()) {
            binding.viewWeb.goBack()
            invalidateOptionsMenu()
        } else {
            super.onBackPressed()
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.toolbarprogress.setValue(newProgress)
            if (newProgress == 100) {
                val imm12 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.viewWeb.windowToken, 0)
                binding.toolbarprogress.visibility = View.INVISIBLE
                val title = view?.title ?: getString(R.string.carkva_gazeta)
                binding.titleToolbar.setText(title)
                if (binding.viewWeb.settings.cacheMode != WebSettings.LOAD_CACHE_ELSE_NETWORK) binding.viewWeb.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            } else {
                binding.toolbarprogress.visibility = View.VISIBLE
            }
            binding.titleToolbar.isCursorVisible = false
        }

        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            view ?: return false
            val href = view.handler.obtainMessage()
            view.requestFocusNodeHref(href)
            val url = href.data.getString("url") ?: ""
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
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("viber://")) {
                try {
                    val share = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    share.setPackage("com.viber.voip")
                    startActivity(share)
                } catch (e: ActivityNotFoundException) {
                    val dialog = DialogInstallDadatak.getInstance("Viber", "market://details?id=" + "com.viber.voip")
                    dialog.show(supportFragmentManager, "dialogInstallDadatak")
                }
                return true
            }
            if (url.contains("tg:msg_url")) {
                try {
                    val share = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    share.setPackage("org.telegram.messenger")
                    startActivity(share)
                } catch (e: ActivityNotFoundException) {
                    val dialog = DialogInstallDadatak.getInstance("Telegram", "market://details?id=" + "org.telegram.messenger")
                    dialog.show(supportFragmentManager, "dialogInstallDadatak")
                }
                return true
            }
            if (url.contains("https://malitounik.page.link/caliandar")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label1)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://carkva-gazeta.by/index.php?ie=14")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label104)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://carkva-gazeta.by/index.php?ie=16")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label102)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/biblijateka1")) {
                return setBiblioteka("https://carkva-gazeta.by/index.php?bib&rub=1")
            }
            if (url.contains("https://malitounik.page.link/biblijateka2")) {
                return setBiblioteka("https://carkva-gazeta.by/index.php?bib&rub=2")
            }
            if (url.contains("https://malitounik.page.link/biblijateka3")) {
                return setBiblioteka("https://carkva-gazeta.by/index.php?bib&rub=3")
            }
            if (url.contains("https://malitounik.page.link/biblijateka4")) {
                return setBiblioteka("https://carkva-gazeta.by/index.php?bib&rub=4")
            }
            if (url.contains("https://malitounik.page.link/biblijateka")) {
                return setBiblioteka("https://carkva-gazeta.by/index.php?bib")
            }
            if (url.contains("https://malitounik.page.link/biblija")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label8)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/bohasluzbovyja")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label3)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/akafisty")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label5)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/ruzaniec")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label6)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/malitvy")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label4)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/pesny_prasl")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label91)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/pesny_bel")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label92)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/pesny_bag")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label93)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/pesny_kal")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label94)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/pesny_taize")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label95)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (url.contains("https://malitounik.page.link/dzie_znajsci_hreka_katolikau_Bielarusi")) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label105)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            if (MainActivity.isNetworkAvailable()) {
                if (url.contains("translate.google.com") || url.contains("carkva-gazeta.by/download.php")) {
                    onChrome(url)
                } else {
                    invalidateOptionsMenu()
                    return false
                }
            } else {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            }
            return true
        }

        private fun setBiblioteka(rubrika: String): Boolean {
            if (MainActivity.checkmoduleResources()) {
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label2)
                prefEditors.apply()
                val intent = Intent(this@Naviny, MainActivity::class.java)
                intent.data = Uri.parse(rubrika)
                intent.putExtra("site", true)
                startActivity(intent)
            } else {
                if (MainActivity.isNetworkAvailable()) {
                    invalidateOptionsMenu()
                    return false
                } else {
                    val dialogNoInternet = DialogNoInternet()
                    dialogNoInternet.show(supportFragmentManager, "no_internet")
                }
            }
            return true
        }
    }
}