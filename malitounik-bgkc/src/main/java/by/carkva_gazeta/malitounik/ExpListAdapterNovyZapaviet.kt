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
internal class ExpListAdapterNovyZapaviet(private val mContext: Activity, private val mGroups: ArrayList<ArrayList<String>>) : BaseExpandableListAdapter() {
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
            0 -> textGroup.text = "Паводле Мацьвея"
            1 -> textGroup.text = "Паводле Марка"
            2 -> textGroup.text = "Паводле Лукаша"
            3 -> textGroup.text = "Паводле Яна"
            4 -> textGroup.text = "Дзеі Апосталаў"
            5 -> textGroup.text = "Якава"
            6 -> textGroup.text = "1-е Пятра"
            7 -> textGroup.text = "2-е Пятра"
            8 -> textGroup.text = "1-е Яна Багаслова"
            9 -> textGroup.text = "2-е Яна Багаслова"
            10 -> textGroup.text = "3-е Яна Багаслова"
            11 -> textGroup.text = "Юды"
            12 -> textGroup.text = "Да Рымлянаў"
            13 -> textGroup.text = "1-е да Карынфянаў"
            14 -> textGroup.text = "2-е да Карынфянаў"
            15 -> textGroup.text = "Да Галятаў"
            16 -> textGroup.text = "Да Эфэсянаў"
            17 -> textGroup.text = "Да Піліпянаў"
            18 -> textGroup.text = "Да Каласянаў"
            19 -> textGroup.text = "1-е да Фесаланікійцаў"
            20 -> textGroup.text = "2-е да Фесаланікійцаў"
            21 -> textGroup.text = "1-е да Цімафея"
            22 -> textGroup.text = "2-е да Цімафея"
            23 -> textGroup.text = "Да Ціта"
            24 -> textGroup.text = "Да Філімона"
            25 -> textGroup.text = "Да Габрэяў"
            26 -> textGroup.text = "Адкрыцьцё (Апакаліпсіс)"
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