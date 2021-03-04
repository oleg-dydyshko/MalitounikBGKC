package by.carkva_gazeta.admin

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import by.carkva_gazeta.admin.databinding.AdminSviatyiaPageFragmentBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

class SvityiaFragment : Fragment() {
    private var dayOfYear = 1
    private var _binding: AdminSviatyiaPageFragmentBinding? = null
    private val binding get() = _binding!!
    private var urlJob: Job? = null
    private val sviatyiaNew1 = ArrayList<ArrayList<String>>()
    private val cal = GregorianCalendar()
    private val array = arrayOf("Чырвоны", "Чырвоны тоўсты", "Нармальны")
    private val array2 = arrayOf("Няма", "З вялікай вячэрняй і вялікім услаўленьнем на ютрані", "Двунадзясятыя і вялікія сьвяты", "З ліцьцёй на вячэрні", "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані", "З штодзённай вячэрняй і малым услаўленьнем на ютрані")

    override fun onDestroyView() {
        super.onDestroyView()
        urlJob?.cancel()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dayOfYear = arguments?.getInt("day_of_year", 1) ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AdminSviatyiaPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            sendPostRequest(cal[Calendar.DAY_OF_MONTH], cal[Calendar.MONTH], dayOfYear - 1, binding.sviaty.text.toString(), binding.chytanne.text.toString(), binding.spinnerStyle.selectedItemPosition, binding.spinnerZnak.selectedItemPosition.toString(), binding.apisanne.text.toString())
        }
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 29)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendPostRequest(data: Int, mun: Int, dayOfYear: Int, name: String, chtenie: String, bold: Int, tipicon: String, spaw: String) {
        CoroutineScope(Dispatchers.Main).launch {
            var style = 8
            when (bold) {
                0 -> style = 6
                1 -> style = 7
                2 -> style = 8
            }
            binding.progressBar2.visibility = View.VISIBLE
            var responseCodeS = 500
            withContext(Dispatchers.IO) {
                var reqParam = URLEncoder.encode("pesny", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8")
                reqParam += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(data.toString(), "UTF-8") //День месяца
                reqParam += "&" + URLEncoder.encode("mun", "UTF-8") + "=" + URLEncoder.encode(mun.toString(), "UTF-8")
                reqParam += "&" + URLEncoder.encode("nomerdny", "UTF-8") + "=" + URLEncoder.encode(dayOfYear.toString(), "UTF-8")
                reqParam += "&" + URLEncoder.encode("addksave", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                reqParam += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                reqParam += "&" + URLEncoder.encode("chtenie", "UTF-8") + "=" + URLEncoder.encode(chtenie, "UTF-8")
                reqParam += "&" + URLEncoder.encode("bold", "UTF-8") + "=" + URLEncoder.encode(style.toString(), "UTF-8")
                reqParam += "&" + URLEncoder.encode("tipicon", "UTF-8") + "=" + URLEncoder.encode(tipicon, "UTF-8")
                reqParam += "&" + URLEncoder.encode("spaw", "UTF-8") + "=" + URLEncoder.encode(spaw, "UTF-8")
                val mURL = URL("https://carkva-gazeta.by/admin/android.php")
                with(mURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    val wr = OutputStreamWriter(outputStream)
                    wr.write(reqParam)
                    wr.flush()
                    responseCodeS = responseCode
                }
            }
            activity?.let {
                if (responseCodeS == 200) {
                    MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.save))
                } else {
                    MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
        binding.progressBar2.visibility = View.VISIBLE
        urlJob = CoroutineScope(Dispatchers.Main).launch {
            var res = ""
            withContext(Dispatchers.IO) {
                var url = "https://carkva-gazeta.by/chytanne/sviatyja/opisanie" + (cal[Calendar.MONTH] + 1) + ".json"
                val builder = URL(url).readText()
                val gson = Gson()
                val type = object : TypeToken<ArrayList<String>>() {}.type
                val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                res = arrayList[cal[Calendar.DAY_OF_MONTH] - 1]
                url = "https://carkva-gazeta.by/calendarsviatyia.txt"
                val textfile = URL(url).readText().trim()
                val line = textfile.split("\n")
                for (element in line) {
                    val reg = element.split("<>")
                    val list = ArrayList<String>()
                    for (element2 in reg) {
                        list.add(element2)
                    }
                    sviatyiaNew1.add(list)
                }
            }
            binding.sviaty.setText(sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][0])
            binding.chytanne.setText(sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][1])
            activity?.let {
                binding.spinnerStyle.adapter = SpinnerAdapter(it, array)
                var position = 0
                when (sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][2].toInt()) {
                    6 -> position = 0
                    7 -> position = 1
                    8 -> position = 2
                }
                binding.spinnerStyle.setSelection(position)
                binding.spinnerZnak.adapter = SpinnerAdapter(it, array2)
                val znaki = sviatyiaNew1[cal[Calendar.DAY_OF_YEAR] - 1][3]
                val position2 = if (znaki == "") 0
                else znaki.toInt()
                binding.spinnerZnak.setSelection(position2)
            }
            binding.apisanne.setText(res)
            binding.progressBar2.visibility = View.GONE
        }
    }

    private class SpinnerAdapter(activity: Activity, private val data: Array<String>) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextViewRobotoCondensed
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.text = data[position]
            textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text.text = data[position]
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }

    }

    private class ViewHolder(var text: TextViewRobotoCondensed)

    companion object {
        fun newInstance(day_of_year: Int): SvityiaFragment {
            val fragmentFirst = SvityiaFragment()
            val args = Bundle()
            args.putInt("day_of_year", day_of_year)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}