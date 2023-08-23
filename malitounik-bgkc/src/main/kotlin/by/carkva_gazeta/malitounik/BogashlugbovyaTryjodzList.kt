package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BogashlugbovyaTryjodzList : BaseActivity() {
    private var data = ArrayList<ArrayList<SlugbovyiaTextuData>>()
    private var mLastClickTime: Long = 0
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = getBaseDzenNoch()
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
        binding.titleToolbar.text = intent?.extras?.getString("title", getString(R.string.tryjodz)) ?: getString(R.string.tryjodz)
        val sluzba = SlugbovyiaTextu()
        var array = ArrayList<SlugbovyiaTextuData>()
        var day = ""
        when (intent?.extras?.getInt("tryjodz", 0) ?: 0) {
            1 -> {
                sluzba.getVilikiTydzen().forEachIndexed { index, it ->
                    if (index == 0) day = it.title
                    if (day != it.title) {
                        data.add(array)
                        array = ArrayList()
                    }
                    array.add(it)
                    day = it.title
                    if (sluzba.getVilikiTydzen().count() == index + 1) {
                        data.add(array)
                    }
                }
            }

            2 -> {
                sluzba.getSvetlyTydzen().forEachIndexed { index, it ->
                    if (index == 0) day = it.title
                    if (day != it.title) {
                        data.add(array)
                        array = ArrayList()
                    }
                    array.add(it)
                    day = it.title
                    if (sluzba.getSvetlyTydzen().count() == index + 1) {
                        data.add(array)
                    }
                }
            }

            else -> {
                sluzba.getMineiaSviatochnaia().forEachIndexed { index, it ->
                    if (index == 0) day = it.title
                    if (day != it.title) {
                        data.add(array)
                        array = ArrayList()
                    }
                    array.add(it)
                    day = it.title
                    if (sluzba.getMineiaSviatochnaia().count() == index + 1) {
                        data.add(array)
                    }
                }
            }
        }
        binding.elvMain.setAdapter(ExpListAdapter(this, data))
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                intent.putExtra("title", data[groupPosition][childPosition].title)
                intent.putExtra("resurs", data[groupPosition][childPosition].resource)
                startActivity(intent)
            } else {
                installFullMalitounik()
            }
            false
        }
        data.forEachIndexed { index, _ ->
            binding.elvMain.expandGroup(index)
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

    private class ExpListAdapter(private val mContext: Activity, private val groups: ArrayList<ArrayList<SlugbovyiaTextuData>>) : BaseExpandableListAdapter() {
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
            rootView.textGroup.text = groups[groupPosition][0].title.uppercase()
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            var opisanie = when (groups[groupPosition][childPosition].sluzba) {
                SlugbovyiaTextu.JUTRAN -> "Ютрань"
                SlugbovyiaTextu.LITURHIJA -> "Літургія"
                SlugbovyiaTextu.VIACZERNIA -> "Вячэрня"
                SlugbovyiaTextu.VIALHADZINY -> "Вялікія гадзіны"
                SlugbovyiaTextu.PAVIACHERNICA -> "Малая павячэрніца"
                SlugbovyiaTextu.PAUNOCHNICA -> "Паўночніца"
                else -> ""
            }
            if (groups[groupPosition][childPosition].day == -2 && groups[groupPosition][childPosition].sluzba == SlugbovyiaTextu.JUTRAN) {
                opisanie = "Ютрань (12 Евангельляў Мукаў Хрыстовых)"
            }
            rootView.textChild.text = opisanie
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}
