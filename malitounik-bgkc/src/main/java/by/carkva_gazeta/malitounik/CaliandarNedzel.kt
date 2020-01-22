package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class CaliandarNedzel : ListFragment() {
    private var year = 0
    private var mun = 0
    private var date = 0
    private var dateInt = 0
    private var position = 0
    private var count = 0
    private val strings = ArrayList<ArrayList<String>>()
    private val strings2 = ArrayList<ArrayList<String>>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        year = arguments?.getInt("year") ?: SettingsActivity.GET_CALIANDAR_YEAR_MIN
        mun = arguments?.getInt("mun") ?: 0
        dateInt = arguments?.getInt("date") ?: 1
        position = arguments?.getInt("position") ?: 0
        count = count()
        date = getMun()
    }

    private fun getMun(): Int {
        val gS = GregorianCalendar(year, mun, dateInt)
        val g = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
        for (i in 0 until count) {
            if (g[Calendar.MONTH] == gS[Calendar.MONTH] && g[Calendar.YEAR] == gS[Calendar.YEAR]) {
                return i
            }
            g.add(Calendar.MONTH, 1)
        }
        return 0
    }

    private fun count(): Int {
        return (SettingsActivity.GET_CALIANDAR_YEAR_MAX - SettingsActivity.GET_CALIANDAR_YEAR_MIN + 1) * 12
    }

    private val jsonFile: Unit
        get() {
            val builder = StringBuilder()
            var inputStream = resources.openRawResource(MainActivity.caliandar(activity, date))
            var isr = InputStreamReader(inputStream)
            var reader = BufferedReader(isr)
            reader.forEachLine {
                builder.append(it)
            }
            isr.close()
            var out = builder.toString()
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
            strings.addAll(gson.fromJson(out, type))
            if (date != count - 1) {
                for (i in strings.indices) {
                    if (26 >= strings[i][1].toInt()) {
                        val t1 = builder.toString().lastIndexOf("]")
                        val builderN = StringBuilder()
                        inputStream = resources.openRawResource(MainActivity.caliandar(activity, date + 1))
                        isr = InputStreamReader(inputStream)
                        reader = BufferedReader(isr)
                        reader.forEachLine {
                            builderN.append(it)
                        }
                        isr.close()
                        val t2 = builderN.toString().indexOf("[")
                        val out1 = out.substring(0, t1)
                        val out2 = builderN.toString().substring(t2 + 1)
                        out = "$out1,$out2"
                        strings.clear()
                        break
                    }
                }
            }
            strings.addAll(gson.fromJson(out, type))
            var i = 0
            while (i < strings.size) {
                if (strings[i][1].toInt() == dateInt) {
                    for (e in 0..6) {
                        strings2.add(strings[i])
                        i++
                    }
                    break
                }
                i++
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        jsonFile
        val str = arrayOfNulls<String>(7)
        
        activity?.let {
            listAdapter = CaliandarNedzelListAdapter(it, strings2, str)
        }

        if (setDenNedeli) {
            val c = Calendar.getInstance() as GregorianCalendar
            if (getNedzel(c[Calendar.WEEK_OF_YEAR] - 1) == position) {
                listView.setSelection(c[Calendar.DAY_OF_WEEK] - 1)
                setDenNedeli = false
            }
        }
        listView.isVerticalScrollBarEnabled = false
    }

    private fun getNedzel(WeekOfYear: Int): Int {
        val calendar = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MAX, 11, 31)
        var dayyear = 0
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until SettingsActivity.GET_CALIANDAR_YEAR_MAX - 1) {
            dayyear = if (calendar.isLeapYear(i)) 366 + dayyear else 365 + dayyear
        }
        return dayyear / 7 + WeekOfYear
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val g = GregorianCalendar(strings2[position][3].toInt(), strings2[position][2].toInt(), strings2[position][1].toInt())
        val intent = Intent()
        intent.putExtra("data", g[Calendar.DAY_OF_YEAR] - 1)
        intent.putExtra("year", strings2[position][3].toInt())
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    companion object {
        var setDenNedeli = false
        fun newInstance(year: Int, mun: Int, date: Int, position: Int): CaliandarNedzel {
            val fragment = CaliandarNedzel()
            val args = Bundle()
            args.putInt("position", position)
            args.putInt("year", year)
            args.putInt("mun", mun)
            args.putInt("date", date)
            fragment.arguments = args
            return fragment
        }
    }
}