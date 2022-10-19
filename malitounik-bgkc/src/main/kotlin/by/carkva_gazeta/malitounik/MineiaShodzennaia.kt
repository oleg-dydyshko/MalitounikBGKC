package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import kotlinx.coroutines.*
import java.util.*


class MineiaShodzennaia : BaseActivity() {
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: MineiaExpListAdapter
    private val groups = ArrayList<ArrayList<MineiaDay>>()
    private var mLastClickTime: Long = 0

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dzenNoch = getBaseDzenNoch()
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTollbarTheme()
        val slugba = SlugbovyiaTextu()
        val mineiaList = slugba.getMineiaShtodzennia()
        val c = GregorianCalendar()
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
        var day = 0
        for (i in 0 until mineiaList.size) {
            if (day == mineiaList[i].day) {
                day = mineiaList[i].day
                continue
            } else {
                day = mineiaList[i].day
            }
            when {
                //Айцоў VII Сусьветнага Сабору
                mineiaList[i].day == SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY -> {
                    c.set(Calendar.DAY_OF_YEAR, SlugbovyiaTextu().getRealDay(SlugbovyiaTextu.AICOU_VII_SUSVETNAGA_SABORY))
                }
                mineiaList[i].pasxa -> {
                    MenuCaliandar.getDataCalaindar(year = c[Calendar.YEAR]).forEach {
                        if (it[22].toInt() == day) {
                            c.set(Calendar.DAY_OF_YEAR, it[24].toInt())
                            return@forEach
                        }
                    }
                }
                else -> {
                    c.set(Calendar.DAY_OF_YEAR, day)
                }
            }
            //c.set(Calendar.DAY_OF_YEAR, day)
            val id = c.timeInMillis
            //val positionCaliandar = MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[24].toInt()
            //day = mineiaList[i].day
            when (c[Calendar.MONTH]) {
                Calendar.JANUARY -> child0.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.FEBRUARY -> child1.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.MARCH -> child2.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.APRIL -> child3.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.MAY -> child4.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.JUNE -> child5.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.JULY -> child6.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.AUGUST -> child7.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.SEPTEMBER -> child8.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.OCTOBER -> child9.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.NOVEMBER -> child10.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
                Calendar.DECEMBER -> child11.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title, mineiaList[i].title, slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA), slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)))
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
            if (groups[groupPosition][childPosition].titleResource == "") {
                val intent = Intent(this, Opisanie::class.java)
                intent.putExtra("mun", groups[groupPosition][childPosition].month + 1)
                intent.putExtra("day", groups[groupPosition][childPosition].day.toInt())
                intent.putExtra("year", c[Calendar.YEAR])
                startActivity(intent)
            } else {
                var count = 0
                if (resourceUtran != "0") count++
                if (resourceLiturgia != "0") count++
                if (resourceViachernia != "0") count++
                if (resourceAbednica != "0") count++
                if (resourceVialikiaGadziny != "0") count++
                if (count > 1) {
                    val resourceArrayList = ArrayList<String>()
                    for (i in 0 until mineiaList.size) {
                        if (groups[groupPosition][childPosition].day.toInt() == mineiaList[i].day) {
                            when (mineiaList[i].sluzba) {
                                SlugbovyiaTextu.UTRAN -> resourceArrayList.add(mineiaList[i].resource)
                                SlugbovyiaTextu.LITURGIA -> resourceArrayList.add(mineiaList[i].resource)
                                SlugbovyiaTextu.VIACHERNIA -> resourceArrayList.add(mineiaList[i].resource)
                            }
                        }
                    }
                    val dialog = DialogMineiaList.getInstance(groups[groupPosition][childPosition].day, groups[groupPosition][childPosition].titleResource, resourceUtran, resourceLiturgia, resourceViachernia, resourceAbednica, resourceVialikiaGadziny, false)
                    dialog.show(supportFragmentManager, "dialogMineiaList")
                } else {
                    if (MainActivity.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                        if (resourceUtran != "0") intent.putExtra("resurs", resourceUtran)
                        if (resourceLiturgia != "0") intent.putExtra("resurs", resourceLiturgia)
                        if (resourceViachernia != "0") intent.putExtra("resurs", resourceViachernia)
                        intent.putExtra("zmena_chastki", true)
                        intent.putExtra("title", groups[groupPosition][childPosition].titleResource)
                        startActivity(intent)
                    } else {
                        val dadatak = DialogInstallDadatak()
                        dadatak.show(supportFragmentManager, "dadatak")
                    }
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
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
            }
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        month = null
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    companion object {
        private var month: Int? = null
    }
}