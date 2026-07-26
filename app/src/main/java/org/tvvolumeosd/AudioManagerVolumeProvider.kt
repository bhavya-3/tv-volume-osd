package org.tvvolumeosd

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

/** Uses only documented public Android audio APIs. */
class AudioManagerVolumeProvider(context: Context) : VolumeProvider {
    private val audioManager = context.getSystemService(AudioManager::class.java)

    override fun getVolumeState(): VolumeState {
        val stream = AudioManager.STREAM_MUSIC
        return VolumeState(
            current = audioManager.getStreamVolume(stream),
            // getStreamMinVolume is public from API 28. Earlier public stream ranges
            // use zero as their documented effective minimum.
            min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                audioManager.getStreamMinVolume(stream)
            } else {
                0
            },
            max = audioManager.getStreamMaxVolume(stream),
            muted = audioManager.isStreamMute(stream),
            outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                .map(::outputLabel)
                .sorted(),
        )
    }

    private fun outputLabel(device: AudioDeviceInfo): String = when (device.type) {
        AudioDeviceInfo.TYPE_HDMI -> "HDMI"
        AudioDeviceInfo.TYPE_HDMI_ARC -> "HDMI ARC"
        AudioDeviceInfo.TYPE_HDMI_EARC -> "HDMI eARC"
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "TV speaker"
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth"
        else -> "Audio device type ${device.type}"
    }
}
