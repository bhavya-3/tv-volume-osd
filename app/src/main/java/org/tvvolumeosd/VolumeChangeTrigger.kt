package org.tvvolumeosd

/**
 * A trigger for volume changes. Can be either a broadcast-based trigger or a polling fallback.
 */
interface VolumeChangeTrigger {
    fun start()
    fun stop()
}