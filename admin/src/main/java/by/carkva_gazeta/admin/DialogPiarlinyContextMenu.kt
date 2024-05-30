package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogContextDisplayBinding

class DialogPiarlinyContextMenu : DialogFragment() {
    private var position = 0
    private var name: String = ""
    private lateinit var mListener: DialogPiarlinyContextMenuListener
    private lateinit var dialog: AlertDialog
    private var _binding: DialogContextDisplayBinding? = null
    private val binding get() = _binding!!

    interface DialogPiarlinyContextMenuListener {
        fun onDialogEditClick(position: Int)
        fun onDialogDeliteClick(position: Int, name: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
        name = arguments?.getString("name") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPiarlinyContextMenuListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPiarlinyContextMenuListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogContextDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it)
            binding.title.text = name
            binding.content.text = getString(by.carkva_gazeta.malitounik.R.string.redagaktirovat)
            binding.content2.text = getString(by.carkva_gazeta.malitounik.R.string.delite)
            builder.setView(binding.root)
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.content2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            dialog = builder.create()
            binding.content.setOnClickListener {
                dialog.cancel()
                mListener.onDialogEditClick(position)
            }
            binding.content2.setOnClickListener {
                dialog.cancel()
                mListener.onDialogDeliteClick(position, name)
            }
        }
        return dialog
    }

    companion object {
        fun getInstance(position: Int, name: String): DialogPiarlinyContextMenu {
            val dialogContextMenu = DialogPiarlinyContextMenu()
            val args = Bundle()
            args.putInt("position", position)
            args.putString("name", name)
            dialogContextMenu.arguments = args
            return dialogContextMenu
        }
    }
}