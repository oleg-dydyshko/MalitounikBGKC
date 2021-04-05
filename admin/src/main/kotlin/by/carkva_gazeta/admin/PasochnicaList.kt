package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import by.carkva_gazeta.admin.databinding.AdminPasochnicaListBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class PasochnicaList : AppCompatActivity(), DialogPasochnicaFileName.DialogPasochnicaFileNameListener, DialogContextMenu.DialogContextMenuListener, DialogDelite.DialogDeliteListener {

    private lateinit var k: SharedPreferences
    private lateinit var binding: AdminPasochnicaListBinding
    private var resetTollbarJob: Job? = null
    private var fileList = ArrayList<String>()
    private lateinit var adapter: PasochnicaListAdaprer

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        super.onCreate(savedInstanceState)
        binding = AdminPasochnicaListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTollbarTheme()

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, Pasochnica::class.java)
            intent.putExtra("fileName", fileList[position])
            startActivity(intent)
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            val contextMenu = DialogContextMenu.getInstance(position, fileList[position])
            contextMenu.show(supportFragmentManager, "contextMenu")
            return@setOnItemLongClickListener true
        }
        getDirPostRequest()
    }

    override fun onDialogRenameClick(position: Int) {
        val dialogPasochnicaFileName = DialogPasochnicaFileName.getInstance(fileList[position])
        dialogPasochnicaFileName.show(supportFragmentManager, "dialogPasochnicaFileName")
    }

    override fun onDialogDeliteClick(position: Int, title: String) {
        val dialogDelite = DialogDelite.getInstance(position, title)
        dialogDelite.show(supportFragmentManager, "dialogDelite")
    }

    override fun fileDelite(position: Int) {
        getFileUnlinkPostRequest(fileList[position])
    }

    override fun setFileName(oldFileName: String, fileName: String) {
        getFileRenamePostRequest(oldFileName, fileName)
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = PasochnicaListAdaprer(this)
        binding.listView.adapter = adapter
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.pasochnica)
    }

    private fun fullTextTollbar() {
        val layoutParams = binding.toolbar.layoutParams
        resetTollbarJob?.cancel()
        if (binding.titleToolbar.isSelected) {
            resetTollbar(layoutParams)
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.titleToolbar.isSingleLine = false
            binding.titleToolbar.isSelected = true
            resetTollbarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                resetTollbar(layoutParams)
            }
        }
    }

    private fun resetTollbar(layoutParams: ViewGroup.LayoutParams) {
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            layoutParams.height = actionBarHeight
        }
        binding.titleToolbar.isSelected = false
        binding.titleToolbar.isSingleLine = true
    }

    override fun onResume() {
        super.onResume()
        setTollbarTheme()
        overridePendingTransition(by.carkva_gazeta.malitounik.R.anim.alphain, by.carkva_gazeta.malitounik.R.anim.alphaout)
        if (k.getBoolean("scrinOn", false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun getFileUnlinkPostRequest(fileName: String) {
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("unlink", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName, "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        inputStream
                    }
                }
                binding.progressBar2.visibility = View.GONE
                getDirPostRequest()
            }
        }
    }

    private fun getFileRenamePostRequest(oldFileName: String, fileName: String) {
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    var reqParam = URLEncoder.encode("rename", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    reqParam += "&" + URLEncoder.encode("oldFileName", "UTF-8") + "=" + URLEncoder.encode(oldFileName, "UTF-8")
                    reqParam += "&" + URLEncoder.encode("fileName", "UTF-8") + "=" + URLEncoder.encode(fileName, "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        inputStream
                    }
                }
                binding.progressBar2.visibility = View.GONE
                getDirPostRequest()
            }
        }
    }

    private fun getDirPostRequest() {
        if (MainActivity.isNetworkAvailable(this)) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    val reqParam = URLEncoder.encode("file", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                    val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                    with(mURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        val wr = OutputStreamWriter(outputStream)
                        wr.write(reqParam)
                        wr.flush()
                        val sb = StringBuilder()
                        BufferedReader(InputStreamReader(inputStream)).use {
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                sb.append(inputLine)
                                inputLine = it.readLine()
                            }
                        }
                        val result = sb.toString()
                        fileList.clear()
                        if (result != "null") {
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<String>>() {}.type
                            fileList.addAll(gson.fromJson(result, type))
                            fileList.sort()
                        }
                    }
                }
                if (intent.extras != null) {
                    val title = intent.extras?.getString("title", "") ?: ""
                    var exits = false
                    for (i in 0 until fileList.size) {
                        if (fileList[i].contains(title)) {
                            exits = true
                            break
                        }
                    }
                    val intent = Intent(this@PasochnicaList, Pasochnica::class.java)
                    intent.putExtra("text", this@PasochnicaList.intent.extras?.getString("text", "") ?: "")
                    intent.putExtra("resours", this@PasochnicaList.intent.extras?.getString("resours", "") ?: "")
                    intent.putExtra("exits", exits)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_plus) {
            val intent = Intent(this, Pasochnica::class.java)
            intent.putExtra("fileName", "")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val infl = menuInflater
        infl.inflate(R.menu.edit_piasochnica_list, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val end = spanString.length
            spanString.setSpan(AbsoluteSizeSpan(SettingsActivity.GET_FONT_SIZE_MIN.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            item.title = spanString
        }
        return true
    }

    private inner class PasochnicaListAdaprer(context: Activity) : ArrayAdapter<String>(context, by.carkva_gazeta.malitounik.R.layout.simple_list_item_2, by.carkva_gazeta.malitounik.R.id.label, fileList) {

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
            viewHolder.text.text = fileList[position]
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            return rootView
        }
    }

    private class ViewHolder(var text: TextViewRobotoCondensed)
}