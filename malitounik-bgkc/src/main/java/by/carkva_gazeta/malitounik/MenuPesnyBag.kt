package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by oleg on 30.5.16
 */
class MenuPesnyBag : ListFragment() {
    private var data = ArrayList<String>()
    private var mLastClickTime: Long = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            data = getMenuListData(it, "bag")
            data.sort()
            listAdapter = MenuPesnyListAdapter(it, data)
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
            val intent = Intent(activity, Class.forName("by.carkva_gazeta.resources.PesnyAll"))
            intent.putExtra("pesny", data[position])
            startActivity(intent)
        } else {
            val dadatak = DialogInstallDadatak()
            fragmentManager?.let { dadatak.show(it, "dadatak") }
        }
    }

    companion object {

        fun getMenuListData(context: Context, pesny: String): ArrayList<String> {
            val menuListData = ArrayList<String>()
            val inputStream = context.resources.openRawResource(R.raw.pesny_menu)
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var line: String
            reader.forEachLine {
                line = it
                if (it.contains(pesny)) {
                    val split = line.split("<>").toTypedArray()
                    menuListData.add(split[1])
                }
            }
            return menuListData
        }
    }
}