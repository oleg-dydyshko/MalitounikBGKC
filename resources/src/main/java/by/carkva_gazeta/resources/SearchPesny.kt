package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.R.raw
import kotlinx.android.synthetic.main.search_biblia.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by oleg on 5.10.16
 */
class SearchPesny : AppCompatActivity() {
    private lateinit var adapter: SearchListAdaprer
    private var dzenNoch = false
    private var posukPesenTimer: Timer? = null
    private var posukPesenSchedule: TimerTask? = null
    private var menuListData1: ArrayList<MenuListData> = getMenuListData()
    private var mLastClickTime: Long = 0
    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_biblia)
        if (dzenNoch) {
            buttonx.setImageResource(by.carkva_gazeta.malitounik.R.drawable.cancel)
        }
        buttonx.setOnClickListener {
            editText.setText("")
            adapter.clear()
            TextView.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        adapter = SearchListAdaprer(this, ArrayList())
        ListView.adapter = adapter
        ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                if (i == 1) { // Скрываем клавиатуру
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(editText.windowToken, 0)
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
        TextView.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        if (dzenNoch) {
            TextView.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        }
        ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, view: View, _: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val ll = view as LinearLayout
            val label: TextViewRobotoCondensed = ll.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            val strText = label.text.toString()
            val intent = Intent(this@SearchPesny, SearchPesnyViewResult::class.java)
            intent.putExtra("resultat", strText)
            startActivity(intent)
        }
        editText.addTextChangedListener(MyTextWatcher())
        setTollbarTheme()
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
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.search)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            title_toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
            title_toolbar.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorIcons))
        } else {
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
            title_toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
        }
    }

    private fun stopPosukPesen() {
        if (posukPesenTimer != null) {
            posukPesenTimer?.cancel()
            posukPesenTimer = null
        }
        posukPesenSchedule = null
    }

    private fun startPosukPesen(poshuk: String) {
        if (posukPesenTimer == null) {
            posukPesenTimer = Timer()
            if (posukPesenSchedule != null) {
                posukPesenSchedule?.cancel()
                posukPesenSchedule = null
            }
            posukPesenSchedule = object : TimerTask() {
                override fun run() {
                    runOnUiThread { rawAsset(poshuk) }
                }
            }
            posukPesenTimer?.schedule(posukPesenSchedule, 0)
        }
    }

    private fun rawAsset(poshuk: String) {
        var poshuk1 = poshuk
        var setClear = true
        if (poshuk1 != "") {
            poshuk1 = poshuk1.toLowerCase(Locale.getDefault())
            poshuk1 = poshuk1.replace("ё", "е")
            poshuk1 = poshuk1.replace("све", "сьве")
            poshuk1 = poshuk1.replace("сві", "сьві")
            poshuk1 = poshuk1.replace("свя", "сьвя")
            poshuk1 = poshuk1.replace("зве", "зьве")
            poshuk1 = poshuk1.replace("зві", "зьві")
            poshuk1 = poshuk1.replace("звя", "зьвя")
            poshuk1 = poshuk1.replace("зме", "зьме")
            poshuk1 = poshuk1.replace("змі", "зьмі")
            poshuk1 = poshuk1.replace("змя", "зьмя")
            poshuk1 = poshuk1.replace("зня", "зьня")
            poshuk1 = poshuk1.replace("сле", "сьле")
            poshuk1 = poshuk1.replace("слі", "сьлі")
            poshuk1 = poshuk1.replace("сль", "сьль")
            poshuk1 = poshuk1.replace("слю", "сьлю")
            poshuk1 = poshuk1.replace("сля", "сьля")
            poshuk1 = poshuk1.replace("сне", "сьне")
            poshuk1 = poshuk1.replace("сні", "сьні")
            poshuk1 = poshuk1.replace("сню", "сьню")
            poshuk1 = poshuk1.replace("сня", "сьня")
            poshuk1 = poshuk1.replace("спе", "сьпе")
            poshuk1 = poshuk1.replace("спі", "сьпі")
            poshuk1 = poshuk1.replace("спя", "сьпя")
            poshuk1 = poshuk1.replace("сце", "сьце")
            poshuk1 = poshuk1.replace("сці", "сьці")
            poshuk1 = poshuk1.replace("сць", "сьць")
            poshuk1 = poshuk1.replace("сцю", "сьцю")
            poshuk1 = poshuk1.replace("сця", "сьця")
            poshuk1 = poshuk1.replace("цце", "цьце")
            poshuk1 = poshuk1.replace("цці", "цьці")
            poshuk1 = poshuk1.replace("ццю", "цьцю")
            poshuk1 = poshuk1.replace("ззе", "зьзе")
            poshuk1 = poshuk1.replace("ззі", "зьзі")
            poshuk1 = poshuk1.replace("ззю", "зьзю")
            poshuk1 = poshuk1.replace("ззя", "зьзя")
            poshuk1 = poshuk1.replace("зле", "зьле")
            poshuk1 = poshuk1.replace("злі", "зьлі")
            poshuk1 = poshuk1.replace("злю", "зьлю")
            poshuk1 = poshuk1.replace("зля", "зьля")
            poshuk1 = poshuk1.replace("збе", "зьбе")
            poshuk1 = poshuk1.replace("збі", "зьбі")
            poshuk1 = poshuk1.replace("збя", "зьбя")
            poshuk1 = poshuk1.replace("нне", "ньне")
            poshuk1 = poshuk1.replace("нні", "ньні")
            poshuk1 = poshuk1.replace("нню", "ньню")
            poshuk1 = poshuk1.replace("ння", "ньня")
            poshuk1 = poshuk1.replace("лле", "льле")
            poshuk1 = poshuk1.replace("ллі", "льлі")
            poshuk1 = poshuk1.replace("ллю", "льлю")
            poshuk1 = poshuk1.replace("лля", "льля")
            poshuk1 = poshuk1.replace("дск", "дзк")
            poshuk1 = poshuk1.replace("дств", "дзтв")
            poshuk1 = poshuk1.replace("з’е", "зье")
            poshuk1 = poshuk1.replace("з’я", "зья")
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (r >= 3) {
                    if (poshuk1[r] == aM) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r))
                    }
                }
            }
            Collections.sort(menuListData1, MenuListDataSort())
            for (i in menuListData1.indices) {
                val inputStream = resources.openRawResource(menuListData1[i].id)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                val builder = StringBuilder()
                reader.forEachLine {
                    line = it.replace(",", "")
                    line = line.replace(" — ", " ")
                    line = line.replace("(", "")
                    line = line.replace(")", "")
                    line = line.replace(".", "")
                    line = line.replace("!", "")
                    builder.append(line).append("\n")
                }
                inputStream.close()
                if (builder.toString().toLowerCase(Locale.getDefault()).replace("ё", "е").contains(poshuk1.toLowerCase(Locale.getDefault()))) {
                    if (setClear) {
                        adapter.clear()
                        setClear = false
                    }
                    adapter.add(menuListData1[i].data)
                }
            }
            if (setClear) {
                adapter.clear()
            }
        }
        TextView.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
    }

    private inner class MyTextWatcher : TextWatcher {
        private var editPosition = 0
        private var check = 0
        private var editch = true
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            editch = count != after
            check = after
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable) {
            if (editch) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                if (edit.length >= 3) {
                    stopPosukPesen()
                    startPosukPesen(edit)
                } else {
                    adapter.clear()
                    adapter.notifyDataSetChanged()
                    TextView.text = resources.getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
                }
                if (check != 0) {
                    editText.removeTextChangedListener(this)
                    editText.setText(edit)
                    editText.setSelection(editPosition)
                    editText.addTextChangedListener(this)
                }
            }
        }
    }

    private class SearchListAdaprer internal constructor(private val mContext: Activity, private val adapterList: ArrayList<String?>) : ArrayAdapter<String?>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, adapterList) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text?.text = adapterList[position]
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, by.carkva_gazeta.malitounik.R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        fun getMenuListData(): ArrayList<MenuListData> {
            val menuListData = ArrayList<MenuListData>()
            menuListData.add(MenuListData(raw.pesny_bag_0, "Анёл ад Бога Габрыэль"))
            menuListData.add(MenuListData(raw.pesny_bag_1, "З далёкай Фацімы"))
            menuListData.add(MenuListData(raw.pesny_bag_2, "Люрдаўская песьня"))
            menuListData.add(MenuListData(raw.pesny_bag_3, "Матачка Божая"))
            menuListData.add(MenuListData(raw.pesny_bag_4, "Маці Божая Будслаўская"))
            menuListData.add(MenuListData(raw.pesny_bag_5, "Маці Божая ў Жыровіцах"))
            menuListData.add(MenuListData(raw.pesny_bag_6, "Маці з Фацімы"))
            menuListData.add(MenuListData(raw.pesny_bag_7, "Маці мая Божая"))
            menuListData.add(MenuListData(raw.pesny_bag_8, "Мне аднойчы"))
            menuListData.add(MenuListData(raw.pesny_bag_9, "О Марыя, Маці Бога (1)"))
            menuListData.add(MenuListData(raw.pesny_bag_10, "О Марыя, Маці Бога (2)"))
            menuListData.add(MenuListData(raw.pesny_bag_11, "Памаліся, Марыя"))
            menuListData.add(MenuListData(raw.pesny_bag_12, "Песьня да Маці Божай Нястомнай Дапамогі"))
            menuListData.add(MenuListData(raw.pesny_bag_13, "Радуйся, Марыя!"))
            menuListData.add(MenuListData(raw.pesny_bag_14, "Табе, Марыя, давяраю я"))
            menuListData.add(MenuListData(raw.pesny_bag_15, "Ціхая, пакорная"))
            menuListData.add(MenuListData(raw.pesny_bel_0, "Ave Maria"))
            menuListData.add(MenuListData(raw.pesny_bel_1, "Божа, што калісь народы"))
            menuListData.add(MenuListData(raw.pesny_bel_2, "Божа, я малюся за Беларусь"))
            menuListData.add(MenuListData(raw.pesny_bel_3, "Вечна жывi, мая Беларусь"))
            menuListData.add(MenuListData(raw.pesny_bel_4, "К табе, Беларусь"))
            menuListData.add(MenuListData(raw.pesny_bel_5, "Магутны Божа"))
            menuListData.add(MenuListData(raw.pesny_bel_6, "Малюся за цябе, Беларусь"))
            menuListData.add(MenuListData(raw.pesny_bel_7, "Малітва"))
            menuListData.add(MenuListData(raw.pesny_bel_8, "Мая краіна"))
            menuListData.add(MenuListData(raw.pesny_bel_9, "Мы хочам Бога"))
            menuListData.add(MenuListData(raw.pesny_kal_0, "Ave Maria (Зорка зазьзяла)"))
            menuListData.add(MenuListData(raw.pesny_kal_1, "А што гэта за сьпевы"))
            menuListData.add(MenuListData(raw.pesny_kal_2, "А ў сьвеце нам навіна была"))
            menuListData.add(MenuListData(raw.pesny_kal_3, "А ўчора з вячора"))
            menuListData.add(MenuListData(raw.pesny_kal_4, "Вясёлых калядных сьвятаў"))
            menuListData.add(MenuListData(raw.pesny_kal_5, "Зазьзяла зорачка над Бэтлеемам"))
            menuListData.add(MenuListData(raw.pesny_kal_6, "Звон зьвініць"))
            menuListData.add(MenuListData(raw.pesny_kal_7, "На шляху ў Бэтлеем"))
            menuListData.add(MenuListData(raw.pesny_kal_8, "Неба і зямля"))
            menuListData.add(MenuListData(raw.pesny_kal_9, "Нова радасьць стала"))
            menuListData.add(MenuListData(raw.pesny_kal_10, "Ночка цiхая, зарыста"))
            menuListData.add(MenuListData(raw.pesny_kal_11, "Ноччу сьвятой"))
            menuListData.add(MenuListData(raw.pesny_kal_12, "Паказалась з неба яснасьць"))
            menuListData.add(MenuListData(raw.pesny_kal_13, "Прыйдзіце да Збаўцы"))
            menuListData.add(MenuListData(raw.pesny_kal_14, "Радасная вестка"))
            menuListData.add(MenuListData(raw.pesny_kal_15, "У начную ціш"))
            menuListData.add(MenuListData(raw.pesny_kal_16, "Учора зьвячора — засьвяціла зора"))
            menuListData.add(MenuListData(raw.pesny_kal_17, "Ціхая ноч (пер. Н. Арсеньневай)"))
            menuListData.add(MenuListData(raw.pesny_kal_18, "Ціхая ноч-2"))
            menuListData.add(MenuListData(raw.pesny_kal_19, "Ціхая ноч-3"))
            menuListData.add(MenuListData(raw.pesny_kal_20, "Прыйдзі, прыйдзі, Эмануэль (ХІХ ст.)"))
            menuListData.add(MenuListData(raw.pesny_kal_21, "Прыйдзі, прыйдзі, Эмануэль (XII–ХVIII стст.)"))
            menuListData.add(MenuListData(raw.pesny_prasl_0, "Ён паўсюль"))
            menuListData.add(MenuListData(raw.pesny_prasl_1, "Ісус вызваліў мяне"))
            menuListData.add(MenuListData(raw.pesny_prasl_2, "Ісус нам дае збаўленьне"))
            menuListData.add(MenuListData(raw.pesny_prasl_3, "Айцец наш і наш Валадар"))
            menuListData.add(MenuListData(raw.pesny_prasl_4, "Алілуя!"))
            menuListData.add(MenuListData(raw.pesny_prasl_5, "Бог блаславіў гэты дзень"))
            menuListData.add(MenuListData(raw.pesny_prasl_6, "Бог ёсьць любоў"))
            menuListData.add(MenuListData(raw.pesny_prasl_7, "Богу сьпявай, уся зямля!"))
            menuListData.add(MenuListData(raw.pesny_prasl_8, "Божа мой"))
            menuListData.add(MenuListData(raw.pesny_prasl_9, "Браце мой"))
            menuListData.add(MenuListData(raw.pesny_prasl_10, "Весяліся і пляскай у далоні"))
            menuListData.add(MenuListData(raw.pesny_prasl_11, "Вольная воля"))
            menuListData.add(MenuListData(raw.pesny_prasl_12, "Вось маё сэрца"))
            menuListData.add(MenuListData(raw.pesny_prasl_13, "Вядзі мяне, Божа"))
            menuListData.add(MenuListData(raw.pesny_prasl_14, "Вялікім і цудоўным"))
            menuListData.add(MenuListData(raw.pesny_prasl_15, "Госпад мой заўсёды па маёй правіцы"))
            menuListData.add(MenuListData(raw.pesny_prasl_16, "Госпаду дзякуйце, бо добры Ён"))
            menuListData.add(MenuListData(raw.pesny_prasl_17, "Дай Духа любові"))
            menuListData.add(MenuListData(raw.pesny_prasl_18, "Дай уславіць Цябе"))
            menuListData.add(MenuListData(raw.pesny_prasl_19, "Дай, добры Божа"))
            menuListData.add(MenuListData(raw.pesny_prasl_20, "Дакраніся да маіх вачэй"))
            menuListData.add(MenuListData(raw.pesny_prasl_21, "Дзякуй за ўсё, што Ты стварыў"))
            menuListData.add(MenuListData(raw.pesny_prasl_22, "Дзякуй!"))
            menuListData.add(MenuListData(raw.pesny_prasl_23, "З намі — Пятро і Андрэй"))
            menuListData.add(MenuListData(raw.pesny_prasl_24, "Знайдзі мяне"))
            menuListData.add(MenuListData(raw.pesny_prasl_25, "Зоркі далёка"))
            menuListData.add(MenuListData(raw.pesny_prasl_26, "Кадош (Сьвяты)"))
            menuListData.add(MenuListData(raw.pesny_prasl_27, "Клічаш ты"))
            menuListData.add(MenuListData(raw.pesny_prasl_28, "Любоў Твая"))
            menuListData.add(MenuListData(raw.pesny_prasl_29, "Любіць — гэта ахвяраваць"))
            menuListData.add(MenuListData(raw.pesny_prasl_30, "Майго жыцьця — мой Бог крыніца"))
            menuListData.add(MenuListData(raw.pesny_prasl_31, "Маё сэрца"))
            menuListData.add(MenuListData(raw.pesny_prasl_32, "Маё шчасьце ў Iсуса"))
            menuListData.add(MenuListData(raw.pesny_prasl_33, "На псалтыры і на арфе"))
            menuListData.add(MenuListData(raw.pesny_prasl_34, "Настане дзень"))
            menuListData.add(MenuListData(raw.pesny_prasl_35, "Невычэрпныя ласкі ў Бога"))
            menuListData.add(MenuListData(raw.pesny_prasl_36, "О, калі б ты паслухаў Мяне"))
            menuListData.add(MenuListData(raw.pesny_prasl_37, "Ойча мой, к Табе іду"))
            menuListData.add(MenuListData(raw.pesny_prasl_38, "Ойча, мяне Ты любіш"))
            menuListData.add(MenuListData(raw.pesny_prasl_39, "Пакліканьне (Човен)"))
            menuListData.add(MenuListData(raw.pesny_prasl_40, "Пачуй мой кліч, чулы Ойча"))
            menuListData.add(MenuListData(raw.pesny_prasl_41, "Песьню славы засьпявайма"))
            menuListData.add(MenuListData(raw.pesny_prasl_42, "Песьня Давіда"))
            menuListData.add(MenuListData(raw.pesny_prasl_43, "Песьня вячэрняя"))
            menuListData.add(MenuListData(raw.pesny_prasl_44, "Песьня пілігрыма"))
            menuListData.add(MenuListData(raw.pesny_prasl_45, "Песьня ранішняя"))
            menuListData.add(MenuListData(raw.pesny_prasl_46, "Пяцёра пакутнікаў"))
            menuListData.add(MenuListData(raw.pesny_prasl_47, "Пілігрым"))
            menuListData.add(MenuListData(raw.pesny_prasl_48, "Руах"))
            menuListData.add(MenuListData(raw.pesny_prasl_49, "Сьвятло жыцьця"))
            menuListData.add(MenuListData(raw.pesny_prasl_50, "Сьпявайма добраму Богу"))
            menuListData.add(MenuListData(raw.pesny_prasl_51, "Сьпявайце Цару"))
            menuListData.add(MenuListData(raw.pesny_prasl_52, "Так, як імкнецца сарна"))
            menuListData.add(MenuListData(raw.pesny_prasl_53, "Твая любоў"))
            menuListData.add(MenuListData(raw.pesny_prasl_54, "Твая прысутнасьць"))
            menuListData.add(MenuListData(raw.pesny_prasl_55, "Толькі Ісус"))
            menuListData.add(MenuListData(raw.pesny_prasl_56, "Толькі Бог, толькі ты"))
            menuListData.add(MenuListData(raw.pesny_prasl_57, "Толькі Бог"))
            menuListData.add(MenuListData(raw.pesny_prasl_58, "Ты ведаеш сэрца маё"))
            menuListData.add(MenuListData(raw.pesny_prasl_59, "Ты ведаеш..."))
            menuListData.add(MenuListData(raw.pesny_prasl_60, "Ты — Госпад мой"))
            menuListData.add(MenuListData(raw.pesny_prasl_61, "Хвала Табе, вялікі Бог"))
            menuListData.add(MenuListData(raw.pesny_prasl_62, "Хвалім Цябе, Божа!"))
            menuListData.add(MenuListData(raw.pesny_prasl_63, "Хрыстос уваскрос! (Resucito)"))
            menuListData.add(MenuListData(raw.pesny_prasl_64, "Ці ты быў на Галгофе"))
            menuListData.add(MenuListData(raw.pesny_prasl_65, "Шалом алэхем (Мір вам)"))
            menuListData.add(MenuListData(raw.pesny_prasl_66, "Я люблю Цябе, Ойча міласэрны"))
            menuListData.add(MenuListData(raw.pesny_prasl_67, "Я ўстану рана, каб сьпяваць"))
            menuListData.add(MenuListData(raw.pesny_prasl_68, "Як гэта хораша й міла"))
            menuListData.add(MenuListData(raw.pesny_prasl_69, "Яму за ўсё слава"))
            menuListData.add(MenuListData(raw.pesny_prasl_70, "Цябе, Бога, хвалім"))
            menuListData.add(MenuListData(raw.pesny_prasl_71, "Мой Госпад, мой Збаўца"))
            menuListData.add(MenuListData(raw.pesny_taize_0, "Magnifikat"))
            menuListData.add(MenuListData(raw.pesny_taize_1, "Ostende nobis"))
            menuListData.add(MenuListData(raw.pesny_taize_2, "Ubi caritas"))
            menuListData.add(MenuListData(raw.pesny_taize_3, "Блаславёны Бог"))
            menuListData.add(MenuListData(raw.pesny_taize_4, "Бог мой, Iсус, сьвяцi нам у цемры"))
            menuListData.add(MenuListData(raw.pesny_taize_5, "Будзь са Мной"))
            menuListData.add(MenuListData(raw.pesny_taize_6, "Дай нам, Божа, моц ласкi Сваёй"))
            menuListData.add(MenuListData(raw.pesny_taize_7, "Дзякуем Табе, Божа наш"))
            menuListData.add(MenuListData(raw.pesny_taize_8, "Дзякуем Табе, Хрысьце"))
            menuListData.add(MenuListData(raw.pesny_taize_9, "Кожны дзень Бог дае мне сiлы"))
            menuListData.add(MenuListData(raw.pesny_taize_10, "Мая душа ў Богу мае спакой"))
            menuListData.add(MenuListData(raw.pesny_taize_11, "О, Iсусе"))
            menuListData.add(MenuListData(raw.pesny_taize_12, "О, Госпадзе мой"))
            menuListData.add(MenuListData(raw.pesny_taize_13, "Прыйдзi, Дух Сьвяты"))
            menuListData.add(MenuListData(raw.pesny_taize_14, "У цемры iдзём"))
            menuListData.add(MenuListData(raw.pesny_taize_15, "У цемры нашых дзён"))
            menuListData.add(MenuListData(raw.pesny_taize_16, "Хай тваё сэрца больш не журыцца"))
            // так же нужно добавить имена песен в by.carkva_gazeta.malitounik.Menu_pesny_*
            return menuListData
        }

        fun getPesniaID(name: String): Int {
            val menuListData = getMenuListData()
            for (list_data in menuListData) {
                if (list_data.data == name) return list_data.id
            }
            return -1
        }
    }
}