package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.DialogAddpesnyBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding

class DialogAddPesny : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var dialogAddPesnyListiner: DialogAddPesnyListiner? = null
    private lateinit var pesny: String
    private lateinit var array: Array<out String>
    private var _binding: DialogAddpesnyBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
        outState.putString("fileName", binding.content.text.toString())
        outState.putString("pesny", pesny)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogAddpesnyBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.add_pesny_title)
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { editText, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (editText.text.toString() != "") {
                        addPesny()
                    }
                    dialog?.cancel()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            binding.spinner.adapter = PesnyAdapter(it)
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> pesny = "piesni_prasl_"
                        1 -> pesny = "piesni_belarus_"
                        2 -> pesny = "piesni_bagar_"
                        3 -> pesny = "piesni_kalady_"
                        4 -> pesny = "piesni_taize_"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            if (savedInstanceState != null) {
                binding.content.setText(savedInstanceState.getString("fileName") ?: "")
                pesny = savedInstanceState.getString("pesny", getString(by.carkva_gazeta.malitounik.R.string.pesny1))
                var position = 0
                when (pesny) {
                    "pesny_prasl_" -> position = 0
                    "pesny_bel_" -> position = 1
                    "pesny_bag_" -> position = 2
                    "pesny_kal_" -> position = 3
                    "pesny_taize_" -> position = 4
                }
                binding.spinner.setSelection(position)
            }
            binding.content.hint = getString(by.carkva_gazeta.malitounik.R.string.hint_pesny_title)
            builder.setView(binding.root)
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.add_pesny)) { _: DialogInterface, _: Int ->
                if (binding.content.text.toString() != "") {
                    addPesny()
                }
            }
            builder.setNegativeButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    private fun addPesny() {
        dialogAddPesnyListiner?.addPesny(binding.content.text.toString().trim(), pesny, arguments?.getString("fileName") ?: "new_filename")
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