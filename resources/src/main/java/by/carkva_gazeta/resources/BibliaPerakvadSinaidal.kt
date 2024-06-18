package by.carkva_gazeta.resources

import android.content.Context
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.DialogVybranoeBibleList
import by.carkva_gazeta.malitounik.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream

interface BibliaPerakvadSinaidal {

    fun getNamePerevod() = DialogVybranoeBibleList.PEREVODSINOIDAL

    fun getZakladki() = BibleGlobalList.zakladkiSinodal

    fun getTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)
    }

    fun getSubTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.bsinaidal2)
    }

    fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        this as BaseActivity
        return if (novyZapaviet) resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidaln)
        else resources.getStringArray(by.carkva_gazeta.malitounik.R.array.sinoidals)
    }

    fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        this as BaseActivity
        return if (novyZapaviet) File("$filesDir/BibliaSinodalNovyZavet/$kniga.json")
        else File("$filesDir/BibliaSinodalStaryZavet/$kniga.json")
    }

    fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        this as BaseActivity
        var inputStream = resources.openRawResource(R.raw.sinaidaln1)
        if (novyZapaviet) {
            when (kniga) {
                0 -> inputStream = resources.openRawResource(R.raw.sinaidaln1)
                1 -> inputStream = resources.openRawResource(R.raw.sinaidaln2)
                2 -> inputStream = resources.openRawResource(R.raw.sinaidaln3)
                3 -> inputStream = resources.openRawResource(R.raw.sinaidaln4)
                4 -> inputStream = resources.openRawResource(R.raw.sinaidaln5)
                5 -> inputStream = resources.openRawResource(R.raw.sinaidaln6)
                6 -> inputStream = resources.openRawResource(R.raw.sinaidaln7)
                7 -> inputStream = resources.openRawResource(R.raw.sinaidaln8)
                8 -> inputStream = resources.openRawResource(R.raw.sinaidaln9)
                9 -> inputStream = resources.openRawResource(R.raw.sinaidaln10)
                10 -> inputStream = resources.openRawResource(R.raw.sinaidaln11)
                11 -> inputStream = resources.openRawResource(R.raw.sinaidaln12)
                12 -> inputStream = resources.openRawResource(R.raw.sinaidaln13)
                13 -> inputStream = resources.openRawResource(R.raw.sinaidaln14)
                14 -> inputStream = resources.openRawResource(R.raw.sinaidaln15)
                15 -> inputStream = resources.openRawResource(R.raw.sinaidaln16)
                16 -> inputStream = resources.openRawResource(R.raw.sinaidaln17)
                17 -> inputStream = resources.openRawResource(R.raw.sinaidaln18)
                18 -> inputStream = resources.openRawResource(R.raw.sinaidaln19)
                19 -> inputStream = resources.openRawResource(R.raw.sinaidaln20)
                20 -> inputStream = resources.openRawResource(R.raw.sinaidaln21)
                21 -> inputStream = resources.openRawResource(R.raw.sinaidaln22)
                22 -> inputStream = resources.openRawResource(R.raw.sinaidaln23)
                23 -> inputStream = resources.openRawResource(R.raw.sinaidaln24)
                24 -> inputStream = resources.openRawResource(R.raw.sinaidaln25)
                25 -> inputStream = resources.openRawResource(R.raw.sinaidaln26)
                26 -> inputStream = resources.openRawResource(R.raw.sinaidaln27)
            }
        } else {
            when (kniga) {
                0 -> inputStream = resources.openRawResource(R.raw.sinaidals1)
                1 -> inputStream = resources.openRawResource(R.raw.sinaidals2)
                2 -> inputStream = resources.openRawResource(R.raw.sinaidals3)
                3 -> inputStream = resources.openRawResource(R.raw.sinaidals4)
                4 -> inputStream = resources.openRawResource(R.raw.sinaidals5)
                5 -> inputStream = resources.openRawResource(R.raw.sinaidals6)
                6 -> inputStream = resources.openRawResource(R.raw.sinaidals7)
                7 -> inputStream = resources.openRawResource(R.raw.sinaidals8)
                8 -> inputStream = resources.openRawResource(R.raw.sinaidals9)
                9 -> inputStream = resources.openRawResource(R.raw.sinaidals10)
                10 -> inputStream = resources.openRawResource(R.raw.sinaidals11)
                11 -> inputStream = resources.openRawResource(R.raw.sinaidals12)
                12 -> inputStream = resources.openRawResource(R.raw.sinaidals13)
                13 -> inputStream = resources.openRawResource(R.raw.sinaidals14)
                14 -> inputStream = resources.openRawResource(R.raw.sinaidals15)
                15 -> inputStream = resources.openRawResource(R.raw.sinaidals16)
                16 -> inputStream = resources.openRawResource(R.raw.sinaidals17)
                17 -> inputStream = resources.openRawResource(R.raw.sinaidals18)
                18 -> inputStream = resources.openRawResource(R.raw.sinaidals19)
                19 -> inputStream = resources.openRawResource(R.raw.sinaidals20)
                20 -> inputStream = resources.openRawResource(R.raw.sinaidals21)
                21 -> inputStream = resources.openRawResource(R.raw.sinaidals22)
                22 -> inputStream = resources.openRawResource(R.raw.sinaidals23)
                23 -> inputStream = resources.openRawResource(R.raw.sinaidals24)
                24 -> inputStream = resources.openRawResource(R.raw.sinaidals25)
                25 -> inputStream = resources.openRawResource(R.raw.sinaidals26)
                26 -> inputStream = resources.openRawResource(R.raw.sinaidals27)
                27 -> inputStream = resources.openRawResource(R.raw.sinaidals28)
                28 -> inputStream = resources.openRawResource(R.raw.sinaidals29)
                29 -> inputStream = resources.openRawResource(R.raw.sinaidals30)
                30 -> inputStream = resources.openRawResource(R.raw.sinaidals31)
                31 -> inputStream = resources.openRawResource(R.raw.sinaidals32)
                32 -> inputStream = resources.openRawResource(R.raw.sinaidals33)
                33 -> inputStream = resources.openRawResource(R.raw.sinaidals34)
                34 -> inputStream = resources.openRawResource(R.raw.sinaidals35)
                35 -> inputStream = resources.openRawResource(R.raw.sinaidals36)
                36 -> inputStream = resources.openRawResource(R.raw.sinaidals37)
                37 -> inputStream = resources.openRawResource(R.raw.sinaidals38)
                38 -> inputStream = resources.openRawResource(R.raw.sinaidals39)
                39 -> inputStream = resources.openRawResource(R.raw.sinaidals39)
                40 -> inputStream = resources.openRawResource(R.raw.sinaidals40)
                41 -> inputStream = resources.openRawResource(R.raw.sinaidals42)
                42 -> inputStream = resources.openRawResource(R.raw.sinaidals43)
                43 -> inputStream = resources.openRawResource(R.raw.sinaidals44)
                44 -> inputStream = resources.openRawResource(R.raw.sinaidals45)
                45 -> inputStream = resources.openRawResource(R.raw.sinaidals46)
                46 -> inputStream = resources.openRawResource(R.raw.sinaidals47)
                47 -> inputStream = resources.openRawResource(R.raw.sinaidals48)
                48 -> inputStream = resources.openRawResource(R.raw.sinaidals49)
                49 -> inputStream = resources.openRawResource(R.raw.sinaidals50)
            }
        }
        return inputStream
    }

    fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        val context = this as BaseActivity
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditors = k.edit()
        prefEditors.remove("bible_time_sinodal")
        prefEditors.putBoolean("bible_time_sinodal_zavet", novyZapaviet)
        prefEditors.putInt("bible_time_sinodal_kniga", kniga)
        prefEditors.putInt("bible_time_sinodal_glava", glava)
        prefEditors.putInt("bible_time_sinodal_stix", stix)
        prefEditors.apply()
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
        val listFiles = if (novyZapaviet) File("${context.filesDir}/BibliaSinodalNovyZavet").listFiles()
        else File("$filesDir/BibliaSinodalStaryZavet").listFiles()
        listFiles?.forEach {
            val inputStream = FileReader(it)
            val reader = BufferedReader(inputStream)
            val list = gson.fromJson<ArrayList<ArrayList<Int>>>(reader.readText(), type)
            val del = ArrayList<ArrayList<Int>>()
            inputStream.close()
            list.forEach { intArrayList ->
                if (intArrayList[2] == 0 && intArrayList[3] == 0 && intArrayList[4] == 0) {
                    del.add(intArrayList)
                }
            }
            list.removeAll(del.toSet())
            if (list.size == 0) {
                it.delete()
            } else {
                it.writer().use { writer ->
                    writer.write(gson.toJson(list, type))
                }
            }
        }
        val file = if (novyZapaviet) File("${context.filesDir}/BibliaSinodalNovyZavet/$kniga.json")
        else File("$filesDir/BibliaSinodalStaryZavet/$kniga.json")
        if (BibleGlobalList.vydelenie.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            file.writer().use {
                it.write(gson.toJson(BibleGlobalList.vydelenie, type))
            }
        }
        val fileZakladki = File("${context.filesDir}/BibliaSinodalZakladki.json")
        if (BibleGlobalList.zakladkiSinodal.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            fileZakladki.writer().use {
                val type2 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleZakladkiData::class.java).type
                it.write(gson.toJson(BibleGlobalList.zakladkiSinodal, type2))
            }
        }
        val fileNatatki = File("${context.filesDir}/BibliaSinodalNatatki.json")
        if (BibleGlobalList.natatkiSinodal.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            fileNatatki.writer().use {
                val type3 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                it.write(gson.toJson(BibleGlobalList.natatkiSinodal, type3))
            }
        }
    }

    fun addZakladka(color: Int, knigaBible: String, bible: String) {
        if (color != -1) {
            val context = this as BaseActivity
            var maxIndex: Long = 0
            BibleGlobalList.zakladkiSinodal.forEach {
                if (maxIndex < it.id) maxIndex = it.id
            }
            maxIndex++
            BibleGlobalList.zakladkiSinodal.add(0, BibleZakladkiData(maxIndex, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible).toString() + "<!--" + color))
            MainActivity.toastView(context, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
        }
    }
}