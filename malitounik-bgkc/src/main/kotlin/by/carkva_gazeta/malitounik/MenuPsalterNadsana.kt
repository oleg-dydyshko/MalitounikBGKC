package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.carkva_gazeta.malitounik.databinding.MenuPsalterBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuPsalterNadsana : Fragment(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0
    private var _binding: MenuPsalterBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MenuPsalterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            binding.saeche.setOnClickListener(this)
            binding.myBible.setOnClickListener(this)
            binding.psalter.setOnClickListener(this)
            binding.prodolzych.setOnClickListener(this)
            binding.pravilaChtenia.setOnClickListener(this)
            binding.malitvaPered.setOnClickListener(this)
            binding.malitvaPosle.setOnClickListener(this)
            binding.pesni.setOnClickListener(this)
            binding.pravila.setOnClickListener(this)
            if (dzenNoch) {
                binding.psalter.setBackgroundResource(R.drawable.knopka_red_black)
            }
            binding.textView1.setOnClickListener(this)
            binding.textView2.setOnClickListener(this)
            binding.textView3.setOnClickListener(this)
            binding.textView4.setOnClickListener(this)
            binding.textView5.setOnClickListener(this)
            binding.textView6.setOnClickListener(this)
            binding.textView7.setOnClickListener(this)
            binding.textView8.setOnClickListener(this)
            binding.textView9.setOnClickListener(this)
            binding.textView10.setOnClickListener(this)
            binding.textView11.setOnClickListener(this)
            binding.textView12.setOnClickListener(this)
            binding.textView13.setOnClickListener(this)
            binding.textView14.setOnClickListener(this)
            binding.textView15.setOnClickListener(this)
            binding.textView16.setOnClickListener(this)
            binding.textView17.setOnClickListener(this)
            binding.textView18.setOnClickListener(this)
            binding.textView19.setOnClickListener(this)
            binding.textView20.setOnClickListener(this)
            if (dzenNoch) {
                binding.textViewtitle.setBackgroundResource(R.drawable.nadsanblack)
            }
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = v?.id ?: 0
        if (id == R.id.saeche) {
            activity?.let { activity ->
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.SEARCHBIBLIA)
                    intent.putExtra("zavet", 3)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    dadatak.show(childFragmentManager, "dadatak")
                }
            }
        }
        if (id == R.id.myBible) {
            val arrayListVybranoe = ArrayList<VybranoeBibliaData>()
            val bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
            if (bibleVybranoe != "") {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
                arrayListVybranoe.addAll(gson.fromJson(bibleVybranoe, type))
            }
            if (bibleVybranoe == "" || arrayListVybranoe.isEmpty()) {
                val dialogBibleVybranoeError = DialogBibleVybranoeError()
                dialogBibleVybranoeError.show(parentFragmentManager, "dialogBibleVybranoeError")
            } else {
                DialogVybranoeBibleList.biblia = 3
                val dialogVybranoeList = DialogVybranoeBibleList()
                dialogVybranoeList.show(childFragmentManager, "vybranoeBibleList")
            }
        }
        if (id == R.id.psalter) {
            startActivity(Intent(activity, NadsanContent::class.java))
        }
        if (id == R.id.prodolzych) {
            val bibleTime = k.getString("psalter_time_psalter_nadsan", "") ?: ""
            if (bibleTime == "") {
                val dialogBibleTimeError = DialogBibleTimeError()
                dialogBibleTimeError.show(parentFragmentManager, "dialogBibleTimeError")
            } else {
                val gson = Gson()
                val type = object : TypeToken<ArrayMap<String?, Int?>?>() {}.type
                val set: ArrayMap<String, Int> = gson.fromJson(bibleTime, type)
                if (MainActivity.checkmoduleResources()) {
                    val intent = Intent(activity, NadsanContent::class.java)
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
        if (id == R.id.pravila_chtenia) {
            val pravila = DialogNadsanPravila()
            pravila.show(childFragmentManager, "pravila")
        }
        if (id == R.id.malitva_pered) {
            if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.NADSANMALITVYIPESNI)
                    intent.putExtra("malitva", 0)
                    intent.putExtra("malitva_title", binding.malitvaPered.text.toString())
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
        if (id == R.id.malitva_posle) {
            if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.NADSANMALITVYIPESNI)
                    intent.putExtra("malitva", 1)
                    intent.putExtra("malitva_title", binding.malitvaPosle.text.toString())
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
        if (id == R.id.pesni) {
            if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.NADSANMALITVYIPESNILIST)
                    intent.putExtra("malitva", 2)
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
        if (id == R.id.pravila) {
            if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.PSALTERNADSANA)
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
        val glava = when (id) {
            R.id.textView1 -> 1
            R.id.textView2 -> 2
            R.id.textView3 -> 3
            R.id.textView4 -> 4
            R.id.textView5 -> 5
            R.id.textView6 -> 6
            R.id.textView7 -> 7
            R.id.textView8 -> 8
            R.id.textView9 -> 9
            R.id.textView10 -> 10
            R.id.textView11 -> 11
            R.id.textView12 -> 12
            R.id.textView13 -> 13
            R.id.textView14 -> 14
            R.id.textView15 -> 15
            R.id.textView16 -> 16
            R.id.textView17 -> 17
            R.id.textView18 -> 18
            R.id.textView19 -> 19
            R.id.textView20 -> 20
            else -> -1
        }
        if (glava != -1) {
            if (MainActivity.checkmoduleResources()) {
                activity?.let {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.NADSANCONTENTACTIVITY)
                    intent.putExtra("kafizma", glava)
                    startActivity(intent)
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
    }
}