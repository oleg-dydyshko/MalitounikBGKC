package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.calendar.*
import java.util.*

/**
 * Created by oleg on 3.7.16
 */
class CaliandarMun : AppCompatActivity() {
    private var yearG = 0
    private var posMun = 0
    private var day = 0
    private lateinit var c: GregorianCalendar
    private var dzenNoch = false
    private lateinit var chin: SharedPreferences
    private var sabytue = false

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        SabytieOnView = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val titleToolbar: TextViewRobotoCondensed = findViewById(R.id.title_toolbar)
        titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        sabytue = intent.getBooleanExtra("sabytie", false)
        if (sabytue) titleToolbar.setText(R.string.get_date) else titleToolbar.setText(R.string.kaliandar)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
        c = Calendar.getInstance() as GregorianCalendar
        posMun = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
        yearG = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        if (yearG > SettingsActivity.GET_CALIANDAR_YEAR_MAX) yearG = SettingsActivity.GET_CALIANDAR_YEAR_MAX
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        CaliandarNedzel.setDenNedeli = true
        tabPager.adapter = MyTabPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(tabPager)
        tabPager.currentItem = chin.getInt("nedelia", 0)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                tabPager.currentItem = position
                val editor = chin.edit()
                editor.putInt("nedelia", position)
                editor.apply()
                invalidateOptionsMenu()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.caliandar_mun, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val pos = chin.getInt("nedelia", 0)
        if (MainActivity.padzeia.size > 0 && pos == 0 && !sabytue) {
            menu.findItem(R.id.action_padzeia).isVisible = true
            if (SabytieOnView) {
                if (dzenNoch) menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_black_on) else menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_on)
            } else {
                if (dzenNoch) menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_black_off) else menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_off)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MyTabPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.mun)
            else getString(R.string.niadzelia)
        }

        override fun getItem(position: Int): Fragment {
            return if (position == 0) {
                CaliandarMunTab1.getInstance(posMun, yearG, day)
            } else {
                CaliandarMunTab2.getInstance(posMun, yearG, day)
            }
        }

        override fun getItemPosition(ob: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    companion object {
        var SabytieOnView = false
    }
}