package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.akafist_list_bible.*
import java.util.*
import kotlin.collections.ArrayList

class MalitvyPrynagodnyia : AppCompatActivity(), DialogClearHishory.DialogClearHistoryListener {

    private val data = ArrayList<MenuListData>()
    private lateinit var adapter: MenuListAdaprer
    private lateinit var searchView: SearchView
    private lateinit var chin: SharedPreferences
    private var mLastClickTime: Long = 0
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var searchViewQwery = ""
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var actionExpandOn = false

    private fun addHistory(item: String) {
        val temp = ArrayList<String>()
        for (i in 0 until history.size) {
            if (history[i] != item) {
                temp.add(history[i])
            }
        }
        history.clear()
        history.add(item)
        for (i in 0 until temp.size) {
            history.add(temp[i])
            if (history.size == 10)
                break
        }
    }

    private fun saveHistopy() {
        val gson = Gson()
        val json = gson.toJson(history)
        val prefEditors = chin.edit()
        prefEditors.putString("history_prynagodnyia", json)
        prefEditors.apply()
        invalidateOptionsMenu()
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistopy()
        invalidateOptionsMenu()
        actionExpandOn = false
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistopy()
        if (history.size == 0)
            invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    private fun getIdHistory(item: String): Int {
        var id = R.raw.prynagodnyia_0
        for (i in 0 until data.size) {
            if (data[i].data == item) {
                id = data[i].id
                break
            }
        }
        return id
    }

    private fun getTypeHistory(item: String): String {
        var type = "prynagodnyia_0"
        for (i in 0 until data.size) {
            if (data[i].data == item) {
                type = data[i].type
                break
            }
        }
        return type
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        chin = getSharedPreferences("biblia", MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.akafist_list_bible)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState != null) {
            searchViewQwery = savedInstanceState.getString("SearchViewQwery", "")
            actionExpandOn = savedInstanceState.getBoolean("actionExpandOn")
        }
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.prynagodnyia)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
        data.add(MenuListData(R.raw.prynagodnyia_0, "Малітва аб блаславеньні", "prynagodnyia_0"))
        data.add(MenuListData(R.raw.prynagodnyia_1, "Малітва аб дапамозе ў выбары жыцьцёвай дарогі дзіцяці", "prynagodnyia_1"))
        data.add(MenuListData(R.raw.prynagodnyia_2, "Малітва аб еднасьці", "prynagodnyia_2"))
        data.add(MenuListData(R.raw.prynagodnyia_3, "Малітва бацькоў за дзяцей 2", "prynagodnyia_3"))
        data.add(MenuListData(R.raw.prynagodnyia_4, "Малітва бацькоў за дзяцей", "prynagodnyia_4"))
        data.add(MenuListData(R.raw.prynagodnyia_5, "Малітва вадзіцеля", "prynagodnyia_5"))
        data.add(MenuListData(R.raw.prynagodnyia_6, "Малітва вучня", "prynagodnyia_6"))
        data.add(MenuListData(R.raw.prynagodnyia_7, "Малітва да Маці Божай Браслаўскай, Валадаркі Азёраў", "prynagodnyia_7"))
        data.add(MenuListData(R.raw.prynagodnyia_8, "Малітва да Маці Божай Будслаўскай, Апякункі Беларусі", "prynagodnyia_8"))
        data.add(MenuListData(R.raw.prynagodnyia_9, "Малітва да Маці Божай Нястомнай Дапамогі", "prynagodnyia_9"))
        data.add(MenuListData(R.raw.prynagodnyia_10, "Малітва за Беларусь", "prynagodnyia_10"))
        data.add(MenuListData(R.raw.prynagodnyia_11, "Малітва за дарослых дзяцей", "prynagodnyia_11"))
        data.add(MenuListData(R.raw.prynagodnyia_12, "Малітва за дзяцей перад пачаткам навукі", "prynagodnyia_12"))
        data.add(MenuListData(R.raw.prynagodnyia_13, "Малітва за парафію", "prynagodnyia_13"))
        data.add(MenuListData(R.raw.prynagodnyia_14, "Малітва за хворага", "prynagodnyia_14"))
        data.add(MenuListData(R.raw.prynagodnyia_15, "Малітва за хворае дзіця", "prynagodnyia_15"))
        data.add(MenuListData(R.raw.prynagodnyia_16, "Малітва за хрысьціянскую еднасьць", "prynagodnyia_16"))
        data.add(MenuListData(R.raw.prynagodnyia_17, "Малітва за ўмацаваньне ў любові", "prynagodnyia_17"))
        data.add(MenuListData(R.raw.prynagodnyia_18, "Малітва маладога чалавека", "prynagodnyia_18"))
        data.add(MenuListData(R.raw.prynagodnyia_19, "Малітва на ўсякую патрэбу", "prynagodnyia_19"))
        data.add(MenuListData(R.raw.prynagodnyia_20, "Малітва падзякі за атрыманыя дабрадзействы", "prynagodnyia_20"))
        data.add(MenuListData(R.raw.prynagodnyia_21, "Малітва перад пачаткам навучаньня", "prynagodnyia_21"))
        data.add(MenuListData(R.raw.prynagodnyia_22, "Малітва перад іспытамі", "prynagodnyia_22"))
        data.add(MenuListData(R.raw.prynagodnyia_23, "Малітва ранішняга намеру (Опціных старцаў)", "prynagodnyia_23"))
        data.add(MenuListData(R.raw.prynagodnyia_24, "Малітвы за сьвятароў і сьвятарскія пакліканьні", "prynagodnyia_24"))
        data.add(MenuListData(R.raw.prynagodnyia_25, "Малітвы ў часе хваробы і за хворых", "prynagodnyia_25"))
        data.add(MenuListData(R.raw.prynagodnyia_26, "Намер ісьці за Хрыстом", "prynagodnyia_26"))
        data.add(MenuListData(R.raw.prynagodnyia_27, "Цябе, Бога, хвалім", "prynagodnyia_27"))
        data.add(MenuListData(R.raw.prynagodnyia_28, "Малітва падчас згубнай пошасьці", "prynagodnyia_28"))
        data.add(MenuListData(R.raw.prynagodnyia_29, "Малітва вучняў перад навучаньнем", "prynagodnyia_29"))
        data.add(MenuListData(R.raw.prynagodnyia_30, "Малітва да Маці Божай Берасьцейскай", "prynagodnyia_30"))
        data.add(MenuListData(R.raw.prynagodnyia_31, "Малітва да Маці Божай Лагішынскай", "prynagodnyia_31"))
        data.add(MenuListData(R.raw.prynagodnyia_32, "Малітва пілігрыма", "prynagodnyia_32"))
        data.add(MenuListData(R.raw.prynagodnyia_33, "Малітва сям’і аб Божым бласлаўленьні на час адпачынку і вакацыяў", "prynagodnyia_33"))
        data.add(MenuListData(R.raw.prynagodnyia_34, "Малітва ў час адпачынку", "prynagodnyia_34"))
        data.add(MenuListData(R.raw.prynagodnyia_35, "Малітва за бязьвінных ахвяраў перасьледу", "prynagodnyia_35"))
        data.add(MenuListData(R.raw.prynagodnyia_36, "Малітва за Айчыну - Ян Павел II", "prynagodnyia_36"))
        data.sort()
        adapter = MenuListAdaprer(this)
        ListView.adapter = adapter
        if (dzenNoch) ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (firstVisibleItem == 1) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(ListView.windowToken, 0)
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }

        })
        ListView.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, Prynagodnyia::class.java)
            intent.putExtra("prynagodnyia", data[position].data)
            intent.putExtra("prynagodnyiaID", data[position].id)
            intent.putExtra("prynagodnyiaType", data[position].type)
            startActivity(intent)
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(ListView.windowToken, 0)
            if (autoCompleteTextView.text.toString() != "") {
                addHistory(data[position].data)
                saveHistopy()
            }
            actionExpandOn = false
        }
        if (chin.getString("history_prynagodnyia", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_prynagodnyia", "")
            val type = object : TypeToken<ArrayList<String>>() {}.type
            history.addAll(gson.fromJson(json, type))
        }
        historyAdapter = HistoryAdapter(this, history)
        if (dzenNoch) History.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else History.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        History.adapter = historyAdapter
        History.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this@MalitvyPrynagodnyia, Prynagodnyia::class.java)
            intent.putExtra("prynagodnyia", history[position])
            intent.putExtra("prynagodnyiaID", getIdHistory(history[position]))
            intent.putExtra("prynagodnyiaType", getTypeHistory(history[position]))
            startActivity(intent)
            val edit = history[position]
            addHistory(edit)
            saveHistopy()
            actionExpandOn = false
        }
        History.setOnItemLongClickListener { _, _, position, _ ->
            val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return@setOnItemLongClickListener true
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
            val p = view.layoutParams as LinearLayout.LayoutParams
            val density = resources.displayMetrics.density
            val margin = (10 * density).toInt()
            p.rightMargin = margin
            autoCompleteTextView.layoutParams = p
            autoCompleteTextView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                autoCompleteTextView.setTextCursorDrawable(by.carkva_gazeta.malitounik.R.color.colorWhite)
            } else {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(autoCompleteTextView, 0)
            }
            autoCompleteTextView.addTextChangedListener(MyTextWatcher())
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val histopy = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.malitvy_prynagodnyia, menu)
        val searchViewItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_seashe_text)
        searchView = searchViewItem.actionView as SearchView
        if (actionExpandOn) {
            searchViewItem.expandActionView()
            if (history.size > 0) {
                History.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            }
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                if (history.size > 0)
                    actionExpandOn = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                actionExpandOn = false
                return true
            }
        })
        searchView.queryHint = getString(by.carkva_gazeta.malitounik.R.string.search_malitv)
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            searchViewItem.expandActionView()
            autoCompleteTextView.setText(searchViewQwery)
        }
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SearchViewQwery", autoCompleteTextView.text.toString())
        outState.putBoolean("actionExpandOn", actionExpandOn)
    }

    private inner class MyTextWatcher : TextWatcher {
        var editPosition = 0
        var check = 0
        var editch = true

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
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                edit = edit.replace("И", "І")
                edit = edit.replace("Щ", "Ў")
                edit = edit.replace("Ъ", "'")
                if (check != 0) {
                    autoCompleteTextView.removeTextChangedListener(this)
                    autoCompleteTextView.setText(edit)
                    autoCompleteTextView.setSelection(editPosition)
                    autoCompleteTextView.addTextChangedListener(this)
                }
            }
            if (editPosition == 0 && actionExpandOn) {
                History.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            } else {
                History.visibility = View.GONE
                ListView.visibility = View.VISIBLE
            }
            adapter.filter.filter(edit)
        }
    }

    private inner class MenuListAdaprer(private val context: Activity) : ArrayAdapter<MenuListData?>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, data as List<MenuListData>) {
        private val origData: ArrayList<MenuListData> = ArrayList(data)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = context.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            viewHolder.text?.text = data[position].data
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch)
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    var constraint1 = constraint
                    constraint1 = constraint1.toString().toLowerCase(Locale.getDefault())
                    val result = FilterResults()
                    if (constraint1.toString().isNotEmpty()) {
                        val founded: ArrayList<MenuListData> = ArrayList()
                        for (item in origData) {
                            if (item.data.toLowerCase(Locale.getDefault()).contains(constraint1)) {
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

                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    clear()
                    for (item in results.values as ArrayList<*>) {
                        add(item as MenuListData)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}
