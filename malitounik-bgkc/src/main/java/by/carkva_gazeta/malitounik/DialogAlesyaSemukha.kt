package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class DialogAlesyaSemukha : DialogFragment() {

    private lateinit var ad: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

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
            ad = AlertDialog.Builder(it, style)
            binding.title.setText(R.string.alesyaSemukha)
            val isSemuxa = arguments?.getBoolean("isSemuxa", true) ?: true
            val inputStream = resources.openRawResource(R.raw.all_rights_reserved)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var text = reader.readText()
            if (!isSemuxa) {
                val t1 = text.indexOf("Усе правы належаць")
                text = text.substring(0, t1)
            }
            binding.content.text = text
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        return ad.create()
    }

    companion object {
        fun getInstance(isSemuxa: Boolean): DialogAlesyaSemukha {
            val instance = DialogAlesyaSemukha()
            val args = Bundle()
            args.putBoolean("isSemuxa", isSemuxa)
            instance.arguments = args
            return instance
        }
    }
}