package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.core.content.ContextCompat
import java.util.*

/**
 * Created by oleg on 3.10.16
 */
internal class ExpListAdapterStaryZapavietSinaidal(private val mContext: Activity, private val mGroups: ArrayList<ArrayList<String>>) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return mGroups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mGroups[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return mGroups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mGroups[groupPosition][childPosition]
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
            0 -> textGroup.text = "Бытие"
            1 -> textGroup.text = "Исход"
            2 -> textGroup.text = "Левит"
            3 -> textGroup.text = "Числа"
            4 -> textGroup.text = "Второзаконие"
            5 -> textGroup.text = "Иисуса Навина"
            6 -> textGroup.text = "Судей израилевых"
            7 -> textGroup.text = "Руфи"
            8 -> textGroup.text = "1-я Царств"
            9 -> textGroup.text = "2-я Царств"
            10 -> textGroup.text = "3-я Царств"
            11 -> textGroup.text = "4-я Царств"
            12 -> textGroup.text = "1-я Паралипоменон"
            13 -> textGroup.text = "2-я Паралипоменон"
            14 -> textGroup.text = "1-я Ездры"
            15 -> textGroup.text = "Неемии"
            16 -> textGroup.text = "2-я Ездры"
            17 -> textGroup.text = "Товита"
            18 -> textGroup.text = "Иудифи"
            19 -> textGroup.text = "Есфири"
            20 -> textGroup.text = "Иова"
            21 -> textGroup.text = "Псалтирь"
            22 -> textGroup.text = "Притчи Соломона"
            23 -> textGroup.text = "Екклезиаста"
            24 -> textGroup.text = "Песнь песней Соломона"
            25 -> textGroup.text = "Премудрости Соломона"
            26 -> textGroup.text = "Премудрости Иисуса, сына Сирахова"
            27 -> textGroup.text = "Исаии"
            28 -> textGroup.text = "Иеремии"
            29 -> textGroup.text = "Плач Иеремии"
            30 -> textGroup.text = "Послание Иеремии"
            31 -> textGroup.text = "Варуха"
            32 -> textGroup.text = "Иезекииля"
            33 -> textGroup.text = "Даниила"
            34 -> textGroup.text = "Осии"
            35 -> textGroup.text = "Иоиля"
            36 -> textGroup.text = "Амоса"
            37 -> textGroup.text = "Авдия"
            38 -> textGroup.text = "Ионы"
            39 -> textGroup.text = "Михея"
            40 -> textGroup.text = "Наума"
            41 -> textGroup.text = "Аввакума"
            42 -> textGroup.text = "Сафонии"
            43 -> textGroup.text = "Аггея"
            44 -> textGroup.text = "Захарии"
            45 -> textGroup.text = "Малахии"
            46 -> textGroup.text = "1-я Маккавейская"
            47 -> textGroup.text = "2-я Маккавейская"
            48 -> textGroup.text = "3-я Маккавейская"
            49 -> textGroup.text = "3-я Ездры"
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
        textChild.text = mGroups[groupPosition][childPosition]
        return rootView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}