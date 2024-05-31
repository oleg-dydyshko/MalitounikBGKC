package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogHelpNotificationApi33 : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogHelpNotificationApi33Listener? = null

    internal interface DialogHelpNotificationApi33Listener {
        fun onDialogHelpNotificationApi33(notification: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogHelpNotificationApi33Listener
            } catch (e: ClassCastException) {
                throw ClassCastException(activity.toString() + " must implement DialogHelpNotificationApi33Listener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.notifi).uppercase()
            binding.content.setText(R.string.help_notifications_api33)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getText(R.string.dazvolic)) { dialog: DialogInterface, _: Int ->
                mListener?.onDialogHelpNotificationApi33(arguments?.getInt("notification", SettingsActivity.NOTIFICATION_SVIATY_FULL) ?: SettingsActivity.NOTIFICATION_SVIATY_FULL)
                dialog.cancel()
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(notification: Int): DialogHelpNotificationApi33 {
            val dialogClearHishory = DialogHelpNotificationApi33()
            val bundle = Bundle()
            bundle.putInt("notification", notification)
            dialogClearHishory.arguments = bundle
            return dialogClearHishory
        }
    }
}