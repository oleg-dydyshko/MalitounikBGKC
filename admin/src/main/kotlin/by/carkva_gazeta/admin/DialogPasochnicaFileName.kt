package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.util.*

class DialogPasochnicaFileName : DialogFragment() {
    private lateinit var input: EditText
    private var mListener: DialogPasochnicaFileNameListener? = null
    private lateinit var builder: AlertDialog.Builder

    internal interface DialogPasochnicaFileNameListener {
        fun setFileName(oldFileName: String, fileName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPasochnicaFileNameListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPasochnicaFileNameListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", input.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            val linearLayout2 = LinearLayout(it)
            linearLayout2.orientation = LinearLayout.VERTICAL
            builder.setView(linearLayout2)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout2.addView(linearLayout)
            val textViewZaglavie = TextViewRobotoCondensed(it)
            textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = getString(by.carkva_gazeta.malitounik.R.string.set_file_name)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            input = EditText(it)
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (savedInstanceState != null) {
                input.setText(savedInstanceState.getString("fileName"))
            } else {
                var oldFileName = arguments?.getString("oldFileName") ?: ""
                if (oldFileName != "") {
                    val t1 = oldFileName.split(".")
                    oldFileName = t1[0]
                }
                input.setText(oldFileName)
            }
            input.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            input.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            input.setPadding(realpadding, realpadding, realpadding, realpadding)
            input.requestFocus()
            input.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    setFileName()
                    dialog?.cancel()
                }
                false
            }
            input.imeOptions = EditorInfo.IME_ACTION_GO
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            linearLayout.addView(input)
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(input.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int ->
                setFileName()
            }
        }
        return builder.create()
    }

    private fun setFileName() {
        var fileName = input.text.toString()
        if (fileName == "") {
            val gc = Calendar.getInstance()
            val mun = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
            fileName = gc[Calendar.DATE].toString() + "_" + mun[gc[Calendar.MONTH]] + "_" + gc[Calendar.YEAR] + "_" + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
        }
        val oldFileName = arguments?.getString("oldFileName") ?: ""
        mListener?.setFileName(oldFileName,"$fileName.html")
    }

    companion object {
        fun getInstance(oldFileName: String): DialogPasochnicaFileName {
            val instance = DialogPasochnicaFileName()
            val args = Bundle()
            args.putString("oldFileName", oldFileName)
            instance.arguments = args
            return instance
        }
    }
}