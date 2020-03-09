package by.carkva_gazeta.malitounik

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

/**
 * Created by oleg on 8.3.18
 */
class DialogDeliteAllVybranoe : DialogFragment() {
    private lateinit var mListener: DialogDeliteAllVybranoeListener
    private lateinit var alert: AlertDialog

    interface DialogDeliteAllVybranoeListener {
        fun deliteAllVybranoe()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteAllVybranoeListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogDeliteAllVybranoeListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val builder = AlertDialog.Builder(it)
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
            textView.text = getString(R.string.del_all_vybranoe)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.deliteAllVybranoe() }
            builder.setNegativeButton(resources.getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(linearLayout)
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
        }
        return alert
    }
}