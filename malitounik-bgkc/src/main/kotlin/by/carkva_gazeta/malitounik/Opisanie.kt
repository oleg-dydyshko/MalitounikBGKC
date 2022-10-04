package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import by.carkva_gazeta.malitounik.databinding.OpisanieBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class Opisanie : BaseActivity(), DialogFontSize.DialogFontSizeListener, DialogOpisanieWIFI.DialogOpisanieWIFIListener, DialogDeliteAllImagesOpisanie.DialogDeliteAllImagesOpisanieListener {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mun = 1
    private var day = 1
    private var year = 2022
    private var svity = false
    private lateinit var binding: OpisanieBinding
    private lateinit var chin: SharedPreferences
    private var resetTollbarJob: Job? = null
    private var loadIconsJob: Job? = null
    private var loadPiarlinyJob: Job? = null
    private var timerCount = 0
    private var timer = Timer()
    private var timerTask: TimerTask? = null

    private fun startTimer() {
        timer = Timer()
        timerCount = 0
        timerTask = object : TimerTask() {
            override fun run() {
                if (loadIconsJob?.isActive == true && timerCount == 6) {
                    loadIconsJob?.cancel()
                    stopTimer()
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.progressBar2.visibility = View.INVISIBLE
                        MainActivity.toastView(this@Opisanie, getString(R.string.bad_internet), Toast.LENGTH_LONG)
                    }
                }
                timerCount++
            }
        }
        timer.schedule(timerTask, 0, 5000)
    }

    private fun viewSviaryiaIIcon() {
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        var builder = ""
        if (svity) {
            loadOpisanieSviat()
        } else {
            if (fileOpisanie.exists()) builder = fileOpisanie.readText()
            loadOpisanieSviatyia(builder)
        }
        for (i in 0..3) {
            var schet = ""
            if (i > 0) schet = "_${i + 1}"
            val file = File("$filesDir/icons/s_${day}_${mun}$schet.jpg")
            if (file.exists()) {
                val imageView = when (i) {
                    1 -> binding.image2
                    2 -> binding.image3
                    3 -> binding.image4
                    else -> binding.image1
                }
                imageView.post {
                    imageView.setImageBitmap(resizeImage(BitmapFactory.decodeFile(file.absolutePath)))
                    imageView.visibility = View.VISIBLE
                    imageView.setOnClickListener {
                        if (file.exists()) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            binding.imageViewFull.setImageBitmap(bitmap)
                            binding.imageViewFull.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        timer.cancel()
        timerTask = null
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        resetTollbarJob?.cancel()
        loadIconsJob?.cancel()
        loadPiarlinyJob?.cancel()
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
        val type = object : TypeToken<ArrayList<String>>() {}.type
        var res = ""
        val arrayList = ArrayList<String>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            res = arrayList[day - 1]
        } else {
            val dialoNoIntent = DialogNoInternet()
            dialoNoIntent.show(supportFragmentManager, "dialoNoIntent")
        }
        if (dzenNoch) res = res.replace("#d00505", "#f44336")
        if (res.contains("<!--image-->")) {
            res.split("<!--image-->").forEachIndexed { index, text ->
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
        } else {
            val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            val spanned = MainActivity.fromHtml(res)
            binding.TextView1.textSize = fontBiblia
            binding.TextView1.text = spanned.trim()
        }
    }

    private fun loadOpisanieSviat() {
        val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
        if (fileOpisanieSviat.exists()) {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
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
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            startLoadIconsJob(!MainActivity.isNetworkAvailable(true))
            binding.swipeRefreshLayout.isRefreshing = false
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary_black)
        } else {
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        }
        viewSviaryiaIIcon()
        if (savedInstanceState == null) startLoadIconsJob(!MainActivity.isNetworkAvailable(true))
        setTollbarTheme()
    }

    override fun onDialogPositiveOpisanieWIFI() {
        startLoadIconsJob(true)
    }

    private fun startLoadIconsJob(loadIcons: Boolean, isFull: Boolean = false) {
        loadIconsJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            startTimer()
            var builder = ""
            val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
            if (!MainActivity.isNetworkAvailable()) {
                if (!svity && fileOpisanie.exists()) {
                    builder = fileOpisanie.readText()
                }
            } else {
                withContext(Dispatchers.IO) {
                    runCatching {
                        try {
                            val mURL = URL("https://carkva-gazeta.by/admin/getFiles.php?update=1")
                            val conections = mURL.openConnection() as HttpURLConnection
                            if (conections.responseCode == 200) {
                                val dir = File("$filesDir/sviatyja/")
                                if (!dir.exists()) dir.mkdir()
                                val builderUrl = mURL.readText()
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builderUrl, type)
                                for (i in 0 until arrayList.size) {
                                    val urlName = arrayList[i][0]
                                    val urlTime = arrayList[i][1].toInt()
                                    val t1 = urlName.lastIndexOf("/")
                                    val file = if (urlName.contains("sviatyja")) {
                                        File("$filesDir/sviatyja/" + urlName.substring(t1 + 1))
                                    } else {
                                        File("$filesDir/" + urlName.substring(t1 + 1))
                                    }
                                    val time = file.lastModified() / 1000
                                    if (!file.exists() || time < urlTime) {
                                        try {
                                            val mURL2 = URL(urlName)
                                            val conections2 = mURL2.openConnection() as HttpURLConnection
                                            if (conections2.responseCode == 200) {
                                                try {
                                                    file.writer().use {
                                                        it.write(mURL2.readText())
                                                    }
                                                } catch (_: Throwable) {
                                                }
                                            }
                                        } catch (_: Throwable) {
                                        }
                                    }
                                }
                            }
                        } catch (_: Throwable) {
                        }
                        try {
                            val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
                            val mURL = URL("https://carkva-gazeta.by/opisanie_sviat.json")
                            val conections = mURL.openConnection() as HttpURLConnection
                            if (conections.responseCode == 200) {
                                fileOpisanieSviat.writer().use {
                                    it.write(mURL.readText())
                                }
                            }
                        } catch (_: Throwable) {
                        }
                        if (!svity && fileOpisanie.exists()) {
                            builder = fileOpisanie.readText()
                        }
                    }
                }
            }
            if (svity) loadOpisanieSviat()
            else loadOpisanieSviatyia(builder)
            if (dzenNoch) binding.imageViewFull.background = ContextCompat.getDrawable(this@Opisanie, R.color.colorbackground_material_dark)
            val dir = File("$filesDir/icons/")
            if (!dir.exists()) dir.mkdir()
            if (MainActivity.isNetworkAvailable()) {
                withContext(Dispatchers.IO) {
                    runCatching {
                        try {
                            val arrayListResult = ArrayList<ArrayList<String>>()
                            val mURL = URL("https://carkva-gazeta.by/admin/getFiles.php?image=1")
                            val conections = mURL.openConnection() as HttpURLConnection
                            if (conections.responseCode == 200) {
                                val builderUrl = mURL.readText()
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builderUrl, type)
                                for (i in 0 until arrayList.size) {
                                    val urlName = arrayList[i][0]
                                    val urlTime = arrayList[i][1].toInt()
                                    val t1 = urlName.lastIndexOf("/")
                                    val file = File("$filesDir/icons/" + urlName.substring(t1 + 1))
                                    val time = file.lastModified() / 1000
                                    if (!file.exists() || time < urlTime) {
                                        if (isFull) {
                                            arrayListResult.add(arrayList[i])
                                        } else {
                                            if (urlName.contains("_${day}_${mun}")) {
                                                arrayListResult.add(arrayList[i])
                                            }
                                        }
                                    }
                                }
                            }
                            var size = 0f
                            for (i in 0 until arrayListResult.size) {
                                size += arrayListResult[i][2].toFloat()
                            }
                            withContext(Dispatchers.Main) {
                                val max = size.toInt()
                                binding.progressBar2.isIndeterminate = false
                                binding.progressBar2.progress = 0
                                binding.progressBar2.max = max
                            }
                            if (!loadIcons && MainActivity.isNetworkAvailable(true) && arrayListResult.isNotEmpty()) {
                                withContext(Dispatchers.Main) {
                                    val dialog = DialogOpisanieWIFI.getInstance(size)
                                    dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
                                }
                            } else {
                                var progress = 0
                                for (i in 0 until arrayListResult.size) {
                                    val urlName = arrayListResult[i][0]
                                    val mURL2 = URL(urlName)
                                    val conections2 = mURL2.openConnection() as HttpURLConnection
                                    if (conections2.responseCode == 200) {
                                        val t1 = urlName.lastIndexOf("/")
                                        val file = File("$filesDir/icons/" + urlName.substring(t1 + 1))
                                        val bufferedInputStream = BufferedInputStream(conections2.inputStream)
                                        val byteArrayOut = ByteArrayOutputStream()
                                        var c2: Int
                                        while (bufferedInputStream.read().also { c2 = it } != -1) {
                                            byteArrayOut.write(c2)
                                        }
                                        val byteArray = byteArrayOut.toByteArray()
                                        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                        val out = FileOutputStream(file)
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                        out.flush()
                                        out.close()
                                        withContext(Dispatchers.Main) {
                                            progress += arrayListResult[i][2].toInt()
                                            binding.progressBar2.progress = progress
                                        }
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    var endImage = 3
                                    if (svity) endImage = 0
                                    for (e in 0..endImage) {
                                        var schet = ""
                                        if (e > 0) schet = "_${e + 1}"
                                        val file2 = if (svity) File("$filesDir/icons/v_${day}_${mun}.jpg")
                                        else File("$filesDir/icons/s_${day}_${mun}$schet.jpg")
                                        if (file2.exists()) {
                                            val imageView = when (e) {
                                                1 -> binding.image2
                                                2 -> binding.image3
                                                3 -> binding.image4
                                                else -> binding.image1
                                            }
                                            imageView.post {
                                                imageView.setImageBitmap(resizeImage(BitmapFactory.decodeFile(file2.absolutePath)))
                                                imageView.visibility = View.VISIBLE
                                                imageView.setOnClickListener {
                                                    if (file2.exists()) {
                                                        val bitmap = BitmapFactory.decodeFile(file2.absolutePath)
                                                        binding.imageViewFull.setImageBitmap(bitmap)
                                                        binding.imageViewFull.visibility = View.VISIBLE
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (_: Throwable) {
                        }
                    }
                }
            }
            binding.progressBar2.visibility = View.INVISIBLE
            binding.progressBar2.isIndeterminate = true
            stopTimer()
        }
    }

    private fun checkParliny(): Boolean {
        val piarliny = ArrayList<ArrayList<String>>()
        val fileOpisanieSviat = File("$filesDir/piarliny.json")
        if (fileOpisanieSviat.exists()) {
            try {
                val builder = fileOpisanieSviat.readText()
                val gson = Gson()
                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
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
                }
            }
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

    override fun onBackPressed() {
        if (binding.imageViewFull.visibility == View.VISIBLE) {
            binding.imageViewFull.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.opisanie, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_piarliny).isVisible = checkParliny()
        menu.findItem(R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        if (chin.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("bitmap", binding.imageViewFull.drawable?.toBitmap())
        outState.putBoolean("imageViewFullVisable", binding.imageViewFull.visibility == View.VISIBLE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_download_all) {
            startLoadIconsJob(true, isFull = true)
        }
        if (id == R.id.action_download_del) {
            val dialogDeliteAllImagesOpisanie = DialogDeliteAllImagesOpisanie()
            dialogDeliteAllImagesOpisanie.show(supportFragmentManager, "dialogDeliteAllImagesOpisanie")
        }
        if (id == R.id.action_piarliny) {
            val i = Intent(this, Piarliny::class.java)
            i.putExtra("mun", mun)
            i.putExtra("day", day)
            startActivity(i)
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
                    var dayofyear = cal[Calendar.DAY_OF_YEAR] - 1
                    if (!cal.isLeapYear(cal[Calendar.YEAR]) && dayofyear >= 59) {
                        dayofyear++
                    }
                    intent.putExtra("dayOfYear", dayofyear)
                }
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
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
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            var sviatylink = ""
            if (svity) sviatylink = "&sviata=1"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=3$sviatylink&date=$day&month=$mun")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
        return super.onOptionsItemSelected(item)
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
}