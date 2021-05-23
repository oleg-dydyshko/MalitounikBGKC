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
import androidx.appcompat.widget.TooltipCompat
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.resources.databinding.ActivityBiblePageFragmentBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class StaryZapavietSemuxaFragment : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var knigaReal = 0
    private var clicParalelListiner: ClicParalelListiner? = null
    private var listPositionListiner: ListPositionListiner? = null
    private lateinit var adapter: BibleArrayAdapterParallel
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
    private var _binding: ActivityBiblePageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            if (binding.linearLayout4.visibility == View.VISIBLE) {
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun addZakladka(color: Int) {
        activity?.let { activity ->
            if (color != -1) {
                var maxIndex: Long = 0
                BibleGlobalList.zakladkiSemuxa.forEach {
                    if (maxIndex < it.id) maxIndex = it.id
                }
                maxIndex++
                BibleGlobalList.zakladkiSemuxa.add(0, BibleZakladkiData(maxIndex, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString() + "<!--" + color))
                MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
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
            if (binding.linearLayout4.visibility == View.GONE) {
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
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

    override fun onPause() {
        super.onPause()
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        binding.linearLayout4.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityBiblePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listView.setSelection(StaryZapavietSemuxa.fierstPosition)
        binding.listView.onItemLongClickListener = this
        binding.listView.onItemClickListener = this
        binding.listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPositionListiner?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            }
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
        val split = builder.toString().split("===")
        val bibleline = split[page + 1].split("\n")
        bibleline.forEach {
            if (it.trim() != "") bible.add(it)
        }
        activity?.let { activity ->
            adapter = BibleArrayAdapterParallel(activity, bible, knigaReal, page, false, 1)
            binding.listView.divider = null
            binding.listView.adapter = adapter
            binding.listView.setSelection(pazicia)
            binding.listView.isVerticalScrollBarEnabled = false
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (k.getBoolean("dzen_noch", false)) {
                binding.linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta)
            }
            TooltipCompat.setTooltipText(binding.copyBigFull, getString(by.carkva_gazeta.malitounik.R.string.copy_big_full))
            TooltipCompat.setTooltipText(binding.copyBig, getString(by.carkva_gazeta.malitounik.R.string.copy_big))
            TooltipCompat.setTooltipText(binding.adpravit, getString(by.carkva_gazeta.malitounik.R.string.share))
            TooltipCompat.setTooltipText(binding.yelloy, getString(by.carkva_gazeta.malitounik.R.string.set_yelloy))
            TooltipCompat.setTooltipText(binding.underline, getString(by.carkva_gazeta.malitounik.R.string.set_underline))
            TooltipCompat.setTooltipText(binding.bold, getString(by.carkva_gazeta.malitounik.R.string.set_bold))
            TooltipCompat.setTooltipText(binding.zakladka, getString(by.carkva_gazeta.malitounik.R.string.set_bookmark))
            TooltipCompat.setTooltipText(binding.zametka, getString(by.carkva_gazeta.malitounik.R.string.natatka_add))
            binding.copyBigFull.setOnClickListener {
                BibleGlobalList.bibleCopyList.clear()
                bible.forEachIndexed { index, _ ->
                    BibleGlobalList.bibleCopyList.add(index)
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
                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val copyString = StringBuilder()
                    BibleGlobalList.bibleCopyList.sort()
                    BibleGlobalList.bibleCopyList.forEach {
                        copyString.append("${bible[it]}<br>")
                    }
                    val clip = ClipData.newPlainText("", MainActivity.fromHtml(copyString.toString()).toString().trim())
                    clipboard.setPrimaryClip(clip)
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.copy))
                    binding.linearLayout4.visibility = View.GONE
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.adpravit.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val copyString = StringBuilder()
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
            binding.yelloy.setOnClickListener {
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
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.underline.setOnClickListener {
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
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.bold.setOnClickListener {
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
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.zakladka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    var index = -1
                    for (i in BibleGlobalList.zakladkiSemuxa.indices) {
                        if (BibleGlobalList.zakladkiSemuxa[i].data.contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            index = i
                            break
                        }
                    }
                    if (index == -1) {
                        val dialog = DialogAddZakladka()
                        dialog.show(childFragmentManager, "DialogAddZakladka")
                    } else {
                        BibleGlobalList.zakladkiSemuxa.removeAt(index)
                        BibleGlobalList.mPedakVisable = false
                        listPositionListiner?.setEdit(true)
                        BibleGlobalList.bibleCopyList.clear()
                    }
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.zametka.setOnClickListener {
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
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1)
                    val zametka = DialogBibleNatatka.getInstance(semuxa = true, novyzavet = false, kniga = knigaReal, bibletext = knigaName)
                    zametka.show(childFragmentManager, "bible_zametka")
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    BibleGlobalList.mPedakVisable = false
                    listPositionListiner?.setEdit(true)
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
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