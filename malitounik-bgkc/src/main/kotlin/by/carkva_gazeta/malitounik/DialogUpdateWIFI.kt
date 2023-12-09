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
import java.text.DecimalFormat

class DialogUpdateWIFI : DialogFragment() {
    private var mListener: DialogUpdateListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var size = 0f

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener?.onUpdateNegativeWIFI()
    }

    internal interface DialogUpdateListener {
        fun onUpdatePositiveWIFI()
        fun onUpdateNegativeWIFI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogUpdateListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogUpdateListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.setText(R.string.update_title2)
            size = arguments?.getFloat("size", 0f) ?: 0f
            val sizeProgram = if (size == 0f) {
                " "
            } else {
                " ${formatFigureTwoPlaces(size / 1024 / 1024)} Мб "
            }
            binding.content.text = getString(R.string.download_opisanie, sizeProgram)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.dazvolic)) { _: DialogInterface?, _: Int -> mListener?.onUpdatePositiveWIFI() }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { _: DialogInterface, _: Int -> mListener?.onUpdateNegativeWIFI() }
        }
        return builder.create()
    }

    private fun formatFigureTwoPlaces(value: Float): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value.toDouble())
    }

    companion object {
        fun getInstance(size: Float): DialogUpdateWIFI {
            val dialogOpisanieWIFI = DialogUpdateWIFI()
            val bundle = Bundle()
            bundle.putFloat("size", size)
            dialogOpisanieWIFI.arguments = bundle
            return dialogOpisanieWIFI
        }
    }
}