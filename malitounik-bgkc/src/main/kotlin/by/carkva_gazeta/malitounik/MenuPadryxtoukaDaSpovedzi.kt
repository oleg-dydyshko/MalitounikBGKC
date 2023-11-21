package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SimpleListItemMaranataBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MenuPadryxtoukaDaSpovedzi : BaseListFragment() {
    private lateinit var adapter: MyArrayAdapter
    private lateinit var k: SharedPreferences

    fun onDialogFontSize() {
        adapter.notifyDataSetChanged()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(childFragmentManager, "font")
            return true
        }
        if (id == R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(childFragmentManager, "brightness")
            return true
        }
        if (id == R.id.action_dzen_noch) {
            activity?.let {
                if (item.isCheckable) {
                    val prefEditor = k.edit()
                    item.isChecked = !item.isChecked
                    if (item.isChecked) {
                        prefEditor.putBoolean("dzen_noch", true)
                    } else {
                        prefEditor.putBoolean("dzen_noch", false)
                    }
                    prefEditor.apply()
                    it.recreate()
                } else {
                    startActivity(Intent(it, SettingsActivity::class.java))
                }
            }
            return true
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.isVerticalScrollBarEnabled = false
        listView.isHorizontalScrollBarEnabled = false
        activity?.let { it ->
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            val data = ArrayList<String>()
            val inputStream = it.resources.openRawResource(R.raw.padryxtouka_da_spovedzi)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.use { bufferedReader ->
                bufferedReader.forEachLine {
                    line = it
                    if (dzenNoch) line = line.replace("#d00505", "#f44336")
                    data.add(line)
                }
            }
            adapter = MyArrayAdapter(it, data)
            listAdapter = adapter
            if (dzenNoch) {
                listView.setBackgroundResource(R.color.colorbackground_material_dark)
                listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            }
        }
        listView.divider = null
        val pad = (10 * resources.displayMetrics.density).toInt()
        listView.setPadding(pad, pad, pad, pad)
    }

    private class MyArrayAdapter(private val activity: Activity, private val list: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_maranata, list) {
        override fun isEnabled(position: Int): Boolean {
            return false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val ea: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemMaranataBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                ea = ViewHolder(binding.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            ea.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT))
            ea.textView.text = MainActivity.fromHtml(list[position])
            if ((activity as BaseActivity).getBaseDzenNoch()) {
                ea.textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
            }
            return rootView
        }

    }

    private class ViewHolder(var textView: TextView)
}