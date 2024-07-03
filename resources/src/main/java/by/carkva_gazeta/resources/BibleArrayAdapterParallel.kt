package by.carkva_gazeta.resources

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.R
import by.carkva_gazeta.malitounik.SettingsActivity
import by.carkva_gazeta.malitounik.databinding.SimpleListItemBibleBinding

internal class BibleArrayAdapterParallel(private val context: Activity, private val stixi: ArrayList<String>, private val kniga: Int, private val glava: Int, private val zapavet: Boolean, private var mPerevod: String) : ArrayAdapter<String>(context, R.layout.simple_list_item_bible, stixi) {
    // 1-Сёмуха, 2-Синоидальный, 3-Псалтырь Надсана, 4-Бокуна, 5-Чарняўскага
    private val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
    private val dzenNoch get() = (context as BaseActivity).getBaseDzenNoch()

    fun setPerevod(perevod: String) {
        mPerevod = perevod
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val rootView: View
        val ea: BibleArrayAdapterParallelItems
        if (convertView == null) {
            val binding = SimpleListItemBibleBinding.inflate(context.layoutInflater, viewGroup, false)
            rootView = binding.root
            ea = BibleArrayAdapterParallelItems(binding.label)
            rootView.tag = ea
        } else {
            rootView = convertView
            ea = rootView.tag as BibleArrayAdapterParallelItems
        }
        val fontSize = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
        ea.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        val parallel = BibliaParallelChtenia()
        var res = "+-+"
        if (mPerevod != DialogVybranoeBibleList.PEREVODNADSAN) {
            if (zapavet) {
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
                    res = if ((context as BibliaActivity).isPsaltyrGreek()) parallel.kniga22(glava + 1, position + 1)
                    else parallel.kniga22Masoretskaya(glava + 1, position + 1)
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
        }
        var stix = stixi[position]
        stix = stix.replace("\\n", "\n")
        ea.textView.text = MainActivity.fromHtml(stix)
        if (mPerevod == DialogVybranoeBibleList.PEREVODSEMUXI || mPerevod == DialogVybranoeBibleList.PEREVODNADSAN) {
            ea.textView.text = MainActivity.fromHtml(stix)
        } else {
            ea.textView.text = stix
        }
        val zakladka = SpannableStringBuilder()
        if (mPerevod == DialogVybranoeBibleList.PEREVODSEMUXI || mPerevod == DialogVybranoeBibleList.PEREVODBOKUNA || mPerevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
            res = MainActivity.translateToBelarus(res)
        }
        zakladka.append(setZakladki(position, mPerevod))
        val ssb = findIntStyx(SpannableStringBuilder(ea.textView.text).append(zakladka))
        if (!res.contains("+-+")) {
            val start = ssb.length
            ssb.append("\n").append(res)
            val end = ssb.length
            ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary_text)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(RelativeSizeSpan(0.7f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val pos = BibleGlobalList.checkPosition(glava, position)
            if (pos != -1) {
                if (BibleGlobalList.vydelenie[pos][2] == 1) {
                    ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorBezPosta)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_text)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (BibleGlobalList.vydelenie[pos][4] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            ea.textView.text = ssb
        } else {
            val end = ssb.length
            val pos = BibleGlobalList.checkPosition(glava, position)
            if (pos != -1) {
                if (BibleGlobalList.vydelenie[pos][2] == 1) {
                    ssb.setSpan(BackgroundColorSpan(ContextCompat.getColor(context, R.color.colorBezPosta)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_text)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (BibleGlobalList.vydelenie[pos][3] == 1) ssb.setSpan(UnderlineSpan(), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (BibleGlobalList.vydelenie[pos][4] == 1) ssb.setSpan(StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            ea.textView.text = ssb
        }
        if (BibleGlobalList.bibleCopyList.size > 0 && BibleGlobalList.bibleCopyList.contains(position) && BibleGlobalList.mPedakVisable) {
            if (dzenNoch) {
                ea.textView.setBackgroundResource(R.color.colorprimary_material_dark2)
                ea.textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            } else {
                ea.textView.setBackgroundResource(R.color.colorDivider)
            }
        } else {
            if (dzenNoch) {
                ea.textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                if (res == "+-+") ea.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorbackground_material_dark))
                else ea.textView.setBackgroundResource(R.drawable.selector_dark)
            } else {
                if (res == "+-+") ea.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                else ea.textView.setBackgroundResource(R.drawable.selector_default)
            }
        }
        if (mPerevod == DialogVybranoeBibleList.PEREVODSEMUXI) {
            var zav = "0"
            if (zapavet) zav = "1"
            if (BibleGlobalList.natatkiSemuxa.size > 0) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i].list[0].contains(zav) && BibleGlobalList.natatkiSemuxa[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i].list[2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i].list[3].toInt() == position) {
                        val ssb1 = SpannableStringBuilder(ea.textView.text)
                        val nachalo = ssb1.length
                        ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiSemuxa[i].list[5]).append("\n")
                        ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView.text = ssb1
                        break
                    }
                }
            }
        }
        if (mPerevod == DialogVybranoeBibleList.PEREVODSINOIDAL) {
            var zav = "0"
            if (zapavet) zav = "1"
            if (BibleGlobalList.natatkiSinodal.size > 0) {
                for (i in BibleGlobalList.natatkiSinodal.indices) {
                    if (BibleGlobalList.natatkiSinodal[i].list[0].contains(zav) && BibleGlobalList.natatkiSinodal[i].list[1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i].list[2].toInt() == glava && BibleGlobalList.natatkiSinodal[i].list[3].toInt() == position) {
                        val ssb1 = SpannableStringBuilder(ea.textView.text)
                        val nachalo = ssb1.length
                        ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiSinodal[i].list[5]).append("\n")
                        ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView.text = ssb1
                        break
                    }
                }
            }
        }
        if (mPerevod == DialogVybranoeBibleList.PEREVODBOKUNA) {
            var zav = "0"
            if (zapavet) zav = "1"
            if (BibleGlobalList.natatkiBokuna.size > 0) {
                for (i in BibleGlobalList.natatkiBokuna.indices) {
                    if (BibleGlobalList.natatkiBokuna[i].list[0].contains(zav) && BibleGlobalList.natatkiBokuna[i].list[1].toInt() == kniga && BibleGlobalList.natatkiBokuna[i].list[2].toInt() == glava && BibleGlobalList.natatkiBokuna[i].list[3].toInt() == position) {
                        val ssb1 = SpannableStringBuilder(ea.textView.text)
                        val nachalo = ssb1.length
                        ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiBokuna[i].list[5]).append("\n")
                        ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView.text = ssb1
                        break
                    }
                }
            }
        }
        if (mPerevod == DialogVybranoeBibleList.PEREVODCARNIAUSKI) {
            var zav = "0"
            if (zapavet) zav = "1"
            if (BibleGlobalList.natatkiCarniauski.size > 0) {
                for (i in BibleGlobalList.natatkiCarniauski.indices) {
                    if (BibleGlobalList.natatkiCarniauski[i].list[0].contains(zav) && BibleGlobalList.natatkiCarniauski[i].list[1].toInt() == kniga && BibleGlobalList.natatkiCarniauski[i].list[2].toInt() == glava && BibleGlobalList.natatkiCarniauski[i].list[3].toInt() == position) {
                        val ssb1 = SpannableStringBuilder(ea.textView.text)
                        val nachalo = ssb1.length
                        ssb1.append("\nНататка:\n").append(BibleGlobalList.natatkiCarniauski[i].list[5]).append("\n")
                        ssb1.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView.text = ssb1
                        break
                    }
                }
            }
        }
        return rootView
    }

    private fun findIntStyx(ssb: SpannableStringBuilder, index: Int = 0): SpannableStringBuilder {
        val t1 = ssb.indexOf(" ", index)
        if (t1 != -1) {
            val subText = ssb.substring(0, t1)
            if (subText.isDigitsOnly()) {
                ssb.insert(t1, ".")
                if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_black)), 0, t1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                else ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, t1 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val t2 = ssb.indexOf("\n", index)
            if (t2 != -1) {
                val t3 = ssb.indexOf(" ", t2)
                if (t3 != -1) {
                    val subText2 = ssb.substring(t2 + 1, t3)
                    if (subText2.isDigitsOnly()) {
                        ssb.insert(t3, ".")
                        if (dzenNoch) ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary_black)), t2 + 1, t3 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        else ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), t2 + 1, t3 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    findIntStyx(ssb, t2 + 1)
                }
            }
        }
        return ssb
    }

    private fun setZakladki(position: Int, perevod: String): SpannableStringBuilder {
        val ssb = SpannableStringBuilder()
        var zav = "0"
        if (zapavet) zav = "1"
        val listn: Array<String>
        val lists: Array<String>
        val listAll: ArrayList<BibleZakladkiData>
        when (perevod) {
            DialogVybranoeBibleList.PEREVODSEMUXI -> {
                if (BibleGlobalList.zakladkiSemuxa.size == 0) return ssb
                listn = context.resources.getStringArray(R.array.semuxan)
                lists = context.resources.getStringArray(R.array.semuxas)
                listAll = BibleGlobalList.zakladkiSemuxa
            }

            DialogVybranoeBibleList.PEREVODSINOIDAL -> {
                if (BibleGlobalList.zakladkiSinodal.size == 0) return ssb
                listn = context.resources.getStringArray(R.array.sinoidaln)
                lists = context.resources.getStringArray(R.array.sinoidals)
                listAll = BibleGlobalList.zakladkiSinodal
            }

            DialogVybranoeBibleList.PEREVODNADSAN -> {
                return ssb
            }

            DialogVybranoeBibleList.PEREVODBOKUNA -> {
                if (BibleGlobalList.zakladkiBokuna.size == 0) return ssb
                listn = context.resources.getStringArray(R.array.bokunan)
                lists = context.resources.getStringArray(R.array.bokunas)
                listAll = BibleGlobalList.zakladkiBokuna
            }

            DialogVybranoeBibleList.PEREVODCARNIAUSKI -> {
                if (BibleGlobalList.zakladkiCarniauski.size == 0) return ssb
                listn = context.resources.getStringArray(R.array.charniauskin)
                lists = context.resources.getStringArray(R.array.charniauskis)
                listAll = BibleGlobalList.zakladkiCarniauski
            }

            else -> {
                if (BibleGlobalList.zakladkiSemuxa.size == 0) return ssb
                listn = context.resources.getStringArray(R.array.semuxan)
                lists = context.resources.getStringArray(R.array.semuxas)
                listAll = BibleGlobalList.zakladkiSemuxa
            }
        }
        for (i in listAll.indices) {
            var knigaN = -1
            var knigaS = -1
            var t1: Int
            var t2: Int
            var t3: Int
            var glava1: Int
            val knigaName = listAll[i].data
            for (e in lists.indices) {
                val t4 = lists[e].indexOf("#")
                if (knigaName.contains(lists[e].substring(0, t4))) knigaS = e
            }
            for (e in listn.indices) {
                val t4 = listn[e].indexOf("#")
                if (knigaName.contains(listn[e].substring(0, t4))) knigaN = e
            }
            t1 = knigaName.indexOf(" ")
            t2 = knigaName.indexOf("/", t1)
            t3 = knigaName.indexOf("\n\n")
            val t4 = knigaName.indexOf(" ", t1 + 1)
            glava1 = knigaName.substring(t1 + 1, t2).toInt() - 1
            val stix1 = knigaName.substring(t4 + 1, t3).toInt() - 1
            var zavet = "1"
            if (knigaS != -1) {
                zavet = "0"
                val title = lists[knigaS]
                val t5 = lists[knigaS].indexOf("#")
                val t6 = lists[knigaS].indexOf("#", t5 + 1)
                knigaN = title.substring(t6 + 1).toInt()
            }
            if (knigaN != -1) {
                val title = listn[knigaN]
                val t5 = listn[knigaN].indexOf("#")
                val t6 = listn[knigaN].indexOf("#", t5 + 1)
                knigaN = title.substring(t6 + 1).toInt()
            }
            if (zavet.contains(zav) && knigaN == kniga && glava1 == glava && stix1 == position) {
                ssb.append(".")
                val t5 = knigaName.lastIndexOf("<!--")
                val color = if (t5 != -1) knigaName.substring(t5 + 4).toInt()
                else 0
                val d = when (color) {
                    0 -> {
                        if (dzenNoch) ContextCompat.getDrawable(context, R.drawable.bookmark)
                        else ContextCompat.getDrawable(context, R.drawable.bookmark_black)
                    }
                    1 -> {
                        if (dzenNoch) ContextCompat.getDrawable(context, R.drawable.bookmark1_black)
                        else ContextCompat.getDrawable(context, R.drawable.bookmark1)
                    }
                    2 -> ContextCompat.getDrawable(context, R.drawable.bookmark2)
                    3 -> ContextCompat.getDrawable(context, R.drawable.bookmark3)
                    4 -> ContextCompat.getDrawable(context, R.drawable.bookmark4)
                    5 -> ContextCompat.getDrawable(context, R.drawable.bookmark5)
                    6 -> ContextCompat.getDrawable(context, R.drawable.bookmark6)
                    7 -> ContextCompat.getDrawable(context, R.drawable.bookmark7)
                    8 -> ContextCompat.getDrawable(context, R.drawable.bookmark8)
                    9 -> ContextCompat.getDrawable(context, R.drawable.bookmark9)
                    10 -> ContextCompat.getDrawable(context, R.drawable.bookmark10)
                    11 -> ContextCompat.getDrawable(context, R.drawable.bookmark11)
                    12 -> ContextCompat.getDrawable(context, R.drawable.bookmark12)
                    else -> null
                }
                val fontSize = k.getFloat("font_biblia", SettingsActivity.GET_FONT_SIZE_DEFAULT)
                val realpadding = (fontSize * context.resources.displayMetrics.density).toInt()
                d?.setBounds(0, 0, realpadding, realpadding)
                d?.let {
                    val span = ImageSpan(it, DynamicDrawableSpan.ALIGN_BASELINE)
                    ssb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                break
            }
        }
        return ssb
    }

    private class BibleArrayAdapterParallelItems(var textView: TextView)

    companion object {
        val colors = arrayOf("#000000", "#D00505", "#800080", "#C71585", "#FF00FF", "#F4A460", "#D2691E", "#A52A2A", "#1E90FF", "#6A5ACD", "#228B22", "#9ACD32", "#20B2AA")
    }
}