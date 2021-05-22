package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogPasxa : DialogFragment() {
    private var value = -1
    private lateinit var input: EditText
    private lateinit var mListener: DialogPasxaListener
    private var realpadding = 0
    private var dzenNoch = false
    private lateinit var alert: AlertDialog

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
        outState.putString("value", input.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.data_search2)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linear.addView(textViewZaglavie)
            input = EditText(it)
            input.filters = Array<InputFilter>(1) { InputFilter.LengthFilter(4)}
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            linear.addView(input)
            if (savedInstanceState != null) {
                val sValue = savedInstanceState.getString("value", "")
                input.setText(sValue)
            }
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (dzenNoch) {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                input.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                input.setBackgroundResource(R.color.colorWhite)
            }
            input.requestFocus()
            input.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(input.windowToken, 0)
                    if (input.text.toString() == "") {
                        error()
                    } else {
                        value = input.text.toString().toInt()
                        if (value in 1583..2093) {
                            mListener.setPasxa(value)
                        } else {
                            error()
                        }
                    }
                    dialog?.cancel()
                }
                false
            }
            input.imeOptions = EditorInfo.IME_ACTION_GO
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            builder.setView(linear)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(input.windowToken, 0)
                if (input.text.toString() == "") {
                    error()
                } else {
                    value = input.text.toString().toInt()
                    if (value in 1583..2093) {
                        mListener.setPasxa(value)
                    } else {
                        error()
                    }
                }
            }
            builder.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface?, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(input.windowToken, 0)
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