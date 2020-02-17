package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

class ReceiverUpdate : BroadcastReceiver() {
    private val updateAllWidgets = "update_all_widgets"
    private val resetMain = "reset_main"
    private lateinit var context: Context
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        task()
    }

    private fun task() {
        Thread(Runnable {
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            // Установка будильников
            val c = Calendar.getInstance() as GregorianCalendar
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (chin.getBoolean("WIDGET_MUN_ENABLED", false)) {
                val munAk = c[Calendar.MONTH]
                val yearAk = c[Calendar.YEAR]
                var resetWid = false
                val intent = Intent(context, WidgetMun::class.java)
                intent.action = updateAllWidgets
                val pIntent = PendingIntent.getBroadcast(context, 51, intent, 0)
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
                    reset.action = resetMain
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
                val intent = Intent(context, Widget::class.java)
                intent.action = updateAllWidgets
                val pIntent = PendingIntent.getBroadcast(context, 50, intent, 0)
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
            var pIntent: PendingIntent?
            File(context.filesDir.toString() + "/Sabytie").walk().forEach { file ->
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
                                                    val intent = createIntentSabytie(t1[0].replace("_", " "), t1[1], t1[2])
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
                                                    val intent = createIntentSabytie(t1[0].replace("_", " "), t1[1], t1[2])
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
                                                    val intent = createIntentSabytie(t1[0].replace("_", " "), t1[1], t1[2])
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
                                                    val intent = createIntentSabytie(t1[0].replace("_", " "), t1[1], t1[2])
                                                    pIntent = PendingIntent.getBroadcast(context, (timerepit / 100000).toInt(), intent, 0)
                                                    am.setRepeating(AlarmManager.RTC_WAKEUP, timerepit, 2419200000L, pIntent)
                                                    break
                                                }
                                                timerepit += 2419200000L
                                            }
                                        }
                                        else -> if (t1[3].toLong() > c.timeInMillis) {
                                            val intent = createIntentSabytie(t1[0].replace("_", " "), t1[1], t1[2])
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
                                        val intent = createIntentSabytie(t1[0].replace("_", " "), t1[1], t1[2])
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
            }
            /*val dir = File(context.filesDir.toString() + "/Sabytie")
            for (s in dir.list()) {

            }*/
            val notify = chin.getInt("notification", 2)
            SettingsActivity.setNotifications(context, notify)
        }).start()
    }

    private fun mkTime(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[year, month, day, 0, 0] = 0
        return calendar.timeInMillis
    }

    private fun createIntentSabytie(action: String, data: String, time: String): Intent {
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
}