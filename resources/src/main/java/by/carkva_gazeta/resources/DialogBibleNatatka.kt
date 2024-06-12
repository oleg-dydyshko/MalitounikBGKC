package by.carkva_gazeta.resources

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
    private var perevod = 1
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
        perevod = arguments?.getInt("perevod") ?: 1
        novyzavet = arguments?.getBoolean("novyzavet") ?: false
        kniga = arguments?.getInt("kniga") ?: 0
        glava = arguments?.getInt("glava") ?: BibleGlobalList.mListGlava
        stix = arguments?.getInt("stix") ?: BibleGlobalList.bibleCopyList[0]
        bibletext = arguments?.getString("bibletext") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { fragmentActivity ->
            _binding = DialogEditviewDisplayBinding.inflate(layoutInflater)
            val dzenNoch = (fragmentActivity as BaseActivity).getBaseDzenNoch()
            var style = R.style.AlertDialogTheme
            if (dzenNoch) style = R.style.AlertDialogThemeBlack
            ad = AlertDialog.Builder(fragmentActivity, style)
            var editText = ""
            binding.title.setText(R.string.natatka_bersha_biblii)
            if (novyzavet)
                nov = "1"
            if (perevod == 1) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(nov) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSemuxa[i].list[5]
                        position = i
                        break
                    }
                }
            }
            if (perevod == 2) {
                for (i in BibleGlobalList.natatkiSinodal.indices) {
                    if (BibleGlobalList.natatkiSinodal[i].list[0].contains(nov) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiSinodal[i].list[5]
                        position = i
                        break
                    }
                }
            }
            if (perevod == 3) {
                for (i in BibleGlobalList.natatkiBokuna.indices) {
                    if (BibleGlobalList.natatkiBokuna[i].list[0].contains(nov) && BibleGlobalList.natatkiBokuna[i].list[1].toInt() == kniga && BibleGlobalList.natatkiBokuna[i].list[2].toInt() == glava && BibleGlobalList.natatkiBokuna[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiBokuna[i].list[5]
                        position = i
                        break
                    }
                }
            }
            if (perevod == 4) {
                for (i in BibleGlobalList.natatkiCarniauski.indices) {
                    if (BibleGlobalList.natatkiCarniauski[i].list[0].contains(nov) && BibleGlobalList.natatkiCarniauski[i].list[1].toInt() == kniga && BibleGlobalList.natatkiCarniauski[i].list[2].toInt() == glava && BibleGlobalList.natatkiCarniauski[i].list[3].toInt() == stix) {
                        redaktor = true
                        editText = BibleGlobalList.natatkiCarniauski[i].list[5]
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
                if (perevod == 1) {
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
                }
                if (perevod == 2) {
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
                if (perevod == 3) {
                    if (redaktor && BibleGlobalList.natatkiBokuna.size > 0) {
                        if (binding.content.text.toString() == "") BibleGlobalList.natatkiBokuna.removeAt(position)
                        else BibleGlobalList.natatkiBokuna[position].list[5] = binding.content.text.toString()
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
                            BibleGlobalList.natatkiBokuna.forEach {
                                if (maxIndex < it.id)
                                    maxIndex = it.id
                            }
                            maxIndex++
                            BibleGlobalList.natatkiBokuna.add(0, BibleNatatkiData(maxIndex, temp))
                        }
                    }
                }
                if (perevod == 4) {
                    if (redaktor && BibleGlobalList.natatkiCarniauski.size > 0) {
                        if (binding.content.text.toString() == "") BibleGlobalList.natatkiCarniauski.removeAt(position)
                        else BibleGlobalList.natatkiCarniauski[position].list[5] = binding.content.text.toString()
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
                            BibleGlobalList.natatkiCarniauski.forEach {
                                if (maxIndex < it.id)
                                    maxIndex = it.id
                            }
                            maxIndex++
                            BibleGlobalList.natatkiCarniauski.add(0, BibleNatatkiData(maxIndex, temp))
                        }
                    }
                }
                val imm12 = fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm12.hideSoftInputFromWindow(binding.content.windowToken, 0)
                dialog.cancel()
            }
            ad.setNeutralButton(getString(R.string.delite)) { dialog: DialogInterface, _: Int ->
                if (perevod == 1 && BibleGlobalList.natatkiSemuxa.size > 0) BibleGlobalList.natatkiSemuxa.removeAt(position)
                if (perevod == 2 && BibleGlobalList.natatkiSinodal.size > 0) BibleGlobalList.natatkiSinodal.removeAt(position)
                if (perevod == 3 && BibleGlobalList.natatkiBokuna.size > 0) BibleGlobalList.natatkiBokuna.removeAt(position)
                if (perevod == 4 && BibleGlobalList.natatkiCarniauski.size > 0) BibleGlobalList.natatkiCarniauski.removeAt(position)
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
        fun getInstance(perevod: Int, novyzavet: Boolean, kniga: Int, glava: Int, stix: Int, bibletext: String): DialogBibleNatatka {
            val zametka = DialogBibleNatatka()
            val bundle = Bundle()
            bundle.putInt("perevod", perevod)
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