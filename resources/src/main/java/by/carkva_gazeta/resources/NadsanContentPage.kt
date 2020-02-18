package by.carkva_gazeta.resources

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class NadsanContentPage : ListFragment(), OnItemLongClickListener {
    private var page = 0
    private var pazicia = 0
    private var listPosition: ListPosition? = null
    private var bible: ArrayList<String> = ArrayList()

    interface ListPosition {
        fun getListPosition(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            listPosition = context as ListPosition
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        page = arguments?.getInt("page") ?: 0
        pazicia = arguments?.getInt("pos") ?: 0
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View?, position: Int, id: Long): Boolean {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copy = MainActivity.fromHtml(parent.adapter.getItem(position).toString()).toString()
        val clip = ClipData.newPlainText("", MainActivity.fromHtml(copy)) //MaranAta_Global_List.getBible().get(MaranAta_Global_List.getListPosition())).toString()
        clipboard.setPrimaryClip(clip)
        val layout = LinearLayout(activity)
        val chin = activity?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = chin?.getBoolean("dzen_noch", false)
        if (dzenNoch == true) layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary_black) else layout.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorPrimary)
        val density = resources.displayMetrics.density
        val realpadding = (10 * density).toInt()
        val toast = TextViewRobotoCondensed(activity)
        activity?.let {
            toast.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorIcons))
        }
        toast.setPadding(realpadding, realpadding, realpadding, realpadding)
        toast.text = getString(by.carkva_gazeta.malitounik.R.string.copynadsan, copy)
        toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        layout.addView(toast)
        val mes = Toast(activity)
        mes.duration = Toast.LENGTH_LONG
        mes.view = layout
        mes.show()
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setSelection(NadsanContentActivity.fierstPosition)
        listView.onItemLongClickListener = this
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
        bible.addAll(listOf(*bibleline).subList(1, bibleline.size))
        activity?.let {
            val adapter = ListAdaprer(it)
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

    internal inner class ListAdaprer(private val mContext: Activity) : ArrayAdapter<String?>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_bible, bible as List<String>) {
        private val k: SharedPreferences = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        override fun isEnabled(position: Int): Boolean {
            return false
        }

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
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            val dzenNoch = k.getBoolean("dzen_noch", false)
            /*String br = "";
            if (!bible.get(position).equals(""))
                br = "<br>";*/viewHolder.text?.text = MainActivity.fromHtml(bible[position]) // + br
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, by.carkva_gazeta.malitounik.R.color.colorIcons))
            }
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