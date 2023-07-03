package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import java.io.File

class DialogUpdateHelp : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var binding: AdminDialogEditviewDisplayBinding? = null
    private var updateHelpJob: Job? = null
    private var mListener: DialogUpdateHelpListener? = null

    interface DialogUpdateHelpListener {
        fun setViersionApp(releaseCode: String, release: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogUpdateHelpListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement DialogUpdateHelpListener")
            }
        }
    }

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
                        mListener?.setViersionApp(ver, release)
                    }
                }
                builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                alert = builder.create()
                if (MainActivity.isNetworkAvailable()) {
                    updateHelpJob = CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val localFile = File("${fragmentActivity.filesDir}/cache/cache.txt")
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