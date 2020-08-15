package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import java.io.File
import java.util.*

data class MyNatatkiFiles(val context: Context, private val lastModified: Long, val name: String, val file: File) : Comparable<MyNatatkiFiles> {
    private val chin: SharedPreferences = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    override fun compareTo(other: MyNatatkiFiles): Int {
        val sort = chin.getInt("natatki_sort", 0)
        if (sort == 1) return this.name.toLowerCase(Locale.getDefault()).compareTo(other.name.toLowerCase(Locale.getDefault()))
        if (sort == 0) {
            if (this.lastModified < other.lastModified) {
                return 1
            } else if (this.lastModified > other.lastModified) {
                return -1
            }
        }
        return 0
    }
}