package by.carkva_gazeta.malitounik

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class SlugbovyiaTextu {
    private val datMinVP = ArrayList<SlugbovyiaTextuData>()
    private val datMinSH = ArrayList<SlugbovyiaTextuData>()
    private val datMinSV = ArrayList<SlugbovyiaTextuData>()
    private val piarliny = ArrayList<ArrayList<String>>()
    private var loadPiarlinyJob: Job? = null

    companion object {
        const val UTRAN = 1
        const val LITURGIA = 2
        const val VIACHERNIA = 3
        const val PAVIACHERNICA = 4
        const val ABEDNICA = 5
        const val VIALIKIAGADZINY = 6
        const val PAUNOCHNICA = 7
        const val AICOU_VII_SUSVETNAGA_SABORY = 1000
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
        datMinVP.add(SlugbovyiaTextuData(-6, "Ютрань Вялікага панядзелака", "vialiki_paniadzielak", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-5, "Ютрань Вялікага аўторака", "vialiki_autorak", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-4, "Ютрань Вялікай серады", "vialikaja_sierada", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-3, "Ютрань Вялікага чацьверга", "vialiki_czacvier", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Ютрань Вялікай пятніцы", "jutran_vial_piatn_12jevanhellau", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "vialikaja_piatnica_viaczernia", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца. Малая павячэрніца", "vialikaja_piatnica_mal_paviaczernica", PAVIACHERNICA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Ютрань Вялікай суботы", "vialikaja_subota_jutran", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота. Паўночніца", "vialikaja_subota_paunocznica", PAUNOCHNICA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота - Літургія", "vialikaja_subota_viaczernia_liturhija", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень) - Ютрань", "vialikdzien_jutran", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень) - Вячэрня", "vialikdzien_viaczernia", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак - Ютрань", "u_svietly_paniadzielak", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак - Літургія", "l_svietly_paniadzielak", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак - Вячэрня", "v_svietly_paniadzielak", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак - Ютрань", "u_svietly_autorak", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак - Літургія", "l_svietly_autorak", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак - Вячэрня", "v_svietly_autorak", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлая серада - Ютрань", "u_svietlaja_sierada", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлая серада - Літургія", "l_svietlaja_sierada", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлая серада - Вячэрня", "v_svietlaja_sierada", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер - Ютрань", "u_svietly_czacvier", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер - Літургія", "l_svietly_czacvier", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер - Вячэрня", "v_svietly_czacvier", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца - Ютрань", "u_svietlaja_piatnica", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца - Літургія", "l_svietlaja_piatnica", LITURGIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца - Вячэрня", "v_svietlaja_piatnica", VIACHERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлая субота - Ютрань", "u_svietlaja_subota", UTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлая субота - Літургія", "l_svietlaja_subota", LITURGIA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлы тыдзень - Велікодныя гадзіны", "vielikodnyja_hadziny", VIALIKIAGADZINY, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(98, "Айцы першых 6-ці Ўсяленскіх сабораў - Літургія", "l_ajcy_6_saborau", LITURGIA, pasxa = true))

        datMinSH.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_jutran", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(312, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "ju_12_11", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "v_12_11", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(316, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "l_12_11", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(317, "Між сьвятымі айца нашага Яна Залатавуснага", "mm_13_11_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(317, "Між сьвятымі айца нашага Яна Залатавуснага", "mm_13_11_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "ju_6_12", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "v_6_12", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(340, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "l_6_12", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(360, "Сабор Найсьвяцейшай Багародзіцы - Літургія", "l_12_26_sabor_baharodzicy", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(1, "Малебен на Новы год - Літургія", "mm_01_01_malebien_novy_hod", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(362, "20 тысячаў мучанікаў Нікамедыйскіх - Літургія", "l_12_28", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня; сьвятаначальніка Сільвестра, папы Рымскага - Літургія", "mm_02_01_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня; Сабор 70-ці апосталаў, пачэснага Тэактыста - Літургія", "mm_04_01_pieradsv_bohazjaulennia_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(127, "Жырoвiцкaй iкoны Maцi Бoжae - Літургія", "mm_07_05_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(131, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода - Літургія", "mm_11_05_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(141, "Сьвятых роўнаапостальных Канстанціна і Алены", "mm_21_05_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(141, "Сьвятых роўнаапостальных Канстанціна і Алены", "mm_21_05_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(143, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_jutran", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(143, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(143, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(180, "Сьвятых слаўных i годных пахвалы апосталаў Пятра i Паўла", "mm_29_06_piatra_i_paula_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(181, "Сабор сьвятых 12-ці апосталаў", "mm_30_06_sabor_12_apostalau_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(244, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "mm_01_09_novy_hod_viczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(244, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "mm_01_09_novy_hod_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(248, "Сьвятога прарока Захара, бацькі Яна Хрысьціцеля, і праведнае Альжбеты, ягонае маці", "mm_05_09_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(250, "Перадсьвяцьце Нараджэньня Багародзіцы і сьвятога мучаніка Сазонта", "mm_07_09_pieradsv_naradz_baharodz_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(251, "Нараджэньне Найсьвяцейшае Багародзіцы", "mm_08_09_naradz_baharodzicy_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(252, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых Якіма і Ганны", "mm_09_09_pasviaccie_naradz_baharodz_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(253, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых мучаніц Мінадоры, Мітрадоры і Німфадоры", "mm_10_09_pasviaccie_naradz_baharodz_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(254, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятое маці нашае Тадоры", "mm_11_09_pasviaccie_naradz_baharodz_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(255, "Адданьне сьвята Нараджэньня Багародзіцы", "mm_12_09_addannie_naradz_baharodzicy_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(256, "Перадсьвяцьце Ўзвышэньня, памяць аднаўленьня царквы Ўваскрасеньня і сьвятога мучаніка Карніла", "mm_13_09_pieradsv_uzvyszennia_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(257, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "mm_14_09_uzvyszennie_kryza_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(258, "Пасьвяцьце Ўзвышэньня і сьвятога вялікамучаніка Мікіты", "mm_15_09_pasviaccie_uzvyszennia_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "mm_11_17_10_ajcou_7_susviet_saboru_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(217, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "mm_05_08_pieradsv_pieramianiennia_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(217, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "mm_05_08_pieradsv_pieramianiennia_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(218, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "mm_06_08_pieramianiennie_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(221, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "mm_09_08_pasviaccie_pieramianiennia_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(221, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "mm_09_08_pasviaccie_pieramianennia_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(227, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_15_08_uspiennie_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(227, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_15_08_uspiennie_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(222, "Пасьвяцьце Перамяненьня і сьв. мучаніка Лаўрына", "mm_10_08_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(226, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "mm_14_08_pieradsv_uspiennia_viaczernia", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(226, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "mm_14_08_pieradsv_uspiennia_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(1, "Абрэзаньне Гасподняе; сьвятаначальніка Васіля Вялікага, архібіск. Кесарыі Кападакійскай", "viachernia_mineia_sviatochnaia1", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "viachernia_mineia_sviatochnaia4", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(3, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "viachernia_mineia_sviatochnaia3", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "viachernia_mineia_sviatochnaia5", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(16, "Пакланеньне кайданам апостала Пятра", "viachernia_mineia_sviatochnaia6", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "viachernia_mineia_sviatochnaia7", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста - Ютрань", "uzniasienne_jutran", UTRAN, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста - Літургія", "uzniasienne_liturhija", LITURGIA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_viaczernia", VIACHERNIA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(42, "Нядзеля сьвятых айцоў I-га Усяленскага Сабору", "ndz_ajcou_1susviet_saboru", LITURGIA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(56, "Нядзеля ўсіх сьвятых", "ndz_usich_sviatych_liturhija", LITURGIA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(63, "Нядзеля ўсіх сьвятых беларускага народу", "ndz_usich_sv_biel_narodu_liturhija", LITURGIA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(274, "Покрыва Найсьвяцейшай Багародзіцы - Літургія", "mm_01_10_pokryva_baharodzicy_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(325, "Уваход у храм Найсьвяцейшай Багародзіцы - Літургія", "mm_21_11_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ) - Літургія", "mm_02_02_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(354, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца - Літургія", "l_rastvo_peradsviaccie_20_12", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(354, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "v_rastvo_peradsviaccie_20_12", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі - Ютрань", "ju_rastvo_sv_vieczar_24_12", UTRAN))
        datMinSH.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "v_rastvo_sv_vieczar_24_12", VIACHERNIA))
        datMinSH.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі - Вялікія гадзіны", "vial_hadziny_rastvo_sv_vieczar_24_12", VIALIKIAGADZINY))
        datMinSH.add(SlugbovyiaTextuData(358, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі - Абедніца", "abed_rastvo_sv_vieczar_24_12", ABEDNICA))
        datMinSH.add(SlugbovyiaTextuData(359, "Нараджэньне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "l_naradzennie_chrystova", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "mm_06_01_bohazjaulennie_liturhija", LITURGIA))
        datMinSH.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем) - Вялікія гадзіны", "mm_05_01_sv_vieczar_bohazjaulennia_vial_hadziny", VIALIKIAGADZINY))
        datMinSH.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем) - Абедніца", "mm_05_01_sv_vieczar_bohazjaulennia_abiednica", ABEDNICA))

        datMinSV.add(SlugbovyiaTextuData(6, "Нядзеля Тамаша (Антыпасха) вячэрня ў суботу", "ndz_tamasza_viaczernia_subota", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха) - Ютрань", "ndz_tamasza_jutran", UTRAN, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха) - Літургія", "ndz_tamasza_liturhija", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша ўвечары", "ndz_tamasza_uvieczary", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(8, "Панядзелак пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_paniadzielak", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(9, "Аўторак пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_autorak", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(10, "Серада пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_sierada", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(11, "Чацьвер пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_czacvier", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(12, "Пятніца пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_piatnica", VIACHERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(14, "Нядзеля міраносіцаў - Літургія", "ndz_miranosic_liturhija", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(21, "Нядзеля расслабленага - Літургія", "ndz_rasslablenaha_liturhija", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(28, "Нядзеля самаранкі - Літургія", "ndz_samaranki_liturhija", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(35, "Нядзеля сьлепанароджанага - Літургія", "ndz_slepanarodz_liturhija", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха) - Ютрань", "zychod_sv_ducha_jutran", UTRAN, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "Тройца (СЁМУХА) - Літургія", "zychod_sv_ducha_liturhija", LITURGIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "paniadzielak_sv_ducha_ndz_viaczaram", VIACHERNIA, pasxa = true))
    }

    fun getTydzen1(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 0..8) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getTydzen2(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 9..16) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getTydzen3(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 17..25) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getTydzen4(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 26..34) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getTydzen5(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 35..45) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getTydzen6(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 46..53) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getVilikiTydzen(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 54..64) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getSvetlyTydzen(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 65..84) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getMineiaMesiachnaia(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 0 until datMinSH.size) list.add(datMinSH[i])
        list.sort()
        return list
    }

    fun getMineiaSviatochnaia(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 0 until datMinSV.size) list.add(datMinSV[i])
        list.sort()
        return list
    }

    fun getResource(day: Int, dayOfYear: Int, sluzba: Int): String {
        var resource = "0"
        val checkDay = if (day == AICOU_VII_SUSVETNAGA_SABORY) getRealDay(day)
        else day
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == sluzba) {
                    resource = it.resource
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == sluzba) {
                    resource = it.resource
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (checkDay == it.day && it.sluzba == sluzba) {
                    resource = it.resource
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == sluzba) {
                    resource = it.resource
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (checkDay == it.day && it.sluzba == sluzba) {
                    resource = it.resource
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == sluzba) {
                    resource = it.resource
                }
            }
        }
        return resource
    }

    fun getResource(day: Int, pasxa: Boolean, sluzba: Int): String {
        var resource = "0"
        val checkDay = if (day == AICOU_VII_SUSVETNAGA_SABORY) getRealDay(day)
        else day
        datMinVP.forEach {
            if (day == it.day && pasxa == it.pasxa && it.sluzba == sluzba) {
                resource = it.resource
            }
        }
        datMinSH.forEach {
            if (checkDay == it.day && pasxa == it.pasxa && it.sluzba == sluzba) {
                resource = it.resource
            }
        }
        datMinSV.forEach {
            if (day == it.day && pasxa == it.pasxa && it.sluzba == sluzba) {
                resource = it.resource
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

    fun loadPiarliny() {
        if (piarliny.size == 0 && loadPiarlinyJob?.isActive != true) {
            val filePiarliny = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
            if (!filePiarliny.exists()) {
                if (MainActivity.isNetworkAvailable()) {
                    loadPiarlinyJob = CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            runCatching {
                                try {
                                    val mURL = URL("https://carkva-gazeta.by/chytanne/piarliny.json")
                                    val conections = mURL.openConnection() as HttpURLConnection
                                    if (conections.responseCode == 200) {
                                        filePiarliny.writer().use {
                                            it.write(mURL.readText())
                                        }
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                        }
                        try {
                            val builder = filePiarliny.readText()
                            val gson = Gson()
                            val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                            piarliny.addAll(gson.fromJson(builder, type))
                        } catch (t: Throwable) {
                            filePiarliny.delete()
                        }
                    }
                }
            } else {
                try {
                    val builder = filePiarliny.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(ArrayList::class.java, TypeToken.getParameterized(ArrayList::class.java, String::class.java).type).type
                    piarliny.addAll(gson.fromJson(builder, type))
                } catch (t: Throwable) {
                    filePiarliny.delete()
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

    fun checkUtran(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == UTRAN) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == UTRAN) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == UTRAN) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == UTRAN) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == UTRAN) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == UTRAN) {
                    return true
                }
            }
        }
        return false
    }

    fun checkLiturgia(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == LITURGIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == LITURGIA) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == LITURGIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == LITURGIA) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == LITURGIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == LITURGIA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkViachernia(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACHERNIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIACHERNIA) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACHERNIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIACHERNIA) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACHERNIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIACHERNIA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkPavichrrnica(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkPaunochnica(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkVialikiaGadziny(day: Int): Boolean {
        datMinVP.forEach {
            if (day == it.day && it.sluzba == VIALIKIAGADZINY) {
                return true
            }
        }
        return false
    }

    fun getRealDay(day: Int): Int {
        var realDay = day
        val calendar = Calendar.getInstance()
        //Айцоў VII Сусьветнага Сабору
        for (i in 11..17) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.OCTOBER, i)
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && (calendar.get(Calendar.DAY_OF_YEAR) == day || day == AICOU_VII_SUSVETNAGA_SABORY)) {
                realDay = calendar.get(Calendar.DAY_OF_YEAR)
            }
        }
        return realDay
    }

    fun checkFullChtenia(resource: Int): Boolean {
        val inputStream = Malitounik.applicationContext().resources.openRawResource(resource)
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        val text = reader.readText()
        return text.contains("NOCH")
    }

    fun onDestroy() {
        loadPiarlinyJob?.cancel()
    }
}
