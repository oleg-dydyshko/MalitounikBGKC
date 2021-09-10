package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding

class DialogBibleRazdel : DialogFragment() {
    private var fullGlav = 0
    private var mListener: DialogBibleRazdelListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogBibleRazdelListener {
        fun onComplete(glava: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogBibleRazdelListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibleRazdelListener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullGlav = arguments?.getInt("full_glav") ?: 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("glava", binding.content.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.text = resources.getString(R.string.data_search, fullGlav)
            binding.content.filters = Array<InputFilter>(1) { InputFilter.LengthFilter(3) }
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (savedInstanceState != null) {
                binding.content.setText(savedInstanceState.getString("glava"))
            } else {
                binding.content.setText("")
            }
            binding.content.inputType = InputType.TYPE_CLASS_NUMBER
            binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            binding.content.setBackgroundResource(R.color.colorWhite)
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    goRazdel()
                    dialog?.cancel()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                goRazdel()
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun goRazdel() {
        activity?.let {
            val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm1.hideSoftInputFromWindow(binding.content.windowToken, 0)
            if (binding.content.text.toString() == "") {
                MainActivity.toastView(getString(R.string.error))
            } else {
                val value: Int = try {
                    binding.content.text.toString().toInt() - 1
                } catch (e: NumberFormatException) {
                    -1
                }
                if (value in 0 until fullGlav) {
                    mListener?.onComplete(value)
                } else {
                    MainActivity.toastView(getString(R.string.error))
                }
            }
        }
    }

    companion object {
        fun getInstance(full_glav: Int): DialogBibleRazdel {
            val instance = DialogBibleRazdel()
            val args = Bundle()
            args.putInt("full_glav", full_glav)
            instance.arguments = args
            return instance
        }
    }
}