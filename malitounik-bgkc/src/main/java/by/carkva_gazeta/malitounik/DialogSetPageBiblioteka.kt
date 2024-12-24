package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding

class DialogSetPageBiblioteka : DialogFragment() {
    private var pageCount = 1
    private var mListener: DialogSetPageBibliotekaListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogSetPageBibliotekaListener {
        fun onDialogSetPage(page: Int)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageCount = arguments?.getInt("pageCount") ?: 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogSetPageBibliotekaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSetPageBibliotekaListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(layoutInflater)
            MainActivity.dialogVisable = true
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            binding.title.text = String.format("Увядзіце нумар старонкі. Усяго: %s", pageCount)
            binding.content.inputType = InputType.TYPE_CLASS_NUMBER
            binding.content.requestFocus()
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int -> // Скрываем клавиатуру
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(binding.content.windowToken, 0)
                if (binding.content.text.toString() == "") {
                    MainActivity.toastView(it, getString(R.string.error))
                } else {
                    val value = try {
                        binding.content.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        1
                    }
                    if (value in 1..pageCount) {
                        mListener?.onDialogSetPage(value - 1)
                    } else {
                        MainActivity.toastView(it, getString(R.string.error))
                    }
                }
            }
            builder.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface?, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
            builder.setNeutralButton("На пачатак") { _: DialogInterface?, _: Int ->
                mListener?.onDialogSetPage(1)
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
        }
        return builder.create()
    }

    companion object {
        fun getInstance(pageCount: Int): DialogSetPageBiblioteka {
            val instance = DialogSetPageBiblioteka()
            val args = Bundle()
            args.putInt("pageCount", pageCount)
            instance.arguments = args
            return instance
        }
    }
}