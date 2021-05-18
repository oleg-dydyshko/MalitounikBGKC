package by.carkva_gazeta.malitounik

data class MenuListData(val data: String, val type: String) : Comparable<MenuListData> {
    override fun compareTo(other: MenuListData): Int {
        return this.data.compareTo(other.data, true)
    }
}