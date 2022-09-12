package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
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
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            _binding = TipiconBinding.inflate(LayoutInflater.from(it))
            binding.textView3.text = getString(R.string.Strogi_post).replace("\n", " ")
            binding.textView4.text = getString(R.string.No_post).replace("\n", " ")
            binding.textView7.setPadding(0,0,0,0)
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
}