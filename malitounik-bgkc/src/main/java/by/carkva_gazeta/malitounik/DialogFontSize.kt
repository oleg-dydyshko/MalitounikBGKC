package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class DialogFontSize : DialogFragment() {
    private lateinit var input: AppCompatSeekBar
    private lateinit var mListener: DialogFontSizeListener
    private lateinit var alert: AlertDialog

    interface DialogFontSizeListener {
        fun onDialogFontSizePositiveClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogFontSizeListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogFontSizeListener")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seekbar", input.progress)
    }

    private fun setProgress(fontBiblia: Int): Int {
        var progress = 1
        when (fontBiblia) {
            14 -> progress = 0
            18 -> progress = 1
            22 -> progress = 2
            26 -> progress = 3
            30 -> progress = 4
            34 -> progress = 5
            38 -> progress = 6
            42 -> progress = 7
            46 -> progress = 8
            50 -> progress = 9
            54 -> progress = 10
        }
        return progress
    }

    private fun getFont(progress: Int): Float {
        var font = SettingsActivity.GET_DEFAULT_FONT_SIZE
        when (progress) {
            0 -> font = 14F
            1 -> font = 18F
            2 -> font = 22F
            3 -> font = 26F
            4 -> font = 30F
            5 -> font = 34F
            6 -> font = 38F
            7 -> font = 42F
            8 -> font = 46F
            9 -> font = 50F
            10 -> font = 54F
        }
        return font
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0f)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
            if (dzenNoch) it.setTheme(R.style.AppCompatDark) else it.setTheme(R.style.AppTheme)
            val builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.FONT_SIZE_APP)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            input = AppCompatSeekBar(it)
            input.setPadding(realpadding, realpadding, realpadding, realpadding)
            input.max = 10
            if (savedInstanceState != null) {
                val seekbar = savedInstanceState.getInt("seekbar")
                input.progress = seekbar
            } else {
                input.progress = setProgress(fontBiblia.toInt())
            }
            linearLayout.addView(input)
            input.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val prefEditors = k.edit()
                    prefEditors.putFloat("font_biblia", getFont(progress))
                    prefEditors.apply()
                    mListener.onDialogFontSizePositiveClick()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int ->
                val progress = input.progress
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", getFont(progress))
                prefEditors.apply()
                mListener.onDialogFontSizePositiveClick()
                dialog.cancel()
            }
            builder.setNegativeButton(resources.getText(R.string.CANCEL)) { dialog: DialogInterface, _: Int ->
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", fontBiblia)
                prefEditors.apply()
                mListener.onDialogFontSizePositiveClick()
                dialog.cancel()
            }
            builder.setNeutralButton(resources.getText(R.string.default_font)) { dialog: DialogInterface, _: Int ->
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
                prefEditors.apply()
                mListener.onDialogFontSizePositiveClick()
                dialog.cancel()
            }
            builder.setView(linearLayout)
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                val btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL)
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
        }
        return alert
    }
}