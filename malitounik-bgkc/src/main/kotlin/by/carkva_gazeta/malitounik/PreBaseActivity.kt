package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class PreBaseActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false

    abstract fun sensorChangeDzenNoch(isDzenNoch: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
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

    override fun onPause() {
        super.onPause()
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getBoolean("auto_dzen_noch", false)) setlightSensor()
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