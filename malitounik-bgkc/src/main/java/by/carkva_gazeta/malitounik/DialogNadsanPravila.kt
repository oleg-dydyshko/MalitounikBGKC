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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by oleg on 23.8.16
 */
class DialogNadsanPravila : DialogFragment() {
    private lateinit var alert: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { it ->
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) it.setTheme(R.style.AppCompatDark) else it.setTheme(R.style.AppTheme)
            val builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.peryiady).toUpperCase(Locale.getDefault())
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val scrollView = ScrollView(it)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            val inputStream = resources.openRawResource(R.raw.nadsan_periody)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            textView.text = MainActivity.fromHtml(reader.readText())
            inputStream.close()
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            scrollView.addView(textView)
            linearLayout.addView(scrollView)
            builder.setView(linearLayout)
            builder.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
        }
        return alert
    }
}