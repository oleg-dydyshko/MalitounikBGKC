package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSpinnerDisplayBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemColorBinding

class DialogPerevodBiblii : DialogFragment() {
    private var dzenNoch = false
    private lateinit var alert: AlertDialog
    private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI
    private var _binding: DialogSpinnerDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogSpinnerDisplayBinding.inflate(layoutInflater)
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            val perevodList = ArrayList<PerevodBiblii>()
            perevodList.add(PerevodBiblii(getString(R.string.title_biblia_bokun), DialogVybranoeBibleList.PEREVODBOKUNA))
            perevodList.add(PerevodBiblii(getString(R.string.title_biblia), DialogVybranoeBibleList.PEREVODSEMUXI))
            perevodList.add(PerevodBiblii(getString(R.string.title_biblia_charniauski), DialogVybranoeBibleList.PEREVODCARNIAUSKI))
            perevodList.add(PerevodBiblii(getString(R.string.bsinaidal), DialogVybranoeBibleList.PEREVODSINOIDAL))
            binding.title.text = resources.getString(R.string.perevod)
            binding.content.adapter = PerevodAdapter(it, perevodList)
            perevod = k.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
            val setPos = when (perevod) {
                DialogVybranoeBibleList.PEREVODBOKUNA -> 0
                DialogVybranoeBibleList.PEREVODSEMUXI -> 1
                DialogVybranoeBibleList.PEREVODCARNIAUSKI -> 2
                DialogVybranoeBibleList.PEREVODSINOIDAL -> 3
                else -> 1
            }
            binding.content.setSelection(setPos)
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                val pos = perevodList[binding.content.selectedItemPosition]
                val edit = k.edit()
                edit.putString("perevod", pos.perevod)
                edit.apply()
                dialog.cancel()
                if (perevod != pos.perevod) it.recreate()
            }
            builder.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
        }
        return alert
    }

    private class PerevodAdapter(private val context: Activity, private val perevodList: ArrayList<PerevodBiblii>) : ArrayAdapter<PerevodBiblii>(context, R.layout.simple_list_item_color, R.id.label, perevodList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderColor
            if (convertView == null) {
                val binding = SimpleListItemColorBinding.inflate(context.layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolderColor(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderColor
            }
            viewHolder.text.text = perevodList[position].title
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view as TextView
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            if (dzenNoch)
                textView.setBackgroundResource(R.drawable.selector_dark)
            else
                textView.setBackgroundResource(R.drawable.selector_default)
            textView.text = perevodList[position].title
            return view
        }
    }

    private class ViewHolderColor(var text: TextView)

    private data class PerevodBiblii(val title: String, val perevod: String)
}