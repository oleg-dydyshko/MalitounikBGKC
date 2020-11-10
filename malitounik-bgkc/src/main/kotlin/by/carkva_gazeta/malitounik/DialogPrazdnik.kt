package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.*
import kotlin.collections.ArrayList

class DialogPrazdnik : DialogFragment() {
    private var setid = 10
    private lateinit var arrayList: ArrayList<Int>
    private lateinit var mListener: DialogPrazdnikListener
    private lateinit var alert: AlertDialog

    internal interface DialogPrazdnikListener {
        fun setPrazdnik(year: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPrazdnikListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPrazdnikListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("arrayList", arrayList)
        outState.putInt("setid", setid)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.CARKVA_SVIATY)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linear.addView(textViewZaglavie)
            val c = Calendar.getInstance() as GregorianCalendar
            if (savedInstanceState != null) {
                setid = savedInstanceState.getInt("setid")
                arrayList = savedInstanceState.getIntegerArrayList("arrayList") ?: ArrayList()
            } else {
                arrayList = ArrayList()
                for (i in c[Calendar.YEAR] + 10 downTo SettingsActivity.GET_CALIANDAR_YEAR_MIN) {
                    arrayList.add(i)
                }
            }
            val arrayAdapter = ListAdapter(it)
            val spinner = Spinner(it)
            spinner.adapter = arrayAdapter
            for (i in arrayList.indices) {
                if (arrayList[i] == arguments?.getInt("year")?: c[Calendar.YEAR]) {
                    setid = i
                }
            }
            spinner.setSelection(setid)
            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setid = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            linear.addView(spinner)
            builder.setView(linear)
            builder.setNegativeButton(getString(R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int -> mListener.setPrazdnik(arrayList[setid]) }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(year: Int): DialogPrazdnik {
            val dialogPrazdnik = DialogPrazdnik()
            val bundle = Bundle()
            bundle.putInt("year", year)
            dialogPrazdnik.arguments = bundle
            return dialogPrazdnik
        }
    }

    private inner class ListAdapter(private val mContext: Activity) : ArrayAdapter<Int>(mContext, R.layout.simple_list_item_1, arrayList) {
        private val k = mContext.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        private val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        private val gc = Calendar.getInstance() as GregorianCalendar
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(R.layout.simple_list_item_1, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.text1)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            if (gc[Calendar.YEAR] == arrayList[position]) viewHolder.text?.setTypeface(null, Typeface.BOLD) else viewHolder.text?.setTypeface(null, Typeface.NORMAL)
            viewHolder.text?.text = arrayList[position].toString()
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dialog_font_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            } else {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_white)
            }
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            val text: TextViewRobotoCondensed = v.findViewById(R.id.text1)
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
            if (gc[Calendar.YEAR] == arrayList[position]) text.setTypeface(null, Typeface.BOLD) else text.setTypeface(null, Typeface.NORMAL)
            text.text = arrayList[position].toString()
            if (dzenNoch) {
                text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
                text.setTextColor(ContextCompat.getColor(mContext, R.color.colorIcons))
            } else {
                text.setBackgroundResource(R.drawable.selector_white)
            }
            return v
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}