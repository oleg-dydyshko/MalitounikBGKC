package by.carkva_gazeta.resources

import kotlinx.coroutines.*

internal class PoshukBible {

    private var execute: Execute? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    internal interface Execute {
        fun onPreExecute()
        fun doInBackground(searche: String): ArrayList<String>
        fun onPostExecute(result: ArrayList<String>)
    }

    internal fun setExecute(execute: Execute) {
        this.execute = execute
    }

    internal fun execute(searche: String) = scope.launch {
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