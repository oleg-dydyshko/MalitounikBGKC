package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SimpleListItemHistoryBinding

class HistoryAdapter(private var context: Activity, private var history: ArrayList<String>, private var spannable: Boolean = false) : ArrayAdapter<String>(context, R.layout.simple_list_item_history, history) {
    private val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
        val rootView: View
        val viewHolder: ViewHolderHistory
        if (mView == null) {
            val binding = SimpleListItemHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
            rootView = binding.root
            viewHolder = ViewHolderHistory(binding.item, binding.search)
            rootView.tag = viewHolder
        } else {
            rootView = mView
            viewHolder = rootView.tag as ViewHolderHistory
        }
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        if (spannable)
            viewHolder.text.text = MainActivity.fromHtml(history[position])
        else
            viewHolder.text.text = history[position]
        viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
        if (dzenNoch)
            viewHolder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.search))
        return rootView
    }

    private class ViewHolderHistory(var text: TextView, var image: ImageView)
}