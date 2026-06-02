package com.kelsos.mbrc.core.networking

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MessageSizeLimitsTest {
  private val min = 8L * 1024 * 1024
  private val max = 64L * 1024 * 1024

  @Test
  fun `clamps to the floor on small heaps`() {
    // 10% of 64 MB = 6.4 MB, below the 8 MB floor.
    assertThat(defaultMaxMessageBytes(maxHeapBytes = 64L * 1024 * 1024)).isEqualTo(min)
  }

  @Test
  fun `clamps to the ceiling on very large heaps`() {
    // 10% of 2 GB = ~205 MB, above the 64 MB ceiling.
    assertThat(defaultMaxMessageBytes(maxHeapBytes = 2L * 1024 * 1024 * 1024)).isEqualTo(max)
  }

  @Test
  fun `scales with heap between the bounds`() {
    val heap = 256L * 1024 * 1024
    val expected = (heap * 0.10).toLong()
    assertThat(defaultMaxMessageBytes(maxHeapBytes = heap)).isEqualTo(expected)
    assertThat(expected).isGreaterThan(min)
    assertThat(expected).isLessThan(max)
  }

  @Test
  fun `cap always leaves huge headroom over the measured worst-case page`() {
    // Measured worst case on a real library: ~244 KB per 800-item page.
    val measuredWorstCasePageBytes = 250_000L
    assertThat(defaultMaxMessageBytes(maxHeapBytes = 64L * 1024 * 1024))
      .isGreaterThan(measuredWorstCasePageBytes * 10)
  }
}
