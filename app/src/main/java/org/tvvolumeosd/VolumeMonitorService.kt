package org.tvvolumeosd

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Build
import android.provider.Settings
import android.util.Log

/** Explicitly enabled, foreground-only monitor; it never writes audio state. */
class VolumeMonitorService : Service() {
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private lateinit var provider: VolumeProvider
    private lateinit var overlay: OverlayController
    private var previous: VolumeState? = null
    private var trigger: VolumeChangeTrigger? = null

    private val poll = object : Runnable {
        override fun run() {
            val next = provider.getVolumeState()
            val old = previous
            if (old != null && changed(old, next)) {
                Log.i(TAG, "volume_change source=polling old=${old.current} new=${next.current} muted=${next.muted}")
                if (Settings.canDrawOverlays(this@VolumeMonitorService)) overlay.show(next)
            }
            previous = next
            handler.postDelayed(this, POLL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        provider = AudioManagerVolumeProvider(this)
        overlay = OverlayController(this)
        startForeground(NOTIFICATION_ID, notification())
        previous = provider.getVolumeState()
        
        // Try to use broadcast-based trigger first, fall back to polling
        val broadcastTrigger = VolumeBroadcastTrigger(this) {
            // On broadcast hint, immediately query AudioManager and update if needed
            val next = provider.getVolumeState()
            val old = previous
            if (old != null && changed(old, next)) {
                Log.i(TAG, "volume_change source=broadcast old=${old.current} new=${next.current} muted=${next.muted}")
                if (Settings.canDrawOverlays(this@VolumeMonitorService)) overlay.show(next)
            }
            previous = next
        }
        try {
            broadcastTrigger.start()
            trigger = broadcastTrigger
            Log.i(TAG, "volume_trigger_broadcast_used")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to start broadcast trigger, using polling fallback", e)
            handler.post(poll)
            trigger = null
        }
        
        Log.i(TAG, "monitor_started initial=${previous?.current}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { 
        trigger?.stop()
        handler.removeCallbacks(poll)
        overlay.remove()
        super.onDestroy() 
    }

    private fun changed(old: VolumeState, next: VolumeState) =
        old.current != next.current || old.min != next.min || old.max != next.max || old.muted != next.muted

    private fun notification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel(CHANNEL, "Volume monitoring", NotificationManager.IMPORTANCE_LOW))
        }
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, CHANNEL) else Notification.Builder(this)
        return builder
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentTitle("TV Volume OSD is monitoring volume")
            .setOngoing(true)
            .build()
    }

    private companion object { 
        const val TAG = "TvVolumeOsd"
        const val CHANNEL = "volume_monitor" 
        const val NOTIFICATION_ID = 7
        const val POLL_MS = 200L 
    }
}
