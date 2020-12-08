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
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.MyMalitvaViewBinding
import java.io.File

class MyNatatkiView : AppCompatActivity() {
    private lateinit var binding: MyMalitvaViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        binding = MyMalitvaViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        val filename = intent.getStringExtra("filename")
        binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (dzenNoch) binding.TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite)) else binding.TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
        val res = File(this.filesDir.toString() + "/Malitva/" + filename).readText().split("<MEMA></MEMA>").toTypedArray()
        binding.titleToolbar.text = res[0]
        if (res[1].contains("<RTE></RTE>")) {
            val start = res[1].indexOf("<RTE></RTE>")
            res[1] = res[1].substring(0, start)
        }
        binding.TextView.text = res[1]
        binding.TextView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, binding.TextView.text.toString())
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, binding.titleToolbar.text.toString())
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, binding.titleToolbar.text.toString()))
        }
        return super.onOptionsItemSelected(item)
    }
}