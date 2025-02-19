package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.LogBinding
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LogView : BaseActivity() {
    private lateinit var binding: LogBinding
    private var log = ArrayList<String>()
    private var logJob: Job? = null
    private val sb = StringBuilder()
    private var resetTollbarJob: Job? = null
    private val dzenNoch get() = getBaseDzenNoch()
    private val checkSB = StringBuilder()
    private var oldCheckSB = ""

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun onPause() {
        super.onPause()
        logJob?.cancel()
    }

    private suspend fun getLogFile(count: Int = 0) {
        val localFile = File("$filesDir/cache/log.txt")
        var error = false
        Malitounik.referens.child("/admin/adminListFile.txt").getFile(localFile).addOnFailureListener {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
            error = true
        }.await()
        if (error && count < 3) {
            getLogFile(count + 1)
            return
        }
        oldCheckSB = localFile.readText()
        val list = Malitounik.referens.child("/admin").list(1000).await()
        runPrefixes(list, oldCheckSB)
        val list2 = Malitounik.referens.child("/chytanne/Semucha").list(1000).await()
        runItems(list2, oldCheckSB)
        var pathReference = Malitounik.referens.child("/calendarsviatyia.txt")
        addItems(pathReference.path, pathReference.name, oldCheckSB)
        for (year in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
            try {
                pathReference = Malitounik.referens.child("/calendar-cytanne_$year.php")
                addItems(pathReference.path, pathReference.name, oldCheckSB)
            } catch (_: Throwable) {
            }
        }
        checkResourcesCount()
        checkResources()
        if (log.isEmpty()) {
            binding.textView.text = getString(by.carkva_gazeta.malitounik.R.string.admin_upload_contine)
            val zip = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MalitounikResource.zip")
            if (zip.exists()) sendAndClearLogFile(zip, isSendLogFile = false)
        } else {
            val strB = StringBuilder()
            log.forEach {
                strB.append(it)
                strB.append("\n")
            }
            binding.textView.text = strB.toString()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        upDate()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
        }
        setTollbarTheme()
        lockOrientation()
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

    private fun checkResourcesCount() {
        checkSB.clear()
        var count = 0
        for (i in 0 until Bogashlugbovya.resursMap.size) {
            for (e in 0 until Bogashlugbovya.resursMap.size) {
                if (Bogashlugbovya.resursMap.keyAt(i) == Bogashlugbovya.resursMap.keyAt(e)) {
                    count++
                }
            }
            if (count > 1) {
                checkSB.append("Bogashlugbovya.resursMap > 1: ${Bogashlugbovya.resursMap.keyAt(i)}\n")
            }
            count = 0
        }
    }

    private fun checkResources() {
        val list = sb.toString().split("\n")
        val oldList = oldCheckSB.split("\n")
        for (element in oldList.indices) {
            val t11 = oldList[element].indexOf("<name>")
            val t22 = oldList[element].indexOf("</name>")
            if (t11 != -1 && t22 != -1) {
                val name2 = oldList[element].substring(t11 + 6, t22)
                val t33 = name2.indexOf(".")
                val nameRR = if (t33 != -1) name2.substring(0, t33)
                else name2
                var testR = false
                for (i in list.indices) {
                    val t1 = list[i].indexOf("<name>")
                    val t2 = list[i].indexOf("</name>")
                    if (t1 != -1 && t2 != -1) {
                        val name1 = list[i].substring(t1 + 6, t2)
                        val t3 = name1.indexOf(".")
                        val nameR = if (t3 != -1) name1.substring(0, t3)
                        else name1
                        if (nameR == nameRR) {
                            testR = true
                            break
                        }
                    }
                }
                if (!testR) {
                    checkSB.append("firebase: перайменавана альбо выдалена $nameRR\n")
                }
            }
        }
        binding.textView2.text = checkSB.toString()
    }

    private fun upDate() {
        if (MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI)) {
            log.clear()
            sb.clear()
            logJob?.cancel()
            logJob = CoroutineScope(Dispatchers.Main).launch {
                val localFile = File("$filesDir/cache/cache.txt")
                Malitounik.referens.child("/admin/log.txt").getFile(localFile).await()
                var log = ""
                if (localFile.exists()) log = localFile.readText()
                if (log.isNotEmpty()) {
                    getLogFile()
                } else {
                    binding.textView.text = getString(by.carkva_gazeta.malitounik.R.string.admin_upload_log_contine)
                    if (dzenNoch) binding.textView.background = ContextCompat.getDrawable(this@LogView, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                    else binding.textView.background = ContextCompat.getDrawable(this@LogView, by.carkva_gazeta.malitounik.R.drawable.selector_default)
                    binding.textView.setOnClickListener {
                        binding.textView.setOnClickListener(null)
                        binding.textView.background = null
                        logJob?.cancel()
                        logJob = CoroutineScope(Dispatchers.Main).launch {
                            getLogFile()
                        }
                    }
                }
            }
        } else {
            binding.textView.text = getString(by.carkva_gazeta.malitounik.R.string.admin_upload_no_wi_fi)
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getText(by.carkva_gazeta.malitounik.R.string.log)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    private suspend fun runPrefixes(list: ListResult, checkList: String) {
        list.prefixes.forEach {
            if (logJob?.isActive != true) return@forEach
            if (it.name != "piasochnica") {
                val list2 = it.list(1000).await()
                runPrefixes(list2, checkList)
                runItems(list2, checkList)
            }
        }
    }

    private suspend fun runItems(list: ListResult, checkList: String) {
        list.items.forEach { storageReference ->
            if (logJob?.isActive != true) return@forEach
            addItems(storageReference.path, storageReference.name, checkList)
        }
    }

    private suspend fun addItems(path: String, name: String, checkList: String, count: Int = 0) {
        val pathReference = Malitounik.referens.child(path)
        var error = false
        val meta = pathReference.metadata.addOnFailureListener {
            error = true
        }.await()
        if (error && count < 3) {
            addItems(path, name, checkList, count + 1)
            return
        }
        val md5Sum = meta.md5Hash ?: 0
        sb.append("<name>")
        sb.append(name)
        sb.append("</name>")
        sb.append("<md5>")
        sb.append(md5Sum)
        sb.append("</md5>\n")
        if (checkList.contains("<name>$name</name>")) {
            val t1 = checkList.indexOf("<name>$name</name>")
            val t2 = checkList.indexOf("<md5>", t1)
            val t3 = checkList.indexOf("</md5>", t2)
            val filemd5Sum = checkList.substring(t2 + 5, t3)
            if (filemd5Sum != md5Sum) {
                log.add(path)
            }
        } else {
            log.add(path)
        }
        binding.textView.text = path
    }

    private fun createAndSentFile() {
        binding.progressBar.visibility = View.VISIBLE
        val zip = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MalitounikResource.zip")
        if (log.isNotEmpty() && MainActivity.isNetworkAvailable()) {
            logJob = CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(zip)))
                    val localFile = File("$filesDir/cache/cache.txt")
                    val logFile = File("$filesDir/cache/log.txt")
                    val strB = StringBuilder()
                    val buffer = ByteArray(1024)
                    for (index in 0 until log.size) {
                        val file = log[index]
                        var filePath = file.replace("//", "/")
                        val t1 = filePath.indexOf("(")
                        if (t1 != -1) filePath = filePath.substring(0, t1)
                        var error = false
                        try {
                            Malitounik.referens.child(filePath).getFile(localFile).addOnFailureListener {
                                MainActivity.toastView(this@LogView, getString(by.carkva_gazeta.malitounik.R.string.error))
                                error = true
                            }.await()
                        } catch (_: Throwable) {
                            error = true
                        }
                        if (error) continue
                        val fi = FileInputStream(localFile)
                        val origin = BufferedInputStream(fi)
                        try {
                            val entry = ZipEntry(file.substring(file.lastIndexOf("/")))
                            out.putNextEntry(entry)
                            while (true) {
                                val len = fi.read(buffer)
                                if (len <= 0) break
                                out.write(buffer, 0, len)
                            }
                        } catch (_: Throwable) {
                        } finally {
                            origin.close()
                        }
                        strB.append(file)
                        strB.append("\n")
                    }
                    strB.append("\n")
                    strB.append(checkSB.toString())
                    logFile.writer().use {
                        it.write(strB.toString())
                    }
                    val fi = FileInputStream(logFile)
                    val origin = BufferedInputStream(fi)
                    try {
                        val entry = ZipEntry("log.txt")
                        out.putNextEntry(entry)
                        while (true) {
                            val len = fi.read(buffer)
                            if (len <= 0) break
                            out.write(buffer, 0, len)
                        }
                    } catch (_: Throwable) {
                    } finally {
                        origin.close()
                    }
                    out.closeEntry()
                    out.close()
                }
                sendAndClearLogFile(zip)
            }
        } else {
            sendAndClearLogFile(zip)
        }
    }

    private fun sendAndClearLogFile(zip: File, isClearLogFile: Boolean = true, isSendLogFile: Boolean = true) {
        if (isSendLogFile) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@LogView, "by.carkva_gazeta.malitounik.fileprovider", zip))
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(by.carkva_gazeta.malitounik.R.string.set_log_file))
            sendIntent.type = "application/zip"
            startActivity(Intent.createChooser(sendIntent, getString(by.carkva_gazeta.malitounik.R.string.set_log_file)))
        }
        if (isClearLogFile && MainActivity.isNetworkAvailable() && sb.toString().isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val logFile = File("$filesDir/cache/log.txt")
                logFile.writer().use {
                    it.write("")
                }
                val localFile = File("$filesDir/cache/cache.txt")
                localFile.writer().use {
                    it.write(sb.toString())
                }
                Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()
                Malitounik.referens.child("/admin/adminListFile.txt").putFile(Uri.fromFile(localFile)).await()
            }
        }
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.log, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_up_date) {
            if (logJob?.isActive != true) {
                upDate()
            }
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_sent_log) {
            if (logJob?.isActive != true) {
                createAndSentFile()
            } else {
                val zip = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "MalitounikResource.zip")
                if (zip.exists()) sendAndClearLogFile(zip, false)
            }
            return true
        }
        return false
    }
}