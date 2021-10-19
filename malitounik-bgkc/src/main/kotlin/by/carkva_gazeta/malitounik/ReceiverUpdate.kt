package by.carkva_gazeta.malitounik

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class ReceiverUpdate : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        getVersionCode(context)
    }

    private fun getVersionCode(context: Context) {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                var onlineVersion = withContext(Dispatchers.IO) {
                    var newVersion: String? = null
                    try {
                        val document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.packageName + "&hl=en").timeout(30000).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get()
                        if (document != null) {
                            val element = document.getElementsContainingOwnText("Current Version")
                            for (ele in element) {
                                if (ele.siblingElements() != null) {
                                    val sibElemets = ele.siblingElements()
                                    for (sibElemet in sibElemets) {
                                        newVersion = sibElemet.text()
                                    }
                                }
                            }
                        }
                    } catch (e: Throwable) {
                    }
                    return@withContext newVersion
                }
                var currentVersion = BuildConfig.VERSION_NAME
                if (onlineVersion != null && onlineVersion.isNotEmpty()) {
                    val versionSize = currentVersion.split(".")
                    if (versionSize.size == 4) {
                        val t1 = currentVersion.lastIndexOf(".")
                        currentVersion = currentVersion.substring(0, t1)
                    }
                    currentVersion = currentVersion.replace(".", "")
                    onlineVersion = onlineVersion.replace(".", "")
                    if (currentVersion.toInt() < onlineVersion.toInt()) {
                        sendNotif(context)
                    }
                }
            }
        }
    }

    private fun sendNotif(context: Context) {
        val packageName = context.packageName
        val notificationIntent = try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        } catch (e: ActivityNotFoundException) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        }
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val id = 45000
        val title = context.getString(R.string.update_title)
        val text = context.getString(R.string.update_text)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val contentIntent = PendingIntent.getActivity(context, id, notificationIntent, flags)
        val chin = context.getSharedPreferences("biblia", Context.MODE_PRIVATE)
        val uri: Uri
        val bigIcon = R.drawable.krest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SettingsActivity.notificationChannel()
            val builder = Notification.Builder(context, SettingsActivity.NOTIFICATION_CHANNEL_ID_SVIATY)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            builder.setContentIntent(contentIntent).setWhen(System.currentTimeMillis()).setShowWhen(true).setSmallIcon(R.drawable.krest).setLargeIcon(BitmapFactory.decodeResource(context.resources, bigIcon)).setAutoCancel(true).setContentTitle(title).setContentText(text)
            val notification = builder.build()
            notificationManager?.notify(id, notification)
        } else {
            val vibrate = longArrayOf(0, 1000, 700, 1000, 700, 1000)
            val sound = chin.getInt("soundnotification", 0)
            uri = when (sound) {
                1 -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                2 -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                3 -> Uri.parse(chin.getString("soundURI", ""))
                else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            val builder = NotificationCompat.Builder(context, "Сьвяты і Падзеі")
            builder.setContentIntent(contentIntent).setWhen(System.currentTimeMillis()).setShowWhen(true).setSmallIcon(R.drawable.krest).setLargeIcon(BitmapFactory.decodeResource(context.resources, bigIcon)).setAutoCancel(true).setPriority(NotificationManagerCompat.IMPORTANCE_HIGH).setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000).setContentTitle(title).setContentText(text)
            if (chin.getInt("guk", 1) == 1) builder.setSound(uri)
            if (chin.getInt("vibra", 1) == 1) builder.setVibrate(vibrate)
            val notification = builder.build()
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(id, notification)
        }
    }
}