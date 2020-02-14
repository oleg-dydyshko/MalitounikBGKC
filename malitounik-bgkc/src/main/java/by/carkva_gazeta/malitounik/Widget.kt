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
 * Created by oleg on 17.11.16
 */
class Widget : AppWidgetProvider() {
    private val updateAllWidgets = "update_all_widgets"
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        //RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        for (widgetID in appWidgetIds) {
            kaliandar(context, appWidgetManager, widgetID)
        }
        // Обновляем виджет
//appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit().putBoolean("WIDGET_ENABLED", true).apply()
        val intent = Intent(context, Widget::class.java)
        intent.action = updateAllWidgets
        val pIntentBoot = PendingIntent.getBroadcast(context, 51, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, 0)
        val c = Calendar.getInstance() as GregorianCalendar
        //if (alarmManager != null) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000, pIntentBoot)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]), pIntent)
            }
            else -> {
                alarmManager[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 300000] = pIntentBoot
                alarmManager[AlarmManager.RTC_WAKEUP, mkTime(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])] = pIntent
            }
        }
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mkTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)), 86400000, pIntent);
//}
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val editor = context.getSharedPreferences("biblia", Context.MODE_PRIVATE).edit()
        for (widgetID in appWidgetIds) {
            editor.remove("dzen_noch_widget_day$widgetID")
        }
        editor.apply()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit().putBoolean("WIDGET_ENABLED", false).apply()
        val intent = Intent(context, Widget::class.java)
        intent.action = updateAllWidgets
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, 0)
        val pIntentBoot = PendingIntent.getBroadcast(context, 51, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //if (alarmManager != null) {
        alarmManager.cancel(pIntent)
        alarmManager.cancel(pIntentBoot)
        //}
    }

    private fun mkTime(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar[year, month, day, 0, 0] = 0
        return calendar.timeInMillis
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.equals(updateAllWidgets, ignoreCase = true)) {
            val thisAppWidget = ComponentName(
                    context.packageName, javaClass.name)
            val appWidgetManager = AppWidgetManager
                    .getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            onUpdate(context, appWidgetManager, ids)
            val intentUpdate = Intent(context, Widget::class.java)
            intentUpdate.action = updateAllWidgets
            val c = Calendar.getInstance() as GregorianCalendar
            c.add(Calendar.DATE, 1)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 50, intentUpdate, 0)
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
    }

    companion object {
        private fun prazdnik(context: Context, updateViews: RemoteViews, R_color_colorPrimary: Int) {
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R_color_colorPrimary))
            updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorIcons))
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R_color_colorPrimary))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R_color_colorPrimary))
            updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorIcons))
            updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorIcons))
        }

        private fun getmun(): Int {
            val g = Calendar.getInstance() as GregorianCalendar
            val position = (g[Calendar.YEAR] - SettingsActivity.GET_CALIANDAR_YEAR_MIN) * 12 + g[Calendar.MONTH]
            val count = (SettingsActivity.GET_CALIANDAR_YEAR_MAX - SettingsActivity.GET_CALIANDAR_YEAR_MIN + 1) * 12
            for (i in 0 until count) {
                if (position == i) {
                    return position
                }
            }
            return position
        }

        fun kaliandar(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
            val updateViews = RemoteViews(context.packageName, R.layout.widget)
            //val tileMe = BitmapDrawable(context.resources, BitmapFactory.decodeResource(context.resources, R.drawable.calendar_fon))
            //tileMe.tileModeX = Shader.TileMode.REPEAT
            val inputStream = context.resources.openRawResource(MainActivity.caliandar(context, getmun()))
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
            val data: ArrayList<ArrayList<String>> = gson.fromJson(reader.readText(), type)
            isr.close()
            val g = Calendar.getInstance() as GregorianCalendar
            val day = g[Calendar.DATE] - 1
            val calendar = GregorianCalendar(data[day][3].toInt(), data[day][2].toInt(), data[day][1].toInt())
            val dzenNoch = context.getSharedPreferences("biblia", Context.MODE_PRIVATE).getBoolean("dzen_noch_widget_day$widgetID", false)
            val rColorColorPrimaryText: Int
            val rColorColorPrimary: Int
            if (dzenNoch) {
                rColorColorPrimary = R.color.colorPrimary_black
                rColorColorPrimaryText = R.color.colorIcons
            } else {
                rColorColorPrimary = R.color.colorPrimary
                rColorColorPrimaryText = R.color.colorPrimary_text
            }
            val month = calendar[Calendar.MONTH]
            val dayofmounth = calendar[Calendar.DAY_OF_MONTH]
            val nedel = calendar[Calendar.DAY_OF_WEEK]
            val intent = Intent(context, SplashActivity::class.java)
            val widgetDay = "widget_day"
            intent.putExtra(widgetDay, true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pIntent = PendingIntent.getActivity(context, 500, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            updateViews.setOnClickPendingIntent(R.id.fullCaliandar, pIntent)
            val settings = Intent(context, WidgetConfig::class.java)
            settings.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            settings.action = "android.appwidget.action.APPWIDGET_CONFIGURE"
            settings.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            val pSsettings = PendingIntent.getActivity(context, 1000 + widgetID, settings, PendingIntent.FLAG_UPDATE_CURRENT)
            updateViews.setOnClickPendingIntent(R.id.settings, pSsettings)
            if (dzenNoch) {
                updateViews.setInt(R.id.Layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorbackground_material_dark))
                updateViews.setTextColor(R.id.textSviatyia, ContextCompat.getColor(context, R.color.colorIcons))
                updateViews.setImageViewResource(R.id.imageView7, R.drawable.settings)
            } else {
                updateViews.setInt(R.id.Layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorIcons))
                updateViews.setTextColor(R.id.textSviatyia, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews.setImageViewResource(R.id.imageView7, R.drawable.settings_black)
            }
            updateViews.setTextViewText(R.id.textPost, "Пост")
            updateViews.setViewVisibility(R.id.textPost, View.GONE)
            updateViews.setTextColor(R.id.textPost, ContextCompat.getColor(context, R.color.colorPrimary_text))
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_whate) else updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe)
            updateViews.setViewVisibility(R.id.imageView4, View.GONE)
            updateViews.setViewVisibility(R.id.znakTipicona, View.GONE)
            updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.GONE)
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorDivider))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorDivider))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorDivider))
            updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimary))
            updateViews.setViewVisibility(R.id.textSviatyia, View.VISIBLE)
            updateViews.setTextViewText(R.id.textChislo, dayofmounth.toString())
            if (data[day][7].toInt() == 1) {
                updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorPrimary_text))
                updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
            }
            if (!(nedel == 1 || nedel == 7)) {
                if (data[day][7].toInt() == 1) {
                    updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                    updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                    updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                    if (nedel == 6) {
                        updateViews.setTextViewText(R.id.textPost, "Посту няма")
                        updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
                        updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
                    }
                }
            }
            if (!(nedel == 1 || nedel == 7)) {
                if (data[day][7].toInt() == 2) {
                    updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                    updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                    updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                }
            }
            if (!data[day][6].contains("no_sviaty")) {
                val svita = data[day][6].replace("\n", "<br>")
                if (data[day][5].contains("1")) updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml("<strong>$svita</strong>")) else updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml(svita))
                updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.VISIBLE)
            }
            if (data[day][6].contains("Пачатак") || data[day][6].contains("Вялікі") || data[day][6].contains("Вялікая") || data[day][6].contains("убот") || data[day][6].contains("ВЕЧАР") || data[day][6].contains("Палова")) {
                updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimaryText))
                updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml(data[day][6]))
                updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.VISIBLE)
            }
            var dataSviatyia = ""
            if (!data[day][4].contains("no_sviatyia")) {
                dataSviatyia = data[day][4]
                if (dzenNoch) dataSviatyia = dataSviatyia.replace("#d00505", "#f44336")
                updateViews.setTextViewText(R.id.textSviatyia, MainActivity.fromHtml(dataSviatyia))
            } else {
                updateViews.setViewVisibility(R.id.textSviatyia, View.GONE)
            }
            if (data[day][8] != "") {
                updateViews.setTextViewText(R.id.textSviatyia, MainActivity.fromHtml(data[day][8] + ";<br>" + dataSviatyia))
                updateViews.setViewVisibility(R.id.textSviatyia, View.VISIBLE)
            }
            if (data[day][7].contains("2")) {
                updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                if (nedel == 6) {
                    updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
                    updateViews.setViewVisibility(R.id.imageView4, View.VISIBLE)
                }
            } else if (data[day][7].contains("3")) {
                updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
                updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorIcons))
                updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
                updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorIcons))
                updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
                updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorIcons))
                updateViews.setTextViewText(R.id.textPost, "Строгі пост")
                updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
                updateViews.setTextColor(R.id.textPost, ContextCompat.getColor(context, R.color.colorIcons))
                updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
                updateViews.setViewVisibility(R.id.imageView4, View.VISIBLE)
                if (dzenNoch) updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_red_black) else updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_red)
            }
            if (data[day][5].contains("1") || data[day][5].contains("2") || data[day][5].contains("3")) {
                updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimary))
                prazdnik(context, updateViews, rColorColorPrimary)
            }
            when (data[day][12].toInt()) {
                1 -> {
                    if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest_black) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest)
                    updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
                }
                2 -> {
                    var rDrawableZnakiKrestVKruge = R.drawable.znaki_krest_v_kruge
                    if (dzenNoch) rDrawableZnakiKrestVKruge = R.drawable.znaki_krest_v_kruge_black
                    updateViews.setImageViewResource(R.id.znakTipicona, rDrawableZnakiKrestVKruge)
                    updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
                }
                3 -> {
                    if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest_v_polukruge_black) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_krest_v_polukruge)
                    updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
                }
                4 -> {
                    if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk_black_black) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk)
                    updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
                }
                5 -> {
                    if (dzenNoch) updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk_whate) else updateViews.setImageViewResource(R.id.znakTipicona, R.drawable.znaki_ttk_black)
                    updateViews.setViewVisibility(R.id.znakTipicona, View.VISIBLE)
                }
            }
            val nedelName = arrayOf("", "нядзеля", "панядзелак", "аўторак", "серада", "чацьвер", "пятніца", "субота")
            updateViews.setTextViewText(R.id.textDenNedeli, nedelName[nedel])
            if (nedel == 1) prazdnik(context, updateViews, rColorColorPrimary)
            val monthName = arrayOf("СТУДЗЕНЯ", "ЛЮТАГА", "САКАВІКА", "КРАСАВІКА", "ТРАЎНЯ", "ЧЭРВЕНЯ",
                    "ЛІПЕНЯ", "ЖНІЎНЯ", "ВЕРАСЬНЯ", "КАСТРЫЧНІКА", "ЛІСТАПАДА", "СЬНЕЖНЯ")
            updateViews.setTextViewText(R.id.textMesiac, monthName[month])
            appWidgetManager.updateAppWidget(widgetID, updateViews)
            //appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), Widget.class.getName()), updateViews);
        }
    }
}