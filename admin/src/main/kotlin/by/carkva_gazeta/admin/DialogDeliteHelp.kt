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

class DialogDeliteHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var filename = ""
    private var mListener: DialogDeliteHelpListener? = null

    internal interface DialogDeliteHelpListener {
        fun onFileDelite(fileName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteHelpListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogDeliteHelpListener")
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
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.help_delite_title)
            binding.content.text = resources.getString(by.carkva_gazeta.malitounik.R.string.help_delite_content)
            builder.setView(binding.root)
            filename = arguments?.getString("filename", "") ?: ""
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.delite)) { _: DialogInterface, _: Int ->
                mListener?.onFileDelite(filename)
            }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun newInstance(filename: String): DialogDeliteHelp {
            val dialogSaveAsHelp = DialogDeliteHelp()
            val bundle = Bundle()
            bundle.putString("filename", filename)
            dialogSaveAsHelp.arguments = bundle
            return dialogSaveAsHelp
        }
    }
}