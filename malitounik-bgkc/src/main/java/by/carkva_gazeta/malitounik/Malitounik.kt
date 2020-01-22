package by.carkva_gazeta.malitounik

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitcompat.SplitCompatApplication

class Malitounik : SplitCompatApplication() {
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.install(this)
    }
}