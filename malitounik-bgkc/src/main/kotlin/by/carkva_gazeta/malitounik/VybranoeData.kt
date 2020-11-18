package by.carkva_gazeta.malitounik

import java.util.*

data class VybranoeData(val id: Long, val resurs: String, val data: String) : Comparable<VybranoeData> {
    override fun compareTo(other: VybranoeData): Int {
        return if (MenuVybranoe.vybranoeSort == 1) this.data.toLowerCase(Locale.getDefault()).compareTo(other.data.toLowerCase(Locale.getDefault()))
        else 0
    }
}