package by.carkva_gazeta.admin

import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DialogLogView : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!
    private val storage: FirebaseStorage
        get() = Firebase.storage
    private val referens: StorageReference
        get() = storage.reference

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(Malitounik.applicationContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val ad = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = getString(R.string.log)
            CoroutineScope(Dispatchers.Main).launch {
                val localFile = withContext(Dispatchers.IO) {
                    File.createTempFile("log", "txt")
                }
                referens.child("/admin/log.txt").getFile(localFile).await()
                binding.content.text = localFile.readText()
            }
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            ad.setNeutralButton(getString(R.string.clear_log)) { _: DialogInterface, _: Int ->
                clearLog()
            }
            alert = ad.create()
        }
        return alert
    }

    private fun clearLog() {
        CoroutineScope(Dispatchers.Main).launch {
            val localFile = withContext(Dispatchers.IO) {
                File.createTempFile("log", "txt")
            }
            localFile.writer().use {
                it.write("")
            }
            referens.child("/admin/log.txt").putFile(Uri.fromFile(localFile)).await()
        }
    }
}