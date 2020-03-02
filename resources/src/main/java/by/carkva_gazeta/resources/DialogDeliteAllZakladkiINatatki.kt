package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.util.*

/**
 * Created by oleg on 4.4.19
 */
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
            builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.Trash).toUpperCase(Locale.getDefault())
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = getString(R.string.delite_all_natatki_i_zakladki, zakladkaAlboNatatka)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.fileAllNatatkiAlboZakladki(semuxa) }
            builder.setNegativeButton(resources.getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(linearLayout)
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