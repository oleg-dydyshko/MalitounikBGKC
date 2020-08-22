package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*

class DialogSabytieTime : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var dialogSabytieTimeListener: DialogSabytieTimeListener? = null

    internal interface DialogSabytieTimeListener {
        fun sabytieTimePositive(nomerDialoga: Int, hour: Int, minute: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogSabytieTimeListener = try {
                context as DialogSabytieTimeListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSabytieTimeListener")
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val c = Calendar.getInstance() as GregorianCalendar
            val ad = AlertDialog.Builder(it)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            ad.setView(linearLayout)
            val timePicker = TimePicker(it)
            timePicker.setIs24HourView(true)
            val settime = arguments?.getString("time")?.split(":")?.toTypedArray()
            val gc = GregorianCalendar(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH], settime?.get(0)?.toInt()?: 0, settime?.get(1)?.toInt()?: 0, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.hour = gc[Calendar.HOUR_OF_DAY]
                timePicker.minute = gc[Calendar.MINUTE]
            } else {
                timePicker.currentHour = gc[Calendar.HOUR_OF_DAY]
                timePicker.currentMinute = gc[Calendar.MINUTE]
            }
            linearLayout.addView(timePicker)
            val nomerDialoga = arguments?.getInt("numarDialoga")?: 1
            ad.setTitle(getString(R.string.set_time_sabytie))
            ad.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialogSabytieTimeListener?.sabytieTimePositive(nomerDialoga, timePicker.hour, timePicker.minute)
                } else {
                    dialogSabytieTimeListener?.sabytieTimePositive(nomerDialoga, timePicker.currentHour, timePicker.currentMinute)
                }
            }
            ad.setNegativeButton(getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            }
        }
        return alert
    }

    companion object {
        fun getInstance(numarDialoga: Int, time: String): DialogSabytieTime {
            val bundle = Bundle()
            bundle.putInt("numarDialoga", numarDialoga)
            bundle.putString("time", time)
            val dialog = DialogSabytieTime()
            dialog.arguments = bundle
            return dialog
        }
    }
}