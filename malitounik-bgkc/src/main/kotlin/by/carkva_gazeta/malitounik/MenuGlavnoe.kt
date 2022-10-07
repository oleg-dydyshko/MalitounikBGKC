package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat

class MenuGlavnoe : BaseListFragment() {
    private val data get() = resources.getStringArray(R.array.galounae)
    private var mLastClickTime: Long = 0

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