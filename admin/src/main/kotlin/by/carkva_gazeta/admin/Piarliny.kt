package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.admin.databinding.AdminPiarlinyBinding
import by.carkva_gazeta.malitounik.CaliandarMun
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuCaliandar
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class Piarliny : AppCompatActivity(), View.OnClickListener, DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminPiarlinyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val piarliny = ArrayList<ArrayList<String>>()
    private var timerCount = 0
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var edit = -1
    private var timeListCalendar = Calendar.getInstance() as GregorianCalendar
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                timeListCalendar.set(VYSOCOSNYI_GOD, arrayList[2].toInt(), arrayList[1].toInt(), 0, 0, 0)
                timeListCalendar.set(Calendar.MILLISECOND, 0)
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, timeListCalendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[timeListCalendar.get(Calendar.MONTH)])
            }
        }
    }

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

    override fun onBackPressed() {
        if (binding.addPiarliny.visibility == View.VISIBLE) {
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDialogEditClick(position: Int) {
        edit = position
        binding.addPiarliny.setText(piarliny[edit][1])
        binding.addPiarliny.setSelection(piarliny[edit][1].length)
        val calendar = GregorianCalendar()
        calendar.timeInMillis = piarliny[edit][0].toLong() * 1000
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, calendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[calendar.get(Calendar.MONTH)])
        binding.listView.visibility = View.GONE
        binding.addPiarliny.visibility = View.VISIBLE
        binding.linearLayout2.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialogDelite = DialogDelite.getInstance(position, name)
        dialogDelite.show(supportFragmentManager, "DialogDelite")
    }

    override fun fileDelite(position: Int) {
        piarliny.removeAt(position)
        val gson = Gson()
        sendPostRequest(gson.toJson(piarliny))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminPiarlinyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)

        urlJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            startTimer()
            withContext(Dispatchers.IO) {
                var responseCodeS: Int
                val mURL = URL("https://carkva-gazeta.by/chytanne/piarliny.json")
                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    responseCodeS = responseCode
                }
                if (responseCodeS == 200) {
                    val builder = mURL.readText()
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                    piarliny.addAll(gson.fromJson(builder, type))
                }
            }
            binding.listView.adapter = PiarlinyListAdaprer(this@Piarliny)
            binding.progressBar2.visibility = View.GONE
            stopTimer()
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            var text = piarliny[position][1]
            if (text.length > 30) {
                text = text.substring(0, 30)
                text = "$text..."
            }
            val dialog = DialogPiarlinyContextMenu.getInstance(position, text)
            dialog.show(supportFragmentManager, "DialogPiarlinyContextMenu")
            return@setOnItemLongClickListener true
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val calendar = GregorianCalendar()
            calendar.timeInMillis = piarliny[position][0].toLong() * 1000
            val day = calendar[Calendar.DATE]
            val mun = calendar[Calendar.MONTH] + 1
            val i = Intent(this, by.carkva_gazeta.malitounik.Piarliny::class.java)
            i.putExtra("mun", mun)
            i.putExtra("day", day)
            startActivity(i)
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
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

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val plus = menu.findItem(R.id.action_plus)
        val save = menu.findItem(R.id.action_save)
        val glava = menu.findItem(R.id.action_glava)
        if (binding.addPiarliny.visibility == View.VISIBLE) {
            plus.isVisible = false
            save.isVisible = true
            glava.isVisible = true
        } else {
            plus.isVisible = true
            save.isVisible = false
            glava.isVisible = false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_piarliny, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_save) {
            val text = binding.addPiarliny.text.toString().trim()
            if (text != "") {
                if (edit != -1) {
                    piarliny[edit][0] = (timeListCalendar.timeInMillis / 1000).toString()
                    piarliny[edit][1] = text
                } else {
                    val arrayList = ArrayList<String>()
                    arrayList.add((timeListCalendar.timeInMillis / 1000).toString())
                    arrayList.add(text)
                    piarliny.add(arrayList)
                }
                val gson = Gson()
                sendPostRequest(gson.toJson(piarliny))
            }
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
        }
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("sabytie", true)
            caliandarMunLauncher.launch(i)
        }
        if (id == R.id.action_plus) {
            edit = -1
            binding.listView.visibility = View.GONE
            binding.addPiarliny.visibility = View.VISIBLE
            binding.linearLayout2.visibility = View.VISIBLE
            binding.addPiarliny.setText("")
            val calendar = Calendar.getInstance() as GregorianCalendar
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, calendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[calendar.get(Calendar.MONTH)])
            invalidateOptionsMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_bold) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.addPiarliny.selectionStart
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 29)
        }
        if (id == R.id.action_p) {
            val endSelect = binding.addPiarliny.selectionEnd
            val text = binding.addPiarliny.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<p>")
                append(text.substring(endSelect))
                toString()
            }
            binding.addPiarliny.setText(build)
            binding.addPiarliny.setSelection(endSelect + 3)
        }
    }

    private fun sendPostRequest(piarliny: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode("5", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("piarliny", "UTF-8") + "=" + URLEncoder.encode(piarliny, "UTF-8")
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
                    binding.addPiarliny.setText("")
                    edit = -1
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                val adapter = binding.listView.adapter as PiarlinyListAdaprer
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private inner class PiarlinyListAdaprer(context: Activity) : ArrayAdapter<ArrayList<String>>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, piarliny) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val calendar = GregorianCalendar()
            calendar.timeInMillis = piarliny[position][0].toLong() * 1000
            val munName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[calendar.get(Calendar.MONTH)]
            viewHolder.text.text = MainActivity.fromHtml(calendar.get(Calendar.DATE).toString() + " " + munName)
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        private const val VYSOCOSNYI_GOD = 2020
    }
}