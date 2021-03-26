package by.carkva_gazeta.malitounik

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
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.PesnyBinding
import by.carkva_gazeta.malitounik.databinding.ProgressPesnyAllBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

class PesnyAll : AppCompatActivity(), OnTouchListener, DialogFontSize.DialogFontSizeListener {

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
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var n = 0
    private var title = ""
    private var men = false
    private var resurs = ""
    private var checkSetDzenNoch = false
    private lateinit var binding: PesnyBinding
    private lateinit var bindingprogress: ProgressPesnyAllBinding
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null

    companion object {
        val resursMap = ArrayMap<String, Int>()

        init {
            resursMap["pesny_bag_0"] = R.raw.pesny_bag_0
            resursMap["pesny_bag_1"] = R.raw.pesny_bag_1
            resursMap["pesny_bag_2"] = R.raw.pesny_bag_2
            resursMap["pesny_bag_3"] = R.raw.pesny_bag_3
            resursMap["pesny_bag_4"] = R.raw.pesny_bag_4
            resursMap["pesny_bag_5"] = R.raw.pesny_bag_5
            resursMap["pesny_bag_6"] = R.raw.pesny_bag_6
            resursMap["pesny_bag_7"] = R.raw.pesny_bag_7
            resursMap["pesny_bag_8"] = R.raw.pesny_bag_8
            resursMap["pesny_bag_9"] = R.raw.pesny_bag_9
            resursMap["pesny_bag_10"] = R.raw.pesny_bag_10
            resursMap["pesny_bag_11"] = R.raw.pesny_bag_11
            resursMap["pesny_bag_12"] = R.raw.pesny_bag_12
            resursMap["pesny_bag_13"] = R.raw.pesny_bag_13
            resursMap["pesny_bag_14"] = R.raw.pesny_bag_14
            resursMap["pesny_bag_15"] = R.raw.pesny_bag_15
            resursMap["pesny_bel_0"] = R.raw.pesny_bel_0
            resursMap["pesny_bel_1"] = R.raw.pesny_bel_1
            resursMap["pesny_bel_2"] = R.raw.pesny_bel_2
            resursMap["pesny_bel_3"] = R.raw.pesny_bel_3
            resursMap["pesny_bel_4"] = R.raw.pesny_bel_4
            resursMap["pesny_bel_5"] = R.raw.pesny_bel_5
            resursMap["pesny_bel_6"] = R.raw.pesny_bel_6
            resursMap["pesny_bel_7"] = R.raw.pesny_bel_7
            resursMap["pesny_bel_8"] = R.raw.pesny_bel_8
            resursMap["pesny_bel_9"] = R.raw.pesny_bel_9
            resursMap["pesny_kal_0"] = R.raw.pesny_kal_0
            resursMap["pesny_kal_1"] = R.raw.pesny_kal_1
            resursMap["pesny_kal_2"] = R.raw.pesny_kal_2
            resursMap["pesny_kal_3"] = R.raw.pesny_kal_3
            resursMap["pesny_kal_4"] = R.raw.pesny_kal_4
            resursMap["pesny_kal_5"] = R.raw.pesny_kal_5
            resursMap["pesny_kal_6"] = R.raw.pesny_kal_6
            resursMap["pesny_kal_7"] = R.raw.pesny_kal_7
            resursMap["pesny_kal_8"] = R.raw.pesny_kal_8
            resursMap["pesny_kal_9"] = R.raw.pesny_kal_9
            resursMap["pesny_kal_10"] = R.raw.pesny_kal_10
            resursMap["pesny_kal_11"] = R.raw.pesny_kal_11
            resursMap["pesny_kal_12"] = R.raw.pesny_kal_12
            resursMap["pesny_kal_13"] = R.raw.pesny_kal_13
            resursMap["pesny_kal_14"] = R.raw.pesny_kal_14
            resursMap["pesny_kal_15"] = R.raw.pesny_kal_15
            resursMap["pesny_kal_16"] = R.raw.pesny_kal_16
            resursMap["pesny_kal_17"] = R.raw.pesny_kal_17
            resursMap["pesny_kal_18"] = R.raw.pesny_kal_18
            resursMap["pesny_kal_19"] = R.raw.pesny_kal_19
            resursMap["pesny_kal_20"] = R.raw.pesny_kal_20
            resursMap["pesny_kal_21"] = R.raw.pesny_kal_21
            resursMap["pesny_prasl_0"] = R.raw.pesny_prasl_0
            resursMap["pesny_prasl_1"] = R.raw.pesny_prasl_1
            resursMap["pesny_prasl_2"] = R.raw.pesny_prasl_2
            resursMap["pesny_prasl_3"] = R.raw.pesny_prasl_3
            resursMap["pesny_prasl_4"] = R.raw.pesny_prasl_4
            resursMap["pesny_prasl_5"] = R.raw.pesny_prasl_5
            resursMap["pesny_prasl_6"] = R.raw.pesny_prasl_6
            resursMap["pesny_prasl_7"] = R.raw.pesny_prasl_7
            resursMap["pesny_prasl_8"] = R.raw.pesny_prasl_8
            resursMap["pesny_prasl_9"] = R.raw.pesny_prasl_9
            resursMap["pesny_prasl_10"] = R.raw.pesny_prasl_10
            resursMap["pesny_prasl_11"] = R.raw.pesny_prasl_11
            resursMap["pesny_prasl_12"] = R.raw.pesny_prasl_12
            resursMap["pesny_prasl_13"] = R.raw.pesny_prasl_13
            resursMap["pesny_prasl_14"] = R.raw.pesny_prasl_14
            resursMap["pesny_prasl_15"] = R.raw.pesny_prasl_15
            resursMap["pesny_prasl_16"] = R.raw.pesny_prasl_16
            resursMap["pesny_prasl_17"] = R.raw.pesny_prasl_17
            resursMap["pesny_prasl_18"] = R.raw.pesny_prasl_18
            resursMap["pesny_prasl_19"] = R.raw.pesny_prasl_19
            resursMap["pesny_prasl_20"] = R.raw.pesny_prasl_20
            resursMap["pesny_prasl_21"] = R.raw.pesny_prasl_21
            resursMap["pesny_prasl_22"] = R.raw.pesny_prasl_22
            resursMap["pesny_prasl_23"] = R.raw.pesny_prasl_23
            resursMap["pesny_prasl_24"] = R.raw.pesny_prasl_24
            resursMap["pesny_prasl_25"] = R.raw.pesny_prasl_25
            resursMap["pesny_prasl_26"] = R.raw.pesny_prasl_26
            resursMap["pesny_prasl_27"] = R.raw.pesny_prasl_27
            resursMap["pesny_prasl_28"] = R.raw.pesny_prasl_28
            resursMap["pesny_prasl_29"] = R.raw.pesny_prasl_29
            resursMap["pesny_prasl_30"] = R.raw.pesny_prasl_30
            resursMap["pesny_prasl_31"] = R.raw.pesny_prasl_31
            resursMap["pesny_prasl_32"] = R.raw.pesny_prasl_32
            resursMap["pesny_prasl_33"] = R.raw.pesny_prasl_33
            resursMap["pesny_prasl_34"] = R.raw.pesny_prasl_34
            resursMap["pesny_prasl_35"] = R.raw.pesny_prasl_35
            resursMap["pesny_prasl_36"] = R.raw.pesny_prasl_36
            resursMap["pesny_prasl_37"] = R.raw.pesny_prasl_37
            resursMap["pesny_prasl_38"] = R.raw.pesny_prasl_38
            resursMap["pesny_prasl_39"] = R.raw.pesny_prasl_39
            resursMap["pesny_prasl_40"] = R.raw.pesny_prasl_40
            resursMap["pesny_prasl_41"] = R.raw.pesny_prasl_41
            resursMap["pesny_prasl_42"] = R.raw.pesny_prasl_42
            resursMap["pesny_prasl_43"] = R.raw.pesny_prasl_43
            resursMap["pesny_prasl_44"] = R.raw.pesny_prasl_44
            resursMap["pesny_prasl_45"] = R.raw.pesny_prasl_45
            resursMap["pesny_prasl_46"] = R.raw.pesny_prasl_46
            resursMap["pesny_prasl_47"] = R.raw.pesny_prasl_47
            resursMap["pesny_prasl_48"] = R.raw.pesny_prasl_48
            resursMap["pesny_prasl_49"] = R.raw.pesny_prasl_49
            resursMap["pesny_prasl_50"] = R.raw.pesny_prasl_50
            resursMap["pesny_prasl_51"] = R.raw.pesny_prasl_51
            resursMap["pesny_prasl_52"] = R.raw.pesny_prasl_52
            resursMap["pesny_prasl_53"] = R.raw.pesny_prasl_53
            resursMap["pesny_prasl_54"] = R.raw.pesny_prasl_54
            resursMap["pesny_prasl_55"] = R.raw.pesny_prasl_55
            resursMap["pesny_prasl_56"] = R.raw.pesny_prasl_56
            resursMap["pesny_prasl_57"] = R.raw.pesny_prasl_57
            resursMap["pesny_prasl_58"] = R.raw.pesny_prasl_58
            resursMap["pesny_prasl_59"] = R.raw.pesny_prasl_59
            resursMap["pesny_prasl_60"] = R.raw.pesny_prasl_60
            resursMap["pesny_prasl_61"] = R.raw.pesny_prasl_61
            resursMap["pesny_prasl_62"] = R.raw.pesny_prasl_62
            resursMap["pesny_prasl_63"] = R.raw.pesny_prasl_63
            resursMap["pesny_prasl_64"] = R.raw.pesny_prasl_64
            resursMap["pesny_prasl_65"] = R.raw.pesny_prasl_65
            resursMap["pesny_prasl_66"] = R.raw.pesny_prasl_66
            resursMap["pesny_prasl_67"] = R.raw.pesny_prasl_67
            resursMap["pesny_prasl_68"] = R.raw.pesny_prasl_68
            resursMap["pesny_prasl_69"] = R.raw.pesny_prasl_69
            resursMap["pesny_prasl_70"] = R.raw.pesny_prasl_70
            resursMap["pesny_prasl_71"] = R.raw.pesny_prasl_71
            resursMap["pesny_taize_0"] = R.raw.pesny_taize_0
            resursMap["pesny_taize_1"] = R.raw.pesny_taize_1
            resursMap["pesny_taize_2"] = R.raw.pesny_taize_2
            resursMap["pesny_taize_3"] = R.raw.pesny_taize_3
            resursMap["pesny_taize_4"] = R.raw.pesny_taize_4
            resursMap["pesny_taize_5"] = R.raw.pesny_taize_5
            resursMap["pesny_taize_6"] = R.raw.pesny_taize_6
            resursMap["pesny_taize_7"] = R.raw.pesny_taize_7
            resursMap["pesny_taize_8"] = R.raw.pesny_taize_8
            resursMap["pesny_taize_9"] = R.raw.pesny_taize_9
            resursMap["pesny_taize_10"] = R.raw.pesny_taize_10
            resursMap["pesny_taize_11"] = R.raw.pesny_taize_11
            resursMap["pesny_taize_12"] = R.raw.pesny_taize_12
            resursMap["pesny_taize_13"] = R.raw.pesny_taize_13
            resursMap["pesny_taize_14"] = R.raw.pesny_taize_14
            resursMap["pesny_taize_15"] = R.raw.pesny_taize_15
            resursMap["pesny_taize_16"] = R.raw.pesny_taize_16
        }

        private fun setVybranoe(context: Context, resurs: String, title: String): Boolean {
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

        private fun vybranoeIndex(): Long {
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

        private fun checkVybranoe(context: Context, resurs: String): Boolean {
            var check = false
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                val gson = Gson()
                if (file.exists()) {
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    MenuVybranoe.vybranoe = gson.fromJson(file.readText(), type)
                } else {
                    return false
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) { //MenuVybranoe.vybranoe.remove(i)
                        check = true
                        break
                    }
                }
            } catch (t: Throwable) {
                file.delete()
                check = false
            }
            return check
        }
    }

    private fun findAllAsanc(search: String) {
        CoroutineScope(Dispatchers.Main).launch {
            findAll(search)
        }
    }

    private fun findAll(search: String, position: Int = 0) {
        val text = binding.textView.text as SpannableString
        val searchLig = search.length
        val strPosition = text.indexOf(search, position, true)
        if (strPosition != -1) {
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorBezPosta)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_text)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            findAll(search,strPosition + 1)
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = PesnyBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        binding.constraint.setOnTouchListener(this)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        if (dzenNoch) {
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            bindingprogress.actionPlusBrighess.setImageResource(R.drawable.plus_v_kruge_black)
            bindingprogress.actionMinusBrighess.setImageResource(R.drawable.minus_v_kruge_black)
            bindingprogress.actionPlusFont.setImageResource(R.drawable.plus_v_kruge_black)
            bindingprogress.actionMinusFont.setImageResource(R.drawable.minus_v_kruge_black)
        }
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        title = intent.extras?.getString("pesny", "") ?: ""
        resurs = intent.extras?.getString("type", "pesny_prasl_0") ?: "pesny_prasl_0"
        val pesny = resursMap[resurs] ?: -1
        val builder = StringBuilder()
        if (pesny != -1) {
            val inputStream = resources.openRawResource(pesny)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.use { bufferedReader ->
                bufferedReader.forEachLine {
                    line = it
                    if (dzenNoch) line = line.replace("#d00505", "#f44336")
                    builder.append(line)
                }
            }
        } else {
            builder.append(getString(R.string.error_ch))
        }
        binding.textView.text = MainActivity.fromHtml(builder.toString())
        val search = intent.extras?.getString("search", "") ?: ""
        if (search != "")
            findAllAsanc(search)
        men = checkVybranoe(this, resurs)
        bindingprogress.actionPlusFont.setOnClickListener {
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                var max = ""
                if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) max = " (макс)"
                bindingprogress.progressText.text = getString(R.string.font_sp, fontBiblia.toInt(), max)
                bindingprogress.progressTitle.text = getString(R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
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
                bindingprogress.progressText.text = getString(R.string.font_sp, fontBiblia.toInt(), min)
                bindingprogress.progressTitle.text = getString(R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
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
                bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
                MainActivity.checkBrightness = false
            }
        }
        bindingprogress.actionMinusBrighess.setOnClickListener {
            if (MainActivity.brightness > 0) {
                MainActivity.brightness = MainActivity.brightness - 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
                MainActivity.checkBrightness = false
            }
        }
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
            binding.toolbar.popupTheme = R.style.AppCompatDark
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
            bindingprogress.fontSize.visibility = View.GONE
            bindingprogress.brighess.visibility = View.GONE
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
                        bindingprogress.brighess.visibility = View.VISIBLE
                        bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        var minmax = ""
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) minmax = " (мін)"
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) minmax = " (макс)"
                        bindingprogress.progressText.text = getString(R.string.font_sp, fontBiblia.toInt(), minmax)
                        bindingprogress.progressTitle.text = getString(R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        startProcent()
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_share).isVisible = true
        menu.findItem(R.id.action_auto).isVisible = false
        menu.findItem(R.id.action_find).isVisible = false
        if (men) {
            menu.findItem(R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, R.drawable.star_big_on)
            menu.findItem(R.id.action_vybranoe).title = resources.getString(R.string.vybranoe_del)
        } else {
            menu.findItem(R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, R.drawable.star_big_off)
            menu.findItem(R.id.action_vybranoe).title = resources.getString(R.string.vybranoe)
        }
        menu.findItem(R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        val item = menu.findItem(R.id.action_vybranoe)
        val spanString = SpannableString(menu.findItem(R.id.action_vybranoe).title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        item.title = spanString
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.akafist, menu)
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
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor: Editor = k.edit()
        val id = item.itemId
        if (id == R.id.action_dzen_noch) {
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
        if (id == R.id.action_vybranoe) {
            men = setVybranoe(this, resurs, title)
            if (men) {
                MainActivity.toastView(this, getString(R.string.addVybranoe))
            }
            invalidateOptionsMenu()
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
        }
        if (id == R.id.action_fullscreen) {
            if (k.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        if (id == R.id.action_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=1&file=$resurs")
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
    }
}