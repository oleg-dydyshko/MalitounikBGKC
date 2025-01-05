package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.google.android.gms.instantapps.InstantApps
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

abstract class BaseActivity : AppCompatActivity(), SensorEventListener, MenuProvider {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var checkDzenNoch = false
    private var mLastClickTime: Long = 0
    private var startTimeJob: Job? = null
    private var myTimer: Job? = null
    private var downloadDynamicModuleListener: DownloadDynamicModuleListener? = null
    private var ferstStart = false

    interface DownloadDynamicModuleListener {
        fun dynamicModuleDownloading(totalBytesToDownload: Double, bytesDownloaded: Double)
        fun dynamicModuleInstalled()
    }

    fun setDownloadDynamicModuleListener(downloadDynamicModuleListener: DownloadDynamicModuleListener) {
        this.downloadDynamicModuleListener = downloadDynamicModuleListener
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
            if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
            spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onMenuItemSelected(item: MenuItem) = false

    open fun onBack() {
        finish()
    }

    override fun attachBaseContext(context: Context) {
        Configuration(context.resources.configuration).apply {
            if (this.fontScale != 1.0f) {
                this.fontScale = 1.0f
            }
            applyOverrideConfiguration(this)
        }
        super.attachBaseContext(context)
        if (checkmoduleResources()) {
            SplitCompat.install(this)
        }
        FirebaseApp.initializeApp(this)
        Firebase.appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        theme.applyStyle(R.style.OptOutEdgeToEdgeEnforcement, false)
        addMenuProvider(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
        ferstStart = true
        mLastClickTime = SystemClock.elapsedRealtime()
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = savedInstanceState?.getBoolean("dzenNoch", false) ?: getBaseDzenNoch()
        checkDzenNoch = dzenNoch
        setMyTheme()
        val file1 = File("$filesDir/BookCache")
        if (file1.exists()) file1.deleteRecursively()
        val file2 = File("$filesDir/Book")
        if (file2.exists()) file2.deleteRecursively()
        val list = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()).listFiles()
        list?.forEach {
            if (it.exists() && it.name.contains("pdf", ignoreCase = true)) it.delete()
        }
    }

    open fun setMyTheme() {
        if (dzenNoch) setTheme(R.style.AppCompatDark)
    }

    fun getCheckDzenNoch() = checkDzenNoch

    fun saveBaseDzenNoch(madeNight: Int) {
        val prefEditor = k.edit()
        prefEditor.putInt("mode_night", madeNight)
        prefEditor.apply()
        if (checkDzenNoch != getBaseDzenNoch()) {
            recreate()
        }
    }

    fun getBaseDzenNoch(): Boolean {
        val modeNight = k.getInt("mode_night", SettingsActivity.MODE_NIGHT_SYSTEM)
        when (modeNight) {
            SettingsActivity.MODE_NIGHT_SYSTEM -> {
                val configuration = Resources.getSystem().configuration
                dzenNoch = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            }

            SettingsActivity.MODE_NIGHT_YES -> {
                dzenNoch = true
            }

            SettingsActivity.MODE_NIGHT_NO -> {
                dzenNoch = false
            }

            SettingsActivity.MODE_NIGHT_AUTO -> {
                dzenNoch = k.getBoolean("dzen_noch", false)
            }
        }
        return dzenNoch
    }

    fun setFontInterface(textSizePixel: Float, isTextSizeSp: Boolean = false): Float {
        var sp = if (isTextSizeSp) textSizePixel
        else textSizePixel / resources.displayMetrics.density
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        when (k.getInt("fontInterface", 1)) {
            1 -> sp += 2
            2 -> sp += 4
            3 -> sp += 6
            4 -> sp += 8
        }
        return sp
    }

    override fun onPause() {
        super.onPause()
        startTimeJob?.cancel()
        myTimer?.cancel()
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        dzenNoch = getBaseDzenNoch()
        if (k.getInt("mode_night", SettingsActivity.MODE_NIGHT_SYSTEM) == SettingsActivity.MODE_NIGHT_AUTO) {
            setlightSensor()
        }
        if (checkDzenNoch != getBaseDzenNoch()) {
            recreate()
        }
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, R.anim.alphain, R.anim.alphaout)
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, R.anim.alphain, R.anim.alphaout)
        } else {
            @Suppress("DEPRECATION") overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        }
    }

    private fun sensorChangeDzenNoch(sensorValue: Float) {
        if (!ferstStart) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 6000) {
                return
            }
        }
        if (myTimer?.isActive != true) {
            myTimer = CoroutineScope(Dispatchers.Main).launch {
                if (!ferstStart) delay(1000)
                when {
                    sensorValue <= 4f -> {
                        if (!dzenNoch && !checkDzenNoch) {
                            timeJob(true)
                        }
                    }

                    sensorValue >= 21f -> {
                        if (dzenNoch && checkDzenNoch) {
                            timeJob(false)
                        }
                    }

                    else -> {
                        if (dzenNoch != checkDzenNoch) {
                            timeJob(!dzenNoch)
                        }
                    }
                }
            }
        }
        ferstStart = false
    }

    private fun timeJob(isDzenNoch: Boolean) {
        if (startTimeJob?.isActive != true) {
            startTimeJob = CoroutineScope(Dispatchers.Main).launch {
                dzenNoch = isDzenNoch
                val prefEditors = k.edit()
                prefEditors.putBoolean("dzen_noch", isDzenNoch)
                prefEditors.apply()
                recreate()
                mLastClickTime = SystemClock.elapsedRealtime()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            sensorChangeDzenNoch(sensorEvent.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("dzenNoch", dzenNoch)
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

    fun installFullMalitounik() {
        val postInstall = Intent(Intent.ACTION_MAIN)
        postInstall.addCategory(Intent.CATEGORY_DEFAULT)
        postInstall.setPackage(packageName)
        InstantApps.showInstallPrompt(this, postInstall, 500, null)
    }

    fun checkmodulesAdmin(): Boolean {
        val muduls = SplitInstallManagerFactory.create(this).installedModules
        for (mod in muduls) {
            if (mod == "admin") {
                return true
            }
        }
        return false
    }

    fun checkmoduleResources(): Boolean {
        val muduls = SplitInstallManagerFactory.create(this).installedModules
        for (mod in muduls) {
            if (mod == "resources") {
                return true
            }
        }
        return false
    }

    fun downloadDynamicModule(moduleName: String) {
        val splitInstallManager = SplitInstallManagerFactory.create(this)

        val request = SplitInstallRequest.newBuilder().addModule(moduleName).build()

        val listener = SplitInstallStateUpdatedListener { state ->
            if (state.status() == SplitInstallSessionStatus.FAILED) {
                downloadDynamicModule(moduleName)
                return@SplitInstallStateUpdatedListener
            }
            if (state.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                splitInstallManager.startConfirmationDialogForResult(state, this, 150)
            }
            if (state.sessionId() == sessionId) {
                when (state.status()) {
                    SplitInstallSessionStatus.PENDING -> {
                    }

                    SplitInstallSessionStatus.DOWNLOADED -> {
                    }

                    SplitInstallSessionStatus.DOWNLOADING -> {
                        downloadDynamicModuleListener?.dynamicModuleDownloading(state.totalBytesToDownload().toDouble(), state.bytesDownloaded().toDouble())
                    }

                    SplitInstallSessionStatus.INSTALLED -> {
                        downloadDynamicModuleListener?.dynamicModuleInstalled()
                    }

                    SplitInstallSessionStatus.CANCELED -> {
                    }

                    SplitInstallSessionStatus.CANCELING -> {
                    }

                    SplitInstallSessionStatus.FAILED -> {
                    }

                    SplitInstallSessionStatus.INSTALLING -> {
                    }

                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    }

                    SplitInstallSessionStatus.UNKNOWN -> {
                    }
                }
            }
        }

        splitInstallManager.registerListener(listener)

        splitInstallManager.startInstall(request).addOnFailureListener {
            if ((it as SplitInstallException).errorCode == SplitInstallErrorCode.NETWORK_ERROR) {
                MainActivity.toastView(this, getString(R.string.no_internet))
            }
        }.addOnSuccessListener {
            sessionId = it
        }
    }

    companion object {
        private var sessionId = 0
    }
}