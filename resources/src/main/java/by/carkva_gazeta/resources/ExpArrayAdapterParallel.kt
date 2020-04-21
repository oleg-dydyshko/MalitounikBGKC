package by.carkva_gazeta.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import by.carkva_gazeta.malitounik.*
import by.carkva_gazeta.malitounik.R

/**
 * Created by oleg on 7.12.16
 */
internal class ExpArrayAdapterParallel(private val context: Activity, private val stixi: ArrayList<String>, private val kniga: Int, private val glava: Int, private val Zapavet: Boolean, private val mPerevod: Int) : ArrayAdapter<String>(context, R.layout.simple_list_item_bible, stixi as List<String>) { // 1-Сёмуха, 2-Синоидальный, 3-Псалтырь Надсана

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val rootView: View
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
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
        ea.textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, k.getFloat("font_biblia", SettingsActivity.GET_DEFAULT_FONT_SIZE))
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
            if (mPerevod == 1) res = MainActivity.translateToBelarus(res)
            val ssb = SpannableStringBuilder(ea.textView?.text).append("\n").append(res) //.append("\n");
            val start = ea.textView?.text?.length ?: 0
            val end = start + 1 + res.length
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
            val ssb = SpannableStringBuilder(ea.textView?.text) // + "\n");
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
                ea.textView?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            } else {
                ea.textView?.setBackgroundResource(R.color.colorDivider)
            }
        } else {
            if (k.getBoolean("dzen_noch", false)) {
                ea.textView?.setBackgroundResource(R.drawable.selector_dark)
                ea.textView?.setTextColor(ContextCompat.getColor(context, R.color.colorIcons))
            } else {
                ea.textView?.setBackgroundResource(R.drawable.selector_default)
            }
        }
        if (mPerevod == 1) {
            var zav = "0"
            if (Zapavet) zav = "1"
            if (BibleGlobalList.natatkiSemuxa.size > 0) {
                for (i in BibleGlobalList.natatkiSemuxa.indices) {
                    if (BibleGlobalList.natatkiSemuxa[i][0].contains(zav) && BibleGlobalList.natatkiSemuxa[i][1].toInt() == kniga && BibleGlobalList.natatkiSemuxa[i][2].toInt() == glava && BibleGlobalList.natatkiSemuxa[i][3].toInt() == position) {
                        val ssb = SpannableStringBuilder(ea.textView?.text)
                        val nachalo = ssb.length
                        ssb.append("\nНататка:\n").append(BibleGlobalList.natatkiSemuxa[i][5]).append("\n")
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
                    if (BibleGlobalList.natatkiSinodal[i][0].contains(zav) && BibleGlobalList.natatkiSinodal[i][1].toInt() == kniga && BibleGlobalList.natatkiSinodal[i][2].toInt() == glava && BibleGlobalList.natatkiSinodal[i][3].toInt() == position) {
                        val ssb = SpannableStringBuilder(ea.textView?.text)
                        val nachalo = ssb.length
                        ssb.append("\nНататка:\n").append(BibleGlobalList.natatkiSinodal[i][5]).append("\n")
                        ssb.setSpan(StyleSpan(Typeface.ITALIC), nachalo, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        ea.textView?.text = ssb
                        break
                    }
                }
            }
        }
        return rootView
    }

    private class ExpArrayAdapterParallelItems {
        var textView: TextViewRobotoCondensed? = null
    }

}