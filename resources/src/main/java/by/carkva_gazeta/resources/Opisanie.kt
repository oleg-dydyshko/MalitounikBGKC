package by.carkva_gazeta.resources

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import kotlinx.android.synthetic.main.akafist_under.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by oleg on 22.7.16
 */
class Opisanie : AppCompatActivity() {
    private var dzenNoch = false
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
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        setContentView(R.layout.akafist_under)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (dzenNoch) {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
            } else {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
            }
        }
        val c = Calendar.getInstance()
        var mun = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
        val day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        val svity = intent.extras?.getString("svity", "") ?: ""
        val r = resources
        var inputStream = r.openRawResource(R.raw.opisanie1)
        if (intent.extras?.getBoolean("glavnyia", false) == true) {
            if (svity.toLowerCase(Locale.getDefault()).contains("уваход у ерусалім")) inputStream = r.openRawResource(R.raw.opisanie_sv0)
            if (svity.toLowerCase(Locale.getDefault()).contains("уваскрасеньне")) inputStream = r.openRawResource(R.raw.opisanie_sv1)
            if (svity.toLowerCase(Locale.getDefault()).contains("узьнясеньне")) inputStream = r.openRawResource(R.raw.opisanie_sv2)
            if (svity.toLowerCase(Locale.getDefault()).contains("зыход")) inputStream = r.openRawResource(R.raw.opisanie_sv3)
            val resFile = day.toString() + "_" + mun
            if (resFile.contains("1_0")) inputStream = r.openRawResource(R.raw.opisanie1_0)
            if (resFile.contains("2_1")) inputStream = r.openRawResource(R.raw.opisanie2_1)
            if (resFile.contains("6_0")) inputStream = r.openRawResource(R.raw.opisanie6_0)
            if (resFile.contains("6_7")) inputStream = r.openRawResource(R.raw.opisanie6_7)
            if (resFile.contains("8_8")) inputStream = r.openRawResource(R.raw.opisanie8_8)
            if (resFile.contains("14_8")) inputStream = r.openRawResource(R.raw.opisanie14_8)
            if (resFile.contains("15_7")) inputStream = r.openRawResource(R.raw.opisanie15_7)
            if (resFile.contains("25_2")) inputStream = r.openRawResource(R.raw.opisanie25_2)
            if (resFile.contains("21_10")) inputStream = r.openRawResource(R.raw.opisanie21_10)
            if (resFile.contains("25_11")) inputStream = r.openRawResource(R.raw.opisanie25_11)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            val builder = StringBuilder()
            reader.forEachLine {
                line = it.replace("h3", "h6")
                builder.append(line)
            }
            inputStream.close()
            if (dzenNoch) TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            TextView.text = MainActivity.fromHtml(builder.toString())
        } else {
            when (mun) {
                0 -> inputStream = r.openRawResource(R.raw.opisanie1)
                1 -> inputStream = r.openRawResource(R.raw.opisanie2)
                2 -> inputStream = r.openRawResource(R.raw.opisanie3)
                3 -> inputStream = r.openRawResource(R.raw.opisanie4)
                4 -> inputStream = r.openRawResource(R.raw.opisanie5)
                5 -> inputStream = r.openRawResource(R.raw.opisanie6)
                6 -> inputStream = r.openRawResource(R.raw.opisanie7)
                7 -> inputStream = r.openRawResource(R.raw.opisanie8)
                8 -> inputStream = r.openRawResource(R.raw.opisanie9)
                9 -> inputStream = r.openRawResource(R.raw.opisanie10)
                10 -> inputStream = r.openRawResource(R.raw.opisanie11)
                11 -> inputStream = r.openRawResource(R.raw.opisanie12)
            }
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            //var line: String
            val builder = StringBuilder()
            reader.forEachLine {
                //line = it.replace("h3", "h6")
                builder.append(it)
            }
            inputStream.close()
            val dataR: String = if (day < 10) "0$day" else day.toString()
            mun++
            val munR: String = if (mun < 10) "0$mun" else mun.toString()
            var res = builder.toString()
            val tN = res.indexOf("<div id=\"$dataR$munR\">")
            val tK = res.indexOf("</div>", tN)
            res = res.substring(tN, tK + 6)
            res = res.replace("<div id=\"$dataR$munR\">", "")
            res = res.replace("<h3 class=\"blocks\">", "<p><strong>")
            res = res.replace("</h3>", "</strong>")
            //res = res.replace("<p class=\"blocks\">", "<p>")
            res = res.replace("</div>", "")
            if (dzenNoch) TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            TextView.text = MainActivity.fromHtml(res)
        }
        setTollbarTheme()
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.zmiest)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }
}