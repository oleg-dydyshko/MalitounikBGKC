package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity

class DialogDeliteAllZakladkiINatatki : DialogFragment() {
    private var mListener: DialogDeliteAllZakladkiINatatkiListener? = null
    private var zakladkaAlboNatatka: String = ""
    private var semuxa = 0
    private lateinit var builder: AlertDialog.Builder

    internal interface DialogDeliteAllZakladkiINatatkiListener {
        fun fileAllNatatkiAlboZakladki(semuxa: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteAllZakladkiINatatkiListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement Dialog_delite_all_zakladki_i_natatki_Listener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zakladkaAlboNatatka = arguments?.getString("zakladka_albo_natatka")?: ""
        semuxa = arguments?.getInt("semuxa")?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.Trash).uppercase()
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextView(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = getString(R.string.delite_all_natatki_i_zakladki, zakladkaAlboNatatka)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.fileAllNatatkiAlboZakladki(semuxa) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(linearLayout)
        }
        return builder.create()
    }

    companion object {
        fun getInstance(zakladka_albo_natatka: String?, semuxa: Int): DialogDeliteAllZakladkiINatatki {
            val dialogDelite = DialogDeliteAllZakladkiINatatki()
            val bundle = Bundle()
            bundle.putString("zakladka_albo_natatka", zakladka_albo_natatka)
            bundle.putInt("semuxa", semuxa)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}