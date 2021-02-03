package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.DialogFontSize
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.AkafistUnderBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class Opisanie : AppCompatActivity(), DialogFontSize.DialogFontSizeListener {
    private var dzenNoch = false
    private var mun = Calendar.getInstance()[Calendar.MONTH] + 1
    private var day = Calendar.getInstance()[Calendar.DATE]
    private var svity = false
    private lateinit var binding: AkafistUnderBinding
    private var change = false
    private lateinit var chin: SharedPreferences
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = AkafistUnderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: c[Calendar.MONTH] + 1
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        if (savedInstanceState != null) {
            change = savedInstanceState.getBoolean("change")
        }
        val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        val inputStream: InputStream
        if (svity) {
            inputStream = resources.openRawResource(R.raw.opisanie_sviat)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
            val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
            binding.TextView.text = getString(by.carkva_gazeta.malitounik.R.string.opisanie_error)
            arrayList.forEach {
                if (day == it[0].toInt() && mun == it[1].toInt()) {
                    binding.TextView.text = MainActivity.fromHtml(it[2])
                }
            }
        } else {
            inputStream = when (mun) {
                1 -> resources.openRawResource(R.raw.opisanie1)
                2 -> resources.openRawResource(R.raw.opisanie2)
                3 -> resources.openRawResource(R.raw.opisanie3)
                4 -> resources.openRawResource(R.raw.opisanie4)
                5 -> resources.openRawResource(R.raw.opisanie5)
                6 -> resources.openRawResource(R.raw.opisanie6)
                7 -> resources.openRawResource(R.raw.opisanie7)
                8 -> resources.openRawResource(R.raw.opisanie8)
                9 -> resources.openRawResource(R.raw.opisanie9)
                10 -> resources.openRawResource(R.raw.opisanie10)
                11 -> resources.openRawResource(R.raw.opisanie11)
                12 -> resources.openRawResource(R.raw.opisanie12)
                else -> resources.openRawResource(R.raw.opisanie1)
            }
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
            val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
            val res = arrayList[day - 1][0]
            binding.TextView.text = MainActivity.fromHtml(res)
        }
        setTollbarTheme()
    }

    override fun onDialogFontSize(fontSize: Float) {
        binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    }

    private fun setTollbarTheme() {
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.zmiest)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
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

    override fun onBackPressed() {
        if (change) {
            onSupportNavigateUp()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.opisanie, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_edit).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = chin.getBoolean("dzen_noch", false)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("change", change)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            change = true
            val prefEditor = chin.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            var sviatylink = ""
            if (svity) sviatylink = "&sviata=1"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=3$sviatylink&date=$day&month=$mun")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
        return super.onOptionsItemSelected(item)
    }
}