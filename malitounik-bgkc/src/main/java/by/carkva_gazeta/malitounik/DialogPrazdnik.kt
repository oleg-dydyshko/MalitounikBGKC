package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.*

class DialogPrazdnik : DialogFragment() {
    private var setid = 10
    private lateinit var arrayList: ArrayList<Int>
    private lateinit var mListener: DialogPrazdnikListener
    private lateinit var alert: AlertDialog

    interface DialogPrazdnikListener {
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
            val builder = AlertDialog.Builder(it)
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
            val arrayAdapter = ListAdapter(it, arrayList)
            val spinner = Spinner(it)
            spinner.adapter = arrayAdapter
            spinner.setSelection(setid)
            for (i in arrayList.indices) {
                if (arrayList[i] == c[Calendar.YEAR]) {
                    setid = i
                }
            }
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
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
            }
        }
        return alert
    }
}