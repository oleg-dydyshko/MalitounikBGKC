package by.carkva_gazeta.malitounik

data class VybranoeData(val id: Long, val resurs: String?, val data: String?) : Comparable<VybranoeData> {
    override fun compareTo(other: VybranoeData): Int {
        return if (MenuVybranoe.vybranoeSort == 1) this.data?.compareTo(other.data ?: "", true) ?: 0
        else 0
    }
}