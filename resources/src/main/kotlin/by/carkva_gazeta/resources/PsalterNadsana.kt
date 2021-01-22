package by.carkva_gazeta.resources

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.DialogNadsanPravila
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.NadsanPravilaBinding
import kotlinx.coroutines.*

class PsalterNadsana : AppCompatActivity(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private lateinit var binding: NadsanPravilaBinding
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = NadsanPravilaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pNadsana = k.getInt("pravalaNadsana", 1)
        binding.buttonleft.setOnClickListener(this)
        binding.buttonrighth.setOnClickListener(this)
        if (pNadsana == 1) binding.buttonleft.visibility = View.GONE
        if (pNadsana == 5) binding.buttonrighth.visibility = View.GONE
        val ftrans = supportFragmentManager.beginTransaction()
        ftrans.setCustomAnimations(by.carkva_gazeta.malitounik.R.anim.alphainfragment, by.carkva_gazeta.malitounik.R.anim.alphaoutfragment)
        when (pNadsana) {
            1 -> {
                val nadsana1 = PsalterNadsana1(this)
                ftrans.replace(R.id.conteiner, nadsana1)
            }
            2 -> {
                val nadsana2 = PsalterNadsana2(this)
                ftrans.replace(R.id.conteiner, nadsana2)
            }
            3 -> {
                val nadsana3 = PsalterNadsana3(this)
                ftrans.replace(R.id.conteiner, nadsana3)
            }
            4 -> {
                val nadsana4 = PsalterNadsana4(this)
                ftrans.replace(R.id.conteiner, nadsana4)
            }
            5 -> {
                val nadsana5 = PsalterNadsana5(this)
                ftrans.replace(R.id.conteiner, nadsana5)
            }
        }
        ftrans.commit()
        setTollbarTheme()
    }

    override fun onClick(v: View?) {
        var pNadsana = k.getInt("pravalaNadsana", 1)
        val prefEditors = k.edit()
        when (v?.id ?: 0) {
            R.id.buttonleft -> {
                pNadsana -= 1
                prefEditors.putInt("pravalaNadsana", pNadsana)
            }
            R.id.buttonrighth -> {
                pNadsana += 1
                prefEditors.putInt("pravalaNadsana", pNadsana)
            }
        }
        prefEditors.apply()
        val ftrans = supportFragmentManager.beginTransaction()
        ftrans.setCustomAnimations(by.carkva_gazeta.malitounik.R.anim.alphainfragment, by.carkva_gazeta.malitounik.R.anim.alphaoutfragment)
        when (pNadsana) {
            1 -> {
                val nadsana1 = PsalterNadsana1(this)
                ftrans.replace(R.id.conteiner, nadsana1)
            }
            2 -> {
                val nadsana2 = PsalterNadsana2(this)
                ftrans.replace(R.id.conteiner, nadsana2)
            }
            3 -> {
                val nadsana3 = PsalterNadsana3(this)
                ftrans.replace(R.id.conteiner, nadsana3)
            }
            4 -> {
                val nadsana4 = PsalterNadsana4(this)
                ftrans.replace(R.id.conteiner, nadsana4)
            }
            5 -> {
                val nadsana5 = PsalterNadsana5(this)
                ftrans.replace(R.id.conteiner, nadsana5)
            }
        }
        ftrans.commit()
        if (pNadsana == 1) binding.buttonleft.visibility = View.GONE else binding.buttonleft.visibility = View.VISIBLE
        if (pNadsana == 5) binding.buttonrighth.visibility = View.GONE else binding.buttonrighth.visibility = View.VISIBLE
    }

    private fun setTollbarTheme() {
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setText(by.carkva_gazeta.malitounik.R.string.title_psalter_privila)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == by.carkva_gazeta.malitounik.R.id.action_glava) {
            val pravila = DialogNadsanPravila()
            pravila.show(supportFragmentManager, "pravila")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.nadsan_pravila, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }
}