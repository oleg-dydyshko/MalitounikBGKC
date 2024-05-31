package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.DialogImageviewEditDisplayBinding
import java.io.File

class DialogEditImage : DialogFragment() {
    private var mListener: DialogEditImageListener? = null
    private var path = ""
    private lateinit var alert: AlertDialog
    private var _binding: DialogImageviewEditDisplayBinding? = null
    private val binding get() = _binding!!
    private var bitmap: Bitmap? = null
    private val mActivityResultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageUri = it.data?.data
            imageUri?.let { image ->
                bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, image)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                }
                binding.imageView2.setImageBitmap(bitmap)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogEditImageListener {
        fun imageFileEdit(bitmap: Bitmap?, opisanie: String)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = arguments?.getString("path") ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogEditImageListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogEditImageListener")
            }
        }
    }

    private fun getImage() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        mActivityResultFile.launch(Intent.createChooser(intent, getString(by.carkva_gazeta.malitounik.R.string.vybrac_file)))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogImageviewEditDisplayBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_opisanne_icon)
            val bitmapCreate = BitmapFactory.decodeFile(path)
            if (bitmapCreate == null) getImage()
            binding.imageView2.setImageBitmap(bitmapCreate)
            binding.imageView2.setOnClickListener {
                getImage()
            }
            val t1 = path.lastIndexOf("/")
            val name = path.substring(t1 + 1)
            val t3 = name.lastIndexOf(".")
            val fileNameT = name.substring(0, t3) + ".txt"
            val file = File("${it.filesDir}/iconsApisanne/$fileNameT")
            if (file.exists()) {
                binding.opisanie.setText(file.readText())
            }
            builder.setPositiveButton(resources.getText(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.imageFileEdit(bitmap, binding.opisanie.text.toString()) }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(path: String): DialogEditImage {
            val dialogDelite = DialogEditImage()
            val bundle = Bundle()
            bundle.putString("path", path)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}