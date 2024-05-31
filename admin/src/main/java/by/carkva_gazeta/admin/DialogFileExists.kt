package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogFileExists : DialogFragment() {
    private var mListener: DialogFileExistsListener? = null
    private var dir = ""
    private var oldFileName = ""
    private var fileName = ""
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogFileExistsListener {
        fun fileExists(dir: String, oldFileName: String, fileName: String, saveAs: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dir = arguments?.getString("dir") ?: ""
        oldFileName = arguments?.getString("oldFileName") ?: ""
        fileName = arguments?.getString("fileName") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogFileExistsListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogFileExistsListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.file_exists)
            binding.content.text = getString(by.carkva_gazeta.malitounik.R.string.file_exists_opis, fileName)
            val saveAs = arguments?.getBoolean("saveAs") ?: true
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.file_perazapisac)) { _: DialogInterface, _: Int -> mListener?.fileExists(dir, oldFileName, fileName, saveAs) }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(dir: String, oldFileName: String, fileName: String, saveAs: Boolean): DialogFileExists {
            val dialogFileExists = DialogFileExists()
            val bundle = Bundle()
            bundle.putString("dir", dir)
            bundle.putString("oldFileName", oldFileName)
            bundle.putString("fileName", fileName)
            bundle.putBoolean("saveAs", saveAs)
            dialogFileExists.arguments = bundle
            return dialogFileExists
        }
    }
}