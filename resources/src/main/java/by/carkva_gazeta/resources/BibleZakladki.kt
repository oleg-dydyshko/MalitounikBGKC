package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import by.carkva_gazeta.resources.DialogDeliteAllZakladkiINatatki.DialogDeliteAllZakladkiINatatkiListener
import by.carkva_gazeta.resources.DialogZakladkaDelite.ZakladkaDeliteListiner
import com.google.gson.Gson
import kotlinx.android.synthetic.main.akafist_list_bible.*
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

class BibleZakladki : AppCompatActivity(), OnItemClickListener, OnItemLongClickListener, ZakladkaDeliteListiner, DialogDeliteAllZakladkiINatatkiListener {
    private lateinit var adapter: BibleZakladkiListAdaprer
    private var semuxa = 1
    private var dzenNoch = false
    private lateinit var data: ArrayList<String>
    private var mLastClickTime: Long = 0
    override fun fileAllNatatkiAlboZakladki(semuxa: Int) {
        if (semuxa == 1) {
            BibleGlobalList.zakladkiSemuxa.removeAll(BibleGlobalList.zakladkiSemuxa)
            adapter.notifyDataSetChanged()
            val fileZakladki = File("$filesDir/BibliaSemuxaZakladki.json")
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        }
        if (semuxa == 2) {
            BibleGlobalList.zakladkiSinodal.removeAll(BibleGlobalList.zakladkiSinodal)
            adapter.notifyDataSetChanged()
            val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        }
        help.visibility = View.VISIBLE
        ListView.visibility = View.GONE
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        setContentView(R.layout.akafist_list_bible)
        semuxa = intent.getIntExtra("semuxa", 1)
        if (semuxa == 1) data = BibleGlobalList.zakladkiSemuxa
        if (semuxa == 2) data = BibleGlobalList.zakladkiSinodal
        adapter = BibleZakladkiListAdaprer(this, data)
        if (data.size == 0) {
            help.visibility = View.VISIBLE
            ListView.visibility = View.GONE
        }
        if (dzenNoch) help.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        ListView.adapter = adapter
        ListView.isVerticalScrollBarEnabled = false
        ListView.onItemClickListener = this
        ListView.onItemLongClickListener = this
    }

    private fun setTollbarTheme() {
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
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_toolbar.setText(by.carkva_gazeta.malitounik.R.string.zakladki_bible)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.trash).isVisible = data.size != 0
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.zakladki_i_natatki, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.trash) {
            val natatki = DialogDeliteAllZakladkiINatatki.getInstance(resources.getString(by.carkva_gazeta.malitounik.R.string.zakladki_bible).toLowerCase(Locale.getDefault()), semuxa)
            natatki.show(supportFragmentManager, "delite_all_zakladki_i_natatki")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun natatkidiliteItem(position: Int, semuxa: Int) {}

    override fun zakladkadiliteItem(position: Int, semuxa: Int) {
        if (semuxa == 1) {
            BibleGlobalList.zakladkiSemuxa.removeAt(position)
            adapter.notifyDataSetChanged()
            val fileZakladki = File("$filesDir/BibliaSemuxaZakladki.json")
            if (BibleGlobalList.zakladkiSemuxa.size == 0) {
                if (fileZakladki.exists()) {
                    fileZakladki.delete()
                }
                help.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            } else {
                val gson = Gson()
                val outputStream = FileWriter(fileZakladki)
                outputStream.write(gson.toJson(BibleGlobalList.zakladkiSemuxa))
                outputStream.close()
            }
        }
        if (semuxa == 2) {
            BibleGlobalList.zakladkiSinodal.removeAt(position)
            adapter.notifyDataSetChanged()
            val fileZakladki = File("$filesDir/BibliaSinodalZakladki.json")
            if (BibleGlobalList.zakladkiSinodal.size == 0) {
                if (fileZakladki.exists()) {
                    fileZakladki.delete()
                }
                help.visibility = View.VISIBLE
                ListView.visibility = View.GONE
            } else {
                val gson = Gson()
                val outputStream = FileWriter(fileZakladki)
                outputStream.write(gson.toJson(BibleGlobalList.zakladkiSinodal))
                outputStream.close()
            }
        }
        invalidateOptionsMenu()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val knigaName = parent.getItemAtPosition(position).toString()
        var kniga = -1
        var knigaS = -1
        var t1: Int
        var t2 = 0
        var t3 = 0
        var glava = 0
        if (semuxa == 1) {
            if (knigaName.contains("Паводле Мацьвея")) kniga = 0
            if (knigaName.contains("Паводле Марка")) kniga = 1
            if (knigaName.contains("Паводле Лукаша")) kniga = 2
            if (knigaName.contains("Паводле Яна")) kniga = 3
            if (knigaName.contains("Дзеі Апосталаў")) kniga = 4
            if (knigaName.contains("Якава")) kniga = 5
            if (knigaName.contains("1-е Пятра")) kniga = 6
            if (knigaName.contains("2-е Пятра")) kniga = 7
            if (knigaName.contains("1-е Яна Багаслова")) kniga = 8
            if (knigaName.contains("2-е Яна Багаслова")) kniga = 9
            if (knigaName.contains("3-е Яна Багаслова")) kniga = 10
            if (knigaName.contains("Юды")) kniga = 11
            if (knigaName.contains("Да Рымлянаў")) kniga = 12
            if (knigaName.contains("1-е да Карынфянаў")) kniga = 13
            if (knigaName.contains("2-е да Карынфянаў")) kniga = 14
            if (knigaName.contains("Да Галятаў")) kniga = 15
            if (knigaName.contains("Да Эфэсянаў")) kniga = 16
            if (knigaName.contains("Да Піліпянаў")) kniga = 17
            if (knigaName.contains("Да Каласянаў")) kniga = 18
            if (knigaName.contains("1-е да Фесаланікійцаў")) kniga = 19
            if (knigaName.contains("2-е да Фесаланікійцаў")) kniga = 20
            if (knigaName.contains("1-е да Цімафея")) kniga = 21
            if (knigaName.contains("2-е да Цімафея")) kniga = 22
            if (knigaName.contains("Да Ціта")) kniga = 23
            if (knigaName.contains("Да Філімона")) kniga = 24
            if (knigaName.contains("Да Габрэяў")) kniga = 25
            if (knigaName.contains("Адкрыцьцё (Апакаліпсіс)")) kniga = 26
            if (knigaName.contains("Быцьцё")) knigaS = 0
            if (knigaName.contains("Выхад")) knigaS = 1
            if (knigaName.contains("Лявіт")) knigaS = 2
            if (knigaName.contains("Лікі")) knigaS = 3
            if (knigaName.contains("Другі Закон")) knigaS = 4
            if (knigaName.contains("Ісуса сына Нава")) knigaS = 5
            if (knigaName.contains("Судзьдзяў")) knigaS = 6
            if (knigaName.contains("Рут")) knigaS = 7
            if (knigaName.contains("1-я Царстваў")) knigaS = 8
            if (knigaName.contains("2-я Царстваў")) knigaS = 9
            if (knigaName.contains("3-я Царстваў")) knigaS = 10
            if (knigaName.contains("4-я Царстваў")) knigaS = 11
            if (knigaName.contains("1-я Летапісаў")) knigaS = 12
            if (knigaName.contains("2-я Летапісаў")) knigaS = 13
            if (knigaName.contains("Эздры")) knigaS = 14
            if (knigaName.contains("Нээміі")) knigaS = 15
            if (knigaName.contains("Эстэр")) knigaS = 16
            if (knigaName.contains("Ёва")) knigaS = 17
            if (knigaName.contains("Псалтыр")) knigaS = 18
            if (knigaName.contains("Выслоўяў Саламонавых")) knigaS = 19
            if (knigaName.contains("Эклезіяста")) knigaS = 20
            if (knigaName.contains("Найвышэйшая Песьня Саламонава")) knigaS = 21
            if (knigaName.contains("Ісаі")) knigaS = 22
            if (knigaName.contains("Ераміі")) knigaS = 23
            if (knigaName.contains("Ераміін Плач")) knigaS = 24
            if (knigaName.contains("Езэкііля")) knigaS = 25
            if (knigaName.contains("Данііла")) knigaS = 26
            if (knigaName.contains("Асіі")) knigaS = 27
            if (knigaName.contains("Ёіля")) knigaS = 28
            if (knigaName.contains("Амоса")) knigaS = 29
            if (knigaName.contains("Аўдзея")) knigaS = 30
            if (knigaName.contains("Ёны")) knigaS = 31
            if (knigaName.contains("Міхея")) knigaS = 32
            if (knigaName.contains("Навума")) knigaS = 33
            if (knigaName.contains("Абакума")) knigaS = 34
            if (knigaName.contains("Сафона")) knigaS = 35
            if (knigaName.contains("Агея")) knigaS = 36
            if (knigaName.contains("Захарыі")) knigaS = 37
            if (knigaName.contains("Малахіі")) knigaS = 38
            t1 = knigaName.indexOf("Разьдзел ")
            t2 = knigaName.indexOf("/", t1)
            t3 = knigaName.indexOf("\n\n")
            glava = knigaName.substring(t1 + 9, t2).toInt()
        }
        if (semuxa == 2) {
            if (knigaName.contains("От Матфея")) kniga = 0
            if (knigaName.contains("От Марка")) kniga = 1
            if (knigaName.contains("От Луки")) kniga = 2
            if (knigaName.contains("От Иоанна")) kniga = 3
            if (knigaName.contains("Деяния святых апостолов")) kniga = 4
            if (knigaName.contains("Иакова")) kniga = 5
            if (knigaName.contains("1-е Петра")) kniga = 6
            if (knigaName.contains("2-е Петра")) kniga = 7
            if (knigaName.contains("1-е Иоанна")) kniga = 8
            if (knigaName.contains("2-е Иоанна")) kniga = 9
            if (knigaName.contains("3-е Иоанна")) kniga = 10
            if (knigaName.contains("Иуды")) kniga = 11
            if (knigaName.contains("Римлянам")) kniga = 12
            if (knigaName.contains("1-е Коринфянам")) kniga = 13
            if (knigaName.contains("2-е Коринфянам")) kniga = 14
            if (knigaName.contains("Галатам")) kniga = 15
            if (knigaName.contains("Ефесянам")) kniga = 16
            if (knigaName.contains("Филиппийцам")) kniga = 17
            if (knigaName.contains("Колоссянам")) kniga = 18
            if (knigaName.contains("1-е Фессалоникийцам (Солунянам)")) kniga = 19
            if (knigaName.contains("2-е Фессалоникийцам (Солунянам)")) kniga = 20
            if (knigaName.contains("1-е Тимофею")) kniga = 21
            if (knigaName.contains("2-е Тимофею")) kniga = 22
            if (knigaName.contains("Титу")) kniga = 23
            if (knigaName.contains("Филимону")) kniga = 24
            if (knigaName.contains("Евреям")) kniga = 25
            if (knigaName.contains("Откровение (Апокалипсис)")) kniga = 26
            if (knigaName.contains("Бытие")) knigaS = 0
            if (knigaName.contains("Исход")) knigaS = 1
            if (knigaName.contains("Левит")) knigaS = 2
            if (knigaName.contains("Числа")) knigaS = 3
            if (knigaName.contains("Второзаконие")) knigaS = 4
            if (knigaName.contains("Иисуса Навина")) knigaS = 5
            if (knigaName.contains("Судей израилевых")) knigaS = 6
            if (knigaName.contains("Руфи")) knigaS = 7
            if (knigaName.contains("1-я Царств")) knigaS = 8
            if (knigaName.contains("2-я Царств")) knigaS = 9
            if (knigaName.contains("3-я Царств")) knigaS = 10
            if (knigaName.contains("4-я Царств")) knigaS = 11
            if (knigaName.contains("1-я Паралипоменон")) knigaS = 12
            if (knigaName.contains("2-я Паралипоменон")) knigaS = 13
            if (knigaName.contains("1-я Ездры")) knigaS = 14
            if (knigaName.contains("Неемии")) knigaS = 15
            if (knigaName.contains("2-я Ездры")) knigaS = 16
            if (knigaName.contains("Товита")) knigaS = 17
            if (knigaName.contains("Иудифи")) knigaS = 18
            if (knigaName.contains("Есфири")) knigaS = 19
            if (knigaName.contains("Иова")) knigaS = 20
            if (knigaName.contains("Псалтирь")) knigaS = 21
            if (knigaName.contains("Притчи Соломона")) knigaS = 22
            if (knigaName.contains("Екклезиаста")) knigaS = 23
            if (knigaName.contains("Песнь песней Соломона")) knigaS = 24
            if (knigaName.contains("Премудрости Соломона")) knigaS = 25
            if (knigaName.contains("Премудрости Иисуса, сына Сирахова")) knigaS = 26
            if (knigaName.contains("Исаии")) knigaS = 27
            if (knigaName.contains("Иеремии")) knigaS = 28
            if (knigaName.contains("Плач Иеремии")) knigaS = 29
            if (knigaName.contains("Послание Иеремии")) knigaS = 30
            if (knigaName.contains("Варуха")) knigaS = 31
            if (knigaName.contains("Иезекииля")) knigaS = 32
            if (knigaName.contains("Даниила")) knigaS = 33
            if (knigaName.contains("Осии")) knigaS = 34
            if (knigaName.contains("Иоиля")) knigaS = 35
            if (knigaName.contains("Амоса")) knigaS = 36
            if (knigaName.contains("Авдия")) knigaS = 37
            if (knigaName.contains("Ионы")) knigaS = 38
            if (knigaName.contains("Михея")) knigaS = 39
            if (knigaName.contains("Наума")) knigaS = 40
            if (knigaName.contains("Аввакума")) knigaS = 41
            if (knigaName.contains("Сафонии")) knigaS = 42
            if (knigaName.contains("Аггея")) knigaS = 43
            if (knigaName.contains("Захарии")) knigaS = 44
            if (knigaName.contains("Малахии")) knigaS = 45
            if (knigaName.contains("1-я Маккавейская")) knigaS = 46
            if (knigaName.contains("2-я Маккавейская")) knigaS = 47
            if (knigaName.contains("3-я Маккавейская")) knigaS = 48
            if (knigaName.contains("3-я Ездры")) knigaS = 49
            t1 = knigaName.indexOf("Глава ")
            t2 = knigaName.indexOf("/", t1)
            t3 = knigaName.indexOf("\n\n", t2)
            glava = knigaName.substring(t1 + 6, t2).toInt()
        }
        val stix = knigaName.substring(t2 + 6, t3).toInt()
        var intent = Intent()
        if (kniga != -1) {
            if (semuxa == 1) {
                intent = Intent(this, NovyZapavietSemuxa::class.java)
            }
            if (semuxa == 2) {
                intent = Intent(this, NovyZapavietSinaidal::class.java)
            }
            intent.putExtra("kniga", kniga)
        }
        if (knigaS != -1) {
            if (semuxa == 1) {
                intent = Intent(this, StaryZapavietSemuxa::class.java)
            }
            if (semuxa == 2) {
                intent = Intent(this, StaryZapavietSinaidal::class.java)
            }
            intent.putExtra("kniga", knigaS)
        }
        intent.putExtra("glava", glava - 1)
        intent.putExtra("stix", stix - 1)
        startActivityForResult(intent, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 500) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val t1 = data[position].indexOf("\n\n")
        val t2: Int
        t2 = if (semuxa == 1) data[position].indexOf(". ", t1) else data[position].indexOf(" ", t1)
        val delite = DialogZakladkaDelite.getInstance(position, data[position].substring(0, t1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + data[position].substring(t1 + 2, t2), semuxa, true)
        delite.show(supportFragmentManager, "zakladka_delite")
        return true
    }

    private inner class BibleZakladkiListAdaprer(private val mContext: Activity, private val itemsL: ArrayList<String>) : ArrayAdapter<String>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_3, by.carkva_gazeta.malitounik.R.id.label, itemsL) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        override fun add(string: String?) {
            super.add(string)
            itemsL.add(string ?: "")
        }

        override fun remove(string: String?) {
            super.remove(string)
            itemsL.remove(string)
        }

        override fun clear() {
            super.clear()
            itemsL.clear()
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_3, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
                viewHolder.buttonPopup = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.button_popup)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.buttonPopup?.setOnClickListener { viewHolder.buttonPopup?.let { showPopupMenu(it, position, itemsL[position]) } }
            viewHolder.text?.text = itemsL[position]
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, by.carkva_gazeta.malitounik.R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }

        private fun showPopupMenu(view: View, position: Int, name: String) {
            val popup = PopupMenu(mContext, view)
            val infl = popup.menuInflater
            infl.inflate(by.carkva_gazeta.malitounik.R.menu.popup, popup.menu)
            popup.menu.getItem(0).isVisible = false
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                when (menuItem.itemId) {
                    by.carkva_gazeta.malitounik.R.id.menu_redoktor -> return@setOnMenuItemClickListener true
                    by.carkva_gazeta.malitounik.R.id.menu_remove -> {
                        val t1 = name.indexOf("\n\n")
                        val t2: Int
                        t2 = if (semuxa == 1) name.indexOf(". ", t1) else name.indexOf(" ", t1)
                        val delite = DialogZakladkaDelite.getInstance(position, name.substring(0, t1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + name.substring(t1 + 2, t2), semuxa, true)
                        delite.show(supportFragmentManager, "zakladka_delite")
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
            popup.show()
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
        var buttonPopup: ImageView? = null
    }
}