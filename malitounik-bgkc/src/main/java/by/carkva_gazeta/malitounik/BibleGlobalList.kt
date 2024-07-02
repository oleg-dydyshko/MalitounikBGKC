package by.carkva_gazeta.malitounik

 abstract class BibleGlobalList {
    companion object {
        var mListGlava = 0
        var mPedakVisable = false
        val bibleCopyList = ArrayList<Int>()
        val vydelenie = ArrayList<ArrayList<Int>>()
        val zakladkiSemuxa = ArrayList<BibleZakladkiData>()
        val zakladkiSinodal = ArrayList<BibleZakladkiData>()
        val zakladkiBokuna = ArrayList<BibleZakladkiData>()
        val zakladkiCarniauski = ArrayList<BibleZakladkiData>()
        val natatkiSemuxa = ArrayList<BibleNatatkiData>()
        val natatkiSinodal = ArrayList<BibleNatatkiData>()
        val natatkiBokuna = ArrayList<BibleNatatkiData>()
        val natatkiCarniauski = ArrayList<BibleNatatkiData>()

        fun checkPosition(glava: Int = mListGlava, position: Int = bibleCopyList[0]): Int {
            for (i in vydelenie.indices) {
                if (vydelenie[i][0] == glava && vydelenie[i][1] == position) {
                    return i
                }
            }
            return -1
        }
    }
}