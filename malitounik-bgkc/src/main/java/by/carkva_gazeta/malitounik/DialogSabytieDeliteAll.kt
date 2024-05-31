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

class DialogSabytieDeliteAll : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var dialogSabytieDeliteListener: DialogSabytieDeliteAllListener? = null
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogSabytieDeliteAllListener {
        fun sabytieDelAll()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogSabytieDeliteListener = try {
                context as DialogSabytieDeliteAllListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSabytieDeliteAllListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            binding.title.text = resources.getString(R.string.trash_sabytie)
            binding.content.text = getString(R.string.del_all_sabytie)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                dialogSabytieDeliteListener?.sabytieDelAll()
            }
            ad.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }
}