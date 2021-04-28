package by.carkva_gazeta.admin

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import by.carkva_gazeta.admin.databinding.AdminChytannyBinding
import by.carkva_gazeta.malitounik.EditTextRobotoCondensed
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class Chytanny : AppCompatActivity() {
    private lateinit var binding: AdminChytannyBinding
    private var timerCount = 0
    private val timer = Timer()
    private var timerTask: TimerTask? = null
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startTimer() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (urlJob?.isActive == true && timerCount == 6) {
                    urlJob?.cancel()
                    stopTimer()
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.toastView(this@Chytanny, getString(by.carkva_gazeta.malitounik.R.string.bad_internet), Toast.LENGTH_LONG)
                        binding.progressBar2.visibility = View.GONE
                    }
                }
                timerCount++
            }
        }
        timer.schedule(timerTask, 0, 5000)
    }

    private fun stopTimer() {
        timer.cancel()
        timerTask = null
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminChytannyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            binding.progressBar2.visibility = View.VISIBLE
            binding.linear.removeAllViewsInLayout()
            startTimer()
            val text: String = withContext(Dispatchers.IO) {
                val url = "https://carkva-gazeta.by/admin/getFilesCaliandar.php?year=$year"
                val builder = URL(url).readText()
                val gson = Gson()
                val type = object : TypeToken<String>() {}.type
                return@withContext gson.fromJson(builder, type)
            }
            val a: Int = year % 19
            val b: Int = year % 4
            val cx: Int = year % 7
            val k: Int = year / 100
            val p = (13 + 8 * k) / 25
            val q = k / 4
            val m = (15 - p + k - q) % 30
            val n = (4 + k - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            var monthP: Int
            var dataP: Int
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            val fileLine = text.split("\n")
            val nedelName = arrayOf("нядзеля", "панядзелак", "аўторак", "серада", "чацьвер", "пятніца", "субота")
            val monName2 = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
            var countDay = 0
            for (fw in fileLine) {
                if (fw.contains("\$calendar[]")) {
                    val t1 = fw.indexOf("\"cviaty\"=>\"")
                    val t2 = fw.indexOf("\", \"")
                    val t3 = fw.indexOf("\".\$ahref.\"")
                    val t4 = fw.indexOf("</a>\"")
                    val c = GregorianCalendar(year, monthP - 1, dataP + countDay)
                    var data = c[Calendar.DATE]
                    var ned = c[Calendar.DAY_OF_WEEK] - 1
                    var mon = c[Calendar.MONTH]
                    val data2 = c[Calendar.YEAR]
                    var datefull = nedelName[ned] + ", <strong>" + data + " " + monName2[mon] + "</strong> " + year
                    countDay++
                    if (data2 != year) {
                        monthP = 1
                        dataP = 1
                        countDay = if (c.isLeapYear(year)) {
                            1
                        } else {
                            0
                        }
                        data = c[Calendar.DATE]
                        ned = c[Calendar.DAY_OF_WEEK] - 1
                        mon = c[Calendar.MONTH]
                        datefull = nedelName[ned] + ", <strong>" + data + " " + monName2[mon] + "</strong> " + year
                        countDay++
                    }
                    binding.linear.addView(grateTextView(datefull))
                    binding.linear.addView(grateEditView(1, fw.substring(t1 + 11, t2)))
                    binding.linear.addView(grateEditView(2, fw.substring(t3 + 10, t4)))
                } else {
                    binding.linear.addView(grateEditViewHidden(fw))
                }
            }
            stopTimer()
            binding.progressBar2.visibility = View.GONE
        }
        setTollbarTheme()
    }

    private fun grateTextView(text: String): TextViewRobotoCondensed {
        val density = resources.displayMetrics.density
        val padding = 10 * density
        val textView = TextViewRobotoCondensed(this)
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        llp.setMargins(padding.toInt(), padding.toInt(), 0, 0)
        textView.layoutParams = llp
        textView.textSize = SettingsActivity.GET_DEFAULT_FONT_SIZE
        val res = MainActivity.fromHtml(text)
        textView.text = res.trim()
        return textView
    }

    private fun grateEditView(position: Int, text: String): EditTextRobotoCondensed {
        val density = resources.displayMetrics.density
        val padding = 5 * density
        val textView = EditTextRobotoCondensed(this)
        textView.tag = position
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        llp.setMargins(padding.toInt(), padding.toInt(), padding.toInt(), 0)
        textView.layoutParams = llp
        textView.textSize = SettingsActivity.GET_DEFAULT_FONT_SIZE
        textView.setText(text)
        return textView
    }

    private fun grateEditViewHidden(text: String): EditTextRobotoCondensed {
        val textView = EditTextRobotoCondensed(this)
        textView.tag = -1
        textView.setText(text)
        textView.visibility = View.GONE
        return textView
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne2)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            val sb = StringBuilder()
            binding.linear.forEachIndexed { _, view ->
                if (view is EditTextRobotoCondensed) {
                    when (view.tag as Int) {
                        -1 -> {
                            sb.append(view.text.toString())
                        }
                        1 -> {
                            sb.append("\$calendar[]=array(\"cviaty\"=>\"${view.text.toString()}\", \"cytanne\"=>\"\".\$ahref.\"")
                        }
                        2 -> {
                            sb.append("${view.text.toString()}</a>\");\n")
                        }
                    }
                }
            }
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            sendPostRequest(sb.toString().trim(), year)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendPostRequest(cytanni: String, year: Int) {
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode("4", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("cytanni", "UTF-8") + "=" + URLEncoder.encode(cytanni, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("year", "UTF-8") + "=" + URLEncoder.encode(year.toString(), "UTF-8")
                    reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/android.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        responseCodeS = responseCode
                    }
                }
                if (responseCodeS == 200) {
                    MainActivity.toastView(this@Chytanny, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(this@Chytanny, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_chytanny, menu)
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