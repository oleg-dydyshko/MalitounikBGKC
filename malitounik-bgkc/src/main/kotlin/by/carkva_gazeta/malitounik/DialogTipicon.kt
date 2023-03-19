package by.carkva_gazeta.malitounik

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
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
            val dvunaIVial = SpannableString(getString(R.string.dvuna_i_vial))
            dvunaIVial.setSpan(StyleSpan(Typeface.BOLD), 0, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.textView8.text = dvunaIVial
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
            builder.setPositiveButton(it.getString(R.string.close)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            alert = builder.create()
        }
        return alert
    }
}