package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogDeliteAllZakladkiINatatki : DialogFragment() {
    private var mListener: DialogDeliteAllZakladkiINatatkiListener? = null
    private var zakladkaAlboNatatka: String = ""
    private var semuxa = 0
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface DialogDeliteAllZakladkiINatatkiListener {
        fun fileAllNatatkiAlboZakladki(semuxa: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogDeliteAllZakladkiINatatkiListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement Dialog_delite_all_zakladki_i_natatki_Listener")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zakladkaAlboNatatka = arguments?.getString("zakladka_albo_natatka")?: ""
        semuxa = arguments?.getInt("semuxa")?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.Trash).uppercase()
            binding.content.text = getString(R.string.delite_all_natatki_i_zakladki, zakladkaAlboNatatka)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite))
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> mListener?.fileAllNatatkiAlboZakladki(semuxa) }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.setView(binding.root)
        }
        return builder.create()
    }

    companion object {
        fun getInstance(zakladka_albo_natatka: String?, semuxa: Int): DialogDeliteAllZakladkiINatatki {
            val dialogDelite = DialogDeliteAllZakladkiINatatki()
            val bundle = Bundle()
            bundle.putString("zakladka_albo_natatka", zakladka_albo_natatka)
            bundle.putInt("semuxa", semuxa)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}