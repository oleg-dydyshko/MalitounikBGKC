package by.carkva_gazeta.resources

import android.content.Context
import androidx.core.text.isDigitsOnly
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

interface ZmenyiaChastki : BibliaPerakvadBokuna, BibliaPerakvadCarniauski, BibliaPerakvadSemuxi {
    companion object {
        private val arrayData = ArrayList<ArrayList<String>>()
        private var novyZapavet = false
        private var perevod = DialogVybranoeBibleList.PEREVODSEMUXI
    }

    override fun addZakladka(color: Int, knigaBible: String, bible: String) {
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.addZakladka(color, knigaBible, bible)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.addZakladka(color, knigaBible, bible)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.addZakladka(color, knigaBible, bible)
        }
    }

    override fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getFileZavet(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getFileZavet(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getFileZavet(novyZapaviet, kniga)
            else -> File("")
        }
    }

    override fun getNamePerevod(): String {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getNamePerevod()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getNamePerevod()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getNamePerevod()
            else -> ""
        }
    }

    override fun getZakladki(): ArrayList<BibleZakladkiData> {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getZakladki()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getZakladki()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getZakladki()
            else -> ArrayList()
        }
    }

    override fun getTitlePerevod(): String {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getTitlePerevod()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getTitlePerevod()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getTitlePerevod()
            else -> ""
        }
    }

    override fun getSubTitlePerevod(): String {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSubTitlePerevod()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSubTitlePerevod()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSubTitlePerevod()
            else -> ""
        }
    }

    override fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getSpisKnig(novyZapaviet)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getSpisKnig(novyZapaviet)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getSpisKnig(novyZapaviet)
            else -> arrayOf("")
        }
    }

    override fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.getInputStream(novyZapaviet, kniga)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.getInputStream(novyZapaviet, kniga)
            else -> super<BibliaPerakvadSemuxi>.getInputStream(novyZapaviet, kniga)
        }
    }

    override fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.saveVydelenieZakladkiNtanki(novyZapaviet, kniga, glava, stix)
        }
    }

    override fun translatePsaltyr(psalm: Int, styx: Int, isUpdate: Boolean): Array<Int> {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.translatePsaltyr(psalm, styx, isUpdate)
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.translatePsaltyr(psalm, styx, isUpdate)
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.translatePsaltyr(psalm, styx, isUpdate)
            else -> arrayOf(1, 1)
        }
    }

    override fun isPsaltyrGreek(): Boolean {
        return when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> super<BibliaPerakvadSemuxi>.isPsaltyrGreek()
            DialogVybranoeBibleList.PEREVODBOKUNA -> super<BibliaPerakvadBokuna>.isPsaltyrGreek()
            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> super<BibliaPerakvadCarniauski>.isPsaltyrGreek()
            else -> true
        }
    }

    fun sviatyia(): String {
        return if (arrayData[0][10] != "") arrayData[0][10]
        else arrayData[0][11]
    }

    fun sviatyiaView(apostal: Int): String {
        this as BaseActivity
        val data = sviatyia()
        val chtenie = if (apostal == 1) 0 else 1
        return if (data == "" || data.contains(resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx))) "<em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx) + "</em><br><br>"
        else chtenia(data)[chtenie]
    }

    fun zmenya(apostal: Int): String {
        this as BaseActivity
        val data = arrayData[0][9]
        var chtenie = if (apostal == 1) 0 else 1
        if (arrayData[0][9].contains("На ютрані")) chtenie++
        return if (data == "" || data.contains(resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx))) "<em>" + resources.getString(by.carkva_gazeta.malitounik.R.string.no_danyx) + "</em><br><br>"
        else chtenia(data)[chtenie]
    }

    fun setArrayData(arrayList: ArrayList<ArrayList<String>>) {
        arrayData.clear()
        arrayData.addAll(arrayList)
    }

    fun raznica() = arrayData[0][22].toInt()

    fun dayOfYear() = arrayData[0][24]

    fun getYear() = arrayData[0][3].toInt()

    fun bibliaNew(chtenie: String): Int {
        var bible = 0
        if (chtenie == "Быт" || chtenie == "Быц") {
            bible = 0
            novyZapavet = false
        }
        if (chtenie == "Исх" || chtenie == "Вых") {
            bible = 1
            novyZapavet = false
        }
        if (chtenie == "Лев" || chtenie == "Ляв") {
            bible = 2
            novyZapavet = false
        }
        if (chtenie == "Чис" || chtenie == "Лікі") {
            bible = 3
            novyZapavet = false
        }
        if (chtenie == "Втор" || chtenie == "Дрг") {
            bible = 4
            novyZapavet = false
        }
        if (chtenie == "Нав") {
            bible = 5
            novyZapavet = false
        }
        if (chtenie == "Суд") {
            bible = 6
            novyZapavet = false
        }
        if (chtenie == "Руфь" || chtenie == "Рут") {
            bible = 7
            novyZapavet = false
        }
        if (chtenie == "1 Цар") {
            bible = 8
            novyZapavet = false
        }
        if (chtenie == "2 Цар") {
            bible = 9
            novyZapavet = false
        }
        if (chtenie == "3 Цар") {
            bible = 10
            novyZapavet = false
        }
        if (chtenie == "4 Цар") {
            bible = 11
            novyZapavet = false
        }
        if (chtenie == "1 Пар" || chtenie == "1 Лет") {
            bible = 12
            novyZapavet = false
        }
        if (chtenie == "2 Пар" || chtenie == "2 Лет") {
            bible = 13
            novyZapavet = false
        }
        if (chtenie == "1 Езд" || chtenie == "1 Эзд") {
            bible = 14
            novyZapavet = false
        }
        if (chtenie == "Неем" || chtenie == "Нээм") {
            bible = 15
            novyZapavet = false
        }
        if (chtenie == "2 Езд" || chtenie == "2 Эзд") {
            bible = 16
            novyZapavet = false
        }
        if (chtenie == "Тов" || chtenie == "Тав") {
            bible = 17
            novyZapavet = false
        }
        if (chtenie == "Иудифь" || chtenie == "Юдт") {
            bible = 18
            novyZapavet = false
        }
        if (chtenie == "Есф" || chtenie == "Эст") {
            bible = 19
            novyZapavet = false
        }
        if (chtenie == "Иов" || chtenie == "Ёва") {
            bible = 20
            novyZapavet = false
        }
        if (chtenie == "Пс") {
            bible = 21
            novyZapavet = false
        }
        if (chtenie == "Притч" || chtenie == "Высл") {
            bible = 22
            novyZapavet = false
        }
        if (chtenie == "Еккл" || chtenie == "Экл") {
            bible = 23
            novyZapavet = false
        }
        if (chtenie == "Песн" || chtenie == "Псн") {
            bible = 24
            novyZapavet = false
        }
        if (chtenie == "Прем" || chtenie == "Мдр") {
            bible = 25
            novyZapavet = false
        }
        if (chtenie == "Сир" || chtenie == "Сір") {
            bible = 26
            novyZapavet = false
        }
        if (chtenie == "Ис" || chtenie == "Іс") {
            bible = 27
            novyZapavet = false
        }
        if (chtenie == "Иер" || chtenie == "Ер") {
            bible = 28
            novyZapavet = false
        }
        if (chtenie == "Плач") {
            bible = 29
            novyZapavet = false
        }
        if (chtenie == "Посл Иер" || chtenie == "Пасл Ер" || chtenie == "Ярэм") {
            bible = 30
            novyZapavet = false
        }
        if (chtenie == "Вар" || chtenie == "Бар") {
            bible = 31
            novyZapavet = false
        }
        if (chtenie == "Иез" || chtenie == "Езк") {
            bible = 32
            novyZapavet = false
        }
        if (chtenie == "Дан") {
            bible = 33
            novyZapavet = false
        }
        if (chtenie == "Ос" || chtenie == "Ас") {
            bible = 34
            novyZapavet = false
        }
        if (chtenie == "Иоил" || chtenie == "Ёіл") {
            bible = 35
            novyZapavet = false
        }
        if (chtenie == "Ам") {
            bible = 36
            novyZapavet = false
        }
        if (chtenie == "Авд" || chtenie == "Аўдз") {
            bible = 37
            novyZapavet = false
        }
        if (chtenie == "Иона" || chtenie == "Ёны") {
            bible = 38
            novyZapavet = false
        }
        if (chtenie == "Мих" || chtenie == "Міх") {
            bible = 39
            novyZapavet = false
        }
        if (chtenie == "Наум" || chtenie == "Нвм") {
            bible = 40
            novyZapavet = false
        }
        if (chtenie == "Авв" || chtenie == "Абк") {
            bible = 41
            novyZapavet = false
        }
        if (chtenie == "Соф" || chtenie == "Саф") {
            bible = 42
            novyZapavet = false
        }
        if (chtenie == "Агг" || chtenie == "Аг") {
            bible = 43
            novyZapavet = false
        }
        if (chtenie == "Зах") {
            bible = 44
            novyZapavet = false
        }
        if (chtenie == "Мал") {
            bible = 45
            novyZapavet = false
        }
        if (chtenie == "1 Мак") {
            bible = 46
            novyZapavet = false
        }
        if (chtenie == "2 Мак") {
            bible = 47
            novyZapavet = false
        }
        if (chtenie == "3 Мак") {
            bible = 48
            novyZapavet = false
        }
        if (chtenie == "3 Езд" || chtenie == "3 Эзд") {
            bible = 49
            novyZapavet = false
        }
        if (chtenie == "Мф" || chtenie == "Мц") {
            bible = 0
            novyZapavet = true
        }
        if (chtenie == "Мк") {
            bible = 1
            novyZapavet = true
        }
        if (chtenie == "Лк") {
            bible = 2
            novyZapavet = true
        }
        if (chtenie == "Ин" || chtenie == "Ян") {
            bible = 3
            novyZapavet = true
        }
        if (chtenie == "Деян" || chtenie == "Дз") {
            bible = 4
            novyZapavet = true
        }
        if (chtenie == "Иак" || chtenie == "Як") {
            bible = 5
            novyZapavet = true
        }
        if (chtenie == "1 Пет" || chtenie == "1 Пт") {
            bible = 6
            novyZapavet = true
        }
        if (chtenie == "2 Пет" || chtenie == "2 Пт") {
            bible = 7
            novyZapavet = true
        }
        if (chtenie == "1 Ин" || chtenie == "1 Ян") {
            bible = 8
            novyZapavet = true
        }
        if (chtenie == "2 Ин" || chtenie == "2 Ян") {
            bible = 9
            novyZapavet = true
        }
        if (chtenie == "3 Ин" || chtenie == "3 Ян") {
            bible = 10
            novyZapavet = true
        }
        if (chtenie == "Иуд" || chtenie == "Юды") {
            bible = 11
            novyZapavet = true
        }
        if (chtenie == "Рим" || chtenie == "Рым") {
            bible = 12
            novyZapavet = true
        }
        if (chtenie == "1 Кор" || chtenie == "1 Кар") {
            bible = 13
            novyZapavet = true
        }
        if (chtenie == "2 Кор" || chtenie == "2 Кар") {
            bible = 14
            novyZapavet = true
        }
        if (chtenie == "Гал") {
            bible = 15
            novyZapavet = true
        }
        if (chtenie == "Еф" || chtenie == "Эф") {
            bible = 16
            novyZapavet = true
        }
        if (chtenie == "Флп" || chtenie == "Плп") {
            bible = 17
            novyZapavet = true
        }
        if (chtenie == "Кол" || chtenie == "Клс") {
            bible = 18
            novyZapavet = true
        }
        if (chtenie == "1 Фес") {
            bible = 19
            novyZapavet = true
        }
        if (chtenie == "2 Фес") {
            bible = 20
            novyZapavet = true
        }
        if (chtenie == "1 Тим" || chtenie == "1 Цім") {
            bible = 21
            novyZapavet = true
        }
        if (chtenie == "2 Тим" || chtenie == "2 Цім") {
            bible = 22
            novyZapavet = true
        }
        if (chtenie == "Тит" || chtenie == "Ціт") {
            bible = 23
            novyZapavet = true
        }
        if (chtenie == "Флм") {
            bible = 24
            novyZapavet = true
        }
        if (chtenie == "Евр" || chtenie == "Гбр") {
            bible = 25
            novyZapavet = true
        }
        if (chtenie == "Откр" || chtenie == "Адкр") {
            bible = 26
            novyZapavet = true
        }
        return bible
    }

    private fun findIndex(chtenie: String): Int {
        val bibliaNew = bibliaNew(chtenie)
        val list = getSpisKnig(novyZapavet)
        var indexListBible = -1
        for (e in list.indices) {
            val t1 = list[e].indexOf("#")
            val t2 = list[e].indexOf("#", t1 + 1)
            val indexBible = list[e].substring(t2 + 1).toInt()
            if (indexBible == bibliaNew) {
                indexListBible = e
                break
            }
        }
        return indexListBible
    }

    fun chtenia(w: String): ArrayList<String> {
        this as BaseActivity
        val k = getSharedPreferences("biblia", Context.MODE_PRIVATE)
        perevod = k.getString("perevodChytanne", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
        var w1 = w
        w1 = w1.replace("\n", ";")
        w1 = MainActivity.removeZnakiAndSlovy(w1)
        val split = w1.split(";")
        val list = ArrayList<String>()
        for (i in split.indices) {
            val res = StringBuilder()
            var knigaN: String
            var knigaK = "0"
            var zaglnum = 0
            val zaglavie = split[i].split(",")
            var zagl = ""
            var zaglavieName = ""
            var result = ""
            for (e in zaglavie.indices) {
                val perevodSave = perevod
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
                    val zaglavieName1 = split[i].trim()
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
                var indexBiblii = findIndex(zagl)
                if (indexBiblii == -1) {
                    perevod = DialogVybranoeBibleList.PEREVODCARNIAUSKI
                    indexBiblii = findIndex(zagl)
                }
                val inputStream = getInputStream(novyZapavet, indexBiblii)
                val title = if (e == 0) {
                    val spis = getSpisKnig(novyZapavet)[indexBiblii]
                    val t1 = spis.indexOf("#")
                    spis.substring(0, t1) + " $zaglavieName"
                } else {
                    "[&#8230;]"
                }
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
                desN = spl.indexOf(knigaN)
                if (e == 0) {
                    res.append("<strong>").append(title).append("</strong><br>\n")
                } else {
                    res.append("[&#8230;]<br>\n")
                }
                if (knigaN == knigaK) {
                    desK1 = desN
                } else {
                    desK1 = spl.indexOf(knigaK)
                    if (desK1 == -1) {
                        val splAll = spl.split("\n").size
                        desK1 = spl.indexOf("$splAll")
                    }
                    if (zag3 != -1 || glav) {
                        val spl1 = split2[zaglnum].trim()
                        val spl2 = split2[zaglnum + 1].trim()
                        val des1 = spl1.length
                        desN = spl1.indexOf(knigaN)
                        desK1 = spl2.indexOf(knigaK)
                        var desN1: Int = spl2.indexOf((knigaK.toInt() + 1).toString(), desK1)
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
                var result2 = res.toString()
                if (polstixaA) {
                    val t2 = result2.indexOf(knigaK)
                    val t3 = result2.indexOf(".", t2)
                    var t1 = result2.indexOf(":", t2)
                    if (t1 == -1) t1 = result2.indexOf(";", t3 + 1)
                    if (t1 == -1) t1 = result2.indexOf(".", t3 + 1)
                    if (t1 != -1) result2 = result2.substring(0, t1 + 1) + "<s>" + result2.substring(t1 + 1, result2.length) + "</s>"
                }
                if (polstixaB) {
                    val t2 = result2.indexOf("\n")
                    val textPol = result2.substring(0, t2 + 1)
                    val t4 = textPol.indexOf("</strong><br>")
                    val t3 = textPol.indexOf(".", t4 + 13)
                    var t1 = textPol.indexOf(":")
                    if (t1 == -1) t1 = textPol.indexOf(";", t3 + 1)
                    if (t1 == -1) t1 = textPol.indexOf(".", t3 + 1)
                    if (t1 != -1) result2 = result2.substring(0, t3 + 1) + "<s>" + result2.substring(t3 + 1, t1 + 1) + "</s>" + result2.substring(t1 + 1, result2.length)
                }
                result = result2
                perevod = perevodSave
            }
            list.add(setIndexBiblii("$result<br>"))
        }
        return list
    }

    private fun setIndexBiblii(ssb: String): String {
        this as BaseActivity
        val list = ssb.split("\n")
        val result = StringBuilder()
        for (glava in list.indices) {
            val stext = list[glava]
            val t1 = list[glava].indexOf(" ")
            if (t1 != -1) {
                var subText = list[glava].substring(0, t1)
                if (subText.isDigitsOnly()) {
                    val color = if (getBaseDzenNoch()) "<font color=\"#ff6666\">"
                    else "<font color=\"#d00505\">"
                    subText = subText.replace(subText, "$color$subText.</font>")
                    result.append(subText).append(stext.substring(t1)).append("\n")
                } else {
                    result.append(stext).append("\n")
                }
            }
        }
        return result.toString()
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

    fun traparyIKandakiNaKognyDzen(dayOfWeek: Int, chast: Int): String {
        var result = ""
        if (raznica() in 0..41) return result
        if (dayOfWeek == 2) {
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
        if (dayOfWeek == 3) {
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
        if (dayOfWeek == 4) {
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
        if (dayOfWeek == 5) {
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
        if (dayOfWeek == 6) {
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
        if (dayOfWeek == 7) {
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
        this as BaseActivity
        val inputStream = resources.openRawResource(resource)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = StringBuilder()
        var result: String
        reader.forEachLine {
            result = it
            if (getBaseDzenNoch()) result = result.replace("#d00505", "#ff6666")
            builder.append(result)
        }
        isr.close()
        return builder.toString()
    }
}