package by.carkva_gazeta.resources

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogBrightness
import by.carkva_gazeta.malitounik.DialogFontSize
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.DialogHelpFullScreenSettings
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.InteractiveScrollView
import by.carkva_gazeta.malitounik.InteractiveScrollView.OnBottomReachedListener
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.AkafistChytanneBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Calendar

class Chytanne : BaseActivity(), OnTouchListener, DialogFontSizeListener, InteractiveScrollView.OnInteractiveScrollChangedCallback, LinkMovementMethodCheck.LinkMovementMethodCheckListener, DialogHelpFullScreen.DialogFullScreenHelpListener, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener, DialogPerevodBiblii.DialogPerevodBibliiListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var mActionDown = false
    private lateinit var binding: AkafistChytanneBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJobBrightness: Job? = null
    private var procentJobFont: Job? = null
    private var procentJobAuto: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var diffScroll = false
    private var titleTwo = SpannableString("")
    private var firstTextPosition = ""
    private var orientation = Configuration.ORIENTATION_UNDEFINED
    private var linkMovementMethodCheck: LinkMovementMethodCheck? = null
    private var mun = 0
    private var day = 1
    private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.textSize = fontBiblia
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = AkafistChytanneBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            orientation = savedInstanceState.getInt("orientation")
            binding.titleToolbar.text = savedInstanceState.getString("tollBarText", getString(by.carkva_gazeta.malitounik.R.string.czytanne3, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])) ?: getString(by.carkva_gazeta.malitounik.R.string.czytanne3, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne3, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
            if (k.getBoolean("autoscrollAutostart", false)) {
                autoStartScroll()
            }
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        binding.constraint.setOnTouchListener(this)
        binding.InteractiveScroll.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached(checkDiff: Boolean) {
                diffScroll = checkDiff
                if (diffScroll) {
                    autoscroll = false
                    stopAutoScroll()
                }
                invalidateOptionsMenu()
            }

            override fun onTouch(action: Boolean) {
                mActionDown = action
            }
        })
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            bindingprogress.seekBarBrighess.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
        }
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        bindingprogress.seekBarBrighess.progress = MainActivity.brightness
        perevod = k.getString("perevodChytanne", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
        checkDay()
        setChtenia(savedInstanceState)
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize(fontBiblia)
                }
                startProcent(MainActivity.PROGRESSACTIONFONT)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        bindingprogress.seekBarBrighess.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (MainActivity.brightness != progress) {
                    MainActivity.brightness = progress
                    val lp = window.attributes
                    lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                    window.attributes = lp
                    bindingprogress.progressBrighess.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                    MainActivity.checkBrightness = false
                }
                startProcent(MainActivity.PROGRESSACTIONBRIGHESS)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTOLEFT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.InteractiveScroll.setOnScrollChangedCallback(this)
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun checkDay() {
        val c = Calendar.getInstance()
        if (!(mun == c[Calendar.MONTH] && day == c[Calendar.DATE])) {
            binding.appBarLayout.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_strogi_post)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val otstup2 = if (autoscroll) (50 * resources.displayMetrics.density).toInt()
        else 0
        val otstup3 = when {
            fullscreenPage && autoscroll -> (160 * resources.displayMetrics.density).toInt()
            autoscroll -> (110 * resources.displayMetrics.density).toInt()
            else -> 0
        }
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                MainActivity.brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    val proc: Int
                    if (x < otstup) {
                        bindingprogress.seekBarBrighess.progress = MainActivity.brightness
                        bindingprogress.progressBrighess.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        if (bindingprogress.seekBarBrighess.visibility == View.GONE) {
                            bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_right)
                            bindingprogress.seekBarBrighess.visibility = View.VISIBLE
                        }
                        startProcent(MainActivity.PROGRESSACTIONBRIGHESS)
                    }
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
                        bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
                            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
                            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
                        }
                        startProcent(MainActivity.PROGRESSACTIONFONT)
                    }
                    if (y > heightConstraintLayout - otstup && x < widthConstraintLayout - otstup3) {
                        spid = k.getInt("autoscrollSpid", 60)
                        proc = 100 - (spid - 15) * 100 / 215
                        bindingprogress.progressAuto.text = getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                        startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                        startAutoScroll()
                        invalidateOptionsMenu()
                    }
                }
            }
        }
        return true
    }

    override fun setPerevod(perevod: String) {
        val edit = k.edit()
        edit.putString("perevodChytanne", perevod)
        edit.apply()
        if (this.perevod != perevod) {
            this.perevod = perevod
            setChtenia(null)
        }
    }

    private fun setChtenia(savedInstanceState: Bundle?) {
        try {
            var w = intent.extras?.getString("cytanne") ?: ""
            val wOld = w
            w = MainActivity.removeZnakiAndSlovy(w)
            val split = w.split(";")
            var knigaN: String
            var knigaK = "0"
            var zaglnum = 0
            var zaglnumEnd = 0
            val ssbTitle = SpannableStringBuilder()
            var title = SpannableString("")
            for (i in split.indices) {
                val zaglavie = split[i].split(",")
                var zagl = ""
                var zaglavieName = ""
                for (e in zaglavie.indices) {
                    try {
                        val zaglav = zaglavie[e].trim()
                        val zag = zaglav.indexOf(" ", 2)
                        val zag1 = zaglav.indexOf(".")
                        val zag2 = zaglav.indexOf("-")
                        val zag3 = zaglav.indexOf(".", zag1 + 1)
                        val zagS = if (zag2 != -1) {
                            zaglav.substring(0, zag2)
                        } else {
                            zaglav
                        }
                        var glav = false
                        if (zag1 > zag2 && zag == -1) {
                            glav = true
                        } else if (zag != -1) {
                            zagl = zaglav.substring(0, zag)
                            val zaglavieName1 = split[i].trim()
                            zaglavieName = " " + zaglavieName1.substring(zag + 1)
                            zaglnum = zaglav.substring(zag + 1, zag1).toInt()
                        } else if (zag1 != -1) {
                            zaglnum = zaglav.substring(0, zag1).toInt()
                        }
                        if (glav) {
                            val zagS1 = zagS.indexOf(".")
                            if (zagS1 == -1) {
                                knigaN = zagS // Начало чтения
                            } else {
                                zaglnum = zagS.substring(0, zagS1).toInt()
                                knigaN = zagS.substring(zagS1 + 1)
                            }
                        } else if (zag2 == -1) { // Конец чтения
                            if (zag != -1) {
                                val zagS1 = zagS.indexOf(".")
                                zaglnum = zagS.substring(zag + 1, zagS1).toInt()
                                knigaN = zagS.substring(zagS1 + 1)
                            } else {
                                knigaN = zaglav
                            }
                            knigaK = knigaN
                        } else {
                            knigaN = zaglav.substring(zag1 + 1, zag2)
                        }
                        if (zag2 != -1 && zag3 != -1) {
                            zaglnumEnd = zaglav.substring(zag2 + 1, zag3).toInt()
                        }
                        if (glav) {
                            knigaK = zaglav.substring(zag1 + 1)
                        } else if (zag2 != -1) {
                            knigaK = if (zag3 == -1) {
                                zaglav.substring(zag2 + 1)
                            } else {
                                zaglav.substring(zag3 + 1)
                            }
                        }
                        var polstixaA = false
                        var polstixaB = false
                        if (knigaK.contains("а", true)) {
                            polstixaA = true
                            knigaK = knigaK.replace("а", "", true)
                        }
                        if (knigaN.contains("б", true)) {
                            polstixaB = true
                            knigaN = knigaN.replace("б", "", true)
                        }
                        var spln = ""
                        if (i > 0) spln = "\n"
                        var kniga = -1
                        if (zagl == "Мц") kniga = 0
                        if (zagl == "Мк") kniga = 1
                        if (zagl == "Лк") kniga = 2
                        if (zagl == "Ян") kniga = 3
                        if (zagl == "Дз") kniga = 4
                        if (zagl == "Як") kniga = 5
                        if (zagl == "1 Пт") kniga = 6
                        if (zagl == "2 Пт") kniga = 7
                        if (zagl == "1 Ян") kniga = 8
                        if (zagl == "2 Ян") kniga = 9
                        if (zagl == "3 Ян") kniga = 10
                        if (zagl == "Юд") kniga = 11
                        if (zagl == "Рым") kniga = 12
                        if (zagl == "1 Кар") kniga = 13
                        if (zagl == "2 Кар") kniga = 14
                        if (zagl == "Гал") kniga = 15
                        if (zagl == "Эф") kniga = 16
                        if (zagl == "Плп") kniga = 17
                        if (zagl == "Клс") kniga = 18
                        if (zagl == "1 Фес") kniga = 19
                        if (zagl == "2 Фес") kniga = 20
                        if (zagl == "1 Цім") kniga = 21
                        if (zagl == "2 Цім") kniga = 22
                        if (zagl == "Ціт") kniga = 23
                        if (zagl == "Піл") kniga = 24
                        if (zagl == "Гбр") kniga = 25
                        if (zagl == "Быц") kniga = 26
                        if (zagl == "Высл") kniga = 27
                        if (zagl == "Езк") kniga = 28
                        if (zagl == "Вых") kniga = 29
                        if (zagl == "Ёў") kniga = 30
                        if (zagl == "Зах") kniga = 31
                        if (zagl == "Ёіл") kniga = 32
                        if (zagl == "Саф") kniga = 33
                        if (zagl == "Іс") kniga = 34
                        if (zagl == "Ер" || zagl == "Ярэм") kniga = 35
                        if (zagl == "Дан") kniga = 36
                        if (zagl == "Лікі") kniga = 37
                        if (zagl == "Міх") kniga = 38
                        if (zagl == "Дрг") kniga = 39
                        if (zagl == "Мдр") kniga = 40
                        if (zagl == "Мал") kniga = 41
                        var inputStream: InputStream? = null
                        var errorChytanne = false
                        when (kniga) {
                            0 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian1)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan1)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin1)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_0, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            1 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian2)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan2)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin2)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_1, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            2 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian3)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan3)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin3)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_2, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            3 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian4)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan4)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin4)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_3, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            4 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian5)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan5)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin5)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_4, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            5 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian6)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan6)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin6)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_5, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            6 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian7)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan7)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin7)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_6, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            7 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian8)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan8)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin8)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_7, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            8 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian9)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan9)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin9)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_8, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            9 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian10)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan10)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin10)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_9, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            10 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian11)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan11)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin11)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_10, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            11 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian12)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan12)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin12)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_11, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            12 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian13)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan13)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin13)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_12, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            13 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian14)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan14)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin14)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_13, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            14 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian15)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan15)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin15)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_14, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            15 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian16)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan16)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin16)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_15, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            16 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian17)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan17)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin17)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_16, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            17 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian18)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan18)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin18)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_17, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            18 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian19)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan19)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin19)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_18, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            19 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian20)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan20)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin20)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_19, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            20 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian21)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan21)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin21)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_20, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            21 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian22)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan22)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin22)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_21, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            22 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian23)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan23)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin23)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_22, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            23 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian24)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan24)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin24)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_23, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            24 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian25)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan25)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin25)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_24, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            25 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian26)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan26)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin26)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_25, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            26 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias1)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas1)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis1)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_26, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            27 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias20)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas20)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis20)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_27, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            28 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias26)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas26)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis26)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_28, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            29 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias2)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas2)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis2)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_29, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            30 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias18)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas18)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis18)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_30, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            31 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias38)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas38)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis38)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_31, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            32 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias29)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas29)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis29)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_32, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            33 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias36)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas36)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis36)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_33, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            34 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias23)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas23)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis23)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_34, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            35 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias24)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas24)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis24)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_35, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            36 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias27)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas27)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis27)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_36, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            37 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias4)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas4)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis4)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_37, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            38 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias33)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas33)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis33)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_38, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            39 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias5)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas5)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis5)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_39, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            40 -> {
                                inputStream = resources.openRawResource(R.raw.carniauskis42)
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_40, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            41 -> {
                                when (perevod) {
                                    DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias39)
                                    DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas39)
                                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis39)
                                }
                                title = if (e == 0) {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_41, spln, zaglavieName))
                                } else {
                                    SpannableString(getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }

                            else -> {
                                errorChytanne = true
                            }
                        }
                        ssbTitle.append(title)
                        if (inputStream == null) errorChytanne = true
                        if (!errorChytanne) {
                            if (e == 0) ssbTitle.setSpan(StyleSpan(Typeface.BOLD), ssbTitle.length - title.length, ssbTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            val builder = StringBuilder()
                            InputStreamReader(inputStream).use { inputStreamReader ->
                                val reader = BufferedReader(inputStreamReader)
                                var line: String
                                reader.forEachLine {
                                    line = it
                                    if (line.contains("//")) {
                                        val t1 = line.indexOf("//")
                                        line = line.substring(0, t1).trim()
                                        if (line != "") builder.append(line).append("\n")
                                    } else {
                                        builder.append(line).append("\n")
                                    }
                                }
                            }
                            val split2 = builder.toString().split("===")
                            var spl: String
                            var desK1: Int
                            var desN: Int
                            spl = split2[zaglnum].trim()
                            desN = spl.indexOf(knigaN)
                            if (knigaN == knigaK && zag3 == -1) {
                                desK1 = desN
                            } else {
                                desK1 = spl.indexOf(knigaK)
                                if (desK1 == -1) {
                                    val splAll = spl.split("\n").size
                                    desK1 = spl.indexOf("$splAll.")
                                }
                                if (zag3 != -1 || glav) {
                                    val spl1 = split2[zaglnum].trim()
                                    val des1 = spl1.length
                                    desN = spl1.indexOf(knigaN)
                                    if (zaglnumEnd - zaglnum == 2) {
                                        val spl3 = split2[zaglnum + 2].trim()
                                        val spl2 = split2[zaglnum + 1].trim()
                                        val des2 = spl2.length
                                        desK1 = spl3.indexOf(knigaK)
                                        desK1 += des1 + des2 + 2
                                        spl = spl1 + "\n" + spl2 + "\n" + spl3
                                    } else {
                                        val spl2 = split2[zaglnum + 1].trim()
                                        desK1 = spl2.indexOf(knigaK)
                                        desK1 += des1 + 1
                                        spl = spl1 + "\n" + spl2
                                    }
                                    zaglnum += 1
                                }
                            }
                            var desK = spl.indexOf("\n", desK1)
                            if (desK == -1) {
                                desK = spl.length
                            }
                            val textBiblia = spl.substring(desN, desK).toSpannable()
                            if (polstixaA) {
                                val t2 = textBiblia.indexOf(knigaK)
                                val t3 = textBiblia.indexOf(".", t2)
                                var t1 = textBiblia.indexOf(":", t2)
                                if (t1 == -1) t1 = textBiblia.indexOf(";", t3 + 1)
                                if (t1 == -1) t1 = textBiblia.indexOf(".", t3 + 1)
                                if (t1 != -1) textBiblia.setSpan(StrikethroughSpan(), t1 + 1, textBiblia.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            if (polstixaB) {
                                val t2 = textBiblia.indexOf("\n")
                                val textPol = textBiblia.substring(0, t2 + 1)
                                val t3 = textPol.indexOf(".")
                                var t1 = textPol.indexOf(":")
                                if (t1 == -1) t1 = textPol.indexOf(";", t3 + 1)
                                if (t1 == -1) t1 = textPol.indexOf(".", t3 + 1)
                                if (t1 != -1) textBiblia.setSpan(StrikethroughSpan(), t3 + 1, t1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            ssbTitle.append("\n").append(setIndexBiblii(textBiblia)).append("\n")
                        } else {
                            ssbTitle.append("\n").append(error())
                        }
                    } catch (t: Throwable) {
                        ssbTitle.append("\n").append(error())
                    }
                    if (i == 1 && e == 0) titleTwo = title
                }
                binding.textView.text = ssbTitle.trim()
                binding.textView.movementMethod = setLinkMovementMethodCheck()
                if (dzenNoch) binding.textView.setLinkTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
            }
            if (k.getBoolean("utran", true) && wOld.contains("На ютрані:") && savedInstanceState == null) {
                binding.textView.post {
                    binding.textView.layout?.let { layout ->
                        val strPosition = binding.textView.text.indexOf(titleTwo.toString().trim(), ignoreCase = true)
                        val line = layout.getLineForOffset(strPosition)
                        val y = layout.getLineTop(line)
                        val anim = ObjectAnimator.ofInt(binding.InteractiveScroll, "scrollY", binding.InteractiveScroll.scrollY, y)
                        anim.setDuration(1000).start()
                    }
                }
            }
            if (savedInstanceState != null) {
                binding.textView.post {
                    val textline = savedInstanceState.getString("textLine", "")
                    if (textline != "") {
                        binding.textView.layout?.let { layout ->
                            val index = binding.textView.text.indexOf(textline)
                            val line = layout.getLineForOffset(index)
                            val y = layout.getLineTop(line)
                            binding.InteractiveScroll.scrollY = y
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            binding.textView.text = error()
        }
    }

    private fun setIndexBiblii(ssb: Spannable): Spannable {
        val list = ssb.split("\n")
        val result = SpannableStringBuilder()
        for (glava in list.indices) {
            val stext = SpannableString(list[glava])
            val t1 = list[glava].indexOf(" ")
            if (t1 != -1) {
                val subText = list[glava].substring(0, t1)
                if (subText.isDigitsOnly()) {
                    stext.setSpan(SuperscriptSpan(), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (dzenNoch) stext.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else stext.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            result.append(stext).append("\n")
        }
        return result
    }

    override fun linkMovementMethodCheckOnTouch(onTouch: Boolean) {
        mActionDown = onTouch
    }

    private fun setLinkMovementMethodCheck(): LinkMovementMethodCheck? {
        linkMovementMethodCheck = LinkMovementMethodCheck()
        linkMovementMethodCheck?.setLinkMovementMethodCheckListener(this)
        return linkMovementMethodCheck
    }

    private fun error(): SpannableString {
        val ssb = SpannableString(getString(by.carkva_gazeta.malitounik.R.string.error_ch))
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, getString(by.carkva_gazeta.malitounik.R.string.error_ch).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            if (spid < 166) {
                val autoTime = (230 - spid) / 10
                var count = 0
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        spid = 230
                        autoScroll()
                        while (true) {
                            delay(1000L)
                            if (!mActionDown && !MainActivity.dialogVisable) {
                                spid -= autoTime
                                count++
                            }
                            if (count == 10) {
                                break
                            }
                        }
                        startAutoScroll()
                    }
                }
            } else {
                startAutoScroll()
            }
        }
    }

    private fun stopAutoStartScroll() {
        autoStartScrollJob?.cancel()
    }

    private fun startProcent(progressAction: Int) {
        if (progressAction == MainActivity.PROGRESSACTIONBRIGHESS) {
            procentJobBrightness?.cancel()
            bindingprogress.progressBrighess.visibility = View.VISIBLE
            procentJobBrightness = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressBrighess.visibility = View.GONE
                delay(3000)
                if (bindingprogress.seekBarBrighess.visibility == View.VISIBLE) {
                    bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this@Chytanne, by.carkva_gazeta.malitounik.R.anim.slide_out_left)
                    bindingprogress.seekBarBrighess.visibility = View.GONE
                }
            }
        }
        if (progressAction == MainActivity.PROGRESSACTIONFONT) {
            procentJobFont?.cancel()
            bindingprogress.progressFont.visibility = View.VISIBLE
            procentJobFont = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressFont.visibility = View.GONE
                delay(3000)
                if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                    bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@Chytanne, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
                    bindingprogress.seekBarFontSize.visibility = View.GONE
                }
            }
        }
        if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT || progressAction == MainActivity.PROGRESSACTIONAUTORIGHT) {
            procentJobAuto?.cancel()
            bindingprogress.progressAuto.visibility = View.VISIBLE
            if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT) {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@Chytanne, by.carkva_gazeta.malitounik.R.drawable.selector_progress_auto_left)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@Chytanne, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            } else {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@Chytanne, by.carkva_gazeta.malitounik.R.drawable.selector_progress_red)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@Chytanne, by.carkva_gazeta.malitounik.R.color.colorWhite))
            }
            procentJobAuto = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressAuto.visibility = View.GONE
            }
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor: Editor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
            }
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            if (fullscreenPage && binding.actionBack.visibility == View.GONE) {
                val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                binding.actionBack.visibility = View.VISIBLE
                binding.actionBack.animation = animation2
            }
            autoScrollJob?.cancel()
            stopAutoStartScroll()
            binding.textView.setTextIsSelectable(true)
            binding.textView.movementMethod = setLinkMovementMethodCheck()
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (!diffScroll) {
            spid = k.getInt("autoscrollSpid", 60)
            if (binding.actionMinus.visibility == View.GONE) {
                binding.actionMinus.visibility = View.VISIBLE
                binding.actionPlus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                binding.actionMinus.animation = animation
                binding.actionPlus.animation = animation
                val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
                binding.actionBack.visibility = View.GONE
                binding.actionBack.animation = animation2
            }
            resetScreenJob?.cancel()
            stopAutoStartScroll()
            autoScroll()
        } else {
            val duration: Long = 1000
            ObjectAnimator.ofInt(binding.InteractiveScroll, "scrollY", 0).setDuration(duration).start()
            binding.InteractiveScroll.postDelayed({
                autoStartScroll()
                invalidateOptionsMenu()
            }, duration)
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            binding.textView.clearFocus()
            binding.textView.setTextIsSelectable(false)
            binding.textView.movementMethod = setLinkMovementMethodCheck()
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            autoscroll = true
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            invalidateOptionsMenu()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        binding.InteractiveScroll.smoothScrollBy(0, 2)
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.chtenia, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
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

    override fun onPrepareMenu(menu: Menu) {
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        when {
            autoscroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
            diffScroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_up)
            else -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = dzenNoch
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto_dzen_noch).isChecked = k.getBoolean("auto_dzen_noch", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto_dzen_noch).isVisible = SettingsActivity.isLightSensorExist()
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_utran).isChecked = k.getBoolean("utran", true)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_utran).isVisible = true
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_perevod).isVisible = true
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        stopAutoStartScroll()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) {
            binding.constraint.post {
                hideHelp()
            }
        }
        spid = k.getInt("autoscrollSpid", 60)
        autoscroll = k.getBoolean("autoscroll", false)
        if (autoscroll) {
            if (resources.configuration.orientation == orientation) {
                startAutoScroll()
            } else autoStartScroll()
        }
        orientation = resources.configuration.orientation
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditor = k.edit()
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_perevod) {
            val dialog = DialogPerevodBiblii.getInstance(false, k.getString("perevodChytanne", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI)
            dialog.show(supportFragmentManager, "DialogPerevodBiblii")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.putBoolean("auto_dzen_noch", false)
            prefEditor.apply()
            removelightSensor()
            recreate()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto_dzen_noch) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("auto_dzen_noch", true)
                setlightSensor()
            } else {
                prefEditor.putBoolean("auto_dzen_noch", false)
                removelightSensor()
            }
            prefEditor.apply()
            if (getCheckDzenNoch() != dzenNoch) {
                recreate()
            }
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_utran) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("utran", true)
            } else {
                prefEditor.putBoolean("utran", false)
            }
            prefEditor.apply()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            if (!k.getBoolean("fullscreenPage", false)) {
                var fullscreenCount = k.getInt("fullscreenCount", 0)
                if (fullscreenCount > 3) {
                    val dialogFullscreen = DialogHelpFullScreenSettings()
                    dialogFullscreen.show(supportFragmentManager, "DialogHelpFullScreenSettings")
                    fullscreenCount = 0
                } else {
                    fullscreenCount++
                    hideHelp()
                }
                prefEditor.putInt("fullscreenCount", fullscreenCount)
                prefEditor.apply()
            } else {
                hideHelp()
            }
            return true
        }
        return false
    }

    override fun dialogHelpFullScreenSettingsClose() {
        hideHelp()
    }

    override fun onDialogFullScreenHelpClose() {
        if (dzenNoch) binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
        else binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
        hide()
    }

    private fun hideHelp() {
        if (k.getBoolean("help_fullscreen", true)) {
            binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPost2))
            if (dzenNoch) binding.InteractiveScroll.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
            else binding.InteractiveScroll.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
            val dialogHelpListView = DialogHelpFullScreen()
            dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
            val prefEditors = k.edit()
            prefEditors.putBoolean("help_fullscreen", false)
            prefEditors.apply()
        } else {
            hide()
        }
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        if (binding.actionMinus.visibility == View.GONE) {
            binding.actionBack.visibility = View.VISIBLE
            binding.actionBack.animation = animation
        }
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onScroll(t: Int, oldt: Int) {
        binding.textView.layout?.let {
            val textForVertical = binding.textView.text.substring(it.getLineStart(it.getLineForVertical(t)), it.getLineEnd(it.getLineForVertical(t))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("orientation", orientation)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putString("textLine", firstTextPosition)
    }
}