package by.carkva_gazeta.malitounik

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.MenuItem
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.HelpBinding
import com.r0adkll.slidr.Slidr
import java.io.BufferedReader
import java.io.InputStreamReader

class Help : BaseActivity() {
    
    private lateinit var binding: HelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val dzenNoch = getBaseDzenNoch()
        setMyTheme()
        binding = HelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Slidr.attach(this)
        binding.textView.movementMethod = LinkMovementMethod.getInstance()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        }
        val inputStream = resources.openRawResource(R.raw.help)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                builder.append(line)
            }
        }
        binding.textView.text = MainActivity.fromHtml(builder.toString())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getString(R.string.help)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}