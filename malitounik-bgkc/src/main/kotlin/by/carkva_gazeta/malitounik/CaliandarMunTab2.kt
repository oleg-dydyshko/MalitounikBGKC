package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.databinding.CalendarTab2Binding
import java.util.*

class CaliandarMunTab2 : Fragment() {
    private lateinit var adapterViewPagerNedel: FragmentStateAdapter
    private var day = 0
    private var posMun = 0
    private var yearG = 0
    private var _binding: CalendarTab2Binding? = null
    private val binding get() = _binding!!
    private var tydzenListener: CaliandarMunTab2Listener? = null

    interface CaliandarMunTab2Listener {
        fun setDayAndMun2(day: Int, mun: Int, year: Int, cviatyGlavnyia: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        day = arguments?.getInt("day") ?: 0
        posMun = arguments?.getInt("posMun") ?: 0
        yearG = arguments?.getInt("yearG") ?: 0
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
        _binding = CalendarTab2Binding.inflate(inflater, container, false)
        binding.pagerNedel.offscreenPageLimit = 3
        activity?.let { activity ->
            adapterViewPagerNedel = MyCalendarNedelAdapter(this)
            binding.pagerNedel.adapter = adapterViewPagerNedel
            binding.pagerNedel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val firstPosition = MenuCaliandar.getFirstPositionNiadzel(position)
                    tydzenListener?.setDayAndMun2(firstPosition[1].toInt(), firstPosition[2].toInt(), firstPosition[3].toInt(), firstPosition[6])
                    activity.invalidateOptionsMenu()
                }
            })
            val firstPosition = MenuCaliandar.getFirstPositionNiadzel(MenuCaliandar.getPositionCaliandarNiadzel(day, posMun, yearG))
            binding.pagerNedel.setCurrentItem(firstPosition[26].toInt(), false)
        }
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_right).isVisible = adapterViewPagerNedel.itemCount - 1 != binding.pagerNedel.currentItem
        menu.findItem(R.id.action_left).isVisible = 0 != binding.pagerNedel.currentItem
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_left) {
            binding.pagerNedel.setCurrentItem(binding.pagerNedel.currentItem - 1, false)
        }
        if (id == R.id.action_right) {
            binding.pagerNedel.setCurrentItem(binding.pagerNedel.currentItem + 1, false)
        }
        return super.onOptionsItemSelected(item)
    }

    private class MyCalendarNedelAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            var count = MenuCaliandar.getDataCalaindar().size / 7
            if (MenuCaliandar.getDataCalaindar()[0][0].toInt() != Calendar.SUNDAY) count++
            if (MenuCaliandar.getDataCalaindar()[MenuCaliandar.getDataCalaindar().size - 1][0].toInt() != Calendar.SATURDAY) count++
            return count
        }

        override fun createFragment(position: Int): Fragment {
            val arrayList = MenuCaliandar.getFirstPositionNiadzel(position)
            return CaliandarNedzel.newInstance(arrayList[3].toInt(), arrayList[2].toInt(), arrayList[1].toInt())
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