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

class UpdateLfmRatingTest {

  private lateinit var stateHandler: PlayerStateHandler
  private lateinit var updateLfmRating: UpdateLfmRating
  private val trackRatingFlow = MutableStateFlow(TrackRating())

  @Before
  fun setUp() {
    stateHandler = mockk(relaxed = true) {
      every { playingTrackRating } returns trackRatingFlow
    }
    updateLfmRating = UpdateLfmRating(stateHandler)
  }

  private fun createMessage(data: Any): ProtocolMessage = mockk {
    every { type } returns Protocol.NowPlayingLfmRating
    every { this@mockk.data } returns data
  }

  @Test
  fun `execute should set lfmRating to Loved when data is Love`() = runTest {
    // Given
    val message = createMessage("Love")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Loved)
    assertThat(slot.captured.isFavorite()).isTrue()
  }

  @Test
  fun `execute should set lfmRating to Banned when data is Ban`() = runTest {
    // Given
    val message = createMessage("Ban")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Banned)
  }

  @Test
  fun `execute should set lfmRating to Normal when data is Normal`() = runTest {
    // Given
    val message = createMessage("Normal")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Normal)
  }

  @Test
  fun `execute should set lfmRating to Normal for unknown values`() = runTest {
    // Given
    val message = createMessage("Unknown")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Normal)
  }

  @Test
  fun `execute should set lfmRating to Normal for empty string`() = runTest {
    // Given
    val message = createMessage("")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Normal)
  }

  @Test
  fun `execute should set lfmRating to Normal for null data`() = runTest {
    // Given - data is not a String
    val message: ProtocolMessage = mockk {
      every { type } returns Protocol.NowPlayingLfmRating
      every { data } returns 123 // Not a String
    }

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Normal)
  }

  @Test
  fun `execute should preserve existing star rating when updating lfmRating`() = runTest {
    // Given - previous state has a star rating
    trackRatingFlow.value = TrackRating(lfmRating = LfmRating.Normal, rating = 4.5f)
    val message = createMessage("Love")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Loved)
    assertThat(slot.captured.rating).isEqualTo(4.5f)
  }

  @Test
  fun `execute should preserve bomb rating when updating lfmRating`() = runTest {
    // Given - previous state has a bomb rating
    trackRatingFlow.value = TrackRating(lfmRating = LfmRating.Normal, rating = 0f)
    val message = createMessage("Ban")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Banned)
    assertThat(slot.captured.rating).isEqualTo(0f)
    assertThat(slot.captured.isBomb()).isTrue()
  }

  @Test
  fun `execute should change from Loved to Normal`() = runTest {
    // Given - previous state has Loved
    trackRatingFlow.value = TrackRating(lfmRating = LfmRating.Loved, rating = 5f)
    val message = createMessage("Normal")

    // When
    updateLfmRating.execute(message)

    // Then
    val slot = slot<TrackRating>()
    verify { stateHandler.updateTrackRating(capture(slot)) }
    assertThat(slot.captured.lfmRating).isEqualTo(LfmRating.Normal)
    assertThat(slot.captured.isFavorite()).isFalse()
  }
}
