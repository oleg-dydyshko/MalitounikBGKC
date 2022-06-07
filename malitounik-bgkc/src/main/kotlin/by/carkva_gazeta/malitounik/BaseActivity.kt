package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var checkDzenNoch = false
    private var mLastClickTime: Long = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("mLastClickTime", mLastClickTime)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        checkDzenNoch = dzenNoch
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mLastClickTime = savedInstanceState.getLong("mLastClickTime")
        }
    }

    override fun onPause() {
        super.onPause()
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        if (k.getBoolean("auto_dzen_noch", false)) {
            setlightSensor()
        } else {
            dzenNoch = k.getBoolean("dzen_noch", false)
            if (checkDzenNoch != dzenNoch) recreate()
        }
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    open fun checkAutoDzenNoch() {}

    private fun sensorChangeDzenNoch(isDzenNoch: Boolean) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 10000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val prefEditor = k.edit()
        prefEditor.putBoolean("dzen_noch", isDzenNoch)
        prefEditor.apply()
        dzenNoch = isDzenNoch
        if (isDzenNoch != checkDzenNoch) {
            checkDzenNoch = isDzenNoch
            checkAutoDzenNoch()
            recreate()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.values[0] <= 4f && !dzenNoch) {
                sensorChangeDzenNoch(true)
            }
            if (sensorEvent.values[0] >= 21f && dzenNoch) {
                sensorChangeDzenNoch(false)
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