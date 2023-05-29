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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DialogUpdateHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var binding: AdminDialogEditviewDisplayBinding? = null
    private var updateHelpJob: Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPause() {
        super.onPause()
        updateHelpJob?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            val builder = AlertDialog.Builder(fragmentActivity, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding = AdminDialogEditviewDisplayBinding.inflate(LayoutInflater.from(fragmentActivity))
            binding?.let { displayBinding ->
                displayBinding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update)
                val release = arguments?.getBoolean("release", false) ?: false
                val version = if (release) "release"
                else "beta"
                displayBinding.content.text = resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update_all, version)
                builder.setView(displayBinding.root)
                builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.admin_update_ok)) { _: DialogInterface, _: Int ->
                    val ver = displayBinding.edittext.text.toString()
                    if (ver != "") {
                        setViersionApp(ver, release)
                    }
                }
                builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                alert = builder.create()
                if (MainActivity.isNetworkAvailable()) {
                    updateHelpJob = CoroutineScope(Dispatchers.Main).launch {
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
                                    if (release) displayBinding.edittext.setText(updeteArrayText["release"])
                                    else displayBinding.edittext.setText(updeteArrayText["devel"])
                                } else {
                                    MainActivity.toastView(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error))
                                }
                            }.await()
                            localFile.delete()
                        } catch (_: Throwable) {
                            MainActivity.toastView(fragmentActivity, getString(by.carkva_gazeta.malitounik.R.string.error_ch2))
                        }
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
                    localFile.delete()
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