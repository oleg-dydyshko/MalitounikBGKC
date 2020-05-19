package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat

internal class MenuPesnyListAdapter(private val activity: Activity, private val data: ArrayList<String>) : ArrayAdapter<String>(activity, R.layout.simple_list_item_2, R.id.label, data) {
    private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text?.text = data[position]
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}