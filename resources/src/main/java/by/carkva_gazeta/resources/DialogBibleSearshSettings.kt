package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences.Editor
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed

/**
 * Created by oleg on 22.7.17
 */
class DialogBibleSearshSettings : DialogFragment() {
    private lateinit var prefEditors: Editor
    private var mListener: DiallogBibleSearshListiner? = null
    private lateinit var builder: AlertDialog.Builder
    private var dzenNoch = false

    internal interface DiallogBibleSearshListiner {
        fun onSetSettings(edit: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DiallogBibleSearshListiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DiallogBibleSearshListiner")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = chin.getBoolean("dzen_noch", false)
            prefEditors = chin.edit()
            builder = AlertDialog.Builder(it)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(R.string.settings_poshuk)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            linear.addView(textViewZaglavie)
            val scrollView = ScrollView(it)
            linear.addView(scrollView)
            scrollView.setPadding(10, 10, 10, 0)
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            scrollView.addView(linearLayout)
            val checkBox = CheckBox(it)
            checkBox.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            if (chin.getInt("pegistr", 0) == 1) checkBox.isChecked = true
            checkBox.text = "Улічваць рэгістр"
            checkBox.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    prefEditors.putInt("pegistr", 1)
                } else {
                    prefEditors.putInt("pegistr", 0)
                }
                prefEditors.apply()
            }
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val checkBox1 = CheckBox(it)
            checkBox1.typeface = TextViewRobotoCondensed.createFont(Typeface.NORMAL)
            if (chin.getInt("slovocalkam", 0) == 1) checkBox1.isChecked = true
            checkBox1.text = "Дакладнае супадзеньне"
            checkBox1.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    prefEditors.putInt("slovocalkam", 1)
                } else {
                    prefEditors.putInt("slovocalkam", 0)
                }
                prefEditors.apply()
            }
            checkBox1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val data = arrayOf("УСЯ БІБЛІЯ", "НОВЫ ЗАПАВЕТ", "СТАРЫ ЗАПАВЕТ")
            val spinner = Spinner(it)
            val arrayAdapter = DialogBibleAdapter(it, data)
            spinner.adapter = arrayAdapter
            spinner.setSelection(chin.getInt("biblia_seash", 0))
            spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prefEditors.putInt("biblia_seash", position)
                    prefEditors.apply()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            linearLayout.addView(checkBox)
            linearLayout.addView(checkBox1)
            linearLayout.addView(spinner)
            builder.setView(linear)
            builder.setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                mListener?.onSetSettings(arguments?.getString("edit")?: "")
            }
        }
        val alert = builder.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN - 2.toFloat())
        }
        return alert
    }

    private inner class DialogBibleAdapter(private val context: Activity, private val name: Array<String>) : ArrayAdapter<String?>(context, R.layout.simple_list_item_1, name) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextViewRobotoCondensed
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            if (dzenNoch)
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            return v
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            if (convertView == null) {
                rootView = context.layoutInflater.inflate(R.layout.simple_list_item_4, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.text1)
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
            if (dzenNoch)
                viewHolder.text?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            viewHolder.text?.gravity = Gravity.START
            viewHolder.text?.setTypeface(null, Typeface.NORMAL)
            viewHolder.text?.text = name[position]
            return rootView
        }

    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }

    companion object {
        fun getInstance(edit: String?): DialogBibleSearshSettings {
            val instance = DialogBibleSearshSettings()
            val args = Bundle()
            args.putString("edit", edit)
            instance.arguments = args
            return instance
        }
    }
}