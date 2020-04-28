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

class StaryZapavietSinaidalFragment : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
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
                0 -> knigaName = "Бытие"
                1 -> knigaName = "Исход"
                2 -> knigaName = "Левит"
                3 -> knigaName = "Числа"
                4 -> knigaName = "Второзаконие"
                5 -> knigaName = "Иисуса Навина"
                6 -> knigaName = "Судей израилевых"
                7 -> knigaName = "Руфи"
                8 -> knigaName = "1-я Царств"
                9 -> knigaName = "2-я Царств"
                10 -> knigaName = "3-я Царств"
                11 -> knigaName = "4-я Царств"
                12 -> knigaName = "1-я Паралипоменон"
                13 -> knigaName = "2-я Паралипоменон"
                14 -> knigaName = "1-я Ездры"
                15 -> knigaName = "Неемии"
                16 -> knigaName = "2-я Ездры"
                17 -> knigaName = "Товита"
                18 -> knigaName = "Иудифи"
                19 -> knigaName = "Есфири"
                20 -> knigaName = "Иова"
                21 -> knigaName = "Псалтирь"
                22 -> knigaName = "Притчи Соломона"
                23 -> knigaName = "Екклезиаста"
                24 -> knigaName = "Песнь песней Соломона"
                25 -> knigaName = "Премудрости Соломона"
                26 -> knigaName = "Премудрости Иисуса, сына Сирахова"
                27 -> knigaName = "Исаии"
                28 -> knigaName = "Иеремии"
                29 -> knigaName = "Плач Иеремии"
                30 -> knigaName = "Послание Иеремии"
                31 -> knigaName = "Варуха"
                32 -> knigaName = "Иезекииля"
                33 -> knigaName = "Даниила"
                34 -> knigaName = "Осии"
                35 -> knigaName = "Иоиля"
                36 -> knigaName = "Амоса"
                37 -> knigaName = "Авдия"
                38 -> knigaName = "Ионы"
                39 -> knigaName = "Михея"
                40 -> knigaName = "Наума"
                41 -> knigaName = "Аввакума"
                42 -> knigaName = "Сафонии"
                43 -> knigaName = "Аггея"
                44 -> knigaName = "Захарии"
                45 -> knigaName = "Малахии"
                46 -> knigaName = "1-я Маккавейская"
                47 -> knigaName = "2-я Маккавейская"
                48 -> knigaName = "3-я Маккавейская"
                49 -> knigaName = "3-я Ездры"
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
        BibleGlobalList.mPedakVisable = true
        linearLayout4.visibility = View.VISIBLE
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
        return true
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!BibleGlobalList.mPedakVisable) {
            BibleGlobalList.bibleCopyList.clear()
            var position1 = position
            val parallel = BibliaParallelChtenia()
            var res = "+-+"
            var knigaName = ""
            var clic = false
            if (kniga == 0) {
                knigaName = "Быт"
                res = parallel.kniga1(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 1) {
                knigaName = "Исх"
                res = parallel.kniga2(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 2) {
                knigaName = "Лев"
                res = parallel.kniga3(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 3) {
                knigaName = "Числа"
                res = parallel.kniga4(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 4) {
                knigaName = "Втор"
                res = parallel.kniga5(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 5) {
                knigaName = "Нав"
                res = parallel.kniga6(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 6) {
                knigaName = "Суд"
                res = parallel.kniga7(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 7) {
                knigaName = "Руфь"
                res = parallel.kniga8(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 8) {
                knigaName = "1 Цар"
                res = parallel.kniga9(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 9) {
                knigaName = "2 Цар"
                res = parallel.kniga10(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 10) {
                knigaName = "3 Цар"
                res = parallel.kniga11(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 11) {
                knigaName = "4 Цар"
                res = parallel.kniga12(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 12) {
                knigaName = "1 Пар"
                res = parallel.kniga13(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 13) {
                knigaName = "2 Пар"
                res = parallel.kniga14(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 14) {
                knigaName = "1 Езд"
                res = parallel.kniga15(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 15) {
                knigaName = "Неем"
                res = parallel.kniga16(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 16) {
                knigaName = "2 Езд"
                res = parallel.kniga17(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 17) {
                knigaName = "Тов"
                res = parallel.kniga18(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 18) {
                knigaName = "Иудифь"
                res = parallel.kniga19(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 19) {
                knigaName = "Есф"
                res = parallel.kniga20(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 20) {
                knigaName = "Иов"
                res = parallel.kniga21(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 21) {
                knigaName = "Пс"
                res = parallel.kniga22(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 22) {
                knigaName = "Притч"
                res = parallel.kniga23(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 23) {
                knigaName = "Еккл"
                res = parallel.kniga24(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 24) {
                knigaName = "Песн"
                res = parallel.kniga25(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 25) {
                knigaName = "Прем"
                res = parallel.kniga26(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 26) {
                knigaName = "Сир"
                res = parallel.kniga27(page + 1, position1 + 1)
                if (page + 1 == 1) position1 -= 8
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 27) {
                knigaName = "Ис"
                res = parallel.kniga28(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 28) {
                knigaName = "Иер"
                res = parallel.kniga29(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 29) {
                knigaName = "Плач Иер"
                res = parallel.kniga30(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 30) {
                knigaName = "Посл Иеремии"
                res = parallel.kniga31(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 31) {
                knigaName = "Вар"
                res = parallel.kniga32(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 32) {
                knigaName = "Иез"
                res = parallel.kniga33(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 33) {
                knigaName = "Дан"
                res = parallel.kniga34(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 34) {
                knigaName = "Ос"
                res = parallel.kniga35(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 35) {
                knigaName = "Иоиль"
                res = parallel.kniga36(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 36) {
                knigaName = "Ам"
                res = parallel.kniga37(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 37) {
                knigaName = "Авдий"
                res = parallel.kniga38(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 38) {
                knigaName = "Иона"
                res = parallel.kniga39(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 39) {
                knigaName = "Мих"
                res = parallel.kniga40(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 40) {
                knigaName = "Наум"
                res = parallel.kniga41(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 41) {
                knigaName = "Аввакум"
                res = parallel.kniga42(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 42) {
                knigaName = "Сафония"
                res = parallel.kniga43(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 43) {
                knigaName = "Аггей"
                res = parallel.kniga44(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 44) {
                knigaName = "Зах"
                res = parallel.kniga45(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 45) {
                knigaName = "Мал"
                res = parallel.kniga46(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 46) {
                knigaName = "1 Макк"
                res = parallel.kniga47(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 47) {
                knigaName = "2 Макк"
                res = parallel.kniga48(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 48) {
                knigaName = "3 Макк"
                res = parallel.kniga49(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 49) {
                knigaName = "3 Езд"
                res = parallel.kniga50(page + 1, position1 + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (clic) {
                clicParalelListiner?.setOnClic(res, knigaName + " " + (page + 1) + ":" + (position1 + 1))
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
        listView.setSelection(StaryZapavietSinaidal.fierstPosition)
        listView.onItemLongClickListener = this
        listView.onItemClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPositionListiner?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
        var inputStream = resources.openRawResource(R.raw.sinaidals1)
        when (kniga) {
            0 -> inputStream = resources.openRawResource(R.raw.sinaidals1)
            1 -> inputStream = resources.openRawResource(R.raw.sinaidals2)
            2 -> inputStream = resources.openRawResource(R.raw.sinaidals3)
            3 -> inputStream = resources.openRawResource(R.raw.sinaidals4)
            4 -> inputStream = resources.openRawResource(R.raw.sinaidals5)
            5 -> inputStream = resources.openRawResource(R.raw.sinaidals6)
            6 -> inputStream = resources.openRawResource(R.raw.sinaidals7)
            7 -> inputStream = resources.openRawResource(R.raw.sinaidals8)
            8 -> inputStream = resources.openRawResource(R.raw.sinaidals9)
            9 -> inputStream = resources.openRawResource(R.raw.sinaidals10)
            10 -> inputStream = resources.openRawResource(R.raw.sinaidals11)
            11 -> inputStream = resources.openRawResource(R.raw.sinaidals12)
            12 -> inputStream = resources.openRawResource(R.raw.sinaidals13)
            13 -> inputStream = resources.openRawResource(R.raw.sinaidals14)
            14 -> inputStream = resources.openRawResource(R.raw.sinaidals15)
            15 -> inputStream = resources.openRawResource(R.raw.sinaidals16)
            16 -> inputStream = resources.openRawResource(R.raw.sinaidals17)
            17 -> inputStream = resources.openRawResource(R.raw.sinaidals18)
            18 -> inputStream = resources.openRawResource(R.raw.sinaidals19)
            19 -> inputStream = resources.openRawResource(R.raw.sinaidals20)
            20 -> inputStream = resources.openRawResource(R.raw.sinaidals21)
            21 -> inputStream = resources.openRawResource(R.raw.sinaidals22)
            22 -> inputStream = resources.openRawResource(R.raw.sinaidals23)
            23 -> inputStream = resources.openRawResource(R.raw.sinaidals24)
            24 -> inputStream = resources.openRawResource(R.raw.sinaidals25)
            25 -> inputStream = resources.openRawResource(R.raw.sinaidals26)
            26 -> inputStream = resources.openRawResource(R.raw.sinaidals27)
            27 -> inputStream = resources.openRawResource(R.raw.sinaidals28)
            28 -> inputStream = resources.openRawResource(R.raw.sinaidals29)
            29 -> inputStream = resources.openRawResource(R.raw.sinaidals30)
            30 -> inputStream = resources.openRawResource(R.raw.sinaidals31)
            31 -> inputStream = resources.openRawResource(R.raw.sinaidals32)
            32 -> inputStream = resources.openRawResource(R.raw.sinaidals33)
            33 -> inputStream = resources.openRawResource(R.raw.sinaidals34)
            34 -> inputStream = resources.openRawResource(R.raw.sinaidals35)
            35 -> inputStream = resources.openRawResource(R.raw.sinaidals36)
            36 -> inputStream = resources.openRawResource(R.raw.sinaidals37)
            37 -> inputStream = resources.openRawResource(R.raw.sinaidals38)
            38 -> inputStream = resources.openRawResource(R.raw.sinaidals39)
            39 -> inputStream = resources.openRawResource(R.raw.sinaidals40)
            40 -> inputStream = resources.openRawResource(R.raw.sinaidals41)
            41 -> inputStream = resources.openRawResource(R.raw.sinaidals42)
            42 -> inputStream = resources.openRawResource(R.raw.sinaidals43)
            43 -> inputStream = resources.openRawResource(R.raw.sinaidals44)
            44 -> inputStream = resources.openRawResource(R.raw.sinaidals45)
            45 -> inputStream = resources.openRawResource(R.raw.sinaidals46)
            46 -> inputStream = resources.openRawResource(R.raw.sinaidals47)
            47 -> inputStream = resources.openRawResource(R.raw.sinaidals48)
            48 -> inputStream = resources.openRawResource(R.raw.sinaidals49)
            49 -> inputStream = resources.openRawResource(R.raw.sinaidals50)
        }
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val split = reader.readText().split("===").toTypedArray()
        inputStream.close()
        val bibleline = split[page + 1].split("\n").toTypedArray()
        bibleline.forEach {
            if (it.trim() != "")
                bible.add(it)
        }
        activity?.let { activity ->
            adapter = ExpArrayAdapterParallel(activity, bible, kniga, page, false, 2)
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
                        val setVydelenie = ArrayList<Int>()
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
                        val setVydelenie = ArrayList<Int>()
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
                        val setVydelenie = ArrayList<Int>()
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
                    for (i in BibleGlobalList.zakladkiSinodal.indices) {
                        if (BibleGlobalList.zakladkiSinodal[i].contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            check = true
                            break
                        }
                    }
                    if (!check) BibleGlobalList.zakladkiSinodal.add(0, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
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
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (BibleGlobalList.bibleCopyList[0] + 1)
                    fragmentManager?.let { fragmentManager ->
                        val natatka = DialogBibleNatatka.getInstance(semuxa = false, novyzavet = false, kniga = kniga, bibletext = knigaName)
                        natatka.show(fragmentManager, "bible_natatka")
                    }
                    linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    listPositionListiner?.setEdit(true)
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            /*val arrayList = arrayOf(by.carkva_gazeta.malitounik.R.drawable.share_bible, by.carkva_gazeta.malitounik.R.drawable.copy, by.carkva_gazeta.malitounik.R.drawable.select_all)
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
            })*/
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
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            layout.addView(toast)
            val mes = Toast(activity)
            mes.duration = Toast.LENGTH_LONG
            mes.view = layout
            mes.show()
        }
    }

    companion object {

        fun newInstance(page: Int, kniga: Int, pazicia: Int): StaryZapavietSinaidalFragment {
            val fragmentFirst = StaryZapavietSinaidalFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}