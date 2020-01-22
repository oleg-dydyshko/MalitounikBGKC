package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import java.util.*

/**
 * Created by oleg on 14.11.16
 */
internal class ListAdapter(private val mContext: Activity, private val itemsL: ArrayList<Int>) : ArrayAdapter<Int>(mContext, R.layout.simple_list_item_1, itemsL) {
    private val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    private val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
    private val gc = Calendar.getInstance() as GregorianCalendar
    override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
        val rootView: View
        val viewHolder: ViewHolder
        if (mView == null) {
            rootView = mContext.layoutInflater.inflate(R.layout.simple_list_item_1, parent, false)
            viewHolder = ViewHolder()
            rootView.tag = viewHolder
            viewHolder.text = rootView.findViewById(R.id.text1)
        } else {
            rootView = mView
            viewHolder = rootView.tag as ViewHolder
        }
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (gc[Calendar.YEAR] == itemsL[position]) viewHolder.text?.setTypeface(null, Typeface.BOLD) else viewHolder.text?.setTypeface(null, Typeface.NORMAL)
        viewHolder.text?.text = itemsL[position].toString()
        viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (dzenNoch) {
            viewHolder.text?.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
        }
        return rootView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getDropDownView(position, convertView, parent)
        val dzenNoch = k.getBoolean("dzen_noch", false)
        val text: TextViewRobotoCondensed = v.findViewById(R.id.text1)
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        if (gc[Calendar.YEAR] == itemsL[position]) text.setTypeface(null, Typeface.BOLD) else text.setTypeface(null, Typeface.NORMAL)
        text.text = itemsL[position].toString()
        if (dzenNoch) {
            text.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
            text.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
        } else {
            text.setBackgroundResource(R.color.colorIcons)
        }
        return v
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}