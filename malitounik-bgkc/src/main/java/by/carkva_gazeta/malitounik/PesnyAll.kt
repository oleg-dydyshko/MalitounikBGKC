package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.PesnyBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class PesnyAll : BaseActivity(), OnTouchListener, DialogFontSize.DialogFontSizeListener, DialogHelpShare.DialogHelpShareListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var checkVybranoe = false
    private var n = 0
    private var title = ""
    private var men = false
    private var resurs = ""
    private lateinit var binding: PesnyBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private var procentJobFont: Job? = null
    private var resetTollbarJob: Job? = null

    companion object {
        val resursMap = ArrayMap<String, Int>()

        init {
            resursMap()
        }

        private fun resursMap() {
            val fields = R.raw::class.java.fields
            for (element in fields) {
                val name = element.name
                resursMap[name] = element.getInt(name)
            }
        }

        private fun setVybranoe(context: Context, resurs: String, title: String): Boolean {
            var check = true
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                val gson = Gson()
                if (file.exists() && MenuVybranoe.vybranoe.isEmpty()) {
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) {
                        MenuVybranoe.vybranoe.removeAt(i)
                        check = false
                        break
                    }
                }
                if (check) {
                    MenuVybranoe.vybranoe.add(0, VybranoeData(vybranoeIndex(), resurs, title))
                }
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                file.writer().use {
                    it.write(gson.toJson(MenuVybranoe.vybranoe, type))
                }
            } catch (t: Throwable) {
                file.delete()
                check = false
            }
            return check
        }

        fun vybranoeIndex(): Long {
            var result: Long = 1
            val vybranoe = MenuVybranoe.vybranoe
            if (vybranoe.size != 0) {
                vybranoe.forEach {
                    if (result < it.id) result = it.id
                }
                result++
            }
            return result
        }

        private fun checkVybranoe(context: Context, resurs: String): Boolean {
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            if (!file.exists()) return false
            try {
                val gson = Gson()
                if (MenuVybranoe.vybranoe.isEmpty()) {
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) return true
                }
            } catch (t: Throwable) {
                file.delete()
                return false
            }
            return false
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        val prefEditors = k.edit()
        prefEditors.putBoolean("fullscreenPage", fullscreenPage)
        prefEditors.apply()
    }

    override fun onResume() {
        super.onResume()
        if (fullscreenPage) {
            binding.constraint.post {
                hide()
            }
        }
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (k.getBoolean("scrinOn", true)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = PesnyBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        fullscreenPage = savedInstanceState?.getBoolean("fullscreen") ?: k.getBoolean("fullscreenPage", false)
        if (fullscreenPage) binding.constraint.setOnTouchListener(this)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, R.drawable.selector_progress_noch)
        }
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        title = intent.extras?.getString("pesny", "") ?: ""
        resurs = intent.extras?.getString("type", "pesny_prasl_0") ?: "pesny_prasl_0"
        val pesny = resursMap[resurs] ?: R.raw.bogashlugbovya_error
        val builder = StringBuilder()
        if (pesny != -1) {
            val inputStream = resources.openRawResource(pesny)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.use { bufferedReader ->
                bufferedReader.forEachLine {
                    line = it
                    if (dzenNoch) line = line.replace("#d00505", "#ff6666")
                    builder.append(line)
                }
            }
        } else {
            builder.append(getString(R.string.error_ch))
        }
        binding.textView.movementMethod = LinkMovementMethod()
        binding.textView.text = MainActivity.fromHtml(builder.toString())
        men = checkVybranoe(this, resurs)
        checkVybranoe = men
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
                startProcent()
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
        setTollbarTheme()
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
        binding.titleToolbar.text = title
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

    private fun startProcent() {
        procentJobFont?.cancel()
        bindingprogress.progressFont.visibility = View.VISIBLE
        procentJobFont = CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            bindingprogress.progressFont.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@PesnyAll, R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
                        bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
                        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
                            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
                            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
                        }
                        startProcent()
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_auto).isVisible = false
        menu.findItem(R.id.action_find).isVisible = false
        if (men) {
            menu.findItem(R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, R.drawable.star_big_on)
            menu.findItem(R.id.action_vybranoe).title = resources.getString(R.string.vybranoe_del)
        } else {
            menu.findItem(R.id.action_vybranoe).icon = ContextCompat.getDrawable(this, R.drawable.star_big_off)
            menu.findItem(R.id.action_vybranoe).title = resources.getString(R.string.vybranoe)
        }
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        val item = menu.findItem(R.id.action_vybranoe)
        val spanString = SpannableString(menu.findItem(R.id.action_vybranoe).title.toString())
        val end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        item.title = spanString
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        menu.findItem(R.id.action_screen_on).isChecked = k.getBoolean("scrinOn", true)
        menu.findItem(R.id.action_screen_on).isVisible = true
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
        if (id == R.id.action_screen_on) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            prefEditor.putBoolean("scrinOn", item.isChecked)
            prefEditor.apply()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
            return true
        }
        if (id == R.id.action_vybranoe) {
            men = setVybranoe(this, resurs, title)
            if (men) {
                MainActivity.toastView(this, getString(R.string.addVybranoe))
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        @SuppressLint("ClickableViewAccessibility") if (id == R.id.action_fullscreen) {
            if (!k.getBoolean("fullscreenPage", false)) {
                binding.constraint.setOnTouchListener(this)
            } else {
                binding.constraint.setOnTouchListener(null)
            }
            hide()
            return true
        }
        if (id == R.id.action_share) {
            val pesny = resursMap[resurs] ?: R.raw.bogashlugbovya_error
            if (pesny != R.raw.bogashlugbovya_error) {
                val inputStream = resources.openRawResource(pesny)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var text: String
                reader.use { bufferedReader ->
                    text = bufferedReader.readText()
                }
                val sent = MainActivity.fromHtml(text).toString()
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.copy_text), sent)
                clipboard.setPrimaryClip(clip)
                MainActivity.toastView(this, getString(R.string.copy_text), Toast.LENGTH_LONG)
                if (k.getBoolean("dialogHelpShare", true)) {
                    val dialog = DialogHelpShare.getInstance(sent)
                    dialog.show(supportFragmentManager, "DialogHelpShare")
                } else {
                    sentShareText(sent)
                }
            } else {
                MainActivity.toastView(this, getString(R.string.error_ch))
            }
            return true
        }
        prefEditor.apply()
        if (id == R.id.action_carkva) {
            if (checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val inputStream = resources.openRawResource(resursMap[resurs] ?: R.raw.bogashlugbovya_error)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", resurs)
                intent.putExtra("title", title)
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
            return true
        }
        return false
    }

    override fun sentShareText(shareText: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, title))
    }

    override fun onBack() {
        if (intent.extras?.getBoolean("chekVybranoe", false) == true && men != checkVybranoe) {
            setResult(200)
        }
        super.onBack()
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
    }
}