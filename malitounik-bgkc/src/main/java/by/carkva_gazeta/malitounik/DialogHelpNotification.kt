package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * Created by oleg on 21.7.17
 */
class DialogHelpNotification : DialogFragment() {
    private lateinit var alert: AlertDialog
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val builder = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            if (dzenNoch) linearLayout.setBackgroundResource(R.color.colorbackground_material_dark_ligte) else linearLayout.setBackgroundResource(R.color.colorIcons)
            builder.setView(linearLayout)
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = getString(R.string.notifi_fix).toUpperCase(Locale.getDefault())
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setText(R.string.notify_help)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            val prefEditor = k.edit()
            prefEditor.putBoolean("check_notifi", false)
            prefEditor.apply()
            builder.setPositiveButton(resources.getText(R.string.tools_item)) { _: DialogInterface?, _: Int ->
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
            builder.setNegativeButton(resources.getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            }
        }
        return alert
    }
}