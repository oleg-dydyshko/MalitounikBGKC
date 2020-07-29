package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.calendar.*
import kotlinx.android.synthetic.main.menu_caliandar.*
import java.util.*

/**
 * Created by oleg on 14.6.19
 */
class MenuCaliandar : Fragment() {
    private lateinit var listinner: MenuCaliandarPageListinner
    private var page = 0

    internal interface MenuCaliandarPageListinner {
        fun setPage(page: Int)
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        if (activity is Activity) {
            listinner = try {
                activity as MenuCaliandarPageListinner
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement MenuCaliandarPageListinner")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_caliandar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentManager?.let {
            val frag = MyCalendarAdapter(it)
            pager.adapter = frag
            pager.currentItem = page
            pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    listinner.setPage(position)
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        page = arguments?.getInt("page") ?: 0
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val c = Calendar.getInstance() as GregorianCalendar
        var dayyear = 0
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until c[Calendar.YEAR]) {
            dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
        }
        menu.findItem(R.id.action_glava).isVisible = dayyear + c[Calendar.DAY_OF_YEAR] - 1 != pager.currentItem
        menu.findItem(R.id.action_mun).isVisible = true
        menu.findItem(R.id.tipicon).isVisible = true
        menu.findItem(R.id.sabytie).isVisible = true
        menu.findItem(R.id.search_sviatyia).isVisible = true
    }

    private class MyCalendarAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        var currentFragment: Fragment? = null
            private set
        private var mun = Calendar.JANUARY
        private var year = SettingsActivity.GET_CALIANDAR_YEAR_MIN
        private val c = Calendar.getInstance() as GregorianCalendar

        override fun getItem(position: Int): Fragment {
            val g = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
            for (i2 in 0 until count) {
                if (position == i2) {
                    if (mun != g[Calendar.MONTH] || year != g[Calendar.YEAR]) {
                        mun = g[Calendar.MONTH]
                        year = g[Calendar.YEAR]
                    }
                    val dayofyear = g[Calendar.DAY_OF_YEAR] - 1
                    val year = g[Calendar.YEAR]
                    val day = g[Calendar.DATE] - 1
                    return CaliandarFull.newInstance(position, day, year, dayofyear)
                }
                g.add(Calendar.DATE, 1)
            }
            return CaliandarFull.newInstance(0, 1, SettingsActivity.GET_CALIANDAR_YEAR_MIN, 1)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            var dayyear = 0
            for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
                dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
            }
            return dayyear
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, ob: Any) {
            if (currentFragment !== ob) {
                currentFragment = ob as Fragment
            }
            super.setPrimaryItem(container, position, ob)
        }
    }

    companion object {
        var dataJson = ""
        var munKal = 0
        fun newInstance(page: Int): MenuCaliandar {
            val caliandar = MenuCaliandar()
            val bundle = Bundle()
            bundle.putInt("page", page)
            caliandar.arguments = bundle
            return caliandar
        }
    }
}