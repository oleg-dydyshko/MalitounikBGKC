package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewCheckboxDisplayBinding

class DialogSemuxaNoKnigi : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewCheckboxDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewCheckboxDisplayBinding.inflate(LayoutInflater.from(it))
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = getString(R.string.title_biblia)
            binding.content.text = getString(R.string.onli_kanon_knigi)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            binding.checkbox.typeface = MainActivity.createFont(it, Typeface.NORMAL)
            binding.checkbox.text = getString(R.string.sabytie_check_mun)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val edit = chin.edit()
                if (isChecked) {
                    edit.putBoolean("SemuxaNoKnigi", false)
                } else {
                    edit.putBoolean("SemuxaNoKnigi", true)
                }
                edit.apply()
            }
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }
}