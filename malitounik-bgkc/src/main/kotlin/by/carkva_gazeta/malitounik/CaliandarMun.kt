package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.CalendarBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import java.util.*

class CaliandarMun : BaseActivity(), CaliandarMunTab1.CaliandarMunTab1Listener, CaliandarMunTab2.CaliandarMunTab2Listener, DialogCaliandarMunDate.DialogCaliandarMunDateListener {
    private var yearG1 = 0
    private var posMun1 = 0
    private var day1 = 0
    private var yearG2 = 0
    private var posMun2 = 0
    private var day2 = 0
    private val dzenNoch get() = getBaseDzenNoch()
    private lateinit var chin: SharedPreferences
    private var getData = false
    private lateinit var binding: CalendarBinding
    private var resetTollbarJob: Job? = null

    override fun setDataCalendar(dataCalendar: Int) {
        val fragment = supportFragmentManager.findFragmentByTag("mun") as? CaliandarMunTab1
        fragment?.setDataCalendar(dataCalendar)
    }

    override fun setDayAndMun1(day: Int, mun: Int, year: Int) {
        day1 = day
        posMun1 = mun
        yearG1 = year
    }

    override fun setDayAndMun2(day: Int, mun: Int, year: Int, cviatyGlavnyia: String) {
        day2 = day
        posMun2 = mun
        yearG2 = year
        if (!cviatyGlavnyia.contains("no_sviaty")) {
            binding.subtitleToolbar.text = MainActivity.fromHtml(cviatyGlavnyia)
            binding.subtitleToolbar.visibility = View.VISIBLE
        } else {
            binding.subtitleToolbar.visibility = View.GONE
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

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBack() {
        super.onBack()
        SabytieOnView = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = CalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.subtitleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        getData = intent.getBooleanExtra("getData", false)
        if (getData) binding.titleToolbar.setText(R.string.get_date)
        else binding.titleToolbar.setText(R.string.kaliandar)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        val c = Calendar.getInstance()
        if (savedInstanceState != null) {
            day1 = savedInstanceState.getInt("day")
            posMun1 = savedInstanceState.getInt("mun")
            yearG1 = savedInstanceState.getInt("year")
            day2 = savedInstanceState.getInt("day2")
            posMun2 = savedInstanceState.getInt("mun2")
            yearG2 = savedInstanceState.getInt("year2")
        } else {
            posMun1 = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
            yearG1 = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
            day1 = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
            posMun2 = posMun1
            if (yearG1 > SettingsActivity.GET_CALIANDAR_YEAR_MAX) yearG1 = SettingsActivity.GET_CALIANDAR_YEAR_MAX
            yearG2 = yearG1
            day2 = day1
        }
        val nedelia = chin.getInt("nedelia", 0)
        binding.tabLayout.getTabAt(nedelia)?.select()
        if (nedelia == 0) {
            replaceFragment(CaliandarMunTab1.getInstance(posMun1, yearG1, day1))
            binding.subtitleToolbar.visibility = View.GONE
        } else {
            replaceFragment(CaliandarMunTab2.getInstance(posMun2, yearG2, day2))
            binding.subtitleToolbar.visibility = View.VISIBLE
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                val editor = chin.edit()
                editor.putInt("nedelia", position)
                editor.apply()
                if (position == 0) {
                    replaceFragment(CaliandarMunTab1.getInstance(posMun1, yearG1, day1))
                    binding.subtitleToolbar.visibility = View.GONE
                } else {
                    replaceFragment(CaliandarMunTab2.getInstance(posMun2, yearG2, day2))
                    binding.subtitleToolbar.visibility = View.VISIBLE
                }
                invalidateOptionsMenu()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val pos = chin.getInt("nedelia", 0)
        val tag = if (pos == 0) "mun"
        else "niadzelia"
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainer.id, fragment, tag)
        transaction.commit()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.caliandar_mun, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val pos = chin.getInt("nedelia", 0)
        if (MainActivity.padzeia.size > 0 && pos == 0 && !getData) {
            menu.findItem(R.id.action_padzeia).isVisible = true
            if (SabytieOnView) {
                if (dzenNoch) menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_black_on) else menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_on)
            } else {
                if (dzenNoch) menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_black_off) else menu.findItem(R.id.action_padzeia).setIcon(R.drawable.calendar_padzea_off)
            }
        }
        if (pos == 1) {
            menu.findItem(R.id.action_left).isVisible = true
            menu.findItem(R.id.action_right).isVisible = true
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("day", day1)
        outState.putInt("mun", posMun1)
        outState.putInt("year", yearG1)
        outState.putInt("day2", day2)
        outState.putInt("mun2", posMun2)
        outState.putInt("year2", yearG1)
    }

    companion object {
        var SabytieOnView = false
    }
}