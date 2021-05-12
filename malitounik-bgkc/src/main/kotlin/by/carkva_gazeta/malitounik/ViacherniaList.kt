package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.AkafistListBinding
import kotlinx.coroutines.*

class ViacherniaList : AppCompatActivity() {
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.viachernia_list)
    private var result = false
    private var mLastClickTime: Long = 0
    private lateinit var binding: AkafistListBinding
    private var resetTollbarJob: Job? = null
    private lateinit var chin: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
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
        binding.titleToolbar.text = resources.getText(R.string.actiox)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
            binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
        binding.ListView.adapter = MenuListAdaprer(this, data)
        binding.ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                when (position) {
                    0 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton1")
                    }
                    1 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton2")
                    }
                    2 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton3")
                    }
                    3 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton4")
                    }
                    4 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton5")
                    }
                    5 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton6")
                    }
                    6 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton7")
                    }
                    7 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_ton8")
                    }
                    8 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_bagarodzichnia_adpushchalnyia")
                    }
                    9 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia1")
                    }
                    10 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia2")
                    }
                    11 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia3")
                    }
                    12 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia4")
                    }
                    13 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia5")
                    }
                    14 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia6")
                    }
                    15 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia7")
                    }
                    16 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia8")
                    }
                    17 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia9")
                    }
                    18 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia10")
                    }
                    19 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia11")
                    }
                    20 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia12")
                    }
                    21 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia13")
                    }
                    22 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia14")
                    }
                    23 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia15")
                    }
                    24 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia16")
                    }
                    25 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia17")
                    }
                    26 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia18")
                    }
                    27 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia19")
                    }
                    28 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia20")
                    }
                    29 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia21")
                    }
                    30 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia22")
                    }
                    31 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia23")
                    }
                    32 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_agulnaia24")
                    }
                    33 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia1")
                    }
                    34 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia2")
                    }
                    35 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia3")
                    }
                    36 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia4")
                    }
                    37 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia5")
                    }
                    38 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia6")
                    }
                    39 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "viachernia_mineia_sviatochnaia7")
                    }
                }
                startActivityForResult(intent, 300)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }
        }
        if (savedInstanceState != null) {
            result = savedInstanceState.getBoolean("result")
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

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300) {
            result = true
            recreate()
        }
    }

    override fun onBackPressed() {
        if (result) onSupportNavigateUp() else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("result", result)
    }
}
