package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuListAdaprer
import by.carkva_gazeta.malitounik.SettingsActivity
import kotlinx.android.synthetic.main.bgkc_list.*

class ParafiiBgkcDekanat : AppCompatActivity() {
    private var bgkc = 0
    private var mLastClickTime: Long = 0
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
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bgkc_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.parafii)
        bgkc = intent.getIntExtra("bgkc", 1)
        var data = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.parafii_bgkc_1)
        if (bgkc == 2) {
            data = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.parafii_bgkc_2)
        }
        if (bgkc == 3) {
            data = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.parafii_bgkc_3)
        }
        if (bgkc == 4) {
            data = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.parafii_bgkc_4)
        }
        val adapter = MenuListAdaprer(this, data)
        ListView.adapter = adapter
        ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, ParafiiBgkc::class.java)
            intent.putExtra("bgkc_parafii", position)
            intent.putExtra("bgkc", bgkc)
            startActivityForResult(intent, 25)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null) bgkc = data.getIntExtra("bgkc", 1)
        }
    }
}