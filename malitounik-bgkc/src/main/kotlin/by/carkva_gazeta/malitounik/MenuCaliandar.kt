package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.databinding.MenuCaliandarBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileWriter
import java.io.InputStreamReader
import java.util.*

class MenuCaliandar : BaseFragment() {
    private var listinner: MenuCaliandarPageListinner? = null
    private lateinit var adapter: MyCalendarAdapter
    private var page = 0
    private var _binding: MenuCaliandarBinding? = null
    private val binding get() = _binding!!
    private var mLastClickTime: Long = 0
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                binding.pager.setCurrentItem(position, false)
            }
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuCaliandarBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setPage(page: Int) {
        binding.pager.setCurrentItem(page, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun delitePadzeia(position: Int) {
        activity?.let {
            val sab = MainActivity.padzeia[position]
            val filen = sab.padz
            val del = ArrayList<Padzeia>()
            for (p in MainActivity.padzeia) {
                if (p.padz == filen) {
                    del.add(p)
                }
            }
            MainActivity.padzeia.removeAll(del.toSet())
            val am = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val filesDir = it.filesDir
            val outputStream = FileWriter("$filesDir/Sabytie.json")
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, Padzeia::class.java).type
            outputStream.write(gson.toJson(MainActivity.padzeia, type))
            outputStream.close()
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE or 0
                    } else {
                        0
                    }
                    if (sab.count == "0") {
                        if (sab.repit == 1 || sab.repit == 4 || sab.repit == 5 || sab.repit == 6) {
                            if (sab.sec != "-1") {
                                val intent = SettingsActivity.createIntentSabytie(sab.padz, sab.dat, sab.tim)
                                val londs3 = sab.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, flags)
                                am.cancel(pIntent)
                                pIntent.cancel()
                            }
                        } else {
                            for (p in del) {
                                if (p.padz.contains(filen)) {
                                    if (p.sec != "-1") {
                                        val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                                        val londs3 = p.paznic / 100000L
                                        val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, flags)
                                        am.cancel(pIntent)
                                        pIntent.cancel()
                                    }
                                }
                            }
                        }
                    } else {
                        for (p in del) {
                            if (p.sec != "-1") {
                                val intent = SettingsActivity.createIntentSabytie(p.padz, p.dat, p.tim)
                                val londs3 = p.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, flags)
                                am.cancel(pIntent)
                                pIntent.cancel()
                            }
                        }
                    }
                }
                MainActivity.toastView(it, getString(R.string.remove_padzea))
                Sabytie.editCaliandar = true
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MyCalendarAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.pager.offscreenPageLimit = 1
        binding.pager.adapter = adapter
        binding.pager.setCurrentItem(page, false)
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                listinner?.setPage(position)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments?.getInt("page") ?: 0
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_carkva) {
            activity?.let {
                if (MainActivity.checkmodulesAdmin()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.ADMINSVIATYIA)
                    intent.putExtra("dayOfYear", getPositionCaliandar(binding.pager.currentItem)[24].toInt())
                    startActivity(intent)
                } else {
                    MainActivity.toastView(it, getString(R.string.error))
                }
            }
            return true
        }
        if (id == R.id.action_mun) {
            activity?.let {
                val data = getPositionCaliandar(binding.pager.currentItem)
                val i = Intent(it, CaliandarMun::class.java)
                i.putExtra("mun", data[2].toInt())
                i.putExtra("day", data[1].toInt())
                i.putExtra("year", data[3].toInt())
                caliandarMunLauncher.launch(i)
            }
            return true
        }
        return false
    }

    private class MyCalendarAdapter(val fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val fragment = fragmentManager.findFragmentByTag("f" + holder.itemId) as? CaliandarFull
            fragment?.sabytieView("")
        }

        override fun getItemCount() = getDataCalaindar().size

        override fun createFragment(position: Int) = CaliandarFull.newInstance(position)
    }

    companion object {
        private val data = ArrayList<ArrayList<String>>()

        init {
            val inputStream = Malitounik.applicationContext().resources.openRawResource(R.raw.caliandar)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            data.addAll(gson.fromJson(builder, type))
        }

        fun getPositionCaliandar(position: Int) = data[position]

        fun getPositionCaliandar(dayOfYear: Int, year: Int): ArrayList<String> {
            var position = 0
            data.forEach { arrayList ->
                if (dayOfYear == arrayList[24].toInt() && year == arrayList[3].toInt()) {
                    position = arrayList[25].toInt()
                    return@forEach
                }
            }
            return data[position]
        }

        fun getPositionCaliandarMun(position: Int): ArrayList<String> {
            var pos = 0
            data.forEach {
                if (it[23].toInt() == position) {
                    pos = it[25].toInt()
                    return@forEach
                }
            }
            return data[pos]
        }

        fun getPositionCaliandarNiadzel(day: Int, mun: Int, year: Int): Int {
            var position = 0
            data.forEach { arrayList ->
                if (day == arrayList[1].toInt() && mun == arrayList[2].toInt() && year == arrayList[3].toInt()) {
                    position = arrayList[26].toInt()
                    return@forEach
                }
            }
            return position
        }

        fun getFirstPositionNiadzel(position: Int): ArrayList<String> {
            var pos = 0
            data.forEach {
                if (it[26].toInt() == position && it[0].toInt() == Calendar.SUNDAY) {
                    pos = it[25].toInt()
                    return@forEach
                }
            }
            return data[pos]
        }

        fun getDataCalaindar(day: Int = -1, mun: Int = -1, year: Int = -1): ArrayList<ArrayList<String>> {
            when {
                day != -1 && mun != -1 && year != -1 -> {
                    val niadzeliaList = ArrayList<ArrayList<String>>()
                    var count = 0
                    data.forEach { arrayList ->
                        if (day == arrayList[1].toInt() && mun == arrayList[2].toInt() && year == arrayList[3].toInt()) {
                            count++
                            if (arrayList[26].toInt() == 0) count = arrayList[0].toInt()
                        }
                        if (count in 1..7) {
                            niadzeliaList.add(arrayList)
                            count++
                        }
                        if (count == 8) return@forEach
                    }
                    return niadzeliaList
                }
                mun != -1 && year != -1 -> {
                    val munList = ArrayList<ArrayList<String>>()
                    data.forEach { arrayList ->
                        if (mun == arrayList[2].toInt() && year == arrayList[3].toInt()) {
                            munList.add(arrayList)
                        }
                    }
                    return munList
                }
                year != -1 -> {
                    val yearList = ArrayList<ArrayList<String>>()
                    data.forEach { arrayList ->
                        if (year == arrayList[3].toInt()) {
                            yearList.add(arrayList)
                        }
                    }
                    return yearList
                }
                day != -1 -> {
                    val dayList = ArrayList<ArrayList<String>>()
                    val g = GregorianCalendar()
                    if(!g.isLeapYear(g[Calendar.YEAR])) g.add(Calendar.DAY_OF_YEAR, 1)
                    data.forEach { arrayList ->
                        if (day == arrayList[1].toInt() && g[Calendar.DAY_OF_YEAR] == arrayList[24].toInt() && g[Calendar.YEAR] == arrayList[3].toInt()) {
                            dayList.add(arrayList)
                            return@forEach
                        }
                    }
                    return dayList
                }
                else -> return data
            }
        }

        fun newInstance(page: Int): MenuCaliandar {
            val caliandar = MenuCaliandar()
            val bundle = Bundle()
            bundle.putInt("page", page)
            caliandar.arguments = bundle
            return caliandar
        }
    }
}