package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding
import java.io.File

class DialogSviatyiaImageApisanne : DialogFragment() {
    private var mListener: DialogSviatyiaImageApisanneListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var fileName = ""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogSviatyiaImageApisanneListener {
        fun setApisanneIcon(text: String, fileName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogSviatyiaImageApisanneListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSviatyiaImageApisanneListener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = arguments?.getString("fileName", "") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.admin_opisanne_icon)
            val t1 = fileName.lastIndexOf(".")
            val fileNameT = fileName.substring(0, t1) + ".txt"
            val file = File("${it.filesDir}/iconsApisanne/$fileNameT")
            if (file.exists()) binding.content.setText(file.readText())
            binding.content.requestFocus()
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int -> // Скрываем клавиатуру
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(binding.content.windowToken, 0)
                mListener?.setApisanneIcon(binding.content.text.toString(), fileName)
            }
            builder.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface?, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
        }
        return builder.create()
    }

    companion object {
        fun getInstance(fileName: String): DialogSviatyiaImageApisanne {
            val bundle = Bundle()
            bundle.putString("fileName", fileName)
            val dialog = DialogSviatyiaImageApisanne()
            dialog.arguments = bundle
            return dialog
        }
    }
}