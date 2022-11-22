package by.carkva_gazeta.malitounik

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import by.carkva_gazeta.malitounik.databinding.HelpBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

class Onas : BaseActivity() {
    private lateinit var binding: HelpBinding
    private var resetTollbarJob: Job? = null
    private var mLastClickTime: Long = 0

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = HelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.movementMethod = LinkMovementMethod.getInstance()
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
        if (dzenNoch) binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getString(R.string.pra_nas)
        val inputStream = resources.openRawResource(R.raw.onas)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                if (line.contains("<!--<VERSION></VERSION>-->")) {
                    line = line.replace("<!--<VERSION></VERSION>-->", "<em>Версія праграмы: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})</em><br><br>")
                }
                builder.append(line)
            }
        }
        val text = MainActivity.fromHtml(builder.toString())
        val str = "https://carkva-gazeta.by"
        val t1 = text.indexOf(str)
        val spannable = text.toSpannable()
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                prefEditors.putInt("id", R.id.label2)
                prefEditors.apply()
                val intent = Intent(this@Onas, Naviny::class.java)
                intent.putExtra("naviny", 0)
                startActivity(intent)
            }
        }, t1, t1 + str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val str2 = "Палітыка прыватнасьці"
        val t2 = text.indexOf(str2)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val url = "https://carkva.web.app/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    try {
                        intent.setPackage(null)
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        MainActivity.toastView(this@Onas, getString(R.string.error_ch2))
                    }
                }
            }
        }, t2, t2 + str2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textView.text = spannable
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
