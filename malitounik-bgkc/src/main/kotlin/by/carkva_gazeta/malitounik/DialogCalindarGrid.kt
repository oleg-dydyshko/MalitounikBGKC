package by.carkva_gazeta.malitounik

import android.app.Activity
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
        fun getInstance(post: Int, ton: String, denNedzeli: Int, data: Int, mun: Int, raznicia: String): DialogCalindarGrid {
            val bundle = Bundle()
            bundle.putInt("post", post)
            bundle.putString("ton", ton)
            bundle.putInt("denNedzeli", denNedzeli)
            bundle.putInt("data", data)
            bundle.putInt("mun", mun)
            bundle.putString("raznicia", raznicia)
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
    private var ton = ""
    private var denNedzeli = 1
    private var data = 1
    private var mun = 1
    private var raznicia = "0"
    private var mItemArray = ArrayList<CalindarGrigData>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        activity?.let {
            _binding = CalindarGridBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            alert = builder.create()
            post = arguments?.getInt("post") ?: 0
            ton = arguments?.getString("ton") ?: ""
            denNedzeli = arguments?.getInt("denNedzeli") ?: 1
            data = arguments?.getInt("data") ?: 1
            mun = arguments?.getInt("mun") ?: 1
            raznicia = arguments?.getString("raznicia") ?: "0"
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getString("caliandarGrid", "") != "") {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<CalindarGrigData>>() {}.type
                mItemArray = gson.fromJson(k.getString("caliandarGrid", ""), type)
            } else {
                mItemArray.add(CalindarGrigData(1, R.drawable.moon2_black, R.drawable.moon2_white, R.drawable.moon2_secondary, "Вячэрня"))
                mItemArray.add(CalindarGrigData(2, R.drawable.moon_black, R.drawable.moon_white, R.drawable.moon_secondary, "Павячэрніца"))
                mItemArray.add(CalindarGrigData(3, R.drawable.sun_black, R.drawable.sun_white, R.drawable.sun_secondary, "Паўночніца"))
                mItemArray.add(CalindarGrigData(4, R.drawable.sun2_black, R.drawable.sun2_white, R.drawable.sun2_secondary, "Ютрань"))
                mItemArray.add(CalindarGrigData(5, R.drawable.clock_black, R.drawable.clock_white, R.drawable.clock_secondary, "Гадзіны"))
                mItemArray.add(CalindarGrigData(6, R.drawable.carkva_black, R.drawable.carkva_white, R.drawable.carkva_secondary, "Літургія"))
                mItemArray.add(CalindarGrigData(7, R.drawable.man_black, R.drawable.man_white, R.drawable.man_secondary, "Жыцьці"))
                mItemArray.add(CalindarGrigData(8, R.drawable.book_black, R.drawable.book_white, R.drawable.book_secondary, "Пярліны"))
                mItemArray.add(CalindarGrigData(9, R.drawable.kanon_black, R.drawable.kanon_white, R.drawable.kanon_secondary, "Устаў"))
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

    private fun setUtran(activity: Activity, check: Boolean = false): Boolean {
        var title = ""
        var type = ""
        var checkValue = false
        when (raznicia) {
            "-42" -> {
                title = "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань"
                type = "bogashlugbovya12_8"
            }
            "-35" -> {
                title = "2-ая нядзеля Вялікага посту Вячэрня Ютрань"
                type = "bogashlugbovya13_7"
            }
            "-28" -> {
                title = "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань"
                type = "bogashlugbovya14_8"
            }
            "-21" -> {
                title = "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань"
                type = "bogashlugbovya15_8"
            }
            "-15" -> {
                title = "Субота Акафісту Ютрань"
                type = "bogashlugbovya16_7"
            }
            "-14" -> {
                title = "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань"
                type = "bogashlugbovya16_10"
            }
            "-8" -> {
                title = "Субота Лазара Ютрань"
                type = "bogashlugbovya17_7"
            }
            else -> checkValue = true
        }
        if (!checkValue && !check) {
            val intent = Intent()
            intent.setClassName(activity, MainActivity.SLUGBYVIALIKAGAPOSTU)
            intent.putExtra("id", -1)
            intent.putExtra("title", title)
            intent.putExtra("type", type)
            startActivity(intent)
        }
        return checkValue
    }

    private fun setLiturgia(activity: Activity, check: Boolean = false): Boolean {
        var title = ""
        var type = ""
        var checkValue = false
        when (raznicia) {
            "-42" -> {
                title = "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Лiтургiя сьвятoга Васіля Вялiкага"
                type = "bogashlugbovya12_9"
            }
            "-35" -> {
                title = "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага"
                type = "bogashlugbovya13_8"
            }
            "-28" -> {
                title = "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага"
                type = "bogashlugbovya14_9"
            }
            "-21" -> {
                title = "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага"
                type = "bogashlugbovya15_9"
            }
            "-15" -> {
                title = "Літургія ў суботу Акафісту"
                type = "bogashlugbovya16_8"
            }
            "-14" -> {
                title = "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага"
                type = "bogashlugbovya16_11"
            }
            "-7" -> {
                title = "Літургія"
                type = "bogashlugbovya17_8"
            }
            else -> checkValue = true
        }
        if (!checkValue && !check) {
            val intent = Intent()
            intent.setClassName(activity, MainActivity.SLUGBYVIALIKAGAPOSTU)
            intent.putExtra("id", -1)
            intent.putExtra("title", title)
            intent.putExtra("type", type)
            startActivity(intent)
        }
        return checkValue
    }

    private fun setViachernia(activity: Activity, check: Boolean = false): Boolean {
        var title = ""
        var type = ""
        var checkValue = false
        when (raznicia) {
            "-49" -> {
                title = "Вячэрня ў нядзелю сырную вeчарам"
                type = "bogashlugbovya12_1"
            }
            "-48" -> {
                title = "Панядзeлак 1-га тыдня посту ўвeчары"
                type = "bogashlugbovya12_2"
            }
            "-47" -> {
                title = "Аўтoрак 1-га тыдня посту ўвeчары"
                type = "bogashlugbovya12_3"
            }
            "-46" -> {
                title = "Сeрада 1-га тыдня посту ўвeчары"
                type = "bogashlugbovya12_4"
            }
            "-45" -> {
                title = "Чацьвeр 1-га тыдня посту ўвeчары"
                type = "bogashlugbovya12_5"
            }
            "-44" -> {
                title = "Пятнiца 1-га тыдня пoсту ўвeчары"
                type = "bogashlugbovya12_6"
            }
            "-43" -> {
                title = "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня"
                type = "bogashlugbovya12_7"
            }
            "-42" -> {
                title = "1-ая нядзеля посту ўвечары"
                type = "bogashlugbovya13_1"
            }
            "-41" -> {
                title = "Панядзелак 2-га тыдня посту ўвечары"
                type = "bogashlugbovya13_2"
            }
            "-40" -> {
                title = "Аўторак 2-га тыдня посту ўвечары"
                type = "bogashlugbovya13_3"
            }
            "-39" -> {
                title = "Серада 2-га тыдня посту ўвечары"
                type = "bogashlugbovya13_4"
            }
            "-38" -> {
                title = "Чацьвер 2-га тыдня посту ўвечары"
                type = "bogashlugbovya13_5"
            }
            "-37" -> {
                title = "Пятніца 2-га тыдня посту ўвечары"
                type = "bogashlugbovya13_6"
            }
            "-35" -> {
                title = "2-ая нядзеля посту ўвечары"
                type = "bogashlugbovya14_1"
            }
            "-34" -> {
                title = "Панядзелак 3-га тыдня посту ўвечары"
                type = "bogashlugbovya14_2"
            }
            "-33" -> {
                title = "Аўторак 3-га тыдня посту ўвечары"
                type = "bogashlugbovya14_3"
            }
            "-32" -> {
                title = "Серада 3-га тыдня посту ўвечары"
                type = "bogashlugbovya14_4"
            }
            "-31" -> {
                title = "Чацьвер 3-га тыдня посту ўвечары"
                type = "bogashlugbovya14_5"
            }
            "-30" -> {
                title = "Пятніца 3-га тыдня посту ўвечары"
                type = "bogashlugbovya14_6"
            }
            "-29" -> {
                title = "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня"
                type = "bogashlugbovya14_7"
            }
            "-28" -> {
                title = "3-яя нядзеля посту ўвечары"
                type = "bogashlugbovya15_1"
            }
            "-27" -> {
                title = "Панядзелак 4-га тыдня посту ўвечары"
                type = "bogashlugbovya15_2"
            }
            "-26" -> {
                title = "Аўторак 4-га тыдня посту ўвечары"
                type = "bogashlugbovya15_3"
            }
            "-25" -> {
                title = "Серада 4-га тыдня посту ўвечары"
                type = "bogashlugbovya15_4"
            }
            "-24" -> {
                title = "Чацьвер 4-га тыдня посту ўвечары"
                type = "bogashlugbovya15_5"
            }
            "-23" -> {
                title = "Пятніца 4-га тыдня посту ўвечары"
                type = "bogashlugbovya15_6"
            }
            "-22" -> {
                title = "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня"
                type = "bogashlugbovya15_7"
            }
            "-21" -> {
                title = "4-ая нядзеля посту ўвечары"
                type = "bogashlugbovya16_1"
            }
            "-20" -> {
                title = "Панядзелак 5-га тыдня посту ўвечары"
                type = "bogashlugbovya16_2"
            }
            "-19" -> {
                title = "Аўторак 5-га тыдня посту ўвечары"
                type = "bogashlugbovya16_3"
            }
            "-18" -> {
                title = "Серада 5-га тыдня посту ўвечары"
                type = "bogashlugbovya16_4"
            }
            "-17" -> {
                title = "Чацьвер 5-га тыдня посту ўвечары"
                type = "bogashlugbovya16_5"
            }
            "-16" -> {
                title = "Пятніца 5-га тыдня посту ўвечары"
                type = "bogashlugbovya16_6"
            }
            "-15" -> {
                title = "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня"
                type = "bogashlugbovya16_9"
            }
            "-14" -> {
                title = "5-ая нядзеля посту ўвечары"
                type = "bogashlugbovya17_1"
            }
            "-13" -> {
                title = "Панядзелак 6-га тыдня посту ўвечары"
                type = "bogashlugbovya17_2"
            }
            "-12" -> {
                title = "Аўторак 6-га тыдня посту ўвечары"
                type = "bogashlugbovya17_3"
            }
            "-11" -> {
                title = "Серада 6-га тыдня посту ўвечары"
                type = "bogashlugbovya17_4"
            }
            "-10" -> {
                title = "Чацьвер 6-га тыдня посту ўвечары"
                type = "bogashlugbovya17_5"
            }
            "-9" -> {
                title = "Пятніца 6-га тыдня посту ўвечары"
                type = "bogashlugbovya17_6"
            }
            else -> checkValue = true
        }
        if (!checkValue && !check) {
            val intent = Intent()
            intent.setClassName(activity, MainActivity.SLUGBYVIALIKAGAPOSTU)
            intent.putExtra("id", -1)
            intent.putExtra("title", title)
            intent.putExtra("type", type)
            startActivity(intent)
        }
        return checkValue
    }

    private inner class ItemAdapter(list: ArrayList<CalindarGrigData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) :
        DragItemAdapter<CalindarGrigData, ItemAdapter.ViewHolder>() {
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
            val text = mItemList[position].title
            holder.mText.text = text
            holder.itemView.tag = mItemList[position]
            activity?.let {
                if ((setUtran(it, true) && denNedzeli != 1) && mItemList[position].id == 4L) {
                    holder.mImage.setImageResource(mItemList[position].imageSecondary)
                    holder.mText.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                } else if (setViachernia(it, true) && mItemList[position].id == 1L) {
                    holder.mImage.setImageResource(mItemList[position].imageSecondary)
                    holder.mText.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                } else if (mItemList[position].id == 2L || mItemList[position].id == 3L || mItemList[position].id == 5L || mItemList[position].id == 8L || mItemList[position].id == 9L) {
                    holder.mImage.setImageResource(mItemList[position].imageSecondary)
                    holder.mText.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                } else {
                    if (post == 3 || post == 4) {
                        holder.mImage.setImageResource(mItemList[position].imageWhite)
                    } else {
                        holder.mImage.setImageResource(mItemList[position].imageBlack)
                    }
                }
            }
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].id
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
                when (itemList[adapterPosition].id.toInt()) {
                    1 -> {
                        activity?.let {
                            if (!setViachernia(it)) dialog?.cancel()
                        }
                    }
                    6 -> {
                        activity?.let {
                            if (setLiturgia(it)) {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.TON)
                                if (ton.contains("Тон")) {
                                    if (ton.contains("Тон 1")) intent.putExtra("ton", 1)
                                    if (ton.contains("Тон 2")) intent.putExtra("ton", 2)
                                    if (ton.contains("Тон 3")) intent.putExtra("ton", 3)
                                    if (ton.contains("Тон 4")) intent.putExtra("ton", 4)
                                    if (ton.contains("Тон 5")) intent.putExtra("ton", 5)
                                    if (ton.contains("Тон 6")) intent.putExtra("ton", 6)
                                    if (ton.contains("Тон 7")) intent.putExtra("ton", 7)
                                    if (ton.contains("Тон 8")) intent.putExtra("ton", 8)
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
                        activity?.let {
                            if (setUtran(it) && denNedzeli == 1) {
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

    private data class CalindarGrigData(val id: Long, val imageBlack: Int, val imageWhite: Int, val imageSecondary: Int, val title: String)
}