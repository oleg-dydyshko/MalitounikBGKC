package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.*
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.SettingsActivityBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.android.play.core.splitinstall.SplitInstallHelper
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class SettingsActivity : BaseActivity(), CheckLogin.CheckLoginListener, DialogHelpNotificationApi33.DialogHelpNotificationApi33Listener, BaseActivity.DownloadDynamicModuleListener {
    private lateinit var k: SharedPreferences
    private lateinit var prefEditor: Editor
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private lateinit var binding: SettingsActivityBinding
    private var resetTollbarJob: Job? = null
    private var adminResetJob: Job? = null
    private var setNotificationsJob: Job? = null
    private var adminClickTime: Long = 0
    private var adminItemCount = 0
    private var edit = false
    private var editFull = false
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            when (k.getInt("notification", NOTIFICATION_SVIATY_FULL)) {
                1 -> setNotificationOnly()
                2 -> setNotificationFull()
                0 -> setNotificationNon()
            }
            binding.pavedamic3.visibility = View.GONE
        } else {
            val prefEditor = k.edit()
            prefEditor.putBoolean("permissionNotificationApi33", false)
            prefEditor.apply()
        }
    }

    companion object {
        const val UPDATE_ALL_WIDGETS = "update_all_widgets"
        const val RESET_MAIN = "reset_main"
        const val GET_FONT_SIZE_DEFAULT = 18F
        const val GET_FONT_SIZE_MIN = 14F
        const val GET_FONT_SIZE_MAX = 54F
        const val GET_FONT_SIZE_TOAST = 12F
        const val GET_CALIANDAR_YEAR_MIN = 2021
        const val GET_CALIANDAR_YEAR_MAX = 2024
        const val NOTIFICATION_CHANNEL_ID_SABYTIE = "3003"
        const val NOTIFICATION_CHANNEL_ID_SVIATY = "2003"
        const val NOTIFICATION_CHANNEL_ID_RADIO_MARYIA = "4007"
        const val NOTIFICATION_SVIATY_NONE = 0
        const val NOTIFICATION_SVIATY_ONLY = 1
        const val NOTIFICATION_SVIATY_FULL = 2
        val vibrate = longArrayOf(0, 1000, 700, 1000)
        var isPadzeiaSetAlarm = false

        private fun mkTime(year: Int, month: Int, day: Int, hour: Int): Long {
            val calendar = Calendar.getInstance()
            calendar[year, month, day, hour, 0] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar.timeInMillis
        }

        private fun mkTimeDayOfYear(year: Int, month: Int, day: Int): Int {
            val calendar = Calendar.getInstance()
            calendar[year, month, day, 19, 0] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar[Calendar.DAY_OF_YEAR]
        }

        private fun mkTimeYear(year: Int, month: Int, day: Int): Int {
            val calendar = Calendar.getInstance()
            calendar[year, month, day, 19, 0] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar[Calendar.YEAR]
        }

        private fun createIntent(title: String, extra: String): Intent {
            val intent = Intent(Malitounik.applicationContext(), ReceiverBroad::class.java)
            intent.action = "by.carkva_gazeta.malitounik.sviaty"
            intent.putExtra("title", title)
            intent.putExtra("extra", extra)
            intent.`package` = Malitounik.applicationContext().packageName
            return intent
        }

        private fun createIntent(title: String, extra: String, dayofyear: Int, year: Int): Intent {
            val intent = Intent(Malitounik.applicationContext(), ReceiverBroad::class.java)
            intent.action = "by.carkva_gazeta.malitounik.sviaty"
            intent.putExtra("title", title)
            intent.putExtra("extra", extra)
            intent.putExtra("dayofyear", dayofyear)
            intent.putExtra("year", year)
            intent.`package` = Malitounik.applicationContext().packageName
            return intent
        }

        private fun mkTime(year: Int, month: Int, day: Int): Long {
            val calendar = Calendar.getInstance()
            calendar[year, month, day, 0, 0] = 0
            return calendar.timeInMillis
        }

        fun createIntentSabytie(title: String, data: String, time: String): Intent {
            val intent = Intent(Malitounik.applicationContext(), ReceiverBroad::class.java)
            intent.action = "by.carkva_gazeta.malitounik.sviaty"
            intent.putExtra("title", title)
            intent.putExtra("sabytieSet", true)
            intent.putExtra("extra", "Падзея $data у $time")
            val dateN = data.split(".")
            val g = GregorianCalendar(dateN[2].toInt(), dateN[1].toInt() - 1, dateN[0].toInt(), 0, 0, 0)
            intent.putExtra("year", g[Calendar.YEAR])
            val timeN = time.split(":")
            intent.putExtra("dataString", dateN[0] + dateN[1] + timeN[0] + timeN[1])
            intent.putExtra("dayofyear", g[Calendar.DAY_OF_YEAR])
            return intent
        }

        private fun setAlarm(timeAlarm: Long, pendingIntent: PendingIntent?, padzeia: Boolean = false) {
            pendingIntent?.let {
                val context = Malitounik.applicationContext()
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (padzeia && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms() -> {
                        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlarm, it)
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAlarm, it)
                    }

                    else -> {
                        am.setExact(AlarmManager.RTC_WAKEUP, timeAlarm, it)
                    }
                }
            }
        }

        fun setNotifications(notifications: Int) {
            val context = Malitounik.applicationContext()
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            var intent: Intent
            var pIntent: PendingIntent?
            if (chin.getBoolean("WIDGET_MUN_ENABLED", false)) {
                val cw = Calendar.getInstance()
                val munAk = cw[Calendar.MONTH]
                val yearAk = cw[Calendar.YEAR]
                var resetWid = false
                intent = Intent(context, WidgetMun::class.java)
                intent.action = UPDATE_ALL_WIDGETS
                var flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
                } else {
                    PendingIntent.FLAG_NO_CREATE
                }
                pIntent = PendingIntent.getBroadcast(context, 51, intent, flags)
                if (pIntent != null) {
                    cw.add(Calendar.DATE, 1)
                }
                flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or 0
                } else {
                    0
                }
                pIntent = PendingIntent.getBroadcast(context, 51, intent, flags)
                setAlarm(mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
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
                    flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                    val pReset = PendingIntent.getBroadcast(context, 257, reset, flags)
                    setAlarm(System.currentTimeMillis() + 120000L, pReset)
                }
            }
            if (chin.getBoolean("WIDGET_ENABLED", false)) {
                val cw = Calendar.getInstance()
                intent = Intent(context, Widget::class.java)
                intent.action = UPDATE_ALL_WIDGETS
                var flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
                } else {
                    PendingIntent.FLAG_NO_CREATE
                }
                pIntent = PendingIntent.getBroadcast(context, 50, intent, flags)
                if (pIntent != null) {
                    cw.add(Calendar.DATE, 1)
                }
                flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or 0
                } else {
                    0
                }
                pIntent = PendingIntent.getBroadcast(context, 50, intent, flags)
                setAlarm(mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
            }
            if (chin.getBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false)) {
                val cw = Calendar.getInstance()
                intent = Intent(context, WidgetRadyjoMaryia::class.java)
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or 0
                } else {
                    0
                }
                pIntent = PendingIntent.getBroadcast(context, 52, intent, flags)
                setAlarm(mkTime(cw[Calendar.YEAR], cw[Calendar.MONTH], cw[Calendar.DAY_OF_MONTH]), pIntent)
            }
            val c = Calendar.getInstance()
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or 0
            } else {
                0
            }
            isPadzeiaSetAlarm = true
            MainActivity.padzeia.forEach {
                if (it.sec != "-1") {
                    val timerepit = it.paznic
                    if (timerepit > c.timeInMillis) {
                        intent = createIntentSabytie(it.padz, it.dat, it.tim)
                        pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, flags)
                        setAlarm(timerepit, pIntent, true)
                    }
                }
            }
            isPadzeiaSetAlarm = false
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
                        intent = createIntent(context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, monthP - 1, dataP), mkTimeYear(year, monthP, dataP - 1)) // Абавязковае
                        val code = "1$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, monthP - 1, dataP - 1, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, monthP - 1, dataP, timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "2$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, monthP - 1, dataP, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 0, 5, 19)) {
                        intent = createIntent(context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 0, 6), mkTimeYear(year, 0, 6)) // Абавязковае
                        val code = "3$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 0, 5, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 0, 6, timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "4$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 0, 6, timeNotification), pIntent)
                    }
                    val cet = Calendar.getInstance()
                    cet[year, monthP - 1] = dataP - 1
                    cet.add(Calendar.DATE, -7)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)) {
                        intent = createIntent(context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1), mkTimeYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)) // Абавязковае
                        val code = "5$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                    }
                    cet.add(Calendar.DATE, 1)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "6$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                    }
                    cet[year, monthP - 1] = dataP - 1
                    cet.add(Calendar.DATE, +39)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)) {
                        intent = createIntent(context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1), mkTimeYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)) // Абавязковае
                        val code = "7$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                    }
                    cet.add(Calendar.DATE, 1)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "8$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                    }
                    cet[year, monthP - 1] = dataP - 1
                    cet.add(Calendar.DATE, +49)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19)) {
                        intent = createIntent(context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1), mkTimeYear(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH] + 1)) // Абавязковае
                        val code = "9$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], 19), pIntent)
                    }
                    cet.add(Calendar.DATE, 1)
                    if (c.timeInMillis < mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "10$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, cet[Calendar.MONTH], cet[Calendar.DAY_OF_MONTH], timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 2, 24, 19)) {
                        intent = createIntent(context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 2, 25), mkTimeYear(year, 2, 25)) // Абавязковае
                        val code = "11$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 2, 24, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 2, 25, timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "12$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 2, 25, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 7, 14, 19)) {
                        intent = createIntent(context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 7, 15), mkTimeYear(year, 7, 15)) // Абавязковае
                        val code = "13$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 7, 14, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 7, 15, timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "14$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 7, 15, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 11, 24, 19)) {
                        intent = createIntent(context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 11, 25), mkTimeYear(year, 11, 25)) // Абавязковае
                        val code = "15$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 11, 24, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 11, 25, timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "16$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 11, 25, timeNotification), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 5, 28, 19)) {
                        intent = createIntent(context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv3), mkTimeDayOfYear(year, 5, 29), mkTimeYear(year, 5, 29)) // Абавязковае
                        val code = "17$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 5, 28, 19), pIntent)
                    }
                    if (c.timeInMillis < mkTime(year, 5, 29, timeNotification)) {
                        intent = createIntent(context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv4)) // Абавязковае
                        val code = "18$year"
                        pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        setAlarm(mkTime(year, 5, 29, timeNotification), pIntent)
                    }
                    if (notifications == 2) {
                        if (c.timeInMillis < mkTime(year, 1, 1, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 1, 2), mkTimeYear(year, 1, 2))
                            val code = "19$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 1, 1, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 1, 2, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv2))
                            val code = "20$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 1, 2, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 7, 5, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 7, 6), mkTimeYear(year, 7, 6))
                            val code = "21$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 7, 5, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 7, 6, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv2))
                            val code = "22$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 7, 6, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 8, 7, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 8, 8), mkTimeYear(year, 8, 8))
                            val code = "23$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 8, 7, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 8, 8, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv2))
                            val code = "24$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 8, 8, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 8, 13, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 8, 14), mkTimeYear(year, 8, 14))
                            val code = "25$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 8, 13, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 8, 14, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv2))
                            val code = "26$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 8, 14, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 10, 20, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 10, 21), mkTimeYear(year, 10, 21))
                            val code = "27$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 10, 20, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 10, 21, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv2))
                            val code = "28$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 10, 21, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 11, 31, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year + 1, 0, 1), mkTimeYear(year + 1, 0, 1))
                            val code = "29$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 11, 31, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 0, 1, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv2))
                            val code = "30$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 0, 1, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 5, 23, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 5, 24), mkTimeYear(year, 5, 24))
                            val code = "31$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 5, 23, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 5, 24, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv2))
                            val code = "32$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 5, 24, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 7, 28, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 7, 29), mkTimeYear(year, 7, 29))
                            val code = "33$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 7, 28, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 7, 29, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv2))
                            val code = "34$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 7, 29, timeNotification), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 8, 30, 19)) {
                            intent = createIntent(context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv1), mkTimeDayOfYear(year, 9, 1), mkTimeYear(year, 9, 1))
                            val code = "35$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 8, 30, 19), pIntent)
                        }
                        if (c.timeInMillis < mkTime(year, 9, 1, timeNotification)) {
                            intent = createIntent(context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv2))
                            val code = "36$year"
                            pIntent = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                            setAlarm(mkTime(year, 9, 1, timeNotification), pIntent)
                        }
                    }
                }
                if (notifications == 1 || notifications == 0) {
                    var code: String
                    if (notifications != 1) {
                        intent = createIntent(context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "1$year"
                        var pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S1), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "2$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "3$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S2), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "4$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "5$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S5), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "6$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "7$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S6), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "8$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "9$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S7), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "10$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "11$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S4), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "12$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "13$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S9), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "14$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "15$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S13), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "16$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv1)) // Абавязковае
                        code = "17$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                        intent = createIntent(context.resources.getString(R.string.S16), context.resources.getString(R.string.Sv2)) // Абавязковае
                        code = "18$year"
                        pIntent1 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                        am.cancel(pIntent1)
                    }
                    intent = createIntent(context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv1))
                    code = "19$year"
                    var pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S3), context.resources.getString(R.string.Sv2))
                    code = "20$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv1))
                    code = "21$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S8), context.resources.getString(R.string.Sv2))
                    code = "22$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv1))
                    code = "23$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S10), context.resources.getString(R.string.Sv2))
                    code = "24$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv1))
                    code = "25$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S11), context.resources.getString(R.string.Sv2))
                    code = "26$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv1))
                    code = "27$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S12), context.resources.getString(R.string.Sv2))
                    code = "28$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv1))
                    code = "29$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S14), context.resources.getString(R.string.Sv2))
                    code = "30$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv1))
                    code = "31$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S15), context.resources.getString(R.string.Sv2))
                    code = "32$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv1))
                    code = "33$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S17), context.resources.getString(R.string.Sv2))
                    code = "34$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv1))
                    code = "35$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                    intent = createIntent(context.resources.getString(R.string.S18), context.resources.getString(R.string.Sv2))
                    code = "36$year"
                    pIntent2 = PendingIntent.getBroadcast(context, code.toInt(), intent, flags)
                    am.cancel(pIntent2)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun notificationChannel(context: Context, channelID: String = NOTIFICATION_CHANNEL_ID_SVIATY) {
            val name = if (channelID == NOTIFICATION_CHANNEL_ID_SVIATY) context.getString(R.string.sviaty)
            else context.getString(R.string.sabytie)
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
            notificationManager.deleteNotificationChannel("3001")
            notificationManager.deleteNotificationChannel("2001")
            notificationManager.deleteNotificationChannel("3002")
            notificationManager.deleteNotificationChannel("2002")
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        setNotificationsJob?.cancel()
        adminResetJob?.cancel()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    override fun onBack() {
        when {
            editFull -> setResult(300)
            edit -> setResult(200)
        }
        super.onBack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("edit", edit)
        outState.putBoolean("editFull", editFull)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        var notification = k.getInt("notification", NOTIFICATION_SVIATY_FULL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                notification = NOTIFICATION_SVIATY_NONE
            }
        }
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefEditor = k.edit()
        val vibr = k.getInt("vibra", 1)
        if (dzenNoch) binding.vibro.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (vibr == 0) binding.vibro.isChecked = false
        val guk = k.getInt("guk", 1)
        if (dzenNoch) binding.guk.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (guk == 0) binding.guk.isChecked = false
        if (notification == NOTIFICATION_SVIATY_NONE) binding.spinnerTime.visibility = View.GONE
        if (savedInstanceState != null) {
            edit = savedInstanceState.getBoolean("edit", false)
            editFull = savedInstanceState.getBoolean("editFull", false)
        }
        val dataTimes = ArrayList<DataTime>()
        for (i in 6..17) {
            dataTimes.add(DataTime(getString(R.string.pavedamic, i), i))
        }
        binding.spinnerTime.adapter = TimeAdapter(this, dataTimes)
        binding.spinnerTime.setSelection(k.getInt("timeNotification", 8) - 6)
        binding.spinnerTime.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                prefEditor.putInt("timeNotification", dataTimes[i].data)
                prefEditor.apply()
                when (notification) {
                    0 -> setNotificationNon()
                    1 -> setNotificationOnly()
                    2 -> setNotificationFull()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.vibro.visibility = View.GONE
            binding.guk.visibility = View.GONE
            notificationChannel(this)
            notificationChannel(this, NOTIFICATION_CHANNEL_ID_SABYTIE)
            val notifi = k.getInt("notification", NOTIFICATION_SVIATY_FULL)
            if (notifi == NOTIFICATION_SVIATY_ONLY || notifi == NOTIFICATION_SVIATY_FULL) binding.notifiSvizta.visibility = View.VISIBLE
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
                        MainActivity.toastView(this, getString(R.string.error_ch2))
                    }
                }
            }
        }
        binding.pavedamic3.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                MainActivity.toastView(this, getString(R.string.error_ch2))
            }
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.textView14.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.textView15.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.textView16.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.pavedamic3.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.notificationView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
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
                binding.titleToolbar.text = resources.getString(R.string.tools_item)
            }
            adminResetJob?.cancel()
            adminClickTime = SystemClock.elapsedRealtime()
            if (adminItemCount == 7) {
                val checkLogin = CheckLogin()
                checkLogin.isCancelable = false
                checkLogin.show(supportFragmentManager, "checkLogin")
                binding.titleToolbar.text = resources.getString(R.string.tools_admin_item, ": Гатова")
            }
            when (adminItemCount) {
                4 -> binding.titleToolbar.text = resources.getString(R.string.tools_admin_item, ": 3")
                5 -> binding.titleToolbar.text = resources.getString(R.string.tools_admin_item, ": 2")
                6 -> binding.titleToolbar.text = resources.getString(R.string.tools_admin_item, ": 1")
            }
            adminResetJob = CoroutineScope(Dispatchers.Main).launch {
                delay(3000L)
                binding.titleToolbar.text = resources.getString(R.string.tools_item)
            }
        }

        if (k.getBoolean("admin", false)) {
            binding.admin.visibility = View.VISIBLE
            binding.checkBox8.visibility = View.VISIBLE
        }
        binding.admin.setOnClickListener {
            if (checkmodulesAdmin()) {
                dynamicModuleInstalled()
            } else {
                val dialog = DialogUpdateMalitounik.getInstance(getString(R.string.title_download_module2))
                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "DialogUpdateMalitounik")
                setDownloadDynamicModuleListener(this)
                downloadDynamicModule("admin")
            }
        }
        if (dzenNoch) binding.prav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.prav.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getInt("pravas", 0)
            if (isChecked) {
                prefEditor.putInt("pravas", 1)
            } else {
                prefEditor.putInt("pravas", 0)
            }
            prefEditor.apply()
            if (check != k.getInt("pravas", 0)) edit = true
        }
        if (dzenNoch) binding.pkc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.pkc.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getInt("pkc", 0)
            if (isChecked) {
                prefEditor.putInt("pkc", 1)
            } else {
                prefEditor.putInt("pkc", 0)
            }
            prefEditor.apply()
            if (check != k.getInt("pkc", 0)) edit = true
        }
        if (dzenNoch) binding.dzair.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.dzair.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getInt("gosud", 0)
            if (isChecked) {
                prefEditor.putInt("gosud", 1)
            } else {
                prefEditor.putInt("gosud", 0)
            }
            prefEditor.apply()
            if (check != k.getInt("gosud", 0)) edit = true
        }
        if (dzenNoch) binding.praf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        binding.praf.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getInt("pafesii", 0)
            if (isChecked) {
                prefEditor.putInt("pafesii", 1)
            } else {
                prefEditor.putInt("pafesii", 0)
            }
            prefEditor.apply()
            if (check != k.getInt("pafesii", 0)) edit = true
        }
        if (k.getInt("pkc", 0) == 1) binding.pkc.isChecked = true
        if (k.getInt("pravas", 0) == 1) binding.prav.isChecked = true
        if (k.getInt("gosud", 0) == 1) binding.dzair.isChecked = true
        if (k.getInt("pafesii", 0) == 1) binding.praf.isChecked = true
        if (Build.MANUFACTURER.contains("huawei", true)) {
            binding.helpNotifi.visibility = View.VISIBLE
            binding.helpNotifi.setOnClickListener {
                val notifi = DialogHelpNotification()
                notifi.show(supportFragmentManager, "help_notification")
            }
        }
        val belarus = k.getBoolean("belarus", true)
        if (belarus) {
            binding.maranataBel.isChecked = true
            binding.maranataRus.isChecked = false
        } else {
            binding.maranataRus.isChecked = true
            binding.maranataBel.isChecked = false
        }
        binding.notificationOnly.isChecked = notification == NOTIFICATION_SVIATY_ONLY
        binding.notificationFull.isChecked = notification == NOTIFICATION_SVIATY_FULL
        binding.notificationNon.isChecked = notification == NOTIFICATION_SVIATY_NONE
        val sinoidal = k.getInt("sinoidal", 0)
        if (dzenNoch) binding.sinoidal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (sinoidal == 1) binding.sinoidal.isChecked = true
        val maranata = k.getInt("maranata", 0)
        if (dzenNoch) binding.maranata.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (maranata == 1) {
            binding.maranata.isChecked = true
        } else {
            binding.maranataBel.isClickable = false
            binding.maranataRus.isClickable = false
            binding.maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            binding.maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
        }
        val autoDzenNochSettings = k.getBoolean("auto_dzen_noch", false)
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor != null) {
            if (autoDzenNochSettings) {
                binding.day.isChecked = false
                binding.night.isChecked = false
                binding.autoNight.isChecked = true
            } else {
                if (dzenNoch) {
                    binding.day.isChecked = false
                    binding.night.isChecked = true
                    binding.autoNight.isChecked = false
                } else {
                    binding.day.isChecked = true
                    binding.night.isChecked = false
                    binding.autoNight.isChecked = false
                }
            }
        } else {
            binding.autoNight.visibility = View.GONE
        }
        val autoscrollAutostart = k.getBoolean("autoscrollAutostart", false)
        if (dzenNoch) binding.checkBox6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (autoscrollAutostart) {
            binding.checkBox6.isChecked = true
        }
        val scrinOn = k.getBoolean("scrinOn", false)
        if (dzenNoch) binding.checkBox7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (scrinOn) {
            binding.checkBox7.isChecked = true
        }
        val fullScreen = k.getBoolean("fullscreenPage", false)
        if (dzenNoch) binding.checkBox9.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (fullScreen) {
            binding.checkBox9.isChecked = true
        }
        val adminDayInYear = k.getBoolean("adminDayInYear", false)
        if (dzenNoch) binding.checkBox8.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        if (adminDayInYear) {
            binding.checkBox8.isChecked = true
        }
        if (dzenNoch) binding.reset.setBackgroundResource(R.drawable.knopka_red_black)
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
            noDelite.add("bible_time")
            noDelite.add("admin")
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
            prefEditor.putFloat("font_biblia", GET_FONT_SIZE_DEFAULT)
            prefEditor.putBoolean("dzen_noch", false)
            prefEditor.putInt("pravas", 0)
            prefEditor.putInt("pkc", 0)
            prefEditor.putInt("nedelia", 0)
            prefEditor.putInt("gosud", 0)
            prefEditor.putInt("pafesii", 0)
            prefEditor.putBoolean("belarus", true)
            prefEditor.putInt("notification", NOTIFICATION_SVIATY_FULL)
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
            prefEditor.putBoolean("AdminDialogSaveAsHelp", true)
            prefEditor.putBoolean("dialogHelpShare", true)
            prefEditor.putBoolean("help_fullscreen", true)
            prefEditor.putInt("fullscreenCount", 0)
            prefEditor.putInt("menuPiarlinyPage", 0)
            prefEditor.putInt("menuCytatyPage", 0)
            binding.maranataBel.isClickable = false
            binding.maranataRus.isClickable = false
            binding.maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            binding.maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            prefEditor.putInt("trafic", 0)
            prefEditor.putBoolean("admin", false)
            prefEditor.putBoolean("adminDayInYear", false)
            prefEditor.apply()
            binding.vibro.isClickable = true
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.guk.isClickable = true
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.day.isChecked = true
            binding.night.isChecked = false
            binding.autoNight.isChecked = false
            binding.checkBox6.isChecked = false
            binding.checkBox7.isChecked = false
            binding.checkBox8.isChecked = false
            binding.checkBox9.isChecked = false
            binding.maranata.isChecked = false
            binding.maranataRus.isChecked = false
            binding.maranataBel.isChecked = true
            binding.sinoidal.isChecked = false
            binding.notificationOnly.isChecked = false
            binding.notificationFull.isChecked = true
            binding.notificationNon.isChecked = false
            binding.vibro.isChecked = true
            binding.guk.isChecked = true
            binding.spinnerTime.setSelection(2)
            binding.pkc.isChecked = false
            binding.prav.isChecked = false
            binding.dzair.isChecked = false
            binding.praf.isChecked = false
            editFull = true
            if (getCheckDzenNoch() != dzenNoch) recreate()
            else setNotificationFull()
        }
        binding.maranataGrup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.maranataBel -> {
                    prefEditor.putBoolean("belarus", true)
                    if (binding.maranata.isChecked) {
                        val semuxaNoKnigi = DialogSemuxaNoKnigi.getInstance(true)
                        semuxaNoKnigi.show(supportFragmentManager, "semuxa_no_knigi")
                    }
                }

                R.id.maranataRus -> {
                    prefEditor.putBoolean("belarus", false)
                }

                else -> {
                }
            }
            prefEditor.apply()
        }
        binding.notificationGrup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.notificationOnly -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                            if (k.getBoolean("permissionNotificationApi33", true) && supportFragmentManager.findFragmentByTag("dialogHelpNotificationApi33") == null) {
                                val dialogHelpNotificationApi33 = DialogHelpNotificationApi33.getInstance(NOTIFICATION_SVIATY_ONLY)
                                dialogHelpNotificationApi33.show(supportFragmentManager, "dialogHelpNotificationApi33")
                            }
                            binding.pavedamic3.visibility = View.VISIBLE
                        } else {
                            binding.pavedamic3.visibility = View.GONE
                            setNotificationOnly()
                        }
                    } else {
                        setNotificationOnly()
                    }
                }

                R.id.notificationFull -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                            if (k.getBoolean("permissionNotificationApi33", true) && supportFragmentManager.findFragmentByTag("dialogHelpNotificationApi33") == null) {
                                val dialogHelpNotificationApi33 = DialogHelpNotificationApi33.getInstance(NOTIFICATION_SVIATY_FULL)
                                dialogHelpNotificationApi33.show(supportFragmentManager, "dialogHelpNotificationApi33")
                            }
                            binding.pavedamic3.visibility = View.VISIBLE
                        } else {
                            binding.pavedamic3.visibility = View.GONE
                            setNotificationFull()
                        }
                    } else {
                        setNotificationFull()
                    }
                }

                R.id.notificationNon -> {
                    binding.pavedamic3.visibility = View.GONE
                    setNotificationNon()
                }
            }
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
            val check = k.getInt("sinoidal", 0)
            if (isChecked) {
                prefEditor.putInt("sinoidal", 1)
            } else {
                prefEditor.putInt("sinoidal", 0)
            }
            prefEditor.apply()
            if (check != k.getInt("sinoidal", 0)) editFull = true
        }
        binding.maranata.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getInt("maranata", 0)
            if (isChecked) {
                if (k.getBoolean("belarus", true)) {
                    val semuxaNoKnigi = DialogSemuxaNoKnigi.getInstance(true)
                    semuxaNoKnigi.show(supportFragmentManager, "semuxa_no_knigi")
                }
                prefEditor.putInt("maranata", 1)
                binding.maranataBel.isClickable = true
                binding.maranataRus.isClickable = true
                if (dzenNoch) {
                    binding.maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                    binding.maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                    binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                } else {
                    binding.maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    binding.maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                    binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
                }
            } else {
                prefEditor.putInt("maranata", 0)
                binding.maranataBel.isClickable = false
                binding.maranataRus.isClickable = false
                binding.maranataBel.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
                binding.maranataRus.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
                binding.maranataOpis.setTextColor(ContextCompat.getColor(this, R.color.colorSecondary_text))
            }
            prefEditor.apply()
            if (check != k.getInt("maranata", 0)) edit = true
        }
        binding.guk.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                prefEditor.putInt("guk", 1)
            } else {
                prefEditor.putInt("guk", 0)
            }
            prefEditor.apply()
        }
        binding.checkBox6.setOnCheckedChangeListener { _, isChecked: Boolean ->
            prefEditor.putBoolean("autoscrollAutostart", isChecked)
            prefEditor.apply()
        }
        binding.checkBox7.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getBoolean("scrinOn", false)
            prefEditor.putBoolean("scrinOn", isChecked)
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            prefEditor.apply()
            if (check != k.getBoolean("scrinOn", false)) editFull = true
        }
        binding.checkBox8.setOnCheckedChangeListener { _, isChecked: Boolean ->
            val check = k.getBoolean("adminDayInYear", false)
            prefEditor.putBoolean("adminDayInYear", isChecked)
            prefEditor.apply()
            if (check != k.getBoolean("adminDayInYear", false)) edit = true
        }
        binding.checkBox9.setOnCheckedChangeListener { _, isChecked: Boolean ->
            prefEditor.putBoolean("fullscreenPage", isChecked)
            prefEditor.apply()
        }
        binding.nightGrup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            if (checkedId == R.id.autoNight) {
                prefEditor.putBoolean("auto_dzen_noch", true)
                prefEditor.apply()
                setlightSensor()
                if (getCheckDzenNoch() != dzenNoch) recreate()
            } else {
                val check = getBaseDzenNoch()
                prefEditor.putBoolean("auto_dzen_noch", false)
                removelightSensor()
                if (checkedId == R.id.day) {
                    prefEditor.putBoolean("dzen_noch", false)
                } else {
                    prefEditor.putBoolean("dzen_noch", true)
                }
                prefEditor.apply()
                if (check != k.getBoolean("dzen_noch", false)) {
                    recreate()
                }
            }
        }
        binding.vibro.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.guk.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.sinoidal.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.maranata.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.prav.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.pkc.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.dzair.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.praf.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.day.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.checkBox6.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.checkBox7.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.checkBox8.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.checkBox9.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.night.typeface = MainActivity.createFont(Typeface.NORMAL)
        binding.autoNight.typeface = MainActivity.createFont(Typeface.NORMAL)
        if (k.getBoolean("help_check_notifi", true) && Build.MANUFACTURER.contains("huawei", true) && (notification == NOTIFICATION_SVIATY_ONLY || notification == NOTIFICATION_SVIATY_FULL)) {
            val notifi = DialogHelpNotification()
            notifi.show(supportFragmentManager, "help_notification")
        }
        setTollbarTheme()
    }

    override fun dynamicModuleDownloading(totalBytesToDownload: Double, bytesDownloaded: Double) {
        val dialog = supportFragmentManager.findFragmentByTag("DialogUpdateMalitounik") as? DialogUpdateMalitounik
        dialog?.updateProgress(totalBytesToDownload, bytesDownloaded)
    }

    override fun dynamicModuleInstalled() {
        val dialog = supportFragmentManager.findFragmentByTag("DialogUpdateMalitounik") as? DialogUpdateMalitounik
        dialog?.updateComplete()
        SplitInstallHelper.updateAppInfo(this)
        val intent = Intent()
        intent.setClassName(this, MainActivity.ADMINMAIN)
        startActivity(intent)
    }

    override fun onDialogHelpNotificationApi33(notification: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            prefEditor.putInt("notification", notification)
            prefEditor.apply()
            mPermissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setNotificationOnly() {
        prefEditor.putInt("notification", NOTIFICATION_SVIATY_ONLY)
        prefEditor.apply()
        binding.notifiSvizta.visibility = View.VISIBLE
        binding.spinnerTime.visibility = View.VISIBLE
        if (k.getBoolean("help_check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
            val notifi = DialogHelpNotification()
            notifi.show(supportFragmentManager, "help_notification")
        }
        if (dzenNoch) {
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        } else {
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
        }
        binding.notificationOnly.isChecked = true
        setNotificationsJob?.cancel()
        setNotificationsJob = CoroutineScope(Dispatchers.IO).launch {
            setNotifications(1)
        }
    }

    private fun setNotificationFull() {
        prefEditor.putInt("notification", NOTIFICATION_SVIATY_FULL)
        prefEditor.apply()
        binding.notifiSvizta.visibility = View.VISIBLE
        binding.spinnerTime.visibility = View.VISIBLE
        if (k.getBoolean("help_check_notifi", true) && Build.MANUFACTURER.contains("huawei", true)) {
            val notifi = DialogHelpNotification()
            notifi.show(supportFragmentManager, "help_notification")
        }
        if (dzenNoch) {
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        } else {
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
        }
        binding.notificationFull.isChecked = true
        setNotificationsJob?.cancel()
        setNotificationsJob = CoroutineScope(Dispatchers.IO).launch {
            setNotifications(2)
        }
    }

    private fun setNotificationNon() {
        prefEditor.putInt("notification", NOTIFICATION_SVIATY_NONE)
        prefEditor.apply()
        binding.notifiSvizta.visibility = View.GONE
        binding.spinnerTime.visibility = View.GONE
        if (dzenNoch) {
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        } else {
            binding.vibro.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
            binding.guk.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_text))
        }
        binding.notificationNon.isChecked = true
        setNotificationsJob?.cancel()
        setNotificationsJob = CoroutineScope(Dispatchers.IO).launch {
            setNotifications(0)
        }
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getString(R.string.tools_item)
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
        binding.checkBox8.visibility = View.VISIBLE
        if (!checkmodulesAdmin()) {
            val dialog = DialogUpdateMalitounik.getInstance(getString(R.string.title_download_module2))
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "DialogUpdateMalitounik")
            setDownloadDynamicModuleListener(this)
            downloadDynamicModule("admin")
        }
    }

    private class TimeAdapter(activity: Activity, private val dataTimes: ArrayList<DataTime>) : ArrayAdapter<DataTime>(activity, R.layout.simple_list_item_1, dataTimes) {
        private val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
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

    private class ViewHolder(var text: TextView)

    private class DataTime(val string: String, val data: Int)
}