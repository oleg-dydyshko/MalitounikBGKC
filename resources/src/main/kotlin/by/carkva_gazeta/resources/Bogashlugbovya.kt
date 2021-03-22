package by.carkva_gazeta.resources

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.toSpannable
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.databinding.BogasluzbovyaBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class Bogashlugbovya : AppCompatActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, InteractiveScrollView.OnScrollChangedCallback {

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
        supportActionBar?.show()
    }

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var resurs = ""
    private var men = true
    private var positionY = 0
    private var title = ""
    private var editVybranoe = false
    private var mActionDown = false
    private var mAutoScroll = true
    private val orientation: Int
        get() = MainActivity.getOrientation(this)
    private lateinit var binding: BogasluzbovyaBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var diffScroll = -1
    private var aliert8 = ""
    private var aliert9 = ""
    private var findPosition = 0
    private val findListSpans = ArrayList<SpanStr>()

    companion object {
        private val resursMap = ArrayMap<String, Int>()

        init {
            resursMap["bogashlugbovya1"] = R.raw.bogashlugbovya1
            resursMap["bogashlugbovya4"] = R.raw.bogashlugbovya4
            resursMap["bogashlugbovya6"] = R.raw.bogashlugbovya6
            resursMap["bogashlugbovya8"] = R.raw.bogashlugbovya8
            resursMap["bogashlugbovya11"] = R.raw.bogashlugbovya11
            resursMap["bogashlugbovya12_1"] = R.raw.bogashlugbovya12_1
            resursMap["bogashlugbovya12_2"] = R.raw.bogashlugbovya12_2
            resursMap["bogashlugbovya12_3"] = R.raw.bogashlugbovya12_3
            resursMap["bogashlugbovya12_4"] = R.raw.bogashlugbovya12_4
            resursMap["bogashlugbovya12_5"] = R.raw.bogashlugbovya12_5
            resursMap["bogashlugbovya12_6"] = R.raw.bogashlugbovya12_6
            resursMap["bogashlugbovya12_7"] = R.raw.bogashlugbovya12_7
            resursMap["bogashlugbovya12_8"] = R.raw.bogashlugbovya12_8
            resursMap["bogashlugbovya12_9"] = R.raw.bogashlugbovya12_9
            resursMap["bogashlugbovya13_1"] = R.raw.bogashlugbovya13_1
            resursMap["bogashlugbovya13_2"] = R.raw.bogashlugbovya13_2
            resursMap["bogashlugbovya13_3"] = R.raw.bogashlugbovya13_3
            resursMap["bogashlugbovya13_4"] = R.raw.bogashlugbovya13_4
            resursMap["bogashlugbovya13_5"] = R.raw.bogashlugbovya13_5
            resursMap["bogashlugbovya13_6"] = R.raw.bogashlugbovya13_6
            resursMap["bogashlugbovya13_7"] = R.raw.bogashlugbovya13_7
            resursMap["bogashlugbovya13_8"] = R.raw.bogashlugbovya13_8
            resursMap["bogashlugbovya14_1"] = R.raw.bogashlugbovya14_1
            resursMap["bogashlugbovya14_2"] = R.raw.bogashlugbovya14_2
            resursMap["bogashlugbovya14_3"] = R.raw.bogashlugbovya14_3
            resursMap["bogashlugbovya14_4"] = R.raw.bogashlugbovya14_4
            resursMap["bogashlugbovya14_5"] = R.raw.bogashlugbovya14_5
            resursMap["bogashlugbovya14_6"] = R.raw.bogashlugbovya14_6
            resursMap["bogashlugbovya14_7"] = R.raw.bogashlugbovya14_7
            resursMap["bogashlugbovya14_8"] = R.raw.bogashlugbovya14_8
            resursMap["bogashlugbovya14_9"] = R.raw.bogashlugbovya14_9
            resursMap["bogashlugbovya15_1"] = R.raw.bogashlugbovya15_1
            resursMap["bogashlugbovya15_2"] = R.raw.bogashlugbovya15_2
            resursMap["bogashlugbovya15_3"] = R.raw.bogashlugbovya15_3
            resursMap["bogashlugbovya15_4"] = R.raw.bogashlugbovya15_4
            resursMap["bogashlugbovya15_5"] = R.raw.bogashlugbovya15_5
            resursMap["bogashlugbovya15_6"] = R.raw.bogashlugbovya15_6
            resursMap["bogashlugbovya15_7"] = R.raw.bogashlugbovya15_7
            resursMap["bogashlugbovya15_8"] = R.raw.bogashlugbovya15_8
            resursMap["bogashlugbovya15_9"] = R.raw.bogashlugbovya15_9
            resursMap["bogashlugbovya16_1"] = R.raw.bogashlugbovya16_1
            resursMap["bogashlugbovya16_2"] = R.raw.bogashlugbovya16_2
            resursMap["bogashlugbovya16_3"] = R.raw.bogashlugbovya16_3
            resursMap["bogashlugbovya16_4"] = R.raw.bogashlugbovya16_4
            resursMap["bogashlugbovya16_5"] = R.raw.bogashlugbovya16_5
            resursMap["bogashlugbovya16_6"] = R.raw.bogashlugbovya16_6
            resursMap["bogashlugbovya16_7"] = R.raw.bogashlugbovya16_7
            resursMap["bogashlugbovya16_8"] = R.raw.bogashlugbovya16_8
            resursMap["bogashlugbovya16_9"] = R.raw.bogashlugbovya16_9
            resursMap["bogashlugbovya16_10"] = R.raw.bogashlugbovya16_10
            resursMap["bogashlugbovya16_11"] = R.raw.bogashlugbovya16_11
            resursMap["bogashlugbovya17_1"] = R.raw.bogashlugbovya17_1
            resursMap["bogashlugbovya17_2"] = R.raw.bogashlugbovya17_2
            resursMap["bogashlugbovya17_3"] = R.raw.bogashlugbovya17_3
            resursMap["bogashlugbovya17_4"] = R.raw.bogashlugbovya17_4
            resursMap["bogashlugbovya17_5"] = R.raw.bogashlugbovya17_5
            resursMap["bogashlugbovya17_6"] = R.raw.bogashlugbovya17_6
            resursMap["bogashlugbovya17_7"] = R.raw.bogashlugbovya17_7
            resursMap["bogashlugbovya17_8"] = R.raw.bogashlugbovya17_8
            resursMap["akafist0"] = R.raw.akafist0
            resursMap["akafist1"] = R.raw.akafist1
            resursMap["akafist2"] = R.raw.akafist2
            resursMap["akafist3"] = R.raw.akafist3
            resursMap["akafist4"] = R.raw.akafist4
            resursMap["akafist5"] = R.raw.akafist5
            resursMap["akafist6"] = R.raw.akafist6
            resursMap["akafist7"] = R.raw.akafist7
            resursMap["malitvy1"] = R.raw.malitvy1
            resursMap["malitvy2"] = R.raw.malitvy2
            resursMap["paslia_prychascia1"] = R.raw.paslia_prychascia1
            resursMap["paslia_prychascia2"] = R.raw.paslia_prychascia2
            resursMap["paslia_prychascia3"] = R.raw.paslia_prychascia3
            resursMap["paslia_prychascia4"] = R.raw.paslia_prychascia4
            resursMap["paslia_prychascia5"] = R.raw.paslia_prychascia5
            resursMap["prynagodnyia_0"] = R.raw.prynagodnyia_0
            resursMap["prynagodnyia_1"] = R.raw.prynagodnyia_1
            resursMap["prynagodnyia_2"] = R.raw.prynagodnyia_2
            resursMap["prynagodnyia_3"] = R.raw.prynagodnyia_3
            resursMap["prynagodnyia_4"] = R.raw.prynagodnyia_4
            resursMap["prynagodnyia_5"] = R.raw.prynagodnyia_5
            resursMap["prynagodnyia_6"] = R.raw.prynagodnyia_6
            resursMap["prynagodnyia_7"] = R.raw.prynagodnyia_7
            resursMap["prynagodnyia_8"] = R.raw.prynagodnyia_8
            resursMap["prynagodnyia_9"] = R.raw.prynagodnyia_9
            resursMap["prynagodnyia_10"] = R.raw.prynagodnyia_10
            resursMap["prynagodnyia_11"] = R.raw.prynagodnyia_11
            resursMap["prynagodnyia_12"] = R.raw.prynagodnyia_12
            resursMap["prynagodnyia_13"] = R.raw.prynagodnyia_13
            resursMap["prynagodnyia_14"] = R.raw.prynagodnyia_14
            resursMap["prynagodnyia_15"] = R.raw.prynagodnyia_15
            resursMap["prynagodnyia_16"] = R.raw.prynagodnyia_16
            resursMap["prynagodnyia_17"] = R.raw.prynagodnyia_17
            resursMap["prynagodnyia_18"] = R.raw.prynagodnyia_18
            resursMap["prynagodnyia_19"] = R.raw.prynagodnyia_19
            resursMap["prynagodnyia_20"] = R.raw.prynagodnyia_20
            resursMap["prynagodnyia_21"] = R.raw.prynagodnyia_21
            resursMap["prynagodnyia_22"] = R.raw.prynagodnyia_22
            resursMap["prynagodnyia_23"] = R.raw.prynagodnyia_23
            resursMap["prynagodnyia_24"] = R.raw.prynagodnyia_24
            resursMap["prynagodnyia_25"] = R.raw.prynagodnyia_25
            resursMap["prynagodnyia_26"] = R.raw.prynagodnyia_26
            resursMap["prynagodnyia_27"] = R.raw.prynagodnyia_27
            resursMap["prynagodnyia_28"] = R.raw.prynagodnyia_28
            resursMap["prynagodnyia_29"] = R.raw.prynagodnyia_29
            resursMap["prynagodnyia_30"] = R.raw.prynagodnyia_30
            resursMap["prynagodnyia_31"] = R.raw.prynagodnyia_31
            resursMap["prynagodnyia_32"] = R.raw.prynagodnyia_32
            resursMap["prynagodnyia_33"] = R.raw.prynagodnyia_33
            resursMap["prynagodnyia_34"] = R.raw.prynagodnyia_34
            resursMap["prynagodnyia_35"] = R.raw.prynagodnyia_35
            resursMap["prynagodnyia_36"] = R.raw.prynagodnyia_36
            resursMap["ruzanec0"] = R.raw.ruzanec0
            resursMap["ruzanec1"] = R.raw.ruzanec1
            resursMap["ruzanec2"] = R.raw.ruzanec2
            resursMap["ruzanec3"] = R.raw.ruzanec3
            resursMap["ruzanec4"] = R.raw.ruzanec4
            resursMap["ruzanec5"] = R.raw.ruzanec5
            resursMap["ruzanec6"] = R.raw.ruzanec6
            resursMap["ton1"] = R.raw.ton1
            resursMap["ton1_budni"] = R.raw.ton1_budni
            resursMap["ton2"] = R.raw.ton2
            resursMap["ton2_budni"] = R.raw.ton2_budni
            resursMap["ton3"] = R.raw.ton3
            resursMap["ton3_budni"] = R.raw.ton3_budni
            resursMap["ton4"] = R.raw.ton4
            resursMap["ton4_budni"] = R.raw.ton4_budni
            resursMap["ton5"] = R.raw.ton5
            resursMap["ton5_budni"] = R.raw.ton5_budni
            resursMap["ton6"] = R.raw.ton6
            resursMap["ton6_budni"] = R.raw.ton6_budni
            resursMap["ton7"] = R.raw.ton7
            resursMap["ton8"] = R.raw.ton8
            PesnyAll.resursMap.forEach {
                resursMap[it.key] = it.value
            }
        }

        fun setVybranoe(context: Context, resurs: String, title: String): Boolean {
            var check = true
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                val gson = Gson()
                if (file.exists()) {
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    MenuVybranoe.vybranoe = gson.fromJson(file.readText(), type)
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) {
                        MenuVybranoe.vybranoe.removeAt(i)
                        check = false
                        break
                    }
                }
                if (check) {
                    MenuVybranoe.vybranoe.add(0, VybranoeData(vybranoeIndex(), resurs, title))
                }
                file.writer().use {
                    it.write(gson.toJson(MenuVybranoe.vybranoe))
                }
            } catch (t: Throwable) {
                file.delete()
                check = false
            }
            return check
        }

        fun vybranoeIndex(): Long {
            var result: Long = 1
            val vybranoe = MenuVybranoe.vybranoe
            if (vybranoe.size != 0) {
                vybranoe.forEach {
                    if (result < it.id) result = it.id
                }
                result++
            }
            return result
        }

        fun checkVybranoe(context: Context, resurs: String): Boolean {
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                if (file.exists()) {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    MenuVybranoe.vybranoe = gson.fromJson(file.readText(), type)
                } else {
                    return false
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) return true
                }
            } catch (t: Throwable) {
                file.delete()
                return false
            }
            return false
        }
    }

    private fun findAllAsanc() {
        CoroutineScope(Dispatchers.Main).launch {
            findRemoveSpan()
            findAll()
            findNext(false)
        }
    }

    private fun findAll(position: Int = 0) {
        val text = binding.textView.text as SpannableString
        val search = binding.textSearch.text.toString()
        val searchLig = search.length
        val strPosition = text.indexOf(search, position, true)
        if (strPosition != -1) {
            findListSpans.add(SpanStr(getColorSpans(text.getSpans(strPosition, strPosition + searchLig, ForegroundColorSpan::class.java)), strPosition, strPosition + searchLig))
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            findAll(strPosition + 1)
        }
    }

    private fun findRemoveSpan() {
        val text = binding.textView.text as SpannableString
        if (findListSpans.isNotEmpty()) {
            findListSpans.forEach {
                text.setSpan(ForegroundColorSpan(it.color), it.start, it.size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            findListSpans.clear()
        }
        val spans = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
        spans.forEach {
            text.removeSpan(it)
        }
        binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.niama)
    }

    private fun findNext(next: Boolean = true, previous: Boolean = false) {
        val text = binding.textView.text as SpannableString
        text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPosition].start, findListSpans[findPosition].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (next) {
            if (previous) findPosition--
            else findPosition++
        }
        if (findPosition == -1) {
            findPosition = findListSpans.size - 1
        }
        if (findPosition == findListSpans.size) {
            findPosition = 0
        }
        if (findListSpans.isNotEmpty()) {
            binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, findPosition + 1, findListSpans.size)
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta2)), findListSpans[findPosition].start, findListSpans[findPosition].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val line = binding.textView.layout.getLineForOffset(findListSpans[findPosition].start)
            val y = binding.textView.layout.getLineTop(line)
            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
            anim.setDuration(1000).start()
        }
    }

    private fun getColorSpans(colorSpan: Array<out ForegroundColorSpan>): Int {
        var color = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
        if (dzenNoch) color = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite)
        if (colorSpan.isNotEmpty()) {
            color = colorSpan[colorSpan.size - 1].foregroundColor
        }
        return color
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.textSize = fontBiblia
    }

    override fun onScroll(t: Int) {
        positionY = t
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = BogasluzbovyaBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        resurs = intent?.extras?.getString("resurs") ?: ""
        if (resurs.contains("pesny")) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        title = intent?.extras?.getString("title") ?: ""
        loadData(savedInstanceState)
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        binding.scrollView2.setOnScrollChangedCallback(this)
        binding.constraint.setOnTouchListener(this)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            editVybranoe = savedInstanceState.getBoolean("editVybranoe")
            MainActivity.dialogVisable = false
            if (savedInstanceState.getBoolean("seach")) {
                binding.textSearch.visibility = View.VISIBLE
                binding.textCount.visibility = View.VISIBLE
                binding.imageView6.visibility = View.VISIBLE
                binding.imageView5.visibility = View.VISIBLE
            }
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        binding.textView.textSize = fontBiblia
        DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.actionPlusBrighess.setImageResource(by.carkva_gazeta.malitounik.R.drawable.plus_v_kruge_black)
            bindingprogress.actionMinusBrighess.setImageResource(by.carkva_gazeta.malitounik.R.drawable.minus_v_kruge_black)
            bindingprogress.actionPlusFont.setImageResource(by.carkva_gazeta.malitounik.R.drawable.plus_v_kruge_black)
            bindingprogress.actionMinusFont.setImageResource(by.carkva_gazeta.malitounik.R.drawable.minus_v_kruge_black)
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
        }
        men = checkVybranoe(this, resurs)
        positionY = k.getInt(resurs + "Scroll", 0)
        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        bindingprogress.actionPlusFont.setOnClickListener {
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                var max = ""
                if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) max = " (макс)"
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.font_sp, fontBiblia.toInt(), max)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent(3000)
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
        }
        bindingprogress.actionMinusFont.setOnClickListener {
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                var min = ""
                if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) min = " (мін)"
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.font_sp, fontBiblia.toInt(), min)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent(3000)
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
        }
        bindingprogress.actionPlusBrighess.setOnClickListener {
            if (MainActivity.brightness < 100) {
                MainActivity.brightness = MainActivity.brightness + 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent(3000)
                MainActivity.checkBrightness = false
            }
        }
        bindingprogress.actionMinusBrighess.setOnClickListener {
            if (MainActivity.brightness > 0) {
                MainActivity.brightness = MainActivity.brightness - 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent(3000)
                MainActivity.checkBrightness = false
            }
        }
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingprogress.progressTitle.text = ""
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingprogress.progressTitle.text = ""
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.textView.movementMethod = LinkMovementMethod.getInstance()
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
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

    private fun loadData(savedInstanceState: Bundle?) = CoroutineScope(Dispatchers.Main).launch {
        val res = withContext(Dispatchers.IO) {
            val builder = StringBuilder()
            val id = resursMap[resurs] ?: R.raw.bogashlugbovya1
            val inputStream: InputStream = resources.openRawResource(id)
            val zmenyiaChastki = ZmenyiaChastki(this@Bogashlugbovya)
            val gregorian = Calendar.getInstance() as GregorianCalendar
            val dayOfWeek = gregorian.get(Calendar.DAY_OF_WEEK)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val color = if (dzenNoch) "<font color=\"#f44336\">"
            else "<font color=\"#d00505\">"
            reader.forEachLine {
                var line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                if (resurs.contains("bogashlugbovya")) {
                    if (line.contains("<KANDAK></KANDAK>")) {
                        line = line.replace("<KANDAK></KANDAK>", "")
                        builder.append(line)
                        try {
                            if (dayOfWeek == 1) {
                                builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(1))
                            } else {
                                builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 1))
                            }
                        } catch (t: Throwable) {
                            builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                        }
                    }
                    if (line.contains("<PRAKIMEN></PRAKIMEN>")) {
                        line = line.replace("<PRAKIMEN></PRAKIMEN>", "")
                        builder.append(line)
                        try {
                            if (dayOfWeek == 1) {
                                builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(2))
                            } else {
                                builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 2))
                            }
                        } catch (t: Throwable) {
                            builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                        }
                    }
                    if (line.contains("<ALILUIA></ALILUIA>")) {
                        line = line.replace("<ALILUIA></ALILUIA>", "")
                        builder.append(line)
                        try {
                            if (dayOfWeek == 1) {
                                builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(3))
                            } else {
                                builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 3))
                            }
                        } catch (t: Throwable) {
                            builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                        }
                    }
                    if (line.contains("<PRICHASNIK></PRICHASNIK>")) {
                        line = line.replace("<PRICHASNIK></PRICHASNIK>", "")
                        builder.append(line)
                        try {
                            if (dayOfWeek == 1) {
                                builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(4))
                            } else {
                                builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 4))
                            }
                        } catch (t: Throwable) {
                            t.printStackTrace()
                            builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                        }
                    }
                    when {
                        line.contains("<APCH></APCH>") -> {
                            line = line.replace("<APCH></APCH>", "")
                            var sv = zmenyiaChastki.sviatyia()
                            if (sv != "") {
                                val s1 = sv.split(":")
                                val s2 = s1[1].split(";")
                                sv = s1[0] + ":" + s2[0]
                                aliert8 = sv
                                builder.append(color).append(sv).append("</font>").append("<br><br>\n")
                            } else builder.append(line)
                            var svDop = zmenyiaChastki.sviatyiaDop()
                            if (svDop != "") {
                                val s1 = svDop.split(":")
                                val s2 = s1[1].split(";")
                                svDop = s1[0] + ":" + s2[0]
                                aliert8 = svDop
                                builder.append(color).append(svDop).append("</font>").append("<br><br>\n")
                            } else builder.append(line)
                            try {
                                builder.append(zmenyiaChastki.zmenya(1))
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                            }
                        }
                        line.contains("<EVCH></EVCH>") -> {
                            line = line.replace("<EVCH></EVCH>", "")
                            var sv = zmenyiaChastki.sviatyia()
                            if (sv != "") {
                                val s1 = sv.split(":")
                                val s2 = s1[1].split(";")
                                sv = s1[0] + ":" + s2[1]
                                aliert9 = sv
                                builder.append(color).append(sv).append("</font>").append("<br><br>\n")
                            } else builder.append(line)
                            var svDop = zmenyiaChastki.sviatyiaDop()
                            if (svDop != "") {
                                val s1 = svDop.split(":")
                                val s2 = s1[1].split(";")
                                svDop = s1[0] + ":" + s2[1]
                                aliert9 = svDop
                                builder.append(color).append(svDop).append("</font>").append("<br><br>\n")
                            } else builder.append(line)
                            try {
                                builder.append(zmenyiaChastki.zmenya(0))
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                            }
                        }
                        else -> {
                            builder.append(line)
                        }
                    }
                } else {
                    builder.append(line)
                }
            }
            inputStream.close()
            return@withContext builder.toString()
        }
        val text = MainActivity.fromHtml(res).toSpannable()
        var string = aliert8
        var strLig = string.length
        var t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(8)
                    dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        string = aliert9
        strLig = string.length
        t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(9)
                    dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        string = "Пасьля чытаецца ікас 1 і кандак 1."
        strLig = string.length
        t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, 0)
                    anim.setDuration(1500).start()
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (resurs == "bogashlugbovya1") {
            string = "Дзе ёсьць звычай, у нядзелю, а таксама ў суботу і на вялікія сьвяты (апрача сьвятаў Гасподніх) сьпяваюцца наступныя радкі з Пс 102:"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(1)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "У буднія дні сьпяваецца наступны антыфон (Пс 91):"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(2)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Іншы антыфон нядзельны і сьвяточны (Пс 145):"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(3)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Антыфон у буднія дні (Пс 92):"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(4)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Іншы антыфон сьвяточны і нядзельны (Мц 5:3-12):"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(5)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Малітва за памерлых"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(6)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Малітва за пакліканых"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia: DialogLiturgia = DialogLiturgia.getInstance(7)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Малітвы пасьля сьвятога прычасьця"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(this@Bogashlugbovya, MalitvyPasliaPrychascia::class.java)
                        startActivity(intent)
                        positionY = 0
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs.contains("bogashlugbovya") || resurs.contains("akafist") || resurs.contains("malitvy") || resurs.contains("ruzanec") || resurs.contains("ton")) {
            if (resurs.contains("ton")) mAutoScroll = false
            if (savedInstanceState == null) {
                if (k.getBoolean("autoscrollAutostart", false) && mAutoScroll) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    autoStartScroll()
                }
            }
        } else {
            mAutoScroll = false
        }
        binding.textView.text = text
        if (savedInstanceState?.getBoolean("seach") == true) {
            findAllAsanc()
        }
        positionY = k.getInt(resurs + "Scroll", 0)
        binding.scrollView2.post { binding.scrollView2.smoothScrollBy(0, positionY) }
        if (dzenNoch) binding.imageView6.setImageResource(by.carkva_gazeta.malitounik.R.drawable.find_up_black)
        binding.imageView6.setOnClickListener { findNext(previous = true) }
        binding.textSearch.addTextChangedListener(object : TextWatcher {
            var editPosition = 0
            var check = 0
            var editch = true

            override fun afterTextChanged(s: Editable?) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                edit = edit.replace("И", "І")
                edit = edit.replace("Щ", "Ў")
                edit = edit.replace("Ъ", "'")
                if (edit.length >= 3) {
                    if (editch) {
                        if (check != 0) {
                            binding.textSearch.removeTextChangedListener(this)
                            binding.textSearch.setText(edit)
                            binding.textSearch.setSelection(editPosition)
                            binding.textSearch.addTextChangedListener(this)
                        }
                    }
                    findAllAsanc()
                } else {
                    findRemoveSpan()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                editch = count != after
                check = after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editPosition = start + count
            }
        })
        if (dzenNoch) binding.imageView5.setImageResource(by.carkva_gazeta.malitounik.R.drawable.find_niz_back)
        binding.imageView5.setOnClickListener { findNext() }
        invalidateOptionsMenu()
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            var autoTime: Long = 10000
            for (i in 0..15) {
                if (i == k.getInt("autoscrollAutostartTime", 5)) {
                    autoTime = (i + 5) * 1000L
                    break
                }
            }
            autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                delay(autoTime)
                startAutoScroll()
                invalidateOptionsMenu()
            }
        }
    }

    private fun stopAutoStartScroll() {
        autoStartScrollJob?.cancel()
    }

    private fun startProcent(delayTime: Long = 1000) {
        procentJob?.cancel()
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(delayTime)
            bindingprogress.progress.visibility = View.GONE
            bindingprogress.fontSize.visibility = View.GONE
            bindingprogress.brighess.visibility = View.GONE
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditors = k.edit()
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.apply()
            }
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            autoScrollJob?.cancel()
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (diffScroll !in 0..1) {
            val prefEditors = k.edit()
            prefEditors.putBoolean("autoscroll", true)
            prefEditors.apply()
            binding.actionMinus.visibility = View.VISIBLE
            binding.actionPlus.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            stopAutoStartScroll()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        binding.scrollView2.smoothScrollBy(0, 2)
                    }
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding.scrollView2.smoothScrollBy(0, 0)
            startAutoScroll()
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.WebView) {
            stopAutoStartScroll()
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> mActionDown = true
                MotionEvent.ACTION_UP -> mActionDown = false
                MotionEvent.ACTION_MOVE -> {
                    val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
                }
            }
            return false
        }
        if (id == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                MainActivity.brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    val proc: Int
                    if (x < otstup) {
                        bindingprogress.brighess.visibility = View.VISIBLE
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        var minmax = ""
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) minmax = " (мін)"
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) minmax = " (макс)"
                        bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.font_sp, fontBiblia.toInt(), minmax)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (y > heightConstraintLayout - otstup) {
                        spid = k.getInt("autoscrollSpid", 60)
                        proc = 100 - (spid - 15) * 100 / 215
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                        bindingprogress.progressTitle.text = ""
                        bindingprogress.progress.visibility = View.VISIBLE
                        startProcent()
                        autoscroll = k.getBoolean("autoscroll", false)
                        if (!autoscroll) {
                            startAutoScroll()
                            invalidateOptionsMenu()
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        val itemVybranoe: MenuItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        if (resurs.contains("bogashlugbovya") || resurs.contains("akafist") || resurs.contains("malitvy") || resurs.contains("ruzanec")) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_share).isVisible = true
        }
        if (mAutoScroll) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
            } else {
                itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
            }
        } else {
            itemAuto.isVisible = false
            stopAutoScroll()
        }
        var spanString = SpannableString(itemAuto.title.toString())
        var end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString

        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)

        spanString = SpannableString(itemVybranoe.title.toString())
        end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemVybranoe.title = spanString
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        stopAutoStartScroll()
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.akafist, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor: Editor = k.edit()
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            editVybranoe = true
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_find) {
            binding.textSearch.visibility = View.VISIBLE
            binding.textCount.visibility = View.VISIBLE
            binding.imageView6.visibility = View.VISIBLE
            binding.imageView5.visibility = View.VISIBLE
            binding.textSearch.requestFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_orientation) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                requestedOrientation = orientation
                prefEditor.putBoolean("orientation", true)
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                prefEditor.putBoolean("orientation", false)
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            editVybranoe = true
            men = setVybranoe(this, resurs, title)
            if (men) {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.addVybranoe))
            }
            invalidateOptionsMenu()
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
            if (k.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=2&file=$resurs")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
        prefEditor.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else if (binding.textSearch.visibility == View.VISIBLE) {
            binding.textSearch.visibility = View.GONE
            binding.textCount.visibility = View.GONE
            binding.imageView6.visibility = View.GONE
            binding.imageView5.visibility = View.GONE
            binding.textSearch.setText("")
            findRemoveSpan()
            val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
        } else {
            if (editVybranoe) onSupportNavigateUp()
            else super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        val prefEditor = k.edit()
        prefEditor.putInt(resurs + "Scroll", positionY)
        prefEditor.apply()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        autoStartScrollJob?.cancel()
        procentJob?.cancel()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        autoscroll = k.getBoolean("autoscroll", false)
        if (autoscroll) {
            startAutoScroll()
        }
        spid = k.getInt("autoscrollSpid", 60)
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun hide() {
        supportActionBar?.hide()
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
        outState.putBoolean("editVybranoe", editVybranoe)
        if (binding.textSearch.visibility == View.VISIBLE) outState.putBoolean("seach", true)
        else outState.putBoolean("seach", false)
    }

    private data class SpanStr(val color: Int, val start: Int, val size: Int)
}
