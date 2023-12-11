package by.carkva_gazeta.malitounik

data class Prazdniki(val dayOfYear: Int, val date: Int, val month: Int, val svaity: Int, val opisanie: String, val opisanieData: String) : Comparable<Prazdniki> {
    override fun compareTo(other: Prazdniki): Int {
        if (dayOfYear < other.dayOfYear) {
            return -1
        } else if (dayOfYear > other.dayOfYear) {
            return 1
        }
        return 0
    }

}