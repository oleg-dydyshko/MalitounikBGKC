package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuListData
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import kotlinx.android.synthetic.main.akafist_list_bible.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by oleg on 30.5.16
 */
class MalitvyPrynagodnyia : AppCompatActivity() {

    private val data: ArrayList<MenuListData> = ArrayList()
    private lateinit var adapter: MenuListAdaprer
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val chin = getSharedPreferences("biblia", MODE_PRIVATE)
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.akafist_list_bible)
        textSearch.addTextChangedListener(object : TextWatcher {

            var editPosition = 0
            var check = 0
            var editch = true

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                editch = count != after
                check = after
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editPosition = start + count
            }

            override fun afterTextChanged(s: Editable) {
                var edit = s.toString()
                if (editch) {
                    edit = edit.replace("и", "і")
                    edit = edit.replace("щ", "ў")
                    edit = edit.replace("ъ", "'")
                    edit = edit.replace("И", "І")
                    edit = edit.replace("Щ", "Ў")
                    edit = edit.replace("Ъ", "'")
                    if (check != 0) {
                        textSearch.removeTextChangedListener(this)
                        textSearch.setText(edit)
                        textSearch.setSelection(editPosition)
                        textSearch.addTextChangedListener(this)
                    }
                }
                adapter.filter.filter(edit)
            }
        })
        if (savedInstanceState?.getBoolean("edittext") == true) {
            textSearch.visibility = View.VISIBLE
        }
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
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        title_toolbar.text = resources.getText(by.carkva_gazeta.malitounik.R.string.prynagodnyia)
        if (dzenNoch) {
            toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
            toolbar.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        }
        data.add(MenuListData(R.raw.prynagodnyia_0, "Малітва аб блаславеньні"))
        data.add(MenuListData(R.raw.prynagodnyia_1, "Малітва аб дапамозе ў выбары жыцьцёвай дарогі дзіцяці"))
        data.add(MenuListData(R.raw.prynagodnyia_2, "Малітва аб еднасьці"))
        data.add(MenuListData(R.raw.prynagodnyia_3, "Малітва бацькоў за дзяцей 2"))
        data.add(MenuListData(R.raw.prynagodnyia_4, "Малітва бацькоў за дзяцей"))
        data.add(MenuListData(R.raw.prynagodnyia_5, "Малітва вадзіцеля"))
        data.add(MenuListData(R.raw.prynagodnyia_6, "Малітва вучня"))
        data.add(MenuListData(R.raw.prynagodnyia_7, "Малітва да Маці Божай Браслаўскай, Валадаркі Азёраў"))
        data.add(MenuListData(R.raw.prynagodnyia_8, "Малітва да Маці Божай Будслаўскай, Апякункі Беларусі"))
        data.add(MenuListData(R.raw.prynagodnyia_9, "Малітва да Маці Божай Нястомнай Дапамогі"))
        data.add(MenuListData(R.raw.prynagodnyia_10, "Малітва за Беларусь"))
        data.add(MenuListData(R.raw.prynagodnyia_11, "Малітва за дарослых дзяцей"))
        data.add(MenuListData(R.raw.prynagodnyia_12, "Малітва за дзяцей перад пачаткам навукі"))
        data.add(MenuListData(R.raw.prynagodnyia_13, "Малітва за парафію"))
        data.add(MenuListData(R.raw.prynagodnyia_14, "Малітва за хворага"))
        data.add(MenuListData(R.raw.prynagodnyia_15, "Малітва за хворае дзіця"))
        data.add(MenuListData(R.raw.prynagodnyia_16, "Малітва за хрысьціянскую еднасьць"))
        data.add(MenuListData(R.raw.prynagodnyia_17, "Малітва за ўмацаваньне ў любові"))
        data.add(MenuListData(R.raw.prynagodnyia_18, "Малітва маладога чалавека"))
        data.add(MenuListData(R.raw.prynagodnyia_19, "Малітва на ўсякую патрэбу"))
        data.add(MenuListData(R.raw.prynagodnyia_20, "Малітва падзякі за атрыманыя дабрадзействы"))
        data.add(MenuListData(R.raw.prynagodnyia_21, "Малітва перад пачаткам навучаньня"))
        data.add(MenuListData(R.raw.prynagodnyia_22, "Малітва перад іспытамі"))
        data.add(MenuListData(R.raw.prynagodnyia_23, "Малітва ранішняга намеру (Опціных старцаў)"))
        data.add(MenuListData(R.raw.prynagodnyia_24, "Малітвы за сьвятароў і сьвятарскія пакліканьні"))
        data.add(MenuListData(R.raw.prynagodnyia_25, "Малітвы ў часе хваробы і за хворых"))
        data.add(MenuListData(R.raw.prynagodnyia_26, "Намер ісьці за Хрыстом"))
        data.add(MenuListData(R.raw.prynagodnyia_27, "Цябе, Бога, хвалім"))
        data.sort()
        adapter = MenuListAdaprer(this)
        ListView.adapter = adapter
        ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (firstVisibleItem == 1) {
                    // Скрываем клавиатуру
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(textSearch.windowToken, 0)
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }

        })
        ListView.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, Prynagodnyia::class.java)
            intent.putExtra("prynagodnyia", data[position].data)
            intent.putExtra("prynagodnyiaID", data[position].id)
            startActivity(intent)
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textSearch.windowToken, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(by.carkva_gazeta.malitounik.R.menu.malitvy_prynagodnyia, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == by.carkva_gazeta.malitounik.R.id.action_seashe_text) {
            count.visibility = View.VISIBLE
            count.text = "(" + data.size.toString() + ")"
            textSearch.visibility = View.VISIBLE
            textSearch.requestFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (textSearch.visibility == View.VISIBLE) {
            textSearch.setText("")
            textSearch.visibility = View.GONE
            count.visibility = View.GONE
            val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textSearch.windowToken, 0)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (textSearch.visibility == View.VISIBLE) {
            outState.putBoolean("edittext", true)
        } else {
            outState.putBoolean("edittext", false)
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

    internal inner class MenuListAdaprer(private val context: Activity) : ArrayAdapter<MenuListData?>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, data as List<MenuListData>) {
        private val k: SharedPreferences = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val origData: ArrayList<MenuListData> = ArrayList(data)

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
            }
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    var constraint1 = constraint
                    constraint1 = constraint1.toString().toLowerCase(Locale.getDefault())
                    val result = FilterResults()
                    if (constraint1.toString().isNotEmpty()) {
                        val founded: ArrayList<MenuListData> = ArrayList()
                        for (item in origData) {
                            if (item.data.toLowerCase(Locale.getDefault()).contains(constraint1)) {
                                founded.add(item)
                            }
                        }
                        result.values = founded
                        result.count = founded.size
                    } else {
                        result.values = origData
                        result.count = origData.size
                    }
                    return result
                }

                @SuppressLint("SetTextI18n")
                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    clear()
                    for (item in results.values as ArrayList<*>) {
                        add(item as MenuListData)
                    }
                    if (results.count > 0) this@MalitvyPrynagodnyia.count.text = "(" + results.count.toString() + ")" else this@MalitvyPrynagodnyia.count.setText(by.carkva_gazeta.malitounik.R.string.niama)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}
