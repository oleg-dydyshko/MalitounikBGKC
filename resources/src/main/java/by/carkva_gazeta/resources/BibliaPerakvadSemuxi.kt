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

interface BibliaPerakvadSemuxi {

    fun isPsaltyrGreek() = true

    fun getNamePerevod() = DialogVybranoeBibleList.PEREVODSEMUXI

    fun getZakladki() = BibleGlobalList.zakladkiSemuxa

    fun getTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.title_biblia)
    }

    fun getSubTitlePerevod(glava: Int): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.title_biblia2)
    }

    fun getSpisKnig(novyZapaviet: Boolean): Array<String> {
        this as BaseActivity
        return if (novyZapaviet) resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxan)
        else resources.getStringArray(by.carkva_gazeta.malitounik.R.array.semuxas)
    }

    fun getFileZavet(novyZapaviet: Boolean, kniga: Int): File {
        this as BaseActivity
        return if (novyZapaviet) File("$filesDir/BibliaSemuxaNovyZavet/$kniga.json")
        else File("$filesDir/BibliaSemuxaStaryZavet/$kniga.json")
    }

    fun translatePsaltyr(psalm: Int, styx: Int, isUpdate: Boolean): Array<Int> {
        var resultPsalm = psalm
        var resultStyx = styx
        if (isUpdate) {
            if (psalm == 10) resultPsalm = 9
            if (psalm in 11..113) resultPsalm -= 1
            if (psalm == 114 || psalm == 115) resultPsalm = 113
            if (psalm == 116) resultPsalm = 114
            if (psalm in 117..146) resultPsalm -= 1
            if (psalm == 147) resultPsalm = 146
            if (psalm == 10) {
                resultStyx += 21
                resultPsalm = 9
            }
            if (psalm == 114) {
                resultStyx = styx
                resultPsalm = 113
            }
            if (psalm == 115) {
                resultStyx += 10
                resultPsalm = 113
            }
            if (psalm == 116 && styx < 10) {
                resultStyx = styx
                resultPsalm = 114
            }
            if (psalm == 116 && styx >= 10) {
                resultStyx -= 9
                resultPsalm = 115
            }
            if (psalm == 147 && styx < 12) {
                resultStyx = styx
                resultPsalm = 146
            }
            if (psalm == 147 && styx >= 12) {
                resultStyx -= 11
                resultPsalm = 147
            }
        }
        return arrayOf(resultPsalm, resultStyx)
    }

    fun getInputStream(novyZapaviet: Boolean, kniga: Int): InputStream {
        this as BaseActivity
        var inputStream = resources.openRawResource(R.raw.biblian1)
        if (novyZapaviet) {
            when (kniga) {
                0 -> inputStream = resources.openRawResource(R.raw.biblian1)
                1 -> inputStream = resources.openRawResource(R.raw.biblian2)
                2 -> inputStream = resources.openRawResource(R.raw.biblian3)
                3 -> inputStream = resources.openRawResource(R.raw.biblian4)
                4 -> inputStream = resources.openRawResource(R.raw.biblian5)
                5 -> inputStream = resources.openRawResource(R.raw.biblian6)
                6 -> inputStream = resources.openRawResource(R.raw.biblian7)
                7 -> inputStream = resources.openRawResource(R.raw.biblian8)
                8 -> inputStream = resources.openRawResource(R.raw.biblian9)
                9 -> inputStream = resources.openRawResource(R.raw.biblian10)
                10 -> inputStream = resources.openRawResource(R.raw.biblian11)
                11 -> inputStream = resources.openRawResource(R.raw.biblian12)
                12 -> inputStream = resources.openRawResource(R.raw.biblian13)
                13 -> inputStream = resources.openRawResource(R.raw.biblian14)
                14 -> inputStream = resources.openRawResource(R.raw.biblian15)
                15 -> inputStream = resources.openRawResource(R.raw.biblian16)
                16 -> inputStream = resources.openRawResource(R.raw.biblian17)
                17 -> inputStream = resources.openRawResource(R.raw.biblian18)
                18 -> inputStream = resources.openRawResource(R.raw.biblian19)
                19 -> inputStream = resources.openRawResource(R.raw.biblian20)
                20 -> inputStream = resources.openRawResource(R.raw.biblian21)
                21 -> inputStream = resources.openRawResource(R.raw.biblian22)
                22 -> inputStream = resources.openRawResource(R.raw.biblian23)
                23 -> inputStream = resources.openRawResource(R.raw.biblian24)
                24 -> inputStream = resources.openRawResource(R.raw.biblian25)
                25 -> inputStream = resources.openRawResource(R.raw.biblian26)
                26 -> inputStream = resources.openRawResource(R.raw.biblian27)
            }
        } else {
            when (kniga) {
                0 -> inputStream = resources.openRawResource(R.raw.biblias1)
                1 -> inputStream = resources.openRawResource(R.raw.biblias2)
                2 -> inputStream = resources.openRawResource(R.raw.biblias3)
                3 -> inputStream = resources.openRawResource(R.raw.biblias4)
                4 -> inputStream = resources.openRawResource(R.raw.biblias5)
                5 -> inputStream = resources.openRawResource(R.raw.biblias6)
                6 -> inputStream = resources.openRawResource(R.raw.biblias7)
                7 -> inputStream = resources.openRawResource(R.raw.biblias8)
                8 -> inputStream = resources.openRawResource(R.raw.biblias9)
                9 -> inputStream = resources.openRawResource(R.raw.biblias10)
                10 -> inputStream = resources.openRawResource(R.raw.biblias11)
                11 -> inputStream = resources.openRawResource(R.raw.biblias12)
                12 -> inputStream = resources.openRawResource(R.raw.biblias13)
                13 -> inputStream = resources.openRawResource(R.raw.biblias14)
                14 -> inputStream = resources.openRawResource(R.raw.biblias15)
                15 -> inputStream = resources.openRawResource(R.raw.biblias16)
                16 -> inputStream = resources.openRawResource(R.raw.biblias17)
                17 -> inputStream = resources.openRawResource(R.raw.biblias18)
                18 -> inputStream = resources.openRawResource(R.raw.biblias19)
                19 -> inputStream = resources.openRawResource(R.raw.biblias20)
                20 -> inputStream = resources.openRawResource(R.raw.biblias21)
                21 -> inputStream = resources.openRawResource(R.raw.biblias22)
                22 -> inputStream = resources.openRawResource(R.raw.biblias23)
                23 -> inputStream = resources.openRawResource(R.raw.biblias24)
                24 -> inputStream = resources.openRawResource(R.raw.biblias25)
                25 -> inputStream = resources.openRawResource(R.raw.biblias26)
                26 -> inputStream = resources.openRawResource(R.raw.biblias27)
                27 -> inputStream = resources.openRawResource(R.raw.biblias28)
                28 -> inputStream = resources.openRawResource(R.raw.biblias29)
                29 -> inputStream = resources.openRawResource(R.raw.biblias30)
                30 -> inputStream = resources.openRawResource(R.raw.biblias31)
                31 -> inputStream = resources.openRawResource(R.raw.biblias32)
                32 -> inputStream = resources.openRawResource(R.raw.biblias33)
                33 -> inputStream = resources.openRawResource(R.raw.biblias34)
                34 -> inputStream = resources.openRawResource(R.raw.biblias35)
                35 -> inputStream = resources.openRawResource(R.raw.biblias36)
                36 -> inputStream = resources.openRawResource(R.raw.biblias37)
                37 -> inputStream = resources.openRawResource(R.raw.biblias38)
                38 -> inputStream = resources.openRawResource(R.raw.biblias39)
            }
        }
        return inputStream
    }

    fun saveVydelenieZakladkiNtanki(novyZapaviet: Boolean, kniga: Int, glava: Int, stix: Int) {
        val context = this as BaseActivity
        val k = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val prefEditors = k.edit()
        prefEditors.remove("bible_time_semuxa")
        prefEditors.putBoolean("bible_time_semuxa_zavet", novyZapaviet)
        prefEditors.putInt("bible_time_semuxa_kniga", kniga)
        prefEditors.putInt("bible_time_semuxa_glava", glava)
        prefEditors.putInt("bible_time_semuxa_stix", stix)
        prefEditors.apply()
        val gson = Gson()
        val type = TypeToken.getParameterized(java.util.ArrayList::class.java, TypeToken.getParameterized(java.util.ArrayList::class.java, Integer::class.java).type).type
        val listFiles = if (novyZapaviet) File("${context.filesDir}/BibliaSemuxaNovyZavet").listFiles()
        else File("$filesDir/BibliaSemuxaStaryZavet").listFiles()
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
        val file = if (novyZapaviet) File("${context.filesDir}/BibliaSemuxaNovyZavet/$kniga.json")
        else File("$filesDir/BibliaSemuxaStaryZavet/$kniga.json")
        if (BibleGlobalList.vydelenie.size == 0) {
            if (file.exists()) {
                file.delete()
            }
        } else {
            file.writer().use {
                it.write(gson.toJson(BibleGlobalList.vydelenie, type))
            }
        }
        val fileZakladki = File("${context.filesDir}/BibliaSemuxaZakladki.json")
        if (BibleGlobalList.zakladkiSemuxa.size == 0) {
            if (fileZakladki.exists()) {
                fileZakladki.delete()
            }
        } else {
            fileZakladki.writer().use {
                val type2 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleZakladkiData::class.java).type
                it.write(gson.toJson(BibleGlobalList.zakladkiSemuxa, type2))
            }
        }
        val fileNatatki = File("${context.filesDir}/BibliaSemuxaNatatki.json")
        if (BibleGlobalList.natatkiSemuxa.size == 0) {
            if (fileNatatki.exists()) {
                fileNatatki.delete()
            }
        } else {
            fileNatatki.writer().use {
                val type3 = TypeToken.getParameterized(java.util.ArrayList::class.java, BibleNatatkiData::class.java).type
                it.write(gson.toJson(BibleGlobalList.natatkiSemuxa, type3))
            }
        }
    }

    fun addZakladka(color: Int, knigaBible: String, bible: String) {
        if (color != -1) {
            val context = this as BaseActivity
            var maxIndex: Long = 0
            BibleGlobalList.zakladkiSemuxa.forEach {
                if (maxIndex < it.id) maxIndex = it.id
            }
            maxIndex++
            BibleGlobalList.zakladkiSemuxa.add(0, BibleZakladkiData(maxIndex, knigaBible + "/" + resources.getString(by.carkva_gazeta.malitounik.R.string.razdzel) + " " + (BibleGlobalList.mListGlava + 1) + getString(by.carkva_gazeta.malitounik.R.string.stix_by) + " " + (BibleGlobalList.bibleCopyList[0] + 1) + "\n\n" + MainActivity.fromHtml(bible).toString() + "<!--" + color))
            MainActivity.toastView(context, getString(by.carkva_gazeta.malitounik.R.string.add_to_zakladki))
        }
    }
}