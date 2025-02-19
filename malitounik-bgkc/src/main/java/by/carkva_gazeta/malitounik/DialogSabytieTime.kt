package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSettimeDisplayBinding
import java.util.Calendar

class DialogSabytieTime : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var dialogSabytieTimeListener: DialogSabytieTimeListener? = null
    private var _binding: DialogSettimeDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogSabytieTimeListener {
        fun sabytieTimePositive(nomerDialoga: Int, hour: Int, minute: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            dialogSabytieTimeListener = try {
                context as DialogSabytieTimeListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSabytieTimeListener")
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogSettimeDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            val c = Calendar.getInstance()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val ad = AlertDialog.Builder(it, style)
            binding.title.text = getString(R.string.set_time_sabytie)
            binding.content.setIs24HourView(true)
            val settime = arguments?.getString("time")?.split(":")
            c.set(Calendar.HOUR_OF_DAY, settime?.get(0)?.toInt() ?: 0)
            c.set(Calendar.MINUTE, settime?.get(1)?.toInt() ?: 0)
            c.set(Calendar.SECOND, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.content.hour = c[Calendar.HOUR_OF_DAY]
                binding.content.minute = c[Calendar.MINUTE]
            } else {
                binding.content.currentHour = c[Calendar.HOUR_OF_DAY]
                binding.content.currentMinute = c[Calendar.MINUTE]
            }
            val nomerDialoga = arguments?.getInt("numarDialoga") ?: 1
            ad.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialogSabytieTimeListener?.sabytieTimePositive(nomerDialoga, binding.content.hour, binding.content.minute)
                } else {
                    dialogSabytieTimeListener?.sabytieTimePositive(nomerDialoga, binding.content.currentHour, binding.content.currentMinute)
                }
            }
            ad.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            ad.setView(binding.root)
            alert = ad.create()
        }
        return alert
    }

    companion object {
        fun getInstance(numarDialoga: Int, time: String): DialogSabytieTime {
            val bundle = Bundle()
            bundle.putInt("numarDialoga", numarDialoga)
            bundle.putString("time", time)
            val dialog = DialogSabytieTime()
            dialog.arguments = bundle
            return dialog
        }
    }
}