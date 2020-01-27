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

class PageFragmentStaryZapaviet : ListFragment(), OnItemLongClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var knigaReal = 0
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
    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        MaranAtaGlobalList.listPosition = position
        MaranAtaGlobalList.bible = bible
        longClicListiner?.onLongClick()
        return true
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        if (!getmPedakVisable()) {
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
            longClicListiner?.onLongClick()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(StaryZapaviet3.fierstPosition)
        listView.onItemLongClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPosition?.getListPosition(view.firstVisiblePosition)
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
        bible.addAll(listOf(*bibleline).subList(1, bibleline.size))
        activity?.let {
            val adapter = ExpArrayAdapterParallel(it, bible, knigaReal, page, false, 1)
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

        fun newInstance(page: Int, kniga: Int, pazicia: Int): PageFragmentStaryZapaviet {
            val fragmentFirst = PageFragmentStaryZapaviet()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}