package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogUpdateMalitounikBinding
import java.text.DecimalFormat

class DialogUpdateMalitounik : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogUpdateMalitounikBinding? = null
    private val binding get() = _binding!!
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateProgress(total: Double, bytesDownload: Double) {
        val totalSizeUpdate = if (total / 1024 > 1000) {
            " ${formatFigureTwoPlaces(total / 1024 / 1024)} Мб "
        } else {
            " ${formatFigureTwoPlaces(total / 1024)} Кб "
        }
        val bytesDownloadUpdate = if (bytesDownload / 1024 > 1000) {
            " ${formatFigureTwoPlaces(bytesDownload / 1024 / 1024)} Мб "
        } else {
            " ${formatFigureTwoPlaces(bytesDownload / 1024)} Кб "
        }
        binding.progressBar.max = total.toInt()
        binding.progressBar.progress = bytesDownload.toInt()
        binding.textProgress.text = getString(R.string.update_program_progress, bytesDownloadUpdate, totalSizeUpdate)
    }

    fun updateComplete() {
        dialog?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogUpdateMalitounikBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val title = arguments?.getString("title", getString(R.string.update_title)) ?: getString(R.string.update_title)
            binding.title.text = title
            builder.setView(binding.root)
            if (title == getString(R.string.update_title)) {
                builder.setPositiveButton(resources.getText(R.string.close)) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
            }
            alert = builder.create()
        }
        return alert
    }

    private fun formatFigureTwoPlaces(value: Double): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value)
    }

    companion object {
        fun getInstance(title: String): DialogUpdateMalitounik {
            val dialogDelite = DialogUpdateMalitounik()
            val bundle = Bundle()
            bundle.putString("title", title)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}