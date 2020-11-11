package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.pashalii.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by oleg on 31.5.16
 */
class MenuPashalii : PashaliiFragment() {
    private val pasxi = ArrayList<Pashalii>()
    private lateinit var myArrayAdapter: MyArrayAdapter
    private var mLastClickTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("year", pasxi[0].search)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.pashalii, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val titlespan = SpannableString(title.text)
            titlespan.setSpan(UnderlineSpan(), 0, titlespan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.text = titlespan
            if (dzenNoch) {
                title.setBackgroundResource(R.drawable.selector_dark)
                //gri.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                title.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            } else {
                title.setBackgroundResource(R.drawable.selector_default)
            }
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            gri.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            ula.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
            title.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val intent = Intent(activity, Pasxa::class.java)
                startActivity(intent)
            }
            if (savedInstanceState == null)
                setArrayPasha()
            else
                setArrayPasha(savedInstanceState.getInt("year"))
            myArrayAdapter = MyArrayAdapter(it)
            pasha.adapter = myArrayAdapter
            pasha.selector = ContextCompat.getDrawable(it, android.R.color.transparent)
            pasha.isClickable = false
        }
    }

    override fun setPasha(year: Int) {
        pasxi.clear()
        setArrayPasha(year)
        myArrayAdapter.notifyDataSetChanged()
    }

    private fun setArrayPasha(year: Int = Calendar.getInstance()[Calendar.YEAR]) {
        val c = Calendar.getInstance() as GregorianCalendar
        var yearG = year
        val yearG2: Int
        if (c[Calendar.YEAR] == yearG) {
            yearG -= 3
            yearG2 = yearG + 13
        } else {
            yearG2 = yearG + 10
        }
        var dataP: Int
        var monthP: Int
        var dataPrav: Int
        var monthPrav: Int
        val monthName = arrayOf("студзеня", "лютага", "сакавіка", "красавіка", "траўня", "чэрвеня",
                "ліпеня", "жніўня", "верасьня", "кастрычніка", "лістапада", "сьнежня")
        for (i in yearG..yearG2) {
            val a = i % 19
            val b = i % 4
            val cx = i % 7
            val k = i / 100
            val p = (13 + 8 * k) / 25
            val q = k / 4
            val m = (15 - p + k - q) % 30
            val n = (4 + k - q) % 7
            val d = (19 * a + m) % 30
            val ex = (2 * b + 4 * cx + 6 * d + n) % 7
            if (d + ex <= 9) {
                dataP = d + ex + 22
                monthP = 3
            } else {
                dataP = d + ex - 9
                if (d == 29 && ex == 6) dataP = 19
                if (d == 28 && ex == 6) dataP = 18
                monthP = 4
            }
            val a2 = (19 * (i % 19) + 15) % 30
            val b2 = (2 * (i % 4) + 4 * (i % 7) + 6 * a2 + 6) % 7
            if (a2 + b2 > 9) {
                dataPrav = a2 + b2 - 9
                monthPrav = 4
            } else {
                dataPrav = 22 + a2 + b2
                monthPrav = 3
            }
            val pravas = GregorianCalendar(i, monthPrav - 1, dataPrav)
            val katolic = GregorianCalendar(i, monthP - 1, dataP)
            val vek = yearG.toString().substring(0, 2)
            if (vek == "15" || vek == "16") pravas.add(Calendar.DATE, 10)
            if (vek == "17") pravas.add(Calendar.DATE, 11)
            if (vek == "18") pravas.add(Calendar.DATE, 12)
            if (vek == "19" || vek == "20") pravas.add(Calendar.DATE, 13)
            var sovpadenie = false
            if (katolic[Calendar.DAY_OF_YEAR] == pravas[Calendar.DAY_OF_YEAR])
                sovpadenie = true
            pasxi.add(Pashalii(dataP.toString() + " " + monthName[monthP - 1] + " " + i, pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]], i, year, sovpadenie))
        }
    }

    private inner class MyArrayAdapter(private val context: Activity) : ArrayAdapter<Pashalii>(context, R.layout.simple_list_item_sviaty, pasxi) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val ea: ViewHolder
            if (convertView == null) {
                ea = ViewHolder()
                rootView = context.layoutInflater.inflate(R.layout.simple_list_item_sviaty, parent, false)
                ea.textView = rootView.findViewById(R.id.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            ea.textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            var color = R.color.colorPrimary_text
            var colorP = R.color.colorPrimary
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
                //ea.textView?.setBackgroundResource(R.drawable.selector_dark)
                color = R.color.colorIcons
                colorP = R.color.colorPrimary_black
            } /*else {
                ea.textView?.setBackgroundResource(R.drawable.selector_white)
            }*/
            val c = Calendar.getInstance() as GregorianCalendar
            val pasxa = SpannableStringBuilder(pasxi[position].katolic)
            if (!pasxi[position].sovpadenie) {
                pasxa.append("\n${pasxi[position].pravas}")
                pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), pasxi[position].katolic.length, pasxa.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            when (pasxi[position].katolicYear) {
                c[Calendar.YEAR] -> {
                    pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, colorP)), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    pasxa.setSpan(StyleSpan(Typeface.BOLD), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                pasxi[position].search -> {
                    pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    pasxa.setSpan(StyleSpan(Typeface.BOLD), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                else -> {
                    pasxa.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), 0, pasxi[position].katolic.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            ea.textView?.text = pasxa
            return rootView
        }
    }

    private class ViewHolder {
        var textView: TextViewRobotoCondensed? = null
    }

    private class Pashalii(val katolic: String, val pravas: String, val katolicYear: Int, val search: Int, val sovpadenie: Boolean)
}