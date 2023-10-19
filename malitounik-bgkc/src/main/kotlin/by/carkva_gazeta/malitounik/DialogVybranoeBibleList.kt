package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
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
                    "1" -> prefEditors.remove("bibleVybranoeSemuxa")
                    "2" -> prefEditors.remove("bibleVybranoeSinoidal")
                    "3" -> prefEditors.remove("bibleVybranoeNadsan")
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
                    "1" -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe, type))
                    "2" -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe, type))
                    "3" -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe, type))
                }
            }
            prefEditors.apply()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        (activity as? BaseActivity)?.let { activity ->
            _binding = DialogVybranoeBibleListBinding.inflate(LayoutInflater.from(activity))
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlackVybranoe
            val builder = AlertDialog.Builder(activity, style)
            builder.setView(binding.root)
            if (dzenNoch) binding.appBarLayout2.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
            binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            var bibleVybranoe = ""
            when (biblia) {
                "1" -> bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
                "2" -> bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
                "3" -> bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
            }
            if (bibleVybranoe != "") arrayListVybranoe = gson.fromJson(bibleVybranoe, type)
            binding.dragListView.setLayoutManager(LinearLayoutManager(activity))
            binding.dragListView.setAdapter(ItemAdapter(arrayListVybranoe, R.id.image, false), false)
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
                        "1" -> prefEditors.putString("bibleVybranoeSemuxa", gson.toJson(arrayListVybranoe, type))
                        "2" -> prefEditors.putString("bibleVybranoeSinoidal", gson.toJson(arrayListVybranoe, type))
                        "3" -> prefEditors.putString("bibleVybranoeNadsan", gson.toJson(arrayListVybranoe, type))
                    }
                    prefEditors.apply()
                }
            })
            setTollbarTheme()
            if (dzenNoch) {
                binding.view.setBackgroundResource(R.color.colorprimary_material_dark)
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
                    intent.setClassName(activity, MainActivity.BIBLIAVYBRANOE)
                    intent.putExtra("biblia", biblia)
                    intent.putExtra("title", binding.subtitleToolbar.text.toString())
                    intent.putExtra("prodoljyt", true)
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.titleToolbar.text = resources.getText(R.string.str_short_label1)
        when (biblia) {
            "1" -> binding.subtitleToolbar.text = getString(R.string.title_biblia)
            "2" -> binding.subtitleToolbar.text = getString(R.string.bsinaidal)
            "3" -> binding.subtitleToolbar.text = getString(R.string.title_psalter)
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

    private inner class ItemAdapter(list: ArrayList<VybranoeBibliaData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<VybranoeBibliaData, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.root.supportedSwipeDirection = SwipeDirection.LEFT
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            if (dzenNoch) {
                view.itemLeft.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemRight.setTextColor(ContextCompat.getColor(parent.context, R.color.colorPrimary_black))
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark)
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
                        intent.setClassName(it, MainActivity.BIBLIAVYBRANOE)
                        intent.putExtra("biblia", biblia)
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
        var arrayListVybranoe = ArrayList<VybranoeBibliaData>()
        var biblia = "1"

        fun checkVybranoe(kniga: Int, glava: Int, bibleName: Int = 1): Boolean {
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val knigaglava = "${kniga + 1}${glava + 1}".toLong()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
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

        fun setVybranoe(title: String, kniga: Int, glava: Int, novyZavet: Boolean = false, bibleName: Int = 1): Boolean {
            val knigaglava = "${kniga + 1}${glava + 1}".toLong()
            var remove = true
            for (i in 0 until arrayListVybranoe.size) {
                if (arrayListVybranoe[i].id == knigaglava) {
                    arrayListVybranoe.removeAt(i)
                    remove = false
                    break
                }
            }
            if (remove) arrayListVybranoe.add(0, VybranoeBibliaData(knigaglava, "$title ${glava + 1}", kniga, glava + 1, novyZavet, bibleName))
            val gson = Gson()
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val prefEditors = k.edit()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
            val gsonSave = gson.toJson(arrayListVybranoe, type)
            when (bibleName) {
                1 -> prefEditors.putString("bibleVybranoeSemuxa", gsonSave)
                2 -> prefEditors.putString("bibleVybranoeSinoidal", gsonSave)
                3 -> prefEditors.putString("bibleVybranoeNadsan", gsonSave)
            }
            prefEditors.apply()
            return remove
        }
    }
}