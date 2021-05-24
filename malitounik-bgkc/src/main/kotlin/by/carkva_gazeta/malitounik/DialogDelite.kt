package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogDelite : DialogFragment() {
    private var mListener: DialogDeliteListener? = null
    private var position = 0
    private var filename = ""
    private var title = ""
    private var massege = ""
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    interface DialogDeliteListener {
        fun fileDelite(position: Int, file: String)
        fun fileDeliteCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mListener?.fileDeliteCancel()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
        filename = arguments?.getString("file") ?: ""
        title = arguments?.getString("title") ?: ""
        massege = arguments?.getString("massege") ?: ""
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
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.remove)
            binding.content.text = getString(R.string.delite_full, title, massege)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.fileDelite(position, filename) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { _: DialogInterface, _: Int -> mListener?.fileDeliteCancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(position: Int, filename: String, title: String, massege: String): DialogDelite {
            val dialogDelite = DialogDelite()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("file", filename)
            bundle.putString("title", title)
            bundle.putString("massege", massege)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}