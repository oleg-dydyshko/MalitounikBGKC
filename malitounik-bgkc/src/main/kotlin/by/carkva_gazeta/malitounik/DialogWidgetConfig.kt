package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogWidgetConfigBinding

class DialogWidgetConfig : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogWidgetConfigBinding? = null
    private val binding get() = _binding!!

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
            val widgetID = arguments?.getInt("widgetID") ?: AppWidgetManager.INVALID_APPWIDGET_ID
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            _binding = DialogWidgetConfigBinding.inflate(LayoutInflater.from(it))
            binding.checkBox20.typeface = MainActivity.createFont(Typeface.NORMAL)
            binding.checkBox20.isChecked = chin.getBoolean("dzen_noch_widget_day$widgetID", false)
            binding.checkBox20.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                val prefEditor = chin.edit()
                prefEditor.putBoolean("dzen_noch_widget_day$widgetID", isChecked)
                prefEditor.apply()
                val intent = Intent(it, Widget::class.java)
                intent.putExtra("widgetID", widgetID)
                it.sendBroadcast(intent)
            }
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.setPositiveButton(resources.getText(R.string.close)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            builder.setView(binding.root)
            alert = builder.create()
            val intent = Intent(it, Widget::class.java)
            intent.putExtra("widgetID", widgetID)
            intent.putExtra("actionEndLoad", true)
            it.sendBroadcast(intent)
        }
        return alert
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