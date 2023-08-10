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
import android.text.style.AbsoluteSizeSpan
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

    companion object {
        fun getTextPasliaPrychascia(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = "\nМалітвы пасьля сьвятога прычасьця"
            val dataSearch = ArrayList<MenuListData>()
            dataSearch.add(MenuListData("Малітва падзякі$opisanie", "paslia_prychascia1"))
            dataSearch.add(MenuListData("Малітва сьв. Васіля Вялікага$opisanie", "paslia_prychascia2"))
            dataSearch.add(MenuListData("Малітва Сымона Мэтафраста$opisanie", "paslia_prychascia3"))
            dataSearch.add(MenuListData("Iншая малітва$opisanie", "paslia_prychascia4"))
            dataSearch.add(MenuListData("Малітва да Найсьвяцейшай Багародзіцы$opisanie", "paslia_prychascia5"))
            return dataSearch
        }

        fun getTextSubBogaslugbovuiaVichernia(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = "\nВячэрня"
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.sub_bogaslugbovuia_vichernia)
            dataSearch.add(MenuListData(r1[0] + opisanie, "viachernia_niadzeli"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "viachernia_liccia_i_blaslavenne_xliabou"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "viachernia_na_kozny_dzen"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "viachernia_u_vialikim_poscie"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "viaczernia_bierascie"))
            return dataSearch
        }

        fun getTextAktoixList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = "\nАктоіх"
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.aktoix_list)
            dataSearch.add(MenuListData(r1[0] + opisanie, "viachernia_ton1"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "viachernia_ton2"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "viachernia_ton3"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "viachernia_ton4"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "viachernia_ton5"))
            dataSearch.add(MenuListData(r1[5] + opisanie, "viachernia_ton6"))
            dataSearch.add(MenuListData(r1[6] + opisanie, "viachernia_ton7"))
            dataSearch.add(MenuListData(r1[7] + opisanie, "viachernia_ton8"))
            dataSearch.add(MenuListData(r1[8] + opisanie, "viachernia_bagarodzichnia_adpushchalnyia"))
            return dataSearch
        }

        fun getTextTonNaKoznyDzenList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = "\nМінэя агульная -> Трапары і кандакі штодзённыя - на кожны дзень тыдня"
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.ton_kogny_dzen)
            dataSearch.add(MenuListData(r1[0] + opisanie, "ton1_budni"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "ton2_budni"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "ton3_budni"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "ton4_budni"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "ton5_budni"))
            dataSearch.add(MenuListData(r1[5] + opisanie, "ton6_budni"))
            return dataSearch
        }

        fun getTextViacherniaList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = "\nМінэя агульная"
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.viachernia_list)
            dataSearch.add(MenuListData(r1[0] + opisanie, "viachernia_mineia_agulnaia1"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "viachernia_mineia_agulnaia2"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "viachernia_mineia_agulnaia3"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "viachernia_mineia_agulnaia4"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "viachernia_mineia_agulnaia5"))
            dataSearch.add(MenuListData(r1[5] + opisanie, "viachernia_mineia_agulnaia6"))
            dataSearch.add(MenuListData(r1[6] + opisanie, "viachernia_mineia_agulnaia7"))
            dataSearch.add(MenuListData(r1[7] + opisanie, "viachernia_mineia_agulnaia8"))
            dataSearch.add(MenuListData(r1[8] + opisanie, "viachernia_mineia_agulnaia9"))
            dataSearch.add(MenuListData(r1[9] + opisanie, "viachernia_mineia_agulnaia10"))
            dataSearch.add(MenuListData(r1[10] + opisanie, "viachernia_mineia_agulnaia11"))
            dataSearch.add(MenuListData(r1[11] + opisanie, "viachernia_mineia_agulnaia12"))
            dataSearch.add(MenuListData(r1[12] + opisanie, "viachernia_mineia_agulnaia13"))
            dataSearch.add(MenuListData(r1[13] + opisanie, "viachernia_mineia_agulnaia14"))
            dataSearch.add(MenuListData(r1[14] + opisanie, "viachernia_mineia_agulnaia15"))
            dataSearch.add(MenuListData(r1[15] + opisanie, "viachernia_mineia_agulnaia16"))
            dataSearch.add(MenuListData(r1[16] + opisanie, "viachernia_mineia_agulnaia17"))
            dataSearch.add(MenuListData(r1[17] + opisanie, "viachernia_mineia_agulnaia18"))
            dataSearch.add(MenuListData(r1[18] + opisanie, "viachernia_mineia_agulnaia19"))
            dataSearch.add(MenuListData(r1[19] + opisanie, "viachernia_mineia_agulnaia20"))
            dataSearch.add(MenuListData(r1[20] + opisanie, "viachernia_mineia_agulnaia21"))
            dataSearch.add(MenuListData(r1[21] + opisanie, "viachernia_mineia_agulnaia22"))
            dataSearch.add(MenuListData(r1[22] + opisanie, "viachernia_mineia_agulnaia23"))
            dataSearch.add(MenuListData(r1[23] + opisanie, "viachernia_mineia_agulnaia24"))
            if (!isSearch) dataSearch.add(MenuListData(r1[24], "1"))
            return dataSearch
        }
    }

    private fun getTextBogaslugbovyiaFolderList(): ArrayList<MenuListData> {
        val dataSearch = ArrayList<MenuListData>()
        dataSearch.add(MenuListData("МАЛІТВЫ ПАСЬЛЯ СЬВЯТОГА ПРЫЧАСЬЦЯ", "1"))
        dataSearch.add(MenuListData("ВЯЧЭРНЯ", "2"))
        dataSearch.add(MenuListData("ТРАПАРЫ І КАНДАКІ НЯДЗЕЛЬНЫЯ ВАСЬМІ ТОНАЎ", "3"))
        dataSearch.add(MenuListData("МІНЭЯ МЕСЯЧНАЯ", "4"))
        dataSearch.add(MenuListData("ТРЫЁДЗЬ", "5"))
        dataSearch.add(MenuListData("МІНЭЯ АГУЛЬНАЯ", "6"))
        dataSearch.add(MenuListData("АКТОІХ", "7"))
        dataSearch.sort()
        return dataSearch
    }

    private fun getTextBogaslugbovyiaList(isSearch: Boolean = false): ArrayList<MenuListData> {
        var opisanie = ""
        if (isSearch) opisanie = "\nБогаслужбовыя тэксты"
        val dataSearch = ArrayList<MenuListData>()
        dataSearch.add(MenuListData("Боская Літургія сьв. Яна Залатавуснага$opisanie", "lit_jan_zalat"))
        dataSearch.add(MenuListData("Боская Літургія ў Велікодны перыяд$opisanie", "lit_jan_zalat_vielikodn"))
        dataSearch.add(MenuListData("Боская Літургія сьв. Васіля Вялікага$opisanie", "lit_vasila_vialikaha"))
        dataSearch.add(MenuListData("Літургія раней асьвячаных дароў$opisanie", "lit_ran_asv_dar"))
        dataSearch.add(MenuListData("Набажэнства ў гонар Маці Божай Нястомнай Дапамогі$opisanie", "nabazenstva_maci_bozaj_niast_dap"))
        dataSearch.add(MenuListData("Ютрань нядзельная (у скароце)$opisanie", "jutran_niadzelnaja"))
        dataSearch.add(MenuListData("Абедніца$opisanie", "abiednica"))
        dataSearch.add(MenuListData("Служба за памерлых — Малая паніхіда$opisanie", "panichida_mal"))
        dataSearch.add(MenuListData("Малебны канон Найсьвяцейшай Багародзіцы$opisanie", "kanon_malebny_baharodzicy"))
        dataSearch.add(MenuListData("Вялікі пакаянны канон сьвятога Андрэя Крыцкага$opisanie", "kanon_a_kryckaha"))
        dataSearch.add(MenuListData("Малебен сьв. Кірылу і Мятоду, настаўнікам славянскім$opisanie", "malebien_kiryla_miatod"))
        dataSearch.add(MenuListData("Служба за памерлых на кожны дзень тыдня$opisanie", "sluzba_za_pamierlych_na_kozny_dzien_tydnia"))
        dataSearch.add(MenuListData("Служба Найсьвяцейшай Багародзіцы$opisanie", "sluzba_najsviaciejszaj_baharodzicy"))
        dataSearch.add(MenuListData("Служба аб вызваленьні бязьвінна зьняволеных$opisanie", "sluzba_vyzvalen_biazvinna_zniavolenych"))
        dataSearch.sort()
        return dataSearch
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
            data.addAll(getTextBogaslugbovyiaFolderList())
            data.addAll(getTextBogaslugbovyiaList())
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
                searchText()
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

    private fun getSluzba(sluzba: Int): String {
        val opisanie = when (sluzba) {
            SlugbovyiaTextu.JUTRAN -> ". Ютрань"
            SlugbovyiaTextu.LITURHIJA -> ". Літургія"
            SlugbovyiaTextu.VIACZERNIA -> ". Вячэрня"
            SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA -> ". Вячэрня з Літургіяй"
            SlugbovyiaTextu.VIALHADZINY -> ". Вялікія гадзіны"
            SlugbovyiaTextu.ABIEDNICA -> ". Абедніца"
            SlugbovyiaTextu.PAVIACHERNICA -> ". Малая павячэрніца"
            SlugbovyiaTextu.PAUNOCHNICA -> ". Паўночніца"
            else -> ""
        }
        return opisanie
    }

    private fun searchText() {
        dataSearch.addAll(getTextBogaslugbovyiaList(true))
        dataSearch.addAll(getTextPasliaPrychascia(true))
        dataSearch.addAll(getTextSubBogaslugbovuiaVichernia(true))
        dataSearch.addAll(getTextAktoixList(true))
        dataSearch.addAll(getTextViacherniaList(true))
        dataSearch.addAll(getTextTonNaKoznyDzenList(true))
        var opisanie = "\nТрапары і кандакі нядзельныя васьмі тонаў"
        for (i in 1..8) {
            dataSearch.add(MenuListData(getString(R.string.ton, i.toString() + opisanie), "ton$i"))
        }
        val sluzba = SlugbovyiaTextu()
        var mesiach = sluzba.getMineiaMesiachnaia()
        opisanie = "\nМінэя месячная"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getVilikiTydzen()
        opisanie = "\nТрыёдзь -> Службы Вялікага тыдня"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getSvetlyTydzen()
        opisanie = "\nТрыёдзь -> Службы Сьветлага тыдня"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getMineiaSviatochnaia()
        opisanie = "\nТрыёдзь -> Трыёдзь сьвяточная"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen1()
        opisanie = "\nТрыёдзь -> Трыёдзь посная -> Службы 1-га тыдня Вялікага посту"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen2()
        opisanie = "\nТрыёдзь -> Трыёдзь посная -> Службы 2-га тыдня Вялікага посту"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen3()
        opisanie = "\nТрыёдзь -> Трыёдзь посная -> Службы 3-га тыдня Вялікага посту"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen4()
        opisanie = "\nТрыёдзь -> Трыёдзь посная -> Службы 4-га тыдня Вялікага посту"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen5()
        opisanie = "\nТрыёдзь -> Трыёдзь посная -> Службы 5-га тыдня Вялікага посту"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen6()
        opisanie = "\nТрыёдзь -> Трыёдзь посная -> Службы 6-га тыдня Вялікага посту"
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + getSluzba(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
        }
        dataSearch.sort()
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
        val searchViewItem = menu.findItem(R.id.search)
        searchView = searchViewItem.actionView as SearchView
        textViewCount = menu.findItem(R.id.count).actionView as TextView
        activity?.let {
            val searcheTextView = searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
            searcheTextView.typeface = MainActivity.createFont(Typeface.NORMAL)
            textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        }
        if (actionExpandOn) {
            searchViewItem.expandActionView()
            textViewCount?.text = getString(R.string.seash, dataSearch.size)
            menu.findItem(R.id.count).isVisible = actionExpandOn
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                actionExpandOn = true
                dataSearch.clear()
                searchText()
                data.clear()
                data.addAll(dataSearch)
                textViewCount?.text = getString(R.string.seash, dataSearch.size)
                menu.findItem(R.id.count).isVisible = actionExpandOn
                adapter.notifyDataSetChanged()
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
                "7" -> {
                    val intent = Intent(it, Aktoix::class.java)
                    startActivity(intent)
                }
                else -> {
                    if (MainActivity.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        var title = data[position].title
                        val t1 = title.lastIndexOf("\n")
                        if (t1 != -1) {
                            title = title.substring(0, t1)
                        }
                        intent.putExtra("title", title)
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
                val preLength = edit.length
                edit = MainActivity.zamena(edit)
                if (preLength != edit.length) {
                    editPosition = edit.length
                }
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
