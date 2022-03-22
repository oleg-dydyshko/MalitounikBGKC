package by.carkva_gazeta.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.admin.databinding.AdminPasochnicaListBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*


class PasochnicaList : AppCompatActivity(), DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogContextMenu.DialogContextMenuListener, DialogDelite.DialogDeliteListener, DialogFileExplorer.DialogFileExplorerListener, DialogNetFileExplorer.DialogNetFileExplorerListener, DialogDeliteAllBackCopy.DialogDeliteAllBackCopyListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaListBinding
    private var resetTollbarJob: Job? = null
    private var fileList = ArrayList<String>()
    private lateinit var adapter: PasochnicaListAdaprer
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            val fileExplorer = DialogFileExplorer()
            fileExplorer.show(supportFragmentManager, "file_explorer")
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onDialogFile(file: File) {
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

    override fun onDialogNetFile(dirToFile: String, fileName: String) {
        getFileCopyPostRequest(dirToFile, fileName)
    }

    override fun deliteAllBackCopy() {
        val dir = getExternalFilesDir("PiasochnicaBackCopy")
        if (dir?.exists() == true) {
            dir.deleteRecursively()
            getDirPostRequest()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getFindFileListAsSave()
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
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

    override fun onDialogRenameClick(position: Int, title: String, isSite: Boolean) {
        val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance(title, isSite)
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
    }

    override fun setFileName(oldFileName: String, fileName: String, isSite: Boolean) {
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
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
            }
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

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun getFileCopyPostRequest(dirToFile: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    try {
                        var reqParam = URLEncoder.encode("copy", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        reqParam += "&" + URLEncoder.encode("dirToFile", "UTF-8") + "=" + URLEncoder.encode(dirToFile, "UTF-8")
                        reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                        val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                        with(mURL.openConnection() as HttpURLConnection) {
                            requestMethod = "POST"
                            val wr = OutputStreamWriter(outputStream)
                            wr.write(reqParam)
                            wr.flush()
                            inputStream
                        }
                    } catch (e: Throwable) {
                        withContext(Dispatchers.Main) {
                            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                }
                binding.progressBar2.visibility = View.GONE
                val intent = Intent(this@PasochnicaList, Pasochnica::class.java)
                intent.putExtra("isSite", true)
                intent.putExtra("fileName", fileName)
                startActivity(intent)
            }
        }
    }

    private fun getFileUnlinkPostRequest(fileName: String, isSite: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    try {
                        var reqParam = URLEncoder.encode("unlink", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        if (isSite) {
                            reqParam += "&" + URLEncoder.encode("isSite", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        }
                        reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                        val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                        with(mURL.openConnection() as HttpURLConnection) {
                            requestMethod = "POST"
                            val wr = OutputStreamWriter(outputStream)
                            wr.write(reqParam)
                            wr.flush()
                            inputStream
                        }
                    } catch (e: Throwable) {
                        withContext(Dispatchers.Main) {
                            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                }
                binding.progressBar2.visibility = View.GONE
                val dialogNetFileExplorer = supportFragmentManager.findFragmentByTag("dialogNetFileExplorer") as? DialogNetFileExplorer
                dialogNetFileExplorer?.getDirListRequest()
                getDirPostRequest()
            }
        }
    }

    private fun getFileRenamePostRequest(oldFileName: String, fileName: String, isSite: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    try {
                        var reqParam = URLEncoder.encode("rename", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        if (isSite) {
                            reqParam += "&" + URLEncoder.encode("isSite", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        }
                        reqParam += "&" + URLEncoder.encode("oldFileName", "UTF-8") + "=" + URLEncoder.encode(oldFileName.replace("\n", " "), "UTF-8")
                        reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName.replace("\n", " "), "UTF-8")
                        val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                        with(mURL.openConnection() as HttpURLConnection) {
                            requestMethod = "POST"
                            val wr = OutputStreamWriter(outputStream)
                            wr.write(reqParam)
                            wr.flush()
                            inputStream
                        }
                    } catch (e: Throwable) {
                        withContext(Dispatchers.Main) {
                            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                }
                binding.progressBar2.visibility = View.GONE
                val dialogNetFileExplorer = supportFragmentManager.findFragmentByTag("dialogNetFileExplorer") as? DialogNetFileExplorer
                dialogNetFileExplorer?.getDirListRequest()
                getDirPostRequest()
            }
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
                withContext(Dispatchers.IO) {
                    try {
                        val reqParam = URLEncoder.encode("file", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                        with(mURL.openConnection() as HttpURLConnection) {
                            requestMethod = "POST"
                            val wr = OutputStreamWriter(outputStream)
                            wr.write(reqParam)
                            wr.flush()
                            val sb = StringBuilder()
                            BufferedReader(InputStreamReader(inputStream)).use {
                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    sb.append(inputLine)
                                    inputLine = it.readLine()
                                }
                            }
                            val result = sb.toString()
                            fileList.clear()
                            if (result != "null") {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<String>>() {}.type
                                fileList.addAll(gson.fromJson(result, type))
                                fileList.sort()
                                fileList.addAll(backCopy)
                            }
                        }
                    } catch (e: Throwable) {
                        withContext(Dispatchers.Main) {
                            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                }
                if (intent.extras != null) {
                    val title = intent.extras?.getString("title", "") ?: ""
                    var exits = false
                    for (i in 0 until fileList.size) {
                        if (fileList[i].contains(title)) {
                            exits = true
                            break
                        }
                    }
                    val intent = Intent(this@PasochnicaList, Pasochnica::class.java)
                    intent.putExtra("text", this@PasochnicaList.intent.extras?.getString("text", "") ?: "")
                    intent.putExtra("resours", this@PasochnicaList.intent.extras?.getString("resours", "") ?: "")
                    intent.putExtra("exits", exits)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
                adapter.notifyDataSetChanged()
                binding.listView.invalidate()
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            fileList.addAll(backCopy)
            adapter.notifyDataSetChanged()
            binding.listView.invalidate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_plus) {
            val intent = Intent(this, Pasochnica::class.java)
            intent.putExtra("newFile", true)
            intent.putExtra("fileName", "newFile.html")
            startActivity(intent)
        }
        if (id == R.id.action_open_net_file) {
            val dialogNetFileExplorer = DialogNetFileExplorer()
            dialogNetFileExplorer.show(supportFragmentManager, "dialogNetFileExplorer")
        }
        if (id == R.id.action_open_file) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val fileExplorer = DialogFileExplorer()
                fileExplorer.show(supportFragmentManager, "file_explorer")
            }
        }
        if (id == R.id.action_delite_all) {
            val dialogDeliteAllBackCopy = DialogDeliteAllBackCopy()
            dialogDeliteAllBackCopy.show(supportFragmentManager, "dialogDeliteAllBackCopy")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_piasochnica_list, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private inner class PasochnicaListAdaprer(context: Activity) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, fileList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
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
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        val findDirAsSave = ArrayList<String>()
        fun getFindFileListAsSave() {
            if (MainActivity.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val reqParam = URLEncoder.encode("findDir", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                            val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                            with(mURL.openConnection() as HttpURLConnection) {
                                requestMethod = "POST"
                                val wr = OutputStreamWriter(outputStream)
                                wr.write(reqParam)
                                wr.flush()
                                val sb = StringBuilder()
                                BufferedReader(InputStreamReader(inputStream)).use {
                                    var inputLine = it.readLine()
                                    while (inputLine != null) {
                                        sb.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                }
                                if (responseCode == 200) {
                                    findDirAsSave.clear()
                                    val gson = Gson()
                                    val type = object : TypeToken<ArrayList<String>>() {}.type
                                    findDirAsSave.addAll(gson.fromJson<ArrayList<String>>(sb.toString(), type))
                                }
                            }
                        } catch (e: Throwable) {
                            MainActivity.toastView(Malitounik.applicationContext().getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                }
            }
        }
    }
}