package by.carkva_gazeta.resources

interface BibleListiner {
    fun setOnClic(cytanneParalelnye: String, cytanneSours: String)
    fun getListPosition(position: Int)
    fun isPanelVisible(widthPanel: Int = 0)
    fun isListEndPosition(page: Int)
    fun getmActionDown(mActionDown: Boolean)
}