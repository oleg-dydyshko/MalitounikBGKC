package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSpinnerDisplayBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import java.util.Calendar

class DialogPrazdnik : DialogFragment() {
    private var setid = 10
    private val arrayList = ArrayList<Int>()
    private var mListener: DialogPrazdnikListener? = null
    private lateinit var alert: AlertDialog
    private var _binding: DialogSpinnerDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

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
            _binding = DialogSpinnerDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            binding.title.text = resources.getString(R.string.carkva_sviaty)
            val c = Calendar.getInstance()
            if (savedInstanceState != null) {
                setid = savedInstanceState.getInt("setid")
                savedInstanceState.getIntegerArrayList("arrayList")?.let { it1 -> arrayList.addAll(it1) }
            } else {
                for (i in c[Calendar.YEAR] + 10 downTo SettingsActivity.GET_CALIANDAR_YEAR_MIN) {
                    arrayList.add(i)
                }
            }
            val arrayAdapter = ListAdapter(it, arrayList)
            binding.content.adapter = arrayAdapter
            for (i in arrayList.indices) {
                if (arrayList[i] == (arguments?.getInt("year") ?: c[Calendar.YEAR])) {
                    setid = i
                }
            }
            binding.content.setSelection(setid)
            binding.content.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setid = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            builder.setView(binding.root)
            builder.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.setPrazdnik(arrayList[setid]) }
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

    private class ListAdapter(private val mContext: Activity, private val arrayList: ArrayList<Int>) : ArrayAdapter<Int>(mContext, R.layout.simple_list_item_1, arrayList) {
        private val gc = Calendar.getInstance()
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            if (gc[Calendar.YEAR] == arrayList[position]) viewHolder.text.typeface = MainActivity.createFont(Typeface.BOLD)
            else viewHolder.text.typeface = MainActivity.createFont(Typeface.NORMAL)
            viewHolder.text.text = arrayList[position].toString()
            if (dzenNoch)
                viewHolder.text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else
                viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            val text = v as TextView
            if (gc[Calendar.YEAR] == arrayList[position]) text.typeface = MainActivity.createFont(Typeface.BOLD)
            else text.typeface = MainActivity.createFont(Typeface.NORMAL)
            text.text = arrayList[position].toString()
            if (dzenNoch)
                text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else
                text.setBackgroundResource(R.drawable.selector_default)
            return v
        }
    }

    private class ViewHolder(var text: TextView)
}