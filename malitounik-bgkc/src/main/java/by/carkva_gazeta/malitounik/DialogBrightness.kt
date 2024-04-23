package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogFontBinding

class DialogBrightness : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogFontBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0F)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            if (MainActivity.checkBrightness) {
                try {
                    MainActivity.brightness = Settings.System.getInt(it.contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
                } catch (e: SettingNotFoundException) {
                    MainActivity.brightness = 15
                }
            }
            _binding = DialogFontBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            builder.setView(binding.root)
            if (dzenNoch) {
                binding.textSize.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            }
            binding.title.text = resources.getString(R.string.Bright3)
            binding.seekBar.max = 100
            binding.seekBar.progress = MainActivity.brightness
            binding.textSize.text = resources.getString(R.string.procent, MainActivity.brightness)
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val lp = it.window.attributes
                    lp.screenBrightness = progress.toFloat() / 100
                    it.window.attributes = lp
                    binding.textSize.text = resources.getString(R.string.procent, progress)
                    MainActivity.checkBrightness = false
                    MainActivity.brightness = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            builder.setNeutralButton(resources.getString(R.string.skid_brightness)) { _: DialogInterface, _: Int ->
                MainActivity.checkBrightness = true
                val lp = it.window.attributes
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.window.attributes = lp
            }
            alert = builder.create()
        }
        return alert
    }
}