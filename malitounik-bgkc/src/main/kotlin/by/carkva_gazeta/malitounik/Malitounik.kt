package by.carkva_gazeta.malitounik

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class Malitounik : SplitCompatApplication() {
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        SplitCompat.install(this)
        FirebaseApp.initializeApp(this)
    }

    init {
        instance = this
    }

    companion object {
        private var instance: Malitounik? = null
        val storage: FirebaseStorage
            get() = Firebase.storage
        val referens: StorageReference
            get() = storage.reference

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}