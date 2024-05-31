package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogContextDisplayBinding

class DialogContextMenuImage : DialogFragment() {
    private lateinit var mListener: DialogContextMenuImageListener
    private lateinit var dialog: AlertDialog
    private var _binding: DialogContextDisplayBinding? = null
    private val binding get() = _binding!!
    private var fileName = ""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogContextMenuImageListener {
        fun onDialogOpisanneIcon(fileName: String)
        fun onDialogUploadIcon()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogContextMenuImageListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogContextMenuImageListener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = arguments?.getString("fileName", "") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogContextDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.admin_img_sviat)
            binding.content.text = getString(by.carkva_gazeta.malitounik.R.string.image_upload)
            binding.content2.text = getString(by.carkva_gazeta.malitounik.R.string.admin_opisanne_icon)
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.content2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            builder.setView(binding.root)
            dialog = builder.create()
            binding.content.setOnClickListener {
                dialog.cancel()
                mListener.onDialogUploadIcon()
            }
            binding.content2.setOnClickListener {
                dialog.cancel()
                mListener.onDialogOpisanneIcon(fileName)
            }
        }
        return dialog
    }

    companion object {
        fun getInstance(fileName: String): DialogContextMenuImage {
            val bundle = Bundle()
            bundle.putString("fileName", fileName)
            val dialog = DialogContextMenuImage()
            dialog.arguments = bundle
            return dialog
        }
    }
}