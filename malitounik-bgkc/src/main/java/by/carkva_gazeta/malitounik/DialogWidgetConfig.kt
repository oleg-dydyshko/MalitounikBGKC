package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogDzenNochSettingsBinding

class DialogWidgetConfig : DialogFragment() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var isWidgetMun = false
    private var mListener: DialogWidgetConfigListener? = null
    private lateinit var alert: AlertDialog
    private var _binding: DialogDzenNochSettingsBinding? = null
    private val binding get() = _binding!!

    interface DialogWidgetConfigListener {
        fun onDialogWidgetConfigPositiveClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = arguments?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        isWidgetMun = arguments?.getBoolean("isWidgetMun", false) ?: false
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
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            _binding = DialogDzenNochSettingsBinding.inflate(layoutInflater)
            val nightMode = if (isWidgetMun) k.getInt("mode_night_widget_mun$widgetID", SettingsActivity.MODE_NIGHT_SYSTEM)
            else k.getInt("mode_night_widget_day$widgetID", SettingsActivity.MODE_NIGHT_SYSTEM)
            binding.system.isChecked = nightMode == SettingsActivity.MODE_NIGHT_SYSTEM
            binding.day.isChecked = nightMode == SettingsActivity.MODE_NIGHT_NO
            binding.night.isChecked = nightMode == SettingsActivity.MODE_NIGHT_YES
            binding.autoNight.visibility = View.GONE
            val ad = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            ad.setView(binding.root)
            ad.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            ad.setPositiveButton(resources.getString(R.string.save_sabytie)) { dialog: DialogInterface, _: Int ->
                var result = SettingsActivity.MODE_NIGHT_SYSTEM
                if (binding.day.isChecked) result = SettingsActivity.MODE_NIGHT_NO
                if (binding.night.isChecked) result = SettingsActivity.MODE_NIGHT_YES
                if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val prefEditor = k.edit()
                    if (isWidgetMun) prefEditor.putInt("mode_night_widget_mun$widgetID", result)
                    else prefEditor.putInt("mode_night_widget_day$widgetID", result)
                    prefEditor.apply()
                }
                mListener?.onDialogWidgetConfigPositiveClick()
                dialog.cancel()
            }
            if (!isWidgetMun) {
                val intent = Intent(it, Widget::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
                intent.putExtra("actionEndLoad", true)
                it.sendBroadcast(intent)
            }
            alert = ad.create()
        }
        return alert
    }

    companion object {
        fun getInstance(widgetID: Int, isWidgetMun: Boolean): DialogWidgetConfig {
            val dialogWidgetConfig = DialogWidgetConfig()
            val bundle = Bundle()
            bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            bundle.putBoolean("isWidgetMun", isWidgetMun)
            dialogWidgetConfig.arguments = bundle
            return dialogWidgetConfig
        }
    }
}