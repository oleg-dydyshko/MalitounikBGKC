package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.R

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
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(it, style)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            var editText = ""
            val textViewZaglavie = TextView(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.setText(R.string.natatka_bersha_biblii)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            if (novyzavet)
                nov = "1"
            if (semuxa) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(nov) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSemuxa[i].list[5]
                        position = i
                        break
                    }
                }
            } else {
                for (i in BibleGlobalList.natatkiSinodal.indices) {
                    if (BibleGlobalList.natatkiSinodal[i].list[0].contains(nov) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSinodal[i].list[5]
                        position = i
                        break
                    }
                }
            }
            val editTextView = EditText(it)
            editTextView.setPadding(realpadding, realpadding, realpadding, realpadding)
            editTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            editTextView.setText(editText)
            editTextView.requestFocus()
            if (dzenNoch) {
                editTextView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                editTextView.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            } else {
                editTextView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                editTextView.setBackgroundResource(R.color.colorWhite)
            }
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            linearLayout.addView(editTextView)
            ad.setView(linearLayout)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                if (semuxa) {
                    if (redaktor && BibleGlobalList.natatkiSemuxa.size > 0) {
                        if (editTextView.text.toString() == "") BibleGlobalList.natatkiSemuxa.removeAt(position)
                        else BibleGlobalList.natatkiSemuxa[position].list[5] = editTextView.text.toString()
                    } else {
                        if (editTextView.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(nov)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(editTextView.text.toString())
                            var maxIndex: Long = 0
                            BibleGlobalList.natatkiSemuxa.forEach {
                                if (maxIndex < it.id)
                                    maxIndex = it.id
                            }
                            maxIndex++
                            BibleGlobalList.natatkiSemuxa.add(0, BibleNatatkiData(maxIndex, temp))
                        }
                    }
                } else {
                    if (redaktor && BibleGlobalList.natatkiSinodal.size > 0) {
                        if (editTextView.text.toString() == "") BibleGlobalList.natatkiSinodal.removeAt(position)
                        else BibleGlobalList.natatkiSinodal[position].list[5] = editTextView.text.toString()
                    } else {
                        if (editTextView.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(nov)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(editTextView.text.toString())
                            var maxIndex: Long = 0
                            BibleGlobalList.natatkiSinodal.forEach {
                                if (maxIndex < it.id)
                                    maxIndex = it.id
                            }
                            maxIndex++
                            BibleGlobalList.natatkiSinodal.add(0, BibleNatatkiData(maxIndex, temp))
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
            ad.setNegativeButton(R.string.cansel) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(editTextView.windowToken, 0)
                dialog.cancel()
            }
        }
        return ad.create()
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