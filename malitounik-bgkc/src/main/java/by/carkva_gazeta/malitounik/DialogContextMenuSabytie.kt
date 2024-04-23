package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogContextDisplayBinding

class DialogContextMenuSabytie : DialogFragment() {
    private var position = 0
    private var name: String = ""
    private lateinit var mListener: DialogContextMenuSabytieListener
    private lateinit var dialog: AlertDialog
    private var _binding: DialogContextDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogContextMenuSabytieListener {
        fun onDialogEditClick(position: Int)
        fun onDialogDeliteClick(position: Int)
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
                context as DialogContextMenuSabytieListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogContextMenuSabytieListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogContextDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            val builder = AlertDialog.Builder(it)
            binding.title.text = name
            binding.content.text = getString(R.string.redagaktirovat)
            if (dzenNoch) {
                binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.content.setBackgroundResource(R.drawable.selector_dark)
            } else {
                binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                binding.content.setBackgroundResource(R.drawable.selector_default)
            }
            binding.content2.text = getString(R.string.delite)
            if (dzenNoch) {
                binding.content2.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.content2.setBackgroundResource(R.drawable.selector_dark)
            } else {
                binding.content2.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                binding.content2.setBackgroundResource(R.drawable.selector_default)
            }
            builder.setView(binding.root)
            dialog = builder.create()
            binding.content.setOnClickListener {
                dialog.cancel()
                mListener.onDialogEditClick(position)
            }
            binding.content2.setOnClickListener {
                dialog.cancel()
                mListener.onDialogDeliteClick(position)
            }
        }
        return dialog
    }

    companion object {
        fun getInstance(position: Int, name: String): DialogContextMenuSabytie {
            val dialogContextMenuSabytie = DialogContextMenuSabytie()
            val args = Bundle()
            args.putInt("position", position)
            args.putString("name", name)
            dialogContextMenuSabytie.arguments = args
            return dialogContextMenuSabytie
        }
    }
}