package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import androidx.collection.ArrayMap
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.resources.DialogBibleRazdel.Companion.getInstance
import by.carkva_gazeta.resources.DialogBibleRazdel.DialogBibleRazdelListener
import by.carkva_gazeta.resources.databinding.ActivityBibleBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.File

class NadsanContentActivity : BaseActivity(), DialogFontSizeListener, DialogBibleRazdelListener, BibleListiner {

    private var fullscreenPage = false
    private var glava = 0
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var dialog = true
    private var men = true
    private lateinit var binding: ActivityBibleBinding
    private var resetTollbarJob: Job? = null
    private var fierstPosition = 0

    override fun onPause() {
        super.onPause()
        val prefEditors = k.edit()
        val set = ArrayMap<String, Int>()
        set["glava"] = binding.pager.currentItem
        set["stix"] = fierstPosition
        val gson = Gson()
        prefEditors.putString("psalter_time_psalter_nadsan", gson.toJson(set))
        prefEditors.apply()
        resetTollbarJob?.cancel()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    override fun onComplete(glava: Int) {
        binding.pager.setCurrentItem(glava, false)
    }

    override fun setOnClic(cytanneParalelnye: String, cytanneSours: String) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", MODE_PRIVATE)
        binding = ActivityBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        glava = if (intent.extras?.containsKey("kafizma") == true) {
            setKafizma(intent.extras?.getInt("kafizma", 1) ?: 1)
        } else {
            intent.extras?.getInt("glava", 0) ?: 0
        }
        if (intent.extras?.containsKey("stix") == true) {
            fierstPosition = intent.extras?.getInt("stix", 0) ?: 0
        }
        binding.pager.adapter = MyPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = resources.getString(R.string.psalom2) + " " + (position + 1)
        }.attach()
        binding.pager.offscreenPageLimit = 3
        binding.titleToolbar.text = getString(R.string.psalter)
        binding.subtitleToolbar.text = getString(R.string.kafizma2, getKafizma(glava))
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (glava != position) fierstPosition = 0
                binding.subtitleToolbar.text = getString(R.string.kafizma2, getKafizma(position))
                men = DialogVybranoeBibleList.checkVybranoe(0, position, 3)
                invalidateOptionsMenu()
            }
        })
        men = DialogVybranoeBibleList.checkVybranoe(0, glava, 3)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            dialog = savedInstanceState.getBoolean("dialog")
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBackPressed()
        }
        binding.pager.setCurrentItem(glava, false)
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

    private fun getKafizma(psalter: Int): Int {
        var psalter1 = psalter
        psalter1++
        var kafizma = 1
        if (psalter1 in 9..16) kafizma = 2
        if (psalter1 in 17..23) kafizma = 3
        if (psalter1 in 24..31) kafizma = 4
        if (psalter1 in 32..36) kafizma = 5
        if (psalter1 in 37..45) kafizma = 6
        if (psalter1 in 46..54) kafizma = 7
        if (psalter1 in 55..63) kafizma = 8
        if (psalter1 in 64..69) kafizma = 9
        if (psalter1 in 70..76) kafizma = 10
        if (psalter1 in 77..84) kafizma = 11
        if (psalter1 in 85..90) kafizma = 12
        if (psalter1 in 91..100) kafizma = 13
        if (psalter1 in 101..104) kafizma = 14
        if (psalter1 in 105..108) kafizma = 15
        if (psalter1 in 109..117) kafizma = 16
        if (psalter1 == 118) kafizma = 17
        if (psalter1 in 119..133) kafizma = 18
        if (psalter1 in 134..142) kafizma = 19
        if (psalter1 in 143..151) kafizma = 20
        return kafizma
    }

    private fun setKafizma(kafizma: Int): Int {
        var glava = 1
        when (kafizma) {
            2 -> glava = 9
            3 -> glava = 17
            4 -> glava = 24
            5 -> glava = 32
            6 -> glava = 37
            7 -> glava = 46
            8 -> glava = 55
            9 -> glava = 64
            10 -> glava = 70
            11 -> glava = 77
            12 -> glava = 85
            13 -> glava = 91
            14 -> glava = 101
            15 -> glava = 105
            16 -> glava = 109
            17 -> glava = 118
            18 -> glava = 119
            19 -> glava = 134
            20 -> glava = 143
        }
        glava--
        return glava
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("dialog", dialog)
    }

    override fun onBackPressed() {
        if (BibleGlobalList.mPedakVisable) {
            val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as NadsanContentPage
            fragment.onBackPressedFragment()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_glava).isVisible = true
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        if (k.getBoolean("auto_dzen_noch", false)) menu.findItem(R.id.action_dzen_noch).isVisible = false
        val itemVybranoe: MenuItem = menu.findItem(R.id.action_vybranoe)
        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(R.string.vybranoe)
        }
        menu.findItem(R.id.action_carkva).isVisible = false
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_vybranoe) {
            men = DialogVybranoeBibleList.setVybranoe(resources.getString(R.string.psalom2), 0, binding.pager.currentItem, bibleName = 3)
            if (men) {
                MainActivity.toastView(this, getString(R.string.addVybranoe))
                if (!DialogVybranoeBibleList.checkVybranoe("3")) {
                    MenuVybranoe.vybranoe.add(0, VybranoeData(Bogashlugbovya.vybranoeIndex(), "3", getString(R.string.title_psalter)))
                    val gson = Gson()
                    val file = File("$filesDir/Vybranoe.json")
                    file.writer().use {
                        it.write(gson.toJson(MenuVybranoe.vybranoe))
                    }
                }
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val prefEditor = k.edit()
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
            return true
        }
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_glava) {
            val dialogBibleRazdel = getInstance(151)
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
        if (id == R.id.action_fullscreen) {
            hide()
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (fullscreenPage) {
            binding.linealLayoutTitle.post {
                hide()
            }
        }
        setTollbarTheme()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.biblia, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
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

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? NadsanContentPage
            fragment?.upDateListView()
        }

        override fun getItemCount() = 151

        override fun createFragment(position: Int): NadsanContentPage {
            val styx = if (glava != position) 0
            else fierstPosition
            return NadsanContentPage.newInstance(position, styx)
        }
    }
}