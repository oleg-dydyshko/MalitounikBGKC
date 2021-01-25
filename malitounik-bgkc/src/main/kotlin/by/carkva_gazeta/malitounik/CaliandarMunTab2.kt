package by.carkva_gazeta.malitounik

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import by.carkva_gazeta.malitounik.databinding.CalendatTab2Binding
import java.util.*

class CaliandarMunTab2 : Fragment() {
    private var dzenNoch = false
    private lateinit var adapterViewPagerNedel: SmartFragmentStatePagerAdapter
    private val c = Calendar.getInstance() as GregorianCalendar
    private var day = 0
    private var posMun = 0
    private var yearG = 0
    private var _binding: CalendatTab2Binding? = null
    private val binding get() = _binding!!
    private var tydzenListener: CaliandarMunTab2Listener? = null

    interface CaliandarMunTab2Listener {
        fun setDayAndMun2(day: Int, mun: Int, year: Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalendatTab2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { activity ->
            val chin = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = chin.getBoolean("dzen_noch", false)
            day = arguments?.getInt("day") ?: 0
            posMun = arguments?.getInt("posMun") ?: 0
            yearG = arguments?.getInt("yearG") ?: 0
            fragmentManager?.let {
                adapterViewPagerNedel = MyCalendarNedelAdapter(it)
                binding.pagerNedel.adapter = adapterViewPagerNedel
            }
            binding.imageButton.setOnClickListener { binding.pagerNedel.currentItem = binding.pagerNedel.currentItem - 1 }
            binding.imageButton2.setOnClickListener { binding.pagerNedel.currentItem = binding.pagerNedel.currentItem + 1 }
            if (dzenNoch) {
                binding.imageButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.levo_catedra))
                binding.imageButton2.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.pravo_catedra))
                binding.nedelName.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
            }
            if (adapterViewPagerNedel.count - 1 == binding.pagerNedel.currentItem) binding.imageButton2.visibility = View.GONE
            if (binding.pagerNedel.currentItem == 0) binding.imageButton.visibility = View.GONE
        }
        binding.pagerNedel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
                        binding.nedelName.text = getString(R.string.tydzen_name, ned)
                        tydzenListener?.setDayAndMun2(start[Calendar.DATE], start[Calendar.MONTH], start[Calendar.YEAR])
                    }
                    start.add(Calendar.DATE, 7)
                    ned = start[Calendar.WEEK_OF_YEAR]
                }
                if (adapterViewPagerNedel.count - 1 == position) binding.imageButton2.visibility = View.GONE else binding.imageButton2.visibility = View.VISIBLE
                if (position == 0) binding.imageButton.visibility = View.GONE else binding.imageButton.visibility = View.VISIBLE
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
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
        val calendarEnd = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MAX, 11, 31)
        calendarEnd.firstDayOfWeek = Calendar.SUNDAY
        for (i in 7 downTo 1) {
            if (calendarEnd[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) break
            calendarEnd.add(Calendar.DATE, -1)
        }
        val c2 = GregorianCalendar(yearG, posMun, day)
        c2.firstDayOfWeek = Calendar.SUNDAY
        when {
            calendarEnd.timeInMillis <= c2.timeInMillis -> {
                for (e in 0 until dayyear2) {
                    var dayFull = dayyear + calendarEnd[Calendar.DAY_OF_YEAR] - 7 - (8 - ost)
                    if (dayFull < 0) dayFull = 0
                    if (e == dayFull) {
                        binding.pagerNedel.currentItem = count2
                        binding.nedelName.text = getString(R.string.tydzen_name, calendarEnd[Calendar.WEEK_OF_YEAR])
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
                        binding.pagerNedel.currentItem = count2
                        binding.nedelName.text = getString(R.string.tydzen_name, c2[Calendar.WEEK_OF_YEAR])
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
                        binding.pagerNedel.currentItem = count2
                        binding.nedelName.text = getString(R.string.tydzen_name, calendarStart[Calendar.WEEK_OF_YEAR])
                    }
                    if (e % 7 == 0) {
                        count2++
                    }
                }
            }
        }
    }

    private inner class MyCalendarNedelAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
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

        override fun getItemPosition(ob: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            var dayyear = 0
            for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
                dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
            }
            return dayyear / 7 - cor
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, ob: Any) {
            if (currentFragment !== ob) {
                currentFragment = ob as Fragment
            }
            super.setPrimaryItem(container, position, ob)
        }

        init {
            val calendarStart = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
            if (calendarStart[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) cor = 0
        }
    }

    companion object {
        fun getInstance(posMun: Int, yearG: Int, day: Int, listener: CaliandarMunTab2Listener): CaliandarMunTab2 {
            val frag = CaliandarMunTab2()
            frag.tydzenListener = listener
            val bundle = Bundle()
            bundle.putInt("posMun", posMun)
            bundle.putInt("yearG", yearG)
            bundle.putInt("day", day)
            frag.arguments = bundle
            return frag
        }
    }
}