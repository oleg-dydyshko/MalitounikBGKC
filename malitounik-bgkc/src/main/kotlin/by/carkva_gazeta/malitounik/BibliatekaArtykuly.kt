package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import by.carkva_gazeta.malitounik.databinding.PasxaBinding
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File


class BibliatekaArtykuly : BaseActivity(), DialogFontSize.DialogFontSizeListener {
    private lateinit var binding: PasxaBinding
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences
    private val data = ArrayList<LinkedTreeMap<String, String>>()
    private val style = "img {max-width: 100%; height: auto; border:0; padding:0} @media (max-width: 990px) {img {height: auto !important}} @media (max-width: 660px) {img {margin: 10px 0 !important}}"

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onDialogFontSize(fontSize: Float) {
        val webSettings = binding.pasxa.settings
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
        val dzenNoch = getBaseDzenNoch()
        binding = PasxaBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val rubrika = intent.extras?.getInt("rubrika") ?: MainActivity.ARTGISTORYIACARKVY
        var title = resources.getText(R.string.bibliateka_gistoryia_carkvy)
        val path = when (rubrika) {
            MainActivity.ARTGISTORYIACARKVY -> {
                title = resources.getText(R.string.bibliateka_gistoryia_carkvy)
                "history.json"
            }
            MainActivity.ARTSVIATLOUSXODU -> {
                title = resources.getText(R.string.svitlo_usxodu)
                "svietlo_uschodu.json"
            }
            MainActivity.ARTCARKVAGRAMADSTVA -> {
                title = resources.getText(R.string.carkva_gramadstva)
                "gramadstva.json"
            }
            MainActivity.ARTARXIYNAVIN -> {
                title = resources.getText(R.string.arx_navin)
                "naviny.json"
            }
            MainActivity.ARTARXABVESTAK -> {
                title = resources.getText(R.string.arx_abvestak)
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
                @Suppress("DEPRECATION") WebSettingsCompat.setForceDark(binding.pasxa.settings, WebSettingsCompat.FORCE_DARK_ON)
            }
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }

        val builder = StringBuilder()
        if (dzenNoch) builder.append("<html><head><style type=\"text/css\">a {color:#f44336;} body{color: #fff; background-color: #303030;}$style</style></head><body>\n")
        else builder.append("<html><head><style type=\"text/css\">a {color:#d00505;} body{color: #000; background-color: #fff;}$style</style></head><body>\n")
        val localFile = File("$filesDir/$path")
        val gson = Gson()
        val text = localFile.readText()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
        data.addAll(gson.fromJson(text, type))
        val position = intent.extras?.getInt("position") ?: 0
        var textData = data[position]["str"] ?: ""
        if (dzenNoch) {
            textData = textData.replace("color: rgb(102, 0, 0)", "color: rgb(244, 67, 54)")
        }
        builder.append(textData)
        builder.append("</body></html>")
        val webSettings = binding.pasxa.settings
        webSettings.standardFontFamily = "sans-serif-condensed"
        webSettings.defaultFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT.toInt()
        webSettings.domStorageEnabled = true
        binding.pasxa.loadDataWithBaseURL(null, builder.toString(), "text/html", "utf-8", null)
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pasxa, menu)
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
        /*if (id == R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=5")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
            return true
        }*/
        return false
    }
}
