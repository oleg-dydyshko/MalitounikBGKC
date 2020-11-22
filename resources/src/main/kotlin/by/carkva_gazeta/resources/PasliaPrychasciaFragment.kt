package by.carkva_gazeta.resources

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.HelpText
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import kotlinx.android.synthetic.main.akafist_fragment_paslia_prich.*
import java.io.BufferedReader
import java.io.InputStreamReader

class PasliaPrychasciaFragment : Fragment() {

    private var resursID = R.raw.paslia_prychascia1
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        resursID = arguments?.getInt("resursID") ?: R.raw.paslia_prychascia1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
            TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            val inputStream = activity.resources.openRawResource(resursID)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            val builder = StringBuilder()
            reader.forEachLine {
                line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                builder.append(line)
            }
            inputStream.close()
            TextView.text = MainActivity.fromHtml(builder.toString())
            if (k.getBoolean("help_str", true)) {
                startActivity(Intent(activity, HelpText::class.java))
                val prefEditor: SharedPreferences.Editor = k.edit()
                prefEditor.putBoolean("help_str", false)
                prefEditor.apply()
            }
            if (dzenNoch) {
                TextView.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.akafist_fragment_paslia_prich, container, false)
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