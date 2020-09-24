package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
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
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.android.synthetic.main.my_bible_list.*
import java.io.File

/**
 * Created by oleg on 30.5.16
 */
class MenuVybranoe : VybranoeFragment() {
    private lateinit var adapter: ItemAdapter //MyVybranoeAdapter
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun fileDeliteCancel() {
        drag_list_view.resetSwipedViews(null)
    }

    override fun fileDelite(position: Int) {
        vybranoe.removeAt(position)
        activity?.let { activity ->
            val gson = Gson()
            val file = File(activity.filesDir.toString() + "/Vybranoe.json")
            //vybranoe.sort()
            file.writer().use {
                it.write(gson.toJson(vybranoe))
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun deliteAllVybranoe() {
        activity?.let { activity ->
            vybranoe.clear()
            val gson = Gson()
            val file = File(activity.filesDir.toString() + "/Vybranoe.json")
            file.writer().use {
                it.write(gson.toJson(vybranoe))
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_vybranoe, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { it ->
            val file = File(it.filesDir.toString() + "/Vybranoe.json")
            if (file.exists()) {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    vybranoe = gson.fromJson(file.readText(), type)
                } catch (t: Throwable) {
                    file.delete()
                }
            }
            adapter = ItemAdapter(vybranoe, R.layout.list_item, R.id.image, false) //MyVybranoeAdapter(it)
            drag_list_view.recyclerView.isVerticalScrollBarEnabled = true
            drag_list_view.setLayoutManager(LinearLayoutManager(it))
            drag_list_view.setAdapter(adapter, false)
            drag_list_view.setCanDragHorizontally(false)
            drag_list_view.setCanDragVertically(true)
            drag_list_view.setCustomDragItem(MyDragItem(it, R.layout.list_item))
            drag_list_view.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
                override fun onItemSwipeStarted(item: ListSwipeItem) {
                }

                override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                    if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                        fragmentManager?.let {
                            val adapterItem = item.tag as VybranoeData
                            val pos: Int = drag_list_view.adapter.getPositionForItem(adapterItem)
                            val dd = DialogDelite.getInstance(pos, "", "з выбранага", vybranoe[pos].data)
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
                    file.writer().use {
                        val gson = Gson()
                        it.write(gson.toJson(vybranoe))
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
            if (vybranoe.size > 0) {
                fragmentManager?.let {
                    DialogDeliteAllVybranoe().show(it, "DeliteVybranoe")
                }
            }
        }
        if (id == R.id.sortdatevybranoe) {
            activity?.let { fragmentActivity ->
                vybranoe.sort()
                val gson = Gson()
                val file = File(fragmentActivity.filesDir.toString() + "/Vybranoe.json")
                file.writer().use {
                    it.write(gson.toJson(vybranoe))
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
                    val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.Bogashlugbovya"))
                    intent.putExtra("resurs", vybranoe[adapterPosition].resurs)
                    intent.putExtra("title", vybranoe[adapterPosition].data)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dd = DialogDelite.getInstance(adapterPosition, "", "з выбранага", vybranoe[adapterPosition].data)
                fragmentManager?.let { dd.show(it, "dialog_dilite") }
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

    /*private inner class MyVybranoeAdapter(private val activity: Activity) : ArrayAdapter<VybranoeData>(activity, R.layout.simple_list_item_3, R.id.label, vybranoe) {
        private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)

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
            viewHolder.buttonPopup?.setOnClickListener { viewHolder.buttonPopup?.let { showPopupMenu(it, position, vybranoe[position].data) } }
            viewHolder.text?.text = vybranoe[position].data
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
            popup.menu.getItem(0).isVisible = false
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                if (menuItem.itemId == R.id.menu_remove) {
                    val dd = DialogDelite.getInstance(position, "", "з выбранага", name)
                    fragmentManager?.let { dd.show(it, "dialog_dilite") }
                    return@setOnMenuItemClickListener true
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
        var vybranoe: ArrayList<VybranoeData> = ArrayList()
    }
}