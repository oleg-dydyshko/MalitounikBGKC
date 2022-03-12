package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.ActivityParafiiBgkcBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

class ParafiiBgkc : AppCompatActivity() {
    private var bgkc = 0
    private lateinit var binding: ActivityParafiiBgkcBinding
    private var resetTollbarJob: Job? = null
    private lateinit var k: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = ActivityParafiiBgkcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.parafii)
        val webSettings = binding.WebView.settings
        webSettings.standardFontFamily = "sans-serif-condensed"
        webSettings.defaultFontSize = fontBiblia.toInt()
        val prynagodnyia = intent.extras?.getInt("bgkc_parafii", 0) ?: 0
        bgkc = intent.extras?.getInt("bgkc", 0) ?: 0
        var inputStream = resources.openRawResource(R.raw.dzie_kuryja)
        when (bgkc) {
            0 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_kuryja)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_centr_dekan)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_usxod_dekan)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_zaxod_dekan)
            }
            1 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_centr_dekan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_barysau)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_zodzina)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_zaslauje)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_maladechna)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_marjinahorka)
                if (prynagodnyia == 6) inputStream = resources.openRawResource(R.raw.dzie_miensk)
            }
            2 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_usxod_dekan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_viciebsk)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_orsha)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_homel)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_polacak)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_mahilou)
            }
            3 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_zaxod_dekan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_baranavichy)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_bierascie)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_horadnia)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_ivacevichy)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_lida)
                if (prynagodnyia == 6) inputStream = resources.openRawResource(R.raw.dzie_navahradak)
                if (prynagodnyia == 7) inputStream = resources.openRawResource(R.raw.dzie_pinsk)
                if (prynagodnyia == 8) inputStream = resources.openRawResource(R.raw.dzie_slonim)
            }
            4 -> {
                if (prynagodnyia == 0) inputStream = resources.openRawResource(R.raw.dzie_antverpan)
                if (prynagodnyia == 1) inputStream = resources.openRawResource(R.raw.dzie_londan)
                if (prynagodnyia == 2) inputStream = resources.openRawResource(R.raw.dzie_varshava)
                if (prynagodnyia == 3) inputStream = resources.openRawResource(R.raw.dzie_vilnia)
                if (prynagodnyia == 4) inputStream = resources.openRawResource(R.raw.dzie_viena)
                if (prynagodnyia == 5) inputStream = resources.openRawResource(R.raw.dzie_kalininhrad)
                if (prynagodnyia == 6) inputStream = resources.openRawResource(R.raw.dzie_praha)
                if (prynagodnyia == 7) inputStream = resources.openRawResource(R.raw.dzie_rym)
                if (prynagodnyia == 8) inputStream = resources.openRawResource(R.raw.dzie_sanktpieciarburg)
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
            binding.WebView.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
        binding.WebView.loadDataWithBaseURL(null, builder.toString(), "text/html", "utf-8", null)
        inputStreamReader.close()
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