package by.carkva_gazeta.resources

import androidx.fragment.app.Fragment

abstract class BackPressedFragment : Fragment() {
    abstract fun onBackPressedFragment()
    abstract fun addNatatka()
    abstract fun addZakladka(color: Int)
}