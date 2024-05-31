package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogWidgetConfigBinding

class DialogWidgetConfig : DialogFragment() {
    private var widgetID = 0
    private var mListener: DialogWidgetConfigListener? = null
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
        mListener?.onDialogWidgetConfigPositiveClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.setPositiveButton(resources.getText(R.string.close)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            _binding = DialogWidgetConfigBinding.inflate(layoutInflater)
            binding.checkBox20.typeface = MainActivity.createFont(Typeface.NORMAL)
            binding.checkBox20.isChecked = chin.getBoolean("dzen_noch_widget_day$widgetID", false)
            binding.checkBox20.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                val prefEditor = chin.edit()
                prefEditor.putBoolean("dzen_noch_widget_day$widgetID", isChecked)
                prefEditor.apply()
                val appWidgetManager = AppWidgetManager.getInstance(it)
                Widget.kaliandar(it, appWidgetManager, widgetID)
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