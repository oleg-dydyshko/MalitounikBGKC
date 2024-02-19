package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ImageSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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


class Pasxa : BaseActivity(), DialogFontSize.DialogFontSizeListener {
    private lateinit var binding: OnasBinding
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onDialogFontSize(fontSize: Float) {
        binding.onas.textSize = fontSize
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = getBaseDzenNoch()
        binding = OnasBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.titleToolbar.text = resources.getText(R.string.pascha_kaliandar_bel)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        val inputStream = resources.openRawResource(R.raw.pasxa)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.forEachLine {
                var line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                builder.append(line)
            }
        }
        binding.onas.textSize = chin.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pasxa, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_dzen_noch).isChecked = getBaseDzenNoch()
        val spanString = if (chin.getBoolean("auto_dzen_noch", false)) {
            menu.findItem(R.id.action_dzen_noch).isCheckable = false
            SpannableString(getString(R.string.auto_widget_day_d_n))
        } else {
            menu.findItem(R.id.action_dzen_noch).isCheckable = true
            SpannableString(getString(R.string.widget_day_d_n))
        }
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menu.findItem(R.id.action_dzen_noch).title = spanString
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val prefEditor = chin.edit()
            if (item.isCheckable) {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    prefEditor.putBoolean("dzen_noch", true)
                } else {
                    prefEditor.putBoolean("dzen_noch", false)
                }
                prefEditor.apply()
                recreate()
            } else {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            return true
        }
        return false
    }
}