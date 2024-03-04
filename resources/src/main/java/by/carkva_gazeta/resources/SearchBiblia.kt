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
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogClearHishory
import by.carkva_gazeta.malitounik.HistoryAdapter
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem4Binding
import by.carkva_gazeta.resources.databinding.SearchBibliaBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class SearchBiblia : BaseActivity(), DialogClearHishory.DialogClearHistoryListener, DialogBibleSearshSettings.DiallogBibleSearshListiner {
    private lateinit var adapter: SearchBibliaListAdaprer
    private lateinit var prefEditors: Editor
    private lateinit var chin: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var fierstPosition = 0
    private var sinodalBible = ArrayList<Int>()
    private var semuxaBible = ArrayList<Int>()
    private lateinit var binding: SearchBibliaBinding
    private var keyword = false
    private var edittext2Focus = false
    private var title = ""
    private var searchJob: Job? = null
    private var histiryJob: Job? = null

    init {
        sinodalBible.add(R.raw.sinaidals1)
        sinodalBible.add(R.raw.sinaidals2)
        sinodalBible.add(R.raw.sinaidals3)
        sinodalBible.add(R.raw.sinaidals4)
        sinodalBible.add(R.raw.sinaidals5)
        sinodalBible.add(R.raw.sinaidals6)
        sinodalBible.add(R.raw.sinaidals7)
        sinodalBible.add(R.raw.sinaidals8)
        sinodalBible.add(R.raw.sinaidals9)
        sinodalBible.add(R.raw.sinaidals10)
        sinodalBible.add(R.raw.sinaidals11)
        sinodalBible.add(R.raw.sinaidals12)
        sinodalBible.add(R.raw.sinaidals13)
        sinodalBible.add(R.raw.sinaidals14)
        sinodalBible.add(R.raw.sinaidals15)
        sinodalBible.add(R.raw.sinaidals16)
        sinodalBible.add(R.raw.sinaidals17)
        sinodalBible.add(R.raw.sinaidals18)
        sinodalBible.add(R.raw.sinaidals19)
        sinodalBible.add(R.raw.sinaidals20)
        sinodalBible.add(R.raw.sinaidals21)
        sinodalBible.add(R.raw.sinaidals22)
        sinodalBible.add(R.raw.sinaidals23)
        sinodalBible.add(R.raw.sinaidals24)
        sinodalBible.add(R.raw.sinaidals25)
        sinodalBible.add(R.raw.sinaidals26)
        sinodalBible.add(R.raw.sinaidals27)
        sinodalBible.add(R.raw.sinaidals28)
        sinodalBible.add(R.raw.sinaidals29)
        sinodalBible.add(R.raw.sinaidals30)
        sinodalBible.add(R.raw.sinaidals31)
        sinodalBible.add(R.raw.sinaidals32)
        sinodalBible.add(R.raw.sinaidals33)
        sinodalBible.add(R.raw.sinaidals34)
        sinodalBible.add(R.raw.sinaidals35)
        sinodalBible.add(R.raw.sinaidals36)
        sinodalBible.add(R.raw.sinaidals37)
        sinodalBible.add(R.raw.sinaidals38)
        sinodalBible.add(R.raw.sinaidals39)
        sinodalBible.add(R.raw.sinaidals40)
        sinodalBible.add(R.raw.sinaidals41)
        sinodalBible.add(R.raw.sinaidals42)
        sinodalBible.add(R.raw.sinaidals43)
        sinodalBible.add(R.raw.sinaidals44)
        sinodalBible.add(R.raw.sinaidals45)
        sinodalBible.add(R.raw.sinaidals46)
        sinodalBible.add(R.raw.sinaidals47)
        sinodalBible.add(R.raw.sinaidals48)
        sinodalBible.add(R.raw.sinaidals49)
        sinodalBible.add(R.raw.sinaidals50)
        sinodalBible.add(R.raw.sinaidaln1)
        sinodalBible.add(R.raw.sinaidaln2)
        sinodalBible.add(R.raw.sinaidaln3)
        sinodalBible.add(R.raw.sinaidaln4)
        sinodalBible.add(R.raw.sinaidaln5)
        sinodalBible.add(R.raw.sinaidaln6)
        sinodalBible.add(R.raw.sinaidaln7)
        sinodalBible.add(R.raw.sinaidaln8)
        sinodalBible.add(R.raw.sinaidaln9)
        sinodalBible.add(R.raw.sinaidaln10)
        sinodalBible.add(R.raw.sinaidaln11)
        sinodalBible.add(R.raw.sinaidaln12)
        sinodalBible.add(R.raw.sinaidaln13)
        sinodalBible.add(R.raw.sinaidaln14)
        sinodalBible.add(R.raw.sinaidaln15)
        sinodalBible.add(R.raw.sinaidaln16)
        sinodalBible.add(R.raw.sinaidaln17)
        sinodalBible.add(R.raw.sinaidaln18)
        sinodalBible.add(R.raw.sinaidaln19)
        sinodalBible.add(R.raw.sinaidaln20)
        sinodalBible.add(R.raw.sinaidaln21)
        sinodalBible.add(R.raw.sinaidaln22)
        sinodalBible.add(R.raw.sinaidaln23)
        sinodalBible.add(R.raw.sinaidaln24)
        sinodalBible.add(R.raw.sinaidaln25)
        sinodalBible.add(R.raw.sinaidaln26)
        sinodalBible.add(R.raw.sinaidaln27)
        semuxaBible.add(R.raw.biblias1)
        semuxaBible.add(R.raw.biblias2)
        semuxaBible.add(R.raw.biblias3)
        semuxaBible.add(R.raw.biblias4)
        semuxaBible.add(R.raw.biblias5)
        semuxaBible.add(R.raw.biblias6)
        semuxaBible.add(R.raw.biblias7)
        semuxaBible.add(R.raw.biblias8)
        semuxaBible.add(R.raw.biblias9)
        semuxaBible.add(R.raw.biblias10)
        semuxaBible.add(R.raw.biblias11)
        semuxaBible.add(R.raw.biblias12)
        semuxaBible.add(R.raw.biblias13)
        semuxaBible.add(R.raw.biblias14)
        semuxaBible.add(R.raw.biblias15)
        semuxaBible.add(R.raw.biblias16)
        semuxaBible.add(R.raw.biblias17)
        semuxaBible.add(R.raw.biblias18)
        semuxaBible.add(R.raw.biblias19)
        semuxaBible.add(R.raw.biblias20)
        semuxaBible.add(R.raw.biblias21)
        semuxaBible.add(R.raw.biblias22)
        semuxaBible.add(R.raw.biblias23)
        semuxaBible.add(R.raw.biblias24)
        semuxaBible.add(R.raw.biblias25)
        semuxaBible.add(R.raw.biblias26)
        semuxaBible.add(R.raw.biblias27)
        semuxaBible.add(R.raw.biblias28)
        semuxaBible.add(R.raw.biblias29)
        semuxaBible.add(R.raw.biblias30)
        semuxaBible.add(R.raw.biblias31)
        semuxaBible.add(R.raw.biblias32)
        semuxaBible.add(R.raw.biblias33)
        semuxaBible.add(R.raw.biblias34)
        semuxaBible.add(R.raw.biblias35)
        semuxaBible.add(R.raw.biblias36)
        semuxaBible.add(R.raw.biblias37)
        semuxaBible.add(R.raw.biblias38)
        semuxaBible.add(R.raw.biblias39)
        semuxaBible.add(R.raw.biblian1)
        semuxaBible.add(R.raw.biblian2)
        semuxaBible.add(R.raw.biblian3)
        semuxaBible.add(R.raw.biblian4)
        semuxaBible.add(R.raw.biblian5)
        semuxaBible.add(R.raw.biblian6)
        semuxaBible.add(R.raw.biblian7)
        semuxaBible.add(R.raw.biblian8)
        semuxaBible.add(R.raw.biblian9)
        semuxaBible.add(R.raw.biblian10)
        semuxaBible.add(R.raw.biblian11)
        semuxaBible.add(R.raw.biblian12)
        semuxaBible.add(R.raw.biblian13)
        semuxaBible.add(R.raw.biblian14)
        semuxaBible.add(R.raw.biblian15)
        semuxaBible.add(R.raw.biblian16)
        semuxaBible.add(R.raw.biblian17)
        semuxaBible.add(R.raw.biblian18)
        semuxaBible.add(R.raw.biblian19)
        semuxaBible.add(R.raw.biblian20)
        semuxaBible.add(R.raw.biblian21)
        semuxaBible.add(R.raw.biblian22)
        semuxaBible.add(R.raw.biblian23)
        semuxaBible.add(R.raw.biblian24)
        semuxaBible.add(R.raw.biblian25)
        semuxaBible.add(R.raw.biblian26)
        semuxaBible.add(R.raw.biblian27)
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
        histiryJob?.cancel()
        prefEditors.putString("search_string_filter", binding.editText2.text.toString())
        prefEditors.putInt("search_bible_fierstPosition", fierstPosition)
        prefEditors.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        prefEditors = chin.edit()
        binding = SearchBibliaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.filterGrup.visibility = View.VISIBLE
        binding.buttonx2.setOnClickListener {
            binding.editText2.setText("")
        }
        DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.buttonx2.setImageResource(by.carkva_gazeta.malitounik.R.drawable.cancel)
        }
        if (intent.getIntExtra("zavet", 1) != zavet) {
            prefEditors.putString("search_string", "")
            prefEditors.putString("search_string_filter", "")
            prefEditors.apply()
        }
        var biblia = "semuxa"
        zavet = intent.getIntExtra("zavet", 1)
        when (zavet) {
            1 -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_semuxa)
                biblia = "semuxa"
            }
            2 -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_sinoidal)
                biblia = "sinoidal"
            }
            3 -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_nadsan)
                biblia = "nadsan"
            }
        }
        if (chin.getString("history_bible_$biblia", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_bible_$biblia", "")
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
            history.addAll(gson.fromJson(json, type))
        }
        adapter = SearchBibliaListAdaprer(this, ArrayList())
        binding.ListView.adapter = adapter
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                fierstPosition = absListView.firstVisiblePosition
                if (i == 1) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
                    searchView?.clearFocus()
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
        if (chin.getString("search_string", "") != "") {
            if (chin.getString("search_array", "") != "") {
                val gson = Gson()
                val json = chin.getString("search_array", "")
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
                val arrayList = ArrayList<String>()
                val arraySpan = ArrayList<Spannable>()
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
                    arraySpan.add(span)
                }
                adapter.addAll(arraySpan)
            }
        }
        binding.editText2.setText(chin.getString("search_string_filter", ""))
        binding.editText2.addTextChangedListener(MyTextWatcher(binding.editText2, true))
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
            binding.History.visibility = View.GONE
            binding.ListView.visibility = View.VISIBLE
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
            autoCompleteTextView?.setText(edit)
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
                    setSettingsBibliaSeash(position)
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
            val edit = autoCompleteTextView?.text.toString()
            execute(edit, true)
        }
        if (chin.getInt("slovocalkam", 0) == 1) binding.checkBox2.isChecked = true
        binding.checkBox2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditors.putInt("slovocalkam", 1)
            } else {
                prefEditors.putInt("slovocalkam", 0)
            }
            prefEditors.apply()
            val edit = autoCompleteTextView?.text.toString()
            execute(edit, true)
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
        if (view.id == androidx.appcompat.R.id.search_edit_frame || view.id == androidx.appcompat.R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == androidx.appcompat.R.id.search_src_text) {
            autoCompleteTextView = view as AutoCompleteTextView
            autoCompleteTextView?.imeOptions = EditorInfo.IME_ACTION_DONE
            autoCompleteTextView?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher(autoCompleteTextView))
            autoCompleteTextView?.setText(chin.getString("search_string", "")) ?: history[0]
            autoCompleteTextView?.setSelection(autoCompleteTextView?.text?.length ?: 0)
            autoCompleteTextView?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
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

    override fun setSettingsPegistrbukv(pegistrbukv: Boolean) {
        binding.checkBox.isChecked = pegistrbukv
    }

    override fun setSettingsSlovocalkam(slovocalkam: Int) {
        binding.checkBox2.isChecked = slovocalkam == 1
    }

    override fun setSettingsBibliaSeash(position: Int) {
        if (zavet != 3) {
            val edit = autoCompleteTextView?.text.toString()
            val bibliaSeash = chin.getInt("biblia_seash", 0)
            binding.spinner6.setSelection(position)
            if (chin.getString("search_string", "") != edit || bibliaSeash != position) {
                prefEditors.putInt("biblia_seash", position)
                prefEditors.apply()
                execute(edit, true)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.search_biblia, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.search)
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                onBack()
                return false
            }
        })
        searchViewItem.expandActionView()
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = title
        val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searcheTextView?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount = menu.findItem(by.carkva_gazeta.malitounik.R.id.count).actionView as TextView
        textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        val closeButton = searchView?.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            prefEditors.putString("search_string", "")
            prefEditors.apply()
            searchJob?.cancel()
            searchView?.setQuery("", false)
            onPostExecute(ArrayList())
        }
        changeSearchViewElements(searchView)
    }

    override fun onPrepareMenu(menu: Menu) {
        val histopy = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_search_bible) {
            val dialogSearshBible = DialogBibleSearshSettings.getInstance(zavet)
            dialogSearshBible.show(supportFragmentManager, "dialogSearshBible")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return true
        }
        return false
    }

    private fun addHistory(item: String) {
        if (histiryJob?.isActive == true) {
            histiryJob?.cancel()
        }
        histiryJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)
            val temp = ArrayList<String>()
            for (i in 0 until history.size) {
                if (history[i] != item) temp.add(history[i])
            }
            history.clear()
            history.add(item)
            for (i in 0 until temp.size) {
                history.add(temp[i])
                if (history.size == 15) break
            }
            if (history.size == 1) invalidateOptionsMenu()
            historyAdapter.notifyDataSetChanged()
            saveHistory()
        }
    }

    private fun saveHistory() {
        var biblia = "semuxa"
        when (zavet) {
            1 -> biblia = "semuxa"
            2 -> biblia = "sinoidal"
            3 -> biblia = "nadsan"
        }
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(history, type)
        prefEditors.putString("history_bible_$biblia", json)
        prefEditors.apply()
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistory()
        invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistory()
        if (history.size == 0) {
            invalidateOptionsMenu()
        }
        historyAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("list_view", binding.ListView.visibility == View.VISIBLE)
        outState.putInt("fierstPosition", fierstPosition)
        prefEditors.putString("search_string", autoCompleteTextView?.text.toString())
        prefEditors.apply()
    }

    private fun execute(searcheString: String, run: Boolean = false) {
        if (searcheString.length >= 3) {
            if (adapter.count == 0 || (history.isNotEmpty() && searcheString != history[0]) || run) {
                binding.History.visibility = View.GONE
                binding.ListView.visibility = View.VISIBLE
                if (searchJob?.isActive == true) {
                    searchJob?.cancel()
                }
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    onPreExecute()
                    val result = withContext(Dispatchers.IO) {
                        return@withContext doInBackground(searcheString)
                    }
                    onPostExecute(result)
                }
            }
        }
    }

    private fun onPreExecute() {
        prefEditors = chin.edit()
        adapter.clear()
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
        binding.progressBar.visibility = View.VISIBLE
        binding.History.visibility = View.GONE
        binding.ListView.visibility = View.GONE
        val edit = autoCompleteTextView?.text.toString()
        if (edit != "") {
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
        }
    }

    private fun doInBackground(searche: String): ArrayList<Spannable> {
        var list = when (zavet) {
            1 -> semuxa(searche)
            2 -> sinoidal(searche)
            3 -> nadsan(searche)
            else -> null
        }
        if (list?.isEmpty() == true && chin.getInt("slovocalkam", 0) == 0) {
            list = when (zavet) {
                1 -> semuxa(searche, true)
                2 -> sinoidal(searche, true)
                3 -> nadsan(searche, true)
                else -> null
            }
        }
        return list ?: ArrayList()
    }

    private fun onPostExecute(result: ArrayList<Spannable>) {
        adapter.clear()
        adapter.addAll(result)
        adapter.filter.filter(binding.editText2.text.toString())
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        if (chin.getString("search_string", "") != "") {
            binding.ListView.post { binding.ListView.setSelection(chin.getInt("search_position", 0)) }
        }
        binding.progressBar.visibility = View.GONE
        val arrayList = ArrayList<String>()
        result.forEach {
            arrayList.add(MainActivity.toHtml(it))
        }
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(arrayList, type)
        prefEditors.putString("search_array", json)
        prefEditors.apply()
        val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        val search = searcheTextView?.text.toString()
        if (search != "" && result.size != 0) {
            binding.ListView.visibility = View.VISIBLE
            addHistory(search)
        }
    }

    private fun semuxa(poshuk: String, secondRun: Boolean = false): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        poshuk1 = MainActivity.zamena(poshuk1, chin.getBoolean("pegistrbukv", true))
        val registr = chin.getBoolean("pegistrbukv", true)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1.length >= 3) {
                    if (poshuk1[r] == aM && r >= 3) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                    }
                }
            }
        }
        val range = when (chin.getInt("biblia_seash", 0)) {
            1 -> 39..42
            2 -> 39 until semuxaBible.size
            3 -> 0..4
            4 -> 0..38
            else -> 0 until semuxaBible.size
        }
        for (i in range) {
            if (searchJob?.isActive == false) break
            val nazva = when (i) {
                0 -> "Быцьцё"
                1 -> "Выхад"
                2 -> "Лявіт"
                3 -> "Лікі"
                4 -> "Другі Закон"
                5 -> "Ісуса сына Нава"
                6 -> "Судзьдзяў"
                7 -> "Рут"
                8 -> "1-я Царстваў"
                9 -> "2-я Царстваў"
                10 -> "3-я Царстваў"
                11 -> "4-я Царстваў"
                12 -> "1-я Летапісаў"
                13 -> "2-я Летапісаў"
                14 -> "Эздры"
                15 -> "Нээміі"
                16 -> "Эстэр"
                17 -> "Ёва"
                18 -> "Псалтыр"
                19 -> "Выслоўяў Саламонавых"
                20 -> "Эклезіяста"
                21 -> "Найвышэйшая Песьня Саламонава"
                22 -> "Ісаі"
                23 -> "Ераміі"
                24 -> "Ераміін Плач"
                25 -> "Езэкііля"
                26 -> "Данііла"
                27 -> "Асіі"
                28 -> "Ёіля"
                29 -> "Амоса"
                30 -> "Аўдзея"
                31 -> "Ёны"
                32 -> "Міхея"
                33 -> "Навума"
                34 -> "Абакума"
                35 -> "Сафона"
                36 -> "Агея"
                37 -> "Захарыі"
                38 -> "Малахіі"
                39 -> "Паводле Мацьвея"
                40 -> "Паводле Марка"
                41 -> "Паводле Лукаша"
                42 -> "Паводле Яна"
                43 -> "Дзеі Апосталаў"
                44 -> "Якава"
                45 -> "1-е Пятра"
                46 -> "2-е Пятра"
                47 -> "1-е Яна Багаслова"
                48 -> "2-е Яна Багаслова"
                49 -> "3-е Яна Багаслова"
                50 -> "Юды"
                51 -> "Да Рымлянаў"
                52 -> "1-е да Карынфянаў"
                53 -> "2-е да Карынфянаў"
                54 -> "Да Галятаў"
                55 -> "Да Эфэсянаў"
                56 -> "Да Піліпянаў"
                57 -> "Да Каласянаў"
                58 -> "1-е да Фесаланікійцаў"
                59 -> "2-е да Фесаланікійцаў"
                60 -> "1-е да Цімафея"
                61 -> "2-е да Цімафея"
                62 -> "Да Ціта"
                63 -> "Да Філімона"
                64 -> "Да Габрэяў"
                65 -> "Адкрыцьцё (Апакаліпсіс)"
                else -> ""
            }
            if (nazva != "") {
                val inputStream = resources.openRawResource(semuxaBible[i])
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
                        prepinanie = prepinanie.replace(",", "")
                        prepinanie = prepinanie.replace(".", "")
                        prepinanie = prepinanie.replace(";", "")
                        prepinanie = prepinanie.replace(":", "")
                        prepinanie = prepinanie.replace(" --", "")
                        val t5 = prepinanie.indexOf("-")
                        if (t5 != -1) {
                            prepinanie = if (prepinanie[t5 - 1].toString() == " ") prepinanie.replace(" -", "")
                            else prepinanie.replace("-", " ")
                        }
                        prepinanie = prepinanie.replace("\"", "")
                        prepinanie = prepinanie.replace("?", "")
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
                                var slovocalkam = false
                                val aSviatyia = MainActivity.fromHtml(bibleline[r])
                                val t2 = poshuk1.length
                                val t4 = prepinanie.indexOf(poshuk1, ignoreCase = registr)
                                if (t4 != -1) {
                                    val charN = prepinanie[t4 - 1].toString()
                                    if (charN == " ") {
                                        val aSvL = prepinanie.length
                                        if (aSvL == t4 + t2) {
                                            slovocalkam = true
                                        } else {
                                            val charK = prepinanie[t4 + t2].toString()
                                            if (charK == " ") {
                                                slovocalkam = true
                                            }
                                        }
                                    }
                                }
                                if (slovocalkam) {
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

    private fun sinoidal(poshuk: String, secondRun: Boolean = false): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        val registr = chin.getBoolean("pegistrbukv", true)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1[r] == aM && r >= 3) {
                    poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                }
            }
        }
        val range = when (chin.getInt("biblia_seash", 0)) {
            1 -> 50..53
            2 -> 50 until sinodalBible.size
            3 -> 0..4
            4 -> 0..49
            else -> 0 until sinodalBible.size
        }
        for (i in range) {
            if (searchJob?.isActive == false) break
            val nazva = when (i) {
                0 -> "Бытие"
                1 -> "Исход"
                2 -> "Левит"
                3 -> "Числа"
                4 -> "Второзаконие"
                5 -> "Иисуса Навина"
                6 -> "Судей израилевых"
                7 -> "Руфи"
                8 -> "1-я Царств"
                9 -> "2-я Царств"
                10 -> "3-я Царств"
                11 -> "4-я Царств"
                12 -> "1-я Паралипоменон"
                13 -> "2-я Паралипоменон"
                14 -> "1-я Ездры"
                15 -> "Неемии"
                16 -> "2-я Ездры"
                17 -> "Товита"
                18 -> "Иудифи"
                19 -> "Есфири"
                20 -> "Иова"
                21 -> "Псалтирь"
                22 -> "Притчи Соломона"
                23 -> "Екклезиаста"
                24 -> "Песнь песней Соломона"
                25 -> "Премудрости Соломона"
                26 -> "Премудрости Иисуса, сына Сирахова"
                27 -> "Исаии"
                28 -> "Иеремии"
                29 -> "Плач Иеремии"
                30 -> "Послание Иеремии"
                31 -> "Варуха"
                32 -> "Иезекииля"
                33 -> "Даниила"
                34 -> "Осии"
                35 -> "Иоиля"
                36 -> "Амоса"
                37 -> "Авдия"
                38 -> "Ионы"
                39 -> "Михея"
                40 -> "Наума"
                41 -> "Аввакума"
                42 -> "Сафонии"
                43 -> "Аггея"
                44 -> "Захарии"
                45 -> "Малахии"
                46 -> "1-я Маккавейская"
                47 -> "2-я Маккавейская"
                48 -> "3-я Маккавейская"
                49 -> "3-я Ездры"
                50 -> "От Матфея"
                51 -> "От Марка"
                52 -> "От Луки"
                53 -> "От Иоанна"
                54 -> "Деяния святых апостолов"
                55 -> "Иакова"
                56 -> "1-е Петра"
                57 -> "2-е Петра"
                58 -> "1-е Иоанна"
                59 -> "2-е Иоанна"
                60 -> "3-е Иоанна"
                61 -> "Иуды"
                62 -> "Римлянам"
                63 -> "1-е Коринфянам"
                64 -> "2-е Коринфянам"
                65 -> "Галатам"
                66 -> "Эфэсянам"
                67 -> "Филиппийцам"
                68 -> "Колоссянам"
                69 -> "1-е Фессалоникийцам (Солунянам)"
                70 -> "2-е Фессалоникийцам (Солунянам)"
                71 -> "1-е Тимофею"
                72 -> "2-е Тимофею"
                73 -> "Титу"
                74 -> "Филимону"
                75 -> "Евреям"
                76 -> "Откровение (Апокалипсис)"
                else -> ""
            }
            if (nazva != "") {
                val inputStream = resources.openRawResource(sinodalBible[i])
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
                        prepinanie = prepinanie.replace(",", "")
                        prepinanie = prepinanie.replace(".", "")
                        prepinanie = prepinanie.replace(";", "")
                        prepinanie = prepinanie.replace(":", "")
                        prepinanie = prepinanie.replace("[", "")
                        prepinanie = prepinanie.replace("]", "")
                        prepinanie = prepinanie.replace(" --", "")
                        val t5 = prepinanie.indexOf("-")
                        if (t5 != -1) {
                            prepinanie = if (prepinanie[t5 - 1].toString() == " ") prepinanie.replace(" -", "")
                            else prepinanie.replace("-", " ")
                        }
                        prepinanie = prepinanie.replace("\"", "")
                        prepinanie = prepinanie.replace("?", "")
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
                                var slovocalkam = false
                                var aSviatyia = bibleline[r]
                                aSviatyia = aSviatyia.replace("\\n", "\n")
                                val t2 = poshuk1.length
                                val t4 = prepinanie.indexOf(poshuk1, ignoreCase = registr)
                                if (t4 != -1) {
                                    val charN = prepinanie[t4 - 1].toString()
                                    if (charN == " ") {
                                        val aSvL = prepinanie.length
                                        if (aSvL == t4 + t2) {
                                            slovocalkam = true
                                        } else {
                                            val charK = prepinanie[t4 + t2].toString()
                                            if (charK == " ") {
                                                slovocalkam = true
                                            }
                                        }
                                    }
                                }
                                if (slovocalkam) {
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

    private fun nadsan(poshuk: String, secondRun: Boolean = false): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        poshuk1 = MainActivity.zamena(poshuk1)
        val registr = chin.getBoolean("pegistrbukv", true)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1.length >= 3) {
                    if (poshuk1[r] == aM && r >= 3) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                    }
                }
            }
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
                prepinanie = prepinanie.replace(" --", "")
                val t5 = prepinanie.indexOf("-")
                if (t5 != -1) {
                    prepinanie = if (prepinanie[t5 - 1].toString() == " ") prepinanie.replace(" -", "")
                    else prepinanie.replace("-", " ")
                }
                prepinanie = prepinanie.replace("\"", "")
                prepinanie = prepinanie.replace("?", "")
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
                        var slovocalkam = false
                        val aSviatyia = MainActivity.fromHtml(bibleline[r])
                        val t2 = poshuk1.length
                        val t4 = prepinanie.indexOf(poshuk1, ignoreCase = registr)
                        if (t4 != -1) {
                            val charN = prepinanie[t4 - 1].toString()
                            if (charN == " ") {
                                val aSvL = prepinanie.length
                                if (aSvL == t4 + t2) {
                                    slovocalkam = true
                                } else {
                                    val charK = prepinanie[t4 + t2].toString()
                                    if (charK == " ") {
                                        slovocalkam = true
                                    }
                                }
                            }
                        }
                        if (slovocalkam) {
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
                    edit = edit.replace("и", "і")
                    edit = edit.replace("щ", "ў")
                    edit = edit.replace("И", "І")
                    edit = edit.replace("Щ", "Ў")
                    edit = edit.replace("ъ", "'")
                } else {
                    edit = edit.replace("і", "и")
                    edit = edit.replace("ў", "щ")
                    edit = edit.replace("І", "И")
                    edit = edit.replace("Ў", "Щ")
                    edit = edit.replace("'", "ъ")
                }
                if (check != 0) {
                    editText?.removeTextChangedListener(this)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
                if (editText?.id == androidx.appcompat.R.id.search_src_text) {
                    histiryJob?.cancel()
                    if (searchJob?.isActive == true && editText.text.length < 3) {
                        searchJob?.cancel()
                        binding.progressBar.visibility = View.GONE
                    } else {
                        if (chin.getString("search_string", "") != edit) execute(edit)
                    }
                }
            }
            if (editText?.id == androidx.appcompat.R.id.search_src_text) {
                if (editText.text.length >= 3) {
                    binding.History.visibility = View.GONE
                    binding.ListView.visibility = View.VISIBLE
                } else {
                    binding.History.visibility = View.VISIBLE
                    binding.ListView.visibility = View.GONE
                }
            }
            if (filtep) adapter.filter.filter(edit)
        }

    }

    private inner class SearchBibliaListAdaprer(context: Activity, private val arrayList: ArrayList<Spannable>) : ArrayAdapter<Spannable>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, arrayList) {
        private val origData = ArrayList<Spannable>(arrayList)
        override fun addAll(collection: Collection<Spannable>) {
            super.addAll(collection)
            origData.clear()
            origData.addAll(collection)
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
            val t1 = arrayList[position].indexOf("-->")
            viewHolder.text.text = arrayList[position].subSequence(t1 + 3, arrayList[position].length)
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
                    textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, results.count)
                }
            }
        }
    }

    private class SearchSpinnerAdapter(private val context: Activity, private val name: Array<String>) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_4, name) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.gravity = Gravity.START
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
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
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
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