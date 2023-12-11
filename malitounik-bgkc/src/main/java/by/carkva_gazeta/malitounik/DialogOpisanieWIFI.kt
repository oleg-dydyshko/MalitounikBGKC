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

class DialogOpisanieWIFI : DialogFragment() {
    private var mListener: DialogOpisanieWIFIListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var size = 0f

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener?.onDialogNegativeOpisanieWIFI()
    }

    internal interface DialogOpisanieWIFIListener {
        fun onDialogPositiveOpisanieWIFI()
        fun onDialogNegativeOpisanieWIFI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogOpisanieWIFIListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogOpisanieWIFIListener")
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
            binding.title.setText(R.string.wifi_error)
            size = arguments?.getFloat("size", 0f) ?: 0f
            val sizeImage = if (size == 0f) {
                " "
            } else {
                if (size / 1024 > 1000) {
                    " ${formatFigureTwoPlaces(size / 1024 / 1024)} Мб "
                } else {
                    " ${formatFigureTwoPlaces(size / 1024)} Кб "
                }
            }
            binding.content.text = getString(R.string.download_opisanie, sizeImage)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.dazvolic)) { _: DialogInterface?, _: Int -> mListener?.onDialogPositiveOpisanieWIFI() }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { _: DialogInterface, _: Int -> mListener?.onDialogNegativeOpisanieWIFI() }
        }
        return builder.create()
    }

    private fun formatFigureTwoPlaces(value: Float): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value.toDouble())
    }

    companion object {
        fun getInstance(size: Float): DialogOpisanieWIFI {
            val dialogOpisanieWIFI = DialogOpisanieWIFI()
            val bundle = Bundle()
            bundle.putFloat("size", size)
            dialogOpisanieWIFI.arguments = bundle
            return dialogOpisanieWIFI
        }
    }
}