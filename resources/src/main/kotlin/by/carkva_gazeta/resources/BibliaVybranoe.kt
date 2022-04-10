package by.carkva_gazeta.resources

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.InteractiveScrollView.OnBottomReachedListener
import by.carkva_gazeta.resources.databinding.AkafistChytanneBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

class BibliaVybranoe : AppCompatActivity(), OnTouchListener, DialogFontSizeListener, InteractiveScrollView.OnInteractiveScrollChangedCallback {

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
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private var dzenNoch = false
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var mActionDown = false
    private var change = false
    private lateinit var binding: AkafistChytanneBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var diffScroll = -1
    private var title = ""
    private var firstTextPosition = ""

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.textSize = fontBiblia
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
        binding = AkafistChytanneBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            change = savedInstanceState.getBoolean("change")
        }
        title = intent.extras?.getString("title", "") ?: ""
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.textView.textSize = fontBiblia
        binding.constraint.setOnTouchListener(this)
        binding.InteractiveScroll.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached() {
                autoscroll = false
                stopAutoScroll()
                invalidateOptionsMenu()
            }

            override fun onScrollDiff(diff: Int) {
                diffScroll = diff
            }

            override fun onTouch(action: Boolean) {
                mActionDown = action
            }
        })
        if (dzenNoch) {
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
        }
        spid = k.getInt("autoscrollSpid", 60)
        autoscroll = k.getBoolean("autoscroll", false)
        loadBible(savedInstanceState)
        bindingprogress.fontSizePlus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.max_font)
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent(3000)
        }
        bindingprogress.fontSizeMinus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.min_font)
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent(3000)
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
            startProcent(3000)
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
            startProcent(3000)
        }
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
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
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionFullscreen.setOnClickListener {
            fullscreenPage = false
            show()
        }
        binding.InteractiveScroll.setOnScrollChangedCallback(this)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.str_short_label1)
        binding.subtitleToolbar.visibility = View.VISIBLE
        when (intent.extras?.getInt("biblia", 1) ?: 1) {
            1 -> binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia)
            2 -> binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)
            3 -> binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_psalter)
        }
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
            }
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
        binding.subtitleToolbar.isSingleLine = true
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val otstup2 = if (autoscroll) (50 * resources.displayMetrics.density).toInt()
        else 0
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
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (y > heightConstraintLayout - otstup) {
                        spid = k.getInt("autoscrollSpid", 60)
                        proc = 100 - (spid - 15) * 100 / 215
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
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

    private fun loadBible(savedInstanceState: Bundle?) {
        val ssbTitle = SpannableStringBuilder()
        DialogVybranoeBibleList.arrayListVybranoe.forEach { VybranoeBibliaData ->
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
                inputStream = resources.openRawResource(R.raw.psaltyr_nadsan)
            }
            val builder = StringBuilder()
            InputStreamReader(inputStream).use { inputStreamReader ->
                val reader = BufferedReader(inputStreamReader)
                var line: String
                reader.forEachLine {
                    line = it
                    line = line.replace("\\n", "<br>")
                    if (line.contains("//")) {
                        val t1 = line.indexOf("//")
                        line = line.substring(0, t1).trim()
                        if (line != "") builder.append(line).append("<br>")
                    } else {
                        if (line != "") builder.append(line).append("<br>")
                    }
                }
            }
            val split2 = builder.toString().split("===")
            val titleBibliaData = VybranoeBibliaData.title
            ssbTitle.append(titleBibliaData)
            ssbTitle.setSpan(StyleSpan(Typeface.BOLD), ssbTitle.length - titleBibliaData.length, ssbTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssbTitle.append("\n")
            ssbTitle.append(MainActivity.fromHtml(split2[VybranoeBibliaData.glava])).append("\n")
        }
        binding.textView.text = ssbTitle.trim()
        if (savedInstanceState != null) {
            binding.textView.post {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    val index = binding.textView.text.indexOf(textline)
                    val line = binding.textView.layout.getLineForOffset(index)
                    val y = binding.textView.layout.getLineTop(line)
                    binding.InteractiveScroll.scrollY = y
                }
            }
        } else {
            binding.InteractiveScroll.post {
                val strPosition = binding.textView.text.indexOf(title + "\n", ignoreCase = true)
                val line = binding.textView.layout.getLineForOffset(strPosition)
                val y = binding.textView.layout.getLineTop(line)
                val anim = ObjectAnimator.ofInt(binding.InteractiveScroll, "scrollY", binding.InteractiveScroll.scrollY, y)
                anim.setDuration(1000).start()
                if (k.getBoolean("autoscrollAutostart", false)) {
                    autoStartScroll()
                }
            }
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
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

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            binding.textView.clearFocus()
            binding.textView.setTextIsSelectable(false)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val autoTime = (230 - spid) / 10
            if (autoStartScrollJob?.isActive != true) {
                autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000L)
                    spid = 230
                    autoScroll()
                    for (i in 0..9) {
                        delay(1500L)
                        spid -= autoTime
                    }
                    startAutoScroll()
                }
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
            bindingprogress.brighess.visibility = View.GONE
            bindingprogress.fontSize.visibility = View.GONE
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
            stopAutoStartScroll()
            binding.textView.setTextIsSelectable(true)
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (diffScroll != 0) {
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.VISIBLE
            binding.actionPlus.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
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
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
        }
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
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        stopAutoStartScroll()
        procentJob?.cancel()
        resetTollbarJob?.cancel()
        resetScreenJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            autoStartScroll()
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
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
            fullscreenPage = true
            hide()
        }
        prefEditor.apply()
        return super.onOptionsItemSelected(item)
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        CoroutineScope(Dispatchers.Main).launch {
            mHidePart2Runnable()
        }
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
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
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
    }

    override fun onScroll(t: Int, oldt: Int) {
        val lineLayout = binding.textView.layout
        lineLayout?.let {
            val textForVertical = binding.textView.text.substring(binding.textView.layout.getLineStart(it.getLineForVertical(t)), binding.textView.layout.getLineEnd(it.getLineForVertical(t))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("change", change)
        outState.putString("textLine", firstTextPosition)
    }
}