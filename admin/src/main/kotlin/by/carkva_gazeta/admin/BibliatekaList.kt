package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.hardware.SensorEvent
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import by.carkva_gazeta.admin.databinding.AdminBibliatekaListBinding
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBibliotekaBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*

class BibliatekaList : BaseActivity(), DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener, DialogDelite.DialogDeliteListener {

    companion object {
        private const val GISTORYIACARKVY = 1
        private const val MALITOUNIKI = 2
        private const val SPEUNIKI = 3
        private const val RELLITARATURA = 4
    }

    private lateinit var binding: AdminBibliatekaListBinding
    private var sqlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var width = 0
    private val arrayList = ArrayList<ArrayList<String>>()
    private lateinit var adapter: BibliotekaAdapter
    private var timeListCalendar = Calendar.getInstance()
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference
    private val caliandarMunLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
        sqlJob?.cancel()
    }

    override fun onDialogEditClick(position: Int) {
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialogDelite = DialogDelite.getInstance(position, name, false)
        dialogDelite.show(supportFragmentManager, "DialogDelite")
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
    }

    private fun getSql() {
        binding.progressBar2.visibility = View.VISIBLE
        try {
            sqlJob = CoroutineScope(Dispatchers.Main).launch {
                val temp = ArrayList<ArrayList<String>>()
                val sb = getBibliatekaJson()
                val gson = Gson()
                val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type).type
                val biblioteka: ArrayList<ArrayMap<String, String>> = gson.fromJson(sb, type)
                for (i in 0 until biblioteka.size) {
                    val mySqlList = ArrayList<String>()
                    val kniga = biblioteka[i]
                    val id = kniga["bib"] ?: ""
                    val rubrika = kniga["rubryka"] ?: ""
                    val link = kniga["link"] ?: ""
                    var str = kniga["str"] ?: ""
                    val pdf = kniga["pdf"] ?: ""
                    var image = kniga["image"] ?: ""
                    mySqlList.add(link)
                    val pos = str.indexOf("</span><br>")
                    str = str.substring(pos + 11)
                    mySqlList.add(str)
                    mySqlList.add(pdf)
                    mySqlList.add(getPdfFile(pdf))
                    mySqlList.add(rubrika)
                    val im1 = image.indexOf("src=\"")
                    val im2 = image.indexOf("\"", im1 + 5)
                    image = image.substring(im1 + 5, im2)
                    val t1 = pdf.lastIndexOf(".")
                    val imageLocal = "$filesDir/image_temp/" + pdf.substring(0, t1) + ".png"
                    mySqlList.add(imageLocal)
                    mySqlList.add(id)
                    if (MainActivity.isNetworkAvailable()) {
                        val dir = File("$filesDir/image_temp")
                        if (!dir.exists()) dir.mkdir()
                        val file = File(imageLocal)
                        if (!file.exists()) {
                            saveImagePdf(pdf, image)
                        }
                    }
                    arrayList.add(mySqlList)
                }
                val json = gson.toJson(arrayList)
                val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val prefEditors = k.edit()
                prefEditors.putString("Biblioteka", json)
                prefEditors.apply()
                adapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE
            }
        } catch (_: Throwable) {
        }
    }

    private suspend fun getPdfFile(pdf: String): String {
        var filesize = "0"
        referens.child("/data/bibliateka/$pdf").metadata.addOnCompleteListener {
            filesize = it.result.sizeBytes.toString()
        }.await()
        return filesize
    }

    private suspend fun saveImagePdf(pdf: String, image: String) {
        val t1 = pdf.lastIndexOf(".")
        val imageTempFile = File("$filesDir/image_temp/" + pdf.substring(0, t1) + ".png")
        referens.child(image).getFile(imageTempFile).addOnFailureListener {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
    }

    private suspend fun getBibliatekaJson(): String {
        var text = ""
        val pathReference = referens.child("/bibliateka.json")
        val localFile = withContext(Dispatchers.IO) {
            File.createTempFile("bibliateka", "json")
        }
        pathReference.getFile(localFile).addOnCompleteListener {
            if (it.isSuccessful) text = localFile.readText()
            else MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
        return text
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = windowManager.currentWindowMetrics
            val bounds = display.bounds
            bounds.width()
        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x
        }
        binding = AdminBibliatekaListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            val dialog = DialogPiarlinyContextMenu.getInstance(position, arrayList[position][0])
            dialog.show(supportFragmentManager, "DialogPiarlinyContextMenu")
            return@setOnItemLongClickListener true
        }
        binding.listView.setOnItemClickListener { _, _, position, _ ->
        }
        adapter = BibliotekaAdapter(this)
        binding.listView.adapter = adapter
        getSql()
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.title_biblijateka)
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

    internal inner class BibliotekaAdapter(private val context: Activity) : ArrayAdapter<ArrayList<String>>(context, R.layout.admin_simple_list_item_biblioteka, arrayList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                val binding = AdminSimpleListItemBibliotekaBinding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label, binding.imageView2, binding.rubrika)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolder
            }
            viewHolder.imageView.setBackgroundResource(R.drawable.frame_image_biblioteka)
            viewHolder.imageView.layoutParams?.width = width / 2
            viewHolder.imageView.layoutParams?.height = (width / 2 * 1.4F).toInt()
            viewHolder.imageView.requestLayout()
            if (arrayList[position].size == 3) {
                if (arrayList[position][2] != "") {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile(arrayList[position][2], options)
                        }
                        viewHolder.imageView.setImageBitmap(bitmap)
                        viewHolder.imageView.visibility = View.VISIBLE
                    }
                } else {
                    viewHolder.imageView.visibility = View.GONE
                }
            } else {
                val t1 = arrayList[position][5].lastIndexOf("/")
                val file = File("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1))
                if (file.exists()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile("$filesDir/image_temp/" + arrayList[position][5].substring(t1 + 1), options)
                        }
                        viewHolder.imageView.setImageBitmap(bitmap)
                        viewHolder.imageView.visibility = View.VISIBLE
                    }
                }
            }
            viewHolder.text.text = arrayList[position][0]
            val rubrika = when (arrayList[position][4].toInt()) {
                GISTORYIACARKVY -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy)
                MALITOUNIKI -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki)
                SPEUNIKI -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki)
                RELLITARATURA -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura)
                else -> ""
            }
            viewHolder.rubrika.text = rubrika
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var imageView: ImageView, var rubrika: TextView)
}