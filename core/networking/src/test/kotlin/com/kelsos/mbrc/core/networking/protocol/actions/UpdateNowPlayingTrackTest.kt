package com.kelsos.mbrc.core.networking.protocol.actions

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.base.ProtocolMessage
import com.squareup.moshi.Moshi
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateNowPlayingTrackTest {

  private lateinit var stateHandler: PlayerStateHandler
  private lateinit var notifier: TrackChangeNotifier
  private lateinit var updateNowPlayingTrack: UpdateNowPlayingTrack
  private val playingTrackFlow = MutableStateFlow<TrackInfo>(BasicTrackInfo())
  private val moshi = Moshi.Builder().build()

  @Before
  fun setUp() {
    stateHandler = mockk(relaxed = true) {
      every { playingTrack } returns playingTrackFlow
    }
    notifier = mockk {
      every { notifyTrackChanged(any()) } just Runs
      coEvery { persistTrackInfo(any()) } just Runs
      coEvery { requestTrackDetails() } just Runs
    }
    updateNowPlayingTrack = UpdateNowPlayingTrack(stateHandler, notifier, moshi)
  }

  private fun createMessage(track: Map<String, String>): ProtocolMessage = mockk {
    every { type } returns Protocol.NowPlayingTrack
    every { data } returns track
  }

  @Test
  fun `execute should update track info from message`() = runTest {
    // Given
    val message = createMessage(
      mapOf(
        "artist" to "Test Artist",
        "title" to "Test Title",
        "album" to "Test Album",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then
    val slot = slot<BasicTrackInfo>()
    coVerify { stateHandler.updatePlayingTrack(capture(slot)) }
    assertThat(slot.captured.artist).isEqualTo("Test Artist")
    assertThat(slot.captured.title).isEqualTo("Test Title")
    assertThat(slot.captured.album).isEqualTo("Test Album")
    assertThat(slot.captured.year).isEqualTo("2024")
    assertThat(slot.captured.path).isEqualTo("/path/to/track.mp3")
  }

  @Test
  fun `execute should preserve coverUrl when album stays the same`() = runTest {
    // Given - previous track with same album and a cover
    playingTrackFlow.value = BasicTrackInfo(
      artist = "Previous Artist",
      title = "Previous Title",
      album = "Same Album",
      coverUrl = "http://example.com/cover.jpg"
    )

    val message = createMessage(
      mapOf(
        "artist" to "New Artist",
        "title" to "New Title",
        "album" to "Same Album",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then - coverUrl should be preserved
    val slot = slot<BasicTrackInfo>()
    coVerify { stateHandler.updatePlayingTrack(capture(slot)) }
    assertThat(slot.captured.album).isEqualTo("Same Album")
    assertThat(slot.captured.coverUrl).isEqualTo("http://example.com/cover.jpg")
  }

  @Test
  fun `execute should reset coverUrl when album changes`() = runTest {
    // Given - previous track with different album and a cover (#139)
    playingTrackFlow.value = BasicTrackInfo(
      artist = "Previous Artist",
      title = "Previous Title",
      album = "Old Album",
      coverUrl = "http://example.com/old-cover.jpg"
    )

    val message = createMessage(
      mapOf(
        "artist" to "New Artist",
        "title" to "New Title",
        "album" to "New Album",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then - coverUrl should be reset to empty
    val slot = slot<BasicTrackInfo>()
    coVerify { stateHandler.updatePlayingTrack(capture(slot)) }
    assertThat(slot.captured.album).isEqualTo("New Album")
    assertThat(slot.captured.coverUrl).isEmpty()
  }

  @Test
  fun `execute should reset coverUrl when changing from album to no album`() = runTest {
    // Given - previous track had an album with cover
    playingTrackFlow.value = BasicTrackInfo(
      artist = "Previous Artist",
      title = "Previous Title",
      album = "Some Album",
      coverUrl = "http://example.com/cover.jpg"
    )

    val message = createMessage(
      mapOf(
        "artist" to "New Artist",
        "title" to "New Title",
        "album" to "",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then - coverUrl should be reset
    val slot = slot<BasicTrackInfo>()
    coVerify { stateHandler.updatePlayingTrack(capture(slot)) }
    assertThat(slot.captured.album).isEmpty()
    assertThat(slot.captured.coverUrl).isEmpty()
  }

  @Test
  fun `execute should handle first track with no previous state`() = runTest {
    // Given - no previous track (empty BasicTrackInfo)
    playingTrackFlow.value = BasicTrackInfo()

    val message = createMessage(
      mapOf(
        "artist" to "First Artist",
        "title" to "First Title",
        "album" to "First Album",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then
    val slot = slot<BasicTrackInfo>()
    coVerify { stateHandler.updatePlayingTrack(capture(slot)) }
    assertThat(slot.captured.artist).isEqualTo("First Artist")
    assertThat(slot.captured.coverUrl).isEmpty()
  }

  @Test
  fun `execute should notify track changed`() = runTest {
    // Given
    val message = createMessage(
      mapOf(
        "artist" to "Artist",
        "title" to "Title",
        "album" to "Album",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then
    coVerify { notifier.notifyTrackChanged(any()) }
    coVerify { notifier.persistTrackInfo(any()) }
    coVerify { notifier.requestTrackDetails() }
  }

  @Test
  fun `execute should preserve duration when album stays the same`() = runTest {
    // Given - previous track with duration
    playingTrackFlow.value = BasicTrackInfo(
      album = "Same Album",
      duration = 180000L
    )

    val message = createMessage(
      mapOf(
        "artist" to "Artist",
        "title" to "Title",
        "album" to "Same Album",
        "year" to "2024",
        "path" to "/path/to/track.mp3"
      )
    )

    // When
    updateNowPlayingTrack.execute(message)

    // Then - duration should be preserved
    val slot = slot<BasicTrackInfo>()
    coVerify { stateHandler.updatePlayingTrack(capture(slot)) }
    assertThat(slot.captured.duration).isEqualTo(180000L)
  }
}
