package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import by.carkva_gazeta.malitounik.databinding.CytatyActivityBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*


class PiarlinyAll : BaseActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private lateinit var adapterViewPager: MyPagerAdapter
    private lateinit var binding: CytatyActivityBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private val piarliny = ArrayList<PiarlinyData>()
    private var piarlinyJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private val piarlinyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        piarlinyJob = CoroutineScope(Dispatchers.Main).launch {
            val localFile = File("$filesDir/cache/cache.txt")
            Malitounik.referens.child("/admin/log.txt").getFile(localFile).addOnFailureListener {
                MainActivity.toastView(this@PiarlinyAll, getString(R.string.error))
            }.await()
            if (localFile.readText().contains("piarliny.json")) getPiarliny()
        }
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
        binding = CytatyActivityBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        binding.constraint.setOnTouchListener(this)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        fullscreenPage = savedInstanceState?.getBoolean("fullscreen") ?: k.getBoolean("fullscreenPage", false)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.tabLayout.setTabTextColors(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorSecondary_text))), Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary_black))))
        }
        binding.pager.offscreenPageLimit = 1
        piarlinyJob = CoroutineScope(Dispatchers.Main).launch {
            getPiarliny()
        }
        bindingprogress.fontSizePlus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) bindingprogress.progressTitle.text = getString(R.string.max_font)
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                bindingprogress.progressText.text = getString(R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent()
        }
        bindingprogress.fontSizeMinus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) bindingprogress.progressTitle.text = getString(R.string.min_font)
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                bindingprogress.progressText.text = getString(R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor = k.edit()
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
                bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(R.string.Bright)
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
                bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent()
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
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
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = resources.getString(R.string.piarliny)
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

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        piarlinyJob?.cancel()
        val edit = k.edit()
        edit.putInt("menuPiarlinyPage", binding.pager.currentItem)
        edit.apply()
    }

    private suspend fun getPiarliny() {
        val localFile = File("$filesDir/piarliny.json")
        if (MainActivity.isNetworkAvailable()) {
            val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
            pathReference.getFile(localFile).await()
        }
        if (localFile.exists()) {
            try {
                val piarlin = ArrayList<ArrayList<String>>()
                val builder = localFile.readText()
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                piarlin.addAll(gson.fromJson(builder, type))
                piarliny.clear()
                piarlin.forEach {
                    piarliny.add(PiarlinyData(it[0].toLong(), it[1]))
                }
                piarliny.sort()
            } catch (_: Throwable) {
            }
        }
        adapterViewPager = MyPagerAdapter(this)
        binding.pager.adapter = adapterViewPager
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            val data = GregorianCalendar()
            data.timeInMillis = piarliny[position].time * 1000L
            tab.text = data[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[data[Calendar.MONTH]]
        }.attach()
        var find = findPiarliny()
        if (find == -1) find = k.getInt("menuPiarlinyPage", 0)
        binding.pager.currentItem = find
    }

    private fun findPiarliny(): Int {
        var result = -1
        val mun = intent.extras?.getInt("mun")
        val day = intent.extras?.getInt("day")
        if (mun != null && day != null) {
            val cal = GregorianCalendar()
            piarliny.forEachIndexed { i, piarliny ->
                cal.timeInMillis = piarliny.time * 1000
                if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                    result = i
                    intent?.removeExtra("mun")
                    intent?.removeExtra("day")
                }
            }
        }
        return result
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.artykuly, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onPrepareMenu(menu: Menu) {
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
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
        if (id == R.id.action_dzen_noch) {
            if (item.isCheckable) {
                val prefEditor = k.edit()
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
        if (id == R.id.action_carkva) {
            if (checkmodulesAdmin()) {
                val intent = Intent()
                val pos = piarliny[binding.pager.currentItem]
                intent.putExtra("time", pos.time * 1000)
                intent.setClassName(this, MainActivity.ADMINPIARLINY)
                piarlinyLauncher.launch(intent)
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
                        bindingprogress.progressText.text = resources.getString(R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.progressText.text = getString(R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(R.string.font_size)
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

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val fragment = supportFragmentManager.findFragmentByTag("f" + holder.itemId) as? PiarlinyAllFragment
            fragment?.upDateTextView()
        }

        override fun getItemCount() = piarliny.size

        override fun createFragment(position: Int) = PiarlinyAllFragment.newInstance(position)
    }

    private data class PiarlinyData(var time: Long, var data: String) : Comparable<PiarlinyData> {
        override fun compareTo(other: PiarlinyData): Int {
            if (this.time > other.time) {
                return 1
            } else if (this.time < other.time) {
                return -1
            }
            return 0
        }
    }
}