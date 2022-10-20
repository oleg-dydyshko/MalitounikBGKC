package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
    private var searchViewQwery = ""
    private var actionExpandOn = false
    private var _binding: MenuPesnyBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MenuListAdaprer
    private val textWatcher = MyTextWatcher()

    companion object {
        fun getTextPasliaPrychascia(): ArrayList<MenuListData> {
            val dataSearch = ArrayList<MenuListData>()
            dataSearch.add(MenuListData("Малітва падзякі", "paslia_prychascia1"))
            dataSearch.add(MenuListData("Малітва сьв. Васіля Вялікага", "paslia_prychascia2"))
            dataSearch.add(MenuListData("Малітва Сымона Мэтафраста", "paslia_prychascia3"))
            dataSearch.add(MenuListData("Iншая малітва", "paslia_prychascia4"))
            dataSearch.add(MenuListData("Малітва да Найсьвяцейшай Багародзіцы", "paslia_prychascia5"))
            return dataSearch
        }

        fun getTextSubBogaslugbovuiaVichernia(isSearch: Boolean = false): ArrayList<MenuListData> {
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.sub_bogaslugbovuia_vichernia)
            dataSearch.add(MenuListData(r1[0], "viachernia_niadzeli"))
            dataSearch.add(MenuListData(r1[1], "viachernia_liccia_i_blaslavenne_xliabou"))
            dataSearch.add(MenuListData(r1[2], "viachernia_na_kozny_dzen"))
            dataSearch.add(MenuListData(r1[3], "viachernia_u_vialikim_poscie"))
            dataSearch.add(MenuListData(r1[4], "viaczernia_bierascie"))
            if (!isSearch) dataSearch.add(MenuListData(r1[5], "1"))
            return dataSearch
        }

        fun getTextAktoixList(): ArrayList<MenuListData> {
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.aktoix_list)
            dataSearch.add(MenuListData(r1[0], "viachernia_ton1"))
            dataSearch.add(MenuListData(r1[1], "viachernia_ton2"))
            dataSearch.add(MenuListData(r1[2], "viachernia_ton3"))
            dataSearch.add(MenuListData(r1[3], "viachernia_ton4"))
            dataSearch.add(MenuListData(r1[4], "viachernia_ton5"))
            dataSearch.add(MenuListData(r1[5], "viachernia_ton6"))
            dataSearch.add(MenuListData(r1[6], "viachernia_ton7"))
            dataSearch.add(MenuListData(r1[7], "viachernia_ton8"))
            dataSearch.add(MenuListData(r1[8], "viachernia_bagarodzichnia_adpushchalnyia"))
            return dataSearch
        }

        fun getTextViacherniaList(isSearch: Boolean = false): ArrayList<MenuListData> {
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.viachernia_list)
            dataSearch.add(MenuListData(r1[0], "viachernia_mineia_agulnaia1"))
            dataSearch.add(MenuListData(r1[1], "viachernia_mineia_agulnaia2"))
            dataSearch.add(MenuListData(r1[2], "viachernia_mineia_agulnaia3"))
            dataSearch.add(MenuListData(r1[3], "viachernia_mineia_agulnaia4"))
            dataSearch.add(MenuListData(r1[4], "viachernia_mineia_agulnaia5"))
            dataSearch.add(MenuListData(r1[5], "viachernia_mineia_agulnaia6"))
            dataSearch.add(MenuListData(r1[6], "viachernia_mineia_agulnaia7"))
            dataSearch.add(MenuListData(r1[7], "viachernia_mineia_agulnaia8"))
            dataSearch.add(MenuListData(r1[8], "viachernia_mineia_agulnaia9"))
            dataSearch.add(MenuListData(r1[9], "viachernia_mineia_agulnaia10"))
            dataSearch.add(MenuListData(r1[10], "viachernia_mineia_agulnaia11"))
            dataSearch.add(MenuListData(r1[11], "viachernia_mineia_agulnaia12"))
            dataSearch.add(MenuListData(r1[12], "viachernia_mineia_agulnaia13"))
            dataSearch.add(MenuListData(r1[13], "viachernia_mineia_agulnaia14"))
            dataSearch.add(MenuListData(r1[14], "viachernia_mineia_agulnaia15"))
            dataSearch.add(MenuListData(r1[15], "viachernia_mineia_agulnaia16"))
            dataSearch.add(MenuListData(r1[16], "viachernia_mineia_agulnaia17"))
            dataSearch.add(MenuListData(r1[17], "viachernia_mineia_agulnaia18"))
            dataSearch.add(MenuListData(r1[18], "viachernia_mineia_agulnaia19"))
            dataSearch.add(MenuListData(r1[19], "viachernia_mineia_agulnaia20"))
            dataSearch.add(MenuListData(r1[20], "viachernia_mineia_agulnaia21"))
            dataSearch.add(MenuListData(r1[21], "viachernia_mineia_agulnaia22"))
            dataSearch.add(MenuListData(r1[22], "viachernia_mineia_agulnaia23"))
            dataSearch.add(MenuListData(r1[23], "viachernia_mineia_agulnaia24"))
            if (!isSearch) dataSearch.add(MenuListData(r1[24], "1"))
            return dataSearch
        }
    }

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
        activity?.let {
            data.add(MenuListData("Боская Літургія між сьвятымі айца нашага Яна Залатавуснага", "lit_jan_zalat"))
            data.add(MenuListData("Боская Літургія ў Велікодны перыяд", "lit_jan_zalat_vielikodn"))
            data.add(MenuListData("Боская Літургія між сьвятымі айца нашага Базыля Вялікага", "lit_vasila_vialikaha"))
            data.add(MenuListData("Літургія раней асьвячаных дароў", "lit_ran_asv_dar"))
            data.add(MenuListData("Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", "nabazenstva_maci_bozaj_niast_dap"))
            data.add(MenuListData("Малітвы пасьля сьвятога прычасьця", "1"))
            data.add(MenuListData("Ютрань нядзельная (у скароце)", "jutran_niadzelnaja"))
            data.add(MenuListData("Вячэрня", "2"))
            data.add(MenuListData("Абедніца", "abiednica"))
            data.add(MenuListData("Служба за памерлых — Малая паніхіда", "panichida_mal"))
            data.add(MenuListData("Трапары і кандакі нядзельныя васьмі тонаў", "3"))
            data.add(MenuListData("Мінэя месячная", "4"))
            data.add(MenuListData("Малебны канон Найсьвяцейшай Багародзіцы", "kanon_malebny_baharodzicy"))
            data.add(MenuListData("Вялікі пакаянны канон сьвятога Андрэя Крыцкага", "kanon_a_kryckaha"))
            data.add(MenuListData("Трыёдзь", "5"))
            data.add(MenuListData("Малебен сьвятым айцам нашым, роўным апосталам Кірылу і Мятоду, настаўнікам славянскім", "malebien_kiryla_miatod"))
            data.add(MenuListData("Служба за памерлых на кожны дзень тыдня", "sluzba_za_pamierlych_na_kozny_dzien_tydnia"))
            data.add(MenuListData("Мінэя агульная", "6"))
            data.add(MenuListData("Служба Найсьвяцейшай Багародзіцы", "sluzba_najsviaciejszaj_baharodzicy"))
            data.sort()
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
            binding.ListView.isVerticalScrollBarEnabled = false
            binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    if (firstVisibleItem == 1) {
                        val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(binding.ListView.windowToken, 0)
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

    private fun searchText() {
        for (i in data.indices) {
            val res = data[i].resurs
            if (!(res == "1" || res == "2" || res == "3" || res == "4" || res == "5" || res == "6")) {
                dataSearch.add(data[i])
            }
        }
        dataSearch.addAll(getTextPasliaPrychascia())
        dataSearch.addAll(getTextSubBogaslugbovuiaVichernia(true))
        dataSearch.addAll(getTextAktoixList())
        for (i in 1..8) {
            dataSearch.add(MenuListData(getString(R.string.ton, i.toString()), "ton$i"))
        }
        val sluzba = SlugbovyiaTextu()
        var mesiach = sluzba.getMineiaMesiachnaia()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getVilikiTydzen()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getSvetlyTydzen()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getMineiaSviatochnaia()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen1()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen2()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen3()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen4()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen5()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen6()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title, mesiach[i].resource))
        }
        dataSearch.addAll(getTextViacherniaList(true))
        dataSearch.sort()
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.malitvy_prynagodnyia, menu)
        val searchViewItem = menu.findItem(R.id.action_seashe_text)
        searchView = searchViewItem.actionView as SearchView
        if (actionExpandOn) {
            searchViewItem.expandActionView()
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                actionExpandOn = true
                dataSearch.clear()
                searchText()
                data.clear()
                data.addAll(dataSearch)
                adapter.notifyDataSetChanged()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                actionExpandOn = false
                data.clear()
                dataSearch.clear()
                data.addAll(dataOriginal)
                dataSearch.addAll(dataOriginal)
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
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
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
        activity?.let {
            when (data[position].resurs) {
                "1" -> {
                    val intent = Intent(it, MalitvyPasliaPrychascia::class.java)
                    startActivity(intent)
                }
                "2" -> {
                    val intent = Intent(it, SubMenuBogashlugbovyaViachernia::class.java)
                    startActivity(intent)
                }
                "3" -> {
                    val intent = Intent(it, TonNiadzelny::class.java)
                    startActivity(intent)
                }
                "4" -> {
                    val intent = Intent(it, MineiaMesiachnaia::class.java)
                    startActivity(intent)
                }
                "5" -> {
                    val intent = Intent(it, BogashlugbovyaTryjodz::class.java)
                    startActivity(intent)
                }
                "6" -> {
                    val intent = Intent(it, MineiaAgulnaia::class.java)
                    startActivity(intent)
                }
                else -> {
                    if (MainActivity.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("title", data[position].title)
                        intent.putExtra("resurs", data[position].resurs)
                        startActivity(intent)
                    } else {
                        val dadatak = DialogInstallDadatak()
                        dadatak.show(childFragmentManager, "dadatak")
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
        outState.putString("SearchViewQwery", editText?.text.toString())
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
                if (check != 0) {
                    editText?.removeTextChangedListener(this)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
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
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = context.getBaseDzenNoch()
            viewHolder.text.text = data[position].title
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
                            if (item.title.contains(constraint1, true)) {
                                founded.add(item)
                            }
                        }
                        result.values = founded
                        result.count = founded.size
                    } else {
                        result.values = dataSearch
                        result.count = dataSearch.size
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
