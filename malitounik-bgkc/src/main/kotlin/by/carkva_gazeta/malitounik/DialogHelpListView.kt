package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.*

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
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = getString(R.string.help_davedka).toUpperCase(Locale.getDefault())
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            var texthelp = getString(R.string.help_list_view)
            if (arguments?.getInt("help") == 2) {
                val t1 = texthelp.indexOf("\n")
                val t2 = texthelp.indexOf(",")
                val t3 = texthelp.indexOf(".\n", t2)
                val texthelp2 = texthelp.substring(t1 + 1, t2)
                val texthelp3 = texthelp.substring(t3)
                texthelp = texthelp2.plus(texthelp3)
            }
            if (arguments?.getInt("help") == 3)
                texthelp = getString(R.string.help_paslia_prychastia)
            textView.text = texthelp
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