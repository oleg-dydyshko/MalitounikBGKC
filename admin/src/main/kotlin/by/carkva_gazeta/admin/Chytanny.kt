package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import by.carkva_gazeta.admin.databinding.AdminChytannyBinding
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class Chytanny : AppCompatActivity() {
    private lateinit var binding: AdminChytannyBinding
    private var timerCount = 0
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val data = ArrayList<String>()

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
                        MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.bad_internet), Toast.LENGTH_LONG)
                        binding.progressBar2.visibility = View.GONE
                    }
                }
                timerCount++
            }
        }
        timer = Timer()
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

    private fun loadChytanny(year: Int) {
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            binding.linear.removeAllViewsInLayout()
            startTimer()
            val text = withContext(Dispatchers.IO) {
                val url = "https://carkva-gazeta.by/admin/getFilesCaliandar.php?year=$year"
                val builder = URL(url).readText()
                val gson = Gson()
                val type = object : TypeToken<ArrayList<String>>() {}.type
                return@withContext gson.fromJson<ArrayList<String>>(builder, type)
            }
            val a = year % 19
            val b = year % 4
            val cx = year % 7
            val k = year / 100
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
            val fileLine = text[0].split("\n")
            val nedelName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.dni_nedeli)
            val monName2 = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
            var countDay = 0
            for (fw in fileLine) {
                if (fw.contains("\$calendar[]")) {
                    val t1 = fw.indexOf("\"cviaty\"=>\"")
                    val t2 = fw.indexOf("\", \"")
                    val t3 = fw.indexOf("\".\$ahref.\"")
                    val t4 = fw.indexOf("</a>\"")
                    val c = GregorianCalendar(year, monthP - 1, dataP + countDay)
                    var data = c[Calendar.DATE]
                    var ned = c[Calendar.DAY_OF_WEEK]
                    var mon = c[Calendar.MONTH]
                    val data2 = c[Calendar.YEAR]
                    var datefull = SpannableString(nedelName[ned] + ", " + data + " " + monName2[mon] + " " + year)
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
                        ned = c[Calendar.DAY_OF_WEEK]
                        mon = c[Calendar.MONTH]
                        datefull = SpannableString(nedelName[ned] + ", " + data + " " + monName2[mon] + " " + year)
                        countDay++
                    }
                    val c1 = nedelName[ned].length
                    val c2 = data.toString().length
                    val c3 = monName2[mon].length
                    datefull.setSpan(StyleSpan(Typeface.BOLD), c1 + 2, c1 + 2 + c2 + c3 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    val font = MainActivity.createFont(Typeface.NORMAL)
                    val font2 = MainActivity.createFont(Typeface.BOLD)
                    datefull.setSpan(CustomTypefaceSpan("", font), 0, c1 + 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    datefull.setSpan(CustomTypefaceSpan("", font2), c1 + 2, c1 + 2 + c2 + c3 + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    datefull.setSpan(CustomTypefaceSpan("", font), c1 + 2 + c2 + c3 + 1, datefull.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminChytannyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) data.add(i.toString())
        binding.spinnerYear.adapter = SpinnerAdapter(this, data)
        binding.spinnerYear.setSelection(2)
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadChytanny(data[position].toInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        setTollbarTheme()
    }

    private fun grateTextView(text: SpannableString): TextView {
        val density = resources.displayMetrics.density
        val padding = 10 * density
        val textView = TextView(this)
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        llp.setMargins(padding.toInt(), padding.toInt(), 0, 0)
        textView.layoutParams = llp
        textView.textSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        textView.text = text.trim()
        return textView
    }

    private fun grateEditView(position: Int, text: String): EditText {
        val density = resources.displayMetrics.density
        val padding = 5 * density
        val textView = EditTextCustom(this)
        textView.typeface = MainActivity.createFont(Typeface.NORMAL)
        textView.tag = position
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        llp.setMargins(padding.toInt(), padding.toInt(), padding.toInt(), 0)
        textView.layoutParams = llp
        textView.textSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        textView.setText(text)
        textView.isSingleLine = true
        return textView
    }

    private fun grateEditViewHidden(text: String): EditText {
        val textView = EditTextCustom(this)
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
                if (view is EditText) {
                    when (view.tag as Int) {
                        -1 -> {
                            sb.append(view.text.toString()+ "\n")
                        }
                        1 -> {
                            sb.append("\$calendar[]=array(\"cviaty\"=>\"${view.text}\", \"cytanne\"=>\"\".\$ahref.\"")
                        }
                        2 -> {
                            sb.append("${view.text}</a>\");\n")
                        }
                    }
                }
            }
            val year = data[binding.spinnerYear.selectedItemPosition].toInt()
            sendPostRequest(sb.toString().trim(), year)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendPostRequest(cytanni: String, year: Int) {
        if (MainActivity.isNetworkAvailable()) {
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
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
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

    private class SpinnerAdapter(activity: Activity, private val data: ArrayList<String>) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        private val gc = Calendar.getInstance() as GregorianCalendar

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.text = data[position]
            if (gc[Calendar.YEAR] == data[position].toInt()) textView.typeface = MainActivity.createFont(Typeface.BOLD)
            else textView.typeface = MainActivity.createFont(Typeface.NORMAL)
            textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text.text = data[position]
            if (gc[Calendar.YEAR] == data[position].toInt()) viewHolder.text.typeface = MainActivity.createFont(Typeface.BOLD)
            else viewHolder.text.typeface = MainActivity.createFont(Typeface.NORMAL)
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)
}