package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBinding
import by.carkva_gazeta.admin.databinding.PiasochnicaNetFileExplorerBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class PiasochnicaNetFileExplorer : BaseActivity(), DialogContextMenu.DialogContextMenuListener, DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogDelite.DialogDeliteListener {

    private lateinit var adapter: TitleListAdaprer
    private val fileList = ArrayList<MyNetFile>()
    private var dir = ""
    private lateinit var binding: PiasochnicaNetFileExplorerBinding
    private var netFileJob: Job? = null
    private var resetTollbarJob: Job? = null

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onPause() {
        super.onPause()
        netFileJob?.cancel()
        resetTollbarJob?.cancel()
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

    override fun setFileName(oldFileName: String, fileName: String, isSite: Boolean, saveAs: Boolean) {
        if (oldFileName.contains("(BackCopy")) {
            val fileNameold = oldFileName.replace("(BackCopy)", "")
            val fileOld = File(getExternalFilesDir("PiasochnicaBackCopy"), fileNameold)
            val fileNew = File(getExternalFilesDir("PiasochnicaBackCopy"), fileName.replace("(BackCopy)", ""))
            fileOld.renameTo(fileNew)
            getDirListRequest(dir)
        } else {
            getFileRenamePostRequest(oldFileName, fileName, isSite)
        }
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        if (title.contains("(BackCopy")) {
            val fileNameold = title.replace("(BackCopy)", "")
            val fileOld = File(getExternalFilesDir("PiasochnicaBackCopy"), fileNameold)
            if (fileOld.exists()) fileOld.delete()
            getDirListRequest(dir)
        } else {
            getFileUnlinkPostRequest(title, isSite)
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditor = k.edit()
        prefEditor.remove("admin" + title + "position")
        prefEditor.apply()
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PiasochnicaNetFileExplorerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        adapter = TitleListAdaprer(this)
        binding.content.adapter = adapter
        if (savedInstanceState != null) {
            dir = savedInstanceState.getString("dir") ?: ""
        }
        getDirListRequest(dir)
        binding.content.setOnItemLongClickListener { _, _, position, _ ->
            if (!(fileList[position].resources == R.drawable.directory_up || fileList[position].resources == R.drawable.directory_icon)) {
                val contextMenu = DialogContextMenu.getInstance(position, dir + "/" + fileList[position].title, true)
                contextMenu.show(supportFragmentManager, "contextMenu")
            }
            return@setOnItemLongClickListener true
        }
        binding.content.setOnItemClickListener { _, _, position, _ ->
            when (fileList[position].resources) {
                R.drawable.directory_up -> {
                    val t1 = dir.lastIndexOf("/")
                    dir = dir.substring(0, t1)
                    getDirListRequest(dir)
                }

                R.drawable.directory_icon -> {
                    dir = dir + "/" + fileList[position].title
                    getDirListRequest(dir)
                }

                else -> {
                    val intent = Intent()
                    intent.putExtra("dirToFile", dir + "/" + fileList[position].title)
                    setResult(Activity.RESULT_OK, intent)
                    onBack()
                }
            }
        }
        setTollbarTheme()
        setResult(Activity.RESULT_CANCELED)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)
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

    private fun getDirListRequest(dir: String) {
        if (MainActivity.isNetworkAvailable()) {
            netFileJob = CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    fileList.clear()
                    val temp = ArrayList<MyNetFile>()
                    val list = Malitounik.referens.child("/$dir").list(1000).await()
                    if (dir != "") {
                        val t1 = dir.lastIndexOf("/")
                        temp.add(MyNetFile(R.drawable.directory_up, dir.substring(t1 + 1)))
                    }
                    list.prefixes.forEach {
                        temp.add(MyNetFile(R.drawable.directory_icon, it.name))
                    }
                    list.items.forEach {
                        if (it.name.contains(".htm")) {
                            temp.add(MyNetFile(R.drawable.file_html_icon, it.name))
                        } else if (it.name.contains(".json")) {
                            temp.add(MyNetFile(R.drawable.file_json_icon, it.name))
                        } else if (it.name.contains(".php")) {
                            temp.add(MyNetFile(R.drawable.file_php_icon, it.name))
                        } else {
                            temp.add(MyNetFile(R.drawable.file_txt_icon, it.name))
                        }
                    }
                    fileList.addAll(temp)
                    adapter.notifyDataSetChanged()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PiasochnicaNetFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
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
                    MainActivity.toastView(this@PiasochnicaNetFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                if (isSite) saveLogFile()
                binding.progressBar2.visibility = View.GONE
                getDirListRequest(dir)
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
            MainActivity.toastView(this@PiasochnicaNetFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error))
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
                            MainActivity.toastView(this@PiasochnicaNetFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }.await()
                        Malitounik.referens.child("/$oldFileName").delete().await()
                        Malitounik.referens.child("/$fileName").putFile(Uri.fromFile(localFile)).await()
                    } else {
                        Malitounik.referens.child("/admin/piasochnica/$oldFileName").getFile(localFile).addOnFailureListener {
                            MainActivity.toastView(this@PiasochnicaNetFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }.await()
                        Malitounik.referens.child("/admin/piasochnica/$oldFileName").delete().await()
                        Malitounik.referens.child("/admin/piasochnica/$fileName").putFile(Uri.fromFile(localFile)).await()
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@PiasochnicaNetFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                if (isSite) saveLogFile()
                binding.progressBar2.visibility = View.GONE
                getDirListRequest(dir)
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("dir", dir)
    }

    private inner class TitleListAdaprer(private val mContext: Activity) : ArrayAdapter<MyNetFile>(mContext, R.layout.admin_simple_list_item, fileList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = AdminSimpleListItemBinding.inflate(mContext.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = fileList[position].title
            val image = ContextCompat.getDrawable(mContext, fileList[position].resources)
            val density = resources.displayMetrics.density.toInt()
            image?.setBounds(0, 0, 48 * density, 48 * density)
            viewHolder.text.setCompoundDrawables(image, null, null, null)
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    private data class MyNetFile(val resources: Int, val title: String)
}