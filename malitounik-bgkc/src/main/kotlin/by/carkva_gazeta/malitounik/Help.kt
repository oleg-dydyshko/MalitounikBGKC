package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.help.*
import java.io.BufferedReader
import java.io.InputStreamReader

class Help : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        textView.movementMethod = LinkMovementMethod.getInstance()
        if (dzenNoch) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        val inputStream = resources.openRawResource(R.raw.help)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        reader.forEachLine {
            line = it
            if (dzenNoch) line = line.replace("#d00505", "#f44336")
            builder.append(line)
        }
        inputStream.close()
        textView.text = MainActivity.fromHtml(builder.toString().replace("<!--version-->", "API " + Build.VERSION.SDK_INT))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        title_toolbar.text = resources.getString(R.string.help)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }
}