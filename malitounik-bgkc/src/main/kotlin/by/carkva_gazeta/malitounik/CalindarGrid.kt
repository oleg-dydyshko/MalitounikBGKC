package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import by.carkva_gazeta.malitounik.databinding.CalindarGridBinding
import by.carkva_gazeta.malitounik.databinding.GridItemBinding
import com.woxthebox.draglistview.DragItemAdapter

class CalindarGrid : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var _binding: CalindarGridBinding? = null
    private val binding get() = _binding!!
    private var mLastClickTime: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        activity?.let {
            _binding = CalindarGridBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            alert = builder.create()
            val mItemArray = ArrayList<CalindarGrigData>()
            for (i in 1..9) {
                mItemArray.add(CalindarGrigData(i.toLong(), "Item $i"))
            }
            binding.dragGridView.setLayoutManager(GridLayoutManager(it, 3))
            val listAdapter = ItemAdapter(mItemArray, R.id.item_layout, true)
            binding.dragGridView.setAdapter(listAdapter, true)
            binding.dragGridView.setCanDragHorizontally(true)
            binding.dragGridView.setCanDragVertically(true)
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
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].id
        }

        private inner class ViewHolder(itemView: GridItemBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                activity?.let { MainActivity.toastView(it, "Item ${itemList[adapterPosition].id}") }
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
                fragmentManager?.let {
                    val contextMenu = DialogContextMenu.getInstance(adapterPosition, itemList[adapterPosition].title)
                    contextMenu.show(it, "context_menu")
                }
                return true
            }
        }

        init {
            itemList = list
        }
    }

    private data class CalindarGrigData(val id: Long, val title: String)
}