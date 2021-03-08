package by.carkva_gazeta.admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import by.carkva_gazeta.admin.databinding.AdminSviatyiaBinding
import by.carkva_gazeta.malitounik.*
import kotlinx.coroutines.*
import java.util.*

class Sviatyia : AppCompatActivity() {
    private lateinit var k: SharedPreferences
    private var setedit = false
    private var checkSetDzenNoch = false
    private val orientation: Int
        get() = MainActivity.getOrientation(this)
    private lateinit var binding: AdminSviatyiaBinding
    private var resetTollbarJob: Job? = null
    private var caliandar = Calendar.getInstance()
    private var dayOfYear = 0
    private lateinit var adapterViewPager: MyPagerAdapter
    private val munName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
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
        binding.pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        for (i in 0 until binding.pagerTabStrip.childCount) {
            val nextChild = binding.pagerTabStrip.getChildAt(i)
            if (nextChild is TextView) {
                nextChild.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            }
        }
        adapterViewPager = MyPagerAdapter(supportFragmentManager)
        binding.pager.adapter = adapterViewPager
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                dayOfYear = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        requestedOrientation = orientation
        //men = VybranoeBibleList.checkVybranoe(this, kniga, glava)
        /*if (savedInstanceState != null) {
            dialog = savedInstanceState.getBoolean("dialog")
            paralel = savedInstanceState.getBoolean("paralel")
            cytanneSours = savedInstanceState.getString("cytanneSours") ?: ""
            cytanneParalelnye = savedInstanceState.getString("cytanneParalelnye") ?: ""
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
        }*/
        caliandar.set(Calendar.YEAR, 2020)
        dayOfYear = intent.extras?.getInt("dayOfYear", caliandar[Calendar.DAY_OF_YEAR] - 1) ?: caliandar[Calendar.DAY_OF_YEAR] - 1
        binding.pager.currentItem = dayOfYear
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

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }*/

    override fun onBackPressed() {
        val fragment = adapterViewPager.getFragment(binding.pager.currentItem) as BackPressedFragment
        if (fragment.onBackPressedFragment())
            super.onBackPressed()
    }

    /*override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_YEAR, dayOfYear + 1)
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("sabytie", true)
            startActivityForResult(i, 1093)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1093) {
            if (data != null) {
                val day = data.getIntExtra("data", 0)
                val cal = Calendar.getInstance() as GregorianCalendar
                if (cal.isLeapYear(cal[Calendar.YEAR])) {
                    binding.pager.currentItem = day
                } else {
                    if (day <= 58)
                        binding.pager.currentItem = day
                    else
                        binding.pager.currentItem = day + 1
                }
            }
        }
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

    private inner class MyPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {

        override fun getCount(): Int = 366

        override fun getItem(position: Int): Fragment = SvityiaFragment.newInstance(position + 1)

        override fun getPageTitle(position: Int): CharSequence {
            caliandar.set(Calendar.DAY_OF_YEAR, position + 1)
            return "${caliandar[Calendar.DAY_OF_MONTH]} ${munName[caliandar[Calendar.MONTH]]}"
        }

        override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE
    }
}