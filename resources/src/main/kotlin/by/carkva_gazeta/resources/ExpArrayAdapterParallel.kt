package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.TextViewRobotoCondensed
import by.carkva_gazeta.malitounik.R

internal class ExpArrayAdapterParallel(private val context: Activity, private val stixi: ArrayList<String>, private val kniga: Int, private val glava: Int, private val Zapavet: Boolean, private val mPerevod: Int) : ArrayAdapter<String>(context, R.layout.simple_list_item_bible, stixi) { // 1-Сёмуха, 2-Синоидальный, 3-Псалтырь Надсана
    private val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    private val fontSize = k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE)

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val rootView: View
        val ea: ExpArrayAdapterParallelItems
        if (convertView == null) {
            ea = ExpArrayAdapterParallelItems()
            rootView = context.layoutInflater.inflate(R.layout.simple_list_item_bible, viewGroup, false)
            ea.textView = rootView.findViewById(R.id.label)
            rootView.tag = ea
        } else {
            rootView = convertView
            ea = rootView.tag as ExpArrayAdapterParallelItems
        }
        ea.textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        val parallel = BibliaParallelChtenia()
        var res = "+-+"
        if (Zapavet) {
            if (kniga == 0) {
                res = parallel.kniga51(glava + 1, position + 1)
            }
            if (kniga == 1) {
                res = parallel.kniga52(glava + 1, position + 1)
            }
            if (kniga == 2) {
                res = parallel.kniga53(glava + 1, position + 1)
            }
            if (kniga == 3) {
                res = parallel.kniga54(glava + 1, position + 1)
            }
            if (kniga == 4) {
                res = parallel.kniga55(glava + 1, position + 1)
            }
            if (kniga == 5) {
                res = parallel.kniga56(glava + 1, position + 1)
            }
            if (kniga == 6) {
                res = parallel.kniga57(glava + 1, position + 1)
            }
            if (kniga == 7) {
                res = parallel.kniga58(glava + 1, position + 1)
            }
            if (kniga == 8) {
                res = parallel.kniga59(glava + 1, position + 1)
            }
            if (kniga == 9) {
                res = parallel.kniga60(glava + 1, position + 1)
            }
            if (kniga == 10) {
                res = parallel.kniga61(glava + 1, position + 1)
            }
            if (kniga == 11) {
                res = parallel.kniga62(glava + 1, position + 1)
            }
            if (kniga == 12) {
                res = parallel.kniga63(glava + 1, position + 1)
            }
            if (kniga == 13) {
                res = parallel.kniga64(glava + 1, position + 1)
            }
            if (kniga == 14) {
                res = parallel.kniga65(glava + 1, position + 1)
            }
            if (kniga == 15) {
                res = parallel.kniga66(glava + 1, position + 1)
            }
            if (kniga == 16) {
                res = parallel.kniga67(glava + 1, position + 1)
            }
            if (kniga == 17) {
                res = parallel.kniga68(glava + 1, position + 1)
            }
            if (kniga == 18) {
                res = parallel.kniga69(glava + 1, position + 1)
            }
            if (kniga == 19) {
                res = parallel.kniga70(glava + 1, position + 1)
            }
            if (kniga == 20) {
                res = parallel.kniga71(glava + 1, position + 1)
            }
            if (kniga == 21) {
                res = parallel.kniga72(glava + 1, position + 1)
            }
            if (kniga == 22) {
                res = parallel.kniga73(glava + 1, position + 1)
            }
            if (kniga == 23) {
                res = parallel.kniga74(glava + 1, position + 1)
            }
            if (kniga == 24) {
                res = parallel.kniga75(glava + 1, position + 1)
            }
            if (kniga == 25) {
                res = parallel.kniga76(glava + 1, position + 1)
            }
            if (kniga == 26) {
                res = parallel.kniga77(glava + 1, position + 1)
            }
        } else {
            if (kniga == 0) {
                res = parallel.kniga1(glava + 1, position + 1)
            }
            if (kniga == 1) {
                res = parallel.kniga2(glava + 1, position + 1)
            }
            if (kniga == 2) {
                res = parallel.kniga3(glava + 1, position + 1)
            }
            if (kniga == 3) {
                res = parallel.kniga4(glava + 1, position + 1)
            }
            if (kniga == 4) {
                res = parallel.kniga5(glava + 1, position + 1)
            }
            if (kniga == 5) {
                res = parallel.kniga6(glava + 1, position + 1)
            }
            if (kniga == 6) {
                res = parallel.kniga7(glava + 1, position + 1)
            }
            if (kniga == 7) {
                res = parallel.kniga8(glava + 1, position + 1)
            }
            if (kniga == 8) {
                res = parallel.kniga9(glava + 1, position + 1)
            }
            if (kniga == 9) {
                res = parallel.kniga10(glava + 1, position + 1)
            }
            if (kniga == 10) {
                res = parallel.kniga11(glava + 1, position + 1)
            }
            if (kniga == 11) {
                res = parallel.kniga12(glava + 1, position + 1)
            }
            if (kniga == 12) {
                res = parallel.kniga13(glava + 1, position + 1)
            }
            if (kniga == 13) {
                res = parallel.kniga14(glava + 1, position + 1)
            }
            if (kniga == 14) {
                res = parallel.kniga15(glava + 1, position + 1)
            }
            if (kniga == 15) {
                res = parallel.kniga16(glava + 1, position + 1)
            }
            if (kniga == 16) {
                res = parallel.kniga17(glava + 1, position + 1)
            }
            if (kniga == 17) {
                res = parallel.kniga18(glava + 1, position + 1)
            }
            if (kniga == 18) {
                res = parallel.kniga19(glava + 1, position + 1)
            }
            if (kniga == 19) {
                res = parallel.kniga20(glava + 1, position + 1)
            }
            if (kniga == 20) {
                res = parallel.kniga21(glava + 1, position + 1)
            }
            if (kniga == 21) {
                res = parallel.kniga22(glava + 1, position + 1)
            }
            if (kniga == 22) {
                res = parallel.kniga23(glava + 1, position + 1)
            }
            if (kniga == 23) {
                res = parallel.kniga24(glava + 1, position + 1)
            }
            if (kniga == 24) {
                res = parallel.kniga25(glava + 1, position + 1)
            }
            if (kniga == 25) {
                res = parallel.kniga26(glava + 1, position + 1)
            }
            if (kniga == 26) {
                res = parallel.kniga27(glava + 1, position + 1)
            }
            if (kniga == 27) {
                res = parallel.kniga28(glava + 1, position + 1)
            }
            if (kniga == 28) {
                res = parallel.kniga29(glava + 1, position + 1)
            }
            if (kniga == 29) {
                res = parallel.kniga30(glava + 1, position + 1)
            }
            if (kniga == 30) {
                res = parallel.kniga31(glava + 1, position + 1)
            }
            if (kniga == 31) {
                res = parallel.kniga32(glava + 1, position + 1)
            }
            if (kniga == 32) {
                res = parallel.kniga33(glava + 1, position + 1)
            }
            if (kniga == 33) {
                res = parallel.kniga34(glava + 1, position + 1)
            }
            if (kniga == 34) {
                res = parallel.kniga35(glava + 1, position + 1)
            }
            if (kniga == 35) {
                res = parallel.kniga36(glava + 1, position + 1)
            }
            if (kniga == 36) {
                res = parallel.kniga37(glava + 1, position + 1)
            }
            if (kniga == 37) {
                res = parallel.kniga38(glava + 1, position + 1)
            }
            if (kniga == 38) {
                res = parallel.kniga39(glava + 1, position + 1)
            }
            if (kniga == 39) {
                res = parallel.kniga40(glava + 1, position + 1)
            }
            if (kniga == 40) {
                res = parallel.kniga41(glava + 1, position + 1)
            }
            if (kniga == 41) {
                res = parallel.kniga42(glava + 1, position + 1)
            }
            if (kniga == 42) {
                res = parallel.kniga43(glava + 1, position + 1)
            }
            if (kniga == 43) {
                res = parallel.kniga44(glava + 1, position + 1)
            }
            if (kniga == 44) {
                res = parallel.kniga45(glava + 1, position + 1)
            }
            if (kniga == 45) {
                res = parallel.kniga46(glava + 1, position + 1)
            }
            if (kniga == 46) {
                res = parallel.kniga47(glava + 1, position + 1)
            }
            if (kniga == 47) {
                res = parallel.kniga48(glava + 1, position + 1)
            }
            if (kniga == 48) {
                res = parallel.kniga49(glava + 1, position + 1)
            }
            if (kniga == 49) {
                res = parallel.kniga50(glava + 1, position + 1)
            }
        }
        var stix = stixi[position]
        stix = stix.replace("\\n", "\n")
        if (!Zapavet && kniga == 21 && mPerevod == 1) {
            ea.textView?.text = MainActivity.fromHtml(stix)
        } else {
            ea.textView?.text = stix
        }
        if (!res.contains("+-+")) {
            var zakladka: SpannableStringBuilder? = null
            var space = 1
            if (mPerevod == 1) {
                res = MainActivity.translateToBelarus(res)
                zakladka = setZakladkiSemuxa(position)
                if (zakladka != null)
                    space = 2
            }
            if (mPerevod == 2) {
                zakladka = setZakladkiSinoidal(position)
                if (zakladka != null)
                    space = 2
            }
            val ssb = SpannableStringBuilder(ea.textView?.text).append(zakladka?: "").append("\n").append(res)
            val start = ea.textView?.text?.length ?: 0
            val end = start + space + res.length
            ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(RelativeSizeSpan(0.7f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val pos = BibleGlobalList.checkPosition(glava, position)
            if (pos != -1) {
                if (BibleGlobalList.vydelenie[pos][2] == 1) {
                    if (k.getBoolean("dzen_noch", false)) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_text)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorYelloy)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (BibleGlobalList.vydelenie[pos][4] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            ea.textView?.text = ssb
        } else {
            var zakladka: SpannableStringBuilder? = null
            if (mPerevod == 1) {
                zakladka = setZakladkiSemuxa(position)?: SpannableStringBuilder("")
            }
            if (mPerevod == 2) {
                zakladka = setZakladkiSinoidal(position)?: SpannableStringBuilder("")
            }
            val ssb = SpannableStringBuilder(ea.textView?.text).append(zakladka)
            val end = ea.textView?.length() ?: 0
            val pos = BibleGlobalList.checkPosition(glava, position)
            if (pos != -1) {
                if (BibleGlobalList.vydelenie[pos][2] == 1) {
                    if (k.getBoolean("dzen_noch", false)) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_text)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorYelloy)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (BibleGlobalList.vydelenie[pos][4] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            ea.textView?.text = ssb
        }
        if (BibleGlobalList.bibleCopyList.size > 0 && BibleGlobalList.bibleCopyList.contains(position) && BibleGlobalList.mPedakVisable) {
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setBackgroundResource(R.color.colorprimary_material_dark2)
                ea.textView?.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            } else {
                ea.textView?.setBackgroundResource(R.color.colorDivider)
            }
        } else {
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setBackgroundResource(R.drawable.selector_dark)
                ea.textView?.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            } else {
                ea.textView?.setBackgroundResource(R.drawable.selector_default)
            }
        }
        if (mPerevod == 1) {
            var zav = "0"
            if (Zapavet) zav = "1"
            if (BibleGlobalList.natatkiSemuxa.size > 0) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(zav) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == position) {
                        val ssb = SpannableStringBuilder(ea.textView?.text)
                        val nachalo = ssb.length
                        ssb.append("\nНататка:\n").append(BibleGlobalList.natatkiSemuxa[i].list[5]).append("\n")
                        ssb.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView?.text = ssb
                        break
                    }
                }
            }
        }
        if (mPerevod == 2) {
            var zav = "0"
            if (Zapavet) zav = "1"
            if (BibleGlobalList.natatkiSinodal.size > 0) {
                for (i in BibleGlobalList.natatkiSinodal.indices) {
                    if (BibleGlobalList.natatkiSinodal[i].list[0].contains(zav) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == position) {
                        val ssb = SpannableStringBuilder(ea.textView?.text)
                        val nachalo = ssb.length
                        ssb.append("\nНататка:\n").append(BibleGlobalList.natatkiSinodal[i].list[5]).append("\n")
                        ssb.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView?.text = ssb
                        break
                    }
                }
            }
        }
        return rootView
    }

    private fun setZakladkiSemuxa(position: Int): SpannableStringBuilder? {
        var ssb: SpannableStringBuilder? = null
        var zav = "0"
        if (Zapavet) zav = "1"
        if (BibleGlobalList.zakladkiSemuxa.size > 0) {
            for (i in BibleGlobalList.zakladkiSemuxa.indices) {
                var knigaN = -1
                var knigaS = -1
                var t1: Int
                var t2: Int
                var t3: Int
                var glava1: Int
                val knigaName = BibleGlobalList.zakladkiSemuxa[i].data
                if (knigaName.contains("Паводле Мацьвея")) knigaN = 0
                if (knigaName.contains("Паводле Марка")) knigaN = 1
                if (knigaName.contains("Паводле Лукаша")) knigaN = 2
                if (knigaName.contains("Паводле Яна")) knigaN = 3
                if (knigaName.contains("Дзеі Апосталаў")) knigaN = 4
                if (knigaName.contains("Якава")) knigaN = 5
                if (knigaName.contains("1-е Пятра")) knigaN = 6
                if (knigaName.contains("2-е Пятра")) knigaN = 7
                if (knigaName.contains("1-е Яна Багаслова")) knigaN = 8
                if (knigaName.contains("2-е Яна Багаслова")) knigaN = 9
                if (knigaName.contains("3-е Яна Багаслова")) knigaN = 10
                if (knigaName.contains("Юды")) knigaN = 11
                if (knigaName.contains("Да Рымлянаў")) knigaN = 12
                if (knigaName.contains("1-е да Карынфянаў")) knigaN = 13
                if (knigaName.contains("2-е да Карынфянаў")) knigaN = 14
                if (knigaName.contains("Да Галятаў")) knigaN = 15
                if (knigaName.contains("Да Эфэсянаў")) knigaN = 16
                if (knigaName.contains("Да Піліпянаў")) knigaN = 17
                if (knigaName.contains("Да Каласянаў")) knigaN = 18
                if (knigaName.contains("1-е да Фесаланікійцаў")) knigaN = 19
                if (knigaName.contains("2-е да Фесаланікійцаў")) knigaN = 20
                if (knigaName.contains("1-е да Цімафея")) knigaN = 21
                if (knigaName.contains("2-е да Цімафея")) knigaN = 22
                if (knigaName.contains("Да Ціта")) knigaN = 23
                if (knigaName.contains("Да Філімона")) knigaN = 24
                if (knigaName.contains("Да Габрэяў")) knigaN = 25
                if (knigaName.contains("Адкрыцьцё (Апакаліпсіс)")) knigaN = 26
                if (knigaName.contains("Быцьцё")) knigaS = 0
                if (knigaName.contains("Выхад")) knigaS = 1
                if (knigaName.contains("Лявіт")) knigaS = 2
                if (knigaName.contains("Лікі")) knigaS = 3
                if (knigaName.contains("Другі Закон")) knigaS = 4
                if (knigaName.contains("Ісуса сына Нава")) knigaS = 5
                if (knigaName.contains("Судзьдзяў")) knigaS = 6
                if (knigaName.contains("Рут")) knigaS = 7
                if (knigaName.contains("1-я Царстваў")) knigaS = 8
                if (knigaName.contains("2-я Царстваў")) knigaS = 9
                if (knigaName.contains("3-я Царстваў")) knigaS = 10
                if (knigaName.contains("4-я Царстваў")) knigaS = 11
                if (knigaName.contains("1-я Летапісаў")) knigaS = 12
                if (knigaName.contains("2-я Летапісаў")) knigaS = 13
                if (knigaName.contains("Эздры")) knigaS = 14
                if (knigaName.contains("Нээміі")) knigaS = 15
                if (knigaName.contains("Эстэр")) knigaS = 19
                if (knigaName.contains("Ёва")) knigaS = 20
                if (knigaName.contains("Псалтыр")) knigaS = 21
                if (knigaName.contains("Выслоўяў Саламонавых")) knigaS = 22
                if (knigaName.contains("Эклезіяста")) knigaS = 23
                if (knigaName.contains("Найвышэйшая Песьня Саламонава")) knigaS = 24
                if (knigaName.contains("Ісаі")) knigaS = 27
                if (knigaName.contains("Ераміі")) knigaS = 28
                if (knigaName.contains("Ераміін Плач")) knigaS = 29
                if (knigaName.contains("Езэкііля")) knigaS = 32
                if (knigaName.contains("Данііла")) knigaS = 33
                if (knigaName.contains("Асіі")) knigaS = 34
                if (knigaName.contains("Ёіля")) knigaS = 35
                if (knigaName.contains("Амоса")) knigaS = 36
                if (knigaName.contains("Аўдзея")) knigaS = 37
                if (knigaName.contains("Ёны")) knigaS = 38
                if (knigaName.contains("Міхея")) knigaS = 39
                if (knigaName.contains("Навума")) knigaS = 40
                if (knigaName.contains("Абакума")) knigaS = 41
                if (knigaName.contains("Сафона")) knigaS = 42
                if (knigaName.contains("Агея")) knigaS = 43
                if (knigaName.contains("Захарыі")) knigaS = 44
                if (knigaName.contains("Малахіі")) knigaS = 45
                t1 = knigaName.indexOf("Разьдзел ")
                t2 = knigaName.indexOf("/", t1)
                t3 = knigaName.indexOf("\n\n")
                glava1 = knigaName.substring(t1 + 9, t2).toInt() - 1
                val stix1 = knigaName.substring(t2 + 6, t3).toInt() - 1
                var zavet = "1"
                if (knigaS != -1) {
                    zavet = "0"
                    knigaN = knigaS
                }

                if (zavet.contains(zav) && knigaN == kniga && glava1 == glava && stix1 == position) {
                    ssb = SpannableStringBuilder(".")
                    var d: Drawable? = null
                    val t5 = knigaName.lastIndexOf("<!--")
                    val color = if (t5 != -1)
                        knigaName.substring(t5 + 4).toInt()
                    else
                        0
                    when(color) {
                        0 -> {
                            d = if (k.getBoolean("dzen_noch", false)) ContextCompat.getDrawable(context, R.drawable.bookmark)
                            else ContextCompat.getDrawable(context, R.drawable.bookmark_black)
                        }
                        1 -> {
                            d = if (k.getBoolean("dzen_noch", false)) ContextCompat.getDrawable(context, R.drawable.bookmark1_black)
                            else ContextCompat.getDrawable(context, R.drawable.bookmark1)
                        }
                        2 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark2)
                        3 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark3)
                        4 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark4)
                        5 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark5)
                        6 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark6)
                        7 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark7)
                        8 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark8)
                        9 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark9)
                        10 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark10)
                        11 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark11)
                        12 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark12)
                    }
                    val realpadding = (fontSize * context.resources.displayMetrics.density).toInt()
                    d?.setBounds(0, 0, realpadding, realpadding)
                    d?.let {
                        val span = ImageSpan(it, DynamicDrawableSpan.ALIGN_BASELINE)
                        ssb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    break
                }
            }
        }
        return ssb
    }

    private fun setZakladkiSinoidal(position: Int): SpannableStringBuilder? {
        var ssb: SpannableStringBuilder? = null
        var zav = "0"
        if (Zapavet) zav = "1"
        if (BibleGlobalList.zakladkiSinodal.size > 0) {
            for (i in BibleGlobalList.zakladkiSinodal.indices) {
                var knigaN = -1
                var knigaS = -1
                var t1: Int
                var t2: Int
                var t3: Int
                var glava1: Int
                val knigaName = BibleGlobalList.zakladkiSinodal[i].data
                if (knigaName.contains("От Матфея")) knigaN = 0
                if (knigaName.contains("От Марка")) knigaN = 1
                if (knigaName.contains("От Луки")) knigaN = 2
                if (knigaName.contains("От Иоанна")) knigaN = 3
                if (knigaName.contains("Деяния святых апостолов")) knigaN = 4
                if (knigaName.contains("Иакова")) knigaN = 5
                if (knigaName.contains("1-е Петра")) knigaN = 6
                if (knigaName.contains("2-е Петра")) knigaN = 7
                if (knigaName.contains("1-е Иоанна")) knigaN = 8
                if (knigaName.contains("2-е Иоанна")) knigaN = 9
                if (knigaName.contains("3-е Иоанна")) knigaN = 10
                if (knigaName.contains("Иуды")) knigaN = 11
                if (knigaName.contains("Римлянам")) knigaN = 12
                if (knigaName.contains("1-е Коринфянам")) knigaN = 13
                if (knigaName.contains("2-е Коринфянам")) knigaN = 14
                if (knigaName.contains("Галатам")) knigaN = 15
                if (knigaName.contains("Ефесянам")) knigaN = 16
                if (knigaName.contains("Филиппийцам")) knigaN = 17
                if (knigaName.contains("Колоссянам")) knigaN = 18
                if (knigaName.contains("1-е Фессалоникийцам (Солунянам)")) knigaN = 19
                if (knigaName.contains("2-е Фессалоникийцам (Солунянам)")) knigaN = 20
                if (knigaName.contains("1-е Тимофею")) knigaN = 21
                if (knigaName.contains("2-е Тимофею")) knigaN = 22
                if (knigaName.contains("Титу")) knigaN = 23
                if (knigaName.contains("Филимону")) knigaN = 24
                if (knigaName.contains("Евреям")) knigaN = 25
                if (knigaName.contains("Откровение (Апокалипсис)")) knigaN = 26
                if (knigaName.contains("Бытие")) knigaS = 0
                if (knigaName.contains("Исход")) knigaS = 1
                if (knigaName.contains("Левит")) knigaS = 2
                if (knigaName.contains("Числа")) knigaS = 3
                if (knigaName.contains("Второзаконие")) knigaS = 4
                if (knigaName.contains("Иисуса Навина")) knigaS = 5
                if (knigaName.contains("Судей израилевых")) knigaS = 6
                if (knigaName.contains("Руфи")) knigaS = 7
                if (knigaName.contains("1-я Царств")) knigaS = 8
                if (knigaName.contains("2-я Царств")) knigaS = 9
                if (knigaName.contains("3-я Царств")) knigaS = 10
                if (knigaName.contains("4-я Царств")) knigaS = 11
                if (knigaName.contains("1-я Паралипоменон")) knigaS = 12
                if (knigaName.contains("2-я Паралипоменон")) knigaS = 13
                if (knigaName.contains("1-я Ездры")) knigaS = 14
                if (knigaName.contains("Неемии")) knigaS = 15
                if (knigaName.contains("2-я Ездры")) knigaS = 16
                if (knigaName.contains("Товита")) knigaS = 17
                if (knigaName.contains("Иудифи")) knigaS = 18
                if (knigaName.contains("Есфири")) knigaS = 19
                if (knigaName.contains("Иова")) knigaS = 20
                if (knigaName.contains("Псалтирь")) knigaS = 21
                if (knigaName.contains("Притчи Соломона")) knigaS = 22
                if (knigaName.contains("Екклезиаста")) knigaS = 23
                if (knigaName.contains("Песнь песней Соломона")) knigaS = 24
                if (knigaName.contains("Премудрости Соломона")) knigaS = 25
                if (knigaName.contains("Премудрости Иисуса, сына Сирахова")) knigaS = 26
                if (knigaName.contains("Исаии")) knigaS = 27
                if (knigaName.contains("Иеремии")) knigaS = 28
                if (knigaName.contains("Плач Иеремии")) knigaS = 29
                if (knigaName.contains("Послание Иеремии")) knigaS = 30
                if (knigaName.contains("Варуха")) knigaS = 31
                if (knigaName.contains("Иезекииля")) knigaS = 32
                if (knigaName.contains("Даниила")) knigaS = 33
                if (knigaName.contains("Осии")) knigaS = 34
                if (knigaName.contains("Иоиля")) knigaS = 35
                if (knigaName.contains("Амоса")) knigaS = 36
                if (knigaName.contains("Авдия")) knigaS = 37
                if (knigaName.contains("Ионы")) knigaS = 38
                if (knigaName.contains("Михея")) knigaS = 39
                if (knigaName.contains("Наума")) knigaS = 40
                if (knigaName.contains("Аввакума")) knigaS = 41
                if (knigaName.contains("Сафонии")) knigaS = 42
                if (knigaName.contains("Аггея")) knigaS = 43
                if (knigaName.contains("Захарии")) knigaS = 44
                if (knigaName.contains("Малахии")) knigaS = 45
                if (knigaName.contains("1-я Маккавейская")) knigaS = 46
                if (knigaName.contains("2-я Маккавейская")) knigaS = 47
                if (knigaName.contains("3-я Маккавейская")) knigaS = 48
                if (knigaName.contains("3-я Ездры")) knigaS = 49
                t1 = knigaName.indexOf("Глава ")
                t2 = knigaName.indexOf("/", t1)
                t3 = knigaName.indexOf("\n\n")
                glava1 = knigaName.substring(t1 + 6, t2).toInt() - 1
                val stix1 = knigaName.substring(t2 + 6, t3).toInt() - 1
                var zavet = "1"
                if (knigaS != -1) {
                    zavet = "0"
                    knigaN = knigaS
                }
                if (zavet.contains(zav) && knigaN == kniga && glava1 == glava && stix1 == position) {
                    ssb = SpannableStringBuilder(".")
                    var d: Drawable? = null
                    val t5 = knigaName.lastIndexOf("<!--")
                    val color = if (t5 != -1)
                        knigaName.substring(t5 + 4).toInt()
                    else
                        0
                    when(color) {
                        0 -> {
                            d = if (k.getBoolean("dzen_noch", false)) ContextCompat.getDrawable(context, R.drawable.bookmark)
                            else ContextCompat.getDrawable(context, R.drawable.bookmark_black)
                        }
                        1 -> {
                            d = if (k.getBoolean("dzen_noch", false)) ContextCompat.getDrawable(context, R.drawable.bookmark1_black)
                            else ContextCompat.getDrawable(context, R.drawable.bookmark1)
                        }
                        2 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark2)
                        3 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark3)
                        4 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark4)
                        5 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark5)
                        6 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark6)
                        7 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark7)
                        8 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark8)
                        9 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark9)
                        10 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark10)
                        11 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark11)
                        12 -> d = ContextCompat.getDrawable(context, R.drawable.bookmark12)
                    }
                    val realpadding = (fontSize * context.resources.displayMetrics.density).toInt()
                    d?.setBounds(0, 0, realpadding, realpadding)
                    d?.let {
                        val span = ImageSpan(it, DynamicDrawableSpan.ALIGN_BASELINE)
                        ssb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    break
                }
            }
        }
        return ssb
    }

    private class ExpArrayAdapterParallelItems {
        var textView: TextViewRobotoCondensed? = null
    }

    companion object {
        val colors = arrayOf("#000000", "#D00505", "#800080", "#C71585", "#FF00FF", "#F4A460", "#D2691E", "#A52A2A", "#1E90FF", "#6A5ACD", "#228B22", "#9ACD32", "#20B2AA")
    }
}