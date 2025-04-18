package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar


class PiarlinyAll : BaseActivity(), View.OnTouchListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private lateinit var adapterViewPager: MyPagerAdapter
    private lateinit var binding: CytatyActivityBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private val piarliny = ArrayList<PiarlinyData>()
    private var piarlinyJob: Job? = null
    private var procentJobFont: Job? = null
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
    private fun onDialogFontSize() {
        binding.pager.adapter?.notifyDataSetChanged()
    }

    private fun setFontDialog() {
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
        }
        startProcent()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CytatyActivityBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        fullscreenPage = savedInstanceState?.getBoolean("fullscreen") ?: k.getBoolean("fullscreenPage", false)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        if (fullscreenPage) binding.constraint.setOnTouchListener(this)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.tabLayout.setTabTextColors(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorSecondary_text))), Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary_black))))
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, R.drawable.selector_progress_noch)
        }
        binding.pager.offscreenPageLimit = 1
        piarlinyJob = CoroutineScope(Dispatchers.Main).launch {
            getPiarliny()
        }
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize()
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
        edit.putBoolean("fullscreenPage", fullscreenPage)
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
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_font) {
            setFontDialog()
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
            return true
        }
        @SuppressLint("ClickableViewAccessibility")
        if (id == R.id.action_fullscreen) {
            if (!k.getBoolean("fullscreenPage", false)) {
                binding.constraint.setOnTouchListener(this)
            } else {
                binding.constraint.setOnTouchListener(null)
            }
            hide()
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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    if (x > widthConstraintLayout - otstup) {
                        setFontDialog()
                    }
                }
            }
        }
        return true
    }

    private fun startProcent() {
        procentJobFont?.cancel()
        bindingprogress.progressFont.visibility = View.VISIBLE
        procentJobFont = CoroutineScope(Dispatchers.Main).launch {
            MainActivity.dialogVisable = true
            delay(2000)
            bindingprogress.progressFont.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@PiarlinyAll, R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
                MainActivity.dialogVisable = false
            }
        }
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
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