package by.carkva_gazeta.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import by.carkva_gazeta.admin.databinding.AdminSviatyiaPageFragmentBinding
import by.carkva_gazeta.malitounik.BaseFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemTipiconBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.util.*


class SvityiaFragment : BaseFragment(), View.OnClickListener {
    private var dayOfYear = 1
    private var _binding: AdminSviatyiaPageFragmentBinding? = null
    private val binding get() = _binding!!
    private var urlJob: Job? = null
    private val sviatyiaNew1 = ArrayList<ArrayList<String>>()
    private val cal = GregorianCalendar()
    private val array: Array<String>
        get() = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.admin_svity)
    private val arrayList = ArrayList<Tipicon>()
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(false)
            dialogImageFileExplorer.show(childFragmentManager, "dialogImageFileExplorer")
        }
    }
    private var mLastClickTime: Long = 0
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference

    override fun onDestroyView() {
        super.onDestroyView()
        urlJob?.cancel()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.apisanne.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.linearLayout2.visibility = View.VISIBLE
                activity?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (!binding.apisanne.showSoftInputOnFocus) {
                            imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                        }
                    }
                }
            } else {
                binding.linearLayout2.visibility = View.GONE
                activity?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (!binding.apisanne.showSoftInputOnFocus) {
                            imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apisanne.onFocusChangeListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(Malitounik.applicationContext())
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
            val dialogSviatyiaImageHelp = DialogSviatyiaImageHelp()
            dialogSviatyiaImageHelp.show(childFragmentManager, "dialogSviatyiaImageHelp")
        }
        if (id == R.id.action_keyword) {
            activity?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (binding.apisanne.showSoftInputOnFocus) {
                        imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                    } else {
                        imm.showSoftInput(binding.apisanne, 0)
                    }
                    binding.apisanne.showSoftInputOnFocus = !binding.apisanne.showSoftInputOnFocus
                }
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

    fun onBackPressedFragment(): Boolean {
        if (binding.scrollpreView.visibility == View.VISIBLE) {
            binding.scrollpreView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
            return false
        }
        return true
    }

    override fun onPrepareMenu(menu: Menu) {
        val editItem = menu.findItem(R.id.action_preview)
        activity?.let {
            if (binding.scrollpreView.visibility == View.GONE) {
                editItem.icon = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
            } else {
                editItem.icon = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.natatka)
            }
        }
    }

    private fun fileUpload(bitmap: Bitmap, image: Int) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("imageSave", "jpeg")
                }
                withContext(Dispatchers.IO) {
                    val out = FileOutputStream(localFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.flush()
                    out.close()
                }
                var fileName = File("/chytanne/icons/s_" + cal[Calendar.DATE] + "_" + (cal[Calendar.MONTH] + 1) + ".jpg")
                if (image > 1) {
                    fileName = File("/chytanne/icons/s_" + cal[Calendar.DATE] + "_" + (cal[Calendar.MONTH] + 1) + "_" + image + ".jpg")
                } else {
                    if (image != 1) {
                        var run = true
                        var i = 1
                        while (run) {
                            if (fileName.exists()) {
                                i++
                                fileName = File("/chytanne/icons/s_" + cal[Calendar.DATE] + "_" + (cal[Calendar.MONTH] + 1) + "_" + i + ".jpg")
                            } else {
                                run = false
                            }
                        }
                    }
                }
                val localFile2 = withContext(Dispatchers.IO) {
                    File.createTempFile("icons", "json")
                }
                referens.child("/chytanne/icons/" + fileName.name).putFile(Uri.fromFile(localFile)).await()
                val arrayListIcon = ArrayList<ArrayList<String>>()
                referens.child("/icons.json").getFile(localFile2).addOnSuccessListener {
                    val gson = Gson()
                    val json = localFile.readText()
                    val type: Type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    arrayListIcon.addAll(gson.fromJson(json, type))
                }.await()
                var chek = false
                arrayListIcon.forEach { result ->
                    if (fileName.name == result[0]) {
                        referens.child("/chytanne/icons/" + fileName.name).metadata.addOnSuccessListener {
                            result[1] = it.sizeBytes.toString()
                            result[2] = it.updatedTimeMillis.toString()
                            chek = true
                        }.await()
                    }
                }
                if (!chek) {
                    referens.child("/chytanne/icons/" + fileName.name).metadata.addOnSuccessListener {
                        val result = ArrayList<String>()
                        result.add(it.name ?: "")
                        result.add(it.sizeBytes.toString())
                        result.add(it.updatedTimeMillis.toString())
                        arrayListIcon.add(result)
                    }.await()
                }
                localFile2.writer().use {
                    val gson = Gson()
                    it.write(gson.toJson(arrayListIcon))
                }
                referens.child("/icons.json").putFile(Uri.fromFile(localFile2)).addOnCompleteListener { task ->
                    activity?.let {
                        if (task.isSuccessful) {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.save))
                        } else {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    fun onDialogFile(absolutePath: String, image: Int) {
        val bitmap = BitmapFactory.decodeFile(absolutePath)
        fileUpload(bitmap, image)
        val dialogImageFileExplorer = childFragmentManager.findFragmentByTag("dialogImageFileExplorer") as? DialogImageFileExplorer
        dialogImageFileExplorer?.dialog?.cancel()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_upload_image) {
            activity?.let { activity ->
                val permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                    mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(false)
                    dialogImageFileExplorer.show(childFragmentManager, "dialogImageFileExplorer")
                }
            }
            return true
        }
        if (id == R.id.action_save) {
            sendPostRequest(cal[Calendar.DAY_OF_MONTH], cal[Calendar.MONTH], dayOfYear - 1, binding.sviaty.text.toString(), binding.chytanne.text.toString(), binding.spinnerStyle.selectedItemPosition, binding.spinnerZnak.selectedItemPosition.toString(), binding.apisanne.text.toString())
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.visibility == View.VISIBLE) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                activity?.invalidateOptionsMenu()
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
            return true
        }
        return false
    }

    private fun sendPostRequest(data: Int, mun: Int, dayOfYear: Int, name: String, chtenie: String, bold: Int, tipicon: String, spaw: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var style = 8
                when (bold) {
                    0 -> style = 6
                    1 -> style = 7
                    2 -> style = 8
                }
                binding.progressBar2.visibility = View.VISIBLE
                val localFile2 = withContext(Dispatchers.IO) {
                    File.createTempFile("calendarsviatyiaEdit", "json")
                }
                val sviatyiaNewList = ArrayList<ArrayList<String>>()
                referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnSuccessListener {
                    val sviatyiaNew = localFile2.readLines()
                    for (element in sviatyiaNew) {
                        val re1 = element.split("<>")
                        val list = ArrayList<String>()
                        for (element2 in re1) {
                            list.add(element2)
                        }
                        sviatyiaNewList.add(list)
                    }
                    sviatyiaNewList[dayOfYear][0] = name
                    sviatyiaNewList[dayOfYear][1] = chtenie
                    sviatyiaNewList[dayOfYear][2] = style.toString()
                    sviatyiaNewList[dayOfYear][3] = tipicon
                }.await()
                var sw3 = ""
                val sb = StringBuilder()
                for (i in 0 until 366) {
                    if (sviatyiaNewList[i][3] != "0") sw3 = sviatyiaNewList[i][3]
                    sb.append(sviatyiaNewList[i][0] + "<>" + sviatyiaNewList[i][1] + "<>" + sviatyiaNewList[i][2] + "<>" + sw3 + "\n")
                }
                val localFile3 = withContext(Dispatchers.IO) {
                    File.createTempFile("calendarsviatyiaSave", "txt")
                }
                localFile3.writer().use {
                    it.write(sb.toString())
                }
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("opisanieEdit", "json")
                }
                val localFile4 = withContext(Dispatchers.IO) {
                    File.createTempFile("opisanieSave", "json")
                }
                var builder = ""
                referens.child("/chytanne/sviatyja/opisanie" + (mun + 1) + ".json").getFile(localFile).addOnSuccessListener {
                    builder = localFile.readText()
                }.await()
                val gson = Gson()
                if (builder != "") {
                    val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
                    val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                    arrayList[data - 1] = spaw
                    localFile4.writer().use {
                        it.write(gson.toJson(arrayList))
                    }
                }
                val logFile = withContext(Dispatchers.IO) {
                    File.createTempFile("piasochnica", "json")
                }
                val stringBuilder = StringBuilder()
                var url = "/calendarsviatyia.txt"
                referens.child("/admin/log.txt").getFile(logFile).await()
                var ref = true
                logFile.readLines().forEach {
                    stringBuilder.append("$it\n")
                    if (it.contains(url)) {
                        ref = false
                    }
                }
                if (ref) {
                    stringBuilder.append("$url\n")
                }
                logFile.writer().use {
                    it.write(stringBuilder.toString())
                }
                referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()
                sb.clear()
                url = "/chytanne/sviatyja/opisanie" + (mun + 1) + ".json"
                referens.child("/admin/log.txt").getFile(logFile).await()
                ref = true
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
                referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()

                referens.child("/calendarsviatyia.txt").putFile(Uri.fromFile(localFile3)).await()
                referens.child("/chytanne/sviatyja/opisanie" + (mun + 1) + ".json").putFile(Uri.fromFile(localFile4)).addOnCompleteListener { task ->
                    activity?.let {
                        if (task.isSuccessful) {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.save))
                        } else {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.actionKeyword.visibility = View.GONE
        } else {
            binding.apisanne.showSoftInputOnFocus = false
        }
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        arrayList.add(Tipicon(0, "Няма"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest, "З вялікай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_kruge, "Двунадзясятыя і вялікія сьвяты"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_polukruge, "З ліцьцёй на вячэрні"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk, "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk_black, "З штодзённай вячэрняй і малым услаўленьнем на ютрані"))
        if (MainActivity.isNetworkAvailable()) {
            binding.actionBold.setOnClickListener(this)
            binding.actionEm.setOnClickListener(this)
            binding.actionRed.setOnClickListener(this)
            binding.actionP.setOnClickListener(this)
            binding.actionImg.setOnClickListener(this)
            binding.actionKeyword.setOnClickListener(this)
            binding.progressBar2.visibility = View.VISIBLE
            urlJob = CoroutineScope(Dispatchers.Main).launch {
                var res = ""
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("opisanieEdit", "json")
                    }
                    var builder = ""
                    referens.child("/chytanne/sviatyja/opisanie" + (cal[Calendar.MONTH] + 1) + ".json").getFile(localFile).addOnSuccessListener {
                        builder = localFile.readText()
                    }.await()
                    val gson = Gson()
                    if (builder != "") {
                        val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
                        val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                        res = arrayList[cal[Calendar.DAY_OF_MONTH] - 1]
                    }
                    val localFile2 = withContext(Dispatchers.IO) {
                        File.createTempFile("calendarsviatyiaEdit", "json")
                    }
                    var builder2 = ""
                    referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnSuccessListener {
                        builder2 = localFile2.readText()
                    }.await()
                    if (builder2 != "") {
                        val line = builder2.split("\n")
                        for (element in line) {
                            val reg = element.split("<>")
                            val list = ArrayList<String>()
                            for (element2 in reg) {
                                list.add(element2)
                            }
                            sviatyiaNew1.add(list)
                        }
                        binding.sviaty.setSelection(0)
                        binding.chytanne.setSelection(0)
                        activity?.let {
                            binding.spinnerStyle.adapter = SpinnerAdapter(it, array)
                            binding.spinnerZnak.adapter = SpinnerAdapterTipicon(it, arrayList)
                        }
                        binding.sviaty.setText(sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][0])
                        binding.chytanne.setText(sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][1])
                        var position = 0
                        when (sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][2].toInt()) {
                            6 -> position = 0
                            7 -> position = 1
                            8 -> position = 2
                        }
                        binding.spinnerStyle.setSelection(position)
                        val znaki = sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][3]
                        val position2 = if (znaki == "") 0
                        else znaki.toInt()
                        binding.spinnerZnak.setSelection(position2)
                    } else {
                        activity?.let {
                            MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
                    }
                } catch (e: Throwable) {
                    activity?.let {
                        MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
                binding.apisanne.setText(res)
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private class SpinnerAdapter(activity: Activity, private val data: Array<String>) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
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

    private class ViewHolder(var text: TextView)

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

    private class ViewHolderImage(var image: ImageView, var text: TextView)

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