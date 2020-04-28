package by.carkva_gazeta.malitounik

import androidx.fragment.app.ListFragment

abstract class VybranoeListFragment : ListFragment() {
    abstract fun fileDelite(position: Int)
    abstract fun deliteAllVybranoe()
}