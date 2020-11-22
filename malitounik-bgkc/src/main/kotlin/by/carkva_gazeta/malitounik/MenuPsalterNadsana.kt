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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.menu_psalter.*

class MenuPsalterNadsana : Fragment(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0
    private var bibleTime = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_psalter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
            bibleTime = k.getString("psalter_time_psalter_nadsan", "") ?: ""
            if (bibleTime == "") {
                MenuBibleSemuxa.bible_time = true
                prodolzych.visibility = View.GONE
            }
            val bibleVybranoe = k.getString("bibleVybranoeNadsan", "") ?: ""
            if (bibleVybranoe == "") {
                myBible.visibility = View.GONE
            } else {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<VybranoeBibliaData>>() {}.type
                val arrayListVybranoe: ArrayList<VybranoeBibliaData> = gson.fromJson(bibleVybranoe, type)
                if (arrayListVybranoe.isEmpty()) myBible.visibility = View.GONE
            }
            myBible.setOnClickListener(this)
            psalter.setOnClickListener(this)
            prodolzych.setOnClickListener(this)
            pravila_chtenia.setOnClickListener(this)
            malitva_pered.setOnClickListener(this)
            malitva_posle.setOnClickListener(this)
            pesni.setOnClickListener(this)
            pravila.setOnClickListener(this)
            if (dzenNoch) {
                psalter.setBackgroundResource(R.drawable.knopka_red_black)
            }
            textView1.setOnClickListener(this)
            textView2.setOnClickListener(this)
            textView3.setOnClickListener(this)
            textView4.setOnClickListener(this)
            textView5.setOnClickListener(this)
            textView6.setOnClickListener(this)
            textView7.setOnClickListener(this)
            textView8.setOnClickListener(this)
            textView9.setOnClickListener(this)
            textView10.setOnClickListener(this)
            textView11.setOnClickListener(this)
            textView12.setOnClickListener(this)
            textView13.setOnClickListener(this)
            textView14.setOnClickListener(this)
            textView15.setOnClickListener(this)
            textView16.setOnClickListener(this)
            textView17.setOnClickListener(this)
            textView18.setOnClickListener(this)
            textView19.setOnClickListener(this)
            textView20.setOnClickListener(this)
            if (dzenNoch) {
                textViewtitle.setBackgroundResource(R.drawable.nadsanblack)
            }
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = v?.id ?: 0
        if (id == R.id.myBible) {
            VybranoeBibleList.biblia = 3
            startActivity(Intent(activity, VybranoeBibleList::class.java))
        }
        if (id == R.id.psalter) {
            startActivity(Intent(activity, NadsanContent::class.java))
        }
        if (id == R.id.prodolzych) {
            val gson = Gson()
            val type = object : TypeToken<ArrayMap<String?, Int?>?>() {}.type
            val set: ArrayMap<String, Int> = gson.fromJson(bibleTime, type)
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, NadsanContent::class.java)
                intent.putExtra("glava", set["glava"])
                intent.putExtra("stix", set["stix"])
                intent.putExtra("prodolzyt", true)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        if (id == R.id.pravila_chtenia) {
            val pravila = DialogNadsanPravila()
            fragmentManager?.let { pravila.show(it, "pravila") }
        }
        if (id == R.id.malitva_pered) {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.NadsanMalitvyIPesni"))
                intent.putExtra("malitva", 0)
                intent.putExtra("malitva_title", malitva_pered.text.toString())
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        if (id == R.id.malitva_posle) {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.NadsanMalitvyIPesni"))
                intent.putExtra("malitva", 1)
                intent.putExtra("malitva_title", malitva_posle.text.toString())
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        if (id == R.id.pesni) {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.NadsanMalitvyIPesni"))
                intent.putExtra("malitva", 2)
                intent.putExtra("malitva_title", pesni.text.toString())
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        if (id == R.id.pravila) {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.PsalterNadsana"))
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
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
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.NadsanContentActivity"))
                intent.putExtra("kafizma", glava)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
    }

    companion object {
        var bible_time = false
    }
}