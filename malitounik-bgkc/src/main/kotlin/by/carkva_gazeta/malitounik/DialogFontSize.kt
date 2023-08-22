package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogFontBinding

class DialogFontSize : DialogFragment() {
    private lateinit var mListener: DialogFontSizeListener
    private lateinit var alert: AlertDialog
    private var _binding: DialogFontBinding? = null
    private val binding get() = _binding!!

    interface DialogFontSizeListener {
        fun onDialogFontSize(fontSize: Float)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    private fun setProgressFontSize(fontBiblia: Int): Int {
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
        var font = SettingsActivity.GET_FONT_SIZE_DEFAULT
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seekbar", binding.seekBar.progress)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0F)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            _binding = DialogFontBinding.inflate(LayoutInflater.from(it))
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            builder.setView(binding.root)
            val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            if (dzenNoch) {
                binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textSize.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            }
            binding.textSize.text = getString(R.string.get_font, fontBiblia.toInt())
            if (savedInstanceState != null) {
                val seekbar = savedInstanceState.getInt("seekbar")
                binding.seekBar.progress = seekbar
            } else {
                binding.seekBar.progress = setProgressFontSize(fontBiblia.toInt())
            }
            binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val prefEditors = k.edit()
                    val fontSize = getFont(progress)
                    prefEditors.putFloat("font_biblia", fontSize)
                    prefEditors.apply()
                    binding.textSize.text = getString(R.string.get_font, fontSize.toInt())
                    mListener.onDialogFontSize(fontSize)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    binding.title.visibility = View.GONE
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.GONE
                    alert.getButton(DialogInterface.BUTTON_NEGATIVE).visibility = View.GONE
                    alert.getButton(DialogInterface.BUTTON_NEUTRAL).visibility = View.GONE
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    binding.title.visibility = View.VISIBLE
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.VISIBLE
                    alert.getButton(DialogInterface.BUTTON_NEGATIVE).visibility = View.VISIBLE
                    alert.getButton(DialogInterface.BUTTON_NEUTRAL).visibility = View.VISIBLE
                }
            })
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface, _: Int ->
                val progress = binding.seekBar.progress
                val fontSize = getFont(progress)
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", fontSize)
                prefEditors.apply()
                mListener.onDialogFontSize(fontSize)
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { _: DialogInterface, _: Int ->
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", fontBiblia)
                prefEditors.apply()
                mListener.onDialogFontSize(fontBiblia)
            }
            builder.setNeutralButton(resources.getString(R.string.font_pa_zmauchanni)) { _: DialogInterface, _: Int ->
                val prefEditors = k.edit()
                prefEditors.putFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
                prefEditors.apply()
                mListener.onDialogFontSize(SettingsActivity.GET_FONT_SIZE_DEFAULT)
            }
            alert = builder.create()
        }
        return alert
    }
}