package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.*
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
import kotlinx.android.synthetic.main.vybranoe_bible_list.*
import java.io.File

class MenuNatatki : NatatkiFragment() {
    private lateinit var adapter: ItemAdapter
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_vybranoe, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val file = File(activity.filesDir.toString() + "/Natatki.json")
            if (file.exists()) {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<MyNatatkiFiles>>() {}.type
                    myNatatkiFiles = gson.fromJson(file.readText(), type)
                    myNatatkiFilesSort = k.getInt("natatki_sort", 0)
                    myNatatkiFiles.sort()
                    activity.invalidateOptionsMenu()
                } catch (t: Throwable) {
                    file.delete()
                }
            }
            adapter = ItemAdapter(myNatatkiFiles, R.layout.list_item, R.id.image, false)
            drag_list_view.recyclerView.isVerticalScrollBarEnabled = false
            drag_list_view.setLayoutManager(LinearLayoutManager(activity))
            drag_list_view.setAdapter(adapter, false)
            drag_list_view.setCanDragHorizontally(false)
            drag_list_view.setCanDragVertically(true)
            drag_list_view.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
                override fun onItemSwipeStarted(item: ListSwipeItem) {
                }

                override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                    val adapterItem = item.tag as MyNatatkiFiles
                    val pos: Int = drag_list_view.adapter.getPositionForItem(adapterItem)
                    if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                        onDialogDeliteClick(pos, adapter.itemList[pos].title)
                    }
                    if (swipedDirection == ListSwipeItem.SwipeDirection.RIGHT) {
                        onDialogEditClick(pos)
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
                            val gson = Gson()
                            it.write(gson.toJson(adapter.itemList))
                        }
                        val edit = k.edit()
                        edit.putInt("natatki_sort", -1)
                        edit.apply()
                        myNatatkiFilesSort = -1
                        activity.invalidateOptionsMenu()
                    }
                }
            })
        }
        if (arguments?.getBoolean("shortcuts") == true) addNatatka()
    }

    override fun fileDeliteCancel() {
        drag_list_view.resetSwipedViews(null)
    }

    override fun fileDelite(position: Int) {
        activity?.let { fragmentActivity ->
            val f = adapter.itemList[position]
            adapter.itemList.removeAt(position)
            val filedel = File(fragmentActivity.filesDir.toString().plus("/Malitva/").plus("Mae_malitvy_").plus(f.id))
            filedel.delete()
            val file = File(fragmentActivity.filesDir.toString().plus("/Natatki.json"))
            file.writer().use {
                val gson = Gson()
                it.write(gson.toJson(adapter.itemList))
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDialogEditClick(position: Int) {
        if (MainActivity.checkmoduleResources(activity)) {
            val f = adapter.itemList[position]
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiAdd"))
            intent.putExtra("filename", "Mae_malitvy_" + f.id)
            intent.putExtra("redak", true)
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dd = DialogDelite.getInstance(position, "", "нататку", name)
        fragmentManager?.let { dd.show(it, "dialog_delite") }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_add) {
            addNatatka()
        }
        if (id == R.id.sortdate) {
            activity?.let { activity ->
                val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                if (item.isChecked) {
                    prefEditors.putInt("natatki_sort", -1)
                } else {
                    prefEditors.putInt("natatki_sort", 1)
                }
                prefEditors.apply()
                myNatatkiFilesSort = k.getInt("natatki_sort", 0)
                adapter.itemList.sort()
                val file = File(activity.filesDir.toString().plus("/Natatki.json"))
                file.writer().use {
                    val gson = Gson()
                    it.write(gson.toJson(adapter.itemList))
                }
                adapter.notifyDataSetChanged()
            }
        }
        if (id == R.id.sorttime) {
            activity?.let { activity ->
                val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                if (item.isChecked) {
                    prefEditors.putInt("natatki_sort", -1)
                } else {
                    prefEditors.putInt("natatki_sort", 0)
                }
                prefEditors.apply()
                myNatatkiFilesSort = k.getInt("natatki_sort", 0)
                adapter.itemList.sort()
                val file = File(activity.filesDir.toString().plus("/Natatki.json"))
                file.writer().use {
                    val gson = Gson()
                    it.write(gson.toJson(adapter.itemList))
                }
                adapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNatatka() {
        if (MainActivity.checkmoduleResources(activity)) {
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiAdd"))
            intent.putExtra("redak", false)
            intent.putExtra("filename", "")
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    private inner class ItemAdapter(list: ArrayList<MyNatatkiFiles>, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<MyNatatkiFiles, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
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
            val text = mItemList[position].title
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
                    val f = itemList[adapterPosition]
                    val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiView"))
                    intent.putExtra("filename", "Mae_malitvy_" + f.id)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val contextMenu = DialogContextMenu.getInstance(adapterPosition, itemList[adapterPosition].title)
                fragmentManager?.let { contextMenu.show(it, "context_menu") }
                return true
            }
        }

        init {
            itemList = list
        }
    }

    companion object {
        var myNatatkiFiles = ArrayList<MyNatatkiFiles>()
        var myNatatkiFilesSort = 0

        fun getInstance(shortcuts: Boolean): MenuNatatki {
            val menuNatatki = MenuNatatki()
            val bundl = Bundle()
            bundl.putBoolean("shortcuts", shortcuts)
            menuNatatki.arguments = bundl
            return menuNatatki
        }
    }
}