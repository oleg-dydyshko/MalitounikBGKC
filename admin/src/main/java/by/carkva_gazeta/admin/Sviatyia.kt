package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminSviatyiaBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.CaliandarMun
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.MenuCaliandar
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemTipiconBinding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar

class Sviatyia : BaseActivity(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var setedit = false
    private lateinit var binding: AdminSviatyiaBinding
    private var resetTollbarJob: Job? = null
    private var dayOfYear = 1
    private var urlJob: Job? = null
    private val sviatyiaNew1 = ArrayList<ArrayList<String>>()
    private val c = Calendar.getInstance()
    private val array: Array<String>
        get() = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.admin_svity)
    private val arrayList = ArrayList<Tipicon>()
    private var mLastClickTime: Long = 0
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val position = intent.getIntExtra("position", 0)
                val arrayList = MenuCaliandar.getPositionCaliandar(position)
                val cal = GregorianCalendar(VYSOCOSNYI_GOD, arrayList[2].toInt(), arrayList[1].toInt())
                dayOfYear = cal[Calendar.DAY_OF_YEAR]
                setDate(dayOfYear)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
        binding.apisanne.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.linearLayout2.visibility = View.VISIBLE
            } else {
                binding.linearLayout2.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        urlJob?.cancel()
        resetTollbarJob?.cancel()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.appBarLayout2.windowToken, 0)
        binding.apisanne.onFocusChangeListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (savedInstanceState != null) {
            setedit = savedInstanceState.getBoolean("setedit")
        }
        binding = AdminSviatyiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dayOfYear = intent.extras?.getInt("dayOfYear") ?: (c[Calendar.DAY_OF_YEAR])

        arrayList.add(Tipicon(0, "Няма"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest, "З вялікай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_kruge, "Двунадзясятыя і вялікія сьвяты"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_krest_v_polukruge, "З ліцьцёй на вячэрні"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk, "З штодзённай вячэрняй і вялікім услаўленьнем на ютрані"))
        arrayList.add(Tipicon(by.carkva_gazeta.malitounik.R.drawable.znaki_ttk_black, "З штодзённай вячэрняй і малым услаўленьнем на ютрані"))
        binding.actionBold.setOnClickListener(this)
        binding.actionEm.setOnClickListener(this)
        binding.actionRed.setOnClickListener(this)
        binding.actionP.setOnClickListener(this)
        binding.imageViewLeft.setOnClickListener(this)
        binding.imageViewRight.setOnClickListener(this)
        setDate(dayOfYear)
        setTollbarTheme()
    }

    private fun setDate(dayOfYear: Int, count: Int = 0) {
        if (MainActivity.isNetworkAvailable()) {
            binding.progressBar2.visibility = View.VISIBLE
            c.set(Calendar.YEAR, VYSOCOSNYI_GOD)
            c.set(Calendar.DAY_OF_YEAR, dayOfYear)
            val munName = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
            binding.date.text = getString(by.carkva_gazeta.malitounik.R.string.admin_date, c[Calendar.DAY_OF_MONTH], munName[c[Calendar.MONTH]])
            urlJob?.cancel()
            urlJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    var builder = ""
                    val localFile = File("$filesDir/cache/cache.txt")
                    referens.child("/chytanne/sviatyja/opisanie" + (c[Calendar.MONTH] + 1) + ".json").getFile(localFile).addOnCompleteListener {
                        if (it.isSuccessful) builder = localFile.readText()
                        else MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    val gson = Gson()
                    builder = if (builder != "") {
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
                        val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                        arrayList[c[Calendar.DAY_OF_MONTH] - 1]
                    } else {
                        getString(by.carkva_gazeta.malitounik.R.string.error)
                    }
                    binding.apisanne.setText(builder)
                    val localFile2 = File("$filesDir/cache/cache2.txt")
                    var builder2 = ""
                    referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnCompleteListener {
                        if (it.isSuccessful) builder2 = localFile2.readText()
                        else MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    if (builder2 != "") {
                        val line = builder2.split("\n")
                        for (element in line) {
                            val reg = element.split("<>")
                            val list = ArrayList<String>()
                            for (element2 in reg) {
                                list.add(element2)
                            }
                            sviatyiaNew1.add(list)
                        }
                        binding.sviaty.setSelection(0)
                        binding.chytanne.setSelection(0)
                        binding.spinnerStyle.adapter = SpinnerAdapter(this@Sviatyia, array)
                        binding.spinnerZnak.adapter = SpinnerAdapterTipicon(this@Sviatyia, arrayList)
                        binding.sviaty.setText(sviatyiaNew1[c[Calendar.DAY_OF_YEAR] - 1][0])
                        binding.chytanne.setText(sviatyiaNew1[c[Calendar.DAY_OF_YEAR] - 1][1])
                        var position = 0
                        when (sviatyiaNew1[c[Calendar.DAY_OF_YEAR] - 1][2].toInt()) {
                            6 -> position = 0
                            7 -> position = 1
                            8 -> position = 2
                        }
                        binding.spinnerStyle.setSelection(position)
                        val znaki = sviatyiaNew1[c[Calendar.DAY_OF_YEAR] - 1][3]
                        val position2 = if (znaki == "") 0
                        else znaki.toInt()
                        binding.spinnerZnak.setSelection(position2)
                    } else {
                        binding.sviaty.setText(getString(by.carkva_gazeta.malitounik.R.string.error))
                        MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                } catch (e: Throwable) {
                    MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                }
                if ((binding.apisanne.text.toString() == getString(by.carkva_gazeta.malitounik.R.string.error) || binding.sviaty.text.toString() == getString(by.carkva_gazeta.malitounik.R.string.error)) && count < 3) {
                    setDate(dayOfYear, count + 1)
                } else {
                    binding.progressBar2.visibility = View.GONE
                }
            }
        }
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.sviatyia)
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
    }

    override fun onBack() {
        if (binding.scrollpreView.visibility == View.VISIBLE) {
            binding.scrollpreView.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
        } else super.onBack()
    }

    override fun onPrepareMenu(menu: Menu) {
        val editItem = menu.findItem(R.id.action_preview)
        if (binding.scrollpreView.visibility == View.GONE) {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka_edit)
        } else {
            editItem.icon = ContextCompat.getDrawable(this, by.carkva_gazeta.malitounik.R.drawable.natatka)
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_bible) {
            val dialogSvityiaBible = DialogSvityiaBible()
            dialogSvityiaBible.show(supportFragmentManager, "dialogSvityiaBible")
            return true
        }
        if (id == R.id.action_glava) {
            val i = Intent(this, CaliandarMun::class.java)
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("year", cal[Calendar.YEAR])
            i.putExtra("mun", cal[Calendar.MONTH])
            i.putExtra("getData", true)
            caliandarMunLauncher.launch(i)
            return true
        }
        if (id == R.id.action_upload_image) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, VYSOCOSNYI_GOD)
            cal.set(Calendar.DAY_OF_YEAR, dayOfYear)
            val i = Intent(this, SviatyiaImage::class.java)
            i.putExtra("day", cal[Calendar.DATE])
            i.putExtra("mun", cal[Calendar.MONTH] + 1)
            startActivity(i)
            return true
        }
        if (id == R.id.action_save) {
            sendPostRequest(binding.sviaty.text.toString(), binding.chytanne.text.toString(), binding.spinnerStyle.selectedItemPosition, binding.spinnerZnak.selectedItemPosition.toString(), binding.apisanne.text.toString())
            return true
        }
        if (id == R.id.action_preview) {
            if (binding.scrollpreView.visibility == View.VISIBLE) {
                binding.scrollpreView.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                invalidateOptionsMenu()
            } else {
                var textApisanne = binding.apisanne.text.toString()
                if (textApisanne.contains("<!--image-->")) {
                    textApisanne = textApisanne.replace("<!--image-->", "<p>")
                }
                binding.preView.text = MainActivity.fromHtml(textApisanne).trim()
                binding.scrollpreView.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.apisanne.windowToken, 0)
                invalidateOptionsMenu()
            }
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_sviatyia, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        if (id == R.id.imageViewLeft) {
            dayOfYear--
            setDate(dayOfYear)
        }
        if (id == R.id.imageViewRight) {
            dayOfYear++
            setDate(dayOfYear)
        }
        if (id == R.id.action_bold) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<strong>")
                append(text.substring(startSelect, endSelect))
                append("</strong>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 17)
        }
        if (id == R.id.action_em) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<em>")
                append(text.substring(startSelect, endSelect))
                append("</em>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 9)
        }
        if (id == R.id.action_red) {
            val startSelect = binding.apisanne.selectionStart
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, startSelect))
                append("<font color=\"#d00505\">")
                append(text.substring(startSelect, endSelect))
                append("</font>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 29)
        }
        if (id == R.id.action_p) {
            val endSelect = binding.apisanne.selectionEnd
            val text = binding.apisanne.text.toString()
            val build = with(StringBuilder()) {
                append(text.substring(0, endSelect))
                append("<p>")
                append(text.substring(endSelect))
                toString()
            }
            binding.apisanne.setText(build)
            binding.apisanne.setSelection(endSelect + 3)
        }
    }

    private suspend fun saveLogFile(count: Int = 0) {
        val logFile = File("$filesDir/cache/log.txt")
        var error = false
        logFile.writer().use {
            it.write(getString(by.carkva_gazeta.malitounik.R.string.check_update_resourse))
        }
        Malitounik.referens.child("/admin/log.txt").putFile(Uri.fromFile(logFile)).addOnFailureListener {
            MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
            error = true
        }.await()
        if (error && count < 3) {
            saveLogFile(count + 1)
        }
    }

    private fun sendPostRequest(name: String, chtenie: String, bold: Int, tipicon: String, spaw: String) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                val data = c[Calendar.DAY_OF_MONTH]
                val mun = c[Calendar.MONTH]
                if (!(name == getString(by.carkva_gazeta.malitounik.R.string.error) || name == "")) {
                    var style = 8
                    when (bold) {
                        0 -> style = 6
                        1 -> style = 7
                        2 -> style = 8
                    }
                    binding.progressBar2.visibility = View.VISIBLE
                    val localFile2 = File("$filesDir/cache/cache2.txt")
                    val sviatyiaNewList = ArrayList<ArrayList<String>>()
                    referens.child("/calendarsviatyia.txt").getFile(localFile2).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val sviatyiaNew = localFile2.readLines()
                            for (element in sviatyiaNew) {
                                val re1 = element.split("<>")
                                val list = ArrayList<String>()
                                for (element2 in re1) {
                                    list.add(element2)
                                }
                                sviatyiaNewList.add(list)
                            }
                            sviatyiaNewList[dayOfYear - 1][0] = name
                            sviatyiaNewList[dayOfYear - 1][1] = chtenie
                            sviatyiaNewList[dayOfYear - 1][2] = style.toString()
                            sviatyiaNewList[dayOfYear - 1][3] = tipicon
                        } else {
                            MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                    var sw3 = ""
                    val sb = StringBuilder()
                    for (i in 0 until 366) {
                        if (sviatyiaNewList[i][3] != "0") sw3 = sviatyiaNewList[i][3]
                        sb.append(sviatyiaNewList[i][0] + "<>" + sviatyiaNewList[i][1] + "<>" + sviatyiaNewList[i][2] + "<>" + sw3 + "\n")
                    }
                    val localFile3 = File("$filesDir/cache/cache3.txt")
                    if (sviatyiaNewList.isNotEmpty()) {
                        localFile3.writer().use {
                            it.write(sb.toString())
                        }
                    }
                    sb.clear()
                    if (sviatyiaNewList.isNotEmpty()) {
                        referens.child("/calendarsviatyia.txt").putFile(Uri.fromFile(localFile3)).await()
                    }
                } else {
                    MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                if (!(spaw == getString(by.carkva_gazeta.malitounik.R.string.error) || spaw == "")) {
                    val localFile = File("$filesDir/cache/cache.txt")
                    val localFile4 = File("$filesDir/cache/cache4.txt")
                    var builder = ""
                    referens.child("/chytanne/sviatyja/opisanie" + (mun + 1) + ".json").getFile(localFile).addOnCompleteListener {
                        if (it.isSuccessful) {
                            builder = localFile.readText()
                        } else {
                            MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                        }
                    }.await()
                    val gson = Gson()
                    if (builder != "") {
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
                        val arrayList: ArrayList<String> = gson.fromJson(builder, type)
                        arrayList[data - 1] = spaw.replace(" ", " ")
                        localFile4.writer().use {
                            it.write(gson.toJson(arrayList, type))
                        }
                    }
                    if (builder != "") {
                        referens.child("/chytanne/sviatyja/opisanie" + (mun + 1) + ".json").putFile(Uri.fromFile(localFile4)).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.save))
                            } else {
                                MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                            }
                        }.await()
                    }
                } else {
                    MainActivity.toastView(this@Sviatyia, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
                saveLogFile()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private class SpinnerAdapter(activity: Activity, private val data: Array<String>) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, data) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.text = data[position]
            textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.text.text = data[position]
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)

    private class SpinnerAdapterTipicon(activity: Activity, private val data: ArrayList<Tipicon>) : BaseAdapter() {
        private val context = activity

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolderImage: ViewHolderImage
            if (convertView == null) {
                val binding = SimpleListItemTipiconBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolderImage = ViewHolderImage(binding.image, binding.text1)
                rootView.tag = viewHolderImage
            } else {
                rootView = convertView
                viewHolderImage = rootView.tag as ViewHolderImage
            }
            if (data[position].imageResource == 0) {
                viewHolderImage.image.visibility = View.GONE
            } else {
                viewHolderImage.image.setImageResource(data[position].imageResource)
            }
            viewHolderImage.text.text = data[position].title
            viewHolderImage.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolderImage(var image: ImageView, var text: TextView)

    private data class Tipicon(val imageResource: Int, val title: String)

    companion object {
        private const val VYSOCOSNYI_GOD = 2020
    }
}