package by.carkva_gazeta.resources

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import kotlinx.android.synthetic.main.activity_bible_page_fragment.*
import java.io.BufferedReader
import java.io.InputStreamReader

class StaryZapavietSemuxaFragment : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var knigaReal = 0
    private var clicParalelListiner: ClicParalelListiner? = null
    private var listPositionListiner: ListPositionListiner? = null
    private lateinit var adapter: ExpArrayAdapterParallel
    private var bible: ArrayList<String> = ArrayList()
    private val knigaBible: String
        get() {
            var knigaName = ""
            when (kniga) {
                0 -> knigaName = "Быцьцё"
                1 -> knigaName = "Выхад"
                2 -> knigaName = "Лявіт"
                3 -> knigaName = "Лікі"
                4 -> knigaName = "Другі Закон"
                5 -> knigaName = "Ісуса сына Нава"
                6 -> knigaName = "Судзьдзяў"
                7 -> knigaName = "Рут"
                8 -> knigaName = "1-я Царстваў"
                9 -> knigaName = "2-я Царстваў"
                10 -> knigaName = "3-я Царстваў"
                11 -> knigaName = "4-я Царстваў"
                12 -> knigaName = "1-я Летапісаў"
                13 -> knigaName = "2-я Летапісаў"
                14 -> knigaName = "Эздры"
                15 -> knigaName = "Нээміі"
                16 -> knigaName = "Эстэр"
                17 -> knigaName = "Ёва"
                18 -> knigaName = "Псалтыр"
                19 -> knigaName = "Выслоўяў Саламонавых"
                20 -> knigaName = "Эклезіяста"
                21 -> knigaName = "Найвышэйшая Песьня Саламонава"
                22 -> knigaName = "Ісаі"
                23 -> knigaName = "Ераміі"
                24 -> knigaName = "Ераміін Плач"
                25 -> knigaName = "Езэкііля"
                26 -> knigaName = "Данііла"
                27 -> knigaName = "Асіі"
                28 -> knigaName = "Ёіля"
                29 -> knigaName = "Амоса"
                30 -> knigaName = "Аўдзея"
                31 -> knigaName = "Ёны"
                32 -> knigaName = "Міхея"
                33 -> knigaName = "Навума"
                34 -> knigaName = "Абакума"
                35 -> knigaName = "Сафона"
                36 -> knigaName = "Агея"
                37 -> knigaName = "Захарыі"
                38 -> knigaName = "Малахіі"
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
        linearLayout4.visibility = View.GONE
        adapter.notifyDataSetChanged()
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
        if (linearLayout4.visibility == View.GONE) {
            BibleGlobalList.bibleCopyList.add(position)
            linearLayout4.visibility = View.VISIBLE
            copyBig.visibility = View.GONE
            copyBigFull.visibility = View.GONE
            spinnerCopy.visibility = View.VISIBLE
            adpravit.visibility = View.GONE
            yelloy.visibility = View.VISIBLE
            underline.visibility = View.VISIBLE
            bold.visibility = View.VISIBLE
            zakladka.visibility = View.VISIBLE
            zametka.visibility = View.VISIBLE
            BibleGlobalList.mPedakVisable = true
        } else {
            if (BibleGlobalList.mPedakVisable) {
                var find = false
                BibleGlobalList.bibleCopyList.forEach {
                    if (it == position)
                        find = true
                }
                if (find) {
                    BibleGlobalList.bibleCopyList.remove(position)
                } else {
                    BibleGlobalList.bibleCopyList.add(position)
                }
                adapter.notifyDataSetChanged()
                if (BibleGlobalList.bibleCopyList.size == bible.size)
                    copyBigFull.visibility = View.GONE
                else
                    copyBigFull.visibility = View.VISIBLE
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
                knigaName = "Быт"
                res = parallel.kniga1(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 1) {
                knigaName = "Исх"
                res = parallel.kniga2(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 2) {
                knigaName = "Лев"
                res = parallel.kniga3(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 3) {
                knigaName = "Числа"
                res = parallel.kniga4(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 4) {
                knigaName = "Втор"
                res = parallel.kniga5(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 5) {
                knigaName = "Нав"
                res = parallel.kniga6(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 6) {
                knigaName = "Суд"
                res = parallel.kniga7(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 7) {
                knigaName = "Руфь"
                res = parallel.kniga8(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 8) {
                knigaName = "1 Цар"
                res = parallel.kniga9(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 9) {
                knigaName = "2 Цар"
                res = parallel.kniga10(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 10) {
                knigaName = "3 Цар"
                res = parallel.kniga11(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 11) {
                knigaName = "4 Цар"
                res = parallel.kniga12(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 12) {
                knigaName = "1 Пар"
                res = parallel.kniga13(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 13) {
                knigaName = "2 Пар"
                res = parallel.kniga14(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 14) {
                knigaName = "1 Езд"
                res = parallel.kniga15(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 15) {
                knigaName = "Неем"
                res = parallel.kniga16(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 16) {
                knigaName = "Есф"
                res = parallel.kniga20(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 17) {
                knigaName = "Иов"
                res = parallel.kniga21(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 18) {
                knigaName = "Пс"
                res = parallel.kniga22(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 19) {
                knigaName = "Притч"
                res = parallel.kniga23(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 20) {
                knigaName = "Еккл"
                res = parallel.kniga24(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 21) {
                knigaName = "Песн"
                res = parallel.kniga25(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 22) {
                knigaName = "Ис"
                res = parallel.kniga28(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 23) {
                knigaName = "Иер"
                res = parallel.kniga29(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 24) {
                knigaName = "Плач Иер"
                res = parallel.kniga30(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 25) {
                knigaName = "Иез"
                res = parallel.kniga33(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 26) {
                knigaName = "Дан"
                res = parallel.kniga34(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 27) {
                knigaName = "Ос"
                res = parallel.kniga35(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 28) {
                knigaName = "Иоиль"
                res = parallel.kniga36(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 29) {
                knigaName = "Ам"
                res = parallel.kniga37(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 30) {
                knigaName = "Авдий"
                res = parallel.kniga38(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 31) {
                knigaName = "Иона"
                res = parallel.kniga39(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 32) {
                knigaName = "Мих"
                res = parallel.kniga40(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 33) {
                knigaName = "Наум"
                res = parallel.kniga41(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 34) {
                knigaName = "Аввакум"
                res = parallel.kniga42(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 35) {
                knigaName = "Сафония"
                res = parallel.kniga43(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 36) {
                knigaName = "Аггей"
                res = parallel.kniga44(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 37) {
                knigaName = "Зах"
                res = parallel.kniga45(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 38) {
                knigaName = "Мал"
                res = parallel.kniga46(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (clic) {
                clicParalelListiner?.setOnClic(res, knigaName + " " + (page + 1) + ":" + (position + 1))
            }
        } else {
            var find = false
            BibleGlobalList.bibleCopyList.forEach {
                if (it == position)
                    find = true
            }
            if (find) {
                BibleGlobalList.bibleCopyList.remove(position)
            } else {
                BibleGlobalList.bibleCopyList.add(position)
            }
            adapter.notifyDataSetChanged()
        }
        if (BibleGlobalList.mPedakVisable) {
            if (BibleGlobalList.bibleCopyList.size > 1) {
                copyBig.visibility = View.VISIBLE
                adpravit.visibility = View.VISIBLE
                spinnerCopy.visibility = View.GONE
                yelloy.visibility = View.GONE
                underline.visibility = View.GONE
                bold.visibility = View.GONE
                zakladka.visibility = View.GONE
                zametka.visibility = View.GONE
                if (BibleGlobalList.bibleCopyList.size == bible.size)
                    copyBigFull.visibility = View.GONE
                else
                    copyBigFull.visibility = View.VISIBLE
            } else {
                copyBig.visibility = View.GONE
                copyBigFull.visibility = View.GONE
                adpravit.visibility = View.GONE
                spinnerCopy.visibility = View.VISIBLE
                yelloy.visibility = View.VISIBLE
                underline.visibility = View.VISIBLE
                bold.visibility = View.VISIBLE
                zakladka.visibility = View.VISIBLE
                zametka.visibility = View.VISIBLE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        linearLayout4.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_bible_page_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(StaryZapavietSemuxa.fierstPosition)
        listView.onItemLongClickListener = this
        listView.onItemClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPositionListiner?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
        var inputStream = resources.openRawResource(R.raw.biblias1)
        when (kniga) {
            0 -> {
                knigaReal = 0
                inputStream = resources.openRawResource(R.raw.biblias1)
            }
            1 -> {
                knigaReal = 1
                inputStream = resources.openRawResource(R.raw.biblias2)
            }
            2 -> {
                knigaReal = 2
                inputStream = resources.openRawResource(R.raw.biblias3)
            }
            3 -> {
                knigaReal = 3
                inputStream = resources.openRawResource(R.raw.biblias4)
            }
            4 -> {
                knigaReal = 4
                inputStream = resources.openRawResource(R.raw.biblias5)
            }
            5 -> {
                knigaReal = 5
                inputStream = resources.openRawResource(R.raw.biblias6)
            }
            6 -> {
                knigaReal = 6
                inputStream = resources.openRawResource(R.raw.biblias7)
            }
            7 -> {
                knigaReal = 7
                inputStream = resources.openRawResource(R.raw.biblias8)
            }
            8 -> {
                knigaReal = 8
                inputStream = resources.openRawResource(R.raw.biblias9)
            }
            9 -> {
                knigaReal = 9
                inputStream = resources.openRawResource(R.raw.biblias10)
            }
            10 -> {
                knigaReal = 10
                inputStream = resources.openRawResource(R.raw.biblias11)
            }
            11 -> {
                knigaReal = 11
                inputStream = resources.openRawResource(R.raw.biblias12)
            }
            12 -> {
                knigaReal = 12
                inputStream = resources.openRawResource(R.raw.biblias13)
            }
            13 -> {
                knigaReal = 13
                inputStream = resources.openRawResource(R.raw.biblias14)
            }
            14 -> {
                knigaReal = 14
                inputStream = resources.openRawResource(R.raw.biblias15)
            }
            15 -> {
                knigaReal = 15
                inputStream = resources.openRawResource(R.raw.biblias16)
            }
            16 -> {
                knigaReal = 19
                inputStream = resources.openRawResource(R.raw.biblias17)
            }
            17 -> {
                knigaReal = 20
                inputStream = resources.openRawResource(R.raw.biblias18)
            }
            18 -> {
                knigaReal = 21
                inputStream = resources.openRawResource(R.raw.biblias19)
            }
            19 -> {
                knigaReal = 22
                inputStream = resources.openRawResource(R.raw.biblias20)
            }
            20 -> {
                knigaReal = 23
                inputStream = resources.openRawResource(R.raw.biblias21)
            }
            21 -> {
                knigaReal = 24
                inputStream = resources.openRawResource(R.raw.biblias22)
            }
            22 -> {
                knigaReal = 27
                inputStream = resources.openRawResource(R.raw.biblias23)
            }
            23 -> {
                knigaReal = 28
                inputStream = resources.openRawResource(R.raw.biblias24)
            }
            24 -> {
                knigaReal = 29
                inputStream = resources.openRawResource(R.raw.biblias25)
            }
            25 -> {
                knigaReal = 32
                inputStream = resources.openRawResource(R.raw.biblias26)
            }
            26 -> {
                knigaReal = 33
                inputStream = resources.openRawResource(R.raw.biblias27)
            }
            27 -> {
                knigaReal = 34
                inputStream = resources.openRawResource(R.raw.biblias28)
            }
            28 -> {
                knigaReal = 35
                inputStream = resources.openRawResource(R.raw.biblias29)
            }
            29 -> {
                knigaReal = 36
                inputStream = resources.openRawResource(R.raw.biblias30)
            }
            30 -> {
                knigaReal = 37
                inputStream = resources.openRawResource(R.raw.biblias31)
            }
            31 -> {
                knigaReal = 38
                inputStream = resources.openRawResource(R.raw.biblias32)
            }
            32 -> {
                knigaReal = 39
                inputStream = resources.openRawResource(R.raw.biblias33)
            }
            33 -> {
                knigaReal = 40
                inputStream = resources.openRawResource(R.raw.biblias34)
            }
            34 -> {
                knigaReal = 41
                inputStream = resources.openRawResource(R.raw.biblias35)
            }
            35 -> {
                knigaReal = 42
                inputStream = resources.openRawResource(R.raw.biblias36)
            }
            36 -> {
                knigaReal = 43
                inputStream = resources.openRawResource(R.raw.biblias37)
            }
            37 -> {
                knigaReal = 44
                inputStream = resources.openRawResource(R.raw.biblias38)
            }
            38 -> {
                knigaReal = 45
                inputStream = resources.openRawResource(R.raw.biblias39)
            }
        }
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        reader.forEachLine {
            line = it
            if (line.contains("//")) {
                val t1 = line.indexOf("//")
                line = line.substring(0, t1).trim()
                if (line != "") builder.append(line).append("\n")
            } else {
                builder.append(line).append("\n")
            }
        }
        inputStream.close()
        val split = builder.toString().split("===").toTypedArray()
        val bibleline = split[page + 1].split("\n").toTypedArray()
        bibleline.forEach {
            if (it.trim() != "")
                bible.add(it)
        }
        activity?.let { activity ->
            adapter = ExpArrayAdapterParallel(activity, bible, knigaReal, page, false, 1)
            listView.divider = null
            listView.adapter = adapter
            listView.setSelection(pazicia)
            listView.isVerticalScrollBarEnabled = false
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getBoolean("dzen_noch", false)) {
                copyBig.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
                copyBigFull.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.knopka_black)
                linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark)
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
            copyBig.setOnClickListener {
                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val copyString = java.lang.StringBuilder()
                BibleGlobalList.bibleCopyList.sort()
                BibleGlobalList.bibleCopyList.forEach {
                    copyString.append("${bible[it]}<br>")
                }
                val clip = ClipData.newPlainText("", MainActivity.fromHtml(copyString.toString()).toString().trim())
                clipboard.setPrimaryClip(clip)
                messageView(getString(by.carkva_gazeta.malitounik.R.string.copy))
                linearLayout4.visibility = View.GONE
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
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                        val setVydelenie = java.util.ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(1)
                        setVydelenie.add(0)
                        setVydelenie.add(0)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                        val setVydelenie = java.util.ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(0)
                        setVydelenie.add(1)
                        setVydelenie.add(0)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                        val setVydelenie = java.util.ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(0)
                        setVydelenie.add(0)
                        setVydelenie.add(1)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            zakladka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    var check = false
                    for (i in BibleGlobalList.zakladkiSemuxa.indices) {
                        if (BibleGlobalList.zakladkiSemuxa[i].contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            check = true
                            break
                        }
                    }
                    if (!check) BibleGlobalList.zakladkiSemuxa.add(0, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                    if (!check)
                        messageView(getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
                    else
                        messageView(getString(by.carkva_gazeta.malitounik.R.string.zakladka_exits))
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    listPositionListiner?.setEdit(true)
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            zametka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    var knigaReal = kniga
                    when (kniga) {
                        16 -> knigaReal = 19
                        17 -> knigaReal = 20
                        18 -> knigaReal = 21
                        19 -> knigaReal = 22
                        20 -> knigaReal = 23
                        21 -> knigaReal = 24
                        22 -> knigaReal = 27
                        23 -> knigaReal = 28
                        24 -> knigaReal = 29
                        25 -> knigaReal = 32
                        26 -> knigaReal = 33
                        27 -> knigaReal = 34
                        28 -> knigaReal = 35
                        29 -> knigaReal = 36
                        30 -> knigaReal = 37
                        31 -> knigaReal = 38
                        32 -> knigaReal = 39
                        33 -> knigaReal = 40
                        34 -> knigaReal = 41
                        35 -> knigaReal = 42
                        36 -> knigaReal = 43
                        37 -> knigaReal = 44
                        38 -> knigaReal = 45
                    }
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.listPosition + 1)
                    fragmentManager?.let { fragmentManager ->
                        val zametka = DialogBibleNatatka.getInstance(semuxa = true, novyzavet = false, kniga = knigaReal, bibletext = knigaName)
                        zametka.show(fragmentManager, "bible_zametka")
                    }
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    listPositionListiner?.setEdit(true)
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            val arrayList = arrayOf(by.carkva_gazeta.malitounik.R.drawable.share_bible, by.carkva_gazeta.malitounik.R.drawable.copy, by.carkva_gazeta.malitounik.R.drawable.select_all)
            spinnerCopy.adapter = SpinnerImageAdapter(activity, arrayList)
            var chekFirst = false
            spinnerCopy.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (savedInstanceState == null && chekFirst) {
                        when (position) {
                            0 -> {
                                if (BibleGlobalList.bibleCopyList.size > 0) {
                                    val sendIntent = Intent()
                                    sendIntent.action = Intent.ACTION_SEND
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                                    sendIntent.type = "text/plain"
                                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("", MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                                    clipboard.setPrimaryClip(clip)
                                    startActivity(Intent.createChooser(sendIntent, null))
                                } else {
                                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                                }
                            }
                            1 -> {
                                if (BibleGlobalList.bibleCopyList.size > 0) {
                                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("", MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                                    clipboard.setPrimaryClip(clip)
                                    messageView(getString(by.carkva_gazeta.malitounik.R.string.copy))
                                    linearLayout4.visibility = View.GONE
                                    BibleGlobalList.bibleCopyList.clear()
                                    BibleGlobalList.mPedakVisable = false
                                } else {
                                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                                }
                            }
                            2 -> {
                                BibleGlobalList.bibleCopyList.clear()
                                bible.forEachIndexed { index, _ ->
                                    BibleGlobalList.bibleCopyList.add(index)
                                }
                                adapter.notifyDataSetChanged()
                                copyBig.visibility = View.VISIBLE
                                copyBigFull.visibility = View.GONE
                                adpravit.visibility = View.VISIBLE
                                spinnerCopy.visibility = View.GONE
                                yelloy.visibility = View.GONE
                                underline.visibility = View.GONE
                                bold.visibility = View.GONE
                                zakladka.visibility = View.GONE
                                zametka.visibility = View.GONE
                            }
                        }
                    }
                    chekFirst = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            })
        }
    }

    private fun messageView(message: String) {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val layout = LinearLayout(activity)
            if (dzenNoch) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            val toast = TextViewRobotoCondensed(activity)
            toast.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorIcons))
            toast.setPadding(realpadding, realpadding, realpadding, realpadding)
            toast.text = message
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            layout.addView(toast)
            val mes = Toast(activity)
            mes.duration = Toast.LENGTH_LONG
            mes.view = layout
            mes.show()
        }
    }

    companion object {
        fun newInstance(page: Int, kniga: Int, pazicia: Int): StaryZapavietSemuxaFragment {
            val fragmentFirst = StaryZapavietSemuxaFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}