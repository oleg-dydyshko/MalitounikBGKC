package by.carkva_gazeta.admin

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.admin.databinding.AdminPasochnicaBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.apache.commons.text.StringEscapeUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Pasochnica : AppCompatActivity(), View.OnClickListener, DialogPasochnicaFileName.DialogPasochnicaFileNameListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaBinding
    private var resetTollbarJob: Job? = null
    private var fileName = ""

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
        binding = AdminPasochnicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        fileName = intent.extras?.getString("fileName", "") ?: ""
        if (savedInstanceState != null) fileName = savedInstanceState.getString("fileName", "")
        if (fileName != "") getFilePostRequest(fileName)
        val text = intent.extras?.getString("text", "") ?: ""
        if (text != "") {
            val gson = Gson()
            val resours = intent.extras?.getString("resours", "") ?: ""
            val title = intent.extras?.getString("title", "") ?: ""
            fileName = "$title($resours).html"
            if (intent.extras?.getBoolean("exits", false) == false) {
                sendPostRequest(fileName, gson.toJson(text))
                binding.apisanne.setText(MainActivity.fromHtml(text))
            } else {
                getFilePostRequest(fileName)
            }
        }
        setTollbarTheme()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", fileName)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.pasochnica)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
            }
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun setFileName(oldFileName: String, fileName: String) {
        this.fileName = fileName
        saveResult(fileName)
    }

    private fun getFilePostRequest(fileName: String) {
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                var result = ""
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("get", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName, "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        val sb = StringBuilder()
                        BufferedReader(InputStreamReader(inputStream)).use {
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                sb.append(inputLine)
                                inputLine = it.readLine()
                            }
                        }
                        val gson = Gson()
                        val type = object : TypeToken<String>() {}.type
                        result = gson.fromJson(sb.toString(), type)
                    }
                }
                binding.apisanne.setText(MainActivity.fromHtml(result))
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun sendPostRequest(fileName: String, content: String) {
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("save", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        responseCodeS = responseCode
                    }
                }
                if (responseCodeS == 200) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun clearColor(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<font color=\"#d00505\">", position)
            val t2 = result.indexOf("</font>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 22, t2)
                val oldSubText = result.substring(t1, t2 + 7)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</font>", position)
            val t2 = result.indexOf("<font color=\"#d00505\">", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 7, t2)
                val oldSubText = result.substring(t1, t2 + 22)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearBold(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<strong>", position)
            val t2 = result.indexOf("</strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 8, t2)
                val oldSubText = result.substring(t1, t2 + 9)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</strong>", position)
            val t2 = result.indexOf("<strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 9, t2)
                val oldSubText = result.substring(t1, t2 + 8)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearEm(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<em>", position)
            val t2 = result.indexOf("</em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 4, t2)
                val oldSubText = result.substring(t1, t2 + 5)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</em>", position)
            val t2 = result.indexOf("<em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 5, t2)
                val oldSubText = result.substring(t1, t2 + 4)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearHtml(text: String): String {
        var result = text
        val t1 = result.indexOf("<p")
        if (t1 != -1) {
            val t2 = result.indexOf(">")
            val subString = result.substring(t1, t2 + 1)
            var stringres = result.replace(subString, "")
            stringres = stringres.replace("</p>", "<br>")
            stringres = stringres.replace("<span", "<font")
            stringres = stringres.replace("</span>", "</font>")
            stringres = stringres.replace("style=\"color:#D00505;\"", "color=\"#d00505\"")
            stringres = stringres.replace("<i>", "<em>")
            stringres = stringres.replace("</i>", "</em>")
            stringres = stringres.replace("<b>", "<strong>")
            stringres = stringres.replace("</b>", "</strong>")
            stringres = stringres.replace("<u>", "")
            stringres = stringres.replace("</u>", "")
            val t3 = stringres.lastIndexOf("<br>")
            result = stringres.substring(0, t3)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            if (fileName == "") {
                val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance("")
                dialogPasochnicaFileName.show(supportFragmentManager, "dialogPasochnicaFileName")
            } else {
                saveResult(fileName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveResult(fileName: String) {
        val text = binding.apisanne.text
        text?.let {
            var result = MainActivity.toHtml(it)
            result = StringEscapeUtils.unescapeHtml4(result)
            result = clearHtml(result)
            result = clearColor(result)
            result = clearEm(result)
            result = clearBold(result)
            result = clearEm(result)
            result = clearColor(result)
            result = clearBold(result)
            result = clearEm(result)
            if (!result.contains("<!DOCTYPE HTML>")) result = "<!DOCTYPE HTML>$result"
            val gson = Gson()
            sendPostRequest(fileName, gson.toJson(result))
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text
            text?.let { editable ->
                val subtext = editable.getSpans(startSelect, endSelect, StyleSpan(Typeface.BOLD)::class.java)
                var check = false
                subtext.forEach {
                    if (it.style == Typeface.BOLD) {
                        check = true
                        editable.removeSpan(it)
                    }
                }
                if (!check) editable.setSpan(StyleSpan(Typeface.BOLD), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text
            text?.let { editable ->
                val subtext = editable.getSpans(startSelect, endSelect, StyleSpan(Typeface.ITALIC)::class.java)
                var check = false
                subtext.forEach {
                    if (it.style == Typeface.ITALIC) {
                        check = true
                        editable.removeSpan(it)
                    }
                }
                if (!check) editable.setSpan(StyleSpan(Typeface.ITALIC), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text
            text?.let { editable ->
                val subtext = editable.getSpans(startSelect, endSelect, ForegroundColorSpan::class.java)
                var check = false
                subtext.forEach {
                    if (it.foregroundColor == ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)) {
                        check = true
                        editable.removeSpan(it)
                    }
                }
                if (!check) editable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_piasochnica, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }
}