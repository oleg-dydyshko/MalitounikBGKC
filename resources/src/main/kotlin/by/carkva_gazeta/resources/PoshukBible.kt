package by.carkva_gazeta.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

internal class PoshukBible : ViewModel() {

    private var execute: Execute? = null

    internal interface Execute {
        fun onPreExecute()
        fun doInBackground(searche: String): ArrayList<String>
        fun onPostExecute(result: ArrayList<String>)
    }

    internal fun setExecute(execute: Execute) {
        this.execute = execute
    }

    internal fun execute(searche: String) = viewModelScope.launch {
        onPreExecute()
        val result = doInBackground(searche)
        onPostExecute(result)
    }

    private fun onPreExecute() {
        execute?.onPreExecute()
    }

    private suspend fun doInBackground(searche: String): ArrayList<String> =
        withContext(Dispatchers.IO) {
            return@withContext execute?.doInBackground(searche)?: ArrayList()
        }

    private fun onPostExecute(result: ArrayList<String>) {
        execute?.onPostExecute(result)
    }
}