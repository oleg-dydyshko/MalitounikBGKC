package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogProgramRadioMariaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DialogProgramRadoiMaryia : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var binding: DialogProgramRadioMariaBinding? = null
    private var sendTitlePadioMaryiaJob: Job? = null
    private val dzenNoch: Boolean
        get() {
            activity?.let {
                if (it is BaseActivity) {
                    return it.getBaseDzenNoch()
                }
            }
            return false
        }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity?.let {
            if (it !is BaseActivity) it.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sendTitlePadioMaryiaJob?.cancel()
        binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity ->
            binding = DialogProgramRadioMariaBinding.inflate(layoutInflater)
            binding?.let {
                var style = R.style.AlertDialogTheme
                if (dzenNoch) style = R.style.AlertDialogThemeBlack
                val builder = AlertDialog.Builder(activity, style)
                it.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                it.title.text = getString(R.string.program_radio_maryia)
                sendTitlePadioMaryia()
                builder.setView(it.root)
                builder.setPositiveButton(resources.getText(R.string.close)) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
                alert = builder.create()
                if (activity !is BaseActivity) {
                    val intent = Intent(activity, WidgetRadyjoMaryia::class.java)
                    intent.putExtra("action", ServiceRadyjoMaryia.WIDGET_RADYJO_MARYIA_PROGRAM_EXIT)
                    activity.sendBroadcast(intent)
                }
            }
        }
        return alert
    }

    private fun sendTitlePadioMaryia() {
        if (MainActivity.isNetworkAvailable()) {
            sendTitlePadioMaryiaJob = CoroutineScope(Dispatchers.Main).launch {
                binding?.progressBar?.visibility = View.VISIBLE
                runCatching {
                    withContext(Dispatchers.IO) {
                        try {
                            var efir: String
                            val mURL1 = URL("https://radiomaria.by/player/hintbackend.php")
                            with(mURL1.openConnection() as HttpURLConnection) {
                                val sb = StringBuilder()
                                BufferedReader(InputStreamReader(inputStream)).use {
                                    var inputLine = it.readLine()
                                    while (inputLine != null) {
                                        sb.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                }
                                var text = MainActivity.fromHtml(sb.toString()).toString().trim()
                                val t1 = text.indexOf(":", ignoreCase = true)
                                if (t1 != -1) {
                                    text = text.substring(t1 + 1)
                                }
                                val t2 = text.indexOf(">", ignoreCase = true)
                                if (t2 != -1) {
                                    text = text.substring(t2 + 1)
                                }
                                efir = "<strong>Цяпер у эфіры:</strong><br><em>" + text.trim() + "</em>"
                            }
                            val mURL = URL("https://radiomaria.by/program")
                            with(mURL.openConnection() as HttpURLConnection) {
                                val sb = StringBuilder()
                                BufferedReader(InputStreamReader(inputStream)).use {
                                    var inputLine = it.readLine()
                                    while (inputLine != null) {
                                        sb.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    var text = sb.toString()
                                    text = text.replace("<h1>Праграма</h1>", "")
                                    text = text.replace("<div class=\"dayhdr\">", "<strong>")
                                    text = text.replace("</div><ul>", "</strong><p>")
                                    text = text.replace("</ul>", "")
                                    text = text.replace("<li>", "")
                                    text = text.replace("</div>", "")
                                    text = text.replace("</li>", "<p>")
                                    text = text.replace("<span class=\"pstarttime\">", "– ")
                                    text = text.replace("</span>", "")
                                    text = text.replace("<div class=\"programday\">", "")
                                    text = text.replace("<span class=\"ptitle\">", " ")
                                    val t1 = text.indexOf("<div class=\"program\">", ignoreCase = true)
                                    if (t1 != -1) {
                                        val t2 = text.indexOf("<div id=\"sidebar-2\"", t1, ignoreCase = true)
                                        if (t2 != -1) {
                                            text = text.substring(t1, t2)
                                        }
                                    }
                                    val t2 = text.lastIndexOf("<div style=\"clear: both;\">")
                                    if (t2 != -1) {
                                        text = text.substring(0, t2)
                                    }
                                    text = text.replace("<div style=\"clear: both;\">", "")
                                    text = "$efir$text"
                                    binding?.content?.text = MainActivity.fromHtml(text.trim())
                                }
                            }
                        } catch (_: Throwable) {
                        }
                    }
                }
                binding?.progressBar?.visibility = View.GONE
            }
        }
    }
}