package by.carkva_gazeta.admin

import android.content.Context
import android.content.SharedPreferences
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
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.SmartFragmentStatePagerAdapter
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import kotlinx.coroutines.*

class StaryZapavietSemuxa : AppCompatActivity(), DialogBibleRazdel.DialogBibleRazdelListener {
    private var trak = false
    private var fullglav = 0
    private var kniga = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var setedit = false
    private var checkSetDzenNoch = false
    private var title = ""
    private lateinit var binding: AdminBibleBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
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
        val adapterViewPager: SmartFragmentStatePagerAdapter = MyPagerAdapter(supportFragmentManager)
        binding.pager.adapter = adapterViewPager
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (glava != position) NovyZapavietSemuxa.fierstPosition = 0
                invalidateOptionsMenu()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        when (kniga) {
            0 -> {
                title = "Быцьцё"
                fullglav = 50
            }
            1 -> {
                title = "Выхад"
                fullglav = 40
            }
            2 -> {
                title = "Лявіт"
                fullglav = 27
            }
            3 -> {
                title = "Лікі"
                fullglav = 36
            }
            4 -> {
                title = "Другі Закон"
                fullglav = 34
            }
            5 -> {
                title = "Ісуса сына Нава"
                fullglav = 24
            }
            6 -> {
                title = "Судзьдзяў"
                fullglav = 21
            }
            7 -> {
                title = "Рут"
                fullglav = 4
            }
            8 -> {
                title = "1-я Царстваў"
                fullglav = 31
            }
            9 -> {
                title = "2-я Царстваў"
                fullglav = 24
            }
            10 -> {
                title = "3-я Царстваў"
                fullglav = 22
            }
            11 -> {
                title = "4-я Царстваў"
                fullglav = 25
            }
            12 -> {
                title = "1-я Летапісаў"
                fullglav = 29
            }
            13 -> {
                title = "2-я Летапісаў"
                fullglav = 36
            }
            14 -> {
                title = "Эздры"
                fullglav = 10
            }
            15 -> {
                title = "Нээміі"
                fullglav = 13
            }
            16 -> {
                title = "Эстэр"
                fullglav = 10
            }
            17 -> {
                title = "Ёва"
                fullglav = 42
            }
            18 -> {
                title = "Псалтыр"
                fullglav = 151
            }
            19 -> {
                title = "Выслоўяў Саламонавых"
                fullglav = 31
            }
            20 -> {
                title = "Эклезіяста"
                fullglav = 12
            }
            21 -> {
                title = "Найвышэйшая Песьня Саламонава"
                fullglav = 8
            }
            22 -> {
                title = "Ісаі"
                fullglav = 66
            }
            23 -> {
                title = "Ераміі"
                fullglav = 52
            }
            24 -> {
                title = "Ераміін Плач"
                fullglav = 5
            }
            25 -> {
                title = "Езэкііля"
                fullglav = 48
            }
            26 -> {
                title = "Данііла"
                fullglav = 12
            }
            27 -> {
                title = "Асіі"
                fullglav = 14
            }
            28 -> {
                title = "Ёіля"
                fullglav = 3
            }
            29 -> {
                title = "Амоса"
                fullglav = 9
            }
            30 -> {
                title = "Аўдзея"
                fullglav = 1
            }
            31 -> {
                title = "Ёны"
                fullglav = 4
            }
            32 -> {
                title = "Міхея"
                fullglav = 7
            }
            33 -> {
                title = "Навума"
                fullglav = 3
            }
            34 -> {
                title = "Абакума"
                fullglav = 3
            }
            35 -> {
                title = "Сафона"
                fullglav = 3
            }
            36 -> {
                title = "Агея"
                fullglav = 2
            }
            37 -> {
                title = "Захарыі"
                fullglav = 14
            }
            38 -> {
                title = "Малахіі"
                fullglav = 4
            }
        }
        binding.pager.currentItem = glava
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
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.stary_zapaviet)
        binding.subtitleToolbar.text = title
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
                0 -> fullglav = 50
                1 -> fullglav = 40
                2 -> fullglav = 27
                3, 13 -> fullglav = 36
                4 -> fullglav = 34
                5, 9 -> fullglav = 24
                6 -> fullglav = 21
                7 -> fullglav = 4
                8 -> fullglav = 31
                10 -> fullglav = 22
                11 -> fullglav = 25
                12 -> fullglav = 29
                14 -> fullglav = 10
                15 -> fullglav = 13
                16 -> fullglav = 10
                17 -> fullglav = 42
                18 -> fullglav = 151
                19 -> fullglav = 31
                20 -> fullglav = 12
                21 -> fullglav = 8
                22 -> fullglav = 66
                23 -> fullglav = 52
                24 -> fullglav = 5
                25 -> fullglav = 48
                26 -> fullglav = 12
                27 -> fullglav = 14
                28 -> fullglav = 3
                29 -> fullglav = 9
                30 -> {
                }
                31 -> fullglav = 4
                32 -> fullglav = 7
                33 -> fullglav = 3
                34 -> fullglav = 3
                35 -> fullglav = 3
                36 -> fullglav = 2
                37 -> fullglav = 14
                38 -> fullglav = 4
            }
            return fullglav
        }

        override fun getItem(position: Int): Fragment {
            for (i in 0 until count) {
                if (position == i) {
                    val pazicia: Int = if (trak) {
                        if (glava != i) 0 else fierstPosition
                    } else 0
                    return StaryZapavietSemuxaFragment.newInstance(i, kniga, pazicia)
                }
            }
            return StaryZapavietSemuxaFragment.newInstance(0, kniga, 1)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return if (kniga == 18) resources.getString(by.carkva_gazeta.malitounik.R.string.psalom) + " " + (position + 1) else resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (position + 1)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    companion object {
        var fierstPosition = 0
    }
}