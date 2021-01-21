package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SimpleListItemMaranataBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MenuPamiatka : PadryxtoukaPamiatkaListFragment() {
    private lateinit var adapter: MyArrayAdapter
    private lateinit var k: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDialogFontSize(fontSize: Float) {
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
            val prefEditor = k.edit()
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
        activity?.let { fragmentActivity ->
            k = fragmentActivity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            listView.isVerticalScrollBarEnabled = false
            listView.isHorizontalScrollBarEnabled = false
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val inputStream = resources.openRawResource(R.raw.pamiatka)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            val data = ArrayList<String>()
            reader.use { bufferedReader ->
                bufferedReader.forEachLine {
                    line = it
                    if (dzenNoch) line = line.replace("#d00505", "#f44336")
                    data.add(line)
                }
            }
            adapter = MyArrayAdapter(fragmentActivity, data)
            listAdapter = adapter
            listView.divider = null
            val pad = (10 * resources.displayMetrics.density).toInt()
            listView.setPadding(pad, pad, pad, pad)
        }
    }

    private class MyArrayAdapter(private val activity: Activity, private val list: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_maranata, list) {
        override fun isEnabled(position: Int): Boolean {
            return false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val ea: ExpArrayAdapterParallelItems
            if (convertView == null) {
                val binding = SimpleListItemMaranataBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                ea = ExpArrayAdapterParallelItems(binding.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ExpArrayAdapterParallelItems
            }
            ea.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            ea.textView.text = MainActivity.fromHtml(list[position])
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
            }
            return rootView
        }

    }

    private class ExpArrayAdapterParallelItems(var textView: TextViewRobotoCondensed)
}