package by.carkva_gazeta.malitounik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsyncTask : ViewModel() {

    private var viewModelListener: ViewModelListener? = null

    interface ViewModelListener {
        fun doInBackground()
    }

    fun setViewModelListener(viewModelListener: ViewModelListener) {
        this.viewModelListener = viewModelListener
        execute()
    }

    private fun execute() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            return@withContext viewModelListener?.doInBackground()
        }
    }
}