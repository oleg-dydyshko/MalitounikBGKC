package by.carkva_gazeta.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.hardware.SensorEvent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import by.carkva_gazeta.admin.databinding.AdminSviatyiaImageBinding
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.ListItemImageBinding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import com.woxthebox.draglistview.swipe.ListSwipeHelper
import com.woxthebox.draglistview.swipe.ListSwipeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class SviatyiaImage : BaseActivity(), DialogDeliteImage.DialogDeliteListener, DialogPiarlinyContextMenu.DialogPiarlinyContextMenuListener {

    private lateinit var binding: AdminSviatyiaImageBinding
    private lateinit var adapter: ItemAdapter
    private var resetTollbarJob: Job? = null
    private var mLastClickTime: Long = 0
    private var mun = 1
    private var day = 1
    private val images = ArrayList<DataImages>()
    private val arrayListIcon = ArrayList<StorageReference>()
    private var position = 0
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageUri = it.data?.data
            imageUri?.let { image ->
                val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(contentResolver, image)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                fileUpload(bitmap)
            }
        }
        binding.dragListView.resetSwipedViews(null)
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun setMyTheme() {
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.installActivity(context)
    }

    override fun onDialogEditClick(position: Int) {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        this.position = position
        mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
    }

    override fun onDialogDeliteClick(position: Int, name: String) {
        val dialog = DialogDeliteImage.getInstance(position, images[position].file.absolutePath)
        dialog.show(supportFragmentManager, "DialogDeliteImage")
    }

    override fun imageFileDeliteCancel() {
        binding.dragListView.resetSwipedViews(null)
    }

    override fun imageFileDelite(position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar2.visibility = View.VISIBLE
            val file = images[position].file
            Malitounik.referens.child("/chytanne/icons/" + file.name).delete().await()
            val imageFile = File("$filesDir/icons/" + file.name)
            if (imageFile.exists()) {
                imageFile.delete()
            }
            images[position].file = File("")
            images[position].size = 0L
            adapter.updateList(images)
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
        binding = AdminSviatyiaImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val c = Calendar.getInstance()
        mun = intent.extras?.getInt("mun", c[Calendar.MONTH] + 1) ?: (c[Calendar.MONTH] + 1)
        day = intent.extras?.getInt("day", c[Calendar.DATE]) ?: c[Calendar.DATE]
        CoroutineScope(Dispatchers.Main).launch {
            val list = Malitounik.referens.child("/chytanne/icons").list(1000).await()
            arrayListIcon.addAll(list.items)
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
                    val intent = Intent()
                    intent.type = "*/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                    position = pos
                    mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
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
                            Malitounik.referens.child("/chytanne/icons/s_${day}_${mun}${posItems}.jpg").putFile(Uri.fromFile(images[i].file)).await()
                            val file = File("$filesDir/icons/s_${day}_${mun}${posItems}.jpg")
                            arraytemp.add(DataImages(getSviatyia(toPosition), file.length(), file, images[i].position))
                        } else {
                            arraytemp.add(DataImages(getSviatyia(toPosition), 0, File(""), images[i].position))
                            if (images[i].size != 0L) {
                                Malitounik.referens.child("/chytanne/icons/s_${day}_${mun}${posItems}.jpg").delete().await()
                            }
                        }
                    }
                    images.clear()
                    images.addAll(arraytemp)
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
        binding.titleToolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_DEFAULT)
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

    private fun fileUpload(bitmap: Bitmap?) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar2.visibility = View.VISIBLE
                val localFile = File("$filesDir/cache/cache.txt")
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
                        Malitounik.referens.child("/chytanne/icons/s_${day}_${mun}.jpg").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                            val file = File("$filesDir/icons/s_${day}_${mun}.jpg")
                            localFile.copyTo(file, true)
                            images[position].file = file
                            images[position].size = file.length()
                        }.await()
                    } else {
                        Malitounik.referens.child("/chytanne/icons/s_${day}_${mun}_${position + 1}.jpg").putFile(Uri.fromFile(localFile)).addOnSuccessListener {
                            val file = File("$filesDir/icons/s_${day}_${mun}_${position + 1}.jpg")
                            localFile.copyTo(file, true)
                            images[position].file = file
                            images[position].size = file.length()
                        }.await()
                    }
                    adapter.updateList(images)
                }
                binding.progressBar2.visibility = View.GONE
            }
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
                if (it.name.contains("s_${day}_${mun}.") || it.name.contains("s_${day}_${mun}_")) {
                    val fileIcon = File("$filesDir/icons/" + it.name)
                    it.getFile(fileIcon).addOnFailureListener {
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
        val list = Malitounik.referens.child("/chytanne/sviatyja").list(12).await()
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
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type
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
            return mItemList[position].position
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
                if (images[bindingAdapterPosition].size == 0L) {
                    val intent = Intent()
                    intent.type = "*/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                    this@SviatyiaImage.position = bindingAdapterPosition
                    mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
                }
            }

            override fun onItemLongClicked(view: View): Boolean {
                if (images[bindingAdapterPosition].size == 0L) {
                    val intent = Intent()
                    intent.type = "*/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                    this@SviatyiaImage.position = bindingAdapterPosition
                    mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
                } else {
                    val contextMenu = DialogPiarlinyContextMenu.getInstance(bindingAdapterPosition, getString(by.carkva_gazeta.malitounik.R.string.sviatyia))
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