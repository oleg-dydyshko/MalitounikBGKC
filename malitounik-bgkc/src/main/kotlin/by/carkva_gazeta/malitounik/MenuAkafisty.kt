package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.core.content.ContextCompat

class MenuAkafisty : BaseListFragment() {
    private val data get() = resources.getStringArray(R.array.akafisty)
    private var mLastClickTime: Long = 0

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

                    9 -> {
                        intent.putExtra("title", data[position])
                        intent.putExtra("resurs", "akafist_rosickim_muczanikam")
                    }
                }
                startActivity(intent)
            } else {
                val dadatak = DialogInstallDadatak()
                dadatak.show(childFragmentManager, "dadatak")
            }
        }
    }
}