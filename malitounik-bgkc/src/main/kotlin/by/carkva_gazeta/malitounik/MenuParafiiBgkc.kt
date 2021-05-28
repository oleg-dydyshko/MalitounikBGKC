package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

class MenuParafiiBgkc : ListFragment() {
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.parafii_bgkc)
    private var mLastClickTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        if (MainActivity.checkmoduleResources()) {
            activity?.let {
                if (position == 0) {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.PARAFIIBGKC)
                    intent.putExtra("bgkc_parafii", position)
                    intent.putExtra("bgkc", position)
                    startActivity(intent)
                } else {
                    val intent = Intent()
                    intent.setClassName(it, MainActivity.PARAFIIBGKCDEKANAT)
                    intent.putExtra("bgkc", position)
                    startActivity(intent)
                }
            }
        } else {
            val dadatak = DialogInstallDadatak()
            dadatak.show(childFragmentManager, "dadatak")
        }
    }
}