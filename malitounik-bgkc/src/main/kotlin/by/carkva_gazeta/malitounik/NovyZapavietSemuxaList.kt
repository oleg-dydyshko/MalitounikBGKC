package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import kotlinx.coroutines.*

class NovyZapavietSemuxaList : BaseActivity() {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
        val groups = ArrayList<ArrayList<String>>()
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
            children1.add("Разьдзел $i")
        }
        groups.add(children1)
        for (i in 1..16) {
            children2.add("Разьдзел $i")
        }
        groups.add(children2)
        for (i in 1..24) {
            children3.add("Разьдзел $i")
        }
        groups.add(children3)
        for (i in 1..21) {
            children4.add("Разьдзел $i")
        }
        groups.add(children4)
        for (i in 1..28) {
            children5.add("Разьдзел $i")
        }
        groups.add(children5)
        for (i in 1..5) {
            children6.add("Разьдзел $i")
        }
        groups.add(children6)
        for (i in 1..5) {
            children7.add("Разьдзел $i")
        }
        groups.add(children7)
        for (i in 1..3) {
            children8.add("Разьдзел $i")
        }
        groups.add(children8)
        for (i in 1..5) {
            children9.add("Разьдзел $i")
        }
        groups.add(children9)
        children10.add("Разьдзел " + 1)
        groups.add(children10)
        children11.add("Разьдзел " + 1)
        groups.add(children11)
        children12.add("Разьдзел " + 1)
        groups.add(children12)
        for (i in 1..16) {
            children13.add("Разьдзел $i")
        }
        groups.add(children13)
        for (i in 1..16) {
            children14.add("Разьдзел $i")
        }
        groups.add(children14)
        for (i in 1..13) {
            children15.add("Разьдзел $i")
        }
        groups.add(children15)
        for (i in 1..6) {
            children16.add("Разьдзел $i")
        }
        groups.add(children16)
        for (i in 1..6) {
            children17.add("Разьдзел $i")
        }
        groups.add(children17)
        for (i in 1..4) {
            children18.add("Разьдзел $i")
        }
        groups.add(children18)
        for (i in 1..4) {
            children19.add("Разьдзел $i")
        }
        groups.add(children19)
        for (i in 1..5) {
            children20.add("Разьдзел $i")
        }
        groups.add(children20)
        for (i in 1..3) {
            children21.add("Разьдзел $i")
        }
        groups.add(children21)
        for (i in 1..6) {
            children22.add("Разьдзел $i")
        }
        groups.add(children22)
        for (i in 1..4) {
            children23.add("Разьдзел $i")
        }
        groups.add(children23)
        for (i in 1..3) {
            children24.add("Разьдзел $i")
        }
        groups.add(children24)
        children25.add("Разьдзел " + 1)
        groups.add(children25)
        for (i in 1..13) {
            children26.add("Разьдзел $i")
        }
        groups.add(children26)
        for (i in 1..22) {
            children27.add("Разьдзел $i")
        }
        groups.add(children27)
        val adapter = ExpListAdapterNovyZapaviet(this, groups)
        binding.elvMain.setAdapter(adapter)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.NOVYZAPAVIETSEMUXA)
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
            intent1.setClassName(this, MainActivity.NOVYZAPAVIETSEMUXA)
            intent1.putExtra("kniga", intent.extras?.getInt("kniga") ?: 0)
            intent1.putExtra("glava", intent.extras?.getInt("glava") ?: 0)
            intent1.putExtra("stix", intent.extras?.getInt("stix") ?: 0)
            startActivity(intent1)
        }
        setTollbarTheme()
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setText(R.string.novy_zapaviet)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    private class ExpListAdapterNovyZapaviet(private val mContext: Activity, private val groups: ArrayList<ArrayList<String>>) : BaseExpandableListAdapter() {
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
                0 -> rootView.textGroup.text = "Паводле Мацьвея"
                1 -> rootView.textGroup.text = "Паводле Марка"
                2 -> rootView.textGroup.text = "Паводле Лукаша"
                3 -> rootView.textGroup.text = "Паводле Яна"
                4 -> rootView.textGroup.text = "Дзеі Апосталаў"
                5 -> rootView.textGroup.text = "Якава"
                6 -> rootView.textGroup.text = "1-е Пятра"
                7 -> rootView.textGroup.text = "2-е Пятра"
                8 -> rootView.textGroup.text = "1-е Яна Багаслова"
                9 -> rootView.textGroup.text = "2-е Яна Багаслова"
                10 -> rootView.textGroup.text = "3-е Яна Багаслова"
                11 -> rootView.textGroup.text = "Юды"
                12 -> rootView.textGroup.text = "Да Рымлянаў"
                13 -> rootView.textGroup.text = "1-е да Карынфянаў"
                14 -> rootView.textGroup.text = "2-е да Карынфянаў"
                15 -> rootView.textGroup.text = "Да Галятаў"
                16 -> rootView.textGroup.text = "Да Эфэсянаў"
                17 -> rootView.textGroup.text = "Да Піліпянаў"
                18 -> rootView.textGroup.text = "Да Каласянаў"
                19 -> rootView.textGroup.text = "1-е да Фесаланікійцаў"
                20 -> rootView.textGroup.text = "2-е да Фесаланікійцаў"
                21 -> rootView.textGroup.text = "1-е да Цімафея"
                22 -> rootView.textGroup.text = "2-е да Цімафея"
                23 -> rootView.textGroup.text = "Да Ціта"
                24 -> rootView.textGroup.text = "Да Філімона"
                25 -> rootView.textGroup.text = "Да Габрэяў"
                26 -> rootView.textGroup.text = "Адкрыцьцё (Апакаліпсіс)"
            }
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            rootView.textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition]
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}