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
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.android.synthetic.main.my_bible_list.*
import java.io.File

/**
 * Created by oleg on 13.6.16
 */
class MenuNatatki : NatatkiFragment() {
    private lateinit var adapter: ItemAdapter
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // Скрываем клавиатуру
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
            //myNatatkiFiles.add(MyNatatkiFiles(index, it, lRTE, res[0], file.absoluteFile))
            /*File(activity?.filesDir.toString().plus("/Malitva")).walk().forEach { file ->
                if (file.isFile) {
                    val name = file.name
                    val t1 = name.lastIndexOf("_")
                    val index = name.substring(t1 + 1).toLong()
                    val inputStream = FileReader(file)
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
                        lRTE = file.lastModified()
                    }
                    activity?.let {
                        myNatatkiFiles.add(MyNatatkiFiles(index, it, lRTE, res[0], file.absoluteFile))
                    }
                }
            }
            myNatatkiFiles.sort()*/
            adapter = ItemAdapter(myNatatkiFiles, R.layout.list_item, R.id.image, false) //MyNatatkiAdapter(it)
            drag_list_view.recyclerView.isVerticalScrollBarEnabled = false
            drag_list_view.setLayoutManager(LinearLayoutManager(activity))
            drag_list_view.setAdapter(adapter, false)
            drag_list_view.setCanDragHorizontally(false)
            drag_list_view.setCanDragVertically(true)
            drag_list_view.setCustomDragItem(MyDragItem(activity, R.layout.list_item))
            drag_list_view.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
                override fun onItemSwipeStarted(item: ListSwipeItem) {
                }

                override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                    val adapterItem = item.tag as MyNatatkiFiles
                    val pos: Int = drag_list_view.adapter.getPositionForItem(adapterItem)
                    if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                        onDialogDeliteClick(pos, myNatatkiFiles[pos].title)
                        /*val dd = DialogDelite.getInstance(pos, "", "з выбранага", MenuVybranoe.vybranoe[pos].data)
                        fragmentManager?.let { dd.show(it, "dialog_dilite") }*/
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
                            it.write(gson.toJson(myNatatkiFiles))
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
            val f = myNatatkiFiles[position]
            myNatatkiFiles.removeAt(position)
            val filedel = File(fragmentActivity.filesDir.toString().plus("/Malitva/").plus("Mae_malitvy_").plus(f.id))
            filedel.delete()
            val file = File(fragmentActivity.filesDir.toString().plus("/Natatki.json"))
            file.writer().use {
                val gson = Gson()
                it.write(gson.toJson(myNatatkiFiles))
            }
            //myNatatkiFiles.sort()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDialogEditClick(position: Int) {
        if (MainActivity.checkmoduleResources(activity)) {
            val f = myNatatkiFiles[position]
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiAdd"))
            intent.putExtra("filename", "Mae_malitvy_" + f.id)
            intent.putExtra("redak", true)
            startActivity(intent)
            //startActivityForResult(intent, 104)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dd = DialogDelite.getInstance(position, "", "нататку", name)
        fragmentManager?.let { dd.show(it, "dialog_delite") }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var save = false
        if (data != null) {
            save = data.getBooleanExtra("savefile", false)
        }
        if (requestCode == 103 || requestCode == 104) {
            if (save) {
                /*myNatatkiFiles.clear()
                File(activity?.filesDir.toString().plus("/Malitva")).walk().forEach { file ->
                    if (file.isFile) {
                        val name = file.name
                        val t1 = name.lastIndexOf("_")
                        val index = name.substring(t1 + 1).toLong()
                        val inputStream = FileReader(file)
                        val reader = BufferedReader(inputStream)
                        val res = reader.readText().split("<MEMA></MEMA>")
                        inputStream.close()
                        var lRTE: Long = 1
                        if (res[1].contains("<RTE></RTE>")) {
                            val start = res[1].indexOf("<RTE></RTE>")
                            val end = res[1].length
                            lRTE = res[1].substring(start + 11, end).toLong()
                            res[1] = res[1].substring(0, start)
                        }
                        if (lRTE <= 1) {
                            lRTE = file.lastModified()
                        }
                        activity?.let {
                            myNatatkiFiles.add(MyNatatkiFiles(index, lRTE, res[0]))
                        }
                    }
                }
                myNatatkiFiles.sort()*/
                adapter.notifyDataSetChanged()
            }
        }
    }*/

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
                myNatatkiFiles.sort()
                val file = File(activity.filesDir.toString().plus("/Natatki.json"))
                file.writer().use {
                    val gson = Gson()
                    it.write(gson.toJson(myNatatkiFiles))
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
                myNatatkiFiles.sort()
                val file = File(activity.filesDir.toString().plus("/Natatki.json"))
                file.writer().use {
                    val gson = Gson()
                    it.write(gson.toJson(myNatatkiFiles))
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
            //startActivityForResult(intent, 103)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    private inner class ItemAdapter(list: ArrayList<MyNatatkiFiles>, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<MyNatatkiFiles, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
            //(view as ListSwipeItem).supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT_AND_RIGHT
            val textview = view.findViewById<TextViewRobotoCondensed>(R.id.text)
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            textview.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                val itemLeft = view.findViewById<TextViewRobotoCondensed>(R.id.item_left)
                itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                itemLeft.setBackgroundResource(R.color.colorprimary_material_dark)
                val itemRight = view.findViewById<TextViewRobotoCondensed>(R.id.item_right)
                itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                itemRight.setBackgroundResource(R.color.colorprimary_material_dark)
                view.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_dark)
                textview.setTextColor(ContextCompat.getColor(parent.context, R.color.colorIcons))
            } else {
                textview.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_text))
                view.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_default)
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
                    val f = myNatatkiFiles[adapterPosition]
                    val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiView"))
                    intent.putExtra("filename", "Mae_malitvy_" + f.id)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val contextMenu = DialogContextMenu.getInstance(adapterPosition, myNatatkiFiles[adapterPosition].title)
                fragmentManager?.let { contextMenu.show(it, "context_menu") }
                return true
            }
        }

        init {
            itemList = list
        }
    }

    private class MyDragItem(context: Context, layoutId: Int) : DragItem(context, layoutId) {
        private val mycontext = context
        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            val dragTextView = dragView.findViewById<View>(R.id.text) as TextView
            dragTextView.text = text
            dragTextView.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            val k = mycontext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                clickedView.findViewById<TextViewRobotoCondensed>(R.id.text).setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
                clickedView.findViewById<ConstraintLayout>(R.id.item_layout).setBackgroundResource(R.drawable.selector_dark)
                val itemLeft = clickedView.findViewById<TextViewRobotoCondensed>(R.id.item_left)
                itemLeft.setTextColor(ContextCompat.getColor(mycontext, R.color.colorPrimary_black))
                itemLeft.setBackgroundResource(R.color.colorprimary_material_dark)
                val itemRight = clickedView.findViewById<TextViewRobotoCondensed>(R.id.item_right)
                itemRight.setTextColor(ContextCompat.getColor(mycontext, R.color.colorPrimary_black))
                itemRight.setBackgroundResource(R.color.colorprimary_material_dark)
                dragTextView.setTextColor(ContextCompat.getColor(mycontext, R.color.colorIcons))
                dragView.findViewById<View>(R.id.item_layout).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.colorprimary_material_dark))
            } else {
                dragTextView.setTextColor(ContextCompat.getColor(mycontext, R.color.colorPrimary_text))
                dragView.findViewById<View>(R.id.item_layout).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.colorDivider))
            }
        }
    }

    /*private inner class MyNatatkiAdapter(private val activity: Activity) : ArrayAdapter<MyNatatkiFiles>(activity, R.layout.simple_list_item_3, R.id.label, myNatatkiFiles) {
        private val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_3, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
                viewHolder.buttonPopup = rootView.findViewById(R.id.button_popup)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.buttonPopup?.setOnClickListener { viewHolder.buttonPopup?.let { showPopupMenu(it, position, myNatatkiFiles[position].name) } }
            viewHolder.text?.text = myNatatkiFiles[position].name
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            } else {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_white)
            }
            return rootView
        }

        private fun showPopupMenu(view: View, position: Int, name: String) {
            val popup = PopupMenu(activity, view)
            val infl = popup.menuInflater
            infl.inflate(R.menu.popup, popup.menu)
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                when (menuItem.itemId) {
                    R.id.menu_redoktor -> {
                        onDialogEditClick(position)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.menu_remove -> {
                        onDialogDeliteClick(position, name)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
            popup.show()
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
        var buttonPopup: ImageView? = null
    }*/

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