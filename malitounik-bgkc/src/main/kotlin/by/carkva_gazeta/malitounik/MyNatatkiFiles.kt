package by.carkva_gazeta.malitounik

data class MyNatatkiFiles(val id: Long, private val lastModified: Long, var title: String) : Comparable<MyNatatkiFiles> {
    override fun compareTo(other: MyNatatkiFiles): Int {
        if (MenuNatatki.myNatatkiFilesSort == 1) return this.title.compareTo(other.title, true)
        if (MenuNatatki.myNatatkiFilesSort == 0) {
            if (this.lastModified < other.lastModified) {
                return 1
            } else if (this.lastModified > other.lastModified) {
                return -1
            }
        }
        return 0
    }
}