package by.carkva_gazeta.resources

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.recyclerview.widget.GridLayoutManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.ChildViewListBinding
import by.carkva_gazeta.malitounik.databinding.DialogBibleRazdelItemBinding
import by.carkva_gazeta.malitounik.databinding.GroupViewBinding
import com.woxthebox.draglistview.DragItemAdapter

class BibliaAdapterList(private val mContext: BaseActivity, private val groups: ArrayList<ArrayList<BibliaAdapterData>>) : BaseExpandableListAdapter() {
    private var mLastClickTime: Long = 0
    private var lastExpandedGroupPosition = 0
    private var mListener: BibliaAdapterListListener? = null

    interface BibliaAdapterListListener {
        fun onComplete(groupPosition: Int, childPosition: Int)
        fun collapseGroup(groupPosition: Int)
    }

    fun setBibliaAdapterListListener(listener: BibliaAdapterListListener) {
        mListener = listener
    }

    override fun onGroupExpanded(groupPosition: Int) {
        if (groupPosition != lastExpandedGroupPosition) {
            mListener?.collapseGroup(lastExpandedGroupPosition)
        }
        super.onGroupExpanded(groupPosition)
        lastExpandedGroupPosition = groupPosition
    }

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

    private fun calculateNoOfColumns(): Int {
        val displayMetrics = mContext.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val scalingFactor = 50
        val columnCount = (dpWidth / scalingFactor).toInt()
        return (if (columnCount >= 2) columnCount else 2)
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val rootView = GroupViewBinding.inflate(mContext.layoutInflater, parent, false)
        rootView.textGroup.text = groups[groupPosition][0].titleKniga
        return rootView.root
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val rootView = ChildViewListBinding.inflate(mContext.layoutInflater, parent, false)
        val glm = GridLayoutManager(mContext, calculateNoOfColumns())
        rootView.dragGridView.setLayoutManager(glm)
        val mItemArray = ArrayList<ListItem>()
        val fullGlav = groups[groupPosition][childPosition].glav
        for (i in 1..fullGlav) mItemArray.add(ListItem(groupPosition, i))
        val listAdapter = ItemAdapter(mItemArray, R.id.item_layout, true)
        rootView.dragGridView.setAdapter(listAdapter, true)
        rootView.dragGridView.setCustomDragItem(null)
        rootView.dragGridView.isDragEnabled = false
        return rootView.root
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private inner class ItemAdapter(list: ArrayList<ListItem>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<ListItem, ItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = DialogBibleRazdelItemBinding.inflate(mContext.layoutInflater, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = mItemList[position].childPosition
            holder.mText.text = text.toString()
            holder.itemView.tag = mItemList[position]
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].childPosition.toLong()
        }

        private inner class ViewHolder(itemView: DialogBibleRazdelItemBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mListener?.onComplete(itemList[bindingAdapterPosition].groupPosition, itemList[bindingAdapterPosition].childPosition - 1)
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        }

        init {
            itemList = list
        }
    }

    private data class ListItem(val groupPosition: Int, val childPosition: Int)
}