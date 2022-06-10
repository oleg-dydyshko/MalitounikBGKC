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
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.resources.databinding.ActivityBiblePageFragmentBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class StaryZapavietSinaidalFragment : Fragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var bibleListiner: BibleListiner? = null
    private lateinit var adapter: BibleArrayAdapterParallel
    private var bible: ArrayList<String> = ArrayList()
    private var knigaBible = ""
    private var _binding: ActivityBiblePageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun upDateListView() {
        adapter.notifyDataSetChanged()
    }

    fun onBackPressedFragment() {
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        activity?.let {
            if (binding.linearLayout4.visibility == View.VISIBLE) {
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                binding.linearLayout4.visibility = View.GONE
                bibleListiner?.isPanelVisible(false)
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun addZakladka(color: Int) {
        if (color != -1) {
            var maxIndex: Long = 0
            BibleGlobalList.zakladkiSinodal.forEach {
                if (maxIndex < it.id) maxIndex = it.id
            }
            maxIndex++
            BibleGlobalList.zakladkiSinodal.add(0, BibleZakladkiData(maxIndex, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString() + "<!--" + color))
            MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
        }
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        adapter.notifyDataSetChanged()
    }

    fun addNatatka() {
        BibleGlobalList.bibleCopyList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            bibleListiner = context as BibleListiner
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        knigaBible = arguments?.getString("title") ?: ""
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
                bibleListiner?.isPanelVisible(true)
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
                knigaName = "2 Езд"
                res = parallel.kniga17(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 17) {
                knigaName = "Тов"
                res = parallel.kniga18(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 18) {
                knigaName = "Иудифь"
                res = parallel.kniga19(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 19) {
                knigaName = "Есф"
                res = parallel.kniga20(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 20) {
                knigaName = "Иов"
                res = parallel.kniga21(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 21) {
                knigaName = "Пс"
                res = parallel.kniga22(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 22) {
                knigaName = "Притч"
                res = parallel.kniga23(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 23) {
                knigaName = "Еккл"
                res = parallel.kniga24(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 24) {
                knigaName = "Песн"
                res = parallel.kniga25(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 25) {
                knigaName = "Прем"
                res = parallel.kniga26(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 26) {
                knigaName = "Сир"
                res = parallel.kniga27(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 27) {
                knigaName = "Ис"
                res = parallel.kniga28(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 28) {
                knigaName = "Иер"
                res = parallel.kniga29(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 29) {
                knigaName = "Плач Иер"
                res = parallel.kniga30(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 30) {
                knigaName = "Посл Иеремии"
                res = parallel.kniga31(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 31) {
                knigaName = "Вар"
                res = parallel.kniga32(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 32) {
                knigaName = "Иез"
                res = parallel.kniga33(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 33) {
                knigaName = "Дан"
                res = parallel.kniga34(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 34) {
                knigaName = "Ос"
                res = parallel.kniga35(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 35) {
                knigaName = "Иоиль"
                res = parallel.kniga36(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 36) {
                knigaName = "Ам"
                res = parallel.kniga37(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 37) {
                knigaName = "Авдий"
                res = parallel.kniga38(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 38) {
                knigaName = "Иона"
                res = parallel.kniga39(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 39) {
                knigaName = "Мих"
                res = parallel.kniga40(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 40) {
                knigaName = "Наум"
                res = parallel.kniga41(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 41) {
                knigaName = "Аввакум"
                res = parallel.kniga42(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 42) {
                knigaName = "Сафония"
                res = parallel.kniga43(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 43) {
                knigaName = "Аггей"
                res = parallel.kniga44(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 44) {
                knigaName = "Зах"
                res = parallel.kniga45(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 45) {
                knigaName = "Мал"
                res = parallel.kniga46(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 46) {
                knigaName = "1 Макк"
                res = parallel.kniga47(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 47) {
                knigaName = "2 Макк"
                res = parallel.kniga48(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 48) {
                knigaName = "3 Макк"
                res = parallel.kniga49(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (kniga == 49) {
                knigaName = "3 Езд"
                res = parallel.kniga50(page + 1, position + 1)
                if (!res.contains("+-+")) clic = true
            }
            if (clic) {
                bibleListiner?.setOnClic(res, knigaName + " " + (page + 1) + ":" + (position + 1))
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
        bibleListiner?.isPanelVisible(false)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityBiblePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listView.onItemLongClickListener = this
        binding.listView.onItemClickListener = this
        binding.listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                bibleListiner?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            }
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
        val split = reader.readText().split("===")
        inputStream.close()
        val bibleline = split[page + 1].split("\n")
        bibleline.forEach {
            if (it.trim() != "") bible.add(it)
        }
        activity?.let { activity ->
            adapter = BibleArrayAdapterParallel(activity, bible, kniga, page, false, 2)
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
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.copy))
                    binding.linearLayout4.visibility = View.GONE
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    bibleListiner?.isPanelVisible(false)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                        val setVydelenie = ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(1)
                        setVydelenie.add(0)
                        setVydelenie.add(0)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible(false)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                        val setVydelenie = ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(0)
                        setVydelenie.add(1)
                        setVydelenie.add(0)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible(false)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
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
                        val setVydelenie = ArrayList<Int>()
                        setVydelenie.add(BibleGlobalList.mListGlava)
                        setVydelenie.add(BibleGlobalList.bibleCopyList[0])
                        setVydelenie.add(0)
                        setVydelenie.add(0)
                        setVydelenie.add(1)
                        BibleGlobalList.vydelenie.add(setVydelenie)
                    }
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible(false)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.zakladka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    var index = -1
                    for (i in BibleGlobalList.zakladkiSinodal.indices) {
                        if (BibleGlobalList.zakladkiSinodal[i].data.contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            index = i
                            break
                        }
                    }
                    if (index == -1) {
                        val dialog = DialogAddZakladka()
                        dialog.show(childFragmentManager, "DialogAddZakladka")
                    } else {
                        BibleGlobalList.zakladkiSinodal.removeAt(index)
                        BibleGlobalList.mPedakVisable = false
                        BibleGlobalList.bibleCopyList.clear()
                    }
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible(false)
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.zametka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.rsinaidal) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_ru) + " " + (BibleGlobalList.bibleCopyList[0] + 1)
                    val natatka = DialogBibleNatatka.getInstance(semuxa = false, novyzavet = false, kniga = kniga, bibletext = knigaName)
                    natatka.show(childFragmentManager, "bible_natatka")
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible(false)
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    MainActivity.toastView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
        }
    }

    companion object {
        fun newInstance(title: String, page: Int, kniga: Int, pazicia: Int): StaryZapavietSinaidalFragment {
            val fragmentFirst = StaryZapavietSinaidalFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}