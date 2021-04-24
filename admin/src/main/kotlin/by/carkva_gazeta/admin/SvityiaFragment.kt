package by.carkva_gazeta.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import by.carkva_gazeta.admin.databinding.AdminSviatyiaPageFragmentBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemTipiconBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


class SvityiaFragment : BackPressedFragment(), View.OnClickListener {
    private var dayOfYear = 1
    private var _binding: AdminSviatyiaPageFragmentBinding? = null
    private val binding get() = _binding!!
    private var urlJob: Job? = null
    private val sviatyiaNew1 = ArrayList<ArrayList<String>>()
    private val cal = GregorianCalendar()
    private val array = arrayOf("Чырвоны", "Чырвоны тоўсты", "Нармальны")
    private val arrayList = ArrayList<Tipicon>()
    private var timerCount = 0
    private val timer = Timer()
    private var timerTask: TimerTask? = null
    private val myPermissionsWriteExternalStorage = 41

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == myPermissionsWriteExternalStorage) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fragmentManager?.let {
                    val fileExplorer = DialogImageFileExplorer()
                    fileExplorer.show(it, "file_explorer")
                }
            }
        }
    }

    private fun startTimer() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (urlJob?.isActive == true && timerCount == 6) {
                    urlJob?.cancel()
                    stopTimer()
                    CoroutineScope(Dispatchers.Main).launch {
                        activity?.let {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.bad_internet), Toast.LENGTH_LONG)
                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        urlJob?.cancel()
        binding.apisanne.onFocusChangeListener = null
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dayOfYear = arguments?.getInt("day_of_year", 1) ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AdminSviatyiaPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 29)
        }
        if (id == R.id.action_p) {
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<p>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 3)
        }
        if (id == R.id.action_img) {
            fragmentManager?.let {
                val dialogSviatyiaImageHelp = DialogSviatyiaImageHelp()
                dialogSviatyiaImageHelp.show(it, "dialogSviatyiaImageHelp")
            }
        }
    }

    fun insertIMG() {
        val endSelect = binding.apisanne.selectionEnd
        val text = binding.apisanne.text.toString()
        val build = with(StringBuilder()) {
            append(text.substring(0, endSelect))
            append("<!--image-->")
            append(text.substring(endSelect))
            toString()
        }
        binding.apisanne.setText(build)
        binding.apisanne.setSelection(endSelect + 12)
    }

    override fun onBackPressedFragment(): Boolean {
        if (binding.scrollpreView.visibility == View.VISIBLE) {
            binding.scrollpreView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
            activity?.let {
                val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            return false
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val editItem = menu.findItem(R.id.action_preview)
        activity?.let {
            if (binding.scrollpreView.visibility == View.GONE) {
                editItem.icon = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
            } else {
                editItem.icon = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.natatka)
            }
        }
    }

    private fun fileUpload(bitmap: Bitmap) {
        activity?.let { actyvity ->
            if (MainActivity.isNetworkAvailable(actyvity)) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.progressBar2.visibility = View.VISIBLE
                    val bao = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                    val ba = bao.toByteArray()
                    val base64 = Base64.encodeToString(ba, Base64.DEFAULT)
                    var responseCodeS = 500
                    withContext(Dispatchers.IO) {
                        var reqParam = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        reqParam += "&" + URLEncoder.encode("base64", "UTF-8") + "=" + URLEncoder.encode(base64, "UTF-8")
                        reqParam += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(cal[Calendar.DATE].toString(), "UTF-8")
                        reqParam += "&" + URLEncoder.encode("mun", "UTF-8") + "=" + URLEncoder.encode((cal[Calendar.MONTH] + 1).toString(), "UTF-8")
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
                        MainActivity.toastView(actyvity, getString(by.carkva_gazeta.malitounik.R.string.save))
                    } else {
                        MainActivity.toastView(actyvity, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                    binding.progressBar2.visibility = View.GONE
                }
            }
        }
    }

    fun onDialogFile(file: File) {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        fileUpload(bitmap)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_upload_image) {
            activity?.let { activity ->
                val permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), myPermissionsWriteExternalStorage)
                } else {
                    fragmentManager?.let {
                        val dialogImageFileExplorer = DialogImageFileExplorer()
                        dialogImageFileExplorer.show(it, "dialogImageFileExplorer")
                    }
                }
            }
        }
        if (id == R.id.action_save) {
            sendPostRequest(cal[Calendar.DAY_OF_MONTH], cal[Calendar.MONTH], dayOfYear - 1, binding.sviaty.text.toString(), binding.chytanne.text.toString(), binding.spinnerStyle.selectedItemPosition, binding.spinnerZnak.selectedItemPosition.toString(), binding.apisanne.text.toString())
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.visibility == View.VISIBLE) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                activity?.let {
                    val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                    it.invalidateOptionsMenu()
                }
            } else {
                var textApisanne = binding.apisanne.text.toString()
                if (textApisanne.contains("<!--image-->")) {
                    textApisanne = textApisanne.replace("<!--image-->", "<p>")
                }
                binding.preView.text = MainActivity.fromHtml(textApisanne).trim()
                binding.scrollpreView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                activity?.let {
                    val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                    it.invalidateOptionsMenu()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendPostRequest(data: Int, mun: Int, dayOfYear: Int, name: String, chtenie: String, bold: Int, tipicon: String, spaw: String) {
        activity?.let { actyvity ->
            if (MainActivity.isNetworkAvailable(actyvity)) {
                CoroutineScope(Dispatchers.Main).launch {
                    var style = 8
                    when (bold) {
                        0 -> style = 6
                        1 -> style = 7
                        2 -> style = 8
                    }
                    binding.progressBar2.visibility = View.VISIBLE
                    var responseCodeS = 500
                    withContext(Dispatchers.IO) {
                        var reqParam = URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8")
                        reqParam += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(data.toString(), "UTF-8") //День месяца
                        reqParam += "&" + URLEncoder.encode("mun", "UTF-8") + "=" + URLEncoder.encode(mun.toString(), "UTF-8")
                        reqParam += "&" + URLEncoder.encode("nomerdny", "UTF-8") + "=" + URLEncoder.encode(dayOfYear.toString(), "UTF-8")
                        reqParam += "&" + URLEncoder.encode("addksave", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                        reqParam += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                        reqParam += "&" + URLEncoder.encode("chtenie", "UTF-8") + "=" + URLEncoder.encode(chtenie, "UTF-8")
                        reqParam += "&" + URLEncoder.encode("bold", "UTF-8") + "=" + URLEncoder.encode(style.toString(), "UTF-8")
                        reqParam += "&" + URLEncoder.encode("tipicon", "UTF-8") + "=" + URLEncoder.encode(tipicon, "UTF-8")
                        reqParam += "&" + URLEncoder.encode("spaw", "UTF-8") + "=" + URLEncoder.encode(spaw, "UTF-8")
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
                        MainActivity.toastView(actyvity, getString(by.carkva_gazeta.malitounik.R.string.save))
                    } else {
                        MainActivity.toastView(actyvity, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                    binding.progressBar2.visibility = View.GONE
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        arrayList.add(Tipicon(0, "Няма"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest, "З вялікай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_kruge, "Двунадзясятыя і вялікія сьвяты"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_polukruge, "З ліцьцёй на вячэрні"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk, "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk_black, "З штодзённай вячэрняй і малым услаўленьнем на ютрані"))
        activity?.let { actyvity ->
            if (MainActivity.isNetworkAvailable(actyvity)) {
                binding.actionBold.setOnClickListener(this)
                binding.actionEm.setOnClickListener(this)
                binding.actionRed.setOnClickListener(this)
                binding.actionP.setOnClickListener(this)
                binding.actionImg.setOnClickListener(this)
                binding.progressBar2.visibility = View.VISIBLE
                binding.apisanne.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) binding.linearLayout2.visibility = View.VISIBLE
                    else binding.linearLayout2.visibility = View.GONE
                }
                urlJob = CoroutineScope(Dispatchers.Main).launch {
                    startTimer()
                    var res = ""
                    withContext(Dispatchers.IO) {
                        var url = "https://carkva-gazeta.by/chytanne/sviatyja/opisanie" + (cal[Calendar.MONTH] + 1) + ".json"
                        val builder = URL(url).readText()
                        val gson = Gson()
                        val type = object : TypeToken<ArrayList<String>>() {}.type
                        val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                        res = arrayList[cal[Calendar.DAY_OF_MONTH] - 1]
                        url = "https://carkva-gazeta.by/calendarsviatyia.txt"
                        val textfile = URL(url).readText().trim()
                        val line = textfile.split("\n")
                        for (element in line) {
                            val reg = element.split("<>")
                            val list = ArrayList<String>()
                            for (element2 in reg) {
                                list.add(element2)
                            }
                            sviatyiaNew1.add(list)
                        }
                    }
                    binding.sviaty.setText(sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][0])
                    binding.chytanne.setText(sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][1])
                    activity?.let {
                        binding.spinnerStyle.adapter = SpinnerAdapter(it, array)
                        var position = 0
                        when (sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][2].toInt()) {
                            6 -> position = 0
                            7 -> position = 1
                            8 -> position = 2
                        }
                        binding.spinnerStyle.setSelection(position)
                        binding.spinnerZnak.adapter = SpinnerAdapterTipicon(it, arrayList)
                        val znaki = sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][3]
                        val position2 = if (znaki == "") 0
                        else znaki.toInt()
                        binding.spinnerZnak.setSelection(position2)
                    }
                    binding.apisanne.setText(res)
                    binding.progressBar2.visibility = View.GONE
                    binding.sviaty.setSelection(binding.sviaty.text.toString().length)
                    activity?.let {
                        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                    }
                    stopTimer()
                }
            }
        }
    }

    private class SpinnerAdapter(activity: Activity, private val data: Array<String>) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextViewRobotoCondensed
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.text = data[position]
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
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextViewRobotoCondensed)

    private class SpinnerAdapterTipicon(activity: Activity, private val data: ArrayList<Tipicon>) : BaseAdapter() {
        private val context = activity

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolderImage: ViewHolderImage
            if (convertView == null) {
                val binding = SimpleListItemTipiconBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolderImage = ViewHolderImage(binding.image, binding.text1)
                rootView.tag = viewHolderImage
            } else {
                rootView = convertView
                viewHolderImage = rootView.tag as ViewHolderImage
            }
            if (data[position].imageResource == 0) {
                viewHolderImage.image.visibility = View.GONE
            } else {
                viewHolderImage.image.setImageResource(data[position].imageResource)
            }
            viewHolderImage.text.text = data[position].title
            viewHolderImage.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolderImage.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolderImage(var image: ImageView, var text: TextViewRobotoCondensed)

    private data class Tipicon(val imageResource: Int, val title: String)

    companion object {
        fun newInstance(day_of_year: Int): SvityiaFragment {
            val fragmentFirst = SvityiaFragment()
            val args = Bundle()
            args.putInt("day_of_year", day_of_year)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}