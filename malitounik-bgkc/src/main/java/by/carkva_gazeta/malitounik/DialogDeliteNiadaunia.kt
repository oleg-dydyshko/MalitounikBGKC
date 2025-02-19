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

class DialogDeliteNiadaunia : DialogFragment() {
    private var mListener: DialogDeliteNiadauniaListener? = null
    private var fileName = ""
    private var massege = ""
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    interface DialogDeliteNiadauniaListener {
        fun deliteNiadaunia(fileName: String)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = arguments?.getString("fileName") ?: ""
        massege = arguments?.getString("massege") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteNiadauniaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            binding.title.text = resources.getString(R.string.remove)
            binding.content.text = getString(R.string.delite_niadaunia, massege)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.deliteNiadaunia(fileName) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialod: DialogInterface, _: Int -> dialod.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(fileName: String, massege: String): DialogDeliteNiadaunia {
            val dialogDeliteNiadaunia = DialogDeliteNiadaunia()
            val bundle = Bundle()
            bundle.putString("fileName", fileName)
            bundle.putString("massege", massege)
            dialogDeliteNiadaunia.arguments = bundle
            return dialogDeliteNiadaunia
        }
    }
}