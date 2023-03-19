package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogSabytieShow : DialogFragment() {
    private var title = ""
    private var data = ""
    private var time = ""
    private var dataK = ""
    private var timeK = ""
    private var res = ""
    private var paz = false
    private var konecSabytie = true
    private var color = 0
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title") ?: ""
        data = arguments?.getString("data") ?: ""
        time = arguments?.getString("time") ?: ""
        dataK = arguments?.getString("dataK") ?: ""
        timeK = arguments?.getString("timeK") ?: ""
        res = arguments?.getString("res") ?: ""
        paz = arguments?.getBoolean("paz") ?: false
        konecSabytie = arguments?.getBoolean("konecSabytie") ?: true
        color = arguments?.getInt("color") ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            binding.title.text = title
            binding.title.setBackgroundColor(Color.parseColor(Sabytie.getColors(it, color)))
            val textR = if (konecSabytie) {
                SpannableString(getString(R.string.sabytie_kali, data, time, res))
            } else {
                SpannableString(getString(R.string.sabytie_pachatak_show, data, time, dataK, timeK, res))
            }
            val t1 = textR.indexOf(res)
            if (dzenNoch) {
                binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                if (paz)
                    textR.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary_black)), t1, textR.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                if (paz)
                    textR.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary)), t1, textR.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val am = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!am.canScheduleExactAlarms() && res != getString(R.string.sabytie_no_pavedam)) {
                    textR.setSpan(StrikethroughSpan(), t1, textR.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            binding.content.text = textR
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    companion object {
        fun getInstance(title: String, data: String, time: String, dataK: String, timeK: String, res: String, paz: Boolean, konecSabytie: Boolean, color: Int): DialogSabytieShow {
            val dialogShowSabytie = DialogSabytieShow()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("data", data)
            bundle.putString("time", time)
            bundle.putString("dataK", dataK)
            bundle.putString("timeK", timeK)
            bundle.putString("res", res)
            bundle.putBoolean("paz", paz)
            bundle.putBoolean("konecSabytie", konecSabytie)
            bundle.putInt("color", color)
            dialogShowSabytie.arguments = bundle
            return dialogShowSabytie
        }
    }
}