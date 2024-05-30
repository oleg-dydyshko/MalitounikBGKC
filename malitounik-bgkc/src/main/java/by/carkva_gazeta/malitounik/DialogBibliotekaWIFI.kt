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

class DialogBibliotekaWIFI : DialogFragment() {
    private var listPosition = "0"
    private var mListener: DialogBibliotekaWIFIListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogBibliotekaWIFIListener {
        fun onDialogPositiveClick(listPosition: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listPosition = arguments?.getString("listPosition")?: "0"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogBibliotekaWIFIListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibliotekaWIFIListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            binding.title.setText(R.string.wifi_error)
            binding.content.setText(R.string.download_bibliateka)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite)) 
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.dazvolic)) { _: DialogInterface?, _: Int -> mListener?.onDialogPositiveClick(listPosition) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        return builder.create()
    }

    companion object {
        fun getInstance(listPosition: String): DialogBibliotekaWIFI {
            val instance = DialogBibliotekaWIFI()
            val args = Bundle()
            args.putString("listPosition", listPosition)
            instance.arguments = args
            return instance
        }
    }
}