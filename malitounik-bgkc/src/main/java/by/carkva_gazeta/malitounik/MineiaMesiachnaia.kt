package by.carkva_gazeta.malitounik

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.ContentMineiaBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


class MineiaMesiachnaia : BaseActivity(), DialogCaliandarMunDate.DialogCaliandarMunDateListener {
    private lateinit var binding: ContentMineiaBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: MineiaListAdapter
    private val groups = ArrayList<ArrayList<MineiaList>>()
    private var mLastClickTime: Long = 0
    private val sluzba = SlugbovyiaTextu()
    private val mineiaList = sluzba.getMineiaMesiachnaia()
    private var mun = Calendar.getInstance()[Calendar.MONTH]
    private val caliandar = Calendar.getInstance()

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun listFilter(day: Int, pasxa: Boolean): Boolean {
        for (i in 0 until groups.size) {
            for (e in 0 until groups[i].size) {
                if (groups[i][e].dayOfYear == day && groups[i][e].pasxa == pasxa) {
                    return true
                }
            }
        }
        return false
    }

    override fun setDataCalendar(dataCalendar: Int) {
        mun = dataCalendar
        loadMineia()
    }

    private fun loadMineia() {
        groups.clear()
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        var dayOfYear = 1
        var day: Int
        val c = ArrayList<String>()
        var isAdd = false
        for (i in mineiaList.indices) {
            val mineiaListDay = mineiaList.filter {
                it.day == mineiaList[i].day && it.pasxa == mineiaList[i].pasxa
            }
            if (listFilter(mineiaList[i].day, mineiaList[i].pasxa)) continue
            val listDay = ArrayList<MineiaList>()
            c.clear()
            for (e in mineiaListDay.indices) {
                val opisanie = ". " + sluzba.getNazouSluzby(mineiaListDay[e].sluzba)
                day = mineiaListDay[e].day
                when {
                    //Айцоў VII Сусьветнага Сабору
                    day == SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY -> {
                        dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY)
                    }
                    //Нядзеля праайцоў
                    day == SlugbovyiaTextu.NIADZELIA_PRA_AICOU -> {
                        dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.NIADZELIA_PRA_AICOU)
                    }
                    //Нядзеля сьвятых Айцоў першых шасьці Сабораў
                    day == SlugbovyiaTextu.NIADZELIA_AICOU_VI_SABORY -> {
                        dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.NIADZELIA_AICOU_VI_SABORY)
                    }
                    //Нядзеля прерад Раством, сьвятых Айцоў
                    day == SlugbovyiaTextu.NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU -> {
                        dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU)
                    }
                    //Субота прерад Раством
                    day == SlugbovyiaTextu.SUBOTA_PERAD_RASTVOM -> {
                        dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.SUBOTA_PERAD_RASTVOM)
                    }

                    mineiaListDay[e].pasxa -> {
                        MenuCaliandar.getDataCalaindar(year = Calendar.getInstance()[Calendar.YEAR]).forEach {
                            if (it[22].toInt() == day) {
                                dayOfYear = it[24].toInt()
                                return@forEach
                            }
                        }
                    }

                    else -> {
                        dayOfYear = day
                    }
                }
                c.addAll(MenuCaliandar.getPositionCaliandar(dayOfYear, Calendar.getInstance()[Calendar.YEAR]))
                var adminDayOfYear = ""
                if (k.getBoolean("adminDayInYear", false)) {
                    adminDayOfYear = "$dayOfYear (${c[22]}): "
                }
                val dayOfMonth = c[1].toInt()
                if (c[2].toInt() == mun) {
                    if (!isAdd && mineiaListDay[e].day == SlugbovyiaTextu.NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU) {
                        listDay.add(MineiaList("Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 18-19 сьнежня", "mm_ndz_pierad_rastvom_sviatych_ajcou_18_19_12_viaczernia", SlugbovyiaTextu.VIACZERNIA, mineiaListDay[e].pasxa, dayOfYear, dayOfMonth))
                        listDay.add(MineiaList("Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 20-23 сьнежня", "mm_ndz_pierad_rastvom_sviatych_ajcou_20_23_12_viaczernia", SlugbovyiaTextu.VIACZERNIA, mineiaListDay[e].pasxa, dayOfYear, dayOfMonth))
                        listDay.add(MineiaList("Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 24 сьнежня", "mm_ndz_pierad_rastvom_sviatych_ajcou_24_12_viaczernia", SlugbovyiaTextu.VIACZERNIA, mineiaListDay[e].pasxa, dayOfYear, dayOfMonth))
                        if (mineiaListDay[e].sluzba != SlugbovyiaTextu.VIACZERNIA) listDay.add(MineiaList(adminDayOfYear + mineiaListDay[e].title + opisanie, mineiaListDay[e].resource, mineiaListDay[e].sluzba, mineiaListDay[e].pasxa, day, dayOfMonth))
                        isAdd = true
                    } else {
                        listDay.add(MineiaList(adminDayOfYear + mineiaListDay[e].title + opisanie, mineiaListDay[e].resource, mineiaListDay[e].sluzba, mineiaListDay[e].pasxa, day, dayOfMonth))
                    }
                }
                listDay.sortWith { o1, o2 ->
                    o1.sluzba.compareTo(o2.sluzba)
                }
            }
            if (c[2].toInt() == mun) groups.add(listDay)
        }
        groups.sortWith { o1, o2 ->
            o1[0].dayOfMonth.compareTo(o2[0].dayOfMonth)
        }
        adapter.notifyDataSetChanged()
        binding.subTitleToolbar.text = resources.getStringArray(R.array.meciac3)[mun]
        if (mun == caliandar[Calendar.MONTH]) {
            for (i in groups.indices) {
                if (groups[i][0].dayOfMonth == caliandar[Calendar.DATE]) {
                    binding.elvMain.expandGroup(i)
                    binding.elvMain.setSelectedGroup(i)
                    break
                }
            }
        } else {
            for (i in groups.indices) {
                binding.elvMain.collapseGroup(i)
                binding.elvMain.post {
                    binding.elvMain.setSelectedGroup(0)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = ContentMineiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mun = savedInstanceState?.getInt("mun") ?: intent.extras?.getInt("mun") ?: Calendar.getInstance()[Calendar.MONTH]
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                intent.putExtra("resurs", groups[groupPosition][childPosition].resource)
                intent.putExtra("zmena_chastki", true)
                intent.putExtra("title", groups[groupPosition][childPosition].title)
                startActivity(intent)
            } else {
                installFullMalitounik()
            }
            false
        }
        adapter = MineiaListAdapter(this)
        binding.elvMain.setAdapter(adapter)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
        loadMineia()
        setTollbarTheme()
    }

    /*for (i in list.indices) {
            var test = false
            for (e in listResult.indices) {
                if (listResult[e].day == list[i].day) {
                    test = true
                    break
                }
            }
            if (!test) {
                listResult.add(list[i])
            }
        }*/

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(R.string.mineia_shtodzennaia)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
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

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.mineia_mesiachnaia, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_mun) {
            val dialogCaliandarMunDate = DialogCaliandarMunDate.getInstance(mun)
            dialogCaliandarMunDate.show(supportFragmentManager, "dialogCaliandarMunDate")
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("mun", mun)
    }

    private inner class MineiaListAdapter(val context: BaseActivity) : BaseExpandableListAdapter() {

        override fun getGroupCount(): Int {
            return groups.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return groups[groupPosition].size
        }

        override fun getGroup(groupPosition: Int): Any {
            return groups[groupPosition]
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return groups[groupPosition][childPosition]
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
            val rootView = GroupViewBinding.inflate(context.layoutInflater, parent, false)
            rootView.textGroup.text = groups[groupPosition][0].dayOfMonth.toString()
            if (mun == caliandar[Calendar.MONTH] && groups[groupPosition][0].dayOfMonth == caliandar[Calendar.DATE]) {
                rootView.textGroup.typeface = MainActivity.createFont(Typeface.BOLD)
            }
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(context.layoutInflater, parent, false)
            val dzenNoch = context.getBaseDzenNoch()
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition].title
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    class MineiaList(val title: String, val resource: String, val sluzba: Int, val pasxa: Boolean, val dayOfYear: Int, val dayOfMonth: Int)
}