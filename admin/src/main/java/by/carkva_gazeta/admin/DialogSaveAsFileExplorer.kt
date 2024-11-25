package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminDialigSaveAsBinding
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DialogSaveAsFileExplorer : DialogFragment() {

    private lateinit var alert: AlertDialog
    private var mListener: DialogSaveAsFileExplorerListener? = null
    private lateinit var adapter: TitleListAdaprer
    private val fileList = ArrayList<MyNetFile>()
    private var dir = ""
    private var oldName = ""
    private var fileName = ""
    private var filenameTitle = ""
    private var binding: AdminDialigSaveAsBinding? = null
    private var saveAsFileJob: Job? = null
    private val textWatcher = object : TextWatcher {
        private var editPosition = 0
        private var check = 0
        private var editch = true

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            editch = count != after
            check = after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable?) {
            if (editch) {
                var edit = s.toString()
                if (!edit.contains(".php", true)) {
                    edit = edit.replace("-", "_")
                }
                edit = edit.replace(" ", "_").lowercase()
                if (edit[0].isDigit()) edit = "mm_$edit"
                if (check != 0) {
                    binding?.let { binding ->
                        binding.edittext.removeTextChangedListener(this)
                        binding.edittext.setText(edit)
                        binding.edittext.setSelection(editPosition)
                        binding.edittext.addTextChangedListener(this)
                    }
                }
            }
        }
    }

    internal interface DialogSaveAsFileExplorerListener {
        fun onDialogSaveAsFile(dir: String, oldFileName: String, fileName: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPause() {
        super.onPause()
        saveAsFileJob?.cancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogSaveAsFileExplorerListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogSaveAsFileExplorerListener")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val dialog = dialog as? AlertDialog
        val positiveButton = dialog?.getButton(Dialog.BUTTON_POSITIVE)
        positiveButton?.setOnClickListener {
            setFileName()
        }
    }

    fun vypraulenneFilename() {
        var fileName = binding?.edittext?.text.toString()
        if (!fileName.contains(".php", true)) {
            fileName = fileName.replace("-", "_")
        }
        fileName = fileName.replace(" ", "_").lowercase()
        val mm = if (fileName[0].isDigit()) "mm_"
        else ""
        fileName = "$mm$fileName"
        binding?.edittext?.setText(fileName)
        setFileName()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            val builder = AlertDialog.Builder(fragmentActivity, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding = AdminDialigSaveAsBinding.inflate(layoutInflater)
            binding?.let { binding ->
                binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.save_as_up)
                binding.content.setOnClickListener {
                    val dialogPasochnicaMkDir = DialogPasochnicaMkDir.getInstance(dir, oldName, binding.edittext.text.toString())
                    dialogPasochnicaMkDir.show(childFragmentManager, "dialogPasochnicaMkDir")
                }
                oldName = arguments?.getString("oldName", "") ?: ""
                val t1 = oldName.indexOf("(")
                if (t1 != -1 && t1 == 0) {
                    val t2 = oldName.indexOf(")")
                    val t3 = oldName.lastIndexOf(".")
                    filenameTitle = if (t3 != -1) oldName.substring(t2 + 2, t3)
                    else oldName.substring(t2 + 2)
                    fileName = oldName.substring(1, t2) + ".html"
                } else {
                    fileName = oldName
                }
                binding.edittext.addTextChangedListener(textWatcher)
                binding.edittext.setText(fileName)
                binding.filetitle.text = filenameTitle
                if (filenameTitle == "") binding.filetitle.visibility = View.GONE
                binding.listView.selector = ContextCompat.getDrawable(fragmentActivity, by.carkva_gazeta.malitounik.R.drawable.selector_default)
                adapter = TitleListAdaprer(fragmentActivity)
                binding.listView.adapter = adapter
                builder.setView(binding.root)
                getDirListRequest("")
                binding.listView.setOnItemClickListener { _, _, position, _ ->
                    when (fileList[position].resources) {
                        R.drawable.directory_up -> {
                            val t4 = dir.lastIndexOf("/")
                            dir = dir.substring(0, t4)
                            getDirListRequest(dir)
                        }

                        R.drawable.directory_icon -> {
                            dir = dir + "/" + fileList[position].title
                            getDirListRequest(dir)
                        }

                        else -> {
                            binding.edittext.setText(fileList[position].title)
                        }
                    }
                }
                builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.save_sabytie), null)
                builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            }
            alert = builder.create()
        }
        return alert
    }

    private fun setFileName() {
        var error = false
        val editText = binding?.edittext?.text.toString()
        if (editText[0].isDigit()) error = true
        for (c in editText) {
            if (c.isUpperCase()) error = true
        }
        if (error) {
            val dialogFileNameError = DialogFileNameError()
            dialogFileNameError.show(childFragmentManager, "DialogFileNameError")
            return
        }
        mListener?.onDialogSaveAsFile(dir, oldName, editText)
        dialog?.cancel()
    }

    private fun getDirListRequest(dir: String) {
        if (MainActivity.isNetworkAvailable()) {
            saveAsFileJob = CoroutineScope(Dispatchers.Main).launch {
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
                } catch (e: Throwable) {
                    activity?.let {
                        MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
                adapter.notifyDataSetChanged()
            }
        } else {
            activity?.let {
                MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
            }
        }
    }

    private inner class TitleListAdaprer(private val mContext: Activity) : ArrayAdapter<MyNetFile>(mContext, R.layout.admin_simple_list_item, fileList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = AdminSimpleListItemBinding.inflate(layoutInflater, parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val title = fileList[position].title
            viewHolder.text.text = title
            if (title == "admin" || title == "bogashlugbovya" || title == "parafii_bgkc" || title == "prynagodnyia" || title == "pesny") {
                viewHolder.text.typeface = MainActivity.createFont(Typeface.BOLD)
            } else {
                viewHolder.text.typeface = MainActivity.createFont(Typeface.NORMAL)
            }
            viewHolder.text.background = ContextCompat.getDrawable(mContext, by.carkva_gazeta.malitounik.R.color.colorWhite)
            val image = ContextCompat.getDrawable(mContext, fileList[position].resources)
            val density = resources.displayMetrics.density.toInt()
            image?.setBounds(0, 0, 48 * density, 48 * density)
            viewHolder.text.setCompoundDrawables(image, null, null, null)
            return rootView
        }

    }

    private class ViewHolder(var text: TextView)

    private data class MyNetFile(val resources: Int, val title: String)

    companion object {
        fun getInstance(oldName: String): DialogSaveAsFileExplorer {
            val error = DialogSaveAsFileExplorer()
            val bundle = Bundle()
            bundle.putString("oldName", oldName)
            error.arguments = bundle
            return error
        }
    }
}