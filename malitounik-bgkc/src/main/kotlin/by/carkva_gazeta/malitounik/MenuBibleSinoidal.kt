package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.MenuBibleBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuBibleSinoidal : Fragment() {
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
        activity?.let { activity ->
            val k = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            binding.myBible.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                val arrayListVybranoe = ArrayList<VybranoeBibliaData>()
                val bibleVybranoe = k.getString("bibleVybranoeSinoidal", "") ?: ""
                if (bibleVybranoe != "") {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
                    arrayListVybranoe.addAll(gson.fromJson(bibleVybranoe, type))
                }
                if (bibleVybranoe == "" || arrayListVybranoe.isEmpty()) {
                    val dialogBibleVybranoeError = DialogBibleVybranoeError()
                    dialogBibleVybranoeError.show(parentFragmentManager, "dialogBibleVybranoeError")
                } else {
                    DialogVybranoeBibleList.biblia = 2
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
                val bibleTime = k.getString("bible_time_sinodal", "") ?: ""
                if (bibleTime == "") {
                    val dialogBibleTimeError = DialogBibleTimeError()
                    dialogBibleTimeError.show(parentFragmentManager, "dialogBibleTimeError")
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ArrayMap<String?, Int?>?>() {}.type
                    val set: ArrayMap<String, Int> = gson.fromJson(bibleTime, type)
                    if (set["zavet"] == 1) {
                        if (MainActivity.checkmoduleResources()) {
                            val intent = Intent(activity, NovyZapavietSinaidalList::class.java)
                            intent.putExtra("kniga", set["kniga"])
                            intent.putExtra("glava", set["glava"])
                            intent.putExtra("stix", set["stix"])
                            intent.putExtra("prodolzyt", true)
                            startActivity(intent)
                        } else {
                            val dadatak = DialogInstallDadatak()
                            dadatak.show(childFragmentManager, "dadatak")
                        }
                    } else {
                        if (MainActivity.checkmoduleResources()) {
                            val intent = Intent(activity, StaryZapavietSinaidalList::class.java)
                            intent.putExtra("kniga", set["kniga"])
                            intent.putExtra("glava", set["glava"])
                            intent.putExtra("stix", set["stix"])
                            intent.putExtra("prodolzyt", true)
                            startActivity(intent)
                        } else {
                            val dadatak = DialogInstallDadatak()
                            dadatak.show(childFragmentManager, "dadatak")
                        }
                    }
                }
            }
            binding.zakladki.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLEZAKLADKI)
                    intent.putExtra("semuxa", 2)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
            }
            binding.natatki.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLENATATKI)
                    intent.putExtra("semuxa", 2)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
            }
            binding.saeche.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.SEARCHBIBLIA)
                    intent.putExtra("zavet", 2)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
            }
            binding.umovyKarystannia.visibility = View.GONE
            if (dzenNoch) {
                binding.novyZavet.setBackgroundResource(R.drawable.knopka_red_black)
                binding.staryZavet.setBackgroundResource(R.drawable.knopka_red_black)
            }
        }
    }
}