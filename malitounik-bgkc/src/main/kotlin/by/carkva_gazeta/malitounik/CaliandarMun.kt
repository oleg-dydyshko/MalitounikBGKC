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
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.CalendarBinding
import com.google.android.material.tabs.TabLayout
import java.util.*

class CaliandarMun : AppCompatActivity(), CaliandarMunTab1.CaliandarMunTab1Listener, CaliandarMunTab2.CaliandarMunTab2Listener {
    private var yearG1 = 0
    private var posMun1 = 0
    private var day1 = 0
    private var yearG2 = 0
    private var posMun2 = 0
    private var day2 = 0
    private var dzenNoch = false
    private lateinit var chin: SharedPreferences
    private var sabytue = false
    private lateinit var binding: CalendarBinding

    override fun setDayAndMun1(day: Int, mun: Int, year: Int) {
        day1 = day
        posMun1 = mun
        yearG1 = year
    }

    override fun setDayAndMun2(day: Int, mun: Int, year: Int) {
        day2 = day
        posMun2 = mun
        yearG2 = year
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        super.onCreate(savedInstanceState)
        binding = CalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        sabytue = intent.getBooleanExtra("sabytie", false)
        if (sabytue) binding.titleToolbar.setText(R.string.get_date) else binding.titleToolbar.setText(R.string.kaliandar)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        val c = Calendar.getInstance() as GregorianCalendar
        posMun1 = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
        posMun2 = posMun1
        yearG1 = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        if (yearG1 > SettingsActivity.GET_CALIANDAR_YEAR_MAX) yearG1 = SettingsActivity.GET_CALIANDAR_YEAR_MAX
        yearG2 = yearG1
        day1 = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        day2 = day1
        CaliandarNedzel.setDenNedeli = true
        val nedelia = chin.getInt("nedelia", 0)
        binding.tabLayout.getTabAt(nedelia)?.select()
        if (nedelia == 0) {
            replaceFragment(CaliandarMunTab1.getInstance(posMun1, yearG1, day1))
        } else {
            replaceFragment(CaliandarMunTab2.getInstance(posMun2, yearG2, day2))
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                if (position == 0) {
                    replaceFragment(CaliandarMunTab1.getInstance(posMun1, yearG1, day1))
                } else {
                    replaceFragment(CaliandarMunTab2.getInstance(posMun2, yearG2, day2))
                }
                val editor = chin.edit()
                editor.putInt("nedelia", position)
                editor.apply()
                invalidateOptionsMenu()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainer.id, fragment)
        transaction.commit()
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

    companion object {
        var SabytieOnView = false
    }
}