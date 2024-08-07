package by.carkva_gazeta.malitounik

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding

class MenuListAdaprer : ArrayAdapter<String> {
    private val mContext: Activity
    private var items: Array<out String>? = null
    private var itemsL: ArrayList<String>? = null

    constructor(context: Activity, strings: Array<out String>) : super(context, R.layout.simple_list_item_2, R.id.label, strings) {
        mContext = context
        items = strings
    }

    constructor(context: Activity, strings: ArrayList<String>) : super(context, R.layout.simple_list_item_2, R.id.label, strings) {
        mContext = context
        itemsL = strings
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
            val binding = SimpleListItem2Binding.inflate(mContext.layoutInflater, parent, false)
            rootView = binding.root
            viewHolder = ViewHolder(binding.label)
            rootView.tag = viewHolder
        } else {
            rootView = mView
            viewHolder = rootView.tag as ViewHolder
        }
        val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
        if (itemsL != null)
            viewHolder.text.text = itemsL?.get(position)
        if (items != null)
            viewHolder.text.text = items?.get(position)
        if (dzenNoch)
            viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
        return rootView
    }

    private class ViewHolder(var text: TextView)
}