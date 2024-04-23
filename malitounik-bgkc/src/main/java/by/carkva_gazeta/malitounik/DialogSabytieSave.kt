package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogSabytieSave : DialogFragment() {
    private var mListener: DialogSabytieSaveListener? = null
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogSabytieSaveListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogSabytieSaveListener
            } catch (e: ClassCastException) {
                throw ClassCastException(activity.toString() + " must implement DialogSabytieSaveListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.sabytie_zmenena)
            binding.content.text = getString(R.string.sabytie_zaxavac)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setNegativeButton(getString(R.string.sabytie_no)) { _: DialogInterface?, _: Int -> mListener?.onDialogNegativeClick() }
            ad.setPositiveButton(getString(R.string.sabytie_yes)) { _: DialogInterface?, _: Int -> mListener?.onDialogPositiveClick() }
            ad.setNeutralButton(getString(R.string.cansel)) { dialogInterface: DialogInterface, _: Int -> dialogInterface.cancel() }
            alert = ad.create()
        }
        return alert
    }
}