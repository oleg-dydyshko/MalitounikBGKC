package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat

class MenuRuzanec : BaseListFragment() {
    private var mLastClickTime: Long = 0
    private val data get() = resources.getStringArray(R.array.ruzanec)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val adapter = MenuListAdaprer(it, data)
            listAdapter = adapter
            listView.isVerticalScrollBarEnabled = false
            val dzenNoch = (it as BaseActivity).getBaseDzenNoch()
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
        (activity as? BaseActivity)?.let {
            if (it.checkmoduleResources()) {
                val intent = Intent()
                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                when (position) {
                    0 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec0")
                    }

                    1 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec2")
                    }

                    2 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec1")
                    }

                    3 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec3")
                    }

                    4 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec4")
                    }

                    5 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec5")
                    }

                    6 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "ruzanec6")
                    }
                }
                startActivity(intent)
            } else {
                it.installFullMalitounik()
            }
        }
    }
}