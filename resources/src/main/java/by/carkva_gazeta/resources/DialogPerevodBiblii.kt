package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.resources.databinding.DialogPerevodBibliiBinding

class DialogPerevodBiblii : DialogFragment() {
    private var dzenNoch = false
    private lateinit var alert: AlertDialog
    private var _binding: DialogPerevodBibliiBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogPerevodBibliiListener? = null
    private var isSinoidal = true

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogPerevodBibliiListener {
        fun setPerevod(perevod: String)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        MainActivity.dialogVisable = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mListener = try {
                context as DialogPerevodBibliiListener
            } catch (e: ClassCastException) {
                throw ClassCastException(activity.toString() + " must implement DialogPerevodBibliiListener")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0F)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun errorView(isError: Boolean) {
        if (isError) binding.error.visibility = View.VISIBLE
        else binding.error.visibility = View.GONE
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            _binding = DialogPerevodBibliiBinding.inflate(layoutInflater)
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = by.carkva_gazeta.malitounik.R.style.AlertDialogTheme
            if (dzenNoch) style = by.carkva_gazeta.malitounik.R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            if (dzenNoch) binding.error.setTextColor(ContextCompat.getColor(it, by.carkva_gazeta.malitounik.R.color.colorPrimary_black))
            isSinoidal = arguments?.getBoolean("isSinoidal", true) ?: true
            val perevod = arguments?.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
            val sinoidal = k.getInt("sinoidal", 0)
            if (sinoidal == 0 || !isSinoidal) binding.sinoidal.visibility = View.GONE
            val isNadsan = arguments?.getBoolean("isNadsan", false) ?: false
            if (!isNadsan) binding.nadsan.visibility = View.GONE
            when (perevod) {
                DialogVybranoeBibleList.PEREVODSEMUXI -> {
                    binding.semuxa.isChecked = true
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = false
                    binding.nadsan.isChecked = false
                }
                DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = true
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = false
                    binding.nadsan.isChecked = false
                }
                DialogVybranoeBibleList.PEREVODBOKUNA -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = true
                    binding.carniauski.isChecked = false
                    binding.nadsan.isChecked = false
                }
                DialogVybranoeBibleList.PEREVODNADSAN -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = false
                    binding.nadsan.isChecked = true
                }
                DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = true
                    binding.nadsan.isChecked = false
                }
            }
            binding.perevodGrupBible.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
                when (checkedId) {
                    R.id.semuxa -> {
                        mListener?.setPerevod(DialogVybranoeBibleList.PEREVODSEMUXI)
                    }
                    R.id.sinoidal -> {
                        mListener?.setPerevod(DialogVybranoeBibleList.PEREVODSINOIDAL)
                    }
                    R.id.bokuna -> {
                        mListener?.setPerevod(DialogVybranoeBibleList.PEREVODBOKUNA)
                    }
                    R.id.nadsan -> {
                        mListener?.setPerevod(DialogVybranoeBibleList.PEREVODNADSAN)
                    }
                    R.id.carniauski -> {
                        mListener?.setPerevod(DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    }
                }
            }
            binding.title.text = resources.getString(by.carkva_gazeta.malitounik.R.string.perevod)
            builder.setView(binding.root)
            builder.setPositiveButton(getString(by.carkva_gazeta.malitounik.R.string.ok)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(isSinoidal: Boolean, isNadsan: Boolean, perevod: String): DialogPerevodBiblii {
            val dialogPerevodBiblii = DialogPerevodBiblii()
            val bundle = Bundle()
            bundle.putBoolean("isSinoidal", isSinoidal)
            bundle.putBoolean("isNadsan", isNadsan)
            bundle.putString("perevod", perevod)
            dialogPerevodBiblii.arguments = bundle
            return dialogPerevodBiblii
        }
    }
}
