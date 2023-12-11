package by.carkva_gazeta.malitounik

data class MenuListData(val title: String, val resurs: String) : Comparable<MenuListData> {
    override fun compareTo(other: MenuListData): Int {
        return this.title.compareTo(other.title, true)
    }
}