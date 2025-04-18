package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminPasochnicaListBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.lang.Character.UnicodeBlock
import java.net.URLEncoder
import java.util.Calendar
import java.util.GregorianCalendar


class PasochnicaList : BaseActivity(), DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogContextMenu.DialogContextMenuListener, DialogDelite.DialogDeliteListener, DialogDeliteAllBackCopy.DialogDeliteAllBackCopyListener, DialogDeliteAllPasochnica.DialogDeliteAllPasochnicaListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaListBinding
    private var resetTollbarJob: Job? = null
    private var fileList = ArrayList<String>()
    private lateinit var adapter: PasochnicaListAdaprer
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageUri = it.data?.data
            imageUri?.let { image ->
                onDialogFile(File(URLEncoder.encode(image.path, "UTF-8")))
            }
        }
    }
    private val mActivityResultNetFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val dirToFile = it.data?.extras?.getString("dirToFile") ?: "/"
            getFileCopyPostRequest(dirToFile)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun onDialogFile(file: File) {
        val title = file.name
        var exits = false
        for (i in 0 until fileList.size) {
            if (fileList[i].contains(title)) {
                exits = true
                break
            }
        }
        val intent = Intent(this, Pasochnica::class.java)
        intent.putExtra("text", file.readText())
        intent.putExtra("resours", "")
        intent.putExtra("exits", exits)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    override fun deliteAllBackCopy() {
        val dir = getExternalFilesDir("PiasochnicaBackCopy")
        if (dir?.exists() == true) {
            dir.deleteRecursively()
            getDirPostRequest()
            invalidateOptionsMenu()
        }
    }

    override fun deliteAllPasochnica() {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val list = Malitounik.referens.child("/admin/piasochnica").list(1000).await()
                    list.items.forEach {
                        it.delete().await()
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
                getDirPostRequest()
                invalidateOptionsMenu()
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFindFileListAsSave()
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = AdminPasochnicaListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTollbarTheme()

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, Pasochnica::class.java)
            var fileName = fileList[position]
            if (fileName.contains("(BackCopy)")) {
                fileName = fileName.replace("(BackCopy)", "")
                val file = File(getExternalFilesDir("PiasochnicaBackCopy"), fileName.replace("(BackCopy)", ""))
                if (file.exists()) {
                    val t1 = fileName.indexOf(")")
                    val t2 = fileName.lastIndexOf(".")
                    val resours = if (t1 != -1) {
                        fileName.substring(1, t1)
                    } else {
                        ""
                    }
                    val title = if (t1 != -1 && t2 != -1) {
                        fileName.substring(t1 + 1, t2).trim()
                    } else {
                        ""
                    }
                    intent.putExtra("text", file.readText())
                    intent.putExtra("resours", resours)
                    intent.putExtra("exits", true)
                    intent.putExtra("title", title)
                    intent.putExtra("backcopy", true)
                }
            } else {
                intent.putExtra("isPasochnica", true)
                intent.putExtra("isSite", true)
                intent.putExtra("fileName", fileName)
            }
            startActivity(intent)
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            val contextMenu = DialogContextMenu.getInstance(position, fileList[position], false)
            contextMenu.show(supportFragmentManager, "contextMenu")
            return@setOnItemLongClickListener true
        }
        getDirPostRequest()
    }

    override fun onDialogRenameClick(title: String, isSite: Boolean) {
        val t1 = title.lastIndexOf("/")
        val t2 = title.indexOf(")")
        val saveAs = if (t2 != -1) true
        else if (t1 != -1) !title.contains("/admin/piasochnica")
        else false
        val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance(title, isSite, saveAs)
        dialogPasochnicaFileName.show(supportFragmentManager, "dialogPasochnicaFileName")
    }

    override fun onDialogDeliteClick(position: Int, title: String, isSite: Boolean) {
        val dialogDelite = DialogDelite.getInstance(position, title, isSite)
        dialogDelite.show(supportFragmentManager, "dialogDelite")
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        if (title.contains("(BackCopy")) {
            val fileNameold = title.replace("(BackCopy)", "")
            val fileOld = File(getExternalFilesDir("PiasochnicaBackCopy"), fileNameold)
            if (fileOld.exists()) fileOld.delete()
            getDirPostRequest()
        } else {
            getFileUnlinkPostRequest(title, isSite)
        }
        val prefEditor = k.edit()
        prefEditor.remove("admin" + title + "position")
        prefEditor.apply()
        invalidateOptionsMenu()
    }

    override fun setFileName(oldFileName: String, fileName: String, isSite: Boolean, saveAs: Boolean) {
        if (oldFileName.contains("(BackCopy")) {
            val fileNameold = oldFileName.replace("(BackCopy)", "")
            val fileOld = File(getExternalFilesDir("PiasochnicaBackCopy"), fileNameold)
            val fileNew = File(getExternalFilesDir("PiasochnicaBackCopy"), fileName.replace("(BackCopy)", ""))
            fileOld.renameTo(fileNew)
            getDirPostRequest()
        } else {
            getFileRenamePostRequest(oldFileName, fileName, isSite)
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = PasochnicaListAdaprer(this)
        binding.listView.adapter = adapter
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.pasochnica)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
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

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
    }

    private fun getFileCopyPostRequest(dirToFile: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val t5 = dirToFile.lastIndexOf("/")
                var fileName = dirToFile.substring(t5 + 1)
                val sb = StringBuilder()
                for (c in fileName) {
                    val unicode = UnicodeBlock.of(c)
                    unicode?.let {
                        if (!(it == UnicodeBlock.CYRILLIC || it == UnicodeBlock.CYRILLIC_SUPPLEMENTARY || it == UnicodeBlock.CYRILLIC_EXTENDED_A || it == UnicodeBlock.CYRILLIC_EXTENDED_B)) {
                            sb.append(c)
                        }
                    }
                }
                fileName = sb.toString()
                if (!fileName.contains(".php", true)) {
                    fileName = fileName.replace("-", "_")
                }
                fileName = fileName.replace(" ", "_").lowercase()
                val mm = if (fileName[0].isDigit()) "mm_"
                else ""
                fileName = "$mm$fileName"
                if (dirToFile != fileName) {
                    getFileRenamePostRequest(dirToFile, dirToFile.substring(0, t5 + 1) + fileName, true)
                }
                var resourse = ""
                val localFile = File("$filesDir/cache/cache.txt")
                try {
                    Malitounik.referens.child("/$dirToFile").getFile(localFile).addOnFailureListener {
                        MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                val t1 = fileName.indexOf(".")
                var newFileName = fileName.replace("\n", " ")
                var title = ""
                if (t1 != -1) {
                    resourse = "(" + newFileName.substring(0, t1) + ") "
                    newFileName = newFileName.substring(0, t1)
                    if (fileName.contains(".html")) {
                        val rt = localFile.readText()
                        val t2 = rt.indexOf("<strong>")
                        if (t2 != -1) {
                            val t3 = rt.indexOf("</strong>")
                            val t4 = rt.indexOf("<br>")
                            title = if (t4 > t2 + 8 && t4 < t3) {
                                rt.substring(t2 + 8, t4).trim()
                            } else {
                                rt.substring(t2 + 8, t3).trim()
                            }
                        }
                    }
                }
                val res = if (title != "") {
                    val preparetext = MainActivity.fromHtml(title).toString()
                    preparetext.substring(0, 1).uppercase() + preparetext.substring(1).lowercase()
                } else newFileName
                try {
                    Malitounik.referens.child("/admin/piasochnica/$resourse$res").putFile(Uri.fromFile(localFile)).await()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
                val intent = Intent(this@PasochnicaList, Pasochnica::class.java)
                intent.putExtra("text", localFile.readText())
                intent.putExtra("isSite", true)
                intent.putExtra("fileName", "$resourse$res")
                startActivity(intent)
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    private fun getFileUnlinkPostRequest(fileName: String, isSite: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    if (isSite) {
                        Malitounik.referens.child("/$fileName").delete().addOnCompleteListener { }.await()
                    } else {
                        Malitounik.referens.child("/admin/piasochnica/$fileName").delete().await()
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                if (isSite) saveLogFile()
                binding.progressBar2.visibility = View.GONE
                getDirPostRequest()
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    private suspend fun saveLogFile(count: Int = 0) {
        val logFile = File("$filesDir/cache/log.txt")
        var error = false
        logFile.writer().use {
            it.write(getString(by.carkva_gazeta.malitounik.R.string.check_update_resourse))
        }
        Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
            MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error))
            error = true
        }.await()
        if (error && count < 3) {
            saveLogFile(count + 1)
        }
    }

    private fun getFileRenamePostRequest(oldFileName: String, fileName: String, isSite: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = File("$filesDir/cache/cache.txt")
                    if (isSite) {
                        Malitounik.referens.child("/$oldFileName").getFile(localFile).addOnFailureListener {
                            MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }.await()
                        Malitounik.referens.child("/$oldFileName").delete().await()
                        Malitounik.referens.child("/$fileName").putFile(Uri.fromFile(localFile)).await()
                    } else {
                        Malitounik.referens.child("/admin/piasochnica/$oldFileName").getFile(localFile).addOnFailureListener {
                            MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }.await()
                        Malitounik.referens.child("/admin/piasochnica/$oldFileName").delete().await()
                        Malitounik.referens.child("/admin/piasochnica/$fileName").putFile(Uri.fromFile(localFile)).await()
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                if (isSite) saveLogFile()
                binding.progressBar2.visibility = View.GONE
                getDirPostRequest()
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    private fun getDirPostRequest() {
        val backCopy = ArrayList<String>()
        val dir = getExternalFilesDir("PiasochnicaBackCopy")
        if (dir?.exists() == true) {
            var list = dir.list()
            list?.forEach {
                val file = File("$dir/$it")
                val systemTime = System.currentTimeMillis()
                val lastModified = GregorianCalendar()
                lastModified.timeInMillis = file.lastModified()
                lastModified.add(Calendar.DATE, 7)
                if (lastModified.timeInMillis < systemTime) {
                    file.delete()
                }
            }
            list = dir.list()
            list?.forEach {
                val t1 = it.lastIndexOf(".")
                val fileName = if (t1 != -1) it.substring(0, t1) + "(BackCopy)" + it.substring(t1)
                else "$it(BackCopy)"
                backCopy.add(fileName)
            }
            backCopy.sort()
        }
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    fileList.clear()
                    val list = Malitounik.referens.child("/admin/piasochnica").list(500).await()
                    list.items.forEach {
                        fileList.add(it.name)
                    }
                    fileList.sort()
                    fileList.addAll(backCopy)
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PasochnicaList, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                if (intent.extras != null) {
                    val res = intent.extras?.getString("resours", "") ?: ""
                    var exits = false
                    for (i in 0 until fileList.size) {
                        var resurs = fileList[i]
                        val t1 = fileList[i].indexOf("(")
                        if (t1 != -1) {
                            val t2 = fileList[i].indexOf(")")
                            resurs = fileList[i].substring(t1 + 1, t2)
                        } else {
                            val t3 = fileList[i].lastIndexOf(".")
                            if (t3 != -1) {
                                resurs = fileList[i].substring(0, t3)
                            }
                        }
                        if (res == resurs) {
                            exits = true
                            break
                        }
                    }
                    val intent = Intent(this@PasochnicaList, Pasochnica::class.java)
                    intent.putExtra("text", this@PasochnicaList.intent.extras?.getString("text", "") ?: "")
                    intent.putExtra("resours", this@PasochnicaList.intent.extras?.getString("resours", "") ?: "")
                    intent.putExtra("exits", exits)
                    intent.putExtra("title", this@PasochnicaList.intent.extras?.getString("title", "") ?: "")
                    startActivity(intent)
                }
                binding.listView.invalidate()
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            fileList.addAll(backCopy)
            adapter.notifyDataSetChanged()
            binding.listView.invalidate()
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val itemDelite = menu.findItem(R.id.action_delite_all)
        val dir = getExternalFilesDir("PiasochnicaBackCopy")
        if (dir?.exists() == true) {
            val list = dir.list()
            itemDelite.isVisible = list?.isNotEmpty() ?: true
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_plus) {
            val intent = Intent(this, Pasochnica::class.java)
            intent.putExtra("newFile", true)
            intent.putExtra("fileName", "new_file.html")
            startActivity(intent)
            return true
        }
        if (id == R.id.action_open_net_file) {
            mActivityResultNetFile.launch(Intent(this, PiasochnicaNetFileExplorer::class.java))
            return true
        }
        if (id == R.id.action_open_file) {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/html", "text/plain"))
            mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
            return true
        }
        if (id == R.id.action_delite_all) {
            val dialogDeliteAllBackCopy = DialogDeliteAllBackCopy()
            dialogDeliteAllBackCopy.show(supportFragmentManager, "dialogDeliteAllBackCopy")
            return true
        }
        if (id == R.id.action_delite_all_pasochnica) {
            val dialogDeliteAllPasochnica = DialogDeliteAllPasochnica()
            dialogDeliteAllPasochnica.show(supportFragmentManager, "dialogDeliteAllPasochnica")
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piasochnica_list, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    private inner class PasochnicaListAdaprer(context: Activity) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, fileList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val posFileList = SpannableString(fileList[position])
            if (fileList[position].contains(("BackCopy"))) {
                posFileList.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary)), 0, posFileList.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                posFileList.setSpan(StyleSpan(Typeface.ITALIC), 0, posFileList.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            viewHolder.text.text = posFileList
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        val findDirAsSave = ArrayList<String>()

        private suspend fun findFile(list: ListResult? = null) {
            CoroutineScope(Dispatchers.Main).launch {
                val nawList = list ?: Malitounik.referens.child("/admin").list(1000).await()
                nawList.items.forEach {
                    findDirAsSave.add(it.path)
                }
                nawList.prefixes.forEach {
                    if (it.name != "piasochnica") {
                        val rList = Malitounik.referens.child(it.path).list(1000).await()
                        findFile(rList)
                    }
                }
            }
        }

        fun getFindFileListAsSave() {
            if (MainActivity.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        findFile()
                    } catch (e: Throwable) {
                        MainActivity.toastView(Malitounik.applicationContext(), Malitounik.applicationContext().getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
            } else {
                MainActivity.toastView(Malitounik.applicationContext(), Malitounik.applicationContext().getString(by.carkva_gazeta.malitounik.R.string.no_internet))
            }
        }
    }
}