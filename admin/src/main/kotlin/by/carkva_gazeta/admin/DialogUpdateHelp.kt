package by.carkva_gazeta.admin

import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.admin.databinding.AdminDialogEditviewDisplayBinding
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DialogUpdateHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: AdminDialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            _binding = AdminDialogEditviewDisplayBinding.inflate(LayoutInflater.from(fragmentActivity))
            val builder = AlertDialog.Builder(fragmentActivity, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update)
            val release = arguments?.getBoolean("release", false) ?: false
            val version = if (release) "release"
            else "beta"
            binding.content.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update_all, version)
            builder.setView(binding.root)
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update_ok)) { _: DialogInterface, _: Int ->
                val ver = binding.edittext.text.toString()
                if (ver != "") {
                    setViersionApp(ver, release)
                }
            }
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
            if (MainActivity.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.Main).launch {
                    //var updeteArrayText = mapOf<String, String>()
                    try {
                        val localFile = withContext(Dispatchers.IO) {
                            File.createTempFile("updateMalitounik", "json")
                        }
                        Malitounik.referens.child("/updateMalitounikBGKC.json").getFile(localFile).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val jsonFile = localFile.readText()
                                val gson = Gson()
                                val type = TypeToken.getParameterized(Map::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type
                                val updeteArrayText = gson.fromJson<Map<String, String>>(jsonFile, type)
                                if (release) binding.edittext.setText(updeteArrayText["release"])
                                else binding.edittext.setText(updeteArrayText["devel"])
                            } else {
                                MainActivity.toastView(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error))
                            }
                        }.await()
                    } catch (_: Throwable) {
                        MainActivity.toastView(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
            }
        }
        return alert
    }

    private fun setViersionApp(releaseCode: String, release: Boolean) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val localFile = withContext(Dispatchers.IO) {
                        File.createTempFile("updateMalitounik", "json")
                    }
                    Malitounik.referens.child("/updateMalitounikBGKC.json").getFile(localFile).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val jsonFile = localFile.readText()
                            val gson = Gson()
                            val type = TypeToken.getParameterized(MutableMap::class.java, TypeToken.getParameterized(String::class.java).type, TypeToken.getParameterized(String::class.java).type).type
                            val updeteArrayText = gson.fromJson<MutableMap<String, String>>(jsonFile, type)
                            if (release) {
                                updeteArrayText["release"] = releaseCode
                            } else {
                                updeteArrayText["devel"] = releaseCode
                            }
                            localFile.writer().use {
                                it.write(gson.toJson(updeteArrayText))
                            }
                        } else {
                            activity?.let {
                                MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                            }
                        }
                    }.await()
                    Malitounik.referens.child("/updateMalitounikBGKC.json").putFile(Uri.fromFile(localFile)).addOnCompleteListener { task ->
                        activity?.let {
                            if (task.isSuccessful) {
                                MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.save))
                            } else {
                                MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error))
                            }
                        }
                    }.await()
                } catch (e: Throwable) {
                    activity?.let {
                        MainActivity.toastView(it, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(release: Boolean): DialogUpdateHelp {
            val dialogSaveAsHelp = DialogUpdateHelp()
            val bundle = Bundle()
            bundle.putBoolean("release", release)
            dialogSaveAsHelp.arguments = bundle
            return dialogSaveAsHelp
        }
    }
}