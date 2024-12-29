package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.OnasBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class Pasxa : BaseActivity() {
    private lateinit var binding: OnasBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private var resetTollbarJob: Job? = null
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private var procentJobFont: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun onDialogFontSize() {
        binding.onas.textSize = fontBiblia
    }

    private fun setFontDialog() {
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
        }
        startProcent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = getBaseDzenNoch()
        binding = OnasBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
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
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize()
                }
                startProcent()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.titleToolbar.text = resources.getText(R.string.pascha_kaliandar_bel)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, R.drawable.selector_progress_noch)
        }
        val inputStream = resources.openRawResource(R.raw.pasxa)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                var line = it
                if (dzenNoch) line = line.replace("#d00505", "#ff6666")
                builder.append(line)
            }
        }
        binding.onas.textSize = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        val textOnas = MainActivity.fromHtml(builder.toString()).toSpannable()
        val t1 = textOnas.indexOf("IMAGE")
        val bitMap = BitmapFactory.decodeResource(resources, R.drawable.uvaskras)
        textOnas.setSpan(ImageSpan(this, bitMap), t1, t1 + 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
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

    private fun startProcent() {
        procentJobFont?.cancel()
        bindingprogress.progressFont.visibility = View.VISIBLE
        procentJobFont = CoroutineScope(Dispatchers.Main).launch {
            MainActivity.dialogVisable = true
            delay(2000)
            bindingprogress.progressFont.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@Pasxa, R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
                MainActivity.dialogVisable = false
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pasxa, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_font) {
            setFontDialog()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
            return true
        }
        return false
    }
}