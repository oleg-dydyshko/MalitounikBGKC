package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.CalaindarNedelBinding
import by.carkva_gazeta.malitounik.databinding.CaliandarNedzeliaBinding
import java.util.Calendar
import java.util.GregorianCalendar

class CaliandarNedzel : BaseFragment(), AdapterView.OnItemClickListener {
    private var year = 0
    private var mun = 0
    private var dateInt = 0
    private val niadzelia = ArrayList<ArrayList<String>>()
    private var _binding: CaliandarNedzeliaBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        year = arguments?.getInt("year") ?: SettingsActivity.GET_CALIANDAR_YEAR_MIN
        mun = arguments?.getInt("mun") ?: 0
        dateInt = arguments?.getInt("date") ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CaliandarNedzeliaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        niadzelia.addAll(MenuCaliandar.getDataCalaindar(dateInt, mun, year))
        activity?.let {
            binding.listView.adapter = CaliandarNedzelListAdapter(it, niadzelia)
            binding.listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_default)
        }
        val c = GregorianCalendar(year, mun, dateInt)
        val cReal = Calendar.getInstance()
        cReal[Calendar.DATE] = dateInt
        if (c[Calendar.YEAR] == cReal[Calendar.YEAR] && c[Calendar.DAY_OF_YEAR] == cReal[Calendar.DAY_OF_YEAR]) {
            binding.listView.setSelection(c[Calendar.DAY_OF_WEEK] - 1)
        }
        binding.listView.isVerticalScrollBarEnabled = false
        binding.listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent()
        intent.putExtra("position", niadzelia[position][25].toInt())
        activity?.let {
            it.setResult(Activity.RESULT_OK, intent)
            it.finish()
        }
    }

    private class CaliandarNedzelListAdapter(private val mContext: Context, private val niadzelia: ArrayList<ArrayList<String>>) : ArrayAdapter<ArrayList<String>>(mContext, R.layout.calaindar_nedel, niadzelia) {
        private val c = Calendar.getInstance()
        private val munName = mContext.resources.getStringArray(R.array.meciac_smoll)
        private val nedelName = mContext.resources.getStringArray(R.array.dni_nedeli)

        override fun getView(position: Int, rootView: View?, parent: ViewGroup): View {
            val view: View
            val viewHolder: ViewHolder
            if (rootView == null) {
                val binding = CalaindarNedelBinding.inflate(LayoutInflater.from(context), parent, false)
                view = binding.root
                viewHolder = ViewHolder(binding.textCalendar, binding.textCviatyGlavnyia, binding.textSviatyia, binding.textPost, binding.linearView)
                view.tag = viewHolder
            } else {
                view = rootView
                viewHolder = view.tag as ViewHolder
            }
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
            viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDivider))
            viewHolder.textSviat.visibility = View.VISIBLE
            viewHolder.textPraz.visibility = View.GONE
            viewHolder.textPostS.visibility = View.GONE
            viewHolder.textPraz.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            viewHolder.textPraz.typeface = MainActivity.createFont(Typeface.BOLD)
            if (dzenNoch) {
                viewHolder.textSviat.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                viewHolder.textPraz.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_black))
            }
            if (c[Calendar.YEAR] == niadzelia[position][3].toInt() && c[Calendar.DATE] == niadzelia[position][1].toInt() && c[Calendar.MONTH] == niadzelia[position][2].toInt()) {
                if (dzenNoch) viewHolder.linearLayout.setBackgroundResource(R.drawable.calendar_nedel_today_black)
                else viewHolder.linearLayout.setBackgroundResource(R.drawable.calendar_nedel_today)
            } else {
                if (dzenNoch) viewHolder.linearLayout.setBackgroundResource(R.drawable.selector_dark)
                else viewHolder.linearLayout.setBackgroundResource(R.drawable.selector_default)
            }
            if (niadzelia[position][3].toInt() != c[Calendar.YEAR]) viewHolder.textCalendar.text = mContext.getString(R.string.tydzen_name3, nedelName[niadzelia[position][0].toInt()], niadzelia[position][1], munName[niadzelia[position][2].toInt()], niadzelia[position][3])
            else viewHolder.textCalendar.text = mContext.getString(R.string.tydzen_name2, nedelName[niadzelia[position][0].toInt()], niadzelia[position][1], munName[niadzelia[position][2].toInt()])
            var sviatyia = niadzelia[position][4]
            if (dzenNoch) {
                sviatyia = sviatyia.replace("#d00505", "#ff6666")
            }
            viewHolder.textSviat.text = MainActivity.fromHtml(sviatyia)
            if (niadzelia[position][4].contains("no_sviatyia")) viewHolder.textSviat.visibility = View.GONE
            viewHolder.textPraz.text = MainActivity.fromHtml(niadzelia[position][6])
            if (!niadzelia[position][6].contains("no_sviaty")) viewHolder.textPraz.visibility = View.VISIBLE // убот = субота
            if (niadzelia[position][6].contains("Пачатак") || niadzelia[position][6].contains("Вялікі") || niadzelia[position][6].contains("Вялікая") || niadzelia[position][6].contains("убот") || niadzelia[position][6].contains("ВЕЧАР") || niadzelia[position][6].contains("Палова")) {
                if (dzenNoch) viewHolder.textPraz.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                else viewHolder.textPraz.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                viewHolder.textPraz.typeface = MainActivity.createFont(Typeface.NORMAL)
            }
            when (niadzelia[position][7].toInt()) {
                1 -> {
                    viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
                    viewHolder.textPostS.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textPostS.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBezPosta))
                    viewHolder.textPostS.text = mContext.resources.getString(R.string.No_post_n)
                }
                2 -> {
                    viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
                    viewHolder.textPostS.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary_text))
                    viewHolder.textPostS.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPost))
                    viewHolder.textPostS.text = mContext.resources.getString(R.string.Post)
                }
                3 -> {
                    viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                    viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
                }
            }
            if (niadzelia[position][5].contains("1") || niadzelia[position][5].contains("2") || niadzelia[position][5].contains("3")) {
                viewHolder.textCalendar.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                viewHolder.textCalendar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            }
            if (niadzelia[position][5].contains("2")) {
                viewHolder.textPraz.typeface = MainActivity.createFont(Typeface.NORMAL)
            }
            if (niadzelia[position][7].contains("3")) {
                viewHolder.textPostS.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                viewHolder.textPostS.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorStrogiPost))
                viewHolder.textPostS.text = mContext.resources.getString(R.string.Strogi_post_n)
                viewHolder.textPostS.visibility = View.VISIBLE
            } else if (niadzelia[position][0].contains("6") && !(niadzelia[position][5].contains("1") || niadzelia[position][5].contains("2"))) { // Пятница
                viewHolder.textPostS.visibility = View.VISIBLE
            }
            return view
        }
    }

    private class ViewHolder(var textCalendar: TextView, var textPraz: TextView, var textSviat: TextView, var textPostS: TextView, var linearLayout: LinearLayout)

    companion object {
        fun newInstance(year: Int, mun: Int, date: Int): CaliandarNedzel {
            val fragment = CaliandarNedzel()
            val args = Bundle()
            args.putInt("year", year)
            args.putInt("mun", mun)
            args.putInt("date", date)
            fragment.arguments = args
            return fragment
        }
    }
}