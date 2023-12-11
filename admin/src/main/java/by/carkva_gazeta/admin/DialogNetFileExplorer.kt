package by.carkva_gazeta.admin

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
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.databinding.DialogListviewDisplayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DialogNetFileExplorer : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var mListener: DialogNetFileExplorerListener? = null
    private lateinit var adapter: TitleListAdaprer
    private val fileList = ArrayList<MyNetFile>()
    private var dir = ""
    private var binding: DialogListviewDisplayBinding? = null
    private var netFileJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPause() {
        super.onPause()
        netFileJob?.cancel()
    }

    internal interface DialogNetFileExplorerListener {
        fun onDialogNetFile(dirToFile: String, fileName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogNetFileExplorerListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogNetFileExplorerListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding = DialogListviewDisplayBinding.inflate(LayoutInflater.from(it))
            binding?.let { binding ->
                binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)
                binding.content.selector = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.selector_default)
                adapter = TitleListAdaprer(it)
                binding.content.adapter = adapter
                builder.setView(binding.root)
                getDirListRequest("")
                binding.content.setOnItemLongClickListener { _, _, position, _ ->
                    if (!(fileList[position].resources == R.drawable.directory_up || fileList[position].resources == R.drawable.directory_icon)) {
                        val contextMenu = DialogContextMenu.getInstance(position, dir + "/" + fileList[position].title, true)
                        contextMenu.show(childFragmentManager, "contextMenu")
                    }
                    return@setOnItemLongClickListener true
                }
                binding.content.setOnItemClickListener { _, _, position, _ ->
                    when (fileList[position].resources) {
                        R.drawable.directory_up -> {
                            val t1 = dir.lastIndexOf("/")
                            dir = dir.substring(0, t1)
                            getDirListRequest(dir)
                        }

                        R.drawable.directory_icon -> {
                            dir = dir + "/" + fileList[position].title
                            getDirListRequest(dir)
                        }

                        else -> {
                            mListener?.onDialogNetFile(dir + "/" + fileList[position].title, fileList[position].title)
                            dialog?.cancel()
                        }
                    }
                }
                builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            }
            alert = builder.create()
        }
        return alert
    }

    fun update() {
        getDirListRequest(dir)
    }

    private fun getDirListRequest(dir: String) {
        if (MainActivity.isNetworkAvailable()) {
            netFileJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    fileList.clear()
                    val temp = ArrayList<MyNetFile>()
                    val list = Malitounik.referens.child("/$dir").list(1000).await()
                    if (dir != "") {
                        val t1 = dir.lastIndexOf("/")
                        temp.add(MyNetFile(R.drawable.directory_up, dir.substring(t1 + 1)))
                    }
                    list.prefixes.forEach {
                        temp.add(MyNetFile(R.drawable.directory_icon, it.name))
                    }
                    list.items.forEach {
                        if (it.name.contains(".htm")) {
                            temp.add(MyNetFile(R.drawable.file_html_icon, it.name))
                        } else if (it.name.contains(".json")) {
                            temp.add(MyNetFile(R.drawable.file_json_icon, it.name))
                        } else if (it.name.contains(".php")) {
                            temp.add(MyNetFile(R.drawable.file_php_icon, it.name))
                        } else {
                            temp.add(MyNetFile(R.drawable.file_txt_icon, it.name))
                        }
                    }
                    fileList.addAll(temp)
                    adapter.notifyDataSetChanged()
                } catch (e: Throwable) {
                    activity?.let {
                        MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
            }
        }
    }

    private inner class TitleListAdaprer(private val mContext: Activity) : ArrayAdapter<MyNetFile>(mContext, R.layout.admin_simple_list_item, fileList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = AdminSimpleListItemBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = fileList[position].title
            val image = ContextCompat.getDrawable(mContext, fileList[position].resources)
            val density = resources.displayMetrics.density.toInt()
            image?.setBounds(0, 0, 48 * density, 48 * density)
            viewHolder.text.setCompoundDrawables(image, null, null, null)
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    private data class MyNetFile(val resources: Int, val title: String)
}