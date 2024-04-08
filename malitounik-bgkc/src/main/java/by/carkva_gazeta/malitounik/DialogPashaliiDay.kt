package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogPashaliiDayBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding

class DialogPashaliiDay : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogPashaliiDayBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogPashaliiDayListener? = null

    interface DialogPashaliiDayListener {
        fun setDataPashi(day: Int, month: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPashaliiDayListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogPashaliiDayListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogPashaliiDayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            val dayList = ArrayList<String>()
            for (i in 1..31)
                dayList.add(i.toString())
            binding.day.adapter = ListAdapterDay(it, dayList)
            val monthList = ArrayList<String>()
            monthList.add("Сакавіка")
            monthList.add("Красавіка")
            binding.month.adapter = ListAdapterDay(it, monthList)
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface, _: Int ->
                mListener?.setDataPashi(binding.day.selectedItemPosition + 1, binding.month.selectedItemPosition + 3)
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    private class ListAdapterDay(private val mContext: Activity, private val arrayList: ArrayList<String>) : ArrayAdapter<String>(mContext, R.layout.simple_list_item_1, arrayList) {
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
            viewHolder.text.text = arrayList[position]
            if (dzenNoch) viewHolder.text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            val text = v as TextView
            text.text = arrayList[position]
            if (dzenNoch) text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else text.setBackgroundResource(R.drawable.selector_default)
            return v
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        fun getInstance(notification: Int): DialogPashaliiDay {
            val dialogDelite = DialogPashaliiDay()
            val bundle = Bundle()
            bundle.putInt("notification", notification)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}