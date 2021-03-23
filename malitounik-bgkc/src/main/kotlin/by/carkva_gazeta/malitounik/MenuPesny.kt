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
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.MenuPesnyBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class MenuPesny : MenuPesnyHistory(), AdapterView.OnItemClickListener {
    private var mLastClickTime: Long = 0
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
    private var _binding: MenuPesnyBinding? = null
    private val binding get() = _binding!!
    private var posukPesenJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuPesnyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { fraragment ->
            chin = fraragment.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            pesny = arguments?.getString("pesny") ?: "prasl"
            menuList = getMenuListData(pesny)
            adapter = MenuPesnyListAdapter(fraragment)
            binding.ListView.adapter = adapter
            binding.ListView.isVerticalScrollBarEnabled = false
            binding.ListView.onItemClickListener = this
            binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                    if (i == 1) {
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
                        binding.History.visibility = View.VISIBLE
                        binding.ListView.visibility = View.GONE
                    }
                    else -> {
                        binding.History.visibility = View.GONE
                        binding.ListView.visibility = View.VISIBLE
                    }
                }
            }
            historyAdapter = HistoryAdapter(fraragment, history, true)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                binding.History.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_dark)
                binding.ListView.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_dark)
            } else {
                binding.History.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_default)
                binding.ListView.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_default)
            }
            binding.History.adapter = historyAdapter
            binding.History.onItemClickListener = this
            binding.History.setOnItemLongClickListener { _, _, position, _ ->
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
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistopy()
        activity?.invalidateOptionsMenu()
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
                editText?.setTextCursorDrawable(R.color.colorWhite)
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
            menuList.addAll(getMenuListData())
            menuList.sort()
            textViewCount?.text = getString(R.string.seash, menuList.size)
            adapter.notifyDataSetChanged()
            menu.findItem(R.id.count).isVisible = search
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                search = true
                menuList.clear()
                menuList.addAll(getMenuListData())
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
                    menuList.addAll(getMenuListData(pesny))
                    adapter.notifyDataSetChanged()
                    menu.findItem(R.id.count).isVisible = search
                }
                binding.History.visibility = View.GONE
                binding.ListView.visibility = View.VISIBLE
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
            intent.putExtra("type", menuList[position].type)
            if (search) {
                intent.putExtra("search", searchViewQwery)
                addHistory(menuList[position].data)
                saveHistopy()
            }
        } else {
            intent.putExtra("pesny", history[position])
            intent.putExtra("type", getTypeHistory(history[position]))
            addHistory(history[position])
            saveHistopy()
        }
        startActivity(intent)
    }

    private fun getTypeHistory(item: String): String {
        var type = "pesny_prasl_0"
        for (i in 0 until menuList.size) {
            if (menuList[i].data == item) {
                type = menuList[i].type
                break
            }
        }
        return type
    }

    override fun onResume() {
        super.onResume()
        editText?.addTextChangedListener(textWatcher)
    }

    private fun stopPosukPesen() {
        posukPesenJob?.cancel()
    }

    private fun startPosukPesen(poshuk: String) {
        posukPesenJob = CoroutineScope(Dispatchers.Main).launch {
            searchPasny(poshuk)
        }
    }

    private fun searchPasny(poshuk: String) {
        var poshuk1 = poshuk
        var setClear = true
        if (poshuk1 != "") {
            poshuk1 = poshuk1.replace("ё", "е", true)
            poshuk1 = poshuk1.replace("све", "сьве", true)
            poshuk1 = poshuk1.replace("сві", "сьві", true)
            poshuk1 = poshuk1.replace("свя", "сьвя", true)
            poshuk1 = poshuk1.replace("зве", "зьве", true)
            poshuk1 = poshuk1.replace("зві", "зьві", true)
            poshuk1 = poshuk1.replace("звя", "зьвя", true)
            poshuk1 = poshuk1.replace("зме", "зьме", true)
            poshuk1 = poshuk1.replace("змі", "зьмі", true)
            poshuk1 = poshuk1.replace("змя", "зьмя", true)
            poshuk1 = poshuk1.replace("зня", "зьня", true)
            poshuk1 = poshuk1.replace("сле", "сьле", true)
            poshuk1 = poshuk1.replace("слі", "сьлі", true)
            poshuk1 = poshuk1.replace("сль", "сьль", true)
            poshuk1 = poshuk1.replace("слю", "сьлю", true)
            poshuk1 = poshuk1.replace("сля", "сьля", true)
            poshuk1 = poshuk1.replace("сне", "сьне", true)
            poshuk1 = poshuk1.replace("сні", "сьні", true)
            poshuk1 = poshuk1.replace("сню", "сьню", true)
            poshuk1 = poshuk1.replace("сня", "сьня", true)
            poshuk1 = poshuk1.replace("спе", "сьпе", true)
            poshuk1 = poshuk1.replace("спі", "сьпі", true)
            poshuk1 = poshuk1.replace("спя", "сьпя", true)
            poshuk1 = poshuk1.replace("сце", "сьце", true)
            poshuk1 = poshuk1.replace("сці", "сьці", true)
            poshuk1 = poshuk1.replace("сць", "сьць", true)
            poshuk1 = poshuk1.replace("сцю", "сьцю", true)
            poshuk1 = poshuk1.replace("сця", "сьця", true)
            poshuk1 = poshuk1.replace("цце", "цьце", true)
            poshuk1 = poshuk1.replace("цці", "цьці", true)
            poshuk1 = poshuk1.replace("ццю", "цьцю", true)
            poshuk1 = poshuk1.replace("ззе", "зьзе", true)
            poshuk1 = poshuk1.replace("ззі", "зьзі", true)
            poshuk1 = poshuk1.replace("ззю", "зьзю", true)
            poshuk1 = poshuk1.replace("ззя", "зьзя", true)
            poshuk1 = poshuk1.replace("зле", "зьле", true)
            poshuk1 = poshuk1.replace("злі", "зьлі", true)
            poshuk1 = poshuk1.replace("злю", "зьлю", true)
            poshuk1 = poshuk1.replace("зля", "зьля", true)
            poshuk1 = poshuk1.replace("збе", "зьбе", true)
            poshuk1 = poshuk1.replace("збі", "зьбі", true)
            poshuk1 = poshuk1.replace("збя", "зьбя", true)
            poshuk1 = poshuk1.replace("нне", "ньне", true)
            poshuk1 = poshuk1.replace("нні", "ньні", true)
            poshuk1 = poshuk1.replace("нню", "ньню", true)
            poshuk1 = poshuk1.replace("ння", "ньня", true)
            poshuk1 = poshuk1.replace("лле", "льле", true)
            poshuk1 = poshuk1.replace("ллі", "льлі", true)
            poshuk1 = poshuk1.replace("ллю", "льлю", true)
            poshuk1 = poshuk1.replace("лля", "льля", true)
            poshuk1 = poshuk1.replace("дск", "дзк", true)
            poshuk1 = poshuk1.replace("дств", "дзтв", true)
            poshuk1 = poshuk1.replace("з’е", "зье", true)
            poshuk1 = poshuk1.replace("з’я", "зья", true)
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (r >= 3) {
                    if (poshuk1[r] == aM) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), true)
                    }
                }
            }
            searchViewQwery = poshuk1
            val menuListData = getMenuListData()
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
                if (builder.toString().replace("ё", "е", true).contains(poshuk1, true)) {
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

    private fun listRaw(filename: String) = PesnyAll.resursMap[filename] ?: -1

    private fun getMenuListData(pesny: String = "no_filter"): ArrayList<MenuListData> {
        var menuListData = ArrayList<MenuListData>()
        val inputStream = resources.openRawResource(R.raw.pesny_menu)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        reader.forEachLine {
            line = it
            val split = line.split("<>")
            val id = listRaw(split[0])
            if (id != -1)
                menuListData.add(MenuListData(id, split[1], split[0]))
        }
        if (pesny != "no_filter") {
            menuListData = menuListData.filter {
                it.type.contains(pesny)
            } as ArrayList<MenuListData>
        }
        return menuListData
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
                //searchViewQwery = edit
                edit = edit.replace("и", "і")
                edit = edit.replace("И", "І")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                when {
                    edit.length >= 3 -> {
                        stopPosukPesen()
                        startPosukPesen(edit)
                        binding.History.visibility = View.GONE
                        binding.ListView.visibility = View.VISIBLE
                    }
                    edit.length in 1..2 -> {
                        binding.History.visibility = View.VISIBLE
                        binding.ListView.visibility = View.GONE
                        textViewCount?.text = "(0)"
                    }
                    else -> {
                        menuList.clear()
                        menuList.addAll(getMenuListData())
                        menuList.sort()
                        adapter.notifyDataSetChanged()
                        textViewCount?.text = getString(R.string.seash, menuList.size)
                        binding.History.visibility = View.GONE
                        binding.ListView.visibility = View.VISIBLE
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

    private inner class MenuPesnyListAdapter(activity: Activity) : ArrayAdapter<MenuListData>(activity, R.layout.simple_list_item_2, R.id.label, menuList) {
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
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            viewHolder.text.text = menuList[position].data
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch)
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextViewRobotoCondensed)

    companion object {
        fun getInstance(pesny: String): MenuPesny {
            val bundle = Bundle()
            bundle.putString("pesny", pesny)
            val menuPesny = MenuPesny()
            menuPesny.arguments = bundle
            return menuPesny
        }
    }
}