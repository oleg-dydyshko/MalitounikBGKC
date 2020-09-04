package by.carkva_gazeta.biblijateka

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.util.*
import kotlin.collections.ArrayList

class DialogTitleBiblioteka : DialogFragment() {
    private var bookmarks: ArrayList<String> = ArrayList()
    private var mListener: DialogTitleBibliotekaListener? = null
    private lateinit var chin: SharedPreferences
    private lateinit var alert: AlertDialog

    internal interface DialogTitleBibliotekaListener {
        fun onDialogTitle(page: Int)
        fun onDialogTitleString(page: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogTitleBibliotekaListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogTitleBibliotekaListener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookmarks = arguments?.getStringArrayList("bookmarks")?: ArrayList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val builder = AlertDialog.Builder(it)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.zmest).toUpperCase(Locale.getDefault())
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linear.addView(textViewZaglavie)
            val listViewCompat = ListView(it)
            listViewCompat.adapter = TitleListAdaprer(it)
            linear.addView(listViewCompat)
            builder.setView(linear)
            builder.setPositiveButton(getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            }
            listViewCompat.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                val t1 = bookmarks[i].indexOf("<>")
                if (t1 != -1) {
                    val t2 = bookmarks[i].substring(0, t1).toInt()
                    mListener?.onDialogTitle(t2)
                } else {
                    val t2 = bookmarks[i].indexOf("<str>")
                    mListener?.onDialogTitleString(bookmarks[i].substring(0, t2))
                }
                alert.cancel()
            }
        }
        return alert
    }

    internal inner class TitleListAdaprer(private val mContext: Activity) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_2, R.id.label, bookmarks) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var t1 = bookmarks[position].indexOf("<>")
            if (t1 == -1) {
                t1 = bookmarks[position].indexOf("<str>")
                viewHolder.text?.text = bookmarks[position].substring(t1 + 5)
            } else {
                viewHolder.text?.text = bookmarks[position].substring(t1 + 2)
            }
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.color.colorbackground_material_dark_ligte)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            } else {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_white)
            }
            return rootView
        }

    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        fun getInstance(bookmarks: ArrayList<String>?): DialogTitleBiblioteka {
            val instance = DialogTitleBiblioteka()
            val args = Bundle()
            args.putStringArrayList("bookmarks", bookmarks)
            instance.arguments = args
            return instance
        }
    }
}