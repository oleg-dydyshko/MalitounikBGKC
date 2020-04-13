package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong


@SuppressWarnings("ResultOfMethodCallIgnored")
class MainActivity : AppCompatActivity(), View.OnClickListener, DialogContextMenu.DialogContextMenuListener, MenuCviaty.CarkvaCarkvaListener, DialogDelite.DialogDeliteListener, MenuCaliandar.MenuCaliandarPageListinner, DialogFontSize.DialogFontSizeListener, DialogPasxa.DialogPasxaListener, DialogPrazdnik.DialogPrazdnikListener, DialogDeliteAllVybranoe.DialogDeliteAllVybranoeListener {

    private lateinit var c: GregorianCalendar
    private lateinit var k: SharedPreferences
    private lateinit var prefEditors: SharedPreferences.Editor
    private var idSelect = 0
    private var idOld = -1
    private var dzenNoch = false
    private var tolbarTitle = ""
    private var shortcuts = false

    override fun setDataCalendar(day_of_year: Int, year: Int) {
        c = Calendar.getInstance() as GregorianCalendar
        idSelect = R.id.label1
        var dayyear = 0
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until year) {
            dayyear += if (c.isLeapYear(i)) 366
            else 365
        }
        setDataCalendar = dayyear + day_of_year - 1
        idOld = -1
        onClick(label1)
    }

    override fun onDialogFontSizePositiveClick() {
        val menuPadryxtoukaDaSpovedzi = supportFragmentManager.findFragmentByTag("MenuPadryxtoukaDaSpovedzi") as? MenuPadryxtoukaDaSpovedzi
        menuPadryxtoukaDaSpovedzi?.onDialogFontSizePositiveClick()
        val menuPamiatka = supportFragmentManager.findFragmentByTag("MenuPamiatka") as? MenuPamiatka
        menuPamiatka?.onDialogFontSizePositiveClick()
    }

    override fun setPage(page: Int) {
        setDataCalendar = page
    }

    override fun fileDelite(position: Int, file: String) {
        val vybranoe = supportFragmentManager.findFragmentByTag("MenuVybranoe") as? MenuVybranoe
        vybranoe?.fileDelite(position)
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.fileDelite(position)
    }

    override fun deliteAllVybranoe() {
        val vybranoe = supportFragmentManager.findFragmentByTag("MenuVybranoe") as? MenuVybranoe
        vybranoe?.deliteAllVybranoe()
    }

    override fun onDialogEditClick(position: Int) {
        val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
        menuNatatki?.onDialogEditClick(position)
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
        k = getSharedPreferences("biblia", MODE_PRIVATE)

        val density = resources.displayMetrics.density

        logosite.post {
            val bd: BitmapDrawable = ContextCompat.getDrawable(this, R.drawable.logotip) as BitmapDrawable
            val imageHeight = bd.bitmap.height / density
            val imageWidth = bd.bitmap.width / density
            val widthDp = logosite.width / density
            val kooficient = widthDp / imageWidth
            val hidch = imageHeight * kooficient
            val layoutParams: ViewGroup.LayoutParams = logosite.layoutParams
            layoutParams.height = (hidch * density).toInt()
            logosite.layoutParams = layoutParams
        }
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    private fun ajustCompoundDrawableSizeWithText(textView: TextViewRobotoCondensed, leftDrawable: Drawable?) {
        textView.addOnLayoutChangeListener(
                object : View.OnLayoutChangeListener {
                    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                        leftDrawable?.setBounds(0, 0, textView.textSize.toInt(), textView.textSize.toInt())
                        textView.setCompoundDrawables(leftDrawable, null, null, null)
                        textView.removeOnLayoutChangeListener(this)
                    }
                }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mkDir()
        k = getSharedPreferences("biblia", MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            idSelect = savedInstanceState.getInt("id")
            idOld = savedInstanceState.getInt("idOld")
        }
/*InputStream inputStream2 = getResources().openRawResource(R.raw.nadsan_psaltyr)
String[] split
try {
    InputStreamReader isr = new InputStreamReader(inputStream2)
    BufferedReader reader = new BufferedReader(isr)
    String line
    StringBuilder builder = new StringBuilder()
    while ((line = reader.readLine()) != null) {
        /*if (line.contains("//")) {
            int t1 = line.indexOf("//")
            line = line.substring(0, t1).trim()
            if (!line.equals(""))
                builder.append(line).append("\n")
            continue
        }*/
        builder.append(line).append("\n")
    }
    inputStream2.close()
   split = builder.toString().split("===")
   StringBuilder builder1 = new StringBuilder()
   for (int e = 1 e < split.length e++) {
       builder1.append("// Псалом ").append(e).append("\n").append("===").append(split[e])
   }
   File file = new File(this.getFilesDir() + "/caliandar_code.txt")
   FileWriter outputStream = new FileWriter(file)
   outputStream.write(builder1.toString())
   outputStream.close()
} catch (Throwable ignored) {
}
*/
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
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        setSupportActionBar(toolbar)
        // Скрываем клавиатуру
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        c = Calendar.getInstance() as GregorianCalendar

        idSelect = k.getInt("id", R.id.label1)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        label1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label3.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label4.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label5.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label6.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label7.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label8.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label9.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label91.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label92.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label93.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label94.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label95.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label10.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label101.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label102.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label103.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label104.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label105.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label11.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label12.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        label13.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)

        carkva_link.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)

        var drawable = ContextCompat.getDrawable(this, R.drawable.krest)
        if (dzenNoch) {
            drawable = ContextCompat.getDrawable(this, R.drawable.krest_black)
            label91.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label92.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label93.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label94.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label95.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label101.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label102.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label103.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label105.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label104.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            label1.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label2.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label3.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label4.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label5.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label6.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label7.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label8.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label9.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label91.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label92.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label93.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label94.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label95.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label10.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label101.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label102.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label103.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label104.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label105.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label11.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label12.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
            label13.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        ajustCompoundDrawableSizeWithText(label1, drawable)
        ajustCompoundDrawableSizeWithText(label2, drawable)
        ajustCompoundDrawableSizeWithText(label3, drawable)
        ajustCompoundDrawableSizeWithText(label4, drawable)
        ajustCompoundDrawableSizeWithText(label5, drawable)
        ajustCompoundDrawableSizeWithText(label6, drawable)
        ajustCompoundDrawableSizeWithText(label7, drawable)
        ajustCompoundDrawableSizeWithText(label8, drawable)
        ajustCompoundDrawableSizeWithText(label9, drawable)
        ajustCompoundDrawableSizeWithText(label10, drawable)
        ajustCompoundDrawableSizeWithText(label11, drawable)
        ajustCompoundDrawableSizeWithText(label12, drawable)
        ajustCompoundDrawableSizeWithText(label13, drawable)

        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            drawable = ContextCompat.getDrawable(this, R.drawable.logotip_whate)
            logosite.setImageDrawable(drawable)
        }
        if (k.getInt("sinoidal", 0) == 1) {
            label11.visibility = View.VISIBLE
        }

        title9.setOnClickListener(this)
        title10.setOnClickListener(this)
        label1.setOnClickListener(this)
        label2.setOnClickListener(this)
        label3.setOnClickListener(this)
        label4.setOnClickListener(this)
        label5.setOnClickListener(this)
        label6.setOnClickListener(this)
        label7.setOnClickListener(this)
        label8.setOnClickListener(this)
        label91.setOnClickListener(this)
        label92.setOnClickListener(this)
        label93.setOnClickListener(this)
        label94.setOnClickListener(this)
        label95.setOnClickListener(this)
        label101.setOnClickListener(this)
        label102.setOnClickListener(this)
        label103.setOnClickListener(this)
        label104.setOnClickListener(this)
        label105.setOnClickListener(this)
        label11.setOnClickListener(this)
        label12.setOnClickListener(this)
        label13.setOnClickListener(this)
        label9a.setOnClickListener(this)
        label10a.setOnClickListener(this)

        val data: Uri? = intent.data
        if (data != null) {
            if (data.toString().contains("shortcuts=1")) {
                idSelect = R.id.label12
                onClick(label12)
            } else if (data.toString().contains("shortcuts=3")) {
                idSelect = R.id.label7
                onClick(label7)
            } else if (data.toString().contains("shortcuts=2")) {
                idSelect = R.id.label2
                shortcuts = true
                onClick(label2)
            } else if (data.toString().contains("caliandar")) {
                idSelect = R.id.label1
                onClick(label1)
            } else if (data.toString().contains("biblija")) {
                idSelect = R.id.label8
                onClick(label8)
            } else if (!data.toString().contains("https://")) {
                idSelect = R.id.label2
                shortcuts = true
                onClick(label2)
            }
        }
        val extras: Bundle? = intent.extras
        if (extras != null) {
            val widgetday = "widget_day"
            val widgetmun = "widget_mun"

            if (extras.getBoolean(widgetmun, false) && savedInstanceState == null) {
                idSelect = R.id.label1
                var dayyear = 0
                for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until extras.getInt("Year", c.get(Calendar.YEAR))) {
                    dayyear += if (c.isLeapYear(i)) 366
                    else 365
                }
                setDataCalendar = dayyear + extras.getInt("DayYear", c.get(Calendar.DAY_OF_YEAR)) - 1
            }
            if (extras.getBoolean(widgetday, false) && savedInstanceState == null) {
                idSelect = R.id.label1
                val chyt = c.get(Calendar.DAY_OF_YEAR) - 1
                var dayyear = 0
                val chytanneYear = c.get(Calendar.YEAR)
                for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until chytanneYear) {
                    dayyear += if (c.isLeapYear(i)) 366
                    else 365
                }
                setDataCalendar = dayyear + chyt
            }

            if (extras.getBoolean("sabytie", false)) {
                idSelect = R.id.label1
                val chyt = extras.getInt("data") - 1
                var dayyear = 0
                var chytanneYear = extras.getInt("year")
                if (chytanneYear == -1) chytanneYear = c.get(Calendar.YEAR)
                for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until chytanneYear) {
                    dayyear += if (c.isLeapYear(i)) 366
                    else 365
                }
                setDataCalendar = dayyear + chyt
            }
        }

        var scroll = false
        // Выбор пункта
        when (idSelect) {
            R.id.label1 -> onClick(label1)
            R.id.label2 -> onClick(label2)
            R.id.label3 -> onClick(label3)
            R.id.label4 -> {
                if (!label4.isShown) scroll = true
                onClick(label4)
            }
            R.id.label5 -> {
                if (!label5.isShown) scroll = true
                onClick(label5)
            }
            R.id.label6 -> {
                if (!label6.isShown) scroll = true
                onClick(label6)
            }
            R.id.label7 -> {
                if (!label7.isShown) scroll = true
                onClick(label7)
            }
            R.id.label8 -> {
                if (!label8.isShown) scroll = true
                onClick(label8)
            }
            R.id.label91 -> {
                if (!label91.isShown) scroll = true
                onClick(label91)
            }
            R.id.label92 -> {
                if (!label92.isShown) scroll = true
                onClick(label92)
            }
            R.id.label93 -> {
                if (!label93.isShown) scroll = true
                onClick(label93)
            }
            R.id.label94 -> {
                if (!label94.isShown) scroll = true
                onClick(label94)
            }
            R.id.label95 -> {
                if (!label95.isShown) scroll = true
                onClick(label95)
            }
            R.id.label101 -> {
                if (!label101.isShown) scroll = true
                onClick(label101)
            }
            R.id.label102 -> {
                if (!label102.isShown) scroll = true
                onClick(label102)
            }
            R.id.label103 -> {
                if (!label103.isShown) scroll = true
                onClick(label103)
            }
            R.id.label104 -> {
                if (!label104.isShown) scroll = true
                onClick(label104)
            }
            R.id.label105 -> {
                if (!label105.isShown) scroll = true
                onClick(label105)
            }
            R.id.label11 -> {
                if (!label11.isShown) scroll = true
                onClick(label11)
            }
            R.id.label12 -> {
                if (!label12.isShown) scroll = true
                onClick(label12)
            }
            R.id.label13 -> {
                if (!label13.isShown) scroll = true
                onClick(label13)
            }
            else -> {
                idSelect = R.id.label1
                onClick(label1)
            }
        }

        if (setAlarm) {
            val i = Intent(this, ReceiverUpdate::class.java)
            //i.action = "UPDATE"
            sendBroadcast(i)
            /*val c2 = Calendar.getInstance() as GregorianCalendar
            val pServise = PendingIntent.getBroadcast(this, 10, i, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (c2.timeInMillis > mkTime(c2[Calendar.YEAR], c2[Calendar.MONTH], c2[Calendar.DAY_OF_MONTH])) c2.add(Calendar.DATE, 1)
            am.setRepeating(AlarmManager.RTC_WAKEUP, mkTime(c2[Calendar.YEAR], c2[Calendar.MONTH], c2[Calendar.DAY_OF_MONTH]), 86400000L, pServise)*/
            setAlarm = false
        }
        if (setPadzeia) {
            setPadzeia = false
            setListPadzeia(this)
        }
        if (scroll) scrollView.post { scrollView.smoothScrollBy(0, scrollView.height) }
    }

    private fun mkDir() {
        var dir = File("$filesDir/Sabytie")
        if (!dir.exists()) {
            dir.mkdir()
        }
        dir = File("$filesDir/MaranAtaBel")
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
        dir = File("$filesDir/Site")
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                moveTaskToBack(true)
                prefEditors = k.edit()
                val allEntries = k.all
                for ((key) in allEntries) {
                    if (key.contains("Scroll")) {
                        prefEditors.putInt(key, 0)
                    }
                }
                prefEditors.putString("search_svityx_string", "")
                prefEditors.putString("search_string", "")
                prefEditors.putInt("search_position", 0)
                prefEditors.putInt("pegistr", 0)
                prefEditors.putInt("slovocalkam", 0)
                prefEditors.putInt("biblia_seash", 0)
                prefEditors.apply()
                setPadzeia = true
                setDataCalendar = -1
                checkBrightness = true
                super.onBackPressed()
            } else {
                back_pressed = System.currentTimeMillis()
                val layout = LinearLayout(this)
                if (dzenNoch) layout.setBackgroundResource(R.color.colorPrimary_black) else layout.setBackgroundResource(R.color.colorPrimary)
                val density = resources.displayMetrics.density
                val realpadding = (10 * density).toInt()
                val toast = TextViewRobotoCondensed(this)
                toast.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
                toast.setPadding(realpadding, realpadding, realpadding, realpadding)
                toast.text = getString(R.string.exit)
                toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2)
                layout.addView(toast)
                val mes = Toast(this)
                mes.duration = Toast.LENGTH_SHORT
                mes.view = layout
                mes.show()
            }
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun setPasxa(year: Int) {
        val menuPashalii = supportFragmentManager.findFragmentByTag("MenuPashalii") as? MenuPashalii
        menuPashalii?.setPasha(year)
    }

    override fun setPrazdnik(year: Int) {
        val menuCviaty = supportFragmentManager.findFragmentByTag("MenuCviaty") as? MenuCviaty
        menuCviaty?.setCviatyYear(year)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_glava) {
            var dayyear = 0
            for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until c.get(Calendar.YEAR)) {
                dayyear += if (c.isLeapYear(i)) 366
                else 365
            }
            setDataCalendar = dayyear + c.get(Calendar.DAY_OF_YEAR) - 1
            idOld = -1
            onClick(label1)
        }
        if (id == R.id.settings) {
            val i = Intent(this, SettingsActivity::class.java)
            startActivity(i)
        }
        if (id == R.id.onas) {
            val i = Intent(this@MainActivity, Onas::class.java)
            startActivity(i)
        }
        if (id == R.id.help) {
            val i = Intent(this, Help::class.java)
            startActivity(i)
        }
        if (id == R.id.pasxa) {
            val pasxa = DialogPasxa()
            pasxa.show(supportFragmentManager, "pasxa")
        }
        if (id == R.id.prazdnik) {
            val menuCviaty = supportFragmentManager.findFragmentByTag("MenuCviaty") as? MenuCviaty
            val year = menuCviaty?.getCviatyYear()?: Calendar.getInstance()[Calendar.YEAR]
            val prazdnik = DialogPrazdnik.getInstance(year)
            prazdnik.show(supportFragmentManager, "prazdnik")
        }
        if (id == R.id.tipicon) {
            val tipicon = DialogTipicon.getInstance(0)
            tipicon.show(supportFragmentManager, "tipicon")
        }
        if (id == R.id.search) {
            if (checkmoduleResources(this)) {
                val intent = Intent(this, Class.forName("by.carkva_gazeta.resources.SearchPesny"))
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }
        }
        if (id == R.id.sabytie) {
            val i = Intent(this, Sabytie::class.java)
            startActivityForResult(i, 105)
        }
        if (id == R.id.search_sviatyia) {
            val i = Intent(this, SearchSviatyia::class.java)
            startActivityForResult(i, 140)
        }
        if (id == R.id.action_mun) {
            if (onStart) {
                val gregorianCalendar = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
                for (i in 0 until setDataCalendar) {
                    gregorianCalendar.add(Calendar.DATE, 1)
                }
                val i = Intent(this, CaliandarMun::class.java)
                i.putExtra("mun", gregorianCalendar.get(Calendar.MONTH))
                i.putExtra("day", gregorianCalendar.get(Calendar.DATE))
                i.putExtra("year", gregorianCalendar.get(Calendar.YEAR))
                startActivityForResult(i, 105)
                onStart = false
            }
        }
        if (id == R.id.search_nadsan) {
            if (checkmoduleResources(this)) {
                val intent = Intent(this, Class.forName("by.carkva_gazeta.resources.SearchBiblia"))
                intent.putExtra("zavet", 3)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(supportFragmentManager, "dadatak")
            }
        }
        if (id == R.id.sortdate) {
            prefEditors = k.edit()
            if (item.isChecked)
                prefEditors.putInt("natatki_sort", 0)
            else
                prefEditors.putInt("natatki_sort", 1)
            prefEditors.apply()
            val menuNatatki = supportFragmentManager.findFragmentByTag("MenuNatatki") as? MenuNatatki
            menuNatatki?.sortAlfavit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 150 && resultCode == RESULT_OK) {
            downloadDynamicModule(this)
        }
        if (requestCode == 105) {
            if (data != null) {
                var dayyear = 0
                val day = data.getIntExtra("data", 0)
                val year = data.getIntExtra("year", c.get(Calendar.YEAR))
                idSelect = R.id.label1
                for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until year) {
                    dayyear += if (c.isLeapYear(i)) 366
                    else 365
                }
                if (setDataCalendar != dayyear + day) {
                    setDataCalendar = dayyear + day
                    idOld = -1
                    onClick(label1)
                }
            }
            onStart = true
        }
        if (requestCode == 140) {
            if (data != null) {
                var dayyear = 0
                val day = data.getIntExtra("data", 0)
                for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until c.get(Calendar.YEAR)) {
                    dayyear += if (c.isLeapYear(i)) 366
                    else 365
                }
                if (setDataCalendar != dayyear + day) {
                    setDataCalendar = dayyear + day
                    idOld = -1
                    onClick(label1)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (!(idSelect == R.id.label9a || idSelect == R.id.label10a)) {
            menu.findItem(R.id.action_add).isVisible = false
            menu.findItem(R.id.action_mun).isVisible = false
            menu.findItem(R.id.action_glava).isVisible = false
            menu.findItem(R.id.tipicon).isVisible = false
            menu.findItem(R.id.pasxa).isVisible = false
            menu.findItem(R.id.search).isVisible = false
            menu.findItem(R.id.trash).isVisible = false
            menu.findItem(R.id.sabytie).isVisible = false
            menu.findItem(R.id.prazdnik).isVisible = false
            menu.findItem(R.id.search_sviatyia).isVisible = false
            menu.findItem(R.id.search_nadsan).isVisible = false
            menu.findItem(R.id.sortdate).isVisible = false
            menu.findItem(R.id.action_font).isVisible = false
            menu.findItem(R.id.action_bright).isVisible = false
            menu.findItem(R.id.action_dzen_noch).isVisible = false
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
                R.id.label104 -> menu.findItem(R.id.pasxa).isVisible = true
                R.id.label7 -> {
                    menu.findItem(R.id.action_add).isVisible = true
                    menu.findItem(R.id.sortdate).isVisible = true
                    val sort = k.getInt("natatki_sort", 0)
                    menu.findItem(R.id.sortdate).isChecked = sort == 1
                }
                R.id.label12 -> menu.findItem(R.id.trash).isVisible = true
                R.id.label13 -> menu.findItem(R.id.search_nadsan).isVisible = true
            }
            if (idSelect == R.id.label91 || idSelect == R.id.label92 || idSelect == R.id.label93 || idSelect == R.id.label94 || idSelect == R.id.label95) {
                menu.findItem(R.id.search).isVisible = true
            }
            if (dzenNoch) {
                menu.findItem(R.id.action_mun).setIcon(R.drawable.calendar_black_full)
                menu.findItem(R.id.action_glava).setIcon(R.drawable.calendar_black)
            }
            menu.findItem(R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
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

    override fun onClick(view: View?) {
        idSelect = view?.id ?: 0
        if (!(idSelect == R.id.label9a || idSelect == R.id.label10a)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (!(idSelect == R.id.label9a || idSelect == R.id.label10a)) {
            if (dzenNoch) {
                label1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label4.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label5.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label6.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label7.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label8.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label91.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label92.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label93.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label94.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label95.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label101.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label102.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label103.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label104.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label105.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label11.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label12.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
                label13.setBackgroundColor(ContextCompat.getColor(this, R.color.colorbackground_material_dark))
            } else {
                label1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label4.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label5.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label6.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label7.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label8.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label91.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label92.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label93.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label94.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label95.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label101.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label102.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label103.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label104.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label105.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label11.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label12.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
                label13.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons))
            }
        }

        prefEditors = k.edit()
        if (idSelect == R.id.label91 || idSelect == R.id.label92 || idSelect == R.id.label93 || idSelect == R.id.label94 || idSelect == R.id.label95) {
            title9.visibility = View.VISIBLE
            if (dzenNoch)
                image2.setImageResource(R.drawable.arrow_up_float_black)
            else
                image2.setImageResource(R.drawable.arrow_up_float)
        }
        if (idSelect == R.id.label101 || idSelect == R.id.label102 || idSelect == R.id.label103 || idSelect == R.id.label104 || idSelect == R.id.label105) {
            title10.visibility = View.VISIBLE
            if (dzenNoch)
                image3.setImageResource(R.drawable.arrow_up_float_black)
            else
                image3.setImageResource(R.drawable.arrow_up_float)
        }

        if (idSelect == R.id.label9a) {
            if (title9.visibility == View.VISIBLE) {
                title9.visibility = View.GONE
                image2.setImageResource(R.drawable.arrow_down_float)
            } else {
                title9.visibility = View.VISIBLE
                if (dzenNoch)
                    image2.setImageResource(R.drawable.arrow_up_float_black)
                else
                    image2.setImageResource(R.drawable.arrow_up_float)
                scrollView.post { scrollView.smoothScrollBy(0, title9.height) }
            }
        }
        if (idSelect == R.id.label10a) {
            if (title10.visibility == View.VISIBLE) {
                title10.visibility = View.GONE
                image3.setImageResource(R.drawable.arrow_down_float)
            } else {
                title10.visibility = View.VISIBLE
                if (dzenNoch)
                    image3.setImageResource(R.drawable.arrow_up_float_black)
                else
                    image3.setImageResource(R.drawable.arrow_up_float)
                scrollView.post { scrollView.smoothScrollBy(0, title10.height) }
            }
        }
        when (idSelect) {
            R.id.label1 -> {
                tolbarTitle = getString(R.string.kaliandar2)
                if (dzenNoch)
                    label1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label2 -> {
                tolbarTitle = getString(R.string.SAJT)
                if (dzenNoch)
                    label2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label3 -> {
                tolbarTitle = getString(R.string.LITURGIKON)
                if (dzenNoch)
                    label3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label4 -> {
                tolbarTitle = getString(R.string.malitvy)
                if (dzenNoch)
                    label4.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label4.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label5 -> {
                tolbarTitle = getString(R.string.akafisty)
                if (dzenNoch)
                    label5.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label5.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label6 -> {
                tolbarTitle = getString(R.string.ruzanec)
                if (dzenNoch)
                    label6.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label6.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label7 -> {
                tolbarTitle = getString(R.string.MAJE_MALITVY)
                if (dzenNoch)
                    label7.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label7.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label8 -> {
                tolbarTitle = getString(R.string.title_biblia)
                if (dzenNoch)
                    label8.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label8.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label13 -> {
                tolbarTitle = getString(R.string.title_psalter)
                if (dzenNoch)
                    label13.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label13.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label91 -> {
                tolbarTitle = getString(R.string.pesny1)
                if (dzenNoch)
                    label91.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label91.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label92 -> {
                tolbarTitle = getString(R.string.pesny2)
                if (dzenNoch)
                    label92.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label92.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label93 -> {
                tolbarTitle = getString(R.string.pesny3)
                if (dzenNoch)
                    label93.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label93.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label94 -> {
                tolbarTitle = getString(R.string.pesny4)
                if (dzenNoch)
                    label94.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label94.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label95 -> {
                tolbarTitle = getString(R.string.pesny5)
                if (dzenNoch)
                    label95.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label95.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label103 -> {
                tolbarTitle = getString(R.string.CARKVA_SVIATY)
                if (dzenNoch)
                    label103.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else
                    label103
                            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label104 -> {
                tolbarTitle = getString(R.string.KALIANDAR_BEL)
                if (dzenNoch)
                    label104.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else
                    label104.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label105 -> {
                tolbarTitle = getString(R.string.parafii)
                if (dzenNoch)
                    label105.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else
                    label105
                            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label102 -> {
                tolbarTitle = getString(R.string.pamiatka)
                if (dzenNoch)
                    label102.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else
                    label102.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label101 -> {
                tolbarTitle = getString(R.string.spovedz)
                if (dzenNoch)
                    label101.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else
                    label101
                            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label11 -> {
                tolbarTitle = getString(R.string.bsinaidal)
                if (dzenNoch)
                    label11.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label11.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
            R.id.label12 -> {
                tolbarTitle = getString(R.string.MenuVybranoe)
                if (dzenNoch)
                    label12.setBackgroundColor(ContextCompat.getColor(this, R.color.colorprimary_material_dark))
                else label12.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDivider))
            }
        }

        if (idOld != idSelect) {
            val ftrans: FragmentTransaction = supportFragmentManager.beginTransaction()
            ftrans.setCustomAnimations(R.anim.alphainfragment, R.anim.alphaoutfragment)

            c = Calendar.getInstance() as GregorianCalendar
            if (idSelect != R.id.label2 && linear.visibility == View.VISIBLE)
                linear.visibility = View.GONE
            when (idSelect) {
                R.id.label1 -> {
                    var dayyear = 0
                    for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until c.get(Calendar.YEAR)) {
                        dayyear += if (c.isLeapYear(i)) 366
                        else 365
                    }
                    if (setDataCalendar == -1)
                        setDataCalendar = dayyear + c.get(Calendar.DAY_OF_YEAR) - 1
                    val caliandar: MenuCaliandar = MenuCaliandar.newInstance(setDataCalendar)
                    ftrans.replace(R.id.conteiner, caliandar)
                    prefEditors.putInt("id", idSelect)
                }
                R.id.label2 -> {
                    prefEditors.putInt("id", idSelect)
                    if (shortcuts) {
                        if (checkmoduleResources(this)) {
                            if (checkmodulesBiblijateka(this)) {
                                val intentBib = Intent(this, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                                intentBib.data = intent.data
                                if (intent.extras?.containsKey("filePath") == true)
                                    intentBib.putExtra("filePath", intent.extras?.getString("filePath"))
                                startActivity(intentBib)
                            } else {
                                downloadDynamicModule(this)
                            }
                        } else {
                            val dadatak = DialogInstallDadatak()
                            dadatak.show(supportFragmentManager, "dadatak")
                        }
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
                }
                R.id.label8 -> {
                    if (MaranAtaGlobalList.natatkiSemuxa == null) {
                        val file = File("$filesDir/BibliaSemuxaNatatki.json")
                        if (file.exists()) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                                MaranAtaGlobalList.natatkiSemuxa = gson.fromJson(file.readText(), type)
                            } catch (t: Throwable) {
                                file.delete()
                                MaranAtaGlobalList.natatkiSemuxa = ArrayList()
                            }
                        } else {
                            MaranAtaGlobalList.natatkiSemuxa = ArrayList()
                        }
                    }
                    if (MaranAtaGlobalList.zakladkiSemuxa == null) {
                        val file = File("$filesDir/BibliaSemuxaZakladki.json")
                        if (file.exists()) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<String>>() {}.type
                                MaranAtaGlobalList.zakladkiSemuxa = gson.fromJson(file.readText(), type)
                            } catch (t: Throwable) {
                                file.delete()
                                MaranAtaGlobalList.zakladkiSemuxa = ArrayList()
                            }
                        } else {
                            MaranAtaGlobalList.zakladkiSemuxa = ArrayList()
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
                    val menuPesnyPrasl = MenuPesnyPrasl()
                    ftrans.replace(R.id.conteiner, menuPesnyPrasl)
                }
                R.id.label92 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesnyBel = MenuPesnyBel()
                    ftrans.replace(R.id.conteiner, menuPesnyBel)
                }
                R.id.label93 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesnyBag = MenuPesnyBag()
                    ftrans.replace(R.id.conteiner, menuPesnyBag)
                }
                R.id.label94 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesnyKal = MenuPesnyKal()
                    ftrans.replace(R.id.conteiner, menuPesnyKal)
                }
                R.id.label95 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuPesnyTaize = MenuPesnyTaize()
                    ftrans.replace(R.id.conteiner, menuPesnyTaize)
                }
                R.id.label103 -> {
                    prefEditors.putInt("id", idSelect)
                    val menuCviaty = MenuCviaty()
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
                    if (MaranAtaGlobalList.natatkiSinodal == null) {
                        val file = File("$filesDir/BibliaSinodalNatatki.json")
                        if (file.exists()) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                                MaranAtaGlobalList.natatkiSinodal = gson.fromJson(file.readText(), type)
                            } catch (t: Throwable) {
                                file.delete()
                                MaranAtaGlobalList.natatkiSinodal = ArrayList()
                            }
                        } else {
                            MaranAtaGlobalList.natatkiSinodal = ArrayList()
                        }
                    }
                    if (MaranAtaGlobalList.zakladkiSinodal == null) {
                        val file = File("$filesDir/BibliaSinodalZakladki.json")
                        if (file.exists()) {
                            try {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<String>>() {}.type
                                MaranAtaGlobalList.zakladkiSinodal = gson.fromJson(file.readText(), type)
                            } catch (t: Throwable) {
                                file.delete()
                                MaranAtaGlobalList.zakladkiSinodal = ArrayList()
                            }
                        } else {
                            MaranAtaGlobalList.zakladkiSinodal = ArrayList()
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
            toolbar.postDelayed({ ftrans.commitAllowingStateLoss() }, 300)
            prefEditors.apply()
        }
        title_toolbar.text = tolbarTitle
        idOld = idSelect
    }

    /*private fun mkTime(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance() as GregorianCalendar
        calendar.set(year, month, day, 10, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }*/

    companion object {
        private var setAlarm = true
        var back_pressed = 0L
        var padzeia: ArrayList<Padzeia> = ArrayList()
        var setDataCalendar = -1
        var checkBrightness = true
        var setPadzeia = true
        private var SessionId = 0
        var onStart = true
        var brightness = 15
        var dialogVisable = false

        private fun getOrientation(context: Activity): Int {
            val rotation = context.windowManager.defaultDisplay.rotation
            val displayOrientation = context.resources.configuration.orientation

            if (displayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_180)
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_90)
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT

            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        fun setListPadzeia(activity: Activity) {
            Thread(Runnable {
                padzeia.clear()
                File(activity.filesDir.toString() + "/Sabytie").walk().forEach { file ->
                    if (file.isFile && file.exists()) {
                        val inputStream = FileReader(file)
                        val reader = BufferedReader(inputStream)
                        reader.forEachLine {
                            val line = it.trim()// { it <= ' ' }
                            if (line != "") {
                                val t1 = line.split(" ").toTypedArray()
                                try {
                                    if (t1.size == 11) padzeia.add(Padzeia(t1[0].replace("_", " "), t1[1], t1[2], t1[3].toLong(), t1[4].toInt(), t1[5], t1[6], t1[7], t1[8].toInt(), t1[9], t1[10], 0)) else padzeia.add(Padzeia(t1[0].replace("_", " "), t1[1], t1[2], t1[3].toLong(), t1[4].toInt(), t1[5], t1[6], t1[7], t1[8].toInt(), t1[9], t1[10], t1[11].toInt()))
                                } catch (e: Throwable) {
                                    file.delete()
                                }
                            }
                        }
                        inputStream.close()
                    }
                }
            }).start()
        }

        @SuppressLint("SetTextI18n")
        fun downloadDynamicModule(context: Activity) {
            val progressBarModule: ProgressBar = context.findViewById(R.id.progressBarModule)
            val layoutDialod: LinearLayout = context.findViewById(R.id.linear)
            val layoutDialod2: LinearLayout = context.findViewById(R.id.linear2)
            val text: TextViewRobotoCondensed = context.findViewById(R.id.textProgress)
            val k: SharedPreferences = context.getSharedPreferences("biblia", MODE_PRIVATE)
            val dzenNoch: Boolean = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                layoutDialod2.setBackgroundResource(R.color.colorbackground_material_dark)
                val maduleDownload: TextViewRobotoCondensed = context.findViewById(R.id.module_download)
                maduleDownload.setBackgroundResource(R.color.colorPrimary_black)
            }
            val splitInstallManager = SplitInstallManagerFactory.create(context)

            val request = SplitInstallRequest
                    .newBuilder()
                    .addModule("biblijateka")
                    .build()

            val listener = SplitInstallStateUpdatedListener {
                val state = it
                if (state.status() == SplitInstallSessionStatus.FAILED) {
                    downloadDynamicModule(context)
                    return@SplitInstallStateUpdatedListener
                }
                if (state.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                    splitInstallManager.startConfirmationDialogForResult(state, context, 150)
                }
                if (state.sessionId() == SessionId) {
                    val bytesDownload = (state.bytesDownloaded() / 1024.0 / 1024.0 * 100.0).roundToLong() / 100.0
                    val total = (state.totalBytesToDownload() / 1024.0 / 1024.0 * 100.0).roundToLong() / 100.0
                    when (state.status()) {
                        SplitInstallSessionStatus.PENDING -> {
                            context.requestedOrientation = getOrientation(context)
                            layoutDialod.visibility = View.VISIBLE
                            text.text = bytesDownload.toString().plus("Мб з ").plus(total).plus("Мб")
                        }
                        SplitInstallSessionStatus.DOWNLOADED -> {
                            layoutDialod.visibility = View.GONE
                            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                        SplitInstallSessionStatus.DOWNLOADING -> {
                            context.requestedOrientation = getOrientation(context)
                            layoutDialod.visibility = View.VISIBLE
                            progressBarModule.max = state.totalBytesToDownload().toInt()
                            progressBarModule.progress = state.bytesDownloaded().toInt()
                            text.text = bytesDownload.toString().plus("Мб з ").plus(total).plus("Мб")
                        }
                        SplitInstallSessionStatus.INSTALLED -> {
                            layoutDialod.visibility = View.GONE
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                SplitInstallHelper.updateAppInfo(context)
                                Handler().post {
                                    val intent = Intent(context, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                                    intent.data = context.intent.data
                                    if (intent.extras?.containsKey("filePath") == true) {
                                        intent.putExtra("filePath", intent.extras?.getString("filePath"))
                                    } else {
                                        intent.putExtra("site", true)
                                    }
                                    context.startActivity(intent)
                                }
                            } else {
                                val newContext = context.createPackageContext(context.packageName, 0)
                                val intent = Intent(newContext, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                                intent.data = context.intent.data
                                if (intent.extras?.containsKey("filePath") == true) {
                                    intent.putExtra("filePath", intent.extras?.getString("filePath"))
                                } else {
                                    intent.putExtra("site", true)
                                }
                                context.startActivity(intent)
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
                    val layout = LinearLayout(context)
                    if (dzenNoch) layout.setBackgroundResource(R.color.colorPrimary_black)
                    else layout.setBackgroundResource(R.color.colorPrimary)
                    val density = context.resources.displayMetrics.density
                    val realpadding = (10 * density).toInt()
                    val toast = TextViewRobotoCondensed(context)
                    toast.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
                    toast.setPadding(realpadding, realpadding, realpadding, realpadding)
                    toast.text = context.getString(R.string.no_internet)
                    toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2)
                    layout.addView(toast)
                    val mes = Toast(context)
                    mes.duration = Toast.LENGTH_LONG
                    mes.view = layout
                    mes.show()
                }
            }.addOnSuccessListener {
                SessionId = it
            }
        }

        fun checkmodulesBiblijateka(context: Context?): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                return true
            val muduls = SplitInstallManagerFactory.create(context).installedModules
            for (mod in muduls) {
                if (mod == "biblijateka") {
                    return true
                }
            }
            return false
        }

        fun checkmoduleResources(context: Context?): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                return true
            val muduls = SplitInstallManagerFactory.create(context).installedModules
            for (mod in muduls) {
                if (mod == "resources") {
                    return true
                }
            }
            return false
        }

        fun caliandar(context: Context?, mun: Int): Int {
            val filename = "caliandar".plus(mun)
            return context?.resources?.getIdentifier(filename, "raw", context.packageName)
                    ?: return 0
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
            cytanne = cytanne.replace("Нов_году.", "")
            cytanne = cytanne.replace("Вял.", "")
            cytanne = cytanne.replace("Арх.", "")
            cytanne = cytanne.replace("Абнаўл.", "")
            cytanne = cytanne.replace("Сьвятамуч.", "")
            cytanne = cytanne.replace("Саб.", "")
            cytanne = cytanne.replace("Першамуч.", "")
            cytanne = cytanne.replace("Суб.", "")
            cytanne = cytanne.replace("Нядз.", "")
            cytanne = cytanne.replace("Ганне", "")
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
//paralel = paralel.replace("Нав", "Нав")
//paralel = paralel.replace("Суд", "Суд")
            paralel = paralel.replace("Руфь", "Рут")
//paralel = paralel.replace("1 Цар", "1 Цар")
//paralel = paralel.replace("2 Цар", "2 Цар")
//paralel = paralel.replace("3 Цар", "3 Цар")
//paralel = paralel.replace("4 Цар", "4 Цар")
            paralel = paralel.replace("1 Пар", "1 Лет")
            paralel = paralel.replace("2 Пар", "2 Лет")
            paralel = paralel.replace("1 Езд", "1 Эзд")
            paralel = paralel.replace("Неем", "Нээм")
            paralel = paralel.replace("2 Езд", "2 Эзд")
            paralel = paralel.replace("Тов", "Тав")
            paralel = paralel.replace("Иудифь", "Юдт")
            paralel = paralel.replace("Есф", "Эст")
            paralel = paralel.replace("Иов", "Ёва")
//paralel = paralel.replace("Пс", "Пс")
            paralel = paralel.replace("Притч", "Высл")
            paralel = paralel.replace("Еккл", "Экл")
            paralel = paralel.replace("Песн", "Псн")
            paralel = paralel.replace("Прем", "Мдр")
            paralel = paralel.replace("Сир", "Сір")
            paralel = paralel.replace("Ис", "Іс")
            paralel = paralel.replace("Иер", "Ер")
//paralel = paralel.replace("Плач", "Плач")
            paralel = paralel.replace("Посл Иер", "Пасл Ер")
//paralel = paralel.replace("Вар", "Бар")
            paralel = paralel.replace("Иез", "Езк")
//paralel = paralel.replace("Дан", "Дан")
            paralel = paralel.replace("Ос", "Ас")
            paralel = paralel.replace("Иоил", "Ёіл")
//paralel = paralel.replace("Ам", "Ам")
            paralel = paralel.replace("Авд", "Аўдз")
            paralel = paralel.replace("Иона", "Ёны")
            paralel = paralel.replace("Мих", "Міх")
            paralel = paralel.replace("Наум", "Нвм")
            paralel = paralel.replace("Авв", "Абк")
            paralel = paralel.replace("Соф", "Саф")
            paralel = paralel.replace("Агг", "Аг")
//paralel = paralel.replace("Зах", "Зах")
//paralel = paralel.replace("Мал", "Мал")
//paralel = paralel.replace("1 Мак", "1 Мак")
//paralel = paralel.replace("2 Мак", "2 Мак")
//paralel = paralel.replace("3 Мак", "3 Мак")
            paralel = paralel.replace("3 Езд", "3 Эзд")
            paralel = paralel.replace("Мф", "Мц")
//paralel = paralel.replace("Мк", "Мк")
//paralel = paralel.replace("Лк", "Лк")
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
//paralel = paralel.replace("Гал", "Гал")
            paralel = paralel.replace("Еф", "Эф")
            paralel = paralel.replace("Флп", "Плп")
            paralel = paralel.replace("Кол", "Клс")
//paralel = paralel.replace("1 Фес", "1 Фес")
//paralel = paralel.replace("2 Фес", "2 Фес")
            paralel = paralel.replace("1 Тим", "1 Цім")
            paralel = paralel.replace("2 Тим", "2 Цім")
            paralel = paralel.replace("Тит", "Ціт")
//paralel = paralel.replace("Флм", "Флм")
            paralel = paralel.replace("Евр", "Гбр")
            paralel = paralel.replace("Откр", "Адкр")
            return paralel
        }

        @Suppress("DEPRECATION")
        fun fromHtml(html: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        }

        @Suppress("DEPRECATION")
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo ?: return false
                return activeNetworkInfo.isConnectedOrConnecting
            }
        }

        @Suppress("DEPRECATION")
        fun isIntNetworkAvailable(context: Context): Int {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return 0
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return 0
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 1
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 2
                    else -> 0
                }
            } else {
                val activeNetwork = connectivityManager.activeNetworkInfo ?: return 0
                return if (activeNetwork.isConnectedOrConnecting) { // connected to the internet
                    when (activeNetwork.type) {
                        ConnectivityManager.TYPE_WIFI -> 1 // connected to wifi
                        ConnectivityManager.TYPE_MOBILE -> 2 // connected to the mobile provider's
                        else -> 0
                    }
                } else 0 // not connected to the internet
            }
        }
    }
}
