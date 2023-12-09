package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogDeliteAllVybranoe : DialogFragment() {
    private lateinit var mListener: DialogDeliteAllVybranoeListener
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogDeliteAllVybranoeListener {
        fun deliteAllVybranoe()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteAllVybranoeListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteAllVybranoeListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.remove)
            binding.content.text = getString(R.string.del_all_vybranoe)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite)) 
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.deliteAllVybranoe() }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }
}