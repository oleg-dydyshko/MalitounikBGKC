package by.carkva_gazeta.malitounik

import java.util.*

internal class VybranoeDataSort : Comparator<VybranoeData> {
    override fun compare(o1: VybranoeData, o2: VybranoeData): Int {
        return o1.data.toLowerCase(Locale.getDefault()).compareTo(o2.data.toLowerCase(Locale.getDefault()))
    }
}