package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 9.6.16
 */
class MenuPamiatka : PadryxtoukaPamiatkaListFragment() {
    private lateinit var adapter: MyArrayAdapter
    private var k: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDialogFontSizePositiveClick() {
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            fragmentManager?.let { dialogFontSize.show(it, "font") }
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            fragmentManager?.let { dialogBrightness.show(it, "brightness") }
        }
        if (id == R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            val prefEditor = k?.edit()
            if (item.isChecked) {
                prefEditor?.putBoolean("dzen_noch", true)
            } else {
                prefEditor?.putBoolean("dzen_noch", false)
            }
            prefEditor?.apply()
            activity?.recreate()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        k = activity?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        listView.isVerticalScrollBarEnabled = false
        listView.isHorizontalScrollBarEnabled = false
        val dzenNoch = k?.getBoolean("dzen_noch", false)
        val data = ArrayList<String>()
        val c = Calendar.getInstance()
        var dataP: Int
        val monthP: Int
        val year = c[Calendar.YEAR]
        val a = year % 19
        val b = year % 4
        val cx = year % 7
        val ks = year / 100
        val p = (13 + 8 * ks) / 25
        val q = ks / 4
        val m = (15 - p + ks - q) % 30
        val n = (4 + ks - q) % 7
        val d = (19 * a + m) % 30
        val ex = (2 * b + 4 * cx + 6 * d + n) % 7
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
        var calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        val pashaD = calendar[Calendar.DAY_OF_MONTH]
        val pashaM = calendar[Calendar.MONTH]
        calendar.add(Calendar.DATE, -7)
        val erusalimD = calendar[Calendar.DAY_OF_MONTH]
        val erusalimM = calendar[Calendar.MONTH]
        calendar.add(Calendar.DATE, +46)
        val uznasenneD = calendar[Calendar.DAY_OF_MONTH]
        val uznasenneM = calendar[Calendar.MONTH]
        calendar.add(Calendar.DATE, +10)
        val troicaD = calendar[Calendar.DAY_OF_MONTH]
        val troicaM = calendar[Calendar.MONTH]
        calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        calendar.add(Calendar.DATE, -70)
        val postMytnikND = calendar[Calendar.DAY_OF_MONTH]
        val postMytnikNM = calendar[Calendar.MONTH]
        calendar.add(Calendar.DATE, 7)
        val postMytnikKD = calendar[Calendar.DAY_OF_MONTH]
        val postMytnikKM = calendar[Calendar.MONTH]
        calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        calendar.add(Calendar.DATE, 7)
        val nopostSvetluKD = calendar[Calendar.DAY_OF_MONTH]
        val nopostSvetluKM = calendar[Calendar.MONTH]
        calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        calendar.add(Calendar.DATE, 49)
        val nopostTroicaND = calendar[Calendar.DAY_OF_MONTH]
        val nopostTroicaNM = calendar[Calendar.MONTH]
        calendar.add(Calendar.DATE, 7)
        val nopostTroicaKD = calendar[Calendar.DAY_OF_MONTH]
        val nopostTroicaKM = calendar[Calendar.MONTH]
        calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        calendar.add(Calendar.DATE, 57)
        val postPetrKD = calendar[Calendar.DAY_OF_MONTH]
        val postPetrKM = calendar[Calendar.MONTH]
        calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        calendar.add(Calendar.DATE, -48)
        val postVialikiND = calendar[Calendar.DAY_OF_MONTH]
        val postVialikiNM = calendar[Calendar.MONTH]
        calendar = GregorianCalendar(c[Calendar.YEAR], monthP - 1, dataP)
        calendar.add(Calendar.DATE, -1)
        val postVialikiKD = calendar[Calendar.DAY_OF_MONTH]
        val postVialikiKM = calendar[Calendar.MONTH]
        calendar.add(Calendar.DATE, -1)
        val postVialikaiPiatKD = calendar[Calendar.DAY_OF_MONTH]
        val postVialikaiPiatKM = calendar[Calendar.MONTH]
        if (dzenNoch == true) data.add("<strong><font>ЕЎХАРЫСТЫЧНЫ ПОСТ ПЕРАД СЬВЯТЫМ ПРЫЧАСЬЦЕМ</font></strong>") else data.add("<font color=\"#d00505\"><strong>ЕЎХАРЫСТЫЧНЫ ПОСТ ПЕРАД СЬВЯТЫМ ПРЫЧАСЬЦЕМ</strong></font>")
        data.add("<em>Ня менш за 1 гадзiну перад пачаткам Боскай Літургіі трэба ўстрымацца ад ежы i напояў.</em>")
        data.add("Чыстая вада, а таксама прыём прыпісаных лекаў не забараняецца.")
        if (dzenNoch == true) data.add("<strong>АДЗНАЧЭНЬНЕ СЬВЯТАЎ</strong>") else data.add("<font color=\"#d00505\"><strong>АДЗНАЧЭНЬНЕ СЬВЯТАЎ</strong></font>")
        data.add("Згодна з кан. 880 Кодэксу Канонаў Усходніх Цэркваў вернікі Беларускай Грэка-Каталіцкай Царквы абавязаны сьвяткаваць, акрамя <strong>кожнай нядзелі</strong>, наступныя царкоўныя сьвяты:")
        data.add("<strong>1.</strong> <em>Сьвяты, якія заўсёды ў нядзелю:</em>")
        data.add("<strong>- Уваход Гасподні ў Ерусалім (Вербніца) " + erusalimD + " " + monthName[erusalimM] + ".</strong>")
        data.add("<strong>- Уваскрасеньне Хрыстова (Вялiкдзень) " + pashaD + " " + monthName[pashaM] + ".</strong>")
        data.add("<strong>- Зыход Сьвятога Духа (Тройца) – " + troicaD + " " + monthName[troicaM] + ".</strong>")
        data.add("<strong>2. Богазьяўленьне (Вадохрышча) – 6 студзеня.</strong>")
        data.add("<strong>3. Дабравешчаньне Найсьвяцейшай Багародзiцы – 25 сакавіка.</strong>")
        data.add("<strong>4. Узьнясеньне Гасподняе (Ушэсьце) – " + uznasenneD + " " + monthName[uznasenneM] + ".</strong>")
        data.add("<strong>5. Сьвята Вярхоўных Апосталаў Пятра і Паўла – 29 чэрвеня.</strong>")
        data.add("<strong>6. Усьпеньне Найсьвяцейшай Багародзiцы – 15 жніўня.</strong>")
        data.add("<strong>7. Нараджэньне Хрыстова (Раство) – 25 сьнежня.</strong>")
        data.add("У гэтыя царкоўныя сьвяты і ў нядзелі вернiкi <strong>абавязаны браць удзел у сьв. Лiтургii</strong> i ўстрымлівацца ад цяжкай фiзiчнай працы. ")
        data.add("Ва ўсе iншыя сьвяты сьвятары адпраўляюць сьв. Лiтургiю для тых, якiя змогуць i пажадаюць сьвяткаваць. Аднак Царква таксама асабліва заахвочвае вернікаў браць удзел у набажэнствах наступных сьвятаў: <br><strong>• Абрэзаньне Гасподняе</strong>, <em>1 студзеня</em>;<br><strong>• Перамяненьне Гасподняе</strong>, <em>6 жніўня</em>; <br><strong>• Нараджэньне Найсьвяцейшай Багародзіцы</strong>, <em>8 верасьня</em>;<br><strong>• Узвышэньне Пачэснага Крыжа Гасподняга (Крыжаўзвышэньне)</strong>, <em>14 верасьня</em>.")
        if (dzenNoch == true) data.add("<strong>АБАВЯЗКОВЫЯ ПАСТЫ</strong>") else data.add("<font color=\"#d00505\"><strong>АБАВЯЗКОВЫЯ ПАСТЫ</strong></font>")
        data.add("• <em><strong>Усе пятнiцы</strong> на працягу году вернікі БГКЦ абавязаны ўстрымлiвацца ад мясных страваў.")
        data.add("Посту ў пятніцу няма ў сьвяты Гасподнiя i Багародзiчныя, а таксама ў перыяд:</em>")
        data.add("- ад Раства (25 сьнежня) да Богазьяўленьня (6 студзеня);")
        data.add("- ад Нядзелi мытнiка i фарысея (" + postMytnikND + " " + monthName[postMytnikNM] + ") да Нядзелi блуднага сына (" + postMytnikKD + " " + monthName[postMytnikKM] + ");")
        data.add("- ад Вялiкадня (" + pashaD + " " + monthName[pashaM] + ") да Нядзелi Тамаша (" + nopostSvetluKD + " " + monthName[nopostSvetluKM] + ");")
        data.add("- ад Тройцы (" + nopostTroicaND + " " + monthName[nopostTroicaNM] + ") да Нядзелi ўсiх сьвятых (" + nopostTroicaKD + " " + monthName[nopostTroicaKM] + ").")
        data.add("<em>• Асаблівы час, калі вернікі прыкладаюць узмоцненыя намаганьні для свайго духоўнага росту, абмяжоўваюць сябе ў ежы, а таксама ўстрымліваюцца ад арганiзацыi публiчных забаў з музыкай i танцамi:</em>")
        data.add("<strong>- Вялiкi пост і Вялікі тыдзень:</strong><em>" + postVialikiND + " " + monthName[postVialikiNM] + " – " + postVialikiKD + " " + monthName[postVialikiKM] + ";</em>")
        data.add("<strong>- Пятроўскі пост:</strong><em>" + postPetrKD + " " + monthName[postPetrKM] + " - 28 чэрвеня;</em>")
        data.add("<strong>- Усьпенскі пост:</strong><em> 1 жніўня - 14 жніўня;</em>")
        data.add("<strong>- Калядны пост (Пiлiпаўка):</strong><em> 15 лістапада – 24 сьнежня.</em>")
        data.add("<strong>Грэка-католiкi абавязаны:</strong>")
        data.add("• <em>Устрымлiвацца ад мясных i малочных страваў:</em>")
        data.add("- у першы дзень Вялiкага посту (" + postVialikiND + " " + monthName[postVialikiNM] + ");")
        data.add("- у Вялiкую пятнiцу (" + postVialikaiPiatKD + " " + monthName[postVialikaiPiatKM] + ").")
        data.add("• <em>Устрымлiвацца ад мясных страваў i абмяжоўвацца адным пасілкам у дзень:</em>")
        data.add("- у Сьвяты вечар перад Раством (24 сьнежня);")
        data.add("- у Сьвяты вечар перад Богазьяўленьнем (5 студзеня);")
        data.add("- на Ўзвышэньне сьв. Крыжа (14 верасьня);")
        data.add("- на Адсячэньне галавы сьв. Яна Хрысьцiцеля (29 жніўня).")
        if (dzenNoch == true) data.add("<strong><font>АД ПОСТУ ЗВОЛЬНЕНЫЯ:</font></strong>") else data.add("<font color=\"#d00505\"><strong>АД ПОСТУ ЗВОЛЬНЕНЫЯ:</strong></font>")
        data.add("- дзецi да 14 гадоў i тыя, чый узрост больш за 60 гадоў;")
        data.add("- хворыя фiзiчна i душэўна, цяжарныя жанчыны, а таксама тыя, што кормяць грудзьмi;")
        data.add("- тыя, што выздараўлiваюць пасьля цяжкай хваробы;")
        data.add("- тыя, што не распараджаюцца сабой у поўнай меры (напрыклад, тыя, што жывуць у чужых; зьнябожаныя; тыя, што жывуць з ахвяраваньня i г. д.)")
        data.add("Таксама бiскуп i парахi могуць звольнiць верніка ад посту дзеля нейкiх важкiх прычынаў. Спаведнiк можа гэта зрабiць у спавядальнi.")
        activity?.let {
            adapter = MyArrayAdapter(it, data)
            listAdapter = adapter
            listView.divider = null
            val pad = (10 * resources.displayMetrics.density).toInt()
            listView.setPadding(pad, pad, pad, pad)
        }
    }

    private class MyArrayAdapter(private val activity: Activity, private val list: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_maranata, list) {
        override fun isEnabled(position: Int): Boolean {
            return false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val ea: ExpArrayAdapterParallelItems
            if (convertView == null) {
                ea = ExpArrayAdapterParallelItems()
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_maranata, parent, false)
                ea.textView = rootView.findViewById(R.id.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ExpArrayAdapterParallelItems
            }
            ea.textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            ea.textView?.text = MainActivity.fromHtml(list[position])
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
            }
            return rootView
        }

    }

    private class ExpArrayAdapterParallelItems {
        var textView: TextViewRobotoCondensed? = null
    }
}