package by.carkva_gazeta.biblijateka

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.transition.TransitionManager
import by.carkva_gazeta.biblijateka.databinding.BibliotekaViewBinding
import by.carkva_gazeta.biblijateka.databinding.SimpleListItemBibliotekaBinding
import by.carkva_gazeta.malitounik.*
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shockwave.pdfium.PdfDocument
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.*
import java.util.*


class BibliotekaView : BaseActivity(), OnPageChangeListener, OnLoadCompleteListener, DialogSetPageBiblioteka.DialogSetPageBibliotekaListener, DialogTitleBiblioteka.DialogTitleBibliotekaListener, OnErrorListener, DialogBibliotekaWIFI.DialogBibliotekaWIFIListener, DialogBibliateka.DialogBibliatekaListener, DialogDelite.DialogDeliteListener, AdapterView.OnItemLongClickListener, DialogDeliteNiadaunia.DialogDeliteNiadauniaListener, DialogDeliteAllNiadaunia.DialogDeliteAllNiadauniaListener {


    private lateinit var pdfView: PDFView
    private var mLastClickTime: Long = 0
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var filePath = ""
    private var fileName = ""
    private val bookTitle = ArrayList<String>()
    private var menu = false
    private val arrayList = ArrayList<ArrayList<String>>()
    private var width = 0
    private lateinit var adapter: BibliotekaAdapter
    private var nameRubrika = ""
    private var defaultPage = 0
    private var idSelect = MainActivity.NIADAUNIA
    private val naidaunia = ArrayList<ArrayList<String>>()
    private var saveindep = true
    private var runSql = false
    private lateinit var binding: BibliotekaViewBinding
    private var site = false
    private var resetTollbarJob: Job? = null
    private var sqlJob: Job? = null
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val dir = File("$filesDir/BookCache")
            if (!dir.exists()) dir.mkdir()
            val fileUri = it.data?.data
            val path = fileUri?.path ?: ""
            val t1 = path.lastIndexOf("/")
            var mime = "Bibliateka.file"
            if (t1 != -1) {
                mime = path.substring(t1 + 1)
            }
            fileUri?.let { uri ->
                copyInputStreamToFile(contentResolver.openInputStream(uri), mime)
            }
            onDialogFile(File("$filesDir/BookCache/$mime"))
        }
    }

    private fun copyInputStreamToFile(inputStream: InputStream?, mime: String) {
        val outputStream = FileOutputStream(File("$filesDir/BookCache/$mime"))
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

    override fun fileDeliteCancel() {
    }

    override fun deliteNiadaunia(position: Int, file: String) {
        deliteCashe(position, file)
    }

    override fun fileDelite(position: Int, file: String) {
        val file1 = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), file)
        if (file1.exists()) {
            file1.delete()
        }
        deliteCashe(position, file)
    }

    override fun delAllNiadaunia() {
        naidaunia.clear()
        arrayList.clear()
        adapter.notifyDataSetChanged()
        val gson = Gson()
        val prefEditor = k.edit()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
        prefEditor.apply()
        binding.progressBar2.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val dir = File("$filesDir/Book")
                if (dir.exists()) {
                    dir.deleteRecursively()
                }
                val dir2 = File("$filesDir/BookCache")
                if (dir2.exists()) {
                    dir2.deleteRecursively()
                }
            }
            binding.progressBar2.visibility = View.GONE
        }
        invalidateOptionsMenu()
    }

    private fun deliteCashe(position: Int, file: String) {
        val t1 = file.lastIndexOf(".")
        val dirName = if (t1 == -1) file
        else file.substring(0, t1)
        var t2 = dirName.lastIndexOf("/")
        var patch = "$filesDir/Book/" + dirName.substring(t2 + 1)
        var file1 = File(patch)
        if (file1.exists() && file1.isDirectory) {
            file1.deleteRecursively()
        } else {
            t2 = file.lastIndexOf("/")
            patch = "$filesDir/Book/" + file.substring(t2 + 1)
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
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
            prefEditor.apply()
        }
    }

    override fun onDialogPositiveClick(listPosition: String) {
        if (!MainActivity.isNetworkAvailable()) {
            val dialogNoInternet = DialogNoInternet()
            dialogNoInternet.show(supportFragmentManager, "no_internet")
        } else {
            writeFile(listPosition)
        }
    }

    override fun onDialogbibliatekaPositiveClick(listPosition: String, title: String) {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), listPosition)
        if (file.exists()) {
            filePath = file.path
            fileName = title
            loadFilePDF()
            binding.swipeRefreshLayout.visibility = View.GONE
            invalidateOptionsMenu()
        } else {
            if (MainActivity.isNetworkAvailable(true)) {
                val bibliotekaWiFi = DialogBibliotekaWIFI.getInstance(listPosition)
                bibliotekaWiFi.show(supportFragmentManager, "biblioteka_WI_FI")
            } else {
                writeFile(listPosition)
            }
        }
    }

    private fun writeFile(url: String) {
        binding.progressBar2.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            var error = false
            try {
                val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                if (dir?.exists() != true) {
                    dir?.mkdir()
                }
                downloadPdfFile(url)
            } catch (t: Throwable) {
                error = true
            }
            if (!error) {
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
                loadFilePDF()
                binding.swipeRefreshLayout.visibility = View.GONE
                invalidateOptionsMenu()
            } else {
                DialogNoInternet().show(supportFragmentManager, "no_internet")
            }
        }
    }

    private suspend fun downloadPdfFile(url: String) {
        val pathReference = Malitounik.referens.child("/data/bibliateka/$url")
        val localFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), url)
        pathReference.getFile(localFile).addOnFailureListener {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
        filePath = localFile.path
        fileName = url
    }

    override fun onError(t: Throwable?) {
        val pdfError = DialogPdfError.getInstance("PDF")
        pdfError.show(supportFragmentManager, "pdf_error")
    }

    private fun onDialogFile(file: File) {
        binding.swipeRefreshLayout.visibility = View.GONE
        saveindep = false
        idSelect = MainActivity.NIADAUNIA
        nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_niadaunia)
        setRubrika(MainActivity.NIADAUNIA)
        filePath = file.absolutePath
        fileName = file.name
        if (fileName.contains(".pdf", true)) {
            loadFilePDF()
        }
        invalidateOptionsMenu()
    }

    override fun onDialogTitle(page: Int) {
        pdfView.jumpTo(page)
        binding.pageToolbar.text = String.format("%d/%d", page + 1, pdfView.pageCount)
        binding.pageToolbar.visibility = View.VISIBLE
    }

    override fun onDialogTitleString(page: String) {
        binding.titleToolbar.visibility = View.GONE
    }

    override fun onDialogSetPage(page: Int) {
        if (pdfView.visibility == View.VISIBLE) {
            pdfView.jumpTo(page - 1)
            binding.pageToolbar.text = String.format("%d/%d", page, pdfView.pageCount)
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        binding.pageToolbar.text = String.format("%d/%d", page + 1, pageCount)
        binding.pageToolbar.visibility = View.VISIBLE
    }

    override fun loadComplete(nbPages: Int) {
        bookTitle.clear()
        printBookmarksTree(pdfView.tableOfContents)
        var title = pdfView.documentMeta.title
        var position = -1
        if (title == "") {
            arrayList.forEachIndexed { index, kniga ->
                if (kniga[2] == fileName) {
                    position = index
                    title = kniga[0]
                    return@forEachIndexed
                }
            }
        }
        if (title == "" && filePath != "") {
            val t1 = filePath.lastIndexOf("/")
            title = filePath.substring(t1 + 1)
        }
        binding.titleToolbar.text = title
        if (filePath != "") {
            for (i in 0 until naidaunia.size) {
                if (naidaunia[i][1].contains(filePath)) {
                    naidaunia.removeAt(i)
                    break
                }
            }
            val gson = Gson()
            val temp = ArrayList<String>()
            temp.add(title)
            temp.add(filePath)
            val image = if (position != -1) {
                File(arrayList[position][5]).name
            } else {
                val t2 = filePath.lastIndexOf("/")
                val img = filePath.substring(t2 + 1)
                val t1 = img.lastIndexOf(".")
                img.substring(0, t1) + ".png"
            }
            val imageTemp = File("$filesDir/image_temp/$image")
            if (imageTemp.exists()) temp.add("$filesDir/image_temp/$image")
            else temp.add("")
            naidaunia.add(temp)
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val prefEditor = k.edit()
            prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
            prefEditor.apply()
        }
        pdfView.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorSecondary_text)
    }

    private fun printBookmarksTree(tree: List<PdfDocument.Bookmark>) {
        for (b in tree) {
            bookTitle.add(b.pageIdx.toString() + "<>" + b.title)
            if (b.hasChildren()) {
                printBookmarksTree(b.children)
            }
        }
        menu = bookTitle.size != 0
        invalidateOptionsMenu()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplitCompat.install(this)
        width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = windowManager.currentWindowMetrics
            val bounds = display.bounds
            bounds.width()
        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }
        // Копирование и удаление старых файлов из Библиотеки
        val fileOldBib = File("$filesDir/Biblijateka")
        if (fileOldBib.exists()) {
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let {
                fileOldBib.copyRecursively(it, overwrite = true)
            }
            File("$filesDir/Biblijateka").deleteRecursively()
        }
        ////////////////////////////////////
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = BibliotekaViewBinding.inflate(layoutInflater)
        try {
            setContentView(binding.root)
        } catch (t: Resources.NotFoundException) {
            super.onBack()
            val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        pdfView = binding.pdfView

        adapter = BibliotekaAdapter(this)
        binding.listView.adapter = adapter
        if (dzenNoch) {
            binding.listView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            binding.swipeRefreshLayout.setColorSchemeResources(by.carkva_gazeta.malitounik.R.color.colorPrimary_black)
        } else {
            binding.listView.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.color.colorDivider)
            binding.listView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default_bibliateka)
            binding.swipeRefreshLayout.setColorSchemeResources(by.carkva_gazeta.malitounik.R.color.colorPrimary)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (runSql) return@setOnRefreshListener
            if (!MainActivity.isNetworkAvailable()) {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    when (idSelect) {
                        MainActivity.GISTORYIACARKVY -> getSql(MainActivity.GISTORYIACARKVY)
                        MainActivity.MALITOUNIKI -> getSql(MainActivity.MALITOUNIKI)
                        MainActivity.SPEUNIKI -> getSql(MainActivity.SPEUNIKI)
                        MainActivity.RELLITARATURA -> getSql(MainActivity.RELLITARATURA)
                        MainActivity.PDF -> getSql(MainActivity.PDF)
                    }
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
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
                    if (fileName.contains(".pdf", true)) {
                        loadFilePDF()
                    }
                    binding.swipeRefreshLayout.visibility = View.GONE
                    invalidateOptionsMenu()
                } else {
                    if (arrayList[position][1].contains(".epub")) {
                        val res = arrayList[position][1]
                        val t1 = res.lastIndexOf(".epub")
                        val t2 = res.lastIndexOf("/")
                        val delite = File("$filesDir/Book/" + res.substring(t2 + 1, t1) + "/")
                        if (delite.exists()) {
                            delite.deleteRecursively()
                        }
                    }
                    arrayList.removeAt(position)
                    naidaunia.clear()
                    naidaunia.addAll(arrayList)
                    adapter.notifyDataSetChanged()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    val prefEditor = k.edit()
                    prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
                    prefEditor.apply()
                    MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_file))
                }
            } else {
                file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), arrayList[position][2])
                if (file.exists()) {
                    filePath = file.absolutePath
                    fileName = file.name
                    loadFilePDF()
                    binding.swipeRefreshLayout.visibility = View.GONE
                    pdfView.visibility = View.VISIBLE
                    invalidateOptionsMenu()
                } else {
                    var opisanie = arrayList[position][1]
                    val t1 = opisanie.indexOf("</span><br>")
                    if (t1 != -1) opisanie = opisanie.substring(t1 + 11)
                    val dialogBibliateka = DialogBibliateka.getInstance(arrayList[position][2], opisanie, arrayList[position][0], arrayList[position][3])
                    dialogBibliateka.show(supportFragmentManager, "dialog_bibliateka")
                }
            }
        }

        setTollbarTheme()

        val gson = Gson()
        val json = k.getString("bibliateka_naidaunia", "")
        if (json == "") {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val dir = File("$filesDir/Book")
                    if (dir.exists()) dir.deleteRecursively()
                    val dirChesh = File(cacheDir.absolutePath + "/Biblijateka")
                    if (dirChesh.exists()) dirChesh.deleteRecursively()
                }
            }
        }
        var savedInstance = -1
        if (savedInstanceState != null) {
            defaultPage = savedInstanceState.getInt("page")
            filePath = savedInstanceState.getString("filePath") ?: ""
            fileName = savedInstanceState.getString("fileName") ?: ""
            idSelect = savedInstanceState.getInt("idSelect")
            nameRubrika = savedInstanceState.getString("nameRubrika") ?: ""
            MainActivity.dialogVisable = false
            when {
                savedInstanceState.getInt("pdfView", 0) == 1 -> {
                    binding.swipeRefreshLayout.visibility = View.GONE
                    pdfView.visibility = View.VISIBLE
                    savedInstance = 1
                }

                savedInstanceState.getInt("pdfView", 0) == 2 -> {
                    binding.swipeRefreshLayout.visibility = View.GONE
                    pdfView.visibility = View.GONE
                    savedInstance = 2
                }

                else -> {
                    binding.swipeRefreshLayout.visibility = View.VISIBLE
                    pdfView.visibility = View.GONE
                    savedInstance = 0
                }
            }
            saveindep = false
            if (!json.equals("")) {
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                naidaunia.addAll(gson.fromJson(json, type))
            }
            invalidateOptionsMenu()
        } else {
            intent.data?.let { uri ->
                if (uri.toString().contains("root")) {
                    val t1 = uri.toString().indexOf("root")
                    filePath = uri.toString().substring(t1 + 4)
                } else {
                    var cursor: Cursor? = null
                    try {
                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        cursor = contentResolver.query(uri, proj, null, null, null)
                        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA) ?: 0
                        cursor?.moveToFirst()
                        filePath = cursor?.getString(columnIndex) ?: ""
                        fileName = cursor?.getString(0) ?: ""
                        cursor?.close()
                        if (filePath == "") {
                            val dir = File(cacheDir.absolutePath + "/Biblijateka")
                            if (!dir.exists()) dir.mkdir()
                            cursor = contentResolver.query(uri, null, null, null, null)
                            cursor?.moveToFirst()
                            fileName = cursor?.getString(0) ?: ""
                            if (fileName != "") {
                                filePath = cacheDir.absolutePath + "/Biblijateka/" + fileName
                                val inputStream = contentResolver.openInputStream(uri)
                                inputStream?.let {
                                    val file = File(filePath)
                                    file.outputStream().use { fileOut ->
                                        it.copyTo(fileOut)
                                    }
                                }
                                inputStream?.close()
                            }
                        }
                    } catch (_: Throwable) {
                    } finally {
                        cursor?.close()
                    }
                }
            }
            if (intent.extras?.containsKey("filePath") == true) {
                filePath = intent.extras?.getString("filePath") ?: ""
            }
            if (filePath != "") {
                val t1 = filePath.lastIndexOf("/")
                fileName = filePath.substring(t1 + 1)
                binding.swipeRefreshLayout.visibility = View.GONE
                if (fileName.contains(".pdf", true)) {
                    pdfView.visibility = View.VISIBLE
                }
                if (!json.equals("")) {
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    naidaunia.addAll(gson.fromJson(json, type))
                }
            } else {
                if (!json.equals("")) {
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    naidaunia.addAll(gson.fromJson(json, type))
                } else {
                    binding.swipeRefreshLayout.visibility = View.VISIBLE
                }
            }
            site = intent.getBooleanExtra("site", false)
        }
        idSelect = intent.getIntExtra("rub", MainActivity.MALITOUNIKI)
        when (idSelect) {
            MainActivity.NIADAUNIA -> setRubrika(MainActivity.NIADAUNIA)
            MainActivity.GISTORYIACARKVY -> setRubrika(MainActivity.GISTORYIACARKVY)
            MainActivity.MALITOUNIKI -> setRubrika(MainActivity.MALITOUNIKI)
            MainActivity.SPEUNIKI -> setRubrika(MainActivity.SPEUNIKI)
            MainActivity.RELLITARATURA -> setRubrika(MainActivity.RELLITARATURA)
            MainActivity.PDF -> setRubrika(MainActivity.PDF)
            MainActivity.SETFILE -> {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
                mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
                binding.progressBar2.visibility = View.GONE
                setRubrika(MainActivity.NIADAUNIA)
            }
        }
        setTitleBibliateka(idSelect)
        if (filePath != "" && savedInstance != 0) {
            if (filePath.contains("raw:")) {
                val t1 = filePath.indexOf("raw:")
                filePath = filePath.substring(t1 + 4)
            }
            if (fileName.contains(".pdf", true)) {
                saveindep = false
                binding.swipeRefreshLayout.visibility = View.GONE
                loadFilePDF()
            }
            if (filePath.contains(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())) {
                loadFilePDF()
            }
        }
    }

    private fun setTitleBibliateka(rub: Int) {
        when (rub) {
            MainActivity.GISTORYIACARKVY -> {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.pageToolbar.text = ""
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy)
                idSelect = MainActivity.GISTORYIACARKVY
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy)
            }

            MainActivity.MALITOUNIKI -> {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.pageToolbar.text = ""
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki)
                idSelect = MainActivity.MALITOUNIKI
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki)
            }

            MainActivity.SPEUNIKI -> {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.pageToolbar.text = ""
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki)
                idSelect = MainActivity.SPEUNIKI
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki)
            }

            MainActivity.RELLITARATURA -> {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.pageToolbar.text = ""
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura)
                idSelect = MainActivity.RELLITARATURA
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura)
            }

            MainActivity.PDF -> {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.pageToolbar.text = ""
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.arx_num_gaz)
                idSelect = MainActivity.PDF
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.arx_num_gaz)
            }

            else -> {
                arrayList.clear()
                arrayList.addAll(naidaunia)
                arrayList.reverse()
                adapter.notifyDataSetChanged()
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.pageToolbar.text = ""
                binding.subtitleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_niadaunia)
                val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (PackageManager.PERMISSION_DENIED != permissionCheck) {
                    idSelect = MainActivity.NIADAUNIA
                }
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_niadaunia)
            }
        }
    }

    private fun setRubrika(rub: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            if (saveindep) {
                binding.swipeRefreshLayout.visibility = View.VISIBLE
                pdfView.visibility = View.GONE
            }
            if (binding.swipeRefreshLayout.visibility == View.VISIBLE) {
                val gson = Gson()
                val jsonB = k.getString("Biblioteka", "") ?: ""
                val timeUpdate = Calendar.getInstance().timeInMillis
                val timeUpdateSave = k.getLong("BibliotekaTimeUpdate", timeUpdate)
                if (!(jsonB == "" || timeUpdate - timeUpdateSave == 0L)) {
                    if (timeUpdate - timeUpdateSave > (24 * 60 * 60 * 1000L)) {
                        if (MainActivity.isNetworkAvailable()) {
                            val prefEditors = k.edit()
                            prefEditors.putLong("BibliotekaTimeUpdate", timeUpdate)
                            prefEditors.apply()
                            getSql(rub)
                        } else {
                            arrayList.clear()
                            adapter.notifyDataSetChanged()
                            val noInternet = DialogNoInternet()
                            noInternet.show(supportFragmentManager, "no_internet")
                            binding.progressBar2.visibility = View.GONE
                        }
                    } else {
                        arrayList.clear()
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                        arrayList.addAll(gson.fromJson(jsonB, type))
                        val temp = ArrayList<ArrayList<String>>()
                        for (i in 0 until arrayList.size) {
                            val rtemp2 = arrayList[i][4].toInt()
                            if (rtemp2 != rub) temp.add(arrayList[i])
                        }
                        arrayList.removeAll(temp.toSet())
                        adapter.notifyDataSetChanged()
                        binding.progressBar2.visibility = View.GONE
                    }
                } else {
                    if (MainActivity.isNetworkAvailable()) {
                        val prefEditors = k.edit()
                        prefEditors.putLong("BibliotekaTimeUpdate", timeUpdate)
                        prefEditors.apply()
                        getSql(rub)
                    } else {
                        arrayList.clear()
                        adapter.notifyDataSetChanged()
                        val noInternet = DialogNoInternet()
                        noInternet.show(supportFragmentManager, "no_internet")
                        binding.progressBar2.visibility = View.GONE
                    }
                }
            }
            setTitleBibliateka(rub)
            saveindep = true
            invalidateOptionsMenu()
            if (sqlJob?.isActive != true) binding.progressBar2.visibility = View.GONE
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val dd = DialogDeliteNiadaunia.getInstance(position, arrayList[position][1], arrayList[position][0])
        dd.show(supportFragmentManager, "dialog_delite_niadaunia")
        return true
    }

    private fun loadFilePDF() {
        binding.progressBar2.visibility = View.GONE
        pdfView.visibility = View.VISIBLE
        val file = File(filePath)
        val allEntries: Map<String, *> = k.all
        for ((key) in allEntries) {
            if (key.contains(fileName)) {
                defaultPage = k.getInt(fileName, 0)
                break
            }
        }
        pdfView.fromFile(file).enableAntialiasing(true).enableSwipe(true).swipeHorizontal(false).enableDoubletap(true).defaultPage(defaultPage).onLoad(this).onPageChange(this).onError(this).enableAnnotationRendering(false).password(null).scrollHandle(null).enableAntialiasing(true).spacing(2).autoSpacing(false).pageFitPolicy(FitPolicy.WIDTH).pageSnap(false).pageFling(false).nightMode(k.getBoolean("inversion", false)).load()
    }

    override fun onPrepareMenu(menu: Menu) {
        val isTrash = binding.swipeRefreshLayout.visibility == View.VISIBLE
        binding.swipeRefreshLayout.isEnabled = binding.swipeRefreshLayout.visibility == View.VISIBLE
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_trash).isVisible = binding.swipeRefreshLayout.visibility == View.VISIBLE && idSelect == MainActivity.NIADAUNIA && naidaunia.size > 0
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_0).isVisible = isTrash
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_1).isVisible = isTrash
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_2).isVisible = isTrash
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_3).isVisible = isTrash
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_4).isVisible = isTrash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_5).isVisible = false
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_5).isVisible = isTrash
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_rub_6).isVisible = isTrash
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        if (binding.swipeRefreshLayout.visibility == View.GONE) {
            binding.subtitleToolbar.visibility = View.GONE
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = true
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_bright).isVisible = true
            if (pdfView.visibility == View.VISIBLE) {
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = false
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = true
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isVisible = true
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_font).isVisible = false
                if (this.menu) menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = true
            } else {
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = false
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isVisible = false
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_font).isVisible = true
            }
        } else {
            binding.subtitleToolbar.visibility = View.VISIBLE
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_bright).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isVisible = false
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isChecked = k.getBoolean("inversion", false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.bibliotekaview, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_niadaunia)
        binding.subtitleToolbar.text = nameRubrika
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val prefEditor = k.edit()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_0) {
            setRubrika(MainActivity.NIADAUNIA)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_1) {
            setRubrika(MainActivity.GISTORYIACARKVY)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_2) {
            setRubrika(MainActivity.MALITOUNIKI)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_3) {
            setRubrika(MainActivity.SPEUNIKI)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_4) {
            setRubrika(MainActivity.RELLITARATURA)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_6) {
            setRubrika(MainActivity.PDF)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_rub_5) {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
            mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
            binding.progressBar2.visibility = View.GONE
            idSelect = MainActivity.SETFILE
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_trash) {
            val dialog = DialogDeliteAllNiadaunia()
            dialog.show(supportFragmentManager, "DialogDeliteAllNiadaunia")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_title) {
            val titleBiblioteka = DialogTitleBiblioteka.getInstance(bookTitle)
            titleBiblioteka.show(supportFragmentManager, "title_biblioteka")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_set_page) {
            val biblioteka = DialogSetPageBiblioteka.getInstance(pdfView.currentPage, pdfView.pageCount)
            biblioteka.show(supportFragmentManager, "set_page_biblioteka")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_inversion) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                pdfView.setNightMode(true)
                prefEditor.putBoolean("inversion", true)
            } else {
                pdfView.setNightMode(false)
                prefEditor.putBoolean("inversion", false)
            }
            prefEditor.apply()
            pdfView.loadPages()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            val intent = Intent()
            intent.setClassName(this, MainActivity.BIBLIATEKALIST)
            startActivity(intent)
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        val prefEditor = k.edit()
        if (pdfView.visibility == View.VISIBLE) {
            prefEditor.putInt(fileName, pdfView.currentPage)
        }
        prefEditor.apply()
        sqlJob?.cancel()
    }

    override fun onBack() {
        val prefEditor = k.edit()
        if (pdfView.visibility == View.VISIBLE) {
            prefEditor.putInt(fileName, pdfView.currentPage)
        }
        prefEditor.apply()
        if (binding.swipeRefreshLayout.visibility == View.GONE) {
            invalidateOptionsMenu()
            if (arrayList.size == 0) {
                if (idSelect != MainActivity.SETFILE) {
                    setRubrika(idSelect)
                } else {
                    if (site) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    setResult(RESULT_OK)
                    super.onBack()
                }
            } else {
                if (idSelect == MainActivity.NIADAUNIA || idSelect == MainActivity.SETFILE) {
                    arrayList.clear()
                    arrayList.addAll(naidaunia)
                    arrayList.reverse()
                    binding.listView.smoothScrollToPosition(0)
                    adapter.notifyDataSetChanged()
                }
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_carkvy)
                binding.subtitleToolbar.text = nameRubrika
                binding.pageToolbar.text = ""
                binding.swipeRefreshLayout.visibility = View.VISIBLE
                pdfView.visibility = View.GONE
            }
        } else {
            if (site) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            setResult(RESULT_OK)
            super.onBack()
        }
    }

    private suspend fun getSql(rub: Int) {
        withContext(Dispatchers.IO) {
            runSql = true
            withContext(Dispatchers.Main) {
                arrayList.clear()
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.VISIBLE
            }
            try {
                if (MainActivity.isNetworkAvailable()) {
                    sqlJob = CoroutineScope(Dispatchers.Main).launch {
                        val temp = ArrayList<ArrayList<String>>()
                        val sb = getBibliatekaJson()
                        if (sb != "") {
                            val gson = Gson()
                            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                            val biblioteka: ArrayList<ArrayList<String>> = gson.fromJson(sb, type)
                            for (i in 0 until biblioteka.size) {
                                val mySqlList = ArrayList<String>()
                                val kniga = biblioteka[i]
                                val rubrika = kniga[4]
                                val link = kniga[0]
                                val str = kniga[1]
                                val pdf = kniga[2]
                                val pdfFileSize = kniga[3]
                                val image = kniga[5]
                                mySqlList.add(link)
                                mySqlList.add(str)
                                mySqlList.add(pdf)
                                mySqlList.add(pdfFileSize)
                                mySqlList.add(rubrika)
                                val t1 = pdf.lastIndexOf(".")
                                mySqlList.add(pdf.substring(0, t1) + ".png")
                                val dir = File("$filesDir/image_temp")
                                if (!dir.exists()) dir.mkdir()
                                val file = File(image)
                                if (!file.exists()) {
                                    saveImagePdf(pdf, image)
                                }
                                if (rubrika.toInt() == rub) {
                                    arrayList.add(mySqlList)
                                }
                                temp.add(mySqlList)
                            }
                            val json = gson.toJson(temp, type)
                            val prefEditors = k.edit()
                            prefEditors.putString("Biblioteka", json)
                            prefEditors.apply()
                            withContext(Dispatchers.Main) {
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                MainActivity.toastView(this@BibliotekaView, getString(by.carkva_gazeta.malitounik.R.string.error))
                            }
                        }
                        runSql = false
                        withContext(Dispatchers.Main) {
                            binding.progressBar2.visibility = View.GONE
                        }
                    }
                }
            } catch (_: Throwable) {
            }
        }
    }

    private suspend fun getBibliatekaJson(): String {
        var text = ""
        val pathReference = Malitounik.referens.child("/bibliateka.json")
        val localFile = File("$filesDir/cache/cache.txt")
        pathReference.getFile(localFile).addOnCompleteListener {
            if (it.isSuccessful) text = localFile.readText()
            else MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
        return text
    }

    private suspend fun saveImagePdf(pdf: String, image: String) {
        val t1 = pdf.lastIndexOf(".")
        val imageTempFile = File("$filesDir/image_temp/" + pdf.substring(0, t1) + ".png")
        Malitounik.referens.child(image).getFile(imageTempFile).addOnFailureListener {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val prefEditor = k.edit()
        if (pdfView.visibility == View.VISIBLE) prefEditor.putInt(fileName, pdfView.currentPage) else prefEditor.putInt(fileName, defaultPage)
        prefEditor.apply()
        if (pdfView.visibility == View.VISIBLE) {
            outState.putInt("page", pdfView.currentPage)
            outState.putInt("pdfView", 1)
        }
        else {
            outState.putInt("pdfView", 0)
        }
        outState.putString("filePath", filePath)
        outState.putString("fileName", fileName)
        outState.putInt("idSelect", idSelect)
        outState.putString("nameRubrika", nameRubrika)
        outState.putString("titleToolbar", binding.titleToolbar.text.toString())
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR) {
            MainActivity.dialogVisable = false
        }
    }

    private fun showPopupMenu(view: View, position: Int, name: String) {
        val popup = PopupMenu(this, view)
        val infl = popup.menuInflater
        infl.inflate(R.menu.popup_biblioteka, popup.menu)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), arrayList[position][2])
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
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            popup.dismiss()
            when (menuItem.itemId) {
                R.id.menu_opisanie -> {
                    val dialogBibliateka = DialogBibliateka.getInstance(arrayList[position][2], arrayList[position][1], arrayList[position][0], arrayList[position][3])
                    dialogBibliateka.show(supportFragmentManager, "dialog_bibliateka")
                    return@setOnMenuItemClickListener true
                }

                R.id.menu_download -> {
                    onDialogbibliatekaPositiveClick(arrayList[position][2], name)
                    return@setOnMenuItemClickListener true
                }

                R.id.menu_delite -> {
                    val dd = DialogDelite.getInstance(0, arrayList[position][2], "з бібліятэкі", name)
                    dd.show(supportFragmentManager, "dialog_delite")
                    return@setOnMenuItemClickListener true
                }

                R.id.menu_share -> {
                    if (file.exists()) {
                        val sendIntent = Intent(Intent.ACTION_SEND)
                        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, "by.carkva_gazeta.malitounik.fileprovider", file))
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(by.carkva_gazeta.malitounik.R.string.set_log_file))
                        sendIntent.type = "text/html"
                        startActivity(Intent.createChooser(sendIntent, getString(by.carkva_gazeta.malitounik.R.string.set_log_file)))
                    } else {
                        val dialogBibliateka = DialogBibliateka.getInstance(arrayList[position][2], arrayList[position][1], arrayList[position][0], arrayList[position][3])
                        dialogBibliateka.show(supportFragmentManager, "dialog_bibliateka")
                    }
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        popup.show()
    }

    internal inner class BibliotekaAdapter(private val context: Activity) : ArrayAdapter<ArrayList<String>>(context, R.layout.simple_list_item_biblioteka, arrayList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            SplitCompat.install(context)
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemBibliotekaBinding.inflate(LayoutInflater.from(context), parent, false)
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
            if (arrayList[position].size == 3) {
                viewHolder.buttonPopup.visibility = View.GONE
                if (arrayList[position][2] != "") {
                    CoroutineScope(Dispatchers.Main).launch {
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
                val t1 = arrayList[position][5].lastIndexOf("/")
                val file = File("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1))
                if (file.exists()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1), options)
                        }
                        viewHolder.imageView.setImageBitmap(bitmap)
                        viewHolder.imageView.visibility = View.VISIBLE
                    }
                }
            }
            if (dzenNoch) {
                viewHolder.text.setTextColor(ContextCompat.getColor(this@BibliotekaView, by.carkva_gazeta.malitounik.R.color.colorWhite))
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text.text = arrayList[position][0]
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var imageView: ImageView, var buttonPopup: ImageView)
}
