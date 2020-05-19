package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment
import java.util.*

/**
 * Created by oleg on 30.5.16
 */
class MenuPesnyTaize : ListFragment() {
    private var data = ArrayList<String>()
    private var mLastClickTime: Long = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            data = MenuPesnyBag.getMenuListData(it, "taize")
            data.sort()
            listAdapter = MenuPesnyListAdapter(it, data)
            listView.isVerticalScrollBarEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (MainActivity.checkmoduleResources(activity)) {
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.PesnyAll"))
            intent.putExtra("pesny", data[position])
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }
}