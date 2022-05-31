package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager

abstract class BaseActivity : PreBaseActivity() {

    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var checkDzenNoch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDarkSlider)
        checkDzenNoch = dzenNoch
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (checkDzenNoch != dzenNoch)
            recreate()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun sensorChangeDzenNoch(isDzenNoch: Boolean) {
        checkDzenNoch = isDzenNoch
        recreate()
    }
}