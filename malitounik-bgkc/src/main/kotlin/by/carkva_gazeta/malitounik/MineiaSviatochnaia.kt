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
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.*
import java.util.*


class MineiaSviatochnaia : BaseActivity() {
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: MineiaExpListAdapter
    private val groups = ArrayList<ArrayList<MineiaDay>>()
    private var mLastClickTime: Long = 0
    private var dzenNoch = false

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dzenNoch = getBaseDzenNoch()
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Slidr.attach(this)
        setTollbarTheme()
        val slugba = SlugbovyiaTextu()
        val mineiaList = slugba.getMineiaSviatochnaia()
        mineiaList.sort()
        val c = Calendar.getInstance()
        var day = 0
        val children0 = ArrayList<MineiaDay>()
        val children1 = ArrayList<MineiaDay>()
        val children2 = ArrayList<MineiaDay>()
        val children3 = ArrayList<MineiaDay>()
        val children4 = ArrayList<MineiaDay>()
        val children5 = ArrayList<MineiaDay>()
        val children6 = ArrayList<MineiaDay>()
        val children7 = ArrayList<MineiaDay>()
        val children8 = ArrayList<MineiaDay>()
        val children9 = ArrayList<MineiaDay>()
        val children10 = ArrayList<MineiaDay>()
        val children11 = ArrayList<MineiaDay>()
        for (i in 0 until mineiaList.size) {
            if (day == mineiaList[i].day) {
                day = mineiaList[i].day
                continue
            } else {
                day = mineiaList[i].day
            }
            if (mineiaList[i].pasxa) {
                MenuCaliandar.getDataCalaindar(year = c[Calendar.YEAR]).forEach {
                    if (it[22].toInt() == day) {
                        c.set(it[3].toInt(), it[2].toInt(), it[1].toInt())
                        day = it[22].toInt()
                        return@forEach
                    }
                }
            } else {
                MenuCaliandar.getDataCalaindar(year = c[Calendar.YEAR]).forEach {
                    if (it[24].toInt() == day) {
                        c.set(it[3].toInt(), it[2].toInt(), it[1].toInt())
                        return@forEach
                    }
                }
            }
            val id = c.timeInMillis
            val count = ArrayList<String>()
            val abednica = slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.ABEDNICA)
            val vialikiaGadziny = slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIALIKIAGADZINY)
            val utran = slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.UTRAN)
            val liturgia = slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.LITURGIA)
            val viachernia = slugba.getResource(day, mineiaList[i].pasxa, SlugbovyiaTextu.VIACHERNIA)
            if (abednica != "0") count.add("1")
            if (vialikiaGadziny != "0") count.add("1")
            if (utran != "0") count.add("1")
            if (liturgia != "0") count.add("1")
            if (viachernia != "0") count.add("1")
            var title = mineiaList[i].title
            if (count.size > 1) {
                val t1 = title.indexOf("-")
                if (t1 != -1) {
                    title = title.substring(0, t1).trim()
                }
            }
            when (c[Calendar.MONTH]) {
                Calendar.JANUARY -> children0.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.FEBRUARY -> children1.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.MARCH -> children2.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.APRIL -> children3.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.MAY -> children4.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.JUNE -> children5.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.JULY -> children6.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.AUGUST -> children7.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.SEPTEMBER -> children8.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.OCTOBER -> children9.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.NOVEMBER -> children10.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
                Calendar.DECEMBER -> children11.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + title, title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
            }
            day = mineiaList[i].day
        }
        children0.sort()
        children1.sort()
        children2.sort()
        children3.sort()
        children4.sort()
        children5.sort()
        children6.sort()
        children7.sort()
        children8.sort()
        children9.sort()
        children10.sort()
        children11.sort()
        if (children0.size != 0) groups.add(children0)
        if (children1.size != 0) groups.add(children1)
        if (children2.size != 0) groups.add(children2)
        if (children3.size != 0) groups.add(children3)
        if (children4.size != 0) groups.add(children4)
        if (children5.size != 0) groups.add(children5)
        if (children6.size != 0) groups.add(children6)
        if (children7.size != 0) groups.add(children7)
        if (children8.size != 0) groups.add(children8)
        if (children9.size != 0) groups.add(children9)
        if (children10.size != 0) groups.add(children10)
        if (children11.size != 0) groups.add(children11)
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
            var count = 0
            if (resourceAbednica != "0") count++
            if (resourceVialikiaGadziny != "0") count++
            if (resourceUtran != "0") count++
            if (resourceLiturgia != "0") count++
            if (resourceViachernia != "0") count++
            if (count > 1) {
                val dialog = DialogMineiaList.getInstance(groups[groupPosition][childPosition].day, groups[groupPosition][childPosition].titleResource, resourceUtran, resourceLiturgia, resourceViachernia, resourceAbednica, resourceVialikiaGadziny, true)
                dialog.show(supportFragmentManager, "dialogMineiaList")
            } else {
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                    if (resourceAbednica != "0") intent.putExtra("resurs", resourceAbednica)
                    if (resourceVialikiaGadziny != "0") intent.putExtra("resurs", resourceVialikiaGadziny)
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
        binding.titleToolbar.text = getString(R.string.mineia_sviatochnaia)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        month = null
    }

    companion object {
        private var month: Int? = null
    }
}