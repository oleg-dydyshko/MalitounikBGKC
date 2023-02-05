package by.carkva_gazeta.malitounik

import android.content.*
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import by.carkva_gazeta.malitounik.databinding.BibliatekaArtykulyBinding
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File


class BibliatekaArtykuly : BaseActivity(), DialogFontSize.DialogFontSizeListener, DialogHelpShare.DialogHelpShareListener {
    private var fullscreenPage = false
    private lateinit var binding: BibliatekaArtykulyBinding
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences
    private val data = ArrayList<LinkedTreeMap<String, String>>()
    private val dzenNoch get() = getBaseDzenNoch()
    private var position = 0
    private var rubrika = 1
    private var path = "history.json"
    private val style = "img {max-width: 100%; height: auto; border:0; padding:0} @media (max-width: 990px) {img {height: auto !important}} @media (max-width: 660px) {img {margin: 10px 0 !important}} .article_naviny_data {text-align: left; color: #999; font-size: 12px} .alt2 { text-align:right; font-weight:700; font-style:italic; margin-top:5px}"
    private val adminUpdateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                val localFile = File("$filesDir/Artykuly/$path")
                Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                    MainActivity.toastView(this@BibliatekaArtykuly, getString(R.string.error))
                }.await()
                load()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onDialogFontSize(fontSize: Float) {
        val webSettings = binding.webView.settings
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.blockNetworkImage = true
        webSettings.loadsImagesAutomatically = true
        webSettings.setGeolocationEnabled(false)
        webSettings.setNeedInitialFocus(false)
        webSettings.defaultFontSize = fontSize.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = BibliatekaArtykulyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
        } else {
            fullscreenPage = chin.getBoolean("fullscreenPage", false)
        }
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
        rubrika = intent.extras?.getInt("rubrika") ?: 1
        var title = resources.getStringArray(R.array.artykuly)[rubrika]
        path = when (rubrika) {
            0 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "svietlo_uschodu.json"
            }
            1 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "history.json"
            }
            2 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "gramadstva.json"
            }
            3 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "videa.json"
            }
            4 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "adkaz.json"
            }
            5 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2022.json"
            }
            6 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2021.json"
            }
            7 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2020.json"
            }
            8 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2019.json"
            }
            9 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2018.json"
            }
            10 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2017.json"
            }
            11 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2016.json"
            }
            12 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2015.json"
            }
            13 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2014.json"
            }
            14 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2013.json"
            }
            15 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2012.json"
            }
            16 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2011.json"
            }
            17 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2010.json"
            }
            18 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2009.json"
            }
            19 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "naviny2008.json"
            }
            20 -> {
                title = resources.getStringArray(R.array.artykuly)[rubrika]
                "abvestki.json"
            }
            else -> "history.json"
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = title
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                @Suppress("DEPRECATION") WebSettingsCompat.setForceDark(binding.webView.settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        load()
        val webSettings = binding.webView.settings
        webSettings.standardFontFamily = "sans-serif-condensed"
        webSettings.defaultFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT.toInt()
        webSettings.domStorageEnabled = true
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
    }

    private fun load() {
        try {
            val builder = StringBuilder()
            if (dzenNoch) builder.append("<html><head><style type=\"text/css\">a {color:#f44336;} body{color: #fff; background-color: #303030;}$style</style></head><body>\n")
            else builder.append("<html><head><style type=\"text/css\">a {color:#d00505;} body{color: #000; background-color: #fff;}$style</style></head><body>\n")
            val gson = Gson()
            val localFile = File("$filesDir/Artykuly/$path")
            val text = localFile.readText()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
            data.clear()
            data.addAll(gson.fromJson(text, type))
            position = intent.extras?.getInt("position") ?: 0
            var textData = data[position]["str"] ?: ""
            if (dzenNoch) {
                textData = textData.replace("color: rgb(102, 0, 0)", "color: rgb(244, 67, 54)")
                textData = textData.replace("color:rgb(102, 0, 0)", "color: rgb(244, 67, 54)")
            }
            builder.append(textData)
            builder.append("</body></html>")
            binding.webView.loadDataWithBaseURL(null, builder.toString(), "text/html", "utf-8", null)
        } catch (_: Throwable) {
            MainActivity.toastView(this, getString(R.string.error_ch2))
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

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR) {
            MainActivity.dialogVisable = false
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.artykuly, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_dzen_noch).isChecked = getBaseDzenNoch()
        if (chin.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
        menu.findItem(R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val prefEditor = chin.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
            return true
        }
        if (id == R.id.action_fullscreen) {
            hide()
            return true
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == R.id.action_share) {
            val textData = data[position]["str"] ?: ""
            val sent = MainActivity.fromHtml(textData).toString()
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.copy_text), sent)
            clipboard.setPrimaryClip(clip)
            MainActivity.toastView(this, getString(R.string.copy_text), Toast.LENGTH_LONG)
            if (chin.getBoolean("dialogHelpShare", true)) {
                val dialog = DialogHelpShare.getInstance(sent)
                dialog.show(supportFragmentManager, "DialogHelpShare")
            } else {
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, sent)
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, title))
            }
            return true
        }
        if (id == R.id.action_carkva) {
            val intent = Intent()
            intent.setClassName(this, MainActivity.ARTYKLY)
            intent.putExtra("rybrika", rubrika)
            intent.putExtra("position", position)
            adminUpdateLauncher.launch(intent)
        }
        return false
    }

    override fun sentShareText(shareText: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, title))
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.VISIBLE
        binding.actionBack.animation = animation
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
    }
}
