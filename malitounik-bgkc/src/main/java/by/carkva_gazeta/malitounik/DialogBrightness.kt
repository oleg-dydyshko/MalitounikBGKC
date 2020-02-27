package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

/**
 * Created by oleg on 20.7.17
 */
class DialogBrightness : DialogFragment() {
    private lateinit var builder: AlertDialog.Builder
    
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0f)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (MainActivity.checkBrightness) {
                try {
                    MainActivity.brightness = Settings.System.getInt(it.contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
                } catch (e: SettingNotFoundException) {
                    MainActivity.brightness = 15
                }
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) it.setTheme(R.style.AppCompatDark) else it.setTheme(R.style.AppTheme)
            builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.Bright3)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            val input = AppCompatSeekBar(it)
            input.max = 100
            input.progress = MainActivity.brightness
            textView.text = resources.getString(R.string.procent, MainActivity.brightness)
            linearLayout.addView(input)
            input.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val lp = it.window.attributes
                    lp.screenBrightness = progress.toFloat() / 100
                    it.window.attributes = lp
                    textView.text = resources.getString(R.string.procent, progress)
                    MainActivity.checkBrightness = false
                    MainActivity.brightness = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setNeutralButton(resources.getText(R.string.skid_brightness)) { _: DialogInterface?, _: Int ->
                MainActivity.checkBrightness = true
                val lp = it.window.attributes
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.window.attributes = lp
            }
            builder.setView(linearLayout)
        }
        val alert = builder.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            val btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL)
            btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        }
        return alert
    }
}