package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import by.carkva_gazeta.malitounik.databinding.OnasBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

class Onas : AppCompatActivity() {
    private lateinit var binding: OnasBinding
    private var resetTollbarJob: Job? = null
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = OnasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        binding.onas.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        binding.onas.movementMethod = LinkMovementMethod.getInstance()
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
        binding.titleToolbar.text = resources.getString(R.string.pra_nas)
        val inputStream = resources.openRawResource(R.raw.onas)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        val version = BuildConfig.VERSION_NAME
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                if (line.contains("<!--<VERSION></VERSION>-->")) {
                    line = line.replace("<!--<VERSION></VERSION>-->", "<em>Версія праграмы: $version</em><br><br>")
                }
                builder.append(line)
            }
        }
        val text = MainActivity.fromHtml(builder.toString())
        val t1 = text.indexOf("carkva-gazeta.by")
        val spannable = text.toSpannable()
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label2)
                prefEditors.apply()
                val intent = Intent(this@Onas, Naviny::class.java)
                intent.putExtra("naviny", 0)
                startActivity(intent)
            }
        }, t1, t1 + 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.onas.text = spannable
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

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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