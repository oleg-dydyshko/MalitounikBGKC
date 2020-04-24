package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
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
import android.text.style.*
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import android.widget.AdapterView.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.akafist_maran_ata.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 18.10.16
 */
class MaranAta : AppCompatActivity(), OnTouchListener, DialogFontSizeListener, OnItemClickListener, OnItemLongClickListener {
    private val mHideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        constraint.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
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
    private var change = false
    private var cytanne = ""
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var autoscroll = false
    private lateinit var adapter: MaranAtaListAdaprer
    private val maranAta = ArrayList<String>()
    private var n = 0
    private var yS = 0
    private var spid = 60
    private var belarus = false
    private var scrollTimer = Timer()
    private var procentTimer = Timer()
    private var resetTimer = Timer()
    private var scrollerSchedule: TimerTask? = null
    private var procentSchedule: TimerTask? = null
    private var resetSchedule: TimerTask? = null
    private var levo = false
    private var pravo = false
    private var niz = false
    private var mActionDown = false
    private var setFont = false
    private var paralel = false
    private var onsave = false
    private var paralelPosition = 0
    private var tollBarText = ""
    private var mPosition = 0
    private var mOffset = 0
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
        setFont = true
        adapter.notifyDataSetChanged()
    }

    private fun forceScroll() {
        val event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_MOVE, ListView.x, -1f, 0)
        onTouch(ListView, event)
    }

    private fun checkPosition(position: Int): Int {
        for (i in BibleGlobalList.vydelenie.indices) {
            if (BibleGlobalList.vydelenie[i][0] == position) {
                return i
            }
        }
        return -1
    }

    private fun clearEmptyPosition() {
        val remove = ArrayList<ArrayList<Int>>()
        for (i in BibleGlobalList.vydelenie.indices) {
            var posrem = true
            for (e in 1 until BibleGlobalList.vydelenie[i].size) {
                if (BibleGlobalList.vydelenie[i][e] == 1) {
                    posrem = false
                    break
                }
            }
            if (posrem) {
                remove.add(BibleGlobalList.vydelenie[i])
            }
        }
        BibleGlobalList.vydelenie.removeAll(remove)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        belarus = k.getBoolean("belarus", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        setContentView(R.layout.akafist_maran_ata)
        setTollbarTheme()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        BibleGlobalList.bibleCopyList.clear()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        ListView.onItemClickListener = this
        ListView.onItemLongClickListener = this
        ListView.setOnTouchListener(this)
        adapter = MaranAtaListAdaprer(this)
        ListView.adapter = adapter
        ListView.divider = null
        cytanne = intent.extras?.getString("cytanneMaranaty") ?: ""
        setMaranata(cytanne)
        if (savedInstanceState != null) {
            onsave = true
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            change = savedInstanceState.getBoolean("change")
            tollBarText = savedInstanceState.getString("tollBarText") ?: ""
            title_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
            paralel = savedInstanceState.getBoolean("paralel", paralel)
            subtitle_toolbar.text = savedInstanceState.getString("chtenie")
            if (paralel) {
                paralelPosition = savedInstanceState.getInt("paralelPosition")
                parralelMestaView(paralelPosition)
            }
        }
        ListView.post {
            ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
                override fun onScroll(list: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    if (list.adapter == null || list.getChildAt(0) == null) return
                    if (list.lastVisiblePosition == list.adapter.count - 1 && list.getChildAt(list.childCount - 1).bottom <= list.height) {
                        autoscroll = false
                        stopAutoScroll()
                        val prefEditors = k.edit()
                        prefEditors.putBoolean("autoscroll", false)
                        prefEditors.apply()
                        invalidateOptionsMenu()
                    }
                    setFont = false
                    val position = list.firstVisiblePosition
                    val offset = list.getChildAt(0).top
                    if (mPosition < position) {
                        mOffset = 0
                    }
                    val scroll: Int
                    scroll = if (mPosition == position && mOffset == offset) { // прокрутка стоит
                        0
                    } else if (mPosition > position && mOffset > offset) { // прокрутка идет вверх
                        1 //-1;
                    } else if (mPosition == position && mOffset < offset) { // прокрутка идет вверх
                        1 //-1;
                    } else { // прокрутка идет вниз
                        1
                    }
                    if (!onsave) {
                        var nazva = ""
                        if (scroll == 1) {
                            nazva = if (list.lastVisiblePosition - 4 >= 0) maranAta[list.lastVisiblePosition - 4] else maranAta[list.lastVisiblePosition]
                        }
                        val oldtollBarText = title_toolbar.text.toString()
                        if (oldtollBarText == "") {
                            nazva = maranAta[list.firstVisiblePosition + 2]
                            if (nazva.contains("nazva+++")) {
                                val t1 = nazva.indexOf("nazva+++")
                                val t2 = nazva.indexOf("-->", t1 + 8)
                                tollBarText = nazva.substring(t1 + 8, t2)
                                title_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
                                subtitle_toolbar.text = tollBarText
                            }
                        }
                        if (!nazva.contains(tollBarText) && scroll != 0) {
                            if (nazva.contains("nazva+++")) {
                                val t1 = nazva.indexOf("nazva+++")
                                val t2 = nazva.indexOf("-->", t1 + 8)
                                tollBarText = nazva.substring(t1 + 8, t2)
                                title_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
                                subtitle_toolbar.text = tollBarText
                            }
                        }
                        mPosition = position
                        mOffset = offset
                    }
                    onsave = false
                }
            })
        }
        constraint.setOnTouchListener(this)
        if (dzenNoch) progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
        copyBig.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val copyString = java.lang.StringBuilder()
            BibleGlobalList.bibleCopyList.sort()
            BibleGlobalList.bibleCopyList.forEach {
                var textView = maranAta[it]
                textView = textView.replace("+-+", "")
                val t1 = textView.indexOf("$")
                if (t1 != -1)
                    textView = textView.substring(0, t1)
                copyString.append("$textView<br>")
            }
            val clip = ClipData.newPlainText("", MainActivity.fromHtml(copyString.toString()).toString().trim())
            clipboard.setPrimaryClip(clip)
            messageView(getString(by.carkva_gazeta.malitounik.R.string.copy))
            linearLayout4.visibility = View.GONE
            BibleGlobalList.mPedakVisable = false
            BibleGlobalList.bibleCopyList.clear()
            adapter.notifyDataSetChanged()
        }
        adpravit.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyString = java.lang.StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
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
            } else {
                messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        imageView2.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val i = checkPosition(BibleGlobalList.bibleCopyList[0])
                if (i != -1) {
                    if (BibleGlobalList.vydelenie[i][2] == 0) {
                        BibleGlobalList.vydelenie[i][2] = 1
                    } else {
                        BibleGlobalList.vydelenie[i][2] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                    setVydelenie.add(0)
                    setVydelenie.add(1)
                    setVydelenie.add(0)
                    BibleGlobalList.vydelenie.add(setVydelenie)
                }
                linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
            } else {
                messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        imageView3.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val i = checkPosition(BibleGlobalList.bibleCopyList[0])
                if (i != -1) {
                    if (BibleGlobalList.vydelenie[i][3] == 0) {
                        BibleGlobalList.vydelenie[i][3] = 1
                    } else {
                        BibleGlobalList.vydelenie[i][3] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                    setVydelenie.add(0)
                    setVydelenie.add(0)
                    setVydelenie.add(1)
                    BibleGlobalList.vydelenie.add(setVydelenie)
                }
                linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
            } else {
                messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        imageView4.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val i = checkPosition(BibleGlobalList.bibleCopyList[0])
                if (i != -1) {
                    if (BibleGlobalList.vydelenie[i][1] == 0) {
                        BibleGlobalList.vydelenie[i][1] = 1
                    } else {
                        BibleGlobalList.vydelenie[i][1] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                    setVydelenie.add(1)
                    setVydelenie.add(0)
                    setVydelenie.add(0)
                    BibleGlobalList.vydelenie.add(setVydelenie)
                }
                linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
            } else {
                messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        if (dzenNoch) {
            linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            copyBig.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
            adpravit.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
        }
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
        val file: File = if (belarus) File("$filesDir/MaranAtaBel/$cytanne.json") else File("$filesDir/MaranAta/$cytanne.json")
        if (file.exists()) {
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<Int?>?>?>() {}.type
            BibleGlobalList.vydelenie = gson.fromJson(reader.readText(), type)
            inputStream.close()
        }
        if (k.getBoolean("help_str", true)) {
            startActivity(Intent(this, HelpText::class.java))
            val prefEditor: Editor = k.edit()
            prefEditor.putBoolean("help_str", false)
            prefEditor.apply()
        }
        val arrayList = arrayOf(by.carkva_gazeta.malitounik.R.drawable.share_bible, by.carkva_gazeta.malitounik.R.drawable.copy)
        spinnerCopy.adapter = SpinnerImageAdapter(this, arrayList)
        var chekFirst = false
        spinnerCopy.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (savedInstanceState == null && chekFirst) {
                    when (position) {
                        0 -> {
                            if (BibleGlobalList.bibleCopyList.size > 0) {
                                val sendIntent = Intent()
                                sendIntent.action = Intent.ACTION_SEND
                                sendIntent.putExtra(Intent.EXTRA_TEXT, MainActivity.fromHtml(maranAta[BibleGlobalList.bibleCopyList[0]]).toString())
                                sendIntent.type = "text/plain"
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("", MainActivity.fromHtml(maranAta[BibleGlobalList.bibleCopyList[0]]).toString())
                                clipboard.setPrimaryClip(clip)
                                startActivity(Intent.createChooser(sendIntent, null))
                            } else {
                                messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                            }
                        }
                        1 -> {
                            if (BibleGlobalList.bibleCopyList.size > 0) {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("", MainActivity.fromHtml(maranAta[BibleGlobalList.bibleCopyList[0]]).toString())
                                clipboard.setPrimaryClip(clip)
                                messageView(getString(by.carkva_gazeta.malitounik.R.string.copy))
                                linearLayout4.visibility = View.GONE
                                BibleGlobalList.bibleCopyList.clear()
                                BibleGlobalList.mPedakVisable = false
                            } else {
                                messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                            }
                        }
                    }
                }
                chekFirst = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })
        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.run { title_toolbar.setHorizontallyScrolling(true) }
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
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    private fun messageView(message: String) {
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        val layout = LinearLayout(this)
        if (dzenNoch) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
        val density = resources.displayMetrics.density
        val realpadding = (10 * density).toInt()
        val toast = TextViewRobotoCondensed(this)
        toast.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        toast.setPadding(realpadding, realpadding, realpadding, realpadding)
        toast.text = message
        toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        layout.addView(toast)
        val mes = Toast(this)
        mes.duration = Toast.LENGTH_LONG
        mes.view = layout
        mes.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (linearLayout4.visibility == View.VISIBLE) {
            return false
        }
        val heightConstraintLayout = constraint.height
        val widthConstraintLayout = constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val prefEditor: Editor = k.edit()
        val id = v?.id ?: 0
        if (id == R.id.ListView) {
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
            linearLayout4.visibility = View.GONE
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
                            progress.visibility = View.VISIBLE
                            startProcent()
                            MainActivity.checkBrightness = false
                        }
                    }
                    if (x < otstup && y < n && y % 15 == 0) {
                        if (MainActivity.brightness < 100) {
                            MainActivity.brightness = MainActivity.brightness + 1
                            val lp = window.attributes
                            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                            window.attributes = lp
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                            progress.visibility = View.VISIBLE
                            startProcent()
                            MainActivity.checkBrightness = false
                        }
                    }
                    if (x > widthConstraintLayout - otstup && y > n && y % 26 == 0) {
                        if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                            fontBiblia -= 4
                            var min = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) min = " (мін)"
                            progress.text = "${fontBiblia.toInt()} sp$min"
                            progress.visibility = View.VISIBLE
                            startProcent()
                            prefEditor.putFloat("font_biblia", fontBiblia)
                            prefEditor.apply()
                            setFont = true
                            adapter.notifyDataSetChanged()
                        }
                    }
                    if (x > widthConstraintLayout - otstup && y < n && y % 26 == 0) {
                        if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                            fontBiblia += 4
                            var max = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) max = " (макс)"
                            progress.text = "${fontBiblia.toInt()} sp$max"
                            progress.visibility = View.VISIBLE
                            startProcent()
                            prefEditor.putFloat("font_biblia", fontBiblia)
                            prefEditor.apply()
                            setFont = true
                            adapter.notifyDataSetChanged()
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

    @SuppressLint("SetTextI18n")
    private fun setMaranata(cytanne: String) {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val chten = cytanne.split(";").toTypedArray()
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
                if (nazvaFull == "")
                    nazvaFull = bible[1]
                if (nazvaFullBel == "")
                    nazvaFullBel = bible[2]
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
                val r = this@MaranAta.resources
                var inputStream = r.openRawResource(R.raw.biblias1)
                var error = false
                if (belarus) {
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
                        else -> error = true
                    }
                } else {
                    if (nomer == 1) inputStream = r.openRawResource(R.raw.sinaidals1)
                    if (nomer == 2) inputStream = r.openRawResource(R.raw.sinaidals2)
                    if (nomer == 3) inputStream = r.openRawResource(R.raw.sinaidals3)
                    if (nomer == 4) inputStream = r.openRawResource(R.raw.sinaidals4)
                    if (nomer == 5) inputStream = r.openRawResource(R.raw.sinaidals5)
                    if (nomer == 6) inputStream = r.openRawResource(R.raw.sinaidals6)
                    if (nomer == 7) inputStream = r.openRawResource(R.raw.sinaidals7)
                    if (nomer == 8) inputStream = r.openRawResource(R.raw.sinaidals8)
                    if (nomer == 9) inputStream = r.openRawResource(R.raw.sinaidals9)
                    if (nomer == 10) inputStream = r.openRawResource(R.raw.sinaidals10)
                    if (nomer == 11) inputStream = r.openRawResource(R.raw.sinaidals11)
                    if (nomer == 12) inputStream = r.openRawResource(R.raw.sinaidals12)
                    if (nomer == 13) inputStream = r.openRawResource(R.raw.sinaidals13)
                    if (nomer == 14) inputStream = r.openRawResource(R.raw.sinaidals14)
                    if (nomer == 15) inputStream = r.openRawResource(R.raw.sinaidals15)
                    if (nomer == 16) inputStream = r.openRawResource(R.raw.sinaidals16)
                    if (nomer == 17) inputStream = r.openRawResource(R.raw.sinaidals17)
                    if (nomer == 18) inputStream = r.openRawResource(R.raw.sinaidals18)
                    if (nomer == 19) inputStream = r.openRawResource(R.raw.sinaidals19)
                    if (nomer == 20) inputStream = r.openRawResource(R.raw.sinaidals20)
                    if (nomer == 21) inputStream = r.openRawResource(R.raw.sinaidals21)
                    if (nomer == 22) inputStream = r.openRawResource(R.raw.sinaidals22)
                    if (nomer == 23) inputStream = r.openRawResource(R.raw.sinaidals23)
                    if (nomer == 24) inputStream = r.openRawResource(R.raw.sinaidals24)
                    if (nomer == 25) inputStream = r.openRawResource(R.raw.sinaidals25)
                    if (nomer == 26) inputStream = r.openRawResource(R.raw.sinaidals26)
                    if (nomer == 27) inputStream = r.openRawResource(R.raw.sinaidals27)
                    if (nomer == 28) inputStream = r.openRawResource(R.raw.sinaidals28)
                    if (nomer == 29) inputStream = r.openRawResource(R.raw.sinaidals29)
                    if (nomer == 30) inputStream = r.openRawResource(R.raw.sinaidals30)
                    if (nomer == 31) inputStream = r.openRawResource(R.raw.sinaidals31)
                    if (nomer == 32) inputStream = r.openRawResource(R.raw.sinaidals32)
                    if (nomer == 33) inputStream = r.openRawResource(R.raw.sinaidals33)
                    if (nomer == 34) inputStream = r.openRawResource(R.raw.sinaidals34)
                    if (nomer == 35) inputStream = r.openRawResource(R.raw.sinaidals35)
                    if (nomer == 36) inputStream = r.openRawResource(R.raw.sinaidals36)
                    if (nomer == 37) inputStream = r.openRawResource(R.raw.sinaidals37)
                    if (nomer == 38) inputStream = r.openRawResource(R.raw.sinaidals38)
                    if (nomer == 39) inputStream = r.openRawResource(R.raw.sinaidals39)
                    if (nomer == 40) inputStream = r.openRawResource(R.raw.sinaidals40)
                    if (nomer == 41) inputStream = r.openRawResource(R.raw.sinaidals41)
                    if (nomer == 42) inputStream = r.openRawResource(R.raw.sinaidals42)
                    if (nomer == 43) inputStream = r.openRawResource(R.raw.sinaidals43)
                    if (nomer == 44) inputStream = r.openRawResource(R.raw.sinaidals44)
                    if (nomer == 45) inputStream = r.openRawResource(R.raw.sinaidals45)
                    if (nomer == 46) inputStream = r.openRawResource(R.raw.sinaidals46)
                    if (nomer == 47) inputStream = r.openRawResource(R.raw.sinaidals47)
                    if (nomer == 48) inputStream = r.openRawResource(R.raw.sinaidals48)
                    if (nomer == 49) inputStream = r.openRawResource(R.raw.sinaidals49)
                    if (nomer == 50) inputStream = r.openRawResource(R.raw.sinaidals50)
                    if (nomer == 51) inputStream = r.openRawResource(R.raw.sinaidaln1)
                    if (nomer == 52) inputStream = r.openRawResource(R.raw.sinaidaln2)
                    if (nomer == 53) inputStream = r.openRawResource(R.raw.sinaidaln3)
                    if (nomer == 54) inputStream = r.openRawResource(R.raw.sinaidaln4)
                    if (nomer == 55) inputStream = r.openRawResource(R.raw.sinaidaln5)
                    if (nomer == 56) inputStream = r.openRawResource(R.raw.sinaidaln6)
                    if (nomer == 57) inputStream = r.openRawResource(R.raw.sinaidaln7)
                    if (nomer == 58) inputStream = r.openRawResource(R.raw.sinaidaln8)
                    if (nomer == 59) inputStream = r.openRawResource(R.raw.sinaidaln9)
                    if (nomer == 60) inputStream = r.openRawResource(R.raw.sinaidaln10)
                    if (nomer == 61) inputStream = r.openRawResource(R.raw.sinaidaln11)
                    if (nomer == 62) inputStream = r.openRawResource(R.raw.sinaidaln12)
                    if (nomer == 63) inputStream = r.openRawResource(R.raw.sinaidaln13)
                    if (nomer == 64) inputStream = r.openRawResource(R.raw.sinaidaln14)
                    if (nomer == 65) inputStream = r.openRawResource(R.raw.sinaidaln15)
                    if (nomer == 66) inputStream = r.openRawResource(R.raw.sinaidaln16)
                    if (nomer == 67) inputStream = r.openRawResource(R.raw.sinaidaln17)
                    if (nomer == 68) inputStream = r.openRawResource(R.raw.sinaidaln18)
                    if (nomer == 69) inputStream = r.openRawResource(R.raw.sinaidaln19)
                    if (nomer == 70) inputStream = r.openRawResource(R.raw.sinaidaln20)
                    if (nomer == 71) inputStream = r.openRawResource(R.raw.sinaidaln21)
                    if (nomer == 72) inputStream = r.openRawResource(R.raw.sinaidaln22)
                    if (nomer == 73) inputStream = r.openRawResource(R.raw.sinaidaln23)
                    if (nomer == 74) inputStream = r.openRawResource(R.raw.sinaidaln24)
                    if (nomer == 75) inputStream = r.openRawResource(R.raw.sinaidaln25)
                    if (nomer == 76) inputStream = r.openRawResource(R.raw.sinaidaln26)
                    if (nomer == 77) inputStream = r.openRawResource(R.raw.sinaidaln27)
                }
                if (!error) {
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
                    inputStream.close()
                    if (chten.size == 6 && i == 3) {
                        if (belarus) {
                            maranAta.add("<br><em><!--no-->" + resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_be) + "</em><br>\n")
                        } else {
                            maranAta.add("<br><em><!--no-->" + resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_ru) + "</em><br>\n")
                        }
                    }
                    val split2 = builder.toString().split("===").toTypedArray()
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
                            val splitline = split2[e].trim().split("\n").toTypedArray()
                            var i3: Int
                            for (i2 in splitline.indices) {
                                i3 = if (kniga.contains("Сир") && e == 1 && i2 >= 8) i2 - 7 else i2 + 1
                                if (belarus) maranAta.add("<!--" + kniga + "." + e + "." + i3 + "--><!--nazva+++" + nazvaBel + " " + e + "-->" + splitline[i2] + getParallel(nomer, e, i2) + "\n") else maranAta.add("<!--" + kniga + "." + e + "." + i3 + "--><!--nazva+++" + nazva + " " + e + "-->" + splitline[i2] + getParallel(nomer, e, i2) + "\n")
                            }
                        }
                    }
                    if (stixn != -1) {
                        val t1 = fit.indexOf(".")
                        if (belarus) {
                            maranAta.add("<!--no--><!--nazva+++" + nazvaBel + " " + fit.substring(s2 + 1, t1) + "--><br><strong>" + nazvaFullBel + " " + fit.substring(s2 + 1) + "</strong><br>\n")
                        } else {
                            maranAta.add("<!--no--><!--nazva+++" + nazva + " " + fit.substring(s2 + 1, t1) + "--><br><strong>" + nazvaFull + " " + fit.substring(s2 + 1) + "</strong><br>\n")
                        }
                        val res1 = r1.toString().trim().split("\n").toTypedArray()
                        var i2 = 0
                        var i3 = stixn
                        while (i2 < res1.size) {
                            if (belarus) maranAta.add("<!--" + kniga + "." + nachalo + "." + i3 + "--><!--nazva+++" + nazvaBel + " " + fit.substring(s2 + 1, t1) + "-->" + res1[i2] + getParallel(nomer, nachalo, i3 - 1) + "\n") else maranAta.add("<!--" + kniga + "." + nachalo + "." + i3 + "--><!--nazva+++" + nazva + " " + fit.substring(s2 + 1, t1) + "-->" + res1[i2] + getParallel(nomer, nachalo, i3 - 1) + "\n")
                            i2++
                            i3++
                        }
                        if (konec - nachalo != 0) {
                            val res2 = r2.trim().split("\n").toTypedArray()
                            for (i21 in res2.indices) {
                                if (belarus) maranAta.add("<!--" + kniga + "." + konec + "." + (i21 + 1) + "--><!--nazva+++" + nazvaBel + " " + konec + "-->" + res2[i21] + getParallel(nomer, konec, i21) + "\n") else maranAta.add("<!--" + kniga + "." + konec + "." + (i21 + 1) + "--><!--nazva+++" + nazva + " " + konec + "-->" + res2[i21] + getParallel(nomer, konec, i21) + "\n")
                            }
                        }
                    }
                } else {
                    // Только Семуха
                    maranAta.add("<!--no--><br><strong>$nazvaFullBel $fit</strong><br>\n")
                    maranAta.add("<!--no--><em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error) + "</em>\n")
                }
            } catch (t: Throwable) {
                if (belarus) {
                    maranAta.add("<!--no--><br><strong>$nazvaFullBel $fit</strong><br>\n")
                } else {
                    maranAta.add("<!--no--><br><strong>$nazvaFull $fit</strong><br>\n")
                }
                maranAta.add("<!--no-->" + resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch) + "\n")
            }
        }
        adapter.notifyDataSetChanged()
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
                    forceScroll()
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        val firstPosition = ListView.firstVisiblePosition
                        if (firstPosition == INVALID_POSITION) {
                            return@runOnUiThread
                        }
                        val firstView = ListView.getChildAt(0) ?: return@runOnUiThread
                        val newTop = firstView.top - 2
                        ListView.setSelectionFromTop(firstPosition, newTop)
                    }
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        scrollTimer.schedule(scrollerSchedule, spid.toLong(), spid.toLong())
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
            scroll.visibility = View.GONE
            ListView.visibility = View.VISIBLE
            title_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2)
            paralel = false
            invalidateOptionsMenu()
        } else if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else if (BibleGlobalList.mPedakVisable) {
            BibleGlobalList.mPedakVisable = false
            BibleGlobalList.bibleCopyList.clear()
            linearLayout4.visibility = View.GONE
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
        clearEmptyPosition()
        val file: File = if (belarus) {
            File("$filesDir/MaranAtaBel/$cytanne.json")
        } else {
            File("$filesDir/MaranAta/$cytanne.json")
        }
        if (BibleGlobalList.vydelenie.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            val gson = Gson()
            val outputStream = FileWriter(file)
            outputStream.write(gson.toJson(BibleGlobalList.vydelenie))
            outputStream.close()
        }
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        linearLayout4.visibility = View.GONE
        scrollTimer.cancel()
        resetTimer.cancel()
        procentTimer.cancel()
        scrollerSchedule = null
        procentSchedule = null
        resetSchedule = null
    }

    override fun onResume() {
        super.onResume()
        if (fullscreenPage) hide()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            startAutoScroll()
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        if (linearLayout4.visibility == View.GONE) {
            itemAuto.isVisible = true
            mActionDown = false
        } else {
            itemAuto.isVisible = false
        }
        if (autoscroll) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = true
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = true
            itemAuto.title = resources.getString(by.carkva_gazeta.malitounik.R.string.autoScrolloff)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_fullscreen).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            itemAuto.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = false
            itemAuto.title = resources.getString(by.carkva_gazeta.malitounik.R.string.autoScrollon)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_fullscreen).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            itemAuto.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        if (paralel) {
            subtitle_toolbar.visibility = View.GONE
            itemAuto.isVisible = false
        } else {
            subtitle_toolbar.visibility = View.VISIBLE
            itemAuto.isVisible = true
        }
        val spanString = SpannableString(itemAuto.title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString

        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_paralel).isChecked = k.getBoolean("paralel_maranata", true)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_paralel).isVisible = true
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        linearLayout4.visibility = View.GONE
        dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor: Editor = k.edit()
        if (id == android.R.id.home) {
            if (paralel) {
                onBackPressed()
                return true
            }
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
        constraint.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("change", change)
        outState.putString("tollBarText", tollBarText)
        outState.putBoolean("paralel", paralel)
        outState.putInt("paralelPosition", paralelPosition)
        outState.putString("chtenie", subtitle_toolbar.text.toString())
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (BibleGlobalList.mPedakVisable) {
            var find = false
            BibleGlobalList.bibleCopyList.forEach {
                if (it == position)
                    find = true
            }
            if (find) {
                BibleGlobalList.bibleCopyList.remove(position)
            } else {
                BibleGlobalList.bibleCopyList.add(position)
            }
            adapter.notifyDataSetChanged()
        } else {
            BibleGlobalList.bibleCopyList.clear()
            parralelMestaView(position)
            paralelPosition = position
        }
        if (BibleGlobalList.mPedakVisable) {
            if (BibleGlobalList.bibleCopyList.size > 1) {
                copyBig.visibility = View.VISIBLE
                adpravit.visibility = View.VISIBLE
                spinnerCopy.visibility = View.GONE
                imageView2.visibility = View.GONE
                imageView3.visibility = View.GONE
                imageView4.visibility = View.GONE
            } else {
                copyBig.visibility = View.GONE
                adpravit.visibility = View.GONE
                spinnerCopy.visibility = View.VISIBLE
                imageView2.visibility = View.VISIBLE
                imageView3.visibility = View.VISIBLE
                imageView4.visibility = View.VISIBLE
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
                    val biblia = ch.split(".").toTypedArray()
                    conteiner.removeAllViewsInLayout()
                    val arrayList = pm.paralel(this, biblia[0] + " " + biblia[1] + "." + biblia[2], maranAta[position].substring(t1 + 1).trim(), belarus)
                    for (i in arrayList.indices) {
                        conteiner.addView(arrayList[i])
                    }
                    scroll.visibility = View.VISIBLE
                    ListView.visibility = View.GONE
                    title_toolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, biblia[0] + " " + biblia[1] + "." + biblia[2])
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        if (!autoscroll) {
            if (!maranAta[position].contains("<!--no-->") && maranAta[position].trim() != "") {
                if (linearLayout4.visibility == View.GONE) {
                    BibleGlobalList.mPedakVisable = true
                    BibleGlobalList.bibleCopyList.add(position)
                    linearLayout4.visibility = View.VISIBLE
                    copyBig.visibility = View.GONE
                    adpravit.visibility = View.GONE
                    spinnerCopy.visibility = View.VISIBLE
                    imageView2.visibility = View.VISIBLE
                    imageView3.visibility = View.VISIBLE
                    imageView4.visibility = View.VISIBLE
                } else {
                    if (BibleGlobalList.mPedakVisable) {
                        var find = false
                        BibleGlobalList.bibleCopyList.forEach {
                            if (it == position)
                                find = true
                        }
                        if (find) {
                            BibleGlobalList.bibleCopyList.remove(position)
                        } else {
                            BibleGlobalList.bibleCopyList.add(position)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
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

    private inner class MaranAtaListAdaprer(private val activity: Activity) : ArrayAdapter<String?>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_maranata, maranAta as List<String>) {
        override fun isEnabled(position: Int): Boolean {
            return if (maranAta[position].contains("<!--no-->")) false else if (!autoscroll) super.isEnabled(position) else false
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null || setFont) {
                rootView = activity.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_maranata, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }

            var textView = maranAta[position]
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
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
                    if (BibleGlobalList.vydelenie[pos][1] == 1) {
                        if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorYelloy)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (BibleGlobalList.vydelenie[pos][2] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                val spanned = MainActivity.fromHtml(textView)
                end = spanned.length
                ssb = SpannableStringBuilder(spanned)
                val pos = checkPosition(position)
                if (pos != -1) {
                    if (BibleGlobalList.vydelenie[pos][1] == 1) {
                        if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorYelloy)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (BibleGlobalList.vydelenie[pos][2] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            viewHolder.text?.text = ssb
            if (BibleGlobalList.bibleCopyList.size > 0 && BibleGlobalList.bibleCopyList.contains(position) && BibleGlobalList.mPedakVisable) {
                if (dzenNoch) {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark2)
                    viewHolder.text?.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
                } else {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
                }
            } else {
                if (dzenNoch) {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                    viewHolder.text?.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
                } else {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
                }
            }
            return rootView
        }

    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}