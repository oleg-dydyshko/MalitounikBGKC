package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
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
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogDzenNochSettings
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuBibleBokuna
import by.carkva_gazeta.malitounik.MenuBibleCarniauski
import by.carkva_gazeta.malitounik.MenuBibleSemuxa
import by.carkva_gazeta.malitounik.MenuBibleSinoidal
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.VybranoeBibleList
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

class MaranAta : BaseActivity(), View.OnTouchListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, DialogHelpFullScreen.DialogFullScreenHelpListener, DialogBibleNatatka.DialogBibleNatatkaListiner, DialogAddZakladka.DialogAddZakladkiListiner, DialogPerevodBiblii.DialogPerevodBibliiListener, BibliaPerakvadBokuna, BibliaPerakvadCarniauski, BibliaPerakvadNadsana, BibliaPerakvadSemuxi, BibliaPerakvadSinaidal, ParalelnyeMesta {

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
    private var perevod = VybranoeBibleList.PEREVODSEMUXI
    private var mActionDown = false
    private var paralel = false
    private var paralelPosition = 0
    private var maranAtaScrollPosition = 0
    private var maranAtaScrollPositionY = 0
    private lateinit var binding: AkafistMaranAtaBinding
    private lateinit var bindingprogress: ProgressBinding
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJobFont: Job? = null
    private var procentJobAuto: Job? = null
    private var resetTollbarJob: Job? = null
    private var resetScreenJob: Job? = null
    private var resetTitleJob: Job? = null
    private var diffScroll = -1
    private var mun = 0
    private var day = 1
    private var vybranae = false
    private var prodoljyt = false
    private var title = ""
    private var novyZapavet = false

    override fun addZakladka(color: Int, knigaBible: String, bible: String) {
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.addZakladka(color, knigaBible, bible)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.addZakladka(color, knigaBible, bible)
        }
    }

    override fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getFileZavet(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getFileZavet(novyZapaviet, kniga)
            else -> File("")
        }
    }

    override fun getNamePerevod(): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getNamePerevod()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getNamePerevod()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getNamePerevod()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getNamePerevod()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getNamePerevod()
            else -> ""
        }
    }

    override fun getZakladki(): ArrayList<BibleZakladkiData> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getZakladki()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getZakladki()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getZakladki()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getZakladki()
            else -> ArrayList()
        }
    }

    override fun getTitlePerevod(): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getTitlePerevod()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getTitlePerevod()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getTitlePerevod()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getTitlePerevod()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getTitlePerevod()
            else -> ""
        }
    }

    override fun getSubTitlePerevod(glava: Int): String {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSubTitlePerevod(0)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSubTitlePerevod(0)
            else -> ""
        }
    }

    override fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSpisKnig(novyZapaviet)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSpisKnig(novyZapaviet)
            else -> arrayOf("")
        }
    }

    override fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getInputStream(novyZapaviet, kniga)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getInputStream(novyZapaviet, kniga)
            else -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
        }
    }

    override fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.saveVydelenieZakladkiNtanki(glava, stix)
        }
    }

    override fun translatePsaltyr(psalm: Int, styx: Int, isUpdate: Boolean): Array<Int> {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.translatePsaltyr(psalm, styx, isUpdate)
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.translatePsaltyr(psalm, styx, isUpdate)
            else -> arrayOf(1, 1)
        }
    }

    override fun isPsaltyrGreek(): Boolean {
        return when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.isPsaltyrGreek()
            VybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.isPsaltyrGreek()
            else -> true
        }
    }

    private fun onDialogFontSize(fontSize: Float) {
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
            intent.extras?.getString("perevod", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI
        } else {
            k.getString("perevod", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI
        }
        maranAtaScrollPosition = if (vybranae) k.getInt(perevod + "BibleVybranoeScroll", 0)
        else k.getInt("maranAtaScrollPasition", 0)
        maranAtaScrollPositionY = k.getInt("maranAtaScrollPasitionY", 0)
        if (savedInstanceState != null) {
            MainActivity.dialogVisable = false
            fullscreenPage = savedInstanceState.getBoolean("fullscreen")
            if (vybranae) {
                binding.titleToolbar.text = getTitlePerevod()
            } else {
                binding.titleToolbar.text = savedInstanceState.getString("tollBarText", getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])) ?: getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
            }
            binding.subtitleToolbar.text = savedInstanceState.getString("subTollBarText", "") ?: ""
            paralel = savedInstanceState.getBoolean("paralel", paralel)
            if (paralel) {
                paralelPosition = savedInstanceState.getInt("paralelPosition")
                parralelMestaView(paralelPosition)
            }
        } else {
            if (vybranae) {
                binding.titleToolbar.text = getTitlePerevod()
            } else {
                binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.maranata2, day, resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)[mun])
            }
            fullscreenPage = k.getBoolean("fullscreenPage", false)
            if (k.getBoolean("autoscrollAutostart", false)) {
                autoStartScroll()
            }
        }
        setMaranata(savedInstanceState)
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
                    VybranoeBibleList.PEREVODSEMUXI -> BibleGlobalList.zakladkiSemuxa
                    VybranoeBibleList.PEREVODSINOIDAL -> BibleGlobalList.zakladkiSinodal
                    VybranoeBibleList.PEREVODBOKUNA -> BibleGlobalList.zakladkiBokuna
                    VybranoeBibleList.PEREVODCARNIAUSKI -> BibleGlobalList.zakladkiCarniauski
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
                val razdelName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == VybranoeBibleList.PEREVODSINOIDAL) resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal)
                else resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel)
                val vershName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == VybranoeBibleList.PEREVODSINOIDAL) getString(by.carkva_gazeta.malitounik.R.string.stix_ru)
                else getString(by.carkva_gazeta.malitounik.R.string.stix_by)
                val knigaName = knigaBible + "/" + razdelName + " " + (maranAta[BibleGlobalList.bibleCopyList[0]].glava + 1) + vershName + " " + (maranAta[BibleGlobalList.bibleCopyList[0]].styx)
                val kniga = maranAta[BibleGlobalList.bibleCopyList[0]].kniga
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
            val razdelName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == VybranoeBibleList.PEREVODSINOIDAL) resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal)
            else resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel)
            val vershName = if (maranAta[BibleGlobalList.bibleCopyList[0]].perevod == VybranoeBibleList.PEREVODSINOIDAL) getString(by.carkva_gazeta.malitounik.R.string.stix_ru)
            else getString(by.carkva_gazeta.malitounik.R.string.stix_by)
            val data = BibleZakladkiData(maxIndex, knigaBible + "/" + razdelName + " " + (maranAta[BibleGlobalList.bibleCopyList[0]].glava + 1) + vershName + " " + maranAta[BibleGlobalList.bibleCopyList[0]].styx + "\n\n" + maranAta[BibleGlobalList.bibleCopyList[0]].bible + "<!--" + color)
            when (maranAta[BibleGlobalList.bibleCopyList[0]].perevod) {
                VybranoeBibleList.PEREVODSEMUXI -> BibleGlobalList.zakladkiSemuxa.add(0, data)
                VybranoeBibleList.PEREVODBOKUNA -> BibleGlobalList.zakladkiBokuna.add(0, data)
                VybranoeBibleList.PEREVODCARNIAUSKI -> BibleGlobalList.zakladkiCarniauski.add(0, data)
                VybranoeBibleList.PEREVODSINOIDAL -> BibleGlobalList.zakladkiSinodal.add(0, data)
            }
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
                MotionEvent.ACTION_DOWN -> {
                    if (fullscreenPage) {
                        if (binding.textViewTitle.visibility == View.GONE) {
                            val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
                            binding.textViewTitle.visibility = View.VISIBLE
                            binding.textViewTitle.animation = animation
                        }
                        resetTitleJob?.cancel()
                        resetTitleJob = CoroutineScope(Dispatchers.Main).launch {
                            delay(3000L)
                            val animation2 = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphaout)
                            binding.textViewTitle.visibility = View.GONE
                            binding.textViewTitle.animation = animation2
                        }
                    }
                    mActionDown = true
                }
                MotionEvent.ACTION_UP -> mActionDown = false
            }
            return false
        }
        if (id == R.id.constraint) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> {
                    n = event?.y?.toInt() ?: 0
                    val proc: Int
                    if (x > widthConstraintLayout - otstup && y < heightConstraintLayout - otstup2) {
                        setFontDialog()
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

    private fun setFontDialog() {
        bindingprogress.seekBarFontSize.progress = SettingsActivity.setProgressFontSize(fontBiblia.toInt())
        bindingprogress.progressFont.text = getString(by.carkva_gazeta.malitounik.R.string.get_font, fontBiblia.toInt())
        if (bindingprogress.seekBarFontSize.visibility == View.GONE) {
            bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this, by.carkva_gazeta.malitounik.R.anim.slide_in_left)
            bindingprogress.seekBarFontSize.visibility = View.VISIBLE
        }
        startProcent(MainActivity.PROGRESSACTIONFONT)
    }

    override fun setPerevod(perevod: String) {
        saveVydelenieNatatkiZakladki()
        val edit = k.edit()
        edit.putString("perevod", perevod)
        edit.apply()
        if (this.perevod != perevod) {
            this.perevod = perevod
            setMaranata(Bundle())
        }
    }

    private fun bibliaNew(chtenie: String): Int {
        val t1 = chtenie.lastIndexOf(" ")
        val kniga = if (t1 != -1) chtenie.substring(0, t1)
        else chtenie
        var bible = 0
        if (kniga == "Быт" || kniga == "Быц") {
            bible = 0
            novyZapavet = false
        }
        if (kniga == "Исх" || kniga == "Вых") {
            bible = 1
            novyZapavet = false
        }
        if (kniga == "Лев" || kniga == "Ляв") {
            bible = 2
            novyZapavet = false
        }
        if (kniga == "Чис" || kniga == "Лікі") {
            bible = 3
            novyZapavet = false
        }
        if (kniga == "Втор" || kniga == "Дрг") {
            bible = 4
            novyZapavet = false
        }
        if (kniga == "Нав") {
            bible = 5
            novyZapavet = false
        }
        if (kniga == "Суд") {
            bible = 6
            novyZapavet = false
        }
        if (kniga == "Руфь" || kniga == "Рут") {
            bible = 7
            novyZapavet = false
        }
        if (kniga == "1 Цар") {
            bible = 8
            novyZapavet = false
        }
        if (kniga == "2 Цар") {
            bible = 9
            novyZapavet = false
        }
        if (kniga == "3 Цар") {
            bible = 10
            novyZapavet = false
        }
        if (kniga == "4 Цар") {
            bible = 11
            novyZapavet = false
        }
        if (kniga == "1 Пар" || kniga == "1 Лет") {
            bible = 12
            novyZapavet = false
        }
        if (kniga == "2 Пар" || kniga == "2 Лет") {
            bible = 13
            novyZapavet = false
        }
        if (kniga == "1 Езд" || kniga == "1 Эзд") {
            bible = 14
            novyZapavet = false
        }
        if (kniga == "Неем" || kniga == "Нээм") {
            bible = 15
            novyZapavet = false
        }
        if (kniga == "2 Езд" || kniga == "2 Эзд") {
            bible = 16
            novyZapavet = false
        }
        if (kniga == "Тов" || kniga == "Тав") {
            bible = 17
            novyZapavet = false
        }
        if (kniga == "Иудифь" || kniga == "Юдт") {
            bible = 18
            novyZapavet = false
        }
        if (kniga == "Есф" || kniga == "Эст") {
            bible = 19
            novyZapavet = false
        }
        if (kniga == "Иов" || kniga == "Ёва") {
            bible = 20
            novyZapavet = false
        }
        if (kniga == "Пс") {
            bible = 21
            novyZapavet = false
        }
        if (kniga == "Притч" || kniga == "Высл") {
            bible = 22
            novyZapavet = false
        }
        if (kniga == "Еккл" || kniga == "Экл") {
            bible = 23
            novyZapavet = false
        }
        if (kniga == "Песн" || kniga == "Псн") {
            bible = 24
            novyZapavet = false
        }
        if (kniga == "Прем" || kniga == "Мдр") {
            bible = 25
            novyZapavet = false
        }
        if (kniga == "Сир" || kniga == "Сір") {
            bible = 26
            novyZapavet = false
        }
        if (kniga == "Ис" || kniga == "Іс") {
            bible = 27
            novyZapavet = false
        }
        if (kniga == "Иер" || kniga == "Ер") {
            bible = 28
            novyZapavet = false
        }
        if (kniga == "Плач") {
            bible = 29
            novyZapavet = false
        }
        if (kniga == "Посл Иер" || kniga == "Пасл Ер" || kniga == "Ярэм") {
            bible = 30
            novyZapavet = false
        }
        if (kniga == "Вар" || kniga == "Бар") {
            bible = 31
            novyZapavet = false
        }
        if (kniga == "Иез" || kniga == "Езк") {
            bible = 32
            novyZapavet = false
        }
        if (kniga == "Дан") {
            bible = 33
            novyZapavet = false
        }
        if (kniga == "Ос" || kniga == "Ас") {
            bible = 34
            novyZapavet = false
        }
        if (kniga == "Иоил" || kniga == "Ёіл") {
            bible = 35
            novyZapavet = false
        }
        if (kniga == "Ам") {
            bible = 36
            novyZapavet = false
        }
        if (kniga == "Авд" || kniga == "Аўдз") {
            bible = 37
            novyZapavet = false
        }
        if (kniga == "Иона" || kniga == "Ёны") {
            bible = 38
            novyZapavet = false
        }
        if (kniga == "Мих" || kniga == "Міх") {
            bible = 39
            novyZapavet = false
        }
        if (kniga == "Наум" || kniga == "Нвм") {
            bible = 40
            novyZapavet = false
        }
        if (kniga == "Авв" || kniga == "Абк") {
            bible = 41
            novyZapavet = false
        }
        if (kniga == "Соф" || kniga == "Саф") {
            bible = 42
            novyZapavet = false
        }
        if (kniga == "Агг" || kniga == "Аг") {
            bible = 43
            novyZapavet = false
        }
        if (kniga == "Зах") {
            bible = 44
            novyZapavet = false
        }
        if (kniga == "Мал") {
            bible = 45
            novyZapavet = false
        }
        if (kniga == "1 Мак") {
            bible = 46
            novyZapavet = false
        }
        if (kniga == "2 Мак") {
            bible = 47
            novyZapavet = false
        }
        if (kniga == "3 Мак") {
            bible = 48
            novyZapavet = false
        }
        if (kniga == "3 Езд" || kniga == "3 Эзд") {
            bible = 49
            novyZapavet = false
        }
        if (kniga == "Мф" || kniga == "Мц") {
            bible = 0
            novyZapavet = true
        }
        if (kniga == "Мк") {
            bible = 1
            novyZapavet = true
        }
        if (kniga == "Лк") {
            bible = 2
            novyZapavet = true
        }
        if (kniga == "Ин" || kniga == "Ян") {
            bible = 3
            novyZapavet = true
        }
        if (kniga == "Деян" || kniga == "Дз") {
            bible = 4
            novyZapavet = true
        }
        if (kniga == "Иак" || kniga == "Як") {
            bible = 5
            novyZapavet = true
        }
        if (kniga == "1 Пет" || kniga == "1 Пт") {
            bible = 6
            novyZapavet = true
        }
        if (kniga == "2 Пет" || kniga == "2 Пт") {
            bible = 7
            novyZapavet = true
        }
        if (kniga == "1 Ин" || kniga == "1 Ян") {
            bible = 8
            novyZapavet = true
        }
        if (kniga == "2 Ин" || kniga == "2 Ян") {
            bible = 9
            novyZapavet = true
        }
        if (kniga == "3 Ин" || kniga == "3 Ян") {
            bible = 10
            novyZapavet = true
        }
        if (kniga == "Иуд" || kniga == "Юды") {
            bible = 11
            novyZapavet = true
        }
        if (kniga == "Рим" || kniga == "Рым") {
            bible = 12
            novyZapavet = true
        }
        if (kniga == "1 Кор" || kniga == "1 Кар") {
            bible = 13
            novyZapavet = true
        }
        if (kniga == "2 Кор" || kniga == "2 Кар") {
            bible = 14
            novyZapavet = true
        }
        if (kniga == "Гал") {
            bible = 15
            novyZapavet = true
        }
        if (kniga == "Еф" || kniga == "Эф") {
            bible = 16
            novyZapavet = true
        }
        if (kniga == "Флп" || kniga == "Плп") {
            bible = 17
            novyZapavet = true
        }
        if (kniga == "Кол" || kniga == "Клс") {
            bible = 18
            novyZapavet = true
        }
        if (kniga == "1 Фес") {
            bible = 19
            novyZapavet = true
        }
        if (kniga == "2 Фес") {
            bible = 20
            novyZapavet = true
        }
        if (kniga == "1 Тим" || kniga == "1 Цім") {
            bible = 21
            novyZapavet = true
        }
        if (kniga == "2 Тим" || kniga == "2 Цім") {
            bible = 22
            novyZapavet = true
        }
        if (kniga == "Тит" || kniga == "Ціт") {
            bible = 23
            novyZapavet = true
        }
        if (kniga == "Флм") {
            bible = 24
            novyZapavet = true
        }
        if (kniga == "Евр" || kniga == "Гбр") {
            bible = 25
            novyZapavet = true
        }
        if (kniga == "Откр" || kniga == "Адкр") {
            bible = 26
            novyZapavet = true
        }
        return bible
    }

    private fun findIndex(chtenie: String): Int {
        var bibliaNew = bibliaNew(chtenie)
        if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI && bibliaNew == 30 && !novyZapavet) {
            bibliaNew = 31
        }
        val list = getSpisKnig(novyZapavet)
        for (e in list.indices) {
            val t1 = list[e].indexOf("#")
            val t2 = list[e].indexOf("#", t1 + 1)
            val indexBible = list[e].substring(t2 + 1).toInt()
            if (indexBible == bibliaNew) {
                return e
            }
        }
        return -1
    }

    private fun setMaranata(savedInstanceState: Bundle?) {
        maranAta.clear()
        fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        val chten = cytanne.split(";")
        var saveName = ""
        for (i in chten.indices) {
            val savePerevod = perevod
            val fit = chten[i].trim()
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
                var replace = 0
                var indexBiblii = findIndex(fit)
                if (indexBiblii == -1) {
                    perevod = VybranoeBibleList.PEREVODCARNIAUSKI
                    indexBiblii = findIndex(fit)
                    replace = 1
                }
                if (indexBiblii == -1) {
                    perevod = VybranoeBibleList.PEREVODSINOIDAL
                    indexBiblii = findIndex(fit)
                    replace = 2
                }
                val bibliaNumar = bibliaNew(fit)
                if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI && bibliaNumar == 30 && !novyZapavet) {
                    nachalo = 6
                    konec = 6
                }
                val inputStream = getInputStream(novyZapavet, indexBiblii)
                val file = if (perevod == VybranoeBibleList.PEREVODSINOIDAL || replace == 2) {
                    if (novyZapavet) {
                        File("$filesDir/BibliaSinodalNovyZavet/$indexBiblii.json")
                    } else {
                        File("$filesDir/BibliaSinodalStaryZavet/$indexBiblii.json")
                    }
                } else if (perevod == VybranoeBibleList.PEREVODBOKUNA) {
                    if (novyZapavet) {
                        File("$filesDir/BibliaBokunaNovyZavet/$indexBiblii.json")
                    } else {
                        File("$filesDir/BibliaBokunaStaryZavet/$indexBiblii.json")
                    }
                } else if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI || replace == 1) {
                    if (novyZapavet) {
                        File("$filesDir/BibliaCarniauskiNovyZavet/$indexBiblii.json")
                    } else {
                        File("$filesDir/BibliaCarniauskiStaryZavet/$indexBiblii.json")
                    }
                } else {
                    if (novyZapavet) {
                        File("$filesDir/BibliaSemuxaNovyZavet/$indexBiblii.json")
                    } else {
                        File("$filesDir/BibliaSemuxaStaryZavet/$indexBiblii.json")
                    }
                }
                BibleGlobalList.vydelenie.clear()
                BibleGlobalList.vydelenie.addAll(readFileGson(file))
                var bold: Int
                var underline: Int
                var color: Int
                if (replace > 0) {
                    val title = when (savePerevod) {
                        VybranoeBibleList.PEREVODSEMUXI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                        VybranoeBibleList.PEREVODBOKUNA -> getString(by.carkva_gazeta.malitounik.R.string.biblia_bokun)
                        VybranoeBibleList.PEREVODCARNIAUSKI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_charniauski)
                        else -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                    }
                    maranAta.add(MaranAtaData(perevod, false, -1, 0, 0, 0, saveName, "<br><em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error, title) + "</em>", 0, 0, 0))
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
                    val endFabreary = if (perevod == VybranoeBibleList.PEREVODSINOIDAL) resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_ru)
                    else resources.getString(by.carkva_gazeta.malitounik.R.string.end_fabreary_be)
                    maranAta.add(MaranAtaData(perevod, false, -1, 0, 0, 0, saveName, "<br><em>$endFabreary</em><br>\n", 0, 0, 0))
                }
                val split2Pre = builder.toString().split("===")
                val split2 = ArrayList<String>()
                split2.addAll(split2Pre)
                var addGlava = -1
                if (konec >= split2.size) {
                    addGlava = split2.size
                    for (g in split2.size..konec) {
                        split2.add(getSinoidalGlavas(fit, g))
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
                            VybranoeBibleList.PEREVODSEMUXI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                            VybranoeBibleList.PEREVODBOKUNA -> getString(by.carkva_gazeta.malitounik.R.string.biblia_bokun)
                            VybranoeBibleList.PEREVODCARNIAUSKI -> getString(by.carkva_gazeta.malitounik.R.string.biblia_charniauski)
                            else -> getString(by.carkva_gazeta.malitounik.R.string.biblia_semuxi)
                        }
                        if (perevod != VybranoeBibleList.PEREVODSINOIDAL) {
                            if (addGlava == e) maranAta.add(MaranAtaData(perevod, novyZapavet, -1, 0, 0, 0, saveName, "<br><em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error_glava, title) + "</em>", 0, 0, 0))
                        }
                        val p = if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI || replace == 1) VybranoeBibleList.PEREVODCARNIAUSKI
                        else if (perevod == VybranoeBibleList.PEREVODSINOIDAL || replace == 2) VybranoeBibleList.PEREVODSINOIDAL
                        else perevod
                        val spis = getSpisKnig(novyZapavet)[indexBiblii]
                        val t1 = spis.indexOf("#")
                        val t2 = spis.indexOf("#", t1 + 1)
                        val knigaBiblii = spis.substring(t2 + 1).toInt()
                        saveName = spis.substring(0, t1) + " $e"
                        maranAta.add(MaranAtaData(perevod, novyZapavet, -1, 0, 0, 0, saveName, "<br><strong>$saveName</strong><br>\n", 0, 0, 0))
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
                            maranAta.add(MaranAtaData(p, novyZapavet, indexBiblii, knigaBiblii, e - 1, i2 + 1, saveName, splitline[i2], bold, underline, color))
                        }
                    }
                }
                if (stixn != -1) {
                    val t1 = fit.indexOf(".")
                    var glava = fit.substring(s2 + 1, t1).toInt()
                    val p = if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI || replace == 1) VybranoeBibleList.PEREVODCARNIAUSKI
                    else if (perevod == VybranoeBibleList.PEREVODSINOIDAL || replace == 2) VybranoeBibleList.PEREVODSINOIDAL
                    else perevod
                    val spis = getSpisKnig(novyZapavet)[indexBiblii]
                    val t2 = spis.indexOf("#")
                    val t3 = spis.indexOf("#", t2 + 1)
                    val knigaBiblii = spis.substring(t3 + 1).toInt()
                    saveName = spis.substring(0, t2) + " $glava"
                    maranAta.add(MaranAtaData(VybranoeBibleList.PEREVODSEMUXI, novyZapavet, -1, 0, 0, 0, saveName, "<br><strong>$saveName</strong><br>\n", 0, 0, 0))
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
                            val t21 = resbib.indexOf("#")
                            val t31 = resbib.indexOf("#", t21 + 1)
                            glava = resbib.substring(t21 + 1, t31).toInt()
                            resbib = resbib.substring(t31 + 1)
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
                        saveName = spis.substring(0, t2) + " $glava"
                        maranAta.add(MaranAtaData(p, novyZapavet, indexBiblii, knigaBiblii, glava - 1, i3, saveName, resbib, bold, underline, color))
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
                            saveName = spis.substring(0, t2) + " $konec"
                            maranAta.add(MaranAtaData(p, novyZapavet, indexBiblii, knigaBiblii, konec - 1, i21 + 1, saveName, res2[i21], bold, underline, color))
                        }
                    }
                }
            } catch (_: Throwable) {
                val t1 = fit.lastIndexOf(" ")
                val title = title + " ${fit.substring(t1 + 1)}"
                maranAta.add(MaranAtaData(perevod = VybranoeBibleList.PEREVODSEMUXI, novyZapavet = false, -1, 0, 0, 0, title, "<br><strong>$title</strong><br>\n", 0, 0, 0))
                maranAta.add(MaranAtaData(perevod = VybranoeBibleList.PEREVODSEMUXI, novyZapavet = false, -1, 0, 0, 0, title, resources.getString(by.carkva_gazeta.malitounik.R.string.error_ch) + "\n", 0, 0, 0))
            }
            perevod = savePerevod
        }
        adapter.notifyDataSetChanged()
        val position = if (vybranae && !prodoljyt && savedInstanceState == null) {
            findTitle()
        } else maranAtaScrollPosition
        CoroutineScope(Dispatchers.Main).launch {
            binding.ListView.setSelection(position)
        }
        binding.ListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            private var checkDiff = false

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                mActionDown = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
            }

            override fun onScroll(list: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (list.adapter == null || list.getChildAt(0) == null) return
                var firstPosition = list.firstVisiblePosition
                if (firstPosition > maranAta.size - 1) firstPosition = maranAta.size - 1
                val nazva = maranAta[firstPosition].title
                if (fullscreenPage) {
                    binding.textViewTitle.text = nazva
                }
                maranAtaScrollPosition = firstPosition
                maranAtaScrollPositionY = list.getChildAt(0).top
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
    }

    private fun findTitle(): Int {
        for (i in 0 until adapter.count) {
            if (title == adapter.getItem(i)?.title) {
                return i
            }
        }
        return 0
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

    private fun getSinoidalGlavas(chtenie: String, konec: Int): String {
        val savePerevod = perevod
        perevod = VybranoeBibleList.PEREVODSINOIDAL
        val nomer = findIndex(chtenie)
        val inputStream = getInputStream(novyZapavet, nomer)
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
        perevod = savePerevod
        return builder.toString().split("===")[konec]
    }

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        autoStartScrollJob?.cancel()
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
            autoScrollJob?.cancel()
            if (delayDisplayOff) {
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
            }
            resetScreenJob?.cancel()
            autoStartScrollJob?.cancel()
            autoScroll()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                binding.ListView.setSelection(0)
            }
            autoStartScroll()
            invalidateOptionsMenu()
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

    private fun startProcent(progressAction: Int) {
        if (progressAction == MainActivity.PROGRESSACTIONFONT) {
            procentJobFont?.cancel()
            bindingprogress.progressFont.visibility = View.VISIBLE
            procentJobFont = CoroutineScope(Dispatchers.Main).launch {
                MainActivity.dialogVisable = true
                delay(2000)
                bindingprogress.progressFont.visibility = View.GONE
                delay(3000)
                if (bindingprogress.seekBarFontSize.visibility == View.VISIBLE) {
                    bindingprogress.seekBarFontSize.animation = AnimationUtils.loadAnimation(this@MaranAta, by.carkva_gazeta.malitounik.R.anim.slide_out_right)
                    bindingprogress.seekBarFontSize.visibility = View.GONE
                    MainActivity.dialogVisable = false
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
                resetTollbar(binding.toolbar.layoutParams)
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
        prefEditors.putInt("maranAtaScrollPasitionY", maranAtaScrollPositionY)
        prefEditors.putBoolean("fullscreenPage", fullscreenPage)
        prefEditors.apply()
        resetTitleJob?.cancel()
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
                    if (kniga != maranata.kniga) {
                        maranataSave.add(MaranAtaSave(maranata.perevod, maranata.novyZapavet, maranata.kniga))
                    }
                    kniga = maranata.kniga
                } else {
                    val file = when (maranata.perevod) {
                        VybranoeBibleList.PEREVODSEMUXI -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaSemuxaNovyZavet/${maranata.kniga}.json")
                            } else {
                                File("$filesDir/BibliaSemuxaStaryZavet/${maranata.kniga}.json")
                            }
                        }
                        VybranoeBibleList.PEREVODSINOIDAL -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaSinodalNovyZavet/${maranata.kniga}.json")
                            } else {
                                File("$filesDir/BibliaSinodalStaryZavet/${maranata.kniga}.json")
                            }
                        }
                        VybranoeBibleList.PEREVODBOKUNA -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaBokunaNovyZavet/${maranata.kniga}.json")
                            } else {
                                File("$filesDir/BibliaBokunaStaryZavet/${maranata.kniga}.json")
                            }
                        }
                        VybranoeBibleList.PEREVODCARNIAUSKI -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaCarniauskiNovyZavet/${maranata.kniga}.json")
                            } else {
                                File("$filesDir/BibliaCarniauskiStaryZavet/${maranata.kniga}.json")
                            }
                        }
                        else -> {
                            if (maranata.novyZapavet) {
                                File("$filesDir/BibliaSemuxaNovyZavet/${maranata.kniga}.json")
                            } else {
                                File("$filesDir/BibliaSemuxaStaryZavet/${maranata.kniga}.json")
                            }
                        }
                    }
                    if (file.exists()) file.delete()
                }
            }
        }
        maranataSave.forEach { maranata ->
            when (maranata.perevod) {
                VybranoeBibleList.PEREVODSEMUXI -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaSemuxaNovyZavet/${maranata.kniga}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaSemuxaStaryZavet/${maranata.kniga}.json"), listBible)
                    }
                }
                VybranoeBibleList.PEREVODSINOIDAL -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaSinodalNovyZavet/${maranata.kniga}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaSinodalStaryZavet/${maranata.kniga}.json"), listBible)
                    }
                }
                VybranoeBibleList.PEREVODBOKUNA -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaBokunaNovyZavet/${maranata.kniga}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaBokunaStaryZavet/${maranata.kniga}.json"), listBible)
                    }
                }
                VybranoeBibleList.PEREVODCARNIAUSKI -> {
                    if (maranata.novyZapavet) {
                        saveGsonFile(File("$filesDir/BibliaCarniauskiNovyZavet/${maranata.kniga}.json"), listBible)
                    } else {
                        saveGsonFile(File("$filesDir/BibliaCarniauskiStaryZavet/${maranata.kniga}.json"), listBible)
                    }
                }
            }
        }
        var zakladki = BibleGlobalList.zakladkiSemuxa
        val fileZakladki = when (perevod) {
            VybranoeBibleList.PEREVODSEMUXI -> {
                zakladki = BibleGlobalList.zakladkiSemuxa
                File("$filesDir/BibliaSemuxaZakladki.json")
            }
            VybranoeBibleList.PEREVODSINOIDAL -> {
                zakladki = BibleGlobalList.zakladkiSinodal
                File("$filesDir/BibliaSinodalZakladki.json")
            }
            VybranoeBibleList.PEREVODBOKUNA -> {
                zakladki = BibleGlobalList.zakladkiBokuna
                File("$filesDir/BibliaBokunaZakladki.json")
            }
            VybranoeBibleList.PEREVODCARNIAUSKI -> {
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
            VybranoeBibleList.PEREVODSEMUXI -> {
                natatki = BibleGlobalList.natatkiSemuxa
                File("$filesDir/BibliaSemuxaNatatki.json")
            }
            VybranoeBibleList.PEREVODSINOIDAL -> {
                natatki = BibleGlobalList.natatkiSinodal
                File("$filesDir/BibliaSinodalNatatki.json")
            }
            VybranoeBibleList.PEREVODBOKUNA -> {
                natatki = BibleGlobalList.natatkiBokuna
                File("$filesDir/BibliaBokunaNatatki.json")
            }
            VybranoeBibleList.PEREVODCARNIAUSKI -> {
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
            autoStartScroll()
        }
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
            val dialog = DialogPerevodBiblii.getInstance(isSinoidal = true, isNadsan = false, perevod = k.getString("perevod", VybranoeBibleList.PEREVODSEMUXI) ?: VybranoeBibleList.PEREVODSEMUXI)
            dialog.show(supportFragmentManager, "DialogPerevodBiblii")
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
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
            prefEditor.putBoolean("autoscrollAutostart", !autoscroll)
            prefEditor.apply()
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_font) {
            setFontDialog()
            return true
        }
        if (id == by.carkva_gazeta.malitounik.R.id.action_fullscreen) {
            hideHelp()
            return true
        }
        return false
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
        val layoutParams = binding.ListView.layoutParams as ViewGroup.MarginLayoutParams
        val px = (resources.displayMetrics.density * 10).toInt()
        layoutParams.setMargins(0, 0, px, px)
        binding.ListView.setPadding(binding.ListView.paddingLeft, binding.ListView.paddingTop, 0 , 0)
        binding.ListView.layoutParams = layoutParams
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, binding.constraint)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        val animation = AnimationUtils.loadAnimation(baseContext, by.carkva_gazeta.malitounik.R.anim.alphain)
        binding.actionFullscreen.visibility = View.VISIBLE
        binding.actionFullscreen.animation = animation
        binding.actionBack.visibility = View.VISIBLE
        binding.actionBack.animation = animation
    }

    private fun show() {
        fullscreenPage = false
        supportActionBar?.show()
        val layoutParams = binding.ListView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0)
        val px = (resources.displayMetrics.density * 10).toInt()
        binding.ListView.setPadding(binding.ListView.paddingLeft, binding.ListView.paddingTop, px , px)
        binding.ListView.layoutParams = layoutParams
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
            if (vybranae && perevod == VybranoeBibleList.PEREVODNADSAN) {
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
                val res = getParallel(maranAta[position].novyZapavet, maranAta[position].knigaBiblii, maranAta[position].glava + 1, maranAta[position].styx - 1)
                if (!res.contains("+-+")) {
                    paralel = true
                    binding.conteiner.text = paralel(res, perevod)
                    binding.scroll.visibility = View.VISIBLE
                    binding.ListView.visibility = View.GONE
                    binding.titleToolbar.text = resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, maranAta[position].title + ":" + maranAta[position].styx)
                    val layoutParams = binding.toolbar.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.titleToolbar.isSingleLine = false
                    binding.subtitleToolbar.isSingleLine = false
                    binding.titleToolbar.isSelected = true
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        if (!autoscroll) {
            if (maranAta[position].kniga != -1) {
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
            if (vybranae && perevod == VybranoeBibleList.PEREVODNADSAN) {
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

    private fun getParallel(novyZapavet: Boolean, kniga: Int, glava: Int, styx: Int): String {
        val parallel = BibliaParallelChtenia()
        var res = "+-+"
        if (novyZapavet) {
            if (kniga == 0) {
                res = parallel.kniga51(glava, styx + 1)
            }
            if (kniga == 1) {
                res = parallel.kniga52(glava, styx + 1)
            }
            if (kniga == 2) {
                res = parallel.kniga53(glava, styx + 1)
            }
            if (kniga == 3) {
                res = parallel.kniga54(glava, styx + 1)
            }
            if (kniga == 4) {
                res = parallel.kniga55(glava, styx + 1)
            }
            if (kniga == 5) {
                res = parallel.kniga56(glava, styx + 1)
            }
            if (kniga == 6) {
                res = parallel.kniga57(glava, styx + 1)
            }
            if (kniga == 7) {
                res = parallel.kniga58(glava, styx + 1)
            }
            if (kniga == 8) {
                res = parallel.kniga59(glava, styx + 1)
            }
            if (kniga == 9) {
                res = parallel.kniga60(glava, styx + 1)
            }
            if (kniga == 10) {
                res = parallel.kniga61(glava, styx + 1)
            }
            if (kniga == 11) {
                res = parallel.kniga62(glava, styx + 1)
            }
            if (kniga == 12) {
                res = parallel.kniga63(glava, styx + 1)
            }
            if (kniga == 13) {
                res = parallel.kniga64(glava, styx + 1)
            }
            if (kniga == 14) {
                res = parallel.kniga65(glava, styx + 1)
            }
            if (kniga == 15) {
                res = parallel.kniga66(glava, styx + 1)
            }
            if (kniga == 16) {
                res = parallel.kniga67(glava, styx + 1)
            }
            if (kniga == 17) {
                res = parallel.kniga68(glava, styx + 1)
            }
            if (kniga == 18) {
                res = parallel.kniga69(glava, styx + 1)
            }
            if (kniga == 19) {
                res = parallel.kniga70(glava, styx + 1)
            }
            if (kniga == 20) {
                res = parallel.kniga71(glava, styx + 1)
            }
            if (kniga == 21) {
                res = parallel.kniga72(glava, styx + 1)
            }
            if (kniga == 22) {
                res = parallel.kniga73(glava, styx + 1)
            }
            if (kniga == 23) {
                res = parallel.kniga74(glava, styx + 1)
            }
            if (kniga == 24) {
                res = parallel.kniga75(glava, styx + 1)
            }
            if (kniga == 25) {
                res = parallel.kniga76(glava, styx + 1)
            }
            if (kniga == 26) {
                res = parallel.kniga77(glava, styx + 1)
            }
        } else {
            if (kniga == 0) {
                res = parallel.kniga1(glava, styx + 1)
            }
            if (kniga == 1) {
                res = parallel.kniga2(glava, styx + 1)
            }
            if (kniga == 2) {
                res = parallel.kniga3(glava, styx + 1)
            }
            if (kniga == 3) {
                res = parallel.kniga4(glava, styx + 1)
            }
            if (kniga == 4) {
                res = parallel.kniga5(glava, styx + 1)
            }
            if (kniga == 5) {
                res = parallel.kniga6(glava, styx + 1)
            }
            if (kniga == 6) {
                res = parallel.kniga7(glava, styx + 1)
            }
            if (kniga == 7) {
                res = parallel.kniga8(glava, styx + 1)
            }
            if (kniga == 8) {
                res = parallel.kniga9(glava, styx + 1)
            }
            if (kniga == 9) {
                res = parallel.kniga10(glava, styx + 1)
            }
            if (kniga == 10) {
                res = parallel.kniga11(glava, styx + 1)
            }
            if (kniga == 11) {
                res = parallel.kniga12(glava, styx + 1)
            }
            if (kniga == 12) {
                res = parallel.kniga13(glava, styx + 1)
            }
            if (kniga == 13) {
                res = parallel.kniga14(glava, styx + 1)
            }
            if (kniga == 14) {
                res = parallel.kniga15(glava, styx + 1)
            }
            if (kniga == 15) {
                res = parallel.kniga16(glava, styx + 1)
            }
            if (kniga == 16) {
                res = parallel.kniga17(glava, styx + 1)
            }
            if (kniga == 17) {
                res = parallel.kniga18(glava, styx + 1)
            }
            if (kniga == 18) {
                res = parallel.kniga19(glava, styx + 1)
            }
            if (kniga == 19) {
                res = parallel.kniga20(glava, styx + 1)
            }
            if (kniga == 20) {
                res = parallel.kniga21(glava, styx + 1)
            }
            if (kniga == 21) {
                res = if (isPsaltyrGreek()) parallel.kniga22(glava, styx + 1)
                else parallel.kniga22Masoretskaya(glava, styx + 1)
            }
            if (kniga == 22) {
                res = parallel.kniga23(glava, styx + 1)
            }
            if (kniga == 23) {
                res = parallel.kniga24(glava, styx + 1)
            }
            if (kniga == 24) {
                res = parallel.kniga25(glava, styx + 1)
            }
            if (kniga == 25) {
                res = parallel.kniga26(glava, styx + 1)
            }
            if (kniga == 26) {
                res = parallel.kniga27(glava, styx + 1)
            }
            if (kniga == 27) {
                res = parallel.kniga28(glava, styx + 1)
            }
            if (kniga == 28) {
                res = parallel.kniga29(glava, styx + 1)
            }
            if (kniga == 29) {
                res = parallel.kniga30(glava, styx + 1)
            }
            if (kniga == 30) {
                res = parallel.kniga31(glava, styx + 1)
            }
            if (kniga == 31) {
                res = parallel.kniga32(glava, styx + 1)
            }
            if (kniga == 32) {
                res = parallel.kniga33(glava, styx + 1)
            }
            if (kniga == 33) {
                res = parallel.kniga34(glava, styx + 1)
            }
            if (kniga == 34) {
                res = parallel.kniga35(glava, styx + 1)
            }
            if (kniga == 35) {
                res = parallel.kniga36(glava, styx + 1)
            }
            if (kniga == 36) {
                res = parallel.kniga37(glava, styx + 1)
            }
            if (kniga == 37) {
                res = parallel.kniga38(glava, styx + 1)
            }
            if (kniga == 38) {
                res = parallel.kniga39(glava, styx + 1)
            }
            if (kniga == 39) {
                res = parallel.kniga40(glava, styx + 1)
            }
            if (kniga == 40) {
                res = parallel.kniga41(glava, styx + 1)
            }
            if (kniga == 41) {
                res = parallel.kniga42(glava, styx + 1)
            }
            if (kniga == 42) {
                res = parallel.kniga43(glava, styx + 1)
            }
            if (kniga == 43) {
                res = parallel.kniga44(glava, styx + 1)
            }
            if (kniga == 44) {
                res = parallel.kniga45(glava, styx + 1)
            }
            if (kniga == 45) {
                res = parallel.kniga46(glava, styx + 1)
            }
            if (kniga == 46) {
                res = parallel.kniga47(glava, styx + 1)
            }
            if (kniga == 47) {
                res = parallel.kniga48(glava, styx + 1)
            }
            if (kniga == 48) {
                res = parallel.kniga49(glava, styx + 1)
            }
            if (kniga == 49) {
                res = parallel.kniga50(glava, styx + 1)
            }
        }
        if (!res.contains("+-+") && perevod != VybranoeBibleList.PEREVODSINOIDAL) {
            res = MainActivity.translateToBelarus(res)
        }
        return res
    }

    private inner class MaranAtaListAdaprer(private val activity: Activity) : ArrayAdapter<MaranAtaData>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_maranata, maranAta) {
        override fun isEnabled(position: Int): Boolean {
            return when {
                maranAta[position].kniga == -1 -> false
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
            zakladka.append(setZakladki(maranAta[position].novyZapavet, maranAta[position].kniga, maranAta[position].glava, maranAta[position].styx, maranAta[position].perevod))
            val biblia = if (maranAta[position].kniga != -1) findIntStyx(SpannableStringBuilder(MainActivity.fromHtml(maranAta[position].bible)))
            else SpannableStringBuilder(MainActivity.fromHtml(maranAta[position].bible))
            val ssb = SpannableStringBuilder(biblia).append(zakladka)
            val res = getParallel(maranAta[position].novyZapavet, maranAta[position].knigaBiblii, maranAta[position].glava + 1, maranAta[position].styx - 1)
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
            if (maranAta[position].perevod == VybranoeBibleList.PEREVODSINOIDAL) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiSinodal.size > 0) {
                    for (i in BibleGlobalList.natatkiSinodal.indices) {
                        if (BibleGlobalList.natatkiSinodal[i].list[0].contains(zav) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == maranAta[position].kniga && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == maranAta[position].styx - 1) {
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
            if (perevod == VybranoeBibleList.PEREVODSEMUXI) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiSemuxa.size > 0) {
                    for (i in BibleGlobalList.natatkiSemuxa.indices) {
                        if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(zav) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == maranAta[position].kniga && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == maranAta[position].styx - 1) {
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
            if (perevod == VybranoeBibleList.PEREVODBOKUNA) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiBokuna.size > 0) {
                    for (i in BibleGlobalList.natatkiBokuna.indices) {
                        if (BibleGlobalList.natatkiBokuna[i].list[0].contains(zav) && BibleGlobalList.natatkiBokuna[i].list[1].toInt() == maranAta[position].kniga && BibleGlobalList.natatkiBokuna[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiBokuna[i].list[3].toInt() == maranAta[position].styx - 1) {
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
            if (perevod == VybranoeBibleList.PEREVODCARNIAUSKI) {
                var zav = "0"
                if (maranAta[position].novyZapavet) zav = "1"
                if (BibleGlobalList.natatkiCarniauski.size > 0) {
                    for (i in BibleGlobalList.natatkiCarniauski.indices) {
                        if (BibleGlobalList.natatkiCarniauski[i].list[0].contains(zav) && BibleGlobalList.natatkiCarniauski[i].list[1].toInt() == maranAta[position].kniga && BibleGlobalList.natatkiCarniauski[i].list[2].toInt() == maranAta[position].glava && BibleGlobalList.natatkiCarniauski[i].list[3].toInt() == maranAta[position].styx - 1) {
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

        private fun findIntStyx(ssb: SpannableStringBuilder, index: Int = 0): SpannableStringBuilder {
            val t1 = ssb.indexOf(" ", index)
            if (t1 != -1) {
                val subText = ssb.substring(0, t1)
                if (subText.isDigitsOnly()) {
                    ssb.insert(t1, ".")
                    if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), 0, t1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary)), 0, t1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                val t2 = ssb.indexOf("\n", index)
                if (t2 != -1) {
                    val t3 = ssb.indexOf(" ", t2)
                    if (t3 != -1) {
                        val subText2 = ssb.substring(t2 + 1, t3)
                        if (subText2.isDigitsOnly()) {
                            ssb.insert(t3, ".")
                            if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)), t2 + 1, t3 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            else ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary)), t2 + 1, t3 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        findIntStyx(ssb, t2 + 1)
                    }
                }
            }
            return ssb
        }

        private fun setZakladki(novyZapavet: Boolean, kniga: Int, glava: Int, styx: Int, perevod: String): SpannableStringBuilder {
            val ssb = SpannableStringBuilder()
            if (kniga == -1) return ssb
            val list: Array<String>
            val globalList = when (perevod) {
                VybranoeBibleList.PEREVODSEMUXI -> {
                    if (BibleGlobalList.zakladkiSemuxa.size == 0) return ssb
                    list = if (novyZapavet) context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
                    else context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
                    BibleGlobalList.zakladkiSemuxa
                }

                VybranoeBibleList.PEREVODSINOIDAL -> {
                    if (BibleGlobalList.zakladkiSinodal.size == 0) return ssb
                    list = if (novyZapavet) context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln)
                    else context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals)
                    BibleGlobalList.zakladkiSinodal
                }

                VybranoeBibleList.PEREVODNADSAN -> {
                    return ssb
                }

                VybranoeBibleList.PEREVODBOKUNA -> {
                    if (BibleGlobalList.zakladkiBokuna.size == 0) return ssb
                    list = if (novyZapavet) context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan)
                    else context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas)
                    BibleGlobalList.zakladkiBokuna
                }

                VybranoeBibleList.PEREVODCARNIAUSKI -> {
                    if (BibleGlobalList.zakladkiCarniauski.size == 0) return ssb
                    list = if (novyZapavet) context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskin)
                    else context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.charniauskis)
                    BibleGlobalList.zakladkiCarniauski
                }

                else -> {
                    if (BibleGlobalList.zakladkiSemuxa.size == 0) return ssb
                    list = if (novyZapavet) context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
                    else context.resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
                    BibleGlobalList.zakladkiSemuxa
                }
            }
            for (i in globalList.indices) {
                var kniga1 = -1
                var t1: Int
                var t2: Int
                var t3: Int
                var glava1: Int
                val knigaName = globalList[i].data
                for (e in list.indices) {
                    val t4 = list[e].indexOf("#")
                    if (knigaName.contains(list[e].substring(0, t4))) kniga1 = e
                }
                t1 = knigaName.indexOf(" ")
                t2 = knigaName.indexOf("/", t1)
                t3 = knigaName.indexOf("\n\n")
                val t4 = knigaName.indexOf(" ", t1 + 1)
                glava1 = knigaName.substring(t1 + 1, t2).toInt() - 1
                val stix1 = knigaName.substring(t4 + 1, t3).toInt() - 1
                if (kniga1 == kniga && glava1 == glava && stix1 == styx - 1) {
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

    private data class MaranAtaData(val perevod: String, val novyZapavet: Boolean, val kniga: Int, val knigaBiblii: Int, val glava: Int, val styx: Int, val title: String, val bible: String, var bold: Int, var underline: Int, var color: Int)

    private data class MaranAtaSave(val perevod: String, val novyZapavet: Boolean, val kniga: Int)
}