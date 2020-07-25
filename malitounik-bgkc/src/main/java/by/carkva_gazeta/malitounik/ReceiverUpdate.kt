package by.carkva_gazeta.malitounik

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReceiverUpdate : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Thread(Runnable {
            val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
            val notify = chin.getInt("notification", 2)
            SettingsActivity.setNotifications(context, notify)
        }).start()
    }
}