package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import by.carkva_gazeta.malitounik.databinding.CalindarGridBinding
import by.carkva_gazeta.malitounik.databinding.GridItemBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView

class DialogCalindarGrid : DialogFragment() {

    companion object {
        fun getInstance(post: Int, ton: Int, denNedzeli: Int, data: Int, mun: Int, raznicia: Int): DialogCalindarGrid {
            val bundle = Bundle()
            bundle.putInt("post", post)
            bundle.putInt("ton", ton)
            bundle.putInt("denNedzeli", denNedzeli)
            bundle.putInt("data", data)
            bundle.putInt("mun", mun)
            bundle.putInt("raznicia", raznicia)
            val dialog = DialogCalindarGrid()
            dialog.arguments = bundle
            return dialog
        }
    }

    private lateinit var alert: AlertDialog
    private var _binding: CalindarGridBinding? = null
    private val binding get() = _binding!!
    private var mLastClickTime: Long = 0
    private var post = 0
    private var ton = 0
    private var denNedzeli = 1
    private var data = 1
    private var mun = 1
    private var raznicia = 400
    private var mItemArray = ArrayList<Int>()
    private val slugba = SlugbovyiaTextu()

    private fun getImage(id: Int, imageWhite: Boolean = false, imageSecondary: Boolean = false): Int {
        if (id == 1) {
            if (imageWhite) return R.drawable.moon2_white
            if (imageSecondary) return R.drawable.moon2_secondary
            return R.drawable.moon2_black
        }
        if (id == 2) {
            if (imageWhite) return R.drawable.moon_white
            if (imageSecondary) return R.drawable.moon_secondary
            return R.drawable.moon_black
        }
        if (id == 3) {
            if (imageWhite) return R.drawable.sun_white
            if (imageSecondary) return R.drawable.sun_secondary
            return R.drawable.sun_black
        }
        if (id == 4) {
            if (imageWhite) return R.drawable.sun2_white
            if (imageSecondary) return R.drawable.sun2_secondary
            return R.drawable.sun2_black
        }
        if (id == 5) {
            if (imageWhite) return R.drawable.clock_white
            if (imageSecondary) return R.drawable.clock_secondary
            return R.drawable.clock_black
        }
        if (id == 6) {
            if (imageWhite) return R.drawable.carkva_white
            if (imageSecondary) return 0
            return R.drawable.carkva_black
        }
        if (id == 7) {
            if (imageWhite) return R.drawable.man_white
            if (imageSecondary) return 0
            return R.drawable.man_black
        }
        if (id == 8) {
            if (imageWhite) return R.drawable.book_white
            if (imageSecondary) return R.drawable.book_secondary
            return R.drawable.book_black
        }
        if (id == 9) {
            if (imageWhite) return R.drawable.kanon_white
            if (imageSecondary) return R.drawable.kanon_secondary
            return R.drawable.kanon_black
        }
        return 0
    }

    private fun getTitle(id: Int): String {
        when (id) {
            1 -> return "Вячэрня"
            2 -> return "Павячэрніца"
            3 -> return "Паўночніца"
            4 -> return "Ютрань"
            5 -> return "Гадзіны"
            6 -> return "Літургія"
            7 -> return "Жыцьці"
            8 -> return "Пярліны"
            9 -> return "Устаў"
        }
        return ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        activity?.let {
            _binding = CalindarGridBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            alert = builder.create()
            post = arguments?.getInt("post") ?: 0
            ton = arguments?.getInt("ton") ?: 0
            denNedzeli = arguments?.getInt("denNedzeli") ?: 1
            data = arguments?.getInt("data") ?: 1
            mun = arguments?.getInt("mun") ?: 1
            raznicia = arguments?.getInt("raznicia", 400) ?: 400
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getString("caliandarGrid", "") != "") {
                try {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<Int>>() {}.type
                    mItemArray = gson.fromJson(k.getString("caliandarGrid", ""), type)
                } catch (e: Throwable) {
                    val edit = k.edit()
                    edit.remove("caliandarGrid")
                    edit.apply()
                    for (i in 1..9) mItemArray.add(i)
                }
            } else {
                for (i in 1..9) mItemArray.add(i)
            }
            binding.dragGridView.setLayoutManager(GridLayoutManager(it, 3))
            val listAdapter = ItemAdapter(mItemArray, R.id.item_layout, true)
            binding.dragGridView.setAdapter(listAdapter, true)
            binding.dragGridView.setCanDragHorizontally(true)
            binding.dragGridView.setCanDragVertically(true)
            binding.dragGridView.setCustomDragItem(null)
            binding.dragGridView.setDragListListener(object : DragListView.DragListListener {
                override fun onItemDragStarted(position: Int) {
                }

                override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
                }

                override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                    if (fromPosition != toPosition) {
                        val gson = Gson()
                        val edit = it.getSharedPreferences("biblia", Context.MODE_PRIVATE).edit()
                        edit.putString("caliandarGrid", gson.toJson(mItemArray))
                        edit.apply()
                    }
                }
            })
        }
        return alert
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private inner class ItemAdapter(list: ArrayList<Int>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<Int, ItemAdapter.ViewHolder>() {
        private var dzenNoch = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val k = parent.context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            view.text.textSize = SettingsActivity.GET_FONT_SIZE_MIN
            when (post) {
                1 -> view.itemLayout.setBackgroundResource(R.drawable.selector_grid_bez_posta)
                2 -> view.itemLayout.setBackgroundResource(R.drawable.selector_grid_post)
                3 -> {
                    view.itemLayout.setBackgroundResource(R.drawable.selector_grid_strogi_post)
                    view.text.setTextColor(ContextCompat.getColor(parent.context, R.color.colorWhite))
                }
                4 -> {
                    if (dzenNoch) view.itemLayout.setBackgroundResource(R.drawable.selector_grid_red_dark)
                    else view.itemLayout.setBackgroundResource(R.drawable.selector_grid_red)
                    view.text.setTextColor(ContextCompat.getColor(parent.context, R.color.colorWhite))
                }
                else -> view.itemLayout.setBackgroundResource(R.drawable.selector_grid_gray)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = getTitle(mItemList[position])
            holder.mText.text = text
            holder.itemView.tag = mItemList[position]
            activity?.let {
                if ((!slugba.checkUtran(raznicia) && denNedzeli != 1) && mItemList[position] == 4) {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                } else if (!slugba.checkViachernia(raznicia) && mItemList[position] == 1) {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                } else if (mItemList[position] == 2 || mItemList[position] == 3 || mItemList[position] == 5 || mItemList[position] == 8 || mItemList[position] == 9) {
                    holder.mImage.setImageResource(getImage(mItemList[position], imageSecondary = true))
                    holder.mText.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                } else {
                    if (post == 3 || post == 4) {
                        holder.mImage.setImageResource(getImage(mItemList[position], imageWhite = true))
                    } else {
                        holder.mImage.setImageResource(getImage(mItemList[position]))
                    }
                }
            }
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].toLong()
        }

        private inner class ViewHolder(itemView: GridItemBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            var mImage = itemView.image
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                activity?.let { fragmentActivity ->
                    if (!MainActivity.checkmoduleResources(fragmentActivity)) {
                        val dadatak = DialogInstallDadatak()
                        fragmentManager?.let { dadatak.show(it, "dadatak") }
                        return
                    }
                }
                when (itemList[adapterPosition].toInt()) {
                    1 -> {
                        activity?.let {
                            if (slugba.checkViachernia(raznicia)) {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTU)
                                intent.putExtra("resource", slugba.getResource(raznicia))
                                startActivity(intent)
                                dialog?.cancel()
                            }
                        }
                    }
                    6 -> {
                        if (slugba.checkLiturgia(raznicia)) {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTU)
                                intent.putExtra("resource", slugba.getResource(raznicia, liturgia = true))
                                startActivity(intent)
                                dialog?.cancel()
                            }
                        } else {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.TON)
                                if (ton != 0) {
                                    intent.putExtra("ton", ton)
                                    intent.putExtra("ton_naidzelny", true)
                                } else {
                                    intent.putExtra("ton", denNedzeli - 1)
                                    intent.putExtra("ton_naidzelny", false)
                                }
                                startActivity(intent)
                                dialog?.cancel()
                            }
                        }
                    }
                    7 -> {
                        activity?.let {
                            val i = Intent()
                            i.setClassName(it, MainActivity.OPISANIE)
                            i.putExtra("mun", mun)
                            i.putExtra("day", data)
                            startActivity(i)
                            dialog?.cancel()
                        }
                    }
                    4 -> {
                        if (slugba.checkUtran(raznicia)) {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTU)
                                intent.putExtra("resource", slugba.getResource(raznicia, utran = true))
                                startActivity(intent)
                                dialog?.cancel()
                            }
                        } else if (denNedzeli == 1) {
                            activity?.let {
                                val data = resources.getStringArray(R.array.bogaslugbovuia)
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[3])
                                intent.putExtra("resurs", "bogashlugbovya6")
                                startActivity(intent)
                                dialog?.cancel()
                            }
                        }
                    }
                }

            }
        }

        init {
            itemList = list
        }
    }
}