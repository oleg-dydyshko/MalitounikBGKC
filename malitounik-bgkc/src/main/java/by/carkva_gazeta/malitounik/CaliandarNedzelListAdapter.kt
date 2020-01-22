package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import java.util.*

internal class CaliandarNedzelListAdapter(private val mContext: Activity, arrayLists: ArrayList<ArrayList<String>>, strings: Array<String?>) : ArrayAdapter<String>(mContext, R.layout.calaindar_nedel, strings) {
    private val dannye: ArrayList<ArrayList<String>> = arrayLists
    private val c: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
    private val chin: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    private val munName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня", "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
    private val nedelName = arrayOf("", "нядзеля", "панядзелак", "аўторак", "серада", "чацьвер", "пятніца", "субота")
    @SuppressLint("SetTextI18n")

    override fun getView(position: Int, rootView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (rootView == null) {
            view = mContext.layoutInflater.inflate(R.layout.calaindar_nedel, parent, false)
            viewHolder = ViewHolder()
            view.tag = viewHolder
            viewHolder.textCalendar = view.findViewById(R.id.textCalendar)
            viewHolder.textPraz = view.findViewById(R.id.textCviatyGlavnyia)
            viewHolder.textSviat = view.findViewById(R.id.textSviatyia)
            viewHolder.textPostS = view.findViewById(R.id.textPost)
            viewHolder.linearLayout = view.findViewById(R.id.linearView)
        } else {
            view = rootView
            viewHolder = view.tag as ViewHolder
        }
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        //по умолчанию
        viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
        viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDivider))
        viewHolder.textSviat?.visibility = View.VISIBLE
        viewHolder.textPraz?.visibility = View.GONE
        viewHolder.textPostS?.visibility = View.GONE
        viewHolder.textPraz?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        viewHolder.textPraz?.setTypeface(null, Typeface.BOLD)
        if (dzenNoch) {
            viewHolder.textSviat?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            viewHolder.textPraz?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_black))
        }
        if (c[Calendar.YEAR] == dannye[position][3].toInt() && c[Calendar.DATE] == dannye[position][1].toInt() && c[Calendar.MONTH] == dannye[position][2].toInt()) {
            if (dzenNoch) viewHolder.linearLayout?.setBackgroundResource(R.drawable.calendar_nedel_today_black) else viewHolder.linearLayout?.setBackgroundResource(R.drawable.calendar_nedel_today)
        } else {
            viewHolder.linearLayout?.setBackgroundResource(0)
        }
        if (dannye[position][3].toInt() != c[Calendar.YEAR]) viewHolder.textCalendar?.text = nedelName[dannye[position][0].toInt()] + " " + dannye[position][1] + " " + munName[dannye[position][2].toInt()] + ", " + dannye[position][3] else viewHolder.textCalendar?.text = nedelName[dannye[position][0].toInt()] + " " + dannye[position][1] + " " + munName[dannye[position][2].toInt()]
        //viewHolder.textPraz.setText(dannye.get(position).get(3)); Год
        var sviatyia = dannye[position][4]
        if (dzenNoch) {
            sviatyia = sviatyia.replace("#d00505", "#f44336")
        }
        viewHolder.textSviat?.text = MainActivity.fromHtml(sviatyia)
        if (dannye[position][4].contains("no_sviatyia")) viewHolder.textSviat?.visibility = View.GONE
        viewHolder.textPraz?.text = dannye[position][6]
        if (!dannye[position][6].contains("no_sviaty")) viewHolder.textPraz?.visibility = View.VISIBLE
        // убот = субота
        if (dannye[position][6].contains("Пачатак") || dannye[position][6].contains("Вялікі") || dannye[position][6].contains("Вялікая") || dannye[position][6].contains("убот") || dannye[position][6].contains("ВЕЧАР") || dannye[position][6].contains("Палова")) {
            viewHolder.textPraz?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textPraz?.setTypeface(null, Typeface.NORMAL)
        }
        if (dannye[position][5].contains("1") || dannye[position][5].contains("2") || dannye[position][5].contains("3")) {
            viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            if (dzenNoch) viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary_black)) else viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        } else if (dannye[position][7].contains("2")) {
            viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
            viewHolder.textPostS?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textPostS?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
            viewHolder.textPostS?.text = mContext.resources.getString(R.string.Post)
        } else if (dannye[position][7].contains("3")) {
            viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
        } else if (dannye[position][7].contains("1")) {
            viewHolder.textCalendar?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textCalendar?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
            viewHolder.textPostS?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textPostS?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
            viewHolder.textPostS?.text = mContext.resources.getString(R.string.No_post)
        }
        if (dannye[position][5].contains("2")) {
            viewHolder.textPraz?.setTypeface(null, Typeface.NORMAL)
        }
        if (dannye[position][7].contains("3")) {
            viewHolder.textPostS?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            viewHolder.textPostS?.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
            viewHolder.textPostS?.text = mContext.resources.getString(R.string.Strogi_post)
            viewHolder.textPostS?.visibility = View.VISIBLE
        } else if (dannye[position][0].contains("6")) { // Пятница
            viewHolder.textPostS?.visibility = View.VISIBLE
        }
        return view
    }

    private class ViewHolder {
        var linearLayout: LinearLayout? = null
        var textCalendar: TextViewRobotoCondensed? = null
        var textPraz: TextViewRobotoCondensed? = null
        var textSviat: TextViewRobotoCondensed? = null
        var textPostS: TextViewRobotoCondensed? = null
    }
}