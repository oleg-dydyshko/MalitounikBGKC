package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.DialogBibleSearshSettings.DiallogBibleSearshListiner
import by.carkva_gazeta.resources.R.raw
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.search_biblia.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 5.10.16
 */
class SearchBiblia : AppCompatActivity(), View.OnClickListener, DiallogBibleSearshListiner, DialogClearHishory.DialogClearHistoryListener {
    private var seash: ArrayList<String> = ArrayList()
    private lateinit var adapter: SearchBibliaListAdaprer
    private lateinit var prefEditors: Editor
    private lateinit var chin: SharedPreferences
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var textViewCount: TextViewRobotoCondensed? = null
    private var searchView: SearchView? = null
    private var title = ""
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var actionExpandOn = false
    private var fierstPosition = 0

    override fun onPause() {
        super.onPause()
        prefEditors.putString("search_string_filter", editText2.text.toString())
        prefEditors.putInt("search_bible_fierstPosition", fierstPosition)
        prefEditors.apply()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onSetSettings(edit: String?) {
        edit?.let {
            if (edit.length >= 3) {
                addHistory(it)
                saveHistory()
                execute(edit)
                Histopy.visibility = View.GONE
                ListView.visibility = View.VISIBLE
            }
        }
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
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_biblia)
        buttonx2.visibility = View.VISIBLE
        buttonx2.setOnClickListener(this)
        if (dzenNoch) {
            buttonx2.setImageResource(by.carkva_gazeta.malitounik.R.drawable.cancel)
        }
        editText2.visibility = View.VISIBLE
        editText2.addTextChangedListener(MyTextWatcher(editText2, true))
        if (intent.getIntExtra("zavet", 1) != zavet) {
            prefEditors.putString("search_string", "")
            prefEditors.putString("search_string_filter", "")
            prefEditors.apply()
        }
        if (chin.getString("search_string", "") != "") {
            if (chin.getString("search_array", "") != "") {
                val gson = Gson()
                val json = chin.getString("search_array", "")
                val type = object : TypeToken<ArrayList<String?>?>() {}.type
                seash.addAll(gson.fromJson(json, type))
                actionExpandOn = true
            }
        }
        zavet = intent.getIntExtra("zavet", 1)
        var biblia = "semuxa"
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
        ListView.adapter = adapter
        ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                fierstPosition = absListView.firstVisiblePosition
                if (i == 1) { // Скрываем клавиатуру
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
        editText2.setText(chin.getString("search_string_filter", ""))
        ListView.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, _: Long ->
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
                //if (strText.contains("От Матфея")) nazva = 0;
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
                //if (strText.contains("Паводле Мацьвея")) nazva = 0;
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
                prefEditors.putInt("search_position", ListView.firstVisiblePosition)
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
                        prefEditors.putInt("search_position", ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@SearchBiblia, StaryZapavietSinaidal::class.java)
                        intent.putExtra("kniga", nazvaS)
                        intent.putExtra("glava", glava - 1)
                        intent.putExtra("stix", stix - 1)
                        prefEditors.putBoolean("novyzavet", false)
                        prefEditors.putInt("search_position", ListView.firstVisiblePosition)
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
                        prefEditors.putInt("search_position", ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@SearchBiblia, NovyZapavietSinaidal::class.java)
                        intent.putExtra("kniga", nazva)
                        intent.putExtra("glava", glava - 1)
                        intent.putExtra("stix", stix - 1)
                        prefEditors.putBoolean("novyzavet", true)
                        prefEditors.putInt("search_position", ListView.firstVisiblePosition)
                        prefEditors.apply()
                        startActivity(intent)
                    }
                }
            }
        }
        historyAdapter = HistoryAdapter(this, history)
        Histopy.adapter = historyAdapter
        Histopy.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val edit = history[position]
            addHistory(edit)
            saveHistory()
            Histopy.visibility = View.GONE
            ListView.visibility = View.VISIBLE
            searche = true
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
            autoCompleteTextView?.setText(edit)
            searchView?.clearFocus()
            execute(edit)
        }
        Histopy.setOnItemLongClickListener { _, _, position, _ ->
            val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return@setOnItemLongClickListener true
        }
        if (savedInstanceState != null) {
            val listView = savedInstanceState.getBoolean("list_view")
            if (listView) ListView.visibility = View.VISIBLE
            actionExpandOn = listView
            fierstPosition = savedInstanceState.getInt("fierstPosition")
        } else {
            fierstPosition = chin.getInt("search_bible_fierstPosition", 0)
        }
        ListView.setSelection(fierstPosition)
        setBibleSinodal()
        setBibleSemuxa()
        setTollbarTheme(title)
    }

    private fun setTollbarTheme(title: String) {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.text = title
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                autoCompleteTextView?.setTextCursorDrawable(by.carkva_gazeta.malitounik.R.color.colorIcons)
            } else {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(autoCompleteTextView, 0)
            }
            val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher(autoCompleteTextView))
            autoCompleteTextView?.setText(chin.getString("search_string", ""))
            autoCompleteTextView?.setSelection(autoCompleteTextView?.text?.length ?: 0)
            autoCompleteTextView?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val edit = autoCompleteTextView?.text.toString()
                    if (edit.length >= 3) {
                        addHistory(edit)
                        saveHistory()
                        Histopy.visibility = View.GONE
                        ListView.visibility = View.VISIBLE
                        execute(edit)
                    } else {
                        MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.seashmin))
                    }
                }
                false
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
        textViewCount = menu.findItem(by.carkva_gazeta.malitounik.R.id.count).actionView as TextViewRobotoCondensed
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_settings) {
            val dialogBiblesearshsettings = DialogBibleSearshSettings.getInstance(autoCompleteTextView?.text.toString())
            dialogBiblesearshsettings.show(supportFragmentManager, "bible_searsh_settings")
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
            editText2.setText("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("list_view", ListView.visibility == View.VISIBLE)
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
        progressBar.visibility = View.VISIBLE
        ListView.visibility = View.GONE
        var edit = autoCompleteTextView?.text.toString()
        if (edit != "") {
            edit = edit.trim()
            autoCompleteTextView?.setText(edit)
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
        }
    }

    private fun doInBackground(searche: String): ArrayList<String> {
        return when (zavet) {
            1 -> semuxa(this@SearchBiblia, searche)
            2 -> sinoidal(this@SearchBiblia, searche)
            3 -> nadsan(this@SearchBiblia, searche)
            else -> ArrayList()
        }
    }

    private fun onPostExecute(result: ArrayList<String>) {
        adapter.addAll(result)
        adapter.filter.filter(editText2?.text.toString())
        textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        if (chin.getString("search_string", "") != "") {
            ListView?.post { ListView.setSelection(chin.getInt("search_position", 0)) }
        }
        progressBar.visibility = View.GONE
        ListView.visibility = View.VISIBLE
        val gson = Gson()
        val json = gson.toJson(result)
        prefEditors.putString("search_array", json)
        prefEditors.apply()
        searche = false
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
                    edit = edit.replace("ъ", "'")
                    edit = edit.replace("И", "І")
                    edit = edit.replace("Щ", "Ў")
                    edit = edit.replace("Ъ", "'")
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
                    Histopy.visibility = View.GONE
                    ListView.visibility = View.VISIBLE
                    actionExpandOn = false
                } else {
                    if (searche && editPosition != 0) {
                        Histopy.visibility = View.GONE
                        ListView.visibility = View.VISIBLE
                        editText.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    } else {
                        Histopy.visibility = View.VISIBLE
                        ListView.visibility = View.GONE
                        textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
                    }
                }
            }
            if (filtep) adapter.filter.filter(edit)
        }

    }

    internal inner class SearchBibliaListAdaprer(context: Activity) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, seash) {
        private val k: SharedPreferences = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val origData: ArrayList<String> = ArrayList(seash)
        private val activity: Activity = context
        override fun addAll(collection: Collection<String>) {
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
                rootView = activity.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text?.text = MainActivity.fromHtml(seash[position])
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            } else {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_white)
            }
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    var charSequence = constraint
                    charSequence = charSequence.toString().toLowerCase(Locale.getDefault())
                    val result = FilterResults()
                    if (charSequence.toString().isNotEmpty()) {
                        val founded = ArrayList<String>()
                        for (item in origData) {
                            if (item.toLowerCase(Locale.getDefault()).contains(charSequence)) {
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
                    var color = "<font color=#d00505>"
                    if (dzenNoch) color = "<font color=#f44336>"
                    for (item in results.values as ArrayList<*>) {
                        var t1 = item.toString().toLowerCase(Locale.getDefault()).indexOf("-->")
                        if (t1 == -1) t1 = 0
                        else t1+=3
                        val itm = item.toString().toLowerCase(Locale.getDefault()).indexOf(constraint.toString().toLowerCase(Locale.getDefault()), t1)
                        val itmcount = constraint.toString().length
                        if (itm != -1)
                            add(item.toString().substring(0, itm) + color + item.toString().substring(itm, itm + itmcount) + "</font>" + item.toString().substring(itm + itmcount))
                        else
                            add(item.toString())
                    }
                    textViewCount?.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, results.count)
                }
            }
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        private var zavet = 1
        private var setSinodalBible: ArrayMap<String, Int> = ArrayMap()
        private var setSemuxaBible: ArrayMap<String, Int> = ArrayMap()
        private var searche = false

        private fun setBibleSinodal() {
            val fields = raw::class.java.fields
            for (field in fields) {
                if (field.name.contains("sinaidal")) {
                    setSinodalBible[field.name] = field.getInt(null)
                }
            }
        }

        private fun setBibleSemuxa() {
            val fields = raw::class.java.fields
            for (field in fields) {
                if (field.name.contains("biblia")) {
                    setSemuxaBible[field.name] = field.getInt(null)
                }
            }
        }

        private fun zamena(replase: String): String {
            var replase1 = replase
            replase1 = replase1.replace("ё", "е")
            replase1 = replase1.replace("и", "і")
            replase1 = replase1.replace("щ", "ў")
            replase1 = replase1.replace("ъ", "'")
            replase1 = replase1.replace("све", "сьве")
            replase1 = replase1.replace("сві", "сьві")
            replase1 = replase1.replace("свя", "сьвя")
            replase1 = replase1.replace("зве", "зьве")
            replase1 = replase1.replace("зві", "зьві")
            replase1 = replase1.replace("звя", "зьвя")
            replase1 = replase1.replace("зме", "зьме")
            replase1 = replase1.replace("змі", "зьмі")
            replase1 = replase1.replace("змя", "зьмя")
            replase1 = replase1.replace("зня", "зьня")
            replase1 = replase1.replace("сле", "сьле")
            replase1 = replase1.replace("слі", "сьлі")
            replase1 = replase1.replace("сль", "сьль")
            replase1 = replase1.replace("слю", "сьлю")
            replase1 = replase1.replace("сля", "сьля")
            replase1 = replase1.replace("сне", "сьне")
            replase1 = replase1.replace("сні", "сьні")
            replase1 = replase1.replace("сню", "сьню")
            replase1 = replase1.replace("сня", "сьня")
            replase1 = replase1.replace("спе", "сьпе")
            replase1 = replase1.replace("спі", "сьпі")
            replase1 = replase1.replace("спя", "сьпя")
            replase1 = replase1.replace("сце", "сьце")
            replase1 = replase1.replace("сці", "сьці")
            replase1 = replase1.replace("сць", "сьць")
            replase1 = replase1.replace("сцю", "сьцю")
            replase1 = replase1.replace("сця", "сьця")
            replase1 = replase1.replace("цце", "цьце")
            replase1 = replase1.replace("цці", "цьці")
            replase1 = replase1.replace("ццю", "цьцю")
            replase1 = replase1.replace("ззе", "зьзе")
            replase1 = replase1.replace("ззі", "зьзі")
            replase1 = replase1.replace("ззю", "зьзю")
            replase1 = replase1.replace("ззя", "зьзя")
            replase1 = replase1.replace("зле", "зьле")
            replase1 = replase1.replace("злі", "зьлі")
            replase1 = replase1.replace("злю", "зьлю")
            replase1 = replase1.replace("зля", "зьля")
            replase1 = replase1.replace("збе", "зьбе")
            replase1 = replase1.replace("збі", "зьбі")
            replase1 = replase1.replace("збя", "зьбя")
            replase1 = replase1.replace("нне", "ньне")
            replase1 = replase1.replace("нні", "ньні")
            replase1 = replase1.replace("нню", "ньню")
            replase1 = replase1.replace("ння", "ньня")
            replase1 = replase1.replace("лле", "льле")
            replase1 = replase1.replace("ллі", "льлі")
            replase1 = replase1.replace("ллю", "льлю")
            replase1 = replase1.replace("лля", "льля")
            replase1 = replase1.replace("дск", "дзк")
            replase1 = replase1.replace("дств", "дзтв")
            replase1 = replase1.replace("з’е", "зье")
            replase1 = replase1.replace("з’я", "зья")
            return replase1
        }

        private fun semuxa(context: Context, poshuk: String): ArrayList<String> {
            var poshuk1 = poshuk
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin?.getBoolean("dzen_noch", false)
            val seashpost = ArrayList<String>()
            if (poshuk1 != "") {
                poshuk1 = zamena(poshuk1)
                if (chin?.getInt("pegistr", 0) == 0) poshuk1 = poshuk1.toLowerCase(Locale.getDefault())
                if (chin?.getInt("slovocalkam", 0) == 0) {
                    val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
                    for (aM in m) {
                        val r = poshuk1.length - 1
                        if (poshuk1.length >= 3) {
                            if (poshuk1[r] == aM) {
                                poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r))
                            }
                        }
                    }
                } else {
                    poshuk1 = " $poshuk1 "
                }
                var color = "<font color=#d00505>"
                if (dzenNoch == true) color = "<font color=#f44336>"
                for (i in 0 until setSemuxaBible.size) {
                    var biblia = "biblia"
                    if (chin?.getInt("biblia_seash", 0) == 1) biblia = "biblian"
                    if (chin?.getInt("biblia_seash", 0) == 2) biblia = "biblias"
                    if (setSemuxaBible.keyAt(i).contains(biblia)) {
                        var nazva = ""
                        if (setSemuxaBible.keyAt(i).contains("biblias1")) nazva = "Быцьцё"
                        if (setSemuxaBible.keyAt(i).contains("biblias2")) nazva = "Выхад"
                        if (setSemuxaBible.keyAt(i).contains("biblias3")) nazva = "Лявіт"
                        if (setSemuxaBible.keyAt(i).contains("biblias4")) nazva = "Лікі"
                        if (setSemuxaBible.keyAt(i).contains("biblias5")) nazva = "Другі Закон"
                        if (setSemuxaBible.keyAt(i).contains("biblias6")) nazva = "Ісуса сына Нава"
                        if (setSemuxaBible.keyAt(i).contains("biblias7")) nazva = "Судзьдзяў"
                        if (setSemuxaBible.keyAt(i).contains("biblias8")) nazva = "Рут"
                        if (setSemuxaBible.keyAt(i).contains("biblias9")) nazva = "1-я Царстваў"
                        if (setSemuxaBible.keyAt(i).contains("biblias10")) nazva = "2-я Царстваў"
                        if (setSemuxaBible.keyAt(i).contains("biblias11")) nazva = "3-я Царстваў"
                        if (setSemuxaBible.keyAt(i).contains("biblias12")) nazva = "4-я Царстваў"
                        if (setSemuxaBible.keyAt(i).contains("biblias13")) nazva = "1-я Летапісаў"
                        if (setSemuxaBible.keyAt(i).contains("biblias14")) nazva = "2-я Летапісаў"
                        if (setSemuxaBible.keyAt(i).contains("biblias15")) nazva = "Эздры"
                        if (setSemuxaBible.keyAt(i).contains("biblias16")) nazva = "Нээміі"
                        if (setSemuxaBible.keyAt(i).contains("biblias17")) nazva = "Эстэр"
                        if (setSemuxaBible.keyAt(i).contains("biblias18")) nazva = "Ёва"
                        if (setSemuxaBible.keyAt(i).contains("biblias19")) nazva = "Псалтыр"
                        if (setSemuxaBible.keyAt(i).contains("biblias20")) nazva = "Выслоўяў Саламонавых"
                        if (setSemuxaBible.keyAt(i).contains("biblias21")) nazva = "Эклезіяста"
                        if (setSemuxaBible.keyAt(i).contains("biblias22")) nazva = "Найвышэйшая Песьня Саламонава"
                        if (setSemuxaBible.keyAt(i).contains("biblias23")) nazva = "Ісаі"
                        if (setSemuxaBible.keyAt(i).contains("biblias24")) nazva = "Ераміі"
                        if (setSemuxaBible.keyAt(i).contains("biblias25")) nazva = "Ераміін Плач"
                        if (setSemuxaBible.keyAt(i).contains("biblias26")) nazva = "Езэкііля"
                        if (setSemuxaBible.keyAt(i).contains("biblias27")) nazva = "Данііла"
                        if (setSemuxaBible.keyAt(i).contains("biblias28")) nazva = "Асіі"
                        if (setSemuxaBible.keyAt(i).contains("biblias29")) nazva = "Ёіля"
                        if (setSemuxaBible.keyAt(i).contains("biblias30")) nazva = "Амоса"
                        if (setSemuxaBible.keyAt(i).contains("biblias31")) nazva = "Аўдзея"
                        if (setSemuxaBible.keyAt(i).contains("biblias32")) nazva = "Ёны"
                        if (setSemuxaBible.keyAt(i).contains("biblias33")) nazva = "Міхея"
                        if (setSemuxaBible.keyAt(i).contains("biblias34")) nazva = "Навума"
                        if (setSemuxaBible.keyAt(i).contains("biblias35")) nazva = "Абакума"
                        if (setSemuxaBible.keyAt(i).contains("biblias36")) nazva = "Сафона"
                        if (setSemuxaBible.keyAt(i).contains("biblias37")) nazva = "Агея"
                        if (setSemuxaBible.keyAt(i).contains("biblias38")) nazva = "Захарыі"
                        if (setSemuxaBible.keyAt(i).contains("biblias39")) nazva = "Малахіі"
                        if (setSemuxaBible.keyAt(i).contains("biblian1")) nazva = "Паводле Мацьвея"
                        if (setSemuxaBible.keyAt(i).contains("biblian2")) nazva = "Паводле Марка"
                        if (setSemuxaBible.keyAt(i).contains("biblian3")) nazva = "Паводле Лукаша"
                        if (setSemuxaBible.keyAt(i).contains("biblian4")) nazva = "Паводле Яна"
                        if (setSemuxaBible.keyAt(i).contains("biblian5")) nazva = "Дзеі Апосталаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian6")) nazva = "Якава"
                        if (setSemuxaBible.keyAt(i).contains("biblian7")) nazva = "1-е Пятра"
                        if (setSemuxaBible.keyAt(i).contains("biblian8")) nazva = "2-е Пятра"
                        if (setSemuxaBible.keyAt(i).contains("biblian9")) nazva = "1-е Яна Багаслова"
                        if (setSemuxaBible.keyAt(i).contains("biblian10")) nazva = "2-е Яна Багаслова"
                        if (setSemuxaBible.keyAt(i).contains("biblian11")) nazva = "3-е Яна Багаслова"
                        if (setSemuxaBible.keyAt(i).contains("biblian12")) nazva = "Юды"
                        if (setSemuxaBible.keyAt(i).contains("biblian13")) nazva = "Да Рымлянаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian14")) nazva = "1-е да Карынфянаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian15")) nazva = "2-е да Карынфянаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian16")) nazva = "Да Галятаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian17")) nazva = "Да Эфэсянаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian18")) nazva = "Да Піліпянаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian19")) nazva = "Да Каласянаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian20")) nazva = "1-е да Фесаланікійцаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian21")) nazva = "2-е да Фесаланікійцаў"
                        if (setSemuxaBible.keyAt(i).contains("biblian22")) nazva = "1-е да Цімафея"
                        if (setSemuxaBible.keyAt(i).contains("biblian23")) nazva = "2-е да Цімафея"
                        if (setSemuxaBible.keyAt(i).contains("biblian24")) nazva = "Да Ціта"
                        if (setSemuxaBible.keyAt(i).contains("biblian25")) nazva = "Да Філімона"
                        if (setSemuxaBible.keyAt(i).contains("biblian26")) nazva = "Да Габрэяў"
                        if (setSemuxaBible.keyAt(i).contains("biblian27")) nazva = "Адкрыцьцё (Апакаліпсіс)"
                        val inputStream = context.resources.openRawResource(setSemuxaBible.valueAt(i))
                        val isr = InputStreamReader(inputStream)
                        val reader = BufferedReader(isr)
                        var glava = 0
                        val split = reader.readText().split("===")
                        inputStream.close()
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
                                if (chin?.getInt("slovocalkam", 0) == 1) prepinanie = " " + bibleline[r] + " "
                                if (chin?.getInt("pegistr", 0) == 0) prepinanie = prepinanie.toLowerCase(Locale.getDefault())
                                val t1a = prepinanie.indexOf(poshuk1)
                                prepinanie = prepinanie.replace(",", "")
                                prepinanie = prepinanie.replace(".", "")
                                prepinanie = prepinanie.replace(";", "")
                                prepinanie = prepinanie.replace(":", "")
                                prepinanie = prepinanie.replace("-", "")
                                prepinanie = prepinanie.replace("\"", "")
                                var count = 0
                                if (t1a != -1) count = t1a - prepinanie.indexOf(poshuk1)
                                prepinanie = prepinanie.replace("ё", "е")
                                prepinanie = prepinanie.replace("<em>", "")
                                prepinanie = prepinanie.replace("</em>", " ")
                                prepinanie = prepinanie.replace("<br>", "")
                                prepinanie = prepinanie.replace("<strong>", "")
                                prepinanie = prepinanie.replace("</strong>", " ")
                                if (chin?.getInt("slovocalkam", 0) == 0) {
                                    if (prepinanie.contains(poshuk1)) {
                                        var aSviatyia = bibleline[r]
                                        var t1 = prepinanie.indexOf(poshuk1)
                                        t1 += count
                                        val t2 = poshuk1.length
                                        aSviatyia = aSviatyia.substring(0, t1) + color + aSviatyia.substring(t1, t1 + t2) + "</font>" + aSviatyia.substring(t1 + t2)
                                        seashpost.add("<!--stix.$stix::glava.$glava--><strong>$nazva Гл. $glava</strong><br>$aSviatyia")
                                    }
                                } else {
                                    if (prepinanie.contains(poshuk1)) {
                                        var aSviatyia = bibleline[r]
                                        var t1 = prepinanie.indexOf(poshuk1)
                                        t1 += count
                                        val t2 = poshuk1.length
                                        aSviatyia = aSviatyia.substring(0, t1) + color + aSviatyia.substring(t1, t1 + t2) + "</font>" + aSviatyia.substring(t1 + t2)
                                        seashpost.add("<!--stix.$stix::glava.$glava--><strong>$nazva Гл. $glava</strong><br>$aSviatyia")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return seashpost
        }

        private fun sinoidal(context: Context, poshuk: String): ArrayList<String> {
            var poshuk1 = poshuk
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val seashpost = ArrayList<String>()
            if (poshuk1 != "") {
                poshuk1 = poshuk1.replace("ё", "е")
                if (chin?.getInt("pegistr", 0) == 0) poshuk1 = poshuk1.toLowerCase(Locale.getDefault())
                if (chin?.getInt("slovocalkam", 0) == 0) {
                    val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
                    for (aM in m) {
                        val r = poshuk1.length - 1
                        if (poshuk1[r] == aM) {
                            poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r))
                        }
                    }
                } else {
                    poshuk1 = " $poshuk1 "
                }
                var color = "<font color=#d00505>"
                if (dzenNoch) color = "<font color=#f44336>"
                for (i in 0 until setSinodalBible.size) {
                    var biblia = "sinaidal"
                    if (chin?.getInt("biblia_seash", 0) == 1) biblia = "sinaidaln"
                    if (chin?.getInt("biblia_seash", 0) == 2) biblia = "sinaidals"
                    if (setSinodalBible.keyAt(i).contains(biblia)) {
                        var nazva = ""
                        if (setSinodalBible.keyAt(i).contains("sinaidals1")) nazva = "Бытие"
                        if (setSinodalBible.keyAt(i).contains("sinaidals2")) nazva = "Исход"
                        if (setSinodalBible.keyAt(i).contains("sinaidals3")) nazva = "Левит"
                        if (setSinodalBible.keyAt(i).contains("sinaidals4")) nazva = "Числа"
                        if (setSinodalBible.keyAt(i).contains("sinaidals5")) nazva = "Второзаконие"
                        if (setSinodalBible.keyAt(i).contains("sinaidals6")) nazva = "Иисуса Навина"
                        if (setSinodalBible.keyAt(i).contains("sinaidals7")) nazva = "Судей израилевых"
                        if (setSinodalBible.keyAt(i).contains("sinaidals8")) nazva = "Руфи"
                        if (setSinodalBible.keyAt(i).contains("sinaidals9")) nazva = "1-я Царств"
                        if (setSinodalBible.keyAt(i).contains("sinaidals10")) nazva = "2-я Царств"
                        if (setSinodalBible.keyAt(i).contains("sinaidals11")) nazva = "3-я Царств"
                        if (setSinodalBible.keyAt(i).contains("sinaidals12")) nazva = "4-я Царств"
                        if (setSinodalBible.keyAt(i).contains("sinaidals13")) nazva = "1-я Паралипоменон"
                        if (setSinodalBible.keyAt(i).contains("sinaidals14")) nazva = "2-я Паралипоменон"
                        if (setSinodalBible.keyAt(i).contains("sinaidals15")) nazva = "1-я Ездры"
                        if (setSinodalBible.keyAt(i).contains("sinaidals16")) nazva = "Неемии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals17")) nazva = "2-я Ездры"
                        if (setSinodalBible.keyAt(i).contains("sinaidals18")) nazva = "Товита"
                        if (setSinodalBible.keyAt(i).contains("sinaidals19")) nazva = "Иудифи"
                        if (setSinodalBible.keyAt(i).contains("sinaidals20")) nazva = "Есфири"
                        if (setSinodalBible.keyAt(i).contains("sinaidals21")) nazva = "Иова"
                        if (setSinodalBible.keyAt(i).contains("sinaidals22")) nazva = "Псалтирь"
                        if (setSinodalBible.keyAt(i).contains("sinaidals23")) nazva = "Притчи Соломона"
                        if (setSinodalBible.keyAt(i).contains("sinaidals24")) nazva = "Екклезиаста"
                        if (setSinodalBible.keyAt(i).contains("sinaidals25")) nazva = "Песнь песней Соломона"
                        if (setSinodalBible.keyAt(i).contains("sinaidals26")) nazva = "Премудрости Соломона"
                        if (setSinodalBible.keyAt(i).contains("sinaidals27")) nazva = "Премудрости Иисуса, сына Сирахова"
                        if (setSinodalBible.keyAt(i).contains("sinaidals28")) nazva = "Исаии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals29")) nazva = "Иеремии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals30")) nazva = "Плач Иеремии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals31")) nazva = "Послание Иеремии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals32")) nazva = "Варуха"
                        if (setSinodalBible.keyAt(i).contains("sinaidals33")) nazva = "Иезекииля"
                        if (setSinodalBible.keyAt(i).contains("sinaidals34")) nazva = "Даниила"
                        if (setSinodalBible.keyAt(i).contains("sinaidals35")) nazva = "Осии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals36")) nazva = "Иоиля"
                        if (setSinodalBible.keyAt(i).contains("sinaidals37")) nazva = "Амоса"
                        if (setSinodalBible.keyAt(i).contains("sinaidals38")) nazva = "Авдия"
                        if (setSinodalBible.keyAt(i).contains("sinaidals39")) nazva = "Ионы"
                        if (setSinodalBible.keyAt(i).contains("sinaidals40")) nazva = "Михея"
                        if (setSinodalBible.keyAt(i).contains("sinaidals41")) nazva = "Наума"
                        if (setSinodalBible.keyAt(i).contains("sinaidals42")) nazva = "Аввакума"
                        if (setSinodalBible.keyAt(i).contains("sinaidals43")) nazva = "Сафонии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals44")) nazva = "Аггея"
                        if (setSinodalBible.keyAt(i).contains("sinaidals45")) nazva = "Захарии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals46")) nazva = "Малахии"
                        if (setSinodalBible.keyAt(i).contains("sinaidals47")) nazva = "1-я Маккавейская"
                        if (setSinodalBible.keyAt(i).contains("sinaidals48")) nazva = "2-я Маккавейская"
                        if (setSinodalBible.keyAt(i).contains("sinaidals49")) nazva = "3-я Маккавейская"
                        if (setSinodalBible.keyAt(i).contains("sinaidals50")) nazva = "3-я Ездры"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln1")) nazva = "От Матфея"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln2")) nazva = "От Марка"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln3")) nazva = "От Луки"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln4")) nazva = "От Иоанна"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln5")) nazva = "Деяния святых апостолов"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln6")) nazva = "Иакова"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln7")) nazva = "1-е Петра"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln8")) nazva = "2-е Петра"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln9")) nazva = "1-е Иоанна"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln10")) nazva = "2-е Иоанна"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln11")) nazva = "3-е Иоанна"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln12")) nazva = "Иуды"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln13")) nazva = "Римлянам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln14")) nazva = "1-е Коринфянам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln15")) nazva = "2-е Коринфянам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln16")) nazva = "Галатам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln17")) nazva = "Эфэсянам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln18")) nazva = "Филиппийцам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln19")) nazva = "Колоссянам"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln20")) nazva = "1-е Фессалоникийцам (Солунянам)"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln21")) nazva = "2-е Фессалоникийцам (Солунянам)"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln22")) nazva = "1-е Тимофею"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln23")) nazva = "2-е Тимофею"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln24")) nazva = "Титу"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln25")) nazva = "Филимону"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln26")) nazva = "Евреям"
                        if (setSinodalBible.keyAt(i).contains("sinaidaln27")) nazva = "Откровение (Апокалипсис)"
                        val inputStream = context.resources.openRawResource(setSinodalBible.valueAt(i))
                        val isr = InputStreamReader(inputStream)
                        val reader = BufferedReader(isr)
                        var glava = 0
                        val split = reader.readText().split("===")
                        inputStream.close()
                        for (e in 1 until split.size) {
                            glava++
                            val bibleline = split[e].split("\n")
                            var stix = 0
                            for (r in 1 until bibleline.size) {
                                stix++
                                var prepinanie = bibleline[r]
                                if (chin?.getInt("slovocalkam", 0) == 1) prepinanie = " " + bibleline[r] + " "
                                if (chin?.getInt("pegistr", 0) == 0) prepinanie = prepinanie.toLowerCase(Locale.getDefault())
                                val t1a = prepinanie.indexOf(poshuk1)
                                prepinanie = prepinanie.replace(",", "")
                                prepinanie = prepinanie.replace(".", "")
                                prepinanie = prepinanie.replace(";", "")
                                prepinanie = prepinanie.replace(":", "")
                                prepinanie = prepinanie.replace("[", "")
                                prepinanie = prepinanie.replace("]", "")
                                prepinanie = prepinanie.replace("-", "")
                                prepinanie = prepinanie.replace("\"", "")
                                var count = 0
                                if (t1a != -1) count = t1a - prepinanie.indexOf(poshuk1)
                                prepinanie = prepinanie.replace("ё", "е")
                                if (chin?.getInt("slovocalkam", 0) == 0) {
                                    if (prepinanie.contains(poshuk1)) {
                                        var aSviatyia = bibleline[r]
                                        var t1 = prepinanie.indexOf(poshuk1)
                                        t1 += count
                                        val t2 = poshuk1.length
                                        aSviatyia = aSviatyia.substring(0, t1) + color + aSviatyia.substring(t1, t1 + t2) + "</font>" + aSviatyia.substring(t1 + t2)
                                        seashpost.add("<!--stix.$stix::glava.$glava--><strong>$nazva Гл. $glava</strong><br>$aSviatyia")
                                    }
                                } else {
                                    if (prepinanie.contains(poshuk1)) {
                                        var aSviatyia = bibleline[r]
                                        var t1 = prepinanie.indexOf(poshuk1)
                                        t1 += count
                                        val t2 = poshuk1.length
                                        aSviatyia = aSviatyia.substring(0, t1) + color + aSviatyia.substring(t1, t1 + t2) + "</font>" + aSviatyia.substring(t1 + t2)
                                        seashpost.add("<!--stix.$stix::glava.$glava--><strong>$nazva Гл. $glava</strong><br>$aSviatyia")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return seashpost
        }

        private fun nadsan(context: Context, poshuk: String): ArrayList<String> {
            var poshuk1 = poshuk
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val seashpost = ArrayList<String>()
            if (poshuk1 != "") {
                poshuk1 = zamena(poshuk1)
                if (chin?.getInt("pegistr", 0) == 0) poshuk1 = poshuk1.toLowerCase(Locale.getDefault())
                if (chin?.getInt("slovocalkam", 0) == 0) {
                    val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
                    for (aM in m) {
                        val r = poshuk1.length - 1
                        if (poshuk1.length >= 3) {
                            if (poshuk1[r] == aM) {
                                poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r))
                            }
                        }
                    }
                } else {
                    poshuk1 = " $poshuk1 "
                }
                var color = "<font color=#d00505>"
                if (dzenNoch) color = "<font color=#f44336>"
                val nazva = "Псалтыр"
                val inputStream = context.resources.openRawResource(raw.nadsan_psaltyr)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                val split = reader.readText().split("===")
                inputStream.close()
                for ((glava, e) in (1 until split.size).withIndex()) {
                    val bibleline = split[e].split("\n")
                    var stix = 0
                    (1 until bibleline.size).forEach { r ->
                        stix++
                        var prepinanie = bibleline[r]
                        if (chin?.getInt("pegistr", 0) == 0) prepinanie = prepinanie.toLowerCase(Locale.getDefault())
                        val t1a = prepinanie.indexOf(poshuk1)
                        prepinanie = prepinanie.replace(",", "")
                        prepinanie = prepinanie.replace(".", "")
                        prepinanie = prepinanie.replace(";", "")
                        prepinanie = prepinanie.replace(":", "")
                        prepinanie = prepinanie.replace("-", "")
                        prepinanie = prepinanie.replace("\"", "")
                        var count = 0
                        if (t1a != -1) count = t1a - prepinanie.indexOf(poshuk1)
                        prepinanie = prepinanie.replace("ё", "е")
                        prepinanie = prepinanie.replace("<em>", "")
                        prepinanie = prepinanie.replace("</em>", " ")
                        prepinanie = prepinanie.replace("<br>", "")
                        prepinanie = prepinanie.replace("<strong>", "")
                        prepinanie = prepinanie.replace("</strong>", " ")
                        if (chin?.getInt("slovocalkam", 0) == 0) {
                            if (prepinanie.contains(poshuk1)) {
                                var aSviatyia = bibleline[r]
                                var t1 = prepinanie.indexOf(poshuk1)
                                t1 += count
                                val t2 = poshuk1.length
                                aSviatyia = aSviatyia.substring(0, t1) + color + aSviatyia.substring(t1, t1 + t2) + "</font>" + aSviatyia.substring(t1 + t2)
                                seashpost.add("<!--stix.$stix::glava.$glava--><strong>$nazva Пс. $glava</strong><br>$aSviatyia")
                            }
                        } else {
                            if (prepinanie.contains(poshuk1)) {
                                var aSviatyia = bibleline[r]
                                var t1 = prepinanie.indexOf(poshuk1)
                                t1 += count
                                val t2 = poshuk1.length
                                aSviatyia = aSviatyia.substring(0, t1) + color + aSviatyia.substring(t1, t1 + t2) + "</font>" + aSviatyia.substring(t1 + t2)
                                seashpost.add("<!--stix.$stix::glava.$glava--><strong>$nazva Пс. $glava</strong><br>$aSviatyia")
                            }
                        }
                    }
                }
            }
            return seashpost
        }
    }
}