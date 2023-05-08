package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewCheckboxDisplayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DialogLogView : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewCheckboxDisplayBinding? = null
    private val binding get() = _binding!!
    private var log = ArrayList<String>()
    private var mListener: DialogLogViewListener? = null
    private var logJob: Job? = null

    interface DialogLogViewListener {
        fun createAndSentFile(log: ArrayList<String>, isClear: Boolean)
        fun clearLogFile(isClear: Boolean)
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
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        logJob?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            _binding = DialogTextviewCheckboxDisplayBinding.inflate(LayoutInflater.from(fragmentActivity))
            val dzenNoch = (fragmentActivity as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(fragmentActivity, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary))
            binding.title.text = getString(R.string.log)
            logJob = CoroutineScope(Dispatchers.Main).launch {
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("log", "txt")
                }
                Malitounik.referens.child("/admin/log.txt").getFile(localFile).addOnFailureListener {
                    MainActivity.toastView(fragmentActivity, getString(R.string.error))
                }.await()
                val sb = SpannableStringBuilder()
                localFile.readLines().forEach {
                    if (it.isNotEmpty()) {
                        log.add(it)
                        val t1 = it.lastIndexOf("/")
                        if (t1 != -1) {
                            val t2 = it.lastIndexOf("/", t1 - 1)
                            if (t2 != -1) {
                                val span = SpannableString(it.substring(t2))
                                span.setSpan(StyleSpan(Typeface.BOLD), 0, t1 - t2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                sb.append(span).append("\n")
                            }
                        }
                    }
                }
                binding.content.text = sb
            }
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary_text))
            binding.checkbox.typeface = MainActivity.createFont(Typeface.NORMAL)
            binding.checkbox.text = getString(R.string.clear_log)
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.set_log)) { _: DialogInterface, _: Int ->
                mListener?.createAndSentFile(log, binding.checkbox.isChecked)
            }
            ad.setNegativeButton(getString(R.string.cansel)) { _: DialogInterface, _: Int ->
                mListener?.clearLogFile(binding.checkbox.isChecked)
            }
            alert = ad.create()
        }
        return alert
    }
}