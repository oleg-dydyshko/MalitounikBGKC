package by.carkva_gazeta.malitounik

class BibleGlobalList {
    companion object {
        var listPosition = 0
        var mListGlava = 0
        var mPedakVisable = false
        var bibleCopyList = ArrayList<Int>()
        var vydelenie = ArrayList<ArrayList<Int>>()
        var zakladkiSemuxa = ArrayList<BibleZakladkiData>()
        var zakladkiSinodal = ArrayList<BibleZakladkiData>()
        var natatkiSemuxa = ArrayList<BibleNatatkiData>()
        var natatkiSinodal = ArrayList<BibleNatatkiData>()

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