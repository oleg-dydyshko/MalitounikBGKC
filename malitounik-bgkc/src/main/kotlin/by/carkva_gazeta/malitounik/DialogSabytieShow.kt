package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogSabytieShow : DialogFragment() {
    private var title = ""
    private var data = ""
    private var time = ""
    private var dataK = ""
    private var timeK = ""
    private var res = ""
    private var paz = false
    private var konecSabytie = true
    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title") ?: ""
        data = arguments?.getString("data") ?: ""
        time = arguments?.getString("time") ?: ""
        dataK = arguments?.getString("dataK") ?: ""
        timeK = arguments?.getString("timeK") ?: ""
        res = arguments?.getString("res") ?: ""
        paz = arguments?.getBoolean("paz") ?: false
        konecSabytie = arguments?.getBoolean("konecSabytie") ?: true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val textViewT = TextView(it)
            textViewT.text = title
            textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewT.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewT.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            if (dzenNoch) textViewT.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewT.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            linearLayout.addView(textViewT)
            val textView = TextView(it)
            val textR = if (konecSabytie) {
                SpannableString(getString(R.string.sabytie_kali, data, time, res))
            } else {
                SpannableString(getString(R.string.sabytie_pachatak_show, data, time, dataK, timeK, res))
            }
            val t1 = textR.indexOf(res)
            if (dzenNoch) {
                textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                if (paz)
                    textR.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary_black)), t1, textR.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                if (paz)
                    textR.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary)), t1, textR.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textView.text = textR
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            linearLayout.addView(textView)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            ad.setView(linearLayout)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    companion object {
        fun getInstance(title: String, data: String, time: String, dataK: String, timeK: String, res: String, paz: Boolean, konecSabytie: Boolean): DialogSabytieShow {
            val dialogShowSabytie = DialogSabytieShow()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("data", data)
            bundle.putString("time", time)
            bundle.putString("dataK", dataK)
            bundle.putString("timeK", timeK)
            bundle.putString("res", res)
            bundle.putBoolean("paz", paz)
            bundle.putBoolean("konecSabytie", konecSabytie)
            dialogShowSabytie.arguments = bundle
            return dialogShowSabytie
        }
    }
}