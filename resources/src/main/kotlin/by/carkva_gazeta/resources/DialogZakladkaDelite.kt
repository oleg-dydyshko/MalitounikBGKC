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
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.DialogTextviewDisplayBinding

class DialogZakladkaDelite : DialogFragment() {
    private var delite: ZakladkaDeliteListiner? = null
    private var position = 0
    private var name = ""
    private var semuxa = 0
    private var zakladka = false
    private lateinit var builder: AlertDialog.Builder
    private var _binding: DialogTextviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position")?: 0
        name = arguments?.getString("name")?: ""
        semuxa = arguments?.getInt("semuxa")?: 0
        zakladka = arguments?.getBoolean("zakladka")?: false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            delite = try {
                context as ZakladkaDeliteListiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement ZakladkaDeliteListiner")
            }
        }
    }

    internal interface ZakladkaDeliteListiner {
        fun zakladkadiliteItem(position: Int, semuxa: Int)
        fun zakladkadiliteItemCancel()
        fun natatkidiliteItem(position: Int, semuxa: Int)
    }

    override fun onDestroy() {
        super.onDestroy()
        delite?.zakladkadiliteItemCancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogTextviewDisplayBinding.inflate(LayoutInflater.from(it))
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary_black))
            else binding.title.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))
            binding.title.text = resources.getString(R.string.remove)
            if (zakladka) binding.content.text = resources.getString(R.string.delite_natatki_i_zakladki, getString(R.string.zakladki_bible2), name) else binding.content.text = resources.getString(R.string.delite_natatki_i_zakladki, getString(R.string.natatki_biblii2), name)
            binding.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorWhite)) 
            else binding.content.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
            if (zakladka) {
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> delite?.zakladkadiliteItem(position, semuxa) }
            } else {
                builder.setPositiveButton(resources.getText(R.string.ok)) { _: DialogInterface?, _: Int -> delite?.natatkidiliteItem(position, semuxa) }
            }
            builder.setNegativeButton(resources.getString(R.string.cansel)) { _: DialogInterface, _: Int -> delite?.zakladkadiliteItemCancel() }
            builder.setView(binding.root)
        }
        return builder.create()
    }

    companion object {
        fun getInstance(position: Int, name: String?, semuxa: Int, zakladka: Boolean): DialogZakladkaDelite {
            val dialogDelite = DialogZakladkaDelite()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("name", name)
            bundle.putInt("semuxa", semuxa)
            bundle.putBoolean("zakladka", zakladka)
            dialogDelite.arguments = bundle
            return dialogDelite
        }
    }
}