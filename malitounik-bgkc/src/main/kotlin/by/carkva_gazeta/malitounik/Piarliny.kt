package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import by.carkva_gazeta.malitounik.databinding.PiarlinyBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class Piarliny : BaseActivity(), DialogFontSize.DialogFontSizeListener, DialogOpisanieWIFI.DialogOpisanieWIFIListener {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mun = Calendar.getInstance()[Calendar.MONTH] + 1
    private var day = Calendar.getInstance()[Calendar.DATE]
    private lateinit var binding: PiarlinyBinding
    private lateinit var chin: SharedPreferences
    private var resetTollbarJob: Job? = null
    private var loadPiarlinyJob: Job? = null

    override fun onPause() {
        super.onPause()
        loadPiarlinyJob?.cancel()
        resetTollbarJob?.cancel()
    }

    private fun loadPiarliny(update: Boolean = false) {
        val piarliny = ArrayList<ArrayList<String>>()
        val fileOpisanieSviat = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
        if (!fileOpisanieSviat.exists() || update) {
            if (MainActivity.isNetworkAvailable()) {
                loadPiarlinyJob = CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            try {
                                val mURL = URL("https://carkva-gazeta.by/chytanne/piarliny.json")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (conections.responseCode == 200) {
                                    fileOpisanieSviat.writer().use {
                                        it.write(mURL.readText())
                                    }
                                }
                            } catch (_: Throwable) {
                            }
                        }
                    }
                    try {
                        val builder = fileOpisanieSviat.readText()
                        val gson = Gson()
                        val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                        piarliny.addAll(gson.fromJson(builder, type))
                    } catch (t: Throwable) {
                        fileOpisanieSviat.delete()
                    }
                    val cal = GregorianCalendar()
                    val sb = StringBuilder()
                    var i = 0
                    piarliny.forEach {
                        cal.timeInMillis = it[0].toLong() * 1000
                        if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                            if (i > 0) sb.append("<p>\n")
                            sb.append(it[1])
                            i++
                        }
                    }
                    withContext(Dispatchers.Main) {
                        binding.textView.text = MainActivity.fromHtml(sb.toString())
                    }
                }
            }
        } else {
            try {
                val builder = fileOpisanieSviat.readText()
                val gson = Gson()
                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                piarliny.addAll(gson.fromJson(builder, type))
            } catch (t: Throwable) {
                fileOpisanieSviat.delete()
            }
            val cal = GregorianCalendar()
            val sb = StringBuilder()
            var i = 0
            piarliny.forEach {
                cal.timeInMillis = it[0].toLong() * 1000
                if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                    if (i > 0) sb.append("<p>\n")
                    sb.append(it[1])
                    i++
                }
            }
            binding.textView.text = MainActivity.fromHtml(sb.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = PiarlinyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (MainActivity.isNetworkAvailable(true)) {
                val dialog = DialogOpisanieWIFI()
                dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
            } else {
                loadPiarliny(true)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary_black)
        } else {
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        }
        loadPiarliny()
        setTollbarTheme()
    }

    override fun onDialogPositiveOpisanieWIFI() {
        loadPiarliny(true)
    }

    override fun onDialogFontSize(fontSize: Float) {
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
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
        binding.titleToolbar.text = getString(R.string.piarliny2, day, resources.getStringArray(R.array.meciac_smoll)[mun - 1])
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.pasxa, menu)
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
        menu.findItem(R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        if (chin.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.ADMINPIARLINY)
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
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=6&date=$day&month=$mun")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
        return super.onOptionsItemSelected(item)
    }
}