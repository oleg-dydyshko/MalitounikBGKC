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
import by.carkva_gazeta.resources.PageFragmentNovyZapavietSinaidal.Companion.newInstance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_bible.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class NovyZapavietSinaidal3 : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, PageFragmentNovyZapavietSinaidal.ClicParalelListiner, PageFragmentNovyZapavietSinaidal.ListPosition, PageFragmentNovyZapavietSinaidal.LongClicListiner {
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
        set["zavet"] = 1
        set["kniga"] = kniga
        set["glava"] = pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("bible_time_sinodal", gson.toJson(set))
        prefEditors.apply()
        clearEmptyPosition()
        val file = File("$filesDir/BibliaSinodalNovyZavet/$kniga.json")
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
            val layout = LinearLayout(this@NovyZapavietSinaidal3)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val toast = TextViewRobotoCondensed(this@NovyZapavietSinaidal3)
            toast.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
            toast.setPadding(realpadding, realpadding, realpadding, realpadding)
            if (!check) toast.text = "Дабаўлена у закладкі" else toast.text = "Закладка існуе"
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            layout.addView(toast)
            val mes = Toast(this@NovyZapavietSinaidal3)
            mes.duration = Toast.LENGTH_LONG
            mes.view = layout
            mes.show()
            linearLayout4.visibility = View.GONE
            setmPedakVisable(false)
            setedit = true
        }
        zametka.setOnClickListener {
            val knigaBible = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (getmListGlava() + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (listPosition + 1)
            val zametka = getInstance(2, "1", kniga, getmListGlava(), listPosition, knigaBible)
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
                bible.text = "От Матфея"
                fullglav = 28
            }
            1 -> {
                bible.text = "От Марка"
                fullglav = 16
            }
            2 -> {
                bible.text = "От Луки"
                fullglav = 24
            }
            3 -> {
                bible.text = "От Иоанна"
                fullglav = 21
            }
            4 -> {
                bible.text = "Деяния святых апостолов"
                fullglav = 28
            }
            5 -> {
                bible.text = "Иакова"
                fullglav = 5
            }
            6 -> {
                bible.text = "1-е Петра"
                fullglav = 5
            }
            7 -> {
                bible.text = "2-е Петра"
                fullglav = 3
            }
            8 -> {
                bible.text = "1-е Иоанна"
                fullglav = 5
            }
            9 -> {
                bible.text = "2-е Иоанна"
                fullglav = 1
            }
            10 -> {
                bible.text = "3-е Иоанна"
                fullglav = 1
            }
            11 -> {
                bible.text = "Иуды"
                fullglav = 1
            }
            12 -> {
                bible.text = "Римлянам"
                fullglav = 16
            }
            13 -> {
                bible.text = "1-е Коринфянам"
                fullglav = 16
            }
            14 -> {
                bible.text = "2-е Коринфянам"
                fullglav = 13
            }
            15 -> {
                bible.text = "Галатам"
                fullglav = 6
            }
            16 -> {
                bible.text = "Ефесянам"
                fullglav = 6
            }
            17 -> {
                bible.text = "Филиппийцам"
                fullglav = 4
            }
            18 -> {
                bible.text = "Колоссянам"
                fullglav = 4
            }
            19 -> {
                bible.text = "1-е Фессалоникийцам (Солунянам)"
                fullglav = 5
            }
            20 -> {
                bible.text = "2-е Фессалоникийцам (Солунянам)"
                fullglav = 3
            }
            21 -> {
                bible.text = "1-е Тимофею"
                fullglav = 6
            }
            22 -> {
                bible.text = "2-е Тимофею"
                fullglav = 4
            }
            23 -> {
                bible.text = "Титу"
                fullglav = 3
            }
            24 -> {
                bible.text = "Филимону"
                fullglav = 1
            }
            25 -> {
                bible.text = "Евреям"
                fullglav = 13
            }
            26 -> {
                bible.text = "Откровение (Апокалипсис)"
                fullglav = 22
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
        val file = File("$filesDir/BibliaSinodalNovyZavet/$kniga.json")
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
        title_toolbar.setText(by.carkva_gazeta.malitounik.R.string.novy_zapaviet)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    private val knigaBible: String
        get() {
            var knigaName = ""
            when (kniga) {
                0 -> knigaName = "От Матфея"
                1 -> knigaName = "От Марка"
                2 -> knigaName = "От Луки"
                3 -> knigaName = "От Иоанна"
                4 -> knigaName = "Деяния святых апостолов"
                5 -> knigaName = "Иакова"
                6 -> knigaName = "1-е Петра"
                7 -> knigaName = "2-е Петра"
                8 -> knigaName = "1-е Иоанна"
                9 -> knigaName = "2-е Иоанна"
                10 -> knigaName = "3-е Иоанна"
                11 -> knigaName = "Иуды"
                12 -> knigaName = "Римлянам"
                13 -> knigaName = "1-е Коринфянам"
                14 -> knigaName = "2-е Коринфянам"
                15 -> knigaName = "Галатам"
                16 -> knigaName = "Ефесянам"
                17 -> knigaName = "Филиппийцам"
                18 -> knigaName = "Колоссянам"
                19 -> knigaName = "1-е Фессалоникийцам (Солунянам)"
                20 -> knigaName = "2-е Фессалоникийцам (Солунянам)"
                21 -> knigaName = "1-е Тимофею"
                22 -> knigaName = "2-е Тимофею"
                23 -> knigaName = "Титу"
                24 -> knigaName = "Филимону"
                25 -> knigaName = "Евреям"
                26 -> knigaName = "Откровение (Апокалипсис)"
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
        val arrayList = pm.paralel(this@NovyZapavietSinaidal3, this.cytanneSours, this.cytanneParalelnye, false)
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
                0, 4 -> fullglav = 28
                1, 13, 12 -> fullglav = 16
                2 -> fullglav = 24
                3 -> fullglav = 21
                5, 19, 8, 6 -> fullglav = 5
                7, 23, 20 -> fullglav = 3
                9, 24, 11, 10 -> {
                }
                14, 25 -> fullglav = 13
                15, 21, 16 -> fullglav = 6
                17, 22, 18 -> fullglav = 4
                26 -> fullglav = 22
            }
            return fullglav
        }

        override fun getItem(position: Int): Fragment {
            for (i in 0 until count) {
                if (position == i) {
                    val pazicia: Int = if (trak) {
                        if (glava != i) 0 else fierstPosition
                    } else 0
                    return newInstance(i, kniga, pazicia)
                }
            }
            return newInstance(0, kniga, 1)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (position + 1)
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