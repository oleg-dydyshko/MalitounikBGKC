package by.carkva_gazeta.malitounik

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SettingsActivityBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import kotlinx.coroutines.*
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class SettingsActivity : AppCompatActivity(), CheckLogin.CheckLoginListener {
    private lateinit var k: SharedPreferences
    private lateinit var prefEditor: Editor
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private var itemDefault = 0
    private lateinit var binding: SettingsActivityBinding
    private var resetTollbarJob: Job? = null
    private var adminClickTime: Long = 0
    private var adminItemCount = 0
    private var edit = false

    companion object {
        private const val UPDATE_ALL_WIDGETS = "update_all_widgets"
        private const val RESET_MAIN = "reset_main"
        const val GET_DEFAULT_FONT_SIZE = 18F
        const val GET_FONT_SIZE_MIN = 14F
        const val GET_FONT_SIZE_MAX = 54F
        const val GET_FONT_SIZE_TOAST = 12F
        const val GET_CALIANDAR_YEAR_MIN = 2018
        const val GET_CALIANDAR_YEAR_MAX = 2022
        const val NOTIFICATION_CHANNEL_ID_SABYTIE = "3001"
        const val NOTIFICATION_CHANNEL_ID_SVIATY = "2001"

        private fun mkTime(year: Int, month: Int, day: Int, hour: Int): Long {
            val calendar = Calendar.getInstance() as GregorianCalendar
            calendar[year, month, day, hour, 0] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar.timeInMillis
        }

        private fun mkTimeDayOfYear(year: Int, month: Int, day: Int): Int {
            val calendar = Calendar.getInstance() as GregorianCalendar
            calendar[year, month, day, 19, 0] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar[Calendar.DAY_OF_YEAR]
        }

        private fun mkTimeYear(year: Int, month: Int, day: Int): Int {
            val calendar = Calendar.getInstance() as GregorianCalendar
            calendar[year, month, day, 19, 0] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar[Calendar.YEAR]
        }

        private fun createIntent(context: Context, action: String, extra: String): Intent {
            val intent = Intent(context, ReceiverBroad::class.java)
            intent.action = action
            intent.putExtra("extra", extra)
            return intent
        }

        private fun createIntent(context: Context, action: String, extra: String, dayofyear: Int, year: Int): Intent {
            val intent = Intent(context, ReceiverBroad::class.java)
            intent.action = action
            intent.putExtra("extra", extra)
            intent.putExtra("dayofyear", dayofyear)
            intent.putExtra("year", year)
            return intent
        }

        private fun mkTime(year: Int, month: Int, day: Int): Long {
            val calendar = Calendar.getInstance()
            calendar[year, month, day, 0, 0] = 0
            return calendar.timeInMillis
        }

        private fun createIntentSabytie(context: Context, action: String, data: String, time: String): Intent {
            val intent = Intent(context, ReceiverBroad::class.java)
            intent.action = action
            intent.putExtra("sabytieSet", true)
            intent.putExtra("extra", "Падзея $data у $time")
            val dateN = data.split(".")
            val g = GregorianCalendar(dateN[2].toInt(), dateN[1].toInt() - 1, dateN[0].toInt(), 0, 0, 0)
            intent.putExtra("data", g[Calendar.DAY_OF_YEAR])
            intent.putExtra("year", g[Calendar.YEAR])
            return intent
        }

        fun setNotifications(context: Context, notifications: Int) {
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            var intent: Intent
            var pIntent: PendingIntent?
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (chin.getBoolean("WIDGET_MUN_ENABLED", false)) {
                val cw = Calendar.getInstance() as GregorianCalendar
                val munAk = cw[Calendar.MONTH]
                val yearAk = cw[Calendar.YEAR]
                var resetWid = false
                intent = Intent(context, WidgetMun::class.java)
                intent.action = UPDATE_ALL_WIDGETS
                pIntent = PendingIntent.getBroadcast(context, 51, intent, PendingIntent.FLAG_NO_CREATE)
                if (pIntent != null) {
                    cw.add(Calendar.DATE, 1)
                }
                pIntent = PendingIntent.getBroadcast(context, 51, intent, 0)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        am.setExact(AlarmManager.RTC_WAKEUP, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    else -> {
                        am[AlarmManager.RTC_WAKEUP, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH])] = pIntent
                    }
                }
                val thisAppWidget = ComponentName(context.packageName, context.packageName + ".Widget_mun")
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
                for (i in ids) {
                    val munS = chin.getInt("WIDGET$i", munAk)
                    val yearS = chin.getInt("WIDGETYEAR$i", yearAk)
                    if (!(munS == munAk && yearS == yearAk)) resetWid = true
                }
                if (resetWid) {
                    val reset = Intent(context, WidgetMun::class.java)
                    reset.action = RESET_MAIN
                    val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_UPDATE_CURRENT)
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000L, pReset)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000L, pReset)
                        }
                        else -> {
                            am[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000L] = pReset
                        }
                    }
                }
            }
            if (chin.getBoolean("WIDGET_ENABLED", false)) {
                val cw = Calendar.getInstance() as GregorianCalendar
                intent = Intent(context, Widget::class.java)
                intent.action = UPDATE_ALL_WIDGETS
                pIntent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_NO_CREATE)
                if (pIntent != null) {
                    cw.add(Calendar.DATE, 1)
                }
                pIntent = PendingIntent.getBroadcast(context, 50, intent, 0)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        am.setExact(AlarmManager.RTC_WAKEUP, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    else -> {
                        am[AlarmManager.RTC_WAKEUP, mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH])] = pIntent
                    }
                }
            }
            val c = Calendar.getInstance() as GregorianCalendar
            MainActivity.padzeia.forEach {
                if (it.sec != "-1") {
                    if (it.count == "0") {
                        when (it.repit) {
                            1 -> {
                                var timerepit = it.paznic
                                while (true) {
                                    if (timerepit > c.timeInMillis) {
                                        intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                                        pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 86400000L, pIntent)
                                        break
                                    }
                                    timerepit += 86400000L
                                }
                            }
                            4 -> {
                                var timerepit = it.paznic
                                while (true) {
                                    if (timerepit > c.timeInMillis) {
                                        intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                                        pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 604800000L, pIntent)
                                        break
                                    }
                                    timerepit += 604800000L
                                }
                            }
                            5 -> {
                                var timerepit = it.paznic
                                while (true) {
                                    if (timerepit > c.timeInMillis) {
                                        intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                                        pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 1209600000L, pIntent)
                                        break
                                    }
                                    timerepit += 1209600000L
                                }
                            }
                            6 -> {
                                var timerepit = it.paznic
                                while (true) {
                                    if (timerepit > c.timeInMillis) {
                                        intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                                        pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
                                        am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 2419200000L, pIntent)
                                        break
                                    }
                                    timerepit += 2419200000L
                                }
                            }
                            else -> if (it.paznic > c.timeInMillis) {
                                intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                                pIntent = PendingIntent.getBroadcast(context, (it.paznic / 100000).toInt(), intent, 0)
                                when {
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, it.paznic, pIntent)
                                    }
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                        am.setExact(AlarmManager.RTC_WAKEUP, it.paznic, pIntent)
                                    }
                                    else -> {
                                        am[AlarmManager.RTC_WAKEUP, it.paznic] = pIntent
                                    }
                                }
                            }
                        }
                    } else {
                        if (it.paznic > c.timeInMillis) {
                            intent = createIntentSabytie(context, it.padz, it.dat, it.tim)
                            pIntent = PendingIntent.getBroadcast(context, (it.paznic / 100000).toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, it.paznic, pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, it.paznic, pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, it.paznic] = pIntent
                                }
                            }
                        }
                    }
                }
            }
            var year = c[Calendar.YEAR]
            var dataP: Int
            var monthP: Int
            val timeNotification = chin.getInt("timeNotification", 8)
            for (i in 0..1) {
                year += i
                val a = year % 19
                val b = year % 4
                val cx = year % 7
                val k = year / 100
                val p = (13 + 8 * k) / 25
                val q = k / 4
                val m = (15 - p + k - q) % 30
                val n = (4 + k - q) % 7
                val d = (19 * a + m) % 30
                val ex = (2 * b + 4 * cx + 6 * d + n) % 7
                if (d + ex <= 9) {
                    dataP = d + ex + 22
                    monthP = 3
                } else {
                    dataP = d + ex - 9
                    if (d == 29 && ex == 6) dataP = 19
                    if (d == 28 && ex == 6) dataP = 18
                    monthP = 4
                }
                if (notifications != 0) {
                    if (c.timeInMillis < mkTime(year, monthP - 1, dataP - 1, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, monthP - 1, dataP), mkTimeYear(year, monthP, dataP - 1)) // Абавязковае
                        val code = "1$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, monthP - 1, dataP - 1, 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, monthP - 1, dataP - 1, 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, monthP - 1, dataP - 1, 19)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, monthP - 1, dataP, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "2$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, monthP - 1, dataP, timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, monthP - 1, dataP, timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, monthP - 1, dataP, timeNotification)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 0, 5, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 0, 6), mkTimeYear(year, 0, 6)) // Абавязковае
                        val code = "3$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 0, 5, 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 0, 5, 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 0, 5, 19)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 0, 6, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "4$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 0, 6, timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 0, 6, timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 0, 6, timeNotification)] = pIntent
                            }
                        }
                    }
                    val cet = Calendar.getInstance()
                    cet[year, monthP - 1] = dataP - 1
                    cet.add(Calendar.DATE, -7)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1), mkTimeYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)) // Абавязковае
                        val code = "5$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)] = pIntent
                            }
                        }
                    }
                    cet.add(Calendar.DATE, 1)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "6$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)] = pIntent
                            }
                        }
                    }
                    cet[year, monthP - 1] = dataP - 1
                    cet.add(Calendar.DATE, +39)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1), mkTimeYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)) // Абавязковае
                        val code = "7$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)] = pIntent
                            }
                        }
                    }
                    cet.add(Calendar.DATE, 1)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "8$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)] = pIntent
                            }
                        }
                    }
                    cet[year, monthP - 1] = dataP - 1
                    cet.add(Calendar.DATE, +49)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1), mkTimeYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)) // Абавязковае
                        val code = "9$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)] = pIntent
                            }
                        }
                    }
                    cet.add(Calendar.DATE, 1)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "10$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 2, 24, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 2, 25), mkTimeYear(year, 2, 25)) // Абавязковае
                        val code = "11$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 2, 24, 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 2, 24, 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 2, 24, 19)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 2, 25, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "12$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 2, 25, timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 2, 25, timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 2, 25, timeNotification)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 7, 14, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 7, 15), mkTimeYear(year, 7, 15)) // Абавязковае
                        val code = "13$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 14, 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 14, 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 7, 14, 19)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 7, 15, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "14$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 15, timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 15, timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 7, 15, timeNotification)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 11, 24, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 11, 25), mkTimeYear(year, 11, 25)) // Абавязковае
                        val code = "15$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 11, 24, 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 11, 24, 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 11, 24, 19)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 11, 25, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "16$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 11, 25, timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 11, 25, timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 11, 25, timeNotification)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 5, 28, 19)) {
                        intent = createIntent(context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 5, 29), mkTimeYear(year, 5, 29)) // Абавязковае
                        val code = "17$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 28, 19), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 28, 19), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 5, 28, 19)] = pIntent
                            }
                        }
                    }
                    if (c.timeInMillis < mkTime(year, 5, 29, timeNotification)) {
                        intent = createIntent(context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "18$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 29, timeNotification), pIntent)
                            }
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 29, timeNotification), pIntent)
                            }
                            else -> {
                                am[AlarmManager.RTC_WAKEUP, mkTime(year, 5, 29, timeNotification)] = pIntent
                            }
                        }
                    }
                    if (notifications == 2) {
                        if (c.timeInMillis < mkTime(year, 1, 1, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 1, 2), mkTimeYear(year, 1, 2))
                            val code = "19$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 1, 1, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 1, 1, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 1, 1, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 1, 2, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv2))
                            val code = "20$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 1, 2, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 1, 2, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 1, 2, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 7, 5, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 7, 6), mkTimeYear(year, 7, 6))
                            val code = "21$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 5, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 5, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 7, 5, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 7, 6, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv2))
                            val code = "22$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 6, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 6, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 7, 6, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 8, 7, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 8, 8), mkTimeYear(year, 8, 8))
                            val code = "23$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 7, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 7, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 8, 7, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 8, 8, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv2))
                            val code = "24$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 8, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 8, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 8, 8, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 8, 13, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 8, 14), mkTimeYear(year, 8, 14))
                            val code = "25$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 13, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 13, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 8, 13, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 8, 14, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv2))
                            val code = "26$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 14, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 14, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 8, 14, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 10, 20, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 10, 21), mkTimeYear(year, 10, 21))
                            val code = "27$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 10, 20, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 10, 20, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 10, 20, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 10, 21, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv2))
                            val code = "28$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 10, 21, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 10, 21, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 10, 21, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 11, 31, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year + 1, 0, 1), mkTimeYear(year + 1, 0, 1))
                            val code = "29$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 11, 31, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 11, 31, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 11, 31, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 0, 1, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv2))
                            val code = "30$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 0, 1, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 0, 1, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 0, 1, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 5, 23, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 5, 24), mkTimeYear(year, 5, 24))
                            val code = "31$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 23, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 23, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 5, 23, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 5, 24, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv2))
                            val code = "32$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 24, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 5, 24, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 5, 24, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 7, 28, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 7, 29), mkTimeYear(year, 7, 29))
                            val code = "33$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 28, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 28, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 7, 28, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 7, 29, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv2))
                            val code = "34$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 29, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 7, 29, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 7, 29, timeNotification)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 8, 30, 19)) {
                            intent = createIntent(context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 9, 1), mkTimeYear(year, 9, 1))
                            val code = "35$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 30, 19), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 8, 30, 19), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 8, 30, 19)] = pIntent
                                }
                            }
                        }
                        if (c.timeInMillis < mkTime(year, 9, 1, timeNotification)) {
                            intent = createIntent(context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv2))
                            val code = "36$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                            when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(year, 9, 1, timeNotification), pIntent)
                                }
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                                    am.setExact(AlarmManager.RTC_WAKEUP, mkTime(year, 9, 1, timeNotification), pIntent)
                                }
                                else -> {
                                    am[AlarmManager.RTC_WAKEUP, mkTime(year, 9, 1, timeNotification)] = pIntent
                                }
                            }
                        }
                    }
                }
                if (notifications == 1 || notifications == 0) {
                    var code: String
                    if (notifications != 1) {
                        intent = createIntent(context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "1$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "2$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "3$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "4$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "5$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "6$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "7$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "8$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "9$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "10$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "11$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "12$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "13$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "14$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "15$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "16$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "17$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                        intent = createIntent(context, context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "18$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                        am.cancel(pIntent)
                    }
                    intent = createIntent(context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv1))
                    code = "19$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv2))
                    code = "20$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv1))
                    code = "21$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv2))
                    code = "22$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv1))
                    code = "23$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv2))
                    code = "24$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv1))
                    code = "25$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv2))
                    code = "26$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv1))
                    code = "27$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv2))
                    code = "28$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv1))
                    code = "29$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv2))
                    code = "30$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv1))
                    code = "31$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv2))
                    code = "32$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv1))
                    code = "33$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv2))
                    code = "34$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv1))
                    code = "35$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                    intent = createIntent(context, context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv2))
                    code = "36$year"
                    pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, 0)
                    am.cancel(pIntent)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun notificationChannel(context: Context, channelID: String = NOTIFICATION_CHANNEL_ID_SVIATY) {
            val name = if (channelID == NOTIFICATION_CHANNEL_ID_SVIATY) context.getString(R.string.sviaty)
            else context.getString(R.string.sabytie)
            val vibrate = longArrayOf(0, 1000, 700, 1000, 700, 1000)
            val channel = NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_HIGH)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.description = name
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.enableLights(true)
            channel.lightColor = Color.RED
            val att = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att)
            channel.enableVibration(true)
            channel.vibrationPattern = vibrate
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            notificationManager.deleteNotificationChannel("by.carkva-gazeta")
            notificationManager.deleteNotificationChannel("3000")
            notificationManager.deleteNotificationChannel("2000")
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (edit)
            onSupportNavigateUp()
        else
            super.onBackPressed()
    }

    private fun formatFigureTwoPlaces(value: Float): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value.toDouble())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("edit", edit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        val notification = k.getInt("notification", 2)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefEditor = k.edit()
        val vibr = k.getInt("vibra", 1)
        if (dzenNoch) binding.vibro.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.vibro.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (vibr == 0) binding.vibro.isChecked = false
        val guk = k.getInt("guk", 1)
        if (dzenNoch) binding.guk.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.guk.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (guk == 0) binding.guk.isChecked = false
        if (k.getInt("notification", 2) == 0) binding.spinnerTime.visibility = View.GONE
        if (savedInstanceState != null) {
            edit = savedInstanceState.getBoolean("edit", false)
        }
        val dataTimes = ArrayList<DataTime>()
        for (i in 6..17) {
            dataTimes.add(DataTime(getString(R.string.pavedamic, i), i))
        }
        for (time in dataTimes) {
            if (time.data == k.getInt("timeNotification", 8)) break
            itemDefault++
        }
        binding.spinnerTime.adapter = TimeAdapter(this, dataTimes)
        binding.spinnerTime.setSelection(itemDefault)
        binding.spinnerTime.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                if (itemDefault != i) {
                    prefEditor.putInt("timeNotification", dataTimes[i].data)
                    prefEditor.apply()
                    itemDefault = i
                    binding.spinnerTime.isEnabled = false
                    CoroutineScope(Dispatchers.IO).launch {
                        setNotifications(this@SettingsActivity, notification)
                        withContext(Dispatchers.Main) {
                            binding.spinnerTime.isEnabled = true
                        }
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val autoPrag = ArrayList<String>()
        for (i in 0..15) {
            autoPrag.add(getString(R.string.autoprag_time, i + 5))
        }
        binding.spinnerAutoPrag.adapter = AutoPragortkaAdapter(this, autoPrag)
        binding.spinnerAutoPrag.setSelection(k.getInt("autoscrollAutostartTime", 5))
        binding.spinnerAutoPrag.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                prefEditor.putInt("autoscrollAutostartTime", p2)
                prefEditor.apply()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.vibro.visibility = View.GONE
            binding.guk.visibility = View.GONE
            notificationChannel(this)
            notificationChannel(this, channelID = NOTIFICATION_CHANNEL_ID_SABYTIE)
            if (k.getInt("notification", 2) > 0) binding.notifiSvizta.visibility = View.VISIBLE
            binding.notifiSvizta.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            binding.notifiSvizta.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                try {
                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID_SVIATY)
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    try {
                        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        MainActivity.toastView(this, getString(R.string.error_ch))
                    }
                }
            }
        }
        binding.textView14.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.textView15.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.textView16.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.notificationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (dzenNoch) {
            binding.textView14.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.textView15.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.textView16.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.notificationView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            //textView57.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.secret.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.line.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.line1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.line2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.line3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.line4.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
        }
        binding.textView16.setOnClickListener {
            if (SystemClock.elapsedRealtime() - adminClickTime < 2000) {
                adminItemCount++
            } else {
                adminItemCount = 1
            }
            adminClickTime = SystemClock.elapsedRealtime()
            if (adminItemCount == 7) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val checkLogin = CheckLogin()
                    checkLogin.isCancelable = false
                    checkLogin.show(supportFragmentManager, "checkLogin")
                }
            }
        }
        binding.cheshe.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        val dir = File("$filesDir/icons/")
        var sizeFiles = 0F
        if (dir.exists()) {
            val list = dir.listFiles()
            list?.forEach {
                sizeFiles += it.length()
            }
        }
        if (sizeFiles / 1024 > 1000) {
            val size = formatFigureTwoPlaces(sizeFiles / 1024 / 1024)
            binding.cheshe.text = getString(R.string.remove_cashe, size, "Мб")
        } else {
            val size = formatFigureTwoPlaces(sizeFiles / 1024)
            binding.cheshe.text = getString(R.string.remove_cashe, size, "Кб")
        }
        binding.cheshe.setOnClickListener {
            if (dir.exists())
                dir.deleteRecursively()
            binding.cheshe.text = getString(R.string.remove_cashe, "0,00", "Кб")
        }

        if (k.getBoolean("admin", false)) {
            binding.admin.visibility = View.VISIBLE
        }
        binding.admin.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.admin.setOnClickListener {
            if (MainActivity.checkmodulesAdmin(this)) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.ADMINMAIN)
                startActivity(intent)
            } else {
                MainActivity.moduleName = "admin"
                MainActivity.downloadDynamicModule(this)
            }
        }
        if (dzenNoch) binding.prav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.prav.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.secret.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.prav.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            if (isChecked) {
                prefEditor.putInt("pravas", 1)
            } else {
                prefEditor.putInt("pravas", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) binding.pkc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.pkc.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.pkc.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            if (isChecked) {
                prefEditor.putInt("pkc", 1)
            } else {
                prefEditor.putInt("pkc", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) binding.dzair.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.dzair.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.dzair.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            if (isChecked) {
                prefEditor.putInt("gosud", 1)
            } else {
                prefEditor.putInt("gosud", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) binding.praf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.praf.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.praf.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            if (isChecked) {
                prefEditor.putInt("pafesii", 1)
            } else {
                prefEditor.putInt("pafesii", 0)
            }
            prefEditor.apply()
        }
        if (k.getInt("pkc", 0) == 1) binding.pkc.isChecked = true
        if (k.getInt("pravas", 0) == 1) binding.prav.isChecked = true
        if (k.getInt("gosud", 0) == 1) binding.dzair.isChecked = true
        if (k.getInt("pafesii", 0) == 1) binding.praf.isChecked = true
        binding.maranataOpis.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.notificationOnly.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.notificationFull.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.notificationNon.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (Build.MANUFACTURER.contains("huawei", true)) {
            binding.helpNotifi.visibility = View.VISIBLE
            binding.helpNotifi.textSize = GET_FONT_SIZE_MIN
            binding.helpNotifi.setOnClickListener {
                val notifi = DialogHelpNotification()
                notifi.show(supportFragmentManager, "help_notification")
            }
        }
        binding.notificationOnly.isChecked = notification == 1
        binding.notificationFull.isChecked = notification == 2
        binding.notificationNon.isChecked = notification == 0
        val sinoidal = k.getInt("sinoidal", 0)
        if (dzenNoch) binding.sinoidal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.sinoidal.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (sinoidal == 1) binding.sinoidal.isChecked = true
        val maranata = k.getInt("maranata", 0)
        if (dzenNoch) binding.maranata.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.maranata.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (maranata == 1) {
            binding.maranata.isChecked = true
        }
        val dzenNochSettings = k.getBoolean("dzen_noch", false)
        if (dzenNoch) binding.checkBox5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.checkBox5.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (dzenNochSettings) binding.checkBox5.isChecked = true
        val autoscrollAutostart = k.getBoolean("autoscrollAutostart", false)
        if (dzenNoch) binding.checkBox6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.checkBox6.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (autoscrollAutostart) {
            binding.checkBox6.isChecked = true
        } else {
            binding.spinnerAutoPrag.visibility = View.GONE
        }
        val scrinOn = k.getBoolean("scrinOn", false)
        if (dzenNoch) binding.checkBox7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.checkBox7.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (scrinOn) {
            binding.checkBox7.isChecked = true
        }
        if (dzenNoch) binding.reset.setBackgroundResource(R.drawable.knopka_red_black)
        binding.reset.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        binding.reset.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val id = k.getInt("id", R.id.label1)
            val noDelite = ArrayList<String>()
            noDelite.add("WIDGET")
            noDelite.add("history")
            noDelite.add("Vybranoe")
            noDelite.add("help_")
            noDelite.add("FullscreenHelp")
            noDelite.add("bible_time")
            for ((key) in k.all) {
                var del = true
                for (i in 0 until noDelite.size) {
                    if (key.contains(noDelite[i], true)) {
                        del = false
                        break
                    }
                }
                if (del) prefEditor.remove(key)
            }
            File("$filesDir/Book").deleteRecursively()
            MainActivity.toastView(this, getString(R.string.save))
            prefEditor.putInt("id", id)
            prefEditor.putFloat("font_biblia", GET_DEFAULT_FONT_SIZE)
            prefEditor.putBoolean("dzen_noch", false)
            prefEditor.putInt("pravas", 0)
            prefEditor.putInt("pkc", 0)
            prefEditor.putInt("nedelia", 0)
            prefEditor.putInt("gosud", 0)
            prefEditor.putInt("pafesii", 0)
            prefEditor.putBoolean("belarus", false)
            prefEditor.putInt("notification", 2)
            prefEditor.putInt("power", 1)
            prefEditor.putInt("vibra", 1)
            prefEditor.putInt("guk", 1)
            prefEditor.putInt("sinoidal", 0)
            prefEditor.putInt("maranata", 0)
            prefEditor.putInt("soundnotification", 0)
            prefEditor.putInt("timeNotification", 8)
            prefEditor.putString("search_string", "")
            prefEditor.putString("search_string_filter", "")
            prefEditor.putInt("biblia_seash", 0)
            prefEditor.putBoolean("pegistrbukv", true)
            prefEditor.putInt("slovocalkam", 0)
            prefEditor.putInt("trafic", 0)
            prefEditor.putBoolean("admin", false)
            prefEditor.apply()
            binding.vibro.isClickable = true
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.guk.isClickable = true
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.checkBox5.isChecked = false
            binding.checkBox6.isChecked = false
            binding.checkBox7.isChecked = false
            binding.maranata.isChecked = false
            binding.sinoidal.isChecked = false
            binding.notificationOnly.isChecked = false
            binding.notificationFull.isChecked = true
            binding.notificationNon.isChecked = false
            binding.vibro.isChecked = true
            binding.guk.isChecked = true
            binding.spinnerTime.setSelection(2)
            binding.spinnerAutoPrag.setSelection(5)
            binding.pkc.isChecked = false
            binding.prav.isChecked = false
            binding.dzair.isChecked = false
            binding.praf.isChecked = false
            recreate()
        }
        binding.notificationGrup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.notificationOnly -> {
                    binding.notifiSvizta.visibility = View.VISIBLE
                    binding.spinnerTime.visibility = View.VISIBLE
                    if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                    prefEditor.putInt("notification", 1)
                    if (dzenNoch) binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) else binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    binding.guk.isClickable = true
                    if (dzenNoch) binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) else binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    CoroutineScope(Dispatchers.Main).launch {
                        if (dzenNoch) {
                            binding.vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                            binding.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                        } else {
                            binding.vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            binding.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                        binding.notificationNon.isClickable = false
                        binding.notificationFull.isClickable = false
                        binding.notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        binding.notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        withContext(Dispatchers.IO) {
                            setNotifications(this@SettingsActivity, 1)
                        }
                        binding.notificationNon.isClickable = true
                        binding.notificationFull.isClickable = true
                        if (dzenNoch) {
                            binding.notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                            binding.notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                        } else {
                            binding.notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            binding.notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                    }
                }
                R.id.notificationFull -> {
                    binding.notifiSvizta.visibility = View.VISIBLE
                    binding.spinnerTime.visibility = View.VISIBLE
                    if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                    prefEditor.putInt("notification", 2)
                    if (dzenNoch) binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) else binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    binding.guk.isClickable = true
                    if (dzenNoch) binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) else binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    CoroutineScope(Dispatchers.Main).launch {
                        if (dzenNoch) {
                            binding.vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                            binding.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                        } else {
                            binding.vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            binding.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                        binding.notificationOnly.isClickable = false
                        binding.notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        binding.notificationNon.isClickable = false
                        binding.notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        withContext(Dispatchers.IO) {
                            setNotifications(this@SettingsActivity, 2)
                        }
                        binding.notificationOnly.isClickable = true
                        binding.notificationNon.isClickable = true
                        if (dzenNoch) {
                            binding.notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                            binding.notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                        } else {
                            binding.notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            binding.notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                    }
                }
                R.id.notificationNon -> {
                    binding.notifiSvizta.visibility = View.GONE
                    binding.spinnerTime.visibility = View.GONE
                    prefEditor.putInt("notification", 0)
                    if (dzenNoch) binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) else binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    binding.guk.isClickable = true
                    if (dzenNoch) binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) else binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.notificationOnly.isClickable = false
                        binding.notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        binding.notificationFull.isClickable = false
                        binding.notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        withContext(Dispatchers.IO) {
                            setNotifications(this@SettingsActivity, 0)
                        }
                        binding.notificationOnly.isClickable = true
                        binding.notificationFull.isClickable = true
                        if (dzenNoch) {
                            binding.notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                            binding.notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorWhite))
                        } else {
                            binding.notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            binding.notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                    }
                }
            }
            prefEditor.apply()
        }
        binding.vibro.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("vibra", 1)
            } else {
                prefEditor.putInt("vibra", 0)
            }
            prefEditor.apply()
        }
        binding.sinoidal.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            if (isChecked) {
                prefEditor.putInt("sinoidal", 1)
            } else {
                prefEditor.putInt("sinoidal", 0)
            }
            prefEditor.apply()
        }
        binding.maranata.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            if (isChecked) {
                prefEditor.putInt("maranata", 1)
                if (dzenNoch) {
                    binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                } else {
                    binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                }
            } else {
                prefEditor.putInt("maranata", 0)
            }
            prefEditor.apply()
        }
        binding.guk.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("guk", 1)
            } else {
                prefEditor.putInt("guk", 0)
            }
            prefEditor.apply()
        }
        binding.checkBox5.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            prefEditor.putBoolean("dzen_noch", isChecked)
            prefEditor.apply()
            recreate()
        }
        binding.checkBox6.setOnCheckedChangeListener { _, isChecked: Boolean ->
            prefEditor.putBoolean("autoscrollAutostart", isChecked)
            if (isChecked) {
                binding.spinnerAutoPrag.visibility = View.VISIBLE
            } else {
                binding.spinnerAutoPrag.visibility = View.GONE
            }
            prefEditor.apply()
        }
        binding.checkBox7.setOnCheckedChangeListener { _, isChecked: Boolean ->
            edit = true
            prefEditor.putBoolean("scrinOn", isChecked)
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            prefEditor.apply()
        }
        binding.vibro.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.guk.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.sinoidal.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.maranata.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.prav.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.pkc.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.dzair.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.praf.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.checkBox5.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.checkBox6.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        binding.checkBox7.typeface = MainActivity.createFont(this, Typeface.NORMAL)
        if (savedInstanceState == null && (notification == 1 || notification == 2)) {
            if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
                val notifi = DialogHelpNotification()
                notifi.show(supportFragmentManager, "help_notification")
            }
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getText(R.string.tools_item)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onLogin() {
        prefEditor.putBoolean("admin", true)
        prefEditor.apply()
        binding.admin.visibility = View.VISIBLE
        if (!MainActivity.checkmodulesAdmin(this)) {
            MainActivity.moduleName = "admin"
            MainActivity.downloadDynamicModule(this)
        }
    }

    private class TimeAdapter(activity: Activity, private val dataTimes: ArrayList<DataTime>) : ArrayAdapter<DataTime>(activity, R.layout.simple_list_item_1, dataTimes) {
        private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val dzenNoch = k.getBoolean("dzen_noch", false)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            textView.text = dataTimes[position].string
            if (dzenNoch) textView.setBackgroundResource(R.drawable.selector_dark)
            else textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataTimes.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            viewHolder.text.text = dataTimes[position].string
            if (dzenNoch) viewHolder.text.setBackgroundResource(R.drawable.selector_dark)
            else viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

    }

    private class AutoPragortkaAdapter(activity: Activity, private val dataTimes: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_1, dataTimes) {
        private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val dzenNoch = k.getBoolean("dzen_noch", false)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            textView.text = dataTimes[position]
            if (dzenNoch) textView.setBackgroundResource(R.drawable.selector_dark)
            else textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataTimes.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            viewHolder.text.text = dataTimes[position]
            if (dzenNoch) viewHolder.text.setBackgroundResource(R.drawable.selector_dark)
            else viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    private class DataTime(val string: String, val data: Int)
}