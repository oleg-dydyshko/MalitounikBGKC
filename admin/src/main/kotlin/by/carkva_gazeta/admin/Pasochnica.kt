package by.carkva_gazeta.admin

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminPasochnicaBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.EditTextCustom
import by.carkva_gazeta.malitounik.InteractiveScrollView
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.commons.text.StringEscapeUtils
import java.io.File


class Pasochnica : BaseActivity(), View.OnClickListener, DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogSaveAsFileExplorer.DialogSaveAsFileExplorerListener, DialogFileExists.DialogFileExistsListener, DialogPasochnicaMkDir.DialogPasochnicaMkDirListener, DialogAddPesny.DialogAddPesnyListiner, InteractiveScrollView.OnInteractiveScrollChangedCallback, DialogPasochnicaAHref.DialogPasochnicaAHrefListener, DialogIsHtml.DialogIsHtmlListener, DialogFileNameError.DialogFileNameErrorListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaBinding
    private var resetTollbarJob: Job? = null
    private var fileName = "new_file.html"
    private var resours = ""
    private var history = ArrayList<History>()
    private var positionY = 0
    private var firstTextPosition = ""
    private var isHTML = true
    private var findPosition = 0
    private val findListSpans = ArrayList<SpanStr>()
    private var animatopRun = false
    private val textWatcher = object : TextWatcher {
        private var editPosition = 0

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable?) {
            addHistory(s, editPosition)
            if (history.size > 1) {
                binding.actionBack.visibility = View.VISIBLE
            } else {
                binding.actionBack.visibility = View.GONE
            }
        }
    }

    override fun renameFileName() {
        val dialogPasochnicaFileName = supportFragmentManager.findFragmentByTag("dialogPasochnicaFileName") as? DialogPasochnicaFileName
        dialogPasochnicaFileName?.vypraulenneFilename()
        val dialogSaveAsFileExplorer = supportFragmentManager.findFragmentByTag("dialogSaveAsFileExplorer") as? DialogSaveAsFileExplorer
        dialogSaveAsFileExplorer?.vypraulenneFilename()
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    private fun findAllAsanc(noNext: Boolean = true) {
        CoroutineScope(Dispatchers.Main).launch {
            findRemoveSpan()
            findAll()
            findCheckPosition()
            if (noNext) findNext(false)
        }
    }

    private fun findAll() {
        var position = 0
        val search = binding.textSearch.text.toString()
        if (search.length >= 3) {
            val text = binding.apisanne.text as SpannableStringBuilder
            val searchLig = search.length
            var run = true
            while (run) {
                val strPosition = text.indexOf(search, position, true)
                if (strPosition != -1) {
                    findListSpans.add(SpanStr(getColorSpans(text.getSpans(strPosition, strPosition + searchLig, ForegroundColorSpan::class.java)), strPosition, strPosition + searchLig))
                    text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    position = strPosition + 1
                } else {
                    run = false
                }
            }
        }
    }

    private fun findCheckPosition() {
        if (findListSpans.isNotEmpty()) {
            binding.apisanne.layout?.let { layout ->
                val lineForVertical = layout.getLineForVertical(positionY)
                for (i in 0 until findListSpans.size) {
                    if (lineForVertical <= layout.getLineForOffset(findListSpans[i].start)) {
                        findPosition = i
                        break
                    }
                }
            }
        } else {
            findPosition = 0
            binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.niama)
        }
    }

    private fun findRemoveSpan() {
        val text = binding.apisanne.text as SpannableStringBuilder
        if (findListSpans.isNotEmpty()) {
            findListSpans.forEach {
                text.setSpan(ForegroundColorSpan(it.color), it.start, it.size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (findListSpans.size >= findPosition) findPosition = 0
            findListSpans.clear()
        }
        val spans = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
        spans.forEach {
            text.removeSpan(it)
        }
    }

    private fun findNext(next: Boolean = true, previous: Boolean = false) {
        val findPositionOld = findPosition
        if (next) {
            if (previous) findPosition--
            else findPosition++
        }
        if (findListSpans.isNotEmpty()) {
            if (findPosition == -1) {
                findPosition = findListSpans.size - 1
            }
            if (findPosition == findListSpans.size) {
                findPosition = 0
            }
            val text = binding.apisanne.text as SpannableStringBuilder
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPositionOld].start, findListSpans[findPositionOld].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, findPosition + 1, findListSpans.size)
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta2)), findListSpans[findPosition].start, findListSpans[findPosition].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.apisanne.layout?.let { layout ->
                val line = layout.getLineForOffset(findListSpans[findPosition].start)
                val y = layout.getLineTop(line)
                val anim = ObjectAnimator.ofInt(binding.scrollView, "scrollY", binding.scrollView.scrollY, y)
                anim.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        animatopRun = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        animatopRun = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })
                anim.setDuration(1000).start()
            }
        }
    }

    private fun getColorSpans(colorSpan: Array<out ForegroundColorSpan>): Int {
        var color = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
        if (colorSpan.isNotEmpty()) {
            color = colorSpan[colorSpan.size - 1].foregroundColor
        }
        return color
    }

    override fun onScroll(t: Int, oldt: Int) {
        positionY = t
        binding.apisanne.layout?.let { layout ->
            val textForVertical = binding.apisanne.text.toString().substring(layout.getLineStart(layout.getLineForVertical(positionY)), layout.getLineEnd(layout.getLineForVertical(positionY))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
            if (binding.find.visibility == View.VISIBLE && !animatopRun) {
                if (findListSpans.isNotEmpty()) {
                    val text = binding.apisanne.text as SpannableStringBuilder
                    for (i in 0 until findListSpans.size) {
                        if (layout.getLineForOffset(findListSpans[i].start) == layout.getLineForVertical(positionY)) {
                            var ii = i + 1
                            if (i == 0) ii = 1
                            findPosition = i
                            var findPositionOld = if (t >= oldt) i - 1
                            else i + 1
                            if (findPositionOld == -1) findPositionOld = findListSpans.size - 1
                            if (findPositionOld == findListSpans.size) findPositionOld = 0
                            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPositionOld].start, findListSpans[findPositionOld].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (findPosition != ii) binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, ii, findListSpans.size)
                            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta2)), findListSpans[i].start, findListSpans[i].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            break
                        }
                    }
                }
            }
        }
    }

    override fun setUrl(url: String, titleUrl: String) {
        val startSelect = binding.apisanne.selectionStart
        val endSelect = binding.apisanne.selectionEnd
        if (isHTML) {
            val text = SpannableStringBuilder(binding.apisanne.text)
            val subtext = text.getSpans(startSelect, endSelect, URLSpan::class.java)
            subtext.forEach {
                if (it.url.contains(url)) {
                    text.removeSpan(it)
                }
            }
            text.setSpan(URLSpan(url), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.append(text.substring(0, startSelect))
            text.append(titleUrl)
            text.append(text.substring(endSelect))
            binding.apisanne.text = text
        } else {
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<a href=\"$url\">")
                append(titleUrl)
                append("</a>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 29)
        }
        addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
    }

    private fun addHistory(s: Editable?, editPosition: Int) {
        if (isHTML) {
            s?.let {
                if (it.toString() != "") {
                    if (history.size == 51) history.removeAt(0)
                    history.add(History(it.toSpannable(), editPosition))
                }
                if (history.size > 1) {
                    binding.actionBack.visibility = View.VISIBLE
                } else {
                    binding.actionBack.visibility = View.GONE
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        val prefEditor = k.edit()
        prefEditor.putInt("admin" + fileName + "position", positionY)
        prefEditor.apply()
    }

    private fun setResoursName() {
        val t1 = fileName.indexOf("(")
        if (t1 != -1) {
            val t2 = fileName.indexOf(")")
            resours = fileName.substring(1, t2)
        }
    }

    private fun findResoursNameAndTitle(): String {
        val t3 = fileName.lastIndexOf(".")
        var title = if (t3 != -1) {
            var findResours = false
            for (i in 0 until PasochnicaList.findDirAsSave.size) {
                if (fileName.substring(0, t3) == PasochnicaList.findDirAsSave[i]) {
                    findResours = true
                    break
                }
            }
            if (findResours) {
                resours = fileName.substring(0, t3)
                fileName.substring(0, t3)
            } else fileName
        } else fileName
        val t1 = fileName.indexOf("(")
        if (t1 != -1) {
            val t2 = fileName.indexOf(")")
            resours = fileName.substring(1, t2)
            title = if (t3 != -1) fileName.substring(t2 + 1, t3)
            else fileName.substring(t2 + 1)
        }
        return title.trim()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        binding = AdminPasochnicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apisanne.addTextChangedListener(textWatcher)
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.actionA.setOnClickListener(this)
        binding.actionBr.setOnClickListener(this)
        binding.actionBack.setOnClickListener(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.actionKeyword.visibility = View.GONE
        } else {
            binding.apisanne.showSoftInputOnFocus = false
        }
        binding.actionKeyword.setOnClickListener(this)
        binding.scrollView.setOnScrollChangedCallback(this)
        fileName = intent.extras?.getString("fileName", "") ?: "new_file.html"
        resours = intent.extras?.getString("resours", "") ?: ""
        var title = intent.extras?.getString("title", "") ?: ""
        val isPasochnica = intent.extras?.getBoolean("isPasochnica", false) ?: false
        val text = intent.extras?.getString("text", "") ?: ""
        if (!isPasochnica) {
            if (resours == "" && title == "") {
                title = findResoursNameAndTitle()
            }
            fileName = if (resours == "") {
                title
            } else {
                "($resours) $title"
            }
        } else {
            setResoursName()
        }
        isHTML = text.contains("<!DOCTYPE HTML>", ignoreCase = true)
        if (savedInstanceState != null) {
            isHTML = savedInstanceState.getBoolean("isHTML", true)
            fileName = savedInstanceState.getString("fileName", "")
            resours = savedInstanceState.getString("resours", "")
            if (savedInstanceState.getBoolean("seach")) {
                binding.find.visibility = View.VISIBLE
            }
            history.clear()
            binding.apisanne.post {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    binding.apisanne.layout?.let { layout ->
                        val index = binding.apisanne.text.toString().indexOf(textline)
                        val line = layout.getLineForOffset(index)
                        val y = layout.getLineTop(line)
                        binding.scrollView.smoothScrollBy(0, y)
                    }
                } else {
                    binding.scrollView.smoothScrollBy(0, positionY)
                }
            }
        } else {
            val newFile = intent.extras?.getBoolean("newFile", false) ?: false
            when {
                intent.extras?.getBoolean("backcopy", false) == true -> {
                    if (isHTML) {
                        binding.apisanne.setText(MainActivity.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
                        binding.actionP.visibility = View.GONE
                        binding.actionBr.visibility = View.GONE
                    } else {
                        binding.apisanne.setText(text)
                    }
                }

                !newFile -> {
                    getOrSendFilePostRequest(text, saveAs = false, isSaveAs = false)
                }

                else -> {
                    binding.actionP.visibility = View.VISIBLE
                    binding.actionBr.visibility = View.VISIBLE
                    intent.removeExtra("newFile")
                }
            }
        }
        positionY = k.getInt("admin" + fileName + "position", 0)
        binding.textSearch.addTextChangedListener(object : TextWatcher {
            var editPosition = 0
            var check = 0
            var editch = true

            override fun afterTextChanged(s: Editable?) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                edit = edit.replace("И", "І")
                if (editch) {
                    if (check != 0) {
                        binding.textSearch.removeTextChangedListener(this)
                        binding.textSearch.setText(edit)
                        binding.textSearch.setSelection(editPosition)
                        binding.textSearch.addTextChangedListener(this)
                    }
                }
                if (edit.length >= 3) {
                    findAllAsanc()
                } else {
                    findRemoveSpan()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                editch = count != after
                check = after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editPosition = start + count
            }
        })
        binding.imageView6.setOnClickListener { findNext(previous = true) }
        binding.imageView5.setOnClickListener { findNext() }
        setTollbarTheme()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isHTML", isHTML)
        outState.putString("fileName", fileName)
        outState.putString("textLine", firstTextPosition)
        outState.putString("resours", resours)
        if (binding.find.visibility == View.VISIBLE) outState.putBoolean("seach", true)
        else outState.putBoolean("seach", false)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.pasochnica)
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
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
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

    override fun onResume() {
        super.onResume()
        findDirAsSave()
        setTollbarTheme()
    }

    override fun onBack() {
        if (binding.find.visibility == View.VISIBLE) {
            binding.find.visibility = View.GONE
            binding.textSearch.setText("")
            findRemoveSpan()
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
        } else {
            onSupportNavigateUp()
        }
    }

    override fun onDialogSaveAsFile(dir: String, oldFileName: String, fileName: String) {
        getFileIssetPostRequest(dir, oldFileName, fileName)
    }

    override fun setFileName(oldFileName: String, fileName: String, isSite: Boolean, saveAs: Boolean) {
        this.fileName = fileName
        saveResult(saveAs)
    }

    override fun fileExists(dir: String, oldFileName: String, fileName: String, saveAs: Boolean) {
        if (saveAs) sendSaveAsPostRequest("$dir/$fileName", oldFileName)
    }

    override fun setDir() {
        val dialogSaveAsFileExplorer = supportFragmentManager.findFragmentByTag("dialogSaveAsFileExplorer") as? DialogSaveAsFileExplorer
        dialogSaveAsFileExplorer?.dismiss()
        Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
            setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
            setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
            setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            show()
        }
    }

    override fun addPesny(title: String, pesny: String, fileName: String) {
        sendSaveAsAddNewPesnyPostRequest(title, pesny, fileName)
    }

    private fun sendSaveAsAddNewPesnyPostRequest(title: String, pesny: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val localFile2 = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val string = StringBuilder()
                    val t1 = fileName.indexOf(".")
                    val nawFileName = if (t1 != -1) fileName.substring(0, t1)
                    else fileName
                    Malitounik.referens.child("/admin/pesny/pesny_menu.txt").getFile(localFile).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var onRun = false
                            localFile.readLines().forEach {
                                if (it.indexOf(pesny, ignoreCase = true) != -1) {
                                    onRun = true
                                    string.append("$it\n")
                                } else {
                                    if (onRun) {
                                        string.append("$pesny$nawFileName<>$title\n")
                                        onRun = false
                                    }
                                    string.append("$it\n")
                                }
                            }
                        } else {
                            MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                    Malitounik.referens.child("/admin/piasochnica/$fileName").getFile(localFile2).addOnFailureListener {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    Malitounik.referens.child("/admin/pesny/$pesny$fileName").putFile(Uri.fromFile(localFile2)).await()
                    Malitounik.referens.child("/admin/piasochnica/($pesny$nawFileName) $title").putFile(Uri.fromFile(localFile2)).await()
                    Malitounik.referens.child("/admin/piasochnica/$fileName").delete().await()
                    localFile.writer().use {
                        it.write(string.toString())
                    }
                    Malitounik.referens.child("/admin/pesny/pesny_menu.txt").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        } else {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        }
                    }.await()
                    localFile.delete()
                    localFile2.delete()
                    PasochnicaList.getFindFileListAsSave()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun getFileIssetPostRequest(dir: String, oldFileName: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    Malitounik.referens.child("/$dir/" + fileName.replace("\n", " ")).downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val dialogFileExists = DialogFileExists.getInstance(dir, oldFileName, fileName, true)
                            dialogFileExists.show(supportFragmentManager, "dialogFileExists")
                        } else {
                            sendSaveAsPostRequest("$dir/$fileName", oldFileName)
                        }
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private fun sendSaveAsPostRequest(dirToFile: String, fileName: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val logFile = withContext(Dispatchers.IO) {
                        File.createTempFile("piasochnica", "json")
                    }
                    val sb = StringBuilder()
                    val url = "/$dirToFile"
                    Malitounik.referens.child("/admin/log.txt").getFile(logFile).addOnFailureListener {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    var ref = true
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
                    Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).await()

                    Malitounik.referens.child("/admin/piasochnica/" + fileName.replace("\n", " ")).getFile(localFile).addOnFailureListener {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    val t3 = dirToFile.lastIndexOf("/")
                    var newFile = dirToFile.substring(t3 + 1)
                    val newDir = dirToFile.substring(0, t3 + 1)
                    newFile = newFile.replace("-", "_")
                    newFile = newFile.replace(" ", "_").lowercase()
                    if (newFile[0].isDigit()) newFile = "mm_$newFile"
                    Malitounik.referens.child("/$newDir$newFile").putFile(Uri.fromFile(localFile)).await()
                    Malitounik.referens.child("/admin/piasochnica/" + fileName.replace("\n", " ")).delete().await()
                    val t6 = newFile.lastIndexOf(".")
                    if (t6 != -1) {
                        this@Pasochnica.fileName = "(" + newFile.substring(0, t6) + ") " + newFile
                        resours = newFile.substring(0, t6)
                        PasochnicaList.findDirAsSave.add("/$newDir$newFile")
                    }
                    var oldFile = ""
                    var title = ""
                    if (fileName.indexOf("(") == -1) {
                        val t1 = dirToFile.lastIndexOf("/")
                        oldFile = "(" + dirToFile.substring(t1 + 1) + ") "
                        val t2 = oldFile.lastIndexOf(".")
                        if (t2 != -1) {
                            oldFile = oldFile.substring(0, t2) + ") "
                        }
                        if (fileName.contains(".html")) {
                            val rt = localFile.readText()
                            val t4 = rt.indexOf("<strong>")
                            if (t4 != -1) {
                                val t5 = rt.indexOf("</strong>")
                                title = rt.substring(t4 + 8, t5).trim()
                            }
                        }
                    }
                    val tv = if (title != "") MainActivity.fromHtml(title).toString()
                    else fileName.replace("\n", " ")
                    Malitounik.referens.child("/admin/piasochnica/$oldFile$tv").putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        } else {
                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                show()
                            }
                        }
                    }.await()
                    localFile.delete()
                    logFile.delete()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
                invalidateOptionsMenu()
            }
        }
    }

    private fun getOrSendFilePostRequest(content: String, saveAs: Boolean, isSaveAs: Boolean = true) {
        if (isSaveAs) {
            val dir = getExternalFilesDir("PiasochnicaBackCopy")
            dir?.let {
                if (!dir.exists()) dir.mkdir()
            }
            val file = File(getExternalFilesDir("PiasochnicaBackCopy"), fileName)
            file.writer().use {
                it.write(content)
            }
        }
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var result = ""
                binding.progressBar2.visibility = View.VISIBLE
                val isSite = intent.extras?.getBoolean("isSite", false) ?: false
                if (isSite) {
                    intent.removeExtra("isSite")
                    try {
                        val localFile = withContext(Dispatchers.IO) {
                            File.createTempFile("piasochnica", "json")
                        }
                        Malitounik.referens.child("/admin/piasochnica/$fileName").getFile(localFile).addOnFailureListener {
                            MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }.await()
                        result = localFile.readText()
                        localFile.delete()
                    } catch (e: Throwable) {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                } else {
                    try {
                        val localFile = withContext(Dispatchers.IO) {
                            File.createTempFile("piasochnica", "json")
                        }
                        result = getTextOnSite(resours)
                        if (!isSaveAs) {
                            if (result == "") result = content
                        } else result = content
                        result = result.replace(" ", " ")
                        localFile.writer().use {
                            it.write(result)
                        }
                        Malitounik.referens.child("/admin/piasochnica/" + fileName.replace("\n", " ")).putFile(Uri.fromFile(localFile)).addOnCompleteListener {
                            if (it.isSuccessful) {
                                PasochnicaList.getFindFileListAsSave()
                                if (isSaveAs) {
                                    if (saveAs) {
                                        if (!findDirAsSave()) {
                                            val dialogSaveAsFileExplorer = DialogSaveAsFileExplorer.getInstance(fileName)
                                            dialogSaveAsFileExplorer.show(supportFragmentManager, "dialogSaveAsFileExplorer")
                                            Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.save), Snackbar.LENGTH_LONG).apply {
                                                setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                                setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                                setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                                show()
                                            }
                                        } else {
                                            sendSaveAsPostRequest(getDirAsSave(), fileName)
                                        }
                                    }
                                }
                            } else {
                                Snackbar.make(binding.scrollView, getString(by.carkva_gazeta.malitounik.R.string.error), Snackbar.LENGTH_LONG).apply {
                                    setActionTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                    setTextColor(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorWhite))
                                    setBackgroundTint(ContextCompat.getColor(this@Pasochnica, by.carkva_gazeta.malitounik.R.color.colorPrimary))
                                    show()
                                }
                            }
                        }.await()
                        localFile.delete()
                    } catch (e: Throwable) {
                        MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
                isHTML = result.contains("<!DOCTYPE HTML>", ignoreCase = true)
                if (isHTML) {
                    binding.apisanne.setText(MainActivity.fromHtml(result, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    binding.actionP.visibility = View.GONE
                    binding.actionBr.visibility = View.GONE
                } else {
                    binding.actionP.visibility = View.VISIBLE
                    binding.actionBr.visibility = View.VISIBLE
                    binding.apisanne.setText(result)
                }
                binding.progressBar2.visibility = View.GONE
                invalidateOptionsMenu()
            }
        } else {
            isHTML = content.contains("<!DOCTYPE HTML>", ignoreCase = true)
            if (isHTML) {
                binding.apisanne.setText(MainActivity.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT))
                binding.actionP.visibility = View.GONE
                binding.actionBr.visibility = View.GONE
            } else {
                binding.apisanne.setText(content)
            }
        }
    }

    private suspend fun getTextOnSite(fileName: String): String {
        var text = ""
        try {
            val localFile = withContext(Dispatchers.IO) {
                File.createTempFile("piasochnica", "json")
            }
            val result = PasochnicaList.findDirAsSave
            for (i in 0 until result.size) {
                val t1 = result[i].lastIndexOf("/")
                var t2 = result[i].lastIndexOf(".")
                if (t2 == -1) t2 = result[i].length
                if (result[i].substring(t1 + 1, t2) == fileName) {
                    Malitounik.referens.child("/" + result[i]).getFile(localFile).addOnCompleteListener {
                        if (it.isSuccessful) text = localFile.readText()
                        else MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    break
                }
            }
            localFile.delete()
        } catch (e: Throwable) {
            text = ""
            MainActivity.toastView(this@Pasochnica, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
        }
        return text
    }

    private fun findDirAsSave(): Boolean {
        var result = false
        if (resours != "") {
            for (i in 0 until PasochnicaList.findDirAsSave.size) {
                if (PasochnicaList.findDirAsSave[i].contains(resours)) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    private fun getDirAsSave(): String {
        var result = ""
        if (resours != "") {
            for (i in 0 until PasochnicaList.findDirAsSave.size) {
                if (PasochnicaList.findDirAsSave[i].contains(resours)) {
                    result = PasochnicaList.findDirAsSave[i]
                    break
                }
            }
        }
        return result
    }

    private fun clearColor(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<font color=\"#d00505\">", position)
            val t2 = result.indexOf("</font>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 22, t2)
                val oldSubText = result.substring(t1, t2 + 7)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</font>", position)
            val t2 = result.indexOf("<font color=\"#d00505\">", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 7, t2)
                val oldSubText = result.substring(t1, t2 + 22)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<font color=\"#d00505\">", "")
                    oldSubText2 = oldSubText2.replace("</font>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearBold(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<strong>", position)
            val t2 = result.indexOf("</strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 8, t2)
                val oldSubText = result.substring(t1, t2 + 9)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</strong>", position)
            val t2 = result.indexOf("<strong>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 9, t2)
                val oldSubText = result.substring(t1, t2 + 8)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<strong>", "")
                    oldSubText2 = oldSubText2.replace("</strong>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearEm(text: String): String {
        var result = text
        var run = true
        var position = 0
        while (run) {
            val t1 = result.indexOf("<em>", position)
            val t2 = result.indexOf("</em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 4, t2)
                val oldSubText = result.substring(t1, t2 + 5)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        run = true
        position = 0
        while (run) {
            val t1 = result.indexOf("</em>", position)
            val t2 = result.indexOf("<em>", t1)
            if (t1 != -1 && t2 != -1) {
                var subText = result.substring(t1 + 5, t2)
                val oldSubText = result.substring(t1, t2 + 4)
                subText = subText.replace("\n", "")
                subText = subText.replace("<br>", "")
                subText = subText.replace("<p>", "").trim()
                if (subText.isEmpty()) {
                    var oldSubText2 = oldSubText.replace("<em>", "")
                    oldSubText2 = oldSubText2.replace("</em>", "")
                    result = result.replace(oldSubText, oldSubText2)
                }
            } else {
                run = false
            }
            position = t1 + 1
        }
        return result
    }

    private fun clearHtml(text: String): String {
        var result = text
        val t1 = result.indexOf("<p")
        if (t1 != -1) {
            val t2 = result.indexOf(">")
            val subString = result.substring(t1, t2 + 1)
            var stringres = result.replace(subString, "")
            stringres = stringres.replace("</p>", "<br>")
            stringres = stringres.replace("<span", "<font")
            stringres = stringres.replace("</span>", "</font>")
            stringres = stringres.replace("style=\"color:#D00505;\"", "color=\"#d00505\"")
            stringres = stringres.replace("<i>", "<em>")
            stringres = stringres.replace("</i>", "</em>")
            stringres = stringres.replace("<b>", "<strong>")
            stringres = stringres.replace("</b>", "</strong>")
            stringres = stringres.replace("<u>", "")
            stringres = stringres.replace("</u>", "")
            val t3 = stringres.lastIndexOf("<br>")
            result = stringres.substring(0, t3)
        }
        return result
    }

    override fun onPrepareMenu(menu: Menu) {
        if (isHTML) {
            menu.findItem(R.id.action_convert).isVisible = false
        } else {
            menu.findItem(R.id.action_preview).isVisible = false
        }
        if (resours != "") menu.findItem(R.id.action_save).isVisible = false
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_find) {
            binding.find.visibility = View.VISIBLE
            binding.textSearch.requestFocus()
            EditTextCustom.focusAndShowKeyboard(binding.textSearch)
            return true
        }
        if (id == R.id.action_preview) {
            binding.apisanne.removeTextChangedListener(textWatcher)
            isHTML = !isHTML
            convertView(binding.apisanne.text)
            binding.apisanne.addTextChangedListener(textWatcher)
            return true
        }
        if (id == R.id.action_convert) {
            binding.apisanne.removeTextChangedListener(textWatcher)
            convertToHtml()
            binding.apisanne.addTextChangedListener(textWatcher)
            return true
        }
        if (id == R.id.action_save) {
            saveAs(false)
            return true
        }
        if (id == R.id.action_save_as) {
            saveAs(true)
            return true
        }
        return false
    }

    private fun saveAs(saveAs: Boolean) {
        val text = binding.apisanne.text.toString()
        if (text.contains("<em>") || text.contains("<strong>") || text.contains("<br>") || text.contains("<font")) {
            val dialog = DialogIsHtml.getInstance(saveAs)
            dialog.show(supportFragmentManager, "DialogIsHtml")
        } else {
            pasochnica(false, saveAs)
        }
    }

    override fun pasochnica(isHtml: Boolean, saveAs: Boolean) {
        if (isHtml) {
            convertToHtml()
        }
        binding.apisanne.removeTextChangedListener(textWatcher)
        if (fileName == "new_file.html") {
            val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance("new_file.html", false, saveAs)
            dialogPasochnicaFileName.show(supportFragmentManager, "dialogPasochnicaFileName")
        } else {
            saveResult(saveAs)
        }
        binding.apisanne.addTextChangedListener(textWatcher)
    }

    private fun convertToHtml() {
        val text = binding.apisanne.text.toString()
        val listText = text.split("\n")
        val result = SpannableStringBuilder()
        result.append("<!DOCTYPE HTML>")
        listText.forEach {
            val string = it.trim()
            val res = if (it.length >= 4) string.substring(it.length - 4)
            else string
            val isBR = res.contains("<br>")
            if (isBR) {
                result.append("$it\n")
            } else {
                result.append("$it<br>\n")
            }
        }
        isHTML = true
        convertView(result)
        invalidateOptionsMenu()
    }

    private fun convertView(text: Editable?) {
        if (isHTML) {
            binding.apisanne.setText(MainActivity.fromHtml(text.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT))
            binding.actionP.visibility = View.GONE
            binding.actionBr.visibility = View.GONE
            if (history.size > 1) {
                binding.actionBack.visibility = View.VISIBLE
            }
        } else {
            text?.let {
                var result = MainActivity.toHtml(it)
                result = StringEscapeUtils.unescapeHtml4(result)
                result = clearHtml(result)
                result = clearColor(result)
                result = clearEm(result)
                result = clearBold(result)
                result = clearEm(result)
                result = clearColor(result)
                result = clearBold(result)
                result = clearEm(result)
                binding.apisanne.setText(result)
                binding.actionP.visibility = View.VISIBLE
                binding.actionBr.visibility = View.VISIBLE
                binding.actionBack.visibility = View.GONE
            }
        }
    }

    private fun saveResult(saveAs: Boolean) {
        val text = binding.apisanne.text
        if (isHTML) {
            text?.let {
                var result = MainActivity.toHtml(it)
                result = StringEscapeUtils.unescapeHtml4(result)
                result = clearHtml(result)
                result = clearColor(result)
                result = clearEm(result)
                result = clearBold(result)
                result = clearEm(result)
                result = clearColor(result)
                result = clearBold(result)
                result = clearEm(result)
                if (!result.contains("<!DOCTYPE HTML>")) result = "<!DOCTYPE HTML>$result"
                getOrSendFilePostRequest(result, saveAs)
            }
        } else {
            getOrSendFilePostRequest(text.toString(), saveAs)
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.action_back) {
            binding.apisanne.removeTextChangedListener(textWatcher)
            if (history.size > 1) {
                binding.apisanne.setText(history[history.size - 2].spannable)
                binding.apisanne.setSelection(history[history.size - 2].editPosition)
                history.removeAt(history.size - 1)
            }
            if (history.size > 1) {
                binding.actionBack.visibility = View.VISIBLE
            } else {
                binding.actionBack.visibility = View.GONE
            }
            binding.apisanne.addTextChangedListener(textWatcher)
        }
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (isHTML) {
                val text = binding.apisanne.text
                text?.let { editable ->
                    val subtext = editable.getSpans(startSelect, endSelect, StyleSpan(Typeface.BOLD)::class.java)
                    var check = false
                    subtext.forEach {
                        if (it.style == Typeface.BOLD) {
                            check = true
                            editable.removeSpan(it)
                        }
                    }
                    if (!check) editable.setSpan(StyleSpan(Typeface.BOLD), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
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
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (isHTML) {
                val text = binding.apisanne.text
                text?.let { editable ->
                    val subtext = editable.getSpans(startSelect, endSelect, StyleSpan(Typeface.ITALIC)::class.java)
                    var check = false
                    subtext.forEach {
                        if (it.style == Typeface.ITALIC) {
                            check = true
                            editable.removeSpan(it)
                        }
                    }
                    if (!check) editable.setSpan(StyleSpan(Typeface.ITALIC), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
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
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (isHTML) {
                val text = binding.apisanne.text
                text?.let { editable ->
                    val subtext = editable.getSpans(startSelect, endSelect, ForegroundColorSpan::class.java)
                    var check = false
                    subtext.forEach {
                        if (it.foregroundColor == ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)) {
                            check = true
                            editable.removeSpan(it)
                        }
                    }
                    if (!check) editable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)), startSelect, endSelect, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
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
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_br) {
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<br>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 4)
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
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
            addHistory(binding.apisanne.text, binding.apisanne.selectionEnd)
        }
        if (id == R.id.action_a) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            if (startSelect == endSelect) {
                MainActivity.toastView(this, "Памылка. Абярыце тэкст", Toast.LENGTH_LONG)
            } else {
                val text = binding.apisanne.text
                val urlSpan = text?.getSpans(startSelect, endSelect, URLSpan::class.java)
                var url = ""
                urlSpan?.forEach {
                    url = it.url
                }
                val dialogPasochnicaAHref = DialogPasochnicaAHref.getInstance(url, text?.substring(startSelect, endSelect) ?: "")
                dialogPasochnicaAHref.show(supportFragmentManager, "dialogPasochnicaAHref")
            }
        }
        if (id == R.id.action_keyword) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (binding.apisanne.showSoftInputOnFocus) {
                    imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                } else {
                    imm.showSoftInput(binding.apisanne, 0)
                }
                binding.apisanne.showSoftInputOnFocus = !binding.apisanne.showSoftInputOnFocus
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piasochnica, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    private data class History(val spannable: Spannable, val editPosition: Int)

    private data class SpanStr(val color: Int, val start: Int, val size: Int)
}
