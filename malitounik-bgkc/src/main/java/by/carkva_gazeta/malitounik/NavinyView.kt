package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_naviny.*
import java.util.regex.Pattern

class NavinyView : AppCompatActivity() {
    private var dzenNoch = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val kq = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = kq.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        setContentView(R.layout.activity_naviny)
        setTollbarTheme()
        if (dzenNoch) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val mnemonics = ArrayMap<String, String>()
        mnemonics["&amp;"] = "\u0026"
        mnemonics["&lt;"] = "\u003C"
        mnemonics["&gt;"] = "\u003E"
        mnemonics["&laquo;"] = "\u00AB"
        mnemonics["&raquo;"] = "\u00BB"
        mnemonics["&nbsp;"] = "\u0020"
        mnemonics["&mdash;"] = "\u0020-\u0020"
        var output = intent.extras?.getString("url") ?: ""
        mnemonics.forEach {
            val matcher = Pattern.compile(it.key).matcher(output)
            output = matcher.replaceAll(mnemonics[it.key] ?: it.key)
        }
        /*for (key in mnemonics.keys) {
            val matcher = Pattern.compile(key).matcher(Objects.requireNonNull(output))
            output = matcher.replaceAll(Objects.requireNonNull(mnemonics[key]))
        }*/
        title_toolbar.text = output
        viewWeb.settings.javaScriptEnabled = true
        viewWeb.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("https://malitounik.page.link/caliandar")) {
                    val prefEditors = kq.edit()
                    prefEditors.putInt("id", R.id.label1)
                    prefEditors.apply()
                    val intent = Intent(this@NavinyView, MainActivity::class.java)
                    startActivity(intent)
                    return false
                }
                if (url.contains("https://malitounik.page.link/biblija")) {
                    val prefEditors = kq.edit()
                    prefEditors.putInt("id", R.id.label8)
                    prefEditors.apply()
                    val intent = Intent(this@NavinyView, MainActivity::class.java)
                    startActivity(intent)
                    return false
                }
                if (url.contains("https://m.carkva-gazeta.by/index.php?bib=")) {
                    if (MainActivity.checkmoduleResources(this@NavinyView)) {
                        if (MainActivity.checkmodulesBiblijateka(this@NavinyView)) {
                            val intent = Intent(this@NavinyView, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                            intent.putExtra("site", true)
                            startActivity(intent)
                        } else {
                            MainActivity.downloadDynamicModule(this@NavinyView)
                        }
                        return false
                    }
                }
                if (MainActivity.isNetworkAvailable(this@NavinyView)) {
                    if (Uri.parse(url).host?.contains("m.carkva-gazeta.by") == true) {
                        return false
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                } else {
                    val dialogNoInternet = DialogNoInternet()
                    dialogNoInternet.show(supportFragmentManager, "no_internet")
                }
                return true
            }
        }
        viewWeb.loadDataWithBaseURL("file:///android_asset/", intent.extras?.getString("htmlData"), "text/html", "UTF-8", null)
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
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }
}