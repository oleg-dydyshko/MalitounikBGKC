package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
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
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TabHost.TabSpec
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.calendar.*
import java.util.*

/**
 * Created by oleg on 3.7.16
 */
class CaliandarMun : AppCompatActivity() {
    private lateinit var adapterViewPager: SmartFragmentStatePagerAdapter
    private lateinit var adapterViewPagerNedel: SmartFragmentStatePagerAdapter
    private var yearG = 0
    private var posMun = 0
    private var day = 0
    private lateinit var c: GregorianCalendar
    private lateinit var names: Array<String?>
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

    @SuppressLint("SetTextI18n")
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
        imageButton.setOnClickListener { pagerNedel.currentItem = pagerNedel.currentItem - 1 }
        imageButton2.setOnClickListener { pagerNedel.currentItem = pagerNedel.currentItem + 1 }
        if (dzenNoch) {
            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.levo_catedra))
            imageButton2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pravo_catedra))
            nedelName.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        CaliandarNedzel.setDenNedeli = true
        val tabHost: TabHost = findViewById(android.R.id.tabhost)
        tabHost.setup()
        var tabSpec: TabSpec
        tabSpec = tabHost.newTabSpec("tag1")
        tabSpec.setIndicator("Месяц")
        tabSpec.setContent(R.id.tvTab1)
        tabHost.addTab(tabSpec)
        tabSpec = tabHost.newTabSpec("tag2")
        tabSpec.setIndicator("Тыдзень")
        tabSpec.setContent(R.id.tvTab2)
        tabHost.addTab(tabSpec)
        tabHost.setOnTabChangedListener { tabId: String ->
            val editor = chin.edit()
            if (tabId.contains("tag1")) editor.putInt("nedelia", 0) else editor.putInt("nedelia", 1)
            editor.apply()
            invalidateOptionsMenu()
        }
        val data2 = ArrayList<String>()
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
            data2.add(i.toString())
        }
        names = resources.getStringArray(R.array.mun_array2)
        val adapter = CaliandarMunAdapter(this, names)
        spinner.adapter = adapter
        val adapter2 = CaliandarMunAdapter(this, data2)
        spinner2.adapter = adapter2
        val pos = chin.getInt("nedelia", 0)
        nedel.setVerticalGravity(View.VISIBLE)
        if (pos == 1) {
            tabHost.setCurrentTabByTag("tag2")
        } else {
            tabHost.setCurrentTabByTag("tag1")
        }
        adapterViewPager = MyPagerAdapter(supportFragmentManager)
        pager.adapter = adapterViewPager
        c = Calendar.getInstance() as GregorianCalendar
        adapterViewPagerNedel = MyCalendarNedelAdapter(supportFragmentManager)
        pagerNedel.adapter = adapterViewPagerNedel
        var dayyear = 0
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until yearG) {
            dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
        }
        var count2 = 0
        var dayyear2 = 0
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
            dayyear2 = if (c.isLeapYear(i)) 366 + dayyear2 else 365 + dayyear2
        }
        val calendarStart = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
        calendarStart.firstDayOfWeek = Calendar.SUNDAY
        var ost = calendarStart[Calendar.DAY_OF_WEEK]
        if (ost == 1) ost = 8
        for (i in 0..6) {
            if (calendarStart[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) break
            calendarStart.add(Calendar.DATE, 1)
        }
        val c2 = GregorianCalendar(yearG, posMun, day)
        c2.firstDayOfWeek = Calendar.SUNDAY
        val calendarEnd = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MAX, 11, 31)
        calendarEnd.firstDayOfWeek = Calendar.SUNDAY
        for (i in 7 downTo 1) {
            if (calendarEnd[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) break
            calendarEnd.add(Calendar.DATE, -1)
        }
        when {
            calendarEnd.timeInMillis <= c2.timeInMillis -> {
                for (e in 0 until dayyear2) {
                    var dayFull = dayyear + calendarEnd[Calendar.DAY_OF_YEAR] - 7 - (8 - ost)
                    if (dayFull < 0) dayFull = 0
                    if (e == dayFull) {
                        pagerNedel.currentItem = count2
                        nedelName.text = calendarEnd[Calendar.WEEK_OF_YEAR].toString() + " тыдзень"
                    }
                    if (e % 7 == 0) {
                        count2++
                    }
                }
            }
            calendarStart.timeInMillis <= c2.timeInMillis -> {
                for (e in 0 until dayyear2) {
                    var dayFull = dayyear + c2[Calendar.DAY_OF_YEAR] - 7 - (8 - ost)
                    if (dayFull < 0) dayFull = 0
                    if (e == dayFull) {
                        pagerNedel.currentItem = count2
                        nedelName.text = c2[Calendar.WEEK_OF_YEAR].toString() + " тыдзень"
                    }
                    if (e % 7 == 0) {
                        count2++
                    }
                }
            }
            else -> {
                for (e in 0 until dayyear2) {
                    var dayFull = dayyear + calendarStart[Calendar.DAY_OF_YEAR] - 7 - (8 - ost)
                    if (dayFull < 0) dayFull = 0
                    if (e == dayFull) {
                        pagerNedel.currentItem = count2
                        nedelName.text = calendarStart[Calendar.WEEK_OF_YEAR].toString() + " тыдзень"
                    }
                    if (e % 7 == 0) {
                        count2++
                    }
                }
            }
        }
        val son = (yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + posMun
        val pagepos = pager.currentItem
        if (pagepos != son) {
            pager.currentItem = son
        }
        if (adapterViewPagerNedel.count - 1 == pagerNedel.currentItem) imageButton2.visibility = View.GONE
        if (pagerNedel.currentItem == 0) imageButton.visibility = View.GONE
        spinner.setSelection(posMun)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val son1 = (yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + position
                posMun = position
                val pagepos1 = pager.currentItem
                if (pagepos1 != son1) {
                    pager.currentItem = son1
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
        spinner2.setSelection(yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN)
        spinner2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                yearG = (parent.selectedItem as String).toInt()
                val son1 = (yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + posMun
                val pagepos1 = pager.currentItem
                if (pagepos1 != son1) {
                    pager.currentItem = son1
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                for (i in 0 until adapterViewPager.count) {
                    if (position == i) {
                        var r = SettingsActivity.GET_CALIANDAR_YEAR_MIN
                        var t = 0
                        for (s in 0..c[Calendar.YEAR] - SettingsActivity.GET_CALIANDAR_YEAR_MIN + 2) {
                            for (w in 0..11) {
                                if (i == t) {
                                    yearG = r
                                    posMun = w
                                }
                                t++
                            }
                            r++
                        }
                        spinner.setSelection(posMun)
                        spinner2.setSelection(yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        pagerNedel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val start = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
                start.firstDayOfWeek = Calendar.SUNDAY
                for (i in 0..6) {
                    if (start[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) break
                    start.add(Calendar.DATE, 1)
                }
                var ned = start[Calendar.WEEK_OF_YEAR]
                for (i in 0 until adapterViewPagerNedel.count) {
                    if (position == i) {
                        nedelName.text = "$ned тыдзень"
                    }
                    start.add(Calendar.DATE, 7)
                    ned = start[Calendar.WEEK_OF_YEAR]
                }
                if (adapterViewPagerNedel.count - 1 == position) imageButton2.visibility = View.GONE else imageButton2.visibility = View.VISIBLE
                if (position == 0) imageButton.visibility = View.GONE else imageButton.visibility = View.VISIBLE
            }

            override fun onPageScrollStateChanged(state: Int) {}
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
        if (item.itemId == R.id.action_padzeia) {
            SabytieOnView = !SabytieOnView
            val messege: String = if (!SabytieOnView) {
                resources.getString(R.string.sabytie_disable_mun)
            } else {
                resources.getString(R.string.sabytie_enable_mun)
            }
            val layout = LinearLayout(this)
            if (dzenNoch) layout.setBackgroundResource(R.color.colorPrimary_black) else layout.setBackgroundResource(R.color.colorPrimary)
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val toast = TextViewRobotoCondensed(this)
            toast.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            toast.setPadding(realpadding, realpadding, realpadding, realpadding)
            toast.text = messege
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            layout.addView(toast)
            val mes = Toast(this)
            mes.duration = Toast.LENGTH_SHORT
            mes.view = layout
            mes.show()
            adapterViewPager.notifyDataSetChanged()
            invalidateOptionsMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class CaliandarMunAdapter : ArrayAdapter<String> {
        private var arrayList: List<String>? = null

        constructor(context: Context, strings: Array<String?>) : super(context, R.layout.simple_list_item_4, strings)
        constructor(context: Context, list: List<String>) : super(context, R.layout.simple_list_item_4, list) {
            arrayList = list
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val day = Calendar.getInstance() as GregorianCalendar
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextViewRobotoCondensed
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            if (dzenNoch)
                textView.setTextColor(ContextCompat.getColor(this@CaliandarMun, R.color.colorIcons))
            if (arrayList == null) {
                if (day[Calendar.MONTH] == position) {
                    textView.setTypeface(null, Typeface.BOLD)
                } else {
                    textView.setTypeface(null, Typeface.NORMAL)
                }
            } else {
                if (day[Calendar.YEAR] == position + SettingsActivity.GET_CALIANDAR_YEAR_MIN) {
                    textView.setTypeface(null, Typeface.BOLD)
                } else {
                    textView.setTypeface(null, Typeface.NORMAL)
                }
            }
            return v
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convert: View
            val viewHolder: ViewHolder
            val day = Calendar.getInstance() as GregorianCalendar
            if (convertView == null) {
                convert = this@CaliandarMun.layoutInflater.inflate(R.layout.simple_list_item_4, parent, false)
                viewHolder = ViewHolder()
                convert.tag = viewHolder
                viewHolder.text = convert.findViewById(R.id.text1)
            } else {
                convert = convertView
                viewHolder = convert.tag as ViewHolder
            }
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            if (dzenNoch)
                viewHolder.text?.setTextColor(ContextCompat.getColor(this@CaliandarMun, R.color.colorIcons))
            if (arrayList == null) {
                if (day[Calendar.MONTH] == position) {
                    viewHolder.text?.setTypeface(null, Typeface.BOLD)
                } else {
                    viewHolder.text?.setTypeface(null, Typeface.NORMAL)
                }
                viewHolder.text?.text = names[position]
            } else {
                if (day[Calendar.YEAR] == position + SettingsActivity.GET_CALIANDAR_YEAR_MIN) {
                    viewHolder.text?.setTypeface(null, Typeface.BOLD)
                } else {
                    viewHolder.text?.setTypeface(null, Typeface.NORMAL)
                }
                arrayList?.let { viewHolder.text?.text = it[position] }
            }
            return convert
        }
    }

    internal inner class MyCalendarNedelAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        private var currentFragment: Fragment? = null
        private var cor = 1

        override fun getItem(position: Int): Fragment {
            val calendarStart = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
            for (i in 0..6) {
                if (calendarStart[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) break
                calendarStart.add(Calendar.DATE, 1)
            }
            var count = 0
            var dayyear = 0
            for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
                dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
            }
            for (e in 1..dayyear) {
                if (e % 7 == 0) {
                    if (count == position) {
                        return CaliandarNedzel.newInstance(calendarStart[Calendar.YEAR], calendarStart[Calendar.MONTH], calendarStart[Calendar.DATE], position)
                    }
                    count++
                    calendarStart.add(Calendar.DATE, 7)
                }
            }
            return CaliandarNedzel.newInstance(calendarStart[Calendar.YEAR], calendarStart[Calendar.MONTH], calendarStart[Calendar.DATE], 0)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            var dayyear = 0
            for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
                dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
            }
            return dayyear / 7 - cor
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            if (currentFragment !== `object`) {
                currentFragment = `object` as Fragment
            }
            super.setPrimaryItem(container, position, `object`)
        }

        init {
            val calendarStart = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
            if (calendarStart[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) cor = 0
        }
    }

    internal inner class MyPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            return (SettingsActivity.GET_CALIANDAR_YEAR_MAX - SettingsActivity.GET_CALIANDAR_YEAR_MIN + 1) * 12
        }

        override fun getItem(position: Int): Fragment {
            val g = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, day)
            for (i in 0 until count) {
                if (position == i) {
                    return PageFragmentMonth.newInstance(g[Calendar.DATE], g[Calendar.MONTH], g[Calendar.YEAR], position)
                }
                g.add(Calendar.MONTH, 1)
            }
            return PageFragmentMonth.newInstance(g[Calendar.DATE], g[Calendar.MONTH], g[Calendar.YEAR], 0)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        var SabytieOnView = false
    }
}