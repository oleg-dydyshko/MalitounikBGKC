package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import java.util.*

/**
 * Created by oleg on 31.5.16
 */
class MenuCviaty : ListFragment() {
    private var year = Calendar.getInstance()[Calendar.YEAR]
    private lateinit var mListener: CarkvaCarkvaListener
    private var mLastClickTime: Long = 0
    private lateinit var myArrayAdapter: MyArrayAdapter
    private var list = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("year", year)
    }

    fun setCviatyYear(year: Int) {
        this.year = year
        list = getPrazdnik(activity, year)
        myArrayAdapter.notifyDataSetChanged()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.isVerticalScrollBarEnabled = false
        listView.isHorizontalScrollBarEnabled = false
        activity?.let {
            if (savedInstanceState != null) {
                year = savedInstanceState.getInt("year")
                list = getPrazdnik(activity, year)
            } else {
                list = getPrazdnik(activity)
            }
            myArrayAdapter = MyArrayAdapter(it)
            listAdapter = myArrayAdapter
        }
        val pad = (10 * resources.displayMetrics.density).toInt()
        listView.setPadding(pad, pad, pad, pad)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (SettingsActivity.GET_CALIANDAR_YEAR_MAX >= year) mListener.setDataCalendar(data[position], year)
    }

    internal interface CarkvaCarkvaListener {
        fun setDataCalendar(day_of_year: Int, year: Int)
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

    private inner class MyArrayAdapter internal constructor(private val context: Activity) : ArrayAdapter<String>(context, R.layout.simple_list_item_sviaty, list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val ea: ViewHolder
            if (convertView == null) {
                ea = ViewHolder()
                rootView = context.layoutInflater.inflate(R.layout.simple_list_item_sviaty, parent, false)
                ea.textView = rootView.findViewById(R.id.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            //CaseInsensitiveResourcesFontLoader fontLoader = new CaseInsensitiveResourcesFontLoader();
            ea.textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            ea.textView?.text = MainActivity.fromHtml(list[position])
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            }
            return rootView
        }

    }

    private class ViewHolder {
        var textView: TextViewRobotoCondensed? = null
    }

    companion object {
        var opisanie: ArrayList<String> = ArrayList()
        private var data: ArrayList<Int> = ArrayList()

        fun getPrazdnik(context: Context?, yearG: Int = Calendar.getInstance().get(Calendar.YEAR)): ArrayList<String> {
            val builder = ArrayList<String>()
            data = ArrayList()
            opisanie = ArrayList()
            var c: GregorianCalendar
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
            val monthName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня",
                    "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
            val nedelName = arrayOf("", "нядзеля", "панядзелак", "аўторак", "серада", "чацьвер", "пятніца", "субота")
            val k = context?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k?.getBoolean("dzen_noch", false)
            val color: String
            color = if (dzenNoch == true) "<font color=\"#f44336\">" else "<font color=\"#d00505\">"
            var prazdnik = emptyArray<Prazdniki>()
            c = GregorianCalendar(yearG, 0, 6)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Богазьяўленьне (Вадохрышча)</font>", "<br><strong><em>6 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + " </strong></em>")
            c = GregorianCalendar(yearG, 1, 2)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Сустрэча Госпада нашага Ісуса Хрыста (Грамніцы)</font>", "<br><strong><em>2 лютага, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 2, 25)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Дабравешчаньне</font>", "<br><strong><em>25 сакавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            var calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
            calendar.add(Calendar.DATE, -7)
            prazdnik += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + calendar[Calendar.DATE] + ":" + calendar[Calendar.MONTH] + "--><!--1-->" + color + "Уваход Гасподні ў Ерусалім (Вербніца)</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            calendar.add(Calendar.DATE, 46)
            prazdnik += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + calendar[Calendar.DATE] + ":" + calendar[Calendar.MONTH] + "--><!--1-->" + color + "Узьнясеньне Гасподняе (Ушэсьце)</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            calendar.add(Calendar.DATE, 10)
            prazdnik += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + calendar[Calendar.DATE] + ":" + calendar[Calendar.MONTH] + "--><!--1-->" + color + "Зыход Сьвятога Духа (Тройца)</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 7, 6)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Перамяненьне Гасподняе (Спас)</font>", "<br><strong><em>6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 7, 15)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Усьпеньне Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>15 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 8, 8)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Нараджэньне Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 8, 14)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Крыжаўзвышэньне</font>", "<br><strong><em>14 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 10, 21)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Уваход у Храм Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>21 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 11, 25)
            prazdnik += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->" + color + "Нараджэньне Хрыстова (Каляды)</font>", "<br><strong><em>25 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            Arrays.sort(prazdnik)
            var prazdnikV = emptyArray<Prazdniki>()
            c = GregorianCalendar(yearG, 0, 1)
            prazdnikV += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--2-->" + color + "Абрэзаньне Гасподняе</font>", "<br><strong><em>1 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 5, 24)
            prazdnikV += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--2-->" + color + "Нараджэньне сьв. Яна Прадвесьніка і Хрысьціцеля</font>", "<br><strong><em>24 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 5, 29)
            prazdnikV += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--2-->" + color + "Сьвятых вярхоўных апосталаў Пятра і Паўла</font>", "<br><strong><em>29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 7, 29)
            prazdnikV += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--2-->" + color + "Адсячэньне галавы сьв. Яна Прадвесьніка і Хрысьціцеля</font>", "<br><strong><em>29 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 9, 1)
            prazdnikV += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--2-->" + color + "Покрыва Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            //c = new GregorianCalendar(yearG, 11, 9);
//prazdnikV[5] = new prazdniki(c.get(Calendar.DAY_OF_YEAR), "<!--" + c.get(Calendar.DATE) + ":" + c.get(Calendar.MONTH) + "--><!--2-->" + color + "Беззаганнае Зачацьце Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>9 сьнежня, " + NedelName[c.get(Calendar.DAY_OF_WEEK)] + "</strong></em>");
            Arrays.sort(prazdnikV)
            var prazdnikPamer = emptyArray<Prazdniki>()
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -57)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Мясапусная бацькоўская субота", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -50)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Успамін усіх сьвятых айцоў, манахаў і посьнікаў", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -29)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Субота 3-га тыдня Вялікага посту", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, -22)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Субота 4-га тыдня Вялікага посту", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 9)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Радаўніца", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            c.add(Calendar.DATE, 48)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Траецкая бацькоўская субота", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            for (i in 19..25) {
                c = GregorianCalendar(yearG, 9, i)
                val dayofweek = c[Calendar.DAY_OF_WEEK]
                if (7 == dayofweek) {
                    prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Зьмітраўская бацькоўская субота", "<br><strong><em>" + c[Calendar.DATE] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
                }
            }
            c = GregorianCalendar(yearG, 10, 2)
            prazdnikPamer += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Дзяды, дзень успаміну памёрлых", "<br><strong><em>2 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            Arrays.sort(prazdnikPamer)
            var prazdnikU = emptyArray<Prazdniki>()
            c = GregorianCalendar(yearG, 6, 11)
            prazdnikU += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Успамін мучаніцкай сьмерці ў катэдры сьв. Сафіі ў Полацку 5 манахаў-базыльянаў", "<br><strong><em>11 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 8, 15)
            prazdnikU += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Успамін Бабровіцкай трагедыі (зьнішчэньне ў 1942 г. жыхароў уніяцкай парафіі в. Бабровічы Івацэвіцкага р-ну)", "<br><strong><em>15 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 9, 8)
            prazdnikU += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--3-->Успамін Берасьцейскай царкоўнай Уніі 1596 году", "<br><strong><em>8(18) кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            Arrays.sort(prazdnikU)
            var prazdnikP = emptyArray<Prazdniki>()
            c = GregorianCalendar(yearG, 0, 30)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Гомель: Трох Сьвяціцеляў</font>", "<br><strong><em>30 студзеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
            prazdnikP += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Антвэрпан: Уваскрасеньня Хрыстовага</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            prazdnikP += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Віцебск: Уваскрасеньня Хрыстовага</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 3, 28)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Пінск: сьвятога Кірылы Тураўскага</font>", "<br><strong><em>28 красавіка, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 4, 1)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Маладэчна: Хрыста Чалавекалюбцы</font>", "<br><strong><em>1 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 4, 7)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Івацэвічы: Маці Божай Жыровіцкай</font>", "<br><strong><em>7 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 4, 11)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Баранавічы: сьвятых роўнаапостальных Кірылы і Мятода</font>", "<br><strong><em>11 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 4, 13)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Горадня: Маці Божай Фацімскай</font>", "<br><strong><em>13 траўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
            calendar.add(Calendar.DATE, 56)
            prazdnikP += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Слонім: Сьвятой Тройцы</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            calendar.add(Calendar.DATE, 1)
            prazdnikP += Prazdniki(calendar[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Менск: Сьвятога Духа</font>", "<br><strong><em>" + calendar[Calendar.DAY_OF_MONTH] + " " + monthName[calendar[Calendar.MONTH]] + ", " + nedelName[calendar[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 5, 27)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Менск: Маці Божай Нястомнай Дапамогі</font>", "<br><strong><em>27 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 5, 29)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Лондан: сьвятых апосталаў Пятра і Паўла</font>", "<br><strong><em>29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Жодзіна: сьвятых апосталаў Пятра і Паўла</font>", "<br><strong><em>29 чэрвеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            // Вылічым калі выпадает парафіяльнае свята ў Брэсте
            val chisla = intArrayOf(24, 25, 26, 27, 28, 29, 30)
            var brest = 24
            for (aChisla in chisla) {
                val cal = GregorianCalendar(c[Calendar.YEAR], 5, aChisla)
                val deyNed = cal[Calendar.DAY_OF_WEEK]
                if (deyNed == 7) brest = aChisla
            }
            c = GregorianCalendar(yearG, 5, brest)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Берасьце: сьвятых братоў-апосталаў Пятра і Андрэя</font>", "<br><strong><em>" + c[Calendar.DAY_OF_MONTH] + " " + monthName[c[Calendar.MONTH]] + ", " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 6, 24)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Наваградак: сьв. Барыса і Глеба</font>", "<br><strong><em>24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Полацак: манастыр сьв. Барыса і Глеба</font>", "<br><strong><em>24 ліпеня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 7, 6)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Заслаўе: Перамяненьня Гасподняга</font>", "<br><strong><em>6 жніўня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 8, 8)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Магілёў: Бялыніцкай іконы Маці Божай</font>", "<br><strong><em>8 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 8, 16)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Ліда: сьвятамучаніка Язафата Полацкага</font>", "<br><strong><em>16 верасьня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 9, 1)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Ворша: Покрыва Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Мар’іна Горка: Покрыва Найсьвяцейшай Багародзіцы</font>", "<br><strong><em>1 кастрычніка, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 10, 8)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Барысаў: сьвятога Арханёла Міхаіла</font>", "<br><strong><em>8 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 10, 12)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Полацак: сьвятамучаніка Язафата</font>", "<br><strong><em>12 лістапада, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 11, 6)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Менск: сьвятога Мікалая Цудатворцы</font>", "<br><strong><em>6 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            c = GregorianCalendar(yearG, 11, 27)
            prazdnikP += Prazdniki(c[Calendar.DAY_OF_YEAR], "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "-->" + color + "Менск: праведнага Язэпа</font>", "<br><strong><em>27 сьнежня, " + nedelName[c[Calendar.DAY_OF_WEEK]] + "</strong></em>")
            Arrays.sort(prazdnikP)
            c = GregorianCalendar(yearG, monthP - 1, dataP)
            var pasha = color + "<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><strong>ПАСХА ХРЫСТОВА (ВЯЛІКДЗЕНЬ)</strong></font><br><strong><em>" + dataP + " " + monthName[monthP - 1] + " " + yearG + " году, " + nedelName[1] + "</strong></em>"
            builder.add(pasha)
            data.add(c[Calendar.DAY_OF_YEAR])
            opisanie.add("<!--" + c[Calendar.DATE] + ":" + c[Calendar.MONTH] + "--><!--1-->ПАСХА ХРЫСТОВА (ВЯЛІКДЗЕНЬ)")
            pasha = "$color<strong>ДВУНАДЗЯСЯТЫЯ СЬВЯТЫ</strong></font><br><br>"
            var one = 0
            for (aPrazdnik in prazdnik) {
                if (one == 0) builder.add(pasha + aPrazdnik.opisanie + aPrazdnik.opisanieData) else builder.add(aPrazdnik.opisanie + aPrazdnik.opisanieData)
                data.add(aPrazdnik.data)
                opisanie.add(aPrazdnik.opisanie)
                one++
            }
            pasha = "$color<strong>ВЯЛІКІЯ СЬВЯТЫ</strong></font><br><br>"
            one = 0
            for (aPrazdnikV in prazdnikV) {
                if (one == 0) builder.add(pasha + aPrazdnikV.opisanie + aPrazdnikV.opisanieData) else builder.add(aPrazdnikV.opisanie + aPrazdnikV.opisanieData)
                data.add(aPrazdnikV.data)
                opisanie.add(aPrazdnikV.opisanie)
                one++
            }
            pasha = "$color<strong>ДНІ ЎСПАМІНУ ПАМЁРЛЫХ</strong></font><br><br>"
            one = 0
            for (pPrazdnik in prazdnikPamer) {
                if (one == 0) builder.add(pasha + pPrazdnik.opisanie + pPrazdnik.opisanieData) else builder.add(pPrazdnik.opisanie + pPrazdnik.opisanieData)
                data.add(pPrazdnik.data)
                opisanie.add(pPrazdnik.opisanie)
                one++
            }
            pasha = "$color<strong>ЦАРКОЎНЫЯ ПАМЯТНЫЯ ДАТЫ</strong></font><br><br>"
            one = 0
            for (aPrazdnikU in prazdnikU) {
                if (one == 0) builder.add(pasha + aPrazdnikU.opisanie + aPrazdnikU.opisanieData) else builder.add(aPrazdnikU.opisanie + aPrazdnikU.opisanieData)
                data.add(aPrazdnikU.data)
                opisanie.add(aPrazdnikU.opisanie)
                one++
            }
            pasha = "$color<strong>ПАРАФІЯЛЬНЫЯ СЬВЯТЫ</strong></font><br><br>"
            one = 0
            for (aPrazdnikP in prazdnikP) {
                if (one == 0) builder.add(pasha + aPrazdnikP.opisanie + aPrazdnikP.opisanieData) else builder.add(aPrazdnikP.opisanie + aPrazdnikP.opisanieData)
                data.add(aPrazdnikP.data)
                opisanie.add(aPrazdnikP.opisanie)
                one++
            }
            return builder
        }
    }
}