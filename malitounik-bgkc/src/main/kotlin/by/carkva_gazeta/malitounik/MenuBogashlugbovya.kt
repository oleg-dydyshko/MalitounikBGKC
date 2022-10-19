package by.carkva_gazeta.malitounik

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.databinding.SimpleListItem2Binding

class MenuBogashlugbovya : BaseListFragment() {
    private var mLastClickTime: Long = 0
    private val data = ArrayList<MenuListData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            data.add(MenuListData("Боская Літургія між сьвятымі айца нашага Яна Залатавуснага", "lit_jan_zalat"))
            data.add(MenuListData("Боская Літургія ў Велікодны перыяд", "lit_jan_zalat_vielikodn"))
            data.add(MenuListData("Боская Літургія між сьвятымі айца нашага Базыля Вялікага", "lit_vasila_vialikaha"))
            data.add(MenuListData("Літургія раней асьвячаных дароў", "lit_ran_asv_dar"))
            data.add(MenuListData("Набажэнства ў гонар Маці Божай Нястомнай Дапамогі", "nabazenstva_maci_bozaj_niast_dap"))
            data.add(MenuListData("Малітвы пасьля сьвятога прычасьця", "1"))
            data.add(MenuListData("Ютрань нядзельная (у скароце)", "jutran_niadzelnaja"))
            data.add(MenuListData("Вячэрня", "2"))
            data.add(MenuListData("Абедніца", "abiednica"))
            data.add(MenuListData("Служба за памерлых — Малая паніхіда", "panichida_mal"))
            data.add(MenuListData("Трапары і кандакі нядзельныя васьмі тонаў", "3"))
            data.add(MenuListData("Мінэя месячная", "4"))
            data.add(MenuListData("Малебны канон Найсьвяцейшай Багародзіцы", "kanon_malebny_baharodzicy"))
            data.add(MenuListData("Вялікі пакаянны канон сьвятога Андрэя Крыцкага", "kanon_a_kryckaha"))
            data.add(MenuListData("Трыёдзь", "5"))
            data.add(MenuListData("Малебен сьвятым айцам нашым, роўным апосталам Кірылу і Мятоду, настаўнікам славянскім", "malebien_kiryla_miatod"))
            data.add(MenuListData("Служба за памерлых на кожны дзень тыдня", "sluzba_za_pamierlych_na_kozny_dzien_tydnia"))
            data.add(MenuListData("Мінэя агульная", "6"))
            data.sort()
            listAdapter = MenuListAdaprer(it as BaseActivity, data)
            val dzenNoch = it.getBaseDzenNoch()
            if (dzenNoch) {
                listView.setBackgroundResource(R.color.colorbackground_material_dark)
                listView.selector = ContextCompat.getDrawable(it, R.drawable.selector_dark)
            }
        }
        listView.isVerticalScrollBarEnabled = false
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        activity?.let {
            when (data[position].resurs) {
                "1" -> {
                    val intent = Intent(it, MalitvyPasliaPrychascia::class.java)
                    startActivity(intent)
                }
                "2" -> {
                    val intent = Intent(it, SubMenuBogashlugbovyaViachernia::class.java)
                    startActivity(intent)
                }
                "3" -> {
                    val intent = Intent(it, TonNiadzelny::class.java)
                    startActivity(intent)
                }
                "4" -> {
                    val intent = Intent(it, MineiaShodzennaia::class.java)
                    startActivity(intent)
                }
                "5" -> {
                    val intent = Intent(it, BogashlugbovyaTryjodz::class.java)
                    startActivity(intent)
                }
                "6" -> {
                    val intent = Intent(it, MineiaAgulnaia::class.java)
                    startActivity(intent)
                }
                else -> {
                    if (MainActivity.checkmoduleResources()) {
                        val intent = Intent()
                        intent.setClassName(it, MainActivity.BOGASHLUGBOVYA)
                        intent.putExtra("title", data[position].title)
                        intent.putExtra("resurs", data[position].resurs)
                        startActivity(intent)
                    } else {
                        val dadatak = DialogInstallDadatak()
                        dadatak.show(childFragmentManager, "dadatak")
                    }
                }
            }
        }
    }

    private class MenuListAdaprer(private val context: BaseActivity, private val data: ArrayList<MenuListData>) : ArrayAdapter<MenuListData>(context, R.layout.simple_list_item_2, R.id.label, data) {

        override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
            val rootView: View
            val viewHolder: ViewHolder
            if (mView == null) {
                val binding = SimpleListItem2Binding.inflate(LayoutInflater.from(context), parent, false)
                rootView = binding.root
                viewHolder = ViewHolder(binding.label)
                rootView.tag = viewHolder
            } else {
                rootView = mView
                viewHolder = rootView.tag as ViewHolder
            }
            val dzenNoch = context.getBaseDzenNoch()
            viewHolder.text.text = data[position].title
            if (dzenNoch) viewHolder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stiker_black, 0, 0, 0)
            return rootView
        }
    }

    private class ViewHolder(var text: TextView)
}
