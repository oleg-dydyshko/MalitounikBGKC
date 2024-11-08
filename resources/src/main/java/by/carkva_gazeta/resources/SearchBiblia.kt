package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogClearHishory
import by.carkva_gazeta.malitounik.HistoryAdapter
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.VybranoeBibleList
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.resources.databinding.SearchBibliaBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class SearchBiblia : BaseActivity(), DialogClearHishory.DialogClearHistoryListener, DialogBibleSearshSettings.DiallogBibleSearshListiner, BibliaPerakvadBokuna, BibliaPerakvadCarniauski, BibliaPerakvadNadsana, BibliaPerakvadSemuxi, BibliaPerakvadSinaidal {
    private lateinit var adapter: SearchBibliaActivityAdaprer
    private lateinit var prefEditors: Editor
    private lateinit var chin: SharedPreferences
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var history = ArrayList<String>()
    private lateinit var historyAdapter: HistoryAdapter
    private var fierstPosition = 0
    private lateinit var binding: SearchBibliaBinding
    private var edittext2Focus = false
    private var title = ""
    private var searchJob: Job? = null
    private var histiryJob: Job? = null
    private var perevod = VybranoeBibleList.PEREVODSEMUXI

    override fun addZakladka(color: Int, knigaBible: String, bible: String) {
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.addZakladka(color, knigaBible, bible)
        }
    }

    override fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getFileZavet(novyZapaviet, kniga)
            else -> File("")
        }
    }

    override fun getNamePerevod(): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getNamePerevod()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getNamePerevod()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getNamePerevod()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getNamePerevod()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getNamePerevod()
            else -> ""
        }
    }

    override fun getZakladki(): ArrayList<BibleZakladkiData> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getZakladki()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getZakladki()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getZakladki()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getZakladki()
            else -> ArrayList()
        }
    }

    override fun getTitlePerevod(): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getTitlePerevod()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getTitlePerevod()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getTitlePerevod()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getTitlePerevod()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getTitlePerevod()
            else -> ""
        }
    }

    override fun getSubTitlePerevod(glava: Int): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSubTitlePerevod(0)
            else -> ""
        }
    }

    override fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSpisKnig(novyZapaviet)
            else -> arrayOf("")
        }
    }

    override fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getInputStream(novyZapaviet, kniga)
            else -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
        }
    }

    override fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.saveVydelenieZakladkiNtanki(glava, stix)
        }
    }

    override fun translatePsaltyr(psalm: Int, styx: Int, isUpdate: Boolean): Array<Int> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.translatePsaltyr(psalm, styx, isUpdate)
            else -> arrayOf(1, 1)
        }
    }

    override fun isPsaltyrGreek(): Boolean {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.isPsaltyrGreek()
            else -> true
        }
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
        histiryJob?.cancel()
        prefEditors.putString("search_string_filter", binding.editText2.text.toString())
        prefEditors.putInt("search_bible_fierstPosition", fierstPosition)
        prefEditors.apply()
    }

    private fun loadHistory() {
        history.clear()
        var biblia = "semuxa"
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_semuxa)
                biblia = "semuxa"
            }
            VybranoeBibleList.PEREVODSINOIDAL -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_sinoidal)
                biblia = "sinoidal"
            }
            VybranoeBibleList.PEREVODNADSAN -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_nadsan)
                biblia = "nadsan"
            }
            VybranoeBibleList.PEREVODBOKUNA -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_bokun)
                biblia = "bokuna"
            }
            VybranoeBibleList.PEREVODCARNIAUSKI -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_charniauski)
                biblia = "carniauski"
            }
        }
        if (chin.getString("history_bible_$biblia", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_bible_$biblia", "")
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
            history.addAll(gson.fromJson(json, type))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        prefEditors = chin.edit()
        binding = SearchBibliaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.filterGrup.visibility = View.VISIBLE
        binding.buttonx2.setOnClickListener {
            binding.editText2.text?.clear()
        }
        DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.buttonx2.setImageResource(by.carkva_gazeta.malitounik.R.drawable.cancel)
        }
        if ((intent.getStringExtra("perevod") ?: VybranoeBibleList.PEREVODSEMUXI) != perevod) {
            prefEditors.putString("search_string", "")
            prefEditors.putString("search_string_filter", "")
            prefEditors.apply()
        }
        perevod = intent.getStringExtra("perevod") ?: VybranoeBibleList.PEREVODSEMUXI
        loadHistory()
        adapter = SearchBibliaActivityAdaprer(this, ArrayList())
        binding.ListView.adapter = adapter
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                fierstPosition = absListView.firstVisiblePosition
                if (i == 1) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
                    searchView?.clearFocus()
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
        if (chin.getString("search_string", "") != "") {
            if (chin.getString("search_array", "") != "") {
                val gson = Gson()
                val json = chin.getString("search_array", "")
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
                val arrayList = ArrayList<String>()
                val arraySpan = ArrayList<Spannable>()
                arrayList.addAll(gson.fromJson(json, type))
                arrayList.forEach {
                    val p = it
                    val t2 = p.indexOf("<p")
                    val t3 = p.indexOf(">", t2)
                    val subP = p.substring(t2, t3 + 1)
                    var str = p.replace(subP, "")
                    str = str.replace("</p>", "<br>")
                    val t1 = str.lastIndexOf("<br>")
                    val span = MainActivity.fromHtml(str.substring(0, t1)) as Spannable
                    arraySpan.add(span)
                }
                adapter.addAll(arraySpan)
            }
        }
        binding.editText2.setText(chin.getString("search_string_filter", ""))
        binding.editText2.addTextChangedListener(MyTextWatcher(binding.editText2, true))
        binding.ListView.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            var strText = adapterView.adapter.getItem(position).toString()
            val t5 = strText.indexOf("\n")
            strText = strText.substring(0, t5)
            var nazva = 0
            var nazvaS = -1
            if (perevod == VybranoeBibleList.PEREVODSEMUXI) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
                for (i in lists.indices) {
                    val t4 = lists[i].indexOf("#")
                    if (strText.contains(lists[i].substring(0, t4))) nazvaS = i
                }
                for (i in listn.indices) {
                    val t4 = listn[i].indexOf("#")
                    if (strText.contains(listn[i].substring(0, t4))) nazva = i
                }
            }
            if (perevod == VybranoeBibleList.PEREVODSINOIDAL) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln)
                for (i in lists.indices) {
                    val t4 = lists[i].indexOf("#")
                    if (strText.contains(lists[i].substring(0, t4))) nazvaS = i
                }
                for (i in listn.indices) {
                    val t4 = listn[i].indexOf("#")
                    if (strText.contains(listn[i].substring(0, t4))) nazva = i
                }
            }
            if (perevod == VybranoeBibleList.PEREVODBOKUNA) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan)
                for (i in lists.indices) {
                    val t4 = lists[i].indexOf("#")
                    if (strText.contains(lists[i].substring(0, t4))) nazvaS = i
                }
                for (i in listn.indices) {
                    val t4 = listn[i].indexOf("#")
                    if (strText.contains(listn[i].substring(0, t4))) nazva = i
                }
            }
            if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskis)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskin)
                for (i in lists.indices) {
                    val t4 = lists[i].indexOf("#")
                    if (strText.contains(lists[i].substring(0, t4))) nazvaS = i
                }
                for (i in listn.indices) {
                    val t4 = listn[i].indexOf("#")
                    if (strText.contains(listn[i].substring(0, t4))) nazva = i
                }
            }
            val str1 = strText.indexOf("glava.")
            val str2 = strText.indexOf("-->")
            val str3 = strText.indexOf("<!--stix.")
            val str4 = strText.indexOf("::")
            val glava = strText.substring(str1 + 6, str2).toInt()
            val stix = strText.substring(str3 + 9, str4).toInt()
            val intent = Intent(this@SearchBiblia, BibliaActivity::class.java)
            if (perevod == VybranoeBibleList.PEREVODNADSAN) {
                intent.putExtra("kniga", getString(by.carkva_gazeta.malitounik.R.string.psalter))
                intent.putExtra("novyZapavet", false)
            } else {
                if (nazvaS != -1) {
                    intent.putExtra("kniga", nazvaS)
                    intent.putExtra("novyZapavet", false)
                    prefEditors.putBoolean("novyzavet", false)
                } else {
                    intent.putExtra("kniga", nazva)
                    intent.putExtra("novyZapavet", true)
                    prefEditors.putBoolean("novyzavet", true)
                }
            }
            intent.putExtra("perevod", perevod)
            intent.putExtra("glava", glava - 1)
            intent.putExtra("stix", stix - 1)
            intent.putExtra("prodolzyt", true)
            prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
            prefEditors.apply()
            startActivity(intent)
        }
        historyAdapter = HistoryAdapter(this, history)
        if (dzenNoch) binding.History.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark)
        else binding.History.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        binding.History.adapter = historyAdapter
        binding.History.setOnItemClickListener { _, _, position, _ ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val edit = history[position]
            binding.History.visibility = View.GONE
            binding.ListView.visibility = View.VISIBLE
            prefEditors.putString("search_string", edit)
            prefEditors.apply()
            autoCompleteTextView?.setText(edit)
            execute(edit)
        }
        binding.History.setOnItemLongClickListener { _, _, position, _ ->
            val dialogClearHishory = DialogClearHishory.getInstance(position, history[position])
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return@setOnItemLongClickListener true
        }
        if (savedInstanceState != null) {
            val listView = savedInstanceState.getBoolean("list_view")
            if (listView) binding.ListView.visibility = View.VISIBLE
            fierstPosition = savedInstanceState.getInt("fierstPosition")
        } else {
            fierstPosition = chin.getInt("search_bible_fierstPosition", 0)
        }
        binding.ListView.setSelection(fierstPosition)
        binding.editText2.setOnFocusChangeListener { _, hasFocus ->
            edittext2Focus = hasFocus
        }
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun changeSearchViewElements(view: View?) {
        if (view == null) return
        if (view.id == androidx.appcompat.R.id.search_edit_frame || view.id == androidx.appcompat.R.id.search_mag_icon) {
            val p = view.layoutParams as LinearLayout.LayoutParams
            p.leftMargin = 0
            p.rightMargin = 0
            view.layoutParams = p
        } else if (view.id == androidx.appcompat.R.id.search_src_text) {
            autoCompleteTextView = view as? AutoCompleteTextView
            autoCompleteTextView?.let {
                it.imeOptions = EditorInfo.IME_ACTION_DONE
                it.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
                it.addTextChangedListener(MyTextWatcher(it))
                it.setText(chin.getString("search_string", ""))
                it.setSelection(it.text.length)
                it.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(it.windowToken, 0)
                    }
                    true
                }
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun setSettingsPegistrbukv(pegistrbukv: Boolean) {
        autoCompleteTextView?.let {
            val edit = it.text.toString()
            execute(edit, true)
        }
    }

    override fun setSettingsSlovocalkam(slovocalkam: Int) {
        autoCompleteTextView?.let {
            val edit = it.text.toString()
            execute(edit, true)
        }
    }

    override fun setSettingsBibliaSeash(position: Int) {
        if (perevod != VybranoeBibleList.PEREVODNADSAN) {
            autoCompleteTextView?.let {
                val edit = it.text.toString()
                val bibliaSeash = chin.getInt("biblia_seash", 0)
                if (chin.getString("search_string", "") != edit || bibliaSeash != position) {
                    prefEditors.putInt("biblia_seash", position)
                    prefEditors.apply()
                    execute(edit, true)
                }
            }
        }
    }

    override fun setBiblePeraklad(peraklad: String) {
        perevod = peraklad
        loadHistory()
        autoCompleteTextView?.let {
            val edit = it.text.toString()
            execute(edit, true)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.search_biblia, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(by.carkva_gazeta.malitounik.R.id.search)
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                onBack()
                return false
            }
        })
        searchViewItem.expandActionView()
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = title
        val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searcheTextView?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount = menu.findItem(by.carkva_gazeta.malitounik.R.id.count).actionView as TextView
        textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        val closeButton = searchView?.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            prefEditors.putString("search_string", "")
            prefEditors.apply()
            searchJob?.cancel()
            searchView?.setQuery("", false)
            onPostExecute(ArrayList())
        }
        changeSearchViewElements(searchView)
    }

    override fun onBack() {
        val intent = Intent()
        intent.putExtra("perevod", perevod)
        setResult(500, intent)
        super.onBack()
    }

    override fun onPrepareMenu(menu: Menu) {
        val histopy = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_clean_histopy)
        histopy.isVisible = history.size != 0
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_search_bible) {
            val dialogSearshBible = DialogBibleSearshSettings.getInstance(perevod)
           dialogSearshBible.show(supportFragmentManager, "dialogSearshBible")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_clean_histopy) {
            val dialogClearHishory = DialogClearHishory.getInstance()
            dialogClearHishory.show(supportFragmentManager, "dialogClearHishory")
            return true
        }
        return false
    }

    private fun addHistory(item: String) {
        if (histiryJob?.isActive == true) {
            histiryJob?.cancel()
        }
        histiryJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)
            val temp = ArrayList<String>()
            for (i in 0 until history.size) {
                if (history[i] != item) temp.add(history[i])
            }
            history.clear()
            history.add(item)
            for (i in 0 until temp.size) {
                history.add(temp[i])
                if (history.size == 15) break
            }
            if (history.size == 1) invalidateOptionsMenu()
            historyAdapter.notifyDataSetChanged()
            saveHistory()
        }
    }

    private fun saveHistory() {
        var biblia = "semuxa"
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> biblia = "semuxa"
            VybranoeBibleList.PEREVODSINOIDAL -> biblia = "sinoidal"
            VybranoeBibleList.PEREVODNADSAN -> biblia = "nadsan"
            VybranoeBibleList.PEREVODBOKUNA -> biblia = "bokuna"
            VybranoeBibleList.PEREVODCARNIAUSKI -> biblia = "carniauski"
        }
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(history, type)
        prefEditors.putString("history_bible_$biblia", json)
        prefEditors.apply()
    }

    override fun cleanFullHistory() {
        history.clear()
        saveHistory()
        invalidateOptionsMenu()
        historyAdapter.notifyDataSetChanged()
    }

    override fun cleanHistory(position: Int) {
        history.removeAt(position)
        saveHistory()
        if (history.size == 0) {
            invalidateOptionsMenu()
        }
        historyAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("list_view", binding.ListView.visibility == View.VISIBLE)
        outState.putInt("fierstPosition", fierstPosition)
        autoCompleteTextView?.let {
            prefEditors.putString("search_string", it.text.toString())
            prefEditors.apply()
        }
    }

    private fun execute(searcheString: String, run: Boolean = false) {
        if (searcheString.length >= 3) {
            searchJob?.cancel()
            if (adapter.count == 0 || (history.isNotEmpty() && searcheString != history[0]) || run) {
                binding.History.visibility = View.GONE
                binding.ListView.visibility = View.VISIBLE
                if (searchJob?.isActive == true) {
                    searchJob?.cancel()
                }
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    onPreExecute()
                    val result = withContext(Dispatchers.IO) {
                        return@withContext doInBackground(searcheString)
                    }
                    onPostExecute(result)
                }
            }
        }
    }

    private fun onPreExecute() {
        prefEditors = chin.edit()
        adapter.clear()
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, 0)
        binding.progressBar.visibility = View.VISIBLE
        binding.History.visibility = View.GONE
        binding.ListView.visibility = View.GONE
        autoCompleteTextView?.let {
            val edit = it.text.toString()
            if (edit != "") {
                prefEditors.putString("search_string", edit)
                prefEditors.apply()
            }
        }
    }

    private fun doInBackground(searche: String): ArrayList<Spannable> {
        var list = biblia(searche, perevod)
        if (list.isEmpty() && chin.getInt("slovocalkam", 0) == 0) {
            list = biblia(searche, perevod, true)
        }
        return list
    }

    private fun onPostExecute(result: ArrayList<Spannable>) {
        adapter.clear()
        adapter.addAll(result)
        adapter.filter.filter(binding.editText2.text.toString())
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        if (chin.getString("search_string", "") != "") {
            binding.ListView.post { binding.ListView.setSelection(chin.getInt("search_position", 0)) }
        }
        binding.progressBar.visibility = View.GONE
        val arrayList = ArrayList<String>()
        result.forEach {
            arrayList.add(MainActivity.toHtml(it))
        }
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(arrayList, type)
        prefEditors.putString("search_array", json)
        prefEditors.apply()
        val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        val search = searcheTextView?.text.toString()
        if (search != "" && result.size != 0) {
            binding.ListView.visibility = View.VISIBLE
            addHistory(search)
        }
    }

    private fun biblia(poshuk: String, perevod: String, secondRun: Boolean = false): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        val registr = chin.getBoolean("pegistrbukv", true)
        if (secondRun) {
            val m = if (perevod == VybranoeBibleList.PEREVODSINOIDAL) charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
            else charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1.length >= 3) {
                    if (poshuk1[r] == aM && r >= 3) {
                        poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                    }
                }
            }
        }
        val rangeBibile = if (perevod == VybranoeBibleList.PEREVODNADSAN) 0..0
        else 0..1
        for (novyZapaviet in rangeBibile) {
            val list = if (novyZapaviet == 0) getSpisKnig(false)
            else getSpisKnig(true)
            val range = when (chin.getInt("biblia_seash", 0)) {
                1 -> {
                    if (novyZapaviet == 0) continue
                    0 until 4
                }
                2 -> {
                    if (novyZapaviet == 0) continue
                    0 until getSpisKnig(true).size
                }
                3 -> {
                    if (novyZapaviet == 1) continue
                    0 until 4
                }
                4 -> {
                    if (novyZapaviet == 1) continue
                    0 until getSpisKnig(false).size
                }
                else -> {
                    if (novyZapaviet == 0) 0 until getSpisKnig(false).size
                    else 0 until getSpisKnig(true).size
                }
            }
            for (i in range) {
                if (searchJob?.isActive == false) break
                val t4 = list[i].indexOf("#")
                val nazva = list[i].substring(0, t4)
                val inputStream = getInputStream(novyZapaviet == 1, i)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var glava = 0
                val split = reader.use {
                    it.readText().split("===")
                }
                for (e in 1 until split.size) {
                    glava++
                    val bibleline = split[e].split("\n")
                    var stix = 0
                    for (r in 1 until bibleline.size) {
                        stix++
                        val aSviatyia = MainActivity.fromHtml(bibleline[r]).toString()
                        val title = "<!--stix.$stix::glava.$glava-->$nazva Гл. $glava\n"
                        val t3 = title.length
                        val span = SpannableStringBuilder()
                        val poshuk2 = findChars(poshuk1, aSviatyia)
                        if (poshuk2.isEmpty()) continue
                        span.append(title)
                        span.setSpan(StyleSpan(Typeface.BOLD), 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        span.append(aSviatyia)
                        for (w in 0 until poshuk2.size) {
                            val t2 = poshuk2[w].str.length
                            val t1 = poshuk2[w].position + t3
                            span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), t1 - t2, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), t1 - t2, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        seashpost.add(span)
                    }
                }
            }
        }
        return seashpost
    }

    private fun findChars(search: String, textSearch: String): ArrayList<FindString> {
        val registr = chin.getBoolean("pegistrbukv", true)
        val stringBuilder = StringBuilder()
        var strSub = 0
        val list = search.toCharArray()
        val result = ArrayList<FindString>()
        while (true) {
            val strSub1Pos = textSearch.indexOf(list[0], strSub, registr)
            if (strSub1Pos != -1) {
                strSub = strSub1Pos + 1
                val subChar2 = StringBuilder()
                for (i in 1 until list.size) {
                    if (textSearch.length >= strSub + 1) {
                        if (list[i].isLetterOrDigit()) {
                            var subChar = textSearch.substring(strSub, strSub + 1)
                            if (subChar == "́") {
                                stringBuilder.append(list[i])
                                strSub++
                                if (textSearch.length >= strSub + 1) {
                                    subChar = textSearch.substring(strSub, strSub + 1)
                                }
                            }
                            val strSub2Pos = subChar.indexOf(list[i], ignoreCase = registr)
                            if (strSub2Pos != -1) {
                                if (stringBuilder.isEmpty()) stringBuilder.append(textSearch.substring(strSub1Pos, strSub1Pos + 1))
                                if (subChar2.isNotEmpty()) stringBuilder.append(subChar2.toString())
                                stringBuilder.append(list[i])
                                subChar2.clear()
                                strSub++
                            } else {
                                stringBuilder.clear()
                                break
                            }
                        } else {
                            while (true) {
                                if (textSearch.length >= strSub + 1) {
                                    val subChar = textSearch.substring(strSub, strSub + 1).toCharArray()
                                    if (!subChar[0].isLetterOrDigit()) {
                                        subChar2.append(subChar[0])
                                        strSub++
                                    } else {
                                        if (list.size - 1 == i) {
                                            stringBuilder.append(list[i])
                                        }
                                        break
                                    }
                                } else {
                                    break
                                }
                            }
                            if (subChar2.isEmpty()) {
                                strSub++
                                stringBuilder.clear()
                                break
                            }
                        }
                    } else {
                        stringBuilder.clear()
                        break
                    }
                }
                if (stringBuilder.toString().isNotEmpty()) {
                    if (chin.getInt("slovocalkam", 0) == 1) {
                        val startString = if (strSub1Pos > 0) textSearch.substring(strSub1Pos - 1, strSub1Pos)
                        else " "
                        val endString = if (strSub1Pos + stringBuilder.length + 1 <= textSearch.length) textSearch.substring(strSub1Pos + stringBuilder.length, strSub1Pos + stringBuilder.length + 1)
                        else " "
                        if (!startString.toCharArray()[0].isLetterOrDigit() && !endString.toCharArray()[0].isLetterOrDigit()) {
                            result.add(FindString(stringBuilder.toString(), strSub))
                            stringBuilder.clear()
                        }
                    } else {
                        result.add(FindString(stringBuilder.toString(), strSub))
                        stringBuilder.clear()
                    }
                }
            } else {
                break
            }
        }
        return result
    }

    private inner class MyTextWatcher(private val editText: EditText?, private val filtep: Boolean = false) : TextWatcher {
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
            var edit = s.toString()
            if (editch) {
                if (perevod == VybranoeBibleList.PEREVODSINOIDAL) {
                    edit = edit.replace("і", "и")
                    edit = edit.replace("ў", "щ")
                    edit = edit.replace("І", "И")
                    edit = edit.replace("Ў", "Щ")
                    edit = edit.replace("'", "ъ")
                } else {
                    edit = edit.replace("и", "і")
                    edit = edit.replace("щ", "ў")
                    edit = edit.replace("И", "І")
                    edit = edit.replace("Щ", "Ў")
                    edit = edit.replace("ъ", "'")
                }
                if (check != 0) {
                    editText?.removeTextChangedListener(this)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
                if (editText?.id == androidx.appcompat.R.id.search_src_text) {
                    histiryJob?.cancel()
                    if (searchJob?.isActive == true && editText.text.length < 3) {
                        searchJob?.cancel()
                        binding.progressBar.visibility = View.GONE
                    } else {
                        if (chin.getString("search_string", "") != edit) execute(edit)
                    }
                }
            }
            if (editText?.id == androidx.appcompat.R.id.search_src_text) {
                if (editText.text.length >= 3) {
                    binding.History.visibility = View.GONE
                    binding.ListView.visibility = View.VISIBLE
                } else {
                    binding.History.visibility = View.VISIBLE
                    binding.ListView.visibility = View.GONE
                }
            }
            if (filtep) adapter.filter.filter(edit)
        }
    }

    private inner class SearchBibliaActivityAdaprer(context: Activity, private val arrayList: ArrayList<Spannable>) : ArrayAdapter<Spannable>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, arrayList) {
        private val origData = ArrayList<Spannable>(arrayList)
        override fun addAll(collection: Collection<Spannable>) {
            super.addAll(collection)
            origData.clear()
            origData.addAll(collection)
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val t1 = arrayList[position].indexOf("-->")
            viewHolder.text.text = arrayList[position].subSequence(t1 + 3, arrayList[position].length)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(by.carkva_gazeta.malitounik.R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val result = FilterResults()
                    if (constraint.toString().isNotEmpty()) {
                        val founded = ArrayList<Spannable>()
                        for (item in origData) {
                            if (item.contains(constraint, true)) {
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

                override fun publishResults(constraint: CharSequence, results: FilterResults) {
                    clear()
                    for (item in results.values as ArrayList<*>) {
                        var t1 = (item as Spannable).indexOf("-->")
                        if (t1 == -1) t1 = 0
                        else t1 += 3
                        val itm = item.indexOf(constraint.toString(), t1, true)
                        val itmcount = constraint.toString().length
                        if (itm != -1) {
                            val span = SpannableString(item)
                            span.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), itm, itm + itmcount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), itm, itm + itmcount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            add(span)
                        } else {
                            add(item)
                        }
                    }
                    textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, results.count)
                }
            }
        }
    }

    private class ViewHolder(var text: TextView)

    private data class FindString(val str: String, val position: Int)
}