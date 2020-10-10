package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.menu_caliandar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.util.*


/**
 * Created by oleg on 14.6.19
 */
class MenuCaliandar : MenuCaliandarFragment() {
    private lateinit var listinner: MenuCaliandarPageListinner
    private lateinit var adapter: MyCalendarAdapter
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

    override fun delitePadzeia(position: Int) {
        activity?.let {
            val sab = MainActivity.padzeia[position]
            val filen = sab.padz
            val del = ArrayList<Padzeia>()
            for (p in MainActivity.padzeia) {
                if (p.padz == filen) {
                    del.add(p)
                }
            }
            MainActivity.padzeia.removeAll(del)
            val  am = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val filesDir = it.filesDir
            val outputStream = FileWriter("$filesDir/Sabytie.json")
            val gson = Gson()
            outputStream.write(gson.toJson(MainActivity.padzeia))
            outputStream.close()
            MainActivity.padzeia.sort()
            CoroutineScope(Dispatchers.IO).launch {
                if (sab.count == "0") {
                    if (sab.repit == 1 || sab.repit == 4 || sab.repit == 5 || sab.repit == 6) {
                        if (sab.sec != "-1") {
                            val intent = createIntent(sab.padz, "Падзея" + " " + sab.dat + " у " + sab.tim, sab.dat, sab.tim)
                            val londs3 = sab.paznic / 100000L
                            val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, 0)
                            am.cancel(pIntent)
                            pIntent.cancel()
                        }
                    } else {
                        for (p in del) {
                            if (p.padz.contains(filen)) {
                                if (p.sec != "-1") {
                                    val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                                    val londs3 = p.paznic / 100000L
                                    val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, 0)
                                    am.cancel(pIntent)
                                    pIntent.cancel()
                                }
                            }
                        }
                    }
                } else {
                    for (p in del) {
                        if (p.sec != "-1") {
                            val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                            val londs3 = p.paznic / 100000L
                            val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, 0)
                            am.cancel(pIntent)
                            pIntent.cancel()
                        }
                    }
                }
            }
            MainActivity.toastView(it, getString(R.string.remove_padzea))
            adapter.notifyDataSetChanged()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentManager?.let {
            adapter = MyCalendarAdapter(it)
            pager.adapter = adapter
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

    private fun createIntent(action: String, extra: String, data: String, time: String): Intent {
        var i = Intent()
        activity?.let {
            i = Intent(it, ReceiverBroad::class.java)
            i.action = action
            i.putExtra("sabytieSet", true)
            i.putExtra("extra", extra)
            val dateN = data.split(".").toTypedArray()
            val timeN = time.split(":").toTypedArray()
            val g = GregorianCalendar(dateN[2].toInt(), dateN[1].toInt() - 1, dateN[0].toInt(), 0, 0, 0)
            i.putExtra("dataString", dateN[0] + dateN[1] + timeN[0] + timeN[1])
            i.putExtra("year", g[Calendar.YEAR])
        }
        return i
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