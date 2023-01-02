package by.carkva_gazeta.admin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding
import java.util.*

class DialogPasochnicaFileName : DialogFragment() {
    private var oldFileName = ""
    private var isSite = false
    private var mListener: DialogPasochnicaFileNameListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!
    private val textWatcher = object : TextWatcher {
        private var editPosition = 0
        private var check = 0
        private var editch = true

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            editch = count != after
            check = after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            editPosition = start + count
        }

        override fun afterTextChanged(s: Editable?) {
            if (editch) {
                var edit = s.toString()
                edit = edit.replace("-", "_")
                edit = edit.replace(" ", "_").lowercase()
                if (check != 0) {
                    binding.content.removeTextChangedListener(this)
                    binding.content.setText(edit)
                    binding.content.setSelection(editPosition)
                    binding.content.addTextChangedListener(this)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogPasochnicaFileNameListener {
        fun setFileName(oldFileName: String, fileName: String, isSite: Boolean)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fileName", binding.content.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oldFileName = arguments?.getString("oldFileName") ?: "new_file.html"
        isSite = arguments?.getBoolean("isSite") ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.set_file_name)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
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
            binding.content.addTextChangedListener(textWatcher)
            binding.content.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    setFileName()
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
                setFileName()
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun setFileName() {
        var fileName = binding.content.text.toString()
        if (fileName == "") {
            val gc = Calendar.getInstance()
            val mun = resources.getStringArray(by.carkva_gazeta.malitounik.R.array.meciac_smoll)
            fileName = gc[Calendar.DATE].toString() + "_" + mun[gc[Calendar.MONTH]] + "_" + gc[Calendar.YEAR] + "_" + gc[Calendar.HOUR_OF_DAY] + ":" + gc[Calendar.MINUTE]
        }
        mListener?.setFileName(oldFileName, fileName, isSite)
    }

    companion object {
        fun getInstance(oldFileName: String, isSite: Boolean): DialogPasochnicaFileName {
            val instance = DialogPasochnicaFileName()
            val args = Bundle()
            args.putString("oldFileName", oldFileName)
            args.putBoolean("isSite", isSite)
            instance.arguments = args
            return instance
        }
    }
}