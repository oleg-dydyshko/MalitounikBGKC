package by.carkva_gazeta.malitounik

import java.util.*

data class VybranoeData(val resurs: String, val data: String) : Comparable<VybranoeData> {
    override fun compareTo(other: VybranoeData): Int {
        return this.data.toLowerCase(Locale.getDefault()).compareTo(other.data.toLowerCase(Locale.getDefault()))
    }
}