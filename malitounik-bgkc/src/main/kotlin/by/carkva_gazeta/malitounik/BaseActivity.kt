package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity(), SensorEventListener, MenuProvider {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var autoDzenNoch = false
    private var checkDzenNoch = false
    private var mLastClickTime: Long = 0
    private var startTimeJob1: Job? = null
    private var startTimeJob2: Job? = null
    private var startTimeJob3: Job? = null
    private var startTimeJob4: Job? = null
    private var startTimeDelay: Long = 5000

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    }

    override fun onMenuItemSelected(item: MenuItem) = false

    open fun onBack() {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("mLastClickTime", mLastClickTime)
        outState.putBoolean("autoDzenNoch", autoDzenNoch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addMenuProvider(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
        startTimeDelay = 0
        startTimeJob3?.cancel()
        startTimeJob3 = CoroutineScope(Dispatchers.IO).launch {
            delay(4000)
            startTimeDelay = 5000
        }
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
            if (autoDzenNoch) setTheme(R.style.AppCompatDark)
        } else {
            if (dzenNoch) setTheme(R.style.AppCompatDark)
       }
    }

    fun getCheckDzenNoch() = checkDzenNoch

    fun getBaseDzenNoch(): Boolean {
        return if (k.getBoolean("auto_dzen_noch", false)) autoDzenNoch
        else dzenNoch
    }

    override fun onPause() {
        super.onPause()
        startTimeJob1?.cancel()
        startTimeJob2?.cancel()
        startTimeJob3?.cancel()
        startTimeJob4?.cancel()
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

    private fun sensorChangeDzenNoch(sensorValue: Float) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 10000) {
            return
        }
        when {
            sensorValue <= 4f -> {
                if (!autoDzenNoch) {
                    startTimeJob2?.cancel()
                    startTimeJob4?.cancel()
                    if (startTimeJob1?.isActive != true) {
                        startTimeJob1 = CoroutineScope(Dispatchers.Main).launch {
                            delay(startTimeDelay)
                            timeJob(true)
                        }
                    }
                }
            }
            sensorValue >= 21f -> {
                if (autoDzenNoch) {
                    startTimeJob1?.cancel()
                    startTimeJob4?.cancel()
                    if (startTimeJob2?.isActive != true) {
                        startTimeJob2 = CoroutineScope(Dispatchers.Main).launch {
                            delay(startTimeDelay)
                            timeJob(false)
                        }
                    }
                }
            }
            else -> {
                if (autoDzenNoch != startAutoDzenNoch) {
                    startTimeJob2?.cancel()
                    startTimeJob1?.cancel()
                    if (startTimeJob4?.isActive != true) {
                        startTimeJob4 = CoroutineScope(Dispatchers.Main).launch {
                            timeJob(!autoDzenNoch)
                        }
                    }
                }
            }
        }
    }

    private fun timeJob(isDzenNoch: Boolean) {
        startTimeDelay = 5000
        mLastClickTime = SystemClock.elapsedRealtime()
        autoDzenNoch = isDzenNoch
        startAutoDzenNoch = isDzenNoch
        val prefEditor = k.edit()
        prefEditor.putBoolean("dzen_noch", isDzenNoch)
        prefEditor.apply()
        recreate()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            sensorChangeDzenNoch(sensorEvent.values[0])
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