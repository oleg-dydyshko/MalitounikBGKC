package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class HistoryAdapter(private var context: Activity, private var history: ArrayList<String>, private var spannable: Boolean = false) : ArrayAdapter<String>(context, R.layout.simple_list_item_history, history) {
    private val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
        val rootView: View
        val viewHolder: ViewHolderHistory
        if (mView == null) {
            rootView = context.layoutInflater.inflate(R.layout.simple_list_item_history, parent, false)
            viewHolder = ViewHolderHistory()
            rootView.tag = viewHolder
            viewHolder.text = rootView.findViewById(R.id.item)
            viewHolder.rootView = rootView.findViewById(R.id.layout)
            viewHolder.image = rootView.findViewById(R.id.search)
        } else {
            rootView = mView
            viewHolder = rootView.tag as ViewHolderHistory
        }
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (spannable)
            viewHolder.text?.text = MainActivity.fromHtml(history[position])
        else
            viewHolder.text?.text = history[position]
        viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        if (dzenNoch) {
            //viewHolder.rootView?.setBackgroundResource(R.drawable.selector_dark)
            //viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
            //viewHolder.text?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            viewHolder.image?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.search))
        } /*else {
            viewHolder.text?.setBackgroundResource(R.drawable.selector_white)
        }*/
        return rootView
    }

    private class ViewHolderHistory {
        var rootView: ConstraintLayout? = null
        var text: TextViewRobotoCondensed? = null
        var image: ImageView? = null
    }
}