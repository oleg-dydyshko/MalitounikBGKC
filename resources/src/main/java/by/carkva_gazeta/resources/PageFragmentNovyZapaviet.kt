package by.carkva_gazeta.resources

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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class PageFragmentNovyZapaviet : ListFragment(), OnItemLongClickListener {
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

    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        MaranAtaGlobalList.listPosition = position
        MaranAtaGlobalList.bible = bible
        longClicListiner?.onLongClick()
        return true
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        if (!MaranAtaGlobalList.getmPedakVisable()) {
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
            longClicListiner?.onLongClick()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(NovyZapaviet3.fierstPosition)
        listView.onItemLongClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPosition?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
        var inputStream = resources.openRawResource(R.raw.biblian1)
        when (kniga) {
            0 -> inputStream = resources.openRawResource(R.raw.biblian1)
            1 -> inputStream = resources.openRawResource(R.raw.biblian2)
            2 -> inputStream = resources.openRawResource(R.raw.biblian3)
            3 -> inputStream = resources.openRawResource(R.raw.biblian4)
            4 -> inputStream = resources.openRawResource(R.raw.biblian5)
            5 -> inputStream = resources.openRawResource(R.raw.biblian6)
            6 -> inputStream = resources.openRawResource(R.raw.biblian7)
            7 -> inputStream = resources.openRawResource(R.raw.biblian8)
            8 -> inputStream = resources.openRawResource(R.raw.biblian9)
            9 -> inputStream = resources.openRawResource(R.raw.biblian10)
            10 -> inputStream = resources.openRawResource(R.raw.biblian11)
            11 -> inputStream = resources.openRawResource(R.raw.biblian12)
            12 -> inputStream = resources.openRawResource(R.raw.biblian13)
            13 -> inputStream = resources.openRawResource(R.raw.biblian14)
            14 -> inputStream = resources.openRawResource(R.raw.biblian15)
            15 -> inputStream = resources.openRawResource(R.raw.biblian16)
            16 -> inputStream = resources.openRawResource(R.raw.biblian17)
            17 -> inputStream = resources.openRawResource(R.raw.biblian18)
            18 -> inputStream = resources.openRawResource(R.raw.biblian19)
            19 -> inputStream = resources.openRawResource(R.raw.biblian20)
            20 -> inputStream = resources.openRawResource(R.raw.biblian21)
            21 -> inputStream = resources.openRawResource(R.raw.biblian22)
            22 -> inputStream = resources.openRawResource(R.raw.biblian23)
            23 -> inputStream = resources.openRawResource(R.raw.biblian24)
            24 -> inputStream = resources.openRawResource(R.raw.biblian25)
            25 -> inputStream = resources.openRawResource(R.raw.biblian26)
            26 -> inputStream = resources.openRawResource(R.raw.biblian27)
        }
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line: String
        val builder = StringBuilder()
        reader.forEachLine { it ->
            line = it
            if (line.contains("//")) {
                val t1 = line.indexOf("//")
                line = line.substring(0, t1).trim { it <= ' ' }
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
            val adapter = ExpArrayAdapterParallel(it, bible, kniga, page, true, 1)
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
        fun newInstance(page: Int, kniga: Int, pazicia: Int): PageFragmentNovyZapaviet {
            val fragmentFirst = PageFragmentNovyZapaviet()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}