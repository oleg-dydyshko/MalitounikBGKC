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
class MenuBogashlugbovya : ListFragment() {
    private var mLastClickTime: Long = 0
    //String[] by.carkva_gazeta.malitounikApp.data = {"няма Літургія сьв. Яна Залатавуснага", "няма Літургія сьв. Васіля Вялікага", "няма Літургія раней асьвячаных дароў", "Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", "Малітвы пасьля сьвятога прычасьця", "няма Ютрань", "няма Вячэрня", "Абедніца"};
    private val data = arrayOf("Боская Літургія між сьвятымі айца нашага Яна Залатавуснага", "Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", "Малітвы пасьля сьвятога прычасьця", "Ютрань нядзельная (у скароце)", "Абедніца", "Служба за памерлых — Малая паніхіда", "Трапары і кандакі нядзельныя васьмі тонаў", "Трапары і кандакі штодзённыя - на кожны дзень тыдня")

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
            2 -> {
                val intent = Intent(activity, MalitvyPasliaPrychascia::class.java)
                startActivity(intent)
            }
            6 -> {
                val intent = Intent(activity, TonNiadzelny::class.java)
                startActivity(intent)
            }
            7 -> {
                val intent = Intent(activity, TonNaKoznyDzen::class.java)
                startActivity(intent)
            }
            else -> {
                if (MainActivity.checkmoduleResources(activity)) {
                    val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.Bogashlugbovya"))
                    intent.putExtra("bogashlugbovya", position)
                    intent.putExtra("menu", 1)
                    startActivity(intent)
                } else {
                    val dadatak = DialogInstallDadatak()
                    fragmentManager?.let { dadatak.show(it, "dadatak") }
                }
            }
        }
    }
}