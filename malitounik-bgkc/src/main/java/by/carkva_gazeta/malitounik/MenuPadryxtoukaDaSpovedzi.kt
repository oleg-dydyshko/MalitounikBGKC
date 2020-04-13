package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class MenuPadryxtoukaDaSpovedzi : ListFragment() {
    private lateinit var adapter: MyArrayAdapter
    private var k: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun onDialogFontSizePositiveClick() {
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            fragmentManager?.let { dialogFontSize.show(it, "font") }
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            fragmentManager?.let { dialogBrightness.show(it, "brightness") }
        }
        if (id == R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            val prefEditor = k?.edit()
            if (item.isChecked) {
                prefEditor?.putBoolean("dzen_noch", true)
            } else {
                prefEditor?.putBoolean("dzen_noch", false)
            }
            prefEditor?.apply()
            activity?.recreate()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        k = activity?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k?.getBoolean("dzen_noch", false)
        listView.isVerticalScrollBarEnabled = false
        listView.isHorizontalScrollBarEnabled = false
        activity?.let { it ->
            val data = ArrayList<String>()
            val inputStream = it.resources.openRawResource(R.raw.padryxtouka_da_spovedzi)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.forEachLine {
                line = it
                if (dzenNoch == true) line = line.replace("#d00505", "#f44336")
                data.add(line)
            }
            inputStream.close()
            adapter = MyArrayAdapter(it, data)
            listAdapter = adapter
        }
        listView.divider = null
        val pad = (10 * resources.displayMetrics.density).toInt()
        listView.setPadding(pad, pad, pad, pad)
    }

    private class MyArrayAdapter(private val activity: Activity, private val list: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_maranata, list as List<String>) {
        override fun isEnabled(position: Int): Boolean {
            return false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val ea: ViewHolder
            if (convertView == null) {
                ea = ViewHolder()
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_maranata, parent, false)
                ea.textView = rootView.findViewById(R.id.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            ea.textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            ea.textView?.text = MainActivity.fromHtml(list[position])
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
            }
            return rootView
        }

    }

    private class ViewHolder {
        var textView: TextViewRobotoCondensed? = null
    }
}