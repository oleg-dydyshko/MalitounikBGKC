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
import by.carkva_gazeta.admin.databinding.DialogTwoEditviewDisplayBinding

class DialogPasochnicaAHref : DialogFragment() {
    private var url = ""
    private var titleUrl = ""
    private var mListener: DialogPasochnicaAHrefListener? = null
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTwoEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogPasochnicaAHrefListener {
        fun setUrl(url: String, titleUrl: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPasochnicaAHrefListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogPasochnicaAHrefListener")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("url", binding.url.text.toString())
        outState.putString("titleUrl", binding.titleUrl.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString("url") ?: ""
        titleUrl = arguments?.getString("titleUrl") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTwoEditviewDisplayBinding.inflate(LayoutInflater.from(it))
            builder = AlertDialog.Builder(it, by.carkva_gazeta.malitounik.R.style.AlertDialogTheme)
            binding.title.text = getString(by.carkva_gazeta.malitounik.R.string.set_file_name)
            if (savedInstanceState != null) {
                binding.url.setText(savedInstanceState.getString("url"))
                binding.titleUrl.setText(savedInstanceState.getString("titleUrl"))
            } else {
                binding.url.setText(url)
                binding.titleUrl.setText(titleUrl)
            }
            binding.url.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
            binding.url.setBackgroundResource(by.carkva_gazeta.malitounik.R.color.colorWhite)
            binding.url.requestFocus()
            binding.titleUrl.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    setUrl()
                    dialog?.cancel()
                }
                false
            }
            binding.titleUrl.imeOptions = EditorInfo.IME_ACTION_GO
            builder.setNegativeButton(resources.getString(by.carkva_gazeta.malitounik.R.string.cansel)) { dialog: DialogInterface, _: Int ->
                val imm12 = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.url.windowToken, 0)
                dialog.cancel()
            }
            builder.setPositiveButton(resources.getString(by.carkva_gazeta.malitounik.R.string.ok)) { _: DialogInterface?, _: Int ->
                setUrl()
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }

    private fun setUrl() {
        val url = binding.url.text.toString()
        val titleUrl = binding.titleUrl.text.toString()
        mListener?.setUrl(url, titleUrl)
    }

    companion object {
        fun getInstance(url: String, titleUrl: String): DialogPasochnicaAHref {
            val instance = DialogPasochnicaAHref()
            val args = Bundle()
            args.putString("url", url)
            args.putString("titleUrl", titleUrl)
            instance.arguments = args
            return instance
        }
    }
}