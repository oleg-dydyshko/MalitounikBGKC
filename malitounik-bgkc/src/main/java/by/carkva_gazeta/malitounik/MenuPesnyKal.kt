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
class MenuPesnyKal : ListFragment() {
    private val data = ArrayList<String>()
    private var mLastClickTime: Long = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        data.add("Ave Maria (Зорка зазьзяла)")
        data.add("А што гэта за сьпевы")
        data.add("А ў сьвеце нам навіна была")
        data.add("А ўчора з вячора")
        data.add("Вясёлых калядных сьвятаў")
        data.add("Зазьзяла зорачка над Бэтлеемам")
        data.add("Звон зьвініць")
        data.add("На шляху ў Бэтлеем")
        data.add("Неба і зямля")
        data.add("Нова радасьць стала")
        data.add("Ночка цiхая, зарыста")
        data.add("Ноччу сьвятой")
        data.add("Паказалась з неба яснасьць")
        data.add("Прыйдзіце да Збаўцы")
        data.add("Радасная вестка")
        data.add("У начную ціш")
        data.add("Учора зьвячора — засьвяціла зора")
        data.add("Ціхая ноч (пер. Н. Арсеньневай)")
        data.add("Ціхая ноч-2")
        data.add("Ціхая ноч-3")
        data.add("Прыйдзі, прыйдзі, Эмануэль (ХІХ ст.)")
        data.add("Прыйдзі, прыйдзі, Эмануэль (XII–ХVIII стст.)")
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