package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogDzenNochSettingsBinding

class DialogDzenNochSettings : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogDzenNochSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        (activity as? BaseActivity)?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = it.getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            _binding = DialogDzenNochSettingsBinding.inflate(layoutInflater)
            val nightMode = k.getInt("mode_night", SettingsActivity.MODE_NIGHT_SYSTEM)
            binding.system.isChecked = nightMode == SettingsActivity.MODE_NIGHT_SYSTEM
            binding.day.isChecked = nightMode == SettingsActivity.MODE_NIGHT_NO
            binding.night.isChecked = nightMode == SettingsActivity.MODE_NIGHT_YES
            binding.autoNight.isChecked = nightMode == SettingsActivity.MODE_NIGHT_AUTO
            val ad = AlertDialog.Builder(it, style)
            ad.setView(binding.root)
            ad.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            ad.setPositiveButton(resources.getString(R.string.save_sabytie)) { dialog: DialogInterface, _: Int ->
                var result = SettingsActivity.MODE_NIGHT_SYSTEM
                if (binding.day.isChecked) result = SettingsActivity.MODE_NIGHT_NO
                if (binding.night.isChecked) result = SettingsActivity.MODE_NIGHT_YES
                if (binding.autoNight.isChecked) result = SettingsActivity.MODE_NIGHT_AUTO
                dialog.cancel()
                it.saveBaseDzenNoch(result)
            }
            alert = ad.create()
        }
        return alert
    }
}