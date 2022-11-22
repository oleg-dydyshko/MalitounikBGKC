package by.carkva_gazeta.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.SensorEvent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Base64
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import by.carkva_gazeta.admin.databinding.AdminSviatyBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class Sviaty : BaseActivity(), View.OnClickListener, DialogImageFileLoad.DialogFileExplorerListener {
    private lateinit var binding: AdminSviatyBinding
    private var urlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val sviaty = ArrayList<SviatyData>()
    private var timerCount = 0
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var edittext: AppCompatEditText? = null
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            val fileExplorer = DialogImageFileExplorer.getInstance(true)
            fileExplorer.show(supportFragmentManager, "file_explorer")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    private fun startTimer() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (urlJob?.isActive == true && timerCount == 6) {
                    urlJob?.cancel()
                    stopTimer()
                    CoroutineScope(Dispatchers.Main).launch {
                        MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.bad_internet), Toast.LENGTH_LONG)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminSviatyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sviaty.add(SviatyData(-1, 0, "Уваход у Ерусалім"))
        sviaty.add(SviatyData(-1, 1, "Уваскрасеньне"))
        sviaty.add(SviatyData(-1, 2, "Узьнясеньне"))
        sviaty.add(SviatyData(-1, 3, "Тройца"))
        sviaty.add(SviatyData(1, 1, "1 Студзеня"))
        sviaty.add(SviatyData(6, 1, "6 Студзеня"))
        sviaty.add(SviatyData(2, 2, "2 Лютага"))
        sviaty.add(SviatyData(25, 3, "25 Сакавіка"))
        sviaty.add(SviatyData(24, 6, "24 Чэрвеня"))
        sviaty.add(SviatyData(29, 6, "29 Чэрвеня"))
        sviaty.add(SviatyData(6, 8, "6 Жніўня"))
        sviaty.add(SviatyData(15, 8, "15 Жніўня"))
        sviaty.add(SviatyData(29, 8, "29 Жніўня"))
        sviaty.add(SviatyData(8, 9, "8 Верасьня"))
        sviaty.add(SviatyData(14, 9, "14 Верасьня"))
        sviaty.add(SviatyData(1, 10, "1 Кастрычніка"))
        sviaty.add(SviatyData(21, 11, "21 Лістапада"))
        sviaty.add(SviatyData(25, 12, "25 Сьнежня"))
        sviaty.add(SviatyData(-1, 4, "Айцоў першых 6-ці Ўсяленскіх сабораў"))
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.sviaty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) edittext = v as? AppCompatEditText
        }
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                binding.progressBar2.visibility = View.VISIBLE
                startTimer()
                val arrayList: ArrayList<ArrayList<String>> = withContext(Dispatchers.IO) {
                    try {
                        val url = "https://carkva-gazeta.by/opisanie_sviat.json"
                        val builder = URL(url).readText()
                        val gson = Gson()
                        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                        return@withContext gson.fromJson(builder, type)
                    } catch (e: Throwable) {
                        withContext(Dispatchers.Main) {
                            MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                        return@withContext ArrayList<ArrayList<String>>()
                    }
                }
                for (i in 0 until arrayList.size) {
                    for (e in 0 until sviaty.size) {
                        if (arrayList[i][0].toInt() == sviaty[e].data && arrayList[i][1].toInt() == sviaty[e].mun) {
                            sviaty[e].opisanie = arrayList[i][2]
                            sviaty[e].utran = arrayList[i][3]
                            sviaty[e].liturgia = arrayList[i][4]
                            sviaty[e].viachernia = arrayList[i][5]
                            break
                        }
                    }
                }
                binding.spinnerSviaty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        binding.sviaty.setText(sviaty[position].opisanie)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
                binding.spinnerSviaty.adapter = SpinnerAdapter(this@Sviaty, sviaty)
                if (intent.extras != null) {
                    for (i in 0 until sviaty.size) {
                        if (intent.extras?.getInt("day") == sviaty[i].data && intent.extras?.getInt("mun") == sviaty[i].mun) {
                            binding.spinnerSviaty.setSelection(i)
                            break
                        }
                    }
                } else {
                    binding.spinnerSviaty.setSelection(0)
                }
                binding.progressBar2.visibility = View.GONE
                stopTimer()
            }
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
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.sviaty)
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

    private fun fileUpload(bitmap: Bitmap) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val bao = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                val ba = bao.toByteArray()
                val base64 = Base64.encodeToString(ba, Base64.DEFAULT)
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    runCatching {
                        try {
                            var reqParam = URLEncoder.encode("imageSV", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                            reqParam += "&" + URLEncoder.encode("base64", "UTF-8") + "=" + URLEncoder.encode(base64, "UTF-8")
                            reqParam += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(sviaty[binding.spinnerSviaty.selectedItemPosition].data.toString(), "UTF-8")
                            reqParam += "&" + URLEncoder.encode("mun", "UTF-8") + "=" + URLEncoder.encode(sviaty[binding.spinnerSviaty.selectedItemPosition].mun.toString(), "UTF-8")
                            val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                            with(mURL.openConnection() as HttpURLConnection) {
                                requestMethod = "POST"
                                val wr = OutputStreamWriter(outputStream)
                                wr.write(reqParam)
                                wr.flush()
                                responseCodeS = responseCode
                            }
                        } catch (e: Throwable) {
                            withContext(Dispatchers.Main) {
                                MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                            }
                        }
                    }
                }
                if (responseCodeS == 200) {
                    MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onDialogFile(absolutePath: String, image: Int) {
        val bitmap = BitmapFactory.decodeFile(absolutePath)
        fileUpload(bitmap)
        val dialogImageFileExplorer = supportFragmentManager.findFragmentByTag("dialogImageFileExplorer") as? DialogImageFileExplorer
        dialogImageFileExplorer?.dialog?.cancel()
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        edittext?.let {
            if (id == R.id.action_bold) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<strong>")
                    append(text.substring(startSelect, endSelect))
                    append("</strong>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 17)
            }
            if (id == R.id.action_em) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<em>")
                    append(text.substring(startSelect, endSelect))
                    append("</em>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 9)
            }
            if (id == R.id.action_red) {
                val startSelect = it.selectionStart
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, startSelect))
                    append("<font color=\"#d00505\">")
                    append(text.substring(startSelect, endSelect))
                    append("</font>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 29)
            }
            if (id == R.id.action_p) {
                val endSelect = it.selectionEnd
                val text = it.text.toString()
                val build = with(StringBuilder()) {
                    append(text.substring(0, endSelect))
                    append("<p>")
                    append(text.substring(endSelect))
                    toString()
                }
                it.setText(build)
                it.setSelection(endSelect + 3)
            }
        }
    }

    override fun onBack() {
        if (binding.scrollpreView.visibility == View.VISIBLE) {
            binding.scrollpreView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        } else {
            super.onBack()
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        val editItem = menu.findItem(R.id.action_preview)
        if (binding.scrollpreView.visibility == View.GONE) {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
        } else {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_upload_image) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(true)
                dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
            }
            return true
        }
        if (id == R.id.action_save) {
            sendPostRequest(binding.spinnerSviaty.selectedItemPosition, binding.sviaty.text.toString())
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.visibility == View.VISIBLE) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            } else {
                binding.preView.text = MainActivity.fromHtml(binding.sviaty.text.toString()).trim()
                binding.scrollpreView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.sviaty.windowToken, 0)
                invalidateOptionsMenu()
            }
            return true
        }
        return false
    }

    private fun sendPostRequest(position: Int, apisanne: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                var responseCodeS = 500
                withContext(Dispatchers.IO) {
                    runCatching {
                        try {
                            var reqParam = URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode("6", "UTF-8")
                            reqParam += "&" + URLEncoder.encode("setsvita", "UTF-8") + "=" + URLEncoder.encode(position.toString(), "UTF-8") //День месяца
                            reqParam += "&" + URLEncoder.encode("spaw", "UTF-8") + "=" + URLEncoder.encode(apisanne, "UTF-8")
                            reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                            val mURL = URL("https://carkva-gazeta.by/admin/android.php")
                            with(mURL.openConnection() as HttpURLConnection) {
                                requestMethod = "POST"
                                val wr = OutputStreamWriter(outputStream)
                                wr.write(reqParam)
                                wr.flush()
                                responseCodeS = responseCode
                            }
                        } catch (e: Throwable) {
                            withContext(Dispatchers.Main) {
                                MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                            }
                        }
                    }
                }
                if (responseCodeS == 200) {
                    MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(this@Sviaty, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviaty, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    private class SpinnerAdapter(activity: Activity, private val data: ArrayList<SviatyData>) : ArrayAdapter<SviatyData>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.text = data[position].title
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
            viewHolder.text.text = data[position].title
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class SviatyData(val data: Int, val mun: Int, val title: String, var opisanie: String = "", var utran: String = "", var liturgia: String = "", var viachernia: String = "")
}