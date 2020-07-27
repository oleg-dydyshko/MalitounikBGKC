package by.carkva_gazeta.malitounik

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by oleg on 21.11.16.
 */
class ReceiverBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.QUICKBOOT_POWERON" || intent.action == "com.htc.intent.action.QUICKBOOT_POWERON") {
            Thread(Runnable {
                val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
                val notify = chin.getInt("notification", 2)
                SettingsActivity.setNotifications(context, notify)
            }).start()
        }
    }
}