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
import androidx.appcompat.widget.TooltipCompat
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BaseFragment
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.resources.databinding.ActivityBiblePageFragmentBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class BibliaFragment : BaseFragment(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var kniga = 0
    private var page = 0
    private var pazicia = 0
    private var bibleListiner: BibleListiner? = null
    private lateinit var adapter: BibleArrayAdapterParallel
    private var bible = ArrayList<String>()
    private var knigaBible = ""
    private var _binding: ActivityBiblePageFragmentBinding? = null
    private val binding get() = _binding!!
    private var novyZapavet = false

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
                bibleListiner?.isPanelVisible()
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun addZakladka(color: Int) {
        if (color != -1) {
            (activity as? BibliaActivity)?.addZakladka(color, knigaBible, bible[BibleGlobalList.bibleCopyList[0]])
            BibleGlobalList.mPedakVisable = false
            BibleGlobalList.bibleCopyList.clear()
            adapter.notifyDataSetChanged()
        }
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
        knigaBible = arguments?.getString("title") ?: ""
        kniga = arguments?.getInt("kniga") ?: 0
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pazicia") ?: 0
        novyZapavet = arguments?.getBoolean("novyZapavet") ?: false
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        BibleGlobalList.mPedakVisable = true
        (activity as? BibliaActivity)?.let { activity ->
            if (binding.linearLayout4.visibility == View.GONE) {
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
                binding.linearLayout4.visibility = View.VISIBLE
                binding.linearLayout4.post {
                    val width = binding.linearLayout4.width
                    bibleListiner?.isPanelVisible(width)
                }
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
            } else if (activity.getNamePerevod() != DialogVybranoeBibleList.PEREVODNADSAN) {
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
        (activity as? BibliaActivity)?.let { bibliaActyvity ->
            if (!BibleGlobalList.mPedakVisable) {
                BibleGlobalList.bibleCopyList.clear()
                val parallel = BibliaParallelChtenia()
                var res = "+-+"
                var clic = false
                var knigaReal = kniga
                if (novyZapavet) {
                    if (knigaReal == 0) {
                        res = parallel.kniga51(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 1) {
                        res = parallel.kniga52(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 2) {
                        res = parallel.kniga53(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 3) {
                        res = parallel.kniga54(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 4) {
                        res = parallel.kniga55(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 5) {
                        res = parallel.kniga56(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 6) {
                        res = parallel.kniga57(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 7) {
                        res = parallel.kniga58(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 8) {
                        res = parallel.kniga59(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 9) {
                        res = parallel.kniga60(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 10) {
                        res = parallel.kniga61(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 11) {
                        res = parallel.kniga62(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 12) {
                        res = parallel.kniga63(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 13) {
                        res = parallel.kniga64(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 14) {
                        res = parallel.kniga65(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 15) {
                        res = parallel.kniga66(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 16) {
                        res = parallel.kniga67(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 17) {
                        res = parallel.kniga68(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 18) {
                        res = parallel.kniga69(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 19) {
                        res = parallel.kniga70(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 20) {
                        res = parallel.kniga71(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 21) {
                        res = parallel.kniga72(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 22) {
                        res = parallel.kniga73(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 23) {
                        res = parallel.kniga74(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 24) {
                        res = parallel.kniga75(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 25) {
                        res = parallel.kniga76(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 26) {
                        res = parallel.kniga77(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                } else {
                    knigaReal = bibliaActyvity.getKnigaReal(kniga)
                    if (knigaReal == 0) {
                        res = parallel.kniga1(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 1) {
                        res = parallel.kniga2(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 2) {
                        res = parallel.kniga3(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 3) {
                        res = parallel.kniga4(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 4) {
                        res = parallel.kniga5(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 5) {
                        res = parallel.kniga6(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 6) {
                        res = parallel.kniga7(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 7) {
                        res = parallel.kniga8(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 8) {
                        res = parallel.kniga9(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 9) {
                        res = parallel.kniga10(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 10) {
                        res = parallel.kniga11(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 11) {
                        res = parallel.kniga12(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 12) {
                        res = parallel.kniga13(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 13) {
                        res = parallel.kniga14(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 14) {
                        res = parallel.kniga15(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 15) {
                        res = parallel.kniga16(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 16) {
                        res = parallel.kniga17(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 17) {
                        res = parallel.kniga18(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 18) {
                        res = parallel.kniga19(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 19) {
                        res = parallel.kniga20(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 20) {
                        res = parallel.kniga21(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 21) {
                        res = parallel.kniga22(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 22) {
                        res = parallel.kniga23(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 23) {
                        res = parallel.kniga24(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 24) {
                        res = parallel.kniga25(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 25) {
                        res = parallel.kniga26(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 26) {
                        res = parallel.kniga27(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 27) {
                        res = parallel.kniga28(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 28) {
                        res = parallel.kniga29(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 29) {
                        res = parallel.kniga30(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 30) {
                        res = parallel.kniga31(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 31) {
                        res = parallel.kniga32(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 32) {
                        res = parallel.kniga33(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 33) {
                        res = parallel.kniga34(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 34) {
                        res = parallel.kniga35(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 35) {
                        res = parallel.kniga36(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 36) {
                        res = parallel.kniga37(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 37) {
                        res = parallel.kniga38(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 38) {
                        res = parallel.kniga39(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 39) {
                        res = parallel.kniga40(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 40) {
                        res = parallel.kniga41(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 41) {
                        res = parallel.kniga42(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 42) {
                        res = parallel.kniga43(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 43) {
                        res = parallel.kniga44(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 44) {
                        res = parallel.kniga45(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 45) {
                        res = parallel.kniga46(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 46) {
                        res = parallel.kniga47(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 47) {
                        res = parallel.kniga48(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 48) {
                        res = parallel.kniga49(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                    if (knigaReal == 49) {
                        res = parallel.kniga50(page + 1, position + 1)
                        if (!res.contains("+-+")) clic = true
                    }
                }
                if (clic) {
                    bibleListiner?.setOnClic(res, bibliaActyvity.getSpisKnig(novyZapavet)[kniga] + " " + (page + 1) + ":" + (position + 1))
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
            if (BibleGlobalList.bibleCopyList.size > 1) {
                binding.view.visibility = View.GONE
                binding.yelloy.visibility = View.GONE
                binding.underline.visibility = View.GONE
                binding.bold.visibility = View.GONE
                binding.zakladka.visibility = View.GONE
                binding.zametka.visibility = View.GONE
            } else if (bibliaActyvity.getNamePerevod() != DialogVybranoeBibleList.PEREVODNADSAN) {
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
        bibleListiner?.isPanelVisible()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityBiblePageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BibliaActivity)?.let { activity ->
            BibleGlobalList.bibleCopyList.clear()
            binding.listView.onItemLongClickListener = this
            binding.listView.onItemClickListener = this
            binding.listView.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                    bibleListiner?.getListPosition(view.firstVisiblePosition)
                }

                override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
            })
            val inputStream = activity.getInputStream(novyZapavet, kniga)
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
            adapter = BibleArrayAdapterParallel(activity, bible, activity.getKnigaReal(kniga), page, novyZapavet, activity.getNamePerevod())
            binding.listView.divider = null
            binding.listView.adapter = adapter
            binding.listView.setSelection(pazicia)
            binding.listView.isVerticalScrollBarEnabled = false
            val dzenNoch = (activity as BaseActivity).getBaseDzenNoch()
            if (dzenNoch) {
                binding.linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta)
            }
            if (activity.getNamePerevod() == DialogVybranoeBibleList.PEREVODNADSAN) {
                binding.view.visibility = View.GONE
                binding.yelloy.visibility = View.GONE
                binding.underline.visibility = View.GONE
                binding.bold.visibility = View.GONE
                binding.zakladka.visibility = View.GONE
                binding.zametka.visibility = View.GONE
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
                    bibleListiner?.isPanelVisible()
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
                    adapter.notifyDataSetChanged()
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
                    bibleListiner?.isPanelVisible()
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
                    bibleListiner?.isPanelVisible()
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
                    bibleListiner?.isPanelVisible()
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
                    for (i in activity.getZakladki().indices) {
                        if (activity.getZakladki()[i].data.contains(MainActivity.fromHtml(bible[BibleGlobalList.bibleCopyList[0]]).toString())) {
                            index = i
                            break
                        }
                    }
                    if (index == -1) {
                        val dialog = DialogAddZakladka()
                        dialog.show(childFragmentManager, "DialogAddZakladka")
                    } else {
                        activity.getZakladki().removeAt(index)
                        BibleGlobalList.mPedakVisable = false
                        BibleGlobalList.bibleCopyList.clear()
                    }
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible()
                    adapter.notifyDataSetChanged()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
            binding.zametka.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val knigaName = knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1)
                    val natatka = DialogBibleNatatka.getInstance(perevod = activity.getNamePerevod(), novyzavet = novyZapavet, kniga = activity.getKnigaReal(kniga), glava = BibleGlobalList.mListGlava, stix = BibleGlobalList.bibleCopyList[0], bibletext = knigaName)
                    natatka.show(childFragmentManager, "bible_natatka")
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
                    bibleListiner?.isPanelVisible()
                    BibleGlobalList.mPedakVisable = false
                    BibleGlobalList.bibleCopyList.clear()
                } else {
                    MainActivity.toastView(activity, getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
        }
    }

    companion object {
        fun newInstance(title: String, page: Int, kniga: Int, pazicia: Int, novyZapavet: Boolean): BibliaFragment {
            val fragmentFirst = BibliaFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putInt("page", page)
            args.putInt("kniga", kniga)
            args.putInt("pazicia", pazicia)
            args.putBoolean("novyZapavet", novyZapavet)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}