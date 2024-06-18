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

interface BibliaPerakvadBokuna {

    fun getNamePerevod() = DialogVybranoeBibleList.PEREVODBOKUNA

    fun getZakladki() = BibleGlobalList.zakladkiBokuna

    fun getTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.title_biblia_bokun)
    }

    fun getSubTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.title_biblia_bokun2)
    }

    fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        this as BaseActivity
        return if (novyZapaviet) resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunan)
        else resources.getStringArray(by.carkva_gazeta.malitounik.R.array.bokunas)
    }

    fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        this as BaseActivity
        return if (novyZapaviet) File("$filesDir/BibliaBokunaNovyZavet/$kniga.json")
        else File("$filesDir/BibliaBokunaStaryZavet/$kniga.json")
    }

    fun getKnigaReal(kniga: Int): Int {
        var knigaReal = kniga
        when (kniga) {
            16 -> knigaReal = 19
            17 -> knigaReal = 20
            18 -> knigaReal = 21
            19 -> knigaReal = 22
            20 -> knigaReal = 23
            21 -> knigaReal = 24
            22 -> knigaReal = 27
            23 -> knigaReal = 28
            24 -> knigaReal = 29
            25 -> knigaReal = 32
            26 -> knigaReal = 33
            27 -> knigaReal = 34
            28 -> knigaReal = 35
            29 -> knigaReal = 36
            30 -> knigaReal = 37
            31 -> knigaReal = 38
            32 -> knigaReal = 39
            33 -> knigaReal = 40
            34 -> knigaReal = 41
            35 -> knigaReal = 42
            36 -> knigaReal = 43
            37 -> knigaReal = 44
            38 -> knigaReal = 45
        }
        return knigaReal
    }

    fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        this as BaseActivity
        var inputStream = resources.openRawResource(R.raw.bokunan1)
        if (novyZapaviet) {
            when (kniga) {
                0 -> inputStream = resources.openRawResource(R.raw.bokunan1)
                1 -> inputStream = resources.openRawResource(R.raw.bokunan2)
                2 -> inputStream = resources.openRawResource(R.raw.bokunan3)
                3 -> inputStream = resources.openRawResource(R.raw.bokunan4)
                4 -> inputStream = resources.openRawResource(R.raw.bokunan5)
                5 -> inputStream = resources.openRawResource(R.raw.bokunan6)
                6 -> inputStream = resources.openRawResource(R.raw.bokunan7)
                7 -> inputStream = resources.openRawResource(R.raw.bokunan8)
                8 -> inputStream = resources.openRawResource(R.raw.bokunan9)
                9 -> inputStream = resources.openRawResource(R.raw.bokunan10)
                10 -> inputStream = resources.openRawResource(R.raw.bokunan11)
                11 -> inputStream = resources.openRawResource(R.raw.bokunan12)
                12 -> inputStream = resources.openRawResource(R.raw.bokunan13)
                13 -> inputStream = resources.openRawResource(R.raw.bokunan14)
                14 -> inputStream = resources.openRawResource(R.raw.bokunan15)
                15 -> inputStream = resources.openRawResource(R.raw.bokunan16)
                16 -> inputStream = resources.openRawResource(R.raw.bokunan17)
                17 -> inputStream = resources.openRawResource(R.raw.bokunan18)
                18 -> inputStream = resources.openRawResource(R.raw.bokunan19)
                19 -> inputStream = resources.openRawResource(R.raw.bokunan20)
                20 -> inputStream = resources.openRawResource(R.raw.bokunan21)
                21 -> inputStream = resources.openRawResource(R.raw.bokunan22)
                22 -> inputStream = resources.openRawResource(R.raw.bokunan23)
                23 -> inputStream = resources.openRawResource(R.raw.bokunan24)
                24 -> inputStream = resources.openRawResource(R.raw.bokunan25)
                25 -> inputStream = resources.openRawResource(R.raw.bokunan26)
                26 -> inputStream = resources.openRawResource(R.raw.bokunan27)
            }
        } else {
            when (kniga) {
                0 -> inputStream = resources.openRawResource(R.raw.bokunas1)
                1 -> inputStream = resources.openRawResource(R.raw.bokunas2)
                2 -> inputStream = resources.openRawResource(R.raw.bokunas3)
                3 -> inputStream = resources.openRawResource(R.raw.bokunas4)
                4 -> inputStream = resources.openRawResource(R.raw.bokunas5)
                5 -> inputStream = resources.openRawResource(R.raw.bokunas6)
                6 -> inputStream = resources.openRawResource(R.raw.bokunas7)
                7 -> inputStream = resources.openRawResource(R.raw.bokunas8)
                8 -> inputStream = resources.openRawResource(R.raw.bokunas9)
                9 -> inputStream = resources.openRawResource(R.raw.bokunas10)
                10 -> inputStream = resources.openRawResource(R.raw.bokunas11)
                11 -> inputStream = resources.openRawResource(R.raw.bokunas12)
                12 -> inputStream = resources.openRawResource(R.raw.bokunas13)
                13 -> inputStream = resources.openRawResource(R.raw.bokunas14)
                14 -> inputStream = resources.openRawResource(R.raw.bokunas15)
                15 -> inputStream = resources.openRawResource(R.raw.bokunas16)
                16 -> inputStream = resources.openRawResource(R.raw.bokunas17)
                17 -> inputStream = resources.openRawResource(R.raw.bokunas18)
                18 -> inputStream = resources.openRawResource(R.raw.bokunas19)
                19 -> inputStream = resources.openRawResource(R.raw.bokunas20)
                20 -> inputStream = resources.openRawResource(R.raw.bokunas21)
                21 -> inputStream = resources.openRawResource(R.raw.bokunas22)
                22 -> inputStream = resources.openRawResource(R.raw.bokunas23)
                23 -> inputStream = resources.openRawResource(R.raw.bokunas24)
                24 -> inputStream = resources.openRawResource(R.raw.bokunas25)
                25 -> inputStream = resources.openRawResource(R.raw.bokunas26)
                26 -> inputStream = resources.openRawResource(R.raw.bokunas27)
                27 -> inputStream = resources.openRawResource(R.raw.bokunas28)
                28 -> inputStream = resources.openRawResource(R.raw.bokunas29)
                29 -> inputStream = resources.openRawResource(R.raw.bokunas30)
                30 -> inputStream = resources.openRawResource(R.raw.bokunas31)
                31 -> inputStream = resources.openRawResource(R.raw.bokunas32)
                32 -> inputStream = resources.openRawResource(R.raw.bokunas33)
                33 -> inputStream = resources.openRawResource(R.raw.bokunas34)
                34 -> inputStream = resources.openRawResource(R.raw.bokunas35)
                35 -> inputStream = resources.openRawResource(R.raw.bokunas36)
                36 -> inputStream = resources.openRawResource(R.raw.bokunas37)
                37 -> inputStream = resources.openRawResource(R.raw.bokunas38)
                38 -> inputStream = resources.openRawResource(R.raw.bokunas39)
            }
        }
        return inputStream
    }

    fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        val context = this as BaseActivity
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditors = k.edit()
        prefEditors.remove("bible_time_bokuna")
        prefEditors.putBoolean("bible_time_bokuna_zavet", novyZapaviet)
        prefEditors.putInt("bible_time_bokuna_kniga", kniga)
        prefEditors.putInt("bible_time_bokuna_glava", glava)
        prefEditors.putInt("bible_time_bokuna_stix", stix)
        prefEditors.apply()
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
        val listFiles = if (novyZapaviet) File("${context.filesDir}/BibliaBokunaNovyZavet").listFiles()
        else File("${context.filesDir}/BibliaBokunaStaryZavet").listFiles()
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
        val file = if (novyZapaviet) File("${context.filesDir}/BibliaBokunaNovyZavet/$kniga.json")
        else File("${context.filesDir}/BibliaBokunaStaryZavet/$kniga.json")
        if (BibleGlobalList.vydelenie.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            file.writer().use {
                it.write(gson.toJson(BibleGlobalList.vydelenie, type))
            }
        }
        val fileZakladki = File("${context.filesDir}/BibliaBokunaZakladki.json")
        if (BibleGlobalList.zakladkiBokuna.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            fileZakladki.writer().use {
                val type2 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleZakladkiData::class.java).type
                it.write(gson.toJson(BibleGlobalList.zakladkiBokuna, type2))
            }
        }
        val fileNatatki = File("${context.filesDir}/BibliaBokunaNatatki.json")
        if (BibleGlobalList.natatkiBokuna.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            fileNatatki.writer().use {
                val type3 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                it.write(gson.toJson(BibleGlobalList.natatkiBokuna, type3))
            }
        }
    }

    fun addZakladka(color: Int, knigaBible: String, bible: String) {
        if (color != -1) {
            val context = this as BaseActivity
            var maxIndex: Long = 0
            BibleGlobalList.zakladkiBokuna.forEach {
                if (maxIndex < it.id) maxIndex = it.id
            }
            maxIndex++
            BibleGlobalList.zakladkiBokuna.add(0, BibleZakladkiData(maxIndex, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible).toString() + "<!--" + color))
            MainActivity.toastView(context, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
        }
    }
}