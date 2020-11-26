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

class DialogClearHishory : DialogFragment() {
    private lateinit var mListener: DialogClearHistoryListener
    private lateinit var alert: AlertDialog

    interface DialogClearHistoryListener {
        fun cleanFullHistory()
        fun cleanHistory(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogClearHistoryListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogClearHistoryListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.clean_histopy_title)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            if (arguments?.getString("itemName")?: "" == "")
                textView.text = getString(R.string.all_clean_histopy)
            else
                textView.text = getString(R.string.all_clean_item_histopy, arguments?.getString("itemName")?: "")
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            if (arguments?.getInt("position")?: -1 == -1)
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.cleanFullHistory() }
            else
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.cleanHistory(arguments?.getInt("position")?: -1) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(linearLayout)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(position: Int = -1, itemName: String = ""): DialogClearHishory {
            val dialogClearHishory = DialogClearHishory()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("itemName", itemName)
            dialogClearHishory.arguments = bundle
            return dialogClearHishory
        }
    }
}