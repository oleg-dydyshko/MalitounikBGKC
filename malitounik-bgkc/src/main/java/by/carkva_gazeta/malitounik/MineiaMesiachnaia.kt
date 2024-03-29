package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


class MineiaMesiachnaia : BaseActivity() {
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: MineiaExpListAdapter
    private val groups = ArrayList<ArrayList<MineiaDay>>()
    private var mLastClickTime: Long = 0

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun getMineiaDayCount(list: List<SlugbovyiaTextuData>, day: Int, slugba: Int, pasxa: Boolean): Int {
        var count = 0
        var errorCount = 0
        for (i in list.indices) {
            if (day == list[i].day && slugba == list[i].sluzba && pasxa == list[i].pasxa) {
                errorCount++
            }
            if (day == list[i].day && pasxa == list[i].pasxa) {
                count++
            }
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getBoolean("admin", false) && errorCount > 1) MainActivity.toastView(this, getString(R.string.admin_resourse_error, day), Toast.LENGTH_LONG)
        return count
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val k = getSharedPreferences("biblia", MODE_PRIVATE)
        val dzenNoch = getBaseDzenNoch()
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTollbarTheme()
        val slugba = SlugbovyiaTextu()
        val mineiaList = slugba.getMineiaMesiachnaia()
        val calendar = Calendar.getInstance()
        var dayOfYear = 1
        val child0 = ArrayList<MineiaDay>()
        val child1 = ArrayList<MineiaDay>()
        val child2 = ArrayList<MineiaDay>()
        val child3 = ArrayList<MineiaDay>()
        val child4 = ArrayList<MineiaDay>()
        val child5 = ArrayList<MineiaDay>()
        val child6 = ArrayList<MineiaDay>()
        val child7 = ArrayList<MineiaDay>()
        val child8 = ArrayList<MineiaDay>()
        val child9 = ArrayList<MineiaDay>()
        val child10 = ArrayList<MineiaDay>()
        val child11 = ArrayList<MineiaDay>()
        var day = -777
        var pasxa = false
        for (i in mineiaList.indices) {
            val count = getMineiaDayCount(mineiaList, mineiaList[i].day, mineiaList[i].sluzba, mineiaList[i].pasxa)
            var opisanie = ""
            if (count == 1) {
                opisanie = ". " + slugba.getNazouSluzby(mineiaList[i].sluzba)
            }
            if (day == mineiaList[i].day && pasxa == mineiaList[i].pasxa) {
                day = mineiaList[i].day
                pasxa = mineiaList[i].pasxa
                continue
            } else {
                day = mineiaList[i].day
                pasxa = mineiaList[i].pasxa
            }
            when {
                //Айцоў VII Сусьветнага Сабору
                mineiaList[i].day == SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY -> {
                    dayOfYear = SlugbovyiaTextu().getRealDay(SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY)
                }
                //Нядзеля праайцоў
                mineiaList[i].day == SlugbovyiaTextu.NIADZELIA_PRA_AICOU -> {
                    dayOfYear = SlugbovyiaTextu().getRealDay(SlugbovyiaTextu.NIADZELIA_PRA_AICOU)
                }
                //Нядзеля сьвятых Айцоў першых шасьці Сабораў
                mineiaList[i].day == SlugbovyiaTextu.NIADZELIA_AICOU_VI_SABORY -> {
                    dayOfYear = SlugbovyiaTextu().getRealDay(SlugbovyiaTextu.NIADZELIA_AICOU_VI_SABORY)
                }

                mineiaList[i].pasxa -> {
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
            val c = MenuCaliandar.getPositionCaliandar(dayOfYear, calendar[Calendar.YEAR])
            val id = dayOfYear.toLong()
            var adminDayOfYear = ""
            if (k.getBoolean("adminDayInYear", false)) {
                adminDayOfYear = " ($dayOfYear (${c[22]}))"
            }
            val dayOfMonth = c[1]

            Log.d("Oleg", "$id, $month, $day, $dayOfMonth, " + mineiaList[i].title)

            when (val month = c[2].toInt()) {
                Calendar.JANUARY -> child0.add(MineiaDay(id, mineiaList[i].pasxa, month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.FEBRUARY -> child1.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.MARCH -> child2.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.APRIL -> child3.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.MAY -> child4.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.JUNE -> child5.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.JULY -> child6.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.AUGUST -> child7.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.SEPTEMBER -> child8.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.OCTOBER -> child9.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.NOVEMBER -> child10.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
                Calendar.DECEMBER -> child11.add(MineiaDay(id, mineiaList[i].pasxa,month, day.toString(), dayOfMonth + " " + resources.getStringArray(R.array.meciac_smoll)[month] + "$adminDayOfYear: " + mineiaList[i].title + opisanie, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.JUTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURHIJA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABIEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALHADZINY), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACZERNIA_Z_LITURHIJA)))
            }
        }
        child0.sort()
        child1.sort()
        child2.sort()
        child3.sort()
        child4.sort()
        child5.sort()
        child6.sort()
        child7.sort()
        child8.sort()
        child9.sort()
        child10.sort()
        child11.sort()
        if (child0.size != 0) groups.add(child0)
        if (child1.size != 0) groups.add(child1)
        if (child2.size != 0) groups.add(child2)
        if (child3.size != 0) groups.add(child3)
        if (child4.size != 0) groups.add(child4)
        if (child5.size != 0) groups.add(child5)
        if (child6.size != 0) groups.add(child6)
        if (child7.size != 0) groups.add(child7)
        if (child8.size != 0) groups.add(child8)
        if (child9.size != 0) groups.add(child9)
        if (child10.size != 0) groups.add(child10)
        if (child11.size != 0) groups.add(child11)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener false
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val resourceUtran = groups[groupPosition][childPosition].resourceUtran
            val resourceLiturgia = groups[groupPosition][childPosition].resourceLiturgia
            val resourceViachernia = groups[groupPosition][childPosition].resourceViachernia
            val resourceAbednica = groups[groupPosition][childPosition].resourceAbednica
            val resourceVialikiaGadziny = groups[groupPosition][childPosition].resourceVialikiaGadziny
            val resourceViacherniaZLiturgia = groups[groupPosition][childPosition].resourceViacherniaZLiturgia
            var count = 0
            if (resourceUtran != "0") count++
            if (resourceLiturgia != "0") count++
            if (resourceViachernia != "0") count++
            if (resourceAbednica != "0") count++
            if (resourceVialikiaGadziny != "0") count++
            if (resourceViacherniaZLiturgia != "0") count++
            if (count > 1) {
                val dialog = DialogMineiaList.getInstance(groups[groupPosition][childPosition].day, groups[groupPosition][childPosition].pasxa)
                dialog.show(supportFragmentManager, "dialogMineiaList")
            } else {
                if (checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                    if (resourceUtran != "0") intent.putExtra("resurs", resourceUtran)
                    if (resourceLiturgia != "0") intent.putExtra("resurs", resourceLiturgia)
                    if (resourceViachernia != "0") intent.putExtra("resurs", resourceViachernia)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", groups[groupPosition][childPosition].titleResource)
                    startActivity(intent)
                } else {
                    installFullMalitounik()
                }
            }
            month = groupPosition
            false
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = MineiaExpListAdapter(this, groups)
        binding.elvMain.setAdapter(adapter)
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

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        val cal = Calendar.getInstance()
        if (month == null) {
            for (i in 0 until groups.size) {
                for (e in 0 until groups[i].size) {
                    if (cal[Calendar.MONTH] == groups[i][e].month) month = i
                }
            }
        }
        binding.elvMain.expandGroup(month ?: 0)
        binding.elvMain.setSelectedGroup(month ?: 0)
    }

    override fun onBack() {
        super.onBack()
        month = null
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    companion object {
        private var month: Int? = null
    }
}