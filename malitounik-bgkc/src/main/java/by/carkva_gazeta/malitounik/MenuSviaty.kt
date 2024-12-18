package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SimpleListItemSviatyBinding
import java.util.Calendar
import java.util.GregorianCalendar

class MenuSviaty : BaseListFragment() {
    private var year = Calendar.getInstance()[Calendar.YEAR]
    private lateinit var mListener: CarkvaCarkvaListener
    private var mLastClickTime: Long = 0
    private lateinit var myArrayAdapter: MyArrayAdapter
    private val list = ArrayList<Prazdniki>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("year", year)
    }

    fun getCviatyYear() = year

    fun setCviatyYear(year: Int) {
        this.year = year
        list.clear()
        list.addAll(getPrazdnik(year))
        activity?.let {
            if (SettingsActivity.GET_CALIANDAR_YEAR_MAX >= year) {
                listView.isClickable = true
                val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
                if (dzenNoch) {
                    listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
                } else {
                    listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_default)
                }
            } else {
                listView.isClickable = false
                listView.selector = ContextCompat.getDrawable(it, android.R.color.transparent)
            }
        }
        myArrayAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.isVerticalScrollBarEnabled = false
        listView.isHorizontalScrollBarEnabled = false
        activity?.let {
            if (savedInstanceState != null) {
                year = savedInstanceState.getInt("year")
                list.addAll(getPrazdnik(year))
            } else {
                list.addAll(getPrazdnik())
            }
            myArrayAdapter = MyArrayAdapter(it, list)
            listAdapter = myArrayAdapter
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                listView.setBackgroundResource(R.color.colorbackground_material_dark)
                listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            }
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val c = GregorianCalendar()
        val dayOfYear = if (!c.isLeapYear(year) && list[position].dayOfYear > 59) list[position].dayOfYear + 1
        else list[position].dayOfYear
        if (SettingsActivity.GET_CALIANDAR_YEAR_MAX >= year) mListener.setDataCalendar(dayOfYear, year)
    }

    internal interface CarkvaCarkvaListener {
        fun setDataCalendar(dayOfYear: Int, year: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as CarkvaCarkvaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement CarkvaCarkvaListener")
            }
        }
    }

    private class MyArrayAdapter(private val context: Activity, private val list: ArrayList<Prazdniki>) : ArrayAdapter<Prazdniki>(context, R.layout.simple_list_item_sviaty, list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val ea: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemSviatyBinding.inflate(context.layoutInflater, parent, false)
                rootView = binding.root
                ea = ViewHolder(binding.group, binding.title, binding.date)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                ea.title.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary_black))
            }
            val title = SpannableString(list[position].opisanie)
            val data = SpannableStringBuilder()
            val eaGroup = SpannableStringBuilder()
            when (list[position].svaity) {
                -1 -> {
                    title.setSpan(StyleSpan(Typeface.BOLD), 0, list[position].opisanie.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                -2 -> {
                    eaGroup.append(context.getString(R.string.dvunad_sv))
                    ea.group.visibility = View.VISIBLE
                }
                -3 -> {
                    eaGroup.append(context.getString(R.string.vial_sv))
                    ea.group.visibility = View.VISIBLE
                }
                -4 -> {
                    eaGroup.append(context.getString(R.string.dni_yspamin_pam))
                    ea.group.visibility = View.VISIBLE
                }
                -5 -> {
                    eaGroup.append(context.getString(R.string.carkva_pamiat_data))
                    ea.group.visibility = View.VISIBLE
                }
                -6 -> {
                    eaGroup.append(context.getString(R.string.parafia_sv))
                    ea.group.visibility = View.VISIBLE
                } else -> {
                    ea.group.visibility = View.GONE
                }
            }
            if (list[position].svaity in 4..6 || list[position].svaity in -4 downTo -6) {
                if (dzenNoch) title.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorWhite)), 0, list[position].opisanie.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                else title.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_text)), 0, list[position].opisanie.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val t1 = list[position].opisanie.indexOf(":")
                if (t1 != -1) {
                    title.setSpan(StyleSpan(Typeface.BOLD), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else if (list[position].svaity == 2 || list[position].svaity == -2) {
                title.setSpan(StyleSpan(Typeface.BOLD), 0, list[position].opisanie.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                if (dzenNoch) title.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_black)), 0, list[position].opisanie.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                else title.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, list[position].opisanie.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (dzenNoch) eaGroup.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_black)), 0, eaGroup.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            else eaGroup.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, eaGroup.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ea.group.text = eaGroup
            data.append(list[position].opisanieData)
            ea.title.text = title
            ea.date.text = data
            return rootView
        }

    }

    private class ViewHolder(var group: TextView, var title: TextView, var date: TextView)

    companion object {
        fun getPrazdnik(yearG: Int = Calendar.getInstance().get(Calendar.YEAR), search: Boolean = false): ArrayList<Prazdniki> {
            val a = yearG % 19
            val b = yearG % 4
            val cx = yearG % 7
            val ks = yearG / 100
            val p = (13 + 8 * ks) / 25
            val q = ks / 4
            val m = (15 - p + ks - q) % 30
            val n = (4 + ks - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            val monthP: Int
            var dataP: Int
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            val monthName = Malitounik.applicationContext().resources.getStringArray(R.array.meciac_smoll)
            val nedelName = Malitounik.applicationContext().resources.getStringArray(R.array.dni_nedeli)
            val prazdnikiAll = ArrayList<Prazdniki>()
            val prazdnik = ArrayList<Prazdniki>()
            val c = GregorianCalendar(yearG, monthP - 1, dataP)
            prazdnikiAll.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], -1, Malitounik.applicationContext().resources.getString(R.string.S1), c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + " " + yearG + " году, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.JANUARY, 6)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], -2, Malitounik.applicationContext().resources.getString(R.string.S2), "6 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, 1, 2)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S3), "2 лютага, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.MARCH, 25)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S4), "25 сакавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            c.add(Calendar.DATE, -7)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S5), c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.add(Calendar.DATE, 46)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S6), c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.add(Calendar.DATE, 10)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S7), c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.AUGUST, 6)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S8), "6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.AUGUST, 15)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S9), "15 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.SEPTEMBER, 8)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S10), "8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.SEPTEMBER, 14)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S11), "14 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.NOVEMBER, 21)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S12), "21 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.DECEMBER, 25)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 2, Malitounik.applicationContext().resources.getString(R.string.S13), "25 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.sort()
            prazdnikiAll.addAll(prazdnik)
            prazdnik.clear()
            c.set(yearG, Calendar.JANUARY, 1)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], -3, Malitounik.applicationContext().resources.getString(R.string.S14), "1 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.JUNE, 24)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 3, Malitounik.applicationContext().resources.getString(R.string.S15), "24 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.JUNE, 29)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 3, Malitounik.applicationContext().resources.getString(R.string.S16), "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.AUGUST, 29)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 3, Malitounik.applicationContext().resources.getString(R.string.S17), "29 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.OCTOBER, 1)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 3, Malitounik.applicationContext().resources.getString(R.string.S18), "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.sort()
            prazdnikiAll.addAll(prazdnik)
            prazdnik.clear()
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -57)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], -4, "Мясапусная задушная субота", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -50)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Успамін усіх сьвятых айцоў, манахаў і посьнікаў", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -29)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Субота 3-га тыдня Вялікага посту", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -22)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Субота 4-га тыдня Вялікага посту", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 9)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Радаўніца", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 48)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Траецкая бацькоўская субота", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            for (i in 19..25) {
                c.set(yearG, Calendar.OCTOBER, i)
                val dayofweek = c[Calendar.DAY_OF_WEEK]
                if (7 == dayofweek) {
                    prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Зьмітраўская бацькоўская субота", c[Calendar.DATE].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
                }
            }
            c.set(yearG, Calendar.NOVEMBER, 2)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 4, "Дзяды, дзень успаміну памёрлых", "2 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.sort()
            prazdnikiAll.addAll(prazdnik)
            prazdnik.clear()
            if (!search) {
                c.set(yearG, Calendar.JULY, 11)
                prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], -5, "Успамін мучаніцкай сьмерці ў катэдры сьв. Сафіі ў Полацку 5 манахаў-базыльянаў", "11 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
                c.set(yearG, Calendar.SEPTEMBER, 15)
                prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 5, "Успамін Бабровіцкай трагедыі (зьнішчэньне ў 1942 г. жыхароў уніяцкай парафіі в. Бабровічы Івацэвіцкага р-ну)", "15 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
                c.set(yearG, Calendar.OCTOBER, 18)
                prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 5, "Успамін Берасьцейскай царкоўнай Уніі 1596 году", "18(8) кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
                prazdnik.sort()
                prazdnikiAll.addAll(prazdnik)
                prazdnik.clear()
            }
            c.set(yearG, Calendar.JANUARY, 30)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], -6, "Гомель: Трох Сьвяціцеляў", "30 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Антвэрпан: Уваскрасеньня Хрыстовага", c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Віцебск: Уваскрасеньня Хрыстовага", c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.MARCH, 28)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Пінск: сьвятога Кірылы Тураўскага", "28 красавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.MAY, 1)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Маладэчна: Хрыста Чалавекалюбцы", "1 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.MAY, 7)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Івацэвічы: Маці Божай Жыровіцкай", "7 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.MAY, 11)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Баранавічы: сьвятых роўнаапостальных Кірылы і Мятода", "11 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.MAY, 13)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Горадня: Маці Божай Фацімскай", "13 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(c[Calendar.YEAR], monthP - 1, dataP)
            c.add(Calendar.DATE, 56)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Слонім: Сьвятой Тройцы", c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.add(Calendar.DATE, 1)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Менск: Сьвятога Духа", c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.JUNE, 27)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Менск: Маці Божай Нястомнай Дапамогі", "27 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.JUNE, 29)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Лондан: сьвятых апосталаў Пятра і Паўла", "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Жодзіна: сьвятых апосталаў Пятра і Паўла", "29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            val chisla = intArrayOf(24, 25, 26, 27, 28, 29, 30)
            var brest = 24
            for (aChisla in chisla) {
                val cal = GregorianCalendar(yearG, 5, aChisla)
                val deyNed = cal[Calendar.DAY_OF_WEEK]
                if (deyNed == Calendar.SATURDAY) brest = aChisla
            }
            c.set(yearG, Calendar.JUNE, brest)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Берасьце: сьвятых братоў-апосталаў Пятра і Андрэя", c[Calendar.DAY_OF_MONTH].toString() + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.JULY, 24)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Наваградак: сьв. Барыса і Глеба", "24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Полацак: манастыр сьв. Барыса і Глеба", "24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.AUGUST, 6)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Заслаўе: Перамяненьня Гасподняга", "6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.SEPTEMBER, 8)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Магілёў: Бялыніцкай іконы Маці Божай", "8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.SEPTEMBER, 16)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Ліда: сьвятамучаніка Язафата Полацкага", "16 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.OCTOBER, 1)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Ворша: Покрыва Найсьвяцейшай Багародзіцы", "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Мар’іна Горка: Покрыва Найсьвяцейшай Багародзіцы", "1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.NOVEMBER, 8)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Барысаў: сьвятога Арханёла Міхаіла", "8 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.NOVEMBER, 12)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Полацак: сьвятамучаніка Язафата", "12 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            c.set(yearG, Calendar.DECEMBER, 6)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Менск: сьвятога Мікалая Цудатворцы", "6 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            val dayofweekrastvo = GregorianCalendar(yearG, Calendar.DECEMBER, 25)[Calendar.DAY_OF_WEEK]
            val chislaJiazep = intArrayOf(26, 27, 28, 29, 30, 31)
            var minsk = 26
            for (aChisla in chislaJiazep) {
                val deyNed = GregorianCalendar(yearG, Calendar.DECEMBER, aChisla)[Calendar.DAY_OF_WEEK]
                if (dayofweekrastvo != Calendar.SUNDAY) {
                    if (deyNed == Calendar.SUNDAY) minsk = aChisla
                } else {
                    if (deyNed == Calendar.MONDAY) minsk = aChisla
                }
            }
            c.set(yearG, Calendar.DECEMBER, minsk)
            prazdnik.add(Prazdniki(c[Calendar.DAY_OF_YEAR], c[Calendar.DATE], c[Calendar.MONTH], 6, "Менск: праведнага Язэпа", c[Calendar.DATE].toString() + " сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]]))
            prazdnik.sort()
            prazdnikiAll.addAll(prazdnik)
            return prazdnikiAll
        }
    }
}
