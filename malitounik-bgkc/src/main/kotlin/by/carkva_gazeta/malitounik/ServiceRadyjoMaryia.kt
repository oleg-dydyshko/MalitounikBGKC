package by.carkva_gazeta.malitounik

import android.Manifest
import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class ServiceRadyjoMaryia : Service() {

    inner class ServiceRadyjoMaryiaBinder : Binder() {
        fun getService() = this@ServiceRadyjoMaryia
    }

    companion object {
        const val PLAY_PAUSE = 1
        const val STOP = 2
        var isServiceRadioMaryiaRun = false
    }

    private var player: ExoPlayer? = null
    private val isPlaying get() = player?.isPlaying ?: false
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var radyjoMaryiaTitle = ""
    private var listener: ServiceRadyjoMaryiaListener? = null

    interface ServiceRadyjoMaryiaListener {
        fun setTitleRadioMaryia(title: String)
        fun unBinding()
    }

    fun setServiceRadyjoMaryiaListener(serviceRadyjoMaryiaListener: ServiceRadyjoMaryiaListener) {
        listener = serviceRadyjoMaryiaListener
    }

    private fun initRadioMaria() {
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse("https://server.radiorm.by:8443/live")))
            prepare()
        }
    }

    override fun onCreate() {
        super.onCreate()
        startTimer()
        isServiceRadioMaryiaRun = true
        initRadioMaria()
    }

    fun stopServiceRadioMaria() {
        stopPlay()
        stopSelf()
    }

    fun playOrPause() {
        if (isPlaying) {
            player?.pause()
        } else {
            player?.play()
        }
    }

    fun getTitleProgramRadioMaria() = radyjoMaryiaTitle

    private fun stopPlay() {
        player?.stop()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(100)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        isServiceRadioMaryiaRun = false
        stopPlay()
    }

    private fun startTimer() {
        stopTimer()
        timerTask = object : TimerTask() {
            override fun run() {
                sendTitlePadioMaryia()
            }
        }
        timer = Timer()
        timer.schedule(timerTask, 0, 20000)
    }

    private fun stopTimer() {
        timer.cancel()
        timerTask = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.extras?.getInt("action") ?: PLAY_PAUSE
        if (action == PLAY_PAUSE) {
            playOrPause()
        } else {
            stopServiceRadioMaria()
            listener?.unBinding()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendTitlePadioMaryia() {
        if (MainActivity.isNetworkAvailable()) {
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    withContext(Dispatchers.IO) {
                        try {
                            val mURL = URL("https://radiomaria.by/player/hintbackend.php")
                            with(mURL.openConnection() as HttpURLConnection) {
                                val sb = StringBuilder()
                                BufferedReader(InputStreamReader(inputStream)).use {
                                    var inputLine = it.readLine()
                                    while (inputLine != null) {
                                        sb.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    var text = MainActivity.fromHtml(sb.toString()).toString().trim()
                                    val t1 = text.indexOf(":", ignoreCase = true)
                                    if (t1 != -1) {
                                        text = text.substring(t1 + 1)
                                    }
                                    val t2 = text.indexOf(">", ignoreCase = true)
                                    if (t2 != -1) {
                                        text = text.substring(t2 + 1)
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        if (radyjoMaryiaTitle != text.trim()) {
                                            radyjoMaryiaTitle = text.trim()
                                            setRadioNotification()
                                        }
                                        listener?.setTitleRadioMaryia(radyjoMaryiaTitle)
                                    }
                                }
                            }
                        } catch (_: Throwable) {
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setRadioNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val mediaSession = MediaSessionCompat(this, "Radyjo Maryia session")
            val name = getString(R.string.padie_maryia_s)
            mediaSession.setMetadata(MediaMetadataCompat.Builder().putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(resources, R.drawable.maria)).putString(MediaMetadataCompat.METADATA_KEY_TITLE, name).putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, radyjoMaryiaTitle).build())
            mediaSession.isActive = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(SettingsActivity.NOTIFICATION_CHANNEL_ID_RADIO_MARYIA, name, NotificationManager.IMPORTANCE_LOW)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                channel.description = name
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
            val notifi = NotificationCompat.Builder(this, SettingsActivity.NOTIFICATION_CHANNEL_ID_RADIO_MARYIA)
            notifi.setShowWhen(false)
            notifi.setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1))
            notifi.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.maria))
            notifi.setSmallIcon(R.drawable.krest)
            notifi.setContentTitle(getString(R.string.padie_maryia_s))
            notifi.setContentText(radyjoMaryiaTitle)
            notifi.setOngoing(true)
            notifi.addAction(R.drawable.play3, "play", retreivePlaybackAction(PLAY_PAUSE))
            notifi.addAction(R.drawable.stop3, "stop", retreivePlaybackAction(STOP))
            val notification = notifi.build()
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(100, notification)
        }
    }

    private fun retreivePlaybackAction(which: Int): PendingIntent? {
        val action = Intent()
        val pendingIntent: PendingIntent
        val serviceName = ComponentName(this, ServiceRadyjoMaryia::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        when (which) {
            PLAY_PAUSE -> {
                action.putExtra("action", PLAY_PAUSE)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, PLAY_PAUSE, action, flags)
                return pendingIntent
            }
            STOP -> {
                action.putExtra("action", STOP)
                action.component = serviceName
                pendingIntent = PendingIntent.getService(this, STOP, action, flags)
                return pendingIntent
            }
        }
        return null
    }

    override fun onBind(intent: Intent) = ServiceRadyjoMaryiaBinder()
}