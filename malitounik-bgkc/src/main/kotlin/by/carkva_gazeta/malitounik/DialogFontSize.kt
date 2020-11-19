package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_font.*

class DialogFontSize : DialogFragment() {
    private lateinit var mListener: DialogFontSizeListener
    private lateinit var alert: AlertDialog
    private lateinit var rootView: View

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
        outState.putInt("seekbar", seekBar.progress)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
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
            textSize.text = getString(R.string.get_font, fontBiblia.toInt())
            zmauchanni.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            cansel.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            zmauchanni.setOnClickListener {
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
                prefEditors.apply()
                mListener.onDialogFontSizePositiveClick()
                dialog?.cancel()
            }
            cansel.setOnClickListener {
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", fontBiblia)
                prefEditors.apply()
                mListener.onDialogFontSizePositiveClick()
                dialog?.cancel()
            }
            ok.setOnClickListener {
                val progress = seekBar.progress
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", getFont(progress))
                prefEditors.apply()
                mListener.onDialogFontSizePositiveClick()
                dialog?.cancel()
            }
            if (savedInstanceState != null) {
                val seekbar = savedInstanceState.getInt("seekbar")
                seekBar.progress = seekbar
            } else {
                seekBar.progress = setProgress(fontBiblia.toInt())
            }
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val prefEditors = k.edit()
                    prefEditors.putFloat("font_biblia", getFont(progress))
                    prefEditors.apply()
                    textSize.text = getString(R.string.get_font, getFont(progress).toInt())
                    mListener.onDialogFontSizePositiveClick()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    title.visibility = View.GONE
                    zmauchanni.visibility = View.GONE
                    cansel.visibility = View.GONE
                    ok.visibility = View.GONE
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    title.visibility = View.VISIBLE
                    zmauchanni.visibility = View.VISIBLE
                    cansel.visibility = View.VISIBLE
                    ok.visibility = View.VISIBLE
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0f)
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            rootView = View.inflate(it, R.layout.dialog_font, null)
            val builder = AlertDialog.Builder(it)
            builder.setView(rootView)
            alert = builder.create()
        }
        return alert
    }
}