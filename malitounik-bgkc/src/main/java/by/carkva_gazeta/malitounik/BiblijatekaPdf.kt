package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
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
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.BiblijatekaPdfBinding
import by.carkva_gazeta.malitounik.databinding.BiblijatekaPdfItemBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.SimpleBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class BiblijatekaPdf : BaseActivity(), View.OnTouchListener, DialogSetPageBiblioteka.DialogSetPageBibliotekaListener, DialogBibliateka.DialogBibliatekaListener, DialogTitleBiblijatekaPdf.DialogTitleBibliotekaListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: BiblijatekaPdfBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private lateinit var adapter: PdfAdapter
    private var fileName = ""
    private var fileTitle = ""
    private var uri: Uri? = null
    private var isPrint = false
    private var totalPage = 1
    private val dzenNoch get() = getBaseDzenNoch()
    private var resetTollbarJob: Job? = null
    private lateinit var arrayList: ArrayList<String>
    private val pdfTitleList = ArrayList<String>()
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var resetScreenJob: Job? = null
    private var procentJobAuto: Job? = null
    private var spid = 60
    private var mActionDown = false
    private var autoscroll = false
    private var isEndList = false
    private var fullscreenPage = false

    override fun onPause() {
        super.onPause()
        val layoutManager = binding.pdfView.layoutManager as LinearLayoutManager
        val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val page = if (firstCompletelyVisiblePosition != RecyclerView.NO_POSITION) firstCompletelyVisiblePosition
        else layoutManager.findFirstVisibleItemPosition()
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditors = k.edit()
        if (isPrint) {
            prefEditors.putInt("Bibliateka_$fileName", page)
        }
        prefEditors.putBoolean("fullscreenPage", fullscreenPage)
        prefEditors.apply()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
    }

    override fun onResume() {
        super.onResume()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            autoStartScroll()
        }
        if (fullscreenPage) {
            binding.constraint.post {
                hide()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val id = v?.id ?: 0
        if (id == R.id.pdfView) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> mActionDown = true
                MotionEvent.ACTION_UP -> mActionDown = false
            }
            return false
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BiblijatekaPdfBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
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
                adapter = PdfAdapter(pdfRenderer)
                binding.pdfView.adapter = adapter
                totalPage = pdfRenderer.pageCount
                val page = savedInstanceState?.getInt("scrollPosition") ?: k.getInt("Bibliateka_$fileName", 0)
                onDialogSetPage(page)
            }
        }
        fullscreenPage = savedInstanceState?.getBoolean("fullscreen") ?: k.getBoolean("fullscreenPage", false)
        binding.pdfView.post {
            binding.pdfView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var lastFirstVisiblePosition = RecyclerView.NO_POSITION
                private var lastCompletelyVisiblePosition = RecyclerView.NO_POSITION

                override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
                    mActionDown = scrollState != RecyclerView.SCROLL_STATE_IDLE
                }

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
                    val findLastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    isEndList = findLastCompletelyVisibleItemPosition == adapter.itemCount
                    if (isEndList) {
                        autoscroll = false
                        stopAutoScroll()
                        invalidateOptionsMenu()
                    }
                }
            })
        }
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = resources.getString(R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = resources.getString(R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTOLEFT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.pdfView.setOnTouchListener(this)
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

    private fun startProcent(progressAction: Int) {
        if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT || progressAction == MainActivity.PROGRESSACTIONAUTORIGHT) {
            procentJobAuto?.cancel()
            bindingprogress.progressAuto.visibility = View.VISIBLE
            if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT) {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@BiblijatekaPdf, R.drawable.selector_progress_auto_left)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@BiblijatekaPdf, R.color.colorPrimary_text))
            } else {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@BiblijatekaPdf, R.drawable.selector_progress_red)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@BiblijatekaPdf, R.color.colorWhite))
            }
            procentJobAuto = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressAuto.visibility = View.GONE
            }
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        autoStartScrollJob?.cancel()
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
            }
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            autoScrollJob?.cancel()
            if (delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (!isEndList) {
            spid = k.getInt("autoscrollSpid", 60)
            if (binding.actionMinus.visibility == View.GONE) {
                binding.actionMinus.visibility = View.VISIBLE
                binding.actionPlus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
                binding.actionMinus.animation = animation
                binding.actionPlus.animation = animation
            }
            resetScreenJob?.cancel()
            autoStartScrollJob?.cancel()
            autoScroll()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                onDialogSetPage(0)
                autoStartScroll()
            }
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            autoscroll = true
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            invalidateOptionsMenu()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        binding.pdfView.scrollBy(0, 2)
                    }
                }
            }
        }
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            if (spid < 166) {
                val autoTime = (230 - spid) / 10
                var count = 0
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        spid = 230
                        autoScroll()
                        while (true) {
                            delay(1000L)
                            if (!mActionDown && !MainActivity.dialogVisable) {
                                spid -= autoTime
                                count++
                            }
                            if (count == 10) {
                                break
                            }
                        }
                        startAutoScroll()
                    }
                }
            } else {
                startAutoScroll()
            }
        }
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
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(R.id.action_auto)
        when {
            autoscroll -> itemAuto.setIcon(R.drawable.scroll_icon_on)
            isEndList -> itemAuto.setIcon(R.drawable.scroll_icon_up)
            else -> itemAuto.setIcon(R.drawable.scroll_icon)
        }
        val spanString = SpannableString(itemAuto.title.toString())
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_fullscreen) {
            hide()
            return true
        }
        if (id == R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscrollAutostart", !autoscroll)
            prefEditor.apply()
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
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

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.VISIBLE
        binding.actionBack.animation = animation
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
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
        outState.putBoolean("fullscreen", fullscreenPage)
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
                    synchronized(this@PdfAdapter) {
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
                }
                holder.image.setImageBitmap(bitmap)
            }
        }

        override fun getItemCount() = pdfRenderer.pageCount
    }
}