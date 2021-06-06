package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.resources.databinding.AkafistUnderBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Ton : AppCompatActivity(), OnTouchListener, DialogFontSizeListener, InteractiveScrollView.OnScrollChangedCallback {

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun mHidePart2Runnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }
    private fun mShowPart2Runnable() {
        val actionBar = supportActionBar
        actionBar?.show()
    }
    private var fullscreenPage = false
    private lateinit var chin: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var n = 0
    private var title = ""
    private var checkSetDzenNoch = false
    private lateinit var binding: AkafistUnderBinding
    private lateinit var bindingprogress: ProgressBinding
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var id = R.raw.ton1
    private var resurs = "ton1"
    private var positionY = 0
    private var firstTextPosition = ""

    override fun onScroll(t: Int, oldt: Int) {
        positionY = t
        val laneLayout = binding.TextView.layout
        laneLayout?.let { layout ->
            val textForVertical = binding.TextView.text.substring(binding.TextView.layout.getLineStart(layout.getLineForVertical(positionY)), binding.TextView.layout.getLineEnd(layout.getLineForVertical(positionY))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
        }
    }

    override fun onPause() {
        super.onPause()
        val prefEditor = chin.edit()
        prefEditor.putInt(resurs + "Scroll", positionY)
        prefEditor.apply()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = AkafistUnderBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        binding.constraint.setOnTouchListener(this)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
        }
        fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        if (dzenNoch) {
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
        }
        binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        val r = resources
        val ton = intent.extras?.getInt("ton", 1) ?: 1
        val tonNadzelny = intent.extras?.getBoolean("ton_naidzelny", true) ?: true
        var inputStream: InputStream
        when {
            intent.extras?.getBoolean("zmena_chastki", false) == true -> {
                resurs = intent?.extras?.getString("resurs") ?: ""
                inputStream = r.openRawResource(Bogashlugbovya.resursMap[resurs] ?: R.raw.zmenyia_chastki_tamash)
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
                val resursOut = builder.toString()
                title = intent.extras?.getString("title", "") ?: ""
                binding.TextView.text = MainActivity.fromHtml(resursOut)
            }
            intent.extras?.getBoolean("ton_na_sviaty", false) == true -> {
                val c = Calendar.getInstance()
                val mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: c[Calendar.MONTH] + 1
                val day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
                val fileOpisanieSviat = File("${filesDir}/opisanie_sviat.json")
                if (!fileOpisanieSviat.exists()) {
                    if (MainActivity.isNetworkAvailable()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                val mURL = URL("https://carkva-gazeta.by/opisanie_sviat.json")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (conections.responseCode == 200) {
                                    try {
                                        fileOpisanieSviat.writer().use {
                                            it.write(mURL.readText())
                                        }
                                    } catch (e: Throwable) {
                                    }
                                }
                            }
                        }
                    }
                }
                if (fileOpisanieSviat.exists()) {
                    val builder = fileOpisanieSviat.readText()
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                    val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
                    arrayList.forEach {
                        if (day == it[0].toInt() && mun == it[1].toInt()) {
                            var res = it[2]
                            val t1 = res.indexOf("<strong>")
                            val t2 = res.indexOf("</strong>")
                            if (t1 != -1 && t2 != -1) {
                                title = res.substring(t1 + 8, t2)
                            }
                            when (intent.extras?.getInt("lityrgia", 4) ?: 4) {
                                3 -> res = it[3]
                                4 -> res = it[4]
                                5 -> res = it[5]
                            }
                            if (dzenNoch) res = res.replace("#d00505", "#f44336")
                            binding.TextView.text = MainActivity.fromHtml(res)
                        }
                    }
                }
            }
            tonNadzelny -> {
                inputStream = r.openRawResource(R.raw.ton1)
                title = "Тон $ton"
                when (ton) {
                    1 -> {
                        resurs = "ton1"
                        id = R.raw.ton1
                        inputStream = r.openRawResource(id)
                    }
                    2 -> {
                        resurs = "ton2"
                        id = R.raw.ton2
                        inputStream = r.openRawResource(id)
                    }
                    3 -> {
                        resurs = "ton3"
                        id = R.raw.ton3
                        inputStream = r.openRawResource(id)
                    }
                    4 -> {
                        resurs = "ton4"
                        id = R.raw.ton4
                        inputStream = r.openRawResource(id)
                    }
                    5 -> {
                        resurs = "ton5"
                        id = R.raw.ton5
                        inputStream = r.openRawResource(id)
                    }
                    6 -> {
                        resurs = "ton6"
                        id = R.raw.ton6
                        inputStream = r.openRawResource(id)
                    }
                    7 -> {
                        resurs = "ton7"
                        id = R.raw.ton7
                        inputStream = r.openRawResource(id)
                    }
                    8 -> {
                        resurs = "ton8"
                        id = R.raw.ton8
                        inputStream = r.openRawResource(id)
                    }
                }
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
                val resursOut = builder.toString()
                binding.TextView.text = MainActivity.fromHtml(resursOut)
            }
            else -> {
                inputStream = r.openRawResource(R.raw.ton1_budni)
                when (ton) {
                    1 -> {
                        resurs = "ton1_budni"
                        id = R.raw.ton1_budni
                        inputStream = r.openRawResource(id)
                        title = "ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам"
                    }
                    2 -> {
                        resurs = "ton2_budni"
                        id = R.raw.ton2_budni
                        inputStream = r.openRawResource(id)
                        title = "АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю"
                    }
                    3 -> {
                        resurs = "ton3_budni"
                        id = R.raw.ton3_budni
                        inputStream = r.openRawResource(id)
                        title = "СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу"
                    }
                    4 -> {
                        resurs = "ton4_budni"
                        id = R.raw.ton4_budni
                        inputStream = r.openRawResource(id)
                        title = "ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю"
                    }
                    5 -> {
                        resurs = "ton5_budni"
                        id = R.raw.ton5_budni
                        inputStream = r.openRawResource(id)
                        title = "ПЯТНІЦА\nСлужба Крыжу Гасподняму"
                    }
                    6 -> {
                        resurs = "ton6_budni"
                        id = R.raw.ton6_budni
                        inputStream = r.openRawResource(id)
                        title = "Субота\nСлужба ўсім сьвятым і памёрлым"
                    }
                }
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
                val resursOut = builder.toString()
                binding.TextView.text = MainActivity.fromHtml(resursOut)
            }
        }
        bindingprogress.fontSizePlus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX)  bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.max_font)
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = chin.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent()
        }
        bindingprogress.fontSizeMinus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN)  bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.min_font)
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = chin.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent()
        }
        bindingprogress.brighessPlus.setOnClickListener {
            if (MainActivity.brightness < 100) {
                MainActivity.brightness = MainActivity.brightness + 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent()
        }
        bindingprogress.brighessMinus.setOnClickListener {
            if (MainActivity.brightness > 0) {
                MainActivity.brightness = MainActivity.brightness - 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent()
        }
        if (savedInstanceState != null) {
            binding.TextView.post {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    val index = binding.TextView.text.indexOf(textline)
                    val line = binding.TextView.layout.getLineForOffset(index)
                    val y = binding.TextView.layout.getLineTop(line)
                    binding.scrollView2.scrollY = y
                } else {
                    binding.scrollView2.smoothScrollBy(0, positionY)
                }
            }
        } else {
            binding.scrollView2.post {
                binding.scrollView2.smoothScrollBy(0, positionY)
            }
        }
        binding.scrollView2.setOnScrollChangedCallback(this)
        positionY = chin.getInt(resurs + "Scroll", 0)
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
        binding.titleToolbar.text = title
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

    private fun startProcent() {
        procentJob?.cancel()
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            bindingprogress.progress.visibility = View.GONE
            bindingprogress.brighess.visibility = View.GONE
            bindingprogress.fontSize.visibility = View.GONE
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                MainActivity.brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    if (x < otstup) {
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        startProcent()
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = chin.getBoolean("dzen_noch", false)
        val item = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        val spanString = SpannableString(menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        item.title = spanString
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.akafist, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        val prefEditor: Editor = chin.edit()
        val id = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            checkSetDzenNoch = true
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            if (chin.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        prefEditor.apply()
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val inputStream = resources.openRawResource(this.id)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", resurs)
                intent.putExtra("title", title)
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else {
            if (checkSetDzenNoch) onSupportNavigateUp() else super.onBackPressed()
        }
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        CoroutineScope(Dispatchers.Main).launch {
            mHidePart2Runnable()
        }
    }

    @Suppress("DEPRECATION")
    private fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            val controller = window.insetsController
            controller?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            mShowPart2Runnable()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("checkSetDzenNoch", checkSetDzenNoch)
        outState.putString("textLine", firstTextPosition)
    }
}