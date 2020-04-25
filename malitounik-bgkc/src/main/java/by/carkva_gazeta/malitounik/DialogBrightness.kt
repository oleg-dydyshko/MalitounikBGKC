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
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_font.*

/**
 * Created by oleg on 20.7.17
 */
class DialogBrightness : DialogFragment() {
    private lateinit var alert: AlertDialog
    private lateinit var rootView: View
    
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0f)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            zmauchanni.text = resources.getText(R.string.skid_brightness)
            zmauchanni.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            cansel.visibility = View.GONE
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                textSize.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                zmauchanni.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                cansel.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                ok.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                zmauchanni.setBackgroundResource(R.drawable.selector_dialog_font_dark)
                cansel.setBackgroundResource(R.drawable.selector_dialog_font_dark)
                ok.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            }
            title.text = resources.getString(R.string.Bright3)
            seekBar.max = 100
            seekBar.progress = MainActivity.brightness
            textSize.text = resources.getString(R.string.procent, MainActivity.brightness)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val lp = it.window.attributes
                    lp.screenBrightness = progress.toFloat() / 100
                    it.window.attributes = lp
                    textSize.text = resources.getString(R.string.procent, progress)
                    MainActivity.checkBrightness = false
                    MainActivity.brightness = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            zmauchanni.setOnClickListener { _ ->
                MainActivity.checkBrightness = true
                val lp = it.window.attributes
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.window.attributes = lp
                dialog?.cancel()
            }
            ok.setOnClickListener {
                dialog?.cancel()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            rootView = View.inflate(it, R.layout.dialog_font, null)
            if (MainActivity.checkBrightness) {
                try {
                    MainActivity.brightness = Settings.System.getInt(it.contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
                } catch (e: SettingNotFoundException) {
                    MainActivity.brightness = 15
                }
            }
            val builder = AlertDialog.Builder(it)
            builder.setView(rootView)
            alert = builder.create()
        }
        return alert
    }
}