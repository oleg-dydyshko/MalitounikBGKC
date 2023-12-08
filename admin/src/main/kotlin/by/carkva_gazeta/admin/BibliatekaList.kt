package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Point
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminBibliatekaListBinding
import by.carkva_gazeta.admin.databinding.AdminSimpleListItemBibliotekaBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItem1Binding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.*

class BibliatekaList : BaseActivity(), DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener, DialogDelite.DialogDeliteListener {

    private lateinit var binding: AdminBibliatekaListBinding
    private var sqlJob: Job? = null
    private var resetTollbarJob: Job? = null
    private var width = 0
    private val arrayList = ArrayList<ArrayList<String>>()
    private lateinit var adapter: BibliotekaAdapter
    private lateinit var rubrikaAdapter: RubrikaAdapter
    private var position = -1
    private val mActivityResultImageFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageUri = it.data?.data
            imageUri?.let { image ->
                val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(contentResolver, image)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                binding.imagePdf.setImageBitmap(bitmap)
            }
        }
    }
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val fileUri = it.data?.data
            fileUri?.let { file ->
                binding.pdfTextView.text = file.toString()
            }
        }
    }
    
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("editVisibility", binding.edit.visibility == View.VISIBLE)
        outState.putString("pdfTextView", binding.pdfTextView.text.toString())
        outState.putParcelable("BitmapImage", binding.imagePdf.drawable?.toBitmap())
        outState.putInt("position", position)
    }

    override fun onBack() {
        if (binding.edit.visibility == View.VISIBLE) {
            binding.edit.visibility = View.GONE
            binding.listView.visibility = View.VISIBLE
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            invalidateOptionsMenu()
        } else {
            super.onBack()
        }
    }

    private fun setImageSize(imageView: View) {
        imageView.setBackgroundResource(R.drawable.frame_image_biblioteka)
        imageView.layoutParams?.width = width / 2
        imageView.layoutParams?.height = (width / 2 * 1.4F).toInt()
        imageView.requestLayout()
    }

    override fun onDialogEditClick(position: Int) {
        binding.edit.visibility = View.VISIBLE
        binding.listView.visibility = View.GONE
        invalidateOptionsMenu()
        this.position = position
        if (position != -1) {
            binding.textViewTitle.setText(arrayList[position][0])
            binding.rubrika.setSelection(arrayList[position][4].toInt() - 1)
            if (arrayList[position].size == 3) {
                if (arrayList[position][2] != "") {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            return@withContext BitmapFactory.decodeFile(arrayList[position][2], options)
                        }
                        binding.imagePdf.setImageBitmap(bitmap)
                    }
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
                        binding.imagePdf.setImageBitmap(bitmap)
                    }
                }
            }
            binding.pdfTextView.text = arrayList[position][2]
            binding.opisanie.setText(arrayList[position][1])
        } else {
            binding.imagePdf.setImageDrawable(null)
            binding.textViewTitle.setText("")
            binding.rubrika.setSelection(1)
            binding.pdfTextView.text = ""
            binding.opisanie.setText("")
        }
        setImageSize(binding.imagePdf)
        binding.imagePdf.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            mActivityResultImageFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
        }
        binding.admin.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
            mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
        }
    }

    private fun saveBibliateka() {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    val rubrika = (binding.rubrika.selectedItemPosition + 1).toString()
                    val link = binding.textViewTitle.text.toString()
                    val str = binding.opisanie.text.toString()
                    val pdf = binding.pdfTextView.text.toString()
                    val t1 = pdf.lastIndexOf(".")
                    if (t1 == -1 || link == "" || binding.imagePdf.drawable == null) {
                        withContext(Dispatchers.Main) {
                            MainActivity.toastView(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.error))
                            binding.progressBar2.visibility = View.GONE
                        }
                        return@withContext
                    }
                    val imageLocal = "$filesDir/image_temp/" + pdf.substring(0, t1) + ".png"
                    if (position == -1) {
                        val mySqlList = ArrayList<String>()
                        mySqlList.add(link)
                        mySqlList.add(str)
                        mySqlList.add(pdf)
                        mySqlList.add(File(binding.pdfTextView.text.toString()).length().toString())
                        mySqlList.add(rubrika)
                        mySqlList.add(imageLocal)
                        arrayList.add(0, mySqlList)
                    } else {
                        arrayList[position][0] = link
                        arrayList[position][1] = str
                        arrayList[position][2] = pdf
                        if (binding.pdfTextView.text.toString() != "") arrayList[position][3] = File(binding.pdfTextView.text.toString()).length().toString()
                        arrayList[position][4] = rubrika
                        arrayList[position][5] = imageLocal
                    }
                    val dir = File("$filesDir/image_temp")
                    if (!dir.exists()) dir.mkdir()
                    val file = File(imageLocal)
                    val bitmap = binding.imagePdf.drawable?.toBitmap()
                    bitmap?.let {
                        val out = FileOutputStream(file)
                        it.compress(Bitmap.CompressFormat.PNG, 90, out)
                        out.flush()
                        out.close()
                    }
                    saveBibliatekaJson()
                    if (binding.pdfTextView.text.toString() != "") Malitounik.referens.child("/data/bibliateka/$pdf").putFile(Uri.fromFile(File(binding.pdfTextView.text.toString()))).await()
                    Malitounik.referens.child("/images/bibliateka/" + file.name).putFile(Uri.fromFile(file)).await()
                }
                adapter.notifyDataSetChanged()
                MainActivity.toastView(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.save))
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private suspend fun saveBibliatekaJson() {
        val localFile = File("$filesDir/cache/cache.txt")
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
        localFile.writer().use {
            it.write(gson.toJson(arrayList, type))
        }
        Malitounik.referens.child("/bibliateka.json").putFile(Uri.fromFile(localFile)).await()
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialogDelite = DialogDelite.getInstance(position, name, false)
        dialogDelite.show(supportFragmentManager, "DialogDelite")
    }

    override fun fileDelite(position: Int, title: String, isSite: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            Malitounik.referens.child("/data/bibliateka/" + arrayList[position][2]).delete().await()
            val file = File(arrayList[position][5])
            if (file.exists()) {
                file.delete()
            }
            Malitounik.referens.child("/images/bibliateka/" + file.name).delete().await()
            arrayList.removeAt(position)
            adapter.notifyDataSetChanged()
            saveBibliatekaJson()
        }
    }

    private fun getSql() {
        if (MainActivity.isNetworkAvailable()) {
            try {
                sqlJob = CoroutineScope(Dispatchers.Main).launch {
                    val sb = getBibliatekaJson()
                    if (sb != "") {
                        binding.progressBar2.visibility = View.VISIBLE
                        val gson = Gson()
                        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(String::class.java).type).type).type
                        val biblioteka = gson.fromJson<ArrayList<ArrayList<String>>>(sb, type)
                        for (i in 0 until biblioteka.size) {
                            val mySqlList = ArrayList<String>()
                            val kniga = biblioteka[i]
                            val rubrika = kniga[4]
                            val link = kniga[0]
                            val str = kniga[1]
                            val pdf = kniga[2]
                            val pdfFileSize = kniga[3]
                            val image = kniga[5]
                            mySqlList.add(link)
                            mySqlList.add(str)
                            mySqlList.add(pdf)
                            mySqlList.add(pdfFileSize)
                            mySqlList.add(rubrika)
                            val t1 = pdf.lastIndexOf(".")
                            mySqlList.add(pdf.substring(0, t1) + ".png")
                            val dir = File("$filesDir/image_temp")
                            if (!dir.exists()) dir.mkdir()
                            val file = File(image)
                            if (!file.exists()) {
                                saveImagePdf(pdf, image)
                            }
                            arrayList.add(mySqlList)
                        }
                        val json = gson.toJson(arrayList, type)
                        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
                        val prefEditors = k.edit()
                        prefEditors.putString("Biblioteka", json)
                        prefEditors.apply()
                        adapter.notifyDataSetChanged()
                    } else {
                        MainActivity.toastView(this@BibliatekaList, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }
                    binding.progressBar2.visibility = View.GONE
                }
            } catch (_: Throwable) {
            }
        }
    }

    private suspend fun saveImagePdf(pdf: String, image: String) {
        val t1 = pdf.lastIndexOf(".")
        val imageTempFile = File("$filesDir/image_temp/" + pdf.substring(0, t1) + ".png")
        Malitounik.referens.child(image).getFile(imageTempFile).addOnFailureListener {
            MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
    }

    private suspend fun getBibliatekaJson(): String {
        var text = ""
        val pathReference = Malitounik.referens.child("/bibliateka.json")
        val localFile = File("$filesDir/cache/cache.txt")
        pathReference.getFile(localFile).addOnCompleteListener {
            if (it.isSuccessful) text = localFile.readText()
            else MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
        }.await()
        return text
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val array = arrayOf(getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy), getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki), getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki), getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura), getString(by.carkva_gazeta.malitounik.R.string.arx_num_gaz))
        rubrikaAdapter = RubrikaAdapter(this, array)
        binding.rubrika.adapter = rubrikaAdapter
        adapter = BibliotekaAdapter(this)
        binding.listView.adapter = adapter
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("editVisibility")) {
                binding.edit.visibility = View.VISIBLE
                binding.listView.visibility = View.GONE
            }
            position = savedInstanceState.getInt("position")
            binding.pdfTextView.text = savedInstanceState.getString("pdfTextView")
            binding.imagePdf.setImageBitmap(savedInstanceState.getParcelable("BitmapImage"))
            setImageSize(binding.imagePdf)
        }
        getSql()
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
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

    override fun onPrepareMenu(menu: Menu) {
        if (binding.edit.visibility == View.VISIBLE) {
            menu.findItem(R.id.action_plus).isVisible = false
            menu.findItem(R.id.action_save).isVisible = true
        } else {
            menu.findItem(R.id.action_plus).isVisible = true
            menu.findItem(R.id.action_save).isVisible = false
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_save) {
            saveBibliateka()
            return true
        }
        if (id == R.id.action_plus) {
            onDialogEditClick(-1)
            return true
        }
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_bibliateka_list, menu)
        super.onCreateMenu(menu, menuInflater)
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
            setImageSize(viewHolder.imageView)
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
                MainActivity.GISTORYIACARKVY -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_gistoryia_carkvy)
                MainActivity.MALITOUNIKI -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_malitouniki)
                MainActivity.SPEUNIKI -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_speuniki)
                MainActivity.RELLITARATURA -> getString(by.carkva_gazeta.malitounik.R.string.bibliateka_rel_litaratura)
                MainActivity.PDF -> getString(by.carkva_gazeta.malitounik.R.string.arx_num_gaz)
                else -> ""
            }
            viewHolder.rubrika.text = rubrika
            return rootView
        }
    }

    private class ViewHolder(var text: TextView, var imageView: ImageView, var rubrika: TextView)

    private class RubrikaAdapter(activity: Activity, private val dataRubrika: Array<String>) : ArrayAdapter<String>(activity, by.carkva_gazeta.malitounik.R.layout.simple_list_item_1, dataRubrika) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getDropDownView(position, convertView, parent)
            val textView = v as TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            textView.text = dataRubrika[position]
            textView.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return v
        }

        override fun getCount(): Int {
            return dataRubrika.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolderRubrika
            if (convertView == null) {
                val binding = SimpleListItem1Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolderRubrika(binding.text1)
                rootView.tag = viewHolder
            } else {
                rootView = convertView
                viewHolder = rootView.tag as ViewHolderRubrika
            }
            viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            viewHolder.text.text = dataRubrika[position]
            viewHolder.text.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default)
            return rootView
        }
    }

    private class ViewHolderRubrika(var text: TextView)
}