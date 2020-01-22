package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.pashalii.*
import java.util.*

/**
 * Created by oleg on 31.5.16
 */
class MenuPashalii : Fragment() {
    private var yearG = 0
    private var yearG2 = 0
    private var seache = 0
    private var mLastClickTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val c = Calendar.getInstance()
        if (arguments != null) {
            yearG = arguments?.getInt("Year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
            seache = yearG
            yearG2 = yearG + 10
        } else {
            yearG = c[Calendar.YEAR] - 3
            yearG2 = yearG + 20
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.pashalii, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val c = Calendar.getInstance()
            val yearfull = c[Calendar.YEAR]
            title.text = MainActivity.fromHtml("<u>" + resources.getString(R.string.pascha_kaliandar_bel) + "</u>")
            if (dzenNoch) {
                gri.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                title.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
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
                val textView1 = TextViewRobotoCondensed(it)
                textView1.isFocusable = false
                pasha.addView(textView1)
                textView1.text = dataP.toString() + " " + monthName[monthP - 1] + " " + i
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                if (yearfull == i) {
                    textView1.setTypeface(null, Typeface.BOLD)
                    if (dzenNoch) textView1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textView1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
                } else if (seache == i) {
                    textView1.setTypeface(null, Typeface.BOLD)
                    if (dzenNoch) textView1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                } else {
                    textView1.setTypeface(null, Typeface.NORMAL)
                    if (dzenNoch) textView1.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                }
                textView1.setPadding(0, 10, 0, 0)
                val textView2 = TextViewRobotoCondensed(it)
                textView2.isFocusable = false
                if (pravas[Calendar.DAY_OF_YEAR] != katolic[Calendar.DAY_OF_YEAR]) {
                    pasha.addView(textView2)
                    textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                    textView2.text = pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]]
                    textView2.setTextColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                }
                val view = View(it)
                val linLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                linLayoutParam.height = 1
                view.setBackgroundColor(ContextCompat.getColor(it, R.color.colorSecondary_text))
                pasha.addView(view, linLayoutParam)
            }
        }
    }

    companion object {
        fun newInstance(): MenuPashalii {
            return MenuPashalii()
        }

        fun newInstance(year: Int): MenuPashalii {
            val fragmentFirst = MenuPashalii()
            val args = Bundle()
            args.putInt("Year", year)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}