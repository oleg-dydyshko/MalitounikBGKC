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

class MenuVybranoe : BaseFragment(), DialogVybranoeBibleList.DialogVybranoeBibleListListener {
    private lateinit var adapter: ItemAdapter
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences
    private var binding: MenuVybranoeBinding? = null
    private val menuVybranoeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 200) {
            adapter.updateList(vybranoe)
        }
    }

    override fun onAllDeliteBible() {
        adapter.updateList(vybranoe)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun fileDeliteCancel() {
        binding?.dragListView?.resetSwipedViews(null)
    }

    fun fileDelite(position: Int) {
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
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
            val file = File(activity.filesDir.toString() + "/Vybranoe.json")
            file.writer().use {
                it.write(gson.toJson(adapter.itemList, type))
            }
            adapter.notifyItemRemoved(position)
        }
    }

    private fun checkBibleVybranoe() {
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
        var bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
        var indexVybranoe = 0
        var remove = false
        if (bibleVybranoe != "") {
            try {
                val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
                if (arrayListVybranoe.isEmpty()) {
                    vybranoe.forEachIndexed { index, vybranoeData ->
                        if (vybranoeData.resurs == "1") {
                            indexVybranoe = index
                            remove = true
                            return@forEachIndexed
                        }
                    }
                } else {
                    var isResurs = false
                    vybranoe.forEach {
                        if (it.resurs == "1") isResurs = true
                    }
                    if (!isResurs) {
                        vybranoe.add(0, VybranoeData(PesnyAll.vybranoeIndex(), "1", getString(R.string.title_biblia)))
                    }
                }
            } catch (e: Throwable) {
                val edit = k.edit()
                edit.remove("bibleVybranoeSemuxa")
                edit.apply()
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
            try {
                val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
                if (arrayListVybranoe.isEmpty()) {
                    vybranoe.forEachIndexed { index, vybranoeData ->
                        if (vybranoeData.resurs == "2") {
                            indexVybranoe = index
                            remove = true
                            return@forEachIndexed
                        }
                    }
                } else {
                    var isResurs = false
                    vybranoe.forEach {
                        if (it.resurs == "2") isResurs = true
                    }
                    if (!isResurs) {
                        vybranoe.add(0, VybranoeData(PesnyAll.vybranoeIndex(), "2", getString(R.string.bsinaidal)))
                    }
                }
            } catch (e: Throwable) {
                val edit = k.edit()
                edit.remove("bibleVybranoeSinoidal")
                edit.apply()
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
            try {
                val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
                if (arrayListVybranoe.isEmpty()) {
                    vybranoe.forEachIndexed { index, vybranoeData ->
                        if (vybranoeData.resurs == "3") {
                            indexVybranoe = index
                            remove = true
                            return@forEachIndexed
                        }
                    }
                } else {
                    var isResurs = false
                    vybranoe.forEach {
                        if (it.resurs == "3") isResurs = true
                    }
                    if (!isResurs) {
                        vybranoe.add(0, VybranoeData(PesnyAll.vybranoeIndex(), "3", getString(R.string.title_psalter)))
                    }
                }
            } catch (e: Throwable) {
                val edit = k.edit()
                edit.remove("bibleVybranoeNadsan")
                edit.apply()
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

    fun deliteAllVybranoe() {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MenuVybranoeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { fragmentActivity ->
            binding?.let { binding ->
                k = fragmentActivity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val gson = Gson()
                val file = File(fragmentActivity.filesDir.toString() + "/Vybranoe.json")
                if (file.exists() && vybranoe.isEmpty()) {
                    try {
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                        vybranoe.addAll(gson.fromJson(file.readText(), type))
                    } catch (_: Throwable) {
                        MainActivity.toastView(fragmentActivity, getString(R.string.error_ch2))
                    }
                }
                checkBibleVybranoe()
                vybranoeSort = k.getInt("vybranoe_sort", 1)
                vybranoe.sort()
                adapter = ItemAdapter(fragmentActivity as BaseActivity, R.id.image, false)
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
                            val pos = binding.dragListView.adapter.getPositionForItem(adapterItem)
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
                            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                            file.writer().use {
                                it.write(gson.toJson(adapter.itemList, type))
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
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.trash) {
            if (adapter.itemList.size > 0) {
                DialogDeliteAllVybranoe().show(childFragmentManager, "DeliteVybranoe")
            }
            return true
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
                            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                            vybranoe.addAll(gson.fromJson(file.readText(), type))
                            vybranoeSort = 0
                        } catch (_: Throwable) {
                        }
                    }
                } else {
                    edit.putInt("vybranoe_sort", 1)
                    edit.apply()
                    vybranoeSort = 1
                }
                vybranoe.sort()
                adapter.updateList(vybranoe)
                return true
            }
        }
        return false
    }

    private inner class ItemAdapter(private val activity: BaseActivity, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeData, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.root.supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT
            val dzenNoch = activity.getBaseDzenNoch()
            if (dzenNoch) {
                view.itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
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
            val mText = itemView.text
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    when (itemList[bindingAdapterPosition].resurs) {
                        "1" -> {
                            DialogVybranoeBibleList.biblia = "1"
                            val dialogVybranoeList = DialogVybranoeBibleList()
                            dialogVybranoeList.setDialogVybranoeBibleListListener(this@MenuVybranoe)
                            dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                        }

                        "2" -> {
                            DialogVybranoeBibleList.biblia = "2"
                            val dialogVybranoeList = DialogVybranoeBibleList()
                            dialogVybranoeList.setDialogVybranoeBibleListListener(this@MenuVybranoe)
                            dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                        }

                        "3" -> {
                            DialogVybranoeBibleList.biblia = "3"
                            val dialogVybranoeList = DialogVybranoeBibleList()
                            dialogVybranoeList.setDialogVybranoeBibleListListener(this@MenuVybranoe)
                            dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                        }

                        else -> {
                            val intent = Intent()
                            intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                            intent.putExtra("resurs", itemList[bindingAdapterPosition].resurs)
                            intent.putExtra("title", itemList[bindingAdapterPosition].data)
                            if (itemList.size > 1) {
                                intent.putExtra("isVybranae", true)
                                intent.putExtra("vybranaePos", bindingAdapterPosition)
                            }
                            intent.putExtra("chekVybranoe", true)
                            menuVybranoeLauncher.launch(intent)
                        }
                    }
                } else {
                    activity.installFullMalitounik()
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dd = DialogDelite.getInstance(bindingAdapterPosition, "", "з выбранага", itemList[bindingAdapterPosition].data)
                dd.show(childFragmentManager, "dialog_dilite")
                return true
            }
        }

        fun updateList(newVybranoe: ArrayList<VybranoeData>) {
            itemList = newVybranoe
        }

        init {
            updateList(vybranoe)
        }
    }

    companion object {
        val vybranoe = ArrayList<VybranoeData>()
        var vybranoeSort = 1
    }
}