package org.tvvolumeosd

/** Snapshot of the public AudioManager music-stream state. */
data class VolumeState(
    val current: Int,
    val min: Int,
    val max: Int,
    val muted: Boolean,
    val outputs: List<String> = emptyList(),
) {
    val normalized: Float
        get() = VolumeNormalizer.normalize(current, min, max)
}

object VolumeNormalizer {
    fun normalize(current: Int, min: Int, max: Int): Float {
        if (max <= min) return 0f
        return ((current - min).toFloat() / (max - min).toFloat()).coerceIn(0f, 1f)
    }
}
