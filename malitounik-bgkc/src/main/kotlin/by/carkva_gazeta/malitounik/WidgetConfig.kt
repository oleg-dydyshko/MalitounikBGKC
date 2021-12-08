package by.carkva_gazeta.malitounik

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WidgetConfig : AppCompatActivity(), DialogWidgetConfig.DialogWidgetConfigListener {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultValue: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        resultValue = Intent()
        resultValue?.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        setResult(Activity.RESULT_CANCELED, resultValue)
        val config = DialogWidgetConfig.getInstance(widgetID)
        config.show(supportFragmentManager, "config")
    }

    override fun onDialogWidgetConfigPositiveClick() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        Widget.kaliandar(this, appWidgetManager, widgetID)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}