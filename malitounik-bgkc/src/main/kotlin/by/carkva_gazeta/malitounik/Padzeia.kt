package by.carkva_gazeta.malitounik

import java.util.*

/**
 * Created by oleg on 10.5.17
 */
data class Padzeia(val padz: String, val dat: String, val tim: String, val paznic: Long, val vybtime: Int, val sec: String, val datK: String, val timK: String, val repit: Int, val count: String, val file: String, val color: Int) : Comparable<Padzeia> {
    override fun compareTo(other: Padzeia): Int {
        val days = dat.split(".").toTypedArray()
        val tims = tim.split(":").toTypedArray()
        val gc = GregorianCalendar(days[2].toInt(), days[1].toInt() - 1, days[0].toInt(), tims[0].toInt(), tims[1].toInt(), 0)
        val days2 = other.dat.split(".").toTypedArray()
        val tims2 = other.tim.split(":").toTypedArray()
        val gc2 = GregorianCalendar(days2[2].toInt(), days2[1].toInt() - 1, days2[0].toInt(), tims2[0].toInt(), tims2[1].toInt(), 0)
        val kon = gc2.timeInMillis
        val result = gc.timeInMillis
        if (result < kon) {
            return -1
        } else if (result > kon) {
            return 1
        }
        return 0
    }
}