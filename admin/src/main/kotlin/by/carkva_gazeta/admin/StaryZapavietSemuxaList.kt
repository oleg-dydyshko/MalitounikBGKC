package by.carkva_gazeta.admin

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
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuBibleSemuxa
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import kotlinx.coroutines.*

class StaryZapavietSemuxaList : AppCompatActivity() {
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private val groups = ArrayList<ArrayList<String>>()
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var k: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (dzenNoch) binding.elvMain.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.elvMain.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
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
        val children28 = ArrayList<String>()
        val children29 = ArrayList<String>()
        val children30 = ArrayList<String>()
        val children31 = ArrayList<String>()
        val children32 = ArrayList<String>()
        val children33 = ArrayList<String>()
        val children34 = ArrayList<String>()
        val children35 = ArrayList<String>()
        val children36 = ArrayList<String>()
        val children37 = ArrayList<String>()
        val children38 = ArrayList<String>()
        val children39 = ArrayList<String>()
        for (i in 1..50) {
            children1.add("Разьдзел $i")
        }
        groups.add(children1)
        for (i in 1..40) {
            children2.add("Разьдзел $i")
        }
        groups.add(children2)
        for (i in 1..27) {
            children3.add("Разьдзел $i")
        }
        groups.add(children3)
        for (i in 1..36) {
            children4.add("Разьдзел $i")
        }
        groups.add(children4)
        for (i in 1..34) {
            children5.add("Разьдзел $i")
        }
        groups.add(children5)
        for (i in 1..24) {
            children6.add("Разьдзел $i")
        }
        groups.add(children6)
        for (i in 1..21) {
            children7.add("Разьдзел $i")
        }
        groups.add(children7)
        for (i in 1..4) {
            children8.add("Разьдзел $i")
        }
        groups.add(children8)
        for (i in 1..31) {
            children9.add("Разьдзел $i")
        }
        groups.add(children9)
        for (i in 1..24) {
            children10.add("Разьдзел $i")
        }
        groups.add(children10)
        for (i in 1..22) {
            children11.add("Разьдзел $i")
        }
        groups.add(children11)
        for (i in 1..25) {
            children12.add("Разьдзел $i")
        }
        groups.add(children12)
        for (i in 1..29) {
            children13.add("Разьдзел $i")
        }
        groups.add(children13)
        for (i in 1..36) {
            children14.add("Разьдзел $i")
        }
        groups.add(children14)
        for (i in 1..10) {
            children15.add("Разьдзел $i")
        }
        groups.add(children15)
        for (i in 1..13) {
            children16.add("Разьдзел $i")
        }
        groups.add(children16)
        for (i in 1..10) {
            children17.add("Разьдзел $i")
        }
        groups.add(children17)
        for (i in 1..42) {
            children18.add("Разьдзел $i")
        }
        groups.add(children18)
        for (i in 1..151) {
            children19.add("Псальма $i")
        }
        groups.add(children19)
        for (i in 1..31) {
            children20.add("Разьдзел $i")
        }
        groups.add(children20)
        for (i in 1..12) {
            children21.add("Разьдзел $i")
        }
        groups.add(children21)
        for (i in 1..8) {
            children22.add("Разьдзел $i")
        }
        groups.add(children22)
        for (i in 1..66) {
            children23.add("Разьдзел $i")
        }
        groups.add(children23)
        for (i in 1..52) {
            children24.add("Разьдзел $i")
        }
        groups.add(children24)
        for (i in 1..5) {
            children25.add("Разьдзел $i")
        }
        groups.add(children25)
        for (i in 1..48) {
            children26.add("Разьдзел $i")
        }
        groups.add(children26)
        for (i in 1..12) {
            children27.add("Разьдзел $i")
        }
        groups.add(children27)
        for (i in 1..14) {
            children28.add("Разьдзел $i")
        }
        groups.add(children28)
        for (i in 1..3) {
            children29.add("Разьдзел $i")
        }
        groups.add(children29)
        for (i in 1..9) {
            children30.add("Разьдзел $i")
        }
        groups.add(children30)
        for (i in 1..1) {
            children31.add("Разьдзел $i")
        }
        groups.add(children31)
        for (i in 1..4) {
            children32.add("Разьдзел $i")
        }
        groups.add(children32)
        for (i in 1..7) {
            children33.add("Разьдзел $i")
        }
        groups.add(children33)
        for (i in 1..3) {
            children34.add("Разьдзел $i")
        }
        groups.add(children34)
        for (i in 1..3) {
            children35.add("Разьдзел $i")
        }
        groups.add(children35)
        for (i in 1..3) {
            children36.add("Разьдзел $i")
        }
        groups.add(children36)
        for (i in 1..2) {
            children37.add("Разьдзел $i")
        }
        groups.add(children37)
        for (i in 1..14) {
            children38.add("Разьдзел $i")
        }
        groups.add(children38)
        for (i in 1..4) {
            children39.add("Разьдзел $i")
        }
        groups.add(children39)
        val adapter = ExpListAdapterStaryZapaviet(this)
        binding.elvMain.setAdapter(adapter)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, StaryZapavietSemuxa::class.java)
            intent.putExtra("kniga", groupPosition)
            intent.putExtra("glava", childPosition)
            startActivity(intent)
            /*if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.STARYZAPAVIETSEMUXA)
                intent.putExtra("kniga", groupPosition)
                intent.putExtra("glava", childPosition)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }*/
            false
        }
        /*if (intent.extras?.getBoolean("prodolzyt", false) == true) {
            val intent1 = Intent()
            intent1.setClassName(this, MainActivity.STARYZAPAVIETSEMUXA)
            intent1.putExtra("kniga", intent.extras?.getInt("kniga"))
            intent1.putExtra("glava", intent.extras?.getInt("glava"))
            intent1.putExtra("stix", intent.extras?.getInt("stix"))
            startActivity(intent1)
        }*/
    }

    private fun setTollbarTheme() {
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
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setText(by.carkva_gazeta.malitounik.R.string.stary_zapaviet)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
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

    override fun onBackPressed() {
        if (MenuBibleSemuxa.bible_time) {
            MenuBibleSemuxa.bible_time = false
            onSupportNavigateUp()
        } else {
            super.onBackPressed()
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
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private inner class ExpListAdapterStaryZapaviet(private val mContext: Activity) : BaseExpandableListAdapter() {
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
                0 -> rootView.textGroup.text = "Быцьцё"
                1 -> rootView.textGroup.text = "Выхад"
                2 -> rootView.textGroup.text = "Лявіт"
                3 -> rootView.textGroup.text = "Лікі"
                4 -> rootView.textGroup.text = "Другі Закон"
                5 -> rootView.textGroup.text = "Ісуса сына Нава"
                6 -> rootView.textGroup.text = "Судзьдзяў"
                7 -> rootView.textGroup.text = "Рут"
                8 -> rootView.textGroup.text = "1-я Царстваў"
                9 -> rootView.textGroup.text = "2-я Царстваў"
                10 -> rootView.textGroup.text = "3-я Царстваў"
                11 -> rootView.textGroup.text = "4-я Царстваў"
                12 -> rootView.textGroup.text = "1-я Летапісаў"
                13 -> rootView.textGroup.text = "2-я Летапісаў"
                14 -> rootView.textGroup.text = "Эздры"
                15 -> rootView.textGroup.text = "Нээміі"
                16 -> rootView.textGroup.text = "Эстэр"
                17 -> rootView.textGroup.text = "Ёва"
                18 -> rootView.textGroup.text = "Псалтыр"
                19 -> rootView.textGroup.text = "Выслоўяў Саламонавых"
                20 -> rootView.textGroup.text = "Эклезіяста"
                21 -> rootView.textGroup.text = "Найвышэйшая Песьня Саламонава"
                22 -> rootView.textGroup.text = "Ісаі"
                23 -> rootView.textGroup.text = "Ераміі"
                24 -> rootView.textGroup.text = "Ераміін Плач"
                25 -> rootView.textGroup.text = "Езэкііля"
                26 -> rootView.textGroup.text = "Данііла"
                27 -> rootView.textGroup.text = "Асіі"
                28 -> rootView.textGroup.text = "Ёіля"
                29 -> rootView.textGroup.text = "Амоса"
                30 -> rootView.textGroup.text = "Аўдзея"
                31 -> rootView.textGroup.text = "Ёны"
                32 -> rootView.textGroup.text = "Міхея"
                33 -> rootView.textGroup.text = "Навума"
                34 -> rootView.textGroup.text = "Абакума"
                35 -> rootView.textGroup.text = "Сафона"
                36 -> rootView.textGroup.text = "Агея"
                37 -> rootView.textGroup.text = "Захарыі"
                38 -> rootView.textGroup.text = "Малахіі"
            }
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            rootView.textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition]
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}