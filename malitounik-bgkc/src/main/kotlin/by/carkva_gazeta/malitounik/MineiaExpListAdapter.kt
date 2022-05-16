package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding

class MineiaExpListAdapter(private val mContext: Activity, private val groups:ArrayList<ArrayList<MineiaDay>>) : BaseExpandableListAdapter() {
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
            val months = mContext.resources.getStringArray(R.array.meciac3)
            rootView.textGroup.text = months[groups[groupPosition][0].month]
            return rootView.root
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val rootView = ChildViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            rootView.textChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            rootView.textChild.text = groups[groupPosition][childPosition].title
            return rootView.root
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }