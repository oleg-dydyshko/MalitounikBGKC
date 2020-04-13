package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogBibleNatatka.Companion.getInstance
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.checkPosition
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.getmListGlava
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.listPosition
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.setmListGlava
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.setmPedakVisable
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.vydelenie
import by.carkva_gazeta.resources.DialogBibleRazdel.Companion.getInstance
import by.carkva_gazeta.resources.DialogBibleRazdel.DialogBibleRazdelListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_bible.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class StaryZapavietSinaidal3 : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, PageFragmentStaryZapavietSinaidal.ClicParalelListiner, PageFragmentStaryZapavietSinaidal.ListPosition, PageFragmentStaryZapavietSinaidal.LongClicListiner {
    private val mHideHandler = Handler()
    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        linealLayoutTitle.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private val mShowPart2Runnable = Runnable {
        val actionBar = supportActionBar
        actionBar?.show()
    }
    private var fullscreenPage = false
    private var trak = false
    private var paralel = false
    private var fullglav = 0
    private var kniga = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var dialog = true
    private var cytanneSours: String = ""
    private var cytanneParalelnye: String = ""
    private var setedit = false
    private var checkSetDzenNoch = false
    private val orientation: Int
        get() {
            val rotation = windowManager.defaultDisplay.rotation
            val displayOrientation = resources.configuration.orientation
            if (displayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                return if (rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_180) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_90) return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    private fun clearEmptyPosition() {
        val remove = ArrayList<ArrayList<Int>>()
        vydelenie?.let {
            for (i in it.indices) {
                var posrem = true
                for (e in 1 until it[i].size) {
                    if (it[i][e] == 1) {
                        posrem = false
                        break
                    }
                }
                if (posrem) {
                    remove.add(it[i])
                }
            }
            it.removeAll(remove)
        }
    }

    override fun onPause() {
        super.onPause()
        val prefEditors = k.edit()
        // Формат: Завет(1-Новый, 0-Старый) : Книга : Глава : Стих
        val set = ArrayMap<String, Int>()
        set["zavet"] = 0
        set["kniga"] = kniga
        set["glava"] = pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("bible_time_sinodal", gson.toJson(set))
        prefEditors.apply()
        clearEmptyPosition()
        val file = File("$filesDir/BibliaSinodalStaryZavet/$kniga.json")
        if (vydelenie?.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            val outputStream = FileWriter(file)
            outputStream.write(gson.toJson(vydelenie))
            outputStream.close()
        }
        val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
        if (MaranAtaGlobalList.zakladkiSinodal?.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            if (MaranAtaGlobalList.zakladkiSinodal != null) {
                val outputStream = FileWriter(fileZakladki)
                outputStream.write(gson.toJson(MaranAtaGlobalList.zakladkiSinodal))
                outputStream.close()
            }
        }
        val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
        if (MaranAtaGlobalList.natatkiSinodal?.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            if (MaranAtaGlobalList.natatkiSinodal != null) {
                val outputStream = FileWriter(fileNatatki)
                outputStream.write(gson.toJson(MaranAtaGlobalList.natatkiSinodal))
                outputStream.close()
            }
        }
        //MyBackupAgent.requestBackup(this);
    }

    override fun onDialogFontSizePositiveClick() {
        pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        pager.currentItem = glava
    }

    override fun getListPosition(position: Int) {
        fierstPosition = position
    }

    override fun onLongClick() {
        if (linearLayout4.visibility == View.VISIBLE) {
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
        } else {
            linearLayout4.visibility = View.VISIBLE
            setmPedakVisable(true)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        if (savedInstanceState != null) {
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bible)
        kniga = intent.extras?.getInt("kniga", 0) ?: 0
        glava = intent.extras?.getInt("glava", 0) ?: 0
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
            trak = true
        }
        setmListGlava(0)
        copy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", MainActivity.fromHtml(MaranAtaGlobalList.bible?.get(listPosition)
                    ?: "").toString())
            clipboard.setPrimaryClip(clip)
            val layout = LinearLayout(this)
            val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            if (dzenNoch) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val toast = TextViewRobotoCondensed(this)
            toast.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
            toast.setPadding(realpadding, realpadding, realpadding, realpadding)
            toast.setText(by.carkva_gazeta.malitounik.R.string.copy)
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            layout.addView(toast)
            val mes = Toast(this)
            mes.duration = Toast.LENGTH_LONG
            mes.view = layout
            mes.show()
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
        }
        yelloy.setOnClickListener {
            val i = checkPosition(getmListGlava(), listPosition)
            vydelenie?.let {
                if (i != -1) {
                    if (it[i][2] == 0) {
                        it[i][2] = 1
                    } else {
                        it[i][2] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(getmListGlava())
                    setVydelenie.add(listPosition)
                    setVydelenie.add(1)
                    setVydelenie.add(0)
                    setVydelenie.add(0)
                    it.add(setVydelenie)
                }
            }
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
        }
        underline.setOnClickListener {
            val i = checkPosition(getmListGlava(), listPosition)
            vydelenie?.let {
                if (i != -1) {
                    if (it[i][3] == 0) {
                        it[i][3] = 1
                    } else {
                        it[i][3] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(getmListGlava())
                    setVydelenie.add(listPosition)
                    setVydelenie.add(0)
                    setVydelenie.add(1)
                    setVydelenie.add(0)
                    it.add(setVydelenie)
                }
            }
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
        }
        bold.setOnClickListener {
            val i = checkPosition(getmListGlava(), listPosition)
            vydelenie?.let {
                if (i != -1) {
                    if (it[i][4] == 0) {
                        it[i][4] = 1
                    } else {
                        it[i][4] = 0
                    }
                } else {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(getmListGlava())
                    setVydelenie.add(listPosition)
                    setVydelenie.add(0)
                    setVydelenie.add(0)
                    setVydelenie.add(1)
                    it.add(setVydelenie)
                }
            }
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
        }
        zakladka.setOnClickListener {
            var check = false
            MaranAtaGlobalList.zakladkiSinodal?.let {
                for (i in it.indices) {
                    if (it[i].contains(MainActivity.fromHtml(MaranAtaGlobalList.bible?.get(listPosition)
                                    ?: "").toString())) {
                        check = true
                        break
                    }
                }
            }
            if (!check) MaranAtaGlobalList.zakladkiSinodal?.add(0, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (getmListGlava() + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (listPosition + 1) + "\n\n" + MainActivity.fromHtml(MaranAtaGlobalList.bible?.get(listPosition)
                    ?: "").toString())
            val layout = LinearLayout(this@StaryZapavietSinaidal3)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val toast = TextViewRobotoCondensed(this@StaryZapavietSinaidal3)
            toast.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
            toast.setPadding(realpadding, realpadding, realpadding, realpadding)
            if (!check) toast.text = "Дабаўлена у закладкі" else toast.text = "Закладка існуе"
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            layout.addView(toast)
            val mes = Toast(this@StaryZapavietSinaidal3)
            mes.duration = Toast.LENGTH_LONG
            mes.view = layout
            mes.show()
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
            setedit = true
        }
        zametka.setOnClickListener {
            val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (getmListGlava() + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (listPosition + 1)
            val zametka = getInstance(2, "0", kniga, getmListGlava(), listPosition, knigaName)
            zametka.show(supportFragmentManager, "bible_zametka")
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
            setedit = true
        }
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        for (i in 0 until pagerTabStrip.childCount) {
            val nextChild = pagerTabStrip.getChildAt(i)
            if (nextChild is TextView) {
                nextChild.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            }
        }
        val adapterViewPager: SmartFragmentStatePagerAdapter = MyPagerAdapter(supportFragmentManager)
        pager.adapter = adapterViewPager
        bible.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (dzenNoch) {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
            } else {
                window.statusBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
                window.navigationBarColor = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimaryDark)
            }
        }
        if (dzenNoch) {
            bible.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        }
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                setmListGlava(position)
                setmPedakVisable(false)
                linearLayout4.visibility = View.GONE
                if (glava != position) fierstPosition = 0
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        when (kniga) {
            0 -> {
                bible.text = "Бытие"
                fullglav = 50
            }
            1 -> {
                bible.text = "Исход"
                fullglav = 40
            }
            2 -> {
                bible.text = "Левит"
                fullglav = 27
            }
            3 -> {
                bible.text = "Числа"
                fullglav = 36
            }
            4 -> {
                bible.text = "Второзаконие"
                fullglav = 34
            }
            5 -> {
                bible.text = "Иисуса Навина"
                fullglav = 24
            }
            6 -> {
                bible.text = "Судей израилевых"
                fullglav = 21
            }
            7 -> {
                bible.text = "Руфи"
                fullglav = 4
            }
            8 -> {
                bible.text = "1-я Царств"
                fullglav = 31
            }
            9 -> {
                bible.text = "2-я Царств"
                fullglav = 24
            }
            10 -> {
                bible.text = "3-я Царств"
                fullglav = 22
            }
            11 -> {
                bible.text = "4-я Царств"
                fullglav = 25
            }
            12 -> {
                bible.text = "1-я Паралипоменон"
                fullglav = 29
            }
            13 -> {
                bible.text = "2-я Паралипоменон"
                fullglav = 37
            }
            14 -> {
                bible.text = "1-я Ездры"
                fullglav = 10
            }
            15 -> {
                bible.text = "Неемии"
                fullglav = 13
            }
            16 -> {
                bible.text = "2-я Ездры"
                fullglav = 9
            }
            17 -> {
                bible.text = "Товита"
                fullglav = 14
            }
            18 -> {
                bible.text = "Иудифи"
                fullglav = 16
            }
            19 -> {
                bible.text = "Есфири"
                fullglav = 10
            }
            20 -> {
                bible.text = "Иова"
                fullglav = 42
            }
            21 -> {
                bible.text = "Псалтирь"
                fullglav = 151
            }
            22 -> {
                bible.text = "Притчи Соломона"
                fullglav = 31
            }
            23 -> {
                bible.text = "Екклезиаста"
                fullglav = 12
            }
            24 -> {
                bible.text = "Песнь песней Соломона"
                fullglav = 8
            }
            25 -> {
                bible.text = "Премудрости Соломона"
                fullglav = 19
            }
            26 -> {
                bible.text = "Премудрости Иисуса, сына Сирахова"
                fullglav = 51
            }
            27 -> {
                bible.text = "Исаии"
                fullglav = 66
            }
            28 -> {
                bible.text = "Иеремии"
                fullglav = 52
            }
            29 -> {
                bible.text = "Плач Иеремии"
                fullglav = 5
            }
            30 -> {
                bible.text = "Послание Иеремии"
                fullglav = 1
            }
            31 -> {
                bible.text = "Варуха"
                fullglav = 5
            }
            32 -> {
                bible.text = "Иезекииля"
                fullglav = 48
            }
            33 -> {
                bible.text = "Даниила"
                fullglav = 14
            }
            34 -> {
                bible.text = "Осии"
                fullglav = 14
            }
            35 -> {
                bible.text = "Иоиля"
                fullglav = 3
            }
            36 -> {
                bible.text = "Амоса"
                fullglav = 9
            }
            37 -> {
                bible.text = "Авдия"
                fullglav = 1
            }
            38 -> {
                bible.text = "Ионы"
                fullglav = 4
            }
            39 -> {
                bible.text = "Михея"
                fullglav = 7
            }
            40 -> {
                bible.text = "Наума"
                fullglav = 3
            }
            41 -> {
                bible.text = "Аввакума"
                fullglav = 3
            }
            42 -> {
                bible.text = "Сафонии"
                fullglav = 3
            }
            43 -> {
                bible.text = "Аггея"
                fullglav = 2
            }
            44 -> {
                bible.text = "Захарии"
                fullglav = 14
            }
            45 -> {
                bible.text = "Малахии"
                fullglav = 4
            }
            46 -> {
                bible.text = "1-я Маккавейская"
                fullglav = 16
            }
            47 -> {
                bible.text = "2-я Маккавейская"
                fullglav = 15
            }
            48 -> {
                bible.text = "3-я Маккавейская"
                fullglav = 7
            }
            49 -> {
                bible.text = "3-я Ездры"
                fullglav = 16
            }
        }
        if (savedInstanceState != null) {
            dialog = savedInstanceState.getBoolean("dialog")
            paralel = savedInstanceState.getBoolean("paralel")
            cytanneSours = savedInstanceState.getString("cytanneSours") ?: ""
            cytanneParalelnye = savedInstanceState.getString("cytanneParalelnye") ?: ""
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            if (paralel) {
                setOnClic(cytanneParalelnye, cytanneSours)
            }
        }
        pager.currentItem = glava
        requestedOrientation = if (k.getBoolean("orientation", false)) {
            orientation
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        val file = File("$filesDir/BibliaSinodalStaryZavet/$kniga.json")
        if (file.exists()) {
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<Int?>?>?>() {}.type
            vydelenie = gson.fromJson<ArrayList<ArrayList<Int>>>(reader.readText(), type)
            inputStream.close()
        } else {
            vydelenie = ArrayList()
        }
    }

    private fun setTollbarTheme() {
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
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setText(by.carkva_gazeta.malitounik.R.string.stary_zapaviet)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    private val knigaBible: String
        get() {
            var knigaName = ""
            when (kniga) {
                0 -> knigaName = "Бытие"
                1 -> knigaName = "Исход"
                2 -> knigaName = "Левит"
                3 -> knigaName = "Числа"
                4 -> knigaName = "Второзаконие"
                5 -> knigaName = "Иисуса Навина"
                6 -> knigaName = "Судей израилевых"
                7 -> knigaName = "Руфи"
                8 -> knigaName = "1-я Царств"
                9 -> knigaName = "2-я Царств"
                10 -> knigaName = "3-я Царств"
                11 -> knigaName = "4-я Царств"
                12 -> knigaName = "1-я Паралипоменон"
                13 -> knigaName = "2-я Паралипоменон"
                14 -> knigaName = "1-я Ездры"
                15 -> knigaName = "Неемии"
                16 -> knigaName = "2-я Ездры"
                17 -> knigaName = "Товита"
                18 -> knigaName = "Иудифи"
                19 -> knigaName = "Есфири"
                20 -> knigaName = "Иова"
                21 -> knigaName = "Псалтирь"
                22 -> knigaName = "Притчи Соломона"
                23 -> knigaName = "Екклезиаста"
                24 -> knigaName = "Песнь песней Соломона"
                25 -> knigaName = "Премудрости Соломона"
                26 -> knigaName = "Премудрости Иисуса, сына Сирахова"
                27 -> knigaName = "Исаии"
                28 -> knigaName = "Иеремии"
                29 -> knigaName = "Плач Иеремии"
                30 -> knigaName = "Послание Иеремии"
                31 -> knigaName = "Варуха"
                32 -> knigaName = "Иезекииля"
                33 -> knigaName = "Даниила"
                34 -> knigaName = "Осии"
                35 -> knigaName = "Иоиля"
                36 -> knigaName = "Амоса"
                37 -> knigaName = "Авдия"
                38 -> knigaName = "Ионы"
                39 -> knigaName = "Михея"
                40 -> knigaName = "Наума"
                41 -> knigaName = "Аввакума"
                42 -> knigaName = "Сафонии"
                43 -> knigaName = "Аггея"
                44 -> knigaName = "Захарии"
                45 -> knigaName = "Малахии"
                46 -> knigaName = "1-я Маккавейская"
                47 -> knigaName = "2-я Маккавейская"
                48 -> knigaName = "3-я Маккавейская"
                49 -> knigaName = "3-я Ездры"
            }
            return knigaName
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("dialog", dialog)
        outState.putBoolean("paralel", paralel)
        outState.putString("cytanneSours", cytanneSours)
        outState.putString("cytanneParalelnye", cytanneParalelnye)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("checkSetDzenNoch", checkSetDzenNoch)
    }

    override fun onBackPressed() {
        if (paralel) {
            scroll.visibility = View.GONE
            bible.visibility = View.VISIBLE
            pager.visibility = View.VISIBLE
            title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.novy_zapaviet)
            paralel = false
            invalidateOptionsMenu()
        } else if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else if (linearLayout4.visibility == View.VISIBLE) {
            setmPedakVisable(false)
            linearLayout4.visibility = View.GONE
        } else {
            if (setedit) {
                setResult(500)
                finish()
            } else {
                if (checkSetDzenNoch) onSupportNavigateUp() else super.onBackPressed()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_glava).isVisible = !paralel
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_orientation).isChecked = k.getBoolean("orientation", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditors = k.edit()
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            checkSetDzenNoch = true
            val prefEditor = k.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_orientation) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                requestedOrientation = orientation
                prefEditors.putBoolean("orientation", true)
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                prefEditors.putBoolean("orientation", false)
            }
        }
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_glava) {
            val dialogBibleRazdel = getInstance(fullglav)
            dialogBibleRazdel.show(supportFragmentManager, "full_glav")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            if (k.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        prefEditors.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.biblia, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private fun hide() {
        val actionBar = supportActionBar
        actionBar?.hide()
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        linealLayoutTitle.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    override fun setOnClic(cytanneParalelnye: String?, cytanneSours: String?) {
        paralel = true
        this.cytanneParalelnye = cytanneParalelnye ?: ""
        this.cytanneSours = cytanneSours ?: ""
        val pm = ParalelnyeMesta()
        conteiner.removeAllViewsInLayout()
        val arrayList = pm.paralel(this@StaryZapavietSinaidal3, this.cytanneSours, this.cytanneParalelnye, false)
        for (textView in arrayList) {
            conteiner.addView(textView)
        }
        scroll.visibility = View.VISIBLE
        bible.visibility = View.GONE
        pager.visibility = View.GONE
        title_toolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, cytanneSours)
        invalidateOptionsMenu()
    }

    private inner class MyPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            var fullglav = 1
            when (kniga) {
                0 -> fullglav = 50
                1 -> fullglav = 40
                2 -> fullglav = 27
                3 -> fullglav = 36
                4 -> fullglav = 34
                5 -> fullglav = 24
                6 -> fullglav = 21
                7 -> fullglav = 4
                8 -> fullglav = 31
                9 -> fullglav = 24
                10 -> fullglav = 22
                11 -> fullglav = 25
                12 -> fullglav = 29
                13 -> fullglav = 37
                14 -> fullglav = 10
                15 -> fullglav = 13
                16 -> fullglav = 9
                17 -> fullglav = 14
                18 -> fullglav = 16
                19 -> fullglav = 10
                20 -> fullglav = 42
                21 -> fullglav = 151
                22 -> fullglav = 31
                23 -> fullglav = 12
                24 -> fullglav = 8
                25 -> fullglav = 19
                26 -> fullglav = 51
                27 -> fullglav = 66
                28 -> fullglav = 52
                29 -> fullglav = 5
                30, 37 -> {
                }
                31 -> fullglav = 5
                32 -> fullglav = 48
                33 -> fullglav = 14
                34 -> fullglav = 14
                35 -> fullglav = 3
                36 -> fullglav = 9
                38 -> fullglav = 4
                39 -> fullglav = 7
                40 -> fullglav = 3
                41 -> fullglav = 3
                42 -> fullglav = 3
                43 -> fullglav = 2
                44 -> fullglav = 14
                45 -> fullglav = 4
                46 -> fullglav = 16
                47 -> fullglav = 15
                48 -> fullglav = 7
                49 -> fullglav = 16
            }
            return fullglav
        }

        override fun getItem(position: Int): Fragment {
            for (i in 0 until count) {
                if (position == i) {
                    val pazicia: Int = if (trak) {
                        if (glava != i) 0 else fierstPosition
                    } else 0
                    return PageFragmentStaryZapavietSinaidal.newInstance(i, kniga, pazicia)
                }
            }
            return PageFragmentStaryZapavietSinaidal.newInstance(0, kniga, 1)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (kniga == 21) resources.getString(by.carkva_gazeta.malitounik.R.string.psinaidal) + " " + (position + 1) else resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (position + 1)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
        var fierstPosition = 0
    }
}