package by.carkva_gazeta.malitounik

import java.util.*

data class MyNatatkiFiles(val id: Long, private val lastModified: Long, var title: String) : Comparable<MyNatatkiFiles> {
    override fun compareTo(other: MyNatatkiFiles): Int {
        if (MenuNatatki.myNatatkiFilesSort == 1) return this.title.toLowerCase(Locale.getDefault()).compareTo(other.title.toLowerCase(Locale.getDefault()))
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