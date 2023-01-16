package by.carkva_gazeta.admin

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import by.carkva_gazeta.admin.databinding.AdminSviatyiaImageBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.MenuListData
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.ListItemImageBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class SviatyiaImage : BaseActivity(), DialogImageFileExplorer.DialogImageFileExplorerListener, DialogDeliteImage.DialogDeliteListener, DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener {

    private lateinit var binding: AdminSviatyiaImageBinding
    private lateinit var adapter: ItemAdapter
    private var resetTollbarJob: Job? = null
    private var mLastClickTime: Long = 0
    private var mun = 1
    private var day = 1
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference
    private val images = ArrayList<DataImages>()
    private val arrayListIcon = ArrayList<ArrayList<String>>()
    private val mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(0, false)
            dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
        }
    }

    override fun setImageFile(bitmap: Bitmap?, position: Int) {
        fileUpload(bitmap, position)
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun onDialogEditClick(position: Int) {
        val permissionCheck = ContextCompat.checkSelfPermission(this@SviatyiaImage, READ_EXTERNAL_STORAGE)
        if (PackageManager.PERMISSION_DENIED == permissionCheck) {
            mPermissionResult.launch(READ_EXTERNAL_STORAGE)
        } else {
            val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(position, false)
            dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
        }
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialog = DialogDeliteImage.getInstance(position, images[position].file.absolutePath)
        dialog.show(supportFragmentManager, "DialogDeliteImage")
    }

    override fun setImageFileCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun imageFileDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun imageFileDelite(position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            val file = images[position].file
            referens.child("/chytanne/icons/" + file.name).delete().await()
            val imageFile = File("$filesDir/icons/" + file.name)
            if (imageFile.exists()) {
                imageFile.delete()
            }
            images[position].file = File("")
            images[position].size = 0L
            adapter.updateList(images)
            saveIconJson()
            binding.progressBar2.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        resetTollbarJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (!MainActivity.checkBrightness) {
            val lp = window.attributes
            lp.screenBrightness = MainActivity.brightness.toFloat() / 100
            window.attributes = lp
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = AdminSviatyiaImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        CoroutineScope(Dispatchers.Main).launch {
            val localFile2 = withContext(Dispatchers.IO) {
                File.createTempFile("icons", "json")
            }
            referens.child("/icons.json").getFile(localFile2).addOnCompleteListener {
                if (it.isSuccessful) {
                    val gson = Gson()
                    val json = localFile2.readText()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    arrayListIcon.addAll(gson.fromJson(json, type))
                } else {
                    MainActivity.toastView(this@SviatyiaImage, getString(by.carkva_gazeta.malitounik.R.string.error))
                }
            }.await()
            getIcons()
        }
        adapter = ItemAdapter(by.carkva_gazeta.malitounik.R.id.image, false)
        binding.dragListView.recyclerView.isVerticalScrollBarEnabled = false
        binding.dragListView.setLayoutManager(LinearLayoutManager(this))
        binding.dragListView.setAdapter(adapter, true)
        binding.dragListView.setCanDragHorizontally(false)
        binding.dragListView.setCanDragVertically(true)
        binding.dragListView.setSwipeListener(object : ListSwipeHelper.OnSwipeListenerAdapter() {
            override fun onItemSwipeStarted(item: ListSwipeItem) {
            }

            override fun onItemSwipeEnded(item: ListSwipeItem, swipedDirection: ListSwipeItem.SwipeDirection) {
                val adapterItem = item.tag as DataImages
                val pos = binding.dragListView.adapter.getPositionForItem(adapterItem)
                if (swipedDirection == ListSwipeItem.SwipeDirection.LEFT) {
                    val dialog = DialogDeliteImage.getInstance(pos, images[pos].file.absolutePath)
                    dialog.show(supportFragmentManager, "DialogDeliteImage")
                }
                if (swipedDirection == ListSwipeItem.SwipeDirection.RIGHT) {
                    val permissionCheck = ContextCompat.checkSelfPermission(this@SviatyiaImage, READ_EXTERNAL_STORAGE)
                    if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                        mPermissionResult.launch(READ_EXTERNAL_STORAGE)
                    } else {
                        val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(pos, false)
                        dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
                    }
                }
            }
        })
        binding.dragListView.setDragListListener(object : DragListView.DragListListener {
            override fun onItemDragStarted(position: Int) {
            }

            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {
            }

            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.progressBar2.visibility = View.VISIBLE
                    binding.dragListView.isDragEnabled = false
                    val arraytemp = ArrayList<DataImages>()
                    for (i in 0 until images.size) {
                        val posItems = when (i) {
                            1 -> "_2"
                            2 -> "_3"
                            3 -> "_4"
                            else -> ""
                        }
                        if (images[i].size != 0L) {
                            referens.child("/chytanne/icons/s_${day}_${mun}${posItems}.jpg").putFile(Uri.fromFile(images[i].file)).await()
                            val file = File("$filesDir/icons/s_${day}_${mun}${posItems}.jpg")
                            arraytemp.add(DataImages(getSviatyia(toPosition), file.length(), file, images[i].position))
                        } else {
                            arraytemp.add(DataImages(getSviatyia(toPosition), 0, File(""), images[i].position))
                            if (images[i].size != 0L) {
                                referens.child("/chytanne/icons/s_${day}_${mun}${posItems}.jpg").delete().await()
                            }
                        }
                    }
                    images.clear()
                    images.addAll(arraytemp)
                    saveIconJson()
                    binding.dragListView.isDragEnabled = true
                    getIcons()
                }
            }
        })
        setTollbarTheme()
    }

    private fun setTollbarTheme() {
        binding.titleToolbar.setOnClickListener {
            fullTextTollbar()
        }
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN + 4.toFloat())
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.titleToolbar.text = getString(by.carkva_gazeta.malitounik.R.string.admin_img_sviat)
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

    private fun fileUpload(bitmap: Bitmap?, position: Int) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("imageSave", "jpeg")
                }
                withContext(Dispatchers.IO) {
                    bitmap?.let {
                        val out = FileOutputStream(localFile)
                        it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                    }
                }
                bitmap?.let {
                    if (position == 0) {
                        referens.child("/chytanne/icons/s_${day}_${mun}.jpg").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                            val file = File("$filesDir/icons/s_${day}_${mun}.jpg")
                            localFile.copyTo(file, true)
                            images[position].file = file
                            images[position].size = file.length()
                        }.await()
                        arrayListIcon.forEach { result ->
                            if (images[position].file.name == result[0]) {
                                referens.child("/chytanne/icons/" + images[position].file.name).metadata.addOnSuccessListener {
                                    result[1] = it.sizeBytes.toString()
                                    result[2] = it.updatedTimeMillis.toString()
                                }.await()
                            }
                        }
                    } else {
                        referens.child("/chytanne/icons/s_${day}_${mun}_${position + 1}.jpg").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                            val file = File("$filesDir/icons/s_${day}_${mun}_${position + 1}.jpg")
                            localFile.copyTo(file, true)
                            images[position].file = file
                            images[position].size = file.length()
                        }.await()
                        arrayListIcon.forEach { result ->
                            if (images[position].file.name == result[0]) {
                                referens.child("/chytanne/icons/" + images[position].file.name).metadata.addOnSuccessListener {
                                    result[1] = it.sizeBytes.toString()
                                    result[2] = it.updatedTimeMillis.toString()
                                }.await()
                            }
                        }
                    }
                    adapter.updateList(images)
                }
                saveIconJson()
                binding.progressBar2.visibility = View.GONE
            }
        }
    }

    private suspend fun saveIconJson() {
        withContext(Dispatchers.IO) {
            val tempList = ArrayList<ArrayList<String>>()
            arrayListIcon.forEach {
                if (it[0].contains("s_${day}_${mun}.") || it[0].contains("s_${day}_${mun}_")) {
                    tempList.add(it)
                }
            }
            arrayListIcon.removeAll(tempList)
            val list = referens.child("/chytanne/icons").list(1000).await()
            list.items.forEach { result ->
                if (result.name.contains("s_${day}_${mun}.") || result.name.contains("s_${day}_${mun}_")) {
                    val array = ArrayList<String>()
                    val meta = result.metadata.await()
                    array.add(result.name)
                    array.add(meta.sizeBytes.toString())
                    array.add(meta.updatedTimeMillis.toString())
                    arrayListIcon.add(array)
                }
            }
            val localFile2 = withContext(Dispatchers.IO) {
                File.createTempFile("icons", "json")
            }
            arrayListIcon.sortBy {
                it[0]
            }
            localFile2.writer().use {
                val gson = Gson()
                it.write(gson.toJson(arrayListIcon))
            }
            referens.child("/icons.json").putFile(Uri.fromFile(localFile2)).addOnCompleteListener {
                if (it.isSuccessful) MainActivity.toastView(this@SviatyiaImage, getString(by.carkva_gazeta.malitounik.R.string.save))
                else MainActivity.toastView(this@SviatyiaImage, getString(by.carkva_gazeta.malitounik.R.string.error))
            }.await()
        }
    }

    private suspend fun getIcons() {
        if (MainActivity.isNetworkAvailable()) {
            binding.progressBar2.visibility = View.VISIBLE
            val dir = File("$filesDir/icons/")
            if (!dir.exists()) dir.mkdir()
            var position = 0L
            val tempArray = ArrayList<DataImages>()
            images.clear()
            if (arrayListIcon.size == 0) MainActivity.toastView(this@SviatyiaImage, getString(by.carkva_gazeta.malitounik.R.string.error))
            arrayListIcon.forEach {
                if (it[0].contains("s_${day}_${mun}.") || it[0].contains("s_${day}_${mun}_")) {
                    val fileIcon = File("$filesDir/icons/" + it[0])
                    val pathReference = referens.child("/chytanne/icons/" + it[0])
                    pathReference.getFile(fileIcon).addOnFailureListener {
                        MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                    tempArray.add(DataImages(getSviatyia(position.toInt()), fileIcon.length(), fileIcon, position))
                    position++
                }
            }
            val tempArraySize = tempArray.size
            var e = 0
            for (i in 0..3) {
                if (i == 0 && tempArraySize > 0) {
                    if (tempArray[e].file.name.contains("s_${day}_${mun}.")) {
                        images.add(tempArray[e])
                        e++
                    } else {
                        images.add(DataImages(getSviatyia(position.toInt()), 0, File(""), position))
                        position++
                    }
                } else {
                    if (e < tempArraySize) {
                        val t1 = tempArray[e].file.name.lastIndexOf(".")
                        val t2 = tempArray[e].file.name.lastIndexOf("_")
                        val chislo = tempArray[e].file.name.substring(t2 + 1, t1).toInt()
                        if (chislo == i + 1) {
                            images.add(tempArray[e])
                            e++
                        } else {
                            images.add(DataImages(getSviatyia(position.toInt()), 0, File(""), position))
                            position++
                        }
                    } else {
                        images.add(DataImages(getSviatyia(position.toInt()), 0, File(""), position))
                        position++
                    }
                }
            }
            adapter.updateList(images)
            binding.progressBar2.visibility = View.GONE
        }
    }

    private suspend fun getSviatyia(position: Int): String {
        var title = ""
        val dir = File("$filesDir/sviatyja/")
        if (!dir.exists()) dir.mkdir()
        val list = referens.child("/chytanne/sviatyja").list(12).await()
        list.items.forEach { storageReference ->
            if (mun == Calendar.getInstance()[Calendar.MONTH] + 1) {
                var update = 0L
                storageReference.metadata.addOnSuccessListener { storageMetadata ->
                    update = storageMetadata.updatedTimeMillis
                }.await()
                val fileOpisanie = File("$filesDir/sviatyja/" + storageReference.name)
                val time = fileOpisanie.lastModified()
                if (!fileOpisanie.exists() || time < update) {
                    storageReference.getFile(fileOpisanie).addOnFailureListener {
                        MainActivity.toastView(this, getString(by.carkva_gazeta.malitounik.R.string.error))
                    }.await()
                }
            }
        }
        val fileOpisanie = File("$filesDir/sviatyja/opisanie$mun.json")
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        var res = ""
        val arrayList = ArrayList<String>()
        if (fileOpisanie.exists() && fileOpisanie.readText().isNotEmpty()) {
            arrayList.addAll(gson.fromJson(fileOpisanie.readText(), type))
            res = arrayList[day - 1]
        }
        val titleArray = ArrayList<String>()
        val listRes = res.split("<strong>")
        var sb: String
        for (i in listRes.size - 1 downTo 0) {
            val text = listRes[i].replace("<!--image-->", "")
            if (text.trim() != "") {
                if (text.contains("Трапар", ignoreCase = true) || text.contains("Кандак", ignoreCase = true)) {
                    continue
                } else {
                    val t1 = text.indexOf("</strong>")
                    if (t1 != -1) {
                        sb = text.substring(0, t1)
                        titleArray.add(0, sb)
                    }
                }
            }
        }
        titleArray.forEachIndexed { index, text ->
            if (position == index) {
                title = text
                return@forEachIndexed
            }
        }
        return title
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
        return false
    }

    private inner class ItemAdapter(private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<DataImages, ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ListItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.itemLayout.setBackgroundResource(by.carkva_gazeta.malitounik.R.drawable.selector_default_list)
            view.root.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorDivider)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val imgFile = mItemList[position].file
            val myBitmap = resizeImage(BitmapFactory.decodeFile(imgFile.absolutePath))
            holder.mImageView.setImageBitmap(myBitmap)
            holder.mTextView.text = mItemList[position].title
            holder.itemView.tag = mItemList[position]
        }

        override fun getUniqueItemId(position: Int): Long {
            return mItemList[position].position.toLong()
        }

        private fun resizeImage(bitmap: Bitmap?): Bitmap? {
            bitmap?.let {
                var newHeight = it.height.toFloat()
                var newWidth = it.width.toFloat()
                val widthLinear = 500f
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
        }

        private inner class ViewHolder(itemView: ListItemImageBinding) : DragItemAdapter.ViewHolder(itemView.root, mGrabHandleId, mDragOnLongPress) {
            val mImageView = itemView.imageView
            val mTextView = itemView.text

            override fun onItemClicked(view: View?) {
                if (images[adapterPosition].size == 0L) {
                    val permissionCheck = ContextCompat.checkSelfPermission(this@SviatyiaImage, READ_EXTERNAL_STORAGE)
                    if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                        mPermissionResult.launch(READ_EXTERNAL_STORAGE)
                    } else {
                        val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(adapterPosition, false)
                        dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
                    }
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                if (images[adapterPosition].size == 0L) {
                    val permissionCheck = ContextCompat.checkSelfPermission(this@SviatyiaImage, READ_EXTERNAL_STORAGE)
                    if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                        mPermissionResult.launch(READ_EXTERNAL_STORAGE)
                    } else {
                        val dialogImageFileExplorer = DialogImageFileExplorer.getInstance(adapterPosition, false)
                        dialogImageFileExplorer.show(supportFragmentManager, "dialogImageFileExplorer")
                    }
                } else {
                    val contextMenu = DialogPiarlinyContextMenu.getInstance(adapterPosition, getString(by.carkva_gazeta.malitounik.R.string.sviatyia))
                    contextMenu.show(supportFragmentManager, "context_menu")
                }
                return true
            }
        }

        fun updateList(newMyNatatkiFiles: ArrayList<DataImages>) {
            val diffCallback = RecyclerViewDiffCallback(images, newMyNatatkiFiles)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            itemList = newMyNatatkiFiles
        }

        init {
            itemList = images
        }
    }

    private class RecyclerViewDiffCallback(private val oldArrayList: ArrayList<DataImages>, private val newArrayList: ArrayList<DataImages>) : DiffUtil.Callback() {
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

    private data class DataImages(var title: String, var size: Long, var file: File, val position: Long)
}