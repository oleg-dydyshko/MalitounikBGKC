package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
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
class DialogBibleNatatka : DialogFragment() {
    private var redaktor = false
    private var position = 0
    private var semuxa = true
    private var novyzavet = false
    private var nov = "0"
    private var kniga = 0
    private var glava = BibleGlobalList.mListGlava
    private var stix = BibleGlobalList.bibleCopyList[0]
    private var bibletext = ""
    private lateinit var ad: AlertDialog.Builder
    private var dialogBibleNatatkaListiner: DialogBibleNatatkaListiner? = null

    interface DialogBibleNatatkaListiner {
        fun addNatatka()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            dialogBibleNatatkaListiner = try {
                context as DialogBibleNatatkaListiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibleNatatkaListiner")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semuxa = arguments?.getBoolean("semuxa") ?: true
        novyzavet = arguments?.getBoolean("novyzavet") ?: false
        kniga = arguments?.getInt("kniga") ?: 0
        bibletext = arguments?.getString("bibletext") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { it ->
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            ad = AlertDialog.Builder(it)
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
            if (novyzavet)
                nov = "1"
            if (semuxa) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i][0].contains(nov) && BibleGlobalList.natatkiSemuxa[i][1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i][2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i][3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSemuxa[i][5]
                        position = i
                        break
                    }
                }
            } else {
                for (i in BibleGlobalList.natatkiSinodal.indices) {
                    if (BibleGlobalList.natatkiSinodal[i][0].contains(nov) && BibleGlobalList.natatkiSinodal[i][1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i][2].toInt() == glava && BibleGlobalList.natatkiSinodal[i][3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSinodal[i][5]
                        position = i
                        break
                    }
                }
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
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                if (semuxa) {
                    if (redaktor && BibleGlobalList.natatkiSemuxa.size > 0) {
                        if (editTextView.text.toString() == "") BibleGlobalList.natatkiSemuxa.removeAt(position)
                        else BibleGlobalList.natatkiSemuxa[position][5] = editTextView.text.toString()
                    } else {
                        if (editTextView.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(nov)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(editTextView.text.toString())
                            BibleGlobalList.natatkiSemuxa.add(0, temp)
                        }
                    }
                } else {
                    if (redaktor && BibleGlobalList.natatkiSinodal.size > 0) {
                        if (editTextView.text.toString() == "") BibleGlobalList.natatkiSinodal.removeAt(position)
                        else BibleGlobalList.natatkiSinodal[position][5] = editTextView.text.toString()
                    } else {
                        if (editTextView.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(nov)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(editTextView.text.toString())
                            BibleGlobalList.natatkiSinodal.add(0, temp)
                        }
                    }
                }
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(editTextView.windowToken, 0)
                dialogBibleNatatkaListiner?.addNatatka()
                dialog.cancel()
            }
            ad.setNeutralButton(getString(R.string.bible_natatka)) { dialog: DialogInterface, _: Int ->
                if (semuxa && BibleGlobalList.natatkiSemuxa.size > 0) BibleGlobalList.natatkiSemuxa.removeAt(position)
                if (!semuxa && BibleGlobalList.natatkiSinodal.size > 0) BibleGlobalList.natatkiSinodal.removeAt(position)
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(editTextView.windowToken, 0)
                dialogBibleNatatkaListiner?.addNatatka()
                dialog.cancel()
            }
            ad.setNegativeButton(R.string.CANCEL) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(editTextView.windowToken, 0)
                dialog.cancel()
            }
        }
        val alert = ad.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            val btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL)
            btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
        }
        return alert
    }

    companion object {
        fun getInstance(semuxa: Boolean, novyzavet: Boolean, kniga: Int, bibletext: String): DialogBibleNatatka {
            val zametka = DialogBibleNatatka()
            val bundle = Bundle()
            bundle.putBoolean("semuxa", semuxa)
            bundle.putBoolean("novyzavet", novyzavet)
            bundle.putInt("kniga", kniga)
            bundle.putString("bibletext", bibletext)
            zametka.arguments = bundle
            return zametka
        }
    }
}