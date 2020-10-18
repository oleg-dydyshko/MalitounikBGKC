package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper.OnSwipeListenerAdapter
import com.woxthebox.draglistview.swipe.ListSwipeItem
import com.woxthebox.draglistview.swipe.ListSwipeItem.SwipeDirection
import kotlinx.android.synthetic.main.my_bible_list.*
import java.io.File


class MyBibleList : AppCompatActivity(), DialogDeliteBibliaVybranoe.DialogDeliteBibliVybranoeListener {

    private var dzenNoch = false
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0

    override fun vybranoeDeliteCancel() {
        drag_list_view.resetSwipedViews(null)
    }

    override fun vybranoeDelite(position: Int) {
        drag_list_view.adapter.removeItem(position)
        drag_list_view.resetSwipedViews(null)
        val gson = Gson()
        val prefEditors = k.edit()
        if (arrayListVybranoe.isEmpty()) {
            when (biblia) {
                1 -> prefEditors.remove("bibleVybranoeSemuxa")
                2 -> prefEditors.remove("bibleVybranoeSinoidal")
                3 -> prefEditors.remove("bibleVybranoeNadsan")
            }
            MenuVybranoe.vybranoe.forEachIndexed { index, it ->
                if (it.resurs == biblia.toString()) {
                    MenuVybranoe.vybranoe.removeAt(index)
                    val file = File("$filesDir/Vybranoe.json")
                    file.writer().use {
                        it.write(gson.toJson(MenuVybranoe.vybranoe))
                    }
                    return@forEachIndexed
                }
            }
            onSupportNavigateUp()
        } else {
            when (biblia) {
                1 -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe))
                2 -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe))
                3 -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe))
            }
        }
        prefEditors.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_bible_list)
        drag_list_view.recyclerView.isVerticalScrollBarEnabled = false
        val gson = Gson()
        val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
        var bibleVybranoe = ""
        when (biblia) {
            1 -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
            2 -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
            3 -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
        }
        if (bibleVybranoe != "") arrayListVybranoe = gson.fromJson(bibleVybranoe, type)
        drag_list_view.setLayoutManager(LinearLayoutManager(this))
        drag_list_view.setAdapter(ItemAdapter(arrayListVybranoe, R.layout.list_item, R.id.image, false), false)
        drag_list_view.setCanDragHorizontally(false)
        drag_list_view.setCanDragVertically(true)
        drag_list_view.setCustomDragItem(MyDragItem(this, R.layout.list_item))
        drag_list_view.setSwipeListener(object : OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: SwipeDirection) {
                if (swipedDirection == SwipeDirection.LEFT) {
                    val adapterItem = item.tag as VybranoeBibliaData
                    val pos: Int = drag_list_view.adapter.getPositionForItem(adapterItem)
                    val dialog = DialogDeliteBibliaVybranoe.getInstance(pos, arrayListVybranoe[pos].title)
                    dialog.show(supportFragmentManager, "DialogDeliteBibliaVybranoe")
                }
            }
        })
        drag_list_view.setDragListListener(object : DragListView.DragListListener {
            override fun onItemDragStarted(position: Int) {
            }

            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
            }

            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                val prefEditors = k.edit()
                when (biblia) {
                    1 -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe))
                    2 -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe))
                    3 -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe))
                }
                prefEditors.apply()
            }
        })
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    override fun onBackPressed() {
        if (arrayListVybranoe.isEmpty()) onSupportNavigateUp()
        else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    private inner class ItemAdapter(list: ArrayList<VybranoeBibliaData>, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeBibliaData, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
            (view as ListSwipeItem).supportedSwipeDirection = SwipeDirection.LEFT
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
                if (MainActivity.checkmoduleResources(this@MyBibleList)) {
                    val intent = Intent(this@MyBibleList, Class.forName("by.carkva_gazeta.resources.BibliaVybranoe"))
                    intent.putExtra("position", adapterPosition * 2)
                    intent.putExtra("biblia", biblia)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(supportFragmentManager, "dadatak")
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dialog = DialogDeliteBibliaVybranoe.getInstance(adapterPosition, arrayListVybranoe[adapterPosition].title)
                dialog.show(supportFragmentManager, "DialogDeliteBibliaVybranoe")
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

    companion object {
        var arrayListVybranoe = ArrayList<VybranoeBibliaData>()
        var biblia = 1

        fun checkVybranoe(context: Context, kniga: Int, glava: Int, bibleName: Int = 1): Boolean {
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val knigaglava = "${kniga + 1}${glava + 1}".toLong()
            val gson = Gson()
            val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
            var bibleVybranoe = ""
            when (bibleName) {
                1 -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
                2 -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
                3 -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
            }
            if (bibleVybranoe != "") {
                arrayListVybranoe = gson.fromJson(bibleVybranoe, type)
                for (i in 0 until arrayListVybranoe.size) {
                    if (arrayListVybranoe[i].id == knigaglava) return true
                }
            } else {
                arrayListVybranoe = ArrayList()
                return false
            }
            return false
        }

        fun checkVybranoe(resurs: String): Boolean {
            MenuVybranoe.vybranoe.forEach {
                if (it.resurs == resurs) {
                    return true
                }
            }
            return false
        }

        fun setVybranoe(context: Context, title: String, kniga: Int, glava: Int, novyZavet: Boolean = false, bibleName: Int = 1): Boolean {
            val knigaglava = "${kniga + 1}${glava + 1}".toLong()
            var remove = true
            for (i in 0 until arrayListVybranoe.size) {
                if (arrayListVybranoe[i].id == knigaglava) {
                    arrayListVybranoe.removeAt(i)
                    remove = false
                    break
                }
            }
            if (remove)
                arrayListVybranoe.add(VybranoeBibliaData(knigaglava, "$title ${glava + 1}", kniga, glava + 1, novyZavet, bibleName))
            val gson = Gson()
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val prefEditors = k.edit()
            when (bibleName) {
                1 -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe))
                2 -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe))
                3 -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe))
            }
            prefEditors.apply()
            return remove
        }
    }
}