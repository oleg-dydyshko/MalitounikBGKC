package by.carkva_gazeta.resources

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.File
import java.io.InputStream

class BibliaList : BaseActivity(), BibliaPerakvadSemuxi, BibliaPerakvadNadsana, BibliaPerakvadSinaidal, BibliaPerakvadBokuna, BibliaPerakvadCarniauski, BibliaAdapterList.BibliaAdapterListListener {
    private val dzenNoch get() = getBaseDzenNoch()
    private lateinit var binding: ContentBibleBinding
    private var resetTollbarJob: Job? = null
    private var novyZapavet = false
    private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI
    private lateinit var adapter: BibliaAdapterList
    private val adapterData = ArrayList<ArrayList<BibliaAdapterData>>()
    private var kniga = 0
    private val listBibliaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 300) {
            val myPerevod = result.data?.extras?.getString("perevod") ?: DialogVybranoeBibleList.PEREVODSEMUXI
            if (perevod != myPerevod) {
                perevod = myPerevod
                kniga =  result.data?.extras?.getInt("kniga") ?: 0
                adapterData.clear()
                adapterData.addAll(getAdapterData())
                adapter.notifyDataSetChanged()
                binding.subTitleToolbar.text = getTitlePerevod()
                if (perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
                    binding.titleToolbar.setText(R.string.title_psalter)
                    binding.subTitleToolbar.visibility = View.GONE
                } else {
                    if (novyZapavet) binding.titleToolbar.setText(R.string.novy_zapaviet)
                    else binding.titleToolbar.setText(R.string.stary_zapaviet)
                    binding.subTitleToolbar.visibility = View.VISIBLE
                }
                binding.elvMain.expandGroup(kniga)
                binding.elvMain.setSelectedGroup(kniga)
            }
        }
    }

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
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getNamePerevod()
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
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getTitlePerevod()
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
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getSpisKnig()
            else -> arrayOf("")
        }
    }

    override fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.getInputStream(novyZapaviet, kniga)
            else -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
        }
    }

    override fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.saveVydelenieZakladkiNtanki(glava, stix)
        }
    }

    override fun translatePsaltyr(psalm: Int, styx: Int, isUpdate: Boolean): Array<Int> {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.translatePsaltyr(psalm, styx, isUpdate)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.translatePsaltyr(psalm, styx, isUpdate)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.translatePsaltyr(psalm,styx, isUpdate)
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.translatePsaltyr(psalm, styx, isUpdate)
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.translatePsaltyr(psalm, styx, isUpdate)
            else -> arrayOf(1, 1)
        }
    }

    override fun isPsaltyrGreek(): Boolean {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.isPsaltyrGreek()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.isPsaltyrGreek()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.isPsaltyrGreek()
            DialogVybranoeBibleList.PEREVODSINOIDAL -> super<BibliaPerakvadSinaidal>.isPsaltyrGreek()
            DialogVybranoeBibleList.PEREVODNADSAN -> super<BibliaPerakvadNadsana>.isPsaltyrGreek()
            else -> true
        }
    }

    override fun onBack() {
        val intent = Intent()
        intent.putExtra("perevod", perevod)
        setResult(500, intent)
        super.onBack()
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("perevod", perevod)
        outState.putInt("kniga", kniga)
        outState.putBoolean("novyZapavet", novyZapavet)
    }

    /*private fun generateSpisKnig() {
        val list = getSpisKnig(novyZapavet)
        val sb = StringBuilder()
        for (kniga in list.indices) {
            val inputStream = getInputStream(novyZapavet, kniga)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = reader.readText()
            val split = builder.split("===")
            sb.append("<item>").append(list[kniga]).append("#").append(split.size - 1).append("#").append(kniga).append("</item>\n")
        }
        val dir = File("$filesDir/biblia")
        if (!dir.exists()) dir.mkdir()
        val fileName = File("$filesDir/biblia/biblia.txt")
        fileName.writer().use {
            it.write(sb.toString())
        }
    }*/

    private fun getAdapterData(): ArrayList<ArrayList<BibliaAdapterData>> {
        val groups = ArrayList<ArrayList<BibliaAdapterData>>()
        val list = getSpisKnig(novyZapavet)
        for (kniga in list.indices) {
            val t1 = list[kniga].indexOf("#")
            val t2 = list[kniga].indexOf("#", t1 + 1)
            val glav = list[kniga].substring(t1 + 1, t2).toInt()
            val children = ArrayList<BibliaAdapterData>()
            children.add(BibliaAdapterData(list[kniga].substring(0, t1), glav))
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
        if (savedInstanceState != null) {
            perevod = savedInstanceState.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI)
            kniga = savedInstanceState.getInt("kniga", 0)
            novyZapavet = savedInstanceState.getBoolean("novyZapavet", false)
        } else {
            perevod = intent.extras?.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
            kniga = intent.extras?.getInt("kniga") ?: 0
            novyZapavet = intent.extras?.getBoolean("novyZapavet", false) ?: false
        }
        adapterData.addAll(getAdapterData())
        adapter = BibliaAdapterList(this, adapterData)
        adapter.setBibliaAdapterListListener(this)
        binding.elvMain.setAdapter(adapter)
        if (adapter.groupCount == 1) binding.elvMain.expandGroup(0)
        if (intent.extras?.getBoolean("prodolzyt", false) == true) {
            val intent1 = Intent(this, BibliaActivity::class.java)
            binding.elvMain.expandGroup(kniga)
            binding.elvMain.setSelectedGroup(kniga)
            intent1.putExtra("kniga", kniga)
            intent1.putExtra("glava", intent.extras?.getInt("glava") ?: 0)
            intent1.putExtra("stix", intent.extras?.getInt("stix") ?: 0)
            intent1.putExtra("novyZapavet", novyZapavet)
            intent1.putExtra("perevod", getNamePerevod())
            intent.removeExtra("prodolzyt")
            listBibliaLauncher.launch(intent1)
        }
        if (savedInstanceState != null) {
            binding.elvMain.expandGroup(kniga)
            binding.elvMain.setSelectedGroup(kniga)
        }
        setTollbarTheme()
    }

    override fun onComplete(groupPosition: Int, childPosition: Int) {
        val intent = Intent(this, BibliaActivity::class.java)
        intent.putExtra("kniga", groupPosition)
        intent.putExtra("glava", childPosition)
        intent.putExtra("novyZapavet", novyZapavet)
        intent.putExtra("perevod", getNamePerevod())
        listBibliaLauncher.launch(intent)
    }

    override fun collapseGroup(groupPosition: Int) {
        binding.elvMain.collapseGroup(groupPosition)
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
        if (perevod == DialogVybranoeBibleList.PEREVODNADSAN) {
            binding.titleToolbar.setText(R.string.title_psalter)
            binding.subTitleToolbar.visibility = View.GONE
        } else {
            if (novyZapavet) binding.titleToolbar.setText(R.string.novy_zapaviet)
            else binding.titleToolbar.setText(R.string.stary_zapaviet)
        }
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