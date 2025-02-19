package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import androidx.core.view.GravityCompat
import androidx.documentfile.provider.DocumentFile
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ActivityMainBinding
import by.carkva_gazeta.malitounik.databinding.AppBarMainBinding
import by.carkva_gazeta.malitounik.databinding.ContentMainBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import by.carkva_gazeta.malitounik.databinding.ToastBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Calendar
import kotlin.random.Random


class MainActivity : SlugbovyiaTextu(), View.OnClickListener, DialogContextMenu.DialogContextMenuListener, MenuSviaty.CarkvaCarkvaListener, DialogDelite.DialogDeliteListener, MenuCaliandar.MenuCaliandarPageListinner, DialogPrazdnik.DialogPrazdnikListener, DialogDeliteAllVybranoe.DialogDeliteAllVybranoeListener, DialogClearHishory.DialogClearHistoryListener, DialogBibliotekaWIFI.DialogBibliotekaWIFIListener, DialogBibliateka.DialogBibliatekaListener, DialogDeliteNiadaunia.DialogDeliteNiadauniaListener, DialogDeliteAllNiadaunia.DialogDeliteAllNiadauniaListener, MyNatatki.MyNatatkiListener, ServiceRadyjoMaryia.ServiceRadyjoMaryiaListener, DialogUpdateWIFI.DialogUpdateListener, MenuBiblijateka.MunuBiblijatekaListener, DialogHelpNotificationApi33.DialogHelpNotificationApi33Listener {

    private val c = Calendar.getInstance()
    private lateinit var k: SharedPreferences
    private lateinit var prefEditors: SharedPreferences.Editor
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingappbar: AppBarMainBinding
    private lateinit var bindingcontent: ContentMainBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private var idSelect = R.id.label1
    private var backPressed: Long = 0
    private val dzenNoch get() = getBaseDzenNoch()
    private var tolbarTitle = ""
    private var mLastClickTime: Long = 0
    private var resetTollbarJob: Job? = null
    private var logJob: Job? = null
    private var versionCodeJob: Job? = null
    private var procentJobFont: Job? = null
    private var snackbar: Snackbar? = null
    private var isConnectServise = false
    private var mRadyjoMaryiaService: ServiceRadyjoMaryia? = null
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
            val dayOfYear = intent?.getIntExtra("dayOfYear", 0) ?: 0
            val data = MenuCaliandar.getPositionCaliandar(dayOfYear, c.get(Calendar.YEAR))
            if (setDataCalendar != data[25].toInt()) {
                setDataCalendar = data[25].toInt()
                val menuCaliandar = supportFragmentManager.findFragmentByTag("menuCaliandar") as? MenuCaliandar
                menuCaliandar?.setPage(setDataCalendar) ?: selectFragment(binding.label1, true)
            }
        }
    }
    private val updateMalitounikLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val dialog = DialogUpdateMalitounik.getInstance(getString(R.string.update_title))
            dialog.show(supportFragmentManager, "DialogUpdateMalitounik")
        } else {
            onUpdateNegativeWIFI()
        }
    }
    private val setFileBiblijatekaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectFragment(binding.label140, true)
            val fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$NIADAUNIA") as? MenuBiblijateka
            fragment?.loadNiadaunia()
        }
    }
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val intent = Intent(this, BiblijatekaPdf::class.java)
                intent.data = uri
                val fileName = getFileName(uri)
                val title = getTitle(fileName)
                intent.putExtra("fileTitle", title)
                intent.putExtra("fileName", fileName)
                saveNaidauniaBiblijateka(fileName, uri)
                setFileBiblijatekaLauncher.launch(intent)
            }
        }
    }
    val listBibliaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 500) {
            val myPerevod = result.data?.extras?.getString("perevod") ?: VybranoeBibleList.PEREVODSEMUXI
            when (myPerevod) {
                VybranoeBibleList.PEREVODSEMUXI -> {
                    val fragment = supportFragmentManager.findFragmentByTag("semuxa")
                    if (fragment == null) {
                        selectFragment(binding.label8)
                    }
                }
                VybranoeBibleList.PEREVODBOKUNA -> {
                    val fragment = supportFragmentManager.findFragmentByTag("bokuna")
                    if (fragment == null) {
                        selectFragment(binding.label16)
                    }
                }
                VybranoeBibleList.PEREVODCARNIAUSKI -> {
                    val fragment = supportFragmentManager.findFragmentByTag("carniauski")
                    if (fragment == null) {
                        selectFragment(binding.label17)
                    }
                }
                VybranoeBibleList.PEREVODNADSAN -> {
                    val fragment = supportFragmentManager.findFragmentByTag("nadsana")
                    if (fragment == null) {
                        selectFragment(binding.label13)
                    }
                }
                VybranoeBibleList.PEREVODSINOIDAL -> {
                    val fragment = supportFragmentManager.findFragmentByTag("sinoidal")
                    if (fragment == null) {
                        selectFragment(binding.label11)
                    }
                }
            }
        }
    }
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ServiceRadyjoMaryia.ServiceRadyjoMaryiaBinder
            mRadyjoMaryiaService = binder.getService()
            mRadyjoMaryiaService?.setServiceRadyjoMaryiaListener(this@MainActivity)
            isConnectServise = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnectServise = false
            mRadyjoMaryiaService = null
        }
    }
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        val dialog = supportFragmentManager.findFragmentByTag("DialogUpdateMalitounik") as? DialogUpdateMalitounik
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownload = state.bytesDownloaded()
            val total = state.totalBytesToDownload()
            dialog?.updateProgress(total.toDouble(), bytesDownload.toDouble())
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            dialog?.updateComplete()
            completeUpdate()
        }
    }
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            SettingsActivity.setNotifications(k.getInt("notification", SettingsActivity.NOTIFICATION_SVIATY_FULL))
        }
    }

    override fun onStop() {
        super.onStop()
        if (isConnectServise) {
            unbindService(mConnection)
        }
        mRadyjoMaryiaService = null
        binding.label15b.visibility = View.GONE
        isConnectServise = false
    }

    override fun onStart() {
        super.onStart()
        if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
            val intent = Intent(this, ServiceRadyjoMaryia::class.java)
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            if (ServiceRadyjoMaryia.isPlayingRadyjoMaryia) binding.image5.setImageResource(R.drawable.pause2)
            binding.label15b.visibility = View.VISIBLE
            binding.label15b.text = ServiceRadyjoMaryia.titleRadyjoMaryia
        }
    }

    override fun onPause() {
        super.onPause()
        logJob?.cancel()
        versionCodeJob?.cancel()
    }

    override fun setDataCalendar(dayOfYear: Int, year: Int) {
        val data = MenuCaliandar.getPositionCaliandar(dayOfYear, year)
        idSelect = R.id.label1
        setDataCalendar = data[25].toInt()
        val menuCaliandar = supportFragmentManager.findFragmentByTag("menuCaliandar") as? MenuCaliandar
        if (menuCaliandar == null) {
            selectFragment(binding.label1, true)
        } else {
            menuCaliandar.setPage(setDataCalendar)
        }
    }

    private fun onDialogFontSize() {
        val menuPadryxtoukaDaSpovedzi = supportFragmentManager.findFragmentByTag("MenuPadryxtoukaDaSpovedzi") as? MenuPadryxtoukaDaSpovedzi
        menuPadryxtoukaDaSpovedzi?.onDialogFontSize()
        val menuPamiatka = supportFragmentManager.findFragmentByTag("MenuPamiatka") as? MenuPamiatka
        menuPamiatka?.onDialogFontSize()
    }

    private fun setFontDialog() {
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
        }
        startProcent()
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

    override fun myNatatkiAdd() {
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.myNatatkiAdd()
    }

    override fun myNatatkiEdit(position: Int) {
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.myNatatkiEdit(position)
    }

    override fun fileDelite(position: Int, file: String) {
        val vybranoe = supportFragmentManager.findFragmentByTag("MenuVybranoe") as? MenuVybranoe
        vybranoe?.fileDelite(position)
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.fileDelite(position)
        val menuCaliandar = supportFragmentManager.findFragmentByTag("menuCaliandar") as? MenuCaliandar
        menuCaliandar?.delitePadzeia(position)
        val menuBiblijateka = findMenuBiblijateka()
        menuBiblijateka?.fileDelite(file)
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

    override fun onDialogbibliatekaPositiveClick(listPosition: String) {
        val fragment = findMenuBiblijateka()
        fragment?.onDialogbibliatekaPositiveClick(listPosition)
    }

    override fun onDialogPositiveClick(listPosition: String) {
        val fragment = findMenuBiblijateka()
        fragment?.onDialogPositiveClick(listPosition)
    }

    override fun onDialogNegativeClick() {
    }

    override fun delAllNiadaunia() {
        val fragment = findMenuBiblijateka()
        fragment?.delAllNiadaunia()
    }

    override fun deliteNiadaunia(fileName: String) {
        val fragment = findMenuBiblijateka()
        fragment?.deliteNiadaunia(fileName)
    }

    private fun findMenuBiblijateka(): MenuBiblijateka? {
        var fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$NIADAUNIA") as? MenuBiblijateka
        if (fragment != null) return fragment
        fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$NIADAUNIA") as? MenuBiblijateka
        if (fragment != null) return fragment
        fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$GISTORYIACARKVY") as? MenuBiblijateka
        if (fragment != null) return fragment
        fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$MALITOUNIKI") as? MenuBiblijateka
        if (fragment != null) return fragment
        fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$SPEUNIKI") as? MenuBiblijateka
        if (fragment != null) return fragment
        fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$RELLITARATURA") as? MenuBiblijateka
        if (fragment != null) return fragment
        fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$PDF") as? MenuBiblijateka
        if (fragment != null) return fragment
        return null
    }

    override fun onResume() {
        super.onResume()
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                completeUpdate()
            }
        }/*val bytesDownload = "12000548".toDouble()
        val total = "25025024".toDouble()
        val dialog = supportFragmentManager.findFragmentByTag("DialogUpdateMalitounik") as? DialogUpdateMalitounik
        dialog?.updateProgress(total, bytesDownload)*/

        /*val density = resources.displayMetrics.density

        binding.logosite.post {
            val bd = ContextCompat.getDrawable(this, R.drawable.logotip) as BitmapDrawable
            val imageHeight = bd.bitmap.height / density
            val imageWidth = bd.bitmap.width / density
            val widthDp = binding.logosite.width / density
            val kooficient = widthDp / imageWidth
            val hidch = imageHeight * kooficient
            val layoutParams = binding.logosite.layoutParams
            layoutParams.height = (hidch * density).toInt()
            binding.logosite.layoutParams = layoutParams
        }*/
    }

    private fun ajustCompoundDrawableSizeWithText(textView: TextView, leftDrawable: Drawable?) {
        leftDrawable?.setBounds(0, 0, textView.textSize.toInt(), textView.textSize.toInt())
        textView.setCompoundDrawables(leftDrawable, null, null, null)
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
        prefEditors = k.edit()
        // Удаление "фантомных" виджетов
        /*val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisAppWidget = ComponentName(packageName, Widget::class.java.name)
        val host = AppWidgetHost(this, 0)
        appWidgetManager.getAppWidgetIds(thisAppWidget).forEach {
            //host.deleteAppWidgetId(it)
            Log.d("Oleg", it.toString())
        }*/
        mkDir()
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingappbar = binding.appBarMain
        bindingcontent = binding.appBarMain.contentMain
        bindingprogress = bindingcontent.progressView
        setContentView(binding.root) // Удаление старого выделения
        val dirMaranAtaBel = File("$filesDir/MaranAtaBel")
        if (dirMaranAtaBel.exists()) dirMaranAtaBel.deleteRecursively()
        val dirMaranAta = File("$filesDir/MaranAta")
        if (dirMaranAta.exists()) dirMaranAta.deleteRecursively() // Удаление кеша интернета
        val fileSite = File("$filesDir/Site")
        if (fileSite.exists()) fileSite.deleteRecursively() // Создание нового формата нататок
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
                val type = TypeToken.getParameterized(ArrayList::class.java, MyNatatkiFiles::class.java).type
                it.write(gson.toJson(MenuNatatki.myNatatkiFiles, type))
            }
        }
        bindingappbar.titleToolbar.setOnClickListener {
            val layoutParams = bindingappbar.toolbar.layoutParams
            if (bindingappbar.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                bindingappbar.titleToolbar.isSingleLine = false
                bindingappbar.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(bindingappbar.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(bindingappbar.toolbar)
        }
        setSupportActionBar(bindingappbar.toolbar)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        idSelect = k.getInt("id", R.id.label1)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, bindingappbar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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
            binding.label140.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label141.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label142.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label143.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label144.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label145.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label148.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            binding.label15b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            bindingappbar.toolbar.popupTheme = R.style.AppCompatDark
            setMenuIcon(ContextCompat.getDrawable(this, R.drawable.krest_black))
            binding.label9a.setBackgroundResource(R.drawable.selector_dark)
            binding.label10a.setBackgroundResource(R.drawable.selector_dark)
            binding.label14a.setBackgroundResource(R.drawable.selector_dark)
            binding.image5.setBackgroundResource(R.drawable.selector_dark)
            binding.image6.setBackgroundResource(R.drawable.selector_dark)
            binding.image7.setBackgroundResource(R.drawable.selector_dark)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, R.drawable.selector_progress_noch)
        } else {
            setMenuIcon(ContextCompat.getDrawable(this, R.drawable.krest))
            binding.label9a.setBackgroundResource(R.drawable.selector_default)
            binding.label10a.setBackgroundResource(R.drawable.selector_default)
            binding.label14a.setBackgroundResource(R.drawable.selector_default)
            binding.image5.setBackgroundResource(R.drawable.selector_default)
            binding.image6.setBackgroundResource(R.drawable.selector_default)
            binding.image7.setBackgroundResource(R.drawable.selector_default)
        }
        if (k.getInt("sinoidal", 0) == 1) {
            binding.label11.visibility = View.VISIBLE
        }
        binding.title9.setOnClickListener(this)
        binding.title10.setOnClickListener(this)
        binding.label1.setOnClickListener(this)
        binding.label3.setOnClickListener(this)
        binding.label4.setOnClickListener(this)
        binding.label5.setOnClickListener(this)
        binding.label6.setOnClickListener(this)
        binding.label7.setOnClickListener(this)
        binding.label8.setOnClickListener(this)
        binding.label16.setOnClickListener(this)
        binding.label17.setOnClickListener(this)
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
        binding.label14a.setOnClickListener(this)
        binding.label140.setOnClickListener(this)
        binding.label141.setOnClickListener(this)
        binding.label142.setOnClickListener(this)
        binding.label143.setOnClickListener(this)
        binding.label144.setOnClickListener(this)
        binding.label145.setOnClickListener(this)
        binding.label148.setOnClickListener(this)
        binding.image5.setOnClickListener(this)
        binding.image6.setOnClickListener(this)
        binding.image7.setOnClickListener(this)
        binding.citata.setOnClickListener(this)

        val data = intent.data
        if (data != null) {
            when {
                data.toString().contains("shortcuts=1") -> {
                    idSelect = R.id.label12
                    selectFragment(binding.label12, true)
                }

                data.toString().contains("shortcuts=3") -> {
                    idSelect = R.id.label7
                    selectFragment(binding.label7, true, shortcuts = true)
                }

                data.toString().contains("shortcuts=4") -> {
                    idSelect = R.id.label1
                    selectFragment(binding.label1, true, shortcuts = true)
                }

                data.toString().contains("shortcuts=2") -> {
                    if (isNiadaunia()) {
                        idSelect = R.id.label140
                        selectFragment(binding.label140, true, shortcuts = true)
                    } else {
                        idSelect = R.id.label142
                        selectFragment(binding.label142, true, shortcuts = true)
                    }
                }

                data.scheme == "content" -> {
                    idSelect = R.id.label140
                    selectFragment(binding.label140, true, shortcuts = true, intent.data)
                    intent.data = null
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
        val inputStream = resources.openRawResource(R.raw.citata)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val citataList = ArrayList<Spannable>()
        reader.forEachLine {
            val line = SpannableStringBuilder()
            val t1 = it.indexOf("(")
            if (t1 != -1) {
                line.append(it.substring(0, t1).trim())
                line.append("\n")
                line.append(it.substring(t1))
                citataList.add(line.toSpannable())
            }
        }
        binding.citata.text = citataList[Random.nextInt(citataList.size)].apply {
            setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this@MainActivity, R.font.andantinoscript)), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (dzenNoch) {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary_black)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.colorDivider)), 1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(AbsoluteSizeSpan(30, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this@MainActivity, R.font.comici)), 1, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
        if (k.getInt("fontInterface", 1) > 0) {
            binding.citata.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOOLBAR)
        }
        var scroll = false
        when (idSelect) {
            R.id.label1 -> selectFragment(binding.label1, true)
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

            R.id.label16 -> {
                if (!binding.label16.isShown) scroll = true
                selectFragment(binding.label16, true)
            }

            R.id.label17 -> {
                if (!binding.label17.isShown) scroll = true
                selectFragment(binding.label17, true)
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

            R.id.label140 -> {
                if (!binding.label140.isShown) scroll = true
                selectFragment(binding.label140, true)
            }

            R.id.label141 -> {
                if (!binding.label141.isShown) scroll = true
                selectFragment(binding.label141, true)
            }

            R.id.label142 -> {
                if (!binding.label142.isShown) scroll = true
                selectFragment(binding.label142, true)
            }

            R.id.label143 -> {
                if (!binding.label143.isShown) scroll = true
                selectFragment(binding.label143, true)
            }

            R.id.label144 -> {
                if (!binding.label144.isShown) scroll = true
                selectFragment(binding.label144, true)
            }

            R.id.label145 -> {
                if (!binding.label140.isShown) scroll = true
                selectFragment(binding.label140, true)
            }

            R.id.label148 -> {
                if (!binding.label148.isShown) scroll = true
                selectFragment(binding.label148, true)
            }

            else -> {
                idSelect = R.id.label1
                selectFragment(binding.label1, true)
            }
        }
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize()
                }
                startProcent()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        CoroutineScope(Dispatchers.IO).launch {
            if (k.getBoolean("admin", false) && isNetworkAvailable()) {
                val localFile = File("$filesDir/cache/cache.txt")
                Malitounik.referens.child("/admin/log.txt").getFile(localFile).addOnFailureListener {
                    toastView(this@MainActivity, getString(R.string.error))
                }.await()
                val log = localFile.readText()
                if (log != "") {
                    withContext(Dispatchers.Main) {
                        toastView(this@MainActivity, getString(R.string.check_update_resourse))
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            cacheDir?.listFiles()?.forEach {
                it?.deleteRecursively()
            }
            if (padzeia.size == 0) setListPadzeia()
            if (k.getBoolean("setAlarm", true)) {
                checkUpdateMalitounik()
                val notify = k.getInt("notification", SettingsActivity.NOTIFICATION_SVIATY_FULL)
                SettingsActivity.setNotifications(notify)
                prefEditors.putBoolean("setAlarm", false)
                prefEditors.apply()
            }
        }
        if (scroll) binding.scrollView.post {
            binding.scrollView.smoothScrollBy(0, binding.scrollView.height)
        }

        if (checkASztoNovagaMD5Sum()) {
            val dialog = DialogSztoHovaha()
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "DialogSztoHovaha")
            prefEditors.putInt("chtoNavaha", BuildConfig.VERSION_CODE)
            prefEditors.apply()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                if (k.getBoolean("permissionNotificationApi33", true) && supportFragmentManager.findFragmentByTag("dialogHelpNotificationApi33") == null) {
                    val dialogHelpNotificationApi33 = DialogHelpNotificationApi33.getInstance(SettingsActivity.NOTIFICATION_SVIATY_FULL)
                    dialogHelpNotificationApi33.isCancelable = false
                    dialogHelpNotificationApi33.show(supportFragmentManager, "dialogHelpNotificationApi33")
                }
            }
        }
    }

    private fun startProcent() {
        procentJobFont?.cancel()
        bindingprogress.progressFont.visibility = View.VISIBLE
        procentJobFont = CoroutineScope(Dispatchers.Main).launch {
            dialogVisable = true
            delay(2000)
            bindingprogress.progressFont.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
                dialogVisable = false
            }
        }
    }

    override fun onDialogHelpNotificationApi33(notification: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            prefEditors.putInt("notification", notification)
            prefEditors.putBoolean("permissionNotificationApi33", false)
            prefEditors.apply()
            mPermissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onDialogHelpNotificationApi33Cansel() {
        prefEditors.putBoolean("permissionNotificationApi33", false)
        prefEditors.apply()
    }

    private fun checkASztoNovagaMD5Sum(): Boolean {
        var st: String
        val inputStream = resources.openRawResource(R.raw.a_szto_novaha)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        reader.use { bufferedReader ->
            st = bufferedReader.readText()
        }
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(st.toByteArray())
        val digest = messageDigest.digest()
        val bigInt = BigInteger(1, digest)
        val md5Hex = StringBuilder(bigInt.toString(16))
        while (md5Hex.length < 32) {
            md5Hex.insert(0, "0")
        }
        val checkMD5 = k.getString("chtoNavahaMD5", "0")
        val edit = k.edit()
        edit.putString("chtoNavahaMD5", md5Hex.toString())
        edit.remove("chtoNavaha")
        edit.apply()
        return checkMD5 != md5Hex.toString()
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams?) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams?.height = actionBarHeight
        }
        bindingappbar.titleToolbar.isSelected = false
        bindingappbar.titleToolbar.isSingleLine = true
    }

    private fun mkDir() {
        var dir = File("$filesDir/Malitva")
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
        dir = File("$filesDir/BibliaBokunaNovyZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/BibliaCarniauskiNovyZavet")
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
        dir = File("$filesDir/BibliaBokunaStaryZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/BibliaCarniauskiStaryZavet")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/cache")
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    private fun setMenuIcon(drawable: Drawable?) {
        ajustCompoundDrawableSizeWithText(binding.label1, drawable)
        ajustCompoundDrawableSizeWithText(binding.label3, drawable)
        ajustCompoundDrawableSizeWithText(binding.label4, drawable)
        ajustCompoundDrawableSizeWithText(binding.label5, drawable)
        ajustCompoundDrawableSizeWithText(binding.label6, drawable)
        ajustCompoundDrawableSizeWithText(binding.label7, drawable)
        ajustCompoundDrawableSizeWithText(binding.label8, drawable)
        ajustCompoundDrawableSizeWithText(binding.label16, drawable)
        ajustCompoundDrawableSizeWithText(binding.label17, drawable)
        ajustCompoundDrawableSizeWithText(binding.label9, drawable)
        ajustCompoundDrawableSizeWithText(binding.label10, drawable)
        ajustCompoundDrawableSizeWithText(binding.label11, drawable)
        ajustCompoundDrawableSizeWithText(binding.label12, drawable)
        ajustCompoundDrawableSizeWithText(binding.label13, drawable)
        ajustCompoundDrawableSizeWithText(binding.label14, drawable)
        ajustCompoundDrawableSizeWithText(binding.label15, drawable)
    }

    override fun onBack() {
        if (snackbar?.isShown == true) {
            snackbar?.dismiss()
        }
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (backPressed + 2000 > System.currentTimeMillis()) {
                moveTaskToBack(true)
                for ((key) in k.all) {
                    if (key.contains("Scroll") || key.contains("position")) {
                        prefEditors.remove(key)
                    }
                }
                prefEditors.remove("search_svityx_string")
                prefEditors.remove("search_string")
                prefEditors.remove("search_array")
                prefEditors.remove("search_bogashugbovya_string")
                prefEditors.remove("search_bogashugbovya_array")
                prefEditors.remove("search_bible_fierstPosition")
                prefEditors.remove("search_position")
                prefEditors.putBoolean("BibliotekaUpdate", false)
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.putBoolean("setAlarm", true)
                prefEditors.apply()
                val dir = File("$filesDir/cache")
                val list = dir.listFiles()
                list?.forEach {
                    it.delete()
                }
                super.onBack()
            } else {
                backPressed = System.currentTimeMillis()
                toastView(this, getString(R.string.exit))
            }
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun setPrazdnik(year: Int) {
        val menuCviaty = supportFragmentManager.findFragmentByTag("MenuCviaty") as? MenuSviaty
        menuCviaty?.setCviatyYear(year)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_font) {
            setFontDialog()
            return true
        }
        if (id == R.id.action_glava) {
            val arrayList = MenuCaliandar.getDataCalaindar(c[Calendar.DATE])
            setDataCalendar = arrayList[0][25].toInt()
            val menuCaliandar = supportFragmentManager.findFragmentByTag("menuCaliandar") as? MenuCaliandar
            if (menuCaliandar == null) {
                selectFragment(binding.label1, true)
            } else {
                menuCaliandar.setPage(setDataCalendar)
            }
            return true
        }
        if (id == R.id.settings) {
            val i = Intent(this, SettingsActivity::class.java)
            mainActivityLauncher.launch(i)
            return true
        }
        if (id == R.id.onas) {
            val i = Intent(this@MainActivity, Onas::class.java)
            startActivity(i)
            return true
        }
        if (id == R.id.help) {
            val i = Intent(this, Help::class.java)
            startActivity(i)
            return true
        }
        if (id == R.id.prazdnik) {
            val menuCviaty = supportFragmentManager.findFragmentByTag("MenuCviaty") as? MenuSviaty
            val year = menuCviaty?.getCviatyYear() ?: Calendar.getInstance()[Calendar.YEAR]
            val prazdnik = DialogPrazdnik.getInstance(year)
            prazdnik.show(supportFragmentManager, "prazdnik")
            return true
        }
        if (id == R.id.tipicon) {
            val tipicon = DialogTipicon()
            tipicon.show(supportFragmentManager, "tipicon")
            return true
        }
        if (id == R.id.sabytie) {
            val i = Intent(this, Sabytie::class.java)
            startActivity(i)
            return true
        }
        if (id == R.id.search_sviatyia) {
            val i = Intent(this, SearchSviatyia::class.java)
            searchSviatyiaLauncher.launch(i)
            return true
        }
        if (id == R.id.action_log) {
            if (checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(this, LOGVIEW)
                startActivity(intent)
            }
        }
        if (id == R.id.action_update) {
            val appUpdateManager = AppUpdateManagerFactory.create(this)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    if (isNetworkAvailable(TRANSPORT_CELLULAR)) {
                        val dialog = DialogUpdateWIFI.getInstance(appUpdateInfo.totalBytesToDownload().toFloat())
                        dialog.show(supportFragmentManager, "DialogUpdateWIFI")
                    } else {
                        appUpdateManager.registerListener(installStateUpdatedListener)
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, updateMalitounikLauncher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                    }
                }
            }
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_add).isVisible = false
        menu.findItem(R.id.action_mun).isVisible = false
        menu.findItem(R.id.action_glava).isVisible = false
        menu.findItem(R.id.tipicon).isVisible = false
        menu.findItem(R.id.trash).isVisible = false
        menu.findItem(R.id.sabytie).isVisible = false
        menu.findItem(R.id.prazdnik).isVisible = false
        menu.findItem(R.id.search_sviatyia).isVisible = false
        menu.findItem(R.id.sortdate).isVisible = false
        menu.findItem(R.id.sorttime).isVisible = false
        menu.findItem(R.id.action_font).isVisible = false
        menu.findItem(R.id.action_dzen_noch).isVisible = false
        menu.findItem(R.id.action_carkva).isVisible = false
        if (updateAvailable) {
            val item = menu.findItem(R.id.action_update)
            val spanString = SpannableString(item.title.toString())
            if (dzenNoch) spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_black)), 0, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            else spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)), 0, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
            if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
            spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        menu.findItem(R.id.action_update).isVisible = updateAvailable
        menu.findItem(R.id.action_log).isVisible = k.getBoolean("admin", false)
        if (idSelect == R.id.label1) {
            val arrayList = MenuCaliandar.getDataCalaindar(Calendar.getInstance()[Calendar.DATE])
            val dataCalendar = arrayList[0][25].toInt()
            menu.findItem(R.id.action_glava).isVisible = dataCalendar != setDataCalendar
            menu.findItem(R.id.action_mun).isVisible = true
            menu.findItem(R.id.tipicon).isVisible = true
            menu.findItem(R.id.sabytie).isVisible = true
            menu.findItem(R.id.search_sviatyia).isVisible = true
            menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
            if (dzenNoch) {
                menu.findItem(R.id.action_mun).setIcon(R.drawable.calendar_black_full)
                menu.findItem(R.id.action_glava).setIcon(R.drawable.calendar_black)
            }
        }
        if (idSelect == R.id.label101 || idSelect == R.id.label102) {
            menu.findItem(R.id.action_font).isVisible = true
            menu.findItem(R.id.action_dzen_noch).isVisible = true
        }
        if (idSelect == R.id.label103) menu.findItem(R.id.prazdnik).isVisible = true
        if (idSelect == R.id.label7) {
            menu.findItem(R.id.action_add).isVisible = true
            menu.findItem(R.id.sortdate).isVisible = true
            menu.findItem(R.id.sorttime).isVisible = true
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
        if (idSelect == R.id.label12) {
            menu.findItem(R.id.trash).isVisible = true
            menu.findItem(R.id.sortdate).isVisible = true
            menu.findItem(R.id.sortdate).isChecked = k.getInt("vybranoe_sort", 1) == 1
        }
    }

    override fun cleanFullHistory() {
        supportFragmentManager.fragments.forEach {
            val tag = it.tag
            if (tag == "menuPesnyPrasl" || tag == "menuPesnyBel" || tag == "menuPesnyBag" || tag == "menuPesnyKal" || tag == "menuPesnyTaize") {
                (it as? MenuPesny)?.cleanFullHistory()
            }
        }
    }

    override fun cleanHistory(position: Int) {
        supportFragmentManager.fragments.forEach {
            val tag = it.tag
            if (tag == "menuPesnyPrasl" || tag == "menuPesnyBel" || tag == "menuPesnyBag" || tag == "menuPesnyKal" || tag == "menuPesnyTaize") {
                (it as? MenuPesny)?.cleanHistory(position)
            }
        }
    }

    private fun selectFragment(view: View?, start: Boolean = false, shortcuts: Boolean = false, uri: Uri? = null) {
        val id = view?.id ?: R.id.label1
        val idOld = if (id == R.id.label14a || id == R.id.label9a || id == R.id.label10a) idSelect
        else id
        if (!(id == R.id.label9a || id == R.id.label10a || id == R.id.label14a || id == R.id.image5 || id == R.id.image6 || id == R.id.image7 || id == R.id.citata)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (dzenNoch) {
                binding.citata.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                binding.title.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary_black))
                binding.description.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                binding.label1.setBackgroundResource(R.drawable.selector_dark)
                binding.label3.setBackgroundResource(R.drawable.selector_dark)
                binding.label4.setBackgroundResource(R.drawable.selector_dark)
                binding.label5.setBackgroundResource(R.drawable.selector_dark)
                binding.label6.setBackgroundResource(R.drawable.selector_dark)
                binding.label7.setBackgroundResource(R.drawable.selector_dark)
                binding.label8.setBackgroundResource(R.drawable.selector_dark)
                binding.label16.setBackgroundResource(R.drawable.selector_dark)
                binding.label17.setBackgroundResource(R.drawable.selector_dark)
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
                binding.label140.setBackgroundResource(R.drawable.selector_dark)
                binding.label141.setBackgroundResource(R.drawable.selector_dark)
                binding.label142.setBackgroundResource(R.drawable.selector_dark)
                binding.label143.setBackgroundResource(R.drawable.selector_dark)
                binding.label144.setBackgroundResource(R.drawable.selector_dark)
                binding.label145.setBackgroundResource(R.drawable.selector_dark)
                binding.label148.setBackgroundResource(R.drawable.selector_dark)
                binding.citata.setBackgroundResource(R.drawable.selector_dark)
            } else {
                binding.label1.setBackgroundResource(R.drawable.selector_default)
                binding.label3.setBackgroundResource(R.drawable.selector_default)
                binding.label4.setBackgroundResource(R.drawable.selector_default)
                binding.label5.setBackgroundResource(R.drawable.selector_default)
                binding.label6.setBackgroundResource(R.drawable.selector_default)
                binding.label7.setBackgroundResource(R.drawable.selector_default)
                binding.label8.setBackgroundResource(R.drawable.selector_default)
                binding.label16.setBackgroundResource(R.drawable.selector_default)
                binding.label17.setBackgroundResource(R.drawable.selector_default)
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
                binding.label140.setBackgroundResource(R.drawable.selector_default)
                binding.label141.setBackgroundResource(R.drawable.selector_default)
                binding.label142.setBackgroundResource(R.drawable.selector_default)
                binding.label143.setBackgroundResource(R.drawable.selector_default)
                binding.label144.setBackgroundResource(R.drawable.selector_default)
                binding.label145.setBackgroundResource(R.drawable.selector_default)
                binding.label148.setBackgroundResource(R.drawable.selector_default)
                binding.citata.setBackgroundResource(R.drawable.selector_default)
            }
        }
        if (id == R.id.label91 || id == R.id.label92 || id == R.id.label93 || id == R.id.label94 || id == R.id.label95) {
            binding.title9.visibility = View.VISIBLE
            if (dzenNoch) binding.image2.setImageResource(R.drawable.arrow_up_float_black)
            else binding.image2.setImageResource(R.drawable.arrow_up_float)
        }
        if (id == R.id.label101 || id == R.id.label102 || id == R.id.label103 || id == R.id.label104 || id == R.id.label105) {
            binding.title10.visibility = View.VISIBLE
            if (dzenNoch) binding.image3.setImageResource(R.drawable.arrow_up_float_black)
            else binding.image3.setImageResource(R.drawable.arrow_up_float)
        }
        if (id == R.id.label140 || id == R.id.label141 || id == R.id.label142 || id == R.id.label143 || id == R.id.label144 || id == R.id.label145 || id == R.id.label148) {
            binding.title14.visibility = View.VISIBLE
            if (dzenNoch) binding.image4.setImageResource(R.drawable.arrow_up_float_black)
            else binding.image4.setImageResource(R.drawable.arrow_up_float)
        }

        if (id == R.id.label9a) {
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
        if (id == R.id.label10a) {
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
        if (id == R.id.label14a) {
            if (binding.title14.visibility == View.VISIBLE) {
                binding.title14.visibility = View.GONE
                binding.image4.setImageResource(R.drawable.arrow_down_float)
            } else {
                binding.title14.visibility = View.VISIBLE
                if (dzenNoch) binding.image4.setImageResource(R.drawable.arrow_up_float_black)
                else binding.image4.setImageResource(R.drawable.arrow_up_float)
                binding.scrollView.post {
                    binding.scrollView.smoothScrollBy(0, binding.title14.height)
                }
            }
        }
        bindingappbar.subtitleToolbar.visibility = View.GONE
        if (!isNiadaunia()) {
            binding.label140.visibility = View.GONE
        }
        if (!(id == R.id.label101 || id == R.id.label102)) {
            bindingprogress.progressFont.visibility = View.GONE
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
                dialogVisable = false
            }
        }
        when (idOld) {
            R.id.label1 -> {
                tolbarTitle = getString(R.string.kaliandar2)
                if (dzenNoch) binding.label1.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label1.setBackgroundResource(R.drawable.selector_gray)
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

            R.id.label16 -> {
                tolbarTitle = getString(R.string.title_biblia_bokun)
                if (dzenNoch) binding.label16.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label16.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label17 -> {
                tolbarTitle = getString(R.string.title_biblia_charniauski)
                if (dzenNoch) binding.label17.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label17.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label13 -> {
                tolbarTitle = getString(R.string.title_psalter)
                if (dzenNoch) binding.label13.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label13.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label91 -> {
                tolbarTitle = getString(R.string.song)
                bindingappbar.subtitleToolbar.text = getString(R.string.pesny1)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label91.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label91.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label92 -> {
                tolbarTitle = getString(R.string.song)
                bindingappbar.subtitleToolbar.text = getString(R.string.pesny2)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label92.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label92.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label93 -> {
                tolbarTitle = getString(R.string.song)
                bindingappbar.subtitleToolbar.text = getString(R.string.pesny3)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label93.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label93.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label94 -> {
                tolbarTitle = getString(R.string.song)
                bindingappbar.subtitleToolbar.text = getString(R.string.pesny4)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label94.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label94.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label95 -> {
                tolbarTitle = getString(R.string.song)
                bindingappbar.subtitleToolbar.text = getString(R.string.pesny5)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label95.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label95.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label103 -> {
                tolbarTitle = getString(R.string.other)
                bindingappbar.subtitleToolbar.text = getString(R.string.carkva_sviaty)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label103.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label103.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label104 -> {
                tolbarTitle = getString(R.string.other)
                bindingappbar.subtitleToolbar.text = getString(R.string.kaliandar_bel)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label104.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label104.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label105 -> {
                tolbarTitle = getString(R.string.other)
                bindingappbar.subtitleToolbar.text = getString(R.string.parafii)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label105.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label105.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label102 -> {
                tolbarTitle = getString(R.string.other)
                bindingappbar.subtitleToolbar.text = getString(R.string.pamiatka)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label102.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label102.setBackgroundResource(R.drawable.selector_gray)
            }

            R.id.label101 -> {
                tolbarTitle = getString(R.string.other)
                bindingappbar.subtitleToolbar.text = getString(R.string.spovedz)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
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

        val ftrans = supportFragmentManager.beginTransaction()
        ftrans.setCustomAnimations(R.anim.alphainfragment, R.anim.alphaoutfragment)
        when (idOld) {
            R.id.label1 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuCaliandar")
                if (fragment == null) {
                    val caliandar = MenuCaliandar.newInstance(setDataCalendar)
                    ftrans.replace(R.id.conteiner, caliandar, "menuCaliandar")
                    prefEditors.putInt("id", id)
                    if (shortcuts) {
                        val i = Intent(this, Sabytie::class.java)
                        i.putExtra("shortcuts", true)
                        startActivity(i)
                    }
                    idSelect = id
                }
            }

            R.id.label3 -> {
                val fragment = supportFragmentManager.findFragmentByTag("bogaslus")
                if (fragment == null) {
                    val bogaslus = MenuBogashlugbovya()
                    ftrans.replace(R.id.conteiner, bogaslus, "bogaslus")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label4 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuMalitvy")
                if (fragment == null) {
                    val menuMalitvy = MenuMalitvy()
                    ftrans.replace(R.id.conteiner, menuMalitvy, "menuMalitvy")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label5 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuAkafisty")
                if (fragment == null) {
                    val menuAkafisty = MenuAkafisty()
                    ftrans.replace(R.id.conteiner, menuAkafisty, "menuAkafisty)")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label6 -> {
                val fragment = supportFragmentManager.findFragmentByTag("ruzanec")
                if (fragment == null) {
                    val ruzanec = MenuRuzanec()
                    ftrans.replace(R.id.conteiner, ruzanec, "ruzanec")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label7 -> {
                val fragment = supportFragmentManager.findFragmentByTag("MenuNatatki")
                if (fragment == null) {
                    val menuNatatki = MenuNatatki()
                    ftrans.replace(R.id.conteiner, menuNatatki, "MenuNatatki")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label8 -> {
                val fragment = supportFragmentManager.findFragmentByTag("semuxa")
                if (fragment == null) {
                    val semuxa = MenuBibleSemuxa()
                    ftrans.replace(R.id.conteiner, semuxa, "semuxa")
                    prefEditors.putInt("id", id)
                    idSelect = id
                    prefEditors.putBoolean("novyzavet", false)
                }
            }

            R.id.label16 -> {
                val fragment = supportFragmentManager.findFragmentByTag("bokuna")
                if (fragment == null) {
                    val semuxa = MenuBibleBokuna()
                    ftrans.replace(R.id.conteiner, semuxa, "bokuna")
                    prefEditors.putInt("id", id)
                    idSelect = id
                    prefEditors.putBoolean("novyzavet", false)
                }
            }

            R.id.label17 -> {
                val fragment = supportFragmentManager.findFragmentByTag("carniauski")
                if (fragment == null) {
                    val semuxa = MenuBibleCarniauski()
                    ftrans.replace(R.id.conteiner, semuxa, "carniauski")
                    prefEditors.putInt("id", id)
                    idSelect = id
                    prefEditors.putBoolean("novyzavet", false)
                }
            }

            R.id.label13 -> {
                val fragment = supportFragmentManager.findFragmentByTag("nadsana")
                if (fragment == null) {
                    val nadsana = MenuBibleNadsana()
                    ftrans.replace(R.id.conteiner, nadsana, "nadsana")
                    prefEditors.putInt("id", id)
                    idSelect = id
                    prefEditors.putBoolean("novyzavet", false)
                }
            }

            R.id.label91 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuPesnyPrasl")
                if (fragment == null) {
                    val menuPesny = MenuPesny.getInstance("prasl")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesnyPrasl")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label92 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuPesnyBel")
                if (fragment == null) {
                    val menuPesny = MenuPesny.getInstance("bel")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesnyBel")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label93 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuPesnyBag")
                if (fragment == null) {
                    val menuPesny = MenuPesny.getInstance("bag")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesnyBag")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label94 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuPesnyKal")
                if (fragment == null) {
                    val menuPesny = MenuPesny.getInstance("kal")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesnyKal")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label95 -> {
                val fragment = supportFragmentManager.findFragmentByTag("menuPesnyTaize")
                if (fragment == null) {
                    val menuPesny = MenuPesny.getInstance("taize")
                    ftrans.replace(R.id.conteiner, menuPesny, "menuPesnyTaize")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label103 -> {
                val fragment = supportFragmentManager.findFragmentByTag("MenuCviaty")
                if (fragment == null) {
                    val menuCviaty = MenuSviaty()
                    ftrans.replace(R.id.conteiner, menuCviaty, "MenuCviaty")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label104 -> {
                val fragment = supportFragmentManager.findFragmentByTag("MenuPashalii")
                if (fragment == null) {
                    val menuPashalii = MenuPashalii()
                    ftrans.replace(R.id.conteiner, menuPashalii, "MenuPashalii")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label105 -> {
                val fragment = supportFragmentManager.findFragmentByTag("parafiiBgkc")
                if (fragment == null) {
                    val parafiiBgkc = MenuParafiiBgkc()
                    ftrans.replace(R.id.conteiner, parafiiBgkc, "parafiiBgkc")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label102 -> {
                val fragment = supportFragmentManager.findFragmentByTag("MenuPamiatka")
                if (fragment == null) {
                    val menuPamiatka = MenuPamiatka()
                    ftrans.replace(R.id.conteiner, menuPamiatka, "MenuPamiatka")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label101 -> {
                val fragment = supportFragmentManager.findFragmentByTag("MenuPadryxtoukaDaSpovedzi")
                if (fragment == null) {
                    val menuPadryxtoukaDaSpovedzi = MenuPadryxtoukaDaSpovedzi()
                    ftrans.replace(R.id.conteiner, menuPadryxtoukaDaSpovedzi, "MenuPadryxtoukaDaSpovedzi")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label11 -> {
                val fragment = supportFragmentManager.findFragmentByTag("sinoidal")
                if (fragment == null) {
                    val sinoidal = MenuBibleSinoidal()
                    ftrans.replace(R.id.conteiner, sinoidal, "sinoidal")
                    prefEditors.putInt("id", id)
                    idSelect = id
                    prefEditors.putBoolean("novyzavet", false)
                }
            }

            R.id.label12 -> {
                val fragment = supportFragmentManager.findFragmentByTag("MenuVybranoe")
                if (fragment == null) {
                    val vybranoe = MenuVybranoe()
                    ftrans.replace(R.id.conteiner, vybranoe, "MenuVybranoe")
                    prefEditors.putInt("id", id)
                    idSelect = id
                }
            }

            R.id.label140 -> {
                startBiblioteka(NIADAUNIA, start, id, uri)
            }

            R.id.label141 -> {
                startBiblioteka(GISTORYIACARKVY, start, id)
            }

            R.id.label142 -> {
                startBiblioteka(MALITOUNIKI, start, id)
            }

            R.id.label143 -> {
                startBiblioteka(SPEUNIKI, start, id)
            }

            R.id.label144 -> {
                startBiblioteka(RELLITARATURA, start, id)
            }

            R.id.label145 -> {
                startBiblioteka(SETFILE, start, id)
            }

            R.id.label148 -> {
                startBiblioteka(PDF, start, id)
            }

            R.id.image5 -> {
                if (isNetworkAvailable()) {
                    if (!ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                        binding.image5.visibility = View.INVISIBLE
                        binding.progressbar.visibility = View.VISIBLE
                        val intent = Intent(this, ServiceRadyjoMaryia::class.java)
                        ContextCompat.startForegroundService(this, intent)
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
                        binding.image5.setImageResource(R.drawable.pause2)
                        binding.image5.contentDescription = getString(R.string.pause)
                    } else {
                        mRadyjoMaryiaService?.apply {
                            if (k.getBoolean("WIDGET_RADYJO_MARYIA_ENABLED", false)) {
                                sendBroadcast(Intent(this, WidgetRadyjoMaryia::class.java))
                            }
                            playOrPause()
                            if (isPlayingRadioMaria()) {
                                binding.image5.setImageResource(R.drawable.pause2)
                                binding.image5.contentDescription = getString(R.string.pause)
                            } else {
                                binding.image5.setImageResource(R.drawable.play2)
                                binding.image5.contentDescription = getString(R.string.play)
                            }
                        }
                    }
                } else {
                    val dialoNoIntent = DialogNoInternet()
                    dialoNoIntent.show(supportFragmentManager, "dialoNoIntent")
                }
            }

            R.id.image6 -> {
                if (ServiceRadyjoMaryia.isServiceRadioMaryiaRun) {
                    if (isConnectServise) {
                        unbindService(mConnection)
                    }
                    isConnectServise = false
                    mRadyjoMaryiaService?.stopServiceRadioMaria()
                    binding.label15b.visibility = View.GONE
                    binding.image5.setImageResource(R.drawable.play2)
                    binding.image5.contentDescription = getString(R.string.play)
                }
            }

            R.id.image7 -> {
                val dialog = DialogProgramRadoiMaryia.getInstance(false)
                dialog.show(supportFragmentManager, "DialogProgramRadoiMaryia")
            }

            R.id.citata -> {
                startActivity(Intent(this, Cytaty::class.java))
            }
        }
        if (start) {
            ftrans.commit()
        } else {
            bindingappbar.toolbar.postDelayed({
                ftrans.commitAllowingStateLoss()
            }, 300)
        }
        prefEditors.apply()

        bindingappbar.titleToolbar.text = tolbarTitle
    }

    override fun setTitleRadioMaryia(title: String) {
        if (title != "") {
            binding.label15b.text = title
            binding.label15b.visibility = View.VISIBLE
        }
    }

    override fun unBinding() {
        if (isConnectServise) {
            unbindService(mConnection)
        }
        binding.label15b.visibility = View.GONE
        binding.image5.visibility = View.VISIBLE
        binding.progressbar.visibility = View.GONE
        binding.image5.setImageResource(R.drawable.play2)
        binding.image5.contentDescription = getString(R.string.play)
        isConnectServise = false
    }

    override fun playingRadioMaria(isPlayingRadioMaria: Boolean) {
        if (isPlayingRadioMaria) {
            binding.image5.setImageResource(R.drawable.pause2)
            binding.image5.contentDescription = getString(R.string.pause)
        } else {
            binding.image5.setImageResource(R.drawable.play2)
            binding.image5.contentDescription = getString(R.string.play)
        }
    }

    override fun playingRadioMariaStateReady() {
        binding.image5.visibility = View.VISIBLE
        binding.progressbar.visibility = View.GONE
        setTitleRadioMaryia(ServiceRadyjoMaryia.titleRadyjoMaryia)
    }

    override fun errorRadioMaria() {
        toastView(this, getString(R.string.error_ch2), Toast.LENGTH_LONG)
    }

    override fun onClick(view: View?) {
        selectFragment(view)
    }

    fun getListBiliateka(): ArrayList<ArrayList<String>> {
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
        val jsonB = k.getString("Biblioteka", "") ?: ""
        val tempList = ArrayList<ArrayList<String>>()
        if (jsonB.isNotEmpty()) {
            tempList.addAll(gson.fromJson(jsonB, type))
        }
        return tempList
    }

    fun getTreeUri(): Uri? {
        val list = contentResolver.persistedUriPermissions
        if (list.size > 0) {
            for (i in 0 until list.size) {
                if (list[i].uri.path?.contains("tree") == true) {
                    return list[i].uri
                }
            }
        }
        return null
    }

    fun getFileName(uri: Uri): String {
        var result: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = cursor.getString(index)
            }
        } catch (_: Throwable) {
        } finally {
            cursor?.close()
        }
        if (result == null) {
            result = uri.path ?: ""
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getTitle(fileName: String): String {
        val arrayList = getListBiliateka()
        var result = fileName
        for (i in 0 until arrayList.size) {
            if (fileName == arrayList[i][2]) {
                result = arrayList[i][0]
                break
            }
        }
        return result
    }

    fun fileExistsBiblijateka(fileName: String): Boolean {
        var exitsFile = false
        val uriTree = getTreeUri()
        if (uriTree != null) {
            val documentsTree = DocumentFile.fromTreeUri(this, uriTree)
            val childDocuments = documentsTree?.listFiles()
            childDocuments?.let { document ->
                for (i in document.indices) {
                    if (document[i].name == fileName) {
                        exitsFile = true
                        break
                    }
                }
            }
        }
        return exitsFile
    }

    private fun fileExistsUnder(fileName: String): Boolean {
        var exitsFile = false
        val naidaunia = ArrayList<ArrayList<String>>()
        val gson = Gson()
        val json = k.getString("biblijatekaLatest", "")
        if (!json.equals("")) {
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
            naidaunia.addAll(gson.fromJson(json, type))
        }
        for (i in 0 until naidaunia.size) {
            if (naidaunia[i][0] == "" && fileName == naidaunia[i][2]) {
                exitsFile = true
                break
            }
        }
        return exitsFile
    }

    fun saveNaidauniaBiblijateka(fileName: String, uri: Uri) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val naidaunia = ArrayList<ArrayList<String>>()
                val gson = Gson()
                val json = k.getString("biblijatekaLatest", "")
                if (!json.equals("")) {
                    val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                    naidaunia.addAll(gson.fromJson(json, type))
                }
                for (i in 0 until naidaunia.size) {
                    if (fileName == naidaunia[i][2]) {
                        naidaunia.removeAt(i)
                        break
                    }
                }
                var isAddN = false
                val listBiliateka = getListBiliateka()
                for (e in 0 until listBiliateka.size) {
                    if (fileName == listBiliateka[e][2]) {
                        val subList = listBiliateka[e]
                        subList.add(uri.toString())
                        naidaunia.add(0, subList)
                        isAddN = true
                    }
                }
                if (!isAddN) {
                    val file = File("$filesDir/image_temp/$fileName.png")
                    if (!file.exists()) {
                        creteUnderImage(file, uri)
                    }
                    val list = ArrayList<String>()
                    list.add("")
                    list.add("")
                    list.add(fileName)
                    list.add("0")
                    list.add("0")
                    list.add(file.name)
                    list.add(uri.toString())
                    naidaunia.add(0, list)
                }
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                val prefEditor = k.edit()
                prefEditor.putString("biblijatekaLatest", gson.toJson(naidaunia, type))
                prefEditor.apply()
            }
        }
    }

    fun creteUnderImage(file: File, uri: Uri) {
        val width = bindingcontent.conteiner.width
        var page: PdfRenderer.Page? = null
        try {
            val fileReader = contentResolver.openFileDescriptor(uri, "r")
            fileReader?.let {
                val pdfRenderer = PdfRenderer(it)
                page = pdfRenderer.openPage(0)
                page?.let { pageS ->
                    val aspectRatio = pageS.width.toFloat() / pageS.height.toFloat()
                    val height = (width / aspectRatio).toInt()
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    pageS.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    val imageWithBG = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    imageWithBG.eraseColor(Color.WHITE)
                    val canvas = Canvas(imageWithBG)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    bitmap.recycle()
                    val os = BufferedOutputStream(FileOutputStream(file))
                    imageWithBG.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.close()
                }
            }
        } catch (_: Throwable) {
        } finally {
            page?.close()
        }
    }

    private fun startBiblioteka(rub: Int, start: Boolean, id: Int, uri: Uri? = null) {
        if (uri != null) {
            val intent = Intent(this, BiblijatekaPdf::class.java)
            val fileName = getFileName(uri)
            val title = getTitle(fileName)
            if (fileExistsBiblijateka(fileName)) {
                val treeUri = getTreeUri()
                treeUri?.let { uri1 ->
                    val documentsTree = DocumentFile.fromTreeUri(this, uri1)
                    val newUri = documentsTree?.findFile(fileName)?.uri
                    newUri?.let {
                        intent.data = newUri
                        intent.putExtra("isPrint", true)
                        saveNaidauniaBiblijateka(fileName, it)
                    }
                }
            } else if (fileExistsUnder(fileName)) {
                val naidaunia = ArrayList<ArrayList<String>>()
                val gson = Gson()
                val json = k.getString("biblijatekaLatest", "")
                if (!json.equals("")) {
                    val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                    naidaunia.addAll(gson.fromJson(json, type))
                }
                for (i in 0 until naidaunia.size) {
                    if (naidaunia[i][0] == "" && fileName == naidaunia[i][2]) {
                        val newUri = Uri.parse(naidaunia[i][6])
                        intent.data = newUri
                        intent.putExtra("isPrint", true)
                        saveNaidauniaBiblijateka(fileName, newUri)
                        break
                    }
                }
            } else {
                intent.data = uri
                intent.putExtra("isPrint", false)
            }
            intent.putExtra("fileTitle", title)
            intent.putExtra("fileName", fileName)
            setFileBiblijatekaLauncher.launch(intent)
        }
        var rubNew = if (rub == SETFILE) {
            if (isNiadaunia()) NIADAUNIA
            else MALITOUNIKI
        } else rub
        if (rub == NIADAUNIA && !isNiadaunia()) rubNew = MALITOUNIKI
        var fragment = supportFragmentManager.findFragmentByTag("MenuBiblijateka$rubNew") as? MenuBiblijateka
        if (fragment == null) {
            val ftrans = supportFragmentManager.beginTransaction()
            ftrans.setCustomAnimations(R.anim.alphainfragment, R.anim.alphaoutfragment)
            fragment = MenuBiblijateka.getInstance(rubNew)
            ftrans.replace(R.id.conteiner, fragment, "MenuBiblijateka$rubNew")
            if (start) {
                ftrans.commit()
            } else {
                bindingappbar.toolbar.postDelayed({
                    ftrans.commitAllowingStateLoss()
                }, 300)
            }
        }
        if (rub == SETFILE) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/pdf"
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            mActivityResultFile.launch(Intent.createChooser(intent, getString(R.string.vybrac_file)))
        }
        when (rubNew) {
            NIADAUNIA -> {
                tolbarTitle = getString(R.string.bibliateka_carkvy)
                bindingappbar.subtitleToolbar.text = getString(R.string.bibliateka_niadaunia)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label140.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label140.setBackgroundResource(R.drawable.selector_gray)
            }

            GISTORYIACARKVY -> {
                tolbarTitle = getString(R.string.bibliateka_carkvy)
                bindingappbar.subtitleToolbar.text = getString(R.string.bibliateka_gistoryia_carkvy)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label141.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label141.setBackgroundResource(R.drawable.selector_gray)
            }

            MALITOUNIKI -> {
                tolbarTitle = getString(R.string.bibliateka_carkvy)
                bindingappbar.subtitleToolbar.text = getString(R.string.bibliateka_malitouniki)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label142.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label142.setBackgroundResource(R.drawable.selector_gray)
            }

            SPEUNIKI -> {
                tolbarTitle = getString(R.string.bibliateka_carkvy)
                bindingappbar.subtitleToolbar.text = getString(R.string.bibliateka_speuniki)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label143.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label143.setBackgroundResource(R.drawable.selector_gray)
            }

            RELLITARATURA -> {
                tolbarTitle = getString(R.string.bibliateka_carkvy)
                bindingappbar.subtitleToolbar.text = getString(R.string.bibliateka_rel_litaratura)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label144.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label144.setBackgroundResource(R.drawable.selector_gray)
            }

            PDF -> {
                tolbarTitle = getString(R.string.bibliateka_carkvy)
                bindingappbar.subtitleToolbar.text = getString(R.string.arx_num_gaz)
                bindingappbar.subtitleToolbar.visibility = View.VISIBLE
                if (dzenNoch) binding.label148.setBackgroundResource(R.drawable.selector_dark_maranata)
                else binding.label148.setBackgroundResource(R.drawable.selector_gray)
            }
        }
        prefEditors.putInt("id", id)
        prefEditors.apply()
        idSelect = id
    }

    override fun munuBiblijatekaUpdate(isNiadaunia: Boolean) {
        if (isNiadaunia) binding.label140.visibility = View.VISIBLE
        else binding.label140.visibility = View.GONE
    }

    override fun menuMainloadNiadaunia() {
        selectFragment(binding.label140)
    }

    private fun isNiadaunia(): Boolean {
        val naidaunia = ArrayList<ArrayList<String>>()
        val gson = Gson()
        val json = k.getString("biblijatekaLatest", "")
        if (json != "") {
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
            naidaunia.addAll(gson.fromJson(json, type))
        }
        return naidaunia.size > 0
    }

    private fun completeUpdate() {
        prefEditors.putInt("updateCount", 0)
        prefEditors.apply()
        updateAvailable = false
        invalidateOptionsMenu()
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.unregisterListener(installStateUpdatedListener)
        appUpdateManager.completeUpdate()
    }

    private fun checkUpdateMalitounik() {
        if (isNetworkAvailable()) {
            val c = Calendar.getInstance()
            val updateCount = k.getInt("updateCount", 0)
            val appUpdateManager = AppUpdateManagerFactory.create(this)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    updateAvailable = true
                    invalidateOptionsMenu()
                    if (updateCount < 3 || c.timeInMillis > k.getLong("updateTime", c.timeInMillis)) {
                        if (isNetworkAvailable(TRANSPORT_CELLULAR)) {
                            val dialog = DialogUpdateWIFI.getInstance(appUpdateInfo.totalBytesToDownload().toFloat())
                            dialog.show(supportFragmentManager, "DialogUpdateWIFI")
                        } else {
                            val dialog = DialogUpdateMalitounik.getInstance(getString(R.string.update_title))
                            dialog.show(supportFragmentManager, "DialogUpdateMalitounik")
                            appUpdateManager.registerListener(installStateUpdatedListener)
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, updateMalitounikLauncher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                        }
                    }
                }
            }
        }
    }

    override fun onUpdatePositiveWIFI() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.registerListener(installStateUpdatedListener)
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, updateMalitounikLauncher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
            }
        }
    }

    override fun onUpdateNegativeWIFI() {
        val c = Calendar.getInstance()
        var updateCount = k.getInt("updateCount", 0)
        updateCount++
        prefEditors.putInt("updateCount", updateCount)
        if (updateCount >= 3) {
            c.set(Calendar.HOUR_OF_DAY, 8)
            c.add(Calendar.DATE, 3)
            prefEditors.putLong("updateTime", c.timeInMillis)
        }
        prefEditors.apply()
    }

    companion object {
        const val BIBLIATEKALIST = "by.carkva_gazeta.admin.BibliatekaList"
        const val ADMINMAIN = "by.carkva_gazeta.admin.AdminMain"
        const val ADMINNOVYZAPAVIETSEMUXA = "by.carkva_gazeta.admin.NovyZapavietSemuxa"
        const val ADMINSTARYZAPAVIETSEMUXA = "by.carkva_gazeta.admin.StaryZapavietSemuxa"
        const val ADMINSVIATYIA = "by.carkva_gazeta.admin.Sviatyia"
        const val ADMINSVIATY = "by.carkva_gazeta.admin.Sviaty"
        const val ADMINPIARLINY = "by.carkva_gazeta.admin.Piarliny"
        const val PASOCHNICALIST = "by.carkva_gazeta.admin.PasochnicaList"
        const val CHYTANNE = "by.carkva_gazeta.resources.Chytanne"
        const val MARANATA = "by.carkva_gazeta.resources.MaranAta"
        const val SEARCHBIBLIA = "by.carkva_gazeta.resources.SearchBiblia"
        const val PASLIAPRYCHASCIA = "by.carkva_gazeta.resources.PasliaPrychascia"
        const val BOGASHLUGBOVYA = "by.carkva_gazeta.resources.Bogashlugbovya"
        const val BIBLEZAKLADKI = "by.carkva_gazeta.resources.BibleZakladki"
        const val BIBLENATATKI = "by.carkva_gazeta.resources.BibleNatatki"
        const val SLUGBYVIALIKAGAPOSTUSPIS = "by.carkva_gazeta.resources.SlugbyVialikagaPostuSpis"
        const val MALITVYPRYNAGODNYIA = "by.carkva_gazeta.resources.MalitvyPrynagodnyia"
        const val NADSANMALITVYIPESNILIST = "by.carkva_gazeta.resources.NadsanMalitvyIPesniList"
        const val PSALTERNADSANA = "by.carkva_gazeta.resources.PsalterNadsana"
        const val SEARCHBOGASHLUGBOVYA = "by.carkva_gazeta.resources.SearchBogashlugbovya"
        const val BIBLIALIST = "by.carkva_gazeta.resources.BibliaList"
        const val BIBLIAACTIVITY = "by.carkva_gazeta.resources.BibliaActivity"
        const val LOGVIEW = "by.carkva_gazeta.resources.LogView"
        const val NIADAUNIA = 0
        const val GISTORYIACARKVY = 1
        const val MALITOUNIKI = 2
        const val SPEUNIKI = 3
        const val RELLITARATURA = 4
        const val PDF = 5
        const val SETFILE = 6
        const val PROGRESSACTIONFONT = 1
        const val PROGRESSACTIONAUTOLEFT = 2
        const val PROGRESSACTIONAUTORIGHT = 3
        private const val TRANSPORT_ALL = 100
        const val TRANSPORT_WIFI = 101
        const val TRANSPORT_CELLULAR = 102
        var padzeia = ArrayList<Padzeia>()
        var setDataCalendar = MenuCaliandar.getDataCalaindar(Calendar.getInstance()[Calendar.DATE])[0][25].toInt()
        var dialogVisable = false
        var updateAvailable = false
        fun setListPadzeia() {
            padzeia.clear()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, Padzeia::class.java).type
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
                    it.write(gson.toJson(padzeia, type))
                }
                dir.deleteRecursively()
            } else {
                val file = File(Malitounik.applicationContext().filesDir.toString() + "/Sabytie.json")
                if (file.exists()) {
                    try {
                        padzeia = gson.fromJson(file.readText(), type)
                    } catch (t: Throwable) {
                        file.delete()
                    }
                }
            }
            padzeia.sort()
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
            cytanne = cytanne.replace("Перадсьв.", "")
            cytanne = cytanne.replace("Сьв.", "", true)
            cytanne = cytanne.replace("Вялеб.", "")
            cytanne = cytanne.replace("Пакл.", "")
            cytanne = cytanne.replace("Багар.", "")
            cytanne = cytanne.replace("Вялікамуч.", "")
            cytanne = cytanne.replace("Ап.", "")
            cytanne = cytanne.replace("Айцам.", "")
            cytanne = cytanne.replace("Айцам", "")
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
            cytanne = cytanne.replace("Абр", "")
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
            replase1 = replase1.replace("и", "і", ignoreCase)
            replase1 = replase1.replace("щ", "ў", ignoreCase)
            replase1 = replase1.replace("ъ", "’", ignoreCase)
            replase1 = replase1.replace("'", "’", ignoreCase)
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
            replase1 = replase1.replace("ццё", "цьцё", ignoreCase)
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
            replase1 = replase1.replace("з'е", "зье", ignoreCase)
            replase1 = replase1.replace("з'я", "зья", ignoreCase)
            return replase1
        }

        @Suppress("DEPRECATION")
        fun toastView(context: Context, message: String, toastLength: Int = Toast.LENGTH_SHORT) {
            val layout = ToastBinding.inflate(LayoutInflater.from(context))
            layout.textView.text = message
            layout.toastRoot.setBackgroundResource(R.color.colorPrimary)
            val mes = Toast(context)
            mes.duration = toastLength
            mes.view = layout.root
            mes.show()
        }

        fun fromHtml(html: String, mode: Int = HtmlCompat.FROM_HTML_MODE_LEGACY) = HtmlCompat.fromHtml(html, mode)

        fun toHtml(html: Spannable) = HtmlCompat.toHtml(html, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)

        @Suppress("DEPRECATION")
        fun isNetworkAvailable(typeTransport: Int = TRANSPORT_ALL): Boolean {
            val connectivityManager = Malitounik.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                when (typeTransport) {
                    TRANSPORT_CELLULAR -> {
                        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
                    }

                    TRANSPORT_WIFI -> {
                        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
                    }

                    TRANSPORT_ALL -> {
                        return when {
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
                if (activeNetwork.isConnectedOrConnecting) {
                    when (typeTransport) {
                        TRANSPORT_CELLULAR -> {
                            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return true
                        }

                        TRANSPORT_WIFI -> {
                            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return true
                        }

                        TRANSPORT_ALL -> {
                            return when (activeNetwork.type) {
                                ConnectivityManager.TYPE_WIFI -> true
                                ConnectivityManager.TYPE_MOBILE -> true
                                ConnectivityManager.TYPE_VPN -> true
                                ConnectivityManager.TYPE_ETHERNET -> true
                                else -> false
                            }
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
