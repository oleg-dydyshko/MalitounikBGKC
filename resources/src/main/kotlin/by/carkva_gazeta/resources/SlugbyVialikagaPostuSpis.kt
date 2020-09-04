package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import kotlinx.android.synthetic.main.akafist_list_bible.*

/**
 * Created by oleg on 30.5.16
 */
class SlugbyVialikagaPostuSpis : AppCompatActivity() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<Data>()
    //private val data = arrayOf("ПАНЯДЗЕЛАК\nСлужба сьвятым анёлам", "АЎТОРАК\nСлужба сьвятому Яну Хрысьціцелю", "СЕРАДА\nСлужба Найсьвяцейшай Багародзіцы і Крыжу", "ЧАЦЬВЕР\nСлужба апосталам і сьвятому Мікалаю", "ПЯТНІЦА\nСлужба Крыжу Гасподняму", "СУБОТА\nСлужба ўсім сьвятым і памёрлым")

    override fun onCreate(savedInstanceState: Bundle?) {
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.akafist_list_bible)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setOnClickListener {
            title_toolbar.setHorizontallyScrolling(true)
            title_toolbar.freezesText = true
            title_toolbar.marqueeRepeatLimit = -1
            if (title_toolbar.isSelected) {
                title_toolbar.ellipsize = TextUtils.TruncateAt.END
                title_toolbar.isSelected = false
            } else {
                title_toolbar.ellipsize = TextUtils.TruncateAt.MARQUEE
                title_toolbar.isSelected = true
            }
        }
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        title_toolbar.text = intent.extras?.getString("title")
                ?: getString(by.carkva_gazeta.malitounik.R.string.slugby_vialikaga_postu)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            title_toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
        when (intent.extras?.getInt("resurs") ?: 0) {
            12 -> {
                data.add(Data(R.raw.bogashlugbovya12_1,"Вячэрня ў нядзелю сырную вeчарам"))
                data.add(Data(R.raw.bogashlugbovya12_2,"Панядзeлак 1-га тыдня посту ўвeчары"))
                data.add(Data(R.raw.bogashlugbovya12_3,"Аўтoрак 1-га тыдня посту ўвeчары"))
                data.add(Data(R.raw.bogashlugbovya12_4,"Сeрада 1-га тыдня посту ўвeчары"))
                data.add(Data(R.raw.bogashlugbovya12_5,"Чацьвeр 1-га тыдня посту ўвeчары"))
                data.add(Data(R.raw.bogashlugbovya12_6,"Пятнiца 1-га тыдня пoсту ўвeчары"))
                data.add(Data(R.raw.bogashlugbovya12_7,"1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня"))
                data.add(Data(R.raw.bogashlugbovya12_8,"1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань"))
                data.add(Data(R.raw.bogashlugbovya12_9,"1-ая Нядзeля пoсту (Нядзeля праваслаўя) Лiтургiя сьвятoга Васіля Вялiкага"))
            }
            13 -> {
                data.add(Data(R.raw.bogashlugbovya13_1,"1-ая нядзеля посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya13_2,"Панядзелак 2-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya13_3,"Аўторак 2-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya13_4,"Серада 2-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya13_5,"Чацьвер 2-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya13_6,"Пятніца 2-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya13_7,"2-ая нядзеля Вялікага посту Вячэрня Ютрань"))
                data.add(Data(R.raw.bogashlugbovya13_8,"2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага"))
            }
            14 -> {
                data.add(Data(R.raw.bogashlugbovya14_1,"2-ая нядзеля посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya14_2,"Панядзелак 3-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya14_3,"Аўторак 3-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya14_4,"Серада 3-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya14_5,"Чацьвер 3-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya14_6,"Пятніца 3-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya14_7,"3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня"))
                data.add(Data(R.raw.bogashlugbovya14_8,"3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань"))
                data.add(Data(R.raw.bogashlugbovya14_9,"3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага"))
            }
            15 -> {
                data.add(Data(R.raw.bogashlugbovya15_1,"3-яя нядзеля посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya15_2,"Панядзелак 4-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya15_3,"Аўторак 4-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya15_4,"Серада 4-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya15_5,"Чацьвер 4-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya15_6,"Пятніца 4-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya15_7,"4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня"))
                data.add(Data(R.raw.bogashlugbovya15_8,"4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань"))
                data.add(Data(R.raw.bogashlugbovya15_9,"4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага"))
            }
            16 -> {
                data.add(Data(R.raw.bogashlugbovya16_1,"4-ая нядзеля посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya16_2,"Панядзелак 5-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya16_3,"Аўторак 5-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya16_4,"Серада 5-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya16_5,"Чацьвер 5-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya16_6,"Пятніца 5-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya16_7,"Субота Акафісту Ютрань"))
                data.add(Data(R.raw.bogashlugbovya16_8,"Літургія ў суботу Акафісту "))
                data.add(Data(R.raw.bogashlugbovya16_9,"5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня"))
                data.add(Data(R.raw.bogashlugbovya16_10,"5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань"))
                data.add(Data(R.raw.bogashlugbovya16_11,"5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага"))
            }
            17 -> {
                data.add(Data(R.raw.bogashlugbovya17_1,"5-ая нядзеля посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya17_2,"Панядзелак 6-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya17_3,"Аўторак 6-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya17_4,"Серада 6-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya17_5,"Чацьвер 6-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya17_6,"Пятніца 6-га тыдня посту ўвечары"))
                data.add(Data(R.raw.bogashlugbovya17_7,"Субота Лазара Ютрань"))
                data.add(Data(R.raw.bogashlugbovya17_8,"Літургія"))
            }
        }
        val adapter = ListAdaprer(this)
        ListView.adapter = adapter
        ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, SlugbyVialikagaPostu::class.java)
            intent.putExtra("id", data[position].id)
            intent.putExtra("title", data[position].data)
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
                rootView = context.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text?.text = data[position].data
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            } else {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_white)
            }
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    data class Data(val id: Int, val data: String)
}