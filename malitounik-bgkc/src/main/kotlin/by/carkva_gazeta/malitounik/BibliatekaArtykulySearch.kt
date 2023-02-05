package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import by.carkva_gazeta.malitounik.databinding.BibliatekaArtykulySearchBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File

class BibliatekaArtykulySearch : BaseActivity(), View.OnClickListener {
    private lateinit var adapter: SearchBibliaListAdaprer
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var fierstPosition = 0
    private var artykulyList = ArrayList<ArtykulyData>()
    private lateinit var binding: BibliatekaArtykulySearchBinding
    private var keyword = false
    private var edittext2Focus = false
    private var searchJob: Job? = null
    private var searchString: String? = null

    companion object {
        private var pegistrbukv = true
        private var slovocalkam = 0
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
    }

    private fun load() {
        try {
            var title = ""
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
            for (rubrika in 0 until resources.getStringArray(R.array.artykuly).size) {
                val path = when (rubrika) {
                    0 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "svietlo_uschodu.json"
                    }
                    1 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "history.json"
                    }
                    2 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "gramadstva.json"
                    }
                    3 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "videa.json"
                    }
                    4 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "adkaz.json"
                    }
                    5 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2022.json"
                    }
                    6 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2021.json"
                    }
                    7 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2020.json"
                    }
                    8 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2019.json"
                    }
                    9 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2018.json"
                    }
                    10 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2017.json"
                    }
                    11 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2016.json"
                    }
                    12 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2015.json"
                    }
                    13 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2014.json"
                    }
                    14 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2013.json"
                    }
                    15 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2012.json"
                    }
                    16 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2011.json"
                    }
                    17 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2010.json"
                    }
                    18 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2009.json"
                    }
                    19 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "naviny2008.json"
                    }
                    20 -> {
                        title = resources.getStringArray(R.array.artykuly)[rubrika]
                        "abvestki.json"
                    }
                    else -> "history.json"
                }
                val localFile = File("$filesDir/$path")
                val text = localFile.readText()
                val arrayData = ArrayList<LinkedTreeMap<String, String>>()
                arrayData.addAll(gson.fromJson(text, type))
                for (i in 0 until arrayData.size) {
                    artykulyList.add(ArtykulyData(title, rubrika, i, arrayData[i]["str"] ?: "", arrayData[i]["link"] ?: ""))
                }
            }
        } catch (_: Throwable) {
            MainActivity.toastView(this, getString(R.string.error_ch2))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BibliatekaArtykulySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load()
        binding.filterGrup.visibility = View.VISIBLE
        binding.buttonx2.setOnClickListener(this)
        DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, R.color.colorPrimary_black))
            binding.buttonx2.setImageResource(R.drawable.cancel)
        }
        adapter = SearchBibliaListAdaprer(this, ArrayList())
        binding.ListView.adapter = adapter
        if (dzenNoch) binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        else binding.ListView.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
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
        binding.editText2.setText("")
        binding.editText2.addTextChangedListener(MyTextWatcher(binding.editText2, true))
        binding.ListView.setOnItemClickListener { adapterView: AdapterView<*>, _: View?, position: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnItemClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val strText = adapterView.adapter.getItem(position).toString()
            var rubrika = -1
            var pos = -1
            for (i in 0 until artykulyList.size) {
                if (strText.contains(artykulyList[i].link)) {
                    rubrika = artykulyList[i].rubrika
                    pos = artykulyList[i].position
                    break
                }
            }
            if (rubrika == -1 || pos == -1) {
                MainActivity.toastView(this, getString(R.string.error_ch2))
            } else {
                val intent = Intent(this, BibliatekaArtykuly::class.java)
                intent.putExtra("rubrika", rubrika)
                intent.putExtra("position", pos)
                startActivity(intent)
            }
        }
        if (savedInstanceState != null) {
            val listView = savedInstanceState.getBoolean("list_view")
            if (listView) binding.ListView.visibility = View.VISIBLE
            fierstPosition = savedInstanceState.getInt("fierstPosition")
            searchString = savedInstanceState.getString("search_string")
        } else {
            fierstPosition = 0
        }
        binding.ListView.setSelection(fierstPosition)
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
        if (!pegistrbukv) binding.checkBox.isChecked = true
        binding.checkBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            pegistrbukv = !isChecked
            val edit = autoCompleteTextView?.text.toString()
            execute(edit)
        }
        if (slovocalkam == 1) binding.checkBox2.isChecked = true
        binding.checkBox2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            slovocalkam = if (isChecked) {
                1
            } else {
                0
            }
            val edit = autoCompleteTextView?.text.toString()
            execute(edit)
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
            binding.toolbar.popupTheme = R.style.AppCompatDark
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
            autoCompleteTextView = view as AutoCompleteTextView
            autoCompleteTextView?.imeOptions = EditorInfo.IME_ACTION_DONE
            autoCompleteTextView?.setBackgroundResource(R.drawable.underline_white)
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher(autoCompleteTextView))
            searchString?.let {
                autoCompleteTextView?.setText(it)
            }
            autoCompleteTextView?.setSelection(autoCompleteTextView?.text?.length ?: 0)
            autoCompleteTextView?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm1 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm1.hideSoftInputFromWindow(autoCompleteTextView?.windowToken, 0)
                }
                true
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_biblia, menu)
        val searchViewItem = menu.findItem(R.id.search)
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
        val searcheTextView = searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searcheTextView.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount = menu.findItem(R.id.count).actionView as TextView
        textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount?.text = getString(R.string.seash, adapter.count)
        val closeButton = searchView?.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener {
            searchJob?.cancel()
            searchView?.setQuery("", false)
            onPostExecute(ArrayList())
        }
        changeSearchViewElements(searchView)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
    }

    override fun onPrepareMenu(menu: Menu) {
        menu.findItem(R.id.action_clean_histopy).isVisible = false
        menu.findItem(R.id.action_search_bible).isVisible = false
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }

    override fun onClick(view: View?) {
        val idSelect = view?.id ?: 0
        if (idSelect == R.id.buttonx2) {
            binding.editText2.setText("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("list_view", binding.ListView.visibility == View.VISIBLE)
        outState.putInt("fierstPosition", fierstPosition)
        outState.putString("search_string", autoCompleteTextView?.text.toString())
    }

    private fun execute(searcheString: String) {
        if (searcheString.length >= 3) {
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

    private fun onPreExecute() {
        adapter.clear()
        textViewCount?.text = getString(R.string.seash, 0)
        binding.progressBar.visibility = View.VISIBLE
        binding.ListView.visibility = View.GONE
    }

    private fun doInBackground(searche: String): ArrayList<Spannable> {
        var list = artykuly(searche)
        if (list.isEmpty() && slovocalkam == 0) {
            list = artykuly(searche, true)
        }
        return list
    }

    private fun onPostExecute(result: ArrayList<Spannable>) {
        adapter.clear()
        adapter.addAll(result)
        adapter.filter.filter(binding.editText2.text.toString())
        textViewCount?.text = getString(R.string.seash, adapter.count)
        binding.progressBar.visibility = View.GONE
        val arrayList = ArrayList<String>()
        result.forEach {
            arrayList.add(MainActivity.toHtml(it))
        }
        val searcheTextView = searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        val search = searcheTextView.text.toString()
        if (search != "" && result.size != 0) {
            binding.ListView.visibility = View.VISIBLE
        }
    }

    private fun artykuly(poshuk: String, secondRun: Boolean = false): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        poshuk1 = poshuk1.replace("ё", "е", pegistrbukv)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'и', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1[r] == aM && r >= 3) {
                    poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), pegistrbukv)
                }
            }
        }
        for (i in 0 until artykulyList.size) {
            if (searchJob?.isActive == false) break
            var prepinanie = artykulyList[i].str
            prepinanie = prepinanie.replace(",", "")
            prepinanie = prepinanie.replace(".", "")
            prepinanie = prepinanie.replace(";", "")
            prepinanie = prepinanie.replace(":", "")
            prepinanie = prepinanie.replace("[", "")
            prepinanie = prepinanie.replace("]", "")
            prepinanie = prepinanie.replace(" --", "")
            val t5 = prepinanie.indexOf("-")
            if (t5 != -1) {
                prepinanie = if (prepinanie[t5 - 1].toString() == " ") prepinanie.replace(" -", "")
                else prepinanie.replace("-", " ")
            }
            prepinanie = prepinanie.replace("\"", "")
            prepinanie = prepinanie.replace("?", "")
            prepinanie = prepinanie.replace("ё", "е", pegistrbukv)
            if (slovocalkam == 0) {
                if (prepinanie.contains(poshuk1, pegistrbukv)) {
                    var aSviatyia = artykulyList[i].link
                    aSviatyia = aSviatyia.replace("\\n", "\n")
                    val t2 = poshuk1.length
                    val title = artykulyList[i].title.length
                    val span = SpannableString(artykulyList[i].title + "\n$aSviatyia")
                    val t1 = span.indexOf(poshuk1, ignoreCase = pegistrbukv)
                    span.setSpan(StyleSpan(Typeface.BOLD), 0, title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (t1 != -1) {
                        span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    seashpost.add(span)
                }
            } else {
                if (prepinanie.contains(poshuk1, pegistrbukv)) {
                    var slovocalkam = false
                    var aSviatyia = artykulyList[i].link
                    aSviatyia = aSviatyia.replace("\\n", "\n")
                    val t2 = poshuk1.length
                    val t4 = prepinanie.indexOf(poshuk1, ignoreCase = pegistrbukv)
                    if (t4 != -1) {
                        val charN = prepinanie[t4 - 1].toString()
                        if (charN == " ") {
                            val aSvL = prepinanie.length
                            if (aSvL == t4 + t2) {
                                slovocalkam = true
                            } else {
                                val charK = prepinanie[t4 + t2].toString()
                                if (charK == " ") {
                                    slovocalkam = true
                                }
                            }
                        }
                    }
                    if (slovocalkam) {
                        val title = artykulyList[i].title.length
                        val span = SpannableString(artykulyList[i].title + "\n$aSviatyia")
                        val t1 = span.indexOf(poshuk1, ignoreCase = pegistrbukv)
                        span.setSpan(StyleSpan(Typeface.BOLD), 0, title, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (t1 != -1) {
                            span.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorBezPosta)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_text)), t1, t1 + t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        seashpost.add(span)
                    }
                }
            }
        }
        return seashpost
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
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("И", "І")
                edit = edit.replace("Щ", "Ў")
                edit = edit.replace("ъ", "'")
                if (check != 0) {
                    editText?.removeTextChangedListener(this)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
                if (editText?.id == androidx.appcompat.R.id.search_src_text) {
                    if (searchJob?.isActive == true && editText.text.length < 3) {
                        searchJob?.cancel()
                        binding.progressBar.visibility = View.GONE
                    } else {
                        execute(edit)
                    }
                }
            }
            if (editText?.id == androidx.appcompat.R.id.search_src_text) {
                if (editText.text.length >= 3) {
                    binding.ListView.visibility = View.VISIBLE
                } else {
                    binding.ListView.visibility = View.GONE
                }
            }
            if (filtep) adapter.filter.filter(edit)
        }

    }

    private inner class SearchBibliaListAdaprer(context: Activity, private val arrayList: ArrayList<Spannable>) : ArrayAdapter<Spannable>(context, R.layout.simple_list_item_2, R.id.label, arrayList) {
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
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = arrayList[position]
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
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
                        val itm = (item as Spannable).indexOf(constraint.toString(), 0, true)
                        val itmcount = constraint.toString().length
                        if (itm != -1) {
                            val span = SpannableString(item)
                            span.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorBezPosta)), itm, itm + itmcount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_text)), itm, itm + itmcount, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            add(span)
                        } else {
                            add(item)
                        }
                    }
                    textViewCount?.text = getString(R.string.seash, results.count)
                }
            }
        }
    }

    private class ViewHolder(var text: TextView)

    private data class ArtykulyData(val title: String, val rubrika: Int, val position: Int, val str: String, val link: String)
}