package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.Sabytie.Companion.getColors
import by.carkva_gazeta.malitounik.databinding.CalendarMunBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        year = arguments?.getInt("year") ?: 0
        val position = arguments?.getInt("position") ?: 0
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
        data.addAll(gson.fromJson(getData(position), type))
        for (p in MainActivity.padzeia) {
            val r1 = p.dat.split(".")
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
            val inputStream = resources.openRawResource(MainActivity.getCaliandarResource(mun))
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            builder = reader.readText()
        }
        return builder
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            activity?.let { it ->
                val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                sabytieOn()
                dzenNoch = chin.getBoolean("dzen_noch", false)
                if (dzenNoch) {
                    binding.textView2.setBackgroundResource(R.drawable.calendar_red_black)
                    binding.button1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    binding.button8.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    binding.button15.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    binding.button22.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    binding.button29.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    binding.button36.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                }
                binding.button1.setOnClickListener(this@PageFragmentMonth)
                binding.button2.setOnClickListener(this@PageFragmentMonth)
                binding.button3.setOnClickListener(this@PageFragmentMonth)
                binding.button4.setOnClickListener(this@PageFragmentMonth)
                binding.button5.setOnClickListener(this@PageFragmentMonth)
                binding.button6.setOnClickListener(this@PageFragmentMonth)
                binding.button7.setOnClickListener(this@PageFragmentMonth)
                binding.button8.setOnClickListener(this@PageFragmentMonth)
                binding.button9.setOnClickListener(this@PageFragmentMonth)
                binding.button10.setOnClickListener(this@PageFragmentMonth)
                binding.button11.setOnClickListener(this@PageFragmentMonth)
                binding.button12.setOnClickListener(this@PageFragmentMonth)
                binding.button13.setOnClickListener(this@PageFragmentMonth)
                binding.button14.setOnClickListener(this@PageFragmentMonth)
                binding.button15.setOnClickListener(this@PageFragmentMonth)
                binding.button16.setOnClickListener(this@PageFragmentMonth)
                binding.button17.setOnClickListener(this@PageFragmentMonth)
                binding.button18.setOnClickListener(this@PageFragmentMonth)
                binding.button19.setOnClickListener(this@PageFragmentMonth)
                binding.button20.setOnClickListener(this@PageFragmentMonth)
                binding.button21.setOnClickListener(this@PageFragmentMonth)
                binding.button22.setOnClickListener(this@PageFragmentMonth)
                binding.button23.setOnClickListener(this@PageFragmentMonth)
                binding.button24.setOnClickListener(this@PageFragmentMonth)
                binding.button25.setOnClickListener(this@PageFragmentMonth)
                binding.button26.setOnClickListener(this@PageFragmentMonth)
                binding.button27.setOnClickListener(this@PageFragmentMonth)
                binding.button28.setOnClickListener(this@PageFragmentMonth)
                binding.button29.setOnClickListener(this@PageFragmentMonth)
                binding.button30.setOnClickListener(this@PageFragmentMonth)
                binding.button31.setOnClickListener(this@PageFragmentMonth)
                binding.button32.setOnClickListener(this@PageFragmentMonth)
                binding.button33.setOnClickListener(this@PageFragmentMonth)
                binding.button34.setOnClickListener(this@PageFragmentMonth)
                binding.button35.setOnClickListener(this@PageFragmentMonth)
                binding.button36.setOnClickListener(this@PageFragmentMonth)
                binding.button37.setOnClickListener(this@PageFragmentMonth)
                binding.button38.setOnClickListener(this@PageFragmentMonth)
                binding.button39.setOnClickListener(this@PageFragmentMonth)
                binding.button40.setOnClickListener(this@PageFragmentMonth)
                binding.button41.setOnClickListener(this@PageFragmentMonth)
                binding.button42.setOnClickListener(this@PageFragmentMonth)
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
                    if (42 - (munAll + wik) >= 6) binding.TableRow.visibility = View.GONE
                    if (munAll + wik == 29) binding.TableRowPre.visibility = View.GONE
                    val calendarPost = GregorianCalendar(year, mun, i)
                    if (e == 1) {
                        if (day == "start") {
                            binding.button1.text = oldDay.toString()
                            binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta)
                            binding.button1.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button1.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button1.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button1.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button1.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button1.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button1.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button1.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button1.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button1.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button1.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button1.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button1.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button1.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button1.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 2) {
                        if (day == "start") {
                            binding.button2.text = oldDay.toString()
                            binding.button2.setBackgroundResource(R.drawable.calendar_day)
                            binding.button2.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button2.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button2.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button2.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button2.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button2.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button2.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button2.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button2.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button2.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button2.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button2.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button2.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button2.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button2.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 3) {
                        if (day == "start") {
                            binding.button3.text = oldDay.toString()
                            binding.button3.setBackgroundResource(R.drawable.calendar_day)
                            binding.button3.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button3.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button3.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button3.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button3.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button3.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button3.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button3.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button3.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button3.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button3.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button3.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button3.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button3.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button3.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 4) {
                        if (day == "start") {
                            binding.button4.text = oldDay.toString()
                            binding.button4.setBackgroundResource(R.drawable.calendar_day)
                            binding.button4.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button4.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button4.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button4.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button4.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button4.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button4.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button4.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button4.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button4.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button4.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button4.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button4.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button4.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button4.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 5) {
                        if (day == "start") {
                            binding.button5.text = oldDay.toString()
                            binding.button5.setBackgroundResource(R.drawable.calendar_day)
                            binding.button5.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button5.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button5.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button5.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button5.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button5.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button5.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button5.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button5.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button5.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button5.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button5.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button5.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button5.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button5.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 6) {
                        if (day == "start") {
                            binding.button6.text = oldDay.toString()
                            binding.button6.setBackgroundResource(R.drawable.calendar_day)
                            binding.button6.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button6.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button6.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button6.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button6.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button6.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button6.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button6.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button6.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button6.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button6.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button6.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button6.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button6.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button6.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 7) {
                        val sab = sabytieCheck(i)
                        binding.button7.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button7.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button7.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button7.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button7.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button7.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button7.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button7.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button7.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button7.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button7.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button7.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button7.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button7.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 8) {
                        val sab = sabytieCheck(i)
                        binding.button8.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button8.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button8.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button8.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button8.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button8.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button8.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button8.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button8.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button8.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button8.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button8.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button8.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button8.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 9) {
                        val sab = sabytieCheck(i)
                        binding.button9.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button9.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button9.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button9.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button9.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button9.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button9.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button9.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button9.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button9.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button9.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button9.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button9.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button9.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 10) {
                        val sab = sabytieCheck(i)
                        binding.button10.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button10.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button10.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button10.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button10.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button10.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button10.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button10.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button10.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button10.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button10.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button10.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button10.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button10.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 11) {
                        val sab = sabytieCheck(i)
                        binding.button11.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button11.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button11.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button11.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button11.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button11.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button11.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button11.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button11.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button11.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button11.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button11.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button11.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button11.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 12) {
                        val sab = sabytieCheck(i)
                        binding.button12.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button12.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button12.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button12.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button12.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button12.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button12.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button12.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button12.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button12.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button12.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button12.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button12.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button12.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 13) {
                        val sab = sabytieCheck(i)
                        binding.button13.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button13.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button13.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button13.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button13.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button13.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button13.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button13.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button13.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button13.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button13.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button13.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button13.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button13.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 14) {
                        val sab = sabytieCheck(i)
                        binding.button14.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button14.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button14.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button14.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button14.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button14.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button14.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button14.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button14.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button14.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button14.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button14.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button14.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button14.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 15) {
                        val sab = sabytieCheck(i)
                        binding.button15.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button15.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button15.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button15.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button15.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button15.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button15.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button15.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button15.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button15.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button15.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button15.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button15.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button15.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 16) {
                        val sab = sabytieCheck(i)
                        binding.button16.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button16.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button16.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button16.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button16.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button16.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button16.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button16.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button16.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button16.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button16.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button16.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button16.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button16.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 17) {
                        val sab = sabytieCheck(i)
                        binding.button17.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button17.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button17.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button17.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button17.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button17.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button17.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button17.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button17.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button17.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button17.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button17.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button17.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button17.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 18) {
                        val sab = sabytieCheck(i)
                        binding.button18.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button18.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button18.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button18.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button18.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button18.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button18.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button18.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button18.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button18.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button18.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button18.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button18.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button18.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 19) {
                        val sab = sabytieCheck(i)
                        binding.button19.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button19.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button19.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button19.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button19.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button19.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button19.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button19.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button19.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button19.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button19.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button19.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button19.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button19.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 20) {
                        val sab = sabytieCheck(i)
                        binding.button20.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button20.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button20.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button20.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button20.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button20.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button20.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button20.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button20.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button20.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button20.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button20.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button20.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button20.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 21) {
                        val sab = sabytieCheck(i)
                        binding.button21.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button21.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button21.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button21.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button21.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button21.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button21.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button21.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button21.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button21.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button21.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button21.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button21.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button21.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 22) {
                        val sab = sabytieCheck(i)
                        binding.button22.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button22.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button22.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button22.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button22.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button22.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button22.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button22.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button22.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button22.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button22.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button22.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button22.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button22.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 23) {
                        val sab = sabytieCheck(i)
                        binding.button23.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button23.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button23.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button23.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button23.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button23.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button23.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button23.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button23.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button23.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button23.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button23.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button23.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button23.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 24) {
                        val sab = sabytieCheck(i)
                        binding.button24.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button24.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button24.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button24.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button24.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button24.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button24.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button24.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button24.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button24.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button24.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button24.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button24.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button24.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 25) {
                        val sab = sabytieCheck(i)
                        binding.button25.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button25.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button25.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button25.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button25.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button25.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button25.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button25.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button25.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button25.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button25.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button25.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button25.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button25.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 26) {
                        val sab = sabytieCheck(i)
                        binding.button26.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button26.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button26.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button26.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button26.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button26.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button26.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button26.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button26.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button26.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button26.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button26.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button26.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button26.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 27) {
                        val sab = sabytieCheck(i)
                        binding.button27.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button27.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button27.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button27.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button27.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button27.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button27.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button27.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button27.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button27.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button27.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button27.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button27.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button27.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 28) {
                        val sab = sabytieCheck(i)
                        binding.button28.text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button28.setTypeface(null, Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button28.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button28.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button28.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button28.setTypeface(null, Typeface.BOLD)
                            }
                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (dzenNoch) {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button28.setBackgroundResource(R.drawable.calendar_red_today_black)
                                    } else {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_red_today)
                                    }
                                } else {
                                    if (dzenNoch) {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button28.setBackgroundResource(R.drawable.calendar_red_black)
                                    } else {
                                        if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_red)
                                    }
                                }
                                binding.button28.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                binding.button28.setTypeface(null, Typeface.NORMAL)
                            }
                            else -> {
                                if (nopost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta)
                                }
                                if (post) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_post)
                                }
                                if (strogiPost) {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_strogi_post)
                                    binding.button28.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                }
                                if (!nopost && !post && !strogiPost) {
                                    denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                    if (denNedeli == 1) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    } else {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button28.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button28.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button28.setBackgroundResource(R.drawable.calendar_day)
                                    }
                                }
                            }
                        }
                    }
                    if (e == 29) {
                        if (day == "end") {
                            binding.button29.text = newDay.toString()
                            binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta)
                            binding.button29.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button29.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button29.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button29.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button29.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button29.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button29.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button29.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button29.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button29.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button29.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button29.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button29.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button29.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button29.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 30) {
                        if (day == "end") {
                            binding.button30.text = newDay.toString()
                            binding.button30.setBackgroundResource(R.drawable.calendar_day)
                            binding.button30.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button30.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button30.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button30.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button30.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button30.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button30.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button30.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button30.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button30.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button30.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button30.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button30.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button30.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button30.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 31) {
                        if (day == "end") {
                            binding.button31.text = newDay.toString()
                            binding.button31.setBackgroundResource(R.drawable.calendar_day)
                            binding.button31.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button31.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button31.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button31.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button31.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button31.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button31.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button31.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button31.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button31.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button31.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button31.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button31.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button31.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button31.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 32) {
                        if (day == "end") {
                            binding.button32.text = newDay.toString()
                            binding.button32.setBackgroundResource(R.drawable.calendar_day)
                            binding.button32.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button32.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button32.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button32.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button32.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button32.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button32.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button32.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button32.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button32.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button32.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button32.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button32.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button32.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button32.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 33) {
                        if (day == "end") {
                            binding.button33.text = newDay.toString()
                            binding.button33.setBackgroundResource(R.drawable.calendar_day)
                            binding.button33.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button33.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button33.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button33.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button33.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button33.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button33.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button33.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button33.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button33.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button33.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button33.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button33.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button33.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button33.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 34) {
                        if (day == "end") {
                            binding.button34.text = newDay.toString()
                            binding.button34.setBackgroundResource(R.drawable.calendar_day)
                            binding.button34.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button34.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button34.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button34.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button34.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button34.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button34.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button34.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button34.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button34.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button34.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button34.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button34.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button34.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button34.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 35) {
                        if (day == "end") {
                            binding.button35.text = newDay.toString()
                            binding.button35.setBackgroundResource(R.drawable.calendar_day)
                            binding.button35.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button35.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button35.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button35.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button35.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button35.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button35.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button35.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button35.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button35.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button35.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button35.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button35.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button35.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button35.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 36) {
                        if (day == "end") {
                            binding.button36.text = newDay.toString()
                            binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta)
                            binding.button36.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button36.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button36.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button36.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button36.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button36.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button36.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button36.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button36.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button36.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button36.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button36.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button36.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button36.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button36.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 37) {
                        if (day == "end") {
                            binding.button37.text = newDay.toString()
                            binding.button37.setBackgroundResource(R.drawable.calendar_day)
                            binding.button37.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        } else {
                            val sab = sabytieCheck(i)
                            binding.button37.text = day
                            if (data[i - 1][4].contains("<font color=#d00505><strong>")) binding.button37.setTypeface(null, Typeface.BOLD)
                            when (data[i - 1][5].toInt()) {
                                1 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button37.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button37.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button37.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button37.setTypeface(null, Typeface.BOLD)
                                }
                                2 -> {
                                    if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                        if (dzenNoch) {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_today_sabytie_black) else binding.button37.setBackgroundResource(R.drawable.calendar_red_today_black)
                                        } else {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_red_today)
                                        }
                                    } else {
                                        if (dzenNoch) {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_sabytie_black) else binding.button37.setBackgroundResource(R.drawable.calendar_red_black)
                                        } else {
                                            if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_red_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_red)
                                        }
                                    }
                                    binding.button37.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    binding.button37.setTypeface(null, Typeface.NORMAL)
                                }
                                else -> {
                                    if (nopost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta)
                                    }
                                    if (post) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_post_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_post_today) else if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_post_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_post)
                                    }
                                    if (strogiPost) {
                                        if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_strogi_post_today) else if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_strogi_post_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_strogi_post)
                                        binding.button37.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                                    }
                                    if (!nopost && !post && !strogiPost) {
                                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                                        if (denNedeli == 1) {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta_today) else if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_bez_posta)
                                        } else {
                                            if (c[Calendar.DAY_OF_MONTH] == i && munTudey) if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_day_sabytie_today) else binding.button37.setBackgroundResource(R.drawable.calendar_day_today) else if (sab) binding.button37.setBackgroundResource(R.drawable.calendar_day_sabytie) else binding.button37.setBackgroundResource(R.drawable.calendar_day)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (e == 38) {
                        if (day == "end") {
                            binding.button38.text = newDay.toString()
                            binding.button38.setBackgroundResource(R.drawable.calendar_day)
                            binding.button38.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 39) {
                        if (day == "end") {
                            binding.button39.text = newDay.toString()
                            binding.button39.setBackgroundResource(R.drawable.calendar_day)
                            binding.button39.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 40) {
                        if (day == "end") {
                            binding.button40.text = newDay.toString()
                            binding.button40.setBackgroundResource(R.drawable.calendar_day)
                            binding.button40.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 41) {
                        if (day == "end") {
                            binding.button41.text = newDay.toString()
                            binding.button41.setBackgroundResource(R.drawable.calendar_day)
                            binding.button41.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                        }
                    }
                    if (e == 42) {
                        if (day == "end") {
                            binding.button42.text = newDay.toString()
                            binding.button42.setBackgroundResource(R.drawable.calendar_day)
                            binding.button42.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
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
            if (binding.linearLayout.visibility == View.GONE) {
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

    private fun sabytieView(DayYear: Int) {
        activity?.let {
            binding.linearLayout.removeAllViewsInLayout()
            val gc = Calendar.getInstance() as GregorianCalendar
            var title: String
            val sabytieList = ArrayList<TextViewRobotoCondensed>()
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
                        val textViewT = TextViewRobotoCondensed(it)
                        textViewT.text = title
                        textViewT.setPadding(20, 10, 10, 10)
                        textViewT.setTypeface(null, Typeface.BOLD)
                        textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        textViewT.setTypeface(null, Typeface.BOLD)
                        textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(getColors(it)[p.color]))
                        sabytieList.add(textViewT)
                        val textView = TextViewRobotoCondensed(it)
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