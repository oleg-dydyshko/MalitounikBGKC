package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.carkva_gazeta.admin.databinding.AdminSviatyiaBinding
import by.carkva_gazeta.malitounik.CaliandarMun
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuCaliandar
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import java.util.*

class Sviatyia : AppCompatActivity(), DialogImageFileLoad.DialogFileExplorerListener, DialogSviatyiaImageHelp.DialodSviatyiaImageHelpListener {
    private lateinit var k: SharedPreferences
    private var setedit = false
    private var checkSetDzenNoch = false
    private lateinit var binding: AdminSviatyiaBinding
    private var resetTollbarJob: Job? = null
    private var caliandar = Calendar.getInstance()
    private var dayOfYear = 0
    private lateinit var adapterViewPager: MyPagerAdapter
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                val cal = GregorianCalendar(VYSOCOSNYI_GOD, arrayList[2].toInt(), arrayList[1].toInt())
                binding.pager.setCurrentItem(cal[Calendar.DAY_OF_YEAR] - 1, false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.appBarLayout2.windowToken, 0)
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
        super.onCreate(savedInstanceState)
        binding = AdminSviatyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pager.offscreenPageLimit = 3
        adapterViewPager = MyPagerAdapter(this)
        binding.pager.adapter = adapterViewPager
        binding.pager.isUserInputEnabled = false
        caliandar.set(Calendar.YEAR, VYSOCOSNYI_GOD)
        dayOfYear = intent.extras?.getInt("dayOfYear") ?: caliandar[Calendar.DAY_OF_YEAR] - 1
        binding.pager.setCurrentItem(dayOfYear, false)
        val munName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
        TabLayoutMediator(binding.tabLayout, binding.pager, false) { tab, position ->
            caliandar.set(Calendar.DAY_OF_YEAR, position + 1)
            tab.text = "${caliandar[Calendar.DAY_OF_MONTH]} ${munName[caliandar[Calendar.MONTH]]}"
        }.attach()
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dayOfYear = position
            }
        })
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.sviatyia)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
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

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onDialogFile(absolutePath: String, image: Int) {
        val sviatyiaFragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as SvityiaFragment
        sviatyiaFragment.onDialogFile(absolutePath, image)
    }

    override fun insertIMG() {
        val sviatyiaFragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as SvityiaFragment
        sviatyiaFragment.insertIMG()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag("f" + binding.pager.currentItem) as BackPressedFragment
        if (fragment.onBackPressedFragment()) super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_YEAR, dayOfYear + 1)
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("sabytie", true)
            caliandarMunLauncher.launch(i)
        }
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
        infl.inflate(R.menu.edit_sviatyia, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private inner class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 366

        override fun createFragment(position: Int) = SvityiaFragment.newInstance(position + 1)
    }

    companion object {
        private const val VYSOCOSNYI_GOD = 2020
    }
}