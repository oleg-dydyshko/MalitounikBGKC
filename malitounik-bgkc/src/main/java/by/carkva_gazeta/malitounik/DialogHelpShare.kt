package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewCheckboxDisplayBinding

class DialogHelpShare : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewCheckboxDisplayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogHelpShareListener? = null
    private var shareText = ""

    interface DialogHelpShareListener {
        fun sentShareText(shareText: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
        mListener?.sentShareText(shareText)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogHelpShareListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogHelpShareListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewCheckboxDisplayBinding.inflate(layoutInflater)
            MainActivity.dialogVisable = true
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.share)
            binding.content.text = getString(R.string.dialog_help_share)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            binding.checkbox.typeface = MainActivity.createFont(Typeface.NORMAL)
            binding.checkbox.text = getString(R.string.sabytie_check_mun)
            val sp = it.setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
            binding.checkbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
            shareText = arguments?.getString("shareText") ?: ""
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val edit = chin.edit()
                if (isChecked) {
                    edit.putBoolean("dialogHelpShare", false)
                } else {
                    edit.putBoolean("dialogHelpShare", true)
                }
                edit.apply()
            }
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    companion object {
        fun getInstance(shareText: String): DialogHelpShare {
            val bundle = Bundle()
            bundle.putString("shareText", shareText)
            val dialogHelpShare = DialogHelpShare()
            dialogHelpShare.arguments = bundle
            return dialogHelpShare
        }
    }
}