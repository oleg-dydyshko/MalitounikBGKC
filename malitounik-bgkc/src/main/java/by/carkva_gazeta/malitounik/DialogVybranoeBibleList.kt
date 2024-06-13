package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.carkva_gazeta.malitounik.databinding.DialogVybranoeBibleListBinding
import by.carkva_gazeta.malitounik.databinding.ListItemBinding
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

class DialogVybranoeBibleList : DialogFragment(), DialogDeliteBibliaVybranoe.DialogDeliteBibliVybranoeListener {
    private val dzenNoch: Boolean
        get() {
            var dzn = false
            activity?.let {
                dzn = (it as BaseActivity).getBaseDzenNoch()
            }
            return dzn
        }
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0
    private var _binding: DialogVybranoeBibleListBinding? = null
    private val binding get() = _binding!!
    private var resetTollbarJob: Job? = null
    private lateinit var alert: AlertDialog
    private var listener: DialogVybranoeBibleListListener? = null

    interface DialogVybranoeBibleListListener {
        fun onAllDeliteBible()
    }

    fun setDialogVybranoeBibleListListener(listener: DialogVybranoeBibleListListener) {
        this.listener = listener
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun vybranoeDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun vybranoeDelite(position: Int) {
        activity?.let { activity ->
            binding.dragListView.adapter.removeItem(position)
            binding.dragListView.resetSwipedViews(null)
            val gson = Gson()
            val prefEditors = k.edit()
            if (arrayListVybranoe.isEmpty()) {
                when (biblia) {
                    PEREVODSEMUXI -> prefEditors.remove("bibleVybranoeSemuxa")
                    PEREVODSINOIDAL -> prefEditors.remove("bibleVybranoeSinoidal")
                    PEREVODNADSAN -> prefEditors.remove("bibleVybranoeNadsan")
                    PEREVODBOKUNA -> prefEditors.remove("bibleVybranoeBokuna")
                    PEREVODCARNIAUSKI -> prefEditors.remove("bibleVybranoeCarniauski")
                }
                var posDelite = -1
                MenuVybranoe.vybranoe.forEachIndexed { index, it ->
                    if (it.resurs == biblia) {
                        posDelite = index
                        return@forEachIndexed
                    }
                }
                if (posDelite != -1) {
                    MenuVybranoe.vybranoe.removeAt(posDelite)
                    val file = File("${activity.filesDir}/Vybranoe.json")
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    file.writer().use {
                        it.write(gson.toJson(MenuVybranoe.vybranoe, type))
                    }
                }
                listener?.onAllDeliteBible()
                dialog?.cancel()
            } else {
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
                when (biblia) {
                    PEREVODSEMUXI -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe, type))
                    PEREVODSINOIDAL -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe, type))
                    PEREVODNADSAN -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe, type))
                    PEREVODBOKUNA -> prefEditors.putString("bibleVybranoeBokuna", gson.toJson(arrayListVybranoe, type))
                    PEREVODCARNIAUSKI -> prefEditors.putString("bibleVybranoeCarniauski", gson.toJson(arrayListVybranoe, type))
                }
            }
            prefEditors.apply()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        (activity as? BaseActivity)?.let { activity ->
            _binding = DialogVybranoeBibleListBinding.inflate(layoutInflater)
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(activity, style)
            builder.setView(binding.root)
            binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            var bibleVybranoe = ""
            when (biblia) {
                PEREVODSEMUXI -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
                PEREVODSINOIDAL -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
                PEREVODNADSAN -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
                PEREVODBOKUNA -> bibleVybranoe = k.getString("bibleVybranoeBokuna", "") ?: ""
                PEREVODCARNIAUSKI -> bibleVybranoe = k.getString("bibleVybranoeCarniauski", "") ?: ""
            }
            if (bibleVybranoe != "") arrayListVybranoe = gson.fromJson(bibleVybranoe, type)
            binding.dragListView.setLayoutManager(LinearLayoutManager(activity))
            binding.dragListView.setAdapter(ItemAdapter(activity, arrayListVybranoe, R.id.image, false), false)
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
                        dialog.setDialogDeliteBibliVybranoeListener(this@DialogVybranoeBibleList)
                        dialog.show(childFragmentManager, "DialogDeliteBibliaVybranoe")
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
                binding.textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                binding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
                binding.textView.setBackgroundResource(R.drawable.selector_dark_list)
            }
            binding.textView.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if ((activity as? BaseActivity)?.checkmoduleResources() == true) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.MARANATA)
                    intent.putExtra("cytanneMaranaty", biblia(biblia.toInt()))
                    intent.putExtra("prodoljyt", true)
                    intent.putExtra("vybranae", true)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            builder.setPositiveButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.text = resources.getText(R.string.str_short_label1)
        when (biblia) {
            PEREVODSEMUXI -> binding.subtitleToolbar.text = getString(R.string.title_biblia)
            PEREVODSINOIDAL -> binding.subtitleToolbar.text = getString(R.string.bsinaidal)
            PEREVODNADSAN -> binding.subtitleToolbar.text = getString(R.string.title_psalter)
            PEREVODBOKUNA -> binding.subtitleToolbar.text = getString(R.string.title_biblia_bokun)
            PEREVODCARNIAUSKI -> binding.subtitleToolbar.text = getString(R.string.title_biblia_charniauski)
        }
    }

    private fun fullTextTollbar() {
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar()
        } else {
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar()
            }
        }
    }

    private fun resetTollbar() {
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
        binding.subtitleToolbar.isSingleLine = true
    }

    private fun biblia(perevod: Int): String {
        var result = ""
        val sb = StringBuilder()
        arrayListVybranoe.forEachIndexed { index, vybranoeBibliaData ->
            if (perevod == 3) {
                result = "Пс"
            } else {
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
                    var vybranoeBibliaKniga = vybranoeBibliaData.kniga
                    if (perevod == 1 || perevod == 4 || perevod == 5) {
                        when (vybranoeBibliaData.kniga) {
                            16 -> vybranoeBibliaKniga = 19
                            17 -> vybranoeBibliaKniga = 20
                            18 -> vybranoeBibliaKniga = 21
                            19 -> vybranoeBibliaKniga = 22
                            20 -> vybranoeBibliaKniga = 23
                            21 -> vybranoeBibliaKniga = 24
                            22 -> vybranoeBibliaKniga = 27
                            23 -> vybranoeBibliaKniga = 28
                            24 -> vybranoeBibliaKniga = 29
                            25 -> vybranoeBibliaKniga = 32
                            26 -> vybranoeBibliaKniga = 33
                            27 -> vybranoeBibliaKniga = 34
                            28 -> vybranoeBibliaKniga = 35
                            29 -> vybranoeBibliaKniga = 36
                            30 -> vybranoeBibliaKniga = 37
                            31 -> vybranoeBibliaKniga = 38
                            32 -> vybranoeBibliaKniga = 39
                            33 -> vybranoeBibliaKniga = 40
                            34 -> vybranoeBibliaKniga = 41
                            35 -> vybranoeBibliaKniga = 42
                            36 -> vybranoeBibliaKniga = 43
                            37 -> vybranoeBibliaKniga = 44
                            38 -> vybranoeBibliaKniga = 45
                        }
                    }
                    if (perevod == 5) {
                        when (vybranoeBibliaData.kniga) {
                            39 -> vybranoeBibliaKniga = 17
                            40 -> vybranoeBibliaKniga = 18
                            41 -> vybranoeBibliaKniga = 25
                            42 -> vybranoeBibliaKniga = 26
                            43 -> vybranoeBibliaKniga = 31
                            44 -> vybranoeBibliaKniga = 46
                            45 -> vybranoeBibliaKniga = 47
                        }
                    }
                    when (vybranoeBibliaKniga) {
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
            }
            val delimiter = if (arrayListVybranoe.size == index + 1) ""
            else "; "
            sb.append(result).append(" ").append(vybranoeBibliaData.glava).append(delimiter)
        }
        return sb.toString()
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
                (activity as? BaseActivity)?.let {
                    if (it.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.MARANATA)
                        intent.putExtra("cytanneMaranaty", biblia(biblia.toInt()))
                        intent.putExtra("vybranae", true)
                        intent.putExtra("title", mItemList[bindingAdapterPosition].title)
                        startActivity(intent)
                    } else {
                        it.installFullMalitounik()
                    }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                val dialog = DialogDeliteBibliaVybranoe.getInstance(bindingAdapterPosition, arrayListVybranoe[bindingAdapterPosition].title)
                dialog.setDialogDeliteBibliVybranoeListener(this@DialogVybranoeBibleList)
                dialog.show(childFragmentManager, "DialogDeliteBibliaVybranoe")
                return true
            }
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
        var biblia = PEREVODSEMUXI

        fun checkVybranoe(kniga: Int, glava: Int, perevod: String): Boolean {
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val knigaglava = "${kniga + 1}${glava + 1}".toLong()
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

        fun setVybranoe(title: String, kniga: Int, glava: Int, perakvad: String, novyZavet: Boolean = false): Boolean {
            val knigaglava = "${kniga + 1}${glava + 1}".toLong()
            var remove = true
            for (i in 0 until arrayListVybranoe.size) {
                if (arrayListVybranoe[i].id == knigaglava) {
                    arrayListVybranoe.removeAt(i)
                    remove = false
                    break
                }
            }
            if (remove) arrayListVybranoe.add(0, VybranoeBibliaData(knigaglava, "$title ${glava + 1}", kniga, glava + 1, novyZavet, perakvad))
            val gson = Gson()
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val prefEditors = k.edit()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            val gsonSave = gson.toJson(arrayListVybranoe, type)
            when (perakvad) {
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