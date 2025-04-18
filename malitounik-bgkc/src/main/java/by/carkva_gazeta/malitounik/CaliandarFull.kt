package by.carkva_gazeta.malitounik

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import by.carkva_gazeta.malitounik.databinding.CalaindarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar


class CaliandarFull : BaseFragment(), View.OnClickListener {
    private val dzenNoch: Boolean
        get() = (requireActivity() as BaseActivity).getBaseDzenNoch()
    private var sabytieTitle = ""
    private var position = 0
    private var mLastClickTime: Long = 0
    private var binding: CalaindarBinding? = null
    private var sabytieJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        sabytieJob?.cancel()
        binding = null
    }

    private fun getDayOfYear() = MenuCaliandar.getPositionCaliandar(position)[24].toInt()

    private fun getYear() = MenuCaliandar.getPositionCaliandar(position)[3].toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CalaindarBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BaseActivity)?.let {
            binding?.let { binding ->
                val nedelName = it.resources.getStringArray(R.array.dni_nedeli)
                val monthName = it.resources.getStringArray(R.array.meciac)
                val c = Calendar.getInstance()
                val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
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
                val dzn = nedelName[MenuCaliandar.getPositionCaliandar(position)[0].toInt()]
                val chislo = MenuCaliandar.getPositionCaliandar(position)[1]
                val mesiac = if (MenuCaliandar.getPositionCaliandar(position)[3].toInt() != c[Calendar.YEAR]) getString(R.string.mesiach, monthName[MenuCaliandar.getPositionCaliandar(position)[2].toInt()], MenuCaliandar.getPositionCaliandar(position)[3])
                else monthName[MenuCaliandar.getPositionCaliandar(position)[2].toInt()]
                val ssb = SpannableStringBuilder()
                ssb.append(dzn)
                ssb.append("\n")
                ssb.append(chislo)
                ssb.append("\n")
                ssb.append(mesiac)
                ssb.setSpan(AbsoluteSizeSpan(80, true), dzn.length + 1, dzn.length + chislo.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.setSpan(StyleSpan(Typeface.BOLD), 0, dzn.length + 1 + chislo.length + 1 + mesiac.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (k.getBoolean("adminDayInYear", false)) {
                    ssb.append("\n")
                    val adminShowDayInYear = getString(R.string.admin_show_day_in_year, MenuCaliandar.getPositionCaliandar(position)[24], MenuCaliandar.getPositionCaliandar(position)[22])
                    ssb.append(adminShowDayInYear)
                }
                binding.textChisloCalendara.text = ssb
                if (!MenuCaliandar.getPositionCaliandar(position)[4].contains("no_sviatyia")) {
                    if (dzenNoch) {
                        binding.sviatyia.setBackgroundResource(R.drawable.selector_dark)
                        binding.sviatyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    }
                    val drawer = when (MenuCaliandar.getPositionCaliandar(position)[12].toInt()) {
                        1 -> {
                            if (dzenNoch) ContextCompat.getDrawable(it, R.drawable.znaki_krest_black)
                            else ContextCompat.getDrawable(it, R.drawable.znaki_krest)
                        }

                        3 -> {
                            if (dzenNoch) ContextCompat.getDrawable(it, R.drawable.znaki_krest_v_polukruge_black)
                            else ContextCompat.getDrawable(it, R.drawable.znaki_krest_v_polukruge)
                        }

                        4 -> {
                            if (dzenNoch) ContextCompat.getDrawable(it, R.drawable.znaki_ttk_black_black)
                            else ContextCompat.getDrawable(it, R.drawable.znaki_ttk)
                        }

                        5 -> {
                            if (dzenNoch) ContextCompat.getDrawable(it, R.drawable.znaki_ttk_whate)
                            else ContextCompat.getDrawable(it, R.drawable.znaki_ttk_black)
                        }

                        else -> null
                    }
                    var dataSviatyia = MenuCaliandar.getPositionCaliandar(position)[4]
                    if (dzenNoch) dataSviatyia = dataSviatyia.replace("#d00505", "#ff6666")
                    val sviatyia = MainActivity.fromHtml(dataSviatyia).toSpannable()
                    if (drawer != null) {
                        binding.znakTipicona2.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                val density = it.resources.displayMetrics.density.toInt()
                                val finalWidth = binding.znakTipicona2.measuredWidth
                                val leftMargin = finalWidth + 5 * density
                                if (finalWidth > 0) binding.znakTipicona2.viewTreeObserver.removeOnPreDrawListener(this)
                                val t1 = sviatyia.indexOf("\n")
                                val length = if (t1 != -1) t1
                                else sviatyia.length
                                sviatyia.setSpan(MarginSviatyia(1, leftMargin), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                binding.znakTipicona2.setImageDrawable(drawer)
                                binding.znakTipicona2.visibility = View.VISIBLE
                                binding.sviatyia.text = sviatyia
                                return true
                            }
                        })
                    } else {
                        binding.sviatyia.text = sviatyia
                    }
                    binding.sviatyia.setOnClickListener(this)
                } else {
                    binding.polosa1.visibility = View.GONE
                    binding.polosa2.visibility = View.GONE
                    binding.sviatyiaView.visibility = View.GONE
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
                        if (MenuCaliandar.getPositionCaliandar(position)[6].contains("Айцоў першых 6-ці Ўсяленскіх сабораў", true) || MenuCaliandar.getPositionCaliandar(position)[6].contains("СУСТРЭЧА ГОСПАДА НАШАГА ІСУСА ХРЫСТА", true)) binding.textCviatyGlavnyia.setOnClickListener(this@CaliandarFull)
                        else if (MenuCaliandar.getPositionCaliandar(position)[6].contains("нядзел", true) || MenuCaliandar.getPositionCaliandar(position)[6].contains("сьветл", true)) binding.textCviatyGlavnyia.isEnabled = false
                        else binding.textCviatyGlavnyia.setOnClickListener(this@CaliandarFull)
                    }
                }
                when (MenuCaliandar.getPositionCaliandar(position)[7].toInt()) {
                    1 -> {
                        binding.textChisloCalendara.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        binding.textChisloCalendara.setBackgroundResource(R.drawable.selector_bez_posta)
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
                            binding.textPost.text = it.resources.getString(R.string.No_post)
                        }
                    }

                    2 -> {
                        binding.chytanne.setBackgroundResource(R.drawable.selector_post)
                        binding.textChytanne.setBackgroundResource(R.drawable.selector_post)
                        binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_post)
                        binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_post)
                        binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_post)
                        binding.textChisloCalendara.setBackgroundResource(R.drawable.selector_post)
                        binding.textChisloCalendara.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_post_black)
                        else binding.kniga.setImageResource(R.drawable.book_post)
                        binding.textPamerlyia.setBackgroundResource(R.drawable.selector_post)
                        if (MenuCaliandar.getPositionCaliandar(position)[0].toInt() == Calendar.FRIDAY) {
                            binding.PostFish.visibility = View.VISIBLE
                            binding.textPost.visibility = View.VISIBLE
                            if (dzenNoch) {
                                binding.PostFish.setImageResource(R.drawable.fishe_whate)
                            }
                        }
                    }

                    3 -> {
                        binding.textChisloCalendara.setBackgroundResource(R.drawable.selector_strogi_post)
                        binding.textChisloCalendara.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
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
                        binding.textPost.text = it.resources.getString(R.string.Strogi_post)
                        binding.textPamerlyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textPost.visibility = View.VISIBLE
                        binding.PostFish.visibility = View.VISIBLE
                        if (dzenNoch) binding.PostFish.setImageResource(R.drawable.fishe_red_black)
                        else binding.PostFish.setImageResource(R.drawable.fishe_red)
                    }

                    else -> {
                        binding.textChisloCalendara.setBackgroundResource(R.color.colorDivider)
                        binding.textChisloCalendara.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                        if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_divider_black)
                        else binding.kniga.setImageResource(R.drawable.book_divider)
                    }
                }
                if (MenuCaliandar.getPositionCaliandar(position)[5].contains("1") || MenuCaliandar.getPositionCaliandar(position)[5].contains("2") || MenuCaliandar.getPositionCaliandar(position)[5].contains("3")) {
                    binding.textChisloCalendara.setBackgroundResource(R.drawable.selector_red)
                    if (dzenNoch) binding.kniga.setImageResource(R.drawable.book_red_black)
                    else binding.kniga.setImageResource(R.drawable.book_red)
                    binding.textChisloCalendara.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                    if (MenuCaliandar.getPositionCaliandar(position)[7].toInt() != 3) {
                        binding.chytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.chytanne.setBackgroundResource(R.drawable.selector_red)
                        binding.textChytanne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChytanne.setBackgroundResource(R.drawable.selector_red)
                        binding.textChytanneSviatyiaDop.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChytanneSviatyiaDop.setBackgroundResource(R.drawable.selector_red)
                        binding.textChytanneSviatyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textChytanneSviatyia.setBackgroundResource(R.drawable.selector_red)
                        binding.textPamerlyia.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textPamerlyia.setBackgroundResource(R.drawable.selector_red)
                        binding.textBlaslavenne.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                        binding.textBlaslavenne.setBackgroundResource(R.drawable.selector_red)
                    }
                }
                if (MenuCaliandar.getPositionCaliandar(position)[5].contains("2")) {
                    binding.textCviatyGlavnyia.typeface = MainActivity.createFont(Typeface.NORMAL)
                    if (MenuCaliandar.getPositionCaliandar(position)[6].contains("<strong>")) {
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
                if (MenuCaliandar.getPositionCaliandar(position)[5] == "1" || MenuCaliandar.getPositionCaliandar(position)[5] == "2") {
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
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        (activity as? BaseActivity)?.let {
            when (v?.id ?: 0) {
                R.id.textTon -> {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                    val ton = MenuCaliandar.getPositionCaliandar(position)[20].toInt()
                    intent.putExtra("resurs", "ton$ton")
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", "Тон $ton")
                    startActivity(intent)
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
                    var ton = MenuCaliandar.getPositionCaliandar(position)[20].toInt()
                    var denNedzeli = MenuCaliandar.getPositionCaliandar(position)[0].toInt()
                    val denNedzeliUtran = denNedzeli
                    if (MenuCaliandar.getPositionCaliandar(position)[22].toInt() in 0..41) {
                        ton = 0
                        denNedzeli = Calendar.SUNDAY
                    }
                    val dialogCalindarGrid = DialogCalindarGrid.getInstance(colorDialog, ton, denNedzeliUtran, denNedzeli, daysv, munsv, MenuCaliandar.getPositionCaliandar(position)[22].toInt(), MenuCaliandar.getPositionCaliandar(position)[4], MenuCaliandar.getPositionCaliandar(position)[3].toInt(), MenuCaliandar.getPositionCaliandar(position)[1].toInt(), MenuCaliandar.getPositionCaliandar(position)[2].toInt() + 1, MenuCaliandar.getPositionCaliandar(position)[24])
                    dialogCalindarGrid.show(childFragmentManager, "grid")
                }

                R.id.textCviatyGlavnyia -> {
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

                R.id.sviatyia -> {
                    val i = Intent(it, Opisanie::class.java)
                    i.putExtra("mun", MenuCaliandar.getPositionCaliandar(position)[2].toInt() + 1)
                    i.putExtra("day", MenuCaliandar.getPositionCaliandar(position)[1].toInt())
                    i.putExtra("year", MenuCaliandar.getPositionCaliandar(position)[3].toInt())
                    startActivity(i)
                }

                R.id.textChytanneSviatyia -> if (it.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", MenuCaliandar.getPositionCaliandar(position)[10])
                    intent.putExtra("mun", MenuCaliandar.getPositionCaliandar(position)[2].toInt())
                    intent.putExtra("day", MenuCaliandar.getPositionCaliandar(position)[1].toInt())
                    startActivity(intent)
                } else {
                    it.installFullMalitounik()
                }

                R.id.textChytanne -> if (it.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", MenuCaliandar.getPositionCaliandar(position)[9])
                    intent.putExtra("mun", MenuCaliandar.getPositionCaliandar(position)[2].toInt())
                    intent.putExtra("day", MenuCaliandar.getPositionCaliandar(position)[1].toInt())
                    startActivity(intent)
                } else {
                    it.installFullMalitounik()
                }

                R.id.textChytanneSviatyiaDop -> if (it.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.CHYTANNE)
                    intent.putExtra("cytanne", MenuCaliandar.getPositionCaliandar(position)[11])
                    intent.putExtra("mun", MenuCaliandar.getPositionCaliandar(position)[2].toInt())
                    intent.putExtra("day", MenuCaliandar.getPositionCaliandar(position)[1].toInt())
                    startActivity(intent)
                } else {
                    it.installFullMalitounik()
                }

                R.id.maranata -> if (it.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.MARANATA)
                    intent.putExtra("cytanneMaranaty", MenuCaliandar.getPositionCaliandar(position)[13])
                    intent.putExtra("mun", MenuCaliandar.getPositionCaliandar(position)[2].toInt())
                    intent.putExtra("day", MenuCaliandar.getPositionCaliandar(position)[1].toInt())
                    startActivity(intent)
                } else {
                    it.installFullMalitounik()
                }
            }
        }
    }

    fun sabytieView(sabytieTitle: String) {
        activity?.let { activity ->
            binding?.padzei?.removeAllViewsInLayout()
            val gc = Calendar.getInstance() as GregorianCalendar
            val sabytieList = ArrayList<TextViewCustom>()
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
                    var dayofyear = gc[Calendar.DAY_OF_YEAR]
                    if (!gc.isLeapYear(yaerw) && dayofyear > 59) {
                        dayofyear++
                    }
                    if (dayofyear == getDayOfYear() && gc[Calendar.YEAR] == getYear()) {
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
                        val density = activity.resources.displayMetrics.density.toInt()
                        val realpadding = 5 * density
                        val textViewT = TextViewCustom(activity)
                        textViewT.text = title
                        textViewT.setPadding(realpadding, realpadding, realpadding, realpadding)
                        textViewT.typeface = MainActivity.createFont(Typeface.BOLD)

                        textViewT.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
                        textViewT.setBackgroundColor(Color.parseColor(Sabytie.getColors(p.color)))
                        sabytieList.add(textViewT)
                        val textView = TextViewCustom(activity)
                        textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary_text))
                        textView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorDivider))
                        textView.setPadding(realpadding, realpadding, realpadding, realpadding)
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
                            SpannableString(activity.resources.getString(R.string.sabytieKali, data, time, res))
                        } else {
                            SpannableString(activity.resources.getString(R.string.sabytieDoKuda, data, time, dataK, timeK, res))
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
                                binding?.scroll?.post { binding?.scroll?.fullScroll(ScrollView.FOCUS_DOWN) }
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
                            binding?.scroll?.post {
                                activity.intent?.removeExtra("sabytieView")
                                binding?.scroll?.fullScroll(ScrollView.FOCUS_DOWN)
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
                binding?.padzei?.addView(sabytieList[i])
            }
        }
    }

    private class MarginSviatyia(val lines: Int, val margin: Int) : LeadingMarginSpan2 {
        override fun getLeadingMargin(first: Boolean): Int {
            return if (first) margin
            else 0
        }

        override fun drawLeadingMargin(c: Canvas?, p: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence?, start: Int, end: Int, first: Boolean, layout: Layout?) {
        }

        override fun getLeadingMarginLineCount() = lines
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
