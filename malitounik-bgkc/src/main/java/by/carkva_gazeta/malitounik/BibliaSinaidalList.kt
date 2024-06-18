package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BibliaSinaidalList : BaseActivity() {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private var novyZapavet = false

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
        novyZapavet = intent.extras?.getBoolean("novyZapavet", false) ?: false
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
        if (novyZapavet) {
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
        } else {
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
        }
        val adapter = ExpListAdapterNovyZapavietSinaidal(this, novyZapavet, groups)
        binding.elvMain.setAdapter(adapter)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.BIBLIAACTIVITY)
                intent.putExtra("kniga", groupPosition)
                intent.putExtra("glava", childPosition)
                intent.putExtra("fullglav", adapter.getChildrenCount(groupPosition))
                intent.putExtra("novyZapavet", novyZapavet)
                intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODSINOIDAL)
                startActivity(intent)
            } else {
                installFullMalitounik()
            }
            false
        }
        if (intent.extras?.getBoolean("prodolzyt", false) == true) {
            val intent1 = Intent()
            intent1.setClassName(this, MainActivity.BIBLIAACTIVITY)
            val kniga = intent.extras?.getInt("kniga") ?: 0
            intent1.putExtra("kniga", intent.extras?.getInt("kniga"))
            intent1.putExtra("glava", intent.extras?.getInt("glava"))
            intent1.putExtra("stix", intent.extras?.getInt("stix"))
            intent1.putExtra("fullglav", adapter.getChildrenCount(kniga))
            intent1.putExtra("novyZapavet", novyZapavet)
            intent1.putExtra("perevod", DialogVybranoeBibleList.PEREVODSINOIDAL)
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.subTitleToolbar.setText(R.string.bsinaidal2)
        if (novyZapavet) binding.titleToolbar.setText(R.string.novsinaidal)
        else binding.titleToolbar.setText(R.string.stary_zapaviet)
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

    private class ExpListAdapterNovyZapavietSinaidal(private val mContext: Activity, private val novyZapavet: Boolean, private val groups: ArrayList<ArrayList<String>>) : BaseExpandableListAdapter() {
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
            val rootView = GroupViewBinding.inflate(mContext.layoutInflater, parent, false)
            rootView.textGroup.text = if (novyZapavet) mContext.resources.getStringArray(R.array.sinoidaln)[groupPosition]
            else mContext.resources.getStringArray(R.array.sinoidals)[groupPosition]
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(mContext.layoutInflater, parent, false)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
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