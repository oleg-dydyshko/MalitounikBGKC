package by.carkva_gazeta.malitounik

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.OnasBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class Onas : BaseActivity() {
    private lateinit var binding: OnasBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = OnasBinding.inflate(layoutInflater)
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        binding.titleToolbar.text = resources.getString(R.string.pra_nas)
        val inputStream = resources.openRawResource(R.raw.onas)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                var line = it
                if (dzenNoch) line = line.replace("#d00505", "#ff6666")
                if (line.contains("<!--<VERSION></VERSION>-->")) {
                    line = line.replace("<!--<VERSION></VERSION>-->", "<em>Версія праграмы: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})</em><br><br>")
                }
                builder.append(line)
            }
        }
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        binding.onas.textSize = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        val textOnas = MainActivity.fromHtml(builder.toString()).toSpannable()
        val t1 = textOnas.indexOf("QR-CODE")
        val bitMap = BitmapFactory.decodeResource(resources, R.drawable.qr_code_google_play)
        textOnas.setSpan(ImageSpan(this, bitMap), t1, t1 + 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        val t3 = textOnas.indexOf("Што новага?")
        textOnas.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val dialog = DialogSztoHovaha()
                dialog.show(supportFragmentManager, "DialogSztoHovaha")
            }
        }, t3, t3 + 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.onas.text = textOnas
        binding.onas.movementMethod = LinkMovementMethod()
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }
}
