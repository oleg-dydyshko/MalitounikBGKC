package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
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
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.MenuPesnyBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuBogashlugbovya : BaseFragment(), AdapterView.OnItemClickListener {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<MenuListData>()
    private val dataOriginal = ArrayList<MenuListData>()
    private val dataSearch = ArrayList<MenuListData>()
    private var editText: AutoCompleteTextView? = null
    private var searchView: SearchView? = null
    private var textViewCount: TextView? = null
    private var searchViewQwery = ""
    private var actionExpandOn = false
    private var _binding: MenuPesnyBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MenuListAdaprer
    private val textWatcher = MyTextWatcher()

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
        (activity as? SlugbovyiaTextu)?.let {
            data.addAll(it.getTextBogaslugbovyiaFolderList())
            data.addAll(it.getTextBogaslugbovyiaList())
            dataOriginal.addAll(data)
            adapter = MenuListAdaprer(it as BaseActivity)
            binding.ListView.adapter = adapter
            binding.ListView.onItemClickListener = this
            val dzenNoch = it.getBaseDzenNoch()
            if (dzenNoch) {
                binding.ListView.setBackgroundResource(R.color.colorbackground_material_dark)
                binding.ListView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            }
            if (savedInstanceState != null) {
                searchViewQwery = savedInstanceState.getString("SearchViewQwery", "")
                actionExpandOn = savedInstanceState.getBoolean("actionExpandOn")
            }
            if (actionExpandOn) {
                dataSearch.clear()
                dataSearch.addAll(it.getBogaslugbovyiaSearchText())
                data.clear()
                data.addAll(dataSearch)
                adapter.notifyDataSetChanged()
            }
            binding.ListView.isVerticalScrollBarEnabled = false
            binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    if (firstVisibleItem == 1) {
                        val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(binding.ListView.windowToken, 0)
                        searchView?.clearFocus()
                    }
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        editText?.addTextChangedListener(textWatcher)
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
            editText?.let { autoCompleteTextView ->
                autoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        activity?.let {
                            val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm1.hideSoftInputFromWindow(autoCompleteTextView.windowToken, 0)
                        }
                    }
                    true
                }
                autoCompleteTextView.imeOptions = EditorInfo.IME_ACTION_DONE
                autoCompleteTextView.addTextChangedListener(textWatcher)
                autoCompleteTextView.setBackgroundResource(R.drawable.underline_white)
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            menu.findItem(R.id.action_search_tyxt_bogaslug).isVisible = k.getBoolean("admin", false)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_search_tyxt_bogaslug) {
            activity?.let {
                val i = Intent()
                i.setClassName(it, MainActivity.SEARCHBOGASHLUGBOVYA)
                startActivity(i)
                return true
            }
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pesny, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(R.id.search)
        searchView = searchViewItem.actionView as SearchView
        textViewCount = menu.findItem(R.id.count).actionView as TextView
        activity?.let {
            val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            searcheTextView?.typeface = MainActivity.createFont(Typeface.NORMAL)
            textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        }
        if (actionExpandOn) {
            searchViewItem.expandActionView()
            textViewCount?.text = getString(R.string.seash, dataSearch.size)
            menu.findItem(R.id.count).isVisible = actionExpandOn
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                (activity as? SlugbovyiaTextu)?.let {
                    actionExpandOn = true
                    dataSearch.clear()
                    dataSearch.addAll(it.getBogaslugbovyiaSearchText())
                    data.clear()
                    data.addAll(dataSearch)
                    textViewCount?.text = getString(R.string.seash, dataSearch.size)
                    menu.findItem(R.id.count).isVisible = actionExpandOn
                    adapter.notifyDataSetChanged()
                }
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                actionExpandOn = false
                data.clear()
                dataSearch.clear()
                data.addAll(dataOriginal)
                dataSearch.addAll(dataOriginal)
                menu.findItem(R.id.count).isVisible = actionExpandOn
                adapter.notifyDataSetChanged()
                return true
            }
        })
        searchView?.queryHint = getString(R.string.searche_bogasluz_text)
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            searchViewItem.expandActionView()
            editText?.setText(searchViewQwery)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        editText?.removeTextChangedListener(textWatcher)
        activity?.let {
            when (data[position].resurs) {
                "1" -> {
                    val intent = Intent(it, MalitvyPasliaPrychascia::class.java)
                    startActivity(intent)
                }
                "2" -> {
                    val intent = Intent(it, TonNiadzelny::class.java)
                    startActivity(intent)
                }
                "3" -> {
                    val intent = Intent(it, MineiaMesiachnaiaMonth::class.java)
                    startActivity(intent)
                }
                "4" -> {
                    val intent = Intent(it, BogashlugbovyaTryjodz::class.java)
                    startActivity(intent)
                }
                "5" -> {
                    val intent = Intent(it, MineiaAgulnaia::class.java)
                    startActivity(intent)
                }
                "6" -> {
                    val intent = Intent(it, Aktoix::class.java)
                    startActivity(intent)
                }
                "7" -> {
                    val intent = Intent(it, Trebnik::class.java)
                    startActivity(intent)
                }
                "8" -> {
                    val intent = Intent(it, Chasaslou::class.java)
                    startActivity(intent)
                }
                else -> {
                    if ((it as BaseActivity).checkmoduleResources()) {
                        val intent = Intent()
                        if (data[position].resurs.contains("paslia_prychascia")) {
                            val pos = data[position].resurs.substring("paslia_prychascia".length).toInt()
                            intent.setClassName(it, MainActivity.PASLIAPRYCHASCIA)
                            intent.putExtra("paslia_prychascia", pos - 1)
                        } else {
                            intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                            var title = data[position].title
                            val t1 = title.lastIndexOf("\n")
                            if (t1 != -1) {
                                title = title.substring(0, t1)
                            }
                            intent.putExtra("title", title)
                            intent.putExtra("resurs", data[position].resurs)
                        }
                        startActivity(intent)
                    } else {
                        it.installFullMalitounik()
                    }
                }
            }
        }
        if (actionExpandOn) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000L)
                actionExpandOn = false
                data.clear()
                dataSearch.clear()
                data.addAll(dataOriginal)
                dataSearch.addAll(dataOriginal)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        editText?.let {
            outState.putString("SearchViewQwery", it.text.toString())
        }
        outState.putBoolean("actionExpandOn", actionExpandOn)
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
            var edit = s.toString()
            if (editch) {
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                edit = edit.replace("И", "І")
                edit = edit.replace("Щ", "Ў")
                edit = edit.replace("Ъ", "'")
                val preLength = edit.length
                edit = MainActivity.zamena(edit)
                if (preLength != edit.length) {
                    editPosition = edit.length
                }
                if (check != 0) {
                    editText?.let {
                        it.removeTextChangedListener(this)
                        it.setText(edit)
                        it.setSelection(editPosition)
                        it.addTextChangedListener(this)
                    }
                }
            }
            adapter.filter.filter(edit)
        }
    }

    private inner class MenuListAdaprer(private val context: BaseActivity) : ArrayAdapter<MenuListData>(context, R.layout.simple_list_item_2, R.id.label, data) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = context.getBaseDzenNoch()
            val spanString = SpannableString(data[position].title)
            val nachalo = spanString.lastIndexOf("\n")
            if (nachalo != -1) {
                spanString.setSpan(StyleSpan(Typeface.ITALIC), nachalo, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), nachalo, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            viewHolder.text.text = spanString
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    var constraint1 = constraint
                    constraint1 = constraint1.toString()
                    val result = FilterResults()
                    if (constraint1.isNotEmpty()) {
                        val founded = ArrayList<MenuListData>()
                        for (item in dataSearch) {
                            var title = item.title
                            val t1 = title.indexOf("\n")
                            if (t1 != -1) {
                                title = title.substring(0, t1)
                            }
                            if (title.contains(constraint1, true)) {
                                founded.add(item)
                            }
                        }
                        result.values = founded
                        result.count = founded.size
                    } else {
                        result.values = dataSearch
                        result.count = dataSearch.size
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        textViewCount?.text = resources.getString(R.string.seash, result.count)
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

    private class ViewHolder(var text: TextView)
}
