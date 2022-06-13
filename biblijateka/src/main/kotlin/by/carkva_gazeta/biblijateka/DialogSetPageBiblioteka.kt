package by.carkva_gazeta.biblijateka

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding

class DialogSetPageBiblioteka : DialogFragment() {
    private var page = 0
    private var pageCount = 0
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments?.getInt("page") ?: 0
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
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = String.format("Увядзіце нумар старонкі. Усяго: %s", pageCount)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.content.setText(page.toString())
            binding.content.inputType = InputType.TYPE_CLASS_NUMBER
            binding.content.requestFocus()
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int -> // Скрываем клавиатуру
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(binding.content.windowToken, 0)
                if (binding.content.text.toString() == "") {
                    MainActivity.toastView(getString(R.string.error))
                } else {
                    val value: Int = try {
                        binding.content.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        1
                    }
                    if (value in 1..pageCount) {
                        mListener?.onDialogSetPage(value)
                    } else {
                        MainActivity.toastView(getString(R.string.error))
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
        fun getInstance(page: Int, pageCount: Int): DialogSetPageBiblioteka {
            val instance = DialogSetPageBiblioteka()
            val args = Bundle()
            args.putInt("page", page + 1)
            args.putInt("pageCount", pageCount)
            instance.arguments = args
            return instance
        }
    }
}