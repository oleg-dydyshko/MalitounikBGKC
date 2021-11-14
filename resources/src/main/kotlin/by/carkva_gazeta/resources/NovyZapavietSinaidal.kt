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
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.resources.DialogBibleRazdel.DialogBibleRazdelListener
import by.carkva_gazeta.resources.databinding.ActivityBibleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class NovyZapavietSinaidal : AppCompatActivity(), DialogFontSizeListener, DialogBibleRazdelListener, NovyZapavietSinaidalFragment.ClicParalelListiner, NovyZapavietSinaidalFragment.ListPositionListiner, DialogBibleNatatka.DialogBibleNatatkaListiner, DialogAddZakladka.DialogAddZakladkiListiner {

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
        BibleGlobalList.vydelenie.removeAll(remove)
    }

    override fun onPause() {
        super.onPause()
        val prefEditors = k.edit()
        val set = ArrayMap<String, Int>()
        set["zavet"] = 1
        set["kniga"] = kniga
        set["glava"] = binding.pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("bible_time_sinodal", gson.toJson(set))
        prefEditors.apply()
        clearEmptyPosition()
        val file = File("$filesDir/BibliaSinodalNovyZavet/$kniga.json")
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
        if (savedInstanceState != null) {
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
            setedit = savedInstanceState.getBoolean("setedit")
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
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
                title = "От Матфея"
                fullglav = 28
            }
            1 -> {
                title = "От Марка"
                fullglav = 16
            }
            2 -> {
                title = "От Луки"
                fullglav = 24
            }
            3 -> {
                title = "От Иоанна"
                fullglav = 21
            }
            4 -> {
                title = "Деяния святых апостолов"
                fullglav = 28
            }
            5 -> {
                title = "Иакова"
                fullglav = 5
            }
            6 -> {
                title = "1-е Петра"
                fullglav = 5
            }
            7 -> {
                title = "2-е Петра"
                fullglav = 3
            }
            8 -> {
                title = "1-е Иоанна"
                fullglav = 5
            }
            9 -> {
                title = "2-е Иоанна"
                fullglav = 1
            }
            10 -> {
                title = "3-е Иоанна"
                fullglav = 1
            }
            11 -> {
                title = "Иуды"
                fullglav = 1
            }
            12 -> {
                title = "Римлянам"
                fullglav = 16
            }
            13 -> {
                title = "1-е Коринфянам"
                fullglav = 16
            }
            14 -> {
                title = "2-е Коринфянам"
                fullglav = 13
            }
            15 -> {
                title = "Галатам"
                fullglav = 6
            }
            16 -> {
                title = "Ефесянам"
                fullglav = 6
            }
            17 -> {
                title = "Филиппийцам"
                fullglav = 4
            }
            18 -> {
                title = "Колоссянам"
                fullglav = 4
            }
            19 -> {
                title = "1-е Фессалоникийцам (Солунянам)"
                fullglav = 5
            }
            20 -> {
                title = "2-е Фессалоникийцам (Солунянам)"
                fullglav = 3
            }
            21 -> {
                title = "1-е Тимофею"
                fullglav = 6
            }
            22 -> {
                title = "2-е Тимофею"
                fullglav = 4
            }
            23 -> {
                title = "Титу"
                fullglav = 3
            }
            24 -> {
                title = "Филимону"
                fullglav = 1
            }
            25 -> {
                title = "Евреям"
                fullglav = 13
            }
            26 -> {
                title = "Откровение (Апокалипсис)"
                fullglav = 22
            }
        }
        for (i in 0 until fullglav) {
            val pazicia = if (glava != i) 0 else fierstPosition
            bibliaKnigi.add(BibliaData(i, kniga, pazicia))
        }
        BibleGlobalList.mListGlava = 0
        val adapterViewPager = MyPagerAdapter(this)
        binding.pager.adapter = adapterViewPager
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (position + 1)
        }.attach()
        binding.pager.offscreenPageLimit = 3
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                BibleGlobalList.mListGlava = position
                men = DialogVybranoeBibleList.checkVybranoe(this@NovyZapavietSinaidal, kniga, position, 2)
                if (glava != position) fierstPosition = 0
                invalidateOptionsMenu()
            }
        })
        men = DialogVybranoeBibleList.checkVybranoe(this, kniga, glava, 2)
        if (savedInstanceState != null) {
            dialog = savedInstanceState.getBoolean("dialog")
            paralel = savedInstanceState.getBoolean("paralel")
            cytanneSours = savedInstanceState.getString("cytanneSours") ?: ""
            cytanneParalelnye = savedInstanceState.getString("cytanneParalelnye") ?: ""
            if (paralel) {
                setOnClic(cytanneParalelnye, cytanneSours)
            }
        }
        binding.pager.setCurrentItem(glava, false)
        val file = File("$filesDir/BibliaSinodalNovyZavet/$kniga.json")
        if (file.exists()) {
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<Int?>?>?>() {}.type
            BibleGlobalList.vydelenie = gson.fromJson(reader.readText(), type)
            inputStream.close()
        }
        binding.titleToolbar.text = savedInstanceState?.getString("title") ?: getString(by.carkva_gazeta.malitounik.R.string.novsinaidal)
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
        outState.putBoolean("dialog", dialog)
        outState.putBoolean("paralel", paralel)
        outState.putString("cytanneSours", cytanneSours)
        outState.putString("cytanneParalelnye", cytanneParalelnye)
        outState.putBoolean("checkSetDzenNoch", checkSetDzenNoch)
        outState.putBoolean("setedit", setedit)
        outState.putString("title", binding.titleToolbar.text.toString())
    }

    override fun onBackPressed() {
        if (paralel) {
            binding.scroll.visibility = View.GONE
            binding.pager.visibility = View.VISIBLE
            binding.tabLayout.visibility = View.VISIBLE
            binding.subtitleToolbar.visibility = View.VISIBLE
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.novsinaidal)
            binding.subtitleToolbar.text = title
            paralel = false
            invalidateOptionsMenu()
        } else if (BibleGlobalList.mPedakVisable) {
            val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as BackPressedFragment
            fragment.onBackPressedFragment()
        } else {
            if (setedit || checkSetDzenNoch) {
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
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
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            checkSetDzenNoch = true
            men = DialogVybranoeBibleList.setVybranoe(this, title, kniga, BibleGlobalList.mListGlava, true, 2)
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
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_glava) {
            val dialogBibleRazdel = DialogBibleRazdel.getInstance(fullglav)
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
        prefEditors.apply()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
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
        binding.conteiner.text = pm.paralel(this@NovyZapavietSinaidal, this.cytanneSours, this.cytanneParalelnye, false).trim()
        binding.scroll.visibility = View.VISIBLE
        binding.pager.visibility = View.GONE
        binding.tabLayout.visibility = View.GONE
        binding.titleToolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, cytanneSours)
        binding.subtitleToolbar.visibility = View.GONE
        invalidateOptionsMenu()
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? NovyZapavietSinaidalFragment
            fragment?.upDateListView() ?: super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount() = bibliaKnigi.size

        override fun createFragment(position: Int) = NovyZapavietSinaidalFragment.newInstance(bibliaKnigi[position].glava, bibliaKnigi[position].kniga, bibliaKnigi[position].styx)
    }

    companion object {
        var fierstPosition = 0
    }
}