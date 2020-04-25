package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.resources.DialogBibleRazdel.Companion.getInstance
import by.carkva_gazeta.resources.DialogBibleRazdel.DialogBibleRazdelListener
import by.carkva_gazeta.resources.NadsanContentPage.Companion.newInstance
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_bible.*

class NadsanContentActivity : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, NadsanContentPage.ListPosition {
    private val mHideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        linealLayoutTitle.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
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
    private var trak = false
    private var fullglav = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var dialog = true
    private var checkSetDzenNoch = false
    private val orientation: Int
        get() {
            val rotation = windowManager.defaultDisplay.rotation
            val displayOrientation = resources.configuration.orientation
            if (displayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                return if (rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_180) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_90) return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    override fun onPause() {
        super.onPause()
        val prefEditors = k.edit()
        val set = ArrayMap<String, Int>()
        set["glava"] = pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("psalter_time_psalter_nadsan", gson.toJson(set))
        prefEditors.apply()
    }

    override fun onDialogFontSizePositiveClick() {
        pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        pager.currentItem = glava
    }

    override fun getListPosition(position: Int) {
        fierstPosition = position
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bible)
        glava = if (intent.extras?.containsKey("kafizma") == true) {
            setKafizma(intent.extras?.getInt("kafizma", 1) ?: 1)
        } else {
            intent.extras?.getInt("glava", 0) ?: 0
        }
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
            trak = true
        }
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        for (i in 0 until pagerTabStrip.childCount) {
            val nextChild = pagerTabStrip.getChildAt(i)
            if (nextChild is TextView) {
                nextChild.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            }
        }
        val adapterViewPager: SmartFragmentStatePagerAdapter = MyPagerAdapter(supportFragmentManager)
        pager.adapter = adapterViewPager
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
        title_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.psalter)
        subtitle_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.kafizma2) + " " + getKafizma(glava)
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (glava != position) fierstPosition = 0
                subtitle_toolbar.text = getString(by.carkva_gazeta.malitounik.R.string.kafizma2) + " " + getKafizma(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        fullglav = 151
        if (savedInstanceState != null) {
            dialog = savedInstanceState.getBoolean("dialog")
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
        }
        pager.currentItem = glava
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
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            title_toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            title_toolbar.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        }
    }

    private fun getKafizma(psalter: Int): Int {
        var psalter1 = psalter
        psalter1++
        var kafizma = 1
        if (psalter1 in 9..16) kafizma = 2
        if (psalter1 in 17..23) kafizma = 3
        if (psalter1 in 24..31) kafizma = 4
        if (psalter1 in 32..36) kafizma = 5
        if (psalter1 in 37..45) kafizma = 6
        if (psalter1 in 46..54) kafizma = 7
        if (psalter1 in 55..63) kafizma = 8
        if (psalter1 in 64..69) kafizma = 9
        if (psalter1 in 70..76) kafizma = 10
        if (psalter1 in 77..84) kafizma = 11
        if (psalter1 in 85..90) kafizma = 12
        if (psalter1 in 91..100) kafizma = 13
        if (psalter1 in 101..104) kafizma = 14
        if (psalter1 in 105..108) kafizma = 15
        if (psalter1 in 109..117) kafizma = 16
        if (psalter1 == 118) kafizma = 17
        if (psalter1 in 119..133) kafizma = 18
        if (psalter1 in 134..142) kafizma = 19
        if (psalter1 in 143..151) kafizma = 20
        return kafizma
    }

    private fun setKafizma(kafizma: Int): Int {
        var glava = 1
        when (kafizma) {
            2 -> glava = 9
            3 -> glava = 17
            4 -> glava = 24
            5 -> glava = 32
            6 -> glava = 37
            7 -> glava = 46
            8 -> glava = 55
            9 -> glava = 64
            10 -> glava = 70
            11 -> glava = 77
            12 -> glava = 85
            13 -> glava = 91
            14 -> glava = 101
            15 -> glava = 105
            16 -> glava = 109
            17 -> glava = 118
            18 -> glava = 119
            19 -> glava = 134
            20 -> glava = 143
        }
        glava--
        return glava
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("dialog", dialog)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("checkSetDzenNoch", checkSetDzenNoch)
    }

    override fun onBackPressed() {
        when {
            fullscreenPage -> {
                fullscreenPage = false
                show()
            }
            BibleGlobalList.mPedakVisable -> {
                val adapter = pager.adapter as MyPagerAdapter
                val fragment = adapter.getFragment(pager.currentItem) as BackPressedFragment
                fragment.onBackPressedFragment()
            }
            checkSetDzenNoch -> {
                onSupportNavigateUp()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_glava).isVisible = true
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditors = k.edit()
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            checkSetDzenNoch = true
            val prefEditor = k.edit()
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
                prefEditors.putBoolean("orientation", true)
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                prefEditors.putBoolean("orientation", false)
            }
        }
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_glava) {
            val dialogBibleRazdel = getInstance(fullglav)
            dialogBibleRazdel.show(supportFragmentManager, "full_glav")
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
        prefEditors.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.biblia, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        linealLayoutTitle.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private inner class MyPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            return 151
        }

        override fun getItem(position: Int): Fragment {
            val pazicia: Int = if (trak) {
                if (glava != position) 0
                else fierstPosition
            } else 0
            return newInstance(position, pazicia)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getString(by.carkva_gazeta.malitounik.R.string.psalom2) + " " + (position + 1)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
        var fierstPosition = 0
    }
}