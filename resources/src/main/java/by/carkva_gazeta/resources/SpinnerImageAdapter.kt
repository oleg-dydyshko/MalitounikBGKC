package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout

internal class SpinnerImageAdapter(private val activity: Activity, private val arrayList: Array<Int>) : ArrayAdapter<Int>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_image, arrayList) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            rootView = activity.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_image, parent, false)
            viewHolder = ViewHolder()
            rootView.tag = viewHolder
            viewHolder.imageView = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.image)
            viewHolder.linearLayout = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.rootView)
        } else {
            rootView = convertView
            viewHolder = rootView.tag as ViewHolder
        }
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        viewHolder.imageView?.setImageResource(arrayList[position])
        val dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch)
            viewHolder.linearLayout?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
        return rootView
    }

    override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
        val rootView: View
        val viewHolder: ViewHolder
        if (mView == null) {
            rootView = activity.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_image, parent, false)
            viewHolder = ViewHolder()
            rootView.tag = viewHolder
            viewHolder.imageView = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.image)
        } else {
            rootView = mView
            viewHolder = rootView.tag as ViewHolder
        }
        viewHolder.imageView?.visibility = View.GONE
        return rootView
    }

    private class ViewHolder {
        var imageView: ImageView? = null
        var linearLayout: LinearLayout? = null
    }
}