package by.carkva_gazeta.resources

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import kotlinx.android.synthetic.main.nadsan_pravila.*

class PsalterNadsana : AppCompatActivity(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    
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
        setContentView(R.layout.nadsan_pravila)
        val pNadsana = k.getInt("pravalaNadsana", 1)
        buttonleft.setOnClickListener(this)
        buttonrighth.setOnClickListener(this)
        if (pNadsana == 1) buttonleft.visibility = View.GONE
        if (pNadsana == 5) buttonrighth.visibility = View.GONE
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
        if (pNadsana == 1) buttonleft.visibility = View.GONE else buttonleft.visibility = View.VISIBLE
        if (pNadsana == 5) buttonrighth.visibility = View.GONE else buttonrighth.visibility = View.VISIBLE
    }

    private fun setTollbarTheme() {
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setText(by.carkva_gazeta.malitounik.R.string.title_psalter_privila)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }
}