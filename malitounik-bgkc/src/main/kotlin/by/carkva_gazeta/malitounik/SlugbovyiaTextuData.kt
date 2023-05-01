package by.carkva_gazeta.malitounik

data class SlugbovyiaTextuData(val day: Int, val title: String, val resource: String, val sluzba: Int, val pasxa: Boolean = false, val mineia: Int = SlugbovyiaTextu.MINEIA_MESIACHNAIA) : Comparable<SlugbovyiaTextuData> {
    override fun compareTo(other: SlugbovyiaTextuData): Int {
        if (this.day > other.day) {
            return 1
        } else if (this.day < other.day) {
            return -1
        }
        return 0
    }
}