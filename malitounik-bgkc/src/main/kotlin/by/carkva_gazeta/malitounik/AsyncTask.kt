package by.carkva_gazeta.malitounik

import kotlinx.coroutines.*

class AsyncTask {

    private var viewModelListener: ViewModelListener? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    interface ViewModelListener {
        fun doInBackground()
    }

    fun setViewModelListener(viewModelListener: ViewModelListener) {
        this.viewModelListener = viewModelListener
        execute()
    }

    private fun execute() = scope.launch {
        withContext(Dispatchers.IO) {
            return@withContext viewModelListener?.doInBackground()
        }
    }
}