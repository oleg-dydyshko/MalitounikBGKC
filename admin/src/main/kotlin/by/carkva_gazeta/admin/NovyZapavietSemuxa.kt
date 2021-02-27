package by.carkva_gazeta.admin

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import by.carkva_gazeta.admin.databinding.AdminBibleBinding
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.SmartFragmentStatePagerAdapter
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import kotlinx.coroutines.*

class NovyZapavietSemuxa : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdel.DialogBibleRazdelListener {
    private var trak = false
    private var fullglav = 0
    private var kniga = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var setedit = false
    private var checkSetDzenNoch = false
    private var title = ""
    private val orientation: Int
        get() = MainActivity.getOrientation(this)
    private lateinit var binding: AdminBibleBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        binding.pager.currentItem = glava
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        if (savedInstanceState != null) {
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
            setedit = savedInstanceState.getBoolean("setedit")
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = AdminBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        kniga = intent.extras?.getInt("kniga", 0) ?: 0
        glava = intent.extras?.getInt("glava", 0) ?: 0
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
            trak = true
        }
        binding.pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        for (i in 0 until binding.pagerTabStrip.childCount) {
            val nextChild = binding.pagerTabStrip.getChildAt(i)
            if (nextChild is TextView) {
                nextChild.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            }
        }
        val adapterViewPager = MyPagerAdapter(supportFragmentManager)
        binding.pager.adapter = adapterViewPager
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (glava != position) fierstPosition = 0
                invalidateOptionsMenu()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        when (kniga) {
            0 -> {
                title = "Паводле Мацьвея"
                fullglav = 28
            }
            1 -> {
                title = "Паводле Марка"
                fullglav = 16
            }
            2 -> {
                title = "Паводле Лукаша"
                fullglav = 24
            }
            3 -> {
                title = "Паводле Яна"
                fullglav = 21
            }
            4 -> {
                title = "Дзеі Апосталаў"
                fullglav = 28
            }
            5 -> {
                title = "Якава"
                fullglav = 5
            }
            6 -> {
                title = "1-е Пятра"
                fullglav = 5
            }
            7 -> {
                title = "2-е Пятра"
                fullglav = 3
            }
            8 -> {
                title = "1-е Яна Багаслова"
                fullglav = 5
            }
            9 -> {
                title = "2-е Яна Багаслова"
                fullglav = 1
            }
            10 -> {
                title = "3-е Яна Багаслова"
                fullglav = 1
            }
            11 -> {
                title = "Юды"
                fullglav = 1
            }
            12 -> {
                title = "Да Рымлянаў"
                fullglav = 16
            }
            13 -> {
                title = "1-е да Карынфянаў"
                fullglav = 16
            }
            14 -> {
                title = "2-е да Карынфянаў"
                fullglav = 13
            }
            15 -> {
                title = "Да Галятаў"
                fullglav = 6
            }
            16 -> {
                title = "Да Эфэсянаў"
                fullglav = 6
            }
            17 -> {
                title = "Да Піліпянаў"
                fullglav = 4
            }
            18 -> {
                title = "Да Каласянаў"
                fullglav = 4
            }
            19 -> {
                title = "1-е да Фесаланікійцаў"
                fullglav = 5
            }
            20 -> {
                title = "2-е да Фесаланікійцаў"
                fullglav = 3
            }
            21 -> {
                title = "1-е да Цімафея"
                fullglav = 6
            }
            22 -> {
                title = "2-е да Цімафея"
                fullglav = 4
            }
            23 -> {
                title = "Да Ціта"
                fullglav = 3
            }
            24 -> {
                title = "Да Філімона"
                fullglav = 1
            }
            25 -> {
                title = "Да Габрэяў"
                fullglav = 13
            }
            26 -> {
                title = "Адкрыцьцё (Апакаліпсіс)"
                fullglav = 22
            }
        }
        //men = VybranoeBibleList.checkVybranoe(this, kniga, glava)
        /*if (savedInstanceState != null) {
            dialog = savedInstanceState.getBoolean("dialog")
            paralel = savedInstanceState.getBoolean("paralel")
            cytanneSours = savedInstanceState.getString("cytanneSours") ?: ""
            cytanneParalelnye = savedInstanceState.getString("cytanneParalelnye") ?: ""
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
        }*/
        binding.pager.currentItem = glava
        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.novy_zapaviet)
        binding.subtitleToolbar.text = title
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

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }*/

    /*override fun onBackPressed() {
        super.onBackPressed()
    }*/

    /*override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_glava) {
            val dialogBibleRazdel = DialogBibleRazdel.getInstance(fullglav)
            dialogBibleRazdel.show(supportFragmentManager, "full_glav")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_bible, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private inner class MyPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            var fullglav = 1
            when (kniga) {
                0, 4 -> fullglav = 28
                1, 13, 12 -> fullglav = 16
                2 -> fullglav = 24
                3 -> fullglav = 21
                5, 19, 8, 6 -> fullglav = 5
                7, 23, 20 -> fullglav = 3
                9, 24, 11, 10 -> {
                }
                14, 25 -> fullglav = 13
                15, 21, 16 -> fullglav = 6
                17, 22, 18 -> fullglav = 4
                26 -> fullglav = 22
            }
            return fullglav
        }

        override fun getItem(position: Int): Fragment {
            for (i in 0 until count) {
                if (position == i) {
                    val pazicia: Int = if (trak) {
                        if (glava != i) 0 else fierstPosition
                    } else 0
                    return NovyZapavietSemuxaFragment.newInstance(i, kniga, pazicia)
                }
            }
            return NovyZapavietSemuxaFragment.newInstance(0, kniga, 1)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (position + 1)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    companion object {
        var fierstPosition = 0
    }
}