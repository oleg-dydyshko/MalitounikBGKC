package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.ContentPsalterBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemArtykulyBinding
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File


class BibliatekaArtykulyList : BaseActivity(), AdapterView.OnItemClickListener, DialogBibliatekaArtykuly.DialogBibliatekaArtykulyListener {
    private var mLastClickTime: Long = 0
    private var position = 1
    private lateinit var binding: ContentPsalterBinding
    private var resetTollbarJob: Job? = null
    private lateinit var listAdapter: ArrayAdapter<LinkedTreeMap<String, String>>
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var textViewCount: TextView? = null
    private var searchView: SearchView? = null
    private var searchString: String? = null
    private var loadJob: Job? = null
    private var isRestart: Boolean = false
    private var keyword = false
    private var edittext2Focus = false
    private var searchJob: Job? = null
    private var fierstPosition = 0
    private var artykulyList = ArrayList<LinkedTreeMap<String, String>>()
    private var path = "history.json"

    companion object {
        private var pegistrbukv = true
        private var slovocalkam = 0
    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
        resetTollbarJob?.cancel()
        loadJob?.cancel()
    }

    private suspend fun load(isLoad: Boolean = false) {
        binding.progressBar.visibility = View.VISIBLE
        try {
            artykulyList.clear()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
            var dirSize = 0L
            for (rubrika in 0 until resources.getStringArray(R.array.artykuly).size) {
                val path = when (rubrika) {
                    0 -> "svietlo_uschodu.json"
                    1 -> "history.json"
                    2 -> "gramadstva.json"
                    3 -> "videa.json"
                    4 -> "adkaz.json"
                    5 -> "naviny2022.json"
                    6 -> "naviny2021.json"
                    7 -> "naviny2020.json"
                    8 -> "naviny2019.json"
                    9 -> "naviny2018.json"
                    10 -> "naviny2017.json"
                    11 -> "naviny2016.json"
                    12 -> "naviny2015.json"
                    13 -> "naviny2014.json"
                    14 -> "naviny2013.json"
                    15 -> "naviny2012.json"
                    16 -> "naviny2011.json"
                    17 -> "naviny2010.json"
                    18 -> "naviny2009.json"
                    19 -> "naviny2008.json"
                    20 -> "abvestki.json"
                    else -> "history.json"
                }
                val localFile = File("$filesDir/Artykuly/$path")
                if (!localFile.exists()) {
                    if ((MainActivity.isNetworkAvailable() && !MainActivity.isNetworkAvailable(true)) || isLoad) {
                        Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                            MainActivity.toastView(this@BibliatekaArtykulyList, getString(R.string.error))
                        }.await()
                    } else if (MainActivity.isNetworkAvailable()) {
                        Malitounik.referens.child("/$path").metadata.addOnSuccessListener {
                            dirSize += it.sizeBytes
                        }.await()
                    }
                }
                if (localFile.exists()) {
                    val text = localFile.readText()
                    if (text == "") localFile.delete()
                    else artykulyList.addAll(gson.fromJson(text, type))
                }
            }
            if (dirSize != 0L) {
                val dialog = DialogBibliatekaArtykuly.getInstance(dirSize)
                dialog.show(supportFragmentManager, "DialogBibliatekaArtykuly")
            }
        } catch (e: Throwable) {
            MainActivity.toastView(this, getString(R.string.error_ch2))
        }
        binding.progressBar.visibility = View.GONE
    }

    override fun onDialogbibliatekaArtykulyPositiveClick() {
        loadJob?.cancel()
        loadJob = CoroutineScope(Dispatchers.Main).launch {
            load(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentPsalterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        position = intent.extras?.getInt("position") ?: 1
        path = when (position) {
            0 -> "svietlo_uschodu.json"
            1 -> "history.json"
            2 -> "gramadstva.json"
            3 -> "videa.json"
            4 -> "adkaz.json"
            5 -> "naviny2022.json"
            6 -> "naviny2021.json"
            7 -> "naviny2020.json"
            8 -> "naviny2019.json"
            9 -> "naviny2018.json"
            10 -> "naviny2017.json"
            11 -> "naviny2016.json"
            12 -> "naviny2015.json"
            13 -> "naviny2014.json"
            14 -> "naviny2013.json"
            15 -> "naviny2012.json"
            16 -> "naviny2011.json"
            17 -> "naviny2010.json"
            18 -> "naviny2009.json"
            19 -> "naviny2008.json"
            20 -> "abvestki.json"
            else -> "history.json"
        }
        if (savedInstanceState != null) {
            fierstPosition = savedInstanceState.getInt("fierstPosition")
            searchString = savedInstanceState.getString("search_string")
            isRestart = savedInstanceState.getBoolean("isSearsh", true)
        } else {
            fierstPosition = 0
        }
        listAdapter = MenuListAdaprer(this)
        binding.listView.adapter = listAdapter
        val oldLocalFile = File("$filesDir/$path")
        if (oldLocalFile.exists()) oldLocalFile.delete()
        val dir = File("$filesDir/Artykuly")
        if (!dir.exists()) dir.mkdir()
        val localFile = File("$filesDir/Artykuly/$path")
        if (MainActivity.isNetworkAvailable(true)) {
            if (!localFile.exists()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                        MainActivity.toastView(this@BibliatekaArtykulyList, getString(R.string.error))
                    }.await()
                    load(localFile)
                }
            } else {
                load(localFile)
            }
        } else if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                Malitounik.referens.child("/$path").getFile(localFile).addOnFailureListener {
                    MainActivity.toastView(this@BibliatekaArtykulyList, getString(R.string.error))
                }.await()
                load(localFile)
            }
        } else {
            if (localFile.exists()) {
                load(localFile)
            }
        }
        binding.listView.onItemClickListener = this
        binding.listView.isVerticalScrollBarEnabled = false
        binding.listView.setSelection(fierstPosition)
        binding.constraint.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding.constraint.rootView.height - binding.constraint.height
            val keywordView = binding.constraint.rootView.height / 4
            keyword = heightDiff > keywordView
            settingsView()
        }
        binding.buttonx2.setOnClickListener {
            binding.editText2.setText("")
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
        binding.listView.setOnScrollListener(object : AbsListView.OnScrollListener {
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
        val dzenNoch = (this as BaseActivity).getBaseDzenNoch()
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.listView.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.listView.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
            binding.buttonx2.setImageResource(R.drawable.cancel)
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        binding.titleToolbar.text = resources.getStringArray(R.array.artykuly)[position]
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
    }

    private fun settingsView() {
        if (keyword && !edittext2Focus) {
            binding.settingsGrup.visibility = View.VISIBLE
        } else {
            binding.settingsGrup.visibility = View.GONE
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    private fun load(localFile: File) {
        try {
            listAdapter.clear()
            val gson = Gson()
            val text = localFile.readText()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(LinkedTreeMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
            listAdapter.addAll(gson.fromJson<ArrayList<LinkedTreeMap<String, String>>>(text, type))
            listAdapter.notifyDataSetChanged()
        } catch (e: Throwable) {
            MainActivity.toastView(this, getString(R.string.error_ch2))
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val intent = Intent(this, BibliatekaArtykuly::class.java)
        val rub = when (listAdapter.getItem(position)?.get("rub") ?: path) {
            "svietlo_uschodu.json" -> 0
            "history.json" -> 1
            "gramadstva.json" -> 2
            "videa.json" -> 3
            "adkaz.json" -> 4
            "naviny2022.json" -> 5
            "naviny2021.json" -> 6
            "naviny2020.json" -> 7
            "naviny2019.json" -> 8
            "naviny2018.json" -> 9
            "naviny2017.json" -> 10
            "naviny2016.json" -> 11
            "naviny2015.json" -> 12
            "naviny2014.json" -> 13
            "naviny2013.json" -> 14
            "naviny2012.json" -> 15
            "naviny2011.json" -> 16
            "naviny2010.json" -> 17
            "naviny2009.json" -> 18
            "naviny2008.json" -> 19
            "abvestki.json" -> 20
            else -> 1
        }
        intent.putExtra("rubrika", rub)
        if (searchView?.isIconified == false) intent.putExtra("position", fingListPosition(listAdapter.getItem(position)?.get("link")))
        else intent.putExtra("position", position)
        startActivity(intent)
    }

    private fun fingListPosition(link: String?): Int {
        var position = 0
        var rub = "history.json"
        for (i in 0 until artykulyList.size) {
            if (rub != artykulyList[i]["rub"]) {
                rub = artykulyList[i]["rub"] ?: "history.json"
                position = 0
            }
            if (artykulyList[i]["link"] == link) {
                return position
            }
            position++
        }
        return 0
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
            autoCompleteTextView?.addTextChangedListener(MyTextWatcher(autoCompleteTextView))
            autoCompleteTextView?.imeOptions = EditorInfo.IME_ACTION_DONE
            autoCompleteTextView?.setBackgroundResource(R.drawable.underline_white)
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
                loadJob = CoroutineScope(Dispatchers.Main).launch {
                    menu.findItem(R.id.count).isVisible = true
                    binding.filterGrup.visibility = View.VISIBLE
                    load()
                }
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                loadJob?.cancel()
                load(File("$filesDir/Artykuly/$path"))
                menu.findItem(R.id.count).isVisible = false
                binding.filterGrup.visibility = View.GONE
                searchView?.setQuery("", false)
                textViewCount?.text = getString(R.string.seash, 0)
                return true
            }
        })
        if (isRestart) searchViewItem.expandActionView()
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.poshuk)
        val searcheTextView = searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searcheTextView.typeface = MainActivity.createFont(Typeface.NORMAL)
        menu.findItem(R.id.count).isVisible = false
        textViewCount = menu.findItem(R.id.count).actionView as TextView
        textViewCount?.typeface = MainActivity.createFont(Typeface.NORMAL)
        textViewCount?.text = getString(R.string.seash, listAdapter.count)
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
        menu.findItem(R.id.count).isVisible = searchView?.isIconified != true
    }

    private fun execute(searcheString: String) {
        if (searcheString.length >= 3) {
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
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun doInBackground(searche: String): ArrayList<LinkedTreeMap<String, String>> {
        var list = artykuly(searche)
        if (list.isEmpty() && slovocalkam == 0) {
            list = artykuly(searche, true)
        }
        return list
    }

    private fun onPostExecute(result: ArrayList<LinkedTreeMap<String, String>>) {
        listAdapter.clear()
        listAdapter.addAll(result)
        listAdapter.notifyDataSetChanged()
        listAdapter.filter.filter(binding.editText2.text.toString())
        textViewCount?.text = getString(R.string.seash, listAdapter.count)
        binding.progressBar.visibility = View.GONE
    }

    private fun artykuly(poshuk: String, secondRun: Boolean = false): ArrayList<LinkedTreeMap<String, String>> {
        var poshuk1 = poshuk
        val seashpost = ArrayList<LinkedTreeMap<String, String>>()
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
            var prepinanie = artykulyList[i]["str"] ?: ""
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
                    seashpost.add(artykulyList[i])
                }
            } else {
                if (prepinanie.contains(poshuk1, pegistrbukv)) {
                    var slovocalkam = false
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
                        seashpost.add(artykulyList[i])
                    }
                }
            }
        }
        return seashpost
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("fierstPosition", fierstPosition)
        outState.putString("search_string", autoCompleteTextView?.text.toString())
        outState.putBoolean("isSearsh", !(searchView?.isIconified ?: true))
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
            if (filtep) listAdapter.filter.filter(edit)
        }

    }

    private inner class MenuListAdaprer(private val context: Activity) : ArrayAdapter<LinkedTreeMap<String, String>>(context, R.layout.simple_list_item_2, R.id.label) {
        private val data = ArrayList<LinkedTreeMap<String, String>>()

        override fun addAll(collection: MutableCollection<out LinkedTreeMap<String, String>>) {
            super.addAll(collection)
            data.addAll(collection)
        }

        override fun clear() {
            super.clear()
            data.clear()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItemArtykulyBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label, binding.image)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val ssb = SpannableStringBuilder()
            val dataArt = SpannableString(data[position]["data"] ?: "")
            dataArt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), 0, dataArt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataArt.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt() - 2, true), 0, dataArt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.append(dataArt)
            val link = MainActivity.fromHtml(data[position]["link"] ?: "")
            ssb.append("\n")
            ssb.append(link)
            viewHolder.text.text = ssb
            if (data[position]["img_cache"]?.isEmpty() == true) {
                viewHolder.image.visibility = View.GONE
            } else {
                Picasso.get().load(data[position]["img_cache"]).into(viewHolder.image)
            }
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var image: ImageView)
}