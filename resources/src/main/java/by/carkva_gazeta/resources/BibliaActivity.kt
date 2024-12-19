package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogBrightness
import by.carkva_gazeta.malitounik.DialogDzenNochSettings
import by.carkva_gazeta.malitounik.DialogFontSize
import by.carkva_gazeta.malitounik.DialogHelpFullScreenSettings
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuBibleBokuna
import by.carkva_gazeta.malitounik.MenuBibleCarniauski
import by.carkva_gazeta.malitounik.MenuBibleSemuxa
import by.carkva_gazeta.malitounik.MenuBibleSinoidal
import by.carkva_gazeta.malitounik.MenuVybranoe
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.VybranoeBibleList
import by.carkva_gazeta.malitounik.VybranoeData
import by.carkva_gazeta.resources.databinding.ActivityBibleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream

class BibliaActivity : BaseActivity(), BibliaPerakvadSemuxi, BibliaPerakvadNadsana, BibliaPerakvadBokuna, BibliaPerakvadCarniauski, BibliaPerakvadSinaidal, DialogFontSize.DialogFontSizeListener, DialogBibleRazdel.DialogBibleRazdelListener, BibleListiner, DialogBibleNatatka.DialogBibleNatatkaListiner, DialogAddZakladka.DialogAddZakladkiListiner, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener, DialogPerevodBiblii.DialogPerevodBibliiListener, ParalelnyeMesta {
    private var fullscreenPage = false
    private var paralel = false
    private var kniga = 0
    private var glava = 0
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var dialog = true
    private var cytanneSours = ""
    private var cytanneParalelnye = ""
    private var title = ""
    private var men = true
    private lateinit var binding: ActivityBibleBinding
    private var resetTollbarJob: Job? = null
    private var fierstPosition = 0
    private var novyZapavet = false
    private lateinit var adapter: MyPagerAdapter
    private var perevod = VybranoeBibleList.PEREVODSEMUXI
    private var isSetPerevod = false
    private var spid = 60
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var resetScreenJob: Job? = null
    private var mActionDown = false
    private var orientation = Configuration.ORIENTATION_PORTRAIT
    private var autoscroll = false
    private var mAutoScroll = true

    override fun addZakladka(color: Int, knigaBible: String, bible: String) {
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.addZakladka(color, knigaBible, bible)
        }
    }

    override fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getFileZavet(novyZapaviet, kniga)
            else -> File("")
        }
    }

    override fun getNamePerevod(): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getNamePerevod()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getNamePerevod()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getNamePerevod()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getNamePerevod()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getNamePerevod()
            else -> ""
        }
    }

    override fun getZakladki(): ArrayList<BibleZakladkiData> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getZakladki()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getZakladki()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getZakladki()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getZakladki()
            else -> ArrayList()
        }
    }

    override fun getTitlePerevod(): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getTitlePerevod()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getTitlePerevod()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getTitlePerevod()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getTitlePerevod()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getTitlePerevod()
            else -> ""
        }
    }

    override fun getSubTitlePerevod(glava: Int): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSubTitlePerevod(glava)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSubTitlePerevod(glava)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSubTitlePerevod(glava)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSubTitlePerevod(glava)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSubTitlePerevod(glava)
            else -> ""
        }
    }

    override fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSpisKnig(novyZapaviet)
            else -> arrayOf("")
        }
    }

    override fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getInputStream(novyZapaviet, kniga)
            else -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
        }
    }

    override fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.saveVydelenieZakladkiNtanki(glava, stix)
        }
    }

    override fun translatePsaltyr(psalm: Int, styx: Int, isUpdate: Boolean): Array<Int> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.translatePsaltyr(psalm, styx, isUpdate)
            else -> arrayOf(1, 1)
        }
    }

    override fun isPsaltyrGreek(): Boolean {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.isPsaltyrGreek()
            else -> true
        }
    }

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
        clearEmptyPosition()
        saveVydelenieZakladkiNtanki(novyZapavet, kniga, binding.pager.currentItem, fierstPosition)
        resetTollbarJob?.cancel()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        binding.pager.setCurrentItem(glava, false)
    }

    override fun isPanelVisible(widthPanel: Int) {
        val density = (resources.displayMetrics.density).toInt()
        val params = binding.actionFullscreen.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, widthPanel + 10 * density, 10 * density)
        binding.actionFullscreen.layoutParams = params
    }

    override fun getListPosition(position: Int) {
        fierstPosition = position
    }

    override fun isListEndPosition(page: Int) {
        autoscroll = false
        stopAutoScroll()
        invalidateOptionsMenu()
    }

    override fun getmActionDown(mActionDown: Boolean) {
        this.mActionDown = mActionDown
    }

    private fun getListDiffScroll(): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
        return (fragment?.getDiffScroll() ?: -1) == 0
    }

    override fun addZakladka(color: Int) {
        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
        fragment?.addZakladka(color)
    }

    fun getAutoScroll() = autoscroll

    override fun addNatatka() {
        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
        fragment?.addNatatka()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = ActivityBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            dialog = savedInstanceState.getBoolean("dialog")
            paralel = savedInstanceState.getBoolean("paralel")
            cytanneSours = savedInstanceState.getString("cytanneSours") ?: ""
            cytanneParalelnye = savedInstanceState.getString("cytanneParalelnye") ?: ""
            perevod = savedInstanceState.getString("perevod", VybranoeBibleList.PEREVODSEMUXI)
            kniga = savedInstanceState.getInt("kniga", 0)
            glava = savedInstanceState.getInt("glava", 0)
            novyZapavet = savedInstanceState.getBoolean("novyZapavet", false)
            if (paralel) {
                setOnClic(cytanneParalelnye, cytanneSours)
            }
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            perevod = intent.extras?.getString("perevod", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI
            kniga = intent.extras?.getInt("kniga", 0) ?: 0
            glava = if (intent.extras?.containsKey("kafizma") == true) {
                setKafizma(intent.extras?.getInt("kafizma", 1) ?: 1)
            } else {
                intent.extras?.getInt("glava", 0) ?: 0
            }
            novyZapavet = intent.extras?.getBoolean("novyZapavet", false) ?: false
        }
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
        }
        val title2 = getSpisKnig(novyZapavet)[kniga]
        val t1 = title2.indexOf("#")
        val t2 = title2.indexOf("#", t1 + 1)
        title = title2.substring(0, t1)
        BibleGlobalList.mListGlava = 0
        val glavyList = ArrayList<String>()
        for (i in 1..title2.substring(t1 + 1, t2).toInt()) {
            glavyList.add(resources.getString(R.string.psalom2) + " ${i - 1}")
        }
        adapter = MyPagerAdapter(glavyList, this)
        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            if (title2.substring(t2 + 1).toInt() == 21) tab.text = resources.getString(R.string.psalom2) + " " + (position + 1)
            else tab.text = getString(R.string.razdzel) + " " + (position + 1)
        }.attach()
        binding.pager.offscreenPageLimit = 1
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCREEN_STATE_ON) {
                    val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
                    fragment?.removeOnScrollListener()
                }
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
                    fragment?.addOnScrollListener()
                }
            }

            override fun onPageSelected(position: Int) {
                if (perevod == VybranoeBibleList.PEREVODNADSAN) {
                    binding.titleToolbar.text = getSubTitlePerevod(position)
                }
                BibleGlobalList.mListGlava = position
                men = VybranoeBibleList.checkVybranoe(title2.substring(t2 + 1).toInt(), position, getNamePerevod())
                if (glava != position && !isSetPerevod) fierstPosition = 0
                val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
                fragment?.addOnScrollListener()
                mAutoScroll = fragment?.getListEndPosition() ?: true
                if (mAutoScroll) {
                    if (k.getBoolean("autoscrollAutostart", false) && !getListDiffScroll()) {
                        stopAutoScroll()
                        autoStartScroll()
                    }
                } else {
                    isListEndPosition(binding.pager.currentItem)
                }
                invalidateOptionsMenu()
            }
        })
        men = VybranoeBibleList.checkVybranoe(kniga, glava, getNamePerevod())
        binding.pager.setCurrentItem(glava, false)
        loadVydelenie()
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        if (perevod == VybranoeBibleList.PEREVODNADSAN) {
            binding.titleToolbar.text = savedInstanceState?.getString("title") ?: getSubTitlePerevod(0)
            binding.subtitleToolbar.text = getTitlePerevod()
        } else {
            binding.titleToolbar.text = savedInstanceState?.getString("title") ?: title
            binding.subtitleToolbar.text = getSubTitlePerevod(0)
        }
        binding.pager.post {
            val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
            fragment?.addOnScrollListener()
            mAutoScroll = fragment?.getListEndPosition() ?: true
        }
        setTollbarTheme()
    }

    private fun loadVydelenie() {
        BibleGlobalList.vydelenie.clear()
        try {
            val file = getFileZavet(novyZapavet, kniga)
            if (file.exists()) {
                val inputStream = FileReader(file)
                val reader = BufferedReader(inputStream)
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
                BibleGlobalList.vydelenie.addAll(gson.fromJson(reader.readText(), type))
                inputStream.close()

            }
        } catch (_: Throwable) {
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            binding.linealLayoutTitle.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.toolbar.popupTheme = R.style.AppCompatDark
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
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
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
        outState.putString("title", binding.titleToolbar.text.toString())
        outState.putString("perevod", perevod)
        outState.putInt("kniga", kniga)
        outState.putInt("glava", glava)
        outState.putBoolean("novyZapavet", novyZapavet)
    }

    override fun onBack() {
        when {
            paralel -> {
                binding.scroll.visibility = View.GONE
                binding.pager.visibility = View.VISIBLE
                binding.tabLayout.visibility = View.VISIBLE
                binding.subtitleToolbar.visibility = View.VISIBLE
                if (perevod == VybranoeBibleList.PEREVODNADSAN) {
                    binding.titleToolbar.text = getSubTitlePerevod(0)
                } else {
                    binding.titleToolbar.text = title
                }
                paralel = false
                resetTollbar(binding.toolbar.layoutParams)
                invalidateOptionsMenu()
            }
            BibleGlobalList.mPedakVisable -> {
                val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
                if (fragment != null) {
                    fragment.onBackPressedFragment()
                } else {
                    val intent = Intent()
                    intent.putExtra("perevod", perevod)
                    intent.putExtra("kniga", kniga)
                    setResult(300, intent)
                    super.onBack()
                }
            }
            else -> {
                val intent = Intent()
                intent.putExtra("perevod", perevod)
                intent.putExtra("kniga", kniga)
                setResult(300, intent)
                super.onBack()
            }
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(R.id.action_auto)
        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
        if (fragment?.islinearLayout4Visable() == true || !mAutoScroll) {
            itemAuto.isVisible = false
        } else {
            if (paralel) {
                binding.subtitleToolbar.visibility = View.GONE
                itemAuto.isVisible = false
            } else {
                binding.subtitleToolbar.visibility = View.VISIBLE
                itemAuto.isVisible = true
            }
            mActionDown = false
        }
        when {
            autoscroll -> itemAuto.setIcon(R.drawable.scroll_icon_on)
            getListDiffScroll() -> itemAuto.setIcon(R.drawable.scroll_icon_up)
            else -> itemAuto.setIcon(R.drawable.scroll_icon)
        }
        val spanString = SpannableString(itemAuto.title.toString())
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString
        menu.findItem(R.id.action_glava).isVisible = !paralel && adapter.itemCount > 1
        menu.findItem(R.id.action_vybranoe).isVisible = !paralel
        menu.findItem(R.id.action_font).isVisible = !paralel
        menu.findItem(R.id.action_bright).isVisible = !paralel
        menu.findItem(R.id.action_dzen_noch).isVisible = !paralel
        menu.findItem(R.id.action_paralel).isChecked = k.getBoolean("paralel_maranata", true)
        menu.findItem(R.id.action_paralel).isVisible = perevod != VybranoeBibleList.PEREVODNADSAN
        val itemVybranoe = menu.findItem(R.id.action_vybranoe)
        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(R.string.vybranoe)
        }
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false) && perevod == VybranoeBibleList.PEREVODSEMUXI
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setPerevod(perevod: String) {
        isSetPerevod = true
        clearEmptyPosition()
        saveVydelenieZakladkiNtanki(novyZapavet, kniga, binding.pager.currentItem, fierstPosition)
        val list = getSpisKnig(novyZapavet)[kniga]
        val t1 = list.indexOf("#")
        val t2 = list.indexOf("#", t1 + 1)
        var myKniga = list.substring(t2 + 1).toInt()
        if (!novyZapavet) {
            var currentItem = binding.pager.currentItem + 1
            var glav = list.substring(t1 + 1, t2).toInt()
            val oldPerevod = this.perevod
            val oldPsaltyrGreek = isPsaltyrGreek()
            this.perevod = perevod
            var kniga2 = -1
            var myKniga2: Int
            var glav2 = 0
            val list2 = getSpisKnig(false)
            var isAdd = false
            var isAddBack = false
            if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI && myKniga == 30 && !novyZapavet) {
                myKniga = 31
                isAdd = true
            }
            if (perevod == VybranoeBibleList.PEREVODSINOIDAL && myKniga == 31 && !novyZapavet && currentItem == 6) {
                myKniga = 30
                isAddBack = true
            }
            var index = 0
            for (i in list2.indices) {
                val t3 = list2[i].indexOf("#")
                val t4 = list2[i].indexOf("#", t3 + 1)
                myKniga2 = list2[i].substring(t4 + 1).toInt()
                if (myKniga == myKniga2) {
                    index = i
                    glav2 = list2[i].substring(t3 + 1, t4).toInt()
                    kniga2 = myKniga2
                    break
                }
            }
            val dialog = supportFragmentManager.findFragmentByTag("DialogPerevodBiblii") as? DialogPerevodBiblii
            if (isAdd) {
                for (i in 1..6) {
                    if (i != 1) {
                        adapter.addFragment(i - 1)
                        val newTab = binding.tabLayout.newTab()
                        newTab.text = getString(R.string.razdzel) + " " + i
                        binding.tabLayout.addTab(newTab, i - 1)
                    }
                    val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(i - 1)) as? BibliaFragment
                    fragment?.loadBibleList(index, i)
                }
                currentItem = 6
                glav = 6
                glava = 6
                binding.pager.currentItem = glava - 1
            }
            if (isAddBack) {
                binding.pager.currentItem = 0
                for (i in 6 downTo 1) {
                    if (i != 1) {
                        adapter.removeFragment(i - 1)
                        binding.tabLayout.removeTabAt(i - 1)
                    } else {
                        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(0)) as? BibliaFragment
                        fragment?.loadBibleList(index, i)
                    }
                }
                currentItem = 1
                glav = 1
                glava = 1
            }
            if (kniga2 == -1) {
                dialog?.errorView(true)
                this.perevod = oldPerevod
            } else {
                if (currentItem > glav2) {
                    dialog?.errorView(true)
                    this.perevod = oldPerevod
                } else {
                    if (this.perevod != oldPerevod && glav != glav2) {
                        if (glav > glav2) {
                            adapter.removeFragment(glav2)
                            binding.tabLayout.removeTabAt(glav2)
                        } else {
                            adapter.addFragment(glav2)
                            val newTab = binding.tabLayout.newTab()
                            newTab.text = if (myKniga == 21) resources.getString(R.string.psalom2) + " " + glav2
                            else getString(R.string.razdzel) + " " + glav2
                            binding.tabLayout.addTab(newTab, glav2 - 1)
                        }

                    }
                    if (kniga2 == 21) {
                        val glava = binding.pager.currentItem + 1
                        val isGrec = oldPsaltyrGreek != isPsaltyrGreek()
                        var styx = fierstPosition + 1
                        val arrayPsaltyrStyx = translatePsaltyr(glava, styx, isGrec)
                        val newGlava = arrayPsaltyrStyx[0]
                        styx = arrayPsaltyrStyx[1]
                        binding.pager.currentItem = newGlava - 1
                        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
                        fragment?.setStyx(styx - 1)
                        fierstPosition = styx - 1
                    }
                    adapter.notifyDataSetChanged()
                    kniga = index
                    val title2 = getSpisKnig(false)[kniga]
                    val t3 = title2.indexOf("#")
                    title = title2.substring(0, t3)
                    if (perevod == VybranoeBibleList.PEREVODNADSAN) {
                        binding.titleToolbar.text = getSubTitlePerevod(0)
                        binding.subtitleToolbar.text = getTitlePerevod()
                    } else {
                        binding.titleToolbar.text = title
                        binding.subtitleToolbar.text = getSubTitlePerevod(0)
                    }
                    dialog?.errorView(false)
                }
            }
        } else {
            this.perevod = perevod
            val list2 = getSpisKnig(true)
            var index = 0
            for (i in list2.indices) {
                val t3 = list2[i].indexOf("#")
                val t4 = list2[i].indexOf("#", t3 + 1)
                val myKniga2 = list2[i].substring(t4 + 1).toInt()
                if (myKniga == myKniga2) {
                    index = i
                    break
                }
            }
            kniga = index
            val t3 = list2[index].indexOf("#")
            title = list2[index].substring(0, t3)
            if (perevod == VybranoeBibleList.PEREVODNADSAN) {
                binding.titleToolbar.text = getSubTitlePerevod(0)
                binding.subtitleToolbar.text = getTitlePerevod()
            } else {
                binding.titleToolbar.text = title
                binding.subtitleToolbar.text = getSubTitlePerevod(0)
            }
            binding.pager.adapter?.notifyDataSetChanged()
        }
        val title2 = getSpisKnig(novyZapavet)[kniga]
        val t3 = title2.indexOf("#")
        val t4 = title2.indexOf("#", t3 + 1)
        men = VybranoeBibleList.checkVybranoe(title2.substring(t4 + 1).toInt(), binding.pager.currentItem, getNamePerevod())
        invalidateOptionsMenu()
        loadVydelenie()
        isSetPerevod = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_paralel) {
            val prefEditor = k.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("paralel_maranata", true)
            } else {
                prefEditor.putBoolean("paralel_maranata", false)
            }
            prefEditor.apply()
            adapter.notifyDataSetChanged()
            return true
        }
        if (id == R.id.action_auto) {
            val prefEditor = k.edit()
            autoscroll = k.getBoolean("autoscroll", false)
            prefEditor.putBoolean("autoscrollAutostart", !autoscroll)
            prefEditor.apply()
            if (autoscroll) {
                Bogashlugbovya.isAutoStartScroll = false
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_perevod) {
            val list = getSpisKnig(false)[kniga]
            val t1 = list.indexOf("#")
            val t2 = list.indexOf("#", t1 + 1)
            val dialog = DialogPerevodBiblii.getInstance(true, list.substring(t2 + 1).toInt() == 21, perevod)
            dialog.show(supportFragmentManager, "DialogPerevodBiblii")
            return true
        }
        if (id == R.id.action_vybranoe) {
            men = VybranoeBibleList.setVybranoe(title, kniga, BibleGlobalList.mListGlava, getNamePerevod(), novyZapavet)
            if (men) {
                MainActivity.toastView(this, getString(R.string.addVybranoe))
                if (!VybranoeBibleList.checkVybranoe(getNamePerevod())) {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    if (MenuVybranoe.vybranoe.isEmpty()) {
                        val file = File("$filesDir/Vybranoe.json")
                        if (file.exists()) {
                            MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
                        }
                    }
                    MenuVybranoe.vybranoe.add(0, VybranoeData(Bogashlugbovya.vybranoeIndex(), getNamePerevod(), getTitlePerevod()))
                    val file = File("$filesDir/Vybranoe.json")
                    file.writer().use {
                        it.write(gson.toJson(MenuVybranoe.vybranoe, type))
                    }
                }
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
            return true
        }
        if (id == R.id.action_glava) {
            val dialogBibleRazdel = DialogBibleRazdel.getInstance(adapter.itemCount, binding.pager.currentItem)
            dialogBibleRazdel.show(supportFragmentManager, "full_glav")
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == R.id.action_carkva) {
            val intent = Intent()
            if (novyZapavet) intent.setClassName(this, MainActivity.ADMINNOVYZAPAVIETSEMUXA)
            else intent.setClassName(this, MainActivity.ADMINSTARYZAPAVIETSEMUXA)
            intent.putExtra("kniga", kniga)
            intent.putExtra("glava", BibleGlobalList.mListGlava)
            startActivity(intent)
            return true
        }
        if (id == R.id.action_fullscreen) {
            if (!k.getBoolean("fullscreenPage", false)) {
                var fullscreenCount = k.getInt("fullscreenCount", 0)
                if (fullscreenCount > 3) {
                    val dialogFullscreen = DialogHelpFullScreenSettings()
                    dialogFullscreen.show(supportFragmentManager, "DialogHelpFullScreenSettings")
                    fullscreenCount = 0
                } else {
                    fullscreenCount++
                    hide()
                }
                val prefEditor = k.edit()
                prefEditor.putInt("fullscreenCount", fullscreenCount)
                prefEditor.apply()
            } else {
                hide()
            }
            return true
        }
        return false
    }

    override fun dialogHelpFullScreenSettingsClose() {
        hide()
    }

    override fun onResume() {
        super.onResume()
        MenuBibleSemuxa.loadNatatkiZakladkiSemuxa(this)
        MenuBibleSinoidal.loadNatatkiZakladkiSinodal(this)
        MenuBibleBokuna.loadNatatkiZakladkiBokuna(this)
        MenuBibleCarniauski.loadNatatkiZakladkiCarniauski(this)
        if (fullscreenPage) {
            binding.linealLayoutTitle.post {
                hide()
            }
        }
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            when {
                Bogashlugbovya.isAutoStartScroll -> autoStartScroll()
                resources.configuration.orientation == orientation -> startAutoScroll()
                else -> autoStartScroll()
            }
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
            }
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            if (fullscreenPage && binding.actionBack.visibility == View.GONE) {
                val animation2 = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
                binding.actionBack.visibility = View.VISIBLE
                binding.actionBack.animation = animation2
            }
            autoScrollJob?.cancel()
            stopAutoStartScroll()
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (!getListDiffScroll()) {
            spid = k.getInt("autoscrollSpid", 60)
            if (binding.actionMinus.visibility == View.GONE) {
                binding.actionMinus.visibility = View.VISIBLE
                binding.actionPlus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
                binding.actionMinus.animation = animation
                binding.actionPlus.animation = animation
                val animation2 = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
                binding.actionBack.visibility = View.GONE
                binding.actionBack.animation = animation2
            }
            resetScreenJob?.cancel()
            stopAutoStartScroll()
            autoScroll()
        } else {
            val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
            fragment?.onSmoothScrollToTop()
            if (mAutoScroll) autoStartScroll()
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            autoscroll = true
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            invalidateOptionsMenu()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        val fragment = supportFragmentManager.findFragmentByTag("f" + adapter.getItemId(binding.pager.currentItem)) as? BibliaFragment
                        fragment?.onAutoScrollList()
                    }
                }
            }
        }
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            if (spid < 166) {
                val autoTime = (230 - spid) / 10
                var count = 0
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                        Bogashlugbovya.isAutoStartScroll = true
                        delay(1000L)
                        spid = 230
                        autoScroll()
                        while (true) {
                            delay(1000L)
                            if (!mActionDown && !MainActivity.dialogVisable) {
                                spid -= autoTime
                                count++
                            }
                            if (count == 10) {
                                break
                            }
                        }
                        Bogashlugbovya.isAutoStartScroll = false
                        startAutoScroll()
                    }
                }
            } else {
                startAutoScroll()
            }
        }
    }

    private fun stopAutoStartScroll() {
        autoStartScrollJob?.cancel()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.biblia, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun setOnClic(cytanneParalelnye: String, cytanneSours: String) {
        paralel = true
        this.cytanneParalelnye = cytanneParalelnye
        this.cytanneSours = cytanneSours
        binding.conteiner.text = paralel(this.cytanneParalelnye, getNamePerevod())
        binding.scroll.visibility = View.VISIBLE
        binding.pager.visibility = View.GONE
        binding.tabLayout.visibility = View.GONE
        binding.titleToolbar.text = resources.getString(R.string.paralel_smoll, cytanneSours)
        binding.subtitleToolbar.visibility = View.GONE
        val layoutParams = binding.toolbar.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.titleToolbar.isSingleLine = false
        binding.subtitleToolbar.isSingleLine = false
        binding.titleToolbar.isSelected = true
        invalidateOptionsMenu()
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.linealLayoutTitle)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.VISIBLE
        binding.actionBack.animation = animation
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowCompat.getInsetsController(window, binding.linealLayoutTitle)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = false
        }
    }

    private inner class MyPagerAdapter(private val items: ArrayList<String>, activity: BibliaActivity) : FragmentStateAdapter(activity) {

        private val pageIds = items.map { it.hashCode().toLong() }

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? BibliaFragment
            fragment?.upDateListView(kniga)
        }

        override fun getItemCount() = items.size

        override fun createFragment(position: Int): BibliaFragment {
            val styx = if (glava != position) 0
            else fierstPosition
            return BibliaFragment.newInstance(title, position, kniga, styx, novyZapavet)
        }

        fun addFragment(position: Int) {
            items.add(resources.getString(R.string.psalom2) + " $position")
            notifyItemInserted(position)
        }

        fun removeFragment(position: Int) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }

        override fun getItemId(position: Int): Long {
            return items[position].hashCode().toLong()
        }

        override fun containsItem(itemId: Long): Boolean {
            return pageIds.contains(itemId)
        }
    }
}