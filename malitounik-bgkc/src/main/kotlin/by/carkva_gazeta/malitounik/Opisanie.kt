package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class Opisanie : BaseActivity(), DialogFontSize.DialogFontSizeListener, DialogOpisanieWIFI.DialogOpisanieWIFIListener {
    private var dzenNoch = false
    private var mun = Calendar.getInstance()[Calendar.MONTH] + 1
    private var day = Calendar.getInstance()[Calendar.DATE]
    private var year = Calendar.getInstance()[Calendar.YEAR]
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
                        binding.progressBar2.visibility = View.INVISIBLE
                        MainActivity.toastView(getString(R.string.bad_internet), Toast.LENGTH_LONG)
                    }
                }
                timerCount++
            }
        }
        timer.schedule(timerTask, 0, 5000)
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

    private fun resizeImage(bitmap: Bitmap): Bitmap {
        var newHeight = bitmap.height.toFloat()
        var newWidth = bitmap.width.toFloat()
        val widthLinear = binding.linearLayout.width.toFloat()
        val resoluton = newWidth / newHeight
        newWidth = 500 * resoluton
        newHeight = 500F
        if (newWidth > widthLinear) {
            newWidth = widthLinear
            newHeight = newWidth / resoluton
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth.toInt(), newHeight.toInt(), false)
    }

    private fun loadOpisanieSviatyia(builder: String) {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        var res = ""
        val arrayList = ArrayList<String>()
        if (builder != "") {
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
            val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        binding = OpisanieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Slidr.attach(this)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        year = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        if (savedInstanceState?.getBoolean("imageViewFullVisable") == true) {
            val bmp: Bitmap? = savedInstanceState.getParcelable("bitmap")
            bmp?.let {
                binding.imageViewFull.setImageBitmap(Bitmap.createScaledBitmap(it, it.width, it.height, false))
                binding.imageViewFull.visibility = View.VISIBLE
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (MainActivity.isNetworkAvailable(true)) {
                val dialog = DialogOpisanieWIFI()
                dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
            } else {
                startLoadIconsJob(update = true, true)
                updatePiarliny()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary_black)
        } else {
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        }
        startLoadIconsJob(loadIcons = !MainActivity.isNetworkAvailable(true))
        setTollbarTheme()
    }

    override fun onDialogPositiveOpisanieWIFI() {
        startLoadIconsJob(update = true, true)
    }

    private fun startLoadIconsJob(update: Boolean = false, loadIcons: Boolean) {
        loadIconsJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            startTimer()
            var builder = ""
            val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
            val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
            if (!MainActivity.isNetworkAvailable()) {
                if (svity) {
                    if (fileOpisanieSviat.exists()) builder = fileOpisanieSviat.readText()
                } else {
                    if (fileOpisanie.exists()) builder = fileOpisanie.readText()
                }
            } else {
                if (update) {
                    val timeUpdate = Calendar.getInstance().timeInMillis
                    val prefEditors = chin.edit()
                    prefEditors.putLong("OpisanieTimeUpdate", timeUpdate)
                    prefEditors.apply()
                }
                withContext(Dispatchers.IO) {
                    try {
                        val dir = File("$filesDir/sviatyja/")
                        if (!dir.exists()) dir.mkdir()
                        if (!fileOpisanieSviat.exists() || update) {
                            val mURL = URL("https://carkva-gazeta.by/opisanie_sviat.json")
                            val conections = mURL.openConnection() as HttpURLConnection
                            if (conections.responseCode == 200) {
                                fileOpisanieSviat.writer().use {
                                    it.write(mURL.readText())
                                }
                            }
                        }
                        for (i in 1..12) {
                            val fileS = File("$filesDir/sviatyja/opisanie$i.json")
                            if (!fileS.exists() || (update && mun == i)) {
                                val mURL = URL("https://carkva-gazeta.by/chytanne/sviatyja/opisanie$i.json")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (conections.responseCode == 200) {
                                    fileS.writer().use {
                                        it.write(mURL.readText())
                                    }
                                }
                            }
                        }
                    } catch (e: Throwable) {
                    }
                    builder = if (svity) fileOpisanieSviat.readText()
                    else fileOpisanie.readText()
                }
            }
            if (svity) loadOpisanieSviat()
            else loadOpisanieSviatyia(builder)
            if (dzenNoch) binding.imageViewFull.background = ContextCompat.getDrawable(this@Opisanie, R.color.colorbackground_material_dark)
            val dir = File("$filesDir/icons/")
            if (!dir.exists()) dir.mkdir()
            var endImage = 3
            if (svity) endImage = 0
            for (i in 0..endImage) {
                var schet = ""
                if (i > 0) schet = "_${i + 1}"
                val file = if (svity) File("$filesDir/icons/v_${day}_${mun}.jpg")
                else File("$filesDir/icons/s_${day}_${mun}$schet.jpg")
                if (file.exists() && !update) {
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
                } else {
                    if (MainActivity.isNetworkAvailable()) {
                        withContext(Dispatchers.IO) {
                            try {
                                val mURL = if (svity) URL("https://carkva-gazeta.by/chytanne/icons/v_${day}_${mun}.jpg")
                                else URL("https://carkva-gazeta.by/chytanne/icons/s_${day}_${mun}$schet.jpg")
                                val file2 = if (svity) File(dir, "v_${day}_${mun}.jpg")
                                else File(dir, "s_${day}_${mun}$schet.jpg")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (!loadIcons && MainActivity.isNetworkAvailable(true) && conections.responseCode == 200) {
                                    withContext(Dispatchers.Main) {
                                        if (supportFragmentManager.findFragmentByTag("dialogOpisanieWIFI") == null) {
                                            val dialog = DialogOpisanieWIFI()
                                            dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
                                        }
                                    }
                                } else {
                                    if (conections.responseCode == 200) {
                                        val bufferedInputStream = BufferedInputStream(conections.inputStream)
                                        val byteArrayOut = ByteArrayOutputStream()
                                        var c2: Int
                                        while (bufferedInputStream.read().also { c2 = it } != -1) {
                                            byteArrayOut.write(c2)
                                        }
                                        val byteArray = byteArrayOut.toByteArray()
                                        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                        val out = FileOutputStream(file2)
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                        out.flush()
                                        out.close()
                                        withContext(Dispatchers.Main) {
                                            val imageView = when (i) {
                                                1 -> binding.image2
                                                2 -> binding.image3
                                                3 -> binding.image4
                                                else -> binding.image1
                                            }
                                            imageView.post {
                                                imageView.setImageBitmap(resizeImage(bmp))
                                                imageView.visibility = View.VISIBLE
                                                imageView.setOnClickListener {
                                                    if (file2.exists()) {
                                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                                        binding.imageViewFull.setImageBitmap(bitmap)
                                                        binding.imageViewFull.visibility = View.VISIBLE
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (conections.responseCode != 200 && file2.exists()) {
                                    file2.delete()
                                    val imageView = when (i) {
                                        1 -> binding.image2
                                        2 -> binding.image3
                                        3 -> binding.image4
                                        else -> binding.image1
                                    }
                                    imageView.post {
                                        imageView.visibility = View.GONE
                                    }
                                }
                            } catch (e: Throwable) {
                            }
                        }
                    }
                }
            }
            binding.progressBar2.visibility = View.INVISIBLE
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

    private fun updatePiarliny() {
        if (loadPiarlinyJob?.isActive != true) {
            val fileOpisanieSviat = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
            if (MainActivity.isNetworkAvailable()) {
                loadPiarlinyJob = CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val mURL = URL("https://carkva-gazeta.by/chytanne/piarliny.json")
                            val conections = mURL.openConnection() as HttpURLConnection
                            if (conections.responseCode == 200) {
                                fileOpisanieSviat.writer().use {
                                    it.write(mURL.readText())
                                }
                            }
                        } catch (e: Throwable) {
                        }
                        checkParliny()
                    }
                }
            }
        }
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
        menu.findItem(R.id.action_dzen_noch).isChecked = chin.getBoolean("dzen_noch", false)
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
                MainActivity.toastView(getString(R.string.error))
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
}