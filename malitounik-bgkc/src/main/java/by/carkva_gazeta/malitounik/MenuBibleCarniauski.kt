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

class MenuBibleCarniauski : Fragment() {
    private var mLastClickTime: Long = 0
    private var _binding: MenuBibleBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.let {
            loadNatatkiZakladkiCarniauski(it)
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
                val bibleVybranoe = k.getString("bibleVybranoeCarniauski", "") ?: ""
                if (bibleVybranoe != "") {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    arrayListVybranoe.addAll(gson.fromJson(bibleVybranoe, type))
                }
                if (bibleVybranoe == "" || arrayListVybranoe.isEmpty()) {
                    val dialogBibleVybranoeError = DialogBibleVybranoeError()
                    dialogBibleVybranoeError.show(parentFragmentManager, "dialogBibleVybranoeError")
                } else {
                    val dialogVybranoeList = DialogVybranoeBibleList.getInstance(DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                }
            }
            binding.novyZavet.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLIALIST)
                    intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    intent.putExtra("novyZapavet", true)
                    (activity as MainActivity).listBibliaLauncher.launch(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.staryZavet.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLIALIST)
                    intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    intent.putExtra("novyZapavet", false)
                    (activity as MainActivity).listBibliaLauncher.launch(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.prodolzych.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (!k.contains("bible_time_carniauski_zavet")) {
                    val dialogBibleTimeError = DialogBibleTimeError()
                    dialogBibleTimeError.show(parentFragmentManager, "dialogBibleTimeError")
                } else {
                    if (activity.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(activity, MainActivity.BIBLIALIST)
                        intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                        intent.putExtra("novyZapavet", k.getBoolean("bible_time_carniauski_zavet", true))
                        intent.putExtra("kniga", k.getInt("bible_time_carniauski_kniga", 0))
                        intent.putExtra("glava", k.getInt("bible_time_carniauski_glava", 0))
                        intent.putExtra("stix", k.getInt("bible_time_carniauski_stix", 0))
                        intent.putExtra("prodolzyt", true)
                        (activity as MainActivity).listBibliaLauncher.launch(intent)
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
                    if (BibleGlobalList.zakladkiCarniauski.size > 0) {
                        val intent = Intent()
                        intent.setClassName(activity, MainActivity.BIBLEZAKLADKI)
                        intent.putExtra("semuxa", 4)
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
                    if (BibleGlobalList.natatkiCarniauski.size > 0) {
                        val intent = Intent()
                        intent.setClassName(activity, MainActivity.BIBLENATATKI)
                        intent.putExtra("semuxa", 4)
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
                    intent.putExtra("perevod", DialogVybranoeBibleList.PEREVODCARNIAUSKI)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.umovyKarystannia.visibility = View.GONE
        }
    }

    companion object {
        fun loadNatatkiZakladkiCarniauski(context: Context) {
            val gson = Gson()
            val file = File("${context.filesDir}/BibliaCarniauskiNatatki.json")
            if (file.exists() && BibleGlobalList.natatkiCarniauski.size == 0) {
                try {
                    val type = TypeToken.getParameterized(ArrayList::class.java, BibleNatatkiData::class.java).type
                    BibleGlobalList.natatkiCarniauski.addAll(gson.fromJson(file.readText(), type))
                } catch (_: Throwable) {
                }
            }
            val file2 = File("${context.filesDir}/BibliaCarniauskiZakladki.json")
            if (file2.exists() && BibleGlobalList.zakladkiCarniauski.size == 0) {
                try {
                    val type = TypeToken.getParameterized(ArrayList::class.java, BibleZakladkiData::class.java).type
                    BibleGlobalList.zakladkiCarniauski.addAll(gson.fromJson(file2.readText(), type))
                } catch (_: Throwable) {
                }
            }
        }
    }
}
