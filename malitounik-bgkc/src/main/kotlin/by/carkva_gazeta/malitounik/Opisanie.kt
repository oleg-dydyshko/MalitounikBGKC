package by.carkva_gazeta.malitounik

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.OpisanieBinding
import com.google.firebase.storage.ListResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.*
import java.util.*


class Opisanie : BaseActivity(), DialogFontSize.DialogFontSizeListener, DialogOpisanieWIFI.DialogOpisanieWIFIListener, DialogDeliteAllImagesOpisanie.DialogDeliteAllImagesOpisanieListener, DialogHelpShare.DialogHelpShareListener {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mun = 1
    private var day = 1
    private var year = 2022
    private var svity = false
    private lateinit var binding: OpisanieBinding
    private lateinit var chin: SharedPreferences
    private var resetTollbarJob: Job? = null
    private var loadIconsJob: Job? = null
    private val dirList = ArrayList<DirList>()

    private fun viewSviaryiaIIcon() {
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        if (svity) {
            loadOpisanieSviat()
        } else {
            if (fileOpisanie.exists()) loadOpisanieSviatyia(fileOpisanie.readText())
        }
        loadIconsOnImageView()
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        loadIconsJob?.cancel()
    }

    private fun resizeImage(bitmap: Bitmap?): Bitmap? {
        bitmap?.let {
            var newHeight = it.height.toFloat()
            var newWidth = it.width.toFloat()
            val widthLinear = binding.linearLayout.width.toFloat()
            val resoluton = newWidth / newHeight
            newWidth = 500f * resoluton
            newHeight = 500f
            if (newWidth > widthLinear) {
                newWidth = widthLinear
                newHeight = newWidth / resoluton
            }
            return Bitmap.createScaledBitmap(it, newWidth.toInt(), newHeight.toInt(), false)
        }
        return null
    }

    private fun loadOpisanieSviatyia(builder: String) {
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        var res = ""
        val arrayList = ArrayList<String>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            res = arrayList[day - 1]
        }
        if (dzenNoch) res = res.replace("#d00505", "#f44336")
        val title = ArrayList<String>()
        val listRes = res.split("<strong>")
        var sb = ""
        for (i in listRes.size - 1 downTo 0) {
            val text = listRes[i].replace("<!--image-->", "")
            if (text.trim() != "") {
                if (text.contains("Трапар", ignoreCase = true) || text.contains("Кандак", ignoreCase = true)) {
                    sb = "<strong>$text$sb"
                    continue
                } else {
                    sb = "<strong>$text$sb"
                    title.add(0, sb)
                    sb = ""
                }
            }
        }
        title.forEachIndexed { index, text ->
            val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            val spanned = MainActivity.fromHtml(text)
            when (index) {
                0 -> {
                    binding.TextView1.textSize = fontBiblia
                    binding.TextView1.text = spanned.trim()
                }

                1 -> {
                    binding.TextView2.textSize = fontBiblia
                    binding.TextView2.text = spanned.trim()
                    binding.TextView2.visibility = View.VISIBLE
                }

                2 -> {
                    binding.TextView3.textSize = fontBiblia
                    binding.TextView3.text = spanned.trim()
                    binding.TextView3.visibility = View.VISIBLE
                }

                3 -> {
                    binding.TextView4.textSize = fontBiblia
                    binding.TextView4.text = spanned.trim()
                    binding.TextView4.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadOpisanieSviat() {
        val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
        if (fileOpisanieSviat.exists()) {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
            if (arrayList != null) {
                arrayList.forEach {
                    if (day == it[0].toInt() && mun == it[1].toInt()) {
                        var res = it[2]
                        if (dzenNoch) res = res.replace("#d00505", "#f44336")
                        val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
                        val spanned = MainActivity.fromHtml(res)
                        binding.TextView1.textSize = fontBiblia
                        binding.TextView1.text = spanned.trim()
                    }
                }
            } else {
                fileOpisanieSviat.delete()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = OpisanieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        year = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        if (savedInstanceState?.getBoolean("imageViewFullVisable") == true) {
            val bmp = if (Build.VERSION.SDK_INT >= 33) {
                savedInstanceState.getParcelable("bitmap", Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION") savedInstanceState.getParcelable("bitmap")
            }
            bmp?.let {
                binding.imageViewFull.setImageBitmap(Bitmap.createScaledBitmap(it, it.width, it.height, false))
                binding.imageViewFull.visibility = View.VISIBLE
                binding.progressBar2.visibility = View.INVISIBLE
                binding.swipeRefreshLayout.visibility = View.GONE
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            startLoadIconsJob(!MainActivity.isNetworkAvailable(true))
            binding.swipeRefreshLayout.isRefreshing = false
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary_black)
            binding.imageViewFull.background = ContextCompat.getDrawable(this, R.color.colorbackground_material_dark)
        } else {
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        }
        viewSviaryiaIIcon()
        if (savedInstanceState == null) startLoadIconsJob(!MainActivity.isNetworkAvailable(true))
        setTollbarTheme()
    }

    override fun onDialogPositiveOpisanieWIFI(isFull: Boolean) {
        startLoadIconsJob(true, isFull)
    }

    override fun onDialogNegativeOpisanieWIFI() {
        binding.progressBar2.visibility = View.INVISIBLE
    }

    private fun startLoadIconsJob(loadIcons: Boolean, isFull: Boolean = false) {
        loadIconsJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.isIndeterminate = true
            binding.progressBar2.visibility = View.VISIBLE
            if (!MainActivity.isNetworkAvailable()) {
                val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
                if (!svity && fileOpisanie.exists()) {
                    loadOpisanieSviatyia(fileOpisanie.readText())
                } else {
                    val dialoNoIntent = DialogNoInternet()
                    dialoNoIntent.show(supportFragmentManager, "dialoNoIntent")
                }
            } else {
                try {
                    if (svity) {
                        getOpisanieSviat()
                    } else {
                        getSviatyia()
                    }
                    getIcons(loadIcons, isFull)
                    getPiarliny()
                } catch (_: Throwable) {
                }
            }
        }
    }

    private suspend fun getOpisanieSviat() {
        val pathReference = Malitounik.referens.child("/opisanie_sviat.json")
        val file = File("$filesDir/" + pathReference.name)
        var update = 0L
        pathReference.metadata.addOnSuccessListener { storageMetadata ->
            update = storageMetadata.updatedTimeMillis
        }.await()
        val time = file.lastModified()
        if (!file.exists() || time < update) {
            pathReference.getFile(file).await()
        }
        loadOpisanieSviat()
    }

    private suspend fun getSviatyia() {
        val dir = File("$filesDir/sviatyja/")
        if (!dir.exists()) dir.mkdir()
        var list: ListResult? = null
        Malitounik.referens.child("/chytanne/sviatyja").list(12).addOnSuccessListener { listResult ->
            list = listResult
        }.await()
        list?.items?.forEach { storageReference ->
            if (mun == Calendar.getInstance()[Calendar.MONTH] + 1) {
                var update = 0L
                storageReference.metadata.addOnSuccessListener { storageMetadata ->
                    update = storageMetadata.updatedTimeMillis
                }.await()
                val fileOpisanie = File("$filesDir/sviatyja/" + storageReference.name)
                val time = fileOpisanie.lastModified()
                if (!fileOpisanie.exists() || time < update) {
                    storageReference.getFile(fileOpisanie).await()
                }
            }
        }
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        if (fileOpisanie.exists()) loadOpisanieSviatyia(fileOpisanie.readText())
    }

    private suspend fun getIcons(loadIcons: Boolean, isFull: Boolean) {
        val dir = File("$filesDir/icons/")
        if (!dir.exists()) dir.mkdir()
        val arrayList = ArrayList<ArrayList<String>>()
        val localFile = File("$filesDir/cache/cache.txt")
        Malitounik.referens.child("/icons.json").getFile(localFile).addOnSuccessListener {
            val gson = Gson()
            val json = localFile.readText()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            arrayList.addAll(gson.fromJson(json, type))
        }.await()
        dirList.clear()
        var size = 0L
        val images = ArrayList<String>()
        images.add("s_${day}_${mun}.jpg")
        images.add("s_${day}_${mun}_2.jpg")
        images.add("s_${day}_${mun}_3.jpg")
        images.add("s_${day}_${mun}_4.jpg")
        arrayList.forEach {
            val pref = if (svity) "v"
            else "s"
            val setIsFull = if (isFull) true
            else it[0].contains("${pref}_${day}_${mun}.") || it[0].contains("${pref}_${day}_${mun}_")
            if (setIsFull) {
                val fileIcon = File("$filesDir/icons/" + it[0])
                for (i in 0 until images.size) {
                    if (fileIcon.name == images[i]) {
                        images.removeAt(i)
                        break
                    }
                }
                val time = fileIcon.lastModified()
                val update = it[2].toLong()
                if (!fileIcon.exists() || time < update) {
                    dirList.add(DirList(it[0], it[1].toLong()))
                    size += it[1].toLong()
                }
            }
        }
        if (images.isNotEmpty()) {
            for (i in 0 until images.size) {
                val file = File("$filesDir/icons/" + images[i])
                if (file.exists()) file.delete()
            }
        }
        if (!loadIcons && MainActivity.isNetworkAvailable(true)) {
            if (dirList.isNotEmpty()) {
                val dialog = DialogOpisanieWIFI.getInstance(size.toFloat(), isFull)
                dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
            } else {
                binding.progressBar2.visibility = View.INVISIBLE
            }
        } else {
            binding.progressBar2.isIndeterminate = false
            binding.progressBar2.progress = 0
            binding.progressBar2.max = size.toInt()
            var progress = 0
            for (i in 0 until dirList.size) {
                val fileIcon = File("$filesDir/icons/" + dirList[i].name)
                val pathReference = Malitounik.referens.child("/chytanne/icons/" + dirList[i].name)
                pathReference.getFile(fileIcon).await()
                progress += dirList[i].sizeBytes.toInt()
                binding.progressBar2.progress = progress
            }
            loadIconsOnImageView()
        }
    }

    private fun loadIconsOnImageView() {
        var endImage = 3
        if (svity) endImage = 0
        for (e in 0..endImage) {
            var schet = ""
            if (e > 0) schet = "_${e + 1}"
            val file2 = if (svity) File("$filesDir/icons/v_${day}_${mun}.jpg")
            else File("$filesDir/icons/s_${day}_${mun}$schet.jpg")
            val imageView = when (e) {
                1 -> binding.image2
                2 -> binding.image3
                3 -> binding.image4
                else -> binding.image1
            }
            if (file2.exists()) {
                imageView.post {
                    imageView.setImageBitmap(resizeImage(BitmapFactory.decodeFile(file2.absolutePath)))
                    imageView.visibility = View.VISIBLE
                    imageView.setOnClickListener {
                        if (file2.exists()) {
                            val bitmap = BitmapFactory.decodeFile(file2.absolutePath)
                            binding.imageViewFull.setImageBitmap(bitmap)
                            binding.imageViewFull.visibility = View.VISIBLE
                            binding.progressBar2.visibility = View.INVISIBLE
                            binding.swipeRefreshLayout.visibility = View.GONE
                        }
                    }
                }
            } else {
                imageView.post {
                    imageView.setImageBitmap(null)
                    imageView.visibility = View.GONE
                    imageView.setOnClickListener(null)
                }
            }
        }
        binding.progressBar2.visibility = View.INVISIBLE
    }

    private suspend fun getPiarliny() {
        val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
        val localFile = File("$filesDir/piarliny.json")
        pathReference.getFile(localFile).await()
        invalidateOptionsMenu()
    }

    private fun checkParliny(): Boolean {
        val piarliny = ArrayList<ArrayList<String>>()
        val fileOpisanieSviat = File("$filesDir/piarliny.json")
        if (fileOpisanieSviat.exists()) {
            try {
                val builder = fileOpisanieSviat.readText()
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                piarliny.addAll(gson.fromJson(builder, type))
            } catch (t: Throwable) {
                fileOpisanieSviat.delete()
            }
            val cal = GregorianCalendar()
            piarliny.forEach {
                cal.timeInMillis = it[0].toLong() * 1000
                if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onDialogFontSize(fontSize: Float) {
        binding.TextView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        binding.TextView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        binding.TextView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        binding.TextView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.collapsingToolbarLayout.layoutParams
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getText(R.string.zmiest)
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

    override fun onBack() {
        if (binding.imageViewFull.visibility == View.VISIBLE) {
            binding.imageViewFull.visibility = View.GONE
            binding.swipeRefreshLayout.visibility = View.VISIBLE
            viewSviaryiaIIcon()
        } else {
            super.onBack()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.opisanie, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_piarliny).isVisible = checkParliny()
        menu.findItem(R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        if (chin.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("bitmap", binding.imageViewFull.drawable?.toBitmap())
        outState.putBoolean("imageViewFullVisable", binding.imageViewFull.visibility == View.VISIBLE)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_download_all) {
            startLoadIconsJob(!MainActivity.isNetworkAvailable(true), isFull = true)
            return true
        }
        if (id == R.id.action_download_del) {
            val dialogDeliteAllImagesOpisanie = DialogDeliteAllImagesOpisanie()
            dialogDeliteAllImagesOpisanie.show(supportFragmentManager, "dialogDeliteAllImagesOpisanie")
            return true
        }
        if (id == R.id.action_piarliny) {
            val i = Intent(this, Piarliny::class.java)
            i.putExtra("mun", mun)
            i.putExtra("day", day)
            startActivity(i)
            return true
        }
        if (id == R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                if (svity) {
                    intent.setClassName(this, MainActivity.ADMINSVIATY)
                    intent.putExtra("day", day)
                    intent.putExtra("mun", mun)
                } else {
                    intent.setClassName(this, MainActivity.ADMINSVIATYIA)
                    val cal = Calendar.getInstance() as GregorianCalendar
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, mun - 1)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    var dayofyear = cal[Calendar.DAY_OF_YEAR]
                    if (!cal.isLeapYear(cal[Calendar.YEAR]) && dayofyear >= 59) {
                        dayofyear++
                    }
                    intent.putExtra("dayOfYear", dayofyear)
                }
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val prefEditor = chin.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == R.id.action_share) {
            val sb = StringBuilder()
            val text1 = binding.TextView1.text.toString()
            if (text1 != "") sb.append(text1).append("\n\n")
            val text2 = binding.TextView2.text.toString()
            if (text2 != "") sb.append(text2).append("\n\n")
            val text3 = binding.TextView3.text.toString()
            if (text3 != "") sb.append(text3).append("\n\n")
            val text4 = binding.TextView4.text.toString()
            if (text4 != "") sb.append(text4)
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.copy_text), sb.toString())
            clipboard.setPrimaryClip(clip)
            MainActivity.toastView(this, getString(R.string.copy_text), Toast.LENGTH_LONG)
            if (chin.getBoolean("dialogHelpShare", true)) {
                val dialog = DialogHelpShare.getInstance(sb.toString())
                dialog.show(supportFragmentManager, "DialogHelpShare")
            } else {
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString())
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.zmiest))
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.zmiest)))
            }
            return true
        }
        return false
    }

    override fun sentShareText(shareText: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.zmiest))
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.zmiest)))
    }

    override fun deliteAllImagesOpisanie() {
        val dir = File("$filesDir/icons/")
        if (dir.exists()) dir.deleteRecursively()
        binding.image1.setImageBitmap(null)
        binding.image2.setImageBitmap(null)
        binding.image3.setImageBitmap(null)
        binding.image4.setImageBitmap(null)
        MainActivity.toastView(this, getString(R.string.remove_padzea))
    }

    private data class DirList(val name: String?, val sizeBytes: Long)
}