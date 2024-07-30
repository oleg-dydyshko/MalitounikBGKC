package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
import by.carkva_gazeta.malitounik.databinding.VybranoeBibleListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper.OnSwipeListenerAdapter
import com.woxthebox.draglistview.swipe.ListSwipeItem
import com.woxthebox.draglistview.swipe.ListSwipeItem.SwipeDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class VybranoeBibleList : BaseActivity(), DialogDeliteBibliaVybranoe.DialogDeliteBibliVybranoeListener, DialogDeliteAllVybranoe.DialogDeliteAllVybranoeListener {
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

    override fun deliteAllVybranoe() {
        val prefEditors = k.edit()
        val gson = Gson()
        when (perevod) {
            PEREVODSEMUXI -> prefEditors.remove("bibleVybranoeSemuxa")
            PEREVODSINOIDAL -> prefEditors.remove("bibleVybranoeSinoidal")
            PEREVODNADSAN -> prefEditors.remove("bibleVybranoeNadsan")
            PEREVODBOKUNA -> prefEditors.remove("bibleVybranoeBokuna")
            PEREVODCARNIAUSKI -> prefEditors.remove("bibleVybranoeCarniauski")
        }
        var posDelite = -1
        MenuVybranoe.vybranoe.forEachIndexed { index, it ->
            if (it.resurs == perevod) {
                posDelite = index
                return@forEachIndexed
            }
        }
        if (posDelite != -1) {
            MenuVybranoe.vybranoe.removeAt(posDelite)
            val file = File("$filesDir/Vybranoe.json")
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
            file.writer().use {
                it.write(gson.toJson(MenuVybranoe.vybranoe, type))
            }
        }
        prefEditors.apply()
        setResult(200)
        onBack()
    }

    override fun vybranoeDelite(position: Int) {
        binding.dragListView.adapter.removeItem(position)
        binding.dragListView.resetSwipedViews(null)
        if (arrayListVybranoe.isEmpty()) {
            deliteAllVybranoe()
        } else {
            val gson = Gson()
            val prefEditors = k.edit()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            when (perevod) {
                PEREVODSEMUXI -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe, type))
                PEREVODSINOIDAL -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe, type))
                PEREVODNADSAN -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe, type))
                PEREVODBOKUNA -> prefEditors.putString("bibleVybranoeBokuna", gson.toJson(arrayListVybranoe, type))
                PEREVODCARNIAUSKI -> prefEditors.putString("bibleVybranoeCarniauski", gson.toJson(arrayListVybranoe, type))
            }
            prefEditors.apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VybranoeBibleListBinding.inflate(layoutInflater)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = getBaseDzenNoch()
        setContentView(binding.root)
        binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
        var bibleVybranoe = ""
        perevod = intent.extras?.getString("perevod", PEREVODSEMUXI) ?: PEREVODSEMUXI
        when (perevod) {
            PEREVODSEMUXI -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
            PEREVODSINOIDAL -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
            PEREVODNADSAN -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
            PEREVODBOKUNA -> bibleVybranoe = k.getString("bibleVybranoeBokuna", "") ?: ""
            PEREVODCARNIAUSKI -> bibleVybranoe = k.getString("bibleVybranoeCarniauski", "") ?: ""
        }
        if (bibleVybranoe != "") arrayListVybranoe = gson.fromJson(bibleVybranoe, type)
        binding.dragListView.setLayoutManager(LinearLayoutManager(this))
        binding.dragListView.setAdapter(ItemAdapter(this, arrayListVybranoe, R.id.image, false), false)
        binding.dragListView.setCanDragHorizontally(false)
        binding.dragListView.setCanDragVertically(true)
        binding.dragListView.setSwipeListener(object : OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: SwipeDirection) {
                if (swipedDirection == SwipeDirection.LEFT) {
                    val adapterItem = item.tag as VybranoeBibliaData
                    val pos = binding.dragListView.adapter.getPositionForItem(adapterItem)
                    val dialog = DialogDeliteBibliaVybranoe.getInstance(pos, arrayListVybranoe[pos].title)
                    dialog.setDialogDeliteBibliVybranoeListener(this@VybranoeBibleList)
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
                when (perevod) {
                    PEREVODSEMUXI -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe, type))
                    PEREVODSINOIDAL -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe, type))
                    PEREVODNADSAN -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe, type))
                    PEREVODBOKUNA -> prefEditors.putString("bibleVybranoeBokuna", gson.toJson(arrayListVybranoe, type))
                    PEREVODCARNIAUSKI -> prefEditors.putString("bibleVybranoeCarniauski", gson.toJson(arrayListVybranoe, type))
                }
                prefEditors.apply()
            }
        })
        setTollbarTheme()
        if (dzenNoch) {
            binding.view.setBackgroundResource(R.color.colorprimary_material_dark2)
            binding.textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.textView.setBackgroundResource(R.drawable.selector_dark_list)
        }
        binding.textView.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.MARANATA)
                intent.putExtra("cytanneMaranaty", biblia())
                intent.putExtra("prodoljyt", true)
                intent.putExtra("perevod", perevod)
                intent.putExtra("vybranae", true)
                startActivity(intent)
            } else {
                installFullMalitounik()
            }
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        binding.titleToolbar.text = resources.getText(R.string.str_short_label1)
        when (perevod) {
            PEREVODSEMUXI -> binding.subtitleToolbar.text = getString(R.string.title_biblia)
            PEREVODSINOIDAL -> binding.subtitleToolbar.text = getString(R.string.bsinaidal)
            PEREVODNADSAN -> binding.subtitleToolbar.text = getString(R.string.title_psalter)
            PEREVODBOKUNA -> binding.subtitleToolbar.text = getString(R.string.title_biblia_bokun)
            PEREVODCARNIAUSKI -> binding.subtitleToolbar.text = getString(R.string.title_biblia_charniauski)
        }
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
        binding.subtitleToolbar.isSingleLine = true
    }

    private fun biblia(): String {
        var result = ""
        val sb = StringBuilder()
        arrayListVybranoe.forEachIndexed { index, vybranoeBibliaData ->
            if (vybranoeBibliaData.novyZavet) {
                when (vybranoeBibliaData.kniga) {
                    0 -> result = "Мц"
                    1 -> result = "Мк"
                    2 -> result = "Лк"
                    3 -> result = "Ян"
                    4 -> result = "Дз"
                    5 -> result = "Як"
                    6 -> result = "1 Пт"
                    7 -> result = "2 Пт"
                    8 -> result = "1 Ян"
                    9 -> result = "2 Ян"
                    10 -> result = "3 Ян"
                    11 -> result = "Юды"
                    12 -> result = "Рым"
                    13 -> result = "1 Кар"
                    14 -> result = "2 Кар"
                    15 -> result = "Гал"
                    16 -> result = "Эф"
                    17 -> result = "Плп"
                    18 -> result = "Клс"
                    19 -> result = "1 Фес"
                    20 -> result = "2 Фес"
                    21 -> result = "1 Цім"
                    22 -> result = "2 Цім"
                    23 -> result = "Ціт"
                    24 -> result = "Флм"
                    25 -> result = "Гбр"
                    26 -> result = "Адкр"
                }
            } else {
                when (vybranoeBibliaData.kniga) {
                    0 -> result = "Быц"
                    1 -> result = "Вых"
                    2 -> result = "Ляв"
                    3 -> result = "Лікі"
                    4 -> result = "Дрг"
                    5 -> result = "Нав"
                    6 -> result = "Суд"
                    7 -> result = "Рут"
                    8 -> result = "1 Цар"
                    9 -> result = "2 Цар"
                    10 -> result = "3 Цар"
                    11 -> result = "4 Цар"
                    12 -> result = "1 Лет"
                    13 -> result = "2 Лет"
                    14 -> result = "1 Эзд"
                    15 -> result = "Нээм"
                    16 -> result = "2 Эзд"
                    17 -> result = "Тав"
                    18 -> result = "Юдт"
                    19 -> result = "Эст"
                    20 -> result = "Ёва"
                    21 -> result = "Пс"
                    22 -> result = "Высл"
                    23 -> result = "Экл"
                    24 -> result = "Псн"
                    25 -> result = "Мдр"
                    26 -> result = "Сір"
                    27 -> result = "Іс"
                    28 -> result = "Ер"
                    29 -> result = "Плач"
                    30 -> result = "Пасл Ер"
                    31 -> result = "Бар"
                    32 -> result = "Езк"
                    33 -> result = "Дан"
                    34 -> result = "Ас"
                    35 -> result = "Ёіл"
                    36 -> result = "Ам"
                    37 -> result = "Аўдз"
                    38 -> result = "Ёны"
                    39 -> result = "Міх"
                    40 -> result = "Нвм"
                    41 -> result = "Абк"
                    42 -> result = "Саф"
                    43 -> result = "Аг"
                    44 -> result = "Зах"
                    45 -> result = "Мал"
                    46 -> result = "1 Мак"
                    47 -> result = "2 Мак"
                    48 -> result = "3 Мак"
                    49 -> result = "3 Эзд"
                }
            }
            val delimiter = if (arrayListVybranoe.size == index + 1) ""
            else "; "
            sb.append(result).append(" ").append(vybranoeBibliaData.glava).append(delimiter)
        }
        return sb.toString()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.vybranae_list, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_sort) {
            arrayListVybranoe.sortWith(compareBy({
                var t1 = it.title.indexOf(" ")
                if (t1 == -1) t1 = it.title.length
                it.title.substring(0, t1)
            }, {
                it.glava
            }))
            val prefEditor = k.edit()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            when (perevod) {
                PEREVODSEMUXI -> prefEditor.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe, type))
                PEREVODSINOIDAL -> prefEditor.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe, type))
                PEREVODNADSAN -> prefEditor.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe, type))
                PEREVODBOKUNA -> prefEditor.putString("bibleVybranoeBokuna", gson.toJson(arrayListVybranoe, type))
                PEREVODCARNIAUSKI -> prefEditor.putString("bibleVybranoeCarniauski", gson.toJson(arrayListVybranoe, type))
            }
            prefEditor.apply()
            (binding.dragListView.adapter as ItemAdapter).updateList(arrayListVybranoe)
            return true
        }
        if (id == R.id.action_delite) {
            val dialogDeliteAllVybranoe = DialogDeliteAllVybranoe()
            dialogDeliteAllVybranoe.show(supportFragmentManager, "DialogDeliteAllVybranoe")
            return true
        }
        return false
    }

    private inner class ItemAdapter(private val mContext: Activity, list: ArrayList<VybranoeBibliaData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeBibliaData, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(mContext.layoutInflater, parent, false)
            view.root.supportedSwipeDirection = SwipeDirection.LEFT
            if (dzenNoch) {
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark2)
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
                if (checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(this@VybranoeBibleList, MainActivity.MARANATA)
                    intent.putExtra("cytanneMaranaty", biblia())
                    intent.putExtra("perevod", perevod)
                    intent.putExtra("vybranae", true)
                    intent.putExtra("title", mItemList[bindingAdapterPosition].title)
                    startActivity(intent)
                } else {
                    installFullMalitounik()
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dialog = DialogDeliteBibliaVybranoe.getInstance(bindingAdapterPosition, arrayListVybranoe[bindingAdapterPosition].title)
                dialog.setDialogDeliteBibliVybranoeListener(this@VybranoeBibleList)
                dialog.show(supportFragmentManager, "DialogDeliteBibliaVybranoe")
                return true
            }
        }

        fun updateList(newVybranoe: ArrayList<VybranoeBibliaData>) {
            itemList = newVybranoe
        }

        init {
            itemList = list
        }
    }

    companion object {
        const val PEREVODSEMUXI = "1"
        const val PEREVODSINOIDAL = "2"
        const val PEREVODNADSAN = "3"
        const val PEREVODBOKUNA = "4"
        const val PEREVODCARNIAUSKI = "5"
        var arrayListVybranoe = ArrayList<VybranoeBibliaData>()
        private var perevod = PEREVODSEMUXI

        fun checkVybranoe(kniga: Int, glava: Int, perevod: String): Boolean {
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val knigaglava = "$kniga${glava + 1}".toLong()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            var bibleVybranoe = ""
            when (perevod) {
                PEREVODSEMUXI -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
                PEREVODSINOIDAL -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
                PEREVODNADSAN -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
                PEREVODBOKUNA -> bibleVybranoe = k.getString("bibleVybranoeBokuna", "") ?: ""
                PEREVODCARNIAUSKI -> bibleVybranoe = k.getString("bibleVybranoeCarniauski", "") ?: ""
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

        fun setVybranoe(title: String, kniga: Int, glava: Int, perevod: String, novyZavet: Boolean): Boolean {
            val context = Malitounik.applicationContext()
            val list = when (perevod) {
                PEREVODSEMUXI -> {
                    if (novyZavet) context.resources.getStringArray(R.array.semuxan)
                    else context.resources.getStringArray(R.array.semuxas)
                }
                PEREVODSINOIDAL -> {
                    if (novyZavet) context.resources.getStringArray(R.array.sinoidaln)
                    else context.resources.getStringArray(R.array.sinoidals)
                }
                PEREVODNADSAN -> {
                    context.resources.getStringArray(R.array.psalter_list)
                }
                PEREVODBOKUNA -> {
                    if (novyZavet) context.resources.getStringArray(R.array.bokunan)
                    else context.resources.getStringArray(R.array.bokunas)
                }
                PEREVODCARNIAUSKI -> {
                    if (novyZavet) context.resources.getStringArray(R.array.charniauskin)
                    else context.resources.getStringArray(R.array.charniauskis)
                }
                else -> {
                    if (novyZavet) context.resources.getStringArray(R.array.semuxan)
                    else context.resources.getStringArray(R.array.semuxas)
                }
            }
            val knigaTitle = list[kniga]
            val t1 = knigaTitle.indexOf("#")
            val t2 = knigaTitle.indexOf("#", t1 + 1)
            val numarKnigi = knigaTitle.substring(t2 + 1).toInt()
            val knigaglava = "$numarKnigi${glava + 1}".toLong()
            var remove = true
            for (i in 0 until arrayListVybranoe.size) {
                if (arrayListVybranoe[i].id == knigaglava) {
                    arrayListVybranoe.removeAt(i)
                    remove = false
                    break
                }
            }
            if (remove) arrayListVybranoe.add(0, VybranoeBibliaData(knigaglava, "$title ${glava + 1}", numarKnigi, glava + 1, novyZavet, perevod))
            val gson = Gson()
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val prefEditors = k.edit()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            val gsonSave = gson.toJson(arrayListVybranoe, type)
            when (perevod) {
                PEREVODSEMUXI -> prefEditors.putString("bibleVybranoeSemuxa", gsonSave)
                PEREVODSINOIDAL -> prefEditors.putString("bibleVybranoeSinoidal", gsonSave)
                PEREVODNADSAN -> prefEditors.putString("bibleVybranoeNadsan", gsonSave)
                PEREVODBOKUNA -> prefEditors.putString("bibleVybranoeBokuna", gsonSave)
                PEREVODCARNIAUSKI -> prefEditors.putString("bibleVybranoeCarniauski", gsonSave)
            }
            prefEditors.apply()
            return remove
        }
    }
}