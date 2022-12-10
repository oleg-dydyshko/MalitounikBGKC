package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import java.util.*

class DialogErrorData : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var dialogErrorDataListener: DialogErrorDataListener? = null

    interface DialogErrorDataListener {
        fun setDataKaliandara()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogErrorDataListener = try {
                context as DialogErrorDataListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogErrorDataListener")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = getString(R.string.dialog_error_data)
            val c = Calendar.getInstance()
            val date = arguments?.getInt("date") ?: c[Calendar.DATE]
            val month = arguments?.getInt("month") ?: c[Calendar.MONTH]
            val year = arguments?.getInt("year") ?: c[Calendar.YEAR]
            binding.content.text = getString(R.string.dialog_error_data_opisanie, date, it.resources.getStringArray(R.array.meciac_smoll)[month], year)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            val prefEditor = k.edit()
            prefEditor.putBoolean("check_notifi", false)
            prefEditor.apply()
            builder.setPositiveButton(resources.getText(R.string.dialog_error_data_ok)) { dialog: DialogInterface, _: Int ->
                dialogErrorDataListener?.setDataKaliandara()
                dialog.cancel()
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(date: Int, month: Int, year: Int): DialogErrorData {
            val bundle = Bundle()
            bundle.putInt("date", date)
            bundle.putInt("month", month)
            bundle.putInt("year", year)
            val dialogErrorData = DialogErrorData()
            dialogErrorData.arguments = bundle
            return dialogErrorData
        }
    }
}