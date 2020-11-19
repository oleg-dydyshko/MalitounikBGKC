package by.carkva_gazeta.malitounik

internal data class Prazdniki(val data: Int, val opisanie: String, val opisanieData: String) : Comparable<Prazdniki> {
    override fun compareTo(other: Prazdniki): Int {
        if (data < other.data) {
            return -1
        } else if (data > other.data) {
            return 1
        }
        return 0
    }

}