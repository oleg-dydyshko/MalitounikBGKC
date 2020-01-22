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

class DialogZakladkaDelite : DialogFragment() {
    private var delite: ZakladkaDeliteListiner? = null
    private var position = 0
    private var name: String = ""
    private var semuxa = 0
    private var zakladka = false
    private lateinit var builder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position")?: 0
        name = arguments?.getString("name")?: ""
        semuxa = arguments?.getInt("semuxa")?: 0
        zakladka = arguments?.getBoolean("zakladka")?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            delite = try {
                context as ZakladkaDeliteListiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement ZakladkaDeliteListiner")
            }
        }
    }

    internal interface ZakladkaDeliteListiner {
        fun zakladkadiliteItem(position: Int, semuxa: Int)
        fun natatkidiliteItem(position: Int, semuxa: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) it.setTheme(R.style.AppCompatDark) else it.setTheme(R.style.AppTheme)
            builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.remove)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            if (zakladka) textView.text = resources.getString(R.string.delite_natatki_i_zakladki, getString(R.string.zakladki_bible2), name) else textView.text = resources.getString(R.string.delite_natatki_i_zakladki, getString(R.string.natatki_biblii2), name)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            if (zakladka) {
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> delite?.zakladkadiliteItem(position, semuxa) }
            } else {
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> delite?.natatkidiliteItem(position, semuxa) }
            }
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
        fun getInstance(position: Int, name: String?, semuxa: Int, zakladka: Boolean): DialogZakladkaDelite {
            val dialogDelite = DialogZakladkaDelite()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("name", name)
            bundle.putInt("semuxa", semuxa)
            bundle.putBoolean("zakladka", zakladka)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}