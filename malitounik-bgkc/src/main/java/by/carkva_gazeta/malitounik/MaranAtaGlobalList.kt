package by.carkva_gazeta.malitounik

import java.util.*

class MaranAtaGlobalList {
    companion object {
        var listPosition = 0
        private var mListGlava = 0
        private var mPedakVisable = false
        var bible: ArrayList<String>? = null

        /*static ArrayList<ArrayList<String>> getNatatkiPsalterNadsana() {
        return natatkiPsalterNadsana;
    }

    static ArrayList<String> getZakladkiPsalterNadsana() {
        return zakladkiPsalterNadsana;
    }

    static void setZakladkiPsalterNadsana(ArrayList<String> zakladkiPsalterNadsana) {
        MaranAta_Global_List.zakladkiPsalterNadsana = zakladkiPsalterNadsana;
    }

    static void setNatatkiPsalterNadsana(ArrayList<ArrayList<String>> natatkiPsalterNadsana) {
        MaranAta_Global_List.natatkiPsalterNadsana = natatkiPsalterNadsana;
    }*/
        var vydelenie: ArrayList<ArrayList<Int>>? = null
        var zakladkiSemuxa: ArrayList<String>? = null
        var zakladkiSinodal: ArrayList<String>? = null
        var natatkiSemuxa: ArrayList<ArrayList<String>>? = null
        var natatkiSinodal: ArrayList<ArrayList<String>>? = null
        //private static ArrayList<String> zakladkiPsalterNadsana;
//private static ArrayList<ArrayList<String>> natatkiPsalterNadsana;

        fun getmListGlava(): Int {
            return mListGlava
        }

        fun setmListGlava(mListGlava: Int) {
            MaranAtaGlobalList.mListGlava = mListGlava
        }

        fun setmPedakVisable(redartor: Boolean) {
            mPedakVisable = redartor
        }

        fun getmPedakVisable(): Boolean {
            return mPedakVisable
        }

        fun checkPosition(glava: Int, position: Int): Int {
            vydelenie?.let {
                for (i in it.indices) {
                    if (it[i][0] == glava && it[i][1] == position) {
                        return i
                    }
                }
            }
            return -1
        }
    }
}