package by.carkva_gazeta.malitounik

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


class ServiceRadioMaryia : Service() {

    companion object {
        const val PLAY_PAUSE = 1
        const val STOP = 2
    }

    private var player: ExoPlayer? = null
    private val isPlaying get() = player?.isPlaying ?: false

    private fun initRadioMaria() {
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse("https://server.radiorm.by:8443/live")))
            prepare()
        }
    }

    override fun onCreate() {
        super.onCreate()
        MainActivity.isServiceRadioMaryiaRun = true
        initRadioMaria()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.extras?.getInt("action") ?: PLAY_PAUSE
        if (action == PLAY_PAUSE) {
            if (isPlaying) {
                player?.pause()
            } else {
                player?.play()
            }
        } else {
            stopPlay()
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopPlay() {
        player?.stop()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(100)
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.isServiceRadioMaryiaRun = false
        stopPlay()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}