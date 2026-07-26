package org.tvvolumeosd

interface VolumeProvider {
    fun getVolumeState(): VolumeState
}
