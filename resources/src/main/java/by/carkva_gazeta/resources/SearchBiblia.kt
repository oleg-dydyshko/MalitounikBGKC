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
import android.view.Gravity
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
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogClearHishory
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.HistoryAdapter
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.NadsanContent
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem4Binding
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
import java.io.InputStreamReader

class SearchBiblia : BaseActivity(), DialogClearHishory.DialogClearHistoryListener, DialogBibleSearshSettings.DiallogBibleSearshListiner {
    private lateinit var adapter: SearchBibliaListAdaprer
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
    private var sinodalBible = ArrayList<Int>()
    private var semuxaBible = ArrayList<Int>()
    private var bokunaBible = ArrayList<Int>()
    private var carniauskiBible = ArrayList<Int>()
    private lateinit var binding: SearchBibliaBinding
    private var keyword = false
    private var edittext2Focus = false
    private var title = ""
    private var searchJob: Job? = null
    private var histiryJob: Job? = null

    init {
        sinodalBible.add(R.raw.sinaidals1)
        sinodalBible.add(R.raw.sinaidals2)
        sinodalBible.add(R.raw.sinaidals3)
        sinodalBible.add(R.raw.sinaidals4)
        sinodalBible.add(R.raw.sinaidals5)
        sinodalBible.add(R.raw.sinaidals6)
        sinodalBible.add(R.raw.sinaidals7)
        sinodalBible.add(R.raw.sinaidals8)
        sinodalBible.add(R.raw.sinaidals9)
        sinodalBible.add(R.raw.sinaidals10)
        sinodalBible.add(R.raw.sinaidals11)
        sinodalBible.add(R.raw.sinaidals12)
        sinodalBible.add(R.raw.sinaidals13)
        sinodalBible.add(R.raw.sinaidals14)
        sinodalBible.add(R.raw.sinaidals15)
        sinodalBible.add(R.raw.sinaidals16)
        sinodalBible.add(R.raw.sinaidals17)
        sinodalBible.add(R.raw.sinaidals18)
        sinodalBible.add(R.raw.sinaidals19)
        sinodalBible.add(R.raw.sinaidals20)
        sinodalBible.add(R.raw.sinaidals21)
        sinodalBible.add(R.raw.sinaidals22)
        sinodalBible.add(R.raw.sinaidals23)
        sinodalBible.add(R.raw.sinaidals24)
        sinodalBible.add(R.raw.sinaidals25)
        sinodalBible.add(R.raw.sinaidals26)
        sinodalBible.add(R.raw.sinaidals27)
        sinodalBible.add(R.raw.sinaidals28)
        sinodalBible.add(R.raw.sinaidals29)
        sinodalBible.add(R.raw.sinaidals30)
        sinodalBible.add(R.raw.sinaidals31)
        sinodalBible.add(R.raw.sinaidals32)
        sinodalBible.add(R.raw.sinaidals33)
        sinodalBible.add(R.raw.sinaidals34)
        sinodalBible.add(R.raw.sinaidals35)
        sinodalBible.add(R.raw.sinaidals36)
        sinodalBible.add(R.raw.sinaidals37)
        sinodalBible.add(R.raw.sinaidals38)
        sinodalBible.add(R.raw.sinaidals39)
        sinodalBible.add(R.raw.sinaidals40)
        sinodalBible.add(R.raw.sinaidals41)
        sinodalBible.add(R.raw.sinaidals42)
        sinodalBible.add(R.raw.sinaidals43)
        sinodalBible.add(R.raw.sinaidals44)
        sinodalBible.add(R.raw.sinaidals45)
        sinodalBible.add(R.raw.sinaidals46)
        sinodalBible.add(R.raw.sinaidals47)
        sinodalBible.add(R.raw.sinaidals48)
        sinodalBible.add(R.raw.sinaidals49)
        sinodalBible.add(R.raw.sinaidals50)
        sinodalBible.add(R.raw.sinaidaln1)
        sinodalBible.add(R.raw.sinaidaln2)
        sinodalBible.add(R.raw.sinaidaln3)
        sinodalBible.add(R.raw.sinaidaln4)
        sinodalBible.add(R.raw.sinaidaln5)
        sinodalBible.add(R.raw.sinaidaln6)
        sinodalBible.add(R.raw.sinaidaln7)
        sinodalBible.add(R.raw.sinaidaln8)
        sinodalBible.add(R.raw.sinaidaln9)
        sinodalBible.add(R.raw.sinaidaln10)
        sinodalBible.add(R.raw.sinaidaln11)
        sinodalBible.add(R.raw.sinaidaln12)
        sinodalBible.add(R.raw.sinaidaln13)
        sinodalBible.add(R.raw.sinaidaln14)
        sinodalBible.add(R.raw.sinaidaln15)
        sinodalBible.add(R.raw.sinaidaln16)
        sinodalBible.add(R.raw.sinaidaln17)
        sinodalBible.add(R.raw.sinaidaln18)
        sinodalBible.add(R.raw.sinaidaln19)
        sinodalBible.add(R.raw.sinaidaln20)
        sinodalBible.add(R.raw.sinaidaln21)
        sinodalBible.add(R.raw.sinaidaln22)
        sinodalBible.add(R.raw.sinaidaln23)
        sinodalBible.add(R.raw.sinaidaln24)
        sinodalBible.add(R.raw.sinaidaln25)
        sinodalBible.add(R.raw.sinaidaln26)
        sinodalBible.add(R.raw.sinaidaln27)

        semuxaBible.add(R.raw.biblias1)
        semuxaBible.add(R.raw.biblias2)
        semuxaBible.add(R.raw.biblias3)
        semuxaBible.add(R.raw.biblias4)
        semuxaBible.add(R.raw.biblias5)
        semuxaBible.add(R.raw.biblias6)
        semuxaBible.add(R.raw.biblias7)
        semuxaBible.add(R.raw.biblias8)
        semuxaBible.add(R.raw.biblias9)
        semuxaBible.add(R.raw.biblias10)
        semuxaBible.add(R.raw.biblias11)
        semuxaBible.add(R.raw.biblias12)
        semuxaBible.add(R.raw.biblias13)
        semuxaBible.add(R.raw.biblias14)
        semuxaBible.add(R.raw.biblias15)
        semuxaBible.add(R.raw.biblias16)
        semuxaBible.add(R.raw.biblias17)
        semuxaBible.add(R.raw.biblias18)
        semuxaBible.add(R.raw.biblias19)
        semuxaBible.add(R.raw.biblias20)
        semuxaBible.add(R.raw.biblias21)
        semuxaBible.add(R.raw.biblias22)
        semuxaBible.add(R.raw.biblias23)
        semuxaBible.add(R.raw.biblias24)
        semuxaBible.add(R.raw.biblias25)
        semuxaBible.add(R.raw.biblias26)
        semuxaBible.add(R.raw.biblias27)
        semuxaBible.add(R.raw.biblias28)
        semuxaBible.add(R.raw.biblias29)
        semuxaBible.add(R.raw.biblias30)
        semuxaBible.add(R.raw.biblias31)
        semuxaBible.add(R.raw.biblias32)
        semuxaBible.add(R.raw.biblias33)
        semuxaBible.add(R.raw.biblias34)
        semuxaBible.add(R.raw.biblias35)
        semuxaBible.add(R.raw.biblias36)
        semuxaBible.add(R.raw.biblias37)
        semuxaBible.add(R.raw.biblias38)
        semuxaBible.add(R.raw.biblias39)
        semuxaBible.add(R.raw.biblian1)
        semuxaBible.add(R.raw.biblian2)
        semuxaBible.add(R.raw.biblian3)
        semuxaBible.add(R.raw.biblian4)
        semuxaBible.add(R.raw.biblian5)
        semuxaBible.add(R.raw.biblian6)
        semuxaBible.add(R.raw.biblian7)
        semuxaBible.add(R.raw.biblian8)
        semuxaBible.add(R.raw.biblian9)
        semuxaBible.add(R.raw.biblian10)
        semuxaBible.add(R.raw.biblian11)
        semuxaBible.add(R.raw.biblian12)
        semuxaBible.add(R.raw.biblian13)
        semuxaBible.add(R.raw.biblian14)
        semuxaBible.add(R.raw.biblian15)
        semuxaBible.add(R.raw.biblian16)
        semuxaBible.add(R.raw.biblian17)
        semuxaBible.add(R.raw.biblian18)
        semuxaBible.add(R.raw.biblian19)
        semuxaBible.add(R.raw.biblian20)
        semuxaBible.add(R.raw.biblian21)
        semuxaBible.add(R.raw.biblian22)
        semuxaBible.add(R.raw.biblian23)
        semuxaBible.add(R.raw.biblian24)
        semuxaBible.add(R.raw.biblian25)
        semuxaBible.add(R.raw.biblian26)
        semuxaBible.add(R.raw.biblian27)

        bokunaBible.add(R.raw.bokunas1)
        bokunaBible.add(R.raw.bokunas2)
        bokunaBible.add(R.raw.bokunas3)
        bokunaBible.add(R.raw.bokunas4)
        bokunaBible.add(R.raw.bokunas5)
        bokunaBible.add(R.raw.bokunas6)
        bokunaBible.add(R.raw.bokunas7)
        bokunaBible.add(R.raw.bokunas8)
        bokunaBible.add(R.raw.bokunas9)
        bokunaBible.add(R.raw.bokunas10)
        bokunaBible.add(R.raw.bokunas11)
        bokunaBible.add(R.raw.bokunas12)
        bokunaBible.add(R.raw.bokunas13)
        bokunaBible.add(R.raw.bokunas14)
        bokunaBible.add(R.raw.bokunas15)
        bokunaBible.add(R.raw.bokunas16)
        bokunaBible.add(R.raw.bokunas17)
        bokunaBible.add(R.raw.bokunas18)
        bokunaBible.add(R.raw.bokunas19)
        bokunaBible.add(R.raw.bokunas20)
        bokunaBible.add(R.raw.bokunas21)
        bokunaBible.add(R.raw.bokunas22)
        bokunaBible.add(R.raw.bokunas23)
        bokunaBible.add(R.raw.bokunas24)
        bokunaBible.add(R.raw.bokunas25)
        bokunaBible.add(R.raw.bokunas26)
        bokunaBible.add(R.raw.bokunas27)
        bokunaBible.add(R.raw.bokunas28)
        bokunaBible.add(R.raw.bokunas29)
        bokunaBible.add(R.raw.bokunas30)
        bokunaBible.add(R.raw.bokunas31)
        bokunaBible.add(R.raw.bokunas32)
        bokunaBible.add(R.raw.bokunas33)
        bokunaBible.add(R.raw.bokunas34)
        bokunaBible.add(R.raw.bokunas35)
        bokunaBible.add(R.raw.bokunas36)
        bokunaBible.add(R.raw.bokunas37)
        bokunaBible.add(R.raw.bokunas38)
        bokunaBible.add(R.raw.bokunas39)
        bokunaBible.add(R.raw.bokunan1)
        bokunaBible.add(R.raw.bokunan2)
        bokunaBible.add(R.raw.bokunan3)
        bokunaBible.add(R.raw.bokunan4)
        bokunaBible.add(R.raw.bokunan5)
        bokunaBible.add(R.raw.bokunan6)
        bokunaBible.add(R.raw.bokunan7)
        bokunaBible.add(R.raw.bokunan8)
        bokunaBible.add(R.raw.bokunan9)
        bokunaBible.add(R.raw.bokunan10)
        bokunaBible.add(R.raw.bokunan11)
        bokunaBible.add(R.raw.bokunan12)
        bokunaBible.add(R.raw.bokunan13)
        bokunaBible.add(R.raw.bokunan14)
        bokunaBible.add(R.raw.bokunan15)
        bokunaBible.add(R.raw.bokunan16)
        bokunaBible.add(R.raw.bokunan17)
        bokunaBible.add(R.raw.bokunan18)
        bokunaBible.add(R.raw.bokunan19)
        bokunaBible.add(R.raw.bokunan20)
        bokunaBible.add(R.raw.bokunan21)
        bokunaBible.add(R.raw.bokunan22)
        bokunaBible.add(R.raw.bokunan23)
        bokunaBible.add(R.raw.bokunan24)
        bokunaBible.add(R.raw.bokunan25)
        bokunaBible.add(R.raw.bokunan26)
        bokunaBible.add(R.raw.bokunan27)

        carniauskiBible.add(R.raw.carniauskis1)
        carniauskiBible.add(R.raw.carniauskis2)
        carniauskiBible.add(R.raw.carniauskis3)
        carniauskiBible.add(R.raw.carniauskis4)
        carniauskiBible.add(R.raw.carniauskis5)
        carniauskiBible.add(R.raw.carniauskis6)
        carniauskiBible.add(R.raw.carniauskis7)
        carniauskiBible.add(R.raw.carniauskis8)
        carniauskiBible.add(R.raw.carniauskis9)
        carniauskiBible.add(R.raw.carniauskis10)
        carniauskiBible.add(R.raw.carniauskis11)
        carniauskiBible.add(R.raw.carniauskis12)
        carniauskiBible.add(R.raw.carniauskis13)
        carniauskiBible.add(R.raw.carniauskis14)
        carniauskiBible.add(R.raw.carniauskis15)
        carniauskiBible.add(R.raw.carniauskis16)
        carniauskiBible.add(R.raw.carniauskis17)
        carniauskiBible.add(R.raw.carniauskis18)
        carniauskiBible.add(R.raw.carniauskis19)
        carniauskiBible.add(R.raw.carniauskis20)
        carniauskiBible.add(R.raw.carniauskis21)
        carniauskiBible.add(R.raw.carniauskis22)
        carniauskiBible.add(R.raw.carniauskis23)
        carniauskiBible.add(R.raw.carniauskis24)
        carniauskiBible.add(R.raw.carniauskis25)
        carniauskiBible.add(R.raw.carniauskis26)
        carniauskiBible.add(R.raw.carniauskis27)
        carniauskiBible.add(R.raw.carniauskis28)
        carniauskiBible.add(R.raw.carniauskis29)
        carniauskiBible.add(R.raw.carniauskis30)
        carniauskiBible.add(R.raw.carniauskis31)
        carniauskiBible.add(R.raw.carniauskis32)
        carniauskiBible.add(R.raw.carniauskis33)
        carniauskiBible.add(R.raw.carniauskis34)
        carniauskiBible.add(R.raw.carniauskis35)
        carniauskiBible.add(R.raw.carniauskis36)
        carniauskiBible.add(R.raw.carniauskis37)
        carniauskiBible.add(R.raw.carniauskis38)
        carniauskiBible.add(R.raw.carniauskis39)
        carniauskiBible.add(R.raw.carniauskis40)
        carniauskiBible.add(R.raw.carniauskis41)
        carniauskiBible.add(R.raw.carniauskis42)
        carniauskiBible.add(R.raw.carniauskis43)
        carniauskiBible.add(R.raw.carniauskis44)
        carniauskiBible.add(R.raw.carniauskis45)
        carniauskiBible.add(R.raw.carniauskis46)
        carniauskiBible.add(R.raw.carniauskin1)
        carniauskiBible.add(R.raw.carniauskin2)
        carniauskiBible.add(R.raw.carniauskin3)
        carniauskiBible.add(R.raw.carniauskin4)
        carniauskiBible.add(R.raw.carniauskin5)
        carniauskiBible.add(R.raw.carniauskin6)
        carniauskiBible.add(R.raw.carniauskin7)
        carniauskiBible.add(R.raw.carniauskin8)
        carniauskiBible.add(R.raw.carniauskin9)
        carniauskiBible.add(R.raw.carniauskin10)
        carniauskiBible.add(R.raw.carniauskin11)
        carniauskiBible.add(R.raw.carniauskin12)
        carniauskiBible.add(R.raw.carniauskin13)
        carniauskiBible.add(R.raw.carniauskin14)
        carniauskiBible.add(R.raw.carniauskin15)
        carniauskiBible.add(R.raw.carniauskin16)
        carniauskiBible.add(R.raw.carniauskin17)
        carniauskiBible.add(R.raw.carniauskin18)
        carniauskiBible.add(R.raw.carniauskin19)
        carniauskiBible.add(R.raw.carniauskin20)
        carniauskiBible.add(R.raw.carniauskin21)
        carniauskiBible.add(R.raw.carniauskin22)
        carniauskiBible.add(R.raw.carniauskin23)
        carniauskiBible.add(R.raw.carniauskin24)
        carniauskiBible.add(R.raw.carniauskin25)
        carniauskiBible.add(R.raw.carniauskin26)
        carniauskiBible.add(R.raw.carniauskin27)
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
        histiryJob?.cancel()
        prefEditors.putString("search_string_filter", binding.editText2.text.toString())
        prefEditors.putInt("search_bible_fierstPosition", fierstPosition)
        prefEditors.apply()
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
        if ((intent.getStringExtra("perevod") ?: DialogVybranoeBibleList.PEREVODSEMUXI) != perevod) {
            prefEditors.putString("search_string", "")
            prefEditors.putString("search_string_filter", "")
            prefEditors.apply()
        }
        var biblia = "semuxa"
        perevod = intent.getStringExtra("perevod") ?: DialogVybranoeBibleList.PEREVODSEMUXI
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_semuxa)
                biblia = "semuxa"
            }
            DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_sinoidal)
                biblia = "sinoidal"
            }
            DialogVybranoeBibleList.PEREVODNADSAN -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_nadsan)
                biblia = "nadsan"
            }
            DialogVybranoeBibleList.PEREVODBOKUNA -> {
                title = getString(by.carkva_gazeta.malitounik.R.string.poshuk_bokun)
                biblia = "bokuna"
            }
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
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
        adapter = SearchBibliaListAdaprer(this, ArrayList())
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
            if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
                for (i in lists.indices) {
                    if (strText.contains(lists[i])) nazvaS = i
                }
                for (i in listn.indices) {
                    if (strText.contains(listn[i])) nazva = i
                }
            }
            if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln)
                for (i in lists.indices) {
                    if (strText.contains(lists[i])) nazvaS = i
                }
                for (i in listn.indices) {
                    if (strText.contains(listn[i])) nazva = i
                }
            }
            if (perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan)
                for (i in lists.indices) {
                    if (strText.contains(lists[i])) nazvaS = i
                }
                for (i in listn.indices) {
                    if (strText.contains(listn[i])) nazva = i
                }
            }
            if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                val lists = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskis)
                val listn = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskin)
                for (i in lists.indices) {
                    if (strText.contains(lists[i])) nazvaS = i
                }
                for (i in listn.indices) {
                    if (strText.contains(listn[i])) nazva = i
                }
            }
            val str1 = strText.indexOf("glava.")
            val str2 = strText.indexOf("-->")
            val str3 = strText.indexOf("<!--stix.")
            val str4 = strText.indexOf("::")
            val glava = strText.substring(str1 + 6, str2).toInt()
            val stix = strText.substring(str3 + 9, str4).toInt()
            if (perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
                val intent = Intent(this@SearchBiblia, NadsanContent::class.java)
                intent.putExtra("glava", glava - 1)
                intent.putExtra("stix", stix - 1)
                intent.putExtra("prodolzyt", true)
                prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                prefEditors.apply()
                startActivity(intent)
            } else {
                var intent = Intent(this@SearchBiblia, BibliaList::class.java)
                if (nazvaS != -1) {
                    if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODSEMUXI)
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODSINOIDAL)
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODBOKUNA)
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    }
                    intent.putExtra("kniga", nazvaS)
                    intent.putExtra("novyZapavet", false)
                    prefEditors.putBoolean("novyzavet", false)
                } else {
                    if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODSEMUXI)
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODSINOIDAL)
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODBOKUNA)
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                        intent = Intent(this@SearchBiblia, BibliaList::class.java)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    }
                    intent.putExtra("kniga", nazva)
                    intent.putExtra("novyZapavet", true)
                    prefEditors.putBoolean("novyzavet", true)
                }
                intent.putExtra("glava", glava - 1)
                intent.putExtra("stix", stix - 1)
                intent.putExtra("prodolzyt", true)
                prefEditors.putInt("search_position", binding.ListView.firstVisiblePosition)
                prefEditors.apply()
                startActivity(intent)
            }
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
        val data = if (perevod == DialogVybranoeBibleList.PEREVODNADSAN) arrayOf(getString(by.carkva_gazeta.malitounik.R.string.psalter))
        else resources.getStringArray(by.carkva_gazeta.malitounik.R.array.serche_bible)
        val arrayAdapter = SearchSpinnerAdapter(this, data)
        binding.spinner6.adapter = arrayAdapter
        if (perevod != DialogVybranoeBibleList.PEREVODNADSAN) {
            binding.spinner6.setSelection(chin.getInt("biblia_seash", 0))
            binding.spinner6.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setSettingsBibliaSeash(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        binding.constraint.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding.constraint.rootView.height - binding.constraint.height
            val keywordView = binding.constraint.rootView.height / 4
            keyword = heightDiff > keywordView
            settingsView()
        }
        binding.editText2.setOnFocusChangeListener { _, hasFocus ->
            edittext2Focus = hasFocus
            settingsView()
        }
        if (!chin.getBoolean("pegistrbukv", true)) binding.checkBox.isChecked = true
        binding.checkBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            prefEditors.putBoolean("pegistrbukv", !isChecked)
            prefEditors.apply()
            autoCompleteTextView?.let {
                val edit = it.text.toString()
                execute(edit, true)
            }
        }
        if (chin.getInt("slovocalkam", 0) == 1) binding.checkBox2.isChecked = true
        binding.checkBox2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditors.putInt("slovocalkam", 1)
            } else {
                prefEditors.putInt("slovocalkam", 0)
            }
            prefEditors.apply()
            autoCompleteTextView?.let {
                val edit = it.text.toString()
                execute(edit, true)
            }
        }
        setTollbarTheme()
    }

    private fun settingsView() {
        if (keyword && !edittext2Focus) {
            binding.settingsGrup.visibility = View.VISIBLE
        } else {
            binding.settingsGrup.visibility = View.GONE
        }
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
        binding.checkBox.isChecked = pegistrbukv
    }

    override fun setSettingsSlovocalkam(slovocalkam: Int) {
        binding.checkBox2.isChecked = slovocalkam == 1
    }

    override fun setSettingsBibliaSeash(position: Int) {
        if (perevod != DialogVybranoeBibleList.PEREVODNADSAN) {
            binding.spinner6.setSelection(position)
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
            DialogVybranoeBibleList.PEREVODSEMUXI -> biblia = "semuxa"
            DialogVybranoeBibleList.PEREVODSINOIDAL -> biblia = "sinoidal"
            DialogVybranoeBibleList.PEREVODNADSAN -> biblia = "nadsan"
            DialogVybranoeBibleList.PEREVODBOKUNA -> biblia = "bokuna"
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> biblia = "carniauski"
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
            val m = if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
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
        var range = 0..0
        var list = arrayOf("")
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> {
                range = when (chin.getInt("biblia_seash", 0)) {
                    1 -> 39..42
                    2 -> 39 until semuxaBible.size
                    3 -> 0..4
                    4 -> 0..38
                    else -> 0 until semuxaBible.size
                }
                list = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan))
            }

            DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                range = when (chin.getInt("biblia_seash", 0)) {
                    1 -> 50..53
                    2 -> 50 until sinodalBible.size
                    3 -> 0..4
                    4 -> 0..49
                    else -> 0 until sinodalBible.size
                }
                list = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln))
            }

            DialogVybranoeBibleList.PEREVODNADSAN -> {
                range = 0..150
            }

            DialogVybranoeBibleList.PEREVODBOKUNA -> {
                range = when (chin.getInt("biblia_seash", 0)) {
                    1 -> 39..42
                    2 -> 39 until bokunaBible.size
                    3 -> 0..4
                    4 -> 0..38
                    else -> 0 until bokunaBible.size
                }
                list = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan))
            }

            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                range = when (chin.getInt("biblia_seash", 0)) {
                    1 -> 46..49
                    2 -> 46 until carniauskiBible.size
                    3 -> 0..4
                    4 -> 0..45
                    else -> 0 until carniauskiBible.size
                }
                list = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskis).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskin))
            }
        }
        for (i in range) {
            if (searchJob?.isActive == false) break
            val nazva = list[i]
            val inputStream = when (perevod) {
                DialogVybranoeBibleList.PEREVODSEMUXI -> resources.openRawResource(semuxaBible[i])
                DialogVybranoeBibleList.PEREVODSINOIDAL -> resources.openRawResource(sinodalBible[i])
                DialogVybranoeBibleList.PEREVODNADSAN -> resources.openRawResource(R.raw.psaltyr_nadsan)
                DialogVybranoeBibleList.PEREVODBOKUNA -> resources.openRawResource(bokunaBible[i])
                DialogVybranoeBibleList.PEREVODCARNIAUSKI -> resources.openRawResource(carniauskiBible[i])
                else -> resources.openRawResource(semuxaBible[i])
            }
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
                if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
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

    private inner class SearchBibliaListAdaprer(context: Activity, private val arrayList: ArrayList<Spannable>) : ArrayAdapter<Spannable>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, arrayList) {
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

    private class SearchSpinnerAdapter(private val context: Activity, private val name: Array<String>) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_4, name) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.gravity = Gravity.START
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            else textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem4Binding.inflate(context.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
            else viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            viewHolder.text.gravity = Gravity.START
            viewHolder.text.text = name[position]
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    private data class FindString(val str: String, val position: Int)

    companion object {
        private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI
    }
}