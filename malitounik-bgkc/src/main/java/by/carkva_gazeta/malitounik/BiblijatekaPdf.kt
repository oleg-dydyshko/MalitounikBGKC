package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.BiblijatekaPdfBinding
import by.carkva_gazeta.malitounik.databinding.BiblijatekaPdfItemBinding
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.SimpleBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class BiblijatekaPdf : BaseActivity(), DialogSetPageBiblioteka.DialogSetPageBibliotekaListener, DialogBibliateka.DialogBibliatekaListener, DialogTitleBiblijatekaPdf.DialogTitleBibliotekaListener {

    private lateinit var k: SharedPreferences
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
        val layoutManager = binding.pdfView.layoutManager as LinearLayoutManager
        val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val page = if (firstCompletelyVisiblePosition != RecyclerView.NO_POSITION) firstCompletelyVisiblePosition
        else layoutManager.findFirstVisibleItemPosition()
        if (isPrint) {
            val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val edit = k.edit()
            edit.putInt("Bibliateka_$fileName", page)
            edit.apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BiblijatekaPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        uri = intent.data
        fileTitle = intent.extras?.getString("fileTitle", "") ?: ""
        arrayList = intent.extras?.getStringArrayList("list") ?: ArrayList()
        fileName = intent.extras?.getString("fileName", "") ?: ""
        isPrint = intent.extras?.getBoolean("isPrint", false) ?: false
        val c = Calendar.getInstance()
        val edit = k.edit()
        edit.putLong("BiblijatekaUseTime", c.timeInMillis)
        edit.apply()
        uri?.let { uri ->
            val fileReader = contentResolver.openFileDescriptor(uri, "r")
            fileReader?.let {
                binding.pdfView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                val pdfRenderer = PdfRenderer(it)
                binding.pdfView.adapter = PdfAdapter(pdfRenderer)
                totalPage = pdfRenderer.pageCount
                val page = savedInstanceState?.getInt("scrollPosition") ?: k.getInt("Bibliateka_$fileName", 0)
                onDialogSetPage(page)
            }
        }
        binding.pdfView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var lastFirstVisiblePosition = RecyclerView.NO_POSITION
            private var lastCompletelyVisiblePosition = RecyclerView.NO_POSITION

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                val isPositionChanged = firstVisiblePosition != lastFirstVisiblePosition || firstCompletelyVisiblePosition != lastCompletelyVisiblePosition
                if (isPositionChanged) {
                    val positionToUse = if (firstCompletelyVisiblePosition != RecyclerView.NO_POSITION) {
                        firstCompletelyVisiblePosition
                    } else {
                        firstVisiblePosition
                    }
                    binding.pageToolbar.text = getString(R.string.pdfView_page_no, positionToUse + 1, totalPage)
                    lastFirstVisiblePosition = firstVisiblePosition
                    lastCompletelyVisiblePosition = firstCompletelyVisiblePosition
                }
            }
        })
        setTollbarTheme()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                printBookmarksTree()
            }
            invalidateOptionsMenu()
        }
    }

    private fun LinearLayoutManager.accurateScrollToPosition(position: Int) {
        this.scrollToPosition(position)
        this.postOnAnimation {
            val realPosition = this.findFirstCompletelyVisibleItemPosition()
            if (position != realPosition) {
                this.accurateScrollToPosition(position)
            } else {
                this.scrollToPosition(position)
            }
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
        (binding.pdfView.layoutManager as? LinearLayoutManager)?.accurateScrollToPosition(page)
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
        if (pdfTitleList.isNotEmpty()) {
            menu.findItem(R.id.action_title).isVisible = true
            menu.findItem(R.id.action_set_page).isVisible = false
        } else {
            menu.findItem(R.id.action_title).isVisible = false
            menu.findItem(R.id.action_set_page).isVisible = true
        }
        menu.findItem(R.id.action_apisane).isVisible = arrayList.size != 0
        menu.findItem(R.id.action_print).isVisible = isPrint
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutManager = binding.pdfView.layoutManager as LinearLayoutManager
        val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val page = if (firstCompletelyVisiblePosition != RecyclerView.NO_POSITION) firstCompletelyVisiblePosition
        else layoutManager.findFirstVisibleItemPosition()
        outState.putInt("scrollPosition", page)
    }

    private inner class PdfAdapter(val pdfRenderer: PdfRenderer) : RecyclerView.Adapter<PdfAdapter.MyViewHolder>() {

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image: ImageView = itemView.findViewById(R.id.image_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = BiblijatekaPdfItemBinding.inflate(layoutInflater, parent, false)
            return MyViewHolder(itemView.root)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                val width = binding.pdfView.width
                var bitmap: Bitmap? = null
                withContext(Dispatchers.IO) {
                    var page: PdfRenderer.Page? = null
                    try {
                        page = pdfRenderer.openPage(position)
                        val aspectRatio = page.width.toFloat() / page.height.toFloat()
                        val height = (width / aspectRatio).toInt()
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap?.let {
                            page.render(it, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        }
                    } catch (_: Throwable) {
                    } finally {
                        page?.close()
                    }
                }
                holder.image.setImageBitmap(bitmap)
            }
        }

        override fun getItemCount() = pdfRenderer.pageCount
    }
}