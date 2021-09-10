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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
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

class MenuVybranoe : VybranoeFragment(), DialogVybranoeBibleList.DialogVybranoeBibleListListener {
    private lateinit var adapter: ItemAdapter
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences
    private var _binding: MenuVybranoeBinding? = null
    private val binding get() = _binding!!
    private val menuVybranoeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 200) {
            adapter.updateList(vybranoe)
            val editDzenNoch = result.data?.extras?.getBoolean("editDzenNoch", false) ?: false
            if (editDzenNoch) activity?.recreate()
        }
    }

    override fun onAllDeliteBible() {
        adapter.updateList(vybranoe)
    }

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
            adapter.notifyItemRemoved(position)
        }
    }

    private fun checkBibleVybranoe() {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
        var bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
        var indexVybranoe = 0
        var remove = false
        if (bibleVybranoe != "") {
            val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
            if (arrayListVybranoe.isEmpty()) {
                vybranoe.forEachIndexed { index, vybranoeData ->
                    if (vybranoeData.resurs == "1") {
                        indexVybranoe = index
                        remove = true
                        return@forEachIndexed
                    }
                }
            }
        } else {
            vybranoe.forEachIndexed { index, vybranoeData ->
                if (vybranoeData.resurs == "1") {
                    indexVybranoe = index
                    remove = true
                    return@forEachIndexed
                }
            }
        }
        if (remove) {
            vybranoe.removeAt(indexVybranoe)
            remove = false
        }
        bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
        if (bibleVybranoe != "") {
            val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
            if (arrayListVybranoe.isEmpty()) {
                vybranoe.forEachIndexed { index, vybranoeData ->
                    if (vybranoeData.resurs == "2") {
                        indexVybranoe = index
                        remove = true
                        return@forEachIndexed
                    }
                }
            }
        } else {
            vybranoe.forEachIndexed { index, vybranoeData ->
                if (vybranoeData.resurs == "2") {
                    indexVybranoe = index
                    remove = true
                    return@forEachIndexed
                }
            }
        }
        if (remove) {
            vybranoe.removeAt(indexVybranoe)
            remove = false
        }
        bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
        if (bibleVybranoe != "") {
            val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
            if (arrayListVybranoe.isEmpty()) {
                vybranoe.forEachIndexed { index, vybranoeData ->
                    if (vybranoeData.resurs == "3") {
                        indexVybranoe = index
                        remove = true
                        return@forEachIndexed
                    }
                }
            }
        } else {
            vybranoe.forEachIndexed { index, vybranoeData ->
                if (vybranoeData.resurs == "3") {
                    indexVybranoe = index
                    remove = true
                    return@forEachIndexed
                }
            }
        }
        if (remove) {
            vybranoe.removeAt(indexVybranoe)
            remove = false
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
            adapter.updateList(vybranoe)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuVybranoeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { fragmentActivity ->
            k = fragmentActivity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val gson = Gson()
            val file = File(fragmentActivity.filesDir.toString() + "/Vybranoe.json")
            if (file.exists()) {
                try {
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    vybranoe = gson.fromJson(file.readText(), type)
                } catch (t: Throwable) {
                    vybranoe = ArrayList()
                    file.writer().use {
                        it.write(gson.toJson(vybranoe))
                    }
                }
            }
            checkBibleVybranoe()
            vybranoeSort = k.getInt("vybranoe_sort", 1)
            vybranoe.sort()
            adapter = ItemAdapter(R.id.image, false)
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
                        val adapterItem = item.tag as VybranoeData
                        val pos: Int = binding.dragListView.adapter.getPositionForItem(adapterItem)
                        val dd = DialogDelite.getInstance(pos, "", "з выбранага", adapter.itemList[pos].data)
                        dd.show(childFragmentManager, "dialog_dilite")
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
                DialogDeliteAllVybranoe().show(childFragmentManager, "DeliteVybranoe")
            }
        }
        if (id == R.id.sortdate) {
            activity?.let { fragmentActivity ->
                val edit = k.edit()
                if (item.isChecked) {
                    edit.putInt("vybranoe_sort", 0)
                    edit.apply()
                    val gson = Gson()
                    val file = File(fragmentActivity.filesDir.toString() + "/Vybranoe.json")
                    if (file.exists()) {
                        try {
                            vybranoe.clear()
                            val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                            vybranoe.addAll(gson.fromJson(file.readText(), type))
                            vybranoeSort = 0
                        } catch (t: Throwable) {
                            file.delete()
                        }
                    }
                } else {
                    edit.putInt("vybranoe_sort", 1)
                    edit.apply()
                    vybranoeSort = 1
                }
                vybranoe.sort()
                adapter.updateList(vybranoe)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ItemAdapter(private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeData, ItemAdapter.ViewHolder>() {
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
                view.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
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
                if (itemList[adapterPosition].resurs.contains("pesny")) {
                    activity?.let {
                        val intent = Intent(it, PesnyAll::class.java)
                        intent.putExtra("type", itemList[adapterPosition].resurs)
                        intent.putExtra("pesny", itemList[adapterPosition].data)
                        startActivity(intent)
                    }
                } else {
                    if (MainActivity.checkmoduleResources()) {
                        when (itemList[adapterPosition].resurs) {
                            "1" -> {
                                DialogVybranoeBibleList.biblia = 1
                                val dialogVybranoeList = DialogVybranoeBibleList()
                                dialogVybranoeList.setDialogVybranoeBibleListListener(this@MenuVybranoe)
                                dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                            }
                            "2" -> {
                                DialogVybranoeBibleList.biblia = 2
                                val dialogVybranoeList = DialogVybranoeBibleList()
                                dialogVybranoeList.setDialogVybranoeBibleListListener(this@MenuVybranoe)
                                dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                            }
                            "3" -> {
                                DialogVybranoeBibleList.biblia = 3
                                val dialogVybranoeList = DialogVybranoeBibleList()
                                dialogVybranoeList.setDialogVybranoeBibleListListener(this@MenuVybranoe)
                                dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                            }
                            else -> {
                                activity?.let {
                                    val intent = Intent()
                                    intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                    intent.putExtra("resurs", itemList[adapterPosition].resurs)
                                    intent.putExtra("title", itemList[adapterPosition].data)
                                    menuVybranoeLauncher.launch(intent)
                                }
                            }
                        }
                    } else {
                        val dadatak = DialogInstallDadatak()
                        dadatak.show(childFragmentManager, "dadatak")
                    }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dd = DialogDelite.getInstance(adapterPosition, "", "з выбранага", itemList[adapterPosition].data)
                dd.show(childFragmentManager, "dialog_dilite")
                return true
            }
        }

        fun updateList(newVybranoe: ArrayList<VybranoeData>) {
            val diffCallback = RecyclerViewDiffCallback(vybranoe, newVybranoe)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            itemList = newVybranoe
        }

        init {
            itemList = vybranoe
        }
    }

    private class RecyclerViewDiffCallback(private val oldArrayList: ArrayList<VybranoeData>, private val newArrayList: ArrayList<VybranoeData>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldArrayList.size
        }

        override fun getNewListSize(): Int {
            return newArrayList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldArrayList[oldItemPosition] == newArrayList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldArrayList[oldItemPosition] == newArrayList[newItemPosition]
        }
    }

    companion object {
        var vybranoe = ArrayList<VybranoeData>()
        var vybranoeSort = 1
    }
}