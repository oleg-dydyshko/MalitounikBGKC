package by.carkva_gazeta.biblijateka

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import com.google.android.play.core.splitcompat.SplitCompat
import java.io.File
import java.io.FilenameFilter
import java.util.*
import kotlin.collections.ArrayList

class DialogFileExplorer : DialogFragment() {
    private val str = ArrayList<String>()
    private var firstLvl = true
    private val fileList = ArrayList<MyFile>()
    private var path: File? = null
    private var chosenFile: String = ""
    private var mListener: DialogFileExplorerListener? = null
    private lateinit var chin: SharedPreferences
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
        val dzenNoch = chin.getBoolean("dzen_noch", false)
        fileList.clear()
        if (path?.exists() == true) {
            val filterDir = FilenameFilter { dir: File, filename: String ->
                val sel = File(dir, filename)
                sel.isDirectory && !sel.isHidden
            }
            val filterFile = FilenameFilter { dir: File, filename: String ->
                val sel = File(dir, filename)
                sel.isFile && !sel.isHidden && (sel.name.toLowerCase(Locale.getDefault()).contains(".pdf") || sel.name.toLowerCase(Locale.getDefault()).contains(".epub") || sel.name.toLowerCase(Locale.getDefault()).contains(".fb2") || sel.name.toLowerCase(Locale.getDefault()).contains(".txt") || sel.name.toLowerCase(Locale.getDefault()).contains(".htm"))
            }
            if (!firstLvl) {
                if (dzenNoch) {
                    fileList.add(MyFile("Верх", R.drawable.directory_up_black))
                } else {
                    fileList.add(MyFile("Верх", R.drawable.directory_up))
                }
            } else {
                sdCard2 = true
                sdCard = false
                if (dzenNoch) {
                    fileList.add(MyFile("Верх", R.drawable.directory_up_black))
                } else {
                    fileList.add(MyFile("Верх", R.drawable.directory_up))
                }
            }
            val dList = path?.list(filterDir) ?: Array(0) { "" }
            dList.sort()
            for (aFList in dList) {
                if (dzenNoch) {
                    fileList.add(MyFile(aFList, R.drawable.directory_icon_black))
                } else {
                    fileList.add(MyFile(aFList, R.drawable.directory_icon))
                }
            }
            val fList = path?.list(filterFile) ?: Array(0) { "" }
            dList.sort()
            for (aFList in fList) {
                if (aFList.toLowerCase(Locale.getDefault()).contains(".pdf")) {
                    if (dzenNoch) {
                        fileList.add(MyFile(aFList, R.drawable.file_icon_black))
                    } else {
                        fileList.add(MyFile(aFList, R.drawable.file_icon))
                    }
                } else if (aFList.toLowerCase(Locale.getDefault()).contains(".fb2")) {
                    if (dzenNoch) {
                        fileList.add(MyFile(aFList, R.drawable.file_fb2_icon_black))
                    } else {
                        fileList.add(MyFile(aFList, R.drawable.file_fb2_icon))
                    }
                } else if (aFList.toLowerCase(Locale.getDefault()).contains(".txt")) {
                    if (dzenNoch) {
                        fileList.add(MyFile(aFList, R.drawable.file_txt_icon_black))
                    } else {
                        fileList.add(MyFile(aFList, R.drawable.file_txt_icon))
                    }
                } else if (aFList.toLowerCase(Locale.getDefault()).contains(".htm")) {
                    if (dzenNoch) {
                        fileList.add(MyFile(aFList, R.drawable.file_html_icon_black))
                    } else {
                        fileList.add(MyFile(aFList, R.drawable.file_html_icon))
                    }
                } else {
                    if (dzenNoch) {
                        fileList.add(MyFile(aFList, R.drawable.file_epub_icon_black))
                    } else {
                        fileList.add(MyFile(aFList, R.drawable.file_epub_icon))
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            val files = ContextCompat.getExternalFilesDirs(it, null)
            if (dzenNoch) {
                fileList.add(MyFile("Унутраная памяць", R.drawable.directory_icon_black))
            } else {
                fileList.add(MyFile("Унутраная памяць", R.drawable.directory_icon))
            }
            if (files.size > 1) {
                if (dzenNoch) {
                    fileList.add(MyFile("Карта SD", R.drawable.directory_icon_black))
                } else {
                    fileList.add(MyFile("Карта SD", R.drawable.directory_icon))
                }
            }
            val builder = AlertDialog.Builder(it)
            val linear = LinearLayout(it)
            linear.orientation = LinearLayout.VERTICAL
            val textViewZaglavie = TextViewRobotoCondensed(it)
            if (dzenNoch) textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_black)) else textViewZaglavie.setBackgroundColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary))
            val density = resources.displayMetrics.density
            val realpadding = (10 * density).toInt()
            textViewZaglavie.setPadding(realpadding, realpadding, realpadding, realpadding)
            textViewZaglavie.text = "ВЫБЕРЫЦЕ ФАЙЛ"
            textViewZaglavie.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textViewZaglavie.setTypeface(null, Typeface.BOLD)
            textViewZaglavie.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorIcons))
            linear.addView(textViewZaglavie)
            val listViewCompat = ListView(it)
            val listAdaprer = TitleListAdaprer(it)
            listViewCompat.adapter = listAdaprer
            linear.addView(listViewCompat)
            builder.setView(linear)
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.CANCEL)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            }
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
                    if (dzenNoch) {
                        fileList.add(MyFile("Унутраная памяць", R.drawable.directory_icon_black))
                    } else {
                        fileList.add(MyFile("Унутраная памяць", R.drawable.directory_icon))
                    }
                    if (files.size > 1) {
                        if (dzenNoch) {
                            fileList.add(MyFile("Карта SD", R.drawable.directory_icon_black))
                        } else {
                            fileList.add(MyFile("Карта SD", R.drawable.directory_icon))
                        }
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

    private inner class TitleListAdaprer(private val mContext: Activity) : ArrayAdapter<MyFile>(mContext, R.layout.biblijateka_simple_list_item, fileList) {
        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            activity?.let {
                SplitCompat.install(it)
            }
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = mContext.layoutInflater.inflate(R.layout.biblijateka_simple_list_item, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            viewHolder.text?.text = fileList[position].name
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            val image = ContextCompat.getDrawable(mContext, fileList[position].resources)
            val density = resources.displayMetrics.density.toInt()
            image?.setBounds(0, 0, 48 * density, 48 * density)
            viewHolder.text?.setCompoundDrawables(image, null, null, null)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorbackground_material_dark_ligte)
                viewHolder.text?.setTextColor(ContextCompat.getColor(mContext, by.carkva_gazeta.malitounik.R.color.colorIcons))
            }
            return rootView
        }

    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
    
    private class MyFile(val name: String, val resources: Int)
}