package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding

class StaryZapavietSinaidalList : AppCompatActivity() {
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private val groups = ArrayList<ArrayList<String>>()
    private lateinit var binding: ContentBibleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (dzenNoch)
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
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
        val children40 = ArrayList<String>()
        val children41 = ArrayList<String>()
        val children42 = ArrayList<String>()
        val children43 = ArrayList<String>()
        val children44 = ArrayList<String>()
        val children45 = ArrayList<String>()
        val children46 = ArrayList<String>()
        val children47 = ArrayList<String>()
        val children48 = ArrayList<String>()
        val children49 = ArrayList<String>()
        val children50 = ArrayList<String>()
        for (i in 1..50) {
            children1.add("Глава $i")
        }
        groups.add(children1)
        for (i in 1..40) {
            children2.add("Глава $i")
        }
        groups.add(children2)
        for (i in 1..27) {
            children3.add("Глава $i")
        }
        groups.add(children3)
        for (i in 1..36) {
            children4.add("Глава $i")
        }
        groups.add(children4)
        for (i in 1..34) {
            children5.add("Глава $i")
        }
        groups.add(children5)
        for (i in 1..24) {
            children6.add("Глава $i")
        }
        groups.add(children6)
        for (i in 1..21) {
            children7.add("Глава $i")
        }
        groups.add(children7)
        for (i in 1..4) {
            children8.add("Глава $i")
        }
        groups.add(children8)
        for (i in 1..31) {
            children9.add("Глава $i")
        }
        groups.add(children9)
        for (i in 1..24) {
            children10.add("Глава $i")
        }
        groups.add(children10)
        for (i in 1..22) {
            children11.add("Глава $i")
        }
        groups.add(children11)
        for (i in 1..25) {
            children12.add("Глава $i")
        }
        groups.add(children12)
        for (i in 1..29) {
            children13.add("Глава $i")
        }
        groups.add(children13)
        for (i in 1..37) {
            children14.add("Глава $i")
        }
        groups.add(children14)
        for (i in 1..10) {
            children15.add("Глава $i")
        }
        groups.add(children15)
        for (i in 1..13) {
            children16.add("Глава $i")
        }
        groups.add(children16)
        for (i in 1..9) {
            children17.add("Глава $i")
        }
        groups.add(children17)
        for (i in 1..14) {
            children18.add("Глава $i")
        }
        groups.add(children18)
        for (i in 1..16) {
            children19.add("Глава $i")
        }
        groups.add(children19)
        for (i in 1..10) {
            children20.add("Глава $i")
        }
        groups.add(children20)
        for (i in 1..42) {
            children21.add("Глава $i")
        }
        groups.add(children21)
        for (i in 1..151) {
            children22.add("Псалом $i")
        }
        groups.add(children22)
        for (i in 1..31) {
            children23.add("Глава $i")
        }
        groups.add(children23)
        for (i in 1..12) {
            children24.add("Глава $i")
        }
        groups.add(children24)
        for (i in 1..8) {
            children25.add("Глава $i")
        }
        groups.add(children25)
        for (i in 1..19) {
            children26.add("Глава $i")
        }
        groups.add(children26)
        for (i in 1..51) {
            children27.add("Глава $i")
        }
        groups.add(children27)
        for (i in 1..66) {
            children28.add("Глава $i")
        }
        groups.add(children28)
        for (i in 1..52) {
            children29.add("Глава $i")
        }
        groups.add(children29)
        for (i in 1..5) {
            children30.add("Глава $i")
        }
        groups.add(children30)
        for (i in 1..1) {
            children31.add("Глава $i")
        }
        groups.add(children31)
        for (i in 1..5) {
            children32.add("Глава $i")
        }
        groups.add(children32)
        for (i in 1..48) {
            children33.add("Глава $i")
        }
        groups.add(children33)
        for (i in 1..14) {
            children34.add("Глава $i")
        }
        groups.add(children34)
        for (i in 1..14) {
            children35.add("Глава $i")
        }
        groups.add(children35)
        for (i in 1..3) {
            children36.add("Глава $i")
        }
        groups.add(children36)
        for (i in 1..9) {
            children37.add("Глава $i")
        }
        groups.add(children37)
        for (i in 1..1) {
            children38.add("Глава $i")
        }
        groups.add(children38)
        for (i in 1..4) {
            children39.add("Глава $i")
        }
        groups.add(children39)
        for (i in 1..7) {
            children40.add("Глава $i")
        }
        groups.add(children40)
        for (i in 1..3) {
            children41.add("Глава $i")
        }
        groups.add(children41)
        for (i in 1..3) {
            children42.add("Глава $i")
        }
        groups.add(children42)
        for (i in 1..3) {
            children43.add("Глава $i")
        }
        groups.add(children43)
        for (i in 1..2) {
            children44.add("Глава $i")
        }
        groups.add(children44)
        for (i in 1..14) {
            children45.add("Глава $i")
        }
        groups.add(children45)
        for (i in 1..4) {
            children46.add("Глава $i")
        }
        groups.add(children46)
        for (i in 1..16) {
            children47.add("Глава $i")
        }
        groups.add(children47)
        for (i in 1..15) {
            children48.add("Глава $i")
        }
        groups.add(children48)
        for (i in 1..7) {
            children49.add("Глава $i")
        }
        groups.add(children49)
        for (i in 1..16) {
            children50.add("Глава $i")
        }
        groups.add(children50)
        val adapter = ExpListAdapterStaryZapavietSinaidal(this)
        binding.elvMain.setAdapter(adapter)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.STARYZAPAVIETSINAIDAL)
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
            val intent1 = Intent()
            intent1.setClassName(this, MainActivity.STARYZAPAVIETSINAIDAL)
            intent1.putExtra("kniga", intent.extras?.getInt("kniga"))
            intent1.putExtra("glava", intent.extras?.getInt("glava"))
            intent1.putExtra("stix", intent.extras?.getInt("stix"))
            startActivity(intent1)
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            binding.titleToolbar.setHorizontallyScrolling(true)
            binding.titleToolbar.freezesText = true
            binding.titleToolbar.marqueeRepeatLimit = -1
            if (binding.titleToolbar.isSelected) {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                binding.titleToolbar.isSelected = false
            } else {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.titleToolbar.isSelected = true
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setText(R.string.stsinaidal)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
    }

    override fun onBackPressed() {
        if (MenuBibleSinoidal.bible_time) {
            MenuBibleSinoidal.bible_time = false
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
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    private inner class ExpListAdapterStaryZapavietSinaidal(private val mContext: Activity) : BaseExpandableListAdapter() {
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
                0 -> rootView.textGroup.text = "Бытие"
                1 -> rootView.textGroup.text = "Исход"
                2 -> rootView.textGroup.text = "Левит"
                3 -> rootView.textGroup.text = "Числа"
                4 -> rootView.textGroup.text = "Второзаконие"
                5 -> rootView.textGroup.text = "Иисуса Навина"
                6 -> rootView.textGroup.text = "Судей израилевых"
                7 -> rootView.textGroup.text = "Руфи"
                8 -> rootView.textGroup.text = "1-я Царств"
                9 -> rootView.textGroup.text = "2-я Царств"
                10 -> rootView.textGroup.text = "3-я Царств"
                11 -> rootView.textGroup.text = "4-я Царств"
                12 -> rootView.textGroup.text = "1-я Паралипоменон"
                13 -> rootView.textGroup.text = "2-я Паралипоменон"
                14 -> rootView.textGroup.text = "1-я Ездры"
                15 -> rootView.textGroup.text = "Неемии"
                16 -> rootView.textGroup.text = "2-я Ездры"
                17 -> rootView.textGroup.text = "Товита"
                18 -> rootView.textGroup.text = "Иудифи"
                19 -> rootView.textGroup.text = "Есфири"
                20 -> rootView.textGroup.text = "Иова"
                21 -> rootView.textGroup.text = "Псалтирь"
                22 -> rootView.textGroup.text = "Притчи Соломона"
                23 -> rootView.textGroup.text = "Екклезиаста"
                24 -> rootView.textGroup.text = "Песнь песней Соломона"
                25 -> rootView.textGroup.text = "Премудрости Соломона"
                26 -> rootView.textGroup.text = "Премудрости Иисуса, сына Сирахова"
                27 -> rootView.textGroup.text = "Исаии"
                28 -> rootView.textGroup.text = "Иеремии"
                29 -> rootView.textGroup.text = "Плач Иеремии"
                30 -> rootView.textGroup.text = "Послание Иеремии"
                31 -> rootView.textGroup.text = "Варуха"
                32 -> rootView.textGroup.text = "Иезекииля"
                33 -> rootView.textGroup.text = "Даниила"
                34 -> rootView.textGroup.text = "Осии"
                35 -> rootView.textGroup.text = "Иоиля"
                36 -> rootView.textGroup.text = "Амоса"
                37 -> rootView.textGroup.text = "Авдия"
                38 -> rootView.textGroup.text = "Ионы"
                39 -> rootView.textGroup.text = "Михея"
                40 -> rootView.textGroup.text = "Наума"
                41 -> rootView.textGroup.text = "Аввакума"
                42 -> rootView.textGroup.text = "Сафонии"
                43 -> rootView.textGroup.text = "Аггея"
                44 -> rootView.textGroup.text = "Захарии"
                45 -> rootView.textGroup.text = "Малахии"
                46 -> rootView.textGroup.text = "1-я Маккавейская"
                47 -> rootView.textGroup.text = "2-я Маккавейская"
                48 -> rootView.textGroup.text = "3-я Маккавейская"
                49 -> rootView.textGroup.text = "3-я Ездры"
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
            rootView.textChild.text = groups[groupPosition][childPosition]
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}