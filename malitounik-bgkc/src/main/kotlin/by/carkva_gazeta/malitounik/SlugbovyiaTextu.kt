package by.carkva_gazeta.malitounik

import android.app.Activity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class SlugbovyiaTextu {
    private val dat12 = ArrayList<SlugbovyiaTextuData>()
    private val dat13 = ArrayList<SlugbovyiaTextuData>()
    private val dat14 = ArrayList<SlugbovyiaTextuData>()
    private val dat15 = ArrayList<SlugbovyiaTextuData>()
    private val dat16 = ArrayList<SlugbovyiaTextuData>()
    private val dat17 = ArrayList<SlugbovyiaTextuData>()
    private val dat18 = ArrayList<SlugbovyiaTextuData>()
    private val opisanieSviat = ArrayList<ArrayList<String>>()
    private var loadOpisanieSviatJob: Job? = null

    init {
        dat12.add(SlugbovyiaTextuData(-49, "Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya12_1"))
        dat12.add(SlugbovyiaTextuData(-48, "Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya12_2"))
        dat12.add(SlugbovyiaTextuData(-47, "Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya12_3"))
        dat12.add(SlugbovyiaTextuData(-46, "Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya12_4"))
        dat12.add(SlugbovyiaTextuData(-45, "Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya12_5"))
        dat12.add(SlugbovyiaTextuData(-44, "Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya12_6"))
        dat12.add(SlugbovyiaTextuData(-43, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya12_7"))
        dat12.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya12_8", utran = true))
        dat12.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Літургія сьвятoга Васіля Вялiкага", "bogashlugbovya12_9", liturgia = true))

        dat13.add(SlugbovyiaTextuData(-42, "1-ая нядзеля посту ўвечары", "bogashlugbovya13_1"))
        dat13.add(SlugbovyiaTextuData(-41, "Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya13_2"))
        dat13.add(SlugbovyiaTextuData(-40, "Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya13_3"))
        dat13.add(SlugbovyiaTextuData(-39, "Серада 2-га тыдня посту ўвечары", "bogashlugbovya13_4"))
        dat13.add(SlugbovyiaTextuData(-38, "Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya13_5"))
        dat13.add(SlugbovyiaTextuData(-37, "Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya13_6"))
        dat13.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya13_7", utran = true))
        dat13.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya13_8", liturgia = true))

        dat14.add(SlugbovyiaTextuData(-35, "2-ая нядзеля посту ўвечары", "bogashlugbovya14_1"))
        dat14.add(SlugbovyiaTextuData(-34, "Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya14_2"))
        dat14.add(SlugbovyiaTextuData(-33, "Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya14_3"))
        dat14.add(SlugbovyiaTextuData(-32, "Серада 3-га тыдня посту ўвечары", "bogashlugbovya14_4"))
        dat14.add(SlugbovyiaTextuData(-31, "Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya14_5"))
        dat14.add(SlugbovyiaTextuData(-30, "Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya14_6"))
        dat14.add(SlugbovyiaTextuData(-29, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya14_7"))
        dat14.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya14_8", utran = true))
        dat14.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya14_9", liturgia = true))

        dat15.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту ўвечары", "bogashlugbovya15_1"))
        dat15.add(SlugbovyiaTextuData(-27, "Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya15_2"))
        dat15.add(SlugbovyiaTextuData(-26, "Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya15_3"))
        dat15.add(SlugbovyiaTextuData(-25, "Серада 4-га тыдня посту ўвечары", "bogashlugbovya15_4"))
        dat15.add(SlugbovyiaTextuData(-24, "Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya15_5"))
        dat15.add(SlugbovyiaTextuData(-23, "Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya15_6"))
        dat15.add(SlugbovyiaTextuData(-22, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya15_7"))
        dat15.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya15_8", utran = true))
        dat15.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya15_9", liturgia = true))

        dat16.add(SlugbovyiaTextuData(-21, "4-ая нядзеля посту ўвечары", "bogashlugbovya16_1"))
        dat16.add(SlugbovyiaTextuData(-20, "Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya16_2"))
        dat16.add(SlugbovyiaTextuData(-19, "Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya16_3"))
        dat16.add(SlugbovyiaTextuData(-18, "Серада 5-га тыдня посту ўвечары", "bogashlugbovya16_4"))
        dat16.add(SlugbovyiaTextuData(-17, "Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya16_5"))
        dat16.add(SlugbovyiaTextuData(-16, "Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya16_6"))
        dat16.add(SlugbovyiaTextuData(-15, "Субота Акафісту Ютрань", "bogashlugbovya16_7", utran = true))
        dat16.add(SlugbovyiaTextuData(-15, "Літургія ў суботу Акафісту", "bogashlugbovya16_8", liturgia = true))
        dat16.add(SlugbovyiaTextuData(-15, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya16_9"))
        dat16.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya16_10", utran = true))
        dat16.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya16_11", liturgia = true))

        dat17.add(SlugbovyiaTextuData(-14, "5-ая нядзеля посту ўвечары", "bogashlugbovya17_1"))
        dat17.add(SlugbovyiaTextuData(-13, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya17_2"))
        dat17.add(SlugbovyiaTextuData(-12, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya17_3"))
        dat17.add(SlugbovyiaTextuData(-11, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya17_4"))
        dat17.add(SlugbovyiaTextuData(-10, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya17_5"))
        dat17.add(SlugbovyiaTextuData(-9, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya17_6"))
        dat17.add(SlugbovyiaTextuData(-8, "Субота Лазара Ютрань", "bogashlugbovya17_7", utran = true))
        dat17.add(SlugbovyiaTextuData(-7, "Літургія", "bogashlugbovya17_8", liturgia = true))

        dat18.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха) - Літургія", "zmenyia_chastki_tamash", liturgia = true))
        dat18.add(SlugbovyiaTextuData(14, "Нядзеля міраносіцаў – Літургія", "zmenyia_chastki_miranosicay", liturgia = true))
        dat18.add(SlugbovyiaTextuData(28, "Нядзеля Самаранкі - Літургія", "zmenyia_chastki_samaranki", liturgia = true))
    }

    fun getTydzen1() = dat12

    fun getTydzen2() = dat13

    fun getTydzen3() = dat14

    fun getTydzen4() = dat15

    fun getTydzen5() = dat16

    fun getTydzen6() = dat17

    fun getResource(day: Int, utran: Boolean = false, liturgia: Boolean = false): String {
        var resource = "0"
        dat12.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat13.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat14.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat15.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat16.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat17.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat18.forEach {
            if (day == it.day) {
                if (!utran && !liturgia) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        return resource
    }

    fun getTitle(resource: String): String {
        dat12.forEach {
            if (resource == it.resource) return it.title
        }
        dat13.forEach {
            if (resource == it.resource) return it.title
        }
        dat14.forEach {
            if (resource == it.resource) return it.title
        }
        dat15.forEach {
            if (resource == it.resource) return it.title
        }
        dat16.forEach {
            if (resource == it.resource) return it.title
        }
        dat17.forEach {
            if (resource == it.resource) return it.title
        }
        dat18.forEach {
            if (resource == it.resource) return it.title
        }
        return ""
    }

    fun loadOpisanieSviat(activity: Activity) {
        if (opisanieSviat.size == 0 && loadOpisanieSviatJob?.isActive != true) {
            val fileOpisanieSviat = File("${activity.filesDir}/opisanie_sviat.json")
            if (!fileOpisanieSviat.exists()) {
                if (MainActivity.isNetworkAvailable(activity)) {
                    loadOpisanieSviatJob = CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val mURL = URL("https://carkva-gazeta.by/opisanie_sviat.json")
                                val conections = mURL.openConnection() as HttpURLConnection
                                if (conections.responseCode == 200) {
                                    fileOpisanieSviat.writer().use {
                                        it.write(mURL.readText())
                                    }
                                }
                            } catch (e: Throwable) {
                            }
                        }
                        try {
                            val builder = fileOpisanieSviat.readText()
                            val gson = Gson()
                            val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                            opisanieSviat.addAll(gson.fromJson(builder, type))
                        } catch (t: Throwable) {
                            fileOpisanieSviat.delete()
                        }
                    }
                }
            } else {
                try {
                    val builder = fileOpisanieSviat.readText()
                    val gson = Gson()
                    val type = object : TypeToken<ArrayList<ArrayList<String>>>() {}.type
                    opisanieSviat.addAll(gson.fromJson(builder, type))
                } catch (t: Throwable) {
                    fileOpisanieSviat.delete()
                }
            }
        }
    }

    fun getTitleOpisanieSviat(day: Int, mun: Int): String {
        var title = ""
        opisanieSviat.forEach {
            if (day == it[0].toInt() && mun == it[1].toInt()) {
                if (it[2] != "") {
                    val trapar = it[2]
                    val t1 = trapar.indexOf("<strong>")
                    val t2 = trapar.indexOf("</strong>")
                    if (t1 != -1 && t2 != -1) title = trapar.substring(t1 + 8, t2)
                }
            }
        }
        return title
    }

    fun checkUtran(day: Int, mun: Int): Boolean {
        opisanieSviat.forEach {
            if (day == it[0].toInt() && mun == it[1].toInt()) {
                if (it[3] != "") return true
            }
        }
        return false
    }

    fun checkUtran(day: Int): Boolean {
        dat12.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        dat13.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        dat14.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        dat15.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        dat16.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        dat17.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        dat18.forEach {
            if (day == it.day) {
                if (it.utran) return true
            }
        }
        return false
    }

    fun checkLiturgia(day: Int, mun: Int): Boolean {
        opisanieSviat.forEach {
            if (day == it[0].toInt() && mun == it[1].toInt()) {
                if (it[4] != "") return true
            }
        }
        return false
    }

    fun checkLiturgia(day: Int): Boolean {
        dat12.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        dat13.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        dat14.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        dat15.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        dat16.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        dat17.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        dat18.forEach {
            if (day == it.day) {
                if (it.liturgia) return true
            }
        }
        return false
    }

    fun checkViachernia(day: Int, mun: Int): Boolean {
        opisanieSviat.forEach {
            if (day == it[0].toInt() && mun == it[1].toInt()) {
                if (it[5] != "") return true
            }
        }
        return false
    }

    fun checkViachernia(day: Int): Boolean {
        dat12.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat13.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat14.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat15.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat16.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat17.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat18.forEach {
            if (day == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        return false
    }

    fun onDestroy() {
        loadOpisanieSviatJob?.cancel()
    }
}