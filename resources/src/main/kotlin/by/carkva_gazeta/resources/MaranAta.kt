package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
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
import android.text.style.*
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.databinding.SimpleListItemMaranataBinding
import by.carkva_gazeta.resources.databinding.AkafistMaranAtaBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.*

class MaranAta : AppCompatActivity(), OnTouchListener, DialogFontSizeListener, OnItemClickListener, OnItemLongClickListener {
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
    private var change = false
    private var cytanne = ""
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var autoscroll = false
    private lateinit var adapter: MaranAtaListAdaprer
    private val maranAta = ArrayList<String>()
    private var n = 0
    private var spid = 60
    private var belarus = false
    private var mActionDown = false
    private var setFont = false
    private var paralel = false
    private var onsave = false
    private var paralelPosition = 0
    private var tollBarText = ""
    private var mPosition = 0
    private var maranAtaScrollPosition = 0
    private var mOffset = 0
    private val uiAnimationDelay: Long = 300
    private val orientation: Int
        get() = MainActivity.getOrientation(this)
    private lateinit var binding: AkafistMaranAtaBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var diffScroll = -1
    private var scrolltosatrt = false

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        setFont = true
        adapter.notifyDataSetChanged()
    }

    private fun forceScroll() {
        val event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_MOVE, binding.ListView.x, -1f, 0)
        onTouch(binding.ListView, event)
    }

    private fun checkPosition(position: Int): Int {
        for (i in vydelenie.indices) {
            if (vydelenie[i][0] == position) {
                return i
            }
        }
        return -1
    }

    private fun clearEmptyPosition() {
        val remove = ArrayList<ArrayList<Int>>()
        for (i in vydelenie.indices) {
            var posrem = true
            for (e in 1 until vydelenie[i].size) {
                if (vydelenie[i][e] == 1) {
                    posrem = false
                    break
                }
            }
            if (posrem) {
                remove.add(vydelenie[i])
            }
        }
        vydelenie.removeAll(remove)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        belarus = k.getBoolean("belarus", false)
        spid = k.getInt("autoscrollSpid", 60)
        maranAtaScrollPosition = k.getInt("maranAtaScrollPasition", 0)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = AkafistMaranAtaBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        setTollbarTheme()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        bibleCopyList.clear()
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        binding.ListView.onItemClickListener = this
        binding.ListView.onItemLongClickListener = this
        binding.ListView.setOnTouchListener(this)
        adapter = MaranAtaListAdaprer(this)
        binding.ListView.adapter = adapter
        binding.ListView.divider = null
        binding.ListView.setSelection(maranAtaScrollPosition)
        cytanne = intent.extras?.getString("cytanneMaranaty") ?: ""
        setMaranata(cytanne)
        if (savedInstanceState != null) {
            onsave = true
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            change = savedInstanceState.getBoolean("change")
            tollBarText = savedInstanceState.getString("tollBarText") ?: ""
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
            paralel = savedInstanceState.getBoolean("paralel", paralel)
            binding.subtitleToolbar.text = savedInstanceState.getString("chtenie")
            if (paralel) {
                paralelPosition = savedInstanceState.getInt("paralelPosition")
                parralelMestaView(paralelPosition)
            }
        } else {
            if (k.getBoolean("autoscrollAutostart", false)) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                autoStartScroll()
            }
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
                setFont = true
                adapter.notifyDataSetChanged()
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
                setFont = true
                adapter.notifyDataSetChanged()
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
        binding.constraint.setOnTouchListener(this)
        if (dzenNoch) {
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.actionPlusBrighess.setImageResource(by.carkva_gazeta.malitounik.R.drawable.plus_v_kruge_black)
            bindingprogress.actionMinusBrighess.setImageResource(by.carkva_gazeta.malitounik.R.drawable.minus_v_kruge_black)
            bindingprogress.actionPlusFont.setImageResource(by.carkva_gazeta.malitounik.R.drawable.plus_v_kruge_black)
            bindingprogress.actionMinusFont.setImageResource(by.carkva_gazeta.malitounik.R.drawable.minus_v_kruge_black)
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
        }
        binding.copyBig.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val copyString = StringBuilder()
            bibleCopyList.sort()
            bibleCopyList.forEach {
                var textView = maranAta[it]
                textView = textView.replace("+-+", "")
                val t1 = textView.indexOf("$")
                if (t1 != -1) textView = textView.substring(0, t1)
                copyString.append("$textView<br>")
            }
            val clip = ClipData.newPlainText("", MainActivity.fromHtml(copyString.toString()).toString().trim())
            clipboard.setPrimaryClip(clip)
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.copy))
            binding.linearLayout4.visibility = View.GONE
            binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
            mPedakVisable = false
            bibleCopyList.clear()
            adapter.notifyDataSetChanged()
        }
        binding.adpravit.setOnClickListener {
            if (bibleCopyList.size > 0) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyString = StringBuilder()
                bibleCopyList.sort()
                bibleCopyList.forEach {
                    copyString.append("${maranAta[it]}<br>")
                }
                val share = MainActivity.fromHtml(copyString.toString()).toString().trim()
                val clip = ClipData.newPlainText("", share)
                clipboard.setPrimaryClip(clip)
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, share)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, null))
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.underline.setOnClickListener {
            if (bibleCopyList.size > 0) {
                val i = checkPosition(bibleCopyList[0])
                if (i != -1) {
                    if (vydelenie[i][2] == 0) {
                        vydelenie[i][2] = 1
                    } else {
                        vydelenie[i][2] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(bibleCopyList[0])
                    setVydelenie.add(0)
                    setVydelenie.add(1)
                    setVydelenie.add(0)
                    vydelenie.add(setVydelenie)
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                mPedakVisable = false
                bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.bold.setOnClickListener {
            if (bibleCopyList.size > 0) {
                val i = checkPosition(bibleCopyList[0])
                if (i != -1) {
                    if (vydelenie[i][3] == 0) {
                        vydelenie[i][3] = 1
                    } else {
                        vydelenie[i][3] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(bibleCopyList[0])
                    setVydelenie.add(0)
                    setVydelenie.add(0)
                    setVydelenie.add(1)
                    vydelenie.add(setVydelenie)
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                mPedakVisable = false
                bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.yelloy.setOnClickListener {
            if (bibleCopyList.size > 0) {
                val i = checkPosition(bibleCopyList[0])
                if (i != -1) {
                    if (vydelenie[i][1] == 0) {
                        vydelenie[i][1] = 1
                    } else {
                        vydelenie[i][1] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(bibleCopyList[0])
                    setVydelenie.add(1)
                    setVydelenie.add(0)
                    setVydelenie.add(0)
                    vydelenie.add(setVydelenie)
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                mPedakVisable = false
                bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        if (dzenNoch) {
            binding.linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta)
        }
        val file: File = if (belarus) File("$filesDir/MaranAtaBel/$cytanne.json") else File("$filesDir/MaranAta/$cytanne.json")
        if (file.exists()) {
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<Int?>?>?>() {}.type
            vydelenie = gson.fromJson(reader.readText(), type)
            inputStream.close()
        }
        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(list: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (list.adapter == null || list.getChildAt(0) == null) return
                val position = list.firstVisiblePosition
                maranAtaScrollPosition = position
                if (position == 0 && scrolltosatrt) {
                    startAutoScroll()
                    scrolltosatrt = false
                    invalidateOptionsMenu()
                }
                diffScroll = if (list.lastVisiblePosition == list.adapter.count - 1) list.getChildAt(list.childCount - 1).bottom - list.height
                else -1
                if (list.lastVisiblePosition == list.adapter.count - 1 && list.getChildAt(list.childCount - 1).bottom <= list.height) {
                    autoscroll = false
                    stopAutoScroll()
                    invalidateOptionsMenu()
                }
                setFont = false
                val offset = list.getChildAt(0).top
                if (mPosition < position) {
                    mOffset = 0
                }
                val scroll = if (mPosition == position && mOffset == offset) {
                    0
                } else if (mPosition > position && mOffset > offset) {
                    1
                } else if (mPosition == position && mOffset < offset) {
                    1
                } else {
                    1
                }
                if (!onsave) {
                    var nazva = ""
                    if (scroll == 1) {
                        nazva = if (list.lastVisiblePosition - 4 >= 0) maranAta[list.lastVisiblePosition - 4] else maranAta[list.lastVisiblePosition]
                    }
                    val oldtollBarText = binding.titleToolbar.text.toString()
                    if (oldtollBarText == "") {
                        nazva = maranAta[list.firstVisiblePosition + 2]
                        if (nazva.contains("nazva+++")) {
                            val t1 = nazva.indexOf("nazva+++")
                            val t2 = nazva.indexOf("-->", t1 + 8)
                            tollBarText = nazva.substring(t1 + 8, t2)
                            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
                            binding.subtitleToolbar.text = tollBarText
                        }
                    }
                    if (!nazva.contains(tollBarText) && scroll != 0) {
                        if (nazva.contains("nazva+++")) {
                            val t1 = nazva.indexOf("nazva+++")
                            val t2 = nazva.indexOf("-->", t1 + 8)
                            tollBarText = nazva.substring(t1 + 8, t2)
                            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
                            binding.subtitleToolbar.text = tollBarText
                        }
                    }
                    mPosition = position
                    mOffset = offset
                }
                onsave = false
            }
        })
        binding.ListView.post {
            binding.ListView.smoothScrollToPosition(maranAtaScrollPosition)
        }
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
        if (binding.linearLayout4.visibility == View.VISIBLE) {
            return false
        }
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.ListView) {
            stopAutoStartScroll()
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> mActionDown = true
                MotionEvent.ACTION_UP -> mActionDown = false
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

    private fun setMaranata(cytanne: String) {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val chten = cytanne.split(";")
        for (i in chten.indices) {
            val fit = chten[i].trim()
            var nazvaFull = ""
            var nazvaFullBel = ""
            try {
                var nachalo: Int
                var konec: Int
                var stixn = -1
                var stixk = -1
                val paralelnyeMesta = ParalelnyeMesta()
                val bible = paralelnyeMesta.biblia(fit)
                val kniga = bible[0]
                val nazva = bible[1]
                val nazvaBel = bible[2]
                val nomer = bible[3].toInt()
                nazvaFull = bible[4]
                nazvaFullBel = bible[5]
                if (nazvaFull == "") nazvaFull = bible[1]
                if (nazvaFullBel == "") nazvaFullBel = bible[2]
                val s2 = fit.lastIndexOf(" ")
                var s5 = -1
                if (s2 == -1) {
                    nachalo = 1
                    konec = 1
                } else {
                    val s3 = fit.indexOf(".", s2 + 1)
                    if (s3 != -1) {
                        val s4 = fit.indexOf("-")
                        s5 = fit.indexOf(".", s3 + 1)
                        nachalo = fit.substring(s2 + 1, s3).toInt()
                        stixn = fit.substring(s3 + 1, s4).toInt()
                        if (s5 != -1) {
                            konec = fit.substring(s4 + 1, s5).toInt()
                            stixk = fit.substring(s5 + 1).toInt()
                        } else {
                            konec = nachalo
                            stixk = fit.substring(s4 + 1).toInt()
                        }
                    } else {
                        val s4 = fit.indexOf("-", s2 + 1)
                        if (s4 != -1) {
                            nachalo = fit.substring(s2 + 1, s4).toInt()
                            konec = fit.substring(s4 + 1).toInt()
                        } else {
                            nachalo = fit.substring(s2 + 1).toInt()
                            konec = nachalo
                        }
                    }
                }
                val r = resources
                var inputStream = r.openRawResource(R.raw.biblias1)
                var replace = true
                if (belarus) {
                    when (nomer) {
                        17 -> inputStream = r.openRawResource(R.raw.sinaidals17)
                        18 -> inputStream = r.openRawResource(R.raw.sinaidals18)
                        19 -> inputStream = r.openRawResource(R.raw.sinaidals19)
                        26 -> inputStream = r.openRawResource(R.raw.sinaidals26)
                        27 -> inputStream = r.openRawResource(R.raw.sinaidals27)
                        31 -> inputStream = r.openRawResource(R.raw.sinaidals31)
                        32 -> inputStream = r.openRawResource(R.raw.sinaidals32)
                        47 -> inputStream = r.openRawResource(R.raw.sinaidals47)
                        48 -> inputStream = r.openRawResource(R.raw.sinaidals48)
                        49 -> inputStream = r.openRawResource(R.raw.sinaidals49)
                        50 -> inputStream = r.openRawResource(R.raw.sinaidals50)
                        else -> replace = false
                    }
                    when (nomer) {
                        1 -> inputStream = r.openRawResource(R.raw.biblias1)
                        2 -> inputStream = r.openRawResource(R.raw.biblias2)
                        3 -> inputStream = r.openRawResource(R.raw.biblias3)
                        4 -> inputStream = r.openRawResource(R.raw.biblias4)
                        5 -> inputStream = r.openRawResource(R.raw.biblias5)
                        6 -> inputStream = r.openRawResource(R.raw.biblias6)
                        7 -> inputStream = r.openRawResource(R.raw.biblias7)
                        8 -> inputStream = r.openRawResource(R.raw.biblias8)
                        9 -> inputStream = r.openRawResource(R.raw.biblias9)
                        10 -> inputStream = r.openRawResource(R.raw.biblias10)
                        11 -> inputStream = r.openRawResource(R.raw.biblias11)
                        12 -> inputStream = r.openRawResource(R.raw.biblias12)
                        13 -> inputStream = r.openRawResource(R.raw.biblias13)
                        14 -> inputStream = r.openRawResource(R.raw.biblias14)
                        15 -> inputStream = r.openRawResource(R.raw.biblias15)
                        16 -> inputStream = r.openRawResource(R.raw.biblias16)
                        20 -> inputStream = r.openRawResource(R.raw.biblias17)
                        21 -> inputStream = r.openRawResource(R.raw.biblias18)
                        22 -> inputStream = r.openRawResource(R.raw.biblias19)
                        23 -> inputStream = r.openRawResource(R.raw.biblias20)
                        24 -> inputStream = r.openRawResource(R.raw.biblias21)
                        25 -> inputStream = r.openRawResource(R.raw.biblias22)
                        28 -> inputStream = r.openRawResource(R.raw.biblias23)
                        29 -> inputStream = r.openRawResource(R.raw.biblias24)
                        30 -> inputStream = r.openRawResource(R.raw.biblias25)
                        33 -> inputStream = r.openRawResource(R.raw.biblias26)
                        34 -> inputStream = r.openRawResource(R.raw.biblias27)
                        35 -> inputStream = r.openRawResource(R.raw.biblias28)
                        36 -> inputStream = r.openRawResource(R.raw.biblias29)
                        37 -> inputStream = r.openRawResource(R.raw.biblias30)
                        38 -> inputStream = r.openRawResource(R.raw.biblias31)
                        39 -> inputStream = r.openRawResource(R.raw.biblias32)
                        40 -> inputStream = r.openRawResource(R.raw.biblias33)
                        41 -> inputStream = r.openRawResource(R.raw.biblias34)
                        42 -> inputStream = r.openRawResource(R.raw.biblias35)
                        43 -> inputStream = r.openRawResource(R.raw.biblias36)
                        44 -> inputStream = r.openRawResource(R.raw.biblias37)
                        45 -> inputStream = r.openRawResource(R.raw.biblias38)
                        46 -> inputStream = r.openRawResource(R.raw.biblias39)
                        51 -> inputStream = r.openRawResource(R.raw.biblian1)
                        52 -> inputStream = r.openRawResource(R.raw.biblian2)
                        53 -> inputStream = r.openRawResource(R.raw.biblian3)
                        54 -> inputStream = r.openRawResource(R.raw.biblian4)
                        55 -> inputStream = r.openRawResource(R.raw.biblian5)
                        56 -> inputStream = r.openRawResource(R.raw.biblian6)
                        57 -> inputStream = r.openRawResource(R.raw.biblian7)
                        58 -> inputStream = r.openRawResource(R.raw.biblian8)
                        59 -> inputStream = r.openRawResource(R.raw.biblian9)
                        60 -> inputStream = r.openRawResource(R.raw.biblian10)
                        61 -> inputStream = r.openRawResource(R.raw.biblian11)
                        62 -> inputStream = r.openRawResource(R.raw.biblian12)
                        63 -> inputStream = r.openRawResource(R.raw.biblian13)
                        64 -> inputStream = r.openRawResource(R.raw.biblian14)
                        65 -> inputStream = r.openRawResource(R.raw.biblian15)
                        66 -> inputStream = r.openRawResource(R.raw.biblian16)
                        67 -> inputStream = r.openRawResource(R.raw.biblian17)
                        68 -> inputStream = r.openRawResource(R.raw.biblian18)
                        69 -> inputStream = r.openRawResource(R.raw.biblian19)
                        70 -> inputStream = r.openRawResource(R.raw.biblian20)
                        71 -> inputStream = r.openRawResource(R.raw.biblian21)
                        72 -> inputStream = r.openRawResource(R.raw.biblian22)
                        73 -> inputStream = r.openRawResource(R.raw.biblian23)
                        74 -> inputStream = r.openRawResource(R.raw.biblian24)
                        75 -> inputStream = r.openRawResource(R.raw.biblian25)
                        76 -> inputStream = r.openRawResource(R.raw.biblian26)
                        77 -> inputStream = r.openRawResource(R.raw.biblian27)
                    }
                } else {
                    when (nomer) {
                        1 -> inputStream = r.openRawResource(R.raw.sinaidals1)
                        2 -> inputStream = r.openRawResource(R.raw.sinaidals2)
                        3 -> inputStream = r.openRawResource(R.raw.sinaidals3)
                        4 -> inputStream = r.openRawResource(R.raw.sinaidals4)
                        5 -> inputStream = r.openRawResource(R.raw.sinaidals5)
                        6 -> inputStream = r.openRawResource(R.raw.sinaidals6)
                        7 -> inputStream = r.openRawResource(R.raw.sinaidals7)
                        8 -> inputStream = r.openRawResource(R.raw.sinaidals8)
                        9 -> inputStream = r.openRawResource(R.raw.sinaidals9)
                        10 -> inputStream = r.openRawResource(R.raw.sinaidals10)
                        11 -> inputStream = r.openRawResource(R.raw.sinaidals11)
                        12 -> inputStream = r.openRawResource(R.raw.sinaidals12)
                        13 -> inputStream = r.openRawResource(R.raw.sinaidals13)
                        14 -> inputStream = r.openRawResource(R.raw.sinaidals14)
                        15 -> inputStream = r.openRawResource(R.raw.sinaidals15)
                        16 -> inputStream = r.openRawResource(R.raw.sinaidals16)
                        17 -> inputStream = r.openRawResource(R.raw.sinaidals17)
                        18 -> inputStream = r.openRawResource(R.raw.sinaidals18)
                        19 -> inputStream = r.openRawResource(R.raw.sinaidals19)
                        20 -> inputStream = r.openRawResource(R.raw.sinaidals20)
                        21 -> inputStream = r.openRawResource(R.raw.sinaidals21)
                        22 -> inputStream = r.openRawResource(R.raw.sinaidals22)
                        23 -> inputStream = r.openRawResource(R.raw.sinaidals23)
                        24 -> inputStream = r.openRawResource(R.raw.sinaidals24)
                        25 -> inputStream = r.openRawResource(R.raw.sinaidals25)
                        26 -> inputStream = r.openRawResource(R.raw.sinaidals26)
                        27 -> inputStream = r.openRawResource(R.raw.sinaidals27)
                        28 -> inputStream = r.openRawResource(R.raw.sinaidals28)
                        29 -> inputStream = r.openRawResource(R.raw.sinaidals29)
                        30 -> inputStream = r.openRawResource(R.raw.sinaidals30)
                        31 -> inputStream = r.openRawResource(R.raw.sinaidals31)
                        32 -> inputStream = r.openRawResource(R.raw.sinaidals32)
                        33 -> inputStream = r.openRawResource(R.raw.sinaidals33)
                        34 -> inputStream = r.openRawResource(R.raw.sinaidals34)
                        35 -> inputStream = r.openRawResource(R.raw.sinaidals35)
                        36 -> inputStream = r.openRawResource(R.raw.sinaidals36)
                        37 -> inputStream = r.openRawResource(R.raw.sinaidals37)
                        38 -> inputStream = r.openRawResource(R.raw.sinaidals38)
                        39 -> inputStream = r.openRawResource(R.raw.sinaidals39)
                        40 -> inputStream = r.openRawResource(R.raw.sinaidals40)
                        41 -> inputStream = r.openRawResource(R.raw.sinaidals41)
                        42 -> inputStream = r.openRawResource(R.raw.sinaidals42)
                        43 -> inputStream = r.openRawResource(R.raw.sinaidals43)
                        44 -> inputStream = r.openRawResource(R.raw.sinaidals44)
                        45 -> inputStream = r.openRawResource(R.raw.sinaidals45)
                        46 -> inputStream = r.openRawResource(R.raw.sinaidals46)
                        47 -> inputStream = r.openRawResource(R.raw.sinaidals47)
                        48 -> inputStream = r.openRawResource(R.raw.sinaidals48)
                        49 -> inputStream = r.openRawResource(R.raw.sinaidals49)
                        50 -> inputStream = r.openRawResource(R.raw.sinaidals50)
                        51 -> inputStream = r.openRawResource(R.raw.sinaidaln1)
                        52 -> inputStream = r.openRawResource(R.raw.sinaidaln2)
                        53 -> inputStream = r.openRawResource(R.raw.sinaidaln3)
                        54 -> inputStream = r.openRawResource(R.raw.sinaidaln4)
                        55 -> inputStream = r.openRawResource(R.raw.sinaidaln5)
                        56 -> inputStream = r.openRawResource(R.raw.sinaidaln6)
                        57 -> inputStream = r.openRawResource(R.raw.sinaidaln7)
                        58 -> inputStream = r.openRawResource(R.raw.sinaidaln8)
                        59 -> inputStream = r.openRawResource(R.raw.sinaidaln9)
                        60 -> inputStream = r.openRawResource(R.raw.sinaidaln10)
                        61 -> inputStream = r.openRawResource(R.raw.sinaidaln11)
                        62 -> inputStream = r.openRawResource(R.raw.sinaidaln12)
                        63 -> inputStream = r.openRawResource(R.raw.sinaidaln13)
                        64 -> inputStream = r.openRawResource(R.raw.sinaidaln14)
                        65 -> inputStream = r.openRawResource(R.raw.sinaidaln15)
                        66 -> inputStream = r.openRawResource(R.raw.sinaidaln16)
                        67 -> inputStream = r.openRawResource(R.raw.sinaidaln17)
                        68 -> inputStream = r.openRawResource(R.raw.sinaidaln18)
                        69 -> inputStream = r.openRawResource(R.raw.sinaidaln19)
                        70 -> inputStream = r.openRawResource(R.raw.sinaidaln20)
                        71 -> inputStream = r.openRawResource(R.raw.sinaidaln21)
                        72 -> inputStream = r.openRawResource(R.raw.sinaidaln22)
                        73 -> inputStream = r.openRawResource(R.raw.sinaidaln23)
                        74 -> inputStream = r.openRawResource(R.raw.sinaidaln24)
                        75 -> inputStream = r.openRawResource(R.raw.sinaidaln25)
                        76 -> inputStream = r.openRawResource(R.raw.sinaidaln26)
                        77 -> inputStream = r.openRawResource(R.raw.sinaidaln27)
                    }
                    replace = false
                }
                if (replace) {
                    maranAta.add("<!--no--><br><em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error) + "</em>")
                }
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                val builder = StringBuilder()
                reader.forEachLine {
                    line = it
                    line = line.replace("\\n", "<br>")
                    if (line.contains("//")) {
                        val t1 = line.indexOf("//")
                        line = line.substring(0, t1).trim()
                        if (line != "") builder.append(line).append("\n")
                    } else {
                        builder.append(line).append("\n")
                    }
                }
                inputStream.close()
                if (chten.size == 6 && i == 3) {
                    if (belarus) {
                        maranAta.add("<br><em><!--no-->" + resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_be) + "</em><br>\n")
                    } else {
                        maranAta.add("<br><em><!--no-->" + resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_ru) + "</em><br>\n")
                    }
                }
                val split2 = builder.toString().split("===")
                if (konec == split2.size) konec -= 1
                var vN: Int
                var vK: Int
                val r1 = StringBuilder()
                var r2 = ""
                for (e in nachalo..konec) {
                    if (stixn != -1) {
                        if (s5 != -1) {
                            if (e == nachalo) {
                                vN = if (belarus) split2[e].indexOf("$stixn. ") else split2[e].indexOf("$stixn ")
                                r1.append(split2[e].substring(vN).trim())
                            }
                            if (e != nachalo && e != konec) {
                                r1.append("\n").append(split2[e].trim())
                            }
                            if (e == konec) {
                                val vK1: Int = if (belarus) split2[e].indexOf("$stixk. ") else split2[e].indexOf("$stixk ")
                                vK = split2[e].indexOf("\n", vK1)
                                r2 = split2[e].substring(0, vK)
                            }
                        } else {
                            var vK1: Int
                            if (belarus) {
                                vN = split2[e].indexOf("$stixn. ")
                                vK1 = split2[e].indexOf("$stixk. ")
                            } else {
                                vN = split2[e].indexOf("$stixn ")
                                vK1 = split2[e].indexOf("$stixk ")
                            }
                            vK = split2[e].indexOf("\n", vK1)
                            r1.append(split2[e].substring(vN, vK))
                        }
                    } else {
                        if (belarus) {
                            maranAta.add("<!--no--><!--nazva+++$nazvaBel $e--><br><strong>$nazvaFullBel $e</strong><br>\n")
                        } else {
                            maranAta.add("<!--no--><!--nazva+++$nazva $e--><br><strong>$nazvaFull $e</strong><br>\n")
                        }
                        val splitline = split2[e].trim().split("\n")
                        var i3: Int
                        for (i2 in splitline.indices) {
                            i3 = if (kniga.contains("Сир") && e == 1 && i2 >= 8) i2 - 7 else i2 + 1
                            if (belarus) maranAta.add("<!--" + kniga + "." + e + "." + i3 + "--><!--nazva+++" + nazvaBel + " " + e + "-->" + splitline[i2] + getParallel(nomer, e, i2) + "\n") else maranAta.add(
                                "<!--" + kniga + "." + e + "." + i3 + "--><!--nazva+++" + nazva + " " + e + "-->" + splitline[i2] + getParallel(nomer, e, i2) + "\n")
                        }
                    }
                }
                if (stixn != -1) {
                    val t1 = fit.indexOf(".")
                    if (belarus) {
                        maranAta.add("<!--no--><!--nazva+++$nazvaBel " + fit.substring(s2 + 1, t1) + "--><br><strong>" + nazvaFullBel + " " + fit.substring(s2 + 1) + "</strong><br>\n")
                    } else {
                        maranAta.add("<!--no--><!--nazva+++$nazva " + fit.substring(s2 + 1, t1) + "--><br><strong>" + nazvaFull + " " + fit.substring(s2 + 1) + "</strong><br>\n")
                    }
                    val res1 = r1.toString().trim().split("\n")
                    var i2 = 0
                    var i3 = stixn
                    while (i2 < res1.size) {
                        if (belarus) maranAta.add("<!--$kniga.$nachalo.$i3--><!--nazva+++$nazvaBel " + fit.substring(s2 + 1, t1) + "-->" + res1[i2] + getParallel(nomer, nachalo, i3 - 1) + "\n") else maranAta.add(
                            "<!--$kniga.$nachalo.$i3--><!--nazva+++$nazva " + fit.substring(s2 + 1, t1) + "-->" + res1[i2] + getParallel(nomer, nachalo, i3 - 1) + "\n")
                        i2++
                        i3++
                    }
                    if (konec - nachalo != 0) {
                        val res2 = r2.trim().split("\n")
                        for (i21 in res2.indices) {
                            if (belarus) maranAta.add("<!--" + kniga + "." + konec + "." + (i21 + 1) + "--><!--nazva+++" + nazvaBel + " " + konec + "-->" + res2[i21] + getParallel(nomer,
                                konec,
                                i21) + "\n") else maranAta.add("<!--" + kniga + "." + konec + "." + (i21 + 1) + "--><!--nazva+++" + nazva + " " + konec + "-->" + res2[i21] + getParallel(nomer,
                                konec,
                                i21) + "\n")
                        }
                    }
                }
            } catch (t: Throwable) {
                val t1 = fit.lastIndexOf(" ")
                if (belarus) {
                    maranAta.add("<!--no--><br><strong>$nazvaFullBel ${fit.substring(t1 + 1)}</strong><br>\n")
                } else {
                    maranAta.add("<!--no--><br><strong>$nazvaFull ${fit.substring(t1 + 1)}</strong><br>\n")
                }
                maranAta.add("<!--no-->" + resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch) + "\n")
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor: Editor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
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
        if (diffScroll != 0) {
            val prefEditor: Editor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            binding.actionMinus.visibility = View.VISIBLE
            binding.actionPlus.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            stopAutoStartScroll()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    forceScroll()
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        val firstPosition = binding.ListView.firstVisiblePosition
                        if (firstPosition == INVALID_POSITION) {
                            return@launch
                        }
                        val firstView = binding.ListView.getChildAt(0) ?: return@launch
                        val newTop = firstView.top - 2
                        binding.ListView.setSelectionFromTop(firstPosition, newTop)
                    }
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding.ListView.smoothScrollToPosition(0)
            scrolltosatrt = true
        }
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
        var parallel = false
        if (!autoscroll) {
            autoscroll = true
            parallel = true
        }
        procentJob?.cancel()
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(delayTime)
            bindingprogress.progress.visibility = View.GONE
            bindingprogress.fontSize.visibility = View.GONE
            bindingprogress.brighess.visibility = View.GONE
            if (parallel) autoscroll = false
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

    override fun onBackPressed() {
        if (paralel) {
            binding.scroll.visibility = View.GONE
            binding.ListView.visibility = View.VISIBLE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
            paralel = false
            invalidateOptionsMenu()
        } else if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else if (mPedakVisable) {
            mPedakVisable = false
            bibleCopyList.clear()
            adapter.notifyDataSetChanged()
            if (binding.linearLayout4.visibility == View.VISIBLE) {
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
            }
            invalidateOptionsMenu()
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
        clearEmptyPosition()
        val file: File = if (belarus) {
            File("$filesDir/MaranAtaBel/$cytanne.json")
        } else {
            File("$filesDir/MaranAta/$cytanne.json")
        }
        if (vydelenie.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            val gson = Gson()
            val outputStream = FileWriter(file)
            outputStream.write(gson.toJson(vydelenie))
            outputStream.close()
        }
        mPedakVisable = false
        bibleCopyList.clear()
        binding.linearLayout4.visibility = View.GONE
        val prefEditors = k.edit()
        maranAtaScrollPosition = binding.ListView.firstVisiblePosition
        prefEditors.putInt("maranAtaScrollPasition", maranAtaScrollPosition)
        prefEditors.apply()
        autoStartScrollJob?.cancel()
        procentJob?.cancel()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (fullscreenPage) hide()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            startAutoScroll()
        }
        bindingprogress.progress.visibility = View.GONE
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        if (binding.linearLayout4.visibility == View.VISIBLE) {
            itemAuto.isVisible = false
        } else {
            if (paralel) {
                binding.subtitleToolbar.visibility = View.GONE
                itemAuto.isVisible = false
            } else {
                binding.subtitleToolbar.visibility = View.VISIBLE
                itemAuto.isVisible = true
            }
            mActionDown = false
        }
        if (autoscroll) {
            itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
        } else {
            itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
        }

        val spanString = SpannableString(itemAuto.title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString

        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_paralel).isChecked = k.getBoolean("paralel_maranata", true)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_paralel).isVisible = true
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_semuxa).isVisible = true
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_semuxa).isChecked = k.getBoolean("belarus", false)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditor = k.edit()
        if (id == android.R.id.home) {
            if (paralel) {
                onBackPressed()
                return true
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_semuxa) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("belarus", true)
                if (k.getBoolean("SemuxaNoKnigi", true)) {
                    val semuxaNoKnigi = DialogSemuxaNoKnigi()
                    semuxaNoKnigi.show(supportFragmentManager, "semuxa_no_knigi")
                }
            } else {
                prefEditor.putBoolean("belarus", false)
            }
            prefEditor.apply()
            recreate()
        }
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_paralel) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("paralel_maranata", true)
            } else {
                prefEditor.putBoolean("paralel_maranata", false)
            }
            prefEditor.apply()
            adapter.notifyDataSetChanged()
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
        outState.putString("tollBarText", tollBarText)
        outState.putBoolean("paralel", paralel)
        outState.putInt("paralelPosition", paralelPosition)
        outState.putString("chtenie", binding.subtitleToolbar.text.toString())
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (mPedakVisable) {
            var find = false
            bibleCopyList.forEach {
                if (it == position) find = true
            }
            if (find) {
                bibleCopyList.remove(position)
            } else {
                bibleCopyList.add(position)
            }
            adapter.notifyDataSetChanged()
        } else {
            bibleCopyList.clear()
            parralelMestaView(position)
            paralelPosition = position
        }
        if (mPedakVisable) {
            if (bibleCopyList.size > 1) {
                binding.yelloy.visibility = View.GONE
                binding.underline.visibility = View.GONE
                binding.bold.visibility = View.GONE
            } else {
                binding.yelloy.visibility = View.VISIBLE
                binding.underline.visibility = View.VISIBLE
                binding.bold.visibility = View.VISIBLE
            }
        }
    }

    private fun parralelMestaView(position: Int) {
        if (k.getBoolean("paralel_maranata", true)) {
            if (!autoscroll) {
                val t1 = maranAta[position].indexOf("$")
                if (t1 != -1) {
                    paralel = true
                    val pm = ParalelnyeMesta()
                    val t2 = maranAta[position].indexOf("-->")
                    val t3 = maranAta[position].indexOf("<!--")
                    val ch = maranAta[position].substring(t3 + 4, t2)
                    val biblia = ch.split(".")
                    binding.conteiner.removeAllViewsInLayout()
                    val arrayList = pm.paralel(this, biblia[0] + " " + biblia[1] + "." + biblia[2], maranAta[position].substring(t1 + 1).trim(), belarus)
                    for (i in arrayList.indices) {
                        binding.conteiner.addView(arrayList[i])
                    }
                    binding.scroll.visibility = View.VISIBLE
                    binding.ListView.visibility = View.GONE
                    binding.titleToolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, biblia[0] + " " + biblia[1] + "." + biblia[2])
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        if (!autoscroll) {
            if (!maranAta[position].contains("<!--no-->") && maranAta[position].trim() != "") {
                mPedakVisable = true
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
                binding.linearLayout4.visibility = View.VISIBLE
                var find = false
                bibleCopyList.forEach {
                    if (it == position) find = true
                }
                if (find) {
                    bibleCopyList.remove(position)
                } else {
                    bibleCopyList.add(position)
                }
                adapter.notifyDataSetChanged()
                invalidateOptionsMenu()
            }
        }
        return true
    }

    private fun getParallel(kniga: Int, glava: Int, position: Int): String {
        val parallel = BibliaParallelChtenia()
        var res = ""
        if (kniga == 1) {
            res = parallel.kniga1(glava, position + 1)
        }
        if (kniga == 2) {
            res = parallel.kniga2(glava, position + 1)
        }
        if (kniga == 3) {
            res = parallel.kniga3(glava, position + 1)
        }
        if (kniga == 4) {
            res = parallel.kniga4(glava, position + 1)
        }
        if (kniga == 5) {
            res = parallel.kniga5(glava, position + 1)
        }
        if (kniga == 6) {
            res = parallel.kniga6(glava, position + 1)
        }
        if (kniga == 7) {
            res = parallel.kniga7(glava, position + 1)
        }
        if (kniga == 8) {
            res = parallel.kniga8(glava, position + 1)
        }
        if (kniga == 9) {
            res = parallel.kniga9(glava, position + 1)
        }
        if (kniga == 10) {
            res = parallel.kniga10(glava, position + 1)
        }
        if (kniga == 11) {
            res = parallel.kniga11(glava, position + 1)
        }
        if (kniga == 12) {
            res = parallel.kniga12(glava, position + 1)
        }
        if (kniga == 13) {
            res = parallel.kniga13(glava, position + 1)
        }
        if (kniga == 14) {
            res = parallel.kniga14(glava, position + 1)
        }
        if (kniga == 15) {
            res = parallel.kniga15(glava, position + 1)
        }
        if (kniga == 16) {
            res = parallel.kniga16(glava, position + 1)
        }
        if (kniga == 17) {
            res = parallel.kniga17(glava, position + 1)
        }
        if (kniga == 18) {
            res = parallel.kniga18(glava, position + 1)
        }
        if (kniga == 19) {
            res = parallel.kniga19(glava, position + 1)
        }
        if (kniga == 20) {
            res = parallel.kniga20(glava, position + 1)
        }
        if (kniga == 21) {
            res = parallel.kniga21(glava, position + 1)
        }
        if (kniga == 22) {
            res = parallel.kniga22(glava, position + 1)
        }
        if (kniga == 23) {
            res = parallel.kniga23(glava, position + 1)
        }
        if (kniga == 24) {
            res = parallel.kniga24(glava, position + 1)
        }
        if (kniga == 25) {
            res = parallel.kniga25(glava, position + 1)
        }
        if (kniga == 26) {
            res = parallel.kniga26(glava, position + 1)
        }
        if (kniga == 27) {
            res = parallel.kniga27(glava, position + 1)
        }
        if (kniga == 28) {
            res = parallel.kniga28(glava, position + 1)
        }
        if (kniga == 29) {
            res = parallel.kniga29(glava, position + 1)
        }
        if (kniga == 30) {
            res = parallel.kniga30(glava, position + 1)
        }
        if (kniga == 31) {
            res = parallel.kniga31(glava, position + 1)
        }
        if (kniga == 32) {
            res = parallel.kniga32(glava, position + 1)
        }
        if (kniga == 33) {
            res = parallel.kniga33(glava, position + 1)
        }
        if (kniga == 34) {
            res = parallel.kniga34(glava, position + 1)
        }
        if (kniga == 35) {
            res = parallel.kniga35(glava, position + 1)
        }
        if (kniga == 36) {
            res = parallel.kniga36(glava, position + 1)
        }
        if (kniga == 37) {
            res = parallel.kniga37(glava, position + 1)
        }
        if (kniga == 38) {
            res = parallel.kniga38(glava, position + 1)
        }
        if (kniga == 39) {
            res = parallel.kniga39(glava, position + 1)
        }
        if (kniga == 40) {
            res = parallel.kniga40(glava, position + 1)
        }
        if (kniga == 41) {
            res = parallel.kniga41(glava, position + 1)
        }
        if (kniga == 42) {
            res = parallel.kniga42(glava, position + 1)
        }
        if (kniga == 43) {
            res = parallel.kniga43(glava, position + 1)
        }
        if (kniga == 44) {
            res = parallel.kniga44(glava, position + 1)
        }
        if (kniga == 45) {
            res = parallel.kniga45(glava, position + 1)
        }
        if (kniga == 46) {
            res = parallel.kniga46(glava, position + 1)
        }
        if (kniga == 47) {
            res = parallel.kniga47(glava, position + 1)
        }
        if (kniga == 48) {
            res = parallel.kniga48(glava, position + 1)
        }
        if (kniga == 49) {
            res = parallel.kniga49(glava, position + 1)
        }
        if (kniga == 50) {
            res = parallel.kniga50(glava, position + 1)
        }
        if (kniga == 51) {
            res = parallel.kniga51(glava, position + 1)
        }
        if (kniga == 52) {
            res = parallel.kniga52(glava, position + 1)
        }
        if (kniga == 53) {
            res = parallel.kniga53(glava, position + 1)
        }
        if (kniga == 54) {
            res = parallel.kniga54(glava, position + 1)
        }
        if (kniga == 55) {
            res = parallel.kniga55(glava, position + 1)
        }
        if (kniga == 56) {
            res = parallel.kniga56(glava, position + 1)
        }
        if (kniga == 57) {
            res = parallel.kniga57(glava, position + 1)
        }
        if (kniga == 58) {
            res = parallel.kniga58(glava, position + 1)
        }
        if (kniga == 59) {
            res = parallel.kniga59(glava, position + 1)
        }
        if (kniga == 60) {
            res = parallel.kniga60(glava, position + 1)
        }
        if (kniga == 61) {
            res = parallel.kniga61(glava, position + 1)
        }
        if (kniga == 62) {
            res = parallel.kniga62(glava, position + 1)
        }
        if (kniga == 63) {
            res = parallel.kniga63(glava, position + 1)
        }
        if (kniga == 64) {
            res = parallel.kniga64(glava, position + 1)
        }
        if (kniga == 65) {
            res = parallel.kniga65(glava, position + 1)
        }
        if (kniga == 66) {
            res = parallel.kniga66(glava, position + 1)
        }
        if (kniga == 67) {
            res = parallel.kniga67(glava, position + 1)
        }
        if (kniga == 68) {
            res = parallel.kniga68(glava, position + 1)
        }
        if (kniga == 69) {
            res = parallel.kniga69(glava, position + 1)
        }
        if (kniga == 70) {
            res = parallel.kniga70(glava, position + 1)
        }
        if (kniga == 71) {
            res = parallel.kniga71(glava, position + 1)
        }
        if (kniga == 72) {
            res = parallel.kniga72(glava, position + 1)
        }
        if (kniga == 73) {
            res = parallel.kniga73(glava, position + 1)
        }
        if (kniga == 74) {
            res = parallel.kniga74(glava, position + 1)
        }
        if (kniga == 75) {
            res = parallel.kniga75(glava, position + 1)
        }
        if (kniga == 76) {
            res = parallel.kniga76(glava, position + 1)
        }
        if (kniga == 77) {
            res = parallel.kniga77(glava, position + 1)
        }
        if (!res.contains("+-+")) {
            if (belarus) res = MainActivity.translateToBelarus(res)
            res = "$$res"
        }
        return res
    }

    private inner class MaranAtaListAdaprer(private val activity: Activity) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_maranata, maranAta) {
        override fun isEnabled(position: Int): Boolean {
            return if (maranAta[position].contains("<!--no-->")) false else if (!autoscroll) super.isEnabled(position) else false
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null || setFont) {
                val binding = SimpleListItemMaranataBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.tag = position
            var textView = maranAta[position]
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            textView = textView.replace("+-+", "")
            var t1 = textView.indexOf("$")
            val ssb: SpannableStringBuilder
            var end: Int
            if (t1 != -1) {
                val t2 = textView.indexOf("-->")
                if (t2 != -1) {
                    textView = textView.substring(t2 + 3)
                    t1 = textView.indexOf("$")
                }
                val paralelLeg = textView.substring(t1 + 1).length
                textView = textView.replace("$", "<br>")
                val spanned = MainActivity.fromHtml(textView.trim())
                end = spanned.length
                t1 = end - paralelLeg
                ssb = SpannableStringBuilder(spanned)
                if (k.getBoolean("paralel_maranata", true)) {
                    ssb.setSpan(RelativeSizeSpan(0.7f), t1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorSecondary_text)), t1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    ssb.delete(t1, end)
                    end = t1
                }
                val pos = checkPosition(position)
                if (pos != -1) {
                    if (vydelenie[pos][1] == 1) {
                        if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)),
                            0,
                            t1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorYelloy)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (vydelenie[pos][2] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (vydelenie[pos][3] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                val spanned = MainActivity.fromHtml(textView)
                end = spanned.length
                ssb = SpannableStringBuilder(spanned)
                val pos = checkPosition(position)
                if (pos != -1) {
                    if (vydelenie[pos][1] == 1) {
                        if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)),
                            0,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorYelloy)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (vydelenie[pos][2] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (vydelenie[pos][3] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            viewHolder.text.text = ssb
            if (bibleCopyList.size > 0 && bibleCopyList.contains(position) && mPedakVisable) {
                if (dzenNoch) {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark2)
                    viewHolder.text.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
                } else {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
                }
            } else {
                if (dzenNoch) {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                    viewHolder.text.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
                } else {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
                }
            }
            return rootView
        }

    }

    private class ViewHolder(var text: TextViewRobotoCondensed)

    companion object {
        private var mPedakVisable = false
        private var vydelenie = ArrayList<ArrayList<Int>>()
        private var bibleCopyList = ArrayList<Int>()
    }
}