package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewCheckboxDisplayBinding

class DialogSemuxaNoKnigi : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewCheckboxDisplayBinding? = null
    private val binding get() = _binding!!
    private var isSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSettings = arguments?.getBoolean("isSettings", false) ?: false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewCheckboxDisplayBinding.inflate(layoutInflater)
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.title_biblia)
            binding.content.text = getString(R.string.onli_kanon_knigi)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            if (isSettings)
                binding.checkbox.visibility = View.GONE
            binding.checkbox.typeface = MainActivity.createFont(Typeface.NORMAL)
            binding.checkbox.text = getString(R.string.sabytie_check_mun)
            val sp = it.setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
            binding.checkbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
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
            ad.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    companion object {
        fun getInstance(isSettings: Boolean): DialogSemuxaNoKnigi {
            val bundle = Bundle()
            bundle.putBoolean("isSettings", isSettings)
            val dialogSemuxaNoKnigi = DialogSemuxaNoKnigi()
            dialogSemuxaNoKnigi.arguments = bundle
            return dialogSemuxaNoKnigi
        }
    }
}