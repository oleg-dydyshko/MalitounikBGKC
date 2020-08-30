package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.Sabytie.Companion.getColors
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.calendar_mun.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class PageFragmentMonth : Fragment(), View.OnClickListener {
    private var wik = 0
    private var date = 0
    private var mun = 0
    private var year = 0
    private var pageNumberFull = 0
    private var dzenNoch = false
    private val data = ArrayList<ArrayList<String>>()
    private val padzei = ArrayList<Padzeia>()
    private var viewId = 0
    private var mLastClickTime: Long = 0

    private fun sabytieOn() {
        if (!CaliandarMun.SabytieOnView) {
            linearLayout.visibility = View.GONE
        } else {
            linearLayout.visibility = View.VISIBLE
            val c = GregorianCalendar(year, mun, date)
            sabytieView(c[Calendar.DAY_OF_YEAR] - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = arguments?.getInt("date") ?: 0
        mun = arguments?.getInt("mun") ?: 0
        year = arguments?.getInt("year") ?: 0
        val position = arguments?.getInt("position") ?: 0
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
        data.addAll(gson.fromJson(getData(position), type))
        for (p in MainActivity.padzeia) {
            val r1 = p.dat.split(".").toTypedArray()
            val munL = r1[1].toInt() - 1
            val yaer = r1[2].toInt()
            if (munL == mun && yaer == year) {
                padzei.add(p)
            }
        }
    }

    private fun getData(mun: Int): String {
        var builder = ""
        activity?.let { it ->
            val inputStream = resources.openRawResource(MainActivity.caliandar(it, mun))
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            builder = reader.readText()
        }
        return builder
    }

    private fun sabytieCheck(day: Int): Boolean {
        var sabytie = false
        for (p in padzei) {
            val r1 = p.dat.split(".").toTypedArray()
            val date = r1[0].toInt()
            if (date == day) {
                sabytie = true
                break
            }
        }
        return sabytie
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.calendar_mun, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            activity?.let { it ->
                val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                sabytieOn()
                dzenNoch = chin.getBoolean("dzen_noch", false)
                if (dzenNoch) {
                    textView2.setBackgroundResource(R.drawable.calendar_red_black)
                    button1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    button8.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    button15.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    button22.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    button29.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    button36.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                }
                button1.setOnClickListener(this@PageFragmentMonth)
                button2.setOnClickListener(this@PageFragmentMonth)
                button3.setOnClickListener(this@PageFragmentMonth)
                button4.setOnClickListener(this@PageFragmentMonth)
                button5.setOnClickListener(this@PageFragmentMonth)
                button6.setOnClickListener(this@PageFragmentMonth)
                button7.setOnClickListener(this@PageFragmentMonth)
                button8.setOnClickListener(this@PageFragmentMonth)
                button9.setOnClickListener(this@PageFragmentMonth)
                button10.setOnClickListener(this@PageFragmentMonth)
                button11.setOnClickListener(this@PageFragmentMonth)
                button12.setOnClickListener(this@PageFragmentMonth)
                button13.setOnClickListener(this@PageFragmentMonth)
                button14.setOnClickListener(this@PageFragmentMonth)
                button15.setOnClickListener(this@PageFragmentMonth)
                button16.setOnClickListener(this@PageFragmentMonth)
                button17.setOnClickListener(this@PageFragmentMonth)
                button18.setOnClickListener(this@PageFragmentMonth)
                button19.setOnClickListener(this@PageFragmentMonth)
                button20.setOnClickListener(this@PageFragmentMonth)
                button21.setOnClickListener(this@PageFragmentMonth)
                button22.setOnClickListener(this@PageFragmentMonth)
                button23.setOnClickListener(this@PageFragmentMonth)
                button24.setOnClickListener(this@PageFragmentMonth)
                button25.setOnClickListener(this@PageFragmentMonth)
                button26.setOnClickListener(this@PageFragmentMonth)
                button27.setOnClickListener(this@PageFragmentMonth)
                button28.setOnClickListener(this@PageFragmentMonth)
                button29.setOnClickListener(this@PageFragmentMonth)
                button30.setOnClickListener(this@PageFragmentMonth)
                button31.setOnClickListener(this@PageFragmentMonth)
                button32.setOnClickListener(this@PageFragmentMonth)
                button33.setOnClickListener(this@PageFragmentMonth)
                button34.setOnClickListener(this@PageFragmentMonth)
                button35.setOnClickListener(this@PageFragmentMonth)
                button36.setOnClickListener(this@PageFragmentMonth)
                button37.setOnClickListener(this@PageFragmentMonth)
                button38.setOnClickListener(this@PageFragmentMonth)
                button39.setOnClickListener(this@PageFragmentMonth)
                button40.setOnClickListener(this@PageFragmentMonth)
                button41.setOnClickListener(this@PageFragmentMonth)
                button42.setOnClickListener(this@PageFragmentMonth)
                if (CaliandarMun.SabytieOnView) {
                    linearLayout.visibility = View.VISIBLE
                    if (savedInstanceState != null) {
                        view?.let {
                            val textView: TextViewRobotoCondensed? = it.findViewById(savedInstanceState.getInt("viewId"))
                            if (textView != null) {
                                pageNumberFull = savedInstanceState.getInt("pageNumberFull")
                                wik = savedInstanceState.getInt("wik")
                                onClick(textView)
                            }
                        }
                    }
                }
                val c = Calendar.getInstance() as GregorianCalendar
                //GregorianCalendar calendar = new GregorianCalendar(year, mun, 1);
                //int Month = calendar.get(Calendar.MONTH);
                var munTudey = false
                if (mun == c[Calendar.MONTH] && year == c[Calendar.YEAR]) munTudey = true
                val calendarFull = GregorianCalendar(year, mun, 1)
                wik = calendarFull[Calendar.DAY_OF_WEEK]
                val munAll = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
                pageNumberFull = calendarFull[Calendar.DAY_OF_YEAR]
                calendarFull.add(Calendar.MONTH, -1)
                val oldMunAktual = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
                var oldDay = oldMunAktual - wik + 1
                var day: String
                var i = 0
                var newDay = 0
                var nopost = false
                var post = false
                var strogiPost = false
                for (e in 1..42) {
                    var denNedeli: Int
                    if (e < wik) {
                        ++oldDay
                        day = "start"
                    } else if (e < munAll + wik) {
                        i++
                        day = i.toString()
                        nopost = data[i - 1][7].toInt() == 1
                        post = data[i - 1][7].toInt() == 2
                        strogiPost = data[i - 1][7].toInt() == 3
                        if (data[i - 1][5].toInt() == 1 || data[i - 1][5].toInt() == 2) nopost = false
                    } else {
                        ++newDay
                        day = "end"
                    }
                    if (42 - (munAll + wik) >= 6) TableRow.visibility = View.GONE
                    if (munAll + wik == 29) TableRowPre.visibility = View.GONE
                    val calendarPost = GregorianCalendar(year, mun, i)
                    if (e == 1) {
                        if (day == "start") {
                            button1.text = oldDay.toString()
                            button1.setBackgroundResource(R.drawable.calendar_bez_posta)
                            button1.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button1.text = day
                            if (data[i - 1][4].contains("#d00505")) button1.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button1.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button1.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_sabytie) else button1.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button1.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button1.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button1.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button1.setBackgroundResource(R.drawable.calendar_red_sabytie) else button1.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button1.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button1.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button1.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button1.setBackgroundResource(R.drawable.calendar_post_sabytie) else button1.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button1.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button1.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button1.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button1.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button1.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button1.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button1.setBackgroundResource(R.drawable.calendar_day_sabytie) else button1.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 2) {
                        if (day == "start") {
                            button2.text = oldDay.toString()
                            button2.setBackgroundResource(R.drawable.calendar_day)
                            button2.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button2.text = day
                            if (data[i - 1][4].contains("#d00505")) button2.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button2.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button2.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_sabytie) else button2.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button2.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button2.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button2.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button2.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button2.setBackgroundResource(R.drawable.calendar_red_sabytie) else button2.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button2.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button2.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button2.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button2.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button2.setBackgroundResource(R.drawable.calendar_post_sabytie) else button2.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button2.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button2.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button2.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button2.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button2.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button2.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button2.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button2.setBackgroundResource(R.drawable.calendar_day_sabytie) else button2.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 3) {
                        if (day == "start") {
                            button3.text = oldDay.toString()
                            button3.setBackgroundResource(R.drawable.calendar_day)
                            button3.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button3.text = day
                            if (data[i - 1][4].contains("#d00505")) button3.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button3.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button3.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_sabytie) else button3.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button3.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button3.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button3.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button3.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button3.setBackgroundResource(R.drawable.calendar_red_sabytie) else button3.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button3.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button3.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button3.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button3.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button3.setBackgroundResource(R.drawable.calendar_post_sabytie) else button3.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button3.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button3.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button3.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button3.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button3.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button3.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button3.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button3.setBackgroundResource(R.drawable.calendar_day_sabytie) else button3.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 4) {
                        if (day == "start") {
                            button4.text = oldDay.toString()
                            button4.setBackgroundResource(R.drawable.calendar_day)
                            button4.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button4.text = day
                            if (data[i - 1][4].contains("#d00505")) button4.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button4.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button4.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_sabytie) else button4.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button4.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button4.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button4.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button4.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button4.setBackgroundResource(R.drawable.calendar_red_sabytie) else button4.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button4.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button4.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button4.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button4.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button4.setBackgroundResource(R.drawable.calendar_post_sabytie) else button4.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button4.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button4.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button4.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button4.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button4.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button4.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button4.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button4.setBackgroundResource(R.drawable.calendar_day_sabytie) else button4.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 5) {
                        if (day == "start") {
                            button5.text = oldDay.toString()
                            button5.setBackgroundResource(R.drawable.calendar_day)
                            button5.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button5.text = day
                            if (data[i - 1][4].contains("#d00505")) button5.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button5.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button5.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_sabytie) else button5.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button5.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button5.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button5.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button5.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button5.setBackgroundResource(R.drawable.calendar_red_sabytie) else button5.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button5.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button5.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button5.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button5.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button5.setBackgroundResource(R.drawable.calendar_post_sabytie) else button5.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button5.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button5.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button5.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button5.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button5.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button5.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button5.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button5.setBackgroundResource(R.drawable.calendar_day_sabytie) else button5.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 6) {
                        if (day == "start") {
                            button6.text = oldDay.toString()
                            button6.setBackgroundResource(R.drawable.calendar_day)
                            button6.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button6.text = day
                            if (data[i - 1][4].contains("#d00505")) button6.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button6.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button6.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_sabytie) else button6.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button6.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button6.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button6.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button6.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button6.setBackgroundResource(R.drawable.calendar_red_sabytie) else button6.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button6.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button6.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button6.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button6.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button6.setBackgroundResource(R.drawable.calendar_post_sabytie) else button6.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button6.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button6.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button6.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button6.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button6.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button6.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button6.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button6.setBackgroundResource(R.drawable.calendar_day_sabytie) else button6.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 7) {
                        val sab = sabytieCheck(i)
                        button7.text = day
                        if (data[i - 1][4].contains("#d00505")) button7.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button7.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button7.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_sabytie) else button7.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button7.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button7.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button7.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button7.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button7.setBackgroundResource(R.drawable.calendar_red_sabytie) else button7.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button7.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button7.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button7.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button7.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button7.setBackgroundResource(R.drawable.calendar_post_sabytie) else button7.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button7.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button7.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button7.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button7.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button7.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button7.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button7.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button7.setBackgroundResource(R.drawable.calendar_day_sabytie) else button7.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 8) {
                        val sab = sabytieCheck(i)
                        button8.text = day
                        if (data[i - 1][4].contains("#d00505")) button8.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button8.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button8.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_sabytie) else button8.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button8.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button8.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button8.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button8.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button8.setBackgroundResource(R.drawable.calendar_red_sabytie) else button8.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button8.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button8.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button8.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button8.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button8.setBackgroundResource(R.drawable.calendar_post_sabytie) else button8.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button8.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button8.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button8.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button8.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button8.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button8.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button8.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button8.setBackgroundResource(R.drawable.calendar_day_sabytie) else button8.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 9) {
                        val sab = sabytieCheck(i)
                        button9.text = day
                        if (data[i - 1][4].contains("#d00505")) button9.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button9.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button9.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_sabytie) else button9.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button9.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button9.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button9.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button9.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button9.setBackgroundResource(R.drawable.calendar_red_sabytie) else button9.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button9.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button9.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button9.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button9.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button9.setBackgroundResource(R.drawable.calendar_post_sabytie) else button9.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button9.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button9.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button9.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button9.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button9.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button9.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button9.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button9.setBackgroundResource(R.drawable.calendar_day_sabytie) else button9.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 10) {
                        val sab = sabytieCheck(i)
                        button10.text = day
                        if (data[i - 1][4].contains("#d00505")) button10.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button10.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button10.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_sabytie) else button10.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button10.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button10.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button10.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button10.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button10.setBackgroundResource(R.drawable.calendar_red_sabytie) else button10.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button10.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button10.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button10.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button10.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button10.setBackgroundResource(R.drawable.calendar_post_sabytie) else button10.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button10.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button10.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button10.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button10.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button10.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button10.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button10.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button10.setBackgroundResource(R.drawable.calendar_day_sabytie) else button10.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 11) {
                        val sab = sabytieCheck(i)
                        button11.text = day
                        if (data[i - 1][4].contains("#d00505")) button11.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button11.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button11.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_sabytie) else button11.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button11.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button11.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button11.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button11.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button11.setBackgroundResource(R.drawable.calendar_red_sabytie) else button11.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button11.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button11.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button11.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button11.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button11.setBackgroundResource(R.drawable.calendar_post_sabytie) else button11.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button11.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button11.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button11.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button11.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button11.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button11.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button11.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button11.setBackgroundResource(R.drawable.calendar_day_sabytie) else button11.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 12) {
                        val sab = sabytieCheck(i)
                        button12.text = day
                        if (data[i - 1][4].contains("#d00505")) button12.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button12.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button12.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_sabytie) else button12.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button12.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button12.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button12.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button12.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button12.setBackgroundResource(R.drawable.calendar_red_sabytie) else button12.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button12.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button12.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button12.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button12.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button12.setBackgroundResource(R.drawable.calendar_post_sabytie) else button12.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button12.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button12.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button12.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button12.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button12.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button12.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button12.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button12.setBackgroundResource(R.drawable.calendar_day_sabytie) else button12.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 13) {
                        val sab = sabytieCheck(i)
                        button13.text = day
                        if (data[i - 1][4].contains("#d00505")) button13.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button13.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button13.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_sabytie) else button13.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button13.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button13.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button13.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button13.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button13.setBackgroundResource(R.drawable.calendar_red_sabytie) else button13.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button13.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button13.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button13.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button13.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button13.setBackgroundResource(R.drawable.calendar_post_sabytie) else button13.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button13.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button13.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button13.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button13.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button13.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button13.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button13.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button13.setBackgroundResource(R.drawable.calendar_day_sabytie) else button13.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 14) {
                        val sab = sabytieCheck(i)
                        button14.text = day
                        if (data[i - 1][4].contains("#d00505")) button14.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button14.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button14.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_sabytie) else button14.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button14.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button14.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button14.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button14.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button14.setBackgroundResource(R.drawable.calendar_red_sabytie) else button14.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button14.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button14.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button14.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button14.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button14.setBackgroundResource(R.drawable.calendar_post_sabytie) else button14.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button14.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button14.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button14.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button14.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button14.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button14.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button14.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button14.setBackgroundResource(R.drawable.calendar_day_sabytie) else button14.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 15) {
                        val sab = sabytieCheck(i)
                        button15.text = day
                        if (data[i - 1][4].contains("#d00505")) button15.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button15.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button15.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_sabytie) else button15.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button15.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button15.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button15.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button15.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button15.setBackgroundResource(R.drawable.calendar_red_sabytie) else button15.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button15.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button15.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button15.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button15.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button15.setBackgroundResource(R.drawable.calendar_post_sabytie) else button15.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button15.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button15.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button15.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button15.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button15.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button15.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button15.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button15.setBackgroundResource(R.drawable.calendar_day_sabytie) else button15.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 16) {
                        val sab = sabytieCheck(i)
                        button16.text = day
                        if (data[i - 1][4].contains("#d00505")) button16.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button16.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button16.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_sabytie) else button16.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button16.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button16.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button16.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button16.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button16.setBackgroundResource(R.drawable.calendar_red_sabytie) else button16.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button16.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button16.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button16.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button16.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button16.setBackgroundResource(R.drawable.calendar_post_sabytie) else button16.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button16.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button16.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button16.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button16.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button16.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button16.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button16.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button16.setBackgroundResource(R.drawable.calendar_day_sabytie) else button16.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 17) {
                        val sab = sabytieCheck(i)
                        button17.text = day
                        if (data[i - 1][4].contains("#d00505")) button17.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button17.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button17.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_sabytie) else button17.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button17.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button17.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button17.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button17.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button17.setBackgroundResource(R.drawable.calendar_red_sabytie) else button17.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button17.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button17.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button17.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button17.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button17.setBackgroundResource(R.drawable.calendar_post_sabytie) else button17.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button17.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button17.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button17.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button17.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button17.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button17.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button17.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button17.setBackgroundResource(R.drawable.calendar_day_sabytie) else button17.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 18) {
                        val sab = sabytieCheck(i)
                        button18.text = day
                        if (data[i - 1][4].contains("#d00505")) button18.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button18.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button18.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_sabytie) else button18.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button18.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button18.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button18.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button18.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button18.setBackgroundResource(R.drawable.calendar_red_sabytie) else button18.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button18.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button18.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button18.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button18.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button18.setBackgroundResource(R.drawable.calendar_post_sabytie) else button18.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button18.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button18.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button18.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button18.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button18.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button18.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button18.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button18.setBackgroundResource(R.drawable.calendar_day_sabytie) else button18.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 19) {
                        val sab = sabytieCheck(i)
                        button19.text = day
                        if (data[i - 1][4].contains("#d00505")) button19.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button19.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button19.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_sabytie) else button19.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button19.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button19.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button19.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button19.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button19.setBackgroundResource(R.drawable.calendar_red_sabytie) else button19.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button19.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button19.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button19.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button19.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button19.setBackgroundResource(R.drawable.calendar_post_sabytie) else button19.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button19.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button19.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button19.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button19.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button19.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button19.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button19.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button19.setBackgroundResource(R.drawable.calendar_day_sabytie) else button19.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 20) {
                        val sab = sabytieCheck(i)
                        button20.text = day
                        if (data[i - 1][4].contains("#d00505")) button20.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button20.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button20.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_sabytie) else button20.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button20.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button20.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button20.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button20.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button20.setBackgroundResource(R.drawable.calendar_red_sabytie) else button20.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button20.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button20.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button20.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button20.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button20.setBackgroundResource(R.drawable.calendar_post_sabytie) else button20.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button20.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button20.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button20.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button20.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button20.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button20.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button20.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button20.setBackgroundResource(R.drawable.calendar_day_sabytie) else button20.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 21) {
                        val sab = sabytieCheck(i)
                        button21.text = day
                        if (data[i - 1][4].contains("#d00505")) button21.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button21.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button21.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_sabytie) else button21.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button21.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button21.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button21.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button21.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button21.setBackgroundResource(R.drawable.calendar_red_sabytie) else button21.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button21.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button21.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button21.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button21.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button21.setBackgroundResource(R.drawable.calendar_post_sabytie) else button21.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button21.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button21.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button21.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button21.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button21.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button21.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button21.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button21.setBackgroundResource(R.drawable.calendar_day_sabytie) else button21.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 22) {
                        val sab = sabytieCheck(i)
                        button22.text = day
                        if (data[i - 1][4].contains("#d00505")) button22.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button22.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button22.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_sabytie) else button22.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button22.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button22.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button22.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button22.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button22.setBackgroundResource(R.drawable.calendar_red_sabytie) else button22.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button22.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button22.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button22.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button22.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button22.setBackgroundResource(R.drawable.calendar_post_sabytie) else button22.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button22.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button22.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button22.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button22.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button22.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button22.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button22.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button22.setBackgroundResource(R.drawable.calendar_day_sabytie) else button22.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 23) {
                        val sab = sabytieCheck(i)
                        button23.text = day
                        if (data[i - 1][4].contains("#d00505")) button23.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button23.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button23.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_sabytie) else button23.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button23.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button23.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button23.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button23.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button23.setBackgroundResource(R.drawable.calendar_red_sabytie) else button23.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button23.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button23.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button23.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button23.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button23.setBackgroundResource(R.drawable.calendar_post_sabytie) else button23.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button23.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button23.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button23.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button23.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button23.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button23.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button23.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button23.setBackgroundResource(R.drawable.calendar_day_sabytie) else button23.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 24) {
                        val sab = sabytieCheck(i)
                        button24.text = day
                        if (data[i - 1][4].contains("#d00505")) button24.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button24.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button24.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_sabytie) else button24.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button24.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button24.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button24.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button24.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button24.setBackgroundResource(R.drawable.calendar_red_sabytie) else button24.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button24.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button24.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button24.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button24.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button24.setBackgroundResource(R.drawable.calendar_post_sabytie) else button24.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button24.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button24.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button24.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button24.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button24.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button24.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button24.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button24.setBackgroundResource(R.drawable.calendar_day_sabytie) else button24.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 25) {
                        val sab = sabytieCheck(i)
                        button25.text = day
                        if (data[i - 1][4].contains("#d00505")) button25.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button25.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button25.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_sabytie) else button25.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button25.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button25.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button25.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button25.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button25.setBackgroundResource(R.drawable.calendar_red_sabytie) else button25.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button25.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button25.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button25.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button25.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button25.setBackgroundResource(R.drawable.calendar_post_sabytie) else button25.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button25.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button25.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button25.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button25.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button25.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button25.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button25.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button25.setBackgroundResource(R.drawable.calendar_day_sabytie) else button25.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 26) {
                        val sab = sabytieCheck(i)
                        button26.text = day
                        if (data[i - 1][4].contains("#d00505")) button26.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button26.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button26.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_sabytie) else button26.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button26.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button26.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button26.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button26.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button26.setBackgroundResource(R.drawable.calendar_red_sabytie) else button26.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button26.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button26.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button26.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button26.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button26.setBackgroundResource(R.drawable.calendar_post_sabytie) else button26.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button26.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button26.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button26.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button26.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button26.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button26.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button26.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button26.setBackgroundResource(R.drawable.calendar_day_sabytie) else button26.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 27) {
                        val sab = sabytieCheck(i)
                        button27.text = day
                        if (data[i - 1][4].contains("#d00505")) button27.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button27.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button27.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_sabytie) else button27.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button27.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button27.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button27.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button27.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button27.setBackgroundResource(R.drawable.calendar_red_sabytie) else button27.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button27.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button27.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button27.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button27.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button27.setBackgroundResource(R.drawable.calendar_post_sabytie) else button27.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button27.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button27.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button27.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button27.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button27.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button27.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button27.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button27.setBackgroundResource(R.drawable.calendar_day_sabytie) else button27.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 28) {
                        val sab = sabytieCheck(i)
                        button28.text = day
                        if (data[i - 1][4].contains("#d00505")) button28.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button28.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button28.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_sabytie) else button28.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button28.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button28.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button28.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button28.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) button28.setBackgroundResource(R.drawable.calendar_red_sabytie) else button28.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                button28.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                button28.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button28.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button28.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button28.setBackgroundResource(R.drawable.calendar_post_sabytie) else button28.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button28.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button28.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button28.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    button28.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button28.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button28.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button28.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button28.setBackgroundResource(R.drawable.calendar_day_sabytie) else button28.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 29) {
                        if (day == "end") {
                            button29.text = newDay.toString()
                            button29.setBackgroundResource(R.drawable.calendar_bez_posta)
                            button29.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button29.text = day
                            if (data[i - 1][4].contains("#d00505")) button29.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button29.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button29.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_sabytie) else button29.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button29.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button29.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button29.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button29.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button29.setBackgroundResource(R.drawable.calendar_red_sabytie) else button29.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button29.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button29.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button29.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button29.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button29.setBackgroundResource(R.drawable.calendar_post_sabytie) else button29.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button29.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button29.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button29.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button29.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button29.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button29.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button29.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button29.setBackgroundResource(R.drawable.calendar_day_sabytie) else button29.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 30) {
                        if (day == "end") {
                            button30.text = newDay.toString()
                            button30.setBackgroundResource(R.drawable.calendar_day)
                            button30.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button30.text = day
                            if (data[i - 1][4].contains("#d00505")) button30.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button30.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button30.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_sabytie) else button30.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button30.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button30.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button30.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button30.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button30.setBackgroundResource(R.drawable.calendar_red_sabytie) else button30.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button30.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button30.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button30.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button30.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button30.setBackgroundResource(R.drawable.calendar_post_sabytie) else button30.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button30.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button30.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button30.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button30.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button30.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button30.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button30.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button30.setBackgroundResource(R.drawable.calendar_day_sabytie) else button30.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 31) {
                        if (day == "end") {
                            button31.text = newDay.toString()
                            button31.setBackgroundResource(R.drawable.calendar_day)
                            button31.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button31.text = day
                            if (data[i - 1][4].contains("#d00505")) button31.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button31.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button31.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_sabytie) else button31.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button31.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button31.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button31.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button31.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button31.setBackgroundResource(R.drawable.calendar_red_sabytie) else button31.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button31.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button31.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button31.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button31.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button31.setBackgroundResource(R.drawable.calendar_post_sabytie) else button31.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button31.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button31.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button31.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button31.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button31.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button31.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button31.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button31.setBackgroundResource(R.drawable.calendar_day_sabytie) else button31.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 32) {
                        if (day == "end") {
                            button32.text = newDay.toString()
                            button32.setBackgroundResource(R.drawable.calendar_day)
                            button32.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button32.text = day
                            if (data[i - 1][4].contains("#d00505")) button32.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button32.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button32.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_sabytie) else button32.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button32.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button32.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button32.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button32.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button32.setBackgroundResource(R.drawable.calendar_red_sabytie) else button32.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button32.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button32.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button32.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button32.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button32.setBackgroundResource(R.drawable.calendar_post_sabytie) else button32.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button32.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button32.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button32.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button32.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button32.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button32.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button32.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button32.setBackgroundResource(R.drawable.calendar_day_sabytie) else button32.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 33) {
                        if (day == "end") {
                            button33.text = newDay.toString()
                            button33.setBackgroundResource(R.drawable.calendar_day)
                            button33.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button33.text = day
                            if (data[i - 1][4].contains("#d00505")) button33.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button33.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button33.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_sabytie) else button33.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button33.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button33.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button33.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button33.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button33.setBackgroundResource(R.drawable.calendar_red_sabytie) else button33.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button33.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button33.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button33.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button33.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button33.setBackgroundResource(R.drawable.calendar_post_sabytie) else button33.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button33.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button33.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button33.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button33.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button33.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button33.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button33.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button33.setBackgroundResource(R.drawable.calendar_day_sabytie) else button33.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 34) {
                        if (day == "end") {
                            button34.text = newDay.toString()
                            button34.setBackgroundResource(R.drawable.calendar_day)
                            button34.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button34.text = day
                            if (data[i - 1][4].contains("#d00505")) button34.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button34.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button34.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_sabytie) else button34.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button34.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button34.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button34.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button34.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button34.setBackgroundResource(R.drawable.calendar_red_sabytie) else button34.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button34.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button34.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button34.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button34.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button34.setBackgroundResource(R.drawable.calendar_post_sabytie) else button34.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button34.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button34.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button34.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button34.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button34.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button34.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button34.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button34.setBackgroundResource(R.drawable.calendar_day_sabytie) else button34.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 35) {
                        if (day == "end") {
                            button35.text = newDay.toString()
                            button35.setBackgroundResource(R.drawable.calendar_day)
                            button35.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button35.text = day
                            if (data[i - 1][4].contains("#d00505")) button35.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button35.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button35.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_sabytie) else button35.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button35.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button35.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button35.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button35.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button35.setBackgroundResource(R.drawable.calendar_red_sabytie) else button35.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button35.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button35.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button35.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button35.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button35.setBackgroundResource(R.drawable.calendar_post_sabytie) else button35.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button35.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button35.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button35.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button35.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button35.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button35.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button35.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button35.setBackgroundResource(R.drawable.calendar_day_sabytie) else button35.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 36) {
                        if (day == "end") {
                            button36.text = newDay.toString()
                            button36.setBackgroundResource(R.drawable.calendar_bez_posta)
                            button36.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button36.text = day
                            if (data[i - 1][4].contains("#d00505")) button36.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button36.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button36.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_sabytie) else button36.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button36.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button36.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button36.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button36.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button36.setBackgroundResource(R.drawable.calendar_red_sabytie) else button36.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button36.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button36.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button36.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button36.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button36.setBackgroundResource(R.drawable.calendar_post_sabytie) else button36.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button36.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button36.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button36.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button36.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button36.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button36.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button36.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button36.setBackgroundResource(R.drawable.calendar_day_sabytie) else button36.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 37) {
                        if (day == "end") {
                            button37.text = newDay.toString()
                            button37.setBackgroundResource(R.drawable.calendar_day)
                            button37.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            button37.text = day
                            if (data[i - 1][4].contains("#d00505")) button37.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button37.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button37.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_sabytie) else button37.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button37.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button37.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else button37.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else button37.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) button37.setBackgroundResource(R.drawable.calendar_red_sabytie) else button37.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    button37.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    button37.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button37.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button37.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) button37.setBackgroundResource(R.drawable.calendar_post_sabytie) else button37.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button37.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) button37.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else button37.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        button37.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else button37.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) button37.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else button37.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) button37.setBackgroundResource(R.drawable.calendar_day_sabytie) else button37.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 38) {
                        if (day == "end") {
                            button38.text = newDay.toString()
                            button38.setBackgroundResource(R.drawable.calendar_day)
                            button38.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 39) {
                        if (day == "end") {
                            button39.text = newDay.toString()
                            button39.setBackgroundResource(R.drawable.calendar_day)
                            button39.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 40) {
                        if (day == "end") {
                            button40.text = newDay.toString()
                            button40.setBackgroundResource(R.drawable.calendar_day)
                            button40.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 41) {
                        if (day == "end") {
                            button41.text = newDay.toString()
                            button41.setBackgroundResource(R.drawable.calendar_day)
                            button41.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 42) {
                        if (day == "end") {
                            button42.text = newDay.toString()
                            button42.setBackgroundResource(R.drawable.calendar_day)
                            button42.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        activity?.let {
            if (linearLayout.visibility == View.GONE) {
                val intent = Intent()
                when (v?.id ?: 0) {
                    R.id.button1 -> {
                        intent.putExtra("data", pageNumberFull - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button2 -> {
                        intent.putExtra("data", pageNumberFull + 1 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button3 -> {
                        intent.putExtra("data", pageNumberFull + 2 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button4 -> {
                        intent.putExtra("data", pageNumberFull + 3 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button5 -> {
                        intent.putExtra("data", pageNumberFull + 4 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button6 -> {
                        intent.putExtra("data", pageNumberFull + 5 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button7 -> {
                        intent.putExtra("data", pageNumberFull + 6 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button8 -> {
                        intent.putExtra("data", pageNumberFull + 7 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button9 -> {
                        intent.putExtra("data", pageNumberFull + 8 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button10 -> {
                        intent.putExtra("data", pageNumberFull + 9 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button11 -> {
                        intent.putExtra("data", pageNumberFull + 10 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button12 -> {
                        intent.putExtra("data", pageNumberFull + 11 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button13 -> {
                        intent.putExtra("data", pageNumberFull + 12 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button14 -> {
                        intent.putExtra("data", pageNumberFull + 13 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button15 -> {
                        intent.putExtra("data", pageNumberFull + 14 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button16 -> {
                        intent.putExtra("data", pageNumberFull + 15 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button17 -> {
                        intent.putExtra("data", pageNumberFull + 16 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button18 -> {
                        intent.putExtra("data", pageNumberFull + 17 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button19 -> {
                        intent.putExtra("data", pageNumberFull + 18 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button20 -> {
                        intent.putExtra("data", pageNumberFull + 19 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button21 -> {
                        intent.putExtra("data", pageNumberFull + 20 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button22 -> {
                        intent.putExtra("data", pageNumberFull + 21 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button23 -> {
                        intent.putExtra("data", pageNumberFull + 22 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button24 -> {
                        intent.putExtra("data", pageNumberFull + 23 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button25 -> {
                        intent.putExtra("data", pageNumberFull + 24 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button26 -> {
                        intent.putExtra("data", pageNumberFull + 25 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button27 -> {
                        intent.putExtra("data", pageNumberFull + 26 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button28 -> {
                        intent.putExtra("data", pageNumberFull + 27 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button29 -> {
                        intent.putExtra("data", pageNumberFull + 28 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button30 -> {
                        intent.putExtra("data", pageNumberFull + 29 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button31 -> {
                        intent.putExtra("data", pageNumberFull + 30 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button32 -> {
                        intent.putExtra("data", pageNumberFull + 31 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button33 -> {
                        intent.putExtra("data", pageNumberFull + 32 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button34 -> {
                        intent.putExtra("data", pageNumberFull + 33 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button35 -> {
                        intent.putExtra("data", pageNumberFull + 34 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button36 -> {
                        intent.putExtra("data", pageNumberFull + 35 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button37 -> {
                        intent.putExtra("data", pageNumberFull + 36 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button38 -> {
                        intent.putExtra("data", pageNumberFull + 37 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button39 -> {
                        intent.putExtra("data", pageNumberFull + 38 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button40 -> {
                        intent.putExtra("data", pageNumberFull + 39 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button41 -> {
                        intent.putExtra("data", pageNumberFull + 40 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                    R.id.button42 -> {
                        intent.putExtra("data", pageNumberFull + 41 - wik)
                        intent.putExtra("year", year)
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                }
            } else {
                viewId = v?.id ?: 0
                when (viewId) {
                    R.id.button1 -> sabytieView(pageNumberFull - wik)
                    R.id.button2 -> sabytieView(pageNumberFull + 1 - wik)
                    R.id.button3 -> sabytieView(pageNumberFull + 2 - wik)
                    R.id.button4 -> sabytieView(pageNumberFull + 3 - wik)
                    R.id.button5 -> sabytieView(pageNumberFull + 4 - wik)
                    R.id.button6 -> sabytieView(pageNumberFull + 5 - wik)
                    R.id.button7 -> sabytieView(pageNumberFull + 6 - wik)
                    R.id.button8 -> sabytieView(pageNumberFull + 7 - wik)
                    R.id.button9 -> sabytieView(pageNumberFull + 8 - wik)
                    R.id.button10 -> sabytieView(pageNumberFull + 9 - wik)
                    R.id.button11 -> sabytieView(pageNumberFull + 10 - wik)
                    R.id.button12 -> sabytieView(pageNumberFull + 11 - wik)
                    R.id.button13 -> sabytieView(pageNumberFull + 12 - wik)
                    R.id.button14 -> sabytieView(pageNumberFull + 13 - wik)
                    R.id.button15 -> sabytieView(pageNumberFull + 14 - wik)
                    R.id.button16 -> sabytieView(pageNumberFull + 15 - wik)
                    R.id.button17 -> sabytieView(pageNumberFull + 16 - wik)
                    R.id.button18 -> sabytieView(pageNumberFull + 17 - wik)
                    R.id.button19 -> sabytieView(pageNumberFull + 18 - wik)
                    R.id.button20 -> sabytieView(pageNumberFull + 19 - wik)
                    R.id.button21 -> sabytieView(pageNumberFull + 20 - wik)
                    R.id.button22 -> sabytieView(pageNumberFull + 21 - wik)
                    R.id.button23 -> sabytieView(pageNumberFull + 22 - wik)
                    R.id.button24 -> sabytieView(pageNumberFull + 23 - wik)
                    R.id.button25 -> sabytieView(pageNumberFull + 24 - wik)
                    R.id.button26 -> sabytieView(pageNumberFull + 25 - wik)
                    R.id.button27 -> sabytieView(pageNumberFull + 26 - wik)
                    R.id.button28 -> sabytieView(pageNumberFull + 27 - wik)
                    R.id.button29 -> sabytieView(pageNumberFull + 28 - wik)
                    R.id.button30 -> sabytieView(pageNumberFull + 29 - wik)
                    R.id.button31 -> sabytieView(pageNumberFull + 30 - wik)
                    R.id.button32 -> sabytieView(pageNumberFull + 31 - wik)
                    R.id.button33 -> sabytieView(pageNumberFull + 32 - wik)
                    R.id.button34 -> sabytieView(pageNumberFull + 33 - wik)
                    R.id.button35 -> sabytieView(pageNumberFull + 34 - wik)
                    R.id.button36 -> sabytieView(pageNumberFull + 35 - wik)
                    R.id.button37 -> sabytieView(pageNumberFull + 36 - wik)
                    R.id.button38 -> sabytieView(pageNumberFull + 37 - wik)
                    R.id.button39 -> sabytieView(pageNumberFull + 38 - wik)
                    R.id.button40 -> sabytieView(pageNumberFull + 39 - wik)
                    R.id.button41 -> sabytieView(pageNumberFull + 40 - wik)
                    R.id.button42 -> sabytieView(pageNumberFull + 41 - wik)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("viewId", viewId)
        outState.putInt("pageNumberFull", pageNumberFull)
        outState.putInt("wik", wik)
    }

    private fun sabytieView(DayYear: Int) {
        activity?.let {
            linearLayout.removeAllViewsInLayout()
            val gc = Calendar.getInstance() as GregorianCalendar
            var title: String
            val sabytieList = ArrayList<TextViewRobotoCondensed>()
            for (p in MainActivity.padzeia) {
                val r1 = p.dat.split(".").toTypedArray()
                val r2 = p.datK.split(".").toTypedArray()
                gc[r1[2].toInt(), r1[1].toInt() - 1] = r1[0].toInt()
                val naY = gc[Calendar.YEAR]
                val na = gc[Calendar.DAY_OF_YEAR]
                gc[r2[2].toInt(), r2[1].toInt() - 1] = r2[0].toInt()
                val yaerw = gc[Calendar.YEAR]
                val kon = gc[Calendar.DAY_OF_YEAR]
                var rezkK = kon - na + 1
                if (yaerw > naY) {
                    var leapYear = 365
                    if (gc.isLeapYear(naY)) leapYear = 366
                    rezkK = leapYear - na + kon
                }
                gc[r1[2].toInt(), r1[1].toInt() - 1] = r1[0].toInt()
                for (i in 0 until rezkK) {
                    if (gc[Calendar.DAY_OF_YEAR] - 1 == DayYear && gc[Calendar.YEAR] == year) {
                        title = p.padz
                        val data = p.dat
                        val time = p.tim
                        val dataK = p.datK
                        val timeK = p.timK
                        val paz = p.paznic
                        var res = "Паведаміць: Ніколі"
                        if (paz != 0L) {
                            gc.timeInMillis = paz
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            if (gc[Calendar.DATE] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 10) nol2 = "0"
                            if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                            res = "Паведаміць: " + nol1 + gc[Calendar.DAY_OF_MONTH] + "." + nol2 + (gc[Calendar.MONTH] + 1) + "." + gc[Calendar.YEAR] + " у " + gc[Calendar.HOUR_OF_DAY] + ":" + nol3 + gc[Calendar.MINUTE]
                        }
                        val textViewT = TextViewRobotoCondensed(it)
                        textViewT.text = title
                        textViewT.setPadding(20, 10, 10, 10)
                        textViewT.setTypeface(null, Typeface.BOLD)
                        textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        textViewT.setTypeface(null, Typeface.BOLD)
                        textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                        textViewT.setBackgroundColor(Color.parseColor(getColors(it)[p.color]))
                        sabytieList.add(textViewT)
                        val textView = TextViewRobotoCondensed(it)
                        textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        textView.setPadding(20, 0, 10, 10)
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        textView.setBackgroundColor(ContextCompat.getColor(it, R.color.colorDivider))
                        if (dzenNoch) {
                            textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                            textView.setBackgroundResource(R.color.colorprimary_material_dark)
                        }
                        if (data == dataK && time == timeK) {
                            textView.text = resources.getString(R.string.sabytieKali, data, time, res)
                        } else {
                            textView.text = resources.getString(R.string.sabytieDoKuda, data, time, dataK, timeK, res)
                        }
                        sabytieList.add(textView)
                        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        llp.setMargins(0, 0, 0, 10)
                        textView.layoutParams = llp
                    }
                    gc.add(Calendar.DATE, 1)
                }
            }
            if (sabytieList.size > 0) {
                for (i in sabytieList.indices) {
                    linearLayout.addView(sabytieList[i])
                }
            }
        }
    }

    companion object {
        fun newInstance(date: Int, mun: Int, year: Int, position: Int): PageFragmentMonth {
            val fragmentFirst = PageFragmentMonth()
            val args = Bundle()
            args.putInt("date", date)
            args.putInt("mun", mun)
            args.putInt("year", year)
            args.putInt("position", position)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}