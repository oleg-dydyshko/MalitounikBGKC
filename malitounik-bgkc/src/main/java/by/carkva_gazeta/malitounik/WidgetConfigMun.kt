package by.carkva_gazeta.malitounik

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.hardware.SensorEvent
import android.os.Bundle


class WidgetConfigMun : BaseActivity(), DialogWidgetConfig.DialogWidgetConfigListener {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        setResult(Activity.RESULT_CANCELED)
        val config = DialogWidgetConfig.getInstance(widgetID, true)
        config.show(supportFragmentManager, "config")
    }

    override fun onDialogWidgetConfigPositiveClick() {
        val resultValue = Intent(this, WidgetMun::class.java)
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        sendBroadcast(resultValue)
        finish()
    }

    override fun setMyTheme() {
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }
}