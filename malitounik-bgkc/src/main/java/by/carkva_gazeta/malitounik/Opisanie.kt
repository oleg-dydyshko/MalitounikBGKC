package by.carkva_gazeta.malitounik

import android.annotation.SuppressLint
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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.transition.TransitionManager
import by.carkva_gazeta.malitounik.databinding.OpisanieBinding
import by.carkva_gazeta.malitounik.databinding.ProgressMainBinding
import by.carkva_gazeta.malitounik.databinding.SimpleListItemOpisanieBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.Calendar
import java.util.GregorianCalendar


class Opisanie : BaseActivity(), View.OnTouchListener, DialogFontSize.DialogFontSizeListener, DialogOpisanieWIFI.DialogOpisanieWIFIListener, DialogHelpShare.DialogHelpShareListener {
    private val dzenNoch get() = getBaseDzenNoch()
    private var mun = 1
    private var day = 1
    private var year = 2022
    private var svity = false
    private lateinit var binding: OpisanieBinding
    private lateinit var bindingprogress: ProgressMainBinding
    private lateinit var k: SharedPreferences
    private var resetTollbarJob: Job? = null
    private var loadIconsJob: Job? = null
    private val dirList = ArrayList<DirList>()
    private val arrayList = ArrayList<OpisanieData>()
    private lateinit var adapter: OpisanieAdapter
    private var fullImagePathVisable = ""
    private var fullPosition = 0
    private var autoScrollJob: Job? = null
    private var autoStartScrollJob: Job? = null
    private var procentJobAuto: Job? = null
    private var resetScreenJob: Job? = null
    private var spid = 60
    private var mActionDown = false
    private var autoscroll = false
    private var diffScroll = -1
    private val carkvaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 700) {
            viewSviaryiaIIcon()
            startLoadIconsJob(MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI))
        }
    }
    private val mSavePdfFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { data ->
                var bis: BufferedInputStream? = null
                var bos: BufferedOutputStream? = null
                try {
                    val input = FileInputStream(File(fullImagePathVisable))
                    val originalSize = input.available()
                    bis = BufferedInputStream(input)
                    bos = BufferedOutputStream(contentResolver.openOutputStream(data))
                    val buf = ByteArray(originalSize)
                    bis.read(buf)
                    do {
                        bos.write(buf)
                    } while (bis.read(buf) != -1)
                } catch (_: Throwable) {
                } finally {
                    bis?.close()
                    bos?.flush()
                    bos?.close()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll(delayDisplayOff = false, saveAutoScroll = false)
        stopAutoStartScroll()
        resetTollbarJob?.cancel()
        loadIconsJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        autoscroll = k.getBoolean("autoscroll", false)
        spid = k.getInt("autoscrollSpid", 60)
        if (autoscroll) {
            autoStartScroll()
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        val id = v?.id ?: 0
        if (id == R.id.listview) {
            when (event?.action ?: MotionEvent.ACTION_CANCEL) {
                MotionEvent.ACTION_DOWN -> mActionDown = true
                MotionEvent.ACTION_UP -> mActionDown = false
            }
            return false
        }
        return true
    }

    private fun viewSviaryiaIIcon() {
        if (svity) {
            loadOpisanieSviat()
        } else {
            loadOpisanieSviatyia()
        }
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

    private fun loadOpisanieSviatyia() {
        arrayList.clear()
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        if (fileOpisanie.exists()) {
            val builder = fileOpisanie.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
            var res = ""
            val arrayList = ArrayList<String>()
            if (builder.isNotEmpty()) {
                arrayList.addAll(gson.fromJson(builder, type))
                res = arrayList[day - 1]
            }
            if (dzenNoch) res = res.replace("#d00505", "#ff6666")
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
                this.arrayList.add(OpisanieData(index + 1, day, mun, spannedtitle, spanned, "", ""))
            }
        }
        val fileOpisanie13 = File("$filesDir/sviatyja/opisanie13.json")
        if (fileOpisanie13.exists()) {
            val builder = fileOpisanie13.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val arrayList = ArrayList<ArrayList<String>>()
            if (builder.isNotEmpty()) {
                arrayList.addAll(gson.fromJson(builder, type))
                val pasha = GregorianCalendar(year, Calendar.DECEMBER, 25)
                val pastvoW = pasha[Calendar.DAY_OF_WEEK]
                for (i in 26..31) {
                    val pastvo = GregorianCalendar(year, Calendar.DECEMBER, i)
                    val iazepW = pastvo[Calendar.DAY_OF_WEEK]
                    for (e in 0 until arrayList.size) {
                        if (pastvoW != Calendar.SUNDAY) {
                            if (arrayList[e][1].toInt() == 0 && mun - 1 == Calendar.DECEMBER && day == i && Calendar.SUNDAY == iazepW) {
                                val t1 = arrayList[e][2].indexOf("</strong>")
                                var textTitle = ""
                                var fulText = ""
                                if (t1 != -1) {
                                    textTitle = arrayList[e][2].substring(0, t1 + 9)
                                    fulText = arrayList[e][2].substring(t1 + 9)
                                }
                                val spannedtitle = MainActivity.fromHtml(textTitle)
                                val spanned = MainActivity.fromHtml(fulText)
                                this.arrayList.add(OpisanieData(this.arrayList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                            }
                        } else {
                            if (arrayList[e][1].toInt() == 0 && mun - 1 == Calendar.DECEMBER && day == i && Calendar.MONDAY == iazepW) {
                                val t1 = arrayList[e][2].indexOf("</strong>")
                                var textTitle = ""
                                var fulText = ""
                                if (t1 != -1) {
                                    textTitle = arrayList[e][2].substring(0, t1 + 9)
                                    fulText = arrayList[e][2].substring(t1 + 9)
                                }
                                val spannedtitle = MainActivity.fromHtml(textTitle)
                                val spanned = MainActivity.fromHtml(fulText)
                                this.arrayList.add(OpisanieData(this.arrayList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                            }
                        }
                    }
                }
                val gc = GregorianCalendar()
                val dayF = if (gc.isLeapYear(year)) 29
                else 28
                for (e in 0 until arrayList.size) {
                    if (arrayList[e][1].toInt() == 1 && mun - 1 == Calendar.FEBRUARY && day == dayF) {
                        val t1 = arrayList[e][2].indexOf("</strong>")
                        var textTitle = ""
                        var fulText = ""
                        if (t1 != -1) {
                            textTitle = arrayList[e][2].substring(0, t1 + 9)
                            fulText = arrayList[e][2].substring(t1 + 9)
                        }
                        val spannedtitle = MainActivity.fromHtml(textTitle)
                        val spanned = MainActivity.fromHtml(fulText)
                        this.arrayList.add(OpisanieData(this.arrayList.size + 1, arrayList[e][0].toInt(), arrayList[e][1].toInt(), spannedtitle, spanned, "", ""))
                    }
                }
            }
        }
        loadIconsOnImageView()
        adapter.notifyDataSetChanged()
    }

    private fun loadOpisanieSviat() {
        arrayList.clear()
        val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
        if (fileOpisanieSviat.exists()) {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
            arrayList?.forEach { strings ->
                if (day == strings[0].toInt() && mun == strings[1].toInt()) {
                    var res = strings[2]
                    if (dzenNoch) res = res.replace("#d00505", "#ff6666")
                    val t1 = res.indexOf("</strong>")
                    var textTitle = ""
                    var fulText = ""
                    if (t1 != -1) {
                        textTitle = res.substring(0, t1 + 9)
                        fulText = res.substring(t1 + 9)
                    }
                    val spannedtitle = MainActivity.fromHtml(textTitle)
                    val spanned = MainActivity.fromHtml(fulText)
                    this.arrayList.add(OpisanieData(1, day, mun, spannedtitle, spanned, "", ""))
                    loadIconsOnImageView()
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        binding = OpisanieBinding.inflate(layoutInflater)
        bindingprogress = binding.progressView
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        year = intent.extras?.getInt("year", c[Calendar.YEAR]) ?: c[Calendar.YEAR]
        svity = intent.extras?.getBoolean("glavnyia", false) ?: false
        if (savedInstanceState?.getBoolean("imageViewFullVisable") == true) {
            fullImagePathVisable = savedInstanceState.getString("filePach") ?: ""
            fullPosition = savedInstanceState.getInt("fullPosition")
            val file2 = File(fullImagePathVisable)
            Picasso.get().load(file2).into(binding.imageViewFull)
            binding.imageViewFull.visibility = View.VISIBLE
            binding.listview.visibility = View.GONE
            binding.progressBar2.visibility = View.INVISIBLE
            binding.titleToolbar.text = savedInstanceState.getString("tollbarText")
        } else {
            binding.titleToolbar.text = resources.getText(R.string.zmiest)
        }
        adapter = OpisanieAdapter(this)
        binding.listview.adapter = adapter
        binding.listview.divider = null
        binding.listview.setOnTouchListener(this)
        if (dzenNoch) {
            binding.constraint.setBackgroundResource(R.color.colorbackground_material_dark)
            binding.imageViewFull.background = ContextCompat.getDrawable(this, R.color.colorbackground_material_dark)
        }
        viewSviaryiaIIcon()
        if (savedInstanceState == null) startLoadIconsJob(MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI))
        binding.actionPlus.setOnClickListener {
            if (spid in 20..235) {
                spid -= 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = resources.getString(R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTORIGHT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.actionMinus.setOnClickListener {
            if (spid in 10..225) {
                spid += 5
                val proc = 100 - (spid - 15) * 100 / 215
                bindingprogress.progressAuto.text = resources.getString(R.string.procent, proc)
                startProcent(MainActivity.PROGRESSACTIONAUTOLEFT)
                val prefEditors = k.edit()
                prefEditors.putInt("autoscrollSpid", spid)
                prefEditors.apply()
            }
        }
        binding.listview.setOnScrollListener(object : AbsListView.OnScrollListener {
            private var checkDiff = false

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                mActionDown = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE
            }

            override fun onScroll(list: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (list.adapter == null || list.getChildAt(0) == null) return
                diffScroll = if (list.lastVisiblePosition == list.adapter.count - 1) list.getChildAt(list.childCount - 1).bottom - list.height
                else -1
                if (checkDiff && diffScroll > 0) {
                    checkDiff = false
                    invalidateOptionsMenu()
                }
                if (list.lastVisiblePosition == list.adapter.count - 1 && list.getChildAt(list.childCount - 1).bottom <= list.height) {
                    checkDiff = true
                    autoscroll = false
                    stopAutoStartScroll()
                    stopAutoScroll()
                    invalidateOptionsMenu()
                }
            }
        })
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
            val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
            val fileOpisanie13 = File("$filesDir/sviatyja/opisanie13.json")
            if (!MainActivity.isNetworkAvailable()) {
                if (!svity && (fileOpisanie.exists() || fileOpisanie13.exists())) {
                    loadOpisanieSviatyia()
                } else {
                    val dialoNoIntent = DialogNoInternet()
                    dialoNoIntent.show(supportFragmentManager, "dialoNoIntent")
                }
                binding.progressBar2.visibility = View.GONE
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
                    binding.progressBar2.visibility = View.GONE
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
        if (error && count < 3) {
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
        if (error && count < 3) {
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
        if (error && count < 3) {
            getSviatyia(count + 1)
            return
        }
        val pathReference2 = Malitounik.referens.child("/chytanne/sviatyja/opisanie13.json")
        var update13 = 0L
        pathReference2.metadata.addOnCompleteListener { storageMetadata ->
            if (storageMetadata.isSuccessful) {
                update13 = storageMetadata.result.updatedTimeMillis
            } else {
                error = true
            }
        }.await()
        if (update13 == 0L) error = true
        if (error && count < 3) {
            getSviatyia(count + 1)
            return
        }
        saveOpisanieSviatyia(update, update13)
    }

    private suspend fun saveOpisanieSviatyia(update: Long, update13: Long, count: Int = 0) {
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
        val pathReference13 = Malitounik.referens.child("/chytanne/sviatyja/opisanie13.json")
        val fileOpisanie13 = File("$filesDir/sviatyja/opisanie13.json")
        val time13 = fileOpisanie13.lastModified()
        if (!fileOpisanie13.exists() || time13 < update) {
            pathReference13.getFile(fileOpisanie13).addOnCompleteListener {
                if (!it.isSuccessful) {
                    error = true
                }
            }.await()
        }
        var read13 = ""
        if (fileOpisanie13.exists()) read13 = fileOpisanie13.readText()
        if (read13 == "") error = true
        if (error && count < 3) {
            saveOpisanieSviatyia(update, update13, count + 1)
            return
        }
        loadOpisanieSviatyia()
    }

    private suspend fun getIcons(loadIcons: Boolean, count: Int = 0) {
        val dir = File("$filesDir/icons/")
        if (!dir.exists()) dir.mkdir()
        val dir2 = File("$filesDir/iconsApisanne")
        if (!dir2.exists()) dir2.mkdir()
        if (count < 3) {
            getIcons(loadIcons, count + 1)
            return
        }
        dirList.clear()
        var size = 0L
        val sb = StringBuilder()
        val fileIconMataData = File("$filesDir/iconsMataData.txt")
        val pathReferenceMataData = Malitounik.referens.child("/chytanne/iconsMataData.txt")
        pathReferenceMataData.getFile(fileIconMataData).await()
        val list = fileIconMataData.readText().split("\n")
        for (i in 0 until arrayList.size) {
            list.forEach {
                val t1 = it.indexOf("<-->")
                if (t1 != -1) {
                    val t2 = it.indexOf("<-->", t1 + 4)
                    val name = it.substring(0, t1)
                    val pref = if (svity) "v"
                    else "s"
                    sb.append(name)
                    if (name.contains("${pref}_${arrayList[i].date}_${arrayList[i].mun}")) {
                        val t3 = name.lastIndexOf(".")
                        val fileNameT = name.substring(0, t3) + ".txt"
                        val file = File("$filesDir/iconsApisanne/$fileNameT")
                        try {
                            Malitounik.referens.child("/chytanne/iconsApisanne/$fileNameT").getFile(file).addOnFailureListener {
                                if (file.exists()) file.delete()
                            }.await()
                        } catch (_: Throwable) {
                        }
                        val fileIcon = File("$filesDir/icons/${name}")
                        val time = fileIcon.lastModified()
                        val update = it.substring(t2 + 4).toLong()
                        if (!fileIcon.exists() || time < update) {
                            val updateFile = it.substring(t1 + 4, t2).toLong()
                            dirList.add(DirList(name, updateFile))
                            size += updateFile
                        }
                    }
                }
            }
        }
        val fileList = File("$filesDir/icons").list()
        fileList?.forEach {
            if (!sb.toString().contains(it)) {
                val file = File("$filesDir/icons/$it")
                if (file.exists()) file.delete()
                val t3 = file.name.lastIndexOf(".")
                val fileNameT = file.name.substring(0, t3) + ".txt"
                val fileOpis = File("$filesDir/iconsApisanne/$fileNameT")
                if (fileOpis.exists()) fileOpis.delete()
            }

        }
        if (!loadIcons && MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_CELLULAR)) {
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
        val pref = if (svity) "v"
        else "s"
        val fileList = File("$filesDir/icons").list()
        for (i in 0 until arrayList.size) {
            val indexImg = if (arrayList[i].date == -1) 1
            else arrayList[i].index
            fileList?.forEach {
                if (it.contains("${pref}_${arrayList[i].date}_${arrayList[i].mun}_${indexImg}")) {
                    val t1 = it.lastIndexOf(".")
                    val fileNameT = it.substring(0, t1) + ".txt"
                    val file = File("$filesDir/iconsApisanne/$fileNameT")
                    if (file.exists()) {
                        arrayList[i].textApisanne = file.readText()
                    } else {
                        arrayList[i].textApisanne = ""
                    }
                    val file2 = File("$filesDir/icons/$it")
                    if (file2.exists()) {
                        arrayList[i].image = file2.absolutePath
                        return@forEach
                    }
                }
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
            val layoutParams = binding.toolbar.layoutParams
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

    private fun stopAutoScroll(delayDisplayOff: Boolean = true, saveAutoScroll: Boolean = true) {
        if (autoScrollJob?.isActive == true) {
            if (saveAutoScroll) {
                val prefEditor = k.edit()
                prefEditor.putBoolean("autoscroll", false)
                prefEditor.apply()
            }
            spid = k.getInt("autoscrollSpid", 60)
            binding.actionMinus.visibility = View.GONE
            binding.actionPlus.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
            binding.actionMinus.animation = animation
            binding.actionPlus.animation = animation
            autoScrollJob?.cancel()
            stopAutoStartScroll()
            adapter.notifyDataSetChanged()
            if (!k.getBoolean("scrinOn", false) && delayDisplayOff) {
                resetScreenJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(60000)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun startAutoScroll() {
        if (diffScroll != 0) {
            spid = k.getInt("autoscrollSpid", 60)
            if (binding.actionMinus.visibility == View.GONE) {
                binding.actionMinus.visibility = View.VISIBLE
                binding.actionPlus.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(baseContext, R.anim.alphain)
                binding.actionMinus.animation = animation
                binding.actionPlus.animation = animation
            }
            resetScreenJob?.cancel()
            stopAutoStartScroll()
            autoScroll()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                binding.listview.setSelection(0)
            }
            autoStartScroll()
            invalidateOptionsMenu()
        }
    }

    private fun autoScroll() {
        if (autoScrollJob?.isActive != true) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            autoscroll = true
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscroll", true)
            prefEditor.apply()
            adapter.notifyDataSetChanged()
            invalidateOptionsMenu()
            autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(spid.toLong())
                    if (!mActionDown && !MainActivity.dialogVisable) {
                        binding.listview.scrollListBy(2)
                    }
                }
            }
        }
    }

    private fun autoStartScroll() {
        if (autoScrollJob?.isActive != true) {
            if (spid < 166) {
                val autoTime = (230 - spid) / 10
                var count = 0
                if (autoStartScrollJob?.isActive != true) {
                    autoStartScrollJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        spid = 230
                        autoScroll()
                        while (true) {
                            delay(1000L)
                            if (!mActionDown && !MainActivity.dialogVisable) {
                                spid -= autoTime
                                count++
                            }
                            if (count == 10) {
                                break
                            }
                        }
                        startAutoScroll()
                    }
                }
            } else {
                startAutoScroll()
            }
        }
    }

    private fun stopAutoStartScroll() {
        autoStartScrollJob?.cancel()
    }

    private fun startProcent(progressAction: Int) {
        if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT || progressAction == MainActivity.PROGRESSACTIONAUTORIGHT) {
            procentJobAuto?.cancel()
            bindingprogress.progressAuto.visibility = View.VISIBLE
            if (progressAction == MainActivity.PROGRESSACTIONAUTOLEFT) {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@Opisanie, R.drawable.selector_progress_auto_left)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@Opisanie, R.color.colorPrimary_text))
            } else {
                bindingprogress.progressAuto.background = ContextCompat.getDrawable(this@Opisanie, R.drawable.selector_progress_red)
                bindingprogress.progressAuto.setTextColor(ContextCompat.getColor(this@Opisanie, R.color.colorWhite))
            }
            procentJobAuto = CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                bindingprogress.progressAuto.visibility = View.GONE
            }
        }
    }

    override fun onBack() {
        if (binding.imageViewFull.visibility == View.VISIBLE) {
            binding.imageViewFull.visibility = View.GONE
            binding.listview.visibility = View.VISIBLE
            viewSviaryiaIIcon()
            binding.titleToolbar.text = resources.getText(R.string.zmiest)
            invalidateOptionsMenu()
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
        menu.findItem(R.id.action_carkva).isVisible = k.getBoolean("admin", false)
        menu.findItem(R.id.action_save_as).isVisible = binding.imageViewFull.visibility == View.VISIBLE
        menu.findItem(R.id.action_share).isVisible = binding.imageViewFull.visibility == View.VISIBLE
        autoscroll = k.getBoolean("autoscroll", false)
        val itemAuto = menu.findItem(R.id.action_auto)
        itemAuto.isVisible = true
        when {
            diffScroll < 0 -> itemAuto.isVisible = false
            autoscroll -> itemAuto.setIcon(R.drawable.scroll_icon_on)
            diffScroll == 0 -> itemAuto.setIcon(R.drawable.scroll_icon_up)
            else -> itemAuto.setIcon(R.drawable.scroll_icon)
        }
        val spanString = SpannableString(itemAuto.title.toString())
        val end = spanString.length
        var itemFontSize = setFontInterface(SettingsActivity.GET_FONT_SIZE_MIN, true)
        if (itemFontSize > SettingsActivity.GET_FONT_SIZE_DEFAULT) itemFontSize = SettingsActivity.GET_FONT_SIZE_DEFAULT
        spanString.setSpan(AbsoluteSizeSpan(itemFontSize.toInt(), true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        itemAuto.title = spanString
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("filePach", fullImagePathVisable)
        outState.putInt("filePach", fullPosition)
        outState.putString("tollbarText", binding.titleToolbar.text.toString())
        outState.putBoolean("imageViewFullVisable", binding.imageViewFull.visibility == View.VISIBLE)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = true
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (featureId == AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR && autoscroll) {
            MainActivity.dialogVisable = false
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.action_auto) {
            autoscroll = k.getBoolean("autoscroll", false)
            val prefEditor = k.edit()
            prefEditor.putBoolean("autoscrollAutostart", !autoscroll)
            prefEditor.apply()
            if (autoscroll) {
                stopAutoScroll()
            } else {
                startAutoScroll()
            }
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_share) {
            val sb = StringBuilder()
            sb.append(arrayList[fullPosition].title.trim())
            sb.append(arrayList[fullPosition].text.trim())
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.copy_text), sb.toString())
            clipboard.setPrimaryClip(clip)
            MainActivity.toastView(this@Opisanie, getString(R.string.copy_text), Toast.LENGTH_LONG)
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, "by.carkva_gazeta.malitounik.fileprovider", File(fullImagePathVisable)))
            sendIntent.putExtra(Intent.EXTRA_TEXT, arrayList[fullPosition].title.trim())
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, arrayList[fullPosition].title.trim())
            sendIntent.type = "image/*"
            startActivity(Intent.createChooser(sendIntent, getString(R.string.set_log_file)))
            return true
        }
        if (id == R.id.action_save_as) {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_TITLE, File(fullImagePathVisable).name)
            mSavePdfFile.launch(intent)
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
                carkvaLauncher.launch(intent)
            } else {
                MainActivity.toastView(this, getString(R.string.error))
            }
            return true
        }
        if (id == R.id.action_dzen_noch) {
            val dialogDzenNochSettings = DialogDzenNochSettings()
            dialogDzenNochSettings.show(supportFragmentManager, "DialogDzenNochSettings")
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

    private inner class OpisanieAdapter(context: Activity) : ArrayAdapter<OpisanieData>(context, R.layout.simple_list_item_opisanie, arrayList) {
        override fun isEnabled(position: Int): Boolean {
            return if (!autoscroll) super.isEnabled(position)
            else false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = SimpleListItemOpisanieBinding.inflate(layoutInflater, parent, false)
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
                        sendIntent.putExtra(Intent.EXTRA_TEXT, viewHolder.textTitle.text)
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, viewHolder.textTitle.text)
                        sendIntent.type = "image/*"
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.zmiest)))
                    } else {
                        if (k.getBoolean("dialogHelpShare", true)) {
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
                        binding.listview.visibility = View.GONE
                        fullImagePathVisable = file2.absolutePath
                        fullPosition = position
                        binding.progressBar2.visibility = View.INVISIBLE
                        binding.titleToolbar.text = arrayList[position].title.trim()
                        invalidateOptionsMenu()
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
            val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
            viewHolder.textTitle.textSize = fontBiblia
            viewHolder.textApisanne.textSize = fontBiblia
            viewHolder.textTitle.text = arrayList[position].title.trim()
            val text = arrayList[position].text.trim()
            if (text.isNotEmpty()) {
                viewHolder.text.textSize = fontBiblia
                viewHolder.text.text = text
                viewHolder.text.visibility = View.VISIBLE
                if (autoscroll) {
                    viewHolder.text.clearFocus()
                    viewHolder.text.setTextIsSelectable(false)
                } else {
                    viewHolder.text.setTextIsSelectable(true)
                }
            } else {
                viewHolder.text.visibility = View.GONE
            }
            return rootView
        }
    }

    private class ViewHolder(var textTitle: TextView, var text: TextView, var imageView: ImageView, var buttonPopup: ImageView, var textApisanne: TextView)

    private data class DirList(val name: String?, val sizeBytes: Long)

    private data class OpisanieData(val index: Int, val date: Int, val mun: Int, val title: Spanned, val text: Spanned, var image: String, var textApisanne: String)
}
