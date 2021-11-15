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
        val c = GregorianCalendar()
        val day = "312"
        c.set(Calendar.DAY_OF_YEAR, day.toInt())
        val child = ArrayList<MineiaDay>()
        child.add(MineiaDay(day, c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], slugba.getTitle(slugba.getResource(day)), slugba.getResource(day), slugba.getResource(day, utran = true), slugba.getResource(day, liturgia = true)))
        groups.add(child)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, _: Int, childPosition: Int, _: Long ->
            val intent = Intent(this, MineiaShodzennaiaList::class.java)
            intent.putExtra("dayOfYear", child[childPosition].day)
            intent.putExtra("titleResource", child[childPosition].titleResource)
            intent.putExtra("resourceUtran", child[childPosition].resourceUtran)
            intent.putExtra("resourceLiturgia", child[childPosition].resourceLiturgia)
            intent.putExtra("resourceViachernia", child[childPosition].resourceViachernia)
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
            when (groupPosition) {
                0 -> rootView.textGroup.text = "Лістапад"
                1 -> rootView.textGroup.text = "От Марка"
                2 -> rootView.textGroup.text = "От Луки"
                3 -> rootView.textGroup.text = "От Иоанна"
                4 -> rootView.textGroup.text = "Деяния святых апостолов"
                5 -> rootView.textGroup.text = "Иакова"
                6 -> rootView.textGroup.text = "1-е Петра"
                7 -> rootView.textGroup.text = "2-е Петра"
                8 -> rootView.textGroup.text = "1-е Иоанна"
                9 -> rootView.textGroup.text = "2-е Иоанна"
                10 -> rootView.textGroup.text = "3-е Иоанна"
                11 -> rootView.textGroup.text = "Иуды"
                12 -> rootView.textGroup.text = "Римлянам"
            }
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            rootView.textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch)
                rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition].title
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    private data class MineiaDay(val day: String, val title: String, val titleResource: String, val resourceViachernia: String, val resourceUtran: String, val resourceLiturgia: String)
}