package by.carkva_gazeta.resources

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.res.Configuration
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
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.resources.databinding.BogasluzbovyaBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class Bogashlugbovya : BaseActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, InteractiveScrollView.OnInteractiveScrollChangedCallback, LinkMovementMethodCheck.LinkMovementMethodCheckListener, DialogErrorData.DialogErrorDataListener {

    private var fullscreenPage = false
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var autoscroll = false
    private var n = 0
    private var spid = 60
    private var resurs = ""
    private var men = true
    private var checkVybranoe = false
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
    private val findListSpans = ArrayList<SpanStr>()
    private var animatopRun = false
    private var chechZmena = false
    private var checkLiturgia = 0
    private var raznica = 400
    private var dayOfYear = "1"
    private var checkDayOfYear = false
    private var sviaty = false
    private var daysv = 1
    private var munsv = 0
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
            resursMap["lit_jan_zalat"] = R.raw.lit_jan_zalat
            resursMap["lit_jan_zalat_vielikodn"] = R.raw.lit_jan_zalat_vielikodn
            resursMap["nabazenstva_maci_bozaj_niast_dap"] = R.raw.nabazenstva_maci_bozaj_niast_dap
            resursMap["jutran_niadzelnaja"] = R.raw.jutran_niadzelnaja
            resursMap["abiednica"] = R.raw.abiednica
            resursMap["kanon_malebny_baharodzicy"] = R.raw.kanon_malebny_baharodzicy
            resursMap["panichida_mal"] = R.raw.panichida_mal
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
            resursMap["bogashlugbovya17_9"] = R.raw.bogashlugbovya17_9
            resursMap["ndz_tamasza_liturhija"] = R.raw.ndz_tamasza_liturhija
            resursMap["ndz_miranosic_liturhija"] = R.raw.ndz_miranosic_liturhija
            resursMap["ndz_samaranki_liturhija"] = R.raw.ndz_samaranki_liturhija
            resursMap["ndz_slepanarodz_liturhija"] = R.raw.ndz_slepanarodz_liturhija
            resursMap["mm_06_08_pieramianiennie_liturhija"] = R.raw.mm_06_08_pieramianiennie_liturhija
            resursMap["akafist0"] = R.raw.akafist0
            resursMap["akafist1"] = R.raw.akafist1
            resursMap["akafist2"] = R.raw.akafist2
            resursMap["akafist3"] = R.raw.akafist3
            resursMap["akafist4"] = R.raw.akafist4
            resursMap["akafist5"] = R.raw.akafist5
            resursMap["akafist6"] = R.raw.akafist6
            resursMap["akafist7"] = R.raw.akafist7
            resursMap["akafist8"] = R.raw.akafist8
            resursMap["malitvy_ran"] = R.raw.malitvy_ran
            resursMap["malitvy_viaczernija"] = R.raw.malitvy_viaczernija
            resursMap["paslia_prychascia1"] = R.raw.paslia_prychascia1
            resursMap["paslia_prychascia2"] = R.raw.paslia_prychascia2
            resursMap["paslia_prychascia3"] = R.raw.paslia_prychascia3
            resursMap["paslia_prychascia4"] = R.raw.paslia_prychascia4
            resursMap["paslia_prychascia5"] = R.raw.paslia_prychascia5
            resursMap["prynagodnyia_0"] = R.raw.prynagodnyia_0
            resursMap["prynagodnyia_1"] = R.raw.prynagodnyia_1
            resursMap["prynagodnyia_2"] = R.raw.prynagodnyia_2
            resursMap["mltv_backou_za_dziaciej_boza_u_trojcy_adziny"] = R.raw.mltv_backou_za_dziaciej_boza_u_trojcy_adziny
            resursMap["prynagodnyia_4"] = R.raw.prynagodnyia_4
            resursMap["mltv_kiroucy"] = R.raw.mltv_kiroucy
            resursMap["prynagodnyia_6"] = R.raw.prynagodnyia_6
            resursMap["prynagodnyia_7"] = R.raw.prynagodnyia_7
            resursMap["prynagodnyia_8"] = R.raw.prynagodnyia_8
            resursMap["prynagodnyia_9"] = R.raw.prynagodnyia_9
            resursMap["prynagodnyia_10"] = R.raw.prynagodnyia_10
            resursMap["prynagodnyia_11"] = R.raw.prynagodnyia_11
            resursMap["prynagodnyia_12"] = R.raw.prynagodnyia_12
            resursMap["prynagodnyia_13"] = R.raw.prynagodnyia_13
            resursMap["mltv_za_chvoraha_milaserny_boza"] = R.raw.mltv_za_chvoraha_milaserny_boza
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
            resursMap["prynagodnyia_26"] = R.raw.prynagodnyia_26
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
            resursMap["viachernia_na_kozny_dzen"] = R.raw.viachernia_na_kozny_dzen
            resursMap["viachernia_u_vialikim_poscie"] = R.raw.viachernia_u_vialikim_poscie
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
            resursMap["mm_08_11_jutran"] = R.raw.mm_08_11_jutran
            resursMap["mm_08_11_viaczernia"] = R.raw.mm_08_11_viaczernia
            resursMap["mm_08_11_liturhija"] = R.raw.mm_08_11_liturhija
            resursMap["zychod_sv_ducha_jutran"] = R.raw.zychod_sv_ducha_jutran
            resursMap["zychod_sv_ducha_liturhija"] = R.raw.zychod_sv_ducha_liturhija
            resursMap["paniadzielak_sv_ducha_ndz_viaczaram"] = R.raw.paniadzielak_sv_ducha_ndz_viaczaram
            resursMap["uzniasienne_jutran"] = R.raw.uzniasienne_jutran
            resursMap["uzniasienne_liturhija"] = R.raw.uzniasienne_liturhija
            resursMap["uzniasienne_viaczernia"] = R.raw.uzniasienne_viaczernia
            resursMap["mm_21_11_liturhija"] = R.raw.mm_21_11_liturhija
            resursMap["mm_02_02_liturhija"] = R.raw.mm_02_02_liturhija
            resursMap["l_ajcy_6_saborau"] = R.raw.l_ajcy_6_saborau
            resursMap["mm_13_11_liturhija"] = R.raw.mm_13_11_liturhija
            resursMap["mm_13_11_viaczernia"] = R.raw.mm_13_11_viaczernia
            resursMap["mm_01_01_malebien_novy_hod"] = R.raw.mm_01_01_malebien_novy_hod
            resursMap["lit_vasila_vialikaha"] = R.raw.lit_vasila_vialikaha
            resursMap["mm_02_01_liturhija"] = R.raw.mm_02_01_liturhija
            resursMap["mm_04_01_pieradsv_bohazjaulennia_liturhija"] = R.raw.mm_04_01_pieradsv_bohazjaulennia_liturhija
            resursMap["mm_06_01_bohazjaulennie_liturhija"] = R.raw.mm_06_01_bohazjaulennie_liturhija
            resursMap["mm_05_01_sv_vieczar_bohazjaulennia_abiednica"] = R.raw.mm_05_01_sv_vieczar_bohazjaulennia_abiednica
            resursMap["mm_05_01_sv_vieczar_bohazjaulennia_vial_hadziny"] = R.raw.mm_05_01_sv_vieczar_bohazjaulennia_vial_hadziny
            resursMap["mltv_paciarpieli_za_bielarus"] = R.raw.mltv_paciarpieli_za_bielarus
            resursMap["mltv_pierad_jadoj_i_pasla"] = R.raw.mltv_pierad_jadoj_i_pasla
            resursMap["mltv_za_backou"] = R.raw.mltv_za_backou
            resursMap["kanon_a_kryckaha"] = R.raw.kanon_a_kryckaha
            resursMap["lit_ran_asv_dar"] = R.raw.lit_ran_asv_dar
            resursMap["viaczernia_bierascie"] = R.raw.viaczernia_bierascie
            resursMap["jutran_vial_piatn_12jevanhellau"] = R.raw.vialikaja_piatnica_jutran_12jevanhellau
            resursMap["vialikaja_piatnica_mal_paviaczernica"] = R.raw.vialikaja_piatnica_mal_paviaczernica
            resursMap["vialikaja_piatnica_viaczernia"] = R.raw.vialikaja_piatnica_viaczernia
            resursMap["vialiki_czacvier"] = R.raw.vialiki_czacvier
            resursMap["vialikaja_sierada"] = R.raw.vialikaja_sierada
            resursMap["vialiki_autorak"] = R.raw.vialiki_autorak
            resursMap["vialiki_paniadzielak"] = R.raw.vialiki_paniadzielak
            resursMap["mltv_za_pamierlych"] = R.raw.mltv_za_pamierlych
            resursMap["vialikaja_subota_jutran"] = R.raw.vialikaja_subota_jutran
            resursMap["vialikaja_subota_paunocznica"] = R.raw.vialikaja_subota_paunocznica
            resursMap["vialikaja_subota_viaczernia_liturhija"] = R.raw.vialikaja_subota_viaczernia_liturhija
            resursMap["vialikdzien_jutran"] = R.raw.vialikdzien_jutran
            resursMap["viaczernia_vialikdzien"] = R.raw.viaczernia_vialikdzien
            resursMap["u_svietly_paniadzielak"] = R.raw.u_svietly_paniadzielak
            resursMap["l_svietly_paniadzielak"] = R.raw.l_svietly_paniadzielak
            resursMap["v_svietly_paniadzielak"] = R.raw.v_svietly_paniadzielak
            resursMap["u_svietly_autorak"] = R.raw.u_svietly_autorak
            resursMap["l_svietly_autorak"] = R.raw.l_svietly_autorak
            resursMap["v_svietly_autorak"] = R.raw.v_svietly_autorak
            resursMap["u_svietlaja_sierada"] = R.raw.u_svietlaja_sierada
            resursMap["l_svietlaja_sierada"] = R.raw.l_svietlaja_sierada
            resursMap["v_svietlaja_sierada"] = R.raw.v_svietlaja_sierada
            resursMap["u_svietly_czacvier"] = R.raw.u_svietly_czacvier
            resursMap["l_svietly_czacvier"] = R.raw.l_svietly_czacvier
            resursMap["v_svietly_czacvier"] = R.raw.v_svietly_czacvier
            resursMap["u_svietlaja_piatnica"] = R.raw.u_svietlaja_piatnica
            resursMap["l_svietlaja_piatnica"] = R.raw.l_svietlaja_piatnica
            resursMap["v_svietlaja_piatnica"] = R.raw.v_svietlaja_piatnica
            resursMap["u_svietlaja_subota"] = R.raw.u_svietlaja_subota
            resursMap["l_svietlaja_subota"] = R.raw.l_svietlaja_subota
            resursMap["vielikodnyja_hadziny"] = R.raw.vielikodnyja_hadziny
            resursMap["ndz_tamasza_viaczernia_subota"] = R.raw.ndz_tamasza_viaczernia_subota
            resursMap["ndz_tamasza_paniadzielak"] = R.raw.ndz_tamasza_01paniadzielak
            resursMap["ndz_tamasza_autorak"] = R.raw.ndz_tamasza_02autorak
            resursMap["ndz_tamasza_sierada"] = R.raw.ndz_tamasza_03sierada
            resursMap["ndz_tamasza_czacvier"] = R.raw.ndz_tamasza_04czacvier
            resursMap["ndz_tamasza_piatnica"] = R.raw.ndz_tamasza_05piatnica
            resursMap["ndz_tamasza_jutran"] = R.raw.ndz_tamasza_jutran
            resursMap["ndz_tamasza_uvieczary"] = R.raw.ndz_tamasza_uvieczary
            resursMap["mm_15_08_uspiennie_liturhija"] = R.raw.mm_15_08_uspiennie_liturhija
            resursMap["mltv_mb_zyrovickaja"] = R.raw.mltv_mb_zyrovickaja
            resursMap["mm_07_05_liturhija"] = R.raw.mm_07_05_liturhija
            resursMap["ndz_rasslablenaha_liturhija"] = R.raw.ndz_rasslablenaha_liturhija
            resursMap["pesny_prasl_70"] = PesnyAll.resursMap["pesny_prasl_70"]
            resursMap["dzie_kuryja"] = R.raw.dzie_kuryja
            resursMap["dzie_centr_dekan"] = R.raw.dzie_centr_dekan
            resursMap["dzie_barysau"] = R.raw.dzie_barysau
            resursMap["dzie_zodzina"] = R.raw.dzie_zodzina
            resursMap["dzie_zaslauje"] = R.raw.dzie_zaslauje
            resursMap["dzie_maladechna"] = R.raw.dzie_maladechna
            resursMap["dzie_marjinahorka"] = R.raw.dzie_marjinahorka
            resursMap["dzie_miensk"] = R.raw.dzie_miensk
            resursMap["dzie_usxod_dekan"] = R.raw.dzie_usxod_dekan
            resursMap["dzie_viciebsk"] = R.raw.dzie_viciebsk
            resursMap["dzie_vorsha"] = R.raw.dzie_vorsha
            resursMap["dzie_homel"] = R.raw.dzie_homel
            resursMap["dzie_polacak"] = R.raw.dzie_polacak
            resursMap["dzie_mahilou"] = R.raw.dzie_mahilou
            resursMap["dzie_zaxod_dekan"] = R.raw.dzie_zaxod_dekan
            resursMap["dzie_baranavichy"] = R.raw.dzie_baranavichy
            resursMap["dzie_bierascie"] = R.raw.dzie_bierascie
            resursMap["dzie_horadnia"] = R.raw.dzie_horadnia
            resursMap["dzie_ivacevichy"] = R.raw.dzie_ivacevichy
            resursMap["dzie_lida"] = R.raw.dzie_lida
            resursMap["dzie_navahradak"] = R.raw.dzie_navahradak
            resursMap["dzie_pinsk"] = R.raw.dzie_pinsk
            resursMap["dzie_slonim"] = R.raw.dzie_slonim
            resursMap["dzie_antverpan"] = R.raw.dzie_antverpan
            resursMap["dzie_londan"] = R.raw.dzie_londan
            resursMap["dzie_varshava"] = R.raw.dzie_varshava
            resursMap["dzie_vilnia"] = R.raw.dzie_vilnia
            resursMap["dzie_viena"] = R.raw.dzie_viena
            resursMap["dzie_kalininhrad"] = R.raw.dzie_kalininhrad
            resursMap["dzie_praha"] = R.raw.dzie_praha
            resursMap["dzie_rym"] = R.raw.dzie_rym
            resursMap["dzie_sanktpieciarburg"] = R.raw.dzie_sanktpieciarburg
            resursMap["dzie_bielastok"] = R.raw.dzie_bielastok
            resursMap["mm_11_05_liturhija"] = R.raw.mm_11_05_liturhija
            resursMap["mltv_za_carkvu"] = R.raw.mltv_za_carkvu
            resursMap["malebien_kiryla_miatod"] = R.raw.malebien_kiryla_miatod
            resursMap["mm_21_05_liturhija"] = R.raw.mm_21_05_liturhija
            resursMap["mm_21_05_viaczernia"] = R.raw.mm_21_05_viaczernia
            resursMap["mm_23_05_jutran"] = R.raw.mm_23_05_jutran
            resursMap["mm_23_05_liturhija"] = R.raw.mm_23_05_liturhija
            resursMap["mm_23_05_viaczernia"] = R.raw.mm_23_05_viaczernia
            resursMap["ndz_ajcou_1susviet_saboru"] = R.raw.ndz_ajcou_1susviet_saboru
            resursMap["ndz_usich_sviatych_liturhija"] = R.raw.ndz_usich_sviatych_liturhija
            resursMap["ndz_usich_sv_biel_narodu_liturhija"] = R.raw.ndz_usich_sv_biel_narodu_liturhija
            resursMap["sluzba_za_pamierlych_na_kozny_dzien_tydnia"] = R.raw.sluzba_za_pamierlych_na_kozny_dzien_tydnia
            resursMap["mm_29_06_piatra_i_paula_liturhija"] = R.raw.mm_29_06_piatra_i_paula_liturhija
            resursMap["mm_30_06_sabor_12_apostalau_liturhija"] = R.raw.mm_30_06_sabor_12_apostalau_liturhija
            resursMap["sluzba_najsviaciejszaj_baharodzicy"] = R.raw.sluzba_najsviaciejszaj_baharodzicy
            resursMap["mltv_mb_budslauskaja"] = R.raw.mltv_mb_budslauskaja
            resursMap["mm_05_08_pieradsv_pieramianiennia_liturhija"] = R.raw.mm_05_08_pieradsv_pieramianiennia_liturhija
            resursMap["mm_05_08_pieradsv_pieramianiennia_viaczernia"] = R.raw.mm_05_08_pieradsv_pieramianiennia_viaczernia
            resursMap["mm_09_08_pasviaccie_pieramianennia_viaczernia"] = R.raw.mm_09_08_pasviaccie_pieramianennia_viaczernia
            resursMap["mm_09_08_pasviaccie_pieramianiennia_liturhija"] = R.raw.mm_09_08_pasviaccie_pieramianiennia_liturhija
            resursMap["mm_15_08_uspiennie_viaczernia"] = R.raw.mm_15_08_uspiennie_viaczernia
            resursMap["mm_10_08_viaczernia"] = R.raw.mm_10_08_viaczernia
            resursMap["mm_14_08_pieradsv_uspiennia_viaczernia"] = R.raw.mm_14_08_pieradsv_uspiennia_viaczernia
            resursMap["mm_14_08_pieradsv_uspiennia_liturhija"] = R.raw.mm_14_08_pieradsv_uspiennia_liturhija
            resursMap["mm_01_09_novy_hod_viaczernia"] = R.raw.mm_01_09_novy_hod_viaczernia
            resursMap["mm_01_09_novy_hod_liturhija"] = R.raw.mm_01_09_novy_hod_liturhija
            resursMap["mm_05_09_liturhija"] = R.raw.mm_05_09_liturhija
            resursMap["mm_07_09_pieradsv_naradz_baharodz_liturhija"] = R.raw.mm_07_09_pieradsv_naradz_baharodz_liturhija
            resursMap["mm_08_09_naradz_baharodzicy_liturhija"] = R.raw.mm_08_09_naradz_baharodzicy_liturhija
            resursMap["mm_09_09_pasviaccie_naradz_baharodz_liturhija"] = R.raw.mm_09_09_pasviaccie_naradz_baharodz_liturhija
            resursMap["mm_10_09_pasviaccie_naradz_baharodz_viaczernia"] = R.raw.mm_10_09_pasviaccie_naradz_baharodz_viaczernia
            resursMap["mm_11_09_pasviaccie_naradz_baharodz_viaczernia"] = R.raw.mm_11_09_pasviaccie_naradz_baharodz_viaczernia
            resursMap["mm_12_09_addannie_naradz_baharodzicy_viaczernia"] = R.raw.mm_12_09_addannie_naradz_baharodzicy_viaczernia
            resursMap["mm_13_09_pieradsv_uzvyszennia_liturhija"] = R.raw.mm_13_09_pieradsv_uzvyszennia_liturhija
            resursMap["mm_14_09_uzvyszennie_kryza_liturhija"] = R.raw.mm_14_09_uzvyszennie_kryza_liturhija
            resursMap["mm_15_09_pasviaccie_uzvyszennia_viaczernia"] = R.raw.mm_15_09_pasviaccie_uzvyszennia_viaczernia
            resursMap["mltv_za_chrosnikau"] = R.raw.mltv_za_chrosnikau
            resursMap["mm_01_10_pokryva_baharodzicy_liturhija"] = R.raw.mm_01_10_pokryva_baharodzicy_liturhija
            resursMap["mm_11_17_10_ajcou_7_susviet_saboru_liturhija"] = R.raw.mm_11_17_10_ajcou_7_susviet_saboru_liturhija
            resursMap["mltv_mb_barunskaja"] = R.raw.mltv_mb_barunskaja
            resursMap["mltv_za_carkvu_2"] = R.raw.mltv_za_carkvu_2
            resursMap["mltv_za_usich_i_za_usio"] = R.raw.mltv_za_usich_i_za_usio
            resursMap["mltv_u_czasie_chvaroby"] = R.raw.mltv_u_czasie_chvaroby
            resursMap["mltv_za_chvoraha_lekaru_dush_cielau"] = R.raw.mltv_za_chvoraha_lekaru_dush_cielau
            resursMap["mltv_za_viazniau"] = R.raw.mltv_za_viazniau
            resursMap["mm_06_12_mikoly_cudatvorcy_jutran"] = R.raw.mm_06_12_mikoly_cudatvorcy_jutran
            resursMap["mm_06_12_mikoly_cudatvorcy_liturhija"] = R.raw.mm_06_12_mikoly_cudatvorcy_liturhija
            resursMap["mm_06_12_mikoly_cudatvorcy_viaczernia"] = R.raw.mm_06_12_mikoly_cudatvorcy_viaczernia
            resursMap["mm_12_11_sviatamucz_jazafat_jutran"] = R.raw.mm_12_11_sviatamucz_jazafat_jutran
            resursMap["mm_12_11_sviatamucz_jazafat_liturhija"] = R.raw.mm_12_11_sviatamucz_jazafat_liturhija
            resursMap["mm_12_11_sviatamucz_jazafat_viaczernia"] = R.raw.mm_12_11_sviatamucz_jazafat_viaczernia
            resursMap["mm_20_12_rastvo_peradsviaccie_liturhija"] = R.raw.mm_20_12_rastvo_peradsviaccie_liturhija
            resursMap["mm_20_12_rastvo_peradsviaccie_viaczernia"] = R.raw.mm_20_12_rastvo_peradsviaccie_viaczernia
            resursMap["mm_24_12_rastvo_sv_vieczar_abednica"] = R.raw.mm_24_12_rastvo_sv_vieczar_abednica
            resursMap["mm_24_12_rastvo_sv_vieczar_jutran"] = R.raw.mm_24_12_rastvo_sv_vieczar_jutran
            resursMap["mm_24_12_rastvo_sv_vieczar_viaczernia"] = R.raw.mm_24_12_rastvo_sv_vieczar_viaczernia
            resursMap["mm_24_12_rastvo_sv_vieczar_vial_hadziny"] = R.raw.mm_24_12_rastvo_sv_vieczar_vial_hadziny
            resursMap["mm_25_12_naradzennie_chrystova_liturhija"] = R.raw.mm_25_12_naradzennie_chrystova_liturhija
            resursMap["mm_26_12_sabor_baharodzicy_liturhija"] = R.raw.mm_26_12_sabor_baharodzicy_liturhija
            resursMap["mm_28_12_liturhija"] = R.raw.mm_28_12_liturhija
            resursMap["sluzba_vyzvalen_biazvinna_zniavolenych"] = R.raw.sluzba_vyzvalen_biazvinna_zniavolenych
        }

        fun setVybranoe(context: Context, resurs: String, title: String): Boolean {
            var check = true
            val file = File(context.filesDir.toString() + "/Vybranoe.json")
            try {
                val gson = Gson()
                if (file.exists() && MenuVybranoe.vybranoe.isEmpty()) {
                    val type = TypeToken.getParameterized(ArrayList::class.java, VybranoeData::class.java).type
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
            if (!file.exists()) return false
            try {
                if (MenuVybranoe.vybranoe.isEmpty()) {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(ArrayList::class.java, VybranoeData::class.java).type
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
            binding.textView.layout?.let { layout ->
                val lineForVertical = layout.getLineForVertical(positionY)
                for (i in 0 until findListSpans.size) {
                    if (lineForVertical <= layout.getLineForOffset(findListSpans[i].start)) {
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
            binding.textView.layout?.let { layout ->
                val line = layout.getLineForOffset(findListSpans[findPosition].start)
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
        resurs = intent?.extras?.getString("resurs") ?: ""
        title = intent?.extras?.getString("title") ?: ""
        spid = k.getInt("autoscrollSpid", 60)
        val autoscrollOFF = intent?.extras?.containsKey("autoscrollOFF") ?: false
        if (autoscrollOFF) {
            mAutoScroll = false
        }
        binding.scrollView2.setOnScrollChangedCallback(this)
        binding.constraint.setOnTouchListener(this)
        if (savedInstanceState != null) {
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            orientation = savedInstanceState.getInt("orientation")
            MainActivity.dialogVisable = false
            if (savedInstanceState.getBoolean("seach")) {
                binding.find.visibility = View.VISIBLE
            }
            c.set(Calendar.DAY_OF_YEAR, savedInstanceState.getInt("day_of_year"))
            c.set(Calendar.YEAR, savedInstanceState.getInt("year"))
        } else {
            fullscreenPage = k.getBoolean("fullscreenPage", false)
        }
        setDatacalendar(savedInstanceState)
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.textView.textSize = fontBiblia
        DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary))
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            DrawableCompat.setTint(binding.textSearch.background, ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.progress.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressText.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            bindingprogress.progressTitle.setTextColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            binding.actionPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.brighessPlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.brighessMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.fontSizePlus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.fontSizeMinus.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionFullscreen.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            binding.actionBack.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
        }
        men = checkVybranoe(this, resurs)
        checkVybranoe = men
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
        binding.actionFullscreen.setOnClickListener {
            show()
        }
        binding.actionBack.setOnClickListener {
            onBack()
        }
        binding.scrollView2.setOnBottomReachedListener(object : InteractiveScrollView.OnBottomReachedListener {
            override fun onBottomReached(checkDiff: Boolean) {
                diffScroll = checkDiff
                autoscroll = false
                stopAutoScroll()
                invalidateOptionsMenu()
            }

            override fun onTouch(action: Boolean) {
                mActionDown = action
            }
        })
        binding.textView.movementMethod = setLinkMovementMethodCheck()
        setTollbarTheme()
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
                }
            }
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4)
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

    override fun setDataKaliandara() {
        val c = Calendar.getInstance()
        val i = Intent(this, CaliandarMun::class.java)
        i.putExtra("mun", c[Calendar.MONTH])
        i.putExtra("day", c[Calendar.DATE])
        i.putExtra("year", c[Calendar.YEAR])
        i.putExtra("getData", true)
        caliandarMunLauncher.launch(i)
    }

    private fun loadData(savedInstanceState: Bundle?) = CoroutineScope(Dispatchers.Main).launch {
        val liturgia = resurs == "lit_jan_zalat" || resurs == "lit_jan_zalat_vielikodn" || resurs == "lit_vasila_vialikaha" || resurs == "abiednica"
        val res = withContext(Dispatchers.IO) {
            var result = 0L
            withContext(Dispatchers.IO) {
                try {
                    var reqParam = URLEncoder.encode("getData", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("saveProgram", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    val mURL = URL("https://android.carkva-gazeta.by/admin/android.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        val sb = StringBuilder()
                        BufferedReader(InputStreamReader(inputStream)).use {
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                sb.append(inputLine)
                                inputLine = it.readLine()
                            }
                        }
                        val gson = Gson()
                        val type = TypeToken.getParameterized(Long::class.java).type
                        result = gson.fromJson<Long>(sb.toString(), type).toLong()
                    }
                } catch (_: Throwable) {
                }
                withContext(Dispatchers.Main) {
                    val kalSite = Calendar.getInstance()
                    kalSite.timeInMillis = result * 1000
                    if (!(c[Calendar.DAY_OF_YEAR] == kalSite[Calendar.DAY_OF_YEAR] && c[Calendar.YEAR] == kalSite[Calendar.YEAR])) {
                        val dialogErrorData = DialogErrorData.getInstance(kalSite[Calendar.DATE], kalSite[Calendar.MONTH], kalSite[Calendar.YEAR])
                        dialogErrorData.show(supportFragmentManager, "dialogErrorData")
                    }
                }
            }
            zmenyiaChastki.setDzenNoch(dzenNoch)
            val builder = StringBuilder()
            val id = resursMap[resurs] ?: R.raw.bogashlugbovya_error
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
            checkDayOfYear = slugbovyiaTextu.checkLiturgia(MenuCaliandar.getPositionCaliandar(c[Calendar.DAY_OF_YEAR], c[Calendar.YEAR])[22].toInt(), dayOfYear.toInt())
            if (liturgia && (checkDayOfYear || slugbovyiaTextu.checkLiturgia(raznica, c[Calendar.DAY_OF_YEAR]))) {
                chechZmena = true
                val resours = slugbovyiaTextu.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                val idZmenyiaChastki = resursMap[resours] ?: R.raw.bogashlugbovya_error
                nochenia = slugbovyiaTextu.checkFullChtenia(idZmenyiaChastki)
            }
            if ((resurs == "lit_ran_asv_dar" || resurs == "viaczernia_bierascie") && (checkDayOfYear || slugbovyiaTextu.checkViachernia(raznica, c[Calendar.DAY_OF_YEAR]))) {
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
                                builder.append("Глядзіце тут").append("<br>\n")
                            } else {
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
                        }
                        if (line.contains("PRAKIMEN")) {
                            line = line.replace("PRAKIMEN", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append("Глядзіце тут").append("<br>\n")
                            } else {
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
                        }
                        if (line.contains("ALILUIA")) {
                            line = line.replace("ALILUIA", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append("Глядзіце тут").append("<br>\n")
                            } else {
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
                        }
                        if (line.contains("PRICHASNIK")) {
                            line = line.replace("PRICHASNIK", "")
                            builder.append(line)
                            if (chechZmena) {
                                builder.append("Глядзіце тут").append("<br>\n")
                            } else {
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
                        }
                        when {
                            line.contains("APCH") -> {
                                line = line.replace("APCH", "")
                                if (chechZmena && !nochenia) {
                                    builder.append("<br>").append("Глядзіце тут").append("<br><br>\n")
                                } else {
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
                            }
                            line.contains("EVCH") -> {
                                line = line.replace("EVCH", "")
                                if (chechZmena && !nochenia) {
                                    builder.append("<br>").append("Глядзіце тут").append("<br><br>\n")
                                } else {
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
        if (resurs == "viachernia_niadzeli") {
            string = "ліцьця і блаславеньне хлябоў"
            strLig = string.length
            t1 = text.indexOf(string)
            if (t1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(this@Bogashlugbovya, Bogashlugbovya::class.java)
                        intent.putExtra("autoscrollOFF", autoscroll)
                        intent.putExtra("title", "Ліцьця і блаславеньне хлябоў")
                        intent.putExtra("resurs", "viachernia_liccia_i_blaslavenne_xliabou")
                        startActivity(intent)
                    }
                }, t1, t1 + strLig, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        if (resurs == "lit_ran_asv_dar") {
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
        if (resurs == "viaczernia_bierascie") {
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
            stringVB = "вершы Пс 140"
            strLigVB = stringVB.length
            vbt1 = text.indexOf(stringVB)
            if (vbt1 != -1) {
                text.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val dialogLiturgia = DialogLiturgia.getInstance(12)
                        dialogLiturgia.show(supportFragmentManager, "dialog_liturgia")
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
                            var strPosition = text.indexOf("Адзінародны Сыне", bst2 + strLigBS2, true)
                            if (resurs == "lit_jan_zalat") strPosition = text.indexOf("Адзінародны Сыне", strPosition + 16, true)
                            val line = layout.getLineForOffset(strPosition)
                            val y = layout.getLineTop(line)
                            val anim = ObjectAnimator.ofInt(binding.scrollView2, "scrollY", binding.scrollView2.scrollY, y)
                            anim.setDuration(1500).start()
                        }
                    }
                }, bst2, bst2 + strLigBS2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
                if (binding.textView.bottom <= binding.scrollView2.height) {
                    stopAutoStartScroll()
                    mAutoScroll = false
                    invalidateOptionsMenu()
                } else if (k.getBoolean("autoscrollAutostart", false) && mAutoScroll) {
                    autoStartScroll()
                }
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
                    if (binding.textView.bottom <= binding.scrollView2.height) {
                        stopAutoStartScroll()
                        mAutoScroll = false
                        invalidateOptionsMenu()
                    }
                    binding.scrollView2.scrollBy(0, positionY)
                    if (((k.getBoolean("autoscrollAutostart", false) && mAutoScroll) || autoscroll) && !diffScroll) {
                        autoStartScroll()
                    }
                }
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
                edit = edit.replace("и", "і")
                edit = edit.replace("щ", "ў")
                edit = edit.replace("ъ", "'")
                edit = edit.replace("И", "І")
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
        val stringGTA1 = getString(by.carkva_gazeta.malitounik.R.string.gl_tyt)
        val strLigGTA1 = stringGTA1.length
        val bsatGTA1 = text.indexOf(stringGTA1, index)
        if (bsatGTA1 != -1) {
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
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        spid = 230
                        autoScroll()
                        for (i in 0..9) {
                            delay(1000L)
                            spid -= autoTime
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
                        bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.Bright)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.brighess.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        bindingprogress.progressText.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.font_size)
                        bindingprogress.progress.visibility = View.VISIBLE
                        bindingprogress.fontSize.visibility = View.VISIBLE
                        startProcent(3000)
                    }
                    if (y > heightConstraintLayout - otstup) {
                        if (mAutoScroll && binding.find.visibility == View.GONE) {
                            spid = k.getInt("autoscrollSpid", 60)
                            proc = 100 - (spid - 15) * 100 / 215
                            bindingprogress.progressText.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                            bindingprogress.progressTitle.text = getString(by.carkva_gazeta.malitounik.R.string.speed_auto_scroll)
                            bindingprogress.progress.visibility = View.VISIBLE
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
        //menu.findItem(by.carkva_gazeta.malitounik.R.id.action_share).isVisible = true
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
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString

        if (men) {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_on)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe_del)
        } else {
            itemVybranoe.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.star_big_off)
            itemVybranoe.title = resources.getString(by.carkva_gazeta.malitounik.R.string.vybranoe)
        }
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = dzenNoch
        if (k.getBoolean("auto_dzen_noch", false)) menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isVisible = false
        spanString = SpannableString(itemVybranoe.title.toString())
        end = spanString.length
        spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemVybranoe.title = spanString
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_zmena).isVisible = chechZmena
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_mun).isVisible = true
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
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.akafist, menu)
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
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
                    val resours = slugba.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                    intent.putExtra("autoscrollOFF", autoscroll)
                    intent.putExtra("resurs", resours)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", slugba.getTitle(resours))
                }
                else -> {
                    val resours = if (checkLiturgia == 0) slugba.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.LITURHIJA)
                    else slugba.getResource(raznica, dayOfYear.toInt(), SlugbovyiaTextu.VIACZERNIA)
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
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.apply()
            recreate()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_find) {
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
            hide()
            return true
        }
        /*if (id == by.carkva_gazeta.malitounik.R.id.action_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            val shareTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://carkva-gazeta.by/share/index.php?pub=2&title=$shareTitle&file=$resurs")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, null))
            return true
        }*/
        prefEditor.apply()
        if (id == by.carkva_gazeta.malitounik.R.id.action_carkva) {
            if (MainActivity.checkmodulesAdmin()) {
                val intent = Intent()
                intent.setClassName(this, MainActivity.PASOCHNICALIST)
                val idres = resursMap[resurs] ?: R.raw.bogashlugbovya_error
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

    override fun onBack() {
        when {
            binding.find.visibility == View.VISIBLE -> {
                binding.find.visibility = View.GONE
                binding.textSearch.setText("")
                findRemoveSpan()
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.textSearch.windowToken, 0)
            }
            intent.extras?.getBoolean("chekVybranoe", false) == true && men != checkVybranoe -> {
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
                hide()
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
    }

    private data class SpanStr(val color: Int, val start: Int, val size: Int)
}
