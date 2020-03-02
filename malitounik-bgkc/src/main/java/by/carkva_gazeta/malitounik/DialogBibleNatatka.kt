package by.carkva_gazeta.malitounik

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
import java.util.*

/**
 * Created by oleg on 29.3.19
 */
class DialogBibleNatatka : DialogFragment() {
    private var redaktor = false
    private var position = 0
    private var semuxa = 0
    private lateinit var novyzavet: String
    private var kniga = 0
    private var glava = 0
    private var stix = 0
    private lateinit var bibletext: String
    private lateinit var ad: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semuxa = arguments?.getInt("semuxa") ?: 0
        novyzavet = arguments?.getString("novyzavet") ?: ""
        kniga = arguments?.getInt("kniga") ?: 0
        glava = arguments?.getInt("glava") ?: 0
        stix = arguments?.getInt("stix") ?: 0
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
            if (semuxa == 1) {
                MaranAtaGlobalList.natatkiSemuxa?.let {
                    for (i in it.indices) {
                        if (it[i][0].contains(novyzavet) && it[i][1].toInt() == kniga && it[i][2].toInt() == glava && it[i][3].toInt() == stix) {
                            redaktor = true
                            editText = it[i][5]
                            position = i
                            break
                        }
                    }
                }
            }
            if (semuxa == 2) {
                MaranAtaGlobalList.natatkiSinodal?.let {
                    for (i in it.indices) {
                        if (it[i][0].contains(novyzavet) && it[i][1].toInt() == kniga && it[i][2].toInt() == glava && it[i][3].toInt() == stix) {
                            redaktor = true
                            editText = it[i][5]
                            position = i
                            break
                        }
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
                if (semuxa == 1) {
                    if (redaktor && MaranAtaGlobalList.natatkiSemuxa?.let { it.size > 0 } == true) {
                        MaranAtaGlobalList.natatkiSemuxa?.let { if (editTextView.text.toString() == "") it.removeAt(position) else it[position][5] = editTextView.text.toString() }
                    } else {
                        if (editTextView.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(novyzavet)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(editTextView.text.toString())
                            MaranAtaGlobalList.natatkiSemuxa?.add(0, temp)
                        }
                    }
                }
                if (semuxa == 2) {
                    if (redaktor && MaranAtaGlobalList.natatkiSinodal?.let { it.size > 0 } == true) {
                        MaranAtaGlobalList.natatkiSinodal?.let { if (editTextView.text.toString() == "") it.removeAt(position) else it[position][5] = editTextView.text.toString() }
                    } else {
                        if (editTextView.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(novyzavet)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(editTextView.text.toString())
                            MaranAtaGlobalList.natatkiSinodal?.add(0, temp)
                        }
                    }
                }
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(editTextView.windowToken, 0)
                dialog.cancel()
            }
            ad.setNeutralButton(getString(R.string.bible_natatka)) { dialog: DialogInterface, _: Int ->
                MaranAtaGlobalList.natatkiSemuxa?.let {
                    if (semuxa == 1 && it.size > 0) it.removeAt(position)
                }
                MaranAtaGlobalList.natatkiSinodal?.let {
                    if (semuxa == 2 && it.size > 0) it.removeAt(position)
                }
                //if (semuxa == 3 && MaranAta_Global_List.getNatatkiPsalterNadsana().size() > 0)
//    MaranAta_Global_List.getNatatkiPsalterNadsana().remove(position);
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(editTextView.windowToken, 0)
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
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            val btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL)
            btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        }
        return alert
    }

    companion object {
        fun getInstance(semuxa: Int, novyzavet: String, kniga: Int, glava: Int, stix: Int, bibletext: String): DialogBibleNatatka {
            val zametka = DialogBibleNatatka()
            val bundle = Bundle()
            bundle.putInt("semuxa", semuxa)
            bundle.putString("novyzavet", novyzavet)
            bundle.putInt("kniga", kniga)
            bundle.putInt("glava", glava)
            bundle.putInt("stix", stix)
            bundle.putString("bibletext", bibletext)
            zametka.arguments = bundle
            return zametka
        }
    }
}