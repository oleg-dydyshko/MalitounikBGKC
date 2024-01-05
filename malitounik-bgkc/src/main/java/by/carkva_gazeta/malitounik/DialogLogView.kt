package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
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

    interface DialogLogViewListener {
        fun createAndSentFile(log: ArrayList<String>)
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

    private suspend fun getLogFile(count: Int = 0): String {
        val sb = StringBuilder()
        activity?.let { fragmentActivity ->
            val localFile = File("${fragmentActivity.filesDir}/cache/log.txt")
            var error = false
            Malitounik.referens.child("/admin/log.txt").getFile(localFile).addOnFailureListener {
                MainActivity.toastView(fragmentActivity, getString(R.string.error))
                error = true
            }.await()
            if (error && count < 2) {
                getLogFile(count + 1)
                return ""
            }
            localFile.readLines().forEach {
                if (it.isNotEmpty()) {
                    if (it.contains(" -->")) {
                        val t1 = it.indexOf(" -->")
                        log.add(it.substring(0, t1))
                    } else {
                        log.add(it)
                    }
                    sb.append(it).append("\n")
                }
            }
            //val list = Malitounik.referens.child("/admin").list(1000).await()
            //runPrefixes(list)
        }
        return sb.toString().trim()
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
                if (MainActivity.isNetworkAvailable()) {
                    logJob = CoroutineScope(Dispatchers.Main).launch {
                        displayBinding.content.text = getLogFile()
                    }
                }
                displayBinding.content.typeface = MainActivity.createFont(Typeface.BOLD)
                if (dzenNoch) displayBinding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorWhite))
                else displayBinding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary_text))
                ad.setView(displayBinding.root)
                ad.setPositiveButton(resources.getString(R.string.set_log)) { _: DialogInterface, _: Int ->
                    mListener?.createAndSentFile(log)
                }
                ad.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
            }
            alert = ad.create()
        }
        return alert
    }

    /*private suspend fun runPrefixes(list: ListResult) {
        list.prefixes.forEach {
            if (it.name != "piasochnica") {
                val list2 = it.list(1000).await()
                runPrefixes(list2)
                runItems(list2)
            }
        }
    }

    private suspend fun runItems(list: ListResult) {
        activity?.let { activity ->
            val dir = File("${activity.filesDir}/test")
            if (!dir.exists()) dir.mkdir()
            list.items.forEach {
                val localFile = File("${activity.filesDir}/test/${it.name}")
                val pathReference = Malitounik.referens.child(it.path)
                pathReference.getFile(localFile).await()
                Log.d("Oleg", it.path)
            }
        }
    }*/
}