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
    private val datMinALL = ArrayList<SlugbovyiaTextuData>()
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
        const val MINEIA_SVIATOCHNAIA = 100
        const val MINEIA_VIALIKI_POST_1 = 101
        const val MINEIA_VIALIKI_POST_2 = 102
        const val MINEIA_VIALIKI_POST_3 = 103
        const val MINEIA_VIALIKI_POST_4 = 104
        const val MINEIA_VIALIKI_POST_5 = 105
        const val MINEIA_VIALIKI_POST_6 = 106
        const val MINEIA_VIALIKI_TYDZEN = 107
        const val MINEIA_SVITLY_TYDZEN = 108
        const val MINEIA_MESIACHNAIA = 109
    }

    init {
        datMinALL.add(SlugbovyiaTextuData(6, "Нядзеля Тамаша (Антыпасха)", "ndz_tamasza_viaczernia_subota", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха)", "ndz_tamasza_jutran", JUTRAN, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха)", "ndz_tamasza_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(7, "Нядзеля Тамаша (Антыпасха)", "ndz_tamasza_uvieczary", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(8, "Панядзелак пасьля нядзелі Тамаша", "ndz_tamasza_paniadzielak", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(9, "Аўторак пасьля нядзелі Тамаша", "ndz_tamasza_autorak", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(10, "Серада пасьля нядзелі Тамаша", "ndz_tamasza_sierada", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(11, "Чацьвер пасьля нядзелі Тамаша", "ndz_tamasza_czacvier", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(12, "Пятніца пасьля нядзелі Тамаша", "ndz_tamasza_piatnica", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(14, "Нядзеля міраносіцаў", "ndz_miranosic_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(20, "Нядзеля расслабленага", "ndz_rasslablenaha_viaczernia_u_subotu_vieczaram", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(21, "Нядзеля расслабленага", "ndz_rasslablenaha_jutran", JUTRAN, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(21, "Нядзеля расслабленага", "ndz_rasslablenaha_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(21, "Нядзеля расслабленага", "ndz_rasslablenaha_uvieczary_viaczernia", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(23, "Палова сьвята Пяцідзясятніцы", "palova_sviata_vialikadnia_viaczernia_u_autorak", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(24, "Палова сьвята Пяцідзясятніцы", "palova_sviata_vialikadnia_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(28, "Нядзеля самаранкі", "ndz_samaranki_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(35, "Нядзеля сьлепанароджанага", "ndz_slepanarodz_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "paniadzielak_sv_ducha_ndz_viaczaram", VIACZERNIA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "zychod_sv_ducha_jutran", JUTRAN, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))
        datMinALL.add(SlugbovyiaTextuData(49, "Тройца (Сёмуха)", "zychod_sv_ducha_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_SVIATOCHNAIA))

        datMinALL.add(SlugbovyiaTextuData(-49, "Вячэрня ў нядзелю сырную вeчарам", "bogashlugbovya12_1", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-48, "Панядзeлак 1-га тыдня посту ўвeчары", "bogashlugbovya12_2", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-47, "Аўтoрак 1-га тыдня посту ўвeчары", "bogashlugbovya12_3", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-46, "Сeрада 1-га тыдня посту ўвeчары", "bogashlugbovya12_4", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-45, "Чацьвeр 1-га тыдня посту ўвeчары", "bogashlugbovya12_5", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-44, "Пятнiца 1-га тыдня пoсту ўвeчары", "bogashlugbovya12_6", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-43, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Вячэрня", "bogashlugbovya12_7", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Ютрань", "bogashlugbovya12_8", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))
        datMinALL.add(SlugbovyiaTextuData(-42, "1-ая Нядзeля пoсту (Нядзeля праваслаўя) Літургія сьвятoга Васіля Вялiкага", "bogashlugbovya12_9", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_1))

        datMinALL.add(SlugbovyiaTextuData(-42, "1-ая нядзеля посту ўвечары", "bogashlugbovya13_1", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-41, "Панядзелак 2-га тыдня посту ўвечары", "bogashlugbovya13_2", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-40, "Аўторак 2-га тыдня посту ўвечары", "bogashlugbovya13_3", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-39, "Серада 2-га тыдня посту ўвечары", "bogashlugbovya13_4", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-38, "Чацьвер 2-га тыдня посту ўвечары", "bogashlugbovya13_5", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-37, "Пятніца 2-га тыдня посту ўвечары", "bogashlugbovya13_6", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Вячэрня Ютрань", "bogashlugbovya13_7", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))
        datMinALL.add(SlugbovyiaTextuData(-35, "2-ая нядзеля Вялікага посту Літургія сьвятога Васіля Вялікага", "bogashlugbovya13_8", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_2))

        datMinALL.add(SlugbovyiaTextuData(-35, "2-ая нядзеля посту ўвечары", "bogashlugbovya14_1", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-34, "Панядзелак 3-га тыдня посту ўвечары", "bogashlugbovya14_2", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-33, "Аўторак 3-га тыдня посту ўвечары", "bogashlugbovya14_3", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-32, "Серада 3-га тыдня посту ўвечары", "bogashlugbovya14_4", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-31, "Чацьвер 3-га тыдня посту ўвечары", "bogashlugbovya14_5", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-30, "Пятніца 3-га тыдня посту ўвечары", "bogashlugbovya14_6", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-29, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Вячэрня", "bogashlugbovya14_7", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Ютрань", "bogashlugbovya14_8", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))
        datMinALL.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту (Нядзеля пакланеньня Сьвятому Крыжу Гасподняму) Літургія сьвятога Васіля Вялікага", "bogashlugbovya14_9", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_3))

        datMinALL.add(SlugbovyiaTextuData(-28, "3-яя нядзеля посту ўвечары", "bogashlugbovya15_1", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-27, "Панядзелак 4-га тыдня посту ўвечары", "bogashlugbovya15_2", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-26, "Аўторак 4-га тыдня посту ўвечары", "bogashlugbovya15_3", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-25, "Серада 4-га тыдня посту ўвечары", "bogashlugbovya15_4", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-24, "Чацьвер 4-га тыдня посту ўвечары", "bogashlugbovya15_5", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-23, "Пятніца 4-га тыдня посту ўвечары", "bogashlugbovya15_6", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-22, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Вячэрня", "bogashlugbovya15_7", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Ютрань", "bogashlugbovya15_8", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))
        datMinALL.add(SlugbovyiaTextuData(-21, "4-ая нядзеля Вялікага посту (Успамін сьвятога айца нашага Яна Лесьвічніка) Літургія сьвятога Васіля Вялікага", "bogashlugbovya15_9", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_4))

        datMinALL.add(SlugbovyiaTextuData(-21, "4-ая нядзеля посту ўвечары", "bogashlugbovya16_1", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-20, "Панядзелак 5-га тыдня посту ўвечары", "bogashlugbovya16_2", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-19, "Аўторак 5-га тыдня посту ўвечары", "bogashlugbovya16_3", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-18, "Серада 5-га тыдня посту ўвечары", "bogashlugbovya16_4", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-17, "Чацьвер 5-га тыдня посту ўвечары", "bogashlugbovya16_5", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-16, "Пятніца 5-га тыдня посту ўвечары", "bogashlugbovya16_6", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-15, "Субота Акафісту", "bogashlugbovya16_7", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-15, "Літургія ў суботу Акафісту", "bogashlugbovya16_8", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-15, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Вячэрня", "bogashlugbovya16_9", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Ютрань", "bogashlugbovya16_10", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))
        datMinALL.add(SlugbovyiaTextuData(-14, "5-ая нядзеля Вялікага посту (Памяць сьвятое Маці нашае Марыі Ягіпецкай) Літургія сьвятога Васіля Вялікага", "bogashlugbovya16_11", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_5))

        datMinALL.add(SlugbovyiaTextuData(-14, "5-ая нядзеля посту ўвечары", "bogashlugbovya17_1", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-13, "Панядзелак 6-га тыдня посту ўвечары", "bogashlugbovya17_2", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-12, "Аўторак 6-га тыдня посту ўвечары", "bogashlugbovya17_3", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-11, "Серада 6-га тыдня посту ўвечары", "bogashlugbovya17_4", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-10, "Чацьвер 6-га тыдня посту ўвечары", "bogashlugbovya17_5", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-9, "Пятніца 6-га тыдня посту ўвечары", "bogashlugbovya17_6", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-8, "Субота Лазара", "bogashlugbovya17_7", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))
        datMinALL.add(SlugbovyiaTextuData(-8, "Субота Лазара", "bogashlugbovya17_8", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_POST_6))

        datMinALL.add(SlugbovyiaTextuData(-7, "Уваход у Ерусалім Госпада, Бога і Збаўцы нашага Ісуса Хрыста (Вербніца)", "bogashlugbovya17_9", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-6, "Вялікі панядзелак", "vialiki_paniadzielak", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-6, "Вялікі панядзелак", "vialiki_paniadzielak_liturhija_raniej_asviacz_darou", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-6, "Вялікі панядзелак", "vialiki_paniadzielak_hadzina_6", VIALHADZINY, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-5, "Вялікі аўторак", "vialiki_autorak", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-4, "Вялікая серада", "vialikaja_sierada", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-4, "Вялікая серада", "vialikaja_sierada_liturhija_raniej_asviacz_darou", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-3, "Вялікі чацьвер", "vialiki_czacvier", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "jutran_vial_piatn_12jevanhellau", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "vialikaja_piatnica_viaczernia", VIACZERNIA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-2, "Вялікая пятніца", "vialikaja_piatnica_mal_paviaczernica", PAVIACHERNICA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-1, "Вялікая субота", "vialikaja_subota_paunocznica", PAUNOCHNICA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-1, "Вялікая субота", "vialikaja_subota_jutran", JUTRAN, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(-1, "Вялікая субота", "vialikaja_subota_viaczernia_liturhija", LITURHIJA, pasxa = true, mineia = MINEIA_VIALIKI_TYDZEN))

        datMinALL.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "vialikdzien_jutran", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(0, "Уваскрасеньне Госпада Бога і Збаўцы нашага Ісуса Хрыста (Вялікдзень)", "vialikdzien_viaczernia", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "u_svietly_paniadzielak", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "l_svietly_paniadzielak", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "v_svietly_paniadzielak", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(1, "Сьветлы панядзелак", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "u_svietly_autorak", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "l_svietly_autorak", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "v_svietly_autorak", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(2, "Сьветлы аўторак", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(3, "Сьветлая серада", "u_svietlaja_sierada", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(3, "Сьветлая серада", "l_svietlaja_sierada", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(3, "Сьветлая серада", "v_svietlaja_sierada", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(3, "Сьветлая серада", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "u_svietly_czacvier", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "l_svietly_czacvier", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "v_svietly_czacvier", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(4, "Сьветлы чацьвер", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "u_svietlaja_piatnica", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "l_svietlaja_piatnica", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "v_svietlaja_piatnica", VIACZERNIA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(5, "Сьветлая пятніца", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(6, "Сьветлая субота", "u_svietlaja_subota", JUTRAN, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(6, "Сьветлая субота", "l_svietlaja_subota", LITURHIJA, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))
        datMinALL.add(SlugbovyiaTextuData(6, "Сьветлая субота", "vielikodnyja_hadziny", VIALHADZINY, pasxa = true, mineia = MINEIA_SVITLY_TYDZEN))

        datMinALL.add(SlugbovyiaTextuData(313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(313, "Сабор сьвятога арханёла Міхаіла і ўсіх анёльскіх сілаў", "mm_08_11_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(318, "Між сьвятымі айца нашага Яна Залатавуснага", "mm_13_11_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(318, "Між сьвятымі айца нашага Яна Залатавуснага", "mm_13_11_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(1, "Малебен на Новы год", "mm_01_01_malebien_novy_hod", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня; сьвятаначальніка Сільвестра, папы Рымскага", "mm_02_01_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня; Сабор 70-ці апосталаў, пачэснага Тэактыста", "mm_04_01_pieradsv_bohazjaulennia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(128, "Жырoвiцкaй iкoны Maцi Бoжae", "mm_07_05_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(142, "Сьвятых роўнаапостальных Канстанціна і Алены", "mm_21_05_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(142, "Сьвятых роўнаапостальных Канстанціна і Алены", "mm_21_05_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(144, "Сьвятое маці нашае Еўфрасіньні Полацкай", "mm_23_05_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(181, "Сьвятых слаўных i годных пахвалы апосталаў Пятра i Паўла", "mm_29_06_piatra_i_paula_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(182, "Сабор сьвятых 12-ці апосталаў", "mm_30_06_sabor_12_apostalau_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(245, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "mm_01_09_novy_hod_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(245, "Пачатак царкоўнага году і сьвятога айца нашага Сямёна Стоўпніка", "mm_01_09_novy_hod_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(249, "Сьвятога прарока Захара, бацькі Яна Хрысьціцеля, і праведнае Альжбеты, ягонае маці", "mm_05_09_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(251, "Перадсьвяцьце Нараджэньня Багародзіцы і сьвятога мучаніка Сазонта", "mm_07_09_pieradsv_naradz_baharodz_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(252, "Нараджэньне Найсьвяцейшае Багародзіцы", "mm_08_09_naradzennie_baharodzicy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(253, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых Якіма і Ганны", "mm_09_09_pasviaccie_naradz_baharodz_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(254, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятых мучаніц Мінадоры, Мітрадоры і Німфадоры", "mm_10_09_pasviaccie_naradz_baharodz_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(255, "Пасьвяцьце Нараджэньня Багародзіцы і сьвятое маці нашае Тадоры", "mm_11_09_pasviaccie_naradz_baharodz_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(256, "Адданьне сьвята Нараджэньня Багародзіцы", "mm_12_09_addannie_naradzennia_baharodzicy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(257, "Перадсьвяцьце Ўзвышэньня, памяць аднаўленьня царквы Ўваскрасеньня і сьвятога мучаніка Карніла", "mm_13_09_pieradsv_uzvyszennia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "mm_14_09_uzvyszennie_kryza_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "mm_14_09_uzvyszennie_kryza_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(258, "Сусьветнае Ўзвышэньне Пачэснага і Жыцьцядайнага Крыжа", "mm_14_09_uzvyszennie_kryza_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(259, "Пасьвяцьце Ўзвышэньня і сьвятога вялікамучаніка Мікіты", "mm_15_09_pasviaccie_uzvyszennia_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "mm_11_17_10_ndz_ajcou_7susvietnaha_saboru_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(AICOU_VII_SUSVETNAGA_SABORY, "Нядзеля сьвятых айцоў VII Сусьветнага сабору", "mm_11_17_10_ndz_ajcou_7susvietnaha_saboru_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(NIADZELIA_PRA_AICOU, "Нядзеля праайцоў", "mm_11_17_12_ndz_praajcou_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(NIADZELIA_AICOU_VI_SABORY, "Нядзеля сьвятых Айцоў першых шасьці Сабораў", "mm_13_19_ndz_ajcou_pierszych_szasci_saborau_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(NIADZELIA_AICOU_VI_SABORY, "Нядзеля сьвятых Айцоў першых шасьці Сабораў", "mm_13_19_07_ndz_ajcou_pierszych_szasci_saborau_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(218, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "mm_05_08_pieradsv_pieramianiennia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(218, "Перадсьвяцьце Перамяненьня і сьв. муч. Яўсігнея", "mm_05_08_pieradsv_pieramianiennia_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(219, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "mm_06_08_pieramianiennie_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(219, "Перамяненьне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "mm_06_08_pieramianiennie_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(222, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "mm_09_08_pasviaccie_pieramianiennia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(222, "Пасьвяцьце Перамяненьня і сьв. апостала Мацея", "mm_09_08_pasviaccie_pieramianennia_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(228, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_15_08_uspiennie_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(228, "Усьпеньне Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_15_08_uspiennie_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(223, "Пасьвяцьце Перамяненьня і сьв. мучаніка Лаўрына", "mm_10_08_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(227, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "mm_14_08_pieradsv_uspiennia_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(227, "Перадсьвяцьце Ўсьпеньня і сьв. прарока Міхея", "mm_14_08_pieradsv_uspiennia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(1, "Абрэзаньне Гасподняе; сьвятаначальніка Васіля Вялікага, архібіск. Кесарыі Кападакійскай", "mm_01_01_abrezannie_viachernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "viachernia_mineia_sviatochnaia4", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(2, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(3, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(4, "Перадсьвяцьце Богазьяўленьня", "viachernia_mineia_sviatochnaia2", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "viachernia_mineia_sviatochnaia3", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(7, "Пасьвяцьце Богазьяўленьня. Сабор сьв. Яна, Прадвесьніка і Хрысьціцеля", "viachernia_mineia_sviatochnaia5", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(16, "Пакланеньне кайданам апостала Пятра", "viachernia_mineia_sviatochnaia6", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(30, "Трох сьвятаначальнікаў: Васіля Вялікага, Рыгора Багаслова і Яна Залатавуснага", "viachernia_mineia_sviatochnaia7", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_jutran", JUTRAN, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(39, "Узьнясеньне Госпада нашага Ісуса Хрыста", "uzniasienne_viaczernia", VIACZERNIA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(42, "Нядзеля сьвятых айцоў I-га Усяленскага Сабору", "ndz_ajcou_1susviet_saboru_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(55, "Нядзеля ўсіх сьвятых", "ndz_usich_sviatych_viaczernia_u_subotu_uvieczary", VIACZERNIA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(56, "Нядзеля ўсіх сьвятых", "ndz_usich_sviatych_jutran", JUTRAN, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(56, "Нядзеля ўсіх сьвятых", "ndz_usich_sviatych_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(63, "Нядзеля ўсіх сьвятых беларускага народу", "ndz_usich_sv_biel_narodu_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(275, "Покрыва Найсьвяцейшай Багародзіцы", "mm_01_10_pokryva_baharodzicy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(326, "Уваход у храм Найсьвяцейшай Багародзіцы", "mm_21_11_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(326, "Уваход у храм Найсьвяцейшай Багародзіцы", "mm_21_11_uvachod_u_sviatyniu_baharodzicy_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(327, "Пасьвяцьце Ўваходу ў сьвятыню Найсьвяцейшай Багародзіцы і сьвятога апостала Халімона", "mm_22_11_pasviaccie_uvachodu_baharodzicy_apostala_chalimona_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(6, "Богазьяўленьне Збаўцы нашага Ісуса Хрыста", "mm_06_01_bohazjaulennie_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "mm_05_01_sv_vieczar_bohazjaulennia_vial_hadziny", VIALHADZINY))
        datMinALL.add(SlugbovyiaTextuData(5, "Чаканьне Богазьяўленьня (Сьвяты вечар перад Богазьяўленьнем)", "mm_05_01_sv_vieczar_bohazjaulennia_abiednica", ABIEDNICA))
        datMinALL.add(SlugbovyiaTextuData(341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "mm_06_12_mikoly_cudatvorcy_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "mm_06_12_mikoly_cudatvorcy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(341, "Між сьвятымі айца нашага Міколы Цудатворца, архібіскупа Мірлікійскага", "mm_06_12_mikoly_cudatvorcy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "mm_12_11_sviatamucz_jazafat_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "mm_12_11_sviatamucz_jazafat_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(317, "Сьвятога сьвятамучаніка Язафата, архібіскупа Полацкага", "mm_12_11_sviatamucz_jazafat_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(355, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "mm_20_12_rastvo_peradsviaccie_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(355, "Перадсьвяцьце Нараджэньня Госпада нашага Ісуса Хрыста і сьв. сьвятамучаніка Ігната Баганосца", "mm_20_12_rastvo_peradsviaccie_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_abednica", ABIEDNICA))
        datMinALL.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(359, "Чаканьне (Сьвяты вечар) Нараджэньня Госпада нашага Ісуса Хрыста і сьв. мучаніцы Яўгеніі", "mm_24_12_rastvo_sv_vieczar_vial_hadziny", VIALHADZINY))
        datMinALL.add(SlugbovyiaTextuData(360, "Нараджэньне Госпада, Бога і Збаўцы нашага Ісуса Хрыста", "mm_25_12_naradzennie_chrystova_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(361, "Сабор Найсьвяцейшай Багародзіцы", "mm_26_12_sabor_baharodzicy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(363, "20 тысячаў мучанікаў Нікамедыйскіх", "mm_28_12_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(-70, "Нядзеля мытніка і фарысэя", "ndz_mytnika_i_faryseja_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(33, "Сустрэча Госпада нашага Ісуса Хрыста (ГРАМНІЦЫ)", "mm_02_02_sustrecza_hospada_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(-56, "Нядзеля мясапусная, пра Страшны суд", "ndz_miasapusnaja_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(-63, "Нядзеля блуднага сына", "ndz_bludnaha_syna_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(-49, "Нядзеля сырная", "ndz_syrnaja_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(66, "Сьвятога і роўнага апосталам Мятода, настаўніка славянскага і архібіскупа Мараўскага", "mm_06_04_miatoda_marauskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(74, "Пачэснага айца нашага Бенядыкта", "mm_14_03_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(77, "Аляксея, чалавека Божага", "mm_17_03_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(78, "Між сьвятымі айца нашага Кірылы, біскупа Ерусалімскага", "mm_18_03_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(114, "Сьвятога вялікамучаніка Юрыя", "mm_23_04_juryja_pieramozcy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(114, "Сьвятога вялікамучаніка Юрыя", "mm_23_04_juryja_pieramozcy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(84, "Перадсьвяцьце Дабравешчаньня", "mm_24_03_pieradsv_dabravieszczannia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(84, "Перадсьвяцьце Дабравешчаньня", "mm_24_03_pieradsv_dabravieszczannia_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "mm_25_03_dabravieszczannie_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "mm_25_03_dabravieszczannie_liturhija_subota_niadziela", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(85, "Дабравешчаньне Найсьвяцейшай Багародзіцы", "mm_25_03_dabravieszczannie_viaczernia_z_liturhijaj", VIACZERNIA_Z_LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(105, "Між сьвятымі айца нашага Марціна Вызнаўцы, папы Рымскага", "mm_14_04_marcina_papy_rymskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(114, "Сьвятога вялікамучаніка Юрыя", "mm_23_04_juryja_pieramozcy_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(116, "Сьвятога апостала і евангеліста Марка", "mm_25_04_apostala_marka_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(116, "Сьвятога апостала і евангеліста Марка", "mm_25_04_apostala_marka_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(118, "Сьвятога сьвятамучаніка Сямёна, сваяка Гасподняга", "mm_27_04_siamiona_svajaka_haspodniaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(119, "Між сьвятымі айца нашага Кірылы, біскупа Тураўскага", "mm_28_04_kiryly_turauskaha_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(119, "Між сьвятымі айца нашага Кірылы, біскупа Тураўскага", "mm_28_04_kiryly_turauskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(121, "Сьвятога апостала Якуба, брата Яна Багаслова", "mm_30_04_apostala_jakuba_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(121, "Сьвятога апостала Якуба, брата Яна Багаслова", "mm_30_04_apostala_jakuba_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(123, "Між сьвятымі айца нашага Апанаса, архібіскупа Александрыйскага", "mm_02_05_apanasa_aleksandryjskaha_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(123, "Між сьвятымі айца нашага Апанаса, архібіскупа Александрыйскага", "mm_02_05_apanasa_aleksandryjskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "mm_11_05_nastaunikau_slavianau_kiryly_i_miatoda_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "mm_11_05_nastaunikau_slavianau_kiryly_i_miatoda_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(132, "Сьвятых і роўных апосталам настаўнікаў славянскіх Кірылы і Мятода", "mm_11_05_nastaunikau_slavianau_kiryly_i_miatoda_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(127, "Ёва Шматпакутнага", "mm_06_05_jova_szmatpakutnaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(131, "Сьвятога апостала Сымана Зілота", "mm_10_05_apostala_symana_zilota_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(161, "Кірылы, архібіскупа Александрыйскага", "mm_09_06_kiryly_aleksandryjskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(163, "Сьвятых апосталаў Баўтрамея і Варнавы", "mm_11_06_apostalau_bautramieja_i_varnavy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(163, "Сьвятых апосталаў Баўтрамея і Варнавы", "mm_11_06_apostalau_bautramieja_i_varnavy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(164, "Сьвятога Анупрэя Вялікага", "mm_12_06_anupreja_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(164, "Сьвятога Анупрэя Вялікага", "mm_12_06_anupreja_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(24, "Палова сьвята Пяцідзясятніцы", "palova_sviata_vialikadnia_liturhija", LITURHIJA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(23, "Палова сьвята Пяцідзясятніцы", "palova_sviata_vialikadnia_viaczernia_u_autorak", VIACZERNIA, pasxa = true))
        datMinALL.add(SlugbovyiaTextuData(171, "Сьвятога апостала Юды, сваяка Гасподняга", "mm_19_06_apostala_judy_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(171, "Сьвятога апостала Юды, сваяка Гасподняга", "mm_19_06_apostala_judy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(176, "Нараджэньне сьвятога прарока Прадвесьніка і Хрысьціцеля", "mm_24_06_jana_chrysciciela_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(176, "Нараджэньне сьвятога прарока Прадвесьніка і Хрысьціцеля", "mm_24_06_naradzennie_jana_chrysciciela_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(181, "Сьвятых слаўных i годных пахвалы апосталаў Пятра i Паўла", "mm_29_06_apostalau_piatra_i_paula_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(182, "Сабор сьвятых 12-ці апосталаў", "mm_30_06_sabor_12_apostalau_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(192, "Сьвятога Антона Кіевапячорскага", "mm_10_07_antona_kijevapiaczorskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(193, "Сьвятой мучаніцы Аўхіміі ўсяхвальнай", "mm_11_07_auchimii_usiachvalnaj_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(201, "Сьвятое маці нашае Макрыны, сястры сьв. Васіля Вялікага", "mm_19_07_maci_makryny_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(202, "Сьвятога прарока Ільлі", "mm_20_07_praroka_illi_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(202, "Сьвятога прарока Ільлі", "mm_20_07_praroka_illi_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(202, "Сьвятога прарока Ільлі", "mm_20_07_praroka_illi_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(204, "Сьвятой і роўнай апосталам Марыі Магдалены", "mm_22_07_maryi_mahdaleny_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(207, "Усьпеньне сьвятой Ганны, маці Найсьвяцейшай Багародзіцы", "mm_25_07_uspiennie_sviatoj_hanny_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(207, "Усьпеньне сьвятой Ганны, маці Найсьвяцейшай Багародзіцы", "mm_25_07_uspiennie_sviatoj_hanny_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(209, "Сьвятога вялікамучаніка і лекара Панцялеймана", "mm_27_07_vialikamuczanika_pancialejmana_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(217, "7-мі юнакоў Эфэскіх", "mm_04_08_7junakou_efeskich_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(224, "Пасьвяцьце Перамяненьня і сьвятога мучаніка Еўпла", "mm_11_08_pasviaccie_pieramianinnie_muczanika_jeupla_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(231, "Пасьвяцьце Ўсьпеньня і сьвятых мучанікаў Флёра і Лаўра", "mm_18_08_pasviaccie_uspiennia_mucz_flora_laura_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(232, "Пасьвяцьце Ўсьпеньня і сьвятога мучаніка Андрэя Сотніка, а з ім 2593 мучанікаў", "mm_19_08_pasviaccie_uspiennia_mucz_andreja_sotnika_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(233, "Пасьвяцьце Ўсьпеньня і сьвятога прарока Самуіла", "mm_20_08_pasviaccie_uspiennia_praroka_samuila_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(234, "Пасьвяцьце Ўсьпеньня, сьв. апостала Тадэя і сьвятога айца нашага Аўрама Смаленскага і вучня ягонага Ахрэма", "mm_21_08_pasviaccie_uspiennia_apostala_tadeja_aurama_smalenskaha_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(234, "Пасьвяцьце Ўсьпеньня, сьв. апостала Тадэя і сьвятога айца нашага Аўрама Смаленскага і вучня ягонага Ахрэма", "mm_21_08_pasviaccie_uspiennia_apostala_tadeja_aurama_smalenskaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(235, "Пасьвяцьце Ўсьпеньня, сьв. мучаніка Агатоніка і сьв. мучаніка Люпа", "mm_22_08_pasviaccie_uspiennia_muczanikau_ahatonika_lupa_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(236, "Адданьне сьвята Ўсьпеньня Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_23_08_addannie_sviata_uspiennia_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(236, "Адданьне сьвята Ўсьпеньня Найсьвяцейшай Багародзіцы і Заўсёды Дзевы Марыі", "mm_23_08_addannie_sviata_uspiennia_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(237, "Сьвятамучаніка Яўціха, вучня сьвятога Яна Багаслова", "mm_24_08_sviatamuczanika_jaucicha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(238, "Перанясеньне мошчаў апостала Баўтрамея, апостала Ціта, вучня сьвятога Паўла", "mm_25_08_pieranias_moszczau_apostala_bautramieja_apostala_cita_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(242, "Адсячэньне галавы сьвятога Яна Хрысьціцеля", "mm_29_08_adsiaczennie_halavy_jana_chrysciciela_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(242, "Адсячэньне галавы сьвятога Яна Хрысьціцеля", "mm_29_08_adsiaczennie_halavy_jana_chrysciciela_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(242, "Адсячэньне галавы сьвятога Яна Хрысьціцеля", "mm_29_08_adsiaczennie_halavy_jana_chrysciciela_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(252, "Нараджэньне Найсьвяцейшай Багародзіцы", "mm_08_09_naradzennie_baharodzicy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(257, "Перадсьвяцьце Ўзвышэньня, памяць аднаўленьня царквы Ўваскрасеньня і сьвятога мучаніка Карніла", "mm_13_09_pieradsv_uzvyszennia_adnaul_carkvy_uvaskr_mucz_karnila_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(262, "Пасьвяцьце Ўзвышэньня і сьвятога Яўмена, біскупа Гартынскага, цудатворцы", "mm_18_09_pasviaccie_uzvyszennia_jaumiena_cudatvorcy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(263, "Пасьвяцьце Ўзвышэньня і сьвятых мучанікаў Трахіма, Савацея і Дарымедонта", "mm_19_09_pasviaccie_uzvyszennia_muczanikau_trachima_savacieja_darymiedonta_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(267, "Зачацьце сьвятога Яна Хрысьціцеля", "mm_23_09_zaczaccie_jana_chrysciciela_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(267, "Зачацьце сьвятога Яна Хрысьціцеля", "mm_23_09_zaczaccie_jana_chrysciciela_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(270, "Перастаўленьне сьвятога апостала і евангеліста Яна Багаслова", "mm_26_09_pierastaulennie_apostala_jana_bahaslova_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(270, "Перастаўленьне сьвятога апостала і евангеліста Яна Багаслова", "mm_26_09_pierastaulennie_apostala_jana_bahaslova_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(275, "Покрыва Найсьвяцейшай Багародзіцы", "mm_01_10_pokryva_baharodzicy_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(276, "Сьвятамучаніка Кіпрыяна і сьв. мучаніцы Юстыны", "mm_02_10_sviatamuczanika_kipryjana_muczanicy_justyny_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(277, "Сьвятога Дзяніса Арэапагіта", "mm_03_10_dzianisa_aerapahita_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(278, "Сьвятамучаніка Ератэя, біскупа Атэнскага", "mm_04_10_sviatamuczanika_jerateja_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(280, "Сьвятога апостала Тамаша", "mm_06_10_apostala_tamasza_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(280, "Сьвятога апостала Тамаша", "mm_06_10_apostala_tamasza_viaczarnia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(283, "Сьвятога апостала Якуба Алфеевага", "mm_09_10_apostala_jakuba_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(283, "Сьвятога апостала Якуба Алфеевага", "mm_09_10_apostala_jakuba_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(285, "Сьвятога апостала Піліпа, аднаго з сямі дыяканаў", "mm_11_10_apostala_pilipa_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(275, "Покрыва Найсьвяцейшай Багародзіцы", "mm_01_10_pokryva_baharodzicy_jutran", JUTRAN))
        datMinALL.add(SlugbovyiaTextuData(297, "Сьвятога апостала Якуба, сваяка Гасподняга", "mm_23_10_apostala_jakuba_svajaka_haspodniaha_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(300, "Сьвятога вялікамучаніка Зьмітра і ўспамін землятрусу", "mm_26_10_vialikamuczanika_zmitra_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(300, "Сьвятога вялікамучаніка Зьмітра і ўспамін землятрусу", "mm_26_10_vialikamuczanika_zmitra_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(316, "Пачэснага айца нашага Тодара Студыта", "mm_11_11_todara_studyta_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(319, "Сьвятога апостала Піліпа", "mm_14_11_apostala_pilipa_liturhija", LITURHIJA))
        datMinALL.add(SlugbovyiaTextuData(319, "Сьвятога апостала Піліпа", "mm_14_11_apostala_pilipa_viaczernia", VIACZERNIA))
        datMinALL.add(SlugbovyiaTextuData(321, "Сьвятога апостала і евангеліста Мацьвея", "mm_16_11_apostala_macvieja_liturhija", LITURHIJA))
    }

    fun getTydzen1(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_1
        }
        return list.sorted()
    }

    fun getTydzen2(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_2
        }
        return list.sorted()
    }

    fun getTydzen3(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_3
        }
        return list.sorted()
    }

    fun getTydzen4(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_4
        }
        return list.sorted()
    }

    fun getTydzen5(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_5
        }
        return list.sorted()
    }

    fun getTydzen6(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_POST_6
        }
        return list.sorted()
    }

    fun getVilikiTydzen(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_VIALIKI_TYDZEN
        }
        return list.sorted()
    }

    fun getSvetlyTydzen(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_SVITLY_TYDZEN
        }
        return list.sorted()
    }

    fun getMineiaMesiachnaia(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_MESIACHNAIA
        }
        return list.sorted()
    }

    fun getMineiaSviatochnaia(): List<SlugbovyiaTextuData> {
        val list = datMinALL.filter {
            it.mineia == MINEIA_SVIATOCHNAIA
        }
        return list.sorted()
    }

    fun getResource(day: Int, dayOfYear: Int, sluzba: Int): String {
        val checkDay = getRealDay(day)
        datMinALL.forEach {
            if (it.pasxa) {
                if (checkDay == it.day && it.sluzba == sluzba) {
                    return it.resource
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == sluzba) {
                    return it.resource
                }
            }
        }
        return "0"
    }

    fun getResource(day: Int, pasxa: Boolean, sluzba: Int): String {
        val checkDay = getFictionalDay(day)
        datMinALL.forEach {
            if (checkDay == it.day && pasxa == it.pasxa && it.sluzba == sluzba) {
                return it.resource
            }
        }
        return "0"
    }

    fun getTitle(resource: String): String {
        datMinALL.forEach {
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
        datMinALL.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == JUTRAN) {
                    return true
                }
            }
        }
        return false
    }

    fun checkLiturgia(day: Int, dayOfYear: Int): Boolean {
        datMinALL.forEach {
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
        return false
    }

    fun checkViachernia(day: Int, dayOfYear: Int): Boolean {
        datMinALL.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == VIACZERNIA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkPavichrrnica(day: Int, dayOfYear: Int): Boolean {
        datMinALL.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == PAVIACHERNICA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkPaunochnica(day: Int, dayOfYear: Int): Boolean {
        datMinALL.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == PAUNOCHNICA) {
                    return true
                }
            }
        }
        return false
    }

    fun checkVialikiaGadziny(day: Int, dayOfYear: Int): Boolean {
        datMinALL.forEach {
            if (it.pasxa) {
                if (day == it.day && it.sluzba == VIALHADZINY) {
                    return true
                }
            } else {
                if (getFictionalDay(dayOfYear) == it.day && it.sluzba == VIALHADZINY) {
                    return true
                }
            }
        }
        return false
    }

    fun getRealDay(day: Int): Int {
        var realDay = day
        val calendar = GregorianCalendar()
        var addDay = 0
        if (!calendar.isLeapYear(calendar.get(Calendar.YEAR))) addDay = 1
        when (day) {
            AICOU_VII_SUSVETNAGA_SABORY -> {
                //Айцоў VII Сусьветнага Сабору
                for (i in 11..17) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.OCTOBER, i)
                    val wik = calendar.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        realDay = calendar.get(Calendar.DAY_OF_YEAR) + addDay
                    }
                }
            }

            NIADZELIA_PRA_AICOU -> {
                //Нядзеля праайцоў
                for (i in 11..17) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
                    val wik = calendar.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        realDay = calendar.get(Calendar.DAY_OF_YEAR) + addDay
                    }
                }
            }

            NIADZELIA_AICOU_VI_SABORY -> {
                //Нядзеля сьвятых Айцоў першых шасьці Сабораў
                for (i in 13..19) {
                    calendar.set(calendar.get(Calendar.YEAR), Calendar.JULY, i)
                    val wik = calendar.get(Calendar.DAY_OF_WEEK)
                    if (wik == Calendar.SUNDAY) {
                        realDay = calendar.get(Calendar.DAY_OF_YEAR) + addDay
                    }
                }
            }
        }
        return realDay
    }

    private fun getFictionalDay(dayOfYear: Int): Int {
        var fictionalDay = dayOfYear
        val calendar = GregorianCalendar()
        //Айцоў VII Сусьветнага Сабору
        for (i in 11..17) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.OCTOBER, i)
            var addDay = 0
            if (!calendar.isLeapYear(calendar.get(Calendar.YEAR))) addDay = 1
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && dayOfYear == calendar[Calendar.DAY_OF_YEAR] + addDay) {
                fictionalDay = AICOU_VII_SUSVETNAGA_SABORY
            }
        }
        //Нядзеля праайцоў
        for (i in 11..17) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, i)
            var addDay = 0
            if (!calendar.isLeapYear(calendar.get(Calendar.YEAR))) addDay = 1
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && dayOfYear == calendar[Calendar.DAY_OF_YEAR] + addDay) {
                fictionalDay = NIADZELIA_PRA_AICOU
            }
        }
        //Нядзеля сьвятых Айцоў першых шасьці Сабораў
        for (i in 13..19) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.JULY, i)
            var addDay = 0
            if (!calendar.isLeapYear(calendar.get(Calendar.YEAR))) addDay = 1
            val wik = calendar.get(Calendar.DAY_OF_WEEK)
            if (wik == Calendar.SUNDAY && dayOfYear == calendar[Calendar.DAY_OF_YEAR] + addDay) {
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
