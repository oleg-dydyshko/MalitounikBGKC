package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.onas.*
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by oleg on 9.7.16
 */
class Onas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onas)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        if (dzenNoch) {
            onas.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        onas.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        onas.movementMethod = LinkMovementMethod.getInstance()
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
        title_toolbar.text = resources.getString(R.string.PRA_NAS)
        val inputStream = resources.openRawResource(R.raw.onas)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        val version = BuildConfig.VERSION_NAME
        reader.forEachLine {
            line = it
            if (dzenNoch) line = line.replace("#d00505", "#f44336")
            if (line.contains("<!--<VERSION></VERSION>-->")) {
                line = line.replace("<!--<VERSION></VERSION>-->", "<em>Версія праграмы: $version</em><br><br>")
            }
            builder.append(line)
        }
        inputStream.close()
        //CaseInsensitiveResourcesFontLoader FontLoader = new CaseInsensitiveResourcesFontLoader();
        onas.text = MainActivity.fromHtml(builder.toString())
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }
}