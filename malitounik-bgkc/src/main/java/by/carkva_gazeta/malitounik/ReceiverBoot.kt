package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

/**
 * Created by oleg on 21.11.16.
 */
class ReceiverBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.QUICKBOOT_POWERON" || intent.action == "com.htc.intent.action.QUICKBOOT_POWERON") {
            val c = Calendar.getInstance() as GregorianCalendar
            val i = Intent(context, ReceiverUpdate::class.java)
            i.action = "UPDATE"
            val pServise = PendingIntent.getBroadcast(context, 10, i, PendingIntent.FLAG_ONE_SHOT)
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (c.timeInMillis > mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])) c.add(Calendar.DATE, 1)
            am.setRepeating(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), 86400000L, pServise)
            context.sendBroadcast(i)
        }
    }

    private fun mkTime(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[year, month, day, 10, 0] = 0
        return calendar.timeInMillis
    }
}