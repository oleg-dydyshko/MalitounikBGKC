package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding

class DialogAddPesny : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var dialogAddPesnyListiner: DialogAddPesnyListiner? = null
    private lateinit var pesny: String
    private lateinit var input: EditText
    private lateinit var array: Array<out String>

    interface DialogAddPesnyListiner {
        fun addPesny(title: String, pesny: String, fileName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pesny = getString(by.carkva_gazeta.malitounik.R.string.pesny1)
        array = arrayOf(getString(by.carkva_gazeta.malitounik.R.string.pesny1), getString(by.carkva_gazeta.malitounik.R.string.pesny2), getString(by.carkva_gazeta.malitounik.R.string.pesny3), getString(by.carkva_gazeta.malitounik.R.string.pesny4), getString(by.carkva_gazeta.malitounik.R.string.pesny5))
        if (context is Activity) dialogAddPesnyListiner = try {
            context as DialogAddPesnyListiner
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DialogAddPesnyListiner")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", input.text.toString())
        outState.putString("pesny", pesny)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextView(it)
            textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = resources.getString(by.carkva_gazeta.malitounik.R.string.add_pesny_title)
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.typeface = MainActivity.createFont(it,  Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorWhite))
            linear.addView(textViewZaglavie)
            input = EditText(it)
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            input.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            input.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            input.setPadding(realpadding, realpadding, realpadding, realpadding)
            input.requestFocus()
            input.setOnEditorActionListener { editText, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (editText.text.toString() != "") {
                        addPesny()
                    }
                    dialog?.cancel()
                }
                false
            }
            input.imeOptions = EditorInfo.IME_ACTION_GO
            linear.addView(input)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(realpadding, realpadding, 0, 0)
            val spinner = Spinner(it)
            spinner.adapter = PesnyAdapter(it)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> pesny = "pesny_prasl_"
                        1 -> pesny = "pesny_bel_"
                        2 -> pesny = "pesny_bag_"
                        3 -> pesny = "pesny_kal_"
                        4 -> pesny = "pesny_taize_"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            if (savedInstanceState != null) {
                input.setText(savedInstanceState.getString("fileName"))
                pesny = savedInstanceState.getString("pesny", getString(by.carkva_gazeta.malitounik.R.string.pesny1))
                var position = 0
                when (pesny) {
                    "pesny_prasl_" -> position = 0
                    "pesny_bel_" -> position = 1
                    "pesny_bag_" -> position = 2
                    "pesny_kal_" -> position = 3
                    "pesny_taize_" -> position = 4
                }
                spinner.setSelection(position)
            }
            input.hint = getString(by.carkva_gazeta.malitounik.R.string.hint_pesny_title)
            linear.addView(spinner, layoutParams)
            builder.setView(linear)
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.add_pesny)) { _: DialogInterface, _: Int ->
                if (input.text.toString() != "") {
                    addPesny()
                }
            }
            builder.setNegativeButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    private fun addPesny() {
        dialogAddPesnyListiner?.addPesny(input.text.toString().trim(), pesny, arguments?.getString("fileName", "")?: "")
    }

    private inner class PesnyAdapter(context: Context) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, array) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderColor
            if (convertView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolderColor(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderColor
            }
            viewHolder.text.text = array[position]
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val text = view.findViewById<TextView>(by.carkva_gazeta.malitounik.R.id.label)
            text.text = array[position]
            return view
        }
    }

    private class ViewHolderColor(var text: TextView)

    companion object {
        fun getInstance(fileName: String): DialogAddPesny {
            val dialogAddPesny = DialogAddPesny()
            val bundle = Bundle()
            bundle.putString("fileName", fileName)
            dialogAddPesny.arguments = bundle
            return dialogAddPesny
        }
    }
}