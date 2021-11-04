package by.carkva_gazeta.admin

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.DialogEditviewDisplayBinding
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DialogUpdateHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update)
            val release = arguments?.getBoolean("release", false) ?: false
            val version = if (release) "release"
            else "beta"
            binding.content.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update_all, version)
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update_ok)) { _: DialogInterface, _: Int ->
                val ver = binding.edittext.text.toString()
                if (ver != "") {
                    setViersionApp(ver)
                }
            }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    private fun setViersionApp(releaseCode: String) {
        try {
            val versionCodeDevel = by.carkva_gazeta.malitounik.BuildConfig.VERSION_CODE
            var reqParam: String = URLEncoder.encode("saveProgram", "UTF-8").toString() + "=" + URLEncoder.encode("1", "UTF-8")
            reqParam += "&" + URLEncoder.encode("updateCode", "UTF-8").toString() + "=" + URLEncoder.encode("1", "UTF-8")
            reqParam += "&" + URLEncoder.encode("reliseApp", "UTF-8").toString() + "=" + URLEncoder.encode(releaseCode, "UTF-8")
            reqParam += "&" + URLEncoder.encode("devApp", "UTF-8").toString() + "=" + URLEncoder.encode(versionCodeDevel.toString(), "UTF-8")
            val mURL = URL("https://carkva-gazeta.by/admin/android.php")
            val connection: HttpURLConnection = mURL.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.requestMethod = "POST"
            val osw = OutputStreamWriter(connection.outputStream)
            osw.write(reqParam)
            osw.flush()
            connection.responseCode
        } catch (ignored: Throwable) {
        }
    }

    companion object {
        fun newInstance(release: Boolean): DialogUpdateHelp {
            val dialogSaveAsHelp = DialogUpdateHelp()
            val bundle = Bundle()
            bundle.putBoolean("release", release)
            dialogSaveAsHelp.arguments = bundle
            return dialogSaveAsHelp
        }
    }
}