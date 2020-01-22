package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.EditTextRobotoCondensed
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.util.*

/**
 * Created by oleg on 21.7.17
 */
class DialogBibleRazdel : DialogFragment() {
    private var fullGlav = 0
    private lateinit var input: EditTextRobotoCondensed
    private var mListener: DialogBibleRazdelListener? = null
    private lateinit var builder: AlertDialog.Builder

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
        fullGlav = arguments?.getInt("full_glav")?: 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("glava", Objects.requireNonNull(input.text).toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = Objects.requireNonNull(it).getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) it.setTheme(R.style.AppCompatDark) else it.setTheme(R.style.AppTheme)
            builder = AlertDialog.Builder(it)
            val linearLayout2 = LinearLayout(it)
            linearLayout2.orientation = LinearLayout.VERTICAL
            builder.setView(linearLayout2)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout2.addView(linearLayout)
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.DATA_SEARCH, fullGlav)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            input = EditTextRobotoCondensed(it)
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (savedInstanceState != null) {
                input.setText(savedInstanceState.getString("glava"))
            } else {
                input.setText("")
            }
            input.inputType = InputType.TYPE_CLASS_NUMBER
            if (dzenNoch) {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                input.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                input.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                input.setBackgroundResource(R.color.colorIcons)
            }
            input.setPadding(realpadding, realpadding, realpadding, realpadding) //10, 0, 0, 0
            input.requestFocus()
            // Показываем клавиатуру
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            linearLayout.addView(input)
            builder.setNegativeButton(resources.getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                Objects.requireNonNull(imm12).hideSoftInputFromWindow(input.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                // Скрываем клавиатуру
                val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                Objects.requireNonNull(imm1).hideSoftInputFromWindow(input.windowToken, 0)
                if (Objects.requireNonNull(input.text).toString() == "") {
                    val layout = LinearLayout(it)
                    if (dzenNoch) layout.setBackgroundResource(R.color.colorPrimary_black) else layout.setBackgroundResource(R.color.colorPrimary)
                    val toast = TextViewRobotoCondensed(it)
                    toast.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                    toast.setPadding(realpadding, realpadding, realpadding, realpadding)
                    toast.text = getString(R.string.error)
                    toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                    layout.addView(toast)
                    val mes = Toast(it)
                    mes.duration = Toast.LENGTH_SHORT
                    mes.view = layout
                    mes.show()
                } else {
                    val value: Int = try {
                        input.text.toString().toInt() - 1
                    } catch (e: NumberFormatException) {
                        -1
                    }
                    if (value in 0 until fullGlav) {
                        mListener?.onComplete(value)
                    } else {
                        val layout = LinearLayout(it)
                        if (dzenNoch) layout.setBackgroundResource(R.color.colorPrimary_black) else layout.setBackgroundResource(R.color.colorPrimary)
                        val toast = TextViewRobotoCondensed(it)
                        toast.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                        toast.setPadding(realpadding, realpadding, realpadding, realpadding)
                        toast.text = getString(R.string.error)
                        toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                        layout.addView(toast)
                        val mes = Toast(it)
                        mes.duration = Toast.LENGTH_SHORT
                        mes.view = layout
                        mes.show()
                    }
                }
            }
        }
        val alert = builder.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        }
        return alert
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