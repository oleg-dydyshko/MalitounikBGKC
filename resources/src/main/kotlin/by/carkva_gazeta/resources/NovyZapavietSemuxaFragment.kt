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
import by.carkva_gazeta.malitounik.MainActivity
import kotlinx.android.synthetic.main.activity_bible_page_fragment.*
import java.io.BufferedReader
import java.io.InputStreamReader

class NovyZapavietSemuxaFragment : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
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
                0 -> knigaName = "Паводле Мацьвея"
                1 -> knigaName = "Паводле Марка"
                2 -> knigaName = "Паводле Лукаша"
                3 -> knigaName = "Паводле Яна"
                4 -> knigaName = "Дзеі Апосталаў"
                5 -> knigaName = "Якава"
                6 -> knigaName = "1-е Пятра"
                7 -> knigaName = "2-е Пятра"
                8 -> knigaName = "1-е Яна Багаслова"
                9 -> knigaName = "2-е Яна Багаслова"
                10 -> knigaName = "3-е Яна Багаслова"
                11 -> knigaName = "Юды"
                12 -> knigaName = "Да Рымлянаў"
                13 -> knigaName = "1-е да Карынфянаў"
                14 -> knigaName = "2-е да Карынфянаў"
                15 -> knigaName = "Да Галятаў"
                16 -> knigaName = "Да Эфэсянаў"
                17 -> knigaName = "Да Піліпянаў"
                18 -> knigaName = "Да Каласянаў"
                19 -> knigaName = "1-е да Фесаланікійцаў"
                20 -> knigaName = "2-е да Фесаланікійцаў"
                21 -> knigaName = "1-е да Цімафея"
                22 -> knigaName = "2-е да Цімафея"
                23 -> knigaName = "Да Ціта"
                24 -> knigaName = "Да Філімона"
                25 -> knigaName = "Да Габрэяў"
                26 -> knigaName = "Адкрыцьцё (Апакаліпсіс)"
            }
            return knigaName
        }

    internal interface ClicParalelListiner {
        fun setOnClic(cytanneParalelnye: String, cytanneSours: String)
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
                linearLayout6.visibility = View.VISIBLE
                if (linearLayout5.visibility == View.VISIBLE) {
                    linearLayout5.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout5.visibility = View.GONE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                }
                if (BibleGlobalList.bibleCopyList.size == bible.size)
                    copyBigFull.visibility = View.GONE
                else
                    copyBigFull.visibility = View.VISIBLE
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
        activity?.let {
            if (BibleGlobalList.bibleCopyList.size > 1) {
                linearLayout6.visibility = View.VISIBLE
                if (linearLayout5.visibility == View.VISIBLE) {
                    linearLayout5.animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    linearLayout5.visibility = View.GONE
                    spinnerCopy.setImageResource(by.carkva_gazeta.malitounik.R.drawable.arrow_down_float_bible)
                }
                if (BibleGlobalList.bibleCopyList.size == bible.size)
                    copyBigFull.visibility = View.GONE
                else
                    copyBigFull.visibility = View.VISIBLE
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
        BibleGlobalList.bibleCopyList.clear()
        listView.setSelection(NovyZapavietSemuxa.fierstPosition)
        listView.onItemLongClickListener = this
        listView.onItemClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPositionListiner?.getListPosition(view.firstVisiblePosition)
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
            adapter = ExpArrayAdapterParallel(activity, bible, kniga, page, true, 1)
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
                    for (i in BibleGlobalList.zakladkiSemuxa.indices) {
                        if (BibleGlobalList.zakladkiSemuxa[i].contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            index = i
                            break
                        }
                    }
                    if (index == -1) {
                        BibleGlobalList.zakladkiSemuxa.add(0, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())
                        MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
                    } else {
                        BibleGlobalList.zakladkiSemuxa.removeAt(index)
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
            zametka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.RAZDZEL) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1)
                    fragmentManager?.let { fragmentManager ->
                        val natatka = DialogBibleNatatka.getInstance(semuxa = true, novyzavet = true, kniga = kniga, bibletext = knigaName)
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
        fun newInstance(page: Int, kniga: Int, pazicia: Int): NovyZapavietSemuxaFragment {
            val fragmentFirst = NovyZapavietSemuxaFragment()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}