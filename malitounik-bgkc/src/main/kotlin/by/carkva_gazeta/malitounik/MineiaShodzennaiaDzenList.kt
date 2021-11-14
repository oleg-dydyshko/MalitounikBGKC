package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.AkafistListBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class MineiaShodzennaiaDzenList : AppCompatActivity() {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AkafistListBinding
    private var resetTollbarJob: Job? = null
    private val fileList = ArrayList<MineiaDay>()
    private lateinit var adapter: ListAdaprer

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        binding = AkafistListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTollbarTheme()
        val slugba = SlugbovyiaTextu()
        val c = GregorianCalendar()
        val day = "312"
        c.set(Calendar.DAY_OF_YEAR, day.toInt())
        fileList.add(MineiaDay(day, c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]], slugba.getTitle(slugba.getResource(day)), slugba.getResource(day), slugba.getResource(day, utran = true), slugba.getResource(day, liturgia = true)))

        binding.ListView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, MineiaShodzennaiaList::class.java)
            intent.putExtra("dayOfYear", fileList[position].day)
            intent.putExtra("titleResource", fileList[position].titleResource)
            intent.putExtra("resourceUtran", fileList[position].resourceUtran)
            intent.putExtra("resourceLiturgia", fileList[position].resourceLiturgia)
            intent.putExtra("resourceViachernia", fileList[position].resourceViachernia)
            startActivity(intent)
        }
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
            binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = ListAdaprer(this)
        binding.ListView.adapter = adapter
        binding.titleToolbar.text = getString(R.string.mineia_shtodzennaia)
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

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ListAdaprer(context: Activity) : ArrayAdapter<MineiaDay>(context, R.layout.simple_list_item_2, R.id.label, fileList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text.text = fileList[position].title
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch)
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class MineiaDay(val day: String, val title: String, val titleResource: String, val resourceViachernia: String, val resourceUtran: String, val resourceLiturgia: String)
}