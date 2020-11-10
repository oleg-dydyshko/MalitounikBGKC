package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.R

/**
 * Created by oleg on 29.3.19
 */
class DialogBibleNatatkaEdit : DialogFragment() {
    private var edit: BibleNatatkaEditlistiner? = null
    private var semuxa = 0
    private var position = 0
    private lateinit var ad: AlertDialog.Builder

    internal interface BibleNatatkaEditlistiner {
        fun setEdit()
        fun editCancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            edit = try {
                context as BibleNatatkaEditlistiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement BibleNatatkaEditlistiner")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        edit?.editCancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semuxa = arguments?.getInt("semuxa") ?: 0
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(it, style)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            var editText = ""
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.setText(R.string.natatka_bersha_biblii)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            if (semuxa == 1) {
                editText = BibleGlobalList.natatkiSemuxa[position].list[5]
            }
            if (semuxa == 2) {
                editText = BibleGlobalList.natatkiSinodal[position].list[5]
            }
            val editTextView = EditTextRobotoCondensed(it)
            editTextView.setPadding(realpadding, realpadding, realpadding, realpadding)
            editTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                editTextView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                editTextView.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                editTextView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                editTextView.setBackgroundResource(R.color.colorIcons)
            }
            editTextView.setText(editText)
            editTextView.requestFocus()
            // Показываем клавиатуру
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            linearLayout.addView(editTextView)
            ad.setView(linearLayout)
            ad.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface, _: Int ->
                if (semuxa == 1) {
                    if (editTextView.text.toString() == "") BibleGlobalList.natatkiSemuxa.removeAt(position) else BibleGlobalList.natatkiSemuxa[position].list[5] = editTextView.text.toString()
                }
                if (semuxa == 2) {
                    if (editTextView.text.toString() == "") BibleGlobalList.natatkiSinodal.removeAt(position) else BibleGlobalList.natatkiSinodal[position].list[5] = editTextView.text.toString()
                }
                it.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                edit?.setEdit()
            }
            ad.setNeutralButton(getString(R.string.bible_natatka)) { _: DialogInterface, _: Int ->
                if (semuxa == 1 && BibleGlobalList.natatkiSemuxa.size > 0) BibleGlobalList.natatkiSemuxa.removeAt(position)
                if (semuxa == 2 && BibleGlobalList.natatkiSinodal.size > 0) BibleGlobalList.natatkiSinodal.removeAt(position)
                it.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                edit?.setEdit()
            }
            ad.setNegativeButton(R.string.CANCEL) { _: DialogInterface, _: Int ->
                it.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                edit?.editCancel()
            }
        }
        return ad.create()
    }

    companion object {
        fun getInstance(semuxa: Int, position: Int): DialogBibleNatatkaEdit {
            val zametka = DialogBibleNatatkaEdit()
            val bundle = Bundle()
            bundle.putInt("semuxa", semuxa)
            bundle.putInt("position", position)
            zametka.arguments = bundle
            return zametka
        }
    }
}