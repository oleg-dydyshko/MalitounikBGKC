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


class WidgetRadyjoMaryia : AppWidgetProvider(), ServiceRadyjoMaryia.ServiceRadyjoMaryiaListener {

    private var mRadyjoMaryiaService: ServiceRadyjoMaryia? = null
    private var isFirstRun = false
    private var isProgram = false

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
        val extra = intent.extras?.getInt("action")
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun && extra == ServiceRadyjoMaryia.STOP) {
            val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
            intent2.putExtra("action", ServiceRadyjoMaryia.STOP)
            context.startService(intent2)
        }
        if (extra == ServiceRadyjoMaryia.PLAY_PAUSE) {
            if (!ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                isFirstRun = true
            }
            val intent2 = Intent(context, ServiceRadyjoMaryia::class.java)
            intent2.putExtra("action", ServiceRadyjoMaryia.PLAY_PAUSE)
            context.startService(intent2)
        }
        if (extra == 20) {
            isProgram = true
            val intent2 = Intent(context, WidgetRadyjoMaryiaProgram::class.java)
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent2)
        }
        if (extra == 30) {
            isProgram = false
        }
        update()
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
        intent3.putExtra("action", 20)
        val pIntent3 = PendingIntent.getBroadcast(context, 20, intent3, flags)
        updateViews.setOnClickPendingIntent(R.id.program, pIntent3)
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
            val service = peekService(context, Intent(context, ServiceRadyjoMaryia::class.java))
            val binder = service as? ServiceRadyjoMaryia.ServiceRadyjoMaryiaBinder
            mRadyjoMaryiaService = binder?.getService()
            mRadyjoMaryiaService?.setServiceRadyjoMaryiaListener(this@WidgetRadyjoMaryia)
            if (mRadyjoMaryiaService?.isPlayingRadioMaria() == true) {
                updateViews.setImageViewResource(R.id.play, R.drawable.pause3)
            } else {
                updateViews.setImageViewResource(R.id.play, R.drawable.play3)
            }
            updateViews.setTextViewText(R.id.textView, mRadyjoMaryiaService?.getTitleProgramRadioMaria() ?: context.getString(R.string.padie_maryia_s))
        } else {
            updateViews.setTextViewText(R.id.textView, context.getString(R.string.padie_maryia_s))
            updateViews.setImageViewResource(R.id.play, R.drawable.play3)
        }
        if (isFirstRun) {
            updateViews.setImageViewResource(R.id.play, R.drawable.load)
        }
        if (isProgram) {
            updateViews.setImageViewResource(R.id.program, R.drawable.load)
        } else {
            updateViews.setImageViewResource(R.id.program, R.drawable.programm_rado_maria2)
        }
        updateViews.setViewVisibility(R.id.stop, View.VISIBLE)
        updateViews.setViewVisibility(R.id.play, View.VISIBLE)
        updateViews.setViewVisibility(R.id.program, View.VISIBLE)
        appWidgetManager.updateAppWidget(appWidgetId, updateViews)
    }

    override fun setTitleRadioMaryia(title: String) {
        isFirstRun = false
        update()
    }

    override fun unBinding() {
    }

    override fun playingRadioMaria(isPlayingRadioMaria: Boolean) {
        update()
    }

    override fun playingRadioMariaStateReady() {
    }

    private fun update() {
        val thisAppWidget = ComponentName(Malitounik.applicationContext().packageName, javaClass.name)
        val appWidgetManager = AppWidgetManager.getInstance(Malitounik.applicationContext())
        val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
        onUpdate(Malitounik.applicationContext(), appWidgetManager, ids)
    }
}