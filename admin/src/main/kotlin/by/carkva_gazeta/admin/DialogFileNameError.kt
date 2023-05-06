package by.carkva_gazeta.admin

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
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogFileNameError : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var listener: DialogFileNameErrorListener? = null

    internal interface DialogFileNameErrorListener {
        fun renameFileName()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            listener = try {
                context as DialogFileNameErrorListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogFileNameErrorListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = getString(R.string.error_up)
            binding.content.setText(R.string.file_name_error)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
            binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getText(R.string.vypravic)) { _: DialogInterface, _: Int ->
                listener?.renameFileName()
            }
            builder.setNegativeButton(resources.getText(R.string.cansel)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
        }
        return alert
    }
}