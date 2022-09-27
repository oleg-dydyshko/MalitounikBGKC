package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.CalaindarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class CaliandarFull : Fragment(), View.OnClickListener {
    private val dzenNoch: Boolean
        get() {
            var dzn = false
            activity?.let {
                dzn = (it as BaseActivity).getBaseDzenNoch()
            }
            return dzn
        }
    private var rColorColorprimary = R.drawable.selector_red
    private var sabytieTitle = ""
    private var position = 0
    private var mLastClickTime: Long = 0
    private var _binding: CalaindarBinding? = null
    private val binding get() = _binding!!
    private var sabytieJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        sabytieJob?.cancel()
        _binding = null
    }

    fun getDayOfYear() = MenuCaliandar.getPositionCaliandar(position)[24].toInt()

    fun getYear() = MenuCaliandar.getPositionCaliandar(position)[3].toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CalaindarBinding.inflate(inflater, container, false)
        activity?.let {
            val nedelName = it.resources.getStringArray(R.array.dni_nedeli)
            val monthName = it.resources.getStringArray(R.array.meciac)
            val c = Calendar.getInstance()
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (dzenNoch) rColorColorprimary = R.drawable.selector_red_dark
            val tileMe = BitmapDrawable(it.resources, BitmapFactory.decodeResource(resources, R.drawable.calendar_fon))
            tileMe.tileModeX = Shader.TileMode.REPEAT
            if (MenuCaliandar.getPositionCaliandar(position)[20].toInt() != 0) {
                binding.textTon.text = getString(R.string.ton, MenuCaliandar.getPositionCaliandar(position)[20])
                if (dzenNoch) {
                    binding.textTon.setBackgroundResource(R.drawable.selector_dark)
                    binding.textTon.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                } else {
                    binding.textTon.setBackgroundResource(R.drawable.selector_default)
                    binding.textTon.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary))
                }
                binding.textTon.visibility = View.VISIBLE
                binding.textTon.setOnClickListener(this@CaliandarFull)
            }
            TooltipCompat.setTooltipText(binding.kniga, getString(R.string.liturgikon2))
            binding.kniga.setOnClickListener(this@CaliandarFull)
            binding.textChytanne.setOnClickListener(this@CaliandarFull)
            binding.textChytanneSviatyia.setOnClickListener(this@CaliandarFull)
            binding.textChytanneSviatyiaDop.setOnClickListener(this@CaliandarFull)
            if (k.getInt("maranata", 0) == 1) {
                binding.maranata.setOnClickListener(this@CaliandarFull)
                if (dzenNoch) {
                    binding.maranata.setBackgroundResource(R.drawable.selector_dark_maranata)
                    binding.maranata.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textTitleMaranata.setBackgroundResource(R.drawable.selector_dark_maranata)
                    binding.textTitleMaranata.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                }
                binding.maranata.visibility = View.VISIBLE
                binding.textTitleMaranata.visibility = View.VISIBLE
                var dataMaranAta = MenuCaliandar.getPositionCaliandar(position)[13]
                if (k.getBoolean("belarus", true)) dataMaranAta = MainActivity.translateToBelarus(dataMaranAta)
                binding.maranata.text = dataMaranAta
            }
            if (MenuCaliandar.getPositionCaliandar(position)[21].isNotEmpty()) {
                binding.textBlaslavenne.text = MenuCaliandar.getPositionCaliandar(position)[21]
                binding.textBlaslavenne.visibility = View.VISIBLE
            }
            if (dzenNoch) {
                if (MenuCaliandar.getPositionCaliandar(position)[20].toInt() == 0) binding.textPost.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textCviatyGlavnyia.setBackgroundResource(R.drawable.selector_dark)
                binding.textPredsviaty.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            }
            binding.textDenNedeli.text = nedelName[MenuCaliandar.getPositionCaliandar(position)[0].toInt()]
            binding.textChislo.text = MenuCaliandar.getPositionCaliandar(position)[1]
            if (MenuCaliandar.getPositionCaliandar(position)[3].toInt() != c[Calendar.YEAR]) binding.textMesiac.text = getString(R.string.mesiach, monthName[MenuCaliandar.getPositionCaliandar(position)[2].toInt()], MenuCaliandar.getPositionCaliandar(position)[3])
            else binding.textMesiac.text = monthName[MenuCaliandar.getPositionCaliandar(position)[2].toInt()]
            if (k.getBoolean("adminDayInYear", false)) {
                binding.textDayInYear.visibility = View.VISIBLE
                binding.textDayInYear.text = getString(R.string.admin_show_day_in_year, MenuCaliandar.getPositionCaliandar(position)[24], MenuCaliandar.getPositionCaliandar(position)[22])
            }
            if (!MenuCaliandar.getPositionCaliandar(position)[4].contains("no_sviatyia")) {
                sviatyiaView()
            } else {
                binding.polosa1.visibility = View.GONE
                binding.polosa2.visibility = View.GONE
                binding.listSviatyia.visibility = View.GONE
            }
            if (!MenuCaliandar.getPositionCaliandar(position)[6].contains("no_sviaty")) {
                binding.textCviatyGlavnyia.text = MenuCaliandar.getPositionCaliandar(position)[6]
                binding.textCviatyGlavnyia.visibility = View.VISIBLE
                if (MenuCaliandar.getPositionCaliandar(position)[6].contains("Пачатак") || MenuCaliandar.getPositionCaliandar(position)[6].contains("Вялікі") || MenuCaliandar.getPositionCaliandar(position)[6].contains("Вялікая") || MenuCaliandar.getPositionCaliandar(position)[6].contains("ВЕЧАР") || MenuCaliandar.getPositionCaliandar(position)[6].contains("Палова")) {
                    if (dzenNoch) binding.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    else binding.textCviatyGlavnyia.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textCviatyGlavnyia.typeface = MainActivity.createFont(Typeface.NORMAL)
                    binding.textCviatyGlavnyia.isEnabled = false
                } else {
                    if (MenuCaliandar.getPositionCaliandar(position)[6].contains("Айцоў першых 6-ці Ўсяленскіх сабораў", true)) binding.textCviatyGlavnyia.setOnClickListener(this@CaliandarFull)
                    else if (MenuCaliandar.getPositionCaliandar(position)[6].contains("нядзел", true) || MenuCaliandar.getPositionCaliandar(position)[6].contains("сьветл", true)) binding.textCviatyGlavnyia.isEnabled = false
                    else binding.textCviatyGlavnyia.setOnClickListener(this@CaliandarFull)
                }
            }
            when (MenuCaliandar.getPositionCaliandar(position)[7].toInt()) {
                1 -> {
                    binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textDayInYear.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textDenNedeli.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textChislo.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textMesiac.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textDayInYear.setBackgroundResource(R.drawable.selector_bez_posta)
                    if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_bez_posta_black)
                    else binding.kniga.setImageResource(R.drawable.book_bez_posta)
                    binding.chytanne.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textChytanne.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_bez_posta)
                    binding.textPamerlyia.setBackgroundResource(R.drawable.selector_bez_posta)
                    if (MenuCaliandar.getPositionCaliandar(position)[0].toInt() == Calendar.FRIDAY) {
                        binding.textPost.visibility = View.VISIBLE
                        binding.textPost.textSize = SettingsActivity.GET_FONT_SIZE_MIN
                        binding.textPost.text = resources.getString(R.string.No_post)
                    }
                }
                2 -> {
                    binding.chytanne.setBackgroundResource(R.drawable.selector_post)
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
                    binding.textDayInYear.setBackgroundResource(R.drawable.selector_post)
                    binding.textDayInYear.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_post_black)
                    else binding.kniga.setImageResource(R.drawable.book_post)
                    binding.textPamerlyia.setBackgroundResource(R.drawable.selector_post)
                    if (MenuCaliandar.getPositionCaliandar(position)[0].toInt() == Calendar.FRIDAY) {
                        binding.PostFish.visibility = View.VISIBLE
                        binding.textPost.visibility = View.VISIBLE
                        binding.textPost.textSize = SettingsActivity.GET_FONT_SIZE_MIN
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
                    binding.textDayInYear.setBackgroundResource(R.drawable.selector_strogi_post)
                    binding.textDayInYear.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_strogi_post_black)
                    else binding.kniga.setImageResource(R.drawable.book_strogi_post)
                    binding.chytanne.setBackgroundResource(R.drawable.selector_strogi_post)
                    binding.chytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChytanne.setBackgroundResource(R.drawable.selector_strogi_post)
                    binding.textChytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_strogi_post)
                    binding.textChytanneSviatyiaDop.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_strogi_post)
                    binding.textChytanneSviatyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_strogi_post)
                    binding.textBlaslavenne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textPost.textSize = SettingsActivity.GET_FONT_SIZE_MIN
                    binding.textPost.text = resources.getString(R.string.Strogi_post)
                    binding.textPamerlyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textPost.visibility = View.VISIBLE
                    binding.PostFish.visibility = View.VISIBLE
                    if (dzenNoch) binding.PostFish.setImageResource(R.drawable.fishe_red_black)
                    else binding.PostFish.setImageResource(R.drawable.fishe_red)
                }
                else -> {
                    binding.textDenNedeli.setBackgroundResource(R.color.colorDivider)
                    binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textChislo.setBackgroundResource(R.color.colorDivider)
                    binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textMesiac.setBackgroundResource(R.color.colorDivider)
                    binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    binding.textDayInYear.setBackgroundResource(R.color.colorDivider)
                    binding.textDayInYear.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_divider_black)
                    else binding.kniga.setImageResource(R.drawable.book_divider)
                }
            }
            if (MenuCaliandar.getPositionCaliandar(position)[5].contains("1") || MenuCaliandar.getPositionCaliandar(position)[5].contains("2") || MenuCaliandar.getPositionCaliandar(position)[5].contains("3")) {
                binding.textDenNedeli.setBackgroundResource(rColorColorprimary)
                binding.textChislo.setBackgroundResource(rColorColorprimary)
                binding.textMesiac.setBackgroundResource(rColorColorprimary)
                binding.textDayInYear.setBackgroundResource(rColorColorprimary)
                if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_red_black)
                else binding.kniga.setImageResource(R.drawable.book_red)
                binding.textDenNedeli.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.textChislo.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.textMesiac.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.textDayInYear.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                if (MenuCaliandar.getPositionCaliandar(position)[7].toInt() != 3) {
                    binding.chytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.chytanne.setBackgroundResource(rColorColorprimary)
                    binding.textChytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChytanne.setBackgroundResource(rColorColorprimary)
                    binding.textChytanneSviatyiaDop.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChytanneSviatyiaDop.setBackgroundResource(rColorColorprimary)
                    binding.textChytanneSviatyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textChytanneSviatyia.setBackgroundResource(rColorColorprimary)
                    binding.textPamerlyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textPamerlyia.setBackgroundResource(rColorColorprimary)
                    binding.textBlaslavenne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    binding.textBlaslavenne.setBackgroundResource(rColorColorprimary)
                }
            }
            if (MenuCaliandar.getPositionCaliandar(position)[5].contains("2")) {
                binding.textCviatyGlavnyia.typeface = MainActivity.createFont(Typeface.NORMAL)
                if(MenuCaliandar.getPositionCaliandar(position)[6].contains("<strong>")) {
                    val t1 = MenuCaliandar.getPositionCaliandar(position)[6].indexOf("<strong>")
                    val t2 = MenuCaliandar.getPositionCaliandar(position)[6].indexOf("</strong>")
                    val spannable = SpannableStringBuilder(binding.textCviatyGlavnyia.text)
                    spannable.setSpan(CustomTypefaceSpan("", MainActivity.createFont(Typeface.BOLD)), t1 + 8, t2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannable.replace(t2, t2 + 9, "")
                    spannable.replace(t1, t1 + 8, "")
                    binding.textCviatyGlavnyia.text = spannable
                }
            }
            if (MenuCaliandar.getPositionCaliandar(position)[8].isNotEmpty()) {
                binding.textPredsviaty.text = MainActivity.fromHtml(MenuCaliandar.getPositionCaliandar(position)[8])
                binding.textPredsviaty.visibility = View.VISIBLE
            }
            binding.textChytanne.text = MenuCaliandar.getPositionCaliandar(position)[9]
            if (MenuCaliandar.getPositionCaliandar(position)[9] == getString(R.string.no_danyx) || MenuCaliandar.getPositionCaliandar(position)[9] == getString(R.string.no_lityrgii)) binding.textChytanne.isEnabled = false
            if (MenuCaliandar.getPositionCaliandar(position)[9] == "") binding.textChytanne.visibility = View.GONE
            if (MenuCaliandar.getPositionCaliandar(position)[10].isNotEmpty()) {
                binding.textChytanneSviatyia.text = MenuCaliandar.getPositionCaliandar(position)[10]
                binding.textChytanneSviatyia.visibility = View.VISIBLE
            }
            if (MenuCaliandar.getPositionCaliandar(position)[11].isNotEmpty()) {
                binding.textChytanneSviatyiaDop.text = MenuCaliandar.getPositionCaliandar(position)[11]
                binding.textChytanneSviatyiaDop.visibility = View.VISIBLE
            }
            if (MenuCaliandar.getPositionCaliandar(position)[12].toInt() == 2) {
                if (dzenNoch) binding.znakTipicona.setImageResource(R.drawable.znaki_krest_v_kruge_black)
                else binding.znakTipicona.setImageResource(R.drawable.znaki_krest_v_kruge)
                binding.znakTipicona.visibility = View.VISIBLE
            }
            val svityDrugasnuia = SpannableStringBuilder()
            if (k.getInt("pkc", 0) == 1 && MenuCaliandar.getPositionCaliandar(position)[19] != "") {
                svityDrugasnuia.append(MenuCaliandar.getPositionCaliandar(position)[19])
            }
            if (k.getInt("pravas", 0) == 1 && MenuCaliandar.getPositionCaliandar(position)[14].isNotEmpty()) {
                if (svityDrugasnuia.isNotEmpty()) svityDrugasnuia.append("\n\n")
                svityDrugasnuia.append(MenuCaliandar.getPositionCaliandar(position)[14])
            }
            if (k.getInt("gosud", 0) == 1) {
                if (MenuCaliandar.getPositionCaliandar(position)[16].isNotEmpty()) {
                    if (svityDrugasnuia.isNotEmpty()) svityDrugasnuia.append("\n\n")
                    svityDrugasnuia.append(MenuCaliandar.getPositionCaliandar(position)[16])
                }
                if (MenuCaliandar.getPositionCaliandar(position)[15].isNotEmpty()) {
                    if (svityDrugasnuia.isNotEmpty()) svityDrugasnuia.append("\n\n")
                    val sviata = MenuCaliandar.getPositionCaliandar(position)[15]
                    val svityDrugasnuiaLength = svityDrugasnuia.length
                    svityDrugasnuia.append(sviata)
                    if (dzenNoch) svityDrugasnuia.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary_black)), svityDrugasnuiaLength, svityDrugasnuia.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else svityDrugasnuia.setSpan(ForegroundColorSpan(ContextCompat.getColor(it, R.color.colorPrimary)), svityDrugasnuiaLength, svityDrugasnuia.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            if (k.getInt("pafesii", 0) == 1 && MenuCaliandar.getPositionCaliandar(position)[17].isNotEmpty()) {
                if (svityDrugasnuia.isNotEmpty()) svityDrugasnuia.append("\n\n")
                svityDrugasnuia.append(MenuCaliandar.getPositionCaliandar(position)[17])
            }
            if (svityDrugasnuia.isNotEmpty()) {
                binding.sviatyDrugasnyia.text = svityDrugasnuia
                binding.sviatyDrugasnyia.visibility = View.VISIBLE
            }
            if (MenuCaliandar.getPositionCaliandar(position)[18].contains("1")) {
                binding.textPamerlyia.visibility = View.VISIBLE
            }
            if (MainActivity.padzeia.size > 0) {
                val extras = it.intent?.extras
                if (extras?.getBoolean("sabytieView", false) == true) {
                    sabytieTitle = extras.getString("sabytieTitle", "") ?: ""
                }
                if (editCaliandarTitle.isNotEmpty()) {
                    sabytieTitle = editCaliandarTitle
                    editCaliandarTitle = ""
                }
                sabytieJob = CoroutineScope(Dispatchers.Main).launch {
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
        return binding.root
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v?.id ?: 0) {
            R.id.textTon -> {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                    val ton = MenuCaliandar.getPositionCaliandar(position)[20].toInt()
                    intent.putExtra("resurs", "ton$ton")
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", "Тон $ton")
                    startActivity(intent)
                }
            }
            R.id.kniga -> {
                val colorDialog = if (MenuCaliandar.getPositionCaliandar(position)[5].contains("1") || MenuCaliandar.getPositionCaliandar(position)[5].contains("2") || MenuCaliandar.getPositionCaliandar(position)[5].contains("3")) {
                    4
                } else {
                    MenuCaliandar.getPositionCaliandar(position)[7].toInt()
                }
                val svity = MenuCaliandar.getPositionCaliandar(position)[6]
                var daysv = MenuCaliandar.getPositionCaliandar(position)[1].toInt()
                var munsv = MenuCaliandar.getPositionCaliandar(position)[2].toInt() + 1
                if (svity.contains("уваход у ерусалім", true)) {
                    daysv = -1
                    munsv = 0
                }
                if (svity.contains("уваскрасеньне", true)) {
                    daysv = -1
                    munsv = 1
                }
                if (svity.contains("узьнясеньне", true)) {
                    daysv = -1
                    munsv = 2
                }
                if (svity.contains("зыход", true)) {
                    daysv = -1
                    munsv = 3
                }
                if (svity.contains("Айцоў першых 6-ці Ўсяленскіх сабораў", true)) {
                    daysv = -1
                    munsv = 4
                }
                val dialogCalindarGrid = DialogCalindarGrid.getInstance(colorDialog, MenuCaliandar.getPositionCaliandar(position)[20].toInt(), MenuCaliandar.getPositionCaliandar(position)[0].toInt(), daysv, munsv, MenuCaliandar.getPositionCaliandar(position)[22].toInt(), MenuCaliandar.getPositionCaliandar(position)[4], MenuCaliandar.getPositionCaliandar(position)[3].toInt(), MenuCaliandar.getPositionCaliandar(position)[1].toInt(), MenuCaliandar.getPositionCaliandar(position)[2].toInt() + 1, MenuCaliandar.getPositionCaliandar(position)[24])
                dialogCalindarGrid.show(childFragmentManager, "grid")
            }
            R.id.textCviatyGlavnyia -> activity?.let {
                val svity = MenuCaliandar.getPositionCaliandar(position)[6]
                var daysv = MenuCaliandar.getPositionCaliandar(position)[1].toInt()
                var munsv = MenuCaliandar.getPositionCaliandar(position)[2].toInt() + 1
                if (svity.contains("уваход у ерусалім", true)) {
                    daysv = -1
                    munsv = 0
                }
                if (svity.contains("уваскрасеньне", true)) {
                    daysv = -1
                    munsv = 1
                }
                if (svity.contains("узьнясеньне", true)) {
                    daysv = -1
                    munsv = 2
                }
                if (svity.contains("зыход", true)) {
                    daysv = -1
                    munsv = 3
                }
                if (svity.contains("Айцоў першых 6-ці Ўсяленскіх сабораў", true)) {
                    daysv = -1
                    munsv = 4
                }
                val i = Intent(it, Opisanie::class.java)
                i.putExtra("glavnyia", true)
                i.putExtra("mun", munsv)
                i.putExtra("day", daysv)
                i.putExtra("year", MenuCaliandar.getPositionCaliandar(position)[3].toInt())
                startActivity(i)
            }
            R.id.textChytanneSviatyia -> if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", MenuCaliandar.getPositionCaliandar(position)[10])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
            R.id.textChytanne -> if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", MenuCaliandar.getPositionCaliandar(position)[9])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
            R.id.textChytanneSviatyiaDop -> if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", MenuCaliandar.getPositionCaliandar(position)[11])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
            R.id.maranata -> if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.MARANATA)
                    intent.putExtra("cytanneMaranaty", MenuCaliandar.getPositionCaliandar(position)[13])
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
    }

    fun sabytieView(sabytieTitle: String) {
        activity?.let { activity ->
            binding.padzei.removeAllViewsInLayout()
            val gc = Calendar.getInstance() as GregorianCalendar
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
                    if (gc[Calendar.DAY_OF_YEAR] == getDayOfYear() && gc[Calendar.YEAR] == getYear()) {
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
                        val density = resources.displayMetrics.density
                        val realpadding = (5 * density).toInt()
                        val textViewT = TextView(activity)
                        textViewT.text = title
                        textViewT.setPadding(realpadding, realpadding, realpadding, realpadding)
                        textViewT.typeface = MainActivity.createFont(Typeface.BOLD)
                        textViewT.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)

                        textViewT.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(Sabytie.getColors(activity, p.color)))
                        sabytieList.add(textViewT)
                        val textView = TextView(activity)
                        textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_text))
                        textView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorDivider))
                        textView.setPadding(realpadding, realpadding, realpadding, realpadding)
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
                        if (dzenNoch) {
                            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                            textView.setBackgroundResource(R.color.colorprimary_material_dark)
                        }
                        val clickableSpanEdit = object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                editCaliandarTitle = textViewT.text.toString()
                                val intent = Intent(activity, Sabytie::class.java)
                                intent.putExtra("edit", true)
                                intent.putExtra("position", index)
                                startActivity(intent)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                            }
                        }
                        val clickableSpanRemove = object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                val dd = DialogDelite.getInstance(index, "", "з падзей", getString(R.string.sabytie_data_name, MainActivity.padzeia[index].dat, MainActivity.padzeia[index].padz))
                                dd.show(childFragmentManager, "dialig_delite")
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val am = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            if (!am.canScheduleExactAlarms() && res != getString(R.string.sabytie_no_pavedam)) {
                                spannable.setSpan(StrikethroughSpan(), t3, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                        val font = MainActivity.createFont(Typeface.NORMAL)
                        spannable.setSpan(clickableSpanEdit, t1 + 1, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(clickableSpanRemove, t2 + 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(CustomTypefaceSpan("", font), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
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
                    gc.add(Calendar.DATE, 1)
                }
            }
            for (i in sabytieList.indices) {
                binding.padzei.addView(sabytieList[i])
            }
        }
    }

    private fun sviatyiaView() {
        activity?.let { activity ->
            val sviatyia = MenuCaliandar.getPositionCaliandar(position)[4]
            val list = sviatyia.split("<br>")
            val density = resources.displayMetrics.density.toInt()
            for (i in list.indices) {
                var spannable = SpannableStringBuilder(list[i])
                val textView = TextView(activity)
                textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_text))
                textView.background = ContextCompat.getDrawable(activity, R.drawable.selector_default)
                when {
                    list.size == 1 -> textView.setPadding(5 * density, 5 * density, 10 * density, 5 * density)
                    i == 0 -> textView.setPadding(5 * density, 5 * density, 10 * density, 0)
                    i == list.size - 1 -> textView.setPadding(5 * density, 0, 10 * density, 5 * density)
                    else -> textView.setPadding(5 * density, 0, 10 * density, 0)
                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
                if (dzenNoch) {
                    textView.setBackgroundResource(R.drawable.selector_dark)
                    textView.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                }
                textView.typeface = MainActivity.createFont(Typeface.NORMAL)
                if (spannable.contains("<font")) {
                    val t1 = spannable.indexOf("<font color=#d00505>")
                    var t2 = spannable.indexOf("</font>")
                    if (t2 == -1) {
                        t2 = spannable.length
                    } else {
                        spannable = spannable.replace(t2, t2 + 7, "")
                    }
                    if (dzenNoch) {
                        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary_black)), t1, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)), t1, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    spannable = spannable.replace(t1, t1 + 20, "")
                } else if (spannable.contains("</font>")) {
                    val t2 = spannable.indexOf("</font>")
                    if (dzenNoch) {
                        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary_black)), 0, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)), 0, t2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    spannable = spannable.replace(t2, t2 + 7, "")
                }
                if (spannable.contains("<strong>")) {
                    val t1 = spannable.indexOf("<strong>")
                    var t2 = spannable.indexOf("</strong>")
                    if (t2 == -1) {
                        t2 = spannable.length
                    } else {
                        spannable = spannable.replace(t2, t2 + 9, "")
                    }
                    spannable.setSpan(CustomTypefaceSpan("", MainActivity.createFont(Typeface.BOLD)), t1, t2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannable = spannable.replace(t1, t1 + 8, "")
                } else if (spannable.contains("</strong>")) {
                    val t2 = spannable.indexOf("</strong>")
                    spannable.setSpan(CustomTypefaceSpan("", MainActivity.createFont(Typeface.BOLD)), 0, t2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannable = spannable.replace(t2, t2 + 9, "")
                }
                textView.text = spannable
                var drawer: Drawable? = null
                if (i == 0) {
                    drawer = when (MenuCaliandar.getPositionCaliandar(position)[12].toInt()) {
                        1 -> {
                            if (dzenNoch) ContextCompat.getDrawable(activity, R.drawable.znaki_krest_black)
                            else ContextCompat.getDrawable(activity, R.drawable.znaki_krest)
                        }
                        3 -> {
                            if (dzenNoch) ContextCompat.getDrawable(activity, R.drawable.znaki_krest_v_polukruge_black)
                            else ContextCompat.getDrawable(activity, R.drawable.znaki_krest_v_polukruge)
                        }
                        4 -> {
                            if (dzenNoch) ContextCompat.getDrawable(activity, R.drawable.znaki_ttk_black_black)
                            else ContextCompat.getDrawable(activity, R.drawable.znaki_ttk)
                        }
                        5 -> {
                            if (dzenNoch) ContextCompat.getDrawable(activity, R.drawable.znaki_ttk_whate)
                            else ContextCompat.getDrawable(activity, R.drawable.znaki_ttk_black)
                        }
                        else -> null
                    }
                }
                drawer?.setBounds(0, 0, 20 * density, 20 * density)
                textView.compoundDrawablePadding = 5 * density
                textView.setCompoundDrawables(drawer, null, null, null)
                textView.setOnClickListener {
                    val intent = Intent(activity, Opisanie::class.java)
                    intent.putExtra("mun", MenuCaliandar.getPositionCaliandar(position)[2].toInt() + 1)
                    intent.putExtra("day", MenuCaliandar.getPositionCaliandar(position)[1].toInt())
                    intent.putExtra("year", MenuCaliandar.getPositionCaliandar(position)[3].toInt())
                    startActivity(intent)
                }
                binding.listSviatyia.addView(textView)
                val llp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                llp.gravity = Gravity.START or Gravity.CENTER_HORIZONTAL
                textView.layoutParams = llp
            }
        }
    }

    companion object {
        var editCaliandarTitle = ""
        fun newInstance(position: Int): CaliandarFull {
            val fragment = CaliandarFull()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }
}
