package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class DialogNadsanPravila : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) 
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.peryiady).uppercase()
            val inputStream = resources.openRawResource(R.raw.nadsan_periody)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            binding.content.text = MainActivity.fromHtml(reader.readText())
            inputStream.close()
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }
}