package by.carkva_gazeta.malitounik

import android.content.Context
import android.content.SharedPreferences
import java.util.*

internal class MyNatatkiFilesSort(context: Context) : Comparator<MyNatatkiFiles> {
    private val chin: SharedPreferences = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    override fun compare(o1: MyNatatkiFiles, o2: MyNatatkiFiles): Int {
        val sort = chin.getInt("natatki_sort", 0)
        if (sort == 1) return o1.name.toLowerCase(Locale.getDefault()).compareTo(o2.name.toLowerCase(Locale.getDefault()))
        if (sort == 0) {
            if (o1.lastModified < o2.lastModified) {
                return 1
            } else if (o1.lastModified > o2.lastModified) {
                return -1
            }
        }
        return 0
    }
}