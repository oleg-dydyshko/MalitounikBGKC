package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import java.io.File
import java.io.FileOutputStream

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getBoolean("auto_dzen_noch", false)) {
            setlightSensor()
        }
        val data = intent.data
        val intent1 = Intent(this, MainActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("site")) intent1.putExtra("site", true)
            val widgetMun = "widget_mun"
            if (extras.getBoolean(widgetMun, false)) {
                intent1.putExtra(widgetMun, true)
                intent1.putExtra("position", extras.getInt("position"))
            }
            val widgetDay = "widget_day"
            if (extras.getBoolean(widgetDay, false)) {
                intent1.putExtra(widgetDay, true)
            }
            if (extras.getBoolean("sabytie", false)) {
                intent1.putExtra("data", extras.getInt("data"))
                intent1.putExtra("year", extras.getInt("year"))
            }
        }
        if (data != null) {
            intent1.data = data

            var file = ""
            val cursor2 = contentResolver.query(data, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
            cursor2?.moveToFirst()
            val nameIndex = cursor2?.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) ?: -1
            if (nameIndex >= 0) {
                file = cursor2?.getString(nameIndex) ?: ""
            }
            cursor2?.close()

            if (file != "") {
                val dir = File("$filesDir/Book")
                if (!dir.exists()) dir.mkdir()
                try {
                    val filePath = "$filesDir/Book/$file"
                    val inputStream = contentResolver.openInputStream(data)
                    val buffer = ByteArray(8192)
                    var count: Int
                    if (inputStream != null) {
                        FileOutputStream(filePath).use { fout -> while (inputStream.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count) }
                    }
                    intent1.putExtra("filePath", filePath)
                    inputStream?.close()
                } catch (_: Throwable) {
                }
            }
        }
        startActivity(intent1)
        finish()
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = false
}