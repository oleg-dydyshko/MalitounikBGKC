package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.SystemClock
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.CalaindarBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class CaliandarFull : Fragment(), View.OnClickListener {
    private var dayYear = 0
    private var day = 0
    private var year = 0
    private var dzenNoch = false
    private var rColorColorprimary = R.color.colorPrimary
    private val data = ArrayList<ArrayList<String>>()
    private val gson = Gson()
    private var sabytieTitle: String = ""
    private var position = 0
    private var mLastClickTime: Long = 0
    private var _binding: CalaindarBinding? = null
    private val binding get() = _binding!!

    private fun getData(mun: Int): String {
        return if (MenuCaliandar.munKal == mun && MenuCaliandar.dataJson != "") {
            MenuCaliandar.dataJson
        } else {
            val inputStream = resources.openRawResource(MainActivity.caliandar(context, mun))
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.readText()
            MenuCaliandar.dataJson = builder
            isr.close()
            MenuCaliandar.munKal = mun
            builder
        }
    }

    private fun getmun(position: Int): Int {
        var count2 = 0
        val g = GregorianCalendar(SettingsActivity.GET_CALIANDAR_YEAR_MIN, 0, 1)
        var mun = g[Calendar.MONTH]
        for (i in 0 until count) {
            g.add(Calendar.DATE, 1)
            if (position == i) {
                return count2
            }
            if (g[Calendar.MONTH] != mun) {
                count2++
                mun = g[Calendar.MONTH]
            }
        }
        return count2
    }

    private val count: Int
        get() {
            val c = Calendar.getInstance() as GregorianCalendar
            var dayyear = 0
            for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN..SettingsActivity.GET_CALIANDAR_YEAR_MAX) {
                dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
            }
            return dayyear
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dayYear = arguments?.getInt("dayYear") ?: 1
        year = arguments?.getInt("year") ?: SettingsActivity.GET_CALIANDAR_YEAR_MIN
        day = arguments?.getInt("day") ?: 1
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalaindarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            val type = object : TypeToken<ArrayList<ArrayList<String?>?>?>() {}.type
            data.addAll(gson.fromJson(getData(getmun(position)), type))
            val nedelName = arrayOf("", "нядзеля", "панядзелак", "аўторак", "серада", "чацьвер", "пятніца", "субота")
            val monthName = arrayOf("СТУДЗЕНЯ", "ЛЮТАГА", "САКАВІКА", "КРАСАВІКА", "ТРАЎНЯ", "ЧЭРВЕНЯ", "ЛІПЕНЯ", "ЖНІЎНЯ", "ВЕРАСЬНЯ", "КАСТРЫЧНІКА", "ЛІСТАПАДА", "СЬНЕЖНЯ")
            val c = Calendar.getInstance() as GregorianCalendar
            val k = activity?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = k?.getBoolean("dzen_noch", false) ?: false
            if (dzenNoch) rColorColorprimary = R.color.colorPrimary_black
            val tileMe = activity?.let { BitmapDrawable(it.resources, BitmapFactory.decodeResource(resources, R.drawable.calendar_fon)) }
            tileMe?.tileModeX = Shader.TileMode.REPEAT
            if (data[day][20] != "" && data[day][0].toInt() == 1) {
                val ton = data[day][20]
                binding.textTitleChyt.text = getString(R.string.bible_natatki, ton, binding.textTitleChyt.text)
                binding.textTitleChyt.setOnClickListener(this@CaliandarFull)
            } else {
                binding.textTitleChyt.isEnabled = false
            }
            binding.textChytanne.setOnClickListener(this@CaliandarFull)
            binding.textChytanneSviatyia.setOnClickListener(this@CaliandarFull)
            binding.textChytanneSviatyiaDop.setOnClickListener(this@CaliandarFull)
            val maranataSh = k?.getInt("maranata", 0) ?: 0
            if (maranataSh == 1) {
                binding.maranata.setOnClickListener(this@CaliandarFull)
                if (dzenNoch) {
                    activity?.let {
                        binding.maranata.setBackgroundResource(R.drawable.selector_dark_maranata)
                        binding.maranata.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textTitleMaranata.setBackgroundResource(R.drawable.selector_dark_maranata)
                        binding.textTitleMaranata.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    }
                }
                binding.maranata.visibility = View.VISIBLE
                binding.textTitleMaranata.visibility = View.VISIBLE
                var dataMaranAta = data[day][13]
                if (k?.getBoolean("belarus", false) == true) dataMaranAta = MainActivity.translateToBelarus(dataMaranAta)
                binding.maranata.text = dataMaranAta
            }
            binding.znakTipicona.setOnClickListener(this@CaliandarFull)
            if (data[day][21] != "") {
                binding.textBlaslavenne.text = data[day][21]
                binding.textBlaslavenne.visibility = View.VISIBLE
            }
            if (dzenNoch) {
                activity?.let {
                    binding.textSviatyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textSviatyia.setBackgroundResource(R.drawable.selector_dark)
                    binding.textPost.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                    binding.textCviatyGlavnyia.setBackgroundResource(R.drawable.selector_dark)
                    binding.textPredsviaty.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                }
            }
            binding.textDenNedeli.text = nedelName[data[day][0].toInt()]
            binding.textChislo.text = data[day][1]
            if (data[day][3].toInt() != c[Calendar.YEAR]) binding.textMesiac.text = getString(R.string.mesiach, monthName[data[day][2].toInt()], data[day][3])
            else binding.textMesiac.text = monthName[data[day][2].toInt()]
            if (!data[day][4].contains("no_sviatyia")) {
                var dataSviatyia = data[day][4]
                if (dzenNoch) dataSviatyia = dataSviatyia.replace("#d00505", "#f44336")
                binding.textSviatyia.text = MainActivity.fromHtml(dataSviatyia)
            } else {
                binding.polosa1.visibility = View.GONE
                binding.polosa2.visibility = View.GONE
                binding.textSviatyia.visibility = View.GONE
            }
            binding.textSviatyia.setOnClickListener(this@CaliandarFull)
            if (!data[day][6].contains("no_sviaty")) {
                binding.textCviatyGlavnyia.text = data[day][6]
                binding.textCviatyGlavnyia.visibility = View.VISIBLE
                if (data[day][6].contains("Пачатак") || data[day][6].contains("Вялікі") || data[day][6].contains("Вялікая") || data[day][6].contains("ВЕЧАР") || data[day][6].contains("Палова")) {
                    activity?.let {
                        if (dzenNoch) binding.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        else binding.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    }
                    binding.textCviatyGlavnyia.setTypeface(null, Typeface.NORMAL)
                    binding.textCviatyGlavnyia.isEnabled = false
                } else {
                    if (data[day][6].toLowerCase(Locale.getDefault()).contains("нядзел") || data[day][6].toLowerCase(Locale.getDefault()).contains("дзень") || data[day][6].toLowerCase(Locale.getDefault()).contains("сьветл")) binding.textCviatyGlavnyia.isEnabled = false
                    else binding.textCviatyGlavnyia.setOnClickListener(this@CaliandarFull)
                }
            }
            activity?.let {
                when (data[day][7].toInt()) {
                    1 -> {
                        binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textDenNedeli.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textChislo.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textMesiac.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textTitleChyt.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textChytanne.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_bez_posta)
                        binding.textPamerlyia.setBackgroundResource(R.drawable.selector_bez_posta)
                        if (data[day][0].contains("6")) {
                            binding.textPost.visibility = View.VISIBLE
                            binding.textPost.text = resources.getString(R.string.No_post)
                        }
                    }
                    2 -> {
                        binding.textTitleChyt.setBackgroundResource(R.drawable.selector_post)
                        binding.textChytanne.setBackgroundResource(R.drawable.selector_post)
                        binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_post)
                        binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_post)
                        binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_post)
                        binding.textDenNedeli.setBackgroundResource(R.drawable.selector_post)
                        binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textChislo.setBackgroundResource(R.drawable.selector_post)
                        binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textMesiac.setBackgroundResource(R.drawable.selector_post)
                        binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textPamerlyia.setBackgroundResource(R.drawable.selector_post)
                        if (data[day][0].contains("6")) {
                            binding.PostFish.visibility = View.VISIBLE
                            binding.textPost.visibility = View.VISIBLE
                            if (dzenNoch) {
                                binding.PostFish.setImageResource(R.drawable.fishe_whate)
                            }
                        }
                    }
                    3 -> {
                        binding.textDenNedeli.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChislo.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textMesiac.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textTitleChyt.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textTitleChyt.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChytanne.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textChytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textChytanneSviatyiaDop.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textChytanneSviatyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textBlaslavenne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textPost.text = resources.getString(R.string.Strogi_post)
                        binding.textPamerlyia.text = resources.getString(R.string.Strogi_post)
                        binding.textPamerlyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textPost.visibility = View.VISIBLE
                        binding.PostFish.visibility = View.VISIBLE
                        if (dzenNoch) binding.PostFish.setImageResource(R.drawable.fishe_red_black) else binding.PostFish.setImageResource(R.drawable.fishe_red)
                    }
                    else -> {
                        binding.textDenNedeli.setBackgroundResource(R.color.colorDivider)
                        binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textChislo.setBackgroundResource(R.color.colorDivider)
                        binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textMesiac.setBackgroundResource(R.color.colorDivider)
                        binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    }
                }
            }
            if (data[day][5].contains("1") || data[day][5].contains("2") || data[day][5].contains("3")) {
                binding.textDenNedeli.setBackgroundResource(rColorColorprimary)
                binding.textChislo.setBackgroundResource(rColorColorprimary)
                binding.textMesiac.setBackgroundResource(rColorColorprimary)
                activity?.let {
                    binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                }
            }
            if (data[day][5].contains("2")) {
                binding.textCviatyGlavnyia.setTypeface(null, Typeface.NORMAL)
            }
            if (data[day][8] != "") {
                binding.textPredsviaty.text = MainActivity.fromHtml(data[day][8])
                binding.textPredsviaty.visibility = View.VISIBLE
            }
            binding.textChytanne.text = data[day][9]
            if (data[day][9] == "Прабачьце, няма дадзеных" || data[day][9] == "Літургіі няма") binding.textChytanne.isEnabled = false
            if (data[day][9] == "") binding.textChytanne.visibility = View.GONE
            if (data[day][10] != "") {
                binding.textChytanneSviatyia.text = data[day][10]
                binding.textChytanneSviatyia.visibility = View.VISIBLE
            }
            if (data[day][11] != "") {
                binding.textChytanneSviatyiaDop.text = data[day][11]
                binding.textChytanneSviatyiaDop.visibility = View.VISIBLE
            }
            when (data[day][12].toInt()) {
                1 -> {
                    if (dzenNoch) binding.znakTipicona.setImageResource(R.drawable.znaki_krest_black) else binding.znakTipicona.setImageResource(R.drawable.znaki_krest)
                    binding.znakTipicona.visibility = View.VISIBLE
                }
                2 -> {
                    if (dzenNoch) binding.znakTipicona.setImageResource(R.drawable.znaki_krest_v_kruge_black)
                    else binding.znakTipicona.setImageResource(R.drawable.znaki_krest_v_kruge)
                    binding.znakTipicona.visibility = View.VISIBLE
                }
                3 -> {
                    if (dzenNoch) binding.znakTipicona.setImageResource(R.drawable.znaki_krest_v_polukruge_black) else binding.znakTipicona.setImageResource(R.drawable.znaki_krest_v_polukruge)
                    binding.znakTipicona.visibility = View.VISIBLE
                }
                4 -> {
                    if (dzenNoch) binding.znakTipicona.setImageResource(R.drawable.znaki_ttk_black_black) else binding.znakTipicona.setImageResource(R.drawable.znaki_ttk)
                    binding.znakTipicona.visibility = View.VISIBLE
                }
                5 -> {
                    binding.znakTipicona.visibility = View.VISIBLE
                    if (dzenNoch) {
                        binding.znakTipicona.setImageResource(R.drawable.znaki_ttk_whate)
                    } else {
                        binding.znakTipicona.setImageResource(R.drawable.znaki_ttk_black)
                    }
                }
            }
            if (k?.getInt("pravas", 0) == 1) {
                if (data[day][14] != "") {
                    binding.pravaslavie.visibility = View.VISIBLE
                    binding.pravaslavie.text = data[day][14]
                }
            }
            if (k?.getInt("pkc", 0) == 1) {
                if (data[day][19] != "") {
                    binding.RKC.visibility = View.VISIBLE
                    binding.RKC.text = data[day][19]
                }
            }
            if (k?.getInt("gosud", 0) == 1) {
                if (data[day][16] != "") {
                    binding.gosudarstvo.visibility = View.VISIBLE
                    binding.gosudarstvo.text = data[day][16]
                }
                if (data[day][15] != "") {
                    binding.gosudarstvo.visibility = View.VISIBLE
                    binding.gosudarstvo.text = data[day][15]
                    activity?.let { binding.gosudarstvo.setTextColor(ContextCompat.getColor(it, rColorColorprimary)) }
                }
            }
            if (k?.getInt("pafesii", 0) == 1) {
                if (data[day][17] != "") {
                    binding.prafesional.visibility = View.VISIBLE
                    binding.prafesional.text = data[day][17]
                }
            }
            if (data[day][18].contains("1")) {
                binding.textPamerlyia.visibility = View.VISIBLE
            }
            if (MainActivity.padzeia.size > 0) {
                val extras = activity?.intent?.extras
                if (extras?.getBoolean("sabytieView", false) == true) {
                    sabytieTitle = extras.getString("sabytieTitle", "") ?: ""
                }
                if (editCaliandarTitle != "") {
                    sabytieTitle = editCaliandarTitle
                    editCaliandarTitle = ""
                }
                withContext(Dispatchers.Main) {
                    sabytieView(sabytieTitle)
                }
            }
            if (Sabytie.editCaliandar) {
                binding.scroll.post {
                    binding.scroll.fullScroll(ScrollView.FOCUS_DOWN)
                    Sabytie.editCaliandar = false
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val c = Calendar.getInstance() as GregorianCalendar
        var dayyear = 0
        for (i in SettingsActivity.GET_CALIANDAR_YEAR_MIN until year) {
            dayyear = if (c.isLeapYear(i)) 366 + dayyear else 365 + dayyear
        }
        MainActivity.setDataCalendar = dayyear + dayYear
        when (v?.id ?: 0) {
            R.id.textCviatyGlavnyia -> if (MainActivity.checkmoduleResources(activity)) {
                activity?.let {
                    val i = Intent()
                    i.setClassName(it, MainActivity.OPISANIE)
                    i.putExtra("glavnyia", true)
                    i.putExtra("svity", data[day][6])
                    i.putExtra("mun", data[day][2].toInt())
                    i.putExtra("day", data[day][1].toInt())
                    startActivity(i)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
            R.id.textSviatyia -> if (MainActivity.checkmoduleResources(activity)) {
                activity?.let {
                    val i = Intent()
                    i.setClassName(it, MainActivity.OPISANIE)
                    i.putExtra("mun", data[day][2].toInt())
                    i.putExtra("day", data[day][1].toInt())
                    startActivity(i)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
            R.id.textChytanneSviatyia -> if (MainActivity.checkmoduleResources(activity)) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", data[day][10])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
            R.id.textChytanne -> if (MainActivity.checkmoduleResources(activity)) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", data[day][9])
                    intent.putExtra("nedelia", data[day][5].toInt())
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
            R.id.textChytanneSviatyiaDop -> if (MainActivity.checkmoduleResources(activity)) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", data[day][11])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
            R.id.maranata -> if (MainActivity.checkmoduleResources(activity)) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.MARANATA)
                    intent.putExtra("cytanneMaranaty", data[day][13])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
            R.id.textTitleChyt -> {
                val ton = data[day][20]
                if (MainActivity.checkmoduleResources(activity)) {
                    activity?.let {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.TON)
                        if (ton.contains("Тон 1")) intent.putExtra("ton", 1)
                        if (ton.contains("Тон 2")) intent.putExtra("ton", 2)
                        if (ton.contains("Тон 3")) intent.putExtra("ton", 3)
                        if (ton.contains("Тон 4")) intent.putExtra("ton", 4)
                        if (ton.contains("Тон 5")) intent.putExtra("ton", 5)
                        if (ton.contains("Тон 6")) intent.putExtra("ton", 6)
                        if (ton.contains("Тон 7")) intent.putExtra("ton", 7)
                        if (ton.contains("Тон 8")) intent.putExtra("ton", 8)
                        intent.putExtra("ton_naidzelny", true)
                        startActivity(intent)
                    }
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }
            R.id.znakTipicona -> {
                val tipiconNumber = data[day][12].toInt()
                val tipicon = DialogTipicon.getInstance(tipiconNumber)
                fragmentManager?.let { tipicon.show(it, "tipicon") }
            }
        }
    }

    private fun sabytieView(sabytieTitle: String) {
        val gc = Calendar.getInstance() as GregorianCalendar
        val sabytieList = ArrayList<TextViewRobotoCondensed>()
        MainActivity.padzeia.sort()
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
                    val title = p.padz
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
                    activity?.let { activity ->
                        val density = resources.displayMetrics.density
                        val realpadding = (5 * density).toInt()
                        val textViewT = TextViewRobotoCondensed(activity)
                        textViewT.text = title
                        textViewT.setPadding(realpadding, realpadding, realpadding, realpadding)
                        textViewT.setTypeface(null, Typeface.BOLD)
                        textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        textViewT.setTypeface(null, Typeface.BOLD)

                        textViewT.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(Sabytie.getColors(activity)[p.color]))
                        sabytieList.add(textViewT)
                        val textView = TextViewRobotoCondensed(activity)
                        textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_text))
                        textView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorDivider))
                        textView.setPadding(realpadding, realpadding, realpadding, realpadding)
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_DEFAULT_FONT_SIZE)
                        if (dzenNoch) {
                            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                            textView.setBackgroundResource(R.color.colorprimary_material_dark)
                        }
                        val clickableSpanEdit = object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                fragmentManager?.let {
                                    editCaliandarTitle = textViewT.text.toString()
                                    val intent = Intent(activity, Sabytie::class.java)
                                    intent.putExtra("edit", true)
                                    intent.putExtra("position", index)
                                    startActivity(intent)
                                }
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                            }
                        }
                        val clickableSpanRemove = object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                fragmentManager?.let {
                                    val dd = DialogDelite.getInstance(index, "", "з падзей", MainActivity.padzeia[index].dat + " " + MainActivity.padzeia[index].padz)
                                    dd.show(it, "dialig_delite")
                                }
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                            }
                        }
                        val spannable = if (!konecSabytie) {
                            SpannableString(resources.getString(R.string.sabytieKali, data, time, res))
                        } else {
                            SpannableString(resources.getString(R.string.sabytieDoKuda, data, time, dataK, timeK, res))
                        }
                        val t1 = spannable.lastIndexOf("\n")
                        val t2 = spannable.lastIndexOf("/")
                        val t3 = spannable.indexOf(res)
                        spannable.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), t1 + 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        if (dzenNoch) {
                            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary_black)), t1 + 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (paznicia) spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary_black)), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        } else {
                            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)), t1 + 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (paznicia) spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)), t3, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        spannable.setSpan(clickableSpanEdit, t1 + 1, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(clickableSpanRemove, t2 + 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        textView.text = spannable
                        textView.movementMethod = LinkMovementMethod.getInstance()
                        sabytieList.add(textView)
                        val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        llp.setMargins(0, 0, 0, 10)
                        textView.layoutParams = llp
                        textViewT.layoutParams = llp
                        textView.visibility = View.GONE
                        textViewT.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0)
                        textViewT.setOnClickListener {
                            if (textView.visibility == View.GONE) {
                                textViewT.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0)
                                textView.visibility = View.VISIBLE
                                val llp2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                llp2.setMargins(0, 0, 0, 0)
                                textViewT.layoutParams = llp2
                                binding.scroll.post { binding.scroll.fullScroll(ScrollView.FOCUS_DOWN) }
                            } else {
                                textViewT.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0)
                                textViewT.layoutParams = llp
                                textView.visibility = View.GONE
                            }
                        }
                        if (title == sabytieTitle) {
                            textViewT.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0)
                            textView.visibility = View.VISIBLE
                            val llp2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                            llp2.setMargins(0, 0, 0, 0)
                            textViewT.layoutParams = llp2
                            binding.scroll.post {
                                activity.intent?.removeExtra("sabytieView")
                                binding.scroll.fullScroll(ScrollView.FOCUS_DOWN)
                                if (!Sabytie.editCaliandar) {
                                    val shakeanimation = AnimationUtils.loadAnimation(activity, R.anim.shake)
                                    textViewT.startAnimation(shakeanimation)
                                    textView.startAnimation(shakeanimation)
                                }
                                Sabytie.editCaliandar = false
                            }
                        }
                    }
                }
                gc.add(Calendar.DATE, 1)
            }
        }
        for (i in sabytieList.indices) {
            binding.padzei.addView(sabytieList[i])
        }
    }

    companion object {
        var editCaliandarTitle = ""
        fun newInstance(position: Int, day: Int, year: Int, dayYear: Int): CaliandarFull {
            val fragment = CaliandarFull()
            val args = Bundle()
            args.putInt("dayYear", dayYear)
            args.putInt("year", year)
            args.putInt("day", day)
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }
}
