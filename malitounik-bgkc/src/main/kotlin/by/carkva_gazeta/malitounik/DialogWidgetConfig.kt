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
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_widget_config.*

/**
 * Created by oleg on 20.7.17
 */
class DialogWidgetConfig : DialogFragment() {
    private var configDzenNoch = false
    private var widgetID = 0
    private lateinit var mListener: DialogWidgetConfigListener
    private lateinit var alert: AlertDialog
    private lateinit var rootView: View

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            checkBox20.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            checkBox20.isChecked = k.getBoolean("dzen_noch_widget_day$widgetID", false)
            checkBox20.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                configDzenNoch = isChecked
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            rootView = View.inflate(it, R.layout.dialog_widget_config, null)
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int ->
                save()
                mListener.onDialogWidgetConfigPositiveClick()
                dialog.cancel()
            }
            builder.setView(rootView)
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