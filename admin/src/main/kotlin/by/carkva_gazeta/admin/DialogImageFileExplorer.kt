package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminImageListItemBinding
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogListviewDisplayBinding
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FilenameFilter

class DialogImageFileExplorer : DialogFragment() {
    private val str = ArrayList<String>()
    private var firstLvl = true
    private val fileList = ArrayList<MyImageFile>()
    private var path: File? = null
    private var chosenFile = ""
    private var sdCard = true
    private var sdCard2 = false
    private lateinit var alert: AlertDialog
    private var _binding: DialogListviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadFileList() {
        fileList.clear()
        if (path?.exists() == true) {
            val filterDir = FilenameFilter { dir: File, filename: String ->
                val sel = File(dir, filename)
                sel.isDirectory && !sel.isHidden
            }
            val filterFile = FilenameFilter { dir: File, filename: String ->
                val sel = File(dir, filename)
                sel.isFile && !sel.isHidden && (sel.name.contains(".png", true) || sel.name.contains(".jp", true))
            }
            val uriUp = Uri.parse("android.resource://by.carkva_gazeta.malitounik/" + R.drawable.directory_up)
            if (!firstLvl) {
                fileList.add(MyImageFile("Верх", uriUp))
            } else {
                sdCard2 = true
                sdCard = false
                fileList.add(MyImageFile("Верх", uriUp))
            }
            val dList = path?.list(filterDir) ?: Array(0) { "" }
            dList.sort()
            val uriDir = Uri.parse("android.resource://by.carkva_gazeta.malitounik/" + R.drawable.directory_icon)
            for (aFList in dList) {
                fileList.add(MyImageFile(aFList, uriDir))
            }
            val fList = path?.list(filterFile) ?: Array(0) { "" }
            dList.sort()
            for (aFList in fList) {
                when {
                    aFList.contains(".png", true) -> {
                        val file = File(path.toString() + "/" + aFList)
                        fileList.add(MyImageFile(aFList, Uri.fromFile(file)))
                    }
                    aFList.contains(".jp", true) -> {
                        val file = File(path.toString() + "/" + aFList)
                        fileList.add(MyImageFile(aFList, Uri.fromFile(file)))
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogListviewDisplayBinding.inflate(LayoutInflater.from(it))
            val files = ContextCompat.getExternalFilesDirs(it, null)
            val uri = Uri.parse("android.resource://by.carkva_gazeta.malitounik/" + R.drawable.directory_icon)
            fileList.add(MyImageFile("Унутраная памяць", uri))
            if (files.size > 1) {
                fileList.add(MyImageFile("Карта SD", uri))
            }
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = "ВЫБЕРЫЦЕ ФАЙЛ"
            binding.content.selector = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.selector_default)
            binding.content.adapter = TitleListAdaprer(it)
            builder.setView(binding.root)
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            binding.content.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                if (sdCard) {
                    var dir = ContextCompat.getExternalFilesDirs(it, null)[i].absolutePath
                    val t1 = dir.indexOf("/Android/data/")
                    if (t1 != -1) dir = dir.substring(0, t1)
                    path = File(dir)
                    loadFileList()
                    (binding.content.adapter as TitleListAdaprer).notifyDataSetChanged()
                } else if (sdCard2 && i == 0) {
                    fileList.clear()
                    sdCard2 = false
                    sdCard = true
                    fileList.add(MyImageFile("Унутраная памяць", uri))
                    if (files.size > 1) {
                        fileList.add(MyImageFile("Карта SD", uri))
                    }
                    (binding.content.adapter as TitleListAdaprer).notifyDataSetChanged()
                } else {
                    sdCard2 = false
                    chosenFile = fileList[i].name
                    val sel = File(path.toString() + "/" + chosenFile)
                    if (sel.isDirectory) {
                        firstLvl = false
                        str.add(chosenFile)
                        path = File(sel.toString() + "")
                        loadFileList()
                        (binding.content.adapter as TitleListAdaprer).notifyDataSetChanged()
                    } else if (chosenFile == "Верх" && !sel.exists()) {
                        val s = str.removeAt(str.size - 1)
                        path = File(path.toString().substring(0, path.toString().lastIndexOf(s)))
                        if (str.isEmpty()) {
                            firstLvl = true
                        }
                        loadFileList()
                        (binding.content.adapter as TitleListAdaprer).notifyDataSetChanged()
                    } else {
                        val isSviaty = arguments?.getBoolean("isSviaty") ?: false
                        val dialogImageFileLoad = DialogImageFileLoad.getInstance(sel.absolutePath, isSviaty)
                        dialogImageFileLoad.show(childFragmentManager, "dialogImageFileLoad")
                    }
                }
            }
        }
        return alert
    }

    private inner class TitleListAdaprer(private val mContext: Activity) : ArrayAdapter<MyImageFile>(mContext, R.layout.admin_image_list_item, fileList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = AdminImageListItemBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label, binding.imageView)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = fileList[position].name
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            Picasso.with(mContext).load(fileList[position].file).resize(600, 1000).onlyScaleDown().centerInside().into(viewHolder.image)
            return rootView
        }

    }

    companion object {
        fun getInstance(isSviaty: Boolean): DialogImageFileExplorer {
            val dialogImageFileLoad = DialogImageFileExplorer()
            val bundle = Bundle()
            bundle.putBoolean("isSviaty", isSviaty)
            dialogImageFileLoad.arguments = bundle
            return dialogImageFileLoad
        }
    }

    private class ViewHolder(var text: TextView, var image: ImageView)

    private class MyImageFile(val name: String, val file: Uri)
}