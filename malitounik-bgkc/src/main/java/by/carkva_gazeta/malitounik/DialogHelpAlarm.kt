package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogHelpAlarm : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogHelpAlarmListener? = null

    interface DialogHelpAlarmListener {
        fun onSettingsAlarm(notification: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogHelpAlarmListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogHelpAlarmListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.help_alarm_title)
            binding.content.setText(R.string.help_alarm_content)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            val notification = arguments?.getInt("notification", SettingsActivity.NOTIFICATION_SVIATY_FULL) ?: SettingsActivity.NOTIFICATION_SVIATY_FULL
            builder.setPositiveButton(resources.getText(R.string.tools_item)) { _: DialogInterface, _: Int ->
                mListener?.onSettingsAlarm(notification)
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(notification: Int): DialogHelpAlarm {
            val dialogDelite = DialogHelpAlarm()
            val bundle = Bundle()
            bundle.putInt("notification", notification)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}