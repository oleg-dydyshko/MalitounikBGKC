package by.carkva_gazeta.malitounik

data class SlugbovyiaTextuData(val day: Int, val title: String, val resource: String, val utran: Boolean = false, val liturgia: Boolean = false, val abednica: Boolean = false, val vialikiaGadziny: Boolean = false, val pasxa: Boolean = false) : Comparable<SlugbovyiaTextuData> {
    override fun compareTo(other: SlugbovyiaTextuData): Int {
        if (this.day > other.day) {
            return 1
        } else if (this.day < other.day) {
            return -1
        }
        return 0
    }
}