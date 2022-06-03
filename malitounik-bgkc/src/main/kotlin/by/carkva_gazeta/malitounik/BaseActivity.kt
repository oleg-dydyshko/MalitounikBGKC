package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var checkDzenNoch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        checkDzenNoch = dzenNoch
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (checkDzenNoch != dzenNoch)
            recreate()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (k.getBoolean("auto_dzen_noch", false)) setlightSensor()
    }

    private fun sensorChangeDzenNoch(isDzenNoch: Boolean) {
        checkDzenNoch = isDzenNoch
        recreate()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.values[0] <= 4f && !dzenNoch) {
                val prefEditor = k.edit()
                prefEditor.putBoolean("dzen_noch", true)
                prefEditor.apply()
                sensorChangeDzenNoch(true)
                dzenNoch = true
            }
            if (sensorEvent.values[0] >= 21f && dzenNoch) {
                val prefEditor = k.edit()
                prefEditor.putBoolean("dzen_noch", false)
                prefEditor.apply()
                sensorChangeDzenNoch(false)
                dzenNoch = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun setlightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
    }

    private fun removelightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.unregisterListener(this, lightSensor)
    }
}