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
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import java.util.Calendar

class Widget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (widgetID in appWidgetIds) {
            kaliandar(context, appWidgetManager, widgetID)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit().putBoolean("WIDGET_ENABLED", true).apply()
        val intent = Intent(context, Widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_IMMUTABLE or 0)
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
        val edit = sp.edit()
        for ((key) in sp.all) {
            if (key.contains("dzen_noch_widget_day")) {
                edit.remove(key)
            }
        }
        edit.putBoolean("WIDGET_ENABLED", false)
        edit.apply()
        val intent = Intent(context, Widget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pIntent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pIntent)
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
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val extSettings = intent.extras?.getBoolean("settings", false) ?: false
        if (extSettings) {
            isSettingsCulling = true
            val settings = Intent(context, WidgetConfig::class.java)
            settings.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            settings.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            context.startActivity(settings)
        }
        if (intent.extras?.getBoolean("actionEndLoad", false) == true) {
            isSettingsCulling = false
        }
        if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            kaliandar(context, appWidgetManager, widgetID)
        }
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val thisAppWidget = ComponentName(context.packageName, javaClass.name)
            val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
            onUpdate(context, appWidgetManager, ids)
            val intentUpdate = Intent(context, Widget::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pIntent = PendingIntent.getBroadcast(context, 50, intentUpdate, PendingIntent.FLAG_IMMUTABLE or 0)
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
    }

    private fun prazdnik(context: Context, updateViews: RemoteViews) {
        updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPrimary))
        updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorWhite))
        updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPrimary))
        updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPrimary))
        updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorWhite))
        updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorWhite))
    }

    private fun getBaseDzenNoch(context: Context, widgetID: Int): Boolean {
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val modeNight = k.getInt("mode_night_widget_day$widgetID", SettingsActivity.MODE_NIGHT_SYSTEM)
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

    private fun kaliandar(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val updateViews = RemoteViews(context.packageName, R.layout.widget)
        val g = Calendar.getInstance()
        val data = MenuCaliandar.getDataCalaindar(g[Calendar.DATE])
        val dzenNoch = getBaseDzenNoch(context, widgetID)
        val rColorColorPrimaryText: Int
        val rColorColorPrimary: Int
        if (dzenNoch) {
            rColorColorPrimary = R.color.colorPrimary_black
            rColorColorPrimaryText = R.color.colorWhite
        } else {
            rColorColorPrimary = R.color.colorPrimary
            rColorColorPrimaryText = R.color.colorPrimary_text
        }
        val month = data[0][2].toInt()
        val dayofmounth = data[0][1].toInt()
        val nedel = data[0][0].toInt()
        val intent = Intent(context, SplashActivity::class.java)
        val widgetDay = "widget_day"
        intent.putExtra(widgetDay, true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pIntent = PendingIntent.getActivity(context, 500, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews.setOnClickPendingIntent(R.id.fullCaliandar, pIntent)
        val settings = Intent(context, Widget::class.java)
        settings.putExtra("settings", true)
        settings.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        val pSsettings = PendingIntent.getBroadcast(context, 40 + widgetID, settings, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        updateViews.setOnClickPendingIntent(R.id.settings, pSsettings)
        if (dzenNoch) {
            updateViews.setInt(R.id.Layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorbackground_material_dark))
            updateViews.setTextColor(R.id.textSviatyia, ContextCompat.getColor(context, R.color.colorWhite))
        } else {
            updateViews.setInt(R.id.Layout, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setTextColor(R.id.textSviatyia, ContextCompat.getColor(context, R.color.colorPrimary_text))
        }
        if (isSettingsCulling) {
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView7, R.drawable.load_kalendar_black)
            else updateViews.setImageViewResource(R.id.imageView7, R.drawable.load_kalendar)
        } else {
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView7, R.drawable.settings)
            else updateViews.setImageViewResource(R.id.imageView7, R.drawable.settings_black)
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
        updateViews.setViewVisibility(R.id.textSviatyia, View.GONE)
        updateViews.setTextViewText(R.id.textChislo, dayofmounth.toString())
        if (data[0][7].toInt() == 1) {
            updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorPrimary_text))
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorBezPosta))
        }
        if (!(nedel == 1 || nedel == 7)) {
            if (data[0][7].toInt() == 1) {
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
            if (data[0][7].toInt() == 2) {
                updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
                updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            }
        }
        if (!data[0][6].contains("no_sviaty")) {
            val svita = data[0][6].replace("\n", "<br>")
            if (data[0][5].contains("1")) updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml("<strong>$svita</strong>"))
            else updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml(svita))
            updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.VISIBLE)
        }
        if (data[0][6].contains("Пачатак") || data[0][6].contains("Вялікі") || data[0][6].contains("Вялікая") || data[0][6].contains("ВЕЧАР") || data[0][6].contains("Палова")) {
            updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimaryText))
            updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml(data[0][6]))
            updateViews.setViewVisibility(R.id.textCviatyGlavnyia, View.VISIBLE)
        }
        if (data[0][6].contains("Сьветл")) {
            updateViews.setTextViewText(R.id.textCviatyGlavnyia, MainActivity.fromHtml("<strong>${data[0][6]}</strong>"))
        }
        var dataSviatyia = ""
        if (!data[0][4].contains("no_sviatyia")) {
            dataSviatyia = data[0][4]
        }
        if (data[0][8] != "") {
            dataSviatyia = data[0][8] + "<br>" + dataSviatyia
        }
        if (data[0][18] == "1") {
            dataSviatyia = dataSviatyia + "<br><strong>" + context.getString(R.string.pamerlyia) + "</strong>"
        }
        if (dataSviatyia != "") {
            if (dzenNoch) dataSviatyia = dataSviatyia.replace("#d00505", "#ff6666")
            updateViews.setTextViewText(R.id.textSviatyia, MainActivity.fromHtml(dataSviatyia))
            updateViews.setViewVisibility(R.id.textSviatyia, View.VISIBLE)
        }
        if (data[0][7].contains("2")) {
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorPost))
            if (nedel == 6) {
                updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
                updateViews.setViewVisibility(R.id.imageView4, View.VISIBLE)
            }
        } else if (data[0][7].contains("3")) {
            updateViews.setInt(R.id.textDenNedeli, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textDenNedeli, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setInt(R.id.textChislo, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textChislo, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setInt(R.id.textMesiac, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textMesiac, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setTextViewText(R.id.textPost, "Строгі пост")
            updateViews.setInt(R.id.textPost, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorStrogiPost))
            updateViews.setTextColor(R.id.textPost, ContextCompat.getColor(context, R.color.colorWhite))
            updateViews.setViewVisibility(R.id.textPost, View.VISIBLE)
            updateViews.setViewVisibility(R.id.imageView4, View.VISIBLE)
            if (dzenNoch) updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_red_black) else updateViews.setImageViewResource(R.id.imageView4, R.drawable.fishe_red)
        }
        if (data[0][5].contains("1") || data[0][5].contains("2") || data[0][5].contains("3")) {
            updateViews.setTextColor(R.id.textCviatyGlavnyia, ContextCompat.getColor(context, rColorColorPrimary))
            prazdnik(context, updateViews)
        }
        when (data[0][12].toInt()) {
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
        val nedelName = context.resources.getStringArray(R.array.dni_nedeli)
        updateViews.setTextViewText(R.id.textDenNedeli, nedelName[nedel])
        if (nedel == 1) prazdnik(context, updateViews)
        val monthName = context.resources.getStringArray(R.array.meciac)
        if (month == Calendar.OCTOBER) updateViews.setFloat(R.id.textMesiac, "setTextSize", 12f)
        else updateViews.setFloat(R.id.textMesiac, "setTextSize", 14f)
        if (nedel == Calendar.MONDAY) updateViews.setFloat(R.id.textDenNedeli, "setTextSize", 12f)
        else updateViews.setFloat(R.id.textDenNedeli, "setTextSize", 14f)
        updateViews.setTextViewText(R.id.textMesiac, monthName[month])
        appWidgetManager.updateAppWidget(widgetID, updateViews)
    }

    companion object {
        private var isSettingsCulling = false
    }
}