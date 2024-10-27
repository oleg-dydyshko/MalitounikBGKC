package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import by.carkva_gazeta.malitounik.databinding.BiblijatekaBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemBibliotekaBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MenuBiblijateka : BaseFragment() {

    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = (activity as BaseActivity).getBaseDzenNoch()
    private var filePath = ""
    private var fileName = ""
    private var fileTitle = ""
    private val arrayList = ArrayList<ArrayList<String>>()
    private var width = 0
    private lateinit var adapter: BibliotekaAdapter
    private var idSelect = MainActivity.MALITOUNIKI
    private val naidaunia = ArrayList<ArrayList<String>>()
    private var saveindep = true
    private var sqlJob: Job? = null
    private var setRubrikaJob: Job? = null
    private var bitmapJob: Job? = null
    private lateinit var binding: BiblijatekaBinding
    private var munuBiblijatekaListener: MunuBiblijatekaListener? = null
    private val mBiblijatekaPdfResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            loadNiadaunia()
        }
    }
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            activity?.let { activity ->
                val dir = File("${activity.filesDir}/BookCache")
                if (!dir.exists()) dir.mkdir()
                val fileUri = it.data?.data
                val path = fileUri?.path ?: ""
                val t1 = path.lastIndexOf("/")
                var mime = "Bibliateka.file"
                if (t1 != -1) {
                    mime = path.substring(t1 + 1)
                }
                fileUri?.let { uri ->
                    copyInputStreamToFile(activity.contentResolver.openInputStream(uri), mime)
                }
                onDialogFile(File("${activity.filesDir}/BookCache/$mime"))
            }
        }
    }

    fun saveNaidauniaBiblijateka(title: String) {
        var titleNew = title
        var position = -1
        if (titleNew == "") {
            arrayList.forEachIndexed { index, kniga ->
                if (kniga[2] == fileName) {
                    position = index
                    titleNew = kniga[0]
                    return@forEachIndexed
                }
            }
        }
        if (titleNew == "" && filePath != "") {
            val t1 = filePath.lastIndexOf("/")
            titleNew = filePath.substring(t1 + 1)
        }
        if (filePath != "") {
            activity?.let {
                for (i in 0 until naidaunia.size) {
                    if (naidaunia[i][1].contains(filePath)) {
                        naidaunia.removeAt(i)
                        break
                    }
                }
                val gson = Gson()
                val temp = ArrayList<String>()
                temp.add(titleNew)
                temp.add(filePath)
                val image = if (position != -1) {
                    File(arrayList[position][5]).name
                } else {
                    val t2 = filePath.lastIndexOf("/")
                    val img = filePath.substring(t2 + 1)
                    val t1 = img.lastIndexOf(".")
                    img.substring(0, t1) + ".png"
                }
                val imageTemp = File("${it.filesDir}/image_temp/$image")
                if (imageTemp.exists()) temp.add("${it.filesDir}/image_temp/$image")
                else temp.add("")
                naidaunia.add(temp)
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                val prefEditor = k.edit()
                prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
                prefEditor.apply()
                munuBiblijatekaListener?.munuBiblijatekaUpdate(naidaunia.size > 0)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            munuBiblijatekaListener = try {
                context as MunuBiblijatekaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement MunuBiblijatekaListener")
            }
        }
    }

    interface MunuBiblijatekaListener {
        fun munuBiblijatekaUpdate(isNiadaunia: Boolean)
        fun menuMainloadNiadaunia()
    }

    private fun copyInputStreamToFile(inputStream: InputStream?, mime: String) {
        activity?.let {
            val outputStream = FileOutputStream(File("${it.filesDir}/BookCache/$mime"))
            val buffer = ByteArray(8192)
            inputStream?.use { input ->
                outputStream.use { fileOut ->
                    while (true) {
                        val length = input.read(buffer)
                        if (length <= 0) break
                        fileOut.write(buffer, 0, length)
                    }
                    fileOut.flush()
                    fileOut.close()
                }
            }
            inputStream?.close()
        }
    }

    fun deliteNiadaunia(position: Int, file: String) {
        deliteCashe(position, file)
    }

    fun fileDelite(position: Int, file: String) {
        activity?.let {
            val file1 = File(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), file)
            if (file1.exists()) {
                file1.delete()
            }
            deliteCashe(position, file)
        }
    }

    fun delAllNiadaunia() {
        naidaunia.clear()
        arrayList.clear()
        adapter.notifyDataSetChanged()
        val gson = Gson()
        val prefEditor = k.edit()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
        prefEditor.apply()
        munuBiblijatekaListener?.munuBiblijatekaUpdate(false)
        binding.progressBar2.visibility = View.VISIBLE
        activity?.let {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val dir = File("${it.filesDir}/Book")
                    if (dir.exists()) {
                        dir.deleteRecursively()
                    }
                    val dir2 = File("${it.filesDir}/BookCache")
                    if (dir2.exists()) {
                        dir2.deleteRecursively()
                    }
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun deliteCashe(position: Int, file: String) {
        activity?.let {
            val t1 = file.lastIndexOf(".")
            val dirName = if (t1 == -1) file
            else file.substring(0, t1)
            var t2 = dirName.lastIndexOf("/")
            var patch = "${it.filesDir}/Book/" + dirName.substring(t2 + 1)
            var file1 = File(patch)
            if (file1.exists() && file1.isDirectory) {
                file1.deleteRecursively()
            } else {
                t2 = file.lastIndexOf("/")
                patch = "${it.filesDir}/Book/" + file.substring(t2 + 1)
                file1 = File(patch)
                if (file1.exists()) file1.delete()
            }
            var position1 = -1
            naidaunia.forEachIndexed { index, arrayList1 ->
                if (arrayList1[1] == arrayList[position][1]) {
                    position1 = index
                }
            }
            if (position1 != -1) {
                if (idSelect == MainActivity.NIADAUNIA) {
                    arrayList.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
                val fileChech = File(naidaunia[position1][1])
                if (fileChech.exists() && !naidaunia[position1][1].contains("PiasochnicaBackCopy")) fileChech.delete()
                naidaunia.removeAt(position1)
                val gson = Gson()
                val prefEditor = k.edit()
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
                prefEditor.apply()
                munuBiblijatekaListener?.munuBiblijatekaUpdate(naidaunia.size > 0)
            }
        }
    }

    fun onDialogPositiveClick(listPosition: String, isShare: Boolean, isPrint: Boolean) {
        if (!MainActivity.isNetworkAvailable()) {
            val dialogNoInternet = DialogNoInternet()
            dialogNoInternet.show(childFragmentManager, "no_internet")
        } else {
            activity?.let {
                writeFile(listPosition, isShare, isPrint)
            }
        }
    }

    fun onDialogbibliatekaPositiveClick(listPosition: String, title: String) {
        (activity as? BaseActivity)?.let {
            if (it.checkmoduleResources()) {
                val file = File(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), listPosition)
                if (file.exists()) {
                    filePath = file.path
                    fileName = title
                    loadComplete(false, isPrint = false)
                } else {
                    if (MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_CELLULAR)) {
                        val bibliotekaWiFi = DialogBibliotekaWIFI.getInstance(listPosition, false, isPrint = false)
                        bibliotekaWiFi.show(childFragmentManager, "biblioteka_WI_FI")
                    } else {
                        writeFile(listPosition, false, isPrint = false)
                    }
                }
            } else {
                it.installFullMalitounik()
            }
        }
    }

    private fun writeFile(url: String, isShare: Boolean, isPrint: Boolean) {
        binding.progressBar2.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            var error = false
            try {
                activity?.let {
                    val dir = it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    if (dir?.exists() != true) {
                        dir?.mkdir()
                    }
                    for (i in 0..2) {
                        error = downloadPdfFile(url)
                        if (!error) break
                    }
                }
            } catch (t: Throwable) {
                error = true
            }
            if (!error) {
                adapter.notifyDataSetChanged()
                loadComplete(isShare, isPrint)
            } else {
                DialogNoInternet().show(childFragmentManager, "no_internet")
            }
            binding.progressBar2.visibility = View.GONE
        }
    }

    private suspend fun downloadPdfFile(url: String): Boolean {
        var error = false
        activity?.let { activity ->
            val pathReference = Malitounik.referens.child("/data/bibliateka/$url")
            val localFile = File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), url)
            pathReference.getFile(localFile).addOnFailureListener {
                error = true
            }.await()
            filePath = localFile.path
            fileName = url
        }
        return error
    }

    private fun onDialogFile(file: File) {
        saveindep = false
        idSelect = MainActivity.NIADAUNIA
        setRubrikaJob = CoroutineScope(Dispatchers.Main).launch {
            if (naidaunia.size > 0) setRubrika(MainActivity.NIADAUNIA)
            else setRubrika(MainActivity.MALITOUNIKI)
        }
        filePath = file.absolutePath
        fileName = file.name
        loadComplete(false, isPrint = false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BiblijatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = it.windowManager.currentWindowMetrics
                val bounds = display.bounds
                bounds.width()
            } else {
                @Suppress("DEPRECATION") val display = it.windowManager.defaultDisplay
                val size = Point()
                @Suppress("DEPRECATION") display.getSize(size)
                size.x
            }
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)

            adapter = BibliotekaAdapter(it)
            binding.listView.adapter = adapter
            if (dzenNoch) {
                binding.listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
                binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary_black)
            } else {
                binding.listView.background = ContextCompat.getDrawable(it, R.color.colorDivider)
                binding.listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_default_bibliateka)
                binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
            }
            binding.swipeRefreshLayout.setOnRefreshListener {
                if (setRubrikaJob?.isActive == true) return@setOnRefreshListener
                if (!MainActivity.isNetworkAvailable()) {
                    val dialogNoInternet = DialogNoInternet()
                    dialogNoInternet.show(childFragmentManager, "no_internet")
                } else {
                    setRubrikaJob = CoroutineScope(Dispatchers.Main).launch {
                        if (setRubrika(idSelect) == NOUPDATE) MainActivity.toastView(it, it.getString(R.string.update_no_biblijateka))
                        if (setRubrika(idSelect) == ERROR) MainActivity.toastView(it, it.getString(R.string.error))
                    }
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
            binding.listView.setOnItemLongClickListener { _, _, position, _ ->
                val dd = DialogDeliteNiadaunia.getInstance(position, arrayList[position][1], arrayList[position][0])
                dd.show(childFragmentManager, "dialog_delite_niadaunia")
                return@setOnItemLongClickListener true
            }
            binding.listView.setOnItemClickListener { _, _, position, _ ->
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnItemClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val file: File
                if (arrayList[position].size == 3) {
                    file = File(arrayList[position][1])
                    if (file.exists()) {
                        filePath = file.absolutePath
                        fileName = file.name
                        fileTitle = arrayList[position][0]
                        loadComplete(false, isPrint = false)
                    } else {
                        arrayList.removeAt(position)
                        naidaunia.clear()
                        naidaunia.addAll(arrayList)
                        adapter.notifyDataSetChanged()
                        val gson = Gson()
                        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                        val prefEditor = k.edit()
                        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
                        prefEditor.apply()
                        munuBiblijatekaListener?.munuBiblijatekaUpdate(naidaunia.size > 0)
                        MainActivity.toastView(it, it.getString(R.string.no_file))
                    }
                } else {
                    file = File(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), arrayList[position][2])
                    if (file.exists()) {
                        filePath = file.absolutePath
                        fileName = file.name
                        fileTitle = arrayList[position][0]
                        loadComplete(false, isPrint = false)
                    } else {
                        var opisanie = arrayList[position][1]
                        val t1 = opisanie.indexOf("</span><br>")
                        if (t1 != -1) opisanie = opisanie.substring(t1 + 11)
                        val dialogBibliateka = DialogBibliateka.getInstance(arrayList[position][2], opisanie, arrayList[position][0], arrayList[position][3])
                        dialogBibliateka.show(childFragmentManager, "dialog_bibliateka")
                    }
                }
            }
            if (savedInstanceState != null) {
                filePath = savedInstanceState.getString("filePath") ?: ""
                fileName = savedInstanceState.getString("fileName") ?: ""
                idSelect = savedInstanceState.getInt("idSelect")
                saveindep = false
            }
            loadNiadaunia(false)
            munuBiblijatekaListener?.munuBiblijatekaUpdate(naidaunia.size > 0)
            idSelect = arguments?.getInt("rub", MainActivity.MALITOUNIKI) ?: MainActivity.MALITOUNIKI
            if (idSelect == MainActivity.NIADAUNIA || idSelect == MainActivity.SETFILE) {
                binding.swipeRefreshLayout.isEnabled = false
                binding.swipeRefreshLayout.isRefreshing = false
            }
            val dir = File("${it.filesDir}/image_temp")
            if (!dir.exists()) dir.mkdir()
            setRubrikaJob = CoroutineScope(Dispatchers.Main).launch {
                when (idSelect) {
                    MainActivity.NIADAUNIA -> setRubrika(MainActivity.NIADAUNIA)
                    MainActivity.GISTORYIACARKVY -> setRubrika(MainActivity.GISTORYIACARKVY)
                    MainActivity.MALITOUNIKI -> setRubrika(MainActivity.MALITOUNIKI)
                    MainActivity.SPEUNIKI -> setRubrika(MainActivity.SPEUNIKI)
                    MainActivity.RELLITARATURA -> setRubrika(MainActivity.RELLITARATURA)
                    MainActivity.PDF -> setRubrika(MainActivity.PDF)
                    MainActivity.SETFILE -> setRubrika(MainActivity.SETFILE)
                }
            }
            if (fileName != "" && filePath != "") {
                loadComplete(false, isPrint = false)
            }
            setTitleBibliateka(idSelect)
        }
    }

    private fun loadNiadaunia(isUpdate: Boolean = true) {
        activity?.let {
            val gson = Gson()
            val json = k.getString("bibliateka_naidaunia", "")
            if (json == "") {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        val dir = File("${it.filesDir}/Book")
                        if (dir.exists()) dir.deleteRecursively()
                    }
                }
            }
            naidaunia.clear()
            if (!json.equals("")) {
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                naidaunia.addAll(gson.fromJson(json, type))
            }
            if (isUpdate) {
                arrayList.clear()
                arrayList.addAll(naidaunia)
                arrayList.reverse()
                munuBiblijatekaListener?.menuMainloadNiadaunia()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadComplete(isShare: Boolean, isPrint: Boolean) {
        (activity as? BaseActivity)?.let {
            when {
                isPrint -> {
                    val file = File(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                    val printAdapter = PdfDocumentAdapter(file.absolutePath)
                    val printManager = it.getSystemService(Context.PRINT_SERVICE) as PrintManager
                    val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                    printManager.print(file.name, printAdapter, printAttributes)
                }

                isShare -> {
                    val file = File(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                    val sendIntent = Intent(Intent.ACTION_SEND)
                    sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(it, "by.carkva_gazeta.malitounik.fileprovider", file))
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, it.getString(R.string.set_log_file))
                    sendIntent.type = "text/html"
                    startActivity(Intent.createChooser(sendIntent, it.getString(R.string.set_log_file)))
                }

                else -> {
                    val intent = Intent(activity, BiblijatekaPdf::class.java)
                    intent.putExtra("filePath", filePath)
                    intent.putExtra("fileTitle", fileTitle)
                    mBiblijatekaPdfResult.launch(intent)
                }
            }
        }
    }

    private fun setTitleBibliateka(rub: Int) {
        activity?.let {
            when (rub) {
                MainActivity.GISTORYIACARKVY -> {
                    idSelect = MainActivity.GISTORYIACARKVY
                }

                MainActivity.MALITOUNIKI -> {
                    idSelect = MainActivity.MALITOUNIKI
                }

                MainActivity.SPEUNIKI -> {
                    idSelect = MainActivity.SPEUNIKI
                }

                MainActivity.RELLITARATURA -> {
                    idSelect = MainActivity.RELLITARATURA
                }

                MainActivity.PDF -> {
                    idSelect = MainActivity.PDF
                }

                else -> {
                    loadNiadaunia()
                }
            }
        }
    }

    suspend fun setRubrika(rub: Int): Int {
        binding.progressBar2.visibility = View.VISIBLE
        var isUbdate = NOUPDATE
        var rubryka = rub
        if (rubryka == MainActivity.SETFILE) {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT/**/
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
            mActivityResultFile.launch(Intent.createChooser(intent, activity?.getString(R.string.vybrac_file)))
            rubryka = if (naidaunia.size > 0) MainActivity.NIADAUNIA
            else MainActivity.MALITOUNIKI
            idSelect = rubryka
        }
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        val jsonB = k.getString("Biblioteka", "") ?: ""
        if (jsonB.isNotEmpty()) {
            arrayList.clear()
            arrayList.addAll(gson.fromJson(jsonB, type))
            val temp = ArrayList<ArrayList<String>>()
            for (i in 0 until arrayList.size) {
                val rtemp2 = arrayList[i][4].toInt()
                if (rtemp2 != rubryka) temp.add(arrayList[i])
            }
            arrayList.removeAll(temp.toSet())
            adapter.notifyDataSetChanged()
            binding.progressBar2.visibility = View.GONE
        }
        if (!k.getBoolean("BibliotekaUpdate", false) && rubryka != MainActivity.NIADAUNIA) {
            var sb = ""
            if (MainActivity.isNetworkAvailable()) {
                for (i in 0..2) {
                    sb = getBibliatekaJson()
                    if (sb.isNotEmpty()) break
                }
            }
            val biblioteka = ArrayList<ArrayList<String>>()
            if (sb.isNotEmpty()) {
                biblioteka.addAll(gson.fromJson(sb, type))
            } else {
                isUbdate = ERROR
            }
            if (biblioteka.size > arrayList.size || arrayList.size == 0) {
                if (MainActivity.isNetworkAvailable()) {
                    getSql()
                }
                val jsonC = k.getString("Biblioteka", "") ?: ""
                if (jsonC.isNotEmpty()) {
                    val arrayTemp = ArrayList<ArrayList<String>>()
                    arrayTemp.addAll(gson.fromJson(jsonC, type))
                    val temp = ArrayList<ArrayList<String>>()
                    for (i in 0 until arrayTemp.size) {
                        val rtemp2 = arrayTemp[i][4].toInt()
                        if (rtemp2 != rubryka) temp.add(arrayTemp[i])
                    }
                    arrayTemp.removeAll(temp.toSet())
                    if (arrayTemp.size != arrayList.size) {
                        arrayList.clear()
                        arrayList.addAll(arrayTemp)
                        adapter.notifyDataSetChanged()
                    }
                }
                isUbdate = if (arrayList.size == 0) ERROR
                else UPDATE
            } else {
                val temp = ArrayList<ArrayList<String>>()
                for (i in 0 until arrayList.size) {
                    val rtemp2 = arrayList[i][4].toInt()
                    if (rtemp2 != rubryka) temp.add(arrayList[i])
                }
                arrayList.removeAll(temp.toSet())
                adapter.notifyDataSetChanged()
            }
        }
        setTitleBibliateka(rubryka)
        saveindep = true
        if (sqlJob?.isActive != true) binding.progressBar2.visibility = View.GONE
        if (isUbdate != ERROR) {
            val prefEditors = k.edit()
            prefEditors.putBoolean("BibliotekaUpdate", true)
            prefEditors.apply()
        }
        return isUbdate
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_trash).isVisible = binding.swipeRefreshLayout.visibility == View.VISIBLE && idSelect == MainActivity.NIADAUNIA && naidaunia.size > 0
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.biblijateka, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_trash) {
            val dialog = DialogDeliteAllNiadaunia()
            dialog.show(childFragmentManager, "DialogDeliteAllNiadaunia")
            return true
        }
        if (id == R.id.action_carkva) {
            activity?.let {
                val intent = Intent()
                intent.setClassName(it, MainActivity.BIBLIATEKALIST)
                startActivity(intent)
            }
        }
        return false
    }

    private suspend fun getSql() {
        activity?.let { activity ->
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    binding.progressBar2.visibility = View.VISIBLE
                }
                try {
                    sqlJob = CoroutineScope(Dispatchers.Main).launch {
                        val temp = ArrayList<ArrayList<String>>()
                        var sb = ""
                        for (i in 0..2) {
                            sb = getBibliatekaJson()
                            if (sb != "") break
                        }
                        if (sb == "") {
                            sb = k.getString("Biblioteka", "") ?: ""
                        }
                        if (sb != "") {
                            val gson = Gson()
                            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                            val biblioteka: ArrayList<ArrayList<String>> = gson.fromJson(sb, type)
                            for (i in 0 until biblioteka.size) {
                                val mySqlList = ArrayList<String>()
                                val kniga = biblioteka[i]
                                val rubrika = kniga[4]
                                val link = kniga[0]
                                val str = kniga[1]
                                val pdf = kniga[2]
                                val pdfFileSize = kniga[3]
                                mySqlList.add(link)
                                mySqlList.add(str)
                                mySqlList.add(pdf)
                                mySqlList.add(pdfFileSize)
                                mySqlList.add(rubrika)
                                val t1 = pdf.lastIndexOf(".")
                                val imageName = pdf.substring(0, t1) + ".png"
                                mySqlList.add(imageName)
                                temp.add(mySqlList)
                            }
                            val json = gson.toJson(temp, type)
                            val prefEditors = k.edit()
                            prefEditors.putString("Biblioteka", json)
                            prefEditors.apply()
                        } else {
                            withContext(Dispatchers.Main) {
                                MainActivity.toastView(activity, activity.getString(R.string.error))
                            }
                        }
                        withContext(Dispatchers.Main) {
                            binding.progressBar2.visibility = View.GONE
                        }
                    }
                } catch (_: Throwable) {
                }
            }
        }
    }

    private suspend fun getBibliatekaJson(): String {
        var text = ""
        activity?.let { activity ->
            val pathReference = Malitounik.referens.child("/bibliateka.json")
            val localFile = File("${activity.filesDir}/cache/cache.txt")
            pathReference.getFile(localFile).addOnCompleteListener {
                if (it.isSuccessful) text = localFile.readText()
            }.await()
        }
        return text
    }

    private suspend fun saveImagePdf(imageFile: File, image: String): Boolean {
        var error = false
        Malitounik.referens.child("/images/bibliateka/$image").getFile(imageFile)
            .addOnFailureListener {
                error = true
            }.await()
        return error
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("filePath", filePath)
        outState.putString("fileName", fileName)
        outState.putInt("idSelect", idSelect)
    }

    private fun showPopupMenu(view: View, position: Int, name: String) {
        (activity as? BaseActivity)?.let {
            val popup = PopupMenu(it, view)
            val infl = popup.menuInflater
            infl.inflate(R.menu.popup_biblioteka, popup.menu)
            val file = File(it.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), arrayList[position][2])
            if (file.exists()) {
                popup.menu.getItem(1).isVisible = false
            } else {
                popup.menu.getItem(2).isVisible = false
                if (!MainActivity.isNetworkAvailable()) popup.menu.getItem(1).isVisible = false
            }
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                var itemFontSize = it.setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
                if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
                spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                when (menuItem.itemId) {
                    R.id.menu_opisanie -> {
                        val dialogBibliateka = DialogBibliateka.getInstance(arrayList[position][2], arrayList[position][1], arrayList[position][0], arrayList[position][3])
                        dialogBibliateka.show(childFragmentManager, "dialog_bibliateka")
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_download -> {
                        onDialogbibliatekaPositiveClick(arrayList[position][2], name)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_delite -> {
                        val dd = DialogDelite.getInstance(0, arrayList[position][2], "з бібліятэкі", name)
                        dd.show(childFragmentManager, "dialog_delite")
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_print -> {
                        if (file.exists()) {
                            val printAdapter = PdfDocumentAdapter(file.absolutePath)
                            val printManager = it.getSystemService(Context.PRINT_SERVICE) as PrintManager
                            val printAttributes = PrintAttributes.Builder()
                                .setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                            printManager.print(file.name, printAdapter, printAttributes)
                        } else {
                            if (it.checkmoduleResources()) {
                                if (MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_CELLULAR)) {
                                    val bibliotekaWiFi = DialogBibliotekaWIFI.getInstance(arrayList[position][2], false, isPrint = true)
                                    bibliotekaWiFi.show(childFragmentManager, "biblioteka_WI_FI")
                                } else {
                                    writeFile(arrayList[position][2], false, isPrint = true)
                                }
                            } else {
                                it.installFullMalitounik()
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_share -> {
                        if (file.exists()) {
                            val sendIntent = Intent(Intent.ACTION_SEND)
                            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(it, "by.carkva_gazeta.malitounik.fileprovider", file))
                            sendIntent.putExtra(Intent.EXTRA_SUBJECT, it.getString(R.string.set_log_file))
                            sendIntent.type = "text/html"
                            startActivity(Intent.createChooser(sendIntent, it.getString(R.string.set_log_file)))
                        } else {
                            if (it.checkmoduleResources()) {
                                if (MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_CELLULAR)) {
                                    val bibliotekaWiFi = DialogBibliotekaWIFI.getInstance(arrayList[position][2], true, isPrint = false)
                                    bibliotekaWiFi.show(childFragmentManager, "biblioteka_WI_FI")
                                } else {
                                    writeFile(arrayList[position][2], true, isPrint = false)
                                }
                            } else {
                                it.installFullMalitounik()
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
            popup.show()
        }
    }

    internal inner class BibliotekaAdapter(context: Activity) : ArrayAdapter<ArrayList<String>>(context, R.layout.simple_list_item_biblioteka, arrayList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemBibliotekaBinding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label, binding.imageView2, binding.buttonPopup)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.imageView.layoutParams?.width = width / 2
            viewHolder.imageView.layoutParams?.height = (width / 2 * 1.4F).toInt()
            viewHolder.imageView.requestLayout()
            activity?.let { activity ->
                if (arrayList[position].size == 3) {
                    viewHolder.buttonPopup.visibility = View.GONE
                    if (arrayList[position][2] != "") {
                        bitmapJob = CoroutineScope(Dispatchers.Main).launch {
                            val bitmap = withContext(Dispatchers.IO) {
                                val options = BitmapFactory.Options()
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                                return@withContext BitmapFactory.decodeFile(arrayList[position][2], options)
                            }
                            viewHolder.imageView.setImageBitmap(bitmap)
                            viewHolder.imageView.visibility = View.VISIBLE
                        }
                    } else {
                        viewHolder.imageView.visibility = View.GONE
                    }
                } else {
                    viewHolder.buttonPopup.visibility = View.VISIBLE
                    viewHolder.buttonPopup.let {
                        viewHolder.buttonPopup.setOnClickListener {
                            showPopupMenu(it, position, arrayList[position][0])
                        }
                    }
                    bitmapJob = CoroutineScope(Dispatchers.Main).launch {
                        val t1 = arrayList[position][5].lastIndexOf("/")
                        val image = arrayList[position][5].substring(t1 + 1)
                        val file = File("${activity.filesDir}/image_temp/$image")
                        if (!file.exists() && MainActivity.isNetworkAvailable()) {
                            for (e in 0..2) {
                                if (!saveImagePdf(file, image)) break
                            }
                        }
                        if (file.exists()) {
                            val bitmap = withContext(Dispatchers.IO) {
                                val options = BitmapFactory.Options()
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                                return@withContext BitmapFactory.decodeFile("${activity.filesDir}/image_temp/$image", options)
                            }
                            viewHolder.imageView.setImageBitmap(bitmap)
                            viewHolder.imageView.visibility = View.VISIBLE
                        }
                    }
                }
                if (dzenNoch) {
                    viewHolder.text.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                    viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
                }
            }
            viewHolder.text.text = arrayList[position][0]
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var imageView: ImageView, var buttonPopup: ImageView)

    companion object {
        private const val NOUPDATE = 0
        private const val ERROR = 1
        private const val UPDATE = 2

        fun getInstance(rub: Int): MenuBiblijateka {
            val bundle = Bundle()
            bundle.putInt("rub", rub)
            val menuPesny = MenuBiblijateka()
            menuPesny.arguments = bundle
            return menuPesny
        }
    }
}
