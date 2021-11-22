package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class MineiaShodzennaiaDzenList : AppCompatActivity() {

    private lateinit var k: SharedPreferences
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: ExpListAdapterMineiaShodzennaia
    private val groups = ArrayList<ArrayList<MineiaDay>>()

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
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
            //Айцоў першых 6-ці Ўсяленскіх сабораў
            if (mineiaList[i].day == 1000) {
                val pasha = Calendar.getInstance()
                for (dny in 13..19) {
                    pasha.set(pasha[Calendar.YEAR], Calendar.JULY, dny)
                    val wik = pasha.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        day = pasha[Calendar.DAY_OF_YEAR]
                    }
                }
            }
            c.set(Calendar.DAY_OF_YEAR, day)
            when (c[Calendar.MONTH]) {
                Calendar.JANUARY -> child0.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.FEBRUARY -> child1.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.MARCH -> child2.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.APRIL -> child3.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.MAY -> child4.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.JUNE -> child5.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.JULY -> child6.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.AUGUST -> child7.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.SEPTEMBER -> child8.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.OCTOBER -> child9.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.NOVEMBER -> child10.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
                Calendar.DECEMBER -> child11.add(MineiaDay(c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], mineiaList[i].title, slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString()), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), utran = true), slugba.getResource(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), day.toString(), liturgia = true)))
            }
        }
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
            val intent = Intent(this, MineiaShodzennaiaList::class.java)
            intent.putExtra("dayOfYear", groups[groupPosition][childPosition].day)
            intent.putExtra("titleResource", groups[groupPosition][childPosition].titleResource)
            intent.putExtra("resourceUtran", groups[groupPosition][childPosition].resourceUtran)
            intent.putExtra("resourceLiturgia", groups[groupPosition][childPosition].resourceLiturgia)
            intent.putExtra("resourceViachernia", groups[groupPosition][childPosition].resourceViachernia)
            startActivity(intent)
            false
        }
        if (dzenNoch) {
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
        adapter = ExpListAdapterMineiaShodzennaia(this)
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
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val cal = Calendar.getInstance()
        var month = 0
        for (i in 0 until groups.size) {
            for (e in 0 until groups[i].size) {
                if (cal[Calendar.MONTH] == groups[i][e].month) month = i
            }
        }
        binding.elvMain.expandGroup(month)
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ExpListAdapterMineiaShodzennaia(private val mContext: Activity) : BaseExpandableListAdapter() {
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
            val rootView = GroupViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            rootView.textGroup.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            when (groups[groupPosition][0].month) {
                0 -> rootView.textGroup.text = "Студзень"
                1 -> rootView.textGroup.text = "Люты"
                2 -> rootView.textGroup.text = "Сакавік"
                3 -> rootView.textGroup.text = "Красавік"
                4 -> rootView.textGroup.text = "Травень"
                5 -> rootView.textGroup.text = "Чэрвень"
                6 -> rootView.textGroup.text = "Ліпень"
                7 -> rootView.textGroup.text = "Жнівень"
                8 -> rootView.textGroup.text = "Верасень"
                9 -> rootView.textGroup.text = "Кастрычнік"
                10 -> rootView.textGroup.text = "Лістапад"
                11 -> rootView.textGroup.text = "Сьнежань"
            }
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            rootView.textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition].title
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    private data class MineiaDay(val month: Int, val day: String, val title: String, val titleResource: String, var resourceViachernia: String, var resourceUtran: String, var resourceLiturgia: String)
}