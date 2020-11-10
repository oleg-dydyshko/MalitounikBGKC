package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
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

            activity?.let {
                listAdapter = CaliandarNedzelListAdapter(it, strings2)
                listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_white)
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

    private inner class CaliandarNedzelListAdapter(private val mContext: Activity, private val arrayList: ArrayList<ArrayList<String>>) : ArrayAdapter<ArrayList<String>>(mContext, R.layout.calaindar_nedel, arrayList) {
        private val c: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
        private val chin: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val munName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
        private val nedelName = arrayOf("", "нядзеля", "панядзелак", "аўторак", "серада", "чацьвер", "пятніца", "субота")

        override fun getView(position: Int, rootView: View?, parent: ViewGroup): View {
            val view: View
            val viewHolder: ViewHolder
            if (rootView == null) {
                view = mContext.layoutInflater.inflate(R.layout.calaindar_nedel, parent, false)
                viewHolder = ViewHolder()
                view.tag = viewHolder
                viewHolder.textCalendar = view.findViewById(R.id.textCalendar)
                viewHolder.textPraz = view.findViewById(R.id.textCviatyGlavnyia)
                viewHolder.textSviat = view.findViewById(R.id.textSviatyia)
                viewHolder.textPostS = view.findViewById(R.id.textPost)
                viewHolder.linearLayout = view.findViewById(R.id.linearView)
            } else {
                view = rootView
                viewHolder = view.tag as ViewHolder
            }
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            //по умолчанию
            viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDivider))
            viewHolder.textSviat?.visibility = View.VISIBLE
            viewHolder.textPraz?.visibility = View.GONE
            viewHolder.textPostS?.visibility = View.GONE
            viewHolder.textPraz?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            viewHolder.textPraz?.setTypeface(null, Typeface.BOLD)
            if (dzenNoch) {
                viewHolder.textSviat?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                viewHolder.textPraz?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_black))
            }
            if (c[Calendar.YEAR] == arrayList[position][3].toInt() && c[Calendar.DATE] == arrayList[position][1].toInt() && c[Calendar.MONTH] == arrayList[position][2].toInt()) {
                if (dzenNoch) viewHolder.linearLayout?.setBackgroundResource(R.drawable.calendar_nedel_today_black)
                else viewHolder.linearLayout?.setBackgroundResource(R.drawable.calendar_nedel_today)
            } /*else {
                //if (dzenNoch) viewHolder.linearLayout?.setBackgroundResource(R.drawable.selector_dark)
                //else viewHolder.linearLayout?.setBackgroundResource(R.drawable.selector_white)
            }*/
            if (arrayList[position][3].toInt() != c[Calendar.YEAR]) viewHolder.textCalendar?.text = getString(R.string.tydzen_name3, nedelName[arrayList[position][0].toInt()], arrayList[position][1], munName[arrayList[position][2].toInt()], arrayList[position][3])
            else viewHolder.textCalendar?.text = getString(R.string.tydzen_name2, nedelName[arrayList[position][0].toInt()], arrayList[position][1], munName[arrayList[position][2].toInt()])
            //viewHolder.textPraz.setText(arrayList.get(position).get(3)); Год
            var sviatyia = arrayList[position][4]
            if (dzenNoch) {
                sviatyia = sviatyia.replace("#d00505", "#f44336")
            }
            viewHolder.textSviat?.text = MainActivity.fromHtml(sviatyia)
            if (arrayList[position][4].contains("no_sviatyia")) viewHolder.textSviat?.visibility = View.GONE
            viewHolder.textPraz?.text = arrayList[position][6]
            if (!arrayList[position][6].contains("no_sviaty")) viewHolder.textPraz?.visibility = View.VISIBLE
            // убот = субота
            if (arrayList[position][6].contains("Пачатак") || arrayList[position][6].contains("Вялікі") || arrayList[position][6].contains("Вялікая") || arrayList[position][6].contains("убот") || arrayList[position][6].contains("ВЕЧАР") || arrayList[position][6].contains("Палова")) {
                viewHolder.textPraz?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textPraz?.setTypeface(null, Typeface.NORMAL)
            }
            if (arrayList[position][5].contains("1") || arrayList[position][5].contains("2") || arrayList[position][5].contains("3")) {
                viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                if (dzenNoch) viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary_black)) else viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            } else if (arrayList[position][7].contains("2")) {
                viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
                viewHolder.textPostS?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textPostS?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
                viewHolder.textPostS?.text = mContext.resources.getString(R.string.Post)
            } else if (arrayList[position][7].contains("3")) {
                viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
            } else if (arrayList[position][7].contains("1")) {
                viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
                viewHolder.textPostS?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textPostS?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
                viewHolder.textPostS?.text = mContext.resources.getString(R.string.No_post)
            }
            if (arrayList[position][5].contains("2")) {
                viewHolder.textPraz?.setTypeface(null, Typeface.NORMAL)
            }
            if (arrayList[position][7].contains("3")) {
                viewHolder.textPostS?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                viewHolder.textPostS?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
                viewHolder.textPostS?.text = mContext.resources.getString(R.string.Strogi_post)
                viewHolder.textPostS?.visibility = View.VISIBLE
            } else if (arrayList[position][0].contains("6")) { // Пятница
                viewHolder.textPostS?.visibility = View.VISIBLE
            }
            return view
        }
    }

    private class ViewHolder {
        var linearLayout: LinearLayout? = null
        var textCalendar: TextViewRobotoCondensed? = null
        var textPraz: TextViewRobotoCondensed? = null
        var textSviat: TextViewRobotoCondensed? = null
        var textPostS: TextViewRobotoCondensed? = null
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