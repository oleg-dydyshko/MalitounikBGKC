package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import by.carkva_gazeta.malitounik.databinding.TonPlayBinding
import kotlinx.coroutines.*

class TonPlay : BaseActivity() {
    private lateinit var binding: TonPlayBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = TonPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
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
        val ton = intent?.extras?.getInt("ton", 1) ?: 1
        if (dzenNoch) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                @Suppress("DEPRECATION") WebSettingsCompat.setForceDark(binding.viewWeb.settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getString(R.string.ton, ton.toString())
        when(ton) {
            1 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-1?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            2 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-2?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            3 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-3?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            4 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-4?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            5 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-5?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            6 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-6?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            7 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-7?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
            8 -> binding.viewWeb.loadUrl("https://soundcloud.com/24dwbqqpu9sk/trapar-8?in=24dwbqqpu9sk/sets/trapary-bgkts&utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.onBackPressed()
            return true
        }
        return false
    }

    private inner class MyWebViewClient : WebViewClient() {
         @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }
    }
}
