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
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.MenuPesnyBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MenuPesny : BaseFragment(), AdapterView.OnItemClickListener {
    private var mLastClickTime: Long = 0
    private var editText: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var searchViewQwery = ""
    private var search = false
    private var pesny = "prasl"
    private lateinit var adapter: MenuPesnyListAdapter
    private val menuList = ArrayList<MenuPesnyData.Data>()
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var chin: SharedPreferences
    private val textWatcher = MyTextWatcher()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { fraragment ->
            chin = fraragment.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            pesny = arguments?.getString("pesny") ?: "prasl"
            getMenuListData(pesny)
            adapter = MenuPesnyListAdapter(fraragment, menuList)
            binding.ListView.adapter = adapter
            binding.ListView.isVerticalScrollBarEnabled = false
            binding.ListView.onItemClickListener = this
            val dzenNoch = (fraragment as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                binding.ListView.setBackgroundResource(R.color.colorbackground_material_dark)
                binding.ListView.selector = ContextCompat.getDrawable(fraragment, R.drawable.selector_dark)
            }
            binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                    if (i == 1) {
                        val imm1 = fraragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                        searchView?.clearFocus()
                    }
                }

                override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
            })
            if (chin.getString("history_pesny", "") != "") {
                val gson = Gson()
                val json = chin.getString("history_pesny", "")
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
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
                val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
                dialogClearHishory.show(childFragmentManager, "dialogClearHishory")
                return@setOnItemLongClickListener true
            }
        }
    }

    private fun addHistory(item: String) {
        var st = item.replace("<font color=#d00505>", "")
        st = st.replace("</font>", "")
        val nachalo = st.lastIndexOf("\n")
        if (nachalo != -1) st = st.substring(0, nachalo)
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
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(history, type)
        val prefEditors = chin.edit()
        prefEditors.putString("history_pesny", json)
        prefEditors.apply()
    }

    fun cleanFullHistory() {
        history.clear()
        saveHistopy()
        activity?.invalidateOptionsMenu()
    }

    fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistopy()
        if (history.size == 0) activity?.invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
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
                    activity?.let {
                        val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                    }
                }
                true
            }
            editText?.imeOptions = EditorInfo.IME_ACTION_DONE
            editText?.addTextChangedListener(textWatcher)
            editText?.setBackgroundResource(R.drawable.underline_white)
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.count).isVisible = search
        menu.findItem(R.id.action_clean_histopy).isVisible = isHistory()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(childFragmentManager, "dialogClearHishory")
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SearchViewQwery", searchView?.query.toString())
        outState.putBoolean("search", search)
    }

    private fun isHistory() = history.size != 0

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pesny, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(R.id.search)
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.search)
        textViewCount = menu.findItem(R.id.count).actionView as TextView
        activity?.let {
            val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            searcheTextView?.typeface = MainActivity.createFont(Typeface.NORMAL)
            textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        }
        if (search) {
            searchViewItem.expandActionView()
            getMenuListData()
            textViewCount?.text = getString(R.string.seash, menuList.size)
            adapter.notifyDataSetChanged()
            menu.findItem(R.id.count).isVisible = search
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                search = true
                getMenuListData()
                textViewCount?.text = getString(R.string.seash, menuList.size)
                adapter.notifyDataSetChanged()
                menu.findItem(R.id.count).isVisible = search
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                searchView?.setOnQueryTextListener(null)
                search = false
                getMenuListData(pesny)
                adapter.notifyDataSetChanged()
                menu.findItem(R.id.count).isVisible = search
                binding.History.visibility = View.GONE
                binding.ListView.visibility = View.VISIBLE
                return true
            }
        })
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            searchViewItem.expandActionView()
            searchView?.setQuery(searchViewQwery, true)
            searchView?.clearFocus()
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
            var title = menuList[position].title
            val nachalo = title.lastIndexOf("\n")
            if (nachalo != -1) title = title.substring(0, nachalo)
            intent.putExtra("pesny", title)
            intent.putExtra("type", menuList[position].resurs)
            if (search) {
                addHistory(menuList[position].title)
                saveHistopy()
            }
        } else {
            intent.putExtra("pesny", history[position])
            intent.putExtra("type", getTypeHistory(history[position]))
            addHistory(history[position])
            saveHistopy()
        }
        startActivity(intent)
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000L)
            searchView?.setOnQueryTextListener(null)
            search = false
            getMenuListData(pesny)
            adapter.notifyDataSetChanged()
            _binding?.History?.visibility = View.GONE
            _binding?.ListView?.visibility = View.VISIBLE
        }
    }

    private fun getTypeHistory(item: String): String {
        var type = "pesny_prasl_0"
        for (i in 0 until menuList.size) {
            if (menuList[i].title == item) {
                type = menuList[i].resurs
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
        var poshuk1 = MainActivity.zamena(poshuk)
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
        menuList.clear()
        val fullMenuList = MenuPesnyData().getPesnyAll()
        for (i in fullMenuList.indices) {
            val inputStream = resources.openRawResource(PesnyAll.resursMap[fullMenuList[i].resurs] ?: R.raw.pesny_prasl_0)
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
                menuList.add(fullMenuList[i])
            }
        }
        menuList.sort()
        adapter.notifyDataSetChanged()
        textViewCount?.text = resources.getString(R.string.seash, menuList.size)
    }

    private fun getMenuListData(pesny: String = "") {
        val menuPesnyData = MenuPesnyData()
        menuList.clear()
        when (pesny) {
            "bel" -> menuList.addAll(menuPesnyData.getPesnyBel())
            "bag" -> menuList.addAll(menuPesnyData.getPesnyBag())
            "kal" -> menuList.addAll(menuPesnyData.getPesnyKal())
            "prasl" -> menuList.addAll(menuPesnyData.getPesnyPrasl())
            "taize" -> menuList.addAll(menuPesnyData.getPesnyTaize())
            else -> menuList.addAll(menuPesnyData.getPesnyAll())
        }
        menuList.sort()
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
                        binding.History.visibility = View.GONE
                        binding.ListView.visibility = View.VISIBLE
                    }

                    edit.length in 1..2 -> {
                        binding.History.visibility = View.VISIBLE
                        binding.ListView.visibility = View.GONE
                        textViewCount?.text = "(0)"
                    }

                    else -> {
                        getMenuListData()
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

    private inner class MenuPesnyListAdapter(private val activity: Activity, private val menuList: ArrayList<MenuPesnyData.Data>) : ArrayAdapter<MenuPesnyData.Data>(activity, R.layout.simple_list_item_2, R.id.label, menuList) {
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
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            var spanString = SpannableString(menuList[position].title)
            val nachalo = spanString.lastIndexOf("\n")
            if (nachalo != -1) {
                if (search) {
                    spanString.setSpan(StyleSpan(Typeface.ITALIC), nachalo, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), nachalo, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    spanString = SpannableString(spanString.substring(0, nachalo))
                }
            }
            viewHolder.text.text = spanString
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

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