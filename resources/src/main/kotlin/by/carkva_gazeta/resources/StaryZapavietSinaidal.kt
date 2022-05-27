package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.resources.DialogBibleRazdel.Companion.getInstance
import by.carkva_gazeta.resources.DialogBibleRazdel.DialogBibleRazdelListener
import by.carkva_gazeta.resources.databinding.ActivityBibleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class StaryZapavietSinaidal : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, StaryZapavietSinaidalFragment.ClicParalelListiner, StaryZapavietSinaidalFragment.ListPositionListiner, DialogBibleNatatka.DialogBibleNatatkaListiner, DialogAddZakladka.DialogAddZakladkiListiner {

    private var fullscreenPage = false
    private var paralel = false
    private var fullglav = 0
    private var kniga = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private var dzenNoch = false
    private var dialog = true
    private var cytanneSours = ""
    private var cytanneParalelnye = ""
    private var setedit = false
    private var checkSetDzenNoch = false
    private var title = ""
    private var men = true
    private lateinit var binding: ActivityBibleBinding
    private var resetTollbarJob: Job? = null
    private var bibliaKnigi = ArrayList<BibliaData>()
    private var fierstPosition = 0

    private fun clearEmptyPosition() {
        val remove = ArrayList<ArrayList<Int>>()
        for (i in BibleGlobalList.vydelenie.indices) {
            var posrem = true
            for (e in 1 until BibleGlobalList.vydelenie[i].size) {
                if (BibleGlobalList.vydelenie[i][e] == 1) {
                    posrem = false
                    break
                }
            }
            if (posrem) {
                remove.add(BibleGlobalList.vydelenie[i])
            }
        }
        BibleGlobalList.vydelenie.removeAll(remove.toSet())
    }

    override fun onPause() {
        super.onPause()
        val prefEditors = k.edit()
        // Формат: Завет(1-Новый, 0-Старый) : Книга : Глава : Стих
        val set = ArrayMap<String, Int>()
        set["zavet"] = 0
        set["kniga"] = kniga
        set["glava"] = binding.pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("bible_time_sinodal", gson.toJson(set))
        prefEditors.apply()
        clearEmptyPosition()
        val file = File("$filesDir/BibliaSinodalStaryZavet/$kniga.json")
        if (BibleGlobalList.vydelenie.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            file.writer().use {
                it.write(gson.toJson(BibleGlobalList.vydelenie))
            }
        }
        val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
        if (BibleGlobalList.zakladkiSinodal.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            fileZakladki.writer().use {
                it.write(gson.toJson(BibleGlobalList.zakladkiSinodal))
            }
        }
        val fileNatatki = File("$filesDir/BibliaSinodalNatatki.json")
        if (BibleGlobalList.natatkiSinodal.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            fileNatatki.writer().use {
                it.write(gson.toJson(BibleGlobalList.natatkiSinodal))
            }
        }
        resetTollbarJob?.cancel()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        binding.pager.setCurrentItem(glava, false)
    }

    override fun getListPosition(position: Int) {
        fierstPosition = position
    }

    override fun setEdit(edit: Boolean) {
        setedit = edit
    }

    override fun addZakladka(color: Int) {
        val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as BackPressedFragment
        fragment.addZakladka(color)
    }

    override fun addNatatka() {
        val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as BackPressedFragment
        fragment.addNatatka()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        checkSetDzenNoch = dzenNoch
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = ActivityBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        kniga = intent.extras?.getInt("kniga", 0) ?: 0
        glava = intent.extras?.getInt("glava", 0) ?: 0
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
        }
        when (kniga) {
            0 -> {
                title = "Бытие"
                fullglav = 50
            }
            1 -> {
                title = "Исход"
                fullglav = 40
            }
            2 -> {
                title = "Левит"
                fullglav = 27
            }
            3 -> {
                title = "Числа"
                fullglav = 36
            }
            4 -> {
                title = "Второзаконие"
                fullglav = 34
            }
            5 -> {
                title = "Иисуса Навина"
                fullglav = 24
            }
            6 -> {
                title = "Судей израилевых"
                fullglav = 21
            }
            7 -> {
                title = "Руфи"
                fullglav = 4
            }
            8 -> {
                title = "1-я Царств"
                fullglav = 31
            }
            9 -> {
                title = "2-я Царств"
                fullglav = 24
            }
            10 -> {
                title = "3-я Царств"
                fullglav = 22
            }
            11 -> {
                title = "4-я Царств"
                fullglav = 25
            }
            12 -> {
                title = "1-я Паралипоменон"
                fullglav = 29
            }
            13 -> {
                title = "2-я Паралипоменон"
                fullglav = 37
            }
            14 -> {
                title = "1-я Ездры"
                fullglav = 10
            }
            15 -> {
                title = "Неемии"
                fullglav = 13
            }
            16 -> {
                title = "2-я Ездры"
                fullglav = 9
            }
            17 -> {
                title = "Товита"
                fullglav = 14
            }
            18 -> {
                title = "Иудифи"
                fullglav = 16
            }
            19 -> {
                title = "Есфири"
                fullglav = 10
            }
            20 -> {
                title = "Иова"
                fullglav = 42
            }
            21 -> {
                title = "Псалтирь"
                fullglav = 151
            }
            22 -> {
                title = "Притчи Соломона"
                fullglav = 31
            }
            23 -> {
                title = "Екклезиаста"
                fullglav = 12
            }
            24 -> {
                title = "Песнь песней Соломона"
                fullglav = 8
            }
            25 -> {
                title = "Премудрости Соломона"
                fullglav = 19
            }
            26 -> {
                title = "Премудрости Иисуса, сына Сирахова"
                fullglav = 51
            }
            27 -> {
                title = "Исаии"
                fullglav = 66
            }
            28 -> {
                title = "Иеремии"
                fullglav = 52
            }
            29 -> {
                title = "Плач Иеремии"
                fullglav = 5
            }
            30 -> {
                title = "Послание Иеремии"
                fullglav = 1
            }
            31 -> {
                title = "Варуха"
                fullglav = 5
            }
            32 -> {
                title = "Иезекииля"
                fullglav = 48
            }
            33 -> {
                title = "Даниила"
                fullglav = 14
            }
            34 -> {
                title = "Осии"
                fullglav = 14
            }
            35 -> {
                title = "Иоиля"
                fullglav = 3
            }
            36 -> {
                title = "Амоса"
                fullglav = 9
            }
            37 -> {
                title = "Авдия"
                fullglav = 1
            }
            38 -> {
                title = "Ионы"
                fullglav = 4
            }
            39 -> {
                title = "Михея"
                fullglav = 7
            }
            40 -> {
                title = "Наума"
                fullglav = 3
            }
            41 -> {
                title = "Аввакума"
                fullglav = 3
            }
            42 -> {
                title = "Сафонии"
                fullglav = 3
            }
            43 -> {
                title = "Аггея"
                fullglav = 2
            }
            44 -> {
                title = "Захарии"
                fullglav = 14
            }
            45 -> {
                title = "Малахии"
                fullglav = 4
            }
            46 -> {
                title = "1-я Маккавейская"
                fullglav = 16
            }
            47 -> {
                title = "2-я Маккавейская"
                fullglav = 15
            }
            48 -> {
                title = "3-я Маккавейская"
                fullglav = 7
            }
            49 -> {
                title = "3-я Ездры"
                fullglav = 16
            }
        }
        for (i in 0 until fullglav) {
            val pazicia = if (glava != i) 0 else fierstPosition
            bibliaKnigi.add(BibliaData(i, kniga, pazicia))
        }
        BibleGlobalList.mListGlava = 0
        binding.pager.adapter = MyPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = if (kniga == 21) resources.getString(by.carkva_gazeta.malitounik.R.string.psinaidal) + " " + (position + 1) else resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (position + 1)
        }.attach()
        binding.pager.offscreenPageLimit = 3
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                BibleGlobalList.mListGlava = position
                men = DialogVybranoeBibleList.checkVybranoe(this@StaryZapavietSinaidal, kniga, position, 2)
                if (glava != position) fierstPosition = 0
                invalidateOptionsMenu()
            }
        })
        men = DialogVybranoeBibleList.checkVybranoe(this, kniga, glava, 2)
        if (savedInstanceState != null) {
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
            setedit = savedInstanceState.getBoolean("setedit")
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            dialog = savedInstanceState.getBoolean("dialog")
            paralel = savedInstanceState.getBoolean("paralel")
            cytanneSours = savedInstanceState.getString("cytanneSours") ?: ""
            cytanneParalelnye = savedInstanceState.getString("cytanneParalelnye") ?: ""
            if (paralel) {
                setOnClic(cytanneParalelnye, cytanneSours)
            }
        }
        binding.pager.setCurrentItem(glava, false)
        val file = File("$filesDir/BibliaSinodalStaryZavet/$kniga.json")
        if (file.exists()) {
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<Int?>?>?>() {}.type
            BibleGlobalList.vydelenie = gson.fromJson(reader.readText(), type)
            inputStream.close()
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBackPressed()
        }
        binding.titleToolbar.text = savedInstanceState?.getString("title") ?: getText(by.carkva_gazeta.malitounik.R.string.stsinaidal)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.subtitleToolbar.text = title
        if (dzenNoch) {
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
            }
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
        binding.subtitleToolbar.isSingleLine = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("dialog", dialog)
        outState.putBoolean("paralel", paralel)
        outState.putString("cytanneSours", cytanneSours)
        outState.putString("cytanneParalelnye", cytanneParalelnye)
        outState.putBoolean("checkSetDzenNoch", checkSetDzenNoch)
        outState.putBoolean("setedit", setedit)
        outState.putString("title", binding.titleToolbar.text.toString())
    }

    override fun onBackPressed() {
        when {
            paralel -> {
                binding.scroll.visibility = View.GONE
                binding.pager.visibility = View.VISIBLE
                binding.tabLayout.visibility = View.VISIBLE
                binding.subtitleToolbar.visibility = View.VISIBLE
                binding.titleToolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.stsinaidal)
                binding.subtitleToolbar.text = title
                paralel = false
                invalidateOptionsMenu()
            }
            BibleGlobalList.mPedakVisable -> {
                val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as BackPressedFragment
                fragment.onBackPressedFragment()
            }
            setedit || checkSetDzenNoch != dzenNoch -> onSupportNavigateUp()
            else -> super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_glava).isVisible = !paralel
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).isVisible = !paralel
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_font).isVisible = !paralel
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_bright).isVisible = !paralel
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isVisible = !paralel
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        val itemVybranoe: MenuItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditors = k.edit()
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            men = DialogVybranoeBibleList.setVybranoe(this, title, kniga, BibleGlobalList.mListGlava, bibleName = 2)
            if (men) {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.addVybranoe))
                if (!DialogVybranoeBibleList.checkVybranoe("2")) {
                    MenuVybranoe.vybranoe.add(0, VybranoeData(Bogashlugbovya.vybranoeIndex(), "2", getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)))
                    val gson = Gson()
                    val file = File("$filesDir/Vybranoe.json")
                    file.writer().use {
                        it.write(gson.toJson(MenuVybranoe.vybranoe))
                    }
                }
            }
            invalidateOptionsMenu()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            val prefEditor = k.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            dzenNoch = item.isChecked
            prefEditor.apply()
            recreate()
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
            hide()
        }
        prefEditors.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        fullscreenPage = k.getBoolean("fullscreenPage", false)
        if (fullscreenPage) {
            binding.linealLayoutTitle.post {
                hide()
            }
        }
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

    override fun setOnClic(cytanneParalelnye: String?, cytanneSours: String?) {
        paralel = true
        this.cytanneParalelnye = cytanneParalelnye ?: ""
        this.cytanneSours = cytanneSours ?: ""
        val pm = ParalelnyeMesta()
        binding.conteiner.text = pm.paralel(this.cytanneParalelnye, false).trim()
        binding.scroll.visibility = View.VISIBLE
        binding.pager.visibility = View.GONE
        binding.tabLayout.visibility = View.GONE
        binding.titleToolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, cytanneSours)
        binding.subtitleToolbar.visibility = View.GONE
        invalidateOptionsMenu()
    }

    private fun hide() {
        fullscreenPage = true
        val prefEditor = k.edit()
        prefEditor.putBoolean("fullscreenPage", true)
        prefEditor.apply()
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = ViewCompat.getWindowInsetsController(binding.linealLayoutTitle)
        controller?.let {
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsetsCompat.Type.systemBars())
        }
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.VISIBLE
        binding.actionBack.animation = animation
    }

    private fun show() {
        fullscreenPage = false
        val prefEditor = k.edit()
        prefEditor.putBoolean("fullscreenPage", false)
        prefEditor.apply()
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = ViewCompat.getWindowInsetsController(binding.linealLayoutTitle)
        controller?.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? StaryZapavietSinaidalFragment
            fragment?.upDateListView() ?: super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount() = bibliaKnigi.size

        override fun createFragment(position: Int) = StaryZapavietSinaidalFragment.newInstance(bibliaKnigi[position].glava, bibliaKnigi[position].kniga, bibliaKnigi[position].styx)
    }
}