package by.carkva_gazeta.resources

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
import by.carkva_gazeta.malitounik.DialogInstallDadatak
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuListAdaprer
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.AkafistListBinding
import kotlinx.coroutines.*

class NadsanMalitvyIPesniList : AppCompatActivity() {
    private val data: ArrayList<String>
        get() {
            val t = ArrayList<String>()
            for (i in 1..9) t.add(getString(by.carkva_gazeta.malitounik.R.string.pesnia, i))
            return t
        }
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
        binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.pesni)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.ListView.adapter = MenuListAdaprer(this, data)
        binding.ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent(this, NadsanMalitvyIPesni::class.java)
                intent.putExtra("malitva", 2)
                intent.putExtra("pesnia", position + 1)
                startActivity(intent)
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
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed() {
        if (result) onSupportNavigateUp() else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("result", result)
    }
}