package org.tvvolumeosd

import org.junit.Assert.assertEquals
import org.junit.Test

class VolumeNormalizerTest {
    @Test fun `normalizes 0 through 100`() = assertEquals(0.44f, VolumeNormalizer.normalize(44, 0, 100), 0.0001f)
    @Test fun `normalizes arbitrary 0 through 50`() = assertEquals(0.5f, VolumeNormalizer.normalize(25, 0, 50), 0.0001f)
    @Test fun `normalizes nonzero minimum`() = assertEquals(0.5f, VolumeNormalizer.normalize(35, 10, 60), 0.0001f)
    @Test fun `clamps unexpected readings`() = assertEquals(1f, VolumeNormalizer.normalize(80, 10, 60), 0f)
    @Test fun `invalid range is safe`() = assertEquals(0f, VolumeNormalizer.normalize(10, 10, 10), 0f)
}
