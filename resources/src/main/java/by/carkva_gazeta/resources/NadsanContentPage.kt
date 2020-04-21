package by.carkva_gazeta.resources

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import kotlinx.android.synthetic.main.activity_bible_page_fragment.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class NadsanContentPage : BackPressedFragment(), OnItemLongClickListener, AdapterView.OnItemClickListener {
    private var page = 0
    private var pazicia = 0
    private var listPosition: ListPosition? = null
    private var bible: ArrayList<String> = ArrayList()
    private lateinit var adapter: ListAdaprer

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
        linearLayout4.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    override fun addNatatka() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pos") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_bible_page_fragment, container, false)
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
        if (linearLayout4.visibility == View.GONE) {
            BibleGlobalList.bibleCopyList.add(position)
            linearLayout4.visibility = View.VISIBLE
            copyBig.visibility = View.VISIBLE
            copyBigFull.visibility = View.VISIBLE
            copy.visibility = View.GONE
            yelloy.visibility = View.GONE
            underline.visibility = View.GONE
            bold.visibility = View.GONE
            zakladka.visibility = View.GONE
            zametka.visibility = View.GONE
            BibleGlobalList.mPedakVisable = true
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
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(NadsanContentActivity.fierstPosition)
        listView.onItemLongClickListener = this
        listView.onItemClickListener = this
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listPosition?.getListPosition(view.firstVisiblePosition)
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
        })
        val inputStream = resources.openRawResource(R.raw.nadsan_psaltyr)
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
            adapter = ListAdaprer(activity)
            listView.divider = null
            listView.adapter = adapter
            listView.setSelection(pazicia)
            listView.isVerticalScrollBarEnabled = false
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (dzenNoch) {
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
            }
            copyBig.setOnClickListener {
                if (BibleGlobalList.bibleCopyList.size > 0) {
                    val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val copyString = StringBuilder()
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
                } else {
                    messageView(getString(by.carkva_gazeta.malitounik.R.string.set_versh))
                }
            }
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

    private inner class ListAdaprer(private val mContext: Activity) : ArrayAdapter<String?>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_bible, bible as List<String>) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(by.carkva_gazeta.malitounik.R.layout.simple_list_item_bible, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(by.carkva_gazeta.malitounik.R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (BibleGlobalList.bibleCopyList.size > 0 && BibleGlobalList.bibleCopyList.contains(position) && BibleGlobalList.mPedakVisable) {
                if (dzenNoch) {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorprimary_material_dark2)
                    viewHolder.text?.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                } else {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
                }
            } else {
                if (dzenNoch) {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_dark)
                    viewHolder.text?.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                } else {
                    viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
                }
            }
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            viewHolder.text?.text = MainActivity.fromHtml(bible[position])
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

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