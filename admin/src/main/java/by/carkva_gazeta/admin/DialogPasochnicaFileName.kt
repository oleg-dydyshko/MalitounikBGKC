package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding
import java.util.Calendar

class DialogPasochnicaFileName : DialogFragment() {
    private var oldFileName = ""
    private var isSite = false
    private var mListener: DialogPasochnicaFileNameListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogPasochnicaFileNameListener {
        fun setFileName(oldFileName: String, fileName: String, isSite: Boolean, saveAs: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPasochnicaFileNameListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPasochnicaFileNameListener")
            }
        }
    }

    fun vypraulenneFilename() {
        var fileNameOld = binding.content.text.toString()
        var fileName = getResourceFileName(fileNameOld)
        if (!fileName.contains(".php", true)) {
            fileName = fileName.replace("-", "_")
        }
        fileName = fileName.replace(" ", "_").lowercase()
        val t1 = fileNameOld.indexOf(")")
        var t2 = fileNameOld.lastIndexOf("/")
        val prefix = if (t2 != -1) {
            t2++
            fileNameOld.substring(0, t2)
        } else {
            t2 = if (t1 != -1) 1
            else 0
            ""
        }
        val mm = if (fileNameOld[t2].isDigit()) "mm_"
        else ""
        if (t1 != -1) fileNameOld = "($mm" + fileName + ") " + fileNameOld.substring(t1 + 2)
        if (prefix != "") fileNameOld = "$prefix$mm$fileName"
        binding.content.setText(fileNameOld)
        setFileName()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", binding.content.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oldFileName = arguments?.getString("oldFileName") ?: "new_file.html"
        isSite = arguments?.getBoolean("isSite") ?: false
    }

    override fun onResume() {
        super.onResume()
        val dialog = dialog as? AlertDialog
        val positiveButton = dialog?.getButton(Dialog.BUTTON_POSITIVE)
        positiveButton?.setOnClickListener {
            setFileName()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(layoutInflater)
            builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.set_file_name)
            val text = if (savedInstanceState != null) {
                binding.content.setText(savedInstanceState.getString("fileName"))
                savedInstanceState.getString("fileName") ?: "new_file.html"
            } else {
                binding.content.setText(oldFileName)
                oldFileName
            }
            val t2 = text.lastIndexOf(".")
            if (t2 != -1) {
                binding.content.setSelection(0, t2)
            }
            binding.content.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            binding.content.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            binding.content.requestFocus()
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    setFileName()
                }
                false
            }
            binding.content.imeOptions = EditorInfo.IME_ACTION_GO
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.ok), null)
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun setFileName() {
        val saveAs = arguments?.getBoolean("saveAs") ?: true
        var fileName = binding.content.text.toString()
        if (fileName == "") {
            val gc = Calendar.getInstance()
            val mun = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
            fileName = gc[Calendar.DATE].toString() + "_" + mun[gc[Calendar.MONTH]] + "_" + gc[Calendar.YEAR] + "_" + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
        } else if (saveAs) {
            var error = false
            val checkFileName = getResourceFileName(fileName)
            var t1 = checkFileName.indexOf(")")
            t1 = if (t1 != -1) 1
            else 0
            if (checkFileName[t1].isDigit()) error = true
            for (c in checkFileName) {
                if (c.isUpperCase()) error = true
            }
            if (error) {
                val dialog = DialogFileNameError()
                dialog.show(childFragmentManager, "DialogFileNameError")
                return
            }
        }
        mListener?.setFileName(oldFileName, fileName, isSite, saveAs)
        dialog?.cancel()
    }

    private fun getResourceFileName(fullResourceFileName: String): String {
        var checkFileName = fullResourceFileName
        val t2 = checkFileName.lastIndexOf("/")
        if (t2 != -1) {
            checkFileName = checkFileName.substring(t2 + 1)
        }
        val t1 = checkFileName.indexOf(")")
        if (t1 != -1) {
            checkFileName = checkFileName.substring(1, t1)
        }
        return checkFileName
    }

    companion object {
        fun getInstance(oldFileName: String, isSite: Boolean, saveAs: Boolean): DialogPasochnicaFileName {
            val instance = DialogPasochnicaFileName()
            val args = Bundle()
            args.putString("oldFileName", oldFileName)
            args.putBoolean("isSite", isSite)
            args.putBoolean("saveAs", saveAs)
            instance.arguments = args
            return instance
        }
    }
}