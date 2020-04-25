package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.search_sviatyia.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 21.10.18
 */
class SearchSviatyia : AppCompatActivity() {
    private lateinit var adapter: SearchListAdapter
    private var dzenNoch = false
    private var posukPesenTimer: Timer? = null
    private var posukPesenSchedule: TimerTask? = null
    private var arrayLists: ArrayList<ArrayList<String>> = ArrayList()
    private var arrayRes: ArrayList<String> = ArrayList()
    private lateinit var chin: SharedPreferences
    private lateinit var c: GregorianCalendar
    private var mLastClickTime: Long = 0
    private val munName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.alphain, R.anim.alphaout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        c = Calendar.getInstance() as GregorianCalendar
        dzenNoch = chin.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(R.style.AppCompatDark)
        setContentView(R.layout.search_sviatyia)
        if (dzenNoch) {
            buttonx.setImageResource(R.drawable.cancel)
        }
        buttonx.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            editText.setText("")
            adapter.clear()
            TextView.text = resources.getString(R.string.seash, 0)
            val prefEditors = chin.edit()
            prefEditors.putString("search_svityx_string", "")
            prefEditors.apply()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
        TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
        if (chin.getString("search_svityx_string", "") != "") {
            if (savedInstanceState == null) {
                val gson = Gson()
                val json = chin.getString("search_svityx_array", "")
                val type = object : TypeToken<ArrayList<String?>?>() {}.type
                arrayRes.addAll(gson.fromJson(json, type))
                TextView.text = resources.getString(R.string.seash, arrayRes.size)
                for (i in arrayRes.indices) {
                    if (dzenNoch) arrayRes[i] = arrayRes[i].replace("#d00505", "#f44336") else arrayRes[i] = arrayRes[i].replace("#f44336", "#d00505")
                }
                editText.setText(chin.getString("search_svityx_string", ""))
                val editPosition: Int = editText.text?.length ?: 0
                editText.setSelection(editPosition)
            }
        }
        adapter = SearchListAdapter(this, arrayRes)
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
        TextView.text = resources.getString(R.string.seash, adapter.count)
        if (dzenNoch) {
            TextView.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        }
        ListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val result = arrayRes[position]
            val t1 = result.indexOf("<!--")
            val t2 = result.indexOf(":")
            val t3 = result.indexOf("-->")
            val g = GregorianCalendar(c[Calendar.YEAR], result.substring(t2 + 1, t3).toInt(), result.substring(t1 + 4, t2).toInt())
            val intent = Intent()
            intent.putExtra("data", g[Calendar.DAY_OF_YEAR] - 1)
            setResult(140, intent)
            finish()
        }
        file
        MenuCviaty.getPrazdnik(this, c[Calendar.YEAR])
        editText.addTextChangedListener(MyTextWatcher())
        setTollbarTheme()
    }

    private val file: Unit
        get() {
            val sparseArray = SparseIntArray()
            sparseArray.append(0, R.raw.caliandar36)
            sparseArray.append(1, R.raw.caliandar37)
            sparseArray.append(2, R.raw.caliandar38)
            sparseArray.append(3, R.raw.caliandar39)
            sparseArray.append(4, R.raw.caliandar40)
            sparseArray.append(5, R.raw.caliandar41)
            sparseArray.append(6, R.raw.caliandar42)
            sparseArray.append(7, R.raw.caliandar43)
            sparseArray.append(8, R.raw.caliandar44)
            sparseArray.append(9, R.raw.caliandar45)
            sparseArray.append(10, R.raw.caliandar46)
            sparseArray.append(11, R.raw.caliandar47)
            val builder = StringBuilder()
            for (i in 0 until sparseArray.size()) {
                val inputStream = resources.openRawResource(sparseArray[i])
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var line: String
                reader.forEachLine {
                    line = it
                    if (sparseArray.keyAt(i) > 0) {
                        val t1 = line.indexOf("[")
                        line = line.substring(t1 + 1)
                    }
                    if (sparseArray.keyAt(i) < 11) {
                        val t1 = line.lastIndexOf("]")
                        line = line.substring(0, t1) + ","
                    }
                    builder.append(line).append("\n")
                }
                inputStream.close()
            }
            val gson = Gson()
            val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
            arrayLists = gson.fromJson(builder.toString(), type)
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
        title_toolbar.text = resources.getText(R.string.search_svityia)
        if (dzenNoch) {
            toolbar.popupTheme = R.style.AppCompatDark
            toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
            title_toolbar.setBackgroundResource(R.color.colorprimary_material_dark)
            title_toolbar.setTextColor(ContextCompat.getColor(this, R.color.colorIcons))
        } else {
            toolbar.setBackgroundResource(R.color.colorPrimary)
            title_toolbar.setBackgroundResource(R.color.colorPrimary)
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

    private fun rawAsset(poshukString: String) {
        var poshuk = poshukString
        val posukOrig = poshuk
        arrayRes.clear()
        adapter.notifyDataSetChanged()
        if (poshuk != "") {
            poshuk = poshuk.toLowerCase(Locale.getDefault())
            poshuk = poshuk.replace("ё", "е")
            poshuk = poshuk.replace("сві", "сьві")
            poshuk = poshuk.replace("свя", "сьвя")
            poshuk = poshuk.replace("зме", "зьме")
            poshuk = poshuk.replace("змі", "зьмі")
            poshuk = poshuk.replace("змя", "зьмя")
            poshuk = poshuk.replace("зня", "зьня")
            poshuk = poshuk.replace("сле", "сьле")
            poshuk = poshuk.replace("слі", "сьлі")
            poshuk = poshuk.replace("сль", "сьль")
            poshuk = poshuk.replace("слю", "сьлю")
            poshuk = poshuk.replace("сля", "сьля")
            poshuk = poshuk.replace("сне", "сьне")
            poshuk = poshuk.replace("сні", "сьні")
            poshuk = poshuk.replace("сню", "сьню")
            poshuk = poshuk.replace("сня", "сьня")
            poshuk = poshuk.replace("спе", "сьпе")
            poshuk = poshuk.replace("спі", "сьпі")
            poshuk = poshuk.replace("спя", "сьпя")
            poshuk = poshuk.replace("сце", "сьце")
            poshuk = poshuk.replace("сці", "сьці")
            poshuk = poshuk.replace("сць", "сьць")
            poshuk = poshuk.replace("сцю", "сьцю")
            poshuk = poshuk.replace("сця", "сьця")
            poshuk = poshuk.replace("цце", "цьце")
            poshuk = poshuk.replace("цці", "цьці")
            poshuk = poshuk.replace("ццю", "цьцю")
            poshuk = poshuk.replace("ззе", "зьзе")
            poshuk = poshuk.replace("ззі", "зьзі")
            poshuk = poshuk.replace("ззю", "зьзю")
            poshuk = poshuk.replace("ззя", "зьзя")
            poshuk = poshuk.replace("зле", "зьле")
            poshuk = poshuk.replace("злі", "зьлі")
            poshuk = poshuk.replace("злю", "зьлю")
            poshuk = poshuk.replace("зля", "зьля")
            poshuk = poshuk.replace("збе", "зьбе")
            poshuk = poshuk.replace("збі", "зьбі")
            poshuk = poshuk.replace("збя", "зьбя")
            poshuk = poshuk.replace("нне", "ньне")
            poshuk = poshuk.replace("нні", "ньні")
            poshuk = poshuk.replace("нню", "ньню")
            poshuk = poshuk.replace("ння", "ньня")
            poshuk = poshuk.replace("лле", "льле")
            poshuk = poshuk.replace("ллі", "льлі")
            poshuk = poshuk.replace("ллю", "льлю")
            poshuk = poshuk.replace("лля", "льля")
            poshuk = poshuk.replace("дск", "дзк")
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ў', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk.length - 1
                if (r >= 3) {
                    if (poshuk[r] == aM) {
                        poshuk = poshuk.replace(poshuk, poshuk.substring(0, r))
                    }
                }
            }
            var color = "<font color=#d00505>"
            if (dzenNoch) color = "<font color=#f44336>"
            for (e in arrayLists.indices) {
                val sviatyia: Array<String> = arrayLists[e][4].split("<br>").toTypedArray()
                for (aSviatyia in sviatyia) {
                    if (aSviatyia.toLowerCase(Locale.getDefault()).replace("ё", "е").contains(poshuk.toLowerCase(Locale.getDefault()))) {
                        var bSviatyia = aSviatyia
                        bSviatyia = bSviatyia.replace("<font color=#d00505>", "")
                        bSviatyia = bSviatyia.replace("</font>", "")
                        bSviatyia = bSviatyia.replace("<strong>", "")
                        bSviatyia = bSviatyia.replace("</strong>", "")
                        val t1 = bSviatyia.toLowerCase(Locale.getDefault()).replace("ё", "е").indexOf(poshuk.toLowerCase(Locale.getDefault()))
                        val t2 = poshuk.toLowerCase(Locale.getDefault()).length
                        bSviatyia = bSviatyia.substring(0, t1) + color + bSviatyia.substring(t1, t1 + t2) + "</font>" + bSviatyia.substring(t1 + t2)
                        val g = GregorianCalendar(arrayLists[e][3].toInt(), arrayLists[e][2].toInt(), arrayLists[e][1].toInt())
                        val res = "<!--" + g[Calendar.DAY_OF_MONTH] + ":" + g[Calendar.MONTH] + "--><em>" + arrayLists[e][1] + " " + munName[arrayLists[e][2].toInt()] + "</em><br>" + bSviatyia
                        arrayRes.add(res)
                    }
                }
            }
            for (e in MenuCviaty.opisanie.indices) {
                if (MenuCviaty.opisanie[e].toLowerCase(Locale.getDefault()).replace("ё", "е").contains(poshuk.toLowerCase(Locale.getDefault()))) {
                    var result = MenuCviaty.opisanie[e]
                    if (result.contains("<font color=")) {
                        val t1 = result.indexOf("<font")
                        val t2 = result.indexOf("\">")
                        val t3 = result.indexOf("</font>")
                        result = result.substring(0, t1) + result.substring(t2 + 2, t3)
                    }
                    val t1 = result.indexOf("<!--")
                    val t2 = result.indexOf(":")
                    val t3 = result.indexOf("-->")
                    var bold = ""
                    var boldEnd = ""
                    var em = ""
                    var emEnd = ""
                    val t6 = result.indexOf("<!--", t3)
                    if (t6 != -1) {
                        val t7 = result.indexOf("-->", t6)
                        val res1 = result.substring(t6 + 4, t7)
                        if (res1.contains("1")) {
                            bold = "$color<strong>"
                            boldEnd = "</strong></font>"
                        }
                        if (res1.contains("2")) {
                            bold = color
                            boldEnd = "</font>"
                        }
                        if (res1.contains("3")) {
                            em = "<em>"
                            emEnd = "</em>"
                        }
                    }
                    val g = GregorianCalendar(c[Calendar.YEAR], result.substring(t2 + 1, t3).toInt(), result.substring(t1 + 4, t2).toInt())
                    var aSviatyia = result.substring(t3 + 3)
                    val t4 = aSviatyia.toLowerCase(Locale.getDefault()).replace("ё", "е").indexOf(poshuk.toLowerCase(Locale.getDefault()))
                    val t5 = poshuk.toLowerCase(Locale.getDefault()).length
                    aSviatyia = aSviatyia.substring(0, t4) + color + aSviatyia.substring(t4, t4 + t5) + "</font>" + aSviatyia.substring(t4 + t5)
                    val res = result.substring(t1, t3 + 3) + "<em>" + bold + g[Calendar.DATE] + " " + munName[g[Calendar.MONTH]] + "</em>" + boldEnd + "<br>" + em + aSviatyia + emEnd
                    arrayRes.add(res)
                }
            }
        }
        TextView.text = resources.getString(R.string.seash, arrayRes.size)
        val gson = Gson()
        val json = gson.toJson(arrayRes)
        val prefEditors = chin.edit()
        prefEditors.putString("search_svityx_array", json)
        prefEditors.putString("search_svityx_string", posukOrig)
        prefEditors.apply()
        adapter.notifyDataSetChanged()
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
                    arrayRes.clear()
                    adapter.notifyDataSetChanged()
                    TextView.text = resources.getString(R.string.seash, 0)
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

    private class SearchListAdapter(private val mContext: Activity, private val adapterList: ArrayList<String>) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_2, R.id.label, adapterList as List<String>) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text?.text = MainActivity.fromHtml(adapterList[position])
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}