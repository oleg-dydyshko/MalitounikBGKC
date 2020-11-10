package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Created by oleg on 17.1.18
 */
class MenuListAdaprer : ArrayAdapter<String> {
    private val mContext: Activity
    private var items: Array<String>? = null
    private var itemsL: ArrayList<String>? = null
    private val k: SharedPreferences

    constructor(context: Activity, strings: Array<String>) : super(context, R.layout.simple_list_item_2, R.id.label, strings) {
        mContext = context
        items = strings
        k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    }

    constructor(context: Activity, strings: ArrayList<String>) : super(context, R.layout.simple_list_item_2, R.id.label, strings) {
        mContext = context
        itemsL = strings
        k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    }

    override fun add(string: String?) {
        super.add(string)
        itemsL?.add(string?: "")
    }

    override fun remove(string: String?) {
        super.remove(string)
        itemsL?.remove(string)
    }

    override fun clear() {
        super.clear()
        itemsL?.clear()
    }

    override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
        val rootView: View
        val viewHolder: ViewHolder
        if (mView == null) {
            rootView = mContext.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
            viewHolder = ViewHolder()
            rootView.tag = viewHolder
            viewHolder.text = rootView.findViewById(R.id.label)
        } else {
            rootView = mView
            viewHolder = rootView.tag as ViewHolder
        }
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (itemsL != null)
            viewHolder.text?.text = itemsL?.get(position)
        if (items != null)
            viewHolder.text?.text = items?.get(position)
        viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        if (dzenNoch) {
            //viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
            //viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        } /*else {
            viewHolder.text?.setBackgroundResource(R.drawable.selector_white)
        }*/
        return rootView
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}