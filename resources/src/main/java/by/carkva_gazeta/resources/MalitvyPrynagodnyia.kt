package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogClearHishory
import by.carkva_gazeta.malitounik.HistoryAdapter
import by.carkva_gazeta.malitounik.MenuListData
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.resources.databinding.MalitvyPrynagodnyiaBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MalitvyPrynagodnyia : BaseActivity(), DialogClearHishory.DialogClearHistoryListener {

    private val data = ArrayList<ArrayList<MenuListData>>()
    private val rub1 = ArrayList<MenuListData>()
    private val rub2 = ArrayList<MenuListData>()
    private val rub3 = ArrayList<MenuListData>()
    private val rub4 = ArrayList<MenuListData>()
    private val rub5 = ArrayList<MenuListData>()
    private val rub6 = ArrayList<MenuListData>()
    private lateinit var adapter: PrynagodnyiaAdaprer
    private var searchView: SearchView? = null
    private lateinit var chin: SharedPreferences
    private var mLastClickTime: Long = 0
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var searchViewQwery = ""
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var searchAdapter: SearchAdaprer
    private var actionExpandOn = false
    private lateinit var binding: MalitvyPrynagodnyiaBinding
    private var resetTollbarJob: Job? = null

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
            if (history.size == 10) break
        }
    }

    private fun saveHistopy() {
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(history, type)
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
        if (history.size == 0) invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    private fun findTypeResource(findText: String): String {
        var type = ""
        for (i in 0 until data.size) {
            for (e in 0 until data[i].size) {
                if (data[i][e].title == findText) {
                    type = data[i][e].resurs
                    break
                }
            }
        }
        return type
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", MODE_PRIVATE)
        val dzenNoch = getBaseDzenNoch()
        binding = MalitvyPrynagodnyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState != null) {
            searchViewQwery = savedInstanceState.getString("SearchViewQwery", "")
            actionExpandOn = savedInstanceState.getBoolean("actionExpandOn")
        }
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
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        rub1.add(MenuListData("Малітва да Маці Божай Браслаўскай, Валадаркі Азёраў", "prynagodnyia_7"))
        rub1.add(MenuListData("Малітва да Маці Божай Будслаўскай, Апякункі Беларусі", "prynagodnyia_8"))
        rub1.add(MenuListData("Малітва да Маці Божай Нястомнай Дапамогі", "prynagodnyia_9"))
        rub1.add(MenuListData("Малітва да Маці Божай Берасьцейскай", "prynagodnyia_30"))
        rub1.add(MenuListData("Малітва да Маці Божай Лагішынскай", "prynagodnyia_31"))
        rub1.add(MenuListData("Малітва да Маці Божай Будслаўскай", "mltv_mb_budslauskaja"))
        rub1.add(MenuListData("Малітва да Божае Маці перад іконай Ейнай Менскай", "mltv_mb_mienskaja"))
        rub1.add(MenuListData("Малітва да Найсьвяцейшай Дзевы Марыі Барунскай", "mltv_mb_barunskaja"))
        rub1.add(MenuListData("Малітва да Багародзіцы, праслаўленай у цудатворнай Жыровіцкай іконе", "mltv_mb_zyrovickaja"))
        rub1.sort()
        rub2.add(MenuListData("Малітва аб дапамозе ў выбары жыцьцёвай дарогі дзіцяці", "prynagodnyia_1"))
        rub2.add(MenuListData("Малітва бацькоў за дзяцей («Божа, у Тройцы Адзіны...»)", "mltv_backou_za_dziaciej_boza_u_trojcy_adziny"))
        rub2.add(MenuListData("Малітва бацькоў за дзяцей", "prynagodnyia_4"))
        rub2.add(MenuListData("Малітва за дарослых дзяцей", "prynagodnyia_11"))
        rub2.add(MenuListData("Малітва за бацькоў", "mltv_za_backou"))
        rub2.add(MenuListData("Малітва за хворае дзіця", "prynagodnyia_15"))
        rub2.add(MenuListData("Малітва сям’і аб Божым бласлаўленьні на час адпачынку і вакацыяў", "prynagodnyia_33"))
        rub2.add(MenuListData("Блаславеньне маці (Матчына малітва)", "prynagodnyia_40"))
        rub2.add(MenuListData("Малітва за хросьнікаў", "mltv_za_chrosnikau"))
        rub2.add(MenuListData("Малітва да сьв. Язэпа", "prynagodnyia_37"))
        rub2.add(MenuListData("Малітва мужа і бацькі да сьв. Язэпа", "prynagodnyia_38"))
        rub2.add(MenuListData("Малітва да сьв. Язэпа за мужчынаў", "prynagodnyia_39"))
        rub2.sort()
        rub3.add(MenuListData("Малітва за Беларусь", "prynagodnyia_10"))
        rub3.add(MenuListData("Малітва за Айчыну - Ян Павел II", "prynagodnyia_36"))
        rub3.add(MenuListData("Малітва за ўсіх, што пацярпелі за Беларусь", "mltv_paciarpieli_za_bielarus"))
        rub3.sort()
        rub4.add(MenuListData("Малітва аб еднасьці", "mltv_ab_jednasci"))
        rub4.add(MenuListData("Малітва за парафію", "prynagodnyia_13"))
        rub4.add(MenuListData("Малітва за хрысьціянскую еднасьць", "prynagodnyia_16"))
        rub4.add(MenuListData("Малітвы за сьвятароў і сьвятарскія пакліканьні", "prynagodnyia_24"))
        rub4.add(MenuListData("Цябе, Бога, хвалім", "pesny_prasl_70"))
        rub4.add(MenuListData("Малітва за Царкву", "mltv_za_carkvu"))
        rub4.add(MenuListData("Малітва за Царкву 2", "mltv_za_carkvu_2"))
        rub4.add(MenuListData("Малітва за царкоўную еднасьць", "mltv_za_carkounuju_jednasc"))
        rub4.add(MenuListData("Малітва разам з Падляшскімі мучанікамі аб еднасьці", "mltv_razam_z_padlaszskimi_muczanikami_ab_jednasci"))
        rub4.add(MenuListData("Малітва аб еднасьці царквы (Экзарха Леаніда Фёдарава)", "mltv_ab_jednasci_carkvy_leanida_fiodarava"))
        rub4.add(MenuListData("Малітва за нашую зямлю", "mltv_za_naszuju_ziamlu"))
        rub4.sort()
        rub5.add(MenuListData("Малітва за хворага («Міласэрны Божа»)", "mltv_za_chvoraha_milaserny_boza"))
        rub5.add(MenuListData("Малітва за хворага («Лекару душ і целаў»)", "mltv_za_chvoraha_lekaru_dush_cielau"))
        rub5.add(MenuListData("Малітва ў часе хваробы", "mltv_u_czasie_chvaroby"))
        rub5.add(MenuListData("Малітва падчас згубнай пошасьці", "prynagodnyia_28"))
        rub6.add(MenuListData("Малітва перад пачаткам навучаньня", "prynagodnyia_21"))
        rub6.add(MenuListData("Малітва за дзяцей перад пачаткам навукі", "prynagodnyia_12"))
        rub6.add(MenuListData("Малітва вучняў перад навучаньнем", "prynagodnyia_29"))
        rub6.add(MenuListData("Малітва вучня", "prynagodnyia_6"))
        rub6.add(MenuListData("Малітвы за памерлых", "mltv_za_pamierlych"))
        rub6.add(MenuListData("Намер ісьці за Хрыстом", "prynagodnyia_26"))
        rub6.add(MenuListData("Малітва пілігрыма", "prynagodnyia_32"))
        rub6.add(MenuListData("Малітва да ўкрыжаванага Хрыста (Францішак Скарына)", "mltv_da_ukryzavanaha_chrysta_skaryna"))
        rub6.add(MenuListData("Малітва аб блаславеньні", "prynagodnyia_0"))
        rub6.add(MenuListData("Малітва кіроўцы", "mltv_kiroucy"))
        rub6.add(MenuListData("Малітва за ўмацаваньне ў любові", "prynagodnyia_17"))
        rub6.add(MenuListData("Малітва маладога чалавека", "prynagodnyia_18"))
        rub6.add(MenuListData("Малітва на ўсякую патрэбу", "prynagodnyia_19"))
        rub6.add(MenuListData("Малітва падзякі за атрыманыя дабрадзействы", "prynagodnyia_20"))
        rub6.add(MenuListData("Малітва перад іспытамі", "prynagodnyia_22"))
        rub6.add(MenuListData("Малітва ранішняга намеру (Опціных старцаў)", "prynagodnyia_23"))
        rub6.add(MenuListData("Малітва ў час адпачынку", "prynagodnyia_34"))
        rub6.add(MenuListData("Малітва за бязьвінных ахвяраў перасьледу", "prynagodnyia_35"))
        rub6.add(MenuListData("Малітвы перад ядою і пасьля яды", "mltv_pierad_jadoj_i_pasla"))
        rub6.add(MenuListData("Малітва за ўсіх і за ўсё", "mltv_za_usich_i_za_usio"))
        rub6.add(MenuListData("Малітва за вязьняў", "mltv_za_viazniau"))
        rub6.add(MenuListData("Малітва перад пачаткам і пасьля кожнай справы", "mltv_pierad_i_pasla_koznaj_spravy"))
        rub6.add(MenuListData("Малітва ў дзень нараджэньня", "mltv_dzien_naradzennia"))
        rub6.add(MenuListData("Малітва аб духу любові", "mltv_ab_duchu_lubovi_sv_franciszak"))
        rub6.sort()
        data.add(rub1)
        data.add(rub2)
        data.add(rub3)
        data.add(rub4)
        data.add(rub5)
        data.add(rub6)
        adapter = PrynagodnyiaAdaprer()
        binding.ExpandableListView.setAdapter(adapter)
        val serchData = ArrayList<MenuListData>()
        serchData.addAll(rub1)
        serchData.addAll(rub2)
        serchData.addAll(rub3)
        serchData.addAll(rub4)
        serchData.addAll(rub5)
        serchData.addAll(rub6)
        serchData.sort()
        searchAdapter = SearchAdaprer(this, serchData)
        binding.SearchView.adapter = searchAdapter
        binding.titleToolbar.text = resources.getText(R.string.prynagodnyia)
        if (dzenNoch) binding.ExpandableListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else binding.ExpandableListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        binding.ExpandableListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (firstVisibleItem == 1) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(binding.ExpandableListView.windowToken, 0)
                    searchView?.clearFocus()
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }

        })
        binding.ExpandableListView.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this@MalitvyPrynagodnyia, Bogashlugbovya::class.java)
            intent.putExtra("title", data[groupPosition][childPosition].title)
            intent.putExtra("resurs", data[groupPosition][childPosition].resurs)
            startActivity(intent)
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.ExpandableListView.windowToken, 0)
            if (autoCompleteTextView?.text.toString() != "") {
                addHistory(data[groupPosition][childPosition].title)
                saveHistopy()
            }
            actionExpandOn = false
            return@setOnChildClickListener false
        }
        if (chin.getString("history_prynagodnyia", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_prynagodnyia", "")
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
            history.addAll(gson.fromJson(json, type))
        }
        historyAdapter = HistoryAdapter(this, history)
        if (dzenNoch) binding.History.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else binding.History.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        binding.History.adapter = historyAdapter
        binding.History.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this@MalitvyPrynagodnyia, Bogashlugbovya::class.java)
            val edit = history[position]
            intent.putExtra("title", edit)
            intent.putExtra("resurs", findTypeResource(edit))
            startActivity(intent)
            addHistory(edit)
            saveHistopy()
            actionExpandOn = false
        }
        binding.History.setOnItemLongClickListener { _, _, position, _ ->
            val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return@setOnItemLongClickListener true
        }
        binding.SearchView.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this@MalitvyPrynagodnyia, Bogashlugbovya::class.java)
            intent.putExtra("title", serchData[position].title)
            intent.putExtra("resurs", serchData[position].resurs)
            startActivity(intent)
            addHistory(serchData[position].title)
            saveHistopy()
            actionExpandOn = false
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

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun changeSearchViewElements(view: View?) {
        if (view == null) return
        if (view.id == androidx.appcompat.R.id.search_edit_frame || view.id == androidx.appcompat.R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == androidx.appcompat.R.id.search_src_text) {
            autoCompleteTextView = view as AutoCompleteTextView
            val p = view.layoutParams as LinearLayout.LayoutParams
            val density = resources.displayMetrics.density
            val margin = (10 * density).toInt()
            p.rightMargin = margin
            autoCompleteTextView?.layoutParams = p
            autoCompleteTextView?.setBackgroundResource(R.drawable.underline_white)
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher())
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val histopy = menu.findItem(R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.malitvy_prynagodnyia, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(R.id.action_seashe_text)
        searchView = searchViewItem.actionView as SearchView
        if (actionExpandOn) {
            searchViewItem.expandActionView()
            binding.History.visibility = View.VISIBLE
            binding.SearchView.visibility = View.GONE
            binding.ExpandableListView.visibility = View.GONE
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                actionExpandOn = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                actionExpandOn = false
                return true
            }
        })
        searchView?.queryHint = getString(R.string.search_malitv)
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            searchViewItem.expandActionView()
            autoCompleteTextView?.setText(searchViewQwery)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SearchViewQwery", autoCompleteTextView?.text.toString())
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
                    autoCompleteTextView?.removeTextChangedListener(this)
                    autoCompleteTextView?.setText(edit)
                    autoCompleteTextView?.setSelection(editPosition)
                    autoCompleteTextView?.addTextChangedListener(this)
                }
            }
            if (actionExpandOn) {
                if (editPosition == 0) {
                    binding.History.visibility = View.VISIBLE
                    binding.ExpandableListView.visibility = View.GONE
                    binding.SearchView.visibility = View.GONE
                } else {
                    binding.History.visibility = View.GONE
                    binding.ExpandableListView.visibility = View.GONE
                    binding.SearchView.visibility = View.VISIBLE
                }
            } else {
                binding.History.visibility = View.GONE
                binding.ExpandableListView.visibility = View.VISIBLE
                binding.SearchView.visibility = View.GONE
            }
            searchAdapter.filter.filter(edit)
        }
    }

    private inner class PrynagodnyiaAdaprer : BaseExpandableListAdapter() {

        override fun getGroupCount(): Int {
            return data.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return data[groupPosition].size
        }

        override fun getGroup(groupPosition: Int): Any {
            return data[groupPosition]
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return data[groupPosition][childPosition]
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = GroupViewBinding.inflate(layoutInflater, parent, false)
            when (groupPosition) {
                0 -> rootView.textGroup.text = getString(R.string.prynad_1)
                1 -> rootView.textGroup.text = getString(R.string.prynad_2)
                2 -> rootView.textGroup.text = getString(R.string.prynad_3)
                3 -> rootView.textGroup.text = getString(R.string.prynad_4)
                4 -> rootView.textGroup.text = getString(R.string.prynad_5)
                5 -> rootView.textGroup.text = getString(R.string.prynad_6)
            }
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(layoutInflater, parent, false)
            val dzenNoch = getBaseDzenNoch()
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = data[groupPosition][childPosition].title
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    private class SearchAdaprer(private val context: Activity, private val data: List<MenuListData>) : ArrayAdapter<MenuListData>(context, R.layout.simple_list_item_2, R.id.label, data) {
        private val origData = ArrayList<MenuListData>(data)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(context.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
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
                        for (item in origData) {
                            if (item.title.contains(constraint1, true)) {
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

    private class ViewHolder(var text: TextView)
}
