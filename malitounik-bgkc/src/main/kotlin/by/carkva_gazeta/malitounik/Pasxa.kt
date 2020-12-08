package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.databinding.PasxaBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class Pasxa : AppCompatActivity() {
    private lateinit var binding: PasxaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        binding = PasxaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titleToolbar.setOnClickListener {
            binding.titleToolbar.setHorizontallyScrolling(true)
            binding.titleToolbar.freezesText = true
            binding.titleToolbar.marqueeRepeatLimit = -1
            if (binding.titleToolbar.isSelected) {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                binding.titleToolbar.isSelected = false
            } else {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.titleToolbar.isSelected = true
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getText(R.string.pascha_kaliandar_bel)
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val inputStream = resources.openRawResource(R.raw.pasxa)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        binding.pasxa.text = reader.readText()
        binding.pasxa.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}