package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.databinding.CytatyActivityBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.ceil


class Cytaty : BaseActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var citataFileCount: Double = 0.0
    private val dzenNoch get() = getBaseDzenNoch()
    private var pasliaPrychascia = 0
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private lateinit var binding: CytatyActivityBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private lateinit var adapterViewPager: MyPagerAdapter
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        val edit = k.edit()
        edit.putInt("menuCitatyPage", binding.pager.currentItem)
        edit.apply()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) {
            binding.constraint.post {
                hide()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding = CytatyActivityBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        pasliaPrychascia = savedInstanceState?.getInt("pasliaPrychascia") ?: (intent.extras?.getInt("paslia_prychascia") ?: 0)
        binding.constraint.setOnTouchListener(this)
        val inputStream = resources.openRawResource(R.raw.citata)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        reader.forEachLine {
            citataFileCount++
        }
        adapterViewPager = MyPagerAdapter(this)
        binding.pager.adapter = adapterViewPager
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = getString(R.string.cytaty_staronka, position + 1)
        }.attach()
        binding.pager.offscreenPageLimit = 1
        binding.pager.setCurrentItem(pasliaPrychascia, false)
        fullscreenPage = savedInstanceState?.getBoolean("fullscreen") ?: k.getBoolean("fullscreenPage", false)
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pasliaPrychascia = position
                invalidateOptionsMenu()
            }
        })
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.tabLayout.setTabTextColors(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorSecondary_text))), Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary_black))))
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            bindingprogress.seekBarBrighess.background = ContextCompat.getDrawable(this, R.drawable.selector_progress_noch)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, R.drawable.selector_progress_noch)
        }
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize(fontBiblia)
                }
                startProcent(MainActivity.PROGRESSACTIONFONT)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        bindingprogress.seekBarBrighess.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (MainActivity.brightness != progress) {
                    MainActivity.brightness = progress
                    val lp = window.attributes
                    lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                    window.attributes = lp
                    bindingprogress.progressBrighess.text = getString(R.string.procent, MainActivity.brightness)
                    MainActivity.checkBrightness = false
                }
                startProcent(MainActivity.PROGRESSACTIONBRIGHESS)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.pager.currentItem = k.getInt("menuCitatyPage", 0)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getString(R.string.cytaty_z_biblii)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
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
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.bogashlugbovya, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val prefEditor = k.edit()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            if (item.isCheckable) {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    prefEditor.putBoolean("dzen_noch", true)
                } else {
                    prefEditor.putBoolean("dzen_noch", false)
                }
                prefEditor.apply()
                recreate()
            } else {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
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
                prefEditor.putInt("fullscreenCount", fullscreenCount)
                prefEditor.apply()
            } else {
                hide()
            }
            return true
        }
        if (id == R.id.action_carkva) {
            if (checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val idres = R.raw.citata
                val inputStream = resources.openRawResource(idres)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", "citata")
                intent.putExtra("title", getString(R.string.cytaty_z_biblii))
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
            return true
        }
        return false
    }

    override fun dialogHelpFullScreenSettingsClose() {
        hide()
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_auto).isVisible = false
        menu.findItem(R.id.action_find).isVisible = false
        menu.findItem(R.id.action_vybranoe).isVisible = false
        menu.findItem(R.id.action_share).isVisible = false
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        val spanString = if (k.getBoolean("auto_dzen_noch", false)) {
            menu.findItem(R.id.action_dzen_noch).isCheckable = false
            SpannableString(getString(R.string.auto_widget_day_d_n))
        } else {
            menu.findItem(R.id.action_dzen_noch).isCheckable = true
            SpannableString(getString(R.string.widget_day_d_n))
        }
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menu.findItem(R.id.action_dzen_noch).title = spanString
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                MainActivity.brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    if (x < otstup) {
                        bindingprogress.seekBarBrighess.progress = MainActivity.brightness
                        bindingprogress.progressBrighess.text = getString(R.string.procent, MainActivity.brightness)
                        if (bindingprogress.seekBarBrighess.visibility == View.GONE) {
                            bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                            bindingprogress.seekBarBrighess.visibility = View.VISIBLE
                        }
                        startProcent(MainActivity.PROGRESSACTIONBRIGHESS)
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
                        bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
                        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
                            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
                            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
                        }
                        startProcent(MainActivity.PROGRESSACTIONFONT)
                    }
                }
            }
        }
        return true
    }

    private fun startProcent(progressAction: Int) {
        procentJob?.cancel()
        if (progressAction == MainActivity.PROGRESSACTIONBRIGHESS) bindingprogress.progressBrighess.visibility = View.VISIBLE
        if (progressAction == MainActivity.PROGRESSACTIONFONT) bindingprogress.progressFont.visibility = View.VISIBLE
        if (progressAction == MainActivity.PROGRESSACTIONAUTO) bindingprogress.progressAuto.visibility = View.VISIBLE
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            bindingprogress.progressBrighess.visibility = View.GONE
            bindingprogress.progressFont.visibility = View.GONE
            bindingprogress.progressAuto.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarBrighess.visibility == View.VISIBLE) {
                bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this@Cytaty, R.anim.slide_out_left)
                bindingprogress.seekBarBrighess.visibility = View.GONE
            }
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@Cytaty, R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
            }
        }
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
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
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putInt("pasliaPrychascia", pasliaPrychascia)
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? CytatyFragment
            fragment?.upDateTextView()
        }

        override fun getItemCount() = ceil(citataFileCount / 10).toInt()

        override fun createFragment(position: Int) = CytatyFragment.newInstance(position)
    }
}