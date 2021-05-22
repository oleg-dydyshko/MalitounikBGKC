package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogHelpListView : DialogFragment() {

    private lateinit var ad: AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { it ->
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(it, style)
            val scrollView = ScrollView(it)
            scrollView.isVerticalScrollBarEnabled = false
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = getString(R.string.help_davedka).uppercase()
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextView(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            when (arguments?.getInt("help") ?: 1) {
                1 -> textView.text = getString(R.string.help_list_view)
                2 -> textView.text = getString(R.string.help_list_sabytie)
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            scrollView.addView(textView)
            linearLayout.addView(scrollView)
            ad.setView(linearLayout)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        return ad.create()
    }

    companion object {
        fun getInstance(help: Int): DialogHelpListView {
            val dialogHelpListView = DialogHelpListView()
            val bundle = Bundle()
            bundle.putInt("help", help)
            dialogHelpListView.arguments = bundle
            return dialogHelpListView
        }
    }
}