package by.carkva_gazeta.malitounik

import androidx.fragment.app.ListFragment

abstract class CviatyListFragment : ListFragment() {
    abstract fun setCviatyYear(year: Int)
    abstract fun getCviatyYear(): Int
}