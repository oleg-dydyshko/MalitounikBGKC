package by.carkva_gazeta.resources

import android.content.Context
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import java.io.InputStream

interface BibliaPerakvadNadsana {

    fun getNamePerevod() = DialogVybranoeBibleList.PEREVODNADSAN

    fun getTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.psalter)
    }

    fun getSubTitlePerevod(glava: Int): String {
        this as BaseActivity
        var psalter1 = glava
        psalter1++
        var kafizma = 1
        if (psalter1 in 9..16) kafizma = 2
        if (psalter1 in 17..23) kafizma = 3
        if (psalter1 in 24..31) kafizma = 4
        if (psalter1 in 32..36) kafizma = 5
        if (psalter1 in 37..45) kafizma = 6
        if (psalter1 in 46..54) kafizma = 7
        if (psalter1 in 55..63) kafizma = 8
        if (psalter1 in 64..69) kafizma = 9
        if (psalter1 in 70..76) kafizma = 10
        if (psalter1 in 77..84) kafizma = 11
        if (psalter1 in 85..90) kafizma = 12
        if (psalter1 in 91..100) kafizma = 13
        if (psalter1 in 101..104) kafizma = 14
        if (psalter1 in 105..108) kafizma = 15
        if (psalter1 in 109..117) kafizma = 16
        if (psalter1 == 118) kafizma = 17
        if (psalter1 in 119..133) kafizma = 18
        if (psalter1 in 134..142) kafizma = 19
        if (psalter1 in 143..151) kafizma = 20
        return resources.getString(by.carkva_gazeta.malitounik.R.string.kafizma2, kafizma)
    }

    fun getSpisKnig(): Array<String> {
        this as BaseActivity
        return resources.getStringArray(by.carkva_gazeta.malitounik.R.array.psalter_list)
    }

    fun setKafizma(kafizma: Int): Int {
        var glava = 1
        when (kafizma) {
            2 -> glava = 9
            3 -> glava = 17
            4 -> glava = 24
            5 -> glava = 32
            6 -> glava = 37
            7 -> glava = 46
            8 -> glava = 55
            9 -> glava = 64
            10 -> glava = 70
            11 -> glava = 77
            12 -> glava = 85
            13 -> glava = 91
            14 -> glava = 101
            15 -> glava = 105
            16 -> glava = 109
            17 -> glava = 118
            18 -> glava = 119
            19 -> glava = 134
            20 -> glava = 143
        }
        glava--
        return glava
    }

    fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        this as BaseActivity
        return resources.openRawResource(R.raw.psaltyr_nadsan)
    }

    fun saveVydelenieZakladkiNtanki(glava: Int, stix: Int) {
        val context = this as BaseActivity
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditors = k.edit()
        prefEditors.putInt("psalter_time_psalter_nadsan_glava", glava)
        prefEditors.putInt("psalter_time_psalter_nadsan_stix", stix)
        prefEditors.apply()
    }
}