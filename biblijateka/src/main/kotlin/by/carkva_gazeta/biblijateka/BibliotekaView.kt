package by.carkva_gazeta.biblijateka

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.*
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import by.carkva_gazeta.biblijateka.databinding.BibliotekaViewAppBinding
import by.carkva_gazeta.biblijateka.databinding.BibliotekaViewBinding
import by.carkva_gazeta.biblijateka.databinding.BibliotekaViewContentBinding
import by.carkva_gazeta.biblijateka.databinding.SimpleListItemBibliotekaBinding
import by.carkva_gazeta.malitounik.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kursx.parser.fb2.*
import com.shockwave.pdfium.PdfDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.xml.sax.SAXException
import java.io.*
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.xml.parsers.ParserConfigurationException
import kotlin.collections.ArrayList

class BibliotekaView : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener, DialogSetPageBiblioteka.DialogSetPageBibliotekaListener, DialogTitleBiblioteka.DialogTitleBibliotekaListener, OnErrorListener, DialogFileExplorer.DialogFileExplorerListener, View.OnClickListener, DialogBibliotekaWIFI.DialogBibliotekaWIFIListener, DialogBibliateka.DialogBibliatekaListener, DialogDelite.DialogDeliteListener, DialogFontSize.DialogFontSizeListener, WebViewCustom.OnScrollChangedCallback,
    WebViewCustom.OnBottomListener, AdapterView.OnItemLongClickListener {

    private val uiAnimationDelaY: Long = 300
    private val mHideHandler: Handler = Handler(Looper.getMainLooper())

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private val mHidePart2Runnable = Runnable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }
    private val mShowPart2Runnable = Runnable {
        supportActionBar?.show()
    }

    private lateinit var pdfView: PDFView
    private var mLastClickTime: Long = 0
    private val myPermissionsWriteExternalStorage = 39
    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var filePath = ""
    private var fileName = ""
    private val bookTitle = ArrayList<String>()
    private var menu = false
    private val popup: PopupMenu? = null
    private val arrayList = ArrayList<ArrayList<String>>()
    private var width = 0
    private lateinit var adapter: BibliotekaAdapter
    private var nameRubrika = ""
    private var defaultPage = 0
    private var idSelect = R.id.label1
    private val naidaunia = ArrayList<ArrayList<String>>()
    private var saveindep = true
    private var runSql = false
    private var biblioteka: BibliotekaEpub? = null
    private var positionY = 0
    private var fb2: FictionBook? = null
    private var fb2PageText = ""
    private var spid = 60
    private var scrollTimer: Timer = Timer()
    private var procentTimer: Timer = Timer()
    private var resetTimer: Timer = Timer()
    private var scrollerSchedule: TimerTask? = null
    private var procentSchedule: TimerTask? = null
    private var resetSchedule: TimerTask? = null
    private var autoscroll = false
    private lateinit var animInRight: Animation
    private lateinit var animOutRight: Animation
    private lateinit var animInLeft: Animation
    private lateinit var animOutLeft: Animation
    private lateinit var binding: BibliotekaViewBinding
    private lateinit var bindingappbar: BibliotekaViewAppBinding
    private lateinit var bindingcontent: BibliotekaViewContentBinding
    private var animationStoronaLeft = true
    private var site = false
    private var mActionDown = false
    private val orientation: Int
        get() {
            return MainActivity.getOrientation(this)
        }
    private val animationListenerOutRight: AnimationListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            if (biblioteka != null) {
                val t1 = fileName.lastIndexOf(".")
                val dirName = if (t1 != -1) fileName.substring(0, t1)
                else fileName
                val dir = File("$filesDir/Book/$dirName/")
                if (defaultPage - 1 >= 0) {
                    defaultPage--
                    bindingcontent.webView.loadUrl("file://" + dir.absolutePath + "/" + biblioteka?.getPageName(defaultPage))
                }
            } else {
                if (defaultPage - 1 >= 0) {
                    defaultPage--
                    fb2PageText = getFB2Page()
                    bindingcontent.webView.loadDataWithBaseURL(null, fb2PageText, "text/html", "utf-8", null)
                }
            }
            if (autoscroll) {
                bindingcontent.webView.postDelayed({
                    startAutoScroll()
                }, 300)
            }
        }

        override fun onAnimationRepeat(animation: Animation) {}
    }
    private val animationListenerOutLeft: AnimationListener = object : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            if (biblioteka != null) {
                val t1 = fileName.lastIndexOf(".")
                val dirName = if (t1 != -1) fileName.substring(0, t1)
                else fileName
                val dir = File("$filesDir/Book/$dirName/")
                if (defaultPage + 1 < biblioteka?.content?.size ?: 0) {
                    defaultPage++
                    bindingcontent.webView.loadUrl("file://" + dir.absolutePath + "/" + biblioteka?.getPageName(defaultPage))
                }
            } else {
                if (defaultPage + 1 < bookTitle.size) {
                    defaultPage++
                    fb2PageText = getFB2Page()
                    bindingcontent.webView.loadDataWithBaseURL(null, fb2PageText, "text/html", "utf-8", null)
                }
            }
            if (autoscroll) {
                bindingcontent.webView.postDelayed({
                    startAutoScroll()
                }, 300)
            }
        }

        override fun onAnimationRepeat(animation: Animation) {}
    }

    override fun onScroll(t: Int) {
        positionY = t
    }

    override fun onBottom() {
        stopAutoScroll()
    }

    override fun onDialogFontSizePositiveClick() {
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        if (bindingcontent.scrollViewB.visibility == View.VISIBLE) {
            bindingcontent.textViewB.textSize = fontBiblia
        } else {
            val webSettings: WebSettings = bindingcontent.webView.settings
            webSettings.defaultFontSize = fontBiblia.toInt()
        }
    }

    override fun fileDeliteCancel() {
    }

    override fun fileDelite(position: Int, file: String) {
        var file1 = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), file)
        if (file1.exists()) {
            file1.delete()
        } else {
            val t1 = file.lastIndexOf(".")
            val dirName = file.substring(0, t1)
            var t2 = dirName.lastIndexOf("/")
            var patch = "$filesDir/Book/" + dirName.substring(t2 + 1)
            file1 = File(patch)
            if (file1.exists() && file1.isDirectory) {
                file1.deleteRecursively()
            } else {
                t2 = file.lastIndexOf("/")
                patch = "$filesDir/Book/" + file.substring(t2 + 1)
                file1 = File(patch)
                if (file1.exists()) file1.delete()
            }
        }
        var position1 = -1
        naidaunia.forEachIndexed { index, arrayList1 ->
            if (arrayList1[0] == arrayList[position][0]) {
                position1 = index
            }
        }
        if (position1 != -1) {
            if (idSelect == R.id.label1) {
                arrayList.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            naidaunia.removeAt(position1)
            val gson = Gson()
            val prefEditor: SharedPreferences.Editor = k.edit()
            prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
            prefEditor.apply()
        }
        popup?.menu?.getItem(2)?.isVisible = false
    }

    override fun onDialogPositiveClick(listPosition: String?) {
        if (MainActivity.isIntNetworkAvailable(this) == 0) {
            val dialogNoInternet = DialogNoInternet()
            dialogNoInternet.show(supportFragmentManager, "no_internet")
        } else {
            writeFile("https://carkva-gazeta.by/data/bibliateka/$listPosition")
        }
    }

    override fun onDialogbibliatekaPositiveClick(listPosition: String, title: String) {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), listPosition)
        if (file.exists()) {
            filePath = file.path
            fileName = title
            loadFilePDF()
            bindingcontent.listView.visibility = View.GONE
            bindingcontent.webView.visibility = View.GONE
            bindingcontent.scrollViewB.visibility = View.GONE
            invalidateOptionsMenu()
        } else {
            if (MainActivity.isIntNetworkAvailable(this) == 2) {
                val bibliotekaWiFi: DialogBibliotekaWIFI = DialogBibliotekaWIFI.getInstance(listPosition)
                bibliotekaWiFi.show(supportFragmentManager, "biblioteka_WI_FI")
            } else {
                writeFile("https://carkva-gazeta.by/data/bibliateka/$listPosition")
            }
        }
    }

    private fun writeFile(url: String) {
        bindingcontent.progressBar2.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            var error = false
            withContext(Dispatchers.IO) {
                try {
                    val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    if (dir?.exists() != true) {
                        dir?.mkdir()
                    }
                    val myUrl = URL(url)
                    val last = url.lastIndexOf("/")
                    val uplFilename = url.substring(last + 1)
                    val inpstr: InputStream = myUrl.openStream()
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), uplFilename)
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inpstr.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.close()
                    filePath = file.path
                    fileName = uplFilename
                } catch (t: Throwable) {
                    error = true
                }
                return@withContext
            }
            if (!error) {
                adapter.notifyDataSetChanged()
                bindingcontent.progressBar2.visibility = View.GONE
                loadFilePDF()
                bindingcontent.listView.visibility = View.GONE
                bindingcontent.webView.visibility = View.GONE
                bindingcontent.scrollViewB.visibility = View.GONE
                invalidateOptionsMenu()
            } else {
                DialogNoInternet().show(supportFragmentManager, "no_internet")
            }
        }
    }

    override fun onError(t: Throwable?) {
        val pdfError = DialogPdfError.getInstance("PDF")
        pdfError.show(supportFragmentManager, "pdf_error")
    }

    override fun onDialogFile(file: File) {
        onClick(binding.label1)
        filePath = file.absolutePath
        fileName = file.name
        when {
            fileName.toLowerCase(Locale.getDefault()).contains(".fb2.zip") -> {
                loadFileFB2ZIP()
            }
            fileName.toLowerCase(Locale.getDefault()).contains(".fb2") -> {
                loadFileFB2()
            }
            fileName.toLowerCase(Locale.getDefault()).contains(".pdf") -> {
                loadFilePDF()
            }
            fileName.toLowerCase(Locale.getDefault()).contains(".txt") -> {
                loadFileTXT()
            }
            fileName.toLowerCase(Locale.getDefault()).contains(".htm") -> {
                loadFileHTML()
            }
            else -> {
                loadFileEPUB()
            }
        }
        bindingcontent.listView.visibility = View.GONE
        invalidateOptionsMenu()
    }

    private fun ajustCompoundDrawableSizeWithText(textView: TextViewRobotoCondensed, leftDrawable: Drawable?) {
        textView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                leftDrawable?.setBounds(0, 0, textView.textSize.toInt(), textView.textSize.toInt())
                textView.setCompoundDrawables(leftDrawable, null, null, null)
                textView.removeOnLayoutChangeListener(this)
            }
        })
    }

    override fun onDialogTitle(page: Int) {
        if (bindingcontent.webView.visibility == View.VISIBLE) {
            defaultPage = page
            fb2PageText = getFB2Page()
            bindingcontent.webView.loadDataWithBaseURL(null, fb2PageText, "text/html", "utf-8", null)
        } else {
            pdfView.jumpTo(page)
            bindingappbar.pageToolbar.text = String.format("%d/%d", page + 1, pdfView.pageCount)
            bindingappbar.pageToolbar.visibility = View.VISIBLE
        }
    }

    override fun onDialogTitleString(page: String) {
        val t1 = fileName.lastIndexOf(".")
        val dirName = if (t1 != -1) fileName.substring(0, t1)
        else fileName
        val dir = File("$filesDir/Book/$dirName/")
        bindingcontent.webView.loadUrl("file://" + dir.absolutePath.toString() + "/" + page)
        defaultPage = biblioteka?.setPage(page) ?: 0
        bindingappbar.pageToolbar.visibility = View.GONE
    }

    override fun onDialogSetPage(page: Int) {
        if (pdfView.visibility == View.VISIBLE) {
            pdfView.jumpTo(page - 1)
            bindingappbar.pageToolbar.text = String.format("%d/%d", page, pdfView.pageCount)
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        bindingappbar.pageToolbar.text = String.format("%d/%d", page + 1, pageCount)
        bindingappbar.pageToolbar.visibility = View.VISIBLE
    }

    override fun loadComplete(nbPages: Int) {
        bookTitle.clear()
        printBookmarksTree(pdfView.tableOfContents)
        var title: String = pdfView.documentMeta.title
        if (title == "") {
            val t1: Int = filePath.lastIndexOf("/")
            title = filePath.substring(t1 + 1)
        }
        bindingappbar.titleToolbar.text = title
        for (i in 0 until naidaunia.size) {
            if (naidaunia[i][1].contains(filePath)) {
                naidaunia.removeAt(i)
                break
            }
        }
        val gson = Gson()
        val temp: ArrayList<String> = ArrayList()
        temp.add(title)
        temp.add(filePath)
        val t2: Int = filePath.lastIndexOf("/")
        val image: String = filePath.substring(t2 + 1)
        val t1 = image.lastIndexOf(".")
        val imageTemp = File(filesDir.toString() + "/image_temp/" + image.substring(0, t1) + ".png")
        if (imageTemp.exists()) temp.add(filesDir.toString() + "/image_temp/" + image.substring(0, t1) + ".png")
        else temp.add("")
        naidaunia.add(temp)
        val prefEditor: SharedPreferences.Editor = k.edit()
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
        prefEditor.apply()
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
        SplitCompat.install(this)
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        // Копирование и удаление старых файлов из Библиотеки
        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let {
            val file = File("$filesDir/Biblijateka")
            if (file.exists()) file.copyRecursively(it, overwrite = true)
        }
        File("$filesDir/Biblijateka").deleteRecursively()
        ////////////////////////////////////
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        binding = BibliotekaViewBinding.inflate(layoutInflater)
        bindingappbar = binding.bibliotekaViewApp
        bindingcontent = binding.bibliotekaViewApp.bibliotekaViewContent
        try {
            setContentView(binding.root)
        } catch (t: Resources.NotFoundException) {
            finish()
            val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        pdfView = bindingcontent.pdfView

        autoscroll = k.getBoolean("autoscroll", false)
        adapter = BibliotekaAdapter(this)
        bindingcontent.listView.adapter = adapter
        if (dzenNoch) bindingcontent.listView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else bindingcontent.listView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        bindingcontent.listView.setOnItemClickListener { _, _, position, _ ->
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
                    if (!File(arrayList[position][2]).exists()) {
                        val permissionCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            1
                        }
                        if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), myPermissionsWriteExternalStorage)
                            }
                            return@setOnItemClickListener
                        }
                    }
                    when {
                        fileName.toLowerCase(Locale.getDefault()).contains(".pdf") -> {
                            loadFilePDF()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".fb2.zip") -> {
                            loadFileFB2ZIP()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".fb2") -> {
                            loadFileFB2()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".txt") -> {
                            loadFileTXT()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".htm") -> {
                            loadFileHTML()
                        }
                        else -> {
                            loadFileEPUB()
                        }
                    }
                    if (!fileName.toLowerCase(Locale.getDefault()).contains(".pdf")) {
                        autoscroll = k.getBoolean("autoscroll", false)
                        spid = k.getInt("autoscrollSpid", 60)
                        if (autoscroll) {
                            startAutoScroll()
                        }
                    }
                    bindingcontent.listView.visibility = View.GONE
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
                    val prefEditor = k.edit()
                    prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
                    prefEditor.apply()
                    MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_file))
                }
            } else {
                file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), arrayList[position][2])
                if (file.exists()) {
                    filePath = file.absolutePath
                    fileName = file.name
                    loadFilePDF()
                    bindingcontent.listView.visibility = View.GONE
                    bindingcontent.webView.visibility = View.GONE
                    bindingcontent.scrollViewB.visibility = View.GONE
                    pdfView.visibility = View.VISIBLE
                    invalidateOptionsMenu()
                } else {
                    val dialogBibliateka = DialogBibliateka.getInstance(arrayList[position][2], arrayList[position][1], arrayList[position][0], arrayList[position][3])
                    dialogBibliateka.show(supportFragmentManager, "dialog_bibliateka")
                }
            }
        }

        animInRight = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_right)
        animOutRight = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
        animInLeft = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
        animOutLeft = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_out_left)
        animOutRight.setAnimationListener(animationListenerOutRight)
        animOutLeft.setAnimationListener(animationListenerOutLeft)
        positionY = k.getInt("webViewBibliotekaScroll", 0)
        bindingcontent.webView.setOnScrollChangedCallback(this)
        bindingcontent.webView.setOnTouchListener(object : OnSwipeTouchListener(this) {

            override fun onSwipeRight() {
                if (defaultPage - 1 >= 0) {
                    stopAutoScroll()
                    animationStoronaLeft = false
                    bindingcontent.webView.startAnimation(animOutRight)
                }
            }

            override fun onSwipeLeft() {
                if (biblioteka != null) {
                    if (defaultPage + 1 < biblioteka?.content?.size ?: 0) {
                        stopAutoScroll()
                        animationStoronaLeft = true
                        bindingcontent.webView.startAnimation(animOutLeft)
                    }
                } else {
                    if (defaultPage + 1 < bookTitle.size) {
                        stopAutoScroll()
                        animationStoronaLeft = true
                        bindingcontent.webView.startAnimation(animOutLeft)
                    }
                }
            }

            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                if (view?.id == R.id.webView) {
                    when (motionEvent?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (autoscroll) mActionDown = true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (autoscroll) mActionDown = false
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            if (autoscroll) mActionDown = false
                        }
                    }
                }
                return super.onTouch(view, motionEvent)
            }
        })
        bindingcontent.webView.webViewClient = HelloWebViewClient()
        val webSettings = bindingcontent.webView.settings
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.standardFontFamily = "sans-serif-condensed"
        webSettings.defaultFontSize = fontBiblia.toInt()
        webSettings.allowFileAccess = true
        binding.label1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label3.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label4.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label5.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label6.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)

        var drawable = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.krest)
        if (dzenNoch) {
            drawable = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.krest_black)
            binding.title.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.label6.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        } else {
            binding.label6.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
        }
        ajustCompoundDrawableSizeWithText(binding.label1, drawable)
        ajustCompoundDrawableSizeWithText(binding.label2, drawable)
        ajustCompoundDrawableSizeWithText(binding.label3, drawable)
        ajustCompoundDrawableSizeWithText(binding.label4, drawable)
        ajustCompoundDrawableSizeWithText(binding.label5, drawable)
        ajustCompoundDrawableSizeWithText(binding.label6, drawable)
        binding.label1.setOnClickListener(this)
        binding.label2.setOnClickListener(this)
        binding.label3.setOnClickListener(this)
        binding.label4.setOnClickListener(this)
        binding.label5.setOnClickListener(this)
        binding.label6.setOnClickListener(this)

        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        setTollbarTheme()

        val gson = Gson()
        val json = k.getString("bibliateka_naidaunia", "")
        var savedInstance = -1
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            defaultPage = savedInstanceState.getInt("page")
            filePath = savedInstanceState.getString("filePath") ?: ""
            fileName = savedInstanceState.getString("fileName") ?: ""
            idSelect = savedInstanceState.getInt("idSelect")
            nameRubrika = savedInstanceState.getString("nameRubrika") ?: ""
            when {
                savedInstanceState.getInt("pdfView", 0) == 1 -> {
                    bindingcontent.listView.visibility = View.GONE
                    pdfView.visibility = View.VISIBLE
                    bindingcontent.webView.visibility = View.GONE
                    bindingcontent.scrollViewB.visibility = View.GONE
                    savedInstance = 1
                }
                savedInstanceState.getInt("pdfView", 0) == 2 -> {
                    bindingcontent.listView.visibility = View.GONE
                    bindingcontent.webView.visibility = View.VISIBLE
                    pdfView.visibility = View.GONE
                    savedInstance = 2
                }
                else -> {
                    bindingcontent.listView.visibility = View.VISIBLE
                    pdfView.visibility = View.GONE
                    bindingcontent.webView.visibility = View.GONE
                    bindingcontent.scrollViewB.visibility = View.GONE
                    savedInstance = 0
                }
            }
            saveindep = false
            if (!json.equals("")) {
                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                naidaunia.addAll(gson.fromJson(json, type))
            }
            if (idSelect != R.id.label6) onClick(findViewById(idSelect))
            invalidateOptionsMenu()
            if (fullscreenPage) hide()
        } else {
            intent.data?.let {
                if (it.toString().contains("root")) {
                    val t1 = it.toString().indexOf("root")
                    filePath = it.toString().substring(t1 + 4)
                } else {
                    var cursor: Cursor? = null
                    try {
                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        cursor = contentResolver.query(it, proj, null, null, null)
                        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        cursor?.moveToFirst()
                        filePath = cursor?.getString(columnIndex ?: 0) ?: ""
                    } catch (t: Throwable) {
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
                bindingcontent.listView.visibility = View.GONE
                if (fileName.toLowerCase(Locale.getDefault()).contains(".pdf")) {
                    pdfView.visibility = View.VISIBLE
                } else {
                    bindingcontent.webView.visibility = View.VISIBLE
                }
                if (!json.equals("")) {
                    val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                    naidaunia.addAll(gson.fromJson(json, type))
                }
            } else {
                if (!json.equals("")) {
                    val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                    naidaunia.addAll(gson.fromJson(json, type))
                    if (naidaunia.size == 0) {
                        binding.drawerLayout.openDrawer(GravityCompat.START)
                        bindingcontent.listView.visibility = View.VISIBLE
                    } else onClick(binding.label1)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                    bindingcontent.listView.visibility = View.VISIBLE
                }
            }
            site = intent.getBooleanExtra("site", false)
            if (site) binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        if (filePath != "" && savedInstance != 0) {
            if (filePath.contains("raw:")) {
                val t1 = filePath.indexOf("raw:")
                filePath = filePath.substring(t1 + 4)
            }
            val permissionCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                1
            }
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), myPermissionsWriteExternalStorage)
                }
            } else {
                when {
                    fileName.toLowerCase(Locale.getDefault()).contains(".pdf") -> {
                        loadFilePDF()
                    }
                    fileName.toLowerCase(Locale.getDefault()).contains(".fb2.zip") -> {
                        loadFileFB2ZIP()
                    }
                    fileName.toLowerCase(Locale.getDefault()).contains(".fb2") -> {
                        loadFileFB2()
                    }
                    fileName.toLowerCase(Locale.getDefault()).contains(".txt") -> {
                        loadFileTXT()
                    }
                    fileName.toLowerCase(Locale.getDefault()).contains(".htm") -> {
                        loadFileHTML()
                    }
                    else -> {
                        loadFileEPUB()
                    }
                }
                bindingcontent.listView.visibility = View.GONE
            }
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val dd = DialogDelite.getInstance(position, arrayList[position][1], "з нядаўніх кніг", arrayList[position][0])
        dd.show(supportFragmentManager, "dialog_dilite")
        return true
    }

    private fun loadFilePDF() {
        bindingcontent.progressBar2.visibility = View.GONE
        pdfView.visibility = View.VISIBLE
        val allEntries: Map<String, *> = k.all
        for ((key) in allEntries) {
            if (key.contains(fileName)) {
                defaultPage = k.getInt(fileName, 0)
                break
            }
        }
        val file = File(filePath)
        pdfView.fromFile(file).enableAntialiasing(true)
            .enableSwipe(true)
            .swipeHorizontal(false).enableDoubletap(true).defaultPage(defaultPage)
            .onLoad(this)
            .onPageChange(this)
            .onError(this)
            .enableAnnotationRendering(false)
            .password(null).scrollHandle(null).enableAntialiasing(true)
            .spacing(2).autoSpacing(false)
            .pageFitPolicy(FitPolicy.WIDTH).pageSnap(false)
            .pageFling(false)
            .nightMode(k.getBoolean("inversion", false))
            .load()
    }

    private fun loadFileTXT() {
        bindingcontent.progressBar2.visibility = View.GONE
        bindingcontent.scrollViewB.visibility = View.VISIBLE
        val file = File(filePath)
        bindingcontent.textViewB.text = file.readText()
        val t1 = file.name.lastIndexOf(".")
        bindingappbar.titleToolbar.text = file.name.substring(0, t1)
        for (i in 0 until naidaunia.size) {
            if (naidaunia[i][1].contains(filePath)) {
                naidaunia.removeAt(i)
                break
            }
        }
        val gson = Gson()
        val temp: ArrayList<String> = ArrayList()
        temp.add(file.name.substring(0, t1))
        temp.add(filePath)
        temp.add("")
        naidaunia.add(temp)
        val prefEditor: SharedPreferences.Editor = k.edit()
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
        prefEditor.apply()
    }

    private fun loadFileHTML() {
        bindingcontent.progressBar2.visibility = View.GONE
        bindingcontent.webView.visibility = View.VISIBLE
        val file = File(filePath)
        bindingcontent.webView.loadUrl("file://" + file.absolutePath)
        val t1 = file.name.lastIndexOf(".")
        bindingappbar.titleToolbar.text = file.name.substring(0, t1)
        for (i in 0 until naidaunia.size) {
            if (naidaunia[i][1].contains(filePath)) {
                naidaunia.removeAt(i)
                break
            }
        }
        val gson = Gson()
        val temp: ArrayList<String> = ArrayList()
        temp.add(file.name.substring(0, t1))
        temp.add(filePath)
        temp.add("")
        naidaunia.add(temp)
        val prefEditor: SharedPreferences.Editor = k.edit()
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
        prefEditor.apply()
    }

    private fun loadFileEPUB() {
        val naidauCount: Int = naidaunia.size - 1
        if (biblioteka == null || naidaunia.size <= 0 || !naidaunia[naidauCount][1].contains(filePath)) {
            val file = File(filePath)
            val t1 = fileName.lastIndexOf(".")
            val dirName = if (t1 != -1) fileName.substring(0, t1)
            else fileName
            val dir = File("$filesDir/Book/$dirName/")
            if (!dir.exists()) {
                bindingcontent.progressBar2.visibility = View.VISIBLE
                dir.mkdirs()
                CoroutineScope(Dispatchers.Main).launch {
                    val unzip = withContext(Dispatchers.IO) {
                        return@withContext unzip(file, dir)
                    }
                    if (unzip) {
                        loadFileEPUB(dir)
                        bindingcontent.progressBar2.visibility = View.GONE
                    } else {
                        bindingcontent.progressBar2.visibility = View.GONE
                    }
                }
            } else {
                loadFileEPUB(dir)
            }
        } else {
            bindingcontent.webView.visibility = View.VISIBLE
            bindingappbar.titleToolbar.text = biblioteka?.bookTitle
        }
    }

    private fun loadFileEPUB(dir: File) {
        animationStoronaLeft = true
        val allEntries: Map<String, *> = k.all
        for ((key) in allEntries) {
            if (key.contains(fileName)) {
                defaultPage = k.getInt(fileName, 0)
                break
            }
        }
        biblioteka = BibliotekaEpub(dir.absolutePath)
        bindingcontent.webView.visibility = View.VISIBLE
        if (defaultPage >= biblioteka?.content?.size ?: 0) {
            defaultPage = 0
            positionY = 0
        }
        val split = biblioteka?.content?.get(defaultPage)?.get(1)?.split("#")?: ArrayList()
        bindingcontent.webView.loadUrl("file://" + dir.absolutePath.toString() + "/" + split[0])
        bindingcontent.webView.scrollTo(0, positionY)
        bookTitle.clear()
        bookTitle.addAll(biblioteka?.contentList as ArrayList<String>)
        bindingappbar.titleToolbar.text = biblioteka?.bookTitle
        bindingappbar.pageToolbar.visibility = View.GONE
        for (i in 0 until naidaunia.size) {
            if (naidaunia[i][1].contains(filePath)) {
                naidaunia.removeAt(i)
                break
            }
        }
        val gson = Gson()
        val temp: ArrayList<String> = ArrayList()
        temp.add(biblioteka?.bookTitle ?: "")
        temp.add(filePath)
        temp.add(biblioteka?.titleImage ?: "")
        naidaunia.add(temp)
        val prefEditor: SharedPreferences.Editor = k.edit()
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
        prefEditor.apply()
    }

    private fun loadFileFB2ZIP() {
        val dir = File("$filesDir/Book")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val naidauCount: Int = naidaunia.size - 1
        if (fb2 == null || naidaunia.size <= 0 || !naidaunia[naidauCount][1].contains(filePath)) {
            var file = File(filePath)
            val zis = ZipInputStream(BufferedInputStream(FileInputStream(file)))
            var ze = ZipEntry("")
            var count: Int
            val buffer = ByteArray(8192)
            while (zis.nextEntry?.also { ze = it } != null) {
                file = File(dir.canonicalPath, ze.name)
                val canonicalPath = file.canonicalPath
                if (!canonicalPath.startsWith(dir.canonicalPath)) {
                    val securityError = DialogSecurityError()
                    securityError.show(supportFragmentManager, "securityError")
                    return
                }
                FileOutputStream(file).use { fout ->
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                }
            }
            filePath = file.absolutePath
            loadFileFB2()
        } else {
            bindingcontent.webView.visibility = View.VISIBLE
            bindingappbar.titleToolbar.text = fb2?.title
        }
    }

    private fun loadFileFB2() {
        animationStoronaLeft = true
        biblioteka = null
        val allEntries: Map<String, *> = k.all
        for ((key) in allEntries) {
            if (key.contains(fileName)) {
                defaultPage = k.getInt(fileName, 0)
                break
            }
        }
        try {
            fb2 = FictionBook(File(filePath))
        } catch (e: ParserConfigurationException) {
            val pdfError: DialogPdfError = DialogPdfError.getInstance("FB2")
            pdfError.show(supportFragmentManager, "pdf_error")
            return
        } catch (e: IOException) {
            val pdfError: DialogPdfError = DialogPdfError.getInstance("FB2")
            pdfError.show(supportFragmentManager, "pdf_error")
            return
        } catch (e: SAXException) {
            val pdfError: DialogPdfError = DialogPdfError.getInstance("FB2")
            pdfError.show(supportFragmentManager, "pdf_error")
            return
        }
        val dir = File("$filesDir/Book")
        if (!dir.exists()) {
            dir.mkdir()
        }
        var file = File("$filesDir/Book")
        fb2?.let {
            val map: Map<String, Binary> = it.binaries
            for ((key, value) in map) {
                if (key.toLowerCase(Locale.getDefault()).contains("cover")) {
                    file = File("$filesDir/Book", File(filePath).name.toString() + key)
                    val buffer = value.binary
                    FileOutputStream(file).use { fout -> fout.write(decode(buffer, DEFAULT)) }
                }
            }
        }
        val section: ArrayList<Section> = fb2?.body?.sections ?: ArrayList()
        val content: ArrayList<String> = ArrayList()
        var count = 0
        for (i in 0 until section.size) {
            val section2: ArrayList<Section> = section[i].sections
            if (section2.size > 0) {
                for (q in 0 until section2.size) {
                    val titles: ArrayList<Title> = section[i].sections[q].titles
                    if (titles.size > 0) {
                        content.add(count.toString() + "<>" + titles[0].paragraphs[0].text)
                        count++
                    }
                }
            } else {
                val titles: ArrayList<Title> = section[i].titles
                if (titles.size > 0) {
                    content.add(count.toString() + "<>" + titles[0].paragraphs[0].text)
                    count++
                }
            }
        }
        bookTitle.clear()
        bookTitle.addAll(content)
        fb2PageText = getFB2Page()
        bindingcontent.webView.loadDataWithBaseURL(null, fb2PageText, "text/html", "utf-8", null)
        bindingcontent.webView.visibility = View.VISIBLE
        if (defaultPage >= content.size) {
            defaultPage = 0
            positionY = 0
        }
        bindingcontent.webView.scrollTo(0, positionY)
        bindingappbar.titleToolbar.text = fb2?.title
        bindingappbar.pageToolbar.visibility = View.GONE
        for (i in 0 until naidaunia.size) {
            if (naidaunia[i][1].contains(filePath)) {
                naidaunia.removeAt(i)
                break
            }
        }
        val gson = Gson()
        val temp: ArrayList<String> = ArrayList()
        temp.add(fb2?.title ?: "")
        temp.add(filePath)
        temp.add(file.absolutePath)
        naidaunia.add(temp)
        val prefEditor: SharedPreferences.Editor = k.edit()
        prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
        prefEditor.apply()
    }

    private fun getFB2Page(): String {
        val section: ArrayList<Section> = fb2?.body?.sections ?: ArrayList()
        val sb = StringBuilder()
        sb.append("<style>::selection {background: #eb9b9a} img{display: inlineheight: automax-width: 100%} a{text-decoration: none}</style>").append("\n")
        for (i in 0 until section.size) {
            val section2: ArrayList<Section> = section[i].sections
            if (section2.size > 0) {
                for (q in 0 until section2.size) {
                    val elements: ArrayList<Element> = section2[q].elements
                    val titles: ArrayList<Title> = section2[q].titles
                    if (titles.size > 0 && bookTitle[defaultPage].contains(titles[0].paragraphs[0].text)) {
                        val notes: ArrayList<String> = ArrayList()
                        if (titles.size > 0) sb.append("<strong>").append(titles[0].paragraphs[0].text).append("</strong>").append("<p>").append("\n")
                        for (e in 0 until elements.size) {
                            if (elements[e] is P) {
                                val p = elements[e] as P
                                val images: ArrayList<Image>? = p.images
                                when {
                                    images != null -> {
                                        val img: String = images[0].value
                                        fb2?.let {
                                            val map: Map<String, Binary> = it.binaries
                                            var imageRaw = ""
                                            for ((key, value) in map) {
                                                if (img.contains(key)) {
                                                    imageRaw = value.binary
                                                }
                                            }
                                            sb.append("<img src=\"data:image/jpegbase64,").append(imageRaw).append("\" />").append("<p>").append("\n")
                                        }
                                    }
                                    elements[e] is EmptyLine -> {
                                        sb.append("<p>").append("\n")
                                    }
                                    else -> {
                                        var text: String = elements[e].text
                                        val t1 = text.indexOf("[")
                                        if (t1 != -1) {
                                            val t2 = text.indexOf("]", t1 + 1)
                                            notes.add(text.substring(t1 + 1, t2))
                                            text = text.substring(0, t1) + "<sup><a id=\"s_" + text.substring(t1 + 1, t2) + "\" href=\"#n_" + text.substring(t1 + 1, t2) + "\">" + text.substring(t1, t2 + 1) + "</a></sup>" + text.substring(t2 + 1)
                                        }
                                        sb.append(text).append("<p>").append("\n")
                                    }
                                }
                            }
                        }
                        if (notes.size > 0) {
                            sb.append("<hr size=\"2\" color=\"#000000\">").append("<p>").append("\n")
                            fb2?.let {
                                val notesK: ArrayList<Section> = it.notes.sections
                                for (r in 0 until notesK.size) {
                                    for (w in 0 until notes.size) {
                                        if (notesK[r].titles[0].paragraphs[0].text.contains(notes[w])) {
                                            sb.append("[").append(notes[w]).append("] ").append(notesK[r].elements[0].text).append("<p>").append("\n").append(" <a id=\"n_").append(notes[w]).append("\" href=\"#s_").append(notes[w]).append("\">Назад</a>").append("<p>").append("\n")
                                        }
                                    }
                                }
                            }
                        }
                        break
                    }
                }
            } else {
                val elements: ArrayList<Element> = section[i].elements
                val titles: ArrayList<Title> = section[i].titles
                val t1: Int = bookTitle[defaultPage].indexOf(">")
                if (titles.size > 0 && titles[0].paragraphs[0].text.contains(bookTitle[defaultPage].substring(t1 + 1))) {
                    val notes: ArrayList<String> = ArrayList()
                    sb.append("<strong>").append(titles[0].paragraphs[0].text).append("</strong>").append("<p>").append("\n")
                    for (e in 0 until elements.size) {
                        if (elements[e] is P) {
                            val p = elements[e] as P
                            val images: ArrayList<Image>? = p.images
                            when {
                                images != null -> {
                                    val img: String = images[0].value
                                    fb2?.let {
                                        val map: Map<String, Binary> = it.binaries
                                        var imageRaw = ""
                                        for ((key, value) in map) {
                                            if (img.contains(key)) {
                                                imageRaw = value.binary
                                            }
                                        }
                                        sb.append("<img src=\"data:image/jpegbase64,").append(imageRaw).append("\" />").append("<p>").append("\n")
                                    }
                                }
                                elements[e] is EmptyLine -> {
                                    sb.append("<p>").append("\n")
                                }
                                else -> {
                                    var text: String = elements[e].text
                                    val t3 = text.indexOf("[")
                                    if (t3 != -1) {
                                        val t2 = text.indexOf("]", t3 + 1)
                                        notes.add(text.substring(t3 + 1, t2))
                                        text = text.substring(0, t3) + "<sup><a id=\"s_" + text.substring(t3 + 1, t2) + "\" href=\"#n_" + text.substring(t3 + 1, t2) + "\">" + text.substring(t3, t2 + 1) + "</a></sup>" + text.substring(t2 + 1)
                                    }
                                    sb.append(text).append("<p>").append("\n")
                                }
                            }
                        }
                    }
                    if (notes.size > 0) {
                        sb.append("<hr size=\"2\" color=\"#000000\">").append("<p>").append("\n")
                        fb2?.let {
                            val notesK: ArrayList<Section> = it.notes.sections
                            for (r in 0 until notesK.size) {
                                for (w in 0 until notes.size) {
                                    if (notesK[r].titles[0].paragraphs[0].text.contains(notes[w])) {
                                        sb.append("[").append(notes[w]).append("] ").append(notesK[r].elements[0].text).append("<p>").append("\n").append(" <a id=\"n_").append(notes[w]).append("\" href=\"#s_").append(notes[w]).append("\">Назад</a>").append("<p>").append("\n")
                                    }
                                }
                            }
                        }
                    }
                    break
                }
            }
        }
        return sb.toString()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_trash).isVisible = bindingcontent.listView.visibility == View.VISIBLE && idSelect == R.id.label1
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_update).isVisible = bindingcontent.listView.visibility == View.VISIBLE && (idSelect == R.id.label2 || idSelect == R.id.label3 || idSelect == R.id.label4 || idSelect == R.id.label5)
        itemAuto.isVisible = false
        if (bindingcontent.listView.visibility == View.GONE) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_fullscreen).isVisible = true
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = true
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_bright).isVisible = true
            if (pdfView.visibility == View.VISIBLE) {
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = false
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = true
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isVisible = true
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_font).isVisible = false
                if (this.menu) menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = true
            } else {
                if (autoscroll) {
                    menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = true
                    menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = true
                    itemAuto.title = getString(by.carkva_gazeta.malitounik.R.string.autoScrolloff)
                } else {
                    menu.findItem(by.carkva_gazeta.malitounik.R.id.action_plus).isVisible = false
                    menu.findItem(by.carkva_gazeta.malitounik.R.id.action_minus).isVisible = false
                    itemAuto.title = getString(by.carkva_gazeta.malitounik.R.string.autoScrollon)
                }
                when {
                    fileName.toLowerCase(Locale.getDefault()).contains(".txt") -> {
                        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = false
                        itemAuto.isVisible = false
                    }
                    fileName.toLowerCase(Locale.getDefault()).contains(".htm") -> {
                        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = false
                        itemAuto.isVisible = true
                    }
                    else -> {
                        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = true
                        itemAuto.isVisible = true
                    }
                }
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = false
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isVisible = false
                menu.findItem(by.carkva_gazeta.malitounik.R.id.action_font).isVisible = true
                val spanString = SpannableString(itemAuto.title.toString())
                val end = spanString.length
                spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                itemAuto.title = spanString
            }
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_fullscreen).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_set_page).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_bright).isVisible = false
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isVisible = false
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isChecked = k.getBoolean("inversion", false)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl: MenuInflater = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.bibliotekaview, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private fun setTollbarTheme() {
        bindingappbar.titleToolbar.setOnClickListener {
            bindingappbar.titleToolbar.setHorizontallyScrolling(true)
            bindingappbar.titleToolbar.freezesText = true
            bindingappbar.titleToolbar.marqueeRepeatLimit = -1
            if (bindingappbar.titleToolbar.isSelected) {
                bindingappbar.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                bindingappbar.titleToolbar.isSelected = false
            } else {
                bindingappbar.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                bindingappbar.titleToolbar.isSelected = true
            }
        }
        bindingappbar.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        setSupportActionBar(bindingappbar.toolbar)
        if (dzenNoch) {
            bindingappbar.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, bindingappbar.toolbar, by.carkva_gazeta.malitounik.R.string.navigation_drawer_open, by.carkva_gazeta.malitounik.R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == myPermissionsWriteExternalStorage) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (idSelect == R.id.label6) {
                    bindingcontent.progressBar2.visibility = View.GONE
                    val fileExplorer = DialogFileExplorer()
                    fileExplorer.show(supportFragmentManager, "file_explorer")
                } else {
                    when {
                        fileName.toLowerCase(Locale.getDefault()).contains(".pdf") -> {
                            loadFilePDF()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".fb2.zip") -> {
                            loadFileFB2ZIP()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".fb2") -> {
                            loadFileFB2()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".txt") -> {
                            loadFileTXT()
                        }
                        fileName.toLowerCase(Locale.getDefault()).contains(".htm") -> {
                            loadFileHTML()
                        }
                        else -> {
                            loadFileEPUB()
                        }
                    }
                    if (!fileName.toLowerCase(Locale.getDefault()).contains(".pdf")) {
                        autoscroll = k.getBoolean("autoscroll", false)
                        spid = k.getInt("autoscrollSpid", 60)
                        if (autoscroll) {
                            startAutoScroll()
                        }
                    }
                    bindingcontent.listView.visibility = View.GONE
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefEditor: SharedPreferences.Editor = k.edit()
        val id: Int = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_update) {
            if (runSql) return false
            if (MainActivity.isIntNetworkAvailable(this) == 0) {
                val dialogNoInternet = DialogNoInternet()
                dialogNoInternet.show(supportFragmentManager, "no_internet")
            } else {
                var rub = 1
                when (idSelect) {
                    R.id.label2 -> rub = 1
                    R.id.label3 -> rub = 2
                    R.id.label4 -> rub = 3
                    R.id.label5 -> rub = 4
                }
                getSql(rub)
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_trash) {
            naidaunia.clear()
            arrayList.clear()
            adapter.notifyDataSetChanged()
            val gson = Gson()
            prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia))
            bindingcontent.progressBar2.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val dir = File("$filesDir/Book")
                    if (dir.exists()) {
                        dir.deleteRecursively()
                    }
                }
                bindingcontent.progressBar2.visibility = View.GONE
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_title) {
            val titleBiblioteka: DialogTitleBiblioteka = DialogTitleBiblioteka.getInstance(bookTitle)
            titleBiblioteka.show(supportFragmentManager, "title_biblioteka")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_set_page) {
            val biblioteka: DialogSetPageBiblioteka = DialogSetPageBiblioteka.getInstance(pdfView.currentPage, pdfView.pageCount)
            biblioteka.show(supportFragmentManager, "set_page_biblioteka")
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
            pdfView.loadPages()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            if (k.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_orientation) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                requestedOrientation = orientation
                prefEditor.putBoolean("orientation", true)
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                prefEditor.putBoolean("orientation", false)
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_plus) {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingcontent.progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                bindingcontent.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingcontent.progress.visibility = View.VISIBLE
                startProcent()
                stopAutoScroll()
                startAutoScroll()
                prefEditor.putInt("autoscrollSpid", spid)
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_minus) {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingcontent.progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                bindingcontent.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingcontent.progress.visibility = View.VISIBLE
                startProcent()
                stopAutoScroll()
                startAutoScroll()
                prefEditor.putInt("autoscrollSpid", spid)
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
                prefEditor.putBoolean("autoscroll", false)
            } else {
                startAutoScroll()
                prefEditor.putBoolean("autoscroll", true)
            }
            invalidateOptionsMenu()
        }
        prefEditor.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        val prefEditor: SharedPreferences.Editor = k.edit()
        if (pdfView.visibility == View.VISIBLE) {
            prefEditor.putInt(fileName, pdfView.currentPage)
        } else {
            prefEditor.putInt("webViewBibliotekaScroll", positionY)
            prefEditor.putInt(fileName, defaultPage)
        }
        prefEditor.apply()
        stopAutoScroll()
        scrollTimer.cancel()
        resetTimer.cancel()
        procentTimer.cancel()
        scrollerSchedule = null
        procentSchedule = null
        resetSchedule = null
    }

    override fun onBackPressed() {
        val prefEditor: SharedPreferences.Editor = k.edit()
        if (pdfView.visibility == View.VISIBLE) {
            prefEditor.putInt(fileName, pdfView.currentPage)
        } else {
            prefEditor.putInt("webViewBibliotekaScroll", positionY)
            prefEditor.putInt(fileName, defaultPage)
        }
        prefEditor.apply()
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed()
        } else if (bindingcontent.listView.visibility == View.GONE) {
            invalidateOptionsMenu()
            if (arrayList.size == 0) {
                if (idSelect != R.id.label6) {
                    onClick(findViewById(idSelect))
                } else {
                    if (site) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        super.onBackPressed()
                    }
                }
            } else {
                if (idSelect == R.id.label1 || idSelect == R.id.label6) {
                    arrayList.clear()
                    arrayList.addAll(naidaunia)
                    arrayList.reverse()
                    bindingcontent.listView.smoothScrollToPosition(0)
                    adapter.notifyDataSetChanged()
                }
                bindingappbar.titleToolbar.text = nameRubrika
                bindingappbar.pageToolbar.text = ""
                bindingcontent.listView.visibility = View.VISIBLE
                pdfView.visibility = View.GONE
                bindingcontent.webView.visibility = View.GONE
                bindingcontent.scrollViewB.visibility = View.GONE
            }
            stopAutoScroll()
        } else {
            if (site) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onClick(view: View?) {
        stopAutoScroll()
        idSelect = view?.id ?: 0
        val permissionCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            1
        }
        var rub = -1
        if (dzenNoch) {
            binding.label1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            binding.label2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            binding.label3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            binding.label4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            binding.label5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        } else {
            binding.label1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.label2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.label3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.label4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.label5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
        }
        if (idSelect == R.id.label1) bindingcontent.listView.onItemLongClickListener = this
        else bindingcontent.listView.onItemLongClickListener = null
        when (idSelect) {
            R.id.label1 -> {
                bindingcontent.progressBar2.visibility = View.GONE
                arrayList.clear()
                arrayList.addAll(naidaunia)
                arrayList.reverse()
                adapter.notifyDataSetChanged()
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_niadaunia)
                bindingappbar.titleToolbar.text = nameRubrika
                bindingappbar.pageToolbar.text = ""
                if (dzenNoch) binding.label1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata)
                else binding.label1.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_gray)
            }
            R.id.label2 -> {
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy)
                bindingappbar.titleToolbar.text = nameRubrika
                bindingappbar.pageToolbar.text = ""
                rub = 1
                if (dzenNoch) binding.label2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata)
                else binding.label2.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_gray)
            }
            R.id.label3 -> {
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki)
                bindingappbar.titleToolbar.text = nameRubrika
                bindingappbar.pageToolbar.text = ""
                rub = 2
                if (dzenNoch) binding.label3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata)
                else binding.label3.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_gray)
            }
            R.id.label4 -> {
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki)
                bindingappbar.titleToolbar.text = nameRubrika
                bindingappbar.pageToolbar.text = ""
                rub = 3
                if (dzenNoch) binding.label4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata)
                else binding.label4.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_gray)
            }
            R.id.label5 -> {
                nameRubrika = getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura)
                bindingappbar.titleToolbar.text = nameRubrika
                bindingappbar.pageToolbar.text = ""
                rub = 4
                if (dzenNoch) binding.label5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata)
                else binding.label5.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_gray)
            }
            R.id.label6 -> {
                if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), myPermissionsWriteExternalStorage)
                    }
                } else {
                    bindingcontent.progressBar2.visibility = View.GONE
                    val fileExplorer = DialogFileExplorer()
                    fileExplorer.show(supportFragmentManager, "file_explorer")
                }
            }
        }
        if (saveindep) {
            bindingcontent.listView.visibility = View.VISIBLE
            pdfView.visibility = View.GONE
            bindingcontent.webView.visibility = View.GONE
            bindingcontent.scrollViewB.visibility = View.GONE
        }
        if (rub != -1 && bindingcontent.listView.visibility == View.VISIBLE) {
            val gson = Gson()
            val json: String = k.getString("Biblioteka", "") ?: ""
            val timeUpdate = Calendar.getInstance().timeInMillis
            val timeUpdateSave = k.getLong("BibliotekaTimeUpdate", timeUpdate)
            if (!(json == "" || timeUpdate - timeUpdateSave == 0L)) {
                if (timeUpdate - timeUpdateSave > (30 * 24 * 60 * 60 * 1000L)) {
                    if (MainActivity.isIntNetworkAvailable(this) == 1 || MainActivity.isIntNetworkAvailable(this) == 2) {
                        val prefEditors: SharedPreferences.Editor = k.edit()
                        prefEditors.putLong("BibliotekaTimeUpdate", timeUpdate)
                        prefEditors.apply()
                        getSql(rub)
                    } else {
                        arrayList.clear()
                        adapter.notifyDataSetChanged()
                        val noInternet = DialogNoInternet()
                        noInternet.show(supportFragmentManager, "no_internet")
                    }
                } else {
                    arrayList.clear()
                    val type: Type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
                    arrayList.addAll(gson.fromJson(json, type))
                    val temp: ArrayList<ArrayList<String>> = ArrayList()
                    for (i in 0 until arrayList.size) {
                        val rtemp2: Int = arrayList[i][4].toInt()
                        if (rtemp2 != rub) temp.add(arrayList[i])
                    }
                    arrayList.removeAll(temp)
                    adapter.notifyDataSetChanged()
                }
            } else {
                if (MainActivity.isIntNetworkAvailable(this) == 1 || MainActivity.isIntNetworkAvailable(this) == 2) {
                    val prefEditors: SharedPreferences.Editor = k.edit()
                    prefEditors.putLong("BibliotekaTimeUpdate", timeUpdate)
                    prefEditors.apply()
                    getSql(rub)
                } else {
                    arrayList.clear()
                    adapter.notifyDataSetChanged()
                    val noInternet = DialogNoInternet()
                    noInternet.show(supportFragmentManager, "no_internet")
                }
            }
        }
        invalidateOptionsMenu()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        saveindep = true
    }

    private fun getSql(rub: Int) {
        runSql = true
        arrayList.clear()
        adapter.notifyDataSetChanged()
        bindingcontent.progressBar2.visibility = View.VISIBLE
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val showUrl = "https://carkva-gazeta.by/biblioteka.php"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, showUrl, null, { response: JSONObject ->
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val temp: ArrayList<ArrayList<String>> = ArrayList()
                    val biblioteka = response.getJSONArray("biblioteka")
                    val gson = Gson()
                    for (i in 0 until biblioteka.length()) {
                        val mySqlList: ArrayList<String> = ArrayList()
                        val kniga = biblioteka.getJSONObject(i)
                        val id = kniga.getString("bib")
                        val rubrika = kniga.getString("rubryka")
                        val link = kniga.getString("link")
                        var str = kniga.getString("str")
                        val pdf = kniga.getString("pdf")
                        var image = kniga.getString("image")
                        mySqlList.add(link)
                        val pos = str.indexOf("</span><br>")
                        str = str.substring(pos + 11)
                        mySqlList.add(str)
                        mySqlList.add(pdf)
                        val url = URL("https://carkva-gazeta.by/data/bibliateka/$pdf")
                        var filesize: String
                        var conn: URLConnection?
                        conn = url.openConnection()
                        if (conn is HttpURLConnection) {
                            (conn as HttpURLConnection?)?.requestMethod = "HEAD"
                        }
                        filesize = java.lang.String.valueOf(conn.contentLength)
                        if (conn is HttpURLConnection) {
                            (conn as HttpURLConnection?)?.disconnect()
                        }
                        mySqlList.add(filesize)
                        mySqlList.add(rubrika)
                        val im1 = image.indexOf("src=\"")
                        val im2 = image.indexOf("\"", im1 + 5)
                        image = "https://carkva-gazeta.by" + image.substring(im1 + 5, im2)
                        val t1 = pdf.lastIndexOf(".") //image.lastIndexOf("/")
                        val imageLocal: String = "$filesDir/image_temp/" + pdf.substring(0, t1) + ".png" //image.substring(t1 + 1)
                        mySqlList.add(imageLocal)
                        mySqlList.add(id)
                        if (MainActivity.isIntNetworkAvailable(this@BibliotekaView) == 1 || MainActivity.isIntNetworkAvailable(this@BibliotekaView) == 2) {
                            val dir = File("$filesDir/image_temp")
                            if (!dir.exists()) dir.mkdir()
                            var mIcon11: Bitmap
                            val file = File(imageLocal)
                            if (!file.exists()) {
                                FileOutputStream("$filesDir/image_temp/" + pdf.substring(0, t1) + ".png").use { out ->
                                    val inputStream: InputStream = URL(image).openStream()
                                    mIcon11 = BitmapFactory.decodeStream(inputStream)
                                    mIcon11.compress(Bitmap.CompressFormat.PNG, 90, out)
                                }
                            }
                        }
                        if (rubrika.toInt() == rub) {
                            arrayList.add(mySqlList)
                        }
                        temp.add(mySqlList)
                    }
                    val json: String = gson.toJson(temp)
                    val prefEditors: SharedPreferences.Editor = k.edit()
                    prefEditors.putString("Biblioteka", json)
                    prefEditors.apply()
                    runSql = false
                }
                adapter.notifyDataSetChanged()
                bindingcontent.progressBar2.visibility = View.GONE
            }
        }, { })
        requestQueue.add(jsonObjectRequest)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    private fun hide() {
        supportActionBar?.hide()
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, uiAnimationDelaY)
    }

    @Suppress("DEPRECATION")
    private fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            val controller = window.insetsController
            controller?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            //controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, uiAnimationDelaY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val prefEditor: SharedPreferences.Editor = k.edit()
        if (pdfView.visibility == View.VISIBLE) prefEditor.putInt(fileName, pdfView.currentPage) else prefEditor.putInt(fileName, defaultPage)
        prefEditor.apply()
        outState.putBoolean("fullscreen", fullscreenPage)
        when {
            pdfView.visibility == View.VISIBLE -> {
                outState.putInt("page", pdfView.currentPage)
                outState.putInt("pdfView", 1)
            }
            bindingcontent.webView.visibility == View.VISIBLE -> {
                prefEditor.putInt("webViewBibliotekaScroll", positionY)
                prefEditor.apply()
                outState.putInt("page", defaultPage)
                outState.putInt("pdfView", 2)
            }
            else -> {
                outState.putInt("pdfView", 0)
            }
        }
        outState.putString("filePath", filePath)
        outState.putString("fileName", fileName)
        outState.putInt("idSelect", idSelect)
        outState.putString("nameRubrika", nameRubrika)
    }

    private fun unzip(zipFile: File, targetDirectory: File): Boolean {
        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
            var ze = ZipEntry("")
            val buffer = ByteArray(8192)
            while (zis.nextEntry?.also { ze = it } != null) {
                val file = File(targetDirectory, ze.name)
                val dir: File = if (ze.isDirectory) file else file.parentFile ?: file
                if (!dir.isDirectory && !dir.mkdirs()) return false
                if (ze.isDirectory) continue
                val canonicalPath = file.canonicalPath
                if (!canonicalPath.startsWith(targetDirectory.canonicalPath)) {
                    val securityError = DialogSecurityError()
                    securityError.show(supportFragmentManager, "securityError")
                    targetDirectory.deleteRecursively()
                    return false
                }
                FileOutputStream(file).use { fout ->
                    var count: Int
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                }
            }
        }
        return true
    }

    private fun stopAutoScroll() {
        bindingcontent.webView.setOnBottomListener(null)
        scrollTimer.cancel()
        scrollerSchedule = null
        if (!k.getBoolean("scrinOn", false)) {
            resetTimer = Timer()
            resetSchedule = object : TimerTask() {
                override fun run() {
                    CoroutineScope(Dispatchers.Main).launch { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
                }
            }
            resetTimer.schedule(resetSchedule, 60000)
        }
    }

    private fun startAutoScroll() {
        resetTimer.cancel()
        bindingcontent.webView.setOnBottomListener(this)
        scrollTimer = Timer()
        resetSchedule = null
        scrollerSchedule = object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!mActionDown && !binding.drawerLayout.isDrawerOpen(GravityCompat.START) && !MainActivity.dialogVisable) {
                        bindingcontent.webView.scrollBy(0, 2)
                    }
                }
            }
        }
        scrollTimer.schedule(scrollerSchedule, spid.toLong(), spid.toLong())
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = false
        }
    }

    private fun stopProcent() {
        procentTimer.cancel()
        procentSchedule = null
    }

    private fun startProcent() {
        stopProcent()
        procentTimer = Timer()
        procentSchedule = object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
                    bindingcontent.progress.visibility = View.GONE
                }
            }
        }
        procentTimer.schedule(procentSchedule, 1000)
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
            if (MainActivity.isIntNetworkAvailable(this) == 0) popup.menu.getItem(1).isVisible = false
        }
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            val spanString = SpannableString(popup.menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        if (arrayList[position].size < 7) popup.menu.getItem(3).isVisible = false
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
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/index.php?bib=${arrayList[position][6]}")
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, null))
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        popup.show()
    }

    internal inner class HelloWebViewClient : WebViewClient() {
        override fun onLoadResource(view: WebView, url: String) {
            view.visibility = View.GONE
            bindingcontent.progressBar2.visibility = View.VISIBLE
            super.onLoadResource(view, url)
        }

        override fun onPageFinished(view: WebView, url: String) {
            bindingcontent.progressBar2.visibility = View.GONE
            view.visibility = View.VISIBLE
            if (animationStoronaLeft) bindingcontent.webView.startAnimation(animInLeft) else bindingcontent.webView.startAnimation(animInRight)
            super.onPageFinished(view, url)
        }
    }

    internal inner class BibliotekaAdapter(context: Activity) : ArrayAdapter<ArrayList<String>>(context, R.layout.simple_list_item_biblioteka, arrayList) {
        private val k: SharedPreferences = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val activity: Activity = context
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            SplitCompat.install(activity)
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
            viewHolder.imageView.setBackgroundResource(R.drawable.frame_image_biblioteka)
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
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch)
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text.text = arrayList[position][0]
            return rootView
        }
    }

    private class ViewHolder(var text: TextViewRobotoCondensed, var imageView: ImageView, var buttonPopup: ImageView)
}
