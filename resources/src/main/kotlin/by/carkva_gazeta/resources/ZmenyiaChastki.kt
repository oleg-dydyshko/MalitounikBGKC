package by.carkva_gazeta.resources

import androidx.collection.ArrayMap
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import by.carkva_gazeta.malitounik.MenuCaliandar
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

internal class ZmenyiaChastki {
    private var dzenNoch: Boolean = false
    private val data: ArrayMap<String, Int> = ArrayMap()
    private val arrayData = ArrayList<ArrayList<String>>()

    fun setDzenNoch(dzenNoch: Boolean) {
        this.dzenNoch = dzenNoch
    }

    fun sviatyia(): String {
        return if (arrayData[0][10] != "") arrayData[0][10]
        else arrayData[0][11]
    }

    fun sviatyiaView(apostal: Int) = chtenia(sviatyia(), apostal)

    fun zmenya(apostal: Int): String {
        val data = arrayData[0][9]
        return if (data == "" || data.contains(Malitounik.applicationContext().resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx))) "<em>" + Malitounik.applicationContext().resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx) + "</em><br><br>"
        else chtenia(arrayData[0][9], apostal)
    }

    fun setArrayData(arrayList: ArrayList<ArrayList<String>>) {
        arrayData.clear()
        arrayData.addAll(arrayList)
    }

    fun raznica() = arrayData[0][22].toInt()

    fun dayOfYear(): String {
        val c = GregorianCalendar()
        var day = arrayData[0][24].toInt()
        if (c.isLeapYear(arrayData[0][3].toInt()) && arrayData[0][24].toInt() > 59) {
            day--
        }
        return day.toString()
    }

    private fun chtenia(w: String, apostal: Int): String {
        var w1 = w
        val res = StringBuilder()
        w1 = MainActivity.removeZnakiAndSlovy(w1)
        val split = w1.split(";")
        if (split.size == 1) return "<em>" + Malitounik.applicationContext().resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx) + "</em><br><br>"
        var knigaN: String
        var knigaK = "0"
        var zaglnum = 0
        var chtenie = if (apostal == 1) 0 else 1
        if (w.contains("На ютрані")) chtenie++
        val zaglavie = split[chtenie].split(",")
        var zagl = ""
        var zaglavieName = ""
        var result = ""
        for (e in zaglavie.indices) {
            val zaglav = zaglavie[e].trim()
            val zag = zaglav.indexOf(" ", 2)
            val zag1 = zaglav.indexOf(".")
            val zag2 = zaglav.indexOf("-")
            val zag3 = zaglav.indexOf(".", zag1 + 1)
            val zagS = if (zag2 != -1) {
                zaglav.substring(0, zag2)
            } else {
                zaglav
            }
            var glav = false
            if (zag1 > zag2 && zag == -1) {
                glav = true
            } else if (zag != -1) {
                zagl = zaglav.substring(0, zag)
                val zaglavieName1 = split[chtenie].trim()
                zaglavieName = " " + zaglavieName1.substring(zag + 1)
                zaglnum = zaglav.substring(zag + 1, zag1).toInt()
            } else if (zag1 != -1) {
                zaglnum = zaglav.substring(0, zag1).toInt()
            }
            if (glav) {
                val zagS1 = zagS.indexOf(".")
                if (zagS1 == -1) {
                    knigaN = zagS
                } else {
                    zaglnum = zagS.substring(0, zagS1).toInt()
                    knigaN = zagS.substring(zagS1 + 1)
                }
            } else if (zag2 == -1) {
                knigaN = if (zag1 != -1) {
                    zaglav.substring(zag1 + 1)
                } else {
                    zaglav
                }
                knigaK = knigaN
            } else {
                knigaN = zaglav.substring(zag1 + 1, zag2)
            }
            if (glav) {
                knigaK = zaglav.substring(zag1 + 1)
            } else if (zag2 != -1) {
                knigaK = if (zag3 == -1) {
                    zaglav.substring(zag2 + 1)
                } else {
                    zaglav.substring(zag3 + 1)
                }
            }
            var polstixaA = false
            var polstixaB = false
            if (knigaK.contains("а", true)) {
                polstixaA = true
                knigaK = knigaK.replace("а", "", true)
            }
            if (knigaN.contains("б", true)) {
                polstixaB = true
                knigaN = knigaN.replace("б", "", true)
            }
            var kniga = 0 //if (zagl.equals("Ціт")) kniga = 0;
            if (zagl == "Езк") kniga = 1
            if (zagl == "Гбр") kniga = 2
            if (zagl == "Гал") kniga = 3
            if (zagl == "Высл") kniga = 4
            if (zagl == "Плп") kniga = 5
            if (zagl == "Лк") kniga = 6
            if (zagl == "Мк") kniga = 7
            if (zagl == "Юд") kniga = 8
            if (zagl == "1 Ян") kniga = 9
            if (zagl == "Мц") kniga = 10
            if (zagl == "2 Пт") kniga = 11
            if (zagl == "Ёіл") kniga = 12
            if (zagl == "Іс") kniga = 13
            if (zagl == "2 Ян") kniga = 14
            if (zagl == "Ёў") kniga = 15
            if (zagl == "Саф") kniga = 16
            if (zagl == "1 Пт") kniga = 17
            if (zagl == "Піл") kniga = 18
            if (zagl == "1 Кар") kniga = 19
            if (zagl == "Быц") kniga = 20
            if (zagl == "Зах") kniga = 21
            if (zagl == "Дз") kniga = 22
            if (zagl == "Вых") kniga = 23
            if (zagl == "Эф") kniga = 24
            if (zagl == "Рым") kniga = 25
            if (zagl == "Клс") kniga = 26
            if (zagl == "Ян") kniga = 27
            if (zagl == "3 Ян") kniga = 28
            if (zagl == "1 Фес") kniga = 29
            if (zagl == "2 Фес") kniga = 30
            if (zagl == "2 Кар") kniga = 31
            if (zagl == "2 Цім") kniga = 32
            if (zagl == "Як") kniga = 33
            if (zagl == "1 Цім") kniga = 34
            val r = Malitounik.applicationContext().resources
            val inputStream = r.openRawResource(data.valueAt(kniga))
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            val builder = StringBuilder()
            reader.use { buffer ->
                buffer.forEachLine {
                    var result1 = it.trim()
                    if (result1.contains("//")) {
                        val t1 = result1.indexOf("//")
                        result1 = result1.substring(0, t1).trim()
                    }
                    if (result1 != "") builder.append(result1).append("<br>\n")
                }
            }
            val split2 = builder.toString().split("===<br>")
            var spl: String
            var desK1: Int
            var desN: Int
            spl = split2[zaglnum].trim()
            desN = spl.indexOf("$knigaN.")
            if (e == 0) {
                res.append("<strong>").append(data.keyAt(kniga)).append(zaglavieName).append("</strong><br>")
            } else {
                res.append("[&#8230;]<br>")
            }
            if (knigaN == knigaK) {
                desK1 = desN
            } else {
                desK1 = spl.indexOf("$knigaK.")
                if (desK1 == -1) {
                    val splAll = spl.split("\n").size
                    desK1 = spl.indexOf("$splAll.")
                }
                if (zag3 != -1 || glav) {
                    val spl1 = split2[zaglnum].trim()
                    val spl2 = split2[zaglnum + 1].trim()
                    val des1 = spl1.length
                    desN = spl1.indexOf("$knigaN.")
                    desK1 = spl2.indexOf("$knigaK.")
                    var desN1: Int = spl2.indexOf((knigaK.toInt() + 1).toString().plus("."), desK1)
                    if (desN1 == -1) {
                        desN1 = spl1.length
                    }
                    desK1 = desN1 + des1
                    spl = spl1 + "\n" + spl2
                    zaglnum += 1
                }
            }
            val desK = spl.indexOf("\n", desK1)
            if (desK == -1) res.append(spl.substring(desN))
            else res.append(spl.substring(desN, desK))
            result = res.toString()
            if (polstixaA) {
                val t2 = result.indexOf("$knigaK.")
                val t3 = result.indexOf(".", t2)
                var t1 = result.indexOf(":", t2)
                if (t1 == -1) t1 = result.indexOf(";", t3 + 1)
                if (t1 == -1) t1 = result.indexOf(".", t3 + 1)
                if (t1 != -1) result = result.substring(0, t1 + 1) + "<s>" + result.substring(t1 + 1, result.length) + "</s>"
            }
            if (polstixaB) {
                val t2 = result.indexOf("\n")
                val textPol = result.substring(0, t2 + 1)
                val t4 = textPol.indexOf("</strong><br>")
                val t3 = textPol.indexOf(".", t4 + 13)
                var t1 = textPol.indexOf(":")
                if (t1 == -1) t1 = textPol.indexOf(";", t3 + 1)
                if (t1 == -1) t1 = textPol.indexOf(".", t3 + 1)
                if (t1 != -1) result = result.substring(0, t3 + 1) + "<s>" + result.substring(t3 + 1, t1 + 1) + "</s>" + result.substring(t1 + 1, result.length)
            }
        }
        return "$result<br>"
    }

    fun traparyIKandakiNiadzelnyia(chast: Int): String {
        if (arrayData[0][20] != "") {
            val w = arrayData[0][20]
            var result = ""
            if (w.contains("1")) {
                val res = readFile(R.raw.ton1)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("2")) {
                val res = readFile(R.raw.ton2)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("3")) {
                val res = readFile(R.raw.ton3)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("4")) {
                val res = readFile(R.raw.ton4)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("5")) {
                val res = readFile(R.raw.ton5)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("6")) {
                val res = readFile(R.raw.ton6)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("7")) {
                val res = readFile(R.raw.ton7)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (w.contains("8")) {
                val res = readFile(R.raw.ton8)
                if (chast == 1) {
                    val tfn = res.indexOf("TRAPARN")
                    val tfk = res.indexOf("TRAPARK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 2) {
                    val tfn = res.indexOf("PRAKIMENN")
                    val tfk = res.indexOf("PRAKIMENK")
                    result = res.substring(tfn, tfk)
                }
                if (chast == 3) {
                    val tfn = res.indexOf("ALILUIAN")
                    val tfk = res.indexOf("ALILUIAK")
                    result = res.substring(tfn, tfk)
                }
            }
            if (chast == 4) {
                result = readFile(R.raw.prichasnik)
            }
            result = result.replace("TRAPARN", "")
            result = result.replace("TRAPARK", "")
            result = result.replace("PRAKIMENN", "")
            result = result.replace("PRAKIMENK", "")
            result = result.replace("ALILUIAN", "")
            result = result.replace("ALILUIAK", "")
            return result
        }
        return ""
    }

    fun traparyIKandakiNaKognyDzen(day_of_week: Int, chast: Int): String {
        var result = ""
        if (day_of_week == 2) {
            val res = readFile(R.raw.ton1_budni)
            if (chast == 1) {
                val tfn = res.indexOf("TRAPARN")
                val tfk = res.indexOf("TRAPARK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 2) {
                val tfn = res.indexOf("PRAKIMENN")
                val tfk = res.indexOf("PRAKIMENK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 3) {
                val tfn = res.indexOf("ALILUIAN")
                val tfk = res.indexOf("ALILUIAK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 4) {
                val tfn = res.indexOf("PRICHASNIKN")
                val tfk = res.indexOf("PRICHASNIKK")
                result = res.substring(tfn, tfk)
            }
        }
        if (day_of_week == 3) {
            val res = readFile(R.raw.ton2_budni)
            if (chast == 1) {
                val tfn = res.indexOf("TRAPARN")
                val tfk = res.indexOf("TRAPARK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 2) {
                val tfn = res.indexOf("PRAKIMENN")
                val tfk = res.indexOf("PRAKIMENK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 3) {
                val tfn = res.indexOf("ALILUIAN")
                val tfk = res.indexOf("ALILUIAK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 4) {
                val tfn = res.indexOf("PRICHASNIKN")
                val tfk = res.indexOf("PRICHASNIKK")
                result = res.substring(tfn, tfk)
            }
        }
        if (day_of_week == 4) {
            val res = readFile(R.raw.ton3_budni)
            if (chast == 1) {
                val tfn = res.indexOf("TRAPARN")
                val tfk = res.indexOf("TRAPARK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 2) {
                val tfn = res.indexOf("PRAKIMENN")
                val tfk = res.indexOf("PRAKIMENK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 3) {
                val tfn = res.indexOf("ALILUIAN")
                val tfk = res.indexOf("ALILUIAK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 4) {
                val tfn = res.indexOf("PRICHASNIKN")
                val tfk = res.indexOf("PRICHASNIKK")
                result = res.substring(tfn, tfk)
            }
        }
        if (day_of_week == 5) {
            val res = readFile(R.raw.ton4_budni)
            if (chast == 1) {
                val tfn = res.indexOf("TRAPARN")
                val tfk = res.indexOf("TRAPARK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 2) {
                val tfn = res.indexOf("PRAKIMENN")
                val tfk = res.indexOf("PRAKIMENK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 3) {
                val tfn = res.indexOf("ALILUIAN")
                val tfk = res.indexOf("ALILUIAK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 4) {
                val tfn = res.indexOf("PRICHASNIKN")
                val tfk = res.indexOf("PRICHASNIKK")
                result = res.substring(tfn, tfk)
            }
        }
        if (day_of_week == 6) {
            val res = readFile(R.raw.ton5_budni)
            if (chast == 1) {
                val tfn = res.indexOf("TRAPARN")
                val tfk = res.indexOf("TRAPARK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 2) {
                val tfn = res.indexOf("PRAKIMENN")
                val tfk = res.indexOf("PRAKIMENK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 3) {
                val tfn = res.indexOf("ALILUIAN")
                val tfk = res.indexOf("ALILUIAK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 4) {
                val tfn = res.indexOf("PRICHASNIKN")
                val tfk = res.indexOf("PRICHASNIKK")
                result = res.substring(tfn, tfk)
            }
        }
        if (day_of_week == 7) {
            val res = readFile(R.raw.ton6_budni)
            if (chast == 1) {
                val tfn = res.indexOf("TRAPARN")
                val tfk = res.indexOf("TRAPARK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 2) {
                val tfn = res.indexOf("PRAKIMENN")
                val tfk = res.indexOf("PRAKIMENK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 3) {
                val tfn = res.indexOf("ALILUIAN")
                val tfk = res.indexOf("ALILUIAK")
                result = res.substring(tfn, tfk)
            }
            if (chast == 4) {
                val tfn = res.indexOf("PRICHASNIKN")
                val tfk = res.indexOf("PRICHASNIKK")
                result = res.substring(tfn, tfk)
            }
        }
        result = result.replace("TRAPARN", "")
        result = result.replace("TRAPARK", "")
        result = result.replace("PRAKIMENN", "")
        result = result.replace("PRAKIMENK", "")
        result = result.replace("ALILUIAN", "")
        result = result.replace("ALILUIAK", "")
        result = result.replace("PRICHASNIKN", "")
        result = result.replace("PRICHASNIKK", "")
        return result
    }

    private fun readFile(resource: Int): String {
        val inputStream = Malitounik.applicationContext().resources.openRawResource(resource)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = StringBuilder()
        var result: String
        reader.forEachLine {
            result = it
            if (dzenNoch) result = result.replace("#d00505", "#f44336")
            builder.append(result)
        }
        isr.close()
        return builder.toString()
    }

    init {
        data["Евангельле паводле Мацьвея"] = R.raw.biblian1
        data["Евангельле паводле Марка"] = R.raw.biblian2
        data["Евангельле паводле Лукаша"] = R.raw.biblian3
        data["Евангельле паводле Яна"] = R.raw.biblian4
        data["Дзеі Апосталаў"] = R.raw.biblian5
        data["Пасланьне Якуба"] = R.raw.biblian6
        data["1-е пасланьне Пятра"] = R.raw.biblian7
        data["2-е пасланьне Пятра"] = R.raw.biblian8
        data["1-е пасланьне Яна Багаслова"] = R.raw.biblian9
        data["2-е пасланьне Яна Багаслова"] = R.raw.biblian10
        data["3-е пасланьне Яна Багаслова"] = R.raw.biblian11
        data["Пасланьне Юды"] = R.raw.biblian12
        data["Пасланьне да Рымлянаў"] = R.raw.biblian13
        data["1-е пасланьне да Карынфянаў"] = R.raw.biblian14
        data["2-е пасланьне да Карынфянаў"] = R.raw.biblian15
        data["Пасланьне да Галятаў"] = R.raw.biblian16
        data["Пасланьне да Эфэсянаў"] = R.raw.biblian17
        data["Пасланьне да Філіпянаў"] = R.raw.biblian18
        data["Пасланьне да Каласянаў"] = R.raw.biblian19
        data["1-е пасланьне да Салунянаў"] = R.raw.biblian20
        data["2-е пасланьне да Салунянаў"] = R.raw.biblian21
        data["1-е пасланьне да Цімафея"] = R.raw.biblian22
        data["2-е пасланьне да Цімафея"] = R.raw.biblian23
        data["Пасланьне да Ціта"] = R.raw.biblian24
        data["Пасланьне да Філімона"] = R.raw.biblian25
        data["Пасланьне да Габрэяў"] = R.raw.biblian26
        data["Кніга Быцьця"] = R.raw.biblias1
        data["Кніга Выслоўяў Саламонавых"] = R.raw.biblias20
        data["Кніга прарока Езэкііля"] = R.raw.biblias26
        data["Кніга Выхаду"] = R.raw.biblias2
        data["Кніга Ёва"] = R.raw.biblias18
        data["Кніга прарока Захарыі"] = R.raw.biblias38
        data["Кніга прарока Ёіля"] = R.raw.biblias29
        data["Кніга прарока Сафона"] = R.raw.biblias36
        data["Кніга прарока Ісаі"] = R.raw.biblias23
        setArrayData(MenuCaliandar.getDataCalaindar(Calendar.getInstance()[Calendar.DATE]))
    }
}