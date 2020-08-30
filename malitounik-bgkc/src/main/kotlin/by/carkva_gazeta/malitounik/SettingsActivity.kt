package by.carkva_gazeta.malitounik

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.SharedPreferences.Editor
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 29.3.16
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var k: SharedPreferences
    private lateinit var prefEditor: Editor
    private var dzenNoch = false
    private var mLastClickTime: Long = 0
    private var itemDefault = 0

    companion object {
        private const val UPDATE_ALL_WIDGETS = "update_all_widgets"
        private const val RESET_MAIN = "reset_main"
        const val GET_DEFAULT_FONT_SIZE = 18F
        const val GET_FONT_SIZE_MIN = 14F
        const val GET_FONT_SIZE_MAX = 54F
        const val GET_FONT_SIZE_TOAST = 12F
        const val GET_CALIANDAR_YEAR_MIN = 2017
        const val GET_CALIANDAR_YEAR_MAX = 2021
        const val NOTIFICATION_CHANNEL_ID_SABYTIE = "3000"
        const val NOTIFICATION_CHANNEL_ID_SVIATY = "2000"

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
            val dateN = data.split(".").toTypedArray()
            val g = GregorianCalendar(dateN[2].toInt(), dateN[1].toInt() - 1, dateN[0].toInt(), 0, 0, 0)
            intent.putExtra("data", g[Calendar.DAY_OF_YEAR])
            intent.putExtra("year", g[Calendar.YEAR])
            return intent
        }

        fun setNotifications(context: Context, notifications: Int) {
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val c = Calendar.getInstance() as GregorianCalendar
            var intent: Intent
            var pIntent: PendingIntent
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (chin.getBoolean("WIDGET_MUN_ENABLED", false)) {
                val munAk = c[Calendar.MONTH]
                val yearAk = c[Calendar.YEAR]
                var resetWid = false
                intent = Intent(context, WidgetMun::class.java)
                intent.action = UPDATE_ALL_WIDGETS
                pIntent = PendingIntent.getBroadcast(context, 51, intent, 0)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        am.setExact(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    else -> {
                        am[AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])] = pIntent
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
                intent = Intent(context, Widget::class.java)
                intent.action = UPDATE_ALL_WIDGETS
                pIntent = PendingIntent.getBroadcast(context, 50, intent, 0)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        am.setExact(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                    }
                    else -> {
                        am[AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])] = pIntent
                    }
                }
            }
            /*File(context.filesDir.toString() + "/Sabytie").walk().forEach { file ->
            if (file.isFile && file.exists()) {
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            reader.forEachLine {
            val line = it
            if (line != "") {
            val t1 = line.split(" ").toTypedArray()
            if (t1[5] != "-1") {
            if (t1[10] == "0") {
            when (t1[9].toInt()) {
            1 -> {
            var timerepit = t1[3].toLong()
            while (true) {
            if (timerepit > c.timeInMillis) {
            intent = createIntentSabytie(context, t1[0].replace("_", " "), t1[1], t1[2])
            pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
            am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 86400000L, pIntent)
            break
            }
            timerepit += 86400000L
            }
            }
            4 -> {
            var timerepit = t1[3].toLong()
            while (true) {
            if (timerepit > c.timeInMillis) {
            intent = createIntentSabytie(context, t1[0].replace("_", " "), t1[1], t1[2])
            pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
            am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 604800000L, pIntent)
            break
            }
            timerepit += 604800000L
            }
            }
            5 -> {
            var timerepit = t1[3].toLong()
            while (true) {
            if (timerepit > c.timeInMillis) {
            intent = createIntentSabytie(context, t1[0].replace("_", " "), t1[1], t1[2])
            pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
            am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 1209600000L, pIntent)
            break
            }
            timerepit += 1209600000L
            }
            }
            6 -> {
            var timerepit = t1[3].toLong()
            while (true) {
            if (timerepit > c.timeInMillis) {
            intent = createIntentSabytie(context, t1[0].replace("_", " "), t1[1], t1[2])
            pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
            am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 2419200000L, pIntent)
            break
            }
            timerepit += 2419200000L
            }
            }
            else -> if (t1[3].toLong() > c.timeInMillis) {
            intent = createIntentSabytie(context, t1[0].replace("_", " "), t1[1], t1[2])
            pIntent = PendingIntent.getBroadcast(context, (t1[3].toLong() / 100000).toInt(), intent, 0)
            when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t1[3].toLong(), pIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            am.setExact(AlarmManager.RTC_WAKEUP, t1[3].toLong(), pIntent)
            }
            else -> {
            am[AlarmManager.RTC_WAKEUP, t1[3].toLong()] = pIntent
            }
            }
            }
            }
            } else {
            if (t1[3].toLong() > c.timeInMillis) {
            intent = createIntentSabytie(context, t1[0].replace("_", " "), t1[1], t1[2])
            pIntent = PendingIntent.getBroadcast(context, (t1[3].toLong() / 100000).toInt(), intent, 0)
            when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t1[3].toLong(), pIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            am.setExact(AlarmManager.RTC_WAKEUP, t1[3].toLong(), pIntent)
            }
            else -> {
            am[AlarmManager.RTC_WAKEUP, t1[3].toLong()] = pIntent
            }
            }
            }
            }
            }
            }
            }
            inputStream.close()
            }
            }*/
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
            val name = if (channelID == NOTIFICATION_CHANNEL_ID_SVIATY) context.getString(R.string.SVIATY)
            else context.getString(R.string.sabytie)
            val vibrate = longArrayOf(0, 1000, 700, 1000, 700, 1000)
            val channel = NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_HIGH)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.description = name
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.lightColor = ContextCompat.getColor(context, R.color.colorPrimary)
            val att = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att)
            channel.enableVibration(true)
            channel.vibrationPattern = vibrate
            channel.enableLights(true)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
            notificationManager?.deleteNotificationChannel("by.carkva-gazeta")
        }
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    /*private fun formatFigureTwoPlaces(value: Float): String {
    val myFormatter = DecimalFormat("##0.00")
    return myFormatter.format(value.toDouble())
    }*/

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
        setContentView(R.layout.settings_activity)
        prefEditor = k.edit()
        val vibr = k.getInt("vibra", 1)
        if (dzenNoch) vibro.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        vibro.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (vibr == 0) vibro.isChecked = false
        val guk = k.getInt("guk", 1)
        if (dzenNoch) this.guk.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        this.guk.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (guk == 0) this.guk.isChecked = false
        if (k.getInt("notification", 2) == 0) spinnerTime.visibility = View.GONE
        val dataTimes = ArrayList<DataTime>()
        for (i in 6..17) {
            dataTimes.add(DataTime(getString(R.string.pavedamic, i), i))
        }
        for (time in dataTimes) {
            if (time.data == k.getInt("timeNotification", 8)) break
            itemDefault++
        }
        spinnerTime.adapter = TimeAdapter(this, dataTimes)
        spinnerTime.setSelection(itemDefault)
        spinnerTime.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                if (itemDefault != i) {
                    prefEditor.putInt("timeNotification", dataTimes[i].data)
                    prefEditor.apply()
                    itemDefault = i
                    spinnerTime.isEnabled = false
                    CoroutineScope(Dispatchers.IO).launch {
                        setNotifications(this@SettingsActivity, notification)
                        withContext(Dispatchers.Main) {
                            spinnerTime.isEnabled = true
                        }
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibro.visibility = View.GONE
            this.guk.visibility = View.GONE
            notificationChannel(this)
            notificationChannel(this, channelID = NOTIFICATION_CHANNEL_ID_SABYTIE)
            if (k.getInt("notification", 2) > 0) notifiSvizta.visibility = View.VISIBLE
            notifiSvizta.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            notifiSvizta.setOnClickListener {
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
        textView14.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        textView15.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        notificationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        //textView57.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (dzenNoch) {
            textView14.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            textView15.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            notificationView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            //textView57.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            secret.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            line.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            line1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            line2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            line3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            //line4.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
        }
        /*var dirCount: Long = 0
        File("$filesDir/Site").walk().forEach {
        if (it.isFile)
        dirCount += it.length()
        }*/
        /*val dir = File("$filesDir/Site")
        val dirContents = dir.listFiles()
        for (dirContent in dirContents) {
        dirCount = dirCount + dirContent.length()
        }*/
        /*File dir2 = new File(getFilesDir() + "/image_temp");
        if (!dir2.exists()) {
        dir2.mkdir();
        }
        File[] dirContents2 = dir2.listFiles();
        for (File aDirContents2 : dirContents2) {
        dirCount = dirCount + aDirContents2.length();
        }*/
        if (dzenNoch) prav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        prav.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        secret.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        prav.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("pravas", 1)
            } else {
                prefEditor.putInt("pravas", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) pkc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        pkc.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        pkc.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("pkc", 1)
            } else {
                prefEditor.putInt("pkc", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) dzair.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        dzair.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        dzair.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("gosud", 1)
            } else {
                prefEditor.putInt("gosud", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) praf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        praf.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        praf.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("pafesii", 1)
            } else {
                prefEditor.putInt("pafesii", 0)
            }
            prefEditor.apply()
        }
        if (dzenNoch) {
            prav.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            dzair.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            praf.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            pkc.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        if (k.getInt("pkc", 0) == 1) pkc.isChecked = true
        if (k.getInt("pravas", 0) == 1) prav.isChecked = true
        if (k.getInt("gosud", 0) == 1) dzair.isChecked = true
        if (k.getInt("pafesii", 0) == 1) praf.isChecked = true
        maranataOpis.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        //button.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        notificationOnly.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        notificationFull.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        notificationNon.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        /*textView58.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (dirCount / 1024 > 1000) {
        textView58.text = resources.getString(R.string.QUOTA_M, formatFigureTwoPlaces(BigDecimal.valueOf(dirCount.toFloat() / 1024 / 1024.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()))
        } else {
        textView58.text = resources.getString(R.string.QUOTA, formatFigureTwoPlaces(BigDecimal.valueOf(dirCount.toFloat() / 1024.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()))
        }*/
        if (Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
            val helpNotifi: TextViewRobotoCondensed = findViewById(R.id.help_notifi)
            helpNotifi.visibility = View.VISIBLE
            helpNotifi.textSize = GET_FONT_SIZE_MIN
            helpNotifi.setOnClickListener {
                val notifi = DialogHelpNotification()
                notifi.show(supportFragmentManager, "help_notification")
            }
        }
        maranataBel.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        maranataRus.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        val belarus = k.getBoolean("belarus", false)
        if (belarus) {
            maranataBel.isChecked = true
            maranataRus.isChecked = false
        } else {
            maranataRus.isChecked = true
            maranataBel.isChecked = false
        }
        notificationOnly.isChecked = notification == 1
        notificationFull.isChecked = notification == 2
        notificationNon.isChecked = notification == 0
        val sinoidal = k.getInt("sinoidal", 0)
        if (dzenNoch) this.sinoidal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        this.sinoidal.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (sinoidal == 1) this.sinoidal.isChecked = true
        val maranata = k.getInt("maranata", 0)
        if (dzenNoch) this.maranata.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        this.maranata.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (maranata == 1) {
            this.maranata.isChecked = true
        } else {
            maranataBel.isClickable = false
            maranataRus.isClickable = false
            maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
        }
        val dzenNochSettings = k.getBoolean("dzen_noch", false)
        if (dzenNoch) checkBox5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        checkBox5.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (dzenNochSettings) checkBox5.isChecked = true
        /*val trafik = k.getInt("trafic", 0)
        if (dzenNoch) checkBox2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        checkBox2.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        if (trafik == 1) checkBox2.isChecked = true*/
        if (dzenNoch) reset.setBackgroundResource(R.drawable.knopka_red_black)
        reset.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
        reset.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val id = k.getInt("id", R.id.label1)
            k.all.forEach {
                if (!(it.key.contains("WIDGET", ignoreCase = true) || it.key.contains("history", ignoreCase = true))) prefEditor.remove(it.key)
            }
            File("$filesDir/Book").deleteRecursively()
            MainActivity.toastView(this, getString(R.string.save))
            prefEditor.putInt("id", id)
            prefEditor.putBoolean("help_str", true)
            prefEditor.putFloat("font_biblia", GET_DEFAULT_FONT_SIZE)
            prefEditor.putBoolean("dzen_noch", false)
            prefEditor.putBoolean("FullscreenHelp", true)
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
            maranataBel.isClickable = false
            maranataRus.isClickable = false
            maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            prefEditor.putInt("trafic", 0)
            prefEditor.apply()
            vibro.isClickable = true
            vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            this.guk.isClickable = true
            this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            checkBox5.isChecked = false
            this.maranata.isChecked = false
            maranataRus.isChecked = true
            maranataBel.isChecked = false
            this.sinoidal.isChecked = false
            notificationOnly.isChecked = false
            notificationFull.isChecked = true
            notificationNon.isChecked = false
            vibro.isChecked = true
            this.guk.isChecked = true
            //checkBox2.isChecked = false
            spinnerTime.setSelection(2)
            pkc.isChecked = false
            prav.isChecked = false
            dzair.isChecked = false
            praf.isChecked = false
            recreate()
        }
        if (dzenNoch) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary_text)
            }
            scrollView.setBackgroundResource(R.color.colorbackground_material_dark)
            notificationOnly.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            notificationFull.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            notificationNon.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            //textView58.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            checkBox5.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            this.sinoidal.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            this.maranata.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            if (maranata != 0) {
                maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
                maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
                maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            }
            //checkBox2.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        maranataGrup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.maranataBel -> {
                    prefEditor.putBoolean("belarus", true)
                    val semuxaNoKnigi = DialogSemuxaNoKnigi()
                    semuxaNoKnigi.show(supportFragmentManager, "semuxa_no_knigi")
                }
                R.id.maranataRus -> prefEditor.putBoolean("belarus", false)
                else -> {
                }
            }
            prefEditor.apply()
        }
        notificationGrup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.notificationOnly -> {
                    notifiSvizta.visibility = View.VISIBLE
                    spinnerTime.visibility = View.VISIBLE
                    if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                    prefEditor.putInt("notification", 1)
                    if (dzenNoch) vibro.setTextColor(ContextCompat.getColor(this, R.color.colorIcons)) else vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    this.guk.isClickable = true
                    if (dzenNoch) this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorIcons)) else this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    CoroutineScope(Dispatchers.Main).launch {
                        if (dzenNoch) {
                            vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                            this@SettingsActivity.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                        } else {
                            vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            this@SettingsActivity.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                        notificationNon.isClickable = false
                        notificationFull.isClickable = false
                        notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        withContext(Dispatchers.IO) {
                            setNotifications(this@SettingsActivity, 1)
                        }
                        notificationNon.isClickable = true
                        notificationFull.isClickable = true
                        if (dzenNoch) {
                            notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                            notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                        } else {
                            notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                    }
                }
                R.id.notificationFull -> {
                    notifiSvizta.visibility = View.VISIBLE
                    spinnerTime.visibility = View.VISIBLE
                    if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
                        val notifi = DialogHelpNotification()
                        notifi.show(supportFragmentManager, "help_notification")
                    }
                    prefEditor.putInt("notification", 2)
                    if (dzenNoch) vibro.setTextColor(ContextCompat.getColor(this, R.color.colorIcons)) else vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    this.guk.isClickable = true
                    if (dzenNoch) this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorIcons)) else this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    CoroutineScope(Dispatchers.Main).launch {
                        if (dzenNoch) {
                            vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                            this@SettingsActivity.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                        } else {
                            vibro.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            this@SettingsActivity.guk.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                        notificationOnly.isClickable = false
                        notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        notificationNon.isClickable = false
                        notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        withContext(Dispatchers.IO) {
                            setNotifications(this@SettingsActivity, 2)
                        }
                        notificationOnly.isClickable = true
                        notificationNon.isClickable = true
                        if (dzenNoch) {
                            notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                            notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                        } else {
                            notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            notificationNon.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                    }
                }
                R.id.notificationNon -> {
                    notifiSvizta.visibility = View.GONE
                    spinnerTime.visibility = View.GONE
                    prefEditor.putInt("notification", 0)
                    if (dzenNoch) vibro.setTextColor(ContextCompat.getColor(this, R.color.colorIcons)) else vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    this.guk.isClickable = true
                    if (dzenNoch) this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorIcons)) else this.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    CoroutineScope(Dispatchers.Main).launch {
                        notificationOnly.isClickable = false
                        notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        notificationFull.isClickable = false
                        notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorSecondary_text))
                        withContext(Dispatchers.IO) {
                            setNotifications(this@SettingsActivity, 0)
                        }
                        notificationOnly.isClickable = true
                        notificationFull.isClickable = true
                        if (dzenNoch) {
                            notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                            notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorIcons))
                        } else {
                            notificationOnly.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                            notificationFull.setTextColor(ContextCompat.getColor(this@SettingsActivity, R.color.colorPrimary_text))
                        }
                    }
                }
            }
            prefEditor.apply()
        }
        vibro.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("vibra", 1)
            } else {
                prefEditor.putInt("vibra", 0)
            }
            prefEditor.apply()
        }
        this.sinoidal.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("sinoidal", 1)
            } else {
                prefEditor.putInt("sinoidal", 0)
            }
            prefEditor.apply()
        }
        this.maranata.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("maranata", 1)
                maranataBel.isClickable = true
                maranataRus.isClickable = true
                if (dzenNoch) {
                    maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
                    maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
                    maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
                } else {
                    maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                }
            } else {
                prefEditor.putInt("maranata", 0)
                maranataBel.isClickable = false
                maranataRus.isClickable = false
                maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
                maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
                maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            }
            prefEditor.apply()
        }
        this.guk.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("guk", 1)
            } else {
                prefEditor.putInt("guk", 0)
            }
            prefEditor.apply()
        }
        /*checkBox2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
        if (isChecked) {
        prefEditor.putInt("trafic", 1)
        } else {
        prefEditor.putInt("trafic", 0)
        }
        prefEditor.apply()
        }*/
        checkBox5.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        /*button.setOnClickListener {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        return@setOnClickListener
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        /*File("$filesDir/Site").walk().forEach {
        if (it.isFile)
        it.delete()
        }*/
        /*for (aDirContents1 in dirContents) {
        aDirContents1.delete()
        }*/
        /*for (File aDirContents2 : dirContents2) {
        aDirContents2.delete();
        }*/
        textView58.text = resources.getString(R.string.QUOTA, formatFigureTwoPlaces(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP).toFloat()))
        }*/
        vibro.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        this.guk.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        this.sinoidal.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        this.maranata.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        //checkBox2.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        prav.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        pkc.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        dzair.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        praf.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        checkBox5.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
        if (savedInstanceState == null && (notification == 1 || notification == 2)) {
            if (k.getBoolean("check_notifi", true) && Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
                val notifi = DialogHelpNotification()
                notifi.show(supportFragmentManager, "help_notification")
            }
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.text = resources.getText(R.string.tools_item)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    private class TimeAdapter(private val activity: Activity, private val dataTimes: ArrayList<DataTime>) : ArrayAdapter<DataTime>(activity, R.layout.simple_list_item_1, dataTimes) {
        private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val dzenNoch = k.getBoolean("dzen_noch", false)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextViewRobotoCondensed
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            textView.text = dataTimes[position].string
            if (dzenNoch) {
                textView.setBackgroundResource(R.drawable.selector_dark)
                textView.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
            }
            return v
        }

        override fun getCount(): Int {
            return dataTimes.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_1, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.text1)
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN)
            viewHolder.text?.text = dataTimes[position].string
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
            }
            return rootView
        }

    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    private class DataTime(val string: String, val data: Int)
}