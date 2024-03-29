package by.carkva_gazeta.malitounik

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.PashaliiBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemPaschaliiBinding
import java.util.Calendar
import java.util.GregorianCalendar

class MenuPashalii : BaseFragment() {
    private val pasxi = ArrayList<Pashalii>()
    private lateinit var myArrayAdapter: MyArrayAdapter
    private var _binding: PashaliiBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("year", pasxi[0].search)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PashaliiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            if (savedInstanceState == null) setArrayPasha(it)
            else setArrayPasha(it, savedInstanceState.getInt("year"))
            myArrayAdapter = MyArrayAdapter(it, pasxi)
            binding.pasha.adapter = myArrayAdapter
            binding.pasha.selector = ContextCompat.getDrawable(it, android.R.color.transparent)
            binding.pasha.isClickable = false
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                binding.gri.setBackgroundResource(R.color.colorbackground_material_dark)
                binding.ula.setBackgroundResource(R.color.colorbackground_material_dark)
                binding.pasha.setBackgroundResource(R.color.colorbackground_material_dark)
                binding.pasha.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            }
        }
    }

    fun setPasha(year: Int) {
        activity?.let {
            pasxi.clear()
            setArrayPasha(it, year)
            myArrayAdapter.notifyDataSetChanged()
        }
    }

    private fun setArrayPasha(context: Activity, year: Int = Calendar.getInstance()[Calendar.YEAR]) {
        var yearG = year
        val yearG2: Int
        if (1586 <= yearG) {
            yearG -= 3
            yearG2 = yearG + 9
        } else {
            yearG2 = yearG + 9
        }
        var dataP: Int
        var monthP: Int
        var dataPrav: Int
        var monthPrav: Int
        val monthName = context.resources.getStringArray(R.array.meciac_smoll)
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
            if (katolic[Calendar.DAY_OF_YEAR] == pravas[Calendar.DAY_OF_YEAR]) sovpadenie = true
            pasxi.add(Pashalii(dataP.toString() + " " + monthName[monthP - 1] + " " + i, pravas[Calendar.DATE].toString() + " " + monthName[pravas[Calendar.MONTH]], i, year, sovpadenie))
        }
    }

    private class MyArrayAdapter(private val context: Activity, private val pasxi: ArrayList<Pashalii>) : ArrayAdapter<Pashalii>(context, R.layout.simple_list_item_sviaty, pasxi) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val ea: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemPaschaliiBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                ea = ViewHolder(binding.label)
                rootView.tag = ea
            } else {
                rootView = convertView
                ea = rootView.tag as ViewHolder
            }
            var color = R.color.colorPrimary_text
            var colorP = R.color.colorPrimary
            if ((context as BaseActivity).getBaseDzenNoch()) {
                ea.textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                color = R.color.colorWhite
                colorP = R.color.colorPrimary_black
            }
            val c = Calendar.getInstance()
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
            ea.textView.text = pasxa
            return rootView
        }
    }

    private class ViewHolder(var textView: TextView)

    private class Pashalii(val katolic: String, val pravas: String, val katolicYear: Int, val search: Int, val sovpadenie: Boolean)
}