package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.carkva_gazeta.malitounik.databinding.MenuPsalterBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuBibleNadsana : BaseFragment(), View.OnClickListener {
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
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
            binding.saeche.setOnClickListener(this)
            binding.myBible.setOnClickListener(this)
            binding.psalter.setOnClickListener(this)
            binding.prodolzych.setOnClickListener(this)
            binding.pravilaChtenia.setOnClickListener(this)
            binding.malitvaPered.setOnClickListener(this)
            binding.malitvaPosle.setOnClickListener(this)
            binding.pesni.setOnClickListener(this)
            binding.pravila.setOnClickListener(this)
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
        (activity as? BaseActivity)?.let { activity ->
            val id = v?.id ?: 0
            if (id == R.id.saeche) {
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.SEARCHBIBLIA)
                    intent.putExtra("perevod", VybranoeBibleList.PEREVODNADSAN)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            if (id == R.id.myBible) {
                val arrayListVybranoe = ArrayList<VybranoeBibliaData>()
                val bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
                if (bibleVybranoe != "") {
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, VybranoeBibliaData::class.java).type
                    arrayListVybranoe.addAll(gson.fromJson(bibleVybranoe, type))
                }
                if (bibleVybranoe == "" || arrayListVybranoe.isEmpty()) {
                    val dialogBibleVybranoeError = DialogBibleVybranoeError()
                    dialogBibleVybranoeError.show(parentFragmentManager, "dialogBibleVybranoeError")
                } else {
                    val intent = Intent(activity, VybranoeBibleList::class.java)
                    intent.putExtra("perevod", VybranoeBibleList.PEREVODNADSAN)
                    startActivity(intent)
                }
            }
            if (id == R.id.psalter) {
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLIALIST)
                    intent.putExtra("novyZapavet", false)
                    intent.putExtra("perevod", VybranoeBibleList.PEREVODNADSAN)
                    (activity as MainActivity).listBibliaLauncher.launch(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            if (id == R.id.prodolzych) {
                val bibleTime = k.getInt("psalter_time_psalter_nadsan_glava", -1)
                if (bibleTime == -1) {
                    val dialogBibleTimeError = DialogBibleTimeError()
                    dialogBibleTimeError.show(parentFragmentManager, "dialogBibleTimeError")
                } else {
                    if (activity.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(activity, MainActivity.BIBLIALIST)
                        intent.putExtra("glava", k.getInt("psalter_time_psalter_nadsan_glava", 0))
                        intent.putExtra("stix", k.getInt("psalter_time_psalter_nadsan_stix", 0))
                        intent.putExtra("novyZapavet", false)
                        intent.putExtra("perevod", VybranoeBibleList.PEREVODNADSAN)
                        intent.putExtra("prodolzyt", true)
                        (activity as MainActivity).listBibliaLauncher.launch(intent)
                    } else {
                        activity.installFullMalitounik()
                    }
                }
            }
            if (id == R.id.pravila_chtenia) {
                val pravila = DialogNadsanPravila()
                pravila.show(childFragmentManager, "pravila")
            }
            if (id == R.id.malitva_pered) {
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("title", binding.malitvaPered.text.toString())
                    intent.putExtra("resurs", "nadsan_pered")
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            if (id == R.id.malitva_posle) {
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BOGASHLUGBOVYA)
                    intent.putExtra("title", binding.malitvaPosle.text.toString())
                    intent.putExtra("resurs", "nadsan_posle")
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            if (id == R.id.pesni) {
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.NADSANMALITVYIPESNILIST)
                    intent.putExtra("malitva", 2)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
            if (id == R.id.pravila) {
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.PSALTERNADSANA)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
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
                if (activity.checkmoduleResources()) {
                    val intent = Intent()
                    intent.setClassName(activity, MainActivity.BIBLIAACTIVITY)
                    intent.putExtra("kafizma", glava)
                    intent.putExtra("perevod", VybranoeBibleList.PEREVODNADSAN)
                    intent.putExtra("fullglav", 151)
                    startActivity(intent)
                } else {
                    activity.installFullMalitounik()
                }
            }
        }
    }
}