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
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
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
import by.carkva_gazeta.malitounik.HistoryAdapter
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuBogashlugbovya
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import by.carkva_gazeta.resources.databinding.SearchBogaslugBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader

class SearchBogashlugbovya : BaseActivity(), DialogClearHishory.DialogClearHistoryListener, DialogBibleSearshSettings.DiallogBibleSearshListiner {
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
    private lateinit var binding: SearchBogaslugBinding
    private var keyword = false
    private var edittext2Focus = false
    private var title = ""
    private var searchJob: Job? = null
    private var histiryJob: Job? = null

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
        binding = SearchBogaslugBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.filterGrup.visibility = View.VISIBLE
        binding.buttonx2.setOnClickListener {
            binding.editText2.setText("")
        }
        DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.editText2.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.buttonx2.setImageResource(by.carkva_gazeta.malitounik.R.drawable.cancel)
        }
        if (chin.getString("history_bible_bogaslug", "") != "") {
            val gson = Gson()
            val json = chin.getString("history_bible_bogaslug", "")
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
        if (chin.getString("search_bogashugbovya_string", "") != "") {
            if (chin.getString("search_bogashugbovya_array", "") != "") {
                val gson = Gson()
                val json = chin.getString("search_bogashugbovya_array", "")
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
            val title = adapterView.adapter.getItem(position).toString()
            val intent = Intent(this@SearchBogashlugbovya, Bogashlugbovya::class.java)
            val t1 = title.indexOf("-->")
            intent.putExtra("title", title.substring(t1 + 3))
            intent.putExtra("resurs", title.substring(4, t1))
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            var poshuk1 = autoCompleteTextView?.text.toString()
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1[r] == aM && r >= 3) {
                    poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), true)
                }
            }
            intent.putExtra("search", poshuk1)
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
            prefEditors.putString("search_bogashugbovya_string", edit)
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
            if (isChecked) {
                prefEditors.putBoolean("pegistrbukv", false)
            } else {
                prefEditors.putBoolean("pegistrbukv", true)
            }
            prefEditors.apply()
            val edit = autoCompleteTextView?.text.toString()
            execute(edit, true)
        }
        if (chin.getInt("slovocalkam", 0) == 1) binding.checkBox2.isChecked = true
        binding.checkBox2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                prefEditors.putInt("slovocalkam", 1)
            } else {
                prefEditors.putInt("slovocalkam", 0)
            }
            prefEditors.apply()
            val edit = autoCompleteTextView?.text.toString()
            execute(edit, true)
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
            autoCompleteTextView = view as AutoCompleteTextView
            autoCompleteTextView?.imeOptions = EditorInfo.IME_ACTION_DONE
            autoCompleteTextView?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.underline_white)
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher(autoCompleteTextView))
            autoCompleteTextView?.setText(chin.getString("search_bogashugbovya_string", "")) ?: history[0]
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

    override fun setSettingsPegistrbukv(pegistrbukv: Boolean) {
        binding.checkBox.isChecked = pegistrbukv
    }

    override fun setSettingsSlovocalkam(slovocalkam: Int) {
        binding.checkBox2.isChecked = slovocalkam == 1
    }

    override fun setSettingsBibliaSeash(position: Int) {
        val edit = autoCompleteTextView?.text.toString()
        val bibliaSeash = chin.getInt("biblia_seash", 0)
        if (chin.getString("search_bogashugbovya_string", "") != edit || bibliaSeash != position) {
            prefEditors.putInt("biblia_seash", position)
            prefEditors.apply()
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
            prefEditors.putString("search_bogashugbovya_string", "")
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
            val dialogSearshBible = DialogBibleSearshSettings.getInstance(4)
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
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        val json = gson.toJson(history, type)
        prefEditors.putString("history_bible_bogaslug", json)
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
        prefEditors.putString("search_bogashugbovya_string", autoCompleteTextView?.text.toString())
        prefEditors.apply()
    }

    private fun execute(searcheString: String, run: Boolean = false) {
        if (searcheString.length >= 3) {
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
        val edit = autoCompleteTextView?.text.toString()
        if (edit != "") {
            prefEditors.putString("search_bogashugbovya_string", edit)
            prefEditors.apply()
        }
    }

    private fun doInBackground(searche: String): ArrayList<Spannable> {
        var list = bogashlugbovya(searche)
        if (list.isEmpty() && chin.getInt("slovocalkam", 0) == 0) {
            list = bogashlugbovya(searche, true)
        }
        return list
    }

    private fun onPostExecute(result: ArrayList<Spannable>) {
        adapter.clear()
        adapter.addAll(result)
        adapter.filter.filter(binding.editText2.text.toString())
        textViewCount?.text = getString(by.carkva_gazeta.malitounik.R.string.seash, adapter.count)
        if (chin.getString("search_bogashugbovya_string", "") != "") {
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
        prefEditors.putString("search_bogashugbovya_array", json)
        prefEditors.apply()
        val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        val search = searcheTextView?.text.toString()
        if (search != "" && result.size != 0) {
            binding.ListView.visibility = View.VISIBLE
            addHistory(search)
        }
    }

    private fun bogashlugbovya(poshuk: String, secondRun: Boolean = false): ArrayList<Spannable> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<Spannable>()
        val registr = chin.getBoolean("pegistrbukv", true)
        poshuk1 = MainActivity.zamena(poshuk1, registr)
        if (secondRun) {
            val m = charArrayOf('у', 'е', 'а', 'о', 'э', 'я', 'і', 'ю', 'ь', 'ы')
            for (aM in m) {
                val r = poshuk1.length - 1
                if (poshuk1[r] == aM && r >= 3) {
                    poshuk1 = poshuk1.replace(poshuk1, poshuk1.substring(0, r), registr)
                }
            }
        }
        val bogaslugbovyiaList = MenuBogashlugbovya.getBogaslugbovyiaSearchText(false)
        for (i in 0 until bogaslugbovyiaList.size) {
            if (searchJob?.isActive == false) break
            var nazva: String
            val id = Bogashlugbovya.resursMap[bogaslugbovyiaList[i].resurs] ?: by.carkva_gazeta.malitounik.R.raw.bogashlugbovya_error
            val inputStream = resources.openRawResource(id)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val bibleline = reader.readText()
            val t1 = bibleline.indexOf("<strong>")
            if (t1 != -1) {
                val t2 = bibleline.indexOf("</strong>", t1 + 8)
                nazva = bibleline.substring(t1 + 8, t2)
                nazva = "<!--" + bogaslugbovyiaList[i].resurs + "-->" + Jsoup.parse(nazva).text()
            } else {
                nazva = "<!--" + bogaslugbovyiaList[i].resurs + "-->" + bogaslugbovyiaList[i].resurs
            }
            var prepinanie = Jsoup.parse(bibleline).text()
            prepinanie = prepinanie.replace(",", "")
            prepinanie = prepinanie.replace(".", "")
            prepinanie = prepinanie.replace(";", "")
            prepinanie = prepinanie.replace(":", "")
            prepinanie = prepinanie.replace("[", "")
            prepinanie = prepinanie.replace("]", "")
            prepinanie = prepinanie.replace("(", "")
            prepinanie = prepinanie.replace(")", "")
            prepinanie = prepinanie.replace(" --", "")
            val t5 = prepinanie.indexOf(" -")
            if (t5 != -1) {
                prepinanie = if (prepinanie[t5 - 1].toString() == " ") prepinanie.replace(" -", "")
                else prepinanie.replace("-", " ")
            }
            prepinanie = prepinanie.replace("\"", "")
            prepinanie = prepinanie.replace("?", "")
            if (chin.getInt("slovocalkam", 0) == 0) {
                if (prepinanie.contains(poshuk1, registr)) {
                    seashpost.add(SpannableString(nazva))
                }
            } else {
                if (prepinanie.contains(poshuk1, registr)) {
                    var slovocalkam = false
                    val t2 = poshuk1.length
                    val t4 = prepinanie.indexOf(poshuk1, ignoreCase = registr)
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
                        seashpost.add(SpannableString(nazva))
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
                val editarig = edit.length
                edit = MainActivity.zamena(edit)
                editPosition += edit.length - editarig
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
                        if (chin.getString("search_bogashugbovya_string", "") != edit) execute(edit)
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
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val t1 = arrayList[position].indexOf("-->")
            viewHolder.text.text = arrayList[position].substring(t1 + 3)
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
}