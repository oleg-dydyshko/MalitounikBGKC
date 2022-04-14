package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.*
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class MineiaShodzennaia : AppCompatActivity() {

    private lateinit var k: SharedPreferences
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: ExpListAdapterMineiaShodzennaia
    private val groups = ArrayList<ArrayList<MineiaDay>>()
    private var mLastClickTime: Long = 0

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun loadOpisanie(mun: Int): ArrayList<String> {
        val fileOpisanieSviat = File("$filesDir/sviatyja/opisanie$mun.json")
        val builder = fileOpisanieSviat.readText()
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(builder, type)
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
            } //Айцоў першых 6-ці Ўсяленскіх сабораў
            when {
                mineiaList[i].day == 1000 -> {
                    val pasha = Calendar.getInstance()
                    for (dny in 13..19) {
                        pasha.set(pasha[Calendar.YEAR], Calendar.JULY, dny)
                        val wik = pasha.get(Calendar.DAY_OF_WEEK)
                        if (wik == Calendar.SUNDAY) {
                            day = pasha[Calendar.DAY_OF_YEAR]
                        }
                    }
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
            day = mineiaList[i].day
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
        for (i in 1..12) {
            loadOpisanie(i).forEachIndexed { index, string ->
                if (string.contains("<em>", ignoreCase = true)) {
                    if (string.contains("трапар", ignoreCase = true) || string.contains("кандак", ignoreCase = true)) {
                        val t1 = string.indexOf("<em>")
                        val t2 = string.lastIndexOf("<p>", t1)
                        val t3 = string.lastIndexOf("</strong>", t2)
                        val t4 = string.lastIndexOf("<strong>", t3)
                        var title = ""
                        if (t3 != -1 && t4 != -1) {
                            title = ": " + string.substring(t4 + 8, t3)
                        }
                        c.set(c[Calendar.YEAR], i - 1, index + 1)
                        val id = c.timeInMillis
                        when (i) {
                            1 -> {
                                child0.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            2 -> {
                                child1.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            3 -> {
                                child2.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            4 -> {
                                child3.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            5 -> {
                                child4.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            6 -> {
                                child5.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            7 -> {
                                child6.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            8 -> {
                                child7.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            9 -> {
                                child8.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            10 -> {
                                child9.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            11 -> {
                                child10.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                            12 -> {
                                child11.add(MineiaDay(id, i - 1, (index + 1).toString(), (index + 1).toString() + " " + resources.getStringArray(R.array.meciac_smoll)[i - 1] + title, "", "", "", "", "", ""))
                            }
                        }
                    }
                }
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
        if (month == null) {
            for (i in 0 until groups.size) {
                for (e in 0 until groups[i].size) {
                    if (cal[Calendar.MONTH] == groups[i][e].month) month = i
                }
            }
        }
        binding.elvMain.expandGroup(month ?: 0)
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        month = null
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

    private data class MineiaDay(val id: Long, val month: Int, val day: String, val title: String, val titleResource: String, var resourceViachernia: String, var resourceUtran: String, var resourceLiturgia: String, var resourceVialikiaGadziny: String, var resourceAbednica: String) : Comparable<MineiaDay> {
        override fun compareTo(other: MineiaDay): Int {
            if (this.id > other.id) {
                return 1
            } else if (this.id < other.id) {
                return -1
            }
            return 0
        }
    }

    companion object {
        private var month: Int? = null
    }
}