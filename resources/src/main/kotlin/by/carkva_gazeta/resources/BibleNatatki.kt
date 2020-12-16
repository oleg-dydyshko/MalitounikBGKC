package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
import by.carkva_gazeta.resources.DialogBibleNatatkaEdit.BibleNatatkaEditlistiner
import by.carkva_gazeta.resources.DialogDeliteAllZakladkiINatatki.DialogDeliteAllZakladkiINatatkiListener
import by.carkva_gazeta.resources.DialogZakladkaDelite.ZakladkaDeliteListiner
import by.carkva_gazeta.resources.databinding.BibleZakladkiBinding
import com.google.gson.Gson
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class BibleNatatki : AppCompatActivity(), ZakladkaDeliteListiner, DialogDeliteAllZakladkiINatatkiListener, BibleNatatkaEditlistiner, DialogContextMenu.DialogContextMenuListener {
    private var data = ArrayList<BibleNatatkiData>()
    private lateinit var adapter: ItemAdapter
    private var semuxa = 1
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private lateinit var binding: BibleZakladkiBinding

    override fun setEdit() {
        adapter.notifyDataSetChanged()
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
            data.removeAll(data)
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        }
        if (semuxa == 2) {
            data.removeAll(data)
            adapter.notifyDataSetChanged()
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
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = BibleZakladkiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        semuxa = intent.getIntExtra("semuxa", 1)
        if (semuxa == 1) data = BibleGlobalList.natatkiSemuxa
        if (semuxa == 2) data = BibleGlobalList.natatkiSinodal
        adapter = ItemAdapter(data, by.carkva_gazeta.malitounik.R.id.image, false)
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
                                it.write(gson.toJson(data))
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
                                it.write(gson.toJson(data))
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
            binding.titleToolbar.setHorizontallyScrolling(true)
            binding.titleToolbar.freezesText = true
            binding.titleToolbar.marqueeRepeatLimit = -1
            if (binding.titleToolbar.isSelected) {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                binding.titleToolbar.isSelected = false
            } else {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.titleToolbar.isSelected = true
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setText(by.carkva_gazeta.malitounik.R.string.natatki_biblii)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.trash).isVisible = data.size != 0
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.zakladki_i_natatki, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.trash) {
            val natatki = DialogDeliteAllZakladkiINatatki.getInstance(resources.getString(by.carkva_gazeta.malitounik.R.string.natatki_biblii).toLowerCase(Locale.getDefault()), semuxa)
            natatki.show(supportFragmentManager, "delite_all_zakladki_i_natatki")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (semuxa == 1) {
            if (MenuBibleSemuxa.bible_time) {
                MenuBibleSemuxa.bible_time = false
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
        if (semuxa == 2) {
            if (MenuBibleSinoidal.bible_time) {
                MenuBibleSinoidal.bible_time = false
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun zakladkadiliteItem(position: Int, semuxa: Int) {}

    override fun zakladkadiliteItemCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun natatkidiliteItem(position: Int, semuxa: Int) {
        if (semuxa == 1) {
            data.removeAt(position)
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
            if (data.size == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete()
                }
                binding.help.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
            } else {
                val gson = Gson()
                val outputStream = FileWriter(fileNatatki)
                outputStream.write(gson.toJson(data))
                outputStream.close()
            }
        }
        if (semuxa == 2) {
            data.removeAt(position)
            adapter.notifyDataSetChanged()
            val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
            if (data.size == 0) {
                if (fileNatatki.exists()) {
                    fileNatatki.delete()
                }
                binding.help.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
            } else {
                val gson = Gson()
                val outputStream = FileWriter(fileNatatki)
                outputStream.write(gson.toJson(data))
                outputStream.close()
            }
        }
        invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 500) {
            if (data.size == 0) {
                binding.help.visibility = View.VISIBLE
                binding.dragListView.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    private inner class ItemAdapter(list: ArrayList<BibleNatatkiData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<BibleNatatkiData, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                BibleArrayAdapterParallel.colors[0] = "#FFFFFF"
                BibleArrayAdapterParallel.colors[1] = "#f44336"
                view.itemLeft.setTextColor(ContextCompat.getColor(parent.context, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
                view.itemRight.setTextColor(ContextCompat.getColor(parent.context, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
                view.itemLayout.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark_list)
                view.root.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            } else {
                BibleArrayAdapterParallel.colors[0] = "#000000"
                BibleArrayAdapterParallel.colors[1] = "#D00505"
                view.itemLayout.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default_list)
                view.root.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            holder.mText.text = getString(by.carkva_gazeta.malitounik.R.string.bible_natatki, mItemList[position].list[4], mItemList[position].list[5])
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
                if (data[adapterPosition].list[0].contains("1")) kniga = data[adapterPosition].list[1].toInt() else knigaS = data[adapterPosition].list[1].toInt()
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
                intent.putExtra("glava", Integer.valueOf(data[adapterPosition].list[2]))
                intent.putExtra("stix", Integer.valueOf(data[adapterPosition].list[3]))
                startActivityForResult(intent, 500)
            }

            override fun onItemLongClicked(view: View): Boolean {
                val contextMenu = DialogContextMenu.getInstance(adapterPosition, data[adapterPosition].list[5])
                contextMenu.show(supportFragmentManager, "context_menu")
                return true
            }
        }

        init {
            itemList = list
        }
    }
}