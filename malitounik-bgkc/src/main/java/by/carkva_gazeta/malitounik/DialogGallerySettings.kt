package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogSpinnerDisplayBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding

class DialogGallerySettings : DialogFragment() {
    private var setid = 4
    private val arrayList = ArrayList<Int>()
    private var mListener: DialogGallerySettingsListener? = null
    private lateinit var alert: AlertDialog
    private var _binding: DialogSpinnerDisplayBinding? = null
    private val binding get() = _binding!!
    private lateinit var k: SharedPreferences

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

    internal interface DialogGallerySettingsListener {
        fun setSpeedGallery(speed: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogGallerySettingsListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogGallerySettingsListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("setid", setid)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogSpinnerDisplayBinding.inflate(LayoutInflater.from(it))
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.gallery_slayd_show_speed)
            setid = savedInstanceState?.getInt("setid") ?: k.getInt("gallerySettingsTime", 4)
            for (i in 1..10) {
                arrayList.add(i)
            }
            val arrayAdapter = ListAdapter(it, arrayList)
            binding.content.adapter = arrayAdapter
            binding.content.setSelection(setid - 1)
            binding.content.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setid = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            builder.setView(binding.root)
            builder.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                val prefEditor = k.edit()
                prefEditor.putInt("gallerySettingsTime", setid + 1)
                prefEditor.apply()
                mListener?.setSpeedGallery(setid + 1) }
            alert = builder.create()
        }
        return alert
    }

    private class ListAdapter(private val mContext: Activity, private val arrayList: ArrayList<Int>) : ArrayAdapter<Int>(mContext, R.layout.simple_list_item_1, arrayList) {
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
            viewHolder.text.text = mContext.resources.getString(R.string.settings_gallery_item_name, arrayList[position])
            if (dzenNoch) viewHolder.text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else viewHolder.text.setBackgroundResource(R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val dzenNoch = (mContext as BaseActivity).getBaseDzenNoch()
            val text = v as TextView
            text.text = mContext.resources.getString(R.string.settings_gallery_item_name, arrayList[position])
            if (dzenNoch) text.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            else text.setBackgroundResource(R.drawable.selector_default)
            return v
        }
    }

    private class ViewHolder(var text: TextView)
}