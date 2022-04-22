package by.carkva_gazeta.malitounik

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class SlugbovyiaTextu {
    private val datMinVP = ArrayList<SlugbovyiaTextuData>()
    private val datMinSH = ArrayList<SlugbovyiaTextuData>()
    private val datMinSV = ArrayList<SlugbovyiaTextuData>()
    private val opisanieSviat = ArrayList<ArrayList<String>>()
    private val piarliny = ArrayList<ArrayList<String>>()
    private var loadOpisanieSviatJob: Job? = null
    private var loadPiarlinyJob: Job? = null

    companion object {
        const val UTRAN = 1
        const val LITURGIA = 2
        const val VIACHERNIA = 3
        const val PAVIACHERNICA = 4
        const val ABEDNICA = 5
        const val VIALIKIAGADZINY = 6
        const val PAUNOCHNICA = 7
    }

    init {
        datMinVP.add(SlugbovyiaTextuData(-49, "Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya12_1", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-48, "Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya12_2", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-47, "Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya12_3", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-46, "Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya12_4", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-45, "Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya12_5", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-44, "Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya12_6", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-43, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya12_7", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya12_8", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Літургія сьвятoга Васіля Вялiкага", "bogashlugbovya12_9", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-42, "1-ая нядзеля посту ўвечары", "bogashlugbovya13_1", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-41, "Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya13_2", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-40, "Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya13_3", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-39, "Серада 2-га тыдня посту ўвечары", "bogashlugbovya13_4", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-38, "Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya13_5", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-37, "Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya13_6", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya13_7", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya13_8", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-35, "2-ая нядзеля посту ўвечары", "bogashlugbovya14_1", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-34, "Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya14_2", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-33, "Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya14_3", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-32, "Серада 3-га тыдня посту ўвечары", "bogashlugbovya14_4", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-31, "Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya14_5", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-30, "Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya14_6", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-29, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya14_7", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya14_8", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya14_9", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту ўвечары", "bogashlugbovya15_1", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-27, "Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya15_2", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-26, "Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya15_3", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-25, "Серада 4-га тыдня посту ўвечары", "bogashlugbovya15_4", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-24, "Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya15_5", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-23, "Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya15_6", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-22, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya15_7", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya15_8", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya15_9", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-21, "4-ая нядзеля посту ўвечары", "bogashlugbovya16_1", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-20, "Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya16_2", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-19, "Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya16_3", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-18, "Серада 5-га тыдня посту ўвечары", "bogashlugbovya16_4", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-17, "Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya16_5", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-16, "Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya16_6", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-15, "Субота Акафісту - Ютрань", "bogashlugbovya16_7", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-15, "Літургія ў суботу Акафісту", "bogashlugbovya16_8", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-15, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya16_9", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya16_10", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya16_11", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-14, "5-ая нядзеля посту ўвечары", "bogashlugbovya17_1", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-13, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya17_2", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-12, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya17_3", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-11, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya17_4", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-10, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya17_5", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-9, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya17_6", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-8, "Субота Лазара - Ютрань", "bogashlugbovya17_7", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-8, "Субота Лазара - Літургія", "bogashlugbovya17_8", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-7, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца) - Літургія", "bogashlugbovya17_9", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-6, "Ютрань Вялікага панядзелака", "vialiki_paniadzielak", UTRAN, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(-5, "Ютрань Вялікага аўторака", "vialiki_autorak", UTRAN, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(-4, "Ютрань Вялікай серады", "vialikaja_sierada", UTRAN, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(-3, "Ютрань Вялікага чацьверга", "vialiki_czacvier", UTRAN, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Ютрань Вялікай пятніцы", "jutran_vial_piatn_12jevanhellau", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "vialikaja_piatnica_viaczernia", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца. Малая павячэрніца", "vialikaja_piatnica_mal_paviaczernica", PAVIACHERNICA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Ютрань Вялікай суботы", "vialikaja_subota_jutran", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота. Паўночніца", "vialikaja_subota_paunocznica", PAUNOCHNICA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота - Літургія", "vialikaja_subota_viaczernia_liturhija", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень) - Ютрань", "vialikdzien_jutran", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(0, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак - Ютрань", "svietly_paniadzielak", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак - Ютрань", "svietly_autorak", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца - Ютрань", "svietlaja_piatnica", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true, checkVialikiaGadziny = true))

        datMinVP.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха) - Літургія", "zmenyia_chastki_tamash", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(14, "Нядзеля міраносіцаў - Літургія", "zmenyia_chastki_miranosicay", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(28, "Нядзеля Самаранкі - Літургія", "zmenyia_chastki_samaranki", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(35, "Нядзеля Сьлепанароджанага - Літургія", "zmenyia_chastki_slepanarodz", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(98, "Айцы першых 6-ці Ўсяленскіх сабораў - Літургія", "l_ajcy_6_saborau", LITURGIA, pasxa = true))

        datMinSH.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў - Ютрань", "ju_8_11", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "v_8_11", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў - Літургія", "l_8_11", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага - Ютрань", "ju_12_11", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "v_12_11", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага - Літургія", "l_12_11", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(317, "Між сьвятымі айца нашага Яна Залатавуснага", "v_13_11", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(317, "Між сьвятымі айца нашага Яна Залатавуснага - Літургія", "l_13_11", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага - Ютрань", "ju_6_12", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "v_6_12", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага - Літургія", "l_6_12", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(360, "Сабор Найсьвяцейшай Багародзіцы - Літургія", "l_12_26_sabor_baharodzicy", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(1, "Малебен на Новы год - Літургія", "maleben_new_year_01_01", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(362, "20 тысячаў мучанікаў Нікамедыйскіх - Літургія", "l_12_28", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня; сьвятаначальніка Сільвестра, папы Рымскага - Літургія", "l_1_2", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня; Сабор 70-ці апосталаў, пачэснага Тэактыста - Літургія", "l_01_04_pieradsv_bohazjaulennia", LITURGIA))

        datMinSV.add(SlugbovyiaTextuData(218, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "zmenyia_chastki_pieramianiennie", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(1, "Абрэзаньне Гасподняе; сьвятаначальніка Васіля Вялікага, архібіск. Кесарыі Кападакійскай", "viachernia_mineia_sviatochnaia1", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "viachernia_mineia_sviatochnaia4", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(3, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "viachernia_mineia_sviatochnaia3", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "viachernia_mineia_sviatochnaia5", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(16, "Пакланеньне кайданам апостала Пятра", "viachernia_mineia_sviatochnaia6", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "viachernia_mineia_sviatochnaia7", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(49, "ТРОЙЦА (СЁМУХА) - Ютрань", "ju_trojca_mineia_sviatochnaia", UTRAN, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "ТРОЙЦА (СЁМУХА) - Літургія", "l_trojca_mineia_sviatochnaia", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "ТРОЙЦА (СЁМУХА)", "v_trojca_mineia_sviatochnaia", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(39, "УЗЬНЯСЕНЬНЕ ГОСПАДА НАШАГА ІСУСА ХРЫСТА - Ютрань", "ju_uzniasenne_mineia_sviatochnaia", UTRAN, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(39, "УЗЬНЯСЕНЬНЕ ГОСПАДА НАШАГА ІСУСА ХРЫСТА - Літургія", "l_uzniasenne_mineia_sviatochnaia", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(39, "УЗЬНЯСЕНЬНЕ ГОСПАДА НАШАГА ІСУСА ХРЫСТА", "v_uzniasenne_mineia_sviatochnaia", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(274, "Покрыва Найсьвяцейшай Багародзіцы - Літургія", "l_1_10", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(325, "Уваход у храм Найсьвяцейшай Багародзіцы - Літургія", "l_21_11", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ) - Літургія", "l_2_2", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(354, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца - Літургія", "l_rastvo_peradsviaccie_20_12", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(354, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "v_rastvo_peradsviaccie_20_12", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі - Ютрань", "ju_rastvo_sv_vieczar_24_12", UTRAN))
        datMinSV.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "v_rastvo_sv_vieczar_24_12", VIACHERNIA))
        datMinSV.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі - Вялікія гадзіны", "vial_hadziny_rastvo_sv_vieczar_24_12", VIALIKIAGADZINY, checkVialikiaGadziny = true))
        datMinSV.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі - Абедніца", "abed_rastvo_sv_vieczar_24_12", ABEDNICA))
        datMinSV.add(SlugbovyiaTextuData(359, "Нараджэньне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "l_naradzennie_chrystova", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "l_1_6_bohazjaulennie", LITURGIA))
        datMinSV.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем) - Вялікія гадзіны", "vial_hadziny_sv_vieczar_bohazjaulennia", VIALIKIAGADZINY, checkVialikiaGadziny = true))
        datMinSV.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем) - Абедніца", "abed_sv_vieczar_bohazjaulennia", ABEDNICA))
    }

    fun getTydzen1(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 0..8) list.add(datMinVP[i])
        return list
    }

    fun getTydzen2(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 9..16) list.add(datMinVP[i])
        return list
    }

    fun getTydzen3(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 17..25) list.add(datMinVP[i])
        return list
    }

    fun getTydzen4(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 26..34) list.add(datMinVP[i])
        return list
    }

    fun getTydzen5(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 35..45) list.add(datMinVP[i])
        return list
    }

    fun getTydzen6(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 46..54) list.add(datMinVP[i])
        return list
    }

    fun getVilikiTydzen(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 55..64) list.add(datMinVP[i])
        return list
    }

    fun getSvetlyTydzen(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 65..69) list.add(datMinVP[i])
        return list
    }

    fun getMineiaShtodzennia() = datMinSH

    fun getMineiaSviatochnaia() = datMinSV

    fun isPasxa(day: Int): Boolean {
        var pasxa = true
        datMinSH.forEach {
            if (it.day == day) {
                pasxa = it.pasxa
            }
        }
        datMinSV.forEach {
            if (it.day == day) {
                pasxa = it.pasxa
            }
        }
        return pasxa
    }

    fun getResource(day: Int, pasxa: Boolean, sluzba: Int, isSviaty: Boolean = false): String {
        var resource = "0"
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if (it.sluzba == sluzba) resource = it.resource
            }
        }
        datMinSH.forEach {
            if (day == it.day && pasxa == it.pasxa && !isSviaty) {
                if (it.sluzba == sluzba) resource = it.resource
            }
        }
        datMinSV.forEach {
            if (day == it.day && pasxa == it.pasxa && isSviaty) {
                if (it.sluzba == sluzba) resource = it.resource
            }
        }
        return resource
    }

    fun getTitle(resource: String): String {
        datMinVP.forEach {
            if (resource == it.resource) return it.title
        }
        datMinSH.forEach {
            if (resource == it.resource) return it.title
        }
        datMinSV.forEach {
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

    fun checkUtran(day: Int, dayOfYear: String, pasxa: Boolean): Boolean {
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if (it.sluzba == UTRAN) return true
            }
        }
        datMinSH.forEach {
            if (dayOfYear.toInt() == it.day && pasxa == it.pasxa) {
                if (it.sluzba == UTRAN) return true
            }
        }
        datMinSV.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day && pasxa == it.pasxa) {
                if (it.sluzba == UTRAN) return true
            }
        }
        return false
    }

    fun checkLiturgia(day: Int, dayOfYear: String, pasxa: Boolean): Boolean {
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if (it.sluzba == LITURGIA) return true
            }
        }
        datMinSH.forEach {
            if (dayOfYear.toInt() == it.day && pasxa == it.pasxa) {
                if (it.sluzba == LITURGIA) return true
            }
        }
        datMinSV.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day && pasxa == it.pasxa) {
                if (it.sluzba == LITURGIA) return true
            }
        }
        return false
    }

    fun checkViachernia(day: Int, dayOfYear: String, pasxa: Boolean): Boolean {
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if (it.sluzba == VIACHERNIA) return true
            }
        }
        datMinSH.forEach {
            if (dayOfYear.toInt() == it.day && pasxa == it.pasxa) {
                if (it.sluzba == VIACHERNIA) return true
            }
        }
        datMinSV.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day && pasxa == it.pasxa) {
                if (it.sluzba == VIACHERNIA) return true
            }
        }
        return false
    }

    fun checkPavichrrnica(day: Int, dayOfYear: String, pasxa: Boolean): Boolean {
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if (it.sluzba == PAVIACHERNICA) return true
            }
        }
        datMinSH.forEach {
            if (dayOfYear.toInt() == it.day && pasxa == it.pasxa) {
                if (it.sluzba == PAVIACHERNICA) return true
            }
        }
        datMinSV.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day && pasxa == it.pasxa) {
                if (it.sluzba == PAVIACHERNICA) return true
            }
        }
        return false
    }

    fun checkPaunochnica(day: Int, dayOfYear: String, pasxa: Boolean): Boolean {
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa) {
                if (it.sluzba == PAUNOCHNICA) return true
            }
        }
        datMinSH.forEach {
            if (dayOfYear.toInt() == it.day && pasxa == it.pasxa) {
                if (it.sluzba == PAUNOCHNICA) return true
            }
        }
        datMinSV.forEach {
            val dayR = if (it.pasxa) day
            else dayOfYear.toInt()
            if (dayR == it.day && pasxa == it.pasxa) {
                if (it.sluzba == PAUNOCHNICA) return true
            }
        }
        return false
    }

    fun checkVialikiaGadziny(day: Int): Boolean {
        datMinVP.forEach {
            if (day == it.day && it.checkVialikiaGadziny) return true
        }
        return false
    }

    fun onDestroy() {
        loadOpisanieSviatJob?.cancel()
        loadPiarlinyJob?.cancel()
    }
}