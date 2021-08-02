package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.resources.DialogBibleRazdel.Companion.getInstance
import by.carkva_gazeta.resources.DialogBibleRazdel.DialogBibleRazdelListener
import by.carkva_gazeta.resources.databinding.ActivityBibleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.File

class NadsanContentActivity : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, NadsanContentPage.ListPosition {

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
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var dialog = true
    private var checkSetDzenNoch = false
    private var men = true
    private lateinit var binding: ActivityBibleBinding
    private var resetTollbarJob: Job? = null
    private var bibliaKnigi = ArrayList<BibliaData>()

    override fun onPause() {
        super.onPause()
        val prefEditors = k.edit()
        val set = ArrayMap<String, Int>()
        set["glava"] = binding.pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("psalter_time_psalter_nadsan", gson.toJson(set))
        prefEditors.apply()
        resetTollbarJob?.cancel()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        binding.pager.setCurrentItem(glava, false)
    }

    override fun getListPosition(position: Int) {
        fierstPosition = position
    }

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
        binding = ActivityBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        glava = if (intent.extras?.containsKey("kafizma") == true) {
            setKafizma(intent.extras?.getInt("kafizma", 1) ?: 1)
        } else {
            intent.extras?.getInt("glava", 0) ?: 0
        }
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
        }
        for (i in 0 until 151) {
            val pazicia = if (glava != i) 0 else fierstPosition
            bibliaKnigi.add(BibliaData(i, 22, pazicia))
        }
        binding.pager.adapter = MyPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = resources.getString(by.carkva_gazeta.malitounik.R.string.psalom2) + " " + (position + 1)
        }.attach()
        binding.pager.offscreenPageLimit = 3
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.psalter)
        binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.kafizma2, getKafizma(glava))
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (glava != position) fierstPosition = 0
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.kafizma2, getKafizma(position))
                men = VybranoeBibleList.checkVybranoe(this@NadsanContentActivity, 0, position, 3)
                invalidateOptionsMenu()
            }
        })
        men = VybranoeBibleList.checkVybranoe(this, 0, glava, 3)
        if (savedInstanceState != null) {
            dialog = savedInstanceState.getBoolean("dialog")
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
        }
        binding.pager.setCurrentItem(glava, false)
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
                val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as BackPressedFragment
                fragment.onBackPressedFragment()
            }
            checkSetDzenNoch -> {
                onSupportNavigateUp()
            }
            MenuBibleSemuxa.bible_time -> {
                MenuBibleSemuxa.bible_time = false
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
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        val itemVybranoe: MenuItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditors = k.edit()
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            checkSetDzenNoch = true
            men = VybranoeBibleList.setVybranoe(this, resources.getString(by.carkva_gazeta.malitounik.R.string.psalom2), 0, binding.pager.currentItem, bibleName = 3)
            if (men) {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.addVybranoe))
                if (!VybranoeBibleList.checkVybranoe("3")) {
                    MenuVybranoe.vybranoe.add(0, VybranoeData(Bogashlugbovya.vybranoeIndex(), "3", getString(by.carkva_gazeta.malitounik.R.string.title_psalter)))
                    val gson = Gson()
                    val file = File("$filesDir/Vybranoe.json")
                    file.writer().use {
                        it.write(gson.toJson(MenuVybranoe.vybranoe))
                    }
                }
            }
            invalidateOptionsMenu()
        }
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
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_glava) {
            val dialogBibleRazdel = getInstance(151)
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val inputStream = resources.openRawResource(R.raw.nadsan_psaltyr)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", "nadsan_psaltyr")
                intent.putExtra("title", getString(by.carkva_gazeta.malitounik.R.string.title_psalter))
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
            }
        }
        prefEditors.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        CoroutineScope(Dispatchers.Main).launch {
            mHidePart2Runnable()
        }
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
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? NadsanContentPage
            fragment?.upDateListView() ?: super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount() = 151

        override fun createFragment(position: Int) = NadsanContentPage.newInstance(bibliaKnigi[position].glava, bibliaKnigi[position].styx)
    }

    companion object {
        var fierstPosition = 0
    }
}