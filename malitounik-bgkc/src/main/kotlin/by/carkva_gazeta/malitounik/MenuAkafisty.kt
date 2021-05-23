package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

class MenuAkafisty : ListFragment() {
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.akafisty)
    private var mLastClickTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            val adapter = MenuListAdaprer(it, data)
            listAdapter = adapter
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
            activity?.let {
                val intent = Intent()
                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                when (position) {
                    0 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist0")
                    }
                    1 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist1")
                    }
                    2 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist2")
                    }
                    3 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist3")
                    }
                    4 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist4")
                    }
                    5 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist5")
                    }
                    6 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist6")
                    }
                    7 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist7")
                    }
                    8 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist8")
                    }
                }
                startActivity(intent)
            }
        } else {
            val dadatak = DialogInstallDadatak()
            dadatak.show(childFragmentManager, "dadatak")
        }
    }

}