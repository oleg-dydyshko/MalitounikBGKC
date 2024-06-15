package by.carkva_gazeta.malitounik

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.databinding.DialogPerevodBibliiBinding

class DialogPerevodBiblii : DialogFragment() {
    private var dzenNoch = false
    private lateinit var alert: AlertDialog
    private var _binding: DialogPerevodBibliiBinding? = null
    private val binding get() = _binding!!
    private var mListener: DialogPerevodBibliiListener? = null
    private var isMaranata = true

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            MainActivity.dialogVisable = true
            _binding = DialogPerevodBibliiBinding.inflate(layoutInflater)
            val k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            val builder = AlertDialog.Builder(it, style)
            isMaranata = arguments?.getBoolean("isMaranata", true) ?: true
            val perevod = if (isMaranata) k.getString("perevod", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
            else k.getString("perevodChytanne", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
            if (!isMaranata) binding.sinoidal.visibility = View.GONE
            when (perevod) {
                DialogVybranoeBibleList.PEREVODSEMUXI -> {
                    binding.semuxa.isChecked = true
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = false
                }
                DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = true
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = false
                }
                DialogVybranoeBibleList.PEREVODBOKUNA -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = true
                    binding.carniauski.isChecked = false
                }
                DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                    binding.semuxa.isChecked = false
                    binding.sinoidal.isChecked = false
                    binding.bokuna.isChecked = false
                    binding.carniauski.isChecked = true
                }
            }
            var newperevod = perevod
            binding.perevodGrupBible.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
                when (checkedId) {
                    R.id.semuxa -> {
                        newperevod = DialogVybranoeBibleList.PEREVODSEMUXI
                    }
                    R.id.sinoidal -> {
                        newperevod = DialogVybranoeBibleList.PEREVODSINOIDAL
                    }
                    R.id.bokuna -> {
                        newperevod = DialogVybranoeBibleList.PEREVODBOKUNA
                    }
                    R.id.carniauski -> {
                        newperevod = DialogVybranoeBibleList.PEREVODCARNIAUSKI
                    }
                }
            }
            binding.title.text = resources.getString(R.string.perevod)
            builder.setView(binding.root)
            builder.setPositiveButton(getString(R.string.ok)) { _: DialogInterface, _: Int ->
                mListener?.setPerevod(newperevod)
            }
            builder.setNegativeButton(getString(R.string.cansel)) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert = builder.create()
        }
        return alert
    }

    companion object {
        fun getInstance(isMaranata: Boolean): DialogPerevodBiblii {
            val dialogPerevodBiblii = DialogPerevodBiblii()
            val bundle = Bundle()
            bundle.putBoolean("isMaranata", isMaranata)
            dialogPerevodBiblii.arguments = bundle
            return dialogPerevodBiblii
        }
    }
}