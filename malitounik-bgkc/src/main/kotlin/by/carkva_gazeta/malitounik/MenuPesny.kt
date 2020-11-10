package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.menu_pesny.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 30.5.16
 */
class MenuPesny : MenuPesnyHistory(), AdapterView.OnItemClickListener {
    private var mLastClickTime: Long = 0
    private var posukPesenTimer: Timer? = null
    private var posukPesenSchedule: TimerTask? = null
    private var editText: AutoCompleteTextView? = null
    private var textViewCount: TextViewRobotoCondensed? = null
    private var searchView: SearchView? = null
    private var searchViewQwery = ""
    private var search = false
    private var pesny = "prasl"
    private lateinit var adapter: MenuPesnyListAdapter
    private lateinit var menuList: ArrayList<MenuListData>
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var chin: SharedPreferences
    private val textWatcher: TextWatcher = MyTextWatcher()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_pesny, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { fraragment ->
            chin = fraragment.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (!menuListDataIsInitialized()) menuListData = getMenuListData(fraragment)
            pesny = arguments?.getString("pesny") ?: "prasl"
            menuList = getMenuListData(fraragment, pesny)
            adapter = MenuPesnyListAdapter(fraragment)
            ListView.adapter = adapter
            ListView.isVerticalScrollBarEnabled = false
            ListView.onItemClickListener = this
            ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                    if (i == 1) { // Скрываем клавиатуру
                        val imm1 = fraragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                    }
                }

                override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
            })
            if (chin.getString("history_pesny", "") != "") {
                val gson = Gson()
                val json = chin.getString("history_pesny", "")
                val type = object : TypeToken<ArrayList<String>>() {}.type
                history.addAll(gson.fromJson(json, type))
            }
            if (savedInstanceState != null) {
                search = savedInstanceState.getBoolean("search", false)
                searchViewQwery = savedInstanceState.getString("SearchViewQwery") ?: ""
                when {
                    searchViewQwery.length >= 3 -> {
                        stopPosukPesen()
                        startPosukPesen(searchViewQwery)
                    }
                    searchViewQwery.length in 1..2 -> {
                        History.visibility = View.VISIBLE
                        ListView.visibility = View.GONE
                    }
                    else -> {
                        History.visibility = View.GONE
                        ListView.visibility = View.VISIBLE
                    }
                }
            }
            historyAdapter = HistoryAdapter(fraragment, history, true)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            if (dzenNoch) History.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_dark)
            else History.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_default)
            History.adapter = historyAdapter
            History.onItemClickListener = this
            History.setOnItemLongClickListener { _, _, position, _ ->
                fragmentManager?.let {
                    val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
                    dialogClearHishory.show(it, "dialogClearHishory")
                }
                return@setOnItemLongClickListener true
            }
        }
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
            if (history.size == 10) break
        }
        historyAdapter.notifyDataSetChanged()
        if (history.size == 1) activity?.invalidateOptionsMenu()
    }

    private fun saveHistopy() {
        val gson = Gson()
        val json = gson.toJson(history)
        val prefEditors = chin.edit()
        prefEditors.putString("history_pesny", json)
        prefEditors.apply()
        //activity?.invalidateOptionsMenu()
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistopy()
        activity?.invalidateOptionsMenu()
        //loadHistory()
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistopy()
        if (history.size == 0) activity?.invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
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
            editText?.addTextChangedListener(textWatcher)
            editText?.setBackgroundResource(R.drawable.underline_white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                editText?.setTextCursorDrawable(R.color.colorIcons)
            } else {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(editText, 0)
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clean_histopy) {
            fragmentManager?.let {
                val dialogClearHishory = DialogClearHishory.getInstance()
                dialogClearHishory.show(it, "dialogClearHishory")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SearchViewQwery", searchView?.query.toString())
        outState.putBoolean("search", search)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.count).isVisible = search
        val histopy = menu.findItem(R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pesny, menu)
        val searchViewItem = menu.findItem(R.id.search)
        textViewCount = menu.findItem(R.id.count).actionView as TextViewRobotoCondensed
        if (search) {
            searchViewItem.expandActionView()
            menuList.clear()
            menuList.addAll(menuListData)
            menuList.sort()
            textViewCount?.text = getString(R.string.seash, menuList.size)
            adapter.notifyDataSetChanged()
            menu.findItem(R.id.count).isVisible = search
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                search = true
                menuList.clear()
                menuList.addAll(menuListData)
                menuList.sort()
                textViewCount?.text = getString(R.string.seash, menuList.size)
                adapter.notifyDataSetChanged()
                menu.findItem(R.id.count).isVisible = search
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchView?.setOnQueryTextListener(null)
                search = false
                activity?.let {
                    menuList.clear()
                    menuList.addAll(getMenuListData(it, pesny))
                    adapter.notifyDataSetChanged()
                    menu.findItem(R.id.count).isVisible = search
                }
                History.visibility = View.GONE
                ListView.visibility = View.VISIBLE
                return true
            }
        })
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.search)
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
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        editText?.removeTextChangedListener(textWatcher)
        val intent = Intent(activity, PesnyAll::class.java)
        if (parent?.id == R.id.ListView) {
            intent.putExtra("pesny", menuList[position].data)
            if (search) {
                addHistory(menuList[position].data)
                saveHistopy()
            }
        } else {
            intent.putExtra("pesny", history[position])
            addHistory(history[position])
            saveHistopy()
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        editText?.addTextChangedListener(textWatcher)
    }

    private fun stopPosukPesen() {
        if (posukPesenTimer != null) {
            posukPesenTimer?.cancel()
            posukPesenTimer = null
        }
        posukPesenSchedule = null
    }

    private fun startPosukPesen(poshuk: String) {
        if (posukPesenTimer == null) {
            posukPesenTimer = Timer()
            if (posukPesenSchedule != null) {
                posukPesenSchedule?.cancel()
                posukPesenSchedule = null
            }
            posukPesenSchedule = object : TimerTask() {
                override fun run() {
                    CoroutineScope(Dispatchers.Main).launch {
                        rawAsset(poshuk)
                    }
                }
            }
            posukPesenTimer?.schedule(posukPesenSchedule, 0)
        }
    }

    private fun rawAsset(poshuk: String) {
        var poshuk1 = poshuk
        var setClear = true
        if (poshuk1 != "") {
            poshuk1 = poshuk1.toLowerCase(Locale.getDefault())
            poshuk1 = poshuk1.replace("ё", "е")
            poshuk1 = poshuk1.replace("све", "сьве")
            poshuk1 = poshuk1.replace("сві", "сьві")
            poshuk1 = poshuk1.replace("свя", "сьвя")
            poshuk1 = poshuk1.replace("зве", "зьве")
            poshuk1 = poshuk1.replace("зві", "зьві")
            poshuk1 = poshuk1.replace("звя", "зьвя")
            poshuk1 = poshuk1.replace("зме", "зьме")
            poshuk1 = poshuk1.replace("змі", "зьмі")
            poshuk1 = poshuk1.replace("змя", "зьмя")
            poshuk1 = poshuk1.replace("зня", "зьня")
            poshuk1 = poshuk1.replace("сле", "сьле")
            poshuk1 = poshuk1.replace("слі", "сьлі")
            poshuk1 = poshuk1.replace("сль", "сьль")
            poshuk1 = poshuk1.replace("слю", "сьлю")
            poshuk1 = poshuk1.replace("сля", "сьля")
            poshuk1 = poshuk1.replace("сне", "сьне")
            poshuk1 = poshuk1.replace("сні", "сьні")
            poshuk1 = poshuk1.replace("сню", "сьню")
            poshuk1 = poshuk1.replace("сня", "сьня")
            poshuk1 = poshuk1.replace("спе", "сьпе")
            poshuk1 = poshuk1.replace("спі", "сьпі")
            poshuk1 = poshuk1.replace("спя", "сьпя")
            poshuk1 = poshuk1.replace("сце", "сьце")
            poshuk1 = poshuk1.replace("сці", "сьці")
            poshuk1 = poshuk1.replace("сць", "сьць")
            poshuk1 = poshuk1.replace("сцю", "сьцю")
            poshuk1 = poshuk1.replace("сця", "сьця")
            poshuk1 = poshuk1.replace("цце", "цьце")
            poshuk1 = poshuk1.replace("цці", "цьці")
            poshuk1 = poshuk1.replace("ццю", "цьцю")
            poshuk1 = poshuk1.replace("ззе", "зьзе")
            poshuk1 = poshuk1.replace("ззі", "зьзі")
            poshuk1 = poshuk1.replace("ззю", "зьзю")
            poshuk1 = poshuk1.replace("ззя", "зьзя")
            poshuk1 = poshuk1.replace("зле", "зьле")
            poshuk1 = poshuk1.replace("злі", "зьлі")
            poshuk1 = poshuk1.replace("злю", "зьлю")
            poshuk1 = poshuk1.replace("зля", "зьля")
            poshuk1 = poshuk1.replace("збе", "зьбе")
            poshuk1 = poshuk1.replace("збі", "зьбі")
            poshuk1 = poshuk1.replace("збя", "зьбя")
            poshuk1 = poshuk1.replace("нне", "ньне")
            poshuk1 = poshuk1.replace("нні", "ньні")
            poshuk1 = poshuk1.replace("нню", "ньню")
            poshuk1 = poshuk1.replace("ння", "ньня")
            poshuk1 = poshuk1.replace("лле", "льле")
            poshuk1 = poshuk1.replace("ллі", "льлі")
            poshuk1 = poshuk1.replace("ллю", "льлю")
            poshuk1 = poshuk1.replace("лля", "льля")
            poshuk1 = poshuk1.replace("дск", "дзк")
            poshuk1 = poshuk1.replace("дств", "дзтв")
            poshuk1 = poshuk1.replace("з’е", "зье")
            poshuk1 = poshuk1.replace("з’я", "зья")
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (r >= 3) {
                    if (poshuk1[r] == aM) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r))
                    }
                }
            }
            for (i in menuListData.indices) {
                val inputStream = resources.openRawResource(menuListData[i].id)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                val builder = StringBuilder()
                reader.forEachLine {
                    line = it.replace(",", "")
                    line = line.replace(" — ", " ")
                    line = line.replace("(", "")
                    line = line.replace(")", "")
                    line = line.replace(".", "")
                    line = line.replace("!", "")
                    builder.append(line).append("\n")
                }
                inputStream.close()
                if (builder.toString().toLowerCase(Locale.getDefault()).replace("ё", "е").contains(poshuk1.toLowerCase(Locale.getDefault()))) {
                    if (setClear) {
                        menuList.clear()
                        setClear = false
                    }
                    menuList.add(menuListData[i])
                }
            }
            if (setClear) {
                menuList.clear()
            }
            adapter.notifyDataSetChanged()
        }
        textViewCount?.text = resources.getString(R.string.seash, menuList.size)
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
            if (editch && search) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("И", "І")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                when {
                    edit.length >= 3 -> {
                        stopPosukPesen()
                        startPosukPesen(edit)
                        History.visibility = View.GONE
                        ListView.visibility = View.VISIBLE
                    }
                    edit.length in 1..2 -> {
                        History.visibility = View.VISIBLE
                        ListView.visibility = View.GONE
                        textViewCount?.text = "(0)"
                    }
                    else -> {
                        menuList.clear()
                        menuList.addAll(menuListData)
                        menuList.sort()
                        adapter.notifyDataSetChanged()
                        textViewCount?.text = getString(R.string.seash, menuList.size)
                        History.visibility = View.GONE
                        ListView.visibility = View.VISIBLE
                    }
                }
                if (check != 0) {
                    editText?.removeTextChangedListener(null)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
            }
        }
    }

    private inner class MenuPesnyListAdapter(private val activity: Activity) : ArrayAdapter<MenuListData>(activity, R.layout.simple_list_item_2, R.id.label, menuList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            viewHolder.text?.text = menuList[position].data
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                //viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                //viewHolder.text?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            } /*else {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_white)
            }*/
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        private lateinit var menuListData: ArrayList<MenuListData>

        fun getInstance(pesny: String): MenuPesny {
            val bundle = Bundle()
            bundle.putString("pesny", pesny)
            val menuPesny = MenuPesny()
            menuPesny.arguments = bundle
            return menuPesny
        }

        private fun menuListDataIsInitialized() = ::menuListData.isInitialized

        fun getPesniaID(context: Context, name: String): Int {
            if (!::menuListData.isInitialized) menuListData = getMenuListData(context)
            for (list_data in menuListData) {
                if (list_data.data == name) return list_data.id
            }
            return -1
        }

        private fun listRaw(filename: String): Int {
            val fields: Array<Field?> = R.raw::class.java.fields
            var id = 0
            run files@{
                fields.forEach {
                    if (it?.name == filename) {
                        id = it.getInt(it)
                        return@files
                    }
                }
            }
            return id
        }

        private fun getMenuListData(context: Context): ArrayList<MenuListData> {
            val menuListData = ArrayList<MenuListData>()
            val inputStream = context.resources.openRawResource(R.raw.pesny_menu)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.forEachLine {
                line = it
                val split = line.split("<>")
                val t1 = split[0].indexOf("_")
                val t2 = split[0].indexOf("_", t1 + 1)
                menuListData.add(MenuListData(listRaw(split[0]), split[1], split[0].substring(t1 + 1, t2)))
            }
            return menuListData
        }

        private fun getMenuListData(context: Context, pesny: String): ArrayList<MenuListData> {
            if (!::menuListData.isInitialized) menuListData = getMenuListData(context)

            val menuList = menuListData.filter {
                it.type.contains(pesny)
            }
            return menuList as ArrayList<MenuListData>
        }
    }
}