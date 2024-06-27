package by.carkva_gazeta.resources

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
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogBrightness
import by.carkva_gazeta.malitounik.DialogFontSize
import by.carkva_gazeta.malitounik.DialogFontSize.DialogFontSizeListener
import by.carkva_gazeta.malitounik.DialogHelpFullScreenSettings
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuBibleBokuna
import by.carkva_gazeta.malitounik.MenuBibleCarniauski
import by.carkva_gazeta.malitounik.MenuBibleSemuxa
import by.carkva_gazeta.malitounik.MenuBibleSinoidal
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItemMaranataBinding
import by.carkva_gazeta.resources.databinding.AkafistMaranAtaBinding
import by.carkva_gazeta.resources.databinding.ProgressBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Calendar

class MaranAta : BaseActivity(), OnTouchListener, DialogFontSizeListener, OnItemClickListener, OnItemLongClickListener, DialogHelpFullScreen.DialogFullScreenHelpListener, DialogHelpFullScreenSettings.DialogHelpFullScreenSettingsListener, DialogBibleNatatka.DialogBibleNatatkaListiner, DialogAddZakladka.DialogAddZakladkiListiner, DialogPerevodBiblii.DialogPerevodBibliiListener {

    private var fullscreenPage = false
    private var cytanne = ""
    private lateinit var k: SharedPreferences
    private var fontBiblia = SettingsActivity.GET_FONT_SIZE_DEFAULT
    private val dzenNoch get() = getBaseDzenNoch()
    private var autoscroll = false
    private lateinit var adapter: MaranAtaListAdaprer
    private val maranAta = ArrayList<MaranAtaData>()
    private var n = 0
    private var spid = 60
    private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI
    private var mActionDown = false
    private var paralel = false
    private var paralelPosition = 0
    private var maranAtaScrollPosition = 0
    private lateinit var binding: AkafistMaranAtaBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJobBrightness: Job? = null
    private var procentJobFont: Job? = null
    private var procentJobAuto: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var resetTitleJob: Job? = null
    private var diffScroll = -1
    private var scrolltosatrt = false
    private var orientation = Configuration.ORIENTATION_UNDEFINED
    private var mun = 0
    private var day = 1
    private var isSmoothScrollToPosition = false
    private var vybranae = false
    private var prodoljyt = false
    private var title = ""

    override fun onDialogFontSize(fontSize: Float) {
        fontBiblia = fontSize
        binding.conteiner.textSize = fontBiblia
        adapter.notifyDataSetChanged()
    }

    private fun clearEmptyPosition() {
        val remove = ArrayList<ArrayList<Int>>()
        for (i in BibleGlobalList.vydelenie.indices) {
            var posrem = true
            for (e in 1 until BibleGlobalList.vydelenie[i].size) {
                if (BibleGlobalList.vydelenie[i][e] == 1) {
                    posrem = false
                    break
                }
            }
            if (posrem) {
                remove.add(BibleGlobalList.vydelenie[i])
            }
        }
        BibleGlobalList.vydelenie.removeAll(remove.toSet())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MenuBibleSemuxa.loadNatatkiZakladkiSemuxa(this)
        MenuBibleSinoidal.loadNatatkiZakladkiSinodal(this)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        spid = k.getInt("autoscrollSpid", 60)
        binding = AkafistMaranAtaBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        setTollbarTheme()
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        binding.conteiner.textSize = fontBiblia
        binding.ListView.onItemClickListener = this
        binding.ListView.onItemLongClickListener = this
        binding.ListView.setOnTouchListener(this)
        adapter = MaranAtaListAdaprer(this)
        binding.ListView.adapter = adapter
        binding.ListView.divider = null
        cytanne = intent.extras?.getString("cytanneMaranaty") ?: ""
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH]) ?: c[Calendar.MONTH]
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        vybranae = intent.extras?.getBoolean("vybranae", false) ?: false
        prodoljyt = intent.extras?.getBoolean("prodoljyt", false) ?: false
        title = intent.extras?.getString("title", "") ?: ""
        perevod = if (vybranae) {
            DialogVybranoeBibleList.perevod
        } else {
            k.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
        }
        maranAtaScrollPosition = if (vybranae) k.getInt(perevod + "BibleVybranoeScroll", 0)
        else k.getInt("maranAtaScrollPasition", 0)
        setMaranata(savedInstanceState)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            if (vybranae) {
                when (perevod) {
                    DialogVybranoeBibleList.PEREVODSEMUXI -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia)
                    DialogVybranoeBibleList.PEREVODSINOIDAL -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)
                    DialogVybranoeBibleList.PEREVODNADSAN -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_psalter)
                    DialogVybranoeBibleList.PEREVODBOKUNA -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia_bokun)
                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia_charniauski)
                }
            } else {
                binding.titleToolbar.text = savedInstanceState.getString("tollBarText", getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])) ?: getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
            }
            binding.subtitleToolbar.text = savedInstanceState.getString("subTollBarText", "") ?: ""
            paralel = savedInstanceState.getBoolean("paralel", paralel)
            orientation = savedInstanceState.getInt("orientation")
            if (paralel) {
                paralelPosition = savedInstanceState.getInt("paralelPosition")
                parralelMestaView(paralelPosition)
            }
        } else {
            if (vybranae) {
                when (perevod) {
                    DialogVybranoeBibleList.PEREVODSEMUXI -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia)
                    DialogVybranoeBibleList.PEREVODSINOIDAL -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)
                    DialogVybranoeBibleList.PEREVODNADSAN -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_psalter)
                    DialogVybranoeBibleList.PEREVODBOKUNA -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia_bokun)
                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblia_charniauski)
                }
            } else {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
            }
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            if (k.getBoolean("autoscrollAutostart", false)) {
                autoStartScroll()
            }
        }
        checkDay()
        bindingprogress.seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fontBiblia != SettingsActivity.getFontSize(progress)) {
                    fontBiblia = SettingsActivity.getFontSize(progress)
                    bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                    val prefEditor = k.edit()
                    prefEditor.putFloat("font_biblia", fontBiblia)
                    prefEditor.apply()
                    onDialogFontSize(fontBiblia)
                }
                startProcent(MainActivity.PROGRESSACTIONFONT)
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
                    bindingprogress.progressBrighess.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                    MainActivity.checkBrightness = false
                }
                startProcent(MainActivity.PROGRESSACTIONBRIGHESS)
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
                bindingprogress.progressAuto.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTOLEFT)
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
        binding.constraint.setOnTouchListener(this)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            binding.textViewTitle.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_dark_maranata_buttom)
            bindingprogress.seekBarBrighess.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
            bindingprogress.seekBarFontSize.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_progress_noch)
        }
        TooltipCompat.setTooltipText(binding.copyBig, getString(by.carkva_gazeta.malitounik.R.string.copy_big))
        TooltipCompat.setTooltipText(binding.adpravit, getString(by.carkva_gazeta.malitounik.R.string.share))
        TooltipCompat.setTooltipText(binding.yelloy, getString(by.carkva_gazeta.malitounik.R.string.set_yelloy))
        TooltipCompat.setTooltipText(binding.underline, getString(by.carkva_gazeta.malitounik.R.string.set_underline))
        TooltipCompat.setTooltipText(binding.bold, getString(by.carkva_gazeta.malitounik.R.string.set_bold))
        binding.copyBigFull.setOnClickListener {
            var glava = if (BibleGlobalList.bibleCopyList.size > 0) maranAta[BibleGlobalList.bibleCopyList[0]].glava
            else -1
            var kniga = if (BibleGlobalList.bibleCopyList.size > 0) maranAta[BibleGlobalList.bibleCopyList[0]].kniga
            else -1
            BibleGlobalList.bibleCopyList.clear()
            val firstVisiblePosition = binding.ListView.firstVisiblePosition
            for (i in firstVisiblePosition until maranAta.size) {
                if (maranAta[i].kniga == -1) continue
                if (glava == -1) {
                    kniga = maranAta[i].kniga
                    glava = maranAta[i].glava
                    break
                }
            }
            for (i in 0 until maranAta.size) {
                if (glava == maranAta[i].glava && kniga == maranAta[i].kniga) BibleGlobalList.bibleCopyList.add(i)
            }
            binding.view.visibility = View.GONE
            binding.yelloy.visibility = View.GONE
            binding.underline.visibility = View.GONE
            binding.bold.visibility = View.GONE
            binding.zakladka.visibility = View.GONE
            binding.zametka.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }
        binding.copyBig.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyString = StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
                    val textView = maranAta[it].bible
                    copyString.append("$textView<br>")
                }
                val clip = ClipData.newPlainText("", MainActivity.fromHtml(copyString.toString()).toString().trim())
                clipboard.setPrimaryClip(clip)
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.copy))
                binding.linearLayout4.visibility = View.GONE
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.adpravit.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyString = StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
                    val textView = maranAta[it].bible
                    copyString.append("$textView<br>")
                }
                val share = MainActivity.fromHtml(copyString.toString()).toString().trim()
                val clip = ClipData.newPlainText("", share)
                clipboard.setPrimaryClip(clip)
                binding.linearLayout4.visibility = View.GONE
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, share)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, null))
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.underline.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                if (maranAta[BibleGlobalList.bibleCopyList[0]].underline == 0) {
                    maranAta[BibleGlobalList.bibleCopyList[0]].underline = 1
                } else {
                    maranAta[BibleGlobalList.bibleCopyList[0]].underline = 0
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.bold.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                if (maranAta[BibleGlobalList.bibleCopyList[0]].bold == 0) {
                    maranAta[BibleGlobalList.bibleCopyList[0]].bold = 1
                } else {
                    maranAta[BibleGlobalList.bibleCopyList[0]].bold = 0
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.yelloy.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                if (maranAta[BibleGlobalList.bibleCopyList[0]].color == 0) {
                    maranAta[BibleGlobalList.bibleCopyList[0]].color = 1
                } else {
                    maranAta[BibleGlobalList.bibleCopyList[0]].color = 0
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.zakladka.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                var index = -1
                val list = when (maranAta[BibleGlobalList.bibleCopyList[0]].perevod) {
                    DialogVybranoeBibleList.PEREVODSEMUXI -> BibleGlobalList.zakladkiSemuxa
                    DialogVybranoeBibleList.PEREVODSINOIDAL -> BibleGlobalList.zakladkiSinodal
                    DialogVybranoeBibleList.PEREVODBOKUNA -> BibleGlobalList.zakladkiBokuna
                    DialogVybranoeBibleList.PEREVODCARNIAUSKI -> BibleGlobalList.zakladkiCarniauski
                    else -> BibleGlobalList.zakladkiSemuxa
                }
                for (i in list.indices) {
                    if (list[i].data.contains(maranAta[BibleGlobalList.bibleCopyList[0]].bible.substring(0, maranAta[BibleGlobalList.bibleCopyList[0]].bible.length - 1))) {
                        index = i
                        break
                    }
                }
                if (index == -1) {
                    val dialog = DialogAddZakladka()
                    dialog.show(supportFragmentManager, "DialogAddZakladka")
                } else {
                    list.removeAt(index)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                }
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                adapter.notifyDataSetChanged()
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        binding.zametka.setOnClickListener {
            if (BibleGlobalList.bibleCopyList.size > 0) {
                val t1 = maranAta[BibleGlobalList.bibleCopyList[0]].title.lastIndexOf(" ")
                val knigaBible = if (t1 != -1) maranAta[BibleGlobalList.bibleCopyList[0]].title.substring(0, t1)
                else maranAta[BibleGlobalList.bibleCopyList[0]].title
                val razdelName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal)
                else resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel)
                val vershName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) getString(by.carkva_gazeta.malitounik.R.string.stix_ru)
                else getString(by.carkva_gazeta.malitounik.R.string.stix_by)
                val knigaName = knigaBible + "/" + razdelName + " " + (maranAta[BibleGlobalList.bibleCopyList[0]].glava + 1) + vershName + " " + (maranAta[BibleGlobalList.bibleCopyList[0]].styx)
                val kniga = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) getNumarKnigi(maranAta[BibleGlobalList.bibleCopyList[0]].kniga)
                else getNumarKnigiBelarusPerevoda(getNumarKnigi(maranAta[BibleGlobalList.bibleCopyList[0]].kniga))
                val zametka = DialogBibleNatatka.getInstance(perevod = maranAta[BibleGlobalList.bibleCopyList[0]].perevod, novyzavet = maranAta[BibleGlobalList.bibleCopyList[0]].novyZapavet, kniga = kniga, glava = maranAta[BibleGlobalList.bibleCopyList[0]].glava, stix = maranAta[BibleGlobalList.bibleCopyList[0]].styx - 1, bibletext = knigaName)
                zametka.show(supportFragmentManager, "bible_zametka")
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
            } else {
                MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
            }
        }
        if (dzenNoch) {
            binding.linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta)
        }
    }

    override fun addZakladka(color: Int) {
        if (color != -1) {
            var maxIndex: Long = 0
            BibleGlobalList.zakladkiSinodal.forEach {
                if (maxIndex < it.id) maxIndex = it.id
            }
            maxIndex++
            val t1 = maranAta[BibleGlobalList.bibleCopyList[0]].title.lastIndexOf(" ")
            val knigaBible = if (t1 != -1) maranAta[BibleGlobalList.bibleCopyList[0]].title.substring(0, t1)
            else maranAta[BibleGlobalList.bibleCopyList[0]].title
            val razdelName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal)
            else resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel)
            val vershName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) getString(by.carkva_gazeta.malitounik.R.string.stix_ru)
            else getString(by.carkva_gazeta.malitounik.R.string.stix_by)
            val data = BibleZakladkiData(maxIndex, knigaBible + "/" + razdelName + " " + (maranAta[BibleGlobalList.bibleCopyList[0]].glava + 1) + vershName + " " + maranAta[BibleGlobalList.bibleCopyList[0]].styx + "\n\n" + maranAta[BibleGlobalList.bibleCopyList[0]].bible + "<!--" + color)
            if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) BibleGlobalList.zakladkiSinodal.add(0, data)
            else BibleGlobalList.zakladkiSemuxa.add(0, data)
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
        }
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun addNatatka() {
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun smoothScrollToPosition(position: Int) {
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            private var checkDiff = false

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                mActionDown = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                if (view.firstVisiblePosition != position) {
                    isSmoothScrollToPosition = false
                }
                if (isSmoothScrollToPosition && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    isSmoothScrollToPosition = false
                    CoroutineScope(Dispatchers.Main).launch {
                        view.setSelection(position)
                    }
                }
            }

            override fun onScroll(list: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (list.adapter == null || list.getChildAt(0) == null) return
                val firstPosition = list.firstVisiblePosition
                val nazva = maranAta[list.firstVisiblePosition].title
                if (fullscreenPage) {
                    if (firstPosition <= maranAtaScrollPosition) {
                        if (binding.textViewTitle.visibility == View.GONE) {
                            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                            binding.textViewTitle.visibility = View.VISIBLE
                            binding.textViewTitle.animation = animation
                        }
                        if (resetTitleJob?.isActive == true) resetTitleJob?.cancel()
                        if (resetTitleJob?.isActive != true) {
                            resetTitleJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(3000L)
                                val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
                                binding.textViewTitle.visibility = View.GONE
                                binding.textViewTitle.animation = animation2
                            }
                        }
                    }
                    binding.textViewTitle.text = nazva
                }
                maranAtaScrollPosition = firstPosition
                if (firstPosition == 0 && scrolltosatrt) {
                    autoStartScroll()
                    scrolltosatrt = false
                    invalidateOptionsMenu()
                }
                diffScroll = if (list.lastVisiblePosition == list.adapter.count - 1) list.getChildAt(list.childCount - 1).bottom - list.height
                else -1
                if (checkDiff && diffScroll > 0) {
                    checkDiff = false
                    invalidateOptionsMenu()
                }
                if (list.lastVisiblePosition == list.adapter.count - 1 && list.getChildAt(list.childCount - 1).bottom <= list.height) {
                    checkDiff = true
                    autoscroll = false
                    stopAutoScroll()
                    invalidateOptionsMenu()
                }
                val nazvaView = binding.subtitleToolbar.text.toString()
                if (nazva != nazvaView || nazvaView == "") {
                    binding.subtitleToolbar.text = nazva
                }
            }
        })
        if (isSmoothScrollToPosition) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.ListView.smoothScrollToPositionFromTop(position, 0)
            }
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = by.carkva_gazeta.malitounik.R.style.AppCompatDark
        }
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
        binding.subtitleToolbar.isSingleLine = true
    }

    private fun checkDay() {
        val c = Calendar.getInstance()
        if (!(mun == c[Calendar.MONTH] && day == c[Calendar.DATE])) {
            binding.appBarLayout.background = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_strogi_post)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        if (binding.linearLayout4.visibility == View.VISIBLE) {
            return false
        }
        val heightConstraintLayout = binding.constraint.height
        val widthConstraintLayout = binding.constraint.width
        val otstup = (10 * resources.displayMetrics.density).toInt()
        val otstup2 = if (autoscroll) (50 * resources.displayMetrics.density).toInt()
        else 0
        val otstup3 = when {
            fullscreenPage && autoscroll -> (160 * resources.displayMetrics.density).toInt()
            autoscroll -> (110 * resources.displayMetrics.density).toInt()
            else -> 0
        }
        val y = event?.y?.toInt() ?: 0
        val x = event?.x?.toInt() ?: 0
        val id = v?.id ?: 0
        if (id == R.id.ListView) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> mActionDown = true
                MotionEvent.ACTION_UP -> mActionDown = false
            }
            return false
        }
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
                        bindingprogress.progressBrighess.text = getString(by.carkva_gazeta.malitounik.R.string.procent, MainActivity.brightness)
                        if (bindingprogress.seekBarBrighess.visibility == View.GONE) {
                            bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_right)
                            bindingprogress.seekBarBrighess.visibility = View.VISIBLE
                        }
                        startProcent(MainActivity.PROGRESSACTIONBRIGHESS)
                    }
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
                        bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
                        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
                            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
                            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
                        }
                        startProcent(MainActivity.PROGRESSACTIONFONT)
                    }
                    if (y > heightConstraintLayout - otstup && x < widthConstraintLayout - otstup3) {
                        spid = k.getInt("autoscrollSpid", 60)
                        proc = 100 - (spid - 15) * 100 / 215
                        bindingprogress.progressAuto.text = resources.getString(by.carkva_gazeta.malitounik.R.string.procent, proc)
                        startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                        startAutoScroll()
                        invalidateOptionsMenu()
                    }
                }
            }
        }
        return true
    }

    private fun getBibleNameFull(kniga: Int, perevod: String): String {
        var fullKniga = kniga - 1
        if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI || perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
            when (kniga - 1) {
                19 -> fullKniga = 16
                20 -> fullKniga = 17
                21 -> fullKniga = 18
                22 -> fullKniga = 19
                23 -> fullKniga = 20
                24 -> fullKniga = 21
                27 -> fullKniga = 22
                28 -> fullKniga = 23
                29 -> fullKniga = 24
                32 -> fullKniga = 25
                33 -> fullKniga = 26
                34 -> fullKniga = 27
                35 -> fullKniga = 28
                36 -> fullKniga = 29
                37 -> fullKniga = 30
                38 -> fullKniga = 31
                39 -> fullKniga = 32
                40 -> fullKniga = 33
                41 -> fullKniga = 34
                42 -> fullKniga = 35
                43 -> fullKniga = 36
                44 -> fullKniga = 37
                45 -> fullKniga = 38
                50 -> fullKniga = 39
                51 -> fullKniga = 40
                52 -> fullKniga = 41
                53 -> fullKniga = 42
                54 -> fullKniga = 43
                55 -> fullKniga = 44
                56 -> fullKniga = 45
                57 -> fullKniga = 46
                58 -> fullKniga = 47
                59 -> fullKniga = 48
                60 -> fullKniga = 49
                61 -> fullKniga = 50
                62 -> fullKniga = 51
                63 -> fullKniga = 52
                64 -> fullKniga = 53
                65 -> fullKniga = 54
                66 -> fullKniga = 55
                67 -> fullKniga = 56
                68 -> fullKniga = 57
                69 -> fullKniga = 58
                70 -> fullKniga = 59
                71 -> fullKniga = 60
                72 -> fullKniga = 61
                73 -> fullKniga = 62
                74 -> fullKniga = 63
                75 -> fullKniga = 64
                76 -> fullKniga = 65
            }
        }
        if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
            when (kniga - 1) {
                19 -> fullKniga = 16
                20 -> fullKniga = 17
                21 -> fullKniga = 18
                22 -> fullKniga = 19
                23 -> fullKniga = 20
                24 -> fullKniga = 21
                27 -> fullKniga = 22
                28 -> fullKniga = 23
                29 -> fullKniga = 24
                32 -> fullKniga = 25
                33 -> fullKniga = 26
                34 -> fullKniga = 27
                35 -> fullKniga = 28
                36 -> fullKniga = 29
                37 -> fullKniga = 30
                38 -> fullKniga = 31
                39 -> fullKniga = 32
                40 -> fullKniga = 33
                41 -> fullKniga = 34
                42 -> fullKniga = 35
                43 -> fullKniga = 36
                44 -> fullKniga = 37
                45 -> fullKniga = 38
                17 -> fullKniga = 39
                18 -> fullKniga = 40
                25 -> fullKniga = 41
                26 -> fullKniga = 42
                31 -> fullKniga = 43
                46 -> fullKniga = 44
                47 -> fullKniga = 45
                51 -> fullKniga = 47
                52 -> fullKniga = 48
                53 -> fullKniga = 49
                54 -> fullKniga = 50
                55 -> fullKniga = 51
                56 -> fullKniga = 52
                57 -> fullKniga = 53
                58 -> fullKniga = 54
                59 -> fullKniga = 55
                60 -> fullKniga = 56
                61 -> fullKniga = 57
                62 -> fullKniga = 58
                63 -> fullKniga = 59
                64 -> fullKniga = 60
                65 -> fullKniga = 61
                66 -> fullKniga = 62
                67 -> fullKniga = 63
                68 -> fullKniga = 64
                69 -> fullKniga = 65
                70 -> fullKniga = 66
                71 -> fullKniga = 67
                72 -> fullKniga = 68
                73 -> fullKniga = 69
                74 -> fullKniga = 70
                75 -> fullKniga = 71
                76 -> fullKniga = 72
            }
        }
        if (perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
            fullKniga = 0
        }
        val list = when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan))
            DialogVybranoeBibleList.PEREVODSINOIDAL -> resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln))
            DialogVybranoeBibleList.PEREVODNADSAN -> arrayOf(getString(by.carkva_gazeta.malitounik.R.string.psalom2))
            DialogVybranoeBibleList.PEREVODBOKUNA -> resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan))
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskis).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskin))
            else -> resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas).plus(resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan))
        }
        val t4 = list[fullKniga].indexOf("#")
        return list[fullKniga].substring(0, t4)
    }

    override fun setPerevod(perevod: String) {
        saveVydelenieNatatkiZakladki()
        val edit = k.edit()
        edit.putString("perevod", perevod)
        edit.apply()
        if (this.perevod != perevod) {
            this.perevod = perevod
            setMaranata(null)
        }
    }

    private fun setMaranata(savedInstanceState: Bundle?) {
        maranAta.clear()
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        val chten = cytanne.split(";")
        var saveName = ""
        for (i in chten.indices) {
            val fit = chten[i].trim()
            val paralelnyeMesta = ParalelnyeMesta()
            val bible = paralelnyeMesta.biblia(fit)
            val kniga = bible[0]
            val nomer = bible[3].toInt()
            try {
                var nachalo: Int
                var konec: Int
                var stixn = -1
                var stixk = -1
                val s2 = fit.lastIndexOf(" ")
                var s5 = -1
                if (s2 == -1) {
                    nachalo = 1
                    konec = 1
                } else {
                    val s3 = fit.indexOf(".", s2 + 1)
                    if (s3 != -1) {
                        val s4 = fit.indexOf("-")
                        s5 = fit.indexOf(".", s3 + 1)
                        nachalo = fit.substring(s2 + 1, s3).toInt()
                        stixn = fit.substring(s3 + 1, s4).toInt()
                        if (s5 != -1) {
                            konec = fit.substring(s4 + 1, s5).toInt()
                            stixk = fit.substring(s5 + 1).toInt()
                        } else {
                            konec = nachalo
                            stixk = fit.substring(s4 + 1).toInt()
                        }
                    } else {
                        val s4 = fit.indexOf("-", s2 + 1)
                        if (s4 != -1) {
                            nachalo = fit.substring(s2 + 1, s4).toInt()
                            konec = fit.substring(s4 + 1).toInt()
                        } else {
                            nachalo = fit.substring(s2 + 1).toInt()
                            konec = nachalo
                        }
                    }
                }
                var inputStream = resources.openRawResource(R.raw.biblias1)
                var replace = false
                if (perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
                    inputStream = resources.openRawResource(R.raw.psaltyr_nadsan)
                }
                if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI) {
                    when (nomer) {
                        1 -> inputStream = resources.openRawResource(R.raw.biblias1)
                        2 -> inputStream = resources.openRawResource(R.raw.biblias2)
                        3 -> inputStream = resources.openRawResource(R.raw.biblias3)
                        4 -> inputStream = resources.openRawResource(R.raw.biblias4)
                        5 -> inputStream = resources.openRawResource(R.raw.biblias5)
                        6 -> inputStream = resources.openRawResource(R.raw.biblias6)
                        7 -> inputStream = resources.openRawResource(R.raw.biblias7)
                        8 -> inputStream = resources.openRawResource(R.raw.biblias8)
                        9 -> inputStream = resources.openRawResource(R.raw.biblias9)
                        10 -> inputStream = resources.openRawResource(R.raw.biblias10)
                        11 -> inputStream = resources.openRawResource(R.raw.biblias11)
                        12 -> inputStream = resources.openRawResource(R.raw.biblias12)
                        13 -> inputStream = resources.openRawResource(R.raw.biblias13)
                        14 -> inputStream = resources.openRawResource(R.raw.biblias14)
                        15 -> inputStream = resources.openRawResource(R.raw.biblias15)
                        16 -> inputStream = resources.openRawResource(R.raw.biblias16)
                        20 -> inputStream = resources.openRawResource(R.raw.biblias17)
                        21 -> inputStream = resources.openRawResource(R.raw.biblias18)
                        22 -> inputStream = resources.openRawResource(R.raw.biblias19)
                        23 -> inputStream = resources.openRawResource(R.raw.biblias20)
                        24 -> inputStream = resources.openRawResource(R.raw.biblias21)
                        25 -> inputStream = resources.openRawResource(R.raw.biblias22)
                        28 -> inputStream = resources.openRawResource(R.raw.biblias23)
                        29 -> inputStream = resources.openRawResource(R.raw.biblias24)
                        30 -> inputStream = resources.openRawResource(R.raw.biblias25)
                        33 -> inputStream = resources.openRawResource(R.raw.biblias26)
                        34 -> inputStream = resources.openRawResource(R.raw.biblias27)
                        35 -> inputStream = resources.openRawResource(R.raw.biblias28)
                        36 -> inputStream = resources.openRawResource(R.raw.biblias29)
                        37 -> inputStream = resources.openRawResource(R.raw.biblias30)
                        38 -> inputStream = resources.openRawResource(R.raw.biblias31)
                        39 -> inputStream = resources.openRawResource(R.raw.biblias32)
                        40 -> inputStream = resources.openRawResource(R.raw.biblias33)
                        41 -> inputStream = resources.openRawResource(R.raw.biblias34)
                        42 -> inputStream = resources.openRawResource(R.raw.biblias35)
                        43 -> inputStream = resources.openRawResource(R.raw.biblias36)
                        44 -> inputStream = resources.openRawResource(R.raw.biblias37)
                        45 -> inputStream = resources.openRawResource(R.raw.biblias38)
                        46 -> inputStream = resources.openRawResource(R.raw.biblias39)
                        51 -> inputStream = resources.openRawResource(R.raw.biblian1)
                        52 -> inputStream = resources.openRawResource(R.raw.biblian2)
                        53 -> inputStream = resources.openRawResource(R.raw.biblian3)
                        54 -> inputStream = resources.openRawResource(R.raw.biblian4)
                        55 -> inputStream = resources.openRawResource(R.raw.biblian5)
                        56 -> inputStream = resources.openRawResource(R.raw.biblian6)
                        57 -> inputStream = resources.openRawResource(R.raw.biblian7)
                        58 -> inputStream = resources.openRawResource(R.raw.biblian8)
                        59 -> inputStream = resources.openRawResource(R.raw.biblian9)
                        60 -> inputStream = resources.openRawResource(R.raw.biblian10)
                        61 -> inputStream = resources.openRawResource(R.raw.biblian11)
                        62 -> inputStream = resources.openRawResource(R.raw.biblian12)
                        63 -> inputStream = resources.openRawResource(R.raw.biblian13)
                        64 -> inputStream = resources.openRawResource(R.raw.biblian14)
                        65 -> inputStream = resources.openRawResource(R.raw.biblian15)
                        66 -> inputStream = resources.openRawResource(R.raw.biblian16)
                        67 -> inputStream = resources.openRawResource(R.raw.biblian17)
                        68 -> inputStream = resources.openRawResource(R.raw.biblian18)
                        69 -> inputStream = resources.openRawResource(R.raw.biblian19)
                        70 -> inputStream = resources.openRawResource(R.raw.biblian20)
                        71 -> inputStream = resources.openRawResource(R.raw.biblian21)
                        72 -> inputStream = resources.openRawResource(R.raw.biblian22)
                        73 -> inputStream = resources.openRawResource(R.raw.biblian23)
                        74 -> inputStream = resources.openRawResource(R.raw.biblian24)
                        75 -> inputStream = resources.openRawResource(R.raw.biblian25)
                        76 -> inputStream = resources.openRawResource(R.raw.biblian26)
                        77 -> inputStream = resources.openRawResource(R.raw.biblian27)
                        else -> {
                            inputStream = getSinoidalResource(nomer)
                            replace = true
                        }
                    }
                }
                if (perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
                    when (nomer) {
                        1 -> inputStream = resources.openRawResource(R.raw.bokunas1)
                        2 -> inputStream = resources.openRawResource(R.raw.bokunas2)
                        3 -> inputStream = resources.openRawResource(R.raw.bokunas3)
                        4 -> inputStream = resources.openRawResource(R.raw.bokunas4)
                        5 -> inputStream = resources.openRawResource(R.raw.bokunas5)
                        6 -> inputStream = resources.openRawResource(R.raw.bokunas6)
                        7 -> inputStream = resources.openRawResource(R.raw.bokunas7)
                        8 -> inputStream = resources.openRawResource(R.raw.bokunas8)
                        9 -> inputStream = resources.openRawResource(R.raw.bokunas9)
                        10 -> inputStream = resources.openRawResource(R.raw.bokunas10)
                        11 -> inputStream = resources.openRawResource(R.raw.bokunas11)
                        12 -> inputStream = resources.openRawResource(R.raw.bokunas12)
                        13 -> inputStream = resources.openRawResource(R.raw.bokunas13)
                        14 -> inputStream = resources.openRawResource(R.raw.bokunas14)
                        15 -> inputStream = resources.openRawResource(R.raw.bokunas15)
                        16 -> inputStream = resources.openRawResource(R.raw.bokunas16)
                        20 -> inputStream = resources.openRawResource(R.raw.bokunas17)
                        21 -> inputStream = resources.openRawResource(R.raw.bokunas18)
                        22 -> inputStream = resources.openRawResource(R.raw.bokunas19)
                        23 -> inputStream = resources.openRawResource(R.raw.bokunas20)
                        24 -> inputStream = resources.openRawResource(R.raw.bokunas21)
                        25 -> inputStream = resources.openRawResource(R.raw.bokunas22)
                        28 -> inputStream = resources.openRawResource(R.raw.bokunas23)
                        29 -> inputStream = resources.openRawResource(R.raw.bokunas24)
                        30 -> inputStream = resources.openRawResource(R.raw.bokunas25)
                        33 -> inputStream = resources.openRawResource(R.raw.bokunas26)
                        34 -> inputStream = resources.openRawResource(R.raw.bokunas27)
                        35 -> inputStream = resources.openRawResource(R.raw.bokunas28)
                        36 -> inputStream = resources.openRawResource(R.raw.bokunas29)
                        37 -> inputStream = resources.openRawResource(R.raw.bokunas30)
                        38 -> inputStream = resources.openRawResource(R.raw.bokunas31)
                        39 -> inputStream = resources.openRawResource(R.raw.bokunas32)
                        40 -> inputStream = resources.openRawResource(R.raw.bokunas33)
                        41 -> inputStream = resources.openRawResource(R.raw.bokunas34)
                        42 -> inputStream = resources.openRawResource(R.raw.bokunas35)
                        43 -> inputStream = resources.openRawResource(R.raw.bokunas36)
                        44 -> inputStream = resources.openRawResource(R.raw.bokunas37)
                        45 -> inputStream = resources.openRawResource(R.raw.bokunas38)
                        46 -> inputStream = resources.openRawResource(R.raw.bokunas39)
                        51 -> inputStream = resources.openRawResource(R.raw.bokunan1)
                        52 -> inputStream = resources.openRawResource(R.raw.bokunan2)
                        53 -> inputStream = resources.openRawResource(R.raw.bokunan3)
                        54 -> inputStream = resources.openRawResource(R.raw.bokunan4)
                        55 -> inputStream = resources.openRawResource(R.raw.bokunan5)
                        56 -> inputStream = resources.openRawResource(R.raw.bokunan6)
                        57 -> inputStream = resources.openRawResource(R.raw.bokunan7)
                        58 -> inputStream = resources.openRawResource(R.raw.bokunan8)
                        59 -> inputStream = resources.openRawResource(R.raw.bokunan9)
                        60 -> inputStream = resources.openRawResource(R.raw.bokunan10)
                        61 -> inputStream = resources.openRawResource(R.raw.bokunan11)
                        62 -> inputStream = resources.openRawResource(R.raw.bokunan12)
                        63 -> inputStream = resources.openRawResource(R.raw.bokunan13)
                        64 -> inputStream = resources.openRawResource(R.raw.bokunan14)
                        65 -> inputStream = resources.openRawResource(R.raw.bokunan15)
                        66 -> inputStream = resources.openRawResource(R.raw.bokunan16)
                        67 -> inputStream = resources.openRawResource(R.raw.bokunan17)
                        68 -> inputStream = resources.openRawResource(R.raw.bokunan18)
                        69 -> inputStream = resources.openRawResource(R.raw.bokunan19)
                        70 -> inputStream = resources.openRawResource(R.raw.bokunan20)
                        71 -> inputStream = resources.openRawResource(R.raw.bokunan21)
                        72 -> inputStream = resources.openRawResource(R.raw.bokunan22)
                        73 -> inputStream = resources.openRawResource(R.raw.bokunan23)
                        74 -> inputStream = resources.openRawResource(R.raw.bokunan24)
                        75 -> inputStream = resources.openRawResource(R.raw.bokunan25)
                        76 -> inputStream = resources.openRawResource(R.raw.bokunan26)
                        77 -> inputStream = resources.openRawResource(R.raw.bokunan27)
                        else -> {
                            inputStream = getSinoidalResource(nomer)
                            replace = true
                        }
                    }
                }
                if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                    when (nomer) {
                        1 -> inputStream = resources.openRawResource(R.raw.carniauskis1)
                        2 -> inputStream = resources.openRawResource(R.raw.carniauskis2)
                        3 -> inputStream = resources.openRawResource(R.raw.carniauskis3)
                        4 -> inputStream = resources.openRawResource(R.raw.carniauskis4)
                        5 -> inputStream = resources.openRawResource(R.raw.carniauskis5)
                        6 -> inputStream = resources.openRawResource(R.raw.carniauskis6)
                        7 -> inputStream = resources.openRawResource(R.raw.carniauskis7)
                        8 -> inputStream = resources.openRawResource(R.raw.carniauskis8)
                        9 -> inputStream = resources.openRawResource(R.raw.carniauskis9)
                        10 -> inputStream = resources.openRawResource(R.raw.carniauskis10)
                        11 -> inputStream = resources.openRawResource(R.raw.carniauskis11)
                        12 -> inputStream = resources.openRawResource(R.raw.carniauskis12)
                        13 -> inputStream = resources.openRawResource(R.raw.carniauskis13)
                        14 -> inputStream = resources.openRawResource(R.raw.carniauskis14)
                        15 -> inputStream = resources.openRawResource(R.raw.carniauskis15)
                        16 -> inputStream = resources.openRawResource(R.raw.carniauskis16)
                        20 -> inputStream = resources.openRawResource(R.raw.carniauskis17)
                        21 -> inputStream = resources.openRawResource(R.raw.carniauskis18)
                        22 -> inputStream = resources.openRawResource(R.raw.carniauskis19)
                        23 -> inputStream = resources.openRawResource(R.raw.carniauskis20)
                        24 -> inputStream = resources.openRawResource(R.raw.carniauskis21)
                        25 -> inputStream = resources.openRawResource(R.raw.carniauskis22)
                        28 -> inputStream = resources.openRawResource(R.raw.carniauskis23)
                        29 -> inputStream = resources.openRawResource(R.raw.carniauskis24)
                        30 -> inputStream = resources.openRawResource(R.raw.carniauskis25)
                        33 -> inputStream = resources.openRawResource(R.raw.carniauskis26)
                        34 -> inputStream = resources.openRawResource(R.raw.carniauskis27)
                        35 -> inputStream = resources.openRawResource(R.raw.carniauskis28)
                        36 -> inputStream = resources.openRawResource(R.raw.carniauskis29)
                        37 -> inputStream = resources.openRawResource(R.raw.carniauskis30)
                        38 -> inputStream = resources.openRawResource(R.raw.carniauskis31)
                        39 -> inputStream = resources.openRawResource(R.raw.carniauskis32)
                        40 -> inputStream = resources.openRawResource(R.raw.carniauskis33)
                        41 -> inputStream = resources.openRawResource(R.raw.carniauskis34)
                        42 -> inputStream = resources.openRawResource(R.raw.carniauskis35)
                        43 -> inputStream = resources.openRawResource(R.raw.carniauskis36)
                        44 -> inputStream = resources.openRawResource(R.raw.carniauskis37)
                        45 -> inputStream = resources.openRawResource(R.raw.carniauskis38)
                        46 -> inputStream = resources.openRawResource(R.raw.carniauskis39)
                        18 -> inputStream = resources.openRawResource(R.raw.carniauskis40)
                        19 -> inputStream = resources.openRawResource(R.raw.carniauskis41)
                        26 -> inputStream = resources.openRawResource(R.raw.carniauskis42)
                        27 -> inputStream = resources.openRawResource(R.raw.carniauskis43)
                        32 -> inputStream = resources.openRawResource(R.raw.carniauskis44)
                        47 -> inputStream = resources.openRawResource(R.raw.carniauskis45)
                        48 -> inputStream = resources.openRawResource(R.raw.carniauskis46)
                        51 -> inputStream = resources.openRawResource(R.raw.carniauskin1)
                        52 -> inputStream = resources.openRawResource(R.raw.carniauskin2)
                        53 -> inputStream = resources.openRawResource(R.raw.carniauskin3)
                        54 -> inputStream = resources.openRawResource(R.raw.carniauskin4)
                        55 -> inputStream = resources.openRawResource(R.raw.carniauskin5)
                        56 -> inputStream = resources.openRawResource(R.raw.carniauskin6)
                        57 -> inputStream = resources.openRawResource(R.raw.carniauskin7)
                        58 -> inputStream = resources.openRawResource(R.raw.carniauskin8)
                        59 -> inputStream = resources.openRawResource(R.raw.carniauskin9)
                        60 -> inputStream = resources.openRawResource(R.raw.carniauskin10)
                        61 -> inputStream = resources.openRawResource(R.raw.carniauskin11)
                        62 -> inputStream = resources.openRawResource(R.raw.carniauskin12)
                        63 -> inputStream = resources.openRawResource(R.raw.carniauskin13)
                        64 -> inputStream = resources.openRawResource(R.raw.carniauskin14)
                        65 -> inputStream = resources.openRawResource(R.raw.carniauskin15)
                        66 -> inputStream = resources.openRawResource(R.raw.carniauskin16)
                        67 -> inputStream = resources.openRawResource(R.raw.carniauskin17)
                        68 -> inputStream = resources.openRawResource(R.raw.carniauskin18)
                        69 -> inputStream = resources.openRawResource(R.raw.carniauskin19)
                        70 -> inputStream = resources.openRawResource(R.raw.carniauskin20)
                        71 -> inputStream = resources.openRawResource(R.raw.carniauskin21)
                        72 -> inputStream = resources.openRawResource(R.raw.carniauskin22)
                        73 -> inputStream = resources.openRawResource(R.raw.carniauskin23)
                        74 -> inputStream = resources.openRawResource(R.raw.carniauskin24)
                        75 -> inputStream = resources.openRawResource(R.raw.carniauskin25)
                        76 -> inputStream = resources.openRawResource(R.raw.carniauskin26)
                        77 -> inputStream = resources.openRawResource(R.raw.carniauskin27)
                        else -> {
                            inputStream = getSinoidalResource(nomer)
                            replace = true
                        }
                    }
                }
                if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
                    inputStream = getSinoidalResource(nomer)
                }
                val file = if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL || replace) {
                    if (nomer > 50) {
                        File("$filesDir/BibliaSinodalNovyZavet/${getNumarKnigi(nomer)}.json")
                    } else {
                        File("$filesDir/BibliaSinodalStaryZavet/${getNumarKnigi(nomer)}.json")
                    }
                } else if (perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
                    if (nomer > 50) {
                        File("$filesDir/BibliaBokunaNovyZavet/${getNumarKnigi(nomer)}.json")
                    } else {
                        File("$filesDir/BibliaBokunaStaryZavet/${getNumarKnigi(nomer)}.json")
                    }
                } else if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                    if (nomer > 50) {
                        File("$filesDir/BibliaCarniauskiNovyZavet/${getNumarKnigi(nomer)}.json")
                    } else {
                        File("$filesDir/BibliaCarniauskiStaryZavet/${getNumarKnigi(nomer)}.json")
                    }
                } else {
                    if (nomer > 50) {
                        File("$filesDir/BibliaSemuxaNovyZavet/${getNumarKnigi(nomer)}.json")
                    } else {
                        File("$filesDir/BibliaSemuxaStaryZavet/${getNumarKnigi(nomer)}.json")
                    }
                }
                BibleGlobalList.vydelenie.clear()
                BibleGlobalList.vydelenie.addAll(readFileGson(file))
                var bold: Int
                var underline: Int
                var color: Int
                if (replace) {
                    val title = when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> getString(by.carkva_gazeta.malitounik.R.string.biblia_bokun)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_charniauski)
                        else -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                    }
                    maranAta.add(MaranAtaData(perevod, false, -1, 0, 0, "", saveName, "<br><em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error, title) + "</em>", 0, 0, 0))
                }
                val builder = StringBuilder()
                var line: String
                val isr = InputStreamReader(inputStream)
                BufferedReader(isr).use {
                    it.forEachLine { string ->
                        line = string
                        line = line.replace("\\n", "<br>")
                        if (line.contains("//")) {
                            val t1 = line.indexOf("//")
                            line = line.substring(0, t1).trim()
                            if (line != "") builder.append(line).append("\n")
                        } else {
                            builder.append(line).append("\n")
                        }
                    }
                }
                if (chten.size == 6 && i == 3) {
                    val endFabreary = if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_ru)
                    else resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_be)
                    maranAta.add(MaranAtaData(perevod, false, -1, 0, 0, "", saveName, "<br><em>$endFabreary</em><br>\n", 0, 0, 0))
                }
                val split2Pre = builder.toString().split("===")
                val split2 = ArrayList<String>()
                split2.addAll(split2Pre)
                var addGlava = -1
                if (konec >= split2.size) {
                    addGlava = split2.size
                    for (g in split2.size..konec) {
                        split2.add(getSinoidalGlavas(nomer, g))
                    }
                }
                var vN: Int
                var vK: Int
                val r1 = StringBuilder()
                var r2 = ""
                for (e in nachalo..konec) {
                    if (stixn != -1) {
                        if (s5 != -1) {
                            if (e == nachalo) {
                                vN = split2[e].indexOf("$stixn ")
                                r1.append(split2[e].substring(vN).trim())
                            }
                            if (e != nachalo && e != konec) {
                                r1.append("\n").append("#$e#").append(split2[e].trim())
                            }
                            if (e == konec) {
                                val vK1 = split2[e].indexOf("$stixk ")
                                vK = split2[e].indexOf("\n", vK1)
                                r2 = split2[e].substring(0, vK)
                            }
                        } else {
                            vN = split2[e].indexOf("$stixn ")
                            val vK1 = split2[e].indexOf("$stixk ")
                            vK = split2[e].indexOf("\n", vK1)
                            r1.append(split2[e].substring(vN, vK))
                        }
                    } else {
                        val title = when (perevod) {
                            DialogVybranoeBibleList.PEREVODSEMUXI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                            DialogVybranoeBibleList.PEREVODBOKUNA -> getString(by.carkva_gazeta.malitounik.R.string.biblia_bokun)
                            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_charniauski)
                            else -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                        }
                        if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) {
                            if (addGlava == e) maranAta.add(MaranAtaData(perevod, nomer > 50, -1, 0, 0, "", saveName, "<br><em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error_glava, title) + "</em>", 0, 0, 0))
                        }
                        val p = if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL || replace) DialogVybranoeBibleList.PEREVODSINOIDAL
                        else perevod
                        saveName = getBibleNameFull(nomer, p) + " $e"
                        maranAta.add(MaranAtaData(perevod, nomer > 50, -1, 0, 0, "", saveName, "<br><strong>$saveName</strong><br>\n", 0, 0, 0))
                        val splitline = split2[e].trim().split("\n")
                        for (i2 in splitline.indices) {
                            val pos = BibleGlobalList.checkPosition(e - 1, i2)
                            if (pos != -1) {
                                color = BibleGlobalList.vydelenie[pos][2]
                                underline = BibleGlobalList.vydelenie[pos][3]
                                bold = BibleGlobalList.vydelenie[pos][4]
                            } else {
                                color = 0
                                underline = 0
                                bold = 0
                            }
                            maranAta.add(MaranAtaData(p, nomer > 50, nomer, e - 1, i2 + 1, kniga + "." + e + "." + (i2 + 1), saveName, splitline[i2], bold, underline, color))
                        }
                    }
                }
                if (stixn != -1) {
                    val t1 = fit.indexOf(".")
                    var glava = fit.substring(s2 + 1, t1).toInt()
                    val p = if (perevod == DialogVybranoeBibleList.PEREVODSINOIDAL || replace) DialogVybranoeBibleList.PEREVODSINOIDAL
                    else perevod
                    saveName = getBibleNameFull(nomer, p) + " $glava"
                    maranAta.add(MaranAtaData(DialogVybranoeBibleList.PEREVODSEMUXI, nomer > 50, -1, 0, 0, "", saveName, "<br><strong>$saveName</strong><br>\n", 0, 0, 0))
                    val res1 = r1.toString().trim().split("\n")
                    var i3 = stixn
                    var ires1 = 1
                    for (i2 in res1.indices) {
                        val tr1 = res1[i2].indexOf(" ")
                        if (tr1 != -1) {
                            var str1 = res1[i2].substring(0, tr1)
                            val tr2 = str1.indexOf(".")
                            if (tr2 != -1) {
                                str1 = res1[i2].substring(0, tr2)
                                if (ires1 > str1.toInt()) {
                                    glava++
                                    i3 = 1
                                }
                                ires1 = str1.toInt()
                            }
                        }
                        var resbib = res1[i2]
                        if (resbib.contains("#")) {
                            val t2 = resbib.indexOf("#")
                            val t3 = resbib.indexOf("#", t2 + 1)
                            glava = resbib.substring(t2 + 1, t3).toInt()
                            resbib = resbib.substring(t3 + 1)
                        }
                        val pos = BibleGlobalList.checkPosition(glava - 1, i3 - 1)
                        if (pos != -1) {
                            color = BibleGlobalList.vydelenie[pos][2]
                            underline = BibleGlobalList.vydelenie[pos][3]
                            bold = BibleGlobalList.vydelenie[pos][4]
                        } else {
                            color = 0
                            underline = 0
                            bold = 0
                        }
                        saveName = getBibleNameFull(nomer, p) + " $glava"
                        maranAta.add(MaranAtaData(p, nomer > 50, nomer, glava - 1, i3, "$kniga.$glava.$i3", saveName, resbib, bold, underline, color))
                        i3++
                    }
                    if (konec - nachalo != 0) {
                        val res2 = r2.trim().split("\n")
                        for (i21 in res2.indices) {
                            val pos = BibleGlobalList.checkPosition(konec - 1, i21)
                            if (pos != -1) {
                                color = BibleGlobalList.vydelenie[pos][2]
                                underline = BibleGlobalList.vydelenie[pos][3]
                                bold = BibleGlobalList.vydelenie[pos][4]
                            } else {
                                color = 0
                                underline = 0
                                bold = 0
                            }
                            saveName = getBibleNameFull(nomer, p) + " $konec"
                            maranAta.add(MaranAtaData(p, nomer > 50, nomer, konec - 1, i21 + 1, kniga + "." + konec + "." + (i21 + 1), saveName, res2[i21], bold, underline, color))
                        }
                    }
                }
            } catch (_: Throwable) {
                val t1 = fit.lastIndexOf(" ")
                val title = getBibleNameFull(nomer, perevod) + " ${fit.substring(t1 + 1)}"
                maranAta.add(MaranAtaData(perevod = DialogVybranoeBibleList.PEREVODSEMUXI, novyZapavet = false, -1, 0, 0, "", title, "<br><strong>$title</strong><br>\n", 0, 0, 0))
                maranAta.add(MaranAtaData(perevod = DialogVybranoeBibleList.PEREVODSEMUXI, novyZapavet = false, -1, 0, 0, "", title, resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch) + "\n", 0, 0, 0))
            }
        }
        adapter.notifyDataSetChanged()
        isSmoothScrollToPosition = true
        if (vybranae && !prodoljyt && savedInstanceState == null) {
            smoothScrollToPosition(findTitle())
        } else {
            smoothScrollToPosition(maranAtaScrollPosition)
        }
    }

    private fun findTitle(): Int {
        for (i in 0 until adapter.count) {
            if (title == adapter.getItem(i)?.title) {
                return i
            }
        }
        return 0
    }

    private fun getNumarKnigi(nomer: Int): Int {
        var result = nomer
        when (nomer) {
            20 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 17
            21 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 18
            22 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 19
            23 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 20
            24 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 21
            25 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 22
            28 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 23
            29 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 24
            30 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 25
            33 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 26
            34 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 27
            35 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 28
            36 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 29
            37 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 30
            38 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 31
            39 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 32
            40 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 33
            41 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 34
            42 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 35
            43 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 36
            44 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 37
            45 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 38
            46 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) result = 39
            18 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 40
            19 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 41
            26 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 42
            27 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 43
            32 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 44
            47 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 45
            48 -> if (perevod != DialogVybranoeBibleList.PEREVODSINOIDAL && perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) result = 46
            51 -> result = 1
            52 -> result = 2
            53 -> result = 3
            54 -> result = 4
            55 -> result = 5
            56 -> result = 6
            57 -> result = 7
            58 -> result = 8
            59 -> result = 9
            60 -> result = 10
            61 -> result = 11
            62 -> result = 12
            63 -> result = 13
            64 -> result = 14
            65 -> result = 15
            66 -> result = 16
            67 -> result = 17
            68 -> result = 18
            69 -> result = 19
            70 -> result = 20
            71 -> result = 21
            72 -> result = 22
            73 -> result = 23
            74 -> result = 24
            75 -> result = 25
            76 -> result = 26
            77 -> result = 27
        }
        result -= 1
        return result
    }

    private fun getNumarKnigiBelarusPerevoda(kniga: Int): Int {
        var result = kniga
        when (kniga) {
            17 -> result = 20
            18 -> result = 21
            19 -> result = 22
            20 -> result = 23
            21 -> result = 24
            22 -> result = 25
            23 -> result = 28
            24 -> result = 29
            25 -> result = 30
            26 -> result = 33
            27 -> result = 34
            28 -> result = 35
            29 -> result = 36
            30 -> result = 37
            31 -> result = 38
            32 -> result = 39
            33 -> result = 40
            34 -> result = 41
            35 -> result = 42
            36 -> result = 43
            37 -> result = 44
            38 -> result = 45
            39 -> result = 46
            40 -> result = 18
            41 -> result = 19
            42 -> result = 26
            43 -> result = 27
            44 -> result = 32
            45 -> result = 47
            46 -> result = 48
            51 -> result = 1
            52 -> result = 2
            53 -> result = 3
            54 -> result = 4
            55 -> result = 5
            56 -> result = 6
            57 -> result = 7
            58 -> result = 8
            59 -> result = 9
            60 -> result = 10
            61 -> result = 11
            62 -> result = 12
            63 -> result = 13
            64 -> result = 14
            65 -> result = 15
            66 -> result = 16
            67 -> result = 17
            68 -> result = 18
            69 -> result = 19
            70 -> result = 20
            71 -> result = 21
            72 -> result = 22
            73 -> result = 23
            74 -> result = 24
            75 -> result = 25
            76 -> result = 26
            77 -> result = 27
        }
        return result
    }

    private fun readFileGson(file: File): ArrayList<ArrayList<Int>> {
        val result = ArrayList<ArrayList<Int>>()
        if (file.exists()) {
            val inputStream2 = FileReader(file)
            val reader = BufferedReader(inputStream2)
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
            result.addAll(gson.fromJson(reader.readText(), type))
            inputStream2.close()
        }
        return result
    }

    private fun getSinoidalGlavas(nomer: Int, konec: Int): String {
        val inputStream = getSinoidalResource(nomer)
        val builder = StringBuilder()
        val isr = InputStreamReader(inputStream)
        BufferedReader(isr).use {
            it.forEachLine { string ->
                builder.append(string).append("\n")
            }
        }
        return builder.toString().split("===")[konec]
    }

    private fun getSinoidalResource(nomer: Int): InputStream {
        var inputStream = resources.openRawResource(R.raw.biblias1)
        when (nomer) {
            1 -> inputStream = resources.openRawResource(R.raw.sinaidals1)
            2 -> inputStream = resources.openRawResource(R.raw.sinaidals2)
            3 -> inputStream = resources.openRawResource(R.raw.sinaidals3)
            4 -> inputStream = resources.openRawResource(R.raw.sinaidals4)
            5 -> inputStream = resources.openRawResource(R.raw.sinaidals5)
            6 -> inputStream = resources.openRawResource(R.raw.sinaidals6)
            7 -> inputStream = resources.openRawResource(R.raw.sinaidals7)
            8 -> inputStream = resources.openRawResource(R.raw.sinaidals8)
            9 -> inputStream = resources.openRawResource(R.raw.sinaidals9)
            10 -> inputStream = resources.openRawResource(R.raw.sinaidals10)
            11 -> inputStream = resources.openRawResource(R.raw.sinaidals11)
            12 -> inputStream = resources.openRawResource(R.raw.sinaidals12)
            13 -> inputStream = resources.openRawResource(R.raw.sinaidals13)
            14 -> inputStream = resources.openRawResource(R.raw.sinaidals14)
            15 -> inputStream = resources.openRawResource(R.raw.sinaidals15)
            16 -> inputStream = resources.openRawResource(R.raw.sinaidals16)
            17 -> inputStream = resources.openRawResource(R.raw.sinaidals17)
            18 -> inputStream = resources.openRawResource(R.raw.sinaidals18)
            19 -> inputStream = resources.openRawResource(R.raw.sinaidals19)
            20 -> inputStream = resources.openRawResource(R.raw.sinaidals20)
            21 -> inputStream = resources.openRawResource(R.raw.sinaidals21)
            22 -> inputStream = resources.openRawResource(R.raw.sinaidals22)
            23 -> inputStream = resources.openRawResource(R.raw.sinaidals23)
            24 -> inputStream = resources.openRawResource(R.raw.sinaidals24)
            25 -> inputStream = resources.openRawResource(R.raw.sinaidals25)
            26 -> inputStream = resources.openRawResource(R.raw.sinaidals26)
            27 -> inputStream = resources.openRawResource(R.raw.sinaidals27)
            28 -> inputStream = resources.openRawResource(R.raw.sinaidals28)
            29 -> inputStream = resources.openRawResource(R.raw.sinaidals29)
            30 -> inputStream = resources.openRawResource(R.raw.sinaidals30)
            31 -> inputStream = resources.openRawResource(R.raw.sinaidals31)
            32 -> inputStream = resources.openRawResource(R.raw.sinaidals32)
            33 -> inputStream = resources.openRawResource(R.raw.sinaidals33)
            34 -> inputStream = resources.openRawResource(R.raw.sinaidals34)
            35 -> inputStream = resources.openRawResource(R.raw.sinaidals35)
            36 -> inputStream = resources.openRawResource(R.raw.sinaidals36)
            37 -> inputStream = resources.openRawResource(R.raw.sinaidals37)
            38 -> inputStream = resources.openRawResource(R.raw.sinaidals38)
            39 -> inputStream = resources.openRawResource(R.raw.sinaidals39)
            40 -> inputStream = resources.openRawResource(R.raw.sinaidals40)
            41 -> inputStream = resources.openRawResource(R.raw.sinaidals41)
            42 -> inputStream = resources.openRawResource(R.raw.sinaidals42)
            43 -> inputStream = resources.openRawResource(R.raw.sinaidals43)
            44 -> inputStream = resources.openRawResource(R.raw.sinaidals44)
            45 -> inputStream = resources.openRawResource(R.raw.sinaidals45)
            46 -> inputStream = resources.openRawResource(R.raw.sinaidals46)
            47 -> inputStream = resources.openRawResource(R.raw.sinaidals47)
            48 -> inputStream = resources.openRawResource(R.raw.sinaidals48)
            49 -> inputStream = resources.openRawResource(R.raw.sinaidals49)
            50 -> inputStream = resources.openRawResource(R.raw.sinaidals50)
            51 -> inputStream = resources.openRawResource(R.raw.sinaidaln1)
            52 -> inputStream = resources.openRawResource(R.raw.sinaidaln2)
            53 -> inputStream = resources.openRawResource(R.raw.sinaidaln3)
            54 -> inputStream = resources.openRawResource(R.raw.sinaidaln4)
            55 -> inputStream = resources.openRawResource(R.raw.sinaidaln5)
            56 -> inputStream = resources.openRawResource(R.raw.sinaidaln6)
            57 -> inputStream = resources.openRawResource(R.raw.sinaidaln7)
            58 -> inputStream = resources.openRawResource(R.raw.sinaidaln8)
            59 -> inputStream = resources.openRawResource(R.raw.sinaidaln9)
            60 -> inputStream = resources.openRawResource(R.raw.sinaidaln10)
            61 -> inputStream = resources.openRawResource(R.raw.sinaidaln11)
            62 -> inputStream = resources.openRawResource(R.raw.sinaidaln12)
            63 -> inputStream = resources.openRawResource(R.raw.sinaidaln13)
            64 -> inputStream = resources.openRawResource(R.raw.sinaidaln14)
            65 -> inputStream = resources.openRawResource(R.raw.sinaidaln15)
            66 -> inputStream = resources.openRawResource(R.raw.sinaidaln16)
            67 -> inputStream = resources.openRawResource(R.raw.sinaidaln17)
            68 -> inputStream = resources.openRawResource(R.raw.sinaidaln18)
            69 -> inputStream = resources.openRawResource(R.raw.sinaidaln19)
            70 -> inputStream = resources.openRawResource(R.raw.sinaidaln20)
            71 -> inputStream = resources.openRawResource(R.raw.sinaidaln21)
            72 -> inputStream = resources.openRawResource(R.raw.sinaidaln22)
            73 -> inputStream = resources.openRawResource(R.raw.sinaidaln23)
            74 -> inputStream = resources.openRawResource(R.raw.sinaidaln24)
            75 -> inputStream = resources.openRawResource(R.raw.sinaidaln25)
            76 -> inputStream = resources.openRawResource(R.raw.sinaidaln26)
            77 -> inputStream = resources.openRawResource(R.raw.sinaidaln27)
        }
        return inputStream
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
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
        if (diffScroll != 0) {
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
            resetScreenJob?.cancel()
            stopAutoStartScroll()
            autoScroll()
        } else {
            isSmoothScrollToPosition = true
            smoothScrollToPosition(0)
            scrolltosatrt = true
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            autoscroll = true
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            invalidateOptionsMenu()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        binding.ListView.scrollListBy(2)
                    }
                }
            }
        }
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

    private fun startProcent(progressAction: Int) {
        if (progressAction == MainActivity.PROGRESSACTIONBRIGHESS) {
            procentJobBrightness?.cancel()
            bindingprogress.progressBrighess.visibility = View.VISIBLE
            procentJobBrightness = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressBrighess.visibility = View.GONE
                delay(3000)
                if (bindingprogress.seekBarBrighess.visibility == View.VISIBLE) {
                    bindingprogress.seekBarBrighess.animation = AnimationUtils.loadAnimation(this@MaranAta, by.carkva_gazeta.malitounik.R.anim.slide_out_left)
                    bindingprogress.seekBarBrighess.visibility = View.GONE
                }
            }
        }
        if (progressAction == MainActivity.PROGRESSACTIONFONT) {
            procentJobFont?.cancel()
            bindingprogress.progressFont.visibility = View.VISIBLE
            procentJobFont = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressFont.visibility = View.GONE
                delay(3000)
                if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                    bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@MaranAta, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
                    bindingprogress.seekBarFontSize.visibility = View.GONE
                }
            }
        }
        if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT || progressAction == MainActivity.PROGRESSACTIONAUTORIGHT) {
            procentJobAuto?.cancel()
            bindingprogress.progressAuto.visibility = View.VISIBLE
            if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT) {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@MaranAta, by.carkva_gazeta.malitounik.R.drawable.selector_progress_auto_left)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@MaranAta, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            } else {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@MaranAta, by.carkva_gazeta.malitounik.R.drawable.selector_progress_red)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@MaranAta, by.carkva_gazeta.malitounik.R.color.colorWhite))
            }
            procentJobAuto = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressAuto.visibility = View.GONE
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(by.carkva_gazeta.malitounik.R.menu.chtenia, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onBack() {
        when {
            paralel -> {
                binding.scroll.visibility = View.GONE
                binding.ListView.visibility = View.VISIBLE
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
                checkDay()
                paralel = false
                invalidateOptionsMenu()
            }

            BibleGlobalList.mPedakVisable -> {
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
                if (binding.linearLayout4.visibility == View.VISIBLE) {
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                }
                invalidateOptionsMenu()
            }

            else -> super.onBack()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        saveVydelenieNatatkiZakladki()
        maranAtaScrollPosition = binding.ListView.firstVisiblePosition
        val prefEditors = k.edit()
        if (vybranae) prefEditors.putInt(perevod + "BibleVybranoeScroll", maranAtaScrollPosition)
        else prefEditors.putInt("maranAtaScrollPasition", maranAtaScrollPosition)
        prefEditors.apply()
        stopAutoStartScroll()
    }

    private fun saveVydelenieNatatkiZakladki() {
        clearEmptyPosition()
        val listBible = ArrayList<ArrayList<Int>>()
        val maranataSave = ArrayList<MaranAtaSave>()
        var kniga = -1
        maranAta.forEach { maranata ->
            if (maranata.kniga != -1) {
                if (maranata.color != 0 || maranata.underline != 0 || maranata.bold != 0) {
                    val setVydelenie = ArrayList<Int>()
                    setVydelenie.add(maranata.glava)
                    setVydelenie.add(maranata.styx - 1)
                    setVydelenie.add(maranata.color)
                    setVydelenie.add(maranata.underline)
                    setVydelenie.add(maranata.bold)
                    listBible.add(setVydelenie)
                    if (kniga != getNumarKnigi(maranata.kniga)) {
                        maranataSave.add(MaranAtaSave(maranata.perevod, maranata.novyZapavet, maranata.kniga))
                    }
                    kniga = getNumarKnigi(maranata.kniga)
                } else {
                    val file = when (maranata.perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaSemuxaNovyZavet/${getNumarKnigi(maranata.kniga)}.json")
                            } else {
                                File("$filesDir/BibliaSemuxaStaryZavet/${getNumarKnigi(maranata.kniga)}.json")
                            }
                        }
                        DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaSinodalNovyZavet/${getNumarKnigi(maranata.kniga)}.json")
                            } else {
                                File("$filesDir/BibliaSinodalStaryZavet/${getNumarKnigi(maranata.kniga)}.json")
                            }
                        }
                        DialogVybranoeBibleList.PEREVODBOKUNA -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaBokunaNovyZavet/${getNumarKnigi(maranata.kniga)}.json")
                            } else {
                                File("$filesDir/BibliaBokunaStaryZavet/${getNumarKnigi(maranata.kniga)}.json")
                            }
                        }
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaCarniauskiNovyZavet/${getNumarKnigi(maranata.kniga)}.json")
                            } else {
                                File("$filesDir/BibliaCarniauskiStaryZavet/${getNumarKnigi(maranata.kniga)}.json")
                            }
                        }
                        else -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaSemuxaNovyZavet/${getNumarKnigi(maranata.kniga)}.json")
                            } else {
                                File("$filesDir/BibliaSemuxaStaryZavet/${getNumarKnigi(maranata.kniga)}.json")
                            }
                        }
                    }
                    if (file.exists()) file.delete()
                }
            }
        }
        maranataSave.forEach { maranata ->
            when (maranata.perevod) {
                DialogVybranoeBibleList.PEREVODSEMUXI -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaSemuxaNovyZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaSemuxaStaryZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    }
                }
                DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaSinodalNovyZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaSinodalStaryZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    }
                }
                DialogVybranoeBibleList.PEREVODBOKUNA -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaBokunaNovyZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaBokunaStaryZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    }
                }
                DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaCarniauskiNovyZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaCarniauskiStaryZavet/${getNumarKnigi(maranata.kniga)}.json"), listBible)
                    }
                }
            }
        }
        var zakladki = BibleGlobalList.zakladkiSemuxa
        val fileZakladki = when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> {
                zakladki = BibleGlobalList.zakladkiSemuxa
                File("$filesDir/BibliaSemuxaZakladki.json")
            }
            DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                zakladki = BibleGlobalList.zakladkiSinodal
                File("$filesDir/BibliaSinodalZakladki.json")
            }
            DialogVybranoeBibleList.PEREVODBOKUNA -> {
                zakladki = BibleGlobalList.zakladkiBokuna
                File("$filesDir/BibliaBokunaZakladki.json")
            }
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                zakladki = BibleGlobalList.zakladkiCarniauski
                File("$filesDir/BibliaCarniauskiZakladki.json")
            }
            else -> File("$filesDir/BibliaSemuxaZakladki.json")
        }
        if (zakladki.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            fileZakladki.writer().use {
                val gson = Gson()
                val type2 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleZakladkiData::class.java).type
                it.write(gson.toJson(zakladki, type2))
            }
        }
        var natatki = BibleGlobalList.natatkiSemuxa
        val fileNatatki = when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> {
                natatki = BibleGlobalList.natatkiSemuxa
                File("$filesDir/BibliaSemuxaNatatki.json")
            }
            DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                natatki = BibleGlobalList.natatkiSinodal
                File("$filesDir/BibliaSinodalNatatki.json")
            }
            DialogVybranoeBibleList.PEREVODBOKUNA -> {
                natatki = BibleGlobalList.natatkiBokuna
                File("$filesDir/BibliaBokunaNatatki.json")
            }
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                natatki = BibleGlobalList.natatkiCarniauski
                File("$filesDir/BibliaCarniauskiNatatki.json")
            }
            else -> File("$filesDir/BibliaSemuxaNatatki.json")
        }
        if (natatki.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            fileNatatki.writer().use {
                val gson = Gson()
                val type3 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                it.write(gson.toJson(natatki, type3))
            }
        }
        BibleGlobalList.mPedakVisable = false
        binding.linearLayout4.visibility = View.GONE
    }

    private fun saveGsonFile(file: File, list: ArrayList<ArrayList<Int>>) {
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
        val outputStream = FileWriter(file)
        outputStream.write(gson.toJson(list, type))
        outputStream.close()
    }

    override fun onResume() {
        super.onResume()
        MenuBibleSemuxa.loadNatatkiZakladkiSemuxa(this)
        MenuBibleSinoidal.loadNatatkiZakladkiSinodal(this)
        MenuBibleBokuna.loadNatatkiZakladkiBokuna(this)
        MenuBibleCarniauski.loadNatatkiZakladkiCarniauski(this)
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
        bindingprogress.progressBrighess.visibility = View.GONE
        bindingprogress.progressFont.visibility = View.GONE
        bindingprogress.progressAuto.visibility = View.GONE
    }

    override fun onPrepareMenu(menu: Menu) {
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto)
        if (binding.linearLayout4.visibility == View.VISIBLE) {
            itemAuto.isVisible = false
        } else {
            if (paralel) {
                binding.subtitleToolbar.visibility = View.GONE
                itemAuto.isVisible = false
            } else {
                binding.subtitleToolbar.visibility = View.VISIBLE
                itemAuto.isVisible = true
            }
            mActionDown = false
        }
        when {
            autoscroll -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_on)
            diffScroll == 0 -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon_up)
            else -> itemAuto.setIcon(by.carkva_gazeta.malitounik.R.drawable.scroll_icon)
        }
        val spanString = SpannableString(itemAuto.title.toString())
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_paralel).isChecked = k.getBoolean("paralel_maranata", true)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_paralel).isVisible = !vybranae
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_dzen_noch).isChecked = dzenNoch
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto_dzen_noch).isChecked = k.getBoolean("auto_dzen_noch", false)
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_auto_dzen_noch).isVisible = SettingsActivity.isLightSensorExist()
        menu.findItem(by.carkva_gazeta.malitounik.R.id.action_perevod).isVisible = !vybranae
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val prefEditor = k.edit()
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_perevod) {
            val dialog = DialogPerevodBiblii.getInstance(isSinoidal = true, isNadsan = false, perevod = k.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI)
            dialog.show(supportFragmentManager, "DialogPerevodBiblii")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("dzen_noch", true)
            } else {
                prefEditor.putBoolean("dzen_noch", false)
            }
            prefEditor.putBoolean("auto_dzen_noch", false)
            prefEditor.apply()
            removelightSensor()
            recreate()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_auto_dzen_noch) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("auto_dzen_noch", true)
                setlightSensor()
            } else {
                prefEditor.putBoolean("auto_dzen_noch", false)
                removelightSensor()
            }
            prefEditor.apply()
            if (getCheckDzenNoch() != dzenNoch) {
                recreate()
            }
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_paralel) {
            item.isChecked = !item.isChecked
            if (item.isChecked) {
                prefEditor.putBoolean("paralel_maranata", true)
            } else {
                prefEditor.putBoolean("paralel_maranata", false)
            }
            prefEditor.apply()
            adapter.notifyDataSetChanged()
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
        return false
    }

    override fun dialogHelpFullScreenSettingsClose() {
        hideHelp()
    }

    override fun onDialogFullScreenHelpClose() {
        if (dzenNoch) binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
        else binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorWhite))
        hide()
    }

    private fun hideHelp() {
        if (k.getBoolean("help_fullscreen", true)) {
            binding.constraint.setBackgroundColor(ContextCompat.getColor(this, by.carkva_gazeta.malitounik.R.color.colorPost2))
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
        outState.putString("tollBarText", binding.titleToolbar.text.toString())
        outState.putString("subTollBarText", binding.subtitleToolbar.text.toString())
        outState.putBoolean("paralel", paralel)
        outState.putInt("paralelPosition", paralelPosition)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (BibleGlobalList.mPedakVisable) {
            var find = false
            BibleGlobalList.bibleCopyList.forEach {
                if (it == position) find = true
            }
            if (find) {
                BibleGlobalList.bibleCopyList.remove(position)
            } else {
                BibleGlobalList.bibleCopyList.add(position)
            }
            adapter.notifyDataSetChanged()
        } else {
            BibleGlobalList.bibleCopyList.clear()
            parralelMestaView(position)
            paralelPosition = position
        }
        if (BibleGlobalList.mPedakVisable) {
            if (vybranae && perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
                binding.view.visibility = View.GONE
                binding.yelloy.visibility = View.GONE
                binding.underline.visibility = View.GONE
                binding.bold.visibility = View.GONE
                binding.zakladka.visibility = View.GONE
                binding.zametka.visibility = View.GONE
            } else {
                if (BibleGlobalList.bibleCopyList.size > 1) {
                    binding.view.visibility = View.GONE
                    binding.yelloy.visibility = View.GONE
                    binding.underline.visibility = View.GONE
                    binding.bold.visibility = View.GONE
                    binding.zakladka.visibility = View.GONE
                    binding.zametka.visibility = View.GONE
                } else {
                    binding.view.visibility = View.VISIBLE
                    binding.yelloy.visibility = View.VISIBLE
                    binding.underline.visibility = View.VISIBLE
                    binding.bold.visibility = View.VISIBLE
                    binding.zakladka.visibility = View.VISIBLE
                    binding.zametka.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun parralelMestaView(position: Int) {
        if (k.getBoolean("paralel_maranata", true) && !vybranae) {
            if (!autoscroll) {
                val res = getParallel(maranAta[position].kniga, maranAta[position].glava + 1, maranAta[position].styx - 1)
                if (!res.contains("+-+")) {
                    paralel = true
                    val pm = ParalelnyeMesta()
                    val ch = maranAta[position].paralel
                    val biblia = ch.split(".")
                    binding.conteiner.text = pm.paralel(res, perevod).trim()
                    binding.scroll.visibility = View.VISIBLE
                    binding.ListView.visibility = View.GONE
                    binding.titleToolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, biblia[0] + " " + biblia[1] + "." + biblia[2])
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        if (!autoscroll) {
            if (maranAta[position].paralel != "") {
                BibleGlobalList.mPedakVisable = true
                if (binding.linearLayout4.visibility == View.GONE) {
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
                    binding.linearLayout4.visibility = View.VISIBLE
                }
                var find = false
                BibleGlobalList.bibleCopyList.forEach {
                    if (it == position) find = true
                }
                if (find) {
                    BibleGlobalList.bibleCopyList.remove(position)
                } else {
                    BibleGlobalList.bibleCopyList.add(position)
                }
                adapter.notifyDataSetChanged()
            }
        }
        if (BibleGlobalList.mPedakVisable) {
            if (vybranae && perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
                binding.view.visibility = View.GONE
                binding.yelloy.visibility = View.GONE
                binding.underline.visibility = View.GONE
                binding.bold.visibility = View.GONE
                binding.zakladka.visibility = View.GONE
                binding.zametka.visibility = View.GONE
            } else {
                if (BibleGlobalList.bibleCopyList.size > 1) {
                    binding.view.visibility = View.GONE
                    binding.yelloy.visibility = View.GONE
                    binding.underline.visibility = View.GONE
                    binding.bold.visibility = View.GONE
                    binding.zakladka.visibility = View.GONE
                    binding.zametka.visibility = View.GONE
                } else {
                    binding.view.visibility = View.VISIBLE
                    binding.yelloy.visibility = View.VISIBLE
                    binding.underline.visibility = View.VISIBLE
                    binding.bold.visibility = View.VISIBLE
                    binding.zakladka.visibility = View.VISIBLE
                    binding.zametka.visibility = View.VISIBLE
                }
            }
        }
        return true
    }

    private fun getParallel(kniga: Int, glava: Int, styx: Int): String {
        val parallel = BibliaParallelChtenia()
        var res = "+-+"
        if (kniga == 1) {
            res = parallel.kniga1(glava, styx + 1)
        }
        if (kniga == 2) {
            res = parallel.kniga2(glava, styx + 1)
        }
        if (kniga == 3) {
            res = parallel.kniga3(glava, styx + 1)
        }
        if (kniga == 4) {
            res = parallel.kniga4(glava, styx + 1)
        }
        if (kniga == 5) {
            res = parallel.kniga5(glava, styx + 1)
        }
        if (kniga == 6) {
            res = parallel.kniga6(glava, styx + 1)
        }
        if (kniga == 7) {
            res = parallel.kniga7(glava, styx + 1)
        }
        if (kniga == 8) {
            res = parallel.kniga8(glava, styx + 1)
        }
        if (kniga == 9) {
            res = parallel.kniga9(glava, styx + 1)
        }
        if (kniga == 10) {
            res = parallel.kniga10(glava, styx + 1)
        }
        if (kniga == 11) {
            res = parallel.kniga11(glava, styx + 1)
        }
        if (kniga == 12) {
            res = parallel.kniga12(glava, styx + 1)
        }
        if (kniga == 13) {
            res = parallel.kniga13(glava, styx + 1)
        }
        if (kniga == 14) {
            res = parallel.kniga14(glava, styx + 1)
        }
        if (kniga == 15) {
            res = parallel.kniga15(glava, styx + 1)
        }
        if (kniga == 16) {
            res = parallel.kniga16(glava, styx + 1)
        }
        if (kniga == 17) {
            res = parallel.kniga17(glava, styx + 1)
        }
        if (kniga == 18) {
            res = parallel.kniga18(glava, styx + 1)
        }
        if (kniga == 19) {
            res = parallel.kniga19(glava, styx + 1)
        }
        if (kniga == 20) {
            res = parallel.kniga20(glava, styx + 1)
        }
        if (kniga == 21) {
            res = parallel.kniga21(glava, styx + 1)
        }
        if (kniga == 22) {
            res = parallel.kniga22(glava, styx + 1)
        }
        if (kniga == 23) {
            res = parallel.kniga23(glava, styx + 1)
        }
        if (kniga == 24) {
            res = parallel.kniga24(glava, styx + 1)
        }
        if (kniga == 25) {
            res = parallel.kniga25(glava, styx + 1)
        }
        if (kniga == 26) {
            res = parallel.kniga26(glava, styx + 1)
        }
        if (kniga == 27) {
            res = parallel.kniga27(glava, styx + 1)
        }
        if (kniga == 28) {
            res = parallel.kniga28(glava, styx + 1)
        }
        if (kniga == 29) {
            res = parallel.kniga29(glava, styx + 1)
        }
        if (kniga == 30) {
            res = parallel.kniga30(glava, styx + 1)
        }
        if (kniga == 31) {
            res = parallel.kniga31(glava, styx + 1)
        }
        if (kniga == 32) {
            res = parallel.kniga32(glava, styx + 1)
        }
        if (kniga == 33) {
            res = parallel.kniga33(glava, styx + 1)
        }
        if (kniga == 34) {
            res = parallel.kniga34(glava, styx + 1)
        }
        if (kniga == 35) {
            res = parallel.kniga35(glava, styx + 1)
        }
        if (kniga == 36) {
            res = parallel.kniga36(glava, styx + 1)
        }
        if (kniga == 37) {
            res = parallel.kniga37(glava, styx + 1)
        }
        if (kniga == 38) {
            res = parallel.kniga38(glava, styx + 1)
        }
        if (kniga == 39) {
            res = parallel.kniga39(glava, styx + 1)
        }
        if (kniga == 40) {
            res = parallel.kniga40(glava, styx + 1)
        }
        if (kniga == 41) {
            res = parallel.kniga41(glava, styx + 1)
        }
        if (kniga == 42) {
            res = parallel.kniga42(glava, styx + 1)
        }
        if (kniga == 43) {
            res = parallel.kniga43(glava, styx + 1)
        }
        if (kniga == 44) {
            res = parallel.kniga44(glava, styx + 1)
        }
        if (kniga == 45) {
            res = parallel.kniga45(glava, styx + 1)
        }
        if (kniga == 46) {
            res = parallel.kniga46(glava, styx + 1)
        }
        if (kniga == 47) {
            res = parallel.kniga47(glava, styx + 1)
        }
        if (kniga == 48) {
            res = parallel.kniga48(glava, styx + 1)
        }
        if (kniga == 49) {
            res = parallel.kniga49(glava, styx + 1)
        }
        if (kniga == 50) {
            res = parallel.kniga50(glava, styx + 1)
        }
        if (kniga == 51) {
            res = parallel.kniga51(glava, styx + 1)
        }
        if (kniga == 52) {
            res = parallel.kniga52(glava, styx + 1)
        }
        if (kniga == 53) {
            res = parallel.kniga53(glava, styx + 1)
        }
        if (kniga == 54) {
            res = parallel.kniga54(glava, styx + 1)
        }
        if (kniga == 55) {
            res = parallel.kniga55(glava, styx + 1)
        }
        if (kniga == 56) {
            res = parallel.kniga56(glava, styx + 1)
        }
        if (kniga == 57) {
            res = parallel.kniga57(glava, styx + 1)
        }
        if (kniga == 58) {
            res = parallel.kniga58(glava, styx + 1)
        }
        if (kniga == 59) {
            res = parallel.kniga59(glava, styx + 1)
        }
        if (kniga == 60) {
            res = parallel.kniga60(glava, styx + 1)
        }
        if (kniga == 61) {
            res = parallel.kniga61(glava, styx + 1)
        }
        if (kniga == 62) {
            res = parallel.kniga62(glava, styx + 1)
        }
        if (kniga == 63) {
            res = parallel.kniga63(glava, styx + 1)
        }
        if (kniga == 64) {
            res = parallel.kniga64(glava, styx + 1)
        }
        if (kniga == 65) {
            res = parallel.kniga65(glava, styx + 1)
        }
        if (kniga == 66) {
            res = parallel.kniga66(glava, styx + 1)
        }
        if (kniga == 67) {
            res = parallel.kniga67(glava, styx + 1)
        }
        if (kniga == 68) {
            res = parallel.kniga68(glava, styx + 1)
        }
        if (kniga == 69) {
            res = parallel.kniga69(glava, styx + 1)
        }
        if (kniga == 70) {
            res = parallel.kniga70(glava, styx + 1)
        }
        if (kniga == 71) {
            res = parallel.kniga71(glava, styx + 1)
        }
        if (kniga == 72) {
            res = parallel.kniga72(glava, styx + 1)
        }
        if (kniga == 73) {
            res = parallel.kniga73(glava, styx + 1)
        }
        if (kniga == 74) {
            res = parallel.kniga74(glava, styx + 1)
        }
        if (kniga == 75) {
            res = parallel.kniga75(glava, styx + 1)
        }
        if (kniga == 76) {
            res = parallel.kniga76(glava, styx + 1)
        }
        if (kniga == 77) {
            res = parallel.kniga77(glava, styx + 1)
        }
        if (!res.contains("+-+") && perevod != DialogVybranoeBibleList.PEREVODSINOIDAL) {
            res = MainActivity.translateToBelarus(res)
        }
        return res
    }

    private inner class MaranAtaListAdaprer(private val activity: Activity) : ArrayAdapter<MaranAtaData>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_maranata, maranAta) {
        override fun isEnabled(position: Int): Boolean {
            return when {
                maranAta[position].paralel == "" -> false
                !autoscroll -> super.isEnabled(position)
                else -> false
            }
        }

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItemMaranataBinding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            val zakladka = SpannableStringBuilder()
            zakladka.append(setZakladki(maranAta[position].novyZapavet, getNumarKnigiBelarusPerevoda(getNumarKnigi(maranAta[position].kniga)), maranAta[position].glava, maranAta[position].styx, maranAta[position].perevod))
            val biblia = setIndexBiblii(MainActivity.fromHtml(maranAta[position].bible).toSpannable())
            val ssb = SpannableStringBuilder(biblia).append(zakladka)
            val res = getParallel(maranAta[position].kniga, maranAta[position].glava + 1, maranAta[position].styx - 1)
            if (!res.contains("+-+")) {
                if (k.getBoolean("paralel_maranata", true) && !vybranae) {
                    val start = ssb.length
                    ssb.append("\n").append(res)
                    ssb.setSpan(RelativeSizeSpan(0.7f), start, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorSecondary_text)), start, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            if (maranAta[position].color == 1) {
                ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorBezPosta)), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_text)), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (maranAta[position].underline == 1) ssb.setSpan(UnderlineSpan(), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (maranAta[position].bold == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            viewHolder.text.text = ssb
            if (BibleGlobalList.bibleCopyList.size > 0 && BibleGlobalList.bibleCopyList.contains(position) && BibleGlobalList.mPedakVisable) {
                if (dzenNoch) {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark2)
                    viewHolder.text.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
                } else {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
                }
            } else {
                if (dzenNoch) {
                    viewHolder.text.setTextColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
                    if (maranAta[position].bible.isNotEmpty()) viewHolder.text.setBackgroundColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark))
                    else viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                } else {
                    if (maranAta[position].bible.isNotEmpty()) viewHolder.text.setBackgroundColor(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorWhite))
                    else viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
                }
            }
            if (maranAta[position].perevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiSinodal.size > 0) {
                    for (i in BibleGlobalList.natatkiSinodal.indices) {
                        if (BibleGlobalList.natatkiSinodal[i].list[0].contains(zav) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == getNumarKnigi(maranAta[position].kniga) && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == maranAta[position].styx - 1) {
                            val ssb1 = SpannableStringBuilder(viewHolder.text.text)
                            val nachalo = ssb1.length
                            ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiSinodal[i].list[5]).append("\n")
                            ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            viewHolder.text.text = ssb1
                            break
                        }
                    }
                }
            }
            if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiSemuxa.size > 0) {
                    for (i in BibleGlobalList.natatkiSemuxa.indices) {
                        if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(zav) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == getNumarKnigiBelarusPerevoda(getNumarKnigi(maranAta[position].kniga)) && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == maranAta[position].styx - 1) {
                            val ssb1 = SpannableStringBuilder(viewHolder.text.text)
                            val nachalo = ssb1.length
                            ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiSemuxa[i].list[5]).append("\n")
                            ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            viewHolder.text.text = ssb1
                            break
                        }
                    }
                }
            }
            if (perevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiBokuna.size > 0) {
                    for (i in BibleGlobalList.natatkiBokuna.indices) {
                        if (BibleGlobalList.natatkiBokuna[i].list[0].contains(zav) && BibleGlobalList.natatkiBokuna[i].list[1].toInt() == getNumarKnigiBelarusPerevoda(getNumarKnigi(maranAta[position].kniga)) && BibleGlobalList.natatkiBokuna[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiBokuna[i].list[3].toInt() == maranAta[position].styx - 1) {
                            val ssb1 = SpannableStringBuilder(viewHolder.text.text)
                            val nachalo = ssb1.length
                            ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiBokuna[i].list[5]).append("\n")
                            ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            viewHolder.text.text = ssb1
                            break
                        }
                    }
                }
            }
            if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiCarniauski.size > 0) {
                    for (i in BibleGlobalList.natatkiCarniauski.indices) {
                        if (BibleGlobalList.natatkiCarniauski[i].list[0].contains(zav) && BibleGlobalList.natatkiCarniauski[i].list[1].toInt() == getNumarKnigiBelarusPerevoda(getNumarKnigi(maranAta[position].kniga)) && BibleGlobalList.natatkiCarniauski[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiCarniauski[i].list[3].toInt() == maranAta[position].styx - 1) {
                            val ssb1 = SpannableStringBuilder(viewHolder.text.text)
                            val nachalo = ssb1.length
                            ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiCarniauski[i].list[5]).append("\n")
                            ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            viewHolder.text.text = ssb1
                            break
                        }
                    }
                }
            }
            return rootView
        }

        private fun setIndexBiblii(ssb: Spannable): Spannable {
            val t1 = ssb.indexOf(" ")
            if (t1 != -1) {
                val subText = ssb.substring(0, t1)
                if (subText.isDigitsOnly()) {
                    if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary)), 0, t1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    val t2 = ssb.indexOf("\n")
                    if (t2 != -1) {
                        val t3 = ssb.indexOf(" ", t2)
                        val subText2 = ssb.substring(t2 + 1, t3)
                        if (subText2.isDigitsOnly()) {
                            if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), t2 + 1, t3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            else ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, by.carkva_gazeta.malitounik.R.color.colorPrimary)), t2 + 1, t3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            }
            return ssb
        }

        private fun setZakladki(zavet: Boolean, kniga: Int, glava: Int, styx: Int, perevod: String): SpannableStringBuilder {
            val ssb = SpannableStringBuilder()
            val listn: Array<String>
            val lists: Array<String>
            val globalList = when (perevod) {
                DialogVybranoeBibleList.PEREVODSEMUXI -> {
                    if (BibleGlobalList.zakladkiSemuxa.size == 0) return ssb
                    listn = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
                    lists = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
                    BibleGlobalList.zakladkiSemuxa
                }

                DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                    if (BibleGlobalList.zakladkiSinodal.size == 0) return ssb
                    listn = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln)
                    lists = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals)
                    BibleGlobalList.zakladkiSinodal
                }

                DialogVybranoeBibleList.PEREVODNADSAN -> {
                    return ssb
                }

                DialogVybranoeBibleList.PEREVODBOKUNA -> {
                    if (BibleGlobalList.zakladkiBokuna.size == 0) return ssb
                    listn = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan)
                    lists = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas)
                    BibleGlobalList.zakladkiBokuna
                }

                DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                    if (BibleGlobalList.zakladkiCarniauski.size == 0) return ssb
                    listn = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskin)
                    lists = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskis)
                    BibleGlobalList.zakladkiCarniauski
                }

                else -> {
                    if (BibleGlobalList.zakladkiSemuxa.size == 0) return ssb
                    listn = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
                    lists = context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
                    BibleGlobalList.zakladkiSemuxa
                }
            }
            for (i in globalList.indices) {
                var knigaN = -1
                var knigaS = -1
                var t1: Int
                var t2: Int
                var t3: Int
                var glava1: Int
                val knigaName = globalList[i].data
                for (e in lists.indices) {
                    val t4 = lists[e].indexOf("#")
                    if (knigaName.contains(lists[e].substring(0, t4))) knigaS = e
                }
                for (e in listn.indices) {
                    val t4 = listn[e].indexOf("#")
                    if (knigaName.contains(listn[e].substring(0, t4))) knigaN = e
                }
                t1 = knigaName.indexOf("Разьдзел ")
                t2 = knigaName.indexOf("/", t1)
                t3 = knigaName.indexOf("\n\n")
                glava1 = knigaName.substring(t1 + 9, t2).toInt() - 1
                val stix1 = knigaName.substring(t2 + 6, t3).toInt() - 1
                var zavetLocal = true
                if (knigaS != -1) {
                    zavetLocal = false
                    knigaN = knigaS
                    if (perevod == DialogVybranoeBibleList.PEREVODSEMUXI || perevod == DialogVybranoeBibleList.PEREVODBOKUNA || perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                        when (knigaS) {
                            16 -> knigaN = 19
                            17 -> knigaN = 20
                            18 -> knigaN = 21
                            19 -> knigaN = 22
                            20 -> knigaN = 23
                            21 -> knigaN = 24
                            22 -> knigaN = 27
                            23 -> knigaN = 28
                            24 -> knigaN = 29
                            25 -> knigaN = 32
                            26 -> knigaN = 33
                            27 -> knigaN = 34
                            28 -> knigaN = 35
                            29 -> knigaN = 36
                            30 -> knigaN = 37
                            31 -> knigaN = 38
                            32 -> knigaN = 39
                            33 -> knigaN = 40
                            34 -> knigaN = 41
                            35 -> knigaN = 42
                            36 -> knigaN = 43
                            37 -> knigaN = 44
                            38 -> knigaN = 45
                        }
                    }
                    if (perevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
                        when (knigaS) {
                            39 -> knigaN = 17
                            40 -> knigaN = 18
                            41 -> knigaN = 25
                            42 -> knigaN = 26
                            43 -> knigaN = 31
                            44 -> knigaN = 46
                            45 -> knigaN = 47
                        }
                    }
                }
                if (zavet == zavetLocal && knigaN == kniga && glava1 == glava && stix1 == styx - 1) {
                    ssb.append(".")
                    val t5 = knigaName.lastIndexOf("<!--")
                    val color = if (t5 != -1) knigaName.substring(t5 + 4).toInt()
                    else 0
                    val d = when (color) {
                        0 -> {
                            if (dzenNoch) ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark)
                            else ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark_black)
                        }
                        1 -> {
                            if (dzenNoch) ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark1_black)
                            else ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark1)
                        }
                        2 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark2)
                        3 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark3)
                        4 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark4)
                        5 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark5)
                        6 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark6)
                        7 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark7)
                        8 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark8)
                        9 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark9)
                        10 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark10)
                        11 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark11)
                        12 -> ContextCompat.getDrawable(activity, by.carkva_gazeta.malitounik.R.drawable.bookmark12)
                        else -> null
                    }
                    val fontSize = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
                    val realpadding = (fontSize * resources.displayMetrics.density).toInt()
                    d?.setBounds(0, 0, realpadding, realpadding)
                    d?.let {
                        val span = ImageSpan(it, DynamicDrawableSpan.ALIGN_BASELINE)
                        ssb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    break
                }
            }
            return ssb
        }
    }

    private class ViewHolder(var text: TextView)

    private data class MaranAtaData(val perevod: String, val novyZapavet: Boolean, val kniga: Int, val glava: Int, val styx: Int, val paralel: String, val title: String, val bible: String, var bold: Int, var underline: Int, var color: Int)

    private data class MaranAtaSave(val perevod: String, val novyZapavet: Boolean, val kniga: Int)
}