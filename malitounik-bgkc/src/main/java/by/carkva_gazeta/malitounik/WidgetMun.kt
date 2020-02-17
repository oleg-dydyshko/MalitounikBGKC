package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by oleg on 20.3.17
 */
class WidgetMun : AppWidgetProvider() {
    private var updateViews: RemoteViews? = null
    private val updateAllWidgets = "update_all_widgets"
    private val munPlus = "mun_plus"
    private val munMinus = "mun_minus"
    private val reset = "reset"
    private lateinit var data: ArrayList<ArrayList<String>>
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (updateViews == null) updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        for (i in appWidgetIds) {
            updateWidget(context, appWidgetManager, i)
        }
        // Обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetIds, updateViews)
        //appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), Widget_mun.class.getName()), updateViews);
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetIDs: IntArray) {
        if (updateViews == null) updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance() as GregorianCalendar
        val monthName = arrayOf("СТУДЗЕНЬ", "ЛЮТЫ", "САКАВІК", "КРАСАВІК", "ТРАВЕНЬ", "ЧЭРВЕНЬ",
                "ЛІПЕНЬ", "ЖНІВЕНЬ", "ВЕРАСЕНЬ", "КАСТРЫЧНІК", "ЛІСТАПАД", "СЬНЕЖАНЬ")
        for (i in widgetIDs) {
            val tecmun = chin.getInt("WIDGET$i", c[Calendar.MONTH])
            updateViews?.setTextViewText(R.id.Mun_widget, monthName[tecmun])
            val updateIntent = Intent(context, WidgetMun::class.java)
            updateIntent.action = munPlus
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i)
            var pIntent = PendingIntent.getBroadcast(context, i, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            updateViews?.setOnClickPendingIntent(R.id.imageButton2, pIntent)
            val countIntent = Intent(context, WidgetMun::class.java)
            countIntent.action = munMinus
            countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i)
            pIntent = PendingIntent.getBroadcast(context, i, countIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            updateViews?.setOnClickPendingIntent(R.id.imageButton, pIntent)
            mun(context, i)
        }
        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetIDs, updateViews)
        //appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), Widget_mun.class.getName()), updateViews);
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance() as GregorianCalendar
        val cYear = SettingsActivity.GET_CALIANDAR_YEAR_MAX //c.get(Calendar.YEAR);
        val tecmun = chin.getInt("WIDGET$widgetID", c[Calendar.MONTH])
        val tecyear = chin.getInt("WIDGETYEAR$widgetID", SettingsActivity.GET_CALIANDAR_YEAR_MAX)
        val monthName = arrayOf("СТУДЗЕНЬ", "ЛЮТЫ", "САКАВІК", "КРАСАВІК", "ТРАВЕНЬ", "ЧЭРВЕНЬ",
                "ЛІПЕНЬ", "ЖНІВЕНЬ", "ВЕРАСЕНЬ", "КАСТРЫЧНІК", "ЛІСТАПАД", "СЬНЕЖАНЬ")
        if (updateViews == null) updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        if (tecyear == c[Calendar.YEAR]) updateViews?.setTextViewText(R.id.Mun_widget, monthName[tecmun]) else updateViews?.setTextViewText(R.id.Mun_widget, monthName[tecmun] + ", " + tecyear)
        if (cYear == tecyear && tecmun == 11) updateViews?.setViewVisibility(R.id.imageButton2, View.INVISIBLE) else updateViews?.setViewVisibility(R.id.imageButton2, View.VISIBLE)
        if (SettingsActivity.GET_CALIANDAR_YEAR_MIN == tecyear && tecmun == 0) updateViews?.setViewVisibility(R.id.imageButton, View.INVISIBLE) else updateViews?.setViewVisibility(R.id.imageButton, View.VISIBLE)
        val updateIntent = Intent(context, WidgetMun::class.java)
        updateIntent.action = munPlus
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        var pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews?.setOnClickPendingIntent(R.id.imageButton2, pIntent)
        val countIntent = Intent(context, WidgetMun::class.java)
        countIntent.action = munMinus
        countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        pIntent = PendingIntent.getBroadcast(context, widgetID, countIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews?.setOnClickPendingIntent(R.id.imageButton, pIntent)
        mun(context, widgetID)
        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, updateViews)
        //appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), Widget_mun.class.getName()), updateViews);
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        chin.edit().putBoolean("WIDGET_MUN_ENABLED", true).apply()
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = updateAllWidgets
        val pIntentBoot = PendingIntent.getBroadcast(context, 53, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, 0)
        val c = Calendar.getInstance() as GregorianCalendar
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
            }
            else -> {
                alarmManager[AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])] = pIntent
                alarmManager[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000] = pIntentBoot
            }
        }
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mkTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)), 86400000, pIntent);
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        chin.edit().putBoolean("WIDGET_MUN_ENABLED", false).apply()
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = updateAllWidgets
        val pIntent = PendingIntent.getBroadcast(context, 52, intent, 0)
        val pIntentBoot = PendingIntent.getBroadcast(context, 53, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reset = Intent(context, WidgetMun::class.java)
        reset.action = this.reset
        val pReset = PendingIntent.getBroadcast(context, 257, reset, 0)
        alarmManager.cancel(pIntent)
        alarmManager.cancel(pIntentBoot)
        alarmManager.cancel(pReset)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // Удаляем Preferences
        val editor = context.getSharedPreferences("biblia", Context.MODE_PRIVATE).edit()
        for (widgetID in appWidgetIds) {
            editor.remove("WIDGET$widgetID")
            editor.remove("WIDGETYEAR$widgetID")
        }
        editor.apply()
    }

    private fun mkTime(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        var c = Calendar.getInstance() as GregorianCalendar
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (intent.action.equals(updateAllWidgets, ignoreCase = true)) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            for (i in ids) {
                chin.edit().putInt("WIDGET$i", c[Calendar.MONTH]).apply()
                chin.edit().putInt("WIDGETYEAR$i", c[Calendar.YEAR]).apply()
            }
            onUpdate(context, appWidgetManager, ids)
            val intentUpdate = Intent(context, WidgetMun::class.java)
            intentUpdate.action = updateAllWidgets
            c.add(Calendar.DATE, 1)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 51, intentUpdate, 0)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
                }
                else -> {
                    alarmManager[AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])] = pIntent
                }
            }
        }
        c = Calendar.getInstance() as GregorianCalendar
        val resetMain = "reset_main"
        if (intent.action.equals(resetMain, ignoreCase = true)) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            for (i in ids) {
                chin.edit().putInt("WIDGET$i", c[Calendar.MONTH]).apply()
                chin.edit().putInt("WIDGETYEAR$i", c[Calendar.YEAR]).apply()
            }
            updateWidget(context, AppWidgetManager.getInstance(context), ids)
        }
        if (intent.action.equals(reset, ignoreCase = true)) { // извлекаем ID экземпляра
            var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID //AppWidgetManager.INVALID_APPWIDGET_ID = 0
            val extras = intent.extras
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                chin.edit().putInt("WIDGET$mAppWidgetId", c[Calendar.MONTH]).apply()
                chin.edit().putInt("WIDGETYEAR$mAppWidgetId", c[Calendar.YEAR]).apply()
                updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
            }
        }
        if (intent.action.equals(munPlus, ignoreCase = true) || intent.action.equals(munMinus, ignoreCase = true)) { // извлекаем ID экземпляра
            var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID //AppWidgetManager.INVALID_APPWIDGET_ID = 0
            val extras = intent.extras
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                var tekmun = chin.getInt("WIDGET$mAppWidgetId", c[Calendar.MONTH])
                var tekyear = chin.getInt("WIDGETYEAR$mAppWidgetId", c[Calendar.YEAR])
                if (intent.action.equals(munPlus, ignoreCase = true)) {
                    if (tekmun < 11) chin.edit().putInt("WIDGET$mAppWidgetId", ++tekmun).apply() else {
                        chin.edit().putInt("WIDGET$mAppWidgetId", 0).apply()
                        chin.edit().putInt("WIDGETYEAR$mAppWidgetId", ++tekyear).apply()
                    }
                } else {
                    if (tekmun > 0) chin.edit().putInt("WIDGET$mAppWidgetId", --tekmun).apply() else {
                        chin.edit().putInt("WIDGET$mAppWidgetId", 11).apply()
                        chin.edit().putInt("WIDGETYEAR$mAppWidgetId", --tekyear).apply()
                    }
                }
                val reset = Intent(context, WidgetMun::class.java)
                reset.action = this.reset
                reset.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                //if (alarmManager != null) {
                alarmManager.cancel(pReset)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
                    }
                    else -> {
                        alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000] = pReset
                    }
                }
                //}
// Обновляем виджет
                updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
            }
        }
    }

    private fun getmun(mun: Int, year: Int): Int {
        val g = GregorianCalendar(year, mun, 1) //(GregorianCalendar) Calendar.getInstance();
        val position = (year - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + g[Calendar.MONTH]
        val count = (SettingsActivity.GET_CALIANDAR_YEAR_MAX - SettingsActivity.GET_CALIANDAR_YEAR_MIN + 1) * 12
        for (i in 0 until count) {
            if (position == i) {
                return position
            }
        }
        return position
    }

    private fun mun(context: Context, widgetID: Int) {
        updateViews?.setViewVisibility(R.id.nedel5, View.VISIBLE)
        updateViews?.setViewVisibility(R.id.nedel6, View.VISIBLE)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance() as GregorianCalendar
        var calendarPost: GregorianCalendar
        //Month = 10;
        val month = chin.getInt("WIDGET$widgetID", c[Calendar.MONTH])
        //year = 2016
        val year = chin.getInt("WIDGETYEAR$widgetID", c[Calendar.YEAR])
        val inputStream = context.resources.openRawResource(MainActivity.caliandar(context, getmun(month, year)))
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
        data = gson.fromJson(reader.readText(), type)
        isr.close()
        val calendarFull = GregorianCalendar(year, month, 1)
        var munTudey = false
        if (month == c[Calendar.MONTH] && year == c[Calendar.YEAR]) munTudey = true
        val wik = calendarFull[Calendar.DAY_OF_WEEK]
        val munAll = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
        val munActual = c[Calendar.DAY_OF_MONTH]
        calendarFull.add(Calendar.MONTH, -1)
        val oldMunAktual = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
        val mouthOld = calendarFull[Calendar.MONTH]
        val yearOld = calendarFull[Calendar.YEAR]
        var oldDay = oldMunAktual - wik + 1
        calendarFull.add(Calendar.MONTH, 2)
        val mouthNew = calendarFull[Calendar.MONTH]
        val yearNew = calendarFull[Calendar.YEAR]
        var day: String
        var i = 0
        var newDay = 0
        var nopost = false
        var post = false
        var strogiPost = false //, do_day, img;
        for (e in 1..42) {
            var denNedeli: Int
            if (e < wik) {
                ++oldDay
                day = "start"
            } else if (e < munAll + wik) {
                i++
                day = i.toString()
                calendarPost = GregorianCalendar(year, month, i)
                denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                nopost = data[i - 1][7].contains("1")
                post = data[i - 1][7].contains("2")
                strogiPost = data[i - 1][7].contains("3")
                if (denNedeli == 1) nopost = false
            } else {
                ++newDay
                day = "end"
            }
            if (42 - (munAll + wik) >= 6) {
                updateViews?.setViewVisibility(R.id.nedel6, View.GONE)
            }
            if (munAll + wik == 29) {
                updateViews?.setViewVisibility(R.id.nedel5, View.GONE)
            }
            calendarPost = GregorianCalendar(year, month, i)
            val widgetMun = "widget_mun"
            if (e == 1) {
                updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                if (day == "start") {
                    calendarPost = GregorianCalendar(yearOld, mouthOld, oldDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button1a, pIntent)
                    updateViews?.setTextViewText(R.id.button1a, oldDay.toString())
                    updateViews?.setTextColor(R.id.button1a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                } else {
                    updateViews?.setTextColor(R.id.button1a, ContextCompat.getColor(context, R.color.colorPrimary))
                    updateViews?.setTextViewText(R.id.button1a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button1a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button1a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button1a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button1a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button1a, pIntent)
                }
            }
            if (e == 2) {
                updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "start") {
                    calendarPost = GregorianCalendar(yearOld, mouthOld, oldDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button2a, pIntent)
                    updateViews?.setTextViewText(R.id.button2a, oldDay.toString())
                    updateViews?.setTextColor(R.id.button2a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                } else {
                    updateViews?.setTextColor(R.id.button2a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button2a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button2a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button2a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button2a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button2a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button2a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button2a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button2a, pIntent)
                }
            }
            if (e == 3) {
                updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "start") {
                    calendarPost = GregorianCalendar(yearOld, mouthOld, oldDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button3a, pIntent)
                    updateViews?.setTextViewText(R.id.button3a, oldDay.toString())
                    updateViews?.setTextColor(R.id.button3a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                } else {
                    updateViews?.setTextColor(R.id.button3a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button3a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button3a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button3a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button3a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button3a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button3a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button3a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button3a, pIntent)
                }
            }
            if (e == 4) {
                updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "start") {
                    calendarPost = GregorianCalendar(yearOld, mouthOld, oldDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button4a, pIntent)
                    updateViews?.setTextViewText(R.id.button4a, oldDay.toString())
                    updateViews?.setTextColor(R.id.button4a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                } else {
                    updateViews?.setTextColor(R.id.button4a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button4a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button4a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button4a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button4a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button4a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button4a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button4a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button4a, pIntent)
                }
            }
            if (e == 5) {
                updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "start") {
                    calendarPost = GregorianCalendar(yearOld, mouthOld, oldDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button5a, pIntent)
                    updateViews?.setTextViewText(R.id.button5a, oldDay.toString())
                    updateViews?.setTextColor(R.id.button5a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                } else {
                    updateViews?.setTextColor(R.id.button5a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button5a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button5a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button5a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button5a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button5a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button5a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button5a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button5a, pIntent)
                }
            }
            if (e == 6) {
                updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "start") {
                    calendarPost = GregorianCalendar(yearOld, mouthOld, oldDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button6a, pIntent)
                    updateViews?.setTextViewText(R.id.button6a, oldDay.toString())
                    updateViews?.setTextColor(R.id.button6a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                } else {
                    updateViews?.setTextColor(R.id.button6a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button6a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button6a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button6a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button6a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button6a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button6a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button6a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button6a, pIntent)
                }
            }
            if (e == 7) {
                updateViews?.setTextColor(R.id.button7a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button7a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button7a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button7a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button7a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button7a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button7a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button7a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button7a, pIntent)
            }
            if (e == 8) {
                updateViews?.setTextColor(R.id.button8a, ContextCompat.getColor(context, R.color.colorPrimary))
                updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                updateViews?.setTextViewText(R.id.button8a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button8a, ContextCompat.getColor(context, R.color.colorIcons))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button8a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button8a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button8a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button8a, pIntent)
            }
            if (e == 9) {
                updateViews?.setTextColor(R.id.button9a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button9a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button9a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button9a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button9a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button9a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button9a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button9a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button9a, pIntent)
            }
            if (e == 10) {
                updateViews?.setTextColor(R.id.button10a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button10a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button10a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button10a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button10a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button10a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button10a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button10a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button10a, pIntent)
            }
            if (e == 11) {
                updateViews?.setTextColor(R.id.button11a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button11a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button11a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button11a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button11a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button11a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button11a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button11a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button11a, pIntent)
            }
            if (e == 12) {
                updateViews?.setTextColor(R.id.button12a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button12a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button12a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button12a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button12a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button12a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button12a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button12a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button12a, pIntent)
            }
            if (e == 13) {
                updateViews?.setTextColor(R.id.button13a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button13a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button13a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button13a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button13a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button13a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button13a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button13a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button13a, pIntent)
            }
            if (e == 14) {
                updateViews?.setTextColor(R.id.button14a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button14a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button14a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button14a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button14a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button14a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button14a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button14a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button14a, pIntent)
            }
            if (e == 15) {
                updateViews?.setTextColor(R.id.button15a, ContextCompat.getColor(context, R.color.colorPrimary))
                updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                updateViews?.setTextViewText(R.id.button15a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button15a, ContextCompat.getColor(context, R.color.colorIcons))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button15a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button15a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button15a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button15a, pIntent)
            }
            if (e == 16) {
                updateViews?.setTextColor(R.id.button16a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button16a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button16a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button16a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button16a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button16a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button16a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button16a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button16a, pIntent)
            }
            if (e == 17) {
                updateViews?.setTextColor(R.id.button17a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button17a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button17a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button17a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button17a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button17a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button17a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button17a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button17a, pIntent)
            }
            if (e == 18) {
                updateViews?.setTextColor(R.id.button18a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button18a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button18a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button18a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button18a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button18a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button18a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button18a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button18a, pIntent)
            }
            if (e == 19) {
                updateViews?.setTextColor(R.id.button19a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button19a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button19a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button19a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button19a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button19a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button19a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button19a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button19a, pIntent)
            }
            if (e == 20) {
                updateViews?.setTextColor(R.id.button20a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button20a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button20a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button20a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button20a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button20a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button20a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button20a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button20a, pIntent)
            }
            if (e == 21) {
                updateViews?.setTextColor(R.id.button21a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button21a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button21a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button21a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button21a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button21a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button21a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button21a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button21a, pIntent)
            }
            if (e == 22) {
                updateViews?.setTextColor(R.id.button22a, ContextCompat.getColor(context, R.color.colorPrimary))
                updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                updateViews?.setTextViewText(R.id.button22a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button22a, ContextCompat.getColor(context, R.color.colorIcons))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button22a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button22a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button22a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button22a, pIntent)
            }
            if (e == 23) {
                updateViews?.setTextColor(R.id.button23a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button23a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button23a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button23a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button23a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button23a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button23a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button23a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button23a, pIntent)
            }
            if (e == 24) {
                updateViews?.setTextColor(R.id.button24a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button24a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button24a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button24a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button24a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button24a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button24a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button24a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button24a, pIntent)
            }
            if (e == 25) {
                updateViews?.setTextColor(R.id.button25a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button25a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button25a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button25a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button25a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button25a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button25a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button25a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button25a, pIntent)
            }
            if (e == 26) {
                updateViews?.setTextColor(R.id.button26a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button26a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button26a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button26a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button26a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button26a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button26a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button26a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button26a, pIntent)
            }
            if (e == 27) {
                updateViews?.setTextColor(R.id.button27a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button27a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button27a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button27a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button27a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button27a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button27a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button27a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button27a, pIntent)
            }
            if (e == 28) {
                updateViews?.setTextColor(R.id.button28a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_day)
                updateViews?.setTextViewText(R.id.button28a, i.toString())
                if (sviatyDvunadesiatya(i)) {
                    if (munActual == i && munTudey) updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_red)
                    updateViews?.setTextColor(R.id.button28a, ContextCompat.getColor(context, R.color.colorIcons))
                    updateViews?.setTextViewText(R.id.button28a, MainActivity.fromHtml("<strong>$i</strong>"))
                } else if (sviatyVialikia(i)) {
                    if (munActual == i && munTudey) {
                        updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_red_today)
                    } else {
                        updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_red)
                    }
                    updateViews?.setTextColor(R.id.button28a, ContextCompat.getColor(context, R.color.colorIcons))
                } else {
                    if (nopost) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                    }
                    if (post) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_post)
                    }
                    if (strogiPost) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                        } else {
                            updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                        }
                        updateViews?.setTextColor(R.id.button28a, ContextCompat.getColor(context, R.color.colorIcons))
                    }
                    if (!nopost && !post && !strogiPost) {
                        denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                        if (denNedeli == 1) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        } else {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button28a, "setBackgroundResource", R.drawable.calendar_day)
                        }
                    }
                }
                if (prorok(i)) updateViews?.setTextViewText(R.id.button28a, MainActivity.fromHtml("<strong>$i</strong>"))
                calendarPost = GregorianCalendar(year, month, i)
                val dayIntent = Intent(context, SplashActivity::class.java)
                dayIntent.putExtra(widgetMun, true)
                dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                val code = year.toString() + "" + month + "" + i
                val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                updateViews?.setOnClickPendingIntent(R.id.button28a, pIntent)
            }
            if (e == 29) {
                updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button29a, pIntent)
                    updateViews?.setTextColor(R.id.button29a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button29a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button29a, ContextCompat.getColor(context, R.color.colorPrimary))
                    updateViews?.setTextViewText(R.id.button29a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button29a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button29a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button29a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button29a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button29a, pIntent)
                }
            }
            if (e == 30) {
                updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button30a, pIntent)
                    updateViews?.setTextColor(R.id.button30a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button30a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button30a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button30a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button30a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button30a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button30a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button30a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button30a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button30a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button30a, pIntent)
                }
            }
            if (e == 31) {
                updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button31a, pIntent)
                    updateViews?.setTextColor(R.id.button31a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button31a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button31a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button31a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button31a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button31a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button31a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button31a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button31a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button31a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button31a, pIntent)
                }
            }
            if (e == 32) {
                updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button32a, pIntent)
                    updateViews?.setTextColor(R.id.button32a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button32a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button32a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button32a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button32a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button32a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button32a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button32a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button32a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button32a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button32a, pIntent)
                }
            }
            if (e == 33) {
                updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button33a, pIntent)
                    updateViews?.setTextColor(R.id.button33a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button33a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button33a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button33a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button33a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button33a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button33a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button33a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button33a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button33a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button33a, pIntent)
                }
            }
            if (e == 34) {
                updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button34a, pIntent)
                    updateViews?.setTextColor(R.id.button34a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button34a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button34a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button34a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button34a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button34a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button34a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button34a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button34a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button34a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button34a, pIntent)
                }
            }
            if (e == 35) {
                updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button35a, pIntent)
                    updateViews?.setTextColor(R.id.button35a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button35a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button35a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button35a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button35a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button35a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button35a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button35a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button35a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button35a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button35a, pIntent)
                }
            }
            if (e == 36) {
                updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button36a, pIntent)
                    updateViews?.setTextColor(R.id.button36a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button36a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button36a, ContextCompat.getColor(context, R.color.colorPrimary))
                    updateViews?.setTextViewText(R.id.button36a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button36a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button36a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button36a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button36a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button36a, pIntent)
                }
            }
            if (e == 37) {
                updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_day)
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button37a, pIntent)
                    updateViews?.setTextColor(R.id.button37a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setTextViewText(R.id.button37a, newDay.toString())
                } else {
                    updateViews?.setTextColor(R.id.button37a, ContextCompat.getColor(context, R.color.colorPrimary_text))
                    updateViews?.setTextViewText(R.id.button37a, i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_red_today) else updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_red)
                        updateViews?.setTextColor(R.id.button37a, ContextCompat.getColor(context, R.color.colorIcons))
                        updateViews?.setTextViewText(R.id.button37a, MainActivity.fromHtml("<strong>$i</strong>"))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(R.id.button37a, ContextCompat.getColor(context, R.color.colorIcons))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_post_today) else updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_post)
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(R.id.button37a, ContextCompat.getColor(context, R.color.colorIcons))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_bez_posta_today) else updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_bez_posta)
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_day_today) else updateViews?.setInt(R.id.button37a, "setBackgroundResource", R.drawable.calendar_day)
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(R.id.button37a, MainActivity.fromHtml("<strong>$i</strong>"))
                    calendarPost = GregorianCalendar(year, month, i)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button37a, pIntent)
                }
            }
            if (e == 38) {
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button38a, pIntent)
                    updateViews?.setTextColor(R.id.button38a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setInt(R.id.button38a, "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextViewText(R.id.button38a, newDay.toString())
                }
            }
            if (e == 39) {
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button39a, pIntent)
                    updateViews?.setTextColor(R.id.button39a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setInt(R.id.button39a, "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextViewText(R.id.button39a, newDay.toString())
                }
            }
            if (e == 40) {
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button40a, pIntent)
                    updateViews?.setTextColor(R.id.button40a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setInt(R.id.button40a, "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextViewText(R.id.button40a, newDay.toString())
                }
            }
            if (e == 41) {
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button41a, pIntent)
                    updateViews?.setTextColor(R.id.button41a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setInt(R.id.button41a, "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextViewText(R.id.button41a, newDay.toString())
                }
            }
            if (e == 42) {
                if (day == "end") {
                    calendarPost = GregorianCalendar(yearNew, mouthNew, newDay)
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    dayIntent.putExtra("DayYear", calendarPost[Calendar.DAY_OF_YEAR])
                    dayIntent.putExtra("Year", calendarPost[Calendar.YEAR])
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews?.setOnClickPendingIntent(R.id.button42a, pIntent)
                    updateViews?.setTextColor(R.id.button42a, ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setInt(R.id.button42a, "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextViewText(R.id.button42a, newDay.toString())
                }
            }
        }
    }

    private fun sviatyVialikia(day: Int): Boolean { // когда выпадают ВЯЛІКІЯ СЬВЯТЫ относительно Пасхі
        return data[day - 1][5].contains("2")
    }

    private fun sviatyDvunadesiatya(day: Int): Boolean { // когда выпадают двунадесятые праздники
        return data[day - 1][5].contains("1")
    }

    private fun prorok(day: Int): Boolean { // когда выпадают Прарокі
        return !data[day - 1][4].contains("no_sviatyia") && data[day - 1][4].contains("#d00505")
    }
}