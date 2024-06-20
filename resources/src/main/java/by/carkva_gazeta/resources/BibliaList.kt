package by.carkva_gazeta.resources

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.ContentBibleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class BibliaList : BaseActivity(), BibliaPerakvadSemuxi, BibliaPerakvadSinaidal, BibliaPerakvadBokuna, BibliaPerakvadCarniauski {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mLastClickTime: Long = 0
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private var novyZapavet = false
    private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI

    override fun addZakladka(color: Int, knigaBible: String, bible: String) {
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.addZakladka(color, knigaBible, bible)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.addZakladka(color, knigaBible, bible)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.addZakladka(color, knigaBible, bible)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.addZakladka(color, knigaBible, bible)
        }
    }

    override fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getFileZavet(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getFileZavet(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getFileZavet(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getFileZavet(novyZapaviet, kniga)
            else -> File("")
        }
    }

    override fun getNamePerevod(): String {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getNamePerevod()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getNamePerevod()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getNamePerevod()
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getNamePerevod()
            else -> ""
        }
    }

    override fun getZakladki(): ArrayList<BibleZakladkiData> {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getZakladki()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getZakladki()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getZakladki()
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getZakladki()
            else -> ArrayList()
        }
    }

    override fun getTitlePerevod(): String {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getTitlePerevod()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getTitlePerevod()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getTitlePerevod()
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getTitlePerevod()
            else -> ""
        }
    }

    override fun getSubTitlePerevod(): String {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSubTitlePerevod()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSubTitlePerevod()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSubTitlePerevod()
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSubTitlePerevod()
            else -> ""
        }
    }

    override fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSpisKnig(novyZapaviet)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSpisKnig(novyZapaviet)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSpisKnig(novyZapaviet)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getSpisKnig(novyZapaviet)
            else -> arrayOf("")
        }
    }

    override fun getKnigaReal(kniga: Int): Int {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getKnigaReal(kniga)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getKnigaReal(kniga)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getKnigaReal(kniga)
            else -> kniga
        }
    }

    override fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getInputStream(novyZapaviet, kniga)
            else -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
        }
    }

    override fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    private fun getAdapterData(): ArrayList<ArrayList<BibliaAdapterData>> {
        val groups = ArrayList<ArrayList<BibliaAdapterData>>()
        val list = getSpisKnig(novyZapavet)
        for (kniga in list.indices) {
            val inputStream = getInputStream(novyZapavet, kniga)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.readText()
            val split = builder.split("===")
            val children = ArrayList<BibliaAdapterData>()
            val razdel = if (!novyZapavet && getKnigaReal(kniga) == 21) getString(R.string.psalom)
            else getString(R.string.razdzel)
            for (glava in 1 until split.size) {
                children.add(BibliaAdapterData(list[kniga], "$razdel $glava"))
            }
            groups.add(children)
        }
        return groups
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_dark)
        } else {
            binding.elvMain.selector = ContextCompat.getDrawable(this, R.drawable.selector_default)
        }
        novyZapavet = intent.extras?.getBoolean("novyZapavet", false) ?: false
        perevod = intent.extras?.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
        val adapter = BibliaAdapterList(this, getAdapterData())
        binding.elvMain.setAdapter(adapter)
        binding.elvMain.setOnChildClickListener { _: ExpandableListView?, _: View?, groupPosition: Int, childPosition: Int, _: Long ->
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnChildClickListener true
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, BibliaActivity::class.java)
            intent.putExtra("kniga", groupPosition)
            intent.putExtra("glava", childPosition)
            intent.putExtra("fullglav", adapter.getChildrenCount(groupPosition))
            intent.putExtra("novyZapavet", novyZapavet)
            intent.putExtra("perevod", getNamePerevod())
            startActivity(intent)
            false
        }
        if (intent.extras?.getBoolean("prodolzyt", false) == true) {
            val intent1 = Intent(this, BibliaActivity::class.java)
            val kniga = intent.extras?.getInt("kniga") ?: 0
            intent1.putExtra("kniga", intent.extras?.getInt("kniga") ?: 0)
            intent1.putExtra("glava", intent.extras?.getInt("glava") ?: 0)
            intent1.putExtra("stix", intent.extras?.getInt("stix") ?: 0)
            intent1.putExtra("fullglav", adapter.getChildrenCount(kniga))
            intent1.putExtra("novyZapavet", novyZapavet)
            intent1.putExtra("perevod", getNamePerevod())
            intent.removeExtra("prodolzyt")
            startActivity(intent1)
        }
        setTollbarTheme()
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
        binding.subTitleToolbar.text = getTitlePerevod()
        if (novyZapavet) binding.titleToolbar.setText(R.string.novy_zapaviet)
        else binding.titleToolbar.setText(R.string.stary_zapaviet)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
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

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        return false
    }
}