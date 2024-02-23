package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.ListViewCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.databinding.SimpleListItemMaranataBinding
import by.carkva_gazeta.resources.databinding.AkafistBibliaVybranoeBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader

class BibliaVybranoe : BaseActivity(), OnTouchListener, DialogFontSizeListener, LinkMovementMethodCheck.LinkMovementMethodCheckListener, DialogHelpFullScreen.DialogFullScreenHelpListener, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var mActionDown = false
    private var mAutoScroll = true
    private lateinit var binding: AkafistBibliaVybranoeBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var resetTitleJob: Job? = null
    private var diffScroll = -1
    private var scrolltosatrt = false
    private var title = ""
    private var orientation = Configuration.ORIENTATION_UNDEFINED
    private var positionY = 0
    private var resurs = "0"
    private var linkMovementMethodCheck: LinkMovementMethodCheck? = null
    private var prodoljyt = false
    private lateinit var adapter: BibliaVybranoeListAdaprer
    private val bibliaVybranoeList = ArrayList<BibliaVybranoeData>()
    private val mActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        prodoljyt = true
        loadBible()
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = AkafistBibliaVybranoeBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            orientation = savedInstanceState.getInt("orientation")
            binding.subtitleToolbar.text = savedInstanceState.getString("subTollBarText", "") ?: ""
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            if (k.getBoolean("autoscrollAutostart", false)) {
                autoStartScroll()
            }
        }
        binding.ListView.post {
            if (binding.ListView.getChildAt(binding.ListView.childCount - 1).bottom <= binding.ListView.height) {
                stopAutoStartScroll()
                mAutoScroll = false
                invalidateOptionsMenu()
            }
        }
        title = intent.extras?.getString("title", "") ?: ""
        resurs = intent.extras?.getString("biblia", "1") ?: "1"
        prodoljyt = intent?.extras?.getBoolean("prodoljyt", false) ?: false
        positionY = k.getInt(resurs + "BibleVybranoeScroll", 0)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        adapter = BibliaVybranoeListAdaprer(this)
        binding.ListView.adapter = adapter
        binding.ListView.divider = null
        binding.ListView.setSelection(positionY)
        binding.constraint.setOnTouchListener(this)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            bindingprogress.progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta))
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.seekBarBrighess.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
        }
        spid = k.getInt("autoscrollSpid", 60)
        autoscroll = k.getBoolean("autoscroll", false)
        loadBible()
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize(fontBiblia)
                }
                startProcent()
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
                    bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                    MainActivity.checkBrightness = false
                }
                startProcent()
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
                bindingprogress.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
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
                bindingprogress.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent()
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
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            private var checkDiff = false

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                mActionDown = scrollState != 0
            }

            override fun onScroll(list: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (list.adapter == null || list.getChildAt(0) == null) return
                val position = list.firstVisiblePosition
                val nazva = bibliaVybranoeList[list.firstVisiblePosition]
                if (fullscreenPage) {
                    if (position < positionY) {
                        if (binding.textViewTitle.visibility == View.GONE) {
                            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                            binding.textViewTitle.visibility = View.VISIBLE
                            binding.textViewTitle.animation = animation
                        }
                        if (resetTitleJob?.isActive == true) resetTitleJob?.cancel()
                        if (resetTitleJob?.isActive != true) {
                            resetTitleJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(3000L)
                                val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
                                binding.textViewTitle.visibility = View.GONE
                                binding.textViewTitle.animation = animation2
                            }
                        }
                    }
                    binding.textViewTitle.text = nazva.title
                }
                positionY = position
                if (position == 0 && scrolltosatrt) {
                    autoStartScroll()
                    scrolltosatrt = false
                    invalidateOptionsMenu()
                }
                diffScroll = if (list.lastVisiblePosition == list.adapter.count - 1) list.getChildAt(list.childCount - 1).bottom - list.height
                else -1
                if (checkDiff && diffScroll > 0) {
                    checkDiff = false
                    invalidateOptionsMenu()
                }
                if (list.lastVisiblePosition == list.adapter.count - 1 && list.getChildAt(list.childCount - 1).bottom <= list.height) {
                    checkDiff = true
                    autoscroll = false
                    stopAutoScroll()
                    invalidateOptionsMenu()
                }
                val nazvaView = binding.subtitleToolbar.text.toString()
                if (nazva.title != nazvaView || nazvaView == "") {
                    binding.subtitleToolbar.text = nazva.title
                }
            }
        })
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when (resurs) {
            "1" -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia)
            "2" -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)
            "3" -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_psalter)
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
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
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
                        bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        if (bindingprogress.seekBarBrighess.visibility == View.GONE) {
                            bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_right)
                            bindingprogress.seekBarBrighess.visibility = View.VISIBLE
                        }
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
                        bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
                            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
                            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
                        }
                        startProcent()
                    }
                    if (y > heightConstraintLayout - otstup && x < widthConstraintLayout - otstup3) {
                        if (mAutoScroll) {
                            spid = k.getInt("autoscrollSpid", 60)
                            proc = 100 - (spid - 15) * 100 / 215
                            bindingprogress.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                            startProcent()
                            startAutoScroll()
                            invalidateOptionsMenu()
                        }
                    }
                }
            }
        }
        return true
    }

    private fun loadBible() {
        bibliaVybranoeList.clear()
        var scrollToPosition = 0
        var count = 0
        DialogVybranoeBibleList.arrayListVybranoe.forEachIndexed { index, vybranoeBibliaData ->
            var inputStream = resources.openRawResource(R.raw.biblias1)
            var file: File? = null
            var intent: Intent? = null
            if (vybranoeBibliaData.bibleName == 1) {
                if (vybranoeBibliaData.novyZavet) {
                    file = File("$filesDir/BibliaSemuxaNovyZavet/${vybranoeBibliaData.kniga}.json")
                    intent = Intent(this, NovyZapavietSemuxa::class.java)
                    when (vybranoeBibliaData.kniga) {
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
                    file = File("$filesDir/BibliaSemuxaStaryZavet/${vybranoeBibliaData.kniga}.json")
                    intent = Intent(this, StaryZapavietSemuxa::class.java)
                    when (vybranoeBibliaData.kniga) {
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
            } else if (vybranoeBibliaData.bibleName == 2) {
                if (vybranoeBibliaData.novyZavet) {
                    file = File("$filesDir/BibliaSinodalNovyZavet/${vybranoeBibliaData.kniga}.json")
                    intent = Intent(this, NovyZapavietSinaidal::class.java)
                    when (vybranoeBibliaData.kniga) {
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
                    file = File("$filesDir/BibliaSinodalStaryZavet/${vybranoeBibliaData.kniga}.json")
                    intent = Intent(this, StaryZapavietSinaidal::class.java)
                    when (vybranoeBibliaData.kniga) {
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
            } else if (vybranoeBibliaData.bibleName == 3) {
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
                        if (line != "") builder.append(line).append("<br><br>")
                    } else {
                        if (line != "") builder.append(line).append("<br><br>")
                    }
                }
            }
            val split2 = builder.toString().split("===")
            val titleBibliaData = SpannableString(vybranoeBibliaData.title)
            if (intent != null) {
                titleBibliaData.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        intent.putExtra("kniga", vybranoeBibliaData.kniga)
                        intent.putExtra("glava", vybranoeBibliaData.glava - 1)
                        intent.putExtra("stix", 0)
                        mActivityResult.launch(intent)
                    }
                }, 0, titleBibliaData.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            titleBibliaData.setSpan(StyleSpan(Typeface.BOLD), 0, titleBibliaData.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            bibliaVybranoeList.add(BibliaVybranoeData(vybranoeBibliaData.title, titleBibliaData))
            if (title == vybranoeBibliaData.title) scrollToPosition = count
            count++
            if (file?.exists() == true) {
                BibleGlobalList.vydelenie.clear()
                val inputStream2 = FileReader(file)
                val reader = BufferedReader(inputStream2)
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
                BibleGlobalList.vydelenie.addAll(gson.fromJson(reader.readText(), type))
                inputStream2.close()
                val bibleline = split2[vybranoeBibliaData.glava].split("<br><br>")
                var position = 0
                for (i in bibleline.indices) {
                    val ssb = SpannableString(MainActivity.fromHtml(bibleline[i]))
                    if (!(bibleline[i] == "" && bibleline.size - 1 == position && DialogVybranoeBibleList.arrayListVybranoe.size - 1 == index)) {
                        val pos = BibleGlobalList.checkPosition(vybranoeBibliaData.glava - 1, position)
                        if (pos != -1) {
                            if (BibleGlobalList.vydelenie[pos][2] == 1) {
                                ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(UnderlineSpan(), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (BibleGlobalList.vydelenie[pos][4] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        position++
                        bibliaVybranoeList.add(BibliaVybranoeData(vybranoeBibliaData.title, ssb))
                    }
                    count++
                }
            } else {
                val bibleline = split2[vybranoeBibliaData.glava].split("<br><br>")
                for (position in bibleline.indices) {
                    if (!(bibleline[position] == "" && bibleline.size - 1 == position && DialogVybranoeBibleList.arrayListVybranoe.size - 1 == index)) {
                        bibliaVybranoeList.add(BibliaVybranoeData(vybranoeBibliaData.title, MainActivity.fromHtml(bibleline[position])))
                    }
                    count++
                }
            }
        }
        adapter.notifyDataSetChanged()
        if (prodoljyt) {
            binding.ListView.setSelection(positionY)
        } else {
            binding.ListView.setSelection(scrollToPosition)
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
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
                        ListViewCompat.scrollListBy(binding.ListView, 2)
                    }
                }
            }
        }
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

    private fun startProcent() {
        procentJob?.cancel()
        bindingprogress.progress.visibility = View.VISIBLE
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            bindingprogress.progress.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarBrighess.visibility == View.VISIBLE) {
                bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this@BibliaVybranoe, by.carkva_gazeta.malitounik.R.anim.slide_out_left)
                bindingprogress.seekBarBrighess.visibility = View.GONE
            }
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@BibliaVybranoe, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
            }
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditors = k.edit()
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.apply()
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
            binding.ListView.smoothScrollToPosition(0)
            scrolltosatrt = true
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
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        if (mAutoScroll) {
            autoscroll = k.getBoolean("autoscroll", false)
            when {
                autoscroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
                diffScroll == 0 -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_up)
                else -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
            }
        } else {
            itemAuto.isVisible = false
            stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = dzenNoch
        val spanString = if (k.getBoolean("auto_dzen_noch", false)) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isCheckable = false
            SpannableString(getString(by.carkva_gazeta.malitounik.R.string.auto_widget_day_d_n))
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isCheckable = true
            SpannableString(getString(by.carkva_gazeta.malitounik.R.string.widget_day_d_n))
        }
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).title = spanString
    }

    override fun onPause() {
        super.onPause()
        val firstVisiblePosition = binding.ListView.firstVisiblePosition
        if (firstVisiblePosition != 0) {
            positionY = firstVisiblePosition
            val prefEditor = k.edit()
            prefEditor.putInt(resurs + "BibleVybranoeScroll", positionY)
            prefEditor.apply()
        }
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
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
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
            recreate()
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
        prefEditor.apply()
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
            if (dzenNoch) binding.ListView.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
            else binding.ListView.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
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

    override fun linkMovementMethodCheckOnTouch(onTouch: Boolean) {
        mActionDown = onTouch
    }

    private fun setLinkMovementMethodCheck(): LinkMovementMethodCheck? {
        linkMovementMethodCheck = LinkMovementMethodCheck()
        linkMovementMethodCheck?.setLinkMovementMethodCheckListener(this)
        return linkMovementMethodCheck
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("orientation", orientation)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putString("subTollBarText", binding.subtitleToolbar.text.toString())
    }

    private inner class BibliaVybranoeListAdaprer(private val activity: Activity) : ArrayAdapter<BibliaVybranoeData>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_maranata, bibliaVybranoeList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItemMaranataBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.movementMethod = setLinkMovementMethodCheck()
            val textView = bibliaVybranoeList[position].text
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            if (dzenNoch) viewHolder.text.setLinkTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
            viewHolder.text.text = textView
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class BibliaVybranoeData(val title: String, val text: Spanned)
}