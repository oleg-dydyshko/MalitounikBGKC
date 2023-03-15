package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import by.carkva_gazeta.malitounik.databinding.CytatyFragmentBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class CytatyFragment : BaseFragment() {

    private var position = 0
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private var _binding: CytatyFragmentBinding? = null
    private val binding get() = _binding!!

    fun upDateTextView() {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            val range = when (position) {
                0 -> 0..9
                1 -> 10..19
                2 -> 20..29
                3 -> 30..39
                4 -> 40..49
                5 -> 50..59
                6 -> 60..69
                7 -> 70..79
                8 -> 80..89
                9 -> 90..99
                10 -> 100..109
                11 -> 110..111
                else -> 0..9
            }
            val inputStream = resources.openRawResource(R.raw.citata)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val text = SpannableStringBuilder()
            var index = 0
            reader.forEachLine {
                if (index in range) {
                    val line = SpannableStringBuilder()
                    val t1 = it.indexOf("(")
                    line.append(it.substring(0, t1).trim())
                    line.append("\n")
                    line.append(it.substring(t1))
                    line.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(activity, R.font.andantinoscript)), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    line.setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    if (dzenNoch) {
                        line.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary_black)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        line.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorDivider)), 1, line.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else line.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    line.setSpan(AbsoluteSizeSpan(30, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    line.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(activity, R.font.comici)), 1, line.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    if (index != range.last) line.append("\n\n")
                    text.append(line)
                }
                index++
            }

            binding.TextView.text = text
            if (dzenNoch) {
                binding.TextView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CytatyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(position: Int): CytatyFragment {
            val fragmentFirst = CytatyFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}