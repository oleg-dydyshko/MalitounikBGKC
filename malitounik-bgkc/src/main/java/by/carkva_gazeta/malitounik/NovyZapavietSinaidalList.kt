package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_bible.*
import java.util.*

class NovyZapavietSinaidalList : AppCompatActivity() {
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private val groups = ArrayList<ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        setContentView(R.layout.content_bible)
        val children1 = ArrayList<String>()
        val children2 = ArrayList<String>()
        val children3 = ArrayList<String>()
        val children4 = ArrayList<String>()
        val children5 = ArrayList<String>()
        val children6 = ArrayList<String>()
        val children7 = ArrayList<String>()
        val children8 = ArrayList<String>()
        val children9 = ArrayList<String>()
        val children10 = ArrayList<String>()
        val children11 = ArrayList<String>()
        val children12 = ArrayList<String>()
        val children13 = ArrayList<String>()
        val children14 = ArrayList<String>()
        val children15 = ArrayList<String>()
        val children16 = ArrayList<String>()
        val children17 = ArrayList<String>()
        val children18 = ArrayList<String>()
        val children19 = ArrayList<String>()
        val children20 = ArrayList<String>()
        val children21 = ArrayList<String>()
        val children22 = ArrayList<String>()
        val children23 = ArrayList<String>()
        val children24 = ArrayList<String>()
        val children25 = ArrayList<String>()
        val children26 = ArrayList<String>()
        val children27 = ArrayList<String>()
        for (i in 1..28) {
            children1.add("Глава $i")
        }
        groups.add(children1)
        for (i in 1..16) {
            children2.add("Глава $i")
        }
        groups.add(children2)
        for (i in 1..24) {
            children3.add("Глава $i")
        }
        groups.add(children3)
        for (i in 1..21) {
            children4.add("Глава $i")
        }
        groups.add(children4)
        for (i in 1..28) {
            children5.add("Глава $i")
        }
        groups.add(children5)
        for (i in 1..5) {
            children6.add("Глава $i")
        }
        groups.add(children6)
        for (i in 1..5) {
            children7.add("Глава $i")
        }
        groups.add(children7)
        for (i in 1..3) {
            children8.add("Глава $i")
        }
        groups.add(children8)
        for (i in 1..5) {
            children9.add("Глава $i")
        }
        groups.add(children9)
        children10.add("Глава " + 1)
        groups.add(children10)
        children11.add("Глава " + 1)
        groups.add(children11)
        children12.add("Глава " + 1)
        groups.add(children12)
        for (i in 1..16) {
            children13.add("Глава $i")
        }
        groups.add(children13)
        for (i in 1..16) {
            children14.add("Глава $i")
        }
        groups.add(children14)
        for (i in 1..13) {
            children15.add("Глава $i")
        }
        groups.add(children15)
        for (i in 1..6) {
            children16.add("Глава $i")
        }
        groups.add(children16)
        for (i in 1..6) {
            children17.add("Глава $i")
        }
        groups.add(children17)
        for (i in 1..4) {
            children18.add("Глава $i")
        }
        groups.add(children18)
        for (i in 1..4) {
            children19.add("Глава $i")
        }
        groups.add(children19)
        for (i in 1..5) {
            children20.add("Глава $i")
        }
        groups.add(children20)
        for (i in 1..3) {
            children21.add("Глава $i")
        }
        groups.add(children21)
        for (i in 1..6) {
            children22.add("Глава $i")
        }
        groups.add(children22)
        for (i in 1..4) {
            children23.add("Глава $i")
        }
        groups.add(children23)
        for (i in 1..3) {
            children24.add("Глава $i")
        }
        groups.add(children24)
        children25.add("Глава " + 1)
        groups.add(children25)
        for (i in 1..13) {
            children26.add("Глава $i")
        }
        groups.add(children26)
        for (i in 1..22) {
            children27.add("Глава $i")
        }
        groups.add(children27)
        val adapter = ExpListAdapterNovyZapavietSinaidal(this)
        elvMain.setAdapter(adapter)
        elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent(this, Class.forName("by.carkva_gazeta.resources.NovyZapavietSinaidal"))
                intent.putExtra("kniga", groupPosition)
                intent.putExtra("glava", childPosition)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }
            false
        }
        if (intent.extras?.getBoolean("prodolzyt", false) == true) {
            val intent1 = Intent(this, Class.forName("by.carkva_gazeta.resources.NovyZapavietSinaidal"))
            intent1.putExtra("kniga", intent.extras?.getInt("kniga"))
            intent1.putExtra("glava", intent.extras?.getInt("glava"))
            intent1.putExtra("stix", intent.extras?.getInt("stix"))
            startActivity(intent1)
        }
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setText(R.string.novsinaidal)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        setTollbarTheme()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    private inner class ExpListAdapterNovyZapavietSinaidal(private val mContext: Activity) : BaseExpandableListAdapter() {
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
            val rootView = mContext.layoutInflater.inflate(R.layout.group_view, parent, false)
            val textGroup: TextViewRobotoCondensed = rootView.findViewById(R.id.textGroup)
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            textGroup.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) textGroup.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            when (groupPosition) {
                0 -> textGroup.text = "От Матфея"
                1 -> textGroup.text = "От Марка"
                2 -> textGroup.text = "От Луки"
                3 -> textGroup.text = "От Иоанна"
                4 -> textGroup.text = "Деяния святых апостолов"
                5 -> textGroup.text = "Иакова"
                6 -> textGroup.text = "1-е Петра"
                7 -> textGroup.text = "2-е Петра"
                8 -> textGroup.text = "1-е Иоанна"
                9 -> textGroup.text = "2-е Иоанна"
                10 -> textGroup.text = "3-е Иоанна"
                11 -> textGroup.text = "Иуды"
                12 -> textGroup.text = "Римлянам"
                13 -> textGroup.text = "1-е Коринфянам"
                14 -> textGroup.text = "2-е Коринфянам"
                15 -> textGroup.text = "Галатам"
                16 -> textGroup.text = "Ефесянам"
                17 -> textGroup.text = "Филиппийцам"
                18 -> textGroup.text = "Колоссянам"
                19 -> textGroup.text = "1-е Фессалоникийцам (Солунянам)"
                20 -> textGroup.text = "2-е Фессалоникийцам (Солунянам)"
                21 -> textGroup.text = "1-е Тимофею"
                22 -> textGroup.text = "2-е Тимофею"
                23 -> textGroup.text = "Титу"
                24 -> textGroup.text = "Филимону"
                25 -> textGroup.text = "Евреям"
                26 -> textGroup.text = "Откровение (Апокалипсис)"
            }
            return rootView
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = mContext.layoutInflater.inflate(R.layout.child_view, parent, false)
            val textChild: TextViewRobotoCondensed = rootView.findViewById(R.id.textChild)
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                textChild.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            }
            textChild.text = groups[groupPosition][childPosition]
            return rootView
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}