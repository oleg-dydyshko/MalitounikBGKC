package by.carkva_gazeta.malitounik

import androidx.fragment.app.ListFragment

abstract class NatatkiListFragment : ListFragment() {
    abstract fun fileDelite(position: Int)
    abstract fun onDialogEditClick(position: Int)
    abstract fun onDialogDeliteClick(position: Int, name: String)
    abstract fun sortAlfavit()
}