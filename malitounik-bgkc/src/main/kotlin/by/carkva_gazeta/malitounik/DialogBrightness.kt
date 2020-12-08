package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogFontBinding

class DialogBrightness : DialogFragment() {
    private lateinit var alert: AlertDialog
    private lateinit var binding: DialogFontBinding
    
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
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
            binding = DialogFontBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            alert = builder.create()
            binding.zmauchanni.text = resources.getText(R.string.skid_brightness)
            binding.zmauchanni.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            binding.ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            binding.cansel.visibility = View.GONE
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textSize.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.zmauchanni.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.cansel.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.ok.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.zmauchanni.setBackgroundResource(R.drawable.selector_dialog_font_dark)
                binding.cansel.setBackgroundResource(R.drawable.selector_dialog_font_dark)
                binding.ok.setBackgroundResource(R.drawable.selector_dialog_font_dark)
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
            binding.zmauchanni.setOnClickListener { _ ->
                MainActivity.checkBrightness = true
                val lp = it.window.attributes
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.window.attributes = lp
                dialog?.cancel()
            }
            binding.ok.setOnClickListener {
                dialog?.cancel()
            }
        }
        return alert
    }
}