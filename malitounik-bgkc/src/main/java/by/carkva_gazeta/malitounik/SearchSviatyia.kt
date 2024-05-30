package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
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
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.SearchSviatyiaBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar

class SearchSviatyia : BaseActivity(), DialogClearHishory.DialogClearHistoryListener {
    private lateinit var adapter: SearchListAdapter
    private val dzenNoch get() = getBaseDzenNoch()
    private var editText: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var searchViewQwery = ""
    private var arrayLists = ArrayList<ArrayList<String>>()
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBack()
            return true
        }
        if (item.itemId == R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return true
        }
        return false
    }

    private fun changeSearchViewElements(view: View?) {
        if (view == null) return
        if (view.id == androidx.appcompat.R.id.search_edit_frame || view.id == androidx.appcompat.R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == androidx.appcompat.R.id.search_src_text) {
            editText = view as AutoCompleteTextView
            editText?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                }
                true
            }
            editText?.imeOptions = EditorInfo.IME_ACTION_DONE
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pesny, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(R.id.search)
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                onBack()
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
        textViewCount?.text = resources.getString(R.string.seash, adapter.count)
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            menu.findItem(R.id.search).expandActionView()
            searchView?.setQuery(searchViewQwery, true)
            searchView?.clearFocus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance()
        binding = SearchSviatyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            var putDayOfYear = g[Calendar.DAY_OF_YEAR]
            if (!g.isLeapYear(g[Calendar.YEAR]) && g[Calendar.DAY_OF_YEAR] > 59) putDayOfYear = g[Calendar.DAY_OF_YEAR] + 1
            intent.putExtra("dayOfYear", putDayOfYear)
            setResult(Activity.RESULT_OK, intent)
            addHistory(result)
            saveHistopy()
            onBack()
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
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
            history.addAll(gson.fromJson(json, type))
        }
        adapter = SearchListAdapter(this, ArrayList())
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        binding.ListView.adapter = adapter
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                if (i == 1) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                    searchView?.clearFocus()
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
            val adapterRes = adapter.getItem(position) as Searche
            val g = GregorianCalendar()
            g.set(Calendar.DAY_OF_YEAR, adapterRes.dayOfYear)
            val date = "<!--" + g[Calendar.DAY_OF_MONTH] + ":" + g[Calendar.MONTH] + "-->"
            val result = date + adapterRes.text.toString()
            val intent = Intent()
            var putDayOfYear = adapterRes.dayOfYear
            if (!g.isLeapYear(g[Calendar.YEAR]) && adapterRes.dayOfYear > 59) putDayOfYear = adapterRes.dayOfYear + 1
            intent.putExtra("dayOfYear", putDayOfYear)
            setResult(Activity.RESULT_OK, intent)
            addHistory(result)
            saveHistopy()
            onBack()
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
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

    override fun onPrepareMenu(menu: Menu) {
        val histopy = menu.findItem(R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
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
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(history, type)
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
            if (adapter.count == 0) {
                rawAsset(poshuk, true)
            }
        }
    }

    private fun rawAsset(poshukString: String, secondRun: Boolean = false) {
        val munName = resources.getStringArray(R.array.meciac_smoll)
        var poshuk = poshukString
        val posukOrig = poshuk
        adapter.clear()
        poshuk = MainActivity.zamena(poshuk)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk.length - 1
                if (r >= 3) {
                    if (poshuk[r] == aM) {
                        poshuk = poshuk.replace(poshuk, poshuk.substring(0, r), true)
                    }
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
                    adapter.add(result)
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
                adapter.add(Searche(data[e].dayOfYear, resultSpan))
            }
        }
        textViewCount?.text = resources.getString(R.string.seash, adapter.count)
        val prefEditors = chin.edit()
        prefEditors.putString("search_svityx_string", posukOrig)
        prefEditors.apply()
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
                        adapter.clear()
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

    private class SearchListAdapter(private val mContext: Activity, private val adapterList: ArrayList<Searche>) : ArrayAdapter<Searche>(mContext, R.layout.simple_list_item_2, R.id.label, adapterList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(mContext.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            viewHolder.text.text = adapterList[position].text
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class Searche(val dayOfYear: Int, val text: Spannable)
}