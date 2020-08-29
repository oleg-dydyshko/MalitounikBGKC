package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import kotlinx.android.synthetic.main.activity_parafii_bgkc.*
import java.io.BufferedReader
import java.io.InputStreamReader

class ParafiiBgkc : AppCompatActivity() {
    private var bgkc = 0
    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parafii_bgkc)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.parafii)
        val webSettings = WebView.settings
        webSettings.standardFontFamily = "sans-serif-condensed"
        webSettings.defaultFontSize = fontBiblia.toInt()
        val prynagodnyia = intent.extras?.getInt("bgkc_parafii", 0) ?: 0
        bgkc = intent.extras?.getInt("bgkc", 0) ?: 0
        var inputStream = resources.openRawResource(R.raw.dzie_kuryia)
        when (bgkc) {
            0 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_kuryia)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_centr_dekan)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_usxod_dekan)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_zaxod_dekan)
            }
            1 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_centr_dekan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_barysau)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_jodino)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_zaslaue)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_maladechna)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_marenagorka)
                if (prynagodnyia == 6) inputStream = resources.openRawResource(R.raw.dzie_mensk)
            }
            2 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_usxod_dekan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_vitebsk)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_orsha)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_gomel)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_polachk)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_magilev)
            }
            3 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_zaxod_dekan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_baranavichi)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_brest)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_grodno)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_ivachevichi)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_lida)
                if (prynagodnyia == 6) inputStream = resources.openRawResource(R.raw.dzie_navagrudak)
                if (prynagodnyia == 7) inputStream = resources.openRawResource(R.raw.dzie_pinsk)
                if (prynagodnyia == 8) inputStream = resources.openRawResource(R.raw.dzie_slonim)
            }
            4 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_anverpan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_londan)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_warshava)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_vilnia)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_vena)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_kaliningrad)
                if (prynagodnyia == 6) inputStream = resources.openRawResource(R.raw.dzie_praga)
                if (prynagodnyia == 7) inputStream = resources.openRawResource(R.raw.dzie_rym)
                if (prynagodnyia == 8) inputStream = resources.openRawResource(R.raw.dzie_sanktpeterburg)
            }
        }
        val inputStreamReader = InputStreamReader(inputStream)
        val reader = BufferedReader(inputStreamReader)
        var line: String
        val builder = StringBuilder()
        if (dzenNoch) builder.append("<html><head><style type=\"text/css\">a {color:#f44336;} body{color: #fff; background-color: #303030;}</style></head><body>\n") else builder.append("<html><head><style type=\"text/css\">a {color:#d00505;} body{color: #000; background-color: #fff;}</style></head><body>\n")
        reader.forEachLine {
            line = it
            if (dzenNoch) line = line.replace("#d00505", "#f44336")
            builder.append(line)
        }
        builder.append("</body></html>")
        if (dzenNoch)
            WebView.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
        WebView.loadDataWithBaseURL(null, builder.toString(), "text/html", "utf-8", null)
        inputStreamReader.close()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.putExtra("bgkc", bgkc)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}