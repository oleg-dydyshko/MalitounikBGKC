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
    private var autoDzenNoch = false
    private var checkDzenNoch = false
    private var mLastClickTime: Long = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("mLastClickTime", mLastClickTime)
        outState.putBoolean("autoDzenNoch", autoDzenNoch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (savedInstanceState != null) {
            mLastClickTime = savedInstanceState.getLong("mLastClickTime")
            autoDzenNoch = savedInstanceState.getBoolean("autoDzenNoch")
        }
        if (k.getBoolean("auto_dzen_noch", false)) {
            autoDzenNoch = startAutoDzenNoch
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        checkDzenNoch = getBaseDzenNoch()
        setMyTheme()
    }

    open fun setMyTheme() {
        if (k.getBoolean("auto_dzen_noch", false)) {
            if (autoDzenNoch) setTheme(R.style.AppCompatDarkSlider)
        } else {
            if (dzenNoch) setTheme(R.style.AppCompatDarkSlider)
        }
    }

    fun getCheckDzenNoch() = checkDzenNoch

    fun getBaseDzenNoch(): Boolean {
        return if (k.getBoolean("auto_dzen_noch", false)) autoDzenNoch
        else dzenNoch
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
        }
        if (checkDzenNoch != getBaseDzenNoch()) recreate()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
    }

    private fun sensorChangeDzenNoch(isDzenNoch: Boolean) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 10000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        autoDzenNoch = isDzenNoch
        startAutoDzenNoch = isDzenNoch
        recreate()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.values[0] <= 4f && !autoDzenNoch) {
                sensorChangeDzenNoch(true)
            }
            if (sensorEvent.values[0] >= 21f && autoDzenNoch) {
                sensorChangeDzenNoch(false)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun setlightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun removelightSensor() {
        val mySensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mySensorManager.unregisterListener(this, lightSensor)
    }

    companion object {
        private var startAutoDzenNoch = false
    }
}