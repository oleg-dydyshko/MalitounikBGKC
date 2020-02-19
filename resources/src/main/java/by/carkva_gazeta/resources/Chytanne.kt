package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.InteractiveScrollView.OnBottomReachedListener
import by.carkva_gazeta.malitounik.InteractiveScrollView.OnNestedTouchListener
import kotlinx.android.synthetic.main.akafist_chytanne.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by oleg on 25.5.16
 */
class Chytanne : AppCompatActivity(), OnTouchListener, DialogFontSizeListener {
    private val mHideHandler = Handler()
    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        LinearButtom.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private val mShowPart2Runnable = Runnable {
        val actionBar = supportActionBar
        actionBar?.show()
    }
    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var autoscroll = false
    private var n = 0
    private var yS = 0
    private var spid = 60
    private var scrollTimer: Timer = Timer()
    private var procentTimer: Timer = Timer()
    private var resetTimer: Timer = Timer()
    private var scrollerSchedule: TimerTask? = null
    private var procentSchedule: TimerTask? = null
    private var resetSchedule: TimerTask? = null
    private var levo = false
    private var pravo = false
    private var niz = false
    private var mActionDown = false
    private var change = false
    private var cytannelist: ArrayList<TextViewRobotoCondensed> = ArrayList()
    private var nedelia = -1
    private var toTwoList = 0
    private val orientation: Int
        get() {
            val rotation = windowManager.defaultDisplay.rotation
            val displayOrientation = resources.configuration.orientation
            if (displayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                return if (rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_180) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_90) return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    override fun onDialogFontSizePositiveClick() {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        cytannelist.forEach {
            it.textSize = fontBiblia
        }
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
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.akafist_chytanne)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            change = savedInstanceState.getBoolean("change")
        }
        nedelia = intent.extras?.getInt("nedelia", -1) ?: -1
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        constraint.setOnTouchListener(this)
        InteractiveScroll.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached() {
                autoscroll = false
                stopAutoScroll()
                val prefEditors = k.edit()
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.apply()
                invalidateOptionsMenu()
            }
        })
        InteractiveScroll.setOnNestedTouchListener(object : OnNestedTouchListener {
            override fun onTouch(action: Boolean) {
                mActionDown = action
            }
        })
        if (dzenNoch) progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        autoscroll = k.getBoolean("autoscroll", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (dzenNoch) {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
            } else {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
            }
        }
        setChtenia(savedInstanceState)
        if (k.getBoolean("help_str", true)) {
            startActivity(Intent(this, HelpText::class.java))
            val prefEditor: Editor = k.edit()
            prefEditor.putBoolean("help_str", false)
            prefEditor.apply()
        }
        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.CZYTANNE)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val heightConstraintLayout = constraint.height
        val widthConstraintLayout = constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val prefEditor: Editor = k.edit()
        if (v?.id ?: 0 == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                MainActivity.brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    yS = event?.x?.toInt() ?: 0
                    val proc: Int
                    if (x < otstup) {
                        levo = true
                        progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                        progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        progress.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        pravo = true
                        var minmax = ""
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) minmax = " (мін)"
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) minmax = " (макс)"
                        progress.text = "${fontBiblia.toInt()} sp$minmax"
                        progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                        progress.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (y > heightConstraintLayout - otstup) {
                        niz = true
                        spid = k.getInt("autoscrollSpid", 60)
                        proc = 100 - (spid - 15) * 100 / 215
                        progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                        progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                        progress.visibility = View.VISIBLE
                        startProcent()
                        autoscroll = k.getBoolean("autoscroll", false)
                        if (!autoscroll) {
                            startAutoScroll()
                            prefEditor.putBoolean("autoscroll", true)
                            prefEditor.apply()
                            invalidateOptionsMenu()
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (x < otstup && y > n && y % 15 == 0) {
                        if (MainActivity.brightness > 0) {
                            MainActivity.brightness = MainActivity.brightness - 1
                            val lp = window.attributes
                            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                            window.attributes = lp
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                            MainActivity.checkBrightness = false
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (x < otstup && y < n && y % 15 == 0) {
                        if (MainActivity.brightness < 100) {
                            MainActivity.brightness = MainActivity.brightness + 1
                            val lp = window.attributes
                            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                            window.attributes = lp
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                            MainActivity.checkBrightness = false
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (x > widthConstraintLayout - otstup && y > n && y % 26 == 0) {
                        if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                            fontBiblia -= 4
                            prefEditor.putFloat("font_biblia", fontBiblia)
                            prefEditor.apply()
                            for (text in cytannelist) {
                                text.textSize = fontBiblia
                            }
                            var min = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) min = " (мін)"
                            progress.text = "${fontBiblia.toInt()} sp$min"
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (x > widthConstraintLayout - otstup && y < n && y % 26 == 0) {
                        if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                            fontBiblia += 4
                            prefEditor.putFloat("font_biblia", fontBiblia)
                            prefEditor.apply()
                            for (text in cytannelist) {
                                text.textSize = fontBiblia
                            }
                            var max = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) max = " (макс)"
                            progress.text = "${fontBiblia.toInt()} sp$max"
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (y > heightConstraintLayout - otstup && x > yS && x % 25 == 0) {
                        if (spid in 20..235) {
                            spid -= 5
                            val proc = 100 - (spid - 15) * 100 / 215
                            progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                            progress.visibility = View.VISIBLE
                            startProcent()
                            stopAutoScroll()
                            startAutoScroll()
                        }
                    }
                    if (y > heightConstraintLayout - otstup && x < yS && x % 25 == 0) {
                        if (spid in 10..225) {
                            spid += 5
                            val proc = 100 - (spid - 15) * 100 / 215
                            progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                            progress.visibility = View.VISIBLE
                            startProcent()
                            stopAutoScroll()
                            startAutoScroll()
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    v?.performClick()
                    if (levo) {
                        levo = false
                    }
                    if (pravo) {
                        pravo = false
                    }
                    if (niz) {
                        niz = false
                        prefEditor.putInt("autoscrollSpid", spid)
                        prefEditor.apply()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (levo) {
                        levo = false
                    }
                    if (pravo) {
                        pravo = false
                    }
                    if (niz) {
                        niz = false
                        prefEditor.putInt("autoscrollSpid", spid)
                        prefEditor.apply()
                    }
                }
            }
        }
        return true
    }

    private fun setChtenia(savedInstanceState: Bundle?) {
        try {
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
            var w = intent.extras?.getString("cytanne") ?: ""
            w = MainActivity.removeZnakiAndSlovy(w)
            val split = w.split(";").toTypedArray()
            //String[] split = {"Гал 1.1-10, 20-2.5"};
            var knigaN: String
            var knigaK = "0"
            var zaglnum = 0
            // Мц 1.1-10, 20-2.5, 10-20, 1.21-2.4, 11;
            for (i in split.indices) {
                val zaglavie = split[i].split(",").toTypedArray()
                var zagl = ""
                var zaglavieName = ""
                for (e in zaglavie.indices) {
                    try {
                        val zaglav = zaglavie[e].trim()
                        val zag = zaglav.indexOf(" ", 2)
                        val zag1 = zaglav.indexOf(".")
                        val zag2 = zaglav.indexOf("-")
                        val zag3 = zaglav.indexOf(".", zag1 + 1)
                        var zagS: String
                        zagS = if (zag2 != -1) {
                            zaglav.substring(0, zag2)
                        } else {
                            zaglav
                        }
                        var glav = false
                        if (zag1 > zag2 && zag == -1) {
                            glav = true
                        } else if (zag != -1) {
                            zagl = zaglav.substring(0, zag) // Название книги
                            val zaglavieName1 = split[i].trim()
                            zaglavieName = " " + zaglavieName1.substring(zag + 1)
                            zaglnum = zaglav.substring(zag + 1, zag1).toInt() // Номер главы
                        } else if (zag1 != -1) {
                            zaglnum = zaglav.substring(0, zag1).toInt() // Номер главы
                        }
                        if (glav) {
                            val zagS1 = zagS.indexOf(".")
                            if (zagS1 == -1) {
                                knigaN = zagS // Начало чтения
                            } else {
                                zaglnum = zagS.substring(0, zagS1).toInt() // Номер главы
                                knigaN = zagS.substring(zagS1 + 1) // Начало чтения
                            }
                        } else if (zag2 == -1) { // Конец чтения
                            if (zag != -1) {
                                val zagS1 = zagS.indexOf(".")
                                zaglnum = zagS.substring(zag + 1, zagS1).toInt() // Номер главы
                                knigaN = zagS.substring(zagS1 + 1) // Начало чтения
                            } else {
                                knigaN = zaglav // Начало чтения
                            }
                            knigaK = knigaN // Конец чтения
                        } else {
                            knigaN = zaglav.substring(zag1 + 1, zag2) // Начало чтения
                        }
                        if (glav) {
                            knigaK = zaglav.substring(zag1 + 1) // Конец чтения
                        } else if (zag2 != -1) {
                            knigaK = if (zag3 == -1) {
                                zaglav.substring(zag2 + 1) // Конец чтения
                            } else {
                                zaglav.substring(zag3 + 1) // Конец чтения
                            }
                        }
                        var polstixaA = false
                        var polstixaB = false
                        if (knigaK.contains("а")) {
                            polstixaA = true
                            knigaK = knigaK.replace("а", "")
                        }
                        if (knigaN.contains("б")) {
                            polstixaB = true
                            knigaN = knigaN.replace("б", "")
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
                        if (zagl == "Ер") kniga = 35
                        if (zagl == "Дан") kniga = 36
                        if (zagl == "Лікі") kniga = 37
                        if (zagl == "Міх") kniga = 38
                        //Быц 1.1-13; Лікі 24.2-3, 5-9, 17-18; Міх 4.6-7, 5.1-4; Іс 11.1-10; Ярэм 3.35-4.4; Дан 2.31-36, 44-45; Іс 9.5-6, 7.10-16, 8.1-4, 9-10
                        val r = resources
                        var inputStream = r.openRawResource(R.raw.biblian1)
                        var ssbTitle = SpannableStringBuilder()
                        var errorChytanne = false
                        when (kniga) {
                            0 -> {
                                inputStream = r.openRawResource(R.raw.biblian1)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_0, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            1 -> {
                                inputStream = r.openRawResource(R.raw.biblian2)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_1, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            2 -> {
                                inputStream = r.openRawResource(R.raw.biblian3)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_2, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            3 -> {
                                inputStream = r.openRawResource(R.raw.biblian4)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_3, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            4 -> {
                                inputStream = r.openRawResource(R.raw.biblian5)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_4, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            5 -> {
                                inputStream = r.openRawResource(R.raw.biblian6)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_5, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            6 -> {
                                inputStream = r.openRawResource(R.raw.biblian7)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_6, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            7 -> {
                                inputStream = r.openRawResource(R.raw.biblian8)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_7, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            8 -> {
                                inputStream = r.openRawResource(R.raw.biblian9)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_8, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            9 -> {
                                inputStream = r.openRawResource(R.raw.biblian10)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_9, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            10 -> {
                                inputStream = r.openRawResource(R.raw.biblian11)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_10, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            11 -> {
                                inputStream = r.openRawResource(R.raw.biblian12)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_11, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            12 -> {
                                inputStream = r.openRawResource(R.raw.biblian13)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_12, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            13 -> {
                                inputStream = r.openRawResource(R.raw.biblian14)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_13, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            14 -> {
                                inputStream = r.openRawResource(R.raw.biblian15)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_14, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            15 -> {
                                inputStream = r.openRawResource(R.raw.biblian16)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_15, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            16 -> {
                                inputStream = r.openRawResource(R.raw.biblian17)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_16, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            17 -> {
                                inputStream = r.openRawResource(R.raw.biblian18)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_17, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            18 -> {
                                inputStream = r.openRawResource(R.raw.biblian19)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_18, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            19 -> {
                                inputStream = r.openRawResource(R.raw.biblian20)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_19, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            20 -> {
                                inputStream = r.openRawResource(R.raw.biblian21)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_20, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            21 -> {
                                inputStream = r.openRawResource(R.raw.biblian22)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_21, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            22 -> {
                                inputStream = r.openRawResource(R.raw.biblian23)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_22, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            23 -> {
                                inputStream = r.openRawResource(R.raw.biblian24)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_23, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            24 -> {
                                inputStream = r.openRawResource(R.raw.biblian25)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_24, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            25 -> {
                                inputStream = r.openRawResource(R.raw.biblian26)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_25, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            26 -> {
                                inputStream = r.openRawResource(R.raw.biblias1)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_26, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            27 -> {
                                inputStream = r.openRawResource(R.raw.biblias20)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_27, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            28 -> {
                                inputStream = r.openRawResource(R.raw.biblias26)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_28, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            29 -> {
                                inputStream = r.openRawResource(R.raw.biblias2)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_29, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            30 -> {
                                inputStream = r.openRawResource(R.raw.biblias18)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_30, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            31 -> {
                                inputStream = r.openRawResource(R.raw.biblias38)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_31, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            32 -> {
                                inputStream = r.openRawResource(R.raw.biblias29)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_32, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            33 -> {
                                inputStream = r.openRawResource(R.raw.biblias36)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_33, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            34 -> {
                                inputStream = r.openRawResource(R.raw.biblias23)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_34, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            35 -> {
                                inputStream = r.openRawResource(R.raw.biblias24)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_35, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            36 -> {
                                inputStream = r.openRawResource(R.raw.biblias27)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_36, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            37 -> {
                                inputStream = r.openRawResource(R.raw.biblias4)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_37, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            38 -> {
                                inputStream = r.openRawResource(R.raw.biblias33)
                                ssbTitle = if (e == 0) {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_38, spln, zaglavieName))
                                } else {
                                    SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                                }
                            }
                            else -> {
                                errorChytanne = true
                            }
                        }
                        if (!errorChytanne) {
                            if (e == 0) ssbTitle.setSpan(StyleSpan(Typeface.BOLD), 0, ssbTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            val textView1 = TextViewRobotoCondensed(this)
                            textView1.isFocusable = false
                            textView1.text = ssbTitle
                            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                            if (dzenNoch) textView1.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else textView1.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
                            textView1.setPadding(0, 10, 0, 0)
                            LinearButtom.addView(textView1)
                            cytannelist.add(textView1)

                            val isr = InputStreamReader(inputStream)
                            val reader = BufferedReader(isr)
                            var line: String
                            val builder = StringBuilder()
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
                            /*while (reader.readLine()?.also { line = it } != null) {
                        if (line.contains("//")) {
                            val t1 = line.indexOf("//")
                            line = line.substring(0, t1).trim()
                            if (line != "") builder.append(line).append("\n")
                            continue
                        }
                        builder.append(line).append("\n")
                    }*/
                            inputStream.close()
                            val split2 = builder.toString().split("===").toTypedArray()
                            var spl: String
                            var desK1: Int
                            var desN: Int
                            spl = split2[zaglnum].trim()
                            desN = spl.indexOf("$knigaN.")
                            if (knigaN == knigaK) {
                                desK1 = desN
                            } else {
                                desK1 = spl.indexOf("$knigaK.")
                                if (desK1 == -1) {
                                    val splAll = spl.split("\n").toTypedArray().size
                                    desK1 = spl.indexOf("$splAll.")
                                }
                                if (zag3 != -1 || glav) {
                                    val spl1 = split2[zaglnum].trim()
                                    val spl2 = split2[zaglnum + 1].trim()
                                    val des1 = spl1.length
                                    desN = spl1.indexOf("$knigaN.")
                                    desK1 = spl2.indexOf("$knigaK.")
                                    var desN1: Int = spl2.indexOf((knigaK.toInt() + 1).toString().plus("."), desK1)
                                    if (desN1 == -1) {
                                        desN1 = spl1.length
                                    }
                                    desK1 = desN1 + des1
                                    spl = spl1 + "\n" + spl2
                                    zaglnum += 1
                                }
                            }
                            var desK = spl.indexOf("\n", desK1)
                            if (desK == -1) {
                                desK = spl.length
                            }
                            val textBiblia = SpannableStringBuilder(spl.substring(desN, desK))
                            if (polstixaA) {
                                val t2 = textBiblia.indexOf("$knigaK.")
                                val t3 = textBiblia.indexOf(".", t2)
                                var t1 = textBiblia.indexOf(":", t2)
                                if (t1 == -1)
                                    t1 = textBiblia.indexOf(";", t3 + 1)
                                if (t1 == -1)
                                    t1 = textBiblia.indexOf(".", t3 + 1)
                                if (t1 != -1)
                                    textBiblia.setSpan(StrikethroughSpan(), t1 + 1, textBiblia.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            if (polstixaB) {
                                val t2 = textBiblia.indexOf("\n")
                                val textPol = textBiblia.substring(0, t2 + 1)
                                val t3 = textPol.indexOf(".")
                                var t1 = textPol.indexOf(":")
                                if (t1 == -1)
                                    t1 = textPol.indexOf(";", t3 + 1)
                                if (t1 == -1)
                                    t1 = textPol.indexOf(".", t3 + 1)
                                if (t1 != -1)
                                    textBiblia.setSpan(StrikethroughSpan(), t3 + 1, t1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            val textView2 = TextViewRobotoCondensed(this)
                            textView2.isFocusable = false
                            textView2.text = textBiblia
                            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                            if (dzenNoch) textView2.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else textView2.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
                            textView2.setPadding(0, 10, 0, 0)
                            cytannelist.add(textView2)
                            LinearButtom.addView(textView2)
                        } else {
                            error()
                        }
                    } catch (t: Throwable) {
                        error()
                    }
                }
                if (i == 0) toTwoList = cytannelist.size
            }
            if (k.getBoolean("utran", true) && (nedelia == 1 || nedelia == 2 || nedelia == 3) && split.size > 2 && savedInstanceState == null) {
                InteractiveScroll.postDelayed({
                    val y = LinearButtom.y + LinearButtom.getChildAt(toTwoList).y
                    InteractiveScroll.smoothScrollTo(0, y.toInt())
                }, 700)
            }
        } catch (t: Throwable) {
            error()
        }
    }

    private fun error() {
        val ssb = SpannableStringBuilder(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch))
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val textView2 = TextViewRobotoCondensed(this)
        textView2.isFocusable = false
        textView2.text = ssb
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (dzenNoch) textView2.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else textView2.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
        textView2.setPadding(0, 10, 0, 0)
        cytannelist.add(textView2)
        LinearButtom.addView(textView2)
    }

    private fun stopProcent() {
        procentTimer.cancel()
        procentSchedule = null
    }

    private fun startProcent() {
        stopProcent()
        procentTimer = Timer()
        procentSchedule = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    progress.visibility = View.GONE
                }
            }
        }
        procentTimer.schedule(procentSchedule, 1000)
    }

    private fun stopAutoScroll() {
        scrollTimer.cancel()
        resetTimer = Timer()
        scrollerSchedule = null
        resetSchedule = object : TimerTask() {
            override fun run() {
                runOnUiThread { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
            }
        }
        resetTimer.schedule(resetSchedule, 60000)
    }

    private fun startAutoScroll() {
        resetTimer.cancel()
        scrollTimer = Timer()
        resetSchedule = null
        scrollerSchedule = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        InteractiveScroll.smoothScrollBy(0, 2)
                    }
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        scrollTimer.schedule(scrollerSchedule, spid.toLong(), spid.toLong())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.chtenia, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = true
        }
        return menu?.let { super.onMenuOpened(featureId, it) } ?: true
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = false
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        autoscroll = k.getBoolean("autoscroll", false)
        if (autoscroll) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = true
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = true
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).title = resources.getString(by.carkva_gazeta.malitounik.R.string.autoScrolloff)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_fullscreen).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).title = resources.getString(by.carkva_gazeta.malitounik.R.string.autoScrollon)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_fullscreen).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        if (nedelia != -1) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_utran).isChecked = k.getBoolean("utran", true)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_utran).isVisible = true
        }
        return true
    }

    override fun onBackPressed() {
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else {
            if (change) {
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        scrollTimer.cancel()
        resetTimer.cancel()
        procentTimer.cancel()
        scrollerSchedule = null
        procentSchedule = null
        resetSchedule = null
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            startAutoScroll()
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor: Editor = k.edit()
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            change = true
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_utran) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("utran", true)
            } else {
                prefEditor.putBoolean("utran", false)
            }
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_plus) {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                progress.visibility = View.VISIBLE
                startProcent()
                stopAutoScroll()
                startAutoScroll()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_minus) {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                progress.visibility = View.VISIBLE
                startProcent()
                stopAutoScroll()
                startAutoScroll()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
                prefEditor.putBoolean("autoscroll", false)
            } else {
                startAutoScroll()
                prefEditor.putBoolean("autoscroll", true)
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
        prefEditor.apply()
        return super.onOptionsItemSelected(item)
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        LinearButtom.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("change", change)
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}