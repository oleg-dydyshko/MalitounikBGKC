package by.carkva_gazeta.malitounik

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.hardware.SensorEvent
import android.os.Bundle


class WidgetConfig : BaseActivity(), DialogWidgetConfig.DialogWidgetConfigListener {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        setResult(Activity.RESULT_OK)
        val config = DialogWidgetConfig.getInstance(widgetID, false)
        config.show(supportFragmentManager, "config")
    }

    override fun onDialogWidgetConfigPositiveClick() {
        val resultValue = Intent(this, Widget::class.java)
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        setResult(Activity.RESULT_OK, resultValue)
        sendBroadcast(resultValue)
        finish()
    }

    override fun setMyTheme() {
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }
}