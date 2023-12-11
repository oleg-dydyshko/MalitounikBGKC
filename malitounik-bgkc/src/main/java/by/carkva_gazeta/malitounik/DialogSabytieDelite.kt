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

class DialogSabytieDelite : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var dialogSabytieDeliteListener: DialogSabytieDeliteListener? = null
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogSabytieDeliteListener {
        fun sabytieDelOld()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogSabytieDeliteListener = try {
                context as DialogSabytieDeliteListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSabytieDeliteListener")
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
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.remove)
            binding.content.text = getString(R.string.remove_sabytie_iak)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setPositiveButton(getString(R.string.sabytie_del_all)) { _: DialogInterface?, _: Int ->
                val dialog = DialogSabytieDeliteAll()
                dialog.show(parentFragmentManager, "dialogSabytieDeliteAll")
            }
            ad.setNeutralButton(getString(R.string.sabytie_del_old)) { _: DialogInterface?, _: Int ->
                dialogSabytieDeliteListener?.sabytieDelOld()
            }
            ad.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }
}