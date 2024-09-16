package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.SensorEvent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SplashActivityBinding
import java.io.File
import java.io.FileOutputStream

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (getBaseDzenNoch()) {
            binding.constraintlayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
            binding.imageView.setImageResource(R.drawable.logotip_splash_black)
        }
        if (SettingsActivity.isLightSensorExist() && k.getInt("mode_night", SettingsActivity.MODE_NIGHT_SYSTEM) == SettingsActivity.MODE_NIGHT_AUTO) {
            setlightSensor()
        }
        val data = intent.data
        val intent1 = Intent(this, MainActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val extras = intent.extras
        if (extras != null) {
            intent1.putExtras(extras)
            val widgetMun = "widget_mun"
            if (extras.getBoolean(widgetMun, false)) {
                intent1.putExtra(widgetMun, true)
            }
            val widgetDay = "widget_day"
            if (extras.getBoolean(widgetDay, false)) {
                intent1.putExtra(widgetDay, true)
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
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent1)
            finish()
        }, 500)
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }
}