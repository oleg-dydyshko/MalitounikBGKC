package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogSviatyiaImageHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var mListener: DialodSviatyiaImageHelpListener? = null
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialodSviatyiaImageHelpListener {
        fun insertIMG()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialodSviatyiaImageHelpListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialodSviatyiaImageHelpListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.image_help_title)
            binding.content.text = resources.getString(by.carkva_gazeta.malitounik.R.string.image_help_text)
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.image_help_ok)) { _: DialogInterface, _: Int -> mListener?.insertIMG() }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }
}