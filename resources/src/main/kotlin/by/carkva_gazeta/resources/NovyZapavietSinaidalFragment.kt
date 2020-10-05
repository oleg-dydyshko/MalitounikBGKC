package by.carkva_gazeta.resources

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.MainActivity
import kotlinx.android.synthetic.main.activity_bible_page_fragment.*
import java.io.BufferedReader
import java.io.InputStreamReader

class NovyZapavietSinaidalFragment : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var clicParalelListiner: ClicParalelListiner? = null
    private var listPositionListiner: ListPositionListiner? = null
    private lateinit var adapter: ExpArrayAdapterParallel
    private var bible: ArrayList<String> = ArrayList()
    private val knigaBible: String
        get() {
            var knigaName = ""
            when (kniga) {
                0 -> knigaName = "От Матфея"
                1 -> knigaName = "От Марка"
                2 -> knigaName = "От Луки"
                3 -> knigaName = "От Иоанна"
                4 -> knigaName = "Деяния святых апостолов"
                5 -> knigaName = "Иакова"
                6 -> knigaName = "1-е Петра"
                7 -> knigaName = "2-е Петра"
                8 -> knigaName = "1-е Иоанна"
                9 -> knigaName = "2-е Иоанна"
                10 -> knigaName = "3-е Иоанна"
                11 -> knigaName = "Иуды"
                12 -> knigaName = "Римлянам"
                13 -> knigaName = "1-е Коринфянам"
                14 -> knigaName = "2-е Коринфянам"
                15 -> knigaName = "Галатам"
                16 -> knigaName = "Ефесянам"
                17 -> knigaName = "Филиппийцам"
                18 -> knigaName = "Колоссянам"
                19 -> knigaName = "1-е Фессалоникийцам (Солунянам)"
                20 -> knigaName = "2-е Фессалоникийцам (Солунянам)"
                21 -> knigaName = "1-е Тимофею"
                22 -> knigaName = "2-е Тимофею"
                23 -> knigaName = "Титу"
                24 -> knigaName = "Филимону"
                25 -> knigaName = "Евреям"
                26 -> knigaName = "Откровение (Апокалипсис)"
            }
            return knigaName
        }

    internal interface ClicParalelListiner {
        fun setOnClic(cytanneParalelnye: String?, cytanneSours: String?)
    }

    internal interface ListPositionListiner {
        fun getListPosition(position: Int)
        fun setEdit(edit: Boolean = false)
    }

    override fun onBackPressedFragment() {
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        activity?.let {
            val animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
            if (linearLayout6.visibility == View.VISIBLE) {
                linearLayout4.visibility = View.GONE
                linearLayout6.animation = animation
                linearLayout6.visibility = View.GONE
            } else if (linearLayout4.visibility == View.VISIBLE) {
                linearLayout4.animation = animation
                linearLayout4.visibility = View.GONE
            }
            if (linearLayout5.visibility == View.VISIBLE) {
                linearLayout5.animation = animation
                linearLayout5.visibility = View.GONE
                spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun addZakladka(color: Int) {
        activity?.let {
            if (color != -1) {
                BibleGlobalList.zakladkiSinodal.add(0, BibleZakladkiData(BibleGlobalList.zakladkiSinodal.size.toLong(), knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString() + "<!--" + color))
                MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
            }
            BibleGlobalList.mPedakVisable = false
            listPositionListiner?.setEdit(true)
            BibleGlobalList.bibleCopyList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    override fun addNatatka() {
        BibleGlobalList.bibleCopyList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            clicParalelListiner = context as ClicParalelListiner
            listPositionListiner = context as ListPositionListiner
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        kniga = arguments?.getInt("kniga") ?: 0
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pazicia") ?: 0
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        BibleGlobalList.mPedakVisable = true
        activity?.let { activity ->
            if (linearLayout4.visibility == View.GONE) {
                linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
                linearLayout4.visibility = View.VISIBLE
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
            if (BibleGlobalList.bibleCopyList.size > 1) {
                linearLayout6.visibility = View.VISIBLE
                if (linearLayout5.visibility == View.VISIBLE) {
                    linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout5.visibility = View.GONE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                }
                if (BibleGlobalList.bibleCopyList.size == bible.size) copyBigFull.visibility = View.GONE
                else copyBigFull.visibility = View.VISIBLE
            } else {
                linearLayout6.visibility = View.GONE
            }
        }
        return true
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!BibleGlobalList.mPedakVisable) {
            BibleGlobalList.bibleCopyList.clear()
            val parallel = BibliaParallelChtenia()
            var res = "+-+"
            var knigaName = ""
            var clic = false
            if (kniga == 0) {
                knigaName = "Мф"
                res = parallel.kniga51(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 1) {
                knigaName = "Мк"
                res = parallel.kniga52(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 2) {
                knigaName = "Лк"
                res = parallel.kniga53(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 3) {
                knigaName = "Ин"
                res = parallel.kniga54(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 4) {
                knigaName = "Деян"
                res = parallel.kniga55(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 5) {
                knigaName = "Иак"
                res = parallel.kniga56(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 6) {
                knigaName = "1 Пет"
                res = parallel.kniga57(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 7) {
                knigaName = "2 Пет"
                res = parallel.kniga58(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 8) {
                knigaName = "1 Ин"
                res = parallel.kniga59(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 9) {
                knigaName = "2 Ин"
                res = parallel.kniga60(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 10) {
                knigaName = "3 Ин"
                res = parallel.kniga61(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 11) {
                knigaName = "Иуд"
                res = parallel.kniga62(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 12) {
                knigaName = "Рим"
                res = parallel.kniga63(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 13) {
                knigaName = "1 Кор"
                res = parallel.kniga64(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 14) {
                knigaName = "2 Кор"
                res = parallel.kniga65(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 15) {
                knigaName = "Гал"
                res = parallel.kniga66(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 16) {
                knigaName = "Еф"
                res = parallel.kniga67(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 17) {
                knigaName = "Флп"
                res = parallel.kniga68(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 18) {
                knigaName = "Кол"
                res = parallel.kniga69(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 19) {
                knigaName = "1 Фес"
                res = parallel.kniga70(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 20) {
                knigaName = "2 Фес"
                res = parallel.kniga71(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 21) {
                knigaName = "1 Тим"
                res = parallel.kniga72(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 22) {
                knigaName = "2 Тим"
                res = parallel.kniga73(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 23) {
                knigaName = "Тит"
                res = parallel.kniga74(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 24) {
                knigaName = "Флм"
                res = parallel.kniga75(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 25) {
                knigaName = "Евр"
                res = parallel.kniga76(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 26) {
                knigaName = "Откр"
                res = parallel.kniga77(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (clic) {
                clicParalelListiner?.setOnClic(res, knigaName + " " + (page + 1) + ":" + (position + 1))
            }
        } else {
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
        activity?.let {
            if (BibleGlobalList.bibleCopyList.size > 1) {
                linearLayout6.visibility = View.VISIBLE
                if (linearLayout5.visibility == View.VISIBLE) {
                    linearLayout5.animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout5.visibility = View.GONE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                }
                if (BibleGlobalList.bibleCopyList.size == bible.size) copyBigFull.visibility = View.GONE
                else copyBigFull.visibility = View.VISIBLE
            } else {
                linearLayout6.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        linearLayout4.visibility = View.GONE
        linearLayout6.visibility = View.GONE
        linearLayout5.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_bible_page_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(NovyZapavietSinaidal.fierstPosition)
        listView.onItemLongClickListener = this
        listView.onItemClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPositionListiner?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
        var inputStream = resources.openRawResource(R.raw.sinaidaln1)
        when (kniga) {
            0 -> inputStream = resources.openRawResource(R.raw.sinaidaln1)
            1 -> inputStream = resources.openRawResource(R.raw.sinaidaln2)
            2 -> inputStream = resources.openRawResource(R.raw.sinaidaln3)
            3 -> inputStream = resources.openRawResource(R.raw.sinaidaln4)
            4 -> inputStream = resources.openRawResource(R.raw.sinaidaln5)
            5 -> inputStream = resources.openRawResource(R.raw.sinaidaln6)
            6 -> inputStream = resources.openRawResource(R.raw.sinaidaln7)
            7 -> inputStream = resources.openRawResource(R.raw.sinaidaln8)
            8 -> inputStream = resources.openRawResource(R.raw.sinaidaln9)
            9 -> inputStream = resources.openRawResource(R.raw.sinaidaln10)
            10 -> inputStream = resources.openRawResource(R.raw.sinaidaln11)
            11 -> inputStream = resources.openRawResource(R.raw.sinaidaln12)
            12 -> inputStream = resources.openRawResource(R.raw.sinaidaln13)
            13 -> inputStream = resources.openRawResource(R.raw.sinaidaln14)
            14 -> inputStream = resources.openRawResource(R.raw.sinaidaln15)
            15 -> inputStream = resources.openRawResource(R.raw.sinaidaln16)
            16 -> inputStream = resources.openRawResource(R.raw.sinaidaln17)
            17 -> inputStream = resources.openRawResource(R.raw.sinaidaln18)
            18 -> inputStream = resources.openRawResource(R.raw.sinaidaln19)
            19 -> inputStream = resources.openRawResource(R.raw.sinaidaln20)
            20 -> inputStream = resources.openRawResource(R.raw.sinaidaln21)
            21 -> inputStream = resources.openRawResource(R.raw.sinaidaln22)
            22 -> inputStream = resources.openRawResource(R.raw.sinaidaln23)
            23 -> inputStream = resources.openRawResource(R.raw.sinaidaln24)
            24 -> inputStream = resources.openRawResource(R.raw.sinaidaln25)
            25 -> inputStream = resources.openRawResource(R.raw.sinaidaln26)
            26 -> inputStream = resources.openRawResource(R.raw.sinaidaln27)
        }
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val split = reader.readText().split("===").toTypedArray()
        inputStream.close()
        val bibleline = split[page + 1].split("\n").toTypedArray()
        bibleline.forEach {
            if (it.trim() != "") bible.add(it)
        }
        activity?.let { activity ->
            adapter = ExpArrayAdapterParallel(activity, bible, kniga, page, true, 2)
            listView.divider = null
            listView.adapter = adapter
            listView.setSelection(pazicia)
            listView.isVerticalScrollBarEnabled = false
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getBoolean("dzen_noch", false)) {
                adpravit.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
                copyBig.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
                copyBigFull.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
                linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
                linearLayout5.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
                linearLayout6.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
                listView.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
            }
            copyBigFull.setOnClickListener {
                BibleGlobalList.bibleCopyList.clear()
                bible.forEachIndexed { index, _ ->
                    BibleGlobalList.bibleCopyList.add(index)
                }
                adapter.notifyDataSetChanged()
                copyBigFull.visibility = View.GONE
            }
            zametka_natatka.setOnClickListener {
                val copyString = StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
                    copyString.append("${bible[it]}<br>")
                }
                val clip = copyString.toString().trim()
                fragmentManager?.let {
                    val dialog = DialogAddNatatka.getInstance(clip)
                    dialog.show(it, "DialogAddNatatka")
                }
                //MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.copy))
                linearLayout4.visibility = View.GONE
                linearLayout6.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                linearLayout6.visibility = View.GONE
                linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                linearLayout5.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            }
            zametkaBig.setOnClickListener {
                val copyString = StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
                    copyString.append("${bible[it]}<br>")
                }
                val clip = copyString.toString().trim()
                fragmentManager?.let {
                    val dialog = DialogAddNatatka.getInstance(clip)
                    dialog.show(it, "DialogAddNatatka")
                }
                //MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.copy))
                linearLayout4.visibility = View.GONE
                linearLayout6.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                linearLayout6.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            }
            copyBig.setOnClickListener {
                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyString = java.lang.StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
                    copyString.append("${bible[it]}<br>")
                }
                val clip = ClipData.newPlainText("", MainActivity.fromHtml(copyString.toString()).toString().trim())
                clipboard.setPrimaryClip(clip)
                MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.copy))
                linearLayout4.visibility = View.GONE
                linearLayout6.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                linearLayout6.visibility = View.GONE
                BibleGlobalList.mPedakVisable = false
                BibleGlobalList.bibleCopyList.clear()
                adapter.notifyDataSetChanged()
            }
            adpravit.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val copyString = java.lang.StringBuilder()
                    BibleGlobalList.bibleCopyList.sort()
                    BibleGlobalList.bibleCopyList.forEach {
                        copyString.append("${bible[it]}<br>")
                    }
                    val share = MainActivity.fromHtml(copyString.toString()).toString().trim()
                    val clip = ClipData.newPlainText("", share)
                    clipboard.setPrimaryClip(clip)
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, share)
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, null))
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            yelloy.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val i = BibleGlobalList.checkPosition()
                    if (i != -1) {
                        if (BibleGlobalList.vydelenie[i][2] == 0) {
                            BibleGlobalList.vydelenie[i][2] = 1
                        } else {
                            BibleGlobalList.vydelenie[i][2] = 0
                        }
                    } else {
                        val setVydelenie = ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(1)
                        setVydelenie.add(0)
                        setVydelenie.add(0)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            underline.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val i = BibleGlobalList.checkPosition()
                    if (i != -1) {
                        if (BibleGlobalList.vydelenie[i][3] == 0) {
                            BibleGlobalList.vydelenie[i][3] = 1
                        } else {
                            BibleGlobalList.vydelenie[i][3] = 0
                        }
                    } else {
                        val setVydelenie = ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(0)
                        setVydelenie.add(1)
                        setVydelenie.add(0)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            bold.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val i = BibleGlobalList.checkPosition()
                    if (i != -1) {
                        if (BibleGlobalList.vydelenie[i][4] == 0) {
                            BibleGlobalList.vydelenie[i][4] = 1
                        } else {
                            BibleGlobalList.vydelenie[i][4] = 0
                        }
                    } else {
                        val setVydelenie = ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(0)
                        setVydelenie.add(0)
                        setVydelenie.add(1)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            zakladka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    var index = -1
                    for (i in BibleGlobalList.zakladkiSinodal.indices) {
                        if (BibleGlobalList.zakladkiSinodal[i].data.contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            index = i
                            break
                        }
                    }
                    if (index == -1) {
                        fragmentManager?.let {
                            val dialog = DialogAddZakladka()
                            dialog.show(it, "DialogAddZakladka")
                        }
                    } else {
                        BibleGlobalList.zakladkiSinodal.removeAt(index)
                        BibleGlobalList.mPedakVisable = false
                        listPositionListiner?.setEdit(true)
                        BibleGlobalList.bibleCopyList.clear()
                    }
                    linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout4.visibility = View.GONE
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            zametka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (BibleGlobalList.bibleCopyList[0] + 1)
                    fragmentManager?.let { fragmentManager ->
                        val natatka = DialogBibleNatatka.getInstance(semuxa = false, novyzavet = true, kniga = kniga, bibletext = knigaName)
                        natatka.show(fragmentManager, "bible_natatka")
                    }
                    linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    listPositionListiner?.setEdit(true)
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            share.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                    sendIntent.type = "text/plain"
                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("", MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                    clipboard.setPrimaryClip(clip)
                    startActivity(Intent.createChooser(sendIntent, null))
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            copy.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("", MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                    clipboard.setPrimaryClip(clip)
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.copy))
                    linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.bibleCopyList.clear()
                    BibleGlobalList.mPedakVisable = false
                    linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout5.visibility = View.GONE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            fullCopy.setOnClickListener {
                BibleGlobalList.bibleCopyList.clear()
                bible.forEachIndexed { index, _ ->
                    BibleGlobalList.bibleCopyList.add(index)
                }
                adapter.notifyDataSetChanged()
                copyBigFull.visibility = View.GONE
                linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                linearLayout5.visibility = View.GONE
                linearLayout6.visibility = View.VISIBLE
                spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
            }
            spinnerCopy.setOnClickListener {
                if (linearLayout5.visibility == View.GONE) {
                    linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
                    linearLayout5.visibility = View.VISIBLE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_up_float_bible)
                } else {
                    linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout5.visibility = View.GONE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                }
            }
        }
    }

    companion object {
        fun newInstance(page: Int, kniga: Int, pazicia: Int): NovyZapavietSinaidalFragment {
            val fragmentFirst = NovyZapavietSinaidalFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}