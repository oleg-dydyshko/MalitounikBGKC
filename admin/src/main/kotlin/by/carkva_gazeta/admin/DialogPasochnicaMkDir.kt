package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DialogPasochnicaMkDir : DialogFragment() {
    private var mListener: DialogPasochnicaMkDirListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var dir = ""
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogPasochnicaMkDirListener {
        fun setDir(oldDir: String)
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
            dir = arguments?.getString("dir", "")?: ""
            binding.content.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    sendMkDirPostRequest(dir)
                    dialog?.cancel()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int ->
                sendMkDirPostRequest(dir)
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun sendMkDirPostRequest(dir: String) {
        val dirName = binding.content.text.toString()
        if (dirName != "") {
            activity?.let {
                if (MainActivity.isNetworkAvailable(it)) {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            var reqParam = URLEncoder.encode("mkdir", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")
                            reqParam += "&" + URLEncoder.encode("dir", "UTF-8") + "=" + URLEncoder.encode("$dir/$dirName", "UTF-8")
                            val mURL = URL("https://carkva-gazeta.by/admin/piasochnica.php")
                            with(mURL.openConnection() as HttpURLConnection) {
                                requestMethod = "POST"
                                val wr = OutputStreamWriter(outputStream)
                                wr.write(reqParam)
                                wr.flush()
                                inputStream
                            }
                        }
                        mListener?.setDir(dir)
                    }
                }
            }
        }
    }

    companion object {
        fun getInstance(dir: String): DialogPasochnicaMkDir {
            val instance = DialogPasochnicaMkDir()
            val args = Bundle()
            args.putString("dir", dir)
            instance.arguments = args
            return instance
        }
    }
}