package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogHelpFullScreen : DialogFragment() {

    private lateinit var ad: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var dialogFullScreenHelpListener: DialogFullScreenHelpListener? = null

    interface DialogFullScreenHelpListener {
        fun onDialogFullScreenHelpClose()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogFullScreenHelpListener = try {
                context as DialogFullScreenHelpListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogFullScreenHelpListener")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
        dialogFullScreenHelpListener?.onDialogFullScreenHelpClose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0.3F)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.help_davedka)
            val help = SpannableString(getString(R.string.help_full_screen))
            val t1 = help.indexOf("\n")
            help.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPost2)), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.content.text = help
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        return ad.create()
    }
}