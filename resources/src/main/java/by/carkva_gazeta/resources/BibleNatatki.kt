package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.DialogContextMenu
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
import by.carkva_gazeta.resources.DialogBibleNatatkaEdit.BibleNatatkaEditlistiner
import by.carkva_gazeta.resources.DialogDeliteAllZakladkiINatatki.DialogDeliteAllZakladkiINatatkiListener
import by.carkva_gazeta.resources.DialogZakladkaDelite.ZakladkaDeliteListiner
import by.carkva_gazeta.resources.databinding.BibleZakladkiBinding
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
import java.io.FileWriter

class BibleNatatki : BaseActivity(), ZakladkaDeliteListiner, DialogDeliteAllZakladkiINatatkiListener, BibleNatatkaEditlistiner, DialogContextMenu.DialogContextMenuListener {
    private var data = ArrayList<BibleNatatkiData>()
    private lateinit var adapter: ItemAdapter
    private var semuxa = 1
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private lateinit var binding: BibleZakladkiBinding
    private var resetTollbarJob: Job? = null
    private val staryZapavietSemuxaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (data.size == 0) {
                binding.help.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
            }
            adapter.updateList(data)
        }
    }

    override fun setEdit() {
        adapter.updateList(data)
    }

    override fun editCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun onDialogEditClick(position: Int) {
        val natatka = DialogBibleNatatkaEdit.getInstance(semuxa, position)
        natatka.show(supportFragmentManager, "bible_natatka_edit")
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val delite = DialogZakladkaDelite.getInstance(position, name, semuxa, false)
        delite.show(supportFragmentManager, "zakladka_delite")
    }

    override fun fileAllNatatkiAlboZakladki(semuxa: Int) {
        if (semuxa == 1) {
            data.removeAll(data.toSet())
            adapter.updateList(data)
            val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        }
        if (semuxa == 2) {
            data.removeAll(data.toSet())
            adapter.updateList(data)
            val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        }
        binding.help.visibility = View.VISIBLE
        binding.dragListView.visibility = View.GONE
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BibleZakladkiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        semuxa = intent.getIntExtra("semuxa", 1)
        if (semuxa == 1) data = BibleGlobalList.natatkiSemuxa
        if (semuxa == 2) data = BibleGlobalList.natatkiSinodal
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
                val adapterItem = item.tag as BibleNatatkiData
                val position: Int = binding.dragListView.adapter.getPositionForItem(adapterItem)
                if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    val delite = DialogZakladkaDelite.getInstance(position, data[position].list[5], semuxa, false)
                    delite.show(supportFragmentManager, "zakladka_delite")
                }
                if (swipedDirection == ListSwipeItem.SwipeDirection.RIGHT) {
                    val natatka = DialogBibleNatatkaEdit.getInstance(semuxa, position)
                    natatka.show(supportFragmentManager, "bible_natatka_edit")
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
                        val fileZakladki = File("$filesDir/BibliaSemuxaNatatki.json")
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
                        val fileZakladki = File("$filesDir/BibliaSinodalNatatki.json")
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
        if (data.size == 0) {
            binding.help.visibility = View.VISIBLE
            binding.dragListView.visibility = View.GONE
        }
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
        binding.titleToolbar.setText(R.string.natatki_biblii)
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
            val natatki = DialogDeliteAllZakladkiINatatki.getInstance(resources.getString(R.string.natatki_biblii).lowercase(), semuxa)
            natatki.show(supportFragmentManager, "delite_all_zakladki_i_natatki")
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun zakladkadiliteItem(position: Int, semuxa: Int) {}

    override fun zakladkadiliteItemCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun natatkidiliteItem(position: Int, semuxa: Int) {
        if (semuxa == 1) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
            if (data.size == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete()
                }
                binding.help.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
            } else {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                val outputStream = FileWriter(fileNatatki)
                outputStream.write(gson.toJson(data, type))
                outputStream.close()
            }
        }
        if (semuxa == 2) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
            val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
            if (data.size == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete()
                }
                binding.help.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
            } else {
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                val outputStream = FileWriter(fileNatatki)
                outputStream.write(gson.toJson(data, type))
                outputStream.close()
            }
        }
        invalidateOptionsMenu()
    }

    private inner class ItemAdapter(list: ArrayList<BibleNatatkiData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<BibleNatatkiData, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                BibleArrayAdapterParallel.colors[0] = "#FFFFFF"
                BibleArrayAdapterParallel.colors[1] = "#f44336"
                view.itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
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
            holder.mText.text = getString(R.string.bible_natatki, mItemList[position].list[4], mItemList[position].list[5])
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
                var kniga = -1
                var knigaS = -1
                if (data[bindingAdapterPosition].list[0].contains("1")) kniga = data[bindingAdapterPosition].list[1].toInt() else knigaS = data[bindingAdapterPosition].list[1].toInt()
                var intent = Intent()
                if (kniga != -1) {
                    if (semuxa == 1) {
                        intent = Intent(this@BibleNatatki, NovyZapavietSemuxa::class.java)
                    }
                    if (semuxa == 2) {
                        intent = Intent(this@BibleNatatki, NovyZapavietSinaidal::class.java)
                    }
                    intent.putExtra("kniga", kniga)
                }
                if (knigaS != -1) {
                    if (semuxa == 1) {
                        intent = Intent(this@BibleNatatki, StaryZapavietSemuxa::class.java)
                        when (knigaS) {
                            19 -> knigaS = 16
                            20 -> knigaS = 17
                            21 -> knigaS = 18
                            22 -> knigaS = 19
                            23 -> knigaS = 20
                            24 -> knigaS = 21
                            27 -> knigaS = 22
                            28 -> knigaS = 23
                            29 -> knigaS = 24
                            32 -> knigaS = 25
                            33 -> knigaS = 26
                            34 -> knigaS = 27
                            35 -> knigaS = 28
                            36 -> knigaS = 29
                            37 -> knigaS = 30
                            38 -> knigaS = 31
                            39 -> knigaS = 32
                            40 -> knigaS = 33
                            41 -> knigaS = 34
                            42 -> knigaS = 35
                            43 -> knigaS = 36
                            44 -> knigaS = 37
                            45 -> knigaS = 38
                        }
                    }
                    if (semuxa == 2) {
                        intent = Intent(this@BibleNatatki, StaryZapavietSinaidal::class.java)
                    }
                    intent.putExtra("kniga", knigaS)
                }
                intent.putExtra("glava", Integer.valueOf(data[bindingAdapterPosition].list[2]))
                intent.putExtra("stix", Integer.valueOf(data[bindingAdapterPosition].list[3]))
                staryZapavietSemuxaLauncher.launch(intent)
            }

            override fun onItemLongClicked(view: View): Boolean {
                val contextMenu = DialogContextMenu.getInstance(bindingAdapterPosition, data[bindingAdapterPosition].list[5])
                contextMenu.show(supportFragmentManager, "context_menu")
                return true
            }
        }

        fun updateList(newNatatki: ArrayList<BibleNatatkiData>) {
            val diffCallback = RecyclerViewDiffCallback(data, newNatatki)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            itemList = newNatatki
        }

        init {
            itemList = list
        }
    }

    private class RecyclerViewDiffCallback(private val oldArrayList: ArrayList<BibleNatatkiData>, private val newArrayList: ArrayList<BibleNatatkiData>) : DiffUtil.Callback() {
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