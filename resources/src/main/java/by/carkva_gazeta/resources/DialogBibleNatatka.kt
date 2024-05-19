package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.databinding.DialogEditviewDisplayBinding

class DialogBibleNatatka : DialogFragment() {
    private var redaktor = false
    private var position = 0
    private var semuxa = true
    private var novyzavet = false
    private var nov = "0"
    private var kniga = 0
    private var glava = BibleGlobalList.mListGlava
    private var stix = BibleGlobalList.bibleCopyList[0]
    private var bibletext = ""
    private lateinit var ad: AlertDialog.Builder
    private var dialogBibleNatatkaListiner: DialogBibleNatatkaListiner? = null
    private var _binding: DialogEditviewDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogBibleNatatkaListiner?.addNatatka()
    }

    interface DialogBibleNatatkaListiner {
        fun addNatatka()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            dialogBibleNatatkaListiner = try {
                context as DialogBibleNatatkaListiner
            } catch (e: ClassCastException) {
                throw ClassCastException("$context must implement DialogBibleNatatkaListiner")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semuxa = arguments?.getBoolean("semuxa") ?: true
        novyzavet = arguments?.getBoolean("novyzavet") ?: false
        kniga = arguments?.getInt("kniga") ?: 0
        glava = arguments?.getInt("glava") ?: BibleGlobalList.mListGlava
        stix = arguments?.getInt("stix") ?: BibleGlobalList.bibleCopyList[0]
        bibletext = arguments?.getString("bibletext") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            _binding = DialogEditviewDisplayBinding.inflate(LayoutInflater.from(fragmentActivity))
            val dzenNoch = (fragmentActivity as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(fragmentActivity, style)
            var editText = ""
            binding.title.setText(R.string.natatka_bersha_biblii)
            if (novyzavet)
                nov = "1"
            if (semuxa) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(nov) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSemuxa[i].list[5]
                        position = i
                        break
                    }
                }
            } else {
                for (i in BibleGlobalList.natatkiSinodal.indices) {
                    if (BibleGlobalList.natatkiSinodal[i].list[0].contains(nov) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSinodal[i].list[5]
                        position = i
                        break
                    }
                }
            }
            binding.content.setText(editText)
            binding.content.requestFocus()
            if (dzenNoch) {
                binding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorWhite))
                binding.content.setBackgroundResource(R.color.colorbackground_material_dark)
            } else {
                binding.content.setTextColor(ContextCompat.getColor(fragmentActivity, R.color.colorPrimary_text))
                binding.content.setBackgroundResource(R.color.colorWhite)
            }
            ad.setView(binding.root)
            ad.setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                if (semuxa) {
                    if (redaktor && BibleGlobalList.natatkiSemuxa.size > 0) {
                        if (binding.content.text.toString() == "") BibleGlobalList.natatkiSemuxa.removeAt(position)
                        else BibleGlobalList.natatkiSemuxa[position].list[5] = binding.content.text.toString()
                    } else {
                        if (binding.content.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(nov)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(binding.content.text.toString())
                            var maxIndex: Long = 0
                            BibleGlobalList.natatkiSemuxa.forEach {
                                if (maxIndex < it.id)
                                    maxIndex = it.id
                            }
                            maxIndex++
                            BibleGlobalList.natatkiSemuxa.add(0, BibleNatatkiData(maxIndex, temp))
                        }
                    }
                } else {
                    if (redaktor && BibleGlobalList.natatkiSinodal.size > 0) {
                        if (binding.content.text.toString() == "") BibleGlobalList.natatkiSinodal.removeAt(position)
                        else BibleGlobalList.natatkiSinodal[position].list[5] = binding.content.text.toString()
                    } else {
                        if (binding.content.text.toString() != "") {
                            val temp = ArrayList<String>()
                            temp.add(nov)
                            temp.add(kniga.toString())
                            temp.add(glava.toString())
                            temp.add(stix.toString())
                            temp.add(bibletext)
                            temp.add(binding.content.text.toString())
                            var maxIndex: Long = 0
                            BibleGlobalList.natatkiSinodal.forEach {
                                if (maxIndex < it.id)
                                    maxIndex = it.id
                            }
                            maxIndex++
                            BibleGlobalList.natatkiSinodal.add(0, BibleNatatkiData(maxIndex, temp))
                        }
                    }
                }
                val imm12 = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            ad.setNeutralButton(getString(R.string.delite)) { dialog: DialogInterface, _: Int ->
                if (semuxa && BibleGlobalList.natatkiSemuxa.size > 0) BibleGlobalList.natatkiSemuxa.removeAt(position)
                if (!semuxa && BibleGlobalList.natatkiSinodal.size > 0) BibleGlobalList.natatkiSinodal.removeAt(position)
                val imm12 = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            ad.setNegativeButton(R.string.cansel) { dialog: DialogInterface, _: Int ->
                val imm12 = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
        }
        return ad.create()
    }

    companion object {
        fun getInstance(semuxa: Boolean, novyzavet: Boolean, kniga: Int, glava: Int, stix: Int, bibletext: String): DialogBibleNatatka {
            val zametka = DialogBibleNatatka()
            val bundle = Bundle()
            bundle.putBoolean("semuxa", semuxa)
            bundle.putBoolean("novyzavet", novyzavet)
            bundle.putInt("kniga", kniga)
            bundle.putInt("glava", glava)
            bundle.putInt("stix", stix)
            bundle.putString("bibletext", bibletext)
            zametka.arguments = bundle
            return zametka
        }
    }
}