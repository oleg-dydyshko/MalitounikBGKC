package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class WidgetConfig : AppCompatActivity(), DialogWidgetConfig.DialogWidgetConfigListener {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultValue: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        resultValue = Intent()
        resultValue?.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        setResult(Activity.RESULT_CANCELED, resultValue)
        val config = DialogWidgetConfig.getInstance(widgetID)
        config.show(supportFragmentManager, "config")
    }

    override fun onDialogWidgetConfigPositiveClick() {
        val sp = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit().putBoolean("WIDGET_ENABLED", true).apply()
        val updateAllWidgets = "update_all_widgets"
        val intent = Intent(this, Widget::class.java)
        intent.action = updateAllWidgets
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val pIntentBoot = PendingIntent.getBroadcast(this, 53, intent, flags)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(this, 50, intent, flags)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
            else -> {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun mkTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}