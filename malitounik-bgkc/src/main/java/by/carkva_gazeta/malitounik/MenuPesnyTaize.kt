package by.carkva_gazeta.malitounik

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import java.util.*

/**
 * Created by oleg on 30.5.16
 */
class MenuPesnyTaize : ListFragment() {
    private val data = ArrayList<String>()
    private var mLastClickTime: Long = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        data.add("Magnifikat")
        data.add("Ostende nobis")
        data.add("Ubi caritas")
        data.add("Блаславёны Бог")
        data.add("Бог мой, Iсус, сьвяцi нам у цемры")
        data.add("Будзь са Мной")
        data.add("Дай нам, Божа, моц ласкi Сваёй")
        data.add("Дзякуем Табе, Божа наш")
        data.add("Дзякуем Табе, Хрысьце")
        data.add("Кожны дзень Бог дае мне сiлы")
        data.add("Мая душа ў Богу мае спакой")
        data.add("О, Iсусе")
        data.add("О, Госпадзе мой")
        data.add("Прыйдзi, Дух Сьвяты")
        data.add("У цемры iдзём")
        data.add("У цемры нашых дзён")
        data.add("Хай тваё сэрца больш не журыцца")
        // так же добавить в search_pesny.get_Menu_list_data
        data.sort()
        activity?.let {
            listAdapter = MenuListAdaprer(it)
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

    internal inner class MenuListAdaprer(private val activity: Activity) : ArrayAdapter<String>(activity, R.layout.simple_list_item_2, R.id.label, data as List<String>) {
        private val k: SharedPreferences = activity.getSharedPreferences("biblia", Context.MODE_PRIVATE)

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                rootView = activity.layoutInflater.inflate(R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder()
                rootView.tag = viewHolder
                viewHolder.text = rootView.findViewById(R.id.label)
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = k.getBoolean("dzen_noch", false)
            viewHolder.text?.text = data[position]
            viewHolder.text?.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsActivity.GET_FONT_SIZE_MIN)
            if (dzenNoch) {
                viewHolder.text?.setBackgroundResource(R.drawable.selector_dark)
                viewHolder.text?.setTextColor(ContextCompat.getColor(activity, R.color.colorIcons))
                viewHolder.text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            }
            return rootView
        }
    }

    private class ViewHolder {
        var text: TextViewRobotoCondensed? = null
    }
}