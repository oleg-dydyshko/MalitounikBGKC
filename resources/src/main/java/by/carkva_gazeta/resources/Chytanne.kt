package by.carkva_gazeta.resources

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.DialogDzenNochSettings
import by.carkva_gazeta.malitounik.InteractiveScrollView.OnBottomReachedListener
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.VybranoeBibleList
import by.carkva_gazeta.resources.databinding.AkafistChytanneBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

class Chytanne : ZmenyiaChastki() {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var mActionDown = false
    private lateinit var binding: AkafistChytanneBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJobFont: Job? = null
    private var procentJobAuto: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var diffScroll = false
    private var titleTwo = ""
    private var firstTextPosition = 0
    private var linkMovementMethodCheck: LinkMovementMethodCheck? = null
    private var mun = 0
    private var day = 1
    private var perevod = VybranoeBibleList.PEREVODSEMUXI

    private fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.textSize = fontBiblia
    }

    private fun setFontDialog() {
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
        }
        startProcent(MainActivity.PROGRESSACTIONFONT)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = AkafistChytanneBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            binding.titleToolbar.text = savedInstanceState.getString("tollBarText", getString(by.carkva_gazeta.malitounik.R.string.czytanne3, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])) ?: getString(by.carkva_gazeta.malitounik.R.string.czytanne3, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.czytanne3, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
            if (k.getBoolean("autoscrollAutostart", false)) {
                autoStartScroll()
            }
        }
        perevod = k.getString("perevodChytanne", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI
        binding.subtitleToolbar.text = when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia2)
            VybranoeBibleList.PEREVODBOKUNA -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia_bokun2)
            VybranoeBibleList.PEREVODCARNIAUSKI -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia_charniauski2)
            else -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia2)
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        binding.constraint.setOnTouchListener(this)
        binding.InteractiveScroll.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached(checkDiff: Boolean) {
                diffScroll = checkDiff
                if (diffScroll) {
                    autoscroll = false
                    stopAutoScroll()
                }
                invalidateOptionsMenu()
            }

            override fun onTouch(action: Boolean) {
                mActionDown = action
            }
        })
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
        }
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        perevod = k.getString("perevodChytanne", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI
        checkDay()
        setChtenia(savedInstanceState)
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
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
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTOLEFT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.InteractiveScroll.setOnScrollChangedCallback(this)
        R.raw.mm_ndz_pasla_bohazjaulennia_liturhija
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
    }

    private fun checkDay() {
        val c = Calendar.getInstance()
        if (!(mun == c[Calendar.MONTH] && day == c[Calendar.DATE])) {
            binding.appBarLayout.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_strogi_post)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val otstup2 = if (autoscroll) (50 * resources.displayMetrics.density).toInt()
        else 0
        val otstup3 = when {
            fullscreenPage && autoscroll -> (160 * resources.displayMetrics.density).toInt()
            autoscroll -> (110 * resources.displayMetrics.density).toInt()
            else -> 0
        }
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    val proc: Int
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        setFontDialog()
                    }
                    if (y > heightConstraintLayout - otstup && x < widthConstraintLayout - otstup3) {
                        spid = k.getInt("autoscrollSpid", 60)
                        proc = 100 - (spid - 15) * 100 / 215
                        bindingprogress.progressAuto.text = getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                        startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                        startAutoScroll()
                        invalidateOptionsMenu()
                    }
                }
            }
        }
        return true
    }

    override fun setPerevod(perevod: String) {
        val edit = k.edit()
        edit.putString("perevodChytanne", perevod)
        edit.apply()
        if (this.perevod != perevod) {
            this.perevod = perevod
            binding.subtitleToolbar.text = when (perevod) {
                VybranoeBibleList.PEREVODSEMUXI -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia2)
                VybranoeBibleList.PEREVODBOKUNA -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia_bokun2)
                VybranoeBibleList.PEREVODCARNIAUSKI -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia_charniauski2)
                else -> getString(by.carkva_gazeta.malitounik.R.string.title_biblia2)
            }
            setChtenia(null)
        }
    }

    private fun setChtenia(savedInstanceState: Bundle?) {
        val wOld = intent.extras?.getString("cytanne") ?: ""
        val ssb = StringBuilder()
        val list = chtenia(wOld, perevod)
        for (i in list.indices) {
            ssb.append(list[i])
            if (i == 1) {
                val t1 = list[i].indexOf("<strong>")
                val t2 = list[i].indexOf("</strong>")
                titleTwo = list[i].substring(t1 + 8, t2)
            }
        }
        binding.textView.text = trimSpannable(SpannableStringBuilder(MainActivity.fromHtml(ssb.toString())))
        binding.textView.movementMethod = setLinkMovementMethodCheck()
        if (dzenNoch) binding.textView.setLinkTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
        if (k.getBoolean("utran", true) && wOld.contains("На ютрані:") && savedInstanceState == null) {
            binding.textView.post {
                binding.textView.layout?.let { layout ->
                    val strPosition = binding.textView.text.indexOf(titleTwo, ignoreCase = true)
                    val line = layout.getLineForOffset(strPosition)
                    val y = layout.getLineTop(line)
                    val anim = ObjectAnimator.ofInt(binding.InteractiveScroll, "scrollY", binding.InteractiveScroll.scrollY, y)
                    anim.setDuration(1000).start()
                }
            }
        }
        if (savedInstanceState != null) {
            binding.textView.post {
                val textline = savedInstanceState.getInt("textLine", 0)
                binding.textView.layout?.let { layout ->
                    val line = layout.getLineForOffset(textline)
                    val y = layout.getLineTop(line)
                    binding.InteractiveScroll.scrollY = y
                }
            }
        }
    }

    private fun trimSpannable(spannable: SpannableStringBuilder): SpannableStringBuilder {
        var trimStart = 0
        var trimEnd = 0
        var text = spannable.toString()
        while (text.isNotEmpty() && text.startsWith("\n")) {
            text = text.substring(1)
            trimStart += 1
        }
        while (text.isNotEmpty() && text.endsWith("\n")) {
            text = text.substring(0, text.length - 1)
            trimEnd += 1
        }
        return spannable.delete(0, trimStart).delete(spannable.length - trimEnd, spannable.length)
    }

    override fun linkMovementMethodCheckOnTouch(onTouch: Boolean) {
        mActionDown = onTouch
    }

    private fun setLinkMovementMethodCheck(): LinkMovementMethodCheck? {
        linkMovementMethodCheck = LinkMovementMethodCheck()
        linkMovementMethodCheck?.setLinkMovementMethodCheckListener(this)
        return linkMovementMethodCheck
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            if (spid < 166) {
                val autoTime = (230 - spid) / 10
                var count = 0
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
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
                        startAutoScroll()
                    }
                }
            } else {
                startAutoScroll()
            }
        }
    }

    private fun startProcent(progressAction: Int) {
        if (progressAction == MainActivity.PROGRESSACTIONFONT) {
            procentJobFont?.cancel()
            bindingprogress.progressFont.visibility = View.VISIBLE
            procentJobFont = CoroutineScope(Dispatchers.Main).launch {
                MainActivity.dialogVisable = true
                delay(2000)
                bindingprogress.progressFont.visibility = View.GONE
                delay(3000)
                if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                    bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@Chytanne, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
                    bindingprogress.seekBarFontSize.visibility = View.GONE
                    MainActivity.dialogVisable = false
                }
            }
        }
        if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT || progressAction == MainActivity.PROGRESSACTIONAUTORIGHT) {
            procentJobAuto?.cancel()
            bindingprogress.progressAuto.visibility = View.VISIBLE
            if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT) {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@Chytanne, by.carkva_gazeta.malitounik.R.drawable.selector_progress_auto_left)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@Chytanne, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            } else {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@Chytanne, by.carkva_gazeta.malitounik.R.drawable.selector_progress_red)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@Chytanne, by.carkva_gazeta.malitounik.R.color.colorWhite))
            }
            procentJobAuto = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressAuto.visibility = View.GONE
            }
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        autoStartScrollJob?.cancel()
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor: Editor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
            }
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            autoScrollJob?.cancel()
            binding.textView.setTextIsSelectable(true)
            binding.textView.movementMethod = setLinkMovementMethodCheck()
            if (delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (!diffScroll) {
            spid = k.getInt("autoscrollSpid", 60)
            if (binding.actionMinus.visibility == View.GONE) {
                binding.actionMinus.visibility = View.VISIBLE
                binding.actionPlus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                binding.actionMinus.animation = animation
                binding.actionPlus.animation = animation
            }
            resetScreenJob?.cancel()
            autoStartScrollJob?.cancel()
            autoScroll()
        } else {
            val duration: Long = 1000
            ObjectAnimator.ofInt(binding.InteractiveScroll, "scrollY", 0).setDuration(duration).start()
            binding.InteractiveScroll.postDelayed({
                autoStartScroll()
                invalidateOptionsMenu()
            }, duration)
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            binding.textView.clearFocus()
            binding.textView.setTextIsSelectable(false)
            binding.textView.movementMethod = setLinkMovementMethodCheck()
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
                        binding.InteractiveScroll.smoothScrollBy(0, 2)
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.chtenia, menu)
        super.onCreateMenu(menu, menuInflater)
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

    override fun onPrepareMenu(menu: Menu) {
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        when {
            autoscroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
            diffScroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_up)
            else -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_utran).isChecked = k.getBoolean("utran", true)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_utran).isVisible = true
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_perevod).isVisible = true
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        val prefEditors = k.edit()
        prefEditors.putBoolean("fullscreenPage", fullscreenPage)
        prefEditors.apply()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) {
            binding.constraint.post {
                hideHelp()
            }
        }
        spid = k.getInt("autoscrollSpid", 60)
        autoscroll = k.getBoolean("autoscroll", false)
        if (autoscroll) {
            autoStartScroll()
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditor = k.edit()
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_perevod) {
            val dialog = DialogPerevodBiblii.getInstance(isSinoidal = false, isNadsan = false, perevod = k.getString("perevodChytanne", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI)
            dialog.show(supportFragmentManager, "DialogPerevodBiblii")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_utran) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("utran", true)
            } else {
                prefEditor.putBoolean("utran", false)
            }
            prefEditor.apply()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            prefEditor.putBoolean("autoscrollAutostart", !autoscroll)
            prefEditor.apply()
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            setFontDialog()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            hideHelp()
            return true
        }
        return false
    }

    override fun onDialogFullScreenHelpClose() {
        if (dzenNoch) binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
        else binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
        hide()
    }

    private fun hideHelp() {
        if (k.getBoolean("help_fullscreen", true)) {
            binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPost2))
            if (dzenNoch) binding.InteractiveScroll.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
            else binding.InteractiveScroll.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
            val dialogHelpListView = DialogHelpFullScreen()
            dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
            val prefEditors = k.edit()
            prefEditors.putBoolean("help_fullscreen", false)
            prefEditors.apply()
        } else {
            hide()
        }
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        val layoutParams = binding.InteractiveScroll.layoutParams as ViewGroup.MarginLayoutParams
        val px = (resources.displayMetrics.density * 10).toInt()
        layoutParams.setMargins(0, 0, px, px)
        binding.InteractiveScroll.setPadding(binding.InteractiveScroll.paddingLeft, binding.InteractiveScroll.paddingTop, 0, 0)
        binding.InteractiveScroll.layoutParams = layoutParams
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        if (binding.actionMinus.visibility == View.GONE) {
            binding.actionBack.visibility = View.VISIBLE
            binding.actionBack.animation = animation
        }
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        val layoutParams = binding.InteractiveScroll.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0)
        val px = (resources.displayMetrics.density * 10).toInt()
        binding.InteractiveScroll.setPadding(binding.InteractiveScroll.paddingLeft, binding.InteractiveScroll.paddingTop, px, 0)
        binding.InteractiveScroll.layoutParams = layoutParams
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onScroll(t: Int, oldt: Int) {
        binding.textView.layout?.let { layout ->
            firstTextPosition = layout.getLineStart(layout.getLineForVertical(t))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putInt("textLine", firstTextPosition)
    }
}