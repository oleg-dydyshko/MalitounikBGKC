package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import by.carkva_gazeta.malitounik.databinding.DialogWebviewDisplayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DialogProgramPadoiMaryia : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogWebviewDisplayBinding? = null
    private val binding get() = _binding!!
    private val dzenNoch: Boolean
        get() {
            activity?.let {
                return (it as BaseActivity).getBaseDzenNoch()
            }
            return false
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogWebviewDisplayBinding.inflate(LayoutInflater.from(it))
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val webSettings = binding.content.settings
            if (dzenNoch) {
                binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    @Suppress("DEPRECATION") WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                }
            } else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = getString(R.string.program_radio_maryia)
            sendTitlePadioMaryia()
            webSettings.standardFontFamily = "sans-serif-condensed"
            webSettings.defaultFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT.toInt()
            webSettings.domStorageEnabled = true
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getText(R.string.ok)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
        }
        return alert
    }

    private fun sendTitlePadioMaryia() {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar.visibility = View.VISIBLE
                runCatching {
                    withContext(Dispatchers.IO) {
                        try {
                            var efir = arguments?.getString("titleRadyjoMaryia") ?: ""
                            if (efir == "") {
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
                                    withContext(Dispatchers.Main) {
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
                                }
                            } else {
                                efir = "<strong>Цяпер у эфіры:</strong><br><em>$efir</em>"
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
                                    binding.content.loadDataWithBaseURL(null, text.trim(), "text/html", "utf-8", null)
                                }
                            }
                        } catch (_: Throwable) {
                        }
                    }
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        fun getInstance(titleRadyjoMaryia: String): DialogProgramPadoiMaryia {
            val bundle = Bundle()
            bundle.putString("titleRadyjoMaryia", titleRadyjoMaryia)
            val dialog = DialogProgramPadoiMaryia()
            dialog.arguments = bundle
            return dialog
        }
    }
}