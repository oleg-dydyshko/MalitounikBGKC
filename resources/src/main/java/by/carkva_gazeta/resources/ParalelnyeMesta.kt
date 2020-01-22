package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created by oleg on 18.10.16
 */
internal class ParalelnyeMesta {
    @SuppressLint("SetTextI18n")
    fun paralel(context: Context, cytanneSours: String, cytanneParalelnye: String, semuxa: Boolean): ArrayList<TextViewRobotoCondensed> {
        var cytanneSours1 = cytanneSours
        val arrayList = ArrayList<TextViewRobotoCondensed>()
        var textViewZag: TextViewRobotoCondensed
        var textViewOpis: TextViewRobotoCondensed
        if (semuxa) {
            if (cytanneSours1.contains("Быт")) {
                cytanneSours1 = cytanneSours1.replace("Быт", "Быц")
            }
            if (cytanneSours1 == "Исх") {
                cytanneSours1 = cytanneSours1.replace("Исх", "Вых")
            }
            if (cytanneSours1.contains("Лев")) {
                cytanneSours1 = cytanneSours1.replace("Лев", "Ляв")
            }
            if (cytanneSours1.contains("Числа")) {
                cytanneSours1 = cytanneSours1.replace("Числа", "Лікі")
            }
            if (cytanneSours1.contains("Втор")) {
                cytanneSours1 = cytanneSours1.replace("Втор", "Дрг")
            }
            if (cytanneSours1.contains("Руфь")) {
                cytanneSours1 = cytanneSours1.replace("Руфь", "Рут")
            }
            if (cytanneSours1.contains("1 Пар")) {
                cytanneSours1 = cytanneSours1.replace("1 Пар", "1 Лет")
            }
            if (cytanneSours1.contains("2 Пар")) {
                cytanneSours1 = cytanneSours1.replace("2 Пар", "2 Лет")
            }
            if (cytanneSours1.contains("1 Езд")) {
                cytanneSours1 = cytanneSours1.replace("1 Езд", "1 Эзд")
            }
            if (cytanneSours1.contains("Неем")) {
                cytanneSours1 = cytanneSours1.replace("Неем", "Нээм")
            }
            if (cytanneSours1.contains("Есф")) {
                cytanneSours1 = cytanneSours1.replace("Есф", "Эст")
            }
            if (cytanneSours1.contains("Иов")) {
                cytanneSours1 = cytanneSours1.replace("Иов", "Ёва")
            }
            if (cytanneSours1.contains("Притч")) {
                cytanneSours1 = cytanneSours1.replace("Притч", "Высл")
            }
            if (cytanneSours1.contains("Еккл")) {
                cytanneSours1 = cytanneSours1.replace("Еккл", "Экл")
            }
            if (cytanneSours1.contains("Песн")) {
                cytanneSours1 = cytanneSours1.replace("Песн", "Псн")
            }
            if (cytanneSours1 == "Ис") {
                cytanneSours1 = cytanneSours1.replace("Ис", "Іс")
            }
            if (cytanneSours1.contains("Иер")) {
                cytanneSours1 = cytanneSours1.replace("Иер", "Ер")
            }
            if (cytanneSours1.contains("Плач Иер")) {
                cytanneSours1 = cytanneSours1.replace("Плач Иер", "Пасл Ер")
            }
            if (cytanneSours1.contains("Иез")) {
                cytanneSours1 = cytanneSours1.replace("Иез", "Езк")
            }
            if (cytanneSours1.contains("Ос")) {
                cytanneSours1 = cytanneSours1.replace("Ос", "Ас")
            }
            if (cytanneSours1.contains("Иоиль")) {
                cytanneSours1 = cytanneSours1.replace("Иоиль", "Ёіл")
            }
            if (cytanneSours1.contains("Авдий")) {
                cytanneSours1 = cytanneSours1.replace("Авдий", "Аўдз")
            }
            if (cytanneSours1.contains("Иона")) {
                cytanneSours1 = cytanneSours1.replace("Иона", "Ёны")
            }
            if (cytanneSours1.contains("Мих")) {
                cytanneSours1 = cytanneSours1.replace("Мих", "Міх")
            }
            if (cytanneSours1.contains("Наум")) {
                cytanneSours1 = cytanneSours1.replace("Наум", "Нвм")
            }
            if (cytanneSours1.contains("Аввакум")) {
                cytanneSours1 = cytanneSours1.replace("Аввакум", "Абк")
            }
            if (cytanneSours1.contains("Сафония")) {
                cytanneSours1 = cytanneSours1.replace("Сафония", "Саф")
            }
            if (cytanneSours1.contains("Аггей")) {
                cytanneSours1 = cytanneSours1.replace("Аггей", "Аг")
            }
            if (cytanneSours1.contains("Мф")) {
                cytanneSours1 = cytanneSours1.replace("Мф", "Мц")
            }
            if (cytanneSours1.contains("Лк")) {
                cytanneSours1 = cytanneSours1.replace("Лк", "Лук")
            }
            if (cytanneSours1.contains("Ин")) {
                cytanneSours1 = cytanneSours1.replace("Ин", "Ян")
            }
            if (cytanneSours1.contains("Деян")) {
                cytanneSours1 = cytanneSours1.replace("Деян", "Дз")
            }
            if (cytanneSours1.contains("Иак")) {
                cytanneSours1 = cytanneSours1.replace("Иак", "Як")
            }
            if (cytanneSours1.contains("1 Пет")) {
                cytanneSours1 = cytanneSours1.replace("1 Пет", "1 Пт")
            }
            if (cytanneSours1.contains("2 Пет")) {
                cytanneSours1 = cytanneSours1.replace("2 Пет", "2 Пт")
            }
            if (cytanneSours1.contains("1 Ин")) {
                cytanneSours1 = cytanneSours1.replace("1 Ин", "1 Ян")
            }
            if (cytanneSours1.contains("2 Ин")) {
                cytanneSours1 = cytanneSours1.replace("2 Ин", "2 Ян")
            }
            if (cytanneSours1.contains("3 Ин")) {
                cytanneSours1 = cytanneSours1.replace("3 Ин", "3 Ян")
            }
            if (cytanneSours1.contains("Иуд")) {
                cytanneSours1 = cytanneSours1.replace("Иуд", "Юд")
            }
            if (cytanneSours1.contains("Рим")) {
                cytanneSours1 = cytanneSours1.replace("Рим", "Рым")
            }
            if (cytanneSours1.contains("1 Кор")) {
                cytanneSours1 = cytanneSours1.replace("1 Кор", "1 Кар")
            }
            if (cytanneSours1.contains("2 Кор")) {
                cytanneSours1 = cytanneSours1.replace("2 Кор", "2 Кар")
            }
            if (cytanneSours1.contains("Еф")) {
                cytanneSours1 = cytanneSours1.replace("Еф", "Эф")
            }
            if (cytanneSours1.contains("Флп")) {
                cytanneSours1 = cytanneSours1.replace("Флп", "Плп")
            }
            if (cytanneSours1.contains("Кол")) {
                cytanneSours1 = cytanneSours1.replace("Кол", "Клс")
            }
            if (cytanneSours1.contains("1 Тим")) {
                cytanneSours1 = cytanneSours1.replace("1 Тим", "1 Цім")
            }
            if (cytanneSours1.contains("2 Тим")) {
                cytanneSours1 = cytanneSours1.replace("2 Тим", "2 Цім")
            }
            if (cytanneSours1.contains("Тит")) {
                cytanneSours1 = cytanneSours1.replace("Тит", "Ціт")
            }
            if (cytanneSours1.contains("Евр")) {
                cytanneSours1 = cytanneSours1.replace("Евр", "Гбр")
            }
            if (cytanneSours1.contains("Откр")) {
                cytanneSours1 = cytanneSours1.replace("Откр", "Адкр")
            }
        }
        val chten = cytanneParalelnye.split(";").toTypedArray()
        val textViewSours = TextViewRobotoCondensed(context)
        textViewSours.setTextIsSelectable(true)
        textViewSours.setTypeface(null, Typeface.BOLD_ITALIC)
        textViewSours.setPadding(0, 0, 0, 10)
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val fontBiblia = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)
        var dzenNoch = k.getBoolean("dzen_noch", false)
        if (dzenNoch) {
            textViewSours.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
        } else {
            textViewSours.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
        }
        textViewSours.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
        textViewSours.text = context.resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_smoll, cytanneSours1)
        arrayList.add(textViewSours)
        for (aChten in chten) {
            var nomerglavy = 1
            val fit = aChten.trim { it <= ' ' }
            var nachalo: String
            var konec: String
            val bible = biblia(fit)
            kniga = bible[0]
            nazva = bible[1]
            nazvaBel = bible[2]
            nomer = bible[3].toInt()
            // Пс 88:12-13; 135:5; 145:6; Сир 18:1; Ин 1:3; Пс 22
// Быт 13:15; 15:7, 18, 15-16; 26:3-4; Втор 34:4; 1 Тим 2:13
            val split = fit.split(",").toTypedArray()
            for (aSplit in split) {
                val splitres = aSplit.trim { it <= ' ' }
                val s2 = splitres.indexOf(" ", 2)
                val a1 = splitres.indexOf(".")
                if (s2 != -1) {
                    if (a1 != -1) {
                        nomerglavy = splitres.substring(s2 + 1, a1).toInt()
                        val a2 = splitres.indexOf("-")
                        if (a2 != -1) {
                            nachalo = splitres.substring(a1 + 1, a2)
                            konec = splitres.substring(a2 + 1)
                        } else {
                            nachalo = splitres.substring(a1 + 1)
                            konec = nachalo
                        }
                    } else {
                        nomerglavy = splitres.substring(s2 + 1).toInt()
                        nachalo = "1"
                        konec = "+-+"
                    }
                } else {
                    val a2 = splitres.indexOf("-")
                    if (a1 != -1) {
                        nomerglavy = splitres.substring(0, a1).toInt()
                        if (a2 != -1) {
                            nachalo = splitres.substring(a1 + 1, a2)
                            konec = splitres.substring(a2 + 1)
                        } else {
                            nachalo = splitres.substring(a1 + 1)
                            konec = nachalo
                        }
                    } else {
                        if (a2 != -1) {
                            nachalo = splitres.substring(0, a2)
                            konec = splitres.substring(a2 + 1)
                        } else {
                            nachalo = splitres
                            konec = nachalo
                        }
                    }
                }
                val r = context.resources
                var inputStream: InputStream? = null
                if (semuxa) {
                    inputStream = when (nomer) {
                        1 -> r.openRawResource(R.raw.biblias1)
                        2 -> r.openRawResource(R.raw.biblias2)
                        3 -> r.openRawResource(R.raw.biblias3)
                        4 -> r.openRawResource(R.raw.biblias4)
                        5 -> r.openRawResource(R.raw.biblias5)
                        6 -> r.openRawResource(R.raw.biblias6)
                        7 -> r.openRawResource(R.raw.biblias7)
                        8 -> r.openRawResource(R.raw.biblias8)
                        9 -> r.openRawResource(R.raw.biblias9)
                        10 -> r.openRawResource(R.raw.biblias10)
                        11 -> r.openRawResource(R.raw.biblias11)
                        12 -> r.openRawResource(R.raw.biblias12)
                        13 -> r.openRawResource(R.raw.biblias13)
                        14 -> r.openRawResource(R.raw.biblias14)
                        15 -> r.openRawResource(R.raw.biblias15)
                        16 -> r.openRawResource(R.raw.biblias16)
                        20 -> r.openRawResource(R.raw.biblias17)
                        21 -> r.openRawResource(R.raw.biblias18)
                        22 -> r.openRawResource(R.raw.biblias19)
                        23 -> r.openRawResource(R.raw.biblias20)
                        24 -> r.openRawResource(R.raw.biblias21)
                        25 -> r.openRawResource(R.raw.biblias22)
                        28 -> r.openRawResource(R.raw.biblias23)
                        29 -> r.openRawResource(R.raw.biblias24)
                        30 -> r.openRawResource(R.raw.biblias25)
                        33 -> r.openRawResource(R.raw.biblias26)
                        34 -> r.openRawResource(R.raw.biblias27)
                        35 -> r.openRawResource(R.raw.biblias28)
                        36 -> r.openRawResource(R.raw.biblias29)
                        37 -> r.openRawResource(R.raw.biblias30)
                        38 -> r.openRawResource(R.raw.biblias31)
                        39 -> r.openRawResource(R.raw.biblias32)
                        40 -> r.openRawResource(R.raw.biblias33)
                        41 -> r.openRawResource(R.raw.biblias34)
                        42 -> r.openRawResource(R.raw.biblias35)
                        43 -> r.openRawResource(R.raw.biblias36)
                        44 -> r.openRawResource(R.raw.biblias37)
                        45 -> r.openRawResource(R.raw.biblias38)
                        46 -> r.openRawResource(R.raw.biblias39)
                        51 -> r.openRawResource(R.raw.biblian1)
                        52 -> r.openRawResource(R.raw.biblian2)
                        53 -> r.openRawResource(R.raw.biblian3)
                        54 -> r.openRawResource(R.raw.biblian4)
                        55 -> r.openRawResource(R.raw.biblian5)
                        56 -> r.openRawResource(R.raw.biblian6)
                        57 -> r.openRawResource(R.raw.biblian7)
                        58 -> r.openRawResource(R.raw.biblian8)
                        59 -> r.openRawResource(R.raw.biblian9)
                        60 -> r.openRawResource(R.raw.biblian10)
                        61 -> r.openRawResource(R.raw.biblian11)
                        62 -> r.openRawResource(R.raw.biblian12)
                        63 -> r.openRawResource(R.raw.biblian13)
                        64 -> r.openRawResource(R.raw.biblian14)
                        65 -> r.openRawResource(R.raw.biblian15)
                        66 -> r.openRawResource(R.raw.biblian16)
                        67 -> r.openRawResource(R.raw.biblian17)
                        68 -> r.openRawResource(R.raw.biblian18)
                        69 -> r.openRawResource(R.raw.biblian19)
                        70 -> r.openRawResource(R.raw.biblian20)
                        71 -> r.openRawResource(R.raw.biblian21)
                        72 -> r.openRawResource(R.raw.biblian22)
                        73 -> r.openRawResource(R.raw.biblian23)
                        74 -> r.openRawResource(R.raw.biblian24)
                        75 -> r.openRawResource(R.raw.biblian25)
                        76 -> r.openRawResource(R.raw.biblian26)
                        77 -> r.openRawResource(R.raw.biblian27)
                        else -> null
                    }
                } else {
                    if (nomer == 1) inputStream = r.openRawResource(R.raw.sinaidals1)
                    if (nomer == 2) inputStream = r.openRawResource(R.raw.sinaidals2)
                    if (nomer == 3) inputStream = r.openRawResource(R.raw.sinaidals3)
                    if (nomer == 4) inputStream = r.openRawResource(R.raw.sinaidals4)
                    if (nomer == 5) inputStream = r.openRawResource(R.raw.sinaidals5)
                    if (nomer == 6) inputStream = r.openRawResource(R.raw.sinaidals6)
                    if (nomer == 7) inputStream = r.openRawResource(R.raw.sinaidals7)
                    if (nomer == 8) inputStream = r.openRawResource(R.raw.sinaidals8)
                    if (nomer == 9) inputStream = r.openRawResource(R.raw.sinaidals9)
                    if (nomer == 10) inputStream = r.openRawResource(R.raw.sinaidals10)
                    if (nomer == 11) inputStream = r.openRawResource(R.raw.sinaidals11)
                    if (nomer == 12) inputStream = r.openRawResource(R.raw.sinaidals12)
                    if (nomer == 13) inputStream = r.openRawResource(R.raw.sinaidals13)
                    if (nomer == 14) inputStream = r.openRawResource(R.raw.sinaidals14)
                    if (nomer == 15) inputStream = r.openRawResource(R.raw.sinaidals15)
                    if (nomer == 16) inputStream = r.openRawResource(R.raw.sinaidals16)
                    if (nomer == 17) inputStream = r.openRawResource(R.raw.sinaidals17)
                    if (nomer == 18) inputStream = r.openRawResource(R.raw.sinaidals18)
                    if (nomer == 19) inputStream = r.openRawResource(R.raw.sinaidals19)
                    if (nomer == 20) inputStream = r.openRawResource(R.raw.sinaidals20)
                    if (nomer == 21) inputStream = r.openRawResource(R.raw.sinaidals21)
                    if (nomer == 22) inputStream = r.openRawResource(R.raw.sinaidals22)
                    if (nomer == 23) inputStream = r.openRawResource(R.raw.sinaidals23)
                    if (nomer == 24) inputStream = r.openRawResource(R.raw.sinaidals24)
                    if (nomer == 25) inputStream = r.openRawResource(R.raw.sinaidals25)
                    if (nomer == 26) inputStream = r.openRawResource(R.raw.sinaidals26)
                    if (nomer == 27) inputStream = r.openRawResource(R.raw.sinaidals27)
                    if (nomer == 28) inputStream = r.openRawResource(R.raw.sinaidals28)
                    if (nomer == 29) inputStream = r.openRawResource(R.raw.sinaidals29)
                    if (nomer == 30) inputStream = r.openRawResource(R.raw.sinaidals30)
                    if (nomer == 31) inputStream = r.openRawResource(R.raw.sinaidals31)
                    if (nomer == 32) inputStream = r.openRawResource(R.raw.sinaidals32)
                    if (nomer == 33) inputStream = r.openRawResource(R.raw.sinaidals33)
                    if (nomer == 34) inputStream = r.openRawResource(R.raw.sinaidals34)
                    if (nomer == 35) inputStream = r.openRawResource(R.raw.sinaidals35)
                    if (nomer == 36) inputStream = r.openRawResource(R.raw.sinaidals36)
                    if (nomer == 37) inputStream = r.openRawResource(R.raw.sinaidals37)
                    if (nomer == 38) inputStream = r.openRawResource(R.raw.sinaidals38)
                    if (nomer == 39) inputStream = r.openRawResource(R.raw.sinaidals39)
                    if (nomer == 40) inputStream = r.openRawResource(R.raw.sinaidals40)
                    if (nomer == 41) inputStream = r.openRawResource(R.raw.sinaidals41)
                    if (nomer == 42) inputStream = r.openRawResource(R.raw.sinaidals42)
                    if (nomer == 43) inputStream = r.openRawResource(R.raw.sinaidals43)
                    if (nomer == 44) inputStream = r.openRawResource(R.raw.sinaidals44)
                    if (nomer == 45) inputStream = r.openRawResource(R.raw.sinaidals45)
                    if (nomer == 46) inputStream = r.openRawResource(R.raw.sinaidals46)
                    if (nomer == 47) inputStream = r.openRawResource(R.raw.sinaidals47)
                    if (nomer == 48) inputStream = r.openRawResource(R.raw.sinaidals48)
                    if (nomer == 49) inputStream = r.openRawResource(R.raw.sinaidals49)
                    if (nomer == 50) inputStream = r.openRawResource(R.raw.sinaidals50)
                    if (nomer == 51) inputStream = r.openRawResource(R.raw.sinaidaln1)
                    if (nomer == 52) inputStream = r.openRawResource(R.raw.sinaidaln2)
                    if (nomer == 53) inputStream = r.openRawResource(R.raw.sinaidaln3)
                    if (nomer == 54) inputStream = r.openRawResource(R.raw.sinaidaln4)
                    if (nomer == 55) inputStream = r.openRawResource(R.raw.sinaidaln5)
                    if (nomer == 56) inputStream = r.openRawResource(R.raw.sinaidaln6)
                    if (nomer == 57) inputStream = r.openRawResource(R.raw.sinaidaln7)
                    if (nomer == 58) inputStream = r.openRawResource(R.raw.sinaidaln8)
                    if (nomer == 59) inputStream = r.openRawResource(R.raw.sinaidaln9)
                    if (nomer == 60) inputStream = r.openRawResource(R.raw.sinaidaln10)
                    if (nomer == 61) inputStream = r.openRawResource(R.raw.sinaidaln11)
                    if (nomer == 62) inputStream = r.openRawResource(R.raw.sinaidaln12)
                    if (nomer == 63) inputStream = r.openRawResource(R.raw.sinaidaln13)
                    if (nomer == 64) inputStream = r.openRawResource(R.raw.sinaidaln14)
                    if (nomer == 65) inputStream = r.openRawResource(R.raw.sinaidaln15)
                    if (nomer == 66) inputStream = r.openRawResource(R.raw.sinaidaln16)
                    if (nomer == 67) inputStream = r.openRawResource(R.raw.sinaidaln17)
                    if (nomer == 68) inputStream = r.openRawResource(R.raw.sinaidaln18)
                    if (nomer == 69) inputStream = r.openRawResource(R.raw.sinaidaln19)
                    if (nomer == 70) inputStream = r.openRawResource(R.raw.sinaidaln20)
                    if (nomer == 71) inputStream = r.openRawResource(R.raw.sinaidaln21)
                    if (nomer == 72) inputStream = r.openRawResource(R.raw.sinaidaln22)
                    if (nomer == 73) inputStream = r.openRawResource(R.raw.sinaidaln23)
                    if (nomer == 74) inputStream = r.openRawResource(R.raw.sinaidaln24)
                    if (nomer == 75) inputStream = r.openRawResource(R.raw.sinaidaln25)
                    if (nomer == 76) inputStream = r.openRawResource(R.raw.sinaidaln26)
                    if (nomer == 77) inputStream = r.openRawResource(R.raw.sinaidaln27)
                }
                if (inputStream != null) {
                    val isr = InputStreamReader(inputStream)
                    val reader = BufferedReader(isr)
                    var line: String
                    val builder = StringBuilder()
                    reader.forEachLine { it ->
                        line = it
                        if (line.contains("//")) {
                            val t1 = line.indexOf("//")
                            line = line.substring(0, t1).trim { it <= ' ' }
                            if (line != "") builder.append(line).append("\n")
                        } else {
                            builder.append(line).append("\n")
                        }
                    }
                    inputStream.close()
                    val split2 = builder.toString().split("===").toTypedArray()
                    var r1 = split2[nomerglavy].trim { it <= ' ' }
                    var r2: String
                    val vN = r1.indexOf(nachalo)
                    val vK1 = r1.indexOf(konec)
                    val vK = r1.indexOf("\n", vK1)
                    if (semuxa && nomer == 22) {
                        r1 = r1.replace("\n", "<br>\n")
                        val r3 = r1.split("\n").toTypedArray()
                        val sb = StringBuilder()
                        for (w in nachalo.toInt()..konec.toInt()) {
                            sb.append(r3[w - 1])
                        }
                        r2 = sb.toString()
                    } else {
                        r2 = if (vK1 != -1) {
                            if (vK != -1) {
                                r1.substring(vN, vK)
                            } else {
                                r1.substring(vN)
                            }
                        } else {
                            r1
                        }
                    }
                    textViewZag = TextViewRobotoCondensed(context)
                    textViewOpis = TextViewRobotoCondensed(context)
                    textViewZag.setTextIsSelectable(true)
                    textViewOpis.setTextIsSelectable(true)
                    dzenNoch = k.getBoolean("dzen_noch", false)
                    if (dzenNoch) {
                        textViewZag.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                        textViewOpis.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                    } else {
                        textViewZag.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
                        textViewOpis.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
                    }
                    textViewZag.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                    textViewZag.setTypeface(null, Typeface.BOLD)
                    if (semuxa) nazva = nazvaBel
                    val kon: String = when {
                        nachalo == konec -> {
                            "$nazva $nomerglavy.$nachalo"
                        }
                        konec.contains("+-+") -> {
                            "$nazva $nomerglavy"
                        }
                        else -> {
                            "$nazva $nomerglavy.$nachalo-$konec"
                        }
                    }
                    textViewZag.text = kon
                    arrayList.add(textViewZag)
                    textViewOpis.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                    if (semuxa && nomer == 22) { //CaseInsensitiveResourcesFontLoader fontLoader = new CaseInsensitiveResourcesFontLoader();
                        textViewOpis.text = MainActivity.fromHtml(context.resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_opis, r2))
                    } else textViewOpis.text = context.resources.getString(by.carkva_gazeta.malitounik.R.string.paralel_opis, r2)
                } else {
                    textViewZag = TextViewRobotoCondensed(context)
                    textViewOpis = TextViewRobotoCondensed(context)
                    textViewZag.setTextIsSelectable(true)
                    textViewOpis.setTextIsSelectable(true)
                    dzenNoch = k.getBoolean("dzen_noch", false)
                    if (dzenNoch) {
                        textViewZag.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                        textViewOpis.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorIcons))
                    } else {
                        textViewZag.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
                        textViewOpis.setTextColor(ContextCompat.getColor(context, by.carkva_gazeta.malitounik.R.color.colorPrimary_text))
                    }
                    textViewOpis.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                    textViewZag.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontBiblia)
                    textViewZag.setTypeface(null, Typeface.BOLD)
                    if (semuxa) nazva = nazvaBel
                    val kon: String = when {
                        nachalo == konec -> {
                            "$nazva $nomerglavy.$nachalo"
                        }
                        konec.contains("+-+") -> {
                            "$nazva $nomerglavy"
                        }
                        else -> {
                            "$nazva $nomerglavy.$nachalo-$konec"
                        }
                    }
                    textViewZag.text = kon
                    arrayList.add(textViewZag)
                    textViewOpis.text = context.resources.getString(by.carkva_gazeta.malitounik.R.string.semuxa_maran_ata_error) + "\n"
                }
                arrayList.add(textViewOpis)
            }
        }
        return arrayList
    }

    companion object {
        var kniga: String = "Быт"
        var nazva: String = "Бытие"
        var nazvaBel: String = "Быцьцё"
        var nomer = 1
        fun biblia(kniga: String): Array<String> {
            val bible = arrayOf(Companion.kniga, nazva, nazvaBel, nomer.toString())
            if (kniga.contains("Быт") || kniga.contains("Быц")) {
                bible[0] = "Быт" // Сокращение по русски
                bible[1] = "Бытие" // Название по русски
                bible[2] = "Быцьцё" // Название по Белорусски
                bible[3] = "1" // Номер книги
            }
            if (kniga.contains("Исх") || kniga.contains("Вых")) {
                bible[0] = "Исх"
                bible[1] = "Исход"
                bible[2] = "Выхад"
                bible[3] = "2"
            }
            if (kniga.contains("Лев") || kniga.contains("Ляв")) {
                bible[0] = "Лев"
                bible[1] = "Левит"
                bible[2] = "Лявіт"
                bible[3] = "3"
            }
            if (kniga.contains("Чис") || kniga.contains("Лікі")) {
                bible[0] = "Числа"
                bible[1] = "Числа"
                bible[2] = "Лікі"
                bible[3] = "4"
            }
            if (kniga.contains("Втор") || kniga.contains("Дрг")) {
                bible[0] = "Втор"
                bible[1] = "Второзаконие"
                bible[2] = "Другі Закон"
                bible[3] = "5"
            }
            if (kniga.contains("Нав")) {
                bible[0] = "Нав"
                bible[1] = "Иисуса Навина"
                bible[2] = "Ісуса сына Нава"
                bible[3] = "6"
            }
            if (kniga.contains("Суд")) {
                bible[0] = "Суд"
                bible[1] = "Судей израилевых"
                bible[2] = "Судзьдзяў"
                bible[3] = "7"
            }
            if (kniga.contains("Руфь") || kniga.contains("Рут")) {
                bible[0] = "Руфь"
                bible[1] = "Руфи"
                bible[2] = "Рут"
                bible[3] = "8"
            }
            if (kniga.contains("1 Цар")) {
                bible[0] = "1 Цар"
                bible[1] = "1-я Царств"
                bible[2] = "1-я Царстваў"
                bible[3] = "9"
            }
            if (kniga.contains("2 Цар")) {
                bible[0] = "2 Цар"
                bible[1] = "2-я Царств"
                bible[2] = "2-я Царстваў"
                bible[3] = "10"
            }
            if (kniga.contains("3 Цар")) {
                bible[0] = "3 Цар"
                bible[1] = "3-я Царств"
                bible[2] = "3-я Царстваў"
                bible[3] = "11"
            }
            if (kniga.contains("4 Цар")) {
                bible[0] = "4 Цар"
                bible[1] = "4-я Царств"
                bible[2] = "4-я Царстваў"
                bible[3] = "12"
            }
            if (kniga.contains("1 Пар") || kniga.contains("1 Лет")) {
                bible[0] = "1 Пар"
                bible[1] = "1-я Паралипоменон"
                bible[2] = "1-я Летапісаў"
                bible[3] = "13"
            }
            if (kniga.contains("2 Пар") || kniga.contains("2 Лет")) {
                bible[0] = "2 Пар"
                bible[1] = "2-я Паралипоменон"
                bible[2] = "2-я Летапісаў"
                bible[3] = "14"
            }
            if (kniga.contains("1 Езд") || kniga.contains("1 Эзд")) {
                bible[0] = "1 Езд"
                bible[1] = "1-я Ездры"
                bible[2] = "1-я Эздры"
                bible[3] = "15"
            }
            if (kniga.contains("Неем") || kniga.contains("Нээм")) {
                bible[0] = "Неем"
                bible[1] = "Неемии"
                bible[2] = "Нээміі"
                bible[3] = "16"
            }
            if (kniga.contains("2 Езд") || kniga.contains("2 Эзд")) {
                bible[0] = "2 Езд"
                bible[1] = "2-я Ездры"
                bible[2] = "2-я Эздры"
                bible[3] = "17"
            }
            if (kniga.contains("Тов") || kniga.contains("Тав")) {
                bible[0] = "Тов"
                bible[1] = "Товита"
                bible[2] = "Тавіта"
                bible[3] = "18"
            }
            if (kniga.contains("Иудифь") || kniga.contains("Юдт")) {
                bible[0] = "Иудифь"
                bible[1] = "Иудифи"
                bible[2] = "Юдыты"
                bible[3] = "19"
            }
            if (kniga.contains("Есф") || kniga.contains("Эст")) {
                bible[0] = "Есф"
                bible[1] = "Есфири"
                bible[2] = "Эстэр"
                bible[3] = "20"
            }
            if (kniga.contains("Иов") || kniga.contains("Ёва")) {
                bible[0] = "Иов"
                bible[1] = "Иова"
                bible[2] = "Ёва"
                bible[3] = "21"
            }
            if (kniga.contains("Пс")) {
                bible[0] = "Пс"
                bible[1] = "Псалтирь"
                bible[2] = "Псалтыр"
                bible[3] = "22"
            }
            if (kniga.contains("Притч") || kniga.contains("Высл")) {
                bible[0] = "Притч"
                bible[1] = "Притчи Соломона"
                bible[2] = "Выслоўяў Саламонавых"
                bible[3] = "23"
            }
            if (kniga.contains("Еккл") || kniga.contains("Экл")) {
                bible[0] = "Еккл"
                bible[1] = "Екклезиаста"
                bible[2] = "Эклезіяста"
                bible[3] = "24"
            }
            if (kniga.contains("Песн") || kniga.contains("Псн")) {
                bible[0] = "Песн"
                bible[1] = "Песнь песней Соломона"
                bible[2] = "Найвышэйшая Песьня Саламонава"
                bible[3] = "25"
            }
            if (kniga.contains("Прем") || kniga.contains("Мдр")) {
                bible[0] = "Прем"
                bible[1] = "Премудрости Соломона"
                bible[2] = "Мудрасьці Саламона"
                bible[3] = "26"
            }
            if (kniga.contains("Сир") || kniga.contains("Сір")) {
                bible[0] = "Сир"
                bible[1] = "Премудрости Иисуса, сына Сирахова"
                bible[2] = "Мудрасьці Ісуса, сына Сірахава"
                bible[3] = "27"
            }
            if ((kniga.contains("Ис") && !kniga.contains("Исх")) || kniga.contains("Іс")) {
                bible[0] = "Ис"
                bible[1] = "Исаии"
                bible[2] = "Ісаі"
                bible[3] = "28"
            }
            if (kniga.contains("Иер") || kniga.contains("Ер")) {
                bible[0] = "Иер"
                bible[1] = "Иеремии"
                bible[2] = "Ераміі"
                bible[3] = "29"
            }
            if (kniga.contains("Плач")) {
                bible[0] = "Плач Иер"
                bible[1] = "Плач Иеремии"
                bible[2] = "Ераміін Плач"
                bible[3] = "30"
            }
            if (kniga.contains("Посл Иер") || kniga.contains("Пасл Ер")) {
                bible[0] = "Посл Иеремии"
                bible[1] = "Послание Иеремии"
                bible[2] = "Пасланьне Ераміі"
                bible[3] = "31"
            }
            if (kniga.contains("Вар") || kniga.contains("Бар")) {
                bible[0] = "Вар"
                bible[1] = "Варуха"
                bible[2] = "Баруха"
                bible[3] = "32"
            }
            if (kniga.contains("Иез") || kniga.contains("Езк")) {
                bible[0] = "Иез"
                bible[1] = "Иезекииля"
                bible[2] = "Езэкііля"
                bible[3] = "33"
            }
            if (kniga.contains("Дан")) {
                bible[0] = "Дан"
                bible[1] = "Даниила"
                bible[2] = "Данііла"
                bible[3] = "34"
            }
            if (kniga.contains("Ос") || kniga.contains("Ас")) {
                bible[0] = "Ос"
                bible[1] = "Осии"
                bible[2] = "Асіі"
                bible[3] = "35"
            }
            if (kniga.contains("Иоил") || kniga.contains("Ёіл")) {
                bible[0] = "Иоиль"
                bible[1] = "Иоиля"
                bible[2] = "Ёіля"
                bible[3] = "36"
            }
            if (kniga.contains("Ам")) {
                bible[0] = "Ам"
                bible[1] = "Амоса"
                bible[2] = "Амоса"
                bible[3] = "37"
            }
            if (kniga.contains("Авд") || kniga.contains("Аўдз")) {
                bible[0] = "Авдий"
                bible[1] = "Авдия"
                bible[2] = "Аўдзея"
                bible[3] = "38"
            }
            if (kniga.contains("Иона") || kniga.contains("Ёны")) {
                bible[0] = "Иона"
                bible[1] = "Ионы"
                bible[2] = "Ёны"
                bible[3] = "39"
            }
            if (kniga.contains("Мих") || kniga.contains("Міх")) {
                bible[0] = "Мих"
                bible[1] = "Михея"
                bible[2] = "Міхея"
                bible[3] = "40"
            }
            if (kniga.contains("Наум") || kniga.contains("Нвм")) {
                bible[0] = "Наум"
                bible[1] = "Наума"
                bible[2] = "Навума"
                bible[3] = "41"
            }
            if (kniga.contains("Авв") || kniga.contains("Абк")) {
                bible[0] = "Аввакум"
                bible[1] = "Аввакума"
                bible[2] = "Абакума"
                bible[3] = "42"
            }
            if (kniga.contains("Соф") || kniga.contains("Саф")) {
                bible[0] = "Сафония"
                bible[1] = "Софонии"
                bible[2] = "Сафона"
                bible[3] = "43"
            }
            if (kniga.contains("Агг") || kniga.contains("Аг")) {
                bible[0] = "Аггей"
                bible[1] = "Аггея"
                bible[2] = "Агея"
                bible[3] = "44"
            }
            if (kniga.contains("Зах")) {
                bible[0] = "Зах"
                bible[1] = "Захарии"
                bible[2] = "Захарыі"
                bible[3] = "45"
            }
            if (kniga.contains("Мал")) {
                bible[0] = "Мал"
                bible[1] = "Малахии"
                bible[2] = "Малахіі"
                bible[3] = "46"
            }
            if (kniga.contains("1 Мак")) {
                bible[0] = "1 Мак"
                bible[1] = "1-я Маккавейская"
                bible[2] = "1-я Макабэяў"
                bible[3] = "47"
            }
            if (kniga.contains("2 Мак")) {
                bible[0] = "2 Мак"
                bible[1] = "2-я Маккавейская"
                bible[2] = "2-я Макабэяў"
                bible[3] = "48"
            }
            if (kniga.contains("3 Мак")) {
                bible[0] = "3 Мак"
                bible[1] = "3-я Маккавейская"
                bible[2] = "3-я Макабэяў"
                bible[3] = "49"
            }
            if (kniga.contains("3 Езд") || kniga.contains("3 Эзд")) {
                bible[1] = "3-я Ездры"
                bible[2] = "3-я Эздры"
                bible[3] = "50"
            }
            if (kniga.contains("Мф") || kniga.contains("Мц")) {
                bible[0] = "Мф"
                bible[1] = "От Матфея"
                bible[2] = "Паводле Мацьвея"
                bible[3] = "51"
            }
            if (kniga.contains("Мк")) {
                bible[0] = "Мк"
                bible[1] = "От Марка"
                bible[2] = "Паводле Марка"
                bible[3] = "52"
            }
            if (kniga.contains("Лк")) {
                bible[0] = "Лк"
                bible[1] = "От Луки"
                bible[2] = "Паводле Лукаша"
                bible[3] = "53"
            }
            if (kniga.contains("Ин") || kniga.contains("Ян")) {
                bible[0] = "Ин"
                bible[1] = "От Иоанна"
                bible[2] = "Паводле Яна"
                bible[3] = "54"
            }
            if (kniga.contains("Деян") || kniga.contains("Дз")) {
                bible[0] = "Деян"
                bible[1] = "Деяния святых апостолов"
                bible[2] = "Дзеі Апосталаў"
                bible[3] = "55"
            }
            if (kniga.contains("Иак") || kniga.contains("Як")) {
                bible[0] = "Иак"
                bible[1] = "Иакова"
                bible[2] = "Якава"
                bible[3] = "56"
            }
            if (kniga.contains("1 Пет") || kniga.contains("1 Пт")) {
                bible[0] = "1 Петр"
                bible[1] = "1-е Петра"
                bible[2] = "1-е Пятра"
                bible[3] = "57"
            }
            if (kniga.contains("2 Пет") || kniga.contains("2 Пт")) {
                bible[0] = "2 Петр"
                bible[1] = "2-е Петра"
                bible[2] = "2-е Пятра"
                bible[3] = "58"
            }
            if (kniga.contains("1 Ин") || kniga.contains("1 Ян")) {
                bible[0] = "1 Ин"
                bible[1] = "1-е Иоанна"
                bible[2] = "1-е Яна Багаслова"
                bible[3] = "59"
            }
            if (kniga.contains("2 Ин") || kniga.contains("2 Ян")) {
                bible[0] = "2 Ин"
                bible[1] = "2-е Иоанна"
                bible[2] = "2-е Яна Багаслова"
                bible[3] = "60"
            }
            if (kniga.contains("3 Ин") || kniga.contains("3 Ян")) {
                bible[0] = "3 Ин"
                bible[1] = "3-е Иоанна"
                bible[2] = "3-е Яна Багаслова"
                bible[3] = "61"
            }
            if (kniga.contains("Иуд") || kniga.contains("Юд") && !kniga.contains("Юдт")) {
                bible[0] = "Иуд"
                bible[1] = "Иуды"
                bible[2] = "Юды"
                bible[3] = "62"
            }
            if (kniga.contains("Рим") || kniga.contains("Рым")) {
                bible[0] = "Рим"
                bible[1] = "Римлянам"
                bible[2] = "Да Рымлянаў"
                bible[3] = "63"
            }
            if (kniga.contains("1 Кор") || kniga.contains("1 Кар")) {
                bible[0] = "1 Кор"
                bible[1] = "1-е Коринфянам"
                bible[2] = "1-е да Карынфянаў"
                bible[3] = "64"
            }
            if (kniga.contains("2 Кор") || kniga.contains("2 Кар")) {
                bible[0] = "2 Кор"
                bible[1] = "2-е Коринфянам"
                bible[2] = "2-е да Карынфянаў"
                bible[3] = "65"
            }
            if (kniga.contains("Гал")) {
                bible[0] = "Гал"
                bible[1] = "Галатам"
                bible[2] = "Да Галятаў"
                bible[3] = "66"
            }
            if (kniga.contains("Еф") || kniga.contains("Эф")) {
                bible[0] = "Еф"
                bible[1] = "Ефесянам"
                bible[2] = "Да Эфэсянаў"
                bible[3] = "67"
            }
            if (kniga.contains("Флп") || kniga.contains("Плп")) {
                bible[0] = "Флп"
                bible[1] = "Филиппийцам"
                bible[2] = "Да Піліпянаў"
                bible[3] = "68"
            }
            if (kniga.contains("Кол") || kniga.contains("Клс")) {
                bible[0] = "Кол"
                bible[1] = "Колоссянам"
                bible[2] = "Да Каласянаў"
                bible[3] = "69"
            }
            if (kniga.contains("1 Фес")) {
                bible[0] = "1 Фес"
                bible[1] = "1-е Фессалоникийцам (Солунянам)"
                bible[2] = "1-е да Фесаланікійцаў"
                bible[3] = "70"
            }
            if (kniga.contains("2 Фес")) {
                bible[0] = "2 Фес"
                bible[1] = "2-е Фессалоникийцам (Солунянам)"
                bible[2] = "2-е да Фесаланікійцаў"
                bible[3] = "71"
            }
            if (kniga.contains("1 Тим") || kniga.contains("1 Цім")) {
                bible[0] = "1 Тим"
                bible[1] = "1-е Тимофею"
                bible[2] = "1-е да Цімафея"
                bible[3] = "72"
            }
            if (kniga.contains("2 Тим") || kniga.contains("2 Цім")) {
                bible[0] = "2 Тим"
                bible[1] = "2-е Тимофею"
                bible[2] = "2-е да Цімафея"
                bible[3] = "73"
            }
            if (kniga.contains("Тит") || kniga.contains("Ціт")) {
                bible[0] = "Тит"
                bible[1] = "Титу"
                bible[2] = "Да Ціта"
                bible[3] = "74"
            }
            if (kniga.contains("Флм")) {
                bible[0] = "Флм"
                bible[1] = "Филимону"
                bible[2] = "Да Філімона"
                bible[3] = "75"
            }
            if (kniga.contains("Евр") || kniga.contains("Гбр")) {
                bible[0] = "Евр"
                bible[1] = "Евреям"
                bible[2] = "Да Габрэяў"
                bible[3] = "76"
            }
            if (kniga.contains("Откр") || kniga.contains("Адкр")) {
                bible[0] = "Откр"
                bible[1] = "Откровение (Апокалипсис)"
                bible[2] = "Адкрыцьцё (Апакаліпсіс)"
                bible[3] = "77"
            }
            return bible
        }
    }
}