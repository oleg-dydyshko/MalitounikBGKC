package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.databinding.AkafistListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.*


class TraparyAndKandaki : AppCompatActivity() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<String>()
    private lateinit var binding: AkafistListBinding
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun loadTraparOrKandak(day: Int, mun: Int) {
        var inputStream = resources.openRawResource(by.carkva_gazeta.malitounik.R.raw.opisanie_sviat)
        var isr = InputStreamReader(inputStream)
        var reader = BufferedReader(isr)
        var builder = reader.use {
            it.readText()
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
        val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
        if (arrayList[day][2] != "") {
            val trapar = arrayList[day][2]
            val t1 = trapar.indexOf("<strong>")
            val t2 = trapar.indexOf("</strong>")
            if (t1 != -1 && t2 != -1) data.add(trapar.substring(t1 + 8, t2))
            else data.add(getString(by.carkva_gazeta.malitounik.R.string.trsviata))
        }
        inputStream = when (mun) {
            1 -> resources.openRawResource(R.raw.opisanie1)
            2 -> resources.openRawResource(R.raw.opisanie2)
            3 -> resources.openRawResource(R.raw.opisanie3)
            4 -> resources.openRawResource(R.raw.opisanie4)
            5 -> resources.openRawResource(R.raw.opisanie5)
            6 -> resources.openRawResource(R.raw.opisanie6)
            7 -> resources.openRawResource(R.raw.opisanie7)
            8 -> resources.openRawResource(R.raw.opisanie8)
            9 -> resources.openRawResource(R.raw.opisanie9)
            10 -> resources.openRawResource(R.raw.opisanie10)
            11 -> resources.openRawResource(R.raw.opisanie11)
            12 -> resources.openRawResource(R.raw.opisanie12)
            else -> resources.openRawResource(R.raw.opisanie1)
        }
        isr = InputStreamReader(inputStream)
        reader = BufferedReader(isr)
        builder = reader.use {
            it.readText()
        }
        val res: ArrayList<String> = gson.fromJson(builder, type)
        if (res[day - 1].contains("трапар", true) || res[day - 1].contains("кандак", true)) {
            val trapar = res[day - 1]
            val t1 = trapar.indexOf("<strong>")
            val t2 = trapar.indexOf("</strong>")
            if (t1 != -1 && t2 != -1) data.add(trapar.substring(t1 + 8, t2))
            else data.add(getString(by.carkva_gazeta.malitounik.R.string.trsviatoy))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = AkafistListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.trapar_kandak)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        val adapter = MenuListAdaprer(this, data)
        binding.ListView.adapter = adapter
        val ton = intent.extras?.getInt("ton", 1) ?: 1
        val tonNadzelny = intent.extras?.getBoolean("ton_naidzelny", true) ?: true
        binding.ListView.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent()
            intent.setClassName(this, MainActivity.TON)
            if (position != 0) {
                intent.putExtra("under", data[position])
            }
            intent.putExtra("ton", ton)
            intent.putExtra("ton_naidzelny", tonNadzelny)
            startActivity(intent)
        }
        if (tonNadzelny) {
            data.add("Тон $ton")
        } else {
            when (ton) {
                1 -> {
                    data.add("ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам")
                }
                2 -> {
                    data.add("АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю")
                }
                3 -> {
                    data.add("СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу")
                }
                4 -> {
                    data.add("ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю")
                }
                5 -> {
                    data.add("ПЯТНІЦА\nСлужба Крыжу Гасподняму")
                }
                6 -> {
                    data.add("Субота\nСлужба ўсім сьвятым і памёрлым")
                }
            }
        }
        val day = intent.extras?.getInt("day", 1) ?: 1
        val mun = intent.extras?.getInt("mun", 1) ?: 0
        loadTraparOrKandak(day, mun + 1)
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
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}