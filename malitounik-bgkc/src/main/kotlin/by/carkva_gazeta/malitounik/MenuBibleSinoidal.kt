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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.menu_bible.*

class MenuBibleSinoidal : Fragment() {
    private var mLastClickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.menu_bible, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val k = activity?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val dzenNoch = k?.getBoolean("dzen_noch", false)?: false
        novyZavet.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            startActivity(Intent(activity, NovyZapavietSinaidalList::class.java))
        }
        staryZavet.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            startActivity(Intent(activity, StaryZapavietSinaidalList::class.java))
        }
        val bibleTime = k?.getString("bible_time_sinodal", "") ?: ""
        if (bibleTime == "") {
            bible_time = true
            prodolzych.visibility = View.GONE
        }
        prodolzych.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val gson = Gson()
            val type = object : TypeToken<ArrayMap<String?, Int?>?>() {}.type
            val set: ArrayMap<String, Int> = gson.fromJson(bibleTime, type)
            if (set["zavet"] == 1) {
                if (MainActivity.checkmoduleResources(activity)) {
                        val intent = Intent(activity, NovyZapavietSinaidalList::class.java)
                        intent.putExtra("kniga", set["kniga"])
                        intent.putExtra("glava", set["glava"])
                        intent.putExtra("stix", set["stix"])
                        intent.putExtra("prodolzyt", true)
                        startActivity(intent)
                    } else {
                        val dadatak = DialogInstallDadatak()
                        fragmentManager?.let { dadatak.show(it, "dadatak") }
                    }
                } else {
                    if (MainActivity.checkmoduleResources(activity)) {
                        val intent = Intent(activity, StaryZapavietSinaidalList::class.java)
                        intent.putExtra("kniga", set["kniga"])
                        intent.putExtra("glava", set["glava"])
                        intent.putExtra("stix", set["stix"])
                        intent.putExtra("prodolzyt", true)
                        startActivity(intent)
                    } else {
                        val dadatak = DialogInstallDadatak()
                        fragmentManager?.let { dadatak.show(it, "dadatak") }
                    }
                }
        }
        zakladki.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.BibleZakladki"))
                intent.putExtra("semuxa", 2)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        natatki.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.BibleNatatki"))
                intent.putExtra("semuxa", 2)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        saeche.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.SearchBiblia"))
                intent.putExtra("zavet", 2)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
        umovy_karystannia.visibility = View.GONE
        if (dzenNoch) {
            activity?.let {
                novyZavet.setBackgroundResource(R.drawable.knopka_red_black)
                staryZavet.setBackgroundResource(R.drawable.knopka_red_black)
            }
        }
    }

    companion object {
        var bible_time = false
    }
}