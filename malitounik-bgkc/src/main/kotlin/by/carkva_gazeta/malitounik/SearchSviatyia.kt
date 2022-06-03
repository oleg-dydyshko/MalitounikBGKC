package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SearchSviatyiaBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.*
import java.util.*

class SearchSviatyia : BaseActivity(), DialogClearHishory.DialogClearHistoryListener {
    private lateinit var adapter: SearchListAdapter
    private var dzenNoch = false
    private var editText: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var searchViewQwery = ""
    private var arrayLists = ArrayList<ArrayList<String>>()
    private var arrayRes = ArrayList<Searche>()
    private lateinit var chin: SharedPreferences
    private var mLastClickTime: Long = 0
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var actionExpandOn = true
    private lateinit var binding: SearchSviatyiaBinding
    private var posukPesenJob: Job? = null
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
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
        textViewCount = menu.findItem(R.id.count).actionView as TextView
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
        val c = Calendar.getInstance() as GregorianCalendar
        dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDarkSlider)
        super.onCreate(savedInstanceState)
        binding = SearchSviatyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Slidr.attach(this)
        val searchSvityxString = chin.getString("search_svityx_string", "") ?: ""
        if (searchSvityxString != "") {
            if (searchSvityxString.length >= 3) {
                stopPosukSviatyx()
                startPosukSviatyx(searchSvityxString)
            } else {
                binding.History.visibility = View.VISIBLE
                binding.ListView.visibility = View.GONE
            }
        } else {
            binding.History.visibility = View.VISIBLE
            binding.ListView.visibility = View.GONE
        }
        historyAdapter = HistoryAdapter(this, history, true)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.History.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.History.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
        binding.History.adapter = historyAdapter
        binding.History.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            actionExpandOn = false
            val result = history[position]
            val t1 = result.indexOf("<!--")
            val t2 = result.indexOf(":")
            val t3 = result.indexOf("-->")
            val g = GregorianCalendar(c[Calendar.YEAR], result.substring(t2 + 1, t3).toInt(), result.substring(t1 + 4, t2).toInt())
            val intent = Intent()
            intent.putExtra("dayOfYear", g[Calendar.DAY_OF_YEAR])
            setResult(Activity.RESULT_OK, intent)
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
            actionExpandOn = false
            val g = Calendar.getInstance()
            g.set(Calendar.DAY_OF_YEAR, arrayRes[position].dayOfYear)
            val date = "<!--" + g[Calendar.DAY_OF_MONTH] + ":" + g[Calendar.MONTH] + "-->"
            val result = date + arrayRes[position].text.toString()
            val intent = Intent()
            intent.putExtra("dayOfYear", arrayRes[position].dayOfYear)
            setResult(Activity.RESULT_OK, intent)
            addHistory(result)
            saveHistopy()
            finish()
        }
        arrayLists = MenuCaliandar.getDataCalaindar(year = c[Calendar.YEAR])
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                }
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

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val histopy = menu.findItem(R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
        return true
    }

    private fun addHistory(item: String) {
        val st = item.replace("\n", "<br>")
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
            if (history.size == 10) break
        }
        if (history.size == 1) invalidateOptionsMenu()
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
        historyAdapter.notifyDataSetChanged()
        invalidateOptionsMenu()
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistopy()
        if (history.size == 0) invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    private fun stopPosukSviatyx() {
        posukPesenJob?.cancel()
    }

    private fun startPosukSviatyx(poshuk: String) {
        posukPesenJob = CoroutineScope(Dispatchers.Main).launch {
            rawAsset(poshuk)
        }
    }

    private fun rawAsset(poshukString: String) {
        val munName = resources.getStringArray(R.array.meciac_smoll)
        var poshuk = poshukString
        val posukOrig = poshuk
        arrayRes.clear()
        adapter.notifyDataSetChanged()
        if (poshuk != "") {
            poshuk = poshuk.replace("ё", "е", true)
            poshuk = poshuk.replace("сві", "сьві", true)
            poshuk = poshuk.replace("свя", "сьвя", true)
            poshuk = poshuk.replace("зме", "зьме", true)
            poshuk = poshuk.replace("змі", "зьмі", true)
            poshuk = poshuk.replace("змя", "зьмя", true)
            poshuk = poshuk.replace("зня", "зьня", true)
            poshuk = poshuk.replace("сле", "сьле", true)
            poshuk = poshuk.replace("слі", "сьлі", true)
            poshuk = poshuk.replace("сль", "сьль", true)
            poshuk = poshuk.replace("слю", "сьлю", true)
            poshuk = poshuk.replace("сля", "сьля", true)
            poshuk = poshuk.replace("сне", "сьне", true)
            poshuk = poshuk.replace("сні", "сьні", true)
            poshuk = poshuk.replace("сню", "сьню", true)
            poshuk = poshuk.replace("сня", "сьня", true)
            poshuk = poshuk.replace("спе", "сьпе", true)
            poshuk = poshuk.replace("спі", "сьпі", true)
            poshuk = poshuk.replace("спя", "сьпя", true)
            poshuk = poshuk.replace("сце", "сьце", true)
            poshuk = poshuk.replace("сці", "сьці", true)
            poshuk = poshuk.replace("сць", "сьць", true)
            poshuk = poshuk.replace("сцю", "сьцю", true)
            poshuk = poshuk.replace("сця", "сьця", true)
            poshuk = poshuk.replace("цце", "цьце", true)
            poshuk = poshuk.replace("цці", "цьці", true)
            poshuk = poshuk.replace("ццю", "цьцю", true)
            poshuk = poshuk.replace("ззе", "зьзе", true)
            poshuk = poshuk.replace("ззі", "зьзі", true)
            poshuk = poshuk.replace("ззю", "зьзю", true)
            poshuk = poshuk.replace("ззя", "зьзя", true)
            poshuk = poshuk.replace("зле", "зьле", true)
            poshuk = poshuk.replace("злі", "зьлі", true)
            poshuk = poshuk.replace("злю", "зьлю", true)
            poshuk = poshuk.replace("зля", "зьля", true)
            poshuk = poshuk.replace("збе", "зьбе", true)
            poshuk = poshuk.replace("збі", "зьбі", true)
            poshuk = poshuk.replace("збя", "зьбя", true)
            poshuk = poshuk.replace("нне", "ньне", true)
            poshuk = poshuk.replace("нні", "ньні", true)
            poshuk = poshuk.replace("нню", "ньню", true)
            poshuk = poshuk.replace("ння", "ньня", true)
            poshuk = poshuk.replace("лле", "льле", true)
            poshuk = poshuk.replace("ллі", "льлі", true)
            poshuk = poshuk.replace("ллю", "льлю", true)
            poshuk = poshuk.replace("лля", "льля", true)
            poshuk = poshuk.replace("дск", "дзк", true)
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk.length - 1
                if (r >= 3) {
                    if (poshuk[r] == aM) {
                        poshuk = poshuk.replace(poshuk, poshuk.substring(0, r), true)
                    }
                }
            }
            for (e in arrayLists.indices) {
                val sviatyia = arrayLists[e][4].split("<br>")
                for (aSviatyia in sviatyia) {
                    if (aSviatyia.replace("ё", "е", true).contains(poshuk, true)) {
                        var bSviatyia = aSviatyia
                        bSviatyia = bSviatyia.replace("<font color=#d00505>", "")
                        bSviatyia = bSviatyia.replace("</font>", "")
                        bSviatyia = bSviatyia.replace("<strong>", "")
                        bSviatyia = bSviatyia.replace("</strong>", "")
                        val t1 = bSviatyia.replace("ё", "е", true).indexOf(poshuk, ignoreCase = true)
                        val t2 = poshuk.length
                        val span = SpannableString(bSviatyia.substring(0, t1) + bSviatyia.substring(t1, t1 + t2) + bSviatyia.substring(t1 + t2))
                        span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        val g = GregorianCalendar(arrayLists[e][3].toInt(), arrayLists[e][2].toInt(), arrayLists[e][1].toInt())
                        val str1 = SpannableStringBuilder(arrayLists[e][1] + " " + munName[arrayLists[e][2].toInt()])
                        str1.setSpan(StyleSpan(Typeface.ITALIC), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        val result = Searche(g[Calendar.DAY_OF_YEAR], str1.append("\n").append(span))
                        arrayRes.add(result)
                    }
                }
            }
            val data = MenuSviaty.getPrazdnik(search = true)
            for (e in data.indices) {
                val sviatya = data[e].opisanie.replace("ё", "е", true)
                if (sviatya.contains(poshuk, true)) {
                    val resultSpan = SpannableStringBuilder()
                    var opisanieData = data[e].opisanieData
                    val opis1 = opisanieData.indexOf(",")
                    if (opis1 != -1) opisanieData = opisanieData.substring(0, opis1)
                    val str1 = SpannableString(opisanieData)
                    when (data[e].svaity) {
                        -1, -2, 2 -> {
                            str1.setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (dzenNoch) str1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_black)), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            else str1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        -3, 3 -> {
                            str1.setSpan(StyleSpan(Typeface.ITALIC), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (dzenNoch) str1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_black)), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            else str1.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        else -> {
                            str1.setSpan(StyleSpan(Typeface.ITALIC), 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                    val span = SpannableString(data[e].opisanie)
                    val t1 = sviatya.indexOf(poshuk, ignoreCase = true)
                    val t2 = poshuk.length
                    span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    resultSpan.append(str1).append("\n").append(span)
                    arrayRes.add(Searche(data[e].dayOfYear, resultSpan))
                }
            }
        }
        textViewCount?.text = resources.getString(R.string.seash, arrayRes.size)
        val prefEditors = chin.edit()
        prefEditors.putString("search_svityx_string", posukOrig)
        prefEditors.apply()
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val prefEditors = chin.edit()
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
                        val prefEditors = chin.edit()
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

    private class SearchListAdapter(mContext: Activity, private val adapterList: ArrayList<Searche>) : ArrayAdapter<Searche>(mContext, R.layout.simple_list_item_2, R.id.label, adapterList) {
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
            viewHolder.text.text = adapterList[position].text
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class Searche(val dayOfYear: Int, val text: Spannable)
}