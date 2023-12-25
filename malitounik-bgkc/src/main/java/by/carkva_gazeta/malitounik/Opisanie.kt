package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.OpisanieBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemOpisanieBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar


class Opisanie : BaseActivity(), DialogFontSize.DialogFontSizeListener, DialogOpisanieWIFI.DialogOpisanieWIFIListener, DialogHelpShare.DialogHelpShareListener {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mun = 1
    private var day = 1
    private var year = 2022
    private var svity = false
    private lateinit var binding: OpisanieBinding
    private lateinit var chin: SharedPreferences
    private var resetTollbarJob: Job? = null
    private var loadIconsJob: Job? = null
    private val dirList = ArrayList<DirList>()
    private val arrayList = ArrayList<OpisanieData>()
    private lateinit var adapter: OpisanieAdapter
    private var fullImagePathVisable = ""

    private fun viewSviaryiaIIcon() {
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        if (svity) {
            loadOpisanieSviat()
        } else {
            if (fileOpisanie.exists()) loadOpisanieSviatyia(fileOpisanie.readText())
        }
        loadIconsOnImageView()
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        loadIconsJob?.cancel()
    }

    /*private fun resizeImage(bitmap: Bitmap?): Bitmap? {
        bitmap?.let {
            var newHeight = it.height.toFloat()
            var newWidth = it.width.toFloat()
            val widthLinear = binding.swipeRefreshLayout.width.toFloat()
            val resoluton = newWidth / newHeight
            newWidth = 500f * resoluton
            newHeight = 500f
            if (newWidth > widthLinear) {
                newWidth = widthLinear
                newHeight = newWidth / resoluton
            }
            return Bitmap.createScaledBitmap(it, newWidth.toInt(), newHeight.toInt(), false)
        }
        return null
    }*/

    private fun loadOpisanieSviatyia(builder: String) {
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
        var res = ""
        val arrayList = ArrayList<String>()
        if (builder.isNotEmpty()) {
            arrayList.addAll(gson.fromJson(builder, type))
            res = arrayList[day - 1]
        }
        if (dzenNoch) res = res.replace("#d00505", "#f44336")
        val title = ArrayList<String>()
        val listRes = res.split("<strong>")
        var sb = ""
        for (i in listRes.size - 1 downTo 0) {
            val text = listRes[i].replace("<!--image-->", "")
            if (text.trim() != "") {
                if (text.contains("Трапар") || text.contains("Кандак")) {
                    sb = "<strong>$text$sb"
                    continue
                } else {
                    sb = "<strong>$text$sb"
                    title.add(0, sb)
                    sb = ""
                }
            }
        }
        title.forEachIndexed { index, text ->
            val t1 = text.indexOf("</strong>")
            var textTitle = ""
            var fulText = ""
            if (t1 != -1) {
                textTitle = text.substring(0, t1 + 9)
                fulText = text.substring(t1 + 9)
            }
            val spannedtitle = MainActivity.fromHtml(textTitle)
            val spanned = MainActivity.fromHtml(fulText)
            var check = false
            this.arrayList.forEach {
                if (it.index == index) check = true
            }
            if (!check) this.arrayList.add(OpisanieData(index, spannedtitle, spanned, "", ""))
        }
        adapter.notifyDataSetChanged()
    }

    private fun loadOpisanieSviat() {
        val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
        if (fileOpisanieSviat.exists()) {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
            arrayList?.forEach { strings ->
                if (day == strings[0].toInt() && mun == strings[1].toInt()) {
                    var res = strings[2]
                    if (dzenNoch) res = res.replace("#d00505", "#f44336")
                    val t1 = res.indexOf("</strong>")
                    var textTitle = ""
                    var fulText = ""
                    if (t1 != -1) {
                        textTitle = res.substring(0, t1 + 9)
                        fulText = res.substring(t1 + 9)
                    }
                    val spannedtitle = MainActivity.fromHtml(textTitle)
                    val spanned = MainActivity.fromHtml(fulText)
                    var check = false
                    this.arrayList.forEach {
                        if (it.index == 0) check = true
                    }
                    if (!check) this.arrayList.add(OpisanieData(0, spannedtitle, spanned, "", ""))
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = OpisanieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        year = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        if (savedInstanceState?.getBoolean("imageViewFullVisable") == true) {
            fullImagePathVisable = savedInstanceState.getString("filePach") ?: ""
            val file2 = File(fullImagePathVisable)
            Picasso.get().load(file2).into(binding.imageViewFull)
            binding.imageViewFull.visibility = View.VISIBLE
            binding.progressBar2.visibility = View.INVISIBLE
            binding.swipeRefreshLayout.visibility = View.GONE
            binding.titleToolbar.text = savedInstanceState.getString("tollbarText")
        } else {
            binding.titleToolbar.text = resources.getText(R.string.zmiest)
        }
        adapter = OpisanieAdapter(this)
        binding.listview.adapter = adapter
        binding.listview.divider = null
        binding.swipeRefreshLayout.setOnRefreshListener {
            arrayList.clear()
            viewSviaryiaIIcon()
            startLoadIconsJob(!MainActivity.isNetworkAvailable(true))
            binding.swipeRefreshLayout.isRefreshing = false
        }
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary_black)
            binding.imageViewFull.background = ContextCompat.getDrawable(this, R.color.colorbackground_material_dark)
        } else {
            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        }
        viewSviaryiaIIcon()
        if (savedInstanceState == null) startLoadIconsJob(!MainActivity.isNetworkAvailable(true))
        setTollbarTheme()
    }

    override fun onDialogPositiveOpisanieWIFI() {
        startLoadIconsJob(true)
    }

    override fun onDialogNegativeOpisanieWIFI() {
        binding.progressBar2.visibility = View.INVISIBLE
    }

    private fun startLoadIconsJob(loadIcons: Boolean) {
        loadIconsJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.isIndeterminate = true
            binding.progressBar2.visibility = View.VISIBLE
            if (!MainActivity.isNetworkAvailable()) {
                val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
                if (!svity && fileOpisanie.exists()) {
                    loadOpisanieSviatyia(fileOpisanie.readText())
                } else {
                    val dialoNoIntent = DialogNoInternet()
                    dialoNoIntent.show(supportFragmentManager, "dialoNoIntent")
                }
            } else {
                try {
                    if (svity) {
                        getOpisanieSviat()
                    } else {
                        getSviatyia()
                    }
                    getIcons(loadIcons)
                    getPiarliny()
                } catch (_: Throwable) {
                }
            }
        }
    }

    private suspend fun getOpisanieSviat(count: Int = 0) {
        val pathReference = Malitounik.referens.child("/opisanie_sviat.json")
        var update = 0L
        var error = false
        pathReference.metadata.addOnCompleteListener { storageMetadata ->
            if (storageMetadata.isSuccessful) {
                update = storageMetadata.result.updatedTimeMillis
            } else {
                error = true
            }
        }.await()
        if (update == 0L) error = true
        if (error && count < 2) {
            getOpisanieSviat(count + 1)
            return
        }
        saveOpisanieSviat(update)
    }

    private suspend fun saveOpisanieSviat(update: Long, count: Int = 0) {
        val pathReference = Malitounik.referens.child("/opisanie_sviat.json")
        val file = File("$filesDir/opisanie_sviat.json")
        var error = false
        val time = file.lastModified()
        if (!file.exists() || time < update) {
            pathReference.getFile(file).addOnCompleteListener {
                if (!it.isSuccessful) {
                    error = true
                }
            }.await()
        }
        var read = ""
        if (file.exists()) read = file.readText()
        if (read == "") error = true
        if (error && count < 2) {
            saveOpisanieSviat(update, count + 1)
            return
        }
        loadOpisanieSviat()
    }

    private suspend fun getSviatyia(count: Int = 0) {
        val dir = File("$filesDir/sviatyja/")
        if (!dir.exists()) dir.mkdir()
        var error = false
        val pathReference = Malitounik.referens.child("/chytanne/sviatyja/opisanie$mun.json")
        var update = 0L
        pathReference.metadata.addOnCompleteListener { storageMetadata ->
            if (storageMetadata.isSuccessful) {
                update = storageMetadata.result.updatedTimeMillis
            } else {
                error = true
            }
        }.await()
        if (update == 0L) error = true
        if (error && count < 2) {
            getSviatyia(count + 1)
            return
        }
        saveOpisanieSviatyia(update)
    }

    private suspend fun saveOpisanieSviatyia(update: Long, count: Int = 0) {
        val pathReference = Malitounik.referens.child("/chytanne/sviatyja/opisanie$mun.json")
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        val time = fileOpisanie.lastModified()
        var error = false
        if (!fileOpisanie.exists() || time < update) {
            pathReference.getFile(fileOpisanie).addOnCompleteListener {
                if (!it.isSuccessful) {
                    error = true
                }
            }.await()
        }
        var read = ""
        if (fileOpisanie.exists()) read = fileOpisanie.readText()
        if (read == "") error = true
        if (error && count < 2) {
            saveOpisanieSviatyia(update, count + 1)
            return
        }
        loadOpisanieSviatyia(read)
    }

    private suspend fun getIcons(loadIcons: Boolean, count: Int = 0) {
        val dir = File("$filesDir/icons/")
        if (!dir.exists()) dir.mkdir()
        val dir2 = File("$filesDir/iconsApisanne")
        if (!dir2.exists()) dir2.mkdir()
        if (count < 2) {
            getIcons(loadIcons, count + 1)
            return
        }
        dirList.clear()
        var size = 0L
        val sb = StringBuilder()
        val list = Malitounik.referens.child("/chytanne/icons").list(1000).await()
        list.items.forEach {
            val pref = if (svity) "v"
            else "s"
            sb.append(it.name)
            if (it.name.contains("${pref}_${day}_${mun}")) {
                val t1 = it.name.lastIndexOf(".")
                val fileNameT = it.name.substring(0, t1) + ".txt"
                val file = File("$filesDir/iconsApisanne/$fileNameT")
                try {
                    Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").getFile(file).addOnFailureListener {
                        if (file.exists()) file.delete()
                    }.await()
                } catch (_: Throwable) {
                }
                val fileIcon = File("$filesDir/icons/${it.name}")
                val meta = it.metadata.await()
                val time = fileIcon.lastModified()
                val update = meta.updatedTimeMillis
                if (!fileIcon.exists() || time < update) {
                    val updateFile = meta.sizeBytes
                    dirList.add(DirList(it.name, updateFile))
                    size += updateFile
                }
            }
        }
        val fileList = File("$filesDir/icons").list()
        fileList?.forEach {
            if (!sb.toString().contains(it)) {
                val file = File("$filesDir/icons/$it")
                if (file.exists()) file.delete()
            }

        }
        if (!loadIcons && MainActivity.isNetworkAvailable(true)) {
            if (dirList.isNotEmpty()) {
                val dialog = DialogOpisanieWIFI.getInstance(size.toFloat())
                dialog.show(supportFragmentManager, "dialogOpisanieWIFI")
            } else {
                binding.progressBar2.visibility = View.INVISIBLE
            }
        } else {
            binding.progressBar2.isIndeterminate = false
            binding.progressBar2.progress = 0
            binding.progressBar2.max = size.toInt()
            var progress = 0
            for (i in 0 until dirList.size) {
                try {
                    val fileIcon = File("$filesDir/icons/" + dirList[i].name)
                    val pathReference = Malitounik.referens.child("/chytanne/icons/" + dirList[i].name)
                    pathReference.getFile(fileIcon).await()
                    progress += dirList[i].sizeBytes.toInt()
                    binding.progressBar2.progress = progress
                } catch (_: Throwable) {
                }
            }
            loadIconsOnImageView()
        }
    }

    private fun loadIconsOnImageView() {
        var e = 0
        val pref = if (svity) "v"
        else "s"
        val fileList = File("$filesDir/icons").list()
        fileList?.forEach {
            if (it.contains("${pref}_${day}_${mun}_")) {
                if (!svity) {
                    val s1 = "${pref}_${day}_${mun}".length
                    val s3 = it.substring(s1 + 1, s1 + 2)
                    if (s3.isDigitsOnly()) e = s3.toInt() - 1
                }
                val t1 = it.lastIndexOf(".")
                val fileNameT = it.substring(0, t1) + ".txt"
                val file = File("$filesDir/iconsApisanne/$fileNameT")
                if (file.exists()) {
                    arrayList[e].textApisanne = file.readText()
                } else {
                    arrayList[e].textApisanne = ""
                }
                val file2 = File("$filesDir/icons/$it")
                if (file2.exists()) arrayList[e].image = file2.absolutePath
            }
        }
        binding.progressBar2.visibility = View.INVISIBLE
        adapter.notifyDataSetChanged()
    }

    private suspend fun getPiarliny() {
        val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
        val localFile = File("$filesDir/piarliny.json")
        pathReference.getFile(localFile).await()
        invalidateOptionsMenu()
    }

    private fun checkParliny(): Boolean {
        val piarliny = ArrayList<ArrayList<String>>()
        val fileOpisanieSviat = File("$filesDir/piarliny.json")
        if (fileOpisanieSviat.exists()) {
            try {
                val builder = fileOpisanieSviat.readText()
                val gson = Gson()
                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                piarliny.addAll(gson.fromJson(builder, type))
            } catch (t: Throwable) {
                fileOpisanieSviat.delete()
            }
            val cal = GregorianCalendar()
            piarliny.forEach {
                cal.timeInMillis = it[0].toLong() * 1000
                if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onDialogFontSize(fontSize: Float) {
        adapter.notifyDataSetChanged()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            val layoutParams = binding.collapsingToolbarLayout.layoutParams
            if (binding.titleToolbar.isSelected) {
                resetTollbarJob?.cancel()
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
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

    override fun onBack() {
        if (binding.imageViewFull.visibility == View.VISIBLE) {
            binding.imageViewFull.visibility = View.GONE
            binding.swipeRefreshLayout.visibility = View.VISIBLE
            viewSviaryiaIIcon()
            binding.titleToolbar.text = resources.getText(R.string.zmiest)
        } else {
            super.onBack()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.opisanie, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onPrepareMenu(menu: Menu) {
        if (checkParliny()) menu.findItem(R.id.action_piarliny).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        else menu.findItem(R.id.action_piarliny).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.findItem(R.id.action_carkva).isVisible = chin.getBoolean("admin", false)
        menu.findItem(R.id.action_dzen_noch).isChecked = dzenNoch
        val spanString = if (chin.getBoolean("auto_dzen_noch", false)) {
            menu.findItem(R.id.action_dzen_noch).isCheckable = false
            SpannableString(getString(R.string.auto_widget_day_d_n))
        } else {
            menu.findItem(R.id.action_dzen_noch).isCheckable = true
            SpannableString(getString(R.string.widget_day_d_n))
        }
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menu.findItem(R.id.action_dzen_noch).title = spanString
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("filePach", fullImagePathVisable)
        outState.putString("tollbarText", binding.titleToolbar.text.toString())
        outState.putBoolean("imageViewFullVisable", binding.imageViewFull.visibility == View.VISIBLE)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_piarliny) {
            val i = Intent(this, PiarlinyAll::class.java)
            if (checkParliny()) {
                i.putExtra("mun", mun)
                i.putExtra("day", day)
            }
            startActivity(i)
            return true
        }
        if (id == R.id.action_gallery) {
            val i = Intent(this, Gallery::class.java)
            startActivity(i)
            return true
        }
        if (id == R.id.action_carkva) {
            if (checkmodulesAdmin()) {
                val intent = Intent()
                if (svity) {
                    intent.setClassName(this, MainActivity.ADMINSVIATY)
                    intent.putExtra("day", day)
                    intent.putExtra("mun", mun)
                } else {
                    intent.setClassName(this, MainActivity.ADMINSVIATYIA)
                    val cal = Calendar.getInstance() as GregorianCalendar
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, mun - 1)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    var dayofyear = cal[Calendar.DAY_OF_YEAR]
                    if (!cal.isLeapYear(cal[Calendar.YEAR]) && dayofyear >= 59) {
                        dayofyear++
                    }
                    intent.putExtra("dayOfYear", dayofyear)
                }
                startActivity(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val prefEditor = chin.edit()
            if (item.isCheckable) {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    prefEditor.putBoolean("dzen_noch", true)
                } else {
                    prefEditor.putBoolean("dzen_noch", false)
                }
                prefEditor.apply()
                recreate()
            } else {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            return true
        }
        if (id == R.id.action_font) {
            val dialogFontSize = DialogFontSize()
            dialogFontSize.show(supportFragmentManager, "font")
            return true
        }
        return false
    }

    override fun sentShareText(shareText: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.zmiest))
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, getText(R.string.zmiest)))
    }

    private inner class OpisanieAdapter(private val context: Activity) : ArrayAdapter<OpisanieData>(context, R.layout.simple_list_item_opisanie, arrayList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemOpisanieBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.title, binding.text, binding.image, binding.buttonPopup, binding.textApisanne)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            val file2 = File(arrayList[position].image)
            viewHolder.buttonPopup.visibility = View.VISIBLE
            viewHolder.buttonPopup.let {
                viewHolder.buttonPopup.setOnClickListener {
                    val sb = StringBuilder()
                    sb.append(viewHolder.textTitle.text)
                    sb.append(viewHolder.text.text)
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(getString(R.string.copy_text), sb.toString())
                    clipboard.setPrimaryClip(clip)
                    MainActivity.toastView(this@Opisanie, getString(R.string.copy_text), Toast.LENGTH_LONG)
                    if (file2.exists()) {
                        val sendIntent = Intent(Intent.ACTION_SEND)
                        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@Opisanie, "by.carkva_gazeta.malitounik.fileprovider", file2))
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString())
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.zmiest))
                        sendIntent.type = "image/*"
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.zmiest)))
                    } else {
                        if (chin.getBoolean("dialogHelpShare", true)) {
                            val dialog = DialogHelpShare.getInstance(sb.toString())
                            dialog.show(supportFragmentManager, "DialogHelpShare")
                        } else {
                            sentShareText(sb.toString())
                        }
                    }
                }
            }
            if (file2.exists()) {
                Picasso.get().load(file2).resize(500, 500).onlyScaleDown().centerInside().into(viewHolder.imageView)
                viewHolder.imageView.visibility = View.VISIBLE
                viewHolder.imageView.setOnClickListener {
                    if (file2.exists()) {
                        Picasso.get().load(file2).into(binding.imageViewFull)
                        binding.imageViewFull.visibility = View.VISIBLE
                        fullImagePathVisable = file2.absolutePath
                        binding.progressBar2.visibility = View.INVISIBLE
                        binding.swipeRefreshLayout.visibility = View.GONE
                        binding.titleToolbar.text = arrayList[position].title.trim()
                    }
                }
                viewHolder.textApisanne.text = arrayList[position].textApisanne
                if (arrayList[position].textApisanne != "") viewHolder.textApisanne.visibility = View.VISIBLE
                else viewHolder.textApisanne.visibility = View.GONE
            } else {
                viewHolder.imageView.setImageBitmap(null)
                viewHolder.imageView.visibility = View.GONE
                viewHolder.imageView.setOnClickListener(null)
                viewHolder.textApisanne.visibility = View.GONE
            }
            if (dzenNoch) {
                viewHolder.text.setTextColor(ContextCompat.getColor(this@Opisanie, R.color.colorWhite))
            }
            val fontBiblia = chin.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            viewHolder.textTitle.textSize = fontBiblia
            viewHolder.textApisanne.textSize = fontBiblia
            viewHolder.textTitle.text = arrayList[position].title.trim()
            val text = arrayList[position].text.trim()
            if (text.isNotEmpty()) {
                viewHolder.text.textSize = fontBiblia
                viewHolder.text.text = text
            } else {
                viewHolder.text.visibility = View.GONE
            }
            return rootView
        }
    }

    private class ViewHolder(var textTitle: TextView, var text: TextView, var imageView: ImageView, var buttonPopup: ImageView, var textApisanne: TextView)

    private data class DirList(val name: String?, val sizeBytes: Long)

    private data class OpisanieData(val index: Int, val title: Spanned, val text: Spanned, var image: String, var textApisanne: String)
}