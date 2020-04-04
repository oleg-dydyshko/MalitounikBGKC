package by.carkva_gazeta.biblijateka

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.util.*

/**
 * Created by oleg on 21.7.17
 */
class DialogMessage : DialogFragment() {
    private lateinit var alert: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val ad = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = arguments?.getString("title") ?: ""
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linearLayout.addView(textViewZaglavie)
            val textView = TextViewRobotoCondensed(it)
            textView.setPadding(realpadding, realpadding, realpadding, realpadding)
            textView.text = arguments?.getString("massege") ?: ""
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            linearLayout.addView(textView)
            ad.setView(linearLayout)
            val sql = arguments?.getBoolean("sql") ?: false
            var okButtomtext = resources.getString(R.string.ok)
            if (sql) {
                okButtomtext = "Адчыніць"
                ad.setNegativeButton(resources.getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
            }
            ad.setPositiveButton(okButtomtext) { dialog: DialogInterface, _: Int ->
                val uri = Uri.parse(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "*/*")
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                val activities: List<ResolveInfo> = it.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                val isIntentSafe = activities.isNotEmpty()
                if (isIntentSafe)
                    startActivity(intent)
                else
                    startActivity(Intent.createChooser(intent, "Адчыніць папку"))
                dialog.cancel()
            }
            alert = ad.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
        }
        return alert
    }

    companion object {
        fun getInstance(sql: Boolean, title: String, message: String): DialogMessage {
            val instance = DialogMessage()
            val args = Bundle()
            args.putString("title", title)
            args.putString("massege", message)
            args.putBoolean("sql", sql)
            instance.arguments = args
            return instance
        }
    }
}