package by.carkva_gazeta.malitounik

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity

abstract class PreBaseActivity : AppCompatActivity(), SensorEventListener {

    abstract fun sensorChangeDzenNoch(isDzenNoch: Boolean)

    override fun onSensorChanged(event: SensorEvent?) {
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor = k.edit()
        event?.let { sensorEvent ->
            if (sensorEvent.values[0] <= 4f && !dzenNoch) {
                prefEditor.putBoolean("dzen_noch", true)
                prefEditor.apply()
                sensorChangeDzenNoch(true)
            }
            if (sensorEvent.values[0] >= 21f && dzenNoch) {
                prefEditor.putBoolean("dzen_noch", false)
                prefEditor.apply()
                sensorChangeDzenNoch(false)
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