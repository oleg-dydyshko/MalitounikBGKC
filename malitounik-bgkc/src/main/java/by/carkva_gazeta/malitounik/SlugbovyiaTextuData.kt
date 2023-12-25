package by.carkva_gazeta.malitounik

import android.content.Context.MODE_PRIVATE

data class SlugbovyiaTextuData(val day: Int, val title: String, val resource: String, val sluzba: Int, val pasxa: Boolean = false, val mineia: Int = SlugbovyiaTextu.MINEIA_MESIACHNAIA) : Comparable<SlugbovyiaTextuData> {
    override fun compareTo(other: SlugbovyiaTextuData): Int {
        val k = Malitounik.applicationContext().getSharedPreferences("biblia", MODE_PRIVATE)
        val sortMineiaList = k.getInt("sortMineiaList", 0)
        if (sortMineiaList == 1) {
            if (this.sluzba > other.sluzba) {
                return 1
            } else if (this.sluzba < other.sluzba) {
                return -1
            }
        } else {
            if (this.day > other.day) {
                return 1
            } else if (this.day < other.day) {
                return -1
            }
        }
        return 0
    }
}