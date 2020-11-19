package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

class MenuGlavnoe : ListFragment() {
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.galounae)
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { listAdapter = MenuListAdaprer(it, data) }
        listView.isVerticalScrollBarEnabled = false
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        val kq = activity?.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditor = kq?.edit()
        prefEditor?.putInt("naviny", position)
        prefEditor?.apply()
        if (position == 7) {
            if (MainActivity.checkmoduleResources(activity)) {
                if (MainActivity.checkmodulesBiblijateka(activity)) {
                    val intent = Intent(activity, Class.forName("by.carkva_gazeta.biblijateka.BibliotekaView"))
                    startActivity(intent)
                } else {
                    activity?.let { MainActivity.downloadDynamicModule(it) }
                }
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        } else {
            val intent = Intent(activity, Naviny::class.java)
            startActivity(intent)
        }
    }
}