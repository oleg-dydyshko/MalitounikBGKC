package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.OpenableColumns
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.BiblijatekaPdfBinding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar


class BiblijatekaPdf : BaseActivity(), DialogSetPageBiblioteka.DialogSetPageBibliotekaListener {

    private lateinit var binding: BiblijatekaPdfBinding
    private var filePath = ""
    private var fileTitle = ""
    private var totalPage = 1
    private val dzenNoch get() = getBaseDzenNoch()
    private var resetTollbarJob: Job? = null

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = cursor.getString(index)
            }
        } catch (_: Throwable) {
        } finally {
            cursor?.close()
        }
        if (result == null) {
            result = uri.path ?: ""
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        filePath = intent.extras?.getString("filePath", "") ?: ""
        fileTitle = intent.extras?.getString("fileTitle", "") ?: ""
        val c = Calendar.getInstance()
        val edit = k.edit()
        edit.putLong("BiblijatekaUseTime", c.timeInMillis)
        edit.apply()
        val data = intent.data
        val file = File(filePath)
        when {
            data != null -> {
                binding.pdfView.initWithUri(data)
                totalPage = binding.pdfView.totalPageCount
                fileTitle = getFileName(data)
            }

            file.exists() -> {
                binding.pdfView.initWithFile(file)
                totalPage = binding.pdfView.totalPageCount
                if (fileTitle == "") {
                    fileTitle = file.name
                }
                saveNiadaunia()
            }
        }
        setTollbarTheme()
    }

    private fun saveNiadaunia() {
        val naidaunia = ArrayList<ArrayList<String>>()
        val gson = Gson()
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val json = k.getString("bibliateka_naidaunia", "")
        if (json != "") {
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            naidaunia.addAll(gson.fromJson(json, type))
        }
        if (filePath != "") {
            for (i in 0 until naidaunia.size) {
                if (naidaunia[i][1].contains(filePath)) {
                    naidaunia.removeAt(i)
                    break
                }
            }
            val temp = ArrayList<String>()
            temp.add(fileTitle)
            temp.add(filePath)
            val t2 = filePath.lastIndexOf("/")
            val img = filePath.substring(t2 + 1)
            val t1 = img.lastIndexOf(".")
            if (t1 != -1) {
                val image = img.substring(0, t1) + ".png"
                val imageTemp = File("$filesDir/image_temp/$image")
                if (imageTemp.exists()) temp.add("$filesDir/image_temp/$image")
                else temp.add("")
            } else {
                temp.add("")
            }
            naidaunia.add(temp)
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val prefEditor = k.edit()
            prefEditor.putString("bibliateka_naidaunia", gson.toJson(naidaunia, type))
            prefEditor.apply()
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
        binding.pdfView.jumpToPage(page - 1)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_open) {
            try {
                val fileProvider = FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileProvider, "application/pdf")
                startActivity(intent)
            } catch (_: Throwable) {
            }
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
        if (id == R.id.menu_print) {
            val printAdapter = PdfDocumentAdapter(filePath)
            val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
            printManager.print(File(filePath).name, printAdapter, printAttributes)
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.biblijateka_pdf, menu)
        super.onCreateMenu(menu, menuInflater)
    }
}