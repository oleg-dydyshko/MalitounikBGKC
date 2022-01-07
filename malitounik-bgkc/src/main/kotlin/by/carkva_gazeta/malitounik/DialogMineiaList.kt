package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
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

    private lateinit var k: SharedPreferences
    private lateinit var binding: DialogListviewDisplayBinding
    private lateinit var alert: AlertDialog
    private var resetTollbarJob: Job? = null
    private val fileList = ArrayList<MineiaDay>()
    private lateinit var adapter: ListAdaprer
    private var resourceArrayList = ArrayList<String>()

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlackVybranoe
            val builder = AlertDialog.Builder(it, style)
            binding = DialogListviewDisplayBinding.inflate(LayoutInflater.from(it))
            builder.setView(binding.root)
            val dayOfYear = arguments?.getString("dayOfYear") ?: "1"
            val titleResource = arguments?.getString("titleResource") ?: "0"
            resourceArrayList = arguments?.getStringArrayList("resourceArrayList") ?: ArrayList()
            for (i in 0 until resourceArrayList.size) {
                val slujba = when {
                    resourceArrayList[i].contains(MineiaSviatochnaia.VIALIKIA_GADZINY) -> " - Вялікія гадзіны"
                    resourceArrayList[i].contains(MineiaSviatochnaia.ABEDNICA) -> " - Абедніца"
                    resourceArrayList[i].contains(MineiaSviatochnaia.UTRAN) -> " - Ютрань"
                    resourceArrayList[i].contains(MineiaSviatochnaia.LINURGIA) -> " - Літургія"
                    else -> " - Вячэрня"
                }
                fileList.add(MineiaDay(dayOfYear, "$titleResource $slujba", resourceArrayList[i]))
            }
            binding.content.setOnItemClickListener { _, _, position, _ ->
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("resurs", fileList[position].resource)
                    intent.putExtra("zmena_chastki", true)
                    intent.putExtra("title", fileList[position].titleResource)
                    startActivity(intent)

                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
            }
            adapter = ListAdaprer(it)
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

    private inner class ListAdaprer(context: Activity) : ArrayAdapter<MineiaDay>(context, R.layout.simple_list_item_2, R.id.label, fileList) {

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
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text.text = fileList[position].titleResource
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private data class MineiaDay(val dayOfYear: String, val titleResource: String, val resource: String)

    companion object {
        fun getInstance(dayOfYear: String, titleResource: String, resourceArrayList: ArrayList<String>, isSvity: Boolean): DialogMineiaList {
            val bundle = Bundle()
            bundle.putStringArrayList("resourceArrayList", resourceArrayList)
            bundle.putString("dayOfYear", dayOfYear)
            bundle.putString("titleResource", titleResource)
            bundle.putBoolean("isSvity", isSvity)
            val dialog = DialogMineiaList()
            dialog.arguments = bundle
            return dialog
        }
    }
}