package by.carkva_gazeta.malitounik

import java.util.*

class MenuListDataSort : Comparator<MenuListData> {
    override fun compare(o1: MenuListData, o2: MenuListData): Int {
        return o1.data.toLowerCase(Locale.getDefault()).compareTo(o2.data.toLowerCase(Locale.getDefault()))
    }
}