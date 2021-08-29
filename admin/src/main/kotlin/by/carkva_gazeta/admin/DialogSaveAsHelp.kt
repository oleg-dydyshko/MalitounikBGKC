package by.carkva_gazeta.admin

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogSaveAsHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var filename = ""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.save_as_up)
            binding.content.text = resources.getString(by.carkva_gazeta.malitounik.R.string.save_as_help)
            builder.setView(binding.root)
            filename = arguments?.getString("filename", "") ?: ""
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.save_as_up)) { _: DialogInterface, _: Int ->
                val dialogSaveAsFileExplorer = DialogSaveAsFileExplorer.getInstance(filename)
                dialogSaveAsFileExplorer.show(parentFragmentManager, "dialogSaveAsFileExplorer")
            }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun newInstance(filename: String): DialogSaveAsHelp {
            val dialogSaveAsHelp = DialogSaveAsHelp()
            val bundle = Bundle()
            bundle.putString("filename", filename)
            dialogSaveAsHelp.arguments = bundle
            return dialogSaveAsHelp
        }
    }
}