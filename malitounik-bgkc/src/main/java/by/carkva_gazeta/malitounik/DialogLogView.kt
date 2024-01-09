package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class DialogLogView : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var binding: DialogTextviewDisplayBinding? = null
    private var log = ArrayList<String>()
    private var mListener: DialogLogViewListener? = null
    private var logJob: Job? = null
    private val sb = StringBuilder()

    interface DialogLogViewListener {
        fun createAndSentFile(log: ArrayList<String>, fileList: String)
        fun dialogLogViewClose()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogLogViewListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogLogViewListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPause() {
        super.onPause()
        logJob?.cancel()
    }

    private suspend fun getLogFile(count: Int = 0) {
        activity?.let { fragmentActivity ->
            val localFile = File("${fragmentActivity.filesDir}/cache/log.txt")
            var error = false
            Malitounik.referens.child("/admin/adminListFile.txt").getFile(localFile).addOnFailureListener {
                MainActivity.toastView(fragmentActivity, getString(R.string.error))
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
            if (log.isEmpty()) {
                binding?.content?.text = getString(R.string.admin_upload_contine)
            } else {
                val strB = java.lang.StringBuilder()
                log.forEach {
                    strB.append(it)
                    strB.append("\n")
                }
                binding?.content?.text = strB.toString()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            val dzenNoch = (fragmentActivity as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(fragmentActivity, style)
            binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(fragmentActivity))
            binding?.let { displayBinding ->
                if (dzenNoch) displayBinding.title.setBackgroundColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary_black))
                else displayBinding.title.setBackgroundColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary))
                displayBinding.title.text = getString(R.string.log)
                if (MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI)) {
                    logJob = CoroutineScope(Dispatchers.Main).launch {
                        val localFile = File("${fragmentActivity.filesDir}/cache/cache.txt")
                        Malitounik.referens.child("/admin/log.txt").getFile(localFile)
                        val log = localFile.readText()
                        if (log != "") {
                            getLogFile()
                        } else {
                            displayBinding.content.text = getString(R.string.admin_upload_log_contine)
                            if (dzenNoch) displayBinding.content.background = ContextCompat.getDrawable(fragmentActivity, R.drawable.selector_dark)
                            else displayBinding.content.background = ContextCompat.getDrawable(fragmentActivity, R.drawable.selector_default)
                            displayBinding.content.setOnClickListener {
                                displayBinding.content.setOnClickListener(null)
                                displayBinding.content.background = null
                                logJob?.cancel()
                                logJob = CoroutineScope(Dispatchers.Main).launch {
                                    getLogFile()
                                }
                            }
                        }
                    }
                } else {
                    displayBinding.content.text = getString(R.string.admin_upload_no_wi_fi)
                }
                if (dzenNoch) displayBinding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorWhite))
                else displayBinding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary_text))
                ad.setView(displayBinding.root)
                ad.setPositiveButton(resources.getString(R.string.set_log)) { _: DialogInterface, _: Int ->
                    if (logJob?.isActive != true) {
                        mListener?.createAndSentFile(log, sb.toString())
                    }
                }
                ad.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface, _: Int ->
                    mListener?.dialogLogViewClose()
                }
            }
            alert = ad.create()
        }
        return alert
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
        binding?.content?.text = path
    }
}