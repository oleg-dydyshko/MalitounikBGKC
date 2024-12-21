package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogBibleRazdelBinding
import by.carkva_gazeta.malitounik.databinding.DialogBibleRazdelItemBinding
import com.woxthebox.draglistview.DragItemAdapter


class DialogBibleRazdel : DialogFragment() {
    private var fullGlav = 0
    private var position = 1
    private var mListener: DialogBibleRazdelListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var binding: DialogBibleRazdelBinding? = null
    private var mItemArray = ArrayList<Int>()
    private var mLastClickTime: Long = 0

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    internal interface DialogBibleRazdelListener {
        fun onComplete(glava: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogBibleRazdelListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibleRazdelListener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullGlav = arguments?.getInt("full_glav") ?: 0
        position = arguments?.getInt("position") ?: 1
    }

    private fun calculateNoOfColumns(context: Context, width: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = width / displayMetrics.density
        val scalingFactor = 50
        val columnCount = (dpWidth / scalingFactor).toInt()
        return (if (columnCount >= 2) columnCount else 2)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            binding = DialogBibleRazdelBinding.inflate(layoutInflater)
            binding?.let { binding ->
                val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
                var style = R.style.AlertDialogTheme
                if (dzenNoch) style = R.style.AlertDialogThemeBlack
                binding.dragGridView.post {
                    val glm = GridLayoutManager(it, calculateNoOfColumns(it, binding.dragGridView.width))
                    binding.dragGridView.setLayoutManager(glm)
                    for (i in 1..fullGlav) mItemArray.add(i)
                    val listAdapter = ItemAdapter(mItemArray, R.id.item_layout, true)
                    binding.dragGridView.setAdapter(listAdapter, true)
                    binding.dragGridView.setCustomDragItem(null)
                    binding.dragGridView.isDragEnabled = false
                    if (savedInstanceState == null) glm.scrollToPositionWithOffset(position, 0)
                }
                builder = AlertDialog.Builder(it, style)
                builder.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
                builder.setView(binding.root)
            }
        }
        return builder.create()
    }

    private inner class ItemAdapter(list: ArrayList<Int>, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<Int, ItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = DialogBibleRazdelItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val text = mItemList[position]
            holder.mText.text = text.toString()
            holder.itemView.tag = mItemList[position]
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].toLong()
        }

        private inner class ViewHolder(itemView: DialogBibleRazdelItemBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            var mText = itemView.text
            override fun onItemClicked(view: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mListener?.onComplete(itemList[bindingAdapterPosition].toInt() - 1)
                mLastClickTime = SystemClock.elapsedRealtime()
                dialog?.dismiss()
            }
        }

        init {
            itemList = list
        }
    }

    companion object {
        fun getInstance(fullGlav: Int, position: Int): DialogBibleRazdel {
            val instance = DialogBibleRazdel()
            val args = Bundle()
            args.putInt("full_glav", fullGlav)
            args.putInt("position", position)
            instance.arguments = args
            return instance
        }
    }
}