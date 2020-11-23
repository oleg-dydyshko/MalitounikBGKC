package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

class MenuMalitvy : ListFragment() {
    private var mLastClickTime: Long = 0
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.malitvy)

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
        if (MainActivity.checkmoduleResources(activity)) {
            activity?.let {
                when (position) {
                    0 -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "malitvy1")
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "malitvy2")
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.MALITVYPRYNAGODNYIA)
                        startActivity(intent)
                        return
                    }
                }
            }
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }
}