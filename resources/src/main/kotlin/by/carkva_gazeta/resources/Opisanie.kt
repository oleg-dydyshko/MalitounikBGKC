package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.AkafistUnderBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class Opisanie : AppCompatActivity() {
    private var dzenNoch = false
    private var mun = Calendar.getInstance()[Calendar.MONTH] + 1
    private var day = Calendar.getInstance()[Calendar.DATE]
    private var svity = false
    private lateinit var binding: AkafistUnderBinding
    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = AkafistUnderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: c[Calendar.MONTH] + 1
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        val inputStream: InputStream
        if (svity) {
            inputStream = resources.openRawResource(R.raw.opisanie_sviat)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
            val arrayList: ArrayList<ArrayList<String>> = gson.fromJson(builder, type)
            binding.TextView.text = getString(by.carkva_gazeta.malitounik.R.string.opisanie_error)
            arrayList.forEach {
                if (day == it[0].toInt() && mun == it[1].toInt()) {
                    binding.TextView.text = MainActivity.fromHtml(it[2])
                }
            }
        } else {
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
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.use {
                it.readText()
            }
            val gson = Gson()
            val type = object : TypeToken<ArrayList<String>>() {}.type
            val arrayList: ArrayList<String> = gson.fromJson(builder, type)
            val res = arrayList[day - 1]
            binding.TextView.text = MainActivity.fromHtml(res)
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            binding.titleToolbar.setHorizontallyScrolling(true)
            binding.titleToolbar.freezesText = true
            binding.titleToolbar.marqueeRepeatLimit = -1
            if (binding.titleToolbar.isSelected) {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                binding.titleToolbar.isSelected = false
            } else {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.titleToolbar.isSelected = true
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
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
}