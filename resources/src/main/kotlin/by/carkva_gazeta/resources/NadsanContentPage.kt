package by.carkva_gazeta.resources

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItemBibleBinding
import by.carkva_gazeta.resources.databinding.ActivityBiblePageFragmentBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class NadsanContentPage : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var page = 0
    private var pazicia = 0
    private var listPosition: ListPosition? = null
    private var bible: ArrayList<String> = ArrayList()
    private lateinit var adapter: ListAdaprer
    private var _binding: ActivityBiblePageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface ListPosition {
        fun getListPosition(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            listPosition = context as ListPosition
        }
    }

    override fun onBackPressedFragment() {
        BibleGlobalList.mPedakVisable = false
        BibleGlobalList.bibleCopyList.clear()
        activity?.let {
            binding.linearLayout4.animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
        }
        binding.linearLayout4.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun addZakladka(color: Int) {
    }

    override fun addNatatka() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pos") ?: 0
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View?, position: Int, id: Long): Boolean {
        BibleGlobalList.mPedakVisable = true
        activity?.let {
            if (binding.linearLayout4.visibility == View.GONE) {
                binding.linearLayout4.animation = AnimationUtils.loadAnimation(it.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_top)
                binding.linearLayout4.visibility = View.VISIBLE
            }
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
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listView.setSelection(NadsanContentActivity.fierstPosition)
        binding.listView.onItemLongClickListener = this
        binding.listView.onItemClickListener = this
        binding.listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPosition?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
        val inputStream = resources.openRawResource(R.raw.nadsan_psaltyr)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val split = reader.readText().split("===")
        inputStream.close()
        val bibleline = split[page + 1].split("\n")
        bibleline.forEach {
            if (it.trim() != "")
                bible.add(it)
        }
        activity?.let { activity ->
            adapter = ListAdaprer(activity)
            binding.view.visibility = View.GONE
            binding.yelloy.visibility = View.GONE
            binding.underline.visibility = View.GONE
            binding.bold.visibility = View.GONE
            binding.zakladka.visibility = View.GONE
            binding.zametka.visibility = View.GONE
            binding.listView.divider = null
            binding.listView.adapter = adapter
            binding.listView.setSelection(pazicia)
            binding.listView.isVerticalScrollBarEnabled = false
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                binding.linearLayout4.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_blackMaranAta)
            }
            TooltipCompat.setTooltipText(binding.copyBigFull, getString(by.carkva_gazeta.malitounik.R.string.copy_big_full))
            TooltipCompat.setTooltipText(binding.copyBig, getString(by.carkva_gazeta.malitounik.R.string.copy_big))
            TooltipCompat.setTooltipText(binding.adpravit, getString(by.carkva_gazeta.malitounik.R.string.share))
            binding.copyBigFull.setOnClickListener {
                BibleGlobalList.bibleCopyList.clear()
                bible.forEachIndexed { index, _ ->
                    BibleGlobalList.bibleCopyList.add(index)
                }
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
                    binding.linearLayout4.animation = AnimationUtils.loadAnimation(activity.baseContext, by.carkva_gazeta.malitounik.R.anim.slide_in_buttom)
                    binding.linearLayout4.visibility = View.GONE
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
        }
    }

    private inner class ListAdaprer(mContext: Activity) : ArrayAdapter<String>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_bible, bible) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItemBibleBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (BibleGlobalList.bibleCopyList.size > 0 && BibleGlobalList.bibleCopyList.contains(position) && BibleGlobalList.mPedakVisable) {
                if (dzenNoch) {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark2)
                    viewHolder.text.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorWhite))
                } else {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
                }
            } else {
                if (dzenNoch) {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                    viewHolder.text.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorWhite))
                } else {
                    viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
                }
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            viewHolder.text.text = MainActivity.fromHtml(bible[position])
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        fun newInstance(page: Int, pos: Int): NadsanContentPage {
            val fragmentFirst = NadsanContentPage()
            val args = Bundle()
            args.putInt("page", page)
            args.putInt("pos", pos)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }
}