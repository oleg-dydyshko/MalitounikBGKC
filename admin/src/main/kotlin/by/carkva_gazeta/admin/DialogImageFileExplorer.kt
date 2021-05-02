package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminImageListItemBinding
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FilenameFilter

class DialogImageFileExplorer : DialogFragment() {
    private val str = ArrayList<String>()
    private var firstLvl = true
    private val fileList = ArrayList<MyImageFile>()
    private var path: File? = null
    private var chosenFile = ""
    private var mListener: DialogFileExplorerListener? = null
    private var sdCard = true
    private var sdCard2 = false
    private lateinit var alert: AlertDialog

    internal interface DialogFileExplorerListener {
        fun onDialogFile(file: File)
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
            val files = ContextCompat.getExternalFilesDirs(it, null)
            val uri = Uri.parse("android.resource://by.carkva_gazeta.malitounik/" + R.drawable.directory_icon)
            fileList.add(MyImageFile("Унутраная памяць", uri))
            if (files.size > 1) {
                fileList.add(MyImageFile("Карта SD", uri))
            }
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = "ВЫБЕРЫЦЕ ФАЙЛ"
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorWhite))
            linear.addView(textViewZaglavie)
            val listViewCompat = ListView(it)
            listViewCompat.selector = ContextCompat.getDrawable(it, by.carkva_gazeta.malitounik.R.drawable.selector_default)
            val listAdaprer = TitleListAdaprer(it)
            listViewCompat.adapter = listAdaprer
            linear.addView(listViewCompat)
            builder.setView(linear)
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            listViewCompat.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                if (sdCard) {
                    var dir = ContextCompat.getExternalFilesDirs(it, null)[i].absolutePath
                    val t1 = dir.indexOf("/Android/data/")
                    if (t1 != -1) dir = dir.substring(0, t1)
                    path = File(dir)
                    loadFileList()
                    listAdaprer.notifyDataSetChanged()
                } else if (sdCard2 && i == 0) {
                    fileList.clear()
                    sdCard2 = false
                    sdCard = true
                    fileList.add(MyImageFile("Унутраная памяць", uri))
                    if (files.size > 1) {
                        fileList.add(MyImageFile("Карта SD", uri))
                    }
                    listAdaprer.notifyDataSetChanged()
                } else {
                    sdCard2 = false
                    chosenFile = fileList[i].name
                    val sel = File(path.toString() + "/" + chosenFile)
                    if (sel.isDirectory) {
                        firstLvl = false
                        str.add(chosenFile)
                        path = File(sel.toString() + "")
                        loadFileList()
                        listAdaprer.notifyDataSetChanged()
                    } else if (chosenFile == "Верх" && !sel.exists()) {
                        val s = str.removeAt(str.size - 1)
                        path = File(path.toString().substring(0, path.toString().lastIndexOf(s)))
                        if (str.isEmpty()) {
                            firstLvl = true
                        }
                        loadFileList()
                        listAdaprer.notifyDataSetChanged()
                    } else {
                        mListener?.onDialogFile(sel)
                        alert.cancel()
                    }
                }
            }
        }
        return alert
    }

    private inner class TitleListAdaprer(mContext: Activity) : ArrayAdapter<MyImageFile>(mContext, R.layout.admin_image_list_item, fileList) {
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
            Picasso.get().load(fileList[position].file).resize(600, 1000).onlyScaleDown().centerInside().into(viewHolder.image)
            return rootView
        }

    }

    private class ViewHolder(var text: TextViewRobotoCondensed, var image: ImageView)

    private class MyImageFile(val name: String, val file: Uri)
}