package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.databinding.CalendarTab1Binding
import java.util.*

class CaliandarMunTab1 : Fragment() {
    private lateinit var adapterViewPager: FragmentStateAdapter
    private var dzenNoch = false
    private lateinit var names: Array<out String>
    private var day = 0
    private var posMun = 0
    private var yearG = 0
    private var _binding: CalendarTab1Binding? = null
    private val binding get() = _binding!!
    private var munListener: CaliandarMunTab1Listener? = null

    interface CaliandarMunTab1Listener {
        fun setDayAndMun1(day: Int, mun: Int, year: Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            munListener = try {
                context as CaliandarMunTab1Listener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement CaliandarMunTab1Listener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun setDataCalendar(dataCalendar: Int) {
        val c = Calendar.getInstance() as GregorianCalendar
        if (dataCalendar >= SettingsActivity.GET_CALIANDAR_YEAR_MIN) {
            yearG = dataCalendar
            if (yearG == c[Calendar.YEAR]) {
                binding.year.typeface = MainActivity.createFont(Typeface.BOLD)
            } else {
                binding.year.typeface = MainActivity.createFont(Typeface.NORMAL)
            }
            binding.year.text = yearG.toString()
        } else {
            posMun = dataCalendar
            if (posMun == c[Calendar.MONTH] && yearG == c[Calendar.YEAR]) {
                binding.mun.typeface = MainActivity.createFont(Typeface.BOLD)
            } else {
                binding.mun.typeface = MainActivity.createFont(Typeface.NORMAL)
            }
            binding.mun.text = names[posMun]
        }
        val son1 = (yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + posMun
        binding.pager.setCurrentItem(son1, false)
        munListener?.setDayAndMun1(day, posMun, yearG)
    }

    private fun showDialog(data: Int) {
        val dialogCaliandarMunDate = DialogCaliandarMunDate.getInstance(data)
        dialogCaliandarMunDate.show(childFragmentManager, "dialogCaliandarMunDate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalendarTab1Binding.inflate(inflater, container, false)
        activity?.let { activity ->
            val chin = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = chin.getBoolean("dzen_noch", false)
            day = arguments?.getInt("day") ?: 0
            posMun = arguments?.getInt("posMun") ?: 0
            yearG = arguments?.getInt("yearG") ?: 0
            names = resources.getStringArray(R.array.meciac2)
            val c = Calendar.getInstance() as GregorianCalendar
            if (posMun == c[Calendar.MONTH] && yearG == c[Calendar.YEAR]) {
                binding.mun.typeface = MainActivity.createFont(Typeface.BOLD)
            }
            if (yearG == c[Calendar.YEAR]) {
                binding.year.typeface = MainActivity.createFont(Typeface.BOLD)
            }
            binding.mun.text = names[posMun]
            binding.year.text = yearG.toString()
            binding.mun.setOnClickListener {
                showDialog(posMun)
            }
            binding.year.setOnClickListener {
                showDialog(yearG)
            }
            binding.pager.offscreenPageLimit = 3
            adapterViewPager = MyPagerAdapter(this)
            binding.pager.adapter = adapterViewPager

            val son = (yearG - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + posMun
            binding.pager.setCurrentItem(son, false)
            binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val caliandarMun = MenuCaliandar.getPositionCaliandarMun(position)
                    yearG = caliandarMun[3].toInt()
                    posMun = caliandarMun[2].toInt()
                    if (posMun == c[Calendar.MONTH] && yearG == c[Calendar.YEAR]) {
                        binding.mun.typeface = MainActivity.createFont(Typeface.BOLD)
                    } else {
                        binding.mun.typeface = MainActivity.createFont(Typeface.NORMAL)
                    }
                    if (yearG == c[Calendar.YEAR]) {
                        binding.year.typeface = MainActivity.createFont(Typeface.BOLD)
                    } else {
                        binding.year.typeface = MainActivity.createFont(Typeface.NORMAL)
                    }
                    binding.mun.text = names[posMun]
                    binding.year.text = yearG.toString()
                    munListener?.setDayAndMun1(day, posMun, yearG)
                }
            })
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_padzeia) {
            activity?.let {
                CaliandarMun.SabytieOnView = !CaliandarMun.SabytieOnView
                val messege: String = if (!CaliandarMun.SabytieOnView) {
                    resources.getString(R.string.sabytie_disable_mun)
                } else {
                    resources.getString(R.string.sabytie_enable_mun)
                }
                MainActivity.toastView(messege)
                adapterViewPager.notifyDataSetChanged()
                it.invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MyPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount() = (SettingsActivity.GET_CALIANDAR_YEAR_MAX - SettingsActivity.GET_CALIANDAR_YEAR_MIN + 1) * 12

        override fun createFragment(position: Int): Fragment {
            val caliandarMun = MenuCaliandar.getPositionCaliandarMun(position)
            return PageFragmentMonth.newInstance(caliandarMun[1].toInt(), caliandarMun[2].toInt(), caliandarMun[3].toInt())
        }
    }

    companion object {
        fun getInstance(posMun: Int, yearG: Int, day: Int): CaliandarMunTab1 {
            val frag = CaliandarMunTab1()
            val bundle = Bundle()
            bundle.putInt("posMun", posMun)
            bundle.putInt("yearG", yearG)
            bundle.putInt("day", day)
            frag.arguments = bundle
            return frag
        }
    }
}