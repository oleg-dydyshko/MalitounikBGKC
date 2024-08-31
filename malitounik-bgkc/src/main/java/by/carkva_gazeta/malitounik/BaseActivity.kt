package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
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
import android.view.WindowManager
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
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

abstract class BaseActivity : AppCompatActivity(), SensorEventListener, MenuProvider {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var checkDzenNoch = false
    private var mLastClickTime: Long = 0
    private var startTimeJob: Job? = null
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
        addMenuProvider(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
        ferstStart = true
        mLastClickTime = SystemClock.elapsedRealtime()
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        checkDzenNoch = getBaseDzenNoch()
        setMyTheme()
        if (checkmodulesBiblijateka()) {
            val c = Calendar.getInstance()
            var useTime = k.getLong("BiblijatekaUseTime", 0)
            if (useTime == 0L) {
                val edit = k.edit()
                edit.putLong("BiblijatekaUseTime", c.timeInMillis)
                edit.apply()
                useTime = c.timeInMillis
            }
            c.add(Calendar.DATE, 7)
            if (c.timeInMillis > useTime) {
                removeDynamicModule()
            }
            c.add(Calendar.DATE, 23)
            if (useTime > c.timeInMillis) {
                val file = File("$filesDir/image_temp")
                if (file.exists()) file.deleteRecursively()
                val file1 = File("$filesDir/BookCache")
                if (file1.exists()) file1.deleteRecursively()
                val file2 = File("$filesDir/Book")
                if (file2.exists()) file2.deleteRecursively()
                val list = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()).listFiles()
                list?.forEach {
                    if (it.exists()) it.delete()
                }
                val edit = k.edit()
                edit.remove("Biblioteka")
                edit.apply()
            }
        }
    }

    open fun setMyTheme() {
        if (dzenNoch) setTheme(R.style.AppCompatDark)
    }

    fun getCheckDzenNoch() = checkDzenNoch

    fun getBaseDzenNoch() = dzenNoch

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
        removelightSensor()
    }

    override fun onResume() {
        super.onResume()
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (k.getBoolean("auto_dzen_noch", false)) {
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
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
    }

    private fun sensorChangeDzenNoch(sensorValue: Float) {
        if (!ferstStart) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 6000) {
                return
            }
        }
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
        ferstStart = false
    }

    private fun timeJob(isDzenNoch: Boolean) {
        if (startTimeJob?.isActive != true) {
            startTimeJob = CoroutineScope(Dispatchers.Main).launch {
                dzenNoch = isDzenNoch
                if (k.getBoolean("auto_dzen_noch", false)) {
                    val prefEditor = k.edit()
                    prefEditor.putBoolean("dzen_noch", isDzenNoch)
                    prefEditor.apply()
                }
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

    fun checkmodulesBiblijateka(): Boolean {
        val muduls = SplitInstallManagerFactory.create(this).installedModules
        for (mod in muduls) {
            if (mod == "biblijateka") {
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

    private fun removeDynamicModule() {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.deferredUninstall(listOf("biblijateka"))
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