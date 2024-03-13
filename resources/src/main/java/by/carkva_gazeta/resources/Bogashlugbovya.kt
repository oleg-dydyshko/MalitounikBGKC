package by.carkva_gazeta.resources

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.CaliandarMun
import by.carkva_gazeta.malitounik.DialogBrightness
import by.carkva_gazeta.malitounik.DialogFontSize
import by.carkva_gazeta.malitounik.DialogHelpFullScreenSettings
import by.carkva_gazeta.malitounik.DialogHelpShare
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.EditTextCustom
import by.carkva_gazeta.malitounik.InteractiveScrollView
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MalitvyPasliaPrychascia
import by.carkva_gazeta.malitounik.MenuCaliandar
import by.carkva_gazeta.malitounik.MenuVybranoe
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.SlugbovyiaTextu
import by.carkva_gazeta.malitounik.VybranoeData
import by.carkva_gazeta.resources.databinding.BogasluzbovyaBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Calendar
import java.util.GregorianCalendar


class Bogashlugbovya : BaseActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, InteractiveScrollView.OnInteractiveScrollChangedCallback, LinkMovementMethodCheck.LinkMovementMethodCheckListener, DialogHelpShare.DialogHelpShareListener, DialogHelpFullScreen.DialogFullScreenHelpListener, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener, DialogVybranoeBibleList.DialogVybranoeBibleListListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var resurs = ""
    private var men = true
    private var positionY = 0
    private var title = ""
    private var mActionDown = false
    private var mAutoScroll = true
    private lateinit var binding: BogasluzbovyaBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var diffScroll = false
    private var aliert8 = ""
    private var aliert9 = ""
    private var findPosition = 0
    private var firstTextPosition = ""
    private val findListSpans = ArrayList<ArrayList<SpanStr>>()
    private var animatopRun = false
    private var chechZmena = false
    private var checkLiturgia = 0
    private var raznica = 400
    private var dayOfYear = "1"
    private var checkDayOfYear = false
    private var sviaty = false
    private var daysv = 1
    private var munsv = 0
    private var vybranoePosition = -1
    private var linkMovementMethodCheck: LinkMovementMethodCheck? = null
    private var orientation = Configuration.ORIENTATION_UNDEFINED
    private val zmenyiaChastki = ZmenyiaChastki()
    private val c = Calendar.getInstance()
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                c.set(arrayList[3].toInt(), arrayList[2].toInt(), arrayList[1].toInt(), 0, 0, 0)
                setDatacalendar(null)
            }
        }
    }

    companion object {
        val resursMap = ArrayMap<String, Int>()

        init {
            resursMap()
        }

        private fun resursMap() {
            val fields = R.raw::class.java.fields
            for (element in fields) {
                val name = element.name
                resursMap[name] = element.getInt(name)
            }
            val fields2 = by.carkva_gazeta.malitounik.R.raw::class.java.fields
            for (element in fields2) {
                val name = element.name
                resursMap[name] = element.getInt(name)
            }
            R.raw.mm_21_01_paczesnaha_maksima_vyznaucy_viaczernia
        }

        fun setVybranoe(context: Context, resurs: String, title: String): Boolean {
            var check = true
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                val gson = Gson()
                if (file.exists() && MenuVybranoe.vybranoe.isEmpty()) {
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) {
                        MenuVybranoe.vybranoe.removeAt(i)
                        check = false
                        break
                    }
                }
                if (check) {
                    MenuVybranoe.vybranoe.add(0, VybranoeData(vybranoeIndex(), resurs, title))
                }
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                file.writer().use {
                    it.write(gson.toJson(MenuVybranoe.vybranoe, type))
                }
            } catch (t: Throwable) {
                file.delete()
                check = false
            }
            return check
        }

        fun vybranoeIndex(): Long {
            var result: Long = 1
            val vybranoe = MenuVybranoe.vybranoe
            if (vybranoe.size != 0) {
                vybranoe.forEach {
                    if (result < it.id) result = it.id
                }
                result++
            }
            return result
        }

        fun checkVybranoe(context: Context, resurs: String): Boolean {
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            if (!file.exists()) return false
            try {
                if (MenuVybranoe.vybranoe.isEmpty()) {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    MenuVybranoe.vybranoe.addAll(gson.fromJson(file.readText(), type))
                }
                for (i in 0 until MenuVybranoe.vybranoe.size) {
                    if (MenuVybranoe.vybranoe[i].resurs.intern() == resurs) return true
                }
            } catch (t: Throwable) {
                file.delete()
                return false
            }
            return false
        }
    }

    private fun findAllAsanc(noNext: Boolean = true) {
        CoroutineScope(Dispatchers.Main).launch {
            findRemoveSpan()
            findAll(binding.textSearch.text.toString().trim())
            findCheckPosition()
            if (noNext) findNext(false)
        }
    }

    private fun findAll(search: String) {
        var position = 0
        val arraySearsh = ArrayList<String>()
        if (search.length >= 3) {
            val text = binding.textView.text as SpannableString
            val t1 = search.indexOf(" ")
            if (t1 != -1) {
                val charList = arrayOf(",", "*", "(", ")", ".", ";", ":", "[", "]", "?")
                var charTest = false
                for (element in charList) {
                    val t2 = search.indexOf(element)
                    if (t2 != -1) {
                        charTest = true
                        break
                    }
                }
                if (!charTest) {
                    var pos = 0
                    val sub1 = search.substring(0, t1)
                    val sub2 = search.substring(t1).trim()
                    while (true) {
                        val strSub1Pos = text.indexOf(sub1, pos, true)
                        if (strSub1Pos != -1) {
                            pos = strSub1Pos + 1
                            val strSub2Pos = text.indexOf(sub2, strSub1Pos + sub1.length, true)
                            if (strSub2Pos == -1) {
                                continue
                            }
                            val subTest = strSub2Pos - strSub1Pos - sub1.length
                            if (subTest < 10) {
                                var subResult = text.substring(strSub1Pos + sub1.length, strSub2Pos)
                                if (subResult == "") subResult = " "
                                if (!isLetterOrDigit(subResult.toCharArray())) {
                                    val subSearch = text.substring(strSub1Pos, strSub1Pos + sub1.length) + subResult + text.substring(strSub2Pos, strSub2Pos + sub2.length)
                                    arraySearsh.add(subSearch)
                                }
                            }
                        } else {
                            break
                        }
                    }
                } else {
                    arraySearsh.add(search)
                }
            } else {
                arraySearsh.add(search)
            }
            for (i in 0 until arraySearsh.size) {
                val searchLig = arraySearsh[i].length
                while (true) {
                    val strPosition = text.indexOf(arraySearsh[i], position, true)
                    if (strPosition != -1) {
                        val list = ArrayList<SpanStr>()
                        for (e in strPosition..strPosition + searchLig) {
                            list.add(SpanStr(getColorSpans(text.getSpans(e, e + 1, ForegroundColorSpan::class.java)), e))
                        }
                        findListSpans.add(list)
                        text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        position = strPosition + 1
                    } else {
                        break
                    }
                }
            }
        }
    }

    private fun isLetterOrDigit(znaki: CharArray): Boolean {
        for (i in znaki) {
            if (i.isLetterOrDigit()) return true
        }
        return false
    }

    private fun findCheckPosition() {
        if (findListSpans.isNotEmpty()) {
            binding.textView.layout?.let { layout ->
                val lineForVertical = layout.getLineForVertical(positionY)
                for (i in 0 until findListSpans.size) {
                    if (lineForVertical <= layout.getLineForOffset(findListSpans[i][0].start)) {
                        findPosition = i
                        break
                    }
                }
            }
        } else {
            findPosition = 0
            binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.niama)
        }
    }

    private fun findRemoveSpan() {
        val text = binding.textView.text as SpannableString
        if (findListSpans.isNotEmpty()) {
            findListSpans.forEach { findListSpans ->
                findListSpans.forEach {
                    text.setSpan(ForegroundColorSpan(it.color), it.start, it.start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            if (findListSpans.size >= findPosition) findPosition = 0
            findListSpans.clear()
        }
        val spans = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
        spans.forEach {
            text.removeSpan(it)
        }
    }

    private fun findNext(next: Boolean = true, previous: Boolean = false) {
        val findPositionOld = findPosition
        if (next) {
            if (previous) findPosition--
            else findPosition++
        }
        if (findListSpans.isNotEmpty()) {
            if (findPosition == -1) {
                findPosition = findListSpans.size - 1
            }
            if (findPosition == findListSpans.size) {
                findPosition = 0
            }
            val text = binding.textView.text as SpannableString
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPositionOld][0].start, findListSpans[findPositionOld][findListSpans[findPositionOld].size - 1].start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, findPosition + 1, findListSpans.size)
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), findListSpans[findPosition][0].start, findListSpans[findPosition][findListSpans[findPosition].size - 1].start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textView.layout?.let { layout ->
                val line = layout.getLineForOffset(findListSpans[findPosition][0].start)
                val y = layout.getLineTop(line)
                val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                anim.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        animatopRun = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        animatopRun = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })
                anim.setDuration(1000).start()
            }
        }
    }

    private fun getColorSpans(colorSpan: Array<out ForegroundColorSpan>): Int {
        var color = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)
        if (dzenNoch) color = ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite)
        if (colorSpan.isNotEmpty()) {
            color = colorSpan[colorSpan.size - 1].foregroundColor
        }
        return color
    }

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.textView.layout?.let { layout ->
            var lineForVertical = layout.getLineForVertical(positionY)
            var textForVertical = binding.textView.text.substring(layout.getLineStart(lineForVertical), layout.getLineEnd(lineForVertical)).trim()
            if (textForVertical == "" && lineForVertical != 0) {
                lineForVertical--
                textForVertical = binding.textView.text.substring(layout.getLineStart(lineForVertical), layout.getLineEnd(lineForVertical)).trim()
            }
            binding.textView.textSize = fontBiblia
            binding.textView.post {
                val strPosition = binding.textView.text.indexOf(textForVertical, ignoreCase = true)
                val line = layout.getLineForOffset(strPosition)
                val y = layout.getLineTop(line)
                binding.scrollView2.scrollTo(0, y)
            }
        }
    }

    override fun onScroll(t: Int, oldt: Int) {
        positionY = t
        setMovementMethodscrollY()
        binding.textView.layout?.let { layout ->
            val textForVertical = binding.textView.text.substring(layout.getLineStart(layout.getLineForVertical(positionY)), layout.getLineEnd(layout.getLineForVertical(positionY))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
            if (binding.find.visibility == View.VISIBLE && !animatopRun) {
                if (findListSpans.isNotEmpty()) {
                    val text = binding.textView.text as SpannableString
                    for (i in 0 until findListSpans.size) {
                        if (layout.getLineForOffset(findListSpans[i][0].start) == layout.getLineForVertical(positionY)) {
                            var ii = i + 1
                            if (i == 0) ii = 1
                            findPosition = i
                            var findPositionOld = if (t >= oldt) i - 1
                            else i + 1
                            if (findPositionOld == -1) findPositionOld = findListSpans.size - 1
                            if (findPositionOld == findListSpans.size) findPositionOld = 0
                            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPositionOld][0].start, findListSpans[findPositionOld][findListSpans[findPositionOld].size - 1].start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (findPosition != ii) binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, ii, findListSpans.size)
                            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), findListSpans[i][0].start, findListSpans[i][findListSpans[findPosition].size - 1].start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun setDatacalendar(savedInstanceState: Bundle?) {
        zmenyiaChastki.setArrayData(MenuCaliandar.getDataCalaindar(c[Calendar.DATE], c[Calendar.MONTH], c[Calendar.YEAR]))
        loadData(savedInstanceState)
        val c2 = Calendar.getInstance()
        if (c[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR] && c[Calendar.YEAR] == c2[Calendar.YEAR]) {
            binding.titleToolbar.text = title
        } else {
            binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bogaslujbovyia_data, title, c[Calendar.DATE], resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[c[Calendar.MONTH]], c[Calendar.YEAR])
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = BogasluzbovyaBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        resurs = intent.extras?.getString("resurs") ?: ""
        title = intent.extras?.getString("title") ?: ""
        spid = k.getInt("autoscrollSpid", 60)
        val autoscrollOFF = intent.extras?.containsKey("autoscrollOFF") ?: false
        if (autoscrollOFF) {
            mAutoScroll = false
        }
        binding.scrollView2.setOnScrollChangedCallback(this)
        binding.constraint.setOnTouchListener(this)
        if (savedInstanceState != null) {
            mAutoScroll = savedInstanceState.getBoolean("mAutoScroll")
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            orientation = savedInstanceState.getInt("orientation")
            MainActivity.dialogVisable = false
            if (savedInstanceState.getBoolean("seach")) {
                binding.find.visibility = View.VISIBLE
            }
            c.set(Calendar.DAY_OF_YEAR, savedInstanceState.getInt("day_of_year"))
            c.set(Calendar.YEAR, savedInstanceState.getInt("year"))
            vybranoePosition = savedInstanceState.getInt("vybranoePosition")
            if (vybranoePosition != -1) {
                resurs = MenuVybranoe.vybranoe[vybranoePosition].resurs
                title = MenuVybranoe.vybranoe[vybranoePosition].data
            }
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            vybranoePosition = intent.extras?.getInt("vybranaePos", -1) ?: -1
        }
        if (resurs == "1" || resurs == "2" || resurs == "3") {
            DialogVybranoeBibleList.biblia = resurs
            val text = SpannableString(title)
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialogVybranoeList = DialogVybranoeBibleList()
                    dialogVybranoeList.setDialogVybranoeBibleListListener(this@Bogashlugbovya)
                    dialogVybranoeList.show(supportFragmentManager, "vybranoeBibleList")
                }
            }, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textView.text = text
            binding.titleToolbar.text = title
        } else {
            setDatacalendar(savedInstanceState)
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.textView.textSize = fontBiblia
        DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta))
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.seekBarBrighess.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
        }
        men = checkVybranoe(this, resurs)
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize(fontBiblia)
                }
                startProcent()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        bindingprogress.seekBarBrighess.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (MainActivity.brightness != progress) {
                    MainActivity.brightness = progress
                    val lp = window.attributes
                    lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                    window.attributes = lp
                    bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                    MainActivity.checkBrightness = false
                }
                startProcent()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.scrollView2.setOnBottomReachedListener(object : InteractiveScrollView.OnBottomReachedListener {
            override fun onBottomReached(checkDiff: Boolean) {
                diffScroll = checkDiff
                if (diffScroll) {
                    autoscroll = false
                    stopAutoScroll()
                }
                invalidateOptionsMenu()
            }

            override fun onTouch(action: Boolean) {
                mActionDown = action
            }
        })
        if (intent.extras?.getBoolean("isVybranae", false) == true) {
            binding.textViewNext.visibility = View.VISIBLE
            var pos = vybranoePosition + 1
            if (MenuVybranoe.vybranoe.size == pos) pos = 0
            binding.textViewNext.text = MenuVybranoe.vybranoe[pos].data
            binding.textViewNext.setOnClickListener {
                nextText()
            }
        }
        binding.textView.movementMethod = setLinkMovementMethodCheck()
        setTollbarTheme()
    }

    override fun onAllDeliteBible() {
        if (MenuVybranoe.vybranoe.size == 0) {
            onBack()
        } else {
            if (MenuVybranoe.vybranoe.size < 2) {
                binding.textViewNext.visibility = View.GONE
            }
            nextText()
        }
    }

    private fun nextText() {
        vybranoePosition += 1
        if (MenuVybranoe.vybranoe.size == vybranoePosition) vybranoePosition = 0
        resurs = MenuVybranoe.vybranoe[vybranoePosition].resurs
        title = MenuVybranoe.vybranoe[vybranoePosition].data
        var pos1 = vybranoePosition + 1
        if (MenuVybranoe.vybranoe.size == pos1) pos1 = 0
        binding.textViewNext.text = MenuVybranoe.vybranoe[pos1].data
        mAutoScroll = false
        diffScroll = false
        autoscroll = false
        stopAutoScroll()
        invalidateOptionsMenu()
        val duration: Long = 1000
        ObjectAnimator.ofInt(binding.scrollView2, "scrollY", 0).setDuration(duration).start()
        if (resurs == "1" || resurs == "2" || resurs == "3") {
            DialogVybranoeBibleList.biblia = resurs
            val dialogVybranoeList = DialogVybranoeBibleList()
            dialogVybranoeList.setDialogVybranoeBibleListListener(this@Bogashlugbovya)
            dialogVybranoeList.show(supportFragmentManager, "vybranoeBibleList")
            val text = SpannableString(title)
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    dialogVybranoeList.setDialogVybranoeBibleListListener(this@Bogashlugbovya)
                    dialogVybranoeList.show(supportFragmentManager, "vybranoeBibleList")
                }
            }, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textView.text = text
            binding.titleToolbar.text = title
        } else {
            setDatacalendar(null)
        }
    }

    override fun linkMovementMethodCheckOnTouch(onTouch: Boolean) {
        mActionDown = onTouch
    }

    private fun setMovementMethodscrollY() {
        linkMovementMethodCheck?.getScrollY(positionY)
    }

    private fun setLinkMovementMethodCheck(): LinkMovementMethodCheck? {
        linkMovementMethodCheck = LinkMovementMethodCheck()
        linkMovementMethodCheck?.setLinkMovementMethodCheckListener(this)
        return linkMovementMethodCheck
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.toolbar.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
                resetTollbar(layoutParams)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.titleToolbar.isSingleLine = false
                binding.titleToolbar.isSelected = true
                resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    resetTollbar(layoutParams)
                    TransitionManager.beginDelayedTransition(binding.toolbar)
                }
            }
            TransitionManager.beginDelayedTransition(binding.toolbar)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    private fun loadData(savedInstanceState: Bundle?) = CoroutineScope(Dispatchers.Main).launch {
        val liturgia = resurs == "lit_jana_zalatavusnaha" || resurs == "lit_jan_zalat_vielikodn" || resurs == "lit_vasila_vialikaha" || resurs == "abiednica"
        val res = withContext(Dispatchers.IO) {
            zmenyiaChastki.setDzenNoch(dzenNoch)
            chechZmena = false
            val builder = StringBuilder()
            val id = resursMap[resurs] ?: by.carkva_gazeta.malitounik.R.raw.bogashlugbovya_error
            var nochenia = false
            val inputStream = resources.openRawResource(id)
            val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val color = if (dzenNoch) "<font color=\"#f44336\">"
            else "<font color=\"#d00505\">"
            val slugbovyiaTextu = SlugbovyiaTextu()
            raznica = zmenyiaChastki.raznica()
            dayOfYear = zmenyiaChastki.dayOfYear()
            var zmennyiaCastkiTitle = ""
            val cal = GregorianCalendar()
            val dayOfYar = if (cal.isLeapYear(cal[Calendar.YEAR])) 0
            else 1
            checkDayOfYear = if (liturgia) slugbovyiaTextu.checkLiturgia(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR] + dayOfYar, c[Calendar.YEAR])[22].toInt(), dayOfYear.toInt(), zmenyiaChastki.getYear())
            else slugbovyiaTextu.checkViachernia(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR] + dayOfYar, c[Calendar.YEAR])[22].toInt(), dayOfYear.toInt(), zmenyiaChastki.getYear())
            if (liturgia && (checkDayOfYear || slugbovyiaTextu.checkLiturgia(raznica, c[Calendar.DAY_OF_YEAR] + dayOfYar, zmenyiaChastki.getYear()))) {
                chechZmena = true
                val resours = slugbovyiaTextu.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                val idZmenyiaChastki = resursMap[resours] ?: by.carkva_gazeta.malitounik.R.raw.bogashlugbovya_error
                zmennyiaCastkiTitle = slugbovyiaTextu.getTitle(resours)
                nochenia = slugbovyiaTextu.checkFullChtenia(idZmenyiaChastki)
            }
            val viachernia = resurs == "lit_raniej_asviaczanych_darou" || resurs == "viaczerniaja_sluzba_sztodzionnaja_biez_sviatara" || resurs == "viaczernia_niadzelnaja" || resurs == "viaczernia_liccia_i_blaslavenne_chliabou" || resurs == "viaczernia_na_kozny_dzen" || resurs == "viaczernia_u_vialikim_poscie"
            if (viachernia && (checkDayOfYear || slugbovyiaTextu.checkViachernia(raznica, c[Calendar.DAY_OF_YEAR] + dayOfYar, zmenyiaChastki.getYear()))) {
                chechZmena = true
                checkLiturgia = 1
            }
            invalidateOptionsMenu()
            reader.forEachLine {
                var line = it
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
                line = line.replace("NOCH", "")
                when {
                    resurs.contains("ton") -> {
                        line = line.replace("TRAPARN", "")
                        line = line.replace("TRAPARK", "")
                        line = line.replace("PRAKIMENN", "")
                        line = line.replace("PRAKIMENK", "")
                        line = line.replace("ALILUIAN", "")
                        line = line.replace("ALILUIAK", "")
                        line = line.replace("PRICHASNIKN", "")
                        line = line.replace("PRICHASNIKK", "")
                        builder.append(line)
                    }

                    liturgia -> {
                        if (line.contains("KANDAK")) {
                            line = line.replace("KANDAK", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append(getString(by.carkva_gazeta.malitounik.R.string.gl_tyt, zmennyiaCastkiTitle)).append("<br><br>\n")
                            }
                            try {
                                if (dayOfWeek == 1) {
                                    builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(1))
                                } else {
                                    builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 1))
                                }
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                            }
                        }
                        if (line.contains("PRAKIMEN")) {
                            line = line.replace("PRAKIMEN", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append(getString(by.carkva_gazeta.malitounik.R.string.gl_tyt, zmennyiaCastkiTitle)).append("<br><br>\n")
                            }
                            try {
                                if (dayOfWeek == 1) {
                                    builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(2))
                                } else {
                                    builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 2))
                                }
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                            }
                        }
                        if (line.contains("ALILUIA")) {
                            line = line.replace("ALILUIA", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append(getString(by.carkva_gazeta.malitounik.R.string.gl_tyt, zmennyiaCastkiTitle)).append("<br><br>\n")
                            }
                            try {
                                if (dayOfWeek == 1) {
                                    builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(3))
                                } else {
                                    builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 3))
                                }
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                            }
                        }
                        if (line.contains("PRICHASNIK")) {
                            line = line.replace("PRICHASNIK", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append(getString(by.carkva_gazeta.malitounik.R.string.gl_tyt, zmennyiaCastkiTitle)).append("<br><br>\n")
                            }
                            try {
                                if (dayOfWeek == 1) {
                                    builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(4))
                                } else {
                                    builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 4))
                                }
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br><br>\n")
                            }
                        }
                        when {
                            line.contains("APCH") -> {
                                line = line.replace("APCH", "")
                                if (chechZmena && !nochenia) {
                                    builder.append("<br>").append(getString(by.carkva_gazeta.malitounik.R.string.gl_tyt, zmennyiaCastkiTitle)).append("<br><br>\n")
                                }
                                var sv = zmenyiaChastki.sviatyia()
                                if (sv != "") {
                                    val s1 = sv.split(":")
                                    val s2 = s1[1].split(";")
                                    sv = if (s1[0].contains("На ютрані")) s2[1]
                                    else s1[0] + ":" + s2[0]
                                    aliert8 = sv.trim()
                                    builder.append(color).append("<br>").append(sv).append("</font>").append("<br><br>\n")
                                } else builder.append(line)
                                try {
                                    builder.append(zmenyiaChastki.zmenya(1))
                                } catch (t: Throwable) {
                                    builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br><br>\n")
                                }
                            }

                            line.contains("EVCH") -> {
                                line = line.replace("EVCH", "")
                                if (chechZmena && !nochenia) {
                                    builder.append("<br>").append(getString(by.carkva_gazeta.malitounik.R.string.gl_tyt, zmennyiaCastkiTitle)).append("<br><br>\n")
                                }
                                var sv = zmenyiaChastki.sviatyia()
                                if (sv != "") {
                                    val s1 = sv.split(":")
                                    val s2 = s1[1].split(";")
                                    sv = if (s1[0].contains("На ютрані")) s2[2]
                                    else s1[0] + ":" + s2[1]
                                    aliert9 = sv.trim()
                                    builder.append(color).append("<br>").append(sv).append("</font>").append("<br><br>\n")
                                } else builder.append(line)
                                try {
                                    builder.append(zmenyiaChastki.zmenya(0))
                                } catch (t: Throwable) {
                                    builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                                }
                            }

                            else -> {
                                builder.append(line)
                            }
                        }

                    }

                    else -> {
                        builder.append(line)
                    }
                }
            }
            return@withContext builder.toString()
        }
        val text = MainActivity.fromHtml(res).toSpannable()
        if (liturgia) {
            val ch1 = runZmennyiaChastki(text, 0)
            val ch2 = runZmennyiaChastki(text, ch1)
            val ch3 = runZmennyiaChastki(text, ch2)
            val ch4 = runZmennyiaChastki(text, ch3)
            val ch5 = runZmennyiaChastki(text, ch4)
            runZmennyiaChastki(text, ch5)
        }
        var string = aliert8
        var strLig = string.length
        var t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialogLiturgia = DialogLiturgia.getInstance(8, c[Calendar.DATE], c[Calendar.MONTH], c[Calendar.YEAR])
                    dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        string = aliert9
        strLig = string.length
        t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialogLiturgia = DialogLiturgia.getInstance(9, c[Calendar.DATE], c[Calendar.MONTH], c[Calendar.YEAR])
                    dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        string = "Пасьля чытаецца ікас 1 і кандак 1."
        strLig = string.length
        t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, 0)
                    anim.setDuration(1500).start()
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (resurs == "abiednica") {
            val stringBSA = "Заканчэньне ў час Вялікага посту гл. ніжэй"
            val strLigBSA = stringBSA.length
            val bsat1 = text.indexOf(stringBSA)
            if (bsat1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("Заканчэньне абедніцы", bsat1 + strLigBSA, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, bsat1, bsat1 + strLigBSA, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "viaczernia_niadzelnaja") {
            string = "ліцьця і блаславеньне хлябоў"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(this@Bogashlugbovya, Bogashlugbovya::class.java)
                        intent.putExtra("autoscrollOFF", autoscroll)
                        intent.putExtra("title", "Ліцьця і блаславеньне хлябоў")
                        intent.putExtra("resurs", "viaczernia_liccia_i_blaslavenne_chliabou")
                        startActivity(intent)
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "lit_raniej_asviaczanych_darou") {
            val stringVB = "зусім прапускаюцца"
            val strLigVB = stringVB.length
            val vbt1 = text.indexOf(stringVB)
            if (vbt1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("Калі ёсьць 10 песьняў", vbt1 + strLigVB, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, vbt1, vbt1 + strLigVB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "viaczerniaja_sluzba_sztodzionnaja_biez_sviatara") {
            var stringVB = "выбраныя вершы з псалмаў 1-3"
            var strLigVB = stringVB.length
            var vbt1 = text.indexOf(stringVB)
            if (vbt1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(11)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, vbt1, vbt1 + strLigVB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringVB = "прапускаюцца"
            strLigVB = stringVB.length
            vbt1 = text.indexOf(stringVB)
            if (vbt1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("Вызваль з вязьніцы душу маю", vbt1 + strLigVB, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, vbt1, vbt1 + strLigVB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringVB = "гл. тут."
            strLigVB = stringVB.length
            vbt1 = text.indexOf(stringVB)
            if (vbt1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(13)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, vbt1, vbt1 + strLigVB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringVB = "«Госпадзе, Цябе клічу»"
            strLigVB = stringVB.length
            vbt1 = text.indexOf(stringVB)
            if (vbt1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("Псалом 140", vbt1 + strLigVB, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, vbt1, vbt1 + strLigVB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val stringVB2 = "гл. ніжэй."
            val strLigVB2 = stringVB2.length
            val vbt2 = text.indexOf(stringVB2)
            if (vbt2 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("ЗАКАНЧЭНЬНЕ ВЯЧЭРНІ Ў ВЯЛІКІ ПОСТ", vbt2 + strLigVB2, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, vbt2, vbt2 + strLigVB2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (liturgia) {
            var stringBS = "Пс 102 (гл. тут)."
            var strLigBS = stringBS.length
            var bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(1)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Пс 91. (Гл. тут)."
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(2)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "(Пс 145). (Гл. тут)."
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(3)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Пс 92. (Гл. тут)."
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(4)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Пс 94. (Гл. тут)."
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(10)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Іншы антыфон сьвяточны і нядзельны (Мц 5:3-12):"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(5)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Малітва за памерлых"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(6)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Малітва за пакліканых"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(7)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "Успамін памерлых і жывых"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(14)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val stringBS2 = "Адзінародны Сыне (гл. тут)"
            val strLigBS2 = stringBS2.length
            val bst2 = text.indexOf(stringBS2)
            if (bst2 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("Адзінародны Сыне", bst2 + strLigBS2, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, bst2, bst2 + strLigBS2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "lit_jana_zalatavusnaha") {
            val stringBS2 = "«Адзінародны Сыне» (↓ гл. тут)"
            val strLigBS2 = stringBS2.length
            val bst2 = text.indexOf(stringBS2)
            if (bst2 != -1) {
                //text.setSpan(ImageSpan(this@Bogashlugbovya, by.carkva_gazeta.malitounik.R.drawable.maria), bst2 + 21, bst2 + 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("Адзінародны Сыне", bst2 + strLigBS2, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, bst2, bst2 + strLigBS2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            var stringBS = "«Блаславі, душа мая, Госпада...»"
            var strLigBS = stringBS.length
            var bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(1)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "«Добра ёсьць славіць Госпада...»"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(2)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "«Хвалі, душа мая, Госпада...»"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(3)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "«Госпад пануе, Ён апрануўся ў красу...»"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(4)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            stringBS = "«У валадарстве Тваім успомні нас, Госпадзе...»"
            strLigBS = stringBS.length
            bst1 = text.indexOf(stringBS)
            if (bst1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(5)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, bst1, bst1 + strLigBS, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "kanon_andreja_kryckaha_4_czastki") {
            var string9 = "Аўторак ↓"
            var strLig9 = string9.length
            var t9 = text.indexOf(string9)
            if (t9 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("АЎТОРАК", t9 + strLig9, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, t9, t9 + strLig9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string9 = "Серада ↓"
            strLig9 = string9.length
            t9 = text.indexOf(string9)
            if (t9 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("СЕРАДА", t9 + strLig9, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, t9, t9 + strLig9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string9 = "Чацьвер ↓"
            strLig9 = string9.length
            t9 = text.indexOf(string9)
            if (t9 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        binding.textView.layout?.let { layout ->
                            val strPosition = text.indexOf("ЧАЦЬВЕР", t9 + strLig9, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, t9, t9 + strLig9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        string = "Малітвы пасьля сьвятога прычасьця"
        strLig = string.length
        t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(this@Bogashlugbovya, MalitvyPasliaPrychascia::class.java)
                    startActivity(intent)
                    positionY = 0
                }
            }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val string11 = "[1]"
        val strLig11 = string11.length
        val t11 = text.indexOf(string11)
        if (t11 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    binding.textView.layout?.let { layout ->
                        val strPosition = text.indexOf("ПЕРШАЯ ГАДЗІНА", t11 + strLig11, true)
                        val line = layout.getLineForOffset(strPosition)
                        val y = layout.getLineTop(line)
                        val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                        anim.setDuration(1500).start()
                    }
                }
            }, t11, t11 + strLig11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val string3 = "[3]"
        val strLig3 = string3.length
        val t3 = text.indexOf(string3)
        if (t3 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    binding.textView.layout?.let { layout ->
                        val strPosition = text.indexOf("ТРЭЦЯЯ ГАДЗІНА", t3 + strLig3, true)
                        val line = layout.getLineForOffset(strPosition)
                        val y = layout.getLineTop(line)
                        val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                        anim.setDuration(1500).start()
                    }
                }
            }, t3, t3 + strLig3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val string6 = "[6]"
        val strLig6 = string6.length
        val t6 = text.indexOf(string6)
        if (t6 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    binding.textView.layout?.let { layout ->
                        val strPosition = text.indexOf("ШОСТАЯ ГАДЗІНА", t6 + strLig6, true)
                        val line = layout.getLineForOffset(strPosition)
                        val y = layout.getLineTop(line)
                        val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                        anim.setDuration(1500).start()
                    }
                }
            }, t6, t6 + strLig6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val string9 = "[9]"
        val strLig9 = string9.length
        val t9 = text.indexOf(string9)
        if (t9 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    binding.textView.layout?.let { layout ->
                        val strPosition = text.indexOf("ДЗЕВЯТАЯ ГАДЗІНА", t9 + strLig9, true)
                        val line = layout.getLineForOffset(strPosition)
                        val y = layout.getLineTop(line)
                        val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                        anim.setDuration(1500).start()
                    }
                }
            }, t9, t9 + strLig9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.textView.text = text
        positionY = k.getInt(resurs + "Scroll", 0)
        binding.scrollView2.post {
            if (savedInstanceState != null) {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    binding.textView.layout?.let { layout ->
                        val index = binding.textView.text.indexOf(textline)
                        val line = layout.getLineForOffset(index)
                        val y = layout.getLineTop(line)
                        binding.scrollView2.smoothScrollBy(0, y)
                    }
                } else {
                    binding.scrollView2.smoothScrollBy(0, positionY)
                }
                if (!autoscroll && savedInstanceState.getBoolean("seach")) {
                    findAllAsanc()
                }
                mAutoScroll = if (binding.textView.bottom <= binding.scrollView2.height) {
                    stopAutoStartScroll()
                    false
                } else {
                    true
                }
                invalidateOptionsMenu()
            } else {
                if (resurs.contains("viachernia_ton")) {
                    binding.textView.layout?.let { layout ->
                        val dzenNedeliname = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.dni_nedeli)
                        val textline = dzenNedeliname[c[Calendar.DAY_OF_WEEK]]
                        val index = binding.textView.text.indexOf(textline, ignoreCase = true)
                        val line = layout.getLineForOffset(index)
                        val y = layout.getLineTop(line)
                        binding.scrollView2.smoothScrollBy(0, y)
                    }
                    if (binding.textView.bottom <= binding.scrollView2.height) {
                        stopAutoStartScroll()
                        mAutoScroll = false
                        invalidateOptionsMenu()
                    } else if (k.getBoolean("autoscrollAutostart", false) && mAutoScroll) {
                        autoStartScroll()
                    }
                } else {
                    mAutoScroll = if (binding.textView.bottom <= binding.scrollView2.height) {
                        stopAutoStartScroll()
                        false
                    } else {
                        true
                    }
                    invalidateOptionsMenu()
                    binding.scrollView2.scrollBy(0, positionY)
                    if (((k.getBoolean("autoscrollAutostart", false) && mAutoScroll) || autoscroll) && !diffScroll) {
                        autoStartScroll()
                    }
                }
            }
            val searchString = intent.extras?.getString("search", "") ?: ""
            if (searchString != "") {
                stopAutoStartScroll()
                stopAutoScroll()
                invalidateOptionsMenu()
                binding.textSearch.setText(searchString)
                binding.find.visibility = View.VISIBLE
                binding.textSearch.requestFocus()
                EditTextCustom.focusAndShowKeyboard(binding.textSearch)
                findAllAsanc()
                intent.extras?.remove("search")
            }

        }
        if (dzenNoch) binding.imageView6.setImageResource(by.carkva_gazeta.malitounik.R.drawable.find_up_black)
        binding.imageView6.setOnClickListener { findNext(previous = true) }
        binding.textSearch.addTextChangedListener(object : TextWatcher {
            var editPosition = 0
            var check = 0
            var editch = true

            override fun afterTextChanged(s: Editable?) {
                var edit = s.toString()
                val editarig = edit.length
                edit = MainActivity.zamena(edit)
                editPosition += edit.length - editarig
                if (editch) {
                    if (check != 0) {
                        binding.textSearch.removeTextChangedListener(this)
                        binding.textSearch.setText(edit)
                        binding.textSearch.setSelection(editPosition)
                        binding.textSearch.addTextChangedListener(this)
                    }
                }
                if (edit.length >= 3) {
                    findAllAsanc()
                } else {
                    findRemoveSpan()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                editch = count != after
                check = after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editPosition = start + count
            }
        })
        if (dzenNoch) binding.imageView5.setImageResource(by.carkva_gazeta.malitounik.R.drawable.find_niz_back)
        binding.imageView5.setOnClickListener { findNext() }
    }

    private fun runZmennyiaChastki(text: Spannable, index: Int): Int {
        val stringGTA1 = "Глядзіце тут"
        val strLigGTA1 = stringGTA1.length
        val bsatGTA1 = text.indexOf(stringGTA1, index)
        if (bsatGTA1 != -1) {
            val t1 = text.lastIndexOf("\n", bsatGTA1)
            if (t1 != -1) {
                if (dzenNoch) text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), t1, bsatGTA1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                else text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary)), t1, bsatGTA1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                text.setSpan(StyleSpan(Typeface.BOLD), t1, bsatGTA1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val slugbovyiaTextu = SlugbovyiaTextu()
                    val intent = Intent(this@Bogashlugbovya, Bogashlugbovya::class.java)
                    val resours = slugbovyiaTextu.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                    intent.putExtra("autoscrollOFF", autoscroll)
                    intent.putExtra("resurs", resours)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", slugbovyiaTextu.getTitle(resours))
                    startActivity(intent)
                }
            }, bsatGTA1, bsatGTA1 + strLigGTA1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return bsatGTA1 + strLigGTA1
        }
        return 0
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            if (spid < 166) {
                val autoTime = (230 - spid) / 10
                var count = 0
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        spid = 230
                        autoScroll()
                        while (true) {
                            delay(1000L)
                            if (!mActionDown && !MainActivity.dialogVisable) {
                                spid -= autoTime
                                count++
                            }
                            if (count == 10) {
                                break
                            }
                        }
                        startAutoScroll()
                    }
                }
            } else {
                startAutoScroll()
            }
        }
    }

    private fun stopAutoStartScroll() {
        autoStartScrollJob?.cancel()
    }

    private fun startProcent() {
        procentJob?.cancel()
        bindingprogress.progress.visibility = View.VISIBLE
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            bindingprogress.progress.visibility = View.GONE
            delay(3000)
            if (bindingprogress.seekBarBrighess.visibility == View.VISIBLE) {
                bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this@Bogashlugbovya, by.carkva_gazeta.malitounik.R.anim.slide_out_left)
                bindingprogress.seekBarBrighess.visibility = View.GONE
            }
            if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@Bogashlugbovya, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
                bindingprogress.seekBarFontSize.visibility = View.GONE
            }
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditors = k.edit()
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.apply()
            }
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            if (fullscreenPage && binding.actionBack.visibility == View.GONE) {
                val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                binding.actionBack.visibility = View.VISIBLE
                binding.actionBack.animation = animation2
            }
            binding.textView.setTextIsSelectable(true)
            binding.textView.movementMethod = setLinkMovementMethodCheck()
            autoScrollJob?.cancel()
            stopAutoStartScroll()
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (!diffScroll) {
            spid = k.getInt("autoscrollSpid", 60)
            if (binding.actionMinus.visibility == View.GONE) {
                binding.actionMinus.visibility = View.VISIBLE
                binding.actionPlus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                binding.actionMinus.animation = animation
                binding.actionPlus.animation = animation
                val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
                binding.actionBack.visibility = View.GONE
                binding.actionBack.animation = animation2
            }
            mActionDown = false
            resetScreenJob?.cancel()
            stopAutoStartScroll()
            autoScroll()
        } else {
            val duration: Long = 1000
            ObjectAnimator.ofInt(binding.scrollView2, "scrollY", 0).setDuration(duration).start()
            binding.scrollView2.postDelayed({
                autoStartScroll()
                invalidateOptionsMenu()
            }, duration)
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            if (binding.find.visibility == View.VISIBLE) {
                findRemoveSpan()
                binding.textSearch.setText("")
                binding.find.visibility = View.GONE
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
            }
            binding.textView.clearFocus()
            binding.textView.setTextIsSelectable(false)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.textView.movementMethod = setLinkMovementMethodCheck()
            autoscroll = true
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            invalidateOptionsMenu()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        binding.scrollView2.smoothScrollBy(0, 2)
                    }
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val otstup2 = if (autoscroll) (50 * resources.displayMetrics.density).toInt()
        else 0
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.constraint) {
            if (MainActivity.checkBrightness) {
                MainActivity.brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
            }
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    val proc: Int
                    if (x < otstup) {
                        bindingprogress.seekBarBrighess.progress = MainActivity.brightness
                        bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        if (bindingprogress.seekBarBrighess.visibility == View.GONE) {
                            bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_right)
                            bindingprogress.seekBarBrighess.visibility = View.VISIBLE
                        }
                        startProcent()
                    }
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
                        bindingprogress.progress.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
                            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
                            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
                        }
                        startProcent()
                    }
                    if (y > heightConstraintLayout - otstup) {
                        if (mAutoScroll && binding.find.visibility == View.GONE) {
                            spid = k.getInt("autoscrollSpid", 60)
                            proc = 100 - (spid - 15) * 100 / 215
                            bindingprogress.progress.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                            startProcent()
                            startAutoScroll()
                            invalidateOptionsMenu()
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareMenu(menu: Menu) {
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        val itemVybranoe = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        if (mAutoScroll) {
            autoscroll = k.getBoolean("autoscroll", false)
            when {
                autoscroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
                diffScroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_up)
                else -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
            }
        } else {
            itemAuto.isVisible = false
            stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        }
        var spanString = SpannableString(itemAuto.title.toString())
        var end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString
        if (!(resurs == "1" || resurs == "2" || resurs == "3")) {
            itemVybranoe.isVisible = true
            if (men) {
                itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
                itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
            } else {
                itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
                itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
            }
        } else {
            itemVybranoe.isVisible = false
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = dzenNoch
        val spanString2 = if (k.getBoolean("auto_dzen_noch", false)) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isCheckable = false
            SpannableString(getString(by.carkva_gazeta.malitounik.R.string.auto_widget_day_d_n))
        } else {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isCheckable = true
            SpannableString(getString(by.carkva_gazeta.malitounik.R.string.widget_day_d_n))
        }
        val end2 = spanString2.length
        spanString2.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).title = spanString2
        spanString = SpannableString(itemVybranoe.title.toString())
        end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemVybranoe.title = spanString
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_zmena).isVisible = chechZmena
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_mun).isVisible = k.getBoolean("admin", false)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = false
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.bogashlugbovya, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val prefEditor = k.edit()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_mun) {
            val c = Calendar.getInstance()
            val i = Intent(this, CaliandarMun::class.java)
            i.putExtra("mun", c[Calendar.MONTH])
            i.putExtra("day", c[Calendar.DATE])
            i.putExtra("year", c[Calendar.YEAR])
            i.putExtra("getData", true)
            caliandarMunLauncher.launch(i)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_zmena) {
            val slugba = SlugbovyiaTextu()
            val intent = Intent(this, Bogashlugbovya::class.java)
            when {
                sviaty -> {
                    intent.putExtra("day", daysv)
                    intent.putExtra("mun", munsv)
                    intent.putExtra("year", Calendar.getInstance()[Calendar.YEAR])
                }

                checkDayOfYear -> {
                    val resours = if (checkLiturgia == 0) slugba.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                    else slugba.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.VIACZERNIA)
                    intent.putExtra("autoscrollOFF", autoscroll)
                    intent.putExtra("resurs", resours)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", slugba.getTitle(resours))
                }

                else -> {
                    val resours = slugba.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                    intent.putExtra("autoscrollOFF", autoscroll)
                    intent.putExtra("resurs", resours)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", slugba.getTitle(resours))
                }
            }
            startActivity(intent)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            if (item.isCheckable) {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    prefEditor.putBoolean("dzen_noch", true)
                } else {
                    prefEditor.putBoolean("dzen_noch", false)
                }
                prefEditor.apply()
                recreate()
            } else {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_find) {
            stopAutoStartScroll()
            stopAutoScroll()
            invalidateOptionsMenu()
            binding.find.visibility = View.VISIBLE
            binding.textSearch.requestFocus()
            EditTextCustom.focusAndShowKeyboard(binding.textSearch)
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            men = setVybranoe(this, resurs, title)
            if (men) {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.addVybranoe))
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            if (!k.getBoolean("fullscreenPage", false)) {
                var fullscreenCount = k.getInt("fullscreenCount", 0)
                if (fullscreenCount > 3) {
                    val dialogFullscreen = DialogHelpFullScreenSettings()
                    dialogFullscreen.show(supportFragmentManager, "DialogHelpFullScreenSettings")
                    fullscreenCount = 0
                } else {
                    fullscreenCount++
                    hideHelp()
                }
                prefEditor.putInt("fullscreenCount", fullscreenCount)
                prefEditor.apply()
            } else {
                hideHelp()
            }
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val pesny = resursMap[resurs] ?: by.carkva_gazeta.malitounik.R.raw.bogashlugbovya_error
            if (pesny != by.carkva_gazeta.malitounik.R.raw.bogashlugbovya_error) {
                val inputStream = resources.openRawResource(pesny)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var text: String
                reader.use { bufferedReader ->
                    text = bufferedReader.readText()
                }
                val sent = MainActivity.fromHtml(text).toString()
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(by.carkva_gazeta.malitounik.R.string.copy_text), sent)
                clipboard.setPrimaryClip(clip)
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.copy_text), Toast.LENGTH_LONG)
                if (k.getBoolean("dialogHelpShare", true)) {
                    val dialog = DialogHelpShare.getInstance(sent)
                    dialog.show(supportFragmentManager, "DialogHelpShare")
                } else {
                    sentShareText(sent)
                }
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error_ch))
            }
            return true
        }
        prefEditor.apply()
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val idres = resursMap[resurs] ?: by.carkva_gazeta.malitounik.R.raw.bogashlugbovya_error
                val inputStream = resources.openRawResource(idres)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", resurs)
                intent.putExtra("title", title)
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
            }
            return true
        }
        return false
    }

    override fun dialogHelpFullScreenSettingsClose() {
        hideHelp()
    }

    override fun sentShareText(shareText: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, title))
    }

    override fun onBack() {
        when {
            binding.find.visibility == View.VISIBLE -> {
                binding.find.visibility = View.GONE
                binding.textSearch.setText("")
                findRemoveSpan()
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
            }

            intent.extras?.getBoolean("chekVybranoe", false) == true -> {
                setResult(200)
                super.onBack()
            }

            else -> {
                super.onBack()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val prefEditor = k.edit()
        prefEditor.putInt(resurs + "Scroll", positionY)
        prefEditor.apply()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        autoStartScrollJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (fullscreenPage) {
            binding.constraint.post {
                hideHelp()
            }
        }
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            if (resources.configuration.orientation == orientation) {
                startAutoScroll()
            } else autoStartScroll()
        }
        orientation = resources.configuration.orientation
    }

    override fun onDialogFullScreenHelpClose() {
        if (dzenNoch) binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
        else binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
        hide()
    }

    private fun hideHelp() {
        if (k.getBoolean("help_fullscreen", true)) {
            binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPost2))
            if (dzenNoch) binding.scrollView2.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
            else binding.scrollView2.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
            val dialogHelpListView = DialogHelpFullScreen()
            dialogHelpListView.show(supportFragmentManager, "DialogHelpListView")
            val prefEditors = k.edit()
            prefEditors.putBoolean("help_fullscreen", false)
            prefEditors.apply()
        } else {
            hide()
        }
    }

    private fun hide() {
        fullscreenPage = true
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        if (binding.actionMinus.visibility == View.GONE) {
            binding.actionBack.visibility = View.VISIBLE
            binding.actionBack.animation = animation
        }
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.show(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
        binding.actionFullscreen.visibility = View.GONE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.GONE
        binding.actionBack.animation = animation
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("orientation", orientation)
        outState.putBoolean("fullscreen", fullscreenPage)
        if (binding.find.visibility == View.VISIBLE) outState.putBoolean("seach", true)
        else outState.putBoolean("seach", false)
        outState.putString("textLine", firstTextPosition)
        outState.putInt("day_of_year", c[Calendar.DAY_OF_YEAR])
        outState.putInt("year", c[Calendar.YEAR])
        outState.putInt("vybranoePosition", vybranoePosition)
        outState.putBoolean("mAutoScrol", mAutoScroll)
    }

    private data class SpanStr(val color: Int, val start: Int)
}
