package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import by.carkva_gazeta.malitounik.databinding.ActivityMainBinding
import by.carkva_gazeta.malitounik.databinding.AppBarMainBinding
import by.carkva_gazeta.malitounik.databinding.ContentMainBinding
import by.carkva_gazeta.malitounik.databinding.ToastBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.roundToLong


class MainActivity : BaseActivity(), View.OnClickListener, DialogContextMenu.DialogContextMenuListener, MenuSviaty.CarkvaCarkvaListener, DialogDelite.DialogDeliteListener, MenuCaliandar.MenuCaliandarPageListinner, DialogFontSize.DialogFontSizeListener, DialogPasxa.DialogPasxaListener, DialogPrazdnik.DialogPrazdnikListener, DialogDeliteAllVybranoe.DialogDeliteAllVybranoeListener, DialogClearHishory.DialogClearHistoryListener {

    private val c = Calendar.getInstance()
    private lateinit var k: SharedPreferences
    private lateinit var prefEditors: SharedPreferences.Editor
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingappbar: AppBarMainBinding
    private lateinit var bindingcontent: ContentMainBinding
    private var idSelect = 0
    private var backPressed: Long = 0
    private var idOld = -1
    private val dzenNoch get() = getBaseDzenNoch()
    private var tolbarTitle = ""
    private var shortcuts = false
    private var mLastClickTime: Long = 0
    private var resetTollbarJob: Job? = null
    private var snackbar: Snackbar? = null
    private val mainActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 300) {
            recreate()
        }
        if (result.resultCode == 200 && supportFragmentManager.findFragmentByTag("menuCaliandar") != null) {
            recreate()
        }
    }
    private val searchSviatyiaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val dayOfYear = intent.getIntExtra("dayOfYear", 0)
                val data = MenuCaliandar.getPositionCaliandar(dayOfYear, c.get(Calendar.YEAR))
                if (setDataCalendar != data[25].toInt()) {
                    setDataCalendar = data[25].toInt()
                    idOld = -1
                    selectFragment(binding.label1, true)
                }
            }
        }
    }

    override fun setDataCalendar(dayOfYear: Int, year: Int) {
        val data = MenuCaliandar.getPositionCaliandar(dayOfYear, year)
        idSelect = R.id.label1
        setDataCalendar = data[25].toInt()
        idOld = -1
        selectFragment(binding.label1, true)
    }

    override fun onDialogFontSize(fontSize: Float) {
        val menuPadryxtoukaDaSpovedzi = supportFragmentManager.findFragmentByTag("MenuPadryxtoukaDaSpovedzi") as? MenuPadryxtoukaDaSpovedzi
        menuPadryxtoukaDaSpovedzi?.onDialogFontSize()
        val menuPamiatka = supportFragmentManager.findFragmentByTag("MenuPamiatka") as? MenuPamiatka
        menuPamiatka?.onDialogFontSize()
    }

    override fun setPage(page: Int) {
        setDataCalendar = page
    }

    override fun fileDeliteCancel() {
        val vybranoe = supportFragmentManager.findFragmentByTag("MenuVybranoe") as? MenuVybranoe
        vybranoe?.fileDeliteCancel()
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.fileDeliteCancel()
    }

    override fun fileDelite(position: Int, file: String) {
        val vybranoe = supportFragmentManager.findFragmentByTag("MenuVybranoe") as? MenuVybranoe
        vybranoe?.fileDelite(position)
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.fileDelite(position)
        val menuCaliandar = supportFragmentManager.findFragmentByTag("menuCaliandar") as? MenuCaliandar
        menuCaliandar?.delitePadzeia(position)
    }

    override fun deliteAllVybranoe() {
        val vybranoe = supportFragmentManager.findFragmentByTag("MenuVybranoe") as? MenuVybranoe
        vybranoe?.deliteAllVybranoe()
    }

    override fun onDialogEditClick(position: Int) {
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.myNatatkiEdit(position)
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.onDialogDeliteClick(position, name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("id", idSelect)
        outState.putInt("idOld", idOld)
    }

    override fun onResume() {
        super.onResume()
        if (checkBrightness) {
            brightness = try {
                Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            } catch (e: Settings.SettingNotFoundException) {
                15
            }
        } else {
            val lp = window.attributes
            lp.screenBrightness = brightness.toFloat() / 100
            window.attributes = lp
        }
        val density = resources.displayMetrics.density

        binding.logosite.post {
            val bd: BitmapDrawable = ContextCompat.getDrawable(this, R.drawable.logotip) as BitmapDrawable
            val imageHeight = bd.bitmap.height / density
            val imageWidth = bd.bitmap.width / density
            val widthDp = binding.logosite.width / density
            val kooficient = widthDp / imageWidth
            val hidch = imageHeight * kooficient
            val layoutParams: ViewGroup.LayoutParams = binding.logosite.layoutParams
            layoutParams.height = (hidch * density).toInt()
            binding.logosite.layoutParams = layoutParams
        }
    }

    private fun ajustCompoundDrawableSizeWithText(textView: TextView, leftDrawable: Drawable?) {
        leftDrawable?.setBounds(0, 0, textView.textSize.toInt(), textView.textSize.toInt())
        textView.setCompoundDrawables(leftDrawable, null, null, null)
    }

    override fun setMyTheme() {
        if (dzenNoch) setTheme(R.style.AppCompatDark)
    }

    /*override fun onSensorChanged(event: SensorEvent?) {
        super.onSensorChanged(event)
        event?.let { sensorEvent ->
            bindingappbar.titleToolbar.text = sensorEvent.values[0].toString()
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", MODE_PRIVATE)
        mkDir()
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingappbar = binding.appBarMain
        bindingcontent = binding.appBarMain.contentMain
        setContentView(binding.root)
        if (savedInstanceState != null) {
            idSelect = savedInstanceState.getInt("id")
            idOld = savedInstanceState.getInt("idOld")
        }
        // Удаление кеша интернета
        val fileSite = File("$filesDir/Site")
        if (fileSite.exists()) fileSite.deleteRecursively()
        // Создание нового формата нататок
        val fileNatatki = File("$filesDir/Natatki.json")
        if (!fileNatatki.exists()) {
            File(filesDir.toString().plus("/Malitva")).walk().forEach { file ->
                if (file.isFile) {
                    val name = file.name
                    val t1 = name.lastIndexOf("_")
                    val index = name.substring(t1 + 1).toLong()
                    val inputStream = FileReader(file)
                    val reader = BufferedReader(inputStream)
                    val res = reader.readText().split("<MEMA></MEMA>")
                    inputStream.close()
                    var lRTE: Long = 1
                    if (res[1].contains("<RTE></RTE>")) {
                        val start = res[1].indexOf("<RTE></RTE>")
                        val end = res[1].length
                        lRTE = res[1].substring(start + 11, end).toLong()
                    }
                    if (lRTE <= 1) {
                        lRTE = file.lastModified()
                    }
                    MenuNatatki.myNatatkiFiles.add(MyNatatkiFiles(index, lRTE, res[0]))
                }
            }
            fileNatatki.writer().use {
                val gson = Gson()
                it.write(gson.toJson(MenuNatatki.myNatatkiFiles))
            }
        }
        bindingappbar.titleToolbar.setOnClickListener {
            val layoutParams = bindingappbar.toolbar.layoutParams
            if (bindingappbar.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                bindingappbar.titleToolbar.isSingleLine = false
                bindingappbar.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                }
            }
        }
        bindingappbar.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        setSupportActionBar(bindingappbar.toolbar)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        idSelect = k.getInt("id", R.id.label1)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, bindingappbar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.label1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label3.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label4.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label5.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label6.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label7.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label8.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label9.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label91.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label92.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label93.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label94.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label95.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label10.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label101.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label102.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label103.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label104.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label105.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label11.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label12.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.label13.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        binding.carkvaLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)

        if (dzenNoch) {
            binding.label91.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label92.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label93.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label94.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label95.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label101.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label102.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label103.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label105.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label104.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            bindingappbar.toolbar.popupTheme = R.style.AppCompatDark
            setMenuIcon(ContextCompat.getDrawable(this, R.drawable.krest_black))
            binding.logosite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logotip_whate))
            binding.label9a.setBackgroundResource(R.drawable.selector_dark)
            binding.label10a.setBackgroundResource(R.drawable.selector_dark)
        } else {
            setMenuIcon(ContextCompat.getDrawable(this, R.drawable.krest))
            binding.label9a.setBackgroundResource(R.drawable.selector_default)
            binding.label10a.setBackgroundResource(R.drawable.selector_default)
        }
        if (k.getInt("sinoidal", 0) == 1) {
            binding.label11.visibility = View.VISIBLE
        }
        binding.title9.setOnClickListener(this)
        binding.title10.setOnClickListener(this)
        binding.label1.setOnClickListener(this)
        binding.label2.setOnClickListener(this)
        binding.label3.setOnClickListener(this)
        binding.label4.setOnClickListener(this)
        binding.label5.setOnClickListener(this)
        binding.label6.setOnClickListener(this)
        binding.label7.setOnClickListener(this)
        binding.label8.setOnClickListener(this)
        binding.label91.setOnClickListener(this)
        binding.label92.setOnClickListener(this)
        binding.label93.setOnClickListener(this)
        binding.label94.setOnClickListener(this)
        binding.label95.setOnClickListener(this)
        binding.label101.setOnClickListener(this)
        binding.label102.setOnClickListener(this)
        binding.label103.setOnClickListener(this)
        binding.label104.setOnClickListener(this)
        binding.label105.setOnClickListener(this)
        binding.label11.setOnClickListener(this)
        binding.label12.setOnClickListener(this)
        binding.label13.setOnClickListener(this)
        binding.label9a.setOnClickListener(this)
        binding.label10a.setOnClickListener(this)

        val data = intent.data
        if (data != null) {
            when {
                data.toString().contains("shortcuts=1") -> {
                    idSelect = R.id.label12
                    selectFragment(binding.label12, true)
                }
                data.toString().contains("shortcuts=3") -> {
                    idSelect = R.id.label7
                    shortcuts = true
                    selectFragment(binding.label7, true)
                }
                data.toString().contains("shortcuts=4") -> {
                    idSelect = R.id.label1
                    shortcuts = true
                    selectFragment(binding.label1, true)
                }
                data.toString().contains("shortcuts=2") -> {
                    idSelect = R.id.label2
                    shortcuts = true
                    selectFragment(binding.label2, true)
                }
                data.toString().contains("caliandar") -> {
                    idSelect = R.id.label1
                    selectFragment(binding.label1, true)
                }
                data.toString().contains("biblija") -> {
                    idSelect = R.id.label8
                    selectFragment(binding.label8, true)
                }
                !data.toString().contains("https://") -> {
                    idSelect = R.id.label2
                    shortcuts = true
                    selectFragment(binding.label2, true)
                }
            }
            intent.data = null
        }
        val extras = intent.extras
        if (extras != null) {
            val widgetday = "widget_day"
            val widgetmun = "widget_mun"

            if (extras.getBoolean(widgetmun, false) && savedInstanceState == null) {
                idSelect = R.id.label1
                setDataCalendar = extras.getInt("position", -1)
            }
            if (extras.getBoolean(widgetday, false) && savedInstanceState == null) {
                idSelect = R.id.label1
                val arrayList = MenuCaliandar.getDataCalaindar(c[Calendar.DATE])
                setDataCalendar = arrayList[0][25].toInt()
            }

            if (extras.getBoolean("sabytie", false)) {
                idSelect = R.id.label1
                val calendar = Calendar.getInstance()
                val chyt = extras.getInt("data")
                val year = extras.getInt("year")
                calendar.set(Calendar.DAY_OF_YEAR, chyt)
                calendar.set(Calendar.YEAR, year)
                val arrayList = MenuCaliandar.getDataCalaindar(mun = calendar[Calendar.MONTH], year = calendar[Calendar.YEAR])
                setDataCalendar = arrayList[calendar[Calendar.DATE] - 1][25].toInt()
            }
        }
        var scroll = false
        when (idSelect) {
            R.id.label1 -> selectFragment(binding.label1, true)
            R.id.label2 -> selectFragment(binding.label2, true)
            R.id.label3 -> selectFragment(binding.label3, true)
            R.id.label4 -> {
                if (!binding.label4.isShown) scroll = true
                selectFragment(binding.label4, true)
            }
            R.id.label5 -> {
                if (!binding.label5.isShown) scroll = true
                selectFragment(binding.label5, true)
            }
            R.id.label6 -> {
                if (!binding.label6.isShown) scroll = true
                selectFragment(binding.label6, true)
            }
            R.id.label7 -> {
                if (!binding.label7.isShown) scroll = true
                selectFragment(binding.label7, true)
            }
            R.id.label8 -> {
                if (!binding.label8.isShown) scroll = true
                selectFragment(binding.label8, true)
            }
            R.id.label91 -> {
                if (!binding.label91.isShown) scroll = true
                selectFragment(binding.label91, true)
            }
            R.id.label92 -> {
                if (!binding.label92.isShown) scroll = true
                selectFragment(binding.label92, true)
            }
            R.id.label93 -> {
                if (!binding.label93.isShown) scroll = true
                selectFragment(binding.label93, true)
            }
            R.id.label94 -> {
                if (!binding.label94.isShown) scroll = true
                selectFragment(binding.label94, true)
            }
            R.id.label95 -> {
                if (!binding.label95.isShown) scroll = true
                selectFragment(binding.label95, true)
            }
            R.id.label101 -> {
                if (!binding.label101.isShown) scroll = true
                selectFragment(binding.label101, true)
            }
            R.id.label102 -> {
                if (!binding.label102.isShown) scroll = true
                selectFragment(binding.label102, true)
            }
            R.id.label103 -> {
                if (!binding.label103.isShown) scroll = true
                selectFragment(binding.label103, true)
            }
            R.id.label104 -> {
                if (!binding.label104.isShown) scroll = true
                selectFragment(binding.label104, true)
            }
            R.id.label105 -> {
                if (!binding.label105.isShown) scroll = true
                selectFragment(binding.label105, true)
            }
            R.id.label11 -> {
                if (!binding.label11.isShown) scroll = true
                selectFragment(binding.label11, true)
            }
            R.id.label12 -> {
                if (!binding.label12.isShown) scroll = true
                selectFragment(binding.label12, true)
            }
            R.id.label13 -> {
                if (!binding.label13.isShown) scroll = true
                selectFragment(binding.label13, true)
            }
            else -> {
                idSelect = R.id.label1
                selectFragment(binding.label1, true)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (padzeia.size == 0) setListPadzeia()
            if (k.getBoolean("setAlarm", true)) {
                withContext(Dispatchers.Main) {
                    getVersionCode()
                }
                val notify = k.getInt("notification", 2)
                SettingsActivity.setNotifications(notify)
                val edit = k.edit()
                edit.putBoolean("setAlarm", false)
                edit.apply()
            }
        }
        if (scroll) binding.scrollView.post { binding.scrollView.smoothScrollBy(0, binding.scrollView.height) }
        //loadSviatyia(2022)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        bindingappbar.titleToolbar.isSelected = false
        bindingappbar.titleToolbar.isSingleLine = true
    }

    private fun mkDir() {
        var dir = File("$filesDir/MaranAtaBel")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/MaranAta")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/Malitva")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/BibliaSemuxaNovyZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/BibliaSinodalNovyZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/BibliaSemuxaStaryZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/BibliaSinodalStaryZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    private fun setMenuIcon(drawable: Drawable?) {
        ajustCompoundDrawableSizeWithText(binding.label1, drawable)
        ajustCompoundDrawableSizeWithText(binding.label2, drawable)
        ajustCompoundDrawableSizeWithText(binding.label3, drawable)
        ajustCompoundDrawableSizeWithText(binding.label4, drawable)
        ajustCompoundDrawableSizeWithText(binding.label5, drawable)
        ajustCompoundDrawableSizeWithText(binding.label6, drawable)
        ajustCompoundDrawableSizeWithText(binding.label7, drawable)
        ajustCompoundDrawableSizeWithText(binding.label8, drawable)
        ajustCompoundDrawableSizeWithText(binding.label9, drawable)
        ajustCompoundDrawableSizeWithText(binding.label10, drawable)
        ajustCompoundDrawableSizeWithText(binding.label11, drawable)
        ajustCompoundDrawableSizeWithText(binding.label12, drawable)
        ajustCompoundDrawableSizeWithText(binding.label13, drawable)
    }

    override fun onBackPressed() {
        if (snackbar?.isShown == true) {
            snackbar?.dismiss()
        }
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                moveTaskToBack(true)
                prefEditors = k.edit()
                for ((key) in k.all) {
                    if (key.contains("Scroll")) {
                        prefEditors.putInt(key, 0)
                    }
                }
                prefEditors.putString("search_svityx_string", "")
                prefEditors.putString("search_string", "")
                prefEditors.putString("search_array", "")
                prefEditors.putInt("search_bible_fierstPosition", 0)
                prefEditors.putInt("search_position", 0)
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.putBoolean("setAlarm", true)
                prefEditors.apply()
                setDataCalendar = -1
                checkBrightness = true
                super.onBackPressed()
            } else {
                backPressed = System.currentTimeMillis()
                toastView(this, getString(R.string.exit))
            }
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun setPasxa(year: Int) {
        val menuPashalii = supportFragmentManager.findFragmentByTag("MenuPashalii") as? MenuPashalii
        menuPashalii?.setPasha(year)
    }

    override fun setPrazdnik(year: Int) {
        val menuCviaty = supportFragmentManager.findFragmentByTag("MenuCviaty") as? MenuSviaty
        menuCviaty?.setCviatyYear(year)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return super.onOptionsItemSelected(item)
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_glava) {
            val arrayList = MenuCaliandar.getDataCalaindar(c[Calendar.DATE])
            setDataCalendar = arrayList[0][25].toInt()
            idOld = -1
            selectFragment(binding.label1, true)
        }
        if (id == R.id.settings) {
            val i = Intent(this, SettingsActivity::class.java)
            mainActivityLauncher.launch(i)
        }
        if (id == R.id.onas) {
            val i = Intent(this@MainActivity, Onas::class.java)
            startActivity(i)
        }
        if (id == R.id.help) {
            val i = Intent(this, Help::class.java)
            startActivity(i)
        }
        if (id == R.id.pasxa_opis) {
            val intent = Intent(this, Pasxa::class.java)
            startActivity(intent)
        }
        if (id == R.id.pasxa) {
            val pasxa = DialogPasxa()
            pasxa.show(supportFragmentManager, "pasxa")
        }
        if (id == R.id.prazdnik) {
            val menuCviaty = supportFragmentManager.findFragmentByTag("MenuCviaty") as? MenuSviaty
            val year = menuCviaty?.getCviatyYear() ?: Calendar.getInstance()[Calendar.YEAR]
            val prazdnik = DialogPrazdnik.getInstance(year)
            prazdnik.show(supportFragmentManager, "prazdnik")
        }
        if (id == R.id.tipicon) {
            val tipicon = DialogTipicon.getInstance(0)
            tipicon.show(supportFragmentManager, "tipicon")
        }
        if (id == R.id.sabytie) {
            val i = Intent(this, Sabytie::class.java)
            startActivity(i)
        }
        if (id == R.id.search_sviatyia) {
            val i = Intent(this, SearchSviatyia::class.java)
            searchSviatyiaLauncher.launch(i)
        }
        if (id == R.id.action_help) {
            val dialogHelpListView = DialogHelpListView.getInstance(1)
            dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun cleanFullHistory() {
        val fragment = supportFragmentManager.findFragmentByTag("menuPesny") as? MenuPesny
        fragment?.cleanFullHistory()
    }

    override fun cleanHistory(position: Int) {
        val fragment = supportFragmentManager.findFragmentByTag("menuPesny") as? MenuPesny
        fragment?.cleanHistory(position)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (!(idSelect == R.id.label9a || idSelect == R.id.label10a)) {
            menu.findItem(R.id.action_add).isVisible = false
            menu.findItem(R.id.action_mun).isVisible = false
            menu.findItem(R.id.action_glava).isVisible = false
            menu.findItem(R.id.tipicon).isVisible = false
            menu.findItem(R.id.pasxa_opis).isVisible = false
            menu.findItem(R.id.pasxa).isVisible = false
            menu.findItem(R.id.trash).isVisible = false
            menu.findItem(R.id.sabytie).isVisible = false
            menu.findItem(R.id.prazdnik).isVisible = false
            menu.findItem(R.id.search_sviatyia).isVisible = false
            menu.findItem(R.id.sortdate).isVisible = false
            menu.findItem(R.id.sorttime).isVisible = false
            menu.findItem(R.id.action_font).isVisible = false
            menu.findItem(R.id.action_bright).isVisible = false
            menu.findItem(R.id.action_dzen_noch).isVisible = false
            menu.findItem(R.id.action_help).isVisible = false
            menu.findItem(R.id.action_carkva).isVisible = false
            when (idSelect) {
                R.id.label101 -> {
                    menu.findItem(R.id.action_font).isVisible = true
                    menu.findItem(R.id.action_bright).isVisible = true
                    menu.findItem(R.id.action_dzen_noch).isVisible = true
                }
                R.id.label102 -> {
                    menu.findItem(R.id.action_font).isVisible = true
                    menu.findItem(R.id.action_bright).isVisible = true
                    menu.findItem(R.id.action_dzen_noch).isVisible = true
                }
                R.id.label103 -> menu.findItem(R.id.prazdnik).isVisible = true
                R.id.label104 -> {
                    menu.findItem(R.id.pasxa_opis).isVisible = true
                    menu.findItem(R.id.pasxa).isVisible = true
                }
                R.id.label7 -> {
                    menu.findItem(R.id.action_add).isVisible = true
                    menu.findItem(R.id.sortdate).isVisible = true
                    menu.findItem(R.id.sorttime).isVisible = true
                    menu.findItem(R.id.action_help).isVisible = true
                    when (k.getInt("natatki_sort", 0)) {
                        1 -> {
                            menu.findItem(R.id.sortdate).isChecked = true
                            menu.findItem(R.id.sorttime).isChecked = false
                        }
                        2 -> {
                            menu.findItem(R.id.sortdate).isChecked = false
                            menu.findItem(R.id.sorttime).isChecked = true
                        }
                        else -> {
                            menu.findItem(R.id.sortdate).isChecked = false
                            menu.findItem(R.id.sorttime).isChecked = false
                        }
                    }
                }
                R.id.label12 -> {
                    menu.findItem(R.id.trash).isVisible = true
                    menu.findItem(R.id.sortdate).isVisible = true
                    menu.findItem(R.id.action_help).isVisible = true
                    menu.findItem(R.id.sortdate).isChecked = k.getInt("vybranoe_sort", 1) == 1
                }
            }
            if (dzenNoch) {
                menu.findItem(R.id.action_mun).setIcon(R.drawable.calendar_black_full)
                menu.findItem(R.id.action_glava).setIcon(R.drawable.calendar_black)
            }
            menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
            if (k.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl: MenuInflater = menuInflater
        infl.inflate(R.menu.main, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private fun selectFragment(view: View?, start: Boolean = false) {
        idSelect = view?.id ?: R.id.label1
        if (!(idSelect == R.id.label9a || idSelect == R.id.label10a)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (dzenNoch) {
                binding.label1.setBackgroundResource(R.drawable.selector_dark)
                binding.label2.setBackgroundResource(R.drawable.selector_dark)
                binding.label3.setBackgroundResource(R.drawable.selector_dark)
                binding.label4.setBackgroundResource(R.drawable.selector_dark)
                binding.label5.setBackgroundResource(R.drawable.selector_dark)
                binding.label6.setBackgroundResource(R.drawable.selector_dark)
                binding.label7.setBackgroundResource(R.drawable.selector_dark)
                binding.label8.setBackgroundResource(R.drawable.selector_dark)
                binding.label91.setBackgroundResource(R.drawable.selector_dark)
                binding.label92.setBackgroundResource(R.drawable.selector_dark)
                binding.label93.setBackgroundResource(R.drawable.selector_dark)
                binding.label94.setBackgroundResource(R.drawable.selector_dark)
                binding.label95.setBackgroundResource(R.drawable.selector_dark)
                binding.label101.setBackgroundResource(R.drawable.selector_dark)
                binding.label102.setBackgroundResource(R.drawable.selector_dark)
                binding.label103.setBackgroundResource(R.drawable.selector_dark)
                binding.label104.setBackgroundResource(R.drawable.selector_dark)
                binding.label105.setBackgroundResource(R.drawable.selector_dark)
                binding.label11.setBackgroundResource(R.drawable.selector_dark)
                binding.label12.setBackgroundResource(R.drawable.selector_dark)
                binding.label13.setBackgroundResource(R.drawable.selector_dark)
            } else {
                binding.label1.setBackgroundResource(R.drawable.selector_default)
                binding.label2.setBackgroundResource(R.drawable.selector_default)
                binding.label3.setBackgroundResource(R.drawable.selector_default)
                binding.label4.setBackgroundResource(R.drawable.selector_default)
                binding.label5.setBackgroundResource(R.drawable.selector_default)
                binding.label6.setBackgroundResource(R.drawable.selector_default)
                binding.label7.setBackgroundResource(R.drawable.selector_default)
                binding.label8.setBackgroundResource(R.drawable.selector_default)
                binding.label91.setBackgroundResource(R.drawable.selector_default)
                binding.label92.setBackgroundResource(R.drawable.selector_default)
                binding.label93.setBackgroundResource(R.drawable.selector_default)
                binding.label94.setBackgroundResource(R.drawable.selector_default)
                binding.label95.setBackgroundResource(R.drawable.selector_default)
                binding.label101.setBackgroundResource(R.drawable.selector_default)
                binding.label102.setBackgroundResource(R.drawable.selector_default)
                binding.label103.setBackgroundResource(R.drawable.selector_default)
                binding.label104.setBackgroundResource(R.drawable.selector_default)
                binding.label105.setBackgroundResource(R.drawable.selector_default)
                binding.label11.setBackgroundResource(R.drawable.selector_default)
                binding.label12.setBackgroundResource(R.drawable.selector_default)
                binding.label13.setBackgroundResource(R.drawable.selector_default)
            }
        }
        prefEditors = k.edit()
        if (idSelect == R.id.label91 || idSelect == R.id.label92 || idSelect == R.id.label93 || idSelect == R.id.label94 || idSelect == R.id.label95) {
            binding.title9.visibility = View.VISIBLE
            if (dzenNoch) binding.image2.setImageResource(R.drawable.arrow_up_float_black)
            else binding.image2.setImageResource(R.drawable.arrow_up_float)
        }
        if (idSelect == R.id.label101 || idSelect == R.id.label102 || idSelect == R.id.label103 || idSelect == R.id.label104 || idSelect == R.id.label105) {
            binding.title10.visibility = View.VISIBLE
            if (dzenNoch) binding.image3.setImageResource(R.drawable.arrow_up_float_black)
            else binding.image3.setImageResource(R.drawable.arrow_up_float)
        }

        if (idSelect == R.id.label9a) {
            if (binding.title9.visibility == View.VISIBLE) {
                binding.title9.visibility = View.GONE
                binding.image2.setImageResource(R.drawable.arrow_down_float)
            } else {
                binding.title9.visibility = View.VISIBLE
                if (dzenNoch) binding.image2.setImageResource(R.drawable.arrow_up_float_black)
                else binding.image2.setImageResource(R.drawable.arrow_up_float)
                binding.scrollView.post {
                    binding.scrollView.smoothScrollBy(0, binding.title9.height)
                }
            }
        }
        if (idSelect == R.id.label10a) {
            if (binding.title10.visibility == View.VISIBLE) {
                binding.title10.visibility = View.GONE
                binding.image3.setImageResource(R.drawable.arrow_down_float)
            } else {
                binding.title10.visibility = View.VISIBLE
                if (dzenNoch) binding.image3.setImageResource(R.drawable.arrow_up_float_black)
                else binding.image3.setImageResource(R.drawable.arrow_up_float)
                binding.scrollView.post {
                    binding.scrollView.smoothScrollBy(0, binding.title10.height)
                }
            }
        }
        when (idSelect) {
            R.id.label1 -> {
                tolbarTitle = getString(R.string.kaliandar2)
                if (dzenNoch) binding.label1.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label1.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label2 -> {
                tolbarTitle = getString(R.string.sajt)
                if (dzenNoch) binding.label2.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label2.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label3 -> {
                tolbarTitle = getString(R.string.liturgikon)
                if (dzenNoch) binding.label3.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label3.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label4 -> {
                tolbarTitle = getString(R.string.malitvy)
                if (dzenNoch) binding.label4.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label4.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label5 -> {
                tolbarTitle = getString(R.string.akafisty)
                if (dzenNoch) binding.label5.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label5.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label6 -> {
                tolbarTitle = getString(R.string.ruzanec)
                if (dzenNoch) binding.label6.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label6.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label7 -> {
                tolbarTitle = getString(R.string.maje_natatki)
                if (dzenNoch) binding.label7.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label7.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label8 -> {
                tolbarTitle = getString(R.string.title_biblia)
                if (dzenNoch) binding.label8.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label8.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label13 -> {
                tolbarTitle = getString(R.string.title_psalter)
                if (dzenNoch) binding.label13.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label13.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label91 -> {
                tolbarTitle = getString(R.string.pesny1)
                if (dzenNoch) binding.label91.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label91.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label92 -> {
                tolbarTitle = getString(R.string.pesny2)
                if (dzenNoch) binding.label92.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label92.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label93 -> {
                tolbarTitle = getString(R.string.pesny3)
                if (dzenNoch) binding.label93.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label93.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label94 -> {
                tolbarTitle = getString(R.string.pesny4)
                if (dzenNoch) binding.label94.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label94.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label95 -> {
                tolbarTitle = getString(R.string.pesny5)
                if (dzenNoch) binding.label95.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label95.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label103 -> {
                tolbarTitle = getString(R.string.carkva_sviaty)
                if (dzenNoch) binding.label103.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label103.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label104 -> {
                tolbarTitle = getString(R.string.kaliandar_bel)
                if (dzenNoch) binding.label104.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label104.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label105 -> {
                tolbarTitle = getString(R.string.parafii)
                if (dzenNoch) binding.label105.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label105.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label102 -> {
                tolbarTitle = getString(R.string.pamiatka)
                if (dzenNoch) binding.label102.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label102.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label101 -> {
                tolbarTitle = getString(R.string.spovedz)
                if (dzenNoch) binding.label101.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label101.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label11 -> {
                tolbarTitle = getString(R.string.bsinaidal)
                if (dzenNoch) binding.label11.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label11.setBackgroundResource(R.drawable.selector_gray)
            }
            R.id.label12 -> {
                tolbarTitle = getString(R.string.MenuVybranoe)
                if (dzenNoch) binding.label12.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label12.setBackgroundResource(R.drawable.selector_gray)
            }
        }

        if (idOld != idSelect) {
            val ftrans = supportFragmentManager.beginTransaction()
            ftrans.setCustomAnimations(R.anim.alphainfragment, R.anim.alphaoutfragment)

            if (idSelect != R.id.label2 && bindingcontent.linear.visibility == View.VISIBLE) bindingcontent.linear.visibility = View.GONE
            when (idSelect) {
                R.id.label1 -> {
                    val arrayList = MenuCaliandar.getDataCalaindar(c[Calendar.DATE])
                    if (setDataCalendar == -1) setDataCalendar = arrayList[0][25].toInt()
                    val caliandar = MenuCaliandar.newInstance(setDataCalendar)
                    ftrans.replace(R.id.conteiner, caliandar, "menuCaliandar")
                    prefEditors.putInt("id", idSelect)
                    if (shortcuts) {
                        val i = Intent(this, Sabytie::class.java)
                        i.putExtra("shortcuts", shortcuts)
                        startActivity(i)
                        shortcuts = false
                    }
                }
                R.id.label2 -> {
                    prefEditors.putInt("id", idSelect)
                    if (shortcuts || intent.extras?.containsKey("site") == true) {
                        if (checkmoduleResources()) {
                            if (checkmodulesBiblijateka()) {
                                val intentBib = Intent()
                                intentBib.setClassName(this, BIBLIOTEKAVIEW)
                                intentBib.data = intent.data
                                if (intent.extras?.containsKey("filePath") == true) intentBib.putExtra("filePath", intent.extras?.getString("filePath"))
                                if (intent.extras?.containsKey("site") == true) intentBib.putExtra("site", true)
                                startActivity(intentBib)
                            } else {
                                downloadDynamicModule(this)
                            }
                        } else {
                            val dadatak = DialogInstallDadatak()
                            dadatak.show(supportFragmentManager, "dadatak")
                        }
                        shortcuts = false
                    }
                    val menuGlavnoe = MenuGlavnoe()
                    ftrans.replace(R.id.conteiner, menuGlavnoe)
                }
                R.id.label3 -> {
                    prefEditors.putInt("id", idSelect)
                    val bogaslus = MenuBogashlugbovya()
                    ftrans.replace(R.id.conteiner, bogaslus)
                }
                R.id.label4 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuMalitvy = MenuMalitvy()
                    ftrans.replace(R.id.conteiner, menuMalitvy)
                }
                R.id.label5 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuAkafisty = MenuAkafisty()
                    ftrans.replace(R.id.conteiner, menuAkafisty)
                }
                R.id.label6 -> {
                    prefEditors.putInt("id", idSelect)
                    val ruzanec = MenuRuzanec()
                    ftrans.replace(R.id.conteiner, ruzanec)
                }
                R.id.label7 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuNatatki = MenuNatatki()
                    ftrans.replace(R.id.conteiner, menuNatatki, "MenuNatatki")
                    shortcuts = false
                }
                R.id.label8 -> {
                    val file = File("$filesDir/BibliaSemuxaNatatki.json")
                    if (file.exists()) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<BibleNatatkiData>>() {}.type
                            BibleGlobalList.natatkiSemuxa = gson.fromJson(file.readText(), type)
                        } catch (t: Throwable) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(file.readText(), type)
                                for (i in 0 until arrayList.size) BibleGlobalList.natatkiSemuxa.add(BibleNatatkiData(i.toLong(), arrayList[i]))
                            } catch (t: Throwable) {
                                file.delete()
                            }
                        }
                    }
                    val file2 = File("$filesDir/BibliaSemuxaZakladki.json")
                    if (file2.exists()) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<BibleZakladkiData>>() {}.type
                            BibleGlobalList.zakladkiSemuxa = gson.fromJson(file2.readText(), type)
                        } catch (t: Throwable) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<String>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<String>>(file2.readText(), type)
                                for (i in 0 until arrayList.size) BibleGlobalList.zakladkiSemuxa.add(BibleZakladkiData(i.toLong(), arrayList[i]))
                            } catch (t: Throwable) {
                                file2.delete()
                            }
                        }
                    }
                    prefEditors.putInt("id", idSelect)
                    prefEditors.putBoolean("novyzavet", false)
                    val semuxa = MenuBibleSemuxa()
                    ftrans.replace(R.id.conteiner, semuxa)
                }
                R.id.label13 -> {
                    prefEditors.putInt("id", idSelect)
                    prefEditors.putBoolean("novyzavet", false)
                    val nadsana = MenuPsalterNadsana()
                    ftrans.replace(R.id.conteiner, nadsana)
                }
                R.id.label91 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesny = MenuPesny.getInstance("prasl")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesny")
                }
                R.id.label92 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesny = MenuPesny.getInstance("bel")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesny")

                }
                R.id.label93 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesny = MenuPesny.getInstance("bag")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesny")
                }
                R.id.label94 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesny = MenuPesny.getInstance("kal")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesny")

                }
                R.id.label95 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesny = MenuPesny.getInstance("taize")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesny")
                }
                R.id.label103 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuCviaty = MenuSviaty()
                    ftrans.replace(R.id.conteiner, menuCviaty, "MenuCviaty")
                }
                R.id.label104 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPashalii = MenuPashalii()
                    ftrans.replace(R.id.conteiner, menuPashalii, "MenuPashalii")
                }
                R.id.label105 -> {
                    prefEditors.putInt("id", idSelect)
                    val parafiiBgkc = MenuParafiiBgkc()
                    ftrans.replace(R.id.conteiner, parafiiBgkc)
                }
                R.id.label102 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPamiatka = MenuPamiatka()
                    ftrans.replace(R.id.conteiner, menuPamiatka, "MenuPamiatka")
                }
                R.id.label101 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPadryxtoukaDaSpovedzi = MenuPadryxtoukaDaSpovedzi()
                    ftrans.replace(R.id.conteiner, menuPadryxtoukaDaSpovedzi, "MenuPadryxtoukaDaSpovedzi")
                }
                R.id.label11 -> {
                    val file = File("$filesDir/BibliaSinodalNatatki.json")
                    if (file.exists()) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<BibleNatatkiData>>() {}.type
                            BibleGlobalList.natatkiSinodal = gson.fromJson(file.readText(), type)
                        } catch (t: Throwable) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(file.readText(), type)
                                for (i in 0 until arrayList.size) BibleGlobalList.natatkiSinodal.add(BibleNatatkiData(i.toLong(), arrayList[i]))
                            } catch (t: Throwable) {
                                file.delete()
                            }
                        }
                    }
                    val file2 = File("$filesDir/BibliaSinodalZakladki.json")
                    if (file2.exists()) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<BibleZakladkiData>>() {}.type
                            BibleGlobalList.zakladkiSinodal = gson.fromJson(file2.readText(), type)
                        } catch (t: Throwable) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<String>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<String>>(file2.readText(), type)
                                for (i in 0 until arrayList.size) BibleGlobalList.zakladkiSinodal.add(BibleZakladkiData(i.toLong(), arrayList[i]))
                            } catch (t: Throwable) {
                                file2.delete()
                            }
                        }
                    }
                    prefEditors.putInt("id", idSelect)
                    prefEditors.putBoolean("novyzavet", false)
                    val sinoidal = MenuBibleSinoidal()
                    ftrans.replace(R.id.conteiner, sinoidal)
                }
                R.id.label12 -> {
                    prefEditors.putInt("id", idSelect)
                    val vybranoe = MenuVybranoe()
                    ftrans.replace(R.id.conteiner, vybranoe, "MenuVybranoe")
                }
                else -> {
                    idSelect = idOld
                    prefEditors.putInt("id", idSelect)
                }
            }
            if (start) {
                ftrans.commit()
            } else {
                bindingappbar.toolbar.postDelayed({ ftrans.commitAllowingStateLoss() }, 300)
            }
            prefEditors.apply()
        }
        bindingappbar.titleToolbar.text = tolbarTitle
        if (idSelect == R.id.label7 || idSelect == R.id.label12) {
            if (k.getBoolean("help_main_list_view", true)) {
                val dialogHelpListView = DialogHelpListView.getInstance(1)
                dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
                prefEditors.putBoolean("help_main_list_view", false)
                prefEditors.apply()
            }
        }
        idOld = idSelect
    }

    override fun onClick(view: View?) {
        selectFragment(view)
    }

    /*private fun loadSviatyia(year: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    try {
                        val sviatyiaNew = Array(366) { Array(4) { "" } }
                        val gregorianCalendar = Calendar.getInstance() as GregorianCalendar
                        val mURL = URL("https://carkva-gazeta.by/admin/getFiles.php?sviatyia=1")
                        val conections = mURL.openConnection() as HttpURLConnection
                        if (conections.responseCode == 200) {
                            val builderUrl = mURL.readText()
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                            val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builderUrl, type)
                            val urlName = arrayList[0][0]
                            val urlTime = arrayList[0][1].toInt()
                            val file = File("$filesDir/calendarsviatyia.txt")
                            val time = file.lastModified() / 1000
                            if (!file.exists() || time < urlTime) {
                                try {
                                    val mURL2 = URL(urlName)
                                    val conections2 = mURL2.openConnection() as HttpURLConnection
                                    if (conections2.responseCode == 200) {
                                        try {
                                            file.writer().use {
                                                it.write(mURL2.readText())
                                            }
                                        } catch (e: Throwable) {
                                        }
                                    }
                                } catch (e: Throwable) {
                                }
                            }
                            var e = 0
                            var e2 = 59
                            file.forEachLine lit@{
                                val re1 = it.split("<>")
                                if (gregorianCalendar.isLeapYear(year)) {
                                    for (i in re1.indices) {
                                        // исправление чтения на 1 января
                                        if (e == 0) sviatyiaNew[e][i] = re1[i].trim().replace("<br>", "\n")
                                        else sviatyiaNew[e][i] = re1[i].trim()
                                    }
                                } else {
                                    if (e == 59) {
                                        e++
                                        return@lit
                                    }
                                    if (e < 59) {
                                        for (i in re1.indices) {
                                            // исправление чтения на 1 января
                                            if (e == 0) sviatyiaNew[e][i] = re1[i].trim().replace("<br>", "\n")
                                            else sviatyiaNew[e][i] = re1[i].trim()
                                        }
                                    } else {
                                        for (i in re1.indices) {
                                            sviatyiaNew[e2][i] = re1[i].trim()
                                        }
                                        e2++
                                    }
                                    e++
                                }
                            }
                        }
                    } catch (e: Throwable) {
                    }
                }
            }
        }
    }*/

    private fun popupSnackbarForCompleteUpdate(code: Int) {
        val c = Calendar.getInstance()
        val updateCode = k.getInt("updateCode", 0)
        if (updateCode != 0 && updateCode != code) {
            val edit = k.edit()
            edit.putInt("updateCount", 0)
            edit.apply()
        }
        var updateCount = k.getInt("updateCount", 0)
        if (updateCount < 3 || c.timeInMillis > k.getLong("updateTime", c.timeInMillis)) {
            snackbar = Snackbar.make(bindingcontent.conteiner, getString(R.string.update_title), Snackbar.LENGTH_INDEFINITE).apply {
                setAction(getString(R.string.update_text)) {
                    val packageName = context.packageName
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                }
                setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorWhite))
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorWhite))
                if (dzenNoch) setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary_black))
                else setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                show()
            }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == Snackbar.Callback.DISMISS_EVENT_MANUAL || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                        updateCount++
                        val edit = k.edit()
                        edit.putInt("updateCount", updateCount)
                        edit.putInt("updateCode", code)
                        if (updateCount >= 3) {
                            c.set(Calendar.HOUR_OF_DAY, 8)
                            c.add(Calendar.DATE, 7)
                            edit.putLong("updateTime", c.timeInMillis)
                        }
                        edit.apply()
                    }
                }
            })
        }
    }

    private fun getVersionCode() {
        if (isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    val updeteArrayText = withContext(Dispatchers.IO) {
                        var updeteArrayText = mapOf<String, String>()
                        try {
                            val mURL = URL("https://carkva-gazeta.by/updateMalitounikBGKC.json")
                            val conections = mURL.openConnection() as HttpURLConnection
                            if (conections.responseCode == 200) {
                                val gson = Gson()
                                val type = object : TypeToken<Map<String, String>>() {}.type
                                updeteArrayText = gson.fromJson(mURL.readText(), type)
                            }
                        } catch (e: Throwable) {
                        }
                        return@withContext updeteArrayText
                    }
                    val currentVersionName = BuildConfig.VERSION_NAME
                    val currentVersionCode = BuildConfig.VERSION_CODE
                    val versionSize = currentVersionName.split(".")
                    if (versionSize.size == 4) {
                        val versionCode = updeteArrayText["devel"]?.toInt() ?: currentVersionCode
                        if (currentVersionCode < versionCode) {
                            popupSnackbarForCompleteUpdate(versionCode)
                        }
                    }
                    if (versionSize.size == 3) {
                        val versionCode = updeteArrayText["release"]?.toInt() ?: currentVersionCode
                        if (currentVersionCode < versionCode) {
                            popupSnackbarForCompleteUpdate(versionCode)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val ADMINMAIN = "by.carkva_gazeta.admin.AdminMain"
        const val ADMINNOVYZAPAVIETSEMUXA = "by.carkva_gazeta.admin.NovyZapavietSemuxa"
        const val ADMINSTARYZAPAVIETSEMUXA = "by.carkva_gazeta.admin.StaryZapavietSemuxa"
        const val ADMINSVIATYIA = "by.carkva_gazeta.admin.Sviatyia"
        const val ADMINSVIATY = "by.carkva_gazeta.admin.Sviaty"
        const val ADMINPIARLINY = "by.carkva_gazeta.admin.Piarliny"
        const val PASOCHNICALIST = "by.carkva_gazeta.admin.PasochnicaList"
        const val BIBLIOTEKAVIEW = "by.carkva_gazeta.biblijateka.BibliotekaView"
        const val CHYTANNE = "by.carkva_gazeta.resources.Chytanne"
        const val MARANATA = "by.carkva_gazeta.resources.MaranAta"
        const val SEARCHBIBLIA = "by.carkva_gazeta.resources.SearchBiblia"
        const val PASLIAPRYCHASCIA = "by.carkva_gazeta.resources.PasliaPrychascia"
        const val BOGASHLUGBOVYA = "by.carkva_gazeta.resources.Bogashlugbovya"
        const val BIBLEZAKLADKI = "by.carkva_gazeta.resources.BibleZakladki"
        const val BIBLENATATKI = "by.carkva_gazeta.resources.BibleNatatki"
        const val SLUGBYVIALIKAGAPOSTUSPIS = "by.carkva_gazeta.resources.SlugbyVialikagaPostuSpis"
        const val MALITVYPRYNAGODNYIA = "by.carkva_gazeta.resources.MalitvyPrynagodnyia"
        const val NADSANMALITVYIPESNI = "by.carkva_gazeta.resources.NadsanMalitvyIPesni"
        const val NADSANMALITVYIPESNILIST = "by.carkva_gazeta.resources.NadsanMalitvyIPesniList"
        const val PSALTERNADSANA = "by.carkva_gazeta.resources.PsalterNadsana"
        const val NADSANCONTENTACTIVITY = "by.carkva_gazeta.resources.NadsanContentActivity"
        const val NOVYZAPAVIETSEMUXA = "by.carkva_gazeta.resources.NovyZapavietSemuxa"
        const val STARYZAPAVIETSEMUXA = "by.carkva_gazeta.resources.StaryZapavietSemuxa"
        const val NOVYZAPAVIETSINAIDAL = "by.carkva_gazeta.resources.NovyZapavietSinaidal"
        const val STARYZAPAVIETSINAIDAL = "by.carkva_gazeta.resources.StaryZapavietSinaidal"
        const val BIBLIAVYBRANOE = "by.carkva_gazeta.resources.BibliaVybranoe"
        var padzeia = ArrayList<Padzeia>()
        var setDataCalendar = -1
        var checkBrightness = true
        private var sessionId = 0
        var brightness = 15
        var dialogVisable = false

        fun setListPadzeia() {
            padzeia.clear()
            val gson = Gson()
            val dir = File(Malitounik.applicationContext().filesDir.toString() + "/Sabytie")
            if (dir.exists()) {
                dir.walk().forEach { file ->
                    if (file.isFile && file.exists()) {
                        val inputStream = FileReader(file)
                        val reader = BufferedReader(inputStream)
                        reader.forEachLine {
                            val line = it.trim()
                            if (line != "") {
                                val t1 = line.split(" ")
                                try {
                                    if (t1.size == 11) padzeia.add(Padzeia(t1[0].replace("_", " "), t1[1], t1[2], t1[3].toLong(), t1[4].toInt(), t1[5], t1[6], t1[7], t1[8].toInt(), t1[9], 0, false)) else padzeia.add(Padzeia(t1[0].replace("_", " "), t1[1], t1[2], t1[3].toLong(), t1[4].toInt(), t1[5], t1[6], t1[7], t1[8].toInt(), t1[9], t1[11].toInt(), false))
                                } catch (e: Throwable) {
                                    file.delete()
                                }
                            }
                        }
                        inputStream.close()
                    }
                }
                val file = File(Malitounik.applicationContext().filesDir.toString() + "/Sabytie.json")
                file.writer().use {
                    it.write(gson.toJson(padzeia))
                }
                dir.deleteRecursively()
            } else {
                val file = File(Malitounik.applicationContext().filesDir.toString() + "/Sabytie.json")
                if (file.exists()) {
                    try {
                        val type = object : TypeToken<ArrayList<Padzeia>>() {}.type
                        padzeia = gson.fromJson(file.readText(), type)
                    } catch (t: Throwable) {
                        file.delete()
                    }
                }
            }
            padzeia.sort()
        }

        fun downloadDynamicModule(context: Activity, moduleName: String = "biblijateka") {
            val progressBarModule = context.findViewById<ProgressBar>(R.id.progressBarModule)
            val layoutDialod = context.findViewById<LinearLayout>(R.id.linear)
            val layoutDialod2 = context.findViewById<LinearLayout>(R.id.linear2)
            val text = context.findViewById<TextView>(R.id.textProgress)
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                layoutDialod2.setBackgroundResource(R.color.colorbackground_material_dark)
                val maduleDownload = context.findViewById<TextView>(R.id.module_download)
                maduleDownload.setBackgroundResource(R.color.colorPrimary_black)
            }
            val splitInstallManager = SplitInstallManagerFactory.create(context)

            val request = SplitInstallRequest.newBuilder().addModule(moduleName).build()

            val listener = SplitInstallStateUpdatedListener {
                val state = it
                if (state.status() == SplitInstallSessionStatus.FAILED) {
                    downloadDynamicModule(context)
                    return@SplitInstallStateUpdatedListener
                }
                if (state.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                    splitInstallManager.startConfirmationDialogForResult(state, context, 150)
                }
                if (state.sessionId() == sessionId) {
                    val bytesDownload = (state.bytesDownloaded() / 1024.0 / 1024.0 * 100.0).roundToLong() / 100.0
                    val total = (state.totalBytesToDownload() / 1024.0 / 1024.0 * 100.0).roundToLong() / 100.0
                    when (state.status()) {
                        SplitInstallSessionStatus.PENDING -> {
                            layoutDialod.visibility = View.VISIBLE
                            text.text = bytesDownload.toString().plus("Мб з ").plus(total).plus("Мб")
                        }
                        SplitInstallSessionStatus.DOWNLOADED -> {
                            layoutDialod.visibility = View.GONE
                            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                        SplitInstallSessionStatus.DOWNLOADING -> {
                            layoutDialod.visibility = View.VISIBLE
                            progressBarModule.max = state.totalBytesToDownload().toInt()
                            progressBarModule.progress = state.bytesDownloaded().toInt()
                            text.text = bytesDownload.toString().plus("Мб з ").plus(total).plus("Мб")
                        }
                        SplitInstallSessionStatus.INSTALLED -> {
                            layoutDialod.visibility = View.GONE
                            SplitInstallHelper.updateAppInfo(context)
                            if (moduleName == "biblijateka") {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val intent = Intent()
                                    intent.setClassName(context, BIBLIOTEKAVIEW)
                                    intent.data = context.intent.data
                                    if (intent.extras?.containsKey("filePath") == true) {
                                        intent.putExtra("filePath", intent.extras?.getString("filePath"))
                                    }
                                    if (intent.extras?.containsKey("site") == true) intent.putExtra("site", true)
                                    context.startActivity(intent)
                                }
                            }
                            if (moduleName == "admin") {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val intent = Intent()
                                    intent.setClassName(context, ADMINMAIN)
                                    context.startActivity(intent)
                                }
                            }
                        }
                        SplitInstallSessionStatus.CANCELED -> {
                        }
                        SplitInstallSessionStatus.CANCELING -> {
                        }
                        SplitInstallSessionStatus.FAILED -> {
                        }
                        SplitInstallSessionStatus.INSTALLING -> {
                        }
                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        }
                        SplitInstallSessionStatus.UNKNOWN -> {
                        }
                    }
                }
            }

            splitInstallManager.registerListener(listener)

            splitInstallManager.startInstall(request).addOnFailureListener {
                if ((it as SplitInstallException).errorCode == SplitInstallErrorCode.NETWORK_ERROR) {
                    toastView(context, context.getString(R.string.no_internet))
                }
            }.addOnSuccessListener {
                sessionId = it
            }
        }

        fun checkmodulesAdmin(): Boolean {
            val muduls = SplitInstallManagerFactory.create(Malitounik.applicationContext()).installedModules
            for (mod in muduls) {
                if (mod == "admin") {
                    return true
                }
            }
            return false
        }

        fun checkmodulesBiblijateka(): Boolean {
            val muduls = SplitInstallManagerFactory.create(Malitounik.applicationContext()).installedModules
            for (mod in muduls) {
                if (mod == "biblijateka") {
                    return true
                }
            }
            return false
        }

        fun checkmoduleResources(): Boolean {
            val muduls = SplitInstallManagerFactory.create(Malitounik.applicationContext()).installedModules
            for (mod in muduls) {
                if (mod == "resources") {
                    return true
                }
            }
            return false
        }

        fun removeZnakiAndSlovy(ctenie: String): String {
            var cytanne = ctenie
            cytanne = cytanne.replace("\n", " ")
            cytanne = cytanne.replace("[", "")
            cytanne = cytanne.replace("?", "")
            cytanne = cytanne.replace("!", "")
            cytanne = cytanne.replace("(", "")
            cytanne = cytanne.replace(")", "")
            cytanne = cytanne.replace("#", "")
            cytanne = cytanne.replace("\"", "")
            cytanne = cytanne.replace(":", "")
            cytanne = cytanne.replace("|", "")
            cytanne = cytanne.replace("]", "")
            cytanne = cytanne.replace("Тон 1.", "")
            cytanne = cytanne.replace("Тон 2.", "")
            cytanne = cytanne.replace("Тон 3.", "")
            cytanne = cytanne.replace("Тон 4.", "")
            cytanne = cytanne.replace("Тон 5.", "")
            cytanne = cytanne.replace("Тон 6.", "")
            cytanne = cytanne.replace("Тон 7.", "")
            cytanne = cytanne.replace("Тон 8.", "")
            cytanne = cytanne.replace("Ганьне", "")
            cytanne = cytanne.replace("Вялікія гадзіны", "")
            cytanne = cytanne.replace("На асьвячэньне вады", "")
            cytanne = cytanne.replace("Багародзіцы", "")
            cytanne = cytanne.replace("Дабравешчаньне", "")
            cytanne = cytanne.replace("Сустрэчы", "")
            cytanne = cytanne.replace("Літургіі няма", "")
            cytanne = cytanne.replace("На вячэрні", "")
            cytanne = cytanne.replace("Строгі пост", "")
            cytanne = cytanne.replace("Вялікі", "")
            cytanne = cytanne.replace("канон", "")
            cytanne = cytanne.replace("сьв.", "")
            cytanne = cytanne.replace("Чын", "")
            cytanne = cytanne.replace("паднясеньня", "")
            cytanne = cytanne.replace("Пачэснага", "")
            cytanne = cytanne.replace("Крыжа", "")
            cytanne = cytanne.replace("Андрэя", "")
            cytanne = cytanne.replace("Крыцкага", "")
            cytanne = cytanne.replace("Літургія", "")
            cytanne = cytanne.replace("раней", "")
            cytanne = cytanne.replace("асьвячаных", "")
            cytanne = cytanne.replace("дароў", "")
            cytanne = cytanne.replace("Яна", "")
            cytanne = cytanne.replace("Яну", "")
            cytanne = cytanne.replace("Залатавуснага", "")
            cytanne = cytanne.replace("сьвятога", "")
            cytanne = cytanne.replace("Васіля", "")
            cytanne = cytanne.replace("Вялікага", "")
            cytanne = cytanne.replace("Блаславеньне", "")
            cytanne = cytanne.replace("вербаў", "")
            cytanne = cytanne.replace("з вячэрняй", "")
            cytanne = cytanne.replace("На ютрані", "")
            cytanne = cytanne.replace("Посту няма", "")
            cytanne = cytanne.replace("Пам.", "")
            cytanne = cytanne.replace("Сьв.", "")
            cytanne = cytanne.replace("Вялеб.", "")
            cytanne = cytanne.replace("Пакл.", "")
            cytanne = cytanne.replace("Багар.", "")
            cytanne = cytanne.replace("Вялікамуч.", "")
            cytanne = cytanne.replace("Ап.", "")
            cytanne = cytanne.replace("Айцам.", "")
            cytanne = cytanne.replace("Прар.", "")
            cytanne = cytanne.replace("Муч.", "")
            cytanne = cytanne.replace("Крыжу", "")
            cytanne = cytanne.replace("Вобр.", "")
            cytanne = cytanne.replace("Новаму году.", "")
            cytanne = cytanne.replace("Вял.", "")
            cytanne = cytanne.replace("Арх.", "")
            cytanne = cytanne.replace("Абнаўл.", "")
            cytanne = cytanne.replace("Сьвятамуч.", "")
            cytanne = cytanne.replace("Саб.", "")
            cytanne = cytanne.replace("Першамуч.", "")
            cytanne = cytanne.replace("Суб.", "")
            cytanne = cytanne.replace("Нядз.", "")
            cytanne = cytanne.trim()
            return cytanne
        }

        fun translateToBelarus(paralelnyia: String): String {
            var paralel = paralelnyia
            paralel = paralel.replace("Быт", "Быц")
            paralel = paralel.replace("Исх", "Вых")
            paralel = paralel.replace("Лев", "Ляв")
            paralel = paralel.replace("Чис", "Лікі")
            paralel = paralel.replace("Втор", "Дрг")
            paralel = paralel.replace("Руфь", "Рут")
            paralel = paralel.replace("1 Пар", "1 Лет")
            paralel = paralel.replace("2 Пар", "2 Лет")
            paralel = paralel.replace("1 Езд", "1 Эзд")
            paralel = paralel.replace("Неем", "Нээм")
            paralel = paralel.replace("2 Езд", "2 Эзд")
            paralel = paralel.replace("Тов", "Тав")
            paralel = paralel.replace("Иудифь", "Юдт")
            paralel = paralel.replace("Есф", "Эст")
            paralel = paralel.replace("Иов", "Ёва")
            paralel = paralel.replace("Притч", "Высл")
            paralel = paralel.replace("Еккл", "Экл")
            paralel = paralel.replace("Песн", "Псн")
            paralel = paralel.replace("Прем", "Мдр")
            paralel = paralel.replace("Сир", "Сір")
            paralel = paralel.replace("Ис", "Іс")
            paralel = paralel.replace("Посл Иер", "Пасл Ер")
            paralel = paralel.replace("Иер", "Ер")
            paralel = paralel.replace("Иез", "Езк")
            paralel = paralel.replace("Ос", "Ас")
            paralel = paralel.replace("Иоил", "Ёіл")
            paralel = paralel.replace("Авд", "Аўдз")
            paralel = paralel.replace("Иона", "Ёны")
            paralel = paralel.replace("Мих", "Міх")
            paralel = paralel.replace("Наум", "Нвм")
            paralel = paralel.replace("Авв", "Абк")
            paralel = paralel.replace("Соф", "Саф")
            paralel = paralel.replace("Агг", "Аг")
            paralel = paralel.replace("3 Езд", "3 Эзд")
            paralel = paralel.replace("Мф", "Мц")
            paralel = paralel.replace("Ин", "Ян")
            paralel = paralel.replace("Деян", "Дз")
            paralel = paralel.replace("Иак", "Як")
            paralel = paralel.replace("1 Пет", "1 Пт")
            paralel = paralel.replace("2 Пет", "2 Пт")
            paralel = paralel.replace("1 Ин", "1 Ян")
            paralel = paralel.replace("2 Ин", "2 Ян")
            paralel = paralel.replace("3 Ин", "3 Ян")
            paralel = paralel.replace("Иуд", "Юд")
            paralel = paralel.replace("Рим", "Рым")
            paralel = paralel.replace("1 Кор", "1 Кар")
            paralel = paralel.replace("2 Кор", "2 Кар")
            paralel = paralel.replace("Еф", "Эф")
            paralel = paralel.replace("Флп", "Плп")
            paralel = paralel.replace("Кол", "Клс")
            paralel = paralel.replace("1 Тим", "1 Цім")
            paralel = paralel.replace("2 Тим", "2 Цім")
            paralel = paralel.replace("Тит", "Ціт")
            paralel = paralel.replace("Евр", "Гбр")
            paralel = paralel.replace("Откр", "Адкр")
            return paralel
        }

        fun zamena(replase: String, ignoreCase: Boolean = true): String {
            var replase1 = replase
            replase1 = replase1.replace("ё", "е", ignoreCase)
            replase1 = replase1.replace("и", "і", ignoreCase)
            replase1 = replase1.replace("щ", "ў", ignoreCase)
            replase1 = replase1.replace("ъ", "'", ignoreCase)
            replase1 = replase1.replace("све", "сьве", ignoreCase)
            replase1 = replase1.replace("сві", "сьві", ignoreCase)
            replase1 = replase1.replace("свя", "сьвя", ignoreCase)
            replase1 = replase1.replace("зве", "зьве", ignoreCase)
            replase1 = replase1.replace("зві", "зьві", ignoreCase)
            replase1 = replase1.replace("звя", "зьвя", ignoreCase)
            replase1 = replase1.replace("зме", "зьме", ignoreCase)
            replase1 = replase1.replace("змі", "зьмі", ignoreCase)
            replase1 = replase1.replace("змя", "зьмя", ignoreCase)
            replase1 = replase1.replace("зня", "зьня", ignoreCase)
            replase1 = replase1.replace("сле", "сьле", ignoreCase)
            replase1 = replase1.replace("слі", "сьлі", ignoreCase)
            replase1 = replase1.replace("сль", "сьль", ignoreCase)
            replase1 = replase1.replace("слю", "сьлю", ignoreCase)
            replase1 = replase1.replace("сля", "сьля", ignoreCase)
            replase1 = replase1.replace("сне", "сьне", ignoreCase)
            replase1 = replase1.replace("сні", "сьні", ignoreCase)
            replase1 = replase1.replace("сню", "сьню", ignoreCase)
            replase1 = replase1.replace("сня", "сьня", ignoreCase)
            replase1 = replase1.replace("спе", "сьпе", ignoreCase)
            replase1 = replase1.replace("спі", "сьпі", ignoreCase)
            replase1 = replase1.replace("спя", "сьпя", ignoreCase)
            replase1 = replase1.replace("сце", "сьце", ignoreCase)
            replase1 = replase1.replace("сці", "сьці", ignoreCase)
            replase1 = replase1.replace("сць", "сьць", ignoreCase)
            replase1 = replase1.replace("сцю", "сьцю", ignoreCase)
            replase1 = replase1.replace("сця", "сьця", ignoreCase)
            replase1 = replase1.replace("цце", "цьце", ignoreCase)
            replase1 = replase1.replace("цці", "цьці", ignoreCase)
            replase1 = replase1.replace("ццю", "цьцю", ignoreCase)
            replase1 = replase1.replace("ззе", "зьзе", ignoreCase)
            replase1 = replase1.replace("ззі", "зьзі", ignoreCase)
            replase1 = replase1.replace("ззю", "зьзю", ignoreCase)
            replase1 = replase1.replace("ззя", "зьзя", ignoreCase)
            replase1 = replase1.replace("зле", "зьле", ignoreCase)
            replase1 = replase1.replace("злі", "зьлі", ignoreCase)
            replase1 = replase1.replace("злю", "зьлю", ignoreCase)
            replase1 = replase1.replace("зля", "зьля", ignoreCase)
            replase1 = replase1.replace("збе", "зьбе", ignoreCase)
            replase1 = replase1.replace("збі", "зьбі", ignoreCase)
            replase1 = replase1.replace("збя", "зьбя", ignoreCase)
            replase1 = replase1.replace("нне", "ньне", ignoreCase)
            replase1 = replase1.replace("нні", "ньні", ignoreCase)
            replase1 = replase1.replace("нню", "ньню", ignoreCase)
            replase1 = replase1.replace("ння", "ньня", ignoreCase)
            replase1 = replase1.replace("лле", "льле", ignoreCase)
            replase1 = replase1.replace("ллі", "льлі", ignoreCase)
            replase1 = replase1.replace("ллю", "льлю", ignoreCase)
            replase1 = replase1.replace("лля", "льля", ignoreCase)
            replase1 = replase1.replace("дск", "дзк", ignoreCase)
            replase1 = replase1.replace("дств", "дзтв", ignoreCase)
            replase1 = replase1.replace("з’е", "зье", ignoreCase)
            replase1 = replase1.replace("з’я", "зья", ignoreCase)
            return replase1
        }

        @Suppress("DEPRECATION")
        fun toastView(context: Context, message: String, toastLength: Int = Toast.LENGTH_SHORT) {
            val layout = ToastBinding.inflate(LayoutInflater.from(context))
            layout.textView.text = message
            val dzenNoch = (context as? BaseActivity)?.getBaseDzenNoch() ?: false
            if (dzenNoch) layout.toastRoot.setBackgroundResource(R.color.colorPrimary_black)
            else layout.toastRoot.setBackgroundResource(R.color.colorPrimary)
            val mes = Toast(context)
            mes.duration = toastLength
            mes.view = layout.root
            mes.show()
        }

        fun fromHtml(html: String, mode: Int = HtmlCompat.FROM_HTML_MODE_LEGACY) = HtmlCompat.fromHtml(html, mode)

        fun toHtml(html: Spannable) = HtmlCompat.toHtml(html, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)

        @Suppress("DEPRECATION")
        fun isNetworkAvailable(isTypeMobile: Boolean = false): Boolean {
            val connectivityManager = Malitounik.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                if (isTypeMobile && actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
                if (!isTypeMobile) {
                    return when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        else -> false
                    }
                }
            } else {
                val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
                if (activeNetwork.isConnectedOrConnecting) {
                    if (isTypeMobile && activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return true
                    if (!isTypeMobile) {
                        return when (activeNetwork.type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            else -> false
                        }
                    }
                }
            }
            return false
        }

        fun createFont(style: Int): Typeface? {
            return when (style) {
                Typeface.BOLD -> ResourcesCompat.getFont(Malitounik.applicationContext(), R.font.robotocondensedbold)
                Typeface.ITALIC -> ResourcesCompat.getFont(Malitounik.applicationContext(), R.font.robotocondenseditalic)
                Typeface.BOLD_ITALIC -> ResourcesCompat.getFont(Malitounik.applicationContext(), R.font.robotocondensedbolditalic)
                else -> ResourcesCompat.getFont(Malitounik.applicationContext(), R.font.robotocondensed)
            }
        }
    }
}
