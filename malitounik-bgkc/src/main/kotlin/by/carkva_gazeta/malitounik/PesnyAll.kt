package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import by.carkva_gazeta.malitounik.databinding.PesnyBinding
import by.carkva_gazeta.malitounik.databinding.ProgressPesnyAllBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

class PesnyAll : BaseActivity(), OnTouchListener, DialogFontSize.DialogFontSizeListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var checkVybranoe = false
    private var n = 0
    private var title = ""
    private var men = false
    private var resurs = ""
    private lateinit var binding: PesnyBinding
    private lateinit var bindingprogress: ProgressPesnyAllBinding
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val shareLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val cw = Calendar.getInstance()
        val intent = Intent(this, ReceiverBroad::class.java)
        intent.putExtra("file", "$resurs.html")
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val pIntent = PendingIntent.getBroadcast(this, 30, intent, flags)
        SettingsActivity.setAlarm(cw.timeInMillis + 10 * 60 * 1000, pIntent)
    }

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
            resursMap["pesny_prasl_72"] = R.raw.pesny_prasl_72
            resursMap["pesny_prasl_73"] = R.raw.pesny_prasl_73
            resursMap["pesny_prasl_74"] = R.raw.pesny_prasl_74
            resursMap["pesny_prasl_75"] = R.raw.pesny_prasl_75
            resursMap["pesny_prasl_aliluja"] = R.raw.pesny_prasl_aliluja
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
                if (file.exists() && MenuVybranoe.vybranoe.isEmpty()) {
                    val type = TypeToken.getParameterized(ArrayList::class.java, VybranoeData::class.java).type
                    MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
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
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            if (!file.exists()) return false
            try {
                val gson = Gson()
                if (MenuVybranoe.vybranoe.isEmpty()) {
                    val type = TypeToken.getParameterized(ArrayList::class.java, VybranoeData::class.java).type
                    MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
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

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (fullscreenPage) {
            binding.constraint.post {
                hide()
            }
        }
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = PesnyBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        binding.constraint.setOnTouchListener(this)
        fullscreenPage = savedInstanceState?.getBoolean("fullscreen") ?: k.getBoolean("fullscreenPage", false)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            bindingprogress.brighessPlus.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            bindingprogress.brighessMinus.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            bindingprogress.fontSizePlus.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            bindingprogress.fontSizeMinus.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
        }
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        title = intent.extras?.getString("pesny", "") ?: ""
        resurs = intent.extras?.getString("type", "pesny_prasl_0") ?: "pesny_prasl_0"
        val pesny = resursMap[resurs] ?: R.raw.pesny_prasl_0
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
        men = checkVybranoe(this, resurs)
        checkVybranoe = men
        bindingprogress.fontSizePlus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) bindingprogress.progressTitle.text = getString(R.string.max_font)
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                bindingprogress.progressText.text = getString(R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent()
        }
        bindingprogress.fontSizeMinus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) bindingprogress.progressTitle.text = getString(R.string.min_font)
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                bindingprogress.progressText.text = getString(R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor = k.edit()
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
                bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(R.string.Bright)
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
                bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent()
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        setTollbarTheme()
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
                        bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.progressText.text = getString(R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        startProcent()
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareMenu(menu: Menu) {
        //menu.findItem(R.id.action_share).isVisible = true
        menu.findItem(R.id.action_auto).isVisible = false
        menu.findItem(R.id.action_find).isVisible = false
        if (men) {
            menu.findItem(R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, R.drawable.star_big_on)
            menu.findItem(R.id.action_vybranoe).title = resources.getString(R.string.vybranoe_del)
        } else {
            menu.findItem(R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, R.drawable.star_big_off)
            menu.findItem(R.id.action_vybranoe).title = resources.getString(R.string.vybranoe)
        }
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        if (k.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
        val item = menu.findItem(R.id.action_vybranoe)
        val spanString = SpannableString(menu.findItem(R.id.action_vybranoe).title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        item.title = spanString
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.bogashlugbovya, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val prefEditor = k.edit()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
            return true
        }
        if (id == R.id.action_vybranoe) {
            men = setVybranoe(this, resurs, title)
            if (men) {
                MainActivity.toastView(this, getString(R.string.addVybranoe))
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == R.id.action_fullscreen) {
            hide()
            return true
        }
        if (id == R.id.action_share) {
            val pesny = resursMap[resurs] ?: R.raw.pesny_prasl_0
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
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$resurs.html")
                file.writer().use {
                    it.write(builder.toString())
                }
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,"by.carkva_gazeta.malitounik.fileprovider", file))
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.set_log_file))
                sendIntent.type = "text/html"
                shareLauncher.launch(Intent.createChooser(sendIntent, getString(R.string.set_log_file)))
            } else {
                MainActivity.toastView(this, getString(R.string.error_ch))
            }
            return true
        }
        prefEditor.apply()
        if (id == R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val inputStream = resources.openRawResource(resursMap[resurs] ?: R.raw.pesny_prasl_0)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", resurs)
                intent.putExtra("title", title)
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
            return true
        }
        return false
    }

    override fun onBack() {
        if (intent.extras?.getBoolean("chekVybranoe", false) == true && men != checkVybranoe) {
            setResult(200)
        }
        super.onBack()
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.VISIBLE
        binding.actionBack.animation = animation
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
    }
}