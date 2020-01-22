package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment

/**
 * Created by oleg on 20.7.17
 */
class DialogWidgetConfig : DialogFragment() {
    private var widgetID = 0
    private lateinit var mListener: DialogWidgetConfigListener
    private lateinit var alert: AlertDialog

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val prefEditor = k.edit()
            val builder = AlertDialog.Builder(it)
            val linearLayout = View.inflate(it, R.layout.dialog_widget_config, null)
            val checkBox20: SwitchCompat = linearLayout.findViewById(R.id.checkBox20)
            checkBox20.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            checkBox20.isChecked = k.getBoolean("dzen_noch_widget_day$widgetID", false)
            checkBox20.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    prefEditor.putBoolean("dzen_noch_widget_day$widgetID", true)
                } else {
                    prefEditor.putBoolean("dzen_noch_widget_day$widgetID", false)
                }
            }
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int ->
                prefEditor.apply()
                mListener.onDialogWidgetConfigPositiveClick()
                dialog.cancel()
            }
            builder.setView(linearLayout)
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
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