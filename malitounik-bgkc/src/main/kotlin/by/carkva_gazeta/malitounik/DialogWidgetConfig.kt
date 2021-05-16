package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogWidgetConfigBinding

class DialogWidgetConfig : DialogFragment() {
    private var configDzenNoch = false
    private var widgetID = 0
    private lateinit var mListener: DialogWidgetConfigListener
    private lateinit var alert: AlertDialog
    private var _binding: DialogWidgetConfigBinding? = null
    private val binding get() = _binding!!

    internal interface DialogWidgetConfigListener {
        fun onDialogWidgetConfigPositiveClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = arguments?.getInt("widgetID") ?: 0
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        if (activity is Activity) {
            mListener = try {
                activity as DialogWidgetConfigListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogWidgetConfigListener")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int ->
                save()
                mListener.onDialogWidgetConfigPositiveClick()
                dialog.cancel()
            }
            _binding = DialogWidgetConfigBinding.inflate(LayoutInflater.from(it))
            binding.checkBox20.typeface = TextViewRobotoCondensed.createFont(it, Typeface.NORMAL)
            binding.checkBox20.isChecked = chin.getBoolean("dzen_noch_widget_day$widgetID", false)
            binding.checkBox20.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                configDzenNoch = isChecked
            }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    private fun save() {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val prefEditor = k.edit()
            prefEditor.putBoolean("dzen_noch_widget_day$widgetID", configDzenNoch)
            prefEditor.apply()
        }
    }

    companion object {
        fun getInstance(widgetID: Int): DialogWidgetConfig {
            val dialogWidgetConfig = DialogWidgetConfig()
            val bundle = Bundle()
            bundle.putInt("widgetID", widgetID)
            dialogWidgetConfig.arguments = bundle
            return dialogWidgetConfig
        }
    }
}