package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.malitounik.DialogBrightness
import by.carkva_gazeta.malitounik.DialogFontSize
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.AkafistActivityPasliaPrichBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*


class PasliaPrychascia : AppCompatActivity(), View.OnTouchListener, DialogFontSizeListener {

    private var fullscreenPage = false
    private var checkSetDzenNoch = false
    private lateinit var k: SharedPreferences
    private var men = false
    private val malitvy = ArrayList<Malitvy>()
    private var dzenNoch = false
    private var pasliaPrychascia = 0
    private var n = 0
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private lateinit var binding: AkafistActivityPasliaPrichBinding
    private lateinit var bindingprogress: ProgressBinding
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        malitvy.forEachIndexed { index, _ ->
            val tabLayout = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(index) as LinearLayout
            val tabTextView = tabLayout.getChildAt(1) as TextView
            tabTextView.typeface = MainActivity.createFont(Typeface.BOLD)
            tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT - 2)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogFontSize(fontSize: Float) {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        checkSetDzenNoch = dzenNoch
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = AkafistActivityPasliaPrichBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        malitvy.add(Malitvy(R.raw.paslia_prychascia1, "paslia_prychascia1", "Малітва падзякі"))
        malitvy.add(Malitvy(R.raw.paslia_prychascia2, "paslia_prychascia2", "Малітва сьв. Васіля Вялікага"))
        malitvy.add(Malitvy(R.raw.paslia_prychascia3, "paslia_prychascia3", "Малітва Сымона Мэтафраста"))
        malitvy.add(Malitvy(R.raw.paslia_prychascia4, "paslia_prychascia4", "Iншая малітва"))
        malitvy.add(Malitvy(R.raw.paslia_prychascia5, "paslia_prychascia5", "Малітва да Найсьвяцейшай Багародзіцы"))
        pasliaPrychascia = savedInstanceState?.getInt("pasliaPrychascia") ?: (intent.extras?.getInt("paslia_prychascia") ?: 0)
        men = Bogashlugbovya.checkVybranoe(this, malitvy[pasliaPrychascia].resourse)
        binding.constraint.setOnTouchListener(this)
        val adapterViewPager = MyPagerAdapter(this)
        binding.pager.adapter = adapterViewPager
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            tab.text = malitvy[position].title
        }.attach()
        if (dzenNoch) binding.tabLayout.setTabTextColors(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorSecondary_text))), Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))))
        binding.pager.offscreenPageLimit = 3
        binding.pager.setCurrentItem(pasliaPrychascia, false)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            checkSetDzenNoch = savedInstanceState.getBoolean("checkSetDzenNoch")
        }
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                men = Bogashlugbovya.checkVybranoe(this@PasliaPrychascia, malitvy[position].resourse)
                pasliaPrychascia = position
                invalidateOptionsMenu()
            }
        })
        if (dzenNoch) {
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
        }
        bindingprogress.fontSizePlus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.max_font)
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent()
        }
        bindingprogress.fontSizeMinus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.min_font)
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent()
        }
        bindingprogress.brighessPlus.setOnClickListener {
            if (MainActivity.brightness < 100) {
                MainActivity.brightness = MainActivity.brightness + 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent()
        }
        bindingprogress.brighessMinus.setOnClickListener {
            if (MainActivity.brightness > 0) {
                MainActivity.brightness = MainActivity.brightness - 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent()
        }
        binding.actionFullscreen.setOnClickListener {
            fullscreenPage = false
            show()
        }
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
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.pasliaPrychscia)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.akafist, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor: Editor = k.edit()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
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
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            men = Bogashlugbovya.setVybranoe(this, malitvy[pasliaPrychascia].resourse, malitvy[pasliaPrychascia].title)
            if (men) {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.addVybranoe))
            }
            invalidateOptionsMenu()
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
            fullscreenPage = true
            hide()
        }
        prefEditor.apply()
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val inputStream = resources.openRawResource(malitvy[pasliaPrychascia].resourseID)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", malitvy[pasliaPrychascia].resourse)
                intent.putExtra("title", malitvy[pasliaPrychascia].title)
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto).isVisible = false
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_find).isVisible = false
        if (men) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)
        val item = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        val spanString = SpannableString(menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe).title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        item.title = spanString
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        return true
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
                    n = event?.y?.toInt() ?: 0
                    if (x < otstup) {
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        startProcent()
                    }
                }
            }
        }
        return true
    }

    private fun startProcent() {
        procentJob?.cancel()
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            bindingprogress.progress.visibility = View.GONE
            bindingprogress.brighess.visibility = View.GONE
            bindingprogress.fontSize.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (fullscreenPage) {
            fullscreenPage = false
            show()
        } else {
            if (checkSetDzenNoch != dzenNoch) {
                onSupportNavigateUp()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (fullscreenPage && hasFocus) hide()
    }

    private fun hide() {
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = ViewCompat.getWindowInsetsController(binding.constraint)
        controller?.let {
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsetsCompat.Type.systemBars())
        }
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
    }

    private fun show() {
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = ViewCompat.getWindowInsetsController(binding.constraint)
        controller?.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("checkSetDzenNoch", checkSetDzenNoch)
        outState.putInt("pasliaPrychascia", pasliaPrychascia)
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? PasliaPrychasciaFragment
            fragment?.upDateTextView() ?: super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount() = malitvy.size

        override fun createFragment(position: Int) = PasliaPrychasciaFragment.newInstance(malitvy[position].resourseID)
    }

    private data class Malitvy(val resourseID: Int, val resourse: String, val title: String)
}