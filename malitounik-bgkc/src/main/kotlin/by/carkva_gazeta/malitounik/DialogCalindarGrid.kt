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
import com.woxthebox.draglistview.DragItemAdapter

class DialogCalindarGrid : DialogFragment() {

    companion object {
        fun getInstance(post: Int, ton: String, denNedzeli: Int, data: Int, mun: Int): DialogCalindarGrid {
            val bundle = Bundle()
            bundle.putInt("post", post)
            bundle.putString("ton", ton)
            bundle.putInt("denNedzeli", denNedzeli)
            bundle.putInt("data", data)
            bundle.putInt("mun", mun)
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
    private var mun = 0

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
            mun = arguments?.getInt("mun") ?: 0
            val mItemArray = ArrayList<CalindarGrigData>()
            if (post == 3 || post == 4) {
                mItemArray.add(CalindarGrigData(1, R.drawable.moon2_white, "Вячэрня"))
                mItemArray.add(CalindarGrigData(2, R.drawable.moon_white, "Павячэрніца"))
                mItemArray.add(CalindarGrigData(3, R.drawable.sun_white, "Полуночніца"))
                mItemArray.add(CalindarGrigData(4, R.drawable.sun2_white, "Ютрань"))
                mItemArray.add(CalindarGrigData(5, R.drawable.clock_white, "Гадзіны"))
                mItemArray.add(CalindarGrigData(6, R.drawable.carkva_white, "Літургія"))
                mItemArray.add(CalindarGrigData(7, R.drawable.man_white, "Жыцьці"))
                mItemArray.add(CalindarGrigData(8, R.drawable.book_white, "Пярліны"))
                mItemArray.add(CalindarGrigData(9, R.drawable.kanon_white, "Устаў"))
            } else {
                mItemArray.add(CalindarGrigData(1, R.drawable.moon2_black, "Вячэрня"))
                mItemArray.add(CalindarGrigData(2, R.drawable.moon_black, "Павячэрніца"))
                mItemArray.add(CalindarGrigData(3, R.drawable.sun_black, "Полуночніца"))
                mItemArray.add(CalindarGrigData(4, R.drawable.sun2_black, "Ютрань"))
                mItemArray.add(CalindarGrigData(5, R.drawable.clock_black, "Гадзіны"))
                mItemArray.add(CalindarGrigData(6, R.drawable.carkva_black, "Літургія"))
                mItemArray.add(CalindarGrigData(7, R.drawable.man_black, "Жыцьці"))
                mItemArray.add(CalindarGrigData(8, R.drawable.book_black, "Пярліны"))
                mItemArray.add(CalindarGrigData(9, R.drawable.kanon_black, "Устаў"))
            }
            binding.dragGridView.setLayoutManager(GridLayoutManager(it, 3))
            val listAdapter = ItemAdapter(mItemArray, R.id.item_layout, true)
            binding.dragGridView.setAdapter(listAdapter, true)
            binding.dragGridView.setCanDragHorizontally(false)
            binding.dragGridView.setCanDragVertically(false)
            binding.dragGridView.setCustomDragItem(null)
        }
        return alert
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private inner class ItemAdapter(list: ArrayList<CalindarGrigData>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<CalindarGrigData, ItemAdapter.ViewHolder>() {
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
            /*if (dzenNoch) {
                view.itemLayout.setBackgroundResource(R.drawable.selector_dark_list)
                view.root.setBackgroundResource(R.color.colorprimary_material_dark)
            } else {
                view.itemLayout.setBackgroundResource(R.drawable.selector_default_list)
                view.root.setBackgroundResource(R.color.colorDivider)
            }*/
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = mItemList[position].title
            holder.mText.text = text
            holder.itemView.tag = mItemList[position]
            holder.mImage.setImageResource(mItemList[position].image)
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
                    if (MainActivity.checkmoduleResources(activity)) {
                        when (itemList[adapterPosition].id.toInt()) {
                            6 -> {
                                val intent = Intent()
                                intent.setClassName(fragmentActivity, MainActivity.TON)
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
                            7 -> {
                                val i = Intent()
                                i.setClassName(fragmentActivity, MainActivity.OPISANIE)
                                i.putExtra("mun", mun)
                                i.putExtra("day", data)
                                startActivity(i)
                                dialog?.cancel()
                            }
                            4 -> {
                                val data = resources.getStringArray(R.array.bogaslugbovuia)
                                val intent = Intent()
                                intent.setClassName(fragmentActivity, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[3])
                                intent.putExtra("resurs", "bogashlugbovya6")
                                startActivity(intent)
                                dialog?.cancel()
                            }
                            else -> {
                                MainActivity.toastView(fragmentActivity, itemList[adapterPosition].title)
                            }
                        }
                    } else {
                        val dadatak = DialogInstallDadatak()
                        fragmentManager?.let { dadatak.show(it, "dadatak") }
                    }
                }
                /*if (MainActivity.checkmoduleResources(this@CalindarGrid)) {
                    val f = itemList[adapterPosition]
                    val intent = Intent()
                    intent.setClassName(this@CalindarGrid, MainActivity.MYNATATKIVIEW)
                    intent.putExtra("filename", "Mae_malitvy_" + f.id)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(supportFragmentManager, "dadatak")
                }*/
            }

            override fun onItemLongClicked(view: View): Boolean {
                /*fragmentManager?.let {
                    val contextMenu = DialogContextMenu.getInstance(adapterPosition, itemList[adapterPosition].title)
                    contextMenu.show(it, "context_menu")
                }*/
                return true
            }
        }

        init {
            itemList = list
        }
    }

    private data class CalindarGrigData(val id: Long, val image: Int, val title: String)
}