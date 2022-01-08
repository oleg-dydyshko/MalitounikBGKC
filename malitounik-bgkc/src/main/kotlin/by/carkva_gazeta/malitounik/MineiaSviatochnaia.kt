package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class MineiaSviatochnaia : AppCompatActivity() {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AkafistListBinding
    private var resetTollbarJob: Job? = null
    private lateinit var adapter: MenuListAdaprer
    private val groups = ArrayList<MineiaDay>()
    private var mLastClickTime: Long = 0

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
        val mineiaList = slugba.getMineiaSviatochnaia()
        mineiaList.sort()
        val c = Calendar.getInstance()
        var day = 0
        for (i in 0 until mineiaList.size) {
            if (day == mineiaList[i].day) {
                day = mineiaList[i].day
                continue
            } else {
                day = mineiaList[i].day
            }
            if (mineiaList[i].pasxa) {
                MenuCaliandar.getDataCalaindar(year = c[Calendar.YEAR]).forEach {
                    if (it[22].toInt() == day) {
                        c.set(it[3].toInt(), it[2].toInt(), it[1].toInt())
                        day = it[22].toInt()
                        return@forEach
                    }
                }
            } else {
                MenuCaliandar.getDataCalaindar(year = c[Calendar.YEAR]).forEach {
                    if (it[24].toInt() == day) {
                        c.set(it[3].toInt(), it[2].toInt(), it[1].toInt())
                        return@forEach
                    }
                }
            }
            val id = c.timeInMillis
            val count = ArrayList<String>()
            val abednica = slugba.getResource(day, mineiaList[i].pasxa, abednica = true, isSviaty = true)
            val vialikiaGadziny = slugba.getResource(day, mineiaList[i].pasxa, vialikiaGadziny = true, isSviaty = true)
            val utran = slugba.getResource(day, mineiaList[i].pasxa, utran = true, isSviaty = true)
            val liturgia = slugba.getResource(day, mineiaList[i].pasxa, liturgia = true, isSviaty = true)
            val viachernia = slugba.getResource(day, mineiaList[i].pasxa, isSviaty = true)
            if (abednica != "0") count.add("1")
            if (vialikiaGadziny != "0") count.add("1")
            if (utran != "0") count.add("1")
            if (liturgia != "0") count.add("1")
            if (viachernia != "0") count.add("1")
            val slujba = when {
                count.size > 1 -> ""
                mineiaList[i].vialikiaGadziny -> " - Вялікія гадзіны"
                mineiaList[i].abednica -> " - Абедніца"
                mineiaList[i].utran -> " - Ютрань"
                mineiaList[i].liturgia -> " - Літургія"
                else -> " - Вячэрня"
            }
            groups.add(MineiaDay(id, c[Calendar.MONTH], day.toString(), c[Calendar.DATE].toString() + " " + resources.getStringArray(R.array.meciac_smoll)[c[Calendar.MONTH]] + ": " + mineiaList[i].title + slujba, mineiaList[i].title, viachernia, utran, liturgia, vialikiaGadziny, abednica))
            day = mineiaList[i].day
        }
        binding.ListView.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val resourceUtran = groups[position].resourceUtran
            val resourceLiturgia = groups[position].resourceLiturgia
            val resourceViachernia = groups[position].resourceViachernia
            val resourceAbednica = groups[position].resourceAbednica
            val resourceVialikiaGadziny = groups[position].resourceVialikiaGadziny
            var count = 0
            if (resourceAbednica != "0") count++
            if (resourceVialikiaGadziny != "0") count++
            if (resourceUtran != "0") count++
            if (resourceLiturgia != "0") count++
            if (resourceViachernia != "0") count++
            if (count > 1) {
                val dialog = DialogMineiaList.getInstance(groups[position].day, groups[position].titleResource, resourceUtran, resourceLiturgia, resourceViachernia, resourceAbednica, resourceVialikiaGadziny, true)
                dialog.show(supportFragmentManager, "dialogMineiaList")
            } else {
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(this, MainActivity.BOGASHLUGBOVYA)
                    if (resourceAbednica != "0") intent.putExtra("resurs", resourceAbednica)
                    if (resourceVialikiaGadziny != "0") intent.putExtra("resurs", resourceVialikiaGadziny)
                    if (resourceUtran != "0") intent.putExtra("resurs", resourceUtran)
                    if (resourceLiturgia != "0") intent.putExtra("resurs", resourceLiturgia)
                    if (resourceViachernia != "0") intent.putExtra("resurs", resourceViachernia)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", groups[position].titleResource)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(supportFragmentManager, "dadatak")
                }
            }
        }
        groups.sort()
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
        adapter = MenuListAdaprer(this, groups)
        binding.ListView.adapter = adapter
        binding.titleToolbar.text = getString(R.string.mineia_sviatochnaia)
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

    private class MenuListAdaprer(context: Activity, val strings: ArrayList<MineiaDay>) : ArrayAdapter<MineiaDay>(context, R.layout.simple_list_item_2, R.id.label, strings) {

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
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text.text = strings[position].title
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }

        private class ViewHolder(var text: TextView)
    }

    private data class MineiaDay(val id: Long, val month: Int, val day: String, val title: String, val titleResource: String, var resourceViachernia: String, var resourceUtran: String, var resourceLiturgia: String, var resourceVialikiaGadziny: String, var resourceAbednica: String) : Comparable<MineiaDay> {
        override fun compareTo(other: MineiaDay): Int {
            if (this.id > other.id) {
                return 1
            } else if (this.id < other.id) {
                return -1
            }
            return 0
        }
    }
}