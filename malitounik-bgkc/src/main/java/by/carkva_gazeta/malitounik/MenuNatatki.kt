package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import by.carkva_gazeta.malitounik.DialogContextMenu.Companion.getInstance
import by.carkva_gazeta.malitounik.DialogDelite.Companion.getInstance
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * Created by oleg on 13.6.16
 */
class MenuNatatki : ListFragment() {
    private lateinit var adapter: MyNatatkiAdapter
    private val myNatatkiFiles = ArrayList<MyNatatkiFiles>()
    private var mLastClickTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // Скрываем клавиатуру
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        File(activity?.filesDir.toString().plus("/Malitva")).walk().forEach { file ->
            if (file.isFile) {
                val inputStream = FileReader(file)
                val reader = BufferedReader(inputStream)
                val res = reader.readText().split("<MEMA></MEMA>").toTypedArray()
                inputStream.close()
                var lRTE: Long = 1
                if (res[1].contains("<RTE></RTE>")) {
                    val start = res[1].indexOf("<RTE></RTE>")
                    val end = res[1].length
                    lRTE = res[1].substring(start + 11, end).toLong()
                }
                if (lRTE <= 1) {
                    lRTE = file.lastModified()
                }
                activity?.let {
                    myNatatkiFiles.add(MyNatatkiFiles(it, lRTE, res[0], file.absoluteFile))
                }
            }
        }
        myNatatkiFiles.sort()
        activity?.let {
            adapter = MyNatatkiAdapter(it)
        }
        listAdapter = adapter
        listView.isVerticalScrollBarEnabled = false
        listView.onItemLongClickListener = OnItemLongClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val contextMenu = getInstance(position, myNatatkiFiles[position].name)
            fragmentManager?.let { contextMenu.show(it, "context_menu") }
            true
        }
    }

    fun sortAlfavit() {
        myNatatkiFiles.sort()
        adapter.notifyDataSetChanged()
    }

    fun fileDelite(position: Int) {
        val f = myNatatkiFiles[position]
        myNatatkiFiles.removeAt(position)
        f.file.delete()
        myNatatkiFiles.sort()
        adapter.notifyDataSetChanged()
    }

    fun onDialogEditClick(position: Int) {
        if (MainActivity.checkmoduleResources(activity)) {
            val f = myNatatkiFiles[position]
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiAdd"))
            intent.putExtra("filename", f.file.name)
            intent.putExtra("redak", true)
            startActivityForResult(intent, 104)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    fun onDialogDeliteClick(position: Int, name: String) {
        val dd = getInstance(position, "", "нататку", name)
        fragmentManager?.let { dd.show(it, "dialog_delite") }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var save = false
        if (data != null) {
            save = data.getBooleanExtra("savefile", false)
        }
        if (requestCode == 103 || requestCode == 104) {
            if (save) {
                myNatatkiFiles.clear()
                File(activity?.filesDir.toString().plus("/Malitva")).walk().forEach { file ->
                    if (file.isFile) {
                        val inputStream = FileReader(file)
                        val reader = BufferedReader(inputStream)
                        val res = reader.readText().split("<MEMA></MEMA>").toTypedArray()
                        inputStream.close()
                        var lRTE: Long = 1
                        if (res[1].contains("<RTE></RTE>")) {
                            val start = res[1].indexOf("<RTE></RTE>")
                            val end = res[1].length
                            lRTE = res[1].substring(start + 11, end).toLong()
                            res[1] = res[1].substring(0, start)
                        }
                        if (lRTE <= 1) {
                            lRTE = file.lastModified()
                        }
                        activity?.let {
                            myNatatkiFiles.add(MyNatatkiFiles(it, lRTE, res[0], file.absoluteFile))
                        }
                    }
                }
                myNatatkiFiles.sort()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.action_add) {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiAdd"))
                intent.putExtra("redak", false)
                intent.putExtra("filename", "")
                startActivityForResult(intent, 103)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        super.onListItemClick(l, v, position, id)
        if (MainActivity.checkmoduleResources(activity)) {
            val f = myNatatkiFiles[position]
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MyNatatkiView"))
            intent.putExtra("filename", f.file.name)
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    private inner class MyNatatkiAdapter(private val activity: Activity) : ArrayAdapter<MyNatatkiFiles>(activity, R.layout.simple_list_item_3, R.id.label, myNatatkiFiles) {
        private val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_3, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
                viewHolder.buttonPopup = rootView.findViewById(R.id.button_popup)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.buttonPopup?.setOnClickListener { viewHolder.buttonPopup?.let { showPopupMenu(it, position, myNatatkiFiles[position].name) } }
            viewHolder.text?.text = myNatatkiFiles[position].name
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }

        private fun showPopupMenu(view: View, position: Int, name: String) {
            val popup = PopupMenu(activity, view)
            val infl = popup.menuInflater
            infl.inflate(R.menu.popup, popup.menu)
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                when (menuItem.itemId) {
                    R.id.menu_redoktor -> {
                        onDialogEditClick(position)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.menu_remove -> {
                        onDialogDeliteClick(position, name)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
            popup.show()
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
        var buttonPopup: ImageView? = null
    }
}