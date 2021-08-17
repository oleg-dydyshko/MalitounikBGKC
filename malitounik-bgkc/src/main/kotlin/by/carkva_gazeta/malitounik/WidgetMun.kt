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
import java.util.*

class WidgetMun : AppWidgetProvider() {
    private var updateViews: RemoteViews? = null
    private val munPlus = "mun_plus"
    private val munMinus = "mun_minus"
    private val reset = "reset"
    private lateinit var data: ArrayList<ArrayList<String>>
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (updateViews == null) updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        for (widgetID in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetID)
        }
        appWidgetManager.updateAppWidget(appWidgetIds, updateViews)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetIDs: IntArray) {
        if (updateViews == null) updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance() as GregorianCalendar
        val monthName = arrayOf("СТУДЗЕНЬ", "ЛЮТЫ", "САКАВІК", "КРАСАВІК", "ТРАВЕНЬ", "ЧЭРВЕНЬ", "ЛІПЕНЬ", "ЖНІВЕНЬ", "ВЕРАСЕНЬ", "КАСТРЫЧНІК", "ЛІСТАПАД", "СЬНЕЖАНЬ")
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        for (i in widgetIDs) {
            val tecmun = chin.getInt("WIDGET$i", c[Calendar.MONTH])
            updateViews?.setTextViewText(R.id.Mun_widget, monthName[tecmun])
            val updateIntent = Intent(context, WidgetMun::class.java)
            updateIntent.action = munPlus
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i)
            var pIntent = PendingIntent.getBroadcast(context, i, updateIntent, flags)
            updateViews?.setOnClickPendingIntent(R.id.imageButton2, pIntent)
            val countIntent = Intent(context, WidgetMun::class.java)
            countIntent.action = munMinus
            countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i)
            pIntent = PendingIntent.getBroadcast(context, i, countIntent, flags)
            updateViews?.setOnClickPendingIntent(R.id.imageButton, pIntent)
            mun(context, i)
        }
        appWidgetManager.updateAppWidget(widgetIDs, updateViews)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance() as GregorianCalendar
        val cYear = SettingsActivity.GET_CALIANDAR_YEAR_MAX //c.get(Calendar.YEAR);
        val tecmun = chin.getInt("WIDGET$widgetID", c[Calendar.MONTH])
        val tecyear = chin.getInt("WIDGETYEAR$widgetID", SettingsActivity.GET_CALIANDAR_YEAR_MAX)
        val monthName = arrayOf("СТУДЗЕНЬ", "ЛЮТЫ", "САКАВІК", "КРАСАВІК", "ТРАВЕНЬ", "ЧЭРВЕНЬ", "ЛІПЕНЬ", "ЖНІВЕНЬ", "ВЕРАСЕНЬ", "КАСТРЫЧНІК", "ЛІСТАПАД", "СЬНЕЖАНЬ")
        if (updateViews == null) updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        if (tecyear == c[Calendar.YEAR]) updateViews?.setTextViewText(R.id.Mun_widget, monthName[tecmun]) else updateViews?.setTextViewText(R.id.Mun_widget, monthName[tecmun] + ", " + tecyear)
        if (cYear == tecyear && tecmun == 11) updateViews?.setViewVisibility(R.id.imageButton2, View.INVISIBLE) else updateViews?.setViewVisibility(R.id.imageButton2, View.VISIBLE)
        if (SettingsActivity.GET_CALIANDAR_YEAR_MIN == tecyear && tecmun == 0) updateViews?.setViewVisibility(R.id.imageButton, View.INVISIBLE) else updateViews?.setViewVisibility(R.id.imageButton, View.VISIBLE)
        val updateIntent = Intent(context, WidgetMun::class.java)
        updateIntent.action = munPlus
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        var pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, flags)
        updateViews?.setOnClickPendingIntent(R.id.imageButton2, pIntent)
        val countIntent = Intent(context, WidgetMun::class.java)
        countIntent.action = munMinus
        countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        pIntent = PendingIntent.getBroadcast(context, widgetID, countIntent, flags)
        updateViews?.setOnClickPendingIntent(R.id.imageButton, pIntent)
        mun(context, widgetID)
        appWidgetManager.updateAppWidget(widgetID, updateViews)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        chin.edit().putBoolean("WIDGET_MUN_ENABLED", true).apply()
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = SettingsActivity.UPDATE_ALL_WIDGETS
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val pIntentBoot = PendingIntent.getBroadcast(context, 53, intent, flags)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, flags)
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
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val edit = chin.edit()
        for ((key, value) in chin.all) {
            if (key.contains("WIDGET")) {
                if (value is Int) {
                    edit.remove(key)
                }
            }
        }
        edit.putBoolean("WIDGET_MUN_ENABLED", false)
        edit.apply()
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = SettingsActivity.UPDATE_ALL_WIDGETS
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val pIntent = PendingIntent.getBroadcast(context, 52, intent, flags)
        val pIntentBoot = PendingIntent.getBroadcast(context, 53, intent, flags)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reset = Intent(context, WidgetMun::class.java)
        reset.action = this.reset
        val pReset = PendingIntent.getBroadcast(context, 257, reset, flags)
        alarmManager.cancel(pIntent)
        alarmManager.cancel(pIntentBoot)
        alarmManager.cancel(pReset)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
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
        if (intent.action.equals(SettingsActivity.UPDATE_ALL_WIDGETS, ignoreCase = true)) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            for (i in ids) {
                chin.edit().putInt("WIDGET$i", c[Calendar.MONTH]).apply()
                chin.edit().putInt("WIDGETYEAR$i", c[Calendar.YEAR]).apply()
            }
            onUpdate(context, appWidgetManager, ids)
            val intentUpdate = Intent(context, WidgetMun::class.java)
            intentUpdate.action = SettingsActivity.UPDATE_ALL_WIDGETS
            c.add(Calendar.DATE, 1)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or 0
            } else {
                0
            }
            val pIntent = PendingIntent.getBroadcast(context, 51, intentUpdate, flags)
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
        val resetMain = SettingsActivity.RESET_MAIN
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
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pReset = PendingIntent.getBroadcast(context, 257, reset, flags)
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
                updateWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
            }
        }
    }

    private fun idView(position: Int): Int {
        var view = R.id.button1a
        when (position) {
            1 -> view = R.id.button1a
            2 -> view = R.id.button2a
            3 -> view = R.id.button3a
            4 -> view = R.id.button4a
            5 -> view = R.id.button5a
            6 -> view = R.id.button6a
            7 -> view = R.id.button7a
            8 -> view = R.id.button8a
            9 -> view = R.id.button9a
            10 -> view = R.id.button10a
            11 -> view = R.id.button11a
            12 -> view = R.id.button12a
            13 -> view = R.id.button13a
            14 -> view = R.id.button14a
            15 -> view = R.id.button15a
            16 -> view = R.id.button16a
            17 -> view = R.id.button17a
            18 -> view = R.id.button18a
            19 -> view = R.id.button19a
            20 -> view = R.id.button20a
            21 -> view = R.id.button21a
            22 -> view = R.id.button22a
            23 -> view = R.id.button23a
            24 -> view = R.id.button24a
            25 -> view = R.id.button25a
            26 -> view = R.id.button26a
            27 -> view = R.id.button27a
            28 -> view = R.id.button28a
            29 -> view = R.id.button29a
            30 -> view = R.id.button30a
            31 -> view = R.id.button31a
            32 -> view = R.id.button32a
            33 -> view = R.id.button33a
            34 -> view = R.id.button34a
            35 -> view = R.id.button35a
            36 -> view = R.id.button36a
            37 -> view = R.id.button37a
            38 -> view = R.id.button38a
            39 -> view = R.id.button39a
            40 -> view = R.id.button40a
            41 -> view = R.id.button41a
            42 -> view = R.id.button42a
        }
        return view
    }

    private fun mun(context: Context, widgetID: Int) {
        updateViews?.setViewVisibility(R.id.nedel5, View.VISIBLE)
        updateViews?.setViewVisibility(R.id.nedel6, View.VISIBLE)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance() as GregorianCalendar
        var calendarPost: GregorianCalendar
        val month = chin.getInt("WIDGET$widgetID", c[Calendar.MONTH])
        val year = chin.getInt("WIDGETYEAR$widgetID", c[Calendar.YEAR])
        data = MenuCaliandar.getDataCalaindar(mun = month, year = year)
        val calendarFull = GregorianCalendar(year, month, 1)
        var munTudey = false
        if (month == c[Calendar.MONTH] && year == c[Calendar.YEAR]) munTudey = true
        val wik = calendarFull[Calendar.DAY_OF_WEEK]
        val munAll = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
        val munActual = c[Calendar.DAY_OF_MONTH]
        calendarFull.add(Calendar.MONTH, -1)
        val oldMunAktual = calendarFull.getActualMaximum(Calendar.DAY_OF_MONTH)
        val mouthOld = calendarFull[Calendar.MONTH]
        var oldDay = oldMunAktual - wik + 1
        calendarFull.add(Calendar.MONTH, 2)
        val mouthNew = calendarFull[Calendar.MONTH]
        var day: String
        var i = 0
        var newDay = 0
        var nopost = false
        var post = false
        var strogiPost = false
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
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            when (day) {
                "start" -> {
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    val position = data[0][25].toInt() - (wik - e)
                    dayIntent.putExtra("position", position)
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, flags)
                    updateViews?.setOnClickPendingIntent(idView(e), pIntent)
                    updateViews?.setTextViewText(idView(e), oldDay.toString())
                    if (e == 1) updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta)
                    else updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorSecondary_text))
                }
                "end" -> {
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    val position = data[data.size - 1][25].toInt() + newDay
                    dayIntent.putExtra("position", position)
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, flags)
                    updateViews?.setOnClickPendingIntent(idView(e), pIntent)
                    updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day)
                    updateViews?.setTextViewText(idView(e), newDay.toString())
                }
                else -> {
                    updateViews?.setTextViewText(idView(e), i.toString())
                    if (sviatyDvunadesiatya(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorWhite))
                    } else if (sviatyVialikia(i)) {
                        if (munActual == i && munTudey) {
                            updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorWhite))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta_today)
                            else updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta)
                            updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary_text))
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_post_today)
                            else updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_post)
                            updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary_text))
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorWhite))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta_today)
                                else updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta)
                                updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary))
                            } else {
                                if (munActual == i && munTudey) updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day_today)
                                else updateViews?.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day)
                                updateViews?.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary_text))
                            }
                        }
                    }
                    if (prorok(i)) updateViews?.setTextViewText(idView(e), MainActivity.fromHtml("<strong>$i</strong>"))
                    val dayIntent = Intent(context, SplashActivity::class.java)
                    dayIntent.putExtra(widgetMun, true)
                    val position = data[i - 1][25].toInt()
                    dayIntent.putExtra("position", position)
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, flags)
                    updateViews?.setOnClickPendingIntent(idView(e), pIntent)
                }
            }
        }
    }

    private fun sviatyVialikia(day: Int): Boolean {
        return data[day - 1][5].contains("2")
    }

    private fun sviatyDvunadesiatya(day: Int): Boolean {
        return data[day - 1][5].contains("1")
    }

    private fun prorok(day: Int): Boolean { // когда выпадают Прарокі
        return data[day - 1][4].contains("<font color=#d00505><strong>")
    }
}