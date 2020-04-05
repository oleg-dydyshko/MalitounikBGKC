package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
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
import java.util.*

class PasliaPrychasciaFragment : Fragment(), View.OnTouchListener {

    private var resurs = ""
    private var title = ""
    private var pasliaPrychascia1 = 0
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_DEFAULT_FONT_SIZE
    private var dzenNoch = false
    private var n = 0
    private var levo = false
    private var pravo = false
    private var procentTimer: Timer = Timer()
    private var procentSchedule: TimerTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        pasliaPrychascia1 = arguments?.getInt("paslia_prychascia") ?: 0
        title = arguments?.getString("title") ?: ""
        resurs = arguments?.getString("resurs") ?: ""
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { activity ->
            k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k.getBoolean("dzen_noch", false)
            fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
            TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            constraint.setOnTouchListener(this)
            val r = activity.resources
            var inputStream = r.openRawResource(R.raw.paslia_prychascia1)
            when (pasliaPrychascia1) {
                0 -> {
                    inputStream = r.openRawResource(R.raw.paslia_prychascia1)
                }
                1 -> {
                    inputStream = r.openRawResource(R.raw.paslia_prychascia2)
                }
                2 -> {
                    inputStream = r.openRawResource(R.raw.paslia_prychascia3)
                }
                3 -> {
                    inputStream = r.openRawResource(R.raw.paslia_prychascia4)
                }
                4 -> {
                    inputStream = r.openRawResource(R.raw.paslia_prychascia5)
                }
            }
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
                TextView.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorIcons))
                progress.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.akafist_fragment_paslia_prich, container, false)
    }

    private fun stopProcent() {
        procentTimer.cancel()
        procentSchedule = null
    }

    private fun startProcent() {
        stopProcent()
        procentTimer = Timer()
        procentSchedule = object : TimerTask() {
            override fun run() {
                activity?.let {
                    it.runOnUiThread {
                        progress.visibility = View.GONE
                    }
                }
            }
        }
        procentTimer.schedule(procentSchedule, 1000)
    }

    @SuppressLint("SetTextI18n")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val widthConstraintLayout = constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val prefEditor: SharedPreferences.Editor = k.edit()
        if (v?.id ?: 0 == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                activity?.let {
                    MainActivity.brightness = Settings.System.getInt(it.contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
                }
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    if (x < otstup) {
                        levo = true
                        progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                        progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        progress.visibility = View.VISIBLE
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup) {
                        pravo = true
                        var minmax = ""
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) minmax = " (мін)"
                        if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) minmax = " (макс)"
                        progress.text = "${fontBiblia.toInt()} sp$minmax"
                        progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f)
                        progress.visibility = View.VISIBLE
                        startProcent()
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (x < otstup && y > n && y % 15 == 0) {
                        if (MainActivity.brightness > 0) {
                            MainActivity.brightness = MainActivity.brightness - 1
                            activity?.let {
                                val lp = it.window.attributes
                                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                                it.window.attributes = lp
                            }
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                            MainActivity.checkBrightness = false
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (x < otstup && y < n && y % 15 == 0) {
                        if (MainActivity.brightness < 100) {
                            MainActivity.brightness = MainActivity.brightness + 1
                            activity?.let {
                                val lp = it.window.attributes
                                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                                it.window.attributes = lp
                            }
                            progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                            MainActivity.checkBrightness = false
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (x > widthConstraintLayout - otstup && y > n && y % 26 == 0) {
                        if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                            fontBiblia -= 4
                            prefEditor.putFloat("font_biblia", fontBiblia)
                            prefEditor.apply()
                            TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                            var min = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) min = " (мін)"
                            progress.text = "${fontBiblia.toInt()} sp$min"
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                    if (x > widthConstraintLayout - otstup && y < n && y % 26 == 0) {
                        if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                            fontBiblia += 4
                            prefEditor.putFloat("font_biblia", fontBiblia)
                            prefEditor.apply()
                            TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                            var max = ""
                            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) max = " (макс)"
                            progress.text = "${fontBiblia.toInt()} sp$max"
                            progress.visibility = View.VISIBLE
                            startProcent()
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    v?.performClick()
                    if (levo) {
                        levo = false
                    }
                    if (pravo) {
                        pravo = false
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (levo) {
                        levo = false
                    }
                    if (pravo) {
                        pravo = false
                    }
                }
            }
        }
        return true
    }

    companion object {
        fun newInstance(pasliaPrychascia: Int, title: String, resurs: String): PasliaPrychasciaFragment {
            val fragmentFirst = PasliaPrychasciaFragment()
            val args = Bundle()
            args.putInt("paslia_prychascia", pasliaPrychascia)
            args.putString("title", title)
            args.putString("resurs", resurs)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}