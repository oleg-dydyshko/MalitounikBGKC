package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.MenuBibleBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MenuBibleSinoidal : Fragment() {
    private var mLastClickTime: Long = 0
    private var _binding: MenuBibleBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.let {
            loadNatatkiZakladkiSinodal(it)
        }
        _binding = MenuBibleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? BaseActivity)?.let { activity ->
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            binding.myBible.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val arrayListVybranoe = ArrayList<VybranoeBibliaData>()
                val bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
                if (bibleVybranoe != "") {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    arrayListVybranoe.addAll(gson.fromJson(bibleVybranoe, type))
                }
                if (bibleVybranoe == "" || arrayListVybranoe.isEmpty()) {
                    val dialogBibleVybranoeError = DialogBibleVybranoeError()
                    dialogBibleVybranoeError.show(parentFragmentManager, "dialogBibleVybranoeError")
                } else {
                    DialogVybranoeBibleList.biblia = "2"
                    val dialogVybranoeList = DialogVybranoeBibleList()
                    dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                }
            }
            binding.novyZavet.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                startActivity(Intent(activity, NovyZapavietSinaidalList::class.java))
            }
            binding.staryZavet.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                startActivity(Intent(activity, StaryZapavietSinaidalList::class.java))
            }
            binding.prodolzych.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (!k.contains("bible_time_sinodal_zavet")) {
                    val dialogBibleTimeError = DialogBibleTimeError()
                    dialogBibleTimeError.show(parentFragmentManager, "dialogBibleTimeError")
                } else {
                    if (activity.checkmoduleResources()) {
                        val intent = if (k.getBoolean("bible_time_sinodal_zavet", true)) Intent(activity, NovyZapavietSinaidalList::class.java)
                        else Intent(activity, StaryZapavietSinaidalList::class.java)
                        intent.putExtra("kniga", k.getInt("bible_time_sinodal_kniga", 0))
                        intent.putExtra("glava", k.getInt("bible_time_sinodal_glava", 0))
                        intent.putExtra("stix", k.getInt("bible_time_sinodal_stix", 0))
                        intent.putExtra("prodolzyt", true)
                        startActivity(intent)
                    } else {
                        activity.installFullMalitounik()
                    }
                }
            }
            binding.zakladki.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    if (BibleGlobalList.zakladkiSinodal.size > 0) {
                        val intent = Intent()
                        intent.setClassName(activity, MainActivity.BIBLEZAKLADKI)
                        intent.putExtra("semuxa", 2)
                        startActivity(intent)
                    } else {
                        val dialog = DialogHelpZakladkiNatatki()
                        dialog.show(childFragmentManager, "DialogHelpZakladkiNatatki")
                    }
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.natatki.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    if (BibleGlobalList.natatkiSinodal.size > 0) {
                        val intent = Intent()
                        intent.setClassName(activity, MainActivity.BIBLENATATKI)
                        intent.putExtra("semuxa", 2)
                        startActivity(intent)
                    } else {
                        val dialog = DialogHelpZakladkiNatatki()
                        dialog.show(childFragmentManager, "DialogHelpZakladkiNatatki")
                    }
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.saeche.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.SEARCHBIBLIA)
                    intent.putExtra("zavet", 2)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.umovyKarystannia.visibility = View.GONE
        }
    }

    companion object {
        fun loadNatatkiZakladkiSinodal(context: Context) {
            val gson = Gson()
            val file = File("${context.filesDir}/BibliaSinodalNatatki.json")
            if (file.exists() && BibleGlobalList.natatkiSinodal.size == 0) {
                try {
                    val type = TypeToken.getParameterized(ArrayList::class.java, BibleNatatkiData::class.java).type
                    BibleGlobalList.natatkiSinodal.addAll(gson.fromJson(file.readText(), type))
                } catch (_: Throwable) {
                }
            }
            val file2 = File("${context.filesDir}/BibliaSinodalZakladki.json")
            if (file2.exists() && BibleGlobalList.zakladkiSinodal.size == 0) {
                try {
                    val type = TypeToken.getParameterized(ArrayList::class.java, BibleZakladkiData::class.java).type
                    BibleGlobalList.zakladkiSinodal.addAll(gson.fromJson(file2.readText(), type))
                } catch (_: Throwable) {
                }
            }
        }
    }
}
