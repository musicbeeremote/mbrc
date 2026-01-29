package com.kelsos.mbrc.core.networking.protocol.actions

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.LfmRating
import com.kelsos.mbrc.core.common.state.TrackRating
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.base.ProtocolMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateRatingTest {

  private lateinit var stateHandler: PlayerStateHandler
  private lateinit var updateRating: UpdateRating
  private val trackRatingFlow = MutableStateFlow(TrackRating())

  @Before
  fun setUp() {
    stateHandler = mockk(relaxed = true) {
      every { playingTrackRating } returns trackRatingFlow
    }
    updateRating = UpdateRating(stateHandler)
  }

  private fun createMessage(data: Any): ProtocolMessage = mockk {
    every { type } returns Protocol.NowPlayingRating
    every { this@mockk.data } returns data
  }

  @Test
  fun `execute should set rating to null when data is empty string`() = runTest {
    // Given
    val message = createMessage("")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isNull()
  }

  @Test
  fun `execute should set rating to null when data is null string`() = runTest {
    // Given
    val message = createMessage("null")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isNull()
  }

  @Test
  fun `execute should set rating to 0 (bomb) when data is 0`() = runTest {
    // Given
    val message = createMessage("0")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(0f)
    assertThat(slot.captured.isBomb()).isTrue()
  }

  @Test
  fun `execute should set rating to 0 (bomb) when data is 0_0`() = runTest {
    // Given
    val message = createMessage("0.0")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(0f)
    assertThat(slot.captured.isBomb()).isTrue()
  }

  @Test
  fun `execute should set rating to half star when data is 0_5`() = runTest {
    // Given
    val message = createMessage("0.5")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(0.5f)
  }

  @Test
  fun `execute should set rating to integer stars`() = runTest {
    // Given
    val message = createMessage("3")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(3f)
  }

  @Test
  fun `execute should set rating to half star values`() = runTest {
    // Given
    val message = createMessage("3.5")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(3.5f)
  }

  @Test
  fun `execute should set rating to max (5 stars)`() = runTest {
    // Given
    val message = createMessage("5")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(5f)
  }

  @Test
  fun `execute should set rating to max with decimal (5_0)`() = runTest {
    // Given
    val message = createMessage("5.0")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(5f)
  }

  @Test
  fun `execute should preserve existing lfmRating when updating rating`() = runTest {
    // Given - previous state has Loved lfmRating
    trackRatingFlow.value = TrackRating(lfmRating = LfmRating.Loved, rating = 2f)
    val message = createMessage("4.5")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(4.5f)
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Loved)
  }

  @Test
  fun `execute should handle numeric data type`() = runTest {
    // Given - data is a Number instead of String
    val message = createMessage(3.5)

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isEqualTo(3.5f)
  }

  @Test
  fun `execute should set rating to null for invalid string`() = runTest {
    // Given
    val message = createMessage("invalid")

    // When
    updateRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.rating).isNull()
  }
}
