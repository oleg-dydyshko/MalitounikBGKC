package by.carkva_gazeta.resources

import android.content.Context
import android.text.SpannableString
import androidx.core.text.isDigitsOnly
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.Malitounik
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

internal class ZmenyiaChastki {
    private var dzenNoch: Boolean = false
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

    fun dayOfYear() = arrayData[0][24]

    fun getYear() = arrayData[0][3].toInt()

    private fun chtenia(w: String, apostal: Int): String {
        var w1 = w
        val res = StringBuilder()
        w1 = w1.replace("\n", ";")
        w1 = MainActivity.removeZnakiAndSlovy(w1)
        val oldList = w1.split(";")
        val split = ArrayList<String>()
        for (i in oldList.indices) {
            if (oldList[i].trim().isNotEmpty()) split.add(oldList[i])
        }
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
            var kniga = -1
            if (zagl == "Мц") kniga = 0
            if (zagl == "Мк") kniga = 1
            if (zagl == "Лк") kniga = 2
            if (zagl == "Ян") kniga = 3
            if (zagl == "Дз") kniga = 4
            if (zagl == "Як") kniga = 5
            if (zagl == "1 Пт") kniga = 6
            if (zagl == "2 Пт") kniga = 7
            if (zagl == "1 Ян") kniga = 8
            if (zagl == "2 Ян") kniga = 9
            if (zagl == "3 Ян") kniga = 10
            if (zagl == "Юд") kniga = 11
            if (zagl == "Рым") kniga = 12
            if (zagl == "1 Кар") kniga = 13
            if (zagl == "2 Кар") kniga = 14
            if (zagl == "Гал") kniga = 15
            if (zagl == "Эф") kniga = 16
            if (zagl == "Плп") kniga = 17
            if (zagl == "Клс") kniga = 18
            if (zagl == "1 Фес") kniga = 19
            if (zagl == "2 Фес") kniga = 20
            if (zagl == "1 Цім") kniga = 21
            if (zagl == "2 Цім") kniga = 22
            if (zagl == "Ціт") kniga = 23
            if (zagl == "Піл") kniga = 24
            if (zagl == "Гбр") kniga = 25
            if (zagl == "Быц") kniga = 26
            if (zagl == "Высл") kniga = 27
            if (zagl == "Езк") kniga = 28
            if (zagl == "Вых") kniga = 29
            if (zagl == "Ёў") kniga = 30
            if (zagl == "Зах") kniga = 31
            if (zagl == "Ёіл") kniga = 32
            if (zagl == "Саф") kniga = 33
            if (zagl == "Іс") kniga = 34
            if (zagl == "Ер" || zagl == "Ярэм") kniga = 35
            if (zagl == "Дан") kniga = 36
            if (zagl == "Лікі") kniga = 37
            if (zagl == "Міх") kniga = 38
            if (zagl == "Дрг") kniga = 39
            if (zagl == "Мдр") kniga = 40
            if (zagl == "Мал") kniga = 41
            var inputStream: InputStream? = null
            val context = Malitounik.applicationContext()
            val resources = context.resources
            val k = Malitounik.applicationContext().getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val perevod = k.getString("perevodChytanne", DialogVybranoeBibleList.PEREVODSEMUXI) ?: DialogVybranoeBibleList.PEREVODSEMUXI
            var title = SpannableString("")
            val spln = ""
            when (kniga) {
                0 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian1)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan1)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin1)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_0, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                1 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian2)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan2)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin2)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_1, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                2 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian3)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan3)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin3)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_2, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                3 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian4)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan4)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin4)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_3, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                4 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian5)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan5)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin5)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_4, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                5 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian6)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan6)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin6)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_5, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                6 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian7)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan7)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin7)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_6, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                7 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian8)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan8)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin8)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_7, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                8 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian9)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan9)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin9)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_8, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                9 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian10)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan10)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin10)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_9, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                10 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian11)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan11)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin11)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_10, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                11 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian12)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan12)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin12)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_11, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                12 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian13)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan13)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin13)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_12, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                13 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian14)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan14)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin14)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_13, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                14 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian15)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan15)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin15)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_14, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                15 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian16)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan16)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin16)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_15, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                16 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian17)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan17)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin17)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_16, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                17 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian18)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan18)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin18)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_17, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                18 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian19)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan19)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin19)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_18, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                19 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian20)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan20)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin20)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_19, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                20 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian21)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan21)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin21)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_20, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                21 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian22)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan22)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin22)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_21, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                22 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian23)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan23)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin23)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_22, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                23 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian24)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan24)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin24)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_23, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                24 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian25)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan25)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin25)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_24, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                25 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblian26)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunan26)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskin26)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_25, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                26 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias1)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas1)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis1)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_26, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                27 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias20)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas20)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis20)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_27, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                28 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias26)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas26)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis26)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_28, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                29 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias2)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas2)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis2)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_29, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                30 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias18)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas18)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis18)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_30, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                31 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias38)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas38)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis38)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_31, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                32 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias29)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas29)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis29)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_32, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                33 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias36)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas36)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis36)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_33, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                34 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias23)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas23)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis23)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_34, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                35 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias24)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas24)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis24)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_35, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                36 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias27)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas27)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis27)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_36, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                37 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias4)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas4)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis4)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_37, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                38 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias33)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas33)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis33)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_38, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                39 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias5)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas5)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis5)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_39, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                40 -> {
                    inputStream = resources.openRawResource(R.raw.carniauskis42)
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_40, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }

                41 -> {
                    when (perevod) {
                        DialogVybranoeBibleList.PEREVODSEMUXI -> inputStream = resources.openRawResource(R.raw.biblias39)
                        DialogVybranoeBibleList.PEREVODBOKUNA -> inputStream = resources.openRawResource(R.raw.bokunas39)
                        DialogVybranoeBibleList.PEREVODCARNIAUSKI -> inputStream = resources.openRawResource(R.raw.carniauskis39)
                    }
                    title = if (e == 0) {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_41, spln, zaglavieName))
                    } else {
                        SpannableString(context.getString(by.carkva_gazeta.malitounik.R.string.chtinia_zag, spln.trim()))
                    }
                }
            }
            inputStream?.let { inputStream1 ->
                val isr = InputStreamReader(inputStream1)
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
                result = res.toString()
                if (polstixaA) {
                    val t2 = result.indexOf(knigaK)
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
        }
        return setIndexBiblii("$result<br>")
    }

    private fun setIndexBiblii(ssb: String): String {
        val list = ssb.split("\n")
        val result = StringBuilder()
        for (glava in list.indices) {
            val stext = list[glava]
            val t1 = list[glava].indexOf(" ")
            if (t1 != -1) {
                var subText = list[glava].substring(0, t1)
                if (subText.isDigitsOnly()) {
                    val color = if (dzenNoch) "<font color=\"#ff6666\">"
                    else "<font color=\"#d00505\">"
                    subText = subText.replace(subText, "$color<sup>$subText</sup></font>")
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
        val inputStream = Malitounik.applicationContext().resources.openRawResource(resource)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val builder = StringBuilder()
        var result: String
        reader.forEachLine {
            result = it
            if (dzenNoch) result = result.replace("#d00505", "#ff6666")
            builder.append(result)
        }
        isr.close()
        return builder.toString()
    }
}