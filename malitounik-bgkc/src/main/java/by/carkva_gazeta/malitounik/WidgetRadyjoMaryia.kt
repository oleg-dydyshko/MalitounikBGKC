package by.carkva_gazeta.malitounik

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat


class WidgetRadyjoMaryia : AppWidgetProvider() {

    companion object {
        private var isFirstRun = false
        private var isProgram = false
        private var isError = false
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit().putBoolean("WIDGET_RADYJO_MARYIA_ENABLED", true).apply()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val sp = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        sp.edit().putBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false).apply()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val extra = intent.extras?.getInt("action", 0) ?: 0
        isError = intent.extras?.getBoolean("isError", false) ?: false
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun && extra == ServiceRadyjoMaryia.STOP) {
            val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
            intent2.putExtra("action", ServiceRadyjoMaryia.STOP)
            ContextCompat.startForegroundService(context, intent2)
        }
        val isInternet = MainActivity.isNetworkAvailable()
        if (extra == ServiceRadyjoMaryia.PLAY_PAUSE) {
            if (isInternet) {
                if (!ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                    isFirstRun = true
                }
                val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
                intent2.putExtra("action", ServiceRadyjoMaryia.PLAY_PAUSE)
                ContextCompat.startForegroundService(context, intent2)
            } else {
                val intent3 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
                intent3.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent3.putExtra("checkInternet", true)
                context.startActivity(intent3)
            }
        }
        if (extra == ServiceRadyjoMaryia.WIDGET_RADYJO_MARYIA_PROGRAM) {
            if (isInternet) {
                isProgram = true
                val intent2 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
                intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent2)
            } else {
                val intent3 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
                intent3.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent3.putExtra("checkInternet", true)
                context.startActivity(intent3)
            }
        }
        if (extra == ServiceRadyjoMaryia.WIDGET_RADYJO_MARYIA_PROGRAM_EXIT) {
            isProgram = false
        }
        if (extra == ServiceRadyjoMaryia.PLAYING_RADIO_MARIA_STATE_READY) {
            isFirstRun = false
        }
        update(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (widgetID in appWidgetIds) {
            radyjoMaryia(context, appWidgetManager, widgetID)
        }
    }

    private fun radyjoMaryia(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val updateViews = RemoteViews(context.packageName, R.layout.widget_radyjo_maryia)
        val intent = Intent(context, WidgetRadyjoMaryia::class.java)
        intent.putExtra("action", ServiceRadyjoMaryia.PLAY_PAUSE)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pIntent = PendingIntent.getBroadcast(context, ServiceRadyjoMaryia.PLAY_PAUSE, intent, flags)
        updateViews.setOnClickPendingIntent(R.id.play, pIntent)
        val intent2 = Intent(context, WidgetRadyjoMaryia::class.java)
        intent2.putExtra("action", ServiceRadyjoMaryia.STOP)
        val pIntent2 = PendingIntent.getBroadcast(context, ServiceRadyjoMaryia.STOP, intent2, flags)
        updateViews.setOnClickPendingIntent(R.id.stop, pIntent2)
        val intent3 = Intent(context, WidgetRadyjoMaryia::class.java)
        intent3.putExtra("action", ServiceRadyjoMaryia.WIDGET_RADYJO_MARYIA_PROGRAM)
        val pIntent3 = PendingIntent.getBroadcast(context, ServiceRadyjoMaryia.WIDGET_RADYJO_MARYIA_PROGRAM, intent3, flags)
        updateViews.setOnClickPendingIntent(R.id.program, pIntent3)
        if (isFirstRun) {
            updateViews.setImageViewResource(R.id.play, R.drawable.load)
        } else if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
            if (ServiceRadyjoMaryia.isPlayingRadyjoMaryia) {
                updateViews.setImageViewResource(R.id.play, R.drawable.pause3)
            } else {
                updateViews.setImageViewResource(R.id.play, R.drawable.play3)
            }
            val title = if (ServiceRadyjoMaryia.titleRadyjoMaryia != "") ServiceRadyjoMaryia.titleRadyjoMaryia
            else context.getString(R.string.padie_maryia_s)
            updateViews.setTextViewText(R.id.textView, title)
        } else {
            updateViews.setTextViewText(R.id.textView, context.getString(R.string.padie_maryia_s))
            updateViews.setImageViewResource(R.id.play, R.drawable.play3)
        }
        if (isProgram) {
            updateViews.setImageViewResource(R.id.program, R.drawable.load)
        } else {
            updateViews.setImageViewResource(R.id.program, R.drawable.programm_rado_maria2)
        }
        if (isError) {
            updateViews.setTextViewText(R.id.textView, context.getString(R.string.padie_maryia_s))
            updateViews.setImageViewResource(R.id.play, R.drawable.play3)
            updateViews.setImageViewResource(R.id.program, R.drawable.programm_rado_maria2)
        }
        updateViews.setViewVisibility(R.id.stop, View.VISIBLE)
        updateViews.setViewVisibility(R.id.play, View.VISIBLE)
        updateViews.setViewVisibility(R.id.program, View.VISIBLE)
        appWidgetManager.updateAppWidget(appWidgetId, updateViews)
    }

    private fun update(context: Context) {
        val thisAppWidget = ComponentName(context.packageName, javaClass.name)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
        onUpdate(context, appWidgetManager, ids)
    }
}