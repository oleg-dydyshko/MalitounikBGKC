package by.carkva_gazeta.resources

import android.content.Intent
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
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
        val checkList = localFile.readText()
        val list = Malitounik.referens.child("/admin").list(1000).await()
        runPrefixes(list, checkList)
        val list2 = Malitounik.referens.child("/chytanne/Semucha").list(1000).await()
        runItems(list2, checkList)
        val pathReference = Malitounik.referens.child("/calendarsviatyia.txt")
        addItems(pathReference.path, pathReference.name, checkList)
        checkResourcesCount()
        checkResources()
        if (log.isEmpty()) {
            binding.textView.text = getString(by.carkva_gazeta.malitounik.R.string.admin_upload_contine)
        } else {
            val strB = java.lang.StringBuilder()
            log.forEach {
                strB.append(it)
                strB.append("\n")
            }
            binding.textView.text = strB.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        upDate()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
        }
        setTollbarTheme()
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

    private fun noCheckResources(name: String): Boolean {
        val list = ArrayList<String>()
        list.add("bogashlugbovya_error")
        list.add("carniauski")
        list.add("nadsan")
        list.add("prichasnik")
        list.add("sinaidaln")
        list.add("sinaidals")
        list.add("viaczernia_bierascie_")
        list.add("biblian")
        list.add("biblias")
        list.add("bogashlugbovya1_")
        for (i in 0 until list.size) {
            if (name.contains(list[i])) {
                return true
            }
        }
        return false
    }

    private fun checkResources() {
        val fields = R.raw::class.java.fields
        val fields2 = by.carkva_gazeta.malitounik.R.raw::class.java.fields
        for (element in fields) {
            val name = element.name
            if (!sb.toString().contains(name) && !noCheckResources(name)) {
                checkSB.append("firebase: няма resources.R.raw.$name\n")
            }
        }
        for (element in fields2) {
            val name = element.name
            if (!sb.toString().contains(name) && (name.contains("pesny_") || name.contains("piesni_"))) {
                checkSB.append("firebase: няма malitounik.R.raw.$name\n")
            }
        }
        /*val data = MenuPesnyData().getPesnyAll()
        for (i in 0 until PesnyAll.resursMap.size) {
            var testR = false
            for (e in 0 until data.size) {
                if (data[e].resurs.contains("pesny_") || data[e].resurs.contains("piesni_")) {
                    if (data[e].resurs == PesnyAll.resursMap.keyAt(i)) {
                        testR = true
                        break
                    }
                } else testR = true
            }
            if (!testR) {
                checkSB.append("У MenuPesnyData няма malitounik.R.raw.${PesnyAll.resursMap.keyAt(i)}\n")
            }
        }
        for (i in 0 until data.size) {
            var testR = false
            for (e in 0 until PesnyAll.resursMap.size) {
                if (data[i].resurs.contains("pesny_") || data[i].resurs.contains("piesni_")) {
                    if (data[i].resurs == PesnyAll.resursMap.keyAt(e)) {
                        testR = true
                        break
                    }
                } else testR = true
            }
            if (!testR) {
                checkSB.append("У malitounik.R.raw няма MenuPesnyData -> ${data[i].resurs}\n")
            }
        }
        val data2 = searchText()
        for (i in 0 until Bogashlugbovya.resursMap.size) {
            if (!(Bogashlugbovya.resursMap.keyAt(i).contains("pesny_") || Bogashlugbovya.resursMap.keyAt(i).contains("piesni_"))) {
                var testR = false
                for (e in 0 until data2.size) {
                    if (data2[e].resurs == Bogashlugbovya.resursMap.keyAt(i) && !noCheckResources(data2[e].resurs)) {
                        testR = true
                        break
                    }
                }
                if (!testR) {
                    checkSB.append("У MenuBogashlugbovya няма resources.R.raw.${Bogashlugbovya.resursMap.keyAt(i)}\n")
                }
            }
        }
        for (i in 0 until data2.size) {
            if (!(Bogashlugbovya.resursMap.keyAt(i).contains("pesny_") || Bogashlugbovya.resursMap.keyAt(i).contains("piesni_"))) {
                var testR = false
                for (e in 0 until Bogashlugbovya.resursMap.size) {
                    if (data2[i].resurs == Bogashlugbovya.resursMap.keyAt(e) && !noCheckResources(data2[i].resurs)) {
                        testR = true
                        break
                    }
                }
                if (!testR) {
                    checkSB.append("У resources.R.raw няма MenuBogashlugbovya -> ${data2[i].resurs}\n")
                }
            }
        }*/
        binding.textView2.text = checkSB.toString()
    }

    /*private fun searchText(): ArrayList<MenuListData> {
        val dataSearch = ArrayList<MenuListData>()
        dataSearch.addAll(MenuBogashlugbovya.getTextBogaslugbovyiaList(true))
        dataSearch.addAll(MenuBogashlugbovya.getTextPasliaPrychascia(true))
        dataSearch.addAll(MenuBogashlugbovya.getTextSubBogaslugbovuiaVichernia(true))
        dataSearch.addAll(MenuBogashlugbovya.getTextAktoixList(true))
        dataSearch.addAll(MenuBogashlugbovya.getTextViacherniaList(true))
        dataSearch.addAll(MenuBogashlugbovya.getTextTonNaKoznyDzenList(true))
        for (i in 1..8) {
            dataSearch.add(MenuListData(getString(by.carkva_gazeta.malitounik.R.string.ton, i.toString()), "ton$i"))
        }
        val sluzba = SlugbovyiaTextu()
        var mesiach = sluzba.getMineiaMesiachnaia()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getVilikiTydzen()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getSvetlyTydzen()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getMineiaSviatochnaia()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen1()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen2()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen3()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen4()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen5()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        mesiach = sluzba.getTydzen6()
        for (i in mesiach.indices) {
            dataSearch.add(MenuListData(mesiach[i].title + ". " + sluzba.getNazouSluzby(mesiach[i].sluzba), mesiach[i].resource))
        }
        return dataSearch
    }*/

    private fun upDate() {
        if (MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI)) {
            logJob?.cancel()
            logJob = CoroutineScope(Dispatchers.Main).launch {
                val localFile = File("$filesDir/cache/cache.txt")
                Malitounik.referens.child("/admin/log.txt").getFile(localFile)
                val log = localFile.readText()
                if (log != "") {
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
        sb.append("<name>")
        sb.append(name)
        sb.append("</name>")
        sb.append("<meta>")
        sb.append(meta.updatedTimeMillis)
        sb.append("</meta>\n")
        if (checkList.contains("<name>$name</name>")) {
            val t1 = checkList.indexOf("<name>$name</name>")
            val t2 = checkList.indexOf("<meta>", t1)
            val t3 = checkList.indexOf("</meta>", t2)
            val fileLastUpdate = checkList.substring(t2 + 6, t3).toLong()
            if (fileLastUpdate < meta.updatedTimeMillis) {
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
                clearLogFile(zip)
            }
        } else {
            clearLogFile(zip)
        }
    }

    private fun clearLogFile(zip: File) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@LogView, "by.carkva_gazeta.malitounik.fileprovider", zip))
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(by.carkva_gazeta.malitounik.R.string.set_log_file))
        sendIntent.type = "application/zip"
        startActivity(Intent.createChooser(sendIntent, getString(by.carkva_gazeta.malitounik.R.string.set_log_file)))
        if (MainActivity.isNetworkAvailable() && sb.toString().isNotEmpty()) {
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
            }
            return true
        }
        return false
    }
}