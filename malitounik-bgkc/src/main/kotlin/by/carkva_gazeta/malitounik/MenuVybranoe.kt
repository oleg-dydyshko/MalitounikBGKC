package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.android.synthetic.main.my_bible_list.*
import java.io.File

class MenuVybranoe : VybranoeFragment() {
    private lateinit var adapter: ItemAdapter
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun fileDeliteCancel() {
        drag_list_view.resetSwipedViews(null)
    }

    override fun fileDelite(position: Int) {
        val edit = k.edit()
        when (adapter.itemList[position].resurs) {
            "1" -> edit.remove("bibleVybranoeSemuxa")
            "2" -> edit.remove("bibleVybranoeSinoidal")
            "3" -> edit.remove("bibleVybranoeNadsan")
        }
        edit.apply()
        adapter.itemList.removeAt(position)
        activity?.let { activity ->
            val gson = Gson()
            val file = File(activity.filesDir.toString() + "/Vybranoe.json")
            file.writer().use {
                it.write(gson.toJson(adapter.itemList))
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun deliteAllVybranoe() {
        activity?.let { activity ->
            val edit = k.edit()
            edit.remove("bibleVybranoeSemuxa")
            edit.remove("bibleVybranoeSinoidal")
            edit.remove("bibleVybranoeNadsan")
            edit.apply()
            adapter.itemList.clear()
            File(activity.filesDir.toString() + "/Vybranoe.json").delete()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_vybranoe, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { fragmentActivity ->
            k = fragmentActivity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val gson = Gson()
            val file = File(fragmentActivity.filesDir.toString() + "/Vybranoe.json")
            if (file.exists()) {
                try {
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    vybranoe = gson.fromJson(file.readText(), type)
                    vybranoeSort = k.getInt("vybranoe_sort", 1)
                    vybranoe.sort()
                } catch (t: Throwable) {
                    file.delete()
                }
            }
            adapter = ItemAdapter(vybranoe, R.layout.list_item, R.id.image, false)
            drag_list_view.recyclerView.isVerticalScrollBarEnabled = false
            drag_list_view.setLayoutManager(LinearLayoutManager(fragmentActivity))
            drag_list_view.setAdapter(adapter, false)
            drag_list_view.setCanDragHorizontally(false)
            drag_list_view.setCanDragVertically(true)
            drag_list_view.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
                override fun onItemSwipeStarted(item: ListSwipeItem) {
                }

                override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                    if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                        fragmentManager?.let {
                            val adapterItem = item.tag as VybranoeData
                            val pos: Int = drag_list_view.adapter.getPositionForItem(adapterItem)
                            val dd = DialogDelite.getInstance(pos, "", "з выбранага", adapter.itemList[pos].data)
                            fragmentManager?.let { dd.show(it, "dialog_dilite") }
                        }
                    }
                }
            })
            drag_list_view.setDragListListener(object : DragListView.DragListListener {
                override fun onItemDragStarted(position: Int) {
                }

                override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
                }

                override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                    if (fromPosition != toPosition) {
                        file.writer().use {
                            it.write(gson.toJson(adapter.itemList))
                        }
                        val edit = k.edit()
                        edit.putInt("vybranoe_sort", 0)
                        edit.apply()
                        vybranoeSort = 0
                        fragmentActivity.invalidateOptionsMenu()
                    }
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.trash) {
            if (adapter.itemList.size > 0) {
                fragmentManager?.let {
                    DialogDeliteAllVybranoe().show(it, "DeliteVybranoe")
                }
            }
        }
        if (id == R.id.sortdate) {
            activity?.let { fragmentActivity ->
                val edit = k.edit()
                if (item.isChecked) {
                    edit.putInt("vybranoe_sort", 0)
                } else {
                    edit.putInt("vybranoe_sort", 1)
                }
                edit.apply()
                vybranoeSort = k.getInt("vybranoe_sort", 1)
                adapter.itemList.sort()
                val gson = Gson()
                val file = File(fragmentActivity.filesDir.toString() + "/Vybranoe.json")
                file.writer().use {
                    it.write(gson.toJson(adapter.itemList))
                }
                adapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ItemAdapter(list: ArrayList<VybranoeData>, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeData, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
            (view as ListSwipeItem).supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT
            val textview = view.findViewById<TextViewRobotoCondensed>(R.id.text)
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            textview.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                view.findViewById<TextViewRobotoCondensed>(R.id.item_left).setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.findViewById<TextViewRobotoCondensed>(R.id.item_right).setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_dark_list)
                view.setBackgroundResource(R.color.colorprimary_material_dark)
            } else {
                view.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_default_list)
                view.setBackgroundResource(R.color.colorDivider)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = mItemList[position].data
            holder.mText.text = text
            holder.itemView.tag = mItemList[position]
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].id
        }

        private inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
            var mText: TextView = itemView.findViewById(R.id.text)
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (MainActivity.checkmoduleResources(activity)) {
                    when (itemList[adapterPosition].resurs) {
                        "1" -> {
                            MyBibleList.biblia = 1
                            startActivity(Intent(activity, MyBibleList::class.java))
                        }
                        "2" -> {
                            MyBibleList.biblia = 2
                            startActivity(Intent(activity, MyBibleList::class.java))
                        }
                        "3" -> {
                            MyBibleList.biblia = 3
                            startActivity(Intent(activity, MyBibleList::class.java))
                        }
                        else -> {
                            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.Bogashlugbovya"))
                            intent.putExtra("resurs", itemList[adapterPosition].resurs)
                            intent.putExtra("title", itemList[adapterPosition].data)
                            startActivity(intent)
                        }
                    }
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dd = DialogDelite.getInstance(adapterPosition, "", "з выбранага", itemList[adapterPosition].data)
                fragmentManager?.let { dd.show(it, "dialog_dilite") }
                return true
            }
        }

        init {
            itemList = list
        }
    }

    companion object {
        var vybranoe = ArrayList<VybranoeData>()
        var vybranoeSort = 1
    }
}