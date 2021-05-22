package by.carkva_gazeta.biblijateka

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity

class DialogSetPageBiblioteka : DialogFragment() {
    private var page = 0
    private var pageCount = 0
    private var mListener: DialogSetPageBibliotekaListener? = null
    private lateinit var builder: AlertDialog.Builder

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
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = String.format("Увядзіце нумар старонкі. Усяго: %s", pageCount)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linear.addView(textViewZaglavie)
            val input = EditText(it)
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            linear.addView(input)
            input.setText(page.toString())
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.requestFocus()
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            builder.setView(linear)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                // Скрываем клавиатуру
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(input.windowToken, 0)
                if (input.text.toString() == "") {
                    MainActivity.toastView(it, getString(R.string.error))
                } else {
                    val value: Int = try {
                        input.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        1
                    }
                    if (value in 1..pageCount) {
                        mListener?.onDialogSetPage(value)
                    } else {
                        MainActivity.toastView(it, getString(R.string.error))
                    }
                }
            }
            builder.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface?, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(input.windowToken, 0)
            }
            builder.setNeutralButton("На пачатак") { _: DialogInterface?, _: Int ->
                mListener?.onDialogSetPage(1)
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(input.windowToken, 0)
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