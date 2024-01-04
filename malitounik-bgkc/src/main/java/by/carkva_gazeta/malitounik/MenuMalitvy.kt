package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat

class MenuMalitvy : BaseListFragment() {
    private var mLastClickTime: Long = 0
    private val data get() = resources.getStringArray(R.array.malitvy)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            listAdapter = MenuListAdaprer(it, data)
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
                when (position) {
                    0 -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "malitvy_ranisznija")
                        startActivity(intent)
                    }

                    1 -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "malitvy_viaczernija")
                        startActivity(intent)
                    }

                    2 -> {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.MALITVYPRYNAGODNYIA)
                        startActivity(intent)
                        return
                    }
                }
            } else {
                it.installFullMalitounik()
            }
        }
    }
}