package by.carkva_gazeta.malitounik

class MineiaDay(val id: Long, val month: Int, val day: String, val title: String, val titleResource: String, var resourceViachernia: String, var resourceUtran: String, var resourceLiturgia: String, var resourceAbednica: String, var resourceVialikiaGadziny: String, var resourceViacherniaZLiturgia: String) : Comparable<MineiaDay> {
    override fun compareTo(other: MineiaDay): Int {
        if (this.id > other.id) {
            return 1
        } else if (this.id < other.id) {
            return -1
        }
        return 0
    }
}

