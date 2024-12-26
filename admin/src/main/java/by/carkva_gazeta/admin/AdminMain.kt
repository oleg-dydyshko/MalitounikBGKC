package by.carkva_gazeta.admin

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.hardware.SensorEvent
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.ViewGroup
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminMainBinding
import by.carkva_gazeta.malitounik.BaseActivity
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdminMain : BaseActivity() {
    private lateinit var binding: AdminMainBinding
    private var resetTollbarJob: Job? = null

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SplitCompat.install(this)
        super.onCreate(savedInstanceState)
        binding = AdminMainBinding.inflate(layoutInflater)
        try {
            setContentView(binding.root)
        } catch (t: Resources.NotFoundException) {
            onBack()
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
        binding.bibliateka.setOnClickListener {
            val intent = Intent(this, BibliatekaList::class.java)
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (android.R.id.home == id) {
            onBack()
            return true
        }
        return false
    }
}