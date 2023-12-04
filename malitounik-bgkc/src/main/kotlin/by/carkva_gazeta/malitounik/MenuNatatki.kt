package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MenuNatatki : BaseFragment() {
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
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuVybranoeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val file = File(activity.filesDir.toString() + "/Natatki.json")
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
            var isNatatkiError = false
            if (file.exists()) {
                try {
                    myNatatkiFiles = gson.fromJson(file.readText(), type)
                    myNatatkiFiles.forEach {
                        if (it.title == null) isNatatkiError = true
                    }
                    activity.invalidateOptionsMenu()
                } catch (_: Throwable) {
                    isNatatkiError = true
                }
            }
            if (isNatatkiError) {
                myNatatkiFiles.clear()
                File(activity.filesDir.toString().plus("/Malitva")).walk().forEach {
                    if (it.isFile) {
                        val name = it.name
                        val t1 = name.lastIndexOf("_")
                        val index = name.substring(t1 + 1).toLong()
                        val inputStream = FileReader(it)
                        val reader = BufferedReader(inputStream)
                        val res = reader.readText().split("<MEMA></MEMA>")
                        inputStream.close()
                        var lRTE: Long = 1
                        if (res[1].contains("<RTE></RTE>")) {
                            val start = res[1].indexOf("<RTE></RTE>")
                            val end = res[1].length
                            lRTE = res[1].substring(start + 11, end).toLong()
                        }
                        if (lRTE <= 1) {
                            lRTE = it.lastModified()
                        }
                        myNatatkiFiles.add(MyNatatkiFiles(index, lRTE, res[0]))
                    }
                }
                file.writer().use {
                    it.write(gson.toJson(myNatatkiFiles, type))
                }
            }
            myNatatkiFiles.sort()
            activity.invalidateOptionsMenu()
            adapter = ItemAdapter(activity, R.id.image, false)
            binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
            binding.dragListView.setLayoutManager(LinearLayoutManager(activity))
            binding.dragListView.setAdapter(adapter, true)
            binding.dragListView.setCanDragHorizontally(false)
            binding.dragListView.setCanDragVertically(true)
            binding.dragListView.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
                override fun onItemSwipeStarted(item: ListSwipeItem) {
                }

                override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                    val adapterItem = item.tag as MyNatatkiFiles
                    val pos = binding.dragListView.adapter.getPositionForItem(adapterItem)
                    if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                        onDialogDeliteClick(pos, adapter.itemList[pos].title ?: "")
                    }
                    if (swipedDirection == ListSwipeItem.SwipeDirection.RIGHT) {
                        myNatatkiEdit(pos)
                    }
                }
            })
            binding.dragListView.setDragListListener(object : DragListView.DragListListener {
                override fun onItemDragStarted(position: Int) {
                    myNatatkiFiles.clear()
                    myNatatkiFiles.addAll(gson.fromJson(file.readText(), type))
                }

                override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
                }

                override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                    if (fromPosition != toPosition) {
                        file.writer().use {
                            it.write(gson.toJson(adapter.itemList, type))
                        }
                    }
                    val edit = k.edit()
                    edit.putInt("natatki_sort", 0)
                    edit.apply()
                    activity.invalidateOptionsMenu()
                }
            })
        }
    }

    fun fileDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    fun myNatatkiEdit(position: Int) {
        binding.dragListView.resetSwipedViews(null)
        val f = adapter.itemList[position]
        activity?.let {
            if (File("${it.filesDir}/Malitva/Mae_malitvy_${f.id}").exists()) {
                val myNatatki = MyNatatki.getInstance("Mae_malitvy_" + f.id, 2, position)
                myNatatki.show(childFragmentManager, "myNatatki")
            } else {
                MainActivity.toastView(it, getString(R.string.no_file))
            }
        }
    }

    fun myNatatkiAdd() {
        val myNatatkiFilesSort = k.getInt("natatki_sort", 0)
        if (myNatatkiFilesSort == 0) {
            activity?.let {
                myNatatkiFiles.clear()
                val file = File(it.filesDir.toString() + "/Natatki.json")
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
                myNatatkiFiles.addAll(gson.fromJson(file.readText(), type))
            }
        }
        myNatatkiFiles.sort()
        if (k.getBoolean("help_main_list_view", true) && myNatatkiFiles.size > 1) {
            val dialogHelpListView = DialogHelpListView()
            dialogHelpListView.show(childFragmentManager, "DialogHelpListView")
            val prefEditors = k.edit()
            prefEditors.putBoolean("help_main_list_view", false)
            prefEditors.apply()
        }
        adapter.updateList(myNatatkiFiles)
    }

    fun fileDelite(position: Int) {
        activity?.let { fragmentActivity ->
            val f = adapter.itemList[position]
            adapter.itemList.removeAt(position)
            val filedel = File(fragmentActivity.filesDir.toString().plus("/Malitva/").plus("Mae_malitvy_").plus(f.id))
            if (filedel.exists()) filedel.delete()
            val file = File(fragmentActivity.filesDir.toString().plus("/Natatki.json"))
            file.writer().use {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
                it.write(gson.toJson(adapter.itemList, type))
            }
            adapter.notifyItemRemoved(position)
        }
    }

    fun onDialogDeliteClick(position: Int, name: String) {
        val dd = DialogDelite.getInstance(position, "", "нататку", name)
        dd.show(childFragmentManager, "dialog_delite")
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_add) {
            binding.dragListView.resetSwipedViews(null)
            val myNatatki = MyNatatki.getInstance("", 1, 0)
            myNatatki.show(childFragmentManager, "myNatatki")
            return true
        }
        if (id == R.id.sortdate) {
            activity?.let { activity ->
                val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                if (item.isChecked) {
                    prefEditors.putInt("natatki_sort", 0)
                    prefEditors.apply()
                    myNatatkiFiles.clear()
                    val file = File(activity.filesDir.toString() + "/Natatki.json")
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
                    myNatatkiFiles.addAll(gson.fromJson(file.readText(), type))
                } else {
                    prefEditors.putInt("natatki_sort", 1)
                    prefEditors.apply()
                }
                activity.invalidateOptionsMenu()
                myNatatkiFiles.sort()
                adapter.updateList(myNatatkiFiles)
            }
            return true
        }
        if (id == R.id.sorttime) {
            activity?.let { activity ->
                val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                if (item.isChecked) {
                    prefEditors.putInt("natatki_sort", 0)
                    prefEditors.apply()
                    myNatatkiFiles.clear()
                    val file = File(activity.filesDir.toString() + "/Natatki.json")
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, MyNatatkiFiles::class.java).type
                    myNatatkiFiles.addAll(gson.fromJson(file.readText(), type))
                } else {
                    prefEditors.putInt("natatki_sort", 2)
                    prefEditors.apply()
                }
                activity.invalidateOptionsMenu()
                myNatatkiFiles.sort()
                adapter.updateList(myNatatkiFiles)
            }
            return true
        }
        if (id == R.id.action_carkva) {
            (activity as? BaseActivity)?.let {
                if (it.checkmodulesAdmin()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.PASOCHNICALIST)
                    startActivity(intent)
                } else {
                    MainActivity.toastView(it, getString(R.string.error))
                }
            }
            return true
        }
        return false
    }

    private inner class ItemAdapter(private val activity: Activity, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<MyNatatkiFiles, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
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
            val text = mItemList[position].title
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
                binding.dragListView.resetSwipedViews(null)
                val f = itemList[bindingAdapterPosition]
                if (File("${activity.filesDir}/Malitva/Mae_malitvy_${f.id}").exists()) {
                    val myNatatki = MyNatatki.getInstance("Mae_malitvy_" + f.id, 3, bindingAdapterPosition)
                    myNatatki.show(childFragmentManager, "myNatatki")
                } else {
                    MainActivity.toastView(activity, getString(R.string.no_file))
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val contextMenu = DialogContextMenu.getInstance(bindingAdapterPosition, itemList[bindingAdapterPosition].title ?: "")
                contextMenu.show(childFragmentManager, "context_menu")
                return true
            }
        }

        fun updateList(newMyNatatkiFiles: ArrayList<MyNatatkiFiles>) {
            itemList = newMyNatatkiFiles
        }

        init {
            updateList(myNatatkiFiles)
        }
    }

    companion object {
        var myNatatkiFiles = ArrayList<MyNatatkiFiles>()
    }
}