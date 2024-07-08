package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.CytatyFragmentBinding

class PiarlinyAllFragment : BaseFragment() {

    private var data = ""
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
        data = arguments?.getString("data", "") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BaseActivity)?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = activity.getBaseDzenNoch()
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            binding.TextView.text = MainActivity.fromHtml(data)
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
        fun newInstance(data: String): PiarlinyAllFragment {
            val fragmentFirst = PiarlinyAllFragment()
            val args = Bundle()
            args.putString("data", data)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}