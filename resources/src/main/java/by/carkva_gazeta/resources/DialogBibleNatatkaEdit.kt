package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding

class DialogBibleNatatkaEdit : DialogFragment() {
    private var edit: BibleNatatkaEditlistiner? = null
    private var semuxa = 0
    private var position = 0
    private lateinit var ad: AlertDialog.Builder
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal interface BibleNatatkaEditlistiner {
        fun setEdit()
        fun editCancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            edit = try {
                context as BibleNatatkaEditlistiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement BibleNatatkaEditlistiner")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        edit?.editCancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semuxa = arguments?.getInt("semuxa") ?: 0
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            _binding = DialogEditviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(it, style)
            var editText = ""
            binding.title.setText(R.string.natatka_bersha_biblii)
            if (semuxa == 1) {
                editText = BibleGlobalList.natatkiSemuxa[position].list[5]
            }
            if (semuxa == 2) {
                editText = BibleGlobalList.natatkiSinodal[position].list[5]
            }
            binding.content.setText(editText)
            binding.content.requestFocus()
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { _: DialogInterface, _: Int ->
                if (semuxa == 1) {
                    if (binding.content.text.toString() == "") BibleGlobalList.natatkiSemuxa.removeAt(position) else BibleGlobalList.natatkiSemuxa[position].list[5] = binding.content.text.toString()
                }
                if (semuxa == 2) {
                    if (binding.content.text.toString() == "") BibleGlobalList.natatkiSinodal.removeAt(position) else BibleGlobalList.natatkiSinodal[position].list[5] = binding.content.text.toString()
                }
                it.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                edit?.setEdit()
            }
            ad.setNeutralButton(getString(R.string.delite)) { _: DialogInterface, _: Int ->
                if (semuxa == 1 && BibleGlobalList.natatkiSemuxa.size > 0) BibleGlobalList.natatkiSemuxa.removeAt(position)
                if (semuxa == 2 && BibleGlobalList.natatkiSinodal.size > 0) BibleGlobalList.natatkiSinodal.removeAt(position)
                it.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                edit?.setEdit()
            }
            ad.setNegativeButton(R.string.cansel) { _: DialogInterface, _: Int ->
                it.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                edit?.editCancel()
            }
        }
        return ad.create()
    }

    companion object {
        fun getInstance(semuxa: Int, position: Int): DialogBibleNatatkaEdit {
            val zametka = DialogBibleNatatkaEdit()
            val bundle = Bundle()
            bundle.putInt("semuxa", semuxa)
            bundle.putInt("position", position)
            zametka.arguments = bundle
            return zametka
        }
    }
}