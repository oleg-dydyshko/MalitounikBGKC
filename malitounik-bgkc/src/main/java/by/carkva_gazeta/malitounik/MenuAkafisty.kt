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
class MenuAkafisty : ListFragment() {
    //String[] by.carkva_gazeta.malitounikApp.data = {"Пра Акафіст", "Найсьвяцейшай Багародзіцы", "Маці Божай Нястомнай Дапамогі", "перад Жыровіцкай іконай", "у гонар Падляшскіх мучанікаў", "няма Імю Ісусаваму", "да Духа Сьвятога", "сьв. Апосталам Пятру і Паўлу", "няма Жыцьцядайнаму Крыжу"};
    private val data = arrayOf("Пра Акафіст", "Найсьвяцейшай Багародзіцы", "Маці Божай Нястомнай Дапамогі", "перад Жыровіцкай іконай", "у гонар Падляшскіх мучанікаў", "Імю Ісусаваму", "да Духа Сьвятога", "сьв. Апосталам Пятру і Паўлу")
    private var mLastClickTime: Long = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.Bogashlugbovya"))
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
            }
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

}