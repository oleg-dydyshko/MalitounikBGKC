package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.hardware.SensorEvent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminDialigSaveAsBinding
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Character.UnicodeBlock
import java.util.Calendar

class PiasochnicaSaveAsFileExplorer : BaseActivity(), DialogPasochnicaMkDir.DialogPasochnicaMkDirListener {

    private lateinit var adapter: TitleListAdaprer
    private val fileList = ArrayList<MyNetFile>()
    private var dir = ""
    private var oldName = ""
    private var fileName = ""
    private var filenameTitle = ""
    private lateinit var binding: AdminDialigSaveAsBinding
    private var saveAsFileJob: Job? = null
    private var resetTollbarJob: Job? = null
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
                val newEdit = checkCyrilic(edit)
                if (newEdit != edit) {
                    editPosition -= 1
                    edit = newEdit
                    MainActivity.toastView(this@PiasochnicaSaveAsFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.admin_cyrylic_no_support))
                }
                if (!edit.contains(".php", true)) {
                    edit = edit.replace("-", "_")
                }
                edit = edit.replace(" ", "_").lowercase()
                if (edit[0].isDigit()) edit = "mm_$edit"
                if (check != 0) {
                    binding.edittext.removeTextChangedListener(this)
                    binding.edittext.setText(edit)
                    binding.edittext.setSelection(editPosition)
                    binding.edittext.addTextChangedListener(this)
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onPause() {
        super.onPause()
        saveAsFileJob?.cancel()
        resetTollbarJob?.cancel()
    }

    override fun setDir(dir: String, oldName: String, newName: String) {
        val intent = Intent()
        intent.putExtra("dir", dir)
        intent.putExtra("oldFileName", oldName)
        intent.putExtra("fileName", newName)
        intent.putExtra("setDir", true)
        setResult(Activity.RESULT_OK, intent)
        onBack()
    }

    private fun checkCyrilic(fileName: String): String {
        val sb = StringBuilder()
        for (c in fileName) {
            val unicode = UnicodeBlock.of(c)
            unicode?.let {
                if (!(it == UnicodeBlock.CYRILLIC || it == UnicodeBlock.CYRILLIC_SUPPLEMENTARY || it == UnicodeBlock.CYRILLIC_EXTENDED_A || it == UnicodeBlock.CYRILLIC_EXTENDED_B)) {
                    sb.append(c)
                }
            }
        }
        return sb.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminDialigSaveAsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        oldName = intent.extras?.getString("fileName", "") ?: ""
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
        binding.listView.selector = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.selector_default)
        adapter = TitleListAdaprer(this)
        binding.listView.adapter = adapter
        if (savedInstanceState != null) {
            dir = savedInstanceState.getString("dir") ?: ""
        }
        getDirListRequest(dir)
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
        setTollbarTheme()
        setResult(Activity.RESULT_CANCELED)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.subtitleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.save_as_up)
        binding.subtitleToolbar.text = filenameTitle
        if (filenameTitle == "") binding.subtitleToolbar.visibility = View.GONE
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.subtitleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
                TransitionManager.beginDelayedTransition(binding.toolbar)
            }
        }
        TransitionManager.beginDelayedTransition(binding.toolbar)
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
        binding.subtitleToolbar.isSingleLine = true
    }

    private fun setFileName() {
        var editText = binding.edittext.text.toString()
        if (editText == "") {
            val gc = Calendar.getInstance()
            val mun = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
            editText = gc[Calendar.DATE].toString() + "_" + mun[gc[Calendar.MONTH]] + "_" + gc[Calendar.YEAR] + "_" + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
        }
        val intent = Intent()
        intent.putExtra("dir", dir)
        intent.putExtra("oldFileName", oldName)
        intent.putExtra("fileName", editText)
        intent.putExtra("setDir", false)
        setResult(Activity.RESULT_OK, intent)
        onBack()
    }

    private fun getDirListRequest(dir: String) {
        if (MainActivity.isNetworkAvailable()) {
            saveAsFileJob = CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
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
                    MainActivity.toastView(this@PiasochnicaSaveAsFileExplorer, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                binding.progressBar2.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        } else {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.no_internet))
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            setFileName()
            return true
        }
        if (id == R.id.action_mk_dir) {
            val dialogPasochnicaMkDir = DialogPasochnicaMkDir.getInstance(dir, oldName, binding.edittext.text.toString())
            dialogPasochnicaMkDir.show(supportFragmentManager, "dialogPasochnicaMkDir")
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_piasochnica_save_as_explorer, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("dir", dir)
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
}