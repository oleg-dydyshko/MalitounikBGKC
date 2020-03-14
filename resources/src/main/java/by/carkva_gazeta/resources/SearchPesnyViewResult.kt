package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.resources.R.raw
import kotlinx.android.synthetic.main.akafist_under.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class SearchPesnyViewResult : AppCompatActivity(), OnTouchListener, DialogFontSizeListener {
    private val mHideHandler = Handler()
    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        scrollView2.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
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
    private var n = 0
    private var title = ""
    private var men = false
    private var levo = false
    private var pravo = false
    private var resurs = ""
    private var resultat = ""
    private var procentTimer = Timer()
    private var procentSchedule: TimerTask? = null
    private val orientation: Int
        get() {
            val rotation = windowManager.defaultDisplay.rotation
            val displayOrientation = resources.configuration.orientation
            if (displayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                return if (rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_180) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_90) return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onDialogFontSizePositiveClick() {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
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
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.akafist_under)
        constraint.setOnTouchListener(this)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
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
        if (dzenNoch) {
            TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
            progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
        }
        TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        resultat = intent.extras?.getString("resultat") ?: ""
        if (!SearchPesny.menuListDataIsInitialized())
            SearchPesny.menuListData = SearchPesny.getMenuListData(this)
        for (i in SearchPesny.menuListData.indices) {
            if (SearchPesny.menuListData[i].data.toLowerCase(Locale.getDefault()).contains(resultat.toLowerCase(Locale.getDefault()))) {
                val fields = raw::class.java.fields
                for (field in fields) {
                    if (field.getInt(null) == SearchPesny.menuListData[i].id) {
                        resurs = field.name
                        break
                    }
                }
                val inputStream = resources.openRawResource(SearchPesny.menuListData[i].id)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                val builder = StringBuilder()
                reader.forEachLine {
                    line = it
                    if (dzenNoch) line = line.replace("#d00505", "#f44336")
                    builder.append(line)
                }
                inputStream.close()
                title = SearchPesny.menuListData[i].data
                TextView.text = MainActivity.fromHtml(builder.toString())
                break
            }
        }
        men = Bogashlugbovya.checkVybranoe(this, resurs)
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
        if (resultat == "")
            title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.search)
        else
            title_toolbar.text = resultat
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
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

    @SuppressLint("SetTextI18n")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
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
                            TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
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
                            TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                            var max = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) max = " (макс)"
                            progress.text = "${fontBiblia.toInt()} sp$max"
                            progress.visibility = View.VISIBLE
                            startProcent()
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
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (levo) {
                        levo = false
                    }
                    if (pravo) {
                        pravo = false
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).isVisible = false
        if (men) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isVisible = false
        val item = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        val spanString = SpannableString(menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        item.title = spanString
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.akafist, menu)
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
        val prefEditor = k.edit()
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            men = Bogashlugbovya.setVybranoe(this, resurs, title)
            if (men) {
                val layout = LinearLayout(this)
                layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
                val density = resources.displayMetrics.density
                val realpadding = (10 * density).toInt()
                val toast = TextViewRobotoCondensed(this)
                toast.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
                toast.setPadding(realpadding, realpadding, realpadding, realpadding)
                toast.text = getString(by.carkva_gazeta.malitounik.R.string.addVybranoe)
                toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                layout.addView(toast)
                val mes = Toast(this)
                mes.duration = Toast.LENGTH_SHORT
                mes.view = layout
                mes.show()
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
            sendIntent.putExtra(Intent.EXTRA_TEXT, TextView.text.toString())
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, "Падзяліцьца..."))
        }
        prefEditor.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else {
            super.onBackPressed()
        }
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        scrollView2.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}