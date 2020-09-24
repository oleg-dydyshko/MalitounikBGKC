package by.carkva_gazeta.malitounik

import androidx.fragment.app.Fragment

abstract class VybranoeFragment : Fragment() {
    abstract fun fileDelite(position: Int)
    abstract fun fileDeliteCancel()
    abstract fun deliteAllVybranoe()
}