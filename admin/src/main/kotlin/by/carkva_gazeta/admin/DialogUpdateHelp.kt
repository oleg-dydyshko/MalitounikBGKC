package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminDialogEditviewDisplayBinding
import by.carkva_gazeta.malitounik.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DialogUpdateHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: AdminDialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogUpdateHelpListener? = null

    internal interface DialogUpdateHelpListener {
        fun onUpdate(error: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogUpdateHelpListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogUpdateHelpListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = AdminDialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
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
                    setViersionApp(ver, release)
                }
            }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            if (MainActivity.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.Main).launch {
                    runCatching {
                        val updeteArrayText = withContext(Dispatchers.IO) {
                            var updeteArrayText = mapOf<String, String>()
                            try {
                                val mURL = URL("https://android.carkva-gazeta.by/updateMalitounikBGKC.json")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (conections.responseCode == 200) {
                                    val gson = Gson()
                                    val type = TypeToken.getParameterized(Map::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type
                                    updeteArrayText = gson.fromJson(mURL.readText(), type)
                                }
                            } catch (e: Throwable) {
                                withContext(Dispatchers.Main) {
                                    MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                                }
                            }
                            return@withContext updeteArrayText
                        }
                        if (release) binding.edittext.setText(updeteArrayText["release"])
                        else binding.edittext.setText(updeteArrayText["devel"])
                    }
                }
            }
        }
        return alert
    }

    private fun setViersionApp(releaseCode: String, release: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var code = 500
                withContext(Dispatchers.IO) {
                    runCatching {
                        try {
                            var reqParam: String = URLEncoder.encode("saveProgram", "UTF-8").toString() + "=" + URLEncoder.encode("1", "UTF-8")
                            reqParam += "&" + URLEncoder.encode("updateCode", "UTF-8").toString() + "=" + URLEncoder.encode("1", "UTF-8")
                            reqParam += if (release) "&" + URLEncoder.encode("reliseApp", "UTF-8").toString() + "=" + URLEncoder.encode(releaseCode, "UTF-8")
                            else "&" + URLEncoder.encode("devApp", "UTF-8").toString() + "=" + URLEncoder.encode(releaseCode, "UTF-8")
                            val mURL = URL("https://android.carkva-gazeta.by/admin/android.php")
                            val connection: HttpURLConnection = mURL.openConnection() as HttpURLConnection
                            connection.doOutput = true
                            connection.requestMethod = "POST"
                            val osw = OutputStreamWriter(connection.outputStream)
                            osw.write(reqParam)
                            osw.flush()
                            code = connection.responseCode
                            val sb = StringBuilder()
                            BufferedReader(InputStreamReader(connection.inputStream)).use {
                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    sb.append(inputLine)
                                    inputLine = it.readLine()
                                }
                            }
                        } catch (e: Throwable) {
                            withContext(Dispatchers.Main) {
                                activity?.let {
                                    MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                                }
                            }
                        }
                    }
                }
                mListener?.onUpdate(code != 200)
            }
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