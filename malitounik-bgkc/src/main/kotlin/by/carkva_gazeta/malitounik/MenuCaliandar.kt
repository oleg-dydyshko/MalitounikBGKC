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
import androidx.fragment.app.Fragment
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
import kotlin.collections.ArrayList

class MenuCaliandar : MenuCaliandarFragment() {
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

    @SuppressLint("NotifyDataSetChanged")
    override fun delitePadzeia(position: Int) {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            while (true) {
                if (!k.getBoolean("setAlarm", true))
                    break
            }
            val sab = MainActivity.padzeia[position]
            val filen = sab.padz
            val del = ArrayList<Padzeia>()
            for (p in MainActivity.padzeia) {
                if (p.padz == filen) {
                    del.add(p)
                }
            }
            MainActivity.padzeia.removeAll(del)
            val am = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val filesDir = it.filesDir
            val outputStream = FileWriter("$filesDir/Sabytie.json")
            val gson = Gson()
            outputStream.write(gson.toJson(MainActivity.padzeia))
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
                                val intent = createIntent(sab.padz, "Падзея" + " " + sab.dat + " у " + sab.tim, sab.dat, sab.tim)
                                val londs3 = sab.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, flags)
                                am.cancel(pIntent)
                                pIntent.cancel()
                            }
                        } else {
                            for (p in del) {
                                if (p.padz.contains(filen)) {
                                    if (p.sec != "-1") {
                                        val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
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
                                val intent = createIntent(p.padz, "Падзея" + " " + p.dat + " у " + p.tim, p.dat, p.tim)
                                val londs3 = p.paznic / 100000L
                                val pIntent = PendingIntent.getBroadcast(it, londs3.toInt(), intent, flags)
                                am.cancel(pIntent)
                                pIntent.cancel()
                            }
                        }
                    }
                }
                MainActivity.toastView(getString(R.string.remove_padzea))
                Sabytie.editCaliandar = true
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MyCalendarAdapter(this)
        binding.pager.offscreenPageLimit = 3
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
        menu.findItem(R.id.action_glava).isVisible = dayyear + c[Calendar.DAY_OF_YEAR] - 1 != binding.pager.currentItem
        menu.findItem(R.id.action_mun).isVisible = true
        menu.findItem(R.id.tipicon).isVisible = true
        menu.findItem(R.id.sabytie).isVisible = true
        menu.findItem(R.id.search_sviatyia).isVisible = true
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        }
    }

    private fun createIntent(action: String, extra: String, data: String, time: String): Intent {
        var i = Intent()
        activity?.let {
            i = Intent(it, ReceiverBroad::class.java)
            i.action = action
            i.putExtra("sabytieSet", true)
            i.putExtra("extra", extra)
            val dateN = data.split(".")
            val timeN = time.split(":")
            val g = GregorianCalendar(dateN[2].toInt(), dateN[1].toInt() - 1, dateN[0].toInt(), 0, 0, 0)
            i.putExtra("dataString", dateN[0] + dateN[1] + timeN[0] + timeN[1])
            i.putExtra("year", g[Calendar.YEAR])
        }
        return i
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return super.onOptionsItemSelected(item)
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_carkva) {
            activity?.let {
                if (MainActivity.checkmodulesAdmin()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.ADMINSVIATYIA)
                    val caliandarFull = childFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as CaliandarFull
                    val year = caliandarFull.getYear()
                    val cal = GregorianCalendar(year, 0, 1)
                    var dayofyear = caliandarFull.getDayOfYear() - 1
                    if (!cal.isLeapYear(year) && dayofyear >= 59) {
                        dayofyear++
                    }
                    intent.putExtra("dayOfYear", dayofyear)
                    startActivity(intent)
                } else {
                    MainActivity.toastView(getString(R.string.error))
                }
            }
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
        }
        return super.onOptionsItemSelected(item)
    }

    private class MyCalendarAdapter(val fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            val fragment = fragment.childFragmentManager.findFragmentByTag("f" + holder.itemId) as? CaliandarFull
            fragment?.sabytieView("") ?: super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount() = getDataCalaindar().size

        override fun createFragment(position: Int) = CaliandarFull.newInstance(position)
    }

    companion object {
        private val data = ArrayList<ArrayList<String>>()

        private fun getData() {
            if (data.size == 0) {
                val inputStream = Malitounik.applicationContext().resources.openRawResource(R.raw.caliandar)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                val builder = reader.use {
                    it.readText()
                }
                val gson = Gson()
                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                data.addAll(gson.fromJson(builder, type))
            }
        }

        fun getPositionCaliandar(position: Int): ArrayList<String> {
            getData()
            return data[position]
        }

        fun getPositionCaliandar(day_of_year: Int, year: Int): ArrayList<String> {
            getData()
            var position = 0
            data.forEach { arrayList ->
                if (day_of_year == arrayList[24].toInt() && year == arrayList[3].toInt()) {
                    position = arrayList[25].toInt()
                    return@forEach
                }
            }
            return data[position]
        }

        fun getPositionCaliandarMun(position: Int): ArrayList<String> {
            getData()
            var pos = 0
            data.forEach {
                if (it[27].toInt() == position) {
                    pos = it[25].toInt()
                    return@forEach
                }
            }
            return data[pos]
        }

        fun getPositionCaliandarNiadzel(day: Int, mun: Int, year: Int): Int {
            getData()
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
            getData()
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
            getData()
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
                    val g = Calendar.getInstance()
                    data.forEach { arrayList ->
                        if (day == arrayList[1].toInt() && g[Calendar.MONTH] == arrayList[2].toInt() && g[Calendar.YEAR] == arrayList[3].toInt()) {
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