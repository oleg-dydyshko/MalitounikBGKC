package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import by.carkva_gazeta.malitounik.databinding.CalendatTab2Binding
import java.util.*

class CaliandarMunTab2 : Fragment() {
    private var dzenNoch = false
    private lateinit var adapterViewPagerNedel: SmartFragmentStatePagerAdapter
    private var day = 0
    private var posMun = 0
    private var yearG = 0
    private var _binding: CalendatTab2Binding? = null
    private val binding get() = _binding!!
    private var tydzenListener: CaliandarMunTab2Listener? = null

    interface CaliandarMunTab2Listener {
        fun setDayAndMun2(day: Int, mun: Int, year: Int, cviatyGlavnyia: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            tydzenListener = try {
                context as CaliandarMunTab2Listener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement CaliandarMunTab2Listener")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalendatTab2Binding.inflate(inflater, container, false)
        activity?.let { activity ->
            val chin = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = chin.getBoolean("dzen_noch", false)
            day = arguments?.getInt("day") ?: 0
            posMun = arguments?.getInt("posMun") ?: 0
            yearG = arguments?.getInt("yearG") ?: 0
            adapterViewPagerNedel = MyCalendarNedelAdapter(childFragmentManager)
            binding.pagerNedel.adapter = adapterViewPagerNedel
            binding.pagerNedel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    val firstPosition = MenuCaliandar.getFirstPositionNiadzel(position)
                    tydzenListener?.setDayAndMun2(firstPosition[1].toInt(), firstPosition[2].toInt(), firstPosition[3].toInt(), firstPosition[6])
                    activity.invalidateOptionsMenu()
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
            val firstPosition = MenuCaliandar.getFirstPositionNiadzel(MenuCaliandar.getPositionCaliandarNiadzel(day, posMun, yearG))
            binding.pagerNedel.currentItem = firstPosition[26].toInt()
        }
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_right).isVisible = adapterViewPagerNedel.count - 1 != binding.pagerNedel.currentItem
        menu.findItem(R.id.action_left).isVisible = 0 != binding.pagerNedel.currentItem
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_left) {
            binding.pagerNedel.currentItem = binding.pagerNedel.currentItem - 1
        }
        if (id == R.id.action_right) {
            binding.pagerNedel.currentItem = binding.pagerNedel.currentItem + 1
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MyCalendarNedelAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            val arrayList = MenuCaliandar.getFirstPositionNiadzel(position)
            return CaliandarNedzel.newInstance(arrayList[3].toInt(), arrayList[2].toInt(), arrayList[1].toInt())
        }

        override fun getItemPosition(ob: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            var count = MenuCaliandar.getDataCalaindar().size / 7
            if (MenuCaliandar.getDataCalaindar()[0][0].toInt() != Calendar.SUNDAY) count++
            if (MenuCaliandar.getDataCalaindar()[MenuCaliandar.getDataCalaindar().size - 1][0].toInt() != Calendar.SATURDAY) count++
            return count
        }
    }

    companion object {
        fun getInstance(posMun: Int, yearG: Int, day: Int): CaliandarMunTab2 {
            val frag = CaliandarMunTab2()
            val bundle = Bundle()
            bundle.putInt("posMun", posMun)
            bundle.putInt("yearG", yearG)
            bundle.putInt("day", day)
            frag.arguments = bundle
            return frag
        }
    }
}