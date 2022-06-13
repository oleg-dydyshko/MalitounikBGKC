package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.CheckLoginBinding

class CheckLogin : DialogFragment() {
    private lateinit var mListener: CheckLoginListener
    private lateinit var alert: AlertDialog
    private var _binding: CheckLoginBinding? = null
    private val binding get() = _binding!!

    interface CheckLoginListener {
        fun onLogin()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as CheckLoginListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$activity must implement CheckLoginListener")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = CheckLoginBinding.inflate(LayoutInflater.from(it))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            alert = builder.create()
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            binding.ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_TOAST)
            if (dzenNoch) {
                binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.username.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
                binding.password.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.ok.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.ok.setBackgroundResource(R.drawable.selector_dialog_font_dark)
            }
            binding.ok.setOnClickListener {
                if (binding.username.text.toString().trim() == "Царква" && binding.password.text.toString().trim() == "Дворнікава63") {
                    mListener.onLogin()
                }
                dialog?.cancel()
            }
        }
        return alert
    }
}