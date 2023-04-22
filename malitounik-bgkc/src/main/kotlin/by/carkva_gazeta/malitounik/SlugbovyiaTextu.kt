package by.carkva_gazeta.malitounik

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Calendar
import java.util.GregorianCalendar

class SlugbovyiaTextu {
    private val datMinVP = ArrayList<SlugbovyiaTextuData>()
    private val datMinSH = ArrayList<SlugbovyiaTextuData>()
    private val datMinSV = ArrayList<SlugbovyiaTextuData>()
    private val piarliny = ArrayList<ArrayList<String>>()
    private var loadPiarlinyJob: Job? = null

    companion object {
        const val JUTRAN = 1
        const val LITURHIJA = 2
        const val VIACZERNIA = 3
        const val PAVIACHERNICA = 4
        const val ABIEDNICA = 5
        const val VIALHADZINY = 6
        const val PAUNOCHNICA = 7
        const val VIACZERNIA_Z_LITURHIJA = 8
        const val AICOU_VII_SUSVETNAGA_SABORY = 1000
        const val NIADZELIA_PRA_AICOU = 1001
        const val NIADZELIA_AICOU_VI_SABORY = 1002
    }

    init {
        datMinVP.add(SlugbovyiaTextuData(-49, "Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya12_1", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-48, "Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya12_2", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-47, "Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya12_3", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-46, "Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya12_4", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-45, "Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya12_5", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-44, "Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya12_6", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-43, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya12_7", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya12_8", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Літургія сьвятoга Васіля Вялiкага", "bogashlugbovya12_9", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-42, "1-ая нядзеля посту ўвечары", "bogashlugbovya13_1", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-41, "Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya13_2", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-40, "Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya13_3", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-39, "Серада 2-га тыдня посту ўвечары", "bogashlugbovya13_4", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-38, "Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya13_5", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-37, "Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya13_6", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya13_7", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya13_8", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-35, "2-ая нядзеля посту ўвечары", "bogashlugbovya14_1", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-34, "Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya14_2", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-33, "Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya14_3", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-32, "Серада 3-га тыдня посту ўвечары", "bogashlugbovya14_4", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-31, "Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya14_5", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-30, "Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya14_6", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-29, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya14_7", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya14_8", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya14_9", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту ўвечары", "bogashlugbovya15_1", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-27, "Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya15_2", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-26, "Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya15_3", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-25, "Серада 4-га тыдня посту ўвечары", "bogashlugbovya15_4", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-24, "Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya15_5", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-23, "Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya15_6", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-22, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya15_7", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya15_8", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya15_9", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-21, "4-ая нядзеля посту ўвечары", "bogashlugbovya16_1", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-20, "Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya16_2", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-19, "Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya16_3", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-18, "Серада 5-га тыдня посту ўвечары", "bogashlugbovya16_4", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-17, "Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya16_5", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-16, "Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya16_6", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-15, "Субота Акафісту", "bogashlugbovya16_7", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-15, "Літургія ў суботу Акафісту", "bogashlugbovya16_8", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-15, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya16_9", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya16_10", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya16_11", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-14, "5-ая нядзеля посту ўвечары", "bogashlugbovya17_1", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-13, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya17_2", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-12, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya17_3", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-11, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya17_4", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-10, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya17_5", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-9, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya17_6", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-8, "Субота Лазара", "bogashlugbovya17_7", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-8, "Субота Лазара", "bogashlugbovya17_8", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(-7, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца)", "bogashlugbovya17_9", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-6, "Вялікі панядзелак", "vialiki_paniadzielak", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-6, "Вялікі панядзелак", "vialiki_paniadzielak_liturhija_raniej_asviacz_darou", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-6, "Вялікі панядзелак", "vialiki_paniadzielak_hadzina_6", VIALHADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-5, "Вялікі аўторак", "vialiki_autorak", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-4, "Вялікая серада", "vialikaja_sierada", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-4, "Вялікая серада", "vialikaja_sierada_liturhija_raniej_asviacz_darou", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-3, "Вялікі чацьвер", "vialiki_czacvier", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "jutran_vial_piatn_12jevanhellau", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "vialikaja_piatnica_viaczernia", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "vialikaja_piatnica_mal_paviaczernica", PAVIACHERNICA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота", "vialikaja_subota_paunocznica", PAUNOCHNICA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота", "vialikaja_subota_jutran", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(-1, "Вялікая субота", "vialikaja_subota_viaczernia_liturhija", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "vialikdzien_jutran", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "vialikdzien_viaczernia", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "u_svietly_paniadzielak", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "l_svietly_paniadzielak", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "v_svietly_paniadzielak", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "u_svietly_autorak", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "l_svietly_autorak", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "v_svietly_autorak", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлая серада", "u_svietlaja_sierada", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлая серада", "l_svietlaja_sierada", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлая серада", "v_svietlaja_sierada", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "u_svietly_czacvier", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "l_svietly_czacvier", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "v_svietly_czacvier", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "u_svietlaja_piatnica", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "l_svietlaja_piatnica", LITURHIJA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "v_svietlaja_piatnica", VIACZERNIA, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлая субота", "u_svietlaja_subota", JUTRAN, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлая субота", "l_svietlaja_subota", LITURHIJA, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(1, "Сьветлы тыдзень", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(2, "Сьветлы тыдзень", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(3, "Сьветлы тыдзень", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(4, "Сьветлы тыдзень", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(5, "Сьветлы тыдзень", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true))
        datMinVP.add(SlugbovyiaTextuData(6, "Сьветлы тыдзень", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true))

        datMinVP.add(SlugbovyiaTextuData(98, "Айцы першых 6-ці Ўсяленскіх сабораў", "l_ajcy_6_saborau", LITURHIJA, pasxa = true))

        datMinSH.add(SlugbovyiaTextuData(313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_jutran", JUTRAN))
        datMinSH.add(SlugbovyiaTextuData(313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(318, "Між сьвятымі айца нашага Яна Залатавуснага", "mm_13_11_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(318, "Між сьвятымі айца нашага Яна Залатавуснага", "mm_13_11_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(1, "Малебен на Новы год", "mm_01_01_malebien_novy_hod", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня; сьвятаначальніка Сільвестра, папы Рымскага", "mm_02_01_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня; Сабор 70-ці апосталаў, пачэснага Тэактыста", "mm_04_01_pieradsv_bohazjaulennia_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(128, "Жырoвiцкaй iкoны Maцi Бoжae", "mm_07_05_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "mm_11_05_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(142, "Сьвятых роўнаапостальных Канстанціна і Алены", "mm_21_05_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(142, "Сьвятых роўнаапостальных Канстанціна і Алены", "mm_21_05_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_jutran", JUTRAN))
        datMinSH.add(SlugbovyiaTextuData(144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(181, "Сьвятых слаўных i годных пахвалы апосталаў Пятра i Паўла", "mm_29_06_piatra_i_paula_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(182, "Сабор сьвятых 12-ці апосталаў", "mm_30_06_sabor_12_apostalau_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(245, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "mm_01_09_novy_hod_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(245, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "mm_01_09_novy_hod_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(249, "Сьвятога прарока Захара, бацькі Яна Хрысьціцеля, і праведнае Альжбеты, ягонае маці", "mm_05_09_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(251, "Перадсьвяцьце Нараджэньня Багародзіцы і сьвятога мучаніка Сазонта", "mm_07_09_pieradsv_naradz_baharodz_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(252, "Нараджэньне Найсьвяцейшае Багародзіцы", "mm_08_09_naradz_baharodzicy_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(253, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых Якіма і Ганны", "mm_09_09_pasviaccie_naradz_baharodz_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(254, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых мучаніц Мінадоры, Мітрадоры і Німфадоры", "mm_10_09_pasviaccie_naradz_baharodz_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(255, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятое маці нашае Тадоры", "mm_11_09_pasviaccie_naradz_baharodz_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(256, "Адданьне сьвята Нараджэньня Багародзіцы", "mm_12_09_addannie_naradz_baharodzicy_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(257, "Перадсьвяцьце Ўзвышэньня, памяць аднаўленьня царквы Ўваскрасеньня і сьвятога мучаніка Карніла", "mm_13_09_pieradsv_uzvyszennia_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "mm_14_09_uzvyszennie_kryza_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(259, "Пасьвяцьце Ўзвышэньня і сьвятога вялікамучаніка Мікіты", "mm_15_09_pasviaccie_uzvyszennia_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "mm_11_17_10_ajcou_7_susviet_saboru_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(NIADZELIA_PRA_AICOU, "Нядзеля праайцоў", "mm_11_17_12_ndz_praajcou_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(NIADZELIA_AICOU_VI_SABORY, "Нядзеля сьвятых Айцоў першых шасьці Сабораў", "mm_13_19_07_ndz_ajcou_6_saborau_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(218, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "mm_05_08_pieradsv_pieramianiennia_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(218, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "mm_05_08_pieradsv_pieramianiennia_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(219, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "mm_06_08_pieramianiennie_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(222, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "mm_09_08_pasviaccie_pieramianiennia_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(222, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "mm_09_08_pasviaccie_pieramianennia_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(228, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_15_08_uspiennie_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(228, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_15_08_uspiennie_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(223, "Пасьвяцьце Перамяненьня і сьв. мучаніка Лаўрына", "mm_10_08_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(227, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "mm_14_08_pieradsv_uspiennia_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(227, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "mm_14_08_pieradsv_uspiennia_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(1, "Абрэзаньне Гасподняе; сьвятаначальніка Васіля Вялікага, архібіск. Кесарыі Кападакійскай", "viachernia_mineia_sviatochnaia1", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "viachernia_mineia_sviatochnaia4", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(3, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "viachernia_mineia_sviatochnaia3", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "viachernia_mineia_sviatochnaia5", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(16, "Пакланеньне кайданам апостала Пятра", "viachernia_mineia_sviatochnaia6", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "viachernia_mineia_sviatochnaia7", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_jutran", JUTRAN, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_viaczernia", VIACZERNIA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(42, "Нядзеля сьвятых айцоў I-га Усяленскага Сабору", "ndz_ajcou_1susviet_saboru", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(56, "Нядзеля ўсіх сьвятых", "ndz_usich_sviatych_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(63, "Нядзеля ўсіх сьвятых беларускага народу", "ndz_usich_sv_biel_narodu_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(275, "Покрыва Найсьвяцейшай Багародзіцы", "mm_01_10_pokryva_baharodzicy_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(326, "Уваход у храм Найсьвяцейшай Багародзіцы", "mm_21_11_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "mm_06_01_bohazjaulennie_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "mm_05_01_sv_vieczar_bohazjaulennia_vial_hadziny", VIALHADZINY))
        datMinSH.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "mm_05_01_sv_vieczar_bohazjaulennia_abiednica", ABIEDNICA))
        datMinSH.add(SlugbovyiaTextuData(341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "mm_06_12_mikoly_cudatvorcy_jutran", JUTRAN))
        datMinSH.add(SlugbovyiaTextuData(341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "mm_06_12_mikoly_cudatvorcy_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "mm_06_12_mikoly_cudatvorcy_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "mm_12_11_sviatamucz_jazafat_jutran", JUTRAN))
        datMinSH.add(SlugbovyiaTextuData(317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "mm_12_11_sviatamucz_jazafat_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "mm_12_11_sviatamucz_jazafat_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(355, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "mm_20_12_rastvo_peradsviaccie_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(355, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "mm_20_12_rastvo_peradsviaccie_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_abednica", ABIEDNICA))
        datMinSH.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_jutran", JUTRAN))
        datMinSH.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_vial_hadziny", VIALHADZINY))
        datMinSH.add(SlugbovyiaTextuData(360, "Нараджэньне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "mm_25_12_naradzennie_chrystova_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(361, "Сабор Найсьвяцейшай Багародзіцы", "mm_26_12_sabor_baharodzicy_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(363, "20 тысячаў мучанікаў Нікамедыйскіх", "mm_28_12_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(-70, "Нядзеля мытніка і фарысэя", "ndz_mytnika_i_faryseja_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ)", "mm_02_02_sustrecza_hospada_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(-56, "Нядзеля мясапусная, пра Страшны суд", "ndz_miasapusnaja_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(-63, "Нядзеля блуднага сына", "ndz_bludnaha_syna_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(-49, "Нядзеля сырная", "ndz_syrnaja_liturhija", LITURHIJA, pasxa = true))
        datMinSH.add(SlugbovyiaTextuData(66, "Сьвятога і роўнага апосталам Мятода, настаўніка славянскага і архібіскупа Мараўскага", "mm_06_04_miatoda_marauskaha_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(74, "Пачэснага айца нашага Бенядыкта", "mm_14_03_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(77, "Аляксея, чалавека Божага", "mm_17_03_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(78, "Між сьвятымі айца нашага Кірылы, біскупа Ерусалімскага", "mm_18_03_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(114, "Сьвятога вялікамучаніка Юрыя", "mm_23_04_juryja_pieramozcy_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(114, "Сьвятога вялікамучаніка Юрыя", "mm_23_04_juryja_pieramozcy_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(84, "Перадсьвяцьце Дабравешчаньня", "mm_24_03_pieradsv_dabravieszczannia_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(84, "Перадсьвяцьце Дабравешчаньня", "mm_24_03_pieradsv_dabravieszczannia_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "mm_25_03_dabravieszczannie_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "mm_25_03_dabravieszczannie_liturhija_subota_niadziela", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "mm_25_03_dabravieszczannie_viaczernia_z_liturhijaj", VIACZERNIA_Z_LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(105, "Між сьвятымі айца нашага Марціна Вызнаўцы, папы Рымскага", "mm_14_04_marcina_papy_rymskaha_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(114, "Сьвятога вялікамучаніка Юрыя", "mm_23_04_juryja_pieramozcy_jutran", JUTRAN))
        datMinSH.add(SlugbovyiaTextuData(116, "Сьвятога апостала і евангеліста Марка", "mm_25_04_apostala_marka_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(116, "Сьвятога апостала і евангеліста Марка", "mm_25_04_apostala_marka_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(118, "Сьвятога сьвятамучаніка Сямёна, сваяка Гасподняга", "mm_27_04_siamiona_svajaka_haspodniaha_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(119, "Між сьвятымі айца нашага Кірылы, біскупа Тураўскага", "mm_28_04_kiryly_turauskaha_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(119, "Між сьвятымі айца нашага Кірылы, біскупа Тураўскага", "mm_28_04_kiryly_turauskaha_viaczernia", VIACZERNIA))
        datMinSH.add(SlugbovyiaTextuData(121, "Сьвятога апостала Якуба, брата Яна Багаслова", "mm_30_04_apostala_jakuba_liturhija", LITURHIJA))
        datMinSH.add(SlugbovyiaTextuData(121, "Сьвятога апостала Якуба, брата Яна Багаслова", "mm_30_04_apostala_jakuba_viaczernia", VIACZERNIA))

        datMinSV.add(SlugbovyiaTextuData(6, "Нядзеля Тамаша (Антыпасха) вячэрня ў суботу", "ndz_tamasza_viaczernia_subota", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха)", "ndz_tamasza_jutran", JUTRAN, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха)", "ndz_tamasza_liturhija", LITURHIJA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша ўвечары", "ndz_tamasza_uvieczary", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(8, "Панядзелак пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_paniadzielak", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(9, "Аўторак пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_autorak", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(10, "Серада пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_sierada", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(11, "Чацьвер пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_czacvier", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(12, "Пятніца пасьля нядзелі Тамаша ўвечары", "ndz_tamasza_piatnica", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(14, "Нядзеля міраносіцаў", "ndz_miranosic_liturhija", LITURHIJA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(21, "Нядзеля расслабленага", "ndz_rasslablenaha_liturhija", LITURHIJA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(28, "Нядзеля самаранкі", "ndz_samaranki_liturhija", LITURHIJA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(35, "Нядзеля сьлепанароджанага", "ndz_slepanarodz_liturhija", LITURHIJA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "paniadzielak_sv_ducha_ndz_viaczaram", VIACZERNIA, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "zychod_sv_ducha_jutran", JUTRAN, pasxa = true))
        datMinSV.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "zychod_sv_ducha_liturhija", LITURHIJA, pasxa = true))

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
        for (i in 54..67) list.add(datMinVP[i])
        list.sort()
        return list
    }

    fun getSvetlyTydzen(): ArrayList<SlugbovyiaTextuData> {
        val list = ArrayList<SlugbovyiaTextuData>()
        for (i in 68..87) list.add(datMinVP[i])
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
        val checkDay = getRealDay(day)
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
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == sluzba) {
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
        val checkDay = getFictionalDay(day)
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
                        try {
                            val builder = getPiarliny()
                            if (builder != "") {
                                val gson = Gson()
                                val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                                piarliny.addAll(gson.fromJson(builder, type))
                            }
                        } catch (t: Throwable) {
                            filePiarliny.delete()
                        }
                    }
                }
            } else {
                try {
                    val builder = filePiarliny.readText()
                    val gson = Gson()
                    val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, String::class.java).type).type
                    piarliny.addAll(gson.fromJson(builder, type))
                } catch (t: Throwable) {
                    filePiarliny.delete()
                }
            }
        }
    }

    private suspend fun getPiarliny(): String {
        val pathReference = Malitounik.referens.child("/chytanne/piarliny.json")
        var text = ""
        val localFile = File("${Malitounik.applicationContext().filesDir}/piarliny.json")
        pathReference.getFile(localFile).addOnSuccessListener {
            text = localFile.readText()
        }.await()
        return text
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
                if (day == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            }
        }
        return false
    }

    fun checkLiturgia(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == LITURHIJA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == LITURHIJA) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == LITURHIJA) {
                    return true
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == LITURHIJA) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == LITURHIJA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == LITURHIJA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkViachernia(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            }
        }
        datMinSV.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIACZERNIA) {
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

    fun checkVialikiaGadziny(day: Int, dayOfYear: Int): Boolean {
        datMinVP.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIALHADZINY) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIALHADZINY) {
                    return true
                }
            }
        }
        datMinSH.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIALHADZINY) {
                    return true
                }
            } else {
                if (dayOfYear == it.day && it.sluzba == VIALHADZINY) {
                    return true
                }
            }
        }
        return false
    }

    fun getRealDay(day: Int): Int {
        var realDay = day
        val calendar = Calendar.getInstance()
        when (day) {
            AICOU_VII_SUSVETNAGA_SABORY -> {
                //Айцоў VII Сусьветнага Сабору
                for (i in 11..17) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.OCTOBER, i)
                    val wik = calendar.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        realDay = calendar.get(Calendar.DAY_OF_YEAR)
                    }
                }
            }
           NIADZELIA_PRA_AICOU -> {
                //Нядзеля праайцоў
                for (i in 11..17) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
                    val wik = calendar.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        realDay = calendar.get(Calendar.DAY_OF_YEAR)
                    }
                }
            }
            NIADZELIA_AICOU_VI_SABORY -> {
                //Нядзеля сьвятых Айцоў першых шасьці Сабораў
                for (i in 13..19) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.JULY, i)
                    val wik = calendar.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        realDay = calendar.get(Calendar.DAY_OF_YEAR)
                    }
                }
            }
        }
        return realDay
    }

    private fun getFictionalDay(dayOfYear: Int): Int {
        var fictionalDay = dayOfYear
        val calendar = Calendar.getInstance()
        //Айцоў VII Сусьветнага Сабору
        for (i in 11..17) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.OCTOBER, i)
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && dayOfYear == calendar[Calendar.DAY_OF_YEAR]) {
                fictionalDay = AICOU_VII_SUSVETNAGA_SABORY
            }
        }
        //Нядзеля праайцоў
        for (i in 11..17) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && dayOfYear == calendar[Calendar.DAY_OF_YEAR]) {
                fictionalDay = NIADZELIA_PRA_AICOU
            }
        }
        //Нядзеля сьвятых Айцоў першых шасьці Сабораў
        for (i in 13..19) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.JULY, i)
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && dayOfYear == calendar[Calendar.DAY_OF_YEAR]) {
                fictionalDay = NIADZELIA_AICOU_VI_SABORY
            }
        }
        return fictionalDay
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
