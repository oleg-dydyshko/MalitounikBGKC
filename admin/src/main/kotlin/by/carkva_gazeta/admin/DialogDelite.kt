package by.carkva_gazeta.admin

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
import by.carkva_gazeta.malitounik.SettingsActivity

class DialogDelite : DialogFragment() {
    private var mListener: DialogDeliteListener? = null
    private var position = 0
    private var title = ""
    private lateinit var alert: AlertDialog

    interface DialogDeliteListener {
        fun fileDelite(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
        title = arguments?.getString("title") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(by.carkva_gazeta.malitounik.R.string.remove)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextView(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = getString(by.carkva_gazeta.malitounik.R.string.vybranoe_biblia_delite, title)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            linearLayout.addView(textView)
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.fileDelite(position) }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(linearLayout)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(position: Int, title: String): DialogDelite {
            val dialogDelite = DialogDelite()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("title", title)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}