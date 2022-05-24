package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogClearHishory : DialogFragment() {
    private lateinit var mListener: DialogClearHistoryListener
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogClearHistoryListener {
        fun cleanFullHistory()
        fun cleanHistory(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogClearHistoryListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogClearHistoryListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.clean_histopy_title)
            if ((arguments?.getString("itemName") ?: "") == "")
                binding.content.text = getString(R.string.all_clean_histopy)
            else
                binding.content.text = getString(R.string.all_clean_item_histopy, arguments?.getString("itemName")?: "")
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            if ((arguments?.getInt("position") ?: -1) == -1)
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.cleanFullHistory() }
            else
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.cleanHistory(arguments?.getInt("position")?: -1) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(position: Int = -1, itemName: String = ""): DialogClearHishory {
            val dialogClearHishory = DialogClearHishory()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("itemName", itemName)
            dialogClearHishory.arguments = bundle
            return dialogClearHishory
        }
    }
}