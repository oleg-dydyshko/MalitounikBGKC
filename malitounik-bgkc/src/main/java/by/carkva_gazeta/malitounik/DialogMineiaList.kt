package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        (activity as? BaseActivity)?.let {
            val dzenNoch = it.getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            binding = DialogListviewDisplayBinding.inflate(layoutInflater)
            builder.setView(binding.root)
            val fileList = (activity as? MineiaMesiachnaia)?.getMineiaListDialog() ?: ArrayList()
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
            binding.title.text = getString(R.string.mineia_shtodzennaia)
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

    private class ListAdaprer(private val context: Activity, private val fileList: ArrayList<MineiaMesiachnaia.MineiaList>) : ArrayAdapter<MineiaMesiachnaia.MineiaList>(context, R.layout.simple_list_item_2, R.id.label, fileList) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(context.layoutInflater, parent, false)
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
}