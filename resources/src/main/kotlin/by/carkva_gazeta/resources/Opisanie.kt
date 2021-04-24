package by.carkva_gazeta.resources

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
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.databinding.OpisanieBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class Opisanie : AppCompatActivity(), DialogFontSize.DialogFontSizeListener {
    private var dzenNoch = false
    private var mun = Calendar.getInstance()[Calendar.MONTH] + 1
    private var day = Calendar.getInstance()[Calendar.DATE]
    private var year = Calendar.getInstance()[Calendar.YEAR]
    private var svity = false
    private lateinit var binding: OpisanieBinding
    private var change = false
    private lateinit var chin: SharedPreferences
    private var resetTollbarJob: Job? = null
    private var loadIconsJob: Job? = null
    private var opisanieDataLink = ArrayList<OpisanieData>()
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
                        if (fileOpisanie.exists()) builder = fileOpisanie.readText()
                        loadOpisanieSviatyia(builder)
                        opisanieDataLink.forEachIndexed { i, opisanieData ->
                            var schet = ""
                            if (i > 0) schet = "_${i + 1}"
                            val file = File("$filesDir/icons/s_${day}_${mun}$schet.jpg")
                            if (file.exists()) {
                                opisanieData.imageView.setImageBitmap(resizeImage(BitmapFactory.decodeFile(file.absolutePath)))
                                opisanieData.imageView.visibility = View.VISIBLE
                                opisanieData.imageName = "s_${day}_${mun}$schet.jpg"
                                opisanieData.imageView.setOnClickListener {
                                    if (file.exists()) {
                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                        binding.imageViewFull.setImageBitmap(bitmap)
                                        binding.imageViewFull.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                        binding.progressBar2.visibility = View.GONE
                        MainActivity.toastView(this@Opisanie, getString(by.carkva_gazeta.malitounik.R.string.bad_internet), Toast.LENGTH_LONG)
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
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun grateImageView(): ImageView {
        val density = resources.displayMetrics.density
        val padding = 10 * density
        val imageView = ImageView(this)
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        llp.setMargins(0, padding.toInt(), 0, 0)
        llp.gravity = Gravity.CENTER
        imageView.layoutParams = llp
        imageView.visibility = View.GONE
        return imageView
    }

    private fun grateTextView(text: String): TextViewRobotoCondensed {
        val density = resources.displayMetrics.density
        val padding = 10 * density
        val textView = TextViewRobotoCondensed(this)
        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        llp.setMargins(0, padding.toInt(), 0, 0)
        textView.layoutParams = llp
        textView.setTextIsSelectable(true)
        val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        textView.textSize = fontBiblia
        val res = MainActivity.fromHtml(text)
        textView.text = res.trim()
        return textView
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
        binding.linearLayout.removeAllViewsInLayout()
        opisanieDataLink.removeAll(opisanieDataLink)
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
            res.split("<!--image-->").forEach {
                val imageView = grateImageView()
                val textView = grateTextView(it)
                binding.linearLayout.addView(imageView)
                binding.linearLayout.addView(textView)
                opisanieDataLink.add(OpisanieData(imageView, textView))
            }
        } else {
            val imageView = grateImageView()
            val textView = grateTextView(res)
            binding.linearLayout.addView(imageView)
            binding.linearLayout.addView(textView)
            opisanieDataLink.add(OpisanieData(imageView, textView))
        }
    }

    private fun loadOpisanieSviat() {
        val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
        val builder = fileOpisanieSviat.readText()
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
        val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
        arrayList.forEach {
            if (day == it[0].toInt() && mun == it[1].toInt()) {
                var res = it[2]
                if (dzenNoch) res = res.replace("#d00505", "#f44336")
                val imageView = grateImageView()
                val textView = grateTextView(res)
                binding.linearLayout.addView(imageView)
                binding.linearLayout.addView(textView)
                opisanieDataLink.add(OpisanieData(imageView, textView))
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
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = OpisanieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: c[Calendar.MONTH] + 1
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        year = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        if (savedInstanceState != null) {
            change = savedInstanceState.getBoolean("change")
            if (savedInstanceState.getBoolean("imageViewFullVisable")) {
                val bmp: Bitmap? = savedInstanceState.getParcelable("bitmap")
                bmp?.let {
                    binding.imageViewFull.setImageBitmap(Bitmap.createScaledBitmap(it, it.width, it.height, false))
                    binding.imageViewFull.visibility = View.VISIBLE
                }
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            startLoadIconsJob(true)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        if (dzenNoch) binding.swipeRefreshLayout.setColorSchemeResources(by.carkva_gazeta.malitounik.R.color.colorPrimary_black)
        else binding.swipeRefreshLayout.setColorSchemeResources(by.carkva_gazeta.malitounik.R.color.colorPrimary)
        startLoadIconsJob()
        setTollbarTheme()
    }

    private fun startLoadIconsJob(update: Boolean = false) {
        loadIconsJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            startTimer()
            var builder = ""
            val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
            val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
            if (!MainActivity.isNetworkAvailable(this@Opisanie)) {
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
            if (dzenNoch) binding.imageViewFull.background = ContextCompat.getDrawable(this@Opisanie, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            val dir = File("$filesDir/icons/")
            if (!dir.exists()) dir.mkdir()
            opisanieDataLink.forEachIndexed { i, opisanieData ->
                var schet = ""
                if (i > 0) schet = "_${i + 1}"
                val file = if (svity) File("$filesDir/icons/v_${day}_${mun}.jpg")
                else File("$filesDir/icons/s_${day}_${mun}$schet.jpg")
                if (file.exists() && !update) {
                    opisanieData.imageView.post {
                        opisanieData.imageView.setImageBitmap(resizeImage(BitmapFactory.decodeFile(file.absolutePath)))
                        opisanieData.imageView.visibility = View.VISIBLE
                        if (svity) opisanieData.imageName = "v_${day}_${mun}.jpg"
                        else opisanieData.imageName = "s_${day}_${mun}$schet.jpg"
                        opisanieData.imageView.setOnClickListener {
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                binding.imageViewFull.setImageBitmap(bitmap)
                                binding.imageViewFull.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    if (MainActivity.isNetworkAvailable(this@Opisanie)) {
                        withContext(Dispatchers.IO) {
                            try {
                                val mURL = if (svity) URL("https://carkva-gazeta.by/chytanne/icons/v_${day}_${mun}.jpg")
                                else URL("https://carkva-gazeta.by/chytanne/icons/s_${day}_${mun}$schet.jpg")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (conections.responseCode == 200) {
                                    val bufferedInputStream = BufferedInputStream(conections.inputStream)
                                    val byteArrayOut = ByteArrayOutputStream()
                                    var c2: Int
                                    while (bufferedInputStream.read().also { c2 = it } != -1) {
                                        byteArrayOut.write(c2)
                                    }
                                    val byteArray = byteArrayOut.toByteArray()
                                    val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                    val file2 = if (svity) File(dir, "v_${day}_${mun}.jpg")
                                    else File(dir, "s_${day}_${mun}$schet.jpg")
                                    val out = FileOutputStream(file2)
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                    out.flush()
                                    out.close()
                                    withContext(Dispatchers.Main) {
                                        opisanieData.imageView.post {
                                            opisanieData.imageView.setImageBitmap(resizeImage(bmp))
                                            opisanieData.imageView.visibility = View.VISIBLE
                                            if (svity) opisanieData.imageName = "v_${day}_${mun}.jpg"
                                            else opisanieData.imageName = "s_${day}_${mun}$schet.jpg"
                                            opisanieData.imageView.setOnClickListener {
                                                if (file2.exists()) {
                                                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                                    binding.imageViewFull.setImageBitmap(bitmap)
                                                    binding.imageViewFull.visibility = View.VISIBLE
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Throwable) {
                            }
                        }
                    }
                }
            }
            binding.progressBar2.visibility = View.GONE
            stopTimer()
        }
    }

    override fun onDialogFontSize(fontSize: Float) {
        opisanieDataLink.forEach {
            it.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        }
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
        binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.zmiest)
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

    override fun onBackPressed() {
        if (binding.imageViewFull.visibility == View.VISIBLE) {
            binding.imageViewFull.visibility = View.GONE
        } else {
            if (change) {
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.opisanie, menu)
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
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_edit).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = chin.getBoolean("dzen_noch", false)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("change", change)
        outState.putParcelable("bitmap", binding.imageViewFull.drawable?.toBitmap())
        outState.putBoolean("imageViewFullVisable", binding.imageViewFull.visibility == View.VISIBLE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin(this)) {
                val intent = Intent()
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
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
            }
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            change = true
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            var sviatylink = ""
            if (svity) sviatylink = "&sviata=1"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=3$sviatylink&date=$day&month=$mun")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
        return super.onOptionsItemSelected(item)
    }

    private data class OpisanieData(val imageView: ImageView, val textView: TextViewRobotoCondensed, var imageName: String = "noImage")
}