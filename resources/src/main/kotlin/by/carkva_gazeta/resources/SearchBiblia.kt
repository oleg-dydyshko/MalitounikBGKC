package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem4Binding
import by.carkva_gazeta.resources.databinding.SearchBibliaBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class SearchBiblia : BaseActivity(), View.OnClickListener, DialogClearHishory.DialogClearHistoryListener {
    private var seash = ArrayList<Spannable>()
    private lateinit var adapter: SearchBibliaListAdaprer
    private lateinit var prefEditors: Editor
    private lateinit var chin: SharedPreferences
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var actionExpandOn = false
    private var fierstPosition = 0
    private var sinodalBible = ArrayMap<String, Int>()
    private var semuxaBible = ArrayMap<String, Int>()
    private var searche = false
    private lateinit var binding: SearchBibliaBinding
    private var keyword = false
    private var edittext2Focus = false

    init {
        sinodalBible["sinaidals1"] = R.raw.sinaidals1
        sinodalBible["sinaidals2"] = R.raw.sinaidals2
        sinodalBible["sinaidals3"] = R.raw.sinaidals3
        sinodalBible["sinaidals4"] = R.raw.sinaidals4
        sinodalBible["sinaidals5"] = R.raw.sinaidals5
        sinodalBible["sinaidals6"] = R.raw.sinaidals6
        sinodalBible["sinaidals7"] = R.raw.sinaidals7
        sinodalBible["sinaidals8"] = R.raw.sinaidals8
        sinodalBible["sinaidals9"] = R.raw.sinaidals9
        sinodalBible["sinaidals10"] = R.raw.sinaidals10
        sinodalBible["sinaidals11"] = R.raw.sinaidals11
        sinodalBible["sinaidals12"] = R.raw.sinaidals12
        sinodalBible["sinaidals13"] = R.raw.sinaidals13
        sinodalBible["sinaidals14"] = R.raw.sinaidals14
        sinodalBible["sinaidals15"] = R.raw.sinaidals15
        sinodalBible["sinaidals16"] = R.raw.sinaidals16
        sinodalBible["sinaidals17"] = R.raw.sinaidals17
        sinodalBible["sinaidals18"] = R.raw.sinaidals18
        sinodalBible["sinaidals19"] = R.raw.sinaidals19
        sinodalBible["sinaidals20"] = R.raw.sinaidals20
        sinodalBible["sinaidals21"] = R.raw.sinaidals21
        sinodalBible["sinaidals22"] = R.raw.sinaidals22
        sinodalBible["sinaidals23"] = R.raw.sinaidals23
        sinodalBible["sinaidals24"] = R.raw.sinaidals24
        sinodalBible["sinaidals25"] = R.raw.sinaidals25
        sinodalBible["sinaidals26"] = R.raw.sinaidals26
        sinodalBible["sinaidals27"] = R.raw.sinaidals27
        sinodalBible["sinaidals28"] = R.raw.sinaidals28
        sinodalBible["sinaidals29"] = R.raw.sinaidals29
        sinodalBible["sinaidals30"] = R.raw.sinaidals30
        sinodalBible["sinaidals31"] = R.raw.sinaidals31
        sinodalBible["sinaidals32"] = R.raw.sinaidals32
        sinodalBible["sinaidals33"] = R.raw.sinaidals33
        sinodalBible["sinaidals34"] = R.raw.sinaidals34
        sinodalBible["sinaidals35"] = R.raw.sinaidals35
        sinodalBible["sinaidals36"] = R.raw.sinaidals36
        sinodalBible["sinaidals37"] = R.raw.sinaidals37
        sinodalBible["sinaidals38"] = R.raw.sinaidals38
        sinodalBible["sinaidals39"] = R.raw.sinaidals39
        sinodalBible["sinaidals40"] = R.raw.sinaidals40
        sinodalBible["sinaidals41"] = R.raw.sinaidals41
        sinodalBible["sinaidals42"] = R.raw.sinaidals42
        sinodalBible["sinaidals43"] = R.raw.sinaidals43
        sinodalBible["sinaidals44"] = R.raw.sinaidals44
        sinodalBible["sinaidals45"] = R.raw.sinaidals45
        sinodalBible["sinaidals46"] = R.raw.sinaidals46
        sinodalBible["sinaidals47"] = R.raw.sinaidals47
        sinodalBible["sinaidals48"] = R.raw.sinaidals48
        sinodalBible["sinaidals49"] = R.raw.sinaidals49
        sinodalBible["sinaidals50"] = R.raw.sinaidals50
        sinodalBible["sinaidaln1"] = R.raw.sinaidaln1
        sinodalBible["sinaidaln2"] = R.raw.sinaidaln2
        sinodalBible["sinaidaln3"] = R.raw.sinaidaln3
        sinodalBible["sinaidaln4"] = R.raw.sinaidaln4
        sinodalBible["sinaidaln5"] = R.raw.sinaidaln5
        sinodalBible["sinaidaln6"] = R.raw.sinaidaln6
        sinodalBible["sinaidaln7"] = R.raw.sinaidaln7
        sinodalBible["sinaidaln8"] = R.raw.sinaidaln8
        sinodalBible["sinaidaln9"] = R.raw.sinaidaln9
        sinodalBible["sinaidaln10"] = R.raw.sinaidaln10
        sinodalBible["sinaidaln11"] = R.raw.sinaidaln11
        sinodalBible["sinaidaln12"] = R.raw.sinaidaln12
        sinodalBible["sinaidaln13"] = R.raw.sinaidaln13
        sinodalBible["sinaidaln14"] = R.raw.sinaidaln14
        sinodalBible["sinaidaln15"] = R.raw.sinaidaln15
        sinodalBible["sinaidaln16"] = R.raw.sinaidaln16
        sinodalBible["sinaidaln17"] = R.raw.sinaidaln17
        sinodalBible["sinaidaln18"] = R.raw.sinaidaln18
        sinodalBible["sinaidaln19"] = R.raw.sinaidaln19
        sinodalBible["sinaidaln20"] = R.raw.sinaidaln20
        sinodalBible["sinaidaln21"] = R.raw.sinaidaln21
        sinodalBible["sinaidaln22"] = R.raw.sinaidaln22
        sinodalBible["sinaidaln23"] = R.raw.sinaidaln23
        sinodalBible["sinaidaln24"] = R.raw.sinaidaln24
        sinodalBible["sinaidaln25"] = R.raw.sinaidaln25
        sinodalBible["sinaidaln26"] = R.raw.sinaidaln26
        sinodalBible["sinaidaln27"] = R.raw.sinaidaln27
        semuxaBible["biblias1"] = R.raw.biblias1
        semuxaBible["biblias2"] = R.raw.biblias2
        semuxaBible["biblias3"] = R.raw.biblias3
        semuxaBible["biblias4"] = R.raw.biblias4
        semuxaBible["biblias5"] = R.raw.biblias5
        semuxaBible["biblias6"] = R.raw.biblias6
        semuxaBible["biblias7"] = R.raw.biblias7
        semuxaBible["biblias8"] = R.raw.biblias8
        semuxaBible["biblias9"] = R.raw.biblias9
        semuxaBible["biblias10"] = R.raw.biblias10
        semuxaBible["biblias11"] = R.raw.biblias11
        semuxaBible["biblias12"] = R.raw.biblias12
        semuxaBible["biblias13"] = R.raw.biblias13
        semuxaBible["biblias14"] = R.raw.biblias14
        semuxaBible["biblias15"] = R.raw.biblias15
        semuxaBible["biblias16"] = R.raw.biblias16
        semuxaBible["biblias17"] = R.raw.biblias17
        semuxaBible["biblias18"] = R.raw.biblias18
        semuxaBible["biblias19"] = R.raw.biblias19
        semuxaBible["biblias20"] = R.raw.biblias20
        semuxaBible["biblias21"] = R.raw.biblias21
        semuxaBible["biblias22"] = R.raw.biblias22
        semuxaBible["biblias23"] = R.raw.biblias23
        semuxaBible["biblias24"] = R.raw.biblias24
        semuxaBible["biblias25"] = R.raw.biblias25
        semuxaBible["biblias26"] = R.raw.biblias26
        semuxaBible["biblias27"] = R.raw.biblias27
        semuxaBible["biblias28"] = R.raw.biblias28
        semuxaBible["biblias29"] = R.raw.biblias29
        semuxaBible["biblias30"] = R.raw.biblias30
        semuxaBible["biblias31"] = R.raw.biblias31
        semuxaBible["biblias32"] = R.raw.biblias32
        semuxaBible["biblias33"] = R.raw.biblias33
        semuxaBible["biblias34"] = R.raw.biblias34
        semuxaBible["biblias35"] = R.raw.biblias35
        semuxaBible["biblias36"] = R.raw.biblias36
        semuxaBible["biblias37"] = R.raw.biblias37
        semuxaBible["biblias38"] = R.raw.biblias38
        semuxaBible["biblias39"] = R.raw.biblias39
        semuxaBible["biblian1"] = R.raw.biblian1
        semuxaBible["biblian2"] = R.raw.biblian2
        semuxaBible["biblian3"] = R.raw.biblian3
        semuxaBible["biblian4"] = R.raw.biblian4
        semuxaBible["biblian5"] = R.raw.biblian5
        semuxaBible["biblian6"] = R.raw.biblian6
        semuxaBible["biblian7"] = R.raw.biblian7
        semuxaBible["biblian8"] = R.raw.biblian8
        semuxaBible["biblian9"] = R.raw.biblian9
        semuxaBible["biblian10"] = R.raw.biblian10
        semuxaBible["biblian11"] = R.raw.biblian11
        semuxaBible["biblian12"] = R.raw.biblian12
        semuxaBible["biblian13"] = R.raw.biblian13
        semuxaBible["biblian14"] = R.raw.biblian14
        semuxaBible["biblian15"] = R.raw.biblian15
        semuxaBible["biblian16"] = R.raw.biblian16
        semuxaBible["biblian17"] = R.raw.biblian17
        semuxaBible["biblian18"] = R.raw.biblian18
        semuxaBible["biblian19"] = R.raw.biblian19
        semuxaBible["biblian20"] = R.raw.biblian20
        semuxaBible["biblian21"] = R.raw.biblian21
        semuxaBible["biblian22"] = R.raw.biblian22
        semuxaBible["biblian23"] = R.raw.biblian23
        semuxaBible["biblian24"] = R.raw.biblian24
        semuxaBible["biblian25"] = R.raw.biblian25
        semuxaBible["biblian26"] = R.raw.biblian26
        semuxaBible["biblian27"] = R.raw.biblian27
    }

    override fun onPause() {
        super.onPause()
        prefEditors.putString("search_string_filter", binding.editText2.text.toString())
        prefEditors.putInt("search_bible_fierstPosition", fierstPosition)
        prefEditors.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        prefEditors = chin.edit()
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        binding = SearchBibliaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Slidr.attach(this)
        binding.filterGrup.visibility = View.VISIBLE
        binding.buttonx2.setOnClickListener(this)
        DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.buttonx2.setImageResource(by.carkva_gazeta.malitounik.R.drawable.cancel)
        }
        binding.editText2.addTextChangedListener(MyTextWatcher(binding.editText2, true))
        if (intent.getIntExtra("zavet", 1) != zavet) {
            prefEditors.putString("search_string", "")
            prefEditors.putString("search_string_filter", "")
            prefEditors.apply()
        }
        if (chin.getString("search_string", "") != "") {
            if (chin.getString("search_array", "") != "") {
                val gson = Gson()
                val json = chin.getString("search_array", "")
                val type = object : TypeToken<ArrayList<String>>() {}.type
                val arrayList = ArrayList<String>()
                arrayList.addAll(gson.fromJson(json, type))
                arrayList.forEach {
                    val p = it
                    val t2 = p.indexOf("<p")
                    val t3 = p.indexOf(">", t2)
                    val subP = p.substring(t2, t3 + 1)
                    var str = p.replace(subP, "")
                    str = str.replace("</p>", "<br>")
                    val t1 = str.lastIndexOf("<br>")
                    val span = MainActivity.fromHtml(str.substring(0, t1)) as Spannable
                    seash.add(span)
                }
                actionExpandOn = true
            }
        }
        var biblia = "semuxa"
        zavet = intent.getIntExtra("zavet", 1)
        when (zavet) {
            1 -> {
                title = resources.getString(by.carkva_gazeta.malitounik.R.string.poshuk_semuxa)
                biblia = "semuxa"
            }
            2 -> {
                title = resources.getString(by.carkva_gazeta.malitounik.R.string.poshuk_sinoidal)
                biblia = "sinoidal"
            }
            3 -> {
                title = resources.getString(by.carkva_gazeta.malitounik.R.string.poshuk_nadsan)
                biblia = "nadsan"
            }
        }
        if (chin.getString("history_bible_$biblia", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_bible_$biblia", "")
            val type = object : TypeToken<ArrayList<String>>() {}.type
            history.addAll(gson.fromJson(json, type))
        }
        adapter = SearchBibliaListAdaprer(this)
        binding.ListView.adapter = adapter
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                fierstPosition = absListView.firstVisiblePosition
                if (i == 1) { // Скрываем клавиатуру
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
        binding.editText2.setText(chin.getString("search_string_filter", ""))
        binding.ListView.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val strText = adapterView.adapter.getItem(position).toString()
            var nazva = 0
            var nazvaS = -1
            if (zavet == 2) {
                if (strText.contains("Бытие")) nazvaS = 0
                if (strText.contains("Исход")) nazvaS = 1
                if (strText.contains("Левит")) nazvaS = 2
                if (strText.contains("Числа")) nazvaS = 3
                if (strText.contains("Второзаконие")) nazvaS = 4
                if (strText.contains("Иисуса Навина")) nazvaS = 5
                if (strText.contains("Судей израилевых")) nazvaS = 6
                if (strText.contains("Руфи")) nazvaS = 7
                if (strText.contains("1-я Царств")) nazvaS = 8
                if (strText.contains("2-я Царств")) nazvaS = 9
                if (strText.contains("3-я Царств")) nazvaS = 10
                if (strText.contains("4-я Царств")) nazvaS = 11
                if (strText.contains("1-я Паралипоменон")) nazvaS = 12
                if (strText.contains("2-я Паралипоменон")) nazvaS = 13
                if (strText.contains("1-я Ездры")) nazvaS = 14
                if (strText.contains("Неемии")) nazvaS = 15
                if (strText.contains("2-я Ездры")) nazvaS = 16
                if (strText.contains("Товита")) nazvaS = 17
                if (strText.contains("Иудифи")) nazvaS = 18
                if (strText.contains("Есфири")) nazvaS = 19
                if (strText.contains("Иова")) nazvaS = 20
                if (strText.contains("Псалтирь")) nazvaS = 21
                if (strText.contains("Притчи Соломона")) nazvaS = 22
                if (strText.contains("Екклезиаста")) nazvaS = 23
                if (strText.contains("Песнь песней Соломона")) nazvaS = 24
                if (strText.contains("Премудрости Соломона")) nazvaS = 25
                if (strText.contains("Премудрости Иисуса, сына Сирахова")) nazvaS = 26
                if (strText.contains("Исаии")) nazvaS = 27
                if (strText.contains("Иеремии")) nazvaS = 28
                if (strText.contains("Плач Иеремии")) nazvaS = 29
                if (strText.contains("Послание Иеремии")) nazvaS = 30
                if (strText.contains("Варуха")) nazvaS = 31
                if (strText.contains("Иезекииля")) nazvaS = 32
                if (strText.contains("Даниила")) nazvaS = 33
                if (strText.contains("Осии")) nazvaS = 34
                if (strText.contains("Иоиля")) nazvaS = 35
                if (strText.contains("Амоса")) nazvaS = 36
                if (strText.contains("Авдия")) nazvaS = 37
                if (strText.contains("Ионы")) nazvaS = 38
                if (strText.contains("Михея")) nazvaS = 39
                if (strText.contains("Наума")) nazvaS = 40
                if (strText.contains("Аввакума")) nazvaS = 41
                if (strText.contains("Сафонии")) nazvaS = 42
                if (strText.contains("Аггея")) nazvaS = 43
                if (strText.contains("Захарии")) nazvaS = 44
                if (strText.contains("Малахии")) nazvaS = 45
                if (strText.contains("1-я Маккавейская")) nazvaS = 46
                if (strText.contains("2-я Маккавейская")) nazvaS = 47
                if (strText.contains("3-я Маккавейская")) nazvaS = 48
                if (strText.contains("3-я Ездры")) nazvaS = 49
                if (strText.contains("От Марка")) nazva = 1
                if (strText.contains("От Луки")) nazva = 2
                if (strText.contains("От Иоанна")) nazva = 3
                if (strText.contains("Деяния святых апостолов")) nazva = 4
                if (strText.contains("Иакова")) nazva = 5
                if (strText.contains("1-е Петра")) nazva = 6
                if (strText.contains("2-е Петра")) nazva = 7
                if (strText.contains("1-е Иоанна")) nazva = 8
                if (strText.contains("2-е Иоанна")) nazva = 9
                if (strText.contains("3-е Иоанна")) nazva = 10
                if (strText.contains("Иуды")) nazva = 11
                if (strText.contains("Римлянам")) nazva = 12
                if (strText.contains("1-е Коринфянам")) nazva = 13
                if (strText.contains("2-е Коринфянам")) nazva = 14
                if (strText.contains("Галатам")) nazva = 15
                if (strText.contains("Эфэсянам")) nazva = 16
                if (strText.contains("Филиппийцам")) nazva = 17
                if (strText.contains("Колоссянам")) nazva = 18
                if (strText.contains("1-е Фессалоникийцам (Солунянам)")) nazva = 19
                if (strText.contains("2-е Фессалоникийцам (Солунянам)")) nazva = 20
                if (strText.contains("1-е Тимофею")) nazva = 21
                if (strText.contains("2-е Тимофею")) nazva = 22
                if (strText.contains("Титу")) nazva = 23
                if (strText.contains("Филимону")) nazva = 24
                if (strText.contains("Евреям")) nazva = 25
                if (strText.contains("Откровение (Апокалипсис)")) nazva = 26
            }
            if (zavet == 1) {
                if (strText.contains("Быцьцё")) nazvaS = 0
                if (strText.contains("Выхад")) nazvaS = 1
                if (strText.contains("Лявіт")) nazvaS = 2
                if (strText.contains("Лікі")) nazvaS = 3
                if (strText.contains("Другі Закон")) nazvaS = 4
                if (strText.contains("Ісуса сына Нава")) nazvaS = 5
                if (strText.contains("Судзьдзяў")) nazvaS = 6
                if (strText.contains("Рут")) nazvaS = 7
                if (strText.contains("1-я Царстваў")) nazvaS = 8
                if (strText.contains("2-я Царстваў")) nazvaS = 9
                if (strText.contains("3-я Царстваў")) nazvaS = 10
                if (strText.contains("4-я Царстваў")) nazvaS = 11
                if (strText.contains("1-я Летапісаў")) nazvaS = 12
                if (strText.contains("2-я Летапісаў")) nazvaS = 13
                if (strText.contains("Эздры")) nazvaS = 14
                if (strText.contains("Нээміі")) nazvaS = 15
                if (strText.contains("Эстэр")) nazvaS = 16
                if (strText.contains("Ёва")) nazvaS = 17
                if (strText.contains("Псалтыр")) nazvaS = 18
                if (strText.contains("Выслоўяў Саламонавых")) nazvaS = 19
                if (strText.contains("Эклезіяста")) nazvaS = 20
                if (strText.contains("Найвышэйшая Песьня Саламонава")) nazvaS = 21
                if (strText.contains("Ісаі")) nazvaS = 22
                if (strText.contains("Ераміі")) nazvaS = 23
                if (strText.contains("Ераміін Плач")) nazvaS = 24
                if (strText.contains("Езэкііля")) nazvaS = 25
                if (strText.contains("Данііла")) nazvaS = 26
                if (strText.contains("Асіі")) nazvaS = 27
                if (strText.contains("Ёіля")) nazvaS = 28
                if (strText.contains("Амоса")) nazvaS = 29
                if (strText.contains("Аўдзея")) nazvaS = 30
                if (strText.contains("Ёны")) nazvaS = 31
                if (strText.contains("Міхея")) nazvaS = 32
                if (strText.contains("Навума")) nazvaS = 33
                if (strText.contains("Абакума")) nazvaS = 34
                if (strText.contains("Сафона")) nazvaS = 35
                if (strText.contains("Агея")) nazvaS = 36
                if (strText.contains("Захарыі")) nazvaS = 37
                if (strText.contains("Малахіі")) nazvaS = 38
                if (strText.contains("Паводле Марка")) nazva = 1
                if (strText.contains("Паводле Лукаша")) nazva = 2
                if (strText.contains("Паводле Яна")) nazva = 3
                if (strText.contains("Дзеі Апосталаў")) nazva = 4
                if (strText.contains("Якава")) nazva = 5
                if (strText.contains("1-е Пятра")) nazva = 6
                if (strText.contains("2-е Пятра")) nazva = 7
                if (strText.contains("1-е Яна Багаслова")) nazva = 8
                if (strText.contains("2-е Яна Багаслова")) nazva = 9
                if (strText.contains("3-е Яна Багаслова")) nazva = 10
                if (strText.contains("Юды")) nazva = 11
                if (strText.contains("Да Рымлянаў")) nazva = 12
                if (strText.contains("1-е да Карынфянаў")) nazva = 13
                if (strText.contains("2-е да Карынфянаў")) nazva = 14
                if (strText.contains("Да Галятаў")) nazva = 15
                if (strText.contains("Да Эфэсянаў")) nazva = 16
                if (strText.contains("Да Піліпянаў")) nazva = 17
                if (strText.contains("Да Каласянаў")) nazva = 18
                if (strText.contains("1-е да Фесаланікійцаў")) nazva = 19
                if (strText.contains("2-е да Фесаланікійцаў")) nazva = 20
                if (strText.contains("1-е да Цімафея")) nazva = 21
                if (strText.contains("2-е да Цімафея")) nazva = 22
                if (strText.contains("Да Ціта")) nazva = 23
                if (strText.contains("Да Філімона")) nazva = 24
                if (strText.contains("Да Габрэяў")) nazva = 25
                if (strText.contains("Адкрыцьцё (Апакаліпсіс)")) nazva = 26
            }
            val str1 = strText.indexOf("glava.")
            val str2 = strText.indexOf("-->")
            val str3 = strText.indexOf("<!--stix.")
            val str4 = strText.indexOf("::")
            val glava = strText.substring(str1 + 6, str2).toInt()
            val stix = strText.substring(str3 + 9, str4).toInt()
            if (zavet == 3) {
                val intent = Intent(this@SearchBiblia, NadsanContentActivity::class.java)
                intent.putExtra("glava", glava - 1)
                intent.putExtra("stix", stix - 1)
                prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                prefEditors.apply()
                startActivity(intent)
            } else {
                if (nazvaS != -1) {
                    if (zavet == 1) {
                        val intent = Intent(this@SearchBiblia, StaryZapavietSemuxa::class.java)
                        intent.putExtra("kniga", nazvaS)
                        intent.putExtra("glava", glava - 1)
                        intent.putExtra("stix", stix - 1)
                        prefEditors.putBoolean("novyzavet", false)
                        prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@SearchBiblia, StaryZapavietSinaidal::class.java)
                        intent.putExtra("kniga", nazvaS)
                        intent.putExtra("glava", glava - 1)
                        intent.putExtra("stix", stix - 1)
                        prefEditors.putBoolean("novyzavet", false)
                        prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    }
                } else {
                    if (zavet == 1) {
                        val intent = Intent(this@SearchBiblia, NovyZapavietSemuxa::class.java)
                        intent.putExtra("kniga", nazva)
                        intent.putExtra("glava", glava - 1)
                        intent.putExtra("stix", stix - 1)
                        prefEditors.putBoolean("novyzavet", true)
                        prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@SearchBiblia, NovyZapavietSinaidal::class.java)
                        intent.putExtra("kniga", nazva)
                        intent.putExtra("glava", glava - 1)
                        intent.putExtra("stix", stix - 1)
                        prefEditors.putBoolean("novyzavet", true)
                        prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    }
                }
            }
        }
        historyAdapter = HistoryAdapter(this, history)
        if (dzenNoch) binding.History.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.History.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.History.adapter = historyAdapter
        binding.History.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val edit = history[position]
            addHistory(edit)
            saveHistory()
            binding.History.visibility = View.GONE
            binding.ListView.visibility = View.VISIBLE
            searche = true
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
            autoCompleteTextView?.setText(edit)
            searchView?.clearFocus()
            execute(edit)
        }
        binding.History.setOnItemLongClickListener { _, _, position, _ ->
            val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return@setOnItemLongClickListener true
        }
        if (savedInstanceState != null) {
            val listView = savedInstanceState.getBoolean("list_view")
            if (listView) binding.ListView.visibility = View.VISIBLE
            actionExpandOn = listView
            fierstPosition = savedInstanceState.getInt("fierstPosition")
        } else {
            fierstPosition = chin.getInt("search_bible_fierstPosition", 0)
        }
        binding.ListView.setSelection(fierstPosition)
        val data = if (zavet == 3) arrayOf(getString(by.carkva_gazeta.malitounik.R.string.psalter))
        else resources.getStringArray(by.carkva_gazeta.malitounik.R.array.serche_bible)
        val arrayAdapter = SearchSpinnerAdapter(this, data)
        binding.spinner6.adapter = arrayAdapter
        if (zavet != 3) {
            binding.spinner6.setSelection(chin.getInt("biblia_seash", 0))
            binding.spinner6.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prefEditors.putInt("biblia_seash", position)
                    prefEditors.apply()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        binding.constraint.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding.constraint.rootView.height - binding.constraint.height
            val keywordView = binding.constraint.rootView.height / 4
            keyword = heightDiff > keywordView
            settingsView()
        }
        binding.editText2.setOnFocusChangeListener { _, hasFocus ->
            edittext2Focus = hasFocus
            settingsView()
        }
        if (!chin.getBoolean("pegistrbukv", true)) binding.checkBox.isChecked = true
        binding.checkBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditors.putBoolean("pegistrbukv", false)
            } else {
                prefEditors.putBoolean("pegistrbukv", true)
            }
            prefEditors.apply()
        }
        if (chin.getInt("slovocalkam", 0) == 1) binding.checkBox2.isChecked = true
        binding.checkBox2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditors.putInt("slovocalkam", 1)
            } else {
                prefEditors.putInt("slovocalkam", 0)
            }
            prefEditors.apply()
        }
        setTollbarTheme()
    }

    private fun settingsView() {
        if (keyword && !edittext2Focus) {
            binding.settingsGrup.visibility = View.VISIBLE
        } else {
            binding.settingsGrup.visibility = View.GONE
        }
    }

    private fun setTollbarTheme() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun changeSearchViewElements(view: View?) {
        if (view == null) return
        if (view.id == by.carkva_gazeta.malitounik.R.id.search_edit_frame || view.id == by.carkva_gazeta.malitounik.R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == by.carkva_gazeta.malitounik.R.id.search_src_text) {
            autoCompleteTextView = view as AutoCompleteTextView
            autoCompleteTextView?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
            val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher(autoCompleteTextView))
            autoCompleteTextView?.setText(chin.getString("search_string", ""))
            autoCompleteTextView?.setSelection(autoCompleteTextView?.text?.length ?: 0)
            autoCompleteTextView?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val edit = autoCompleteTextView?.text.toString()
                    if (edit.length >= 3) {
                        searche = true
                        addHistory(edit)
                        saveHistory()
                        binding.History.visibility = View.GONE
                        binding.ListView.visibility = View.VISIBLE
                        execute(edit)
                    } else {
                        MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.seashmin))
                    }
                }
                true
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.search_biblia, menu)
        val searchViewItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.search)
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                finish()
                return false
            }
        })
        searchViewItem.expandActionView()
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = title
        val searcheTextView = searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searcheTextView.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount = menu.findItem(by.carkva_gazeta.malitounik.R.id.count).actionView as TextView
        textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, seash.size)
        changeSearchViewElements(searchView)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val histopy = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (zavet == 1) {
            if (MenuBibleSemuxa.bible_time) {
                MenuBibleSemuxa.bible_time = false
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
        if (zavet == 2) {
            if (MenuBibleSinoidal.bible_time) {
                MenuBibleSinoidal.bible_time = false
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
        if (zavet == 3) {
            if (MenuPsalterNadsana.bible_time) {
                MenuPsalterNadsana.bible_time = false
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun addHistory(item: String) {
        val temp = ArrayList<String>()
        for (i in 0 until history.size) {
            if (history[i] != item) temp.add(history[i])
        }
        history.clear()
        history.add(item)
        for (i in 0 until temp.size) {
            history.add(temp[i])
            if (history.size == 10) break
        }
        if (history.size == 1) invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    private fun saveHistory() {
        var biblia = "semuxa"
        when (zavet) {
            1 -> biblia = "semuxa"
            2 -> biblia = "sinoidal"
            3 -> biblia = "nadsan"
        }
        val gson = Gson()
        val json = gson.toJson(history)
        prefEditors.putString("history_bible_$biblia", json)
        prefEditors.apply()
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistory()
        invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
        actionExpandOn = true
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistory()
        if (history.size == 0) {
            actionExpandOn = true
            invalidateOptionsMenu()
        }
        historyAdapter.notifyDataSetChanged()
    }

    override fun onClick(view: View?) {
        val idSelect = view?.id ?: 0
        if (idSelect == R.id.buttonx2) {
            binding.editText2.setText("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("list_view", binding.ListView.visibility == View.VISIBLE)
        outState.putInt("fierstPosition", fierstPosition)
        prefEditors.putString("search_string", autoCompleteTextView?.text.toString())
        prefEditors.apply()
    }

    private fun execute(searche: String) {
        CoroutineScope(Dispatchers.Main).launch {
            onPreExecute()
            val result = withContext(Dispatchers.IO) {
                return@withContext doInBackground(searche)
            }
            onPostExecute(result)
        }
    }

    private fun onPreExecute() {
        searche = true
        prefEditors = chin.edit()
        adapter.clear()
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
        binding.progressBar.visibility = View.VISIBLE
        binding.ListView.visibility = View.GONE
        var edit = autoCompleteTextView?.text.toString()
        if (edit != "") {
            edit = edit.trim()
            autoCompleteTextView?.setText(edit)
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
        }
    }

    private fun doInBackground(searche: String): ArrayList<Spannable> {
        return when (zavet) {
            1 -> semuxa(searche)
            2 -> sinoidal(searche)
            3 -> nadsan(searche)
            else -> ArrayList()
        }
    }

    private fun onPostExecute(result: ArrayList<Spannable>) {
        adapter.addAll(result)
        adapter.filter.filter(binding.editText2.text.toString())
        textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        if (chin.getString("search_string", "") != "") {
            binding.ListView.post { binding.ListView.setSelection(chin.getInt("search_position", 0)) }
        }
        binding.progressBar.visibility = View.GONE
        binding.History.visibility = View.GONE
        binding.ListView.visibility = View.VISIBLE
        val arrayList = ArrayList<String>()
        result.forEach {
            arrayList.add(MainActivity.toHtml(it))
        }
        val gson = Gson()
        val json = gson.toJson(arrayList)
        prefEditors.putString("search_array", json)
        prefEditors.apply()
        searche = false
        val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
    }

    private fun zamena(replase: String): String {
        val registr = chin.getBoolean("pegistrbukv", true)
        var replase1 = replase
        replase1 = replase1.replace("ё", "е", registr)
        replase1 = replase1.replace("и", "і", registr)
        replase1 = replase1.replace("щ", "ў", registr)
        replase1 = replase1.replace("ъ", "'", registr)
        replase1 = replase1.replace("све", "сьве", registr)
        replase1 = replase1.replace("сві", "сьві", registr)
        replase1 = replase1.replace("свя", "сьвя", registr)
        replase1 = replase1.replace("зве", "зьве", registr)
        replase1 = replase1.replace("зві", "зьві", registr)
        replase1 = replase1.replace("звя", "зьвя", registr)
        replase1 = replase1.replace("зме", "зьме", registr)
        replase1 = replase1.replace("змі", "зьмі", registr)
        replase1 = replase1.replace("змя", "зьмя", registr)
        replase1 = replase1.replace("зня", "зьня", registr)
        replase1 = replase1.replace("сле", "сьле", registr)
        replase1 = replase1.replace("слі", "сьлі", registr)
        replase1 = replase1.replace("сль", "сьль", registr)
        replase1 = replase1.replace("слю", "сьлю", registr)
        replase1 = replase1.replace("сля", "сьля", registr)
        replase1 = replase1.replace("сне", "сьне", registr)
        replase1 = replase1.replace("сні", "сьні", registr)
        replase1 = replase1.replace("сню", "сьню", registr)
        replase1 = replase1.replace("сня", "сьня", registr)
        replase1 = replase1.replace("спе", "сьпе", registr)
        replase1 = replase1.replace("спі", "сьпі", registr)
        replase1 = replase1.replace("спя", "сьпя", registr)
        replase1 = replase1.replace("сце", "сьце", registr)
        replase1 = replase1.replace("сці", "сьці", registr)
        replase1 = replase1.replace("сць", "сьць", registr)
        replase1 = replase1.replace("сцю", "сьцю", registr)
        replase1 = replase1.replace("сця", "сьця", registr)
        replase1 = replase1.replace("цце", "цьце", registr)
        replase1 = replase1.replace("цці", "цьці", registr)
        replase1 = replase1.replace("ццю", "цьцю", registr)
        replase1 = replase1.replace("ззе", "зьзе", registr)
        replase1 = replase1.replace("ззі", "зьзі", registr)
        replase1 = replase1.replace("ззю", "зьзю", registr)
        replase1 = replase1.replace("ззя", "зьзя", registr)
        replase1 = replase1.replace("зле", "зьле", registr)
        replase1 = replase1.replace("злі", "зьлі", registr)
        replase1 = replase1.replace("злю", "зьлю", registr)
        replase1 = replase1.replace("зля", "зьля", registr)
        replase1 = replase1.replace("збе", "зьбе", registr)
        replase1 = replase1.replace("збі", "зьбі", registr)
        replase1 = replase1.replace("збя", "зьбя", registr)
        replase1 = replase1.replace("нне", "ньне", registr)
        replase1 = replase1.replace("нні", "ньні", registr)
        replase1 = replase1.replace("нню", "ньню", registr)
        replase1 = replase1.replace("ння", "ньня", registr)
        replase1 = replase1.replace("лле", "льле", registr)
        replase1 = replase1.replace("ллі", "льлі", registr)
        replase1 = replase1.replace("ллю", "льлю", registr)
        replase1 = replase1.replace("лля", "льля", registr)
        replase1 = replase1.replace("дск", "дзк", registr)
        replase1 = replase1.replace("дств", "дзтв", registr)
        replase1 = replase1.replace("з’е", "зье", registr)
        replase1 = replase1.replace("з’я", "зья", registr)
        return replase1
    }

    private fun semuxa(poshuk: String): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        if (poshuk1 != "") {
            poshuk1 = zamena(poshuk1)
            val registr = chin.getBoolean("pegistrbukv", true)
            if (chin.getInt("slovocalkam", 0) == 0) {
                val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
                for (aM in m) {
                    val r = poshuk1.length - 1
                    if (poshuk1.length >= 3) {
                        if (poshuk1[r] == aM) {
                            poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                        }
                    }
                }
            } else {
                poshuk1 = " $poshuk1 "
            }
            for (i in 0 until semuxaBible.size) {
                val biblia = chin.getInt("biblia_seash", 0)
                var nazva = ""
                if (biblia == 0 || biblia == 3 || biblia == 4) {
                    if (semuxaBible.keyAt(i) == "biblias1") nazva = "Быцьцё"
                    if (semuxaBible.keyAt(i) == "biblias2") nazva = "Выхад"
                    if (semuxaBible.keyAt(i) == "biblias3") nazva = "Лявіт"
                    if (semuxaBible.keyAt(i) == "biblias4") nazva = "Лікі"
                    if (semuxaBible.keyAt(i) == "biblias5") nazva = "Другі Закон"
                }
                if (biblia == 0 || biblia == 4) {
                    if (semuxaBible.keyAt(i) == "biblias6") nazva = "Ісуса сына Нава"
                    if (semuxaBible.keyAt(i) == "biblias7") nazva = "Судзьдзяў"
                    if (semuxaBible.keyAt(i) == "biblias8") nazva = "Рут"
                    if (semuxaBible.keyAt(i) == "biblias9") nazva = "1-я Царстваў"
                    if (semuxaBible.keyAt(i) == "biblias10") nazva = "2-я Царстваў"
                    if (semuxaBible.keyAt(i) == "biblias11") nazva = "3-я Царстваў"
                    if (semuxaBible.keyAt(i) == "biblias12") nazva = "4-я Царстваў"
                    if (semuxaBible.keyAt(i) == "biblias13") nazva = "1-я Летапісаў"
                    if (semuxaBible.keyAt(i) == "biblias14") nazva = "2-я Летапісаў"
                    if (semuxaBible.keyAt(i) == "biblias15") nazva = "Эздры"
                    if (semuxaBible.keyAt(i) == "biblias16") nazva = "Нээміі"
                    if (semuxaBible.keyAt(i) == "biblias17") nazva = "Эстэр"
                    if (semuxaBible.keyAt(i) == "biblias18") nazva = "Ёва"
                    if (semuxaBible.keyAt(i) == "biblias19") nazva = "Псалтыр"
                    if (semuxaBible.keyAt(i) == "biblias20") nazva = "Выслоўяў Саламонавых"
                    if (semuxaBible.keyAt(i) == "biblias21") nazva = "Эклезіяста"
                    if (semuxaBible.keyAt(i) == "biblias22") nazva = "Найвышэйшая Песьня Саламонава"
                    if (semuxaBible.keyAt(i) == "biblias23") nazva = "Ісаі"
                    if (semuxaBible.keyAt(i) == "biblias24") nazva = "Ераміі"
                    if (semuxaBible.keyAt(i) == "biblias25") nazva = "Ераміін Плач"
                    if (semuxaBible.keyAt(i) == "biblias26") nazva = "Езэкііля"
                    if (semuxaBible.keyAt(i) == "biblias27") nazva = "Данііла"
                    if (semuxaBible.keyAt(i) == "biblias28") nazva = "Асіі"
                    if (semuxaBible.keyAt(i) == "biblias29") nazva = "Ёіля"
                    if (semuxaBible.keyAt(i) == "biblias30") nazva = "Амоса"
                    if (semuxaBible.keyAt(i) == "biblias31") nazva = "Аўдзея"
                    if (semuxaBible.keyAt(i) == "biblias32") nazva = "Ёны"
                    if (semuxaBible.keyAt(i) == "biblias33") nazva = "Міхея"
                    if (semuxaBible.keyAt(i) == "biblias34") nazva = "Навума"
                    if (semuxaBible.keyAt(i) == "biblias35") nazva = "Абакума"
                    if (semuxaBible.keyAt(i) == "biblias36") nazva = "Сафона"
                    if (semuxaBible.keyAt(i) == "biblias37") nazva = "Агея"
                    if (semuxaBible.keyAt(i) == "biblias38") nazva = "Захарыі"
                    if (semuxaBible.keyAt(i) == "biblias39") nazva = "Малахіі"
                }
                if (biblia == 0 || biblia == 1 || biblia == 2) {
                    if (semuxaBible.keyAt(i) == "biblian1") nazva = "Паводле Мацьвея"
                    if (semuxaBible.keyAt(i) == "biblian2") nazva = "Паводле Марка"
                    if (semuxaBible.keyAt(i) == "biblian3") nazva = "Паводле Лукаша"
                    if (semuxaBible.keyAt(i) == "biblian4") nazva = "Паводле Яна"
                }
                if (biblia == 0 || biblia == 2) {
                    if (semuxaBible.keyAt(i) == "biblian5") nazva = "Дзеі Апосталаў"
                    if (semuxaBible.keyAt(i) == "biblian6") nazva = "Якава"
                    if (semuxaBible.keyAt(i) == "biblian7") nazva = "1-е Пятра"
                    if (semuxaBible.keyAt(i) == "biblian8") nazva = "2-е Пятра"
                    if (semuxaBible.keyAt(i) == "biblian9") nazva = "1-е Яна Багаслова"
                    if (semuxaBible.keyAt(i) == "biblian10") nazva = "2-е Яна Багаслова"
                    if (semuxaBible.keyAt(i) == "biblian11") nazva = "3-е Яна Багаслова"
                    if (semuxaBible.keyAt(i) == "biblian12") nazva = "Юды"
                    if (semuxaBible.keyAt(i) == "biblian13") nazva = "Да Рымлянаў"
                    if (semuxaBible.keyAt(i) == "biblian14") nazva = "1-е да Карынфянаў"
                    if (semuxaBible.keyAt(i) == "biblian15") nazva = "2-е да Карынфянаў"
                    if (semuxaBible.keyAt(i) == "biblian16") nazva = "Да Галятаў"
                    if (semuxaBible.keyAt(i) == "biblian17") nazva = "Да Эфэсянаў"
                    if (semuxaBible.keyAt(i) == "biblian18") nazva = "Да Піліпянаў"
                    if (semuxaBible.keyAt(i) == "biblian19") nazva = "Да Каласянаў"
                    if (semuxaBible.keyAt(i) == "biblian20") nazva = "1-е да Фесаланікійцаў"
                    if (semuxaBible.keyAt(i) == "biblian21") nazva = "2-е да Фесаланікійцаў"
                    if (semuxaBible.keyAt(i) == "biblian22") nazva = "1-е да Цімафея"
                    if (semuxaBible.keyAt(i) == "biblian23") nazva = "2-е да Цімафея"
                    if (semuxaBible.keyAt(i) == "biblian24") nazva = "Да Ціта"
                    if (semuxaBible.keyAt(i) == "biblian25") nazva = "Да Філімона"
                    if (semuxaBible.keyAt(i) == "biblian26") nazva = "Да Габрэяў"
                    if (semuxaBible.keyAt(i) == "biblian27") nazva = "Адкрыцьцё (Апакаліпсіс)"
                }
                if (nazva != "") {
                    val inputStream = resources.openRawResource(semuxaBible.valueAt(i))
                    val isr = InputStreamReader(inputStream)
                    val reader = BufferedReader(isr)
                    var glava = 0
                    val split = reader.use {
                        it.readText().split("===")
                    }
                    (1 until split.size).forEach { e ->
                        glava++
                        val bibleline = split[e].split("\n")
                        var stix = 0
                        for (r in 1 until bibleline.size) {
                            var prepinanie = bibleline[r]
                            if (prepinanie.contains("//")) {
                                val t1 = prepinanie.indexOf("//")
                                prepinanie = if (t1 == 0) continue else prepinanie.substring(0, t1).trim()
                            }
                            stix++
                            if (chin.getInt("slovocalkam", 0) == 1) prepinanie = " " + bibleline[r] + " "
                            prepinanie = prepinanie.replace(",", "")
                            prepinanie = prepinanie.replace(".", "")
                            prepinanie = prepinanie.replace(";", "")
                            prepinanie = prepinanie.replace(":", "")
                            prepinanie = prepinanie.replace("-", "")
                            prepinanie = prepinanie.replace("\"", "")
                            prepinanie = prepinanie.replace("ё", "е", registr)
                            prepinanie = prepinanie.replace("<em>", "", registr)
                            prepinanie = prepinanie.replace("</em>", " ", registr)
                            prepinanie = prepinanie.replace("<br>", "", registr)
                            prepinanie = prepinanie.replace("<strong>", "", registr)
                            prepinanie = prepinanie.replace("</strong>", " ", registr)
                            if (chin.getInt("slovocalkam", 0) == 0) {
                                if (prepinanie.contains(poshuk1, registr)) {
                                    val aSviatyia = MainActivity.fromHtml(bibleline[r])
                                    val title = "$nazva Гл. $glava".length
                                    val span = SpannableString("<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n$aSviatyia")
                                    val t3 = span.indexOf("-->")
                                    val t1 = span.indexOf(poshuk1, ignoreCase = registr)
                                    val t2 = poshuk1.length
                                    span.setSpan(StyleSpan(Typeface.BOLD), t3 + 3, t3 + 3 + title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    if (t1 != -1) {
                                        span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                    seashpost.add(span)
                                }
                            } else {
                                if (prepinanie.contains(poshuk1, registr)) {
                                    val aSviatyia = MainActivity.fromHtml(bibleline[r])
                                    val t2 = poshuk1.length
                                    val title = "$nazva Гл. $glava".length
                                    val span = SpannableString("<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n$aSviatyia")
                                    val t3 = span.indexOf("-->")
                                    val t1 = span.indexOf(poshuk1, ignoreCase = registr)
                                    span.setSpan(StyleSpan(Typeface.BOLD), t3 + 3, t3 + 3 + title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    if (t1 != -1) {
                                        span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                    seashpost.add(span)
                                }
                            }
                        }
                    }
                }
            }
        }
        return seashpost
    }

    private fun sinoidal(poshuk: String): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        if (poshuk1 != "") {
            val registr = chin.getBoolean("pegistrbukv", true)
            poshuk1 = poshuk1.replace("ё", "е", registr)
            if (chin.getInt("slovocalkam", 0) == 0) {
                val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
                for (aM in m) {
                    val r = poshuk1.length - 1
                    if (poshuk1[r] == aM) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                    }
                }
            } else {
                poshuk1 = " $poshuk1 "
            }
            for (i in 0 until sinodalBible.size) {
                val biblia = chin.getInt("biblia_seash", 0)
                var nazva = ""
                if (biblia == 0 || biblia == 3 || biblia == 4) {
                    if (sinodalBible.keyAt(i) == "sinaidals1") nazva = "Бытие"
                    if (sinodalBible.keyAt(i) == "sinaidals2") nazva = "Исход"
                    if (sinodalBible.keyAt(i) == "sinaidals3") nazva = "Левит"
                    if (sinodalBible.keyAt(i) == "sinaidals4") nazva = "Числа"
                    if (sinodalBible.keyAt(i) == "sinaidals5") nazva = "Второзаконие"
                }
                if (biblia == 0 || biblia == 4) {
                    if (sinodalBible.keyAt(i) == "sinaidals6") nazva = "Иисуса Навина"
                    if (sinodalBible.keyAt(i) == "sinaidals7") nazva = "Судей израилевых"
                    if (sinodalBible.keyAt(i) == "sinaidals8") nazva = "Руфи"
                    if (sinodalBible.keyAt(i) == "sinaidals9") nazva = "1-я Царств"
                    if (sinodalBible.keyAt(i) == "sinaidals10") nazva = "2-я Царств"
                    if (sinodalBible.keyAt(i) == "sinaidals11") nazva = "3-я Царств"
                    if (sinodalBible.keyAt(i) == "sinaidals12") nazva = "4-я Царств"
                    if (sinodalBible.keyAt(i) == "sinaidals13") nazva = "1-я Паралипоменон"
                    if (sinodalBible.keyAt(i) == "sinaidals14") nazva = "2-я Паралипоменон"
                    if (sinodalBible.keyAt(i) == "sinaidals15") nazva = "1-я Ездры"
                    if (sinodalBible.keyAt(i) == "sinaidals16") nazva = "Неемии"
                    if (sinodalBible.keyAt(i) == "sinaidals17") nazva = "2-я Ездры"
                    if (sinodalBible.keyAt(i) == "sinaidals18") nazva = "Товита"
                    if (sinodalBible.keyAt(i) == "sinaidals19") nazva = "Иудифи"
                    if (sinodalBible.keyAt(i) == "sinaidals20") nazva = "Есфири"
                    if (sinodalBible.keyAt(i) == "sinaidals21") nazva = "Иова"
                    if (sinodalBible.keyAt(i) == "sinaidals22") nazva = "Псалтирь"
                    if (sinodalBible.keyAt(i) == "sinaidals23") nazva = "Притчи Соломона"
                    if (sinodalBible.keyAt(i) == "sinaidals24") nazva = "Екклезиаста"
                    if (sinodalBible.keyAt(i) == "sinaidals25") nazva = "Песнь песней Соломона"
                    if (sinodalBible.keyAt(i) == "sinaidals26") nazva = "Премудрости Соломона"
                    if (sinodalBible.keyAt(i) == "sinaidals27") nazva = "Премудрости Иисуса, сына Сирахова"
                    if (sinodalBible.keyAt(i) == "sinaidals28") nazva = "Исаии"
                    if (sinodalBible.keyAt(i) == "sinaidals29") nazva = "Иеремии"
                    if (sinodalBible.keyAt(i) == "sinaidals30") nazva = "Плач Иеремии"
                    if (sinodalBible.keyAt(i) == "sinaidals31") nazva = "Послание Иеремии"
                    if (sinodalBible.keyAt(i) == "sinaidals32") nazva = "Варуха"
                    if (sinodalBible.keyAt(i) == "sinaidals33") nazva = "Иезекииля"
                    if (sinodalBible.keyAt(i) == "sinaidals34") nazva = "Даниила"
                    if (sinodalBible.keyAt(i) == "sinaidals35") nazva = "Осии"
                    if (sinodalBible.keyAt(i) == "sinaidals36") nazva = "Иоиля"
                    if (sinodalBible.keyAt(i) == "sinaidals37") nazva = "Амоса"
                    if (sinodalBible.keyAt(i) == "sinaidals38") nazva = "Авдия"
                    if (sinodalBible.keyAt(i) == "sinaidals39") nazva = "Ионы"
                    if (sinodalBible.keyAt(i) == "sinaidals40") nazva = "Михея"
                    if (sinodalBible.keyAt(i) == "sinaidals41") nazva = "Наума"
                    if (sinodalBible.keyAt(i) == "sinaidals42") nazva = "Аввакума"
                    if (sinodalBible.keyAt(i) == "sinaidals43") nazva = "Сафонии"
                    if (sinodalBible.keyAt(i) == "sinaidals44") nazva = "Аггея"
                    if (sinodalBible.keyAt(i) == "sinaidals45") nazva = "Захарии"
                    if (sinodalBible.keyAt(i) == "sinaidals46") nazva = "Малахии"
                    if (sinodalBible.keyAt(i) == "sinaidals47") nazva = "1-я Маккавейская"
                    if (sinodalBible.keyAt(i) == "sinaidals48") nazva = "2-я Маккавейская"
                    if (sinodalBible.keyAt(i) == "sinaidals49") nazva = "3-я Маккавейская"
                    if (sinodalBible.keyAt(i) == "sinaidals50") nazva = "3-я Ездры"
                }
                if (biblia == 0 || biblia == 1 || biblia == 2) {
                    if (sinodalBible.keyAt(i) == "sinaidaln1") nazva = "От Матфея"
                    if (sinodalBible.keyAt(i) == "sinaidaln2") nazva = "От Марка"
                    if (sinodalBible.keyAt(i) == "sinaidaln3") nazva = "От Луки"
                    if (sinodalBible.keyAt(i) == "sinaidaln4") nazva = "От Иоанна"
                }
                if (biblia == 0 || biblia == 2) {
                    if (sinodalBible.keyAt(i) == "sinaidaln5") nazva = "Деяния святых апостолов"
                    if (sinodalBible.keyAt(i) == "sinaidaln6") nazva = "Иакова"
                    if (sinodalBible.keyAt(i) == "sinaidaln7") nazva = "1-е Петра"
                    if (sinodalBible.keyAt(i) == "sinaidaln8") nazva = "2-е Петра"
                    if (sinodalBible.keyAt(i) == "sinaidaln9") nazva = "1-е Иоанна"
                    if (sinodalBible.keyAt(i) == "sinaidaln10") nazva = "2-е Иоанна"
                    if (sinodalBible.keyAt(i) == "sinaidaln11") nazva = "3-е Иоанна"
                    if (sinodalBible.keyAt(i) == "sinaidaln12") nazva = "Иуды"
                    if (sinodalBible.keyAt(i) == "sinaidaln13") nazva = "Римлянам"
                    if (sinodalBible.keyAt(i) == "sinaidaln14") nazva = "1-е Коринфянам"
                    if (sinodalBible.keyAt(i) == "sinaidaln15") nazva = "2-е Коринфянам"
                    if (sinodalBible.keyAt(i) == "sinaidaln16") nazva = "Галатам"
                    if (sinodalBible.keyAt(i) == "sinaidaln17") nazva = "Эфэсянам"
                    if (sinodalBible.keyAt(i) == "sinaidaln18") nazva = "Филиппийцам"
                    if (sinodalBible.keyAt(i) == "sinaidaln19") nazva = "Колоссянам"
                    if (sinodalBible.keyAt(i) == "sinaidaln20") nazva = "1-е Фессалоникийцам (Солунянам)"
                    if (sinodalBible.keyAt(i) == "sinaidaln21") nazva = "2-е Фессалоникийцам (Солунянам)"
                    if (sinodalBible.keyAt(i) == "sinaidaln22") nazva = "1-е Тимофею"
                    if (sinodalBible.keyAt(i) == "sinaidaln23") nazva = "2-е Тимофею"
                    if (sinodalBible.keyAt(i) == "sinaidaln24") nazva = "Титу"
                    if (sinodalBible.keyAt(i) == "sinaidaln25") nazva = "Филимону"
                    if (sinodalBible.keyAt(i) == "sinaidaln26") nazva = "Евреям"
                    if (sinodalBible.keyAt(i) == "sinaidaln27") nazva = "Откровение (Апокалипсис)"
                }
                if (nazva != "") {
                    val inputStream = resources.openRawResource(sinodalBible.valueAt(i))
                    val isr = InputStreamReader(inputStream)
                    val reader = BufferedReader(isr)
                    var glava = 0
                    val split = reader.use {
                        it.readText().split("===")
                    }
                    (1 until split.size).forEach { e ->
                        glava++
                        val bibleline = split[e].split("\n")
                        var stix = 0
                        (1 until bibleline.size).forEach { r ->
                            stix++
                            var prepinanie = bibleline[r]
                            if (chin.getInt("slovocalkam", 0) == 1) prepinanie = " " + bibleline[r] + " "
                            prepinanie = prepinanie.replace(",", "")
                            prepinanie = prepinanie.replace(".", "")
                            prepinanie = prepinanie.replace(";", "")
                            prepinanie = prepinanie.replace(":", "")
                            prepinanie = prepinanie.replace("[", "")
                            prepinanie = prepinanie.replace("]", "")
                            prepinanie = prepinanie.replace("-", "")
                            prepinanie = prepinanie.replace("\"", "")
                            prepinanie = prepinanie.replace("ё", "е", registr)
                            if (chin.getInt("slovocalkam", 0) == 0) {
                                if (prepinanie.contains(poshuk1, registr)) {
                                    var aSviatyia = bibleline[r]
                                    aSviatyia = aSviatyia.replace("\\n", "\n")
                                    val t2 = poshuk1.length
                                    val title = "$nazva Гл. $glava".length
                                    val span = SpannableString("<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n$aSviatyia")
                                    val t3 = span.indexOf("-->")
                                    val t1 = span.indexOf(poshuk1, ignoreCase = registr)
                                    span.setSpan(StyleSpan(Typeface.BOLD), t3 + 3, t3 + 3 + title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    if (t1 != -1) {
                                        span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                    seashpost.add(span)
                                }
                            } else {
                                if (prepinanie.contains(poshuk1, registr)) {
                                    var aSviatyia = bibleline[r]
                                    aSviatyia = aSviatyia.replace("\\n", "\n")
                                    val t2 = poshuk1.length
                                    val title = "$nazva Гл. $glava".length
                                    val span = SpannableString("<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n$aSviatyia")
                                    val t3 = span.indexOf("-->")
                                    val t1 = span.indexOf(poshuk1, ignoreCase = registr)
                                    span.setSpan(StyleSpan(Typeface.BOLD), t3 + 3, t3 + 3 + title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    if (t1 != -1) {
                                        span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                    seashpost.add(span)
                                }
                            }
                        }
                    }
                }
            }
        }
        return seashpost
    }

    private fun nadsan(poshuk: String): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        if (poshuk1 != "") {
            poshuk1 = zamena(poshuk1)
            val registr = chin.getBoolean("pegistrbukv", true)
            if (chin.getInt("slovocalkam", 0) == 0) {
                val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
                for (aM in m) {
                    val r = poshuk1.length - 1
                    if (poshuk1.length >= 3) {
                        if (poshuk1[r] == aM) {
                            poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                        }
                    }
                }
            } else {
                poshuk1 = " $poshuk1 "
            }
            val nazva = getString(by.carkva_gazeta.malitounik.R.string.psalter)
            val inputStream = resources.openRawResource(R.raw.psaltyr_nadsan)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val split = reader.use {
                it.readText().split("===")
            }
            var glava = 0
            (1 until split.size).forEach { e ->
                glava++
                val bibleline = split[e].split("\n")
                var stix = 0
                (1 until bibleline.size).forEach { r ->
                    stix++
                    var prepinanie = bibleline[r]
                    prepinanie = prepinanie.replace(",", "")
                    prepinanie = prepinanie.replace(".", "")
                    prepinanie = prepinanie.replace(";", "")
                    prepinanie = prepinanie.replace(":", "")
                    prepinanie = prepinanie.replace("-", "")
                    prepinanie = prepinanie.replace("\"", "")
                    prepinanie = prepinanie.replace("ё", "е")
                    prepinanie = prepinanie.replace("<em>", "", registr)
                    prepinanie = prepinanie.replace("</em>", " ", registr)
                    prepinanie = prepinanie.replace("<br>", "", registr)
                    prepinanie = prepinanie.replace("<strong>", "", registr)
                    prepinanie = prepinanie.replace("</strong>", " ", registr)
                    if (chin.getInt("slovocalkam", 0) == 0) {
                        if (prepinanie.contains(poshuk1, registr)) {
                            val aSviatyia = MainActivity.fromHtml(bibleline[r])
                            val t2 = poshuk1.length
                            val title = "$nazva Гл. $glava".length
                            val span = SpannableString("<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n$aSviatyia")
                            val t3 = span.indexOf("-->")
                            val t1 = span.indexOf(poshuk1, ignoreCase = registr)
                            span.setSpan(StyleSpan(Typeface.BOLD), t3 + 3, t3 + 3 + title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (t1 != -1) {
                                span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            seashpost.add(span)
                        }
                    } else {
                        if (prepinanie.contains(poshuk1, registr)) {
                            val aSviatyia = MainActivity.fromHtml(bibleline[r])
                            val t2 = poshuk1.length
                            val title = "$nazva Гл. $glava".length
                            val span = SpannableString("<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n$aSviatyia")
                            val t3 = span.indexOf("-->")
                            val t1 = span.indexOf(poshuk1, ignoreCase = registr)
                            span.setSpan(StyleSpan(Typeface.BOLD), t3 + 3, t3 + 3 + title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (t1 != -1) {
                                span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                            seashpost.add(span)
                        }
                    }
                }
            }
        }
        return seashpost
    }

    private inner class MyTextWatcher(private val editText: EditText?, private val filtep: Boolean = false) : TextWatcher {
        private var editPosition = 0
        private var check = 0
        private var editch = true
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            editch = count != after
            check = after
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable) {
            var edit = s.toString()
            if (editch) {
                if (zavet == 1 || zavet == 3) {
                    edit = edit.replace("и", "і", true)
                    edit = edit.replace("щ", "ў", true)
                    edit = edit.replace("ъ", "'", true)
                }
                if (check != 0) {
                    editText?.removeTextChangedListener(this)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
            }
            if (editText?.id == by.carkva_gazeta.malitounik.R.id.search_src_text) {
                if (actionExpandOn && editPosition != 0) {
                    binding.History.visibility = View.GONE
                    binding.ListView.visibility = View.VISIBLE
                    actionExpandOn = false
                } else {
                    if (searche && editPosition != 0) {
                        binding.History.visibility = View.GONE
                        binding.ListView.visibility = View.VISIBLE
                        editText.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    } else {
                        binding.History.visibility = View.VISIBLE
                        binding.ListView.visibility = View.GONE
                        textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
                    }
                }
            }
            if (filtep) adapter.filter.filter(edit)
        }

    }

    internal inner class SearchBibliaListAdaprer(context: Activity) : ArrayAdapter<Spannable>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, seash) {
        private val origData: ArrayList<Spannable> = ArrayList(seash)
        override fun addAll(collection: Collection<Spannable>) {
            super.addAll(collection)
            origData.addAll(collection)
        }

        override fun clear() {
            super.clear()
            if (searche) origData.clear()
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val t1 = seash[position].indexOf("-->")
            viewHolder.text.text = seash[position].subSequence(t1 + 3, seash[position].length)
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val result = FilterResults()
                    if (constraint.toString().isNotEmpty()) {
                        val founded = ArrayList<Spannable>()
                        for (item in origData) {
                            if (item.contains(constraint, true)) {
                                founded.add(item)
                            }
                        }
                        result.values = founded
                        result.count = founded.size
                    } else {
                        result.values = origData
                        result.count = origData.size
                    }
                    return result
                }

                override fun publishResults(constraint: CharSequence, results: FilterResults) {
                    clear()
                    for (item in results.values as ArrayList<*>) {
                        var t1 = (item as Spannable).indexOf("-->")
                        if (t1 == -1) t1 = 0
                        else t1 += 3
                        val itm = item.indexOf(constraint.toString(), t1, true)
                        val itmcount = constraint.toString().length
                        if (itm != -1) {
                            val span = SpannableString(item)
                            span.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), itm, itm + itmcount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), itm, itm + itmcount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            add(span)
                        } else {
                            add(item)
                        }
                    }
                    textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, results.count)
                }
            }
        }
    }

    private inner class SearchSpinnerAdapter(context: Activity, private val name: Array<String>) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_4, name) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
            textView.gravity = Gravity.START
            if (dzenNoch) textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            else textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem4Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
            if (dzenNoch) viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            else viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            viewHolder.text.gravity = Gravity.START
            viewHolder.text.text = name[position]
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    companion object {
        private var zavet = 1
    }
}