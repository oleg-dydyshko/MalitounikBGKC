package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.Sabytie.Companion.getColors
import by.carkva_gazeta.malitounik.databinding.CalendarMunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PageFragmentMonth : Fragment() {
    private var wik = 0
    private var date = 0
    private var mun = 0
    private var year = 0
    private var pageNumberFull = 0
    private var dzenNoch = false
    private lateinit var data: ArrayList<ArrayList<String>>
    private val padzei = ArrayList<Padzeia>()
    private var _binding: CalendarMunBinding? = null
    private val binding get() = _binding!!

    private fun sabytieOn() {
        if (!CaliandarMun.SabytieOnView) {
            binding.linearLayout.visibility = View.GONE
        } else {
            binding.linearLayout.visibility = View.VISIBLE
            val c = GregorianCalendar(year, mun, date)
            sabytieView(c[Calendar.DAY_OF_YEAR] - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = arguments?.getInt("date") ?: 0
        mun = arguments?.getInt("mun") ?: 0
        year = arguments?.getInt("year") ?: SettingsActivity.GET_CALIANDAR_YEAR_MIN
        data = MenuCaliandar.getDataCalaindar(mun = mun, year = year)
        for (p in MainActivity.padzeia) {
            val r1 = p.dat.split(".")
            val munL = r1[1].toInt() - 1
            val yaer = r1[2].toInt()
            if (munL == mun && yaer == year) {
                padzei.add(p)
            }
        }
    }

    private fun sabytieCheck(day: Int): Boolean {
        var sabytie = false
        for (p in padzei) {
            val r1 = p.dat.split(".")
            val date = r1[0].toInt()
            if (date == day) {
                sabytie = true
                break
            }
        }
        return sabytie
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalendarMunBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getTextView(position: Int): TextView {
        var view = binding.button1
        when (position) {
            1 -> view = binding.button1
            2 -> view = binding.button2
            3 -> view = binding.button3
            4 -> view = binding.button4
            5 -> view = binding.button5
            6 -> view = binding.button6
            7 -> view = binding.button7
            8 -> view = binding.button8
            9 -> view = binding.button9
            10 -> view = binding.button10
            11 -> view = binding.button11
            12 -> view = binding.button12
            13 -> view = binding.button13
            14 -> view = binding.button14
            15 -> view = binding.button15
            16 -> view = binding.button16
            17 -> view = binding.button17
            18 -> view = binding.button18
            19 -> view = binding.button19
            20 -> view = binding.button20
            21 -> view = binding.button21
            22 -> view = binding.button22
            23 -> view = binding.button23
            24 -> view = binding.button24
            25 -> view = binding.button25
            26 -> view = binding.button26
            27 -> view = binding.button27
            28 -> view = binding.button28
            29 -> view = binding.button29
            30 -> view = binding.button30
            31 -> view = binding.button31
            32 -> view = binding.button32
            33 -> view = binding.button33
            34 -> view = binding.button34
            35 -> view = binding.button35
            36 -> view = binding.button36
            37 -> view = binding.button37
            38 -> view = binding.button38
            39 -> view = binding.button39
            40 -> view = binding.button40
            41 -> view = binding.button41
            42 -> view = binding.button42
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        CoroutineScope(Dispatchers.Main).launch {
            activity?.let { activity ->
                val chin = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                sabytieOn()
                dzenNoch = chin.getBoolean("dzen_noch", false)
                if (dzenNoch) {
                    binding.textView2.setBackgroundResource(R.drawable.calendar_red_black)
                    binding.button1.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    binding.button8.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    binding.button15.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    binding.button22.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    binding.button29.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    binding.button36.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                }
                if (CaliandarMun.SabytieOnView) {
                    binding.linearLayout.visibility = View.VISIBLE
                }
                val c = Calendar.getInstance() as GregorianCalendar
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
                var end = 42
                if (42 - (munAll + wik) >= 6) {
                    binding.TableRow.visibility = View.GONE
                    end -= 7
                }
                if (munAll + wik == 29) {
                    binding.TableRowPre.visibility = View.GONE
                    end -= 7
                }
                for (e in 1..end) {
                    var denNedeli: Int
                    if (e < wik) {
                        oldDay++
                        day = "start"
                    } else if (e < munAll + wik) {
                        i++
                        day = i.toString()
                        nopost = data[i - 1][7].toInt() == 1
                        post = data[i - 1][7].toInt() == 2
                        strogiPost = data[i - 1][7].toInt() == 3
                        if (data[i - 1][5].toInt() == 1 || data[i - 1][5].toInt() == 2) nopost = false
                    } else {
                        newDay++
                        day = "end"
                    }
                    val calendarPost = GregorianCalendar(year, mun, i)
                    when (day) {
                        "start" -> {
                            getTextView(e).text = oldDay.toString()
                            if (e == 1) getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta)
                            else getTextView(e).setBackgroundResource(R.drawable.calendar_day)
                            getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                            getTextView(e).setOnClickListener {
                                val intent = Intent()
                                if (binding.linearLayout.visibility == View.GONE) {
                                    val position = data[0][25].toInt() - (wik - e)
                                    intent.putExtra("position", position)
                                    activity.setResult(Activity.RESULT_OK, intent)
                                    activity.finish()
                                } else {
                                    sabytieView(pageNumberFull + (e - 1) - wik)
                                }
                            }
                        }
                        "end" -> {
                            getTextView(e).text = newDay.toString()
                            getTextView(e).setBackgroundResource(R.drawable.calendar_day)
                            getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                            getTextView(e).setOnClickListener {
                                val intent = Intent()
                                if (binding.linearLayout.visibility == View.GONE) {
                                    val text = (it as TextView).text.toString().toInt()
                                    val position = data[data.size - 1][25].toInt() + text
                                    intent.putExtra("position", position)
                                    activity.setResult(Activity.RESULT_OK, intent)
                                    activity.finish()
                                } else {
                                    sabytieView(pageNumberFull + (e - 1) - wik)
                                }
                            }
                        }
                        else -> {
                            getTextView(e).setOnClickListener {
                                val intent = Intent()
                                if (binding.linearLayout.visibility == View.GONE) {
                                    val text = (it as TextView).text.toString().toInt()
                                    val position = data[text - 1][25].toInt()
                                    intent.putExtra("position", position)
                                    activity.setResult(Activity.RESULT_OK, intent)
                                    activity.finish()
                                } else {
                                    sabytieView(pageNumberFull + (e - 1) - wik)
                                }
                            }
                            val sab = sabytieCheck(i)
                            getTextView(e).text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) getTextView(e).typeface = MainActivity.createFont(Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_today_sabytie_black)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie_today)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie_black)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                                    getTextView(e).typeface = MainActivity.createFont(Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_today_sabytie_black)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie_today)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie_black)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                                    getTextView(e).typeface = MainActivity.createFont(Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today)
                                        else getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta_today)
                                        else if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta_sabytie)
                                        else getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_post_sabytie_today)
                                        else getTextView(e).setBackgroundResource(R.drawable.calendar_post_today)
                                        else if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_post_sabytie)
                                        else getTextView(e).setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else getTextView(e).setBackgroundResource(R.drawable.calendar_strogi_post_today)
                                        else if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_strogi_post_sabytie)
                                        else getTextView(e).setBackgroundResource(R.drawable.calendar_strogi_post)
                                        getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta_today)
                                            else if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta_sabytie)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_day_sabytie_today)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_day_today)
                                            else if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_day_sabytie)
                                            else getTextView(e).setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sabytieView(DayYear: Int) {
        activity?.let {
            binding.linearLayout.removeAllViewsInLayout()
            val gc = Calendar.getInstance() as GregorianCalendar
            var title: String
            val sabytieList = ArrayList<TextView>()
            for (index in 0 until MainActivity.padzeia.size) {
                val p = MainActivity.padzeia[index]
                val r1 = p.dat.split(".")
                val r2 = p.datK.split(".")
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
                        var res = getString(R.string.sabytie_no_pavedam)
                        val konecSabytie = p.konecSabytie
                        val realTime = Calendar.getInstance().timeInMillis
                        var paznicia = false
                        if (paz != 0L) {
                            gc.timeInMillis = paz
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            if (gc[Calendar.DATE] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                            res = getString(R.string.sabytie_pavedam, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR], gc[Calendar.HOUR_OF_DAY], nol3, gc[Calendar.MINUTE])
                            if (realTime > paz) paznicia = true
                        }
                        val textViewT = TextView(it)
                        textViewT.text = title
                        textViewT.setPadding(20, 10, 10, 10)
                        textViewT.typeface = MainActivity.createFont(Typeface.BOLD)
                        textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(getColors(it)[p.color]))
                        sabytieList.add(textViewT)
                        val textView = TextView(it)
                        textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        textView.setPadding(20, 0, 10, 10)
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        textView.setBackgroundColor(ContextCompat.getColor(it, R.color.colorDivider))
                        if (dzenNoch) {
                            textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                            textView.setBackgroundResource(R.color.colorprimary_material_dark)
                        }
                        val textR = if (!konecSabytie) {
                            getString(R.string.sabytieKali, data, time, res)
                        } else {
                            getString(R.string.sabytieDoKuda, data, time, dataK, timeK, res)
                        }
                        val t1 = textR.lastIndexOf("\n")
                        val spannable = SpannableString(textR.substring(0, t1))
                        val t3 = spannable.indexOf(res)
                        if (dzenNoch) {
                            if (paznicia) spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary_black)), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        } else {
                            if (paznicia) spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary)), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        val font = MainActivity.createFont(Typeface.NORMAL)
                        spannable.setSpan(CustomTypefaceSpan("", font), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        textView.text = spannable
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
                    binding.linearLayout.addView(sabytieList[i])
                }
            }
        }
    }

    companion object {
        fun newInstance(date: Int, mun: Int, year: Int): PageFragmentMonth {
            val fragmentFirst = PageFragmentMonth()
            val args = Bundle()
            args.putInt("date", date)
            args.putInt("mun", mun)
            args.putInt("year", year)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}