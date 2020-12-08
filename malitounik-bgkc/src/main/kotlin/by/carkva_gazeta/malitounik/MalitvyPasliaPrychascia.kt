package by.carkva_gazeta.malitounik

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
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.AkafistListBinding

class MalitvyPasliaPrychascia : AppCompatActivity() {
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.malitvy_pasli_prychastia)
    private var result = false
    private var mLastClickTime: Long = 0
    private lateinit var binding: AkafistListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        binding = AkafistListBinding.inflate(layoutInflater)
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
        binding.titleToolbar.text = resources.getText(R.string.pasliaPrychscia)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        if (dzenNoch)
            binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else
            binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        binding.ListView.adapter = MenuListAdaprer(this, data)
        binding.ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(this)) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASLIAPRYCHASCIA)
                intent.putExtra("paslia_prychascia", position)
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

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
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