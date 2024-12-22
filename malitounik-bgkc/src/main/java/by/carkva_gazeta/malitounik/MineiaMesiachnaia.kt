package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ContentMineiaBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemMineiaBinding
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
    private val groups = ArrayList<MineiaList>()
    private var mLastClickTime: Long = 0
    private val sluzba = SlugbovyiaTextu()
    private val mineiaList = sluzba.getMineiaMesiachnaia()
    private var mun = Calendar.getInstance()[Calendar.MONTH]

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun listFilter(day: Int, pasxa: Boolean): Boolean {
        for (i in groups.indices) {
            if (groups[i].dayOfYear == day && groups[i].pasxa == pasxa) return true
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
        for (i in mineiaList.indices) {
            val mineiaListDay = mineiaList.filter {
                it.day == mineiaList[i].day && it.pasxa == mineiaList[i].pasxa
            }
            if (listFilter(mineiaList[i].day, mineiaList[i].pasxa)) continue
            var opisanie = ""
            if (mineiaListDay.size == 1) {
                opisanie = ". " + sluzba.getNazouSluzby(mineiaListDay[0].sluzba)
            }
            day = mineiaListDay[0].day
            when {
                //Айцоў VII Сусьветнага Сабору
                mineiaListDay[0].day == SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY -> {
                    dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY)
                }
                //Нядзеля праайцоў
                mineiaListDay[0].day == SlugbovyiaTextu.NIADZELIA_PRA_AICOU -> {
                    dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.NIADZELIA_PRA_AICOU)
                }
                //Нядзеля сьвятых Айцоў першых шасьці Сабораў
                mineiaListDay[0].day == SlugbovyiaTextu.NIADZELIA_AICOU_VI_SABORY -> {
                    dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.NIADZELIA_AICOU_VI_SABORY)
                }
                //Нядзеля прерад Раством, сьвятых Айцоў
                mineiaListDay[0].day == SlugbovyiaTextu.NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU -> {
                    dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU)
                }
                //Субота прерад Раством
                mineiaListDay[0].day == SlugbovyiaTextu.SUBOTA_PERAD_RASTVOM -> {
                    dayOfYear = sluzba.getRealDay(SlugbovyiaTextu.SUBOTA_PERAD_RASTVOM)
                }

                mineiaListDay[0].pasxa -> {
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
            val c = MenuCaliandar.getPositionCaliandar(dayOfYear, Calendar.getInstance()[Calendar.YEAR])
            var adminDayOfYear = ""
            if (k.getBoolean("adminDayInYear", false)) {
                adminDayOfYear = " ($dayOfYear (${c[22]}))"
            }
            val dayOfMonth = c[1].toInt()
            if (c[2].toInt() == mun) groups.add(MineiaList(adminDayOfYear + mineiaListDay[0].title + opisanie, mineiaListDay[0].resource, mineiaListDay[0].sluzba, mineiaListDay[0].pasxa, day, dayOfMonth))
        }
        sortedMineia = SORTED_DATA
        groups.sort()
        adapter.notifyDataSetChanged()
        binding.subTitleToolbar.text = resources.getStringArray(R.array.meciac3)[mun]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = ContentMineiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mun = intent.extras?.getInt("mun") ?: Calendar.getInstance()[Calendar.MONTH]
        binding.elvMain.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val mineiaListDay = mineiaList.filter {
                it.day == groups[position].dayOfYear && it.pasxa == groups[position].pasxa
            }
            if (mineiaListDay.size > 1) {
                val dialog = DialogMineiaList.getInstance(groups[position].dayOfYear, groups[position].dayOfMonth, groups[position].pasxa)
                dialog.show(supportFragmentManager, "dialogMineiaList")
            } else {
                if (checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("resurs", groups[position].resource)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", groups[position].title)
                    startActivity(intent)
                } else {
                    installFullMalitounik()
                }
            }
        }
        adapter = MineiaListAdapter(this, groups)
        binding.elvMain.adapter = adapter
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

    fun getMineiaListDialog(dayOfYear: Int, dayOfMonth: Int, pasxa: Boolean): ArrayList<MineiaList> {
        val minList = mineiaList.filter {
            it.day == dayOfYear && it.pasxa == pasxa
        }
        val mineiaListDialog = ArrayList<MineiaList>()
        if (dayOfYear == SlugbovyiaTextu.NIADZELIA_PERAD_RASTVOM_SVIATYCH_AJCOU) {
            mineiaListDialog.add(MineiaList("Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 18-19 сьнежня", "mm_ndz_pierad_rastvom_sviatych_ajcou_18_19_12_viaczernia", SlugbovyiaTextu.VIACZERNIA, pasxa, dayOfYear, dayOfMonth))
            mineiaListDialog.add(MineiaList("Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 20-23 сьнежня", "mm_ndz_pierad_rastvom_sviatych_ajcou_20_23_12_viaczernia", SlugbovyiaTextu.VIACZERNIA, pasxa, dayOfYear, dayOfMonth))
            mineiaListDialog.add(MineiaList("Нядзеля перад Нараджэньнем Госпада нашага Ісуса Хрыста – Нядзеля сьвятых айцоў, калі 24 сьнежня", "mm_ndz_pierad_rastvom_sviatych_ajcou_24_12_viaczernia", SlugbovyiaTextu.VIACZERNIA, pasxa, dayOfYear, dayOfMonth))
            for (i in minList.indices) {
                if (minList[i].sluzba != SlugbovyiaTextu.VIACZERNIA) mineiaListDialog.add(MineiaList(minList[i].title, minList[i].resource, minList[i].sluzba, pasxa, dayOfYear, dayOfMonth))
            }
        } else {
            for (i in minList.indices) {
                mineiaListDialog.add(MineiaList(minList[i].title, minList[i].resource, minList[i].sluzba, pasxa, dayOfYear, dayOfMonth))
            }
        }
        return mineiaListDialog
    }

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

    private class MineiaListAdapter(val context: BaseActivity, val mineiaList: ArrayList<MineiaList>) : ArrayAdapter<MineiaList>(context, R.layout.simple_list_item_mineia, R.id.label, mineiaList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItemMineiaBinding.inflate(context.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.date, binding.title)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = context.getBaseDzenNoch()
            viewHolder.title.text = mineiaList[position].title
            viewHolder.date.text = mineiaList[position].dayOfMonth.toString()
            if (dzenNoch) viewHolder.date.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary_black))
            return rootView
        }
    }

    private class ViewHolder(var date: TextView, var title: TextView)

    class MineiaList(val title: String, val resource: String, val sluzba: Int, val pasxa: Boolean, val dayOfYear: Int,  val dayOfMonth: Int) : Comparable<MineiaList> {
        override fun compareTo(other: MineiaList): Int {
            if (sortedMineia == SORTED_SLUZBA) {
                if (this.sluzba > other.sluzba) {
                    return 1
                } else if (this.sluzba < other.sluzba) {
                    return -1
                }
            } else {
                if (this.dayOfMonth > other.dayOfMonth) {
                    return 1
                } else if (this.dayOfMonth < other.dayOfMonth) {
                    return -1
                }
            }
            return 0
        }
    }

    companion object {
        const val SORTED_SLUZBA = 1
        const val SORTED_DATA = 2
        var sortedMineia = SORTED_SLUZBA
    }
}