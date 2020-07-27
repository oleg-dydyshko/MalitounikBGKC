package by.carkva_gazeta.malitounik

import java.util.*

class MenuListData(val id: Int, val data: String, val type: String) : Comparable<MenuListData> {
    override fun compareTo(other: MenuListData): Int {
        return this.data.toLowerCase(Locale.getDefault()).compareTo(other.data.toLowerCase(Locale.getDefault()))
    }
}