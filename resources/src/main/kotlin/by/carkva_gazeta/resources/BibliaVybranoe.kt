package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 25.5.16
 */
class BibliaVybranoe : AppCompatActivity(), OnTouchListener, DialogFontSizeListener {
    private val mHideHandler = Handler(Looper.getMainLooper())

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private val mHidePart2Runnable = Runnable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
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
    private var scrollTimer = Timer()
    private var procentTimer = Timer()
    private var resetTimer = Timer()
    private var autoscrollTimer = Timer()
    private var scrollerSchedule: TimerTask? = null
    private var procentSchedule: TimerTask? = null
    private var resetSchedule: TimerTask? = null
    private var autoscrollSchedule: TimerTask? = null
    private var levo = false
    private var pravo = false
    private var niz = false
    private var mActionDown = false
    private var change = false
    private var cytannelist = ArrayList<TextViewRobotoCondensed>()
    private var toTwoList = 0
    private val uiAnimationDelay: Long = 300
    private val orientation: Int
        get() {
            return MainActivity.getOrientation(this)
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
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.akafist_chytanne)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            change = savedInstanceState.getBoolean("change")
        }
        toTwoList = intent.extras?.getInt("position", 0) ?: 0
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
        spid = k.getInt("autoscrollSpid", 60)
        autoscroll = k.getBoolean("autoscroll", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (dzenNoch) {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
            } else {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
            }
        }
        loadBible()
        if (k.getBoolean("help_str", true)) {
            startActivity(Intent(this, HelpText::class.java))
            val prefEditor: Editor = k.edit()
            prefEditor.putBoolean("help_str", false)
            prefEditor.apply()
        }
        if (k.getBoolean("autoscrollAutostart", false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            autoStartScroll()
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.str_short_label1)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

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
                        progress.text = getString(by.carkva_gazeta.malitounik.R.string.font_sp, fontBiblia.toInt(), minmax)
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
                            progress.text = getString(by.carkva_gazeta.malitounik.R.string.font_sp, fontBiblia.toInt(), min)
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
                            progress.text = getString(by.carkva_gazeta.malitounik.R.string.font_sp, fontBiblia.toInt(), max)
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

    private fun loadBible() {
        MyBibleList.arrayListVybranoe.forEach { VybranoeBibliaData ->
            var inputStream = resources.openRawResource(R.raw.biblias1)
            if (VybranoeBibliaData.bibleName == 1) {
                if (VybranoeBibliaData.novyZavet) {
                    when (VybranoeBibliaData.kniga) {
                        0 -> inputStream = resources.openRawResource(R.raw.biblian1)
                        1 -> inputStream = resources.openRawResource(R.raw.biblian2)
                        2 -> inputStream = resources.openRawResource(R.raw.biblian3)
                        3 -> inputStream = resources.openRawResource(R.raw.biblian4)
                        4 -> inputStream = resources.openRawResource(R.raw.biblian5)
                        5 -> inputStream = resources.openRawResource(R.raw.biblian6)
                        6 -> inputStream = resources.openRawResource(R.raw.biblian7)
                        7 -> inputStream = resources.openRawResource(R.raw.biblian8)
                        8 -> inputStream = resources.openRawResource(R.raw.biblian9)
                        9 -> inputStream = resources.openRawResource(R.raw.biblian10)
                        10 -> inputStream = resources.openRawResource(R.raw.biblian11)
                        11 -> inputStream = resources.openRawResource(R.raw.biblian12)
                        12 -> inputStream = resources.openRawResource(R.raw.biblian13)
                        13 -> inputStream = resources.openRawResource(R.raw.biblian14)
                        14 -> inputStream = resources.openRawResource(R.raw.biblian15)
                        15 -> inputStream = resources.openRawResource(R.raw.biblian16)
                        16 -> inputStream = resources.openRawResource(R.raw.biblian17)
                        17 -> inputStream = resources.openRawResource(R.raw.biblian18)
                        18 -> inputStream = resources.openRawResource(R.raw.biblian19)
                        19 -> inputStream = resources.openRawResource(R.raw.biblian20)
                        20 -> inputStream = resources.openRawResource(R.raw.biblian21)
                        21 -> inputStream = resources.openRawResource(R.raw.biblian22)
                        22 -> inputStream = resources.openRawResource(R.raw.biblian23)
                        23 -> inputStream = resources.openRawResource(R.raw.biblian24)
                        24 -> inputStream = resources.openRawResource(R.raw.biblian25)
                        25 -> inputStream = resources.openRawResource(R.raw.biblian26)
                        26 -> inputStream = resources.openRawResource(R.raw.biblian27)
                    }
                } else {
                    when (VybranoeBibliaData.kniga) {
                        0 -> inputStream = resources.openRawResource(R.raw.biblias1)
                        1 -> inputStream = resources.openRawResource(R.raw.biblias2)
                        2 -> inputStream = resources.openRawResource(R.raw.biblias3)
                        3 -> inputStream = resources.openRawResource(R.raw.biblias4)
                        4 -> inputStream = resources.openRawResource(R.raw.biblias5)
                        5 -> inputStream = resources.openRawResource(R.raw.biblias6)
                        6 -> inputStream = resources.openRawResource(R.raw.biblias7)
                        7 -> inputStream = resources.openRawResource(R.raw.biblias8)
                        8 -> inputStream = resources.openRawResource(R.raw.biblias9)
                        9 -> inputStream = resources.openRawResource(R.raw.biblias10)
                        10 -> inputStream = resources.openRawResource(R.raw.biblias11)
                        11 -> inputStream = resources.openRawResource(R.raw.biblias12)
                        12 -> inputStream = resources.openRawResource(R.raw.biblias13)
                        13 -> inputStream = resources.openRawResource(R.raw.biblias14)
                        14 -> inputStream = resources.openRawResource(R.raw.biblias15)
                        15 -> inputStream = resources.openRawResource(R.raw.biblias16)
                        16 -> inputStream = resources.openRawResource(R.raw.biblias17)
                        17 -> inputStream = resources.openRawResource(R.raw.biblias18)
                        18 -> inputStream = resources.openRawResource(R.raw.biblias19)
                        19 -> inputStream = resources.openRawResource(R.raw.biblias20)
                        20 -> inputStream = resources.openRawResource(R.raw.biblias21)
                        21 -> inputStream = resources.openRawResource(R.raw.biblias22)
                        22 -> inputStream = resources.openRawResource(R.raw.biblias23)
                        23 -> inputStream = resources.openRawResource(R.raw.biblias24)
                        24 -> inputStream = resources.openRawResource(R.raw.biblias25)
                        25 -> inputStream = resources.openRawResource(R.raw.biblias26)
                        26 -> inputStream = resources.openRawResource(R.raw.biblias27)
                        27 -> inputStream = resources.openRawResource(R.raw.biblias28)
                        28 -> inputStream = resources.openRawResource(R.raw.biblias29)
                        29 -> inputStream = resources.openRawResource(R.raw.biblias30)
                        30 -> inputStream = resources.openRawResource(R.raw.biblias31)
                        31 -> inputStream = resources.openRawResource(R.raw.biblias32)
                        32 -> inputStream = resources.openRawResource(R.raw.biblias33)
                        33 -> inputStream = resources.openRawResource(R.raw.biblias34)
                        34 -> inputStream = resources.openRawResource(R.raw.biblias35)
                        35 -> inputStream = resources.openRawResource(R.raw.biblias36)
                        36 -> inputStream = resources.openRawResource(R.raw.biblias37)
                        37 -> inputStream = resources.openRawResource(R.raw.biblias38)
                        38 -> inputStream = resources.openRawResource(R.raw.biblias39)
                    }
                }
            } else if (VybranoeBibliaData.bibleName == 2) {
                if (VybranoeBibliaData.novyZavet) {
                    when (VybranoeBibliaData.kniga) {
                        0 -> inputStream = resources.openRawResource(R.raw.sinaidaln1)
                        1 -> inputStream = resources.openRawResource(R.raw.sinaidaln2)
                        2 -> inputStream = resources.openRawResource(R.raw.sinaidaln3)
                        3 -> inputStream = resources.openRawResource(R.raw.sinaidaln4)
                        4 -> inputStream = resources.openRawResource(R.raw.sinaidaln5)
                        5 -> inputStream = resources.openRawResource(R.raw.sinaidaln6)
                        6 -> inputStream = resources.openRawResource(R.raw.sinaidaln7)
                        7 -> inputStream = resources.openRawResource(R.raw.sinaidaln8)
                        8 -> inputStream = resources.openRawResource(R.raw.sinaidaln9)
                        9 -> inputStream = resources.openRawResource(R.raw.sinaidaln10)
                        10 -> inputStream = resources.openRawResource(R.raw.sinaidaln11)
                        11 -> inputStream = resources.openRawResource(R.raw.sinaidaln12)
                        12 -> inputStream = resources.openRawResource(R.raw.sinaidaln13)
                        13 -> inputStream = resources.openRawResource(R.raw.sinaidaln14)
                        14 -> inputStream = resources.openRawResource(R.raw.sinaidaln15)
                        15 -> inputStream = resources.openRawResource(R.raw.sinaidaln16)
                        16 -> inputStream = resources.openRawResource(R.raw.sinaidaln17)
                        17 -> inputStream = resources.openRawResource(R.raw.sinaidaln18)
                        18 -> inputStream = resources.openRawResource(R.raw.sinaidaln19)
                        19 -> inputStream = resources.openRawResource(R.raw.sinaidaln20)
                        20 -> inputStream = resources.openRawResource(R.raw.sinaidaln21)
                        21 -> inputStream = resources.openRawResource(R.raw.sinaidaln22)
                        22 -> inputStream = resources.openRawResource(R.raw.sinaidaln23)
                        23 -> inputStream = resources.openRawResource(R.raw.sinaidaln24)
                        24 -> inputStream = resources.openRawResource(R.raw.sinaidaln25)
                        25 -> inputStream = resources.openRawResource(R.raw.sinaidaln26)
                        26 -> inputStream = resources.openRawResource(R.raw.sinaidaln27)
                    }
                } else {
                    when (VybranoeBibliaData.kniga) {
                        0 -> inputStream = resources.openRawResource(R.raw.sinaidals1)
                        1 -> inputStream = resources.openRawResource(R.raw.sinaidals2)
                        2 -> inputStream = resources.openRawResource(R.raw.sinaidals3)
                        3 -> inputStream = resources.openRawResource(R.raw.sinaidals4)
                        4 -> inputStream = resources.openRawResource(R.raw.sinaidals5)
                        5 -> inputStream = resources.openRawResource(R.raw.sinaidals6)
                        6 -> inputStream = resources.openRawResource(R.raw.sinaidals7)
                        7 -> inputStream = resources.openRawResource(R.raw.sinaidals8)
                        8 -> inputStream = resources.openRawResource(R.raw.sinaidals9)
                        9 -> inputStream = resources.openRawResource(R.raw.sinaidals10)
                        10 -> inputStream = resources.openRawResource(R.raw.sinaidals11)
                        11 -> inputStream = resources.openRawResource(R.raw.sinaidals12)
                        12 -> inputStream = resources.openRawResource(R.raw.sinaidals13)
                        13 -> inputStream = resources.openRawResource(R.raw.sinaidals14)
                        14 -> inputStream = resources.openRawResource(R.raw.sinaidals15)
                        15 -> inputStream = resources.openRawResource(R.raw.sinaidals16)
                        16 -> inputStream = resources.openRawResource(R.raw.sinaidals17)
                        17 -> inputStream = resources.openRawResource(R.raw.sinaidals18)
                        18 -> inputStream = resources.openRawResource(R.raw.sinaidals19)
                        19 -> inputStream = resources.openRawResource(R.raw.sinaidals20)
                        20 -> inputStream = resources.openRawResource(R.raw.sinaidals21)
                        21 -> inputStream = resources.openRawResource(R.raw.sinaidals22)
                        22 -> inputStream = resources.openRawResource(R.raw.sinaidals23)
                        23 -> inputStream = resources.openRawResource(R.raw.sinaidals24)
                        24 -> inputStream = resources.openRawResource(R.raw.sinaidals25)
                        25 -> inputStream = resources.openRawResource(R.raw.sinaidals26)
                        26 -> inputStream = resources.openRawResource(R.raw.sinaidals27)
                        27 -> inputStream = resources.openRawResource(R.raw.sinaidals28)
                        28 -> inputStream = resources.openRawResource(R.raw.sinaidals29)
                        29 -> inputStream = resources.openRawResource(R.raw.sinaidals30)
                        30 -> inputStream = resources.openRawResource(R.raw.sinaidals31)
                        31 -> inputStream = resources.openRawResource(R.raw.sinaidals32)
                        32 -> inputStream = resources.openRawResource(R.raw.sinaidals33)
                        33 -> inputStream = resources.openRawResource(R.raw.sinaidals34)
                        34 -> inputStream = resources.openRawResource(R.raw.sinaidals35)
                        35 -> inputStream = resources.openRawResource(R.raw.sinaidals36)
                        36 -> inputStream = resources.openRawResource(R.raw.sinaidals37)
                        37 -> inputStream = resources.openRawResource(R.raw.sinaidals38)
                        38 -> inputStream = resources.openRawResource(R.raw.sinaidals39)
                        39 -> inputStream = resources.openRawResource(R.raw.sinaidals40)
                        40 -> inputStream = resources.openRawResource(R.raw.sinaidals41)
                        41 -> inputStream = resources.openRawResource(R.raw.sinaidals42)
                        42 -> inputStream = resources.openRawResource(R.raw.sinaidals43)
                        43 -> inputStream = resources.openRawResource(R.raw.sinaidals44)
                        44 -> inputStream = resources.openRawResource(R.raw.sinaidals45)
                        45 -> inputStream = resources.openRawResource(R.raw.sinaidals46)
                        46 -> inputStream = resources.openRawResource(R.raw.sinaidals47)
                        47 -> inputStream = resources.openRawResource(R.raw.sinaidals48)
                        48 -> inputStream = resources.openRawResource(R.raw.sinaidals49)
                        49 -> inputStream = resources.openRawResource(R.raw.sinaidals50)
                    }
                }
            } else if (VybranoeBibliaData.bibleName == 3) {
                inputStream = resources.openRawResource(R.raw.nadsan_psaltyr)
            }
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            val builder = StringBuilder()
            reader.forEachLine {
                line = it
                if (line.contains("//")) {
                    val t1 = line.indexOf("//")
                    line = line.substring(0, t1).trim()
                    if (line != "") builder.append(line).append("<br>")
                } else {
                    if (line != "") builder.append(line).append("<br>")
                }
            }
            inputStream.close()
            val split2 = builder.toString().split("===")
            val ssbTitle = SpannableStringBuilder()
            ssbTitle.append(VybranoeBibliaData.title)
            ssbTitle.setSpan(StyleSpan(Typeface.BOLD), 0, ssbTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val textView1 = TextViewRobotoCondensed(this)
            textView1.isFocusable = false
            textView1.text = ssbTitle
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            if (dzenNoch) textView1.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else textView1.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            LinearButtom.addView(textView1)
            cytannelist.add(textView1)
            val textView2 = TextViewRobotoCondensed(this)
            textView2.isFocusable = false
            textView2.text = MainActivity.fromHtml(split2[VybranoeBibliaData.glava])
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            if (dzenNoch) textView2.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons)) else textView2.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            cytannelist.add(textView2)
            LinearButtom.addView(textView2)
        }
        InteractiveScroll.postDelayed({
            val y = LinearButtom.y + LinearButtom.getChildAt(toTwoList).y
            InteractiveScroll.smoothScrollTo(0, y.toInt())
        }, 700)
    }

    private fun autoStartScroll() {
        stopAutoStartScroll()
        autoscrollTimer = Timer()
        autoscrollSchedule = object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
                    stopAutoScroll()
                    startAutoScroll()
                    val prefEditor: Editor = k.edit()
                    prefEditor.putBoolean("autoscroll", true)
                    prefEditor.apply()
                    invalidateOptionsMenu()
                }
            }
        }
        var autoTime: Long = 10000
        for (i in 0..15) {
            if (i == k.getInt("autoscrollAutostartTime", 5)) {
                autoTime = (i + 5) * 1000L
                break
            }
        }
        autoscrollTimer.schedule(autoscrollSchedule, autoTime)
    }

    private fun stopAutoStartScroll() {
        autoscrollTimer.cancel()
        autoscrollSchedule = null
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
                CoroutineScope(Dispatchers.Main).launch {
                    progress.visibility = View.GONE
                }
            }
        }
        procentTimer.schedule(procentSchedule, 1000)
    }

    private fun stopAutoScroll() {
        cytannelist.forEach {
            it.setTextIsSelectable(true)
        }
        scrollTimer.cancel()
        scrollerSchedule = null
        if (!k.getBoolean("scrinOn", false)) {
            resetTimer = Timer()
            resetSchedule = object : TimerTask() {
                override fun run() {
                    CoroutineScope(Dispatchers.Main).launch { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
                }
            }
            resetTimer.schedule(resetSchedule, 60000)
        }
    }

    private fun startAutoScroll() {
        cytannelist.forEach {
            it.setTextIsSelectable(false)
        }
        resetTimer.cancel()
        scrollTimer = Timer()
        resetSchedule = null
        scrollerSchedule = object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
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
        mHideHandler.postDelayed(mHidePart2Runnable, uiAnimationDelay)
    }

    @Suppress("DEPRECATION")
    private fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            val controller = window.insetsController
            controller?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            //controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, uiAnimationDelay)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("change", change)
    }
}