package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogDeliteAllNiadaunia : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var listener: DialogDeliteAllNiadauniaListener? = null

    interface DialogDeliteAllNiadauniaListener {
        fun delAllNiadaunia()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            listener = try {
                context as DialogDeliteAllNiadauniaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteAllNiadauniaListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.remove)
            binding.content.text = getString(R.string.delite_all_niadaunia)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface, _: Int -> listener?.delAllNiadaunia() }
            ad.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }
}