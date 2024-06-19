package by.carkva_gazeta.resources

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.ChildViewBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding

class BibliaAdapterList(private val mContext: BaseActivity, private val groups: ArrayList<ArrayList<BibliaAdapterData>>) : BaseExpandableListAdapter() {
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
        rootView.textGroup.text = groups[groupPosition][0].titleKniga
        return rootView.root
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val rootView = ChildViewBinding.inflate(mContext.layoutInflater, parent, false)
        val dzenNoch = mContext.getBaseDzenNoch()
        if (dzenNoch)
            rootView.textChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        rootView.textChild.text = groups[groupPosition][childPosition].glava
        return rootView.root
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}