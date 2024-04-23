package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.CalendarMunBinding
import java.util.Calendar
import java.util.GregorianCalendar

class PageFragmentMonth : BaseFragment() {
    private var wik = 0
    private var date = 0
    private var mun = 0
    private var year = 0
    private var pageNumberFull = 0
    private val data = ArrayList<ArrayList<String>>()
    private val padzei = ArrayList<Padzeia>()
    private var _binding: CalendarMunBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = arguments?.getInt("date") ?: 0
        mun = arguments?.getInt("mun") ?: 0
        year = arguments?.getInt("year") ?: SettingsActivity.GET_CALIANDAR_YEAR_MIN
        data.addAll(MenuCaliandar.getDataCalaindar(mun = mun, year = year))
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
        if (day == 0) return false
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
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            val c = Calendar.getInstance()
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
                binding.TableRow.visibility = View.INVISIBLE
                end -= 7
            }
            if (munAll + wik == 29) {
                binding.TableRowPre.visibility = View.INVISIBLE
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
                    i = 0
                }
                val calendarPost = GregorianCalendar(year, mun, i)
                val sab = sabytieCheck(i)
                when (day) {
                    "start" -> {
                        getTextView(e).text = oldDay.toString()
                        if (e == 1) getTextView(e).setBackgroundResource(R.drawable.calendar_bez_posta)
                        else getTextView(e).setBackgroundResource(R.drawable.calendar_day)
                        getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                        getTextView(e).setOnClickListener {
                            val intent = Intent()
                            if (!CaliandarMun.SabytieOnView) {
                                val position = data[0][25].toInt() - (wik - e)
                                intent.putExtra("position", position)
                                activity.setResult(Activity.RESULT_OK, intent)
                                activity.finish()
                            } else if (sab) {
                                val dialogSabytieShowInMun = DialogSabytieShowInMun.getInstance(pageNumberFull + (e - 1) - wik, year)
                                dialogSabytieShowInMun.show(childFragmentManager, "dialogSabytieShowInMun")
                            }
                        }
                    }

                    "end" -> {
                        getTextView(e).text = newDay.toString()
                        getTextView(e).setBackgroundResource(R.drawable.calendar_day)
                        getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorSecondary_text))
                        getTextView(e).setOnClickListener {
                            val intent = Intent()
                            if (!CaliandarMun.SabytieOnView) {
                                val text = (it as TextView).text.toString().toInt()
                                val position = data[data.size - 1][25].toInt() + text
                                intent.putExtra("position", position)
                                activity.setResult(Activity.RESULT_OK, intent)
                                activity.finish()
                            } else if (sab) {
                                val dialogSabytieShowInMun = DialogSabytieShowInMun.getInstance(pageNumberFull + (e - 1) - wik, year)
                                dialogSabytieShowInMun.show(childFragmentManager, "dialogSabytieShowInMun")
                            }
                        }
                    }

                    else -> {
                        getTextView(e).setOnClickListener {
                            val intent = Intent()
                            if (!CaliandarMun.SabytieOnView) {
                                val text = (it as TextView).text.toString().toInt()
                                val position = data[text - 1][25].toInt()
                                intent.putExtra("position", position)
                                activity.setResult(Activity.RESULT_OK, intent)
                                activity.finish()
                            } else if (sab) {
                                val dialogSabytieShowInMun = DialogSabytieShowInMun.getInstance(pageNumberFull + (e - 1) - wik, year)
                                dialogSabytieShowInMun.show(childFragmentManager, "dialogSabytieShowInMun")
                            }
                        }
                        getTextView(e).text = day
                        if (data[i - 1][4].contains("<font color=#d00505><strong>")) getTextView(e).typeface = MainActivity.createFont(Typeface.BOLD)
                        when (data[i - 1][5].toInt()) {
                            1 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie_today)
                                    else getTextView(e).setBackgroundResource(R.drawable.calendar_red_today)
                                } else {
                                    if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie)
                                    else getTextView(e).setBackgroundResource(R.drawable.calendar_red)
                                }
                                getTextView(e).setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                                getTextView(e).typeface = MainActivity.createFont(Typeface.BOLD)
                            }

                            2 -> {
                                if (c[Calendar.DAY_OF_MONTH] == i && munTudey) {
                                    if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie_today)
                                    else getTextView(e).setBackgroundResource(R.drawable.calendar_red_today)
                                } else {
                                    if (sab) getTextView(e).setBackgroundResource(R.drawable.calendar_red_sabytie)
                                    else getTextView(e).setBackgroundResource(R.drawable.calendar_red)
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
                                    if (denNedeli == Calendar.SUNDAY) {
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