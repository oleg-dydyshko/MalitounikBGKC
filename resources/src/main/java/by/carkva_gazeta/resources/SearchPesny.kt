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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var mLastClickTime: Long = 0
    override fun onResume() {
        super.onResume()
        if (!by.carkva_gazeta.malitounik.MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = by.carkva_gazeta.malitounik.MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!menuListDataIsInitialized())
            menuListData = getMenuListData(this)
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
        TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, by.carkva_gazeta.malitounik.SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
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
        ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, view: View?, _: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val ll = view as LinearLayout
            val label: by.carkva_gazeta.malitounik.TextViewRobotoCondensed = ll.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            val strText = label.text.toString()
            val intent = Intent(this@SearchPesny, SearchPesnyViewResult::class.java)
            intent.putExtra("resultat", strText)
            startActivity(intent)
        }
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                // Скрываем клавиатуру
                val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(editText.windowToken, 0)
            }
            false
        }
        editText.imeOptions = EditorInfo.IME_ACTION_GO
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
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, by.carkva_gazeta.malitounik.SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
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
            menuListData.sort()
            for (i in menuListData.indices) {
                val inputStream = resources.openRawResource(menuListData[i].id)
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
                    adapter.add(menuListData[i].data)
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
                edit = edit.replace("И", "І")
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

    private class SearchListAdaprer(private val mContext: Activity, private val adapterList: ArrayList<String?>) : ArrayAdapter<String?>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, adapterList) {
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
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, by.carkva_gazeta.malitounik.SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, by.carkva_gazeta.malitounik.R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }
    }

    private class ViewHolder {
        var text: by.carkva_gazeta.malitounik.TextViewRobotoCondensed? = null
    }

    companion object {

        lateinit var menuListData: ArrayList<by.carkva_gazeta.malitounik.MenuListData>

        fun menuListDataIsInitialized() = ::menuListData.isInitialized

        private fun listRaw(filename: String): Int {
            val fields = R.raw::class.java.fields
            var id = 0
            run files@{
                fields.forEach {
                    if (it.name == filename) {
                        id = it.getInt(it)
                        return@files
                    }
                }
            }
            return id
        }

        fun getMenuListData(context: Context): ArrayList<by.carkva_gazeta.malitounik.MenuListData> {
            val menuListData = ArrayList<by.carkva_gazeta.malitounik.MenuListData>()
            val inputStream = context.resources.openRawResource(by.carkva_gazeta.malitounik.R.raw.pesny_menu)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.forEachLine {
                line = it
                val split = line.split("<>").toTypedArray()
                menuListData.add(by.carkva_gazeta.malitounik.MenuListData(listRaw(split[0]), split[1]))
            }
            return menuListData
        }

        fun getPesniaID(context: Context, name: String): Int {
            if (!::menuListData.isInitialized)
                menuListData = getMenuListData(context)
            for (list_data in menuListData) {
                if (list_data.data == name) return list_data.id
            }
            return -1
        }
    }
}