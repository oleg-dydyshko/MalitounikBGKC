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
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSabytieShowInMunBinding
import java.util.Calendar
import java.util.GregorianCalendar

class DialogSabytieShowInMun : DialogFragment() {
    private var dayYear = 1
    private var date = 1
    private var mun = 0
    private var year = 0
    private var gosSviata = ""
    private var svity = ""
    private var svityRKC = ""
    private lateinit var alert: AlertDialog
    private var _binding: DialogSabytieShowInMunBinding? = null
    private val binding get() = _binding!!
    private val dzenNoch: Boolean
        get() {
            activity?.let {
                return (it as BaseActivity).getBaseDzenNoch()
            }
            return false
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayYear = arguments?.getInt("dayYear", 1) ?: 1
        date = arguments?.getInt("date", 1) ?: 1
        mun = arguments?.getInt("mun", 0) ?: 0
        year = arguments?.getInt("year", 0) ?: 0
        svity = arguments?.getString("svity", "") ?: ""
        svityRKC = arguments?.getString("svityRKC", "") ?: ""
        gosSviata = arguments?.getString("gosSviata", "") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogSabytieShowInMunBinding.inflate(layoutInflater)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            if (gosSviata.isNotEmpty()) {
                binding.textViewGosSvity.text = gosSviata
                binding.textViewGosSvity.visibility = View.VISIBLE
            }
            if (svity.isNotEmpty()) {
                binding.textViewSvity.text = svity
                binding.textViewSvity.visibility = View.VISIBLE
            }
            if (svityRKC.isNotEmpty()) {
                binding.textViewSvityRKC.text = svityRKC
                binding.textViewSvityRKC.visibility = View.VISIBLE
            }
            if (dzenNoch) {
                binding.textViewSvity.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textViewGosSvity.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            }
            binding.title.text = getString(R.string.padzei_i_sviaty, date, it.resources.getStringArray(R.array.meciac_smoll)[mun])
            val isSabytie = sabytieView(dayYear)
            if (gosSviata.isEmpty() && svity.isEmpty() && svityRKC.isEmpty() && !isSabytie) {
                binding.textViewSvityRKC.text = getString(R.string.padzei_no)
                binding.textViewSvityRKC.visibility = View.VISIBLE
            }
            val ad = AlertDialog.Builder(it, style)
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = ad.create()
        }
        return alert
    }

    private fun sabytieView(dayYear: Int): Boolean {
        val sabytieList = ArrayList<TextView>()
        activity?.let {
            val density = (resources.displayMetrics.density).toInt()
            val gc = Calendar.getInstance() as GregorianCalendar
            var title: String
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
                    if (gc[Calendar.DAY_OF_YEAR] - 1 == dayYear && gc[Calendar.YEAR] == year) {
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
                        val textViewT = TextViewCustom(it)
                        textViewT.text = title
                        textViewT.setPadding(10 * density, 5 * density, 5 * density, 5 * density)
                        textViewT.typeface = MainActivity.createFont(Typeface.BOLD)
                        textViewT.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(Sabytie.getColors(p.color)))
                        sabytieList.add(textViewT)
                        val textView = TextViewCustom(it)
                        textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        textView.setPadding(10 * density, 0, 10 * density, 10 * density)
                        if (dzenNoch) {
                            textView.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                            textView.setBackgroundResource(R.color.colorbackground_material_dark)
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
                        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        llp.setMargins(0, 0, 0, 5 * density)
                        textView.layoutParams = llp
                        sabytieList.add(textView)
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
        return sabytieList.size > 0
    }

    companion object {
        fun getInstance(dayYear: Int, date: Int, mun: Int, year: Int, svity: String, svityRKC: String, gosSviata: String): DialogSabytieShowInMun {
            val dialogShowSabytie = DialogSabytieShowInMun()
            val bundle = Bundle()
            bundle.putInt("dayYear", dayYear)
            bundle.putInt("date", date)
            bundle.putInt("mun", mun)
            bundle.putInt("year", year)
            bundle.putString("svity", svity)
            bundle.putString("svityRKC", svityRKC)
            bundle.putString("gosSviata", gosSviata)
            dialogShowSabytie.arguments = bundle
            return dialogShowSabytie
        }
    }
}