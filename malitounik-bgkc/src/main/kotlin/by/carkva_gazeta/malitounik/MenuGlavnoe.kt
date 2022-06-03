package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment

class MenuGlavnoe : ListFragment() {
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.galounae)
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            listAdapter = MenuListAdaprer(it, data)
            listView.isVerticalScrollBarEnabled = false
            val chin = it.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val dzenNoch = chin.getBoolean("dzen_noch", false)
            if (dzenNoch) {
                listView.setBackgroundResource(R.color.colorbackground_material_dark)
                listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            }
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (position == 7) {
            if (MainActivity.checkmoduleResources()) {
                if (MainActivity.checkmodulesBiblijateka()) {
                    activity?.let {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BIBLIOTEKAVIEW)
                        startActivity(intent)
                    }
                } else {
                    activity?.let {
                        MainActivity.moduleName = "biblijateka"
                        MainActivity.downloadDynamicModule(it)
                    }
                }
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        } else {
            val intent = Intent(activity, Naviny::class.java)
            intent.putExtra("naviny", position)
            startActivity(intent)
        }
    }
}