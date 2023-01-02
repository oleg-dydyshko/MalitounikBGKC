package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DialogPasochnicaMkDir : DialogFragment() {
    private var mListener: DialogPasochnicaMkDirListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var dir = ""
    private var oldName = ""
    private var newName = ""
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogPasochnicaMkDirListener {
        fun setDir()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPasochnicaMkDirListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPasochnicaMkDirListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", binding.content.text.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.set_file_dir)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (savedInstanceState != null) {
                binding.content.setText(savedInstanceState.getString("fileName"))
            }
            dir = arguments?.getString("dir", "") ?: ""
            oldName = arguments?.getString("oldName", "") ?: ""
            newName = arguments?.getString("newName", "") ?: ""
            binding.content.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendMkDirPostRequest()
                    dialog?.cancel()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int ->
                sendMkDirPostRequest()
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun sendMkDirPostRequest() {
        val dirName = binding.content.text.toString()
        if (dirName != "") {
            activity?.let {
                if (MainActivity.isNetworkAvailable()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            FirebaseApp.initializeApp(it)
                            val storage = Firebase.storage
                            val referens = storage.reference
                            val localFile = withContext(Dispatchers.IO) {
                                File.createTempFile("mkdir", "html")
                            }
                            referens.child("/admin/piasochnica/$oldName").getFile(localFile).await()
                            referens.child("/$dir/$dirName/$newName").putFile(Uri.fromFile(localFile)).await()
                        } catch (e: Throwable) {
                            activity?.let {
                                MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                            }
                        }
                        mListener?.setDir()
                    }
                }
            }
        }
    }

    companion object {
        fun getInstance(dir: String, oldName: String, newName: String): DialogPasochnicaMkDir {
            val instance = DialogPasochnicaMkDir()
            val args = Bundle()
            args.putString("dir", dir)
            args.putString("oldName", oldName)
            args.putString("newName", newName)
            instance.arguments = args
            return instance
        }
    }
}