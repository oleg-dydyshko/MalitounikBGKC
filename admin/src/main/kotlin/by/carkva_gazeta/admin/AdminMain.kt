package by.carkva_gazeta.admin

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.admin.databinding.AdminMainBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.*

class AdminMain : AppCompatActivity(), DialogUpdateHelp.DialogUpdateHelpListener {
    private lateinit var binding: AdminMainBinding
    private var resetTollbarJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        SplitCompat.install(this)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
        binding = AdminMainBinding.inflate(layoutInflater)
        try {
            setContentView(binding.root)
        } catch (t: Resources.NotFoundException) {
            finish()
            val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
        setTollbarTheme()

        binding.novyZavet.setOnClickListener {
            val intent = Intent(this, NovyZapavietSemuxaList::class.java)
            startActivity(intent)
        }
        binding.staryZavet.setOnClickListener {
            val intent = Intent(this, StaryZapavietSemuxaList::class.java)
            startActivity(intent)
        }
        binding.sviatyia.setOnClickListener {
            val intent = Intent(this, Sviatyia::class.java)
            startActivity(intent)
        }
        binding.pesochnicha.setOnClickListener {
            val intent = Intent(this, PasochnicaList::class.java)
            startActivity(intent)
        }
        binding.sviaty.setOnClickListener {
            val intent = Intent(this, Sviaty::class.java)
            startActivity(intent)
        }
        binding.chytanne.setOnClickListener {
            val intent = Intent(this, Chytanny::class.java)
            startActivity(intent)
        }
        binding.parliny.setOnClickListener {
            val intent = Intent(this, Piarliny::class.java)
            startActivity(intent)
        }
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.site_admin)
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

    override fun onUpdate(error: Boolean) {
        if (error) {
            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
        } else {
            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.save))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_beta) {
            val dialogUpdateHelp = DialogUpdateHelp.newInstance(false)
            dialogUpdateHelp.show(supportFragmentManager, "dialogUpdateHelp")
        }
        if (id == R.id.action_release) {
            val dialogUpdateHelp = DialogUpdateHelp.newInstance(true)
            dialogUpdateHelp.show(supportFragmentManager, "dialogUpdateHelp")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_admin_main, menu)
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
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}