package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogListviewDisplayBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import kotlinx.coroutines.Job


class DialogMineiaList : DialogFragment() {

    private lateinit var binding: DialogListviewDisplayBinding
    private lateinit var alert: AlertDialog
    private var resetTollbarJob: Job? = null
    private lateinit var k: SharedPreferences

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val prefEditors = k.edit()
        prefEditors.putInt("sortMineiaList", 0)
        prefEditors.apply()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        (activity as? BaseActivity)?.let {
            val dzenNoch = it.getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlackVybranoe
            val builder = AlertDialog.Builder(it, style)
            binding = DialogListviewDisplayBinding.inflate(LayoutInflater.from(it))
            builder.setView(binding.root)
            if (dzenNoch) {
                binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            }
            val fileList = ArrayList<SlugbovyiaTextuData>()
            val dayOfYear = arguments?.getString("dayOfYear") ?: "1"
            val slugba = SlugbovyiaTextu()
            val mineia = slugba.getMineiaMesiachnaia()
            mineia.forEach { data ->
                if (data.day == dayOfYear.toInt()) {
                    fileList.add(data)
                }
            }
            k = it.getSharedPreferences("biblia", MODE_PRIVATE)
            val prefEditors = k.edit()
            prefEditors.putInt("sortMineiaList", 1)
            prefEditors.apply()
            fileList.sort()
            binding.content.setOnItemClickListener { _, _, position, _ ->
                if (it.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("resurs", fileList[position].resource)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", fileList[position].title)
                    startActivity(intent)

                } else {
                    it.installFullMalitounik()
                }
            }
            val adapter = ListAdaprer(it, fileList)
            binding.content.adapter = adapter
            binding.title.text = if (arguments?.getBoolean("isSvity", false) == true) getString(R.string.mineia_sviatochnaia)
            else getString(R.string.mineia_shtodzennaia)
            if (dzenNoch) {
                binding.content.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            } else {
                binding.content.selector = ContextCompat.getDrawable(it, R.drawable.selector_default)
            }
            builder.setPositiveButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    private class ListAdaprer(private val context: Activity, private val fileList: ArrayList<SlugbovyiaTextuData>) : ArrayAdapter<SlugbovyiaTextuData>(context, R.layout.simple_list_item_2, R.id.label, fileList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = (context as BaseActivity).getBaseDzenNoch()
            val slugba = ". " + SlugbovyiaTextu().getNazouSluzby(fileList[position].sluzba)
            viewHolder.text.text = context.resources.getString(R.string.mineia_slugba, fileList[position].title, slugba)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    companion object {
        fun getInstance(dayOfYear: String): DialogMineiaList {
            val bundle = Bundle()
            bundle.putString("dayOfYear", dayOfYear)
            val dialog = DialogMineiaList()
            dialog.arguments = bundle
            return dialog
        }
    }
}