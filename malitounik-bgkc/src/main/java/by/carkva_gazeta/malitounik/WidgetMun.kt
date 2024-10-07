package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import java.util.Calendar
import java.util.GregorianCalendar

class WidgetMun : AppWidgetProvider() {
    private val munPlus = "mun_plus"
    private val munMinus = "mun_minus"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (widgetID in appWidgetIds) {
            mun(context, appWidgetManager, widgetID)
        }
    }

    private fun getBaseDzenNoch(context: Context, widgetID: Int): Boolean {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val modeNight = k.getInt("mode_night_widget_mun$widgetID", SettingsActivity.MODE_NIGHT_SYSTEM)
        var dzenNoch = false
        when (modeNight) {
            SettingsActivity.MODE_NIGHT_SYSTEM -> {
                val configuration = Resources.getSystem().configuration
                dzenNoch = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            }

            SettingsActivity.MODE_NIGHT_YES -> {
                dzenNoch = true
            }

            SettingsActivity.MODE_NIGHT_NO -> {
                dzenNoch = false
            }
        }
        return dzenNoch
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        chin.edit().putBoolean("WIDGET_MUN_ENABLED", true).apply()
        val intent = Intent(context, WidgetMun::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
            }
            else -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(), pIntent)
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
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pIntent = PendingIntent.getBroadcast(context, 60, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reset = Intent(context, WidgetMun::class.java)
        reset.action = SettingsActivity.RESET_WIDGET_MUN
        val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_IMMUTABLE or 0)
        alarmManager.cancel(pIntent)
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

    private fun mkTime(addDate: Int = 0): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, addDate)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val c = Calendar.getInstance()
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            mun(context, AppWidgetManager.getInstance(context), widgetID)
        }
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            val edit = chin.edit()
            for (i in ids) {
                edit.putInt("WIDGET$i", c[Calendar.MONTH])
                edit.putInt("WIDGETYEAR$i", c[Calendar.YEAR])
            }
            edit.apply()
            onUpdate(context, appWidgetManager, ids)
            val intentUpdate = Intent(context, WidgetMun::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 60, intentUpdate, PendingIntent.FLAG_IMMUTABLE or 0)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }
                else -> {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(1), pIntent)
                }
            }
        }
        if (intent.action == SettingsActivity.RESET_WIDGET_MUN) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            val edit = chin.edit()
            for (i in ids) {
                edit.putInt("WIDGET$i", c[Calendar.MONTH])
                edit.putInt("WIDGETYEAR$i", c[Calendar.YEAR])
            }
            edit.apply()
            onUpdate(context, appWidgetManager, ids)
        }
        if (intent.action == munPlus || intent.action == munMinus) {
            val mAppWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                var tekmun = chin.getInt("WIDGET$mAppWidgetId", c[Calendar.MONTH])
                var tekyear = chin.getInt("WIDGETYEAR$mAppWidgetId", c[Calendar.YEAR])
                val edit = chin.edit()
                if (intent.action == munPlus) {
                    if (tekmun < 11) edit.putInt("WIDGET$mAppWidgetId", ++tekmun)
                    else {
                        edit.putInt("WIDGET$mAppWidgetId", 0)
                        edit.putInt("WIDGETYEAR$mAppWidgetId", ++tekyear)
                    }
                } else {
                    if (tekmun > 0) edit.putInt("WIDGET$mAppWidgetId", --tekmun)
                    else {
                        edit.putInt("WIDGET$mAppWidgetId", 11)
                        edit.putInt("WIDGETYEAR$mAppWidgetId", --tekyear)
                    }
                }
                edit.apply()
                val reset = Intent(context, WidgetMun::class.java)
                reset.action = SettingsActivity.RESET_WIDGET_MUN
                reset.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                val pReset = PendingIntent.getBroadcast(context, 257, reset, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pReset)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
                    }
                    else -> {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 120000, pReset)
                    }
                }
                mun(context, AppWidgetManager.getInstance(context), mAppWidgetId)
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

    private fun mun(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val updateViews = RemoteViews(context.packageName, R.layout.widget_mun)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val c = Calendar.getInstance()
        val cYear = SettingsActivity.GET_CALIANDAR_YEAR_MAX
        val tecmun = chin.getInt("WIDGET$widgetID", c[Calendar.MONTH])
        val tecyear = chin.getInt("WIDGETYEAR$widgetID", SettingsActivity.GET_CALIANDAR_YEAR_MAX)
        val monthName = context.resources.getStringArray(R.array.meciac2)
        if (tecyear == c[Calendar.YEAR]) {
            val spannableString = SpannableString(monthName[tecmun])
            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (tecmun == c[Calendar.MONTH] && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) updateViews.setTextViewText(R.id.Mun_widget, spannableString)
            else updateViews.setTextViewText(R.id.Mun_widget, monthName[tecmun])
        } else {
            updateViews.setTextViewText(R.id.Mun_widget, monthName[tecmun] + ", " + tecyear)
        }
        if (cYear == tecyear && tecmun == 11) updateViews.setViewVisibility(R.id.imageButton2, View.INVISIBLE)
        else updateViews.setViewVisibility(R.id.imageButton2, View.VISIBLE)
        if (SettingsActivity.GET_CALIANDAR_YEAR_MIN == tecyear && tecmun == 0) updateViews.setViewVisibility(R.id.imageButton, View.INVISIBLE)
        else updateViews.setViewVisibility(R.id.imageButton, View.VISIBLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val dzenNoch = getBaseDzenNoch(context, widgetID)
            if (dzenNoch) {
                updateViews.setTextColor(R.id.Mun_widget, ContextCompat.getColor(context, R.color.colorWhite))
                updateViews.setInt(R.id.root, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorbackground_material_dark))
                updateViews.setImageViewResource(R.id.imageButton, R.drawable.levo_catedra_31)
                updateViews.setImageViewResource(R.id.imageButton2, R.drawable.pravo_catedra_31)
            } else {
                updateViews.setTextColor(R.id.Mun_widget, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews.setInt(R.id.root, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorWhite))
                updateViews.setImageViewResource(R.id.imageButton, R.drawable.levo_catedra_blak_31)
                updateViews.setImageViewResource(R.id.imageButton2, R.drawable.pravo_catedra_blak_31)
            }
        }
        val updateIntent = Intent(context, WidgetMun::class.java)
        updateIntent.action = munPlus
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        val pIntentButton2 = PendingIntent.getBroadcast(context, widgetID, updateIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews.setOnClickPendingIntent(R.id.imageButton2, pIntentButton2)
        val countIntent = Intent(context, WidgetMun::class.java)
        countIntent.action = munMinus
        countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        val pIntentButton = PendingIntent.getBroadcast(context, widgetID, countIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews.setOnClickPendingIntent(R.id.imageButton, pIntentButton)
        updateViews.setViewVisibility(R.id.nedel5, View.VISIBLE)
        updateViews.setViewVisibility(R.id.nedel6, View.VISIBLE)
        val month = chin.getInt("WIDGET$widgetID", c[Calendar.MONTH])
        val year = chin.getInt("WIDGETYEAR$widgetID", c[Calendar.YEAR])
        val data = MenuCaliandar.getDataCalaindar(mun = month, year = year)
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
                val calendarPost = GregorianCalendar(year, month, i)
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
                updateViews.setViewVisibility(R.id.nedel6, View.GONE)
            }
            if (munAll + wik == 29) {
                updateViews.setViewVisibility(R.id.nedel5, View.GONE)
            }
            val calendarPost = GregorianCalendar(year, month, i)
            val widgetMun = "widget_mun"
            val dayIntent = Intent(context, SplashActivity::class.java)
            dayIntent.putExtra(widgetMun, true)
            when (day) {
                "start" -> {
                    val position = data[0][25].toInt() - (wik - e)
                    dayIntent.putExtra("position", position)
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthOld + "" + oldDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews.setOnClickPendingIntent(idView(e), pIntent)
                    updateViews.setTextViewText(idView(e), oldDay.toString())
                    if (e == 1) updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta)
                    else updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day)
                    updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorSecondary_text))
                }
                "end" -> {
                    val position = data[data.size - 1][25].toInt() + newDay
                    dayIntent.putExtra("position", position)
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + mouthNew + "" + newDay
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews.setOnClickPendingIntent(idView(e), pIntent)
                    updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorSecondary_text))
                    updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day)
                    updateViews.setTextViewText(idView(e), newDay.toString())
                }
                else -> {
                    updateViews.setTextViewText(idView(e), i.toString())
                    if (data[i - 1][5].contains("1")) {
                        if (munActual == i && munTudey) {
                            updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorWhite))
                    } else if (data[i - 1][5].contains("2")) {
                        if (munActual == i && munTudey) {
                            updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red_today)
                        } else {
                            updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_red)
                        }
                        updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorWhite))
                    } else {
                        if (nopost) {
                            if (munActual == i && munTudey) updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta_today)
                            else updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta)
                            updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary_text))
                        }
                        if (post) {
                            if (munActual == i && munTudey) updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_post_today)
                            else updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_post)
                            updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary_text))
                        }
                        if (strogiPost) {
                            if (munActual == i && munTudey) {
                                updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_strogi_post_today)
                            } else {
                                updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_strogi_post)
                            }
                            updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorWhite))
                        }
                        if (!nopost && !post && !strogiPost) {
                            denNedeli = calendarPost[Calendar.DAY_OF_WEEK]
                            if (denNedeli == 1) {
                                if (munActual == i && munTudey) updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta_today)
                                else updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_bez_posta)
                                updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary))
                            } else {
                                if (munActual == i && munTudey) updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day_today)
                                else updateViews.setInt(idView(e), "setBackgroundResource", R.drawable.calendar_day)
                                updateViews.setTextColor(idView(e), ContextCompat.getColor(context, R.color.colorPrimary_text))
                            }
                        }
                    }
                    if (data[i - 1][4].contains("<font color=#d00505><strong>")) {
                        val spannableString = SpannableString(i.toString())
                        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        updateViews.setTextViewText(idView(e), spannableString)
                    }
                    val position = data[i - 1][25].toInt()
                    dayIntent.putExtra("position", position)
                    dayIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val code = year.toString() + "" + month + "" + i
                    val pIntent = PendingIntent.getActivity(context, code.toInt(), dayIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    updateViews.setOnClickPendingIntent(idView(e), pIntent)
                }
            }
        }
        appWidgetManager.updateAppWidget(widgetID, updateViews)
    }
}