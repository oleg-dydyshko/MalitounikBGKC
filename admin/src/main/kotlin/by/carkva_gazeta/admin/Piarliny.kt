package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Intent
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import by.carkva_gazeta.admin.databinding.AdminPiarlinyBinding
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*

class Piarliny : BaseActivity(), View.OnClickListener, DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminPiarlinyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val piarliny = ArrayList<PiarlinyData>()
    private var edit = -1
    private var timeListCalendar = Calendar.getInstance()
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

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        urlJob?.cancel()
    }

    override fun onBack() {
        if (binding.addPiarliny.visibility == View.VISIBLE) {
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
        } else {
            super.onBack()
        }
    }

    override fun onDialogEditClick(position: Int) {
        edit = position
        binding.addPiarliny.setText(piarliny[edit].data)
        binding.addPiarliny.setSelection(piarliny[edit].data.length)
        timeListCalendar.timeInMillis = piarliny[edit].time * 1000
        timeListCalendar.set(Calendar.MILLISECOND, 0)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, timeListCalendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[timeListCalendar.get(Calendar.MONTH)])
        binding.listView.visibility = View.GONE
        binding.addPiarliny.visibility = View.VISIBLE
        binding.linearLayout2.visibility = View.VISIBLE
        invalidateOptionsMenu()
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialogDelite = DialogDelite.getInstance(position, name, false)
        dialogDelite.show(supportFragmentManager, "DialogDelite")
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        piarliny.removeAt(position)
        piarliny.sort()
        val gson = Gson()
        val resultArray = ArrayList<ArrayList<String>>()
        for (i in 0 until piarliny.size) {
            val resultArray2 = ArrayList<String>()
            resultArray2.add(piarliny[i].time.toString())
            resultArray2.add(piarliny[i].data)
            resultArray.add(resultArray2)
        }
        sendPostRequest(gson.toJson(resultArray))
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
            try {
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("piarliny", "json")
                }
                Malitounik.referens.child("/chytanne/piarliny.json").getFile(localFile).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val jsonFile = localFile.readText()
                        val gson = Gson()
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                        val piarlin = ArrayList<ArrayList<String>>()
                        piarlin.addAll(gson.fromJson(jsonFile, type))
                        piarlin.forEach {
                            piarliny.add(PiarlinyData(it[0].toLong(), it[1]))
                        }
                        piarliny.sort()
                        binding.listView.adapter = PiarlinyListAdaprer(this@Piarliny)
                    } else {
                        MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                    binding.progressBar2.visibility = View.GONE
                    if (intent.extras != null) {
                        val time = intent.extras?.getLong("time") ?: Calendar.getInstance().timeInMillis
                        val cal = GregorianCalendar()
                        cal.timeInMillis = time
                        val day = cal[Calendar.DATE]
                        val mun = cal[Calendar.MONTH]
                        val cal2 = GregorianCalendar()
                        for (i in 0 until piarliny.size) {
                            val t = piarliny[i].time * 1000
                            cal2.timeInMillis = t
                            val day2 = cal2[Calendar.DATE]
                            val mun2 = cal2[Calendar.MONTH]
                            if (day == day2 && mun == mun2) {
                                onDialogEditClick(i)
                                break
                            }
                        }
                    }
                }.await()
            } catch (e: Throwable) {
                MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
            }
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            var text = piarliny[position].data
            if (text.length > 30) {
                text = text.substring(0, 30)
                text = "$text..."
            }
            val dialog = DialogPiarlinyContextMenu.getInstance(position, text)
            dialog.show(supportFragmentManager, "DialogPiarlinyContextMenu")
            return@setOnItemLongClickListener true
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            var text = piarliny[position].data
            if (text.length > 30) {
                text = text.substring(0, 30)
                text = "$text..."
            }
            val dialog = DialogPiarlinyContextMenu.getInstance(position, text)
            dialog.show(supportFragmentManager, "DialogPiarlinyContextMenu")
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

    override fun onPrepareMenu(menu: Menu) {
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
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piarliny, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_save) {
            val text = binding.addPiarliny.text.toString().trim()
            if (text != "") {
                if (edit != -1) {
                    piarliny[edit].time = timeListCalendar.timeInMillis / 1000
                    piarliny[edit].data = text
                } else {
                    piarliny.add(PiarlinyData(timeListCalendar.timeInMillis / 1000, text))
                }
                piarliny.sort()
                val gson = Gson()
                val resultArray = ArrayList<ArrayList<String>>()
                for (i in 0 until piarliny.size) {
                    val resultArray2 = ArrayList<String>()
                    resultArray2.add(piarliny[i].time.toString())
                    resultArray2.add(piarliny[i].data)
                    resultArray.add(resultArray2)
                }
                sendPostRequest(gson.toJson(resultArray))
            }
            binding.listView.visibility = View.VISIBLE
            binding.addPiarliny.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny)
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("getData", true)
            caliandarMunLauncher.launch(i)
            return true
        }
        if (id == R.id.action_plus) {
            edit = -1
            binding.listView.visibility = View.GONE
            binding.addPiarliny.visibility = View.VISIBLE
            binding.linearLayout2.visibility = View.VISIBLE
            binding.addPiarliny.setText("")
            timeListCalendar.timeInMillis = Calendar.getInstance().timeInMillis
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.piarliny2, timeListCalendar.get(Calendar.DATE), resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[timeListCalendar.get(Calendar.MONTH)])
            invalidateOptionsMenu()
            return true
        }
        return false
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
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piarliny", "json")
                    }
                    localFile.writer().use {
                        it.write(piarliny)
                    }
                    val logFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val sb = StringBuilder()
                    val url = "/chytanne/piarliny.json"
                    Malitounik.referens.child("/admin/log.txt").getFile(logFile).addOnFailureListener {
                        MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    var ref = true
                    logFile.readLines().forEach {
                        sb.append("$it\n")
                        if (it.contains(url)) {
                            ref = false
                        }
                    }
                    if (ref) {
                        sb.append("$url\n")
                    }
                    logFile.writer().use {
                        it.write(sb.toString())
                    }
                    Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()

                    Malitounik.referens.child("/chytanne/piarliny.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.save))
                            binding.addPiarliny.setText("")
                            edit = -1
                        } else {
                            MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Piarliny, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                val adapter = binding.listView.adapter as PiarlinyListAdaprer
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private inner class PiarlinyListAdaprer(context: Activity) : ArrayAdapter<PiarlinyData>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, piarliny) {

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
            calendar.timeInMillis = piarliny[position].time * 1000
            val munName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[calendar.get(Calendar.MONTH)]
            viewHolder.text.text = MainActivity.fromHtml(calendar.get(Calendar.DATE).toString() + " " + munName)
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class PiarlinyData(var time: Long, var data: String) : Comparable<PiarlinyData> {
        override fun compareTo(other: PiarlinyData): Int {
            if (this.time > other.time) {
                return 1
            } else if (this.time < other.time) {
                return -1
            }
            return 0
        }
    }

    companion object {
        private const val VYSOCOSNYI_GOD = 2020
    }
}