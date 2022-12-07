package by.carkva_gazeta.resources

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class DialogLiturgia : DialogFragment() {
    private var chast = 1
    private lateinit var ab: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chast = arguments?.getInt("chast") ?: 1
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity ->
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(activity))
            MainActivity.dialogVisable = true
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            var style = by.carkva_gazeta.malitounik.R.style.AlertDialogTheme
            if (dzenNoch) style = by.carkva_gazeta.malitounik.R.style.AlertDialogThemeBlack
            ab = AlertDialog.Builder(activity, style)
            val builder = StringBuilder()
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val r = activity.resources
            var inputStream: InputStream = r.openRawResource(R.raw.bogashlugbovya1_1)
            when (chast) {
                1 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_1)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.ps_102)
                }
                2 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_2)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.ps_91)
                }
                3 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_3)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.ps_145)
                }
                4 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_4)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.ps_92)
                }
                5 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_5)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.mc_5_3_12)
                }
                6 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_6)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.malitva_za_pamerlyx)
                }
                7 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_7)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.malitva_za_paclicanyx)
                }
                8 -> {
                    binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne).uppercase()
                    val zch = ZmenyiaChastki()
                    zch.setDzenNoch(dzenNoch)
                    builder.append(zch.sviatyiaView(1))
                }
                9 -> {
                    binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne).uppercase()
                    val zch = ZmenyiaChastki()
                    zch.setDzenNoch(dzenNoch)
                    builder.append(zch.sviatyiaView(0))
                }
                10 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_8)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.ps_94)
                }
                11 -> {
                    inputStream = r.openRawResource(R.raw.viaczernia_bierascie_1)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.viaczernia_bierascie_1)
                }
                12 -> {
                    inputStream = r.openRawResource(R.raw.viaczernia_bierascie_2)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.viaczernia_bierascie_2)
                }
                13 -> {
                    inputStream = r.openRawResource(R.raw.viaczernia_bierascie_3)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.viaczernia_bierascie_3)
                }
                14 -> {
                    inputStream = r.openRawResource(R.raw.bogashlugbovya1_9)
                    binding.title.setText(by.carkva_gazeta.malitounik.R.string.malitva_za_paclicanyx_i_jyvyx)
                }
            }
            if (!(chast == 8 || chast == 9)) {
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                reader.forEachLine {
                    line = it
                    if (dzenNoch) line = line.replace("#d00505", "#f44336")
                    builder.append(line).append("\n")
                }
                inputStream.close()
            }
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT))
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            binding.content.text = MainActivity.fromHtml(builder.toString())
            ab.setView(binding.root)
            ab.setPositiveButton(activity.resources.getString(by.carkva_gazeta.malitounik.R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        }
        return ab.create()
    }

    companion object {
        fun getInstance(chast: Int): DialogLiturgia {
            val instance = DialogLiturgia()
            val args = Bundle()
            args.putInt("chast", chast)
            instance.arguments = args
            return instance
        }
    }
}