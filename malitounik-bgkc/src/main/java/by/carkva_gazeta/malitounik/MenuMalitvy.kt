package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

/**
 * Created by oleg on 30.5.16
 */
class MenuMalitvy : ListFragment() {
    private var mLastClickTime: Long = 0
    //String[] by.carkva_gazeta.malitounikApp.data = {"Ранішняя малітвы", "Вячэрнія малітвы", "Прынагодныя малітвы", "няма Прынагодныя – у псальмах"};
    private val data = arrayOf("Ранішняя малітвы", "Вячэрнія малітвы", "Прынагодныя малітвы")

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
        if (position == 2) {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.MalitvyPrynagodnyia"))
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        } else {
            if (MainActivity.checkmoduleResources(activity)) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.Bogashlugbovya"))
                intent.putExtra("bogashlugbovya", position)
                intent.putExtra("menu", 2)
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                fragmentManager?.let { dadatak.show(it, "dadatak") }
            }
        }
    }
}