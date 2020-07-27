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
class MenuParafiiBgkc : ListFragment() {
    private val data = arrayOf("Курыя Апостальскай Візітатуры БГКЦ", "Цэнтральны дэканат", "Усходні дэканат", "Заходні дэканат", "Замежжа")
    private var mLastClickTime: Long = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            listAdapter = MenuListAdaprer(it, data)
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
            if (position == 0) {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.ParafiiBgkc"))
                intent.putExtra("bgkc_parafii", position)
                intent.putExtra("bgkc", position)
                startActivity(intent)
            } else {
                val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.ParafiiBgkcDekanat"))
                intent.putExtra("bgkc", position)
                startActivity(intent)
            }
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }
}