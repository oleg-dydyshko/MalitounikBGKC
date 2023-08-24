package by.carkva_gazeta.admin

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminMainBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class AdminMain : BaseActivity(), DialogUpdateHelp.DialogUpdateHelpListener {
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

    override fun setViersionApp(releaseCode: String, release: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val localFile = File("$filesDir/cache/cache.txt")
                    referens.child("/updateMalitounikBGKC.json").getFile(localFile).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val jsonFile = localFile.readText()
                            val gson = Gson()
                            val type = TypeToken.getParameterized(MutableMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type
                            val updeteArrayText = gson.fromJson<MutableMap<String, String>>(jsonFile, type)
                            if (release) {
                                updeteArrayText["release"] = releaseCode
                            } else {
                                updeteArrayText["devel"] = releaseCode
                            }
                            localFile.writer().use {
                                it.write(gson.toJson(updeteArrayText, type))
                            }
                        } else {
                            MainActivity.toastView(this@AdminMain, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                    referens.child("/updateMalitounikBGKC.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            MainActivity.toastView(this@AdminMain, getString(by.carkva_gazeta.malitounik.R.string.save))
                        } else {
                            MainActivity.toastView(this@AdminMain, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                } catch (e: Throwable) {
                    MainActivity.toastView(this@AdminMain, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (android.R.id.home == id) {
            onBack()
            return true
        }
        if (id == R.id.action_beta) {
            val dialogUpdateHelp = DialogUpdateHelp.newInstance(false)
            dialogUpdateHelp.show(supportFragmentManager, "dialogUpdateHelp")
            return true
        }
        if (id == R.id.action_release) {
            val dialogUpdateHelp = DialogUpdateHelp.newInstance(true)
            dialogUpdateHelp.show(supportFragmentManager, "dialogUpdateHelp")
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_admin_main, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }
}