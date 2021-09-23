package by.carkva_gazeta.admin

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogTextviewCheckboxDisplayBinding

class DialogSaveAsHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewCheckboxDisplayBinding? = null
    private val binding get() = _binding!!
    private var filename = ""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewCheckboxDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.text = resources.getString(R.string.save_as_up)
            val span = SpannableString(resources.getString(R.string.save_as_help))
            val t1 = span.indexOf("скапіяваць")
            val t2 = span.indexOf("(")
            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary)), t1, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(StyleSpan(Typeface.BOLD), t2, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.content.text = span
            builder.setView(binding.root)
            filename = arguments?.getString("filename", "") ?: ""
            binding.checkbox.typeface = MainActivity.createFont(Typeface.NORMAL)
            binding.checkbox.text = getString(R.string.sabytie_check_mun)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val edit = chin.edit()
                if (isChecked) {
                    edit.putBoolean("AdminDialogSaveAsHelp", false)
                } else {
                    edit.putBoolean("AdminDialogSaveAsHelp", true)
                }
                edit.apply()
            }
            builder.setPositiveButton(resources.getString(R.string.save_as_up)) { _: DialogInterface, _: Int ->
                val dialogSaveAsFileExplorer = DialogSaveAsFileExplorer.getInstance(filename)
                dialogSaveAsFileExplorer.show(parentFragmentManager, "dialogSaveAsFileExplorer")
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun newInstance(filename: String): DialogSaveAsHelp {
            val dialogSaveAsHelp = DialogSaveAsHelp()
            val bundle = Bundle()
            bundle.putString("filename", filename)
            dialogSaveAsHelp.arguments = bundle
            return dialogSaveAsHelp
        }
    }
}