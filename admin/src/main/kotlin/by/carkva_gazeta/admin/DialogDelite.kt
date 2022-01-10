package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogDelite : DialogFragment() {
    private var mListener: DialogDeliteListener? = null
    private var position = 0
    private var title = ""
    private var isSite = false
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogDeliteListener {
        fun fileDelite(position: Int, title: String, isSite: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
        title = arguments?.getString("title") ?: ""
        isSite = arguments?.getBoolean("isSite") ?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.remove)
            binding.content.text = getString(by.carkva_gazeta.malitounik.R.string.vybranoe_biblia_delite, title)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.fileDelite(position, title, isSite) }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(position: Int, title: String, isSite: Boolean): DialogDelite {
            val dialogDelite = DialogDelite()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("title", title)
            bundle.putBoolean("isSite", isSite)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}