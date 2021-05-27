package by.carkva_gazeta.malitounik

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitcompat.SplitCompatApplication

@Suppress("unused")
class Malitounik : SplitCompatApplication() {
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.install(this)
    }

    init {
        instance = this
    }

    companion object {
        private var instance: Malitounik? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}