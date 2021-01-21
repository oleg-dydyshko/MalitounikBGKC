package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.TipiconBinding

class DialogTipicon : DialogFragment() {
    private lateinit var alert: AlertDialog
    private var _binding: TipiconBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val tipicon = arguments?.getInt("tipicon") ?: 0
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            _binding = TipiconBinding.inflate(LayoutInflater.from(it))
            binding.line1.visibility = View.GONE
            if (tipicon == 1) binding.t7.visibility = View.VISIBLE else binding.t7.visibility = View.GONE
            if (tipicon == 2) binding.t5.visibility = View.VISIBLE else binding.t5.visibility = View.GONE
            if (tipicon == 3) binding.t6.visibility = View.VISIBLE else binding.t6.visibility = View.GONE
            if (tipicon == 4) binding.t8.visibility = View.VISIBLE else binding.t8.visibility = View.GONE
            if (tipicon == 5) binding.t9.visibility = View.VISIBLE else binding.t9.visibility = View.GONE
            binding.textView1.visibility = View.GONE
            binding.textView2.visibility = View.GONE
            binding.textView3.visibility = View.GONE
            binding.textView4.visibility = View.GONE
            binding.textView5.visibility = View.GONE
            binding.textView6.visibility = View.GONE
            if (tipicon == 0) {
                binding.line1.visibility = View.VISIBLE
                binding.t5.visibility = View.VISIBLE
                binding.t6.visibility = View.VISIBLE
                binding.t7.visibility = View.VISIBLE
                binding.t8.visibility = View.VISIBLE
                binding.t9.visibility = View.VISIBLE
                binding.textView1.visibility = View.VISIBLE
                binding.textView2.visibility = View.VISIBLE
                binding.textView3.visibility = View.VISIBLE
                binding.textView4.visibility = View.VISIBLE
                binding.textView5.visibility = View.VISIBLE
                binding.textView6.visibility = View.VISIBLE
            } else {
                binding.textView7.setPadding(0,0,0,0)
            }
            val dvunaIVial = SpannableString(getString(R.string.dvuna_i_vial))
            dvunaIVial.setSpan(StyleSpan(Typeface.BOLD), 0, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textView8.text = dvunaIVial
            binding.textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView6.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView7.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView8.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView9.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView10.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView11.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView12.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            binding.textView13.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                binding.imageView14.setImageResource(R.drawable.znaki_ttk_whate)
                binding.textView5.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textView13.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textView1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textView7.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textView8.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.textView2.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                binding.textView4.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                binding.textView6.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                binding.line1.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.line2.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
                binding.image1.setImageResource(R.drawable.znaki_krest_v_kruge_black)
                binding.image2.setImageResource(R.drawable.znaki_krest_v_polukruge_black)
                binding.image3.setImageResource(R.drawable.znaki_krest_black)
                binding.image4.setImageResource(R.drawable.znaki_ttk_black_black)
            }
            val builder = AlertDialog.Builder(it, style)
            builder.setView(binding.root)
            builder.setPositiveButton(it.getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(tipicon: Int): DialogTipicon {
            val dialogTipicon = DialogTipicon()
            val bundle = Bundle()
            bundle.putInt("tipicon", tipicon)
            dialogTipicon.arguments = bundle
            return dialogTipicon
        }
    }
}