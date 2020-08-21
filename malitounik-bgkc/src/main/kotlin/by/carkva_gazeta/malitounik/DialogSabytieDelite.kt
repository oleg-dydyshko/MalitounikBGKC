package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogSabytieDelite : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var dialogSabytieDeliteListener: DialogSabytieDeliteListener? = null

    internal interface DialogSabytieDeliteListener {
        fun sabytieDelAll()
        fun sabytieDelOld()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogSabytieDeliteListener = try {
                context as DialogSabytieDeliteListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSabytieDeliteListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity ->
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val ad = AlertDialog.Builder(activity)
            val linearLayout = LinearLayout(activity)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(activity)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.remove)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(activity)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = getString(R.string.remove_sabytie_iak)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            ad.setView(linearLayout)
            ad.setPositiveButton(getString(R.string.sabytie_del_all)) { _: DialogInterface?, _: Int ->
                dialogSabytieDeliteListener?.sabytieDelAll()
            }
            ad.setNeutralButton(getString(R.string.sabytie_del_old)) { _: DialogInterface?, _: Int ->
                dialogSabytieDeliteListener?.sabytieDelOld()
            }
            ad.setNegativeButton(getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
                val btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL)
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
                if (dzenNoch) {
                    btnPositive.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    btnNegative.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    btnNeutral.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                }
            }
        }
        return alert
    }
}