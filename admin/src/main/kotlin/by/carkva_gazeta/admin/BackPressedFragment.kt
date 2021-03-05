package by.carkva_gazeta.admin

import androidx.fragment.app.Fragment

abstract class BackPressedFragment : Fragment() {
    abstract fun onBackPressedFragment(): Boolean
}