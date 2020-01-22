package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_bible.*
import java.util.*

class StaryZapaviet2 : AppCompatActivity() {
    private var dzenNoch = false
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        setContentView(R.layout.content_bible)
        val groups = ArrayList<ArrayList<String>>()
        val children1 = ArrayList<String>()
        val children2 = ArrayList<String>()
        val children3 = ArrayList<String>()
        val children4 = ArrayList<String>()
        val children5 = ArrayList<String>()
        val children6 = ArrayList<String>()
        val children7 = ArrayList<String>()
        val children8 = ArrayList<String>()
        val children9 = ArrayList<String>()
        val children10 = ArrayList<String>()
        val children11 = ArrayList<String>()
        val children12 = ArrayList<String>()
        val children13 = ArrayList<String>()
        val children14 = ArrayList<String>()
        val children15 = ArrayList<String>()
        val children16 = ArrayList<String>()
        val children17 = ArrayList<String>()
        val children18 = ArrayList<String>()
        val children19 = ArrayList<String>()
        val children20 = ArrayList<String>()
        val children21 = ArrayList<String>()
        val children22 = ArrayList<String>()
        val children23 = ArrayList<String>()
        val children24 = ArrayList<String>()
        val children25 = ArrayList<String>()
        val children26 = ArrayList<String>()
        val children27 = ArrayList<String>()
        val children28 = ArrayList<String>()
        val children29 = ArrayList<String>()
        val children30 = ArrayList<String>()
        val children31 = ArrayList<String>()
        val children32 = ArrayList<String>()
        val children33 = ArrayList<String>()
        val children34 = ArrayList<String>()
        val children35 = ArrayList<String>()
        val children36 = ArrayList<String>()
        val children37 = ArrayList<String>()
        val children38 = ArrayList<String>()
        val children39 = ArrayList<String>()
        for (i in 1..50) {
            children1.add("Разьдзел $i")
        }
        groups.add(children1)
        for (i in 1..40) {
            children2.add("Разьдзел $i")
        }
        groups.add(children2)
        for (i in 1..27) {
            children3.add("Разьдзел $i")
        }
        groups.add(children3)
        for (i in 1..36) {
            children4.add("Разьдзел $i")
        }
        groups.add(children4)
        for (i in 1..34) {
            children5.add("Разьдзел $i")
        }
        groups.add(children5)
        for (i in 1..24) {
            children6.add("Разьдзел $i")
        }
        groups.add(children6)
        for (i in 1..21) {
            children7.add("Разьдзел $i")
        }
        groups.add(children7)
        for (i in 1..4) {
            children8.add("Разьдзел $i")
        }
        groups.add(children8)
        for (i in 1..31) {
            children9.add("Разьдзел $i")
        }
        groups.add(children9)
        for (i in 1..24) {
            children10.add("Разьдзел $i")
        }
        groups.add(children10)
        for (i in 1..22) {
            children11.add("Разьдзел $i")
        }
        groups.add(children11)
        for (i in 1..25) {
            children12.add("Разьдзел $i")
        }
        groups.add(children12)
        for (i in 1..29) {
            children13.add("Разьдзел $i")
        }
        groups.add(children13)
        for (i in 1..36) {
            children14.add("Разьдзел $i")
        }
        groups.add(children14)
        for (i in 1..10) {
            children15.add("Разьдзел $i")
        }
        groups.add(children15)
        for (i in 1..13) {
            children16.add("Разьдзел $i")
        }
        groups.add(children16)
        for (i in 1..10) {
            children17.add("Разьдзел $i")
        }
        groups.add(children17)
        for (i in 1..42) {
            children18.add("Разьдзел $i")
        }
        groups.add(children18)
        for (i in 1..151) {
            children19.add("Псальма $i")
        }
        groups.add(children19)
        for (i in 1..31) {
            children20.add("Разьдзел $i")
        }
        groups.add(children20)
        for (i in 1..12) {
            children21.add("Разьдзел $i")
        }
        groups.add(children21)
        for (i in 1..8) {
            children22.add("Разьдзел $i")
        }
        groups.add(children22)
        for (i in 1..66) {
            children23.add("Разьдзел $i")
        }
        groups.add(children23)
        for (i in 1..52) {
            children24.add("Разьдзел $i")
        }
        groups.add(children24)
        for (i in 1..5) {
            children25.add("Разьдзел $i")
        }
        groups.add(children25)
        for (i in 1..48) {
            children26.add("Разьдзел $i")
        }
        groups.add(children26)
        for (i in 1..12) {
            children27.add("Разьдзел $i")
        }
        groups.add(children27)
        for (i in 1..14) {
            children28.add("Разьдзел $i")
        }
        groups.add(children28)
        for (i in 1..3) {
            children29.add("Разьдзел $i")
        }
        groups.add(children29)
        for (i in 1..9) {
            children30.add("Разьдзел $i")
        }
        groups.add(children30)
        for (i in 1..1) {
            children31.add("Разьдзел $i")
        }
        groups.add(children31)
        for (i in 1..4) {
            children32.add("Разьдзел $i")
        }
        groups.add(children32)
        for (i in 1..7) {
            children33.add("Разьдзел $i")
        }
        groups.add(children33)
        for (i in 1..3) {
            children34.add("Разьдзел $i")
        }
        groups.add(children34)
        for (i in 1..3) {
            children35.add("Разьдзел $i")
        }
        groups.add(children35)
        for (i in 1..3) {
            children36.add("Разьдзел $i")
        }
        groups.add(children36)
        for (i in 1..2) {
            children37.add("Разьдзел $i")
        }
        groups.add(children37)
        for (i in 1..14) {
            children38.add("Разьдзел $i")
        }
        groups.add(children38)
        for (i in 1..4) {
            children39.add("Разьдзел $i")
        }
        groups.add(children39)
        val adapter = ExpListAdapterStaryZapaviet(this, groups)
        elvMain.setAdapter(adapter)
        elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent(this, Class.forName("by.carkva_gazeta.resources.StaryZapaviet3"))
                intent.putExtra("kniga", groupPosition)
                intent.putExtra("glava", childPosition)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }
            false
        }
        if (intent.extras?.getBoolean("prodolzyt", false) == true) {
            val intent1 = Intent(this, Class.forName("by.carkva_gazeta.resources.StaryZapaviet3"))
            intent1.putExtra("kniga", intent.extras?.getInt("kniga"))
            intent1.putExtra("glava", intent.extras?.getInt("glava"))
            intent1.putExtra("stix", intent.extras?.getInt("stix"))
            startActivity(intent1)
        }
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
        title_toolbar.setText(R.string.stary_zapaviet)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        setTollbarTheme()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }
}