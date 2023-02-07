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
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class DialogBibliatekaArtykuly : DialogFragment() {
    private var size = 0L
    private var mListener: DialogBibliatekaArtykulyListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogBibliatekaArtykulyListener {
        fun onDialogbibliatekaArtykulyPositiveClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        size = arguments?.getLong("size") ?: 0L
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogBibliatekaArtykulyListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibliatekaArtykulyListener")
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
            binding.title.text = getString(R.string.wifi_error)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            val izm = if (size / 1024 > 1000) {
                formatFigureTwoPlaces(BigDecimal.valueOf(size.toFloat() / 1024 / 1024.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()) + " Мб"
            } else {
                formatFigureTwoPlaces(BigDecimal.valueOf(size.toFloat() / 1024.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()) + " Кб"
            }
            binding.content.text = getString(R.string.artykuly_opisanie, izm)
            builder.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface, _: Int -> mListener?.onDialogbibliatekaArtykulyPositiveClick() }
            builder.setNegativeButton(R.string.cansel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun formatFigureTwoPlaces(value: Float): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value.toDouble())
    }

    companion object {
        fun getInstance(size: Long): DialogBibliatekaArtykuly {
            val instance = DialogBibliatekaArtykuly()
            val args = Bundle()
            args.putLong("size", size)
            instance.arguments = args
            return instance
        }
    }
}