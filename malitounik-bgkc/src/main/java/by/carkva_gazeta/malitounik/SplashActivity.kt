package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by oleg on 22.4.17
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.data
        val intent1 = Intent(this, MainActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val extras = intent.extras
        if (extras != null) {
            val widgetMun = "widget_mun"
            if (extras.getBoolean(widgetMun, false)) {
                intent1.putExtra(widgetMun, true)
                intent1.putExtra("DayYear", extras.getInt("DayYear"))
                intent1.putExtra("Year", extras.getInt("Year"))
            }
            val widgetDay = "widget_day"
            if (extras.getBoolean(widgetDay, false)) {
                intent1.putExtra(widgetDay, true)
            }
            if (extras.getBoolean("sabytie", false)) {
                intent1.putExtra("data", extras.getInt("data"))
                intent1.putExtra("year", extras.getInt("year"))
                intent1.putExtra("sabytie", true)
                intent1.putExtra("sabytieView", extras.getBoolean("sabytieView", false))
                intent1.putExtra("sabytieTitle", extras.getString("sabytieTitle"))
            }
        }
        if (data != null) {
            intent1.data = data
        }
        startActivity(intent1)
        finish()
    }
}