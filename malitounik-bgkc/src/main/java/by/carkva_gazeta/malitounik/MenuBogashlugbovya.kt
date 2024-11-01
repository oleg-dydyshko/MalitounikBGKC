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

    companion object {
        fun getTextPasliaPrychascia(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.paslia_prychascia)
            val dataSearch = ArrayList<MenuListData>()
            val list = Malitounik.applicationContext().resources.getStringArray(R.array.paslia_prychascia_list)
            dataSearch.add(MenuListData(list[0] + opisanie, "paslia_prychascia1"))
            dataSearch.add(MenuListData(list[1] + opisanie, "paslia_prychascia2"))
            dataSearch.add(MenuListData(list[2] + opisanie, "paslia_prychascia3"))
            dataSearch.add(MenuListData(list[3] + opisanie, "paslia_prychascia4"))
            dataSearch.add(MenuListData(list[4] + opisanie, "paslia_prychascia5"))
            return dataSearch
        }

        fun getTextSubBogaslugbovuiaVichernia(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.vichernia)
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.sub_bogaslugbovuia_vichernia)
            dataSearch.add(MenuListData(r1[0] + opisanie, "viaczernia_niadzelnaja"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "viaczernia_liccia_i_blaslavenne_chliabou"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "viaczernia_na_kozny_dzen"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "viaczernia_u_vialikim_poscie"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "viaczerniaja_sluzba_sztodzionnaja_biez_sviatara"))
            dataSearch.add(MenuListData(r1[5] + opisanie, "viaczernia_svietly_tydzien"))
            return dataSearch
        }

        fun getTextAktoixList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.aktoix)
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.aktoix_list)
            dataSearch.add(MenuListData(r1[0] + opisanie, "viaczernia_ton1"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "viaczernia_ton2"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "viaczernia_ton3"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "viaczernia_ton4"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "viaczernia_ton5"))
            dataSearch.add(MenuListData(r1[5] + opisanie, "viaczernia_ton6"))
            dataSearch.add(MenuListData(r1[6] + opisanie, "viaczernia_ton7"))
            dataSearch.add(MenuListData(r1[7] + opisanie, "viaczernia_ton8"))
            dataSearch.add(MenuListData(r1[8] + opisanie, "viaczernia_baharodzicznyja_adpuszczalnyja"))
            return dataSearch
        }

        fun getTextTrebnikList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.trebnik_n)
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.trebnik_list)
            dataSearch.add(MenuListData(r1[0] + opisanie, "sluzba_vyzvalen_biazvinna_zniavolenych"))
            dataSearch.add(MenuListData(r1[1] + opisanie, "panichida_malaja"))
            dataSearch.add(MenuListData(r1[2] + opisanie, "czyn_asviaczennia_transpartnaha_srodku"))
            dataSearch.add(MenuListData(r1[3] + opisanie, "asviaczennie_kryza"))
            dataSearch.add(MenuListData(r1[4] + opisanie, "mltv_blaslaviennie_usialakaj_reczy"))
            dataSearch.add(MenuListData(r1[5] + opisanie, "mltv_asviacz_pamiatnaj_tablicy"))
            dataSearch.add(MenuListData(r1[6] + opisanie, "mltv_asviacz_pamiatnaj_tablicy_paciarpieu_za_bielarus1"))
            dataSearch.add(MenuListData(r1[7] + opisanie, "mltv_asviacz_pamiatnaj_tablicy_biazvinnym_achviaram_paciarpieli_za_bielarus"))
            dataSearch.add(MenuListData(r1[8] + opisanie, "sluzba_posnikam_manacham_pustelnikam"))
            dataSearch.add(MenuListData(r1[9] + opisanie, "sluzba_posniku_manachu_pustelniku"))
            return dataSearch
        }

        fun getTextTonNaKoznyDzenList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.ton_na_kozny_dzen_list)
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

        fun getTextMineiaAgulnaiaList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.mineia_agulnaia_n)
            val dataSearch = ArrayList<MenuListData>()
            val r1 = Malitounik.applicationContext().resources.getStringArray(R.array.mineia_agulnaia_list)
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
            dataSearch.add(MenuListData(r1[24] + opisanie, "sluzba_apostalu_apostalam"))
            dataSearch.add(MenuListData(r1[25] + opisanie, "sluzba_nastauniku_cark_vyznaucu"))
            dataSearch.add(MenuListData(r1[26] + opisanie, "sluzba_sviatanaczalnikam"))
            dataSearch.add(MenuListData(r1[27] + opisanie, "sluzba_sviatanaczalniku"))
            dataSearch.add(MenuListData(r1[28] + opisanie, "sluzba_najsviaciejszaj_baharodzicy"))
            dataSearch.add(MenuListData(r1[29] + opisanie, "sluzba_za_pamierlych_na_kozny_dzien_tydnia"))
            dataSearch.add(MenuListData(r1[30] + opisanie, "sluzba_bieskaryslivym_lekaram_cudatvorcam"))
            dataSearch.add(MenuListData(r1[31] + opisanie, "sluzba_muczanicam"))
            dataSearch.add(MenuListData(r1[32] + opisanie, "sluzba_muczanicy"))
            dataSearch.add(MenuListData(r1[33] + opisanie, "sluzba_muczanikam"))
            dataSearch.add(MenuListData(r1[34] + opisanie, "sluzba_muczanikam_sviataram_i_manacham"))
            dataSearch.add(MenuListData(r1[35] + opisanie, "sluzba_muczaniku"))
            dataSearch.add(MenuListData(r1[36] + opisanie, "sluzba_muczaniku_sviataru_i_manachu"))
            dataSearch.add(MenuListData(r1[37] + opisanie, "sluzba_sviatoj_zanczynie"))
            dataSearch.add(MenuListData(r1[38] + opisanie, "sluzba_sviatym_zanczynam"))
            if (!isSearch) dataSearch.add(MenuListData(r1[39], "1"))
            return dataSearch
        }

        fun getTextBogaslugbovyiaList(isSearch: Boolean = false): ArrayList<MenuListData> {
            var opisanie = ""
            if (isSearch) opisanie = Malitounik.applicationContext().getString(R.string.bogaslugbovyia)
            val dataSearch = ArrayList<MenuListData>()
            val list = Malitounik.applicationContext().resources.getStringArray(R.array.bogaslugbovyia_list)
            dataSearch.add(MenuListData(list[0] + opisanie, "lit_jana_zalatavusnaha"))
            dataSearch.add(MenuListData(list[1] + opisanie, "lit_jan_zalat_vielikodn"))
            dataSearch.add(MenuListData(list[2] + opisanie, "lit_vasila_vialikaha"))
            dataSearch.add(MenuListData(list[3] + opisanie, "lit_raniej_asviaczanych_darou"))
            dataSearch.add(MenuListData(list[4] + opisanie, "nabazenstva_maci_bozaj_niast_dap"))
            dataSearch.add(MenuListData(list[5] + opisanie, "jutran_niadzelnaja"))
            dataSearch.add(MenuListData(list[6] + opisanie, "abiednica"))
            dataSearch.add(MenuListData(list[7] + opisanie, "kanon_malebny_baharodzicy"))
            dataSearch.add(MenuListData(list[8] + opisanie, "kanon_andreja_kryckaha"))
            dataSearch.add(MenuListData(list[9] + opisanie, "malebien_kiryla_miatod"))
            dataSearch.add(MenuListData(list[10] + opisanie, "paviaczernica_malaja"))
            dataSearch.add(MenuListData(list[11] + opisanie, "kanon_andreja_kryckaha_4_czastki"))
            dataSearch.sort()
            return dataSearch
        }

        private fun getTextBogaslugbovyiaFolderList(): ArrayList<MenuListData> {
            val dataSearch = ArrayList<MenuListData>()
            val list = Malitounik.applicationContext().resources.getStringArray(R.array.bogaslugbovyia_folder_list)
            for (i in list.indices) {
                dataSearch.add(MenuListData(list[i], (i + 1).toString()))
            }
            dataSearch.sort()
            return dataSearch
        }

        fun getBogaslugbovyiaSearchText(isMenu: Boolean = true): ArrayList<MenuListData> {
            val dataSearch = ArrayList<MenuListData>()
            dataSearch.addAll(getTextBogaslugbovyiaList(isMenu))
            dataSearch.addAll(getTextTrebnikList(isMenu))
            dataSearch.addAll(getTextPasliaPrychascia(isMenu))
            dataSearch.addAll(getTextSubBogaslugbovuiaVichernia(isMenu))
            dataSearch.addAll(getTextAktoixList(isMenu))
            dataSearch.addAll(getTextMineiaAgulnaiaList(isMenu))
            dataSearch.addAll(getTextTonNaKoznyDzenList(isMenu))
            var opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.ton_na_niadzelu)
            else ""
            for (i in 1..8) {
                dataSearch.add(MenuListData(Malitounik.applicationContext().getString(R.string.ton, i.toString() + opisanie), "ton$i"))
            }
            val sluzba = SlugbovyiaTextu()
            var mesiach = sluzba.getMineiaMesiachnaia()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.mineia_mesiachnaia)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getVilikiTydzen()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_1)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getSvetlyTydzen()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_2)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getMineiaSviatochnaia()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_3)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getTydzen1()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_4)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getTydzen2()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_5)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getTydzen3()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_6)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getTydzen4()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_7)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getTydzen5()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_8)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            mesiach = sluzba.getTydzen6()
            opisanie = if (isMenu) Malitounik.applicationContext().getString(R.string.tryedz_9)
            else ""
            for (i in mesiach.indices) {
                dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba) + opisanie, mesiach[i].resource))
            }
            dataSearch.sort()
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
                dataSearch.addAll(getBogaslugbovyiaSearchText())
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
                actionExpandOn = true
                dataSearch.clear()
                dataSearch.addAll(getBogaslugbovyiaSearchText())
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
                "8" -> {
                    val intent = Intent(it, Trebnik::class.java)
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
