package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.resources.databinding.AkafistListBibleBinding

class SlugbyVialikagaPostuSpis : AppCompatActivity() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<Data>()
    private lateinit var binding: AkafistListBibleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        if (chin.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        binding = AkafistListBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.setOnClickListener {
            binding.titleToolbar.setHorizontallyScrolling(true)
            binding.titleToolbar.freezesText = true
            binding.titleToolbar.marqueeRepeatLimit = -1
            if (binding.titleToolbar.isSelected) {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.END
                binding.titleToolbar.isSelected = false
            } else {
                binding.titleToolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.titleToolbar.isSelected = true
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = intent.extras?.getString("title")
                ?: getString(by.carkva_gazeta.malitounik.R.string.slugby_vialikaga_postu)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
        when (intent.extras?.getInt("resurs") ?: 0) {
            12 -> {
                data.add(Data(R.raw.bogashlugbovya12_1,"Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya12_1"))
                data.add(Data(R.raw.bogashlugbovya12_2,"Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya12_2"))
                data.add(Data(R.raw.bogashlugbovya12_3,"Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya12_3"))
                data.add(Data(R.raw.bogashlugbovya12_4,"Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya12_4"))
                data.add(Data(R.raw.bogashlugbovya12_5,"Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya12_5"))
                data.add(Data(R.raw.bogashlugbovya12_6,"Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya12_6"))
                data.add(Data(R.raw.bogashlugbovya12_7,"1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya12_7"))
                data.add(Data(R.raw.bogashlugbovya12_8,"1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya12_8"))
                data.add(Data(R.raw.bogashlugbovya12_9,"1-ая Нядзeля пoсту (Нядзeля праваслаўя) Лiтургiя сьвятoга Васіля Вялiкага", "bogashlugbovya12_9"))
            }
            13 -> {
                data.add(Data(R.raw.bogashlugbovya13_1,"1-ая нядзеля посту ўвечары", "bogashlugbovya13_1"))
                data.add(Data(R.raw.bogashlugbovya13_2,"Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya13_2"))
                data.add(Data(R.raw.bogashlugbovya13_3,"Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya13_3"))
                data.add(Data(R.raw.bogashlugbovya13_4,"Серада 2-га тыдня посту ўвечары", "bogashlugbovya13_4"))
                data.add(Data(R.raw.bogashlugbovya13_5,"Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya13_5"))
                data.add(Data(R.raw.bogashlugbovya13_6,"Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya13_6"))
                data.add(Data(R.raw.bogashlugbovya13_7,"2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya13_7"))
                data.add(Data(R.raw.bogashlugbovya13_8,"2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya13_8"))
            }
            14 -> {
                data.add(Data(R.raw.bogashlugbovya14_1,"2-ая нядзеля посту ўвечары", "bogashlugbovya14_1"))
                data.add(Data(R.raw.bogashlugbovya14_2,"Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya14_2"))
                data.add(Data(R.raw.bogashlugbovya14_3,"Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya14_3"))
                data.add(Data(R.raw.bogashlugbovya14_4,"Серада 3-га тыдня посту ўвечары", "bogashlugbovya14_4"))
                data.add(Data(R.raw.bogashlugbovya14_5,"Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya14_5"))
                data.add(Data(R.raw.bogashlugbovya14_6,"Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya14_6"))
                data.add(Data(R.raw.bogashlugbovya14_7,"3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya14_7"))
                data.add(Data(R.raw.bogashlugbovya14_8,"3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya14_8"))
                data.add(Data(R.raw.bogashlugbovya14_9,"3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya14_9"))
            }
            15 -> {
                data.add(Data(R.raw.bogashlugbovya15_1,"3-яя нядзеля посту ўвечары", "bogashlugbovya15_1"))
                data.add(Data(R.raw.bogashlugbovya15_2,"Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya15_2"))
                data.add(Data(R.raw.bogashlugbovya15_3,"Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya15_3"))
                data.add(Data(R.raw.bogashlugbovya15_4,"Серада 4-га тыдня посту ўвечары", "bogashlugbovya15_4"))
                data.add(Data(R.raw.bogashlugbovya15_5,"Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya15_5"))
                data.add(Data(R.raw.bogashlugbovya15_6,"Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya15_6"))
                data.add(Data(R.raw.bogashlugbovya15_7,"4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya15_7"))
                data.add(Data(R.raw.bogashlugbovya15_8,"4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya15_8"))
                data.add(Data(R.raw.bogashlugbovya15_9,"4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya15_9"))
            }
            16 -> {
                data.add(Data(R.raw.bogashlugbovya16_1,"4-ая нядзеля посту ўвечары", "bogashlugbovya16_1"))
                data.add(Data(R.raw.bogashlugbovya16_2,"Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya16_2"))
                data.add(Data(R.raw.bogashlugbovya16_3,"Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya16_3"))
                data.add(Data(R.raw.bogashlugbovya16_4,"Серада 5-га тыдня посту ўвечары", "bogashlugbovya16_4"))
                data.add(Data(R.raw.bogashlugbovya16_5,"Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya16_5"))
                data.add(Data(R.raw.bogashlugbovya16_6,"Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya16_6"))
                data.add(Data(R.raw.bogashlugbovya16_7,"Субота Акафісту Ютрань", "bogashlugbovya16_7"))
                data.add(Data(R.raw.bogashlugbovya16_8,"Літургія ў суботу Акафісту ", "bogashlugbovya16_8"))
                data.add(Data(R.raw.bogashlugbovya16_9,"5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya16_9"))
                data.add(Data(R.raw.bogashlugbovya16_10,"5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya16_10"))
                data.add(Data(R.raw.bogashlugbovya16_11,"5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya16_11"))
            }
            17 -> {
                data.add(Data(R.raw.bogashlugbovya17_1, "5-ая нядзеля посту ўвечары", "bogashlugbovya17_1"))
                data.add(Data(R.raw.bogashlugbovya17_2, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya17_2"))
                data.add(Data(R.raw.bogashlugbovya17_3, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya17_3"))
                data.add(Data(R.raw.bogashlugbovya17_4, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya17_4"))
                data.add(Data(R.raw.bogashlugbovya17_5, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya17_5"))
                data.add(Data(R.raw.bogashlugbovya17_6, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya17_6"))
                data.add(Data(R.raw.bogashlugbovya17_7, "Субота Лазара Ютрань", "bogashlugbovya17_7"))
                data.add(Data(R.raw.bogashlugbovya17_8, "Літургія", "bogashlugbovya17_8"))
            }
        }
        val adapter = ListAdaprer(this)
        binding.ListView.adapter = adapter
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, SlugbyVialikagaPostu::class.java)
            intent.putExtra("id", data[position].id)
            intent.putExtra("title", data[position].data)
            intent.putExtra("type", data[position].type)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ListAdaprer(private val context: Activity) : ArrayAdapter<Data>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, data as List<Data>) {
        private val k: SharedPreferences = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)

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
            viewHolder.text.text = data[position].data
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch)
                viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextViewRobotoCondensed)

    private data class Data(val id: Int, val data: String, val type: String)
}