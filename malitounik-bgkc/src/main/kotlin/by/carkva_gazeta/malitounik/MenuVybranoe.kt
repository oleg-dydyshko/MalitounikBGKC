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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
import by.carkva_gazeta.malitounik.databinding.MenuVybranoeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import java.io.File

class MenuVybranoe : VybranoeFragment() {
    private lateinit var adapter: ItemAdapter
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences
    private var _binding: MenuVybranoeBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun fileDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
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
        _binding = MenuVybranoeBinding.inflate(inflater, container, false)
        return binding.root
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
            adapter = ItemAdapter(vybranoe, R.id.image, false)
            binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
            binding.dragListView.setLayoutManager(LinearLayoutManager(fragmentActivity))
            binding.dragListView.setAdapter(adapter, false)
            binding.dragListView.setCanDragHorizontally(false)
            binding.dragListView.setCanDragVertically(true)
            binding.dragListView.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
                override fun onItemSwipeStarted(item: ListSwipeItem) {
                }

                override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                    if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                        fragmentManager?.let {
                            val adapterItem = item.tag as VybranoeData
                            val pos: Int = binding.dragListView.adapter.getPositionForItem(adapterItem)
                            val dd = DialogDelite.getInstance(pos, "", "з выбранага", adapter.itemList[pos].data)
                            fragmentManager?.let { dd.show(it, "dialog_dilite") }
                        }
                    }
                }
            })
            binding.dragListView.setDragListListener(object : DragListView.DragListListener {
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

    private inner class ItemAdapter(list: ArrayList<VybranoeData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeData, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.root.supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                view.itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark)
            } else {
                view.itemLayout.setBackgroundResource(R.drawable.selector_default_list)
                view.root.setBackgroundResource(R.color.colorDivider)
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

        private inner class ViewHolder(itemView: ListItemBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (MainActivity.checkmoduleResources(activity)) {
                    when (itemList[adapterPosition].resurs) {
                        "1" -> {
                            VybranoeBibleList.biblia = 1
                            startActivity(Intent(activity, VybranoeBibleList::class.java))
                        }
                        "2" -> {
                            VybranoeBibleList.biblia = 2
                            startActivity(Intent(activity, VybranoeBibleList::class.java))
                        }
                        "3" -> {
                            VybranoeBibleList.biblia = 3
                            startActivity(Intent(activity, VybranoeBibleList::class.java))
                        }
                        else -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("resurs", itemList[adapterPosition].resurs)
                                intent.putExtra("title", itemList[adapterPosition].data)
                                startActivity(intent)
                            }
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