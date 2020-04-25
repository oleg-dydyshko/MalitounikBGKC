package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * Created by oleg on 29.9.17
 */
class DialogSabytieShow : DialogFragment() {
    private var title: String = ""
    private var data: String = ""
    private var time: String = ""
    private var dataK: String = ""
    private var timeK: String = ""
    private var res: String = ""
    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title") ?: ""
        data = arguments?.getString("data") ?: ""
        time = arguments?.getString("time") ?: ""
        dataK = arguments?.getString("dataK") ?: ""
        timeK = arguments?.getString("timeK") ?: ""
        res = arguments?.getString("res") ?: ""
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val textViewT = TextViewRobotoCondensed(it)
            textViewT.text = title.toUpperCase(Locale.getDefault())
            textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewT.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewT.setTypeface(null, Typeface.BOLD)
            if (dzenNoch) textViewT.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewT.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            linearLayout.addView(textViewT)
            val textView = TextViewRobotoCondensed(it)
            if (data == dataK && time == timeK) {
                textView.text = "Калі: $data $time\n$res"
            } else {
                textView.text = "Пачатак: $data $time\nКанец: $dataK $timeK\n$res"
            }
            if (dzenNoch) {
                textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            } else {
                textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            linearLayout.addView(textView)
            val ad = AlertDialog.Builder(it)
            ad.setView(linearLayout)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            val ts = data.split(".").toTypedArray()
            val g = GregorianCalendar(ts[2].toInt(), ts[1].toInt() - 1, ts[0].toInt())
            if (g[Calendar.YEAR] <= SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
                ad.setNeutralButton(getString(R.string.sabytie_kaliandar)) { dialog: DialogInterface, _: Int ->
                    val intent = Intent()
                    intent.putExtra("data", g[Calendar.DAY_OF_YEAR] - 1)
                    intent.putExtra("year", g[Calendar.YEAR])
                    it.setResult(Activity.RESULT_OK, intent)
                    it.finish()
                    dialog.cancel()
                }
            }
            alert = ad.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
                val btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL)
                btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            }
        }
        return alert
    }

    companion object {
        fun getInstance(title: String, data: String, time: String, dataK: String, timeK: String, res: String): DialogSabytieShow {
            val dialogShowSabytie = DialogSabytieShow()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("data", data)
            bundle.putString("time", time)
            bundle.putString("dataK", dataK)
            bundle.putString("timeK", timeK)
            bundle.putString("res", res)
            dialogShowSabytie.arguments = bundle
            return dialogShowSabytie
        }
    }
}