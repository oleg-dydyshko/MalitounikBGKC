package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.SensorEvent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Surface
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SplashActivityBinding
import java.io.File
import java.io.FileOutputStream


@SuppressLint("CustomSplashScreen") class SplashActivity : BaseActivity() {
    private var sensor = -1f

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
        lockOrientation()
        Handler(Looper.getMainLooper()).postDelayed({
            if (sensor != -1f) {
                val result = when {
                    sensor <= 4f -> true
                    sensor >= 21f -> false
                    else -> {
                        val configuration = Resources.getSystem().configuration
                        configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                    }
                }
                val prefEditors = k.edit()
                prefEditors.putBoolean("dzen_noch", result)
                prefEditors.apply()
            }
            startActivity(intent1)
            finish()
        }, 500)
    }

    private fun lockOrientation() {
        val display = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION") windowManager.defaultDisplay
        }
        val rotation = display.rotation
        val currentOrientation = resources.configuration.orientation
        var orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
        requestedOrientation = orientation
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            sensor = sensorEvent.values[0]
        }
    }
}