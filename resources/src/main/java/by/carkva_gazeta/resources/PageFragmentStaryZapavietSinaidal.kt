package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import androidx.fragment.app.ListFragment
import by.carkva_gazeta.malitounik.MaranAtaGlobalList
import by.carkva_gazeta.malitounik.MaranAtaGlobalList.Companion.getmPedakVisable
import java.io.BufferedReader
import java.io.InputStreamReader

class PageFragmentStaryZapavietSinaidal : ListFragment(), OnItemLongClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var clicParalelListiner: ClicParalelListiner? = null
    private var listPosition: ListPosition? = null
    private var longClicListiner: LongClicListiner? = null
    private var bible: ArrayList<String> = ArrayList()

    interface ClicParalelListiner {
        fun setOnClic(cytanneParalelnye: String?, cytanneSours: String?)
    }

    interface ListPosition {
        fun getListPosition(position: Int)
    }

    interface LongClicListiner {
        fun onLongClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            clicParalelListiner = context as ClicParalelListiner
            listPosition = context as ListPosition
            longClicListiner = context as LongClicListiner
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        kniga = arguments?.getInt("kniga") ?: 0
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pazicia") ?: 0
    }

    @SuppressLint("SetTextI18n")
    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        MaranAtaGlobalList.listPosition = position
        MaranAtaGlobalList.bible = bible
        longClicListiner?.onLongClick()
        return true
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        var position1 = position
        super.onListItemClick(l, v, position1, id)
        if (!getmPedakVisable()) {
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
            longClicListiner?.onLongClick()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(StaryZapavietSinaidal3.fierstPosition)
        listView.onItemLongClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPosition?.getListPosition(view.firstVisiblePosition)
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
        activity?.let {
            val adapter = ExpArrayAdapterParallel(it, bible, kniga, page, false, 2)
            listView.divider = null
            listAdapter = adapter
            listView.setSelection(pazicia)
            listView.isVerticalScrollBarEnabled = false
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getBoolean("dzen_noch", false)) listView.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
        }
        //float scale = getResources().getDisplayMetrics().density;
//int dpAsPixels = (int) (scale * 10f);
//getListView().setPadding(dpAsPixels, 0, dpAsPixels, dpAsPixels);
    }

    companion object {

        fun newInstance(page: Int, kniga: Int, pazicia: Int): PageFragmentStaryZapavietSinaidal {
            val fragmentFirst = PageFragmentStaryZapavietSinaidal()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}