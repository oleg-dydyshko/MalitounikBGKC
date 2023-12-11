package by.carkva_gazeta.malitounik

import android.content.Context

data class MyNatatkiFiles(val id: Long, private val lastModified: Long, var title: String?) : Comparable<MyNatatkiFiles> {
    override fun compareTo(other: MyNatatkiFiles): Int {
        val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val myNatatkiFilesSort = k.getInt("natatki_sort", 0)
        if (myNatatkiFilesSort == 1) return this.title?.compareTo(other.title ?: "", true) ?: 0
        if (myNatatkiFilesSort == 2) {
            if (this.lastModified < other.lastModified) {
                return 1
            } else if (this.lastModified > other.lastModified) {
                return -1
            }
        }
        return 0
    }
}