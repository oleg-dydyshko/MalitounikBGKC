package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminDialogImageLoadBinding
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

class DialogImageFileLoad : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var _binding: AdminDialogImageLoadBinding? = null
    private val binding get() = _binding!!
    private var arrayList = ArrayList<String>()
    private var mListener: DialogFileExplorerListener? = null

    internal interface DialogFileExplorerListener {
        fun onDialogFile(absolutePath: String, image: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogFileExplorerListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogFileExplorerListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = AdminDialogImageLoadBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            builder.setView(binding.root)
            arrayList.add("Дадаць ікону па чарзе")
            arrayList.add("Перазапісаць 1 ікону")
            arrayList.add("Перазапісаць 2 ікону")
            arrayList.add("Перазапісаць 3 ікону")
            arrayList.add("Перазапісаць 4 ікону")
            binding.title.text = "ЗАХАВАЦЬ ІКОНУ"
            val arrayAdapter = ListAdapter(it)
            binding.content.adapter = arrayAdapter
            val path = arguments?.getString("path") ?: ""
            val isSviaty = arguments?.getBoolean("isSviaty") ?: false
            if (isSviaty) binding.content.visibility = View.GONE
            val file = File(path)
            Picasso.with(it).load(file).resize(600, 1000).onlyScaleDown().centerInside().into(binding.icon)
            builder.setNegativeButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.save_sabytie)) { _: DialogInterface?, _: Int ->
                mListener?.onDialogFile(path, binding.content.selectedItemPosition)
            }
            alert = builder.create()
        }
        return alert
    }

    private inner class ListAdapter(mContext: Activity) : ArrayAdapter<String>(mContext, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, arrayList) {
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
            viewHolder.text.text = arrayList[position]
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val text = v as TextView
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
            text.text = arrayList[position]
            text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }
    }

    companion object {
        fun getInstance(path: String, isSviaty: Boolean): DialogImageFileLoad {
            val dialogImageFileLoad = DialogImageFileLoad()
            val bundle = Bundle()
            bundle.putString("path", path)
            bundle.putBoolean("isSviaty", isSviaty)
            dialogImageFileLoad.arguments = bundle
            return dialogImageFileLoad
        }
    }

    private class ViewHolder(var text: TextView)
}