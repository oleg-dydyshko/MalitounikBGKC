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
internal class ExpListAdapterStaryZapaviet(private val mContext: Activity, private val mGroups: ArrayList<ArrayList<String>>) : BaseExpandableListAdapter() {
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
            0 -> textGroup.text = "Быцьцё"
            1 -> textGroup.text = "Выхад"
            2 -> textGroup.text = "Лявіт"
            3 -> textGroup.text = "Лікі"
            4 -> textGroup.text = "Другі Закон"
            5 -> textGroup.text = "Ісуса сына Нава"
            6 -> textGroup.text = "Судзьдзяў"
            7 -> textGroup.text = "Рут"
            8 -> textGroup.text = "1-я Царстваў"
            9 -> textGroup.text = "2-я Царстваў"
            10 -> textGroup.text = "3-я Царстваў"
            11 -> textGroup.text = "4-я Царстваў"
            12 -> textGroup.text = "1-я Летапісаў"
            13 -> textGroup.text = "2-я Летапісаў"
            14 -> textGroup.text = "Эздры"
            15 -> textGroup.text = "Нээміі"
            16 -> textGroup.text = "Эстэр"
            17 -> textGroup.text = "Ёва"
            18 -> textGroup.text = "Псалтыр"
            19 -> textGroup.text = "Выслоўяў Саламонавых"
            20 -> textGroup.text = "Эклезіяста"
            21 -> textGroup.text = "Найвышэйшая Песьня Саламонава"
            22 -> textGroup.text = "Ісаі"
            23 -> textGroup.text = "Ераміі"
            24 -> textGroup.text = "Ераміін Плач"
            25 -> textGroup.text = "Езэкііля"
            26 -> textGroup.text = "Данііла"
            27 -> textGroup.text = "Асіі"
            28 -> textGroup.text = "Ёіля"
            29 -> textGroup.text = "Амоса"
            30 -> textGroup.text = "Аўдзея"
            31 -> textGroup.text = "Ёны"
            32 -> textGroup.text = "Міхея"
            33 -> textGroup.text = "Навума"
            34 -> textGroup.text = "Абакума"
            35 -> textGroup.text = "Сафона"
            36 -> textGroup.text = "Агея"
            37 -> textGroup.text = "Захарыі"
            38 -> textGroup.text = "Малахіі"
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