package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.PashaliiBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem3Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemPaschaliiBinding
import java.util.Calendar
import java.util.GregorianCalendar

class MenuPashalii : BaseFragment() {
    private lateinit var myArrayAdapter: MyArrayAdapter
    private var binding: PashaliiBinding? = null
    private var editText: AutoCompleteTextView? = null
    private var searchView: SearchView? = null
    private var search = false
    private var searchViewQwery = ""
    private val textWatcher = MyTextWatcher()
    private var day = 0
    private var month = 0

    companion object {
        private const val XVI = 1
        private const val XVII = 2
        private const val XVIII = 3
        private const val XIX = 4
        private const val XX = 5
        private const val XXI = 6
        private const val ALL = 7
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        editText?.addTextChangedListener(textWatcher)
        if (search) {
            setArrayPasha(ALL)
        } else {
            binding?.let { binding ->
                if (binding.spinnerVek.selectedItemPosition == 0) {
                    setArrayPasha(binding.day.selectedItemPosition + 1, binding.month.selectedItemPosition + 3)
                } else {
                    setArrayPasha(binding.spinnerVek.selectedItemPosition)
                }
                if (binding.spinnerVek.selectedItemPosition == XXI) {
                    binding.pasha.post {
                        binding.pasha.setSelection(Calendar.getInstance()[Calendar.YEAR] - 2000 - 3)
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pashalii, menu)
        super.onCreateMenu(menu, menuInflater)
        val searchViewItem = menu.findItem(R.id.search)
        searchView = searchViewItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.data_search3)
        activity?.let {
            val searcheTextView = searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            searcheTextView?.typeface = MainActivity.createFont(Typeface.NORMAL)
        }
        if (search) {
            searchViewItem.expandActionView()
            setArrayPasha(ALL)
        }
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                search = true
                day = 0
                month = 0
                binding?.day?.visibility = View.GONE
                binding?.month?.visibility = View.GONE
                binding?.help?.visibility = View.GONE
                setArrayPasha(ALL)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                searchView?.setOnQueryTextListener(null)
                search = false
                if (binding?.spinnerVek?.selectedItemPosition == 0) binding?.spinnerVek?.setSelection(XXI)
                setArrayPasha(binding?.spinnerVek?.selectedItemPosition ?: XXI)
                binding?.searshResult?.visibility = View.GONE
                return true
            }
        })
        changeSearchViewElements(searchView)
        if (searchViewQwery != "") {
            searchViewItem.expandActionView()
            searchView?.setQuery(searchViewQwery, true)
            searchView?.clearFocus()
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
            editText = view as AutoCompleteTextView
            editText?.inputType = InputType.TYPE_CLASS_NUMBER
            editText?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    activity?.let {
                        val imm1 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                    }
                }
                true
            }
            editText?.imeOptions = EditorInfo.IME_ACTION_DONE
            editText?.addTextChangedListener(textWatcher)
            editText?.setBackgroundResource(R.drawable.underline_white)
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                changeSearchViewElements(view.getChildAt(i))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SearchViewQwery", searchView?.query.toString())
        outState.putBoolean("search", search)
        outState.putInt("day", day)
        outState.putInt("month", month)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PashaliiBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            binding?.let { binding ->
                myArrayAdapter = MyArrayAdapter(activity, ArrayList())
                if (savedInstanceState != null) {
                    searchViewQwery = savedInstanceState.getString("SearchViewQwery") ?: ""
                    search = savedInstanceState.getBoolean("search", false)
                    day = savedInstanceState.getInt("day", 0)
                    month = savedInstanceState.getInt("month", 0)
                }
                val listVek = resources.getStringArray(R.array.vek)
                binding.spinnerVek.adapter = VekAdapter(activity, listVek)
                binding.spinnerVek.setSelection(XXI)
                binding.spinnerVek.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position != 0) {
                            day = 0
                            month = 0
                            binding.day.visibility = View.GONE
                            binding.month.visibility = View.GONE
                            binding.help.visibility = View.GONE
                            setArrayPasha(position)
                            if (position == XXI) binding.pasha.setSelection(Calendar.getInstance()[Calendar.YEAR] - 2000 - 3)
                            else binding.pasha.setSelection(binding.month.selectedItemPosition)
                        } else {
                            binding.day.visibility = View.VISIBLE
                            binding.month.visibility = View.VISIBLE
                            binding.help.visibility = View.VISIBLE
                            day = binding.day.selectedItemPosition + 1
                            month = binding.month.selectedItemPosition + 3
                            setArrayPasha(day, month)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                val dayList = ArrayList<String>()
                for (i in 1..31)
                    dayList.add(i.toString())
                binding.day.adapter = ListAdapterDay(activity, dayList)
                val monthList = ArrayList<String>()
                monthList.add("Сакавіка")
                monthList.add("Красавіка")
                binding.month.adapter = ListAdapterDay(activity, monthList)
                binding.day.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        day = position + 1
                        if (binding.spinnerVek.selectedItemPosition == 0) setArrayPasha(day, month)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                binding.month.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        month = position + 3
                        if (binding.spinnerVek.selectedItemPosition == 0) setArrayPasha(day, month)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                binding.pasha.adapter = myArrayAdapter
                binding.pasha.selector = ContextCompat.getDrawable(activity, android.R.color.transparent)
                binding.pasha.isClickable = false
                val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
                if (dzenNoch) {
                    binding.gri.setBackgroundResource(R.color.colorbackground_material_dark)
                    binding.ula.setBackgroundResource(R.color.colorbackground_material_dark)
                    binding.pasha.setBackgroundResource(R.color.colorbackground_material_dark)
                    binding.pasha.selector = ContextCompat.getDrawable(activity, R.drawable.selector_dark)
                }
                binding.textView.setOnClickListener {
                    val intent = Intent(activity, Pasxa::class.java)
                    startActivity(intent)
                }
                binding.pasha.setOnScrollListener(object : AbsListView.OnScrollListener {
                    override fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                        if (i == 1) {
                            val imm1 = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm1.hideSoftInputFromWindow(editText?.windowToken, 0)
                            searchView?.clearFocus()
                        }
                    }

                    override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
                })
            }
        }
    }

    private fun setArrayPasha(day: Int, month: Int) {
        myArrayAdapter.clear()
        var dataP: Int
        var monthP: Int
        var dataPrav: Int
        var monthPrav: Int
        val monthName = resources.getStringArray(R.array.meciac_smoll)
        val pasxi = ArrayList<Pashalii>()
        for (year in 1..2499) {
            val a = year % 19
            val b = year % 4
            val cx = year % 7
            val k = year / 100
            val p = (13 + 8 * k) / 25
            val q = k / 4
            val m = (15 - p + k - q) % 30
            val n = (4 + k - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            val a2 = (19 * (year % 19) + 15) % 30
            val b2 = (2 * (year % 4) + 4 * (year % 7) + 6 * a2 + 6) % 7
            if (a2 + b2 > 9) {
                dataPrav = a2 + b2 - 9
                monthPrav = 4
            } else {
                dataPrav = 22 + a2 + b2
                monthPrav = 3
            }
            val pravas = GregorianCalendar(year, monthPrav - 1, dataPrav)
            val katolic = GregorianCalendar(year, monthP - 1, dataP)
            val vek = if (year > 1582) year.toString().substring(0, 2)
            else ""
            when {
                year <= 1582 -> pravas.timeInMillis = katolic.timeInMillis
                vek == "15" || vek == "16" -> pravas.add(Calendar.DATE, 10)
                vek == "17" -> pravas.add(Calendar.DATE, 11)
                vek == "18" -> pravas.add(Calendar.DATE, 12)
                vek == "19" || vek == "20" -> pravas.add(Calendar.DATE, 13)
                vek == "21" -> pravas.add(Calendar.DATE, 14)
                vek == "22" -> pravas.add(Calendar.DATE, 15)
                vek == "23" || vek == "24" -> pravas.add(Calendar.DATE, 16)
            }
            var sovpadenie = false
            if (katolic[Calendar.DAY_OF_YEAR] == pravas[Calendar.DAY_OF_YEAR]) sovpadenie = true
            if (day == dataP && month == monthP) {
                pasxi.add(Pashalii(dataP.toString() + " " + monthName[monthP - 1] + " " + year, pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]], year, sovpadenie))
            }
        }
        myArrayAdapter.addAll(pasxi)
    }

    private fun setArrayPasha(yearS: String) {
        val year = yearS.toInt()
        var dataP: Int
        val monthP: Int
        val dataPrav: Int
        val monthPrav: Int
        val monthName = resources.getStringArray(R.array.meciac_smoll)
        val a = year % 19
        val b = year % 4
        val cx = year % 7
        val k = year / 100
        val p = (13 + 8 * k) / 25
        val q = k / 4
        val m = (15 - p + k - q) % 30
        val n = (4 + k - q) % 7
        val d = (19 * a + m) % 30
        val ex = (2 * b + 4 * cx + 6 * d + n) % 7
        if (d + ex <= 9) {
            dataP = d + ex + 22
            monthP = 3
        } else {
            dataP = d + ex - 9
            if (d == 29 && ex == 6) dataP = 19
            if (d == 28 && ex == 6) dataP = 18
            monthP = 4
        }
        val a2 = (19 * (year % 19) + 15) % 30
        val b2 = (2 * (year % 4) + 4 * (year % 7) + 6 * a2 + 6) % 7
        if (a2 + b2 > 9) {
            dataPrav = a2 + b2 - 9
            monthPrav = 4
        } else {
            dataPrav = 22 + a2 + b2
            monthPrav = 3
        }
        val pravas = GregorianCalendar(year, monthPrav - 1, dataPrav)
        val katolic = GregorianCalendar(year, monthP - 1, dataP)
        val vek = if (year > 1582) year.toString().substring(0, 2)
        else ""
        when (vek) {
            "21" -> pravas.add(Calendar.DATE, 14)
            "22" -> pravas.add(Calendar.DATE, 15)
            "23", "24" -> pravas.add(Calendar.DATE, 16)
        }
        var sovpadenie = false
        if (katolic[Calendar.DAY_OF_YEAR] == pravas[Calendar.DAY_OF_YEAR]) sovpadenie = true
        var color = R.color.colorPrimary_text
        if ((requireActivity() as BaseActivity).getBaseDzenNoch()) {
            color = R.color.colorWhite
            binding?.searshResult?.setTextColor(ContextCompat.getColor(requireActivity(), color))
        }
        val pasxa = SpannableStringBuilder(dataP.toString() + " " + monthName[monthP - 1] + " " + year)
        if (year <= 1582) {
            pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorSecondary_text)), 0, (dataP.toString() + " " + monthName[monthP - 1] + " " + year).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            if (!sovpadenie) {
                pasxa.append("\n${pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]]}")
                pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorSecondary_text)), (dataP.toString() + " " + monthName[monthP - 1] + " " + year).length, pasxa.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), color)), 0, (dataP.toString() + " " + monthName[monthP - 1] + " " + year).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding?.searshResult?.visibility = View.VISIBLE
        binding?.searshResult?.text = pasxa
    }

    private fun setArrayPasha(vekPashi: Int) {
        myArrayAdapter.clear()
        val yearG: Int
        val yearG2: Int
        when (vekPashi) {
            XVI -> {
                yearG = 1500
                yearG2 = 1599
            }

            XVII -> {
                yearG = 1600
                yearG2 = 1699
            }

            XVIII -> {
                yearG = 1700
                yearG2 = 1799
            }

            XIX -> {
                yearG = 1800
                yearG2 = 1899
            }

            XX -> {
                yearG = 1900
                yearG2 = 1999
            }

            XXI -> {
                yearG = 2000
                yearG2 = 2099
            }

            ALL -> {
                yearG = 1500
                yearG2 = 2099
            }

            else -> {
                yearG = 2000
                yearG2 = 2099
            }
        }
        var dataP: Int
        var monthP: Int
        var dataPrav: Int
        var monthPrav: Int
        val monthName = resources.getStringArray(R.array.meciac_smoll)
        val pasxi = ArrayList<Pashalii>()
        for (year in yearG..yearG2) {
            val a = year % 19
            val b = year % 4
            val cx = year % 7
            val k = year / 100
            val p = (13 + 8 * k) / 25
            val q = k / 4
            val m = (15 - p + k - q) % 30
            val n = (4 + k - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            val a2 = (19 * (year % 19) + 15) % 30
            val b2 = (2 * (year % 4) + 4 * (year % 7) + 6 * a2 + 6) % 7
            if (a2 + b2 > 9) {
                dataPrav = a2 + b2 - 9
                monthPrav = 4
            } else {
                dataPrav = 22 + a2 + b2
                monthPrav = 3
            }
            val pravas = GregorianCalendar(year, monthPrav - 1, dataPrav)
            val katolic = GregorianCalendar(year, monthP - 1, dataP)
            val vek = yearG.toString().substring(0, 2)
            when {
                year <= 1582 -> pravas.timeInMillis = katolic.timeInMillis
                vek == "15" || vek == "16" -> pravas.add(Calendar.DATE, 10)
                vek == "17" -> pravas.add(Calendar.DATE, 11)
                vek == "18" -> pravas.add(Calendar.DATE, 12)
                vek == "19" || vek == "20" -> pravas.add(Calendar.DATE, 13)
                vek == "21" -> pravas.add(Calendar.DATE, 14)
                vek == "22" -> pravas.add(Calendar.DATE, 15)
                vek == "23" || vek == "24" -> pravas.add(Calendar.DATE, 16)
            }
            var sovpadenie = false
            if (katolic[Calendar.DAY_OF_YEAR] == pravas[Calendar.DAY_OF_YEAR]) sovpadenie = true
            pasxi.add(Pashalii(dataP.toString() + " " + monthName[monthP - 1] + " " + year, pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]], year, sovpadenie))
        }
        myArrayAdapter.addAll(pasxi)
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
            if (editch && search) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("И", "І")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                if (check != 0) {
                    editText?.removeTextChangedListener(null)
                    editText?.setText(edit)
                    editText?.setSelection(editPosition)
                    editText?.addTextChangedListener(this)
                }
                if (edit.isNotEmpty()) {
                    val year = edit.toInt()
                    if (year in 1..1499 || year in 2100..2499) setArrayPasha(edit)
                    else binding?.searshResult?.visibility = View.GONE
                } else binding?.searshResult?.visibility = View.GONE
                myArrayAdapter.filter.filter(edit)
            }
        }
    }

    private class MyArrayAdapter(private val context: Activity, private val pasxi: ArrayList<Pashalii>) : ArrayAdapter<Pashalii>(context, R.layout.simple_list_item_sviaty, pasxi) {
        private val origData = ArrayList<Pashalii>()

        override fun addAll(collection: Collection<Pashalii>) {
            super.addAll(collection)
            origData.clear()
            origData.addAll(collection)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val ea: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemPaschaliiBinding.inflate(context.layoutInflater, parent, false)
                rootView = binding.root
                ea = ViewHolder(binding.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            var color = R.color.colorPrimary_text
            var colorP = R.color.colorPrimary
            if ((context as BaseActivity).getBaseDzenNoch()) {
                ea.textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                color = R.color.colorWhite
                colorP = R.color.colorPrimary_black
            }
            val c = Calendar.getInstance()
            val pasxa = SpannableStringBuilder(pasxi[position].katolic)
            if (pasxi[position].katolicYear <= 1582) {
                pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                if (!pasxi[position].sovpadenie) {
                    pasxa.append("\n${pasxi[position].pravas}")
                    pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), pasxi[position].katolic.length, pasxa.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (pasxi[position].katolicYear == c[Calendar.YEAR]) {
                    pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, colorP)), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    pasxa.setSpan(StyleSpan(Typeface.BOLD), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            ea.textView.text = pasxa
            return rootView
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val result = FilterResults()
                    if (constraint.toString().isNotEmpty()) {
                        val founded = ArrayList<Pashalii>()
                        for (item in origData) {
                            if (item.katolic.contains(constraint, true)) {
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
                        add(item as Pashalii)
                    }
                }
            }
        }
    }

    private class VekAdapter(private val activity: Activity, private val dataVek: Array<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_1, dataVek) {
        private val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = dataVek[position]
            if (dzenNoch) textView.setBackgroundResource(R.drawable.selector_dark)
            else textView.setBackgroundResource(R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataVek.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(activity.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.textView.text = dataVek[position]
            if (dzenNoch) viewHolder.textView.setBackgroundResource(R.drawable.selector_dark)
            else viewHolder.textView.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }
    }

    private class ListAdapterDay(private val mContext: Activity, private val arrayList: ArrayList<String>) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_1, arrayList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem3Binding.inflate(mContext.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            viewHolder.textView.text = arrayList[position]
            if (dzenNoch) viewHolder.textView.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else viewHolder.textView.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            val text = v as TextView
            text.text = arrayList[position]
            if (dzenNoch) text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else text.setBackgroundResource(R.drawable.selector_default)
            return v
        }
    }

    private class ViewHolder(var textView: TextView)

    private class Pashalii(val katolic: String, val pravas: String, val katolicYear: Int, val sovpadenie: Boolean)
}
