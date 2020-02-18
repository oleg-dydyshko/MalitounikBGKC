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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.menu_psalter.*

class MenuPsalterNadsana : Fragment(), View.OnClickListener {
    private lateinit var k: SharedPreferences
    private var mLastClickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_psalter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            k = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = k.getBoolean("dzen_noch", false)
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
                textView1.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView2.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView3.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView4.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView5.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView6.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView7.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView8.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView9.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView10.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView11.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView12.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView13.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView14.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView15.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView16.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView17.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView18.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView19.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textView20.setTextColor(ContextCompat.getColor(it, R.color.colorPrimary_text))
                textViewtitle.setTextColor(ContextCompat.getColor(it, R.color.colorIcons))
            }
        }
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val id = v?.id ?: 0
        if (id == R.id.psalter) {
            startActivity(Intent(activity, NadsanContent::class.java))
        }
        if (id == R.id.prodolzych) {
            val bibleTime = k.getString("psalter_time_psalter_nadsan", "")
            if (bibleTime != "") {
                val gson = Gson()
                val type = object : TypeToken<ArrayMap<String?, Int?>?>() {}.type
                val set: ArrayMap<String, Int> = gson.fromJson(bibleTime, type)
                if (MainActivity.checkmoduleResources(activity)) {
                    val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.NadsanContentActivity"))
                    intent.putExtra("glava", set["glava"])
                    intent.putExtra("stix", set["stix"])
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            } else {
                val chtenia = DialogNoBibleChtenia()
                fragmentManager?.let { chtenia.show(it, "no_bible_chtenia") }
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
        var glava = -1
        when (id) {
            R.id.textView1 -> glava = 1
            R.id.textView2 -> glava = 2
            R.id.textView3 -> glava = 3
            R.id.textView4 -> glava = 4
            R.id.textView5 -> glava = 5
            R.id.textView6 -> glava = 6
            R.id.textView7 -> glava = 7
            R.id.textView8 -> glava = 8
            R.id.textView9 -> glava = 9
            R.id.textView10 -> glava = 10
            R.id.textView11 -> glava = 11
            R.id.textView12 -> glava = 12
            R.id.textView13 -> glava = 13
            R.id.textView14 -> glava = 14
            R.id.textView15 -> glava = 15
            R.id.textView16 -> glava = 16
            R.id.textView17 -> glava = 17
            R.id.textView18 -> glava = 18
            R.id.textView19 -> glava = 19
            R.id.textView20 -> glava = 20
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
}