package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSabytieShowInMunBinding
import java.util.*
import kotlin.collections.ArrayList

class DialogSabytieShowInMun : DialogFragment() {
    private var dayYear = 1
    private var year = 0
    private lateinit var alert: AlertDialog
    private var _binding: DialogSabytieShowInMunBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayYear = arguments?.getInt("dayYear") ?: 1
        year = arguments?.getInt("year") ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogSabytieShowInMunBinding.inflate(LayoutInflater.from(it))
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            sabytieView(dayYear)
            val ad = AlertDialog.Builder(it, style)
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    private fun sabytieView(DayYear: Int) {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            binding.linearLayout.removeAllViewsInLayout()
            val density = (resources.displayMetrics.density).toInt()
            val gc = Calendar.getInstance() as GregorianCalendar
            var title: String
            val sabytieList = ArrayList<TextView>()
            for (index in 0 until MainActivity.padzeia.size) {
                val p = MainActivity.padzeia[index]
                val r1 = p.dat.split(".")
                val r2 = p.datK.split(".")
                gc[r1[2].toInt(), r1[1].toInt() - 1] = r1[0].toInt()
                val naY = gc[Calendar.YEAR]
                val na = gc[Calendar.DAY_OF_YEAR]
                gc[r2[2].toInt(), r2[1].toInt() - 1] = r2[0].toInt()
                val yaerw = gc[Calendar.YEAR]
                val kon = gc[Calendar.DAY_OF_YEAR]
                var rezkK = kon - na + 1
                if (yaerw > naY) {
                    var leapYear = 365
                    if (gc.isLeapYear(naY)) leapYear = 366
                    rezkK = leapYear - na + kon
                }
                gc[r1[2].toInt(), r1[1].toInt() - 1] = r1[0].toInt()
                for (i in 0 until rezkK) {
                    if (gc[Calendar.DAY_OF_YEAR] - 1 == DayYear && gc[Calendar.YEAR] == year) {
                        title = p.padz
                        val data = p.dat
                        val time = p.tim
                        val dataK = p.datK
                        val timeK = p.timK
                        val paz = p.paznic
                        var res = getString(R.string.sabytie_no_pavedam)
                        val konecSabytie = p.konecSabytie
                        val realTime = Calendar.getInstance().timeInMillis
                        var paznicia = false
                        if (paz != 0L) {
                            gc.timeInMillis = paz
                            var nol1 = ""
                            var nol2 = ""
                            var nol3 = ""
                            if (gc[Calendar.DATE] < 10) nol1 = "0"
                            if (gc[Calendar.MONTH] < 9) nol2 = "0"
                            if (gc[Calendar.MINUTE] < 10) nol3 = "0"
                            res = getString(R.string.sabytie_pavedam, nol1, gc[Calendar.DAY_OF_MONTH], nol2, gc[Calendar.MONTH] + 1, gc[Calendar.YEAR], gc[Calendar.HOUR_OF_DAY], nol3, gc[Calendar.MINUTE])
                            if (realTime > paz) paznicia = true
                        }
                        val textViewT = TextView(it)
                        textViewT.text = title
                        textViewT.setPadding(10 * density, 10 * density, 10 * density, 10 * density)
                        textViewT.typeface = MainActivity.createFont(Typeface.BOLD)
                        textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
                        textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(Sabytie.getColors(p.color)))
                        sabytieList.add(textViewT)
                        val textView = TextView(it)
                        textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        textView.setPadding(10 * density, 0, 10 * density, 10 * density)
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
                        if (dzenNoch) {
                            textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                            textView.setBackgroundResource(R.color.colorprimary_material_dark)
                        }
                        val textR = if (!konecSabytie) {
                            getString(R.string.sabytieKali, data, time, res)
                        } else {
                            getString(R.string.sabytieDoKuda, data, time, dataK, timeK, res)
                        }
                        val t1 = textR.lastIndexOf("\n")
                        val spannable = SpannableString(textR.substring(0, t1))
                        val t3 = spannable.indexOf(res)
                        if (dzenNoch) {
                            if (paznicia) spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary_black)), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        } else {
                            if (paznicia) spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary)), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val am = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            if (!am.canScheduleExactAlarms() && res != getString(R.string.sabytie_no_pavedam)) {
                                spannable.setSpan(StrikethroughSpan(), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                        val font = MainActivity.createFont(Typeface.NORMAL)
                        spannable.setSpan(CustomTypefaceSpan("", font), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                        textView.text = spannable
                        sabytieList.add(textView)
                        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        llp.setMargins(0, 0, 0, 5 * density)
                        textView.layoutParams = llp
                    }
                    gc.add(Calendar.DATE, 1)
                }
            }
            if (sabytieList.size > 0) {
                for (i in sabytieList.indices) {
                    binding.linearLayout.addView(sabytieList[i])
                }
            }
        }
    }

    companion object {
        fun getInstance(dayYear: Int, year: Int): DialogSabytieShowInMun {
            val dialogShowSabytie = DialogSabytieShowInMun()
            val bundle = Bundle()
            bundle.putInt("dayYear", dayYear)
            bundle.putInt("year", year)
            dialogShowSabytie.arguments = bundle
            return dialogShowSabytie
        }
    }
}