package by.carkva_gazeta.resources

import android.content.Context
import by.carkva_gazeta.malitounik.BaseActivity
import by.carkva_gazeta.malitounik.BibleGlobalList
import by.carkva_gazeta.malitounik.BibleNatatkiData
import by.carkva_gazeta.malitounik.BibleZakladkiData
import by.carkva_gazeta.malitounik.MainActivity
import by.carkva_gazeta.malitounik.VybranoeBibleList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream

interface BibliaPerakvadSinaidal {

    fun isPsaltyrGreek() = true

    fun getNamePerevod() = VybranoeBibleList.PEREVODSINOIDAL

    fun getZakladki() = BibleGlobalList.zakladkiSinodal

    fun getTitlePerevod(): String {
        this as BaseActivity
        return resources.getString(by.carkva_gazeta.malitounik.R.string.bsinaidal)
    }

    fun getSubTitlePerevod(glava: Int): String {
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
        val fields = R.raw::class.java.fields
        val zavet = if (novyZapaviet) "n"
        else "s"
        for (element in fields) {
            val name = element.name
            if (name == "sinaidal$zavet${kniga + 1}") {
                return resources.openRawResource(element.getInt(name))
            }
        }
        return resources.openRawResource(by.carkva_gazeta.malitounik.R.raw.biblia_error)
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