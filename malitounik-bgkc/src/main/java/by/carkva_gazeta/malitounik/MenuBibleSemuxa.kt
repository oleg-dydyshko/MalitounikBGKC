package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.carkva_gazeta.malitounik.databinding.MenuBibleBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuBibleSemuxa : BaseFragment() {
    private var mLastClickTime: Long = 0
    private var _binding: MenuBibleBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuBibleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BaseActivity)?.let { activity ->
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = activity.getBaseDzenNoch()
            binding.myBible.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val arrayListVybranoe = ArrayList<VybranoeBibliaData>()
                val bibleVybranoe = k.getString("bibleVybranoeSemuxa", "") ?: ""
                if (bibleVybranoe != "") {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeData::class.java).type
                    arrayListVybranoe.addAll(gson.fromJson(bibleVybranoe, type))
                }
                if (bibleVybranoe == "" || arrayListVybranoe.isEmpty()) {
                    val dialogBibleVybranoeError = DialogBibleVybranoeError()
                    dialogBibleVybranoeError.show(parentFragmentManager, "dialogBibleVybranoeError")
                } else {
                    DialogVybranoeBibleList.biblia = "1"
                    val dialogVybranoeList = DialogVybranoeBibleList()
                    dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
                }
            }
            binding.novyZavet.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                startActivity(Intent(activity, NovyZapavietSemuxaList::class.java))
            }
            binding.staryZavet.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                startActivity(Intent(activity, StaryZapavietSemuxaList::class.java))
            }
            binding.prodolzych.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (!k.contains("bible_time_semuxa_zavet")) {
                    val dialogBibleTimeError = DialogBibleTimeError()
                    dialogBibleTimeError.show(parentFragmentManager, "dialogBibleTimeError")
                } else {
                    if (activity.checkmoduleResources()) {
                        val intent = if (k.getBoolean("bible_time_semuxa_zavet", true)) Intent(activity, NovyZapavietSemuxaList::class.java)
                        else Intent(activity, StaryZapavietSemuxaList::class.java)
                        intent.putExtra("kniga", k.getInt("bible_time_semuxa_kniga", 0))
                        intent.putExtra("glava", k.getInt("bible_time_semuxa_glava", 0))
                        intent.putExtra("stix", k.getInt("bible_time_semuxa_stix", 0))
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
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLEZAKLADKI)
                    intent.putExtra("semuxa", 1)
                    startActivity(intent)
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
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLENATATKI)
                    intent.putExtra("semuxa", 1)
                    startActivity(intent)
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
                    intent.putExtra("zavet", 1)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            binding.umovyKarystannia.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val semukha = DialogAlesyaSemukha()
                semukha.show(childFragmentManager, "Alesya_Semukha")
            }
            if (dzenNoch) {
                binding.novyZavet.setBackgroundResource(R.drawable.knopka_red_black)
                binding.staryZavet.setBackgroundResource(R.drawable.knopka_red_black)
            }
        }
    }
}