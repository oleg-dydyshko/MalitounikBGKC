package by.carkva_gazeta.resources

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.NovyZapavietBokunaList
import by.carkva_gazeta.malitounik.NovyZapavietCarniauskiList
import by.carkva_gazeta.malitounik.NovyZapavietSemuxaList
import by.carkva_gazeta.malitounik.NovyZapavietSinaidalList
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.StaryZapavietBokunaList
import by.carkva_gazeta.malitounik.StaryZapavietCarniauskiList
import by.carkva_gazeta.malitounik.StaryZapavietSemuxaList
import by.carkva_gazeta.malitounik.StaryZapavietSinaidalList
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
import by.carkva_gazeta.resources.DialogDeliteAllZakladkiINatatki.DialogDeliteAllZakladkiINatatkiListener
import by.carkva_gazeta.resources.DialogZakladkaDelite.ZakladkaDeliteListiner
import by.carkva_gazeta.resources.databinding.BibleZakladkiNatatkiBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class BibleZakladki : BaseActivity(), ZakladkaDeliteListiner, DialogDeliteAllZakladkiINatatkiListener {
    private lateinit var adapter: ItemAdapter
    private var data = ArrayList<BibleZakladkiData>()
    private var semuxa = 1
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private lateinit var binding: BibleZakladkiNatatkiBinding
    private var resetTollbarJob: Job? = null

    override fun onResume() {
        super.onResume()
        if (semuxa == 1) data = BibleGlobalList.zakladkiSemuxa
        if (semuxa == 2) data = BibleGlobalList.zakladkiSinodal
        if (semuxa == 3) data = BibleGlobalList.zakladkiBokuna
        if (semuxa == 4) data = BibleGlobalList.zakladkiCarniauski
        if (data.size == 0) {
            onBack()
        } else {
            adapter.updateList(data)
        }
    }

    override fun fileAllNatatkiAlboZakladki(semuxa: Int) {
        if (semuxa == 1) {
            data.removeAll(data.toSet())
            adapter.updateList(data)
            val fileZakladki = File("$filesDir/BibliaSemuxaZakladki.json")
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        }
        if (semuxa == 2) {
            data.removeAll(data.toSet())
            adapter.updateList(data)
            val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        }
        if (semuxa == 3) {
            data.removeAll(data.toSet())
            adapter.updateList(data)
            val fileZakladki = File("$filesDir/BibliaBokunaZakladki.json")
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        }
        if (semuxa == 4) {
            data.removeAll(data.toSet())
            adapter.updateList(data)
            val fileZakladki = File("$filesDir/BibliaCarniauskiZakladki.json")
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        }
        onBack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BibleZakladkiNatatkiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        semuxa = intent.getIntExtra("semuxa", 1)
        if (semuxa == 1) data = BibleGlobalList.zakladkiSemuxa
        if (semuxa == 2) data = BibleGlobalList.zakladkiSinodal
        if (semuxa == 3) data = BibleGlobalList.zakladkiBokuna
        if (semuxa == 4) data = BibleGlobalList.zakladkiCarniauski
        adapter = ItemAdapter(data, R.id.image, false)
        binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
        binding.dragListView.setLayoutManager(LinearLayoutManager(this))
        binding.dragListView.setAdapter(adapter, false)
        binding.dragListView.setCanDragHorizontally(false)
        binding.dragListView.setCanDragVertically(true)
        binding.dragListView.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    val adapterItem = item.tag as BibleZakladkiData
                    val position: Int = binding.dragListView.adapter.getPositionForItem(adapterItem)
                    val t1 = data[position].data.indexOf("\n\n")
                    val t2 = if (semuxa == 1) data[position].data.indexOf(". ", t1) else data[position].data.indexOf(" ", t1)
                    val delite = DialogZakladkaDelite.getInstance(position, data[position].data.substring(0, t1) + getString(R.string.stix_by) + " " + data[position].data.substring(t1 + 2, t2), semuxa, true)
                    delite.show(supportFragmentManager, "zakladka_delite")
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
                    val gson = Gson()
                    if (semuxa == 1) {
                        val fileZakladki = File("$filesDir/BibliaSemuxaZakladki.json")
                        if (data.size == 0) {
                            if (fileZakladki.exists()) {
                                fileZakladki.delete()
                            }
                        } else {
                            fileZakladki.writer().use {
                                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                                it.write(gson.toJson(data, type))
                            }
                        }
                    }
                    if (semuxa == 2) {
                        val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
                        if (data.size == 0) {
                            if (fileZakladki.exists()) {
                                fileZakladki.delete()
                            }
                        } else {
                            fileZakladki.writer().use {
                                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                                it.write(gson.toJson(data, type))
                            }
                        }
                    }
                    if (semuxa == 3) {
                        val fileZakladki = File("$filesDir/BibliaBokunaZakladki.json")
                        if (data.size == 0) {
                            if (fileZakladki.exists()) {
                                fileZakladki.delete()
                            }
                        } else {
                            fileZakladki.writer().use {
                                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                                it.write(gson.toJson(data, type))
                            }
                        }
                    }
                    if (semuxa == 4) {
                        val fileZakladki = File("$filesDir/BibliaCarniauskiZakladki.json")
                        if (data.size == 0) {
                            if (fileZakladki.exists()) {
                                fileZakladki.delete()
                            }
                        } else {
                            fileZakladki.writer().use {
                                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                                it.write(gson.toJson(data, type))
                            }
                        }
                    }
                }
            }
        })
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setText(R.string.zakladki_bible)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.trash).isVisible = data.size != 0
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.zakladki_i_natatki, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.trash) {
            val natatki = DialogDeliteAllZakladkiINatatki.getInstance(resources.getString(R.string.zakladki_bible).lowercase(), semuxa)
            natatki.show(supportFragmentManager, "delite_all_zakladki_i_natatki")
            return true
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun natatkidiliteItem(position: Int, semuxa: Int) {}

    override fun zakladkadiliteItemCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun zakladkadiliteItem(position: Int, semuxa: Int) {
        if (semuxa == 1) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            val fileZakladki = File("$filesDir/BibliaSemuxaZakladki.json")
            if (data.size == 0) {
                if (fileZakladki.exists()) {
                    fileZakladki.delete()
                }
                onBack()
            } else {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                fileZakladki.writer().use {
                    it.write(gson.toJson(data, type))
                }
            }
        }
        if (semuxa == 2) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
            if (data.size == 0) {
                if (fileZakladki.exists()) {
                    fileZakladki.delete()
                }
                onBack()
            } else {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                fileZakladki.writer().use {
                    it.write(gson.toJson(data, type))
                }
            }
        }
        if (semuxa == 3) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            val fileZakladki = File("$filesDir/BibliaBokunaZakladki.json")
            if (data.size == 0) {
                if (fileZakladki.exists()) {
                    fileZakladki.delete()
                }
                onBack()
            } else {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                fileZakladki.writer().use {
                    it.write(gson.toJson(data, type))
                }
            }
        }
        if (semuxa == 4) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            val fileZakladki = File("$filesDir/BibliaCarniauskiZakladki.json")
            if (data.size == 0) {
                if (fileZakladki.exists()) {
                    fileZakladki.delete()
                }
                onBack()
            } else {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                fileZakladki.writer().use {
                    it.write(gson.toJson(data, type))
                }
            }
        }
        invalidateOptionsMenu()
    }

    private inner class ItemAdapter(list: ArrayList<BibleZakladkiData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<BibleZakladkiData, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(layoutInflater, parent, false)
            view.root.supportedSwipeDirection = ListSwipeItem.SwipeDirection.LEFT
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                BibleArrayAdapterParallel.colors[0] = "#FFFFFF"
                BibleArrayAdapterParallel.colors[1] = "#ff6666"
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark)
                view.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            } else {
                BibleArrayAdapterParallel.colors[0] = "#000000"
                BibleArrayAdapterParallel.colors[1] = "#D00505"
                view.itemLayout.setBackgroundResource(R.drawable.selector_default_list)
                view.root.setBackgroundResource(R.color.colorDivider)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = mItemList[position].data
            val t1 = text.lastIndexOf("<!--")
            val t2 = text.indexOf("\n\n")
            var colorPosition = 0
            val textItem = if (t1 == -1) {
                SpannableString(text)
            } else {
                colorPosition = text.substring(t1 + 4).toInt()
                SpannableString(text.substring(0, t1))
            }
            textItem.setSpan(ForegroundColorSpan(Color.parseColor(BibleArrayAdapterParallel.colors[colorPosition])), 0, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.mText.text = textItem
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
                val knigaName = mItemList[bindingAdapterPosition].data
                var kniga = -1
                var knigaS = -1
                val t1: Int
                val t2: Int
                val t3: Int
                val glava: Int
                val listn: Array<String>
                val lists: Array<String>
                when (semuxa) {
                    1 -> {
                        listn = resources.getStringArray(R.array.semuxan)
                        lists = resources.getStringArray(R.array.semuxas)
                    }

                    2 -> {
                        listn = resources.getStringArray(R.array.sinoidaln)
                        lists = resources.getStringArray(R.array.sinoidals)
                    }

                    3 -> {
                        listn = resources.getStringArray(R.array.bokunan)
                        lists = resources.getStringArray(R.array.bokunas)
                    }

                    4 -> {
                        listn = resources.getStringArray(R.array.charniauskin)
                        lists = resources.getStringArray(R.array.charniauskis)
                    }

                    else -> {
                        listn = resources.getStringArray(R.array.semuxan)
                        lists = resources.getStringArray(R.array.semuxas)
                    }
                }
                for (e in listn.indices) {
                    if (knigaName.contains(listn[e])) kniga = e
                }
                for (e in lists.indices) {
                    if (knigaName.contains(lists[e])) knigaS = e
                }
                if (semuxa == 2) {
                    t1 = knigaName.indexOf("Глава ")
                    t2 = knigaName.indexOf("/", t1)
                    t3 = knigaName.indexOf("\n\n", t2)
                    glava = knigaName.substring(t1 + 6, t2).toInt()
                } else {
                    t1 = knigaName.indexOf("Разьдзел ")
                    t2 = knigaName.indexOf("/", t1)
                    t3 = knigaName.indexOf("\n\n")
                    glava = knigaName.substring(t1 + 9, t2).toInt()
                }
                val stix = knigaName.substring(t2 + 6, t3).toInt()
                var intent = Intent(this@BibleZakladki, NovyZapavietSemuxaList::class.java)
                if (kniga != -1) {
                    if (semuxa == 1) {
                        intent = Intent(this@BibleZakladki, NovyZapavietSemuxaList::class.java)
                    }
                    if (semuxa == 2) {
                        intent = Intent(this@BibleZakladki, NovyZapavietSinaidalList::class.java)
                    }
                    if (semuxa == 3) {
                        intent = Intent(this@BibleZakladki, NovyZapavietBokunaList::class.java)
                    }
                    if (semuxa == 4) {
                        intent = Intent(this@BibleZakladki, NovyZapavietCarniauskiList::class.java)
                    }
                    intent.putExtra("kniga", kniga)
                }
                if (knigaS != -1) {
                    if (semuxa == 1) {
                        intent = Intent(this@BibleZakladki, StaryZapavietSemuxaList::class.java)
                    }
                    if (semuxa == 2) {
                        intent = Intent(this@BibleZakladki, StaryZapavietSinaidalList::class.java)
                    }
                    if (semuxa == 3) {
                        intent = Intent(this@BibleZakladki, StaryZapavietBokunaList::class.java)
                    }
                    if (semuxa == 4) {
                        intent = Intent(this@BibleZakladki, StaryZapavietCarniauskiList::class.java)
                    }
                    intent.putExtra("kniga", knigaS)
                }
                intent.putExtra("glava", glava - 1)
                intent.putExtra("stix", stix - 1)
                intent.putExtra("prodolzyt", true)
                startActivity(intent)
            }

            override fun onItemLongClicked(view: View): Boolean {
                val t1 = itemList[bindingAdapterPosition].data.indexOf("\n\n")
                val t2 = if (semuxa == 1) itemList[bindingAdapterPosition].data.indexOf(". ", t1) else itemList[bindingAdapterPosition].data.indexOf(" ", t1)
                val delite = DialogZakladkaDelite.getInstance(bindingAdapterPosition, itemList[bindingAdapterPosition].data.substring(0, t1) + getString(R.string.stix_by) + " " + itemList[bindingAdapterPosition].data.substring(t1 + 2, t2), semuxa, true)
                delite.show(supportFragmentManager, "zakladka_delite")
                return true
            }
        }

        fun updateList(newVybranoe: ArrayList<BibleZakladkiData>) {
            val diffCallback = RecyclerViewDiffCallback(data, newVybranoe)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            itemList = newVybranoe
        }

        init {
            itemList = list
        }
    }

    private class RecyclerViewDiffCallback(private val oldArrayList: ArrayList<BibleZakladkiData>, private val newArrayList: ArrayList<BibleZakladkiData>) : DiffUtil.Callback() {
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
}