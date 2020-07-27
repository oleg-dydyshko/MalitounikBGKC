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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogPasxa : DialogFragment() {
    private var value = -1
    private lateinit var input: EditTextRobotoCondensed
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
            val builder = AlertDialog.Builder(it)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.DATA_SEARCH2)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linear.addView(textViewZaglavie)
            input = EditTextRobotoCondensed(it)
            input.filters = Array<InputFilter>(1) { InputFilter.LengthFilter(4)}
            if (dzenNoch) {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                input.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                input.setBackgroundResource(R.color.colorIcons)
            }
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            linear.addView(input)
            if (savedInstanceState != null) {
                val sValue = savedInstanceState.getString("value", "")
                input.setText(sValue)
            }
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.requestFocus()
            input.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // Скрываем клавиатуру
                    val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(input.windowToken, 0)
                    if (input.text.toString() == "") {
                        error()
                    } else {
                        value = input.text.toString().toInt()
                        if (value in 1583..2089) {
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
            // Показываем клавиатуру
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            builder.setView(linear)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                // Скрываем клавиатуру
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(input.windowToken, 0)
                if (input.text.toString() == "") {
                    error()
                } else {
                    value = input.text.toString().toInt()
                    if (value in 1583..2089) {
                        mListener.setPasxa(value)
                    } else {
                        error()
                    }
                }
        }
        builder.setNegativeButton(getString(R.string.CANCEL)) { _: DialogInterface?, _: Int ->
            val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm12.hideSoftInputFromWindow(input.windowToken, 0)
        }
        alert = builder.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
        }
    }
    return alert
}

private fun error() {
    activity?.let {
        val layout = LinearLayout(it)
        if (dzenNoch) layout.setBackgroundResource(R.color.colorPrimary_black) else layout.setBackgroundResource(R.color.colorPrimary)
        val toast = TextViewRobotoCondensed(it)
        toast.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
        toast.setPadding(realpadding, realpadding, realpadding, realpadding)
        toast.text = getString(R.string.error)
        toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
        layout.addView(toast)
        val mes = Toast(it)
        mes.duration = Toast.LENGTH_SHORT
        mes.view = layout
        mes.show()
    }
}
}