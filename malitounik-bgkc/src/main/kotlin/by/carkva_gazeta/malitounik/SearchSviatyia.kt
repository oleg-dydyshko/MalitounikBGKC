package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SearchSviatyiaBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class SearchSviatyia : AppCompatActivity(), DialogClearHishory.DialogClearHistoryListener {
    private lateinit var adapter: SearchListAdapter
    private var dzenNoch = false
    private var posukPesenTimer: Timer? = null
    private var posukPesenSchedule: TimerTask? = null
    private var editText: AutoCompleteTextView? = null
    private var textViewCount: TextViewRobotoCondensed? = null
    private var searchView: SearchView? = null
    private var searchViewQwery = ""
    private var arrayLists: ArrayList<ArrayList<String>> = ArrayList()
    private var arrayRes: ArrayList<String> = ArrayList()
    private lateinit var chin: SharedPreferences
    private lateinit var c: GregorianCalendar
    private var mLastClickTime: Long = 0
    private val munName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var actionExpandOn = true
    private lateinit var binding: SearchSviatyiaBinding

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (item.itemId == R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeSearchViewElements(view: View?) {
        if (view == null) return
        if (view.id == R.id.search_edit_frame || view.id == R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == R.id.search_src_text) {
            editText = view as AutoCompleteTextView
            editText?.setBackgroundResource(R.drawable.underline_white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                editText?.setTextCursorDrawable(R.color.colorWhite)
            } else {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(editText, 0)
            }
            val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
            editText?.setText(chin.getString("search_svityx_string", ""))
            editText?.setSelection(editText?.text?.length ?: 0)
            editText?.addTextChangedListener(MyTextWatcher())
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.pesny, menu)
        val searchViewItem = menu.findItem(R.id.search)
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                finish()
                actionExpandOn = false
                return true
            }
        })
        searchViewItem.expandActionView()
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.search_svityia)
        textViewCount = menu.findItem(R.id.count).actionView as TextViewRobotoCondensed
        val density = resources.displayMetrics.density.toInt()
        textViewCount?.setPadding(0, 0, 10 * density, 0)
        menu.findItem(R.id.count).isVisible = true
        textViewCount?.text = resources.getString(R.string.seash, arrayRes.size)
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            menu.findItem(R.id.search).expandActionView()
            searchView?.setQuery(searchViewQwery, true)
            searchView?.clearFocus()
        }
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        c = Calendar.getInstance() as GregorianCalendar
        dzenNoch = chin.getBoolean("dzen_noch", false)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        binding = SearchSviatyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val searchSvityxString = chin.getString("search_svityx_string", "") ?: ""
        if (searchSvityxString != "") {
            if (savedInstanceState == null) {
                val gson = Gson()
                val json = chin.getString("search_svityx_array", "")
                val type = object : TypeToken<ArrayList<String?>?>() {}.type
                arrayRes.addAll(gson.fromJson(json, type))
                for (i in arrayRes.indices) {
                    if (dzenNoch) arrayRes[i] = arrayRes[i].replace("#d00505", "#f44336") else arrayRes[i] = arrayRes[i].replace("#f44336", "#d00505")
                }
            }
        } else {
            binding.History.visibility = View.VISIBLE
            binding.ListView.visibility = View.GONE
        }
        historyAdapter = HistoryAdapter(this, history, true)
        if (dzenNoch) binding.History.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else binding.History.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        binding.History.adapter = historyAdapter
        binding.History.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val result = history[position]
            val t1 = result.indexOf("<!--")
            val t2 = result.indexOf(":")
            val t3 = result.indexOf("-->")
            val g = GregorianCalendar(c[Calendar.YEAR], result.substring(t2 + 1, t3).toInt(), result.substring(t1 + 4, t2).toInt())
            val intent = Intent()
            intent.putExtra("data", g[Calendar.DAY_OF_YEAR] - 1)
            setResult(140, intent)
            addHistory(result)
            saveHistopy()
            finish()
        }
        binding.History.setOnItemLongClickListener { _, _, position, _ ->
            val t1 = history[position].indexOf("</em><br>")
            val hishoryResult = history[position].substring(t1 + 9)
            val dialogClearHishory = DialogClearHishory.getInstance(position, MainActivity.fromHtml(hishoryResult).toString())
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return@setOnItemLongClickListener true
        }
        if (chin.getString("history_sviatyia", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_sviatyia", "")
            val type = object : TypeToken<ArrayList<String>>() {}.type
            history.addAll(gson.fromJson(json, type))
        }
        if (savedInstanceState != null) {
            if (searchSvityxString.length >= 3) {
                stopPosukSviatyx()
                startPosukSviatyx(searchSvityxString)
            } else {
                binding.History.visibility = View.VISIBLE
                binding.ListView.visibility = View.GONE
            }
        } else if (searchSvityxString.length < 3) {
            binding.History.visibility = View.VISIBLE
            binding.ListView.visibility = View.GONE
        }
        adapter = SearchListAdapter(this, arrayRes)
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        binding.ListView.adapter = adapter
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                if (i == 1) { // Скрываем клавиатуру
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
        binding.ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val result = arrayRes[position]
            val t1 = result.indexOf("<!--")
            val t2 = result.indexOf(":")
            val t3 = result.indexOf("-->")
            val g = GregorianCalendar(c[Calendar.YEAR], result.substring(t2 + 1, t3).toInt(), result.substring(t1 + 4, t2).toInt())
            val intent = Intent()
            intent.putExtra("data", g[Calendar.DAY_OF_YEAR] - 1)
            setResult(140, intent)
            addHistory(result)
            saveHistopy()
            finish()
        }
        file
        MenuCviaty.getPrazdnik(this, c[Calendar.YEAR])
        setTollbarTheme()
    }

    private val file: Unit
        get() {
            val sparseArray = SparseIntArray()
            sparseArray.append(0, R.raw.caliandar36)
            sparseArray.append(1, R.raw.caliandar37)
            sparseArray.append(2, R.raw.caliandar38)
            sparseArray.append(3, R.raw.caliandar39)
            sparseArray.append(4, R.raw.caliandar40)
            sparseArray.append(5, R.raw.caliandar41)
            sparseArray.append(6, R.raw.caliandar42)
            sparseArray.append(7, R.raw.caliandar43)
            sparseArray.append(8, R.raw.caliandar44)
            sparseArray.append(9, R.raw.caliandar45)
            sparseArray.append(10, R.raw.caliandar46)
            sparseArray.append(11, R.raw.caliandar47)
            val builder = StringBuilder()
            for (i in 0 until sparseArray.size()) {
                val inputStream = resources.openRawResource(sparseArray[i])
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                reader.forEachLine {
                    line = it
                    if (sparseArray.keyAt(i) > 0) {
                        val t1 = line.indexOf("[")
                        line = line.substring(t1 + 1)
                    }
                    if (sparseArray.keyAt(i) < 11) {
                        val t1 = line.lastIndexOf("]")
                        line = line.substring(0, t1) + ","
                    }
                    builder.append(line).append("\n")
                }
                inputStream.close()
            }
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
            arrayLists = gson.fromJson(builder.toString(), type)
        }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            binding.titleToolbar.setHorizontallyScrolling(true)
            binding.titleToolbar.freezesText = true
            binding.titleToolbar.marqueeRepeatLimit = -1
            if (binding.titleToolbar.isSelected) {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                binding.titleToolbar.isSelected = false
            } else {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.titleToolbar.isSelected = true
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getText(R.string.search_svityia)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val histopy = menu.findItem(R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
        return true
    }

    private fun addHistory(item: String) {
        var st = item.replace("<font color=#d00505>", "")
        st = st.replace("</font>", "")
        val temp = ArrayList<String>()
        for (i in 0 until history.size) {
            if (history[i] != st) {
                temp.add(history[i])
            }
        }
        history.clear()
        history.add(st)
        for (i in 0 until temp.size) {
            history.add(temp[i])
            if (history.size == 10)
                break
        }
        if (history.size == 1)
            invalidateOptionsMenu()
    }

    private fun saveHistopy() {
        val gson = Gson()
        val json = gson.toJson(history)
        val prefEditors = chin.edit()
        prefEditors.putString("history_sviatyia", json)
        prefEditors.apply()
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistopy()
        invalidateOptionsMenu()
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistopy()
        if (history.size == 0)
            invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    private fun stopPosukSviatyx() {
        if (posukPesenTimer != null) {
            posukPesenTimer?.cancel()
            posukPesenTimer = null
        }
        posukPesenSchedule = null
    }

    private fun startPosukSviatyx(poshuk: String) {
        if (posukPesenTimer == null) {
            posukPesenTimer = Timer()
            if (posukPesenSchedule != null) {
                posukPesenSchedule?.cancel()
                posukPesenSchedule = null
            }
            posukPesenSchedule = object : TimerTask() {
                override fun run() {
                    CoroutineScope(Dispatchers.Main).launch { rawAsset(poshuk) }
                }
            }
            posukPesenTimer?.schedule(posukPesenSchedule, 0)
        }
    }

    private fun rawAsset(poshukString: String) {
        var poshuk = poshukString
        val posukOrig = poshuk
        arrayRes.clear()
        adapter.notifyDataSetChanged()
        if (poshuk != "") {
            poshuk = poshuk.toLowerCase(Locale.getDefault())
            poshuk = poshuk.replace("ё", "е")
            poshuk = poshuk.replace("сві", "сьві")
            poshuk = poshuk.replace("свя", "сьвя")
            poshuk = poshuk.replace("зме", "зьме")
            poshuk = poshuk.replace("змі", "зьмі")
            poshuk = poshuk.replace("змя", "зьмя")
            poshuk = poshuk.replace("зня", "зьня")
            poshuk = poshuk.replace("сле", "сьле")
            poshuk = poshuk.replace("слі", "сьлі")
            poshuk = poshuk.replace("сль", "сьль")
            poshuk = poshuk.replace("слю", "сьлю")
            poshuk = poshuk.replace("сля", "сьля")
            poshuk = poshuk.replace("сне", "сьне")
            poshuk = poshuk.replace("сні", "сьні")
            poshuk = poshuk.replace("сню", "сьню")
            poshuk = poshuk.replace("сня", "сьня")
            poshuk = poshuk.replace("спе", "сьпе")
            poshuk = poshuk.replace("спі", "сьпі")
            poshuk = poshuk.replace("спя", "сьпя")
            poshuk = poshuk.replace("сце", "сьце")
            poshuk = poshuk.replace("сці", "сьці")
            poshuk = poshuk.replace("сць", "сьць")
            poshuk = poshuk.replace("сцю", "сьцю")
            poshuk = poshuk.replace("сця", "сьця")
            poshuk = poshuk.replace("цце", "цьце")
            poshuk = poshuk.replace("цці", "цьці")
            poshuk = poshuk.replace("ццю", "цьцю")
            poshuk = poshuk.replace("ззе", "зьзе")
            poshuk = poshuk.replace("ззі", "зьзі")
            poshuk = poshuk.replace("ззю", "зьзю")
            poshuk = poshuk.replace("ззя", "зьзя")
            poshuk = poshuk.replace("зле", "зьле")
            poshuk = poshuk.replace("злі", "зьлі")
            poshuk = poshuk.replace("злю", "зьлю")
            poshuk = poshuk.replace("зля", "зьля")
            poshuk = poshuk.replace("збе", "зьбе")
            poshuk = poshuk.replace("збі", "зьбі")
            poshuk = poshuk.replace("збя", "зьбя")
            poshuk = poshuk.replace("нне", "ньне")
            poshuk = poshuk.replace("нні", "ньні")
            poshuk = poshuk.replace("нню", "ньню")
            poshuk = poshuk.replace("ння", "ньня")
            poshuk = poshuk.replace("лле", "льле")
            poshuk = poshuk.replace("ллі", "льлі")
            poshuk = poshuk.replace("ллю", "льлю")
            poshuk = poshuk.replace("лля", "льля")
            poshuk = poshuk.replace("дск", "дзк")
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk.length - 1
                if (r >= 3) {
                    if (poshuk[r] == aM) {
                        poshuk = poshuk.replace(poshuk, poshuk.substring(0, r))
                    }
                }
            }
            var color = "<font color=#d00505>"
            if (dzenNoch) color = "<font color=#f44336>"
            for (e in arrayLists.indices) {
                val sviatyia = arrayLists[e][4].split("<br>")
                for (aSviatyia in sviatyia) {
                    if (aSviatyia.toLowerCase(Locale.getDefault()).replace("ё", "е").contains(poshuk.toLowerCase(Locale.getDefault()))) {
                        var bSviatyia = aSviatyia
                        bSviatyia = bSviatyia.replace("<font color=#d00505>", "")
                        bSviatyia = bSviatyia.replace("</font>", "")
                        bSviatyia = bSviatyia.replace("<strong>", "")
                        bSviatyia = bSviatyia.replace("</strong>", "")
                        val t1 = bSviatyia.toLowerCase(Locale.getDefault()).replace("ё", "е").indexOf(poshuk.toLowerCase(Locale.getDefault()))
                        val t2 = poshuk.toLowerCase(Locale.getDefault()).length
                        bSviatyia = bSviatyia.substring(0, t1) + color + bSviatyia.substring(t1, t1 + t2) + "</font>" + bSviatyia.substring(t1 + t2)
                        val g = GregorianCalendar(arrayLists[e][3].toInt(), arrayLists[e][2].toInt(), arrayLists[e][1].toInt())
                        val res = "<!--" + g[Calendar.DAY_OF_MONTH] + ":" + g[Calendar.MONTH] + "--><em>" + arrayLists[e][1] + " " + munName[arrayLists[e][2].toInt()] + "</em><br>" + bSviatyia
                        arrayRes.add(res)
                    }
                }
            }
            for (e in MenuCviaty.opisanie.indices) {
                if (MenuCviaty.opisanie[e].toLowerCase(Locale.getDefault()).replace("ё", "е").contains(poshuk.toLowerCase(Locale.getDefault()))) {
                    var result = MenuCviaty.opisanie[e]
                    if (result.contains("<font color=")) {
                        val t1 = result.indexOf("<font")
                        val t2 = result.indexOf("\">")
                        val t3 = result.indexOf("</font>")
                        result = result.substring(0, t1) + result.substring(t2 + 2, t3)
                    }
                    val t1 = result.indexOf("<!--")
                    val t2 = result.indexOf(":")
                    val t3 = result.indexOf("-->")
                    var bold = ""
                    var boldEnd = ""
                    var em = ""
                    var emEnd = ""
                    val t6 = result.indexOf("<!--", t3)
                    if (t6 != -1) {
                        val t7 = result.indexOf("-->", t6)
                        val res1 = result.substring(t6 + 4, t7)
                        if (res1.contains("1")) {
                            bold = "$color<strong>"
                            boldEnd = "</strong></font>"
                        }
                        if (res1.contains("2")) {
                            bold = color
                            boldEnd = "</font>"
                        }
                        if (res1.contains("3")) {
                            em = "<em>"
                            emEnd = "</em>"
                        }
                    }
                    val g = GregorianCalendar(c[Calendar.YEAR], result.substring(t2 + 1, t3).toInt(), result.substring(t1 + 4, t2).toInt())
                    var aSviatyia = result.substring(t3 + 3)
                    val t4 = aSviatyia.toLowerCase(Locale.getDefault()).replace("ё", "е").indexOf(poshuk.toLowerCase(Locale.getDefault()))
                    val t5 = poshuk.toLowerCase(Locale.getDefault()).length
                    aSviatyia = aSviatyia.substring(0, t4) + color + aSviatyia.substring(t4, t4 + t5) + "</font>" + aSviatyia.substring(t4 + t5)
                    val res = result.substring(t1, t3 + 3) + "<em>" + bold + g[Calendar.DATE] + " " + munName[g[Calendar.MONTH]] + "</em>" + boldEnd + "<br>" + em + aSviatyia + emEnd
                    arrayRes.add(res)
                }
            }
        }
        textViewCount?.text = resources.getString(R.string.seash, arrayRes.size)
        val gson = Gson()
        val json = gson.toJson(arrayRes)
        val prefEditors = chin.edit()
        prefEditors.putString("search_svityx_array", json)
        prefEditors.putString("search_svityx_string", posukOrig)
        prefEditors.apply()
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gson = Gson()
        val json = gson.toJson(arrayRes)
        val prefEditors = chin.edit()
        prefEditors.putString("search_svityx_array", json)
        prefEditors.putString("search_svityx_string", editText?.text.toString())
        prefEditors.apply()
    }

    private inner class MyTextWatcher : TextWatcher {
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
            if (editch) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                if (edit.length >= 3) {
                    stopPosukSviatyx()
                    startPosukSviatyx(edit)
                    binding.History.visibility = View.GONE
                    binding.ListView.visibility = View.VISIBLE
                } else {
                    if (actionExpandOn) {
                        arrayRes.clear()
                        adapter.notifyDataSetChanged()
                        textViewCount?.text = resources.getString(R.string.seash, 0)
                        val gson = Gson()
                        val json = gson.toJson(arrayRes)
                        val prefEditors = chin.edit()
                        prefEditors.putString("search_svityx_array", json)
                        prefEditors.putString("search_svityx_string", edit)
                        prefEditors.apply()
                        binding.History.visibility = View.VISIBLE
                        binding.ListView.visibility = View.GONE
                    }
                }
                if (check != 0) {
                    editText?.removeTextChangedListener(this)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
            }
        }
    }

    private class SearchListAdapter(mContext: Activity, private val adapterList: ArrayList<String>) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_2, R.id.label, adapterList) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)

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
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text.text = MainActivity.fromHtml(adapterList[position])
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch)
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextViewRobotoCondensed)
}