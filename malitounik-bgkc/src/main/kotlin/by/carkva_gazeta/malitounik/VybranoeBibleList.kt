package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
import by.carkva_gazeta.malitounik.databinding.VybranoeBibleListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper.OnSwipeListenerAdapter
import com.woxthebox.draglistview.swipe.ListSwipeItem
import com.woxthebox.draglistview.swipe.ListSwipeItem.SwipeDirection
import kotlinx.coroutines.*
import java.io.File

class VybranoeBibleList : AppCompatActivity(), DialogDeliteBibliaVybranoe.DialogDeliteBibliVybranoeListener {
    private var dzenNoch = false
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0
    private lateinit var binding: VybranoeBibleListBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun vybranoeDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun vybranoeDelite(position: Int) {
        binding.dragListView.adapter.removeItem(position)
        binding.dragListView.resetSwipedViews(null)
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
        super.onCreate(savedInstanceState)
        binding = VybranoeBibleListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
        val gson = Gson()
        val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
        var bibleVybranoe = ""
        when (biblia) {
            1 -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
            2 -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
            3 -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
        }
        if (bibleVybranoe != "") arrayListVybranoe = gson.fromJson(bibleVybranoe, type)
        binding.dragListView.setLayoutManager(LinearLayoutManager(this))
        binding.dragListView.setAdapter(ItemAdapter(arrayListVybranoe, R.id.image, false), false)
        binding.dragListView.setCanDragHorizontally(false)
        binding.dragListView.setCanDragVertically(true)
        binding.dragListView.setSwipeListener(object : OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: SwipeDirection) {
                if (swipedDirection == SwipeDirection.LEFT) {
                    val adapterItem = item.tag as VybranoeBibliaData
                    val pos: Int = binding.dragListView.adapter.getPositionForItem(adapterItem)
                    val dialog = DialogDeliteBibliaVybranoe.getInstance(pos, arrayListVybranoe[pos].title)
                    dialog.show(supportFragmentManager, "DialogDeliteBibliaVybranoe")
                }
            }
        })
        binding.dragListView.setDragListListener(object : DragListView.DragListListener {
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getText(R.string.str_short_label1)
        when (biblia) {
            1 -> binding.subtitleToolbar.text = getString(R.string.title_biblia)
            2 -> binding.subtitleToolbar.text = getString(R.string.bsinaidal)
            3 -> binding.subtitleToolbar.text = getString(R.string.title_psalter)
        }
        if (dzenNoch) {
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

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl: MenuInflater = menuInflater
        infl.inflate(R.menu.vybranoe_bible, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_help) {
            val dialogHelpListView = DialogHelpListView.getInstance(1)
            dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ItemAdapter(list: ArrayList<VybranoeBibliaData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeBibliaData, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.root.supportedSwipeDirection = SwipeDirection.LEFT
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                view.itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark)
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
                if (MainActivity.checkmoduleResources(this@VybranoeBibleList)) {
                    val intent = Intent()
                    intent.setClassName(this@VybranoeBibleList, MainActivity.BIBLIAVYBRANOE)
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
                arrayListVybranoe.add(0, VybranoeBibliaData(knigaglava, "$title ${glava + 1}", kniga, glava + 1, novyZavet, bibleName))
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