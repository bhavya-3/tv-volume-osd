package org.tvvolumeosd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

/**
 * A volume change trigger that listens for VOLUME_CHANGED_ACTION broadcasts.
 * This is an optional optimization that provides faster detection than polling.
 * Uses string literals because these are not public SDK constants.
 */
class VolumeBroadcastTrigger(
    private val context: Context,
    private val onChangeHint: () -> Unit
) : VolumeChangeTrigger {
    private var receiver: BroadcastReceiver? = null

    override fun start() {
        val filter = IntentFilter(VOLUME_CHANGED_ACTION)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == VOLUME_CHANGED_ACTION) {
                    val streamType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1)
                    if (streamType == android.media.AudioManager.STREAM_MUSIC) {
                        Log.i(TAG, "volume_broadcast_received")
                        onChangeHint()
                    }
                }
            }
        }
        try {
            context.registerReceiver(receiver, filter)
            Log.i(TAG, "volume_broadcast_registered")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to register volume broadcast receiver", e)
            receiver = null
        }
    }

    override fun stop() {
        receiver?.let { 
            try {
                context.unregisterReceiver(it)
                Log.i(TAG, "volume_broadcast_unregistered")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to unregister volume broadcast receiver", e)
            }
            receiver = null
        }
    }

    private companion object {
        const val TAG = "TvVolumeOsd"
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
        private const val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"
    }
}