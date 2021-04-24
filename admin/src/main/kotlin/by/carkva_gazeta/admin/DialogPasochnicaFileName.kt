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
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.EditTextRobotoCondensed
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.util.*

class DialogPasochnicaFileName : DialogFragment() {
    private lateinit var input: EditTextRobotoCondensed
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
            input = EditTextRobotoCondensed(it)
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val text = if (savedInstanceState != null) {
                input.setText(savedInstanceState.getString("fileName"))
                savedInstanceState.getString("fileName") ?: "newFile.html"
            } else {
                input.setText(arguments?.getString("oldFileName") ?: "newFile.html")
                arguments?.getString("oldFileName") ?: "newFile.html"
            }
            val t2 = text.lastIndexOf(".")
            if (t2 != -1) {
                input.setSelection(0, t2)
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
            input.post {
                val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            linearLayout.addView(input)
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(input.windowToken, 0)
                dialog.cancel()
            }
            val oldFileName = arguments?.getString("oldFileName") ?: "newFile.html"
            val textNetral = if (oldFileName.contains(".htm")) {
                resources.getString(by.carkva_gazeta.malitounik.R.string.set_file_txt)
            } else {
                resources.getString(by.carkva_gazeta.malitounik.R.string.set_file_html)
            }
            builder.setNeutralButton(textNetral) { _: DialogInterface?, _: Int ->
                var fileName = input.text.toString()
                val t1 = fileName.lastIndexOf(".")
                if (fileName == "") {
                    val gc = Calendar.getInstance()
                    val mun = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
                    fileName = gc[Calendar.DATE].toString() + "_" + mun[gc[Calendar.MONTH]] + "_" + gc[Calendar.YEAR] + "_" + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
                }
                fileName = if (oldFileName.contains(".htm")) {
                    if (t1 != -1) {
                        fileName.substring(0, t1) + ".txt"
                    } else {
                        "$fileName.txt"
                    }
                } else {
                    if (t1 != -1) {
                        fileName.substring(0, t1) + ".html"
                    } else {
                        "$fileName.html"
                    }
                }
                mListener?.setFileName(oldFileName, fileName)
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
        val oldFileName = arguments?.getString("oldFileName") ?: "newFile.html"
        mListener?.setFileName(oldFileName, fileName)
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