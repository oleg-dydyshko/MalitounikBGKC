package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

class MenuBogashlugbovya : ListFragment() {
    private var mLastClickTime: Long = 0
    private val data: Array<out String>
        get() = resources.getStringArray(R.array.bogaslugbovuia)

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
        when (position) {
            3 -> {
                val intent = Intent(activity, MalitvyPasliaPrychascia::class.java)
                startActivity(intent)
            }
            5 -> {
                val intent = Intent(activity, ViacherniaList::class.java)
                startActivity(intent)
            }
            8 -> {
                val intent = Intent(activity, TonNiadzelny::class.java)
                startActivity(intent)
            }
            9 -> {
                val intent = Intent(activity, TonNaKoznyDzen::class.java)
                startActivity(intent)
            }
            else -> {
                if (MainActivity.checkmoduleResources(activity)) {
                    when (position) {
                        0 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", "bogashlugbovya1")
                                startActivity(intent)
                            }
                        }
                        1 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", "bogashlugbovya2")
                                startActivity(intent)
                            }
                        }
                        2 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", "bogashlugbovya4")
                                startActivity(intent)
                            }
                        }
                        4 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", "bogashlugbovya6")
                                startActivity(intent)
                            }
                        }
                        6 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", "bogashlugbovya8")
                                startActivity(intent)
                            }
                        }
                        7 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", "bogashlugbovya11")
                                startActivity(intent)
                            }
                        }
                        10 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTUSPIS)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", 12)
                                startActivity(intent)
                            }
                        }
                        11 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTUSPIS)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", 13)
                                startActivity(intent)
                            }
                        }
                        12 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTUSPIS)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", 14)
                                startActivity(intent)
                            }
                        }
                        13 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTUSPIS)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", 15)
                                startActivity(intent)
                            }
                        }
                        14 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTUSPIS)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", 16)
                                startActivity(intent)
                            }
                        }
                        15 -> {
                            activity?.let {
                                val intent = Intent()
                                intent.setClassName(it, MainActivity.SLUGBYVIALIKAGAPOSTUSPIS)
                                intent.putExtra("title", data[position])
                                intent.putExtra("resurs", 17)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }
        }
    }
}