package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.BiblijatekaPdfBinding
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.SimpleBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class BiblijatekaPdf : BaseActivity(), DialogSetPageBiblioteka.DialogSetPageBibliotekaListener, DialogBibliateka.DialogBibliatekaListener, PdfRendererView.StatusCallBack, DialogTitleBiblijatekaPdf.DialogTitleBibliotekaListener {

    private lateinit var binding: BiblijatekaPdfBinding
    private var fileName = ""
    private var fileTitle = ""
    private var uri: Uri? = null
    private var isPrint = false
    private var totalPage = 1
    private val dzenNoch get() = getBaseDzenNoch()
    private var resetTollbarJob: Job? = null
    private lateinit var arrayList: ArrayList<String>
    private val pdfTitleList = ArrayList<String>()

    override fun onPause() {
        super.onPause()
        val layoutManager = binding.pdfView.recyclerView.layoutManager as? LinearLayoutManager
        val page = layoutManager?.findFirstVisibleItemPosition() ?: 0
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val edit = k.edit()
        edit.putInt("Bibliateka_$fileName", page)
        edit.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BiblijatekaPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        uri = intent.data
        fileTitle = intent.extras?.getString("fileTitle", "") ?: ""
        arrayList = intent.extras?.getStringArrayList("list") ?: ArrayList()
        fileName = intent.extras?.getString("fileName", "") ?: ""
        isPrint = intent.extras?.getBoolean("isPrint", false) ?: false
        val c = Calendar.getInstance()
        val edit = k.edit()
        edit.putLong("BiblijatekaUseTime", c.timeInMillis)
        edit.apply()
        try {
            uri?.let {
                binding.pdfView.initWithUri(it)
                totalPage = binding.pdfView.totalPageCount
                val page = k.getInt("Bibliateka_$fileName", 0)
                onDialogSetPage(page + 1)
            }
        } catch (_: Throwable) {
            MainActivity.toastView(this, getString(R.string.error_ch))
        }
        setTollbarTheme()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                printBookmarksTree()
            }
            invalidateOptionsMenu()
        }
    }

    private fun printBookmarksTree() {
        uri?.let { uri ->
            val reader = PdfReader(contentResolver.openInputStream(uri))
            val bookmarks = SimpleBookmark.getBookmark(reader)
            bookmarks?.let {
                for (i in bookmarks.indices) {
                    showTitle(bookmarks[i])
                }
            }
            reader.close()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun showTitle(bm: HashMap<String, Any>) {
        val kids = bm["Kids"] as? List<HashMap<String, Any>>
        var page = bm["Page"].toString()
        val t1 = page.indexOf(" ")
        if (t1 != -1) page = page.substring(0, t1)
        pdfTitleList.add("$page<>${bm["Title"]}")
        if (kids != null) {
            for (i in kids.indices) {
                showTitle(kids[i])
            }
        }
    }

    override fun onPageChanged(currentPage: Int, totalPage: Int) {
        binding.pageToolbar.text = getString(R.string.pdfView_page_no, currentPage, totalPage)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.text = fileTitle
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
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

    override fun onDialogSetPage(page: Int) {
        binding.pdfView.recyclerView.smoothScrollToPosition(page - 1)
    }

    override fun onDialogbibliatekaPositiveClick(listPosition: String) {
    }

    override fun onBack() {
        setResult(Activity.RESULT_OK)
        super.onBack()
    }

    override fun onDialogTitle(page: Int) {
        onDialogSetPage(page)
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_apisane).isVisible = arrayList.size != 0
        menu.findItem(R.id.action_print).isVisible = isPrint
        menu.findItem(R.id.action_title).isVisible = pdfTitleList.isNotEmpty()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_title) {
            val titleBiblioteka = DialogTitleBiblijatekaPdf.getInstance(pdfTitleList)
            titleBiblioteka.show(supportFragmentManager, "title_biblioteka")
            return true
        }
        if (id == R.id.action_apisane) {
            val dialogBibliateka = DialogBibliateka.getInstance(arrayList, true)
            dialogBibliateka.show(supportFragmentManager, "dialog_bibliateka")
            return true
        }
        if (id == R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.set_log_file))
            sendIntent.type = "text/html"
            startActivity(Intent.createChooser(sendIntent, getString(R.string.set_log_file)))
            return true
        }
        if (id == R.id.action_open) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            startActivity(intent)
            return true
        }
        if (id == R.id.action_set_page) {
            val biblioteka = DialogSetPageBiblioteka.getInstance(totalPage)
            biblioteka.show(supportFragmentManager, "set_page_biblioteka")
            return true
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == R.id.action_print) {
            uri?.let {
                val printAdapter = PdfDocumentAdapter(this, fileName, it)
                val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                printManager.print(fileName, printAdapter, printAttributes)
            }
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.biblijateka_pdf, menu)
        super.onCreateMenu(menu, menuInflater)
    }
}