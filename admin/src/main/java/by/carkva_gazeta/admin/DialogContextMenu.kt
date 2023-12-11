package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogContextDisplayBinding

class DialogContextMenu : DialogFragment() {
    private var position = 0
    private var name = ""
    private var isSite = false
    private lateinit var mListener: DialogContextMenuListener
    private lateinit var dialog: AlertDialog
    private var _binding: DialogContextDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogContextMenuListener {
        fun onDialogRenameClick(title: String, isSite: Boolean)
        fun onDialogDeliteClick(position: Int, title: String, isSite: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
        name = arguments?.getString("name") ?: ""
        isSite = arguments?.getBoolean("isSite") ?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogContextMenuListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogContextMenuListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogContextDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            binding.title.text = name
            binding.content.text = getString(by.carkva_gazeta.malitounik.R.string.rename_file)
            binding.content2.text = getString(by.carkva_gazeta.malitounik.R.string.delite)
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.content2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            builder.setView(binding.root)
            dialog = builder.create()
            binding.content.setOnClickListener {
                dialog.cancel()
                mListener.onDialogRenameClick(name, isSite)
            }
            binding.content2.setOnClickListener {
                dialog.cancel()
                mListener.onDialogDeliteClick(position, name, isSite)
            }
        }
        return dialog
    }

    companion object {
        fun getInstance(position: Int, name: String, isSite: Boolean): DialogContextMenu {
            val dialogContextMenu = DialogContextMenu()
            val args = Bundle()
            args.putInt("position", position)
            args.putString("name", name)
            args.putBoolean("isSite", isSite)
            dialogContextMenu.arguments = args
            return dialogContextMenu
        }
    }
}