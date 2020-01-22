package by.carkva_gazeta.biblijateka

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

internal class BibliotekaEpub(dirPatch: String) {
    private var navigation: String? = null
    private var rootDir = "/"
    private val navig: ArrayList<ArrayList<String>>
    private val patch: String
    private var contentOpf = "content.opf"
    private fun getFullPatch(dirPatch: String): String {
        val file = File("$dirPatch/META-INF/container.xml")
        val inputStream = FileReader(file)
        val reader = BufferedReader(inputStream)
        val line = reader.readText()
        inputStream.close()
        val t1 = line.indexOf("full-path=\"")
        val t2 = line.indexOf("\"", t1 + 11)
        contentOpf = line.substring(t1 + 11, t2)
        val t3 = contentOpf.lastIndexOf("/")
        if (t3 != -1) {
            rootDir = "/" + contentOpf.substring(0, t3 + 1)
            contentOpf = contentOpf.substring(t3 + 1)
        }
        return dirPatch + rootDir
    }

    private val bookNavigation: Unit
        get() {
            val file = File(patch + "toc.ncx")
            val inputStream = FileReader(file)
            val reader = BufferedReader(inputStream)
            navigation = reader.readText()
            reader.close()
        }

    val bookTitle: String
        get() {
            val t1 = navigation?.indexOf("<docTitle>") ?: 0
            val t2 = navigation?.indexOf("<text>", t1) ?: 0
            val t3 = navigation?.indexOf("</text>", t2) ?: 0
            return navigation?.substring(t2 + 6, t3) ?: ""
        }

    val content: ArrayList<ArrayList<String>>
        get() {
            if (navig.size == 0) {
                val rew = navigation?.split("<navPoint") ?: ArrayList()
                for (i in 1 until rew.size) {
                    val temp = ArrayList<String>()
                    val t1 = rew[i].indexOf("<navLabel>")
                    val t2 = rew[i].indexOf("<text>", t1)
                    val t3 = rew[i].indexOf("</text>", t2)
                    val t4 = rew[i].indexOf("<content src=\"", t3)
                    val t5 = rew[i].indexOf("\"", t4 + 14)
                    val t6 = rew[i].indexOf("playOrder=\"")
                    val t7 = rew[i].indexOf("\"", t6 + 11)
                    temp.add(rew[i].substring(t2 + 6, t3))
                    temp.add(rootDir + rew[i].substring(t4 + 14, t5))
                    temp.add(rew[i].substring(t6 + 11, t7))
                    navig.add(temp)
                }
            }
            return navig
        }

    val contentList: ArrayList<String>
        get() {
            val arrayList = ArrayList<String>()
            for (i in navig.indices) {
                arrayList.add(navig[i][1] + "<str>" + navig[i][0])
            }
            return arrayList
        }

    fun setPage(page: String): Int {
        var count = 1
        for (i in navig.indices) {
            if (navig[i][1].contains(page)) {
                count = navig[i][2].toInt()
                break
            }
        }
        return count - 1
    }

    fun getPageName(page: Int): String {
        return navig[page][1]
    }

    val titleImage: String
        get() {
            var file = File(patch + contentOpf)
            var inputStream = FileReader(file)
            var reader = BufferedReader(inputStream)
            var builder = reader.readText()
            inputStream.close()
            var spineSrc = builder
            val t1 = spineSrc.indexOf("id=\"cover\"")
            val t2 = spineSrc.indexOf("href=\"", t1 + 10)
            val t3 = spineSrc.indexOf("\"", t2 + 6)
            file = File(patch + spineSrc.substring(t2 + 6, t3))
            inputStream = FileReader(file)
            reader = BufferedReader(inputStream)
            builder = reader.readText()
            inputStream.close()
            spineSrc = builder
            val t4 = spineSrc.indexOf("<img")
            val t5 = spineSrc.indexOf("src=\"", t4 + 4)
            val t6 = spineSrc.indexOf("\"", t5 + 5)
            var res = spineSrc.substring(t5 + 5, t6)
            res = File(res).canonicalPath
            val t7 = res.indexOf("/")
            res = res.substring(t7 + 1)
            return patch + res
        }

    init {
        patch = getFullPatch(dirPatch)
        bookNavigation
        navig = ArrayList()
    }
}