package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class DialogCheckLogFile : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogCheckLogFileListener? = null
    private var isClose = false

    interface DialogCheckLogFileListener {
        fun dialogCheckLogFileClose(isClose: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isClose = arguments?.getBoolean("isClose") ?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogCheckLogFileListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogCheckLogFileListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun saveLogFile(url: String, count: Int = 0) {
        activity?.let { activity ->
            var error = false
            val logFile = File("${activity.filesDir}/cache/log.txt")
            logFile.writer().use {
                it.write(url)
            }
            Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
                MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.error))
                error = true
            }.await()
            if (error && count < 2) {
                saveLogFile(url, count + 1)
            }
            mListener?.dialogCheckLogFileClose(isClose)
        }
    }

    private suspend fun loadLogFile(count: Int = 0) {
        activity?.let { activity ->
            val sb = StringBuilder()
            val logFile = File("${activity.filesDir}/cache/log.txt")
            var error = false
            Malitounik.referens.child("/admin/log.txt").getFile(logFile).addOnFailureListener {
                MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.error))
                error = true
            }.await()
            if (error && count < 2) {
                loadLogFile(count + 1)
                return
            }
            logFile.readLines().forEach {
                sb.append("$it\n")
            }
            binding.content.setText(sb.toString(), TextView.BufferType.EDITABLE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_dialog_check_log_title)
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int ->
                CoroutineScope(Dispatchers.Main).launch {
                    saveLogFile(binding.content.text.toString())
                }
            }
            builder.setView(binding.root)
            CoroutineScope(Dispatchers.Main).launch {
                loadLogFile()
            }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(isClose: Boolean): DialogCheckLogFile {
            val dialogDelite = DialogCheckLogFile()
            val bundle = Bundle()
            bundle.putBoolean("isClose", isClose)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}