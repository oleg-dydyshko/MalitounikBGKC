package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.carkva_gazeta.malitounik.databinding.GalleryBinding
import by.carkva_gazeta.malitounik.databinding.GalleryItemBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.DecimalFormat
import java.util.Calendar
import java.util.GregorianCalendar

class Gallery : BaseActivity(), DialogOpisanieWIFI.DialogOpisanieWIFIListener {

    private lateinit var binding: GalleryBinding
    private lateinit var adapter: GalleryAdapter
    private val gallery = ArrayList<GalleryData>()
    private val dzenNoch get() = getBaseDzenNoch()
    private val dirList = ArrayList<DirList>()
    private var loadIconsJob: Job? = null
    private lateinit var chin: SharedPreferences

    override fun onPause() {
        super.onPause()
        loadIconsJob?.cancel()
        val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
        val prefEditor = chin.edit()
        prefEditor.putInt("galleryPosition", layoutManager.findFirstVisibleItemPosition())
        prefEditor.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chin = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        if (savedInstanceState?.getBoolean("imageViewFullVisable") == true) {
            val file2 = File(fullImagePathVisable)
            Picasso.get().load(file2).into(binding.imageViewFull)
            binding.imageViewFull.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.titleToolbar.text = savedInstanceState.getString("textFull")
        } else {
            binding.titleToolbar.text = resources.getText(R.string.gallery)
        }
        if (dzenNoch) {
            binding.actionForward.background = ContextCompat.getDrawable(this, R.drawable.selector_dark_maranata_buttom)
        }
        binding.recyclerView.post {
            val width = binding.recyclerView.width
            val spancount = when {
                width < 600 -> 1
                width < 1200 -> 2
                width < 2200 -> 4
                else -> 6
            }
            loadGallery()
            binding.recyclerView.layoutManager = GridLayoutManager(this, spancount)
            adapter = GalleryAdapter(this, binding, gallery)
            binding.recyclerView.adapter = adapter
            val pos = chin.getInt("galleryPosition", 0)
            binding.recyclerView.scrollToPosition(pos)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (dzenNoch) {
            binding.toolbar.popupTheme = R.style.AppCompatDark
        }
        binding.actionForward.setOnClickListener {
            val file = File(gallery[fullImagePosition].iconPath)
            Picasso.get().load(file).into(binding.imageViewFull)
            fullImagePathVisable = file.absolutePath
            binding.recyclerView.visibility = View.INVISIBLE
            binding.titleToolbar.text = gallery[fullImagePosition].title.replace("\n", " ")
            fullImagePosition++
            if (fullImagePosition >= gallery.size) {
                val animation2 = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
                binding.actionForward.visibility = View.GONE
                binding.actionForward.animation = animation2
            }
        }
        startLoadIconsJob(MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI))
    }

    private fun loadGallery() {
        gallery.clear()
        val dir = File("$filesDir/icons").list()
        dir?.forEach {
            val t1 = it.indexOf("_")
            val t2 = it.indexOf("_", t1 + 1)
            var t3 = it.indexOf("_", t2 + 1)
            if (t3 == -1) t3 = it.indexOf(".", t2 + 1)
            val day = it.substring(t1 + 1, t2).toInt()
            val mun = it.substring(t2 + 1, t3).toInt()
            val gc = GregorianCalendar()
            gc.set(Calendar.DATE, day)
            gc.set(Calendar.MONTH, mun - 1)
            if (it.contains("s")) {
                gallery.add(GalleryData(gc[Calendar.DAY_OF_YEAR], loadOpisanieSviatyia(day, mun, it.substring(t3 + 1, t3 + 2).toInt()), "$filesDir/icons/$it", File("$filesDir/icons/$it").lastModified()))
            } else {
                if (day == -1) {
                    val list = svityRuchomyia(mun)
                    gc.set(Calendar.DATE, list[0])
                    gc.set(Calendar.MONTH, list[1])
                }
                gallery.add(GalleryData(gc[Calendar.DAY_OF_YEAR], loadOpisanieSviat(day, mun), "$filesDir/icons/$it", File("$filesDir/icons/$it").lastModified()))
            }
        }
        gallery.sort()
        if (this::adapter.isInitialized) adapter.updateList(gallery)
    }

    private fun svityRuchomyia(mun: Int): Array<Int> {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        var dataP: Int
        val monthP: Int
        val a = year % 19
        val b = year % 4
        val cx = year % 7
        val ks = year / 100
        val p = (13 + 8 * ks) / 25
        val q = ks / 4
        val m = (15 - p + ks - q) % 30
        val n = (4 + ks - q) % 7
        val d = (19 * a + m) % 30
        val ex = (2 * b + 4 * cx + 6 * d + n) % 7
        if (d + ex <= 9) {
            dataP = d + ex + 22
            monthP = Calendar.MARCH
        } else {
            dataP = d + ex - 9
            if (d == 29 && ex == 6) dataP = 19
            if (d == 28 && ex == 6) dataP = 18
            monthP = Calendar.APRIL
        }
        val gc = GregorianCalendar(year, monthP, dataP)
        when (mun) {
            0 -> gc.add(Calendar.DATE, -7)
            2 -> gc.add(Calendar.DATE, 39)
            3 -> gc.add(Calendar.DATE, 49)
            4 -> {
                for (i in 13..19) {
                    gc.set(year, Calendar.JULY, i)
                    val wik = gc.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        break
                    }
                }
            }
        }
        return arrayOf(gc.get(Calendar.DATE), gc.get(Calendar.MONTH) + 1)
    }

    private fun loadOpisanieSviat(day: Int, mun: Int): String {
        var dayS = day
        var munS = mun
        val munName = resources.getStringArray(R.array.meciac_smoll)
        var result = ""
        val fileOpisanieSviat = File("$filesDir/opisanie_sviat.json")
        if (fileOpisanieSviat.exists()) {
            val builder = fileOpisanieSviat.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val arrayList = gson.fromJson<ArrayList<ArrayList<String>>>(builder, type)
            arrayList?.forEach { strings ->
                if (day == strings[0].toInt() && mun == strings[1].toInt()) {
                    try {
                        var res = strings[2]
                        if (dzenNoch) res = res.replace("#d00505", "#f44336")
                        val t1 = res.indexOf("<strong>")
                        val t2 = res.indexOf("</strong>")
                        if (day == -1) {
                            val list = svityRuchomyia(mun)
                            dayS = list[0]
                            munS = list[1]
                        }
                        result = MainActivity.fromHtml(res.substring(t1 + 8, t2)).toString() + "\n(" + dayS + " " + munName[munS - 1] + ")"
                    } catch (_: Throwable) {
                    }
                }
            }
        }
        return result
    }

    private fun loadOpisanieSviatyia(day: Int, mun: Int, count: Int): String {
        val munName = resources.getStringArray(R.array.meciac_smoll)
        var result = ""
        if (day == -1) {
            val fileOpisanie = File("$filesDir/sviatyja/opisanie13.json")
            if (!fileOpisanie.exists()) return result
            val builder = fileOpisanie.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
            val arrayList = ArrayList<ArrayList<String>>()
            if (builder.isNotEmpty()) {
                arrayList.addAll(gson.fromJson(builder, type))
                for (i in 0 until arrayList.size) {
                    var dayR = 1
                    if (mun == arrayList[i][1].toInt()) {
                        val t1 = arrayList[i][2].indexOf("<strong>")
                        val t2 = arrayList[i][2].indexOf("</strong>")
                        if (t1 != -1 && t2 != -1) {
                            if (mun == 0) {
                                val pasha = Calendar.getInstance()
                                pasha.set(pasha[Calendar.YEAR], Calendar.DECEMBER, 25)
                                val pastvoW = pasha[Calendar.DAY_OF_WEEK]
                                for (e in 26..31) {
                                    val pastvo = GregorianCalendar(pasha[Calendar.YEAR], Calendar.DECEMBER, e)
                                    val iazepW = pastvo[Calendar.DAY_OF_WEEK]
                                    if (pastvoW != Calendar.SUNDAY) {
                                        if (Calendar.SUNDAY == iazepW) {
                                            dayR = pastvo[Calendar.DATE]
                                        }
                                    } else {
                                        if (Calendar.MONDAY == iazepW) {
                                            dayR = pastvo[Calendar.DATE]
                                        }
                                    }
                                }
                                result = arrayList[i][2].substring(t1 + 8, t2) + "\n(" + dayR + " " + munName[Calendar.DECEMBER] + ")"
                            }
                            if (mun == 1) {
                                val gc = Calendar.getInstance() as GregorianCalendar
                                dayR = if (gc.isLeapYear(gc[Calendar.YEAR])) 29
                                else 28
                                result = arrayList[i][2].substring(t1 + 8, t2) + "\n(" + dayR + " " + munName[Calendar.FEBRUARY] + ")"
                            }
                        }
                    }

                }
            } else return result
        } else {
            val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
            if (!fileOpisanie.exists()) return result
            val builder = fileOpisanie.readText()
            val gson = Gson()
            val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
            var res: String
            val arrayList = ArrayList<String>()
            if (builder.isNotEmpty()) {
                arrayList.addAll(gson.fromJson(builder, type))
                res = arrayList[day - 1]
            } else return result
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
            try {
                val tit = title[count - 1]
                val t1 = tit.indexOf("<strong>")
                val t2 = tit.indexOf("</strong>")
                result = tit.substring(t1 + 8, t2) + "\n(" + day + " " + munName[mun - 1] + ")"
            } catch (_: Throwable) {
            }
        }
        return result
    }

    override fun onBack() {
        if (binding.recyclerView.visibility == View.INVISIBLE) {
            binding.imageViewFull.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.titleToolbar.text = resources.getText(R.string.gallery)
            if (binding.actionForward.visibility == View.VISIBLE) {
                val animation2 = AnimationUtils.loadAnimation(baseContext, R.anim.alphaout)
                binding.actionForward.visibility = View.GONE
                binding.actionForward.animation = animation2
            }
            invalidateOptionsMenu()
        } else {
            super.onBack()
        }
    }

    override fun onDialogPositiveOpisanieWIFI() {
        startLoadIconsJob(true)
    }

    override fun onDialogNegativeOpisanieWIFI() {
        binding.progressBar2.visibility = View.GONE
    }

    private fun startLoadIconsJob(loadIcons: Boolean) {
        if (loadIconsJob?.isActive != true) {
            loadIconsJob = CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.isIndeterminate = true
                binding.progressBar2.visibility = View.VISIBLE
                if (!MainActivity.isNetworkAvailable()) {
                    val dialoNoIntent = DialogNoInternet()
                    dialoNoIntent.show(supportFragmentManager, "dialoNoIntent")
                } else {
                    try {
                        getSviatyia()
                        getOpisanieSviat()
                        getIcons(loadIcons)
                    } catch (_: Throwable) {
                    }
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
        val file = File("$filesDir/opisanie_sviat.json")
        if (!file.exists()) {
            val pathReference = Malitounik.referens.child("/opisanie_sviat.json")
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
        }
    }

    private suspend fun getSviatyia(count: Int = 0) {
        val dir = File("$filesDir/sviatyja/")
        if (!dir.exists()) dir.mkdir()
        for (mun in 1..12) {
            val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
            if (!fileOpisanie.exists()) {
                val pathReference = Malitounik.referens.child("/chytanne/sviatyja/opisanie$mun.json")
                var error = false
                pathReference.getFile(fileOpisanie).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        error = true
                    }
                }.await()
                var read = ""
                if (fileOpisanie.exists()) read = fileOpisanie.readText()
                if (read == "") error = true
                if (error && count < 3) {
                    getSviatyia(count + 1)
                    return
                }
            }
        }
    }

    private fun formatFigureTwoPlaces(value: Float): String {
        val myFormatter = DecimalFormat("##0.00")
        return myFormatter.format(value.toDouble())
    }

    private suspend fun getIcons(loadIcons: Boolean, count: Int = 0) {
        val dir = File("$filesDir/icons/")
        if (!dir.exists()) dir.mkdir()
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
        var sizeUpload = 0f
        binding.titleToolbar.text = getString(R.string.gallery_search, "няма")
        binding.progressBar2.isIndeterminate = true
        list.forEach {
            val t1 = it.indexOf("<-->")
            if (t1 != -1) {
                val t2 = it.indexOf("<-->", t1 + 4)
                val name = it.substring(0, t1)
                sb.append(name)
                val fileIcon = File("$filesDir/icons/$name")
                val time = fileIcon.lastModified()
                val update = it.substring(t2 + 4).toLong()
                if (!fileIcon.exists() || time < update) {
                    val updateFile = it.substring(t1 + 4, t2).toLong()
                    sizeUpload += updateFile
                    val sizeImage = if (size / 1024 > 1000) {
                        " ${formatFigureTwoPlaces(sizeUpload / 1024 / 1024)} Мб "
                    } else {
                        " ${formatFigureTwoPlaces(sizeUpload / 1024)} Кб "
                    }
                    binding.titleToolbar.text = getString(R.string.gallery_search, sizeImage)
                    dirList.add(DirList(name, updateFile))
                    size += updateFile
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
                binding.progressBar2.visibility = View.GONE
            }
        } else {
            binding.progressBar2.isIndeterminate = false
            binding.progressBar2.progress = 0
            binding.progressBar2.max = size.toInt()
            var progress = 0
            sizeUpload = size.toFloat()
            for (i in 0 until dirList.size) {
                try {
                    val fileIcon = File("$filesDir/icons/" + dirList[i].name)
                    val pathReference = Malitounik.referens.child("/chytanne/icons/" + dirList[i].name)
                    pathReference.getFile(fileIcon).await()
                    sizeUpload -= dirList[i].sizeBytes
                    val sizeImage = if (size / 1024 > 1000) {
                        " ${formatFigureTwoPlaces(sizeUpload / 1024 / 1024)} Мб "
                    } else {
                        " ${formatFigureTwoPlaces(sizeUpload / 1024)} Кб "
                    }
                    binding.titleToolbar.text = getString(R.string.gallery_download, sizeImage)
                    progress += dirList[i].sizeBytes.toInt()
                    binding.progressBar2.progress = progress
                } catch (_: Throwable) {
                }
            }
            binding.progressBar2.visibility = View.GONE
            loadGallery()
        }
        binding.titleToolbar.text = resources.getText(R.string.gallery)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.gallery, menu)
        super.onCreateMenu(menu, menuInflater)
    }

    override fun onPrepareMenu(menu: Menu) {
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
        menu.findItem(R.id.action_download_all).isVisible = binding.recyclerView.visibility != View.INVISIBLE
        when (chin.getInt("gallery_sort", 0)) {
            1 -> {
                menu.findItem(R.id.sortdate).isChecked = true
                menu.findItem(R.id.sorttime).isChecked = false
            }

            2 -> {
                menu.findItem(R.id.sortdate).isChecked = false
                menu.findItem(R.id.sorttime).isChecked = true
            }

            else -> {
                menu.findItem(R.id.sortdate).isChecked = false
                menu.findItem(R.id.sorttime).isChecked = false
            }
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBack()
            return true
        }
        if (id == R.id.sortdate) {
            val prefEditors = chin.edit()
            if (item.isChecked) {
                prefEditors.putInt("gallery_sort", 0)
            } else {
                prefEditors.putInt("gallery_sort", 1)
            }
            prefEditors.apply()
            val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            gallery.sort()
            adapter.updateList(gallery)
            binding.recyclerView.scrollToPosition(position)
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.sorttime) {
            val prefEditors = chin.edit()
            if (item.isChecked) {
                prefEditors.putInt("gallery_sort", 0)
            } else {
                prefEditors.putInt("gallery_sort", 2)
            }
            prefEditors.apply()
            val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
            val position = layoutManager.findFirstVisibleItemPosition()
            gallery.sort()
            adapter.updateList(gallery)
            binding.recyclerView.scrollToPosition(position)
            invalidateOptionsMenu()
            return true
        }
        if (id == R.id.action_download_all) {
            startLoadIconsJob(MainActivity.isNetworkAvailable(MainActivity.TRANSPORT_WIFI))
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
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("imageViewFullVisable", binding.imageViewFull.visibility == View.VISIBLE)
        outState.putString("textFull", binding.titleToolbar.text.toString())
    }

    private class GalleryAdapter(val context: Activity, val binding: GalleryBinding, gallery: ArrayList<GalleryData>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
        private val mItemList = ArrayList<GalleryData>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val galleryBinding = GalleryItemBinding.inflate(LayoutInflater.from(context), parent, false)
            val view = galleryBinding.root
            view.setOnClickListener {
                val position = binding.recyclerView.getChildLayoutPosition(it)
                val file = File(mItemList[position].iconPath)
                Picasso.get().load(file).into(binding.imageViewFull)
                binding.imageViewFull.visibility = View.VISIBLE
                fullImagePathVisable = file.absolutePath
                fullImagePosition = position
                binding.recyclerView.visibility = View.INVISIBLE
                binding.titleToolbar.text = mItemList[position].title.replace("\n", " ")
                fullImagePosition++
                if (fullImagePosition >= mItemList.size) {
                    if (binding.actionForward.visibility == View.VISIBLE) {
                        val animation2 = AnimationUtils.loadAnimation(context, R.anim.alphaout)
                        binding.actionForward.visibility = View.GONE
                        binding.actionForward.animation = animation2
                    }
                } else {
                    val animation2 = AnimationUtils.loadAnimation(context, R.anim.alphain)
                    binding.actionForward.visibility = View.VISIBLE
                    binding.actionForward.animation = animation2
                }
                context.invalidateOptionsMenu()
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = mItemList[position].title
            Picasso.get().load(File(mItemList[position].iconPath)).resize(500, 500).onlyScaleDown().centerInside().into(holder.imageView)
        }

        override fun getItemCount(): Int {
            return mItemList.size
        }

        fun updateList(newGalleryData: ArrayList<GalleryData>) {
            val diffCallback = RecyclerViewDiffCallback(mItemList, newGalleryData)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            mItemList.clear()
            mItemList.addAll(newGalleryData)
            diffResult.dispatchUpdatesTo(this)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val textView: TextView = itemView.findViewById(R.id.textView)
        }

        init {
            mItemList.addAll(gallery)
        }
    }

    private class RecyclerViewDiffCallback(private val oldArrayList: ArrayList<GalleryData>, private val newArrayList: ArrayList<GalleryData>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldArrayList.size
        }

        override fun getNewListSize(): Int {
            return newArrayList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldArrayList[oldItemPosition] == newArrayList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldArrayList[oldItemPosition] == newArrayList[newItemPosition]
        }
    }

    private data class GalleryData(val id: Int, val title: String, val iconPath: String, val fileLastModified: Long) : Comparable<GalleryData> {
        override fun compareTo(other: GalleryData): Int {
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            when (k.getInt("gallery_sort", 0)) {
                1 -> return title.compareTo(other.title, true)
                2 -> {
                    if (fileLastModified < other.fileLastModified) {
                        return 1
                    } else if (fileLastModified > other.fileLastModified) {
                        return -1
                    }
                }

                else -> {
                    if (this.id > other.id) {
                        return 1
                    } else if (this.id < other.id) {
                        return -1
                    }
                }
            }
            return 0
        }
    }

    private data class DirList(val name: String?, val sizeBytes: Long)

    companion object {
        private var fullImagePathVisable = ""
        private var fullImagePosition = 0
    }
}