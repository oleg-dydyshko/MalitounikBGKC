package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter

/**
 * Created by oleg on 30.5.16
 */
class MenuVybranoe : ListFragment() {
    private lateinit var adapter: MyVybranoeAdapter
    private var mLastClickTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun fileDelite(position: Int) {
        vybranoe.removeAt(position)
        activity?.let {
            val gson = Gson()
            val file = File(it.filesDir.toString() + "/Vybranoe.json")
            val outputStream = FileWriter(file)
            outputStream.write(gson.toJson(vybranoe))
            outputStream.close()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { it ->
            val file = File(it.filesDir.toString() + "/Vybranoe.json")
            if (file.exists()) {
                vybranoe = try {
                    val gson = Gson()
                    val line = file.readText()
                    val type = object : TypeToken<ArrayList<VybranoeData?>?>() {}.type
                    gson.fromJson(line, type)
                } catch (t: Throwable) {
                    file.delete()
                    ArrayList()
                }
            }
        }
        vybranoe.sortWith(VybranoeDataSort())
        activity?.let { it ->
            adapter = MyVybranoeAdapter(it)
            listAdapter = adapter
            listView.isVerticalScrollBarEnabled = false
            listView.onItemLongClickListener = OnItemLongClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                val dd = DialogDelite.getInstance(position, "", "з выбранага", vybranoe[position].data)
                fragmentManager?.let { dd.show(it, "dialog_dilite") }
                true
            }
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        super.onListItemClick(l, v, position, id)
        if (MainActivity.checkmoduleResources(activity)) {
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.VybranoeView"))
            intent.putExtra("resurs", vybranoe[position].resurs)
            intent.putExtra("title", vybranoe[position].data)
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == R.id.trash) {
            if (vybranoe.size > 0) {
                activity?.let {
                    val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                    val dzenNoch = chin.getBoolean("dzen_noch", false)
                    val builder = AlertDialog.Builder(it)
                    val linearLayout = LinearLayout(it)
                    linearLayout.orientation = LinearLayout.VERTICAL
                    val textViewZaglavie = TextViewRobotoCondensed(it)
                    if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
                    val density = resources.displayMetrics.density
                    val realpadding = (10 * density).toInt()
                    textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
                    textViewZaglavie.text = resources.getString(R.string.remove)
                    textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
                    textViewZaglavie.setTypeface(null, Typeface.BOLD)
                    textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
                    linearLayout.addView(textViewZaglavie)
                    val textView = TextViewRobotoCondensed(it)
                    textView.text = "Вы сапраўды жадаеце выдаліць усё Выбранае?"
                    textView.setPadding(realpadding, realpadding, realpadding, realpadding)
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
                    if (dzenNoch) textView.setTextColor(ContextCompat.getColor(it, R.color.colorIcons)) else textView.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                    linearLayout.addView(textView)
                    builder.setView(linearLayout)
                    builder.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                        vybranoe.clear()
                        val gson = Gson()
                        val file = File(it.filesDir.toString() + "/Vybranoe.json")
                        val outputStream = FileWriter(file)
                        outputStream.write(gson.toJson(vybranoe))
                        outputStream.close()
                        adapter.notifyDataSetChanged()
                    }
                    builder.setNegativeButton(resources.getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                    val alert = builder.create()
                    alert.setOnShowListener {
                        val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                        val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                        btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                    }
                    alert.show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class MyVybranoeAdapter(private val activity: Activity) : ArrayAdapter<VybranoeData?>(activity, R.layout.simple_list_item_3, R.id.label, vybranoe as List<VybranoeData>) {
        private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)

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
            viewHolder.buttonPopup?.setOnClickListener { viewHolder.buttonPopup?.let { showPopupMenu(it, position, vybranoe[position].data) } }
            viewHolder.text?.text = vybranoe[position].data
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
            popup.menu.getItem(0).isVisible = false
            for (i in 0 until popup.menu.size()) {
                val item = popup.menu.getItem(i)
                val spanString = SpannableString(popup.menu.getItem(i).title.toString())
                val end = spanString.length
                spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                item.title = spanString
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                popup.dismiss()
                if (menuItem.itemId == R.id.menu_remove) {
                    val dd = DialogDelite.getInstance(position, "", "з выбранага", name)
                    fragmentManager?.let { dd.show(it, "dialog_dilite") }
                    return@setOnMenuItemClickListener true
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

    companion object {
        var vybranoe: ArrayList<VybranoeData> = ArrayList()
    }
}