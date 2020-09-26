package by.carkva_gazeta.malitounik

import androidx.fragment.app.Fragment

abstract class NatatkiFragment : Fragment() {
    abstract fun fileDelite(position: Int)
    abstract fun fileDeliteCancel()
    abstract fun onDialogEditClick(position: Int)
    abstract fun onDialogDeliteClick(position: Int, name: String)
}