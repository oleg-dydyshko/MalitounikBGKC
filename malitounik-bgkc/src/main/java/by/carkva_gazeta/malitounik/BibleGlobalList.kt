package by.carkva_gazeta.malitounik

import kotlin.collections.ArrayList

class BibleGlobalList {
    companion object {
        var listPosition = 0
        var mListGlava = 0
        var mPedakVisable = false
        var bibleCopyList = ArrayList<Int>()
        var vydelenie = ArrayList<ArrayList<Int>>()
        var zakladkiSemuxa = ArrayList<String>()
        var zakladkiSinodal = ArrayList<String>()
        var natatkiSemuxa = ArrayList<ArrayList<String>>()
        var natatkiSinodal = ArrayList<ArrayList<String>>()

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