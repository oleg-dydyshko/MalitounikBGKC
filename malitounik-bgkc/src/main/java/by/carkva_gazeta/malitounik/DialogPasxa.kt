package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding

class DialogPasxa : DialogFragment() {
    private var value = -1
    private var mListener: DialogPasxaListener? = null
    private var dzenNoch = false
    private lateinit var alert: AlertDialog
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

    internal interface DialogPasxaListener {
        fun setPasxa(year: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPasxaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPasxaListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("value", binding.content.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.data_search2)
            binding.content.filters = Array<InputFilter>(1) { InputFilter.LengthFilter(4)}
            if (savedInstanceState != null) {
                val sValue = savedInstanceState.getString("value", "")
                binding.content.setText(sValue)
            }
            binding.content.inputType = InputType.TYPE_CLASS_NUMBER
            if (dzenNoch) {
                binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.content.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                binding.content.setBackgroundResource(R.color.colorWhite)
            }
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(binding.content.windowToken, 0)
                    if (binding.content.text.toString() == "") {
                        error()
                    } else {
                        value = binding.content.text.toString().toInt()
                        if (value in 1583..2093) {
                            mListener?.setPasxa(value)
                        } else {
                            error()
                        }
                    }
                    dialog?.cancel()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.content, 0)
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(binding.content.windowToken, 0)
                if (binding.content.text.toString() == "") {
                    error()
                } else {
                    value = binding.content.text.toString().toInt()
                    if (value in 1583..2093) {
                        mListener?.setPasxa(value)
                    } else {
                        error()
                    }
                }
            }
            builder.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface?, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
            alert = builder.create()
        }
        return alert
    }

    private fun error() {
        activity?.let {
            MainActivity.toastView(it, getString(R.string.error))
        }
    }
}