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

class StaryZapaviet3 : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, PageFragmentStaryZapaviet.ClicParalelListiner, PageFragmentStaryZapaviet.ListPosition, PageFragmentStaryZapaviet.LongClicListiner {
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
        prefEditors.putString("bible_time_semuxa", gson.toJson(set))
        prefEditors.apply()
        clearEmptyPosition()
        val file = File("$filesDir/BibliaSemuxaStaryZavet/$kniga.json")
        if (vydelenie?.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            val outputStream = FileWriter(file)
            outputStream.write(gson.toJson(vydelenie))
            outputStream.close()
        }
        val fileZakladki = File("$filesDir/BibliaSemuxaZakladki.json")
        if (MaranAtaGlobalList.zakladkiSemuxa?.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            if (MaranAtaGlobalList.zakladkiSemuxa != null) {
                val outputStream = FileWriter(fileZakladki)
                outputStream.write(gson.toJson(MaranAtaGlobalList.zakladkiSemuxa))
                outputStream.close()
            }
        }
        val fileNatatki = File("$filesDir/BibliaSemuxaNatatki.json")
        if (MaranAtaGlobalList.natatkiSemuxa?.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            if (MaranAtaGlobalList.natatkiSemuxa != null) {
                val outputStream = FileWriter(fileNatatki)
                outputStream.write(gson.toJson(MaranAtaGlobalList.natatkiSemuxa))
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
            MaranAtaGlobalList.zakladkiSemuxa?.let {
                for (i in it.indices) {
                    if (it[i].contains(MainActivity.fromHtml(MaranAtaGlobalList.bible?.get(listPosition)
                                    ?: "").toString())) {
                        check = true
                        break
                    }
                }
            }
            if (!check) MaranAtaGlobalList.zakladkiSemuxa?.add(0, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (getmListGlava() + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (listPosition + 1) + "\n\n" + MainActivity.fromHtml(MaranAtaGlobalList.bible?.get(listPosition)
                    ?: "").toString())
            val layout = LinearLayout(this@StaryZapaviet3)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val toast = TextViewRobotoCondensed(this@StaryZapaviet3)
            toast.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
            toast.setPadding(realpadding, realpadding, realpadding, realpadding)
            if (!check) toast.text = "Дабаўлена у закладкі" else toast.text = "Закладка існуе"
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            layout.addView(toast)
            val mes = Toast(this@StaryZapaviet3)
            mes.duration = Toast.LENGTH_LONG
            mes.view = layout
            mes.show()
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
            setedit = true
        }
        zametka.setOnClickListener {
            var knigaReal = kniga
            when (kniga) {
                16 -> knigaReal = 19
                17 -> knigaReal = 20
                18 -> knigaReal = 21
                19 -> knigaReal = 22
                20 -> knigaReal = 23
                21 -> knigaReal = 24
                22 -> knigaReal = 27
                23 -> knigaReal = 28
                24 -> knigaReal = 29
                25 -> knigaReal = 32
                26 -> knigaReal = 33
                27 -> knigaReal = 34
                28 -> knigaReal = 35
                29 -> knigaReal = 36
                30 -> knigaReal = 37
                31 -> knigaReal = 38
                32 -> knigaReal = 39
                33 -> knigaReal = 40
                34 -> knigaReal = 41
                35 -> knigaReal = 42
                36 -> knigaReal = 43
                37 -> knigaReal = 44
                38 -> knigaReal = 45
            }
            val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (getmListGlava() + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (listPosition + 1)
            val zametka = getInstance(1, "0", knigaReal, getmListGlava(), listPosition, knigaName)
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
                bible.text = "Быцьцё"
                fullglav = 50
            }
            1 -> {
                bible.text = "Выхад"
                fullglav = 40
            }
            2 -> {
                bible.text = "Лявіт"
                fullglav = 27
            }
            3 -> {
                bible.text = "Лікі"
                fullglav = 36
            }
            4 -> {
                bible.text = "Другі Закон"
                fullglav = 34
            }
            5 -> {
                bible.text = "Ісуса сына Нава"
                fullglav = 24
            }
            6 -> {
                bible.text = "Судзьдзяў"
                fullglav = 21
            }
            7 -> {
                bible.text = "Рут"
                fullglav = 4
            }
            8 -> {
                bible.text = "1-я Царстваў"
                fullglav = 31
            }
            9 -> {
                bible.text = "2-я Царстваў"
                fullglav = 24
            }
            10 -> {
                bible.text = "3-я Царстваў"
                fullglav = 22
            }
            11 -> {
                bible.text = "4-я Царстваў"
                fullglav = 25
            }
            12 -> {
                bible.text = "1-я Летапісаў"
                fullglav = 29
            }
            13 -> {
                bible.text = "2-я Летапісаў"
                fullglav = 36
            }
            14 -> {
                bible.text = "Эздры"
                fullglav = 10
            }
            15 -> {
                bible.text = "Нээміі"
                fullglav = 13
            }
            16 -> {
                bible.text = "Эстэр"
                fullglav = 10
            }
            17 -> {
                bible.text = "Ёва"
                fullglav = 42
            }
            18 -> {
                bible.text = "Псалтыр"
                fullglav = 151
            }
            19 -> {
                bible.text = "Выслоўяў Саламонавых"
                fullglav = 31
            }
            20 -> {
                bible.text = "Эклезіяста"
                fullglav = 12
            }
            21 -> {
                bible.text = "Найвышэйшая Песьня Саламонава"
                fullglav = 8
            }
            22 -> {
                bible.text = "Ісаі"
                fullglav = 66
            }
            23 -> {
                bible.text = "Ераміі"
                fullglav = 52
            }
            24 -> {
                bible.text = "Ераміін Плач"
                fullglav = 5
            }
            25 -> {
                bible.text = "Езэкііля"
                fullglav = 48
            }
            26 -> {
                bible.text = "Данііла"
                fullglav = 12
            }
            27 -> {
                bible.text = "Асіі"
                fullglav = 14
            }
            28 -> {
                bible.text = "Ёіля"
                fullglav = 3
            }
            29 -> {
                bible.text = "Амоса"
                fullglav = 9
            }
            30 -> {
                bible.text = "Аўдзея"
                fullglav = 1
            }
            31 -> {
                bible.text = "Ёны"
                fullglav = 4
            }
            32 -> {
                bible.text = "Міхея"
                fullglav = 7
            }
            33 -> {
                bible.text = "Навума"
                fullglav = 3
            }
            34 -> {
                bible.text = "Абакума"
                fullglav = 3
            }
            35 -> {
                bible.text = "Сафона"
                fullglav = 3
            }
            36 -> {
                bible.text = "Агея"
                fullglav = 2
            }
            37 -> {
                bible.text = "Захарыі"
                fullglav = 14
            }
            38 -> {
                bible.text = "Малахіі"
                fullglav = 4
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
        val file = File("$filesDir/BibliaSemuxaStaryZavet/$kniga.json")
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.stary_zapaviet)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    private val knigaBible: String
        get() {
            var knigaName = ""
            when (kniga) {
                0 -> knigaName = "Быцьцё"
                1 -> knigaName = "Выхад"
                2 -> knigaName = "Лявіт"
                3 -> knigaName = "Лікі"
                4 -> knigaName = "Другі Закон"
                5 -> knigaName = "Ісуса сына Нава"
                6 -> knigaName = "Судзьдзяў"
                7 -> knigaName = "Рут"
                8 -> knigaName = "1-я Царстваў"
                9 -> knigaName = "2-я Царстваў"
                10 -> knigaName = "3-я Царстваў"
                11 -> knigaName = "4-я Царстваў"
                12 -> knigaName = "1-я Летапісаў"
                13 -> knigaName = "2-я Летапісаў"
                14 -> knigaName = "Эздры"
                15 -> knigaName = "Нээміі"
                16 -> knigaName = "Эстэр"
                17 -> knigaName = "Ёва"
                18 -> knigaName = "Псалтыр"
                19 -> knigaName = "Выслоўяў Саламонавых"
                20 -> knigaName = "Эклезіяста"
                21 -> knigaName = "Найвышэйшая Песьня Саламонава"
                22 -> knigaName = "Ісаі"
                23 -> knigaName = "Ераміі"
                24 -> knigaName = "Ераміін Плач"
                25 -> knigaName = "Езэкііля"
                26 -> knigaName = "Данііла"
                27 -> knigaName = "Асіі"
                28 -> knigaName = "Ёіля"
                29 -> knigaName = "Амоса"
                30 -> knigaName = "Аўдзея"
                31 -> knigaName = "Ёны"
                32 -> knigaName = "Міхея"
                33 -> knigaName = "Навума"
                34 -> knigaName = "Абакума"
                35 -> knigaName = "Сафона"
                36 -> knigaName = "Агея"
                37 -> knigaName = "Захарыі"
                38 -> knigaName = "Малахіі"
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
            title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.stary_zapaviet)
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
        val arrayList = pm.paralel(this@StaryZapaviet3, this.cytanneParalelnye, this.cytanneParalelnye, true)
        for (textView in arrayList) {
            conteiner.addView(textView)
        }
        scroll.visibility = View.VISIBLE
        bible.visibility = View.GONE
        pager.visibility = View.GONE
        title_toolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, cytanneSours)
        invalidateOptionsMenu()
    }

    internal inner class MyPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            var fullglav = 1
            when (kniga) {
                0 -> fullglav = 50
                1 -> fullglav = 40
                2 -> fullglav = 27
                3, 13 -> fullglav = 36
                4 -> fullglav = 34
                5, 9 -> fullglav = 24
                6 -> fullglav = 21
                7 -> fullglav = 4
                8 -> fullglav = 31
                10 -> fullglav = 22
                11 -> fullglav = 25
                12 -> fullglav = 29
                14 -> fullglav = 10
                15 -> fullglav = 13
                16 -> fullglav = 10
                17 -> fullglav = 42
                18 -> fullglav = 151
                19 -> fullglav = 31
                20 -> fullglav = 12
                21 -> fullglav = 8
                22 -> fullglav = 66
                23 -> fullglav = 52
                24 -> fullglav = 5
                25 -> fullglav = 48
                26 -> fullglav = 12
                27 -> fullglav = 14
                28 -> fullglav = 3
                29 -> fullglav = 9
                30 -> {
                }
                31 -> fullglav = 4
                32 -> fullglav = 7
                33 -> fullglav = 3
                34 -> fullglav = 3
                35 -> fullglav = 3
                36 -> fullglav = 2
                37 -> fullglav = 14
                38 -> fullglav = 4
            }
            return fullglav
        }

        override fun getItem(position: Int): Fragment {
            for (i in 0 until count) {
                if (position == i) {
                    val pazicia: Int = if (trak) {
                        if (glava != i) 0 else fierstPosition
                    } else 0
                    return PageFragmentStaryZapaviet.newInstance(i, kniga, pazicia)
                }
            }
            return PageFragmentStaryZapaviet.newInstance(0, kniga, 1)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (kniga == 18) resources.getString(by.carkva_gazeta.malitounik.R.string.psalom) + " " + (position + 1) else resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (position + 1)
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