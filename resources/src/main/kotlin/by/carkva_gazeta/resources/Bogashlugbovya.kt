package by.carkva_gazeta.resources

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.toSpannable
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.databinding.BogasluzbovyaBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class Bogashlugbovya : AppCompatActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, InteractiveScrollView.OnScrollChangedCallback {

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun mHidePart2Runnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    private fun mShowPart2Runnable() {
        supportActionBar?.show()
    }

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private var dzenNoch = false
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var resurs = ""
    private var men = true
    private var positionY = 0
    private var title = ""
    private var editVybranoe = false
    private var editDzenNoch = false
    private var mActionDown = false
    private var mAutoScroll = true
    private lateinit var binding: BogasluzbovyaBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var diffScroll = -1
    private var aliert8 = ""
    private var aliert9 = ""
    private var findPosition = 0
    private var firstTextPosition = ""
    private val findListSpans = ArrayList<SpanStr>()
    private var animatopRun = false
    private var onRestore = false
    private var onFind = false
    private var chechZmena = false
    private var raznica = 400
    private var checkDayOfYear = false
    private var sviaty = false
    private var daysv = 1
    private var munsv = 0

    companion object {
        val resursMap = ArrayMap<String, Int>()

        init {
            resursMap["bogashlugbovya1"] = R.raw.bogashlugbovya1
            resursMap["bogashlugbovya2"] = R.raw.bogashlugbovya2
            resursMap["bogashlugbovya4"] = R.raw.bogashlugbovya4
            resursMap["bogashlugbovya6"] = R.raw.bogashlugbovya6
            resursMap["bogashlugbovya8"] = R.raw.bogashlugbovya8
            resursMap["bogashlugbovya9"] = R.raw.bogashlugbovya9
            resursMap["bogashlugbovya11"] = R.raw.bogashlugbovya11
            resursMap["bogashlugbovya12_1"] = R.raw.bogashlugbovya12_1
            resursMap["bogashlugbovya12_2"] = R.raw.bogashlugbovya12_2
            resursMap["bogashlugbovya12_3"] = R.raw.bogashlugbovya12_3
            resursMap["bogashlugbovya12_4"] = R.raw.bogashlugbovya12_4
            resursMap["bogashlugbovya12_5"] = R.raw.bogashlugbovya12_5
            resursMap["bogashlugbovya12_6"] = R.raw.bogashlugbovya12_6
            resursMap["bogashlugbovya12_7"] = R.raw.bogashlugbovya12_7
            resursMap["bogashlugbovya12_8"] = R.raw.bogashlugbovya12_8
            resursMap["bogashlugbovya12_9"] = R.raw.bogashlugbovya12_9
            resursMap["bogashlugbovya13_1"] = R.raw.bogashlugbovya13_1
            resursMap["bogashlugbovya13_2"] = R.raw.bogashlugbovya13_2
            resursMap["bogashlugbovya13_3"] = R.raw.bogashlugbovya13_3
            resursMap["bogashlugbovya13_4"] = R.raw.bogashlugbovya13_4
            resursMap["bogashlugbovya13_5"] = R.raw.bogashlugbovya13_5
            resursMap["bogashlugbovya13_6"] = R.raw.bogashlugbovya13_6
            resursMap["bogashlugbovya13_7"] = R.raw.bogashlugbovya13_7
            resursMap["bogashlugbovya13_8"] = R.raw.bogashlugbovya13_8
            resursMap["bogashlugbovya14_1"] = R.raw.bogashlugbovya14_1
            resursMap["bogashlugbovya14_2"] = R.raw.bogashlugbovya14_2
            resursMap["bogashlugbovya14_3"] = R.raw.bogashlugbovya14_3
            resursMap["bogashlugbovya14_4"] = R.raw.bogashlugbovya14_4
            resursMap["bogashlugbovya14_5"] = R.raw.bogashlugbovya14_5
            resursMap["bogashlugbovya14_6"] = R.raw.bogashlugbovya14_6
            resursMap["bogashlugbovya14_7"] = R.raw.bogashlugbovya14_7
            resursMap["bogashlugbovya14_8"] = R.raw.bogashlugbovya14_8
            resursMap["bogashlugbovya14_9"] = R.raw.bogashlugbovya14_9
            resursMap["bogashlugbovya15_1"] = R.raw.bogashlugbovya15_1
            resursMap["bogashlugbovya15_2"] = R.raw.bogashlugbovya15_2
            resursMap["bogashlugbovya15_3"] = R.raw.bogashlugbovya15_3
            resursMap["bogashlugbovya15_4"] = R.raw.bogashlugbovya15_4
            resursMap["bogashlugbovya15_5"] = R.raw.bogashlugbovya15_5
            resursMap["bogashlugbovya15_6"] = R.raw.bogashlugbovya15_6
            resursMap["bogashlugbovya15_7"] = R.raw.bogashlugbovya15_7
            resursMap["bogashlugbovya15_8"] = R.raw.bogashlugbovya15_8
            resursMap["bogashlugbovya15_9"] = R.raw.bogashlugbovya15_9
            resursMap["bogashlugbovya16_1"] = R.raw.bogashlugbovya16_1
            resursMap["bogashlugbovya16_2"] = R.raw.bogashlugbovya16_2
            resursMap["bogashlugbovya16_3"] = R.raw.bogashlugbovya16_3
            resursMap["bogashlugbovya16_4"] = R.raw.bogashlugbovya16_4
            resursMap["bogashlugbovya16_5"] = R.raw.bogashlugbovya16_5
            resursMap["bogashlugbovya16_6"] = R.raw.bogashlugbovya16_6
            resursMap["bogashlugbovya16_7"] = R.raw.bogashlugbovya16_7
            resursMap["bogashlugbovya16_8"] = R.raw.bogashlugbovya16_8
            resursMap["bogashlugbovya16_9"] = R.raw.bogashlugbovya16_9
            resursMap["bogashlugbovya16_10"] = R.raw.bogashlugbovya16_10
            resursMap["bogashlugbovya16_11"] = R.raw.bogashlugbovya16_11
            resursMap["bogashlugbovya17_1"] = R.raw.bogashlugbovya17_1
            resursMap["bogashlugbovya17_2"] = R.raw.bogashlugbovya17_2
            resursMap["bogashlugbovya17_3"] = R.raw.bogashlugbovya17_3
            resursMap["bogashlugbovya17_4"] = R.raw.bogashlugbovya17_4
            resursMap["bogashlugbovya17_5"] = R.raw.bogashlugbovya17_5
            resursMap["bogashlugbovya17_6"] = R.raw.bogashlugbovya17_6
            resursMap["bogashlugbovya17_7"] = R.raw.bogashlugbovya17_7
            resursMap["bogashlugbovya17_8"] = R.raw.bogashlugbovya17_8
            resursMap["zmenyia_chastki_tamash"] = R.raw.zmenyia_chastki_tamash
            resursMap["zmenyia_chastki_miranosicay"] = R.raw.zmenyia_chastki_miranosicay
            resursMap["zmenyia_chastki_samaranki"] = R.raw.zmenyia_chastki_samaranki
            resursMap["zmenyia_chastki_slepanarodz"] = R.raw.zmenyia_chastki_slepanarodz
            resursMap["zmenyia_chastki_pieramianiennie"] = R.raw.zmenyia_chastki_pieramianiennie
            resursMap["akafist0"] = R.raw.akafist0
            resursMap["akafist1"] = R.raw.akafist1
            resursMap["akafist2"] = R.raw.akafist2
            resursMap["akafist3"] = R.raw.akafist3
            resursMap["akafist4"] = R.raw.akafist4
            resursMap["akafist5"] = R.raw.akafist5
            resursMap["akafist6"] = R.raw.akafist6
            resursMap["akafist7"] = R.raw.akafist7
            resursMap["akafist8"] = R.raw.akafist8
            resursMap["malitvy1"] = R.raw.malitvy1
            resursMap["malitvy2"] = R.raw.malitvy2
            resursMap["paslia_prychascia1"] = R.raw.paslia_prychascia1
            resursMap["paslia_prychascia2"] = R.raw.paslia_prychascia2
            resursMap["paslia_prychascia3"] = R.raw.paslia_prychascia3
            resursMap["paslia_prychascia4"] = R.raw.paslia_prychascia4
            resursMap["paslia_prychascia5"] = R.raw.paslia_prychascia5
            resursMap["prynagodnyia_0"] = R.raw.prynagodnyia_0
            resursMap["prynagodnyia_1"] = R.raw.prynagodnyia_1
            resursMap["prynagodnyia_2"] = R.raw.prynagodnyia_2
            resursMap["prynagodnyia_3"] = R.raw.prynagodnyia_3
            resursMap["prynagodnyia_4"] = R.raw.prynagodnyia_4
            resursMap["prynagodnyia_5"] = R.raw.prynagodnyia_5
            resursMap["prynagodnyia_6"] = R.raw.prynagodnyia_6
            resursMap["prynagodnyia_7"] = R.raw.prynagodnyia_7
            resursMap["prynagodnyia_8"] = R.raw.prynagodnyia_8
            resursMap["prynagodnyia_9"] = R.raw.prynagodnyia_9
            resursMap["prynagodnyia_10"] = R.raw.prynagodnyia_10
            resursMap["prynagodnyia_11"] = R.raw.prynagodnyia_11
            resursMap["prynagodnyia_12"] = R.raw.prynagodnyia_12
            resursMap["prynagodnyia_13"] = R.raw.prynagodnyia_13
            resursMap["prynagodnyia_14"] = R.raw.prynagodnyia_14
            resursMap["prynagodnyia_15"] = R.raw.prynagodnyia_15
            resursMap["prynagodnyia_16"] = R.raw.prynagodnyia_16
            resursMap["prynagodnyia_17"] = R.raw.prynagodnyia_17
            resursMap["prynagodnyia_18"] = R.raw.prynagodnyia_18
            resursMap["prynagodnyia_19"] = R.raw.prynagodnyia_19
            resursMap["prynagodnyia_20"] = R.raw.prynagodnyia_20
            resursMap["prynagodnyia_21"] = R.raw.prynagodnyia_21
            resursMap["prynagodnyia_22"] = R.raw.prynagodnyia_22
            resursMap["prynagodnyia_23"] = R.raw.prynagodnyia_23
            resursMap["prynagodnyia_24"] = R.raw.prynagodnyia_24
            resursMap["prynagodnyia_25"] = R.raw.prynagodnyia_25
            resursMap["prynagodnyia_26"] = R.raw.prynagodnyia_26
            resursMap["prynagodnyia_27"] = R.raw.prynagodnyia_27
            resursMap["prynagodnyia_28"] = R.raw.prynagodnyia_28
            resursMap["prynagodnyia_29"] = R.raw.prynagodnyia_29
            resursMap["prynagodnyia_30"] = R.raw.prynagodnyia_30
            resursMap["prynagodnyia_31"] = R.raw.prynagodnyia_31
            resursMap["prynagodnyia_32"] = R.raw.prynagodnyia_32
            resursMap["prynagodnyia_33"] = R.raw.prynagodnyia_33
            resursMap["prynagodnyia_34"] = R.raw.prynagodnyia_34
            resursMap["prynagodnyia_35"] = R.raw.prynagodnyia_35
            resursMap["prynagodnyia_36"] = R.raw.prynagodnyia_36
            resursMap["prynagodnyia_37"] = R.raw.prynagodnyia_37
            resursMap["prynagodnyia_38"] = R.raw.prynagodnyia_38
            resursMap["prynagodnyia_39"] = R.raw.prynagodnyia_39
            resursMap["prynagodnyia_40"] = R.raw.prynagodnyia_40
            resursMap["ruzanec0"] = R.raw.ruzanec0
            resursMap["ruzanec1"] = R.raw.ruzanec1
            resursMap["ruzanec2"] = R.raw.ruzanec2
            resursMap["ruzanec3"] = R.raw.ruzanec3
            resursMap["ruzanec4"] = R.raw.ruzanec4
            resursMap["ruzanec5"] = R.raw.ruzanec5
            resursMap["ruzanec6"] = R.raw.ruzanec6
            resursMap["ton1"] = R.raw.ton1
            resursMap["ton1_budni"] = R.raw.ton1_budni
            resursMap["ton2"] = R.raw.ton2
            resursMap["ton2_budni"] = R.raw.ton2_budni
            resursMap["ton3"] = R.raw.ton3
            resursMap["ton3_budni"] = R.raw.ton3_budni
            resursMap["ton4"] = R.raw.ton4
            resursMap["ton4_budni"] = R.raw.ton4_budni
            resursMap["ton5"] = R.raw.ton5
            resursMap["ton5_budni"] = R.raw.ton5_budni
            resursMap["ton6"] = R.raw.ton6
            resursMap["ton6_budni"] = R.raw.ton6_budni
            resursMap["ton7"] = R.raw.ton7
            resursMap["ton8"] = R.raw.ton8
            resursMap["viachernia_niadzeli"] = R.raw.viachernia_niadzeli
            resursMap["viachernia_liccia_i_blaslavenne_xliabou"] = R.raw.viachernia_liccia_i_blaslavenne_xliabou
            resursMap["viachernia_na_kogny_dzen"] = R.raw.viachernia_na_kogny_dzen
            resursMap["viachernia_y_vialikim_poste"] = R.raw.viachernia_y_vialikim_poste
            resursMap["viachernia_ton1"] = R.raw.viachernia_ton1
            resursMap["viachernia_ton2"] = R.raw.viachernia_ton2
            resursMap["viachernia_ton3"] = R.raw.viachernia_ton3
            resursMap["viachernia_ton4"] = R.raw.viachernia_ton4
            resursMap["viachernia_ton5"] = R.raw.viachernia_ton5
            resursMap["viachernia_ton6"] = R.raw.viachernia_ton6
            resursMap["viachernia_ton7"] = R.raw.viachernia_ton7
            resursMap["viachernia_ton8"] = R.raw.viachernia_ton8
            resursMap["viachernia_bagarodzichnia_adpushchalnyia"] = R.raw.viachernia_bagarodzichnia_adpushchalnyia
            resursMap["viachernia_mineia_agulnaia1"] = R.raw.viachernia_mineia_agulnaia1
            resursMap["viachernia_mineia_agulnaia2"] = R.raw.viachernia_mineia_agulnaia2
            resursMap["viachernia_mineia_agulnaia3"] = R.raw.viachernia_mineia_agulnaia3
            resursMap["viachernia_mineia_agulnaia4"] = R.raw.viachernia_mineia_agulnaia4
            resursMap["viachernia_mineia_agulnaia5"] = R.raw.viachernia_mineia_agulnaia5
            resursMap["viachernia_mineia_agulnaia6"] = R.raw.viachernia_mineia_agulnaia6
            resursMap["viachernia_mineia_agulnaia7"] = R.raw.viachernia_mineia_agulnaia7
            resursMap["viachernia_mineia_agulnaia8"] = R.raw.viachernia_mineia_agulnaia8
            resursMap["viachernia_mineia_agulnaia9"] = R.raw.viachernia_mineia_agulnaia9
            resursMap["viachernia_mineia_agulnaia10"] = R.raw.viachernia_mineia_agulnaia10
            resursMap["viachernia_mineia_agulnaia11"] = R.raw.viachernia_mineia_agulnaia11
            resursMap["viachernia_mineia_agulnaia12"] = R.raw.viachernia_mineia_agulnaia12
            resursMap["viachernia_mineia_agulnaia13"] = R.raw.viachernia_mineia_agulnaia13
            resursMap["viachernia_mineia_agulnaia14"] = R.raw.viachernia_mineia_agulnaia14
            resursMap["viachernia_mineia_agulnaia15"] = R.raw.viachernia_mineia_agulnaia15
            resursMap["viachernia_mineia_agulnaia16"] = R.raw.viachernia_mineia_agulnaia16
            resursMap["viachernia_mineia_agulnaia17"] = R.raw.viachernia_mineia_agulnaia17
            resursMap["viachernia_mineia_agulnaia18"] = R.raw.viachernia_mineia_agulnaia18
            resursMap["viachernia_mineia_agulnaia19"] = R.raw.viachernia_mineia_agulnaia19
            resursMap["viachernia_mineia_agulnaia20"] = R.raw.viachernia_mineia_agulnaia20
            resursMap["viachernia_mineia_agulnaia21"] = R.raw.viachernia_mineia_agulnaia21
            resursMap["viachernia_mineia_agulnaia22"] = R.raw.viachernia_mineia_agulnaia22
            resursMap["viachernia_mineia_agulnaia23"] = R.raw.viachernia_mineia_agulnaia23
            resursMap["viachernia_mineia_agulnaia24"] = R.raw.viachernia_mineia_agulnaia24
            resursMap["viachernia_mineia_sviatochnaia1"] = R.raw.viachernia_mineia_sviatochnaia1
            resursMap["viachernia_mineia_sviatochnaia2"] = R.raw.viachernia_mineia_sviatochnaia2
            resursMap["viachernia_mineia_sviatochnaia3"] = R.raw.viachernia_mineia_sviatochnaia3
            resursMap["viachernia_mineia_sviatochnaia4"] = R.raw.viachernia_mineia_sviatochnaia4
            resursMap["viachernia_mineia_sviatochnaia5"] = R.raw.viachernia_mineia_sviatochnaia5
            resursMap["viachernia_mineia_sviatochnaia6"] = R.raw.viachernia_mineia_sviatochnaia6
            resursMap["viachernia_mineia_sviatochnaia7"] = R.raw.viachernia_mineia_sviatochnaia7
            PesnyAll.resursMap.forEach {
                resursMap[it.key] = it.value
            }
        }

        fun setVybranoe(context: Context, resurs: String, title: String): Boolean {
            var check = true
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                val gson = Gson()
                if (file.exists()) {
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    MenuVybranoe.vybranoe = gson.fromJson(file.readText(), type)
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
                file.writer().use {
                    it.write(gson.toJson(MenuVybranoe.vybranoe))
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
            try {
                if (file.exists()) {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<VybranoeData>>() {}.type
                    MenuVybranoe.vybranoe = gson.fromJson(file.readText(), type)
                } else {
                    return false
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
            findAll()
            findCheckPosition()
            if (noNext) findNext(false)
        }
    }

    private fun findAll() {
        var position = 0
        val search = binding.textSearch.text.toString()
        if (search.length >= 3) {
            val text = binding.textView.text as SpannableString
            val searchLig = search.length
            var run = true
            while (run) {
                val strPosition = text.indexOf(search, position, true)
                if (strPosition != -1) {
                    findListSpans.add(SpanStr(getColorSpans(text.getSpans(strPosition, strPosition + searchLig, ForegroundColorSpan::class.java)), strPosition, strPosition + searchLig))
                    text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), strPosition, strPosition + searchLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    position = strPosition + 1
                } else {
                    run = false
                }
            }
        }
    }

    private fun findCheckPosition() {
        if (findListSpans.isNotEmpty()) {
            val lineForVertical = binding.textView.layout.getLineForVertical(positionY)
            for (i in 0 until findListSpans.size) {
                if (lineForVertical <= binding.textView.layout.getLineForOffset(findListSpans[i].start)) {
                    findPosition = i
                    break
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
            findListSpans.forEach {
                text.setSpan(ForegroundColorSpan(it.color), it.start, it.size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPositionOld].start, findListSpans[findPositionOld].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, findPosition + 1, findListSpans.size)
            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta2)), findListSpans[findPosition].start, findListSpans[findPosition].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val line = binding.textView.layout.getLineForOffset(findListSpans[findPosition].start)
            val y = binding.textView.layout.getLineTop(line)
            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    animatopRun = true
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animatopRun = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            anim.setDuration(1000).start()
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
        val laneLayout = binding.textView.layout
        laneLayout?.let { layout ->
            var lineForVertical = layout.getLineForVertical(positionY)
            var textForVertical = binding.textView.text.substring(layout.getLineStart(lineForVertical), layout.getLineEnd(lineForVertical)).trim()
            if (textForVertical == "" && lineForVertical != 0) {
                lineForVertical--
                textForVertical = binding.textView.text.substring(layout.getLineStart(lineForVertical), layout.getLineEnd(lineForVertical)).trim()
            }
            binding.textView.textSize = fontBiblia
            binding.textView.post {
                binding.textView.layout?.let {
                    val strPosition = binding.textView.text.indexOf(textForVertical, ignoreCase = true)
                    val line = it.getLineForOffset(strPosition)
                    val y = it.getLineTop(line)
                    binding.scrollView2.scrollTo(0, y)
                }
            }
        }
    }

    override fun onScroll(t: Int, oldt: Int) {
        positionY = t
        val laneLayout = binding.textView.layout
        laneLayout?.let { layout ->
            val textForVertical = binding.textView.text.substring(layout.getLineStart(layout.getLineForVertical(positionY)), layout.getLineEnd(layout.getLineForVertical(positionY))).trim()
            if (textForVertical != "") firstTextPosition = textForVertical
            if (binding.find.visibility == View.VISIBLE && !animatopRun) {
                if (findListSpans.isNotEmpty()) {
                    val text = binding.textView.text as SpannableString
                    for (i in 0 until findListSpans.size) {
                        if (layout.getLineForOffset(findListSpans[i].start) == layout.getLineForVertical(positionY)) {
                            var ii = i + 1
                            if (i == 0) ii = 1
                            findPosition = i
                            var findPositionOld = if (t >= oldt) i - 1
                            else i + 1
                            if (findPositionOld == -1) findPositionOld = findListSpans.size - 1
                            if (findPositionOld == findListSpans.size) findPositionOld = 0
                            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), findListSpans[findPositionOld].start, findListSpans[findPositionOld].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            if (findPosition != ii) binding.textCount.text = getString(by.carkva_gazeta.malitounik.R.string.fing_count, ii, findListSpans.size)
                            text.setSpan(BackgroundColorSpan(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorBezPosta2)), findListSpans[i].start, findListSpans[i].size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            break
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        dzenNoch = k.getBoolean("dzen_noch", false)
        super.onCreate(savedInstanceState)
        if (dzenNoch) setTheme(by.carkva_gazeta.malitounik.R.style.AppCompatDark)
        binding = BogasluzbovyaBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        resurs = intent?.extras?.getString("resurs") ?: ""
        title = intent?.extras?.getString("title") ?: ""
        loadData(savedInstanceState)
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        binding.scrollView2.setOnScrollChangedCallback(this)
        binding.constraint.setOnTouchListener(this)
        if (savedInstanceState != null) {
            onRestore = true
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            editVybranoe = savedInstanceState.getBoolean("editVybranoe")
            editDzenNoch = savedInstanceState.getBoolean("editDzenNoch")
            MainActivity.dialogVisable = false
            if (savedInstanceState.getBoolean("seach")) {
                binding.find.visibility = View.VISIBLE
            }
        }
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.textView.textSize = fontBiblia
        DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
        }
        men = checkVybranoe(this, resurs)
        bindingprogress.fontSizePlus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MAX) bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.max_font)
            if (fontBiblia < SettingsActivity.GET_FONT_SIZE_MAX) {
                fontBiblia += 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent(3000)
        }
        bindingprogress.fontSizeMinus.setOnClickListener {
            if (fontBiblia == SettingsActivity.GET_FONT_SIZE_MIN) bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.min_font)
            if (fontBiblia > SettingsActivity.GET_FONT_SIZE_MIN) {
                fontBiblia -= 4
                bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                bindingprogress.progress.visibility = View.VISIBLE
                val prefEditor: Editor = k.edit()
                prefEditor.putFloat("font_biblia", fontBiblia)
                prefEditor.apply()
                onDialogFontSize(fontBiblia)
            }
            startProcent(3000)
        }
        bindingprogress.brighessPlus.setOnClickListener {
            if (MainActivity.brightness < 100) {
                MainActivity.brightness = MainActivity.brightness + 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent(3000)
        }
        bindingprogress.brighessMinus.setOnClickListener {
            if (MainActivity.brightness > 0) {
                MainActivity.brightness = MainActivity.brightness - 1
                val lp = window.attributes
                lp.screenBrightness = MainActivity.brightness.toFloat() / 100
                window.attributes = lp
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                bindingprogress.progress.visibility = View.VISIBLE
                MainActivity.checkBrightness = false
            }
            startProcent(3000)
        }
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
                bindingprogress.progress.visibility = View.VISIBLE
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
                bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
                bindingprogress.progress.visibility = View.VISIBLE
                startProcent()
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.scrollView2.setOnBottomReachedListener(object : InteractiveScrollView.OnBottomReachedListener {
            override fun onBottomReached() {
                autoscroll = false
                stopAutoScroll()
                invalidateOptionsMenu()
            }

            override fun onScrollDiff(diff: Int) {
                diffScroll = diff
            }

            override fun onTouch(action: Boolean) {
                stopAutoStartScroll()
                mActionDown = action
            }
        })
        binding.textView.movementMethod = LinkMovementMethod.getInstance()
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
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = title
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
        val zmenyiaChastki = ZmenyiaChastki()
        val res = withContext(Dispatchers.IO) {
            val builder = StringBuilder()
            val id = resursMap[resurs] ?: R.raw.bogashlugbovya1
            val inputStream = resources.openRawResource(id)
            raznica = zmenyiaChastki.raznica()
            val gregorian = Calendar.getInstance() as GregorianCalendar
            val dayOfWeek = gregorian.get(Calendar.DAY_OF_WEEK)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val color = if (dzenNoch) "<font color=\"#f44336\">"
            else "<font color=\"#d00505\">"
            reader.forEachLine {
                var line = it
                if (line.contains("Апостал:")) Log.d("Oleg", "Ok")
                if (dzenNoch) line = line.replace("#d00505", "#f44336")
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
                    resurs.contains("bogashlugbovya") -> {
                        if (line.contains("KANDAK")) {
                            line = line.replace("KANDAK", "")
                            builder.append(line)
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
                            try {
                                if (dayOfWeek == 1) {
                                    builder.append(zmenyiaChastki.traparyIKandakiNiadzelnyia(4))
                                } else {
                                    builder.append(zmenyiaChastki.traparyIKandakiNaKognyDzen(dayOfWeek, 4))
                                }
                            } catch (t: Throwable) {
                                builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                            }
                        }
                        when {
                            line.contains("APCH") -> {
                                line = line.replace("APCH", "")
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
                                    builder.append(resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch)).append("<br>\n")
                                }
                            }
                            line.contains("EVCH") -> {
                                line = line.replace("EVCH", "")
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
            inputStream.close()
            return@withContext builder.toString()
        }
        val text = MainActivity.fromHtml(res).toSpannable()
        var string = aliert8
        var strLig = string.length
        var t1 = text.indexOf(string)
        if (t1 != -1) {
            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val dialogLiturgia = DialogLiturgia.getInstance(8)
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
                    val dialogLiturgia = DialogLiturgia.getInstance(9)
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
        if (resurs == "bogashlugbovya8") {
            string = "Заканчэньне ў час Вялікага посту гл. ніжэй"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val strPosition = text.indexOf("Заканчэньне абедніцы", t1 + strLig, true)
                        val line = binding.textView.layout.getLineForOffset(strPosition)
                        val y = binding.textView.layout.getLineTop(line)
                        val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                        anim.setDuration(1500).start()
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "bogashlugbovya1" || resurs == "bogashlugbovya2") {
            string = "Пс 102 (гл. тут)."
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(1)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Пс 91. (Гл. тут)."
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(2)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "(Пс 145). (Гл. тут)."
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(3)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Пс 92. (Гл. тут)."
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(4)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Пс 94. (Гл. тут)."
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(10)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Іншы антыфон сьвяточны і нядзельны (Мц 5:3-12):"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(5)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Малітва за памерлых"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(6)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            string = "Малітва за пакліканых"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(7)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        }
        if (resurs.contains("bogashlugbovya") || resurs.contains("akafist") || resurs.contains("malitvy") || resurs.contains("ruzanec") || resurs.contains("viachernia")) {
            if (savedInstanceState == null) {
                if (k.getBoolean("autoscrollAutostart", false) && mAutoScroll) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    autoStartScroll()
                }
            }
        } else {
            mAutoScroll = false
        }
        binding.textView.text = text
        if (savedInstanceState != null) {
            binding.textView.post {
                val textline = savedInstanceState.getString("textLine", "")
                if (textline != "") {
                    val index = binding.textView.text.indexOf(textline)
                    val line = binding.textView.layout.getLineForOffset(index)
                    val y = binding.textView.layout.getLineTop(line)
                    binding.scrollView2.scrollY = y
                } else {
                    binding.scrollView2.smoothScrollBy(0, positionY)
                }
                if (!autoscroll && savedInstanceState.getBoolean("seach")) {
                    findAllAsanc()
                }
                if (autoscroll) {
                    startAutoScroll()
                }
            }
        } else {
            if (resurs.contains("viachernia_ton")) {
                binding.textView.post {
                    val cal = Calendar.getInstance()
                    val dzenNedeliname = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.dni_nedeli)
                    val textline = dzenNedeliname[cal[Calendar.DAY_OF_WEEK]]
                    val index = binding.textView.text.indexOf(textline, ignoreCase = true)
                    val line = binding.textView.layout.getLineForOffset(index)
                    val y = binding.textView.layout.getLineTop(line)
                    binding.scrollView2.scrollY = y
                }
            } else {
                binding.scrollView2.post {
                    binding.scrollView2.smoothScrollBy(0, positionY)
                    if (autoscroll) {
                        startAutoScroll()
                    }
                }
            }
        }
        positionY = k.getInt(resurs + "Scroll", 0)
        if (dzenNoch) binding.imageView6.setImageResource(by.carkva_gazeta.malitounik.R.drawable.find_up_black)
        binding.imageView6.setOnClickListener { findNext(previous = true) }
        binding.textSearch.addTextChangedListener(object : TextWatcher {
            var editPosition = 0
            var check = 0
            var editch = true

            override fun afterTextChanged(s: Editable?) {
                var edit = s.toString()
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                edit = edit.replace("И", "І")
                edit = edit.replace("Щ", "Ў")
                edit = edit.replace("Ъ", "'")
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
        val slugbovyiaTextu = SlugbovyiaTextu()
        checkDayOfYear = slugbovyiaTextu.checkLiturgia(zmenyiaChastki.dayOfYear())
        if ((resurs == "bogashlugbovya1" || resurs == "bogashlugbovya2") && (checkDayOfYear || slugbovyiaTextu.checkLiturgia(raznica) || checkDataCalindar(slugbovyiaTextu, zmenyiaChastki.getData()))) {
            chechZmena = true
        }
        invalidateOptionsMenu()
    }

    private fun checkDataCalindar(slugbovyiaTextu: SlugbovyiaTextu, data: ArrayList<ArrayList<String>>): Boolean {
        slugbovyiaTextu.loadOpisanieSviat()
        val svity = data[0][6]
        daysv = data[0][1].toInt()
        munsv = data[0][2].toInt() + 1
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
        sviaty = slugbovyiaTextu.checkLiturgia(daysv, munsv)
        return sviaty
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            var autoTime: Long = 10000
            for (i in 0..15) {
                if (i == k.getInt("autoscrollAutostartTime", 5)) {
                    autoTime = (i + 5) * 1000L
                    break
                }
            }
            autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                delay(autoTime)
                startAutoScroll()
                invalidateOptionsMenu()
            }
        }
    }

    private fun stopAutoStartScroll() {
        autoStartScrollJob?.cancel()
    }

    private fun startProcent(delayTime: Long = 1000) {
        procentJob?.cancel()
        procentJob = CoroutineScope(Dispatchers.Main).launch {
            delay(delayTime)
            bindingprogress.progress.visibility = View.GONE
            bindingprogress.fontSize.visibility = View.GONE
            bindingprogress.brighess.visibility = View.GONE
        }
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditors = k.edit()
                prefEditors.putBoolean("autoscroll", false)
                prefEditors.apply()
            }
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            binding.textView.setTextIsSelectable(true)
            binding.textView.movementMethod = LinkMovementMethod.getInstance()
            autoScrollJob?.cancel()
            if (onFind) {
                onFind = false
                findAllAsanc(false)
                binding.find.visibility = View.VISIBLE
            }
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (diffScroll !in 0..1) {
            val prefEditors = k.edit()
            prefEditors.putBoolean("autoscroll", true)
            prefEditors.apply()
            if (binding.find.visibility == View.VISIBLE) {
                findRemoveSpan()
                onFind = true
                binding.find.visibility = View.GONE
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
            }
            binding.actionMinus.visibility = View.VISIBLE
            binding.actionPlus.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            binding.textView.setTextIsSelectable(false)
            stopAutoStartScroll()
            if (autoScrollJob?.isActive != true) {
                autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                    while (isActive) {
                        delay(spid.toLong())
                        if (!mActionDown && !MainActivity.dialogVisable) {
                            binding.scrollView2.smoothScrollBy(0, 2)
                        }
                    }
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            val duration: Long = 1000
            ObjectAnimator.ofInt(binding.scrollView2, "scrollY", 0).setDuration(duration).start()
            binding.scrollView2.postDelayed({
                startAutoScroll()
            }, duration)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
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
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (x > widthConstraintLayout - otstup) {
                        bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (y > heightConstraintLayout - otstup) {
                        if (binding.find.visibility == View.GONE) {
                            spid = k.getInt("autoscrollSpid", 60)
                            proc = 100 - (spid - 15) * 100 / 215
                            bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                            bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
                            bindingprogress.progress.visibility = View.VISIBLE
                            startProcent()
                            autoscroll = k.getBoolean("autoscroll", false)
                            if (!autoscroll) {
                                startAutoScroll()
                                invalidateOptionsMenu()
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        val itemVybranoe = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_vybranoe)
        val find = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_find)
        if (resurs.contains("bogashlugbovya") || resurs.contains("akafist") || resurs.contains("malitvy") || resurs.contains("ruzanec") || resurs.contains("viachernia") || resurs.contains("prynagodnyia")) {
            menu.findItem(by.carkva_gazeta.malitounik.R.id.action_share).isVisible = true
        }
        if (mAutoScroll) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                find.isVisible = false
                itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
            } else {
                find.isVisible = true
                itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
            }
        } else {
            itemAuto.isVisible = false
            stopAutoScroll()
        }
        var spanString = SpannableString(itemAuto.title.toString())
        var end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString

        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = k.getBoolean("dzen_noch", false)

        spanString = SpannableString(itemVybranoe.title.toString())
        end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemVybranoe.title = spanString
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_zmena).isVisible = chechZmena
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        stopAutoStartScroll()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.akafist, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        dzenNoch = k.getBoolean("dzen_noch", false)
        val prefEditor: Editor = k.edit()
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_zmena) {
            val slugba = SlugbovyiaTextu()
            val intent = Intent(this, Ton::class.java)
            when {
                sviaty -> {
                    intent.putExtra("ton_na_sviaty", true)
                    intent.putExtra("lityrgia", 4)
                    intent.putExtra("day", daysv)
                    intent.putExtra("mun", munsv)
                }
                checkDayOfYear -> {
                    val zmenyiaChastki = ZmenyiaChastki()
                    val resours = slugba.getResource(zmenyiaChastki.dayOfYear(), liturgia = true)
                    intent.putExtra("resurs", resours)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", slugba.getTitle(resours))
                }
                else -> {
                    val resours = slugba.getResource(raznica, liturgia = true)
                    intent.putExtra("resurs", resours)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", slugba.getTitle(resours))
                }
            }
            startActivity(intent)
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            editDzenNoch = true
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_find) {
            binding.find.visibility = View.VISIBLE
            binding.textSearch.requestFocus()
            EditTextCustom.focusAndShowKeyboard(binding.textSearch)
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_vybranoe) {
            editVybranoe = true
            men = setVybranoe(this, resurs, title)
            if (men) {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.addVybranoe))
            }
            invalidateOptionsMenu()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_bright) {
            val dialogBrightness = DialogBrightness()
            dialogBrightness.show(supportFragmentManager, "brightness")
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            if (k.getBoolean("FullscreenHelp", true)) {
                val dialogHelpFullscreen = DialogHelpFullscreen()
                dialogHelpFullscreen.show(supportFragmentManager, "FullscreenHelp")
            }
            fullscreenPage = true
            hide()
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            val rub = if (resurs.contains("prynagodnyia")) "pub=4"
            else "pub=2"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?$rub&file=$resurs")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
        }
        prefEditor.apply()
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val idres = resursMap[resurs] ?: R.raw.bogashlugbovya1
                val inputStream = resources.openRawResource(idres)
                val text = inputStream.use {
                    it.reader().readText()
                }
                intent.putExtra("resours", resurs)
                intent.putExtra("title", title)
                intent.putExtra("text", text)
                startActivity(intent)
            } else {
                MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.error))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            fullscreenPage -> {
                fullscreenPage = false
                show()
            }
            binding.find.visibility == View.VISIBLE -> {
                binding.find.visibility = View.GONE
                binding.textSearch.setText("")
                findRemoveSpan()
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
            }
            else -> {
                if (editVybranoe || editDzenNoch) {
                    val intent = Intent()
                    if (editDzenNoch) intent.putExtra("editDzenNoch", true)
                    setResult(200, intent)
                }
                super.onBackPressed()
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
        procentJob?.cancel()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        if (fullscreenPage) hide()
        autoscroll = k.getBoolean("autoscroll", false)
        if (autoscroll && !onRestore) {
            startAutoScroll()
        }
        spid = k.getInt("autoscrollSpid", 60)
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun hide() {
        supportActionBar?.hide()
        CoroutineScope(Dispatchers.Main).launch {
            mHidePart2Runnable()
        }
    }

    @Suppress("DEPRECATION")
    private fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            val controller = window.insetsController
            controller?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            mShowPart2Runnable()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("fullscreen", fullscreenPage)
        outState.putBoolean("editVybranoe", editVybranoe)
        outState.putBoolean("editDzenNoch", editDzenNoch)
        if (binding.find.visibility == View.VISIBLE) outState.putBoolean("seach", true)
        else outState.putBoolean("seach", false)
        outState.putString("textLine", firstTextPosition)
    }

    private data class SpanStr(val color: Int, val start: Int, val size: Int)
}
