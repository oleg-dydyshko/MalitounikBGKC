package by.carkva_gazeta.resources

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BaseFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.resources.databinding.AkafistFragmentPasliaPrichBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class PasliaPrychasciaFragment : BaseFragment() {

    private var resursID = R.raw.paslia_prychascia1
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private var _binding: AkafistFragmentPasliaPrichBinding? = null
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
        resursID = arguments?.getInt("resursID") ?: R.raw.paslia_prychascia1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            binding.TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            val inputStream = activity.resources.openRawResource(resursID)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.use {
                line = it.readText()
            }
            val t1 = line.indexOf("<br><br>")
            binding.TextView.text = MainActivity.fromHtml(line.substring(t1 + 8))
            if (dzenNoch) {
                binding.TextView.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AkafistFragmentPasliaPrichBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(resursID: Int): PasliaPrychasciaFragment {
            val fragmentFirst = PasliaPrychasciaFragment()
            val args = Bundle()
            args.putInt("resursID", resursID)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}