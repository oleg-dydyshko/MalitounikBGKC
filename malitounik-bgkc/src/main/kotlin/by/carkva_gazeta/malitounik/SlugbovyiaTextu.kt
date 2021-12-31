package by.carkva_gazeta.malitounik

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class SlugbovyiaTextu {
    private val dat12 = ArrayList<SlugbovyiaTextuData>()
    private val dat13 = ArrayList<SlugbovyiaTextuData>()
    private val dat14 = ArrayList<SlugbovyiaTextuData>()
    private val dat15 = ArrayList<SlugbovyiaTextuData>()
    private val dat16 = ArrayList<SlugbovyiaTextuData>()
    private val dat17 = ArrayList<SlugbovyiaTextuData>()
    private val dat18 = ArrayList<SlugbovyiaTextuData>()
    private val dat19 = ArrayList<SlugbovyiaTextuData>()
    private val dat20 = ArrayList<SlugbovyiaTextuData>()
    private val opisanieSviat = ArrayList<ArrayList<String>>()
    private val piarliny = ArrayList<ArrayList<String>>()
    private var loadOpisanieSviatJob: Job? = null
    private var loadPiarlinyJob: Job? = null

    init {
        dat12.add(SlugbovyiaTextuData(-49, "Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya12_1", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-48, "Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya12_2", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-47, "Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya12_3", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-46, "Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya12_4", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-45, "Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya12_5", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-44, "Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya12_6", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-43, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya12_7", pasxa = true))
        dat12.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya12_8", utran = true, pasxa = true))
        dat12.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Літургія сьвятoга Васіля Вялiкага", "bogashlugbovya12_9", liturgia = true, pasxa = true))

        dat13.add(SlugbovyiaTextuData(-42, "1-ая нядзеля посту ўвечары", "bogashlugbovya13_1", pasxa = true))
        dat13.add(SlugbovyiaTextuData(-41, "Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya13_2", pasxa = true))
        dat13.add(SlugbovyiaTextuData(-40, "Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya13_3", pasxa = true))
        dat13.add(SlugbovyiaTextuData(-39, "Серада 2-га тыдня посту ўвечары", "bogashlugbovya13_4", pasxa = true))
        dat13.add(SlugbovyiaTextuData(-38, "Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya13_5", pasxa = true))
        dat13.add(SlugbovyiaTextuData(-37, "Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya13_6", pasxa = true))
        dat13.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya13_7", utran = true, pasxa = true))
        dat13.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya13_8", liturgia = true, pasxa = true))

        dat14.add(SlugbovyiaTextuData(-35, "2-ая нядзеля посту ўвечары", "bogashlugbovya14_1", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-34, "Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya14_2", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-33, "Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya14_3", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-32, "Серада 3-га тыдня посту ўвечары", "bogashlugbovya14_4", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-31, "Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya14_5", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-30, "Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya14_6", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-29, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya14_7", pasxa = true))
        dat14.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya14_8", utran = true, pasxa = true))
        dat14.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya14_9", liturgia = true, pasxa = true))

        dat15.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту ўвечары", "bogashlugbovya15_1", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-27, "Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya15_2", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-26, "Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya15_3", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-25, "Серада 4-га тыдня посту ўвечары", "bogashlugbovya15_4", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-24, "Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya15_5", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-23, "Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya15_6", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-22, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya15_7", pasxa = true))
        dat15.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya15_8", utran = true, pasxa = true))
        dat15.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya15_9", liturgia = true, pasxa = true))

        dat16.add(SlugbovyiaTextuData(-21, "4-ая нядзеля посту ўвечары", "bogashlugbovya16_1", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-20, "Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya16_2", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-19, "Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya16_3", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-18, "Серада 5-га тыдня посту ўвечары", "bogashlugbovya16_4", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-17, "Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya16_5", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-16, "Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya16_6", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-15, "Субота Акафісту Ютрань", "bogashlugbovya16_7", utran = true, pasxa = true))
        dat16.add(SlugbovyiaTextuData(-15, "Літургія ў суботу Акафісту", "bogashlugbovya16_8", liturgia = true, pasxa = true))
        dat16.add(SlugbovyiaTextuData(-15, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya16_9", pasxa = true))
        dat16.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya16_10", utran = true, pasxa = true))
        dat16.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya16_11", liturgia = true, pasxa = true))

        dat17.add(SlugbovyiaTextuData(-14, "5-ая нядзеля посту ўвечары", "bogashlugbovya17_1", pasxa = true))
        dat17.add(SlugbovyiaTextuData(-13, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya17_2", pasxa = true))
        dat17.add(SlugbovyiaTextuData(-12, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya17_3", pasxa = true))
        dat17.add(SlugbovyiaTextuData(-11, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya17_4", pasxa = true))
        dat17.add(SlugbovyiaTextuData(-10, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya17_5", pasxa = true))
        dat17.add(SlugbovyiaTextuData(-9, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya17_6", pasxa = true))
        dat17.add(SlugbovyiaTextuData(-8, "Субота Лазара Ютрань", "bogashlugbovya17_7", utran = true, pasxa = true))
        dat17.add(SlugbovyiaTextuData(-7, "Літургія", "bogashlugbovya17_8", liturgia = true, pasxa = true))

        dat18.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха) - Літургія", "zmenyia_chastki_tamash", liturgia = true, pasxa = true))
        dat18.add(SlugbovyiaTextuData(14, "Нядзеля міраносіцаў - Літургія", "zmenyia_chastki_miranosicay", liturgia = true, pasxa = true))
        dat18.add(SlugbovyiaTextuData(28, "Нядзеля Самаранкі - Літургія", "zmenyia_chastki_samaranki", liturgia = true, pasxa = true))
        dat18.add(SlugbovyiaTextuData(35, "Нядзеля Сьлепанароджанага - Літургія", "zmenyia_chastki_slepanarodz", liturgia = true, pasxa = true))

        dat19.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "ju_8_11", utran = true))
        dat19.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "v_8_11"))
        dat19.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "l_8_11", liturgia = true))
        dat19.add(SlugbovyiaTextuData(1000, "Айцы першых 6-ці Ўсяленскіх сабораў", "l_ajcy_6_saborau", liturgia = true))
        dat19.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "ju_12_11", utran = true))
        dat19.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "v_12_11"))
        dat19.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "l_12_11", liturgia = true))
        dat19.add(SlugbovyiaTextuData(317, "Між сьвятымі айца нашага Яна Залатавуснага", "v_13_11"))
        dat19.add(SlugbovyiaTextuData(317, "Між сьвятымі айца нашага Яна Залатавуснага", "l_13_11", liturgia = true))
        dat19.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "ju_6_12", utran = true))
        dat19.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "v_6_12"))
        dat19.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "l_6_12", liturgia = true))
        dat19.add(SlugbovyiaTextuData(360, "Сабор Найсьвяцейшай Багародзіцы", "l_12_26_sabor_baharodzicy", liturgia = true))
        dat19.add(SlugbovyiaTextuData(1, "Малебен на Новы год", "maleben_new_year_01_01", liturgia = true))

        dat20.add(SlugbovyiaTextuData(218, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "zmenyia_chastki_pieramianiennie", liturgia = true))
        dat20.add(SlugbovyiaTextuData(1, "Абрэзаньне Гасподняе; сьвятаначальніка Васіля Вялікага, архібіск. Кесарыі Кападакійскай", "viachernia_mineia_sviatochnaia1"))
        dat20.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "viachernia_mineia_sviatochnaia4"))
        dat20.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2"))
        dat20.add(SlugbovyiaTextuData(3, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2"))
        dat20.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2"))
        dat20.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня", "viachernia_mineia_sviatochnaia3"))
        dat20.add(SlugbovyiaTextuData(7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "viachernia_mineia_sviatochnaia5"))
        dat20.add(SlugbovyiaTextuData(16, "Пакланеньне кайданам апостала Пятра", "viachernia_mineia_sviatochnaia6"))
        dat20.add(SlugbovyiaTextuData(30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "viachernia_mineia_sviatochnaia7"))
        dat20.add(SlugbovyiaTextuData(49, "ТРОЙЦА (СЁМУХА)", "ju_trojca_mineia_sviatochnaia", utran = true, pasxa = true))
        dat20.add(SlugbovyiaTextuData(49, "ТРОЙЦА (СЁМУХА)", "l_trojca_mineia_sviatochnaia", liturgia = true, pasxa = true))
        dat20.add(SlugbovyiaTextuData(49, "ТРОЙЦА (СЁМУХА)", "v_trojca_mineia_sviatochnaia", pasxa = true))
        dat20.add(SlugbovyiaTextuData(39, "УЗЬНЯСЕНЬНЕ ГОСПАДА НАШАГА ІСУСА ХРЫСТА", "ju_uzniasenne_mineia_sviatochnaia", utran = true, pasxa = true))
        dat20.add(SlugbovyiaTextuData(39, "УЗЬНЯСЕНЬНЕ ГОСПАДА НАШАГА ІСУСА ХРЫСТА", "l_uzniasenne_mineia_sviatochnaia", liturgia = true, pasxa = true))
        dat20.add(SlugbovyiaTextuData(39, "УЗЬНЯСЕНЬНЕ ГОСПАДА НАШАГА ІСУСА ХРЫСТА", "v_uzniasenne_mineia_sviatochnaia", pasxa = true))
        dat20.add(SlugbovyiaTextuData(274, "Покрыва Найсьвяцейшай Багародзіцы", "l_1_10", liturgia = true))
        dat20.add(SlugbovyiaTextuData(325, "Уваход у храм Найсьвяцейшай Багародзіцы", "l_21_11", liturgia = true))
        dat20.add(SlugbovyiaTextuData(33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ)", "l_2_2", liturgia = true))
        dat20.add(SlugbovyiaTextuData(354, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "l_rastvo_peradsviaccie_20_12", liturgia = true))
        dat20.add(SlugbovyiaTextuData(354, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "v_rastvo_peradsviaccie_20_12"))
        dat20.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "ju_rastvo_sv_vieczar_24_12", utran = true))
        dat20.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "v_rastvo_sv_vieczar_24_12"))
        dat20.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "vgad_rastvo_sv_vieczar_24_12", other = true))
        dat20.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "abed_rastvo_sv_vieczar_24_12", other = true))
        dat20.add(SlugbovyiaTextuData(359, "Нараджэньне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "l_naradzennie_chrystova", liturgia = true))
    }

    fun getTydzen1() = dat12

    fun getTydzen2() = dat13

    fun getTydzen3() = dat14

    fun getTydzen4() = dat15

    fun getTydzen5() = dat16

    fun getTydzen6() = dat17

    fun getMineiaShtodzennia() = dat19

    fun getMineiaSviatochnaia() = dat20

    fun isPasxa(day: Int): Boolean {
        var pasxa = true
        dat19.forEach {
            if (it.day == day) {
                pasxa = it.pasxa
            }
        }
        dat20.forEach {
            if (it.day == day) {
                pasxa = it.pasxa
            }
        }
        return pasxa
    }

    fun getResource(day: Int, pasxa: Boolean, utran: Boolean = false, liturgia: Boolean = false, other: Boolean = false, isSviaty: Boolean = false): String {
        var resource = "0"
        dat12.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat13.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat14.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat15.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat16.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat17.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat18.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat19.forEach {
            if (day == it.day && pasxa == it.pasxa && !isSviaty) {
                if ((!utran && !liturgia) && (!it.utran && !it.liturgia)) resource = it.resource
                if (utran && utran == it.utran) resource = it.resource
                if (liturgia && liturgia == it.liturgia) resource = it.resource
            }
        }
        dat20.forEach {
            if (day == it.day && pasxa == it.pasxa && isSviaty) {
                when {
                    other && other == it.other -> resource = it.resource
                    (!utran && !liturgia && !other) && (!it.utran && !it.liturgia && !it.other) -> resource = it.resource
                    utran && utran == it.utran -> resource = it.resource
                    liturgia && liturgia == it.liturgia -> resource = it.resource
                }
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
        dat19.forEach {
            if (resource == it.resource) return it.title
        }
        dat20.forEach {
            if (resource == it.resource) return it.title
        }
        return ""
    }

    fun loadOpisanieSviat() {
        if (opisanieSviat.size == 0 && loadOpisanieSviatJob?.isActive != true) {
            val fileOpisanieSviat = File("${Malitounik.applicationContext().filesDir}/opisanie_sviat.json")
            if (!fileOpisanieSviat.exists()) {
                if (MainActivity.isNetworkAvailable()) {
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

    fun loadPiarliny() {
        if (piarliny.size == 0 && loadPiarlinyJob?.isActive != true) {
            val fileOpisanieSviat = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
            if (!fileOpisanieSviat.exists()) {
                if (MainActivity.isNetworkAvailable()) {
                    loadPiarlinyJob = CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val mURL = URL("https://carkva-gazeta.by/chytanne/piarliny.json")
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
                            piarliny.addAll(gson.fromJson(builder, type))
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
                    piarliny.addAll(gson.fromJson(builder, type))
                } catch (t: Throwable) {
                    fileOpisanieSviat.delete()
                }
            }
        }
    }

    fun checkParliny(day: Int, mun: Int): Boolean {
        val cal = GregorianCalendar()
        piarliny.forEach {
            cal.timeInMillis = it[0].toLong() * 1000
            if (day == cal.get(Calendar.DATE) && mun - 1 == cal.get(Calendar.MONTH)) {
                return true
            }
        }
        return false
    }

    fun checkUtran(day: Int, dayOfYear: String): Boolean {
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
        dat19.forEach {
            if (dayOfYear.toInt() == it.day) {
                if (it.utran) return true
            }
        }
        dat20.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day) {
                if (it.utran) return true
            }
        }
        return false
    }

    fun checkLiturgia(day: Int, dayOfYear: String): Boolean {
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
        dat19.forEach {
            //Айцоў першых 6-ці Ўсяленскіх сабораў
            if (it.day == 1000) {
                val pasha = Calendar.getInstance()
                for (dny in 13..19) {
                    pasha.set(pasha[Calendar.YEAR], Calendar.JULY, dny)
                    val wik = pasha.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY && pasha.get(Calendar.DAY_OF_YEAR) == dayOfYear.toInt()) {
                        if (it.liturgia) return true
                    }
                }
            }
            if (dayOfYear.toInt() == it.day) {
                if (it.liturgia) return true
            }
        }
        dat20.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day) {
                if (it.liturgia) return true
            }
        }
        return false
    }

    fun checkViachernia(day: Int, dayOfYear: String): Boolean {
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
        dat19.forEach {
            if (dayOfYear.toInt() == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        dat20.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day) {
                if (!it.utran && !it.liturgia) return true
            }
        }
        return false
    }

    fun onDestroy() {
        loadOpisanieSviatJob?.cancel()
        loadPiarlinyJob?.cancel()
    }
}