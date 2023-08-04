package by.carkva_gazeta.biblijateka

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import by.carkva_gazeta.biblijateka.databinding.BiblijatekaPdfBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogBrightness
import by.carkva_gazeta.malitounik.SettingsActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shockwave.pdfium.PdfDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class BiblijatekaPdf : BaseActivity(), OnPageChangeListener, OnLoadCompleteListener, OnErrorListener, DialogSetPageBiblioteka.DialogSetPageBibliotekaListener, DialogTitleBiblioteka.DialogTitleBibliotekaListener {

    private lateinit var pdfView: PDFView
    private lateinit var binding: BiblijatekaPdfBinding
    private lateinit var k: SharedPreferences
    private val bookTitle = ArrayList<String>()
    private var menu = false
    private var filePath = ""
    private var fileName = ""
    private var defaultPage = 0
    private val dzenNoch get() = getBaseDzenNoch()
    private var resetTollbarJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplitCompat.install(this)
        binding = BiblijatekaPdfBinding.inflate(layoutInflater)
        try {
            setContentView(binding.root)
        } catch (t: Resources.NotFoundException) {
            super.onBack()
            val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        pdfView = binding.pdfView
        if (savedInstanceState != null) {
            defaultPage = savedInstanceState.getInt("page")
            filePath = savedInstanceState.getString("filePath") ?: ""
            fileName = savedInstanceState.getString("fileName") ?: ""
        } else {
            filePath = intent.extras?.getString("filePath", "") ?: ""
            fileName = intent.extras?.getString("fileName", "") ?: ""
        }
        setTollbarTheme()
        loadFilePDF()
    }

    override fun onDialogTitle(page: Int) {
        pdfView.jumpTo(page)
        binding.pageToolbar.text = String.format("%d/%d", page + 1, pdfView.pageCount)
        binding.pageToolbar.visibility = View.VISIBLE
    }

    override fun onDialogTitleString() {
        binding.titleToolbar.visibility = View.GONE
    }

    override fun onDialogSetPage(page: Int) {
        if (pdfView.visibility == View.VISIBLE) {
            pdfView.jumpTo(page - 1)
            binding.pageToolbar.text = String.format("%d/%d", page, pdfView.pageCount)
        }
    }

    override fun onError(t: Throwable?) {
        val pdfError = DialogPdfError.getInstance("PDF")
        pdfError.show(supportFragmentManager, "pdf_error")
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        binding.pageToolbar.text = String.format("%d/%d", page + 1, pageCount)
        binding.pageToolbar.visibility = View.VISIBLE
    }

    override fun loadComplete(nbPages: Int) {
        val naidaunia = ArrayList<ArrayList<String>>()
        val gson = Gson()
        val json = k.getString("bibliateka_naidaunia", "")
        if (json != "") {
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            naidaunia.addAll(gson.fromJson(json, type))
        }
        bookTitle.clear()
        printBookmarksTree(pdfView.tableOfContents)
        var title = pdfView.documentMeta.title
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
            val temp = ArrayList<String>()
            temp.add(title)
            temp.add(filePath)
            val t2 = filePath.lastIndexOf("/")
            val img = filePath.substring(t2 + 1)
            val t1 = img.lastIndexOf(".")
            val image = img.substring(0, t1) + ".png"
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
        return false
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_title).isVisible = this.menu
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_inversion).isChecked = k.getBoolean("inversion", false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.biblijateka_pdf, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onPause() {
        super.onPause()
        val prefEditor = k.edit()
        if (pdfView.visibility == View.VISIBLE) {
            prefEditor.putInt(fileName, pdfView.currentPage)
        }
        prefEditor.apply()
    }

    override fun onBack() {
        val intent = Intent()
        intent.putExtra("title", pdfView.documentMeta.title)
        setResult(RESULT_OK, intent)
        super.onBack()
        val prefEditor = k.edit()
        prefEditor.putInt(fileName, pdfView.currentPage)
        prefEditor.apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val prefEditor = k.edit()
        prefEditor.putInt(fileName, pdfView.currentPage)
        prefEditor.apply()
        outState.putString("filePath", filePath)
        outState.putString("fileName", fileName)
    }
}