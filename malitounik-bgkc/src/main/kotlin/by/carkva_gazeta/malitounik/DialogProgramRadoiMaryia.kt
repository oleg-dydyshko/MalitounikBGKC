package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import by.carkva_gazeta.malitounik.databinding.DialogWebviewDisplayBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DialogProgramRadoiMaryia : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var binding: DialogWebviewDisplayBinding? = null
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
            binding = DialogWebviewDisplayBinding.inflate(LayoutInflater.from(activity))
            binding?.let {
                var style = R.style.AlertDialogTheme
                if (dzenNoch) style = R.style.AlertDialogThemeBlack
                val builder = AlertDialog.Builder(activity, style)
                val webSettings = it.content.settings
                if (dzenNoch) {
                    it.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary_black))
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                        @Suppress("DEPRECATION") WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                    }
                } else it.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                it.title.text = getString(R.string.program_radio_maryia)
                sendTitlePadioMaryia()
                webSettings.standardFontFamily = "sans-serif-condensed"
                webSettings.defaultFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT.toInt()
                webSettings.domStorageEnabled = true
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
                                val builder = StringBuilder()
                                val inputStream = resources.openRawResource(R.raw.radio_maria_style)
                                val isr = InputStreamReader(inputStream)
                                val reader = BufferedReader(isr)
                                var line: String
                                reader.use { bufferedReader ->
                                    bufferedReader.forEachLine {
                                        line = it
                                        builder.append(line)
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    var text = sb.toString()
                                    text = text.replace("<h1>Праграма</h1>", "")
                                    val t1 = text.indexOf("<div class=\"program\">", ignoreCase = true)
                                    if (t1 != -1) {
                                        val t2 = text.indexOf("<div id=\"sidebar-2\"", t1, ignoreCase = true)
                                        if (t2 != -1) {
                                            text = text.substring(t1, t2)
                                        }
                                    }
                                    val style = if (dzenNoch) "<style type=\"text/css\">a {color:#f44336;} body{color: #fff; background-color: #424242;}</style>\n"
                                    else "<style type=\"text/css\">a {color:#d00505;} body{color: #000; background-color: #fff;}</style>\n"
                                    text = "<html><head>$style$builder</head><body>$efir$text</body></html>"
                                    binding?.content?.loadDataWithBaseURL(null, text.trim(), "text/html", "utf-8", null)
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